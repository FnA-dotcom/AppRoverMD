//
// Decompiled by Procyon v0.5.36
//

package oe;

import java.io.FileWriter;
import java.net.Socket;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

@SuppressWarnings("Duplicates")
public class EROFDALLAS_hl7loopsender implements Runnable {
    private static boolean SignonDone;
    private static boolean Connected;
    private static int TraceNo;
    private static String TraceId;
    private static String version;
    private static String clientid;
    private static String clientdb;
    private static String mrn;
    private static PreparedStatement pStmt = null;

    static {
        EROFDALLAS_hl7loopsender.SignonDone = false;
    }

    private Socket sock;
    private boolean done;

    public EROFDALLAS_hl7loopsender(final Socket socket) {
        this.done = false;
        this.sock = socket;
        TraceNo = 0;
    }

    public static void main(final String[] args) {
        Connection conn = null;
        Statement hstmt = null;
        Statement hstmt2 = null;
        ResultSet hrset = null;
        Statement hstmt3 = null;
        ResultSet hrset3 = null;
        String Query = "";
//        long StartTime = 0L;
        Socket sock = null;
        try {
            final Date dt = new Date();
//            StartTime = dt.getTime();

            version = "v1.1";
            final int port = 13141; //13181 //TEsting PORT
            final boolean useTls = false;
            final String Host = "104.43.130.159";//"104.43.130.19";;

            clientid = "27";
            clientdb = "ER_Dallas";

            System.out.println("Starting   Client App ..........");
            System.out.println("Connecting to Modified server Ver. " + version + " Dated 02nd march  2021 0341 hours");


            System.out.println("Connecting To  Database  @  localhost ");

            //  System.out.println("Connection: " + conn);
//            System.out.println("Connecting To  Epower Doc socket  @  192.168.110.10 3389");
            System.out.println("Connecting To  Epower Doc socket  @  " + Host + " " + port + "");
//            sock = new Socket("192.168.110.10", 3389);
            pushtologs("Connecting To  Epower Doc socket  @  " + Host + " " + port + "");
            sock = new Socket(Host, port);
            System.out.println("Socket Done");
            pushtologs("Socket Done");
            Connected = true;
            conn = getConnection();
        } catch (Exception e) {
            try {
                Connected = false;
                sock.close();
                conn.close();
            } catch (Exception ee) {
            }
            e.printStackTrace(System.out);
            System.out.println("Exception Creating Socket .........." + e.getMessage());
            pushtologs("Exception Creating Socket .........." + e.getMessage());
            final String[] a = null;
            try {

                Thread.sleep(20000L);
            } catch (InterruptedException ee) {
                ee.printStackTrace();
            }
            Thread.currentThread().stop();

            main(a);
            pushtologs("going for Reconnection");

            System.out.println("going for Reconnection");
            return;
        }
        try {
            Thread.sleep(2000L);
//            final String TransId = "";
            final int RequestType = 1;
            String Id = "";
            String msg = "";
            mrn = "";
            String flag = "";
//            int FacilityId = 0;
//            String flagvalue = "1";
            final String Param9 = "";
            System.out.println("Application SIGNON Successfull........");
            pushtologs("Application SIGNON Successfull........");
            while (true) {
                try {
                    Id = "";
                    msg = "";
                    mrn = "";
                    hstmt = conn.createStatement();
                    hstmt2 = conn.createStatement();
                    Query = " SELECT id,msg,mrn,flag,ClientIndex FROM request WHERE status=0 AND ClientIndex = 28 " +
                            "AND flag <= 3 LIMIT 2";
//                    System.out.println("Query request Tb: "+Query);
                    hrset = hstmt.executeQuery(Query);
                    while (hrset.next()) {
                        Id = hrset.getString(1);
                        TraceId = hrset.getString(1);
                        msg = hrset.getString(2);
                        mrn = hrset.getString(3);
                        flag = hrset.getString(4);
                        clientid = hrset.getString(5);
                        pushtologs("pull MRN " + mrn + " Successful........");

//                        if (flag.compareTo("1") == 0) {
//                            flagvalue = "0";
//                        }
                        //Date dNow = new Date(System.currentTimeMillis() - 7200000L);
                        String dNow = "";
                        String Query123 = "";
                        Query123 = "Select DATE_FORMAT(NOW(),'%Y%m%d%H%i%s')";
                        hstmt3 = conn.createStatement();
                        hrset3 = hstmt3.executeQuery(Query123);
                        if (hrset3.next()) {
                            dNow = hrset3.getString(1);
                        }
                        hrset3.close();
                        hstmt3.close();

//                        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
                        String MSCID = dNow;
                        switch (RequestType) {
                            case 1: {
                                sendmsg2(sock, msg, mrn, 0, conn, MSCID);
                                //hstmt2.executeUpdate("UPDATE oe.request SET status=1,posttime=now(), MSCID = '" + MSCID + "' WHERE id=" + Id);

                                pStmt = conn.prepareStatement("UPDATE oe.request SET status= ?, posttime= NOW(), MSCID=? WHERE Id = ? ");
                                pStmt.setInt(1, 1);
                                pStmt.setString(2, MSCID);
                                pStmt.setString(3, Id);
                                pStmt.executeUpdate();
                                pStmt.close();

                                break;
                            }
                            case 2: {
                                sendmsg2(sock, msg, mrn, 1, conn, MSCID);
                                //hstmt2.executeUpdate("UPDATE oe.request SET status=1,posttime=now(), MSCID = '" + MSCID + "' WHERE id=" + Id);
                                pStmt = conn.prepareStatement("UPDATE oe.request SET status= ?, posttime= NOW(), MSCID=? WHERE Id = ? ");
                                pStmt.setInt(1, 1);
                                pStmt.setString(2, MSCID);
                                pStmt.setString(3, Id);
                                pStmt.executeUpdate();
                                pStmt.close();

                                break;
                            }
                        }
                        Thread.sleep(750L);
                        final EROFDALLAS_hl7loopsender client = new EROFDALLAS_hl7loopsender(sock);
                        final Thread reader = new Thread(client, "Euro Client");
                        pushtologs("Creating  Thread To Read Response ..MRN." + mrn + ".....IN");
                        System.out.println("Creating  Thread To Read Response ....MRN." + mrn + ".....IN");
                        reader.start();
                    }
                    hrset.close();
                    hstmt.close();
                    hstmt2.close();

                } catch (SQLException ee2) {
                    System.out.println("Error in Conn: " + ee2.getMessage());
                    pushtologs("Error in Conn: " + ee2.getMessage());

                }
                /*final Date dt2 = new Date();
                if ((dt2.getTime() - StartTime) / 1000L > 45.0) {
                    final Victoria_hl7loopsender client2 = new Victoria_hl7loopsender(sock);
                    final Thread reader2 = new Thread(client2, "Euro Client");
                    System.out.println("Creating Thread To Read Response ..........");
                    pushtologs("Creating Thread To Read Response ..........OUT");
                    reader2.stop();
                    StartTime = dt2.getTime();
                }*/
                Thread.sleep(8000L);
            }
        } catch (Exception e) {
            System.out.println("Excep in main..." + e.getMessage());
            pushtologs("Excep in main..." + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    public static void sendmsg2(final Socket sock, String msg, final String mrn, final int flag, final Connection conn, String MSCID) {
        Statement stmt = null;
        ResultSet rset = null;
/*        Statement hstmt = null;
        ResultSet hrset = null;
        Statement hstmt2 = null;*/
        String Query = "";
//        String Title = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String MaritalStatus = "";
        String DOB = "";
//        String Age = "";
        String gender = "";
        String Email = "";
        String PhNumber = "";
        String Address = "";
        String City = "";
        String State = "";
//        String Country = "";
        String ZipCode = "";
        String SSN = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
//        String PriCarePhy = "";
//        String ReasonVisit = "";
        String MRN = "";
        String DOS = "";
        String sync = "";
        String Id = "";
        String Ethnicity = "";
        String Race = "";
        String GuarantorEmployerPhNumber = "";
        int SelfPayChk = 0;
/*        int WorkersCompPolicy = 0;
        int MotorVehAccident = 0;
        String PriInsurance = "";
        String MemId = "";
        String GrpNumber = "";
        String PriInsuranceName = "";
        String AddressIfDifferent = "";
        String PrimaryDOB = "";
        String PrimarySSN = "";
        String PatientRelationtoPrimary = "";
        String PrimaryOccupation = "";
        String PrimaryEmployer = "";
        String EmployerAddress = "";
        String EmployerPhone = "";
        String SecondryInsurance = "";
        String SubscriberName = "";
        String SubscriberDOB = "";
        String MemberID_2 = "";
        String GroupNumber_2 = "";
        String PatientRelationshiptoSecondry = "";*/
        String NextofKinName = "";
        String RelationToPatientER = "";
        String PhoneNumberER = "";
        String VisitNumber = "";
        try {
            String testMessage = "This is a test message that the client will transmit";
            Query = " Select Title, FirstName, MiddleInitial, LastName, IFNULL(MaritalStatus,''), MRN, DOB, Age, Gender, Email, PhNumber, Address, City, State, Country,  " +
                    " ZipCode, SSN, Occupation, Employer, EmpContact, PriCarePhy, ReasonVisit, SelfPayChk, " +
                    " IFNULL(DATE_FORMAT(DateofService,'%Y%m%d%H%i%s'),DATE_FORMAT(CreatedDate,'%Y%m%d%H%i%s')), IFNULL(sync,0) ,ID " +
                    " from " + clientdb + ".PatientReg where MRN ='" + mrn.trim() + "' AND Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            System.out.println(Query);
            if (rset.next()) {
//                Title = rset.getString(1);
                FirstName = rset.getString(2);
                MiddleInitial = rset.getString(3);
                LastName = rset.getString(4);
                MaritalStatus = rset.getString(5);
                MRN = rset.getString(6);
                DOB = rset.getString(7);
//                Age = rset.getString(8);
                gender = rset.getString(9);
                Email = rset.getString(10);
                PhNumber = rset.getString(11);
                Address = rset.getString(12);
                City = rset.getString(13);
                State = rset.getString(14);
//                Country = rset.getString(15);
                ZipCode = rset.getString(16);
                SSN = rset.getString(17);
//                Occupation = rset.getString(18);
//                Employer = rset.getString(19);
//                EmpContact = rset.getString(20);
//                PriCarePhy = rset.getString(21);
//                ReasonVisit = rset.getString(22);
//                SelfPayChk = rset.getInt(23);
                DOS = rset.getString(24);
                sync = rset.getString(25);
                Id = rset.getString(26);
            }
            rset.close();
            stmt.close();

            Query = "Select CASE WHEN Ethnicity = 1 THEN 'Hispanic' WHEN Ethnicity = 2 THEN 'Non Hispanic' WHEN Ethnicity = 3 THEN 'Declined to Specify' ELSE 'Declined to Specify' END, " +
                    "CASE WHEN Race = 1 THEN 'Asian' WHEN Race = 2 THEN 'Black' WHEN Race = 3 THEN 'White' WHEN Race = 4 THEN 'Other' WHEN Race = 5 THEN 'Unknown' ELSE 'Unknown' END, " +
                    "IFNULL(GuarantorEmployerPhNumber,'') from " + clientdb + ".PatientReg_Details where PatientRegId = " + Id;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            //System.out.println(Query);
            if (rset.next()) {
                Ethnicity = rset.getString(1);
                Race = rset.getString(2);
                GuarantorEmployerPhNumber = rset.getString(3);
            }
            rset.close();
            stmt.close();
            if (GuarantorEmployerPhNumber.equals("")) {

                GuarantorEmployerPhNumber = "999-99-9999";
            }
//            System.out.println("GuarantorEmployerPhNumber:  " + GuarantorEmployerPhNumber);
            GuarantorEmployerPhNumber = GuarantorEmployerPhNumber.replaceAll("-", "");
//            System.out.println("sql pass");
            pushtologs("sql pass");
//            final String MRN2 = "3109900";
            DOB = DOB.replace("-", "");
            if (gender.toUpperCase().compareTo("FEMALE") == 0) {
                gender = "F";
            } else {
                gender = "M";
            }
//            System.out.println("G& M pass");
//            System.out.println("MaritalStatus" + MaritalStatus);
            if (!MaritalStatus.equals("")) {
                MaritalStatus = MaritalStatus.substring(0, 1);
            } else {
                MaritalStatus = "";
            }
//            System.out.println("MaritalStatus_AFTER--" + MaritalStatus);
            testMessage = "MSH|^~\\&||665|ADT|3344|20200707020302||ADT^A04|20200707020302|P|2.3\r\nEVN|A04|20200707020302|||XXX^^^^^^^^488 \r\nPID|1||714629||Ali^test^d||19721027|F||W|209 HOPKINS ST^^LONDON^KY^41604|097|||ENGLISH|M|001|201963|987654321|||IRISH|MOBILE|||||||N \r\nNK1|1|MOON^KEITH^F|SPOUSE|209 HOPKINS ST^^LONDON^KY^41604|(555)123-4444|(555)123-8100|||||||||||||||||||||||EMERGENCY|MOON^KEITH^F \r\nPV1||3^E/R^02|||||194501^TOWNSHEND^PETE|194502^DALTREY^ROGER|194506^ROGERS^PAUL|E|||||||194501^TOWNSHEND^PETE|3||BB1||||||||||||||||G||||||||201603221352|201603221352\r\nAL1|1|MA|2484^PHARMACEUTICAL GLAZE \r\nAL1|2|DA|4558^13C UREA \r\nIN1|1|SELF-PAY^SELF PAY|SELF PAY|SELF PAY||PFO Sequence 99 Self P|||||||||4";
            String Synctype = "A04";
            if (sync.compareTo("1") == 0) {
                Synctype = "A08";
            }
//            System.out.println("Synctype PASS: " + Synctype);
            pushtologs("Synctype PASS: " + Synctype);
            String IN1 = "IN1|1|SELF-PAY^SELF PAY|SELF PAY|SELF PAY||PFO Sequence 99 Self P|||||||||4";
            int HealthInsuranceChk = 0;
            String HIPriInsurance = "";
            String HISubscriberGroupNo = "";
            String HISubscriberPolicyNo = "";
            Query = "Select HealthInsuranceChk from " + clientdb + ".PatientReg_Details where PatientRegId = " + Id;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                HealthInsuranceChk = rset.getInt(1);
            }
            rset.close();
            stmt.close();

//            System.out.println("HealthInsuranceChk PASS: " + HealthInsuranceChk);
            pushtologs("HealthInsuranceChk PASS: " + HealthInsuranceChk);
            if (HealthInsuranceChk == 1) {
//                System.out.println("HealthInsuranceChk Inside: " + HealthInsuranceChk);
                Query = " Select IFNULL(HIPrimaryInsurance,''), IFNULL(HISubscriberGroupNo,''), IFNULL(HISubscriberPolicyNo,'') " +
                        " from " + clientdb + ".Patient_HealthInsuranceInfo where PatientRegId = " + Id;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    HIPriInsurance = rset.getString(1);
                    HISubscriberGroupNo = rset.getString(2);
                    HISubscriberPolicyNo = rset.getString(3);
                }
                rset.close();
                stmt.close();
                //IN1 = "IN1|1|" + HISubscriberPolicyNo + "||" + HIPriInsurance + "||||" + HISubscriberGroupNo + "|||||||||||||||||||||||||||||||||||";
                //IN1 = "IN1|1|||" + HIPriInsurance + "|^^^^|||" + HISubscriberGroupNo + "||||||||||||||||||||||||||||"+HISubscriberPolicyNo+"|||||||||||||"+HISubscriberPolicyNo+"\n";
                IN1 = "IN1|1|||" + HIPriInsurance + "|^^^^||||||||||G|||" + HISubscriberGroupNo + "||Y||||||||||||||||" + HISubscriberPolicyNo + "|||||||F||||M\r\n";
            }

            Query = "Select MAX(VisitNumber) from " + clientdb + ".PatientVisit where PatientRegId = " + Id;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                VisitNumber = rset.getString(1).trim();
            }
            rset.close();
            stmt.close();

            VisitNumber = "VN-" + MRN + "-" + VisitNumber;
//            PhNumber = PhNumber.replace("-","");
//            SSN = SSN.replace("-","");


            String NK1 = "";
            NK1 = "NK1|1|" + "" + "^" + NextofKinName + "^^|" + RelationToPatientER + "|^^^^|" + PhoneNumberER + "||EMERGENCY\r\n";
//            msg = "MSH|^~\\&||665|ADT|3344|" + MSH7 + "||ADT^" + Synctype + "|" + MSH7 + "|P|2.3\r\n" + "EVN|" + Synctype + "|20200707020302|||XXX^^^^^^^^488 \r\n"
////                    + "PID|1||" + MRN + "||" + LastName + "^" + FirstName + "^" + MiddleInitial + "||" + DOB + "|" + gender + "||W|" + Address + "^^" + City + "^" + State + "^" + ZipCode + "|" + PhNumber + "|" + PhNumber + "||ENGLISH|M|001||" + SSN + "|||||||||||N \r\n"
//                    + "PID|1||" + MRN + "||"+LastName + "^" + FirstName + "^" + MiddleInitial + "||" + DOB + "|" + gender + "||WH| " + Address + "^^" + City + "^" + State + "^" + ZipCode + "|" + "" +"|" + PhNumber + "|||"+MaritalStatus+"|||" + SSN +"|||11\n"
////                    + "PV1||3^E/R^02|||||" + Address + "^^" + City + "^" + State + "^" + ZipCode + "|||||||||||||||||||||||||||||||||||||" + MSH7 + "|" + MSH7 + "\r\n"
//                    +"PV1||3^E/R^02|EMERGENCY ROOM|||||||||||||||||||||||||||||||||||||||||"+MSH7+"|\n"
//                    + IN1;

            String MSH7 = MSCID;
            msg = "MSH|^~\\&||665|ADT|3344|" + MSH7 + "||ADT^" + Synctype + "|" + MSH7 + "|P|2.3\r\n"
                    + "EVN|" + Synctype + "|" + MSH7 + "|||XXX^^^^^^^^488 \r\n"
                    //+ "PID|1||" + MRN + "||" + LastName + "^" + FirstName + "^" + MiddleInitial + "||" + DOB + "|" + gender + "||W|" + Address + "^^" + City + "^" + State + "^" + ZipCode + "|" + PhNumber + "|" + PhNumber + "||ENGLISH|M|001||" + SSN + "|||||||||||N \r\n"
                    + "PID|1||" + MRN + "||" + LastName + "^" + FirstName + "^" + MiddleInitial + "||" + DOB + "|" + gender + "||" + Race + "| " + Address + "^^" + City + "^" + State + "^" + ZipCode + "|" + "" + "|" + PhNumber + "^^CP^^^(" + PhNumber.substring(0, 3) + ")^" + PhNumber.substring(4) + "|" + PhNumber + "^^PH^" + Email + "^^(" + PhNumber.substring(0, 3) + ")^" + PhNumber.substring(4) + "||" + MaritalStatus + "||" + VisitNumber + "|" + SSN + "|||" + Ethnicity + "\r\n"
//                    + "PV1||3^E/R^02|||||" + Address + "^^" + City + "^" + State + "^" + ZipCode + "|||||||||||||||||||||||||||||||||||||" + MSH7 + "|" + MSH7 + "\r\n"
                    + "PV1||3^E/R^02|EMERGENCY ROOM|||||||||||||||||||||||||||||||||||||||||" + MSH7 + "|\r\n"
                    + "GT1|1|" + "230" + "|" + "" + "||" + "" + "|(" + GuarantorEmployerPhNumber.substring(0, 3) + ")" + GuarantorEmployerPhNumber.substring(3) + "^^CP^^^" + "" + "||" + "" + "|" + "" + "\r\n"
                    + IN1;
            //+ NK1;

            String MSH = "MSH|^~\\&||665|ADT|3344|" + MSH7 + "||ADT^" + Synctype + "|" + MSH7 + "|P|2.3";
            String EVN = "EVN|" + Synctype + "|" + MSH7 + "|||XXX^^^^^^^^488";
            String PID = "PID|1||" + MRN + "||" + LastName + "^" + FirstName + "^" + MiddleInitial + "||" + DOB + "|" + gender + "||" + Race + "|" + Address + "^^" + City + "^" + State + "^" + ZipCode + "||" + PhNumber + "^^CP^^^(" + PhNumber.substring(0, 3) + ")^" + PhNumber.substring(4) + "|" + PhNumber + "^^PH^" + Email + "^^(" + PhNumber.substring(0, 3) + ")^" + PhNumber.substring(4) + "||" + MaritalStatus + "||" + VisitNumber + "|" + SSN + "|||" + Ethnicity;

            String PV1 = "PV1||3^E/R^02|EMERGENCY ROOM|||||||||||||||||||||||||||||||||||||||||" + DOS + "|";
            String GT1 = "GT1|1|" + "230" + "||||(" + GuarantorEmployerPhNumber.substring(0, 3) + ")" + GuarantorEmployerPhNumber.substring(3) + "^^CP^^^|||";
            final char END_OF_BLOCK = '\u001c';
            final char START_OF_BLOCK = '\u000b';
            final char CARRIAGE_RETURN = 13;

            StringBuffer testHL7MessageToTransmit = new StringBuffer();

            testHL7MessageToTransmit.append(START_OF_BLOCK)
                    //.append("MSH|^~\\&|AcmeHIS|StJohn|CATH|StJohn|20061019172719||ORM^O01|MSGID12349876|P|2.3")
                    .append(MSH)
                    .append(CARRIAGE_RETURN)
                    //.append("PID|||20301||Durden^Tyler^^^Mr.||19700312|M|||88 Punchward Dr.^^Los Angeles^CA^11221^USA|||||||")
                    .append(EVN)
                    .append(CARRIAGE_RETURN)
                    // .append("PV1||O|OP^^||||4652^Paulson^Robert|||OP|||||||||9|||||||||||||||||||||||||20061019172717|20061019172718")
                    .append(PID)
                    .append(CARRIAGE_RETURN)
                    //.append("ORC|NW|20061019172719")
                    .append(PV1)
                    .append(CARRIAGE_RETURN)
                    //.append("OBR|1|20061019172719||76770^Ultrasound: retroperitoneal^C4|||12349876")
                    .append(GT1)
                    .append(CARRIAGE_RETURN)
                    .append(END_OF_BLOCK)
                    .append(CARRIAGE_RETURN);


            byte[] byteBuffer = msg.getBytes();

            pushtologs("MSG updated TraceId: " + TraceId);
/*
            Query = "update oe.request set msg = '" + msg + "' where id = '" + TraceId + "'";
            //System.out.println("TraceId: "+TraceId);
            hstmt = conn.createStatement();
            hstmt.executeUpdate(Query);
            hstmt.close();
*/
            pStmt = conn.prepareStatement("UPDATE oe.request SET msg= ? WHERE Id = ? ");
            pStmt.setString(1, msg);
            pStmt.setString(2, TraceId);
            pStmt.executeUpdate();
            pStmt.close();

            sock.getOutputStream().write(testHL7MessageToTransmit.toString().getBytes());
            //sock.getOutputStream().write(new String(msg).getBytes());
            //sock.getOutputStream().write(byteBuffer);
            pushtologs("DEMO GRAPHIC UPDATE REQUEST Send Successfully...[" + new String(byteBuffer) + "] -- [" + byteBuffer.length + "]");
            System.out.println("DEMO GRAPHIC UPDATE REQUEST Send Successfully...[" + new String(byteBuffer) + "] -- [" + byteBuffer.length + "]");
        } catch (Exception ee) {
            String str = "";
            for (int i = 0; i < ee.getStackTrace().length; ++i) {
                str = str + ee.getStackTrace()[i] + "<br>";
            }
            System.out.println("Exception Occurred. Pushed to logs!");
//            System.out.println("Lower " + ee.getMessage());
            pushtologs("Exception (ER-Dallas) : " + str);
            try {

/*                hstmt2 = conn.createStatement();
                hstmt2.executeUpdate("UPDATE request SET status=0,posttime=now(),flag=flag+1  WHERE MSCID = '" + MSCID + "' ");
                hstmt2.close();*/

                pStmt = conn.prepareStatement("UPDATE oe.request SET status= ?, posttime= NOW(),flag= ? WHERE MSCID = ? ");
                pStmt.setInt(1, 0);
                pStmt.setInt(2, flag + 1);
                pStmt.setString(3, MSCID);
                pStmt.executeUpdate();
                pStmt.close();

                Connected = false;
                sock.close();
                conn.close();

                Thread.currentThread().stop();
                String[] a = null;
                main(a);
                pushtologs("Gone for Reconnection...");
            } catch (Exception e) {
                // TODO: handle exception
            }

            try {
                Thread.sleep(20000L);
            } catch (InterruptedException eedd) {
                ee.printStackTrace();
            }

            System.out.println("going for Reconnection....");
/*

            for (int i = 0; i < ee.getStackTrace().length; ++i) {
                str = str + ee.getStackTrace()[i] + "<br>";
            }
            System.out.println();
            System.out.println("Lower " + ee.getMessage());
            ee.getStackTrace();*/
        }
    }

