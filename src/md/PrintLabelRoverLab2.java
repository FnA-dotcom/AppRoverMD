// 
// Decompiled by Procyon v0.5.36
// 

package md;

import DAL.TwilioSMSConfiguration;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PrintLabelRoverLab2 extends HttpServlet {
    private Connection conn = null;

    public static int getAge(LocalDate dob) {
        LocalDate curDate = LocalDate.now();
        return Period.between(dob, curDate).getYears();
    }

    public static void type1_02(PdfStamper stamper, int pageno, String Data, Float x, Float y, Float Font) throws DocumentException, IOException {
        Image image = Image.getInstance(Data);
        PdfContentByte pdfContentByte = stamper.getOverContent(pageno);
        image.setAbsolutePosition(x, y);
        image.scaleAbsolute(180.0f, 180.0f);
        //	image.setAbsolutePosition(180.0f, 180.0f);
        pdfContentByte.addImage(image);

    }

    public static void type1_01(PdfStamper stamper, int pageno, String Data, Float x, Float y, Float Font) throws DocumentException, IOException {

        PdfContentByte pdfContentByte = stamper.getOverContent(pageno);
        pdfContentByte.beginText();
        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), Font);
        pdfContentByte.setColorFill(BaseColor.BLACK);
        pdfContentByte.setTextMatrix(x, y);
        pdfContentByte.showText(Data);
        pdfContentByte.endText();

    }

    public static void encrypt(PdfReader reader, OutputStream os, byte userPassword[], byte ownerPassword[], int permissions, boolean strength128Bits, HashMap<String, String> newInfo) throws DocumentException, IOException {
        PdfStamper stamper = new PdfStamper(reader, os);
        stamper.setEncryption(userPassword, ownerPassword, permissions, strength128Bits);
        stamper.setMoreInfo(newInfo);
        stamper.close();
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
        String locationArray = "";
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
                locationArray = session.getAttribute("LocationArray").toString();
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
            if (ActionID.compareTo("GETINPUTRoverLab") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for ", FacilityIndex);
                this.GETINPUTRoverLab(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex);
            }
            if (ActionID.compareTo("GETINPUT_dymo1") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for ", FacilityIndex);
                this.GETINPUT_dymo1(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex);
            }
            if (ActionID.compareTo("GETINPUT_dymo2") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for ", FacilityIndex);
                this.GETINPUT_dymo2(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex);
            }
            if (ActionID.compareTo("GETINPUT_QR_Generator") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for ", FacilityIndex);
                this.GETINPUT_QR_Generator(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex);
            }
            if (ActionID.compareTo("GETINPUT_QR") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for ", FacilityIndex);
                this.GETINPUT_QR(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex, locationArray);
            }
            if (ActionID.compareTo("SendSmsLAB") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for ", FacilityIndex);
                this.SendSmsLAB(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex);
            } else {
                out.println("UNDER DEVELOPMENT");
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

    private void GETINPUTRoverLab(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final String Database, final ServletContext servletContext, final String UserId, final int ClientId) {
        final String ID = request.getParameter("ID");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String DateTime = "";
        String ClientName = "";
        String ClientIndex = null;
        String FirstName = null;
        String LastName = null;
        String DOB = null;
        String Age = null;
        String Gender = null;
        String MRN = null;
        String CreatedDate = null;
        String DirectoryName = "";
        String testid = "";
        String testlist = "";
        String OrderId = "";
        try {
            Query = "SELECT ID, ClientIndex, FirstName, LastName, MiddleInitial, DATE_FORMAT(DOB,'%m/%d/%Y'), " +
                    "Age, Gender, MRN," +
                    "DATE_FORMAT(CreatedDate,'%m/%d/%Y'),date_format(now(),'%Y%m%d%H%i%s') " +
                    "FROM   " + Database + ".PatientReg where ID= '" + ID + "'";

            //Query = "SELECT ID, ClientIndex, FirstName, LastName, MiddleInitial, IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'),''), IFNULL(Age,''), Gender, MRN, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y'),DATE_FORMAT(CreatedDate,'%m/%d/%Y')),date_format(now(),'%Y%m%d%H%i%s'), IFNULL(DoctorsName, '-'),IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'),'') FROM   " + Database + ".PatientReg where ID= '" + ID + "'";
            http:
//app.rovermd.com:8443/md/md.result?oid=

            System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientIndex = rset.getString(2);
                FirstName = rset.getString(3);
                LastName = rset.getString(4);
                String MiddleInitial = rset.getString(5);
                DOB = rset.getString(6);
                Age = rset.getString(7);
                Gender = rset.getString(8);
                MRN = rset.getString(9);
                CreatedDate = rset.getString(10);

            }
            rset.close();
            stmt.close();
            Query = "Select name,IFNULL(DirectoryName,'') from oe.clients where Id = " + ClientId;
            //out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientName = rset.getString(1);
                DirectoryName = rset.getString(2);
            }
            rset.close();
            stmt.close();

            Query = "SELECT Id,OrderNum FROM " + Database + ".TestOrder WHERE PatRegIdx= " + ID;
            //out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                testid = rset.getString(1);
                OrderId = rset.getString(2);
            }
            rset.close();
            stmt.close();


            Query = "SELECT IFNULL(b.TestName,''),IFNULL(a.OrderId,''),IFNULL(a.TestIdx,'') , IFNULL(a.Priority,'') , IFNULL(a.Narration,'') , IFNULL(a.TestStatus,'') FROM " + Database + ".Tests a " +
                    "LEFT JOIN " + Database + ".ListofTests b ON a.TestIdx=b.id WHERE OrderId=" + testid;
            System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                testlist = testlist + "|" + rset.getString(1);
                //OrderId = rset.getString(2);
            }
            rset.close();
            stmt.close();

        } catch (Exception ex3) {
        }
        try {
            String charset = "UTF-8";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + ".pdf";

            //out.println(outputFilePath);
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader = new PdfReader("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/roverlab_label_temp.pdf");
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            final GenerateBarCode barCode = new GenerateBarCode();
            //out.println(ClientId+"|"+MRN);
            String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);

            final String outputFilePath2 = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + ".png";
            // https://app1.rovermd.com:8443/md/md.result?ActionID=GetInput&oid=OI-310018-17&m=310017
            String url = "https://app1.rovermd.com:8443/md/md.result?ActionID=GetInput&oid=" + OrderId + "&m=" + MRN;
            String QrodeFilePath = QRcodeV2.generateQRcode_v1(url, outputFilePath2, charset, 150, 150);

            Image image = Image.getInstance(BarCodeFilePath);
            image.scaleAbsolute(70, 30);
            image.setAbsolutePosition(420.0f, 760.0f);


            System.out.println(QrodeFilePath);
            Image image2 = Image.getInstance(QrodeFilePath);
            image2.scaleAbsolute(70, 70);
            // image2.setAbsolutePosition(420.0f, 860.0f);


            for (int i = 1; i <= pdfReader.getNumberOfPages(); ++i) {

                final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 90.0f);
                pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName + "(" + Gender + ")");
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 80.0f);
                pdfContentByte.showText("Orderid:" + OrderId);
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 70.0f);
                pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 60.0f);
                pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 50.0f);
                pdfContentByte.showText("Test :" + testlist + "");
                pdfContentByte.endText();


                image.setAbsolutePosition(30, 15);
                pdfContentByte.addImage(image);

                image2.setAbsolutePosition(240, 10);
                pdfContentByte.addImage(image2);


            }
            pdfStamper.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + MRN + "_" + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
        } catch (Exception ex2) {
            try {
                out.println(ex2.getMessage());
                Services.DumException("PrintLabelRoverLab2", "Printlabel4 ", request, ex2);
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

    private void GETINPUT_dymo1(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final String Database, final ServletContext servletContext, final String UserId, final int ClientId) {
        final String ID = request.getParameter("ID");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String DateTime = "";
        String ClientName = "";
        String ClientIndex = null;
        String FirstName = null;
        String LastName = null;
        String DOB = null;
        String Age = null;
        String Gender = null;
        String MRN = null;
        String CreatedDate = null;
        String DirectoryName = "";
        String testid = "";
        String testlist = "";
        String OrderId = "";
        try {
            Query = "SELECT ID, ClientIndex, FirstName, LastName, MiddleInitial, DATE_FORMAT(DOB,'%m/%d/%Y'), " +
                    "Age, Gender, MRN," +
                    "DATE_FORMAT(CreatedDate,'%m/%d/%Y'),date_format(now(),'%Y%m%d%H%i%s') " +
                    "FROM   " + Database + ".PatientReg where ID= '" + ID + "'";

            //Query = "SELECT ID, ClientIndex, FirstName, LastName, MiddleInitial, IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'),''), IFNULL(Age,''), Gender, MRN, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y'),DATE_FORMAT(CreatedDate,'%m/%d/%Y')),date_format(now(),'%Y%m%d%H%i%s'), IFNULL(DoctorsName, '-'),IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'),'') FROM   " + Database + ".PatientReg where ID= '" + ID + "'";
            http:
//app.rovermd.com:8443/md/md.result?oid=

            System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientIndex = rset.getString(2);
                FirstName = rset.getString(3);
                LastName = rset.getString(4);
                String MiddleInitial = rset.getString(5);
                DOB = rset.getString(6);
                Age = rset.getString(7);
                Gender = rset.getString(8);
                MRN = rset.getString(9);
                CreatedDate = rset.getString(10);

            }
            rset.close();
            stmt.close();
            Query = "Select name,IFNULL(DirectoryName,'') from oe.clients where Id = " + ClientId;
            //out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientName = rset.getString(1);
                DirectoryName = rset.getString(2);
            }
            rset.close();
            stmt.close();

            Query = "SELECT Id,OrderNum FROM " + Database + ".TestOrder WHERE PatRegIdx= " + ID;
            //out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                testid = rset.getString(1);
                OrderId = rset.getString(2);
            }
            rset.close();
            stmt.close();


            Query = "SELECT IFNULL(b.TestName,''),IFNULL(a.OrderId,''),IFNULL(a.TestIdx,'') , IFNULL(a.Priority,'') , IFNULL(a.Narration,'') , IFNULL(a.TestStatus,'') FROM " + Database + ".Tests a " +
                    "LEFT JOIN " + Database + ".ListofTests b ON a.TestIdx=b.id WHERE OrderId=" + testid;
            System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                testlist = testlist + "|" + rset.getString(1);
                //OrderId = rset.getString(2);
            }
            rset.close();
            stmt.close();

        } catch (Exception ex3) {
        }
        try {
            String charset = "UTF-8";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + ".pdf";

            //out.println(outputFilePath);
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader = new PdfReader("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/roverlab_label_temp.pdf");
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            final GenerateBarCode barCode = new GenerateBarCode();
            //out.println(ClientId+"|"+MRN);
            String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);

            final String outputFilePath2 = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + ".png";
            // https://app1.rovermd.com:8443/md/md.result?ActionID=GetInput&oid=OI-310018-17&m=310017
            String url = "https://app1.rovermd.com:8443/md/md.result?ActionID=GetInput&oid=" + OrderId + "&m=" + MRN;
            String QrodeFilePath = QRcodeV2.generateQRcode_v1(url, outputFilePath2, charset, 150, 150);

            Image image = Image.getInstance(BarCodeFilePath);
            image.scaleAbsolute(70, 30);
            image.setAbsolutePosition(420.0f, 760.0f);


            System.out.println(QrodeFilePath);
            Image image2 = Image.getInstance(QrodeFilePath);
            image2.scaleAbsolute(70, 70);
            // image2.setAbsolutePosition(420.0f, 860.0f);


            for (int i = 1; i <= pdfReader.getNumberOfPages(); ++i) {

                final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 90.0f);
                pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName + "(" + Gender + ")");
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 80.0f);
                pdfContentByte.showText("Orderid:" + OrderId);
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 70.0f);
                pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 60.0f);
                pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 50.0f);
                pdfContentByte.showText("Test :" + testlist + "");
                pdfContentByte.endText();


                image.setAbsolutePosition(30, 15);
                pdfContentByte.addImage(image);

                //   image2.setAbsolutePosition(240, 10);
                //   pdfContentByte.addImage(image2);


            }
            pdfStamper.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + MRN + "_" + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
        } catch (Exception ex2) {
            try {
                out.println(ex2.getMessage());
                Services.DumException("PrintLabelRoverLab2", "Printlabel4 ", request, ex2);
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

    private void GETINPUT_dymo2(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final String Database, final ServletContext servletContext, final String UserId, final int ClientId) {
        final String ID = request.getParameter("ID");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String DateTime = "";
        String ClientName = "";
        String ClientIndex = null;
        String FirstName = null;
        String LastName = null;
        String DOB = null;
        String Age = null;
        String Gender = null;
        String MRN = null;
        String CreatedDate = null;
        String DirectoryName = "";
        String testid = "";
        String testlist = "";
        String OrderId = "";
        try {
            Query = "SELECT ID, ClientIndex, FirstName, LastName, MiddleInitial, DATE_FORMAT(DOB,'%m/%d/%Y'), " +
                    "Age, Gender, MRN," +
                    "DATE_FORMAT(CreatedDate,'%m/%d/%Y'),date_format(now(),'%Y%m%d%H%i%s') " +
                    "FROM   " + Database + ".PatientReg where ID= '" + ID + "'";

            //Query = "SELECT ID, ClientIndex, FirstName, LastName, MiddleInitial, IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'),''), IFNULL(Age,''), Gender, MRN, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y'),DATE_FORMAT(CreatedDate,'%m/%d/%Y')),date_format(now(),'%Y%m%d%H%i%s'), IFNULL(DoctorsName, '-'),IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'),'') FROM   " + Database + ".PatientReg where ID= '" + ID + "'";
            http:
//app.rovermd.com:8443/md/md.result?oid=

            System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientIndex = rset.getString(2);
                FirstName = rset.getString(3);
                LastName = rset.getString(4);
                String MiddleInitial = rset.getString(5);
                DOB = rset.getString(6);
                Age = rset.getString(7);
                Gender = rset.getString(8);
                MRN = rset.getString(9);
                CreatedDate = rset.getString(10);

            }
            rset.close();
            stmt.close();
            Query = "Select name,IFNULL(DirectoryName,'') from oe.clients where Id = " + ClientId;
            //out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientName = rset.getString(1);
                DirectoryName = rset.getString(2);
            }
            rset.close();
            stmt.close();

            Query = "SELECT Id,OrderNum FROM " + Database + ".TestOrder WHERE PatRegIdx= " + ID;
            //out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                testid = rset.getString(1);
                OrderId = rset.getString(2);
            }
            rset.close();
            stmt.close();


            Query = "SELECT IFNULL(b.TestName,''),IFNULL(a.OrderId,''),IFNULL(a.TestIdx,'') , IFNULL(a.Priority,'') , IFNULL(a.Narration,'') , IFNULL(a.TestStatus,'') FROM " + Database + ".Tests a " +
                    "LEFT JOIN " + Database + ".ListofTests b ON a.TestIdx=b.id WHERE OrderId=" + testid;
            System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                testlist = testlist + "|" + rset.getString(1);
                //OrderId = rset.getString(2);
            }
            rset.close();
            stmt.close();

        } catch (Exception ex3) {
        }
        try {
            String charset = "UTF-8";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + ".pdf";

            //out.println(outputFilePath);
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader = new PdfReader("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/roverlab_label_temp.pdf");
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            final GenerateBarCode barCode = new GenerateBarCode();
            //out.println(ClientId+"|"+MRN);
            String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);

            final String outputFilePath2 = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + ".png";
            // https://app1.rovermd.com:8443/md/md.result?ActionID=GetInput&oid=OI-310018-17&m=310017
            String url = "https://app1.rovermd.com:8443/md/md.result?ActionID=GetInput&oid=" + OrderId + "&m=" + MRN;
            String QrodeFilePath = QRcodeV2.generateQRcode_v1(url, outputFilePath2, charset, 150, 150);

            Image image = Image.getInstance(BarCodeFilePath);
            image.scaleAbsolute(70, 30);
            image.setAbsolutePosition(420.0f, 760.0f);


            System.out.println(QrodeFilePath);
            Image image2 = Image.getInstance(QrodeFilePath);
            image2.scaleAbsolute(70, 70);
            // image2.setAbsolutePosition(420.0f, 860.0f);


            for (int i = 1; i <= pdfReader.getNumberOfPages(); ++i) {

                final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 90.0f);
                pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName + "(" + Gender + ")");
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 80.0f);
                pdfContentByte.showText("Orderid:" + OrderId);
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 70.0f);
                pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 60.0f);
                pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20.0f, 50.0f);
                pdfContentByte.showText("Test :" + testlist + "");
                pdfContentByte.endText();


                image.setAbsolutePosition(30, 15);
                pdfContentByte.addImage(image);

                //  image2.setAbsolutePosition(240, 10);
                //    pdfContentByte.addImage(image2);


            }
            pdfStamper.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + MRN + "_" + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
        } catch (Exception ex2) {
            try {
                out.println(ex2.getMessage());
                Services.DumException("PrintLabelRoverLab2", "Printlabel4 ", request, ex2);
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

    private void SendSmsLAB(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final String Database, final ServletContext servletContext, final String UserId, final int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String url = "";
            String Gentime = "";
            String LocationName = "";
            String LocationAddress = "";
            String testname = "";

            TwilioSMSConfiguration smsConfiguration = new TwilioSMSConfiguration();

            String PtMRN = "0";

            String PtName = request.getParameter("Name").trim();
            final String l_id = request.getParameter("l_id");
            final String t_id = request.getParameter("t_id");
            final String f_id = request.getParameter("f_id");
            String PtPhNumber = request.getParameter("Ph").trim();
            String ClientName = "";
            String Sms = request.getParameter("Sms").trim();
            try {
                Query = "Select url,now(),Location,concat(Address,City,State,Zip) from " + Database + ".Locations where Id = " + l_id;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    url = rset.getString(1);
                    Gentime = rset.getString(2);
                    LocationName = rset.getString(3);
                    LocationAddress = rset.getString(4);
                }
                rset.close();
                stmt.close();

                Query = "Select name from oe.clients where Id = " + ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    ClientName = rset.getString(1);
                }


                Query = "Select TestName from " + Database + ".ListofTests where Id = " + t_id;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    testname = rset.getString(1);
                }
                rset.close();
                stmt.close();

                url = url + "?loc=" + l_id + "&st=" + t_id;

                Sms = "Thank you for choosing Primescope Diagnostics, LLC for your test. Please complete the registration form prior to your visit at the link below  "
                        + " " + url;

            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] result = smsConfiguration.sendTwilioMessages(request, conn, servletContext, Sms, Integer.parseInt(f_id), PtPhNumber, 67, Database, PtMRN, 0);
            out.println("1");
        } catch (Exception ex) {
            out.println(ex.getMessage());
            ex.getStackTrace();
            String str = "";
            for (int i = 0; i < ex.getStackTrace().length; i++) {
                str = str + ex.getStackTrace()[i] + "<br>";
            }
            out.println("2");
        }
    }

    private void GETINPUT_QR(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final String Database, final ServletContext servletContext, final String UserId, final int ClientId, String locationArray) {
        //final String l_id = request.getParameter("l_id");
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
        String insuranceStatus = "-";
        int SelfPayChk = 0;

        String url = "";
        String Gentime = "";
        String LocationName = "";
        String LocationAddress = "";
        String testname = "";
        StringBuilder locationList = new StringBuilder();
        StringBuilder testList = new StringBuilder();
        try {

            ArrayList<String> al = new ArrayList<>();
            al.add(locationArray);
            String list = Arrays.toString(al.toArray()).replace("[", "").replace("]", "");

            String locCondition = "";
            if (locationArray.length() > 1) {
                locCondition = " AND f.Id IN (" + list + ") ";
            }


            Query = "Select Id, Location from roverlab.Locations WHERE Id IN (" + list + ") ";
            System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            locationList.append("<option value='-1' selected disabled>Select Location</option>");
            while (rset.next()) {
                locationList.append("<option value=" + rset.getString(1) + "  >" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Query = "Select name from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientName = rset.getString(1);
            }


            Query = "Select id,TestName from " + Database + ".ListofTests where status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            testList.append("<option value='-1' selected disabled>Select Location</option>");

            while (rset.next()) {

                testList.append("<option value=" + rset.getString(1) + "  >" + rset.getString(2) + "</option>");

            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("testList", testList.toString());
            Parser.SetField("UserId", UserId);
            Parser.SetField("LocationList", locationList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/GenerateQr.html");


        } catch (Exception ex3) {

            System.out.println(ex3.getMessage());
        }


    }

    private void GETINPUT_QR_Generator(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final String Database, final ServletContext servletContext, final String UserId, final int ClientId) {
        final String l_id = request.getParameter("l_id");
        final String t_id = request.getParameter("t_id");
        final String f_id = request.getParameter("f_id");
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
        String insuranceStatus = "-";
        int SelfPayChk = 0;

        String url = "";
        String Gentime = "";
        String LocationName = "";
        String LocationAddress = "";
        String testname = "";
        try {


            Query = "Select url,now(),Location,concat(Address,City,State,Zip) from " + Database + ".Locations where Id = " + l_id;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                url = rset.getString(1);
                Gentime = rset.getString(2);
                LocationName = rset.getString(3);
                LocationAddress = rset.getString(4);
            }
            rset.close();
            stmt.close();

            Query = "Select name from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientName = rset.getString(1);
            }


            Query = "Select TestName from " + Database + ".ListofTests where Id = " + t_id;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                testname = rset.getString(1);
            }
            rset.close();
            stmt.close();


        } catch (Exception ex3) {
        }
        try {


            String charset = "UTF-8";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + ClientId + "_" + l_id + "_" + DateTime + ".pdf";
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            //final PdfReader pdfReader = new PdfReader(inputFilePath);
            final PdfReader pdfReader = new PdfReader("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/blanka4.pdf");

            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            final String outputFilePath2 = "/sftpdrive/AdmissionBundlePdf/Labels/" + ClientId + "_" + l_id + "_" + DateTime + "_" + ".png";


            // https://app1.rovermd.com:8443/md/md.result?ActionID=GetInput&oid=OI-310018-17&m=310017
            url = url + "?loc=" + l_id + "&st=" + t_id;
            String QrodeFilePath = QRcodeV2.generateQRcode_v1(url, outputFilePath2, charset, 150, 150);


            for (int i = 1; i <= pdfReader.getNumberOfPages(); ++i) {
                if (i == 1) {
                    type1_01(pdfStamper, i, LocationName, 150.0f, 700.0f, 30.0f);
                    type1_01(pdfStamper, i, testname, 150.0f, 640.0f, 10.0f);
                    type1_01(pdfStamper, i, Gentime, 150.0f, 620.0f, 10.0f);
                    type1_01(pdfStamper, i, "RoverMD v.1.1", 150.0f, 600.0f, 10.0f);
                    type1_02(pdfStamper, i, QrodeFilePath, 180.0f, 340.0f, 30.0f);
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
            System.out.println(str);
        }
    }
}