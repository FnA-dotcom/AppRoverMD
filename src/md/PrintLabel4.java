package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
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

@SuppressWarnings("Duplicates")
public class PrintLabel4 extends HttpServlet {

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
        String DirectoryName = "";
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
                DirectoryName = session.getAttribute("DirectoryName").toString();
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
                GETINPUT(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex, helper);
            } else if (ActionID.compareTo("GETINPUTVictoria") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for Victoria", FacilityIndex);
                GETINPUTVictoria(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex);
            } else if (ActionID.compareTo("GETINPUT_frontline") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for Victoria", FacilityIndex);
                GETINPUT_frontline(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex);
            } else if (ActionID.compareTo("GETINPUT_dynamo1") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for Victoria", FacilityIndex);
                GETINPUT_dynamo1(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex, DirectoryName);
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

    private void GETINPUT(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String Database, ServletContext servletContext, String UserId, int ClientId, UtilityHelper helper) {
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
        String ReasonVisit = "";
        int SelfPayChk = 0;
        String InsuranceName = "";
        String insuranceStatus = "";
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

            Query = "SELECT a.ID, a.ClientIndex, a.FirstName, a.LastName, a.MiddleInitial, " +
                    "IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''), IFNULL(a.Age,''), a.Gender, a.MRN, " +
                    "IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y'),DATE_FORMAT(a.CreatedDate,'%m/%d/%Y'))," +
                    "date_format(now(),'%Y%m%d%H%i%s'), IFNULL(a.DoctorsName, '-'),IFNULL(DATE_FORMAT(a.DOB,'%Y-%m-%d'),'') ," +
                    "IFNULL(a.ReasonVisit,''),IFNULL(a.SelfPayChk,9)," +
                    "IFNULL(LTRIM(rtrim(REPLACE(c.PayerName,'Servicing States','') )),'-') " +
                    " FROM   " + Database + ".PatientReg a " +
                    " LEFT JOIN " + Database + ".InsuranceInfo b ON a.ID = b.PatientRegId " +
                    " LEFT JOIN " + Database + ".ProfessionalPayers c ON b.PriInsuranceName = c.id  " +
                    " WHERE a.ID= '" + ID + "'";
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

                if (ClientId == 41 || ClientId == 42 || ClientId == 43) {
                    ReasonVisit = rset.getString(14);
                    SelfPayChk = rset.getInt(15);
                    InsuranceName = rset.getString(16);
                } else {
                    InsuranceName = rset.getString(14);
                }
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

            if (ClientId == 41 || ClientId == 42 || ClientId == 43) {
                if (ReasonVisit.length() > 10) {
                    ReasonVisit = ReasonVisit.substring(0, 10);
                }

                if (SelfPayChk == 1)
                    insuranceStatus = "IP";
                else if (SelfPayChk == 0)
                    insuranceStatus = "SP";
                else
                    insuranceStatus = "##";

                if (ReasonVisit.toUpperCase().contains("COVID")) {

                    if (SelfPayChk == 0) {
                        ReasonVisit = "Uninsured Covid-" + insuranceStatus;
                    } else {
                        ReasonVisit = ReasonVisit + "-" + insuranceStatus;
                    }

                } else {
                    ReasonVisit = ReasonVisit + "-" + insuranceStatus;
                }
            }
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


                        if (InsuranceName.equals("-") == false) {

                            if (InsuranceName.length() > 30)
                                InsuranceName = InsuranceName.substring(0, 30);
                            else if (InsuranceName.length() > 20)
                                InsuranceName = InsuranceName.substring(0, 20);
                            else
                                InsuranceName = InsuranceName.substring(0, InsuranceName.length());
                        }


                        if (ClientId == 41 || ClientId == 42 || ClientId == 43) {

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
                            pdfContentByte.showText("Dr. " + DoctorsName);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 710.0f);
                            pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "  Age:(" + Age + ")");
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 700.0f);
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   DOB:" + DOB);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 690.0f);
                            if (InsuranceName.equals("-")) {
                                pdfContentByte.showText("Self Pay");
                            } else {

                                if (InsuranceName.length() < 20) {
                                    pdfContentByte.showText("INS: " + InsuranceName + "");
                                } else {
                                    pdfContentByte.showText("INS: " + InsuranceName + "...");
                                }


                            }
                            pdfContentByte.endText();


