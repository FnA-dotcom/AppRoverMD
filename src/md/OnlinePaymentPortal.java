//
// Decompiled by Procyon v0.5.36
//

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import PaymentIntegrations.CardConnectPayment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.security.Key;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;

//import com.itextpdf.text.pdf.PdfWriter;

@SuppressWarnings("Duplicates")
public class OnlinePaymentPortal extends HttpServlet {
    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[]{'T', '#', '3', 'B', '3', '$', 'T',
                    '$', '3', 'C', 'r', '3', 't', 'K', '3', 'Q'};
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private Connection conn = null;

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

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void handleRequestOLD(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        ResultSet rset = null;
        Statement stmt = null;
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        String Query = "";
        int ClientId = 0;
        String Database = "";
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();

        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);

        Cookie[] cookies = request.getCookies();
        Zone = UserId = Passwd = "";
        int checkCookie = 0;
        for (int coky = 0; coky < cookies.length; coky++) {
            String cName = cookies[coky].getName();
            String cValue = cookies[coky].getValue();
            if (cName.equals("UserId")) {
                UserId = cValue;
            }
        }

        try {
            Query = "SELECT ClientId FROM oe.sysusers WHERE ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientId = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "SELECT dbname FROM oe.clients WHERE Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();

            if (ActionID.equals("PaymentPortalInput")) {
                this.PaymentPortalInput(request, out, conn, context, response, UserId, Database, ClientId);
            } else if (ActionID.equals("PaymentProcessingAndSave")) {
                this.PaymentProcessingAndSave(request, out, conn, context, response, UserId, Database, ClientId);
            } else if (ActionID.equals("IgnorePCICompliance")) {
                this.IgnorePCICompliance(request, out, conn, context);
            } else {
                out.println("Under Development");
            }
            try {
                conn.close();
            } catch (Exception ex) {
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            out.println("Error in Handling:" + e.getMessage());
        }
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ActionID;

        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        String DatabaseName;
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

            switch (ActionID) {
                case "PaymentPortalInput":
                    this.PaymentPortalInput(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
                    break;
                case "PaymentProcessingAndSave":
                    this.PaymentProcessingAndSave(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
                    break;
                case "IgnorePCICompliance":
                    this.IgnorePCICompliance(request, out, conn, context);
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

    private void IgnorePCICompliance(HttpServletRequest req, PrintWriter out, Connection conn, ServletContext context) {
        String InvoiceNo = req.getParameter("InvoiceNo").trim();

        try {
            Parsehtm Parser = new Parsehtm(req);
            Parser.SetField("InvoiceNo", InvoiceNo);
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/IgnorePCI.html");

        } catch (Exception Ex) {
            out.println("Exception Message : " + Ex.getMessage());
            out.close();
            out.flush();
            return;
        }
    }

    private void PaymentPortalInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId) {
        //out.println("IN METHOD !! ");

        Statement stmt = null;
        ResultSet rset = null;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuilder InstallementPlan = new StringBuilder();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        String Query = "";
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
//        String MyToken = request.getParameter("mytoken").trim();
//        String expiry = request.getParameter("expiry").trim();
//        String cvv2 = request.getParameter("cvv2").trim();
        //String AmountCC = request.getParameter("AmountCC").trim();

        String PatientMRN = "";
        double TotalAmount = 0.0;
        double PaidAmount = 0.0;
        double BalAmount = 0.0;
        double AmountToPay = 0.0;
        String FirstName = "";
        String LastName = "";
        String Address = "";
        String City = "";
        String State = "";
        String ZipCode = "";
        String Country = "";
        String PhNumber = "";
        String Email = "";
        NumberFormat nf = new DecimalFormat("##.00");
        int SNo = 0;
        try {
/*            if (MyToken.equals(null) || MyToken.isEmpty()) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Please Enter Credit Card number!!");
                Parser.SetField("FormName", "OnlinePaymentPortal");
                Parser.SetField("ActionID", "IgnorePCICompliance&InvoiceNo='" + InvoiceNo + "'");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Error.html");
                return;
            }*/
            Query = "Select a.PatientMRN, b.FirstName, b.LastName, a.TotalAmount, a.PaidAmount, a.BalAmount, " +
                    "IFNULL(b.Email,''), b.City, b.State, b.ZipCode, b.Country, b.PhNumber, b.Address  " +
                    "from " + Database + ".InvoiceMaster a " +
                    "LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN " +
                    "where a.InvoiceNo = '" + InvoiceNo + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientMRN = rset.getString(1);
                FirstName = rset.getString(2);
                LastName = rset.getString(3);
                TotalAmount = rset.getDouble(4);
                PaidAmount = rset.getDouble(4) - rset.getDouble(6);
                BalAmount = rset.getDouble(6);
                Email = rset.getString(7);
                City = rset.getString(8);
                State = rset.getString(9);
                ZipCode = rset.getString(10);
                Country = rset.getString(11);
                PhNumber = rset.getString(12);
                Address = rset.getString(13);
            }
            rset.close();
            stmt.close();

            int InstallmentPlanFound = 0;
            Query = "Select COUNT(*) from " + Database + ".InstallmentPlan where MRN = '" + PatientMRN + "' and InvoiceNo = '" + InvoiceNo + "' and status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                InstallmentPlanFound = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (InstallmentPlanFound > 0) {
                String PatientName = FirstName + "" + LastName;
                Query = "Select IFNULL(MRN,''), IFNULL(InvoiceNo,''), IFNULL(PaymentAmount,0), IFNULL(DATE_FORMAT(PaymentDate,'%m/%d/%Y'),''), " +
                        " CASE WHEN PAID = 0 THEN 'Pending' WHEN Paid = 1 THEN 'Paid' ELSE 'Pending' END from " + Database + ".InstallmentPlan where MRN = '" + PatientMRN + "' and " +
                        " InvoiceNo = '" + InvoiceNo + "' and status = 0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    InstallementPlan.append("<tr><td align=left>" + SNo + "</td>\n");
                    InstallementPlan.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    InstallementPlan.append("<td align=left>" + PatientName + "</td>\n");
                    InstallementPlan.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    InstallementPlan.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    InstallementPlan.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    InstallementPlan.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    SNo++;
                }
                rset.close();
                stmt.close();

                Query = " Select IFNULL(PaymentAmount,0) from " + Database + ".InstallmentPlan where MRN = '" + PatientMRN + "' and " +
                        " InvoiceNo = '" + InvoiceNo + "' and Paid = 0 limit 1 ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    AmountToPay = rset.getDouble(1);
                }
                rset.close();
                stmt.close();
            }

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientMRN", String.valueOf(PatientMRN));
            Parser.SetField("InvoiceNo", String.valueOf(InvoiceNo));
            Parser.SetField("FirstName", String.valueOf(FirstName));
            Parser.SetField("LastName", String.valueOf(LastName));
            Parser.SetField("TotalAmount", String.valueOf(nf.format(TotalAmount)));
            Parser.SetField("PaidAmount", String.valueOf(nf.format(PaidAmount)));
            Parser.SetField("BalAmount", String.valueOf(nf.format(BalAmount)));
            Parser.SetField("Email", String.valueOf(Email));
            Parser.SetField("City", String.valueOf(City));
            Parser.SetField("State", String.valueOf(State));
            Parser.SetField("ZipCode", String.valueOf(ZipCode));
            Parser.SetField("Country", String.valueOf(Country));
            Parser.SetField("PhNumber", String.valueOf(PhNumber));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.SetField("Address", String.valueOf(Address));
//			Parser.SetField("Address", String.valueOf(Address+" "+City+" "+State+" "+ZipCode));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
//            Parser.SetField("myToken", MyToken);
//            Parser.SetField("myExpiryDate", expiry);
            Parser.SetField("InstallementPlan", InstallementPlan.toString());
            Parser.SetField("AmountToPay", String.valueOf(AmountToPay));
            Parser.SetField("InstallmentPlanFound", String.valueOf(InstallmentPlanFound));
//            Parser.SetField("cvv2", cvv2);
            //Parser.SetField("AmountCC", AmountCC);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/OnlinePaymentPortal.html");

        } catch (Exception var11) {
            out.println("ERROR MESSAGE " + var11.getMessage());
            out.flush();
            out.close();
        }
    }

    private void PaymentProcessingAndSave(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, int ClientId) {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String PatientMRN = "";
        String InvoiceNo = "";
        String FirstName = "";
        String LastName = "";
        String Address = "";
        String City = "";
        String State = "";
        String ZipCode = "";
        String Country = "";
        String CVV2 = "";
        String ResponseType = "";
        String AmountCC = "";
        String Description = "";

        double BalAmount;
        double PaidAmount;
        double TotalAmount;

        double Amount = 0.0;
        String myToken = "";
        String myExpiryDate = "";
        int InstallmentPlanFound;
        NumberFormat nf = new DecimalFormat("##.00");
        try {

            if (request.getParameter("PatientMRN") == null) {
                PatientMRN = "";
            } else {
                PatientMRN = request.getParameter("PatientMRN").trim();
            }
            if (request.getParameter("InvoiceNo") == null) {
                InvoiceNo = "";
            } else {
                InvoiceNo = request.getParameter("InvoiceNo").trim();
            }
            if (request.getParameter("FirstName") == null) {
                FirstName = "";
            } else {
                FirstName = request.getParameter("FirstName").trim();
            }
            if (request.getParameter("LastName") == null) {
                LastName = "";
            } else {
                LastName = request.getParameter("LastName").trim();
            }
            if (request.getParameter("City") == null) {
                City = "";
            } else {
                City = request.getParameter("City").trim();
            }
            if (request.getParameter("State") == null) {
                State = "";
            } else {
                State = request.getParameter("State").trim();
            }
            if (request.getParameter("ZipCode") == null) {
                ZipCode = "";
            } else {
                ZipCode = request.getParameter("ZipCode").trim();
            }
            if (request.getParameter("Country") == null) {
                Country = "";
            } else {
                Country = request.getParameter("Country").trim();
            }
            if (request.getParameter("Address") == null) {
                Address = "";
            } else {
                Address = request.getParameter("Address").trim();
            }
            if (request.getParameter("BalAmount") == null) {
                BalAmount = 0.0;
            } else {
                BalAmount = Double.parseDouble(request.getParameter("BalAmount").trim());
            }
            if (request.getParameter("PaidAmount") == null) {
                PaidAmount = 0.0;
            } else {
                PaidAmount = Double.parseDouble(request.getParameter("PaidAmount").trim());
            }
            if (request.getParameter("TotalAmount") == null) {
                TotalAmount = 0.0;
            } else {
                TotalAmount = Double.parseDouble(request.getParameter("TotalAmount").trim());
            }

            //*************** CARD DETAIL ***************************
            if (request.getParameter("Amount") == null) {
                Amount = 0.0;
            } else {
                Amount = Double.parseDouble(request.getParameter("Amount").trim());
            }
            if (request.getParameter("mytoken") == null) {
                myToken = "";
            } else {
                myToken = request.getParameter("mytoken").trim();
            }
            if (request.getParameter("myExpiryDate") == null) {
                myExpiryDate = "";
            } else {
                myExpiryDate = request.getParameter("myExpiryDate").trim();
            }
            if (request.getParameter("InstallmentPlanFound") == null) {
                InstallmentPlanFound = 0;
            } else {
                InstallmentPlanFound = Integer.parseInt(request.getParameter("InstallmentPlanFound").trim());
            }
            if (request.getParameter("Description") == null) {
                Description = "";
            } else {
                Description = request.getParameter("Description").trim();
            }
            /*if (request.getParameter("CardNum") == null) {
                CardNum = "";
            } else {
                CardNum = request.getParameter("CardNum").trim();
            }
            if (request.getParameter("CardExpiry") == null) {
                CardExpiry = "";
            } else {
                CardExpiry = request.getParameter("CardExpiry").trim();
            }*/
            if (request.getParameter("CVC") == null) {
                CVV2 = "";
            } else {
                CVV2 = request.getParameter("CVC").trim();
            }
/*            if (request.getParameter("AmountCC") == null) {
                AmountCC = "";
            } else {
                AmountCC = request.getParameter("AmountCC").trim();
            }*/

/*
            out.println("CVV2 " + CVV2 + "<br>");
            out.println("myExpiryDate " + myExpiryDate + "<br>");
            out.println("myToken " + myToken + "<br>");
            out.println("Description " + Description + "<br>");
            out.println("InstallmentPlanFound " + InstallmentPlanFound + "<br>");
            out.println("InvoiceNo " + InvoiceNo + "<br>");
*/

            myExpiryDate = myExpiryDate.replace("/", "");
            String Values = FirstName + "^" + LastName + "^" + City + "^" + State + "^" + ZipCode + "^" + Country + "^" + Address + "^" + Amount + "^" + myToken + "^" + myExpiryDate + "^" + CVV2;
            Values = encrypt(Values.trim());
            Values = Values.replace(" ", "");
//            out.println("Values " + Values + "<br>");

            CardConnectPayment cardConnectPayment = new CardConnectPayment();
            String[] Response = cardConnectPayment.InquireTransaction(Values, ClientId, conn);

/*            out.println("ResponseText " + Response[0] + "<br> ");
            out.println("cvvresp " + Response[1] + "<br> ");
            out.println("respcode " + Response[2] + "<br> ");
            out.println("entrymode " + Response[3] + "<br> ");
            out.println("authcode " + Response[4] + "<br> ");
            out.println("respproc " + Response[5] + "<br> ");
            out.println("respstat " + Response[6] + "<br> ");
            out.println("retref " + Response[7] + "<br> ");
            out.println("expiry " + Response[8] + "<br> ");
            out.println("AVS " + Response[9] + "<br> ");
            out.println("Receipt " + Response[10] + "<br> ");
            out.println("BinType " + Response[11] + "<br> ");
            out.println("Amount " + Response[12]+ "<br>");
            out.println("AccountNo " + Response[13]+ "<br>");
            out.println("orderId " + Response[14]+ "<br>");
            out.println("commCard " + Response[15]+ "<br>");*/

            //out.println("Amount " + Double.parseDouble(Response[12]) + "<br>");
            JSONParser parser = new JSONParser();
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
            String items = (String) obj2.get("items");
/*            out.println("<br> ************************************************** <br> ");
            out.println("dateTime " + dateTime + "<br> ");
            out.println("dba " + dba + "<br> ");
            out.println("address2 " + address2 + "<br> ");
            out.println("phone " + phone + "<br> ");
            out.println("footer " + footer + "<br> ");
            out.println("nameOnCard " + nameOnCard + "<br> ");
            out.println("address1 " + address1 + "<br> ");
            out.println("orderNote " + orderNote + "<br> ");
            out.println("header " + header + "<br> ");
            out.println("items " + items + "<br> ");
            out.println("<br> ************************************************** <br> ");*/


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Your Payment has been credited!!");
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.SetField("dateTime", dateTime);
            Parser.SetField("Address", address1 + " " + address2);
            Parser.SetField("Name", nameOnCard);
            Parser.SetField("Facility", dba);
            Parser.SetField("Phone", phone);
            Parser.SetField("CardNo", Response[13]);
            Parser.SetField("Amount", Response[12]);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/CCPaymentMessage.html");
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReceiptDisplay.html");
            UtilityHelper helper = new UtilityHelper();
            String UserIP = helper.getClientIp(request);
/*
            if (Response[0].equals("Approval") || Response[0].equals("APPROVAL") ||
                    Response[0].equals("Success") || Response[0].equals("SUCCESS")) {
                int Paid = 0;
                if (Amount == BalAmount) {
                    Paid = 1;
                }

                Query = " Update " + Database + ".InvoiceMaster set PaidAmount = '" + nf.format(PaidAmount + Amount) + "', " +
                        "BalAmount = '" + nf.format((BalAmount - Amount)) + "', Paid = '" + Paid + "', PaymentDateTime = now() " +
                        "where InvoiceNo = '" + InvoiceNo + "'";
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + Database + ".PaymentReceiptInfo (PatientMRN,InvoiceNo,TotalAmount," +
                                    " PaidAmount,Paid,RefNo,Remarks,PayMethod,CreatedDate, BalAmount) " +
                                    "VALUES (?,?,?,?,?,?,?,?,now(),?) ");
                    MainReceipt.setString(1, PatientMRN);
                    MainReceipt.setString(2, InvoiceNo);
                    MainReceipt.setDouble(3, TotalAmount);
                    MainReceipt.setDouble(4, Amount);
                    MainReceipt.setInt(5, Paid);
                    MainReceipt.setString(6, InvoiceNo);
                    MainReceipt.setString(7, Description);
                    MainReceipt.setString(8, "1");
                    MainReceipt.setDouble(9, (BalAmount - Amount));
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception e) {
                    out.println("Error 6- Insertion PaymentReceiptInfo Table :" + e.getMessage());
                    return;
                }

                if (InstallmentPlanFound > 0) {
                    int InstallmentPlanId = 0;
                    try {
                        Query = "Select Id from " + Database + ".InstallmentPlan " +
                                " where Paid = 0 and InvoiceNo = '" + InvoiceNo + "' limit 1";
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next()) {
                            InstallmentPlanId = rset.getInt(1);
                        }
                        rset.close();
                        stmt.close();

                        if (InstallmentPlanId > 0) {
                            Query = " Update " + Database + ".InstallmentPlan set Paid = 1 where " +
                                    " Id = " + InstallmentPlanId + "";
                            stmt = conn.createStatement();
                            stmt.executeUpdate(Query);
                            stmt.close();

                        }
                    } catch (Exception e) {
                        out.println("Error in Updating Installment plan Table: " + e.getMessage());
                    }
                }
                ResponseType = "SUCCESS";
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO oe.CardConnectResponses(ResponseText,CVVResponse,ResponseCode,EntryMode," +
                                    "AuthCode,ResponseProc,ResponseStatus,RetRef,Expiry,AVS,CreatedDate,Status," +
                                    "PatientMRN,InvoiceNo,ClientIndex, Token, RetRefDate, CVV2, BinType, Reciept, " +
                                    "ResponseType,Remarks, Amount, AccountNo,orderId,commCard,UserIP,ActionID) " +
                                    "VALUES (?,?,?,?,?,?,?,?,?,?,NOW(),0,?,?,?,?, NOW(),?,?,?,?,?,?,?,?,?,?,'PaymentProcessingAndSave') ");
                    MainReceipt.setString(1, Response[0]);
                    MainReceipt.setString(2, Response[1]);
                    MainReceipt.setString(3, Response[2]);
                    MainReceipt.setString(4, Response[3]);
                    MainReceipt.setString(5, Response[4]);
                    MainReceipt.setString(6, Response[5]);
                    MainReceipt.setString(7, Response[6]);
                    MainReceipt.setString(8, Response[7]);
                    MainReceipt.setString(9, Response[8]);
                    MainReceipt.setString(10, Response[9]);
                    MainReceipt.setString(11, PatientMRN);
                    MainReceipt.setString(12, InvoiceNo);
                    MainReceipt.setInt(13, ClientId);
                    MainReceipt.setString(14, myToken);
                    MainReceipt.setString(15, CVV2);
                    MainReceipt.setString(16, Response[11]);
                    MainReceipt.setString(17, Response[10]);
                    MainReceipt.setString(18, ResponseType);
                    MainReceipt.setString(19, Description);
                    MainReceipt.setDouble(20, Double.parseDouble(Response[12]));
                    MainReceipt.setString(21, Response[13]);
                    MainReceipt.setString(22, Response[14]);
                    MainReceipt.setString(23, Response[15]);
                    MainReceipt.setString(24, UserIP);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                } catch (Exception e) {
                    out.println("Error 3- :" + e.getMessage());
                }

                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Your Payment has been credited!!");
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "CollectPayment");
                Parser.SetField("dateTime", dateTime);
                Parser.SetField("Address", address1 + " " + address2);
                Parser.SetField("Name", nameOnCard);
                Parser.SetField("Facility", dba);
                Parser.SetField("Phone", phone);
                Parser.SetField("CardNo", Response[13]);
                Parser.SetField("Amount", Response[12]);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/CCPaymentMessage.html");

*/
/*                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Your Payment has been credited!!");
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "CollectPayment");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");*//*


            } else {
                ResponseType = "ERROR";
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO oe.CardConnectResponses(ResponseText,CVVResponse,ResponseCode,EntryMode," +
                                    "AuthCode,ResponseProc,ResponseStatus,RetRef,Expiry,AVS,CreatedDate,Status," +
                                    "PatientMRN,InvoiceNo,ClientIndex, Token, RetRefDate, CVV2, BinType, Reciept, " +
                                    "ResponseType,Remarks, Amount, AccountNo,orderId,commCard,UserIP,ActionID) " +
                                    "VALUES (?,?,?,?,?,?,?,?,?,?,NOW(),0,?,?,?,?, NOW(),?,?,?,?,?,?,?,?,?,?,'PaymentProcessingAndSave') ");
                    MainReceipt.setString(1, Response[0]);
                    MainReceipt.setString(2, Response[1]);
                    MainReceipt.setString(3, Response[2]);
                    MainReceipt.setString(4, Response[3]);
                    MainReceipt.setString(5, Response[4]);
                    MainReceipt.setString(6, Response[5]);
                    MainReceipt.setString(7, Response[6]);
                    MainReceipt.setString(8, Response[7]);
                    MainReceipt.setString(9, Response[8]);
                    MainReceipt.setString(10, Response[9]);
                    MainReceipt.setString(11, PatientMRN);
                    MainReceipt.setString(12, InvoiceNo);
                    MainReceipt.setInt(13, ClientId);
                    MainReceipt.setString(14, myToken);
                    MainReceipt.setString(15, CVV2);
                    MainReceipt.setString(16, Response[11]);
                    MainReceipt.setString(17, Response[10]);
                    MainReceipt.setString(18, ResponseType);
                    MainReceipt.setString(19, Description);
                    MainReceipt.setDouble(20, Double.parseDouble(Response[12]));
                    MainReceipt.setString(21, Response[13]);
                    MainReceipt.setString(22, Response[14]);
                    MainReceipt.setString(23, Response[15]);
                    MainReceipt.setString(24, UserIP);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                } catch (Exception e) {
                    out.println("Error 3- :" + e.getMessage());
                }
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", Response[0]);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "CollectPayment");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Error.html");
            }
*/

        } catch (Exception e) {
            out.println("Exception in Online Payment Portal " + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + " ~~~~~~~~~~~~~!!!!!!!!!!!!!!!! <br>";
            }
            out.println(str);
        }

    }

    public String encode(String url) {
        return URLEncoder.encode(url);
    }

}
