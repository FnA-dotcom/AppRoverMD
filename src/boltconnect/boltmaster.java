package boltconnect;

import md.BoltClientProperties;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class boltmaster {

    //private  final  BoltClientProperties props = null;
    private BoltClientProperties props;

    private String sessionKey;
    private boolean terminalConnected;
    private boolean requestInProgress;

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub

        boltmaster bm = new boltmaster();
        bm.init();
        bm.validateConnection();
        Map<String, String> AuthCardRe = new HashMap<String, String>();
        AuthCardRe = bm.authCard("103");
        System.out.println(AuthCardRe);
    }

    public void validateConnection() {
        if (this.sessionKey == null) {
            try {
                this.sessionKey = connect();
                System.out.println("SESSION KEY-->" + this.sessionKey);
            } catch (IOException e) {
                //SessionKey header is required EXCEPTION's will be treated in this catch
                //TO-DO -- Display a message which says that your device is not connected!
                System.out.println("ERROR IN FIRST CATCH --> " + e.getMessage());
            }
        }
        if (this.sessionKey != null && !this.requestInProgress) {
            try {
                boolean ok = ping();
                if (ok && !this.terminalConnected) {
                    this.terminalConnected = true;
                    System.out.println("connectionEstablished");
                    dateTime();
                    display("Connected");
                } else if (!ok && this.terminalConnected) {
                    this.terminalConnected = false;
                }
            } catch (IOException e) {
                //hsn: C032UQ02350040 is currently in merchant mode
                System.out.println("ERROR IN SECOND CATCH -->" + e.getMessage());
                if (e.getMessage().startsWith("Session key") && e.getMessage().endsWith("not valid")) {
                    this.sessionKey = null;
                    this.terminalConnected = false;
                } else {
                    System.out.println("ERROR IN THIRD CATCH -->" + e.getMessage());
                }
            }
        }
    }

    public void stop() {
        try {
            disconnect();
        } catch (Exception e) {

        }

    }

    private void disconnect() {
        try {
            sendRequest("disconnect", "{\"merchantId\" : \"" + this.props
                    .getMerchantId() + "\",\"hsn\" : \"" + this.props
                    .getHsn() + "\"}");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void display(String text) throws IOException {
        sendRequest("display", "{\"merchantId\" : \"" + this.props
                .getMerchantId() + "\",\"hsn\" : \"" + this.props
                .getHsn() + "\",\"text\" : \"" + text + "\"}");
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


    private String connect() throws IOException {
        HttpResponse result = sendRequest("connect", "{ \"merchantId\" : \"" + this.props
                .getMerchantId() + "\",\"hsn\" : \"" + this.props
                .getHsn() + "\",\"force\" : \"" + this.props
                .isForceConnect() + "\"}");

        return result.sessionKey;
    }

    public Map<String, String> authCard(String amount) throws IOException {
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
                .isAuthCardCapture() + "}");


        return parseJsonString(result.body);
    }


    private class HttpResponse {
        private String body;


        private String sessionKey;


        public HttpResponse(String body, String sessionKey) {
            this.body = body;
            this.sessionKey = sessionKey;
        }
    }


    public void cancel() {
        try {
            sendRequest("cancel", "{\"merchantId\" : \"" + this.props
                    .getMerchantId() + "\",\"hsn\" : \"" + this.props
                    .getHsn() + "\"}");
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
            if (this.sessionKey != null) {
                conn.setRequestProperty("X-CardConnect-SessionKey", this.sessionKey);
            }
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
            while ((line = br.readLine()) != null) {
                output.append(line);
            }

            String sessionKey = conn.getHeaderField("X-CardConnect-SessionKey");
            if (sessionKey != null && sessionKey.indexOf(';') > -1) {
                sessionKey = sessionKey.substring(0, sessionKey.indexOf(';'));
            }

            conn.disconnect();

            String responseBody = output.toString();
            if (statusCode != 200) {
                //this.logger.log(Level.SEVERE, () -> String.format("Response status: %s, body:%n%s", new Object[] { Integer.valueOf(statusCode), responseBody }));
                Map<String, String> fields = parseJsonString(responseBody);
                throw new IOException((String) fields.get("errorMessage"));
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
                this.sessionKey = null;
                this.terminalConnected = false;
                // this.listeners.forEach(BoltClientListener::connectionLost);
                throw new IOException("Terminal session was lost");
            }
            throw e;
        } finally {

            this.requestInProgress = false;
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


    public void init() throws FileNotFoundException {
        File propertiesFile = new File("E:\\client.properties");
        if (propertiesFile.exists()) {
            Properties properties = new Properties();
            try {

                properties.load(new FileInputStream(propertiesFile));

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
            this.props = new BoltClientProperties(properties);


        } else {
            throw new FileNotFoundException("Missing client.properties file");
        }

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


}
