package md;

/**
 * Created by Siddiqui on 9/12/2017.
 */

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("Duplicates")
public class FacilityLogin_backup_06032021 extends HttpServlet {
    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[]{'T', '#', '3', 'B', '3', '$', 'T',
                    '$', '3', 'C', 'r', '3', 't', 'K', '3', 'Q'};
    private PreparedStatement pStmt = null;
    private String Query = "";

    public static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }

    public static boolean checkSession(final PrintWriter out, final HttpServletRequest request, ServletContext context, HttpServletResponse response) throws IOException {
        return request.getSession(false) != null;
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void HandleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Connection conn = null;
        String Action;
        PrintWriter out = new PrintWriter(response.getOutputStream());
        ServletContext context;
        context = getServletContext();
        String UserId;
        String UserType;
        String FontColor;
        int FacilityIndex;
        String DatabaseName;
        String DirectoryName;

        try {
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            response.setHeader("Expires", "0"); // Proxies.

            Action = request.getParameter("Action");
            UtilityHelper helper = new UtilityHelper();

            if (request.getParameter("Action") != null) {
                //out.println("Action is not null");
                conn = Services.GetConnection(context, 1);
                if (conn == null) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                    return;
                }

                if (Action.compareTo("Login") == 0) {
                    SignIn(request, out, conn, context, helper);
                } else if (Action.compareTo("Logout") == 0) {
                    Logout(request, out, conn, context, helper);
                } else if (Action.compareTo("LoginAdmin") == 0) {
                    this.LoginAdmin(request, response, out, conn, context, helper);
                } else {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Error", "You are not logged in! Please log-in.");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                }
            } else {
                //out.println("Action is null");
                HttpSession session = request.getSession(false);
                if (session != null) {
                    UserId = session.getAttribute("UserId").toString();
                    UserType = session.getAttribute("UserType").toString();
                    FontColor = session.getAttribute("FontColor").toString();
                    DatabaseName = session.getAttribute("DatabaseName").toString();
                    DirectoryName = session.getAttribute("DirectoryName").toString();
                    FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());

                    SupportiveMethods suppMethods = new SupportiveMethods();
                    StringBuffer LeftSideBarMenu;
                    StringBuffer Header;
                    StringBuffer Footer;

                    conn = Services.GetConnection(context, 1);
                    if (conn == null) {
                        Parsehtm Parser = new Parsehtm(request);
                        Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                        Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                        return;
                    }

                    Header = suppMethods.Header(request, out, conn, context, UserId, "", FacilityIndex);
                    LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, context, UserId, "", FacilityIndex);
                    Footer = suppMethods.Footer(request, out, conn, context, UserId, "", FacilityIndex);

                    String[] UserDetails = helper.loginUserDetails(request, conn, UserId, context);

                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Header", String.valueOf(Header));
                    Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                    Parser.SetField("Footer", String.valueOf(Footer));
                    Parser.SetField("UserName", UserDetails[0]);
                    Parser.SetField("FontColor", FontColor);
                    Parser.SetField("UserType", UserType);
                    Parser.SetField("FacilityIndex", String.valueOf(FacilityIndex));
                    Parser.SetField("ClientId", UserDetails[3]);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + UserDetails[5]);
                } else {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Error", "You are not logged in. Please log-in!");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                }
            }
        } catch (Exception e) {
            Services.DumException("Login", "HandleRequest", request, e, getServletContext());
            try {
                HttpSession session = request.getSession(false);
                session.removeAttribute("UserId");
                session.removeAttribute("FacilityIndex");
                session.removeAttribute("UserType");
                session.removeAttribute("DatabaseName");
                session.removeAttribute("DirectoryName");
                session.removeAttribute("FontColor");
                //Deleting Session from table
                conn = Services.GetConnection(context, 1);
                if (conn == null) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                    return;
                }
                UtilityHelper helper = new UtilityHelper();
                helper.deleteUserSession(request, conn, session.getId());
                //Invalidating Session.
                session.invalidate();

                String Message = "Problem while getting you sign-in. Please contact System Administrator";

                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", Message);
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "FacilityLogin.html");
            } catch (Exception localException1) {
                out.println("local exception " + localException1);
            }
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.flush();
            out.close();
        }
    }

    private void SignIn(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, UtilityHelper helper) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu;
        StringBuffer Header;
        StringBuffer Footer;

        String UserId = request.getParameter("UserId").trim();
        String Password = request.getParameter("password");
        String UserType;
        int FoundUser = 0;
        int Found = 0;
        String Enabled;
        int FacilityIndex;
        String UserName;
        String menu;
        String FontColor;
        String ClientName;
        String DatabaseName;
        String DirectoryName;
        String passwordEnc;
        String SessionId;
        String PasswordExpiryDate = "";
        String CurrentDate;
        String CountDownMessage = "";
        String icon = "success";

        try {
            if ((UserId == null) || (UserId.trim().length() < 1)) {
                throw new Exception("Enter Valid UserId.");
            }
            if ((Password == null) || (Password.trim() == null) || (Password.trim().length() == 0)) {
                throw new Exception("Enter Valid Password.");
            }

            try {
                /*if (!UserId.equals("orange01") && !UserId.equals("victoria"))
                    passwordEnc = FacilityLogin.encrypt(Password);
                else
                    passwordEnc = Password;*/
                passwordEnc = FacilityLogin.encrypt(Password);

                Object[] LoginInfo = helper.getLoginInfo(request, UserId, passwordEnc, conn, context);
                Found = (Integer) LoginInfo[0];
                UserType = (String) LoginInfo[1];
                Enabled = (String) LoginInfo[2];
                UserName = (String) LoginInfo[3];
                FacilityIndex = (Integer) LoginInfo[4];
                menu = (String) LoginInfo[5];
                FontColor = (String) LoginInfo[6];
                ClientName = (String) LoginInfo[7];
                DatabaseName = (String) LoginInfo[8];
                DirectoryName = (String) LoginInfo[9];
                FoundUser = (Integer) LoginInfo[10];
                PasswordExpiryDate = (String) LoginInfo[11];
                CurrentDate = (String) LoginInfo[12];
            } catch (Exception Ex) {
                Services.DumException("Login", "Credentials Error", request, Ex, getServletContext());
                throw new Exception("Invalid UserId/Password. \nPlease enter correct credentials!!");
            }

            if (FoundUser == 0) {
                throw new Exception("UserID Incorrect!, Enter Valid User ID");
            }
            int CountDown = 0;
            if (!PasswordExpiryDate.equals("")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date1 = sdf.parse(CurrentDate);
                Date date2 = sdf.parse(PasswordExpiryDate);

                CountDown = helper.getDifferenceDays(date1, date2) - 1;

                if (CountDown < 6) {
                    CountDownMessage = "Your Password will be expired in " + CountDown + " Day. Please Change your password, Otherwise your ID will be blocked!!";
                    icon = "warning";
                }
                if (date1.after(date2)) {
                    throw new Exception("Your Password is Expired Please Contact System Administrator!!!");
                }
            }
//            if (Found > 0) {
//                int NoOfTries = helper.getNoOfTries_WrongAttempts(request, UserId.toUpperCase(), conn, context);
//                if (NoOfTries == 0)
//                    helper.saveWrongPasswordAttempts(request, UserId, passwordEnc, FacilityIndex, NoOfTries + 1, conn, context);
//                else
//                    helper.updateWrongEntriesNoOfTries(request, conn, UserId, context);//Update PAssWORD as well here
//
//                throw new Exception("Invalid UserId/Password !!!");
//            }
            if (Enabled.equals("N")) {
                throw new Exception("Your User ID has been blocked, kindly contact with System Administrator!!!");
            }

            //getSession(true) always return a new session
            HttpSession session = request.getSession(true);
            //get the session Id
            SessionId = session.getId();
            boolean checkUserSession = helper.checkUserSession(request, UserId, FacilityIndex, conn, context);
            if (!checkUserSession) {
                helper.saveUserSession(request, UserId, FacilityIndex, SessionId, conn, context);
            }
            //Capture Web Log Activity
            helper.captureWebLogActivity(request, UserId, FacilityIndex, "LogIn", conn, context);

            session.setAttribute("UserId", UserId);
            session.setAttribute("UserType", UserType);
            session.setAttribute("FacilityIndex", FacilityIndex);
            session.setAttribute("DatabaseName", DatabaseName);
            session.setAttribute("DirectoryName", DirectoryName);
            session.setAttribute("FontColor", FontColor);

            if (UserType.equals("9"))
                logActivity(request, "Admin LogIn", conn, "A", FacilityIndex, UserId);
            else
                logActivity(request, "LogIn", conn, "W", FacilityIndex, UserId);

            Header = suppMethods.Header(request, out, conn, context, UserId, "", FacilityIndex);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, context, UserId, "", FacilityIndex);
            Footer = suppMethods.Footer(request, out, conn, context, UserId, "", FacilityIndex);

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.SetField("UserName", UserName);
            Parser.SetField("FontColor", FontColor);
            Parser.SetField("UserType", UserType);
            Parser.SetField("FacilityIndex", String.valueOf(FacilityIndex));
            Parser.SetField("ClientId", String.valueOf(ClientName));
            Parser.SetField("CountDownMessage", String.valueOf(CountDownMessage));
            Parser.SetField("icon", String.valueOf(icon));
            Parser.SetField("CountDown", String.valueOf(CountDown));
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + menu);

        } catch (Exception e) {
            Services.DumException("Login", "Login", request, e, getServletContext());
            try {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", e.getMessage());
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "FacilityLogin.html");
            } catch (Exception localException1) {
            }
        }
    }

    private void Logout(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, UtilityHelper helper) {
        try {
            String UserType = "";
            //Returning a pre-existing session
            HttpSession session = request.getSession(false);
            if (session != null) {
                if (session.getAttribute("UserId") != null || !session.getAttribute("UserId").equals("")) {
                    String UserId = session.getAttribute("UserId").toString();
                    UserType = session.getAttribute("UserType").toString();

                    int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
                    //Capture Web Log Activity
                    helper.captureWebLogActivity(request, UserId, FacilityIndex, "LogOut", conn, context);

                    //get the session Id
                    String SessionId = session.getId();

                    helper.deleteUserSession(request, conn, SessionId);

                    session.removeAttribute("UserId");
                    session.removeAttribute("UserType");
                    session.removeAttribute("FacilityIndex");
                    session.removeAttribute("DatabaseName");
                    session.removeAttribute("DirectoryName");
                    session.removeAttribute("FontColor");
                    //Destroying the session
                    session.invalidate();
                }
            }
            String Message = "You have been successfully logged out from the System.";

/*
            Query = "DELETE FROM admin_activity_log WHERE UserId = '" + UserId + "' ";
            stmt = conn.createStatement();
            stmt.executeQuery(Query);
            stmt.close();

            Query = "DELETE FROM LoginTrail WHERE UserId = '" + UserId + "' ";
            stmt = conn.createStatement();
            stmt.executeQuery(Query);
            stmt.close();
*/

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Error", Message);
            if (UserType.equals("7"))
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "index.html");
            else
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
        } catch (Exception e) {
            try {
                Services.DumException("Login", "Logout", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", e.getMessage());
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
            } catch (Exception exception) {
            }
        }
    }

    private void logActivity(HttpServletRequest request, String Action, Connection conn, String UserType, int FacilityIndex, String UserId) {
        Query = null;
        pStmt = null;
        try {
            String UserIP = request.getRemoteAddr();

            if (UserType.equals("A")) {
                Query = " INSERT INTO admin_activity_log (userid,action,entrydate,ip) VALUES (?,?,now(),?) ";

                PreparedStatement ps = conn.prepareStatement(Query);
                ps.setString(1, UserId);
                ps.setString(2, Action);
                ps.setString(3, UserIP);
                ps.execute();
                ps.close();
            }

            pStmt = conn.prepareStatement(
                    "INSERT INTO LoginTrail(UserId, UserType, IP, Status, CreatedDate, FacilityIndex) VALUES(?,?,?,0,now(),?)");

            pStmt.setString(1, UserId);
            pStmt.setString(2, "W");
            pStmt.setString(3, UserIP);
            pStmt.setInt(4, FacilityIndex);

            pStmt.executeUpdate();
            pStmt.close();
        } catch (Exception e) {
            Services.DumException("Login", "logActivity", request, e, getServletContext());
        }
    }

    private void LoginAdmin(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext context, UtilityHelper helper) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int i = 0;
        final int Found = 0;
        String ClientId = "";
        String menu = "";
        String FontColor = "";
        String DatabaseName = "";
        String Directory = "";
        String UNAME = "";
        int userindex = 0;
        String UserId = request.getParameter("UserId");
        final String Password = request.getParameter("Passwd");
        int FacilityIndex = Integer.parseInt(request.getParameter("ClientIndex"));
        String passwordEnc = "";
        try {
            if (UserId == null || UserId.trim() == null || UserId.trim().length() < 1) {
                throw new Exception("Enter Valid UserId.");
            }
            if (Password == null || Password.trim() == null || Password.trim().length() == 0) {
                throw new Exception("Enter Valid Password.");
            }
            if (UserId.equals("tiffany")) {
                if (FacilityIndex != 10) {
                    throw new Exception("You have selected the wrong Facility. Please select Odessa from the selection!");
                }
            }
            passwordEnc = FacilityLogin.encrypt(Password);
            String UID = "";
            final String CompanyId = "";
            String UserType = "";
            String Enabled = "";
            Query = " select userid,username,enabled,usertype,indexptr from sysusers " +
                    "where upper(ltrim(rtrim(userid))) = '" + UserId.trim().toUpperCase() + "'" +
                    " and   password = '" + passwordEnc.trim() + "'";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (!hrset.next()) {
                throw new Exception("Invalid UserId or Password!!!");
            }
            UID = hrset.getString(1).trim();
            UNAME = hrset.getString(2).trim();
            Enabled = hrset.getString(3).trim();
            UserType = hrset.getString(4).trim();
            userindex = hrset.getInt(5);
            if (Enabled.compareTo("N") == 0) {
                throw new Exception("Your login has blocked , Contact OE Representative !!!");
            }
            if (Enabled.compareTo("W") == 0) {
                throw new Exception("Waiting for Approvel , Contact OE Representative !!!");
            }
            if (UserType.equals("7")) {
                Query = "Select userid from oe.sysusers where clientid = " + FacilityIndex;
                hstmt = conn.createStatement();
                hrset = hstmt.executeQuery(Query);
                if (hrset.next()) {
                    UserId = hrset.getString(1);
                }
            }
            hrset.close();
            hstmt.close();


/*            final ServletContext context2 = this.getServletContext();
            if (context2.getAttribute("ActiveSessions") == null) {
                final HttpSession session = request.getSession(true);
                final Hashtable ht = new Hashtable();
                ht.put(UserId, session);
                context2.setAttribute("ActiveSessions", (Object) ht);
            } else {
                final Hashtable ht2 = (Hashtable) context2.getAttribute("ActiveSessions");
                if (ht2.get(UserId) != null) {
                    final HttpSession session2 = (HttpSession) ht2.get(UserId);
                    ht2.remove(UserId);
                    context2.setAttribute("ActiveSessions", (Object) ht2);
                }
                final HttpSession session2 = request.getSession(true);
                ht2.put(UserId, session2);
                context2.setAttribute("ActiveSessions", (Object) ht2);
            }*/

            Query = "SELECT name,menu,FontColor,dbname,directory_1 FROM oe.clients WHERE Id = " + FacilityIndex;
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                ClientId = hrset.getString(1).trim();
                menu = hrset.getString(2).trim();
                FontColor = hrset.getString(3).trim();
                DatabaseName = hrset.getString(4).trim();
                Directory = hrset.getString(5).trim();
            }

            //getSession(true) always return a new session
            HttpSession session = request.getSession(true);
            //get the session Id
            String SessionId = session.getId();
            boolean checkUserSession = helper.checkUserSession(request, UserId, FacilityIndex, conn, context);
            if (!checkUserSession) {
                helper.saveUserSession(request, UserId, FacilityIndex, SessionId, conn, context);
            }
            //Capture Web Log Activity
            helper.captureWebLogActivity(request, UserId, FacilityIndex, "LogIn", conn, context);

            session.setAttribute("UserId", UserId);
            session.setAttribute("UserType", UserType);
            session.setAttribute("FacilityIndex", FacilityIndex);
            session.setAttribute("DatabaseName", DatabaseName);
            session.setAttribute("DirectoryName", Directory);
            session.setAttribute("FontColor", FontColor);

            StringBuilder ReportView = new StringBuilder();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserName", UNAME);
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.SetField("UserType", String.valueOf(UserType));
            Parser.SetField("ClientIndex", String.valueOf(FacilityIndex));
            Parser.SetField("FacilityIndex", String.valueOf(FacilityIndex));
            Parser.SetField("FontColor", FontColor);
            if (UserType.equals("7")) {
                if (FacilityIndex == 28 || FacilityIndex == 27) {
                    ReportView.append("<li><a href=\"/md/md.PatientDemographics?ActionID=GetInput&FacilityIndex=" + FacilityIndex + "\" target=\"NewFrame1\"><i class=\"ti-more\"></i><font color=\"white\">Patient Demographics</font></a></li>");
                    Parser.SetField("ReportView", ReportView.toString());
                }
                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + menu);
            }
        } catch (Exception e) {
            Services.DumException("Login", "Login", request, e, this.getServletContext());
            try {
                final Parsehtm Parser2 = new Parsehtm(request);
                Parser2.SetField("Error", e.getMessage());
                Parser2.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "index.html");
            } catch (Exception ex) {
            }
        }
    }
}
