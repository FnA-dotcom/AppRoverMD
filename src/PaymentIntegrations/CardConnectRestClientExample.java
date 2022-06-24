package PaymentIntegrations;
/*
Copyright 2014, CardConnect (http://www.cardconnect.com)

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
PERFORMANCE OF THIS SOFTWARE.
*/

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

/**
 * Client Example showing various service request calls to CardConnect using REST
 */
@SuppressWarnings("Duplicates")
public class CardConnectRestClientExample {
    //private static final String ENDPOINT = "https://sitename.cardconnect.com:6443/cardconnect/rest/";
    //private static final String ENDPOINT = "https://fts-uat.cardconnect.com/cardconnect/rest/";
/*    private static final String ENDPOINT = "https://boltgw-uat.cardconnect.com/cardconnect/rest/";
    private static final String USERNAME = "testing";
    private static final String PASSWORD = "testing123";*/
    //Live Excel ER
/*    private static final String ENDPOINT = "https://boltgw.cardconnect.com/cardconnect/rest/";
    private static final String USERNAME = "excelero";
    private static final String PASSWORD = "fg!DpNtp@yDhbP5#rZyb";   */
//Test Prod
/*    private static final String ENDPOINT = "https://boltgw.cardconnect.com/cardconnect/rest/";
    private static final String USERNAME = "esendprodtst";
    private static final String PASSWORD = "uRai!ErwD3TjwUM$A#NTP8";*/

    //Victoria
//    private static final String ENDPOINT = "https://boltgw.cardconnect.com/cardconnect/rest/";
//    private static final String USERNAME = "victoria";
//    private static final String PASSWORD = "sHC!x7pL!3YGDu!jdDk4";

    //Frontline-ER -- White Rock
/*    private static final String ENDPOINT = "https://boltgw.cardconnect.com/cardconnect/rest/";
    private static final String USERNAME = "frontlin";
    private static final String PASSWORD = "w6vu$HGc$5VeW5$b7uwy";*/

    //Frontline-ER -- Richmond
/*    private static final String ENDPOINT = "https://boltgw.cardconnect.com/cardconnect/rest/";
    private static final String USERNAME = "frontlin";
    private static final String PASSWORD = "avZ!dg5@sAF#udR4vUWh";*/

    //Golden Triangle -GTEC-Orange
    private static final String ENDPOINT = "https://boltgw.cardconnect.com/cardconnect/rest/";
    private static final String USERNAME = "goldentr";
    private static final String PASSWORD = "Cu!!!U32cmXb9kHkaFWS";
    //E-CHK
//    private static final String USERNAME = "goldeapi";
//    private static final String PASSWORD = "cb!h5R9B@tq3gFQ#AjRk";
    //EXCEL ER ODESSA
/*    private static final String ENDPOINT = "https://boltgw.cardconnect.com/cardconnect/rest/";
    private static final String USERNAME = "excelero";
    private static final String PASSWORD = "fg!DpNtp@yDhbP5#rZyb";*/
//EXCEL ER Long View
/*    private static final String ENDPOINT = "https://boltgw.cardconnect.com/cardconnect/rest/";
    private static final String USERNAME = "exceler1";
    private static final String PASSWORD = "3wj!DD5@XYF#H5HD6ZXZ";*/
// NACOGDOCHES
/*    private static final String ENDPOINT = "https://boltgw.cardconnect.com/cardconnect/rest/";
    private static final String USERNAME = "excenaco";
    private static final String PASSWORD = "y0t!oaGr@2lU7#z7bGZh";*/

