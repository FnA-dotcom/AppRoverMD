package PaymentIntegrations;

import Handheld.UtilityHelper;
import org.json.simple.JSONObject;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServlet;
import java.security.Key;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Set;

@SuppressWarnings("Duplicates")
public class CardConnectPayment extends HttpServlet {
    private static final byte[] keyValue =
            new byte[]{'T', '#', '3', 'B', '3', '$', 'T',
                    '$', '3', 'C', 'r', '3', 't', 'K', '3', 'Q'};
    private static final String ALGO = "AES";


    /**
     * Authorize Transaction REST Example
     *
     * @return
     */
    public static String[] authTransaction(String EndPoint, String UserName, String Password, String MerchantId,
                                           String myToken, String myExpiryDate, String amount, String currency,
                                           String name, String city, String state, String country, String zipCode, String CVV2, String address) {
        System.out.println("\nSending Authorization Request ........");

        String ResponseText;
        String CVVResponse;
        String ResponseCode;
        String EntryMode;
        String AuthCode;
        String ResponseProc;
        String ResponseStatus;
        String RetrieveReference;
        String Expiry;
        String AVSResponse;
        String Receipt;
        String BinType;
        String Amount;
        String AccountNo;
        String orderId;
        String commCard;


/*        System.out.println("***************** Auth Transaction START **************************");
        System.out.println("EndPoint " + EndPoint);
        System.out.println("UserName " + UserName);
        System.out.println("Password " + Password);
        System.out.println("MerchantId " + MerchantId);
        System.out.println("myToken " + myToken);
        System.out.println("myExpiryDate " + myExpiryDate);
        System.out.println("amount " + amount);
        System.out.println("name " + name);
        System.out.println("city " + city);
        System.out.println("state " + state);
        System.out.println("country " + country);
        System.out.println("zipCode " + zipCode);
        System.out.println("Address " + address);
        System.out.println("***************** Auth Transaction END **************************");*/

/*        if (country.equals("UnitedStates") || country.equals("United States") ||
                country.equals("UNITED STATES") || country.equals("UNITEDSTATES") ||
                country.equals("unitedstates") || country.equals("USA") )
            country = "US";*/

        // Create Authorization Transaction request
        JSONObject request = new JSONObject();
        // Merchant ID
        request.put("merchid", MerchantId);
        // Card Number
        request.put("account", myToken);
        // Card Expiry
        request.put("expiry", myExpiryDate);
        // Transaction amount
        request.put("amount", amount);
        // Transaction currency
        request.put("currency", currency);
        // Cardholder Name
        request.put("name", name);
        // Cardholder Address
        request.put("Street", address);
        // Cardholder City
        request.put("city", city);
        // Cardholder State
        request.put("region", state);
        // Cardholder Country
        request.put("country", country);
        // Cardholder Zip-Code
        request.put("postal", zipCode);
        // Return a token for this card number
        request.put("tokenize", "Y");
        // Print Receipt
        request.put("receipt", "Y");
        //Capture request
        request.put("capture", "Y");
        //CVV2
        request.put("cvv2", CVV2);
        //
        request.put("ecomind", "E");

//        System.out.println("%%%%%%%%%%%%% TABISH %%%%%%%%%%%%%%%");
        // Create the REST client
        CardConnectRestClient client = new CardConnectRestClient(EndPoint, UserName, Password);
//        System.out.println("%%%%%%%%%%%%% TABISH 1111 %%%%%%%%%%%%%%%");
        // Send an AuthTransaction request
        JSONObject response = client.authorizeTransaction(request);
        if (response == null) {
            System.out.println("RESPONSE IS NULL IN CC PAYMENT!!");
            return new String[]{""};
        }
//        System.out.println("%%%%%%%%%%%%% TABISH 22222 %%%%%%%%%%%%%%%" + response.toString());
        System.out.println("***************** Auth Transaction RESPONSE START **************************");
        // Handle response
        Set<String> keys = response.keySet();
        for (String key : keys)
            System.out.println(key + ": " + response.get(key));

        System.out.println("***************** Auth Transaction RESPONSE END **************************");

        ResponseText = (String) response.get("resptext");
        CVVResponse = (String) response.get("cvvresp");
        ResponseCode = (String) response.get("respcode");
        EntryMode = (String) response.get("entrymode");
        AuthCode = (String) response.get("authcode");
        ResponseProc = (String) response.get("respproc");
        ResponseStatus = (String) response.get("respstat");
        RetrieveReference = (String) response.get("retref");
        Expiry = (String) response.get("expiry");
        AVSResponse = (String) response.get("avsresp");
        Receipt = (String) response.get("receipt");
        BinType = (String) response.get("bintype");
        Amount = (String) response.get("amount");
        AccountNo = (String) response.get("account");
        orderId = (String) response.get("orderId");
        commCard = (String) response.get("commCard");


        System.out.println("\n****** Ending Authorization Request ******");

        return new String[]{ResponseText, CVVResponse, ResponseCode, EntryMode, AuthCode, ResponseProc, ResponseStatus,
                RetrieveReference, Expiry, AVSResponse, Receipt, BinType, Amount, AccountNo, orderId, commCard};
    }

