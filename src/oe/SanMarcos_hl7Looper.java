package oe;

import java.io.FileWriter;
import java.net.Socket;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//789
@SuppressWarnings("Duplicates")
public class SanMarcos_hl7Looper implements Runnable {
    private static int TraceNo;
    private static boolean SignonDone;
    private static boolean Connected;
    private static String TraceId;
    private static String version;
    private static String clientid;
    private static String clientdb;
    private static String mrn;

    static {
        SanMarcos_hl7Looper.SignonDone = false;
    }

    private Socket sock;
    private boolean done;

    public SanMarcos_hl7Looper(final Socket socket) {
        this.done = false;
        this.sock = socket;
        TraceNo = 0;
    }

    public static void main(String[] args) {
        Connection conn = null;
        Statement hstmt = null;
        ResultSet hrset = null;
        Statement hstmt3 = null;
        ResultSet hrset3 = null;
        String Query = "";
        long StartTime = 0L;
        Socket sock = null;
        PreparedStatement pStmt = null;

        try {
            final Date dt = new Date();
            StartTime = dt.getTime();

            version = "v1.2";
            final int port = 8093;
            final boolean useTls = false;
            final String Host = "192.168.168.18";
            clientid = "";
            clientdb = "";

            System.out.println("Initializing the application ..........");
            System.out.println("Connecting to EPD server Ver. " + version + " Dated 8 February 2022");
            System.out.println("Connecting To  Database  @  localhost ");
            System.out.println("Connecting To  Epower Doc socket  @  " + Host + " " + port + "");
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
            final String TransId = "";
            final int RequestType = 1;
            String Id = "";
            String msg = "";
            mrn = "";
            String flag = "";
            int FacilityId = 0;
            System.out.println("Application SIGNON Successfull........");
            pushtologs("Application SIGNON Successfull........");
            while (true) {
                try {
                    Id = "";
                    msg = "";
                    mrn = "";
                    hstmt = conn.createStatement();
                    Query = " SELECT id,msg,mrn,flag,ClientIndex FROM request " +
                            "WHERE status=0 and ClientIndex in (25) " +
                            "and flag <= 3 limit 2";
                    hrset = hstmt.executeQuery(Query);
                    while (hrset.next()) {
                        Id = hrset.getString(1);
                        TraceId = hrset.getString(1);
                        msg = hrset.getString(2);
                        mrn = hrset.getString(3);
                        flag = hrset.getString(4);
                        clientid = hrset.getString(5);
                        pushtologs("pull MRN " + mrn + " Successfull........");

                        String Queryyy = "Select dbname from oe.clients where Id = " + clientid;
                        hstmt3 = conn.createStatement();
                        hrset3 = hstmt3.executeQuery(Queryyy);
                        if (hrset3.next()) {
                            clientdb = hrset3.getString(1);
                        }
                        hrset3.close();
                        hstmt3.close();

                        //Willowbrook - 41
                        //Summerwood - 42
                        // Height - 43
/*                        if (clientid.equals("41")) {
                            FacilityId = 786;
                        } else if (clientid.equals("42")) {
                            FacilityId = 773;
                        }*/
                        FacilityId = 789;
                        Date dNow = new Date(System.currentTimeMillis() - 7200000L);
                        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
                        String MSCID = ft.format(dNow);
                        switch (RequestType) {
                            case 1: {
                                sendmsg2(sock, msg, mrn, 0, conn, MSCID, FacilityId);
                                pStmt = conn.prepareStatement("UPDATE oe.request SET status= ?, posttime= NOW(), MSCID=? WHERE Id = ? ");
                                pStmt.setInt(1, 1);
                                pStmt.setString(2, MSCID);
                                pStmt.setString(3, Id);
                                pStmt.executeUpdate();
                                pStmt.close();

                                break;
                            }
                            case 2: {
                                sendmsg2(sock, msg, mrn, 1, conn, MSCID, FacilityId);
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
                        final SanMarcos_hl7Looper client = new SanMarcos_hl7Looper(sock);
                        final Thread reader = new Thread(client, "Euro Client");
                        pushtologs("Creating  Thread To Read Response ..MRN." + mrn + ".....IN");
                        System.out.println("Creating  Thread To Read Response ....MRN." + mrn + ".....IN");
                        reader.start();
                    }
                    hrset.close();
                    hstmt.close();

                } catch (SQLException ee2) {
                    System.out.println("Error in Conn: " + ee2.getMessage());
                    pushtologs("Error in Conn: " + ee2.getMessage());

                }
                Thread.sleep(8000L);
            }
        } catch (Exception e) {
            System.out.println("Excep in main..." + e.getMessage());
            pushtologs("Excep in main..." + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    public static void sendmsg2(final Socket sock, String msg, final String mrn, final int flag, final Connection conn, String MSCID, int FacilityId) {
        Statement stmt = null;
        ResultSet rset = null;
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
//        String Occupation = "";
//        String Employer = "";
        String EmpContact = "";
//        String PriCarePhy = "";
//        String ReasonVisit = "";
        String MRN = "";
//        String DOS = "";
        String sync = "";
        String Id = "";
        int SelfPayChk = 0;
/*        int WorkersCompPolicy = 0;
        int MotorVehAccident = 0;
        String PriInsurance = "";*/
        String MemId = "";
        String GrpNumber = "";
        String PriInsuranceName = "";
/*         String AddressIfDifferent = "";
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
        String Ethnicity = "";
        String Race = "";
        String VisitNumber = "";
        String GuarantorNumber = "";
        String GuarantorName = "";
        try {
            Query = "Select Title, FirstName, MiddleInitial, LastName, IFNULL(MaritalStatus,''), MRN, DOB, Age, Gender, " +
                    "Email, PhNumber, Address, City, State, Country,  ZipCode, SSN, Occupation, Employer, " +
                    "IFNULL(EmpContact,''), PriCarePhy, ReasonVisit, SelfPayChk, CreatedDate,sync,ID, " +
                    "CASE WHEN Ethnicity = 1 THEN 'Hispanic' WHEN Ethnicity = 2 THEN 'Non-Hispanic' " +
                    "WHEN Ethnicity = 3 THEN 'Others' ELSE 'Others' END " +
                    "from " + clientdb + ".PatientReg where MRN ='" + mrn.trim() + "' AND Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
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
                EmpContact = rset.getString(20);
//                PriCarePhy = rset.getString(21);
//                ReasonVisit = rset.getString(22);
                SelfPayChk = rset.getInt(23);
//                DOS = rset.getString(24);
                sync = rset.getString(25);
                Id = rset.getString(26);
                Ethnicity = rset.getString(27);
            }
            rset.close();
            stmt.close();

            if (!EmpContact.equals("")) {
                EmpContact = EmpContact.replace("-", "");
            } else {
                EmpContact = "";
            }

            PhNumber = PhNumber.replace("-", "");
            DOB = DOB.replace("-", "");
            if (gender.toUpperCase().compareTo("FEMALE") == 0) {
                gender = "F";
            } else {
                gender = "M";
            }
            if (!MaritalStatus.equals("")) {
                MaritalStatus = MaritalStatus.substring(0, 1);
            } else {
                MaritalStatus = "";
            }
            String Synctype = "A04";
            if (sync.compareTo("1") == 0) {
                Synctype = "A08";
            }
            String IN1 = "IN1|1|SELF-PAY^SELF PAY|SELF PAY|SELF PAY||PFO Sequence 99 Self P|||||||||4";
            if (SelfPayChk == 1) {
                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-')," +
                        "IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), " +
                        "IFNULL(DATE_FORMAT(PrimaryDOB,'%Y-%m-%d'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), " +
                        "IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  " +
                        "IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), " +
                        "IFNULL(DATE_FORMAT(SubscriberDOB,'%Y-%m-%d'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), " +
                        "IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from " + clientdb + ".InsuranceInfo  " +
                        "where PatientRegId = " + Id;
//                System.out.println(Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
/*                    WorkersCompPolicy = rset.getInt(1);
                    MotorVehAccident = rset.getInt(2);
                    PriInsurance = rset.getString(3);*/
                    MemId = rset.getString(4);
                    GrpNumber = rset.getString(5);
                    PriInsuranceName = rset.getString(6);
/*                    AddressIfDifferent = rset.getString(7);
                    PrimaryDOB = rset.getString(8);
                    PrimarySSN = rset.getString(9);
                    PatientRelationtoPrimary = rset.getString(10);
                    PrimaryOccupation = rset.getString(11);
                    PrimaryEmployer = rset.getString(12);
                    EmployerAddress = rset.getString(13);
                    EmployerPhone = rset.getString(14);
                    SecondryInsurance = rset.getString(15);
                    SubscriberName = rset.getString(16);
                    SubscriberDOB = rset.getString(17);
                    PatientRelationshiptoSecondry = rset.getString(18);
                    MemberID_2 = rset.getString(19);
                    GroupNumber_2 = rset.getString(20);*/
                }
                rset.close();
                stmt.close();

                if (!PriInsuranceName.equals("-") && !PriInsuranceName.equals("")) {
                    Query = " Select PayerName from oe_2.ProfessionalPayers where Id =  '" + PriInsuranceName + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PriInsuranceName = rset.getString(1);
                    }
                    rset.close();
                    stmt.close();
                    PriInsuranceName = PriInsuranceName.replaceAll("(\\r|\\n)", "");
                }

                IN1 = "IN1|1|||" + PriInsuranceName + "|^^^^|||" + GrpNumber + "|||||||G|||||Y||||||||||||||||" + MemId + "|||||||F||||M\r\n";
            }
            Query = "Select IFNULL(NextofKinName,''), IFNULL(RelationToPatient,''), IFNULL(PhoneNumber,'') " +
                    "from " + clientdb + ".EmergencyInfo where PatientRegId = " + Id;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                NextofKinName = rset.getString(1);
                RelationToPatientER = rset.getString(2);
                PhoneNumberER = rset.getString(3);
            }
            rset.close();
            stmt.close();

            Query = "Select CASE " +
                    "WHEN Race = 1 THEN 'Black or African American' " +
                    "WHEN Race = 2 THEN 'American Indian' " +
                    "WHEN Race = 3 THEN 'Asian' " +
                    "WHEN Race = 4 THEN 'Native Hawaiian or Other Specific Islander' " +
                    "WHEN Race = 5 THEN 'White' " +
                    "WHEN Race = 6 THEN 'Others' " +
                    "ELSE 'Others' END," +
                    "IFNULL(GuarantorName,''), IFNULL(GuarantorNumber,'') " +
                    "from " + clientdb + ".PatientReg_Details where PatientRegId = " + Id;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Race = rset.getString(1);
                GuarantorName = rset.getString(2);
                GuarantorNumber = rset.getString(3);
            }
            rset.close();
            stmt.close();

            PhoneNumberER = PhoneNumberER.replaceAll("-", "");

            Query = "Select MAX(VisitNumber) from " + clientdb + ".PatientVisit where PatientRegId = " + Id;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                VisitNumber = rset.getString(1).trim();
            }
            rset.close();
            stmt.close();