    public static void main(String args[]) {
        // Send an Auth Transaction request
//        String retref = authTransaction();
//        System.out.println(" *********** AFTER TRANSACTION  " + retref);


        // Void transaction
        //voidTransaction("060307040677");

        // Send an Auth Transaction w/ user fields
        //String retref = authTransactionWithUserFields();
        // Inquire transaction
        //inquireTransaction(retref);

        // Send an Auth w/ Capture
//        String retref = authTransactionWithCapture();
//        System.out.println(" *********** AFTER TRANSACTION  " + retref);
//         Void
        //voidTransaction(retref);
        //voidTransaction(retref);
        //voidTransaction("687650046243");
        //voidTransaction("187652252094");
        //voidTransaction("687089052291");
        //voidTransaction("187382052905");
        //voidTransaction("729190053512");
        //voidTransaction("229156053781");
        // Odessa 1-Sept-2021
        //voidTransaction("244975230701");

        // Settlement Status
        //settlementStatusTransaction();

        //Refund
        //refundTransaction(retref);
//        refundTransaction("094716758774");
        //refundTransaction("095744069739");
        //refundTransaction("015541159568");
        //refundTransaction("054934250973");
        //refundTransaction("606856053628");
        //Victoria - 06-07-2021
        //* TRIPLE PAYMENT ISSUE
        //refundTransaction("150375249241");
        //refundTransaction("150933749351");
        //3-feb-2022
        //voidTransaction("034128062194");

        //5-May-2022-- Voiding correct transaction of Orange. It was made from check. Now, they want us to cash back the customer
        voidTransaction("094298078540");

        //Odessa - 06-07-2021
        //Double Payment Issue
        //refundTransaction("681615072466");
        //Richmond - 08-26-2021
        //Double Payment Issue
        //refundTransaction("228934285376");
        //refundTransaction("236481740296");
        //Victoria - 27-Aug-21
        //refundTransaction("234159063297");
        //Odessa 1-Sept-2021 -- Not performed due to same day. It should be void
        //refundTransaction("244975230701");
        //LongView 1-Sept-21
        //refundTransaction("214095201501");
        //Naco 1-Sept-21
        //refundTransaction("186934746155");
        //Front-Line WHITEROCK-Sept-21
        //refundTransaction("229909244473");
        //Frontline-Richmond -- 02-SEPT-21
        //refundTransaction("232772254406");
        //refundTransaction("244929756183");


        //Victoria 21-Sept-21
//        refundTransaction("258237760048");
//        refundTransaction("258821060254");
//        refundTransaction("258774259988");
//        refundTransaction("758514059872");
        //refundTransaction("258224260152");

        //Victoria 23-Sept-21
        //refundTransaction("266377039522");


        //Abid
        //refundTransaction("048309062731");
        //refundTransaction("057101043679");
        //refundTransaction("060307040677");
        //refundTransaction("183323764771");


        // Send normal Auth
        //retref = authTransaction();

        // Explicit capture
        //captureTransaction(retref);


        // Deposit Status
        //depositTransaction();

        // Auth with Profile
        //String profileid = authTransactionWithProfile();

        // Get profile
        //getProfile(profileid);

        // Delete profile
        //deleteProfile(profileid);

        // Create profile
        //addProfile();

        //Creating Token
        //String token = gettoken();
        //System.out.println("TOKEN " + token);
        //String retref2 = authTransaction2(token);
        //String retref2 = authTransaction2();
        //System.out.println(" *********** AFTER ACH TRANSACTION " + retref2);


        //Capture ACH
        //String retRef = captureACHRRequest();
        //System.out.println("ACH " + retRef);

/*        POST /cardsecure/api/v1/ccn/tokenize HTTP/1.1
        Content-Type: application/json

        {
            "account" : "4444333322221111"
        }*/

        //Inquire Check Transactions
        //inquireTransaction("293203064497");
    }

    public static String gettoken() {

        JSONObject request = new JSONObject();
        //"account" : "<routing number>/<account number>"
        request.put("account", "031000503/123456798");
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
    }


    public static String captureACHRRequest() {
        System.out.println("\nCapture ACH Response");

        // Create Authorization Transaction request
        JSONObject request = new JSONObject();
        // Merchant ID
        request.put("merchid", "496407709880");

        request.put("account", "9117099959230304");

        request.put("accttype", "ECHK");

        // Transaction amount
        request.put("amount", "5.00");

        request.put("ecomind", "E");

        //Capture request
        request.put("capture", "Y");

        // Create the REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        // Send an AuthTransaction request
        JSONObject response = client.authorizeTransaction(request);

        // Handle response
        Set<String> keys = response.keySet();
        for (String key : keys)
            System.out.println(key + ": " + response.get(key));

        return (String) response.get("retref");
    }

