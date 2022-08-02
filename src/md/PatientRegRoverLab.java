package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
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
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.*;

@SuppressWarnings("Duplicates")
public class PatientRegRoverLab extends HttpServlet {
/*    private Connection conn = null;
    private ResultSet rset = null;
    private String Query = "";
    private Statement stmt = null;
    private PreparedStatement pStmt = null;
    */

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

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ActionID = "";
        String UserId = "";
        String DatabaseName = "";
        String DirectoryName = "";
        HttpSession session = null;
        Connection conn = null;
        boolean validSession = false;
        int FacilityIndex = 0;
        int isLocationAdmin = 0;
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
        String locationArray = "";

        try {
            Parsehtm Parser;
            if (request.getParameter("ActionID") == null) {
                ActionID = "Nosession";
            } else {
                //ActionID = request.getParameter("ActionID").trim();
                session = request.getSession(false);
                validSession = helper.checkSession(request, context, session, out);
                if (!validSession) {
                    out.flush();
                    out.close();
                    return;
                }
                UserId = session.getAttribute("UserId").toString();
                DatabaseName = session.getAttribute("DatabaseName").toString();
                locationArray = session.getAttribute("LocationArray").toString();
                DirectoryName = session.getAttribute("DirectoryName").toString();
                FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
                isLocationAdmin = Integer.parseInt(session.getAttribute("isLocationAdmin").toString());

                if (UserId.equals("")) {
                    Parsehtm parsehtm = new Parsehtm(request);
                    parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            }

            ActionID = request.getParameter("ActionID");
//            System.out.println("locationArray" + locationArray);
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("Error", "Unable to connect. Our team is looking into it!");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "ShowReport":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View Patient Option", "Click on View Patient Option Patients List", FacilityIndex);
                    ShowReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, locationArray, isLocationAdmin);
                    break;
                case "ShowResult":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View Patient Option", "Click on View Patient Option Patients List", FacilityIndex);
                    ShowResult(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, locationArray);
                    break;
                case "PatientHistory":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View Patient Option", "Click on View Patient Option Patients List", FacilityIndex);
                    PatientHistory(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, locationArray);
                    break;
                case "ShowReport_FILTER":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View Patient Option", "Click on View Patient Option Patients List", FacilityIndex);
                    ShowReport_FILTER(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, locationArray, isLocationAdmin);
                    break;
                case "ShowReport_FILTERRESULT":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View Patient Option", "Click on View Patient Option Patients List", FacilityIndex);
                    ShowReport_FILTERRESULT(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;

                case "PatientUpdateInformation":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update Patient Information", "Open Patient Screen Update Info", FacilityIndex);
                    PatientUpdateInformation(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "UpdateTest":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update Patient Information", "Open Patient Screen Update Info", FacilityIndex);
                    UpdateTest(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "UpdateOrder":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update Patient Information", "Open Patient Screen Update Info", FacilityIndex);
                    UpdateOrder(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "sendResultReport":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update Patient Information", "Open Patient Screen Update Info", FacilityIndex);
                    sendResultReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "SendEmailWithReciept_ROVERLAB":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Send Reciept To Patient Email Address", "Send Reciept To Patient Email Address", FacilityIndex);
                    SendEmailWithReciept_ROVERLAB(request, context, conn, DatabaseName, out);
                    break;
                case "PrintLabel":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for Orange and Odessa", FacilityIndex);
                    this.PrintLabel(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex);
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
                    GetData(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "DeActivePatient": {
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "DeActivate Patients of Lab", "Deactivate the Selected Patients from the View Patient Option and Search Old Patient Option", FacilityIndex);
                    DeActivePatient(request, response, out, conn, context, UserId, DatabaseName, helper);
                    break;
                }
                default: {
                    helper.deleteUserSession(request, conn, session.getId());
                    session.invalidate();
                    Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
                }
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientRegRoverLab ** (handleRequest)", context, e, "PatientRegRoverLab", "handleRequest", conn);
            Services.DumException("PatientRegRoverLab", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
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
                helper.SendEmailWithAttachment("Error in PatientRegRoverLab ** (handleRequest -- SqlException)", context, e, "PatientRegRoverLab", "handleRequest", conn);
                Services.DumException("PatientRegRoverLab", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }

    void ShowReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, String locationArray, int isLocationAdmin) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuilder RoverLab = new StringBuilder();

        StringBuilder locationList = new StringBuilder();
        StringBuilder stageList = new StringBuilder();
        StringBuilder statusList = new StringBuilder();
        String filter = "";
        String Stage = request.getParameter("Stage") != null ? request.getParameter("Stage").trim() : null;
        String Status = request.getParameter("Status") != null ? request.getParameter("Status").trim() : null;

        try {

            ArrayList<String> al = new ArrayList<>();
            al.add(locationArray);
            String list = Arrays.toString(al.toArray()).replace("[", "").replace("]", "");

            String locCondition = " ";
            if (isLocationAdmin == 0) {
                if (locationArray.length() > 1) {
                    locCondition = " AND f.Id IN (" + list + ") ";
                }
            }


            if (Stage != null) {
                filter += " and b.StageIdx=" + Stage;
            }
            if (Status != null) {
                filter += " and  b.Status=" + Status;
            }

            Query = " SELECT CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " + //1
                    "DATE_FORMAT(a.DOB,'%m/%d/%Y'),  IFNULL(a.PhNumber,''), IFNULL(a.MRN,0), a.ID AS PatRegId, " +//5
                    "DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'),  " + //6
                    "CASE " +
                    "WHEN a.Insured = 'Yes' THEN 'Insured' " +
                    "WHEN a.Insured = 'NO' THEN 'Self Pay' " +
                    "ELSE 'Self Pay' " +
                    "END, b.OrderNum , b.OrderDate,c.Status," + //10
                    " e.TestName,f.Location,b.Id as OrderIdx " +//13
//                        "CASE " +
//                        " WHEN a.Status = 0 THEN c.Status " +
//                        " WHEN a.Status = 1 THEN c.Status" +
//                        " WHEN a.Status = 2 THEN c.Status" +
//                        " WHEN a.Status = 3 THEN c.Status " +
//                        " ELSE 'Pending' END " + //10
                    " FROM " + Database + ".PatientReg a" +
                    " INNER JOIN " + Database + ".TestOrder b ON a.ID = b.PatRegIdx " +
                    " INNER JOIN " + Database + ".ListofStages c ON b.StageIdx = c.Id " +
                    " INNER JOIN " + Database + ".Tests d ON b.Id = d.OrderId " +
                    " INNER JOIN " + Database + ".ListofTests e ON d.TestIdx = e.Id " +
                    " INNER JOIN " + Database + ".Locations f ON a.TestingLocation = f.Id " +
                    " WHERE a.Status = 0 " +
                    filter + locCondition +
                    " ORDER BY b.OrderDate DESC limit 500";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                RoverLab.append("<tr>\n");
                RoverLab.append("<td align=left>" + rset.getString(8) + "</td>\n");//OrderID
                RoverLab.append("<td align=left>" + rset.getString(4) + "</td>\n");//MRN
                RoverLab.append("<td align=left>" + rset.getString(1) + "</td>\n");//PatientName
                RoverLab.append("<td align=left>" + rset.getString(2) + "</td>\n");//DOB
                RoverLab.append("<td align=left>" + rset.getString(3) + "</td>\n");//Num
                RoverLab.append("<td align=left>" + rset.getString(9) + "</td>\n");//DOS
                RoverLab.append("<td align=left>" + rset.getString(12) + "</td>\n");//Location
                RoverLab.append("<td align=left>" + rset.getString(11) + "</td>\n");//TestName
                RoverLab.append("<td align=left><span class=\"badge badge-success\">" + rset.getString(10) + "</span></td>\n");//Status
                RoverLab.append("<td>");
                RoverLab.append("<button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-3\" onclick=\"UpdateInfoPatient(" + rset.getInt(5) + "," + rset.getInt(13) + ")\">View</button> &nbsp;&nbsp;&nbsp;");
                //RoverLab.append("<button type=\"button\" class=\"fa fa-file-pdf-o pdfIcon mb-2\" onclick=\"UpdateInfoPatient(" + rset.getInt(5) + ")\"></button>");
                RoverLab.append("<a class=\"btn fa fa-file-pdf-o pdfIcon mb-2\" href=/md/md.PatientRegRoverLab?ActionID=GETINPUTRoverLab&ID=" + rset.getInt(5) + " target=\"_blank\" rel=\"noopener noreferrer\"></a>");

                RoverLab.append("</td>\n");
                RoverLab.append("</tr>\n");
            }
            rset.close();
            stmt.close();

            Query = "Select Id, Location from " + Database + ".Locations WHERE Id IN (" + list + ") ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            locationList.append("<option value='-1' selected disabled>Select Location</option>");
            while (rset.next()) {
                locationList.append("<option value=" + rset.getString(1) + "  >" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Query = "Select Id, Status from " + Database + ".ListofStages";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            stageList.append("<option value='-1' selected>None </option>");
            while (rset.next()) {
                stageList.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();

            Query = "Select Id, Status from " + Database + ".ListofStatus";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            statusList.append("<option value='-1' selected>None </option>");
            while (rset.next()) {
                statusList.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("RoverLab", RoverLab.toString());
            Parser.SetField("UserId", UserId);
            Parser.SetField("LocationList", locationList.toString());
            Parser.SetField("StageList", stageList.toString());
            Parser.SetField("StatusList", statusList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowRegisteredPatient_ROVERLAB.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientRegRoverLab ** (ShowReport)", servletContext, e, "PatientRegRoverLab", "ShowReport", conn);
            Services.DumException("ShowReport", "PatientRegRoverLab ", request, e);
            /*Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
*//*            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void ShowResult(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, String locationArray) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String TODAY = "";
        int PatientCount = 0;
        StringBuilder RoverLab = new StringBuilder();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = formatter.format(date);
        StringBuilder locationList = new StringBuilder();
        StringBuilder stageList = new StringBuilder();
        StringBuilder TestList = new StringBuilder();
        String filter = "";
        String Stage = request.getParameter("Stage") != null ? request.getParameter("Stage").trim() : null;
        String Status = request.getParameter("Status") != null ? request.getParameter("Status").trim() : null;

        try {
            ArrayList<String> al = new ArrayList<>();
            al.add(locationArray);
            String list = Arrays.toString(al.toArray()).replace("[", "").replace("]", "");

            String locCondition = "";
            if (locationArray.length() > 1) {
                locCondition = " AND f.Id IN (" + list + ") ";
            }


            if (Stage != null) {
                filter += " and b.StageIdx=" + Stage;
                System.out.println("filter -> " + filter);
            }
            if (Status != null) {
                filter += " and  b.Status=" + Status;
                System.out.println("filter -> " + filter);

            }
            Query = "Select DATE_FORMAT(NOW(),'%Y-%m-%d')";


            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                TODAY = rset.getString(1);

            }
            rset.close();
            stmt.close();


            Query = "SELECT COUNT(*) FROM " + Database + ".PatientRegRoverLab";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                PatientCount = rset.getInt(1);
            rset.close();
            stmt.close();
            if (PatientCount > 0) {

                Query = " SELECT CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " + //1
                        "DATE_FORMAT(a.DOB,'%m/%d/%Y'),  IFNULL(a.PhNumber,''), IFNULL(a.MRN,0), a.ID AS PatRegId, " +//5
                        "DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'),  " + //6
                        "CASE " +
                        "WHEN a.Insured = 'Yes' THEN 'Insured' " +
                        "WHEN a.Insured = 'NO' THEN 'Self Pay' " +
                        "ELSE 'Self Pay' " +
                        "END, b.OrderNum , b.OrderDate,c.Status," +//10
                        "b.Id as OrderIdx, d.Id as TestIdx," + //12
                        " CASE " +
                        " WHEN d.TestStatus = 1 THEN 'BROKEN' " +
                        " WHEN d.TestStatus = 2 THEN 'NEGATIVE' " +
                        " WHEN d.TestStatus = 3 THEN 'POSITIVE' " +
                        " WHEN d.TestStatus = 4 THEN 'REJECTED' " +
                        " WHEN d.TestStatus = 5 THEN 'LOST' " +
                        " WHEN d.TestStatus = 6 THEN 'UNCONCLUSIVE' " +
                        " ELSE 'No Result' END, IFNULL(d.TestStatus,99), " +//14
                        " e.TestName,f.Location,e.Id, " + //17
                        " CASE WHEN b.email=0 THEN 'Email Not Sent' WHEN b.email=1 THEN 'Email Sent' Else '' END, IFNULL(DATE_FORMAT(b.emailtime,'%m/%d/%Y %H:%i:%s'),'00/00/0000'), " + //19
                        " CASE WHEN b.sms=0 THEN 'SMS Not Sent' WHEN b.sms=1 THEN 'Email Sent' Else '' END, IFNULL(DATE_FORMAT(b.smstime,'%m/%d/%Y %H:%i:%s'),'00/00/0000'), " + //21
                        " b.email,b.sms " +//23
                        " FROM roverlab.PatientReg a " +
                        " INNER JOIN roverlab.TestOrder b ON a.ID = b.PatRegIdx " +
                        " INNER JOIN roverlab.ListofStages c ON b.StageIdx = c.Id " +
                        " INNER JOIN roverlab.Tests d ON b.Id = d.OrderId " +
                        " INNER JOIN roverlab.ListofTests e ON d.TestIdx = e.Id " +
                        " INNER JOIN roverlab.Locations f ON a.TestingLocation = f.Id " +
                        " WHERE a.Status = 0 AND d.TestStatus IN (1,2,3,4,5,6) And " +
                        " DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' and " +
                        " DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59'  " +
                        filter + locCondition +
                        " ORDER BY b.OrderDate DESC limit 500";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    RoverLab.append("<tr>\n");
                    RoverLab.append("<td align=left>" + rset.getString(8) + "</td>\n");//OrderID
                    RoverLab.append("<td align=left>" + rset.getString(4) + "</td>\n");//MRN
                    RoverLab.append("<td align=left>" + rset.getString(1) + "</td>\n");//PatientName
//                    RoverLab.append("<td align=left>" + rset.getString(2) + "</td>\n");//DOB
//                    RoverLab.append("<td align=left>" + rset.getString(3) + "</td>\n");//Num
                    RoverLab.append("<td align=left>" + rset.getString(9) + "</td>\n");//DOS
                    RoverLab.append("<td align=left>" + rset.getString(16) + "</td>\n");//Location
                    RoverLab.append("<td align=left>" + rset.getString(15) + "</td>\n");//TestName
                    if (rset.getInt(14) == 1)
                        RoverLab.append("<td align=left><span class=\"badge badge-warning\">" + rset.getString(13) + "</span></td>\n");//Result
                    else if (rset.getInt(14) == 2)
                        RoverLab.append("<td align=left><span class=\"badge badge-success\">" + rset.getString(13) + "</span></td>\n");//Result
                    else if (rset.getInt(14) == 3)
                        RoverLab.append("<td align=left><span class=\"badge badge-danger\">" + rset.getString(13) + "</span></td>\n");//Result
                    else if (rset.getInt(14) == 4)
                        RoverLab.append("<td align=left><span class=\"badge badge-info\">" + rset.getString(13) + "</span></td>\n");//Result
                    else if (rset.getInt(14) == 5)
                        RoverLab.append("<td align=left><span class=\"badge badge-primary\">" + rset.getString(13) + "</span></td>\n");//Result
                    else if (rset.getInt(14) == 6)
                        RoverLab.append("<td align=left><span class=\"badge badge-dark\">" + rset.getString(13) + "</span></td>\n");//Result
                    else
                        RoverLab.append("<td align=left><span class=\"badge badge-light\">" + rset.getString(13) + "</span></td>\n");//Result

                    if (rset.getInt(22) == 1) {
                        RoverLab.append("<td align=left><span class=\"badge badge-success\">" + rset.getString(18) + "</span></td>\n");//EmailSent
                    } else if (rset.getInt(22) == 0) {
                        RoverLab.append("<td align=left><span class=\"badge badge-warning\">" + rset.getString(18) + "</span></td>\n");//EmailSent
                    }
                    RoverLab.append("<td align=left>" + rset.getString(19) + "</td>\n");//EmailTime
                    if (rset.getInt(23) == 1) {
                        RoverLab.append("<td align=left><span class=\"badge badge-success\">" + rset.getString(20) + "</span></td>\n");//SMS
                    } else if (rset.getInt(23) == 0) {
                        RoverLab.append("<td align=left><span class=\"badge badge-warning\">" + rset.getString(20) + "</span></td>\n");//SMS
                    }
                    RoverLab.append("<td align=left>" + rset.getString(21) + "</td>\n");//SMSTime


                    RoverLab.append("<td>");
                    RoverLab.append("<button type=\"button\" class=\"waves-effect waves-circle btn btn-circle btn-info btn-sm mb-3\" onclick=\"UpdateInfoPatient(" + rset.getInt(5) + ", " + rset.getInt(11) + ")\">View</button> &nbsp;&nbsp;&nbsp;");
                    RoverLab.append("<a class=\"btn fa fa-file-pdf-o pdfIcon mb-2 tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Bundle\" href=/md/md.PatientRegRoverLab?ActionID=GETINPUTRoverLab&ID=" + rset.getInt(5) + " target=\"_blank\" rel=\"noopener noreferrer\"></a>");
                    if (rset.getInt(17) == 4) {
                        RoverLab.append("<button type=\"button\" class=\"waves-effect waves-circle btn btn-circle btn-info btn-sm mb-3\" onclick=\"sendResult(" + rset.getInt(11) + "," + rset.getInt(12) + ")\">Send</button> &nbsp;&nbsp;&nbsp;");
                        RoverLab.append("<a class=\"btn glyphicon glyphicon-list-alt tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Result\" href=/md/md.PatientRegRoverLab?ActionID=sendResultReport&O_ID=" + rset.getInt(11) + "&T_ID=" + rset.getInt(12) + "&PatRegIdx=" + rset.getInt(5) + "&TestType=4 target=\"_blank\" rel=\"noopener noreferrer\"></a>");
                    } else {
                        RoverLab.append("<button type=\"button\" class=\"waves-effect waves-circle btn btn-circle btn-info btn-sm mb-3\" onclick=\"sendResult(" + rset.getInt(11) + "," + rset.getInt(12) + ")\">Send</button> &nbsp;&nbsp;&nbsp;");
                        //RoverLab.append("<button type=\"button\" class=\"fa fa-file-pdf-o pdfIcon mb-2\" onclick=\"UpdateInfoPatient(" + rset.getInt(5) + ")\"></button>");
                        RoverLab.append("<a class=\"btn glyphicon glyphicon-list-alt tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Result\" href=/md/md.RoverLabResult?ActionID=getResultPdf&O_ID=" + rset.getInt(11) + "&T_ID=" + rset.getInt(12) + "&PatRegIdx=" + rset.getInt(5) + " target=\"_blank\" rel=\"noopener noreferrer\"></a>");
                    }
                    RoverLab.append("</td>\n");

                    RoverLab.append("</tr>\n");
                }
                rset.close();
                stmt.close();
            }


            Query = "Select Id, Location from roverlab.Locations WHERE Id IN (" + list + ") ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            locationList.append("<option value='' selected>Select Location</option>");
            while (rset.next()) {
                locationList.append("<option value=" + rset.getString(1) + "  >" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Query = "SELECT Id,TestName FROM " + Database + ".ListofTests " +
                    "WHERE Status = 0 ORDER BY TestName ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            TestList.append("<option value='' selected>None</option>");
            while (rset.next()) {

                TestList.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("RoverLab", RoverLab.toString());
            Parser.SetField("UserId", UserId);
            Parser.SetField("LocationList", locationList.toString());
            Parser.SetField("StageList", stageList.toString());
            Parser.SetField("TestList", TestList.toString());
            Parser.SetField("searchdatefrom", String.valueOf(today));
            Parser.SetField("searchdateto", String.valueOf(today));
            Parser.SetField("today", String.valueOf(today));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ResultRegisteredPatient_ROVERLAB.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientRegRoverLab ** (ShowReport)", servletContext, e, "PatientRegRoverLab", "ShowReport", conn);
            Services.DumException("ShowReport", "PatientRegRoverLab ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    private void PrintLabel(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String Database, ServletContext servletContext, String UserId, int ClientId) {
        final String ID = request.getParameter("ID");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String DateTime = "";
        String ClientName = "";
        String DoctorsName = "";
        String DOBForAge = "";
        String DoctorsId = null;
        String ClientIndex = null;
        String FirstName = null;
        String LastName = null;
        String DOB = null;
        String Age = null;
        String Gender = null;
        String MRN = null;
        String CreatedDate = null;
        String DirectoryName = "";
        String VisitNumber = "";
        try {
            if (ClientId == 8) {
                DirectoryName = "Orange";
            } else if (ClientId == 9) {
                DirectoryName = "Victoria";
            } else if (ClientId == 10) {
                DirectoryName = "Odessa";
            } else if (ClientId == 12) {
                DirectoryName = "SAustin";
            } else if (ClientId == 15) {
                DirectoryName = "Sublime";
            }
            Query = "SELECT ID, ClientIndex, FirstName, LastName, MiddleInitial, " +
                    "IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'),''), IFNULL(Age,''), Gender, MRN, " +
                    "IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y'),DATE_FORMAT(CreatedDate,'%m/%d/%Y'))," +
                    "date_format(now(),'%Y%m%d%H%i%s'),IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'),'') " +
                    "FROM   " + Database + ".PatientRegRoverLab where ID= '" + ID + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientIndex = rset.getString(2);
                FirstName = rset.getString(3);
                LastName = rset.getString(4);
                final String MiddleInitial = rset.getString(5);
                DOB = rset.getString(6);
                Age = rset.getString(7);
                Gender = rset.getString(8);
                MRN = rset.getString(9);
                CreatedDate = rset.getString(10);
                DateTime = rset.getString(11);
                DOBForAge = rset.getString(12);
            }
            rset.close();
            stmt.close();

            if (!DOB.equals("")) {
                Age = String.valueOf(getAge(LocalDate.parse(DOBForAge)));
            }

            Query = "Select name from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientName = rset.getString(1);
            }
            rset.close();
            stmt.close();
//            if (!DoctorsId.equals("-")) {
//                Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorsId;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                while (rset.next()) {
//                    DoctorsName = rset.getString(1);
//                }
//                rset.close();
//                stmt.close();
//            } else {
//                DoctorsName = "";
//            }


//            Query = "Select MAX(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + ID;
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                VisitNumber = rset.getString(1).trim();
//            }
//            rset.close();
//            stmt.close();
//
//            VisitNumber = "VN-" + MRN + "-" + VisitNumber;

        } catch (Exception ex3) {
            //System.out.println("Error in GETINPUT, printlable4 "+ex3.getMessage()+Query);
        }
        try {
            String inputFilePath = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/label01.pdf";
            String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + DateTime + ".pdf";
            OutputStream fos = new FileOutputStream(new File(outputFilePath));
            PdfReader pdfReader = new PdfReader("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/label01.pdf");
            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            GenerateBarCode barCode = new GenerateBarCode();
//            String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
//            String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
//            Image image = Image.getInstance(BarCodeFilePath);
//            image.scaleAbsolute(150, 30);
//            image.setAbsolutePosition(420.0f, 760.0f);
            for (int i = 1; i <= pdfReader.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    //pdfContentByte.addImage(image);
                    int x = 0;
                    for (int r = 1; r <= 3; ++r) {
                        if (r == 1) {
                            x = 25;
                        } else if (r == 2) {
                            x = 220;
                        } else if (r == 3) {
                            x = 420;
                        }
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 740.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 730.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 720.0f);
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 710.0f);
                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 700.0f);
//                        pdfContentByte.showText("ACT#:" + VisitNumber);
//                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 690.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();


                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 665.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 655.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 645.0f);
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 635.0f);
                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 625);
//                        pdfContentByte.showText("ACT#: " + VisitNumber);
//                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 615.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 595.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 585.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 575.0f);
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 565.0f);
                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 555.0f);
//                        pdfContentByte.showText("ACT#: " + VisitNumber);
//                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 545.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 525.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 515.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 505.0f);
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 495.0f);
                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 485.0f);
//                        pdfContentByte.showText("ACT#: " + VisitNumber);
//                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 475);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 450.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 440.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 430.0f);
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 420.0f);
                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                        pdfContentByte.endText();
                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setTextMatrix((float) x, 410.0f);
//                        pdfContentByte.showText("ACT#: " + VisitNumber);
//                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setTextMatrix((float) x, 400.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 380.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 370.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 360.0f);
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 350.0f);
                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 340.0f);
//                        pdfContentByte.showText("ACT#: " + VisitNumber);
//                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 330.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 305.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 295.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 285.0f);
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 275.0f);
                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 265.0f);
//                        pdfContentByte.showText("ACT#: " + VisitNumber);
//                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 255.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 235.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 225.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 215.0f);
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 205.0f);
                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 195.0f);
//                        pdfContentByte.showText("ACT#: " + VisitNumber);
//                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 185.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 160.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 150.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 140.0f);
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 130.0f);
                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 120.0f);
                        pdfContentByte.showText("ACT#: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 110.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 90.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 80.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 70.0f);
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 60.0f);
                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 50.0f);
//                        pdfContentByte.showText("ACT#: " + VisitNumber);
//                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 40.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();
                    }
                }
            }
            pdfStamper.close();

            File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + MRN + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            FileInputStream fileInputStream = new FileInputStream(pdfFile);
            OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }

        } catch (Exception ex2) {
            try {
                Services.DumException("PatientRegRoverLab", "Printlabel", request, ex2);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientUpdateInfo");
                Parser.SetField("ActionID", "GetInput&ID=" + ID);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (Exception e) {
            }
//            String str = "";
//            out.println(ex2.getMessage());
//            for (int j = 0; j < ex2.getStackTrace().length; ++j) {
//                str = str + ex2.getStackTrace()[j] + "<br>";
//            }
//            out.println(str);
        }
    }

    void ShowReport_FILTER(HttpServletRequest request, PrintWriter out, Connection conn,
                           ServletContext servletContext, String UserId, String Database,
                           int ClientId, UtilityHelper helper, String locationArray,
                           int isLocationAdmin) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String BundleFnName = "";
        String LabelFnName = "";
        int PatientCount = 0;
        StringBuilder RoverLab = new StringBuilder();
        String filter = "";
        int filterCount = 0;
        int SNo = 1;
        StringBuilder locationList = new StringBuilder();
        StringBuilder stageList = new StringBuilder();
        StringBuilder statusList = new StringBuilder();

        String Stage = !request.getParameter("Stage").equals("-1") ? request.getParameter("Stage").trim() : null;
        String Status = !request.getParameter("Status").equals("-1") ? request.getParameter("Status").trim() : null;
        String Location = request.getParameter("Location") != null ? request.getParameter("Location").trim() : null;

        ArrayList<String> al = new ArrayList<>();
        al.add(locationArray);
        String list = Arrays.toString(al.toArray()).replace("[", "").replace("]", "");

        String locCondition = " ";
        if (isLocationAdmin == 0) {
            if (locationArray.length() > 1) {
                locCondition = " AND f.Id IN (" + list + ") ";
            }
        }

        if (Stage != null || Status != null || Location != null) {
            filter += " WHERE ";
            if (Stage != null) {
                filter += " b.StageIdx = '" + Stage + "' ";
                filterCount++;
            }
            if (Status != null) {
                if (filterCount > 0) {
                    filter += " AND b.Status = '" + Status + "' ";
                } else {
                    filter += " b.Status = '" + Status + "' ";
                }
                filterCount++;
            }
            if (Location != null) {
                if (filterCount > 0) {
                    filter += " AND a.TestingLocation = '" + Location + "' ";
                    filterCount++;
                } else {
                    filter += " a.TestingLocation = '" + Location + "' ";
                }
            }
        }


        try {
/*            Query = "SELECT COUNT(*) FROM " + Database + ".PatientRegRoverLab";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                PatientCount = rset.getInt(1);
            rset.close();
            stmt.close();
            if (PatientCount > 0) {*/

/*                Query = " SELECT CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " + //1
                        "DATE_FORMAT(a.DOB,'%m/%d/%Y'),  IFNULL(a.PhNumber,''), IFNULL(a.MRN,0), a.ID AS PatRegId, " +//5
                        "DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'),  " + //6
                        "CASE " +
                        "WHEN a.Insured = 'Yes' THEN 'Insured' " +
                        "WHEN a.Insured = 'NO' THEN 'Self Pay' " +
                        "ELSE 'Self Pay' " +
                        "END, b.OrderNum , b.OrderDate,c.Status" + //9
//                        "CASE " +
//                        " WHEN a.Status = 0 THEN c.Status " +
//                        " WHEN a.Status = 1 THEN c.Status" +
//                        " WHEN a.Status = 2 THEN c.Status" +
//                        " WHEN a.Status = 3 THEN c.Status " +
//                        " ELSE 'Pending' END " + //10
                        " FROM roverlab.PatientReg a" +
                        " INNER JOIN roverlab.TestOrder b ON a.ID = b.PatRegIdx " +
                        " INNER JOIN roverlab.ListofStages c ON b.StageIdx = c.Id " +
//                        " WHERE a.Status = 0 " +
                        filter +
                        " ORDER BY a.CreatedDate DESC limit 500";*/
            Query = " SELECT CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " + //1
                    "DATE_FORMAT(a.DOB,'%m/%d/%Y'),  IFNULL(a.PhNumber,''), IFNULL(a.MRN,0), a.ID AS PatRegId, " +//5
                    "DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'),  " + //6
                    "CASE " +
                    "WHEN a.Insured = 'Yes' THEN 'Insured' " +
                    "WHEN a.Insured = 'NO' THEN 'Self Pay' " +
                    "ELSE 'Self Pay' " +
                    "END, b.OrderNum , b.OrderDate,c.Status," + //10
                    " e.TestName,f.Location " +//12
//                        "CASE " +
//                        " WHEN a.Status = 0 THEN c.Status " +
//                        " WHEN a.Status = 1 THEN c.Status" +
//                        " WHEN a.Status = 2 THEN c.Status" +
//                        " WHEN a.Status = 3 THEN c.Status " +
//                        " ELSE 'Pending' END " + //10
                    " FROM roverlab.PatientReg a" +
                    " INNER JOIN roverlab.TestOrder b ON a.ID = b.PatRegIdx " +
                    " INNER JOIN roverlab.ListofStages c ON b.StageIdx = c.Id " +
                    " INNER JOIN roverlab.Tests d ON b.Id = d.OrderId " +
                    " INNER JOIN roverlab.ListofTests e ON d.TestIdx = e.Id " +
                    " INNER JOIN roverlab.Locations f ON a.TestingLocation = f.Id " +
                    filter + locCondition +
                    " ORDER BY a.CreatedDate DESC limit 500";
            System.out.println("Query - >>> " + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
/*                    RoverLab.append("<tr>\n");
                    RoverLab.append("<td align=left>" + rset.getString(8) + "</td>\n");//OrderID
                    RoverLab.append("<td align=left>" + rset.getString(4) + "</td>\n");//MRN
                    RoverLab.append("<td align=left>" + rset.getString(1) + "</td>\n");//PatientName
                    RoverLab.append("<td align=left>" + rset.getString(2) + "</td>\n");//DOB
                    RoverLab.append("<td align=left>" + rset.getString(3) + "</td>\n");//Num
                    RoverLab.append("<td align=left>" + rset.getString(9) + "</td>\n");//DOS
//                    RoverLab.append("<td align=left>" + rset.getString(7) + "</td>\n");//TestName
                    RoverLab.append("<td align=left>" + rset.getString(10) + "</td>\n");//Status
                    RoverLab.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-5\" onclick=\"UpdateInfoPatient(" + rset.getInt(5) + ")\">View</button></td>\n");
                    RoverLab.append("</tr>\n");*/
                RoverLab.append("<tr>\n");
                RoverLab.append("<td align=left>" + rset.getString(8) + "</td>\n");//OrderID
                RoverLab.append("<td align=left>" + rset.getString(4) + "</td>\n");//MRN
                RoverLab.append("<td align=left>" + rset.getString(1) + "</td>\n");//PatientName
                RoverLab.append("<td align=left>" + rset.getString(2) + "</td>\n");//DOB
                RoverLab.append("<td align=left>" + rset.getString(3) + "</td>\n");//Num
                RoverLab.append("<td align=left>" + rset.getString(9) + "</td>\n");//DOS
                RoverLab.append("<td align=left>" + rset.getString(12) + "</td>\n");//Location
                RoverLab.append("<td align=left>" + rset.getString(11) + "</td>\n");//TestName
                RoverLab.append("<td align=left><span class=\"badge badge-success\">" + rset.getString(10) + "</span></td>\n");//Status
                RoverLab.append("<td>");
                RoverLab.append("<button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-3\" onclick=\"UpdateInfoPatient(" + rset.getInt(5) + ")\">View</button> &nbsp;&nbsp;&nbsp;");
                //RoverLab.append("<button type=\"button\" class=\"fa fa-file-pdf-o pdfIcon mb-2\" onclick=\"UpdateInfoPatient(" + rset.getInt(5) + ")\"></button>");
                RoverLab.append("<a class=\"btn fa fa-file-pdf-o pdfIcon mb-2\" href=/md/md.PatientRegRoverLab?ActionID=GETINPUTRoverLab&ID=" + rset.getInt(5) + " target=\"_blank\" rel=\"noopener noreferrer\"></a>");

                RoverLab.append("</td>\n");
                RoverLab.append("</tr>\n");
            }
            rset.close();
            stmt.close();
            // }

            Query = "Select Id, Location from roverlab.Locations WHERE Id IN (" + list + ") ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            locationList.append("<option value='-1' selected disabled>Select Location</option>");
            while (rset.next()) {
                locationList.append("<option value=" + rset.getString(1) + "  >" + rset.getString(2) + "</option>");
                locationList.append("<option value=" + rset.getString(1) + "  >" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Query = "Select Id, Status from roverlab.ListofStages";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            stageList.append("<option value='-1' selected>None </option>");
            while (rset.next()) {
                stageList.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();

            Query = "Select Id, Status from roverlab.ListofStatus";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            statusList.append("<option value='-1' selected>None </option>");
            while (rset.next()) {
                statusList.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();

//            out.println(RoverLab.toString());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("RoverLab", RoverLab.toString());
            Parser.SetField("UserId", UserId);
            Parser.SetField("LocationList", locationList.toString());
            Parser.SetField("StageList", stageList.toString());
            Parser.SetField("StatusList", statusList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowRegisteredPatient_ROVERLAB.html");


        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientRegRoverLab ** (ShowReport)", servletContext, e, "PatientRegRoverLab", "ShowReport", conn);
            Services.DumException("ShowReport", "PatientRegRoverLab ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
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

    private Dictionary doUpload(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out) throws Exception {
        try {
            String boundary = request.getHeader("Content-Type");
            int pos = boundary.indexOf(61);
            boundary = boundary.substring(pos + 1);
            boundary = "--" + boundary;
            final byte[] bytes = new byte[512];
            int state = 0;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            String name = null;
            String value = null;
            String filename = null;
            String contentType = null;
            final Dictionary fields = new Hashtable();
            final ServletInputStream in = request.getInputStream();
            for (int i = in.readLine(bytes, 0, 512); -1 != i; i = in.readLine(bytes, 0, 512)) {
                final String st = new String(bytes, 0, i);
                if (st.startsWith(boundary)) {
                    state = 0;
                    if (name != null) {
                        if (value != null) {
                            fields.put(name, value.substring(0, value.length() - 2));
                        } else if (buffer.size() > 2) {
                            fields.put(filename, buffer);
                        }
                        name = null;
                        value = null;
                        filename = null;
                        contentType = null;
                        buffer = new ByteArrayOutputStream();
                    }
                } else if (st.startsWith("Content-Disposition: form-data") && state == 0) {
                    final StringTokenizer tokenizer = new StringTokenizer(st, ";=\"");
                    while (tokenizer.hasMoreTokens()) {
                        final String token = tokenizer.nextToken();
                        if (token.startsWith(" name")) {
                            name = tokenizer.nextToken();
                            state = 2;
                        } else {
                            if (token.startsWith(" filename")) {
                                filename = tokenizer.nextToken();
                                final StringTokenizer ftokenizer = new StringTokenizer(filename, "\\/:");
                                filename = ftokenizer.nextToken();
                                while (ftokenizer.hasMoreTokens()) {
                                    filename = ftokenizer.nextToken();
                                }
                                state = 1;
                                break;
                            }
                            continue;
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

        final int ID = IDpatient;//Integer.parseInt(request.getParameter("ID").trim());
//        out.println("ID -> "+ID);
        try {
            Query = "select date_format(now(),'%Y%m%d%H%i%s'), DATE_FORMAT(now(), '%m/%d/%Y'), DATE_FORMAT(now(), '%T')";
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
                Query = " Select FirstName ,LastName ,MiddleInitial ,Gender ,PhNumber ,Email ,Address ,City ,State ,County ,Ethnicity ,Race ,Test ,AtTestSite ,TestingLocation ,Insured ,RespParty ,CarrierName ,GrpNumber ,MemID ,ExtendedMRN ,Status ,CreatedDate ,EditBy ,Edittime ,DOB ,ZipCode" +
                        "  From " + Database + ".PatientReg Where ID = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
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

            inputFilePath += "/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/COVID_REG.pdf";

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


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100, 320); // set x and y co-ordinates
                    pdfContentByte.showText("");//"Patient Signature"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420, 320); // set x and y co-ordinates
                    pdfContentByte.showText("Date"); // add the text
                    pdfContentByte.endText();


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

    void ShowReport_FILTERRESULT(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String BundleFnName = "";
        String LabelFnName = "";
        int PatientCount = 0;
        String Location = "";
        String TestType = "";
        String EmailStatus = "";
        String LocationFilter = "";
        String EmailFilter = "";
        String TestFilter = "";
        StringBuilder RoverLab = new StringBuilder();
        String filter = "";
        Integer filterCount = 0;
        int SNo = 1;
        StringBuilder locationList = new StringBuilder();
        StringBuilder stageList = new StringBuilder();
        StringBuilder TestList = new StringBuilder();
        String DateRange = request.getParameter("Month").trim();
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        System.out.println("in the function");
//        String TestType = !request.getParameter("TestType").equals("-1") ? request.getParameter("TestType").trim() : null;
//        String EmailStatus = !request.getParameter("EmailStatus").equals("-1") ? request.getParameter("EmailStatus").trim() : null;
//        String Location = request.getParameter("Location") != null ? request.getParameter("Location").trim() : null;
//        if (Stage != null || Status != null || Location != null) {
//            filter += " WHERE a.Status = 0 AND d.TestStatus IN (1,2,3,4,5,6)  ";
//            if (Stage != null) {
//                filter += " AND b.StageIdx = '" + Stage + "' ";
//                filterCount++;
//            }
//            if (Status != null) {
//                if (filterCount > 0) {
//                    filter += " AND b.Status = '" + Status + "' ";
//                } else {
//                    filter += " b.Status = '" + Status + "' ";
//                }
//                filterCount++;
//            }
//            if (Location != null) {
//                if (filterCount > 0) {
//                    filter += " AND a.TestingLocation = '" + Location + "' ";
//                    filterCount++;
//                } else {
//                    filter += " a.TestingLocation = '" + Location + "' ";
//                }
//            }
//        }

        if (request.getParameter("Location") == null) {
            Location = "";
        } else {
            Location = request.getParameter("Location");
        }

        if (request.getParameter("TestType") == null) {
            TestType = "";
        } else {
            TestType = request.getParameter("TestType");
        }

        if (request.getParameter("EmailStatus") == null) {
            EmailStatus = "";
        } else {
            EmailStatus = request.getParameter("EmailStatus");
        }


        if (Location == "" || Location == null) {
            LocationFilter = "";
        } else {

            LocationFilter = " And a.TestingLocation =" + Location + "";
        }


        if (TestType == "" || TestType == null) {
            TestFilter = "";
        } else {

            TestFilter = " AND d.TestIdx =" + TestType + "";
        }


        if (EmailStatus == "" || EmailStatus == null) {
            EmailFilter = "";
        } else {

            EmailFilter = " AND b.email =" + EmailStatus + "";
        }


        try {
            Query = "SELECT COUNT(*) FROM " + Database + ".PatientRegRoverLab";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                PatientCount = rset.getInt(1);
            rset.close();
            stmt.close();
            if (PatientCount > 0) {
                Query = "Select Bundle_FnName, Label_FnName from oe.clients where Id = " + ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    BundleFnName = rset.getString(1);
                    LabelFnName = rset.getString(2);
                }
                rset.close();
                stmt.close();

/*                Query = " SELECT CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " + //1
                        "DATE_FORMAT(a.DOB,'%m/%d/%Y'),  IFNULL(a.PhNumber,''), IFNULL(a.MRN,0), a.ID AS PatRegId, " +//5
                        "DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'),  " + //6
                        "CASE " +
                        "WHEN a.Insured = 'Yes' THEN 'Insured' " +
                        "WHEN a.Insured = 'NO' THEN 'Self Pay' " +
                        "ELSE 'Self Pay' " +
                        "END, b.OrderNum , b.OrderDate,c.Status" + //9
//                        "CASE " +
//                        " WHEN a.Status = 0 THEN c.Status " +
//                        " WHEN a.Status = 1 THEN c.Status" +
//                        " WHEN a.Status = 2 THEN c.Status" +
//                        " WHEN a.Status = 3 THEN c.Status " +
//                        " ELSE 'Pending' END " + //10
                        " FROM roverlab.PatientReg a" +
                        " INNER JOIN roverlab.TestOrder b ON a.ID = b.PatRegIdx " +
                        " INNER JOIN roverlab.ListofStages c ON b.StageIdx = c.Id " +
//                        " WHERE a.Status = 0 " +
                        filter +
                        " ORDER BY a.CreatedDate DESC limit 500";*/
                Query = " SELECT CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " + //1
                        "DATE_FORMAT(a.DOB,'%m/%d/%Y'),  IFNULL(a.PhNumber,''), IFNULL(a.MRN,0), a.ID AS PatRegId, " +//5
                        "DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'),  " + //6
                        "CASE " +
                        "WHEN a.Insured = 'Yes' THEN 'Insured' " +
                        "WHEN a.Insured = 'NO' THEN 'Self Pay' " +
                        "ELSE 'Self Pay' " +
                        "END, b.OrderNum , b.OrderDate,c.Status," +//10
                        "b.Id as OrderIdx, d.Id as TestIdx," + //12
                        " CASE " +
                        " WHEN d.TestStatus = 1 THEN 'BROKEN' " +
                        " WHEN d.TestStatus = 2 THEN 'NEGATIVE' " +
                        " WHEN d.TestStatus = 3 THEN 'POSITIVE' " +
                        " WHEN d.TestStatus = 4 THEN 'REJECTED' " +
                        " WHEN d.TestStatus = 5 THEN 'LOST' " +
                        " WHEN d.TestStatus = 6 THEN 'UNCONCLUSIVE' " +
                        " ELSE 'No Result' END, IFNULL(d.TestStatus,99), " +//14
                        " e.TestName,f.Location,e.Id, " + //17
                        " CASE WHEN b.email=0 THEN 'Email Not Sent' WHEN b.email=1 THEN 'Email Sent' Else '' END, IFNULL(DATE_FORMAT(b.emailtime,'%m/%d/%Y %H:%i:%s'),'00/00/0000'), " + //19
                        " CASE WHEN b.sms=0 THEN 'SMS Not Sent' WHEN b.sms=1 THEN 'Email Sent' Else '' END, IFNULL(DATE_FORMAT(b.smstime,'%m/%d/%Y %H:%i:%s'),'00/00/0000'), " + //21
                        " b.email,b.sms " +//23
                        " FROM roverlab.PatientReg a " +
                        " INNER JOIN roverlab.TestOrder b ON a.ID = b.PatRegIdx " +
                        " INNER JOIN roverlab.ListofStages c ON b.StageIdx = c.Id " +
                        " INNER JOIN roverlab.Tests d ON b.Id = d.OrderId " +
                        " INNER JOIN roverlab.ListofTests e ON d.TestIdx = e.Id " +
                        " INNER JOIN roverlab.Locations f ON a.TestingLocation = f.Id " +
                        " where a.Status=0 AND b.OrderDate BETWEEN '" + FromDate + " 00:00:00' AND '" + ToDate + " 23:59:59' " +
                        LocationFilter + TestFilter + EmailFilter +
                        " ORDER BY b.OrderDate DESC limit 500";

                System.out.println("Query - >>> " + Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    RoverLab.append("<tr>\n");
                    RoverLab.append("<td align=left>" + rset.getString(8) + "</td>\n");//OrderID
                    RoverLab.append("<td align=left>" + rset.getString(4) + "</td>\n");//MRN
                    RoverLab.append("<td align=left>" + rset.getString(1) + "</td>\n");//PatientName
//                    RoverLab.append("<td align=left>" + rset.getString(2) + "</td>\n");//DOB
//                    RoverLab.append("<td align=left>" + rset.getString(3) + "</td>\n");//Num
                    RoverLab.append("<td align=left>" + rset.getString(9) + "</td>\n");//DOS
                    RoverLab.append("<td align=left>" + rset.getString(16) + "</td>\n");//Location
                    RoverLab.append("<td align=left>" + rset.getString(15) + "</td>\n");//TestName
                    if (rset.getInt(14) == 1)
                        RoverLab.append("<td align=left><span class=\"badge badge-warning\">" + rset.getString(13) + "</span></td>\n");//Result
                    else if (rset.getInt(14) == 2)
                        RoverLab.append("<td align=left><span class=\"badge badge-success\">" + rset.getString(13) + "</span></td>\n");//Result
                    else if (rset.getInt(14) == 3)
                        RoverLab.append("<td align=left><span class=\"badge badge-danger\">" + rset.getString(13) + "</span></td>\n");//Result
                    else if (rset.getInt(14) == 4)
                        RoverLab.append("<td align=left><span class=\"badge badge-info\">" + rset.getString(13) + "</span></td>\n");//Result
                    else if (rset.getInt(14) == 5)
                        RoverLab.append("<td align=left><span class=\"badge badge-primary\">" + rset.getString(13) + "</span></td>\n");//Result
                    else if (rset.getInt(14) == 6)
                        RoverLab.append("<td align=left><span class=\"badge badge-dark\">" + rset.getString(13) + "</span></td>\n");//Result
                    else
                        RoverLab.append("<td align=left><span class=\"badge badge-light\">" + rset.getString(13) + "</span></td>\n");//Result

                    if (rset.getInt(22) == 1) {
                        RoverLab.append("<td align=left><span class=\"badge badge-success\">" + rset.getString(18) + "</span></td>\n");//EmailSent
                    } else if (rset.getInt(22) == 0) {
                        RoverLab.append("<td align=left><span class=\"badge badge-warning\">" + rset.getString(18) + "</span></td>\n");//EmailSent
                    }
                    RoverLab.append("<td align=left>" + rset.getString(19) + "</td>\n");//EmailTime
                    if (rset.getInt(23) == 1) {
                        RoverLab.append("<td align=left><span class=\"badge badge-success\">" + rset.getString(20) + "</span></td>\n");//SMS
                    } else if (rset.getInt(23) == 0) {
                        RoverLab.append("<td align=left><span class=\"badge badge-warning\">" + rset.getString(20) + "</span></td>\n");//SMS
                    }
                    RoverLab.append("<td align=left>" + rset.getString(21) + "</td>\n");//SMSTime


                    RoverLab.append("<td>");
                    RoverLab.append("<button type=\"button\" class=\"waves-effect waves-circle btn btn-circle btn-info btn-sm mb-3\" onclick=\"UpdateInfoPatient(" + rset.getInt(5) + ", " + rset.getInt(11) + ")\">View</button> &nbsp;&nbsp;&nbsp;");
                    RoverLab.append("<a class=\"btn fa fa-file-pdf-o pdfIcon mb-2 tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Bundle\" href=/md/md.PatientRegRoverLab?ActionID=GETINPUTRoverLab&ID=" + rset.getInt(5) + " target=\"_blank\" rel=\"noopener noreferrer\"></a>");
                    if (rset.getInt(17) == 4) {
                        RoverLab.append("<button type=\"button\" class=\"waves-effect waves-circle btn btn-circle btn-info btn-sm mb-3\" onclick=\"sendResult(" + rset.getInt(11) + "," + rset.getInt(12) + ")\">Send</button> &nbsp;&nbsp;&nbsp;");
                        RoverLab.append("<a class=\"btn glyphicon glyphicon-list-alt tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Result\" href=/md/md.PatientRegRoverLab?ActionID=sendResultReport&O_ID=" + rset.getInt(11) + "&T_ID=" + rset.getInt(12) + "&PatRegIdx=" + rset.getInt(5) + "&TestType=4 target=\"_blank\" rel=\"noopener noreferrer\"></a>");
                    } else {
                        RoverLab.append("<button type=\"button\" class=\"waves-effect waves-circle btn btn-circle btn-info btn-sm mb-3\" onclick=\"sendResult(" + rset.getInt(11) + "," + rset.getInt(12) + ")\">Send</button> &nbsp;&nbsp;&nbsp;");
                        //RoverLab.append("<button type=\"button\" class=\"fa fa-file-pdf-o pdfIcon mb-2\" onclick=\"UpdateInfoPatient(" + rset.getInt(5) + ")\"></button>");
                        RoverLab.append("<a class=\"btn glyphicon glyphicon-list-alt tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Result\" href=/md/md.RoverLabResult?ActionID=getResultPdf&O_ID=" + rset.getInt(11) + "&T_ID=" + rset.getInt(12) + "&PatRegIdx=" + rset.getInt(5) + " target=\"_blank\" rel=\"noopener noreferrer\"></a>");
                    }
                    RoverLab.append("</td>\n");
                    RoverLab.append("</tr>\n");
                }
                rset.close();
                stmt.close();
            }
            Query = "Select Id, Location from roverlab.Locations";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            locationList.append("<option value='' >Select Location</option>");
            while (rset.next()) {
                if (rset.getString(1) == Location) {
                    locationList.append("<option value=" + rset.getString(1) + "  selected>" + rset.getString(2) + "</option>");
                } else {
                    locationList.append("<option value=" + rset.getString(1) + "  >" + rset.getString(2) + "</option>");

                }
            }
            rset.close();
            stmt.close();

            Query = "SELECT Id,TestName FROM " + Database + ".ListofTests " +
                    "WHERE Status = 0 ORDER BY TestName ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            TestList.append("<option value='' >None</option>");
            while (rset.next()) {

                TestList.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();

//            out.println(RoverLab.toString());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("RoverLab", RoverLab.toString());
            Parser.SetField("UserId", UserId);
            Parser.SetField("DateRange", DateRange);
            Parser.SetField("LocationList", locationList.toString());
            Parser.SetField("StageList", stageList.toString());
            Parser.SetField("TestList", TestList.toString());
            Parser.SetField("searchdatefrom", String.valueOf(FromDate));
            Parser.SetField("searchdateto", String.valueOf(ToDate));

            Parser.SetField("Selectedlocation", String.valueOf(Location));
            Parser.SetField("SelectedTestType", String.valueOf(TestType));
            Parser.SetField("SelectedEmailStatus", String.valueOf(EmailStatus));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ResultRegisteredPatient_ROVERLAB.html");


        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientRegRoverLab ** (ShowReport)", servletContext, e, "PatientRegRoverLab", "ShowReport", conn);
            Services.DumException("ShowReport", "PatientRegRoverLab ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void GetData(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
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
        try {
            Dictionary d = doUpload(request, response, out);
            Enumeration en = d.keys();
            while (en.hasMoreElements()) {
                key = (String) en.nextElement();
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
                } else if (key.startsWith("Order")) {
                    Order = (String) d.get(key);
                } else if (key.startsWith("outputFilePath")) {
                    outputFilePath = (String) d.get(key);
                }
            }
            PatientRegId = PatientRegId.substring(4);
            imagedataURL = imagedataURL.substring(4);
            UID = UID.substring(4);
            MRN = MRN.substring(4);
            pageCount = pageCount.substring(4);
            Order = Order.substring(4);
            outputFilePath = outputFilePath.substring(4);

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
                    ImageIO.write(bufferedImage, "png", new File("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_" + i + "_" + PatientRegId + "_" + Ordering[i] + ".png"));

//                    out.println("Result => "+ isValid(new File("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_" + i + "_" + PatientRegId + "_"+Ordering[i]+".png")));// isValid(in);
//                    out.print("HEre ");
                    //String ImageTransparent = this.MakeTransparent(out, "/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_" + i + "_" + PatientRegId + ".png", "/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_" + i + "_" + PatientRegId + ".png");
                    if (isValid(new File("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_" + i + "_" + PatientRegId + "_" + Ordering[i] + ".png"))) {
                        String ImageTransparent = this.MakeTransparent(out, "/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_" + i + "_" + PatientRegId + "_" + Ordering[i] + ".png", "/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_" + i + "_" + PatientRegId + "_" + Ordering[i] + ".png");
                        if (ImageTransparent.trim().toUpperCase().equals("CONVERTED")) {
                            Message = " and Transparency DONE";
                        } else {
                            Message = " and Image Created";
                        }
                    } else {
                        Query = "DELETE FROM roverlab.SignRequest WHERE PatientRegId = '" + PatientRegId + "' ";
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

            Query = "UPDATE roverlab.SignRequest SET isSign = 1 , SignBy = '" + UserId + "', SignTime = NOW() " +
                    "WHERE PatientRegId = " + PatientRegId + " AND " +
                    "MRN = " + MRN + " AND UID = '" + UID + "' ";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Done! Signed PDF is Ready " + Message);
            Parser.SetField("FormName", "PatientRegRoverLab");
            Parser.SetField("ActionID", "");
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message_ROVERLAB.html");

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

    private void PatientUpdateInformation(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String paid = "";
        Statement stmt2 = null;
        ResultSet rset2 = null;
        ResultSet rset3 = null;
        String Query2 = "";
        String PatientName = "";
        String DOB = "";
        String PhNumber = "";
        String CovidStatusVL = "NONE";
        String MRN = "";
        String ReasonVisit = "";
        String DOS = "";
        String COVIDStatus = "";
        String Address = "";
        String City = "";
        String State = "";
        String County = "";
        String ZipCode = "";
        String DoctorsId = "";
        String BundleFnName = "";
        String LabelFnName = "";
        String Gender = "";
        int FoundAddInfo = 0;
        int SelfPayChk = 0;
        String WorkerCompPolicyChk = "0";
        String MotorVehicleAccidentChk = "0";
        String HealthInsuranceChk = "0";
        String InsuredStatus = "";
        String Test = "";
        String COVIDTestDate = "";
        int AdditionalInfoSelect = 0;
        int PatientCatagory = 0;
        int ReasonLeaving = 0;
        int PatientStatus = 0;
        String RefName = "";
        String CovidTestNo = "";
        String VisitNumber = "";
        String Style = "";
        int RefPhysicianName = 0;
        int StageIdx = 0;
        String RefSourceName = "";
        String PrimaryInsurance = "";
        String GroupNo = "";
        String MemId = "";
        String email = "";
        String PatientInvoiceMRN = "";
        String VisitId = "";
        String TestingLocation = "";
        String OrderId = "";
        String OrderDate = "";
        int Status = 0;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer NotesList = new StringBuffer();
        StringBuffer TestBuffer = new StringBuffer();
        StringBuffer AlertList = new StringBuffer();
        StringBuffer AlertListModal = new StringBuffer();
        StringBuffer PatientList = new StringBuffer();
        StringBuffer CovidBuffer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        StringBuffer DoctorsBuffer = new StringBuffer();
        StringBuffer AdditionalInfoSelectBuffer = new StringBuffer();
        StringBuffer PatientCatagoryBuffer = new StringBuffer();
        StringBuffer ReasonLeavingBuffer = new StringBuffer();
        StringBuffer PatientStatusBuffer = new StringBuffer();
        StringBuffer RefPhysicianNameBuffer = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Day = new StringBuffer();
        StringBuffer Year = new StringBuffer();
        StringBuffer Hours = new StringBuffer();
        StringBuffer Mins = new StringBuffer();
        StringBuilder locationList = new StringBuilder();
        StringBuilder stageList = new StringBuilder();
        StringBuilder statusList = new StringBuilder();

        String ID = request.getParameter("ID").trim();
        int orderIdx = Integer.parseInt(request.getParameter("orderId"));

        String facilityName = helper.getFacilityName(request, conn, servletContext, ClientId);

        int Notescount = 0;
        int Alertscount = 0;
        int isVerified = 0;

        int SNo = 1;
        try {
            Query = "Select Bundle_FnName, Label_FnName from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                BundleFnName = rset.getString(1);
                LabelFnName = rset.getString(2);
            }
            rset.close();
            stmt.close();

            Query = "SELECT CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''), IFNULL(PhNumber,''), " +
                    " IFNULL(a.MRN,0),  a.ID, DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'), " +
                    " IFNULL(a.Address,''), " +
                    "IFNULL(a.City,''), IFNULL(a.State,''), IFNULL(a.County,''), IFNULL(a.ZipCode,''),CASE WHEN a.Gender='male' then 'M' else 'F' END, " +
                    "CASE WHEN a.Insured = 'Yes' THEN 'Insured' WHEN a.Insured = 'NO' THEN 'Self Pay' ELSE 'Self Pay' END," +
                    "IFNULL(a.Test,''),IFNULL(a.TestingLocation,''),IFNULL(b.StageIdx,0)," +
                    " b.OrderNum,IFNULL(b.Status,0),IFNULL(isVerified,0), IFNULL(a.email,'') , DATE_FORMAT(b.OrderDate,'%m/%d/%Y') " +
                    " FROM " + Database + ".PatientReg a" +
                    " INNER JOIN " + Database + ".TestOrder b ON a.ID = b.PatRegIdx " +
                    " where a.Status = 0 and a.ID = " + ID;

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientName = rset.getString(1);
                DOB = rset.getString(2);
                PhNumber = rset.getString(3);
                MRN = rset.getString(4);
                DOS = rset.getString(6);
                //COVIDStatus = rset.getString(8);
                Address = rset.getString(7);
                City = rset.getString(8);
                State = rset.getString(9);
                County = rset.getString(10);
                ZipCode = rset.getString(11);
//                DoctorsId = rset.getString(14);
//                SelfPayChk = rset.getInt(15);
                Gender = rset.getString(12);
                InsuredStatus = rset.getString(13);
                Test = rset.getString(14);
                TestingLocation = rset.getString(15);
                StageIdx = rset.getInt(16);
                OrderId = rset.getString(17);
                Status = rset.getInt(18);
                isVerified = rset.getInt(19);
                email = rset.getString(20).trim();
                OrderDate = rset.getString(21).trim();
            }
            rset.close();
            stmt.close();


//            PreparedStatement ps = conn.prepareStatement(
//                    "SELECT Id FROM " + Database + ".TestOrder WHERE PatRegIdx=" + ID);
//            rset = ps.executeQuery();
//            if (rset.next()) {
            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT IFNULL(b.TestName,''),IFNULL(a.OrderId,''),IFNULL(a.Id,'') , " +
                            "IFNULL(a.Priority,'') , IFNULL(a.Narration,''),IFNULL(a.TestStatus,'-1'), " +
                            "IFNULL(a.SampleType,''), IFNULL(a.SampleNumber,''), IFNULL(a.TestIdx,'') " +
                            " FROM " + Database + ".Tests a " +
                            " LEFT JOIN " + Database + ".ListofTests b ON a.TestIdx=b.id " +
                            " WHERE OrderId=" + orderIdx);
            rset2 = ps1.executeQuery();
            while (rset2.next()) {
                Test = rset2.getString(1);

                if (rset2.getInt(9) == 4) {
                    TestBuffer.append("<tr class=\"profile\">\n" +
                            "<td colspan=\"8\">\n" +
                            "<div class=\"panel-group\" id=\"accordionQuali" + Test.replaceAll(" ", "") + "\">\n" +
                            "<div class=\"panel panel-info\">\n" +
                            "\n" +
                            "    <div class=\"panel-heading\" data-toggle=\"collapse\" data-parent=\"#accordionQuali" + Test.replaceAll(" ", "") + "\" href=\"#collapseInQuali" + Test.replaceAll(" ", "") + "\">\n" +
                            "        <h4 class=\"panel-title\">\n" +
                            "            " + Test + "\n" +
                            "        </h4>\n" +
                            "    </div>\n" +
                            "\n" +
                            "    <div id=\"collapseInQuali" + Test.replaceAll(" ", "") + "\" class=\"panel-collapse collapse in show\">\n" +
                            "        <div class=\"panel-body\">\n" +
                            "            <table class=\"table table-bordered table-hover no-border-table recTable tree-table\">\n" +
                            "                <tbody>\n" +
                            "                    <tr data-index=\"0\" class=\"cloned addQualitativeScreen\">\n" +
                            "                        <td width=\"21%\">\n" +
                            "                            <div class=\" compoundClass\">\n" +
                            "                                <select name=\"Test\" id=\"Test" + rset2.getInt(2) + rset2.getInt(3) + "\" class=\"form-control no-border allcompounds\" data-bv-field=\"Test\">\n" +
                            "                                    <option value=" + Test + " selected=\"selected\">" + Test + "</option>\n" +
                            "                                </select>\n" +
                            "                                <small class=\"help-block\" data-bv-validator=\"notEmpty\" data-bv-for=\"Test\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">Please select molecules</small></div>\n" +
                            "                            </td>\n" +
                            "                            <td width=\"15%\">\n" +
                            "                                <div class=\" compoundClass\">\n" +
                            "                                    <select name=\"result\" id=\"result" + rset2.getInt(2) + rset2.getInt(3) + "\" class=\"form-control no-border\" data-bv-field=\"result\" >\n");

                    PreparedStatement ps3 = conn.prepareStatement("SELECT Id, Result from " + Database + ".ListofTestResults");
                    rset3 = ps3.executeQuery();
                    TestBuffer.append("<option value=\"\" selected=\"selected\">Select One</option>");
                    while (rset3.next()) {
                        //TestID == ListofTestResults_ID
                        if (rset2.getInt(6) == rset3.getInt(1))
                            TestBuffer.append("<option value=\"" + rset3.getInt(1) + "\" selected=\"selected\">" + rset3.getString(2) + "</option>");
                        else
                            TestBuffer.append("<option value=\"" + rset3.getInt(1) + "\" >" + rset3.getString(2) + "</option>");
                    }
                    rset3.close();
                    ps3.close();

//                            TestBuffer.append("<option value=\"\" selected=\"selected\">Select One</option>");
//                            "                                        <option value=\"\" selected=\"selected\">Select One</option>\n" +
//                            "                                        <option value=\"INVALID\">INVALID</option>\n" +
//                            "                                        <option value=\"NEGATIVE\">NEGATIVE</option>\n" +
//                            "                                        <option value=\"POSITIVE\">POSITIVE</option>\n" +
//                            "                                        <option value=\"REJECTED\">REJECTED</option>\n" +
                    TestBuffer.append("                                    </select>\n" +
                            "\n" +
                            "                                    <small class=\"help-block\" data-bv-validator=\"notEmpty\" data-bv-for=\"result\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">Please select result</small></div>\n" +
                            "                                </td>\n" +
                            "\n" +
                            "                                <td width=\"28%\">\n" +
//                            "<input type=\"text\" class=\"form-control no-border\" placeholder=\"Sample #\" name=\"sampleType\" id=\"sampleType" + rset2.getInt(2) + rset2.getInt(3) + "\" value=\"" + rset2.getString(7) + "\">\n" +
                            "<input type=\"text\" class=\"form-control no-border\" placeholder=\"Sample #\" name=\"sampleNum\" id=\"sampleNum" + rset2.getInt(2) + rset2.getInt(3) + "\" value=\"" + rset2.getString(8) + "\">\n" +
                            "                                </td>\n" +
                            "                                <td width=\"21%\">\n" +
                            "                                    <div class=\"row\">\n" +
                            "                                        <div class=\"col-md-2\">\n" +
                            "                                            <div class=\" compoundClass\">\n" +
                            "                                                <div class=\"checkbox-inline\">\n" +
                            "                                                    <label> <input type=\"checkbox\" class=\"commentsOnRep colored-blue \" name=\"showCommentsOnReport\" value=\"false\"> <span class=\"text\"></span>\n" +
                            "                                                    </label>\n" +
                            "                                                </div>\n" +
                            "                                            </div>\n" +
                            "                                        </div>\n" +
                            "                                        <div class=\"col-md-9\">\n" +
                            "                                            <div class=\" compoundClass\">\n" +
                            "                                                <input type=\"text\" class=\"form-control no-border\" placeholder=Comments name=\"comments\" id=\"comments" + rset2.getInt(2) + rset2.getInt(3) + "\" value=\"" + rset2.getString(5) + "\" data-bv-field=\"comments\">\n" +
                            "                                                <small class=\"help-block\" data-bv-validator=\"stringLength\" data-bv-for=\"comments\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">allowed max 100 characters</small></div>\n" +
                            "\n" +
                            "                                            </div>\n" +
                            "                                        </div>\n" +
                            "                                    </td>\n" +
                            "                                    <td width=\"15%\">\n" +
                            "\n" +
                            "                                        <div class=\" compoundClass\">\n" +
                            "                                            <select name=\"testPriority\" class=\"form-control no-border\" id=\"testPriority" + rset2.getInt(2) + rset2.getInt(3) + "\">");

                    if (rset2.getString(4).equals("HIGH"))
                        TestBuffer.append("<option value=\"HIGH\" selected >High</option>\n");
                    else
                        TestBuffer.append("<option value=\"HIGH\" >High</option>\n");

                    TestBuffer.append("<option value=\"LOW\" selected >Low</option>\n");


//                            "                                                <option value=\"\">Select One</option>\n" +
//                            "                                                <option value=\"HIGH\">High</option>\n" +
//                            "                                                <option value=\"LOW\" selected>Low</option>\n" +
//
                    TestBuffer.append("                                            </select>\n" +
                            "                                        </div>\n" +
                            "\n" +
                            "                                    </td>\n" +
                            "\n" +
                            "                                    <td width=\"20\">\n" +
                            "                                        <button type=\"button\" class=\"btn btn-primary btn-xs verifyMoleCompounds\" id=\"verifyBtn\" onClick=updateTest(" + rset2.getInt(2) + "," + rset2.getInt(3) + ")>\n" +
                            "                                            Verify\n" +
                            "                                        </button>\n" +
                            "                                    </td>\n" +
                            "                                    <td width=\"20\">\n" +
                            "                                        <button type=\"button\" class=\"btn btn-info btn-xs verifyMoleCompounds\" id=\"sendBtn\" onClick='sendResult(" + rset2.getInt(2) + "," + rset2.getInt(3) + "," + rset2.getInt(9) + ")'>\n" +
                            "                                            Send\n" +
                            "                                        </button>\n" +
                            "                                    </td>\n");

                        /*if (rset2.getInt(6) == -1) {
                            TestBuffer.append(
                                    "  <a type=\"button\" class=\"btn btn-info btn-xs verifyMoleCompounds isDisabled\" id=\"viewBtn\" data-toggle=\"tooltip\" title=\"Result is NOT Updated Yet. Please Update Result!\"'>\n" +
                                            "      <i class=\"fa fa-file-pdf-o\"></i>\n" +
                                            "  </a>\n");
                        } else {
                            TestBuffer.append(
                                    "<a type=\"button\" class=\"btn btn-info btn-xs verifyMoleCompounds\" id=\"viewBtn\" href='/md/md.RoverLabResult?ActionID=getResultPdf&O_ID=" + rset2.getInt(2) + "&T_ID=" + rset2.getInt(3) + "&PatRegIdx=" + ID + "'>\n" +
                                            "           <i class=\"fa fa-file-pdf-o\"></i>\n" +
                                            "       </a>\n");
                        }*/

                    TestBuffer.append("" +
                            "\n" +
                            "                                    <td width=\"12\">\n" +
                            "                                        <button type=\"button\" class=\"btn btn-danger btn-xs removeMoleQualiButton\" id=\"removemoleQualiCompounds" + rset2.getInt(2) + rset2.getInt(3) + "\">\n" +
                            "                                            <i class=\"fa fa-minus\"></i>\n" +
                            "                                        </button>\n" +
                            "\n" +
                            "                                    </td>\n" +
                            "\n" +
                            "\n" +
                            "                                </tr>\n" +
                            "\n" +
                            "                            </tbody>\n" +
                            "                        </table>\n" +
                            "                    </div>\n" +
                            "                </div>\n" +
                            "            </div>\n" +
                            "        </div>\n" +
                            "    </td>\n" +
                            "</tr>");
                } else if (rset2.getInt(9) == 1 || rset2.getInt(9) == 2) {
                    TestBuffer.append("<tr class=\"profile\">\n" +
                            "<td colspan=\"8\">\n" +
                            "<div class=\"panel-group\" id=\"accordionQuali" + Test.replaceAll(" ", "") + "\">\n" +
                            "<div class=\"panel panel-info\">\n" +
                            "\n" +
                            "    <div class=\"panel-heading\" data-toggle=\"collapse\" data-parent=\"#accordionQuali" + Test.replaceAll(" ", "") + "\" href=\"#collapseInQuali" + Test.replaceAll(" ", "") + "\">\n" +
                            "        <h4 class=\"panel-title\">\n" +
                            "            " + Test + "\n" +
                            "        </h4>\n" +
                            "    </div>\n" +
                            "\n" +
                            "    <div id=\"collapseInQuali" + Test.replaceAll(" ", "") + "\" class=\"panel-collapse collapse in show\">\n" +
                            "        <div class=\"panel-body\">\n" +
                            "            <table class=\"table table-bordered table-hover no-border-table recTable tree-table\">\n" +
                            "                <tbody>\n" +
                            "                    <tr data-index=\"0\" class=\"cloned addQualitativeScreen\">\n" +
                            "                        <td width=\"21%\">\n" +
                            "                            <div class=\" compoundClass\">\n" +
                            "                                <select name=\"Test\" id=\"Test" + rset2.getInt(2) + rset2.getInt(3) + "\" class=\"form-control no-border allcompounds\" data-bv-field=\"Test\">\n" +
                            "                                    <option value=" + Test + " selected=\"selected\">" + Test + "</option>\n" +
                            "                                </select>\n" +
                            "                                <small class=\"help-block\" data-bv-validator=\"notEmpty\" data-bv-for=\"Test\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">Please select molecules</small></div>\n" +
                            "                            </td>\n" +
                            "                            <td width=\"15%\">\n" +
                            "                                <div class=\" compoundClass\">\n" +
                            "                                    <select name=\"result\" id=\"result" + rset2.getInt(2) + rset2.getInt(3) + "\" class=\"form-control no-border\" data-bv-field=\"result\" disabled>\n");

                    PreparedStatement ps3 = conn.prepareStatement("SELECT Id, Result from " + Database + ".ListofTestResults");
                    rset3 = ps3.executeQuery();
                    TestBuffer.append("<option value=\"\" selected=\"selected\">Select One</option>");
                    while (rset3.next()) {
                        //TestID == ListofTestResults_ID
                        if (rset2.getInt(6) == rset3.getInt(1))
                            TestBuffer.append("<option value=\"" + rset3.getInt(1) + "\" selected=\"selected\">" + rset3.getString(2) + "</option>");
                        else
                            TestBuffer.append("<option value=\"" + rset3.getInt(1) + "\" >" + rset3.getString(2) + "</option>");
                    }
                    rset3.close();
                    ps3.close();

//                            TestBuffer.append("<option value=\"\" selected=\"selected\">Select One</option>");
//                            "                                        <option value=\"\" selected=\"selected\">Select One</option>\n" +
//                            "                                        <option value=\"INVALID\">INVALID</option>\n" +
//                            "                                        <option value=\"NEGATIVE\">NEGATIVE</option>\n" +
//                            "                                        <option value=\"POSITIVE\">POSITIVE</option>\n" +
//                            "                                        <option value=\"REJECTED\">REJECTED</option>\n" +
                    TestBuffer.append("                                    </select>\n" +
                            "\n" +
                            "                                    <small class=\"help-block\" data-bv-validator=\"notEmpty\" data-bv-for=\"result\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">Please select result</small></div>\n" +
                            "                                </td>\n" +
                            "\n" +
                            "                                <td width=\"28%\">\n" +
//                            "<input type=\"text\" class=\"form-control no-border\" placeholder=\"Sample #\" name=\"sampleType\" id=\"sampleType" + rset2.getInt(2) + rset2.getInt(3) + "\" value=\"" + rset2.getString(7) + "\">\n" +
                            "<input type=\"text\" class=\"form-control no-border\" placeholder=\"Sample #\" name=\"sampleNum\" id=\"sampleNum" + rset2.getInt(2) + rset2.getInt(3) + "\" value=\"" + rset2.getString(8) + "\">\n" +
                            "                                </td>\n" +
                            "                                <td width=\"21%\">\n" +
                            "                                    <div class=\"row\">\n" +
                            "                                        <div class=\"col-md-2\">\n" +
                            "                                            <div class=\" compoundClass\">\n" +
                            "                                                <div class=\"checkbox-inline\">\n" +
                            "                                                    <label> <input type=\"checkbox\" class=\"commentsOnRep colored-blue \" name=\"showCommentsOnReport\" value=\"false\"> <span class=\"text\"></span>\n" +
                            "                                                    </label>\n" +
                            "                                                </div>\n" +
                            "                                            </div>\n" +
                            "                                        </div>\n" +
                            "                                        <div class=\"col-md-9\">\n" +
                            "                                            <div class=\" compoundClass\">\n" +
                            "                                                <input type=\"text\" class=\"form-control no-border\" placeholder=Comments name=\"comments\" id=\"comments" + rset2.getInt(2) + rset2.getInt(3) + "\" value=\"" + rset2.getString(5) + "\" data-bv-field=\"comments\">\n" +
                            "                                                <small class=\"help-block\" data-bv-validator=\"stringLength\" data-bv-for=\"comments\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">allowed max 100 characters</small></div>\n" +
                            "\n" +
                            "                                            </div>\n" +
                            "                                        </div>\n" +
                            "                                    </td>\n" +
                            "                                    <td width=\"15%\">\n" +
                            "\n" +
                            "                                        <div class=\" compoundClass\">\n" +
                            "                                            <select name=\"testPriority\" class=\"form-control no-border\" id=\"testPriority" + rset2.getInt(2) + rset2.getInt(3) + "\">");

                    if (rset2.getString(4).equals("HIGH"))
                        TestBuffer.append("<option value=\"HIGH\" selected >High</option>\n");
                    else
                        TestBuffer.append("<option value=\"HIGH\" >High</option>\n");

                    TestBuffer.append("<option value=\"LOW\" selected >Low</option>\n");


//                            "                                                <option value=\"\">Select One</option>\n" +
//                            "                                                <option value=\"HIGH\">High</option>\n" +
//                            "                                                <option value=\"LOW\" selected>Low</option>\n" +
//
                    TestBuffer.append("                                            </select>\n" +
                            "                                        </div>\n" +
                            "\n" +
                            "                                    </td>\n" +
                            "\n" +
                            "                                    <td width=\"20\">\n" +
//                               onClick=updateTest(" + rset2.getInt(2) + "," + rset2.getInt(3) + ")
                            "                                        <button type=\"button\" class=\"btn btn-primary btn-xs verifyMoleCompounds \" onClick=updateTest(" + rset2.getInt(2) + "," + rset2.getInt(3) + ") id=\"verifyBtn\" >\n" +
                            "                                            Verify\n" +
                            "                                        </button>\n" +
                            "                                    </td>\n" +
                            "                                    <td width=\"20\">\n" +
                            "                                        <button type=\"button\" class=\"btn btn-info btn-xs verifyMoleCompounds\" id=\"sendBtn\" onClick='sendResult(" + rset2.getInt(2) + "," + rset2.getInt(3) + "," + rset2.getInt(9) + ")'>\n" +
                            "                                            Send\n" +
                            "                                        </button>\n" +
                            "                                    </td>\n" +
                            "                                    <td width=\"20\">\n");

                    if (rset2.getInt(6) == -1) {
                        TestBuffer.append(
                                "  <a type=\"button\" class=\"btn btn-info btn-xs verifyMoleCompounds isDisabled\" id=\"viewBtn\" data-toggle=\"tooltip\" title=\"Result is NOT Updated Yet. Please Update Result!\"'>\n" +
                                        "      <i class=\"fa fa-file-pdf-o\"></i>\n" +
                                        "  </a>\n");
                    } else {
                        TestBuffer.append(
                                "<a type=\"button\" class=\"btn btn-info btn-xs verifyMoleCompounds\" id=\"viewBtn\" href='/md/md.RoverLabResult?ActionID=getResultPdf&O_ID=" + rset2.getInt(2) + "&T_ID=" + rset2.getInt(3) + "&PatRegIdx=" + ID + "'>\n" +
                                        "           <i class=\"fa fa-file-pdf-o\"></i>\n" +
                                        "       </a>\n");
                    }

                    TestBuffer.append("                                    </td>\n" +
                            "\n" +
                            "                                    <td width=\"12\">\n" +
                            "                                        <button type=\"button\" class=\"btn btn-danger btn-xs removeMoleQualiButton\" id=\"removemoleQualiCompounds" + rset2.getInt(2) + rset2.getInt(3) + "\">\n" +
                            "                                            <i class=\"fa fa-minus\"></i>\n" +
                            "                                        </button>\n" +
                            "\n" +
                            "                                    </td>\n" +
                            "\n" +
                            "\n" +
                            "                                </tr>\n" +
                            "\n" +
                            "                            </tbody>\n" +
                            "                        </table>\n" +
                            "                    </div>\n" +
                            "                </div>\n" +
                            "            </div>\n" +
                            "        </div>\n" +
                            "    </td>\n" +
                            "</tr>");
                } else {
                    TestBuffer.append("<tr class=\"profile\">\n" +
                            "<td colspan=\"8\">\n" +
                            "<div class=\"panel-group\" id=\"accordionQuali" + Test.replaceAll(" ", "") + "\">\n" +
                            "<div class=\"panel panel-info\">\n" +
                            "\n" +
                            "    <div class=\"panel-heading\" data-toggle=\"collapse\" data-parent=\"#accordionQuali" + Test.replaceAll(" ", "") + "\" href=\"#collapseInQuali" + Test.replaceAll(" ", "") + "\">\n" +
                            "        <h4 class=\"panel-title\">\n" +
                            "            " + Test + "\n" +
                            "        </h4>\n" +
                            "    </div>\n" +
                            "\n" +
                            "    <div id=\"collapseInQuali" + Test.replaceAll(" ", "") + "\" class=\"panel-collapse collapse in show\">\n" +
                            "        <div class=\"panel-body\">\n" +
                            "            <table class=\"table table-bordered table-hover no-border-table recTable tree-table\">\n" +
                            "                <tbody>\n" +
                            "                    <tr data-index=\"0\" class=\"cloned addQualitativeScreen\">\n" +
                            "                        <td width=\"21%\">\n" +
                            "                            <div class=\" compoundClass\">\n" +
                            "                                <select name=\"Test\" id=\"Test" + rset2.getInt(2) + rset2.getInt(3) + "\" class=\"form-control no-border allcompounds\" data-bv-field=\"Test\">\n" +
                            "                                    <option value=" + Test + " selected=\"selected\">" + Test + "</option>\n" +
                            "                                </select>\n" +
                            "                                <small class=\"help-block\" data-bv-validator=\"notEmpty\" data-bv-for=\"Test\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">Please select molecules</small></div>\n" +
                            "                            </td>\n" +
                            "                            <td width=\"15%\">\n" +
                            "                                <div class=\" compoundClass\">\n" +
                            "                                    <select name=\"result\" id=\"result" + rset2.getInt(2) + rset2.getInt(3) + "\" class=\"form-control no-border\" data-bv-field=\"result\" disabled>\n");

                    PreparedStatement ps3 = conn.prepareStatement("SELECT Id, Result from " + Database + ".ListofTestResults");
                    rset3 = ps3.executeQuery();
                    TestBuffer.append("<option value=\"\" selected=\"selected\">Select One</option>");
                    while (rset3.next()) {
                        //TestID == ListofTestResults_ID
                        if (rset2.getInt(6) == rset3.getInt(1))
                            TestBuffer.append("<option value=\"" + rset3.getInt(1) + "\" selected=\"selected\">" + rset3.getString(2) + "</option>");
                        else
                            TestBuffer.append("<option value=\"" + rset3.getInt(1) + "\" >" + rset3.getString(2) + "</option>");
                    }
                    rset3.close();
                    ps3.close();

//                            TestBuffer.append("<option value=\"\" selected=\"selected\">Select One</option>");
//                            "                                        <option value=\"\" selected=\"selected\">Select One</option>\n" +
//                            "                                        <option value=\"INVALID\">INVALID</option>\n" +
//                            "                                        <option value=\"NEGATIVE\">NEGATIVE</option>\n" +
//                            "                                        <option value=\"POSITIVE\">POSITIVE</option>\n" +
//                            "                                        <option value=\"REJECTED\">REJECTED</option>\n" +
                    TestBuffer.append("                                    </select>\n" +
                            "\n" +
                            "                                    <small class=\"help-block\" data-bv-validator=\"notEmpty\" data-bv-for=\"result\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">Please select result</small></div>\n" +
                            "                                </td>\n" +
                            "\n" +
                            "                                <td width=\"28%\">\n" +
//                            "<input type=\"text\" class=\"form-control no-border\" placeholder=\"Sample #\" name=\"sampleType\" id=\"sampleType" + rset2.getInt(2) + rset2.getInt(3) + "\" value=\"" + rset2.getString(7) + "\">\n" +
                            "<input type=\"text\" class=\"form-control no-border\" placeholder=\"Sample #\" name=\"sampleNum\" id=\"sampleNum" + rset2.getInt(2) + rset2.getInt(3) + "\" value=\"" + rset2.getString(8) + "\">\n" +
                            "                                </td>\n" +
                            "                                <td width=\"21%\">\n" +
                            "                                    <div class=\"row\">\n" +
                            "                                        <div class=\"col-md-2\">\n" +
                            "                                            <div class=\" compoundClass\">\n" +
                            "                                                <div class=\"checkbox-inline\">\n" +
                            "                                                    <label> <input type=\"checkbox\" class=\"commentsOnRep colored-blue \" name=\"showCommentsOnReport\" value=\"false\"> <span class=\"text\"></span>\n" +
                            "                                                    </label>\n" +
                            "                                                </div>\n" +
                            "                                            </div>\n" +
                            "                                        </div>\n" +
                            "                                        <div class=\"col-md-9\">\n" +
                            "                                            <div class=\" compoundClass\">\n" +
                            "                                                <input type=\"text\" class=\"form-control no-border\" placeholder=Comments name=\"comments\" id=\"comments" + rset2.getInt(2) + rset2.getInt(3) + "\" value=\"" + rset2.getString(5) + "\" data-bv-field=\"comments\">\n" +
                            "                                                <small class=\"help-block\" data-bv-validator=\"stringLength\" data-bv-for=\"comments\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">allowed max 100 characters</small></div>\n" +
                            "\n" +
                            "                                            </div>\n" +
                            "                                        </div>\n" +
                            "                                    </td>\n" +
                            "                                    <td width=\"15%\">\n" +
                            "\n" +
                            "                                        <div class=\" compoundClass\">\n" +
                            "                                            <select name=\"testPriority\" class=\"form-control no-border\" id=\"testPriority" + rset2.getInt(2) + rset2.getInt(3) + "\">");

                    if (rset2.getString(4).equals("HIGH"))
                        TestBuffer.append("<option value=\"HIGH\" selected >High</option>\n");
                    else
                        TestBuffer.append("<option value=\"HIGH\" >High</option>\n");

                    TestBuffer.append("<option value=\"LOW\" selected >Low</option>\n");


//                            "                                                <option value=\"\">Select One</option>\n" +
//                            "                                                <option value=\"HIGH\">High</option>\n" +
//                            "                                                <option value=\"LOW\" selected>Low</option>\n" +
//
                    TestBuffer.append("                                            </select>\n" +
                            "                                        </div>\n" +
                            "\n" +
                            "                                    </td>\n" +
                            "\n" +
                            "                                    <td width=\"20\">\n" +
//                                        onClick='updateTest(" + rset2.getInt(2) + "," + rset2.getInt(3) + ")'
                            "                                        <button type=\"button\" class=\"btn btn-primary btn-xs verifyMoleCompounds\" id=\"verifyBtn\" onClick=updateTest(" + rset2.getInt(2) + "," + rset2.getInt(3) + ") >\n" +
                            "                                            Verify\n" +
                            "                                        </button>\n" +
                            "                                    </td>\n" +
                            "                                    <td width=\"20\">\n" +
                            "                                        <button type=\"button\" class=\"btn btn-info btn-xs verifyMoleCompounds\" id=\"sendBtn\" onClick='sendResult(" + rset2.getInt(2) + "," + rset2.getInt(3) + "," + rset2.getInt(9) + ")'>\n" +
                            "                                            Send\n" +
                            "                                        </button>\n" +
                            "                                    </td>\n" +
                            "                                    <td width=\"20\">\n");

                    if (rset2.getInt(6) == -1) {
                        TestBuffer.append(
                                "  <a type=\"button\" class=\"btn btn-info btn-xs verifyMoleCompounds isDisabled\" id=\"viewBtn\" data-toggle=\"tooltip\" title=\"Result is NOT Updated Yet. Please Update Result!\"'>\n" +
                                        "      <i class=\"fa fa-file-pdf-o\"></i>\n" +
                                        "  </a>\n");
                    } else {
                        TestBuffer.append(
                                "<a type=\"button\" class=\"btn btn-info btn-xs verifyMoleCompounds\" id=\"viewBtn\" href='/md/md.RoverLabResult?ActionID=getResultPdf&O_ID=" + rset2.getInt(2) + "&T_ID=" + rset2.getInt(3) + "&PatRegIdx=" + ID + "'>\n" +
                                        "           <i class=\"fa fa-file-pdf-o\"></i>\n" +
                                        "       </a>\n");
                    }

                    TestBuffer.append("                                    </td>\n" +
                            "\n" +
                            "                                    <td width=\"12\">\n" +
                            "                                        <button type=\"button\" class=\"btn btn-danger btn-xs removeMoleQualiButton\" id=\"removemoleQualiCompounds" + rset2.getInt(2) + rset2.getInt(3) + "\">\n" +
                            "                                            <i class=\"fa fa-minus\"></i>\n" +
                            "                                        </button>\n" +
                            "\n" +
                            "                                    </td>\n" +
                            "\n" +
                            "\n" +
                            "                                </tr>\n" +
                            "\n" +
                            "                            </tbody>\n" +
                            "                        </table>\n" +
                            "                    </div>\n" +
                            "                </div>\n" +
                            "            </div>\n" +
                            "        </div>\n" +
                            "    </td>\n" +
                            "</tr>");
                }
            }
            rset2.close();
            ps1.close();
//            }
//            rset.close();
//            ps.close();


//            if(Test.equals("Both")){
//                TestBuffer.append("<tr data-index=\"0\" class=\"cloned addQualitativeScreen\">\n" +
//                        "<td width=\"21%\">\n" +
//                        "<div class=\"form-group compoundClass\">\n" +
//                        "<select name=\"test\" id=\"test\" class=\"form-control no-border allcompounds\">\n" +
//                        "<option value=\"COVID-19 PCR\" selected=\"selected\">COVID-19 PCR</option>\n" +
//                        "</select>\n" +
//                        "</td>\n" +
//                        "<td width=\"15%\">\n" +
//                        "<div class=\"form-group compoundClass\">\n" +
//                        "\n" +
//                        "<select name=TestResult id=\"TestResult\" class=\"form-control no-border\\\" data-bv-field=\"TestResult\">\n" +
//                        "   <option value=\"\" selected=\"selected\">Select One</option>\\n\" +\n" +
//                        "   <option value=\"INVALID\">INVALID</option>\n" +
//                        "   <option value=\"NEGATIVE\">NEGATIVE</option>\n" +
//                        "   <option value=\"POSITIVE\">POSITIVE</option>\n" +
//                        "   <option value=\"REJECTED\">REJECTED</option>\n" +
//                        "   </select>\n" +
//                        "</div>\n" +
//                        "</td>\n" +
//                        "\n" +
//                        "<td width=\"28%\">\n" +
//                        "\n" +
//                        "</td>\n" +
//                        "<td width=\"21%\">\n" +
//                        "<div class=\"row\">\n" +
//                        "\t<div class=\"col-md-2\">\n" +
//                        "\t\t<div class=\"form-group compoundClass\">\n" +
//                        "\t\t\t<div class=\"checkbox-inline\">\n" +
//                        "\t\t\t\t<label> <input type=\"checkbox\" class=\"commentsOnRep colored-blue \" name=\"showCommentsOnReport\" value=\"false\"> \n" +
//                        "\t\t\t\t\t<span class=\"text\"></span>\n" +
//                        "\t\t\t\t</label>\n" +
//                        "\t\t\t</div>\n" +
//                        "\t\t</div>\n" +
//                        "\t</div>\n" +
//                        "\t<div class=\"col-md-9\">\n" +
//                        "\t\t<div class=\"form-group compoundClass\">\n" +
//                        "\t\t\t<input type=\"text\" class=\"form-control no-border\" name=\"comments\">\n" +
//                        "\t\t\t</div>\n" +
//                        "\n" +
//                        "\t\t</div>\n" +
//                        "\t</div>\n" +
//                        "</td>\n" +
//                        "<td width=\"15%\">\n" +
//                        "\n" +
//                        "\t<div class=\"form-group compoundClass\">\n" +
//                        "\t\t<select name=\"testPriority\" class=\"form-control no-border\" id=\"testPriority\">\n" +
//                        "\t\t\t<option value=\"\">Select One</option>\n" +
//                        "\t\t\t<option value=\"HIGH\">High</option>\n" +
//                        "\t\t\t<option value=\"LOW\">Low</option>\n" +
//                        "\t\t</select>\n" +
//                        "\t</div>\n" +
//                        "\n" +
//                        "</td>\n" +
//                        "\n" +
//                        "<td width=\"20\">\n" +
//                        "\t<button type=\"button\" class=\"btn btn-primary btn-xs verifyMoleCompounds\" id=\"verifyBtnmoleQualiCompounds0\" onclick=\"verifyQualitative(this,'moleQualiCompounds','',false)\">\n" +
//                        "\t\tVerify\n" +
//                        "\t</button>\n" +
//                        "</td>\n" +
//                        "\n" +
//                        "<td width=\"12\">\n" +
//                        "\t<button type=\"button\" class=\"btn btn-danger btn-xs removeMoleQualiButton\" id=\"removemoleQualiCompounds0\">\n" +
//                        "\t\t<i class=\"fa fa-minus\"></i>\n" +
//                        "\t</button>\n" +
//                        "\n" +
//                        "</td>\n" +
//                        "\n" +
//                        "\n" +
//                        "</tr>");
//                TestBuffer.append("<tr data-index=\"0\" class=\"cloned addQualitativeScreen\">\n" +
//                        "<td width=\"21%\">\n" +
//                        "<div class=\"form-group compoundClass\">\n" +
//                        "<select name=\"test\" id=\"test\" class=\"form-control no-border allcompounds\">\n" +
//                        "<option value=\"COVID-19 Rapid Antigen Test\" selected=\"selected\">COVID-19 Rapid Antigen Test</option>\n" +
//                        "</select>\n" +
//                        "</td>\n" +
//                        "<td width=\"15%\">\n" +
//                        "<div class=\"form-group compoundClass\">\n" +
//                        "\n" +
//                        "<select name=TestResult id=\"TestResult\" class=\"form-control no-border\\\" data-bv-field=\"TestResult\">\n" +
//                        "   <option value=\"\" selected=\"selected\">Select One</option>\\n\" +\n" +
//                        "   <option value=\"INVALID\">INVALID</option>\n" +
//                        "   <option value=\"NEGATIVE\">NEGATIVE</option>\n" +
//                        "   <option value=\"POSITIVE\">POSITIVE</option>\n" +
//                        "   <option value=\"REJECTED\">REJECTED</option>\n" +
//                        "   </select>\n" +
//                        "</div>\n" +
//                        "</td>\n" +
//                        "\n" +
//                        "<td width=\"28%\">\n" +
//                        "\n" +
//                        "</td>\n" +
//                        "<td width=\"21%\">\n" +
//                        "<div class=\"row\">\n" +
//                        "\t<div class=\"col-md-2\">\n" +
//                        "\t\t<div class=\"form-group compoundClass\">\n" +
//                        "\t\t\t<div class=\"checkbox-inline\">\n" +
//                        "\t\t\t\t<label> <input type=\"checkbox\" class=\"commentsOnRep colored-blue \" name=\"showCommentsOnReport\" value=\"false\"> \n" +
//                        "\t\t\t\t\t<span class=\"text\"></span>\n" +
//                        "\t\t\t\t</label>\n" +
//                        "\t\t\t</div>\n" +
//                        "\t\t</div>\n" +
//                        "\t</div>\n" +
//                        "\t<div class=\"col-md-9\">\n" +
//                        "\t\t<div class=\"form-group compoundClass\">\n" +
//                        "\t\t\t<input type=\"text\" class=\"form-control no-border\" name=\"comments\">\n" +
//                        "\t\t\t</div>\n" +
//                        "\n" +
//                        "\t\t</div>\n" +
//                        "\t</div>\n" +
//                        "</td>\n" +
//                        "<td width=\"15%\">\n" +
//                        "\n" +
//                        "\t<div class=\"form-group compoundClass\">\n" +
//                        "\t\t<select name=\"testPriority\" class=\"form-control no-border\" id=\"testPriority\">\n" +
//                        "\t\t\t<option value=\"\">Select One</option>\n" +
//                        "\t\t\t<option value=\"HIGH\">High</option>\n" +
//                        "\t\t\t<option value=\"LOW\">Low</option>\n" +
//                        "\t\t</select>\n" +
//                        "\t</div>\n" +
//                        "\n" +
//                        "</td>\n" +
//                        "\n" +
//                        "<td width=\"20\">\n" +
//                        "\t<button type=\"button\" class=\"btn btn-primary btn-xs verifyMoleCompounds\" id=\"verifyBtnmoleQualiCompounds0\" onclick=\"verifyQualitative(this,'moleQualiCompounds','',false)\">\n" +
//                        "\t\tVerify\n" +
//                        "\t</button>\n" +
//                        "</td>\n" +
//                        "\n" +
//                        "<td width=\"12\">\n" +
//                        "\t<button type=\"button\" class=\"btn btn-danger btn-xs removeMoleQualiButton\" id=\"removemoleQualiCompounds0\">\n" +
//                        "\t\t<i class=\"fa fa-minus\"></i>\n" +
//                        "\t</button>\n" +
//                        "\n" +
//                        "</td>\n" +
//                        "\n" +
//                        "\n" +
//                        "</tr>");
//            }else{
//                TestBuffer.append("<tr data-index=\"0\" class=\"cloned addQualitativeScreen\">\n" +
//                        "<td width=\"21%\">\n" +
//                        "<div class=\"form-group compoundClass\">\n" +
//                        "<select name=\"test\" id=\"test\" class=\"form-control no-border allcompounds\">\n" +
//                        "<option value="+Test+" selected=\"selected\">"+Test+"</option>\n" +
//                        "</select>\n" +
//                        "</td>\n" +
//                        "<td width=\"15%\">\n" +
//                        "<div class=\"form-group compoundClass\">\n" +
//                        "\n" +
//                        "<select name=TestResult id=\"TestResult\" class=\"form-control no-border\\\" data-bv-field=\"TestResult\">\n" +
//                        "   <option value=\"\" selected=\"selected\">Select One</option>\\n\" +\n" +
//                        "   <option value=\"INVALID\">INVALID</option>\n" +
//                        "   <option value=\"NEGATIVE\">NEGATIVE</option>\n" +
//                        "   <option value=\"POSITIVE\">POSITIVE</option>\n" +
//                        "   <option value=\"REJECTED\">REJECTED</option>\n" +
//                        "   </select>\n" +
//                        "</div>\n" +
//                        "</td>\n" +
//                        "\n" +
//                        "<td width=\"28%\">\n" +
//                        "\n" +
//                        "</td>\n" +
//                        "<td width=\"21%\">\n" +
//                        "<div class=\"row\">\n" +
//                        "\t<div class=\"col-md-2\">\n" +
//                        "\t\t<div class=\"form-group compoundClass\">\n" +
//                        "\t\t\t<div class=\"checkbox-inline\">\n" +
//                        "\t\t\t\t<label> <input type=\"checkbox\" class=\"commentsOnRep colored-blue \" name=\"showCommentsOnReport\" value=\"false\"> \n" +
//                        "\t\t\t\t\t<span class=\"text\"></span>\n" +
//                        "\t\t\t\t</label>\n" +
//                        "\t\t\t</div>\n" +
//                        "\t\t</div>\n" +
//                        "\t</div>\n" +
//                        "\t<div class=\"col-md-9\">\n" +
//                        "\t\t<div class=\"form-group compoundClass\">\n" +
//                        "\t\t\t<input type=\"text\" class=\"form-control no-border\" name=\"comments\">\n" +
//                        "\t\t\t</div>\n" +
//                        "\n" +
//                        "\t\t</div>\n" +
//                        "\t</div>\n" +
//                        "</td>\n" +
//                        "<td width=\"15%\">\n" +
//                        "\n" +
//                        "\t<div class=\"form-group compoundClass\">\n" +
//                        "\t\t<select name=\"testPriority\" class=\"form-control no-border\" id=\"testPriority\">\n" +
//                        "\t\t\t<option value=\"\">Select One</option>\n" +
//                        "\t\t\t<option value=\"HIGH\">High</option>\n" +
//                        "\t\t\t<option value=\"LOW\">Low</option>\n" +
//                        "\t\t</select>\n" +
//                        "\t</div>\n" +
//                        "\n" +
//                        "</td>\n" +
//                        "\n" +
//                        "<td width=\"20\">\n" +
//                        "\t<button type=\"button\" class=\"btn btn-primary btn-xs verifyMoleCompounds\" id=\"verifyBtnmoleQualiCompounds0\" onclick=\"verifyQualitative(this,'moleQualiCompounds','',false)\">\n" +
//                        "\t\tVerify\n" +
//                        "\t</button>\n" +
//                        "</td>\n" +
//                        "\n" +
//                        "<td width=\"12\">\n" +
//                        "\t<button type=\"button\" class=\"btn btn-danger btn-xs removeMoleQualiButton\" id=\"removemoleQualiCompounds0\">\n" +
//                        "\t\t<i class=\"fa fa-minus\"></i>\n" +
//                        "\t</button>\n" +
//                        "\n" +
//                        "</td>\n" +
//                        "\n" +
//                        "\n" +
//                        "</tr>");
//            }

//            if (Test.equals("Both")) {
//                TestBuffer.append("<tr class=\"profile\">\n" +
//                        "<td colspan=\"8\">\n" +
//                        "<div class=\"panel-group\" id=\"accordionQualiPCR\">\n" +
//                        "<div class=\"panel panel-info\">\n" +
//                        "\n" +
//                        "    <div class=\"panel-heading\">\n" +
//                        "        <h4 class=\"panel-title\">\n" +
//                        "            <a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent=\"#accordionQualiPCR\" href=\"#collapseInQualiPCR\">\n" +
//                        "            COVID-19 PCR</a>\n" +
//                        "        </h4>\n" +
//                        "    </div>\n" +
//                        "\n" +
//                        "    <div id=\"collapseInQualiPCR\" class=\"panel-collapse collapse in\">\n" +
//                        "        <div class=\"panel-body\">\n" +
//                        "            <table class=\"table table-bordered table-hover no-border-table recTable tree-table\">\n" +
//                        "                <tbody>\n" +
//                        "                    <tr data-index=\"0\" class=\"cloned addQualitativeScreen\">\n" +
//                        "                        <td width=\"21%\">\n" +
//                        "                            <div class=\"form-group compoundClass\">\n" +
//                        "                                <select name=\"Test\" id=\"Test\" class=\"form-control no-border allcompounds\" data-bv-field=\"Test\">\n" +
//                        "                                    <option value=\"COVID-19 PCR\" selected=\"selected\">COVID-19 PCR</option>\n" +
//                        "                                </select>\n" +
//                        "                                <small class=\"help-block\" data-bv-validator=\"notEmpty\" data-bv-for=\"Test\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">Please select molecules</small></div>\n" +
//                        "                            </td>\n" +
//                        "                            <td width=\"15%\">\n" +
//                        "                                <div class=\"form-group compoundClass\">\n" +
//                        "\n" +
//                        "                                    <select name=\"result\" id=\"result\" class=\"form-control no-border\" data-bv-field=\"result\">\n" +
//                        "                                        <option value=\"\" selected=\"selected\">Select One</option>\n" +
//                        "                                        <option value=\"INVALID\">INVALID</option>\n" +
//                        "                                        <option value=\"NEGATIVE\">NEGATIVE</option>\n" +
//                        "                                        <option value=\"POSITIVE\">POSITIVE</option>\n" +
//                        "                                        <option value=\"REJECTED\">REJECTED</option>\n" +
//                        "                                    </select>\n" +
//                        "\n" +
//                        "                                    <small class=\"help-block\" data-bv-validator=\"notEmpty\" data-bv-for=\"result\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">Please select result</small></div>\n" +
//                        "                                </td>\n" +
//                        "\n" +
//                        "                                <td width=\"28%\">\n" +
//                        "\n" +
//                        "                                </td>\n" +
//                        "                                <td width=\"21%\">\n" +
//                        "                                    <div class=\"row\">\n" +
//                        "                                        <div class=\"col-md-2\">\n" +
//                        "                                            <div class=\"form-group compoundClass\">\n" +
//                        "                                                <div class=\"checkbox-inline\">\n" +
//                        "                                                    <label> <input type=\"checkbox\" class=\"commentsOnRep colored-blue \" name=\"showCommentsOnReport\" value=\"false\"> <span class=\"text\"></span>\n" +
//                        "                                                    </label>\n" +
//                        "                                                </div>\n" +
//                        "                                            </div>\n" +
//                        "                                        </div>\n" +
//                        "                                        <div class=\"col-md-9\">\n" +
//                        "                                            <div class=\"form-group compoundClass\">\n" +
//                        "                                                <input type=\"text\" class=\"form-control no-border\" name=\"comments\" value=\"\" data-bv-field=\"comments\">\n" +
//                        "                                                <small class=\"help-block\" data-bv-validator=\"stringLength\" data-bv-for=\"comments\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">allowed max 100 characters</small></div>\n" +
//                        "\n" +
//                        "                                            </div>\n" +
//                        "                                        </div>\n" +
//                        "                                    </td>\n" +
//                        "                                    <td width=\"15%\">\n" +
//                        "\n" +
//                        "                                        <div class=\"form-group compoundClass\">\n" +
//                        "                                            <select name=\"testPriority\" class=\"form-control no-border\" id=\"testPriority\">\n" +
//                        "                                                <option value=\"\">Select One</option>\n" +
//                        "                                                <option value=\"HIGH\">High</option>\n" +
//                        "                                                <option value=\"LOW\">Low</option>\n" +
//                        "                                            </select>\n" +
//                        "                                        </div>\n" +
//                        "\n" +
//                        "                                    </td>\n" +
//                        "\n" +
//                        "                                    <td width=\"20\">\n" +
//                        "                                        <button type=\"button\" class=\"btn btn-primary btn-xs verifyMoleCompounds\" id=\"verifyBtn\">\n" +
//                        "                                            Verify\n" +
//                        "                                        </button>\n" +
//                        "                                    </td>\n" +
//                        "\n" +
//                        "                                    <td width=\"12\">\n" +
//                        "                                        <button type=\"button\" class=\"btn btn-danger btn-xs removeMoleQualiButton\" id=\"removemoleQualiCompounds0\">\n" +
//                        "                                            <i class=\"fa fa-minus\"></i>\n" +
//                        "                                        </button>\n" +
//                        "\n" +
//                        "                                    </td>\n" +
//                        "\n" +
//                        "\n" +
//                        "                                </tr>\n" +
//                        "\n" +
//                        "                            </tbody>\n" +
//                        "                        </table>\n" +
//                        "                    </div>\n" +
//                        "                </div>\n" +
//                        "            </div>\n" +
//                        "        </div>\n" +
//                        "    </td>\n" +
//                        "</tr>");
//
//                TestBuffer.append("<tr class=\"profile\">\n" +
//                        "<td colspan=\"8\">\n" +
//                        "<div class=\"panel-group\" id=\"accordionQualiRPT\">\n" +
//                        "<div class=\"panel panel-info\">\n" +
//                        "\n" +
//                        "    <div class=\"panel-heading\">\n" +
//                        "        <h4 class=\"panel-title\">\n" +
//                        "            <a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent=\"#accordionQualiRPT\" href=\"#collapseInQualiRPT\">\n" +
//                        "            COVID-19 Rapid Antigen Test</a>\n" +
//                        "        </h4>\n" +
//                        "    </div>\n" +
//                        "\n" +
//                        "    <div id=\"collapseInQualiRPT\" class=\"panel-collapse collapse in\">\n" +
//                        "        <div class=\"panel-body\">\n" +
//                        "            <table class=\"table table-bordered table-hover no-border-table recTable tree-table\">\n" +
//                        "                <tbody>\n" +
//                        "                    <tr data-index=\"0\" class=\"cloned addQualitativeScreen\">\n" +
//                        "                        <td width=\"21%\">\n" +
//                        "                            <div class=\"form-group compoundClass\">\n" +
//                        "                                <select name=\"Test\" id=\"Test\" class=\"form-control no-border allcompounds\" data-bv-field=\"Test\">\n" +
//                        "                                    <option value=\"COVID-19 Rapid Antigen Test\" selected=\"selected\">COVID-19 Rapid Antigen Test</option>\n" +
//                        "                                </select>\n" +
//                        "                                <small class=\"help-block\" data-bv-validator=\"notEmpty\" data-bv-for=\"Test\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">Please select molecules</small></div>\n" +
//                        "                            </td>\n" +
//                        "                            <td width=\"15%\">\n" +
//                        "                                <div class=\"form-group compoundClass\">\n" +
//                        "\n" +
//                        "                                    <select name=\"result\" id=\"result\" class=\"form-control no-border\" data-bv-field=\"result\">\n" +
//                        "                                        <option value=\"\" selected=\"selected\">Select One</option>\n" +
//                        "                                        <option value=\"INVALID\">INVALID</option>\n" +
//                        "                                        <option value=\"NEGATIVE\">NEGATIVE</option>\n" +
//                        "                                        <option value=\"POSITIVE\">POSITIVE</option>\n" +
//                        "                                        <option value=\"REJECTED\">REJECTED</option>\n" +
//                        "                                    </select>\n" +
//                        "\n" +
//                        "                                    <small class=\"help-block\" data-bv-validator=\"notEmpty\" data-bv-for=\"result\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">Please select result</small></div>\n" +
//                        "                                </td>\n" +
//                        "\n" +
//                        "                                <td width=\"28%\">\n" +
//                        "\n" +
//                        "                                </td>\n" +
//                        "                                <td width=\"21%\">\n" +
//                        "                                    <div class=\"row\">\n" +
//                        "                                        <div class=\"col-md-2\">\n" +
//                        "                                            <div class=\"form-group compoundClass\">\n" +
//                        "                                                <div class=\"checkbox-inline\">\n" +
//                        "                                                    <label> <input type=\"checkbox\" class=\"commentsOnRep colored-blue \" name=\"showCommentsOnReport\" value=\"false\"> <span class=\"text\"></span>\n" +
//                        "                                                    </label>\n" +
//                        "                                                </div>\n" +
//                        "                                            </div>\n" +
//                        "                                        </div>\n" +
//                        "                                        <div class=\"col-md-9\">\n" +
//                        "                                            <div class=\"form-group compoundClass\">\n" +
//                        "                                                <input type=\"text\" class=\"form-control no-border\" name=\"comments\" value=\"\" data-bv-field=\"comments\">\n" +
//                        "                                                <small class=\"help-block\" data-bv-validator=\"stringLength\" data-bv-for=\"comments\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">allowed max 100 characters</small></div>\n" +
//                        "\n" +
//                        "                                            </div>\n" +
//                        "                                        </div>\n" +
//                        "                                    </td>\n" +
//                        "                                    <td width=\"15%\">\n" +
//                        "\n" +
//                        "                                        <div class=\"form-group compoundClass\">\n" +
//                        "                                            <select name=\"testPriority\" class=\"form-control no-border\" id=\"testPriority\">\n" +
//                        "                                                <option value=\"\">Select One</option>\n" +
//                        "                                                <option value=\"HIGH\">High</option>\n" +
//                        "                                                <option value=\"LOW\">Low</option>\n" +
//                        "                                            </select>\n" +
//                        "                                        </div>\n" +
//                        "\n" +
//                        "                                    </td>\n" +
//                        "\n" +
//                        "                                    <td width=\"20\">\n" +
//                        "                                        <button type=\"button\" class=\"btn btn-primary btn-xs verifyMoleCompounds\" id=\"verifyBtn\">\n" +
//                        "                                            Verify\n" +
//                        "                                        </button>\n" +
//                        "                                    </td>\n" +
//                        "\n" +
//                        "                                    <td width=\"12\">\n" +
//                        "                                        <button type=\"button\" class=\"btn btn-danger btn-xs removeMoleQualiButton\" id=\"removemoleQualiCompounds0\">\n" +
//                        "                                            <i class=\"fa fa-minus\"></i>\n" +
//                        "                                        </button>\n" +
//                        "\n" +
//                        "                                    </td>\n" +
//                        "\n" +
//                        "\n" +
//                        "                                </tr>\n" +
//                        "\n" +
//                        "                            </tbody>\n" +
//                        "                        </table>\n" +
//                        "                    </div>\n" +
//                        "                </div>\n" +
//                        "            </div>\n" +
//                        "        </div>\n" +
//                        "    </td>\n" +
//                        "</tr>");
//            } else {
//                TestBuffer.append("<tr class=\"profile\">\n" +
//                        "<td colspan=\"8\">\n" +
//                        "<div class=\"panel-group\" id=\"accordionQuali" + Test.replaceAll(" ", "") + "\">\n" +
//                        "<div class=\"panel panel-info\">\n" +
//                        "\n" +
//                        "    <div class=\"panel-heading\">\n" +
//                        "        <h4 class=\"panel-title\">\n" +
//                        "            <a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent=\"#accordionQuali" + Test.replaceAll(" ", "") + "\" href=\"#collapseInQuali" + Test.replaceAll(" ", "") + "\">\n" +
//                        "            " + Test + "</a>\n" +
//                        "        </h4>\n" +
//                        "    </div>\n" +
//                        "\n" +
//                        "    <div id=\"collapseInQuali" + Test.replaceAll(" ", "") + "\" class=\"panel-collapse collapse in\">\n" +
//                        "        <div class=\"panel-body\">\n" +
//                        "            <table class=\"table table-bordered table-hover no-border-table recTable tree-table\">\n" +
//                        "                <tbody>\n" +
//                        "                    <tr data-index=\"0\" class=\"cloned addQualitativeScreen\">\n" +
//                        "                        <td width=\"21%\">\n" +
//                        "                            <div class=\"form-group compoundClass\">\n" +
//                        "                                <select name=\"Test\" id=\"Test\" class=\"form-control no-border allcompounds\" data-bv-field=\"Test\">\n" +
//                        "                                    <option value=" + Test + " selected=\"selected\">" + Test + "</option>\n" +
//                        "                                </select>\n" +
//                        "                                <small class=\"help-block\" data-bv-validator=\"notEmpty\" data-bv-for=\"Test\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">Please select molecules</small></div>\n" +
//                        "                            </td>\n" +
//                        "                            <td width=\"15%\">\n" +
//                        "                                <div class=\"form-group compoundClass\">\n" +
//                        "\n" +
//                        "                                    <select name=\"result\" id=\"result\" class=\"form-control no-border\" data-bv-field=\"result\">\n" +
//                        "                                        <option value=\"\" selected=\"selected\">Select One</option>\n" +
//                        "                                        <option value=\"INVALID\">INVALID</option>\n" +
//                        "                                        <option value=\"NEGATIVE\">NEGATIVE</option>\n" +
//                        "                                        <option value=\"POSITIVE\">POSITIVE</option>\n" +
//                        "                                        <option value=\"REJECTED\">REJECTED</option>\n" +
//                        "                                    </select>\n" +
//                        "\n" +
//                        "                                    <small class=\"help-block\" data-bv-validator=\"notEmpty\" data-bv-for=\"result\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">Please select result</small></div>\n" +
//                        "                                </td>\n" +
//                        "\n" +
//                        "                                <td width=\"28%\">\n" +
//                        "\n" +
//                        "                                </td>\n" +
//                        "                                <td width=\"21%\">\n" +
//                        "                                    <div class=\"row\">\n" +
//                        "                                        <div class=\"col-md-2\">\n" +
//                        "                                            <div class=\"form-group compoundClass\">\n" +
//                        "                                                <div class=\"checkbox-inline\">\n" +
//                        "                                                    <label> <input type=\"checkbox\" class=\"commentsOnRep colored-blue \" name=\"showCommentsOnReport\" value=\"false\"> <span class=\"text\"></span>\n" +
//                        "                                                    </label>\n" +
//                        "                                                </div>\n" +
//                        "                                            </div>\n" +
//                        "                                        </div>\n" +
//                        "                                        <div class=\"col-md-9\">\n" +
//                        "                                            <div class=\"form-group compoundClass\">\n" +
//                        "                                                <input type=\"text\" class=\"form-control no-border\" name=\"comments\" value=\"\" data-bv-field=\"comments\">\n" +
//                        "                                                <small class=\"help-block\" data-bv-validator=\"stringLength\" data-bv-for=\"comments\" data-bv-result=\"NOT_VALIDATED\" style=\"display: none;\">allowed max 100 characters</small></div>\n" +
//                        "\n" +
//                        "                                            </div>\n" +
//                        "                                        </div>\n" +
//                        "                                    </td>\n" +
//                        "                                    <td width=\"15%\">\n" +
//                        "\n" +
//                        "                                        <div class=\"form-group compoundClass\">\n" +
//                        "                                            <select name=\"testPriority\" class=\"form-control no-border\" id=\"testPriority\">\n" +
//                        "                                                <option value=\"\">Select One</option>\n" +
//                        "                                                <option value=\"HIGH\">High</option>\n" +
//                        "                                                <option value=\"LOW\">Low</option>\n" +
//                        "                                            </select>\n" +
//                        "                                        </div>\n" +
//                        "\n" +
//                        "                                    </td>\n" +
//                        "\n" +
//                        "                                    <td width=\"20\">\n" +
//                        "                                        <button type=\"button\" class=\"btn btn-primary btn-xs verifyMoleCompounds\" id=\"verifyBtn\">\n" +
//                        "                                            Verify\n" +
//                        "                                        </button>\n" +
//                        "                                    </td>\n" +
//                        "\n" +
//                        "                                    <td width=\"12\">\n" +
//                        "                                        <button type=\"button\" class=\"btn btn-danger btn-xs removeMoleQualiButton\" id=\"removemoleQualiCompounds0\">\n" +
//                        "                                            <i class=\"fa fa-minus\"></i>\n" +
//                        "                                        </button>\n" +
//                        "\n" +
//                        "                                    </td>\n" +
//                        "\n" +
//                        "\n" +
//                        "                                </tr>\n" +
//                        "\n" +
//                        "                            </tbody>\n" +
//                        "                        </table>\n" +
//                        "                    </div>\n" +
//                        "                </div>\n" +
//                        "            </div>\n" +
//                        "        </div>\n" +
//                        "    </td>\n" +
//                        "</tr>");
//            }


//            Query = "Select MAX(VisitNumber), max(Id) from " + Database + ".PatientVisit where PatientRegId = " + ID;
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                VisitNumber = rset.getString(1).trim();
//                VisitId = rset.getString(2).trim();
//            }
//            rset.close();
//            stmt.close();
//
//            VisitNumber = "VN-" + MRN + "-" + VisitNumber;

//            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select IFNULL(WorkersCompPolicyChk,0), IFNULL(MotorVehicleAccidentChk,0),IFNULL(HealthInsuranceChk,0) from " + Database + ".PatientReg_Details where PatientRegId = " + ID;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    WorkerCompPolicyChk = rset.getString(1);
//                    MotorVehicleAccidentChk = rset.getString(2);
//                    HealthInsuranceChk = rset.getString(3);
//                }
//                rset.close();
//                stmt.close();
//
//                if (!HealthInsuranceChk.equals("0")) {
//                    InsuredStatus = "Insured";
////                    Query = "Select IFNULL(HIPrimaryInsurance,'-'), IFNULL(HISubscriberGroupNo,''), IFNULL(HISubscriberPolicyNo,'') " +
////                            " from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = " + ID;
////                    stmt = conn.createStatement();
////                    rset = stmt.executeQuery(Query);
////                    if (rset.next()) {
////                        PrimaryInsurance = rset.getString(1);
////                        GroupNo = rset.getString(2);
////                        MemId = rset.getString(3);
////                    }
////                    rset.close();
////                    stmt.close();
//                } else {
//                    InsuredStatus = "Self Pay";
//                }
//
//            } else {
//                if (SelfPayChk == 1) {
//                    InsuredStatus = "Insured";
//                    Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0)" +
//                            " from " + Database + ".InsuranceInfo where PatientRegId = " + ID;
//                    stmt = conn.createStatement();
//                    rset = stmt.executeQuery(Query);
//                    if (rset.next()) {
//                        WorkerCompPolicyChk = rset.getString(1);
//                        MotorVehicleAccidentChk = rset.getString(2);
//                    }
//                    rset.close();
//                    stmt.close();
//                } else {
//                    InsuredStatus = "Self Pay";
//                }
//
//            }

//            if (WorkerCompPolicyChk.equals("0")) {
//                WorkerCompPolicyChk = "NO";
//            } else {
//                WorkerCompPolicyChk = "YES";
//            }
//
//            if (MotorVehicleAccidentChk.equals("0")) {
//                MotorVehicleAccidentChk = "NO";
//            } else {
//                MotorVehicleAccidentChk = "YES";
//            }

//            Query = "Select Id, CONCAT(IFNULL(DoctorsLastName,''),', ',IFNULL(DoctorsFirstName,'')) from " + Database + ".DoctorsList where Status = 1 order by DoctorsLastName";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            DoctorsBuffer.append("<option value=''>Select Doctor</option>");
//            while (rset.next()) {
//                if (DoctorsId.equals(rset.getString(1).trim())) {
//                    DoctorsBuffer.append("<option value='" + rset.getString(1) + "' selected>" + rset.getString(2) + "</option>");
//                } else {
//                    DoctorsBuffer.append("<option value='" + rset.getString(1) + "'>" + rset.getString(2) + "</option>");
//                }
//            }
//            rset.close();
//            stmt.close();


            Query = " Select IFNULL(MRN,''), IFNULL(Notes,''), IFNULL(DATE_FORMAT(CreatedDate,'%m/%d/%Y %T'),''), IFNULL(CreatedBy,''), Id  " +
                    " from " + Database + ".Notes where PatientRegId = " + ID + " and Status = 0 order by Id desc ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//                out.println(Query);
            while (rset.next()) {
//                NotesList.append("<tr>\n");
//                NotesList.append("<td align=left>"+rset.getString(2)+"</td>\n");
////                NotesList.append("<td align=left>" + rset.getString(2) + "</td>\n");
////                NotesList.append("<td align=left>" + rset.getString(4) + "</td>\n");
////                NotesList.append("<td align=left>" + rset.getString(3) + "</td>\n");
//                NotesList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteNote("+rset.getInt(5)+")\"></i></td>\n");
//                NotesList.append("</tr>\n");
                NotesList.append("<div class=\"box\" >\n" +
                        "\t\t\t\t  <div class=\"box-header\" style='height: 50%;'>\n" +
                        "\t\t\t\t\t<h5 class=\"box-title\" style='margin-right: 10%;'>" + rset.getString(2) + "" +
                        "<br><sub>Added by <u>" + rset.getString(4) + "</u> at <u>" + rset.getString(3) + "</u></sub></h5>\n" +
                        "\t\t\t\t\t<div class=\"box-controls pull-right\">\n" +

                        "\t\t\t\t\t  <button class=\"btn btn-xs btn-info\" onclick=\"DeleteNote(" + rset.getInt(5) + ")\"><i class=\"fa fa-trash\"></i></button>\n" +
                        "\t\t\t\t\t</div>                \n" +
                        "\t\t\t\t  </div>\t\t\t\t  \n" +
                        "\t\t\t\t</div>");
                SNo++;
                Notescount++;
            }
            rset.close();
            stmt.close();


            Query = " Select IFNULL(MRN,''), IFNULL(Alerts,''), IFNULL(DATE_FORMAT(CreatedDate,'%m/%d/%Y %T'),''), IFNULL(CreatedBy,''), Id  " +
                    " from " + Database + ".Alerts where PatientRegId = " + ID + " and Status = 0 order by Id desc ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            while (rset.next()) {
                AlertList.append("<div class=\"box\" >\n" +
                        "\t\t\t\t  <div class=\"box-header\" style='height: 50%;'>\n" +
                        "\t\t\t\t\t<h5 class=\"box-title\" style='margin-right: 10%;'>" + rset.getString(2) + "" +
                        "<br><sub>Added by <u>" + rset.getString(4) + "</u> at <u>" + rset.getString(3) + "</u></sub></h5>\n" +
                        "\t\t\t\t\t<div class=\"box-controls pull-right\">\n" +
                        "\t\t\t\t\t  <button class=\"btn btn-xs btn-info\" onclick=\"DeleteAlert(" + rset.getInt(5) + ")\"><i class=\"fa fa-trash\"></i></button>\n" +
                        "\t\t\t\t\t</div>                \n" +
                        "\t\t\t\t  </div>\t\t\t\t  \n" +
                        "\t\t\t\t</div>");

                AlertListModal.append("<div class=\"box\" >\n" +
                        "\t\t\t\t  <div class=\"box-header\" style='height: 50%;'>\n" +
                        "\t\t\t\t\t<h5 class=\"box-title\" style='margin-right: 10%;'>" + rset.getString(2) + "" +
                        "<br><sub>Added by <u>" + rset.getString(4) + "</u> at <u>" + rset.getString(3) + "</u></sub></h5>\n" +
                        "\t\t\t\t\t<div class=\"box-controls pull-right\">\n" +
                        "\t\t\t\t\t</div>                \n" +
                        "\t\t\t\t  </div>\t\t\t\t  \n" +
                        "\t\t\t\t</div>");
                Alertscount++;
            }
            rset.close();
            stmt.close();

            Query = "Select Id, Location from roverlab.Locations";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            locationList.append("<option value='' selected disabled>Select Location</option>");
            while (rset.next()) {
                if (TestingLocation.equals(rset.getString(1))) {
                    locationList.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(2) + "</option>");
                    continue;
                }
                locationList.append("<option value=" + rset.getString(1) + "  >" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            System.out.println("StageIdx " + StageIdx);
            Query = "Select Id, Status from roverlab.ListofStages";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            stageList.append("<option value=\"\" selected>None </option>");
            while (rset.next()) {
                if (StageIdx == rset.getInt(1)) {
                    stageList.append("<option value=\"" + rset.getInt(1) + "\" selected>" + rset.getString(2) + " </option>");
                } else
                    stageList.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();


            Query = "Select Id, Status from roverlab.ListofStatus";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            statusList.append("<option value=\"\" selected>None </option>");
            while (rset.next()) {
                if (Status == rset.getInt(1))
                    statusList.append("<option value=\"" + rset.getInt(1) + "\"selected>" + rset.getString(2) + " </option>");
                else
                    statusList.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();


            PreparedStatement ps = conn.prepareStatement("Select Paid FROM " + Database + ".InvoiceMaster WHERE OrderID=?");
            ps.setString(1, OrderId);
            System.out.println("Query ->> " + ps.toString());
            rset = ps.executeQuery();
            while (rset.next()) {
                if (rset.getInt(1) == 1) {
                    paid = "( PAID )";
                }
            }
            rset.close();
            ps.close();

            String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            int year = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = 1; i <= month.length; i++) {
                Month.append("<option value=" + i + ">" + month[i - 1] + "</option>");
            }
            for (int i = 1; i <= 31; i++) {
                Day.append("<option value=" + i + ">" + i + "</option>");
            }
            for (int i = 1901; i <= year; i++) {
                if (i == year) {
                    Year.append("<option value=" + i + " selected>" + i + "</option>");
                } else {
                    Year.append("<option value=" + i + ">" + i + "</option>");
                }
            }
            for (int i = 1; i <= 23; i++) {
                Hours.append("<option value=" + i + ">" + i + "</option>");
            }
            for (int i = 1; i <= 59; i++) {
                Mins.append("<option value=" + i + ">" + i + "</option>");
            }

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);

            String verifiedUser = "";
            System.out.println("isVerified " + isVerified);
            if (isVerified == 0)
                verifiedUser = "false";
            else
                verifiedUser = "true";

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.SetField("Gender", String.valueOf(Gender));
            Parser.SetField("DOB", String.valueOf(DOB));
            Parser.SetField("PhNumber", String.valueOf(PhNumber));
            Parser.SetField("MRN", String.valueOf(MRN));
            Parser.SetField("VisitNumber", String.valueOf(VisitNumber));
            Parser.SetField("ReasonVisit", String.valueOf(ReasonVisit));
            Parser.SetField("DOS", String.valueOf(DOS));
            Parser.SetField("COVIDStatus", String.valueOf(COVIDStatus));
            Parser.SetField("COVIDStatus", String.valueOf(COVIDStatus));
            Parser.SetField("WorkerCompPolicyChk", String.valueOf(WorkerCompPolicyChk));
            Parser.SetField("MotorVehicleAccidentChk", String.valueOf(MotorVehicleAccidentChk));
            if (InsuredStatus.equals("Self Pay")) {
                if (paid.equals(""))
                    Parser.SetField("selfPaymentBtn", "<button class=\"btn btn-md btn-success bold pull-right\" id=\"selfPayment\" onclick=\"window.open('/md/md.LabPatientRegistration?ActionID=PayNow&i=" + MRN + "&j=i&k=" + OrderId + "','NewFrame')\"><span><i class=\"fa fa-money fa-lg\"></i></span> Make a Payment </button>");
                else
                    Parser.SetField("selfPaymentBtn", "<button class=\"btn btn-md btn-success bold pull-right\" id=\"selfPayment\"  disable><span><i class=\"fa fa-money fa-lg\"></i></span> Make a Payment " + paid + "</button>");

            }
            Parser.SetField("Address", String.valueOf(Address));
            Parser.SetField("City", String.valueOf(City));
            Parser.SetField("State", String.valueOf(State));
            Parser.SetField("County", String.valueOf(County));
            Parser.SetField("ZipCode", String.valueOf(ZipCode));
            Parser.SetField("ID", String.valueOf(ID));
            Parser.SetField("BundleFnName", String.valueOf(BundleFnName));
            Parser.SetField("LabelFnName", String.valueOf(LabelFnName));
            Parser.SetField("DoctorsBuffer", String.valueOf(DoctorsBuffer));
            Parser.SetField("CovidBuffer", String.valueOf(CovidBuffer));
            Parser.SetField("VisitId", String.valueOf(VisitId));
            Parser.SetField("Style", String.valueOf(Style));

            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("RefPhysicianNameBuffer", String.valueOf(RefPhysicianNameBuffer));
            Parser.SetField("PatientStatusBuffer", String.valueOf(PatientStatusBuffer));
            Parser.SetField("PatientCatagoryBuffer", String.valueOf(PatientCatagoryBuffer));
            Parser.SetField("ReasonLeavingBuffer", String.valueOf(ReasonLeavingBuffer));
            Parser.SetField("RefSourceName", String.valueOf(RefSourceName));
            Parser.SetField("COVIDTestDate", String.valueOf(COVIDTestDate));
            Parser.SetField("CovidTestNo", String.valueOf(CovidTestNo));
            Parser.SetField("RefName", String.valueOf(RefName));
            Parser.SetField("AdditionalInfoSelectBuffer", String.valueOf(AdditionalInfoSelectBuffer));

            Parser.SetField("Month", String.valueOf(Month));
            Parser.SetField("Day", String.valueOf(Day));
            Parser.SetField("Year", String.valueOf(Year));
            Parser.SetField("Hours", String.valueOf(Hours));
            Parser.SetField("Mins", String.valueOf(Mins));
            Parser.SetField("NotesList", String.valueOf(NotesList));
            Parser.SetField("Notescount", String.valueOf(Notescount));

            Parser.SetField("AlertList", String.valueOf(AlertList));
            Parser.SetField("AlertListModal", String.valueOf(AlertListModal));
            Parser.SetField("Alertscount", String.valueOf(Alertscount));


            Parser.SetField("TestBuffer", String.valueOf(TestBuffer));
            Parser.SetField("LocationList", locationList.toString());
            Parser.SetField("StageList", stageList.toString());
            Parser.SetField("StatusList", statusList.toString());
            Parser.SetField("OrderID", OrderId);
            Parser.SetField("OrderDate", OrderDate);
            Parser.SetField("UserID", UserId);
            Parser.SetField("verifiedUser", verifiedUser);
            Parser.SetField("email", email);
            Parser.SetField("isVerified", String.valueOf(isVerified));
            Parser.SetField("orderIdx", String.valueOf(orderIdx));


            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("PatientInvoiceMRN", String.valueOf(PatientInvoiceMRN));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/PatientUpdateInfo_ROVERLAB.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientRegRoverLab ** (GetInput^^ " + facilityName + " ^^ Patient Reg Idx --> " + ID + ")", servletContext, e, "PatientRegRoverLab", "GetInput", conn);
            Services.DumException("PatientRegRoverLab", "GetInput", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                helper.SendEmailWithAttachment("Error in PatientRegRoverLab ** (handleRequest -- SqlException)", servletContext, e, "PatientRegRoverLab", "handleRequest", conn);
                Services.DumException("PatientRegRoverLab", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }

    void BundlePrimescopeOLDTabish8March2022(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
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
        String filepath = null;
        String filename = null;

        final int ID = Integer.parseInt(request.getParameter("ID").trim());
        try {
            Query = "select date_format(now(),'%Y%m%d%H%i%s'), DATE_FORMAT(now(), '%m/%d/%Y'), DATE_FORMAT(now(), '%T')";
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
                Query = " Select a.FirstName ,a.LastName ,a.MiddleInitial ,a.Gender ,a.PhNumber ,a.Email ," +
                        "a.Address ,a.City ,a.State ,a.County ,a.Ethnicity ,a.Race ,b.TestName ,a.AtTestSite ," +
                        "c.Location ,a.Insured ,a.RespParty ,a.CarrierName ,a.GrpNumber ,a.MemID ," +
                        "a.ExtendedMRN ,a.Status ,a.CreatedDate ,a.EditBy ,a.Edittime ,a.DOB ,a.ZipCode," +
                        " IFNULL(BundleFilePath,''),IFNULL(BundleFileName,'')" +
                        "  From roverlab.PatientReg a " +
                        " INNER JOIN roverlab.ListofTests b ON a.Test = b.Id " +
                        " INNER JOIN roverlab.Locations c ON a.TestingLocation = c.Id " +
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
                    filepath = rset.getString(28);
                    filename = rset.getString(29);
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
            File tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_0_" + ID + "_P.png");
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

                SignImages = com.itextpdf.text.Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_0_" + ID + "_P.png");
                SignImages.scaleAbsolute(80.0f, 30.0f);
                tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_1_" + ID + "_HPC.png");
                exists = tmpDir.exists();
                out.print("exists " + exists);
                if (exists) {
                    SignImages2 = com.itextpdf.text.Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_1_" + ID + "_HPC.png");
                    SignImages2.scaleAbsolute(80.0f, 30.0f);
                }
                //outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + UID + "_.pdf";
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

            if (filepath.compareTo("") != 0 && filename.compareTo("") != 0) {
                final File pdfFile = new File(filepath);
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition", "inline; filename=" + filename);
                response.setContentLength((int) pdfFile.length());
                final FileInputStream fileInputStream = new FileInputStream(pdfFile);
                final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
                int bytes;
                while ((bytes = fileInputStream.read()) != -1) {
                    responseOutputStream.write(bytes);
                }
                return;
            }


            inputFilePath += "/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/COVID_REG.pdf";

            FirstNameNoSpaces = FirstName.replaceAll("\\s+", "");
            final String Filename = FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + Filename;
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


                }
            }
//            out.println("Printing Done");
            pdfStamper.close();


            PreparedStatement ps = conn.prepareStatement("UPDATE " + Database + ".PatientReg SET BundleFilePath=? ,BundleFileName=?  WHERE Id='" + ID + "'");
            ps.setString(1, outputFilePath);
            ps.setString(2, Filename);
            ps.executeUpdate();
            ps.close();


            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + Filename);
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

    private void BundlePrimescope(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
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
        String filepath = null;
        String filename = null;

        String SSN = "";
        String DrivingLicense = "";
        String StateID = "";
        final int ID = Integer.parseInt(request.getParameter("ID").trim());

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
                        " IFNULL(a.BundleFilePath,''),IFNULL(a.BundleFileName,'')," +
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

                    filepath = rset.getString(43);
                    filename = rset.getString(44);

                    SSN = rset.getString(45);
                    DrivingLicense = rset.getString(46);
                    StateID = rset.getString(47);
                }
                rset.close();
                stmt.close();


            } catch (Exception e) {
                out.println("Error In PateintRegROVERLAB SAVEBUNDLE:--" + e.getMessage());
                out.close();
                out.flush();
            }

            String UID = "";
            com.itextpdf.text.Image SignImages = null;
            com.itextpdf.text.Image SignImages2 = null;
            File tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_0_" + ID + "_P.png");
            boolean exists = tmpDir.exists();
            System.out.print("exists " + exists);
            if (exists) {
                Query = "Select UID from " + Database + ".SignRequest where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    UID = rset.getString(1);
                }
                rset.close();
                stmt.close();

                SignImages = com.itextpdf.text.Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_0_" + ID + "_P.png");
                SignImages.scaleAbsolute(80.0f, 30.0f);
                tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_1_" + ID + "_HPC.png");
                exists = tmpDir.exists();
                System.out.print("exists " + exists);
                if (exists) {
                    SignImages2 = com.itextpdf.text.Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_1_" + ID + "_HPC.png");
                    SignImages2.scaleAbsolute(80.0f, 30.0f);
                }
                //outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + UID + "_.pdf";
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

            if (filepath.compareTo("") != 0 && filename.compareTo("") != 0) {
                final File pdfFile = new File(filepath);
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition", "inline; filename=" + filename);
                response.setContentLength((int) pdfFile.length());
                final FileInputStream fileInputStream = new FileInputStream(pdfFile);
                final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
                int bytes;
                while ((bytes = fileInputStream.read()) != -1) {
                    responseOutputStream.write(bytes);
                }
                return;
            }


//            inputFilePath += "/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/COVID_REG.pdf";
            inputFilePath += "/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/COVID_REGNEW.pdf";

            FirstNameNoSpaces = FirstName.replaceAll("\\s+", "");
            final String Filename = FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf";
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);


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

//                    pdfContentByte.beginText();
//                    pdfContentByte.setTextMatrix(500, 236); // set x and y co-ordinates
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(300, 228);
                        pdfContentByte.addImage(SignImages);
                    }
//                    pdfContentByte.endText();
/*                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(300, 236); // set x and y co-ordinates
                    pdfContentByte.showText("Tabish Hafeez"); // Signature
                    pdfContentByte.endText();*/

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

            pdfStamper.close();


            PreparedStatement ps = conn.prepareStatement("UPDATE " + Database + ".PatientReg SET BundleFilePath=? ,BundleFileName=?  WHERE Id='" + ID + "'");
            ps.setString(1, outputFilePath);
            ps.setString(2, Filename);
            ps.executeUpdate();
            ps.close();


            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + Filename);
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }

        } catch (Exception e) {
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
            ResultSet rset = null;
            String Query = "";
            Statement stmt = null;
            int MRN = 0;
            String PatientName = "";
            String AUTHID = "";
            String SendType = "0";
            InetAddress ip = InetAddress.getLocalHost();
            long unixTime = System.currentTimeMillis() / 1000L;
            UUID uuid = UUID.randomUUID();
            String pageCount = request.getParameter("pageCount");
            String outputFilePath = request.getParameter("outputFilePath");
            String FileName = request.getParameter("FileName");
            int PatientRegId = Integer.parseInt(request.getParameter("PatientRegId"));

            int found = 0;
            int isExist = 0;

            try {
                Query = "SELECT MRN FROM roverlab.PatientReg WHERE Id = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    MRN = rset.getInt(1);
                rset.close();
                stmt.close();
            } catch (Exception Ex) {
                Services.DumException("PatientRegRoverLab - SignPDF", "PatientRegIdx", request, Ex, getServletContext());
            }
            try {
                Query = "Select Count(*) from roverlab.SignRequest where PatientRegId = " + PatientRegId + " AND isSign = 1";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    found = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception Ex) {

                Services.DumException("PatientRegRoverLab ", "signPDFCheck", request, Ex, getServletContext());
            }
            try {
                Query = "Select Count(*) from roverlab.SignRequest where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    isExist = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception Ex) {
                Services.DumException("PatientRegRoverLab ", "signPDFCheck", request, Ex, getServletContext());
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
            List<String> imagelist = new ArrayList(values);

            for (int i = 0; i < imagelist.size(); ++i) {
                Style.append(".desktop-image" + (i + 1) + " {\n\tbackground-image: url(" + (String) imagelist.get(i) + ");\n\tbackground-size: cover;\n\tbackground-position: center;\n\twidth: 600px;\n\theight: 777px;\n\tmargin: 0 auto;\n\tborder: 5px solid #0f0f10;\n\tposition: relative;\n}\n\n.desktop-image" + (i + 1) + "> #Sign" + (i + 1) + "{\n\t\n\tposition: relative;\n\ttop: 54%;\n\tleft: 17%;\n\t/*transform: translate(50%, -50%);*/\n}");
                ulTag.append("<div id=\"page" + (i + 1) + "\" class=\"images\"><div  class=\"desktop-image" + (i + 1) + "\"><button type=\"button\" class=\"btn btn-primary\" id=\"Sign" + (i + 1) + "\"  onclick=\"signhere(" + (i + 1) + ", this.id);\" class=\"mobile-image-stella\">Patients Sign</button>\n<img id=\"canvasimg" + (i + 1) + "\" style=\"display:none;background-color:rgba(255,255,255,0.6);\">\n\n</div>\n</div>\n");
            }

//          Insert Data in the SignRequest Table here.
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO roverlab.SignRequest (MRN,Status,isSign,IP,CreatedBy,CreatedDate," +
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
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/SigningBundleRoverLab.html");

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Sign Print ^^ ** (SignPDF)", servletContext, e, "SignPrint", "SignPDF", conn);
            Services.DumException("SignPDF", "SignPDF", request, e, getServletContext());
        }


    }

    private void UpdateTest(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) throws FileNotFoundException {
        String O_ID = request.getParameter("O_ID").trim();
        String T_ID = request.getParameter("T_ID").trim();
        String result = request.getParameter("result").trim();
        String comments = request.getParameter("comments".trim());
        String testPriority = request.getParameter("testPriority".trim());
        String sampleNum = request.getParameter("sampleNum".trim());
        String userid = request.getParameter("userid".trim());
        ResultSet rset = null;
        String orderNumber = null;
        String timeStamp = null;
        String PatRegIdx = null;
        String OrderDate = null;
        String UpdatedAt = null;
        String UpdatedBy = null;
        String PatFirstName = null;
        String PatLastName = null;
        String UpdateByFirstName = null;
        String UpdateByLastName = null;
        String PatDOB = null;
        String Filepath = null;
        int testIdx = 0;

        try {
/*            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE " + Database + ".Tests SET Narration='" + comments + "' , " +
                            "TestStatus='" + result + "' , Priority='" + testPriority + "' , " +
                            "UpdateDate = NOW() , UpdatedBy = '" + userid + "' , " +
                            "SampleNumber='" + sampleNum + "'" +
                            " WHERE OrderId='" + O_ID + "' AND Id='" + T_ID + "'");*/

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE " + Database + ".Tests SET Narration='" + comments + "' , " +
                            "Priority='" + testPriority + "' , " +
                            "UpdateDate = NOW() , UpdatedBy = '" + userid + "' , " +
                            "SampleNumber='" + sampleNum + "' " +
                            " WHERE OrderId='" + O_ID + "' AND Id='" + T_ID + "'");
            ps.executeUpdate();
            ps.close();


            ps = conn.prepareStatement(
                    "SELECT OrderNum,DATE_FORMAT(NOW(),'%Y%m%d%H%m%s'),PatRegIdx,OrderDate " +
                            "FROM " + Database + ".TestOrder WHERE Id=?");
            ps.setString(1, O_ID);
            rset = ps.executeQuery();
            if (rset.next()) {
                orderNumber = rset.getString(1);
                timeStamp = rset.getString(2);
                PatRegIdx = rset.getString(3);
                OrderDate = rset.getString(4);
            }
            ps.close();
            rset.close();

            ps = conn.prepareStatement("" +
                    "SELECT UpdateDate,UpdatedBy,Filepath,TestIdx FROM " + Database + ".Tests " +
                    "WHERE OrderId=? AND Id=?");
            ps.setString(1, O_ID);
            ps.setString(2, T_ID);
            rset = ps.executeQuery();
            if (rset.next()) {
                UpdatedAt = rset.getString(1);
                UpdatedBy = rset.getString(2);
                Filepath = rset.getString(3);
                testIdx = rset.getInt(4);
            }
            ps.close();
            rset.close();

            //If test is Antigen, then we need do the following tasks
            // Antigen has seperate result page
            if (testIdx == 4) {

                PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE " + Database + ".Tests SET Narration='" + comments + "' , " +
                                "Priority='" + testPriority + "' , " +
                                "UpdateDate = NOW() , UpdatedBy = '" + userid + "' , " +
                                "SampleNumber='" + sampleNum + "'," +
                                "TestStatus='" + result + "' " +
                                " WHERE OrderId='" + O_ID + "' AND Id='" + T_ID + "'");
                ps2.executeUpdate();
                ps2.close();

                ps = conn.prepareStatement("SELECT firstname,lastname FROM oe.sysusers WHERE userid=?");
                ps.setString(1, UpdatedBy);
                rset = ps.executeQuery();
                if (rset.next()) {
                    UpdateByFirstName = rset.getString(1);
                    UpdateByLastName = rset.getString(2);
                }
                ps.close();
                rset.close();

                ps = conn.prepareStatement("SELECT\n" +
                        " FirstName, " +
                        " LastName, " +
                        " DOB " +
                        " FROM " +
                        Database + ".PatientReg WHERE Id=?");
                ps.setString(1, PatRegIdx);
                rset = ps.executeQuery();
                if (rset.next()) {
                    PatFirstName = rset.getString(1);
                    PatLastName = rset.getString(2);
                    PatDOB = rset.getString(3);
                }
                ps.close();
                rset.close();

                if (Filepath != null) {
                    File File = new File(Filepath);
                    File.delete();
                }
                String filename = orderNumber + "_" + timeStamp + "_RapidAntigenResult.pdf";
                String inputFilePath = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/roverlab/RapidAntigen.pdf"; // Existing file
                String outputFilePath = "/sftpdrive/AdmissionBundlePdf/roverlab/RapidAntigenResults/" + filename; // New file
                OutputStream fos = new FileOutputStream(new File(outputFilePath));


                PdfReader pdfReader = new PdfReader(inputFilePath);
                PdfStamper pdfStamper3 = new PdfStamper(pdfReader, fos);
                com.itextpdf.text.Image Images = com.itextpdf.text.Image.getInstance("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/roverlab/Green tick.png");
                Images.scaleAbsolute(50.0f, 50.0f);
                // loop on all the PDF pages
                // i is the pdfPageNumber
                for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

                    if (i == 1) {
                        final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(i);

                        pdfContentByte3.beginText();
                        pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte3.setColorFill(BaseColor.BLACK);
                        pdfContentByte3.setTextMatrix(150.0f, 510.0f);
                        pdfContentByte3.showText(PatFirstName);
                        pdfContentByte3.endText();
                        pdfContentByte3.beginText();
                        pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte3.setColorFill(BaseColor.BLACK);
                        pdfContentByte3.setTextMatrix(150.0f, 480.0f);
                        pdfContentByte3.showText(PatLastName);
                        pdfContentByte3.endText();
                        pdfContentByte3.beginText();
                        pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte3.setColorFill(BaseColor.BLACK);
                        pdfContentByte3.setTextMatrix(150.0f, 450.0f);
                        pdfContentByte3.showText(PatDOB.split("-")[1]);
                        pdfContentByte3.endText();
                        pdfContentByte3.beginText();
                        pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte3.setColorFill(BaseColor.BLACK);
                        pdfContentByte3.setTextMatrix(190.0f, 450.0f);
                        pdfContentByte3.showText(PatDOB.split("-")[2]);
                        pdfContentByte3.endText();
                        pdfContentByte3.beginText();
                        pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte3.setColorFill(BaseColor.BLACK);
                        pdfContentByte3.setTextMatrix(230.0f, 450.0f);
                        pdfContentByte3.showText(PatDOB.split("-")[0]);
                        pdfContentByte3.endText();

                        if (result.equals("3")) {
                            Images.setAbsolutePosition(240.0f, 348);
                            pdfContentByte3.addImage(Images);
                        }

                        if (result.equals("2")) {
                            Images.setAbsolutePosition(360.0f, 348);
                            pdfContentByte3.addImage(Images);
                        }

                        pdfContentByte3.beginText();
                        pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte3.setColorFill(BaseColor.BLACK);
                        pdfContentByte3.setTextMatrix(265.0f, 320.0f);
                        pdfContentByte3.showText(OrderDate.split("-")[1]);
                        pdfContentByte3.endText();
                        pdfContentByte3.beginText();
                        pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte3.setColorFill(BaseColor.BLACK);
                        pdfContentByte3.setTextMatrix(300.0f, 320.0f);
                        pdfContentByte3.showText(OrderDate.split("-")[2].split(" ")[0]);
                        pdfContentByte3.endText();
                        pdfContentByte3.beginText();
                        pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte3.setColorFill(BaseColor.BLACK);
                        pdfContentByte3.setTextMatrix(350.0f, 320.0f);
                        pdfContentByte3.showText(OrderDate.split("-")[0] + " " + OrderDate.split("-")[2].split(" ")[1]);
                        pdfContentByte3.endText();

                        pdfContentByte3.beginText();
                        pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte3.setColorFill(BaseColor.BLACK);
                        pdfContentByte3.setTextMatrix(215.0f, 280.0f);
                        pdfContentByte3.showText(UpdatedAt.split("-")[1]);
                        pdfContentByte3.endText();
                        pdfContentByte3.beginText();
                        pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte3.setColorFill(BaseColor.BLACK);
                        pdfContentByte3.setTextMatrix(255.0f, 280.0f);
                        pdfContentByte3.showText(UpdatedAt.split("-")[2].split(" ")[0]);
                        pdfContentByte3.endText();
                        pdfContentByte3.beginText();
                        pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte3.setColorFill(BaseColor.BLACK);
                        pdfContentByte3.setTextMatrix(305.0f, 280.0f);
                        pdfContentByte3.showText(UpdatedAt.split("-")[0] + " " + OrderDate.split("-")[2].split(" ")[1]);
                        pdfContentByte3.endText();


                        pdfContentByte3.beginText();
                        pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte3.setColorFill(BaseColor.BLACK);
                        pdfContentByte3.setTextMatrix(150.0f, 210.0f);
                        pdfContentByte3.showText(UpdateByFirstName);
                        pdfContentByte3.endText();
                        pdfContentByte3.beginText();
                        pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte3.setColorFill(BaseColor.BLACK);
                        pdfContentByte3.setTextMatrix(150.0f, 180.0f);
                        pdfContentByte3.showText(UpdateByLastName);
                        pdfContentByte3.endText();


                    }

                }

                pdfStamper3.close();

                ps = conn.prepareStatement(
                        "UPDATE " + Database + ".TestOrder SET " +
                                "Status='6',StageIdx = 2, UpdatedAt = NOW() , UpdatedBy = '" + userid + "' " +
                                " WHERE Id='" + O_ID + "' AND PatRegIdx='" + PatRegIdx + "'");
                ps.executeUpdate();
                ps.close();

                ps = conn.prepareStatement(
                        "UPDATE " + Database + ".Tests SET " +
                                "filepath='" + outputFilePath + "', " +
                                "filename='" + filename + "' " +
                                " WHERE OrderId='" + O_ID + "' AND Id='" + T_ID + "'");
                ps.executeUpdate();
                ps.close();

            }
            out.println("1");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            System.out.println(str);
            e.getMessage();
            out.println("0");
        }
    }

    private void UpdateOrder(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) throws FileNotFoundException {
        String O_ID = request.getParameter("O_ID").trim();
        String status = request.getParameter("status").trim();
        String stage = request.getParameter("stage".trim());
        String userid = request.getParameter("userid".trim());

        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE " + Database + ".TestOrder SET Status='" + status + "' , StageIdx='" + stage + "' , UpdatedAt = NOW() , UpdatedBy = '" + userid + "'" +
                    " WHERE OrderNum='" + O_ID + "'");
            ps.executeUpdate();
            ps.close();
            out.println("1");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.getMessage();
            out.println("0");
        }
    }

    void DeActivePatient(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String MRN = "";
        String className = getClass().getName();
        int PatientID = Integer.parseInt(request.getParameter("ID").trim());
        try {
            Query = " Update " + Database + ".PatientReg set Status = 1 " +
                    "where ID = '" + PatientID + "'";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
            out.println("1");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientRegRoverLab ** (DeActivePatient -- 01)", servletContext, e, "RegisteredPatients", "DeActivePatient -- 01", conn);
            Services.DumException("DeActivePatient ", "PatientRegRoverLab ", request, e, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "PatientRegRoverLab");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.SetField("Message", "MES#025");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    private void sendResultReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) throws FileNotFoundException {
        String O_ID = request.getParameter("O_ID").trim();
        String T_ID = request.getParameter("T_ID").trim();
        String TestType = request.getParameter("TestType").trim();
        ResultSet rset = null;
        String email = null;
        String filepath = null;
        String filename = null;
        String MRN = null;
        String OrderNum = null;
        String check = "";
        if (TestType.compareTo("4") != 0) {
            check = "AND b.Status IN (6,7)";
        }
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT IFNULL(a.email,''), IFNULL(c.filepath,''),IFNULL(c.filename,''), " +
                            "b.Status,a.MRN,b.OrderNum,c.TestIdx " +
                            "FROM  " + Database + ".PatientReg a" +
                            " LEFT JOIN  " + Database + ".TestOrder b ON a.ID=b.PatRegIdx " +
                            " LEFT JOIN  " + Database + ".Tests c ON b.ID=c.OrderId " +
                            "  where b.id=? AND c.id=? " + check);
            ps.setString(1, O_ID);
            ps.setString(2, T_ID);
            rset = ps.executeQuery();
            if (rset.next()) {
                email = rset.getString(1);
                filepath = rset.getString(2);
                filename = rset.getString(3);
                MRN = rset.getString(5);
                OrderNum = rset.getString(6);
            }
            rset.close();
            ps.close();

            helper.SendEmailWithAttachment_ROVERLAB("Test Report", servletContext, conn, email, filepath, OrderNum, MRN);

            if (TestType.equals("4")) {
                PreparedStatement ps1 = conn.prepareStatement("UPDATE " + Database + ".TestOrder SET email=1 , emailtime = NOW() " +
                        " WHERE Id=?");
                ps1.setString(1, O_ID);
                ps1.executeUpdate();
                ps1.close();

                PreparedStatement ps2 = conn.prepareStatement("INSERT INTO roverlab.EmailSentHistory " +
                        "(MRN, OrderIdx, TestIdx, EmailSentBy, EmailSentDate, Status, CreatedDate) " +
                        " VALUES (?,?,?,?,NOW(),0,NOW() )");
                ps2.setInt(1, Integer.parseInt(MRN));
                ps2.setInt(2, Integer.parseInt(O_ID));
                ps2.setInt(3, Integer.parseInt(T_ID));
                ps2.setString(4, UserId);
                ps2.executeUpdate();
                ps2.close();
            }

            out.println("1");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            System.out.println(str);
            e.getMessage();
            out.println("0");
        }
    }

    public int SendEmailWithReciept_ROVERLAB(final HttpServletRequest request, ServletContext servletContext, Connection conn, String databaseName, PrintWriter out) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        //String Email1 = "tabish.hafeez@fam-llc.com";//change accordingly
        String FilePath = Services.GetEmailLogsPath(servletContext);
        String emailHtmlFilePath = Services.GetEmailFilePath(servletContext);
        String EmailTo = "";
        String MRN = request.getParameter("MRN");
        String Reciept = request.getParameter("Reciept");

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT Email from " + databaseName + ".PatientReg WHERE MRN=?");
            ps.setString(1, MRN);
            rset = ps.executeQuery();
            if (rset.next()) {
                EmailTo = rset.getString(1);
            }
            rset.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String HostName = "";
            String EmailUserId = "";
            String EmailPassword = "";
            String SMTP = "";
            String Port = "";
            String Authentication = "";
            try {
                if (conn == null) {
                    conn = Services.getMysqlConn(servletContext);
                }
                Query = "{CALL SP_GET_CredentialsEmail()}";
                cStmt = conn != null ? conn.prepareCall(Query) : (CallableStatement) Services.getMysqlConn(servletContext);
                rset = cStmt != null ? cStmt.executeQuery() : null;
                if (rset != null && rset.next()) {
                    HostName = rset.getString(1);
                    EmailUserId = rset.getString(2);
                    EmailPassword = rset.getString(3);
                    SMTP = rset.getString(4);
                    Port = rset.getString(5);
                    Authentication = rset.getString(6);
                }
                if (rset != null) {
                    rset.close();
                }
                if (cStmt != null) {
                    cStmt.close();
                }
            } catch (Exception Ex) {
                Ex.printStackTrace();
            }
            System.out.println("*****************************************");
            System.out.println("HostName " + HostName);
            System.out.println("EmailUserId " + EmailUserId);
            System.out.println("EmailPassword " + EmailPassword);
            System.out.println("SMTP " + SMTP);
            System.out.println("Port " + Port);
            System.out.println("Authentication " + Authentication);


            System.out.println("*****************************************");
            //1) get the session object

            Properties props = new Properties();
            props.put("mail.transport.protocol", SMTP);
            props.put("mail.smtp.host", HostName);
            props.put("mail.smtp.port", Port);
            props.put("mail.smtp.auth", Authentication);
            final String user = EmailUserId;//change accordingly
            final String password = EmailPassword;//change accordingly
            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user, password);
                        }
                    });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("PrimeScope Diagnostic <no-reply@rovermd.com>"));
            // Set To: header field of the header.
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress((EmailTo == null ? "tabish.hafeez@fam-llc.com" : EmailTo.equals("") ? "tabish.hafeez@fam-llc.com" : EmailTo)));
            EmailTo = (EmailTo == null ? "tabish.hafeez@fam-llc.com" : EmailTo.equals("") ? "tabish.hafeez@fam-llc.com" : EmailTo);
            System.out.println("EMAIL ADDRESS FROM UH --> " + EmailTo);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(EmailTo));
            // Set Subject: header field
            message.setSubject("PrimeScope Diagnostics Invoice");
            //Setting the email priority high
            message.addHeader("X-Priority", "1");
            message.setContent(Reciept, "text/html");

            Transport t = session.getTransport("smtp");
            t.connect();


            Transport.send(message);

            System.out.println("Email Sent..");
            out.println("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    void PatientHistory(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, String locationArray) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String MRN = request.getParameter("MRN");
        String OrderId = request.getParameter("OrderId");
        String ClientIndex = request.getParameter("ClientId");
        StringBuffer PatientHistoryList = new StringBuffer();
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();

        System.out.println("Here ! in Patient History");


        try {


            Query = " SELECT IFNULL(a.OrderNum,''),DATE_FORMAT(a.OrderDate,'%m/%d/%Y'),IFNULL(a.OrderBy,''), a.Status,a.email,a.sms,a.StageIdx, d.TestStatus,  " +
                    " CASE " +
                    " WHEN a.Status = 1 THEN 'PENDING' " +
                    " WHEN a.Status = 2 THEN 'ON-ARIVAL' " +
                    " WHEN a.Status = 3 THEN 'ACCEPTED' " +
                    " WHEN a.Status = 4 THEN 'REJECTED' " +
                    " WHEN a.Status = 5 THEN 'PROCESSING AT LAB' " +
                    " WHEN a.Status = 6 THEN 'FINALIZING' " +
                    " WHEN a.Status = 7 THEN 'ANNOUNCED' " +
                    " WHEN a.Status = 8 THEN 'REFUSED' " +
                    " WHEN a.Status = 9 THEN 'READY FOR BILL' " +
                    " WHEN a.Status = 10 THEN 'BILLED' " +
                    " WHEN a.Status = 11 THEN 'REVIEW' " +
                    " ELSE 'N/A' END,  " +
                    " CASE " +
                    " WHEN a.StageIdx = 0 THEN 'REGISTERED' " +
                    " WHEN a.StageIdx = 1 THEN 'DISPATCHED' " +
                    " WHEN a.StageIdx = 2 THEN 'RECEIVED AT LAB' " +
                    " WHEN a.StageIdx = 3 THEN 'SAMPLE NOT RECEIVED' " +
                    " ELSE 'N/A' END,  " +
                    " CASE " +
                    " WHEN d.TestStatus = 1 THEN 'BROKEN' " +
                    " WHEN d.TestStatus = 2 THEN 'NEGATIVE' " +
                    " WHEN d.TestStatus = 3 THEN 'POSITIVE' " +
                    " WHEN d.TestStatus = 4 THEN 'REJECTED' " +
                    " WHEN d.TestStatus = 5 THEN 'LOST' " +
                    " WHEN d.TestStatus = 6 THEN 'UNCONCLUSIVE' " +
                    " ELSE 'No Result' END,  " +
                    " CASE WHEN a.email=0 THEN 'Email Not Sent' WHEN a.email=1 THEN 'Email Sent' Else '' END, IFNULL(DATE_FORMAT(a.emailtime,'%m/%d/%Y %H:%i:%s'),'00/00/0000'), " +
                    " CASE WHEN a.sms=0 THEN 'SMS Not Sent' WHEN a.sms=1 THEN 'Email Sent' Else '' END, IFNULL(DATE_FORMAT(a.smstime,'%m/%d/%Y %H:%i:%s'),'00/00/0000') " +
                    " FROM roverlab.TestOrder a " +
                    " INNER JOIN roverlab.PatientReg b ON b.ID = a.PatRegIdx " +
                    " INNER JOIN roverlab.Tests d ON a.Id = d.OrderId " +
                    " WHERE b.MRN = " + MRN + "   " +
                    " ORDER BY a.OrderDate DESC";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            System.out.println("Query excecution--->   " + Query);
            while (rset.next()) {
                PatientHistoryList.append("<tr>\n");
                PatientHistoryList.append("<td align=left>" + rset.getString(1) + "</td>\n");  //order name
                PatientHistoryList.append("<td align=left>" + rset.getString(2) + "</td>\n"); // order date
                PatientHistoryList.append("<td align=left>" + rset.getString(3) + "</td>\n"); // order by
                if (rset.getInt(4) == 0) {
                    PatientHistoryList.append("<td align=left><span class=\"badge badge-danger\">" + rset.getString(9) + "</span></td>\n"); // sttus
                } else {
                    PatientHistoryList.append("<td align=left><span class=\"badge badge-success\">" + rset.getString(9) + "</span></td>\n"); // status
                }

                PatientHistoryList.append("<td align=left><span class=\"badge badge-success\">" + rset.getString(10) + "</span></td>\n"); // stage


                if (rset.getInt(8) == 1) {

                    PatientHistoryList.append("<td align=left><span class=\"badge badge-warning\">" + rset.getString(11) + "</span></td>\n");//Result
                } else if (rset.getInt(8) == 2) {

                    PatientHistoryList.append("<td align=left><span class=\"badge badge-success\">" + rset.getString(11) + "</span></td>\n");//Result
                } else if (rset.getInt(8) == 3) {

                    PatientHistoryList.append("<td align=left><span class=\"badge badge-danger\">" + rset.getString(11) + "</span></td>\n");//Result
                } else if (rset.getInt(8) == 4) {

                    PatientHistoryList.append("<td align=left><span class=\"badge badge-info\">" + rset.getString(11) + "</span></td>\n");//Result
                } else if (rset.getInt(8) == 5) {

                    PatientHistoryList.append("<td align=left><span class=\"badge badge-primary\">" + rset.getString(11) + "</span></td>\n");//Result
                } else if (rset.getInt(8) == 6) {

                    PatientHistoryList.append("<td align=left><span class=\"badge badge-dark\">" + rset.getString(11) + "</span></td>\n");//Result
                } else {

                    PatientHistoryList.append("<td align=left><span class=\"badge badge-light\">" + rset.getString(11) + "</span></td>\n");//Result

                }


                if (rset.getString(5).equals("0")) {
                    PatientHistoryList.append("<td align=left><span class=\"badge badge-warning\">" + rset.getString(12) + "</span></td>\n");//Result
                } else {
                    PatientHistoryList.append("<td align=left><span class=\"badge badge-success\">" + rset.getString(12) + "</span></td>\n");//Result
                }
                PatientHistoryList.append("<td align=left>" + rset.getString(13) + "</td>\n"); // email time

                if (rset.getString(6).equals("0")) {
                    PatientHistoryList.append("<td align=left><span class=\"badge badge-warning\">" + rset.getString(14) + "</span></td>\n");//Result
                } else {
                    PatientHistoryList.append("<td align=left><span class=\"badge badge-success\">" + rset.getString(14) + "</span></td>\n");//Result
                }
                PatientHistoryList.append("<td align=left>" + rset.getString(15) + "</td>\n"); // sms time
                PatientHistoryList.append("</tr>\n");
            }
            System.out.println("Query--->   " + Query);
            rset.close();
            stmt.close();

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientHistoryList", String.valueOf(PatientHistoryList));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/PatientHistory_ROVERLAB.html");
        } catch (Exception e) {
            System.out.println("in the catch exception of GetReport Function ");
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println(e);
//        helper.SendEmailWithAttachment("Error in PatientRegRoverLab ** (ShowReport)", servletContext, e, "PatientRegRoverLab", "ShowReport", conn);
//        Services.DumException("ShowReport", "PatientRegRoverLab ", request, e);
//        Parsehtm Parser = new Parsehtm(request);
//        Parser.SetField("FormName", "ManagementDashboard");
//        Parser.SetField("ActionID", "GetInput");
//        Parser.SetField("Message", "MES#002");
//        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
//


        }
    }
}