            VisitNumber = "VN-" + MRN + "-" + VisitNumber;
            String NK1 = "";
            NK1 = "NK1|1|" + "" + "^" + NextofKinName + "^^|" + RelationToPatientER + "|^^^^|" + PhoneNumberER + "||EMERGENCY\r\n";
            String MSH7 = MSCID;
            msg = "MSH|^~\\&||" + FacilityId + "|ADT|3344|" + MSH7 + "||ADT^" + Synctype + "|" + MSH7 + "|P|2.3\r\n"
                    + "EVN|" + Synctype + "|" + MSH7 + "|||XXX^^^^^^^^488 \r\n"
                    + "PID|1||" + MRN + "||" + LastName + "^" + FirstName + "^" + MiddleInitial + "||" + DOB + "|" + gender + "||" + Race + "| "
                    + Address + "^^" + City + "^" + State + "^" + ZipCode + "|" + "" + "|" + PhNumber + "^^CP^^^" + "" + "^" + ""
                    + "|" + EmpContact + "^^PH^" + Email + "^^" + "" + "||" + MaritalStatus + "||" + VisitNumber + "|"
                    + SSN + "|||" + Ethnicity + "\r\n"
                    + "PV1||3^E/R^02|EMERGENCY ROOM|||||||||||||||||||||||||||||||||||||||||" + MSH7 + "|\r\n"
                    + "GT1|1|" + "230" + "|" + "" + "^" + GuarantorName + "^" + "||" + "" + "|" + GuarantorNumber + "^^CP^^^" + "" + "||" + "" + "|" + "" + "\r\n"
                    + IN1
                    + NK1;
            String MSH = "MSH|^~\\&||" + FacilityId + "|ADT|3344|" + MSH7 + "||ADT^" + Synctype + "|" + MSH7 + "|P|2.3";
            String EVN = "EVN|" + Synctype + "|" + MSH7 + "|||XXX^^^^^^^^488";
            String PID = "PID|1||" + MRN + "||" + LastName + "^" + FirstName + "^" + MiddleInitial + "||" + DOB + "|" + gender + "||" + Race + "|" + Address + "^^" + City + "^" + State + "^" + ZipCode + "||" + PhNumber + "^^CP^^^" + "" + "^" + "" + "|" + EmpContact + "^^PH^" + Email + "^^" + "" + "^" + "" + "||" + MaritalStatus + "||" + VisitNumber + "|" + SSN + "|||" + Ethnicity;

