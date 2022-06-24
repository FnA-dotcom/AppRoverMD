package Testing;

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

public class AvailTest {
    public static void main(String[] args) {
        try {
            BufferedReader br;
            String client_id = URLEncoder.encode("deea5802-6af3-4daa-a26c-ffd5cee32fdb", StandardCharsets.UTF_8.toString());
            String client_secret = URLEncoder.encode("gV7nQ6pW2qM8eS2eC1rI2hB8tQ4nC1tD3xN7rL5bE1uB2xS4vU", StandardCharsets.UTF_8.toString());
            System.out.println("client_id " + client_id);
            System.out.println("client_secret " + client_secret);
            String operational = "scope=hipaa&grant_type=client_credentials&client_id=" + client_id + "&client_secret=" + client_secret + "";

            String baseUrl = "https://api.availity.com/availity/v1/token";
            System.out.println(baseUrl);

            HttpURLConnection conn = (HttpURLConnection) (new URL(baseUrl)).openConnection();
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
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
            Map fields = parseJsonString(responseBody);
            System.out.println(fields);
            String access_token = (String) fields.get("\"access_token");
            System.out.println(access_token);

            //AAIkZGVlYTU4MDItNmFmMy00ZGFhLWEyNmMtZmZkNWNlZTMyZmRiAqd5_oKjEJYPrzy6snQwSRDCk1k369Ai1_Xj_9DCDD4xZ7Kgro57H-i-pl5DgxfFm8vySOfjfpN5f5VtdbbzbtkjbkcO3pWO6deK5p3r3FHeu7TZLaE5QY-gEGsuMNUs6D_OBpLncl1AdA6R8Ah-Nw
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {

        }
    }

    private static Map parseJsonString(String json) throws IOException {
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

}
