//
// Decompiled by Procyon v0.5.36
//

package md;

import DAL.Payments;
import DAL.SelfPaymentHelper;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import PaymentIntegrations.CardConnectPayment;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;

@SuppressWarnings("Duplicates")
public class SelfServicePortal extends HttpServlet {
    private static final byte[] keyValue;

    static {
        keyValue = new byte[]{84, 35, 51, 66, 51, 36, 84, 36, 51, 67, 114, 51, 116, 75, 51, 81};
    }

    private PreparedStatement pStmt;
    private String Query;
    private Statement stmt;
    private ResultSet rset;

    public SelfServicePortal() {
        this.pStmt = null;
        this.Query = "";
    }

    public static String encrypt(final String Data) throws Exception {
        final Key key = generateKey();
        final Cipher c = Cipher.getInstance("AES");
        c.init(1, key);
        final byte[] encVal = c.doFinal(Data.getBytes());
        final String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

    public static String decrypt(final String encryptedData) throws Exception {
        final Key key = generateKey();
        final Cipher c = Cipher.getInstance("AES");
        c.init(2, key);
        final byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        final byte[] decValue = c.doFinal(decordedValue);
        final String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        final Key key = new SecretKeySpec(SelfServicePortal.keyValue, "AES");
        return key;
    }

    public static boolean checkSession(final PrintWriter out, final HttpServletRequest request, final ServletContext context, final HttpServletResponse response) throws IOException {
        return request.getSession(false) != null;
    }

    public static int getAge(LocalDate dob) {
        LocalDate curDate = LocalDate.now();
        return Period.between(dob, curDate).getYears();
    }

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.HandleRequest(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.HandleRequest(request, response);
    }

    public void HandleRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        Connection conn = null;
        final PrintWriter out = new PrintWriter(response.getOutputStream());
        final ServletContext context = this.getServletContext();
        final UtilityHelper helper = new UtilityHelper();
        try {

            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            final String Action = request.getParameter("Action");


            final Payments payment = new Payments();
            final SelfPaymentHelper paymentPortal = new SelfPaymentHelper();

            if (request.getParameter("Action") != null) {
                conn = Services.GetConnection(context, 1);
                if (conn == null) {
                    final Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "SSPLogin.html");
                    return;
                }

                switch (Action) {
                    case "exceptionMessaging":
                        exceptionMSGHandling(request, out, context, helper);
                        break;
                    case "LogIn":
                        this.LogIn(request, out, conn, context, helper);
                        break;
                    case "SSP_LOGIN":
                        this.LogInScreen(request, out, conn, context, helper);
                        break;
                    case "Logout":
                        this.Logout(request, out, conn, context, helper);
                        break;
                    case "RecoverPwd":
                        this.RecoverPwd(request, out, conn, context, helper);
                        break;
                    case "RecoverPwd_Data":
                        this.RecoverPwd_Data(request, out, conn, context, helper);
                        break;
                    case "SignUp":
                        this.SignUp(request, out, conn, context, helper, paymentPortal);
                        break;
                    case "ChangePWD":
                        this.ChangePwd(request, out, conn, context, helper);
                        break;
                    case "saveChangePwd":
                        this.saveChangePwd(request, out, conn, context, helper);
                        break;
                    case "SIGNUP_DATA":
                        this.SIGNUP_DATA(request, out, conn, context, helper);
                        break;
                    case "xyz":
                        this.SavePwd(request, out, conn, context, helper);
                        break;
                    case "8!@9$1$2*(5)64578":
                        this.PwdChangeScreen(request, out, conn, context, helper);
                        break;
                    case "CollectPayment_View":
                        this.CollectPayment_View(request, out, conn, context, helper);
                        break;
                    case "PaymentHistory":
                        this.PaymentHistory(request, out, conn, context, helper);
                        break;
                    case "InvoicePdf":
                        this.InvoicePdf(request, response, out, conn, context, helper);
                        break;
                    case "PayNow":
                        this.PayNow(request, out, conn, context, helper);
                        break;
                    case "makeCardConnectPayment":
                        this.cardConnectPaymentSave(request, out, conn, context, helper, payment);
                        break;
                    case "SendSignUpRequest":
                        this.SendSignUpRequest(request, out, conn, context, helper, paymentPortal);
                        break;
                    case "e89b<12d3>a456":
                        this.RecoverPwd_Save(request, out, conn, context, helper);
                        break;
                    case "9Ey_s59)8sM^upsuUh*D":
                        this.PwdChangeScreen_Reset(request, out, conn, context, helper);
                        break;
                    default:
                        final Parsehtm Parser = new Parsehtm(request);
                        Parser.SetField("Error", "You are not logged in! Please log-in.");
                        Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "SSPLogin.html");
                        break;
                }
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal Handle Request *** MES#000 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "handleRequest", conn);
            Services.DumException("SelfServicePortal", "handleRequest", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "SelfServicePortal");
            Parser.SetField("ActionID", "exceptionMessaging");
            Parser.SetField("Message", "MES#000");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            out.flush();
            out.close();
        }
    }

    private void exceptionMSGHandling(final HttpServletRequest request, final PrintWriter out, final ServletContext context, final UtilityHelper helper) throws FileNotFoundException {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("msg", "Something went wrong. Please re-login!");
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSPLogin.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LogIn(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext context, final UtilityHelper helper) throws FileNotFoundException {
        String Email = request.getParameter("Email").trim();
        String Pwd = request.getParameter("password").trim();
        String FacilityIdx = request.getParameter("FacilityIdx").trim();

        String database = "";
        boolean Not_found_DB = true;

        String Query = "";
        Statement stmt = null;
        ResultSet rset = null;

        try {
            Query = "Select dbname from oe.clients where Id='" + FacilityIdx + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                database = rset.getString(1);
                Not_found_DB = false;
            }
            rset.close();
            stmt.close();

            if (Not_found_DB) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("msg", "Please contact System Administrator!");
                Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSPLogin.html");
                return;
            }

        } catch (SQLException e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal Login *** MES#001 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "Login", conn);
            Services.DumException("SelfServicePortal", "LogIn", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "SelfServicePortal");
            Parser.SetField("ActionID", "exceptionMessaging");
            Parser.SetField("Message", "MES#001");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
        }

        String Query2 = "";
        Statement stmt2 = null;
        ResultSet rset2 = null;
        try {
            String encPwd = encrypt(Pwd);

            Query = "Select IFNULL(a.FirstName,'-'),a.Id,IFNULL(a.Email,'-'),IFNULL(a.Password,'-'),IFNULL(a.FacilityIdx,'-'),IFNULL(b.dbname,'-'), IFNULL(b.DirectoryName,'-'), IFNULL(b.menu,'-')" +
                    ", IFNULL(a.LastName,'-'), IFNULL(a.DOB,'-'), IFNULL(a.Zipcode,'-'), b.name  " +
                    " from " + database + ".PatientsCredentials a " +
                    "STRAIGHT_JOIN oe.clients b ON a.FacilityIdx = b.Id " +
                    " where TRIM(a.Email)= TRIM('" + Email.toUpperCase() + "') and TRIM(a.Password)=TRIM('" + encPwd + "')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            if (rset.next()) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FirstName", rset.getString(1));
                Parser.SetField("ptId", rset.getString(2));
                Parser.SetField("FacilityIdx", rset.getString(5));
                Parser.SetField("FacilityName", rset.getString(12));

                try {
                    Query2 = "Select MRN  from " + database + ".PatientReg  " +
                            " where FirstName='" + rset.getString(1) + "' and LastName='" + rset.getString(9) + "' and DOB='" + rset.getString(10) + "' \n" +
                            "and ZipCode='" + rset.getString(11) + "'";
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        Parser.SetField("MRN", rset2.getString(1));
                    }
                    rset2.close();
                    stmt2.close();
                } catch (SQLException e) {
                    helper.SendEmailWithAttachment("Error in Self Service Portal Login *** MES#002 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "Login", conn);
                    Services.DumException("SelfServicePortal", "LogIn", request, e, getServletContext());
                    Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "SelfServicePortal");
                    Parser.SetField("ActionID", "exceptionMessaging");
                    Parser.SetField("Message", "MES#002");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
                }

                Query = "UPDATE " + database + ".PatientsCredentials SET Password = '" + encPwd + "', isReset=0, ResetDateTime=NULL " +
                        "WHERE Id = " + rset.getString(2);
                (stmt = conn.createStatement()).executeUpdate(Query);
                rset.close();
                stmt.close();

                try {
                    Query = "Select Name, Email, PhNumber" +
                            " from oe.SelfServicePortal_Advocates  " +
                            " where FacilityIdx='" + FacilityIdx + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        Parser.SetField("Name", rset.getString(1));
                        Parser.SetField("AdEmail", rset.getString(2));
                        Parser.SetField("PhNumber", rset.getString(3));
                    }
                    rset.close();
                    stmt.close();
                } catch (SQLException e) {
                    helper.SendEmailWithAttachment("Error in Self Service Portal Login *** MES#003 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "Login", conn);
                    Services.DumException("SelfServicePortal", "LogIn", request, e, getServletContext());
                    Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "SelfServicePortal");
                    Parser.SetField("ActionID", "exceptionMessaging");
                    Parser.SetField("Message", "MES#003");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
                }
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "WS/index.html");
            } else {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("msg", "Invalid Password/Email");
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSPLogin.html");
            }

        } catch (SQLException e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal Login *** MES#004 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "Login", conn);
            Services.DumException("SelfServicePortal", "LogIn", request, e, getServletContext());
            Parsehtm parser = new Parsehtm(request);
            parser.SetField("FormName", "SelfServicePortal");
            parser.SetField("ActionID", "exceptionMessaging");
            parser.SetField("Message", "MES#004");
            parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal Login *** MES#005 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "Login", conn);
            Services.DumException("SelfServicePortal", "LogIn", request, e, getServletContext());
            Parsehtm parser = new Parsehtm(request);
            parser.SetField("FormName", "SelfServicePortal");
            parser.SetField("ActionID", "exceptionMessaging");
            parser.SetField("Message", "MES#005");
            parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

        }
    }

    private void LogInScreen(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final UtilityHelper helper) throws FileNotFoundException {
        try {
            String FacilityIdx = request.getParameter("FacilityIdx").trim();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "SSPLogin.html");
        } catch (FileNotFoundException e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#006 *** (Occurred At :  Customer Portal)", servletContext, e, "SelfServicePortal", "LogInScreen", conn);
            Services.DumException("SelfServicePortal", "LogInScreen", request, e, getServletContext());
            Parsehtm parser = new Parsehtm(request);
            parser.SetField("FormName", "SelfServicePortal");
            parser.SetField("ActionID", "exceptionMessaging");
            parser.SetField("Message", "MES#006");
            parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

        }
    }

    private void SignUp(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final UtilityHelper helper, SelfPaymentHelper paymentPortal) throws FileNotFoundException {
        try {
            int FacilityIdx = Integer.parseInt(request.getParameter("FacilityIdx").trim());

            //String FDetails[] = paymentPortal.getFacilityDetails(request, conn, servletContext, FacilityIdx);
            //paymentPortal.saveInitiateRequestPaymentPortal(request, conn, servletContext, "", 0, 0, 3, helper.getCurrDate(request, conn), "Generate from Within System", FDetails[0]);

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSP_SIGNUP.html");
        } catch (FileNotFoundException e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#007 *** (Occurred At :  Customer Portal)", servletContext, e, "SelfServicePortal", "SignUp", conn);
            Services.DumException("SelfServicePortal", "SignUp", request, e, getServletContext());
            Parsehtm parser = new Parsehtm(request);
            parser.SetField("FormName", "SelfServicePortal");
            parser.SetField("ActionID", "exceptionMessaging");
            parser.SetField("Message", "MES#007");
            parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

        }
    }

    private void ChangePwd(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext context, final UtilityHelper helper) throws FileNotFoundException {
        try {
            String Query = "";
            ResultSet rset = null;
            Statement stmt = null;
            boolean Not_found_DB = true;
            String database = "";

            String Opwd = request.getParameter("Opwd");
            String Pwd = request.getParameter("Pwd");
            String FacilityIdx = request.getParameter("FacilityIdx");
            String PtId = request.getParameter("PtId");
            String encOPwd = encrypt(Opwd);
            String encPwd = encrypt(Pwd);

            try {
                Query = "Select dbname from oe.clients  where Id='" + FacilityIdx + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    database = rset.getString(1);
                    Not_found_DB = false;
                }
                rset.close();
                stmt.close();

                if (Not_found_DB) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("msg", "DB not Found");
                    Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSPLogin.html");
                    return;
                }

            } catch (SQLException e) {
                helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#008 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "ChangePwd", conn);
                Services.DumException("SelfServicePortal", "ChangePwd", request, e, getServletContext());
                Parsehtm parser = new Parsehtm(request);
                parser.SetField("FormName", "SelfServicePortal");
                parser.SetField("ActionID", "exceptionMessaging");
                parser.SetField("Message", "MES#008");
                parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }

            try {
                Query = "SELECT Password from " + database + ".PatientsCredentials " +
                        " WHERE Id = " + PtId + " and FacilityIdx='" + FacilityIdx + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);

                if (rset.next()) {
                    if (rset.getString(1).equals(encOPwd)) {
                        rset.close();
                        stmt.close();

                        Query = "UPDATE " + database + ".PatientsCredentials SET Password = '" + encPwd + "' " +
                                " WHERE Id = " + PtId + " and FacilityIdx='" + FacilityIdx + "'";
                        (stmt = conn.createStatement()).executeUpdate(Query);
                        rset.close();
                        stmt.close();
                        out.println("1");
                        return;
                    } else {
                        out.println("0");
                        return;
                    }
                } else {
                    out.println("0");
                    return;
                }

            } catch (SQLException e) {
                helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#009 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "ChangePwd", conn);
                Services.DumException("SelfServicePortal", "ChangePwd", request, e, getServletContext());
                out.println("0~Oops Something went Wrong, Please Try Again");
            }
            out.println("1");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#010 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "ChangePwd", conn);
            Services.DumException("SelfServicePortal", "ChangePwd", request, e, getServletContext());
            Parsehtm parser = new Parsehtm(request);
            parser.SetField("FormName", "SelfServicePortal");
            parser.SetField("ActionID", "exceptionMessaging");
            parser.SetField("Message", "MES#010");
            parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
        }
    }

    private void saveChangePwd(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext context, final UtilityHelper helper) throws FileNotFoundException {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/ChangePwdForce_SSP.html");
        } catch (FileNotFoundException e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#011 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "saveChangePwd", conn);
            Services.DumException("SelfServicePortal", "saveChangePwd", request, e, getServletContext());
            Parsehtm parser = new Parsehtm(request);
            parser.SetField("FormName", "SelfServicePortal");
            parser.SetField("ActionID", "exceptionMessaging");
            parser.SetField("Message", "MES#011");
            parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
        }
    }

    private void SIGNUP_DATA(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext context, final UtilityHelper helper) throws FileNotFoundException {
        try {
            String Query = "";
            Statement stmt = null;
            ResultSet rset = null;
            int PtId = 0;
            String database = "";
            String Facility_Name = "";
            boolean Not_found_DB = true;
            boolean Not_found_RECORD = true;
            boolean found_EMAIL = false;
            boolean found_RECORD_IN_PATIENTCREDENTIALS = false;

            String FirstName = request.getParameter("FirstName").trim();
            String LastName = request.getParameter("LastName").trim();
            String Email = request.getParameter("Email").trim();
//            String Username=request.getParameter("Username").trim();
            String DateofBirth = request.getParameter("DateofBirth").trim();
            String ZipCode = request.getParameter("ZipCode").trim();
            String MRN = "";
            String FacilityIdx = request.getParameter("FacilityIdx").trim();

            //generate randomPassword
            //String randomPwd = generateRandomPassword(13);

            //String encryptedPwd = encrypt(randomPwd);
            try {
                Query = "Select dbname,FullName from oe.clients where Id='" + FacilityIdx + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    database = rset.getString(1);
                    Facility_Name = rset.getString(2);
                    Not_found_DB = false;
                }
                rset.close();
                stmt.close();

                if (Not_found_DB) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("msg", "DB not Found");
                    Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSP_SIGNUP.html");
                    return;
                }

            } catch (SQLException e) {
                helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#012 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "SIGNUP_DATA", conn);
                Services.DumException("SelfServicePortal", "SIGNUP_DATA", request, e, getServletContext());
                Parsehtm parser = new Parsehtm(request);
                parser.SetField("FormName", "SelfServicePortal");
                parser.SetField("ActionID", "exceptionMessaging");
                parser.SetField("Message", "MES#012");
                parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

            }

            try {
                Query = "Select FirstName,LastName,DOB,ZipCode,MRN from " + database + ".PatientReg" +
                        " where FirstName='" + FirstName.toUpperCase() + "' and LastName='" + LastName.toUpperCase() + "' and DOB='" + DateofBirth + "'" +
                        " and ZipCode='" + ZipCode + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    FirstName = rset.getString(1);
                    LastName = rset.getString(2);
                    DateofBirth = rset.getString(3);
                    ZipCode = rset.getString(4);
                    MRN = rset.getString(5);
                    Not_found_RECORD = false;
                }
                rset.close();
                stmt.close();

                if (Not_found_RECORD) {
//                    System.out.println("Query => "+Query);
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + database + ".PatientsCredentials_TRIED (FirstName,LastName,DOB ,ZipCode,Email,CreatedAt,Verified,FacilityIdx,isReset,PwdResetCount) " +
                                    " VALUES (?,?,?,?,?,now(),0,?,0,0) ");
                    MainReceipt.setString(1, FirstName.toUpperCase());
                    MainReceipt.setString(2, LastName.toUpperCase());
                    MainReceipt.setString(3, DateofBirth);
                    MainReceipt.setString(4, ZipCode);
                    MainReceipt.setString(5, Email.toUpperCase());
                    MainReceipt.setString(6, FacilityIdx);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();


                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("msg", "Your Credentials are not Listed <br> Please Try SigningUp Again!");
                    Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
                    Parser.SetField("FirstName", "");
                    Parser.SetField("LastName", "");
                    Parser.SetField("DOB", "");
                    Parser.SetField("ZipCode", "");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSP_SIGNUP.html");
                    return;
                }

            } catch (SQLException e) {
                helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#013 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "SIGNUP_DATA", conn);
                Services.DumException("SelfServicePortal", "SIGNUP_DATA", request, e, getServletContext());
                Parsehtm parser = new Parsehtm(request);
                parser.SetField("FormName", "SelfServicePortal");
                parser.SetField("ActionID", "exceptionMessaging");
                parser.SetField("Message", "MES#013");
                parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
                return;
            }

            try {
                Query = "Select FirstName,LastName,DOB,ZipCode from " + database + ".PatientsCredentials" +
                        " where FirstName='" + FirstName.toUpperCase() + "' and LastName='" + LastName.toUpperCase() + "' and DOB='" + DateofBirth + "'" +
                        " and ZipCode='" + ZipCode + "' and FacilityIdx='" + FacilityIdx + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    FirstName = rset.getString(1);
                    LastName = rset.getString(2);
                    DateofBirth = rset.getString(3);
                    ZipCode = rset.getString(4);
                    found_RECORD_IN_PATIENTCREDENTIALS = true;
                }
                rset.close();
                stmt.close();

                if (found_RECORD_IN_PATIENTCREDENTIALS) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("msg", "Your Credentials are already Listed <br> Please Try Signing-In!");
                    Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
                    Parser.SetField("FirstName", "");
                    Parser.SetField("LastName", "");
                    Parser.SetField("DOB", "");
                    Parser.SetField("ZipCode", "");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSPLogin.html");
                    return;
                }

            } catch (SQLException e) {
                helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#014 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "SIGNUP_DATA", conn);
                Services.DumException("SelfServicePortal", "SIGNUP_DATA", request, e, getServletContext());
                Parsehtm parser = new Parsehtm(request);
                parser.SetField("FormName", "SelfServicePortal");
                parser.SetField("ActionID", "exceptionMessaging");
                parser.SetField("Message", "MES#014");
                parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
                return;
            }


            try {
                Query = "Select Email from " + database + ".PatientsCredentials" +
                        " where Email='" + Email.toUpperCase() + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    Email = rset.getString(1);
                    found_EMAIL = true;
                }
                rset.close();
                stmt.close();

                if (found_EMAIL) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("email_error", "Email exists already! Please Try Another");
                    Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
                    Parser.SetField("FirstName", "");
                    Parser.SetField("LastName", "");
                    Parser.SetField("DOB", "");
                    Parser.SetField("ZipCode", "");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSP_SIGNUP.html");
                    return;
                }
            } catch (SQLException e) {
                helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#015 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "SIGNUP_DATA", conn);
                Services.DumException("SelfServicePortal", "SIGNUP_DATA", request, e, getServletContext());
                Parsehtm parser = new Parsehtm(request);
                parser.SetField("FormName", "SelfServicePortal");
                parser.SetField("ActionID", "exceptionMessaging");
                parser.SetField("Message", "MES#015");
                parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

            }


            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + database + ".PatientsCredentials (FirstName,LastName,DOB ,ZipCode,Email,CreatedAt,Verified,FacilityIdx,isReset,PwdResetCount) " +
                                " VALUES (?,?,?,?,?,now(),0,?,0,0) ");
                MainReceipt.setString(1, FirstName.toUpperCase());
                MainReceipt.setString(2, LastName.toUpperCase());
                MainReceipt.setString(3, DateofBirth);
                MainReceipt.setString(4, ZipCode);
                MainReceipt.setString(5, Email.toUpperCase());
                MainReceipt.setString(6, FacilityIdx);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (SQLException e) {
                helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#016 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "SIGNUP_DATA", conn);
                Services.DumException("SelfServicePortal", "SIGNUP_DATA", request, e, getServletContext());
                Parsehtm parser = new Parsehtm(request);
                parser.SetField("FormName", "SelfServicePortal");
                parser.SetField("ActionID", "exceptionMessaging");
                parser.SetField("Message", "MES#016");
                parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
                return;
            }

            Query = "Select Id from " + database + ".PatientsCredentials" +
                    " where Email='" + Email.toUpperCase() + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PtId = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            String ServerName = "";
            InetAddress ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();
            //front-rovermd-01 app1
            //dev-rover-01 dev1
            //front2 app
            switch (hostname) {
                case "front-rovermd-01":
                    ServerName = "app1";
                    break;
                case "dev-rover-01":
                    ServerName = "dev1";
                    break;
                case "front2.rovermd.com":
                    ServerName = "app";
                    break;
                case "appx-dev01":
                    ServerName = "appx";
                    break;
            }
            //send welcome Email
            String Body = "";
            String ConfirmationLink = "https://" + ServerName + ".rovermd.com:8443/md/md.SelfServicePortal?Action=8!@9$1$2*(5)64578&xy109uyt=" + URLEncoder.encode(encrypt(FacilityIdx).replaceAll("=", ""), StandardCharsets.UTF_8.toString()) + "&pt76yui=" + URLEncoder.encode(encrypt(String.valueOf(PtId)).replaceAll("=", ""), StandardCharsets.UTF_8.toString());
            Body = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<title></title>\n" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                    "<style type=\"text/css\">\n" +
                    "    /* FONTS */\n" +
                    "    @import url('https://fonts.googleapis.com/css?family=Poppins:100,100i,200,200i,300,300i,400,400i,500,500i,600,600i,700,700i,800,800i,900,900i');\n" +
                    "\n" +
                    "    /* CLIENT-SPECIFIC STYLES */\n" +
                    "    body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }\n" +
                    "    table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }\n" +
                    "    img { -ms-interpolation-mode: bicubic; }\n" +
                    "\n" +
                    "    /* RESET STYLES */\n" +
                    "    img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; }\n" +
                    "    table { border-collapse: collapse !important; }\n" +
                    "    body { height: 100% !important; margin: 0 !important; padding: 0 !important; width: 100% !important; }\n" +
                    "\n" +
                    "    /* iOS BLUE LINKS */\n" +
                    "    a[x-apple-data-detectors] {\n" +
                    "        color: inherit !important;\n" +
                    "        text-decoration: none !important;\n" +
                    "        font-size: inherit !important;\n" +
                    "        font-family: inherit !important;\n" +
                    "        font-weight: inherit !important;\n" +
                    "        line-height: inherit !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    /* MOBILE STYLES */\n" +
                    "    @media screen and (max-width:600px){\n" +
                    "        h1 {\n" +
                    "            font-size: 32px !important;\n" +
                    "            line-height: 32px !important;\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    /* ANDROID CENTER FIX */\n" +
                    "    div[style*=\"margin: 16px 0;\"] { margin: 0 !important; }\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body style=\"background-color: #f3f5f7; margin: 0 !important; padding: 0 !important;\">\n" +
                    "\n" +
                    "<!-- HIDDEN PREHEADER TEXT -->\n" +
                    "<div style=\"display: none; font-size: 1px; color: #fefefe; line-height: 1px; font-family: 'Poppins', sans-serif; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden;\">\n" +
                    "    We're thrilled to have you here! Get ready to dive into your new account.\n" +
                    "</div>\n" +
                    "\n" +
                    "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                    "    <!-- LOGO -->\n" +
                    "    <tr>\n" +
                    "        <td align=\"center\">\n" +
                    "            <!--[if (gte mso 9)|(IE)]>\n" +
                    "            <table align=\"center\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n" +
                    "            <tr>\n" +
                    "            <td align=\"center\" valign=\"top\" width=\"600\">\n" +
                    "            <![endif]-->\n" +
                    "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                    "                <tr>\n" +
                    "                    <td align=\"center\" valign=\"top\" style=\"padding: 40px 10px 10px 10px;\">\n" +
                    "                        <a href=\"#\" target=\"_blank\" style=\"text-decoration: none;\">\n" +
                    "\t\t\t\t\t\t\t<img src=\"https://" + ServerName + ".rovermd.com:8443/md/images_/" + FacilityIdx + ".png\" alt=\"\">\n" +
                    "                        </a>\n" +
                    "                    </td>\n" +
                    "                </tr>\n" +
                    "            </table>\n" +
                    "            <!--[if (gte mso 9)|(IE)]>\n" +
                    "            </td>\n" +
                    "            </tr>\n" +
                    "            </table>\n" +
                    "            <![endif]-->\n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "    <!-- HERO -->\n" +
                    "    <tr>\n" +
                    "        <td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">\n" +
                    "            <!--[if (gte mso 9)|(IE)]>\n" +
                    "            <table align=\"center\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n" +
                    "            <tr>\n" +
                    "            <td align=\"center\" valign=\"top\" width=\"600\">\n" +
                    "            <![endif]-->\n" +
                    "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                    "<tr>\n" +
                    "                 \n" +
                    "                    <td bgcolor=\"#f1cd4c\" align=\"center\" valign=\"top\" style=\"padding: 40px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #111111; font-family: 'Poppins', sans-serif; font-size: 48px; font-weight: 400; letter-spacing: 2px; line-height: 48px;\">\n" +
                    "                      <h1 style=\"font-size: 36px; font-weight: 600; margin: 0;\">" + Facility_Name + "</h1>\n" +
                    "                    </td>\n" +
                    "                </tr>                " +
                    "<tr>\n" +
                    "                    <td bgcolor=\"#ffffff\" align=\"center\" valign=\"top\" style=\"padding: 40px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #111111; font-family: 'Poppins', sans-serif; font-size: 48px; font-weight: 400; letter-spacing: 2px; line-height: 48px;\">\n" +
                    "                      <h1 style=\"font-size: 20px; font-weight: 600; margin: 0;\">Hi! " + LastName + ", " + FirstName + "</h1>\n" +
                    "                    </td>\n" +
                    "                </tr>\n" +
                    "            </table>\n" +
                    "            <!--[if (gte mso 9)|(IE)]>\n" +
                    "            </td>\n" +
                    "            </tr>\n" +
                    "            </table>\n" +
                    "            <![endif]-->\n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "    <!-- COPY BLOCK -->\n" +
                    "    <tr>\n" +
                    "        <td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">\n" +
                    "            <!--[if (gte mso 9)|(IE)]>\n" +
                    "            <table align=\"center\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n" +
                    "            <tr>\n" +
                    "            <td align=\"center\" valign=\"top\" width=\"600\">\n" +
                    "            <![endif]-->\n" +
                    "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                    "              <!-- COPY -->\n" +
                    "              <tr>\n" +
                    "                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 20px 30px 20px 30px; color: #666666; font-family: 'Poppins', sans-serif; font-size: 16px; font-weight: 400; line-height: 25px;\">\n" +
                    "                  <p style=\"margin: 0;\">We're excited to have you get started. First, you need to confirm your account. Just press the button below.</p>\n" +
                    "                </td>\n" +
                    "              </tr>\n" +
                    "              <!-- BULLETPROOF BUTTON -->\n" +
                    "              <tr>\n" +
                    "                <td bgcolor=\"#ffffff\" align=\"left\">\n" +
                    "                  <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                    "                    <tr>\n" +
                    "                      <td bgcolor=\"#ffffff\" align=\"center\" style=\"padding: 20px 30px 30px 30px;\">\n" +
                    "                        <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                    "                          <tr>\n" +
                    "                              <td align=\"center\" style=\"border-radius: 3px;\" bgcolor=\"#17b3a3\"><a href=\"" + ConfirmationLink + "\"  target=\"_blank\" style=\"font-size: 18px; font-family: Helvetica, Arial, sans-serif; color: #ffffff; text-decoration: none; color: #ffffff; text-decoration: none; padding: 12px 50px; border-radius: 2px; border: 1px solid #17b3a3; display: inline-block;\">Confirm Account</a></td>\n" +
                    "                          </tr>\n" +
                    "                        </table>\n" +
                    "                      </td>\n" +
                    "                    </tr>\n" +
                    "                  </table>\n" +
                    "                </td>\n" +
                    "              </tr>\n" +
                    "              <!-- COPY -->\n" +
                    "              <tr>\n" +
                    "                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 20px 30px; color: #666666; font-family: &apos;Lato&apos;, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 25px;\">\n" +
                    "                  <p style=\"margin: 0;\">If you have any questions, just reply to this email—we're always happy to help out.</p>\n" +
                    "                </td>\n" +
                    "              </tr>\n" +
                    "              <!-- COPY -->\n" +
                    "              <tr>\n" +
                    "                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 40px 30px; border-radius: 0px 0px 0px 0px; color: #666666; font-family: 'Poppins', sans-serif; font-size: 14px; font-weight: 400; line-height: 25px;\">\n" +
                    "                  <p style=\"margin: 0;\">Cheers,<br>Team</p>\n" +
                    "                </td>\n" +
                    "              </tr>\n" +
                    "            </table>\n" +
                    "            <!--[if (gte mso 9)|(IE)]>\n" +
                    "            </td>\n" +
                    "            </tr>\n" +
                    "            </table>\n" +
                    "            <![endif]-->\n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "    <!-- FOOTER -->\n" +
                    "    <tr>\n" +
                    "        <td align=\"center\" style=\"padding: 10px 10px 50px 10px;\">\n" +
                    "            <!--[if (gte mso 9)|(IE)]>\n" +
                    "            <table align=\"center\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n" +
                    "            <tr>\n" +
                    "            <td align=\"center\" valign=\"top\" width=\"600\">\n" +
                    "            <![endif]-->\n" +
                    "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                    "\t\t      <!-- COPYRIGHT -->\n" +
                    "              <tr>\n" +
                    "                <td align=\"center\" style=\"padding: 30px 30px 30px 30px; color: #333333; font-family: 'Poppins', sans-serif; font-size: 12px; font-weight: 400; line-height: 18px;\">\n" +
                    "                  <p style=\"margin: 0;\">All Right Reserved. Copyrights &#169; R O V E R</p>\n" +
                    "                </td>\n" +
                    "              </tr>\n" +
                    "            </table>\n" +
                    "            <!--[if (gte mso 9)|(IE)]>\n" +
                    "            </td>\n" +
                    "            </tr>\n" +
                    "            </table>\n" +
                    "            <![endif]-->\n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "</table>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>";
            helper.SendEmail("", "Greetings from Self Service Portal", Body, Email, conn, context);


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("msg", "Sign-Up successful! <br> We've sent you a Verification Email, <br> Please Check Your inbox");
            Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSPLogin.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#017 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "SIGNUP_DATA", conn);
            Services.DumException("SelfServicePortal", "SIGNUP_DATA", request, e, getServletContext());
            Parsehtm parser = new Parsehtm(request);
            parser.SetField("FormName", "SelfServicePortal");
            parser.SetField("ActionID", "exceptionMessaging");
            parser.SetField("Message", "MES#017");
            parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
        }
    }

    private void PwdChangeScreen(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext context, final UtilityHelper helper) throws FileNotFoundException {
        try {
            String Query = "";
            Statement stmt = null;
            ResultSet rset = null;
            String database = null;

            boolean Not_found_DB = true;

            String FacilityIdx = request.getParameter("xy109uyt").trim();
            String PtID = request.getParameter("pt76yui").trim();


            //this is because browser is taking = as send post and we need to add == to make the String proper for dycryption
            FacilityIdx = decrypt(FacilityIdx + "==");
            PtID = decrypt(PtID + "==");

            try {
                Query = "Select dbname from oe.clients where Id='" + FacilityIdx + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    database = rset.getString(1);
                    Not_found_DB = false;
                }
                rset.close();
                stmt.close();

                if (Not_found_DB) {
//                    Parsehtm Parser = new Parsehtm(request);
//                    Parser.SetField("msg", "DB not Found");
//                    Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
//                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ChangePwd.html");
                    return;
                }

            } catch (SQLException e) {
                e.printStackTrace();
//                out.println(e.getMessage());
            }
            try {
                Query = "Select Verified from " + database + ".PatientsCredentials " +
                        " where Id='" + PtID + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    if (rset.getString(1).equals("1")) {
                        Parsehtm Parser = new Parsehtm(request);
                        Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "expiredLink.html");
                        return;
                    }
                }
                rset.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                out.println(e.getMessage());
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
            Parser.SetField("xyz", String.valueOf(PtID));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ChangePwd.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#018 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "PwdChangeScreen", conn);
            Services.DumException("SelfServicePortal", "PwdChangeScreen", request, e, getServletContext());
            Parsehtm parser = new Parsehtm(request);
            parser.SetField("FormName", "SelfServicePortal");
            parser.SetField("ActionID", "exceptionMessaging");
            parser.SetField("Message", "MES#018");
            parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

        }
    }

    private void SavePwd(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext context, final UtilityHelper helper) {
        try {
            String Query = "";
            Statement stmt = null;
            ResultSet rset = null;

            String Password = request.getParameter("Password").trim();
            String FacilityIdx = request.getParameter("FacilityIdx").trim();
            String ID = request.getParameter("funky").trim();

            String database = "";
            boolean Not_found_DB = true;

            try {
                Query = "Select dbname from oe.clients  where Id='" + FacilityIdx + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    database = rset.getString(1);
                    Not_found_DB = false;
                }
                rset.close();
                stmt.close();

                if (Not_found_DB) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("msg", "DB not Found");
                    Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ChangePwd.html");
                    return;
                }

            } catch (SQLException e) {
                helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#019 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "SavePwd", conn);
                Services.DumException("SelfServicePortal", "SavePwd", request, e, getServletContext());
                Parsehtm parser = new Parsehtm(request);
                parser.SetField("FormName", "SelfServicePortal");
                parser.SetField("ActionID", "exceptionMessaging");
                parser.SetField("Message", "MES#019");
                parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
                return;
            }

            Password = encrypt(Password);
            try {
                Query = "UPDATE " + database + ".PatientsCredentials SET Password = '" + Password + "' , Verified = 1 WHERE Id = " + ID + " and Verified=0";
                (stmt = conn.createStatement()).executeUpdate(Query);
                rset.close();
                stmt.close();
            } catch (SQLException e) {
                helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#020 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "SavePwd", conn);
                Services.DumException("SelfServicePortal", "SavePwd", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("msg", "Oops! password is not updated. <br> Please Try Again!");
                Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
                Parser.SetField("xyz", String.valueOf(ID));
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ChangePwd.html");
                return;
            }


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("msg", "Password Changed Successfully <br> Now you can Sign-In!");
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSPLogin.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Logout(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext context, final UtilityHelper helper) throws FileNotFoundException {
        try {
            String FacilityIdx = request.getParameter("FacilityIdx").trim();

            final HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("msg", "You are successfully loggedOut!");
            Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSPLogin.html");
        } catch (FileNotFoundException e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal *** MES#021 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "Logout", conn);
            Services.DumException("SelfServicePortal", "Logout", request, e, getServletContext());
            Parsehtm parser = new Parsehtm(request);
            parser.SetField("FormName", "SelfServicePortal");
            parser.SetField("ActionID", "exceptionMessaging");
            parser.SetField("Message", "MES#021");
            parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

        }
    }

    private void RecoverPwd(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext context, final UtilityHelper helper) throws FileNotFoundException {
        try {
            String FacilityIdx = request.getParameter("FacilityIdx").trim();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSP_recoverPassword.html");
        } catch (FileNotFoundException e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal RecoverPwd *** MES#022 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "RecoverPwd", conn);
            Services.DumException("SelfServicePortal", "RecoverPwd", request, e, getServletContext());
            Parsehtm parser = new Parsehtm(request);
            parser.SetField("FormName", "SelfServicePortal");
            parser.SetField("ActionID", "exceptionMessaging");
            parser.SetField("Message", "MES#022");
            parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

        }
    }

    private void RecoverPwd_Data(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext context, final UtilityHelper helper) throws FileNotFoundException {
        String msg = "";
        String Query = "";
        Statement stmt = null;
        ResultSet rset = null;
        try {
            String Email = request.getParameter("Email").trim();
            String FacilityIdx = request.getParameter("FacilityIdx").trim();
            int PtId = 0;
            String database = null;
            String Facility_Name = null;

            try {
                Query = "Select dbname,FullName from oe.clients  where Id='" + FacilityIdx + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    database = rset.getString(1);
                    Facility_Name = rset.getString(2);
                }
                rset.close();
                stmt.close();
            } catch (SQLException e) {
                helper.SendEmailWithAttachment("Error in Self Service Portal RecoverPwd_Data *** MES#023 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "RecoverPwd_Data", conn);
                Services.DumException("SelfServicePortal", "RecoverPwd_Data", request, e, getServletContext());
                Parsehtm parser = new Parsehtm(request);
                parser.SetField("FormName", "SelfServicePortal");
                parser.SetField("ActionID", "exceptionMessaging");
                parser.SetField("Message", "MES#023");
                parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

            }

            try {
                Query = "Select Email, isReset, ResetDateTime, Id from " + database + ".PatientsCredentials " +
                        " where Email='" + Email + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    Email = rset.getString(1);
                    PtId = rset.getInt(4);
                    if (rset.getInt(2) == 1 && Is30minutes_NOT_Crossed(PtId, conn, out, database)) {
                        rset.close();
                        stmt.close();

                        msg = "We've sent you an Email Already , Please Check your inbox!";
                        Parsehtm Parser = new Parsehtm(request);
                        Parser.SetField("msg", String.valueOf(msg));
                        Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSP_recoverPassword.html");
                        return;
                    }
                    msg = "We've sent you an Email , Please Check!";
                    rset.close();
                    stmt.close();

                    String ServerName = "";
                    InetAddress ip = InetAddress.getLocalHost();
                    String hostname = ip.getHostName();


                    //front-rovermd-01 app1
                    //dev-rover-01 dev1
                    //front2 app
                    switch (hostname) {
                        case "front-rovermd-01":
                            ServerName = "app1";
                            break;
                        case "dev-rover-01":
                            ServerName = "dev1";
                            break;
                        case "front2.rovermd.com":
                            ServerName = "app";
                            break;
                        case "appx-dev01":
                            ServerName = "appx";
                            break;
                    }


                    String link = "https://" + ServerName + ".rovermd.com:8443/md/md.SelfServicePortal?Action=9Ey_s59)8sM^upsuUh*D&xy109uyt=" + URLEncoder.encode(encrypt(FacilityIdx).replaceAll("=", ""), StandardCharsets.UTF_8.toString()) + "&pt76yui=" + URLEncoder.encode(encrypt(String.valueOf(PtId)).replaceAll("=", ""), StandardCharsets.UTF_8.toString());
                    //send email
                    String Body = "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<head>\n" +
                            "<title></title>\n" +
                            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                            "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                            "<style type=\"text/css\">\n" +
                            "    /* FONTS */\n" +
                            "    @import url('https://fonts.googleapis.com/css?family=Poppins:100,100i,200,200i,300,300i,400,400i,500,500i,600,600i,700,700i,800,800i,900,900i');\n" +
                            "\n" +
                            "    /* CLIENT-SPECIFIC STYLES */\n" +
                            "    body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }\n" +
                            "    table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }\n" +
                            "    img { -ms-interpolation-mode: bicubic; }\n" +
                            "\n" +
                            "    /* RESET STYLES */\n" +
                            "    img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; }\n" +
                            "    table { border-collapse: collapse !important; }\n" +
                            "    body { height: 100% !important; margin: 0 !important; padding: 0 !important; width: 100% !important; }\n" +
                            "\n" +
                            "    /* iOS BLUE LINKS */\n" +
                            "    a[x-apple-data-detectors] {\n" +
                            "        color: inherit !important;\n" +
                            "        text-decoration: none !important;\n" +
                            "        font-size: inherit !important;\n" +
                            "        font-family: inherit !important;\n" +
                            "        font-weight: inherit !important;\n" +
                            "        line-height: inherit !important;\n" +
                            "    }\n" +
                            "\n" +
                            "    /* MOBILE STYLES */\n" +
                            "    @media screen and (max-width:600px){\n" +
                            "        h1 {\n" +
                            "            font-size: 32px !important;\n" +
                            "            line-height: 32px !important;\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    /* ANDROID CENTER FIX */\n" +
                            "    div[style*=\"margin: 16px 0;\"] { margin: 0 !important; }\n" +
                            "</style>\n" +
                            "</head>\n" +
                            "<body style=\"background-color: #f3f5f7; margin: 0 !important; padding: 0 !important;\">\n" +
                            "\n" +
                            "<!-- HIDDEN PREHEADER TEXT -->\n" +
                            "<div style=\"display: none; font-size: 1px; color: #fefefe; line-height: 1px; font-family: 'Poppins', sans-serif; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden;\">\n" +
                            "    We're thrilled to have you here! Get ready to dive into your new account.\n" +
                            "</div>\n" +
                            "\n" +
                            "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                            "    <!-- LOGO -->\n" +
                            "    <tr>\n" +
                            "        <td align=\"center\">\n" +
                            "            \n" +
                            "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                            "                <tr>\n" +
                            "                    <td align=\"center\" valign=\"top\" style=\"padding: 40px 10px 10px 10px;\">\n" +
                            "                        <a href=\"#\" target=\"_blank\" style=\"text-decoration: none;\">\n" +
                            "\t\t\t\t\t\t\t<img src=\"https://" + ServerName + ".rovermd.com:8443/md/images_/" + FacilityIdx + ".png\" alt=\"\">\n" +
                            "                        </a>\n" +
                            "                    </td>\n" +
                            "                </tr>\n" +
                            "            </table>\n" +
                            "         \n" +
                            "        </td>\n" +
                            "    </tr>\n" +
                            "    <!-- HERO -->\n" +
                            "    <tr>\n" +
                            "        <td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">\n" +
                            "           \n" +
                            "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                            "           <tr>\n" +
                            "                 \n" +
                            "                    <td bgcolor=\"#f1cd4c\" align=\"center\" valign=\"top\" style=\"padding: 40px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #111111; font-family: 'Poppins', sans-serif; font-size: 48px; font-weight: 400; letter-spacing: 2px; line-height: 48px;\">\n" +
                            "                      <h1 style=\"font-size: 30px; font-weight: 600; margin: 0;\">" + Facility_Name + "</h1>\n" +
                            "                    </td>\n" +
                            "                </tr>     " +
                            "                <tr>\n" +
                            "                    <td bgcolor=\"#ffffff\" align=\"center\" valign=\"top\" style=\"padding: 40px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #ff4c52; font-family: 'Poppins', sans-serif; font-size: 48px; font-weight: 400; letter-spacing: 2px; line-height: 48px;\">\n" +
                            "                      <h1 style=\"font-size: 36px; font-weight: 600; margin: 0;\">Trouble signing in?</h1>\n" +
                            "                    </td>\n" +
                            "                </tr>\n" +
                            "            </table>\n" +
                            "        \n" +
                            "        </td>\n" +
                            "    </tr>\n" +
                            "    <!-- COPY BLOCK -->\n" +
                            "    <tr>\n" +
                            "        <td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">\n" +
                            "          \n" +
                            "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                            "              <!-- COPY -->\n" +
                            "              <tr>\n" +
                            "                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 20px 30px 40px 30px; color: #666666; font-family: 'Poppins', sans-serif; font-size: 16px; font-weight: 400; line-height: 25px;\">\n" +
                            "                  <p style=\"margin: 0;\">There is a request to change your password. Resetting your password is easy. Just press the button below and follow the instructions. We'll have you up and running in no time.</p>\n" +
                            "                </td>\n" +
                            "              </tr>\n" +
                            "              <!-- BULLETPROOF BUTTON -->\n" +
                            "              <tr>\n" +
                            "                <td bgcolor=\"#ffffff\" align=\"left\">\n" +
                            "                  <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                            "                    <tr>\n" +
                            "                      <td bgcolor=\"#ffffff\" align=\"center\" style=\"padding: 20px 30px 60px 30px;\">\n" +
                            "                        <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                            "                          <tr>\n" +
                            "                              <td align=\"center\" style=\"border-radius: 3px;\" bgcolor=\"#ff4c52\"><a href=" + link + " target=\"_blank\" style=\"font-size: 18px; font-family: Helvetica, Arial, sans-serif; color: #ffffff; text-decoration: none; color: #ffffff; text-decoration: none; padding: 12px 50px; border-radius: 2px; border: 1px solid #ff4c52; display: inline-block;\">Reset Password</a></td>\n" +
                            "                          </tr>\n" +
                            "                        </table>\n" +
                            "                      </td>\n" +
                            "                    </tr>\n" +
                            "                  </table>\n" +
                            "                </td>\n" +
                            "              </tr>\n" +
                            "              <!-- COPY -->\n" +
                            "              <tr>\n" +
                            "                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 20px 30px; color: #aaaaaa; font-family: &apos;Lato&apos;, Helvetica, Arial, sans-serif; font-size: 13px; font-weight: 400; line-height: 25px;\">\n" +
                            "                  <p style=\"margin: 0; text-align: center;\">If you did not make this request, just ignore this email. Otherwise, pleas click button above to change your password.</p>\n" +
                            "                </td>\n" +
                            "              </tr>\n" +
                            "              <!-- COPY -->\n" +
                            "              <tr>\n" +
                            "                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 40px 30px; border-radius: 0px 0px 0px 0px; color: #666666; font-family: 'Poppins', sans-serif; font-size: 14px; font-weight: 400; line-height: 25px;\">\n" +
                            "                  <p style=\"margin: 0;\">Cheers,<br>Team</p>\n" +
                            "                </td>\n" +
                            "              </tr>\n" +
                            "            </table>\n" +
                            "          \n" +
                            "        </td>\n" +
                            "    </tr>\n" +
                            "    <!-- FOOTER -->\n" +
                            "    <tr>\n" +
                            "        <td align=\"center\" style=\"padding: 10px 10px 50px 10px;\">\n" +
                            "         \n" +
                            "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                            "              \n" +
                            "              <tr>\n" +
                            "                <td align=\"center\" style=\"padding: 30px 30px 30px 30px; color: #333333; font-family: 'Poppins', sans-serif; font-size: 12px; font-weight: 400; line-height: 18px;\">\n" +
                            "                  <p style=\"margin: 0;\">All Right Reserved. Copyrights &#169; R O V E R.</p>\n" +
                            "                </td>\n" +
                            "              </tr>\n" +
                            "            </table>\n" +
                            "           \n" +
                            "        </td>\n" +
                            "    </tr>\n" +
                            "</table>\n" +
                            "\n" +
                            "</body>\n" +
                            "</html>\n";

                    helper.SendEmail("", "Password Reset", Body, Email, conn, context);
                    Query = "UPDATE " + database + ".PatientsCredentials SET isReset = 1 , ResetDateTime=NOW() WHERE Id = " + PtId;
                    (stmt = conn.createStatement()).executeUpdate(Query);
                    rset.close();
                    stmt.close();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("msg", String.valueOf(msg));
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSP_recoverPassword.html");
                    return;
                } else {
                    msg = "Email is not Associated with any of our User, Please Try Different one";
                }
                rset.close();
                stmt.close();
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("msg", String.valueOf(msg));
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSP_recoverPassword.html");
            } catch (SQLException e) {
                helper.SendEmailWithAttachment("Error in Self Service Portal RecoverPwd_Data *** MES#024 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "RecoverPwd_Data", conn);
                Services.DumException("SelfServicePortal", "RecoverPwd_Data", request, e, getServletContext());
                Parsehtm parser = new Parsehtm(request);
                parser.SetField("FormName", "SelfServicePortal");
                parser.SetField("ActionID", "exceptionMessaging");
                parser.SetField("Message", "MES#024");
                parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal RecoverPwd_Data *** MES#025 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "RecoverPwd_Data", conn);
            Services.DumException("SelfServicePortal", "RecoverPwd_Data", request, e, getServletContext());
            Parsehtm parser = new Parsehtm(request);
            parser.SetField("FormName", "SelfServicePortal");
            parser.SetField("ActionID", "exceptionMessaging");
            parser.SetField("Message", "MES#025");
            parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

        }
    }

    private void PwdChangeScreen_Reset(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext context, final UtilityHelper helper) throws FileNotFoundException {
        try {
            String Query = "";
            Statement stmt = null;
            ResultSet rset = null;
            String database = null;
            String isReset = null;

            boolean Not_found_DB = true;

            String FacilityIdx = request.getParameter("xy109uyt").trim();
            String PtID = request.getParameter("pt76yui").trim();

            //this is becuse browser is taking = as send post and we need to add == to make the String proper for dycryption
            FacilityIdx = decrypt(FacilityIdx + "==");
            PtID = decrypt(PtID + "==");


            try {
                Query = "Select dbname from oe.clients" +
                        " where Id='" + FacilityIdx + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    database = rset.getString(1);
                    Not_found_DB = false;
                }
                rset.close();
                stmt.close();

                if (Not_found_DB) {
//                    Parsehtm Parser = new Parsehtm(request);
//                    Parser.SetField("msg", "DB not Found");
//                    Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
//                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ChangePwd.html");
                    return;
                }

            } catch (SQLException e) {
                helper.SendEmailWithAttachment("Error in Self Service Portal PwdChangeScreen_Reset *** MES#026 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "PwdChangeScreen_Reset", conn);
                Services.DumException("SelfServicePortal", "PwdChangeScreen_Reset", request, e, getServletContext());
                Parsehtm parser = new Parsehtm(request);
                parser.SetField("FormName", "SelfServicePortal");
                parser.SetField("ActionID", "exceptionMessaging");
                parser.SetField("Message", "MES#026");
                parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

            }

            Query = "Select isReset from " + database + ".PatientsCredentials where Id='" + PtID + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                isReset = rset.getString(1);
            }
            stmt.close();
            rset.close();


            if (isReset.equals("0") || (Is30minutes_NOT_Crossed(Integer.parseInt(PtID), conn, out, database) == false)) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "expiredLink.html");
                return;
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
            Parser.SetField("xyz", String.valueOf(PtID));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ChangePwd_Reset.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal PwdChangeScreen_Reset *** MES#027 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "PwdChangeScreen_Reset", conn);
            Services.DumException("SelfServicePortal", "PwdChangeScreen_Reset", request, e, getServletContext());
            Parsehtm parser = new Parsehtm(request);
            parser.SetField("FormName", "SelfServicePortal");
            parser.SetField("ActionID", "exceptionMessaging");
            parser.SetField("Message", "MES#027");
            parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

        }
    }

    private void RecoverPwd_Save(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext context, final UtilityHelper helper) throws FileNotFoundException {
        try {
            String Query = "";
            Statement stmt = null;
            ResultSet rset = null;

            String Password = request.getParameter("Password").trim();
            String FacilityIdx = request.getParameter("FacilityIdx").trim();
            String ID = request.getParameter("funky").trim();


            String database = "";
            boolean Not_found_DB = true;


            try {
                Query = "Select dbname from oe.clients  where Id='" + FacilityIdx + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    database = rset.getString(1);
                    Not_found_DB = false;
                }
                rset.close();
                stmt.close();

                if (Not_found_DB) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("msg", "DB not Found");
                    Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ChangePwd_Reset.html");
                    return;
                }

            } catch (SQLException e) {
                helper.SendEmailWithAttachment("Error in Self Service Portal RecoverPwd_Save *** MES#028 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "RecoverPwd_Save", conn);
                Services.DumException("SelfServicePortal", "RecoverPwd_Save", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("msg", e.getMessage());
                Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
                Parser.SetField("xyz", String.valueOf(ID));
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ChangePwd_Reset.html");
                return;
            }

            Password = encrypt(Password);
            try {
                Query = "UPDATE " + database + ".PatientsCredentials SET Password = '" + Password + "', isReset=0, ResetDateTime=NULL, PwdResetCount=PwdResetCount+1 " +
                        "WHERE Id = " + ID;
                (stmt = conn.createStatement()).executeUpdate(Query);
                rset.close();
                stmt.close();


            } catch (SQLException e) {
                e.printStackTrace();
                out.println(e.getMessage());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("msg", "Oops! password is not updated. <br> Please Try Again!");
                Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
                Parser.SetField("xyz", String.valueOf(ID));
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ChangePwd_Reset.html");
                return;
            }


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("msg", "Password Changed Successfully <br> Now you can Sign-In!");
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SSPLogin.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal RecoverPwd_Save *** MES#029 *** (Occurred At :  Customer Portal)", context, e, "SelfServicePortal", "RecoverPwd_Save", conn);
            Services.DumException("SelfServicePortal", "RecoverPwd_Save", request, e, getServletContext());
            Parsehtm parser = new Parsehtm(request);
            parser.SetField("FormName", "SelfServicePortal");
            parser.SetField("ActionID", "exceptionMessaging");
            parser.SetField("Message", "MES#029");
            parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

        }
    }

    public String generateRandomPassword(int len) {
        // ASCII range – alphanumeric (0-9, a-z, A-Z)
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        // each iteration of the loop randomly chooses a character from the given
        // ASCII range and appends it to the `StringBuilder` instance

        for (int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }

    void CollectPayment_View(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, UtilityHelper helper) throws FileNotFoundException {
        stmt = null;
        rset = null;
        Statement stmt2 = null;
        ResultSet rset2 = null;
        Query = "";
        String Query2 = "";
        double TotalAmount = 0.0D;
        double PaidAmount = 0.0D;
        int InstallmentPlanFound = 0;
        String PatientName = "";
        String PatientInvoiceMRN = request.getParameter("MRN");
        String PtId = request.getParameter("PtId");

        StringBuilder CDRList = new StringBuilder();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        String FacilityIdx = request.getParameter("FacilityIdx").trim();
        String database = null;

        try {
            Query = "Select dbname from oe.clients  where Id='" + FacilityIdx + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                database = rset.getString(1);
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            helper.SendEmailWithAttachment("Error in Self Service Portal CollectPayment_View *** MES#030 *** (Occurred At :  Customer Portal)", servletContext, e, "SelfServicePortal", "CollectPayment_View", conn);
            Services.DumException("SelfServicePortal", "CollectPayment_View", request, e, getServletContext());
            Parsehtm parser = new Parsehtm(request);
            parser.SetField("FormName", "SelfServicePortal");
            parser.SetField("ActionID", "exceptionMessaging");
            parser.SetField("Message", "MES#030");
            parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }

        try {
            Query = " SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,'-'),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')),DATE_FORMAT(b.DOB,'%d-%m-%Y')," +
                    "IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')) AS DOS,  " +
                    "a.InvoiceNo, DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T'), a.TotalAmount, a.PaidAmount, a.BalAmount, a.Id   " +
                    " FROM " + database + ".InvoiceMaster a   " +
                    " LEFT JOIN " + database + ".PatientReg b ON a.PatientMRN = b.MRN    " +
                    " WHERE a.PatientMRN = '" + PatientInvoiceMRN + "' AND a.Status = 0 AND b.Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Query2 = "Select COUNT(*) FROM " + database + ".InstallmentPlan WHERE MRN = '" + rset.getString(1) + "' AND " +
                        " InvoiceNo = '" + rset.getString(5) + "'";
                stmt2 = conn.createStatement();
                rset2 = stmt2.executeQuery(Query2);
                if (rset2.next())
                    InstallmentPlanFound = rset2.getInt(1);
                rset2.close();
                stmt2.close();
                TotalAmount = rset.getDouble(7);
                PaidAmount = rset.getDouble(7) - rset.getDouble(9);
                PatientName = rset.getString(2);
                CDRList.append("<td align=left > ");
                CDRList.append("<a class='btn'  onclick=\"window.open(\'/md/md.SelfServicePortal?Action=InvoicePdf&PatientMRN=" + rset.getString(1) + "&InvoiceNo=" + rset.getString(5) + "&FacilityIdx=" + FacilityIdx + "\')\">View</a> <br> <br>\n");
                if (rset.getDouble(9) == 0.00D)
                    CDRList.append("<b><font color='red'>PAID</font></b>\n");
                else
                    CDRList.append("<a class='btn' onclick=\"OpenModal(\'/md/md.SelfServicePortal?Action=PayNow&InvoiceNo=" + rset.getString(5) + "&FacilityIdx=" + FacilityIdx + "&PtId=" + PtId + "\')\">PayNow</a>\n");
                CDRList.append("</td>");
                CDRList.append("<td align=left>" + numFormat.format(TotalAmount) + "</td>\n");//Total Amount
                CDRList.append("<td align=left>" + numFormat.format(PaidAmount) + "</td>\n");//Paid Amount
                CDRList.append("<td align=left>" + numFormat.format(rset.getDouble(9)) + "</td>\n");//Balance Due
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");//DOS
                CDRList.append("</tr>");
            }
            rset.close();
            stmt.close();

            out.println(PatientName + "~" + PatientInvoiceMRN + "~" + CDRList);

        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in SelfServicePortal ** (CollectPayment_View -- 01)", servletContext, ex, "RegisteredPatients", "CollectPayment_View -- 31", conn);
            Services.DumException("CollectPayment_View", "SelfServicePortal ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "SelfServicePortal");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.SetField("Message", "MES#031");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void PaymentHistory(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query = "";
        String Query2 = "";
        double TotalAmount = 0.0D;
        double PaidAmount = 0.0D;
        int InstallmentPlanFound = 0;
        String PatientName = "";
        String PatientInvoiceMRN = request.getParameter("MRN");

        StringBuilder CDRList = new StringBuilder();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        String FacilityIdx = request.getParameter("FacilityIdx").trim();
        String database = null;

        try {
            Query = "Select dbname from oe.clients" +
                    " where Id='" + FacilityIdx + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                database = rset.getString(1);
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
//            PatientInvoiceMRN = request.getParameter("PatientInvoice").trim();
//            PatientInvoiceMRN = "315275";

            Query = " SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,'-'),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')),DATE_FORMAT(b.DOB,'%d-%m-%Y')," +
                    "IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')) AS DOS,  " +
                    "a.InvoiceNo, DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T'), a.TotalAmount, a.PaidAmount, a.BalAmount, a.Id   " +
                    " FROM " + database + ".InvoiceMaster a   " +
                    " LEFT JOIN " + database + ".PatientReg b ON a.PatientMRN = b.MRN    " +
                    " WHERE a.PatientMRN = '" + PatientInvoiceMRN + "' AND a.Status = 0 AND b.Status = 0 ORDER BY a.CreatedDate DESC LIMIT 10";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Query2 = "Select COUNT(*) FROM " + database + ".InstallmentPlan WHERE MRN = '" + rset.getString(1) + "' AND " +
                        " InvoiceNo = '" + rset.getString(5) + "'";
                stmt2 = conn.createStatement();
                rset2 = stmt2.executeQuery(Query2);
                if (rset2.next())
                    InstallmentPlanFound = rset2.getInt(1);
                rset2.close();
                stmt2.close();
                TotalAmount = rset.getDouble(7);
                PaidAmount = rset.getDouble(7) - rset.getDouble(9);
                PatientName = rset.getString(2) + "(" + rset.getString(1) + ")";
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left width=15%>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(TotalAmount) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(PaidAmount) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(rset.getDouble(9)) + "</td>\n");
                CDRList.append("</tr>");
            }
            rset.close();
            stmt.close();

            out.println(CDRList);

        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in SelfServicePortal ** (CollectPayment_View -- 01)", servletContext, ex, "RegisteredPatients", "CollectPayment_View -- 032", conn);
            Services.DumException("CollectPayment_View", "SelfServicePortal ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "SelfServicePortal");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.SetField("Message", "MES#032");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    private void InvoicePdf(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, UtilityHelper helper) throws FileNotFoundException {
        String PatientMRN = request.getParameter("PatientMRN");
        String InvoiceNo = request.getParameter("InvoiceNo");
        String ClientId = request.getParameter("FacilityIdx").trim();

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String ClientName = "";
        String PatientName = "";
        String DOB = "";
        String DOBForAge = "";
        String Age = "";
        String Sex = "";
        String DOS = "";
        String CreatedDate = null;
        double TotalAmount = 0.0D;
        double PaidAmount = 0.0D;
        String InvoiceCreatedDate = "";
        String PayMethod = "";
        int Sno = 0;
        String FileName = "";
        String DirectoryName = "";
        String database = null;
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");


        try {
            Query = "Select dbname from oe.clients" +
                    " where Id='" + ClientId + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                database = rset.getString(1);
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Query = "Select DirectoryName from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                DirectoryName = rset.getString(1);
            rset.close();
            stmt.close();
            Font MainHeading = new Font(2, 12.0F, 1, new Color(0, 0, 0));
            Font normfont = new Font(2, 8.0F, 0, new Color(0, 0, 0));
            Font normfont2 = new Font(2, 10.0F, 0, new Color(0, 0, 0));
            Font normfont3 = new Font(2, 12.0F, 0, new Color(0, 0, 0));
            Font UnderLine = new Font(2, 12.0F, 4, new Color(0, 0, 0));
            try {
                Query = "SELECT IFNULL(InvoiceNo,''), IFNULL(TotalAmount,''), IFNULL(PaidAmount,''), IFNULL(DATE_FORMAT(CreatedDate,'%m/%d/%Y'),'') " +
                        "FROM " + database + ".InvoiceMaster where PatientMRN = '" + PatientMRN + "' AND InvoiceNo='" + InvoiceNo + "' ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    TotalAmount = rset.getDouble(2);
                    PaidAmount = rset.getDouble(3);
                    InvoiceCreatedDate = rset.getString(4);
                }
                rset.close();
                stmt.close();

                Query = " Select CASE WHEN PayMethod = 1 THEN 'Credit Card' WHEN PayMethod = 2 THEN 'Cash' WHEN PayMethod = 3 THEN 'BOLT' WHEN PayMethod = 4 THEN 'Ingenico' ELSE '' END " +
                        " from " + database + ".PaymentReceiptInfo where PatientMRN = '" + PatientMRN + "' and InvoiceNo = '" + InvoiceNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    PayMethod = rset.getString(1);
                }
                rset.close();
                stmt.close();

            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in SelfServicePortal ** (InvoicePdf -- 01)", servletContext, e, "SelfServicePortal", "InvoicePdf -- 33", conn);
                Services.DumException("InvoicePdf -- 01", "SelfServicePortal ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "SelfServicePortal");
                Parser.SetField("ActionID", "CollectPayment_View");
                Parser.SetField("Message", "MES#033");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            Document document = new Document(PageSize.A4, 0.0F, 0.0F, 70.0F, 30.0F);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            PdfWriter.getInstance(document, new FileOutputStream("/sftpdrive/AdmissionBundlePdf/Invoices/" + DirectoryName + "/" + InvoiceNo + "_" + PatientMRN + ".pdf"));
            document.addAuthor(DirectoryName);
            document.addSubject("Customer Invoice");
            document.addCreationDate();
            Paragraph p = new Paragraph();
            Image jpeg = null;
            if (database.equals("oe_2")) {
                jpeg = Image.getInstance("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/orange/images/logorange.jpg");
            } else {
                jpeg = Image.getInstance("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/md/images_/jpg/" + ClientId + ".jpg");
            }
            if (jpeg != null) {
                jpeg.setAlignment(3);
                jpeg.setAbsolutePosition(210.0F, 730.0F);
                jpeg.scaleToFit(1200.0F, 95.0F);
                p.add(jpeg);
            }
            HeaderFooter header = new HeaderFooter((Phrase) p, false);
            header.setBorder(0);
            header.setAlignment(1);
            document.setHeader(header);
            document.open();
            document.add((Element) new Paragraph("\n"));
            document.add((Element) new Paragraph("\n"));
            try {
                Query = " Select CONCAT(IFNULL(a.FirstName,''), ' ', IFNULL(a.MiddleInitial,''), ' ', IFNULL(a.LastName,'')), IFNULL(b.name,''), IFNULL(a.Gender,''), " +
                        "IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''), IFNULL(a.Age,''),  IFNULL(DATE_FORMAT(a.CreatedDate,'%m/%d/%Y'),'') as DOS, " +
                        "IFNULL(DATE_FORMAT(a.DOB,'%Y-%m-%d'),'')" +
                        "from " + database + ".PatientReg a " +
                        "LEFT JOIN oe.clients b on a.ClientIndex = b.Id  where a.MRN = '" + PatientMRN + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientName = rset.getString(1);
                    ClientName = rset.getString(2);
                    Sex = rset.getString(3);
                    DOB = rset.getString(4);
                    Age = rset.getString(5);
                    DOS = rset.getString(6);
                    DOBForAge = rset.getString(7);
                }
                rset.close();
                stmt.close();

                if (!DOB.equals("")) {
                    Age = String.valueOf(getAge(LocalDate.parse(DOBForAge)));
                } else {
                    Age = "0";
                }

            } catch (Exception e2) {
                helper.SendEmailWithAttachment("Error in SelfServicePortal ** (InvoicePdf -- 02)", servletContext, e2, "SelfServicePortal", "InvoicePdf -- 02", conn);
                Services.DumException("InvoicePdf -- 02", "SelfServicePortal ", request, e2);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "SelfServicePortal");
                Parser.SetField("ActionID", "CollectPayment_View");
                Parser.SetField("Message", "MES#034");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            Table datatable1 = new Table(6);
            datatable1.setWidth(100.0F);
            int[] widths1 = {5, 15, 25, 25, 25, 5};
            datatable1.setWidths(widths1);
            datatable1.setBorder(0);
            datatable1.setCellpadding(1.0F);
            datatable1.setCellspacing(0.0F);
            datatable1.setDefaultCellBorder(0);
            datatable1.setDefaultColspan(1);
            datatable1.setDefaultHorizontalAlignment(0);
            datatable1.addCell((Phrase) new Paragraph(" ", normfont2));
            datatable1.addCell((Phrase) new Paragraph("INVOICE DATE: ", normfont2));
            datatable1.addCell((Phrase) new Paragraph(InvoiceCreatedDate, UnderLine));
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultCellBorder(13);
            datatable1.addCell((Phrase) new Paragraph(PatientName, normfont));
            datatable1.setDefaultCellBorder(0);
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultColspan(1);
            datatable1.setDefaultHorizontalAlignment(0);
            datatable1.addCell((Phrase) new Paragraph(" ", normfont2));
            datatable1.addCell((Phrase) new Paragraph("INVOICE NO: ", normfont2));
            datatable1.addCell((Phrase) new Paragraph(InvoiceNo, UnderLine));
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultCellBorder(12);
            datatable1.addCell((Phrase) new Paragraph(ClientName + "         Sex:" + Sex, normfont));
            datatable1.setDefaultCellBorder(0);
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultColspan(1);
            datatable1.setDefaultHorizontalAlignment(0);
            datatable1.addCell((Phrase) new Paragraph(" ", normfont2));
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.addCell((Phrase) new Paragraph("", UnderLine));
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultCellBorder(12);
            datatable1.addCell((Phrase) new Paragraph("DOB: " + DOB + "         Age: " + Age, normfont));
            datatable1.setDefaultCellBorder(0);
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultColspan(1);
            datatable1.setDefaultHorizontalAlignment(0);
            datatable1.addCell((Phrase) new Paragraph(" ", normfont2));
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.addCell((Phrase) new Paragraph("", UnderLine));
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultCellBorder(14);
            datatable1.addCell((Phrase) new Paragraph("MRN: " + PatientMRN + "         DOS: " + DOS, normfont));
            datatable1.setDefaultCellBorder(0);
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            document.add((Element) datatable1);
            Table datatable2 = new Table(5);
            datatable2.setWidth(100.0F);
            int[] widths2 = {10, 15, 5, 65, 5};
            datatable2.setWidths(widths2);
            datatable2.setBorder(0);
            datatable2.setCellpadding(1.0F);
            datatable2.setCellspacing(0.0F);
            datatable2.setDefaultCellBorder(0);
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell((Phrase) new Paragraph(" ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("Name: ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph(PatientName, normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell((Phrase) new Paragraph(" ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("Age: ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph(Age, normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell((Phrase) new Paragraph(" ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("Date of Birth: ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph(DOB, normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell((Phrase) new Paragraph(" ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("Sex: ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph(Sex, normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph("Payment Method:", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph(PayMethod, normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            document.add((Element) datatable2);
            Table datatable3 = new Table(7);
            datatable3.setWidth(100.0F);
            int[] widths3 = {10, 5, 40, 10, 10, 20, 5};
            datatable3.setWidths(widths3);
            datatable3.setBorder(0);
            datatable3.setCellpadding(1.0F);
            datatable3.setCellspacing(0.0F);
            datatable3.setDefaultCellBorder(0);
            datatable3.setDefaultColspan(1);
            datatable3.setDefaultHorizontalAlignment(0);
            datatable3.setDefaultCellBorder(0);
            datatable3.addCell((Phrase) new Paragraph(" ", MainHeading));
            datatable3.setDefaultCellBorder(15);
            datatable3.addCell((Phrase) new Paragraph("SNo.", MainHeading));
            datatable3.setDefaultCellBorder(11);
            datatable3.addCell((Phrase) new Paragraph(" Disease Name (Procedure)", MainHeading));
            datatable3.setDefaultCellBorder(11);
            datatable3.addCell((Phrase) new Paragraph("Cost Per Disease", MainHeading));
            datatable3.setDefaultCellBorder(11);
            datatable3.addCell((Phrase) new Paragraph("QTY", MainHeading));
            datatable3.setDefaultCellBorder(11);
            datatable3.addCell((Phrase) new Paragraph("Amount", MainHeading));
            datatable3.setDefaultCellBorder(0);
            datatable3.addCell((Phrase) new Paragraph("", MainHeading));
            Query = "Select CONCAT('(',b.Catagory,')', ' ', b.Description), a.CostPerDisease, a.Quantity " +
                    "from " + database + ".InvoiceDetail a  " +
                    "LEFT JOIN " + database + ".SelfPaySheet b on a.DiseaseId = b.Id " +
                    "where PatientMRN = " + PatientMRN + " and ltrim(rtrim(a.InvoiceNo)) = ltrim(rtrim('" + InvoiceNo.trim() + "'))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Sno++;
                datatable3.setDefaultColspan(1);
                datatable3.setDefaultHorizontalAlignment(0);
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell((Phrase) new Paragraph(" ", normfont3));
                datatable3.setDefaultCellBorder(12);
                datatable3.addCell((Phrase) new Paragraph(String.valueOf(Sno), normfont3));
                datatable3.setDefaultCellBorder(8);
                datatable3.addCell((Phrase) new Paragraph(rset.getString(1), normfont3));
                datatable3.setDefaultCellBorder(8);
                datatable3.addCell((Phrase) new Paragraph(rset.getString(2), normfont3));
                datatable3.setDefaultCellBorder(8);
                datatable3.addCell((Phrase) new Paragraph(rset.getString(3), normfont3));
                datatable3.setDefaultCellBorder(8);
                datatable3.addCell((Phrase) new Paragraph(String.valueOf(rset.getDouble(2) * rset.getDouble(3)), normfont3));
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell((Phrase) new Paragraph("", normfont3));
            }
            rset.close();
            stmt.close();
            datatable3.setDefaultColspan(1);
            datatable3.setDefaultHorizontalAlignment(0);
            datatable3.setDefaultCellBorder(0);
            datatable3.addCell((Phrase) new Paragraph(" ", MainHeading));
            datatable3.setDefaultCellBorder(14);
            datatable3.addCell((Phrase) new Paragraph("", normfont2));
            datatable3.setDefaultCellBorder(10);
            datatable3.addCell((Phrase) new Paragraph("", MainHeading));
            datatable3.setDefaultCellBorder(10);
            datatable3.addCell((Phrase) new Paragraph("", MainHeading));
            datatable3.setDefaultCellBorder(10);
            datatable3.addCell((Phrase) new Paragraph("", MainHeading));
            datatable3.setDefaultCellBorder(10);
            datatable3.addCell((Phrase) new Paragraph("", MainHeading));
            datatable3.setDefaultCellBorder(0);
            datatable3.addCell((Phrase) new Paragraph("", MainHeading));
            document.add((Element) datatable3);
            document.add((Element) new Paragraph("\n"));
            Table datatable4 = new Table(7);
            datatable4.setWidth(100.0F);
            int[] widths4 = {10, 5, 40, 0, 20, 20, 5};
            datatable4.setWidths(widths4);
            datatable4.setBorder(0);
            datatable4.setCellpadding(1.0F);
            datatable4.setCellspacing(0.0F);
            datatable4.setDefaultCellBorder(0);
            datatable4.setDefaultColspan(1);
            datatable4.setDefaultHorizontalAlignment(0);
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph(" ", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("Total ", MainHeading));
            datatable4.setDefaultCellBorder(15);
            datatable4.addCell((Phrase) new Paragraph(String.valueOf(TotalAmount), normfont3));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultColspan(1);
            datatable4.setDefaultHorizontalAlignment(0);
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph(" ", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("Paid Amount ", MainHeading));
            datatable4.setDefaultCellBorder(15);
            datatable4.addCell((Phrase) new Paragraph(String.valueOf(PaidAmount), normfont3));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultColspan(1);
            datatable4.setDefaultHorizontalAlignment(0);
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph(" ", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("Balance ", MainHeading));
            datatable4.setDefaultCellBorder(15);
            datatable4.addCell((Phrase) new Paragraph(String.valueOf(numFormat.format(TotalAmount - PaidAmount)), normfont3));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            document.add((Element) datatable4);
            document.add((Element) new Paragraph("\n"));
            Table datatable5 = new Table(2);
            datatable5.setWidth(100.0F);
            int[] widths5 = {10, 90};
            datatable5.setWidths(widths5);
            datatable5.setBorder(0);
            datatable5.setCellpadding(1.0F);
            datatable5.setCellspacing(0.0F);
            datatable5.setDefaultCellBorder(0);
            datatable5.setDefaultColspan(1);
            datatable5.setDefaultHorizontalAlignment(0);
            datatable5.addCell((Phrase) new Paragraph(" ", normfont3));
            datatable5.addCell((Phrase) new Paragraph("Patient Signature: ___________________", normfont3));
            document.add((Element) datatable5);
            document.close();
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Progma", "no-cache");
            response.setContentType("application/pdf");
            response.setContentLength(baos.size());
            ServletOutputStream out2 = response.getOutputStream();
            baos.writeTo((OutputStream) out2);
            out2.flush();
            try {
                conn.close();
            } catch (Exception ex) {
                out.println(ex.getMessage());
            }
        } catch (Exception e3) {
            helper.SendEmailWithAttachment("Error in SelfServicePortal ** (InvoicePdf -- 03)", servletContext, e3, "SelfServicePortal", "InvoicePdf -- 03", conn);
            Services.DumException("InvoicePdf -- 03", "SelfServicePortal ", request, e3);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "SelfServicePortal");
            Parser.SetField("ActionID", "CollectPayment_View");
            Parser.SetField("Message", "MES#035");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(e3.getMessage());
            String str = "";
            for (int i = 0; i < (e3.getStackTrace()).length; i++)
                str = str + e3.getStackTrace()[i] + "<br>";
            out.println(str);*/
        }
    }

    void PayNow(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, UtilityHelper helper) throws FileNotFoundException {
        stmt = null;
        rset = null;
        Query = "";
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
        String ClientId = request.getParameter("FacilityIdx").trim();
        String PatientMRN = "";
        String PatientName = "";
        double TotalAmount = 0.0D;
        double PaidAmount = 0.0D;
        double BalAmount = 0.0D;
        double AmountToPay = 0.0D;
        String DOS = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuilder DeviceList = new StringBuilder();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int SNo = 1;
        int InstallmentPlanFound = 0;
        StringBuilder installmentPlan = new StringBuilder();

        String Database = null;
        try {
            Query = "Select dbname from oe.clients" +
                    " where Id='" + ClientId + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Query = "SELECT a.PatientMRN,  CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,''))," +
                    "a.TotalAmount,a.PaidAmount,a.BalAmount,IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')) AS DOS " +
                    " FROM " + Database + ".InvoiceMaster a  " +
                    " LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN  " +
                    "WHERE a.InvoiceNo = '" + InvoiceNo + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientMRN = rset.getString(1);
                PatientName = rset.getString(2);
                TotalAmount = rset.getDouble(3);
                PaidAmount = rset.getDouble(3) - rset.getDouble(5);
                BalAmount = rset.getDouble(5);
                DOS = rset.getString(6).trim();
            }
            rset.close();
            stmt.close();

            if (BalAmount == 0.0D) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("Message", "Payment has already been paid!");
                parsehtm.SetField("FormName", "SelfServicePortal");
                parsehtm.SetField("ActionID", "CollectPayment_View");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/PortalMessage.html");
                return;
            }

            Query = "SELECT COUNT(*) FROM " + Database + ".InstallmentPlan " +
                    "WHERE MRN = '" + PatientMRN + "' AND InvoiceNo = '" + InvoiceNo + "' AND status = 0";
            stmt = conn.createStatement();
            rset = this.stmt.executeQuery(this.Query);
            if (rset.next())
                InstallmentPlanFound = this.rset.getInt(1);
            rset.close();
            stmt.close();

            if (InstallmentPlanFound > 0) {
                installmentPlan.append("<a class='btn-sm btn btn-primary' data-toggle=\"modal\" data-target=\"#installmentModal\">View</a>");
                Query = "SELECT IFNULL(MRN,''), IFNULL(InvoiceNo,''), IFNULL(PaymentAmount,0), " +
                        "IFNULL(DATE_FORMAT(PaymentDate,'%m/%d/%Y'),''),  " +
                        "CASE WHEN PAID = 0 THEN 'Pending' WHEN Paid = 1 THEN 'Paid' ELSE 'Pending' END " +
                        "FROM " + Database + ".InstallmentPlan WHERE MRN = '" + PatientMRN + "' AND  InvoiceNo = '" + InvoiceNo + "' AND status = 0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + PatientName + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    SNo++;
                }
                rset.close();
                stmt.close();

                Query = " Select IFNULL(PaymentAmount,0) from " + Database + ".InstallmentPlan " +
                        "where MRN = '" + PatientMRN + "' and  InvoiceNo = '" + InvoiceNo + "' and Paid = 0 limit 1 ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    AmountToPay = rset.getDouble(1);
                rset.close();
                stmt.close();
            } else {
                installmentPlan.append("No Installment Applied");
            }


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.SetField("PatientMRN", String.valueOf(PatientMRN));
            Parser.SetField("TotalAmount", String.valueOf(numFormat.format(TotalAmount)));
            Parser.SetField("PaidAmount", String.valueOf(numFormat.format(PaidAmount)));
            Parser.SetField("BalAmount", String.valueOf(numFormat.format(BalAmount)));
            Parser.SetField("InvoiceNo", String.valueOf(InvoiceNo));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("AmountToPay", String.valueOf(numFormat.format(AmountToPay)));
            Parser.SetField("InstallmentPlanFound", String.valueOf(InstallmentPlanFound));
            Parser.SetField("DOS", DOS);
            Parser.SetField("installmentPlan", installmentPlan.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/PayNow_SSP.html");
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in SelfServicePortal ** (PayNow -- 01)", servletContext, ex, "RegisteredPatients", "PayNow -- 01", conn);
            Services.DumException("PayNow", "SelfServicePortal ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "SelfServicePortal");
            Parser.SetField("ActionID", "CollectPayment_View");
            Parser.SetField("Message", "MES#036");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    private void cardConnectPaymentSave(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, UtilityHelper helper, Payments payments) {
        stmt = null;
        rset = null;
        Query = "";

        double CCAmount = Double.parseDouble(request.getParameter("CCAmount").trim().replaceAll(",", ""));
        String CCExpiry = request.getParameter("CCExpiry").trim();
        String CCCVC = request.getParameter("CCCVC").trim();
        String CCnameCard = request.getParameter("CCnameCard").trim();
        String myToken = request.getParameter("mytoken").trim();
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
        String PatientMRN = request.getParameter("x0Y61008").trim();
        String Description = request.getParameter("Description").trim();
        int facilityIndex = Integer.parseInt(request.getParameter("FacilityIdx").trim());
        int PtId = Integer.parseInt(request.getParameter("PtId").trim());

        String database = null;
        String userId = null;
        try {
            Query = "Select dbname,name from oe.clients  where Id='" + facilityIndex + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                database = rset.getString(1);
//                userId = rset.getString(2);
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Query = "Select email from " + database + ".PatientsCredentials  where Id='" + PtId + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
//                database = rset.getString(1);
                userId = rset.getString(1);
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int Paid = 0;
        int PayRecIdx = 0;
        int InstallmentPlanId = 0;
        double PaidAmount = 0.0D;
        double TotalAmount = 0.0D;
        double BalAmount = 0.0D;
        String ResponseType = "";
        String receipt = "";
        String printDate = "";
        String printTime = "";
        String facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        try {
            String[] PatientInfo = helper.getPatientInfo(request, conn, servletContext, database, PatientMRN);
            String FName = PatientInfo[0];
            String LName = PatientInfo[1];
            String Name = FName + " " + LName;
            CCExpiry = CCExpiry.replace("/", "");
            String Values = PatientInfo[0] + "^" + PatientInfo[1] + "^" + PatientInfo[3] + "^" + PatientInfo[4] + "^" + PatientInfo[5] + "^" + PatientInfo[6] + "^" + PatientInfo[8] + "^" + CCAmount + "^" + myToken + "^" + CCExpiry + "^" + CCCVC;
            Values = encrypt(Values.trim());
            Values = Values.replace(" ", "");

            Object[] invoiceMaster = payments.getInvoiceMasterDetails(request, conn, servletContext, database, InvoiceNo, PatientMRN);
            PaidAmount = (double) invoiceMaster[0];
            BalAmount = (double) invoiceMaster[1];
            TotalAmount = (double) invoiceMaster[3];

            if (CCAmount > BalAmount) {
                out.println("11~Amount should not be greater than Balance Due!");
                return;
            }
            if (BalAmount == 0) {
                out.println("11~No Balance amount left to pay!");
                return;
            }
            String receiptCounter = helper.getReceiptCounter(request, conn, servletContext, database);
            printDate = helper.printDateTime(request, conn, servletContext)[0];
            printTime = helper.printDateTime(request, conn, servletContext)[1];

            CardConnectPayment cardConnectPayment = new CardConnectPayment();
            String[] Response = cardConnectPayment.InquireTransaction(Values, facilityIndex, conn);

            String FullName = "";
            String Address = "";
            String Phone = "";
            FullName = helper.receiptClientData(request, conn, servletContext, facilityIndex)[0];
            Address = helper.receiptClientData(request, conn, servletContext, facilityIndex)[1];
            Phone = helper.receiptClientData(request, conn, servletContext, facilityIndex)[2];

            String UserIP = helper.getClientIp(request);
            String CurrDate = helper.getCurrDate(request, conn);
            if (Response[0].equals("Approval") || Response[0].equals("APPROVAL") ||
                    Response[0].equals("Success") || Response[0].equals("SUCCESS")) {
                if (CCAmount == BalAmount) {
                    Paid = 1;
                }

                payments.insertInvoiceMasterHistory(request, conn, servletContext, database, PatientMRN, InvoiceNo, UserIP);

                payments.updateInvoiceMaster(request, conn, servletContext, database, PaidAmount, CCAmount, BalAmount, Paid, PatientMRN, InvoiceNo);

                receipt = "<div id='printThis'><div id='mystyle' style='width: 377.68px;border: 1px; border-style: solid; border-color: black;'><div class='reciept' style='margin-top:20px;text-align: center;'><p style='margin:0px;'>" + FullName + "</p><p style='margin:0px;'>" + Address + "</p><p style='margin:0px;'>" + Phone + "</p></div><div class='reciept' style='margin-left: 5px; text-align:left !important'><p style='margin:0px;'>Ref #: " + Response[7] + "</p><p style='margin:0px;'>Status: " + Response[0] + "</p><p style='margin:0px;'>Auth #: " + Response[4] + "</p><p style='margin:0px;'>MID: " + Response[13] + "</p><p style='margin:0px;'>Receipt#: " + receiptCounter + "</p></div><div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>" + printDate + "</span> <span style='margin-left: 166px'>" + printTime + "</span></div><div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>Amount</span> <span style='margin-left: 199px'>$" + CCAmount + "</span></div><div class='reciept' style='margin-left: 5px;text-align:left !important'><p style='margin:0px;'>Method: Card</p><p style='margin:0px;'>" + Name + "</p></div><div class='reciept' style='text-align: center;'>Approved</div><div class='reciept' style='margin-bottom: 15px; text-align: center;'>Thank you. Please come again</div> <div class='reciept' style='text-align: right;font-size:12px;'>Printed By Rover</div>  </div></div>";

                payments.paymentReceiptInsertion(request, conn, servletContext, database, PatientMRN, InvoiceNo, TotalAmount, Paid, InvoiceNo, Description, "1", BalAmount, userId, UserIP, "cardConnectPaymentSave(CustomerPortal)", CCAmount, receiptCounter, receipt);

                PayRecIdx = payments.getPaymentReceiptIndex(request, conn, servletContext, database);
                ResponseType = "SUCCESS";

                payments.insertionCardConnectResponses(request, conn, servletContext, database, InvoiceNo, PatientMRN, Response[0], Response[1], Response[2], Response[3], Response[4], Response[5], Response[6], Response[7], Response[8], Response[9], CurrDate, 999, facilityIndex, myToken, CurrDate, CCCVC, Response[11], Response[10], ResponseType, Description, CCAmount, Response[13], Response[14], Response[15], UserIP, "cardConnectPaymentSave(CustomerPortal)", CCnameCard, PayRecIdx, "3", "No Insurance", receipt, "No File", userId);

                out.println("1~" + receipt);
            } else {
                ResponseType = "ERROR";
                payments.insertionCardConnectResponses(request, conn, servletContext, database, InvoiceNo, PatientMRN, Response[0], Response[1] == null ? "" : Response[1], Response[2] == null ? "" : Response[2], Response[3] == null ? "" : Response[3], Response[4] == null ? "" : Response[4], Response[5] == null ? "" : Response[5], Response[6] == null ? "" : Response[6], Response[7] == null ? "" : Response[7], Response[8] == null ? "" : Response[8], Response[9] == null ? "" : Response[9], CurrDate, 999, facilityIndex, myToken, CurrDate, CCCVC, Response[11] == null ? "" : Response[11], Response[10] == null ? "" : Response[10], ResponseType, Description, CCAmount, Response[13] == null ? "" : Response[13], Response[14] == null ? "" : Response[14], Response[15] == null ? "" : Response[15], UserIP, "cardConnectPaymentSave(CustomerPortal)", CCnameCard, PayRecIdx, "3", "No Insurance", "No Slip", "No File", userId);

                out.println("0~" + Response[0]);
            }
        } catch (Exception Ex) {
            out.println("0~Error while making payment.");
            helper.SendEmailWithAttachment("Error in SelfServicePortal Services ^^ (Occurred At : " + facilityName + ") ** (cardConnectPaymentSave)", servletContext, Ex, "CardConnectServices", "cardConnectPaymentSave", conn);
            Services.DumException("SelfServicePortal", "cardConnectPaymentSave Portal", request, Ex, getServletContext());
        }
    }

    private void SendSignUpRequest(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, UtilityHelper helper, SelfPaymentHelper paymentPortal) throws FileNotFoundException, UnknownHostException {
        int FacilityIdx = Integer.parseInt(request.getParameter("FacilityIndex").trim());
        int MRN = Integer.parseInt(request.getParameter("MRN").trim());
        String PatientEMAIL = null;
        String FirstName = null;
        String LastName = null;
        int PatientRegIdx = 0;
        String Body = null;
        String signupLink = null;

        String FDetails[] = paymentPortal.getFacilityDetails(request, conn, servletContext, FacilityIdx);

        try {
            Query = "Select IFNULL(Email,'-'),IFNULL(FirstName,'N/A'),IFNULL(LastName,'N/A'),DOB,ZipCode,Id " +
                    "FROM " + FDetails[0] + ".PatientReg  WHERE MRN='" + MRN + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                if (rset.getString(1).equals('-') || rset.getString(1).equals("")) {
                    out.println("0~");
                    out.println("Can't find EMAIL for this patient!");
                    return;
                } else {
                    PatientEMAIL = rset.getString(1);
                    FirstName = rset.getString(2);
                    LastName = rset.getString(3);
                    PatientRegIdx = rset.getInt(6);

                    out.println("1~");
                    out.println("Invitation has been sent to Email : " + PatientEMAIL);
                }
            } else {
                out.println("0~");
                out.println("Can't find EMAIL for this patient!");
                return;
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            out.println("0~Can't find EMAIL for this patient!");
            return;
        }


        String ServerName = "";
        InetAddress ip = InetAddress.getLocalHost();
        String hostname = ip.getHostName();

//        System.out.println("Hostname:--- " + hostname);

        //front-rovermd-01 app1
        //dev-rover-01 dev1
        //front2 app
        switch (hostname) {
            case "front-rovermd-01":
                ServerName = "app1";
                break;
            case "dev-rover-01":
                ServerName = "dev1";
                break;
            case "front2.rovermd.com":
                ServerName = "app";
                break;
            case "appx-dev01":
                ServerName = "appx";
                break;
        }

        int result = paymentPortal.saveInitiateRequestPaymentPortal(request, conn, servletContext, PatientEMAIL, MRN, PatientRegIdx, 1, helper.getCurrDate(request, conn), "Generate from Within System", FDetails[0]);
        if (result == 1) {
            signupLink = "https://" + ServerName + ".rovermd.com:8443/md/md.SelfServicePortal?Action=SSP_LOGIN&FacilityIdx=" + FacilityIdx;
            Body = "\n" +
                    "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<title></title>\n" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                    "<style type=\"text/css\">\n" +
                    "    /* FONTS */\n" +
                    "    @import url('https://fonts.googleapis.com/css?family=Poppins:100,100i,200,200i,300,300i,400,400i,500,500i,600,600i,700,700i,800,800i,900,900i');\n" +
                    "\n" +
                    "    /* CLIENT-SPECIFIC STYLES */\n" +
                    "    body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }\n" +
                    "    table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }\n" +
                    "    img { -ms-interpolation-mode: bicubic; }\n" +
                    "\n" +
                    "    /* RESET STYLES */\n" +
                    "    img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; }\n" +
                    "    table { border-collapse: collapse !important; }\n" +
                    "    body { height: 100% !important; margin: 0 !important; padding: 0 !important; width: 100% !important; }\n" +
                    "\n" +
                    "    /* iOS BLUE LINKS */\n" +
                    "    a[x-apple-data-detectors] {\n" +
                    "        color: inherit !important;\n" +
                    "        text-decoration: none !important;\n" +
                    "        font-size: inherit !important;\n" +
                    "        font-family: inherit !important;\n" +
                    "        font-weight: inherit !important;\n" +
                    "        line-height: inherit !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    /* MOBILE STYLES */\n" +
                    "    @media screen and (max-width:600px){\n" +
                    "        h1 {\n" +
                    "            font-size: 32px !important;\n" +
                    "            line-height: 32px !important;\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    /* ANDROID CENTER FIX */\n" +
                    "    div[style*=\"margin: 16px 0;\"] { margin: 0 !important; }\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body style=\"background-color: #f3f5f7; margin: 0 !important; padding: 0 !important;\">\n" +
                    "\n" +
                    "<!-- HIDDEN PREHEADER TEXT -->\n" +
                    "<div style=\"display: none; font-size: 1px; color: #fefefe; line-height: 1px; font-family: 'Poppins', sans-serif; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden;\">\n" +
                    "    We're thrilled to have you here! Get ready to dive into your new account.\n" +
                    "</div>\n" +
                    "\n" +
                    "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                    "    <!-- LOGO -->\n" +
                    "    <tr>\n" +
                    "        <td align=\"center\">\n" +
                    "           \n" +
                    "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                    "                <tr>\n" +
                    "                    <td align=\"center\" valign=\"top\" style=\"padding: 40px 10px 10px 10px;\">\n" +
                    "                        <a href=\"#\" target=\"_blank\" style=\"text-decoration: none;\">\n" +
                    "\t\t\t\t\t\t\t<img src=\"https://" + ServerName + ".rovermd.com:8443/md/images_/" + FacilityIdx + ".png\" alt=\"\">\n" +
                    "                        </a>\n" +
                    "                    </td>\n" +
                    "                </tr>\n" +
                    "            </table>\n" +
                    "            \n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "    <!-- HERO -->\n" +
                    "    <tr>\n" +
                    "        <td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">\n" +
                    "            \n" +
                    "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                    "           <tr>\n" +
                    "                 \n" +
                    "                    <td bgcolor=\"#f1cd4c\" align=\"center\" valign=\"top\" style=\"padding: 40px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #111111; font-family: 'Poppins', sans-serif; font-size: 48px; font-weight: 400; letter-spacing: 2px; line-height: 48px;\">\n" +
                    "                      <h1 style=\"font-size: 36px; font-weight: 600; margin: 0;\">" + FDetails[1] + "</h1>\n" +
                    "                    </td>\n" +
                    "                </tr>     " +
                    "<tr>\n" +
                    "                    <td bgcolor=\"#ffffff\" align=\"center\" valign=\"top\" style=\"padding: 40px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #111111; font-family: 'Poppins', sans-serif; font-size: 48px; font-weight: 400; letter-spacing: 2px; line-height: 48px;\">\n" +
                    "                      <h1 style=\"font-size: 20px; font-weight: 600; margin: 0;\">Hi! " + LastName + ", " + FirstName + "</h1>\n" +
                    "                    </td>\n" +
                    "                </tr>\n" +
                    "            </table>\n" +
                    "            \n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "    <!-- COPY BLOCK -->\n" +
                    "    <tr>\n" +
                    "        <td align=\"center\" style=\"padding: 0px 10px 0px 10px;\">\n" +
                    "            \n" +
                    "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                    "              <!-- COPY -->\n" +
                    "              <tr>\n" +
                    "                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 20px 30px 20px 30px; color: #666666; font-family: 'Poppins', sans-serif; font-size: 16px; font-weight: 400; line-height: 25px;\">\n" +
                    "                  <p style=\"margin: 0;\">We're excited to have you get started. First, you need to <strong>register</strong> your account. Just press the button below. It will take you to the Sign-Up page</p>\n" +
                    "                </td>\n" +
                    "              </tr>\n" +
                    "              <!-- BULLETPROOF BUTTON -->\n" +
                    "              <tr>\n" +
                    "                <td bgcolor=\"#ffffff\" align=\"left\">\n" +
                    "                  <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                    "                    <tr>\n" +
                    "                      <td bgcolor=\"#ffffff\" align=\"center\" style=\"padding: 20px 30px 30px 30px;\">\n" +
                    "                        <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                    "                          <tr>\n" +
                    "                              <td align=\"center\" style=\"border-radius: 3px;\" bgcolor=\"#3e8ef7\"><a href=\"" + signupLink + "\" target=\"_blank\" style=\"font-size: 18px; font-family: Helvetica, Arial, sans-serif; color: #ffffff; text-decoration: none; color: #ffffff; text-decoration: none; padding: 12px 50px; border-radius: 2px; border: 1px solid #17b3a3; display: inline-block;\">Get Started</a></td>\n" +
                    "                          </tr>\n" +
                    "                        </table>\n" +
                    "                      </td>\n" +
                    "                    </tr>\n" +
                    "                  </table>\n" +
                    "                </td>\n" +
                    "              </tr>\n" +
                    "              <!-- COPY -->\n" +
                    "              <!-- COPY -->\n" +
                    "              <tr>\n" +
                    "                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 20px 30px; color: #666666; font-family: &apos;Lato&apos;, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 25px;\">\n" +
                    "                  <p style=\"margin: 0;\">If you have any questions, just reply to this email—we're always happy to help out.</p>\n" +
                    "                </td>\n" +
                    "              </tr>\n" +
                    "              <!-- COPY -->\n" +
                    "              <tr>\n" +
                    "                <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 40px 30px; border-radius: 0px 0px 0px 0px; color: #666666; font-family: 'Poppins', sans-serif; font-size: 14px; font-weight: 400; line-height: 25px;\">\n" +
                    "                  <p style=\"margin: 0;\">Cheers,<br>Team</p>\n" +
                    "                </td>\n" +
                    "              </tr>\n" +
                    "            </table>\n" +
                    "           \n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "    <!-- FOOTER -->\n" +
                    "    <tr>\n" +
                    "        <td align=\"center\" style=\"padding: 10px 10px 50px 10px;\">\n" +
                    "           \n" +
                    "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                    "              <tr>\n" +
                    "                <td align=\"center\" style=\"padding: 30px 30px 30px 30px; color: #333333; font-family: 'Poppins', sans-serif; font-size: 12px; font-weight: 400; line-height: 18px;\">\n" +
                    "                  <p style=\"margin: 0;\">All Right Reserved. Copyrights &#169; R O V E R.</p>\n" +
                    "                </td>\n" +
                    "              </tr>\n" +
                    "            </table>\n" +
                    "            \n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "</table>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>\n";
            helper.SendEmail("", "Sign-Up Invitation", Body, PatientEMAIL, conn, servletContext);
        } else {
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Title", "Warning!");
            Parser.SetField("Text", "Issue reported while sending the link.");
            Parser.SetField("FormName", "SelfServicePortal");
            Parser.SetField("ActionID", "CollectPayment_View");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Warning.html");
        }
    }

    private boolean Is30minutes_NOT_Crossed(int ID, final Connection conn, final PrintWriter out, final String database) {
        Statement stmt1 = null;
        ResultSet rset1 = null;
        String Query1 = "";
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query2 = "";
        try {
            Query2 = "SELECT ResetDateTime FROM " + database + ".PatientsCredentials WHERE Id=" + ID;
            stmt2 = conn.createStatement();
            rset2 = stmt2.executeQuery(Query2);
            if (rset2.next()) {
                if (rset2.getString(1).equals("null")) {
                    return false;
                }
                Query1 = "SELECT TIMESTAMPDIFF(SECOND ,'" + rset2.getString(1) + "',NOW())/60";
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                if (rset1.next()) {
                    if (30 >= rset1.getDouble(1)) {
                        return true;
                    }
                }
                stmt1.close();
                rset1.close();
            }
            stmt2.close();
            rset2.close();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
