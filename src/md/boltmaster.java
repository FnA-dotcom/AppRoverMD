package md;

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
public class boltmaster {

    //private  final  BoltClientProperties props = null;
    private BoltClientProperties props;

    private String sessionKey;
    private String Query;
    private Statement stmt;
    private ResultSet rset;
    private boolean terminalConnected;
    private boolean requestInProgress;


    public static void main(String[] args) {
        // TODO Auto-generated method stub
//        Connection conn = null;
//        try {
//            conn = getConnection();
//            System.out.println(conn);
//        }catch(Exception e){
//            System.out.println("ERROR IN CONNECTION:"+e.getMessage());
//        }
//        boltmaster bm = new boltmaster();
//        bm.cancel();
//		bm.init("C032UQ02350381", 9, conn);
//		bm.validateConnection();
//		Map<String, String> AuthCardRe = new HashMap<String, String>();
//		AuthCardRe = bm.authCard("11");
//		System.out.println(AuthCardRe);
        //System.out.println(bm.parseJsonString("{dateTime=20210120123739, resptext=Approval, footer=, orderNote=, respcode=000, avsresp=Y, merchid=800000001045, respproc=RPCT, nameOnCard=DEVKIT DISCOVER TESTCARD, bintype=, expiry=1225, respstat=A, retref=020865145459, amount=0.11, address2=King of Prussia, PA, orderid=C032UQ02350040-20210120123739, address1=1000 Continental dr, cvvresp=P, receiptData=dba, batchid=103, entrymode=Swipe (non emv), token=9605132990786668, authcode=PPS472, phone=4846549613, name=DEVKIT DISCOVER TESTCARD, header=, items=}"));
    }

    public static Connection getConnection() {
        try {
            Class.forName("org.mysql.jdbc.Driver");
            final Connection connection = DriverManager.getConnection("jdbc:mysql://database-1.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986");
            return connection;
        } catch (Exception e) {
            System.out.println("GetConnection ERROR: " + e.getMessage());
            return null;
        }
    }

    public String validateConnection() {
        String Message = "";
        sessionKey = null;
        if (sessionKey == null) {
            try {
                this.sessionKey = connect();
            } catch (IOException e) {
                //SessionKey header is required EXCEPTION's will be treated in this catch
                //TO-DO -- Display a message which says that your device is not connected!
                //System.out.println("ERROR IN FIRST CATCH --> " + e.getMessage());
                terminalConnected = false;
                return "Error";
            }
        }
        if (sessionKey != null && !requestInProgress) {
            try {
//                System.out.println("Inside Session Key NOT NULL");
                //boolean ok = ping();
                boolean ok = ping2();
                // boolean ok = true;
                if (ok) {
                    terminalConnected = true;
                    //Message = "connectionEstablished";
//                    System.out.println("connectionEstablished");
                    dateTime();
                    //display("Connected");
                } else if (!ok) {
                    terminalConnected = false;
                    sessionKey = null;
                    sessionKey = validateConnection();
                }
            } catch (IOException e) {
                //hsn: C032UQ02350040 is currently in merchant mode
                System.out.println("ERROR IN SECOND CATCH -->" + e.getMessage());
                //Message += "~ERROR IN SECOND CATCH -->" + e.getMessage();
                if (e.getMessage().startsWith("Session key") && e.getMessage().endsWith("not valid")) {
                    sessionKey = null;
                    terminalConnected = false;
                } else {
                    System.out.println("ERROR IN THIRD CATCH -->" + e.getMessage());
                    //Message += "~ERROR IN THIRD CATCH -->" + e.getMessage();
                }
            }
        }
        return sessionKey;
    }

    public void stop() {
        try {
            cancel();
            //clearDisplay();
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
//                    .getHsn() + "\", \"SessionKey\" : \""+this.props.getApiKey()+"\"}");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR IN DISCONNECT METHOD-----> " + e.getMessage());
        }
    }

    public void display(String text) throws IOException {
        sendRequest("display", "{\"merchantId\" : \"" + this.props
                .getMerchantId() + "\",\"hsn\" : \"" + this.props
                .getHsn() + "\",\"text\" : \"" + text + "\"}");
    }

    public void clearDisplay() {
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
            // TODO Auto-generated catch block
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

        System.out.println("Raw msg: PING: " + result.body);
        System.out.println("P_arse JSON MSG: PING: " + fields.toString());
        if (fields.containsKey("connected")) {
            if (fields.get("connected").equals("true")) {
                value = true;
            }
        } else {
            value = false;
        }

//        if("true".equals(fields.get("connected"))){
//            value = true;
//        }
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


//		return parseJsonString(result.body);
        return result.body;
    }

