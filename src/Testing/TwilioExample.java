package Testing;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class TwilioExample {
    //    private static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    //TESTING
    //private static final String ACCOUNT_SID = "AC60325bc1c82a680a9b654baf076df3d8";
    //LIVE
    private static final String ACCOUNT_SID = "AC66d7b3eaa213fbb50fc1ed4d4425583f";
//    private static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    //TESTING
    //private static final String AUTH_TOKEN = "edcaf2fbbf9d7dd2659d33eb0d41eadf";
    //LIVE
    private static final String AUTH_TOKEN = "a63c9ce764d1bc68411c284682507adc";

    public static void main(String[] args) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber("+14694980033"), // to (ALI)
//                new com.twilio.type.PhoneNumber("+923313075414"), // to (ME)
                //DEV-ACCOUNT
                //new com.twilio.type.PhoneNumber("+14255499574"), // from
                //FSARWAR ACCOUNT
                new com.twilio.type.PhoneNumber("+19724981837"), // from
                "Testing message from TWILIO sent from ROVER!")
                .create();
        System.out.println("Error Code --> " + message.getErrorCode());
        System.out.println("Get Body --> " + message.getBody());
        System.out.println("get Error Message --> " + message.getErrorMessage());
        System.out.println("Price --> " + message.getPrice());
        System.out.println("Price --> " + message.getDateCreated());
        System.out.println("Price --> " + message.getDateSent());
        System.out.println("Price --> " + message.getDateUpdated());
        System.out.println("Status --> " + message.getStatus());

        System.out.println("Message SID " + message.getSid());
    }
}