    private static void pushtologs(String Message) {
        try {
            String UniqueId = "";
            UniqueId = TraceId;
            Date dt = GetDate();
            NumberFormat nf = new DecimalFormat("#00");


            String Eventtime = nf.format(dt.getYear() + 1900) + "_" + nf.format(dt.getMonth() + 1) + "_" + nf.format(dt.getDate()) + "_" + nf.format(dt.getHours()) + "_" + nf.format(dt.getMinutes()) + "_" + nf.format(dt.getSeconds());

            // System.out.println("CM " + version + " UID " + UniqueId + " -- " + Message);
            String FileName = clientid + "_" + clientdb + "_" + GetExceptionFileName();
            FileWriter fr = new FileWriter("/sftpdrive/tmp/" + FileName, true);
            fr.write(version + ": Event " + Eventtime + " UID " + UniqueId + "  Msg " + Message + "\r\n");
            fr.flush();
            fr.close();


        } catch (Exception e) {
            System.out.println("Unable to Generate Thread for Console Event " + e.getMessage());
        }
    }

    private static String GetExceptionFileName() {
        int temp = 0;
        try {
            Date dt = GetDate();
            NumberFormat nf = new DecimalFormat("#00");

            return nf.format(dt.getYear() + 1900) + "_" + nf.format(dt.getMonth() + 1) + "_" + nf.format(dt.getDate()) + ".log";
        } catch (Exception e) {
            return "invalid filename " + e.getMessage();
        }
    }

