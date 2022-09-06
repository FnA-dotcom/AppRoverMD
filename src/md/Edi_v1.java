/*
   Copyright [2011] [Prasad Balan]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package md;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Example showing how to create a X12 transaction from scratch.
 *
 * @author Prasad Balan
 *
 * <pre>
 * Context c = new Context('&tilde;', '*', ':');
 * X12 x12 = new X12(c);
 * Loop loop_isa = x12.addChild(&quot;ISA&quot;);
 *
 * // Add ISA segment to the loop
 * loop_isa.addSegment(&quot;ISA*00*          *00*          *ZZ*SENDERID       *&quot;
 * 		+ &quot;ZZ*RECEIVERID    *030409*0701*U*00401*0000000001*0*T*:&quot;);
 *
 * // Add GS child loop to ISA loop
 * Loop loop_gs = loop_isa.addChild(&quot;GS&quot;);
 * // Add GS segment directly as a string
 * loop_gs.addSegment(&quot;GS*1212*SENDERID*RECEIVERID*0701*000000001*X*00401&quot;);
 *
 * Loop loop_st = loop_gs.addChild(&quot;ST&quot;);
 * loop_st.addSegment(&quot;ST*83w5*000000001&quot;);
 * loop_st.addSegment(&quot;BPR*DATA*NOT*VALID*RANDOM*TEXT&quot;);
 * loop_st.addSegment(&quot;TRN*1*0000000000*1999999999&quot;);
 * loop_st.addSegment(&quot;DTM*111*20090915&quot;);
 *
 * Loop loop_1000A = loop_st.addChild(&quot;1000A&quot;);
 * loop_1000A.addSegment(&quot;N1*PR*ALWAYS INSURANCE COMPANY&quot;);
 * loop_1000A.addSegment(&quot;N7*AROUND THE CORNER&quot;);
 * loop_1000A.addSegment(&quot;N4*SHINE CITY*GREEN STATE*ZIP&quot;);
 * loop_1000A.addSegment(&quot;REF*DT*435864864&quot;);
 *
 * Loop loop_1000B = loop_st.addChild(&quot;1000B&quot;);
 * loop_1000B
 * 		.addSegment(&quot;N1*PE*FI*888888888*P.O.BOX 456*SHINE CITY*GREEN STATE*ZIP*EARTH&quot;);
 *
 * Loop loop_2000 = loop_st.addChild(&quot;2000&quot;);
 * loop_2000.addSegment(&quot;LX*1&quot;);
 *
 * Loop loop_2010_1 = loop_2000.addChild(&quot;2010&quot;);
 * loop_2010_1.addSegment(&quot;CLP*PCN123456789**5555.55**CCN987654321&quot;);
 * loop_2010_1.addSegment(&quot;CAS*PR*909099*100.00&quot;);
 * loop_2010_1.addSegment(&quot;NM1*QC*1*PATIENT*TREATED*ONE***34*333333333&quot;);
 * loop_2010_1.addSegment(&quot;DTM*273*20020824&quot;);
 * loop_2010_1.addSegment(&quot;AMT*A1*10.10&quot;);
 * loop_2010_1.addSegment(&quot;AMT*A2*20.20&quot;);
 *
 * Loop loop_2010_2 = loop_2000.addChild(&quot;2010&quot;);
 * loop_2010_2.addSegment(&quot;LX*2&quot;);
 * loop_2010_2.addSegment(&quot;CLP*PCN123456789**4444.44**CCN987654321&quot;);
 * loop_2010_2.addSegment(&quot;CAS*PR*909099*200.00&quot;);
 * loop_2010_2.addSegment(&quot;NM1*QC*1*PATIENT*TREATED*TWO***34*444444444&quot;);
 * loop_2010_2.addSegment(&quot;DTM*273*20020824&quot;);
 * loop_2010_2.addSegment(&quot;AMT*A1*30.30&quot;);
 * loop_2010_2.addSegment(&quot;AMT*A2*40.40&quot;);
 *
 * Loop loop_se = loop_gs.addChild(&quot;SE&quot;);
 * loop_se.addSegment(&quot;SE*XX*000000001&quot;);
 *
 * Loop loop_ge = loop_isa.addChild(&quot;GE&quot;);
 * loop_ge.addSegment(&quot;GE*1*000000001&quot;);
 *
 * Loop loop_iea = x12.addChild(&quot;IEA&quot;);
 * loop_iea.addSegment(&quot;IEA*1*000000001&quot;);
 *
 * // Since the SE loop has the incorrect segment count let us fix that.
 * Integer count = loop_st.size();
 * count += 1; // In the loop hierarchy SE is not a child loop of ST. So
 * // when we get the rows in ST loop it does not have the count of SE.
 * // so add 1.
 *
 * // We can set the count directly, like
 * // loop_se.getSegment(0).setElement(1, count.toString());
 * // this is just to show how to use the findLoop()
 * List&lt;Loop&gt; trailer = x12.findLoop(&quot;SE&quot;);
 * trailer.get(0).getSegment(0).setElement(1, count.toString());
 *
 * //another way
 * List&lt;Segment&gt; se = x12.findSegment(&quot;SE&quot;);
 * se.get(0).setElement(1, count.toString());
 *
 * //another way
 * loop_se.getSegment(0).setElement(1, count.toString());
 *
 * System.out.println(loop_st.size());
 * System.out.println(x12.toString());
 * System.out.println(x12.toXML());
 *
 * </pre>
 */
