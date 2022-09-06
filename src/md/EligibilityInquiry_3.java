//
// Decompiled by Procyon v0.5.36
//

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

@SuppressWarnings("Duplicates")
public class EligibilityInquiry_3 extends HttpServlet {
    private static void pushLogs(String Message, Exception exp) {
        try {
            Date dt = GetDate();
            NumberFormat nf = new DecimalFormat("#00");

            String eventTime = nf.format(dt.getYear() + 1900) + "_" + nf.format(dt.getMonth() + 1) + "_" + nf.format(dt.getDate()) + "_" + nf.format(dt.getHours()) + "_" + nf.format(dt.getMinutes()) + "_" + nf.format(dt.getSeconds());
            String FileName = "FinalTrizetto_" + eventTime + "_" + GetExceptionFileName();
            FileWriter fr = new FileWriter("/sftpdrive/opt/FinalTrizettoLogs/" + FileName, true);
            fr.write(": Event " + eventTime + " ****  Msg " + Message + " **** Exception: " + exp + " \r\n");
            fr.flush();
            fr.close();
        } catch (Exception e) {
            System.out.println("Unable to Generate Thread for Console Event " + e.getMessage());
        }
    }

    private static Date GetDate() {
        try {
            return new Date();
        } catch (Exception localException) {
        }
        return null;
    }

