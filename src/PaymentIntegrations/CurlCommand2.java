package PaymentIntegrations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CurlCommand2 {
    public static void main(String arg[]) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("curl", "--silent", "--location", "--request", "POST",
                "https://bolt.cardpointe.com/api/v3/authCard",
                "--header", "Content-Type:application/json",
                "--header", "Authorization: EEEuMACVaH11XGKxREqjnEcMaNIftRuv5KSlb501H9o=",
                "--header", "X-CardConnect-SessionKey: 6f923caa7e5a4ada8205073dee29865d",
                "--data-raw", "merchantId: 496407356880",
                "--data-raw", "hsn: C032UQ02350040",
                "--data-raw", "amount: 100",
                "--data-raw", "includeSignature: false",
                "--data-raw", "includeAmountDisplay: true",
                "--data-raw", "includeAVS: false",
                "--data-raw", "capture: true",
                "--data-raw", "orderId: NCC1701D",
                "--data-raw", "clearDisplayDelay: 500",
                "--data-raw", "printReceipt: true",
                "--data-raw", "printExtraReceipt: true",
                "--data-raw", "printDelay: 2000");
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


/*        try {
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
        }*/
    }
}
