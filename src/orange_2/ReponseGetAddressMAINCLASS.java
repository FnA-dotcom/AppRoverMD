package orange_2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * How to Add Text To Existing PDF in java
 * Example Using iText library - core java tutorial
 */
public class ReponseGetAddressMAINCLASS {

    public static void main(String[] args) throws Exception {

//        String s1="2019";
//        System.out.println(s1.substring(2,4));//returns v

        String reply = "";
        reply = "{ \"id\": \"ch_1Hn3SCEmy3qYdmGdS1UOGAWP\", \"object\": \"charge\", \"amount\": 300, \"amount_captured\": 300, \"amount_refunded\": 0, \"application\": null, \"application_fee\": null, \"application_fee_amount\": null, \"balance_transaction\": \"txn_1Hn3SDEmy3qYdmGdqF3HwnOW\", \"billing_details\": { \"address\": { \"city\": null, \"country\": null, \"line1\": null, \"line2\": null, \"postal_code\": null, \"state\": null }, \"email\": null, \"name\": null, \"phone\": null }, \"calculated_statement_descriptor\": \"Stripe\", \"captured\": true, \"created\": 1605278444, \"currency\": \"usd\", \"customer\": null, \"description\": \"Thisistest\", \"destination\": null, \"dispute\": null, \"disputed\": false, \"failure_code\": null, \"failure_message\": null, \"fraud_details\": { }, \"invoice\": null, \"livemode\": false, \"metadata\": { }, \"on_behalf_of\": null, \"order\": null, \"outcome\": { \"network_status\": \"approved_by_network\", \"reason\": null, \"risk_level\": \"normal\", \"risk_score\": 21, \"seller_message\": \"Payment complete.\", \"type\": \"authorized\" }, \"paid\": true, \"payment_intent\": null, \"payment_method\": \"card_1Hn3SCEmy3qYdmGdvaHjv7ob\", \"payment_method_details\": { \"card\": { \"brand\": \"visa\", \"checks\": { \"address_line1_check\": null, \"address_postal_code_check\": null, \"cvc_check\": \"pass\" }, \"country\": \"US\", \"exp_month\": 11, \"exp_year\": 2021, \"fingerprint\": \"ZaBHkNL6fAk28W73\", \"funding\": \"credit\", \"installments\": null, \"last4\": \"4242\", \"network\": \"visa\", \"three_d_secure\": null, \"wallet\": null }, \"type\": \"card\" }, \"receipt_email\": null, \"receipt_number\": null, \"receipt_url\": \"https://pay.stripe.com/receipts/acct_1HbXX1Emy3qYdmGd/ch_1Hn3SCEmy3qYdmGdS1UOGAWP/rcpt_INp6j939LLuo9n12CAipvfINnfVxKsy\", \"refunded\": false, \"refunds\": { \"object\": \"list\", \"data\": [ ], \"has_more\": false, \"total_count\": 0, \"url\": \"/v1/charges/ch_1Hn3SCEmy3qYdmGdS1UOGAWP/refunds\" }, \"review\": null, \"shipping\": null, \"source\": { \"id\": \"card_1Hn3SCEmy3qYdmGdvaHjv7ob\", \"object\": \"card\", \"address_city\": null, \"address_country\": null, \"address_line1\": null, \"address_line1_check\": null, \"address_line2\": null, \"address_state\": null, \"address_zip\": null, \"address_zip_check\": null, \"brand\": \"Visa\", \"country\": \"US\", \"customer\": null, \"cvc_check\": \"pass\", \"dynamic_last4\": null, \"exp_month\": 11, \"exp_year\": 2021, \"fingerprint\": \"ZaBHkNL6fAk28W73\", \"funding\": \"credit\", \"last4\": \"4242\", \"metadata\": { }, \"name\": null, \"tokenization_method\": null }, \"source_transfer\": null, \"statement_descriptor\": null, \"statement_descriptor_suffix\": null, \"status\": \"succeeded\", \"transfer_data\": null, \"transfer_group\": null }";

//        System.out.println(reply);

//        boolean Validate = isValidJSON(reply);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);

        JsonNode jsonNode = objectMapper.readTree(reply);
        JsonNode transaction_response = null;
        JsonNode customer_response = null;
        String status = "";
        String amount = "";
        String amount_captured = "";
        String amount_refunded = "";

        if (jsonNode.has("status")) {
            System.out.println("||||||||||||||||");
            status = jsonNode.get("status").toString();
            status = status.substring(1, status.length() - 1);
            ;
            System.out.println(status);
            if (status.equals("succeeded")) {
                amount = jsonNode.get("amount").toString();
                amount_captured = jsonNode.get("amount_captured").toString();
                amount_refunded = jsonNode.get("amount_refunded").toString();
            }

            amount = amount.substring(0, amount.length() - 2);
            amount_captured = amount_captured.substring(0, amount_captured.length() - 2);
            amount = amount + ".00";
            amount_captured = amount_captured + ".00";
            System.out.println(amount);
            System.out.println(amount_captured);
            System.out.println(amount_refunded);


        }


//
//        JsonNode jsonNode = objectMapper.readTree(reply);
//        JsonNode content = null;
//        JsonNode BILLS = null;
//        JsonNode transaction_uid = null;
//        String AA = null;
//        if (jsonNode.has("transaction_response")) {
//
//            transaction_uid = content.get("transaction_uid");
//            System.out.print(transaction_uid.toString());
//        }
//        JsonFactory jsonFactory = new JsonFactory();
//        JsonParser jsonParser = jsonFactory.createParser(reply);
//        for(JsonParser.Feature feature : JsonParser.Feature.values()) {
//            System.out.println(feature.name() + ":" + jsonParser.isEnabled(feature));
//        }

//        JSONParser parse = new JSONParser();
//        JSONObject jObj = new JSONObject() parse.parse(reply);
//        JSONArray jsonArr_1 = (JSONArray) jObj.get("transaction_response");
//
//        System.out.println(jsonArr_1);


//        JSONObject jsonObject = new JSONArray(reply).getJSONObject(0);
//        System.out.println(jsonObject.get("transaction_response").toString());


//        ObjectMapper objectMapper2 = new ObjectMapper();
//        JsonNode jsonNode2 = objectMapper2.readTree(reply);
//
//        JsonNode data = jsonNode2.get("transaction_response");
//        System.out.println(data);

//        if (jsonNode2.has("transaction_response")) {


//            JsonNode data = jsonNode2.get("data");
//            System.out.println("Data "+data);
//
//            JsonNode cardsData = data.get("cardsData");
//            System.out.println("cardsData: "+cardsData);
//
////				JsonNode CardMask = cardsData.get("cardMask");
//            JsonNode CardMask = data.get("cardMask");
//            System.out.println("CardMask: "+CardMask);
//        }


//        JSONObject jsonObject = new JSONArray(reply).getJSONObject(0);
//        if(jsonObject.length() > 0){
//            String Delivery_lineAddress = jsonObject.get("delivery_line_1").toString();
//
//            String city_name = jsonObject.getJSONObject("components").getString("city_name");
//            String state_abbreviation = jsonObject.getJSONObject("components").getString("state_abbreviation");
//            String zipcode = jsonObject.getJSONObject("components").getString("zipcode");
//            String county_name = jsonObject.getJSONObject("metadata").getString("county_name");
//
//            System.out.println("Delivery_lineAddress: "+Delivery_lineAddress);
//
//            System.out.println("city_name : "+city_name);
//            System.out.println("state_abbreviation : "+state_abbreviation);
//            System.out.println("zipcode : "+zipcode);
//            System.out.println("county_name : "+county_name);
//
//
//        }else{
//            System.out.println("2");
//        }


    }


    public static boolean isValidJSON(final String json) {
        boolean valid = false;
        try {
            final JsonParser parser = new ObjectMapper().getJsonFactory()
                    .createJsonParser(json);
            while (parser.nextToken() != null) {
            }
            valid = true;
        } catch (Exception jpe) {
            jpe.printStackTrace();
        }


        return valid;
    }

}