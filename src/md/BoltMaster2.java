package md;

import Handheld.UtilityHelper;
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
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class BoltMaster2 extends HttpServlet {
    String globalExpMsg;
    Long errorCode;
    private String Query;
    private Statement stmt;
    private ResultSet rset;
    private BoltClientProperties props;
    private UtilityHelper helper = new UtilityHelper();

    /**
     * Initialize global variables
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**
     * Process the HTTP Get request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = new PrintWriter(response.getOutputStream());
        out.println("<html>");
        out.println("<head><title>Bolt Master</title></head>");
        out.println("<body>Hello From boltmaster doGet()");
        out.println("</body></html>");
        out.close();
    }

    void initialize(String DeviceS, int ClientId, Connection conn, ServletContext servletContext) {
        stmt = null;
        rset = null;
        Query = "";

        Properties properties = new Properties();
        try {

            Query = "Select BaseURL, APIKey, MerchantId, HSN, ConnectForce, ReadcardIncludeSignature, ReadcardAmountDisplay, ReadcardIncludePin, ReadcardBeep, " +
                    "ReadcardAid, ReadmanualIncludeSignature, ReadmanualExpirationDate, ReadmanualBeep, AuthcardIncludeSignature, AuthcardAmountDisplay, " +
                    "AuthcardBeep, AuthcardAuthMerchant, AuthcardAid, AuthcardIncludeAvs, AuthcardIncludePin, AuthcardCapture, AuthmanualIncludeSignature, " +
                    "AuthmanualAmountDisplay, AuthmanualBeep, AuthmanualAuthMerchant, AuthmanualIncludeAvs, AuthmanualIncludeCvv, AuthmanualCapture " +
                    "from oe.BoltClientProperties where Status = 0 and ClientId = " + ClientId + " and DeviceHSN = '" + DeviceS + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                properties.setProperty("base.url", rset.getString(1));
                properties.setProperty("api.key", rset.getString(2));
                properties.setProperty("merchant.id", rset.getString(3));
                properties.setProperty("hsn", rset.getString(4));
                properties.setProperty("connect.force", rset.getString(5));
                properties.setProperty("readcard.include.signature", rset.getString(6));
                properties.setProperty("readcard.amount.display", rset.getString(7));
                properties.setProperty("readcard.include.pin", rset.getString(8));
                properties.setProperty("readcard.beep", rset.getString(9));
                properties.setProperty("readcard.aid", rset.getString(10));
                properties.setProperty("readmanual.include.signature", rset.getString(11));
                properties.setProperty("readmanual.expiration.date", rset.getString(12));
                properties.setProperty("readmanual.beep", rset.getString(13));
                properties.setProperty("authcard.include.signature", rset.getString(14));
                properties.setProperty("authcard.amount.display", rset.getString(15));
                properties.setProperty("authcard.beep", rset.getString(16));
                properties.setProperty("authcard.aid", rset.getString(18));
                properties.setProperty("authcard.include.avs", rset.getString(19));
                properties.setProperty("authcard.include.pin", rset.getString(20));
                properties.setProperty("authcard.capture", rset.getString(21));
                properties.setProperty("authmanual.include.signature", rset.getString(22));
                properties.setProperty("authmanual.amount.display", rset.getString(23));
                properties.setProperty("authmanual.beep", rset.getString(24));
                properties.setProperty("authmanual.include.avs", rset.getString(26));
                properties.setProperty("authmanual.include.cvv", rset.getString(27));
                properties.setProperty("authmanual.capture", rset.getString(28));
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println("EXCEPTION IN INIT()" + str);
            helper.SendEmailWithAttachment("Error in Bolt Master ** (init() -- Setting the Properties)", servletContext, e, "boltmaster", "init()", conn);
            Services.DumException("boltmaster", "init()", null, e, getServletContext());
            //globalExpMsg = e.getMessage();
        }
        props = new BoltClientProperties(properties);
    }

    String[] getSession(ServletContext servletContext, String mId, String hsn, String forceConnect, String bURL, String aKey) {
        String sessionKey = "";
        try {
            globalExpMsg = null;
            errorCode = null;
            try {
                sessionKey = connect(servletContext, mId, hsn, forceConnect, bURL, aKey);
            } catch (IOException e) {
                System.out.println("ERROR IN FIRST CATCH --> " + e.getMessage());
                return new String[]{"Error", globalExpMsg, String.valueOf(errorCode)};
            }
        } catch (Exception e) {
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println("EXCEPTION IN getSession()" + str);
            //helper.SendEmailWithAttachment("Error in Bolt Master ** (init() -- Setting the Properties)", servletContext, e, "boltmaster", "init()", conn);
            Services.DumException("boltmaster", "getSession", null, e, getServletContext());
        }
        return new String[]{sessionKey, globalExpMsg, String.valueOf(errorCode)};
    }

    private String connect(ServletContext servletContext, String mId, String hsn, String forceConnect, String bURL, String aKey) throws IOException {
        try {
//            System.out.println("getMerchantId in connect --> " + mId);
/*            HttpResponse result = sendRequest("connect", "{ \"merchantId\" : \"" + this.props
                    .getMerchantId() + "\",\"hsn\" : \"" + this.props
                    .getHsn() + "\",\"force\" : \"" + this.props
                    .isForceConnect() + "\"}", servletContext, null);*/
            //Changed from Global to Local on 16th Sept 2021
            //Tabish *****
            HttpResponse result = sendRequest("connect", "{ \"merchantId\" : \"" + mId + "\",\"hsn\" : \"" + hsn + "\",\"force\" : \"" + forceConnect + "\"}", servletContext, null, bURL, aKey);
            return result.sessionKey;
        } catch (Exception e) {
            System.out.println("Error in Connect: " + e.getMessage());
            Services.DumException("boltmaster", "connect", null, e, servletContext);
            globalExpMsg = e.getMessage();
            return null;
        }
    }

    String[] authCard(String amount, ServletContext servletContext, String sessionKey, String mId, String hsn, String includeSignature, String authCardDisplayAmount, String authCardBeep, String authCardAid, String includeAVS, String includePIN, String capture, String bURL, String aKey) throws IOException {
        //String authMid = props.getAuthCardAuthMerchId();
        String authMid = null;
        try {
//            System.out.println("Merchant Id --> " + mId);
            HttpResponse result = sendRequest("authCard", "{\"merchantId\":\"" + mId + "\",\"hsn\":\"" + hsn + "\",\"includeSignature\":" + includeSignature + ",\"includeAmountDisplay\":" + authCardDisplayAmount + ",\"amount\":\"" + amount.replaceAll("\\.", "") + "\",\"beep\":" + authCardBeep + "," + ((authMid == null || authMid.length() == 0) ? "" : ("\"authMerchantId\":\"" + authMid + "\",")) + "\"aid\":\"" + authCardAid + "\",\"includeAVS\":" + includeAVS + ",\"includePIN\":" + includePIN + ",\"capture\":" + capture + ", \"printReceipt\":true }", servletContext, sessionKey, bURL, aKey);
/*            HttpResponse result = sendRequest("authCard", "{\"merchantId\":\"" + this.props
                    .getMerchantId() + "\",\"hsn\":\"" + this.props
                    .getHsn() + "\",\"includeSignature\":" + this.props
                    .isAuthCardIncludeSignature() + ",\"includeAmountDisplay\":" + this.props
                    .isAuthCardDisplayAmount() + ",\"amount\":\"" + amount
                    .replaceAll("\\.", "") + "\",\"beep\":" + this.props
                    .isAuthCardBeep() + "," + ((authMid == null || authMid
                    .length() == 0) ? "" : ("\"authMerchantId\":\"" + authMid + "\",")) + "\"aid\":\"" + this.props
                    .getAuthCardAid() + "\",\"includeAVS\":" + this.props
                    .isAuthCardIncludeAvs() + ",\"includePIN\":" + this.props
                    .isAuthCardIncludePin() + ",\"capture\":" + this.props
                    .isAuthCardCapture() + ", \"printReceipt\":true }", servletContext, sessionKey, bURL, aKey);*/
            return new String[]{result.body, "", null};
        } catch (Exception e) {
            System.out.println("Error in authCard: " + e.getMessage());
            globalExpMsg = e.getMessage();
            Services.DumException("boltmaster", "authCard", null, e, servletContext);
            return new String[]{"Error in Auth Card", globalExpMsg, String.valueOf(errorCode)};
        }
    }

    void stopSession(ServletContext context, String sessionKey, String mId, String hsn, String bURL, String aKey) {
        try {
            disconnect(context, sessionKey, mId, hsn, bURL, aKey);
        } catch (Exception e) {
            System.out.println("ERROR IN ** TAB ** STOP FUNCTION----> " + e.getMessage());
            Services.DumException("boltmaster", "stop", null, e, context);
            globalExpMsg = e.getMessage();
        }
    }

    private void disconnect(ServletContext servletContext, String sessionKey, String mId, String hsn, String bURL, String aKey) {
        try {
            sendRequest("disconnect", "{\"merchantId\" : \"" + mId + "\",\"hsn\" : \"" + hsn + "\"}", servletContext, sessionKey, bURL, aKey);
/*            sendRequest("disconnect", "{\"merchantId\" : \"" + this.props
                    .getMerchantId() + "\",\"hsn\" : \"" + this.props
                    .getHsn() + "\"}", servletContext, sessionKey, bURL, aKey);*/
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR IN TAB DISCONNECT METHOD-----> " + e.getMessage());
            Services.DumException("boltmaster", "disconnect", null, e, servletContext);
            globalExpMsg = e.getMessage();
        }
    }

    void haltSession(ServletContext servletContext, String sessionKey, String mId, String hsn, String bURL, String aKey) {
        try {
            cancel(servletContext, sessionKey, mId, hsn, bURL, aKey);
        } catch (Exception e) {
            System.out.println("ERROR IN ** TAB ** HALT FUNCTION----> " + e.getMessage());
            Services.DumException("boltmaster", "halt", null, e, servletContext);
            globalExpMsg = e.getMessage();
        }
    }

    private void cancel(ServletContext servletContext, String sessionKey, String mId, String hsn, String bURL, String aKey) {
        try {
            sendRequest("cancel", "{\"merchantId\" : \"" + mId + "\",\"hsn\" : \"" + hsn + "\"}", servletContext, sessionKey, bURL, aKey);
/*            sendRequest("cancel", "{\"merchantId\" : \"" + this.props
                    .getMerchantId() + "\",\"hsn\" : \"" + this.props
                    .getHsn() + "\"}", servletContext, sessionKey, bURL, aKey);*/
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR IN CANCEL TAB METHOD-----> " + e.getMessage());
            Services.DumException("boltmaster", "cancel", null, e, servletContext);
            globalExpMsg = e.getMessage();
        }
    }

    private HttpResponse sendRequest(String operation, String body, ServletContext servletContext, String sessionKey, String bURL, String aKey) throws IOException {
        try {
//            System.out.println("SESSION KEY IN send Request --> " + sessionKey);
            System.out.println("Operation " + operation);
            BufferedReader br;
            //String baseUrl = props.getBaseUrl();
            //String apiKey = props.getApiKey();
            HttpURLConnection conn = (HttpURLConnection) (new URL(bURL + "/" + operation)).openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", aKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            if (sessionKey != null) {
                conn.setRequestProperty("X-CardConnect-SessionKey", sessionKey);
            }
            conn.setReadTimeout(300000);

            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes());
            os.flush();

            int statusCode = conn.getResponseCode();
            System.out.println("Status Code --> " + statusCode);
            if (statusCode == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                output.append(line);
            }

            try {
                JSONParser parser = new JSONParser();
                if (statusCode != 200) {
                    Object obj = parser.parse("[" + output + "]");
                    JSONArray array = (JSONArray) obj;
                    JSONObject obj2 = (JSONObject) array.get(0);
                    errorCode = (Long) obj2.get("errorCode");
                    String errorMessage = (String) obj2.get("errorMessage");
//                    System.out.println("errorCode " + errorCode);
//                    System.out.println("errorMessage " + errorMessage);
                }

            } catch (ParseException e) {
                System.out.println("Error in sendRequest First Catch: " + e.getMessage());
                Services.DumException("boltmaster", "sendRequest - 001", null, e, servletContext);
            }

            System.out.println("output: " + output);
            sessionKey = conn.getHeaderField("X-CardConnect-SessionKey");
            System.out.println("sessionKey: " + sessionKey);
            if (sessionKey != null && sessionKey.indexOf(';') > -1) {
                sessionKey = sessionKey.substring(0, sessionKey.indexOf(';'));
            }

            conn.disconnect();

            String responseBody = output.toString();

            if (statusCode != 200) {
                Map<String, String> fields = parseJsonString(responseBody);
                throw new IOException((String) fields.get("errorMessage"));
            }
            return new HttpResponse(output.toString(), sessionKey);
        } catch (IOException e) {
            System.out.println("Error in sendRequest Second Catch: " + e.getMessage());
            Services.DumException("boltmaster", "sendRequest - 002", null, e, servletContext);
            if (e.getMessage().startsWith("Session key") && e.getMessage().endsWith("not valid")) {
                // this.listeners.forEach(BoltClientListener::connectionLost);
                throw new IOException("Terminal session was lost");
            }
            globalExpMsg = e.getMessage();
            throw e;
        }
    }

    private Map<String, String> parseJsonString(String json) throws IOException {
        String noBrackets = json.replaceAll("\\{", "").replaceAll("\\}", "");
        return (Map) Arrays.stream(noBrackets.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
                .collect(Collectors.toMap(s ->
                        s.split("\":")[0].substring(1), s -> {

                    String value = s.split("\":")[1];
                    if (value.startsWith("\"")) {
                        value = value.substring(1);
                    }
                    if (value.endsWith("\"")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    return value;
                }));
    }

    private class HttpResponse {
        private String body;
        private String sessionKey;

        HttpResponse(String body, String sessionKey) {
            this.body = body;
            this.sessionKey = sessionKey;
        }
    }
}