//                        2nd block
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
                            pdfContentByte.showText("Dr. " + DoctorsName);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 635.0f);
                            pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "  Age:(" + Age + ")");
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 625);
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   DOB:" + DOB);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 615.0f);
                            if (InsuranceName.equals("-")) {
                                pdfContentByte.showText("Self Pay");
                            } else {
                                if (InsuranceName.length() < 20) {
                                    pdfContentByte.showText("INS: " + InsuranceName + "");
                                } else {
                                    pdfContentByte.showText("INS: " + InsuranceName + "...");
                                }

                            }
                            pdfContentByte.endText();


//                        3rd block
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
                            pdfContentByte.showText("Dr. " + DoctorsName);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 565.0f);
                            pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "  Age:(" + Age + ")");
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 555.0f);
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   DOB:" + DOB);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 545.0f);
                            if (InsuranceName.equals("-")) {
                                pdfContentByte.showText("Self Pay");
                            } else {
                                if (InsuranceName.length() < 20) {
                                    pdfContentByte.showText("INS: " + InsuranceName + "");
                                } else {
                                    pdfContentByte.showText("INS: " + InsuranceName + "...");
                                }

                            }
                            pdfContentByte.endText();


//                        4rth block
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
                            pdfContentByte.showText("Dr. " + DoctorsName);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 495.0f);
                            pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "  Age:(" + Age + ")");
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 485.0f);
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   DOB:" + DOB);

                            pdfContentByte.endText();
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 475);
                            if (InsuranceName.equals("-")) {
                                pdfContentByte.showText("Self Pay");
                            } else {
                                if (InsuranceName.length() < 20) {
                                    pdfContentByte.showText("INS: " + InsuranceName + "");
                                } else {
                                    pdfContentByte.showText("INS: " + InsuranceName + "...");
                                }

                            }
                            pdfContentByte.endText();

//                        5th block
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
                            pdfContentByte.showText("Dr. " + DoctorsName);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 420.0f);
                            pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "  Age:(" + Age + ")");
                            pdfContentByte.endText();

                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setTextMatrix((float) x, 410.0f);
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   DOB:" + DOB);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setTextMatrix((float) x, 400.0f);
                            if (InsuranceName.equals("-")) {
                                pdfContentByte.showText("Self Pay");
                            } else {
                                if (InsuranceName.length() < 20) {
                                    pdfContentByte.showText("INS: " + InsuranceName + "");
                                } else {
                                    pdfContentByte.showText("INS: " + InsuranceName + "...");
                                }

                            }
                            pdfContentByte.endText();

//                        6th block
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
                            pdfContentByte.showText("Dr. " + DoctorsName);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 350.0f);
                            pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "  Age:(" + Age + ")");
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 340.0f);
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   DOB:" + DOB);

                            pdfContentByte.endText();
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 330.0f);
                            if (InsuranceName.equals("-")) {
                                pdfContentByte.showText("Self Pay");
                            } else {
                                if (InsuranceName.length() < 20) {
                                    pdfContentByte.showText("INS: " + InsuranceName + "");
                                } else {
                                    pdfContentByte.showText("INS: " + InsuranceName + "...");
                                }

                            }
                            pdfContentByte.endText();

