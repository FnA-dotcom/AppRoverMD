package md;

import DAL.Payments;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import PaymentIntegrations.CardConnectPayment;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Key;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@SuppressWarnings("Duplicates")
@WebServlet(name = "people", urlPatterns = {"/people/*"})
public class LabPatientRegistration extends HttpServlet {

    private static final String ALGO = "AES";
    private static final byte[] keyValue;
    static {
        keyValue = new byte[]{84, 35, 51, 66, 51, 36, 84, 36, 51, 67, 114, 51, 116, 75, 51, 81};
    }


    public static int getAge(LocalDate dob) {
        LocalDate curDate = LocalDate.now();
        return Period.between(dob, curDate).getYears();
    }

    private static BufferedImage imageToBufferedImage(final Image image) {
        final BufferedImage bufferedImage =
                new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return bufferedImage;
    }

    public static Image makeColorTransparent(final BufferedImage im, final Color color) {
        final ImageFilter filter = new RGBImageFilter() {
            // the color we are looking for (white)... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFFFFFFFF;

            public final int filterRGB(final int x, final int y, final int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                } else {
                    // nothing to do
                    return rgb;
                }
            }
        };

        final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    public static boolean isValid(File in) throws IOException, InterruptedException {
        Image img = ImageIO.read(in);
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        int[] pixels = new int[w * h];
        PixelGrabber pg = new PixelGrabber(img, 0, 0, w, h, pixels, 0, w);
        pg.grabPixels();
        boolean isValid = false;
        for (int pixel : pixels) {
            Color color = new Color(pixel);
            if (color.getAlpha() == 0 || color.getRGB() != Color.WHITE.getRGB()) {
                isValid = true;
                break;
            }
        }
        return isValid;
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public HttpSession Createsession(String UCID, HttpServletRequest request, ServletContext context) {
        String Query = "";
        Statement cStmt = null;
        ResultSet rset = null;

        try {
            Connection conn = Services.GetConnection(context, 1);
            String UserId = "", UserType = "", DatabaseName = "", DirectoryName = "", FontColor = "", ClientName = "", UserIndex = "";

            int FacilityIndex = 0;

            HttpSession session = request.getSession(true);
            String SessionId = session.getId();

            Query = "SELECT IFNULL(a.UserType,'-')," +
                    "IFNULL(a.clientid,0), IFNULL(b.name,''), " +
                    "IFNULL(b.dbname,''), IFNULL(b.DirectoryName,''),IFNULL(a.userid,''),a.indexptr" +
                    " FROM sysusers  a" +
                    " STRAIGHT_JOIN clients b ON a.clientid = b.Id" +
                    " WHERE upper(trim(a.clientid)) =  " + UCID;

            cStmt = conn.createStatement();
            rset = cStmt.executeQuery(Query);
            if (rset.next()) {
                UserType = rset.getString(1);
                FacilityIndex = rset.getInt(2);
                ClientName = rset.getString(3);
                DatabaseName = rset.getString(4);
                DirectoryName = rset.getString(5);
                UserId = rset.getString(6);
                UserIndex = rset.getString(7);
            }
            rset.close();
            cStmt.close();

            session.setAttribute("UserId", UserId);
            session.setAttribute("FacilityIndex", FacilityIndex);
            session.setAttribute("DatabaseName", DatabaseName);
            session.setAttribute("DirectoryName", DirectoryName);
            session.setAttribute("UserIndex", UserIndex);
            session.setMaxInactiveInterval(600);


            return session;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ActionID = "";
        String rqtype = request.getParameter("rqtype");
        String UCID = request.getParameter("UCID");
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
        Connection conn = null;
        HttpSession session = null;
        Payments payments = new Payments();


        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setHeader("Expires", "0"); // Proxies.
        String getMRN = "";
        int clientIdx = 0;

        Parsehtm Parser;
        if (rqtype != null) {
            if (request.getHeader("origin") == null) {
                out.println("InValid Request!");
                return;
            } else if (request.getHeader("origin").compareTo("https://app1.rovermd.com:8443") == 0) {
                session = Createsession(UCID, request, context);
            } else if (request.getHeader("origin").compareTo("https://app1.rovermd.com:8443") != 0) {
                out.println("InValid Request!");
                return;
            }
        } else {
            session = request.getSession(false);
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
        }

        String UserId = session.getAttribute("UserId").toString();
        String DatabaseName = session.getAttribute("DatabaseName").toString();
        int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
        String DirectoryName = session.getAttribute("DirectoryName").toString();
        int UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());


        if (request.getRequestURI().contains("people")) {
            getMRN = request.getRequestURI().substring("/md/people/".length());
            clientIdx = Integer.parseInt(request.getRequestURI().substring("/md/people/".length()));
            updatePatientReg(request, response, out, getMRN, DatabaseName);
            out.flush();
            out.close();
            return;
        }

        if (UserId.equals("")) {
            Parsehtm parsehtm = new Parsehtm(request);
            parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
            out.flush();
            out.close();
            return;
        }

        if (request.getParameter("ActionID") == null) {
            out.println("InValid Request");
            return;
        } else {
            ActionID = request.getParameter("ActionID");
        }


        conn = Services.GetConnection(context, 1);

        if (conn == null) {
            Parsehtm parsehtm = new Parsehtm(request);
            parsehtm.SetField("Error", "Unable to connect. Our team is looking into it!");
            parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
            return;
        }
        try {
            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);

            switch (ActionID) {
                case "GetValues":
                    GetValues(request, out, context, helper, conn, DatabaseName);
                    break;
                case "PatientsDocUpload_Save":
                    PatientsDocUpload_Save(request, out, conn, context, response, FacilityIndex);
                    break;
                case "GETINPUTRoverLab":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "RoverLab Admission Bundle", "Download or View Admission Bundle", FacilityIndex);
                    BundlePrimescope(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName);
                    break;
                case "SignPdf":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Signing", "Download or View Admission Bundle", FacilityIndex);
                    SignPdf(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName, helper);
                    break;
                case "GetData":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Input", "Click on Search Old Patient Option", FacilityIndex);
                    GetData(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, DirectoryName);
                    break;
                case "CheckPatient":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Check Duplicate Patients in Rover Lab", "Check if the Patient Exist ", FacilityIndex);
                    CheckPatient(request, out, conn, context, DatabaseName, helper);
                    break;
                case "TransactionReport_Input":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Transaction Report", "Open Transaction Report Input ", FacilityIndex);
                    TransactionReport_Input(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "TransactionReport":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Transaction Report", "Get Transaction Report", FacilityIndex);
                    showReport(request, response, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, UserIndex);
                    break;
                case "PatientTransaction":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Transaction Report", "Get Transaction Report", FacilityIndex);
                    PatientTransaction(request, response, out, conn, context, UserId, DatabaseName, helper, FacilityIndex);
                    break;
                case "PayNow":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "PayNow in Rover Lab", "PayNow ", FacilityIndex);
                    PayNow(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, payments);
                    break;
                case "makeCardConnectPayment":
                    cardConnectPaymentSave(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;
                //CLOSED BY TABISH ***** 28-FEB-2022
/*                    case "updatePatientReg":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Input", "Click on Search Old Patient Option", FacilityIndex);
                    updatePatientReg(request, response, out, DatabaseName);
                    break;*/
                // CLOSED BY TABISH ***** 28-FEB-2022
                case "EditValues":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Edit Values New from Lab Patient Reg", "Click on View Edit Option from View Patients Option ", FacilityIndex);
                    EditValues(request, out, conn, context, DatabaseName, UserId, helper);
                    break;
                case "GetPatientsMainScreen":
                    GetPatientsMainScreen(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "CheckRepeatPatient":
                    CheckRepeatPatient(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "DisplayExistingPatient":
                    DisplayExistingPatient(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "SaveExistingPatient":
                    SaveExistingPatient(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
                    break;
                case "EditSave":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Edit in Lab ", "Updating the new information from Page in Lab", FacilityIndex);
                    SaveEditData(request, out, conn, context, DatabaseName, helper, DirectoryName, FacilityIndex, UserId, DatabaseName);
                    break;
                default: {
                    helper.deleteUserSession(request, conn, session.getId());
                    session.invalidate();
                    Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
                }
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (handleRequest)", context, e, "LabPatientRegistration", "handleRequest", conn);
            Services.DumException("LabPatientRegistration", "Handle Request", request, e, getServletContext());
            Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (handleRequest -- SqlException)", context, e, "LabPatientRegistration", "handleRequest", conn);
                Services.DumException("LabPatientRegistration", "Handle Request", request, e, getServletContext());
                Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }

    private void updatePatientReg(HttpServletRequest request, HttpServletResponse response, PrintWriter out, String mrn, String DatabaseName) {

        Connection conn = null;
        String isVerifiedCheck = "";
        ResultSet rset = null;
        String Query = "";
        Statement stmt = null;
        PreparedStatement pStmt = null;
        ServletContext context = getServletContext();
        response.setContentType("text/html");
        UtilityHelper helper = new UtilityHelper();
        try {

//            UserId = "rover.lab";//session.getAttribute("UserId").toString();
//            DatabaseName = "roverlab";//session.getAttribute("DatabaseName").toString();
//            DirectoryName = "roverlab";//session.getAttribute("DirectoryName").toString();
//            FacilityIndex = 36;//Integer.parseInt(session.getAttribute("FacilityIndex").toString());

//            conn = Services.GetConnection(context, 1);
//            if (conn == null) {
//                Parsehtm parsehtm = new Parsehtm(request);
//                parsehtm = new Parsehtm(request);
//                parsehtm.SetField("Error", "Unable to connect. Our team is looking into it!");
//                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
//                return;
//            }
            Query = "select isVerified from " + DatabaseName + ".PatientReg where MRN='" + mrn + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                isVerifiedCheck = rset.getString(1);
            }
            if (isVerifiedCheck.equals("0")) {
                Query = "update " + DatabaseName + ".PatientReg set isVerified=1 where MRN='" + mrn + "'";
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

//                System.out.println("Email verified...");
//                System.out.println("zur Patient updated 1");
                request.setAttribute("ActionID", "OKK");
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("EmailVerified", "Verified");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/EmailVerified.html");
            } else if (isVerifiedCheck.equals("1")) {
//                System.out.println("zur  already updated");
                request.setAttribute("ActionID", "OKK");
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("EmailVerified", "Already Verified");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/EmailVerified.html");

            }

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (handleRequest)", context, e, "LabPatientRegistration", "handleRequest", conn);
            Services.DumException("LabPatientRegistration", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            out.flush();
            out.close();
        }
    }

    void GetValuesOLDTABISH(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, UtilityHelper helper) throws FileNotFoundException {

        String facilityName = "";
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Date = "";
            String PRF_name = "";
            StringBuffer Month = new StringBuffer();
            StringBuffer Day = new StringBuffer();
            StringBuffer Year = new StringBuffer();
            StringBuffer ProfessionalPayersList = new StringBuffer();
            int ClientIndex = 36;
            facilityName = helper.getFacilityName(request, conn, servletContext, ClientIndex);

            Query = "Select Date_format(now(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                Date = rset.getString(1);
            rset.close();
            stmt.close();

            Query = "Select PRF_name from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                PRF_name = rset.getString(1);
            rset.close();
            stmt.close();

            Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers " +
                    "where id  in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955,64,1545,3646,8589,200,201,202,203,204,205,206,207,3649,5978,8206,4763,3465,3466,3467,3468,41,387,388,389,697,698,4757, 8605, 8606, 8254, 2560) " +
                    " AND Status != 100 group by PayerId";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ProfessionalPayersList.append("<option value=''>Select Insurance</option>");
            while (rset.next())
                ProfessionalPayersList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            rset.close();
            stmt.close();

            Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers " +
                    "where PayerName like  '%Texas%' OR PayerName like '%TX%' AND Status != 100";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                ProfessionalPayersList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            rset.close();
            stmt.close();

            String[] month = {
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
                    "Nov", "Dec"};
            int day = 1;
            int year = Calendar.getInstance().get(1);
            int i;
            for (i = 1; i <= month.length; i++) {
                if (i < 10)
                    Month.append("<option value=0" + i + ">" + month[i - 1] + "</option>");
                else
                    Month.append("<option value=" + i + ">" + month[i - 1] + "</option>");
            }
            for (i = 1; i <= 31; i++) {
                if (i < 10)
                    Day.append("<option value=0" + i + ">" + i + "</option>");
                else
                    Day.append("<option value=" + i + ">" + i + "</option>");
            }
            for (i = 1901; i <= year; i++) {
                if (i == year) {
                    Year.append("<option value=" + i + " selected>" + i + "</option>");
                } else {
                    Year.append("<option value=" + i + ">" + i + "</option>");
                }
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Date", String.valueOf(Date));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("ClientIndex_logo", String.valueOf(ClientIndex));
            Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
            Parser.SetField("ProfessionalPayersList2", String.valueOf(ProfessionalPayersList));
            Parser.SetField("Month", String.valueOf(Month));
            Parser.SetField("Day", String.valueOf(Day));
            Parser.SetField("Year", String.valueOf(Year));
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/PRF_files/" + PRF_name);
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (GetValues^^" + facilityName + ")", servletContext, ex, "LabPatientRegistration", "GetValues", conn);
            Services.DumException("GetValues^^" + facilityName + "", "LabPatientRegistration ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#001");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void GetValues(HttpServletRequest request, PrintWriter out, ServletContext servletContext, UtilityHelper helper, Connection conn, String Database) throws FileNotFoundException {

        String facilityName = "";
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Date = "";
            String PRF_name = "CovidReg.html";
            StringBuffer Month = new StringBuffer();
            StringBuffer Day = new StringBuffer();
            StringBuffer Year = new StringBuffer();
            StringBuilder TestList = new StringBuilder();
            StringBuilder LocationList = new StringBuilder();
            int ClientIndex = 36;
            String loc = "0";
            String st = "0";
            facilityName = "RoverLab";

            String rqType = null;
            if (request.getParameter("rqtype") != null) {
                rqType = request.getParameter("rqtype");
            }


            if (request.getParameter("PRF_name") == null) {
                PRF_name = "CovidReg.html";

            } else {
                PRF_name = request.getParameter("PRF_name");
            }
            if (request.getParameter("loc") == null) {
                loc = "0";
            } else {
                loc = request.getParameter("loc");
            }
            if (request.getParameter("st") == null) {
                st = "0";
            } else {
                st = request.getParameter("st");
            }

            String[] month = {
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
                    "Nov", "Dec"};
            int day = 1;
            int year = Calendar.getInstance().get(1);
            int i;
            for (i = 1; i <= month.length; i++) {
                if (i < 10)
                    Month.append("<option value=0" + i + ">" + month[i - 1] + "</option>");
                else
                    Month.append("<option value=" + i + ">" + month[i - 1] + "</option>");
            }
            for (i = 1; i <= 31; i++) {
                if (i < 10)
                    Day.append("<option value=0" + i + ">" + i + "</option>");
                else
                    Day.append("<option value=" + i + ">" + i + "</option>");
            }
            for (i = 1901; i <= year; i++) {
                if (i == year) {
                    Year.append("<option value=" + i + " selected>" + i + "</option>");
                } else {
                    Year.append("<option value=" + i + ">" + i + "</option>");
                }
            }

            Query = "SELECT Id,TestName FROM " + Database + ".ListofTests " +
                    "WHERE Status = 0 ORDER BY TestName ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            //TestList.append("<option value='-1' selected>None</option>");
            while (rset.next()) {
                if (rset.getInt(1) == 1)
                    TestList.append("<option value=\"" + rset.getInt(1) + "\" selected>" + rset.getString(2) + " </option>");
                else
                    TestList.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();

            Query = "SELECT Id,Location FROM " + Database + ".Locations " +
                    "WHERE Status = 0 ORDER BY Location ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            LocationList.append("<option value='' selected>None</option>");
            while (rset.next()) {
                if (rset.getString(1).equals(loc))
                    LocationList.append("<option value=\"" + rset.getInt(1) + "\" selected>" + rset.getString(2) + " </option>");
                else
                    LocationList.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Date", String.valueOf(Date));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("ClientIndex_logo", String.valueOf(ClientIndex));
            Parser.SetField("loc", loc);
            Parser.SetField("st", st);
            Parser.SetField("Month", String.valueOf(Month));
            Parser.SetField("Day", String.valueOf(Day));
            Parser.SetField("Location", String.valueOf(Day));
            Parser.SetField("Year", String.valueOf(Year));
            Parser.SetField("rqtype", rqType);
            Parser.SetField("TestList", TestList.toString());
            Parser.SetField("LocationList", LocationList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/PRF_files/" + PRF_name);
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (GetValues^^" + facilityName + ")", servletContext, ex, "LabPatientRegistration", "GetValues", conn);
            Services.DumException("GetValues^^" + facilityName + "", "LabPatientRegistration ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#001");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void SaveData(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database, UtilityHelper helper, String DirectoryName, String IDs, String InsuranceIDsF, String InsuranceIDsB, String ClientId, HashMap<String, String> valuemap) throws FileNotFoundException {
        PreparedStatement ps = null;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        int DocIdx = 0;
        int AddmissionBundle = 0;
        int ClientIndex = 0;
        ClientIndex = Integer.parseInt(ClientId);//Integer.parseInt(request.getParameter("ClientIndex").trim());
/*        int EditFlag = Integer.parseInt(request.getParameter("EditFlag").trim());
        if (EditFlag == 0)
            ClientIndex = Integer.parseInt(ClientId);//Integer.parseInt(request.getParameter("ClientIndex").trim());
        else if (EditFlag == 1)
            ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());*/

        String rqType = null;
        String UCID = null;
        String Title = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String MaritalStatus = "";
        String DOB = "";
        String Age = "";
        String gender = "";
        String Email = "";
        String PhNumber = "";
        String Address = "";
        String Address2 = "";
        String StreetAddress2 = "";
        String City = "";
        String State = "";
        String Country = "";
        String County = "";
        String ZipCode = "";
        String Ethnicity = "";
        String Race = "";
        String PhotoID = "";
        String SSN = "";
        String DrivingLicense = "";
        String StateID = "";
        String SpCarePhy = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        int TravellingChk = 0;
        String TravelWhen = "";
        String TravelWhere = "";
        String TravelHowLong = "";
        int COVIDExposedChk = 0;
        int COVIDPositveChk = 0;
        String CovidPositiveDate = "";
        String CovidExpWhen = "";
        String SympFever = "0";
        String SympBodyAches = "0";
        String SympSoreThroat = "0";
        String SympFatigue = "0";
        String SympRash = "0";
        String SympVomiting = "0";
        String SympDiarrhea = "0";
        String SympChills = "0";
        String SympRunnyNose = "0";
        String SympDifficultBreathing = "0";
        String SympNausea = "0";
        String SympFluSymptoms = "0";
        String SympHeadache = "0";
        String SympLossTaste = "0";
        String sympDate = "";
        String vaccOrnot = "";
        String suspecSymp = "";
        String SympShortBreath = "0";
        String SympCongestion = "0";
        String AddInfoTextArea = "";
        String GuarantorName = "";
        String GuarantorDOB = "";
        String GuarantorNumber = "";
        String GuarantorSSN = "";
        String SympEyeConjunctivitis = "0";
        String PriCarePhy = "";
        String ReasonVisit = "";
        int WorkersCompPolicy = 0;
        int MotorVehAccident = 0;
        String PriInsurance = "";
        String MemId = "";
        String GrpNumber = "";
        String Test = "";
        String AtTestSite = "";
        String TesttingLoc = "";
        String haveIns = "";
        String RespParty = "";
        String Carrier = "";

        String insuranceFront = "";
        String insuranceBack = "";
        String anySymp = "";

        String PriInsuranceName = "";
        String OtherInsuranceName = "";


        String PolicyHolder = "";
        String NoInsurance = "";
        String Covid_19_PCR = "";
        String Mid_turbinate_Nasal_Swab = "";
        String datetimeColected = "";
        String DateOnset = "";
        String COVID_Result = "";
        String specimentType = "";
        String specimenID = "";
        String ancestry = "";
        String datetimeofspecimen = "";
        String insuranceAgreement = "";
        String PolicyHolderDOB = "";
        String RelationshipToPH = "";
        String memberID = "";
        String PolicyType = "";

        String Providence = "";
        String PSSNtext = "";
        String SSLtext = "";
        String SIDtext = "";


        String ICD_10_J80 = "";
        String ICD_10_J20_8 = "";
        String ICD_10_J22 = "";
        String ICD_10_J1_89 = "";
        String ICD_10_J98_8 = "";
        String ICD_10_R05 = "";
        String ICD_10_R06_02 = "";
        String ICD_10_R50_9 = "";
        String ICD_10_Z20_828 = "";
        String ICD_10_Z03_818 = "";
        String ICD_10_B97_29 = "";
        String InternationalTravel = "";
        String closeContact = "";
        String massGather = "";
        String askedBy = "";
        String testPositive = "";
        String healthCareEmp = "";
        String atICU = "";
        String atHosp = "";
        String PregOrNot = "";
        String atCongreCare = "";
        String sampleType = "";
        String Fever = "";
        String HowLongFever = "";
        String Cough = "";
        String HowLongCough = "";
        String breathShortness = "";
        String HowLongbreathShortness = "";
        String breathingDifficulty = "";
        String HowLongbreathingDifficulty = "";


        String ClinicName = "";
        String PhysicianName = "";
        String NPI = "";
        String AddressClinical = "";
        String CityClinical = "";
        String StateClinical = "";
        String ZipClinical = "";
        String Fax = "";
        String PhClinical = "";

        String EmailClinical = "";

        String SendReportTo = "";
        String PCFR = "";
        String TestType = "";


        String AddressIfDifferent = "";
        String PrimaryDOB = "";
        String PrimarySSN = "";
        String PatientRelationtoPrimary = "";
        String PrimaryOccupation = "";
        String PrimaryEmployer = "";
        String EmployerAddress = "";
        String EmployerPhone = "";
        String SecondryInsurance = "";
        String SubscriberName = "";
        String SubscriberDOB = "";
        String MemberID_2 = "";
        String GroupNumber_2 = "";
        String PatientRelationshiptoSecondry = "";
        String NextofKinName = "";
        String RelationToPatientER = "";
        String PhoneNumberER = "";
        int LeaveMessageER = 0;
        String AddressER = "";
        String CityER = "";
        String StateER = "";
        String CountryER = "";
        String ZipCodeER = "";
        int ReturnPatient = 0;
        int Google = 0;
        int MapSearch = 0;
        int Billboard = 0;
        int OnlineReview = 0;
        int TV = 0;
        int Website = 0;
        int BuildingSignDriveBy = 0;
        int Facebook = 0;
        int School = 0;
        String School_text = "";
        int Twitter = 0;
        int Magazine = 0;
        String Magazine_text = "";
        int Newspaper = 0;
        String Newspaper_text = "";
        int FamilyFriend = 0;
        String FamilyFriend_text = "";
        int UrgentCare = 0;
        String UrgentCare_text = "";
        int CommunityEvent = 0;
        String CommunityEvent_text = "";
        int Work = 0;
        String Work_text = "";
        int Physician = 0;
        String Physician_text = "";
        int Other = 0;
        String Other_text = "";
        int SelfPayChk = 0;
        int FrVisitedBefore = 0;
        int FrFamiliyVisitedBefore = 0;
        int FrInternet = 0;
        int FrBillboard = 0;
        int FrGoogle = 0;
        int FrBuildingSignage = 0;
        int FrFacebook = 0;
        int FrLivesNear = 0;
        int FrTwitter = 0;
        int FrTV = 0;
        int FrMapSearch = 0;
        int FrEvent = 0;
        String FrPhysicianReferral = "";
        String FrNeurologyReferral = "";
        String FrUrgentCareReferral = "";
        String FrOrganizationReferral = "";
        String FrFriendFamily = "";
        String PatientName = "";
        String ExtendedMRN = "";
        String VisitId = "";
        int MRN = 0;
        String CurrentDate = "";
        String facilityName = helper.getFacilityName(request, conn, servletContext, ClientIndex);


        if (valuemap.get("rqtype1") == null) {
            rqType = null;
        } else {
            rqType = valuemap.get("rqtype1").trim();
        }
        try {

            try {
                try {
                    if (valuemap.get("Title") == null) {
                        Title = "";
                    } else {
                        Title = valuemap.get("Title").trim();
                    }
                    if (valuemap.get("FirstName") == null) {
                        FirstName = "";
                    } else {
                        FirstName = valuemap.get("FirstName").trim();
//                    System.out.println("FirstName => "+FirstName);
                    }
                    if (valuemap.get("LastName") == null) {
                        LastName = "";
                    } else {
                        LastName = valuemap.get("LastName").trim();
                    }
                    if (valuemap.get("MiddleInitial") == null) {
                        MiddleInitial = "";
                    } else {
                        MiddleInitial = valuemap.get("MiddleInitial").trim();
                    }
                    if (valuemap.get("MaritalStatus") == null) {
                        MaritalStatus = "";
                    } else {
                        MaritalStatus = valuemap.get("MaritalStatus").trim();
                    }
                    if (valuemap.get("DOB") == null) {
                        DOB = "0000-00-00";
                    } else {
                        DOB = valuemap.get("DOB").trim();
                    }
                    if (valuemap.get("Age") == null) {
                        Age = "0";
                    } else {
                        Age = valuemap.get("Age").trim();
                    }
                    if (valuemap.get("gender") == null) {
                        gender = "";
                    } else {
                        gender = valuemap.get("gender").trim();
                    }
                    if (valuemap.get("Email") == null) {
                        Email = "";
                    } else {
                        Email = valuemap.get("Email").trim();
                    }
                    if (valuemap.get("PhNumber") == null) {
                        PhNumber = "";
                    } else {
                        PhNumber = valuemap.get("PhNumber").trim();
                    }
                    if (valuemap.get("Address") == null) {
                        Address = "";
                    } else {
                        Address = valuemap.get("Address").trim();
                    }
                    if (valuemap.get("Address2") == null) {
                        Address2 = "";
                    } else {
                        Address2 = valuemap.get("Address2").trim();
                    }
                    if (valuemap.get("StreetAddress2") == null) {
                        StreetAddress2 = "";
                    } else {
                        StreetAddress2 = valuemap.get("StreetAddress2").trim();
                    }
                    if (valuemap.get("City") == null) {
                        City = "";
                    } else {
                        City = valuemap.get("City").trim();
                    }
                    if (valuemap.get("State") == null) {
                        State = "";
                    } else {
                        State = valuemap.get("State").trim();
                    }
                    if (valuemap.get("Country") == null) {
                        Country = "";
                    } else {
                        Country = valuemap.get("Country").trim();
                    }
                    if (valuemap.get("Ethnicity") == null) {
                        Ethnicity = "";
                    } else {
                        Ethnicity = valuemap.get("Ethnicity").trim();
                    }
                    if (valuemap.get("Race") == null) {
                        Race = "";
                    } else {
                        Race = valuemap.get("Race").trim();
                    }
                    if (valuemap.get("IDfile") == null) {
                        PhotoID = "";
                    } else {
                        PhotoID = valuemap.get("IDfile").trim();
                    }
                    if (valuemap.get("County") == null) {
                        County = "";
                    } else {
                        County = valuemap.get("County").trim();
                    }
                    if (valuemap.get("ZipCode") == null) {
                        ZipCode = "";
                    } else {
                        ZipCode = valuemap.get("ZipCode").trim();
                    }

                    if (valuemap.get("PriInsurance") == null) {
                        PriInsurance = "";
                    } else {
                        PriInsurance = valuemap.get("PriInsurance").trim();
                    }
                    if (valuemap.get("MemId") == null) {
                        MemId = "";
                    } else {
                        MemId = valuemap.get("MemId").trim();
                    }
                    if (valuemap.get("GrpNumber") == null) {
                        GrpNumber = "";
                    } else {
                        GrpNumber = valuemap.get("GrpNumber").trim();
                    }

                    if (valuemap.get("Test") == null) {
                        Test = "";
                    } else {
                        Test = valuemap.get("Test").trim();
                    }

                    if (valuemap.get("testingSite") == null) {
                        AtTestSite = "";
                    } else {
                        AtTestSite = valuemap.get("testingSite").trim();
                    }

                    if (valuemap.get("TesttingLoc") == null) {
                        TesttingLoc = "";
                    } else {
                        TesttingLoc = valuemap.get("TesttingLoc").trim();
                    }

                    if (valuemap.get("haveIns") == null) {
                        haveIns = "";
                    } else {
                        haveIns = valuemap.get("haveIns").trim();
                    }


                    if (valuemap.get("RespParty") == null) {
                        RespParty = "";
                    } else {
                        RespParty = valuemap.get("RespParty").trim();
                    }

                    if (valuemap.get("Carrier") == null) {
                        Carrier = "";
                    } else {
                        Carrier = valuemap.get("Carrier").trim();
                    }

                    if (valuemap.get("insuranceFront") == null) {
                        insuranceFront = "";
                    } else {
                        insuranceFront = valuemap.get("insuranceFront").trim();
                    }

                    if (valuemap.get("insuranceBack") == null) {
                        insuranceBack = "";
                    } else {
                        insuranceBack = valuemap.get("insuranceBack").trim();
                    }


                    if (valuemap.get("anySymp") == null) {
                        anySymp = "";
                    } else {
                        anySymp = valuemap.get("anySymp").trim();
                    }

                    if (valuemap.get("SympFever") == null) {
                        SympFever = "0";
                    } else {
                        SympFever = "1";
                    }
                    if (valuemap.get("SympDiarrhea") == null) {
                        SympDiarrhea = "0";
                    } else {
                        SympDiarrhea = "1";
                    }
                    if (valuemap.get("SympHeadache") == null) {
                        SympHeadache = "0";
                    } else {
                        SympHeadache = "1";
                    }
                    if (valuemap.get("SympCongestion") == null) {
                        SympCongestion = "0";
                    } else {
                        SympCongestion = "1";
                    }

                    if (valuemap.get("SympShortBreath") == null) {
                        SympShortBreath = "0";
                    } else {
                        SympShortBreath = "1";
                    }
                    if (valuemap.get("SympBodyAches") == null) {
                        SympBodyAches = "0";
                    } else {
                        SympBodyAches = "1";
                    }
                    if (valuemap.get("SympChills") == null) {
                        SympChills = "0";
                    } else {
                        SympChills = "1";
                    }
                    if (valuemap.get("SympFatigue") == null) {
                        SympFatigue = "0";
                    } else {
                        SympFatigue = "1";
                    }
                    if (valuemap.get("SympSoreThroat") == null) {
                        SympSoreThroat = "0";
                    } else {
                        SympSoreThroat = "1";
                    }
                    if (valuemap.get("SympRunnyNose") == null) {
                        SympRunnyNose = "0";
                    } else {
                        SympRunnyNose = "1";
                    }

                    if (valuemap.get("SympDifficultBreathing") == null) {
                        SympDifficultBreathing = "0";
                    } else {
                        SympDifficultBreathing = "1";
                    }
                    if (valuemap.get("SympVomiting") == null) {
                        SympVomiting = "0";
                    } else {
                        SympVomiting = "1";
                    }
                    if (valuemap.get("SympLossTaste") == null) {
                        SympLossTaste = "0";
                    } else {
                        SympLossTaste = "1";
                    }

                    if (valuemap.get("sympDate") == null) {
                        sympDate = "";
                    } else {
                        sympDate = valuemap.get("sympDate").trim();
                    }

                    if (valuemap.get("vaccOrnot") == null) {
                        vaccOrnot = "";
                    } else {
                        vaccOrnot = valuemap.get("vaccOrnot").trim();
                    }

                    if (valuemap.get("suspecSymp") == null) {
                        suspecSymp = "";
                    } else {
                        suspecSymp = valuemap.get("suspecSymp").trim();
                    }

                    if (valuemap.get("closeContact") == null) {
                        closeContact = "";
                    } else {
                        closeContact = valuemap.get("closeContact").trim();
                    }


                    if (valuemap.get("massGather") == null) {
                        massGather = "";
                    } else {
                        massGather = valuemap.get("massGather").trim();
                    }

                    if (valuemap.get("askedBy") == null) {
                        askedBy = "";
                    } else {
                        askedBy = valuemap.get("askedBy").trim();
                    }

                    if (valuemap.get("testPositive") == null) {
                        testPositive = "";
                    } else {
                        testPositive = valuemap.get("testPositive").trim();
                    }

                    if (valuemap.get("healthCareEmp") == null) {
                        healthCareEmp = "";
                    } else {
                        healthCareEmp = valuemap.get("healthCareEmp").trim();
                    }


                    if (valuemap.get("atICU") == null) {
                        atICU = "";
                    } else {
                        atICU = valuemap.get("atICU").trim();
                    }

                    if (valuemap.get("atHosp") == null) {
                        atHosp = "";
                    } else {
                        atHosp = valuemap.get("atHosp").trim();
                    }

                    if (valuemap.get("PregOrNot") == null) {
                        PregOrNot = "";
                    } else {
                        PregOrNot = valuemap.get("PregOrNot").trim();
                    }

                    if (valuemap.get("atCongreCare") == null) {
                        atCongreCare = "";
                    } else {
                        atCongreCare = valuemap.get("atCongreCare").trim();
                    }
                    if (valuemap.get("sampleType") == null) {
                        sampleType = "";
                    } else {
                        sampleType = valuemap.get("sampleType").trim();
                    }
                    if (valuemap.get("SendReportTo") == null) {
                        SendReportTo = "";
                    } else {
                        SendReportTo = valuemap.get("SendReportTo").trim();
                    }

                    if (valuemap.get("SSN") == null) {
                        SSN = "";
                    } else {
                        SSN = valuemap.get("SSN").trim();
                    }
                    if (valuemap.get("DL") == null) {
                        DrivingLicense = "";
                    } else {
                        DrivingLicense = valuemap.get("DL").trim();
                    }
                    if (valuemap.get("StateID") == null) {
                        StateID = "";
                    } else {
                        StateID = valuemap.get("StateID").trim();
                    }

                } catch (Exception ex) {
                    helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (SaveData^^" + facilityName + " ##MES#002)", servletContext, ex, "LabPatientRegistration", "SaveData", conn);
                    Services.DumException("SaveData^^" + facilityName + "", "PatientReg##MES#002 ", request, ex);
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg");
                    Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
                    Parser.SetField("Message", "MES#002");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    return;
                }
                try {
                    Query = "Select Id,dbname from oe.clients where ltrim(rtrim(UPPER(Id))) =  ltrim(rtrim(UPPER('" + ClientIndex + "')))";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        ClientIndex = rset.getInt(1);
                        Database = rset.getString(2);
                    }
                    rset.close();
                    stmt.close();

                    if (MRN == 0)
                        MRN = 310001;

                    Query = "Select MRN from " + Database + ".PatientReg order by ID desc limit 1 ";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next())
                        MRN = rset.getInt(1);
                    rset.close();
                    stmt.close();
                    if (String.valueOf(MRN).length() == 0) {
                        MRN = 310001;
                    } else if (String.valueOf(MRN).length() == 4) {
                        MRN = 310001;
                    } else if (String.valueOf(MRN).length() == 8) {
                        MRN = 310001;
                    } else if (String.valueOf(MRN).length() == 6) {
                        MRN++;
                    }
                    if (String.valueOf(ClientIndex).length() == 1) {
                        ExtendedMRN = "100" + ClientIndex + MRN;
                    } else if (String.valueOf(ClientIndex).length() == 2) {
                        ExtendedMRN = "10" + ClientIndex + MRN;
                    } else if (String.valueOf(ClientIndex).length() == 3) {
                        ExtendedMRN = "1" + ClientIndex + MRN;
                    }
                } catch (Exception ex) {
                    helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (SaveData^^" + facilityName + " ##MES#003)", servletContext, ex, "LabPatientRegistration", "SaveData", conn);
                    Services.DumException("SaveData^^" + facilityName + " ##MES#003", "LabPatientRegistration ", request, ex);
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg");
                    Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
                    Parser.SetField("Message", "MES#003");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    return;
                }
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (SaveData^^" + facilityName + " ##MES#004)", servletContext, ex, "LabPatientRegistration", "SaveData", conn);
                Services.DumException("SaveData^^" + facilityName + " ##MES#004", "LabPatientRegistration ", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg");
                Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
                Parser.SetField("Message", "MES#004");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }
            if (sampleType.equals("")) {
                sampleType = String.valueOf(MRN);
            }

            UtilityHelper utilityHelper = new UtilityHelper();
            String ClientIp = utilityHelper.getClientIp(request);
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".PatientReg (FirstName ,LastName ,MiddleInitial ,Gender ,PhNumber ,Email ,Address ,City ,State ,County ,Ethnicity ,Race ,PhotoID, " +
                                " Test ,AtTestSite ,TestingLocation ," +
                                " Insured ,RespParty ,CarrierName ,GrpNumber ,MemID ,InsuranceIDFront ,InsuranceIDBack ," +
                                " HaveSymptoms ,SympFever ,SympDiarrhea ,SympHeadache ,SympCongestion ,SympShortBreath ,SympBodyache ,SympChills ,SympFatigue ,SympSoreThroat ,SympRunnyNose ,SympDiffBreathin ,SympNausea ,SympLossSmellTaste ,FullyVaccinated ,HaveExposed ,FirstTimeCovid ,HealthCareEmp ,InICU ,InHospital ,IsPregnant ,IsResident ,HaveCloseContact ,HaveParticipated ,HaveAsked ,DateOfSymp ,MRN ,ExtendedMRN ,Status ,CreatedDate," +
                                " DOB,ZipCode,UserIP,StageIdx,SendReportTo, SSN, DrivingLicense, StateId)  " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                                "?, ?, ?, " +
                                "?, ?, ?, ?, ?, ?, ?, " +
                                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(),?,?,?,0,?, ?,?,?) ");
                MainReceipt.setString(1, FirstName);
                MainReceipt.setString(2, LastName);
                MainReceipt.setString(3, MiddleInitial);
                MainReceipt.setString(4, gender);
                MainReceipt.setString(5, PhNumber);
                MainReceipt.setString(6, Email);
                MainReceipt.setString(7, Address);
                MainReceipt.setString(8, City);
                MainReceipt.setString(9, State);
                MainReceipt.setString(10, County);
                MainReceipt.setString(11, Ethnicity);
                MainReceipt.setString(12, Race);
                MainReceipt.setString(13, PhotoID);
                MainReceipt.setString(14, Test);
                MainReceipt.setString(15, AtTestSite);
                MainReceipt.setString(16, TesttingLoc);
                MainReceipt.setString(17, haveIns);
                MainReceipt.setString(18, RespParty);
                MainReceipt.setString(19, Carrier);
                MainReceipt.setString(20, GrpNumber);
                MainReceipt.setString(21, MemId);
                MainReceipt.setString(22, insuranceFront);
                MainReceipt.setString(23, insuranceBack);
                MainReceipt.setString(24, anySymp);
                MainReceipt.setString(25, SympFever);
                MainReceipt.setString(26, SympDiarrhea);
                MainReceipt.setString(27, SympHeadache);
                MainReceipt.setString(28, SympCongestion);
                MainReceipt.setString(29, SympShortBreath);
                MainReceipt.setString(30, SympBodyAches);
                MainReceipt.setString(31, SympChills);
                MainReceipt.setString(32, SympFatigue);
                MainReceipt.setString(33, SympSoreThroat);
                MainReceipt.setString(34, SympRunnyNose);
                MainReceipt.setString(35, SympDifficultBreathing);
                MainReceipt.setString(36, SympVomiting);
                MainReceipt.setString(37, SympLossTaste);
                MainReceipt.setString(38, vaccOrnot);
                MainReceipt.setString(39, suspecSymp);
                MainReceipt.setString(40, testPositive);
                MainReceipt.setString(41, healthCareEmp);
                MainReceipt.setString(42, atICU);
                MainReceipt.setString(43, atHosp);
                MainReceipt.setString(44, PregOrNot);
                MainReceipt.setString(45, atCongreCare);
                MainReceipt.setString(46, closeContact);
                MainReceipt.setString(47, massGather);
                MainReceipt.setString(48, askedBy);
                MainReceipt.setString(49, sympDate);
                MainReceipt.setInt(50, MRN);
                MainReceipt.setString(51, ExtendedMRN);
                MainReceipt.setString(52, "0");
                MainReceipt.setString(53, DOB);
                MainReceipt.setString(54, ZipCode);
                MainReceipt.setString(55, ClientIp);
                MainReceipt.setString(56, SendReportTo);

                MainReceipt.setString(57, SSN);
                MainReceipt.setString(58, DrivingLicense);
                MainReceipt.setString(59, StateID);
                //MainReceipt.setString(56, sampleType);
//                System.out.println("INSERTION *** " + MainReceipt.toString());
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (SaveData Insertion LabPatientRegistration^^" + facilityName + " ##MES#005)", servletContext, ex, "LabPatientRegistration", "SaveData", conn);
                Services.DumException("SaveData^^" + facilityName + " ##MES#005", "LabPatientRegistration ", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "LabPatientRegistration");
                Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
                Parser.SetField("Message", "MES#005");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }


            try {
                Query = "Select max(ID) from " + Database + ".PatientReg ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    PatientRegId = rset.getInt(1);
                rset.close();
                stmt.close();
            } catch (Exception e) {
//                out.println("Error 3- :" + e.getMessage());
            }


            Query = "SELECT Id FROM " + Database + ".DoctorsList " +
                    "WHERE LocationIdx='" + TesttingLoc + "' AND DefaultDoc=1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                DocIdx = rset.getInt(1);
            rset.close();
            stmt.close();

            Query = "UPDATE " + Database + ".PatientReg SET DoctorIdx='" + DocIdx + "' WHERE ID=" + PatientRegId;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            int seqNo = 0;
            try {
                Query = "Select IFNULL(MAX(Convert(Substring(OrderNum,11,9),UNSIGNED INTEGER)),0) + 1  " +
                        "from " + Database + ".TestOrder ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    seqNo = rset.getInt(1);
                rset.close();
                stmt.close();
            } catch (Exception e) {
//                out.println("Error 3- :" + e.getMessage());
            }

            String OrderId = "OI-" + MRN + "-" + seqNo;
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".TestOrder " +
                                "(PatRegIdx, OrderNum, OrderDate, OrderBy, Status, StageIdx) " +
                                " VALUES (?,?,NOW(),?,0,0) ");
                MainReceipt.setInt(1, PatientRegId);
                MainReceipt.setString(2, OrderId);
                MainReceipt.setString(3, "Web Registration");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (SaveData Insertion Order Table^^" + facilityName + " ##MES#006)", servletContext, ex, "LabPatientRegistration", "SaveData", conn);
                Services.DumException("SaveData^^" + facilityName + " ##MES#006", "LabPatientRegistration ", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "LabPatientRegistration");
                Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
                Parser.SetField("Message", "MES#006");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }

            int orderIdx = 0;
            try {
                Query = "Select max(Id) from " + Database + ".TestOrder ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    orderIdx = rset.getInt(1);
                rset.close();
                stmt.close();
            } catch (Exception e) {
//                out.println("Error 3- :" + e.getMessage());
            }

            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".Tests " +
                                "(OrderId, CreatedDate, CreatedBy, TestIdx, TestStageIdx," +
                                "CollectionDateTime,SampleNumber) " +
                                "VALUES (?,NOW(),'Web User',?,0,NOW(),?) ");
                MainReceipt.setInt(1, orderIdx);
                MainReceipt.setString(2, Test);
                MainReceipt.setString(3, sampleType);
                MainReceipt.executeUpdate();
                MainReceipt.close();
               /* if (Test.equals("3")) {
                    for (int i = 0; i < 2; i++) {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "INSERT INTO "+Database+".Tests " +
                                        "(OrderId, CreatedDate, CreatedBy, TestIdx, " +
                                        "TestStageIdx,sampleType) " +
                                        "VALUES (?,NOW(),'Web User',?,0,?) ");
                        MainReceipt.setInt(1, orderIdx);
                        MainReceipt.setInt(2, i + 1);
                        MainReceipt.setString(3, sampleType);
                        MainReceipt.executeUpdate();
                        MainReceipt.close();
                    }
                } else {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO "+Database+".Tests " +
                                    "(OrderId, CreatedDate, CreatedBy, TestIdx, TestStageIdx) " +
                                    "VALUES (?,NOW(),'Web User',?,0) ");
                    MainReceipt.setInt(1, orderIdx);
                    MainReceipt.setString(2, Test);
                    System.out.println("Test *** " + MainReceipt.toString());
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                }*/

            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (SaveData Insertion Order Table^^" + facilityName + " ##MES#007)", servletContext, ex, "LabPatientRegistration", "SaveData", conn);
                Services.DumException("SaveData^^" + facilityName + " ##MES#007", "LabPatientRegistration ", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "LabPatientRegistration");
                Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
                Parser.SetField("Message", "MES#007");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }


            try {
                if (!IDs.equals("")) {
                    ps = conn.prepareStatement("INSERT INTO " + Database + ".PatientDocUpload (UploadDocumentName,FileName,CreatedBy,CreatedDate,Status,PatientMRN,PatientRegId,PremisisId) VALUES (?,?,'PatientRegForm',NOW(),0,?,?,?)");
                    ps.setString(1, "ID Front");
                    ps.setString(2, IDs);
                    ps.setInt(3, MRN);
                    ps.setInt(4, PatientRegId);
                    ps.setInt(5, ClientIndex);
//                    System.out.println("IDs ->> QUERY ->> " + ps.toString());
                    ps.executeUpdate();
                    ps.close();
                }


                if (!InsuranceIDsF.equals("")) {
                    ps = conn.prepareStatement("INSERT INTO " + Database + ".PatientDocUpload (UploadDocumentName,FileName,CreatedBy,CreatedDate,Status,PatientMRN,PatientRegId,PremisisId) VALUES (?,?,'PatientRegForm',NOW(),0,?,?,?)");
                    ps.setString(1, "Insurance ID Front");
                    ps.setString(2, InsuranceIDsF);
                    ps.setInt(3, MRN);
                    ps.setInt(4, PatientRegId);
                    ps.setInt(5, ClientIndex);
                    ps.executeUpdate();
                    ps.close();
                }
                if (!InsuranceIDsB.equals("")) {
                    ps = conn.prepareStatement("INSERT INTO " + Database + ".PatientDocUpload (UploadDocumentName,FileName,CreatedBy,CreatedDate,Status,PatientMRN,PatientRegId,PremisisId) VALUES (?,?,'PatientRegForm',NOW(),0,?,?,?)");
                    ps.setString(1, "Insurance ID Back");
                    ps.setString(2, InsuranceIDsB);
                    ps.setInt(3, MRN);
                    ps.setInt(4, PatientRegId);
                    ps.setInt(5, ClientIndex);
                    ps.executeUpdate();
                    ps.close();
                }
            } catch (SQLException e) {
                System.out.println("PatientDocUpload ROVER LAB Error ");
                System.out.println(e.getMessage());
            }

            String PtEmail = null;
            String PtPhNumber = null;
            Query = "Select CONCAT(FirstName,' ',MiddleInitial,' ',LastName),id,Email,PhNumber from " + Database + ".PatientReg " +
                    "where MRN = " + MRN;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientName = rset.getString(1);
                PatientRegId = rset.getInt(2);
                PtEmail = rset.getString(3);
                PtPhNumber = rset.getString(4);
            }
            rset.close();
            stmt.close();

            String link = "Thank You " + PatientName + ". Your Registration number is " + MRN + " \n" +
                    "<a href=\"https://app1.rovermd.com:8443/md/people/" + MRN + "\">Verify User</a>";

//            String link = "Thank You " + PatientName + ". Your Registration number is " + MRN + " \n" +
//                    "<a href=\"https://app1.rovermd.com:8443/md/md.LabPatientRegistration?ActionID=updatePatientReg&MRN=" + MRN + "\">Verify User</a>";

//            TwilioSMSConfiguration smsConfiguration = new TwilioSMSConfiguration();
//            smsConfiguration.sendTwilioMessages_roverLab(request, conn, servletContext, Sms, ClientIndex, PtPhNumber, 202, Database);
            System.out.println("Sending an email from Reg Option ... ");
            helper.SendEmailRoverLab("", "RoverLab Covid Registration", link, PtEmail, conn, servletContext, MRN, PatientName, ClientId);
            String temp = SaveBundle(request, out, conn, Database, DirectoryName, PatientRegId);
//            out.print("temp "+ temp);
            String[] arr = temp.split("~");
            String FileName = arr[2];
            String outputFilePath = arr[1];
            String pageCount = arr[0];

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Thank You " + PatientName + ". Your Registration number is " + MRN + ". Press ok to proceed to sign the document.");
            Parser.SetField("MRN", "Registration Number: " + MRN);
            Parser.SetField("FormName", "LabPatientRegistration");
            Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex);
            Parser.SetField("pageCount", String.valueOf(pageCount));
            Parser.SetField("FileName", String.valueOf(FileName));
            Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
            Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("rqtype", rqType);
            Parser.SetField("UCID", UCID);
            Parser.SetField("OrderId", OrderId);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Exception/MessageRoverLab.html");


        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (SaveData Main Catch^^" + facilityName + " ##MES#014)", servletContext, ex, "LabPatientRegistration", "SaveData", conn);
            Services.DumException("SaveData^^" + facilityName + " ##MES#014", "LabPatientRegistration ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "LabPatientRegistration");
            Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
            Parser.SetField("Message", "MES#014");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void BundlePrimescope(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String Gender = "";
        String PhNumber = "";
        String Email = "";
        String Address = "";
        String City = "";
        String State = "";
        String County = "";
        String Ethnicity = "";
        String Race = "";
        String Test = "";
        String AtTestSite = "";
        String TestingLocation = "";
        String Insured = "";
        String RespParty = "";
        String CarrierName = "";
        String GrpNumber = "";
        String MemID = "";
        String ExtendedMRN = "";
        String Status = "";
        String CreatedDate = "";
        String EditBy = "";
        String Edittime = "";
        String DOB = "";
        String ZipCode = "";
        String FirstNameNoSpaces = "";

        final int ID = Integer.parseInt(request.getParameter("ID").trim());
        try {
            Query = "select date_format(now(),'%Y%m%d%H%i%s'), " +
                    "DATE_FORMAT(now(), '%m/%d/%Y'), DATE_FORMAT(now(), '%T')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DateTime = rset.getString(1);
                Date = rset.getString(2);
                Time = rset.getString(3);
            }
            rset.close();
            stmt.close();
            try {
                Query = " Select FirstName ,LastName ,MiddleInitial ,Gender ,PhNumber ," +
                        "Email ,Address ,City ,State ,County ,Ethnicity ,Race ,Test ," +
                        "AtTestSite ,TestingLocation ,Insured ,RespParty ,CarrierName ," +
                        "GrpNumber ,MemID ,ExtendedMRN ,Status ,CreatedDate ,EditBy ,Edittime ," +
                        "DOB ,ZipCode" +
                        "  From " + Database + ".PatientReg Where ID = " + ID;
//                System.out.println("QUERY BUNDLE " + Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    FirstName = rset.getString(1);
                    LastName = rset.getString(2);
                    MiddleInitial = rset.getString(3);
                    Gender = rset.getString(4);
                    PhNumber = rset.getString(5);
                    Email = rset.getString(6);
                    Address = rset.getString(7);
                    City = rset.getString(8);
                    State = rset.getString(9);
                    County = rset.getString(10);
                    Ethnicity = rset.getString(11);
                    Race = rset.getString(12);
                    Test = rset.getString(13);
                    AtTestSite = rset.getString(14);
                    TestingLocation = rset.getString(15);
                    Insured = rset.getString(16);
                    RespParty = rset.getString(17);
                    CarrierName = rset.getString(18);
                    GrpNumber = rset.getString(19);
                    MemID = rset.getString(20);
                    ExtendedMRN = rset.getString(21);
                    Status = rset.getString(22);
                    CreatedDate = rset.getString(23);
                    EditBy = rset.getString(24);
                    Edittime = rset.getString(25);
                    DOB = rset.getString(26);
                    ZipCode = rset.getString(27);
                }
                rset.close();
                stmt.close();


//                Query = "Select PayerName from oe.ProfessionalPayers where Id = " + PriInsuranceName;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PriInsuranceName = rset.getString(1);
//                }
//                rset.close();
//                stmt.close();

            } catch (Exception e) {
                out.println("Error In PateintReg:--" + e.getMessage());
                out.println(Query);
            }
//            if (SelfPayChk == 1) {
            String UID = "";
            com.itextpdf.text.Image SignImages = null;
            com.itextpdf.text.Image SignImages2 = null;
            File tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_0_" + ID + "_P.png");
            boolean exists = tmpDir.exists();
            out.print("exists " + exists);
            if (exists) {
                Query = "Select UID from " + Database + ".SignRequest where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    UID = rset.getString(1);
                }
                rset.close();
                stmt.close();

                SignImages = com.itextpdf.text.Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_0_" + ID + "_P.png");
                SignImages.scaleAbsolute(80.0f, 30.0f);
                tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_1_" + ID + "_HPC.png");
                exists = tmpDir.exists();
                out.print("exists " + exists);
                if (exists) {
                    SignImages2 = com.itextpdf.text.Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_1_" + ID + "_HPC.png");
                    SignImages2.scaleAbsolute(80.0f, 30.0f);
                }
            } else {
                SignImages = null;
                SignImages2 = null;
            }

            String inputFilePath = null;
            try {
                inputFilePath = "";
                final InetAddress ip = InetAddress.getLocalHost();
                final String hostname = ip.getHostName();
                if (hostname.trim().equals("rover-01")) {
                    inputFilePath = "";
                } else {
                    inputFilePath = "/sftpdrive";
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            //inputFilePath += "/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/COVID_REG.pdf";
            inputFilePath += "/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/COVID_REGNEW.pdf";

            FirstNameNoSpaces = FirstName.replaceAll("\\s+", "");

            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf";
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
//            final GenerateBarCode barCode = new GenerateBarCode();
//            final String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
//            final Image image = Image.getInstance(BarCodeFilePath);
//            image.scaleAbsolute(150.0f, 30.0f);
            // loop on all the PDF pages
            // i is the pdfPageNumber
//            out.println("inputFilePath -> "+inputFilePath);
//            out.println("outputFilePath -> "+outputFilePath);

            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    //////***************************** NEW FORM ADDITION 02-03-2022 TABISH ************////
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 710); // set x and y co-ordinates
                    pdfContentByte.showText("Tabish"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190, 710); // set x and y co-ordinates
                    pdfContentByte.showText("Hafeez"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(280, 710); // set x and y co-ordinates
                    pdfContentByte.showText("S"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 710); // set x and y co-ordinates
                    pdfContentByte.showText("Primescope Diagnostics"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80, 685); // set x and y co-ordinates
                    pdfContentByte.showText("17154 butte creek road TX, houston, United States"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(370, 685); // set x and y co-ordinates
                    pdfContentByte.showText("Dr, Joel Persall"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500, 685); // set x and y co-ordinates
                    pdfContentByte.showText("123456789"); // NPI
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80, 665); // set x and y co-ordinates
                    pdfContentByte.showText("Houston"); // City
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170, 665); // set x and y co-ordinates
                    pdfContentByte.showText("TX"); // State
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250, 665); // set x and y co-ordinates
                    pdfContentByte.showText("779900"); // ZipCode
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(370, 665); // set x and y co-ordinates
                    pdfContentByte.showText("11822 Westheimer Rd, Houston, TX 77077"); // Primescope Address
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 648); // set x and y co-ordinates
                    pdfContentByte.showText("4694980033"); // Ph
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170, 648); // set x and y co-ordinates
                    pdfContentByte.showText("01/25/1992"); // DOB
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(265, 648); // set x and y co-ordinates
                    pdfContentByte.showText("Male"); // Gender
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(345, 648); // set x and y co-ordinates
                    pdfContentByte.showText("Houston"); // City
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430, 648); // set x and y co-ordinates
                    pdfContentByte.showText("Texas"); // State
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(505, 648); // set x and y co-ordinates
                    pdfContentByte.showText("698741"); // Zip
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 630); // set x and y co-ordinates
                    pdfContentByte.showText("Asian"); // Race
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(145, 624); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Ethnicity
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(215, 624); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Ethnicity
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(144, 614); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Ethnicity
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(190, 614); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Ethnicity
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(345, 622); // set x and y co-ordinates
                    pdfContentByte.showText("789654123"); // Phone
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460, 622); // set x and y co-ordinates
                    pdfContentByte.showText("789654123"); // Fax
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 603); // set x and y co-ordinates
                    pdfContentByte.showText("tabish.hafeez@fam-llc.com"); // Email
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(353, 603); // set x and y co-ordinates
                    pdfContentByte.showText("tabish.hafeez@fam-llc.com"); // Email
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(229, 570); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // have Insurance (YES)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(254, 570); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // have Insurance (NO)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80, 555); // set x and y co-ordinates
                    pdfContentByte.showText("BCBS"); // Insurance
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(490, 552); // set x and y co-ordinates
                    pdfContentByte.showText("999999999"); // SSN
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(490, 538); // set x and y co-ordinates
                    pdfContentByte.showText("999999999"); // DL
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(490, 523); // set x and y co-ordinates
                    pdfContentByte.showText("999999999"); // StateID
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 530); // set x and y co-ordinates
                    pdfContentByte.showText("TABISH HAFEEZ"); // Policy Holder Name
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170, 530); // set x and y co-ordinates
                    pdfContentByte.showText("01/25/1992"); // Policy Holder DOB
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(270, 530); // set x and y co-ordinates
                    pdfContentByte.showText("65874"); // Member ID
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 508); // set x and y co-ordinates
                    pdfContentByte.showText("SELF"); // Relationship
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150, 508); // set x and y co-ordinates
                    pdfContentByte.showText("UYTRW"); // Policy Type
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260, 508); // set x and y co-ordinates
                    pdfContentByte.showText("GRP-0987"); // Group
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(36, 452); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Symptomatic
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(63, 452); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Symptomatic
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(87, 452); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Symptomatic
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(366, 480); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // First Test(YES)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(328, 469); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // First Test(NO)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(350, 469); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // First Test(UNKNOWN)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(410, 472); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Employed in Healthcare(YES)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(435, 472); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Employed in Healthcare(No)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(457, 472); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Employed in Healthcare(UNKNOWN)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(548, 482); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Pregnant(Yes)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(512, 472); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Pregnant(No)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(533, 472); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Pregnant(Unknown)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(401, 455); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // ICU for COVID-19(Yes)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(426, 455); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // ICU for COVID-19(No)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(448, 455); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // ICU for COVID-19(Unknown)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(203, 445); // set x and y co-ordinates
                    pdfContentByte.showText("1"); // Symptomatic(DATE)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(212, 445); // set x and y co-ordinates
                    pdfContentByte.showText("2"); // Symptomatic(DATE)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(223, 445); // set x and y co-ordinates
                    pdfContentByte.showText("0"); // Symptomatic(DATE)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(232, 445); // set x and y co-ordinates
                    pdfContentByte.showText("2"); // Symptomatic(DATE)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(243, 445); // set x and y co-ordinates
                    pdfContentByte.showText("2"); // Symptomatic(DATE)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(252, 445); // set x and y co-ordinates
                    pdfContentByte.showText("2"); // Symptomatic(DATE)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(160, 432); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Resident in congregate(YES)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(185, 432); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Resident in congregate(NO)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(208, 432); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Resident in congregate(Unknown)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(429, 432); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Hospitalized for COVID-19(Yes)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(454, 432); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Hospitalized for COVID-19(No)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(478, 432); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Hospitalized for COVID-19(Unknown)
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(40, 360); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Patient Has Insurance Coverage
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(40, 297); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // Patient Does Not Have Insurance Coverage
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 236); // set x and y co-ordinates
                    pdfContentByte.showText("Tabish Hafeez"); // Name
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(300, 236); // set x and y co-ordinates
                    pdfContentByte.showText("Tabish Hafeez"); // Signature
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500, 236); // set x and y co-ordinates
                    pdfContentByte.showText("20/05/2022"); // Date
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 107); // set x and y co-ordinates
                    pdfContentByte.showText("Test#123"); // Specimen ID
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150, 107); // set x and y co-ordinates
                    pdfContentByte.showText("02/25/2022 01:05:58"); // Specimen ID
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150, 107); // set x and y co-ordinates
                    pdfContentByte.showText("02/25/2022 01:05:58"); // Date & Time Collected
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(450, 107); // set x and y co-ordinates
                    pdfContentByte.showText("02/25/2022 01:05:58"); // Date & Time
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 68); // set x and y co-ordinates
                    pdfContentByte.showText("COVID-19 PCT Alina"); // Test Type
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(215, 68); // set x and y co-ordinates
                    pdfContentByte.showText("02/25/2022 01:05:58"); // Date & Time Collected
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(315, 68); // set x and y co-ordinates
                    pdfContentByte.showText("02/25/2022 01:05:58"); // Date of Onset
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(415, 68); // set x and y co-ordinates
                    pdfContentByte.showText(" "); // Result
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(427, 155); // set x and y co-ordinates
                    pdfContentByte.showText("02/25/2022 01:05:58"); // MEDICAL NECESSITY Date
                    pdfContentByte.endText();
                    //////***************************** NEW FORM ADDITION 02-03-2022 TABISH ************////
                    /*
                    /////////////////////////////////////Patient Information//////////////////////////////////////////////////
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 645); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName);//"First Name"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(175, 645); // set x and y co-ordinates
                    pdfContentByte.showText(LastName);//"Last Name"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(270, 645); // set x and y co-ordinates
                    pdfContentByte.showText(MiddleInitial);//"Middle"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340, 640); // set x and y co-ordinates
                    pdfContentByte.showText(Test);//"Test"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 615); // set x and y co-ordinates
                    pdfContentByte.showText(Address);//"Address"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340, 615); // set x and y co-ordinates
                    pdfContentByte.showText(AtTestSite);//"Yes / NO"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 600); // set x and y co-ordinates
                    pdfContentByte.showText(City);//"City"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160, 600); // set x and y co-ordinates
                    pdfContentByte.showText(State);//"State"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250, 600); // set x and y co-ordinates
                    pdfContentByte.showText(ZipCode);//"Zip"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(345, 585); // set x and y co-ordinates
                    pdfContentByte.showText(TestingLocation);//"testing Location"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 590); // set x and y co-ordinates
                    pdfContentByte.showText(PhNumber);//"Phone"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(155, 590); // set x and y co-ordinates
                    pdfContentByte.showText(DOB);//"DOB"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260, 590); // set x and y co-ordinates
                    pdfContentByte.showText(Gender);//"Gender"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 580); // set x and y co-ordinates
                    pdfContentByte.showText(Ethnicity);//"Ethnicity"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(165, 580); // set x and y co-ordinates
                    pdfContentByte.showText(Race);//"Race"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 570); // set x and y co-ordinates
                    pdfContentByte.showText(Email);//"Email"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(375, 540); // set x and y co-ordinates
                    pdfContentByte.showText(Insured);//"Yes / No"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 515); // set x and y co-ordinates
                    pdfContentByte.showText(RespParty);//"Responsible Party"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200, 521); // set x and y co-ordinates
                    pdfContentByte.showText(CarrierName);//"Carier Name"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340, 521); // set x and y co-ordinates
                    pdfContentByte.showText(GrpNumber);//"Group#"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(497, 525); // set x and y co-ordinates
                    pdfContentByte.showText(MemID);//"Member#"); // add the text
                    pdfContentByte.endText();


                    if (Insured.equals("Yes")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(27.5f, 488); // set x and y co-ordinates
                        pdfContentByte.showText("*"); //If Insurance
                        pdfContentByte.endText();
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(27.5f, 407); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.RED);
                    pdfContentByte.setTextMatrix(27.5f, 386); // set x and y co-ordinates
                    pdfContentByte.showText("*"); // add the text
                    pdfContentByte.endText();


                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100, 320);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420, 320); // set x and y co-ordinates
                    pdfContentByte.showText(Date); // add the text
                    pdfContentByte.endText();
                    */

                }
            }
