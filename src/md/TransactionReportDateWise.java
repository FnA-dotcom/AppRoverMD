package md;

import DAL.Payments;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import PaymentIntegrations.CardConnectPayment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@SuppressWarnings("Duplicates")
public class TransactionReportDateWise extends HttpServlet {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = null;
    private PreparedStatement pStmt = null;

    Integer CardConnectTransaction_Index = 23;
    Integer BoltTransaction_Index = 24;


    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String Action;

        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        String DatabaseName;
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

            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            int UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            Action = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (Action) {
                case "cardConnectGetInput":

                    if(!helper.AuthorizeScreen(request,out,conn,context,UserIndex,CardConnectTransaction_Index)){
//                out.println("You are not Authorized to access this page");
                        Parsehtm Parser = new Parsehtm(request);
                        Parser.SetField("Message", "You are not Authorized to access this page");
                        Parser.SetField("FormName", "ManagementDashboard");
                        Parser.SetField("ActionID", "GetInput");
                        Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                        return;
                    }


                    GetInput(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "getTransactionPassword":
                    getTransactionPassword(request, out, conn, context, UserId, FacilityIndex, helper, DatabaseName);
                    break;
                case "cardConnectShowReport":
                    showReport(request, out, conn, context, UserId, helper, FacilityIndex, DatabaseName);
                    break;
                case "boltGetInput":

                    if(!helper.AuthorizeScreen(request,out,conn,context,UserIndex,BoltTransaction_Index)){
//                out.println("You are not Authorized to access this page");
                        Parsehtm Parser = new Parsehtm(request);
                        Parser.SetField("Message", "You are not Authorized to access this page");
                        Parser.SetField("FormName", "ManagementDashboard");
                        Parser.SetField("ActionID", "GetInput");
                        Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                        return;
                    }

                    boltGetInput(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "boltShowReport":
                    boltShowReport(request, out, conn, context, UserId, helper, FacilityIndex, DatabaseName);
                    break;
                case "refundTransaction":
                    refundTransaction(request, out, conn, context, UserId, helper, FacilityIndex, DatabaseName);
                    break;
                case "refundTransactionBolt":
                    refundTransactionBolt(request, out, conn, context, UserId, helper, FacilityIndex, DatabaseName);
                    break;
                case "voidTransaction":
                    voidTransaction(request, out, conn, context, UserId, helper, FacilityIndex, DatabaseName, payments);
                    break;
                case "voidTransactionBolt":
                    voidTransactionBolt(request, out, conn, context, UserId, helper, FacilityIndex, DatabaseName, payments);
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
            //helper.SendEmail("Main Section", "Error in Transaction Report", "Message Body");
            helper.SendEmailWithAttachment("Error in Transaction Report Date Wise ** (handleRequest)", context, Ex, "TransactionReportDateWise", "handleRequest", conn);
            Services.DumException("TransactionReportDateWise", "Handle Request", request, Ex, getServletContext());
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

    private void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) {
        try {
//            String[] UserDetails = helper.loginUserDetails(request, conn, UserId, servletContext);

            //Card Connect
            //FlagType = 1
            //Bolt
            //FlagType = 2
            if (facilityIndex == 10) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("o85RtQa20", "1");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/TransactionPassword.html");
                return;
            } else {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/CardConnectTransReportInput.html");
            }

        } catch (Exception Ex) {
            //helper.SendEmail("Main Section", "Error in Transaction Report -- (GetInput)", "Message Body");
            helper.SendEmailWithAttachment("Error in Transaction Report ** (GetInput)", servletContext, Ex, "TransactionReportDateWise", "GetInput", conn);
            Services.DumException("TransactionReportDateWise", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void getTransactionPassword(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper, String databaseName) {
        stmt = null;
        rset = null;
        Query = "";
        int transFound = -1;
        String TransactionPwd = request.getParameter("xQar2f0").trim();
        int FlagType = Integer.parseInt(request.getParameter("o85RtQa20").trim());
        try {
            TransactionPwd = FacilityLogin_old.encrypt(TransactionPwd);

            Query = "SELECT COUNT(*) FROM " + databaseName + ".TransactionCredentials WHERE UserPassword = '" + TransactionPwd + "' ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                transFound = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            //transFound = helper.checkingTransactionCredentials(request, servletContext, conn, TransactionPwd);
            if (transFound > 0) {
                Parsehtm Parser = new Parsehtm(request);
                if (FlagType == 1)
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/CardConnectTransReportInput.html");
                else
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/BoltTransReportInput.html");
            } else {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("ErrorMessage", "Password is Incorrect or you are not authorized!!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/TransactionPassword.html");
            }

        } catch (Exception Ex) {
            //helper.SendEmail("Main Section","Error in Transaction Report -- (getTransactionPassword) ","Message Body");
            //int i = SendEmailWithAttachment("Main Section", "Error In Transaction report", "Message Body", request, servletContext, Ex, "Transactionreport", "TransactionPassword");
            helper.SendEmailWithAttachment("Error in Transaction Report ** (getTransactionPassword)", servletContext, Ex, "TransactionReportDateWise", "getTransactionPassword", conn);
            Services.DumException("TransactionReportDateWise", "getTransactionPassword", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "TransactionReportDateWise");
            Parser.SetField("ActionID", "cardConnectGetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, UtilityHelper helper, int facilityIndex, String databaseName) {
        stmt = null;
        rset = null;
        Query = "";
        StringBuilder TransactionReport = new StringBuilder();
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        int SrlNo = 1;
        int refundFlag = 0;
        int voidFlag = 0;
        String Status = "";
        try {
            //StringBuilder TransactionReport = helper.showTransactionReport(request, servletContext, conn, FromDate, ToDate, facilityIndex, databaseName);

/*            Query = "SELECT CONCAT(a.FirstName, ' ', a.LastName , ' ', a.MiddleInitial) AS NAME,DATE_FORMAT(a.DOB,'%d-%b-%Y') AS DOB, a.Gender,b.InvoiceNo,\n" +
                    "c.TotalAmount,c.PaidAmount,c.Paid,c.BalAmount,c.Remarks,\n" +
                    "b.ResponseText,b.ResponseStatus,b.RetRef,IFNULL(DATE_FORMAT(b.RetRefDate,'%d-%b-%Y %h:%i:%s'),'00-00-0000') AS RetDate,b.CreatedDate," +
                    "a.ID,a.MRN\n" +
                    "FROM \n" +
                    "" + databaseName + ".PatientReg a \n" +
                    "STRAIGHT_JOIN oe.CardConnectResponses b ON a.MRN = b.PatientMRN AND b.ClientIndex = " + facilityIndex + " AND b.CreatedDate BETWEEN '" + FromDate + "' AND '" + ToDate + "' \n" +
                    "STRAIGHT_JOIN " + databaseName + ".PaymentReceiptInfo c ON a.MRN = c.PatientMRN AND c.PayMethod = 1";*/
/*            Query = "SELECT a.ID, CONCAT(a.FirstName, ' ', a.LastName , ' ', a.MiddleInitial) AS NAME,DATE_FORMAT(a.DOB,'%d-%b-%Y') AS DOB, a.Gender, a.MRN,\n" +
                    "b.InvoiceNo,b.ResponseText,b.ResponseStatus,b.RetRef,IFNULL(DATE_FORMAT(b.RetRefDate,'%d-%b-%Y'),'00-00-0000') AS RetDate," +
                    "b.CreatedDate AS PaymentDate,IFNULL(b.Remarks,'-'),  IFNULL(b.Amount,0), IFNULL(b.AccountNo,'-'), c.`name` AS ClientName, " +
                    "b.Id AS CardConnectId, b.refundFlag, b.voidFlag \n" +
                    "FROM \n" +
                    "" + databaseName + ".PatientReg a \n" +
                    "STRAIGHT_JOIN oe.CardConnectResponses b ON a.MRN = b.PatientMRN AND b.ClientIndex = " + facilityIndex + " AND " +
                    " b.CreatedDate BETWEEN '" + FromDate + " 00:00:00' AND '" + ToDate + " 23:59:59' AND " +
                    " b.refundFlag = 0 AND b.voidFlag = 0 \n" +
                    "STRAIGHT_JOIN oe.clients c ON a.ClientIndex = c.Id " +
                    "ORDER BY b.CreatedDate DESC ";*/
            Query = "SELECT a.ID, CONCAT(IFNULL(a.FirstName,''), ' ', IFNULL(a.LastName,'') , ' ', IFNULL(a.MiddleInitial,'')) AS NAME,DATE_FORMAT(a.DOB,'%d-%b-%Y') AS DOB, a.Gender, a.MRN,\n" +
                    "b.InvoiceNo,b.ResponseText,b.ResponseStatus,b.RetRef,IFNULL(DATE_FORMAT(b.RetRefDate,'%d-%b-%Y'),'00-00-0000') AS RetDate," +
                    "b.CreatedDate AS PaymentDate,IFNULL(b.Remarks,'-'),  IFNULL(b.Amount,0), IFNULL(b.AccountNo,'-'), c.`name` AS ClientName, " +
                    "b.Id AS CardConnectId, b.refundFlag, b.voidFlag,DATE_FORMAT(b.CreatedDate ,'%d-%b-%Y') \n" +
                    "FROM \n" +
                    "" + databaseName + ".PatientReg a \n" +
                    "STRAIGHT_JOIN " + databaseName + ".CardConnectResponses b ON a.MRN = b.PatientMRN AND b.ClientIndex = " + facilityIndex + " AND " +
                    " b.CreatedDate BETWEEN '" + FromDate + " 00:00:00' AND '" + ToDate + " 23:59:59'  " +
                    "STRAIGHT_JOIN oe.clients c ON a.ClientIndex = c.Id " +
                    "ORDER BY b.CreatedDate DESC ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                refundFlag = 0;
                voidFlag = 0;
                refundFlag = rset.getInt(17);
                voidFlag = rset.getInt(18);
                if (refundFlag == 1) {
                    Status = "Refunded";
                    TransactionReport.append("<tr>");
                    TransactionReport.append("<td>" + rset.getString(5) + "</td>");//MRN

                    TransactionReport.append("<td>" + rset.getString(2) + "</td>");//NAME
                    TransactionReport.append("<td>$" + rset.getString(13) + "</td>");//Amount
                    TransactionReport.append("<td>" + rset.getString(14) + "</td>");//AccountNo
//                    TransactionReport.append("<td>" + rset.getString(6) + "</td>");//InvoiceNo
//                    TransactionReport.append("<td>" + rset.getString(19) + "</td>");//TransDate
//                    TransactionReport.append("<td>" + rset.getString(12) + "</td>");//Remarks
//                    TransactionReport.append("<td>" + rset.getString(7) + "</td>");//ResponseText
//                TransactionReport.append("<td width=08%>" + rset.getString(8) + "</td>");//ResponseStatus
                    TransactionReport.append("<td>" + rset.getString(9) + "</td>");//RetRef
                    TransactionReport.append("<td>" + rset.getString(10) + "</td>");//RetDate
                    TransactionReport.append("<td>" + Status + "</td>");//Status
                    TransactionReport.append("<td> ");
                    if (rset.getString(7).toUpperCase().equals("APPROVAL") && !rset.getString(13).equals("0.0")) {
                        TransactionReport.append("<button id=refundBtn onclick=\"refund(this.value)\" class=\"btn btn-primary btn-sm\" disabled title=\"Amount is refunded!\" value=" + rset.getInt(16) + " target=NewFrame1> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\"></i>[Refund]</font></button>");
                        TransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" onclick=\"voidTransactions(this.value)\" title=\"Amount is refunded!\" disabled value=" + rset.getInt(16) + " target=NewFrame1 > <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i>[Void]</font></button>");
                    } else if (rset.getString(13).equals("0.0") || rset.getString(13).equals("0")) {
                        TransactionReport.append("<button id=refundBtn title=\"Amount is zero!\" class=\"btn btn-primary btn-sm\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\" ></i>[Refund]</font></button>");
                        TransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" title=\"Amount is zero!\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\" ></i>[Void]</font></button>");
                    } else {
                        TransactionReport.append("<button id=refundBtn class=\"btn btn-primary btn-sm\" data-toggle=\"modal\" data-target=\"#refundModal\" value=" + rset.getInt(1) + " disabled><i class=\"fa fa-plus\"></i>[Refund]</button>");
                        TransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" value=" + rset.getInt(1) + " target=NewFrame1 disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i>[Void]</font></button>");
                    }
                    TransactionReport.append("</td>");
                    TransactionReport.append("</tr>");
                } else if (voidFlag == 1) {
                    Status = "Voided";
                    TransactionReport.append("<tr>");
                    TransactionReport.append("<td>" + rset.getString(5) + "</td>");//MRN

                    TransactionReport.append("<td>" + rset.getString(2) + "</td>");//NAME
                    TransactionReport.append("<td>$" + rset.getString(13) + "</td>");//Amount
                    TransactionReport.append("<td>" + rset.getString(14) + "</td>");//AccountNo
//                    TransactionReport.append("<td>" + rset.getString(6) + "</td>");//InvoiceNo
//                    TransactionReport.append("<td>" + rset.getString(19) + "</td>");//TransDate
//                    TransactionReport.append("<td>" + rset.getString(12) + "</td>");//Remarks
//                    TransactionReport.append("<td>" + rset.getString(7) + "</td>");//ResponseText
//                TransactionReport.append("<td width=08%>" + rset.getString(8) + "</td>");//ResponseStatus
                    TransactionReport.append("<td>" + rset.getString(9) + "</td>");//RetRef
                    TransactionReport.append("<td>" + rset.getString(10) + "</td>");//RetDate
                    TransactionReport.append("<td>" + Status + "</td>");//Status
                    TransactionReport.append("<td> ");
                    if (rset.getString(7).toUpperCase().equals("APPROVAL") && !rset.getString(13).equals("0.0")) {
                        TransactionReport.append("<button id=refundBtn onclick=\"refund(this.value)\" class=\"btn btn-primary btn-sm\" title=\"Amount is voided!\"  disabled value=" + rset.getInt(16) + " target=NewFrame1> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\"></i>[Refund]</font></button>");
                        TransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" onclick=\"voidTransactions(this.value)\" title=\"Amount is voided!\"  disabled value=" + rset.getInt(16) + " target=NewFrame1 > <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i>[Void]</font></button>");
                    } else if (rset.getString(13).equals("0.0") || rset.getString(13).equals("0")) {
                        TransactionReport.append("<button id=refundBtn title=\"Amount is zero!\" class=\"btn btn-primary btn-sm\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\" ></i>[Refund]</font></button>");
                        TransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" title=\"Amount is zero!\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\" ></i>[Void]</font></button>");
                    } else {
                        TransactionReport.append("<button id=refundBtn class=\"btn btn-primary btn-sm\" data-toggle=\"modal\" data-target=\"#refundModal\" value=" + rset.getInt(1) + " disabled><i class=\"fa fa-plus\"></i>[Refund]</button>");
                        TransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" value=" + rset.getInt(1) + " target=NewFrame1 disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i>[Void]</font></button>");
                    }
                    TransactionReport.append("</td>");
                    TransactionReport.append("</tr>");
                } else {
                    Status = "No Action";
                    TransactionReport.append("<tr>");
                    TransactionReport.append("<td>" + rset.getString(5) + "</td>");//MRN

                    TransactionReport.append("<td>" + rset.getString(2) + "</td>");//NAME
                    TransactionReport.append("<td>$" + rset.getString(13) + "</td>");//Amount
                    TransactionReport.append("<td>" + rset.getString(14) + "</td>");//AccountNo
//                    TransactionReport.append("<td>" + rset.getString(6) + "</td>");//InvoiceNo
//                    TransactionReport.append("<td>" + rset.getString(19) + "</td>");//TransDate
//                    TransactionReport.append("<td>" + rset.getString(12) + "</td>");//Remarks
//                    TransactionReport.append("<td>" + rset.getString(7) + "</td>");//ResponseText
//                TransactionReport.append("<td width=08%>" + rset.getString(8) + "</td>");//ResponseStatus
                    TransactionReport.append("<td>" + rset.getString(9) + "</td>");//RetRef
                    TransactionReport.append("<td>" + rset.getString(10) + "</td>");//RetDate
                    TransactionReport.append("<td>" + Status + "</td>");//Status
                    TransactionReport.append("<td> ");
                    if (rset.getString(7).toUpperCase().equals("APPROVAL") && !rset.getString(13).equals("0.0")) {
                        TransactionReport.append("<button id=refundBtn onclick=\"refund(this.value)\" class=\"btn btn-primary btn-sm\" value=" + rset.getInt(16) + " target=NewFrame1> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\"></i>[Refund]</font></button>");
                        TransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" onclick=\"voidTransactions(this.value)\" value=" + rset.getInt(16) + " target=NewFrame1 > <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i>[Void]</font></button>");
//                        TransactionReport.append("<button id=printBtn class=\"btn btn-info btn-sm\" onclick=\"printReceipt()\" value=" + rset.getInt(16) + " target=NewFrame1 > <font color = \"FFFFFF\"> <i class=\"fa fa-print\"></i>[Print]</font></button>");
                    } else if (rset.getString(13).equals("0.0") || rset.getString(13).equals("0")) {
                        TransactionReport.append("<button id=refundBtn title=\"Amount is zero!\" class=\"btn btn-primary btn-sm\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\" ></i>[Refund]</font></button>");
                        TransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" title=\"Amount is zero!\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\" ></i>[Void]</font></button>");
//                        TransactionReport.append("<button id=printBtn class=\"btn btn-info btn-sm\" title=\"Amount is zero!\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\" ></i>[Print]</font></button>");
                    } else {
                        TransactionReport.append("<button id=refundBtn class=\"btn btn-primary btn-sm\" data-toggle=\"modal\" data-target=\"#refundModal\" value=" + rset.getInt(1) + " disabled><i class=\"fa fa-plus\"></i>[Refund]</button>");
                        TransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" value=" + rset.getInt(1) + " target=NewFrame1 disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i>[Void]</font></button>");
//                        TransactionReport.append("<button id=printBtn class=\"btn btn-info btn-sm\" value=" + rset.getInt(1) + " target=NewFrame1 disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-print\"></i>[Print]</font></button>");
                    }
                    TransactionReport.append("</td>");
                    TransactionReport.append("</tr>");
                }
                ++SrlNo;
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("TransactionReport", TransactionReport.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/CardConnectTransReportInput.html");
        } catch (Exception Ex) {
            //helper.SendEmail("Main Section", "Error in Transaction Report", "Message Body");
            helper.SendEmailWithAttachment("Error in Transaction Report ** (showReport)", servletContext, Ex, "TransactionReportDateWise", "showReport", conn);
            Services.DumException("TransactionReportDateWise", "showReport", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "TransactionReportDateWise");
            Parser.SetField("ActionID", "cardConnectGetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
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

    private void boltGetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) {
        try {
            //Card Connect
            //FlagType = 1
            //Bolt
            //FlagType = 2
            if (facilityIndex == 10) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("o85RtQa20", "2");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/TransactionPassword.html");
                return;
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/BoltTransReportInput.html");
        } catch (Exception Ex) {
            //helper.SendEmail("Main Section", "Error in Transaction Report", "Message Body");
            helper.SendEmailWithAttachment("Error in Transaction Report ** (boltGetInput)", servletContext, Ex, "TransactionReportDateWise", "boltGetInput", conn);
            Services.DumException("TransactionReportDateWise", "boltGetInput", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
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

    private void boltShowReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, UtilityHelper helper, int facilityIndex, String databaseName) {
        stmt = null;
        rset = null;
        Query = "";

        StringBuilder BoltTransactionReport = new StringBuilder();
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        int SrlNo = 1;
        int refundFlag = 0;
        int voidFlag = 0;
        String Status = "";
        try {
            //StringBuilder TransactionReport = helper.showTransactionReport(request, servletContext, conn, FromDate, ToDate, facilityIndex, databaseName);

/*            Query = "SELECT a.ID, CONCAT(a.FirstName, ' ', a.LastName , ' ', a.MiddleInitial) AS NAME," +
                    "DATE_FORMAT(a.DOB,'%d-%b-%Y') AS DOB, a.Gender, a.MRN, b.InvoiceNo,b.JSON_Response, b.Id, DATE_FORMAT(b.CreatedDate,'%d-%b-%Y') \n" +
                    "FROM \n" +
                    "" + databaseName + ".PatientReg a \n" +
                    "STRAIGHT_JOIN " + databaseName + ".JSON_Response b ON a.MRN = b.PatientMRN AND " +
                    "b.CreatedDate BETWEEN '" + FromDate + " 00:00:00' AND '" + ToDate + " 23:59:59' AND " +
                    "b.RefundFlag = 0 AND b.VoidFlag = 0 \n" +
                    "ORDER BY b.CreatedDate ";*/
            Query = "SELECT a.ID, CONCAT(IFNULL(a.FirstName,''), ' ', IFNULL(a.LastName,'') , ' ', IFNULL(a.MiddleInitial,'')) AS NAME," +
                    "DATE_FORMAT(a.DOB,'%d-%b-%Y') AS DOB, a.Gender, a.MRN, b.InvoiceNo,b.JSON_Response, " +
                    "b.Id, DATE_FORMAT(b.CreatedDate,'%d-%b-%Y'), b.RefundFlag, b.VoidFlag \n" +
                    "FROM \n" +
                    "" + databaseName + ".PatientReg a \n" +
                    "STRAIGHT_JOIN " + databaseName + ".JSON_Response b ON a.MRN = b.PatientMRN AND " +
                    "b.CreatedDate BETWEEN '" + FromDate + " 00:00:00' AND '" + ToDate + " 23:59:59' " +
                    "ORDER BY b.CreatedDate ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                refundFlag = 0;
                voidFlag = 0;
                refundFlag = rset.getInt(10);
                voidFlag = rset.getInt(11);

                JSONParser parser = new JSONParser();
                Object obj = parser.parse("[" + rset.getString(7) + "]");
                JSONArray array = (JSONArray) obj;
                JSONObject obj2 = (JSONObject) array.get(0);
                String amount = (String) obj2.get("amount");
                String retref = (String) obj2.get("retref");
                String resptext = (String) obj2.get("resptext");

                if (refundFlag == 1) {
                    Status = "Refunded";
                    BoltTransactionReport.append("<tr>");
                    BoltTransactionReport.append("<td width=02%>" + rset.getString(5) + "</td>");//MRN
                    BoltTransactionReport.append("<td width=08%>" + rset.getString(2) + "</td>");//NAME
                    BoltTransactionReport.append("<td width=08%>" + rset.getString(6) + "</td>");//InvoiceNo
                    BoltTransactionReport.append("<td width=08%>$ " + amount + "</td>");//amount
                    BoltTransactionReport.append("<td width=05%>" + retref + "</td>");//RetRef
                    BoltTransactionReport.append("<td width=05%>" + resptext + "</td>");//ResponseText
                    BoltTransactionReport.append("<td width=18%>" + Status + "</td>");//Status

                    BoltTransactionReport.append("<td width=32%> ");
                    if (resptext.equals("Approval") && !amount.equals("0.0")) {
                        BoltTransactionReport.append("<button id=refundBtn onclick=\"refund(this.value)\" class=\"btn btn-primary btn-sm\" disabled title=\"Amount is refunded!\" value=" + rset.getInt(8) + " target=NewFrame1> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\"></i> [Refund] </font></button>&nbsp;&nbsp;");
                        BoltTransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" onclick=\"voidTransactions(this.value)\" disabled title=\"Amount is refunded!\" disabled value=" + rset.getInt(8) + " target=NewFrame1 > <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i> [Void] </font></button>");
                    } else if (amount.equals("0.0") || amount.equals("0")) {
                        BoltTransactionReport.append("<button id=refundBtn title=\"Amount is zero!\" class=\"btn btn-primary btn-sm\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\" ></i> [Refund] </font></button>&nbsp;&nbsp;");
                        BoltTransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" title=\"Amount is zero!\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\" ></i> [Void] </font></button>");
                    } else {
                        BoltTransactionReport.append("<button id=refundBtn class=\"btn btn-primary btn-sm\" data-toggle=\"modal\" data-target=\"#refundModal\" value=" + rset.getInt(1) + " disabled><i class=\"fa fa-plus\"></i> [Refund] </button>");
                        BoltTransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" value=" + rset.getInt(1) + " target=NewFrame1 disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i> [Void] </font></button>");
                    }
                    BoltTransactionReport.append("</td>");
                    BoltTransactionReport.append("</tr>");
                } else if (voidFlag == 1) {
                    Status = "Voided";
                    BoltTransactionReport.append("<tr>");
                    BoltTransactionReport.append("<td width=02%>" + rset.getString(5) + "</td>");//MRN
                    BoltTransactionReport.append("<td width=08%>" + rset.getString(2) + "</td>");//NAME
                    BoltTransactionReport.append("<td width=08%>" + rset.getString(6) + "</td>");//InvoiceNo
                    BoltTransactionReport.append("<td width=08%>$ " + amount + "</td>");//amount
                    BoltTransactionReport.append("<td width=05%>" + retref + "</td>");//RetRef
                    BoltTransactionReport.append("<td width=05%>" + resptext + "</td>");//ResponseText
                    BoltTransactionReport.append("<td width=18%>" + Status + "</td>");//Status

                    BoltTransactionReport.append("<td width=32%> ");
                    if (resptext.equals("Approval") && !amount.equals("0.0")) {
                        BoltTransactionReport.append("<button id=refundBtn onclick=\"refund(this.value)\" class=\"btn btn-primary btn-sm\"  disabled title=\"Amount is voided!\" value=" + rset.getInt(8) + " target=NewFrame1> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\"></i> [Refund] </font></button>&nbsp;&nbsp;");
                        BoltTransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" onclick=\"voidTransactions(this.value)\" disabled title=\"Amount is voided!\" disabled value=" + rset.getInt(8) + " target=NewFrame1 > <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i> [Void] </font></button>");
                    } else if (amount.equals("0.0") || amount.equals("0")) {
                        BoltTransactionReport.append("<button id=refundBtn title=\"Amount is zero!\" class=\"btn btn-primary btn-sm\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\" ></i> [Refund] </font></button>&nbsp;&nbsp;");
                        BoltTransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" title=\"Amount is zero!\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\" ></i> [Void] </font></button>");
                    } else {
                        BoltTransactionReport.append("<button id=refundBtn class=\"btn btn-primary btn-sm\" data-toggle=\"modal\" data-target=\"#refundModal\" value=" + rset.getInt(1) + " disabled><i class=\"fa fa-plus\"></i> [Refund] </button>");
                        BoltTransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" value=" + rset.getInt(1) + " target=NewFrame1 disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i> [Void] </font></button>");
                    }
                    BoltTransactionReport.append("</td>");
                    BoltTransactionReport.append("</tr>");
                } else {
                    Status = "No Action";
                    BoltTransactionReport.append("<tr>");
                    BoltTransactionReport.append("<td width=02%>" + rset.getString(5) + "</td>");//MRN
                    BoltTransactionReport.append("<td width=08%>" + rset.getString(2) + "</td>");//NAME
                    BoltTransactionReport.append("<td width=08%>" + rset.getString(6) + "</td>");//InvoiceNo
                    BoltTransactionReport.append("<td width=08%>$ " + amount + "</td>");//amount
                    BoltTransactionReport.append("<td width=05%>" + retref + "</td>");//RetRef
                    BoltTransactionReport.append("<td width=05%>" + resptext + "</td>");//ResponseText
                    BoltTransactionReport.append("<td width=18%>" + Status + "</td>");//Status

                    BoltTransactionReport.append("<td width=32%> ");
                    if (resptext.equals("Approval") && !amount.equals("0.0")) {
                        BoltTransactionReport.append("<button id=refundBtn onclick=\"refund(this.value)\" class=\"btn btn-primary btn-sm\" value=" + rset.getInt(8) + " target=NewFrame1> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\"></i> [Refund] </font></button>&nbsp;&nbsp;");
                        BoltTransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" onclick=\"voidTransactions(this.value)\" value=" + rset.getInt(8) + " target=NewFrame1 > <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i> [Void] </font></button>");
                    } else if (amount.equals("0.0") || amount.equals("0")) {
                        BoltTransactionReport.append("<button id=refundBtn title=\"Amount is zero!\" class=\"btn btn-primary btn-sm\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\" ></i> [Refund] </font></button>&nbsp;&nbsp;");
                        BoltTransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" title=\"Amount is zero!\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\" ></i> [Void] </font></button>");
                    } else {
                        BoltTransactionReport.append("<button id=refundBtn class=\"btn btn-primary btn-sm\" data-toggle=\"modal\" data-target=\"#refundModal\" value=" + rset.getInt(1) + " disabled><i class=\"fa fa-plus\"></i> [Refund] </button>");
                        BoltTransactionReport.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" value=" + rset.getInt(1) + " target=NewFrame1 disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i> [Void] </font></button>");
                    }
                    BoltTransactionReport.append("</td>");
                    BoltTransactionReport.append("</tr>");
                }

                ++SrlNo;
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("BoltTransactionReport", BoltTransactionReport.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/BoltTransReportInput.html");
        } catch (Exception Ex) {
            //helper.SendEmail("Main Section", "Error in Transaction Report", "Message Body");
            helper.SendEmailWithAttachment("Error in Transaction Report ** (boltShowReport)", servletContext, Ex, "TransactionReportDateWise", "boltShowReport", conn);
            Services.DumException("TransactionReportDateWise", "boltShowReport", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "TransactionReportDateWise");
            Parser.SetField("ActionID", "boltGetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
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

    private void refundTransaction(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, UtilityHelper helper, int facilityIndex, String databaseName) {
        stmt = null;
        rset = null;
        Query = "";
        pStmt = null;
        //Type Flag
        // 0 - Sandbox
        //int FlagType = 0;
        // 1 - Dev
        //int FlagType = 1;
        // 2 - Prod
        int FlagType = 2;
//        out.println("Parameter Val " + request.getParameter("CardConnectIdx").trim());
        int CardConnectIndx = Integer.parseInt(request.getParameter("CardConnectIdx").trim());
        int PatientMRN = 0;
        String ENDPOINT = "";
        String USERNAME = "";
        String PASSWORD = "";
        String MerchantId = "";
        String Currency = "";
        String Amount = "";
        String RetRef = "";
        String InvoiceNo = "";
        NumberFormat nf = new DecimalFormat("##.00");
        String UserIP = helper.getClientIp(request);

        try {
            String[] cardConnect = helper.getCardConnectData(request, conn, servletContext, CardConnectIndx, databaseName);
            Amount = cardConnect[0];
            RetRef = cardConnect[1];
            InvoiceNo = cardConnect[2];
            PatientMRN = Integer.parseInt(cardConnect[3]);
            facilityIndex = Integer.parseInt(cardConnect[4]);

/*            Query = "SELECT PatientMRN,Amount,RetRef,InvoiceNo,ClientIndex FROM " + databaseName + ".CardConnectResponses " +
                    "WHERE Id = " + CardConnectIndx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientMRN = rset.getInt(1);
                Amount = rset.getString(2);
                RetRef = rset.getString(3);
                InvoiceNo = rset.getString(4);
                facilityIndex = rset.getInt(5);
            }
            rset.close();
            stmt.close();*/

/*            Query = "Select dbname, IFNULL(DirectoryName,'') from oe.clients where Id = " + facilityIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                databaseName = rset.getString(1);
            }
            rset.close();
            stmt.close();*/

            String[] AuthConnect = helper.getAuthConnect(conn, FlagType, facilityIndex);
            ENDPOINT = AuthConnect[0];
            USERNAME = AuthConnect[1];
            PASSWORD = AuthConnect[2];
            MerchantId = AuthConnect[3];
            Currency = AuthConnect[4];

/*            out.println("ENDPOINT " + ENDPOINT + "<br>");
            out.println("USERNAME " + USERNAME + "<br>");
            out.println("PASSWORD " + PASSWORD + "<br>");
            out.println("MerchantId " + MerchantId + "<br>");
            out.println("Currency " + Currency + "<br>");*/

            CardConnectPayment cardConnectPayment = new CardConnectPayment();
            String[] Response = cardConnectPayment.refundTransaction(ENDPOINT, USERNAME, PASSWORD, MerchantId, Amount, Currency, RetRef);
            /*        respproc: PPS
        amount: 11.00
        resptext: Approval
        orderId:
        receipt: {"dateTime":"20210217172531","dba":"Victoria ER","address2":"VICTORIA, TX","phone":"3615731500","footer":"","nameOnCard":"Abidtest2","address1":"","orderNote":"","header":"","items":""}
        retref: 056536464911
        respstat: A
        respcode: 00
        merchid: 496406685883*/
/*            out.println("respproc " + Response[0] + "<br> ");
            out.println("amount " + Response[1] + "<br> ");
            out.println("resptext " + Response[2] + "<br> ");
            out.println("orderId " + Response[3] + "<br> ");
            out.println("receipt " + Response[4] + "<br> ");
            out.println("retref " + Response[5] + "<br> ");
            out.println("respstat " + Response[6] + "<br> ");
            out.println("respcode " + Response[7] + "<br> ");
            out.println("merchid " + Response[8] + "<br> ");*/

            try {
                pStmt = conn.prepareStatement(
                        "INSERT INTO " + databaseName + ".RefundTransactions (Amount, ResponseText, OrderId, ResponseCode, MerchantId, " +
                                "ResponseProc, Receipt,Currency,ResponseStatus,ReturnReference,Status,CreatedDate," +
                                "PatientMRN,InvoiceNo,OriginalRetRef,CardConnectResponseIndex,TransactionType,UserIP,ActionID,CreatedBy) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?,?,?,?,?,?,?) ");
                pStmt.setString(1, Response[1]);
                pStmt.setString(2, Response[2]);
                pStmt.setString(3, Response[3]);
                pStmt.setString(4, Response[7]);//ResponseCode
                pStmt.setString(5, Response[8]);//MerchantId
                pStmt.setString(6, Response[0]);//ResponseProc
                pStmt.setString(7, Response[4]);//Receipt
                pStmt.setString(8, Currency);//Currency
                pStmt.setString(9, Response[6]);//ResponseStatus
                pStmt.setString(10, Response[5]);//ReturnReference
                pStmt.setInt(11, PatientMRN);//PatientMRN
                pStmt.setString(12, InvoiceNo);//InvoiceNo
                pStmt.setString(13, RetRef);//OriginalRetRef
                pStmt.setInt(14, CardConnectIndx);//CardConnectResponseIndex
                pStmt.setString(15, "Card Connect");//TransactionType
                pStmt.setString(16, UserIP);
                pStmt.setString(17, "refundTransaction");
                pStmt.setString(18, UserId);
                pStmt.executeUpdate();
                pStmt.close();
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in Transaction Report ** (refundTransaction -- while insertion data in RefundTransaction table)", servletContext, Ex, "TransactionReportDateWise", "refundTransaction", conn);
                Services.DumException("TransactionReportDateWise", "refundTransaction -- while insertion data in RefundTransaction table", request, Ex, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "TransactionReportDateWise");
                Parser.SetField("ActionID", "cardConnectGetInput");
                try {
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }

            if (Response[2].equals("Approval") || Response[2].equals("APPROVAL") ||
                    Response[2].equals("Success") || Response[2].equals("SUCCESS")) {


                Query = " Update " + databaseName + ".CardConnectResponses SET refundFlag = 1, refundDate = NOW(), UserIP = '" + UserIP + "', " +
                        "ActionID = 'refundTransaction' " +
                        " WHERE Id =  " + CardConnectIndx;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();


                double TotalAmount = 0.0;
                double BalanceAmount = 0.0;
                double PaidAmount = 0.0;
                int Paid = 0;

                Query = "SELECT TotalAmount,BalAmount,Paid,PaidAmount FROM " + databaseName + ".InvoiceMaster " +
                        "WHERE PatientMRN = " + PatientMRN + " AND InvoiceNo = '" + InvoiceNo + "' AND Status = 0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    TotalAmount = rset.getDouble(1);
                    BalanceAmount = rset.getDouble(2);
                    Paid = rset.getInt(3);
                    PaidAmount = rset.getDouble(4);
                }
                rset.close();
                stmt.close();

                if (Paid == 1) {
                    if (PaidAmount == BalanceAmount) {
                        Paid = 0;
                    }
                }

                try {
                    Query = " Update " + databaseName + ".InvoiceMaster set PaidAmount = '" + nf.format(PaidAmount - Double.parseDouble(Amount)) + "', " +
                            "RefundFlag = 1 , BalAmount = '" + nf.format((BalanceAmount + Double.parseDouble(Amount))) + "', Paid = '" + Paid + "', RefundDateTime = now() " +
                            " WHERE PatientMRN = " + PatientMRN + " AND InvoiceNo = '" + InvoiceNo + "' AND Status = 0";
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                } catch (Exception Ex) {
                    helper.SendEmailWithAttachment("Error in Transaction Report ** (refundTransaction -- while updation of Invoice Master)", servletContext, Ex, "TransactionReportDateWise", "refundTransaction", conn);
                    Services.DumException("TransactionReportDateWise", "refundTransaction -- while updation of Invoice Master", request, Ex, getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "TransactionReportDateWise");
                    Parser.SetField("ActionID", "cardConnectGetInput");
                    try {
                        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }

                PaidAmount = Double.parseDouble(nf.format(PaidAmount - Double.parseDouble(Amount)));
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + databaseName + ".PaymentReceiptInfo (PatientMRN,InvoiceNo,TotalAmount," +
                                    " PaidAmount,Paid,RefNo,Remarks,PayMethod,CreatedDate, BalAmount) " +
                                    "VALUES (?,?,?,?,?,?,?,?,now(),?) ");
                    MainReceipt.setInt(1, PatientMRN);
                    MainReceipt.setString(2, InvoiceNo);
                    MainReceipt.setDouble(3, TotalAmount);
                    MainReceipt.setDouble(4, -PaidAmount);
                    MainReceipt.setInt(5, Paid);
                    MainReceipt.setString(6, InvoiceNo);
                    MainReceipt.setString(7, "Amount Refund");
                    MainReceipt.setString(8, "1");
                    MainReceipt.setDouble(9, (BalanceAmount + Double.parseDouble(Amount)));
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception Ex) {
                    helper.SendEmailWithAttachment("Error in Transaction Report ** (refundTransaction)", servletContext, Ex, "TransactionReportDateWise", "refundTransaction", conn);
                    Services.DumException("TransactionReportDateWise", "refundTransaction -- while insertion in PaymentReceiptInfo table", request, Ex, getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "TransactionReportDateWise");
                    Parser.SetField("ActionID", "cardConnectGetInput");
                    try {
                        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }

                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Your Payment has been refunded!!");
                Parser.SetField("FormName", "TransactionReportDateWise");
                Parser.SetField("ActionID", "cardConnectGetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
            } else {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", Response[2]);
                Parser.SetField("FormName", "TransactionReportDateWise");
                Parser.SetField("ActionID", "cardConnectGetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/PaymentMessage.html");
            }
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Transaction Report ** (refundTransaction)", servletContext, Ex, "TransactionReportDateWise", "refundTransaction", conn);
            Services.DumException("TransactionReportDateWise", "refundTransaction", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "TransactionReportDateWise");
            Parser.SetField("ActionID", "cardConnectGetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
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

    private void refundTransactionBolt(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, UtilityHelper helper, int facilityIndex, String databaseName) {
        stmt = null;
        rset = null;
        Query = "";
        pStmt = null;

        int BoltIdx = Integer.parseInt(request.getParameter("BoltIdx").trim());
        NumberFormat nf = new DecimalFormat("##.00");
        int PatientMRN = 0;
        String ENDPOINT = "";
        String USERNAME = "";
        String PASSWORD = "";
        String MerchantId = "";
        String Currency = "";
        String Amount = "";
        String RetRef = "";
        String InvoiceNo = "";
        String JSON_Response = "";
        String UserIP = helper.getClientIp(request);
        //Type Flag
        // 0 - Sandbox
        //int FlagType = 0;
        // 1 - Dev
        //int FlagType = 1;
        // 2 - Prod
        int FlagType = 2;
        try {
            Query = "SELECT InvoiceNo,PatientMRN,JSON_Response FROM " + databaseName + ".JSON_Response WHERE Id =" + BoltIdx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                InvoiceNo = rset.getString(1);
                PatientMRN = rset.getInt(2);
                JSON_Response = rset.getString(3);
            }
            rset.close();
            stmt.close();

            JSONParser parser = new JSONParser();
            Object obj = parser.parse("[" + JSON_Response + "]");
            JSONArray array = (JSONArray) obj;
            JSONObject obj2 = (JSONObject) array.get(0);
            Amount = (String) obj2.get("amount");
            RetRef = (String) obj2.get("retref");

            String[] AuthConnect = helper.getAuthConnect(conn, FlagType, facilityIndex);
            ENDPOINT = AuthConnect[0];
            USERNAME = AuthConnect[1];
            PASSWORD = AuthConnect[2];
            MerchantId = AuthConnect[3];
            Currency = AuthConnect[4];

            CardConnectPayment cardConnectPayment = new CardConnectPayment();
            String[] Response = cardConnectPayment.refundTransaction(ENDPOINT, USERNAME, PASSWORD, MerchantId, Amount, Currency, RetRef);

/*            out.println("respproc " + Response[0] + "<br> ");
            out.println("amount " + Response[1] + "<br> ");
            out.println("resptext " + Response[2] + "<br> ");
            out.println("orderId " + Response[3] + "<br> ");
            out.println("receipt " + Response[4] + "<br> ");
            out.println("retref " + Response[5] + "<br> ");
            out.println("respstat " + Response[6] + "<br> ");
            out.println("respcode " + Response[7] + "<br> ");
            out.println("merchid " + Response[8] + "<br> ");*/

            try {
                pStmt = conn.prepareStatement(
                        "INSERT INTO " + databaseName + ".RefundTransactions (Amount, ResponseText, OrderId, ResponseCode, MerchantId, " +
                                "ResponseProc, Receipt,Currency,ResponseStatus,ReturnReference,Status,CreatedDate," +
                                "PatientMRN,InvoiceNo,OriginalRetRef,BoltIdx,TransactionType,UserIP,ActionID,CreatedBy) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?,?,?,?,?,?,?) ");
                pStmt.setString(1, Response[1]);
                pStmt.setString(2, Response[2]);
                pStmt.setString(3, Response[3]);
                pStmt.setString(4, Response[7]);//ResponseCode
                pStmt.setString(5, Response[8]);//MerchantId
                pStmt.setString(6, Response[0]);//ResponseProc
                pStmt.setString(7, Response[4]);//Receipt
                pStmt.setString(8, Currency);//Currency
                pStmt.setString(9, Response[6]);//ResponseStatus
                pStmt.setString(10, Response[5]);//ReturnReference
                pStmt.setInt(11, PatientMRN);//PatientMRN
                pStmt.setString(12, InvoiceNo);//InvoiceNo
                pStmt.setString(13, RetRef);//OriginalRetRef
                pStmt.setInt(14, BoltIdx);//BoltIdx
                pStmt.setString(15, "Bolt");//TransactionType
                pStmt.setString(16, UserIP);
                pStmt.setString(17, "refundTransactionBolt");
                pStmt.setString(18, UserId);
                pStmt.executeUpdate();
                pStmt.close();
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in Transaction Report ** (refundTransactionBolt)", servletContext, Ex, "TransactionReportDateWise", "refundTransactionBolt", conn);
                Services.DumException("TransactionReportDateWise", "refundTransaction -- Insertion RefundTransactions Table BOLT", request, Ex, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "TransactionReportDateWise");
                Parser.SetField("ActionID", "boltGetInput");
                try {
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }

            if (Response[2].equals("Approval") || Response[2].equals("APPROVAL") ||
                    Response[2].equals("Success") || Response[2].equals("SUCCESS")) {
                double TotalAmount = 0.0;
                double BalanceAmount = 0.0;
                double PaidAmount = 0.0;
                int Paid = 0;

                Query = " Update " + databaseName + ".JSON_Response SET RefundFlag = 1, RefundDate = NOW(), UserIP = '" + UserIP + "'," +
                        " ActionID = 'refundTransactionBolt' " +
                        " WHERE Id =  " + BoltIdx;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

                Query = "SELECT TotalAmount,BalAmount,Paid,PaidAmount FROM " + databaseName + ".InvoiceMaster " +
                        " WHERE PatientMRN = " + PatientMRN + " AND InvoiceNo = '" + InvoiceNo + "' AND Status = 0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    TotalAmount = rset.getDouble(1);
                    BalanceAmount = rset.getDouble(2);
                    Paid = rset.getInt(3);
                    PaidAmount = rset.getDouble(4);
                }
                rset.close();
                stmt.close();

                if (Paid == 1) {
                    if (PaidAmount == BalanceAmount) {
                        Paid = 0;
                    }
                }

                try {
                    Query = " Update " + databaseName + ".InvoiceMaster set PaidAmount = '" + nf.format(PaidAmount - Double.parseDouble(Amount)) + "', " +
                            "RefundFlag = 1 , BalAmount = '" + nf.format((BalanceAmount + Double.parseDouble(Amount))) + "', Paid = '" + Paid + "', RefundDateTime = now() " +
                            " WHERE PatientMRN = " + PatientMRN + " AND InvoiceNo = '" + InvoiceNo + "' AND Status = 0";
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                } catch (Exception Ex) {
                    helper.SendEmailWithAttachment("Error in Transaction Report ** (refundTransactionBolt)", servletContext, Ex, "TransactionReportDateWise", "refundTransactionBolt", conn);
                    Services.DumException("TransactionReportDateWise", "refundTransaction -- Error 2 - Updation ERROR RefundTransactions Table", request, Ex, getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "TransactionReportDateWise");
                    Parser.SetField("ActionID", "boltGetInput");
                    try {
                        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }

                PaidAmount = Double.parseDouble(nf.format(PaidAmount - Double.parseDouble(Amount)));
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + databaseName + ".PaymentReceiptInfo (PatientMRN,InvoiceNo,TotalAmount," +
                                    " PaidAmount,Paid,RefNo,Remarks,PayMethod,CreatedDate, BalAmount) " +
                                    "VALUES (?,?,?,?,?,?,?,?,now(),?) ");
                    MainReceipt.setInt(1, PatientMRN);
                    MainReceipt.setString(2, InvoiceNo);
                    MainReceipt.setDouble(3, TotalAmount);
                    MainReceipt.setDouble(4, -PaidAmount);
                    MainReceipt.setInt(5, Paid);
                    MainReceipt.setString(6, InvoiceNo);
                    MainReceipt.setString(7, "Amount Refund");
                    MainReceipt.setString(8, "1");
                    MainReceipt.setDouble(9, (BalanceAmount + Double.parseDouble(Amount)));
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception Ex) {
                    helper.SendEmailWithAttachment("Error in Transaction Report ** (refundTransactionBolt)", servletContext, Ex, "TransactionReportDateWise", "refundTransactionBolt", conn);
                    Services.DumException("TransactionReportDateWise", "refundTransaction -- Error 3- Insertion PaymentReceiptInfo Table", request, Ex, getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "TransactionReportDateWise");
                    Parser.SetField("ActionID", "boltGetInput");
                    try {
                        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }

                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Your Payment has been refunded!!");
                Parser.SetField("FormName", "TransactionReportDateWise");
                Parser.SetField("ActionID", "boltGetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
            } else {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", Response[2]);
                Parser.SetField("FormName", "TransactionReportDateWise");
                Parser.SetField("ActionID", "boltGetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/PaymentMessge.html");
            }
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Transaction Report ** (refundTransactionBolt)", servletContext, Ex, "TransactionReportDateWise", "refundTransactionBolt", conn);
            Services.DumException("TransactionReportDateWise", "refundTransaction -- Error 4 - Exception in refundTransactionBolt", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "TransactionReportDateWise");
            Parser.SetField("ActionID", "boltGetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
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

    private void voidTransaction(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, UtilityHelper helper, int facilityIndex, String databaseName, Payments payments) {
        stmt = null;
        rset = null;
        Query = "";
        pStmt = null;
        //Type Flag
        // 0 - Sandbox
        //int FlagType = 0;
        // 1 - Dev
        //int FlagType = 1;
        // 2 - Prod
        int FlagType = 2;
        int CardConnectIndx = Integer.parseInt(request.getParameter("CardConnectIdx").trim());
        int PatientMRN = 0;
        String ENDPOINT = "";
        String USERNAME = "";
        String PASSWORD = "";
        String MerchantId = "";
        String Currency = "";
        String Amount = "";
        String RetRef = "";
        String InvoiceNo = "";
        NumberFormat nf = new DecimalFormat("##.00");
        String UserIP = helper.getClientIp(request);

        try {
            Query = "SELECT PatientMRN,Amount,RetRef,InvoiceNo,ClientIndex FROM " + databaseName + ".CardConnectResponses " +
                    " WHERE Id = " + CardConnectIndx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientMRN = rset.getInt(1);
                Amount = rset.getString(2);
                RetRef = rset.getString(3);
                InvoiceNo = rset.getString(4);
                facilityIndex = rset.getInt(5);
            }
            rset.close();
            stmt.close();

            Query = "Select dbname, IFNULL(DirectoryName,'') from oe.clients where Id = " + facilityIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                databaseName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            String[] AuthConnect = helper.getAuthConnect(conn, FlagType, facilityIndex);
            ENDPOINT = AuthConnect[0];
            USERNAME = AuthConnect[1];
            PASSWORD = AuthConnect[2];
            MerchantId = AuthConnect[3];
            Currency = AuthConnect[4];

/*            out.println("ENDPOINT " + ENDPOINT + "<br>");
            out.println("USERNAME " + USERNAME + "<br>");
            out.println("PASSWORD " + PASSWORD + "<br>");
            out.println("MerchantId " + MerchantId + "<br>");
            out.println("Currency " + Currency + "<br>");*/

            CardConnectPayment cardConnectPayment = new CardConnectPayment();
            String[] Response = cardConnectPayment.voidTransaction(ENDPOINT, USERNAME, PASSWORD, MerchantId, Amount, Currency, RetRef);

/*            out.println("respproc " + Response[0] + "<br> ");
            out.println("amount " + Response[1] + "<br> ");
            out.println("resptext " + Response[2] + "<br> ");
            out.println("orderId " + Response[3] + "<br> ");
            out.println("receipt " + Response[4] + "<br> ");
            out.println("retref " + Response[5] + "<br> ");
            out.println("respstat " + Response[6] + "<br> ");
            out.println("respcode " + Response[7] + "<br> ");
            out.println("merchid " + Response[8] + "<br> ");*/

            try {
                pStmt = conn.prepareStatement(
                        "INSERT INTO " + databaseName + ".VoidTransactions (Amount, ResponseText, OrderId, ResponseCode, MerchantIndex, " +
                                "RespProc, Receipt,Currency,ResponseStatus,ReturnReference,Status,CreatedDate," +
                                "PatientMRN,InvoiceNo,OriginalRetRef,CardConnectIndex,TransactionType,UserIP,ActionID,CreatedBy) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?,?,?,?,?,?,?) ");
                pStmt.setString(1, Response[1]);
                pStmt.setString(2, Response[2]);
                pStmt.setString(3, Response[3]);
                pStmt.setString(4, Response[7]);//ResponseCode
                pStmt.setString(5, Response[8]);//MerchantId
                pStmt.setString(6, Response[0]);//ResponseProc
                pStmt.setString(7, Response[4]);//Receipt
                pStmt.setString(8, Currency);//Currency
                pStmt.setString(9, Response[6]);//ResponseStatus
                pStmt.setString(10, Response[5]);//ReturnReference
                pStmt.setInt(11, PatientMRN);//PatientMRN
                pStmt.setString(12, InvoiceNo);//InvoiceNo
                pStmt.setString(13, RetRef);//OriginalRetRef
                pStmt.setInt(14, CardConnectIndx);//CardConnectResponseIndex
                pStmt.setString(15, "Card Connect");//TransactionType
                pStmt.setString(16, UserIP);
                pStmt.setString(17, "voidTransaction");
                pStmt.setString(18, UserId);
                pStmt.executeUpdate();
                pStmt.close();
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in Transaction Report ** (voidTransaction)", servletContext, Ex, "TransactionReportDateWise", "voidTransaction", conn);
                Services.DumException("TransactionReportDateWise", "voidTransaction -- Error 1 - Insertion Void Transactions Table ", request, Ex, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "TransactionReportDateWise");
                Parser.SetField("ActionID", "cardConnectGetInput");
                try {
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }

            if (Response[2].equals("Approval") || Response[2].equals("APPROVAL") ||
                    Response[2].equals("Success") || Response[2].equals("SUCCESS")) {

                Query = " Update " + databaseName + ".CardConnectResponses SET voidFlag = 1, voidDate = NOW(), UserIP = '" + UserIP + "',ActionID = 'voidTransaction' " +
                        "WHERE Id =  " + CardConnectIndx;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

                double TotalAmount = 0.0;
                double BalanceAmount = 0.0;
                double PaidAmount = 0.0;
                int Paid = 0;
                Object[] invoiceMaster = payments.getInvoiceMasterDetails(request, conn, servletContext, databaseName, InvoiceNo, String.valueOf(PatientMRN));
                PaidAmount = (double) invoiceMaster[0];
                BalanceAmount = (double) invoiceMaster[1];
                TotalAmount = (double) invoiceMaster[3];
/*                Query = "SELECT TotalAmount,BalAmount,Paid,PaidAmount FROM " + databaseName + ".InvoiceMaster " +
                        "WHERE PatientMRN = " + PatientMRN + " AND InvoiceNo = '" + InvoiceNo + "' AND Status = 0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    TotalAmount = rset.getDouble(1);
                    BalanceAmount = rset.getDouble(2);
                    Paid = rset.getInt(3);
                    PaidAmount = rset.getDouble(4);
                }
                rset.close();
                stmt.close();*/

                if (Paid == 1) {
                    if (PaidAmount == BalanceAmount) {
                        Paid = 0;
                    }
                }

                payments.insertInvoiceMasterHistory(request, conn, servletContext, databaseName, String.valueOf(PatientMRN), InvoiceNo, UserIP);

                try {
                    //payments.updateInvoiceMaster(request, conn, servletContext, databaseName, PaidAmount, CCAmount, BalanceAmount, Paid, PatientMRN, InvoiceNo);

                    Query = " Update " + databaseName + ".InvoiceMaster set PaidAmount = '" + nf.format(PaidAmount - Double.parseDouble(Amount)) + "', " +
                            "RefundFlag = 1 , BalAmount = '" + nf.format((BalanceAmount + Double.parseDouble(Amount))) + "', Paid = '" + Paid + "', RefundDateTime = now() " +
                            " WHERE PatientMRN = " + PatientMRN + " AND InvoiceNo = '" + InvoiceNo + "' AND Status = 0";
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                } catch (Exception Ex) {
                    helper.SendEmailWithAttachment("Error in Transaction Report ** (voidTransaction)", servletContext, Ex, "TransactionReportDateWise", "voidTransaction", conn);
                    Services.DumException("TransactionReportDateWise", "voidTransaction -- Error 2 - Updation ERROR RefundTransactions Table ", request, Ex, getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "TransactionReportDateWise");
                    Parser.SetField("ActionID", "cardConnectGetInput");
                    try {
                        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
                String receiptCounter = helper.getReceiptCounter(request, conn, servletContext, databaseName);
                PaidAmount = Double.parseDouble(nf.format(PaidAmount - Double.parseDouble(Amount)));
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + databaseName + ".PaymentReceiptInfo (PatientMRN,InvoiceNo,TotalAmount," +
                                    " PaidAmount,Paid,RefNo,Remarks,PayMethod,CreatedDate, BalAmount,CreatedBy,UserIP,ActionID,ReceiptNo) " +
                                    "VALUES (?,?,?,?,?,?,?,?,now(),?,?,?,?,?) ");
                    MainReceipt.setInt(1, PatientMRN);
                    MainReceipt.setString(2, InvoiceNo);
                    MainReceipt.setDouble(3, TotalAmount);
                    MainReceipt.setDouble(4, -PaidAmount);
                    MainReceipt.setInt(5, Paid);
                    MainReceipt.setString(6, InvoiceNo);
                    MainReceipt.setString(7, "Amount Voided");
                    MainReceipt.setString(8, "1");//CreditCard
                    MainReceipt.setDouble(9, (BalanceAmount + Double.parseDouble(Amount)));
                    MainReceipt.setString(10, UserId);
                    MainReceipt.setString(11, UserIP);
                    MainReceipt.setString(12, "Credit Card voidTransaction");
                    MainReceipt.setString(13, receiptCounter);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception Ex) {
                    helper.SendEmailWithAttachment("Error in Transaction Report ** (voidTransaction)", servletContext, Ex, "TransactionReportDateWise", "voidTransaction", conn);
                    Services.DumException("TransactionReportDateWise", "voidTransaction -- Error 3- Insertion PaymentReceiptInfo Table ", request, Ex, getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "TransactionReportDateWise");
                    Parser.SetField("ActionID", "cardConnectGetInput");
                    try {
                        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }

                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Your Payment has been voided!!");
                Parser.SetField("FormName", "TransactionReportDateWise");
                Parser.SetField("ActionID", "cardConnectGetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
            } else {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", Response[2]);
                Parser.SetField("FormName", "TransactionReportDateWise");
                Parser.SetField("ActionID", "cardConnectGetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/PaymentMessage.html");
            }
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Transaction Report ** (voidTransaction)", servletContext, Ex, "TransactionReportDateWise", "voidTransaction", conn);
            Services.DumException("TransactionReportDateWise", "voidTransaction -- Error 4 - Exception in Void Transaction ", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "TransactionReportDateWise");
            Parser.SetField("ActionID", "cardConnectGetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
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

    private void voidTransactionBolt(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, UtilityHelper helper, int facilityIndex, String databaseName, Payments payments) {
        stmt = null;
        rset = null;
        Query = "";
        pStmt = null;
        //Type Flag
        // 0 - Sandbox
        //int FlagType = 0;
        // 1 - Dev
        //int FlagType = 1;
        // 2 - Prod
        int FlagType = 2;
        int BoltIdx = Integer.parseInt(request.getParameter("BoltIdx").trim());
        int PatientMRN = 0;
        String ENDPOINT = "";
        String USERNAME = "";
        String PASSWORD = "";
        String MerchantId = "";
        String Currency = "";
        String Amount = "";
        String RetRef = "";
        String InvoiceNo = "";
        String JSON_Response = "";
        NumberFormat nf = new DecimalFormat("##.00");
        String UserIP = helper.getClientIp(request);

        try {
            Query = "SELECT InvoiceNo,PatientMRN,JSON_Response FROM " + databaseName + ".JSON_Response WHERE Id =" + BoltIdx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                InvoiceNo = rset.getString(1);
                PatientMRN = rset.getInt(2);
                JSON_Response = rset.getString(3);
            }
            rset.close();
            stmt.close();

            Query = "Select dbname, IFNULL(DirectoryName,'') from oe.clients where Id = " + facilityIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                databaseName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            JSONParser parser = new JSONParser();
            Object obj = parser.parse("[" + JSON_Response + "]");
            JSONArray array = (JSONArray) obj;
            JSONObject obj2 = (JSONObject) array.get(0);
            Amount = (String) obj2.get("amount");
            RetRef = (String) obj2.get("retref");

            String[] AuthConnect = helper.getAuthConnect(conn, FlagType, facilityIndex);
            ENDPOINT = AuthConnect[0];
            USERNAME = AuthConnect[1];
            PASSWORD = AuthConnect[2];
            MerchantId = AuthConnect[3];
            Currency = AuthConnect[4];

/*            out.println("ENDPOINT " + ENDPOINT + "<br>");
            out.println("USERNAME " + USERNAME + "<br>");
            out.println("PASSWORD " + PASSWORD + "<br>");
            out.println("MerchantId " + MerchantId + "<br>");
            out.println("Currency " + Currency + "<br>");*/

            CardConnectPayment cardConnectPayment = new CardConnectPayment();
            String[] Response = cardConnectPayment.voidTransaction(ENDPOINT, USERNAME, PASSWORD, MerchantId, Amount, Currency, RetRef);

/*            out.println("respproc " + Response[0] + "<br> ");
            out.println("amount " + Response[1] + "<br> ");
            out.println("resptext " + Response[2] + "<br> ");
            out.println("orderId " + Response[3] + "<br> ");
            out.println("receipt " + Response[4] + "<br> ");
            out.println("retref " + Response[5] + "<br> ");
            out.println("respstat " + Response[6] + "<br> ");
            out.println("respcode " + Response[7] + "<br> ");
            out.println("merchid " + Response[8] + "<br> ");*/

            try {
                pStmt = conn.prepareStatement(
                        "INSERT INTO " + databaseName + ".VoidTransactions (Amount, ResponseText, OrderId, ResponseCode, MerchantIndex, " +
                                "RespProc, Receipt,Currency,ResponseStatus,ReturnReference,Status,CreatedDate," +
                                "PatientMRN,InvoiceNo,OriginalRetRef,BoltIdx,TransactionType,UserIP,ActionID,CreatedBy) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?,?,?,?,?,?,?) ");
                pStmt.setString(1, Response[1]);
                pStmt.setString(2, Response[2]);
                pStmt.setString(3, Response[3]);
                pStmt.setString(4, Response[7]);//ResponseCode
                pStmt.setString(5, Response[8]);//MerchantId
                pStmt.setString(6, Response[0]);//ResponseProc
                pStmt.setString(7, Response[4]);//Receipt
                pStmt.setString(8, Currency);//Currency
                pStmt.setString(9, Response[6]);//ResponseStatus
                pStmt.setString(10, Response[5]);//ReturnReference
                pStmt.setInt(11, PatientMRN);//PatientMRN
                pStmt.setString(12, InvoiceNo);//InvoiceNo
                pStmt.setString(13, RetRef);//OriginalRetRef
                pStmt.setInt(14, BoltIdx);//CardConnectResponseIndex
                pStmt.setString(15, "Bolt");//TransactionType
                pStmt.setString(16, UserIP);
                pStmt.setString(17, "voidTransactionBolt");
                pStmt.setString(18, UserId);
                pStmt.executeUpdate();
                pStmt.close();
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in Transaction Report ** (voidTransactionBolt)", servletContext, Ex, "TransactionReportDateWise", "voidTransactionBolt", conn);
                Services.DumException("TransactionReportDateWise", "voidTransactionBolt -- Error 1 - Insertion Void Transactions Table", request, Ex, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "TransactionReportDateWise");
                Parser.SetField("ActionID", "boltGetInput");
                try {
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }

            if (Response[2].equals("Approval") || Response[2].equals("APPROVAL") ||
                    Response[2].equals("Success") || Response[2].equals("SUCCESS")) {

                Query = " Update " + databaseName + ".JSON_Response SET VoidFlag = 1, VoidDate = NOW(), UserIP = '" + UserIP + "'," +
                        " ActionID = 'voidTransactionBolt' " +
                        " WHERE Id =  " + BoltIdx;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

                double TotalAmount = 0.0;
                double BalanceAmount = 0.0;
                double PaidAmount = 0.0;
                int Paid = 0;

/*                Query = "SELECT TotalAmount,BalAmount,Paid,PaidAmount FROM " + databaseName + ".InvoiceMaster " +
                        " WHERE PatientMRN = " + PatientMRN + " AND InvoiceNo = '" + InvoiceNo + "' AND Status = 0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    TotalAmount = rset.getDouble(1);
                    BalanceAmount = rset.getDouble(2);
                    Paid = rset.getInt(3);
                    PaidAmount = rset.getDouble(4);
                }
                rset.close();
                stmt.close();*/
                Object[] invoiceMaster = payments.getInvoiceMasterDetails(request, conn, servletContext, databaseName, InvoiceNo, String.valueOf(PatientMRN));
                PaidAmount = (double) invoiceMaster[0];
                BalanceAmount = (double) invoiceMaster[1];
                Paid = (int) invoiceMaster[2];
                TotalAmount = (double) invoiceMaster[3];

                if (Paid == 1) {
                    if (PaidAmount == BalanceAmount) {
                        Paid = 0;
                    }
                }

                try {
                    Query = " Update " + databaseName + ".InvoiceMaster set PaidAmount = '" + nf.format(PaidAmount - Double.parseDouble(Amount)) + "', " +
                            "VoidFlag = 1 , BalAmount = '" + nf.format((BalanceAmount + Double.parseDouble(Amount))) + "', Paid = '" + Paid + "', VoidDateTime = now(), CreatedBy = '" + UserId + "' " +
                            "where PatientMRN = " + PatientMRN + " AND InvoiceNo = '" + InvoiceNo + "' AND Status = 0 ";
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                } catch (Exception Ex) {
                    helper.SendEmailWithAttachment("Error in Transaction Report ** (voidTransactionBolt)", servletContext, Ex, "TransactionReportDateWise", "voidTransactionBolt -- Error 2 - Updation ERROR RefundTransactions Table", conn);
                    Services.DumException("TransactionReportDateWise", "voidTransactionBolt -- Error 2 - Updation ERROR RefundTransactions Table", request, Ex, getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "TransactionReportDateWise");
                    Parser.SetField("ActionID", "boltGetInput");
                    try {
                        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }

                PaidAmount = Double.parseDouble(nf.format(PaidAmount - Double.parseDouble(Amount)));
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + databaseName + ".PaymentReceiptInfo (PatientMRN,InvoiceNo,TotalAmount," +
                                    " PaidAmount,Paid,RefNo,Remarks,PayMethod,CreatedDate, BalAmount) " +
                                    "VALUES (?,?,?,?,?,?,?,?,now(),?) ");
                    MainReceipt.setInt(1, PatientMRN);
                    MainReceipt.setString(2, InvoiceNo);
                    MainReceipt.setDouble(3, TotalAmount);
                    MainReceipt.setDouble(4, -PaidAmount);
                    MainReceipt.setInt(5, Paid);
                    MainReceipt.setString(6, InvoiceNo);
                    MainReceipt.setString(7, "Amount Voided");
                    MainReceipt.setString(8, "3");
                    MainReceipt.setDouble(9, (BalanceAmount + Double.parseDouble(Amount)));
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception Ex) {
                    helper.SendEmailWithAttachment("Error in Transaction Report ** (voidTransactionBolt)", servletContext, Ex, "TransactionReportDateWise", "voidTransactionBolt -- Error 3- Insertion PaymentReceiptInfo Table", conn);
                    Services.DumException("TransactionReportDateWise", "voidTransactionBolt -- Error 3- Insertion PaymentReceiptInfo Table", request, Ex, getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "TransactionReportDateWise");
                    Parser.SetField("ActionID", "boltGetInput");
                    try {
                        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }

                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Your Payment has been voided!!");
                Parser.SetField("FormName", "TransactionReportDateWise");
                Parser.SetField("ActionID", "boltGetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
            } else {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", Response[2]);
                Parser.SetField("FormName", "TransactionReportDateWise");
                Parser.SetField("ActionID", "boltGetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/PaymentMessage.html");
            }
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Transaction Report ** (voidTransactionBolt)", servletContext, Ex, "TransactionReportDateWise", "voidTransactionBolt -- Error 4 - Exception in voidTransaction...", conn);
            Services.DumException("TransactionReportDateWise", "voidTransactionBolt -- Error 4 - Exception in voidTransaction...", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "TransactionReportDateWise");
            Parser.SetField("ActionID", "boltGetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
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

}
