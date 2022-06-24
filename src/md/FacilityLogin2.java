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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SuppressWarnings("Duplicates")

public class FacilityLogin2 extends HttpServlet {
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
        if (request.getSession(false) == null) {
            //response.sendRedirect("https://app1.rovermd.com:8443/md/FacilityLogin.html");
            return false;
        } else
            return true;
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
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin2.html");
                    return;
                }

                if (Action.compareTo("Login") == 0) {
                    SignIn(request, out, conn, context, helper);
                } else if (Action.compareTo("Logout") == 0) {
                    Logout(request, out, conn, context, helper);
                } else {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Error", "You are not logged in! Please log-in.");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin2.html");
                }
            } else {
                //out.println("Action is null");
                HttpSession session = request.getSession(false);
                if (session != null) {
                    UserId = session.getAttribute("UserId").toString();
                    UserType = session.getAttribute("UserType").toString();
                    FontColor = session.getAttribute("FontColor").toString();
                    DatabaseName = session.getAttribute("DatabaseName").toString();
                    FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());

                    SupportiveMethods suppMethods = new SupportiveMethods();
                    StringBuffer LeftSideBarMenu;
                    StringBuffer Header;
                    StringBuffer Footer;

                    conn = Services.GetConnection(context, 1);
                    if (conn == null) {
                        Parsehtm Parser = new Parsehtm(request);
                        Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                        Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin2.html");
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
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin2.html");
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
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin2.html");
                    return;
                }
                UtilityHelper helper = new UtilityHelper();
                helper.deleteUserSession(request, conn, session.getId());
                //Invalidating Session.
                session.invalidate();

                String Message = "Problem while getting you sign-in. Please contact System Administrator";

                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", Message);
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "FacilityLogin2.html");
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
        String Password = request.getParameter("Passwd");

        String UserType;
        int Found;
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

        try {
            if ((UserId == null) || (UserId.trim().length() < 1)) {
                throw new Exception("Enter Valid UserId.");
            }
            if ((Password == null) || (Password.trim() == null) || (Password.trim().length() == 0)) {
                throw new Exception("Enter Valid Password.");
            }

            try {
                //passwordEnc = FacilityLogin2.encrypt(Password);

                Object[] LoginInfo = helper.getLoginInfo(request, UserId, Password, conn, context);
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

            } catch (Exception Ex) {
                Services.DumException("Login", "Credentials Error", request, Ex, getServletContext());
                throw new Exception("Invalid UserId/Password. \nPlease enter correct credentials!!");
            }
            if (Found < 1) {
                int NoOfTries = helper.getNoOfTries_WrongAttempts(request, UserId.toUpperCase(), conn, context);
                if (NoOfTries == 0)
                    helper.saveWrongPasswordAttempts(request, UserId, Password, FacilityIndex, NoOfTries + 1, conn, context);
                else
                    helper.updateWrongEntriesNoOfTries(request, conn, UserId, context);

                throw new Exception("Invalid UserId/Password !!!");
            }
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
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + menu);

        } catch (Exception e) {
            Services.DumException("Login", "Login", request, e, getServletContext());
            try {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", e.getMessage());
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "FacilityLogin2.html");
            } catch (Exception localException1) {
            }
        }
    }

    private void Logout(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, UtilityHelper helper) {
        try {
            //Returning a pre-existing session
            HttpSession session = request.getSession(false);
            if (session != null) {
                if (session.getAttribute("UserId") != null || !session.getAttribute("UserId").equals("")) {
                    String UserId = session.getAttribute("UserId").toString();
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
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin2.html");
        } catch (Exception e) {
            try {
                Services.DumException("Login", "Logout", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", e.getMessage());
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin2.html");
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
}