//            out.println("Printing Done");
            pdfStamper.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }


        } catch (Exception e) {
//            out.println(e.getMessage());
            System.out.println(e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            System.out.println(str);
        }
    }

    void SignPdf(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId, String DirectoryName, UtilityHelper helper) {
        try {
            String Query = "";
            Statement stmt = null;
            ResultSet rset = null;
            int MRN = 0;
            String PatientName = "";
            String AUTHID = "";
            String SendType = "1";
            InetAddress ip = InetAddress.getLocalHost();
            long unixTime = System.currentTimeMillis() / 1000L;
            UUID uuid = UUID.randomUUID();
            String pageCount = request.getParameter("pageCount");
            String outputFilePath = request.getParameter("outputFilePath");
            String FileName = request.getParameter("FileName");
            String OrderId = request.getParameter("OrderId");
            int PatientRegId = Integer.parseInt(request.getParameter("PatientRegId"));
            String rqType = null;
            if (request.getParameter("rqtype1") != null) {
                rqType = request.getParameter("rqtype1");
            }
            int found = 0;
            int isExist = 0;

            try {
                Query = "SELECT MRN FROM " + Database + ".PatientReg WHERE Id = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    MRN = rset.getInt(1);
                rset.close();
                stmt.close();
            } catch (Exception Ex) {
                Services.DumException("LabPatientRegistration - SignPDF", "PatientRegIdx", request, Ex, getServletContext());
            }
            try {
                Query = "Select Count(*) from " + Database + ".SignRequest where PatientRegId = " + PatientRegId + " AND isSign = 1";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    found = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception Ex) {

                Services.DumException("LabPatientRegistration ", "signPDFCheck", request, Ex, getServletContext());
            }
            try {
                Query = "Select Count(*) from " + Database + ".SignRequest where PatientRegId = " + PatientRegId + " AND isSign = 1";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    isExist = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception Ex) {
                Services.DumException("LabPatientRegistration ", "signPDFCheck", request, Ex, getServletContext());
            }
            if (found > 0) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "This Bundle is already been signed!");
                Parser.SetField("FormName", "DownloadBundle");
                Parser.SetField("ActionID", "GETINPUTERDallas&ID=" + PatientRegId);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                return;
            }

            if (isExist > 0) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Bundle has already been sent for Signing");
                Parser.SetField("FormName", "DownloadBundle");
                Parser.SetField("ActionID", "GETINPUTERDallas&ID=" + PatientRegId);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                return;
            }

            StringBuffer Style = new StringBuffer();
            StringBuffer ulTag = new StringBuffer();
            PDFtoImages pdftoImage = new PDFtoImages();
            new HashMap();
            HashMap<Integer, String> images_Map_final = pdftoImage.GetValues(request, out, conn, Database, ClientId, outputFilePath, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/md/tmpImages/");
            Collection<String> values = images_Map_final.values();
            java.util.List<String> imagelist = new ArrayList(values);

            for (int i = 0; i < imagelist.size(); ++i) {
                Style.append(".desktop-image" + (i + 1) + " {\n\tbackground-image: url(" + (String) imagelist.get(i) + ");\n\tbackground-size: cover;\n\tbackground-position: center;\n\twidth: 600px;\n\theight: 777px;\n\tmargin: 0 auto;\n\tborder: 5px solid #0f0f10;\n\tposition: relative;\n}\n\n.desktop-image" + (i + 1) + "> #Sign" + (i + 1) + "{\n\t\n\tposition: relative;\n\ttop: 67%;\n\tleft: 48%;\n\t/*transform: translate(50%, -50%);*/\n}");
                ulTag.append("<div id=\"page" + (i + 1) + "\" class=\"images\"><div  class=\"desktop-image" + (i + 1) + "\"><button type=\"button\" class=\"btn btn-primary btn-sm\" id=\"Sign" + (i + 1) + "\"  onclick=\"signhere(" + (i + 1) + ", this.id);\" class=\"mobile-image-stella\">Patients Sign</button>\n<img id=\"canvasimg" + (i + 1) + "\" style=\"display:none;background-color:rgba(255,255,255,0.6);\">\n\n</div>\n</div>\n");
            }

//          Insert Data in the SignRequest Table here.
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".SignRequest (MRN,Status,isSign,IP,CreatedBy,CreatedDate," +
                                " SendType,SignBy,UID, AUTHID, PatientRegId) VALUES (?,?,?,?,?,now(),?,?,?,?,?) ");
                MainReceipt.setInt(1, MRN);
                MainReceipt.setInt(2, 0);
                MainReceipt.setInt(3, 0);
                MainReceipt.setString(4, ip.toString());
                MainReceipt.setString(5, UserId);
                MainReceipt.setString(6, SendType);
                MainReceipt.setString(7, "");
                MainReceipt.setString(8, uuid.toString());
                MainReceipt.setString(9, "");
                MainReceipt.setInt(10, PatientRegId);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in Sign Print ^^ ** (SignPDF)", servletContext, e, "SignPrint", "SignPDF", conn);
                Services.DumException("SignPDF", "SignPDF", request, e, getServletContext());
                return;
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
            Parser.SetField("imagelist", String.valueOf(imagelist));
            Parser.SetField("ulTag", String.valueOf(ulTag));
            Parser.SetField("Style", String.valueOf(Style));
            Parser.SetField("pageCount", String.valueOf(pageCount));
            Parser.SetField("FileName", String.valueOf(FileName));
            Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
            Parser.SetField("UID", String.valueOf(uuid));
            Parser.SetField("MRN", String.valueOf(MRN));
            Parser.SetField("rqtype", rqType);
            Parser.SetField("OrderId", OrderId);

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/SigningBundleRoverLab.html");

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Sign Print ^^ ** (SignPDF)", servletContext, e, "SignPrint", "SignPDF", conn);
            Services.DumException("SignPDF", "SignPDF", request, e, getServletContext());
        }


    }

    void GetData(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, String DirectoryName) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        String Message = "";
        String imagedataURL = "";
        String PatientRegId = "";
        boolean FileFound = false;
        byte[] Data = null;
        String key = "";
        String UID = "";
        String MRN = "";
        String pageCount = "";
        String Order = "";
        String outputFilePath = "";
        String rqType = "";
        String WEB = "";
        String OrderId = "";
        Boolean isSelfPay = false;
        try {
            Dictionary d = doUpload(request, response, out);
            Enumeration en = d.keys();
            while (en.hasMoreElements()) {
                key = (String) en.nextElement();

                //System.out.println("key ->> " + key);
                if (key.startsWith("PatientRegId")) {
                    PatientRegId = (String) d.get(key);
                } else if (key.startsWith("imagedataURLbtnIdHdn")) {
                    imagedataURL = (String) d.get(key);
                } else if (key.startsWith("UID")) {
                    UID = (String) d.get(key);
                } else if (key.startsWith("MRN")) {
                    MRN = (String) d.get(key);
                } else if (key.startsWith("pageCount")) {
                    pageCount = (String) d.get(key);
                } else if (key.startsWith("Order") && key.endsWith("er")) {
                    Order = (String) d.get(key);
                } else if (key.startsWith("outputFilePath")) {
                    outputFilePath = (String) d.get(key);
                } else if (key.startsWith("rqtype1")) {
                    rqType = (String) d.get(key);
                } else if (key.startsWith("OrderId")) {
//                    System.out.println("ORDERID- >> "+(String) d.get(key));
                    OrderId = (String) d.get(key);
                }
            }
            PatientRegId = PatientRegId.substring(4);
            imagedataURL = imagedataURL.substring(4);
            UID = UID.substring(4);
            MRN = MRN.substring(4);
            pageCount = pageCount.substring(4);
            Order = Order.substring(4);
            outputFilePath = outputFilePath.substring(4);
            OrderId = OrderId.substring(4);
//            System.out.println("OrderId ->> "+OrderId);


            String[] imageURL = imagedataURL.split("\\~");
            String[] Ordering = Order.split("\\~");

            BufferedImage image = null;
            byte[] imageByte;
            for (int i = 0; i < imageURL.length; i++) {
//                out.println(imageURL[i]);
                try {
                    byte[] imagedata = DatatypeConverter.parseBase64Binary(imageURL[i].substring(imageURL[i].indexOf(",") + 1));
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagedata));
                    //ImageIO.write(bufferedImage, "png", new File("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_" + i + "_" + PatientRegId + ".png"));
                    ImageIO.write(bufferedImage, "png", new File("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_" + i + "_" + PatientRegId + "_" + Ordering[i] + ".png"));

//                    out.println("Result => "+ isValid(new File("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_" + i + "_" + PatientRegId + "_"+Ordering[i]+".png")));// isValid(in);
//                    out.print("HEre ");
                    //String ImageTransparent = this.MakeTransparent(out, "/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_" + i + "_" + PatientRegId + ".png", "/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_" + i + "_" + PatientRegId + ".png");
                    if (isValid(new File("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_" + i + "_" + PatientRegId + "_" + Ordering[i] + ".png"))) {
                        String ImageTransparent = this.MakeTransparent(out, "/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_" + i + "_" + PatientRegId + "_" + Ordering[i] + ".png", "/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_" + i + "_" + PatientRegId + "_" + Ordering[i] + ".png");
                        if (ImageTransparent.trim().toUpperCase().equals("CONVERTED")) {
                            Message = " and Transparency DONE";
                        } else {
                            Message = " and Image Created";
                        }
                    } else {
                        Query = "DELETE FROM " + Database + ".SignRequest WHERE PatientRegId = '" + PatientRegId + "' ";
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();

                        Parsehtm Parser = new Parsehtm(request);
                        Parser.SetField("Message", "Invalid Signature, Please Try Again!");
                        Parser.SetField("MRN", "Invalid Signature");
//                        Parser.SetField("FormName", "PatientReg");
//                        Parser.SetField("ActionID", "GetValues&ClientIndex=36");
                        Parser.SetField("pageCount", String.valueOf(pageCount));
                        Parser.SetField("FileName", String.valueOf(outputFilePath));
                        Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
                        Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
                        Parser.SetField("ClientIndex", "36");
                        Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/MessageRoverLab.html");
                        return;
                    }
//                    out.print("\n After ");


                } catch (IOException e) {
                    out.println("Error in IO" + e.getStackTrace());
                }
            }

            Query = "UPDATE " + Database + ".SignRequest SET isSign = 1 , SignBy = '" + UserId + "', SignTime = NOW() " +
                    "WHERE PatientRegId = " + PatientRegId + " AND " +
                    "MRN = " + MRN + " AND UID = '" + UID + "' ";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            PreparedStatement ps = conn.prepareStatement("SELECT website from oe.ClientsWebsite where clientID=?");
            ps.setInt(1, ClientId);
            rset = ps.executeQuery();
            if (rset.next()) {
                WEB = rset.getString(1);
            }
            ps.close();
            rset.close();

            ps = conn.prepareStatement("SELECT Insured FROM " + Database + ".PatientReg where ID=?");
            ps.setString(1, PatientRegId);
            rset = ps.executeQuery();
            if (rset.next()) {
                if (rset.getString(1).toUpperCase().equals("NO"))
                    isSelfPay = true;
            }
            rset.close();
            ps.close();
