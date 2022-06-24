//
// Decompiled by Procyon v0.5.36
//

package orange_2;

import Parsehtm.Parsehtm;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.net.ssl.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

//import com.itextpdf.text.pdf.PdfWriter;

public class OnePay extends HttpServlet {
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
            while (rset.next()) {
                ClientId = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "SELECT dbname FROM oe.clients WHERE Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();


            if (ActionID.equals("PaymentPortalInput")) {
                this.PaymentPortalInput(request, out, conn, context, response, UserId, Database, ClientId);
            } else if (ActionID.equals("PaymentProcessingAndSave")) {
                this.PaymentProcessingAndSave(request, out, conn, context, response, UserId, Database, ClientId);
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

    void PaymentPortalInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        String Query = "";
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
        String PatientMRN = "";
        double TotalAmount = 0.0;
        double PaidAmount = 0.0;
        double BalAmount = 0.0;
        String FirstName = "";
        String LastName = "";
        String Address = "";
        String City = "";
        String State = "";
        String ZipCode = "";
        String Country = "";
        String PhNumber = "";
        String Email = "";

        try {
            Query = "Select a.PatientMRN, b.FirstName, b.LastName, a.TotalAmount, a.PaidAmount, a.BalAmount, IFNULL(b.Email,''), b.City, b.State, b.ZipCode, b.Country, b.PhNumber, b.Address  from " + Database + ".InvoiceMaster a LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN where a.InvoiceNo = '" + InvoiceNo + "'";
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

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientMRN", String.valueOf(PatientMRN));
            Parser.SetField("InvoiceNo", String.valueOf(InvoiceNo));
            Parser.SetField("FirstName", String.valueOf(FirstName));
            Parser.SetField("LastName", String.valueOf(LastName));
            Parser.SetField("TotalAmount", String.valueOf(TotalAmount));
            Parser.SetField("PaidAmount", String.valueOf(PaidAmount));
            Parser.SetField("BalAmount", String.valueOf(BalAmount));
            Parser.SetField("Email", String.valueOf(Email));
            Parser.SetField("City", String.valueOf(City));
            Parser.SetField("State", String.valueOf(State));
            Parser.SetField("ZipCode", String.valueOf(ZipCode));
            Parser.SetField("Country", String.valueOf(Country));
            Parser.SetField("PhNumber", String.valueOf(PhNumber));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.SetField("Address", String.valueOf(Address));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/OnlinePaymentPortal.html");

        } catch (Exception var11) {
            out.println(var11.getMessage());
            out.flush();
            out.close();
        }
    }

    void PaymentProcessingAndSave(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String InvoiceNo = "";
        String PatientMRN = "";
        int Paid = 0;
        HashMap<String, Object> responseJSON = new HashMap();
        HashMap<String, Object> CardInfo = new HashMap();
        HashMap<String, Object> CustomerInfo = new HashMap();
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        double TotalAmount = 0.0;
        double PaidAmount = 0.0;
        double BalAmount = 0.0;
        String FirstName = "";
        String LastName = "";
        String Address = "";
        String City = "";
        String State = "";
        String ZipCode = "";
        String Country = "";
        String PhNumber = "";
        String Email = "";
        double Amount = 0.0;
        String PayMethod = "";
        String CardExpiry = "";
        String CardNum = "";
        int i = 0;
        String CardExpMonth = "";
        String CardExpYear = "";
        String CodeCVV = "";
        String Request = "";
        try {
            long unixTime = System.currentTimeMillis() / 1000L;
            PatientMRN = request.getParameter("PatientMRN").trim();
            InvoiceNo = request.getParameter("InvoiceNo").trim();
            FirstName = request.getParameter("FirstName").trim();
            LastName = request.getParameter("LastName").trim();
            TotalAmount = Double.parseDouble(request.getParameter("TotalAmount").trim());
            PaidAmount = Double.parseDouble(request.getParameter("PaidAmount").trim());
            BalAmount = Double.parseDouble(request.getParameter("BalAmount").trim());
            Amount = Double.parseDouble(request.getParameter("Amount").trim());
            City = request.getParameter("City").trim();
            State = request.getParameter("State").trim();
            ZipCode = request.getParameter("ZipCode").trim();
            Country = request.getParameter("Country").trim();
            Email = request.getParameter("Email").trim();
            PhNumber = request.getParameter("PhNumber").trim();
            PayMethod = request.getParameter("PayMethod").trim();
            CardNum = request.getParameter("CardNum").trim();
            CardExpiry = request.getParameter("CardExpiry").trim();
            CodeCVV = request.getParameter("CodeCVV").trim();
            Address = request.getParameter("Address").trim();

//      out.println("CodeCVV: "+CodeCVV);
            if (CardExpiry != null) {
                String[] CardExpiryMMYY = CardExpiry.split("\\-");
                CardExpMonth = CardExpiryMMYY[1].trim();
                CardExpYear = CardExpiryMMYY[0].trim().substring(2, 4);
            }
            String market_code = URLEncoder.encode("M");
            PayMethod = URLEncoder.encode(PayMethod);
            Address = URLEncoder.encode(Address);

            //JSONObject CardInfo = new JSONObject();
            CardInfo.put("number", CardNum);
            CardInfo.put("expiration_date", CardExpMonth + CardExpYear);
            CardInfo.put("code", CodeCVV);


//      JSONObject CustomerInfo = new JSONObject();
            CustomerInfo.put("first_name", "Oscar");//FirstName
            CustomerInfo.put("last_name", "Brady");//LastName
            CustomerInfo.put("street_1", "55 Airport Drive");//address
            CustomerInfo.put("street_2", "");
            CustomerInfo.put("city", "Pensacola");//City
            CustomerInfo.put("state", "FL");//State
            CustomerInfo.put("zip", "32503");//ZipCode
            CustomerInfo.put("country", "UnitedStates");//Country
            CustomerInfo.put("phone_number", "2025550197");//PhNumber
            CustomerInfo.put("company", "");
            CustomerInfo.put("customer_id", "AUTO");
            CustomerInfo.put("invoice_number", "null");
            CustomerInfo.put("email", "oscar@brady.com");//Email
            CustomerInfo.put("email_receipt", "YES");
            CustomerInfo.put("notes", "Sample Notes");
            CustomerInfo.put("action_code", "1");

            responseJSON.put("market_code", market_code);
            responseJSON.put("amount", Amount);
            responseJSON.put("method", PayMethod);
            responseJSON.put("type", "2");
            responseJSON.put("nonce", unixTime);//{{$timestamp}}
            responseJSON.put("client_ip", "106.51.74.202");
            responseJSON.put("card", CardInfo);
            responseJSON.put("Customer", CustomerInfo);

            Request = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseJSON);


            //out.println(Request);

            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }

            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            String BaseURL = "";
            BaseURL = "https://api.onepay.com/Transaction/";

            URL url2 = new URL(BaseURL);
            HttpURLConnection con = (HttpURLConnection) url2.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("x-authorization", "39328380-1e95-e57a-655e-84660b531c29");

            con.setDoOutput(true);
            con.getOutputStream().write(Request.getBytes("UTF-8"));
            con.getInputStream();

            String result1;
            String reply = "";
            BufferedInputStream bis1 = new BufferedInputStream(con.getInputStream());
            ByteArrayOutputStream buf1 = new ByteArrayOutputStream();
            int result3 = bis1.read();
            while (result3 != -1) {
                buf1.write((byte) result3);
                result3 = bis1.read();
            }
            result1 = buf1.toString();

            reply = StringEscapeUtils.unescapeJava(result1);
            reply = reply.substring(1, reply.length() - 1);

            //out.println(reply);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);

            JsonNode jsonNode = objectMapper.readTree(reply);

            JsonNode transaction_response = null;
            JsonNode customer_response = null;
            String trresult_text = "";
            String trresult_sub_code = "";
            String trtransaction_datetime = "";
            String tramount = "";
            String trapproved_amount = "";
            String trtransaction_id = "";
            String crresult_text = "";
            String crresult_sub_code = "";
            String crtransaction_datetime = "";
            String trDateTime = "";

            if (jsonNode.has("transaction_response")) {
                System.out.println("||||||||||||||||");
                transaction_response = jsonNode.get("transaction_response");
                //System.out.println(transaction_response);
                trresult_text = transaction_response.get("result_text").toString().replaceAll("\"", "");
                trresult_sub_code = transaction_response.get("result_sub_code").toString().replaceAll("\"", "");
                trtransaction_id = transaction_response.get("transaction_id").toString().replaceAll("\"", "");
                trtransaction_datetime = transaction_response.get("transaction_datetime").toString().replaceAll("\"", "");
                tramount = transaction_response.get("amount").toString().replaceAll("\"", "");
                trapproved_amount = transaction_response.get("approved_amount").toString().replaceAll("\"", "");
                System.out.println("Transaction Response" + trresult_text + trresult_sub_code + trtransaction_id + trtransaction_datetime + tramount + trapproved_amount);

            }

            if (trresult_sub_code.equals("000")) {
                if (jsonNode.has("customer_response")) {
                    System.out.println("||||||||||||||||");
                    customer_response = jsonNode.get("customer_response");
                    //System.out.println(transaction_response);
                    crresult_text = customer_response.get("result_text").toString().replaceAll("\"", "");
                    crresult_sub_code = customer_response.get("result_sub_code").toString().replaceAll("\"", "");
                    crtransaction_datetime = customer_response.get("transaction_datetime").toString().replaceAll("\"", "");
                    System.out.println("customer Response" + crresult_text + crresult_sub_code + crtransaction_datetime);

                    String Date = trtransaction_datetime.substring(0, 2) + "-" + trtransaction_datetime.substring(3, 5) + "-" + trtransaction_datetime.substring(6, 8);
                    String Time = trtransaction_datetime.substring(9, 11) + ":" + trtransaction_datetime.substring(11, 13) + ":" + trtransaction_datetime.substring(13, 15);
                    trDateTime = Date + " " + Time;

                    if ((trresult_sub_code.equals("000") && crresult_sub_code.equals("000"))) {

                        if (String.valueOf(BalAmount).equals(trapproved_amount)) {
                            Paid = 1;
                        }
                        Query = " Update " + Database + ".InvoiceMaster set PaidAmount = '" + (PaidAmount + Double.parseDouble(trapproved_amount)) + "', BalAmount = '" + (BalAmount - Double.parseDouble(trapproved_amount)) + "', Paid = '" + Paid + "', PaymentDateTime = '" + trDateTime + "' where InvoiceNo = '" + InvoiceNo + "'";
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();

                        try {
                            final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".PaymentReceiptInfo (PatientMRN,InvoiceNo,TotalAmount," + " PaidAmount,Paid,RefNo,Remarks,PayMethod,CreatedDate, BalAmount) \nVALUES (?,?,?,?,?,?,?,?,now(),?) ");
                            MainReceipt.setString(1, PatientMRN);
                            MainReceipt.setString(2, InvoiceNo);
                            MainReceipt.setDouble(3, TotalAmount);
                            MainReceipt.setDouble(4, Double.parseDouble(trapproved_amount));
                            MainReceipt.setInt(5, Paid);
                            MainReceipt.setString(6, InvoiceNo);
                            MainReceipt.setString(7, "Credit Card Payment");
                            MainReceipt.setString(8, String.valueOf(PayMethod));
                            MainReceipt.setDouble(9, (BalAmount - Double.parseDouble(trapproved_amount)));
                            MainReceipt.executeUpdate();
                            MainReceipt.close();
                        } catch (Exception e) {
                            out.println("Error 6- Insertion PaymentReceiptInfo Table :" + e.getMessage());
                            return;
                        }


                        final Parsehtm Parser = new Parsehtm(request);
                        Parser.SetField("Message", String.valueOf("Transaction Performed Success :" + crresult_text));
                        Parser.SetField("FormName", String.valueOf("RegisteredPatients_old"));
                        Parser.SetField("ActionID", String.valueOf("CollectPayment_View&PatientInvoice=" + PatientMRN));
                        Parser.SetField("UserId", String.valueOf(UserId));
                        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/SuccessCC.html");

                    }

                }

            } else {
                //out.println("Transaction Declined Due to : "+trresult_text);
                final Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", String.valueOf("Transaction Declined Due to : " + trresult_text));
                Parser.SetField("FormName", String.valueOf("RegisteredPatients_old"));
                Parser.SetField("ActionID", String.valueOf("CollectPayment_View&PatientInvoice=" + PatientMRN));
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ErrorCC.html");
            }


        } catch (Exception var11) {
            out.println("Error: " + var11.getMessage());
            String str = "";
            for (i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
    }


    public void download_direct(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        final String FileName = request.getParameter("fname");
        final String path = request.getParameter("path");
        final String RecordingPath = String.valueOf(path) + FileName;
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "Inline;filename=" + FileName);
            final FileInputStream fin = new FileInputStream(RecordingPath);
            final byte[] content = new byte[fin.available()];
            fin.read(content);
            fin.close();
            final OutputStream os = (OutputStream) response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }
}
