package md;

/**
 * Created by Siddiqui on 9/12/2017.
 */

import Parsehtm.Parsehtm;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

@SuppressWarnings("Duplicates")

public class FacilityLoginOLD extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
    private static final String POST_PARAMS = "userName=Pankaj";
    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[]{'T', '#', '3', 'B', '3', '$', 'T',
                    '$', '3', 'C', 'r', '3', 't', 'K', '3', 'Q'};
    private PreparedStatement pStmt = null;
    private Statement stmt = null;
    private ResultSet rset = null;
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

/*    private static final byte[] keyValue =
            new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't',
                    'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};*/

    public void HandleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        String Action = null;
        PrintWriter out = new PrintWriter(response.getOutputStream());
/*
        Services supp = new Services();
        String connect_string = supp.GetConnectString();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(connect_string);
        } catch (Exception excp) {
            conn = null;
            System.out.println("Exception excp conn: " + excp.getMessage());
        }
*/

        try {
            //String constring = Services.ConnString(getServletContext(), 1);
            conn = Services.GetConnection(getServletContext(), 1);
            if (conn == null) {
                out.println("Unable to connect with Database...1");
                //out.println(constring);
                out.flush();
                out.close();
                return;
            }
        } catch (Exception excp) {
            conn = null;
            out.println("Exception excp conn: " + excp.getMessage());
            //out.println(excp.getMessage());
            //return;
        }

        ServletContext context = null;
        context = getServletContext();


        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setHeader("Expires", "0"); // Proxies.

        try {
            if (request.getParameter("Action") == null) {
                Action = "Home";
                return;
            }
            Action = request.getParameter("Action");

            if (Action.compareTo("Login") != 0) {
                boolean ValidSession = Services.checkSession(out, request);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    conn.close();
                    return;
                }
            }
            if (Action.compareTo("Login") == 0) {
                SignIn(request, out, conn, context, response);
            } else if (Action.compareTo("Logout") == 0) {
                Logout(request, response, out, conn, context);
            } else {
                out.println("<font size=\"3\" face=\"Calibri\">Under Development  " + Action + "</font>");
            }
            out.flush();
            out.close();

            conn.close();
        } catch (Exception e) {
            out.println("In Main Exception " + e.getMessage());
            Services.DumException("Login", "HandleRequest", request, e, getServletContext());
            try {
                Cookie cookie = new Cookie("UserId", "");
                response.addCookie(cookie);

                String Message = "You have been logged out of the System.";

                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", Message);
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "index.html");
            } catch (Exception localException1) {
            }
        }
        out.flush();
        out.close();
    }

    private void SignIn(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, HttpServletResponse response) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "", UserType = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();

        String UserId = request.getParameter("UserId").trim();
        String Password = request.getParameter("password");

        try {
            if ((UserId == null) || (UserId.trim().length() < 1)) {
                throw new Exception("Enter Valid UserId.");
            }
            if ((Password == null) || (Password.trim() == null) || (Password.trim().length() == 0)) {
                throw new Exception("Enter Valid Password.");
            }
            int Found = 0;
            String Enabled = "";
            int FacilityIndex = 0;
            String UserName = "";
            String menu = "";
            String FontColor = "";
            String ClientName = "";
            String test = "1 <br>";

            try {

                String passwordEnc = FacilityLoginOLD.encrypt(Password);
                test = "2 <br>";

                Query = " SELECT IFNULL(count(*),0),IFNULL(a.UserType,'-'),IFNULL(a.Enabled,0), " +
                        " IFNULL(ltrim(rtrim(a.UserName)),'-'),IFNULL(a.clientid,0),IFNULL(b.menu,'-'), IFNULL(b.FontColor,''), IFNULL(b.name,'') " +
                        " FROM sysusers  a " +
                        " STRAIGHT_JOIN clients b ON a.clientid = b.Id " +
                        " WHERE upper(trim(a.userid))='" + UserId.toUpperCase() + "'  " +
                        " AND a.password='" + passwordEnc + "' ";
                test = "3 <br>";
                hstmt = conn.createStatement();
                hrset = hstmt.executeQuery(Query);
                if (hrset.next()) {
                    test = "4 <br>";
                    Found = hrset.getInt(1);
                    test = "5 <br>";
                    UserType = hrset.getString(2).trim();
                    test = "6 <br>";
                    Enabled = hrset.getString(3);
                    test = "7 <br>";
                    UserName = hrset.getString(4).trim();
                    test = "8 <br>";
                    FacilityIndex = hrset.getInt(5);
                    test = "9 <br>";
                    menu = hrset.getString(6);
                    test = "10 <br>";
                    FontColor = hrset.getString(7);
                    ClientName = hrset.getString(8);
                }
                hrset.close();
                hstmt.close();


                test = "11 <br>";
            } catch (Exception Ex) {
                out.println("Split " + test + "<br>");
                out.println("Ex " + Ex.getMessage() + "<br>");
                Services.DumException("Login", "Credentials Error", request, Ex, getServletContext());
                throw new Exception("Invalid UserId/Password. \nPlease enter correct credentials!!");
            }

            if (Found < 1) {
                throw new Exception("Invalid UserId/Password !!!");
            }
            if (Enabled.equals("N")) {
                throw new Exception("Your User ID has been blocked, kindly contact with System Administrator!!!");
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("UserId", UserId);
            session.setAttribute("UserType", UserType);
            session.setAttribute("FacilityIndex", FacilityIndex);

            Cookie uid = new Cookie("UserId", UserId.trim());

            Cookie usertype = new Cookie("UserType", UserType.trim());
            Cookie username = new Cookie("username", UserName.trim());
//out.println("USerid: "+UserId+"<br>");
            response.addCookie(uid);
            response.addCookie(usertype);
            //response.addCookie(username);

            if (UserType.equals("9"))
                logActivity(request, "Admin LogIn", conn, "A", FacilityIndex);
            else
                logActivity(request, "LogIn", conn, "W", FacilityIndex);

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
//            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + menu);
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + menu);


        } catch (Exception e) {
            out.println("In Login Exception " + e.getMessage() + "<br>");
            Services.DumException("Login", "Login", request, e, getServletContext());
            try {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", e.getMessage());
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "FacilityLogin.html");
            } catch (Exception localException1) {
            }
        }
    }

    private void Logout(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext context) {


        try {
            HttpSession session1 = request.getSession(true);
            String UserId = session1.getAttribute("UserId").toString();
            HttpSession session = request.getSession(false);
            session.removeAttribute("UserId");
            session.invalidate();

            if (context.getAttribute("ActiveSessions") != null) {
                Hashtable ht = (Hashtable) context.getAttribute("ActiveSessions");

                ht.remove(UserId);
                context.setAttribute("ActiveSessions", ht);
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

    public void logActivity(HttpServletRequest request, String Action, Connection conn, String UserType, int FacilityIndex) {
        String Query = null;
        try {
            HttpSession session1 = request.getSession(true);
            String UserId = session1.getAttribute("UserId").toString();
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