//
            if (isSelfPay) {
                Parsehtm Parser = new Parsehtm(request);
//                Parser.SetField("Message", "Done! Signed PDF is Ready " + Message);
                Parser.SetField("Message", "Thank You for Registration");
                Parser.SetField("FormName", "LabPatientRegistration");
                Parser.SetField("ActionID", "PayNow&i=" + MRN + "&j=" + rqType + "&k=" + OrderId);
                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message_ROVERLABPayment.html");
                return;
            }


//                System.out.println("**** RQ TYPE **** " + rqType);
//                Parser.SetField("Message", "Done! Signed PDF is Ready " + Message);
//                Parser.SetField("FormName", "DownloadBundle");
//                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message_W.html");            if(
//            System.out.println("Just before successs -->> rqType " + rqType);
            if (rqType.equals("nullGetValues")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Thank You for Registration ");
                Parser.SetField("WEB", WEB);
                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message_RoverLab.html");
            } else {
                Parsehtm Parser = new Parsehtm(request);
//                Parser.SetField("Message", "Done! Signed PDF is Ready " + Message);
                Parser.SetField("Message", "Document has been signed successfully! ");
                Parser.SetField("FormName", "LabPatientRegistration");
                Parser.SetField("ActionID", "GetValues");
                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message_ROVERLAB.html");
            }


        } catch (Exception var11) {

            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
    }

    String MakeTransparent(PrintWriter out, String inputFileName, String outputFileName) {

        try {
//            String inputFileName = "C:\\Users\\abid_\\Desktop\\download.png";
//            int decimalPosition = inputFileName.lastIndexOf(".");
//            String outputFileName = inputFileName+ ".png";

            //out.println("Copying file " + inputFileName + " to " + outputFileName);

            File in = new File(inputFileName);


            BufferedImage source = ImageIO.read(in);

            int color = source.getRGB(0, 0);
            Image imageWithTransparency = makeColorTransparent(source, new Color(color));

            BufferedImage transparentImage = imageToBufferedImage(imageWithTransparency);

            File file = new File(outputFileName);
            ImageIO.write(transparentImage, "PNG", file);

        } catch (Exception e) {
            out.println(e.getMessage());
        }
        return "CONVERTED";
    }

    private Dictionary doUpload(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws Exception {
        try {
            UUID uuid = UUID.randomUUID();
            String boundary = request.getHeader("Content-Type");
            int pos = boundary.indexOf('=');
            boundary = boundary.substring(pos + 1);
            boundary = "--" + boundary;
            byte[] bytes = new byte[512];
            int state = 0;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            String name = null;
            String value = null;
            String filename = null;
            String contentType = null;
            Dictionary<Object, Object> fields = new Hashtable<>();
            ServletInputStream in = request.getInputStream();
            int i = in.readLine(bytes, 0, 512);
            for (; -1 != i; i = in.readLine(bytes, 0, 512)) {
                String st = new String(bytes, 0, i);
                if (st.startsWith(boundary)) {
                    state = 0;
                    if (name != null) {

//                        System.out.println("NAME *** " + name);
//                        System.out.println("filename *** " + filename);

                        if (value != null) {
                            fields.put(name, value.substring(0, value.length() - 2));
                        } else if (buffer.size() > 2) {
                            uuid = UUID.randomUUID();
                            filename = filename.replaceAll("\\s+", "");
                            fields.put(name + uuid + filename, buffer);
                        }
                        name = null;
                        value = null;
                        filename = null;
                        contentType = null;
                        buffer = new ByteArrayOutputStream();
                    }
                } else if (st.startsWith("Content-Disposition: form-data") && state == 0) {
                    StringTokenizer tokenizer = new StringTokenizer(st, ";=\"");
                    while (tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        if (token.startsWith(" name")) {
                            name = tokenizer.nextToken();
                            state = 2;
                            continue;
                        }
                        if (token.startsWith(" filename")) {
                            filename = tokenizer.nextToken();
                            StringTokenizer ftokenizer = new StringTokenizer(filename, "\\/:");
                            filename = ftokenizer.nextToken();
                            while (ftokenizer.hasMoreTokens())
                                filename = ftokenizer.nextToken();
                            state = 1;
                            break;
                        }
                    }
                } else if (st.startsWith("Content-Type") && state == 1) {
                    pos = st.indexOf(":");
                    st.substring(pos + 2, st.length() - 2);
                } else if (st.equals("\r\n") && state == 1) {
                    state = 3;
                } else if (st.equals("\r\n") && state == 2) {
                    state = 4;
                } else if (state == 4) {
                    value = String.valueOf(String.valueOf(value)) + st;
                } else if (state == 3) {
                    buffer.write(bytes, 0, i);
                }
            }
            return fields;
        } catch (Exception var20) {
            System.out.println("Error in Do Upload!!");
            String str = "";
            for (int i = 0; i < (var20.getStackTrace()).length; i++)
                str = str + var20.getStackTrace()[i] + "<br>";
            System.out.println(str);
            throw new Exception("Error In Do Upload " + var20.getMessage());
        }
    }

    String SaveBundle(final HttpServletRequest request, final PrintWriter out, final Connection conn, final String Database, final String DirectoryName, int IDpatient) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String Gender = "";
        String PhNumber = "";
        String Email = "";
        String Address = "";
        String City = "";
        String State = "";
        String County = "";
        String Ethnicity = "";
        String Race = "";
        String Test = "";
        String AtTestSite = "";
        String TestingLocation = "";
        String Insured = "";
        String RespParty = "";
        String CarrierName = "";
        String GrpNumber = "";
        String MemID = "";
        String ExtendedMRN = "";
        String Status = "";
        String CreatedDate = "";
        String EditBy = "";
        String Edittime = "";
        String DOB = "";
        String ZipCode = "";
        String FirstNameNoSpaces = "";
        String sampleType = "";

        String HaveSymptoms = "";
        String DateOfSymp = "";
        String FirstTimeCovid = "";
        String HealthCareEmp = "";
        String IsPregnant = "";
        String InICU = "";
        String InHospital = "";
        String IsResident = "";
        String formattedDateTime = "";
        String DateOfSympMonthh1 = "";
        String DateOfSympMonthh2 = "";
        String DateOfSympDayy1 = "";
        String DateOfSympDayy2 = "";
        String DateOfSympYearr1 = "";
        String DateOfSympYearr2 = "";

        String SSN = "";
        String DrivingLicense = "";
        String StateID = "";

        final int ID = IDpatient;//Integer.parseInt(request.getParameter("ID").trim());
//        out.println("ID -> "+ID);
        try {
            Query = "select date_format(now(),'%Y%m%d%H%i%s'), DATE_FORMAT(now(), '%m/%d/%Y'), " +
                    "DATE_FORMAT(now(), '%T'),date_format(now(),'%m/%d/%Y %H:%i%:%s')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DateTime = rset.getString(1);
                Date = rset.getString(2);
                Time = rset.getString(3);
                formattedDateTime = rset.getString(4);
            }
            rset.close();
            stmt.close();
            try {
/*                Query = " Select a.FirstName ,a.LastName ,a.MiddleInitial ,a.Gender ,a.PhNumber ,a.Email ," +
                        "a.Address ,a.City ,a.State ,a.County ,a.Ethnicity ,a.Race ,b.TestName ,a.AtTestSite ," +
                        "c.Location ,a.Insured ,a.RespParty ,a.CarrierName ,a.GrpNumber ,a.MemID ," +
                        "a.ExtendedMRN ,a.Status ,a.CreatedDate ,a.EditBy ,a.Edittime ,a.DOB ,a.ZipCode" +
                        "  From " + Database + ".PatientReg a " +
                        " INNER JOIN " + Database + ".ListofTests b ON a.Test = b.Id " +
                        " INNER JOIN " + Database + ".Locations c ON a.TestingLocation = c.Id " +
                        " WHERE a.ID = " + ID;*/
                Query = " Select a.FirstName ,a.LastName ,a.MiddleInitial ,a.Gender ,a.PhNumber ,a.Email ," +
                        "a.Address ,a.City ,a.State ,a.County ,a.Ethnicity ,a.Race ,b.TestName ,a.AtTestSite ," +
                        "c.Location ,a.Insured ,a.RespParty ,a.CarrierName ,a.GrpNumber ,a.MemID ," +
                        "a.ExtendedMRN ,a.Status ,a.CreatedDate ,a.EditBy ,a.Edittime ,a.DOB ,a.ZipCode," +
                        "a.HaveSymptoms,a.DateOfSymp,a.FirstTimeCovid,a.HealthCareEmp,a.IsPregnant,a.InICU," +
                        "a.InHospital,a.sampleType,a.IsResident," +
                        "SUBSTRING(DATE_FORMAT(a.DateOfSymp,'%m'),1,1) AS Monthh1, \n" +
                        "SUBSTRING(DATE_FORMAT(a.DateOfSymp,'%m'),2,1) AS Monthh2, \n" +
                        "SUBSTRING(DATE_FORMAT(a.DateOfSymp,'%d'),1,1) AS Dayy1, \n" +
                        "SUBSTRING(DATE_FORMAT(a.DateOfSymp,'%d'),2,1) AS Dayy1, \n" +
                        "SUBSTRING(DATE_FORMAT(a.DateOfSymp,'%y'),1,1) AS Yearr1,\n" +
                        "SUBSTRING(DATE_FORMAT(a.DateOfSymp,'%y'),2,1) AS Yearr2," +
                        "a.SSN,a.DrivingLicense,a.StateID " +
                        "  From " + Database + ".PatientReg a " +
                        " INNER JOIN " + Database + ".ListofTests b ON a.Test = b.Id " +
                        " INNER JOIN " + Database + ".Locations c ON a.TestingLocation = c.Id " +
                        " WHERE a.ID = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    FirstName = rset.getString(1);
                    LastName = rset.getString(2);
                    MiddleInitial = rset.getString(3);
                    Gender = rset.getString(4);
                    PhNumber = rset.getString(5);
                    Email = rset.getString(6);
                    Address = rset.getString(7);
                    City = rset.getString(8);
                    State = rset.getString(9);
                    County = rset.getString(10);
                    Ethnicity = rset.getString(11);
                    Race = rset.getString(12);
                    Test = rset.getString(13);
                    TestingLocation = rset.getString(15);
                    Insured = rset.getString(16);
                    RespParty = rset.getString(17);
                    CarrierName = rset.getString(18);
                    GrpNumber = rset.getString(19);
                    MemID = rset.getString(20);
                    CreatedDate = rset.getString(23);
                    DOB = rset.getString(26);
                    ZipCode = rset.getString(27);

                    HaveSymptoms = rset.getString(28);
                    DateOfSymp = rset.getString(29);
                    FirstTimeCovid = rset.getString(30);
                    HealthCareEmp = rset.getString(31);
                    IsPregnant = rset.getString(32);
                    InICU = rset.getString(33);
                    InHospital = rset.getString(34);
                    sampleType = rset.getString(35);
                    IsResident = rset.getString(36);
                    DateOfSympMonthh1 = rset.getString(37);
                    DateOfSympMonthh2 = rset.getString(38);
                    DateOfSympDayy1 = rset.getString(39);
                    DateOfSympDayy2 = rset.getString(40);
                    DateOfSympYearr1 = rset.getString(41);
                    DateOfSympYearr2 = rset.getString(42);

                    SSN = rset.getString(43);
                    DrivingLicense = rset.getString(44);
                    StateID = rset.getString(45);
                }
                rset.close();
                stmt.close();


//                try {
//                    Query = "Select PayerName from oe.ProfessionalPayers where Id = " + PriInsuranceName;
////                    out.print("Query => " + Query );
////                    out.print("PriInsuranceName => " + PriInsuranceName );
//                    stmt = conn.createStatement();
//                    rset = stmt.executeQuery(Query);
//                    if (rset.next()) {
//                        PriInsuranceName = rset.getString(1);
//                    }
//                    rset.close();
//                    stmt.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }

            } catch (Exception e) {
                out.println("Error In PateintRegROVERLAB SAVEBUNDLE:--" + e.getMessage());
//                out.println(Query);
            }