            String PV1 = "PV1||3^E/R^02|EMERGENCY ROOM|||||||||||||||||||||||||||||||||||||||||" + MSH7 + "|";
            String GT1 = "GT1|1|" + "230" + "|" + "" + "^" + GuarantorName + "^" + "||" + "" + "|" + GuarantorNumber + "^^CP^^^" + "" + "||" + "" + "|" + "" + "";

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

                    .append(IN1)
                    .append(CARRIAGE_RETURN)

                    .append(NK1)
                    .append(CARRIAGE_RETURN)

                    .append(END_OF_BLOCK)
                    .append(CARRIAGE_RETURN);


            byte[] byteBuffer = msg.getBytes();
            sock.getOutputStream().write(testHL7MessageToTransmit.toString().getBytes());

            PreparedStatement pStmt = null;
            pStmt = conn.prepareStatement("UPDATE oe.request SET msg= ? WHERE Id = ? ");
            pStmt.setString(1, msg);
            pStmt.setString(2, TraceId);
            pStmt.executeUpdate();
            pStmt.close();

            pushtologs("DEMO GRAPHIC UPDATE REQUEST Send Successfully...[" + new String(byteBuffer) + "] -- [" + byteBuffer.length + "]");
            System.out.println("DEMO GRAPHIC UPDATE REQUEST Send Successfully...[" + new String(byteBuffer) + "] -- [" + byteBuffer.length + "]");
        } catch (Exception ee) {
            String str = "";
            for (int i = 0; i < ee.getStackTrace().length; ++i) {
                str = str + ee.getStackTrace()[i] + "<br>";
            }
            System.out.println("Exception Occurred. Pushed to logs!");
            pushtologs("Exception (MY ER - " + clientdb + ") : " + ee.getMessage());
            pushtologs("Exception (MY ER - " + clientdb + ") : " + str);
            try {
                PreparedStatement pStmt = null;
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
                pushtologs("going for Reconnection...");
            } catch (Exception e) {
                // TODO: handle exception
            }

            try {
                Thread.sleep(20000L);
            } catch (InterruptedException eedd) {
                ee.printStackTrace();
            }

            System.out.println("going for Reconnection");
/*            for (int i = 0; i < ee.getStackTrace().length; ++i) {
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
        PreparedStatement pStmt = null;
        try {
            System.out.println("In Read Thread  ..........");
            pushtologs("In Read Thread  ..........");
            this.sock.getInputStream().read(lenbuf);
            final int size = (lenbuf[0] & 0xFF) << 8 | (lenbuf[1] & 0xFF);
            final byte[] buf = new byte[size];
            final int readsize = sock.getInputStream().read(buf);
            String Response_ADT = new String(buf);

            System.out.println("  READ   " + Response_ADT);
            pushtologs("  READ   " + Response_ADT);
            String bb[] = Response_ADT.split("\\|");
            if (bb.length >= 9) {
                String MSCID = bb[9];
                System.out.println("  MSCID   " + MSCID);
                pushtologs("  MSCID   " + MSCID);

                conn = getConnection();
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
