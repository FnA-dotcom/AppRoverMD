package md;

import DAL.Payments;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.DecimalFormat;

@SuppressWarnings("Duplicates")
public class CashPayments extends HttpServlet {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = null;
    private PreparedStatement pStmt = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        serviceHandling(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        serviceHandling(request, response);
    }

    public void serviceHandling(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                case "makeCashPayment":
                    cashPaymentSave(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
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
            helper.SendEmailWithAttachment("Error in Cash Payment ** (handleRequest)", context, Ex, "CashPayments", "handleRequest", conn);
            Services.DumException("CashPayments", "Handle Request", request, Ex, getServletContext());
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

    private void cashPaymentSave(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) {
        stmt = null;
        rset = null;
        Query = "";

        int PayMethod = Integer.parseInt(request.getParameter("PayMethod").trim());
        int InstallmentPlanFound = Integer.parseInt(request.getParameter("InstallmentPlanFound").trim());
        double CashAmount = Double.parseDouble(request.getParameter("CashAmount").trim().replaceAll(",", ""));
        String CashRefNo = request.getParameter("CashRefNo").trim();
        String CashRemarks = request.getParameter("CashRemarks").trim();
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
        String PatientMRN = request.getParameter("x0Y61008").trim();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int Paid = 0;
        int InstallmentPlanId = 0;
        double PaidAmount = 0.0D;
        double TotalAmount = 0.0D;
        double BalAmount = 0.0D;
        double BalAmount1 = 0.0D;
        String receipt;

        String facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        String[] PatientInfo = helper.getPatientInfo(request, conn, servletContext, database, PatientMRN);
        String FName = PatientInfo[0];
        String LName = PatientInfo[1];
        String Name = FName + " " + LName;
        try {
            if (PayMethod == 1 || PayMethod == 3) {
                out.println("11~Please select Cash Method!!");
/*                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Please select Cash Method!!");
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "PayNow&InvoiceNo='" + InvoiceNo + "'");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Error.html");*/
                return;
            }
            String UserIP = helper.getClientIp(request);
            Object[] invoiceMaster = payments.getInvoiceMasterDetails(request, conn, servletContext, database, InvoiceNo, PatientMRN);
            PaidAmount = (double) invoiceMaster[0];
            BalAmount = (double) invoiceMaster[1];
            TotalAmount = (double) invoiceMaster[3];

//            if (PaidAmount > TotalAmount) {
//                out.println("11~Paid Amount Cannot be greater than Total Amount!");
//                return;
//            }
            if (CashAmount > BalAmount) {
//                out.println("11~Amount should not be greater than Balance Due!");
                out.println("11~Total sum of amount paid (including this one) exceed the total price, which is not allowed.");
                return;
            }
            if (BalAmount == 0) {
                out.println("11~Customer has already payed the total price.\nTherefore, you cannot charge more than that.");
                return;
            }
            //PaidAmount = payments.getPaidAmount(request,conn,servletContext,database,InvoiceNo,PatientMRN);

            if (BalAmount == CashAmount)
                Paid = 1;

            payments.insertInvoiceMasterHistory(request, conn, servletContext, database, PatientMRN, InvoiceNo, UserIP);

            payments.updateInvoiceMaster(request, conn, servletContext, database, PaidAmount, CashAmount, BalAmount, Paid, PatientMRN, InvoiceNo);

            String receiptCounter = helper.getReceiptCounter(request, conn, servletContext, database);
            String printDate = "";
            String printTime = "";
            printDate = helper.printDateTime(request, conn, servletContext)[0];
            printTime = helper.printDateTime(request, conn, servletContext)[1];
            String FullName = "";
            String Address = "";
            String Phone = "";
            String[] RecieptCD = helper.receiptClientData(request, conn, servletContext, facilityIndex);
            FullName = RecieptCD[0];
            Address = RecieptCD[1];
            Phone = RecieptCD[2];
            Object[] invoiceMaster1 = payments.getInvoiceMasterDetails(request, conn, servletContext, database, InvoiceNo, PatientMRN);
            BalAmount1 = (double) invoiceMaster1[1];

            //            receipt= "<div id='printThis'><div id='mystyle' style='width: 377.68px;border: 1px; border-style: solid; border-color: black;'><div class='reciept' style='margin-top:20px;text-align: center;'><p style='margin:0px;'>"+mySplitResult[11]+"</p><p style='margin:0px;'>"+mySplitResult[12]+"</p><p style='margin:0px;'>"+mySplitResult[13]+"</p></div><div class='reciept' style='margin-left: 5px; text-align:left !important'><p style='margin:0px;'>Invoice #: "+mySplitResult[2]+"</p> <p style='margin:0px;'>Receipt #: "+mySplitResult[10]+"</p>   <p style='margin:0px;'>Status: "+mySplitResult[3]+"</p><p style='margin:0px;'>Balance : $"+mySplitResult[4]+"</p></div><div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>"+mySplitResult[7]+"</span> <span style='margin-left: 166px'>"+mySplitResult[8]+"</span></div><div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>Amount</span> <span style='margin-left: 199px'>$"+mySplitResult[5]+"</span></div><div class='reciept' style='margin-left: 5px;text-align:left !important'><p style='margin:0px;'>Method: "+mySplitResult[6]+"</p><p style='margin:0px;'>"+mySplitResult[9]+"</p></div><div class='reciept' style='text-align: center;'>Approved</div><div class='reciept' style='margin-bottom: 15px; text-align: center;'>Thank you. Please come again</div>  <div class='reciept' style='text-align: right;font-size:12px;'>Printed By Rover</div>    </div></div>"
            receipt = "<div id='printThis'><div id='mystyle' style='width: 377.68px;border: 1px; border-style: solid; border-color: black;'>" +
                    "<div class='reciept' style='margin-top:20px;text-align: center;'><p style='margin:0px;'>" + FullName + "</p><p style='margin:0px;'>" + Address + "</p><p style='margin:0px;'>" + Phone + "</p></div>" +
                    "<div class='reciept' style='margin-left: 5px; text-align:left !important'><p style='margin:0px;'>Invoice #: " + InvoiceNo + "</p> <p style='margin:0px;'>Receipt #: " + receiptCounter + "</p>   <p style='margin:0px;'>Status: Approval </p><p style='margin:0px;'>Balance : $" + BalAmount1 + "</p></div>" +
                    "<div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>" + printDate + "</span> <span style='margin-left: 166px'>" + printTime + "</span></div>" +
                    "<div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>Amount</span> <span style='margin-left: 199px'>$" + CashAmount + "</span></div>" +
                    "<div class='reciept' style='margin-left: 5px;text-align:left !important'><p style='margin:0px;'>Method: Cash </p><p style='margin:0px;'>" + Name + "</p></div>" +
                    "<div class='reciept' style='text-align: center;'>Approved</div><div class='reciept' style='margin-bottom: 15px; text-align: center;'>Thank you. Please come again</div>  <div class='reciept' style='text-align: right;font-size:12px;'>Printed By Rover</div>    </div></div>";


            payments.paymentReceiptInsertion(request, conn, servletContext, database, PatientMRN, InvoiceNo, TotalAmount, Paid, CashRefNo, CashRemarks, String.valueOf(PayMethod), BalAmount, userId, UserIP, "CashPayments", CashAmount, receiptCounter, receipt);

            payments.insertCashPayments(request, conn, servletContext, database, InvoiceNo, PatientMRN, userId, UserIP, "CashPayment", CashAmount, CashRefNo);

            if (InstallmentPlanFound > 0) {
                InstallmentPlanId = payments.getInstallmentIdx(request, conn, servletContext, database, PatientMRN, InvoiceNo);

                payments.updateInstallmentTable(request, conn, servletContext, database, PatientMRN, InvoiceNo, InstallmentPlanId);
            }


            //out.println("1~" + facilityName + "~" + InvoiceNo + "~Approval~" + BalAmount + "~" + CashAmount + "~Cash~" + printDate + "~" + printTime + "~" + Name + "~" + receiptCounter + "~" + FullName + "~" + Address + "~" + Phone);
            out.println("1~" + receipt);
        } catch (Exception Ex) {
            out.println("0");
            helper.SendEmailWithAttachment("Error in Cash Payment ^^ (Occurred At : " + facilityName + ") ** (GetInput)", servletContext, Ex, "CashPayments", "cashPaymentSave", conn);
            Services.DumException("CashPayments", "cashPaymentSave", request, Ex, getServletContext());
/*            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "PayNow&InvoiceNo='" + InvoiceNo + "'");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
        }
    }
}
