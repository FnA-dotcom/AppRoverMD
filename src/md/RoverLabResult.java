//
// Decompiled by Procyon v0.5.36
//

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

@SuppressWarnings("Duplicates")
public class RoverLabResult extends HttpServlet {


    private static boolean ReadPdfGetData(final String FileName, final String Path) {
        try (final PDDocument document = PDDocument.load(new File(Path + "/" + FileName))) {
            document.getClass();
            if (document.isEncrypted()) {
                return false;
            }
            final PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            final PDFTextStripper tStripper = new PDFTextStripper();
            tStripper.getStartPage();
            final String pdfFileInText = tStripper.getText(document);
            final String[] lines;
            final String[] split;
            final String[] array2;
            final String[] array = array2 = (split = (lines = pdfFileInText.split("\\r?\\n")));
            for (final String line : array2) {
                if (line.toUpperCase().trim().contains("SIGNATURE")) {
                }
            }
        } catch (Exception ee) {
            System.out.println(ee.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String ActionID = "";
        String DirectoryName = "";
        Connection conn = null;
        ResultSet rset = null;
        String Query = null;
        Statement stmt = null;
        final ServletContext context = this.getServletContext();
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        response.setContentType("text/html");
        final UtilityHelper helper = new UtilityHelper();
        final Services supp = new Services();
        try {
            final HttpSession session = request.getSession(false);
            final boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            final String UserId = session.getAttribute("UserId").toString();
            final String DatabaseName = session.getAttribute("DatabaseName").toString();
            final int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            if (UserId.equals("")) {
                final Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                final Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            Query = "Select dbname, IFNULL(DirectoryName,'') from oe.clients where Id = " + FacilityIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DirectoryName = rset.getString(2);
            }
            rset.close();
            stmt.close();
            out.println("ActionID -> " + ActionID);
            out.println(ActionID.equals("getResultPdf"));
            if (ActionID.equals("getResultPdf")) {
                getResultPdf(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName);
            }else if (ActionID.equals("sendResultReport")) {
                sendResultReport(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName,helper);
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
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            out.flush();
            out.close();
        }
    }


    private void sendResultReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context,
                                  HttpServletResponse response, String userId, String Database, int facilityIndex, String directoryName,UtilityHelper helper) {
        String O_ID = request.getParameter("O_ID").trim();
        String T_ID = request.getParameter("T_ID").trim();
        ResultSet rset = null;
        String email = null;
        String filepath = null;
        String filename = null;
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT IFNULL(a.email,''), IFNULL(c.Reportpath,''),IFNULL(c.filename,'') FROM  " + Database + ".PatientReg a LEFT JOIN  " + Database + ".TestOrder b ON a.ID=b.PatRegIdx  LEFT JOIN  " + Database + ".Tests c ON b.ID=c.OrderId   where b.id=? AND c.id=?");
            ps.setString(1, O_ID);
            ps.setString(2, T_ID);
            rset = ps.executeQuery();
            if (rset.next()) {
                email = rset.getString(1);
                filepath = rset.getString(2);
                filename = rset.getString(3);
            }
            rset.close();
            ps.close();

            //    String filePath = "https://rovermd.com:8443/md/md.Filedispatcher?p=/sftpdrive/users/epowerdoc/lab-integration/Integration/Reports_final/&fn=0000000047_OI-310031-30_TESTRS_202202171218.HL7.pdf";
            String filePath = "https://rovermd.com:8443/md/md.Filedispatcher?p="+filepath+"&fn="+filename;

            URL url = new URL(filePath);
            HttpURLConnection httpConn = (HttpURLConnection)
                    url.openConnection();

            System.out.println("");
            // File downloadFile = new File(filePath);
            // FileInputStream inStream = new FileInputStream(downloadFile);

            InputStream inStream = httpConn.getInputStream();

            // if you want to use a relative path to context root:
            String relativePath = getServletContext().getRealPath("");
            System.out.println("relativePath = " + relativePath);

            // obtains ServletContext


            // gets MIME type of the file
            String mimeType = context.getMimeType(filePath);
            if (mimeType == null) {
                // set to binary type if MIME mapping not found
                mimeType = "application/octet-stream";
            }
            System.out.println("MIME type: " + mimeType);

            // modifies response
            response.setContentType(mimeType);
            response.setContentLength((int) httpConn.getContentLength());

            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1,
                    filePath.length());

            // forces download
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"",
                    fileName);
            response.setHeader(headerKey, headerValue);
            String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + directoryName + "/Results/" + fileName.replace("&","");

            // obtains response's output stream
            // OutputStream outStream = response.getOutputStream();
            FileOutputStream fout =  new FileOutputStream(outputFilePath);;

            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while ((bytesRead = inStream.read(buffer)) != -1) {
                fout.write(buffer, 0, bytesRead);
            }

            inStream.close();
            fout.close();


            System.out.println("EMAIL " + email);
            System.out.println("filepath " + filepath);
            System.out.println("outputFilePath " + outputFilePath);
            email="alisaadbaig@gmail.com";
            String[] files={outputFilePath};
//            helper.SendEmailWithAttachment_ROVERLAB("Test Report", context, conn, email,files );
            System.out.println("1");
            out.println("1");
            out.flush();
            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.getMessage();
            out.println("0");
        }

    }

    void getResultPdf(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
        out.println("inside getResultPdf");

        ResultSet rset = null;
        ResultSet rset2 = null;

        String ID = request.getParameter("PatRegIdx").trim();
        String O_ID = request.getParameter("O_ID").trim();
        String T_ID = request.getParameter("T_ID").trim();
        String Name = null;
        String DOB = null;
        String Gender = null;
        String TestingLocation = null;
        String TestName = null;
        String TestComments = null;
        String TestResult = null;
        String CollectionDateTime = null;
        String ReceivedDateTime = null;
        String ReportedDateTime = null;
        String Physician = null;
        String SampleType = null;
        String DateTime = null;
        String filepath = null;
        String filename = null;
        PreparedStatement ps = null;


        try {


            ps = conn.prepareStatement("SELECT IFNULL(Reportpath,''),IFNULL(filename,'') FROM " + Database + ".Tests WHERE OrderId='" + O_ID + "' AND Id='" + T_ID + "'");
            rset = ps.executeQuery();
            if (rset.next()) {
                filepath = rset.getString(1);
                filename = rset.getString(2);
            }
            rset.close();
            ps.close();
            out.println(filepath);
            out.println(filename);
            if (filepath.compareTo("") == 0 && filename.compareTo("") == 0) {
                out.println("Result not Available");
                return;
            }
            // String filePath = "https://rovermd.com:8443/md/md.Filedispatcher?p=/sftpdrive/users/epowerdoc/lab-integration/Integration/Reports_final/&fn=0000000047_OI-310031-30_TESTRS_202202171218.HL7.pdf";
            String filePath = "https://rovermd.com:8443/md/md.Filedispatcher?p="+filepath+"&fn="+filename;

            URL url = new URL(filePath);
            HttpURLConnection httpConn = (HttpURLConnection)
                    url.openConnection();
            //File downloadFile = new File(filePath);
            //FileInputStream inStream = new FileInputStream(downloadFile);

            InputStream inStream = httpConn.getInputStream();

            // if you want to use a relative path to context root:
            String relativePath = getServletContext().getRealPath("");
            System.out.println("relativePath = " + relativePath);

            // obtains ServletContext
            ServletContext context = getServletContext();

            // gets MIME type of the file
            String mimeType = context.getMimeType(filePath);
            if (mimeType == null) {
                // set to binary type if MIME mapping not found
                mimeType = "application/octet-stream";
            }
            System.out.println("MIME type: " + mimeType);

            // modifies response
            response.setContentType(mimeType);
            response.setContentLength((int) httpConn.getContentLength());

            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1,
                    filePath.length());

            // forces download
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"",
                    fileName);
            response.setHeader(headerKey, headerValue);

            // obtains response's output stream
            OutputStream outStream = response.getOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

            inStream.close();
            outStream.close();


            //    out.println("Before Getting ... ");
