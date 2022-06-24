package md;

import DAL.Payments;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import PaymentIntegrations.CardConnectPayment;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
public class CardConnectServices extends HttpServlet {
    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[]{'T', '#', '3', 'B', '3', '$', 'T',
                    '$', '3', 'C', 'r', '3', 't', 'K', '3', 'Q'};
//    private Connection conn = null;
//    private Statement stmt = null;
//    private ResultSet rset = null;
//    private String Query = null;

    public static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        serviceHandling(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        serviceHandling(request, response);
    }

    private void serviceHandling(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                case "makeCardConnectPayment":
                    cardConnectPaymentSave(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "preAuthConnection":
                    makePreAuthConn(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
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
            helper.SendEmailWithAttachment("Error in Card Connect Payment ** (handleRequest)", context, Ex, "CardConnectServices", "handleRequest", conn);
            Services.DumException("CardConnectServices", "Handle Request", request, Ex, getServletContext());
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

    private void cardConnectPaymentSave(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) {

        double CCAmount = Double.parseDouble(request.getParameter("CCAmount").trim().replaceAll(",", ""));
        String CCExpiry = request.getParameter("CCExpiry").trim();
        String CCCVC = request.getParameter("CCCVC").trim();
        String CCnameCard = request.getParameter("CCnameCard").trim();
        String myToken = request.getParameter("mytoken").trim();
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
        String PatientMRN = request.getParameter("x0Y61008").trim();
        String Description = request.getParameter("Description").trim();
        int InstallmentPlanFound = Integer.parseInt(request.getParameter("InstallmentPlanFound").trim());

        int Paid = 0;
        int PayRecIdx = 0;
        int InstallmentPlanId = 0;
        double PaidAmount = 0.0D;
        double TotalAmount = 0.0D;
        double BalAmount = 0.0D;
        String ResponseType = "";
        String receipt = "";
        String facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        try {

            int checkCredentials = payments.checkCCCredentials(request, conn, facilityIndex, servletContext);
            if (checkCredentials == 0) {
                out.println("11~No account found. Please contact System Administrator.");
                return;
            }

/*            boolean addVerify = payments.addressVerification(request, conn, database, servletContext, Integer.parseInt(PatientMRN));
            System.out.println("addVerify " + addVerify);
            if (!addVerify) {
                out.println("12~Please complete the address details!");
                return;
            }*/

            String[] PatientInfo = helper.getPatientInfo(request, conn, servletContext, database, PatientMRN);
            String FName = PatientInfo[0];
            String LName = PatientInfo[1];
            String Name = FName + " " + LName;
            CCExpiry = CCExpiry.replace("/", "");
            String Values = PatientInfo[0] + "^" + PatientInfo[1] + "^" + PatientInfo[3] + "^" + PatientInfo[4] + "^" + PatientInfo[5] + "^" + PatientInfo[6] + "^" + PatientInfo[8] + "^" + CCAmount + "^" + myToken + "^" + CCExpiry + "^" + CCCVC;
            Values = encrypt(Values.trim());
            Values = Values.replace(" ", "");

            Object[] invoiceMaster = payments.getInvoiceMasterDetails(request, conn, servletContext, database, InvoiceNo, PatientMRN);
            PaidAmount = (double) invoiceMaster[0];
            BalAmount = (double) invoiceMaster[1];
            TotalAmount = (double) invoiceMaster[3];

/*            if (PaidAmount > TotalAmount) {
                out.println("11~Paid Amount Cannot be greater than Total Amount!");
                return;
            }*/
            if (CCAmount > BalAmount) {
                out.println("11~Amount should not be greater than Balance Due!");
                return;
            }
            if (BalAmount == 0) {
                out.println("11~No Balance amount left to pay!");
                return;
            }
            String receiptCounter = helper.getReceiptCounter(request, conn, servletContext, database);

            //String dateTime = "20210411232226";
            //dateTime = dateTime.substring(0, 4) + "-" + dateTime.substring(4, 6) + "-" + dateTime.substring(6, 8) + " " + dateTime.substring(8, 10) + ":" + dateTime.substring(10, 12) + ":00".replaceAll("\n", "");
            String printDate = "";
            String printTime = "";
            printDate = helper.printDateTime(request, conn, servletContext)[0];
            printTime = helper.printDateTime(request, conn, servletContext)[1];

/*            String FullName = "";
            String Address = "";
            String Phone = "";
            FullName = helper.receiptClientData(request, conn, servletContext, facilityIndex)[0];
            Address = helper.receiptClientData(request, conn, servletContext, facilityIndex)[1];
            Phone = helper.receiptClientData(request, conn, servletContext, facilityIndex)[2];*/

            //out.println("1~"+printDate+"~~West Orange, TX~Golden Triangle Emergency Center~4099204470~60XXXXXXXXXX0896~1.00~AsjadAzam~091814764910");
            // out.println("1~" + facilityName + "~" + InvoiceNo + "~Approval~" + BalAmount + "~" + CCAmount + "~Card~" + printDate + "~" + printTime + "~Asjad~" + receiptCounter + "~Frontline ER~Dallas, TX~2144999555~033234~106449012753~40XXXXXXXXXX0506~"+receiptCounter);

            CardConnectPayment cardConnectPayment = new CardConnectPayment();
            String[] Response = cardConnectPayment.InquireTransaction(Values, facilityIndex, conn);
            System.out.println("ResponseText " + Response[0] + "<br> ");
            System.out.println("cvvresp " + Response[1] + "<br> ");
            System.out.println("respcode " + Response[2] + "<br> ");
            System.out.println("entrymode " + Response[3] + "<br> ");
            System.out.println("authcode " + Response[4] + "<br> ");
            System.out.println("respproc " + Response[5] + "<br> ");
            System.out.println("respstat " + Response[6] + "<br> ");
            System.out.println("retref " + Response[7] + "<br> ");
            System.out.println("expiry " + Response[8] + "<br> ");
            System.out.println("AVS " + Response[9] + "<br> ");
            System.out.println("Receipt " + Response[10] + "<br> ");
            System.out.println("BinType " + Response[11] + "<br> ");
            System.out.println("Amount " + Response[12] + "<br>");
            System.out.println("AccountNo " + Response[13] + "<br>");
            System.out.println("orderId " + Response[14] + "<br>");
            System.out.println("commCard " + Response[15] + "<br>");

/*            JSONParser parser = new JSONParser();
            Object obj = parser.parse("[" + Response[10] + "]");
            JSONArray array = (JSONArray) obj;
            JSONObject obj2 = (JSONObject) array.get(0);
            String dateTime = (String) obj2.get("dateTime");
            String dba = (String) obj2.get("dba");
            String address2 = (String) obj2.get("address2");
            String phone = (String) obj2.get("phone");
            String footer = (String) obj2.get("footer");
            String nameOnCard = (String) obj2.get("nameOnCard");
            String address1 = (String) obj2.get("address1");
            String orderNote = (String) obj2.get("orderNote");
            String header = (String) obj2.get("header");
            String items = (String) obj2.get("items");*/
            String FullName = "";
            String Address = "";
            String Phone = "";
            FullName = helper.receiptClientData(request, conn, servletContext, facilityIndex)[0];
            Address = helper.receiptClientData(request, conn, servletContext, facilityIndex)[1];
            Phone = helper.receiptClientData(request, conn, servletContext, facilityIndex)[2];


            //out.println("1~"+dateTime+"~"+address1+"~"+address2+"~"+dba+"~"+phone+"~"+Response[13]+"~"+Response[12]+"~"+nameOnCard);
            //out.println("1~20210402100916~~West Orange, TX~Golden Triangle Emergency Center~4099204470~60XXXXXXXXXX0896~1.00~AsjadAzam~091814764910");

            String UserIP = helper.getClientIp(request);
            String CurrDate = helper.getCurrDate(request, conn);
            if (Response[0].equals("Approval") || Response[0].equals("APPROVAL") ||
                    Response[0].equals("Success") || Response[0].equals("SUCCESS")) {
                if (CCAmount == BalAmount) {
                    Paid = 1;
                }

                payments.insertInvoiceMasterHistory(request, conn, servletContext, database, PatientMRN, InvoiceNo, UserIP);

                payments.updateInvoiceMaster(request, conn, servletContext, database, PaidAmount, CCAmount, BalAmount, Paid, PatientMRN, InvoiceNo);

                receipt = "<div id='printThis'><div id='mystyle' style='width: 377.68px;border: 1px; border-style: solid; border-color: black;'><div class='reciept' style='margin-top:20px;text-align: center;'><p style='margin:0px;'>" + FullName + "</p><p style='margin:0px;'>" + Address + "</p><p style='margin:0px;'>" + Phone + "</p></div><div class='reciept' style='margin-left: 5px; text-align:left !important'><p style='margin:0px;'>Ref #: " + Response[7] + "</p><p style='margin:0px;'>Status: " + Response[0] + "</p><p style='margin:0px;'>Auth #: " + Response[4] + "</p><p style='margin:0px;'>MID: " + Response[13] + "</p><p style='margin:0px;'>Receipt#: " + receiptCounter + "</p></div><div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>" + printDate + "</span> <span style='margin-left: 166px'>" + printTime + "</span></div><div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>Amount</span> <span style='margin-left: 199px'>$" + CCAmount + "</span></div><div class='reciept' style='margin-left: 5px;text-align:left !important'><p style='margin:0px;'>Method: Card</p><p style='margin:0px;'>" + Name + "</p></div><div class='reciept' style='text-align: center;'>Approved</div><div class='reciept' style='margin-bottom: 15px; text-align: center;'>Thank you. Please come again</div> <div class='reciept' style='text-align: right;font-size:12px;'>Printed By Rover</div>  </div></div>";

                payments.paymentReceiptInsertion(request, conn, servletContext, database, PatientMRN, InvoiceNo, TotalAmount, Paid, InvoiceNo, Description, "1", BalAmount, userId, UserIP, "cardConnectPaymentSave", CCAmount, receiptCounter, receipt);

                if (InstallmentPlanFound > 0) {
                    InstallmentPlanId = payments.getInstallmentIdx(request, conn, servletContext, database, PatientMRN, InvoiceNo);

                    payments.updateInstallmentTable(request, conn, servletContext, database, PatientMRN, InvoiceNo, InstallmentPlanId);
                }

                PayRecIdx = payments.getPaymentReceiptIndex(request, conn, servletContext, database);
                ResponseType = "SUCCESS";
                payments.insertionCardConnectResponses(request, conn, servletContext, database, InvoiceNo, PatientMRN, Response[0], Response[1] == null ? "" : Response[1], Response[2] == null ? "" : Response[2], Response[3] == null ? "" : Response[3], Response[4] == null ? "" : Response[4], Response[5] == null ? "" : Response[5], Response[6] == null ? "" : Response[6], Response[7] == null ? "" : Response[7], Response[8] == null ? "" : Response[8], Response[9] == null ? "" : Response[9], CurrDate, 0, facilityIndex, myToken, CurrDate, CCCVC, Response[11] == null ? "" : Response[11], Response[10] == null ? "" : Response[10], ResponseType, Description, CCAmount, Response[13] == null ? "" : Response[13], Response[14] == null ? "" : Response[14], Response[15] == null ? "" : Response[15], UserIP, "cardConnectPaymentSave", CCnameCard, PayRecIdx, "2", "No Insurance", receipt, "No File", userId);

                //out.println("1~" + dateTime + "~" + address1 + "~" + address2 + "~" + dba + "~" + phone + "~" + Response[13] + "~" + Response[12] + "~" + nameOnCard + "~" + Response[7]);
                //out.println("1~" + facilityName + "~" + InvoiceNo + "~" + Response[0] + "~" + BalAmount + "~" + CCAmount + "~Card~" + printDate + "~" + printTime + "~" + Name + "~" + receiptCounter + "~" + FullName + "~" + Address + "~" + Phone + "~" + Response[4] + "~" + Response[7] + "~" + Response[13] + "~" + receiptCounter);

                out.println("1~" + receipt);
            } else {
                ResponseType = "ERROR";
                payments.insertionCardConnectResponses(request, conn, servletContext, database, InvoiceNo, PatientMRN, Response[0], Response[1] == null ? "" : Response[1], Response[2] == null ? "" : Response[2], Response[3] == null ? "" : Response[3], Response[4] == null ? "" : Response[4], Response[5] == null ? "" : Response[5], Response[6] == null ? "" : Response[6], Response[7] == null ? "" : Response[7], Response[8] == null ? "" : Response[8], Response[9] == null ? "" : Response[9], CurrDate, 0, facilityIndex, myToken, CurrDate, CCCVC, Response[11] == null ? "" : Response[11], Response[10] == null ? "" : Response[10], ResponseType, Description, CCAmount, Response[13] == null ? "" : Response[13], Response[14] == null ? "" : Response[14], Response[15] == null ? "" : Response[15], UserIP, "cardConnectPaymentSave", CCnameCard, PayRecIdx, "2", "No Insurance", "No Slip", "No File", userId);
                //payments.insertionCardConnectResponses(request, conn, servletContext, database, InvoiceNo, PatientMRN, Response[0], "", "", "", "", "", "", Response[7], Response[8], Response[9], CurrDate, 0, facilityIndex, myToken, CurrDate, CCCVC, Response[11], Response[10], ResponseType, Description, CCAmount, Response[13], Response[14], Response[15], UserIP, "cardConnectPaymentSave", CCnameCard, PayRecIdx, "2", "No Insurance", "No Slip", "No File");
                out.println("0~" + Response[0]);
            }
        } catch (Exception Ex) {
            out.println("0~Error while making payment.");
            helper.SendEmailWithAttachment("Error in CardConnect Services ^^ (Occurred At : " + facilityName + ") ** (cardConnectPaymentSave)", servletContext, Ex, "CardConnectServices", "cardConnectPaymentSave", conn);
            Services.DumException("CardConnectServices", "cardConnectPaymentSave", request, Ex, getServletContext());

/*            String Message = Ex.getMessage() + " <br> ";
            for (int i = 0; i < Ex.getStackTrace().length; ++i) {
                Message = Message + Ex.getStackTrace()[i] + " ******* <br>";
            }
            out.println(Message);*/
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

    private void makePreAuthConn(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;
    }


}
