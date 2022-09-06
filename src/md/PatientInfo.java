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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("Duplicates")
public class PatientInfo extends HttpServlet {
    public static String msgfinal;
    public static String msgresponse;
    private Connection conn = null;
    static {
        PatientInfo.msgfinal = null;
        PatientInfo.msgresponse = null;
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

    public ServletContext context = null;

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String ActionID = "";

        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
        try {
            HttpSession session = request.getSession(false);

            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

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
            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
//            out.println("OUTSIDE SWITCH");
//            out.println("ActionID -> "+ActionID);
            switch (ActionID) {
                case "sendToEPD":
                    sendToEPD(request, out, conn, context, DatabaseName, FacilityIndex, helper);
                    break;
                case "showRecords":
                    showRecords(request, out, conn, context, DatabaseName, FacilityIndex, helper);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
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

    private void sendToEPD(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String Database, int ClientId, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        Statement hstmt = null;
        final ResultSet hrset = null;
        String Query = "";
        int ID = Integer.parseInt(request.getParameter("ID").trim());

        String Title = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String MaritalStatus = "";
        String DOB = "";
        String Age = "";
        String gender = "";
        String Email = "";
        String PhNumber = "";
        String Address = "";
        String City = "";
        String State = "";
        String Country = "";
        String ZipCode = "";
        String SSN = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        String MRN = "";
        String DOS = "";
        String msg = "";
        int SelfPayChk = 0;
        StringBuilder StatusReport = new StringBuilder();
        try {
            Query = "Select Title, FirstName, MiddleInitial, LastName, MaritalStatus, MRN, DOB, Age, Gender, Email, PhNumber, " +
                    "Address, City, State, Country,  ZipCode, SSN, Occupation, Employer, EmpContact, PriCarePhy, ReasonVisit, SelfPayChk, " +
                    "CreatedDate, Address2 from " + Database + ".PatientReg where ID = " + ID;
out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Title = rset.getString(1);
                FirstName = rset.getString(2);
                MiddleInitial = rset.getString(3);
                LastName = rset.getString(4);
                MaritalStatus = rset.getString(5);
                MRN = rset.getString(6);
                DOB = rset.getString(7);
                Age = rset.getString(8);
                gender = rset.getString(9);
                Email = rset.getString(10);
                PhNumber = rset.getString(11);
                Address = rset.getString(12) + " " + rset.getString(24);
                City = rset.getString(13);
                State = rset.getString(14);
                Country = rset.getString(15);
                ZipCode = rset.getString(16);
                SSN = rset.getString(17);
                Occupation = rset.getString(18);
                Employer = rset.getString(19);
                EmpContact = rset.getString(20);
                PriCarePhy = rset.getString(21);
                ReasonVisit = rset.getString(22);
                SelfPayChk = rset.getInt(23);
                DOS = rset.getString(24);
            }
            rset.close();
            stmt.close();
            out.println("ID :"+FirstName);


            final Date dNow = new Date(System.currentTimeMillis() - 7200000L);
            final SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
            final String MSH7 = ft.format(dNow);
            final String MRN2 = "3109900";
            DOB = DOB.replace("-", "");
            if (gender.toUpperCase().compareTo("FEMALE") == 0) {
                gender = "F";
            } else {
                gender = "M";
            }

            msg = "MSH|^~\\&||665|ADT|3344|" + MSH7 + "||ADT^A04|" + MSH7 + "|P|2.3\r\n" + "EVN|A04|20200707020302|||XXX^^^^^^^^488 \r\n" + "PID|1||" + MRN + "||" + FirstName + "^" + LastName + "^" + MiddleInitial + "||" + DOB + "|" + gender + "||W|" + Address + "^^" + City + "^" + State + "^" + ZipCode + "|" + PhNumber + "|" + PhNumber + "||ENGLISH|M|001||" + SSN + "|||||||||||N \r\n" + "PV1||3^E/R^02|||||194501^TOWNSHEND^PETE|194502^DALTREY^ROGER|194506^ROGERS^PAUL|E|||||||194501^TOWNSHEND^PETE|3||BB1||||||||||||||||G|||||||||\r\n" + "IN1|1|SELF-PAY^SELF PAY|SELF PAY|SELF PAY||PFO Sequence 99 Self P|||||||||4";
            String CurrDate = helper.getCurrDate(request,conn);
            int Result = helper.saveRequestEPD(request, msg, Integer.parseInt(MRN), CurrDate, ClientId, conn, servletContext);
            out.println("ID :"+Result);
            if (Result == 1)
                out.println("1|");
            else
                out.println("0|");




            //            if(!MaritalStatus.equals("")) {
//                MaritalStatus = MaritalStatus.substring(1, 1);
//            }

            //String clientid = request.getParameter("clientid").trim();
//            hl7rm hl7 = new hl7rm();
//            hl7.hl7request(request, out, String.valueOf(ClientId), context);

/*            Query = " insert into oe.request  (msg,mrn, requestdate, RequestType, ClientIndex)  values ('" + msg + "','" + MRN + "', now(),'1', '" + ClientId + "') ";
            hstmt = conn.createStatement();
            hstmt.executeUpdate(Query);
            hstmt.close();*/

//out.println("Status sent to SYSTEM");

/*            Query = "SELECT a.Id,DATE_FORMAT(a.requestdate,'%d-%b-%Y %h:%i:%s') AS RequestDate,a.mrn,a.ResponseCode, \n" +
                    "CONCAT(b.FirstName, ' ', b.LastName , ' ', b.MiddleInitial) AS PatientName, " +
                    "IFNULL(DATE_FORMAT(a.posttime,'%d-%b-%Y %h:%i:%s'),'-') AS ReceivedDate," +
                    "CASE " +
                    "WHEN a.ResponseCode = 1 THEN 'SENT TO EPD' \n" +
                    "WHEN a.ResponseCode = 0 THEN 'Pending' \n" +
                    "ELSE 'No Result' \n" +
                    "END,b.Id \n" +
                    " FROM oe.request a \n" +
                    " STRAIGHT_JOIN " + Database + ".PatientReg b ON a.mrn = b.MRN \n" +
                    " WHERE a.ClientIndex = " + ClientId + " AND a.mrn = '"+MRN+"' \n" +
                    " ORDER BY a.requestdate DESC";
            System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                StatusReport.append("<tr>");
                StatusReport.append("<td width=01%>" + rset.getString(3) + "</td>");//MRN
                StatusReport.append("<td width=01%>" + rset.getString(5) + "</td>");//NAME
                StatusReport.append("<td width=05%>" + rset.getString(2) + "</td>");//SentDate
                StatusReport.append("<td width=01%>" + rset.getString(6) + "</td>");//ReceivedDate
                StatusReport.append("<td width=01%>" + rset.getString(7) + "</td>");//Status
                StatusReport.append("<td width=05%><button id=sendToEPD title=\"Sent To EPD\" onclick=\"sendEPD(this.value)\" value=" + rset.getInt(8) + "  class=\"btn btn-danger btn-md\"> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\" ></i> [SEND] </font></button></td>");
                StatusReport.append("</tr>");
            }
            rset.close();
            stmt.close();*/


/*            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Request has been send, Please find Patient MRN " + MRN);
            Parser.SetField("StatusReport", String.valueOf(StatusReport));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Forms/EPDStatusReport.html");*/
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("StatusReport", StatusReport.toString());
//            Parser.SetField("Message", "Request has been send, Find Patient MRN " + MRN );
//            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/EPDStatusReport.html");
//            out.println("<!DOCTYPE html><html><body><p style=\"color:black;\">Request has been send, Find Patient MRN " + MRN + "</p>");
//            out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
        } catch (Exception e) {
            //out.println("ERROR"+e.getMessage());
            try {
                helper.SendEmailWithAttachment("Error in PatientInfo ** (GetInput)", context, e, "PatientInfo", "GetInput", conn);
                Services.DumException("PatientInfo", "GetInput ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientUpdateInfo");
                Parser.SetField("ActionID", "GetInput&ID=" + ID);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (Exception e2) {
            }
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("Message", e.getMessage());
//            Parser.SetField("FormName", String.valueOf("RegisteredPatients"));
//            Parser.SetField("ActionID", String.valueOf("ShowReport"));
//            out.println(e.getMessage());
//            out.println(Query);
//            String str = "";
//            for (int i = 0; i < e.getStackTrace().length; ++i) {
//                str = str + e.getStackTrace()[i] + "<br>";
//            }
//            out.println(str);
        }
    }

    private void showRecords(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientIdx = Integer.parseInt(request.getParameter("PatientIdx").trim());
        StringBuilder StatusReport = new StringBuilder();

        try {
            int MRN = helper.getPatientRegMRN(request, conn, servletContext, Database, PatientIdx);


            Query = "SELECT a.Id,DATE_FORMAT(a.requestdate,'%d-%b-%Y %h:%i:%s') AS RequestDate,a.mrn,a.ResponseCode, \n" +
                    "CONCAT(b.FirstName, ' ', b.LastName , ' ', b.MiddleInitial) AS PatientName, " +
                    "IFNULL(DATE_FORMAT(a.posttime,'%d-%b-%Y %h:%i:%s'),'-') AS ReceivedDate," +
                    "CASE " +
                    "WHEN a.ResponseCode = 1 THEN 'SENT TO EPD' \n" +
                    "WHEN a.ResponseCode = 0 THEN 'Pending' \n" +
                    "ELSE 'No Result' \n" +
                    "END,b.Id \n" +
                    " FROM oe.request a \n" +
                    " STRAIGHT_JOIN " + Database + ".PatientReg b ON a.mrn = b.MRN \n" +
                    " WHERE a.ClientIndex = " + ClientId + " AND a.mrn = '" + MRN + "' \n" +
                    " ORDER BY a.requestdate DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                StatusReport.append("<tr>");
                StatusReport.append("<td width=01%>" + rset.getString(3) + "</td>");//MRN
                StatusReport.append("<td width=01%>" + rset.getString(5) + "</td>");//NAME
                StatusReport.append("<td width=05%>" + rset.getString(2) + "</td>");//SentDate
                StatusReport.append("<td width=01%>" + rset.getString(6) + "</td>");//ReceivedDate
                StatusReport.append("<td width=01%>" + rset.getString(7) + "</td>");//Status
                StatusReport.append("<td width=05%><button id=sendToEPD title=\"Sent To EPD\" onclick=\"sendEPD(this.value)\" value=" + rset.getInt(8) + "  class=\"btn btn-danger btn-md\"> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\" ></i> [SEND] </font></button></td>");
                StatusReport.append("</tr>");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Please find Patient MRN " + MRN+" in EPD. ");
            Parser.SetField("StatusReport", String.valueOf(StatusReport));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Forms/EPDStatusReport.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientInfo ** (GetInput)", context, e, "PatientInfo", "GetInput", conn);
            Services.DumException("PatientInfo", "GetInput ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "PatientUpdateInfo");
            Parser.SetField("ActionID", "GetInput&ID=" + PatientIdx);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }
}
