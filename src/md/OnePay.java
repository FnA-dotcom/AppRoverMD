//
// Decompiled by Procyon v0.5.36
//

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.net.ssl.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.sql.*;

//import com.itextpdf.text.pdf.PdfWriter;
@SuppressWarnings("Duplicates")
public class OnePay extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private Connection conn = null;

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
                out.println("Exception excp conn: " + excp.getMessage());
            }

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
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

    private void PaymentPortalInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId) {
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
            Parser.SetField("Address", String.valueOf(Address + " " + City + " " + State + " " + ZipCode));
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

    private void PaymentProcessingAndSave(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String InvoiceNo = "";
        String PatientMRN = "";
        int Paid = 0;
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
        String Amount = "00";
        String PayMethod = "";
        String CardExpiry = "";
        String CardNum = "";
        int i = 0;
        String CardExpMonth = "";
        String CardExpYear = "";
        String CodeCVV = "";
        String Description = "";
        String Request = "";
        String Request2 = "";
        try {
            //      long unixTime = System.currentTimeMillis() / 1000L;

            //      FirstName = request.getParameter("FirstName").trim();
            //      LastName = request.getParameter("LastName").trim();
            //      City = request.getParameter("City").trim();
            //      State = request.getParameter("State").trim();
            //      ZipCode = request.getParameter("ZipCode").trim();
            //      Country = request.getParameter("Country").trim();
            //      Email = request.getParameter("Email").trim();
            //      PhNumber = request.getParameter("PhNumber").trim();
            //      PayMethod = request.getParameter("PayMethod").trim();
            //    	Address = request.getParameter("Address").trim();
            PatientMRN = request.getParameter("PatientMRN").trim();
            InvoiceNo = request.getParameter("InvoiceNo").trim();
            TotalAmount = Double.parseDouble(request.getParameter("TotalAmount").trim());
            PaidAmount = Double.parseDouble(request.getParameter("PaidAmount").trim());
            BalAmount = Double.parseDouble(request.getParameter("BalAmount").trim());
            Amount = request.getParameter("Amount").trim();
            if (Amount.contains(".")) {
                String parts[] = Amount.split("\\.");
                if (parts[1].length() == 1) {
                    parts[1] = parts[1] + "0";
                } else if (parts[1].length() > 2) {
                    parts[1] = parts[1].substring(0, 2);
                }
                Amount = parts[0] + parts[1];

            } else {
                Amount = Amount + "00";
            }


            CardNum = request.getParameter("CardNum").trim();
            CardExpiry = request.getParameter("CardExpiry").trim();
            CodeCVV = request.getParameter("CodeCVV").trim();

            Description = request.getParameter("Description").trim();
            Description = URLEncoder.encode(Description, "UTF-8");
            //      out.println("CodeCVV: "+CodeCVV);

            String CardMonth = "";
            String CardYear = "";
            String parts2[] = CardExpiry.split("\\-");
            CardYear = parts2[0];
            CardMonth = parts2[1];

            //out.println(Request);

            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
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

            Request = "card[number]=" + CardNum + "&card[exp_month]=" + CardMonth + "&card[exp_year]=" + CardYear + "&card[cvc]=" + CodeCVV;
            String BaseURL = "";
            BaseURL = "https://api.stripe.com/v1/tokens";

            URL url2 = new URL(BaseURL);
            HttpURLConnection con = (HttpURLConnection) url2.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Authorization", "Bearer  sk_test_51HbXX1Emy3qYdmGduJ0lwd7TOcuGApDk5a9efinF9gFTl4k3iG5eqCn2MYO7qT5VXmZyli3Tty06U3PCrTaV3DFP004FUIqYuN");

            con.setDoOutput(true);
            con.getOutputStream().write(Request.getBytes("UTF-8"));
            con.getInputStream();

            String reply = "";
            BufferedInputStream bis1 = new BufferedInputStream(con.getInputStream());
            ByteArrayOutputStream buf1 = new ByteArrayOutputStream();
            int result3 = bis1.read();
            while (result3 != -1) {
                buf1.write((byte) result3);
                result3 = bis1.read();
            }
            reply = buf1.toString();

            //      out.println(reply);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
            //
            JsonNode jsonNode = objectMapper.readTree(reply);

            // { "id": "tok_1HmmuwEmy3qYdmGdlLzKiX2J", "object": "token", "card": { "id": "card_1HmmuwEmy3qYdmGdaEqlwOMS", "object": "card", "address_city": null, "address_country": null, "address_line1": null, "address_line1_check": null, "address_line2": null, "address_state": null, "address_zip": null, "address_zip_check": null, "brand": "Visa", "country": "US", "cvc_check": "unchecked", "dynamic_last4": null, "exp_month": 11, "exp_year": 2021, "fingerprint": "ZaBHkNL6fAk28W73", "funding": "credit", "last4": "4242", "metadata": { }, "name": null, "tokenization_method": null }, "client_ip": "54.167.174.84", "created": 1605214878, "livemode": false, "type": "card", "used": false }

            String Tokenid = "";
            String Object = "";
            if (jsonNode.has("object")) {
                Tokenid = jsonNode.get("id").toString();
                Tokenid = Tokenid.substring(1, Tokenid.length() - 1);
                System.out.println(Tokenid);


                TrustManager[] trustAllCerts1 = new TrustManager[]{new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }

                }};
                SSLContext sc1 = SSLContext.getInstance("SSL");
                sc1.init(null, trustAllCerts1, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc1.getSocketFactory());

                // Create all-trusting host name verifier
                HostnameVerifier allHostsValid1 = new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };
                // Install the all-trusting host verifier
                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid1);


                Request = "amount=" + Amount + "&currency=usd&description=" + Description + "&source=" + Tokenid;
                BaseURL = "https://api.stripe.com/v1/charges";

                url2 = new URL(BaseURL);
                con = (HttpURLConnection) url2.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Authorization", "Bearer  sk_test_51HbXX1Emy3qYdmGduJ0lwd7TOcuGApDk5a9efinF9gFTl4k3iG5eqCn2MYO7qT5VXmZyli3Tty06U3PCrTaV3DFP004FUIqYuN");

                con.setDoOutput(true);
                con.getOutputStream().write(Request.getBytes("UTF-8"));
                con.getInputStream();

                reply = "";
                bis1 = new BufferedInputStream(con.getInputStream());
                buf1 = new ByteArrayOutputStream();
                result3 = bis1.read();
                while (result3 != -1) {
                    buf1.write((byte) result3);
                    result3 = bis1.read();
                }
                reply = buf1.toString();

                //        out.println("reply2: "+reply);

                jsonNode = objectMapper.readTree(reply);
                String status = "";
                String amount = "";
                String amount_captured = "";
                String amount_refunded = "";

                if (jsonNode.has("status")) {
                    System.out.println("||||||||||||||||");
                    status = jsonNode.get("status").toString();
                    status = status.substring(1, status.length() - 1);
                    ;
                    System.out.println(status);
                    if (status.equals("succeeded")) {
                        amount = jsonNode.get("amount").toString();
                        amount_captured = jsonNode.get("amount_captured").toString();
                        amount_refunded = jsonNode.get("amount_refunded").toString();
                    }
                    amount = amount.substring(0, amount.length() - 2);
                    amount_captured = amount_captured.substring(0, amount_captured.length() - 2);
                    amount = amount + ".00";
                    amount_captured = amount_captured + ".00";

                    System.out.println(amount);
                    System.out.println(amount_captured);
                    System.out.println(amount_refunded);

                    if (Double.parseDouble(Amount) == BalAmount) {
                        Paid = 1;
                    }

                    Query = " Update " + Database + ".InvoiceMaster set PaidAmount = '" + (PaidAmount + Double.parseDouble(amount_captured)) + "', BalAmount = '" + (BalAmount - Double.parseDouble(amount_captured)) + "', Paid = '" + Paid + "', PaymentDateTime = now() where InvoiceNo = '" + InvoiceNo + "'";
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();

                    try {
                        final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".PaymentReceiptInfo (PatientMRN,InvoiceNo,TotalAmount," + " PaidAmount,Paid,RefNo,Remarks,PayMethod,CreatedDate, BalAmount) \nVALUES (?,?,?,?,?,?,?,?,now(),?) ");
                        MainReceipt.setString(1, PatientMRN);
                        MainReceipt.setString(2, InvoiceNo);
                        MainReceipt.setDouble(3, TotalAmount);
                        MainReceipt.setDouble(4, Double.parseDouble(amount_captured));
                        MainReceipt.setInt(5, Paid);
                        MainReceipt.setString(6, InvoiceNo);
                        MainReceipt.setString(7, "Credit Card Payment");
                        MainReceipt.setString(8, String.valueOf(PayMethod));
                        MainReceipt.setDouble(9, (BalAmount - Double.parseDouble(amount_captured)));
                        MainReceipt.executeUpdate();
                        MainReceipt.close();
                    } catch (Exception e) {
                        out.println("Error 6- Insertion PaymentReceiptInfo Table :" + e.getMessage());
                        return;
                    }


                    final Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Message", String.valueOf("Transaction Performed Success :" + status));
                    Parser.SetField("FormName", String.valueOf("RegisteredPatients"));
                    Parser.SetField("ActionID", String.valueOf("CollectPayment_View&PatientInvoice=" + PatientMRN));
                    Parser.SetField("UserId", String.valueOf(UserId));
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/SuccessCC.html");


                }

            } else {
                out.println("Error in getting card Information!! or May be Card Information is not correct");
            }

            //      if(trresult_sub_code.equals("000")) {
            //        if (jsonNode.has("customer_response")) {
            //          System.out.println("||||||||||||||||");
            //          customer_response = jsonNode.get("customer_response");
            //          //System.out.println(transaction_response);
            //          crresult_text = customer_response.get("result_text").toString().replaceAll("\"", "");
            //          crresult_sub_code = customer_response.get("result_sub_code").toString().replaceAll("\"", "");
            //          crtransaction_datetime = customer_response.get("transaction_datetime").toString().replaceAll("\"", "");
            //          System.out.println("customer Response" + crresult_text + crresult_sub_code + crtransaction_datetime);
            //
            //          String Date = trtransaction_datetime.substring(0,2) + "-" + trtransaction_datetime.substring(3,5) + "-" + trtransaction_datetime.substring(6,8);
            //          String Time = trtransaction_datetime.substring(9,11) + ":" + trtransaction_datetime.substring(11,13) + ":" + trtransaction_datetime.substring(13,15);
            //          trDateTime = Date + " " + Time;
            //
            //          if((trresult_sub_code.equals("000") && crresult_sub_code.equals("000"))){
            //
            //            if(String.valueOf(BalAmount).equals(trapproved_amount)){
            //              Paid = 1;
            //            }
            //            Query = " Update " + Database + ".InvoiceMaster set PaidAmount = '" + (PaidAmount + Double.parseDouble(trapproved_amount)) + "', BalAmount = '" + (BalAmount - Double.parseDouble(trapproved_amount)) + "', Paid = '" + Paid + "', PaymentDateTime = '"+trDateTime+"' where InvoiceNo = '" + InvoiceNo + "'";
            //            stmt = conn.createStatement();
            //            stmt.executeUpdate(Query);
            //            stmt.close();
            //
            //            try {
            //              final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".PaymentReceiptInfo (PatientMRN,InvoiceNo,TotalAmount," + " PaidAmount,Paid,RefNo,Remarks,PayMethod,CreatedDate, BalAmount) \nVALUES (?,?,?,?,?,?,?,?,now(),?) ");
            //              MainReceipt.setString(1, PatientMRN);
            //              MainReceipt.setString(2, InvoiceNo);
            //              MainReceipt.setDouble(3, TotalAmount);
            //              MainReceipt.setDouble(4, Double.parseDouble(trapproved_amount));
            //              MainReceipt.setInt(5, Paid);
            //              MainReceipt.setString(6, InvoiceNo);
            //              MainReceipt.setString(7, "Credit Card Payment");
            //              MainReceipt.setString(8, String.valueOf(PayMethod));
            //              MainReceipt.setDouble(9, (BalAmount - Double.parseDouble(trapproved_amount)));
            //              MainReceipt.executeUpdate();
            //              MainReceipt.close();
            //            }
            //            catch (Exception e) {
            //              out.println("Error 6- Insertion PaymentReceiptInfo Table :" + e.getMessage());
            //              return;
            //            }
            //
            //
            //            final Parsehtm Parser = new Parsehtm(request);
            //            Parser.SetField("Message", String.valueOf("Transaction Performed Success :" + crresult_text));
            //            Parser.SetField("FormName", String.valueOf("RegisteredPatients_old"));
            //            Parser.SetField("ActionID", String.valueOf("CollectPayment_View&PatientInvoice="+PatientMRN));
            //            Parser.SetField("UserId", String.valueOf(UserId));
            //            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/SuccessCC.html");
            //
            //          }
            //
            //        }
            //
            //      }
            //      else {
            //        //out.println("Transaction Declined Due to : "+trresult_text);
            //        final Parsehtm Parser = new Parsehtm(request);
            //        Parser.SetField("Message", String.valueOf("Transaction Declined Due to : "+trresult_text));
            //        Parser.SetField("FormName", String.valueOf("RegisteredPatients_old"));
            //        Parser.SetField("ActionID", String.valueOf("CollectPayment_View&PatientInvoice="+PatientMRN));
            //        Parser.SetField("UserId", String.valueOf(UserId));
            //        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ErrorCC.html");
            //      }


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


    private void download_direct(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
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