/*
            String Filename = Name.replaceAll(" ", "") + "_" + ID + "_" + DateTime + "_" + TestName.replaceAll(" ", "") + "_" + O_ID + "_" + T_ID + "_" + TestResult + "_ROVERLAB.pdf";
            String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Results/" + Filename;



            File pdfFile = new File(outputFilePath);

            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + Filename);
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }*/
        } catch (Exception e) {
            out.println("Hello ->>> " + e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }


    void getResultPdf_org(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
        out.println("inside getResultPdf");

        ResultSet rset = null;
        ResultSet rset2 = null;

        String ID = request.getParameter("PatRegIdx").trim();
        String O_ID = request.getParameter("O_ID").trim();
        String T_ID = request.getParameter("T_ID").trim();
        String Name = null;
        String DOB = null;
        String Gender = null;
        String TestingLocation = null;
        String TestName = null;
        String TestComments = null;
        String TestResult = null;
        String CollectionDateTime = null;
        String ReceivedDateTime = null;
        String ReportedDateTime = null;
        String Physician = null;
        String SampleType = null;
        String DateTime = null;
        String filepath = null;
        String filename = null;
        PreparedStatement ps = null;


        try {

            ps = conn.prepareStatement("SELECT CONCAT(IFNULL(a.FirstName,'') ,' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'') ), " +
                    " IFNULL(a.DOB,'') , IFNULL(a.Gender,'') , IFNULL(b.Location,''),  DATE_FORMAT(NOW(),'%Y-%m-%d')" +
                    " FROM " + Database + ".PatientReg a " +
                    " LEFT JOIN " + Database + ".Locations b  ON b.id = a.TestingLocation  " +
                    " WHERE a.Id='" + ID + "'");
//            System.out.println("Query ->> " + ps.toString());
            rset = ps.executeQuery();
            if (rset.next()) {
                Name = rset.getString(1);
                DOB = rset.getString(2);
                Gender = rset.getString(3);
                TestingLocation = rset.getString(4);
                DateTime = rset.getString(5);
            }
            ps.close();
            rset.close();
//            out.println("Before Getting ... ");


            ps = conn.prepareStatement("SELECT IFNULL(b.TestName,'') , IFNULL(a.Narration,'') , IFNULL(d.Result,'')," +
                    " IFNULL(CollectionDateTime,'N/A') , DATE_FORMAT(IFNULL(ReceivedDateTime,'N/A'),'%Y-%m-%d'), IFNULL(ReportedDateTime,'N/A')," +
                    " CONCAT(IFNULL(c.DoctorsFirstName,'') ,' ',IFNULL(c.DoctorsLastName,'')), IFNULL(a.SampleType,'') " +
                    " FROM " + Database + ".Tests a " +
                    " LEFT JOIN " + Database + ".ListofTests b ON a.TestIdx=b.id " +
                    " LEFT JOIN " + Database + ".DoctorsList c ON a.PhysicianIdx=c.id " +
                    " LEFT JOIN " + Database + ".ListofTestResults d ON a.TestStatus=d.id " +
                    " WHERE a.OrderId='" + O_ID + "' AND a.Id='" + T_ID + "'");
//            System.out.println("Query -> " + ps.toString());
            rset = ps.executeQuery();
            while (rset.next()) {
                TestName = rset.getString(1);
                TestComments = rset.getString(2);
                TestResult = rset.getString(3);
                CollectionDateTime = rset.getString(4);
                ReceivedDateTime = rset.getString(5);
                ReportedDateTime = rset.getString(6);
                Physician = rset.getString(7);
                SampleType = rset.getString(8);

            }
            rset.close();
            ps.close();

            ps = conn.prepareStatement("SELECT IFNULL(filepath,''),IFNULL(filename,'') FROM " + Database + ".Tests WHERE OrderId='" + O_ID + "' AND Id='" + T_ID + "'");
            rset = ps.executeQuery();
            if (rset.next()) {
                filepath = rset.getString(1);
                filename = rset.getString(2);
            }
            rset.close();
            ps.close();

            if (filepath.compareTo("") != 0 && filename.compareTo("") != 0 && filename.contains(TestResult)) {
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


//            out.println("Before Getting ... ");

            String Filename = Name.replaceAll(" ", "") + "_" + ID + "_" + DateTime + "_" + TestName.replaceAll(" ", "") + "_" + O_ID + "_" + T_ID + "_" + TestResult + "_ROVERLAB.pdf";
            final String inputFilePath = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/result_template.pdf";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Results/" + Filename;
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

//            out.println("Before Printing ... ");
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(345, 715); // set x and y co-ordinates
                    pdfContentByte.showText("CLIA ID :  45D2212719"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(445, 715); // set x and y co-ordinates
                    pdfContentByte.showText("Lab Director : Shiraz Pirali, MD"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(20, 685); // set x and y co-ordinates
                    pdfContentByte.showText(Name); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(205, 685); // set x and y co-ordinates
                    pdfContentByte.showText(DOB);//"DOB"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275, 685); // set x and y co-ordinates
                    pdfContentByte.showText(Gender);//"GENDER"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(345, 685); // set x and y co-ordinates
                    pdfContentByte.showText("CV22-21722");//ACCESSION#"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(409, 685); // set x and y co-ordinates
                    pdfContentByte.showText(CollectionDateTime);//"COLLECTED"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(495, 685); // set x and y co-ordinates
                    pdfContentByte.showText(ReceivedDateTime);//"RECEIVED"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(20, 655); // set x and y co-ordinates
                    pdfContentByte.showText(Physician);//"Date Of Service"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(205, 655); // set x and y co-ordinates
                    pdfContentByte.showText(TestingLocation);//"LOCATION"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(495, 655); // set x and y co-ordinates
                    pdfContentByte.showText(ReportedDateTime);//"REPORTED"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(20, 620); // set x and y co-ordinates
                    pdfContentByte.showText(TestName);//"SARS-CoV-2 RT-PCR (Aries)"); // add the text
                    pdfContentByte.endText();

                    if (!SampleType.equals("")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(20, 610); // set x and y co-ordinates
                        pdfContentByte.showText("Sample # : " + SampleType);//"SARS-CoV-2 RT-PCR (Aries)"); // add the text
                        pdfContentByte.endText();
                    }
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(20, 610); // set x and y co-ordinates
//                    pdfContentByte.showText("Sample Type: Nasopharyngeal Swab"); // add the text
//                    pdfContentByte.endText();

                    if (TestResult != null || !TestResult.equals("")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.WHITE);
                        pdfContentByte.setTextMatrix(25, 580); // set x and y co-ordinates
                        pdfContentByte.showText("Result Summary: Tests Performed, " + TestResult); // add the text
                        pdfContentByte.endText();
                    }


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(20, 545); // set x and y co-ordinates
                    pdfContentByte.showText(TestName);//"Test Name"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(230, 545); // set x and y co-ordinates
                    pdfContentByte.showText(TestResult);//"Result"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(315, 545); // set x and y co-ordinates
                    pdfContentByte.showText(TestComments);//"Comments"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(495, 545); // set x and y co-ordinates
                    pdfContentByte.showText("SEE"); // add the text
                    pdfContentByte.endText();

                }
            }
            pdfStamper.close();
            pdfReader.close();

            ps = conn.prepareStatement("UPDATE " + Database + ".Tests SET Filepath=? ,Filename=? ,FileCreatedAt=NOW() WHERE OrderId='" + O_ID + "' AND Id='" + T_ID + "'");
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
            out.println("Hello ->>> " + e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }


}