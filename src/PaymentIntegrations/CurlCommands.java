package PaymentIntegrations;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CurlCommands {
    public static void main(String arg[]) throws IOException {
        try {
            ProcessBuilder pb1 = new ProcessBuilder(
                    "curl",
                    "-s",
                    "https://fts-uat.cardconnect.com/cardconnect/rest/inquireMerchant/496160873888 ");

            ProcessBuilder pb = new ProcessBuilder("curl", "--silent", "--location", "--request", "GET", "https://fts-uat.cardconnect.com/cardconnect/rest/inquireMerchant/496160873888", "--header", "Content-Type:application/json", "--header", "Authorization: Basic dGVzdGluZzp0ZXN0aW5nMTIz", "--header", "Cookie: BIGipServerphu-smbcs-vip_2400=!HJlIJEMcMrdD71U5Pk/Al9QX2MnzTtDCVNLKcpiwCl2XaKMy2vxbpPT3pmfI47q0WMc6GqXooCFWPA==; BIGipServerphu-smb-vip_8080=!NzVNEMJpfB/H8rw5Pk/Al9QX2MnzTlm9hQjGN7nrPRR7j+MgqcjP9Tm5dcG0m/00H7Qhkq9ZQc8G8go=; BIGipServerphu-smbquery-vip_8080=!xK5THFA+gDEydF45Pk/Al9QX2MnzTkH6hdf3vTrEy5tx9frzMC3j/lRghwLcCIip2YtKeIUWHfbmA8g=");
            // errorstream of the process will be redirected to standard output
            pb.redirectErrorStream(true);
            // start the process
            Process proc = pb.start();
            /* get the inputstream from the process which would get printed on
             * the console / terminal
             */
            InputStream ins = proc.getInputStream();
            // creating a buffered reader
            BufferedReader read = new BufferedReader(new InputStreamReader(ins));
            StringBuilder sb = new StringBuilder();
            read.lines().forEach(line -> {
                System.out.println("line>" + line);
                sb.append(line);
            });
            // close the buffered reader
            read.close();
            /*
             * wait until process completes, this should be always after the
             * input_stream of processbuilder is read to avoid deadlock
             * situations
             */
            proc.waitFor();
            /* exit code can be obtained only after process completes, 0
             * indicates a successful completion
             */
            int exitCode = proc.exitValue();
            System.out.println("exit code::" + exitCode);
            // finally destroy the process
            proc.destroy();


            try {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse("[" + sb + "]");

                JSONArray array = (JSONArray) obj;
                JSONObject obj2 = (JSONObject) array.get(0);

                String fee_value = (String) obj2.get("fee_value");
                String site = (String) obj2.get("site");
                String acctupdater = (String) obj2.get("acctupdater");
                String cardproc = (String) obj2.get("cardproc");
                String fee_format = (String) obj2.get("fee_format");
                String fee_type = (String) obj2.get("fee_type");
                Boolean enabled = (Boolean) obj2.get("enabled");
                String merchid = (String) obj2.get("merchid");
                String fee_merchid = (String) obj2.get("fee_merchid");
                System.out.println("fee_value " + fee_value);
                System.out.println("site " + site);
                System.out.println("acctupdater " + acctupdater);
                System.out.println("cardproc " + cardproc);
                System.out.println("fee_format " + fee_format);
                System.out.println("fee_type " + fee_type);
                System.out.println("merchid " + merchid);
                System.out.println("fee_merchid " + fee_merchid);
                System.out.println("enabled " + enabled);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedOperationException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