    private static String GetExceptionFileName() {
        int temp = 0;
        try {
            Date dt = GetDate();
            NumberFormat nf = new DecimalFormat("#00");

            return nf.format(dt.getYear() + 1900) + "_" + nf.format(dt.getMonth() + 1) + "_" + nf.format(dt.getDate()) + ".log";
        } catch (Exception e) {
            return "invalid filename " + e.getMessage();
        }
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
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();
        Connection conn = null;

        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();

        try {
            HttpSession session = request.getSession(false);
            UtilityHelper helper = new UtilityHelper();

            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());

            try {
/*                boolean ValidSession = FacilityLogin.checkSession(out, request, context, response);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    return;
                }*/
                if (UserId.equals("")) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            } catch (Exception excp) {
                conn = null;
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "EligibilityGetInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Insurance Eligibility Inquiry 2", "Input Fields Details", FacilityIndex);
                    EligibilityGetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "GetDetails":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Insurance Eligibility Inquiry 2 Patient Details", "Get Patient Details and Auto Fill the Input Fields", FacilityIndex);
                    GetDetails(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "GetResponse":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Insurance Eligibility Inquiry 2 Get Insurance Information", "Get Insurance Info, Response from finaltrizetto And eireponse Class", FacilityIndex);
                    GetResponse(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }
        } catch (Exception Ex) {
            Services.DumException("EligibilityInquiry2", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
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

    private void EligibilityGetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        ResultSet rset = null;
        Statement stmt = null;
        String Query = "";
        String DOS = "";
        String SubscriberID = "";
        String Sec_SubscriberID = "";
        String FirstName = "";
        String LastName = "";
        String DOBAvaility = "";
        String DOSAvaility = "";
        String DOB = "";
        String GroupNo = "";
        String Sec_GroupNo = "";
        String Gender = "";
        String Pri_gender = "";
        String Sec_gender = "";
        String NPI = "";
        String proname = "";
        String PatientMRN = "";
        int PatientRegId = 0;
        int InsuranceFound = 0;
        StringBuilder PatientList = new StringBuilder();
        StringBuilder ServiceType = new StringBuilder();
        StringBuilder PayProcedureList = new StringBuilder();
        StringBuilder CDRList = new StringBuilder();
        StringBuilder ProfessionalPayersList = new StringBuilder();
        StringBuilder ServiceDates = new StringBuilder();
        StringBuilder AvailityPayersList = new StringBuilder();
        StringBuilder TypeOfInsurance = new StringBuilder();
        String PatientId = request.getParameter("PatientId");
        ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
        String patName = "";
        int profPayerIdx = 0;
        int Sec_profPayerIdx = 0;
        String Sec_payerName = "";
        String PrimaryDOB = "";
        String PrimaryDOB_Avail = "";
        String SubscriberDOB = "";
        String SubscriberDOB_Avail = "";
        String PatientRelationtoPrimary = "";
        String PatientRelationshiptoSecondry = "";
        String SubscriberFirstName = "";
        String SubscriberLastName = "";
        String PriInsurerFirstName = "";
        String PriInsurerLastName = "";
        String payerName = "";
        String PayerId = "";
        String Sec_PayerId = "";
        String filter = "";
        String genericDOB = "";
        String genericDOS = "";
        try {

/*            Query = "Select MRN, CONCAT(Title, ' ' , FirstName, ' ', MiddleInitial, ' ', LastName) " +
                    "from " + Database + ".PatientReg where status = 0 and ID = " + PatientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            //PatientList.append("<option class=Inner value='0'>Please Select Patient</option>");
            if (rset.next()) {
                PatientMRN = rset.getString(1);
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
            }
            rset.close();
            stmt.close();*/

            Query = "Select NPI,proname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                NPI = rset.getString(1);
                proname = rset.getString(2);
            }
            rset.close();
            stmt.close();

            Query = " Select ID, IFNULL(FirstName,''), IFNULL(LastName,''), IFNULL(DATE_FORMAT(DOB, '%Y%m%d'),'')," +
                    " IFNULL(DATE_FORMAT(DateofService,'%d-%m-%Y %T'), DATE_FORMAT(CreatedDate,'%d-%m-%Y %T')), " +
                    " gender, IFNULL(DATE_FORMAT(DOB, '%Y-%m-%d'),''), " +
                    " IFNULL(DATE_FORMAT(DateofService,'%Y-%m-%d'),DATE_FORMAT(CreatedDate,'%Y-%m-%d'))," +
                    " MRN,  CONCAT(IFNULL(Title,''), ' ' , IFNULL(FirstName,''), ' ', IFNULL(MiddleInitial,''), ' ', IFNULL(LastName,''))" +
                    " FROM " + Database + ".PatientReg " +
                    " WHERE ID = '" + PatientId + "' AND status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientRegId = rset.getInt(1);
                FirstName = rset.getString(2);
                LastName = rset.getString(3);
                DOB = rset.getString(4);
                DOS = rset.getString(5);
                Gender = rset.getString(6).toUpperCase();
                DOBAvaility = rset.getString(7);
                DOSAvaility = rset.getString(8);
                PatientMRN = rset.getString(9);
                patName = rset.getString(10);
            }
            rset.close();
            stmt.close();

            Query = "SELECT Id, ReasonVisit,IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')),DATE_FORMAT(DateofService,'%d-%m-%Y %T') " +
                    "FROM " + Database + ".PatientVisit WHERE PatientRegId = " + PatientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ServiceDates.append("<option  value='" + rset.getString(4) + "'> " + rset.getString(2) + " | " + rset.getString(3) + " </option>");
            }
            stmt.close();
            rset.close();


            Query = "Select COUNT(*) from " + Database + ".InsuranceInfo " +
                    " where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                InsuranceFound = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (InsuranceFound > 0) {
                Query = "Select GrpNumber, MemId,PriInsuranceName,MemberID_2,GroupNumber_2,SecondryInsurance,IFNULL(DATE_FORMAT(PrimaryDOB, '%Y%m%d'),''),IFNULL(DATE_FORMAT(SubscriberDOB, '%Y%m%d'),'')," +
                        "PatientRelationtoPrimary,PatientRelationshiptoSecondry,SubscriberFirstName,SubscriberLastName,PriInsurerFirstName," +
                        "PriInsurerLastName,PriInsurergender,Subscribergender,IFNULL(DATE_FORMAT(PrimaryDOB, '%Y-%m-%d'),'') ,IFNULL(DATE_FORMAT(SubscriberDOB, '%Y-%m-%d'),'') " +
                        "from " + Database + ".InsuranceInfo " +
                        " where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    GroupNo = rset.getString(1);
                    SubscriberID = rset.getString(2);
                    profPayerIdx = rset.getInt(3);
                    Sec_SubscriberID = rset.getString(4);
                    Sec_GroupNo = rset.getString(5);
                    Sec_profPayerIdx = rset.getInt(6);
                    PrimaryDOB = rset.getString(7);
                    SubscriberDOB = rset.getString(8);
                    PatientRelationtoPrimary = rset.getString(9);
                    PatientRelationshiptoSecondry = rset.getString(10);
                    SubscriberFirstName = rset.getString(11);
                    SubscriberLastName = rset.getString(12);
                    PriInsurerFirstName = rset.getString(13);
                    PriInsurerLastName = rset.getString(14);
                    Pri_gender = rset.getString(15);
                    Sec_gender = rset.getString(16);
                    PrimaryDOB_Avail = rset.getString(17);
                    SubscriberDOB_Avail = rset.getString(18);
                }
                rset.close();
                stmt.close();