    /**
     * Authorize Transaction REST Example
     *
     * @return
     */
    public static String authTransaction() {
        System.out.println("\nAuthorization Request");

        // Create Authorization Transaction request
        JSONObject request = new JSONObject();
        // Merchant ID
        //request.put("merchid", "496160873888");

        //request.put("merchid", "496407339886");
        request.put("merchid", "496407339886");
        // Card Type
        //request.put("accttype", "VI");
        // Card Number
        //VISA Card Approved
        //request.put("account", "4387750101010101");
        request.put("account", "9497673769787388");
        //request.put("account", "9477709629051443");
        // Card Expiry 4000065433421984
        request.put("expiry", "0624");
        // Card CCV2
        request.put("cvv2", "390");
        // Transaction amount
        request.put("amount", "0.11");
        // Transaction currency
        request.put("currency", "USD");
        // Order ID
        //request.put("orderid", "12345");
        // Cardholder Name
        request.put("name", "TAB");
        // Cardholder Address
        request.put("Street", "2616 ALICE ST");
        // Cardholder City
        request.put("city", "ODESSA");
        // Cardholder State
        request.put("region", "-");
        // Cardholder Country
        request.put("country", "Canada");
        // Cardholder Zip-Code
        request.put("postal", "1234");
        // Return a token for this card number
        request.put("tokenize", "Y");
        // Print Receipt
        request.put("receipt", "Y");
        //Capture request
        request.put("capture", "Y");

        request.put("ecomind", "E");

        // Create the REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        // Send an AuthTransaction request
        JSONObject response = client.authorizeTransaction(request);

        // Handle response
        Set<String> keys = response.keySet();
        for (String key : keys)
            System.out.println(key + ": " + response.get(key));

        return (String) response.get("retref");
    }


    /**
     * Authorize Transaction REST Example
     *
     * @param token
     * @return
     */
    public static String authTransaction2(String token) {
        System.out.println("\nAuthorization Request");

        // Create Authorization Transaction request
        JSONObject request = new JSONObject();
        // Merchant ID
        request.put("merchid", "BCX101076215512");
        // Card Number
        //VISA Card Approved
        request.put("account", token);
        // Card Expiry
        //request.put("expiry", "0914");
        // Card CCV2
        //request.put("cvv2", "112");
        // Transaction amount
        request.put("amount", "246.01");
        // Transaction currency
        request.put("currency", "USD");
        // Order ID
        //request.put("orderid", "12345");
        // Cardholder Name
        request.put("name", "Test User");
        // Cardholder Address
        request.put("Street", "123 Test St");
        // Cardholder City
        request.put("city", "TestCity");
        // Cardholder State
        request.put("region", "TestState");
        // Cardholder Country
        request.put("country", "US");
        // Cardholder Zip-Code
        request.put("postal", "11111");
        // Return a token for this card number
        request.put("tokenize", "Y");

        request.put("accttype", "ECHK");

        request.put("ecomind", "E");

        request.put("achDescription", "310154");
        request.put("achEntryCode", "WEB");

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

        return (String) response.get("retref");
    }

    /**
     * Authorize Transaction with User Fields REST Example
     *
     * @return
     */
    public static String authTransactionWithUserFields() {
        System.out.println("\nAuthorization With User Fields Request");

        // Create Authorization Transaction request
        JSONObject request = new JSONObject();
        // Merchant ID
        request.put("merchid", "496160873888");
        // Card Type
        request.put("accttype", "VI");
        // Card Number
        request.put("account", "4000065433421984");
        // Card Expiry
        request.put("expiry", "1220");
        // Card CCV2
        request.put("cvv2", "112");
        // Transaction amount
        request.put("amount", "100");
        // Transaction currency
        request.put("currency", "USD");
        // Order ID
        request.put("orderid", "12345");
        // Cardholder Name
        request.put("name", "Test User");
        // Cardholder Address
        request.put("Street", "123 Test St");
        // Cardholder City
        request.put("city", "TestCity");
        // Cardholder State
        request.put("region", "TestState");
        // Cardholder Country
        request.put("country", "US");
        // Cardholder Zip-Code
        request.put("postal", "11111");
        // Return a token for this card number
        request.put("tokenize", "Y");

        // Create user fields
        JSONArray fields = new JSONArray();
        JSONObject field = new JSONObject();
        field.put("Field1", "Value1");
        fields.add(field);
        request.put("userfields", fields);

        // Create the REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        // Send an AuthTransaction request
        JSONObject response = client.authorizeTransaction(request);

        // Handle response
        Set<String> keys = response.keySet();
        for (String key : keys)
            System.out.println(key + ": " + response.get(key));

        return (String) response.get("retref");
    }