    private static Date GetDate() {
        try {
            return new Date();
        } catch (Exception localException) {
        }
        return null;
    }

    private static Connection getConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
//            final Connection connection = DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986");
//            final Connection connection = DriverManager.getConnection("jdbc:mysql://database-1.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986");
//            Connection connection = DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=Dev01kolkka&password=12Wasljro402330AA1&autoReconnect=true");
            Connection connection = DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=webserver873849&password=Asljdpiwoeurj!!3498&autoReconnect=true");
            return connection;

        } catch (Exception e) {
            System.out.println("WatchDog: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void run() {
        byte[] lenbuf = new byte[2];
        Connection conn = null;
/*        Statement hstmt = null;
        Statement hstmt2 = null;
        ResultSet hrset = null;
        String Query = "";*/
        try {
            System.out.println("In Read Thread  ..........");
            pushtologs("In Read Thread  ..........");
            this.sock.getInputStream().read(lenbuf);
            final int size = (lenbuf[0] & 0xFF) << 8 | (lenbuf[1] & 0xFF);
            final byte[] buf = new byte[size];
            final int readsize = sock.getInputStream().read(buf);
            String Response_ADT = new String(buf);

//            System.out.println("  READ   " + Response_ADT);
            pushtologs("  READ   " + Response_ADT);
            String bb[] = Response_ADT.split("\\|");
            if (bb.length >= 9) {
                String MSCID = bb[9];
                //System.out.println("dddd "+bb[11]);
//                System.out.println("  MSCID   " + MSCID);
                pushtologs("  MSCID   " + MSCID);

                conn = getConnection();
/*                Query = "update oe.request set Response = '" + Response_ADT + "', ResponseCode = 1 where Id = '" + TraceId + "'";

                System.out.println("TraceId: " + TraceId);
                pushtologs("Response updated TraceId: " + TraceId);
                hstmt = conn.createStatement();
                System.out.println("Query: " + Query);
                pushtologs("Query: " + Query);
                hstmt.executeUpdate(Query);
                hstmt.close();*/
                if (conn != null) {
                    pStmt = conn.prepareStatement("UPDATE oe.request SET Response= ?, ResponseCode= ? WHERE Id = ? ");
                    pStmt.setString(1, Response_ADT);
                    pStmt.setInt(2, 1);
                    pStmt.setString(3, TraceId);
                    pStmt.executeUpdate();
                    pStmt.close();
                } else
                    pushtologs("Conn is Null -->  " + conn);

                pushtologs("Response updated TraceId: " + TraceId);

/*                hstmt2 = conn.createStatement();
                hstmt2.executeUpdate("update " + clientdb + ".PatientReg set sync=1 where sync=0 and mrn=" + mrn);
                hstmt2.close();*/
                pStmt = conn.prepareStatement("UPDATE " + clientdb + ".PatientReg SET sync= ? WHERE sync= ? AND mrn = ? ");
                pStmt.setInt(1, 1);
                pStmt.setInt(2, 0);
                pStmt.setString(3, mrn);
                pStmt.executeUpdate();
                pStmt.close();

                TraceId = "";
                conn.close();
            }


            System.out.println("Message received from Server: " + new String(buf));
            pushtologs("Message received from Server: " + new String(buf));
        } catch (Exception ex) {
            try {
                conn.close();
            } catch (Exception e) {
            }
            ex.printStackTrace(System.out);
            System.out.println("Exception in Client Thread run method   : " + ex.getMessage());
            pushtologs("Exception in Client Thread run method   : " + ex.getMessage());
            final String[] a = null;
            try {
                Thread.sleep(20000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("going for Reconnection");
                pushtologs("going for Reconnection from run method   : " + ex.getMessage());
            }
            Thread.currentThread().stop();
            stop();

            main(a);
        }
        System.out.println("Read Thread Ends....");
        pushtologs("Read Thread Ends....");

    }

    protected void stop() {
        done = true;
        try {
            sock.close();
            Connected = false;
            System.out.println("in Thread stop ....");
            pushtologs("in Thread stop ....");
        } catch (Exception ex) {
            System.out.println("Exception in Thread stop ...." + ex.getMessage());
            pushtologs("Exception in Thread stop ...." + ex.getMessage());
        }
        sock = null;
    }
}