    private static String[] authTransaction(String EndPoint, String UserName, String Password, String MerchantId,
                                            String myToken, String myExpiryDate, double amount, String currency,
                                            String name, String CVV2) {
        System.out.println("\nSending CC Insurance Authorization Request ........");

        String ResponseText;
        String CVVResponse;
        String ResponseCode;
        String EntryMode;
        String AuthCode;
        String ResponseProc;
        String ResponseStatus;
        String RetrieveReference;
        String Expiry;
        String AVSResponse;
        String Receipt;
        String BinType;
        String Amount;
        String AccountNo;
        String orderId;
        String commCard;

        // Create Authorization Transaction request
        JSONObject request = new JSONObject();
        // Merchant ID
        request.put("merchid", MerchantId);
        // Card Number
        request.put("account", myToken);
        // Card Expiry
        request.put("expiry", myExpiryDate);
        // Transaction amount
        request.put("amount", amount);
        // Transaction currency
        request.put("currency", currency);
        // Cardholder Name
        request.put("name", name);
        // Return a token for this card number
        request.put("tokenize", "Y");
        // Print Receipt
        request.put("receipt", "Y");
        //Capture request
        request.put("capture", "Y");
        //CVV2
        request.put("cvv2", CVV2);
        //
        request.put("ecomind", "E");

        //System.out.println("%%%%%%%%%%%%% TABISH %%%%%%%%%%%%%%%");
        // Create the REST client
        CardConnectRestClient client = new CardConnectRestClient(EndPoint, UserName, Password);
        //System.out.println("%%%%%%%%%%%%% TABISH 1111 %%%%%%%%%%%%%%%");
        // Send an AuthTransaction request
        JSONObject response = client.authorizeTransaction(request);
        //System.out.println("%%%%%%%%%%%%% TABISH 22222 %%%%%%%%%%%%%%%");
        System.out.println("***************** Auth CC Insurance Transaction RESPONSE START **************************");
        // Handle response
        Set<String> keys = response.keySet();
        for (String key : keys)
            System.out.println(key + ": " + response.get(key));

        System.out.println("***************** Auth CC Insurance Transaction RESPONSE END **************************");

        ResponseText = (String) response.get("resptext");
        CVVResponse = (String) response.get("cvvresp");
        ResponseCode = (String) response.get("respcode");
        EntryMode = (String) response.get("entrymode");
        AuthCode = (String) response.get("authcode");
        ResponseProc = (String) response.get("respproc");
        ResponseStatus = (String) response.get("respstat");
        RetrieveReference = (String) response.get("retref");
        Expiry = (String) response.get("expiry");
        AVSResponse = (String) response.get("avsresp");
        Receipt = (String) response.get("receipt");
        BinType = (String) response.get("bintype");
        Amount = (String) response.get("amount");
        AccountNo = (String) response.get("account");
        orderId = (String) response.get("orderId");
        commCard = (String) response.get("commCard");


        System.out.println("\n****** Ending CC Insurance Authorization Request ******");

        return new String[]{ResponseText, CVVResponse, ResponseCode, EntryMode, AuthCode, ResponseProc, ResponseStatus,
                RetrieveReference, Expiry, AVSResponse, Receipt, BinType, Amount, AccountNo, orderId, commCard};
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }

    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Connection getConnection() {
        try {
            try {
                //Class.forName("org.mariadb.jdbc.Driver").newInstance();
                Class.forName("org.mysql.jdbc.Driver").newInstance();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Connection connection = DriverManager.getConnection("jdbc:mysql://3.237.15.112:33306/oe?user=rovermdadmin&password=atyu!ioujy1986");
//            Connection connection = DriverManager.getConnection("jdbc:mysql://54.167.174.84/oe?user=909090XXXZZZ1&password=990909090909ABC1");
            return connection;
        } catch (Exception e) {
            System.out.println("Connection Exception 33306 --> " + e.getMessage());
            return null;
        }
    }

    public String[] InquireTransaction(String Values, int ClientId, Connection conn) {
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

        String Message = "";

        String FirstName = "";
        String LastName = "";
        String Address = "";
        String City = "";
        String State = "";
        String ZipCode = "";
        String Country = "";
        String Amount = "";
        String myToken = "";
        String myExpiryDate = "";
        String Currency = "";
        String CVV2 = "";

        String AuthString[];
        try {
            //Connection conn = getConnection();

            String[] splitValues = decrypt(Values).split("\\^");
/*            System.out.println("FirstName : " + splitValues[0]);
            System.out.println("LastName: " + splitValues[1]);
            System.out.println("City: " + splitValues[2]);
            System.out.println("State: " + splitValues[3]);
            System.out.println("ZipCode: " + splitValues[4]);
            System.out.println("Country: " + splitValues[5]);
            System.out.println("Address: " + splitValues[6]);
            System.out.println("Amount: " + splitValues[7]);
            System.out.println("myToken: " + splitValues[8]);
            System.out.println("myExpiryDate: " + splitValues[9]);
            System.out.println("CVV2: " + splitValues[10]);*/

            FirstName = splitValues[0];
            LastName = splitValues[1];
            City = splitValues[2];
            State = splitValues[3];
            ZipCode = splitValues[4];
            Country = splitValues[5];
            Address = splitValues[6];
            Amount = splitValues[7];
            myToken = splitValues[8];
            myExpiryDate = splitValues[9];
            CVV2 = splitValues[10];

//            System.out.println("FirstName : " + FirstName);
//            System.out.println("LastName: " + LastName);
//            System.out.println("City: " + City);
//            System.out.println("State: " + State);
//            System.out.println("ZipCode: " + ZipCode);
//            System.out.println("Country: " + Country);
//            System.out.println("Address: " + Address);
//            System.out.println("Amount: " + Amount);
//            System.out.println("myToken: " + myToken);
//            System.out.println("myExpiryDate: " + myExpiryDate);
//            System.out.println("CVV2: " + CVV2);

            UtilityHelper helper = new UtilityHelper();
            String[] AuthConnect = helper.getAuthConnect(conn, FlagType, ClientId);
            ENDPOINT = AuthConnect[0];
            USERNAME = AuthConnect[1];
            PASSWORD = AuthConnect[2];
            MerchantId = AuthConnect[3];
            Currency = AuthConnect[4];

/*            System.out.println("ENDPOINT : " + ENDPOINT);
            System.out.println("USERNAME: " + USERNAME);
            System.out.println("PASSWORD: " + PASSWORD);
            System.out.println("MerchantId: " + MerchantId);
            System.out.println("Currency: " + Currency);*/

            String Name = FirstName + "" + LastName;

            AuthString = authTransaction(ENDPOINT, USERNAME, PASSWORD, MerchantId, myToken, myExpiryDate, Amount, Currency, Name, City, State, Country, ZipCode, CVV2, Address);
//            System.out.println("AuthString: " + Arrays.toString(AuthString));
        } catch (Exception e) {
            Message = e.getMessage() + " <br> ";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                Message = Message + e.getStackTrace()[i] + " ******* <br>";
            }
            return new String[]{Message};
        }
        if (!AuthString.equals(""))
            return new String[]{AuthString[0], AuthString[1], AuthString[2], AuthString[3], AuthString[4], AuthString[5], AuthString[6], AuthString[7], AuthString[8], AuthString[9], AuthString[10], AuthString[11], AuthString[12], AuthString[13], AuthString[14], AuthString[15]};
        else
            return new String[]{Arrays.toString(AuthString)};
    }

