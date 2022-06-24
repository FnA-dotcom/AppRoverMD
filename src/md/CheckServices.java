package md;

import DAL.Payments;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import PaymentIntegrations.CheckPayment;

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
import java.text.DecimalFormat;

@SuppressWarnings("Duplicates")
public class CheckServices extends HttpServlet {
/*
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = null;
    private PreparedStatement pStmt = null;*/

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
        Connection conn = null;
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
                case "initiateCheckPayment":
                    initiateCheckPayment(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
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
            helper.SendEmailWithAttachment("Error in Check Payment ** (handleRequest)", context, Ex, "CheckServices", "handleRequest", conn);
            Services.DumException("CheckServices", "Handle Request", request, Ex, getServletContext());
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

    private void initiateCheckPayment(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;

        String routing = request.getParameter("routing").trim();
        String accountNo = request.getParameter("accountNo").trim();
        String checkNumber = request.getParameter("checkNumber").trim();
        double checkAmount = Double.parseDouble(request.getParameter("checkAmount").trim().replaceAll(",", "").replaceAll("$", ""));
        String checkDescription = request.getParameter("checkDescription").trim();
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
        int PatientMRN = Integer.parseInt(request.getParameter("x0Y61008").trim());
        int PayMethod = Integer.parseInt(request.getParameter("PayMethod").trim());
        int InstallmentPlanFound = Integer.parseInt(request.getParameter("InstallmentPlanFound").trim());
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");

        int Paid = 0;
        int InstallmentPlanId = 0;
        double PaidAmount = 0.0D;
        double TotalAmount = 0.0D;
        double BalAmount = 0.0D;

        String facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);

        try {
            if (PayMethod == 1 || PayMethod == 3) {
                out.println("11~Please select Check Method!!");
                return;
            }
            String accountNo3 = routing + "/" + accountNo;
            CheckPayment checkPayment = new CheckPayment();
            String token = checkPayment.generateToken(facilityIndex, conn, accountNo3);

            String[] PatientInfo = helper.getPatientInfo(request, conn, servletContext, database, String.valueOf(PatientMRN));
            String FName = PatientInfo[0];
            String LName = PatientInfo[1];
            String Name = FName + " " + LName;
            String PatientAddress = PatientInfo[8];
            String City = PatientInfo[3];
            String State = PatientInfo[4];
            String Country = PatientInfo[6];
            String ZipCode = PatientInfo[5];

//            out.println("token " + token + "<br>");
            String Response[] = checkPayment.performCheckPaymentAuth(facilityIndex, conn, token, checkAmount, PatientMRN, Name, PatientAddress, City, State, Country, ZipCode, "WEB");
//            out.println("Auth Check " + RetRef + "<br>");
//            System.out.println("ResponseText " + Response[0] + "<br> ");
/*            System.out.println("Amount " + Response[1] + "<br> ");
            System.out.println("CardProc " + Response[2] + "<br> ");
            System.out.println("Commcard " + Response[3] + "<br> ");
            System.out.println("ResponseCode " + Response[4] + "<br> ");
            System.out.println("EntryMode " + Response[5] + "<br> ");
            System.out.println("Merchant " + Response[6] + "<br> ");
            System.out.println("ResponseToken " + Response[7] + "<br> ");
            System.out.println("RespProc " + Response[8] + "<br> ");
            System.out.println("BinType " + Response[9] + "<br> ");
            System.out.println("Expiry " + Response[10] + "<br> ");
            System.out.println("RetRef " + Response[11] + "<br> ");
            System.out.println("RespStat " + Response[12]+ "<br>");
            System.out.println("Account " + Response[13]+ "<br>");*/

            if (Response[0].equals("Approved") || Response[0].equals("APPROVED") ||
                    Response[0].equals("Success") || Response[0].equals("SUCCESS")) {
                String ResponseText = Response[0];
                String Amount = Response[1];
                String CardProc = Response[2];
                String Commcard = Response[3];
                String ResponseCode = Response[4];
                String EntryMode = Response[5];
                String Merchant = Response[6];
                String ResponseToken = Response[7];
                String RespProc = Response[8];
                String BinType = Response[9];
                String Expiry = Response[10];
                String RetRef = Response[11];
                String RespStat = Response[12];
                String Account = Response[13];

                String UserIP = helper.getClientIp(request);
                Object[] invoiceMaster = payments.getInvoiceMasterDetails(request, conn, servletContext, database, InvoiceNo, String.valueOf(PatientMRN));
                PaidAmount = (double) invoiceMaster[0];
                BalAmount = (double) invoiceMaster[1];
                TotalAmount = (double) invoiceMaster[3];

                if (PaidAmount > TotalAmount) {
                    out.println("11~Paid Amount Cannot be greater than Total Amount!");
                    return;
                }
                if (checkAmount > BalAmount) {
                    out.println("11~Amount should not be greater than Balance Due!");
                    return;
                }
                if (BalAmount == 0) {
                    out.println("11~No Balance amount left to pay!");
                    return;
                }
                if (InstallmentPlanFound > 0) {
                    InstallmentPlanId = payments.getInstallmentIdx(request, conn, servletContext, database, String.valueOf(PatientMRN), InvoiceNo);

                    payments.updateInstallmentTable(request, conn, servletContext, database, String.valueOf(PatientMRN), InvoiceNo, InstallmentPlanId);
                }

                payments.insertCheckInfo(request, conn, servletContext, database, PatientMRN, InvoiceNo, InstallmentPlanFound, routing,
                        accountNo, checkNumber, checkDescription, checkAmount, PayMethod, "CheckServices",
                        userId, UserIP, facilityIndex, RetRef, Response, "", "No Insurance", 0);

                String printDate = "";
                String printTime = "";
                printDate = helper.printDateTime(request, conn, servletContext)[0];
                printTime = helper.printDateTime(request, conn, servletContext)[1];

                String FullName = "";
                String Address = "";
                String Phone = "";
                FullName = helper.receiptClientData(request, conn, servletContext, facilityIndex)[0];
                Address = helper.receiptClientData(request, conn, servletContext, facilityIndex)[1];
                Phone = helper.receiptClientData(request, conn, servletContext, facilityIndex)[2];
                String receiptCounter = helper.getReceiptCounter(request, conn, servletContext, database);

                out.println("1~" + facilityName + "~" + InvoiceNo + "~Pending~" + BalAmount + "~" + checkAmount + "~Check~" + printDate + "~" + printTime + "~" + Name + "~" + receiptCounter + "~" + FullName + "~" + Address + "~" + Phone);
            } else if (Response.length > 1) {
                out.println("11~" + Response[0]);
            } else {
                out.println("11~Something went wrong. Please contact System Administrator.");
            }
        } catch (Exception Ex) {
            out.println("0");
            helper.SendEmailWithAttachment("Error in Check Payment ^^ (Occurred At : " + facilityName + ") ** (initiateCheckPayment)", servletContext, Ex, "CheckServices", "initiateCheckPayment", conn);
            Services.DumException("CheckServices", "initiateCheckPayment", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "PayNow&InvoiceNo='" + InvoiceNo + "'");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
