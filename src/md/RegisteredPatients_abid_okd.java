//
// Decompiled by Procyon v0.5.36
//

package md;

import Parsehtm.Parsehtm;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//import static oracle.net.aso.C08.e;

public class RegisteredPatients_abid_okd extends HttpServlet {
    static String DOS = "";
    static String Acct = "";
    static String printabledate = "";

    private static void copyFileUsingJava7Files(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }

    private static String datainsert_new(String filename, String dosdate, String acc, String epowertime, String path, String firstname, String lastname, String mrn, String client, Connection conn) {
        String success = "0";
        try {

            PreparedStatement MainReceipt1 = conn
                    .prepareStatement(" Insert IGNORE into oe.filelogs_sftp_test3 (target,clientdirectory,filename,acc,dosdate,epowertime,entrydate,firstname,lastname,mrn)" +
                            " values (?,?,?,?,?,?,now(),?,?,?) ");

            String temp[] = path.split("/");

            // 2019 03 11 17 52
            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmm"); // first example
            SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmm"); // second example

            Date d1 = format1.parse(dosdate);
            Date d2 = format2.parse(epowertime);

            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            String date = simpleDateFormat.format(d1);
            String date1 = simpleDateFormat.format(d2);
            System.out.println(date);
            System.out.println(date1);


            MainReceipt1.setString(1, path);
            MainReceipt1.setString(2, client);
            MainReceipt1.setString(3, filename);
            MainReceipt1.setString(4, acc);
            MainReceipt1.setString(5, dosdate);
            MainReceipt1.setString(6, epowertime);
            MainReceipt1.setString(7, firstname);
            MainReceipt1.setString(8, lastname);
            MainReceipt1.setString(9, mrn);

            MainReceipt1.executeUpdate();
            MainReceipt1.close();
            conn.close();
            success = "1";

        } catch (Exception ee) {
            System.out.println(ee.getLocalizedMessage());
            success = "0";
        }
        return success;

    }

    public static boolean isStringOnlyAlphabet(String str) {
        return ((str != null) && (!str.equals("")) && (str.matches("^[a-zA-Z]*$")));
    }

