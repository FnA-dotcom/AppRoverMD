package md;

/**
 * Created by Siddiqui on 9/12/2017.
 */

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
public class FacilityLogin extends HttpServlet {
//    private PreparedStatement pStmt = null;
//    private String Query = "";
//    private Statement stmt = null;

    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[]{'T', '#', '3', 'B', '3', '$', 'T',
                    '$', '3', 'C', 'r', '3', 't', 'K', '3', 'Q'};

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

//                System.out.println("ACTION --> " + Action);
                if (Action.compareTo("Login") == 0) {
                    SignIn(request, out, conn, context, helper);
                } else if (Action.compareTo("Logout") == 0) {
                    Logout(request, out, conn, context, helper);
                } else if (Action.compareTo("LoginAdmin") == 0) {
                    this.LoginAdmin(request, response, out, conn, context, helper);
                } else if (Action.compareTo("saveNewPassword") == 0) {
                    saveNewPassword(request, response, out, conn, context, helper);
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

    private void SignInOLD(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, UtilityHelper helper) {

        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu;
        StringBuffer Header;
        StringBuffer Footer;

        String UserId = request.getParameter("UserId").trim();
        String Password = request.getParameter("password");
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
/*                if (!UserId.equals("orange01") && !UserId.equals("victoria"))
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

            } catch (Exception Ex) {
                Services.DumException("Login", "Credentials Error", request, Ex, getServletContext());
                throw new Exception("Invalid UserId/Password. \nPlease enter correct credentials!!");
            }

            if (Found < 1) {
                int NoOfTries = helper.getNoOfTries_WrongAttempts(request, UserId.toUpperCase(), conn, context);
                if (NoOfTries == 0)
                    helper.saveWrongPasswordAttempts(request, UserId, passwordEnc, FacilityIndex, NoOfTries + 1, conn, context);
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
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "FacilityLogin.html");
            } catch (Exception localException1) {
            }
        }
    }

    private void SignIn(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, UtilityHelper helper) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        String UserId = request.getParameter("UserId").trim();
        String Password = request.getParameter("password");
        String Query = "";
        Statement stmt = null;
        ResultSet rset = null;
        try {
            boolean useridChk;
            boolean PwdChk;
            String passwordEnc;
            int Found;
            int CountDown = 0;
            int FoundUser = 0;
            int UserIndex = 0;
            int MaxRetryAllowed = 0;
            int PRetry = 0;
            int LoginCount = 0;
            int PChangeDate = 0;
            String UserType;
            String Enabled = "N";
            String UserName;
            int FacilityIndex;
            String menu;
            String FontColor;
            String ClientName;
            String DatabaseName;
            String DirectoryName;
            String PasswordExpiryDate = "";
            String CurrentDate = "";
            String CountDownMessage = "";
            String icon = "success";
            int LocationIdx = 0;
            int isLocationAdmin = 0;
            int isPwdChanged = 0;

            String ar[] = helper.MasterConfig(conn, request, context);
            String exp_period = "";
            String wrong_pass = "";
            String pass_history = "";
            String enforced_pass = "";
            String app_ppolicy = "";

            exp_period = ar[0];
            wrong_pass = ar[1];
            pass_history = ar[2];
            enforced_pass = ar[3];
            app_ppolicy = ar[4];
            //System.out.println(exp_period+""+wrong_pass+""+pass_history+""+enforced_pass+""+app_ppolicy);

            String UserIP = request.getRemoteAddr();
            ArrayList locationIndex = new ArrayList();

            if (UserId == null || UserId.trim().length() < 1) {
                throw new Exception("UserId Empty, Please Enter UserId.");
                //Update PRETRY INcrement 1
            } else {
                //Check if Userid is correct
                useridChk = helper.CheckUserId(request, UserId, conn, context, "4,11");
                if (useridChk == false) {
                    throw new Exception("UserID Incorrect!, Enter Valid User ID");
                } else {
                    if (Password == null || Password.trim() == null || Password.trim().length() == 0) {
                        //helper.UpdatePRetry(request,UserId, conn, context,);
                        throw new Exception("Password Empty, Please Enter Password.");
                        //Update PRETRY INcrement 1
                    } else {
                        passwordEnc = encrypt(Password);
                        PwdChk = helper.CheckPwd(request, UserId, conn, context, passwordEnc);
                        if (PwdChk == false) {
//                            Object[] PrtryData = helper.UpdatePRetry(request,UserId, conn, context, passwordEnc);
                            final Object[] PrtryData = helper.UpdatePRetry(request, UserId, conn, context, passwordEnc);
                            MaxRetryAllowed = (Integer) PrtryData[0];
                            PRetry = (Integer) PrtryData[1];
                            //throw new Exception("Please Enter Valid Password.");
//                            System.out.println(MaxRetryAllowed+"----"+PRetry);
                            if (PRetry >= Integer.parseInt(wrong_pass)) {
                                helper.UpdateEnabledCol(request, UserId, conn, context);
                                throw new Exception("Your User ID has been blocked due to multiple wrong attempts. kindly contact System Administrator!!!");
                            } else {
                                throw new Exception("Please Enter Valid Password.");
                            }
                            //Update PRETRY INcrement 1
                        } else {
                            try {
                                final Object[] LoginInfo = helper.getLoginInfo(request, UserId, passwordEnc, conn, context);
                                Found = (int) LoginInfo[0];
                                UserType = (String) LoginInfo[1];
                                Enabled = (String) LoginInfo[2];
                                UserName = (String) LoginInfo[3];
                                FacilityIndex = (int) LoginInfo[4];
                                menu = (String) LoginInfo[5];
                                FontColor = (String) LoginInfo[6];
                                ClientName = (String) LoginInfo[7];
                                DatabaseName = (String) LoginInfo[8];
                                DirectoryName = (String) LoginInfo[9];
                                FoundUser = (Integer) LoginInfo[10];
                                PasswordExpiryDate = (String) LoginInfo[11];
                                CurrentDate = (String) LoginInfo[12];
                                UserIndex = (Integer) LoginInfo[13];
                                MaxRetryAllowed = (Integer) LoginInfo[14];
                                PRetry = (Integer) LoginInfo[15];
                                LoginCount = (Integer) LoginInfo[16];
                                PChangeDate = (Integer) LoginInfo[17];
                                LocationIdx = (Integer) LoginInfo[18];
                                isLocationAdmin = (Integer) LoginInfo[19];
                                isPwdChanged = (Integer) LoginInfo[20];

                            } catch (Exception Ex) {
                                Services.DumException("Login", "Credentials Error", request, Ex, this.getServletContext());
                                throw new Exception("Invalid UserId/Password. \nPlease enter correct credentials!!");
                                //Update PRETRY INcrement 1
                            }

                        }
                    }
                }
            }

            if (Enabled.equals("N")) {
                //throw new Exception("Your User ID is blocked, kindly contact with System Administrator!!!");
                throw new Exception("You cannot Login into the system since your UserId is blocked. Kindly contact with System Administrator!!!");
            }

            if (PRetry >= Integer.parseInt(wrong_pass)) {
                throw new Exception("Your User ID has been blocked due to multiple wrong attempts. Kindly Contact System Administrator");
            } else if (LoginCount == 0) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserName", UserName);
                Parser.SetField("UserId", UserId);
                Parser.SetField("Error", "First Login Password Change");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/ChangePwdForce.html");
                return;

            } else if (PChangeDate >= Integer.parseInt(exp_period)) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserName", UserName);
                Parser.SetField("UserId", UserId);
                Parser.SetField("Error", "Password Expired");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/ChangePwdForce.html");
                return;
            } else {
                helper.UpdateLoginCount(request, UserId, conn, context);
                Query = "update sysusers set Pretry=0,LastLoginDate = NOW() " +
                        "where ltrim(rtrim(UPPER(userid)))=ltrim(rtrim(UPPER('" + UserId + "')))";
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

                if (Integer.parseInt(exp_period) - PChangeDate < 6) {
                    CountDownMessage = "Your Password will be expired in " + (Integer.parseInt(exp_period) - PChangeDate) + " Day. Please Change your password.";
                    icon = "warning";
                }
                if (UserType.equals("11")) {
                    Query = "SELECT LocationIndex FROM " + DatabaseName + ".Location_mapping " +
                            " WHERE UserIndex = " + UserIndex;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        locationIndex.add(rset.getInt(1));
                    }
                    rset.close();
                    stmt.close();
                }

                HttpSession session = request.getSession(true);
                String SessionId = session.getId();
                boolean checkUserSession = helper.checkUserSession(request, UserId, FacilityIndex, conn, context);
                if (!checkUserSession) {
                    helper.saveUserSession(request, UserId, FacilityIndex, SessionId, conn, context);
                }
                helper.captureWebLogActivity(request, UserId, FacilityIndex, "LogIn", conn, context);
                session.setAttribute("UserId", UserId);
                session.setAttribute("UserType", UserType);
                session.setAttribute("FacilityIndex", FacilityIndex);
                session.setAttribute("DatabaseName", DatabaseName);
                session.setAttribute("DirectoryName", DirectoryName);
                session.setAttribute("FontColor", FontColor);
                session.setAttribute("UserIndex", UserIndex);
                session.setAttribute("isLocationAdmin", isLocationAdmin);
                session.setAttribute("LocationArray", locationIndex);
                if (UserType.equals("9")) {
                    logActivity(request, "Admin LogIn", conn, "A", FacilityIndex, UserId);
                } else {
                    logActivity(request, "LogIn", conn, "W", FacilityIndex, UserId);
                }
                //if login count == 0 then OPEN Change Password Screen to forcefully chnage the password for the user.
                //Udpate Login COut here

                StringBuffer Header = suppMethods.Header(request, out, conn, context, UserId, "", FacilityIndex);
                StringBuffer LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, context, UserId, "", FacilityIndex);
                StringBuffer Footer = suppMethods.Footer(request, out, conn, context, UserId, "", FacilityIndex);

                Parsehtm Parser = new Parsehtm(request);
                if (isPwdChanged == 0) {
                    Parser.SetField("FacilityIndex", String.valueOf(FacilityIndex));
                    Parser.SetField("UserIndex", String.valueOf(UserIndex));
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/PasswordScreen.html");
                } else {
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
                    Parser.SetField("UserIndex", String.valueOf(UserIndex));
//                Parser.SetField("LocationIndex", String.valueOf(LocationIdx));
                    //Parser.SetField("CountDown", String.valueOf(CountDown));
                    Parser.SetField("ShowFacility", "<h5 class=\"mb-0\"><b> " + ClientName + " </b></h5>");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + menu);
                }
            }
        } catch (Exception e) {
            Services.DumException("Login", "Login", request, e, this.getServletContext());
            try {
                Parsehtm Parser2 = new Parsehtm(request);
                Parser2.SetField("Error", e.getMessage());
                Parser2.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "FacilityLogin.html");
            } catch (Exception ex) {
            }
        }
    }

    private void LoginAdmin(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext context, UtilityHelper helper) {
        Statement stmt = null;
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int i = 0;
        int Found = 0;
        String ClientId = "";
        String menu = "";
        String FontColor = "";
        String DatabaseName = "";
        String Directory = "";
        String UNAME = "";
        int userindex = 0;
        String icon = "success";
        String UserId = request.getParameter("UserId");
        final String Password = request.getParameter("Passwd");
        int FacilityIndex = Integer.parseInt(request.getParameter("ClientIndex"));
        String passwordEnc = "";
        boolean useridChk;
        boolean PwdChk;
        String Query1 = "";
        ResultSet resultSet = null;
        Statement statement = null;

        int MaxRetryAllowed = 0;
        int PRetry = 0;
        String UID = "";
        String CompanyId = "";
        String UserType = "";
        String Enabled = "";
        int isPwdChanged = 0;

        try {
            String ar[] = helper.MasterConfig(conn, request, context);
            String exp_period = "";
            String wrong_pass = "";
            String pass_history = "";
            String enforced_pass = "";
            String app_ppolicy = "";

            exp_period = ar[0];
            wrong_pass = ar[1];
            pass_history = ar[2];
            enforced_pass = ar[3];
            app_ppolicy = ar[4];

            if (UserId == null || UserId.trim().length() < 1) {
                throw new Exception("UserId Empty, Please Enter UserId.");
                //Update PRETRY INcrement 1
            } else {
                //Check if Userid is correct
                useridChk = helper.CheckUserId(request, UserId, conn, context, "7,10,9,12,2,3,1");
                if (!useridChk) {
                    throw new Exception("UserID Incorrect!, Enter Valid User ID");
                } else {
                    if (Password == null || Password.trim() == null || Password.trim().length() == 0) {
                        //helper.UpdatePRetry(request,UserId, conn, context,);
                        throw new Exception("Password Empty, Please Enter Password.");
                    } else {
                        passwordEnc = encrypt(Password);
                        PwdChk = helper.CheckPwd(request, UserId, conn, context, passwordEnc);
                        if (!PwdChk) {
                            final Object[] PrtryData = helper.UpdatePRetry(request, UserId, conn, context, passwordEnc);
                            MaxRetryAllowed = (Integer) PrtryData[0];
                            PRetry = (Integer) PrtryData[1];

                            if (PRetry >= Integer.parseInt(wrong_pass)) {
                                helper.UpdateEnabledCol(request, UserId, conn, context);
                                throw new Exception("Your User ID has been blocked due to multiple wrong attempts. kindly contact System Administrator!!!");
                            } else {
                                throw new Exception("Please Enter Valid Password.");
                            }
                        } else {
/*
                            if (UserId.equals("tiffany") && FacilityIndex != 10) {
                                throw new Exception("You have selected the wrong Facility. Please select Odessa from the selection!");
                            }
*/
//                            System.out.println("Password " + Password);
//                            passwordEnc = encrypt(Password);
//                            System.out.println("passwordEnc " + passwordEnc);
                            Query = " select userid,username,enabled,usertype,indexptr,IFNULL(isPwdChanged,0) " +
                                    " from sysusers " +
                                    "where upper(ltrim(rtrim(userid))) = '" + UserId.trim().toUpperCase() + "'" +
                                    " and   password = '" + passwordEnc.trim() + "'";
                            hstmt = conn.createStatement();
                            hrset = hstmt.executeQuery(Query);
//                            System.out.println("Query " + Query);
                            if (!hrset.next()) {
                                throw new Exception("Invalid UserId or Password!!!");
                            }
                            UID = hrset.getString(1).trim();
                            UNAME = hrset.getString(2).trim();
                            Enabled = hrset.getString(3).trim();
                            UserType = hrset.getString(4).trim();
                            userindex = hrset.getInt(5);
                            isPwdChanged = hrset.getInt(6);
//                            System.out.println("UserType " + UserType);
//                            UserType.equals("10")
                            if (Arrays.asList("10", "9", "12", "2", "3", "1").contains(UserType)) {
                                Multimap<String, Integer> multimap = ArrayListMultimap.create();
//                                Query = "SELECT indexptr,userid FROM oe.sysusers WHERE usertype = 10";
                                Query = "SELECT indexptr,userid FROM oe.sysusers WHERE usertype in (10,9,12,2,3,1)";
                                hstmt = conn.createStatement();
                                hrset = hstmt.executeQuery(Query);
                                while (hrset.next()) {
                                    Query1 = "SELECT FacilityIdx FROM oe.AdvocateSMSNumber WHERE AdvocateIdx = " + hrset.getInt(1);
                                    statement = conn.createStatement();
                                    resultSet = statement.executeQuery(Query1);
                                    while (resultSet.next()) {
                                        multimap.put(hrset.getString(2), resultSet.getInt(1));
                                    }
                                    resultSet.close();
                                    statement.close();
                                }
                                hrset.close();
                                hstmt.close();

//                                System.out.println("UserId " + UserId);
//                                System.out.println("FacilityIndex " + FacilityIndex);
//                                System.out.println("multimap " + multimap);

                                if (!multimap.get(UserId).contains(FacilityIndex)) {
                                    throw new Exception("You have selected the wrong Facility. Please select correct facility from the selection!");
                                }
                            }


                            if (Enabled.compareTo("N") == 0) {
                                throw new Exception("Your login has blocked , Contact Rover Representative !!!");
                            }
                            if (Enabled.compareTo("W") == 0) {
                                throw new Exception("Waiting for Approval , Contact Rover Representative !!!");
                            }
                            Query = "update sysusers set LastLoginDate = NOW() " +
                                    "where ltrim(rtrim(UPPER(userid)))=ltrim(rtrim(UPPER('" + UserId + "')))";
                            stmt = conn.createStatement();
                            stmt.executeUpdate(Query);
                            stmt.close();
                            helper.UpdateLoginCount(request, UserId, conn, context);

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

                            Query = "SELECT name,menu,FontColor,dbname,DirectoryName FROM oe.clients WHERE Id = " + FacilityIndex;
                            hstmt = conn.createStatement();
                            hrset = hstmt.executeQuery(Query);
                            if (hrset.next()) {
                                ClientId = hrset.getString(1).trim();
                                menu = hrset.getString(2).trim();
                                FontColor = hrset.getString(3).trim();
                                DatabaseName = hrset.getString(4).trim();
                                Directory = hrset.getString(5).trim();
                            }
                        }
                    }
                }
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
            session.setAttribute("UserIndex", userindex);
            session.setAttribute("FacilityIndex", FacilityIndex);
            session.setAttribute("DatabaseName", DatabaseName);
            session.setAttribute("DirectoryName", Directory);
            session.setAttribute("FontColor", FontColor);

            Parsehtm Parser = new Parsehtm(request);

            StringBuilder ReportView = new StringBuilder();
            StringBuilder SMSSend = new StringBuilder();
            if (isPwdChanged == 0) {
                Parser.SetField("FacilityIndex", String.valueOf(FacilityIndex));
                Parser.SetField("UserIndex", String.valueOf(userindex));
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/PasswordScreen.html");
            } else {

                Parser.SetField("UserName", UNAME);
                Parser.SetField("ClientId", String.valueOf(ClientId));
                Parser.SetField("UserIndex", String.valueOf(userindex));
                Parser.SetField("UserType", String.valueOf(UserType));
                Parser.SetField("ClientIndex", String.valueOf(FacilityIndex));
                Parser.SetField("FacilityIndex", String.valueOf(FacilityIndex));
                Parser.SetField("FontColor", FontColor);
                Parser.SetField("icon", String.valueOf(icon));
                Parser.SetField("ShowFacility", "<h5 class=\"mb-0\">Logged As : <b> " + ClientId + " </b></h5>");

//            if (UserType.equals("10") || UserType.equals("7")) {
//                if (FacilityIndex == 27 || FacilityIndex == 29) {
                ReportView.append("<li><a id=\"PatientDemographics\" style=\"display: none;\" href=\"/md/md.PatientDemographics?ActionID=GetInput&FacilityIndex=" + FacilityIndex + "\" target=\"NewFrame1\"><i class=\"ti-more\"></i>Patient Demographics</a></li>");
                Parser.SetField("ReportView", ReportView.toString());
//                }

                SMSSend.append("<li id=\"SMSManagement\" style=\"display: none;\" class=\"treeview\">");
                SMSSend.append("<a href=\"#\">");
                SMSSend.append("<i class=\"ti-pencil-alt\"></i>");
                SMSSend.append("<span>SMS Management</span>");
                SMSSend.append("<span class=\"pull-right-container\">");
                SMSSend.append("<i class=\"fa fa-angle-right pull-right\"></i>");
                SMSSend.append("</span></a>");
                SMSSend.append("<ul class=\"treeview-menu\">");
                SMSSend.append("<li id=\"SingleSMS\" style=\"display: none;\"><a href=\"/md/md.SendSMS?ActionID=GetInput \"target=\"NewFrame1\"><i class=\"ti-more\"></i> Single SMS </a></li>");
                SMSSend.append("<li id=\"BulkSMS\" style=\"display: none;\"><a href=\"/md/md.SendSMSBulk?ActionID=GetInput \"target=\"NewFrame1\"><i class=\"ti-more\"></i> Bulk SMS </a></li>");
                SMSSend.append("<li id=\"SMSReport\" style=\"display: none;\"><a href=\"/md/md.SMS_Report?ActionID=GetInput \"target=\"NewFrame1\"><i class=\"ti-more\"></i> SMS Report </a></li>");
                SMSSend.append("</ul> </li>");
                Parser.SetField("SMSSend", SMSSend.toString());
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

    private void saveNewPassword(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext context, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        boolean LowerFound = false;
        boolean NumericFound = false;
        boolean UpperFound = false;
        int CategoriesFound = 0;
        boolean AlreadyUsed = false;
        String UserId = "";
        String passwordEnc = "";
        try {
            String nPassword1 = request.getParameter("nPassword1").trim();
            String nPassword2 = request.getParameter("nPassword2");
            int userIndex = Integer.parseInt(request.getParameter("UserIndex").trim());
            int FacilityIndex = Integer.parseInt(request.getParameter("FacilityIndex").trim());

//            System.out.println("nPASS" + nPassword1);
//            System.out.println("userIndex" + userIndex);

            UserId = helper.getAdvocateLoginId(request, conn, context, userIndex);
//            System.out.println("UserId" + UserId);
            passwordEnc = FacilityLogin.encrypt(nPassword1);
//            System.out.println("passwordEnc" + passwordEnc);
            String previousPassword = "";
            String username = "";
            Query = "SELECT password,username FROM oe.sysusers WHERE userid = '" + UserId + "' ";
            System.out.println("QQQ " + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                previousPassword = rset.getString(1);
                username = rset.getString(2);
            }
            rset.close();
            stmt.close();
/*
            System.out.println("previousPassword " + previousPassword);

            String ClientName = "";
            Query = "SELECT name FROM oe.clients WHERE Id = " + FacilityIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                ClientName = rset.getString(1).trim();
            rset.close();
            stmt.close();


            if (!passwordEnc.equals(previousPassword)) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserName", username);
                Parser.SetField("ClientName", ClientName);
                Parser.SetField("Error", "Current Password Does not Match!!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/PasswordScreen.html");
                return;
            }
*/

            String ar[] = helper.MasterConfig(conn, request, context);
            String exp_period = "";
            String wrong_pass = "";
            String pass_history = "";
            String enforced_pass = "";
            String app_policy = "";

            exp_period = ar[0];
            wrong_pass = ar[1];
            pass_history = ar[2];
            enforced_pass = ar[3];
            app_policy = ar[4];

            if (nPassword1 == null || nPassword1.trim() == null || nPassword1.trim().length() < 8) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Password cannot be left empty or it should be minimum of 8 characters.");
                Parser.SetField("FacilityIndex", String.valueOf(FacilityIndex));
                Parser.SetField("UserIndex", String.valueOf(userIndex));
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/PasswordScreen.html");
                return;
            }


            // Numeric check
            int Numeric = 0;
            for (int i = 0; i < nPassword1.length(); i++) {
                Numeric = (int) nPassword1.charAt(i);
                if ((Numeric >= 48 && Numeric <= 57)) {
                    NumericFound = true;
                    CategoriesFound++;
                    break;
                }
            }

            // UpperCase check
            int UpperCase = 0;

            for (int i = 0; i < nPassword1.length(); i++) {
                UpperCase = (int) nPassword1.charAt(i);

                if ((UpperCase >= 65 && UpperCase <= 90)) {
                    UpperFound = true;
                    CategoriesFound++;
                    break;
                }
            }

            // Lowercase check
            int LowerCase = 0;

            for (int i = 0; i < nPassword1.length(); i++) {
                LowerCase = (int) nPassword1.charAt(i);

                if ((LowerCase >= 97 && LowerCase <= 122)) {
                    LowerFound = true;
                    CategoriesFound++;
                    break;
                }
            }

            String pattern1 = "(?=.*[@#$%^_+=]).{8,}";
            if (nPassword1.matches(pattern1)) {
                CategoriesFound++;
            }


            int Useridfound = 0;
            Pattern pattern = Pattern.compile(UserId, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(nPassword1);
            while (matcher.find()) {
                //System.out.println(matcher.group());
                Useridfound = 1;
            }
            if (Useridfound == 0) {
                CategoriesFound++;
                //System.out.println("Not Found User Element");
            }

            if (CategoriesFound < 5) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Password must contains 8 characters and   5 categories   Upper Case, Lower Case ,Numbers  and Special Character (@#$%^_+=) ,Password Cannot Contain UserId Elements.");
                Parser.SetField("FacilityIndex", String.valueOf(FacilityIndex));
                Parser.SetField("UserIndex", String.valueOf(userIndex));
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/PasswordScreen.html");
                return;
            }

            nPassword1 = FacilityLogin.encrypt(nPassword1);
            if (pass_history.compareTo("0") != 0) {
                stmt = conn.createStatement();
                Query = " select NewPassword from oe.PasswordHistory where UserId='" + UserId + "' order by CreatedDate desc limit " + pass_history;
                rset = stmt.executeQuery(Query);

                while (rset.next()) {
                    if (rset.getString(1).compareToIgnoreCase(nPassword1) == 0) {
                        AlreadyUsed = true;
                        break;
                    }
                }
                rset.close();
                stmt.close();

                if (AlreadyUsed) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Error", "Password Already Used, please do not choose  password from your last passwords");
                    Parser.SetField("FacilityIndex", String.valueOf(FacilityIndex));
                    Parser.SetField("UserIndex", String.valueOf(userIndex));
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/PasswordScreen.html");
                    return;
                }
            }


            helper.updateUserPassword(request, context, conn, out, nPassword1, UserId);

            helper.savePasswordLogs(request, conn, context, passwordEnc, nPassword1, UserId, FacilityIndex);


            Query = "UPDATE oe.sysusers SET isPwdChanged = 1 WHERE indexptr = " + userIndex;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            Logout(request, out, conn, context, helper);

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Error", "Password has changed. Please re-login");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
            out.flush();
            out.close();
        } catch (Exception e) {
            Services.DumException("Login", "saveNewPassword", request, e, this.getServletContext());
            try {
                Parsehtm Parser2 = new Parsehtm(request);
                Parser2.SetField("Error", e.getMessage());
                Parser2.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "FacilityLogin.html");
            } catch (Exception ex) {
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

//            System.out.println("USER TYPE " + UserType);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Error", Message);
//            if (UserType.equals("7") || UserType.equals("10"))
            if (Arrays.asList("7", "10", "9", "12", "2", "3", "1").contains(UserType))
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
        String Query = "";
        PreparedStatement pStmt = null;
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

    private void LoginAdminOLD(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext context, UtilityHelper helper) {
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
        String icon = "success";
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
            if (UserId.equals("tiffany") && FacilityIndex != 10) {
                throw new Exception("You have selected the wrong Facility. Please select Odessa from the selection!");
            }
            passwordEnc = encrypt(Password);
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
                throw new Exception("Waiting for Approval , Contact OE Representative !!!");
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
            session.setAttribute("UserIndex", userindex);
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
            Parser.SetField("icon", String.valueOf(icon));
            Parser.SetField("ShowFacility", "<h5 class=\"mb-0\">Logged As : <b> " + ClientId + " </b></h5>");

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
