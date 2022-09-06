
package md;

import DAL.Payments;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.*;
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
public class RegisteredPatients extends HttpServlet {

    public static int getAge(LocalDate dob) {
        LocalDate curDate = LocalDate.now();
        return Period.between(dob, curDate).getYears();
    }

    private static int copyFileUsingJava7Files(File source, File dest) throws IOException {
        File fe = new File(String.valueOf(dest));
        if (!fe.exists()) {
            Files.copy(source.toPath(), dest.toPath(), new java.nio.file.CopyOption[0]);
            return 1;
        }
        return 0;
    }

    public static boolean isStringOnlyAlphabet(String str) {
        return (str != null && !str.equals("") && str.matches("^[a-zA-Z]*$"));
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

    void GetValues(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Dashboards/DashBoardInput.html");
        } catch (Exception exception) {
        }
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        Services supp = new Services();
        UtilityHelper helper = new UtilityHelper();
        Payments payments = new Payments();
        try {
            HttpSession session = request.getSession(false);
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            String UserId = session.getAttribute("UserId").toString();
            String DatabaseName = session.getAttribute("DatabaseName").toString();
            int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            int UserType = Integer.parseInt(session.getAttribute("UserType").toString());
            if (UserId.equals("") || UserId.isEmpty()) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            String ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
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
                PatientsDocUpload(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("ShowReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View Patient Option", "Click on View Patient Option Patients List", FacilityIndex);
                ShowReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);

            } else if (ActionID.equals("ShowReportIncompleteForm")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View Patient Option", "Click on View Patient Option Patients List", FacilityIndex);
                ShowReportIncompleteForm(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("PatientsDocUpload_Save") && request.getContentType().startsWith("multipart/form-data")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs Upload Save", "Save the Documents for the selected patients", FacilityIndex);
                PatientsDocUpload_Save(request, out, conn, context, response, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("ViewDocuments_Input")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs View", "Click on View Documents Options", FacilityIndex);
                ViewDocuments_Input(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("ViewDocuments")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs View", "Docmuents Uploaded List", FacilityIndex);
                ViewDocuments(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("CreateInvoiceInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Create Invoice Option", "Invoice Craetion Add Diseases", FacilityIndex);
                CreateInvoiceInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("GetCost")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Cost", "Get the cost of the Diseases and Fill input fields", FacilityIndex);
                GetCost(request, out, conn, context, DatabaseName, helper);
            } else if (ActionID.equals("SaveInvoice")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Invoice ", "Save the Created Invoice Data in Tables and Details", FacilityIndex);
                SaveInvoice(request, out, conn, context, UserId, DatabaseName, helper);
            } else if (ActionID.equals("InvoicePdf")) {
                InvoicePdf(request, response, out, conn, UserId, DatabaseName, FacilityIndex, context, helper);
            } else if (ActionID.equals("CollectPayment")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Payement Collection Option", "Search the Invoice and Then Collect Payment Option", FacilityIndex);
                CollectPayment(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("CollectPayment_View")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Payement Collection Option", "View the List of Created Invoice Of selected Patient", FacilityIndex);
                CollectPayment_View(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, UserType);
            } else if (ActionID.equals("PayNow")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Pay Now Option", "Pay Now Option for the Created Invoice for Selected Pateint", FacilityIndex);
                PayNow(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, payments);
            } else if (ActionID.equals("PayNowSave")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Pay Now Option", "Save the Details Of Paid Amount for the Invoice Created for the Selected Patient", FacilityIndex);
                PayNowSave(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("DeActivePatient")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "DeActivate Patients", "Deactivate the Selected Patients from the View Patient Option and Search Old Patient Option", FacilityIndex);
                DeActivePatient(request, response, out, conn, context, UserId, DatabaseName, helper);
            } else if (ActionID.equals("ReActivePatient")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "ReActivate Patients", "ReActivate the Selected Patients from the Search Old Patient Option", FacilityIndex);
                ReActivePatient(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("DeActiveInvoice")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "DeActivate Invoice Option", "Deactivate the selected Invoice from The Collect Payment Option Where you can Search Invoice for the Respentive Patient", FacilityIndex);
                DeActiveInvoice(request, out, conn, context, DatabaseName, helper);
            } else if (ActionID.equals("TransactionReport_Input")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Transaction Report Option", "Transaction Report for the Selected Patient Input Option", FacilityIndex);
                TransactionReport_Input(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("TransactionReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Transaction Report Option", "View Transaction Report List", FacilityIndex);
                TransactionReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("AddDoctors")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Doctors Physicians", "Click on Add Doctor Physicians Option", FacilityIndex);
                AddDoctors(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("SaveDoctor")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Doctors Physicians", "Save Doctor or Physician", FacilityIndex);
                SaveDoctor(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("EditDoctor")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Edit Doctors Physicians", "Edit Doctor or Physician", FacilityIndex);
                EditDoctor(request, out, conn, context, DatabaseName, helper);
            } else if (ActionID.equals("UpdateDoctor")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update Doctors Physicians", "Save Doctor or Physician", FacilityIndex);
                UpdateDoctor(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("AddAlergyInfo")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Allergy Information", "Add Allergy Options Input", FacilityIndex);
                AddAlergyInfo(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("SaveAllergy")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Allergy Information", "Save Allergy Options And Other Information", FacilityIndex);
                SaveAllergy(request, out, conn, context, UserId, DatabaseName, helper);
            } else if (ActionID.equals("ShowHistory")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Show History for Allergy and Other Info", "Show Pdf for the Recent Allergy Information or any Other Info form View Patients Option or Search Old Patients Option", FacilityIndex);
                ShowHistory(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("CashReceipt")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "CashReceipt Download", "Show CashReceipt for Self Pay Patient Only", FacilityIndex);
                CashReceipt(request, out, conn, context, DatabaseName, FacilityIndex, helper, response);
            } else if (ActionID.equals("GetPatients")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patients List", "Get Patients List matching from the Fields Input", FacilityIndex);
                GetPatients(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetPatientsEligibility")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patients List For Eligibility Inquiry", "Get Patients List matching from the Fields Input For Eligibility Inquiry", FacilityIndex);
                GetPatientsEligibility(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetDateOfService")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patients List For Eligibility Inquiry", "Get Patients List matching from the Fields Input For Eligibility Inquiry", FacilityIndex);
                GetDateOfService(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetPatientsUploadDocs")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patients List For Docs Upload", "Get Patients List matching from the Fields Input For Docs Upload", FacilityIndex);
                GetPatientsUploadDocs(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetProfessionalPayersList")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Professional Payer List For Eligibility Check", "Get Professional Payer LIst matching from the Fields Input For Eligbility Check", FacilityIndex);
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
                helper.deleteUserSession(request, conn, session.getId());
                session.invalidate();
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (handleRequest)", context, e, "RegisteredPatients", "handleRequest", conn);
            Services.DumException("RegisteredPatients", "Handle Request", request, e, getServletContext());
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
                helper.SendEmailWithAttachment("Error in RegisteredPatients ** (handleRequest -- SqlException)", context, e, "RegisteredPatients", "handleRequest", conn);
                Services.DumException("RegisteredPatients", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }

    void ShowReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query1 = "";
        String Query2 = "";
        String BundleFnName = "";
        String LabelFnName = "";
        int PatientCount = 0;
        int VictoriaDetailsFound = 0;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        int SNo = 0;
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
/*                if (ClientId == 17) {
                    Query = " SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), " +//1
                            "DATE_FORMAT(DOB,'%m/%d/%Y'),  IFNULL(PhNumber,''), IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, " + //6
                            "IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'), DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')),  " + //7
                            "CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'NONE' ELSE 'NONE' END, " +//8
                            "CASE WHEN SelfPayChk = 1 THEN 'Insured' WHEN SelfPayChk = 0 THEN 'Self Pay' ELSE 'Self Pay' END  " +//9
                            "FROM " + Database + ".PatientReg where Status = 0 ORDER BY ViewDate DESC limit 500";
                } else {
                    Query = " SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), " +//1
                            "DATE_FORMAT(DOB,'%m/%d/%Y'),  IFNULL(PhNumber,''), IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, " +//6
                            "IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'), DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')),  " + //7
                            "CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'NONE' WHEN COVIDStatus == null THEN 'NONE' ELSE 'NONE' END, " +//8
                            "CASE WHEN SelfPayChk = 1 THEN 'Insured' WHEN SelfPayChk = 0 THEN 'Self Pay' ELSE 'Self Pay' END " +//9
                            " FROM " + Database + ".PatientReg where Status = 0 and LENGTH(MRN) = 6 ORDER BY ViewDate DESC limit 500";
                }*/
                Query = " SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), " +//1
                        "DATE_FORMAT(DOB,'%m/%d/%Y'),  IFNULL(PhNumber,''), IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, " +//6
                        "IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'), DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')),  " + //7
                        "CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'NONE' WHEN COVIDStatus = null THEN 'NONE' ELSE 'NONE' END, " +//8
                        "CASE WHEN SelfPayChk = 1 THEN 'Insured' WHEN SelfPayChk = 0 THEN 'Self Pay' ELSE 'Self Pay' END " +//9
                        " FROM " + Database + ".PatientReg where Status = 0 ORDER BY ViewDate DESC limit 500";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    CDRList.append("<tr>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                    if (ClientId == 9 || ClientId == 28) {
                        Query1 = "Select IFNULL(COUNT(*),0) from " + Database + ".PatientReg_Details where PatientRegId = " + rset.getInt(6);
                        stmt1 = conn.createStatement();
                        rset1 = stmt1.executeQuery(Query1);
                        if (rset1.next()) {
                            VictoriaDetailsFound = rset1.getInt(1);
                        }
                        rset1.close();
                        stmt1.close();
                        if (VictoriaDetailsFound > 0) {
                            Query2 = "Select CASE WHEN HealthInsuranceChk = 1 THEN 'Insured' WHEN HealthInsuranceChk = 0 THEN 'Self Pay' ELSE 'SelfPay' END " +
                                    " from " + Database + ".PatientReg_Details where PatientRegId = " + rset.getInt(6);
                            stmt2 = conn.createStatement();
                            rset2 = stmt2.executeQuery(Query2);
                            if (rset2.next()) {
                                CDRList.append("<td align=left>" + rset2.getString(1) + "</td>\n");//SelfPayChk /// Victoria ERDALLAS
                            }
                            rset2.close();
                            stmt2.close();
                        } else {
                            CDRList.append("<td align=left>-</td>\n");//SelfPayChk /// Victoria ERDALLAS
                        }
                    } else {
                        CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");//SelfPayChk
                    }
                    CDRList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-5\" onclick=\"UpdateInfoPatient(" + rset.getInt(6) + ")\">View</button></td>\n");
                    CDRList.append("</tr>\n");
                }
                rset.close();
                stmt.close();
            }
//            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
//            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
//            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("UserId", String.valueOf(UserId));
//            Parser.SetField("Header", String.valueOf(Header));
//            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
//            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowRegisteredPatient.html");
        } catch (Exception e) {
            if (conn == null) {
                conn = Services.getMysqlConn(servletContext);
            }
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (ShowReport)", servletContext, e, "RegisteredPatients", "ShowReport", conn);
            Services.DumException("ShowReport", "RegisteredPatients ", request, e, servletContext);
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

    void ShowReportIncompleteForm(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query1 = "";
        String Query2 = "";
        String BundleFnName = "";
        String LabelFnName = "";
        int PatientCount = 0;
        int VictoriaDetailsFound = 0;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        int SNo = 0;
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
/*                if (ClientId == 17) {
                    Query = " SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), " +//1
                            "DATE_FORMAT(DOB,'%m/%d/%Y'),  IFNULL(PhNumber,''), IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, " + //6
                            "IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'), DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')),  " + //7
                            "CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'NONE' ELSE 'NONE' END, " +//8
                            "CASE WHEN SelfPayChk = 1 THEN 'Insured' WHEN SelfPayChk = 0 THEN 'Self Pay' ELSE 'Self Pay' END  " +//9
                            "FROM " + Database + ".PatientReg where Status = 0 ORDER BY ViewDate DESC limit 500";
                } else {
                    Query = " SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), " +//1
                            "DATE_FORMAT(DOB,'%m/%d/%Y'),  IFNULL(PhNumber,''), IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, " +//6
                            "IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'), DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')),  " + //7
                            "CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'NONE' WHEN COVIDStatus == null THEN 'NONE' ELSE 'NONE' END, " +//8
                            "CASE WHEN SelfPayChk = 1 THEN 'Insured' WHEN SelfPayChk = 0 THEN 'Self Pay' ELSE 'Self Pay' END " +//9
                            " FROM " + Database + ".PatientReg where Status = 0 and LENGTH(MRN) = 6 ORDER BY ViewDate DESC limit 500";
                }*/
                Query = " SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), " +//1
                        "DATE_FORMAT(DOB,'%m/%d/%Y'),  IFNULL(PhNumber,''), IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, " +//6
                        "IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'), DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')),  " + //7
                        "CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'NONE' WHEN COVIDStatus = null THEN 'NONE' ELSE 'NONE' END, " +//8
                        "CASE WHEN SelfPayChk = 1 THEN 'Insured' WHEN SelfPayChk = 0 THEN 'Self Pay' ELSE 'Self Pay' END " +//9
                        " FROM " + Database + ".PatientReg where Status = 999 ORDER BY ViewDate DESC limit 500";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    CDRList.append("<tr>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                    if (ClientId == 9 || ClientId == 28) {
                        Query1 = "Select IFNULL(COUNT(*),0) from " + Database + ".PatientReg_Details where PatientRegId = " + rset.getInt(6);
                        stmt1 = conn.createStatement();
                        rset1 = stmt1.executeQuery(Query1);
                        if (rset1.next()) {
                            VictoriaDetailsFound = rset1.getInt(1);
                        }
                        rset1.close();
                        stmt1.close();
                        if (VictoriaDetailsFound > 0) {
                            Query2 = "Select CASE WHEN HealthInsuranceChk = 1 THEN 'Insured' WHEN HealthInsuranceChk = 0 THEN 'Self Pay' ELSE 'SelfPay' END " +
                                    " from " + Database + ".PatientReg_Details where PatientRegId = " + rset.getInt(6);
                            stmt2 = conn.createStatement();
                            rset2 = stmt2.executeQuery(Query2);
                            if (rset2.next()) {
                                CDRList.append("<td align=left>" + rset2.getString(1) + "</td>\n");//SelfPayChk /// Victoria ERDALLAS
                            }
                            rset2.close();
                            stmt2.close();
                        } else {
                            CDRList.append("<td align=left>-</td>\n");//SelfPayChk /// Victoria ERDALLAS
                        }
                    } else {
                        CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");//SelfPayChk
                    }
                    CDRList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-5\" onclick=EditInfoPatient(\"/md/md.PatientReg?ActionID=EditValues_New&MRN=" + rset.getString(4) + "&ClientId=" + ClientId + "\")>Edit</button></td>\n");
                    CDRList.append("</tr>\n");
                }
                rset.close();
                stmt.close();
            }
//            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
//            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
//            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Msg", "[ Incomplete Forms ]");

//            Parser.SetField("Header", String.valueOf(Header));
//            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
//            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowRegisteredPatient.html");
        } catch (Exception e) {
            if (conn == null) {
                conn = Services.getMysqlConn(servletContext);
            }
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (ShowReportIncompleteForm)", servletContext, e, "RegisteredPatients", "ShowReportIncompleteForm", conn);
            Services.DumException("ShowReportIncompleteForm", "RegisteredPatients ", request, e, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
//            out.println(var11.getMessage());
//            String str = "";
//            for (int i = 0; i < (var11.getStackTrace()).length; i++)
//                str = str + var11.getStackTrace()[i] + "<br>";
//            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void PatientsDocUpload(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        try {
            SupportiveMethods suppMethods = new SupportiveMethods();
            StringBuffer LeftSideBarMenu = new StringBuffer();
            StringBuffer Header = new StringBuffer();
            StringBuffer Footer = new StringBuffer();
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            StringBuffer PatientList = new StringBuffer();

            Query = "SELECT CONCAT(ID,',',MRN,',',Title,' ',FirstName,' ',MiddleInitial,' ',LastName), CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), MRN FROM " + Database + ".PatientReg where Status = 0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option class=Inner value=-1>Select Patient</option>");
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
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/UploadPatientsDocs.html");
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (PatientsDocUpload)", servletContext, ex, "RegisteredPatients", "PatientsDocUpload", conn);
            Services.DumException("PatientsDocUpload", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#003");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            //System.out.println(ex.getMessage());
        }
    }

    void PatientsDocUpload_Save(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
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
        String VisitNo = "";

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
//                if (key.endsWith(".jpg") || key.endsWith(".png") || key.endsWith(".pdf") || key.endsWith(".txt") || key.endsWith(".csv") || key.endsWith(".doc")  || key.endsWith(".docx") || key.endsWith(".xlsx")) {
//                    FileName = key;
//                    FileFound = true;
//                    ByteArrayOutputStream baos = null;
//                    baos = (ByteArrayOutputStream) d.get(key);
//                    Data = baos.toByteArray();
//                }
                if (key.toUpperCase().endsWith(".JPG") || key.toUpperCase().endsWith(".JPEG") || key.endsWith(".PNG") || key.endsWith(".PDF") || key.endsWith(".TXT") || key.endsWith(".CSV") || key.endsWith(".DOC") || key.endsWith(".DOCX") || key.endsWith(".XLSX")) {
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
                } else if (key.startsWith("visitDos")) {
                    VisitNo = (String) d.get(key);
                }
                if (FileFound) {
                    System.out.println(FileName);
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
            PatientId = PatientId.substring(4);
            DocumentName = DocumentName.substring(4);
            UserId = UserId.substring(4);
            VisitNo = VisitNo.substring(4);


//            out.println("Visit No "+VisitNo);
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
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "Insert into " + Database + ".PatientDocUpload (PremisisId, PatientRegId, PatientMRN, PatientName, " +
                                "UploadDocumentName, FileName, CreatedBy, CreatedDate, DocumentType,VisitIdx) values (?,?,?,?,?,?,?,now(),?,?) ");
                MainReceipt.setInt(1, PremisisId);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, PatientMRN);
                MainReceipt.setString(4, PatientName);
                MainReceipt.setString(5, DocumentName);
                MainReceipt.setString(6, FileName);
                MainReceipt.setString(7, UserId);
                MainReceipt.setString(8, DocumentType);
                MainReceipt.setString(9, VisitNo);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in RegisteredPatients ** (PatientsDocUpload_Save)", servletContext, e, "RegisteredPatients", "PatientsDocUpload_Save", conn);
                Services.DumException("PatientsDocUpload_Save", "RegisteredPatients ", request, e, servletContext);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "PatientsDocUpload");
                Parser.SetField("Message", "MES#004");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                //out.println("Error in Insertion:-" + e.getMessage());
            }
            String target = "";
            String firstname = "";
            String lastname = "";
            String Message = "";
            if (DocumentType.compareTo("1") == 0) {
                Query = "SELECT directory_1 FROM oe.clients WHERE Id = " + PremisisId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    target = rset.getString(1);
                rset.close();
                stmt.close();
                File source = new File(String.valueOf(String.valueOf(UploadPath)) + FileName);
                File dest = new File(String.valueOf("/opt/" + FileName));
                int i = copyFileUsingJava7Files(source, dest);
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", String.valueOf("File Has been Uploaded Successfully" + Message));
            Parser.SetField("FormName", String.valueOf("RegisteredPatients"));
            Parser.SetField("ActionID", String.valueOf("PatientsDocUpload"));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Success.html");
        } catch (Exception e2) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (PatientsDocUpload_Save MES#005)", servletContext, e2, "RegisteredPatients", "PatientsDocUpload_Save MES#005", conn);
            Services.DumException("PatientsDocUpload_Save", "RegisteredPatients MES#005", request, e2, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "PatientsDocUpload");
            Parser.SetField("Message", "MES#005");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println("Error in Upload DOcuments!!" + e2.getMessage());
            String str = "";
            for (int i = 0; i < (e2.getStackTrace()).length; i++)
                str = str + e2.getStackTrace()[i] + "<br>";
            out.println(str);*/
        }
    }

    void ViewDocuments_Input(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
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
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (ViewDocuments_Input)", servletContext, e, "RegisteredPatients", "ViewDocuments_Input", conn);
            Services.DumException("ViewDocuments_Input", "RegisteredPatients ", request, e, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#006");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void ViewDocuments(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        final String PatientName = "";
        final String PatientMRN = "";
        final SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        final StringBuffer CDRList = new StringBuffer();
        final StringBuffer PatientList = new StringBuffer();
        int SNo = 1;
        String DirectoryName = "";
        String DocumentPath = "";
        try {
            Query = "Select DirectoryName from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DirectoryName = rset.getString(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            Services.DumException("RegisteredPatient2", "PatientDocUplaodSave", request, e, servletContext);
        }
        try {
            final InetAddress ip = InetAddress.getLocalHost();
            final String hostname = ip.getHostName();
            PatientRegId = Integer.parseInt(request.getParameter("PatientId").trim());
            Query = "SELECT ID, CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), MRN FROM " + Database + ".PatientReg where Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ")</option>");
            }
            rset.close();
            stmt.close();

//
//            PreparedStatement ps = conn.prepareStatement(" Select " +
//                    "CASE WHEN IDFront_Status = 0 THEN IDFront ELSE NULL END,\n" +
//                    "CASE WHEN InsuranceFront_Status = 0 THEN InsuranceFront ELSE NULL END,\n" +
//                    "CASE WHEN InsuranceBack_Status = 0 THEN InsuranceBack ELSE NULL END, " +
//                    " MRN , FirstName ,CreatedDate " +
//                    " from " + Database + ".PatientReg where ID = '" + PatientRegId + "' and status = 0");
//            rset = ps.executeQuery();
//            if (rset.next()) {
//                if (rset.getString(1) != null) {
//                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
//                    CDRList.append("<td align=left> ID Front </td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
//                    CDRList.append("<td align=left><a href=\"md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(1) + "&path=/sftpdrive/AdmissionBundlePdf/Attachment/Victoria/Uploads/\" target=\"_blank\">ID_Front.png</a></td>\n");
//                    CDRList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteIDs('IS'," + PatientRegId + ")\"></i></td>\n");
//                    CDRList.append("</tr>");
//                    SNo++;
//                }
//                if (rset.getString(2) != null) {
//                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
//                    CDRList.append("<td align=left> Insurance Front </td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
//                    CDRList.append("<td align=left><a href=\"md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(2) + "&path=/sftpdrive/AdmissionBundlePdf/Attachment/Victoria/Uploads/\" target=\"_blank\">Insurance_Front.png</a></td>\n");
//                    CDRList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteIDs('IF'," + PatientRegId + ")\"></i></td>\n");
//                    CDRList.append("</tr>");
//                    SNo++;
//                }
//                if (rset.getString(3) != null) {
//
//                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
//                    CDRList.append("<td align=left> Insurance Back </td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
//                    CDRList.append("<td align=left><a href=\"md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(3) + "&path=/sftpdrive/AdmissionBundlePdf/Attachment/Victoria/Uploads/\" target=\"_blank\">Insurance_Back.png</a></td>\n");
//                    CDRList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteIDs('IB'," + PatientRegId + ")\"></i></td>\n");
//                    CDRList.append("</tr>");
//                    SNo++;
//                }
//            }
//            rset.close();
//            ps.close();


            Query = "SELECT a.PatientMRN, a.PatientName, a.UploadDocumentName, a.FileName, IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')), a.Id FROM " + Database + ".PatientDocUpload a  LEFT JOIN " + Database + ".PatientReg b ON a.PatientRegId = b.ID WHERE a.Status = 0 and a.PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                final File tmpFile = new File("/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "/" + rset.getString(4));
                final boolean exists = tmpFile.exists();
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
                if (hostname.trim().equals("romver-01")) {
                    CDRList.append("<td align=left><a href=https://rovermd.com:8443/md/md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(4) + "&path=" + DocumentPath + " target=\"_blank\">" + rset.getString(4) + "</a></td>\n");
                } else {
                    CDRList.append("<td align=left><a href=https://app.rovermd.com:8443/md/md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(4) + "&path=" + DocumentPath + " target=\"_blank\">" + rset.getString(4) + "</a></td>\n");
                }
                CDRList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteDocument(" + rset.getInt(6) + ")\"></i></td>\n");
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
        } catch (Exception ex) {
            Services.DumException("ViewDocuments", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser2 = new Parsehtm(request);
            Parser2.SetField("FormName", "RegisteredPatients");
            Parser2.SetField("ActionID", "ViewDocuments_Input");
            Parser2.SetField("Message", "MES#007");
            Parser2.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void DeleteDocument(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database, UtilityHelper helper) throws FileNotFoundException {
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
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (DeleteDocument)", servletContext, ex, "RegisteredPatients", "DeleteDocument", conn);
            Services.DumException("DeleteDocument", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "ViewDocuments_Input");
            Parser.SetField("Message", "MES#008");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println("Error: Updating PatientDocUpload Table " + e.getMessage());
            Services.DumException("RegisteredPatients", "Delete DOC - Updating PatientDocUpload table :", request, e, getServletContext());
            return;*/
        }
    }

    void CreateInvoiceInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer diseaseList = new StringBuffer();
        StringBuffer PatientList = new StringBuffer();
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuilder DOS = new StringBuilder();

        try {
            Query = "SELECT ID, CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), " +
                    "MRN, DATE_FORMAT(DOB,'%d_%m-%Y') FROM " + Database + ".PatientReg where Status = 0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                PatientList.append("<option class=Inner value=\"" + rset.getString(3) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ") (" + rset.getString(4) + ")</option>");
            rset.close();
            stmt.close();
            Query = "SELECT Id, CONCAT('(',Catagory, ') ', Description) FROM " + Database + ".SelfPaySheet order by Id desc";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            diseaseList.append("<option class=Inner value=''>Select Payment </option>");
            while (rset.next())
                diseaseList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            rset.close();
            stmt.close();

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("diseaseList", String.valueOf(diseaseList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/InvoiceSheetInput.html");
        } catch (Exception var11) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (CreateInvoiceInput)", servletContext, var11, "RegisteredPatients", "CreateInvoiceInput", conn);
            Services.DumException("CreateInvoiceInput", "RegisteredPatients ", request, var11, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#009");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            Services.DumException("Invoice Sheet Input", "CreateInvoice RegisteredPatient ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void GetCost(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Cost = "";

        try {
            int DiseaseId = Integer.parseInt(request.getParameter("DiseaseId").trim());
            Query = "Select IFNULL(Cost,'0') from " + Database + ".SelfPaySheet where Id = " + DiseaseId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                Cost = rset.getString(1);
            rset.close();
            stmt.close();

            out.println(Cost);
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (GetCost)", servletContext, ex, "RegisteredPatients", "GetCost", conn);
            Services.DumException("GetCost", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#010");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void SaveInvoice(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, UtilityHelper helper) throws FileNotFoundException {
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
        String VisitID = request.getParameter("visit").trim();
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
                helper.SendEmailWithAttachment("Error in RegisteredPatients ** (SaveInvoice -- 01)", servletContext, e, "RegisteredPatients", "SaveInvoice -- 01", conn);
                Services.DumException("SaveInvoice -- 01", "RegisteredPatients ", request, e, servletContext);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatient");
                Parser.SetField("ActionID", "CreateInvoiceInput");
                Parser.SetField("Message", "MES#011");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".InvoiceMaster (PatientMRN,InvoiceNo,TotalAmount ,PaidAmount,Paid,PaymentDateTime," +
                                "InvoiceCreatedBy ,CreatedDate, Status, BalAmount,InstallmentApplied,VisitID) " +
                                "VALUES (?,?,?,?,?,?,?,now(),0,?,0,?) ");
                MainReceipt.setString(1, PatientMRN);
                MainReceipt.setString(2, InvoiceNo);
                MainReceipt.setDouble(3, TotalAmount);
                MainReceipt.setDouble(4, 0.0D);
                MainReceipt.setInt(5, 0);
                MainReceipt.setString(6, "0000-00-00");
                MainReceipt.setString(7, UserId);
                MainReceipt.setDouble(8, TotalAmount);
                MainReceipt.setDouble(8, TotalAmount);
                MainReceipt.setString(9, VisitID);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in RegisteredPatients ** (SaveInvoice -- 02)", servletContext, e, "RegisteredPatients", "SaveInvoice -- 02", conn);
                Services.DumException("SaveInvoice -- 02", "RegisteredPatients ", request, e, servletContext);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatient");
                Parser.SetField("ActionID", "CreateInvoiceInput");
                Parser.SetField("Message", "MES#012");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
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
                helper.SendEmailWithAttachment("Error in RegisteredPatients ** (SaveInvoice -- 03)", servletContext, e, "RegisteredPatients", "SaveInvoice -- 03", conn);
                Services.DumException("SaveInvoice -- 03", "RegisteredPatients ", request, e, servletContext);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatient");
                Parser.SetField("ActionID", "CreateInvoiceInput");
                Parser.SetField("Message", "MES#013");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
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

                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + Database + ".InvoiceDetail (InvoiceMasterId,PatientMRN,InvoiceNo ," +
                                    "DiseaseId,CostPerDisease,Quantity,CreatedDate ,CreatedBy) " +
                                    "VALUES (?,?,?,?,?,?,now(),?) ");
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
                helper.SendEmailWithAttachment("Error in RegisteredPatients ** (SaveInvoice -- 04)", servletContext, e, "RegisteredPatients", "SaveInvoice -- 04", conn);
                Services.DumException("SaveInvoice -- 04", "RegisteredPatients ", request, e, servletContext);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatient");
                Parser.SetField("ActionID", "CreateInvoiceInput");
                Parser.SetField("Message", "MES#014");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            out.println("1|" + PatientMRN + "|" + InvoiceNo);
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (SaveInvoice -- 05)", servletContext, ex, "RegisteredPatients", "SaveInvoice -- 05", conn);
            Services.DumException("SaveInvoice -- 05", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatient");
            Parser.SetField("ActionID", "CreateInvoiceInput");
            Parser.SetField("Message", "MES#015");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println("Error: " + var11.getMessage());
            String str = "";
            for (i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    private void InvoicePdf(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String UserId, String Database, int ClientId, ServletContext servletContext, UtilityHelper helper) throws FileNotFoundException {
        String PatientMRN = request.getParameter("PatientMRN");
        String InvoiceNo = request.getParameter("InvoiceNo");
        int VisitID = Integer.parseInt(request.getParameter("VisitID").trim());
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
        //int VisitID = 0;
        String FileName = "";
        String DirectoryName = "";
        String nDOS = "";
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
                Query = "SELECT IFNULL(InvoiceNo,''), IFNULL(TotalAmount,''), IFNULL(PaidAmount,''), " +
                        "IFNULL(DATE_FORMAT(CreatedDate,'%m/%d/%Y'),''), IFNULL(VisitID,0) " +
                        "FROM " + Database + ".InvoiceMaster " +
                        " where PatientMRN = '" + PatientMRN + "' AND " +
                        " InvoiceNo='" + InvoiceNo + "' ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    TotalAmount = rset.getDouble(2);
                    PaidAmount = rset.getDouble(3);
                    InvoiceCreatedDate = rset.getString(4);
                    //VisitID = rset.getInt(5);
                }
                rset.close();
                stmt.close();


                Query = "SELECT DATE_FORMAT(DateofService,'%m/%d/%Y') " +
                        " FROM " + Database + ".PatientVisit WHERE Id = " + VisitID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    nDOS = rset.getString(1);
                }
                rset.close();
                stmt.close();

                Query = " Select CASE WHEN PayMethod = 1 THEN 'Credit Card' WHEN PayMethod = 2 THEN 'Cash' WHEN PayMethod = 3 THEN 'BOLT' WHEN PayMethod = 4 THEN 'Ingenico' ELSE '' END " +
                        " from " + Database + ".PaymentReceiptInfo where PatientMRN = '" + PatientMRN + "' and InvoiceNo = '" + InvoiceNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    PayMethod = rset.getString(1);
                }
                rset.close();
                stmt.close();

            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in RegisteredPatients ** (InvoicePdf -- 01)", servletContext, e, "RegisteredPatients", "InvoicePdf -- 01", conn);
                Services.DumException("InvoicePdf -- 01", "RegisteredPatients ", request, e, servletContext);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatient");
                Parser.SetField("ActionID", "CreateInvoiceInput");
                Parser.SetField("Message", "MES#016");
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
            HeaderFooter header = new HeaderFooter(p, false);
            header.setBorder(0);
            header.setAlignment(1);
            document.setHeader(header);
            document.open();
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));
            try {
                Query = " Select CONCAT(IFNULL(a.FirstName,''), ' ', IFNULL(a.MiddleInitial,''), ' ', IFNULL(a.LastName,'')), IFNULL(b.name,''), IFNULL(a.Gender,''), " +
                        "IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''), IFNULL(a.Age,''),  IFNULL(DATE_FORMAT(a.CreatedDate,'%m/%d/%Y'),'') as DOS, " +
                        "IFNULL(DATE_FORMAT(a.DOB,'%Y-%m-%d'),'')" +
                        "from " + Database + ".PatientReg a " +
                        "LEFT JOIN oe.clients b on a.ClientIndex = b.Id  " +
                        "where a.MRN = '" + PatientMRN + "'";
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
                helper.SendEmailWithAttachment("Error in RegisteredPatients ** (InvoicePdf -- 02)", servletContext, e2, "RegisteredPatients", "InvoicePdf -- 02", conn);
                Services.DumException("InvoicePdf -- 02", "RegisteredPatients ", request, e2, servletContext);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatient");
                Parser.SetField("ActionID", "CreateInvoiceInput");
                Parser.SetField("Message", "MES#017");
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
            datatable1.addCell(new Paragraph(" ", normfont2));
            datatable1.addCell(new Paragraph("INVOICE DATE: ", normfont2));
            datatable1.addCell(new Paragraph(InvoiceCreatedDate, UnderLine));
            datatable1.addCell(new Paragraph("", normfont2));
            datatable1.setDefaultCellBorder(13);
            datatable1.addCell(new Paragraph(PatientName, normfont));
            datatable1.setDefaultCellBorder(0);
            datatable1.addCell(new Paragraph("", normfont2));
            datatable1.setDefaultColspan(1);
            datatable1.setDefaultHorizontalAlignment(0);
            datatable1.addCell(new Paragraph(" ", normfont2));
            datatable1.addCell(new Paragraph("INVOICE NO: ", normfont2));
            datatable1.addCell(new Paragraph(InvoiceNo, UnderLine));
            datatable1.addCell(new Paragraph("", normfont2));
            datatable1.setDefaultCellBorder(12);
            datatable1.addCell(new Paragraph(ClientName + "         Sex:" + Sex, normfont));
            datatable1.setDefaultCellBorder(0);
            datatable1.addCell(new Paragraph("", normfont2));
            datatable1.setDefaultColspan(1);
            datatable1.setDefaultHorizontalAlignment(0);
            datatable1.addCell(new Paragraph(" ", normfont2));
            datatable1.addCell(new Paragraph("", normfont2));
            datatable1.addCell(new Paragraph("", UnderLine));
            datatable1.addCell(new Paragraph("", normfont2));
            datatable1.setDefaultCellBorder(12);
            datatable1.addCell(new Paragraph("DOB: " + DOB + "         Age: " + Age, normfont));
            datatable1.setDefaultCellBorder(0);
            datatable1.addCell(new Paragraph("", normfont2));
            datatable1.setDefaultColspan(1);
            datatable1.setDefaultHorizontalAlignment(0);
            datatable1.addCell(new Paragraph(" ", normfont2));
            datatable1.addCell(new Paragraph("", normfont2));
            datatable1.addCell(new Paragraph("", UnderLine));
            datatable1.addCell(new Paragraph("", normfont2));
            datatable1.setDefaultCellBorder(14);
//            datatable1.addCell((Phrase) new Paragraph("MRN: " + PatientMRN + "         DOS: " + DOS, normfont));
            datatable1.addCell(new Paragraph("MRN: " + PatientMRN, normfont));
            datatable1.setDefaultCellBorder(0);
            datatable1.addCell(new Paragraph("", normfont2));
            document.add(datatable1);
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
            datatable2.addCell(new Paragraph(" ", normfont3));
            datatable2.addCell(new Paragraph("Name: ", normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.addCell(new Paragraph(PatientName, normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell(new Paragraph(" ", normfont3));
            datatable2.addCell(new Paragraph("Age: ", normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.addCell(new Paragraph(Age, normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell(new Paragraph(" ", normfont3));
            datatable2.addCell(new Paragraph("Date of Birth: ", normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.addCell(new Paragraph(DOB, normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell(new Paragraph(" ", normfont3));
            datatable2.addCell(new Paragraph("Sex: ", normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.addCell(new Paragraph(Sex, normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell(new Paragraph(" ", normfont3));
            datatable2.addCell(new Paragraph("DOS: ", normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.addCell(new Paragraph(DOS, normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.addCell(new Paragraph("Payment Method:", normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.addCell(new Paragraph(PayMethod, normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            datatable2.addCell(new Paragraph("", normfont3));
            document.add(datatable2);
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
            datatable3.addCell(new Paragraph(" ", MainHeading));
            datatable3.setDefaultCellBorder(15);
            datatable3.addCell(new Paragraph("SNo.", MainHeading));
            datatable3.setDefaultCellBorder(11);
            datatable3.addCell(new Paragraph(" Disease Name (Procedure)", MainHeading));
            datatable3.setDefaultCellBorder(11);
            datatable3.addCell(new Paragraph("Cost Per Disease", MainHeading));
            datatable3.setDefaultCellBorder(11);
            datatable3.addCell(new Paragraph("QTY", MainHeading));
            datatable3.setDefaultCellBorder(11);
            datatable3.addCell(new Paragraph("Amount", MainHeading));
            datatable3.setDefaultCellBorder(0);
            datatable3.addCell(new Paragraph("", MainHeading));
            Query = "Select CONCAT('(',b.Catagory,')', ' ', b.Description), a.CostPerDisease, a.Quantity " +
                    "from " + Database + ".InvoiceDetail a  " +
                    "LEFT JOIN " + Database + ".SelfPaySheet b on a.DiseaseId = b.Id " +
                    "where PatientMRN = " + PatientMRN + " and ltrim(rtrim(a.InvoiceNo)) = ltrim(rtrim('" + InvoiceNo.trim() + "'))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Sno++;
                datatable3.setDefaultColspan(1);
                datatable3.setDefaultHorizontalAlignment(0);
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell(new Paragraph(" ", normfont3));
                datatable3.setDefaultCellBorder(12);
                datatable3.addCell(new Paragraph(String.valueOf(Sno), normfont3));
                datatable3.setDefaultCellBorder(8);
                datatable3.addCell(new Paragraph(rset.getString(1), normfont3));
                datatable3.setDefaultCellBorder(8);
                datatable3.addCell(new Paragraph(rset.getString(2), normfont3));
                datatable3.setDefaultCellBorder(8);
                datatable3.addCell(new Paragraph(rset.getString(3), normfont3));
                datatable3.setDefaultCellBorder(8);
                datatable3.addCell(new Paragraph(String.valueOf(rset.getDouble(2) * rset.getDouble(3)), normfont3));
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell(new Paragraph("", normfont3));
            }
            rset.close();
            stmt.close();
            datatable3.setDefaultColspan(1);
            datatable3.setDefaultHorizontalAlignment(0);
            datatable3.setDefaultCellBorder(0);
            datatable3.addCell(new Paragraph(" ", MainHeading));
            datatable3.setDefaultCellBorder(14);
            datatable3.addCell(new Paragraph("", normfont2));
            datatable3.setDefaultCellBorder(10);
            datatable3.addCell(new Paragraph("", MainHeading));
            datatable3.setDefaultCellBorder(10);
            datatable3.addCell(new Paragraph("", MainHeading));
            datatable3.setDefaultCellBorder(10);
            datatable3.addCell(new Paragraph("", MainHeading));
            datatable3.setDefaultCellBorder(10);
            datatable3.addCell(new Paragraph("", MainHeading));
            datatable3.setDefaultCellBorder(0);
            datatable3.addCell(new Paragraph("", MainHeading));
            document.add(datatable3);
            document.add(new Paragraph("\n"));
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
            datatable4.addCell(new Paragraph(" ", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("Total ", MainHeading));
            datatable4.setDefaultCellBorder(15);
            datatable4.addCell(new Paragraph(String.valueOf(TotalAmount), normfont3));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("", MainHeading));
            datatable4.setDefaultColspan(1);
            datatable4.setDefaultHorizontalAlignment(0);
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph(" ", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("Paid Amount ", MainHeading));
            datatable4.setDefaultCellBorder(15);
            datatable4.addCell(new Paragraph(String.valueOf(PaidAmount), normfont3));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("", MainHeading));
            datatable4.setDefaultColspan(1);
            datatable4.setDefaultHorizontalAlignment(0);
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph(" ", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("Balance ", MainHeading));
            datatable4.setDefaultCellBorder(15);
            datatable4.addCell(new Paragraph(String.valueOf(numFormat.format(TotalAmount - PaidAmount)), normfont3));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell(new Paragraph("", MainHeading));
            document.add(datatable4);
            document.add(new Paragraph("\n"));
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
            datatable5.addCell(new Paragraph(" ", normfont3));
            datatable5.addCell(new Paragraph("Patient Signature: ___________________", normfont3));
            document.add(datatable5);
            document.close();
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Progma", "no-cache");
            response.setContentType("application/pdf");
            response.setContentLength(baos.size());
            ServletOutputStream out2 = response.getOutputStream();
            baos.writeTo(out2);
            out2.flush();
            try {
                conn.close();
            } catch (Exception ex) {
                out.println(ex.getMessage());
            }
        } catch (Exception e3) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (InvoicePdf -- 03)", servletContext, e3, "RegisteredPatients", "InvoicePdf -- 03", conn);
            Services.DumException("InvoicePdf -- 03", "RegisteredPatients ", request, e3, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatient");
            Parser.SetField("ActionID", "CreateInvoiceInput");
            Parser.SetField("Message", "MES#018");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(e3.getMessage());
            String str = "";
            for (int i = 0; i < (e3.getStackTrace()).length; i++)
                str = str + e3.getStackTrace()[i] + "<br>";
            out.println(str);*/
        }
    }

    void CollectPayment(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuilder PatientInvoiceList = new StringBuilder();
        try {
            Query = "SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                    "DATE_FORMAT(b.DOB,'%m/%d/%Y')  " +
                    "FROM " + Database + ".PatientReg b   " +
                    "INNER JOIN " + Database + ".InvoiceMaster a ON a.PatientMRN = b.MRN  " +
                    "WHERE a.Paid!=1 AND  b.Status = 0 AND a.Status = 0 GROUP BY a.PatientMRN";
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
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (CollectPayment -- 01)", servletContext, ex, "RegisteredPatients", "CollectPayment -- 01", conn);
            Services.DumException("CollectPayment", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#019");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (CollectPayment)", servletContext, var11, "RegisteredPatients", "CollectPayment", conn);
            Services.DumException("RegisteredPatients", "CollectPayment", request, var11, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
        }
    }

    void CollectPayment_View(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, int UserType) throws FileNotFoundException {
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
        StringBuilder CDRList = new StringBuilder();
        StringBuilder PatientInvoiceList = new StringBuilder();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int SNo = 1;
        try {
            PatientInvoiceMRN = request.getParameter("PatientInvoice").trim();
/*            Query = "SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                    "DATE_FORMAT(b.DOB,'%m/%d/%Y')  " +
                    "FROM " + Database + ".InvoiceMaster a   " +
                    "LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN  " +
                    "WHERE b.Status = 0 AND a.Status = 0 GROUP BY a.PatientMRN";*/
            //Changed Query --> 22-OCT-2021
            //By Tabish --> No Visit information was displayed and query was wrongly mapped.
            Query = "SELECT a.MRN, CONCAT(IFNULL(a.Title,' '),' ',IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')) AS Name," +
                    "DATE_FORMAT(a.DOB,'%m/%d/%Y')\n" +
                    "FROM \n" +
                    "" + Database + ".PatientReg a \n" +
                    "INNER JOIN " + Database + ".InvoiceMaster  b ON a.MRN = b.PatientMRN AND b.Status = 0\n" +
                    "INNER JOIN " + Database + ".PatientVisit c ON a.Id = c.PatientRegId AND c.Id = b.VisitID\n" +
                    "Where b.Paid!=1 AND a.status=0 GROUP BY a.MRN ORDER BY a.MRN ";
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

/*            Query = " SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,'-'),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')),DATE_FORMAT(b.DOB,'%d-%m-%Y')," +
                    "IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')) AS DOS,  " +
                    "a.InvoiceNo, DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T'), a.TotalAmount, a.PaidAmount, a.BalAmount, a.Id,IFNULL(a.Discount,'0.00')   " +
                    " FROM " + Database + ".InvoiceMaster a   " +
                    " LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN    " +
                    " WHERE a.PatientMRN = '" + PatientInvoiceMRN + "' AND a.Status = 0 AND b.Status = 0";*/
            //Changed Query --> 22-OCT-2021
            //By Tabish --> No Visit information was displayed and query was wrongly mapped.
            Query = "SELECT a.MRN, CONCAT(IFNULL(a.Title,' '),' ',IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')) AS Name,DATE_FORMAT(a.DOB,'%d-%m-%Y') AS DOB,\n" +
                    "DATE_FORMAT(c.DateofService,'%m/%d/%Y %T') AS DOS, b.InvoiceNo,DATE_FORMAT(b.CreatedDate, '%m/%d/%Y %T') AS InvoiceGeneratedDate,\n" +
                    "b.TotalAmount, b.PaidAmount, b.BalAmount, b.Id AS InvoiceIdx ,b.Discount,c.Id AS VisitIdx \n" +
                    "FROM \n" +
                    " " + Database + ".PatientReg a \n" +
                    " INNER JOIN " + Database + ".InvoiceMaster  b ON a.MRN = b.PatientMRN AND b.Status = 0 \n" +
                    " INNER JOIN " + Database + ".PatientVisit c ON a.Id = c.PatientRegId AND c.Id = b.VisitID \n" +
                    " WHERE a.MRN = " + PatientInvoiceMRN + " AND b.Paid!=1 \n" +
                    "ORDER BY b.CreatedDate DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Query2 = "Select COUNT(*) FROM " + Database + ".InstallmentPlan WHERE MRN = '" + rset.getString(1) + "' AND " +
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
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left width=15%>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(TotalAmount) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(rset.getDouble(8)) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(rset.getDouble(9)) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(rset.getDouble(11)) + "</td>\n");
                CDRList.append("<td align=left width=30%> ");
                CDRList.append("<a class='btn-sm btn btn-primary' href=/md/md.RegisteredPatients?ActionID=InvoicePdf&PatientMRN=" + rset.getString(1) + "&InvoiceNo=" + rset.getString(5) + "&VisitID=" + rset.getInt(12) + ">View</a>\n");
                CDRList.append("<a class='btn-sm btn btn-success' href=/md/md.RegisteredPatients?ActionID=PayNow&InvoiceNo=" + rset.getString(5) + ">PayNow</a>\n");
                if (InstallmentPlanFound > 0) {
                    CDRList.append("<a class='btn-sm btn btn-warning' href=/md/md.InstallmentPlan?ActionID=GetInput&InvoiceNo=" + rset.getString(5) + ">Installments (Plan Applied) </a>\n");
                } else {
                    CDRList.append("<a class='btn-sm btn btn-warning' href=/md/md.InstallmentPlan?ActionID=GetInput&InvoiceNo=" + rset.getString(5) + ">Installments</a>\n");
                }
                if (UserType == 7 || UserType == 10) {
                    if (ClientId == 8)
                        CDRList.append("<br> <a class='btn-sm btn btn-info' onclick=\"SendRequest(\'/md/md.SelfServicePortal?Action=SendSignUpRequest&FacilityIndex=" + ClientId + "&MRN=" + PatientInvoiceMRN + "\')\">Send Link</a>\n");
                    CDRList.append("<button type=\"button\" class=\" btn-sm waves-effect waves-light btn  bg-gradient-success\" onclick=\"OpenModal('" + rset.getString(5) + "', '" + PatientInvoiceMRN + "','" + numFormat.format(TotalAmount) + "','" + numFormat.format(rset.getDouble(9)) + "','" + numFormat.format(rset.getDouble(8)) + "');\" >Add Discount</button>\n");
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
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("PatientInvoiceList", String.valueOf(PatientInvoiceList));
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.SetField("MRN", String.valueOf(PatientInvoiceMRN));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowInvoice.html");
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (CollectPayment_View -- 01)", servletContext, ex, "RegisteredPatients", "CollectPayment_View -- 01", conn);
            Services.DumException("CollectPayment_View", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.SetField("Message", "MES#020");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void PayNow(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, Payments payments) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
        String PatientMRN = "";
        String PatientName = "";
        double TotalAmount = 0.0D;
        double PaidAmount = 0.0D;
        double BalAmount = 0.0D;
        double AmountToPay = 0.0D;
        String DOS = "";
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

/*            int checkCredentials = payments.checkBoltCredentials(request, conn, ClientId, servletContext);
            if (checkCredentials == 0) {
                //out.println("11~Clover credentials are not listed! Please contact System Administrator.");
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("Message", "Clover credentials are not listed! Please contact System Administrator.");
                parsehtm.SetField("FormName", "RegisteredPatients");
                parsehtm.SetField("ActionID", "CollectPayment");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                return;
            }*/

            // System.out.println(" ********** SESSION IN PROGRESS ************* " + CS.sessionInProgress);
            // System.out.println("INITIAL SESSION IN PROGRESS IN PN FROM BoltMaster **** " + bm.sessionInProgress);


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
                parsehtm.SetField("FormName", "RegisteredPatients");
                parsehtm.SetField("ActionID", "CollectPayment");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                return;
            }
/*            StringBuilder checkAmountDisplay = new StringBuilder();
            double checkAmount = 0.0;
            int checkInfoCHK = 0;
            Query = "SELECT COUNT(*),Amount FROM " + Database + ".CheckInfo WHERE InvoiceNo =  '" + InvoiceNo + "' AND isPaid = 0 AND Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                checkInfoCHK = rset.getInt(1);
                checkAmount += rset.getDouble(2);
            }
            rset.close();
            stmt.close();

            if (checkInfoCHK > 0) {
                checkAmountDisplay.append("<tr>");
                checkAmountDisplay.append("<td>Check</td>");
                checkAmountDisplay.append("<td class=\"text-right font-weight-700\"><span class=\"text-warning mr-15\"><b>($" + checkAmount + ")</b></span>");
                checkAmountDisplay.append("</td>");
                checkAmountDisplay.append("</tr>");
                //checkAmountDisplay.append("<p><span class='text-warning'> <b>(" + checkAmount + ")</b></span></p>");
            }*/


            Query = "SELECT COUNT(*) FROM " + Database + ".InstallmentPlan " +
                    "WHERE MRN = '" + PatientMRN + "' AND InvoiceNo = '" + InvoiceNo + "' AND status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                InstallmentPlanFound = rset.getInt(1);
            rset.close();
            stmt.close();

            if (InstallmentPlanFound > 0) {
                installmentPlan.append("<a class='btn-sm btn btn-primary' data-toggle=\"modal\" data-target=\"#installmentModal\">View</a>");
                Query = "SELECT IFNULL(MRN,''), IFNULL(InvoiceNo,''), IFNULL(PaymentAmount,0), IFNULL(DATE_FORMAT(PaymentDate,'%m/%d/%Y'),''),  CASE WHEN PAID = 0 THEN 'Pending' WHEN Paid = 1 THEN 'Paid' ELSE 'Pending' END FROM " + Database + ".InstallmentPlan WHERE MRN = '" + PatientMRN + "' AND  InvoiceNo = '" + InvoiceNo + "' AND status = 0";
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


            DeviceList = payments.getDeviceList(request, conn, servletContext, ClientId);

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
            Parser.SetField("DOS", DOS);
            Parser.SetField("installmentPlan", installmentPlan.toString());
            //Parser.SetField("checkAmountDisplay", checkAmountDisplay.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/PayNow.html");
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

    void PayNowSave(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
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
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("Message", "Please select Cash Method!!");
                parsehtm.SetField("FormName", "RegisteredPatients");
                parsehtm.SetField("ActionID", "PayNow");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Error.html");
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
                helper.SendEmailWithAttachment("Error in RegisteredPatients ** (PaymentReceiptInfo -- 01)", servletContext, e, "RegisteredPatients", "PaymentReceiptInfo -- 01", conn);
                Services.DumException("PayNowSave -- Insertion PaymentReceiptInfo Table", "RegisteredPatients ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "CollectPayment");
                Parser.SetField("Message", "MES#022");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }
            if (InstallmentPlanFound > 0)
                try {
                    Query = "Select Id from " + Database + ".InstallmentPlan where Paid = 0 and InvoiceNo = '" + InvoiceNo + "' limit 1";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next())
                        InstallmentPlanId = rset.getInt(1);
                    rset.close();
                    stmt.close();
                    if (InstallmentPlanId > 0) {
                        Query = " Update " + Database + ".InstallmentPlan set Paid = 1 where Id = " + InstallmentPlanId + "";
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();
                    }
                } catch (Exception e) {
                    helper.SendEmailWithAttachment("Error in RegisteredPatients ** (PayNowSave -- 01)", servletContext, e, "RegisteredPatients", "PayNowSave -- 01", conn);
                    Services.DumException("PayNowSave -- Updating Installment plan Table", "RegisteredPatients ", request, e);
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "RegisteredPatients");
                    Parser.SetField("ActionID", "CollectPayment");
                    Parser.SetField("Message", "MES#023");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Message", "Payment Saved Successfully ");
            Parser.SetField("FormName", String.valueOf("RegisteredPatients"));
            Parser.SetField("ActionID", String.valueOf("CollectPayment"));
            Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/md/Exception/Success.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (PayNowSave -- 01)", servletContext, e, "RegisteredPatients", "PayNowSave -- 01", conn);
            Services.DumException("PayNowSave", "RegisteredPatients ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.SetField("Message", "MES#024");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(this.Query);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
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
            Query = " Update " + Database + ".PatientReg set Status = 1 where ID = '" + PatientID + "'";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
            out.println("1");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (DeActivePatient -- 01)", servletContext, e, "RegisteredPatients", "DeActivePatient -- 01", conn);
            Services.DumException("DeActivePatient ", "RegisteredPatients ", request, e, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.SetField("Message", "MES#025");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(Query);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void ReActivePatient(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
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
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (ReActivePatient -- 01)", servletContext, e, "RegisteredPatients", "DeActivePatient -- 01", conn);
            Services.DumException("ReActivePatient ", "RegisteredPatients ", request, e, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.SetField("Message", "MES#026");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(Query);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void DeActiveInvoice(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database, UtilityHelper helper) throws FileNotFoundException {
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
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Cannot perform transaction. Either invoice is partial paid or fully paid!");
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "CollectPayment");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Error.html");
//                out.println("<!DOCTYPE html><html><body><p style=\"color:gray;\">Invoice Cannot DeActivate Because it is Paid or Partially Paid</p></body></html>");
//                out.println("<input class=\"btn btn-primary\" type=button name=Back Value=\"  Back  \" onclick=history.back()>");
                return;
            }
            Query = " Update " + Database + ".InvoiceMaster set Status = 1 where Id = '" + InvoiceMasterID + "'";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Invoice DE-ACTIVATED Successfully!");
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Success.html");
//            out.println("<!DOCTYPE html><html><body><p style=\"color:gray;\">Invoice DE-ACTIVATED Successfully</p></body></html>");
//            out.println("<input class=\"btn btn-primary\" type=button name=Back Value=\"  Back  \" onclick=history.back()>");

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (DeActiveInvoice -- 01)", servletContext, e, "RegisteredPatients", "DeActiveInvoice -- 01", conn);
            Services.DumException("DeActiveInvoice ", "RegisteredPatients ", request, e, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.SetField("Message", "MES#027");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(Query);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void TransactionReport_Input(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
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
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (TransactionReport_Input -- 01)", servletContext, ex, "RegisteredPatients", "TransactionReport_Input -- 01", conn);
            Services.DumException("TransactionReport_Input", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#028");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(Query);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void TransactionReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
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
            Query = " SELECT a.PatientMRN, CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), a.InvoiceNo  FROM " + Database + ".InvoiceMaster a   LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN  WHERE b.Status = 0 GROUP BY a.PatientMRN";
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
            Query = " Select a.PatientMRN , CONCAT(b.Title,' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), a.InvoiceNo, a.TotalAmount, a.PaidAmount,  a.BalAmount, IFNULL(a.RefNo,'-'), IFNULL(a.Remarks,'-') , DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T') as PaymentDate, DATE_FORMAT(c.CreatedDate, '%m/%d/%Y %T') as InvoiceDate,  CASE WHEN PayMethod = 1 THEN 'Credit Card' WHEN PayMethod = 2 THEN 'Cash' WHEN PayMethod = 3 THEN 'BOLT Device' WHEN PayMethod = 4 THEN 'Ingenico' ELSE '' END, IFNULL(PayMethod,0)  FROM " + Database + ".PaymentReceiptInfo a LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN LEFT JOIN " + Database + ".InvoiceMaster c on a.InvoiceNo = c.InvoiceNo  WHERE a.PatientMRN = '" + MRN + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                TransactionList.append("<tr id=" + SNo + ">");
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
                TransactionList.append("<td align=left><input type=\"button\" onclick=\"printDiv('" + SNo + "')\" value=\"print\"></td>\n");
                TransactionList.append("</tr>");
                SNo++;
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
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (TransactionReport -- 01)", servletContext, ex, "RegisteredPatients", "TransactionReport -- 01", conn);
            Services.DumException("TransactionReport", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "TransactionReport_Input");
            Parser.SetField("Message", "MES#029");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(Query);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void AddDoctors(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int SNo = 1;
        StringBuffer DoctorsList = new StringBuffer();

        try {
            Query = "Select Id,CONCAT(DoctorsLastName, ' , ', DoctorsFirstName) ," +
                    "CASE WHEN Status = 1 THEN 'Active' ELSE 'InActive' END, IFNULL(NPI,'0') , IFNULL(TaxonomySpecialty,'') " +
                    "from " + Database + ".DoctorsList ORDER BY CreatedDate DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                DoctorsList.append("<tr>");
                DoctorsList.append("<td align=left>" + SNo + "</td>\n");
                DoctorsList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                DoctorsList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                DoctorsList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                DoctorsList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                DoctorsList.append("<td align=left><i  id=\"edit\" class=\"fa fa-edit\" onClick=\"editRow(" + rset.getInt(1) + ")\"></i></td>\n");
                DoctorsList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DoctorsList", String.valueOf(DoctorsList));
            Parser.SetField("UserId", String.valueOf(UserId));

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/AddDoctors.html");
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (AddDoctors -- 01)", servletContext, ex, "RegisteredPatients", "AddDoctors -- 01", conn);
            Services.DumException("AddDoctors", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#030");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(Query);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void SaveDoctor(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {

        String DoctorsFirstName = request.getParameter("DoctorsFirstName").trim();
        String DoctorsLastName = request.getParameter("DoctorsLastName").trim();
        String Status = request.getParameter("Status").trim();
        String NPI = request.getParameter("NPI").trim();
        String Taxonomy = request.getParameter("Taxonomy").trim();
        UserId = request.getParameter("UserId").trim();

        try {
            PreparedStatement MainReceipt = conn.prepareStatement(
                    "INSERT INTO " + Database + ".DoctorsList (DoctorsFirstName,DoctorsLastName," +
                            "Status,NPI,CreatedBy,CreatedDate,TaxonomySpecialty) VALUES (?,?,?,?,?,now(),?) ");
            MainReceipt.setString(1, DoctorsFirstName);
            MainReceipt.setString(2, DoctorsLastName);
            MainReceipt.setString(3, Status);
            MainReceipt.setString(4, NPI);
            MainReceipt.setString(5, UserId);
            MainReceipt.setString(6, Taxonomy);
            MainReceipt.executeUpdate();
            MainReceipt.close();
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (AddDoctorsSave -- 01)", servletContext, ex, "RegisteredPatients", "AddDoctorsSave -- 01", conn);
            Services.DumException("AddDoctorsSave -- 01", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "AddDoctors");
            Parser.SetField("Message", "MES#031");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }

        AddDoctors(request, out, conn, servletContext, UserId, Database, ClientId, helper);

    }

    void UpdateDoctor(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int SNo = 1;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer DoctorsList = new StringBuffer();

        int Id = Integer.parseInt(request.getParameter("Id").trim());
        String DoctorsFirstName = request.getParameter("DoctorsFirstName").trim();
        String DoctorsLastName = request.getParameter("DoctorsLastName").trim();
        String Status = request.getParameter("Status").trim();
        String NPI = request.getParameter("NPI").trim();
        UserId = request.getParameter("UserId").trim();
        String Taxonomy = request.getParameter("Taxonomy").trim();


        try {
            PreparedStatement MainReceipt = conn.prepareStatement(
                    " Update  " + Database + ".DoctorsList set DoctorsFirstName = ?, " +
                            "DoctorsLastName = ?, Status = ?, NPI = ?,UpdatedBy = ?, " +
                            "UpdatedAt= now(), TaxonomySpecialty= ? where Id = " + Id + " ");
            MainReceipt.setString(1, DoctorsFirstName);
            MainReceipt.setString(2, DoctorsLastName);
            MainReceipt.setString(3, Status);
            MainReceipt.setString(4, NPI);
            MainReceipt.setString(5, UserId);
            MainReceipt.setString(6, Taxonomy);

            MainReceipt.executeUpdate();
            MainReceipt.close();

        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (AddDoctorsSave -- 02)", servletContext, ex, "RegisteredPatients", "AddDoctorsSave -- 02", conn);
            Services.DumException("AddDoctorsSave -- 02", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "AddDoctors");
            Parser.SetField("Message", "MES#032");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
        AddDoctors(request, out, conn, servletContext, UserId, Database, ClientId, helper);
    }

    void EditDoctor(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int Id = Integer.parseInt(request.getParameter("Id").trim());
        String DoctorsFirstName = "";
        String DoctorsLastName = "";
        String NPI = "";
        String Taxonomy = "";
        int Status = 0;
        try {
            Query = "Select DoctorsFirstName, DoctorsLastName, Status, IFNULL(NPI,'no entry'),IFNULL(TaxonomySpecialty,'') from " + Database + ".DoctorsList where Id = " + Id;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DoctorsFirstName = rset.getString(1);
                DoctorsLastName = rset.getString(2);
                Status = rset.getInt(3);
                NPI = rset.getString(4);
                Taxonomy = rset.getString(5);
            }
            rset.close();
            stmt.close();
            out.println(DoctorsFirstName + "|" + DoctorsLastName + "|" + String.valueOf(Status) + "|" + NPI + "|" + String.valueOf(Id) + "|" + Taxonomy);
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (EditDoctor -- 01)", servletContext, ex, "RegisteredPatients", "EditDoctor -- 01", conn);
            Services.DumException("EditDoctor -- 01", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "AddDoctors");
            Parser.SetField("Message", "MES#034");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(Query);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void AddAlergyInfo(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
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
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (AddAllergyInfo -- 01)", servletContext, ex, "RegisteredPatients", "AddAllergyInfo -- 01", conn);
            Services.DumException("AddAllergyInfo", "RegisteredPatients ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#035");
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

    void SaveAllergy(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = Integer.parseInt(request.getParameter("PatientId").trim());
        String AllergyInfo = request.getParameter("AllergyInfo").trim();
        String OtherInfo = request.getParameter("OtherInfo").trim();
        try {
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".Patient_AllergyInfo (PatientRegId, AlergyInfo,OtherInfo," +
                                "Status, CreatedDate) VALUES (?,?,?,?,now()) ");
                MainReceipt.setInt(1, PatientRegId);
                MainReceipt.setString(2, AllergyInfo);
                MainReceipt.setString(3, OtherInfo);
                MainReceipt.setInt(4, 0);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in RegisteredPatients ** (AddAllergySave -- 01)", servletContext, ex, "RegisteredPatients", "AddAllergySave -- 01", conn);
                Services.DumException("AddAlergySave", "RegisteredPatients ", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "AddAlergyInfo");
                Parser.SetField("Message", "MES#036");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Message_fileUpload", "Data Successfully Entered ");
            Parser.SetField("FormName", String.valueOf("RegisteredPatients"));
            Parser.SetField("ActionID", String.valueOf("AddAllergyInfo"));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Success.html");
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (AddAllergySave -- 02)", servletContext, ex, "RegisteredPatients", "AddAllergySave -- 02", conn);
            Services.DumException("AddAlergySave", "RegisteredPatients ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "AddAlergyInfo");
            Parser.SetField("Message", "MES#037");
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

    private void ShowHistory(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) throws FileNotFoundException {
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
            } catch (Exception ex) {
                Services.DumException("ShowHistory", "RegisteredPatients ", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "ShowHistory");
                Parser.SetField("Message", "MES#038");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
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
                HeaderFooter header = new HeaderFooter(p, false);
                header.setBorder(0);
                header.setAlignment(1);
                document.setHeader(header);
                document.open();
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));
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
                datatable1.addCell(new Paragraph(" ", normfont2));
                datatable1.addCell(new Paragraph("MRN: ", normfont2));
                datatable1.addCell(new Paragraph(MRN, UnderLine));
                datatable1.addCell(new Paragraph("", normfont2));
                datatable1.setDefaultCellBorder(13);
                datatable1.addCell(new Paragraph(PatientName, normfont));
                datatable1.setDefaultCellBorder(0);
                datatable1.addCell(new Paragraph("", normfont2));
                datatable1.setDefaultColspan(1);
                datatable1.setDefaultHorizontalAlignment(0);
                datatable1.addCell(new Paragraph("", normfont2));
                datatable1.addCell(new Paragraph("", normfont2));
                datatable1.addCell(new Paragraph("", UnderLine));
                datatable1.addCell(new Paragraph("", normfont2));
                datatable1.setDefaultCellBorder(12);
                datatable1.addCell(new Paragraph(ClientName + "         Sex:" + Sex, normfont));
                datatable1.setDefaultCellBorder(0);
                datatable1.addCell(new Paragraph("", normfont2));
                datatable1.setDefaultColspan(1);
                datatable1.setDefaultHorizontalAlignment(0);
                datatable1.addCell(new Paragraph(" ", normfont2));
                datatable1.addCell(new Paragraph("", normfont2));
                datatable1.addCell(new Paragraph("", UnderLine));
                datatable1.addCell(new Paragraph("", normfont2));
                datatable1.setDefaultCellBorder(12);
                datatable1.addCell(new Paragraph("DOB: " + DOB + "         Age: " + Age, normfont));
                datatable1.setDefaultCellBorder(0);
                datatable1.addCell(new Paragraph("", normfont2));
                datatable1.setDefaultColspan(1);
                datatable1.setDefaultHorizontalAlignment(0);
                datatable1.addCell(new Paragraph(" ", normfont2));
                datatable1.addCell(new Paragraph("", normfont2));
                datatable1.addCell(new Paragraph("", UnderLine));
                datatable1.addCell(new Paragraph("", normfont2));
                datatable1.setDefaultCellBorder(14);
                datatable1.addCell(new Paragraph("MRN: " + MRN + "         DOS: " + DOS, normfont));
                datatable1.setDefaultCellBorder(0);
                datatable1.addCell(new Paragraph("", normfont2));
                document.add(datatable1);
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
                datatable2.addCell(new Paragraph(" ", normfont3));
                datatable2.addCell(new Paragraph("Name: ", normfont3));
                datatable2.addCell(new Paragraph("", normfont3));
                datatable2.addCell(new Paragraph(PatientName, normfont3));
                datatable2.addCell(new Paragraph("", normfont3));
                datatable2.setDefaultColspan(1);
                datatable2.setDefaultHorizontalAlignment(0);
                datatable2.addCell(new Paragraph(" ", normfont3));
                datatable2.addCell(new Paragraph("Age: ", normfont3));
                datatable2.addCell(new Paragraph("", normfont3));
                datatable2.addCell(new Paragraph(Age, normfont3));
                datatable2.addCell(new Paragraph("", normfont3));
                datatable2.setDefaultColspan(1);
                datatable2.setDefaultHorizontalAlignment(0);
                datatable2.addCell(new Paragraph(" ", normfont3));
                datatable2.addCell(new Paragraph("Date of Birth: ", normfont3));
                datatable2.addCell(new Paragraph("", normfont3));
                datatable2.addCell(new Paragraph(DOB, normfont3));
                datatable2.addCell(new Paragraph("", normfont3));
                datatable2.setDefaultColspan(1);
                datatable2.setDefaultHorizontalAlignment(0);
                datatable2.addCell(new Paragraph(" ", normfont3));
                datatable2.addCell(new Paragraph("Sex: ", normfont3));
                datatable2.addCell(new Paragraph("", normfont3));
                datatable2.addCell(new Paragraph(Sex, normfont3));
                datatable2.addCell(new Paragraph("", normfont3));
                datatable2.setDefaultColspan(1);
                datatable2.setDefaultHorizontalAlignment(0);
                datatable2.addCell(new Paragraph("", normfont3));
                datatable2.addCell(new Paragraph("", normfont3));
                datatable2.addCell(new Paragraph("", normfont3));
                datatable2.addCell(new Paragraph("", normfont3));
                datatable2.addCell(new Paragraph("", normfont3));
                document.add(datatable2);
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
                datatable3.addCell(new Paragraph(" ", MainHeading));
                datatable3.setDefaultCellBorder(15);
                datatable3.addCell(new Paragraph("SNo.", MainHeading));
                datatable3.setDefaultCellBorder(11);
                datatable3.addCell(new Paragraph(" Allergy Info", MainHeading));
                datatable3.setDefaultCellBorder(11);
                datatable3.addCell(new Paragraph("Other Info", MainHeading));
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell(new Paragraph("", MainHeading));
                try {
                    Query = "Select AlergyInfo, OtherInfo from " + Database + ".Patient_AllergyInfo where PatientRegId = " + PatientID;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        Sno++;
                        datatable3.setDefaultColspan(1);
                        datatable3.setDefaultHorizontalAlignment(0);
                        datatable3.setDefaultCellBorder(0);
                        datatable3.addCell(new Paragraph(" ", normfont3));
                        datatable3.setDefaultCellBorder(0);
                        datatable3.addCell(new Paragraph(String.valueOf(Sno), normfont3));
                        datatable3.setDefaultCellBorder(0);
                        datatable3.addCell(new Paragraph(rset.getString(1), normfont3));
                        datatable3.setDefaultCellBorder(0);
                        datatable3.addCell(new Paragraph(rset.getString(2), normfont3));
                        datatable3.setDefaultCellBorder(0);
                        datatable3.addCell(new Paragraph("", normfont3));
                    }
                    rset.close();
                    stmt.close();
                } catch (Exception ex) {
                    Services.DumException("ShowHistory-MES#039", "RegisteredPatients ", request, ex);
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "RegisteredPatients");
                    Parser.SetField("ActionID", "ShowHistory");
                    Parser.SetField("Message", "MES#039");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                }
                datatable3.setDefaultColspan(1);
                datatable3.setDefaultHorizontalAlignment(0);
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell(new Paragraph(" ", normfont3));
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell(new Paragraph(" ", normfont3));
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell(new Paragraph(" ", normfont3));
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell(new Paragraph(" ", normfont3));
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell(new Paragraph(" ", normfont3));
                document.add(datatable3);
                document.close();
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Progma", "no-cache");
                response.setContentType("application/pdf");
                response.setContentLength(baos.size());
                ServletOutputStream out2 = response.getOutputStream();
                baos.writeTo(out2);
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
        } catch (Exception ex) {
            Services.DumException("ShowHistory-MES#040", "RegisteredPatients ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "ShowHistory");
            Parser.SetField("Message", "MES#040");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(e3.getMessage());
            String str = "";
            for (int i = 0; i < (e3.getStackTrace()).length; i++)
                str = str + e3.getStackTrace()[i] + "<br>";
            out.println(str);*/
        }
    }

    void CashReceipt(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database, int ClientId, UtilityHelper helper, HttpServletResponse response) throws FileNotFoundException {
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
            if (rset.next())
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
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (CashReceipt -- 01)", servletContext, ex, "RegisteredPatients", "CashReceipt -- 01", conn);
            Services.DumException("CashReceipt-MES#041", "RegisteredPatients ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CashReceipt");
            Parser.SetField("Message", "MES#041");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void GetPatients(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) throws FileNotFoundException {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Patient = request.getParameter("Patient").trim();
            StringBuilder PatientList = new StringBuilder();
            Query = " Select Id, MRN, FirstName, MiddleInitial, LastName, DOB, PhNumber  " +
                    "from " + Database + ".PatientReg " +
                    "where status = 0 and CONCAT(FirstName,LastName,PhNumber,MiddleInitial,MRN) like '%" + Patient + "%'  ";
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
            Services.DumException("GetPatients-MES#042", "RegisteredPatients ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "GetPatients");
            Parser.SetField("Message", "MES#042");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void GetPatientsUploadDocs(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) throws FileNotFoundException {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Patient = request.getParameter("Patient").trim();
            StringBuilder PatientList = new StringBuilder();

            Query = " Select Id, IFNULL(MRN,''), IFNULL(FirstName,''), IFNULL(MiddleInitial,''), IFNULL(LastName,''), IFNULL(DOB,''), " +
                    "IFNULL(PhNumber,''),  CONCAT(ID,',',IFNULL(MRN,''),',',IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,''))  " +
                    "from " + Database + ".PatientReg  where status = 0 and CONCAT(FirstName,LastName,PhNumber,MiddleInitial,MRN) like '%" + Patient + "%'  ";
            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\" onchange=\"GetDOS(this.value);\">");
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
            Services.DumException("CashReceipt-MES#043", "RegisteredPatients ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "GetPatients");
            Parser.SetField("Message", "MES#043");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void GetPatientsEligibility(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) throws FileNotFoundException {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Patient = request.getParameter("Patient").trim();
            StringBuilder PatientList = new StringBuilder();

            //Changing the query 9-April-2021
            //Reason -- Like keyword is not working properly. So, i have added concat
/*            Query = "Select Id, MRN, FirstName, MiddleInitial, LastName, DOB, PhNumber  " +
                    "FROM " + Database + ".PatientReg " +
                    "where status = 0 and FirstName like '%" + Patient + "%' OR LastName like '%" + Patient + "%'  OR MRN like '%" + Patient + "%' OR PhNumber like '%" + Patient + "%'  OR MiddleInitial like '%" + Patient + "%'";*/
/*            Query = "Select Id, MRN, FirstName, MiddleInitial, LastName, DOB, PhNumber,`Status`\n" +
                    "FROM "+Database+".PatientReg \n" +
                    "WHERE status = 0 and CONCAT(FirstName,LastName,PhNumber,MiddleInitial,MRN) like '%" + Patient + "%'  ";*/
            Query = "Select Id, MRN, FirstName, MiddleInitial, LastName, DOB, PhNumber,`Status`\n" +
                    "FROM " + Database + ".PatientReg \n" +
                    "WHERE status = 0 and CONCAT(MRN,FirstName,LastName,PhNumber) like '%" + Patient + "%'  ";
            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\" onchange=\"GetDetails(this.value);\">");
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
            Services.DumException("GetPatientsEligibility-MES#044", "RegisteredPatients ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "GetPatientsEligibility");
            Parser.SetField("Message", "MES#044");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
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

    void GetProfessionalPayersList(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) throws FileNotFoundException {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String ProfessionalPayer = request.getParameter("ProfessionalPayer").trim();
            StringBuffer ProfessionalPayersList = new StringBuffer();
            Query = "Select PayerId, PayerName from " + Database + ".ProfessionalPayers " +
                    "where PayerName like '%" + ProfessionalPayer + "%' order by status";
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
            Services.DumException("GetPatientsEligibility-MES#045", "RegisteredPatients ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "GetPatientsEligibility");
            Parser.SetField("Message", "MES#045");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void GetDateOfService(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) throws FileNotFoundException {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Patient = request.getParameter("Patient").trim();
            StringBuffer DOS = new StringBuffer();
            DOS.append("<option style=\"color:white\" value='' selected disabled> Please Select visit </option>");
            Query = "SELECT Id, ReasonVisit,IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')) " +
                    "FROM " + Database + ".PatientVisit WHERE MRN = " + Patient;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                DOS.append("<option style=\"color:white\" value=" + rset.getInt(1) + "> " + rset.getString(2) + " | " + rset.getString(3) + " </option>");
            }
            stmt.close();
            rset.close();
            out.println(DOS);
        } catch (Exception ex) {
            Services.DumException("GetDateOfService-MES#045", "RegisteredPatients ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "GetDateOfService");
            Parser.SetField("Message", "MES#045");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
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
                Query = " SELECT CONCAT(a.Title,' ',a.FirstName,' ',a.MiddleInitial,' ',a.LastName), DATE_FORMAT(a.DOB,'%m/%d/%Y')," +
                        " a.PhNumber, IFNULL(a.MRN,0), IFNULL(a.ReasonVisit,'-'),  a.ID, IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T')),  " +
                        "CASE WHEN a.COVIDStatus = 1 THEN 'POSITIVE' WHEN a.COVIDStatus = 0 THEN 'NEGATIVE'  WHEN a.COVIDStatus = -1 THEN 'SUSPECTED' ELSE 'NONE' END," +
                        "  a.SelfPayChk,IFNULL(a.Email,'-'), IFNULL(a.Address,'-'), IFNULL(c.PayerName,'-'),IFNULL(a.City,'-'), IFNULL(a.State, '-'), " +
                        "IFNULL(a.ZipCode, '-')  " +
                        "FROM " + Database + ".PatientReg a  " +
                        "LEFT JOIN " + Database + ".InsuranceInfo b on a.ID = b.PatientRegId   " +
                        "LEFT JOIN " + Database + ".ProfessionalPayers c on b.PriInsuranceName = c.Id  " +
                        "where a.Status = 0 and a.CreatedDate >= '" + FromDate + " 00:00:00' and a.CreatedDate <= '" + ToDate + " 23:59:59' " +
                        "ORDER BY ID DESC; ";
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

/*
    private static boolean ReadPdfGetData(String FileName, String Path) {
        try {
            DOS = "";
            Acct = "";
            printabledate = "";
            try (PDDocument document = PDDocument.load(new File(Path + "/" + FileName))) {
                document.getClass();
                if (!document.isEncrypted()) {
                    PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                    stripper.setSortByPosition(true);
                    PDFTextStripper tStripper = new PDFTextStripper();
                    tStripper.getStartPage();
                    String pdfFileInText = tStripper.getText(document);
                    int ii = 0;
                    int iChComplaint = 0;
                    String[] lines = pdfFileInText.split("\\r?\\n");
                    for (String line : lines) {
                        if (line.startsWith("Acct #:")) {
                            String AcctRaw = line;
                            String[] AcctArr = AcctRaw.split("\\s+");
                            Acct = AcctArr[2];
                            String printabledateRaw = AcctArr[4];
                            String[] printabledateArr = printabledateRaw.split("\\/");
                            printabledate = printabledateArr[2] + "-" + printabledateArr[0] + "-" + printabledateArr[1] + " " + AcctArr[5] + ":00";
                        }
                        if (line.startsWith("DOS:")) {
                            String DOSRaw = line;
                            String[] DOSArr = DOSRaw.split("\\s+");
                            DOS = DOSArr[1];
                            String[] DOSFormatArr = DOS.split("\\/");
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
*/

/*
    public void handleRequestNEW(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        response.setContentType("text/html");
        Services supp = new Services();
        UtilityHelper helper = new UtilityHelper();
        Payments payments = new Payments();
        try {
            HttpSession session = request.getSession(false);
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            String UserId = session.getAttribute("UserId").toString();
            String DatabaseName = session.getAttribute("DatabaseName").toString();
            int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            int UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());
            int UserType = Integer.parseInt(session.getAttribute("UserType").toString());

            if (UserId.equals("") || UserId.isEmpty()) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            String ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);

            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.equals("GetValues")) {
                GetValues(request, out, conn, context);
            } else if (ActionID.equals("PatientsDocUpload")) {

                if (!helper.AuthorizeScreen(request, out, conn, context, UserIndex, Upload_Documents_index)) {
//                out.println("You are not Authorized to access this page");
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Message", "You are not Authorized to access this page");
                    Parser.SetField("FormName", "ManagementDashboard");
                    Parser.SetField("ActionID", "GetInput");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                    return;
                }

                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs Upload", "Click on Upload Docs Option", FacilityIndex);
                PatientsDocUpload(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("ShowReport")) {

                if (!helper.AuthorizeScreen(request, out, conn, context, UserIndex, View_Patient_index)) {
//                out.println("You are not Authorized to access this page");
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Message", "You are not Authorized to access this page");
                    Parser.SetField("FormName", "ManagementDashboard");
                    Parser.SetField("ActionID", "GetInput");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                    return;
                }

                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View Patient Option", "Click on View Patient Option Patients List", FacilityIndex);
                ShowReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("PatientsDocUpload_Save") && request.getContentType().startsWith("multipart/form-data")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs Upload Save", "Save the Documents for the selected patients", FacilityIndex);
                PatientsDocUpload_Save(request, out, conn, context, response, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("ViewDocuments_Input")) {

                if (!helper.AuthorizeScreen(request, out, conn, context, UserIndex, ViewDocuments_index)) {
//                out.println("You are not Authorized to access this page");
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Message", "You are not Authorized to access this page");
                    Parser.SetField("FormName", "ManagementDashboard");
                    Parser.SetField("ActionID", "GetInput");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                    return;
                }

                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs View", "Click on View Documents Options", FacilityIndex);
                ViewDocuments_Input(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("ViewDocuments")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs View", "Docmuents Uploaded List", FacilityIndex);
                ViewDocuments(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("CreateInvoiceInput")) {

                if (!helper.AuthorizeScreen(request, out, conn, context, UserIndex, CreateInvoiceInput_index)) {
//                out.println("You are not Authorized to access this page");
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Message", "You are not Authorized to access this page");
                    Parser.SetField("FormName", "ManagementDashboard");
                    Parser.SetField("ActionID", "GetInput");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                    return;
                }

                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Create Invoice Option", "Invoice Craetion Add Diseases", FacilityIndex);
                CreateInvoiceInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("GetCost")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Cost", "Get the cost of the Diseases and Fill input fields", FacilityIndex);
                GetCost(request, out, conn, context, DatabaseName, helper);
            } else if (ActionID.equals("SaveInvoice")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Invoice ", "Save the Created Invoice Data in Tables and Details", FacilityIndex);
                SaveInvoice(request, out, conn, context, UserId, DatabaseName, helper);
            } else if (ActionID.equals("InvoicePdf")) {
                InvoicePdf(request, response, out, conn, UserId, DatabaseName, FacilityIndex, context, helper);
            } else if (ActionID.equals("CollectPayment")) {

                if (!helper.AuthorizeScreen(request, out, conn, context, UserIndex, CollectPayment_index)) {
//                out.println("You are not Authorized to access this page");
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Message", "You are not Authorized to access this page");
                    Parser.SetField("FormName", "ManagementDashboard");
                    Parser.SetField("ActionID", "GetInput");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                    return;
                }

                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Payement Collection Option", "Search the Invoice and Then Collect Payment Option", FacilityIndex);
                CollectPayment(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("CollectPayment_View")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Payement Collection Option", "View the List of Created Invoice Of selected Patient", FacilityIndex);
                CollectPayment_View(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, UserType);
            } else if (ActionID.equals("PayNow")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Pay Now Option", "Pay Now Option for the Created Invoice for Selected Pateint", FacilityIndex);
                PayNow(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, payments);
            } else if (ActionID.equals("PayNowSave")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Pay Now Option", "Save the Details Of Paid Amount for the Invoice Created for the Selected Patient", FacilityIndex);
                PayNowSave(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("DeActivePatient")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "DeActivate Patients", "Deactivate the Selected Patients from the View Patient Option and Search Old Patient Option", FacilityIndex);
                DeActivePatient(request, response, out, conn, context, UserId, DatabaseName, helper);
            } else if (ActionID.equals("ReActivePatient")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "ReActivate Patients", "ReActivate the Selected Patients from the Search Old Patient Option", FacilityIndex);
                ReActivePatient(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("DeActiveInvoice")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "DeActivate Invoice Option", "Deactivate the selected Invoice from The Collect Payment Option Where you can Search Invoice for the Respentive Patient", FacilityIndex);
                DeActiveInvoice(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("TransactionReport_Input")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Transaction Report Option", "Transaction Report for the Selected Patient Input Option", FacilityIndex);
                TransactionReport_Input(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("TransactionReport")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Transaction Report Option", "View Transaction Report List", FacilityIndex);
                TransactionReport(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("AddDoctors")) {

                if (!helper.AuthorizeScreen(request, out, conn, context, UserIndex, AddDoctor_index)) {
//                out.println("You are not Authorized to access this page");
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Message", "You are not Authorized to access this page");
                    Parser.SetField("FormName", "ManagementDashboard");
                    Parser.SetField("ActionID", "GetInput");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                    return;
                }


                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Doctors Physicians", "Click on Add Doctor Physicians Option", FacilityIndex);
                AddDoctors(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("SaveDoctor")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Doctors Physicians", "Save Doctor or Physician", FacilityIndex);
                SaveDoctor(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("EditDoctor")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Edit Doctors Physicians", "Edit Doctor or Physician", FacilityIndex);
                EditDoctor(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("AddAlergyInfo")) {

                if (!helper.AuthorizeScreen(request, out, conn, context, UserIndex, AddAllergyInfo_index)) {
//                out.println("You are not Authorized to access this page");
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Message", "You are not Authorized to access this page");
                    Parser.SetField("FormName", "ManagementDashboard");
                    Parser.SetField("ActionID", "GetInput");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                    return;
                }

                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Allergy Information", "Add Allergy Options Input", FacilityIndex);
                AddAlergyInfo(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("SaveAllergy")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Save Allergy Information", "Save Allergy Options And Other Information", FacilityIndex);
                SaveAllergy(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("ShowHistory")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Show History for Allergy and Other Info", "Show Pdf for the Recent Allergy Information or any Other Info form View Patients Option or Search Old Patients Option", FacilityIndex);
                ShowHistory(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("CashReceipt")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "CashReceipt Download", "Show CashReceipt for Self Pay Patient Only", FacilityIndex);
                CashReceipt(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetPatients")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patients List", "Get Patients List matching from the Fields Input", FacilityIndex);
                GetPatients(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetPatientsEligibility")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patients List For Eligibility Inquiry", "Get Patients List matching from the Fields Input For Eligibility Inquiry", FacilityIndex);
                GetPatientsEligibility(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetPatientsUploadDocs")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patients List For Docs Upload", "Get Patients List matching from the Fields Input For Docs Upload", FacilityIndex);
                GetPatientsUploadDocs(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetProfessionalPayersList")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Professional Payer List For Eligibility Check", "Get Professional Payer LIst matching from the Fields Input For Eligbility Check", FacilityIndex);
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
                helper.deleteUserSession(request, conn, session.getId());
                session.invalidate();
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (handleRequest)", context, e, "RegisteredPatients", "handleRequest", conn);
            Services.DumException("RegisteredPatients", "Handle Request", request, e, getServletContext());
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
                helper.SendEmailWithAttachment("Error in RegisteredPatients ** (handleRequest -- SqlException)", context, e, "RegisteredPatients", "handleRequest", conn);
                Services.DumException("RegisteredPatients", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }
*/

    void ShowReportOLD(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
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
                    Query = " SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), " +
                            "DATE_FORMAT(DOB,'%m/%d/%Y'),  IFNULL(PhNumber,''), IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, " +
                            "IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'), DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')),  " +
                            "CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'NONE' ELSE 'NONE' END  " +
                            "FROM " + Database + ".PatientReg where Status = 0 ORDER BY CreatedDate DESC limit 500";
                } else {
                    Query = " SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), " +
                            "DATE_FORMAT(DOB,'%m/%d/%Y'),  IFNULL(PhNumber,''), IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, " +
                            "IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'), DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')),  " +
                            "CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'NONE' ELSE 'NONE' END " +
                            " FROM " + Database + ".PatientReg where Status = 0 and LENGTH(MRN) = 6 ORDER BY CreatedDate DESC limit 500";
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
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (ShowReport)", servletContext, e, "RegisteredPatients", "ShowReport", conn);
            Services.DumException("ShowReport", "RegisteredPatients ", request, e);
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

    void ViewDocumentsOLD(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        final String PatientName = "";
        final String PatientMRN = "";
        final SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        final StringBuffer CDRList = new StringBuffer();
        final StringBuffer PatientList = new StringBuffer();
        int SNo = 1;
        String DirectoryName = "";
        String DocumentPath = "";
        try {
            Query = "Select DirectoryName from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DirectoryName = rset.getString(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            Services.DumException("RegisteredPatient2", "PatientDocUplaodSave", request, e, servletContext);
        }
        try {
            final InetAddress ip = InetAddress.getLocalHost();
            final String hostname = ip.getHostName();
            PatientRegId = Integer.parseInt(request.getParameter("PatientId").trim());
            Query = "SELECT ID, CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), MRN FROM " + Database + ".PatientReg where Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ")</option>");
            }
            rset.close();
            stmt.close();
            Query = "SELECT a.PatientMRN, a.PatientName, a.UploadDocumentName, a.FileName, IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')), a.Id FROM " + Database + ".PatientDocUpload a  LEFT JOIN " + Database + ".PatientReg b ON a.PatientRegId = b.ID WHERE a.Status = 0 and a.PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                final File tmpFile = new File("/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "/" + rset.getString(4));
                final boolean exists = tmpFile.exists();
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
                if (hostname.trim().equals("romver-01")) {
                    CDRList.append("<td align=left><a href=https://rovermd.com:8443/md/md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(4) + "&path=" + DocumentPath + " target=\"_blank\">" + rset.getString(4) + "</a></td>\n");
                } else {
                    CDRList.append("<td align=left><a href=https://app.rovermd.com:8443/md/md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(4) + "&path=" + DocumentPath + " target=\"_blank\">" + rset.getString(4) + "</a></td>\n");
                }
                CDRList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteDocument(" + rset.getInt(6) + ")\"></i></td>\n");
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
        } catch (Exception ex) {
            Services.DumException("ViewDocuments", "RegisteredPatients ", request, ex);
            Parsehtm Parser2 = new Parsehtm(request);
            Parser2.SetField("FormName", "RegisteredPatients");
            Parser2.SetField("ActionID", "ViewDocuments_Input");
            Parser2.SetField("Message", "MES#007");
            Parser2.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void AddDoctorsOLD30112021(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
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
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (AddDoctors -- 01)", servletContext, ex, "RegisteredPatients", "AddDoctors -- 01", conn);
            Services.DumException("AddDoctors", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#030");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(Query);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void SaveDoctorOLD30112021(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
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
                } catch (Exception ex) {
                    helper.SendEmailWithAttachment("Error in RegisteredPatients ** (AddDoctorsSave -- 01)", servletContext, ex, "RegisteredPatients", "AddDoctorsSave -- 01", conn);
                    Services.DumException("AddDoctorsSave -- 01", "RegisteredPatients ", request, ex, servletContext);
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "RegisteredPatients");
                    Parser.SetField("ActionID", "AddDoctors");
                    Parser.SetField("Message", "MES#031");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                }
            } else {
                try {
                    Query = " Update " + Database + ".DoctorsList set DoctorsFirstName = '" + DoctorsFirstName + "', DoctorsLastName = '" + DoctorsLastName + "', Status = '" + Status + "'  where Id = " + Id;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                } catch (Exception ex) {
                    helper.SendEmailWithAttachment("Error in RegisteredPatients ** (AddDoctorsSave -- 02)", servletContext, ex, "RegisteredPatients", "AddDoctorsSave -- 02", conn);
                    Services.DumException("AddDoctorsSave -- 02", "RegisteredPatients ", request, ex, servletContext);
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "RegisteredPatients");
                    Parser.SetField("ActionID", "AddDoctors");
                    Parser.SetField("Message", "MES#032");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
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
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (AddDoctorsSave -- 03)", servletContext, ex, "RegisteredPatients", "AddDoctorsSave -- 03", conn);
            Services.DumException("AddDoctorsSave -- 03", "RegisteredPatients ", request, ex, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "AddDoctors");
            Parser.SetField("Message", "MES#033");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(Query);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

}
