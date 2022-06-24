package md;

import DAL.Payments;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

@SuppressWarnings("Duplicates")
public class CloverServices13SEPT2021 extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private PreparedStatement pStmt = null;
    private Connection conn = null;
    private boltmaster bm = new boltmaster();
    private BoltClientProperties props;

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        requestHandling(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        requestHandling(request, response);
    }

    private void requestHandling(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        PrintWriter out = new PrintWriter(response.getOutputStream());
        UtilityHelper helper = new UtilityHelper();
        Payments payments = new Payments();
        String ServiceRequests = request.getParameter("ServiceRequests").trim();
        ServletContext context;
        context = getServletContext();

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

            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ServiceRequests) {
                case "getConnect":
                    getConnect(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper, payments);
                    break;
                case "DisconnectSession":
                    DisconnectSession(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper, payments);
                    break;
                case "performTransaction":
                    performTransaction(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper, payments);
                    break;
/*                case "GetDetails":
                    GetDetails(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper);
                    break;
                case "GetConnect":
                    GetConnect(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper);
                    break;
                case "GetTransaction":
                    GetTransaction(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper);
                    break;
                case "Disconnect_Stop":
                    Disconnect_Stop(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper);
                    break;*/
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
            helper.SendEmailWithAttachment("Error in CloverServices ** (handleRequest)", context, Ex, "CloverServices", "handleRequest", conn);
            Services.DumException("CloverServices", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
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

    private void getConnect(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int ClientId, UtilityHelper helper, Payments payments) throws FileNotFoundException {
        Query = "";
        stmt = null;
        rset = null;

        String DeviceS = request.getParameter("DeviceS").trim();
        String MRN = request.getParameter("x0Y61008").trim();
        String SessionKey = "";
        int Flag = 0;
        try {
            try {
                CheckActiveSession(conn, Database, UserId, ClientId, helper, request, out, servletContext, DeviceS, payments);
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in CloverServices ** (getConnect -- Check Active Session)", servletContext, Ex, "CloverServices", "getConnect", conn);
                Services.DumException("CloverServices", "getConnect", request, Ex, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "PayNow");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                out.flush();
                out.close();
            }

            try {
                //bm.init(DeviceS, ClientId, conn);
                //SessionKey = bm.validateConnection();
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in CloverServices ** (getConnect -- validate Connection)", servletContext, Ex, "CloverServices", "getConnect", conn);
                Services.DumException("CloverServices", "get Connect", request, Ex, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "PayNow");
                Parser.SetField("Message", "Connection is not validated. Please connect your device!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                out.flush();
                out.close();
            }

            String UserIP = helper.getClientIp(request);

            if (SessionKey.equals("") || SessionKey == null) {
                Flag = 10;
                String SessionResponse = "SessionKey header is required.It is null!";
                int ReturnValue = payments.boltFailures(request, conn, servletContext, Database, DeviceS, SessionResponse, UserId, UserIP, Flag);
            } else if (SessionKey.equals("Error")) {
                Flag = 0;
                String SessionResponse = "SessionKey header is required. Error appears not null!";
                int ReturnValue = payments.boltFailures(request, conn, servletContext, Database, DeviceS, SessionResponse, UserId, UserIP, Flag);
            } else {
                int ReturnValue = payments.activeSessionBolt(request, conn, servletContext, Database, DeviceS, SessionKey, UserId);
                Flag = 1;
            }
            out.println(Flag + "~" + DeviceS);
        } catch (Exception Ex) {
            try {
                CheckActiveSession(conn, Database, UserId, ClientId, helper, request, out, servletContext, DeviceS, payments);
                //bm.stop();
            } catch (Exception er) {
            }
            //helper.SendEmail("Main Section", "Error in Transaction Report", "Message Body");
            helper.SendEmailWithAttachment("Error in CloverServices ** (getConnect -- Main Catch )", servletContext, Ex, "CloverServices", "get Connect", conn);
            Services.DumException("CloverServices", "get Connect", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "PayNow");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
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

    private void CheckActiveSession(Connection conn, String Database, String UserId, int ClientId, UtilityHelper helper, HttpServletRequest request, PrintWriter out, ServletContext servletContext, String DeviceId, Payments payments) throws IOException {
        int FoundSession = 0;
        Query = "";
        stmt = null;
        rset = null;
        try {
            //Changed query - 26 MARCH 2021
            // FoundSession = payments.checkActiveSessionBolt(request, conn, servletContext, Database, DeviceId);
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in CloverServices ** (CheckActiveSession --Error 1- Getting Data from Active Session Bolt Table)", servletContext, Ex, "CloverServices", "CheckActiveSession", conn);
            Services.DumException("CloverServices", "Check Active Session", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "PayNow");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
            return;
        }
        if (FoundSession > 0) {
            try {
                //26-March-2021
                //Dont need to truncate table. Just need to delete the row on behalf of device id
                payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in CloverServices ** (CheckActiveSession -- Error 2- Updating/Truncate Table Active Session Bolt Table)", servletContext, Ex, "BoltPayServices", "CheckActiveSession", conn);
                Services.DumException("CloverServices", "Get Details", request, Ex, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "PayNow");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                out.flush();
                out.close();
            }
        }
    }

    private void DisconnectSession(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int ClientId, UtilityHelper helper, Payments payments) throws IOException {
        try {
            String oiPyRe3Q = request.getParameter("oiPyRe3Q").trim();
            CheckActiveSession(conn, Database, UserId, ClientId, helper, request, out, servletContext, oiPyRe3Q, payments);
            // bm.stop(context);
            out.println("100");
        } catch (Exception e) {
        }
    }

    private void performTransaction(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int facilityIndex, UtilityHelper helper, Payments payments) throws IOException, ParseException {
        String InvoiceNo = "";
        String PatientMRN = "";
        int Paid = 0;
        double TotalAmount = 0.0;
        double PaidAmount = 0.0;
        double BalAmount = 0.0;
        int InstallmentPlanId = 0;
        String UserIP = "";
        String facilityName = "";
        int PayRecIdx = 0;

        PatientMRN = request.getParameter("x0Y61008").trim();
        InvoiceNo = request.getParameter("InvoiceNo").trim();
        String BoltAmount = request.getParameter("pUxQ210Ol").trim().replaceAll(",", "");
        String boltDescription = request.getParameter("boltDescription").trim().replaceAll(",", "");
        String DeviceList = request.getParameter("DeviceList").trim().replaceAll(",", "");
        int InstallmentPlanFound = Integer.parseInt(request.getParameter("InstallmentCount").trim());

        //Should take card no (TOKEN NO)

        if (BoltAmount.contains(".")) {
            String parts[] = BoltAmount.split("\\.");
            if (parts[1].length() == 1) {
                parts[1] = parts[1] + "0";
            } else if (parts[1].length() > 2) {
                parts[1] = parts[1].substring(0, 2);
            }
            if (parts[0].equals("0")) {
                BoltAmount = parts[1];
            } else {
                BoltAmount = parts[0] + parts[1];
            }
        } else {
            BoltAmount = BoltAmount + "00";
        }

        String AuthCardRe = "";
        JSONParser parser = new JSONParser();
        Object obj = null;
        facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        try {
            int checkCredentials = payments.checkBoltCredentials(request, conn, facilityIndex, servletContext);
            if (checkCredentials == 0) {
                out.println("11~No Device Listed! Please contact System Administrator.");
                return;
            }

            UserIP = helper.getClientIp(request);
            Object[] invoiceMaster = payments.getInvoiceMasterDetails(request, conn, servletContext, Database, InvoiceNo, PatientMRN);
            PaidAmount = (double) invoiceMaster[0];
            BalAmount = (double) invoiceMaster[1];
            TotalAmount = (double) invoiceMaster[3];

/*            if (Double.parseDouble(BoltAmount) > BalAmount) {
                out.println("11~Amount should not be greater than Balance Due!");
                return;
            }*/
            if (BalAmount == 0) {
                out.println("11~No Balance amount left to pay!");
                return;
            }

            //AuthCardRe = bm.authCard(BoltAmount);
            //System.out.println("AuthCard " + AuthCardRe);
            obj = parser.parse("[" + AuthCardRe + "]");
            JSONArray array = (JSONArray) obj;
            JSONObject obj2 = (JSONObject) array.get(0);
            String token = (String) obj2.get("token");
            String expiry = (String) obj2.get("expiry");
            String name = (String) obj2.get("name");
            String batchid = (String) obj2.get("batchid");
            String retref = (String) obj2.get("retref");
            String avsresp = (String) obj2.get("avsresp");
            String respproc = (String) obj2.get("respproc");
            String amount = (String) obj2.get("amount");
            String resptext = (String) obj2.get("resptext");
            String authcode = (String) obj2.get("authcode");
            String respcode = (String) obj2.get("respcode");
            String merchid = (String) obj2.get("merchid");
            String cvvresp = (String) obj2.get("cvvresp");
            String respstat = (String) obj2.get("respstat");
            String emvTagData = (String) obj2.get("emvTagData");
            String orderid = (String) obj2.get("orderid");
            String entrymode = (String) obj2.get("entrymode");
            String bintype = (String) obj2.get("bintype");
/*            System.out.println("token " + token + " <br>");
            System.out.println("expiry " + expiry + " <br>");
            System.out.println("name " + name + " <br>");
            System.out.println("batchid " + batchid + " <br>");
            System.out.println("retref " + retref + " <br>");
            System.out.println("avsresp " + avsresp + " <br>");
            System.out.println("respproc " + respproc + " <br>");
            System.out.println("resptext " + resptext + " <br>");
            System.out.println("authcode " + authcode + " <br>");
            System.out.println("respcode " + respcode + " <br>");
            System.out.println("merchid " + merchid + " <br>");
            System.out.println("cvvresp " + cvvresp + " <br>");
            System.out.println("emvTagData " + emvTagData + " <br>");
            System.out.println("orderid " + orderid + " <br>");
            System.out.println("respstat " + respstat + " <br>");
            System.out.println("entrymode " + entrymode + " <br>");
            System.out.println("bintype " + bintype + " <br>");*/

//            System.out.println("BOLT amount " + amount);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);

            JsonNode jsonNode = objectMapper.readTree(AuthCardRe);
            String respCode = null;
//            String DateTime = null;
            String ApprovedAmount = null;
//            String CardAddress1 = null;
//            String CardAddress2 = null;
//            String CardPhone = null;
//            String CardName = null;
//            JsonNode receiptData = null;
            if (jsonNode.has("resptext")) {
                resptext = jsonNode.get("resptext").toString();
                respCode = jsonNode.get("respcode").toString();
                resptext = resptext.substring(1, resptext.length() - 1);
                respCode = respCode.substring(1, respCode.length() - 1);

                if (resptext.equals("Approval") && respCode.equals("000")) {
                    ApprovedAmount = jsonNode.get("amount").toString();
                    ApprovedAmount = ApprovedAmount.substring(1, ApprovedAmount.length() - 1);

                    if (BalAmount == Double.parseDouble(ApprovedAmount)) {
                        Paid = 1;
                    }
                    //System.out.println("BOLT ApprovedAmount " + ApprovedAmount);

                    payments.insertInvoiceMasterHistory(request, conn, servletContext, Database, PatientMRN, InvoiceNo, UserIP);

                    payments.updateInvoiceMaster(request, conn, servletContext, Database, PaidAmount, Double.parseDouble(ApprovedAmount), BalAmount, Paid, PatientMRN, InvoiceNo);

                    String receiptCounter = helper.getReceiptCounter(request, conn, servletContext, Database);
                    payments.paymentReceiptInsertion(request, conn, servletContext, Database, PatientMRN, InvoiceNo, TotalAmount, Paid, "BOLT", "BOLT PAYMENT", "3", BalAmount, UserId, UserIP, "CloverServices-PerformTransaction", Double.parseDouble(ApprovedAmount), receiptCounter, "Print From Device ** BOLT");

                    if (InstallmentPlanFound > 0) {
                        InstallmentPlanId = payments.getInstallmentIdx(request, conn, servletContext, Database, PatientMRN, InvoiceNo);

                        payments.updateInstallmentTable(request, conn, servletContext, Database, PatientMRN, InvoiceNo, InstallmentPlanId);
                    }
                    PayRecIdx = payments.getPaymentReceiptIndex(request, conn, servletContext, Database);
                    payments.insertionJSONResponseBolt(request, conn, servletContext, Database, InvoiceNo, PatientMRN, AuthCardRe, UserId, UserIP, "BoltPayServices ** performTransaction", Double.parseDouble(ApprovedAmount), boltDescription, DeviceList, PayRecIdx, "SUCCESS");

                    out.println("success~" + resptext + "~" + retref);
                } else {
                    //Error or Declined will treated After This
                    payments.insertionJSONResponseBolt(request, conn, servletContext, Database, InvoiceNo, PatientMRN, AuthCardRe, UserId, UserIP, "BoltPayServices ** performTransaction", 0, boltDescription, DeviceList, 0, "ERROR");
                    out.println("09~" + resptext);//Transaction Declined.
                }
            } else {
                out.println("jsonvalidatefail~" + resptext);//Transaction Declined.
            }

        } catch (Exception Ex) {
            payments.boltFailures(request, conn, servletContext, Database, DeviceList, Ex.getMessage(), UserId, UserIP, 9);
            out.println("09~" + Ex.getMessage());//Transaction Declined.
            helper.SendEmailWithAttachment("Error in CloverServices ^^ (Occurred At : " + facilityName + ") ** (performTransaction -- Error 01 - Exception ERROR in perform Transaction method)", servletContext, Ex, "CloverServices", "performTransaction", conn);
            Services.DumException("CloverServices", "perform Transaction", request, Ex, getServletContext());
/*            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "PayNow&InvoiceNo='" + InvoiceNo + "'");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");*/
            out.flush();
            out.close();
        }
    }

    /*public static void getrequestdetails(HttpServletRequest request,Exception ee) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        String context = request.getContextPath();
        String servlet = request.getServletPath();
        String info = request.getPathInfo();
        try {
            Enumeration<String> enum_1 = request.getParameterNames();
            while (enum_1.hasMoreElements()) {
                String name = (String) enum_1.nextElement();
                String values[] = request.getParameterValues(name);
                if (values != null) {
                    for (int i = 0; i < values.length; i++) {
                        //pushLogs(name + " (" + i + "): " + values[i],ee);
                    }
                }
            }
        }catch(Exception eee) {
        }
    }*/

    private void insertionBoltResponse(String Database, int facilityIndex, int advocateIdx, int Priority, String PtName, int PtMRN, String PtPhNumber, String Sms, String Username, int status) {
        PreparedStatement MainReceipt = null;
        try {
            MainReceipt = conn.prepareStatement(
                    "INSERT INTO oe_2.BoltResponseWithoutPerformingActions(BoltResponse, MRN, InvoiceNo, " +
                            "AmountFromWebPage, DescriptionFromWebPage, DeviceList, InstallementPlan, " +
                            "CreatedDate, Status, CreatedBy) " +
                            "VALUES ()");
        } catch (Exception ex) {
            System.out.println("EXCEPTION in Saving Record" + ex.getMessage());
        }
    }
}