    /**
     * Refund Transaction REST Example
     *
     * @param retref
     */
    public String[] refundTransaction(String EndPoint, String UserName, String Password, String MerchantId,
                                      String amount, String currency, String retref) {
        System.out.println("\nRefund Transaction Request");
        String RespProc;
        String Amount;
        String ResponseText;
        String OrderId;
        String Receipt;
        String RefundRetRef;
        String RespStat;
        String RespCode;
        String RefundMerchantId;
/*        Void Transaction Request
        authcode: REVERS
        respproc: RPCT
        amount: 0.00
        resptext: Approval
        orderId:
        currency: USD
        receipt: {"dateTime":"20210301111757","dba":"Frontline ER","address2":"Dallas, TX","phone":"2144999555","footer":"","nameOnCard":"TabishTest","address1":"","orderNote":"","header":"","items":""}
        retref: 060307040677
        respstat: A
        respcode: 000
        merchid: 496407709880*/
//        System.out.println("***************** Refund Transaction START **************************");
/*        System.out.println("EndPoint " + EndPoint);
        System.out.println("UserName " + UserName);
        System.out.println("Password " + Password);
        System.out.println("MerchantId " + MerchantId);
        System.out.println("amount " + amount);
        System.out.println("retref " + retref);*/


        // Create Update Transaction request
        JSONObject request = new JSONObject();
        // Merchant ID
        request.put("merchid", MerchantId);
        // Transaction amount
        request.put("amount", amount);
        // Transaction currency
        request.put("currency", currency);
        // Return Reference code from authorization request
        request.put("retref", retref);

        // Create the CardConnect REST client
        CardConnectRestClient client = new CardConnectRestClient(EndPoint, UserName, Password);

        // Send an refundTransaction request
        JSONObject response = client.refundTransaction(request);

//        System.out.println("***************** Refund Transaction RESPONSE START **************************");
        // Handle response
/*        Set<String> keys = response.keySet();
        for (String key : keys)
            System.out.println(key + ": " + response.get(key));
        System.out.println("***************** Refund Transaction RESPONSE END **************************");*/

/*        respproc: PPS
        amount: 11.00
        resptext: Approval
        orderId:
        receipt: {"dateTime":"20210217172531","dba":"Victoria ER","address2":"VICTORIA, TX","phone":"3615731500","footer":"","nameOnCard":"Abidtest2","address1":"","orderNote":"","header":"","items":""}
        retref: 056536464911
        respstat: A
        respcode: 00
        merchid: 496406685883*/
        RespProc = (String) response.get("respproc");
        Amount = (String) response.get("amount");
        ResponseText = (String) response.get("resptext");
        OrderId = (String) response.get("orderId");
        Receipt = (String) response.get("receipt");
        RefundRetRef = (String) response.get("retref");
        RespStat = (String) response.get("respstat");
        RespCode = (String) response.get("respcode");
        RefundMerchantId = (String) response.get("merchid");

//        System.out.println("***************** Refund Transaction END **************************");

        return new String[]{RespProc, Amount, ResponseText, OrderId, Receipt, RefundRetRef, RespStat, RespCode, RefundMerchantId};
    }

