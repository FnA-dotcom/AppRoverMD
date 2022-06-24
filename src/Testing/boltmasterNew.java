package Testing;

import md.BoltClientProperties;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class boltmasterNew {
    private BoltClientProperties props;
    private String sessionKey;
    private String Query;
    private Statement stmt;
    private ResultSet rset;
    private boolean terminalConnected;
    private boolean requestInProgress;

    public static void main(String[] args) throws IOException {
    }

    public static Connection getConnection() {
        try {
            Class.forName("org.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://database-1.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986");
            return connection;
        } catch (Exception e) {
            System.out.println("GetConnection ERROR: " + e.getMessage());
            return null;
        }
    }

    public String validateConnection() {
        String Message = "";
        sessionKey = null;
        if (sessionKey == null)
            try {
                sessionKey = connect();
            } catch (IOException e) {
                terminalConnected = false;
                return "Error";
            }
        if (sessionKey != null && !requestInProgress)
            try {
                boolean ok = ping2();
                if (ok) {
                    terminalConnected = true;
                    dateTime();
                } else if (!ok) {
                    terminalConnected = false;
                    sessionKey = null;
                    sessionKey = validateConnection();
                }
            } catch (IOException e) {
                System.out.println("ERROR IN SECOND CATCH -->" + e.getMessage());
                if (e.getMessage().startsWith("Session key") && e.getMessage().endsWith("not valid")) {
                    sessionKey = null;
                    terminalConnected = false;
                } else {
                    System.out.println("ERROR IN THIRD CATCH -->" + e.getMessage());
                }
            }
        return sessionKey;
    }

    public void stop() {
        try {
            cancel();
            disconnect();
        } catch (Exception e) {
            System.out.println("ERROR IN STOP FUNCTION----> " + e.getMessage());
        }
    }

    private void disconnect() {
        try {
            sendRequest("disconnect", "{\"merchantId\" : \"" + this.props
                    .getMerchantId() + "\",\"hsn\" : \"" + this.props
                    .getHsn() + "\"}");
            sessionKey = null;
        } catch (IOException e) {
            System.out.println("ERROR IN DISCONNECT METHOD-----> " + e.getMessage());
        }
    }

    public void display(String text) throws IOException {
        sendRequest("display", "{\"merchantId\" : \"" + this.props
                .getMerchantId() + "\",\"hsn\" : \"" + this.props
                .getHsn() + "\",\"text\" : \"" + text + "\"}");
    }

    public void clearDisplay() throws IOException {
        try {
            sendRequest("clearDisplay", "{\"merchantId\" : \"" + this.props
                    .getMerchantId() + "\",\"hsn\" : \"" + this.props
                    .getHsn() + "}");
        } catch (Exception e) {
            System.out.println("ERROR IN CLEAR DISPLAY METHOD-----> " + e.getMessage());
        }
    }

    public void dateTime() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        try {
            sendRequest("dateTime", "{\"merchantId\" : \"" + this.props
                    .getMerchantId() + "\",\"hsn\" : \"" + this.props
                    .getHsn() + "\",\"dateTime\" : \"" + dateTime + "\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean ping() throws IOException {
        HttpResponse result = sendRequest("ping", "{\"merchantId\" : \"" + this.props
                .getMerchantId() + "\",\"hsn\" : \"" + this.props
                .getHsn() + "\"}");
        Map<String, String> fields = parseJsonString(result.body);
        return "true".equals(fields.get("connected"));
    }

    public boolean ping2() throws IOException {
        boolean value = false;
        HttpResponse result = sendRequest("ping", "{\"merchantId\" : \"" + this.props
                .getMerchantId() + "\",\"hsn\" : \"" + this.props
                .getHsn() + "\"}");
        Map<String, String> fields = parseJsonString(result.body);
        if (fields.containsKey("connected")) {
            if (((String) fields.get("connected")).equals("true"))
                value = true;
        } else {
            value = false;
        }
        return value;
    }

    private String connect() throws IOException {
        try {
            HttpResponse result = sendRequest("connect", "{ \"merchantId\" : \"" + this.props
                    .getMerchantId() + "\",\"hsn\" : \"" + this.props
                    .getHsn() + "\",\"force\" : \"" + this.props
                    .isForceConnect() + "\"}");
            return result.sessionKey;
        } catch (Exception e) {
            System.out.println("Error in Connect: " + e.getMessage());
            return "";
        }
    }

    public String authCard(String amount) throws IOException {
        String authMid = this.props.getAuthCardAuthMerchId();
        HttpResponse result = sendRequest("authCard", "{\"merchantId\":\"" + this.props
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
                .isAuthCardCapture() + ", \"printReceipt\":true }");
        return result.body;
    }

    public void cancel() {
        try {
            sendRequest("cancel", "{\"merchantId\" : \"" + this.props
                    .getMerchantId() + "\",\"hsn\" : \"" + this.props
                    .getHsn() + "\"}");
        } catch (IOException e) {
            System.out.println("ERROR IN CANCEL METHOD-----> " + e.getMessage());
            e.printStackTrace();
        }
    }

    private HttpResponse sendRequest(String operation, String body) throws IOException {
        try {
            BufferedReader br;
            String baseUrl = this.props.getBaseUrl();
            String apiKey = this.props.getApiKey();
            HttpURLConnection conn = (HttpURLConnection) (new URL(baseUrl + "/" + operation)).openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            if (this.sessionKey != null)
                conn.setRequestProperty("X-CardConnect-SessionKey", this.sessionKey);
            conn.setReadTimeout(300000);
            this.requestInProgress = true;
            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes());
            os.flush();
            int statusCode = conn.getResponseCode();
            if (statusCode == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                output.append(line);
            System.out.println("output: " + output);
            String sessionKey = conn.getHeaderField("X-CardConnect-SessionKey");
            System.out.println("sessionKey: " + sessionKey);
            if (sessionKey != null && sessionKey.indexOf(';') > -1)
                sessionKey = sessionKey.substring(0, sessionKey.indexOf(';'));
            conn.disconnect();
            String responseBody = output.toString();
            if (statusCode != 200) {
                Map<String, String> fields = parseJsonString(responseBody);
                throw new IOException((String) fields.get("errorMessage"));
            }
            return new HttpResponse(output.toString(), sessionKey);
        } catch (IOException e) {
            if (e.getMessage().startsWith("Session key") && e.getMessage().endsWith("not valid")) {
                this.sessionKey = null;
                this.terminalConnected = false;
                throw new IOException("Terminal session was lost");
            }
            throw e;
        } finally {
            this.requestInProgress = false;
        }
    }

    private Map<String, String> parseJsonString(String json) throws IOException {
        String noBrackets = json.replaceAll("\\{", "").replaceAll("\\}", "");
        return (Map<String, String>) Arrays.<String>stream(noBrackets.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
                .collect(Collectors.toMap(s -> s.split("\":")[0].substring(1), s -> {
                    String value = s.split("\":")[1];
                    if (value.startsWith("\""))
                        value = value.substring(1);
                    if (value.endsWith("\""))
                        value = value.substring(0, value.length() - 1);
                    return value;
                }));
    }

    public void init(String DeviceS, int ClientId, Connection conn) throws FileNotFoundException {
        this.stmt = null;
        this.rset = null;
        this.Query = "";
        Properties properties = new Properties();
        try {
            Query = "Select BaseURL, APIKey, MerchantId, HSN, ConnectForce, ReadcardIncludeSignature, " +
                    "ReadcardAmountDisplay, ReadcardIncludePin, ReadcardBeep, ReadcardAid, " +
                    "ReadmanualIncludeSignature, ReadmanualExpirationDate, ReadmanualBeep, " +
                    "AuthcardIncludeSignature, AuthcardAmountDisplay, AuthcardBeep, AuthcardAuthMerchant, " +
                    "AuthcardAid, AuthcardIncludeAvs, AuthcardIncludePin, AuthcardCapture, " +
                    "AuthmanualIncludeSignature, AuthmanualAmountDisplay, AuthmanualBeep, AuthmanualAuthMerchant, " +
                    "AuthmanualIncludeAvs, AuthmanualIncludeCvv, AuthmanualCapture " +
                    "from oe.BoltClientProperties " +
                    "where Status = 0 and ClientId = " + ClientId + " and DeviceHSN = '" + DeviceS + "'";
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
            e.printStackTrace();
        }
        props = new BoltClientProperties(properties);
    }

    private void setStatus(boolean error, String message, boolean disableButtons) {
        String indicatorFile = error ? "circle_red.png" : "circle_green.png";
        String id = error ? "status-error" : "status-ok";
    }

    public void connectionEstablished() {
        setStatus(false, "Terminal connected, waiting for agent", false);
    }

    public void connectionLost() {
        setStatus(true, "Terminal not connected", true);
    }

    public void connectionError(String message) {
        setStatus(true, message, true);
    }

    private class HttpResponse {
        private String body;


        private String sessionKey;


        public HttpResponse(String body, String sessionKey) {
            this.body = body;
            this.sessionKey = sessionKey;
        }
    }
}
