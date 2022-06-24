package HL7Tester;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.Terser;


public class TerserHL7Parser {
    public static void main(String[] args) throws Exception {
        String msg = "";
/*        String msg = "MSH|^~\\&|HIS|RIH|EKG|EKG|199904140038||ADT^A01||P|2.2\r"
                + "PID|0001|00009874|00001122|A00977|SMITH^JOHN^M|MOM|19581119|F|NOTREAL^LINDA^M|C|564 SPRING ST^^NEEDHAM^MA^02494^US|0002|(818)565-1551|(425)828-3344|E|S|C|0000444444|252-00-4414||||SA|||SA||||NONE|V1|0001|I|D.ER^50A^M110^01|ER|P00055|11B^M011^02|070615^BATMAN^GEORGE^L|555888^NOTREAL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^NOTREAL^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|199904101200||||5555112333|||666097^NOTREAL^MANNY^P\r"
                + "NK1|0222555|NOTREAL^JAMES^R|FA|STREET^OTHER STREET^CITY^ST^55566|(222)111-3333|(888)999-0000|||||||ORGANIZATION\r"
                + "PV1|0001|I|D.ER^1F^M950^01|ER|P000998|11B^M011^02|070615^BATMAN^GEORGE^L|555888^OKNEL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^VOICE^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|||||5555112333|||666097^DNOTREAL^MANNY^P\r"
                + "PV2|||0112^TESTING|55555^PATIENT IS NORMAL|NONE|||19990225|19990226|1|1|TESTING|555888^NOTREAL^BOB^K^DR^MD||||||||||PROD^003^099|02|ER||NONE|19990225|19990223|19990316|NONE\r"
                + "AL1||SEV|001^POLLEN\r"
                + "AL1||SEV|003^DUST\r"
                + "GT1||0222PL|NOTREAL^BOB^B||STREET^OTHER STREET^CITY^ST^77787|(444)999-3333|(222)777-5555||||MO|111-33-5555||||NOTREAL GILL N|STREET^OTHER STREET^CITY^ST^99999|(111)222-3333\r"
                + "IN1||022254P|4558PD|BLUE CROSS|STREET^OTHER STREET^CITY^ST^00990||(333)333-6666||221K|LENIX|||19980515|19990515|||PATIENT01 TEST D||||||||||||||||||02LL|022LP554";*/
        msg = "MSH|^~\\&||665|ADT|3344|20210218112843||ADT^A04|20210218112843|P|2.3\r" +
                "EVN|A08|20200707020302|||XXX^^^^^^^^488\r" +
                "PID|1||313002||Test^Abid^||19931114|M||Asian| 17154 Butte Creek road^^houston^tx^77090||888-888-8888^^cp^^^(888)^888-8888|888-888-8888^^ph^abid@gmail.com^^(888)^888-8888||S|||555-55-5555|||\r" +
                "PV1||3^E/R^02|EMERGENCY ROOM|||||||||||||||||||||||||||||||||||||||||20210218112843|\r" +
                "GT1|1|230||||^^CP^^^|||\r" +
                "IN1|1|||Blue Cross and Blue Shield of Texas\r" +
                "Servicing States: TX|^^^^|||Grp123|||||||G|||||Y||||||||||||||||123MEM|||||||F||||M\r" +
                "NK1|1|^Tabish^^|Friend|^^^^|2222222222||EMERGENCY";

        HapiContext context = new DefaultHapiContext();
        Parser p = context.getGenericParser();
        Message hapiMsg = p.parse(msg);

        /*
         * Another way of reading messages is to use a Terser. The terser
         * accepts a particular syntax to retrieve segments. See the API
         * documentation for the Terser for more information.
         */
        Terser terser = new Terser(hapiMsg);

        /*
         * Sending Application is in MSH-3-1 (the first component of the third
         * field of the MSH segment)
         */
        String sendingApplication = terser.get("/.MSH-3-1");
        System.out.println(sendingApplication);
        // HIS

        /*
         * We can use brackets to get particular repetitions
         */
        String secondAllergyType = terser.get("/AL1(1)-3-2");
        System.out.println(secondAllergyType);
        // DUST

        // We can also use the terser to set values
        terser.set("/.MSH-3-1", "new_sending_app");

        // Let's try something more complicated, adding values to an OBX in an ORU^R01
//        ORU_R01 oru = new ORU_R01();
//        oru.initQuickstart("ORU", "R01", "P");

//        terser = new Terser(oru);
        for (int i = 0; i < 5; i++) {
            terser.set("/PATIENT_RESULT/ORDER_OBSERVATION/OBSERVATION(" + i + ")/OBX-1", "" + (i + 1));
            terser.set("/PATIENT_RESULT/ORDER_OBSERVATION/OBSERVATION(" + i + ")/OBX-3", "ST");
            terser.set("/PATIENT_RESULT/ORDER_OBSERVATION/OBSERVATION(" + i + ")/OBX-5", "This is the value for rep " + i);
        }

//        System.out.println(p.encode(oru));

        /*
         *   MSH|^~\&|||||||ORU^R01|||2.5
         *   OBX|1||ST||This is the value for rep 0
         *   OBX|2||ST||This is the value for rep 1
         *   OBX|3||ST||This is the value for rep 2
         *   OBX|4||ST||This is the value for rep 3
         *   OBX|5||ST||This is the value for rep 4
         */
    }
}
