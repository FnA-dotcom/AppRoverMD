// 
// Decompiled by Procyon v0.5.36
// 

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;

import com.itextpdf.text.Image;

@SuppressWarnings("Duplicates")
public class PrintLabel426JAN2022 extends HttpServlet {

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

    public void HandleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId = "";
        int FacilityIndex = 0;
        String DatabaseName = "";
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();
        Connection conn = null;

        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();
        UtilityHelper helper = new UtilityHelper();

        try {
            HttpSession session = request.getSession(false);


            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

            if (session.getAttribute("UserId") != null || !session.getAttribute("UserId").equals("")) {
                UserId = session.getAttribute("UserId").toString();
                DatabaseName = session.getAttribute("DatabaseName").toString();
                FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());

                if (UserId.equals("")) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            }

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.compareTo("GETINPUT") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for Orange and Odessa", FacilityIndex);
                this.GETINPUT(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex);
            } else if (ActionID.compareTo("GETINPUTVictoria") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for Victoria", FacilityIndex);
                this.GETINPUTVictoria(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex);
            } else if (ActionID.compareTo("GETINPUT_frontline") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for Victoria", FacilityIndex);
                this.GETINPUT_frontline(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex);
            } else if (ActionID.compareTo("GETINPUT_LifeSavers") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for Victoria", FacilityIndex);
                GETINPUT_dynamo1(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex);
            }
        } catch (Exception e) {
            out.println("Exception in main... " + e.getMessage());
            out.flush();
            out.close();
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

    private void GETINPUT(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String Database, ServletContext servletContext, String UserId, int ClientId) {
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
        String MiddleInitial = null;
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
                    "date_format(now(),'%Y%m%d%H%i%s'), IFNULL(DoctorsName, '-'),IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'),'') " +
                    "FROM   " + Database + ".PatientReg where ID= '" + ID + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientIndex = rset.getString(2);
                FirstName = rset.getString(3);
                LastName = rset.getString(4);
                MiddleInitial = rset.getString(5);
                DOB = rset.getString(6);
                Age = rset.getString(7);
                Gender = rset.getString(8);
                MRN = rset.getString(9);
                CreatedDate = rset.getString(10);
                DateTime = rset.getString(11);
                DoctorsId = rset.getString(12);
                DOBForAge = rset.getString(13);
            }
            rset.close();
            stmt.close();

            if (!DOB.equals("")) {
                Age = String.valueOf(getAge(LocalDate.parse(DOBForAge)));
            }

            Query = "Select name,DirectoryName from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientName = rset.getString(1);
                DirectoryName = rset.getString(2).trim();
            }
            rset.close();
            stmt.close();

            if (!DoctorsId.equals("-")) {
                Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorsId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    DoctorsName = rset.getString(1);
                }
                rset.close();
                stmt.close();
            } else {
                DoctorsName = "";
            }


            Query = "Select MAX(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                VisitNumber = rset.getString(1).trim();
            }
            rset.close();
            stmt.close();

            VisitNumber = "VN-" + MRN + "-" + VisitNumber;

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
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 700.0f);
                        pdfContentByte.showText("ACT#:" + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 690.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
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
                        pdfContentByte.setTextMatrix((float) x, 625);
                        pdfContentByte.showText("ACT#: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 615.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
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
                        pdfContentByte.setTextMatrix((float) x, 555.0f);
                        pdfContentByte.showText("ACT#: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 545.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
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
                        pdfContentByte.setTextMatrix((float) x, 485.0f);
                        pdfContentByte.showText("ACT#: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 475);
                        pdfContentByte.showText("Dr. " + DoctorsName);
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
                        pdfContentByte.setTextMatrix((float) x, 410.0f);
                        pdfContentByte.showText("ACT#: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setTextMatrix((float) x, 400.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
                        pdfContentByte.endText();

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
                        pdfContentByte.setTextMatrix((float) x, 340.0f);
                        pdfContentByte.showText("ACT#: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 330.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
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
                        pdfContentByte.setTextMatrix((float) x, 265.0f);
                        pdfContentByte.showText("ACT#: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 255.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
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
                        pdfContentByte.setTextMatrix((float) x, 195.0f);
                        pdfContentByte.showText("ACT#: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 185.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
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
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 50.0f);
                        pdfContentByte.showText("ACT#: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 40.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
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
                Services.DumException("GETINPUT", "Printlabel4 ", request, ex2);
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

    private void GETINPUTVictoria(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final String Database, final ServletContext servletContext, final String UserId, final int ClientId) {
        final String ID = request.getParameter("ID");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String DateTime = "";
        String ClientName = "";
        String DoctorsName = "";
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
            Query = "SELECT ID, ClientIndex, FirstName, LastName, MiddleInitial, DATE_FORMAT(DOB,'%m/%d/%Y'), Age, Gender, MRN, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')),date_format(now(),'%Y%m%d%H%i%s'), IFNULL(DoctorsName, '-') FROM   " + Database + ".PatientReg where ID= '" + ID + "'";
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
                DoctorsId = rset.getString(12);
            }
            rset.close();
            stmt.close();
            Query = "Select name from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientName = rset.getString(1);
            }
            rset.close();
            stmt.close();
            if (!DoctorsId.equals("-")) {
                Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorsId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    DoctorsName = rset.getString(1);
                }
                rset.close();
                stmt.close();
            }
        } catch (Exception ex3) {
        }
        try {
            //final String inputFilePath = "/sftpdrive/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/label01_Victoria.pdf";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + DateTime + ".pdf";
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader = new PdfReader("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/label01_Victoria.pdf");
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            final GenerateBarCode barCode = new GenerateBarCode();

            String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
            final Image image = Image.getInstance(BarCodeFilePath);
            image.scaleAbsolute(60, 15);
            image.setAbsolutePosition(420.0f, 760.0f);
            for (int i = 1; i <= pdfReader.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    int x = 0;
                    int xx = 0;
                    for (int r = 1; r <= 3; ++r) {
                        if (r == 1) {
                            x = 25;
                            xx = 135;
                        } else if (r == 2) {
                            x = 220;
                            xx = 330;
                        } else if (r == 3) {
                            x = 420;
                            xx = 530;
                        }
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 740.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();

                        image.setAbsolutePosition(xx, 720);
                        pdfContentByte.addImage(image);

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
                        pdfContentByte.setTextMatrix((float) x, 700.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 665.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();

                        image.setAbsolutePosition(xx, 645);
                        pdfContentByte.addImage(image);

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
                        pdfContentByte.setTextMatrix((float) x, 625.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 595.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();

                        image.setAbsolutePosition(xx, 575);
                        pdfContentByte.addImage(image);

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
                        pdfContentByte.setTextMatrix((float) x, 555.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 525.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();

                        image.setAbsolutePosition(xx, 505);
                        pdfContentByte.addImage(image);

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
                        pdfContentByte.setTextMatrix((float) x, 485.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 450.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();

                        image.setAbsolutePosition(xx, 430);
                        pdfContentByte.addImage(image);

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
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 410.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 380.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();

                        image.setAbsolutePosition(xx, 360);
                        pdfContentByte.addImage(image);

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
                        pdfContentByte.setTextMatrix((float) x, 340.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 305.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();

                        image.setAbsolutePosition(xx, 285);
                        pdfContentByte.addImage(image);

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
                        pdfContentByte.setTextMatrix((float) x, 265.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 235.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();

                        image.setAbsolutePosition(xx, 215);
                        pdfContentByte.addImage(image);

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
                        pdfContentByte.setTextMatrix((float) x, 195.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 160.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();

                        image.setAbsolutePosition(xx, 140);
                        pdfContentByte.addImage(image);

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
                        pdfContentByte.showText("Dr. " + DoctorsName);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 90.0f);
                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                        pdfContentByte.endText();

                        image.setAbsolutePosition(xx, 70);
                        pdfContentByte.addImage(image);

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
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 50.0f);
                        pdfContentByte.showText("Dr. " + DoctorsName);
                        pdfContentByte.endText();
                    }
                }
            }
            pdfStamper.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + MRN + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
        } catch (Exception ex2) {
            try {
                Services.DumException("GETINPUTVictoria", "Printlabel4 ", request, ex2);
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

    private void GETINPUT_frontline(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String Database, ServletContext servletContext, String UserId, int ClientId) {
        final String ID = request.getParameter("ID");
        Statement stmt = null;
        ResultSet rset = null;
        String VisitNumber = "";
        String Query = "";
        String DateTime = "";
        String ClientName = "";
        String DoctorsName = "";
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
            Query = "SELECT ID, ClientIndex, FirstName, LastName, MiddleInitial, IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'),''), IFNULL(Age,''), Gender, MRN, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')),date_format(now(),'%Y%m%d%H%i%s'), IFNULL(DoctorsName, '-'),IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'),'') FROM   " + Database + ".PatientReg where ID= '" + ID + "'";
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
                DoctorsId = rset.getString(12);
            }
            rset.close();
            stmt.close();
            Query = "Select name from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientName = rset.getString(1);
            }
            rset.close();
            stmt.close();
            Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorsId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                DoctorsName = rset.getString(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception ex3) {
        }
        try {
            //String inputFilePath = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/label01.pdf";
            String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + DateTime + ".pdf";
            OutputStream fos = new FileOutputStream(new File(outputFilePath));
            PdfReader pdfReader = new PdfReader("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/label01_Victoria.pdf");
//            PdfReader pdfReader = new PdfReader("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/label01_Frontline.pdf");
            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            GenerateBarCode barCode = new GenerateBarCode();
//            String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
//            String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
//            Image image = Image.getInstance(BarCodeFilePath);
//            image.scaleAbsolute(150, 30);
//            image.setAbsolutePosition(420.0f, 760.0f);


            Query = "Select MAX(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                VisitNumber = rset.getString(1).trim();
            }
            rset.close();
            stmt.close();

            VisitNumber = "VN-" + MRN + "-" + VisitNumber;
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
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 720.0f);
                        pdfContentByte.showText("MRN:" + MRN + "    ACT: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 710.0f);
                        pdfContentByte.showText("DOS:" + CreatedDate);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 700.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
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
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 645.0f);
                        pdfContentByte.showText("MRN:" + MRN + "    ACT: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 635.0f);
                        pdfContentByte.showText("DOS:" + CreatedDate);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 625.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
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
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 575.0f);
                        pdfContentByte.showText("MRN:" + MRN + "    ACT: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 565.0f);
                        pdfContentByte.showText("DOS:" + CreatedDate);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 555.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
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
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 505.0f);
                        pdfContentByte.showText("MRN:" + MRN + "    ACT: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 495.0f);
                        pdfContentByte.showText("DOS:" + CreatedDate);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 485.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
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
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 430.0f);
                        pdfContentByte.showText("MRN:" + MRN + "    ACT: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 420.0f);
                        pdfContentByte.showText("DOS:" + CreatedDate);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 410.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
                        pdfContentByte.endText();


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
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 360.0f);
                        pdfContentByte.showText("MRN:" + MRN + "    ACT: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 350.0f);
                        pdfContentByte.showText("DOS:" + CreatedDate);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 340.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
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
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 285.0f);
                        pdfContentByte.showText("MRN:" + MRN + "    ACT: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 275.0f);
                        pdfContentByte.showText("DOS:" + CreatedDate);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 265.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
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
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 215.0f);
                        pdfContentByte.showText("MRN:" + MRN + "    ACT: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 205.0f);
                        pdfContentByte.showText("DOS:" + CreatedDate);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 195.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
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
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 140.0f);
                        pdfContentByte.showText("MRN:" + MRN + "    ACT: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 130.0f);
                        pdfContentByte.showText("DOS:" + CreatedDate);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 120.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
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
                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 70.0f);
                        pdfContentByte.showText("MRN:" + MRN + "    ACT: " + VisitNumber);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 60.0f);
                        pdfContentByte.showText("DOS:" + CreatedDate);
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix((float) x, 50.0f);
                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
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
                Services.DumException("GETINPUTFrontline", "Printlabel4 ", request, ex2);
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

    private void GETINPUTVictoria_old(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final String Database, final ServletContext servletContext, final String UserId, final int ClientId) {
        final String ID = request.getParameter("ID");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String DateTime = "";
        String ClientName = "";
        String DoctorsName = null;
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
        final Font MainHeading = new Font(2, 12.0f, 1, new Color(0, 0, 0));
        final Font normfont = new Font(2, 8.0f, 0, new Color(0, 0, 0));
        final Font normfont2 = new Font(2, 10.0f, 0, new Color(0, 0, 0));
        final Font normfont3 = new Font(2, 12.0f, 0, new Color(0, 0, 0));
        final Font UnderLine = new Font(2, 12.0f, 4, new Color(0, 0, 0));
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
            Query = "SELECT ID, ClientIndex, FirstName, LastName, MiddleInitial, DATE_FORMAT(DOB,'%m/%d/%Y'), Age, Gender, MRN, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y'),DATE_FORMAT(CreatedDate,'%m/%d/%Y')),date_format(now(),'%Y%m%d%H%i%s'), IFNULL(DoctorsName, '-') FROM   " + Database + ".PatientReg where ID= '" + ID + "'";
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
                DoctorsId = rset.getString(12);
            }
            rset.close();
            stmt.close();
            Query = "Select name from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientName = rset.getString(1);
            }
            rset.close();
            stmt.close();
            Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorsId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                DoctorsName = rset.getString(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception ex3) {
        }
        try {
            final Rectangle pageSize = new Rectangle(0.0f, 0.0f, 106.0f, 336.0f);
            final Document document = new Document(pageSize.rotate());
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, (OutputStream) baos);
            PdfWriter.getInstance(document, (OutputStream) new FileOutputStream("/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + ".pdf"));
            document.open();
            final Table datatable1 = new Table(3);
            datatable1.setWidth(100.0f);
            final int[] widths1 = {5, 80, 5};
            datatable1.setWidths(widths1);
            datatable1.setBorder(0);
            datatable1.setCellpadding(0.0f);
            datatable1.setCellspacing(0.0f);
            datatable1.setDefaultCellBorder(0);
            datatable1.setDefaultColspan(1);
            datatable1.setDefaultHorizontalAlignment(0);
            datatable1.addCell((Phrase) new Paragraph(" ", normfont2));
            datatable1.addCell((Phrase) new Paragraph(" ", normfont2));
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultColspan(1);
            datatable1.setDefaultHorizontalAlignment(0);
            datatable1.addCell((Phrase) new Paragraph(" ", normfont2));
            datatable1.addCell((Phrase) new Paragraph(" ", normfont2));
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            document.add((Element) datatable1);
            document.close();
            final String inputFilePath = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + ".pdf";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + DateTime + ".pdf";
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            final GenerateBarCode barCode = new GenerateBarCode();
//            final String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
            String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
            final Image image = Image.getInstance(BarCodeFilePath);
            image.scaleAbsolute(150.0f, 50.0f);
            image.setAbsolutePosition(180.0f, 40.0f);
            for (int i = 1; i <= pdfReader.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 80.0f);
                    pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 70.0f);
                    pdfContentByte.showText(ClientName + "  Sex: " + Gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 60.0f);
                    pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 50.0f);
                    pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 40.0f);
                    pdfContentByte.showText("Dr. " + DoctorsName);
                    pdfContentByte.endText();
                    pdfContentByte.addImage(image);
                }
            }
            pdfStamper.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=Lables_" + MRN + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
        } catch (Exception ex2) {
            String str = "";
            for (int j = 0; j < ex2.getStackTrace().length; ++j) {
                str = str + ex2.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }

    private void GETINPUT_dynamo1(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final String Database, final ServletContext servletContext, final String UserId, final int ClientId) {
        final String ID = request.getParameter("ID");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String DateTime = "";
        String ClientName = "";
        String DoctorsName = null;
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
        String MiddleInitial = "";
        int SelfPayChk = 0;
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
                    "DATE_FORMAT(DOB,'%m/%d/%Y'), Age, Gender, MRN, " +
                    "IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y')," +
                    "DATE_FORMAT(CreatedDate,'%m/%d/%Y')),date_format(now(),'%Y%m%d%H%i%s'), " +
                    "IFNULL(DoctorsName, '-'),IFNULL(SelfPayChk,9) FROM   " + Database + ".PatientReg " +
                    "where ID= '" + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientIndex = rset.getString(2);
                FirstName = rset.getString(3);
                LastName = rset.getString(4);
                MiddleInitial = rset.getString(5);
                DOB = rset.getString(6);
                Age = rset.getString(7);
                Gender = rset.getString(8);
                MRN = rset.getString(9);
                CreatedDate = rset.getString(10);
                DateTime = rset.getString(11);
                DoctorsId = rset.getString(12);
                SelfPayChk = rset.getInt(13);
            }
            rset.close();
            stmt.close();

            Query = "Select name from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) " +
                    "from " + Database + ".DoctorsList where Id = " + DoctorsId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DoctorsName = rset.getString(1);
            }
            rset.close();
            stmt.close();

        } catch (Exception ex3) {
        }
        try {

            String insuranceStatus = "";
            if (SelfPayChk == 1)
                insuranceStatus = "IP";
            else if (SelfPayChk == 0)
                insuranceStatus = "IP";
            else
                insuranceStatus = "##";

            final String inputFilePath = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + ".pdf";

            //final String outputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/Labels/" + MRN + LastName + ID + "_" + DateTime + ".pdf";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + DateTime + ".pdf";
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            //final PdfReader pdfReader = new PdfReader(inputFilePath);
            final PdfReader pdfReader = new PdfReader("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/roverlab_label_temp.pdf");

            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

            final GenerateBarCode barCode = new GenerateBarCode();
            final String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);

            final Image image = Image.getInstance(BarCodeFilePath);
            image.scaleAbsolute(150.0f, 30.0f);
            image.setAbsolutePosition(180, 30.0f);
            for (int i = 1; i <= pdfReader.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 80.0f);
                    pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 70.0f);
                    pdfContentByte.showText(ClientName + "  Sex: " + Gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 60.0f);
                    pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 50.0f);
                    pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 40.0f);
                    pdfContentByte.showText("Dr. " + DoctorsName);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 30.0f);
                    pdfContentByte.showText(insuranceStatus);
                    pdfContentByte.endText();

                    pdfContentByte.addImage(image);
                }

            }
            pdfStamper.close();
            pdfReader.close();

            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=Lables_" + MRN + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);


            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
        } catch (Exception ex2) {
            String str = "";
            for (int j = 0; j < ex2.getStackTrace().length; ++j) {
                str = str + ex2.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }
}