//                        7th block
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
                            pdfContentByte.showText("Dr. " + DoctorsName);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 275.0f);
                            pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "  Age:(" + Age + ")");
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 265.0f);
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   DOB:" + DOB);

                            pdfContentByte.endText();
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 255.0f);
                            if (InsuranceName.equals("-")) {
                                pdfContentByte.showText("Self Pay");
                            } else {
                                if (InsuranceName.length() < 20) {
                                    pdfContentByte.showText("INS: " + InsuranceName + "");
                                } else {
                                    pdfContentByte.showText("INS: " + InsuranceName + "...");
                                }

                            }
                            pdfContentByte.endText();

//                        8th block
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
                            pdfContentByte.showText("Dr. " + DoctorsName);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 205.0f);
                            pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "  Age:(" + Age + ")");
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 195.0f);
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   DOB:" + DOB);

                            pdfContentByte.endText();
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 185.0f);
                            if (InsuranceName.equals("-")) {
                                pdfContentByte.showText("Self Pay");
                            } else {
                                if (InsuranceName.length() < 20) {
                                    pdfContentByte.showText("INS: " + InsuranceName + "");
                                } else {
                                    pdfContentByte.showText("INS: " + InsuranceName + "...");
                                }

                            }
                            pdfContentByte.endText();

//                        9th block
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
                            pdfContentByte.showText("Dr. " + DoctorsName);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 130.0f);
                            pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "  Age:(" + Age + ")");
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 120.0f);
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   DOB:" + DOB);

                            pdfContentByte.endText();
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 110.0f);
                            if (InsuranceName.equals("-")) {
                                pdfContentByte.showText("Self Pay");
                            } else {
                                if (InsuranceName.length() < 20) {
                                    pdfContentByte.showText("INS: " + InsuranceName + "");
                                } else {
                                    pdfContentByte.showText("INS: " + InsuranceName + "...");
                                }

                            }
                            pdfContentByte.endText();

//                        10th block
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
                            pdfContentByte.showText("Dr. " + DoctorsName);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 60.0f);
                            pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "  Age:(" + Age + ")");
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 50.0f);
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   DOB:" + DOB);
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 40.0f);
                            pdfContentByte.showText("INS:(" + InsuranceName + ")");
                            if (InsuranceName.equals("-")) {
                                pdfContentByte.showText("Self Pay");
                            } else {
                                if (InsuranceName.length() < 20) {
                                    pdfContentByte.showText("INS: " + InsuranceName + "");
                                } else {
                                    pdfContentByte.showText("INS: " + InsuranceName + "...");
                                }

                            }
                            pdfContentByte.endText();


                        } else {
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
                            pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate);
                            pdfContentByte.endText();
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix((float) x, 700.0f);

                            pdfContentByte.showText("ACT#:" + VisitNumber + "  " + ReasonVisit);
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
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   " + ReasonVisit);
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
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   " + ReasonVisit);
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
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   " + ReasonVisit);

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
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   " + ReasonVisit);

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
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   " + ReasonVisit);

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
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   " + ReasonVisit);

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
                            pdfContentByte.showText("ACT#:" + VisitNumber + "   " + ReasonVisit);

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