                Query = "SELECT PayerName,PayerId FROM oe_2.ProfessionalPayers WHERE id = " + profPayerIdx + "  ";
//                AND (payername like '%TX%' OR payername like '%Texas%')
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    payerName = rset.getString(1).trim();
                    PayerId = rset.getString(2).trim();
                    ProfessionalPayersList.append("<option class=\"Inner\" value=\"" + PayerId + "\" selected>Primary - " + payerName + "</option>");
                    TypeOfInsurance.append("<option class=\"Inner\" value=\"Primary\" selected>Primary</option>");
                }
                rset.close();
                stmt.close();

                Query = "SELECT PayerName,PayerId FROM oe_2.ProfessionalPayers WHERE id = " + Sec_profPayerIdx + "";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    Sec_payerName = rset.getString(1).trim();
                    Sec_PayerId = rset.getString(2).trim();
                    ProfessionalPayersList.append("<option class=\"Inner\" value=\"" + Sec_PayerId + "\" >Secondary - " + Sec_payerName + "</option>");
                    TypeOfInsurance.append("<option class=\"Inner\" value=\"Secondary\" >Secondary</option>");
                }
                rset.close();
                stmt.close();


                if (!PayerId.equals("")) {

                    filter += "'" + PayerId + "'";
                }
                if (!Sec_PayerId.equals("")) {
                    filter += ",'" + Sec_PayerId + "'";
                }


                Query = "SELECT PayerName,PayerId FROM oe_2.ProfessionalPayers WHERE PayerId not IN (" + filter + ") ";
                System.out.println("Query -> " + Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    Sec_payerName = rset.getString(1).trim();
                    Sec_PayerId = rset.getString(2).trim();
                    ProfessionalPayersList.append("<option class=\"Inner\" value=\"" + Sec_PayerId + "\" >" + Sec_payerName + "</option>");
                }
                rset.close();
                stmt.close();
            }

            Query = "Select IFNULL(PayerId,''), IFNULL(PayerName,'') " +
                    "from " + Database + ".AvailityPayerList where Status = 1 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option class=Inner value=''>Please Select Patient</option>");
            while (rset.next()) {
                AvailityPayersList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
            }
            rset.close();
            stmt.close();

            if (ClientId == 9 || ClientId == 28) {

                Query = "Select COUNT(*) from " + Database + ".Patient_HealthInsuranceInfo " +
                        " where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    InsuranceFound = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (InsuranceFound > 0) {
                    Query = "Select HISubscriberGroupNo,HISubscriberPolicyNo,HIPrimaryInsurance," +
                            " SHISubscriberGroupNo,SHISubscriberPolicyNo,SHISecondaryName " +
                            " from " + Database + ".Patient_HealthInsuranceInfo " +
                            " where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        GroupNo = rset.getString(1);
                        SubscriberID = rset.getString(2);
                        payerName = rset.getString(3).trim();
                        PayerId = rset.getString(3).trim();
                        Sec_GroupNo = rset.getString(4);
                        Sec_SubscriberID = rset.getString(5);
                        Sec_payerName = rset.getString(6);
                    }
                    rset.close();
                    stmt.close();

                    switch (payerName) {
                        case "Aetna":
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"38692\" selected> Aetna Better Health of Texas</option>");
                            break;
                        case "BCBS":
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"00021\" selected>Blue Cross and Blue Shield of Texas</option>");
                            break;
                        case "Cigna":
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"63092\" selected> Cigna HealthSpring of Texas</option>");
                            break;
                        case "Humana":
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"-1\" selected> Please Select From Below Payers</option>");
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"61102\" > Humana (Encounters)</option>");
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"61101\" > Humana - Employers Health </option>");
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"61115\" > Humana American ElderCare</option>");
                            break;
                        case "Medicare":
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"04412\" selected> Medicare of Texas</option>");
                            break;
                        case "Medicaid":
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"-1\" selected> Please Select From Below Payers</option>");
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"00024\" > Medicaid of Texas - Acute Care </option>");
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"LTC24\" > Medicaid of Texas - Long Term Care </option>");
                            break;
                        case "Tricare":
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"TREST\" selected> Tricare East </option>");
                            break;
                        case "UMR":
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"-1\" selected> Please Select From Below Payers</option>");
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"J2004\" > UMR Risk Management (WC) </option>");
                            ProfessionalPayersList.append("<option class=\"Inner\" value=\"79480\" > UMR/Midwest Securities </option>");
                            break;
                    }
                }
            }

            SubscriberID = SubscriberID.replace("-", "");
            //ltrim(rtrim(UPPER(CreatedBy))) = ltrim(rtrim(UPPER('" + UserId + "'))) DATE_FORMAT(DateofService,'%m/%d/%Y %T') AS DisplayedDOS
            Query = " Select IFNULL(PatientMRN,''), IFNULL(Name,''), IFNULL(DATE_FORMAT(DateofBirth,'%m/%d/%Y'),''), " +
                    " DATE_FORMAT(STR_TO_DATE(DateofService,'%d-%m-%Y %T'),'%m/%d/%Y %T'), " +
                    " IFNULL(PolicyStatus,''), IFNULL(strmsg,''), " +
                    " IFNULL(InsuranceNum,''), Id, CreatedBy , IFNULL(DATE_FORMAT(CreatedDate,'%m/%d/%Y %T'),'') " +
                    " from oe.EligibilityInquiry " +
                    " where " +
                    " PatientMRN = '" + PatientMRN + "' AND FacilityIndex=" + ClientId + " ORDER BY CreatedDate DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr><td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                if (rset.getString(6).equals("")) {
                    CDRList.append("<td align=left>" + "No Response Found " + "</td>\n");
                } else {
                    CDRList.append("<td align=left><a href=/md/md.EligibilityInquiryReport?ActionID=GetResponse&Id=" + rset.getString(8) + " target=\"_blank\">" + "Eligibility Response</a></td>\n");
                }
                CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                CDRList.append("</tr>");
            }
            rset.close();
            stmt.close();

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("ServiceType", String.valueOf(ServiceType));
            Parser.SetField("PayProcedureList", String.valueOf(PayProcedureList));
            Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
            Parser.SetField("AvailityPayersList", String.valueOf(AvailityPayersList));
            Parser.SetField("TypeOfInsurance", String.valueOf(TypeOfInsurance));
            Parser.SetField("ClientIndex", String.valueOf(ClientId));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("DOS", String.valueOf(DOS));
            Parser.SetField("DOSAvaility", String.valueOf(DOSAvaility));
            Parser.SetField("SubscriberID", String.valueOf(SubscriberID));
            Parser.SetField("Sec_SubscriberID", String.valueOf(Sec_SubscriberID));

            if (PatientRelationtoPrimary.equals("Self")) {
                Parser.SetField("FirstName", String.valueOf(FirstName));
                Parser.SetField("LastName", String.valueOf(LastName));
                Parser.SetField("DOB", String.valueOf(DOB));
                Parser.SetField("DOBAvaility", String.valueOf(DOBAvaility));
                Parser.SetField("Gender", String.valueOf(Gender));
            } else if (!PatientRelationtoPrimary.equals("")) {
                Parser.SetField("FirstName", String.valueOf(PriInsurerFirstName));
                Parser.SetField("LastName", String.valueOf(PriInsurerLastName));
                Parser.SetField("DOB", String.valueOf(PrimaryDOB));
                Parser.SetField("DOBAvaility", String.valueOf(PrimaryDOB_Avail));
                Parser.SetField("Gender", String.valueOf(Pri_gender));
            }


            if (!PatientRelationshiptoSecondry.equals("Self") && !PatientRelationtoPrimary.equals("")) {
                Parser.SetField("SecFirstName", String.valueOf(SubscriberFirstName));
                Parser.SetField("SecLastName", String.valueOf(SubscriberLastName));
                Parser.SetField("SecDOB", String.valueOf(SubscriberDOB));
                Parser.SetField("SecDOB_Avail", String.valueOf(SubscriberDOB_Avail));
                Parser.SetField("Sec_gender", String.valueOf(Sec_gender));
            }


            Parser.SetField("GroupNo", String.valueOf(GroupNo));
            Parser.SetField("Sec_GroupNo", String.valueOf(Sec_GroupNo));
            Parser.SetField("NPI", String.valueOf(NPI));
            Parser.SetField("proname", String.valueOf(proname));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("PatientId", String.valueOf(PatientId));
            Parser.SetField("PatientRegId", String.valueOf(PatientId));
            Parser.SetField("profPayerIdx", String.valueOf(profPayerIdx));
            Parser.SetField("patName", patName);
            Parser.SetField("payerName", payerName);
            Parser.SetField("PayerId", PayerId);
            Parser.SetField("ServiceDates", ServiceDates.toString());
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(servletContext)) + "Forms/EligibilityGetInput2_Copy.html");
        } catch (Exception ex) {
            pushLogs("Error in EligibilityGetInput in main : ", ex);
            helper.SendEmailWithAttachment("Error in EligibilityInquiry_3 ** (EligibilityGetInput )", servletContext, ex, "EligibilityInquiry_3", "EligibilityGetInput", conn);
            Services.DumException("EligibilityInquiry_3", "Eligibility Get Input", request, ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "PatientUpdateInfo");
            Parser.SetField("ActionID", "GetInput&ID=" + PatientId);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

        }
    }

    private void GetDetails(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) throws FileNotFoundException {
        ResultSet rset = null;
        Statement stmt = null;
        String Query = "";
        final String PatientId = request.getParameter("PatientId").trim();
        String DOS = "";
        String SubscriberID = "";
        String FirstName = "";
        String LastName = "";
        String DOB = "";
        String GroupNo = "";
        String Gender = "";
        String NPI = "";
        String proname = "";
        int PatientRegId = 0;
        int InsuranceFound = 0;
        try {
            Query = "Select NPI,proname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                NPI = rset.getString(1);
                proname = rset.getString(2);
            }
            rset.close();
            stmt.close();
            Query = " Select ID, FirstName, LastName, DATE_FORMAT(DOB, '%Y%m%d'),IFNULL(DATE_FORMAT(DateofService,'%d-%m-%Y %T'),DATE_FORMAT(CreatedDate,'%d-%m-%Y %T')), gender from " + Database + ".PatientReg " + " where MRN = '" + PatientId + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientRegId = rset.getInt(1);
                FirstName = rset.getString(2);
                LastName = rset.getString(3);
                DOB = rset.getString(4);
                DOS = rset.getString(5);
                Gender = rset.getString(6).toUpperCase();
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".InsuranceInfo where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                InsuranceFound = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (InsuranceFound > 0) {
                Query = "Select GrpNumber, MemId from " + Database + ".InsuranceInfo where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    GroupNo = rset.getString(1);
                    SubscriberID = rset.getString(2);
                }
                rset.close();
                stmt.close();
            }

            if (ClientId == 9 || ClientId == 28) {
                Query = "Select COUNT(*) from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    InsuranceFound = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (InsuranceFound > 0) {
                    Query = "Select HISubscriberGroupNo,HISubscriberPolicyNo from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        GroupNo = rset.getString(1);
                        SubscriberID = rset.getString(2);
                    }
                    rset.close();
                    stmt.close();
                }
            }

            SubscriberID = SubscriberID.replace("-", "");
            out.println(String.valueOf(DOS) + "|" + SubscriberID + "|" + FirstName + "|" + LastName + "|" + DOB + "|" + GroupNo + "|" + Gender + "|" + NPI + "|" + proname);
        } catch (Exception ex) {
            pushLogs("Error in GetDetails in main : ", ex);
            helper.SendEmailWithAttachment("Error in EligibilityInquiry_3 ** (GetDetails )", servletContext, ex, "EligibilityInquiry_3", "GetDetails", conn);
            Services.DumException("EligibilityInquiry_3", "Eligibility Get Details", request, ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "PatientUpdateInfo");
            Parser.SetField("ActionID", "GetInput&ID=" + PatientId);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    private void GetResponse(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        ResultSet rset = null;
        Statement stmt = null;
        String Query = "";
        try {
            String soapEndpointUrl = "https://services.gatewayedi.com/eligibility/service.asmx";
            String soapAction = "GatewayEDI.WebServices";
            String GediPayerID = request.getParameter("ProfessionalPayer").trim();
            String NPI = request.getParameter("NPI").trim();
            String FirstName = request.getParameter("FirstName").trim();
            String LastName = request.getParameter("LastName").trim();
            String InsuranceNum = request.getParameter("SubscriberId").trim();
            String DOB = request.getParameter("DOB").trim();
            String Gender = request.getParameter("Gender").trim();
            String DOS = request.getParameter("DateofService").trim();

            String PatientId = request.getParameter("PatientId").trim();
            String proname = request.getParameter("proname").trim();
            String PatientMRN = null;
            if (Gender.toUpperCase().trim().equals("MALE")) {
                Gender = "M";
            } else {
                Gender = "F";
            }

            String Service_Type = request.getParameter("Service_Type").trim();
            if (Service_Type.equals("A"))
                Service_Type = "52";
            else if (Service_Type.equals("B"))
                Service_Type = "86";
            String GPN = request.getParameter("GroupNo").trim();

            finaltrizetto3.callSoapWebService(soapEndpointUrl, soapAction, GediPayerID, NPI, FirstName, LastName, InsuranceNum, DOB, Gender, GPN, PatientId, UserId, proname, Service_Type, ClientId);
            String ResponseType = "";
            String ResultMessage = eiresponse3.ResultMessage;
            if (!ResultMessage.equals("ValidationFailure")) {
                out.println("<!DOCTYPE html><html><body><h3 style=\"color:white;\">Basic Information</h3></body></html>");
                out.println("<table><tbody><tr class=\"Inner\"><td align=left> <font color=black>Patient Name</font> </td><td align=left><font color=black>" + FirstName + " " + LastName + "</font></td> <td align=left> <font color=black>Date of Birth </font></td>" + "<td align=left><font color=black>" + DOB + "</font></td> <td align=left> <font color=black>Gender </font></td>" + "<td align=left><font color=black>" + Gender + "</font></td>" + "<td align=left> <font color=black>Date of Service </font></td>" + "<td align=left><font color=black>" + DOS + "</font></td>" + "<td align=left> <font color=black>Policy Status </font></td>" + "<td align=left><font color=black>" + eiresponse3.policystatus + "</font></td>" + "</tr>" + "</tbody>" + "</table>");
                ResultMessage = "";
                ResponseType = "SUCCESS";
            } else
                ResponseType = "ERROR";

            String strMsg = eiresponse3.finaloutput.toString();
            out.println(strMsg);

            Query = "Select MRN from " + Database + ".PatientReg where status = 0 and ID = " + PatientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientMRN = rset.getString(1);
            }
            rset.close();
            stmt.close();

            //1 - Trizetto
            //2 - Availity
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO oe.EligibilityInquiry (PatientMRN,DateofService,TraceId ,PolicyStatus,strmsg, " +
                                "Name, DateofBirth, Gender, InsuranceNum, GediPayerId, CreatedBy, CreatedDate,ResponseType,FacilityIndex,EProvider) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?) ");
                MainReceipt.setString(1, PatientMRN);
                MainReceipt.setString(2, DOS);
                MainReceipt.setString(3, eiresponse3.traceNo);
                MainReceipt.setString(4, eiresponse3.policystatus);
                MainReceipt.setString(5, strMsg);
                MainReceipt.setString(6, String.valueOf(FirstName) + " " + LastName);
                MainReceipt.setString(7, DOB);
                MainReceipt.setString(8, Gender);
                MainReceipt.setString(9, InsuranceNum);
                MainReceipt.setString(10, GediPayerID);
                MainReceipt.setString(11, UserId);
                MainReceipt.setString(12, ResponseType);
                MainReceipt.setInt(13, ClientId);
                MainReceipt.setInt(14, 1);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception ex) {
                out.println("Error while insertion --> Exception in GetResponse:- " + ex.getMessage());
                out.close();
                out.flush();
                return;
            }
        } catch (Exception ex) {
            pushLogs("EligibilityInquiry2 --> Exception in GetResponse:", ex);
            out.println("EligibilityInquiry2 --> Exception in GetResponse:- " + ex.getMessage());
            out.close();
            out.flush();
        }
    }


}