    /**
     * Authorize Transaction With Capture REST Example
     *
     * @return
     */
    public static String authTransactionWithCapture() {
        System.out.println("\nAuthorization With Capture Request");

        // Create Authorization Transaction request
        JSONObject request = new JSONObject();
        // Merchant ID
        request.put("merchid", "496160873888");
        // Card Type
        //request.put("accttype", "VI");
        // Card Number
        //request.put("account", "9036412947515678");
        request.put("account", "6011000995500000");
        // Card Expiry
        request.put("expiry", "1223");
        // Card CCV2
        request.put("cvv2", "112");
        // Transaction amount
        request.put("amount", "9.00");
        // Transaction currency
        request.put("currency", "USD");
        // Order ID
        //request.put("orderid", "12345");
        // Cardholder Name
        request.put("name", "Test User");
        // Cardholder Address
        request.put("Street", "123 Test St");
        // Cardholder City
        request.put("city", "TestCity");
        // Cardholder State
        request.put("region", "TestState");
        // Cardholder Country
        request.put("country", "US");
        // Cardholder Zip-Code
        request.put("postal", "11111");
        // Return a token for this card number
        request.put("tokenize", "Y");
        // Capture auth
        request.put("capture", "Y");
        // Print Receipt
        request.put("receipt", "Y");
        //
        request.put("ecomind", "E");


        // Create the REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        // Send an AuthTransaction request
        JSONObject response = client.authorizeTransaction(request);

        // Handle response
        Set<String> keys = response.keySet();
        for (String key : keys)
            System.out.println(key + ": " + response.get(key));

        return (String) response.get("retref");
    }


    /**
     * Authorize Transaction with Profile REST Example
     *
     * @return
     */
    public static String authTransactionWithProfile() {
        System.out.println("\nAuthorization With Profile Request");

        // Create Authorization Transaction request
        JSONObject request = new JSONObject();
        // Merchant ID
        request.put("merchid", "496160873888");
        // Card Type
        request.put("accttype", "VI");
        // Card Number
        request.put("account", "4000065433421984");
        // Card Expiry
        request.put("expiry", "1220");
        // Card CCV2
        request.put("cvv2", "112");
        // Transaction amount
        request.put("amount", "111695");
        // Transaction currency
        request.put("currency", "USD");
        // Order ID
        request.put("orderid", "12345");
        // Cardholder Name
        request.put("name", "Test User");
        // Cardholder Address
        request.put("Street", "123 Test St");
        // Cardholder City
        request.put("city", "TestCity");
        // Cardholder State
        request.put("region", "TestState");
        // Cardholder Country
        request.put("country", "US");
        // Cardholder Zip-Code
        request.put("postal", "11111");
        // Return a token for this card number
        request.put("tokenize", "Y");
        // Create Profile
        request.put("profile", "Y");

        // Create the REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        // Send an AuthTransaction request
        JSONObject response = client.authorizeTransaction(request);

        // Handle response
        Set<String> keys = response.keySet();
        for (String key : keys)
            System.out.println(key + ": " + response.get(key));

        return (String) response.get("profileid");
    }


    /**
     * Capture Transaction REST Example
     *
     * @param retref
     */
    public static void captureTransaction(String retref) {
        System.out.println("\nCapture Transaction Request");

        // Create Authorization Transaction request
        JSONObject request = new JSONObject();
        // Merchant ID
        request.put("merchid", "496160873888");
        // Transaction amount
        request.put("amount", "111695");
        // Transaction currency
        request.put("currency", "USD");
        // Order ID
        request.put("retref", retref);
        // Purchase Order Number
        request.put("ponumber", "12345");
        // Tax Amount
        request.put("taxamnt", "007");
        // Ship From ZipCode
        request.put("shipfromzip", "11111");
        // Ship To Zip
        request.put("shiptozip", "11111");
        // Ship to County
        request.put("shiptocountry", "US");
        // Cardholder Zip-Code
        request.put("postal", "11111");

        // Line item details
        JSONArray items = new JSONArray();
        // Singe line item
        JSONObject item = new JSONObject();
        item.put("lineno", "1");
        item.put("material", "12345");
        item.put("description", "Item Description");
        item.put("upc", "0001122334455");
        item.put("quantity", "5");
        item.put("uom", "each");
        item.put("unitcost", "020");
        items.add(item);
        // Add items to request
        request.put("items", items);

        // Authorization Code from auth response
        request.put("authcode", "0001234");
        // Invoice ID
        request.put("invoiceid", "0123456789");
        // Order Date
        request.put("orderdate", "20140131");
        // Total Order Freight Amount
        request.put("frtamnt", "1");
        // Total Duty Amount
        request.put("dutyamnt", "1");

        // Create the CardConnect REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        // Send a captureTransaction request
        JSONObject response = client.captureTransaction(request);

        // Handle response
        Set<String> keys = response.keySet();
        for (String key : keys)
            System.out.println(key + ": " + response.get(key));
    }