//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 740.0f);
//                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 730.0f);
//                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 720.0f);
//                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 710.0f);
//                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 700.0f);
//                        pdfContentByte.showText("ACT#:" + VisitNumber + "  " + ReasonVisit);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 690.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 665.0f);
//                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 655.0f);
//                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 645.0f);
//                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 635.0f);
//                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 625);
//                        pdfContentByte.showText("ACT#:" + VisitNumber + "   " + ReasonVisit);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 615.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 595.0f);
//                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 585.0f);
//                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 575.0f);
//                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 565.0f);
//                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 555.0f);
//                        pdfContentByte.showText("ACT#:" + VisitNumber + "   " + ReasonVisit);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 545.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 525.0f);
//                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 515.0f);
//                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 505.0f);
//                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 495.0f);
//                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 485.0f);
//                        pdfContentByte.showText("ACT#:" + VisitNumber + "   " + ReasonVisit);
//
//                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 475);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 450.0f);
//                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 440.0f);
//                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 430.0f);
//                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 420.0f);
//                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setTextMatrix((float) x, 410.0f);
//                        pdfContentByte.showText("ACT#: " + VisitNumber);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setTextMatrix((float) x, 400.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 380.0f);
//                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 370.0f);
//                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 360.0f);
//                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 350.0f);
//                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 340.0f);
//                        pdfContentByte.showText("ACT#:" + VisitNumber + "   " + ReasonVisit);
//
//                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 330.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 305.0f);
//                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 295.0f);
//                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 285.0f);
//                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 275.0f);
//                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 265.0f);
//                        pdfContentByte.showText("ACT#:" + VisitNumber + "   " + ReasonVisit);
//
//                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 255.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 235.0f);
//                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 225.0f);
//                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 215.0f);
//                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 205.0f);
//                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 195.0f);
//                        pdfContentByte.showText("ACT#:" + VisitNumber + "   " + ReasonVisit);
//
//                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 185.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 160.0f);
//                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 150.0f);
//                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 140.0f);
//                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 130.0f);
//                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 120.0f);
//                        pdfContentByte.showText("ACT#:" + VisitNumber + "   " + ReasonVisit);
//
//                        pdfContentByte.endText();
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 110.0f);
//                        pdfContentByte.showText("Dr. " + DoctorsName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 90.0f);
//                        pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 80.0f);
//                        pdfContentByte.showText(ClientName + "  Sex: " + Gender);
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 70.0f);
//                        pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 60.0f);
//                        pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
//                        pdfContentByte.endText();
//
//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix((float) x, 50.0f);
//                        pdfContentByte.showText("ACT#: " + VisitNumber);
//                        pdfContentByte.endText();
//
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

        } catch (Exception e) {
            System.out.println("in the catch exception of GetReport Function ");
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println(str);
        }

