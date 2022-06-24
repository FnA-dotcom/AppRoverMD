package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;

@SuppressWarnings("Duplicates")
public class LabLabels extends HttpServlet {

    public static int getAge(LocalDate dob) {
        LocalDate curDate = LocalDate.now();
        return Period.between(dob, curDate).getYears();
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
        boolean validSession = false;
        int FacilityIndex = 0;
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
        Connection conn = null;
        try {
            Parsehtm Parser;
            session = request.getSession(false);
            validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            DirectoryName = session.getAttribute("DirectoryName").toString();
            if (UserId.equals("")) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            switch (ActionID) {
                case "PrintLabel":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option FROM LAB *** ", "Click on Print Label Option from View Patients Tab", FacilityIndex);
                    this.PrintLabel(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex);
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
            if (conn == null) {
                conn = Services.getMysqlConn(context);
            }
            helper.SendEmailWithAttachment("Error in LabLabels ** (handleRequest)", context, e, "LabLabels", "handleRequest", conn);
            Services.DumException("LabLabels", "Handle Request", request, e, getServletContext());
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
                helper.SendEmailWithAttachment("Error in PatientReg ** (handleRequest -- SqlException)", context, e, "PatientReg", "handleRequest", conn);
                Services.DumException("PatientReg", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
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
            if (rset.next()) {
                ClientName = rset.getString(1);
            }
            rset.close();
            stmt.close();

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
        }
    }

}