    public String[] voidTransaction(String EndPoint, String UserName, String Password, String MerchantId,
                                    String amount, String currency, String retref) {
        System.out.println("\nVoid Transaction Request");
        String RespProc;
        String Amount;
        String ResponseText;
        String OrderId;
        String Receipt;
        String RefundRetRef;
        String RespStat;
        String RespCode;
        String RefundMerchantId;

/*        System.out.println("***************** Void Transaction START **************************");
        System.out.println("EndPoint " + EndPoint);
        System.out.println("UserName " + UserName);
        System.out.println("Password " + Password);
        System.out.println("MerchantId " + MerchantId);
        System.out.println("amount " + amount);
        System.out.println("retref " + retref);*/


        // Create Update Transaction request
        JSONObject request = new JSONObject();
        // Merchant ID
        request.put("merchid", MerchantId);
        // Transaction amount
        request.put("amount", "0");
        // Transaction currency
//        request.put("currency", "USD");
        // Return Reference code from authorization request
        request.put("retref", retref);

        //request.put("setlstat", "Authorized");

        // Create the CardConnect REST client
        CardConnectRestClient client = new CardConnectRestClient(EndPoint, UserName, Password);

        // Send a voidTransaction request
        JSONObject response = client.voidTransaction(request);

/*        System.out.println("***************** Void Transaction RESPONSE START **************************");
        // Handle response
        Set<String> keys = response.keySet();
        for (String key : keys)
            System.out.println(key + ": " + response.get(key));
        System.out.println("***************** Void Transaction RESPONSE END **************************");*/

        RespProc = (String) response.get("respproc");
        Amount = (String) response.get("amount");
        ResponseText = (String) response.get("resptext");
        OrderId = (String) response.get("orderId");
        Receipt = (String) response.get("receipt");
        RefundRetRef = (String) response.get("retref");
        RespStat = (String) response.get("respstat");
        RespCode = (String) response.get("respcode");
        RefundMerchantId = (String) response.get("merchid");

//        System.out.println("***************** Void Transaction END **************************");

        return new String[]{RespProc, Amount, ResponseText, OrderId, Receipt, RefundRetRef, RespStat, RespCode, RefundMerchantId};
    }