//        catch (Exception ex2) {
//            try {
//                System.out.println(ex2.getMessage());
//                Services.DumException("GETINPUT", "Printlabel4 ", request, ex2);
//
//                Parsehtm Parser = new Parsehtm(request);
//                Parser.SetField("FormName", "PatientUpdateInfo");
//                Parser.SetField("ActionID", "GetInput&ID=" + ID);
//                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
//            } catch (Exception e) {
//            }
////            String str = "";
////            out.println(ex2.getMessage());
////            for (int j = 0; j < ex2.getStackTrace().length; ++j) {
////                str = str + ex2.getStackTrace()[j] + "<br>";
////            }
////            out.println(str);
//        }
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
            Query = "SELECT ID, ClientIndex, FirstName, LastName, MiddleInitial, " +
                    "DATE_FORMAT(DOB,'%m/%d/%Y'), Age, Gender, MRN, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T')," +
                    "DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')),date_format(now(),'%Y%m%d%H%i%s'), IFNULL(DoctorsName, '-') " +
                    "FROM   " + Database + ".PatientReg where ID= '" + ID + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
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
                Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) " +
                        "from " + Database + ".DoctorsList where Id = " + DoctorsId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
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

    private void GETINPUT_dynamo1(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final String Database, final ServletContext servletContext, final String UserId, final int ClientId, String DirectoryName) {
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
//        String DirectoryName = "";
        String insuranceStatus = "-";
        int SelfPayChk = 0;
        String ReasonVisit = "";
        String InsuranceName = "";
        String VisitNumber = "";
        try {
//            if (ClientId == 8) {
//                DirectoryName = "Orange";
//            } else if (ClientId == 9) {
//                DirectoryName = "Victoria";
//            } else if (ClientId == 10) {
//                DirectoryName = "Odessa";
//            } else if (ClientId == 12) {
//                DirectoryName = "SAustin";
//            } else if (ClientId == 15) {
//                DirectoryName = "Sublime";
//            }

//            Query = "SELECT a.ID, a.ClientIndex, a.FirstName, a.LastName, a.MiddleInitial, DATE_FORMAT(a.DOB,'%m/%d/%Y'), a.Age,"
//            		+ " a.Gender, a.MRN, IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y'), "
//            		+ "DATE_FORMAT(a.CreatedDate,'%m/%d/%Y')),date_format(now(),'%Y%m%d%H%i%s'),"
//            		+ " IFNULL(a.DoctorsName, '-'),IFNULL(a.SelfPayChk,9),IFNULL(a.ReasonVisit,''),"
//            		+ " IFNULL(LTRIM(rtrim(REPLACE(c.PayerName,'Servicing States','') )),'') FROM   " + Database + ".PatientReg a "
//            		+ "INNER JOIN \"+Database + \".InsuranceInfo b ON a.ID = b.PatientRegId INNER JOIN \"+Database + \".ProfessionalPayers c "
//            		+ "ON b.PriInsuranceName = c.Id where a.ID= '" + ID + "'";
            Query = "SELECT IFNULL(a.ID,''),IFNULL(a.ClientIndex,''),IFNULL(a.FirstName,''),IFNULL(a.LastName,'') ,IFNULL(a.MiddleInitial,'') , DATE_FORMAT(a.DOB,'%m/%d/%Y'),IFNULL( a.Age,'') ,\r\n"
                    + "         IFNULL(  a.Gender,'')   ,IFNULL( a.MRN,''), IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y'), \r\n"
                    + "            		DATE_FORMAT(a.CreatedDate,'%m/%d/%Y')),date_format(now(),'%Y%m%d%H%i%s'),\r\n"
                    + "            		 IFNULL(a.DoctorsName, ' '),IFNULL(a.SelfPayChk,9),IFNULL(a.ReasonVisit,''),\r\n"
                    + "            	IFNULL(LTRIM(rtrim(REPLACE(c.PayerName,'Servicing States','') )),'') FROM   " + Database + ".PatientReg a \r\n"
                    + "            		LEFT JOIN  " + Database + ".InsuranceInfo b ON a.ID = b.PatientRegId LEFT JOIN " + Database + ".ProfessionalPayers c \r\n"
                    + "            		ON b.PriInsuranceName = c.Id where a.ID= '" + ID + "'";

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
                SelfPayChk = rset.getInt(13);
                ReasonVisit = rset.getString(14);
                InsuranceName = rset.getString(15);
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


            Query = "Select MAX(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                VisitNumber = rset.getString(1).trim();
            }
            rset.close();
            stmt.close();

            VisitNumber = "VN-" + MRN + "-" + VisitNumber;


            if (SelfPayChk == 1)
                insuranceStatus = "IP";
            else if (SelfPayChk == 0)
                insuranceStatus = "SP";
            else
                insuranceStatus = "##";


            if (ReasonVisit.toUpperCase().contains("COVID")) {

                if (SelfPayChk == 0) {
                    ReasonVisit = "Uninsured Covid-" + insuranceStatus;
                } else {
                    ReasonVisit = ReasonVisit + "-" + insuranceStatus;
                }

            } else {

                ReasonVisit = ReasonVisit + "-" + insuranceStatus;
            }


        } catch (Exception ex3) {
        }
        try {

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
                    pdfContentByte.showText(String.valueOf(LastName) + ", " + FirstName + " (ACT:" + VisitNumber + ")");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 70.0f);
                    pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ") Sex: " + Gender);
                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(40.0f, 60.0f);
//                    pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ") - " + insuranceStatus);
//                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 60.0f);
                    pdfContentByte.showText("MRN:" + MRN + "  DOS:" + CreatedDate + "");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 50.0f);
                    pdfContentByte.showText("Dr. " + DoctorsName);
                    pdfContentByte.endText();

//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(40.0f, 40.0f);
//                    pdfContentByte.showText("RV:" + ReasonVisit);
//                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 40.0f);
                    pdfContentByte.showText(InsuranceName);
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
            System.out.println(str);
        }
    }
}