//            if (SelfPayChk == 1) {

            String inputFilePath = null;
            try {
                inputFilePath = "";
                final InetAddress ip = InetAddress.getLocalHost();
                final String hostname = ip.getHostName();
                if (hostname.trim().equals("rover-01")) {
                    inputFilePath = "";
                } else {
                    inputFilePath = "/sftpdrive";
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            //inputFilePath += "/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/COVID_REG.pdf";
            inputFilePath += "/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/COVID_REGNEW.pdf";

            FirstNameNoSpaces = FirstName.replaceAll("\\s+", "");
            String filename = FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf";
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
//            final GenerateBarCode barCode = new GenerateBarCode();
//            final String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
//            final Image image = Image.getInstance(BarCodeFilePath);
//            image.scaleAbsolute(150.0f, 30.0f);
            // loop on all the PDF pages
            // i is the pdfPageNumber
//            out.println("inputFilePath -> "+inputFilePath);
//            out.println("outputFilePath -> "+outputFilePath);


//            out.println("Before pdf");
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    //////***************************** NEW FORM ADDITION 02-03-2022 TABISH ************////
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 710); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName);//First Name
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190, 710); // set x and y co-ordinates
                    pdfContentByte.showText(LastName); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(280, 710); // set x and y co-ordinates
                    pdfContentByte.showText(MiddleInitial); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 710); // set x and y co-ordinates
                    pdfContentByte.showText("Primescope Diagnostics"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80, 685); // set x and y co-ordinates
                    pdfContentByte.showText(Address); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(370, 685); // set x and y co-ordinates
                    pdfContentByte.showText("Dr, Joel Persall"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500, 685); // set x and y co-ordinates
                    pdfContentByte.showText("123456789"); // NPI
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80, 665); // set x and y co-ordinates
                    pdfContentByte.showText(City); // City
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170, 665); // set x and y co-ordinates
                    pdfContentByte.showText(State); // State
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250, 665); // set x and y co-ordinates
                    pdfContentByte.showText(ZipCode); // ZipCode
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(370, 665); // set x and y co-ordinates
                    pdfContentByte.showText("11822 Westheimer Rd, Houston, TX 77077"); // Primescope Address
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 648); // set x and y co-ordinates
                    pdfContentByte.showText(PhNumber); // Ph
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170, 648); // set x and y co-ordinates
                    pdfContentByte.showText(DOB); // DOB
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(265, 648); // set x and y co-ordinates
                    pdfContentByte.showText(Gender); // Gender
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(345, 648); // set x and y co-ordinates
                    pdfContentByte.showText("Houston"); // City
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430, 648); // set x and y co-ordinates
                    pdfContentByte.showText("Texas"); // State
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(505, 648); // set x and y co-ordinates
                    pdfContentByte.showText("77077"); // Zip
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 630); // set x and y co-ordinates
                    pdfContentByte.showText(Race); // Race
                    pdfContentByte.endText();


                    if (Ethnicity.equals("Hispanic/Latino")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(145, 624); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Ethnicity
                        pdfContentByte.endText();
                    } else if (Ethnicity.equals("Non-Hispanic/Latino")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(215, 624); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Ethnicity
                        pdfContentByte.endText();
                    } else if (Ethnicity.equals("Unknown")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(144, 614); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Ethnicity
                        pdfContentByte.endText();
                    } else if (Ethnicity.equals("Not Given/Refused")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(190, 614); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Ethnicity
                        pdfContentByte.endText();
                    }


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(345, 622); // set x and y co-ordinates
                    pdfContentByte.showText("+1 832-243-4433"); // Phone
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460, 622); // set x and y co-ordinates
                    pdfContentByte.showText(" "); // Fax
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 603); // set x and y co-ordinates
                    pdfContentByte.showText(Email); // Email
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(353, 603); // set x and y co-ordinates
                    pdfContentByte.showText("info@primescopediagnostics.com"); // Email
                    pdfContentByte.endText();

                    if (Insured.equals("Yes")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(229, 570); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // have Insurance (YES)
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(80, 555); // set x and y co-ordinates
                        pdfContentByte.showText(" "); // Insurance
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(70, 530); // set x and y co-ordinates
                        pdfContentByte.showText(FirstName + " " + LastName); // Policy Holder Name
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(270, 530); // set x and y co-ordinates
                        pdfContentByte.showText(MemID); // Member ID
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(170, 530); // set x and y co-ordinates
                        pdfContentByte.showText(DOB); // Policy Holder DOB
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(70, 508); // set x and y co-ordinates
                        pdfContentByte.showText(RespParty); // Relationship
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(150, 508); // set x and y co-ordinates
                        pdfContentByte.showText(" "); // Policy Type
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(260, 508); // set x and y co-ordinates
                        pdfContentByte.showText(GrpNumber); // Group
                        pdfContentByte.endText();
                    } else if (Insured.equals("No")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(254, 570); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // have Insurance (NO)
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(80, 555); // set x and y co-ordinates
                        pdfContentByte.showText(" "); // Insurance
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(70, 530); // set x and y co-ordinates
                        pdfContentByte.showText(" "); // Policy Holder Name
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(270, 530); // set x and y co-ordinates
                        pdfContentByte.showText(" "); // Member ID
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(170, 530); // set x and y co-ordinates
                        pdfContentByte.showText(" "); // Policy Holder DOB
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(70, 508); // set x and y co-ordinates
                        pdfContentByte.showText(" "); // Relationship
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(150, 508); // set x and y co-ordinates
                        pdfContentByte.showText(" "); // Policy Type
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(260, 508); // set x and y co-ordinates
                        pdfContentByte.showText(" "); // Group
                        pdfContentByte.endText();

                    }

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(490, 552); // set x and y co-ordinates
                    pdfContentByte.showText(SSN); // SSN
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(490, 538); // set x and y co-ordinates
                    pdfContentByte.showText(DrivingLicense); // DL
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(490, 523); // set x and y co-ordinates
                    pdfContentByte.showText(StateID); // StateID
                    pdfContentByte.endText();


                    if (HaveSymptoms.equals("Yes")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(36, 452); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Symptomatic(YES)
                        pdfContentByte.endText();
                    } else if (HaveSymptoms.equals("No")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(63, 452); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Symptomatic(NO)
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(87, 452); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Symptomatic(UNKNOWN)
                        pdfContentByte.endText();
                    }

                    if (FirstTimeCovid.equals("Yes")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(366, 480); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // First Test(YES)
                        pdfContentByte.endText();
                    } else if (FirstTimeCovid.equals("No")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(328, 469); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // First Test(NO)
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(350, 469); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // First Test(UNKNOWN)
                        pdfContentByte.endText();
                    }

                    if (HealthCareEmp.equals("Yes")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(410, 472); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Employed in Healthcare(YES)
                        pdfContentByte.endText();
                    } else if (HealthCareEmp.equals("No")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(435, 472); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Employed in Healthcare(No)
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(457, 472); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Employed in Healthcare(UNKNOWN)
                        pdfContentByte.endText();
                    }

                    if (IsPregnant.equals("Yes")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(548, 482); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Pregnant(Yes)
                        pdfContentByte.endText();
                    } else if (IsPregnant.equals("No")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(512, 472); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Pregnant(No)
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(533, 472); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Pregnant(Unknown)
                        pdfContentByte.endText();
                    }

                    if (InICU.equals("Yes")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(401, 455); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // ICU for COVID-19(Yes)
                        pdfContentByte.endText();
                    } else if (InICU.equals("No")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(426, 455); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // ICU for COVID-19(No)
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(448, 455); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // ICU for COVID-19(Unknown)
                        pdfContentByte.endText();
                    }


                    if (HaveSymptoms.equals("Yes")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(203, 445); // set x and y co-ordinates
                        pdfContentByte.showText(DateOfSympMonthh1); // Symptomatic(DATE)
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(212, 445); // set x and y co-ordinates
                        pdfContentByte.showText(DateOfSympMonthh2); // Symptomatic(DATE)
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(223, 445); // set x and y co-ordinates
                        pdfContentByte.showText(DateOfSympDayy1); // Symptomatic(DATE)
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(232, 445); // set x and y co-ordinates
                        pdfContentByte.showText(DateOfSympDayy2); // Symptomatic(DATE)
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(243, 445); // set x and y co-ordinates
                        pdfContentByte.showText(DateOfSympYearr1); // Symptomatic(DATE)
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(252, 445); // set x and y co-ordinates
                        pdfContentByte.showText(DateOfSympYearr1); // Symptomatic(DATE)
                        pdfContentByte.endText();
                    }


                    if (IsResident.equals("Yes")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(160, 432); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Resident in congregate(YES)
                        pdfContentByte.endText();
                    } else if (IsResident.equals("No")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(185, 432); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Resident in congregate(NO)
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(208, 432); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Resident in congregate(Unknown)
                        pdfContentByte.endText();
                    }


                    if (InHospital.equals("Yes")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(429, 432); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Hospitalized for COVID-19(Yes)
                        pdfContentByte.endText();
                    } else if (InHospital.equals("No")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(454, 432); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Hospitalized for COVID-19(No)
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(478, 432); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Hospitalized for COVID-19(Unknown)
                        pdfContentByte.endText();
                    }


                    if (Insured.equals("Yes")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(40, 360); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Patient Has Insurance Coverage
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.RED);
                        pdfContentByte.setTextMatrix(40, 297); // set x and y co-ordinates
                        pdfContentByte.showText("*"); // Patient Does Not Have Insurance Coverage
                        pdfContentByte.endText();
                    }


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 236); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName + " " + LastName); // Name
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(300, 236); // set x and y co-ordinates
                    pdfContentByte.showText("Tabish Hafeez"); // Signature
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500, 236); // set x and y co-ordinates
                    pdfContentByte.showText(Date); // Date
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 107); // set x and y co-ordinates
                    pdfContentByte.showText(sampleType); // Specimen ID
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150, 107); // set x and y co-ordinates
                    pdfContentByte.showText(formattedDateTime); // Specimen ID
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150, 107); // set x and y co-ordinates
                    pdfContentByte.showText(formattedDateTime); // Date & Time Collected
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(450, 107); // set x and y co-ordinates
                    pdfContentByte.showText(formattedDateTime); // Date & Time
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 68); // set x and y co-ordinates
                    pdfContentByte.showText(Test); // Test Type
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(215, 68); // set x and y co-ordinates
                    pdfContentByte.showText(formattedDateTime); // Date & Time Collected
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(315, 68); // set x and y co-ordinates
                    pdfContentByte.showText(DateOfSymp); // Date of Onset
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(415, 68); // set x and y co-ordinates
                    pdfContentByte.showText(" "); // Result
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(427, 155); // set x and y co-ordinates
                    pdfContentByte.showText(formattedDateTime); // MEDICAL NECESSITY Date
                    pdfContentByte.endText();
                    //////***************************** NEW FORM ADDITION 02-03-2022 TABISH ************////


                }
            }
//            out.println("Printing Done");
            pdfStamper.close();
//                final File pdfFile = new File(outputFilePath);
//                response.setContentType("application/pdf");
//                response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
//                response.setContentLength((int) pdfFile.length());
//                final FileInputStream fileInputStream = new FileInputStream(pdfFile);
//                final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
//                int bytes;
//                while ((bytes = fileInputStream.read()) != -1) {
//                    responseOutputStream.write(bytes);
//                }

