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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

@SuppressWarnings("Duplicates")
public class EligibilityInquiry2 extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private Connection conn = null;
    Integer ScreenIndex = 8;


    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

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

    private void GetDetails(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
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
            out.println("Error:-" + ex.getMessage());
        }
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

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();

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
            int UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());

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
                Parser.SetField("FormName","ManagementDashboard");
                Parser.SetField("ActionID","GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);

            if(!helper.AuthorizeScreen(request,out,conn,context,UserIndex,this.ScreenIndex)){
//                out.println("You are not Authorized to access this page");
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "You are not Authorized to access this page");
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                return;
            }


            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "EligibilityGetInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Insurance Eligibility Inquiry 2", "Input Fields Details", FacilityIndex);
                    this.EligibilityGetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "GetDetails":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Insurance Eligibility Inquiry 2 Patient Details", "Get Patient Details and Auto Fill the Input Fields", FacilityIndex);
                    this.GetDetails(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "GetResponse":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Insurance Eligibility Inquiry 2 Get Insurance Information", "Get Insurance Info, Response from finaltrizetto And eireponse Class", FacilityIndex);
                    this.GetResponse(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
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
            Parser.SetField("FormName","ManagementDashboard");
            Parser.SetField("ActionID","GetInput");
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

    private void EligibilityGetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        ResultSet rset = null;
        Statement stmt = null;
        String Query = "";
        final StringBuffer PatientList = new StringBuffer();
        final StringBuffer ServiceType = new StringBuffer();
        final StringBuffer PayProcedureList = new StringBuffer();
        final StringBuffer InstitutionalPayerList = new StringBuffer();
        final StringBuffer ProfessionalPayersList = new StringBuffer();
        try {
            Query = "Select MRN, CONCAT(Title, ' ' , FirstName, ' ', MiddleInitial, ' ', LastName) from " + Database + ".PatientReg where status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option class=Inner value='0'>Please Select Patient</option>");
            while (rset.next()) {
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
            }
            rset.close();
            stmt.close();
            Query = "Select ServiceId, ServiceName from " + Database + ".ServiceType";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ServiceType.append("<option class=Inner value='-1'>All</option>");
            while (rset.next()) {
                ServiceType.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(1) + " - " + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();
            Query = "Select PayerId, PayerProcedure from " + Database + ".PayerProcedures";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PayProcedureList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();
            Query = "Select PayerId, PayerName from " + Database + ".ProfessionalPayers order by status";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ProfessionalPayersList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("ServiceType", String.valueOf(ServiceType));
            Parser.SetField("PayProcedureList", String.valueOf(PayProcedureList));
            Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
            Parser.SetField("ClientIndex", String.valueOf(ClientId));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(servletContext)) + "Forms/EligibilityGetInput2.html");
        } catch (Exception ex) {
            pushLogs("Error in EligibilityGetInput in main : ", ex);
            out.println("Error:-" + ex.getMessage());
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

            finaltrizetto2.callSoapWebService(soapEndpointUrl, soapAction, GediPayerID, NPI, FirstName, LastName, InsuranceNum, DOB, Gender, GPN, PatientId, UserId, proname, Service_Type);
            String ResponseType = "";
            String ResultMessage = eiresponse2.ResultMessage;
            if (!ResultMessage.equals("ValidationFailure")) {
                out.println("<!DOCTYPE html><html><body><h3 style=\"color:white;\">Basic Information</h3></body></html>");
                out.println("<table><tbody><tr class=\"Inner\"><td align=left> <font color=black>Patient Name</font> </td><td align=left><font color=black>" + FirstName + " " + LastName + "</font></td> <td align=left> <font color=black>Date of Birth </font></td>" + "<td align=left><font color=black>" + DOB + "</font></td> <td align=left> <font color=black>Gender </font></td>" + "<td align=left><font color=black>" + Gender + "</font></td>" + "<td align=left> <font color=black>Date of Service </font></td>" + "<td align=left><font color=black>" + DOS + "</font></td>" + "<td align=left> <font color=black>Policy Status </font></td>" + "<td align=left><font color=black>" + eiresponse2.policystatus + "</font></td>" + "</tr>" + "</tbody>" + "</table>");
                ResultMessage = "";
                ResponseType = "SUCCESS";
            } else
                ResponseType = "ERROR";

            String strMsg = eiresponse2.finaloutput.toString();
            out.println(strMsg);
            //1 - Trizetto
            //2 - Availity
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO oe.EligibilityInquiry (PatientMRN,DateofService,TraceId ,PolicyStatus,strmsg, " +
                                "Name, DateofBirth, Gender, InsuranceNum, GediPayerId, CreatedBy, CreatedDate,ResponseType,FacilityIndex,EProvider) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?) ");
                MainReceipt.setString(1, PatientId);
                MainReceipt.setString(2, DOS);
                MainReceipt.setString(3, eiresponse2.traceNo);
                MainReceipt.setString(4, eiresponse2.policystatus);
                MainReceipt.setString(5, strMsg);
                MainReceipt.setString(6, String.valueOf(FirstName) + " " + LastName);
                MainReceipt.setString(7, DOB);
                MainReceipt.setString(8, Gender);
                MainReceipt.setString(9, InsuranceNum);
                MainReceipt.setString(10, GediPayerID);
                MainReceipt.setString(11, UserId);
                MainReceipt.setString(12, ResponseType);
                MainReceipt.setInt(13,ClientId);
                MainReceipt.setInt(14,1);
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
