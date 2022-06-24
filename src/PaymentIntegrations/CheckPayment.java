package PaymentIntegrations;

import DAL.Payments;
import Handheld.UtilityHelper;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.util.Set;

@SuppressWarnings("Duplicates")
public class CheckPayment {

    public String generateToken(int ClientId, Connection conn, String accountNo) {
        String ENDPOINT = "";
        String USERNAME = "";
        String PASSWORD = "";
        String MerchantId = "";
        //Type Flag
        // 0 - Sandbox
        //int FlagType = 0;
        // 1 - Dev
        //int FlagType = 1;
        // 2 - Prod
        int FlagType = 2;
        StringBuilder Message;
        try {

            UtilityHelper helper = new UtilityHelper();
            String[] AuthConnect = helper.getAuthConnect(conn, FlagType, ClientId);
            ENDPOINT = AuthConnect[0];
            USERNAME = AuthConnect[1];
            PASSWORD = AuthConnect[2];
            MerchantId = AuthConnect[3];

//            System.out.println("ENDPOINT : " + ENDPOINT);
//            System.out.println("USERNAME: " + USERNAME);
//            System.out.println("PASSWORD: " + PASSWORD);
//            System.out.println("MerchantId: " + MerchantId);

            JSONObject request = new JSONObject();
            //"account" : "<routing number>/<account number>"
            request.put("account", accountNo);
            System.out.println("Request : " + request);
            CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

            // Send an AuthTransaction request
            JSONObject response = client.getToken(request);
            System.out.println("Response : " + response);
            // Handle response
            Set<String> keys = response.keySet();
            for (String key : keys)
                System.out.println(key + ": " + response.get(key));

            return (String) response.get("token");
        } catch (Exception e) {
            Message = new StringBuilder(e.getMessage() + "<br>");
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                Message.append(e.getStackTrace()[i]).append(" TTTTTTT ******* <br>");
            }
            return Message.toString();
        }
    }

    /**
     * Authorize Transaction for Check Payment
     *
     * @param token
     * @return
     */
    public String[] performCheckPaymentAuth(int ClientId, Connection conn, String token, double amount, int patientMRN,
                                            String patientName, String street, String city, String region, String country, String postal,
                                            String achEntryCode) {
        String ENDPOINT = "";
        String USERNAME = "";
        String PASSWORD = "";
        String MerchantId = "";
        String Currency = "";

        //Type Flag
        // 0 - Sandbox
        //int FlagType = 0;
        // 1 - Dev
        //int FlagType = 1;
        // 2 - Prod
        int FlagType = 2;

        StringBuilder Message;
        System.out.println("\nCheck Authorization Request");

        try {

            Payments payments = new Payments();
            String[] AuthConnect = payments.getACHAuthConnect(conn, ClientId);
            ENDPOINT = AuthConnect[0];
            USERNAME = AuthConnect[1];
            PASSWORD = AuthConnect[2];
            MerchantId = AuthConnect[3];

/*            System.out.println("ENDPOINT : " + ENDPOINT);
            System.out.println("USERNAME: " + USERNAME);
            System.out.println("PASSWORD: " + PASSWORD);
            System.out.println("MerchantId: " + MerchantId);*/

            // Create Authorization Transaction request
            JSONObject request = new JSONObject();
            // Merchant ID
            request.put("merchid", MerchantId);
            // Card Number
            //VISA Card Approved
            request.put("account", token);
            // Card Expiry
            //request.put("expiry", "0914");
            // Card CCV2
            //request.put("cvv2", "112");
            // Transaction amount
            request.put("amount", amount);
            // Transaction currency
            request.put("currency", Currency);
            // Cardholder Name
            request.put("name", "Fam-llc");
/*            // Order ID
            //request.put("orderid", "12345");

            // Cardholder Address
            request.put("Street", street);
            // Cardholder City
            request.put("city", city);
            // Cardholder State
            request.put("region", region);
            // Cardholder Country
            request.put("country", country);
            // Cardholder Zip-Code
            request.put("postal", postal);*/
            // Return a token for this card number
            request.put("tokenize", "Y");

            request.put("accttype", "ECHK");

            request.put("ecomind", "E");

            request.put("achDescription", patientMRN);

            request.put("achEntryCode", achEntryCode);

            System.out.println("Request : " + request);
            // Create the REST client
            CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

            // Send an AuthTransaction request
            JSONObject response = client.authorizeTransaction(request);
            System.out.println("Response : " + response);
            // Handle response
            Set<String> keys = response.keySet();
            for (String key : keys)
                System.out.println(key + ": " + response.get(key));

            String ResponseText = (String) response.get("resptext");
            String Amount = (String) response.get("amount");
            String CardProc = (String) response.get("cardproc");
            String Commcard = (String) response.get("commcard");
            String ResponseCode = (String) response.get("respcode");
            String EntryMode = (String) response.get("entrymode");
            String Merchant = (String) response.get("merchid");
            String ResponseToken = (String) response.get("token");
            String RespProc = (String) response.get("respproc");
            String BinType = (String) response.get("bintype");
            String Expiry = (String) response.get("expiry");
            String RetRef = (String) response.get("retref");
            String RespStat = (String) response.get("respstat");
            String Account = (String) response.get("account");

            return new String[]{ResponseText, Amount, CardProc, Commcard, ResponseCode, EntryMode, Merchant,
                    ResponseToken, RespProc, BinType, Expiry, BinType, RetRef, RespStat, Account};
        } catch (Exception e) {
            Message = new StringBuilder(e.getMessage() + "<br>");
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                Message.append(e.getStackTrace()[i]).append(" TTTTTTT ******* <br>");
            }
            return new String[]{String.valueOf(Message)};
        }
    }
}
