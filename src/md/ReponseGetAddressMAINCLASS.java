package md;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;


/**
 * How to Add Text To Existing PDF in java
 * Example Using iText library - core java tutorial
 */
public class ReponseGetAddressMAINCLASS {

    public static void main(String[] args) throws Exception {

//        String s1="2019";
//        System.out.println(s1.substring(2,4));//returns va
        String reply = "";
        reply = "{  \"transaction_response\": {    \"result_code\": 1,    \"result_sub_code\": \"000\",    \"result_text\": \"APPROVAL\",    \"transaction_id\": \"1042452\",    \"transaction_uid\": \"d9fa8bc8-905e-4e4c-bf5e-53145a937cc6\",    \"transaction_datetime\": \"20201006T153416Z\",    \"account_type\": \"VI\",    \"account_last_4\": \"1111\",    \"amount\": \"12.12\",    \"approved_amount\": \"12.12\",    \"method\": \"CC\",    \"auth_code\": \"OK0504\",    \"avs_result_code\": \"Y\",    \"code_result\": \"\",    \"type\": \"2\",    \"expiration_date\": \"0421\",    \"token\": \"4039468926761111\",    \"emv\": \"\",    \"emv_tag_data\": \"\",    \"entry_mode\": \"\"  },  \"nonce\": \"1601998453\",  \"test\": \"0\",  \"customer_response\": {    \"result_code\": 1,    \"result_text\": \"SUCCESS\",    \"result_sub_code\": \"000\",    \"transaction_datetime\": \"20201006T153420Z\",    \"token\": \"4039468926761111\",    \"account_type\": \"VI\",    \"account_last_4\": \"1111\",    \"expiration_date\": \"0421\"  },  \"customer\": {    \"first_name\": \"Oscar\",    \"last_name\": \"Brady\",    \"street_1\": \"55 Airport Drive\",    \"street_2\": \"\",    \"city\": \"Pensacola\",    \"state\": \"FL\",    \"zip\": \"32503\",    \"country\": \"UnitedStates\",    \"phone_number\": \"2025550197\",    \"company\": \"Ferry Group\",    \"customer_id\": \"1000008\",    \"email\": \"oscar@brady.com\",    \"email_receipt\": \"YES\",    \"notes\": \"Sample Test \",    \"action_code\": \"1\"  },  \"card_info\": {    \"card_class\": \"\",    \"product_id\": \"\",    \"prepaid_indicator\": \"\",    \"detailcard_indicator\": \"\",    \"debitnetwork_indicator\": \"\"  }}";
        //reply = "\"{ \\\"transaction_response\\\": { \\\"result_code\\\": 1, \\\"result_sub_code\\\": \\\"000\\\", \\\"result_text\\\": \\\"APPROVAL\\\", \\\"transaction_id\\\": \\\"1042010\\\", \\\"transaction_uid\\\": \\\"d0a91edb-b38d-4214-ba74-beaf247bfd43\\\", \\\"transaction_datetime\\\": \\\"20201002T194153Z\\\", \\\"account_type\\\": \\\"VI\\\", \\\"account_last_4\\\": \\\"1111\\\", \\\"amount\\\": \\\"12.12\\\", \\\"approved_amount\\\": \\\"12.12\\\", \\\"method\\\": \\\"CC\\\", \\\"auth_code\\\": \\\"OK7409\\\", \\\"avs_result_code\\\": \\\"Y\\\", \\\"code_result\\\": \\\"\\\", \\\"type\\\": \\\"2\\\", \\\"expiration_date\\\": \\\"0421\\\", \\\"token\\\": \\\"4039468926761111\\\", \\\"emv\\\": \\\"\\\", \\\"emv_tag_data\\\": \\\"\\\", \\\"entry_mode\\\": \\\"\\\" }, \\\"nonce\\\": \\\"{{$timestamp}}\\\", \\\"test\\\": \\\"0\\\", \\\"customer_response\\\": { \\\"result_code\\\": 1, \\\"result_text\\\": \\\"SUCCESS\\\", \\\"result_sub_code\\\": \\\"000\\\", \\\"transaction_datetime\\\": \\\"20201002T194201Z\\\", \\\"token\\\": \\\"4039468926761111\\\", \\\"account_type\\\": \\\"VI\\\", \\\"account_last_4\\\": \\\"1111\\\", \\\"expiration_date\\\": \\\"0421\\\" }, \\\"customer\\\": { \\\"first_name\\\": \\\"Oscar\\\", \\\"last_name\\\": \\\"Brady\\\", \\\"street_1\\\": \\\"55 Airport Drive\\\", \\\"street_2\\\": \\\"\\\", \\\"city\\\": \\\"Pensacola\\\", \\\"state\\\": \\\"FL\\\", \\\"zip\\\": \\\"32503\\\", \\\"country\\\": \\\"UnitedStates\\\", \\\"phone_number\\\": \\\"2025550197\\\", \\\"company\\\": \\\"\\\", \\\"customer_id\\\": \\\"1000006\\\", \\\"email\\\": \\\"oscar@brady.com\\\", \\\"email_receipt\\\": \\\"YES\\\", \\\"notes\\\": \\\"Sample Notes\\\", \\\"action_code\\\": \\\"1\\\" }, \\\"card_info\\\": { \\\"card_class\\\": \\\"\\\", \\\"product_id\\\": \\\"\\\", \\\"prepaid_indicator\\\": \\\"\\\", \\\"detailcard_indicator\\\": \\\"\\\", \\\"debitnetwork_indicator\\\": \\\"\\\" }}\"";

        reply = StringEscapeUtils.unescapeJava(reply);

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
        String trresult_text = "";
        String trresult_sub_code = "";
        String trtransaction_datetime = "";
        String tramount = "";
        String trapproved_amount = "";
        String trtransaction_id = "";
        String crresult_text = "";
        String crresult_sub_code = "";
        String crtransaction_datetime = "";
        if (jsonNode.has("transaction_response")) {
            System.out.println("||||||||||||||||");
            transaction_response = jsonNode.get("transaction_response");
            //System.out.println(transaction_response);
            trresult_text = transaction_response.get("result_text").toString().replaceAll("\"", "");
            trresult_sub_code = transaction_response.get("result_sub_code").toString().replaceAll("\"", "");
            trtransaction_id = transaction_response.get("transaction_id").toString().replaceAll("\"", "");
            trtransaction_datetime = transaction_response.get("transaction_datetime").toString().replaceAll("\"", "");
            tramount = transaction_response.get("amount").toString().replaceAll("\"", "");
            trapproved_amount = transaction_response.get("approved_amount").toString().replaceAll("\"", "");
            System.out.println("Transaction Response" + trresult_text + trresult_sub_code + trtransaction_id + trtransaction_datetime + tramount + trapproved_amount);

        }
        if (trresult_sub_code.equals("000")) {
            if (jsonNode.has("customer_response")) {
                System.out.println("||||||||||||||||");
                customer_response = jsonNode.get("customer_response");
                //System.out.println(transaction_response);
                crresult_text = customer_response.get("result_text").toString();
                crresult_sub_code = customer_response.get("result_sub_code").toString();
                crtransaction_datetime = customer_response.get("transaction_datetime").toString();
                System.out.println("customer Response" + crresult_text + crresult_sub_code + crtransaction_datetime);

            }
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