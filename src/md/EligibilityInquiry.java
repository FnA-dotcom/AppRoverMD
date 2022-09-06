// 
// Decompiled by Procyon v0.5.36
// 

package md;

import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class EligibilityInquiry extends HttpServlet
{
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
        final PrintWriter out = new PrintWriter((OutputStream)response.getOutputStream());
        final Services supp = new Services();

        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        try {
            final Cookie[] cookies = request.getCookies();
            Zone = (UserId = (Passwd = ""));
            final int checkCookie = 0;
            for (int coky = 0; coky < cookies.length; ++coky) {
                final String cName = cookies[coky].getName();
                final String cValue = cookies[coky].getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
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

            Query = "Select dbname from oe.clients where Id = "+ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();
//            if (ClientId == 8) {
//                Database = "oe_2";
//            }
//            else if (ClientId == 9) {
//                Database = "victoria";
//            }
//            else if (ClientId == 10) {
//                Database = "oddasa";
//            }
            if (ActionID.equals("EligibilityGetInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Insurance Eligibility Inquiry", "Input Fields Details", ClientId);
                this.EligibilityGetInput(request, out, conn, context, UserId, Database, ClientId);
            }
            else if (ActionID.equals("GetDetails")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Insurance Eligibility Inquiry Patient Details", "Get Patient Details and Auto Fill the Input Fields", ClientId);
                this.GetDetails(request, out, conn, context, UserId, Database, ClientId);
            }
            else if (ActionID.equals("GetResponse")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Insurance Eligibility Inquiry Get Insurance Information", "Get Insurance Info, Response from finaltrizetto And eireponse Class", ClientId);
                this.GetResponse(request, out, conn, context, UserId, Database, ClientId);
            }
            try {
                conn.close();
            }
            catch (Exception ex) {}
            out.flush();
            out.close();
        }
        catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    void EligibilityGetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
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
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(servletContext)) + "Forms/EligibilityGetInput.html");
        }
        catch (Exception ex) {
            out.println("Error:-" + ex.getMessage());
        }
    }

    void GetDetails(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
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
        int PatientRegId = 0;
        int InsuranceFound = 0;
        try {
            Query = "Select NPI from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                NPI = rset.getString(1);
            }
            rset.close();
            stmt.close();
            Query = " Select ID, FirstName, LastName, DATE_FORMAT(DOB, '%Y%m%d'),DATE_FORMAT(CreatedDate, '%d-%m-%Y'), gender from " + Database + ".PatientReg " + " where MRN = '" + PatientId + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientRegId = rset.getInt(1);
                FirstName = rset.getString(2);
                LastName = rset.getString(3);
                DOB = rset.getString(4);
                DOS = rset.getString(5);
                Gender = rset.getString(6);
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
            out.println(String.valueOf(DOS) + "|" + SubscriberID + "|" + FirstName + "|" + LastName + "|" + DOB + "|" + GroupNo + "|" + Gender + "|" + NPI);
        }
        catch (Exception ex) {
            out.println("Error:-" + ex.getMessage());
        }
    }

    void GetResponse(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        final ResultSet rset = null;
        final Statement stmt = null;
        final String Query = "";
        try {
            final String soapEndpointUrl = "https://services.gatewayedi.com/eligibility/service.asmx";
            final String soapAction = "GatewayEDI.WebServices";
            final String GediPayerID = request.getParameter("ProfessionalPayer").trim();
            final String NPI = request.getParameter("NPI").trim();
            final String FirstName = request.getParameter("FirstName").trim();
            final String LastName = request.getParameter("LastName").trim();
            final String InsuranceNum = request.getParameter("SubscriberId").trim();
            final String DOB = request.getParameter("DOB").trim();
            String Gender = request.getParameter("Gender").trim();
            if (Gender.equals("male")) {
                Gender = "M";
            }
            else {
                Gender = "F";
            }
            final String GPN = request.getParameter("GroupNo").trim();
            finaltrizetto.callSoapWebService("https://services.gatewayedi.com/eligibility/service.asmx", "GatewayEDI.WebServices", GediPayerID, NPI, FirstName, LastName, InsuranceNum, DOB, Gender, GPN);
            final String strMsg = eiresponse.finaloutput.toString();
            out.println(strMsg);
        }
        catch (Exception ex) {
            out.println("Error:-" + ex.getMessage());
        }
    }
}