//            out.print("Mouhid Here\n");
            return pdfReader.getNumberOfPages() + "~" + outputFilePath + "~" + filename;
        } catch (Exception e) {
//            out.println(e.getMessage());
            System.out.println(e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            System.out.println(str);
        }
//        out.print("NOT Mouhid Here\n");
        return "";
    }

    void PatientsDocUpload_Save(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, int facilityIndex) {
//        System.out.println("inside 9999 PatientsDocUpload_Save");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String PatientId = "";
        String DocumentName = "";
        String UserId = "";
        String ClientIndex = "";
        String ClientId = "";
        int PremisisId = 0;
        int PatientRegId = 0;
        String PatientMRN = "";
        String PatientName = "";
        String DirectoryName = "";
        String Database = "";
        String DocumentType = "";
        String VisitNo = "";
        String Path = null;
        String UploadPath = null;


        boolean IdFound = false;
        boolean FileFound = false;
        boolean insuranceF = false;
        boolean insuranceB = false;
//        boolean insurance = false;
        byte[] Data = null;
        String key = "";
        String filename = "";
        String IDs = "";
        String InsuranceIDsF = "";
        String InsuranceIDsB = "";
        String Idfront = "";
        String insuranceFront = "";
        String insuranceBack = "";
        String IdfrontName = "";
        String insuranceFrontName = "";
        String insuranceBackName = "";
        String value = "";


        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(
                    "Select Id, dbname,DirectoryName from oe.clients " +
                            "where Id =" + facilityIndex);
            rset = ps.executeQuery();
            if (rset.next()) {
                PremisisId = rset.getInt(1);
                Database = rset.getString(2);
                DirectoryName = rset.getString(3);
            }
            rset.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        HashMap<String, String> valuemap = new HashMap<String, String>();

        try {
            Dictionary d = doUpload(request, response, out);
            Enumeration<String> en = d.keys();
            while (en.hasMoreElements()) {
                key = en.nextElement();
                FileFound = false;
                if (!(key.startsWith("Idfront") || key.startsWith("insuranceFront") || key.startsWith("insuranceBack"))) {
                    value = (String) d.get(key);
                    valuemap.put(key, value.substring(4));
                }
                if (key.startsWith("ClientIndex")) {
                    ClientId = (String) d.get(key);
                } else if ((key.startsWith("Idfront") && key.endsWith(".jpg")) || (key.startsWith("Idfront") && key.endsWith(".png"))) {
                    filename = key;
                    FileFound = true;
                    IdFound = true;
                    IDs = key;
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                } else if ((key.startsWith("insuranceFront") && key.endsWith(".jpg")) || (key.startsWith("insuranceFront") && key.endsWith(".png"))) {
                    filename = key;
                    FileFound = true;
                    InsuranceIDsF = key;
                    insuranceF = true;

                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                } else if ((key.startsWith("insuranceBack") && key.endsWith(".jpg")) || (key.startsWith("insuranceBack") && key.endsWith(".png"))) {
                    filename = key;
                    FileFound = true;
                    insuranceB = true;
                    InsuranceIDsB = key;

                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                }
                if (FileFound) {
                    UploadPath = "/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "/";
                    filename = filename.replaceAll("\\s+", "");

                    File fe = new File(String.valueOf(String.valueOf(UploadPath)) + filename);
                    if (fe.exists())
                        fe.delete();
                    FileOutputStream fouts = new FileOutputStream(fe);
                    fouts.write(Data);
                    fouts.flush();
                    fouts.close();
                }
            }


            ClientId = ClientId.substring(4);

//            ps = conn.prepareStatement("Select Id from " + Database + ".PatientReg order by ID desc limit 1 ");
//            rset = ps.executeQuery();
//            if (rset.next()) {
//                PatientId = rset.getString(1);
//            }

            rset.close();
            ps.close();
//            System.out.println("PatientId : " + PatientId);
//            System.out.println("PremisisId : " + PremisisId);
//            System.out.println("Database : " + Database);
//            System.out.println("DirectoryName : " + DirectoryName);

//            ps = conn.prepareStatement("UPDATE  " + Database + ".PatientReg  SET IDFront = ? , InsuranceFront = ? , InsuranceBack = ? WHERE Id = ?");
//            if (!IDs.equals("")) {
//                ps.setString(1, IDs);
//            } else {
//                ps.setString(1, null);
//            }
//            if (!InsuranceIDsF.equals("")) {
//                ps.setString(2, InsuranceIDsF);
//            } else {
//                ps.setString(2, null);
//            }
//            if (!InsuranceIDsB.equals("")) {
//                ps.setString(3, InsuranceIDsB);
//            } else {
//                ps.setString(3, null);
//            }
//            ps.setString(4, PatientId);
//            ps.executeUpdate();
//            ps.close();

//            try {
////                Query = "";
////                stmt = conn.createStatement();
////                rset = stmt.executeQuery(Query);
////                if
//
////                PreparedStatement MainReceipt = conn.prepareStatement(
////                        "Insert into " + Database + ".PatientDocUpload (PremisisId, PatientRegId, PatientMRN, PatientName, " +
////                                "UploadDocumentName, FileName, CreatedBy, CreatedDate, DocumentType, VisitIdx) " +
////                                "values (?,?,?,?,?,?,?,now(),?,?) ");
////                MainReceipt.setInt(1, PremisisId);
////                MainReceipt.setInt(2, PatientRegId);
////                MainReceipt.setString(3, PatientMRN);
////                MainReceipt.setString(4, PatientName);
////                MainReceipt.setString(5, DocumentName);
////                MainReceipt.setString(6, FileName);
////                MainReceipt.setString(7, UserId);
////                MainReceipt.setString(8, DocumentType);
////                MainReceipt.setString(9, VisitNo);
////                MainReceipt.executeUpdate();
////
////                MainReceipt.close();
//            } catch (Exception e) {
//                out.println("Error in Insertion:-" + e.getMessage());
//            }

            /*String target = "";
            String firstname = "";
            String lastname = "";
            String Message = "";
            if (DocumentType.compareTo("1") == 0) {
                Query = "SELECT directory_1 FROM oe.clients WHERE Id = " + PremisisId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next())
                    target = rset.getString(1);
                rset.close();
                stmt.close();
                File source = new File(String.valueOf(String.valueOf(UploadPath)) + FileName);
                File dest = new File(String.valueOf("/opt/" + FileName));
                int i = copyFileUsingJava7Files(source, dest);
            }*/
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("Message", "File Has been Uploaded Successfully");
//            Parser.SetField("FormName", "RegisteredPatients2");
//            Parser.SetField("ActionID", "PatientsDocUpload&PatientId=" + PatientRegId);
//            Parser.SetField("UserId", UserId);
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Success.html");
            UtilityHelper helper = new UtilityHelper();

            SaveData(request, out, conn, servletContext, Database, helper, DirectoryName, IDs, InsuranceIDsF, InsuranceIDsB, ClientId, valuemap);

        } catch (Exception e2) {
            out.println("Error in Upload DOcuments!!" + e2.getMessage());
            String str = "";
            for (int i = 0; i < (e2.getStackTrace()).length; i++)
                str = str + e2.getStackTrace()[i] + "<br>";
            out.println(str);
        }
    }

    private void CheckPatient(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database, UtilityHelper helper) {
        String Query = "";
        PreparedStatement stmt = null;
        ResultSet rset = null;
        String FirstName = "";
        String LastName = "";
        String DOB = "";
        int ClientIndex = 0;
        FirstName = request.getParameter("FirstName").trim();
        LastName = request.getParameter("LastName").trim();
        DOB = request.getParameter("DOB").trim();
        ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());
        try {
            stmt = conn.prepareStatement(
                    "Select dbname from oe.clients where Id =?");
            stmt.setInt(1, ClientIndex);
            rset = stmt.executeQuery();
            if (rset.next()) {
                Database = rset.getString(1);
            }
            stmt.close();
            rset.close();

            int PatientFound = 0;
            String FoundMRN = "";
/*            Query = " Select COUNT(*), IFNULL(MRN,0) from " + Database + ".PatientReg  " +
                    "where " +
                    " Status = 0 and " +
                    " ltrim(rtrim(UPPER(FirstName))) = ltrim(rtrim(UPPER('" + FirstName.trim() + "')))  and " +
                    " ltrim(rtrim(UPPER(LastName))) = ltrim(rtrim(UPPER('" + LastName.trim() + "'))) and " +
                    " DOB = '" + DOB + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientFound = rset.getInt(1);
                FoundMRN = rset.getString(2);
            }
            rset.close();
            stmt.close();*/

            stmt = conn.prepareStatement(
                    "Select COUNT(*), IFNULL(MRN,0) from " + Database + ".PatientReg WHERE Status = 0 and " +
                            "ltrim(rtrim(UPPER(FirstName))) = ltrim(rtrim(UPPER(?)))  and " +
                            "ltrim(rtrim(UPPER(LastName))) = ltrim(rtrim(UPPER(?))) and " +
                            "DOB = ? ");
            stmt.setString(1, FirstName);
            stmt.setString(2, LastName);
            stmt.setString(3, DOB);
            rset = stmt.executeQuery();
            if (rset.next()) {
                PatientFound = rset.getInt(1);
                FoundMRN = rset.getString(2);
            }
            stmt.close();
            rset.close();

            if (PatientFound > 0) {
                //out.println(FoundMRN + "|" + PatientFound);
                //return;
                String _LastName = "";
                String _FirstName = "";
                String MiddleInitial = "";
                String _DOB = "";
                String gender = "";
                String PhNumber = "";
                String Email = "";
                String Address = "";
                String City = "";
                String State = "";
                String ZipCode = "";
                String Ethnicity = "";
                String Race = "";
                String Test = "";
                String AtTestSite = "";
                String TestingLocation = "";
                String sampleType = "";
                String Insured = "";
                String MemID = "";
                String RespParty = "";
                String CarrierName = "";
                String GrpNumber = "";
                String County = "";

/*                Query = "SELECT LastName,FirstName,MiddleInitial,DOB, Gender,PhNumber,Email," +
                        "IFNULL(Address, '-'),IFNULL(City, '-'),IFNULL(State, '-'),IFNULL(ZipCode, '-')," + //11
                        "IFNULL(Ethnicity, ''),IFNULL(Race, ''),Test,AtTestSite,TestingLocation,IFNULL(sampleType, '')," + //17
                        "Insured,RespParty,CarrierName , " + //20
                        "GrpNumber ,MemID ,ExtendedMRN ,Status ,CreatedDate ,EditBy ," + //26
                        "Edittime,ID as PatRegIdx, IFNULL(County,'')  " + //29
                        " FROM " + Database + ".PatientReg WHERE MRN = " + FoundMRN;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    _LastName = rset.getString(1).trim();
                    _FirstName = rset.getString(2).trim();
                    MiddleInitial = rset.getString(3).trim();
                    _DOB = rset.getString(4);
                    gender = rset.getString(5);
                    PhNumber = rset.getString(6);
                    Email = rset.getString(7);
                    Address = rset.getString(8);
                    City = rset.getString(9);
                    State = rset.getString(10);
                    ZipCode = rset.getString(11);
                    Ethnicity = rset.getString(12);
                    Race = rset.getString(13);
                    Test = rset.getString(14);
                    AtTestSite = rset.getString(15);
                    TestingLocation = rset.getString(16);
                    sampleType = rset.getString(17);
                    Insured = rset.getString(18);
                    RespParty = rset.getString(19);
                    CarrierName = rset.getString(20);
                    GrpNumber = rset.getString(21);
                    MemID = rset.getString(22);
                    County = rset.getString(29);
                }
                rset.close();
                stmt.close();*/

                stmt = conn.prepareStatement(
                        "SELECT LastName,FirstName,MiddleInitial,DOB, Gender,PhNumber,Email," +
                                "IFNULL(Address, '-'),IFNULL(City, '-'),IFNULL(State, '-'),IFNULL(ZipCode, '-')," +
                                "IFNULL(Ethnicity, ''),IFNULL(Race, ''),Test,AtTestSite,TestingLocation,IFNULL(sampleType, '')," +
                                "Insured,RespParty,CarrierName , " + //20\n" +
                                "GrpNumber ,MemID ,ExtendedMRN ,Status ,CreatedDate ,EditBy ," + //26\n" +
                                "Edittime,ID as PatRegIdx, IFNULL(County,'')" + //29\n" +
                                " FROM " + Database + ".PatientReg WHERE MRN = ?");
                stmt.setString(1, FoundMRN);
                rset = stmt.executeQuery();
                if (rset.next()) {
                    _LastName = rset.getString(1).trim();
                    _FirstName = rset.getString(2).trim();
                    MiddleInitial = rset.getString(3).trim();
                    _DOB = rset.getString(4);
                    gender = rset.getString(5);
                    PhNumber = rset.getString(6);
                    Email = rset.getString(7);
                    Address = rset.getString(8);
                    City = rset.getString(9);
                    State = rset.getString(10);
                    ZipCode = rset.getString(11);
                    Ethnicity = rset.getString(12);
                    Race = rset.getString(13);
                    Test = rset.getString(14);
                    AtTestSite = rset.getString(15);
                    TestingLocation = rset.getString(16);
                    sampleType = rset.getString(17);
                    Insured = rset.getString(18);
                    RespParty = rset.getString(19);
                    CarrierName = rset.getString(20);
                    GrpNumber = rset.getString(21);
                    MemID = rset.getString(22);
                    County = rset.getString(29);
                }
                stmt.close();
                rset.close();

                out.println(PatientFound + "|" + _FirstName + "|" + _LastName + "|" + MiddleInitial + "|" + _DOB //4
                        + "|" + gender + "|" + PhNumber + "|" + Email + "|" + Address //8
                        + "|" + City + "|" + State + "|" + ZipCode + "|" + Ethnicity //12
                        + "|" + Race + "|" + Test + "|" + AtTestSite + "|" + TestingLocation + "|" + sampleType + "|" + Insured //18
                        + "|" + RespParty + "|" + CarrierName + "|" + GrpNumber + "|" + MemID + "|" + County); //23

            }
        } catch (Exception e) {
            if (conn == null) {
                conn = Services.getMysqlConn(servletContext);
            }
            Services.DumException("CheckPatient^^Facility : " + ClientIndex + " ##MES#00001", "PatientReg ", request, e);
            helper.SendEmailWithAttachment("Error in PatientReg Facility : " + ClientIndex + " ** (CheckPatient Error in Main Catch ^^ ##MES#00001)", servletContext, e, "PatientReg", "CheckPatient", conn);
        }
    }

    void EditValues(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database, String UserId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String MRN = request.getParameter("MRN").trim();
        String orderId = request.getParameter("orderId").trim();
        int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String DOB = "";
        String gender = "";
        String Email = "";
        String PhNumber = "";
        String Address = "";
        String City = "";
        String State = "";
        String Country = "";
        String ZipCode = "";
        String Ethnicity = "";
        String Race = "";
        String County = "";
        StringBuffer testAtSiteBuff = new StringBuffer();
        StringBuffer insuredBuff = new StringBuffer();
        StringBuffer respParty = new StringBuffer();
        StringBuffer carrier = new StringBuffer();
        StringBuffer RaceBuff = new StringBuffer();
        StringBuffer TestsBuff = new StringBuffer();
        StringBuffer LocationBuff = new StringBuffer();
        StringBuffer genderBuff = new StringBuffer();
        StringBuffer ethinicityBuff = new StringBuffer();

        String Test = "";
        String AtTestSite = "";
        String TestingLocation = "";
        String sampleType = "";
        String Insured = "";
        String RespParty = "";
        String CarrierName = "";
        String MemID = "";
        String GrpNumber = "";
        String Patientconsent = "";
        String ContactConsent = "";
        String DrivingLicense = "";
        String StateId = "";

        String HaveSymptoms = "";
        String SympFever = "";
        String SympDiarrhea = "";
        String SympHeadache = "";
        String SympCongestion = "";
        String SympShortBreath = "";
        String SympBodyache = "";
        String SympChills = "";
        String SympFatigue = "";
        String SympSoreThroat = "";
        String SympRunnyNose = "";
        String SympDiffBreathin = "";
        String SympNausea = "";
        String SympLossSmellTaste = "";
        String FullyVaccinated = "";
        String HaveExposed = "";
        String FirstTimeCovid = "";
        String HealthCareEmp = "";
        String InICU = "";
        String InHospital = "";
        String IsPregnant = "";
        String IsResident = "";
        String HaveCloseContact = "";
        String HaveParticipated = "";
        String HaveAsked = "";
        String DateOfSymp = "";

        String FullyVaccinatedYes = "";
        String FullyVaccinatedNo = "";
        String HaveExposedYes = "";
        String HaveExposedNo = "";
        String FirstTimeCovidYes = "";
        String FirstTimeCovidNo = "";
        String HealthCareEmpYes = "";
        String HealthCareEmpNo = "";
        String InICUYes = "";
        String InICUNo = "";
        String InHospitalYes = "";
        String InHospitalNo = "";
        String IsResidentYes = "";
        String IsResidentNo = "";
        String HaveCloseContactYes = "";
        String HaveCloseContactNo = "";
        String HaveParticipatedYes = "";
        String HaveParticipatedNo = "";
        String HaveAskedYes = "";
        String HaveAskedNo = "";
        String IsPregnantYes = "";
        String IsPregnantNo = "";


        String facilityName = helper.getFacilityName(request, conn, servletContext, ClientId);
        try {
            Query = "SELECT LastName,FirstName,MiddleInitial,DOB, Gender,PhNumber,Email," +
                    "IFNULL(Address, '-'),IFNULL(City, '-'),IFNULL(State, '-'),IFNULL(ZipCode, '-')," + //11
                    "IFNULL(Ethnicity, ''),IFNULL(Race, ''),Test,AtTestSite,TestingLocation,IFNULL(sampleType, '')," + //17
                    "Insured,RespParty,CarrierName , " + //20
                    "GrpNumber ,MemID ,ExtendedMRN ,Status ,CreatedDate ,EditBy ," + //26
                    "Edittime,ID as PatRegIdx, IFNULL(County,'')," + //29
                    "Patientconsent, ContactConsent, DrivingLicense, StateId,  " +//33
                    "HaveSymptoms ,SympFever ,SympDiarrhea ,SympHeadache ,SympCongestion ,SympShortBreath ,SympBodyache ,SympChills ,SympFatigue ,SympSoreThroat ,SympRunnyNose ,SympDiffBreathin ,SympNausea ,SympLossSmellTaste ,FullyVaccinated ,HaveExposed ,FirstTimeCovid ,HealthCareEmp ,InICU ,InHospital ,IsPregnant ,IsResident ,HaveCloseContact ,HaveParticipated ,HaveAsked ,DateOfSymp" +
                    " FROM roverlab.PatientReg WHERE MRN =" + MRN;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                LastName = rset.getString(1).trim();
                FirstName = rset.getString(2).trim();
                MiddleInitial = rset.getString(3).trim();
                DOB = rset.getString(4);
                gender = rset.getString(5);
                PhNumber = rset.getString(6);
                Email = rset.getString(7);
                Address = rset.getString(8);
                City = rset.getString(9);
                State = rset.getString(10);
                ZipCode = rset.getString(11);
                Ethnicity = rset.getString(12);
                Race = rset.getString(13);
                Test = rset.getString(14);
                AtTestSite = rset.getString(15);
                TestingLocation = rset.getString(16);
                sampleType = rset.getString(17);
                Insured = rset.getString(18);
                RespParty = rset.getString(19);
                CarrierName = rset.getString(20);
                GrpNumber = rset.getString(21);
                MemID = rset.getString(22);
                PatientRegId = rset.getInt(28);
                County = rset.getString(29);
                Patientconsent = rset.getString(30);
                ContactConsent = rset.getString(31);
                DrivingLicense = rset.getString(32);
                StateId = rset.getString(33);

                HaveSymptoms = rset.getString(34);
                SympFever = rset.getString(35);
                SympDiarrhea = rset.getString(36);
                SympHeadache = rset.getString(37);
                SympCongestion = rset.getString(38);
                SympShortBreath = rset.getString(39);
                SympBodyache = rset.getString(40);
                SympChills = rset.getString(41);
                SympFatigue = rset.getString(42);
                SympSoreThroat = rset.getString(43);
                SympRunnyNose = rset.getString(44);
                SympDiffBreathin = rset.getString(45);
                SympNausea = rset.getString(46);
                SympLossSmellTaste = rset.getString(47);
                FullyVaccinated = rset.getString(48);
                HaveExposed = rset.getString(49);
                FirstTimeCovid = rset.getString(50);
                HealthCareEmp = rset.getString(51);
                InICU = rset.getString(52);
                InHospital = rset.getString(53);
                IsPregnant = rset.getString(54);
                IsResident = rset.getString(55);
                HaveCloseContact = rset.getString(56);
                HaveParticipated = rset.getString(57);
                HaveAsked = rset.getString(58);
                DateOfSymp = rset.getString(59);

            }
            rset.close();
            stmt.close();

            switch (Race) {
                case "Black":
                    RaceBuff.append("<select class=\"form-control\" id=\"Race\" name=\"Race\" style=\"color:black;\" required>\n<option value=\"\"  disabled>Select Race</option>\n<option value=\"Black\" selected>Black</option>\n<option value=\"White\" >White</option>\n <option value=\"Asian\" >Asian</option>\n<option value=\"American Indian or Alaska Native\" >American Indian or Alaska Native</option>\n<option value=\"Native Hawaiian or Other Pacific Islander\" >Native Hawaiian or Other Pacific Islander</option>\n<option value=\"Mixed\" >Mixed</option>\n<option value=\"Other\" >Other</option>\n</select>\n");
                    break;
                case "White":
                    RaceBuff.append("<select class=\"form-control\" id=\"Race\" name=\"Race\" style=\"color:black;\" required>\n<option value=\"\"  disabled>Select Race</option>\n<option value=\"Black\">Black</option>\n<option value=\"White\" selected>White</option>\n <option value=\"Asian\" >Asian</option>\n<option value=\"American Indian or Alaska Native\" >American Indian or Alaska Native</option>\n<option value=\"Native Hawaiian or Other Pacific Islander\" >Native Hawaiian or Other Pacific Islander</option>\n<option value=\"Mixed\" >Mixed</option>\n<option value=\"Other\" >Other</option>\n</select>\n");
                    break;
                case "Asian":
                    RaceBuff.append("<select class=\"form-control\" id=\"Race\" name=\"Race\" style=\"color:black;\" required>\n<option value=\"\"  disabled>Select Race</option>\n<option value=\"Black\" >Black</option>\n<option value=\"White\" >White</option>\n <option value=\"Asian\" selected>Asian</option>\n<option value=\"American Indian or Alaska Native\" >American Indian or Alaska Native</option>\n<option value=\"Native Hawaiian or Other Pacific Islander\" >Native Hawaiian or Other Pacific Islander</option>\n<option value=\"Mixed\" >Mixed</option>\n<option value=\"Other\" >Other</option>\n</select>\n");
                    break;
                case "American Indian or Alaska Native":
                    RaceBuff.append("<select class=\"form-control\" id=\"Race\" name=\"Race\" style=\"color:black;\" required>\n<option value=\"\"  disabled>Select Race</option>\n<option value=\"Black\" >Black</option>\n<option value=\"White\" >White</option>\n <option value=\"Asian\" >Asian</option>\n<option value=\"American Indian or Alaska Native\" selected>American Indian or Alaska Native</option>\n<option value=\"Native Hawaiian or Other Pacific Islander\" >Native Hawaiian or Other Pacific Islander</option>\n<option value=\"Mixed\" >Mixed</option>\n<option value=\"Other\" >Other</option>\n</select>\n");
                    break;
                case "Native Hawaiian or Other Pacific Islander":
                    RaceBuff.append("<select class=\"form-control\" id=\"Race\" name=\"Race\" style=\"color:black;\" required>\n<option value=\"\"  disabled>Select Race</option>\n<option value=\"Black\" selected>Black</option>\n<option value=\"White\" >White</option>\n <option value=\"Asian\" >Asian</option>\n<option value=\"American Indian or Alaska Native\" selected>American Indian or Alaska Native</option>\n<option value=\"Native Hawaiian or Other Pacific Islander\" selected >Native Hawaiian or Other Pacific Islander</option>\n<option value=\"Mixed\" >Mixed</option>\n<option value=\"Other\" >Other</option>\n</select>\n");
                    break;
                case "Mixed":
                    RaceBuff.append("<select class=\"form-control\" id=\"Race\" name=\"Race\" style=\"color:black;\" required>\n<option value=\"\"  disabled>Select Race</option>\n<option value=\"Black\" selected>Black</option>\n<option value=\"White\" >White</option>\n <option value=\"Asian\" >Asian</option>\n<option value=\"American Indian or Alaska Native\" >American Indian or Alaska Native</option>\n<option value=\"Native Hawaiian or Other Pacific Islander\" selected>Native Hawaiian or Other Pacific Islander</option>\n<option value=\"Mixed\" selected>Mixed</option>\n<option value=\"Other\" >Other</option>\n</select>\n");
                    break;
                case "Other":
                    RaceBuff.append("<select class=\"form-control\" id=\"Race\" name=\"Race\" style=\"color:black;\" required>\n<option value=\"\"  disabled>Select Race</option>\n<option value=\"Black\" selected>Black</option>\n<option value=\"White\" >White</option>\n <option value=\"Asian\" >Asian</option>\n<option value=\"American Indian or Alaska Native\" >American Indian or Alaska Native</option>\n<option value=\"Native Hawaiian or Other Pacific Islander\" >Native Hawaiian or Other Pacific Islander</option>\n<option value=\"Mixed\" >Mixed</option>\n<option value=\"Other\" selected>Other</option>\n</select>\n");
                    break;
                default:
                    RaceBuff.append("<select class=\"form-control\" id=\"Race\" name=\"Race\" style=\"color:black;\" required>\n<option value=\"\"  disabled>Select Race</option>\n<option value=\"Black\">Black</option>\n<option value=\"White\" >White</option>\n <option value=\"Asian\" >Asian</option>\n<option value=\"American Indian or Alaska Native\" >American Indian or Alaska Native</option>\n<option value=\"Native Hawaiian or Other Pacific Islander\" >Native Hawaiian or Other Pacific Islander</option>\n<option value=\"Mixed\" >Mixed</option>\n<option value=\"Other\" >Other</option>\n</select>\n");
                    break;
            }

            switch (Ethnicity) {
                case "Latino":
                    ethinicityBuff.append("<select class=\"form-control\" id=\"Ethnicity\" name=\"Ethnicity\" style=\"color:black;\" required>\n<option value=\"\"  disabled>Select Ethnicity</option>\n<option value=\"Not Hispanic\" selected>Not Hispanic</option>\n<option value=\"Latino\" >Latino</option>\n</select>\n");
                    break;
                case "Not Hispanic":
                    ethinicityBuff.append("<select class=\"form-control\" id=\"Ethnicity\" name=\"Ethnicity\" style=\"color:black;\" required>\n<option value=\"\"  disabled >Select Ethnicity</option>\n<option value=\"Not Hispanic\" >Not Hispanic</option>\n<option value=\"Latino\" selected>Latino</option>\n</select>\n");
                    break;
                default:
                    ethinicityBuff.append("<select class=\"form-control\" id=\"Ethnicity\" name=\"Ethnicity\" style=\"color:black;\" required>\n<option value=\"\"  disabled >Select Ethnicity</option>\n<option value=\"Not Hispanic\">Not Hispanic</option>\n<option value=\"Latino\">Latino</option>\n</select>\n");
                    break;
            }

            switch (gender) {
                case "male":
                    genderBuff.append("<select class=\"form-control\" id=\"gender\" name=\"gender\" style=\"color:black;\" required>\n<option value=\"\"  disabled>Select Gender</option>\n<option value=\"male\" selected>Male</option>\n<option value=\"female\"  disabled >Female</option>\n</select>\n");
                    break;
                case "female":
                    genderBuff.append("<select class=\"form-control\" id=\"gender\" name=\"gender\" style=\"color:black;\" required>\n<option value=\"\"  disabled >Select Gender</option>\n<option value=\"male\"  disabled >Male</option>\n<option value=\"female\" selected>Female</option>\n</select>\n");
                    break;
                default:
                    genderBuff.append("<select class=\"form-control\" id=\"gender\" name=\"gender\" style=\"color:black;\" required>\n<option value=\"\"  disabled >Select Gender</option>\n<option value=\"male\">Male</option>\n<option value=\"female\">Female</option>\n</select>\n");
                    break;
            }

            String _testName = "";
            Query = "Select TestName from " + Database + ".ListofTests WHERE Id = " + Test;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                _testName = rset.getString(1).trim();
            rset.close();
            stmt.close();

            Query = "Select Id,TestName from " + Database + ".ListofTests";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            TestsBuff.append("<option value=''>Select Test</option>");
            while (rset.next()) {
                if (Test.equals(rset.getString(1))) {
                    TestsBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(2) + "</option>");
                    continue;
                }
                TestsBuff.append("<option value=" + rset.getString(1) + ">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

//            switch (AtTestSite) {
//                case "No":
//                    testAtSiteBuff.append("<div> <label class=\"radio-inline\"> <input type=\"radio\" name=\"testingSite\" value=\"No\" checked required> NO </label> <label class=\"radio-inline\" style=\"margin-left:70px\"> <input type=\"radio\" name=\"testingSite\" value=\"Yes\"> Yes </label> </div>\n");
//                    break;
//                case "Yes":
//                    testAtSiteBuff.append("<div> <label class=\"radio-inline\"> <input type=\"radio\" name=\"testingSite\" value=\"No\" required> NO </label> <label class=\"radio-inline\" style=\"margin-left:70px\"> <input type=\"radio\" name=\"testingSite\" value=\"Yes\" checked> Yes </label> </div>\n");
//                    break;
//                default:
//                    testAtSiteBuff.append("<div> <label class=\"radio-inline\"> <input type=\"radio\" name=\"testingSite\" value=\"No\" required> NO </label> <label class=\"radio-inline\" style=\"margin-left:70px\"> <input type=\"radio\" name=\"testingSite\" value=\"Yes\"> Yes </label> </div>\n");
//                    break;
//            }

            Query = "Select Id,Location from " + Database + ".Locations";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            LocationBuff.append("<option value=''>Select Location</option>");
            while (rset.next()) {
                if (TestingLocation.equals(rset.getString(1))) {
                    LocationBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(2) + "</option>");
                    continue;
                }
                LocationBuff.append("<option value=" + rset.getString(1) + ">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();


            switch (Insured) {
                case "No":
                    insuredBuff.append("<div> <label class=\"radio-inline\"> <input type=\"radio\" name=\"haveIns\" value=\"No\" checked required onclick=\"HideDiv('insuranceDiv');showNonInsuranceDiv('0');\"> NO </label> <label class=\"radio-inline\" style=\"margin-left:70px\"> <input type=\"radio\" name=\"haveIns\" value=\"Yes\" onclick=\"ShowDiv('insuranceDiv');showNonInsuranceDiv('1');\"> Yes </label> </div>\n");
                    break;
                case "Yes":
                    insuredBuff.append("<div> <label class=\"radio-inline\"> <input type=\"radio\" name=\"haveIns\" value=\"No\" required onclick=\"HideDiv('insuranceDiv');showNonInsuranceDiv('0');\"> NO </label> <label class=\"radio-inline\" style=\"margin-left:70px\"> <input type=\"radio\" name=\"haveIns\" value=\"Yes\" checked onclick=\"ShowDiv('insuranceDiv');showNonInsuranceDiv('1');\"> Yes </label> </div>\n");
                    break;
                default:
                    insuredBuff.append("<div> <label class=\"radio-inline\"> <input type=\"radio\" name=\"haveIns\" value=\"No\" required onclick=\"HideDiv('insuranceDiv');\"> NO </label> <label class=\"radio-inline\" style=\"margin-left:70px\"> <input type=\"radio\" name=\"haveIns\" value=\"Yes\" onclick=\"ShowDiv('insuranceDiv');\"> Yes </label> </div>\n");
                    break;
            }

            switch (RespParty) {
                case "Self":
                    respParty.append("<option value=\"Self\" selected>Self</option> <option value=\" Spouse \"> Spouse</option> <option value=\" Child \"> Child</option> <option value=\" Other \"> Other</option>");
                    break;
                case "Spouse":
                    respParty.append("<option value=\"Self\" >Self</option> <option value=\" Spouse \" selected> Spouse</option> <option value=\" Child \"> Child</option> <option value=\" Other \"> Other</option>");
                    break;
                case "Child":
                    respParty.append("<option value=\"Self\" >Self</option> <option value=\" Spouse \"> Spouse</option> <option value=\" Child \" selected> Child</option> <option value=\" Other \"> Other</option>");
                    break;
                case "Other":
                    respParty.append("<option value=\"Self\" >Self</option> <option value=\" Spouse \"> Spouse</option> <option value=\" Child \"> Child</option> <option value=\" Other \" selected> Other</option>");
                    break;
                default:
                    respParty.append("<option value=\"Self\">Self</option> <option value=\" Spouse \"> Spouse</option> <option value=\" Child \"> Child</option> <option value=\" Other \"> Other</option>");
                    break;
            }

            switch (CarrierName) {
                case "Self":
                    carrier.append("<option value=\"Self\" selected>Self</option> <option value=\" Other \"> Other</option>");
                    break;
                case "Other":
                    carrier.append("<option value=\"Self\" >Self</option> <option value=\" Other \" selected> Other</option>");
                    break;
                default:
                    carrier.append("<option value=\"Self\" >Self</option> <option value=\" Other \"> Other</option>");
                    break;
            }


            //Sysmptoms SQLException
            String HaveSymptomsYes = "";
            String HaveSymptomsNo = "";


            switch (HaveSymptoms) {
                case "Yes":
                    HaveSymptomsYes = "<input type=\"radio\" name=\"anySymp\" value=\"Yes\" onclick=\"showCovidQues('YesSympQues');\" checked>";
                    HaveSymptomsNo = "<input type=\"radio\" name=\"anySymp\" value=\"No\" required onclick=\"hideCovidQues('YesSympQues');\">";
                    break;
                case "No":
                    HaveSymptomsYes = "<input type=\"radio\" name=\"anySymp\" value=\"Yes\" onclick=\"showCovidQues('YesSympQues');\">";
                    HaveSymptomsNo = "<input type=\"radio\" name=\"anySymp\" value=\"No\" required onclick=\"hideCovidQues('YesSympQues');\" checked>";
                    break;
                default:
                    HaveSymptomsYes = "<input type=\"radio\" name=\"anySymp\" value=\"Yes\" onclick=\"showCovidQues('YesSympQues');\">";
                    HaveSymptomsNo = "<input type=\"radio\" name=\"anySymp\" value=\"No\" required onclick=\"hideCovidQues('YesSympQues');\">";
                    break;
            }


            if (SympFever.equals("1")) {
                SympFever = "<input type=\"checkbox\" id=\"SympFever\" name=\"SympFever\" checked />";
            } else {
                SympFever = "<input type=\"checkbox\" id=\"SympFever\" name=\"SympFever\" />";
            }

            if (SympBodyache.equals("1")) {
                SympBodyache = "<input type=\"checkbox\" id=\"SympBodyAches\" name=\"SympBodyAches\" checked />";
            } else {
                SympBodyache = "<input type=\"checkbox\" id=\"SympBodyAches\" name=\"SympBodyAches\" />";
            }

            if (SympSoreThroat.equals("1")) {
                SympSoreThroat = "<input type=\"checkbox\" id=\"SympSoreThroat\" name=\"SympSoreThroat\" checked />";
            } else {
                SympSoreThroat = "<input type=\"checkbox\" id=\"SympSoreThroat\" name=\"SympSoreThroat\" />";
            }

            if (SympFatigue.equals("1")) {
                SympFatigue = "<input type=\"checkbox\" id=\"SympFatigue\" name=\"SympFatigue\"  checked />";
            } else {
                SympFatigue = "<input type=\"checkbox\" id=\"SympFatigue\" name=\"SympFatigue\"  />";
            }

            if (SympDiffBreathin.equals("1")) {
                SympDiffBreathin = "<input type=\"checkbox\" id=\"SympDifficultBreathing\" name=\"SympDifficultBreathing\"  checked />";
            } else {
                SympDiffBreathin = "<input type=\"checkbox\" id=\"SympDifficultBreathing\" name=\"SympDifficultBreathing\"  />";
            }

            if (SympDiarrhea.equals("1")) {
                SympDiarrhea = "<input type=\"checkbox\" id=\"SympDiarrhea\" name=\"SympDiarrhea\" checked />";
            } else {
                SympDiarrhea = "<input type=\"checkbox\" id=\"SympDiarrhea\" name=\"SympDiarrhea\" />";
            }
            if (SympChills.equals("1")) {
                SympChills = "<input type=\"checkbox\" id=\"SympChills\" name=\"SympChills\" checked />";
            } else {
                SympChills = "<input type=\"checkbox\" id=\"SympChills\" name=\"SympChills\" />";
            }
            if (SympRunnyNose.equals("1")) {
                SympRunnyNose = "<input type=\"checkbox\" id=\"SympRunnyNose\" name=\"SympRunnyNose\"  checked />";
            } else {
                SympRunnyNose = "<input type=\"checkbox\" id=\"SympRunnyNose\" name=\"SympRunnyNose\"  />";
            }

            if (SympNausea.equals("1")) {
                SympNausea = "<input type=\"checkbox\" id=\"SympVomiting\" name=\"SympVomiting\" checked  />";
            } else {
                SympNausea = "<input type=\"checkbox\" id=\"SympVomiting\" name=\"SympVomiting\"  />";
            }

            if (SympHeadache.equals("1")) {
                SympHeadache = "<input type=\"checkbox\" id=\"SympHeadache\" name=\"SympHeadache\"  checked/>";
            } else {
                SympHeadache = "<input type=\"checkbox\" id=\"SympHeadache\" name=\"SympHeadache\"  />";
            }

            if (SympLossSmellTaste.equals("1")) {
                SympLossSmellTaste = "<input type=\"checkbox\" id=\"SympLossTaste\" name=\"SympLossTaste\"  checked/>";
            } else {
                SympLossSmellTaste = "<input type=\"checkbox\" id=\"SympLossTaste\" name=\"SympLossTaste\"  />";
            }

            if (SympShortBreath.equals("1")) {
                SympShortBreath = "<input type=\"checkbox\" id=\"SympShortBreath\" name=\"SympShortBreath\"  checked/>";
            } else {
                SympShortBreath = "<input type=\"checkbox\" id=\"SympShortBreath\" name=\"SympShortBreath\"  />";
            }

            if (SympCongestion.equals("1")) {
                SympCongestion = "<input type=\"checkbox\" id=\"SympCongestion\" name=\"SympCongestion\"  checked/>";
            } else {
                SympCongestion = "<input type=\"checkbox\" id=\"SympCongestion\" name=\"SympCongestion\"  />";
            }


            //yes and no options

            switch (FullyVaccinated) {
                case "Yes":
                    FullyVaccinatedYes = "<input type=\"radio\" name=\"vaccOrnot\" value=\"Yes\" onclick=\"showCovidQues('covidQuestions');showYesDiv('yesDiv','noDiv');\" checked>";
                    FullyVaccinatedNo = "<input type=\"radio\" name=\"vaccOrnot\" value=\"No\" required onclick=\"showCovidQues('covidQuestions');showYesDiv('noDiv','yesDiv');\">";
                    break;
                case "No":
                    FullyVaccinatedYes = "<input type=\"radio\" name=\"vaccOrnot\" value=\"Yes\" onclick=\"showCovidQues('covidQuestions');showYesDiv('yesDiv','noDiv');\">";
                    FullyVaccinatedNo = "<input type=\"radio\" name=\"vaccOrnot\" value=\"No\" required onclick=\"showCovidQues('covidQuestions');showYesDiv('noDiv','yesDiv');\" checked>";
                    break;
                default:
                    FullyVaccinatedYes = "<input type=\"radio\" name=\"vaccOrnot\" value=\"Yes\" onclick=\"showCovidQues('covidQuestions');showYesDiv('yesDiv','noDiv');\">";
                    FullyVaccinatedNo = "<input type=\"radio\" name=\"vaccOrnot\" value=\"No\" required onclick=\"showCovidQues('covidQuestions');showYesDiv('noDiv','yesDiv');\">";
                    break;
            }


            switch (HaveExposed) {
                case "Yes":
                    HaveExposedYes = "<input type=\"radio\" name=\"suspecSymp\" value=\"Yes\" checked>";
                    HaveExposedNo = "<input type=\"radio\" name=\"suspecSymp\" value=\"No\">";
                    break;
                case "No":
                    HaveExposedYes = "<input type=\"radio\" name=\"suspecSymp\" value=\"Yes\">";
                    HaveExposedNo = "<input type=\"radio\" name=\"suspecSymp\" value=\"No\" checked>";
                    break;
                default:
                    HaveExposedYes = "<input type=\"radio\" name=\"suspecSymp\" value=\"Yes\">";
                    HaveExposedNo = "<input type=\"radio\" name=\"suspecSymp\" value=\"No\">";
                    break;
            }

            switch (FirstTimeCovid) {
                case "Yes":
                    FirstTimeCovidYes = "<input type=\"radio\" name=\"testPositive\" value=\"Yes\" checked>";
                    FirstTimeCovidNo = "<input type=\"radio\" name=\"testPositive\" value=\"No\">";
                    break;
                case "No":
                    FirstTimeCovidYes = "<input type=\"radio\" name=\"testPositive\" value=\"Yes\">";
                    FirstTimeCovidNo = "<input type=\"radio\" name=\"testPositive\" value=\"No\" checked>";
                    break;
                default:
                    FirstTimeCovidYes = "<input type=\"radio\" name=\"testPositive\" value=\"Yes\">";
                    FirstTimeCovidNo = "<input type=\"radio\" name=\"testPositive\" value=\"No\">";
                    break;
            }


            switch (HealthCareEmp) {
                case "Yes":
                    HealthCareEmpYes = "<input type=\"radio\" name=\"healthCareEmp\" value=\"Yes\" checked>";
                    HealthCareEmpNo = "<input type=\"radio\" name=\"healthCareEmp\" value=\"No\">";
                    break;
                case "No":
                    HealthCareEmpYes = "<input type=\"radio\" name=\"healthCareEmp\" value=\"Yes\">";
                    HealthCareEmpNo = "<input type=\"radio\" name=\"healthCareEmp\" value=\"No\" checked>";
                    break;
                default:
                    HealthCareEmpYes = "<input type=\"radio\" name=\"healthCareEmp\" value=\"Yes\">";
                    HealthCareEmpNo = "<input type=\"radio\" name=\"healthCareEmp\" value=\"No\">";
                    break;
            }


            switch (InICU) {
                case "Yes":
                    InICUYes = " <input type=\"radio\" name=\"atICU\" value=\"Yes\" checked>";
                    InICUNo = " <input type=\"radio\" name=\"atICU\" value=\"No\">";
                    break;
                case "No":
                    InICUYes = " <input type=\"radio\" name=\"atICU\" value=\"Yes\">";
                    InICUNo = " <input type=\"radio\" name=\"atICU\" value=\"No\"  checked>";
                    break;
                default:
                    InICUYes = " <input type=\"radio\" name=\"atICU\" value=\"Yes\">";
                    InICUNo = " <input type=\"radio\" name=\"atICU\" value=\"No\">";
                    break;
            }


            switch (InHospital) {
                case "Yes":
                    InHospitalYes = "<input type=\"radio\" name=\"atHosp\" value=\"Yes\" checked>";
                    InHospitalNo = "<input type=\"radio\" name=\"PregOrNot\" value=\"No\">";
                    break;
                case "No":
                    InHospitalYes = "<input type=\"radio\" name=\"atHosp\" value=\"Yes\">";
                    InHospitalNo = "<input type=\"radio\" name=\"atHosp\" value=\"No\" checked>";
                    break;
                default:
                    InHospitalYes = "<input type=\"radio\" name=\"atHosp\" value=\"Yes\">";
                    InHospitalNo = "<input type=\"radio\" name=\"atHosp\" value=\"No\">";
                    break;
            }


            switch (IsPregnant) {
                case "Yes":
                    IsPregnantYes = "<input type=\"radio\" name=\"PregOrNot\" value=\"Yes\" checked> ";
                    IsPregnantNo = "<input type=\"radio\" name=\"PregOrNot\" value=\"No\">";
                    break;
                case "No":
                    IsPregnantYes = "<input type=\"radio\" name=\"PregOrNot\" value=\"Yes\"> ";
                    IsPregnantNo = "<input type=\"radio\" name=\"PregOrNot\" value=\"No\" checked>";
                    break;
                default:
                    IsPregnantYes = "<input type=\"radio\" name=\"PregOrNot\" value=\"Yes\"> ";
                    IsPregnantNo = "<input type=\"radio\" name=\"PregOrNot\" value=\"No\">";
                    break;
            }

            switch (IsResident) {
                case "Yes":
                    IsResidentYes = " <input type=\"radio\" name=\"atCongreCare\" value=\"Yes\" checked>";
                    IsResidentNo = "<input type=\"radio\" name=\"atCongreCare\" value=\"No\">";
                    break;
                case "No":
                    IsResidentYes = " <input type=\"radio\" name=\"atCongreCare\" value=\"Yes\">";
                    IsResidentNo = "<input type=\"radio\" name=\"atCongreCare\" value=\"No\" checked>";
                    break;
                default:
                    IsResidentYes = " <input type=\"radio\" name=\"atCongreCare\" value=\"Yes\">";
                    IsResidentNo = "<input type=\"radio\" name=\"atCongreCare\" value=\"No\">";
                    break;
            }

            switch (HaveCloseContact) {
                case "Yes":
                    HaveCloseContactYes = "<input type=\"radio\" name=\"closeContact\" value=\"Yes\" checked>";
                    HaveCloseContactNo = "<input type=\"radio\" name=\"closeContact\" value=\"No\">";
                    break;
                case "No":
                    HaveCloseContactYes = "<input type=\"radio\" name=\"closeContact\" value=\"Yes\">";
                    HaveCloseContactNo = "<input type=\"radio\" name=\"closeContact\" value=\"No\" checked>";
                    break;
                default:
                    HaveCloseContactYes = "<input type=\"radio\" name=\"closeContact\" value=\"Yes\">";
                    HaveCloseContactNo = "<input type=\"radio\" name=\"closeContact\" value=\"No\">";
                    break;
            }

            switch (HaveParticipated) {
                case "Yes":
                    HaveParticipatedYes = " <input type=\"radio\" name=\"massGather\" value=\"Yes\" checked>";
                    HaveParticipatedNo = "<input type=\"radio\" name=\"massGather\" value=\"No\">";
                    break;
                case "No":
                    HaveParticipatedYes = " <input type=\"radio\" name=\"massGather\" value=\"Yes\">";
                    HaveParticipatedNo = "<input type=\"radio\" name=\"massGather\" value=\"No\" checked>";
                    break;
                default:
                    HaveParticipatedYes = " <input type=\"radio\" name=\"massGather\" value=\"Yes\">";
                    HaveParticipatedNo = "<input type=\"radio\" name=\"massGather\" value=\"No\">";
                    break;
            }

            switch (HaveAsked) {
                case "Yes":
                    HaveAskedYes = "<input type=\"radio\" name=\"askedBy\" value=\"Yes\" checked>";
                    HaveAskedNo = "<input type=\"radio\" name=\"askedBy\" value=\"No\">";
                    break;
                case "No":
                    HaveAskedYes = "<input type=\"radio\" name=\"askedBy\" value=\"Yes\">";
                    HaveAskedNo = "<input type=\"radio\" name=\"askedBy\" value=\"No\"checked>";
                    break;
                default:
                    HaveAskedYes = "<input type=\"radio\" name=\"askedBy\" value=\"Yes\">";
                    HaveAskedNo = "<input type=\"radio\" name=\"askedBy\" value=\"No\">";
                    break;
            }


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("LastName", String.valueOf(LastName));
            Parser.SetField("sampleType", String.valueOf(sampleType));
            Parser.SetField("FirstName", String.valueOf(FirstName));
            Parser.SetField("MiddleInitial", String.valueOf(MiddleInitial));
            Parser.SetField("DOB", String.valueOf(DOB));
            Parser.SetField("genderBuff", String.valueOf(genderBuff));
            Parser.SetField("Email", String.valueOf(Email));
            Parser.SetField("PhNumber", String.valueOf(PhNumber));
            Parser.SetField("Address", String.valueOf(Address));
            Parser.SetField("City", String.valueOf(City));
            Parser.SetField("State", String.valueOf(State));
            Parser.SetField("County", String.valueOf(County));
            Parser.SetField("Country", String.valueOf(Country));
            Parser.SetField("ZipCode", String.valueOf(ZipCode));
            Parser.SetField("EthnicityBuff", String.valueOf(ethinicityBuff));
            Parser.SetField("RaceBuff", String.valueOf(RaceBuff));
            Parser.SetField("TestsBuff", String.valueOf(TestsBuff));
            Parser.SetField("testAtSiteBuff", String.valueOf(testAtSiteBuff));
            Parser.SetField("LocationBuff", String.valueOf(LocationBuff));
            Parser.SetField("insuredBuff", String.valueOf(insuredBuff));
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
            Parser.SetField("MRN", MRN);
            Parser.SetField("County", County);
            Parser.SetField("Test", _testName);
            Parser.SetField("RespParty", respParty.toString());
            Parser.SetField("CarrierName", carrier.toString());
            Parser.SetField("GrpNumber", GrpNumber);
            Parser.SetField("MemID", MemID);
            Parser.SetField("Insured", Insured);
            Parser.SetField("DrivingLicense", DrivingLicense);
            Parser.SetField("StateId", StateId);
            Parser.SetField("orderId", orderId);


            //Symptom Section

            Parser.SetField("HaveSymptoms", String.valueOf(HaveSymptoms));
            Parser.SetField("HaveSymptomsYes", String.valueOf(HaveSymptomsYes));
            Parser.SetField("HaveSymptomsNo", String.valueOf(HaveSymptomsNo));
            Parser.SetField("SympFever", String.valueOf(SympFever));
            Parser.SetField("SympDiarrhea", String.valueOf(SympDiarrhea));
            Parser.SetField("SympHeadache", String.valueOf(SympHeadache));
            Parser.SetField("SympCongestion", String.valueOf(SympCongestion));
            Parser.SetField("SympShortBreath", String.valueOf(SympShortBreath));
            Parser.SetField("SympBodyache", String.valueOf(SympBodyache));

            Parser.SetField("SympChills", String.valueOf(SympChills));
            Parser.SetField("SympFatigue", String.valueOf(SympFatigue));
            Parser.SetField("SympSoreThroat", String.valueOf(SympSoreThroat));
            Parser.SetField("SympRunnyNose", String.valueOf(SympRunnyNose));
            Parser.SetField("SympDiffBreathin", String.valueOf(SympDiffBreathin));
            Parser.SetField("SympNausea", String.valueOf(SympNausea));
            Parser.SetField("SympLossSmellTaste", String.valueOf(SympLossSmellTaste));
            Parser.SetField("DateOfSymp", String.valueOf(DateOfSymp));

            //yed no options
            Parser.SetField("FullyVaccinated", String.valueOf(FullyVaccinated));
            Parser.SetField("FullyVaccinatedYes", String.valueOf(FullyVaccinatedYes));
            Parser.SetField("FullyVaccinatedNo", String.valueOf(FullyVaccinatedNo));
            Parser.SetField("HaveExposedYes", String.valueOf(HaveExposedYes));
            Parser.SetField("HaveExposedNo", String.valueOf(HaveExposedNo));
            Parser.SetField("FirstTimeCovidYes", String.valueOf(FirstTimeCovidYes));
            Parser.SetField("FirstTimeCovidNo", String.valueOf(FirstTimeCovidNo));
            Parser.SetField("HealthCareEmpYes", String.valueOf(HealthCareEmpYes));
            Parser.SetField("HealthCareEmpNo", String.valueOf(HealthCareEmpNo));
            Parser.SetField("InICUYes", String.valueOf(InICUYes));
            Parser.SetField("InICUNo", String.valueOf(InICUNo));
            Parser.SetField("InHospitalYes", String.valueOf(InHospitalYes));
            Parser.SetField("InHospitalNo", String.valueOf(InHospitalNo));
            Parser.SetField("IsResidentYes", String.valueOf(IsResidentYes));
            Parser.SetField("IsResidentNo", String.valueOf(IsResidentNo));
            Parser.SetField("HaveCloseContactYes", String.valueOf(HaveCloseContactYes));
            Parser.SetField("HaveCloseContactNo", String.valueOf(HaveCloseContactNo));
            Parser.SetField("HaveParticipatedYes", String.valueOf(HaveParticipatedYes));
            Parser.SetField("HaveParticipatedNo", String.valueOf(HaveParticipatedNo));
            Parser.SetField("HaveAskedYes", String.valueOf(HaveAskedYes));
            Parser.SetField("HaveAskedNo", String.valueOf(HaveAskedNo));
            Parser.SetField("IsPregnantYes", String.valueOf(IsPregnantYes));
            Parser.SetField("IsPregnantNo", String.valueOf(IsPregnantNo));


            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/Edit/CovidRegEdit.html");
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in PatientReg ** (EditValues_New Main Catch ^^" + facilityName + " ##MES#016)", servletContext, ex, "PatientReg", "EditValues_New", conn);
            Services.DumException("EditValues_New^^" + facilityName + " ##MES#016", "PatientReg ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "LabPatientRegistration");
            Parser.SetField("ActionID", "GetValues");
            Parser.SetField("Message", "MES#016");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    private void SaveEditData(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String databaseName, UtilityHelper helper, String directoryName, int facilityIndex, String userId, String Database) throws FileNotFoundException {
        PreparedStatement pStmt = null;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String RespParty = "";
        String Carrier = "";
        String GrpNumber = "";
        String MemId = "";
        String InsuranceConsent = "";
        int _InsCons = 0;
        final String MRN = request.getParameter("MRN").trim();
        final int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
        int PatientRegId = Integer.parseInt(request.getParameter("PatientRegId").trim());
        String FirstName = request.getParameter("FirstName").trim();
        String orderId = request.getParameter("orderId").trim();
        String LastName = request.getParameter("LastName").trim();
        String MiddleInitial = request.getParameter("MiddleInitial").trim();
        String DOB = request.getParameter("DOB").trim();
        String gender = request.getParameter("gender").trim();
        String PhNumber = request.getParameter("PhNumber").trim();
        String Email = request.getParameter("Email").trim();
        String Address = request.getParameter("Address").trim();
        String City = request.getParameter("City").trim();
        String State = request.getParameter("State").trim();
        String County = request.getParameter("County").trim();
        String ZipCode = request.getParameter("ZipCode").trim();
//        String Country = request.getParameter("Country").trim();
        String Ethnicity = request.getParameter("Ethnicity").trim();
        String Race = request.getParameter("Race").trim();
//        String Test = request.getParameter("Test").trim();
//        String testingSite = request.getParameter("testingSite").trim();
        String testingSite = null;
        String TesttingLoc = request.getParameter("TesttingLoc").trim();
        String sampleType = request.getParameter("sampleType").trim();
        String haveIns = request.getParameter("haveIns").trim();
        System.out.println("haveIns " + haveIns);
        if (haveIns.equals("Yes")) {
            RespParty = request.getParameter("RespParty").trim();
            Carrier = request.getParameter("Carrier").trim();
            GrpNumber = request.getParameter("GrpNumber").trim();
            MemId = request.getParameter("MemId").trim();
//            InsuranceConsent = request.getParameter("InsuranceConsent").trim();
//            if (InsuranceConsent.equals("Yes"))
//                _InsCons = 1;
        }

        String Patientconsent = request.getParameter("Patientconsent").trim();
        int _PatCons = 0;
        if (Patientconsent.equals("Yes"))
            _PatCons = 1;
        String Contactconsent = request.getParameter("Contactconsent").trim();
        int _ContCons = 0;
        if (Contactconsent.equals("Yes"))
            _ContCons = 1;

        String facilityName = helper.getFacilityName(request, conn, context, ClientId);

        String ClientIp = helper.getClientIp(request);
        try {
            pStmt = conn.prepareStatement(
                    "UPDATE " + Database + ".PatientReg SET FirstName = ? ,LastName = ? ,MiddleInitial = ? ," +
                            " Gender = ?,PhNumber = ? ,Email = ?,Address = ? ,City = ? ,State = ? ,County = ? ," +
                            " Ethnicity = ? ,Race  = ?, " +
                            " AtTestSite = ? ,TestingLocation = ? ," +
                            " Insured = ? ,RespParty  = ?,CarrierName = ? ,GrpNumber = ? ,MemID = ? ," +
                            " Status = 0 ,ModifiedDate =  NOW()," +
                            " DOB = ?, ZipCode = ?,UserIP = ?, sampleType = ?," +
                            " ModifiedBy = ?, Patientconsent = ? , " +
                            " Contactconsent = ?, InsuranceConsent = ?  " +
                            " WHERE ID = ? ");
            pStmt.setString(1, FirstName);
            pStmt.setString(2, LastName);
            pStmt.setString(3, MiddleInitial);
            pStmt.setString(4, gender);
            pStmt.setString(5, PhNumber);
            pStmt.setString(6, Email);
            pStmt.setString(7, Address);
            pStmt.setString(8, City);
            pStmt.setString(9, State);
            pStmt.setString(10, County);
            pStmt.setString(11, Ethnicity);
            pStmt.setString(12, Race);
//            pStmt.setString(13, Test);
            pStmt.setString(13, testingSite);
            pStmt.setString(14, TesttingLoc);
            pStmt.setString(15, haveIns);
            pStmt.setString(16, RespParty);
            pStmt.setString(17, Carrier);
            pStmt.setString(18, GrpNumber);
            pStmt.setString(19, MemId);
            pStmt.setString(20, DOB);
            pStmt.setString(21, ZipCode);
            pStmt.setString(22, ClientIp);
            pStmt.setString(23, sampleType);
            pStmt.setString(24, userId);
            pStmt.setInt(25, _PatCons);
            pStmt.setInt(26, _ContCons);
            pStmt.setInt(27, _InsCons);
            pStmt.setInt(28, PatientRegId);
//            System.out.println("UPDATE *** " + pStmt.toString());
            pStmt.executeUpdate();
            pStmt.close();
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (UPDATE LabPatientRegistration^^" + facilityName + " ##MES#001)", context, ex, "LabPatientRegistration", "UpdateData", conn);
            Services.DumException("UpdateData^^" + facilityName + " ##MES#001", "LabPatientRegistration ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "LabPatientRegistration");
            Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientId + "");
            Parser.SetField("Message", "MES#001");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
        }

        Parsehtm Parser = new Parsehtm(request);
        Parser.SetField("Message", "Information has been updated!");
        Parser.SetField("FormName", "PatientRegRoverLab");
        Parser.SetField("ActionID", "PatientUpdateInformation&ID=" + PatientRegId + "&orderId=" + orderId);
        Parser.SetField("ClientId", String.valueOf(ClientId));
        Parser.SetField("MRN", String.valueOf(MRN));
        Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/Message.html");

/*
        int orderIdx = 0;
        try {
            Query = "Select Id from roverlab.TestOrder WHERE PatRegIdx = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                orderIdx = rset.getInt(1);
            rset.close();
            stmt.close();
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (UPDATE LabPatientRegistration^^" + facilityName + " ##MES#001)", context, ex, "LabPatientRegistration", "UpdateData", conn);
            Services.DumException("UpdateData^^" + facilityName + " ##MES#002-a", "LabPatientRegistration ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "LabPatientRegistration");
            Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientId + "");
            Parser.SetField("Message", "MES#002-a");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");

        }


        try {

            Query = "SELECT * FROM roverlab.Tests WHERE OrderId";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()){
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO roverlab.TestsHistory " +
                                "(OrderId, CreatedDate, CreatedBy, TestIdx, TestStageIdx,CollectionDateTime) " +
                                "VALUES (?,NOW(),'Web User',?,0,NOW()) ");
                MainReceipt.setInt(1, orderIdx);
                MainReceipt.setString(2, Test);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }
            rset.close();
            stmt.close();

            pStmt = conn.prepareStatement("DELETE FROM roverlab.Tests WHERE OrderId = ?");
            pStmt.setInt(1, orderIdx);
            pStmt.executeUpdate();
            pStmt.close();

            PreparedStatement MainReceipt = conn.prepareStatement(
                    "INSERT INTO " + Database + ".Tests " +
                            "(OrderId, CreatedDate, CreatedBy, TestIdx, TestStageIdx,CollectionDateTime) " +
                            "VALUES (?,NOW(),'Web User',?,0,NOW()) ");
            MainReceipt.setInt(1, orderIdx);
            MainReceipt.setString(2, Test);
            MainReceipt.executeUpdate();
            MainReceipt.close();
        }  catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (UPDATE LabPatientRegistration^^" + facilityName + " ##MES#001)", context, ex, "LabPatientRegistration", "UpdateData", conn);
            Services.DumException("UpdateData^^" + facilityName + " ##MES#002", "LabPatientRegistration ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "LabPatientRegistration");
            Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientId + "");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
        }
*/
    }

    private void GetPatientsMainScreen(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            PreparedStatement pStmt = null;
            ResultSet rset = null;
            String Query = "";
            String Patient = request.getParameter("Patient").trim();
            StringBuffer PatientList = new StringBuffer();
            int i = 0;
            Query = "Select a.Id, a.MRN, IFNULL(a.FirstName,''), IFNULL(a.MiddleInitial,''), " +
                    "IFNULL(a.LastName,''), IFNULL(a.DOB,''), " +
                    "IFNULL(a.PhNumber,''),a.`Status`, b.Id as OrderIdx \n" +
                    "FROM " + Database + ".PatientReg a " +
                    " INNER JOIN " + Database + ".TestOrder b ON a.ID = b.PatRegIdx \n" +
                    "WHERE a.status = 0 and " +
                    "CONCAT(a.FirstName,a.LastName,a.PhNumber,a.MRN,IFNULL(DATE_FORMAT(a.DOB,'%m-%d-%Y'),''),b.OrderNum) like \"%" + Patient + "%\" ";

            //stmt = conn.createStatement();
            pStmt = conn.prepareStatement(Query);
            rset = pStmt.executeQuery();
            while (rset.next()) {
                if (i == 0) {
                    PatientList.append("<select class=\"form-control\" id=\"PatientId\" name=\"PatientId\" onchange=\"OpenPatients(" + rset.getInt(1) + "," + rset.getInt(9) + ");\">");
                    PatientList.append("<option value=''> Please Select Below Patient </option>");
                }
                PatientList.append("<option value=" + rset.getInt(1) + ">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
                i++;
            }
            rset.close();
            pStmt.close();
            PatientList.append("</select>");
            out.println(PatientList.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void CheckRepeatPatient(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String FirstNameR = request.getParameter("FirstNameR").trim();
            String LastNameR = request.getParameter("LastNameR").trim();
            String DOBR = request.getParameter("DOBR").trim();
            String genderR = request.getParameter("genderR").trim();
            String PhR = request.getParameter("PhR").trim();
            String emailR = request.getParameter("emailR").trim();
            int ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());


            Query = "Select dbname from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                Database = rset.getString(1);
            rset.close();
            stmt.close();

            int PatientFound = 0;
            int PatRegIdx = 0;
            String FoundMRN = "";
            String FullName = "";
            Query = " Select COUNT(*), IFNULL(MRN,0),CONCAT(FirstName,' ',LastName),Id AS PatRegIdx " +
                    " from " + Database + ".PatientReg  " +
                    "where Status = 0 and " +
                    "ltrim(rtrim(UPPER(FirstName))) = ltrim(rtrim(UPPER('" + FirstNameR.trim() + "')))  and " +
                    "ltrim(rtrim(UPPER(LastName))) = ltrim(rtrim(UPPER('" + LastNameR.trim() + "'))) and " +
                    "ltrim(rtrim((DOB))) = '" + DOBR + "' AND " +
                    "ltrim(rtrim((PhNumber))) = '" + PhR + "' AND " +
                    "ltrim(rtrim((Email))) = '" + emailR + "' ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientFound = rset.getInt(1);
                FoundMRN = rset.getString(2);
                FullName = rset.getString(3);
                PatRegIdx = rset.getInt(4);
            }
            rset.close();
            stmt.close();

            //out.println(PatientFound + "|" +FullName);
            Parsehtm Parser = new Parsehtm(request);
            if (PatientFound > 0) {
                Parser.SetField("FullName", FullName);
                Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
                Parser.SetField("PatRegIdx", String.valueOf(PatRegIdx));
                Parser.SetField("FoundMRN", FoundMRN);
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "MasterDef/ExistingRoverLab.html");
            } else {
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "MasterDef/NotExistingRoverLab.html");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void DisplayExistingPatient(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            int ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());
            int PatRegIdx = Integer.parseInt(request.getParameter("PatRegIdx").trim());
            int FoundMRN = Integer.parseInt(request.getParameter("FoundMRN").trim());
            String FullName = request.getParameter("FullName").trim();

            StringBuilder TestList = new StringBuilder();
            StringBuilder LocationList = new StringBuilder();

            Query = "Select dbname from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                Database = rset.getString(1);
            rset.close();
            stmt.close();

            Query = "SELECT Id,TestName FROM " + Database + ".ListofTests " +
                    "WHERE Status = 0 ORDER BY TestName ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            //TestList.append("<option value='-1' selected>None</option>");
            while (rset.next()) {
                if (rset.getInt(1) == 1)
                    TestList.append("<option value=\"" + rset.getInt(1) + "\" selected>" + rset.getString(2) + " </option>");
                else
                    TestList.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();

            Query = "SELECT Id,Location FROM " + Database + ".Locations " +
                    "WHERE Status = 0 ORDER BY Location ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            LocationList.append("<option value='-1' selected>None</option>");
            while (rset.next()) {
                LocationList.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FullName", FullName);
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("PatRegIdx", String.valueOf(PatRegIdx));
            Parser.SetField("FoundMRN", String.valueOf(FoundMRN));
            Parser.SetField("TestList", TestList.toString());
            Parser.SetField("LocationList", LocationList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/PRF_files/CovidRegExisting.html");
            out.close();
            out.flush();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void SaveExistingPatient(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, HttpServletResponse response) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String PatientId = "";
            String DocumentName = "";
            String ClientIndex = "";
            int PremisisId = 0;
            int PatientRegId = 0;
            String PatientMRN = "";
            String PatientName = "";
            String DirectoryName = "";
            String UploadPath = null;


            boolean IdFound = false;
            boolean FileFound = false;
            boolean insuranceF = false;
            boolean insuranceB = false;
            byte[] Data = null;
            String key = "";
            String filename = "";
            String IDs = "";
            String InsuranceIDsF = "";
            String InsuranceIDsB = "";
            String Idfront = "";
            String insuranceFront = "";
            String insuranceBack = "";
            String IdfrontName = "";
            String insuranceFrontName = "";
            String insuranceBackName = "";
            String value = "";


            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(
                        "Select Id, dbname,DirectoryName from oe.clients where Id =" + ClientId);
                rset = ps.executeQuery();
                if (rset.next()) {
                    PremisisId = rset.getInt(1);
                    Database = rset.getString(2);
                    DirectoryName = rset.getString(3);
                }
                rset.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            HashMap<String, String> valuemap = new HashMap<String, String>();

            try {
                Dictionary d = doUpload(request, response, out);
                Enumeration<String> en = d.keys();
                while (en.hasMoreElements()) {
                    key = en.nextElement();
                    FileFound = false;
                    if (!(key.startsWith("Idfront") || key.startsWith("insuranceFront") || key.startsWith("insuranceBack"))) {
                        value = (String) d.get(key);
                        valuemap.put(key, value.substring(4));
                    }
                    if (key.startsWith("ClientIndex")) {
                        ClientIndex = (String) d.get(key);
                    } else if ((key.startsWith("Idfront") && key.endsWith(".jpg")) || (key.startsWith("Idfront") && key.endsWith(".png"))) {
                        filename = key;
                        FileFound = true;
                        IdFound = true;
                        IDs = key;
                        ByteArrayOutputStream baos = null;
                        baos = (ByteArrayOutputStream) d.get(key);
                        Data = baos.toByteArray();
                    } else if ((key.startsWith("insuranceFront") && key.endsWith(".jpg")) || (key.startsWith("insuranceFront") && key.endsWith(".png"))) {
                        filename = key;
                        FileFound = true;
                        InsuranceIDsF = key;
                        insuranceF = true;

                        ByteArrayOutputStream baos = null;
                        baos = (ByteArrayOutputStream) d.get(key);
                        Data = baos.toByteArray();
                    } else if ((key.startsWith("insuranceBack") && key.endsWith(".jpg")) || (key.startsWith("insuranceBack") && key.endsWith(".png"))) {
                        filename = key;
                        FileFound = true;
                        insuranceB = true;
                        InsuranceIDsB = key;

                        ByteArrayOutputStream baos = null;
                        baos = (ByteArrayOutputStream) d.get(key);
                        Data = baos.toByteArray();
                    }
                    if (FileFound) {
                        UploadPath = "/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "/ExistingPatDoc/";
                        filename = filename.replaceAll("\\s+", "");

                        File fe = new File(String.valueOf(String.valueOf(UploadPath)) + filename);
                        if (fe.exists())
                            fe.delete();
                        FileOutputStream fouts = new FileOutputStream(fe);
                        fouts.write(Data);
                        fouts.flush();
                        fouts.close();
                    }
                }

                ClientIndex = ClientIndex.substring(4);

                UtilityHelper helper = new UtilityHelper();

                SaveExistingData(request, out, conn, servletContext, Database, helper, DirectoryName, IDs, InsuranceIDsF, InsuranceIDsB, ClientIndex, valuemap, UserId);

            } catch (Exception e2) {
                out.println("Error in Upload DOcuments!!" + e2.getMessage());
                String str = "";
                for (int i = 0; i < (e2.getStackTrace()).length; i++)
                    str = str + e2.getStackTrace()[i] + "<br>";
                out.println(str);
            }
        } catch (Exception Ex) {
            System.out.println("SaveExistingPatient " + Ex.getMessage());
        }
    }

    private void SaveExistingData(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database, UtilityHelper helper, String DirectoryName, String IDs, String InsuranceIDsF, String InsuranceIDsB, String ClientId, HashMap<String, String> valuemap, String userId) throws FileNotFoundException {
        PreparedStatement ps = null;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        int DocIdx = 0;
        int AddmissionBundle = 0;
        int ClientIndex = 0;
        int PatRegIdx = 0;
        int MRN = 0;
        ClientIndex = Integer.parseInt(ClientId);

        String rqType = null;
        String UCID = null;
        String Title = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String MaritalStatus = "";
        String DOB = "";
        String Age = "";
        String gender = "";
        String Email = "";
        String PhNumber = "";
        String Address = "";
        String Address2 = "";
        String StreetAddress2 = "";
        String City = "";
        String State = "";
        String Country = "";
        String County = "";
        String ZipCode = "";
        String Ethnicity = "";
        String Race = "";
        String PhotoID = "";
        String SSN = "";
        String SpCarePhy = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        int TravellingChk = 0;
        String TravelWhen = "";
        String TravelWhere = "";
        String TravelHowLong = "";
        int COVIDExposedChk = 0;
        int COVIDPositveChk = 0;
        String CovidPositiveDate = "";
        String CovidExpWhen = "";
        String SympFever = "0";
        String SympBodyAches = "0";
        String SympSoreThroat = "0";
        String SympFatigue = "0";
        String SympRash = "0";
        String SympVomiting = "0";
        String SympDiarrhea = "0";
        String SympChills = "0";
        String SympRunnyNose = "0";
        String SympDifficultBreathing = "0";
        String SympNausea = "0";
        String SympFluSymptoms = "0";
        String SympHeadache = "0";
        String SympLossTaste = "0";
        String sympDate = "";
        String vaccOrnot = "";
        String suspecSymp = "";
        String SympShortBreath = "0";
        String SympCongestion = "0";
        String AddInfoTextArea = "";
        String GuarantorName = "";
        String GuarantorDOB = "";
        String GuarantorNumber = "";
        String GuarantorSSN = "";
        String SympEyeConjunctivitis = "0";
        String PriCarePhy = "";
        String ReasonVisit = "";
        int WorkersCompPolicy = 0;
        int MotorVehAccident = 0;
        String PriInsurance = "";
        String MemId = "";
        String GrpNumber = "";
        String Test = "";
        String AtTestSite = "";
        String TesttingLoc = "";
        String haveIns = "";
        String RespParty = "";
        String Carrier = "";

        String insuranceFront = "";
        String insuranceBack = "";
        String anySymp = "";

        String PriInsuranceName = "";
        String OtherInsuranceName = "";


        String PolicyHolder = "";
        String NoInsurance = "";
        String Covid_19_PCR = "";
        String Mid_turbinate_Nasal_Swab = "";
        String datetimeColected = "";
        String DateOnset = "";
        String COVID_Result = "";
        String specimentType = "";
        String specimenID = "";
        String ancestry = "";
        String datetimeofspecimen = "";
        String insuranceAgreement = "";
        String PolicyHolderDOB = "";
        String RelationshipToPH = "";
        String memberID = "";
        String PolicyType = "";

        String Providence = "";
        String PSSNtext = "";
        String SSLtext = "";
        String SIDtext = "";


        String InternationalTravel = "";
        String closeContact = "";
        String massGather = "";
        String askedBy = "";
        String testPositive = "";
        String healthCareEmp = "";
        String atICU = "";
        String atHosp = "";
        String PregOrNot = "";
        String atCongreCare = "";
        String sampleType = "";
        String Fever = "";
        String HowLongFever = "";
        String Cough = "";
        String HowLongCough = "";
        String breathShortness = "";
        String HowLongbreathShortness = "";
        String breathingDifficulty = "";
        String HowLongbreathingDifficulty = "";


        String ClinicName = "";
        String PhysicianName = "";
        String NPI = "";
        String AddressClinical = "";
        String CityClinical = "";
        String StateClinical = "";
        String ZipClinical = "";
        String Fax = "";
        String PhClinical = "";

        String EmailClinical = "";

        String SendReportTo = "";
        String PCFR = "";
        String TestType = "";


        String AddressIfDifferent = "";
        String PrimaryDOB = "";
        String PrimarySSN = "";
        String PatientRelationtoPrimary = "";
        String PrimaryOccupation = "";
        String PrimaryEmployer = "";
        String EmployerAddress = "";
        String EmployerPhone = "";
        String SecondryInsurance = "";
        String SubscriberName = "";
        String SubscriberDOB = "";
        String MemberID_2 = "";
        String GroupNumber_2 = "";
        String PatientRelationshiptoSecondry = "";
        String NextofKinName = "";
        String RelationToPatientER = "";
        String PhoneNumberER = "";
        int LeaveMessageER = 0;
        String AddressER = "";
        String CityER = "";
        String StateER = "";
        String CountryER = "";
        String ZipCodeER = "";
        int ReturnPatient = 0;
        int Google = 0;
        int MapSearch = 0;
        int Billboard = 0;
        int OnlineReview = 0;
        int TV = 0;
        int Website = 0;
        int BuildingSignDriveBy = 0;
        int Facebook = 0;
        int School = 0;
        String School_text = "";
        int Twitter = 0;
        int Magazine = 0;
        String Magazine_text = "";
        int Newspaper = 0;
        String Newspaper_text = "";
        int FamilyFriend = 0;
        String FamilyFriend_text = "";
        int UrgentCare = 0;
        String UrgentCare_text = "";
        int CommunityEvent = 0;
        String CommunityEvent_text = "";
        int Work = 0;
        String Work_text = "";
        int Physician = 0;
        String Physician_text = "";
        int Other = 0;
        String Other_text = "";
        int SelfPayChk = 0;
        int FrVisitedBefore = 0;
        int FrFamiliyVisitedBefore = 0;
        int FrInternet = 0;
        int FrBillboard = 0;
        int FrGoogle = 0;
        int FrBuildingSignage = 0;
        int FrFacebook = 0;
        int FrLivesNear = 0;
        int FrTwitter = 0;
        int FrTV = 0;
        int FrMapSearch = 0;
        int FrEvent = 0;
        String FrPhysicianReferral = "";
        String FrNeurologyReferral = "";
        String FrUrgentCareReferral = "";
        String FrOrganizationReferral = "";
        String FrFriendFamily = "";
        String PatientName = "";
        String ExtendedMRN = "";
        String VisitId = "";
        String CurrentDate = "";
        String facilityName = helper.getFacilityName(request, conn, servletContext, ClientIndex);


        if (valuemap.get("PatRegIdx") == null) {
            PatRegIdx = 0;
        } else {
            PatRegIdx = Integer.parseInt(valuemap.get("PatRegIdx").trim());
        }
//        System.out.println("PatRegIdx IN REPEAT " + PatRegIdx);
        if (valuemap.get("FoundMRN") == null) {
            MRN = 0;
        } else {
            MRN = Integer.parseInt(valuemap.get("FoundMRN").trim());
        }

        if (valuemap.get("PriInsurance") == null) {
            PriInsurance = "";
        } else {
            PriInsurance = valuemap.get("PriInsurance").trim();
        }
        if (valuemap.get("MemId") == null) {
            MemId = "";
        } else {
            MemId = valuemap.get("MemId").trim();
        }
        if (valuemap.get("GrpNumber") == null) {
            GrpNumber = "";
        } else {
            GrpNumber = valuemap.get("GrpNumber").trim();
        }

        if (valuemap.get("Test") == null) {
            Test = "";
        } else {
            Test = valuemap.get("Test").trim();
        }

        if (valuemap.get("testingSite") == null) {
            AtTestSite = "";
        } else {
            AtTestSite = valuemap.get("testingSite").trim();
        }

        if (valuemap.get("TesttingLoc") == null) {
            TesttingLoc = "";
        } else {
            TesttingLoc = valuemap.get("TesttingLoc").trim();
        }

        if (valuemap.get("haveIns") == null) {
            haveIns = "";
        } else {
            haveIns = valuemap.get("haveIns").trim();
        }


        if (valuemap.get("RespParty") == null) {
            RespParty = "";
        } else {
            RespParty = valuemap.get("RespParty").trim();
        }

        if (valuemap.get("Carrier") == null) {
            Carrier = "";
        } else {
            Carrier = valuemap.get("Carrier").trim();
        }

        if (valuemap.get("insuranceFront") == null) {
            insuranceFront = "";
        } else {
            insuranceFront = valuemap.get("insuranceFront").trim();
        }

        if (valuemap.get("insuranceBack") == null) {
            insuranceBack = "";
        } else {
            insuranceBack = valuemap.get("insuranceBack").trim();
        }


        if (valuemap.get("anySymp") == null) {
            anySymp = "";
        } else {
            anySymp = valuemap.get("anySymp").trim();
        }

        if (valuemap.get("SympFever") == null) {
            SympFever = "0";
        } else {
            SympFever = "1";
        }
        if (valuemap.get("SympDiarrhea") == null) {
            SympDiarrhea = "0";
        } else {
            SympDiarrhea = "1";
        }
        if (valuemap.get("SympHeadache") == null) {
            SympHeadache = "0";
        } else {
            SympHeadache = "1";
        }
        if (valuemap.get("SympCongestion") == null) {
            SympCongestion = "0";
        } else {
            SympCongestion = "1";
        }

        if (valuemap.get("SympShortBreath") == null) {
            SympShortBreath = "0";
        } else {
            SympShortBreath = "1";
        }
        if (valuemap.get("SympBodyAches") == null) {
            SympBodyAches = "0";
        } else {
            SympBodyAches = "1";
        }
        if (valuemap.get("SympChills") == null) {
            SympChills = "0";
        } else {
            SympChills = "1";
        }
        if (valuemap.get("SympFatigue") == null) {
            SympFatigue = "0";
        } else {
            SympFatigue = "1";
        }
        if (valuemap.get("SympSoreThroat") == null) {
            SympSoreThroat = "0";
        } else {
            SympSoreThroat = "1";
        }
        if (valuemap.get("SympRunnyNose") == null) {
            SympRunnyNose = "0";
        } else {
            SympRunnyNose = "1";
        }

        if (valuemap.get("SympDifficultBreathing") == null) {
            SympDifficultBreathing = "0";
        } else {
            SympDifficultBreathing = "1";
        }
        if (valuemap.get("SympVomiting") == null) {
            SympVomiting = "0";
        } else {
            SympVomiting = "1";
        }
        if (valuemap.get("SympLossTaste") == null) {
            SympLossTaste = "0";
        } else {
            SympLossTaste = "1";
        }

        if (valuemap.get("sympDate") == null) {
            sympDate = "";
        } else {
            sympDate = valuemap.get("sympDate").trim();
        }

        if (valuemap.get("vaccOrnot") == null) {
            vaccOrnot = "";
        } else {
            vaccOrnot = valuemap.get("vaccOrnot").trim();
        }

        if (valuemap.get("suspecSymp") == null) {
            suspecSymp = "";
        } else {
            suspecSymp = valuemap.get("suspecSymp").trim();
        }

        if (valuemap.get("closeContact") == null) {
            closeContact = "";
        } else {
            closeContact = valuemap.get("closeContact").trim();
        }


        if (valuemap.get("massGather") == null) {
            massGather = "";
        } else {
            massGather = valuemap.get("massGather").trim();
        }

        if (valuemap.get("askedBy") == null) {
            askedBy = "";
        } else {
            askedBy = valuemap.get("askedBy").trim();
        }

        if (valuemap.get("testPositive") == null) {
            testPositive = "";
        } else {
            testPositive = valuemap.get("testPositive").trim();
        }

        if (valuemap.get("healthCareEmp") == null) {
            healthCareEmp = "";
        } else {
            healthCareEmp = valuemap.get("healthCareEmp").trim();
        }


        if (valuemap.get("atICU") == null) {
            atICU = "";
        } else {
            atICU = valuemap.get("atICU").trim();
        }

        if (valuemap.get("atHosp") == null) {
            atHosp = "";
        } else {
            atHosp = valuemap.get("atHosp").trim();
        }

        if (valuemap.get("PregOrNot") == null) {
            PregOrNot = "";
        } else {
            PregOrNot = valuemap.get("PregOrNot").trim();
        }

        if (valuemap.get("atCongreCare") == null) {
            atCongreCare = "";
        } else {
            atCongreCare = valuemap.get("atCongreCare").trim();
        }
        if (valuemap.get("sampleType") == null) {
            sampleType = "";
        } else {
            sampleType = valuemap.get("sampleType").trim();
        }
        if (valuemap.get("SendReportTo") == null) {
            SendReportTo = "";
        } else {
            SendReportTo = valuemap.get("SendReportTo").trim();
        }

        PreparedStatement pStmt = null;
        String ClientIp = helper.getClientIp(request);
        try {

            Query = "SELECT FirstName ,LastName ,MiddleInitial ,Gender ,PhNumber ,Email ,Address ," +
                    " City ,State ,County ,Ethnicity ,Race ,PhotoID, Test ,AtTestSite ," +
                    " TestingLocation, Insured ,RespParty ,CarrierName ,GrpNumber ,MemID ," +
                    " InsuranceIDFront ,InsuranceIDBack , HaveSymptoms ,SympFever ,SympDiarrhea ," +
                    " SympHeadache ,SympCongestion ,SympShortBreath ,SympBodyache ,SympChills , " +
                    " SympFatigue ,SympSoreThroat ,SympRunnyNose ,SympDiffBreathin ,SympNausea , " +
                    " SympLossSmellTaste ,FullyVaccinated ,HaveExposed ,FirstTimeCovid ,HealthCareEmp , " +
                    " InICU ,InHospital ,IsPregnant ,IsResident ,HaveCloseContact ,HaveParticipated , " +
                    " HaveAsked ,DateOfSymp ,MRN ,ExtendedMRN ,Status ,CreatedDate, " +
                    " DOB,ZipCode,UserIP,StageIdx,SendReportTo " +
                    " FROM roverlab.PatientReg WHERE ID = " + PatRegIdx;
//            System.out.println("QUERY123 --> " + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO roverlab.PatientRegLabHistory (FirstName ,LastName ,MiddleInitial ,Gender ," +
                                    " PhNumber ,Email ,Address ,City ,State ,County ,Ethnicity ,Race ,PhotoID, " +
                                    " Test ,AtTestSite ,TestingLocation ," +
                                    " Insured ,RespParty ,CarrierName ,GrpNumber ,MemID ,InsuranceIDFront ,InsuranceIDBack ," +
                                    " HaveSymptoms ,SympFever ,SympDiarrhea ,SympHeadache ,SympCongestion ,SympShortBreath , " +
                                    " SympBodyache ,SympChills ,SympFatigue ,SympSoreThroat ,SympRunnyNose ,SympDiffBreathin , " +
                                    " SympNausea ,SympLossSmellTaste ,FullyVaccinated ,HaveExposed ,FirstTimeCovid ,HealthCareEmp , " +
                                    " InICU ,InHospital ,IsPregnant ,IsResident ,HaveCloseContact ,HaveParticipated ,HaveAsked , " +
                                    " DateOfSymp ,MRN ,ExtendedMRN ,Status ,CreatedDate," +
                                    " DOB,ZipCode,UserIP,StageIdx,SendReportTo,OldPatRegIdx)  " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                                    "?, ?, ?, " +
                                    "?, ?, ?, ?, ?, ?, ?, " +
                                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, NOW(),?,?,?,?,?,?) ");
                    MainReceipt.setString(1, rset.getString(1)); //FirstName
                    MainReceipt.setString(2, rset.getString(2));//LastName
                    MainReceipt.setString(3, rset.getString(3));//MiddleInitial
                    MainReceipt.setString(4, rset.getString(4));//Gender
                    MainReceipt.setString(5, rset.getString(5));//PhNumber
                    MainReceipt.setString(6, rset.getString(6));//Email
                    MainReceipt.setString(7, rset.getString(7));//Address
                    MainReceipt.setString(8, rset.getString(8));//City
                    MainReceipt.setString(9, rset.getString(9));//State
                    MainReceipt.setString(10, rset.getString(10));//County
                    MainReceipt.setString(11, rset.getString(11));//Ethnicity
                    MainReceipt.setString(12, rset.getString(12));//Race
                    MainReceipt.setString(13, rset.getString(13));//PhotoID
                    MainReceipt.setString(14, rset.getString(14));//Test
                    MainReceipt.setString(15, rset.getString(15));//AtTestSite
                    MainReceipt.setString(16, rset.getString(16));//TesttingLoc
                    MainReceipt.setString(17, rset.getString(17));//haveIns
                    MainReceipt.setString(18, rset.getString(18));//RespParty
                    MainReceipt.setString(19, rset.getString(19));//Carrier
                    MainReceipt.setString(20, rset.getString(20));//GrpNumber
                    MainReceipt.setString(21, rset.getString(21));//MemId
                    MainReceipt.setString(22, rset.getString(22));//insuranceFront
                    MainReceipt.setString(23, rset.getString(23));//insuranceBack
                    MainReceipt.setString(24, rset.getString(24));//anySymp
                    MainReceipt.setString(25, rset.getString(25));//SympFever
                    MainReceipt.setString(26, rset.getString(26));//SympDiarrhea
                    MainReceipt.setString(27, rset.getString(27));//SympHeadache
                    MainReceipt.setString(28, rset.getString(28));//SympCongestion
                    MainReceipt.setString(29, rset.getString(29));//SympShortBreath
                    MainReceipt.setString(30, rset.getString(30));//SympBodyAches
                    MainReceipt.setString(31, rset.getString(31));//SympChills
                    MainReceipt.setString(32, rset.getString(32));//SympFatigue
                    MainReceipt.setString(33, rset.getString(33));//SympSoreThroat
                    MainReceipt.setString(34, rset.getString(34));//SympRunnyNose
                    MainReceipt.setString(35, rset.getString(35));//SympDifficultBreathing
                    MainReceipt.setString(36, rset.getString(36));//SympVomiting
                    MainReceipt.setString(37, rset.getString(37));//SympLossTaste
                    MainReceipt.setString(38, rset.getString(38));//vaccOrnot
                    MainReceipt.setString(39, rset.getString(39));//suspecSymp
                    MainReceipt.setString(40, rset.getString(40));//testPositive
                    MainReceipt.setString(41, rset.getString(41));//healthCareEmp
                    MainReceipt.setString(42, rset.getString(42));//atICU
                    MainReceipt.setString(43, rset.getString(43));//atHosp
                    MainReceipt.setString(44, rset.getString(44));//PregOrNot
                    MainReceipt.setString(45, rset.getString(45));//atCongreCare
                    MainReceipt.setString(46, rset.getString(46));//closeContact
                    MainReceipt.setString(47, rset.getString(47));//massGather
                    MainReceipt.setString(48, rset.getString(48));//askedBy
                    MainReceipt.setString(49, rset.getString(49));//sympDate
                    MainReceipt.setInt(50, rset.getInt(50));//MRN
                    MainReceipt.setString(51, rset.getString(51));//ExtendedMRN
                    MainReceipt.setString(52, rset.getString(54));//DOB
                    MainReceipt.setString(53, rset.getString(55));//ZipCode
                    MainReceipt.setString(54, rset.getString(56));//ClientIp
                    MainReceipt.setString(55, rset.getString(57));//StageIdx
                    MainReceipt.setString(56, rset.getString(58));//SendReportTo
                    MainReceipt.setInt(57, PatRegIdx);//SendReportTo
//                    System.out.println("INSERTION *** " + MainReceipt.toString());
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception ex) {
                    helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (SaveData Insertion LabPatientRegistration^^" + facilityName + " ##MES#005)", servletContext, ex, "LabPatientRegistration", "SaveData", conn);
                    Services.DumException("SaveData^^" + facilityName + " ##MES#005", "LabPatientRegistration ", request, ex);
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "LabPatientRegistration");
                    Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
                    Parser.SetField("Message", "MES#005");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    return;
                }
            }
            rset.close();
            stmt.close();

            pStmt = conn.prepareStatement(
                    "UPDATE roverlab.PatientReg SET TestingLocation = ? ," +
                            " Insured = ? ,RespParty  = ?,CarrierName = ? ,GrpNumber = ? ,MemID = ? ," +
                            " Status = 0 ,ModifiedDate =  NOW(),UserIP = ?, sampleType = ?," +
                            " ModifiedBy = ?, Patientconsent = ? , " +
                            " Contactconsent = ?, InsuranceConsent = ?," +
                            " InsuranceIDFront = ? ,InsuranceIDBack  = ?, HaveSymptoms  = ?, " +
                            " SympFever  = ?,SympDiarrhea = ? , SympHeadache  = ?,SympCongestion  = ?," +
                            " SympShortBreath = ? ,SympBodyache = ? ,SympChills = ? , " +
                            " SympFatigue = ? ,SympSoreThroat = ? ,SympRunnyNose = ? ," +
                            " SympDiffBreathin  = ?,SympNausea = ? , " +
                            " SympLossSmellTaste = ? ,FullyVaccinated = ? ,HaveExposed  = ?," +
                            " FirstTimeCovid  = ?,HealthCareEmp = ? , " +
                            " InICU = ? ,InHospital = ? ,IsPregnant = ? ,IsResident = ? ," +
                            " HaveCloseContact = ? ,HaveParticipated = ?,HaveAsked = ? ,DateOfSymp= ?  " +
                            " WHERE ID = ? ");
            pStmt.setString(1, TesttingLoc);
            pStmt.setString(2, haveIns);
            pStmt.setString(3, RespParty);
            pStmt.setString(4, Carrier);
            pStmt.setString(5, GrpNumber);
            pStmt.setString(6, MemId);
            pStmt.setString(7, ClientIp);
            pStmt.setString(8, sampleType);
            pStmt.setString(9, userId);
            pStmt.setInt(10, 1);//Patientconsent
            pStmt.setInt(11, 1);//Contactconsent
            if (haveIns.equals("Yes"))
                pStmt.setInt(12, 1);//InsuranceConsent
            else
                pStmt.setInt(12, 0);//InsuranceConsent
            pStmt.setString(13, InsuranceIDsF);
            pStmt.setString(14, InsuranceIDsB);
            pStmt.setString(15, anySymp);
            pStmt.setString(16, SympFever);
            pStmt.setString(17, SympDiarrhea);
            pStmt.setString(18, SympHeadache);
            pStmt.setString(19, SympCongestion);
            pStmt.setString(20, SympShortBreath);
            pStmt.setString(21, SympBodyAches);
            pStmt.setString(22, SympChills);
            pStmt.setString(23, SympFatigue);
            pStmt.setString(24, SympSoreThroat);
            pStmt.setString(25, SympRunnyNose);
            pStmt.setString(26, SympDifficultBreathing);
            pStmt.setString(27, SympVomiting);
            pStmt.setString(28, SympLossTaste);
            pStmt.setString(29, vaccOrnot);
            pStmt.setString(30, suspecSymp);
            pStmt.setString(31, testPositive);
            pStmt.setString(32, healthCareEmp);
            pStmt.setString(33, atICU);
            pStmt.setString(34, atHosp);
            pStmt.setString(35, PregOrNot);
            pStmt.setString(36, atCongreCare);
            pStmt.setString(37, closeContact);
            pStmt.setString(38, massGather);
            pStmt.setString(39, askedBy);
            pStmt.setString(40, sympDate);

            pStmt.setInt(41, PatRegIdx);
//            System.out.println("UPDATE *** " + pStmt.toString());
            pStmt.executeUpdate();
            pStmt.close();
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (UPDATE LabPatientRegistration^^" + facilityName + " ##MES#001)", servletContext, ex, "LabPatientRegistration", "UpdateData", conn);
            Services.DumException("UpdateData^^" + facilityName + " ##MES#001", "LabPatientRegistration ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "LabPatientRegistration");
            Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientId + "");
            Parser.SetField("Message", "MES#001");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }


        int seqNo = 0;
        try {
            Query = "Select IFNULL(MAX(Convert(Substring(OrderNum,11,9),UNSIGNED INTEGER)),0) + 1  " +
                    "from " + Database + ".TestOrder ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                seqNo = rset.getInt(1);
            rset.close();
            stmt.close();
        } catch (Exception e) {
//                out.println("Error 3- :" + e.getMessage());
        }

        String OrderId = "OI-" + MRN + "-" + seqNo;
        try {
            PreparedStatement MainReceipt = conn.prepareStatement(
                    "INSERT INTO " + Database + ".TestOrder " +
                            "(PatRegIdx, OrderNum, OrderDate, OrderBy, Status, StageIdx) " +
                            " VALUES (?,?,NOW(),?,0,0) ");
            MainReceipt.setInt(1, PatRegIdx);
            MainReceipt.setString(2, OrderId);
            MainReceipt.setString(3, "Web Registration");
            MainReceipt.executeUpdate();
            MainReceipt.close();
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (SaveData Insertion Order Table^^" + facilityName + " ##MES#006)", servletContext, ex, "LabPatientRegistration", "SaveData", conn);
            Services.DumException("SaveData^^" + facilityName + " ##MES#006", "LabPatientRegistration ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "LabPatientRegistration");
            Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
            Parser.SetField("Message", "MES#006");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            return;
        }

        int orderIdx = 0;
        try {
            Query = "Select max(Id) from " + Database + ".TestOrder ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                orderIdx = rset.getInt(1);
            rset.close();
            stmt.close();
        } catch (Exception e) {
//                out.println("Error 3- :" + e.getMessage());
        }

        try {
            PreparedStatement MainReceipt = conn.prepareStatement(
                    "INSERT INTO " + Database + ".Tests " +
                            "(OrderId, CreatedDate, CreatedBy, TestIdx, TestStageIdx," +
                            "CollectionDateTime,SampleNumber) " +
                            "VALUES (?,NOW(),'Web User',?,0,NOW(),?) ");
            MainReceipt.setInt(1, orderIdx);
            MainReceipt.setString(2, Test);
            MainReceipt.setString(3, sampleType);
            MainReceipt.executeUpdate();
            MainReceipt.close();
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (SaveData Insertion Order Table^^" + facilityName + " ##MES#007)", servletContext, ex, "LabPatientRegistration", "SaveData", conn);
            Services.DumException("SaveData^^" + facilityName + " ##MES#007", "LabPatientRegistration ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "LabPatientRegistration");
            Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
            Parser.SetField("Message", "MES#007");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            return;
        }


        try {
            if (!IDs.equals("")) {
                ps = conn.prepareStatement("INSERT INTO " + Database + ".PatientDocUpload (UploadDocumentName,FileName,CreatedBy,CreatedDate,Status,PatientMRN,PatientRegId,PremisisId) VALUES (?,?,'PatientRegForm',NOW(),0,?,?,?)");
                ps.setString(1, "ID Front");
                ps.setString(2, IDs);
                ps.setInt(3, MRN);
                ps.setInt(4, PatRegIdx);
                ps.setInt(5, ClientIndex);
                ps.executeUpdate();
                ps.close();
            }


            if (!InsuranceIDsF.equals("")) {
                ps = conn.prepareStatement("INSERT INTO " + Database + ".PatientDocUpload (UploadDocumentName,FileName,CreatedBy,CreatedDate,Status,PatientMRN,PatientRegId,PremisisId) VALUES (?,?,'PatientRegForm',NOW(),0,?,?,?)");
                ps.setString(1, "Insurance ID Front");
                ps.setString(2, InsuranceIDsF);
                ps.setInt(3, MRN);
                ps.setInt(4, PatRegIdx);
                ps.setInt(5, ClientIndex);
                ps.executeUpdate();
                ps.close();
            }
            if (!InsuranceIDsB.equals("")) {
                ps = conn.prepareStatement("INSERT INTO " + Database + ".PatientDocUpload (UploadDocumentName,FileName,CreatedBy,CreatedDate,Status,PatientMRN,PatientRegId,PremisisId) VALUES (?,?,'PatientRegForm',NOW(),0,?,?,?)");
                ps.setString(1, "Insurance ID Back");
                ps.setString(2, InsuranceIDsB);
                ps.setInt(3, MRN);
                ps.setInt(4, PatRegIdx);
                ps.setInt(5, ClientIndex);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException e) {
            System.out.println("PatientDocUpload ROVER LAB Error ");
            System.out.println(e.getMessage());
        }

        try {
            int found = 0;
            Query = "Select Count(*) from " + Database + ".SignRequest where PatientRegId = " + PatRegIdx + "";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                found = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (found > 0) {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".SignRequest_History " +
                                "SELECT * FROM " + Database + ".SignRequest " +
                                "where PatientRegId = " + PatRegIdx);
                MainReceipt.executeUpdate();
                MainReceipt.close();


                Query = "Update " + Database + ".SignRequest set isSign = 0 , SignedFrom='EDIT' where PatientRegId = " + PatRegIdx;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            }

            // System.out.println("Sending an email from Reg Option ... ");
            // helper.SendEmailRoverLab("", "RoverLab Covid Registration", Sms, PtEmail, conn, servletContext, MRN, PatientName);
            String temp = SaveBundle(request, out, conn, Database, DirectoryName, PatRegIdx);

            String[] arr = temp.split("~");
            String FileName = arr[2];
            String outputFilePath = arr[1];
            String pageCount = arr[0];

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "New order has been registered. Press ok to proceed to sign the document.");
            Parser.SetField("MRN", "Registration Number: " + MRN);
            Parser.SetField("FormName", "LabPatientRegistration");
            Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex);
            Parser.SetField("pageCount", String.valueOf(pageCount));
            Parser.SetField("FileName", String.valueOf(FileName));
            Parser.SetField("PatientRegId", String.valueOf(PatRegIdx));
            Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("rqtype", rqType);
            Parser.SetField("UCID", UCID);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Exception/MessageRoverLab.html");

        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in LabPatientRegistration ** (SaveData Main Catch^^" + facilityName + " ##MES#014)", servletContext, ex, "LabPatientRegistration", "SaveData", conn);
            Services.DumException("SaveData^^" + facilityName + " ##MES#014", "LabPatientRegistration ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "LabPatientRegistration");
            Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
            Parser.SetField("Message", "MES#014");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }

    }

    void PayNow(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, Payments payments) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String PatientMRN = request.getParameter("i").trim();
        String rqtype = request.getParameter("j").trim();
        String OrderId = request.getParameter("k").trim();
        String PatientName = "";
        double TotalAmount = 0.0D;
        double PaidAmount = 0.0D;
        double BalAmount = 0.0D;
        double AmountToPay = 0.0D;
        String DOS = "";
        String WEB = "";
        String OrderIdx = "";
        String amt = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        StringBuilder DeviceList = new StringBuilder();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int SNo = 1;
        int InstallmentPlanFound = 0;
        StringBuilder installmentPlan = new StringBuilder();
        try {
            PreparedStatement ps = conn.prepareStatement("Select Id FROM " + Database + ".TestOrder " +
                    "  WHERE OrderNum=?");
            ps.setString(1, OrderId);
            rset = ps.executeQuery();
            while (rset.next()) {
                OrderIdx = rset.getString(1);
            }
            rset.close();
            ps.close();

            ps = conn.prepareStatement("Select TestIdx FROM " + Database + ".Tests " +
                    "  WHERE OrderID=?");
            ps.setString(1, OrderIdx);
            System.out.println("Query ->> " + ps.toString());
            rset = ps.executeQuery();
            while (rset.next()) {
                if (rset.getInt(1) == 1 || rset.getInt(1) == 2) {
                    amt = "50";
                } else if (rset.getInt(1) == 4)
                    amt = "100";
            }
            rset.close();
            ps.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientMRN", String.valueOf(PatientMRN));
            Parser.SetField("InvoiceNo", String.valueOf(PatientMRN));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("ClientIndex", String.valueOf(ClientId));
            Parser.SetField("DOS", DOS);
            Parser.SetField("OrderId", OrderId);
            Parser.SetField("AmountToPay", amt);
            if (rqtype.equals("nullGetValues")) {

                ps = conn.prepareStatement("SELECT website from oe.ClientsWebsite where clientID=?");
                ps.setInt(1, ClientId);
                rset = ps.executeQuery();
                if (rset.next()) {
                    WEB = rset.getString(1);
                }
                ps.close();
                rset.close();

                Parser.SetField("WEB", WEB);
            } else {
                Parser.SetField("WEB", "/md/md.LabPatientRegistration?ActionID=GetValues");
            }
            //Parser.SetField("checkAmountDisplay", checkAmountDisplay.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/PayNow_ROVERLAB.html");
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (PayNow -- 01)", servletContext, ex, "RegisteredPatients", "PayNow -- 01", conn);
            Services.DumException("PayNow", "RegisteredPatients ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.SetField("Message", "MES#021");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (PayNow)", servletContext, var11, "RegisteredPatients", "PayNow", conn);
            Services.DumException("Registered Patients", "PayNow", request, var11, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
        }
    }

    private void cardConnectPaymentSave(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) {

        double CCAmount = Double.parseDouble(request.getParameter("CCAmount").trim().replaceAll(",", ""));
        String CCExpiry = request.getParameter("CCExpiry").trim();
        String CCCVC = request.getParameter("CCCVC").trim();
        String CCnameCard = request.getParameter("CCnameCard").trim();
        String myToken = request.getParameter("mytoken").trim();
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
        String PatientMRN = request.getParameter("x0Y61008").trim();
        String Description = request.getParameter("Description").trim();
        String OrderId = request.getParameter("OrderId").trim();
//        int InstallmentPlanFound = Integer.parseInt(request.getParameter("InstallmentPlanFound").trim());

        System.out.println("ORDER ID ->> " + OrderId);
        int Paid = 0;
        int PayRecIdx = 0;
        int InstallmentPlanId = 0;
        double PaidAmount = 0.0D;
        double TotalAmount = 0.0D;
        double BalAmount = 0.0D;
        String ResponseType = "";
        String receipt = "";
        String facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        try {
            System.out.println("facilityIndex ->> " + facilityIndex);
            int checkCredentials = payments.checkCCCredentials(request, conn, facilityIndex, servletContext);
            if (checkCredentials == 0) {
                out.println("11~No account found. Please contact System Administrator.");
                return;
            }

/*            boolean addVerify = payments.addressVerification(request, conn, database, servletContext, Integer.parseInt(PatientMRN));
            System.out.println("addVerify " + addVerify);
            if (!addVerify) {
                out.println("12~Please complete the address details!");
                return;
            }*/
            System.out.println("HERE 4");

            String[] PatientInfo = getPatientInfo(request, conn, servletContext, database, PatientMRN, helper);
            System.out.println("HERE 3");

            String FName = PatientInfo[0];
            String LName = PatientInfo[1];
            String Name = FName + " " + LName;

            System.out.println("HERE 5");

            CCExpiry = CCExpiry.replace("/", "");
            String Values = PatientInfo[0] + "^" + PatientInfo[1] + "^" + PatientInfo[3] + "^" + PatientInfo[4] + "^" + PatientInfo[5] + "^" + PatientInfo[6] + "^" + PatientInfo[8] + "^" + CCAmount + "^" + myToken + "^" + CCExpiry + "^" + CCCVC;
            System.out.println("HERE 6");

            Values = encrypt(Values.trim());
            System.out.println("HERE 7");

            Values = Values.replace(" ", "");
            System.out.println("HERE 2");

//            Object[] invoiceMaster = payments.getInvoiceMasterDetails(request, conn, servletContext, database, InvoiceNo, PatientMRN);
//            PaidAmount = (double) invoiceMaster[0];
//            BalAmount = (double) invoiceMaster[1];
//            TotalAmount = (double) invoiceMaster[3];

/*            if (PaidAmount > TotalAmount) {
                out.println("11~Paid Amount Cannot be greater than Total Amount!");
                return;
            }*/
//            if (CCAmount > BalAmount) {
//                out.println("11~Amount should not be greater than Balance Due!");
//                return;
//            }
//            if (BalAmount == 0) {
//                out.println("11~No Balance amount left to pay!");
//                return;
//            }
            String receiptCounter = helper.getReceiptCounter(request, conn, servletContext, database);

            //String dateTime = "20210411232226";
            //dateTime = dateTime.substring(0, 4) + "-" + dateTime.substring(4, 6) + "-" + dateTime.substring(6, 8) + " " + dateTime.substring(8, 10) + ":" + dateTime.substring(10, 12) + ":00".replaceAll("\n", "");
            String printDate = "";
            String printTime = "";
            printDate = helper.printDateTime(request, conn, servletContext)[0];
            printTime = helper.printDateTime(request, conn, servletContext)[1];

/*            String FullName = "";
            String Address = "";
            String Phone = "";
            FullName = helper.receiptClientData(request, conn, servletContext, facilityIndex)[0];
            Address = helper.receiptClientData(request, conn, servletContext, facilityIndex)[1];
            Phone = helper.receiptClientData(request, conn, servletContext, facilityIndex)[2];*/

            //out.println("1~"+printDate+"~~West Orange, TX~Golden Triangle Emergency Center~4099204470~60XXXXXXXXXX0896~1.00~AsjadAzam~091814764910");
            // out.println("1~" + facilityName + "~" + InvoiceNo + "~Approval~" + BalAmount + "~" + CCAmount + "~Card~" + printDate + "~" + printTime + "~Asjad~" + receiptCounter + "~Frontline ER~Dallas, TX~2144999555~033234~106449012753~40XXXXXXXXXX0506~"+receiptCounter);

            CardConnectPayment cardConnectPayment = new CardConnectPayment();
            String[] Response = cardConnectPayment.InquireTransaction(Values, facilityIndex, conn);
            System.out.println("ResponseText " + Response[0] + "<br> ");
            System.out.println("cvvresp " + Response[1] + "<br> ");
            System.out.println("respcode " + Response[2] + "<br> ");
            System.out.println("entrymode " + Response[3] + "<br> ");
            System.out.println("authcode " + Response[4] + "<br> ");
            System.out.println("respproc " + Response[5] + "<br> ");
            System.out.println("respstat " + Response[6] + "<br> ");
            System.out.println("retref " + Response[7] + "<br> ");
            System.out.println("expiry " + Response[8] + "<br> ");
            System.out.println("AVS " + Response[9] + "<br> ");
            System.out.println("Receipt " + Response[10] + "<br> ");
            System.out.println("BinType " + Response[11] + "<br> ");
            System.out.println("Amount " + Response[12] + "<br>");
            System.out.println("AccountNo " + Response[13] + "<br>");
            System.out.println("orderId " + Response[14] + "<br>");
            System.out.println("commCard " + Response[15] + "<br>");

/*            JSONParser parser = new JSONParser();
            Object obj = parser.parse("[" + Response[10] + "]");
            JSONArray array = (JSONArray) obj;
            JSONObject obj2 = (JSONObject) array.get(0);
            String dateTime = (String) obj2.get("dateTime");
            String dba = (String) obj2.get("dba");
            String address2 = (String) obj2.get("address2");
            String phone = (String) obj2.get("phone");
            String footer = (String) obj2.get("footer");
            String nameOnCard = (String) obj2.get("nameOnCard");
            String address1 = (String) obj2.get("address1");
            String orderNote = (String) obj2.get("orderNote");
            String header = (String) obj2.get("header");
            String items = (String) obj2.get("items");*/
            String FullName = "";
            String Address = "";
            String Phone = "";

            System.out.println("HERE 1");

            FullName = helper.receiptClientData(request, conn, servletContext, facilityIndex)[0];
            Address = helper.receiptClientData(request, conn, servletContext, facilityIndex)[1];
            Phone = helper.receiptClientData(request, conn, servletContext, facilityIndex)[2];


            //out.println("1~"+dateTime+"~"+address1+"~"+address2+"~"+dba+"~"+phone+"~"+Response[13]+"~"+Response[12]+"~"+nameOnCard);
            //out.println("1~20210402100916~~West Orange, TX~Golden Triangle Emergency Center~4099204470~60XXXXXXXXXX0896~1.00~AsjadAzam~091814764910");

            String UserIP = helper.getClientIp(request);
            String CurrDate = helper.getCurrDate(request, conn);
            if (Response[0].equals("Approval") || Response[0].equals("APPROVAL") ||
                    Response[0].equals("Success") || Response[0].equals("SUCCESS")) {
//            if (true) {

                System.out.println("SUCCESSFULL");
//                if (CCAmount == BalAmount) {
                Paid = 1;
//                }

                TotalAmount=CCAmount;

                InvoiceNo = SaveInvoice(out,conn,userId,database,PatientMRN,TotalAmount,OrderId);

                payments.insertInvoiceMasterHistory(request, conn, servletContext, database, PatientMRN, InvoiceNo, UserIP);

                payments.updateInvoiceMaster(request, conn, servletContext, database, PaidAmount, CCAmount, BalAmount, Paid, PatientMRN, InvoiceNo);

                receipt = "<div id='printThis'><div id='mystyle' style='width: 377.68px;border: 1px; border-style: solid; border-color: black;'><div class='reciept' style='margin-top:20px;text-align: center;'><p style='margin:0px;'>" + FullName + "</p><p style='margin:0px;'>" + Address + "</p><p style='margin:0px;'>" + Phone + "</p></div><div class='reciept' style='margin-left: 5px; text-align:left !important'><p style='margin:0px;'>Ref #: " + Response[7] + "</p><p style='margin:0px;'>Status: " + Response[0] + "</p><p style='margin:0px;'>Auth #: " + Response[4] + "</p><p style='margin:0px;'>MID: " + Response[13] + "</p><p style='margin:0px;'>Receipt#: " + receiptCounter + "</p></div><div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>" + printDate + "</span> <span style='margin-left: 166px'>" + printTime + "</span></div><div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>Amount</span> <span style='margin-left: 199px'>$" + CCAmount + "</span></div><div class='reciept' style='margin-left: 5px;text-align:left !important'><p style='margin:0px;'>Method: Card</p><p style='margin:0px;'>" + Name + "</p></div><div class='reciept' style='text-align: center;'>Approved</div><div class='reciept' style='margin-bottom: 15px; text-align: center;'>Thank you. Please come again</div> <div class='reciept' style='text-align: right;font-size:12px;'>Printed By Rover</div>  </div></div>";

                payments.paymentReceiptInsertion(request, conn, servletContext, database, PatientMRN, InvoiceNo, TotalAmount, Paid, InvoiceNo, Description, "1", BalAmount, userId, UserIP, "cardConnectPaymentSave_ROVERLAB", CCAmount, receiptCounter, receipt);


                PayRecIdx = payments.getPaymentReceiptIndex(request, conn, servletContext, database);
                ResponseType = "SUCCESS";
//                payments.insertionCardConnectResponses(request, conn, servletContext, database, InvoiceNo, PatientMRN, Response[0], Response[1] == null ? "" : Response[1], Response[2] == null ? "" : Response[2], Response[3] == null ? "" : Response[3], Response[4] == null ? "" : Response[4], Response[5] == null ? "" : Response[5], Response[6] == null ? "" : Response[6], Response[7] == null ? "" : Response[7], Response[8] == null ? "" : Response[8], Response[9] == null ? "" : Response[9], CurrDate, 0, facilityIndex, myToken, CurrDate, CCCVC, Response[11] == null ? "" : Response[11], Response[10] == null ? "" : Response[10], ResponseType, Description, CCAmount, Response[13] == null ? "" : Response[13], Response[14] == null ? "" : Response[14], Response[15] == null ? "" : Response[15], UserIP, "cardConnectPaymentSave", CCnameCard, PayRecIdx, "2", "No Insurance", receipt, "No File", userId);

                //out.println("1~" + dateTime + "~" + address1 + "~" + address2 + "~" + dba + "~" + phone + "~" + Response[13] + "~" + Response[12] + "~" + nameOnCard + "~" + Response[7]);
                //out.println("1~" + facilityName + "~" + InvoiceNo + "~" + Response[0] + "~" + BalAmount + "~" + CCAmount + "~Card~" + printDate + "~" + printTime + "~" + Name + "~" + receiptCounter + "~" + FullName + "~" + Address + "~" + Phone + "~" + Response[4] + "~" + Response[7] + "~" + Response[13] + "~" + receiptCounter);

                out.println("1~" + receipt);
            } else {
//                System.out.println("UNSUCCESSFULL");

                ResponseType = "ERROR";
                payments.insertionCardConnectResponses(request, conn, servletContext, database, InvoiceNo, PatientMRN, Response[0], Response[1] == null ? "" : Response[1], Response[2] == null ? "" : Response[2], Response[3] == null ? "" : Response[3], Response[4] == null ? "" : Response[4], Response[5] == null ? "" : Response[5], Response[6] == null ? "" : Response[6], Response[7] == null ? "" : Response[7], Response[8] == null ? "" : Response[8], Response[9] == null ? "" : Response[9], CurrDate, 0, facilityIndex, myToken, CurrDate, CCCVC, Response[11] == null ? "" : Response[11], Response[10] == null ? "" : Response[10], ResponseType, Description, CCAmount, Response[13] == null ? "" : Response[13], Response[14] == null ? "" : Response[14], Response[15] == null ? "" : Response[15], UserIP, "cardConnectPaymentSave", CCnameCard, PayRecIdx, "2", "No Insurance", "No Slip", "No File", userId);
                //payments.insertionCardConnectResponses(request, conn, servletContext, database, InvoiceNo, PatientMRN, Response[0], "", "", "", "", "", "", Response[7], Response[8], Response[9], CurrDate, 0, facilityIndex, myToken, CurrDate, CCCVC, Response[11], Response[10], ResponseType, Description, CCAmount, Response[13], Response[14], Response[15], UserIP, "cardConnectPaymentSave", CCnameCard, PayRecIdx, "2", "No Insurance", "No Slip", "No File");
                out.println("0~" + Response[0]);
            }
        } catch (Exception Ex) {
            out.println("0~Error while making payment.");
            helper.SendEmailWithAttachment("Error in LabPatientRegistration_Test ^^ (Occurred At : " + facilityName + ") ** (LabPatientRegistration_Test)", servletContext, Ex, "LabPatientRegistration_Test", "cardConnectPaymentSave_ROVERLAB", conn);
            Services.DumException("LabPatientRegistration_Test", "cardConnectPaymentSave_ROVERLAB", request, Ex, getServletContext());

/*            String Message = Ex.getMessage() + " <br> ";
            for (int i = 0; i < Ex.getStackTrace().length; ++i) {
                Message = Message + Ex.getStackTrace()[i] + " ******* <br>";
            }
            out.println(Message);*/
            /*            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "PayNow&InvoiceNo='" + InvoiceNo + "'");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
        }

    }

    public static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

    private static Key generateKey() throws Exception {
        final Key key = new SecretKeySpec(keyValue, "AES");
        return key;
    }

    String SaveInvoice(PrintWriter out, Connection conn, String UserId, String Database, String _MRN, double _Total, String OrderId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        double TotalAmount = _Total;
        int i = 0;
        int j = 0;
        int k = 0;
        String InvoiceNo = "";
        int InvoiceMasterId = 0;
        String PatientMRN = _MRN;

        try {

            try {
                Query = "SELECT IFNULL(MAX(Id),0) + 1, DATE_FORMAT(now(),'%y-%m-%d') FROM " + Database + ".InvoiceMaster";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    InvoiceNo = "Inv_" + rset.getString(2) + "_" + rset.getInt(1);
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error 1-  in Getting Invoice No: " + e.getMessage());
            }
            try {
                PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".InvoiceMaster " +
                        " (PatientMRN,InvoiceNo,TotalAmount ,PaidAmount,Paid,PaymentDateTime,InvoiceCreatedBy " +
                        " ,CreatedDate, Status, BalAmount,InstallmentApplied,OrderID) " +
                        " VALUES (?,?,?,?,?,?,?,now(),0,?,0,?) ");
                MainReceipt.setString(1, PatientMRN);
                MainReceipt.setString(2, InvoiceNo);
                MainReceipt.setDouble(3, TotalAmount);
                MainReceipt.setDouble(4, 0.0D);
                MainReceipt.setInt(5, 0);
                MainReceipt.setString(6, "0000-00-00");
                MainReceipt.setString(7, UserId);
                MainReceipt.setDouble(8, TotalAmount);
                MainReceipt.setString(9, OrderId);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                System.out.println("Error 2- Insertion InvoiceMaster Table :" + e.getMessage());
            }

            return InvoiceNo;
        } catch (Exception var11) {
            out.println("Error: " + var11.getMessage());
            String str = "";
            for (i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
        return null;
    }

    public String[] getPatientInfo(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String PatientMRN, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        String FirstName = "";
        String LastName = "";
        String Address = "";
        String City = "";
        String State = "";
        String ZipCode = "";
        String Country = "";
        String PhNumber = "";
        String Email = "";
        String Title = "";
        String MiddleInitial = "";
        String MaritalStatus = "";
        String MRN = "";
        String DOB = "";
        String Age = "";
        String Gender = "";
        String SSN = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        String SelfPayChk = "";
        String CreatedDate = "";
        String Address2 = "";

        try {
            Query = "SELECT FirstName, LastName, IFNULL(Email,''), IFNULL(City,'N/A'), IFNULL(State,'N/A'), IFNULL(ZipCode,'N/A'), " + //6
                    "IFNULL(Country,'N/A'), IFNULL(PhNumber,'N/A'), IFNULL(Address,'N/A') " + //9
                    "FROM " + database + ".PatientReg  " +
                    "WHERE MRN = '" + PatientMRN + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FirstName = rset.getString(1);
                LastName = rset.getString(2);
                Email = rset.getString(3);
                City = rset.getString(4);
                State = rset.getString(5);
                ZipCode = rset.getString(6);
                Country = rset.getString(7);
                PhNumber = rset.getString(8);
                Address = rset.getString(9);
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in getPatientInfo_ROVERLAB ", servletContext, Ex, "MD - LabPatientRegistration", "getPatientInfo_ROVERLAB", conn);
            Services.DumException("LabPatientRegistration - getPatientInfo_ROVERLAB", "getPatientInfo_ROVERLAB", request, Ex, getServletContext());
        }
        return new String[]{FirstName, LastName, Email, City, State, ZipCode, Country, PhNumber, Address};
    }

    void TransactionReport_Input(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer PatientInvoiceList = new StringBuffer();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        try {
            Query = " SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                    "a.InvoiceNo FROM " + Database + ".InvoiceMaster a  " +
                    " LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN " +
                    " where b.Status = 0 GROUP BY a.PatientMRN";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1).trim() + "," + rset.getString(3).trim() + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
            rset.close();
            stmt.close();

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientInvoiceList", String.valueOf(PatientInvoiceList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/TransactionReport_ROVERLAB.html");
        } catch (Exception var11) {
            out.println(Query);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    private void showReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, UtilityHelper helper, int ClientId, int userIndex) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query2 = "";
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query = "";

        int Found = 0;
        int SNo = 1;
        String PatientId = "";
        String FromDate = "";
        String ToDate = "";
        StringBuffer PatientInvoiceList = new StringBuffer();
        StringBuffer TransactionList = new StringBuffer();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int SearchBy = Integer.parseInt(request.getParameter("SearchByVal").trim());
        String RetRef = "N/A";
        String ResponseText = "N/A";
        String AccountNo = "N/A";
        String filter = "";
        Boolean isAdmin = false;

        try {

            PreparedStatement ps = conn.prepareStatement("SELECT IsAdmin FROM oe.UserRights WHERE SysUserID=?");
            ps.setInt(1, userIndex);
            rset = ps.executeQuery();
            if (rset.next()) {
                if (rset.getInt(1) == 1) {
                    isAdmin = true;
                }
            }
            rset.close();
            ps.close();

            if (!isAdmin) {
                filter = " AND c.InvoiceCreatedBy='" + UserId + "'";
            }

            if (SearchBy == 1) {
                PatientId = request.getParameter("PatientId").trim();
                String[] parts = PatientId.split("\\,");
                String MRN = parts[0];
                String InvoiceNo = parts[1];
                Query = " SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                        "a.InvoiceNo  " +
                        " FROM " + Database + ".InvoiceMaster a  " +
                        " LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN  " +
                        " WHERE b.Status = 0 GROUP BY a.PatientMRN";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    if (rset.getString(1).equals(MRN)) {
                        PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1).trim() + "," + rset.getString(3).trim() + "\" selected>" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
                        continue;
                    }
                    PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1).trim() + "," + rset.getString(3).trim() + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
                }
                rset.close();
                stmt.close();

                Query = " Select a.PatientMRN , CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                        "a.InvoiceNo, a.TotalAmount, a.PaidAmount,  a.BalAmount, IFNULL(a.RefNo,'-'), IFNULL(a.Remarks,'-') , " +
                        "DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T') as PaymentDate, DATE_FORMAT(c.CreatedDate, '%m/%d/%Y %T') as InvoiceDate,  " +
                        "CASE " +
                        "WHEN PayMethod = 1 THEN 'Credit Card' " +
                        "WHEN PayMethod = 2 THEN 'Cash' " +
                        "WHEN PayMethod = 3 THEN 'BOLT Device' " +
                        "WHEN PayMethod = 4 THEN 'Ingenico' " +
                        "ELSE '' END, IFNULL(PayMethod,0),IFNULL(a.Id,'N/A'),IFNULL(c.OrderID,'N/A')  " +
                        "FROM " + Database + ".PaymentReceiptInfo a " +
                        "LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN " +
                        "LEFT JOIN " + Database + ".InvoiceMaster c on a.InvoiceNo = c.InvoiceNo  " +
                        "WHERE a.PatientMRN = '" + MRN + "' AND c.Status = 0 AND b.status=0 " + filter + " ORDER BY a.CreatedDate DESC ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    RetRef = "N/A";
                    ResponseText = "N/A";
                    AccountNo = "N/A";
                    if (rset.getInt(12) == 1) {
                        Query2 = "SELECT RetRef,ResponseText,AccountNo FROM " + Database + ".CardConnectResponses " +
                                "WHERE PatientMRN = " + rset.getInt(1) + " AND InvoiceNo = '" + rset.getString(3) + "' ";
                        stmt2 = conn.createStatement();
                        rset2 = stmt2.executeQuery(Query2);
                        if (rset2.next()) {
                            RetRef = rset2.getString(1);
                            ResponseText = rset2.getString(2);
                            AccountNo = rset2.getString(3);
                        }
                        rset2.close();
                        stmt2.close();

                    }
                    if (rset.getInt(12) == 3) {
                        Query2 = "SELECT JSON_Response FROM " + Database + ".JSON_Response " +
                                "WHERE PatientMRN = " + rset.getInt(1) + " AND InvoiceNo = '" + rset.getString(3) + "' ";
                        stmt2 = conn.createStatement();
                        rset2 = stmt2.executeQuery(Query2);
                        if (rset2.next()) {
                            JSONParser parser = new JSONParser();
                            Object obj = parser.parse("[" + rset2.getString(1) + "]");
                            JSONArray array = (JSONArray) obj;
                            JSONObject obj2 = (JSONObject) array.get(0);
                            RetRef = (String) obj2.get("retref");
                            ResponseText = (String) obj2.get("resptext");
                        }
                        rset2.close();
                        stmt2.close();
                    }

                    TransactionList.append("<tr id=\"Tran_" + SNo + "\">");
                    TransactionList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(14) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(4)) + "</td>\n");
                    TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(5)) + "</td>\n");
                    TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(6)) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    if (rset.getString(11).trim().equals("Cash")) {
                        if (ClientId == 27 || ClientId == 29) {
                            TransactionList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=CashReceipt&MRN=" + rset.getInt(1) + "&InvoiceNo=" + rset.getString(3) + ">" + rset.getString(11) + "</a></td>\n");
                        } else {
                            TransactionList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                        }
                    } else {
                        TransactionList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    }
                    TransactionList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                    TransactionList.append("<td align=left>" + RetRef + "</td>\n");
                    TransactionList.append("<td align=left>" + ResponseText + "</td>\n");
                    TransactionList.append("<td align=left>" + AccountNo + "</td>\n");
                    if (rset.getInt(12) != 3) {
                        TransactionList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info\" onclick=\"ShowReceipt(" + rset.getInt(13) + "," + MRN + ")\">Show Receipt</button></td>\n");
                    } else
                        TransactionList.append("<td align=center>No Receipt</td>\n");

                    TransactionList.append("</tr>");
                    SNo++;
                }
                rset.close();
                stmt.close();
            } else {
                FromDate = request.getParameter("FromDate").trim();
                ToDate = request.getParameter("ToDate").trim();

                Query = " SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                        "a.InvoiceNo  " +
                        "FROM " + Database + ".InvoiceMaster a   " +
                        "LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN  " +
                        "WHERE a.Status = 0 AND b.Status = 0 GROUP BY a.PatientMRN";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next())
                    PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1).trim() + "," + rset.getString(3).trim() + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
                rset.close();
                stmt.close();

                Query = " Select a.PatientMRN , CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                        "a.InvoiceNo, a.TotalAmount, a.PaidAmount,  a.BalAmount, IFNULL(a.RefNo,'-'), IFNULL(a.Remarks,'-') , " +
                        "DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T') as PaymentDate, DATE_FORMAT(c.CreatedDate, '%m/%d/%Y %T') as InvoiceDate,  " +
                        "CASE " +
                        "WHEN PayMethod = 1 THEN 'Credit Card' " +
                        "WHEN PayMethod = 2 THEN 'Cash' " +
                        "WHEN PayMethod = 3 THEN 'BOLT Device' " +
                        "WHEN PayMethod = 4 THEN 'Ingenico' " +
                        "ELSE '' END, IFNULL(PayMethod,0),IFNULL(a.Id,'N/A'),IFNULL(c.OrderID,'N/A') " +
                        "FROM " + Database + ".PaymentReceiptInfo a " +
                        "LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN " +
                        "LEFT JOIN " + Database + ".InvoiceMaster c on a.InvoiceNo = c.InvoiceNo " +
                        "WHERE a.CreatedDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59'  AND c.Status = 0 AND b.status=0  " + filter +
                        " ORDER BY a.CreatedDate DESC ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    RetRef = "N/A";
                    ResponseText = "N/A";
                    AccountNo = "N/A";
                    if (rset.getInt(12) == 1) {
                        Query2 = "SELECT RetRef,ResponseText,AccountNo FROM " + Database + ".CardConnectResponses " +
                                "WHERE PatientMRN = " + rset.getInt(1) + " AND InvoiceNo = '" + rset.getString(3) + "' ";
                        stmt2 = conn.createStatement();
                        rset2 = stmt2.executeQuery(Query2);
                        if (rset2.next()) {
                            RetRef = rset2.getString(1);
                            ResponseText = rset2.getString(2);
                            AccountNo = rset2.getString(3);
                        }
                        rset2.close();
                        stmt2.close();

                    }
                    if (rset.getInt(12) == 3) {
                        Query2 = "SELECT JSON_Response FROM " + Database + ".JSON_Response " +
                                "WHERE PatientMRN = " + rset.getInt(1) + " AND InvoiceNo = '" + rset.getString(3) + "' ";
                        stmt2 = conn.createStatement();
                        rset2 = stmt2.executeQuery(Query2);
                        if (rset2.next()) {
                            if (rset2.getString(1) != null) {
                                JSONParser parser = new JSONParser();
                                Object obj = parser.parse("[" + rset2.getString(1) + "]");
                                JSONArray array = (JSONArray) obj;
                                JSONObject obj2 = (JSONObject) array.get(0);
                                RetRef = (String) obj2.get("retref");
                                ResponseText = (String) obj2.get("resptext");
                            }
                        }
                        rset2.close();
                        stmt2.close();

                    }

                    TransactionList.append("<tr id=\"Tran_" + SNo + "\">");
                    TransactionList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(14) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(4)) + "</td>\n");
                    TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(5)) + "</td>\n");
                    TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(6)) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    if (rset.getString(11).trim().equals("Cash")) {
                        if (ClientId == 27 || ClientId == 29) {
                            TransactionList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=CashReceipt&MRN=" + rset.getInt(1) + "&InvoiceNo=" + rset.getString(3) + ">" + rset.getString(11) + "</a></td>\n");
                        } else {
                            TransactionList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                        }
                    } else {
                        TransactionList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    }
                    TransactionList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                    TransactionList.append("<td align=left>" + RetRef + "</td>\n");
                    TransactionList.append("<td align=left>" + ResponseText + "</td>\n");
                    TransactionList.append("<td align=left>" + AccountNo + "</td>\n");
                    //TransactionList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info\" onclick=\"ShowReceipt(" + rset.getInt(13) + ")\">Show Receipt</button></td>\n");
                    if (rset.getInt(12) != 3) {
                        TransactionList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info\" onclick=\"ShowReceipt(" + rset.getInt(13) + "," + rset.getInt(1) + ")\">Show Receipt</button></td>\n");
                    } else
                        TransactionList.append("<td align=center>No Receipt</td>\n");
                    TransactionList.append("</tr>");
                    SNo++;
                }
                rset.close();
                stmt.close();
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientInvoiceList", String.valueOf(PatientInvoiceList));
            Parser.SetField("TransactionList", String.valueOf(TransactionList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/TransactionReport_ROVERLAB.html");
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Transaction Report ** (showReport)", servletContext, Ex, "TransactionReport", "showReport", conn);
            Services.DumException("TransactionReport", "showReport ", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "TransactionReport");
            Parser.SetField("ActionID", "TransactionReport_Input");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    void PatientTransaction(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, UtilityHelper helper, int ClientId) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query = "";
        String Query2 = "";
        int Found = 0;
        int SNo = 1;
        String PatientId = "";
        String FromDate = "";
        String ToDate = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer TransactionList = new StringBuffer();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");

        int MRN = Integer.parseInt(request.getParameter("MRN").trim());
        try {

            Query = " Select a.PatientMRN , CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), a.InvoiceNo, a.TotalAmount, " +
                    "a.PaidAmount,  a.BalAmount, IFNULL(a.RefNo,'-'), IFNULL(a.Remarks,'-') , DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T') as PaymentDate, " +
                    "DATE_FORMAT(c.CreatedDate, '%m/%d/%Y %T') as InvoiceDate,  " +
                    "CASE WHEN PayMethod = 1 THEN 'Credit Card' WHEN PayMethod = 2 THEN 'Cash' WHEN PayMethod = 3 THEN 'BOLT Device' WHEN PayMethod = 4 THEN 'Ingenico' ELSE '' END, " +
                    "IFNULL(PayMethod,0),IFNULL(c.InvoiceCreatedBy,0),IFNULL(c.OrderID,'N/A') " +
                    " FROM " + Database + ".PaymentReceiptInfo a " +
                    "LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN " +
                    "LEFT JOIN " + Database + ".InvoiceMaster c on a.InvoiceNo = c.InvoiceNo " +
                    " WHERE a.PatientMRN = '" + MRN + "' and b.status=0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                TransactionList.append("<tr id=\"Tran_" + SNo + "\">");
                TransactionList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(14) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(4)) + "</td>\n");
                TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(5)) + "</td>\n");
                TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(6)) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                if (rset.getString(11).trim().equals("Cash")) {
                    if (ClientId == 27 || ClientId == 29) {
                        TransactionList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=CashReceipt&MRN=" + rset.getInt(1) + "&InvoiceNo=" + rset.getString(3) + ">" + rset.getString(11) + "</a></td>\n");
                    } else {
                        TransactionList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    }
                } else {
                    TransactionList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                }
                TransactionList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(13) + "</td>\n");
                TransactionList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();


            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("TransactionList", String.valueOf(TransactionList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/PatientTransactions_ROVERLAB.html");
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Transaction Report ** (PatientTransaction)", servletContext, Ex, "TransactionReport", "PatientTransaction", conn);
            Services.DumException("TransactionReport", "Patient Transaction", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
    }

}
