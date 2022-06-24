
package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.nio.file.Files;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

@SuppressWarnings("Duplicates")
public class RegisteredPatients2_28APRIL2021 extends HttpServlet {
    static String DOS = "";

    static String Acct = "";

    static String printabledate = "";
    private Connection conn = null;

    private static int copyFileUsingJava7Files(File source, File dest) throws IOException {
        File fe = new File(String.valueOf(dest));
        if (!fe.exists()) {
            Files.copy(source.toPath(), dest.toPath(), new java.nio.file.CopyOption[0]);
            return 1;
        }
        return 0;
    }

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

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId = "";
        int FacilityIndex = 0;
        String DatabaseName = "";
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();

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
            if (ActionID.equals("GetValues")) {
                GetValues(request, out, conn, context);
            } else if (ActionID.equals("PatientsDocUpload")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs Upload", "Click on Upload Docs Option", FacilityIndex);
                PatientsDocUpload(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("ShowReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View Patient Option", "Click on View Patient Option Patients List", FacilityIndex);
                ShowReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("PatientsDocUpload_Save") && request.getContentType().startsWith("multipart/form-data")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs Upload Save", "Save the Documents for the selected patients", FacilityIndex);
                PatientsDocUpload_Save(request, out, conn, context, response, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("ViewDocuments_Input")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs View", "Click on View Documents Options", FacilityIndex);
                ViewDocuments_Input(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("ViewDocuments")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs View", "Docmuents Uploaded List", FacilityIndex);
                ViewDocuments(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("DeleteDocument")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Delete Document", "Document is Deleted", FacilityIndex);
                DeleteDocument(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("CreateInvoiceInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Create Invoice Option", "Invoice Craetion Add Diseases", FacilityIndex);
                CreateInvoiceInput(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetCost")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Create Invoice Option", "Get the cost of the Diseases and Fill input fields", FacilityIndex);
                GetCost(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("SaveInvoice")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Invoice ", "Save the Created Invoice Data in Tables and Details", FacilityIndex);
                SaveInvoice(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("InvoicePdf")) {
                InvoicePdf(request, response, out, conn, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("CollectPayment")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Payement Collection Option", "Search the Invoice and Then Collect Payment Option", FacilityIndex);
                CollectPayment(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("CollectPayment_View")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Payement Collection Option", "View the List of Created Invoice Of selected Patient", FacilityIndex);
                CollectPayment_View(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("PayNow")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Pay Now Option", "Pay Now Option for the Created Invoice for Selected Pateint", FacilityIndex);
                PayNow(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("PayNowSave")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Pay Now Option", "Save the Details Of Paid Amount for the Invoice Created for the Selected Patient", FacilityIndex);
                PayNowSave(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("DeActivePatient")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "DeActivate Patients", "Deactivate the Selected Patients from the View Patient Option and Search Old Patient Option", FacilityIndex);
                DeActivePatient(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("ReActivePatient")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "ReActivate Patients", "ReActivate the Selected Patients from the Search Old Patient Option", FacilityIndex);
                ReActivePatient(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("DeActiveInvoice")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "DeActivate Invoice Option", "Deactivate the selected Invoice from The Collect Payment Option Where you can Search Invoice for the Respentive Patient", FacilityIndex);
                DeActiveInvoice(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("TransactionReport_Input")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Transaction Report Option", "Transaction Report for the Selected Patient Input Option", FacilityIndex);
                TransactionReport_Input(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("TransactionReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Transaction Report Option", "View Transaction Report List", FacilityIndex);
                TransactionReport(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("AddDoctors")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Doctors Physicians", "Click on Add Doctor Physicians Option", FacilityIndex);
                AddDoctors(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("SaveDoctor")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Doctors Physicians", "Save Doctor or Physician", FacilityIndex);
                SaveDoctor(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("EditDoctor")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Edit Doctors Physicians", "Edit Doctor or Physician", FacilityIndex);
                EditDoctor(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("AddAlergyInfo")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Allergy Information", "Add Allergy Options Input", FacilityIndex);
                AddAlergyInfo(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("SaveAllergy")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Allergy Information", "Save Allergy Options And Other Information", FacilityIndex);
                SaveAllergy(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("ShowHistory")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Show History for Allergy and Other Info", "Show Pdf for the Recent Allergy Information or any Other Info form View Patients Option or Search Old Patients Option", FacilityIndex);
                ShowHistory(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("CashReceipt")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "CashReceipt Download", "Show CashReceipt for Self Pay Patient Only", FacilityIndex);
                CashReceipt(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetPatients")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patients List", "Get Patients List matching from the Fields Input", FacilityIndex);
                GetPatients(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetPatientsMainScreen")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patients List", "Get Patients List matching from the Fields Input", FacilityIndex);
                GetPatientsMainScreen(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetPatientsEligibility")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patients List For Eligibility Inquiry", "Get Patients List matching from the Fields Input For Eligibility Inquiry", FacilityIndex);
                GetPatientsEligibility(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetPatientsUploadDocs")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patients List For Docs Upload", "Get Patients List matching from the Fields Input For Docs Upload", FacilityIndex);
                GetPatientsUploadDocs(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetProfessionalPayersList")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Professional Payer List For Eligbility Check", "Get Professional Payer LIst matching from the Fields Input For Eligbility Check", FacilityIndex);
                GetProfessionalPayersList(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("RegisteredPatientListExcel")) {
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=RoverPatientsList.xls");
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "DownloadExcelReport", "Click on Excel Report of All Patients List", FacilityIndex);
                RegisteredPatientListExcel(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("DownloadPatientInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Admin Report for Patients", "Download Admin Report For Patients", FacilityIndex);
                DownloadPatientInput(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("download_direct")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View Documents Uplaoded", "View Uploaded Docs", FacilityIndex);
                download_direct(request, response, out, conn);
            } else if (ActionID.equals("DownloadPatientList")) {
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=" + UserId + "_RoverPatientsList.xls");
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "DownloadExcelReport", "Download Admin Report Excel Option", FacilityIndex);
                DownloadPatientList(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else {
                out.println("Under Development");
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

    void GetValues(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Dashboards/DashBoardInput.html");
        } catch (Exception exception) {
        }
    }

    void ShowReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String BundleFnName = "";
        String LabelFnName = "";
        int PatientCount = 0;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        try {
            Query = "SELECT COUNT(*) FROM " + Database + ".PatientReg";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
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
                if (ClientId == 17) {
                    Query = " SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), DATE_FORMAT(DOB,'%m/%d/%Y'), " +
                            " IFNULL(PhNumber,''), IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T')," +
                            " DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), " +
                            " CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'NONE' ELSE 'NONE' END " +
                            " FROM " + Database + ".PatientReg where Status = 0 ORDER BY ID DESC limit 300";
                } else {
                    Query = " SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), DATE_FORMAT(DOB,'%m/%d/%Y'), " +
                            " IFNULL(PhNumber,''), IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T')," +
                            " DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), " +
                            " CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'NONE' ELSE 'NONE' END " +
                            " FROM " + Database + ".PatientReg where Status = 0 and LENGTH(MRN) = 6 ORDER BY ID DESC limit 300";
                }
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
                    CDRList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-5\" onclick=\"UpdateInfoPatient(" + rset.getInt(6) + ")\">Update</button></td>\n");
                    SNo++;
                }
                rset.close();
                stmt.close();
            }
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
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
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void PatientsDocUpload(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            SupportiveMethods suppMethods = new SupportiveMethods();
            StringBuffer LeftSideBarMenu = new StringBuffer();
            StringBuffer Header = new StringBuffer();
            StringBuffer Footer = new StringBuffer();
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String DocumentPath = "";
            String DirectoryName = "";
            int SNo = 1;
            StringBuffer PatientList = new StringBuffer();
            StringBuffer CDRList = new StringBuffer();

            Query = "Select DirectoryName from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                DirectoryName = rset.getString(1);
            rset.close();
            stmt.close();


            String PatientId = request.getParameter("PatientId");
            Query = " Select Id, IFNULL(MRN,''), IFNULL(FirstName,''), IFNULL(MiddleInitial,''), IFNULL(LastName,''), IFNULL(DOB,''), " +
                    " IFNULL(PhNumber,''),  CONCAT(ID,',',IFNULL(MRN,''),',',IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' '," +
                    " IFNULL(LastName,''))  " +
                    "from " + Database + ".PatientReg  where status = 0 and ID = " + PatientId;
//            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\" >");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//            PatientList.append("<option value=-1> Please Select Below Patient </option>");
            while (rset.next())
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "," + rset.getString(2) + "," + rset.getString(3) + "," + rset.getString(4) + "," + rset.getString(5) + "," + rset.getString(6) + "," + rset.getString(7) + "\">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
            rset.close();
            stmt.close();

            Query = "SELECT a.PatientMRN, a.PatientName, a.UploadDocumentName, a.FileName, IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T')," +
                    " DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')), a.Id FROM " + Database + ".PatientDocUpload a  " +
                    " LEFT JOIN " + Database + ".PatientReg b ON a.PatientRegId = b.ID WHERE a.Status = 0 and a.PatientRegId = " + PatientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//            out.println(Query);
            while (rset.next()) {
                File tmpFile = new File("/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "/" + rset.getString(4));
                boolean exists = tmpFile.exists();
                if (exists) {
                    DocumentPath = "/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "/";
                } else {
                    DocumentPath = "/sftpdrive/AdmissionBundlePdf/Attachment/";
                }

                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(4) + "&path=" + DocumentPath + " target=\"_blank\">" + rset.getString(4) + "</a></td>\n");
//                if (hostname.trim().equals("romver-01")) {
//                    CDRList.append("<td align=left><a href=https://rovermd.com:8443/md/md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(4) + "&path=" + DocumentPath + " target=\"_blank\">" + rset.getString(4) + "</a></td>\n");
//                } else {
//
//                }
                CDRList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteDocument(" + rset.getInt(6) + ")\"></i></td>\n");
                CDRList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/UploadPatientsDocs_copy.html");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void PatientsDocUpload_Save(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String PatientId = "";
        String DocumentName = "";
        String UserId = "";
        String Client = "";
        int PremisisId = 0;
        int PatientRegId = 0;
        String PatientMRN = "";
        String PatientName = "";
        String DirectoryName = "";
        String DocumentType = "";

        try {
            Query = "Select DirectoryName from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                DirectoryName = rset.getString(1);
            rset.close();
            stmt.close();
        } catch (Exception e) {
            Services.DumException("RegisteredPatient2", "PatientDocUplaodSave", request, e, servletContext);
        }

        String Path = "/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "";
        String UploadPath = String.valueOf(String.valueOf(Path)) + "/";
        boolean FileFound = false;
        byte[] Data = null;
        String key = "";
        String FileName = "";
        try {
            Dictionary d = doUpload(request, response, out);
            Enumeration<String> en = d.keys();
            while (en.hasMoreElements()) {
                key = en.nextElement();
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
                } else if (key.startsWith("Client")) {
                    Client = (String) d.get(key);
                } else if (key.startsWith("DocumentType")) {
                    DocumentType = (String) d.get(key);
                }
                if (FileFound) {
                    System.out.println(UploadPath + FileName);
                    FileName = FileName.replaceAll("\\s+", "");
                    File fe = new File(String.valueOf(String.valueOf(UploadPath)) + FileName);
                    if (fe.exists())
                        fe.delete();
                    FileOutputStream fouts = new FileOutputStream(fe);
                    fouts.write(Data);
                    fouts.flush();
                    fouts.close();
                }
            }
            key = PatientId = PatientId.substring(4);
            key = DocumentName = DocumentName.substring(4);
            key = UserId = UserId.substring(4);
            System.out.println("}}}]" + PatientId);
            String[] PatientInfo = PatientId.split("\\,");
            PatientRegId = Integer.parseInt(PatientInfo[0]);
            PatientMRN = PatientInfo[1];
            PatientName = PatientInfo[2];
            try {
                Query = "Select clientid from oe.sysusers where ltrim(rtrim(UPPER(userid))) = ltrim(rtrim(UPPER('" + UserId + "')))";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next())
                    PremisisId = rset.getInt(1);
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Client Id get (PremisisID)" + e.getMessage());
            }
            DocumentType = DocumentType.substring(4);
            try {
                PreparedStatement MainReceipt = conn.prepareStatement("Insert into " + Database + ".PatientDocUpload (PremisisId, PatientRegId, PatientMRN, PatientName, UploadDocumentName, FileName, CreatedBy, CreatedDate, DocumentType) values (?,?,?,?,?,?,?,now(),?) ");
                MainReceipt.setInt(1, PremisisId);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, PatientMRN);
                MainReceipt.setString(4, PatientName);
                MainReceipt.setString(5, DocumentName);
                MainReceipt.setString(6, FileName);
                MainReceipt.setString(7, UserId);
                MainReceipt.setString(8, DocumentType);
                MainReceipt.executeUpdate();

                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error in Insertion:-" + e.getMessage());
            }

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
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", String.valueOf("File Has been Uploaded Succesfully"));
            Parser.SetField("FormName", String.valueOf("RegisteredPatients2"));
            Parser.SetField("ActionID", String.valueOf("PatientsDocUpload&PatientId=" + PatientRegId));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Success.html");
        } catch (Exception e2) {
            out.println("Error in Upload DOcuments!!" + e2.getMessage());
            String str = "";
            for (int i = 0; i < (e2.getStackTrace()).length; i++)
                str = str + e2.getStackTrace()[i] + "<br>";
            out.println(str);
        }
    }

    void ViewDocuments_Input(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer PatientList = new StringBuffer();
        try {
            Query = "SELECT ID, CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), MRN FROM " + Database + ".PatientReg where Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ")</option>");
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/ViewDocuments.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void ViewDocuments(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String PatientName = "";
        String PatientMRN = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        StringBuffer PatientList = new StringBuffer();
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
            InetAddress ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();
            PatientRegId = Integer.parseInt(request.getParameter("PatientId").trim());
            Query = "SELECT ID, CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), MRN FROM " + Database + ".PatientReg where Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ")</option>");
            rset.close();
            stmt.close();
            Query = "SELECT a.PatientMRN, a.PatientName, a.UploadDocumentName, a.FileName, IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')), a.Id FROM " + Database + ".PatientDocUpload a  LEFT JOIN " + Database + ".PatientReg b ON a.PatientRegId = b.ID WHERE a.Status = 0 and a.PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//            out.println(Query);
            while (rset.next()) {
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                if (hostname.trim().equals("romver-01")) {
                    CDRList.append("<td align=left><a href=https://rovermd.com:8443/md/md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(4) + "&path=" + DocumentPath + " target=\"_blank\">" + rset.getString(4) + "</a></td>\n");
                } else {
                    CDRList.append("<td align=left><a href=https://app.rovermd.com:8443/md/md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(4) + "&path=" + DocumentPath + " target=\"_blank\">" + rset.getString(4) + "</a></td>\n");
                }
                CDRList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteDocument(" + rset.getInt(6) + ")\"></i></td>\n");
                CDRList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
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
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void DeleteDocument(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int DocId = Integer.parseInt(request.getParameter("ID").trim());

        try {
            Query = "Update " + Database + ".PatientDocUpload Set Status = 1 where ID = " + DocId;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            out.println("1");

        } catch (Exception e) {
            out.println("Error: Updating PatientDocUpload Table " + e.getMessage());
            Services.DumException("RegisteredPatients", "Delete DOC - Updating PatientDocUpload table :", request, e, this.getServletContext());
            return;
        }

    }

    void CreateInvoiceInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query2 = "";
        double TotalAmount = 0.0D;
        double PaidAmount = 0.0D;
        int PatientRegId = 0;
        int InstallmentPlanFound = 0;
        String PatientName = "";
        String PatientInvoiceMRN = "";
        int SNo = 1;
        StringBuffer diseaseList = new StringBuffer();
        StringBuffer PatientList = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        String PatientId = request.getParameter("PatientId").trim();
        try {
            Query = "SELECT ID, CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), MRN, DATE_FORMAT(DOB,'%m/%d/%Y') FROM " + Database + ".PatientReg where Status = 0 and ID = " + PatientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientInvoiceMRN = rset.getString(3);
                PatientList.append("<option class=Inner value=\"" + rset.getString(3) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ") (" + rset.getString(4) + ")</option>");
            }
            rset.close();
            stmt.close();
            Query = "SELECT Id, CONCAT('(',Catagory, ') ', Description) FROM " + Database + ".SelfPaySheet order by Id desc";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            diseaseList.append("<option class=Inner value=''>Select Payment </option>");
            while (rset.next()) {
                diseaseList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            /*Query = " SELECT a.PatientMRN, CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), DATE_FORMAT(b.DOB,'%m/%d/%Y'),  " +
                    "IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')) AS DOS,  a.InvoiceNo, " +
                    "DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T'), a.TotalAmount, a.PaidAmount, a.BalAmount, a.Id  " +
                    "FROM " + Database + ".InvoiceMaster a  LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN   " +
                    "WHERE a.PatientMRN = '" + PatientInvoiceMRN + "' and a.Status = 0 ";
            //System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Query2 = "Select COUNT(*) from " + Database + ".InstallmentPlan where MRN = '" + rset.getString(1) + "' and InvoiceNo = '" + rset.getString(5) + "'";
                stmt2 = conn.createStatement();
                rset2 = stmt2.executeQuery(Query2);
                if (rset2.next())
                    InstallmentPlanFound = rset2.getInt(1);
                rset2.close();
                stmt2.close();
                TotalAmount = rset.getDouble(7);
                PaidAmount = rset.getDouble(7) - rset.getDouble(9);
                PatientName = rset.getString(2) + "(" + rset.getString(1) + ")";
                CDRList.append("<tr>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
//                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
//                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
//                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
//                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(TotalAmount) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(PaidAmount) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(rset.getDouble(9)) + "</td>\n");
                CDRList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=InvoicePdf&PatientMRN=" + rset.getString(1) + "&InvoiceNo=" + rset.getString(5) + ">ViewInvoice_" + rset.getString(1) + "</a></td>\n");
                CDRList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=PayNow&InvoiceNo=" + rset.getString(5) + ">PayNow</a></td>\n");
                if (InstallmentPlanFound > 0) {
                    CDRList.append("<td align=left><a href=/md/md.InstallmentPlan?ActionID=GetInput&InvoiceNo=" + rset.getString(5) + ">Intallments (Plan Applied) </a></td>\n");
                } else {
                    CDRList.append("<td align=left><a href=/md/md.InstallmentPlan?ActionID=GetInput&InvoiceNo=" + rset.getString(5) + ">Intallments</a></td>\n");
                }
                CDRList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=DeActiveInvoice&InvoiceMasterID=" + rset.getString(10) + "&InvoiceNo=" + rset.getString(5).trim() + ">De-Activate Invoice</a></td>\n");
                CDRList.append("</tr>");
            }
            rset.close();
            stmt.close();*/

            Query = " SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), DATE_FORMAT(b.DOB,'%d-%m-%Y')," +
                    "IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')) AS DOS,  a.InvoiceNo, " +
                    "DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T'), a.TotalAmount, a.PaidAmount, a.BalAmount, a.Id  " +
                    " FROM " + Database + ".InvoiceMaster a  " +
                    " Left JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN   " +
                    " WHERE a.PatientMRN = '" + PatientInvoiceMRN + "' AND a.Status = 0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Query2 = "Select COUNT(*) FROM " + Database + ".InstallmentPlan WHERE MRN = '" + rset.getString(1) + "' AND InvoiceNo = '" + rset.getString(5) + "'";
                stmt2 = conn.createStatement();
                rset2 = stmt2.executeQuery(Query2);
                if (rset2.next())
                    InstallmentPlanFound = rset2.getInt(1);
                rset2.close();
                stmt2.close();
                TotalAmount = rset.getDouble(7);
                PaidAmount = rset.getDouble(7) - rset.getDouble(9);
                PatientName = rset.getString(2) + "(" + rset.getString(1) + ")";

//                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left width=15%>" + rset.getString(2) + "</td>\n");
//                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(TotalAmount) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(PaidAmount) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(rset.getDouble(9)) + "</td>\n");
                CDRList.append("<td align=left width=35%> ");
                CDRList.append("<a class='btn-sm btn btn-primary' href=/md/md.RegisteredPatients?ActionID=InvoicePdf&PatientMRN=" + rset.getString(1) + "&InvoiceNo=" + rset.getString(5) + ">View</a>\n");
                CDRList.append("<a class='btn-sm btn btn-success' href=/md/md.RegisteredPatients?ActionID=PayNow&InvoiceNo=" + rset.getString(5) + ">PayNow</a>\n");
                if (InstallmentPlanFound > 0) {
                    CDRList.append("<a class='btn-sm btn btn-warning' href=/md/md.InstallmentPlan?ActionID=GetInput&InvoiceNo=" + rset.getString(5) + ">Installments (Plan Applied) </a>\n");
                } else {
                    CDRList.append("<a class='btn-sm btn btn-warning' href=/md/md.InstallmentPlan?ActionID=GetInput&InvoiceNo=" + rset.getString(5) + ">Installments</a>\n");
                }
                CDRList.append("<a class='btn-sm btn btn-danger' href=/md/md.RegisteredPatients?ActionID=DeActiveInvoice&InvoiceMasterID=" + rset.getString(10) + "&InvoiceNo=" + rset.getString(5).trim() + ">De-Activate</a>\n");
                CDRList.append("</td>");
                CDRList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("diseaseList", String.valueOf(diseaseList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("PatientId", String.valueOf(PatientId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/InvoiceSheetInput_Copy.html");
        } catch (Exception var11) {
            Services.DumException("Invoice Sheet Input", "CreateInvoice RegisteredPatient ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void GetCost(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Cost = "";
        int DiseaseId = Integer.parseInt(request.getParameter("DiseaseId").trim());
        try {
            Query = "Select Cost from " + Database + ".SelfPaySheet where Id = " + DiseaseId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                Cost = rset.getString(1);
            rset.close();
            stmt.close();
            out.println(Cost.toString());
        } catch (Exception var11) {
            out.println(var11.getMessage());
            out.flush();
            out.close();
        }
    }

    void SaveInvoice(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        double TotalAmount = 0.0D;
        double CostPerDiseases = 0.0D;
        int i = 0;
        int j = 0;
        int k = 0;
        String InvoiceNo = "";
        int InvoiceMasterId = 0;
        String PatientString = request.getParameter("PatientString").trim();
        String PatientMRN = request.getParameter("PatientMRN").trim();
        try {
            String[] myInfo1 = new String[0];
            myInfo1 = PatientString.split("\\~");
            int RowCount = Integer.parseInt(myInfo1[1].substring(myInfo1[1].indexOf("=") + 1));
            String[][] InvoiceTableInput = new String[RowCount][5];
            for (i = j = k = 0, i = 2; i < myInfo1.length; i++) {
                if (myInfo1[i].length() > 0) {
                    InvoiceTableInput[k][j] = myInfo1[i].substring(myInfo1[i].indexOf("=") + 1);
                    if (++j > 4) {
                        j = 0;
                        k++;
                    }
                }
            }
            for (i = 0; i < RowCount; i++)
                TotalAmount += Double.parseDouble(InvoiceTableInput[i][2]);
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
                PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".InvoiceMaster (PatientMRN,InvoiceNo,TotalAmount ,PaidAmount,Paid,PaymentDateTime,InvoiceCreatedBy ,CreatedDate, Status, BalAmount,InstallmentApplied) \nVALUES (?,?,?,?,?,?,?,now(),0,?,0) ");
                MainReceipt.setString(1, PatientMRN);
                MainReceipt.setString(2, InvoiceNo);
                MainReceipt.setDouble(3, TotalAmount);
                MainReceipt.setDouble(4, 0.0D);
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
                if (rset.next())
                    InvoiceMasterId = rset.getInt(1);
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error 3- in Getting InvoiceMasterID: " + e.getMessage());
            }
            try {
                for (i = 0; i < RowCount; i++) {
                    Query = "Select Cost from " + Database + ".SelfPaySheet where Id = " + Integer.parseInt(InvoiceTableInput[i][4]);
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next())
                        CostPerDiseases = rset.getDouble(1);
                    rset.close();
                    stmt.close();
                    PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".InvoiceDetail (InvoiceMasterId,PatientMRN,InvoiceNo ,DiseaseId,CostPerDisease,Quantity,CreatedDate ,CreatedBy) \nVALUES (?,?,?,?,?,?,now(),?) ");
                    MainReceipt.setInt(1, InvoiceMasterId);
                    MainReceipt.setString(2, InvoiceTableInput[i][0]);
                    MainReceipt.setString(3, InvoiceNo);
                    MainReceipt.setString(4, InvoiceTableInput[i][4]);
                    MainReceipt.setDouble(5, Double.parseDouble(InvoiceTableInput[i][2]));
                    MainReceipt.setInt(6, Integer.parseInt(InvoiceTableInput[i][3]));
                    MainReceipt.setString(7, UserId);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                }
            } catch (Exception e) {
                System.out.println("Error 4- Insertion InvoiceDetail Table :" + e.getMessage());
            }
            out.println("1|" + PatientMRN + "|" + InvoiceNo);
        } catch (Exception var11) {
            out.println("Error: " + var11.getMessage());
            String str = "";
            for (i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    private void InvoicePdf(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String UserId, String Database, int ClientId) {
        String PatientMRN = request.getParameter("PatientMRN");
        String InvoiceNo = request.getParameter("InvoiceNo");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String ClientName = "";
        String PatientName = "";
        String DOB = "";
        String Age = "";
        String Sex = "";
        String DOS = "";
        String DOBForAge = "";
        String CreatedDate = null;
        double TotalAmount = 0.0D;
        double PaidAmount = 0.0D;
        String InvoiceCreatedDate = "";
        int Sno = 0;
        String FileName = "";
        String DirectoryName = "";
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
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
                //Query = "Select InvoiceNo, TotalAmount, PaidAmount, DATE_FORMAT(CreatedDate,'%m/%d/%Y') from " + Database + ".InvoiceMaster where PatientMRN = '" + PatientMRN + "' order by Id desc limit 1";
                Query = "Select InvoiceNo, TotalAmount, PaidAmount, DATE_FORMAT(CreatedDate,'%m/%d/%Y') " +
                        "from " + Database + ".InvoiceMaster where PatientMRN = '" + PatientMRN + "' AND InvoiceNo='" + InvoiceNo + "' ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    TotalAmount = rset.getDouble(2);
                    PaidAmount = rset.getDouble(3);
                    InvoiceCreatedDate = rset.getString(4);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error in 1:-" + e.getMessage());
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
            if (Database.equals("oe_2")) {
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
                Query = " Select CONCAT(IFNULL(a.FirstName,''), ' ', IFNULL(a.MiddleInitial,''), ' ', IFNULL(a.LastName,'')), " +
                        " IFNULL(b.name,''), IFNULL(a.Gender,''), IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''), IFNULL(a.Age,'0'),  " +
                        " DATE_FORMAT(a.CreatedDate,'%m/%d/%Y') as DOS, IFNULL(DATE_FORMAT(a.DOB,'%Y-%m-%d'),'') from " + Database + ".PatientReg a " +
                        " LEFT JOIN oe.clients b on a.ClientIndex = b.Id  " +
                        " where a.MRN = '" + PatientMRN + "'";
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
                System.out.println("Error in 2:-" + e2.getMessage());
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
            Query = "Select CONCAT('(',b.Catagory,')', ' ', b.Description), a.CostPerDisease, a.Quantity from " + Database + ".InvoiceDetail a  LEFT JOIN " + Database + ".SelfPaySheet b on a.DiseaseId = b.Id where PatientMRN = " + PatientMRN + " and ltrim(rtrim(a.InvoiceNo)) = ltrim(rtrim('" + InvoiceNo.trim() + "'))";
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
            datatable4.addCell((Phrase) new Paragraph(String.valueOf(numFormat.format(TotalAmount - PaidAmount)), normfont3));//here we have changed
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
            datatable5.addCell((Phrase) new Paragraph("Sign: ___________________", normfont3));
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
            out.println(e3.getMessage());
            String str = "";
            for (int i = 0; i < (e3.getStackTrace()).length; i++)
                str = str + e3.getStackTrace()[i] + "<br>";
            out.println(str);
        }
    }

    void CollectPayment(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer PatientInvoiceList = new StringBuffer();
        int SNo = 1;
        try {
            Query = " SELECT a.PatientMRN, CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), DATE_FORMAT(b.DOB,'%m/%d/%Y') FROM " + Database + ".InvoiceMaster a  LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN where b.Status = 0 GROUP BY a.PatientMRN";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(1) + ") (" + rset.getString(3) + ")</option>");
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
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowInvoice.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void CollectPayment_View(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query = "";
        String Query2 = "";
        double TotalAmount = 0.0D;
        double PaidAmount = 0.0D;
        int PatientRegId = 0;
        int InstallmentPlanFound = 0;
        String PatientName = "";
        String PatientInvoiceMRN = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        StringBuffer PatientInvoiceList = new StringBuffer();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int SNo = 1;
        String DocumentPath = "";
        try {
            PatientInvoiceMRN = request.getParameter("PatientInvoice").trim();
            Query = " SELECT a.PatientMRN, CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), DATE_FORMAT(b.DOB,'%m/%d/%Y') FROM " + Database + ".InvoiceMaster a  LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN where b.Status = 0 GROUP BY a.PatientMRN";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                if (rset.getString(1).equals(PatientInvoiceMRN)) {
                    PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
                    continue;
                }
                PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
            }
            rset.close();
            stmt.close();
            Query = " SELECT a.PatientMRN, CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), DATE_FORMAT(b.DOB,'%d-%m-%Y'),  IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')) AS DOS,  a.InvoiceNo, DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T'), a.TotalAmount, a.PaidAmount, a.BalAmount, a.Id  FROM " + Database + ".InvoiceMaster a  LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN   WHERE a.PatientMRN = '" + PatientInvoiceMRN + "' and a.Status = 0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Query2 = "Select COUNT(*) from " + Database + ".InstallmentPlan where MRN = '" + rset.getString(1) + "' and InvoiceNo = '" + rset.getString(5) + "'";
                stmt2 = conn.createStatement();
                rset2 = stmt2.executeQuery(Query2);
                if (rset2.next())
                    InstallmentPlanFound = rset2.getInt(1);
                rset2.close();
                stmt2.close();
                TotalAmount = rset.getDouble(7);
                PaidAmount = rset.getDouble(7) - rset.getDouble(9);
                PatientName = rset.getString(2) + "(" + rset.getString(1) + ")";
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(TotalAmount) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(PaidAmount) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(rset.getDouble(9)) + "</td>\n");
                CDRList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=InvoicePdf&PatientMRN=" + rset.getString(1) + "&InvoiceNo=" + rset.getString(5) + ">ViewInvoice_" + rset.getString(1) + "</a></td>\n");
                CDRList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=PayNow&InvoiceNo=" + rset.getString(5) + ">PayNow</a></td>\n");
                if (InstallmentPlanFound > 0) {
                    CDRList.append("<td align=left><a href=/md/md.InstallmentPlan?ActionID=GetInput&InvoiceNo=" + rset.getString(5) + ">Intallments (Plan Applied) </a></td>\n");
                } else {
                    CDRList.append("<td align=left><a href=/md/md.InstallmentPlan?ActionID=GetInput&InvoiceNo=" + rset.getString(5) + ">Intallments</a></td>\n");
                }
                CDRList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=DeActiveInvoice&InvoiceMasterID=" + rset.getString(10) + "&InvoiceNo=" + rset.getString(5).trim() + ">De-Activate Invoice</a></td>\n");
                CDRList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
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
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void PayNow(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String PatientMRN = "";
        String PatientName = "";
        double TotalAmount = 0.0D;
        double PaidAmount = 0.0D;
        double BalAmount = 0.0D;
        double AmountToPay = 0.0D;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        StringBuffer DeviceList = new StringBuffer();
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int SNo = 1;
        int InstallmentPlanFound = 0;
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
            Query = "Select COUNT(*) from " + Database + ".InstallmentPlan where MRN = '" + PatientMRN + "' and InvoiceNo = '" + InvoiceNo + "' and status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                InstallmentPlanFound = rset.getInt(1);
            rset.close();
            stmt.close();
            if (InstallmentPlanFound > 0) {
                Query = "Select IFNULL(MRN,''), IFNULL(InvoiceNo,''), IFNULL(PaymentAmount,0), IFNULL(DATE_FORMAT(PaymentDate,'%m/%d/%Y'),''),  CASE WHEN PAID = 0 THEN 'Pending' WHEN Paid = 1 THEN 'Paid' ELSE 'Pending' END from " + Database + ".InstallmentPlan where MRN = '" + PatientMRN + "' and  InvoiceNo = '" + InvoiceNo + "' and status = 0";
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
                Query = " Select IFNULL(PaymentAmount,0) from " + Database + ".InstallmentPlan where MRN = '" + PatientMRN + "' and  InvoiceNo = '" + InvoiceNo + "' and Paid = 0 limit 1 ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    AmountToPay = rset.getDouble(1);
                rset.close();
                stmt.close();
            }
            if (BalAmount == 0.0D) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("Message", "Payment has already been paid!");
                parsehtm.SetField("FormName", "RegisteredPatients2");
                parsehtm.SetField("ActionID", "CreateInvoiceInput&PatientId");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                return;
            }
            Query = "Select hsn, DeviceName from oe.BoltDevice where Status = 0 and ClientId = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                DeviceList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(1) + "))</option>");
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.SetField("PatientMRN", String.valueOf(PatientMRN));
            Parser.SetField("TotalAmount", String.valueOf(numFormat.format(TotalAmount)));
            Parser.SetField("PaidAmount", String.valueOf(numFormat.format(PaidAmount)));
            Parser.SetField("BalAmount", String.valueOf(numFormat.format(BalAmount)));
            Parser.SetField("InvoiceNo", String.valueOf(InvoiceNo));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("AmountToPay", String.valueOf(numFormat.format(AmountToPay)));
            Parser.SetField("InstallmentPlanFound", String.valueOf(InstallmentPlanFound));
            Parser.SetField("DeviceList", String.valueOf(DeviceList));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.SetField("ClientIndex", String.valueOf(ClientId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/PayNow.html");
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

    void PayNowSave(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int Paid = 0;
        int InstallmentPlanId = 0;
        try {
            int PayMethod = Integer.parseInt(request.getParameter("PayMethod").trim());
            int InstallmentPlanFound = Integer.parseInt(request.getParameter("InstallmentPlanFound").trim());
            double Amount = Double.parseDouble(request.getParameter("Amount").trim().replaceAll(",", ""));
            double TotalAmount = Double.parseDouble(request.getParameter("TotalAmount").trim().replaceAll(",", ""));
            double BalAmount = Double.parseDouble(request.getParameter("BalAmount").trim().replaceAll(",", ""));
            String RefNo = request.getParameter("RefNo").trim();
            String Remarks = request.getParameter("Remarks").trim();
            String InvoiceNo = request.getParameter("InvoiceNo").trim();
            String PatientMRN = request.getParameter("PatientMRN").trim();
            DecimalFormat numFormat = new DecimalFormat("#,###,###.00");

            if (PayMethod == 1 || PayMethod == 3) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Please select Cash Method!!");
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "PayNow");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Error.html");
                return;
            }

            int SNo = 1;
            double PaidAmount = 0.0D;
            Query = "Select PaidAmount from " + Database + ".InvoiceMaster where InvoiceNo = '" + InvoiceNo + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                PaidAmount = rset.getDouble(1);
            rset.close();
            stmt.close();
            if (BalAmount == Amount)
                Paid = 1;
            Query = " Update " + Database + ".InvoiceMaster set PaidAmount = '" + (PaidAmount + Amount) + "', BalAmount = '" + (BalAmount - Amount) + "', Paid = '" + Paid + "', PaymentDateTime = now()  where InvoiceNo = '" + InvoiceNo + "'";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
            try {
                PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".PaymentReceiptInfo (PatientMRN,InvoiceNo,TotalAmount, PaidAmount,Paid,RefNo,Remarks,PayMethod,CreatedDate, BalAmount) \nVALUES (?,?,?,?,?,?,?,?,now(),?) ");
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
            if (InstallmentPlanFound > 0)
                try {
                    out.println("STage 1: Inside Installment Plan Found");
                    Query = "Select Id from " + Database + ".InstallmentPlan where Paid = 0 and InvoiceNo = '" + InvoiceNo + "' limit 1";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next())
                        InstallmentPlanId = rset.getInt(1);
                    rset.close();
                    stmt.close();
                    out.println("Stage 2: Inside Installment Plan FOund" + Query);
                    if (InstallmentPlanId > 0) {
                        out.println("Stage 3: Inside Installment Plan ID Found");
                        Query = " Update " + Database + ".InstallmentPlan set Paid = 1 where Id = " + InstallmentPlanId + "";
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();
                        out.println("Stage 4: Inside Installment Plan ID Found" + Query);
                    }
                } catch (Exception e) {
                    out.println("Error in Updating Installment plan Table: " + e.getMessage());
                }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Message", "Payment Saved Successfully ");
            Parser.SetField("FormName", String.valueOf("RegisteredPatients"));
            Parser.SetField("ActionID", String.valueOf("CollectPayment"));
            Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/md/Exception/Success.html");
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

    void DeActivePatient(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String MRN = "";
        String className = getClass().getName();
        int PatientID = Integer.parseInt(request.getParameter("ID").trim());
        try {
            Query = " Update " + Database + ".PatientReg set Status = 1 where ID = '" + PatientID + "'";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
            out.println("1");
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

    void ReActivePatient(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientID = Integer.parseInt(request.getParameter("ID").trim());
        try {
            Query = " Update " + Database + ".PatientReg set Status = 0 where ID = '" + PatientID + "'";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
            out.println("1");
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

    void DeActiveInvoice(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PaymentFound = 0;
        int InvoiceMasterID = Integer.parseInt(request.getParameter("InvoiceMasterID").trim());
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
        try {
            Query = "Select COUNT(*) from " + Database + ".PaymentReceiptInfo where ltrim(rtrim(InvoiceNo)) = ltrim(rtrim('" + InvoiceNo + "'))";
            System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                PaymentFound = rset.getInt(1);
            rset.close();
            stmt.close();
            if (PaymentFound > 0) {
                out.println("<!DOCTYPE html><html><body><p style=\"color:gray;\">Invoice Cannot DeActivate Becase it is Paid or Partially Paid</p></body></html>");
                out.println("<input class=\"btn btn-primary\" type=button name=Back Value=\"  Back  \" onclick=history.back()>");
                return;
            }
            Query = " Update " + Database + ".InvoiceMaster set Status = 1 where Id = '" + InvoiceMasterID + "'";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
            out.println("<!DOCTYPE html><html><body><p style=\"color:gray;\">Invoice DE-ACTIVATED Successfully</p></body></html>");
            out.println("<input class=\"btn btn-primary\" type=button name=Back Value=\"  Back  \" onclick=history.back()>");
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
            Query = " SELECT a.PatientMRN, CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), a.InvoiceNo FROM " + Database + ".InvoiceMaster a  LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN where b.Status = 0 GROUP BY a.PatientMRN";
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
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/TransactionReport.html");
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

    void TransactionReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query = "";
        String Query2 = "";
        int Found = 0;
        int SNo = 1;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer PatientInvoiceList = new StringBuffer();
        StringBuffer TransactionList = new StringBuffer();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        String PatientId = request.getParameter("PatientId").trim();
        try {
            String[] parts = PatientId.split("\\,");
            String MRN = parts[0];
            String InvoiceNo = parts[1];
            Query = " SELECT a.PatientMRN, CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), a.InvoiceNo " +
                    " FROM " + Database + ".InvoiceMaster a  " +
                    " LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN " +
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
            Query = " Select a.PatientMRN , CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), a.InvoiceNo, a.TotalAmount, " +
                    "a.PaidAmount,  a.BalAmount, IFNULL(a.RefNo,'-'), IFNULL(a.Remarks,'-') , DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T') as PaymentDate, " +
                    "DATE_FORMAT(c.CreatedDate, '%m/%d/%Y %T') as InvoiceDate,  " +
                    "CASE WHEN PayMethod = 1 THEN 'Credit Card' WHEN PayMethod = 2 THEN 'Cash' WHEN PayMethod = 3 THEN 'BOLT Device' WHEN PayMethod = 4 THEN 'Ingenico' ELSE '' END, " +
                    "IFNULL(PayMethod,0) " +
                    " FROM " + Database + ".PaymentReceiptInfo a " +
                    "LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN " +
                    "LEFT JOIN " + Database + ".InvoiceMaster c on a.InvoiceNo = c.InvoiceNo " +
                    " WHERE a.PatientMRN = '" + MRN + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                TransactionList.append("<tr>");
                TransactionList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(4)) + "</td>\n");
                TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(5)) + "</td>\n");
                TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(6)) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                if (rset.getString(11).trim().equals("Cash")) {
                    if (ClientId == 27) {
                        TransactionList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=CashReceipt&MRN=" + rset.getInt(1) + "&InvoiceNo=" + rset.getString(3) + ">" + rset.getString(11) + "</a></td>\n");
                    } else {
                        TransactionList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    }
                } else {
                    TransactionList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                }
                TransactionList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                TransactionList.append("</tr>");
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
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
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void AddDoctors(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int SNo = 1;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer DoctorsList = new StringBuffer();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        try {
            Query = "Select Id,CONCAT(DoctorsLastName, ' , ', DoctorsFirstName) ,CASE WHEN Status = 1 THEN 'Active' ELSE 'InActive' END from " + Database + ".DoctorsList ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                DoctorsList.append("<tr>");
                DoctorsList.append("<td align=left>" + SNo + "</td>\n");
                DoctorsList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                DoctorsList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                DoctorsList.append("<td align=left><i class=\"fa fa-edit\" onClick=\"editRow(" + rset.getInt(1) + ")\"></i></td>\n");
                DoctorsList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
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
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void SaveDoctor(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int SNo = 1;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer DoctorsList = new StringBuffer();
        int SaveUpdateFlag = Integer.parseInt(request.getParameter("SaveUpdateFlag").trim());
        int Id = Integer.parseInt(request.getParameter("Id").trim());
        String DoctorsFirstName = request.getParameter("DoctorsFirstName").trim();
        String DoctorsLastName = request.getParameter("DoctorsLastName").trim();
        String Status = request.getParameter("Status").trim();
        try {
            if (SaveUpdateFlag == 0) {
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".DoctorsList (DoctorsFirstName,DoctorsLastName,Status, CreatedDate) \nVALUES (?,?,?,now()) ");
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
                    Query = " Update " + Database + ".DoctorsList set DoctorsFirstName = '" + DoctorsFirstName + "', DoctorsLastName = '" + DoctorsLastName + "', Status = '" + Status + "'  where Id = " + Id;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                } catch (Exception e) {
                    out.println("Error in Updating Doctors Info: " + e.getMessage());
                }
            }
            Query = "Select Id,CONCAT(DoctorsLastName, ' , ', DoctorsFirstName) ,CASE WHEN Status = 1 THEN 'Active' ELSE 'InActive' END from " + Database + ".DoctorsList ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                DoctorsList.append("<tr>");
                DoctorsList.append("<td align=left>" + SNo + "</td>\n");
                DoctorsList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                DoctorsList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                DoctorsList.append("<td align=left><i class=\"fa fa-edit\" onClick=\"editRow(" + rset.getInt(1) + ")\"></i></td>\n");
                DoctorsList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
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
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void EditDoctor(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int Id = Integer.parseInt(request.getParameter("Id").trim());
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
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void AddAlergyInfo(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer PatientList = new StringBuffer();
        try {
            Query = "SELECT ID, CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), MRN FROM " + Database + ".PatientReg where Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ")</option>");
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/AddAllergy.html");
        } catch (Exception var11) {
            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void SaveAllergy(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = Integer.parseInt(request.getParameter("PatientId").trim());
        String AllergyInfo = request.getParameter("AllergyInfo").trim();
        String OtherInfo = request.getParameter("OtherInfo").trim();
        try {
            try {
                PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".Patient_AllergyInfo (PatientRegId, AlergyInfo,OtherInfo,Status, CreatedDate) \nVALUES (?,?,?,?,now()) ");
                MainReceipt.setInt(1, PatientRegId);
                MainReceipt.setString(2, AllergyInfo);
                MainReceipt.setString(3, OtherInfo);
                MainReceipt.setInt(4, 0);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error in Iserting AllergyInfo: " + e.getMessage());
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Message_fileUpload", "Data Successfully Entered ");
            Parser.SetField("FormName", String.valueOf("RegisteredPatients"));
            Parser.SetField("ActionID", String.valueOf("AddAlergyInfo"));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Success.html");
        } catch (Exception var11) {
            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    private void ShowHistory(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        String PatientID = request.getParameter("ID");
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
        String OtherInfo = null;
        String CreatedDate = null;
        int Sno = 0;
        String FileName = "";
        int Found = 0;
        try {
            Font MainHeading = new Font(2, 12.0F, 1, new Color(0, 0, 0));
            Font normfont = new Font(2, 8.0F, 0, new Color(0, 0, 0));
            Font normfont2 = new Font(2, 10.0F, 0, new Color(0, 0, 0));
            Font normfont3 = new Font(2, 12.0F, 0, new Color(0, 0, 0));
            Font UnderLine = new Font(2, 12.0F, 4, new Color(0, 0, 0));
            try {
                Query = "Select COUNT(*) from " + Database + ".Patient_AllergyInfo where PatientRegId = " + PatientID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next())
                    Found = rset.getInt(1);
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println(e.getMessage());
            }
            try {
                Query = "Select CONCAT(a.FirstName, ' ', a.MiddleInitial, ' ', a.LastName), b.name, a.Gender, a.DOB, a.Age,  DATE_FORMAT(a.CreatedDate,'%m/%d/%Y') as DOS, MRN from " + Database + ".PatientReg a LEFT JOIN oe.clients b on a.ClientIndex = b.Id   where a.ID = " + PatientID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
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
            String Path = "";
            InetAddress ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();
            if (hostname.trim().equals("romver-01")) {
                Path = "";
            } else {
                Path = "/sftpdrive";
            }
            if (Found > 0) {
                Document document = new Document(PageSize.A4, 0.0F, 0.0F, 70.0F, 30.0F);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter.getInstance(document, baos);
                PdfWriter.getInstance(document, new FileOutputStream(Path + "/opt/apache-tomcat-7.0.65/webapps/md/Attachment/PatientHistory_" + MRN + "_" + PatientName + ".pdf"));
                document.addAuthor("Golden Triangle");
                document.addSubject("Customer History");
                document.addCreationDate();
                Paragraph p = new Paragraph();
                Image jpeg = null;
                if (Database.equals("oe_2")) {
                    jpeg = Image.getInstance(Path + "/opt/apache-tomcat-7.0.65/webapps/orange/images/logorange.jpg");
                } else if (Database.equals("victoria")) {
                    jpeg = Image.getInstance(Path + "/opt/apache-tomcat-7.0.65/webapps/orange/images/logVictoria.jpg");
                } else if (Database.equals("oddesa")) {
                    jpeg = Image.getInstance(Path + "/opt/apache-tomcat-7.0.65/webapps/orange/images/logOddesa.jpg");
                } else if (Database.equals("s_austin")) {
                    System.out.println("Inside South Austin");
                    jpeg = Image.getInstance(Path + "/opt/apache-tomcat-7.0.65/webapps/orange/images/logSAustin.jpg");
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
                datatable2.addCell((Phrase) new Paragraph("", normfont3));
                datatable2.addCell((Phrase) new Paragraph("", normfont3));
                datatable2.addCell((Phrase) new Paragraph("", normfont3));
                datatable2.addCell((Phrase) new Paragraph("", normfont3));
                document.add((Element) datatable2);
                Table datatable3 = new Table(5);
                datatable3.setWidth(100.0F);
                int[] widths3 = {5, 5, 45, 40, 5};
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
                        Sno++;
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
                ServletOutputStream out2 = response.getOutputStream();
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
            for (int i = 0; i < (e3.getStackTrace()).length; i++)
                str = str + e3.getStackTrace()[i] + "<br>";
            out.println(str);
        }
    }

    void CashReceipt(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String PatientMRN = "";
        String InvoiceNo = "";
        String DirectoryName = "";
        String PatientName = "";
        String PaymentDate = "";
        String ReasonVisit = "";
        String ReasonVisitCatagory = "";
        String PaidAmount = "";
        try {
            PatientMRN = request.getParameter("MRN").trim();
            InvoiceNo = request.getParameter("InvoiceNo").trim();
            Query = "Select IFNULL(DirectoryName,'') from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                DirectoryName = rset.getString(1);
            rset.close();
            stmt.close();
            Query = "Select CONCAT(IFNULL(b.Title,''),' ', IFNULL(b.FirstName,''), ' ', IFNULL(b.MiddleInitial,''), ' ', IFNULL(b.LastName,'')), DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'), b.ReasonVisit, a.PaidAmount from " + Database + ".PaymentReceiptInfo a LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN where a.PatientMRN = '" + PatientMRN + "' and a.InvoiceNo = '" + InvoiceNo + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientName = rset.getString(1);
                PaymentDate = rset.getString(2);
                ReasonVisit = rset.getString(3);
                PaidAmount = rset.getString(4);
            }
            rset.close();
            stmt.close();
            Query = "Select Catagory from " + Database + ".ReasonVisits where ltrim(rtrim(UPPER(ReasonVisit))) = ltrim(rtrim(UPPER('" + ReasonVisit.trim() + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                if (rset.getString(1).trim().toUpperCase().equals("COVID")) {
                    ReasonVisitCatagory = "COVID";
                } else {
                    ReasonVisitCatagory = "EMERGENCY";
                }
            rset.close();
            stmt.close();
            String inputFilePath = "";
            inputFilePath = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/CashReceipt.pdf";
            String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + PatientMRN + InvoiceNo + "_CashReceipt.pdf";
            OutputStream fos = new FileOutputStream(new File(outputFilePath));
            PdfReader pdfReader = new PdfReader(inputFilePath);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 14.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(300.0F, 632.0F);
                    pdfContentByte.showText(PatientName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 14.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250.0F, 597.0F);
                    pdfContentByte.showText(PaymentDate);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 14.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0F, 512.0F);
                    pdfContentByte.showText(ReasonVisitCatagory);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 14.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250.0F, 490.0F);
                    pdfContentByte.showText(ReasonVisit);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 14.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350.0F, 435.0F);
                    pdfContentByte.showText(PaidAmount);
                    pdfContentByte.endText();
                }
            }
            pdfStamper.close();
            File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + PatientMRN + InvoiceNo + "_CashReceipt.pdf");
            response.setContentLength((int) pdfFile.length());
            FileInputStream fileInputStream = new FileInputStream(pdfFile);
            ServletOutputStream servletOutputStream = response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1)
                servletOutputStream.write(bytes);
        } catch (Exception e) {
            out.println("Error in Cash Receipt Method: " + e.getMessage());
        }
    }

    void GetPatients(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Patient = request.getParameter("Patient").trim();
            StringBuffer PatientList = new StringBuffer();
            Query = " Select Id, MRN, FirstName, MiddleInitial, LastName, DOB, PhNumber  from " + Database + ".PatientReg where status = 0 and FirstName like '%" + Patient + "%' OR LastName like '%" + Patient + "%'  OR MRN like '%" + Patient + "%' OR PhNumber like '%" + Patient + "%' OR MiddleInitial like '%" + Patient + "%' ";
            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\" >");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option value=-1> Please Select Below Patient </option>");
            while (rset.next())
                PatientList.append("<option value=" + rset.getInt(1) + ">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
            rset.close();
            stmt.close();
            PatientList.append("</select>");
            out.println(PatientList.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void GetPatientsUploadDocs(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Patient = request.getParameter("Patient").trim();
            StringBuffer PatientList = new StringBuffer();
            Query = " Select Id, IFNULL(MRN,''), IFNULL(FirstName,''), IFNULL(MiddleInitial,''), IFNULL(LastName,''), IFNULL(DOB,''), IFNULL(PhNumber,''),  CONCAT(ID,',',IFNULL(MRN,''),',',IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,''))  from " + Database + ".PatientReg  where status = 0 and FirstName like '%" + Patient + "%' OR LastName like '%" + Patient + "%'  OR MRN like '%" + Patient + "%' OR PhNumber like '%" + Patient + "%'  OR MiddleInitial like '%" + Patient + "%' ";
            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\" >");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option value=-1> Please Select Below Patient </option>");
            while (rset.next())
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "," + rset.getString(2) + "," + rset.getString(3) + "," + rset.getString(4) + "," + rset.getString(5) + "," + rset.getString(6) + "," + rset.getString(7) + "\">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
            rset.close();
            stmt.close();
            PatientList.append("</select>");
            out.println(PatientList.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void GetPatientsEligibility(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Patient = request.getParameter("Patient").trim();
            StringBuffer PatientList = new StringBuffer();
            Query = "Select Id, MRN, FirstName, MiddleInitial, LastName, DOB, PhNumber  from " + Database + ".PatientReg where status = 0 and FirstName like '%" + Patient + "%' OR LastName like '%" + Patient + "%'  OR MRN like '%" + Patient + "%' OR PhNumber like '%" + Patient + "%'  OR MiddleInitial like '%" + Patient + "%'";
            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\" onchange=\"GetDetails();\">");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option value=-1> Please Select Below Patient </option>");
            while (rset.next())
                PatientList.append("<option value=" + rset.getInt(2) + ">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
            rset.close();
            stmt.close();
            PatientList.append("</select>");
            out.println(PatientList.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void GetProfessionalPayersList(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String ProfessionalPayer = request.getParameter("ProfessionalPayer").trim();
            StringBuffer ProfessionalPayersList = new StringBuffer();
            Query = "Select PayerId, PayerName from " + Database + ".ProfessionalPayers where PayerName like '%" + ProfessionalPayer + "%' order by status";
            ProfessionalPayersList.append("<label>Payer List</label>");
            ProfessionalPayersList.append("<select class=\"form-control select2\" id=\"ProfessionalPayer\" name=\"ProfessionalPayer\" >");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ProfessionalPayersList.append("<option value=-1> Please Select From Below Payers </option>");
            while (rset.next())
                ProfessionalPayersList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            rset.close();
            stmt.close();
            ProfessionalPayersList.append("</select>");
            out.println(ProfessionalPayersList.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void RegisteredPatientListExcel(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientCount = 0;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        try {
            Query = "SELECT COUNT(*) FROM " + Database + ".PatientReg";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                PatientCount = rset.getInt(1);
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
                Query = "SELECT CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), DATE_FORMAT(DOB,'%m/%d/%Y'), PhNumber, IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'SUSPECTED' ELSE 'NONE' END FROM " + Database + ".PatientReg where Status = 0 ORDER BY ID DESC ";
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
                    SNo++;
                }
                rset.close();
                stmt.close();
                out.println("</table>");
            }
        } catch (Exception var11) {
            out.println("Error in Excel Report: " + var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void DownloadPatientInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        int ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());
        try {
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/DownloadPatientInput.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void DownloadPatientList(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientCount = 0;
        int SNo = 1;
        int ClientIndex = 0;
        String ToDate = "";
        String FromDate = request.getParameter("FromDate").trim();
        ToDate = request.getParameter("ToDate").trim();
        ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());
        try {
            Query = "Select dbname from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                Database = rset.getString(1);
            rset.close();
            stmt.close();
            Query = "SELECT COUNT(*) FROM " + Database + ".PatientReg";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                PatientCount = rset.getInt(1);
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
                Query = " SELECT CONCAT(a.Title,' ',a.FirstName,' ',a.MiddleInitial,' ',a.LastName), DATE_FORMAT(a.DOB,'%m/%d/%Y'), a.PhNumber, IFNULL(a.MRN,0), IFNULL(a.ReasonVisit,'-'),  a.ID, IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T')),  CASE WHEN a.COVIDStatus = 1 THEN 'POSITIVE' WHEN a.COVIDStatus = 0 THEN 'NEGATIVE'  WHEN a.COVIDStatus = -1 THEN 'SUSPECTED' ELSE 'NONE' END,  a.SelfPayChk,IFNULL(a.Email,'-'), IFNULL(a.Address,'-'), IFNULL(c.PayerName,'-'),IFNULL(a.City,'-'), IFNULL(a.State, '-'), IFNULL(a.ZipCode, '-')  FROM " + Database + ".PatientReg a  LEFT JOIN " + Database + ".InsuranceInfo b on a.ID = b.PatientRegId   LEFT JOIN " + Database + ".ProfessionalPayers c on b.PriInsuranceName = c.Id  where a.Status = 0 and a.CreatedDate >= '" + FromDate + " 00:00:00' and a.CreatedDate <= '" + ToDate + " 23:59:59' ORDER BY ID DESC; ";
                System.out.println(Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    out.println("<tr  bgcolor=\"#FFFFFF\">");
                    out.println("<td class=\"fieldm\">" + SNo + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(4) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(1) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(2) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(3) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(11) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(13) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(14) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(15) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(5) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(7) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(10) + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(12) + "</td>");
                    out.println("</tr>");
                    SNo++;
                }
                rset.close();
                stmt.close();
                out.println("</table>");
            }
        } catch (Exception var11) {
            out.println("Error in Excel Report: " + var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    private Dictionary doUpload(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws Exception {
        try {
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
            int i;
            for (i = in.readLine(bytes, 0, 512); -1 != i; i = in.readLine(bytes, 0, 512)) {
                String st = new String(bytes, 0, i);
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
            throw new Exception("Error In Do Upload " + var20.getMessage());
        }
    }

    public void download_direct(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        String FileName = request.getParameter("fname");
        String path = request.getParameter("path");
        String RecordingPath = path + FileName;
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
            response.setHeader("Content-Disposition", "Inline; filename=\"" + FileName + "\";");
            FileInputStream fin = new FileInputStream(RecordingPath);
            byte[] content = new byte[fin.available()];
            fin.read(content);
            fin.close();
            ServletOutputStream servletOutputStream = response.getOutputStream();
            servletOutputStream.write(content);
            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }

    void GetPatientsMainScreen(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Patient = request.getParameter("Patient").trim();
            StringBuffer PatientList = new StringBuffer();
            //Query = "Select Id, MRN, IFNULL(FirstName,''), IFNULL(MiddleInitial,''), IFNULL(LastName,''), DOB, IFNULL(PhNumber ,'') from " + Database + ".PatientReg where status = 0 and FirstName like '%" + Patient + "%' OR LastName like '%" + Patient + "%'  OR MRN like '%" + Patient + "%' OR PhNumber like '%" + Patient + "%'  OR MiddleInitial like '%" + Patient + "%'";
            Query = "Select Id, MRN, IFNULL(FirstName,''), IFNULL(MiddleInitial,''), IFNULL(LastName,''), IFNULL(DOB,''), IFNULL(PhNumber,''),`Status`\n" +
                    "FROM " + Database + ".PatientReg \n" +
                    "WHERE status = 0 and CONCAT(FirstName,LastName,PhNumber,MRN) like '%" + Patient + "%' ";
//            Query = "Select Id, MRN, FirstName, MiddleInitial, LastName, DOB, PhNumber,`Status`\n" +
//                    "FROM PatientReg\n" +
//                    "WHERE status = 0 and CONCAT(MRN,FirstName,LastName,PhNumber) like '%39277709%'";
            PatientList.append("<select class=\"form-control\" id=\"PatientId\" name=\"PatientId\" onchange=\"OpenPatients(this.value);\">");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option value=''> Please Select Below Patient </option>");
            while (rset.next())
                PatientList.append("<option value=" + rset.getInt(1) + ">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
            rset.close();
            stmt.close();
            PatientList.append("</select>");
            out.println(PatientList.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