    /**
     * Void Transaction REST Example
     *
     * @param retref
     */
    public static void voidTransaction(String retref) {
        System.out.println("\nVoid Transaction Request");

        // Create Update Transaction request
        JSONObject request = new JSONObject();
        // Merchant ID
        //request.put("merchid", "496407339886");
        //Odessa
        //request.put("merchid", "496407339886");
        //request.put("merchid", "496406685883");
        //Orange-ECHK
        request.put("merchid", "496405167883");
        // Transaction amount
        request.put("amount", "0");
        // Transaction currency
//        request.put("currency", "USD");
        // Return Reference code from authorization request
        request.put("retref", retref);

        //request.put("setlstat", "Authorized");

        // Create the CardConnect REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        // Send a voidTransaction request
        JSONObject response = client.voidTransaction(request);

        // Handle response
        Set<String> keys = response.keySet();
        for (String key : keys)
            System.out.println(key + ": " + response.get(key));
    }


    /**
     * Refund Transaction REST Example
     *
     * @param retref
     */
    public static void refundTransaction(String retref) {
        System.out.println("\nRefund Transaction Request");

        // Create Update Transaction request
        JSONObject request = new JSONObject();
        // Merchant ID
        //request.put("merchid", "496407356880");
        //request.put("merchid", "496407339886");
        //Richmond
        //request.put("merchid", "496409008885");
        //Victoria
        request.put("merchid", "496406685883");
        //Odessa
        //request.put("merchid", "496407339886");
        //LongView
        //request.put("merchid", "496409187887");
        //Naco
        //request.put("merchid", "496412107880");
        //WhiteRock
        //request.put("merchid", "496407709880");
        //Testing Device
        //request.put("merchid", "496407356880");
        // Transaction amount
        request.put("amount", "250.00");
        // Transaction currency
        request.put("currency", "USD");
        // Return Reference code from authorization request
        request.put("retref", retref);

        // Create the CardConnect REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        // Send an refundTransaction request
        JSONObject response = client.refundTransaction(request);

        // Handle response
        Set<String> keys = response.keySet();
        for (String key : keys)
            System.out.println(key + ": " + response.get(key));
    }


    /**
     * Inquire Transaction REST Example
     *
     * @param retref
     */
    public static void inquireTransaction(String retref) {
        System.out.println("\nInquire Transaction Request");
        //Orange
        String merchid = "496405167883";

        // Create the CardConnect REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        // Send an inquire Transaction request
        JSONObject response = client.inquireTransaction(merchid, retref);

        // Handle response
        if (response != null) {
            Set<String> keys = response.keySet();
            for (String key : keys)
                System.out.println(key + ": " + response.get(key));
        }
    }


    /**
     * Settlement Status REST Example
     */
    public static void settlementStatusTransaction() {
        System.out.println("\nSettlement Status Transaction Request");
        // Merchant ID
        String merchid = "496160873888";
        String date = "0404";

        // Create the CardConnect REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        JSONArray responses = client.settlementStatus(merchid, date);
        //JSONArray responses = client.settlementStatus(null, null);

        // Handle response
        if (responses != null) {
            for (int i = 0; i < responses.size(); i++) {
                JSONObject response = (JSONObject) responses.get(i);
                Set<String> keys = response.keySet();
                for (String key : keys) {
                    if ("txns".equals(key)) {
                        System.out.println("transactions: ");
                        JSONArray txns = (JSONArray) response.get(key);
                        for (int j = 0; j < txns.size(); j++) {
                            System.out.println("  ===");
                            JSONObject txn = (JSONObject) txns.get(j);
                            Set<String> txnkeys = txn.keySet();
                            for (String txnkey : txnkeys)
                                System.out.println("  " + txnkey + ": " + txn.get(txnkey));
                        }
                    } else {
                        System.out.println(key + ": " + response.get(key));
                    }
                }
            }
        }
    }


    /**
     * Deposit Transaction REST Example
     */
    //ACH
    public static void depositTransaction() {
        System.out.println("\nDeposit Transaction Request");
        // Merchant ID
        String merchid = "496400000840";
        // Date
        String date = "20140131";

        // Create the CardConnect REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        JSONObject response = client.depositStatus(merchid, date);

        // Handle response
        if (response != null) {
            Set<String> keys = response.keySet();
            for (String key : keys) {
                if ("txns".equals(key)) {
                    System.out.println("transactions: ");
                    JSONArray txns = (JSONArray) response.get(key);
                    for (int i = 0; i < txns.size(); i++) {
                        System.out.println("  ===");
                        JSONObject txn = (JSONObject) txns.get(i);
                        Set<String> txnkeys = txn.keySet();
                        for (String txnkey : txnkeys)
                            System.out.println("  " + txnkey + ": " + txn.get(txnkey));
                    }
                } else {
                    System.out.println(key + ": " + response.get(key));
                }
            }
        }
    }