    public String[] InquireTransaction(int ClientId, Connection conn, String myToken, String myExpiryDate, double amount, String nameOnCard, String CVV2) {
        String ENDPOINT = "";
        String USERNAME = "";
        String PASSWORD = "";
        String MerchantId = "";
        String Message = "";
        String Currency = "";

        //Type Flag
        // 0 - Sandbox
        //int FlagType = 0;
        // 1 - Dev
        //int FlagType = 1;
        // 2 - Prod
        int FlagType = 2;

        String AuthString[];
        try {
            UtilityHelper helper = new UtilityHelper();
            String[] AuthConnect = helper.getAuthConnect(conn, FlagType, ClientId);
            ENDPOINT = AuthConnect[0];
            USERNAME = AuthConnect[1];
            PASSWORD = AuthConnect[2];
            MerchantId = AuthConnect[3];
            Currency = AuthConnect[4];

            AuthString = authTransaction(ENDPOINT, USERNAME, PASSWORD, MerchantId, myToken, myExpiryDate, amount, Currency, nameOnCard, CVV2);
        } catch (Exception e) {
            Message = e.getMessage() + "<br>";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                Message = Message + e.getStackTrace()[i] + " AAAAAAAAAAAAAa ******* <br>";
            }
            return new String[]{Message};
        }
        return new String[]{AuthString[0], AuthString[1], AuthString[2], AuthString[3], AuthString[4], AuthString[5], AuthString[6], AuthString[7], AuthString[8], AuthString[9], AuthString[10], AuthString[11], AuthString[12], AuthString[13], AuthString[14], AuthString[15]};
    }
}