    private static boolean ReadPdfGetData(String FileName, String Path) {

        try {
            DOS = "";
            Acct = "";
            printabledate = "";

            try (PDDocument document = PDDocument.load(new File(Path + "/" + FileName))) {
                //document.getPage(0);
                document.getClass();

                if (!document.isEncrypted()) {

                    PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                    stripper.setSortByPosition(true);

                    PDFTextStripper tStripper = new PDFTextStripper();
                    tStripper.getStartPage();

                    String pdfFileInText = tStripper.getText(document);
//                System.out.println("pdfFileInText: " + pdfFileInText);
                    int ii = 0;
                    int iChComplaint = 0;
                    // split by whitespace
                    String lines[] = pdfFileInText.split("\\r?\\n");

                    for (String line : lines) {


                        if (line.startsWith("Acct #:")) {
                            String AcctRaw = line;
                            String AcctArr[] = AcctRaw.split("\\s+");
                            Acct = AcctArr[2];
                            String printabledateRaw = AcctArr[4];
                            String printabledateArr[] = printabledateRaw.split("\\/");
                            printabledate = printabledateArr[2] + "-" + printabledateArr[0] + "-" + printabledateArr[1] + " " + AcctArr[5] + ":00";
                        }
                        if (line.startsWith("DOS:")) {
                            String DOSRaw = line;
                            String DOSArr[] = DOSRaw.split("\\s+");
                            DOS = DOSArr[1];
                            String DOSFormatArr[] = DOS.split("\\/");
                            DOS = DOSFormatArr[2] + "-" + DOSFormatArr[0] + "-" + DOSFormatArr[1] + " " + DOSArr[2] + ":00";
                        }

                    }
                } else {
                    return false;

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
        Connection conn = null;
        ResultSet rset = null;
        Statement stmt = null;
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        String Database = "";
        String Query = "";
        int ClientId = 0;
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();
//    conn = Services.getMysqlConn();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        try {
            final Cookie[] cookies = request.getCookies();
            UserId = (Zone = (Passwd = ""));
            String UserName = "";
            final int checkCookie = 0;
            for (int coky = 0; coky < cookies.length; ++coky) {
                final String cName = cookies[coky].getName();
                final String cValue = cookies[coky].getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
                }
                if (cName.equals("username")) {
                    UserName = cValue;
                }
            }
            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientId = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select dbname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();

            //System.out.println(Database);

//      if (ClientId == 8) {
//        Database = "oe_2";
//      }
//      else if (ClientId == 9) {
//        Database = "victoria";
//      }
//      else if (ClientId == 10) {
//        Database = "oddasa";
//      }
            if (ActionID.equals("GetValues")) {
                this.GetValues(request, out, conn, context);
            } else if (ActionID.equals("PatientsDocUpload")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs Upload", "Click on Upload Docs Option", ClientId);
                this.PatientsDocUpload(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("ShowReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View Patient Option", "Click on View Patient Option Patients List", ClientId);
                this.ShowReport(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("PatientsDocUpload_Save") && request.getContentType().startsWith("multipart/form-data")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs Upload Save", "Save the Documents for the selected patients", ClientId);
                this.PatientsDocUpload_Save(request, out, conn, context, response, Database, ClientId);
            } else if (ActionID.equals("ViewDocuments_Input")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs View", "Click on View Documents Options", ClientId);
                this.ViewDocuments_Input(request, out, conn, context, response, UserId, Database, ClientId);
            } else if (ActionID.equals("ViewDocuments")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs View", "Docmuents Uploaded List", ClientId);
                this.ViewDocuments(request, out, conn, context, response, UserId, Database, ClientId);
            } else if (ActionID.equals("CreateInvoiceInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Create Invoice Option", "Invoice Craetion Add Diseases", ClientId);
                this.CreateInvoiceInput(request, out, conn, context, response, UserId, Database, ClientId);
            } else if (ActionID.equals("GetCost")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Create Invoice Option", "Get the cost of the Diseases and Fill input fields", ClientId);
                this.GetCost(request, out, conn, context, response, UserId, Database, ClientId);
            } else if (ActionID.equals("SaveInvoice")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Invoice ", "Save the Created Invoice Data in Tables and Details", ClientId);
                this.SaveInvoice(request, out, conn, context, response, UserId, Database, ClientId);
            } else if (ActionID.equals("InvoicePdf")) {
                this.InvoicePdf(request, response, out, conn, UserId, Database, ClientId);
            } else if (ActionID.equals("CollectPayment")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Payement Collection Option", "Search the Invoice and Then Collect Payment Option", ClientId);
                this.CollectPayment(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("CollectPayment_View")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Payement Collection Option", "View the List of Created Invoice Of selected Patient", ClientId);
                this.CollectPayment_View(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("PayNow")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Pay Now Option", "Pay Now Option for the Created Invoice for Selected Pateint", ClientId);
                this.PayNow(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("PayNowSave")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Pay Now Option", "Save the Details Of Paid Amount for the Invoice Created for the Selected Patient", ClientId);
                this.PayNowSave(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("DeActivePatient")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "DeActivate Patients", "Deactivate the Selected Patients from the View Patient Option and Search Old Patient Option", ClientId);
                this.DeActivePatient(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("ReActivePatient")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "ReActivate Patients", "ReActivate the Selected Patients from the Search Old Patient Option", ClientId);
                this.ReActivePatient(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("DeActiveInvoice")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "DeActivate Invoice Option", "Deactivate the selected Invoice from The Collect Payment Option Where you can Search Invoice for the Respentive Patient", ClientId);
                this.DeActiveInvoice(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("TransactionReport_Input")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Transaction Report Option", "Transaction Report for the Selected Patient Input Option", ClientId);
                this.TransactionReport_Input(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("TransactionReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Transaction Report Option", "View Transaction Report List", ClientId);
                this.TransactionReport(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("AddDoctors")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Doctors Physicians", "Click on Add Doctor Physicians Option", ClientId);
                this.AddDoctors(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("SaveDoctor")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Doctors Physicians", "Save Doctor or Physician", ClientId);
                this.SaveDoctor(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("EditDoctor")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Edit Doctors Physicians", "Edit Doctor or Physician", ClientId);
                this.EditDoctor(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("AddAlergyInfo")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Allergy Information", "Add Allergy Options Input", ClientId);
                this.AddAlergyInfo(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("SaveAllergy")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Allergy Information", "Save Allergy Options And Other Information", ClientId);
                this.SaveAllergy(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("ShowHistory")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Show History for Allergy and Other Info", "Show Pdf for the Recent Allergy Information or any Other Info form View Patients Option or Search Old Patients Option", ClientId);
                this.ShowHistory(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("GetPatients")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patients List", "Get Patients List matching from the Fields Input", ClientId);
                this.GetPatients(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("GetPatientsEligibility")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patients List For Eligibility Inquiry", "Get Patients List matching from the Fields Input For Eligibility Inquiry", ClientId);
                this.GetPatientsEligibility(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("GetPatientsUploadDocs")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patients List For Docs Upload", "Get Patients List matching from the Fields Input For Docs Upload", ClientId);
                this.GetPatientsUploadDocs(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("GetProfessionalPayersList")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Professional Payer List For Eligbility Check", "Get Professional Payer LIst matching from the Fields Input For Eligbility Check", ClientId);
                this.GetProfessionalPayersList(request, response, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("RegisteredPatientListExcel")) {
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=RoverPatientsList.xls");
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "DownloadExcelReport", "Click on Excel Report of All Patients List", ClientId);
                this.RegisteredPatientListExcel(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("DownloadPatientInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Admin Report for Patients", "Download Admin Report For Patients", ClientId);
                this.DownloadPatientInput(request, out, conn, context, response, UserId, Database, ClientId);
            } else if (ActionID.equals("download_direct")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View Documents Uplaoded", "View Uploaded Docs", ClientId);
                this.download_direct(request, response, out, conn);
            } else if (ActionID.equals("DownloadPatientList")) {
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=" + UserId + "_RoverPatientsList.xls");
                supp.Dologing(UserName, conn, request.getRemoteAddr(), ActionID, "DownloadExcelReport", "Download Admin Report Excel Option", ClientId);
                this.DownloadPatientList(request, out, conn, context, UserId, Database, ClientId);
            } else {
                out.println("Under Development");
            }
            try {
                conn.close();
            } catch (Exception ex) {
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    void GetValues(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext) {
        try {
            final Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Dashboards/DashBoardInput.html");
        } catch (Exception ex) {
        }
    }

    void ShowReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientCount = 0;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        final StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        try {
            Query = "SELECT COUNT(*) FROM " + Database + ".PatientReg";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientCount = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            if (PatientCount > 0) {
                Query = "SELECT CONCAT(IFNULL(Title,''),' ',FirstName,' ',MiddleInitial,' ',LastName), DATE_FORMAT(DOB,'%m/%d/%Y'), PhNumber, IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'SUSPECTED' ELSE 'UN-EXAMINED' END FROM " + Database + ".PatientReg where Status = 0 ORDER BY ID DESC limit 300";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                    if (ClientId == 8) {
                        CDRList.append("<td align=left><a href=/md/md.PrintLabel4?ActionID=GETINPUT&ID=" + rset.getInt(6) + ">Print Label</a></td>\n");
                        CDRList.append("<td align=left><a href=/md/md.DownloadBundle_OLD_15042021?ActionID=GETINPUT&ID=" + rset.getInt(6) + ">Downlaod Admission Bundle</a></td>\n");
                    } else if (ClientId == 9) {
                        CDRList.append("<td align=left><a href=/md/md.PrintLabel4?ActionID=GETINPUTVictoria&ID=" + rset.getInt(6) + ">Print Label</a></td>\n");
                        CDRList.append("<td align=left><a href=/md/md.DownloadBundle_OLD_15042021?ActionID=GETINPUTVictoria&ID=" + rset.getInt(6) + ">Downlaod Admission Bundle</a></td>\n");
                    } else if (ClientId == 10) {
                        CDRList.append("<td align=left><a href=/md/md.PrintLabel4?ActionID=GETINPUT&ID=" + rset.getInt(6) + ">Print Label</a></td>\n");
                        CDRList.append("<td align=left><a href=/md/md.DownloadBundle_OLD_15042021?ActionID=GETINPUTOddasa&ID=" + rset.getInt(6) + ">Downlaod Admission Bundle</a></td>\n");
                    } else if (ClientId == 12) {
                        CDRList.append("<td align=left><a href=/md/md.PrintLabel4?ActionID=GETINPUT&ID=" + rset.getInt(6) + ">Print Label</a></td>\n");
                        CDRList.append("<td align=left><a href=/md/md.DownloadBundle_OLD_15042021?ActionID=GETINPUTSAustin&ID=" + rset.getInt(6) + ">Downlaod Admission Bundle</a></td>\n");
                    }
                    CDRList.append("<td align=left><a href=/md/md.RegisteredPatients_abid_okd?ActionID=ShowHistory&ID=" + rset.getInt(6) + ">Show History</a></td>\n");
//          CDRList.append("<td align=left><a href=/md/md.RegisteredPatients_abid_okd?ActionID=UpdateAgeVictoria>Show History</a></td>\n");
//          CDRList.append("<td align=left><a href=/md/md.RegisteredPatients_abid_okd?ActionID=EnterDateinPatientVisit>Show History</a></td>\n");
                    if (ClientId == 9) {
                        CDRList.append("<td align=left><a href=/md/md.PatientReg2?ActionID=EditValues&MRN=" + rset.getString(4).trim() + "&ClientId=" + ClientId + ">View/Edit</a></td>\n");
                    } else {
                        CDRList.append("<td align=left><a href=/md/md.PatientReg?ActionID=EditValues&MRN='" + rset.getString(4).trim() + "'&ClientId=" + ClientId + ">View/Edit</a></td>\n");
                    }
                    CDRList.append("<td align=left><a href=/md/md.PatientInfo?ActionID=GetValues&ID=" + rset.getInt(6) + ">Send to E-Doc</a></td>\n");
                    CDRList.append("<td align=left><a href=/md/md.RegisteredPatients_abid_okd?ActionID=DeActivePatient&ID=" + rset.getInt(6) + ">De-Activate Patient</a></td>\n");
//          CDRList.append("<td align=left><a href=/md/md.RegisteredPatients_abid_okd?ActionID=ReActivePatient&ID=" + rset.getInt(6) + ">Re-Activate Patient</a></td>\n");
                    CDRList.append("</tr>");
                    ++SNo;
                }
                rset.close();
                stmt.close();
            }
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowRegisteredPatient.html");
        } catch (Exception var11) {
            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
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

    void PatientsDocUpload(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            SupportiveMethods suppMethods = new SupportiveMethods();
            StringBuffer LeftSideBarMenu = new StringBuffer();
            StringBuffer Header = new StringBuffer();
            StringBuffer Footer = new StringBuffer();
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            final StringBuffer PatientList = new StringBuffer();
            Query = "SELECT CONCAT(ID,',',MRN,',',Title,' ',FirstName,' ',MiddleInitial,' ',LastName), CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), MRN FROM " + Database + ".PatientReg where Status = 0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option class=Inner value=-1>Select Patient</option>");
            while (rset.next()) {
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ")</option>");
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/UploadPatientsDocs.html");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void PatientsDocUpload_Save(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String PatientId = "";
        String DocumentName = "";
        String UserId = "";
        String firstname = "";
        String lastname = "";
        String Client = "";
        int PremisisId = 0;
        int clientdirectory = 0;
        int PatientRegId = 0;
        String PatientMRN = "";
        String PatientName = "";
        String DirectoryName = "";
        String DocumentType = "";
        String target = "";
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
//    final String Path = "/opt/apache-tomcat-7.0.65/webapps/orange_2/Attachment";
        final String Path = "/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "";
        final String UploadPath = String.valueOf(String.valueOf(Path)) + "/";
        boolean FileFound = false;
        byte[] Data = null;
        String key = "";
        String FileName = "";
        try {
            final Dictionary d = this.doUpload(request, response, out);
            final Enumeration en = d.keys();
            while (en.hasMoreElements()) {
                key = (String) en.nextElement();
                if (key.endsWith(".jpg") || key.endsWith(".png") || key.endsWith(".pdf") || key.endsWith(".txt") || key.endsWith(".csv") || key.endsWith(".doc") || key.endsWith(".xlsx")) {
                    FileName = key;
                    FileFound = true;
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                }
                if (key.toUpperCase().endsWith(".jpg") || key.endsWith(".png") || key.endsWith(".PDF") || key.endsWith(".txt") || key.endsWith(".csv") || key.endsWith(".doc") || key.endsWith(".xlsx")) {
                    FileName = key;
                    FileFound = true;
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                } else if (key.startsWith("PatientId")) {
                    PatientId = (String) d.get(key);
                } else if (key.startsWith("DocumentName")) {
                    DocumentName = (String) d.get(key);
                } else if (key.startsWith("UserId")) {
                    UserId = (String) d.get(key);
                }
//        else if (key.startsWith("Client")) {
//          Client = (String) d.get(key);
//        }
                else if (key.startsWith("DocumentType")) {
                    DocumentType = (String) d.get(key);
                }
                if (FileFound) {
                    System.out.println(FileName);


                    FileName = FileName.replaceAll("\\s+", "");
                    final File fe = new File(String.valueOf(String.valueOf(UploadPath)) + FileName);
                    if (fe.exists()) {
                        fe.delete();
                    }
                    final FileOutputStream fouts = new FileOutputStream(fe);
                    fouts.write(Data);
                    fouts.flush();
                    fouts.close();
                }
            }
            key = (PatientId = PatientId.substring(4));
            key = (DocumentName = DocumentName.substring(4));
            key = (UserId = UserId.substring(4));
            //key = (Client = Client.substring(4));
            System.out.println("}}}]" + PatientId);
            final String[] PatientInfo = PatientId.split("\\,");

            PatientRegId = Integer.parseInt(PatientInfo[0]);
            PatientMRN = PatientInfo[1];
            PatientName = PatientInfo[2];
            try {
                Query = "Select clientid from oe.sysusers where ltrim(rtrim(UPPER(userid))) = ltrim(rtrim(UPPER('" + UserId + "')))";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    PremisisId = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Client Id get (PremisisID)" + e.getMessage());
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement("Insert into " + Database + ".PatientDocUpload (PremisisId, PatientRegId, PatientMRN, PatientName,"
                        + " UploadDocumentName, FileName, CreatedBy, CreatedDate) values (?,?,?,?,?,?,?,now()) ");
                MainReceipt.setInt(1, PremisisId);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, PatientMRN);
                MainReceipt.setString(4, PatientName);
                MainReceipt.setString(5, DocumentName);
                MainReceipt.setString(6, FileName);
                MainReceipt.setString(7, UserId);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error in Insertion:-" + e.getMessage());
            }

            DocumentType = DocumentType.substring(4);
            out.println("DocumentType " + DocumentType);

            if (DocumentType.compareTo("1") == 0) {
                Query = "SELECT directory_1 FROM oe.clients WHERE Id = " + PremisisId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    target = rset.getString(1);
                }
                rset.close();
                stmt.close();

                File source = new File(String.valueOf(String.valueOf(UploadPath)) + FileName);
                File dest = new File(String.valueOf(target + FileName));
                copyFileUsingJava7Files(source, dest);

                if (PremisisId == 9) {
                    clientdirectory = 14;
                } else if (PremisisId == 10) {
                    clientdirectory = 13;
                } else {
                    clientdirectory = PremisisId;
                }
                boolean readable = false;
                readable = ReadPdfGetData(FileName, target);
                if (readable) {
                    Query = "Select ifnull(FirstName,''), ifnull(LastName,'') from " + Database + ".PatientReg where MRN = '" + PatientMRN + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        firstname = rset.getString(1);
                        lastname = rset.getString(2);
                    }
                    rset.close();
                    stmt.close();
                }
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + "oe" + ".filelogs_sftp (target, entrydate, clientdirectory, filename , acc, " +
                            "dosdate, epowertime, processed, processby, filestatus, liststatus, " +
                            "firstname, lastname, MRN) VALUES (?,now(),?,?,?,?,?,0,0,0,0,?,?,?) ");
                    MainReceipt.setString(1, target);
                    MainReceipt.setInt(2, clientdirectory);
                    MainReceipt.setString(3, FileName);
                    MainReceipt.setString(4, Acct);
                    MainReceipt.setString(5, DOS);
                    MainReceipt.setString(6, printabledate);
                    MainReceipt.setString(7, firstname);
                    MainReceipt.setString(8, lastname);
                    MainReceipt.setString(9, PatientMRN);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception e) {
                    out.println("Error in Insertion:-" + e.getMessage());
                }

//    	 String cc = DataInsertfilelogs_sftp(FileName, dosdate, acc, epowertime, path, firstname, lastname, PatientMRN, ClientId, conn);
            }

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message_fileUpload", String.valueOf("File Has been Uploaded Succesfully"));
            Parser.SetField("FormName", String.valueOf("RegisteredPatients_abid_okd"));
            Parser.SetField("ActionID", String.valueOf("PatientsDocUpload"));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Success.html");
        } catch (Exception e2) {
            out.println("Error in Upload DOcuments!!" + e2.getMessage());
            String str = "";
            for (int i = 0; i < e2.getStackTrace().length; ++i) {
                str = str + e2.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    void ViewDocuments_Input(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        final StringBuffer PatientList = new StringBuffer();
        try {
            Query = "SELECT ID, CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), MRN FROM " + Database + ".PatientReg where Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ")</option>");
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/ViewDocuments.html");
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

    void ViewDocuments(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        final String PatientName = "";
        final String PatientMRN = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        final StringBuffer CDRList = new StringBuffer();
        final StringBuffer PatientList = new StringBuffer();
        int SNo = 1;
        String DirectoryName = "";
        String DocumentPath = "";
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
        DocumentPath = "/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "/";
        try {
            PatientRegId = Integer.parseInt(request.getParameter("PatientId").trim());
            Query = "SELECT ID, CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), MRN FROM " + Database + ".PatientReg where Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ")</option>");
            }
            rset.close();
            stmt.close();
            Query = "SELECT a.PatientMRN, a.PatientName, a.UploadDocumentName, a.FileName, DATE_FORMAT(b.CreatedDate,'%m/%d/%Y') FROM " + Database + ".PatientDocUpload a " + " LEFT JOIN " + Database + ".PatientReg b ON a.PatientRegId = b.ID WHERE a.PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                //DocumentPath = "/sftpdrive/AdmissionBundlePdf/Attachment/"+DirectoryName;
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left><a href=https://app.rovermd.com:8443/md/md.RegisteredPatients_abid_okd?ActionID=download_direct&fname=" + rset.getString(4) + "&path=" + DocumentPath + " target=\"_blank\">" + rset.getString(4) + "</a></td>\n");
                CDRList.append("</tr>");
                ++SNo;
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ViewDocuments.html");
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

    void CreateInvoiceInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        final StringBuffer diseaseList = new StringBuffer();
        final StringBuffer PatientList = new StringBuffer();
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        try {
            Query = "SELECT ID, CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), MRN, DATE_FORMAT(DOB,'%d_%m-%Y') FROM " + Database + ".PatientReg where Status = 0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientList.append("<option class=Inner value=\"" + rset.getString(3) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ") (" + rset.getString(4) + ")</option>");
            }
            rset.close();
            stmt.close();
            Query = "SELECT Id, CONCAT('(',Catagory, ') ', Description) FROM " + Database + ".SelfPaySheet";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            diseaseList.append("<option class=Inner value='-1'>Select Disease </option>");
            while (rset.next()) {
                diseaseList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("diseaseList", String.valueOf(diseaseList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/InvoiceSheetInput.html");
        } catch (Exception var11) {
            Services.DumException("Invoice Sheet Input", "CreateInvoice RegisteredPatient ", request, var11);
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

    void GetCost(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Cost = "";
        final int DiseaseId = Integer.parseInt(request.getParameter("DiseaseId").trim());
        try {
            Query = "Select Cost from " + Database + ".SelfPaySheet where Id = " + DiseaseId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Cost = rset.getString(1);
            }
            rset.close();
            stmt.close();
            out.println(Cost.toString());
        } catch (Exception var11) {
            out.println(var11.getMessage());
            out.flush();
            out.close();
        }
    }

    void SaveInvoice(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        double TotalAmount = 0.0;
        double CostPerDiseases = 0.0;
        int i = 0;
        int j = 0;
        int k = 0;
        String InvoiceNo = "";
        int InvoiceMasterId = 0;
        final String PatientString = request.getParameter("PatientString").trim();
        final String PatientMRN = request.getParameter("PatientMRN").trim();
        try {
            String[] myInfo1 = new String[0];
            myInfo1 = PatientString.split("\\^");
            final int RowCount = Integer.parseInt(myInfo1[1].substring(myInfo1[1].indexOf("=") + 1));
            final String[][] InvoiceTableInput = new String[RowCount][5];
            for (i = (j = (k = 0)), i = 2; i < myInfo1.length; ++i) {
                if (myInfo1[i].length() > 0) {
                    InvoiceTableInput[k][j] = myInfo1[i].substring(myInfo1[i].indexOf("=") + 1);
                    if (++j > 4) {
                        j = 0;
                        ++k;
                    }
                }
            }
            for (i = 0; i < RowCount; ++i) {
                TotalAmount += Double.parseDouble(InvoiceTableInput[i][2]);
            }
            try {
                Query = "SELECT IFNULL(MAX(Id),0) + 1, DATE_FORMAT(now(),'%y-%m-%d') FROM " + Database + ".InvoiceMaster";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    InvoiceNo = "Inv_" + rset.getString(2) + "_" + rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error 1-  in Getting Invoice No: " + e.getMessage());
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".InvoiceMaster (PatientMRN,InvoiceNo,TotalAmount ,PaidAmount,Paid," + "PaymentDateTime,InvoiceCreatedBy ,CreatedDate, Status, BalAmount) \n" + "VALUES (?,?,?,?,?,?,?,now(),0,?) ");
                MainReceipt.setString(1, PatientMRN);
                MainReceipt.setString(2, InvoiceNo);
                MainReceipt.setDouble(3, TotalAmount);
                MainReceipt.setDouble(4, 0.0);
                MainReceipt.setInt(5, 0);
                MainReceipt.setString(6, "0000-00-00");
                MainReceipt.setString(7, UserId);
                MainReceipt.setDouble(8, TotalAmount);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                System.out.println("Error 2- Insertion InvoiceMaster Table :" + e.getMessage());
            }
            try {
                Query = "SELECT max(Id) FROM " + Database + ".InvoiceMaster";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    InvoiceMasterId = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error 3- in Getting InvoiceMasterID: " + e.getMessage());
            }
            try {
                for (i = 0; i < RowCount; ++i) {
                    Query = "Select Cost from " + Database + ".SelfPaySheet where Id = " + Integer.parseInt(InvoiceTableInput[i][4]);
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        CostPerDiseases = rset.getDouble(1);
                    }
                    rset.close();
                    stmt.close();
                    final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".InvoiceDetail (InvoiceMasterId,PatientMRN,InvoiceNo ,DiseaseId,CostPerDisease," + "Quantity,CreatedDate ,CreatedBy) \n" + "VALUES (?,?,?,?,?,?,now(),?) ");
                    MainReceipt.setInt(1, InvoiceMasterId);
                    MainReceipt.setString(2, InvoiceTableInput[i][0]);
                    MainReceipt.setString(3, InvoiceNo);
                    MainReceipt.setString(4, InvoiceTableInput[i][4]);
                    MainReceipt.setDouble(5, CostPerDiseases);
                    MainReceipt.setInt(6, Integer.parseInt(InvoiceTableInput[i][3]));
                    MainReceipt.setString(7, UserId);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                }
            } catch (Exception e) {
                System.out.println("Error 4- Insertion InvoiceDetail Table :" + e.getMessage());
            }
            out.println("1");
        } catch (Exception var11) {
            out.println("Error: " + var11.getMessage());
            String str = "";
            for (i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
    }

    private void InvoicePdf(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final String UserId, final String Database, final int ClientId) {
        final String PatientMRN = request.getParameter("PatientMRN");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String ClientName = "";
        String PatientName = "";
        String DOB = "";
        String Age = "";
        String Sex = "";
        String DOS = "";
        final String CreatedDate = null;
        String InvoiceNo = "";
        double TotalAmount = 0.0;
        double PaidAmount = 0.0;
        String InvoiceCreatedDate = "";
        int Sno = 0;
        final String FileName = "";
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
            }

            final Font MainHeading = new Font(2, 12.0f, 1, new Color(0, 0, 0));
            final Font normfont = new Font(2, 8.0f, 0, new Color(0, 0, 0));
            final Font normfont2 = new Font(2, 10.0f, 0, new Color(0, 0, 0));
            final Font normfont3 = new Font(2, 12.0f, 0, new Color(0, 0, 0));
            final Font UnderLine = new Font(2, 12.0f, 4, new Color(0, 0, 0));
            try {
                Query = "Select InvoiceNo, TotalAmount, PaidAmount, DATE_FORMAT(CreatedDate,'%m/%d/%Y') from " + Database + ".InvoiceMaster where PatientMRN = '" + PatientMRN + "' order by Id desc limit 1";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    InvoiceNo = rset.getString(1);
                    TotalAmount = rset.getDouble(2);
                    PaidAmount = rset.getDouble(3);
                    InvoiceCreatedDate = rset.getString(4);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error in 1:-" + e.getMessage());
            }
            final Document document = new Document(PageSize.A4, 0.0f, 0.0f, 70.0f, 30.0f);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, (OutputStream) baos);
            PdfWriter.getInstance(document, new FileOutputStream("/sftpdrive/AdmissionBundlePdf/Invoices/" + DirectoryName + "/" + InvoiceNo + "_" + PatientMRN + ".pdf"));

            document.addAuthor("Golden Triangle");
            document.addSubject("Customer Invoice");
            document.addCreationDate();
            final Paragraph p = new Paragraph();
            Image jpeg = null;
            if (Database.equals("oe_2")) {
                jpeg = Image.getInstance("/sftpdrive/opt/apache-tomcat-7.0.65/webapps/orange/images/logorange.jpg");
            } else if (Database.equals("victoria")) {
                jpeg = Image.getInstance("/sftpdrive/opt/apache-tomcat-7.0.65/webapps/orange/images/logVictoria.jpg");
            } else if (Database.equals("oddesa")) {
                jpeg = Image.getInstance("/sftpdrive/opt/apache-tomcat-7.0.65/webapps/orange/images/logOddesa.jpg");
            } else if (Database.equals("s_austin")) {
                jpeg = Image.getInstance("/sftpdrive/opt/apache-tomcat-7.0.65/webapps/orange/images/logSAustin.jpg");
            }
            if (jpeg != null) {
                jpeg.setAlignment(3);
                jpeg.setAbsolutePosition(210.0f, 730.0f);
                jpeg.scaleToFit(1200.0f, 95.0f);
                p.add((Object) jpeg);
            }
            final HeaderFooter header = new HeaderFooter((Phrase) p, false);
            header.setBorder(0);
            header.setAlignment(1);
            document.setHeader(header);
            document.open();
            document.add((Element) new Paragraph("\n"));
            document.add((Element) new Paragraph("\n"));
            try {
                Query = " Select CONCAT(a.FirstName, ' ', a.MiddleInitial, ' ', a.LastName), b.name, a.Gender, DATE_FORMAT(a.DOB,'%m/%d/%Y'), a.Age,  DATE_FORMAT(a.CreatedDate,'%m/%d/%Y') as DOS from " + Database + ".PatientReg a LEFT JOIN oe.clients b on a.ClientIndex = b.Id " + " where a.MRN = '" + PatientMRN + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientName = rset.getString(1);
                    ClientName = rset.getString(2);
                    Sex = rset.getString(3);
                    DOB = rset.getString(4);
                    Age = rset.getString(5);
                    DOS = rset.getString(6);
                }
                rset.close();
                stmt.close();
            } catch (Exception e2) {
                System.out.println("Error in 2:-" + e2.getMessage());
            }
            final Table datatable1 = new Table(6);
            datatable1.setWidth(100.0f);
            final int[] widths1 = {5, 15, 25, 25, 25, 5};
            datatable1.setWidths(widths1);
            datatable1.setBorder(0);
            datatable1.setCellpadding(1.0f);
            datatable1.setCellspacing(0.0f);
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
            final Table datatable2 = new Table(5);
            datatable2.setWidth(100.0f);
            final int[] widths2 = {10, 15, 5, 65, 5};
            datatable2.setWidths(widths2);
            datatable2.setBorder(0);
            datatable2.setCellpadding(1.0f);
            datatable2.setCellspacing(0.0f);
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
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            document.add((Element) datatable2);
            final Table datatable3 = new Table(7);
            datatable3.setWidth(100.0f);
            final int[] widths3 = {10, 5, 40, 10, 10, 20, 5};
            datatable3.setWidths(widths3);
            datatable3.setBorder(0);
            datatable3.setCellpadding(1.0f);
            datatable3.setCellspacing(0.0f);
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
            Query = "Select CONCAT('(',b.Catagory,')', ' ', b.Description), a.CostPerDisease, a.Quantity from " + Database + ".InvoiceDetail a " + " LEFT JOIN " + Database + ".SelfPaySheet b on a.DiseaseId = b.Id where PatientMRN = " + PatientMRN + " and ltrim(rtrim(a.InvoiceNo)) = ltrim(rtrim('" + InvoiceNo.trim() + "'))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ++Sno;
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
            final Table datatable4 = new Table(7);
            datatable4.setWidth(100.0f);
            final int[] widths4 = {10, 5, 40, 0, 20, 20, 5};
            datatable4.setWidths(widths4);
            datatable4.setBorder(0);
            datatable4.setCellpadding(1.0f);
            datatable4.setCellspacing(0.0f);
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
            datatable4.addCell((Phrase) new Paragraph(String.valueOf(TotalAmount - PaidAmount), normfont3));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            document.add((Element) datatable4);
            document.add((Element) new Paragraph("\n"));
            final Table datatable5 = new Table(2);
            datatable5.setWidth(100.0f);
            final int[] widths5 = {10, 90};
            datatable5.setWidths(widths5);
            datatable5.setBorder(0);
            datatable5.setCellpadding(1.0f);
            datatable5.setCellspacing(0.0f);
            datatable5.setDefaultCellBorder(0);
            datatable5.setDefaultColspan(1);
            datatable5.setDefaultHorizontalAlignment(0);
            datatable5.addCell((Phrase) new Paragraph(" ", normfont3));
            datatable5.addCell((Phrase) new Paragraph("Sign: ___________________", normfont3));
            document.add((Element) datatable5);
            document.close();
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Progma", "no-cache");
            response.setContentType("application/pdf");
            response.setContentLength(baos.size());
            final ServletOutputStream out2 = response.getOutputStream();
            baos.writeTo((OutputStream) out2);
            out2.flush();
            try {
                conn.close();
            } catch (Exception ex) {
                out.println(ex.getMessage());
            }
        } catch (Exception e3) {
            out.println(e3.getMessage());
            String str = "";
            for (int i = 0; i < e3.getStackTrace().length; ++i) {
                str = str + e3.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    void CollectPayment(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        final StringBuffer PatientInvoiceList = new StringBuffer();
        final int SNo = 1;
        try {
            Query = " SELECT a.PatientMRN, CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), DATE_FORMAT(b.DOB,'%m/%d/%Y') FROM " + Database + ".InvoiceMaster a " + " LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN where b.Status = 0 GROUP BY a.PatientMRN";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(1) + ") (" + rset.getString(3) + ")</option>");
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientInvoiceList", String.valueOf(PatientInvoiceList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowInvoice.html");
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

    void CollectPayment_View(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        double TotalAmount = 0.0;
        double PaidAmount = 0.0;
        final int PatientRegId = 0;
        String PatientName = "";
        String PatientInvoiceMRN = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        final StringBuffer CDRList = new StringBuffer();
        final StringBuffer PatientInvoiceList = new StringBuffer();
        final DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int SNo = 1;
        String DocumentPath = "";
        try {
            PatientInvoiceMRN = request.getParameter("PatientInvoice").trim();
            Query = " SELECT a.PatientMRN, CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), DATE_FORMAT(b.DOB,'%m/%d/%Y') FROM " + Database + ".InvoiceMaster a " + " LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN where b.Status = 0 GROUP BY a.PatientMRN";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
            }
            rset.close();
            stmt.close();
            Query = " SELECT a.PatientMRN, CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), DATE_FORMAT(b.DOB,'%d-%m-%Y'), DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T') AS DOS,  a.InvoiceNo, DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T'), a.TotalAmount, a.PaidAmount, a.BalAmount, a.Id  FROM " + Database + ".InvoiceMaster a " + " LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN  " + " WHERE a.PatientMRN = '" + PatientInvoiceMRN + "' and a.Status = 0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                TotalAmount = rset.getDouble(7);
                PaidAmount = rset.getDouble(7) - rset.getDouble(9);
                PatientName = rset.getString(2) + "(" + rset.getString(1) + ")";
                DocumentPath = "https://rovermd.com:8443/orange_2/Invoices/" + rset.getString(5) + "_" + rset.getString(1) + ".pdf";
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + TotalAmount + "</td>\n");
                CDRList.append("<td align=left>" + PaidAmount + "</td>\n");
                CDRList.append("<td align=left>" + rset.getDouble(9) + "</td>\n");
//        CDRList.append("<td align=left><a href=" + DocumentPath + " target=\"_blank\">" + "ViewInvoice_" + rset.getString(1) + "</a></td>\n");
                CDRList.append("<td align=left><a href=/md/md.RegisteredPatients_abid_okd?ActionID=InvoicePdf&PatientMRN=" + rset.getString(1) + ">" + "ViewInvoice_" + rset.getString(1) + "</a></td>\n");
                CDRList.append("<td align=left><a href=/md/md.RegisteredPatients_abid_okd?ActionID=PayNow&InvoiceNo=" + rset.getString(5) + ">PayNow</a></td>\n");
                CDRList.append("<td align=left><a href=/md/md.RegisteredPatients_abid_okd?ActionID=DeActiveInvoice&InvoiceMasterID=" + rset.getString(10) + "&InvoiceNo=" + rset.getString(5).trim() + ">De-Activate Invoice</a></td>\n");
                CDRList.append("</tr>");
                ++SNo;
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("PatientInvoiceList", String.valueOf(PatientInvoiceList));
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowInvoice.html");
        } catch (Exception var11) {
            out.println(Query);
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

    void PayNow(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String PatientMRN = "";
        String PatientName = "";
        double TotalAmount = 0.0;
        double PaidAmount = 0.0;
        double BalAmount = 0.0;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        final String InvoiceNo = request.getParameter("InvoiceNo").trim();
        final DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        final int SNo = 1;
        try {
            Query = "Select a.PatientMRN, CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), a.TotalAmount, a.PaidAmount, a.BalAmount  from " + Database + ".InvoiceMaster a LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN where a.InvoiceNo = '" + InvoiceNo + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientMRN = rset.getString(1);
                PatientName = rset.getString(2);
                TotalAmount = rset.getDouble(3);
                PaidAmount = rset.getDouble(3) - rset.getDouble(5);
                BalAmount = rset.getDouble(5);
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.SetField("PatientMRN", String.valueOf(PatientMRN));
            Parser.SetField("TotalAmount", String.valueOf(TotalAmount));
            Parser.SetField("PaidAmount", String.valueOf(PaidAmount));
            Parser.SetField("BalAmount", String.valueOf(BalAmount));
            Parser.SetField("InvoiceNo", String.valueOf(InvoiceNo));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/PayNow.html");
        } catch (Exception var11) {
            out.println(Query);
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

    void PayNowSave(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int Paid = 0;
        int PayMethod = Integer.parseInt(request.getParameter("PayMethod").trim());
        double Amount = Double.parseDouble(request.getParameter("Amount").trim());
        double TotalAmount = Double.parseDouble(request.getParameter("TotalAmount").trim());
        double BalAmount = Double.parseDouble(request.getParameter("BalAmount").trim());
        String RefNo = request.getParameter("RefNo").trim();
        String Remarks = request.getParameter("Remarks").trim();
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
        String PatientMRN = request.getParameter("PatientMRN").trim();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int SNo = 1;
        double PaidAmount = 0.0;
        try {

            Query = "Select PaidAmount from " + Database + ".InvoiceMaster where InvoiceNo = '" + InvoiceNo + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PaidAmount = rset.getDouble(1);
            }
            rset.close();
            stmt.close();

            if (BalAmount == Amount) {
                Paid = 1;
            }
            Query = " Update " + Database + ".InvoiceMaster set PaidAmount = '" + (PaidAmount + Amount) + "', BalAmount = '" + (BalAmount - Amount) + "', Paid = '" + Paid + "', PaymentDateTime = now() " + " where InvoiceNo = '" + InvoiceNo + "'";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".PaymentReceiptInfo (PatientMRN,InvoiceNo,TotalAmount," + " PaidAmount,Paid,RefNo,Remarks,PayMethod,CreatedDate, BalAmount) \nVALUES (?,?,?,?,?,?,?,?,now(),?) ");
                MainReceipt.setString(1, PatientMRN);
                MainReceipt.setString(2, InvoiceNo);
                MainReceipt.setDouble(3, TotalAmount);
                MainReceipt.setDouble(4, Amount);
                MainReceipt.setInt(5, Paid);
                MainReceipt.setString(6, RefNo);
                MainReceipt.setString(7, Remarks);
                MainReceipt.setString(8, String.valueOf(PayMethod));
                MainReceipt.setDouble(9, BalAmount - Amount);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 6- Insertion PaymentReceiptInfo Table :" + e.getMessage());
                return;
            }
//      out.println("<!DOCTYPE html><html><body><p style=\"color:white;\">Updated Successfully</p></body></html>");

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Message", "Payment Saved Successfully ");
            Parser.SetField("FormName", String.valueOf("RegisteredPatients_abid_okd"));
            Parser.SetField("ActionID", String.valueOf("CollectPayment"));
            Parser.GenerateHtml(out, "/opt/Htmls/md/Exception/Success.html");
        } catch (Exception var11) {
            out.println(Query);
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

    void DeActivePatient(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        final ResultSet rset = null;
        String Query = "";
        final int PatientID = Integer.parseInt(request.getParameter("ID").trim());
        try {
            Query = " Update " + Database + ".PatientReg set Status = 1 where ID = '" + PatientID + "'";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
//      out.println("Patient DE-ACTIVATED Successfully");
            out.println("<!DOCTYPE html><html><body><p style=\"color:gray;\">Patient DE-ACTIVATED Successfully</p></body></html>");
            out.println("<input class=\"btn btn-primary\" type=button name=Back Value=\"  Back  \" onclick=history.back()>");

        } catch (Exception var11) {
            out.println(Query);
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

    void ReActivePatient(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        final ResultSet rset = null;
        String Query = "";
        final int PatientID = Integer.parseInt(request.getParameter("ID").trim());
        try {
            Query = " Update " + Database + ".PatientReg set Status = 0 where ID = '" + PatientID + "'";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
//      out.println("Patient ACTIVATED Successfully");
            out.println("<!DOCTYPE html><html><body><p style=\"color:gray;\">Patient ACTIVATED Successfully</p></body></html>");
            out.println("<input class=\"btn btn-primary\" type=button name=Back Value=\"  Back  \" onclick=history.back()>");
        } catch (Exception var11) {
            out.println(Query);
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

    void DeActiveInvoice(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PaymentFound = 0;
        final int InvoiceMasterID = Integer.parseInt(request.getParameter("InvoiceMasterID").trim());
        final String InvoiceNo = request.getParameter("InvoiceNo").trim();
        try {
            Query = "Select COUNT(*) from " + Database + ".PaymentReceiptInfo where ltrim(rtrim(InvoiceNo)) = ltrim(rtrim('" + InvoiceNo + "'))";
            System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PaymentFound = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            if (PaymentFound > 0) {
                //out.println("Invoice Cannot DeActivate Becase it is Paid or Partially Paid");
                out.println("<!DOCTYPE html><html><body><p style=\"color:gray;\">Invoice Cannot DeActivate Becase it is Paid or Partially Paid</p></body></html>");
                out.println("<input class=\"btn btn-primary\" type=button name=Back Value=\"  Back  \" onclick=history.back()>");
                return;
            }
            Query = " Update " + Database + ".InvoiceMaster set Status = 1 where Id = '" + InvoiceMasterID + "'";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
            //out.println("Invoice DE-ACTIVATED Successfully");
            out.println("<!DOCTYPE html><html><body><p style=\"color:gray;\">Invoice DE-ACTIVATED Successfully</p></body></html>");
            out.println("<input class=\"btn btn-primary\" type=button name=Back Value=\"  Back  \" onclick=history.back()>");
        } catch (Exception var11) {
            out.println(Query);
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

    void TransactionReport_Input(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        final StringBuffer PatientInvoiceList = new StringBuffer();
        final DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        try {
            Query = " SELECT a.PatientMRN, CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), a.InvoiceNo FROM " + Database + ".InvoiceMaster a " + " LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN where b.Status = 0 GROUP BY a.PatientMRN";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1).trim() + "," + rset.getString(3).trim() + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientInvoiceList", String.valueOf(PatientInvoiceList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/TransactionReport.html");
        } catch (Exception var11) {
            out.println(Query);
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

    void TransactionReport(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        final Statement stmt2 = null;
        final ResultSet rset2 = null;
        String Query = "";
        final String Query2 = "";
        final int Found = 0;
        final int SNo = 1;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        final StringBuffer PatientInvoiceList = new StringBuffer();
        final StringBuffer TransactionList = new StringBuffer();
        final DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        final String PatientId = request.getParameter("PatientId").trim();
        try {
            Query = " SELECT a.PatientMRN, CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), a.InvoiceNo FROM " + Database + ".InvoiceMaster a " + " LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN where b.Status = 0 GROUP BY a.PatientMRN";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1).trim() + "," + rset.getString(3).trim() + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
            }
            rset.close();
            stmt.close();
            final String[] parts = PatientId.split("\\,");
            final String MRN = parts[0];
            final String InvoiceNo = parts[1];
            Query = " Select a.PatientMRN , CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), a.InvoiceNo, a.TotalAmount, a.PaidAmount,  a.BalAmount, a.RefNo, a.Remarks, DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T') as PaymentDate, DATE_FORMAT(c.CreatedDate, '%m/%d/%Y %T') as InvoiceDate from " + Database + ".PaymentReceiptInfo a LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN" + " LEFT JOIN " + Database + ".InvoiceMaster c on a.InvoiceNo = c.InvoiceNo where a.PatientMRN = '" + MRN + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                TransactionList.append("<tr>");
                TransactionList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getDouble(4) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getDouble(5) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getDouble(6) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                TransactionList.append("</tr>");
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientInvoiceList", String.valueOf(PatientInvoiceList));
            Parser.SetField("TransactionList", String.valueOf(TransactionList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/TransactionReport.html");
        } catch (Exception var11) {
            out.println(Query);
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

    void AddDoctors(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int SNo = 1;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        final StringBuffer DoctorsList = new StringBuffer();
        final DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        try {
            Query = "Select Id,CONCAT(DoctorsLastName, ' , ', DoctorsFirstName) ,CASE WHEN Status = 1 THEN 'Actve' ELSE 'InActive' END from " + Database + ".DoctorsList ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                DoctorsList.append("<tr>");
                DoctorsList.append("<td align=left>" + SNo + "</td>\n");
                DoctorsList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                DoctorsList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                DoctorsList.append("<td align=left><i class=\"fa fa-edit\" onClick=\"editRow(" + rset.getInt(1) + ")\"></i></td>\n");
                DoctorsList.append("</tr>");
                ++SNo;
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DoctorsList", String.valueOf(DoctorsList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/AddDoctors.html");
        } catch (Exception var11) {
            out.println(Query);
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

    void SaveDoctor(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int SNo = 1;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        final StringBuffer DoctorsList = new StringBuffer();
        final int SaveUpdateFlag = Integer.parseInt(request.getParameter("SaveUpdateFlag").trim());
        final int Id = Integer.parseInt(request.getParameter("Id").trim());
        final String DoctorsFirstName = request.getParameter("DoctorsFirstName").trim();
        final String DoctorsLastName = request.getParameter("DoctorsLastName").trim();
        final String Status = request.getParameter("Status").trim();
        try {
            if (SaveUpdateFlag == 0) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".DoctorsList (DoctorsFirstName,DoctorsLastName,Status," + " CreatedDate) \nVALUES (?,?,?,now()) ");
                    MainReceipt.setString(1, DoctorsFirstName);
                    MainReceipt.setString(2, DoctorsLastName);
                    MainReceipt.setString(3, Status);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception e) {
                    out.println("Error in Iserting DoctorsInfo: " + e.getMessage());
                }
            } else {
                try {
                    Query = " Update " + Database + ".DoctorsList set DoctorsFirstName = '" + DoctorsFirstName + "', DoctorsLastName = '" + DoctorsLastName + "', Status = '" + Status + "' " + " where Id = " + Id;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                } catch (Exception e) {
                    out.println("Error in Updating Doctors Info: " + e.getMessage());
                }
            }
            Query = "Select Id,CONCAT(DoctorsLastName, ' , ', DoctorsFirstName) ,CASE WHEN Status = 1 THEN 'Actve' ELSE 'InActive' END from " + Database + ".DoctorsList ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                DoctorsList.append("<tr>");
                DoctorsList.append("<td align=left>" + SNo + "</td>\n");
                DoctorsList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                DoctorsList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                DoctorsList.append("<td align=left><i class=\"fa fa-edit\" onClick=\"editRow(" + rset.getInt(1) + ")\"></i></td>\n");
                DoctorsList.append("</tr>");
                ++SNo;
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DoctorsList", String.valueOf(DoctorsList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/AddDoctors.html");
        } catch (Exception var11) {
            out.println(Query);
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

    void EditDoctor(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        final int Id = Integer.parseInt(request.getParameter("Id").trim());
        String DoctorsFirstName = "";
        String DoctorsLastName = "";
        int Status = 0;
        try {
            Query = "Select DoctorsFirstName, DoctorsLastName, Status from " + Database + ".DoctorsList where Id = " + Id;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DoctorsFirstName = rset.getString(1);
                DoctorsLastName = rset.getString(2);
                Status = rset.getInt(3);
            }
            rset.close();
            stmt.close();
            out.println(DoctorsFirstName + "|" + DoctorsLastName + "|" + String.valueOf(Status) + "|" + String.valueOf(Id));
        } catch (Exception var11) {
            out.println(Query);
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

    void AddAlergyInfo(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        final StringBuffer PatientList = new StringBuffer();
        try {
            Query = "SELECT ID, CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), MRN FROM " + Database + ".PatientReg where Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ")</option>");
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/AddAllergy.html");
        } catch (Exception var11) {
            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
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

    void SaveAllergy(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        final Statement stmt = null;
        final ResultSet rset = null;
        final String Query = "";
        final int PatientRegId = Integer.parseInt(request.getParameter("PatientId").trim());
        final String AllergyInfo = request.getParameter("AllergyInfo").trim();
        final String OtherInfo = request.getParameter("OtherInfo").trim();
        try {
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".Patient_AllergyInfo (PatientRegId, AlergyInfo,OtherInfo,Status," + " CreatedDate) \nVALUES (?,?,?,?,now()) ");
                MainReceipt.setInt(1, PatientRegId);
                MainReceipt.setString(2, AllergyInfo);
                MainReceipt.setString(3, OtherInfo);
                MainReceipt.setInt(4, 0);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error in Iserting AllergyInfo: " + e.getMessage());
            }
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Message", "Data Successfully Entered ");
            Parser.SetField("FormName", String.valueOf("RegisteredPatients_abid_okd"));
            Parser.SetField("ActionID", String.valueOf("AddAlergyInfo"));
            Parser.GenerateHtml(out, "/opt/Htmls/orange_2/Exception/Success.html");
        } catch (Exception var11) {
            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
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

    private void ShowHistory(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        final String PatientID = request.getParameter("ID");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String ClientName = null;
        String PatientName = null;
        String DOB = null;
        String Age = null;
        String Sex = null;
        String DOS = null;
        String MRN = null;
        final String OtherInfo = null;
        final String CreatedDate = null;
        int Sno = 0;
        final String FileName = "";
        int Found = 0;
        try {
            final Font MainHeading = new Font(2, 12.0f, 1, new Color(0, 0, 0));
            final Font normfont = new Font(2, 8.0f, 0, new Color(0, 0, 0));
            final Font normfont2 = new Font(2, 10.0f, 0, new Color(0, 0, 0));
            final Font normfont3 = new Font(2, 12.0f, 0, new Color(0, 0, 0));
            final Font UnderLine = new Font(2, 12.0f, 4, new Color(0, 0, 0));
            try {
                Query = "Select COUNT(*) from " + Database + ".Patient_AllergyInfo where PatientRegId = " + PatientID;
                System.out.println(Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    Found = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println(e.getMessage());
            }
            try {
                Query = "Select CONCAT(a.FirstName, ' ', a.MiddleInitial, ' ', a.LastName), b.name, a.Gender, a.DOB, a.Age,  DATE_FORMAT(a.CreatedDate,'%m/%d/%Y') as DOS, MRN from " + Database + ".PatientReg a LEFT JOIN oe.clients b on a.ClientIndex = b.Id " + "  where a.ID = " + PatientID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                System.out.println(Query);
                while (rset.next()) {
                    PatientName = rset.getString(1);
                    ClientName = rset.getString(2);
                    Sex = rset.getString(3);
                    DOB = rset.getString(4);
                    Age = rset.getString(5);
                    DOS = rset.getString(6);
                    MRN = rset.getString(7);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error in 1:-" + e.getMessage());
            }
            if (Found > 0) {
                final Document document = new Document(PageSize.A4, 0.0f, 0.0f, 70.0f, 30.0f);
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter.getInstance(document, (OutputStream) baos);
                PdfWriter.getInstance(document, (OutputStream) new FileOutputStream("/opt/apache-tomcat-7.0.65/webapps/orange_2/Attachment/PatientHistory_" + MRN + "_" + PatientName + ".pdf"));
                document.addAuthor("Golden Triangle");
                document.addSubject("Customer History");
                document.addCreationDate();
                final Paragraph p = new Paragraph();
                Image jpeg = null;
                if (Database.equals("oe_2")) {
                    jpeg = Image.getInstance("/sftpdrive/opt/apache-tomcat-7.0.65/webapps/orange/images/logorange.jpg");
                } else if (Database.equals("victoria")) {
                    jpeg = Image.getInstance("/sftpdrive/opt/apache-tomcat-7.0.65/webapps/orange/images/logVictoria.jpg");
                } else if (Database.equals("oddesa")) {
                    jpeg = Image.getInstance("/sftpdrive/opt/apache-tomcat-7.0.65/webapps/orange/images/logOddesa.jpg");
                }
                if (jpeg != null) {
                    jpeg.setAlignment(3);
                    jpeg.setAbsolutePosition(210.0f, 730.0f);
                    jpeg.scaleToFit(1200.0f, 95.0f);
                    p.add((Object) jpeg);
                }
                final HeaderFooter header = new HeaderFooter((Phrase) p, false);
                header.setBorder(0);
                header.setAlignment(1);
                document.setHeader(header);
                document.open();
                document.add((Element) new Paragraph("\n"));
                document.add((Element) new Paragraph("\n"));
                final Table datatable1 = new Table(6);
                datatable1.setWidth(100.0f);
                final int[] widths1 = {5, 15, 25, 25, 25, 5};
                datatable1.setWidths(widths1);
                datatable1.setBorder(0);
                datatable1.setCellpadding(1.0f);
                datatable1.setCellspacing(0.0f);
                datatable1.setDefaultCellBorder(0);
                datatable1.setDefaultColspan(1);
                datatable1.setDefaultHorizontalAlignment(0);
                datatable1.addCell((Phrase) new Paragraph(" ", normfont2));
                datatable1.addCell((Phrase) new Paragraph("MRN: ", normfont2));
                datatable1.addCell((Phrase) new Paragraph(MRN, UnderLine));
                datatable1.addCell((Phrase) new Paragraph("", normfont2));
                datatable1.setDefaultCellBorder(13);
                datatable1.addCell((Phrase) new Paragraph(PatientName, normfont));
                datatable1.setDefaultCellBorder(0);
                datatable1.addCell((Phrase) new Paragraph("", normfont2));
                datatable1.setDefaultColspan(1);
                datatable1.setDefaultHorizontalAlignment(0);
                datatable1.addCell((Phrase) new Paragraph("", normfont2));
                datatable1.addCell((Phrase) new Paragraph("", normfont2));
                datatable1.addCell((Phrase) new Paragraph("", UnderLine));
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
                datatable1.addCell((Phrase) new Paragraph("MRN: " + MRN + "         DOS: " + DOS, normfont));
                datatable1.setDefaultCellBorder(0);
                datatable1.addCell((Phrase) new Paragraph("", normfont2));
                document.add((Element) datatable1);
                final Table datatable2 = new Table(5);
                datatable2.setWidth(100.0f);
                final int[] widths2 = {10, 15, 5, 65, 5};
                datatable2.setWidths(widths2);
                datatable2.setBorder(0);
                datatable2.setCellpadding(1.0f);
                datatable2.setCellspacing(0.0f);
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
                datatable2.addCell((Phrase) new Paragraph("", normfont3));
                datatable2.addCell((Phrase) new Paragraph("", normfont3));
                datatable2.addCell((Phrase) new Paragraph("", normfont3));
                datatable2.addCell((Phrase) new Paragraph("", normfont3));
                document.add((Element) datatable2);
                final Table datatable3 = new Table(5);
                datatable3.setWidth(100.0f);
                final int[] widths3 = {5, 5, 45, 40, 5};
                datatable3.setWidths(widths3);
                datatable3.setBorder(0);
                datatable3.setCellpadding(1.0f);
                datatable3.setCellspacing(0.0f);
                datatable3.setDefaultCellBorder(0);
                datatable3.setDefaultColspan(1);
                datatable3.setDefaultHorizontalAlignment(0);
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell((Phrase) new Paragraph(" ", MainHeading));
                datatable3.setDefaultCellBorder(15);
                datatable3.addCell((Phrase) new Paragraph("SNo.", MainHeading));
                datatable3.setDefaultCellBorder(11);
                datatable3.addCell((Phrase) new Paragraph(" Allergy Info", MainHeading));
                datatable3.setDefaultCellBorder(11);
                datatable3.addCell((Phrase) new Paragraph("Other Info", MainHeading));
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell((Phrase) new Paragraph("", MainHeading));
                try {
                    Query = "Select AlergyInfo, OtherInfo from " + Database + ".Patient_AllergyInfo where PatientRegId = " + PatientID;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        ++Sno;
                        datatable3.setDefaultColspan(1);
                        datatable3.setDefaultHorizontalAlignment(0);
                        datatable3.setDefaultCellBorder(0);
                        datatable3.addCell((Phrase) new Paragraph(" ", normfont3));
                        datatable3.setDefaultCellBorder(0);
                        datatable3.addCell((Phrase) new Paragraph(String.valueOf(Sno), normfont3));
                        datatable3.setDefaultCellBorder(0);
                        datatable3.addCell((Phrase) new Paragraph(rset.getString(1), normfont3));
                        datatable3.setDefaultCellBorder(0);
                        datatable3.addCell((Phrase) new Paragraph(rset.getString(2), normfont3));
                        datatable3.setDefaultCellBorder(0);
                        datatable3.addCell((Phrase) new Paragraph("", normfont3));
                    }
                    rset.close();
                    stmt.close();
                } catch (Exception e2) {
                    out.println(e2.getMessage());
                    out.println(Query);
                }
                datatable3.setDefaultColspan(1);
                datatable3.setDefaultHorizontalAlignment(0);
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell((Phrase) new Paragraph(" ", normfont3));
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell((Phrase) new Paragraph(" ", normfont3));
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell((Phrase) new Paragraph(" ", normfont3));
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell((Phrase) new Paragraph(" ", normfont3));
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell((Phrase) new Paragraph(" ", normfont3));
                document.add((Element) datatable3);
                document.close();
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Progma", "no-cache");
                response.setContentType("application/pdf");
                response.setContentLength(baos.size());
                final ServletOutputStream out2 = response.getOutputStream();
                baos.writeTo((OutputStream) out2);
                out2.flush();
                try {
                    conn.close();
                } catch (Exception ex) {
                    out.println(ex.getMessage());
                }
            } else {
                out.println("<!DOCTYPE html><html><body><p style=\"color:white;\">Opps No History Found for this Patient " + PatientName + " MRN: " + MRN + "</p>");
                out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
            }
        } catch (Exception e3) {
            out.println(e3.getMessage());
            String str = "";
            for (int i = 0; i < e3.getStackTrace().length; ++i) {
                str = str + e3.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    void GetPatients(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            final String Patient = request.getParameter("Patient").trim();
            final StringBuffer PatientList = new StringBuffer();
            Query = "Select Id, MRN, FirstName, MiddleInitial, LastName, DOB, PhNumber from " + Database + ".PatientReg where FirstName like '%" + Patient + "%' OR LastName like '%" + Patient + "%' " + " OR MRN like '%" + Patient + "%' OR PhNumber like '%" + Patient + "%' OR MiddleInitial like '%" + Patient + "%'";
            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\" >");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option value=-1> Please Select Below Patient </option>");
            while (rset.next()) {
                PatientList.append("<option value=" + rset.getInt(1) + ">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
            }
            rset.close();
            stmt.close();
            PatientList.append("</select>");
            out.println(PatientList.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void GetPatientsUploadDocs(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            final String Patient = request.getParameter("Patient").trim();
            final StringBuffer PatientList = new StringBuffer();
            Query = "Select Id, MRN, FirstName, MiddleInitial, LastName, DOB, PhNumber, CONCAT(ID,',',MRN,',',Title,' ',FirstName,' ',MiddleInitial,' ',LastName) from " + Database + ".PatientReg where FirstName like '%" + Patient + "%' OR LastName like '%" + Patient + "%' " + " OR MRN like '%" + Patient + "%' OR PhNumber like '%" + Patient + "%'  OR MiddleInitial like '%" + Patient + "%' ";
//      System.out.println(Query);
            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\" >");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option value=-1> Please Select Below Patient </option>");
            while (rset.next()) {
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "," + rset.getString(2) + "," + rset.getString(3) + "," + rset.getString(4) + "," + rset.getString(5) + "," + rset.getString(6) + "," + rset.getString(7) + "\">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
            }
            rset.close();
            stmt.close();
            PatientList.append("</select>");
            out.println(PatientList.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void GetPatientsEligibility(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            final String Patient = request.getParameter("Patient").trim();
            final StringBuffer PatientList = new StringBuffer();
            Query = "Select Id, MRN, FirstName, MiddleInitial, LastName, DOB, PhNumber from " + Database + ".PatientReg where FirstName like '%" + Patient + "%' OR LastName like '%" + Patient + "%' " + " OR MRN like '%" + Patient + "%' OR PhNumber like '%" + Patient + "%'  OR MiddleInitial like '%" + Patient + "%'";
            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\" onchange=\"GetDetails();\">");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option value=-1> Please Select Below Patient </option>");
            while (rset.next()) {
                PatientList.append("<option value=" + rset.getInt(2) + ">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
            }
            rset.close();
            stmt.close();
            PatientList.append("</select>");
            out.println(PatientList.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void GetProfessionalPayersList(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            final String ProfessionalPayer = request.getParameter("ProfessionalPayer").trim();
            final StringBuffer ProfessionalPayersList = new StringBuffer();
            Query = "Select PayerId, PayerName from " + Database + ".ProfessionalPayers where PayerName like '%" + ProfessionalPayer + "%' order by status";
            ProfessionalPayersList.append("<select class=\"form-control select2\" id=\"ProfessionalPayer\" name=\"ProfessionalPayer\" >");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ProfessionalPayersList.append("<option value=-1> Please Select From Below Payers </option>");
            while (rset.next()) {
                ProfessionalPayersList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();
            ProfessionalPayersList.append("</select>");
            out.println(ProfessionalPayersList.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void RegisteredPatientListExcel(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientCount = 0;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        final StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        try {
            Query = "SELECT COUNT(*) FROM " + Database + ".PatientReg";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientCount = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            if (PatientCount > 0) {
                out.println("<table width=100% cellspacing=0 cellpading=0 border=1>");
                out.println("<tr bgcolor=\"#ff0000\"><td colspan=8 class=\"fieldm\" align=center><font face=\"Arial\" color=\"#FFFFFF\"><b>Registered Patient List</b></font></td></tr>\n");
                out.println("<tr bgcolor=\"#ff0000\">");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>SNo</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>MRN</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Patient Name</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Date of Birth</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Ph Number</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Reason Of Visit</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Date of Service</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>COVID Status</b></font></td></tr>");

                Query = "SELECT CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), DATE_FORMAT(DOB,'%m/%d/%Y'), PhNumber, IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'SUSPECTED' ELSE 'UN-EXAMINED' END FROM " + Database + ".PatientReg where Status = 0 ORDER BY ID DESC ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    out.println("<tr  bgcolor=\"#FFFFFF\">");
                    out.println("<td class=\"fieldm\">" + SNo + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(4) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(1) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(2) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(3) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(5) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(7) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(8) + "</td>");
                    out.println("</tr>");
                    ++SNo;
                }
                rset.close();
                stmt.close();
                out.println("</table>");
            }
//      Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
//      LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
//      Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
//      final Parsehtm Parser = new Parsehtm(request);
//      Parser.SetField("CDRList", String.valueOf(CDRList));
//      Parser.SetField("UserId", String.valueOf(UserId));
//      Parser.SetField("Header", String.valueOf(Header));
//      Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
//      Parser.SetField("Footer", String.valueOf(Footer));
//      Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowRegisteredPatient.html");
        } catch (Exception var11) {
            //Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println("Error in Excel Report: " + var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void DownloadPatientInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        try {

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/DownloadPatientInput.html");
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

    void DownloadPatientList(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientCount = 0;
        int SNo = 1;
        String FromDate, ToDate = "";
        FromDate = request.getParameter("FromDate").trim();
        ToDate = request.getParameter("ToDate").trim();
        try {
            Query = "SELECT COUNT(*) FROM " + Database + ".PatientReg";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientCount = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            if (PatientCount > 0) {
                out.println("<table width=100% cellspacing=0 cellpading=0 border=1>");
                out.println("<tr bgcolor=\"#ff0000\"><td colspan=13 class=\"fieldm\" align=center><font face=\"Arial\" color=\"#FFFFFF\"><b>Registered Patient List</b></font></td></tr>\n");
                out.println("<tr bgcolor=\"#ff0000\">");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>SNo</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>MRN</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Patient Name</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Date of Birth</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Ph Number</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Address</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>City</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>State</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>ZipCode</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Reason Of Visit</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Date of Service</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Email</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>InsuranceType (PayerName)</b></font></td></tr>");

                Query = " SELECT CONCAT(a.Title,' ',a.FirstName,' ',a.MiddleInitial,' ',a.LastName), DATE_FORMAT(a.DOB,'%m/%d/%Y'), a.PhNumber, IFNULL(a.MRN,0), IFNULL(a.ReasonVisit,'-'), " +
                        " a.ID, IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T')), " +
                        " CASE WHEN a.COVIDStatus = 1 THEN 'POSITIVE' WHEN a.COVIDStatus = 0 THEN 'NEGATIVE'  WHEN a.COVIDStatus = -1 THEN 'SUSPECTED' ELSE 'UN-EXAMINED' END, " +
                        " a.SelfPayChk,IFNULL(a.Email,'-'), IFNULL(a.Address,'-'), IFNULL(c.PayerName,'-'),IFNULL(a.City,'-'), IFNULL(a.State, '-'), IFNULL(a.ZipCode, '-') " +
                        " FROM PatientReg a " +
                        " LEFT JOIN InsuranceInfo b on a.ID = b.PatientRegId  " +
                        " LEFT JOIN ProfessionalPayers c on b.PriInsuranceName = c.Id " +
                        " where a.Status = 0 and a.CreatedDate >= '" + FromDate + " 00:00:00' and a.CreatedDate <= '" + ToDate + " 23:59:59' ORDER BY ID DESC; ";
                System.out.println(Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    out.println("<tr  bgcolor=\"#FFFFFF\">");
                    out.println("<td class=\"fieldm\">" + SNo + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(4) + "</td>");//mrn
                    out.println("<td class=\"fieldm\">" + rset.getString(1) + "</td>");//name
                    out.println("<td class=\"fieldm\">" + rset.getString(2) + "</td>");//dob
                    out.println("<td class=\"fieldm\">" + rset.getString(3) + "</td>");//phnumber
                    out.println("<td class=\"fieldm\">" + rset.getString(11) + "</td>");//address
                    out.println("<td class=\"fieldm\">" + rset.getString(13) + "</td>");//city
                    out.println("<td class=\"fieldm\">" + rset.getString(14) + "</td>");//State
                    out.println("<td class=\"fieldm\">" + rset.getString(15) + "</td>");//ZipCode
                    out.println("<td class=\"fieldm\">" + rset.getString(5) + "</td>");//rov
                    out.println("<td class=\"fieldm\">" + rset.getString(7) + "</td>");//dos
                    out.println("<td class=\"fieldm\">" + rset.getString(10) + "</td>");//email
                    out.println("<td class=\"fieldm\">" + rset.getString(12) + "</td>");//Inssurance
                    out.println("</tr>");
                    ++SNo;
                }
                rset.close();
                stmt.close();
                out.println("</table>");
            }
//      Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
//      LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
//      Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
//      final Parsehtm Parser = new Parsehtm(request);
//      Parser.SetField("CDRList", String.valueOf(CDRList));
//      Parser.SetField("UserId", String.valueOf(UserId));
//      Parser.SetField("Header", String.valueOf(Header));
//      Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
//      Parser.SetField("Footer", String.valueOf(Footer));
//      Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowRegisteredPatient.html");
        } catch (Exception var11) {
            //Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println("Error in Excel Report: " + var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
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

    public void download_direct(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        String FileName = request.getParameter("fname");
        String path = request.getParameter("path");
        String RecordingPath = path + FileName;
        //String UserId = Services.GetCookie("UserId", request).trim();
        //String userindex = Services.GetCookie("userindex", request).trim();

        if (FileName.endsWith("docx")) {
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        } else if (FileName.endsWith("doc")) {
            response.setContentType("application/msword");
        } else if (FileName.endsWith("csv")) {
            response.setContentType("text/csv");
        } else if (FileName.endsWith("jpg")) {
            response.setContentType("image/jpeg");
        } else if (FileName.endsWith("png")) {
            response.setContentType("image/png");
        } else if (FileName.endsWith("pdf")) {
            response.setContentType("application/pdf");
        } else if (FileName.endsWith("txt")) {
            response.setContentType("text/plain");
        } else if (FileName.endsWith("xlsx")) {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }

        try {

            //logging(Integer.parseInt(userindex),2, Integer.parseInt(indexptr) , conn);
            //markuser(Integer.parseInt(userindex),2, Integer.parseInt(indexptr), conn);

            //response.setContentType("application/pdf");
            // response.setContentType("audio/x-gsm");
            //response.setHeader("Content-Disposition", "Inline;filename=" + FileName);
            response.setHeader("Content-Disposition",
                    "Inline; filename=\"" + FileName + "\";");

            FileInputStream fin = new FileInputStream(RecordingPath);
            byte[] content = new byte[fin.available()];
            fin.read(content);
            fin.close();
            OutputStream os = response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
          /*File file = new File(RecordingPath);
          String mimeType = getServletContext().getMimeType(file.getName());
          response.setHeader("Content-Type", mimeType);
          response.setHeader("Content-Length", String.valueOf(file.length()));
          response.setHeader("Content-Disposition", "embed; filename=\"fileName.pdf\"");
          Files.copy(file.toPath(), response.getOutputStream());*/


        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }

}