    /**
     * Get Profile REST Example
     *
     * @param profileid
     */
    private static void getProfile(String profileid) {
        System.out.println("\nGet Profile Request");
        // Merchant ID
        String merchid = "496160873888";
        // Account ID
        String accountid = "1";

        // Create the CardConnect REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        // Retrieve profile from Profile Service
        JSONArray response = client.profileGet(profileid, accountid, merchid);

        // Handle response
        if (response != null) {
            for (int i = 0; i < response.size(); i++) {
                JSONObject object = (JSONObject) response.get(i);
                Set<String> keys = object.keySet();
                for (String key : keys)
                    System.out.println(key + ": " + object.get(key));
            }
        }
    }


    /**
     * Delete Profile REST Example
     *
     * @param profileid
     */
    private static void deleteProfile(String profileid) {
        System.out.println("\nDelete Profile Request");
        // Merchant ID
        String merchid = "496400000840";
        String accountid = "";

        // Create the CardConnect REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        // Delete profile using Profile Service
        JSONObject response = client.profileDelete(profileid, accountid, merchid);

        // Handle response
        if (response != null) {
            Set<String> keys = response.keySet();
            for (String key : keys)
                System.out.println(key + ": " + response.get(key));
        }
    }


    /**
     * Add Profile REST Example
     */
    private static void addProfile() {
        System.out.println("\nAdd Profile Request");

        // Create Profile Request
        JSONObject request = new JSONObject();
        // Merchant ID
        request.put("merchid", "496400000840");
        // Default account
        request.put("defaultacct", "Y");
        // Card Number
        request.put("account", "4444333322221111");
        // Card Expiry
        request.put("expiry", "0914");
        // Cardholder Name
        request.put("name", "Test User");
        // Cardholder Address
        request.put("address", "123 Test St");
        // Cardholder City
        request.put("city", "TestCity");
        // Cardholder State
        request.put("region", "TestState");
        // Cardholder Country
        request.put("country", "US");
        // Cardholder Zip-Code
        request.put("postal", "11111");

        // Create the CardConnect REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        // Create profile using Profile Service
        JSONObject response = client.profileCreate(request);

        // Handle response
        Set<String> keys = response.keySet();
        for (String key : keys)
            System.out.println(key + ": " + response.get(key));
    }

    /**
     * Funding Request REST Example
     */
/*    public static void fundingRequest() {
        System.out.println("\nFunding Request");
        // Merchant ID
        String merchid = "123456789012";
        // Date
        String date = "0131";

        // Create the CardConnect REST client
        CardConnectRestClient client = new CardConnectRestClient(ENDPOINT, USERNAME, PASSWORD);

        JSONObject response = client.fundingRequest(merchid, date);

        // Handle response
        if (response != null) {
            Set<String> keys = response.keySet();
            for (String key : keys) {
                if ("txns".equals(key)) {
                    System.out.println("transactions: ");
                    JSONArray txns = (JSONArray) response.get(key);
                    for (int i=0; i<txns.size(); i++) {
                        System.out.println("  ===");
                        JSONObject txn = (JSONObject)txns.get(i);
                        Set<String> txnkeys = txn.keySet();
                        for (String txnkey : txnkeys)
                            System.out.println("  " + txnkey + ": " + txn.get(txnkey));
                    }
                } else {
                    System.out.println(key + ": " + response.get(key));
                }
            }
        }
    }*/
    private static void myFunc() {
        String strRet = "";
        String strJson = "";
        OutputStream os = null;


        try {
            URL url = new URL("https://fts-uat.cardconnect.com/cardconnect/rest");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", "ZCb8pPkXcZDVO0CIngLSFrBJgA/BYyUZIHT8zaj3MPg=");
            connection.setRequestMethod("PUT");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.connect();
            if (strJson != null && !"".equals(strJson.trim())) {
                os = connection.getOutputStream();
                os.write(strJson.getBytes("UTF-8"));
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                strRet += line;
            }


            String reply = strRet.trim();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
