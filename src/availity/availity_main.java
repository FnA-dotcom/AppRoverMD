package availity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class availity_main {
    // one instance, reuse
    public static String access_token = "";
    public static String bodyinc = "";

    public static void main(String[] args) throws Exception {

        availity_main obj = new availity_main();

        try {

            System.out.println("Testing 2 - Send Http POST request");
            // obj.connect();
            String aa = obj.connect();
            System.out.println(aa);


            System.out.println("Testing 1 - Send Http GET request");
            String ba = obj.getinqury();
            System.out.println(ba);

        } finally {
            obj.close();
        }
    }

    private void close() throws IOException {

    }

    private  HttpResponse sendRequest(String operation, String body) throws IOException {
        try {
            BufferedReader br;
            String client_id= URLEncoder.encode("deea5802-6af3-4daa-a26c-ffd5cee32fdb", StandardCharsets.UTF_8.toString());
            String client_secret=URLEncoder.encode("gV7nQ6pW2qM8eS2eC1rI2hB8tQ4nC1tD3xN7rL5bE1uB2xS4vU", StandardCharsets.UTF_8.toString());

            String operational="scope=hipaa&grant_type=client_credentials&client_id="+client_id+"&client_secret="+client_secret+"";

            String baseUrl = "https://api.availity.com/availity/v1/token";
            System.out.println(baseUrl);
            // String apiKey = this.props.getApiKey();
            HttpURLConnection conn = (HttpURLConnection)(new URL(baseUrl )).openConnection();
            conn.setDoOutput(true);



            conn.setRequestMethod("POST");
            // conn.setRequestProperty("Authorization", apiKey);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "application/json");




            conn.setReadTimeout(300000);


            OutputStream os = conn.getOutputStream();
            os.write(operational.getBytes());
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


            conn.disconnect();

            String responseBody = output.toString();
	     /* if (statusCode != 200) {
	        //this.logger.log(Level.SEVERE, () -> String.format("Response status: %s, body:%n%s", new Object[] { Integer.valueOf(statusCode), responseBody }));
	        Map<String, String> fields = parseJsonString(responseBody);
	        throw new IOException((String)fields.get("errorMessage"));
	      } */
            Map<String, String> fields = parseJsonString(responseBody);
            System.out.println(fields);
            access_token=(String)fields.get("\"access_token");
            System.out.println(access_token);




            return new HttpResponse(output.toString(), access_token);
        }
        catch (IOException e) {

            System.out.println(e.getMessage());
            throw e;
        } finally {


        }
    }

    private String connect() throws IOException {
        HttpResponse result = sendRequest("connect", "");

        return result.sessionKey;
    }

    private String getinqury() throws IOException {
        HttpResponse result = getsendRequest("connect", "");

        return result.body;
    }

    private HttpResponse getsendRequest(String operation, String body) throws IOException {
        try {

            BufferedReader br;
            String baseUrl = "https://api.availity.com/availity/v1/coverages?payerId=BCBSF&providerNpi=1234567893&memberId=PBHR123456&patientLastName=Parker&patientFirstName=Peter&serviceType=98&patientBirthDate=1990-01-01&providerTaxId=123456789";

            baseUrl = "https://api.availity.com/availity/v1/coverages?payerId=G84980&providerNpi=1538503024&memberId=t2u830474429&patientLastName=Rufer&patientFirstName=Abigail&serviceType=52&patientBirthDate=1994-02-15&groupNumber=385003";

            System.out.println(baseUrl);
            // String apiKey = this.props.getApiKey();
            HttpURLConnection conn = (HttpURLConnection) (new URL(baseUrl)).openConnection();
            conn.setDoOutput(true);


            conn.setRequestMethod("GET");
            // conn.setRequestProperty("Authorization", apiKey);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("authorization", "Bearer " + access_token);


            conn.setReadTimeout(300000);


		      /*OutputStream os = conn.getOutputStream();
		      os.write(o);
		      os.flush();*/


            int statusCode = conn.getResponseCode();
            System.out.println(statusCode);
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


            conn.disconnect();

            String responseBody = output.toString();


            return new HttpResponse(output.toString(), output.toString());
        } catch (IOException e) {

            System.out.println(e.getMessage());
            throw e;
        } finally {


        }
    }

    private class HttpResponse {
        private String body;


        private String sessionKey;


        public HttpResponse(String body, String sessionKey) {
            this.body = body;
            this.sessionKey = sessionKey;
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



}