    public void cancel() {
        try {
            sendRequest("cancel", "{\"merchantId\" : \"" + this.props
                    .getMerchantId() + "\",\"hsn\" : \"" + this.props
                    .getHsn() + "\"}");
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
            if (sessionKey != null) {
                conn.setRequestProperty("X-CardConnect-SessionKey", sessionKey);
            }
            conn.setReadTimeout(300000);

            requestInProgress = true;

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
            while ((line = br.readLine()) != null) {
                output.append(line);
            }
            System.out.println("output: " + output);
            String sessionKey = conn.getHeaderField("X-CardConnect-SessionKey");
            System.out.println("sessionKey: " + sessionKey);
            if (sessionKey != null && sessionKey.indexOf(';') > -1) {
                sessionKey = sessionKey.substring(0, sessionKey.indexOf(';'));
            }

            conn.disconnect();

//            System.out.println(statusCode + "---:StatusCode");
            String responseBody = output.toString();
            if (statusCode != 200) {
                //this.logger.log(Level.SEVERE, () -> String.format("Response status: %s, body:%n%s", new Object[] { Integer.valueOf(statusCode), responseBody }));
                Map<String, String> fields = parseJsonString(responseBody);
                throw new IOException(fields.get("errorMessage"));
            }
		     /* this.logger.log(Level.INFO, () -> {
		            if (responseBody.isEmpty()) {
		              return String.format("Response status: %s", new Object[] { Integer.valueOf(statusCode) });
		            }
		            return String.format("Response status: %s, body:%n%s", new Object[] { Integer.valueOf(statusCode), responseBody });
		          });*/

            return new HttpResponse(output.toString(), sessionKey);
        } catch (IOException e) {
            if (e.getMessage().startsWith("Session key") && e.getMessage().endsWith("not valid")) {
                sessionKey = null;
                terminalConnected = false;
                // this.listeners.forEach(BoltClientListener::connectionLost);
                throw new IOException("Terminal session was lost");
            }
            throw e;
        } finally {
            requestInProgress = false;
        }
    }


    private Map<String, String> parseJsonString(String json) throws IOException {
        String noBrackets = json.replaceAll("\\{", "").replaceAll("\\}", "");
        return Arrays.stream(noBrackets.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
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


    public void init(String DeviceS, int ClientId, Connection conn) throws FileNotFoundException {
//		File propertiesFile = new File("E:\\client.properties");
        stmt = null;
        rset = null;
        Query = "";
        //File propertiesFile = new File("/sftpdrive/opt/client.properties");
        //if (propertiesFile.exists()) {
        Properties properties = new Properties();
        try {

            Query = "Select BaseURL, APIKey, MerchantId, HSN, ConnectForce, ReadcardIncludeSignature, ReadcardAmountDisplay, ReadcardIncludePin, ReadcardBeep, " +
                    "ReadcardAid, ReadmanualIncludeSignature, ReadmanualExpirationDate, ReadmanualBeep, AuthcardIncludeSignature, AuthcardAmountDisplay, " +
                    "AuthcardBeep, AuthcardAuthMerchant, AuthcardAid, AuthcardIncludeAvs, AuthcardIncludePin, AuthcardCapture, AuthmanualIncludeSignature, " +
                    "AuthmanualAmountDisplay, AuthmanualBeep, AuthmanualAuthMerchant, AuthmanualIncludeAvs, AuthmanualIncludeCvv, AuthmanualCapture " +
                    "from oe.BoltClientProperties where Status = 0 and ClientId = " + ClientId + " and DeviceHSN = '" + DeviceS + "'";
            //System.out.println(Query);
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
                //properties.setProperty("#authcard.auth.merchant.id",rset.getString(17));
                properties.setProperty("authcard.aid", rset.getString(18));
                properties.setProperty("authcard.include.avs", rset.getString(19));
                properties.setProperty("authcard.include.pin", rset.getString(20));
                properties.setProperty("authcard.capture", rset.getString(21));
                properties.setProperty("authmanual.include.signature", rset.getString(22));
                properties.setProperty("authmanual.amount.display", rset.getString(23));
                properties.setProperty("authmanual.beep", rset.getString(24));
                //properties.setProperty("#authmanual.auth.merchant.id",rset.getString(25));
                properties.setProperty("authmanual.include.avs", rset.getString(26));
                properties.setProperty("authmanual.include.cvv", rset.getString(27));
                properties.setProperty("authmanual.capture", rset.getString(28));
                //properties.load(new FileInputStream(propertiesFile));
            }
            rset.close();
            stmt.close();

            //System.out.println(properties);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
//			catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//
//			}
        this.props = new BoltClientProperties(properties);


//        }else {
//            throw new FileNotFoundException("Missing client.properties file");
//        }

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