public class Edi_v1 {

	public static HashMap<String,Integer> ISATAG=new HashMap<String, Integer>();
    public static int ClaimType = 0; //1 prof , 2 inst
    public static String ClaimTypeIdentifier = "005010X222A1";// 005010X222A1 Prof , 005010X223A2 Inst


	public static void main(String[] args) {
//
//		Context c = new Context('~', '*', ':');
//
//		ISATAG.put("0000-ISA01-AuthorizationInfoQualifier-00",2);
//		ISATAG.put("0000-ISA02-AuthorizationInformation-",10);
//		ISATAG.put("0000-ISA03-SecurityInformationQualifier-00",2);
//		ISATAG.put("0000-ISA04-SecurityInformation-          ",10);
//		ISATAG.put("0000-ISA05-Interchange ID Qualifier-ZZ",2);
//		ISATAG.put("0000-ISA06-Interchange Sender ID (*)-BS01834        ",15);
//		ISATAG.put("0000-ISA07-Interchange ID Qualifier-ZZ",2);
//		ISATAG.put("0000-ISA08-Interchange Receiver ID-33477          ",15);
//		ISATAG.put("0000-ISA09-InterchangeDate-140205",6);
//		ISATAG.put("0000-ISA10-InterchangeTime-1452",4);
//		ISATAG.put("0000-ISA11-RepetitionSeparator-^",1);
//		ISATAG.put("0000-ISA12-InterchangeControlVersionNumber-00501",5);
//		ISATAG.put("0000-ISA13-InterchangeControlNumber-100000467",9);
//		ISATAG.put("0000-ISA14-AcknowledgmentRequested-0",1);
//		ISATAG.put("0000-ISA15-Usage Indicator-T",1);
//		ISATAG.put("0000-ISA16-ComponentElementSeparator-:~",1);
//
//
//		HashMap<Integer,String> ISAValue=new HashMap<Integer, String>();
//
//		ISAValue.put(0,"ISA");
//		ISAValue.put(1,"00");
//		ISAValue.put(2," " );
//		ISAValue.put(3,"00");
//		ISAValue.put(4," ");
//		ISAValue.put(5,"ZZ");
//		ISAValue.put(6,"BS01834");
//		ISAValue.put(7,"ZZ");
//		ISAValue.put(8,"33477");
//		ISAValue.put(9,"140205");
//		ISAValue.put(10,"1452");
//		ISAValue.put(11,"^");
//		ISAValue.put(12,"00501");
//		ISAValue.put(13,"000987654");
//		ISAValue.put(14,"0");
//		ISAValue.put(15,"T");
//		ISAValue.put(16,":");
//
//		X12 x12 = new X12(c);
//		Loop loop_isa = x12.addChild("ISA");
//
//		// add segment
//		loop_isa.addSegment("ISA*00*          *00*          *ZZ*SENDERID       *ZZ*RECEIVERID    *030409*0701*U*00401*0000000001*0*T*:");
//
//		for (String key : ISATAG.keySet()) {
//			//   System.out.println(key);
//			String temp[]=key.split("-");
//			int elementid=Integer.parseInt(getOnlyDigits(temp[1]));
//			int len= ISATAG.get(key);
//			// System.out.println(elementid);
//			// System.out.println(len);
//			loop_isa.getSegment(0).setElement(elementid, ISAValue.get(elementid),len);
//		}
//
//        if (ClaimType == 1)//Prof
//        {
//            //Transaction Set Header (ST)
//            // Add GS child loop to ISA loop
//            Loop loop_gs = loop_isa.addChild("GS");
//            // Add GS segment directly as a string
//            loop_gs.addSegment("GS*HC*AZ08260*0048*20140205*1452*000987654*X*"+ClaimTypeIdentifier);
//
//            Loop loop_st = loop_gs.addChild("ST");
//            loop_st.addSegment("ST*837*000987654*"+ClaimTypeIdentifier);// 005010X223 <===> 005010X222A1
//            loop_st.addSegment("BHT*0019*00*0123*19960918*0932*CH");
//
//
//
//            //1000A SUBMITTER NAME
//            Loop loop_1000A = loop_st.addChild("1000A");
//            loop_1000A.addSegment("NM1*41*2*JONES HOSPITAL*****46*12345");
//            loop_1000A.addSegment("PER*IC*JANE DOE*TE*9005555555");
//
//
//            //1000B RECEIVER NAME
//            Loop loop_1000B = loop_st.addChild("1000B");
//            loop_1000B.addSegment("NM1*40*2*MEDICARE*****46*00120");
//
//
//            //2000A BILLING PROVIDER
////            Loop loop_2000 = loop_st.addChild("2000");
////            loop_2000.addSegment("HL*1**20*1"); //HL BILLING PROVIDER HIERARCHICAL LEVEL
////            loop_2000.addSegment("PRV*BI*PXC*203BA0200N"); //PRV BILLING PROVIDER SPECIALTY
//
//
//            //2010AA BILLING PROVIDER NAME
////            Loop loop_2010_1 = loop_2000.addChild("2010");
////            loop_2010_1.addSegment("NM1*85*2*JONES HOSPITAL*****XX*9876540809");//NM1 BILLING PROVIDER NAME INCLUDING NATIONAL PROVIDER ID
////            loop_2010_1.addSegment("N3*225 MAIN STREET BARKLEY BUILDING"); //N3 BILLING PROVIDER ADDRESS
////            loop_2010_1.addSegment("N4*CENTERVILLE*PA*17111");//N4 BILLING PROVIDER LOCATION
////            loop_2010_1.addSegment("REF*EI*567891234"); //REF BILLING PROVIDER TAX IDENTIFICATION NUMBER
////            loop_2010_1.addSegment("PER*IC*CONNIE*TE*3055551234");//PER BILLING PROVIDER CONTACT INFORMATION
//
//            //2000B SUBSCRIBER HL LOOP
////            Loop loop_2000B = loop_2000.addChild("2000");
////            loop_2000B.addSegment("HL*2*1*22*0");//HL SUBSCRIBER HIERARCHICAL LEVEL
////            loop_2000B.addSegment("SBR*P*18*******MB");//SBR SUBSCRIBER INFORMATION
//
//            //2010BA SUBSCRIBER NAME LOOP
////            Loop loop_2010BA = loop_2000.addChild("2000");
////            loop_2010BA.addSegment("NM1*IL*1*DOE*JOHN*T***MI*030005074A");//NM1 SUBSCRIBER NAME
////            loop_2010BA.addSegment("N3*125 CITY AVENUE");//N3 SUBSCRIBER ADDRESS
////            loop_2010BA.addSegment("N4*CENTERVILLE*PA*17111");//N4 SUBSCRIBER LOCATION
////            loop_2010BA.addSegment("DMG*D8*19261111*M"); //DMG SUBSCRIBER DEMOGRAPHIC INFORMATION SITUATIONAL
//
//            //2010BB PAYER NAME LOOP
////            Loop loop_2010BB = loop_2000.addChild("2000");
////            loop_2010BB.addSegment("NM1*PR*2*MEDICARE B*****PI*00435");//NM1 PAYER NAME
////            loop_2010BB.addSegment("N4*AKRON*OH*44306");//N4 PAYER ADDRESS ** ADDED
////            loop_2010BB.addSegment("REF*G2*330127");//REF BILLING PROVIDER SECONDARY IDENTIFICATION SITUATIONAL
//
//
//            //	2300 CLAIM INFORMATION
////            Loop loop_2300 = loop_st.addChild("2300");
////            loop_2300.addSegment("CLM*756048Q*89.93***14:A:1*A*Y*Y"); //CLM CLAIM LEVEL INFORMATION
////            loop_2300.addSegment("DTP*434*RD8*19960911"); // DTP STATEMENT DATES
////            loop_2300.addSegment("CL1*3**01"); // CL1 INSTITUTIONAL CLAIM CODE
////            loop_2300.addSegment("HI*BK:3669"); //HI PRINCIPAL DIAGNOSIS CODES REQUIRED
////            loop_2300.addSegment("HI*BF:4019*BF:79431"); //HI OTHER DIAGNOSIS INFORMATION
////            loop_2300.addSegment("HI*BH:A1:D8:19261111*BH:A2:D8:19911101*BH:B1:D8:19261111*BH:B2:D8:19870101"); //HI OCCURRENCE INFORMATION
////            loop_2300.addSegment("HI*BE:A2:::15.31"); //HI VALUE INFORMATION !PRESENT IN WPC
////            loop_2300.addSegment("HI*BG:09"); //HI CONDITION INFORMATION
//
//
//            //2310A ATTENDING PROVIDER NAME
////            Loop loop_2310A = loop_2300.addChild("2310");
////            loop_2310A.addSegment("NM1*71*1*JONES*JOHN*J");//NM1 ATTENDING PROVIDER
////            loop_2310A.addSegment("REF*1G*B99937");//REF ATTENDING PROVIDER SECONDARY IDENTIFICATION
//
//
//            // 2320 OTHER SUBSCRIBER INFORMATION
////            Loop loop_2320 = loop_2300.addChild("2320");
////            loop_2320.addSegment("SBR*S*01*351630*STATE TEACHERS*****CI");//SBR OTHER SUBSCRIBER INFORMATION
////            loop_2320.addSegment("OI***Y***Y");//OI OTHER INSURANCE COVERAGE INFORMATION
//
//
//            //2330A OTHER SUBSCRIBER NAME
////            Loop loop_2330A = loop_2300.addChild("2330");
////            loop_2330A.addSegment("NM1*IL*1*DOE*JANE*S***MI*222004433");//NM1 OTHER SUBSCRIBER NAME
////            loop_2330A.addSegment("N3*125 CITY AVENUE");//N3 OTHER SUBSCRIBER ADDRESS
////            loop_2330A.addSegment("N4*CENTERVILLE*PA*17111");//N4 OTHER SUBSCRIBER CITY, STATE, ZIP CODE
//
//
//            //2330B OTHER PAYER NAME
////            Loop loop_2330B = loop_2300.addChild("2330");
////            loop_2330B.addSegment("NM1*PR*2*STATE TEACHERS*****PI*1135");//NM1 OTHER PAYER NAME
//
//
//            //2400 SERVICE LINE
////            Loop loop_2400 = loop_st.addChild("2400");
////            loop_2400.addSegment("LX*1");//LX SERVICE LINE COUNTER
////            loop_2400.addSegment("SV2*0305*HC:85025*13.39*UN*1"); //SV2 INSTITUTIONAL SERVICE
////            loop_2400.addSegment("DTP*472*D8*19960911");//DTP DATE - SERVICE DATES
//
//
////            Loop loop_2400A = loop_st.addChild("2400");
////            loop_2400A.addSegment("LX*2");//LX SERVICE LINE COUNTER
////            loop_2400A.addSegment("SV2*0730*HC:93005*76.54*UN*3");//SV2 INSTITUTIONAL SERVICE
////            loop_2400A.addSegment("DTP*472*D8*19960911");//DTP DATE - SERVICE DATES
//
//            Loop loop_se = loop_gs.addChild("SE");
//            loop_se.addSegment("SE*XX*000987654");
//
//            Loop loop_ge = loop_isa.addChild("GE");
//            loop_ge.addSegment("GE*1*000987654");
//
//            Loop loop_iea = x12.addChild("IEA");
//            loop_iea.addSegment("IEA*1*000987654");
//
//            // Since the SE loop has the incorrect segment count let us fix that.
//            Integer count = loop_st.size();
//            count += 1; // In the loop hierarchy SE is not a child loop of ST. So
//            // when we get the rows in ST loop it does not have the count of SE.
//            // so add 1.
//
//            // We can set the count directly, like
//            // loop_se.getSegment(0).setElement(1, count.toString());
//            // this is just to show how to use the findLoop()
//
//            //TRAILER
//            List<Loop> trailer = x12.findLoop("SE");
//            trailer.get(0).getSegment(0).setElement(1, count.toString(),count.toString().length());
//
//            //another way
//            List<Segment> se = x12.findSegment("SE");
//            se.get(0).setElement(1, count.toString(),count.toString().length());
//
//            //another way
//            loop_se.getSegment(0).setElement(1, count.toString(), count.toString().length());
//
//
//            //System.out.println(loop_st.size());
//            System.out.println(x12.toString());
//            //System.out.println(x12.toXML());
//        }
//
//        String InterControlNo = "000000001";
//
//        InterControlNo = String.format("%09d", Integer.parseInt(InterControlNo) + 1);
//
//        System.out.println("InterControlNo ->" +InterControlNo);

        String RecieverInsurance_IdentificationCode = "";
        if(isEmpty(RecieverInsurance_IdentificationCode)) System.out.println("NULL or Empty");

    }



	public static String getOnlyDigits(String s) {
		Pattern pattern = Pattern.compile("[^0-9.]");
		Matcher matcher = pattern.matcher(s);
		String number = matcher.replaceAll("");
		return number;
	}

    static boolean isEmpty(final String str){
        return (str == null) || (str.length() <= 0);
    }
}
