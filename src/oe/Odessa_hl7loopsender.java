//
// Decompiled by Procyon v0.5.36
//

package oe;

import java.io.FileWriter;
import java.net.Socket;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
@SuppressWarnings("Duplicates")
public class Odessa_hl7loopsender implements Runnable
{
    private static boolean SignonDone;
    private static boolean Connected;
    private static String MRN = "";

    static {
        Odessa_hl7loopsender.SignonDone = false;
    }
    private static int TraceNo;
    private static String TraceId;
    private static String version;
    private static String clientid;
    private static String clientdb;

    private Socket sock;
    private boolean done;

    public Odessa_hl7loopsender(final Socket socket) {
        this.done = false;
        this.sock = socket;
        TraceNo = 0;
    }

    public static void main(final String[] args) {
        Connection conn = null;
        Statement hstmt = null;
        Statement hstmt2 = null;
        ResultSet hrset = null;
        String Query = "";
        long StartTime = 0L;
        Socket sock = null;
        try {
            final Date dt = new Date();
            StartTime = dt.getTime();

            version="v1.1";
            final int port = 10076;
            final boolean useTls = false;
            final String Host = "10.55.10.15";
            clientid="10";
            clientdb="oddasa";

            System.out.println("Starting   Client App ..........");
            System.out.println("Connecting to Modified server Ver. "+version+" Dated 02nd march  2021 0341 hours");

            System.out.println("Connecting To  Database  @  localhost ");

            //  System.out.println("Connection: " + conn);
//            System.out.println("Connecting To  Epower Doc socket  @  192.168.110.10 3389");
            System.out.println("Connecting To  Epower Doc socket  @  "+Host+" "+port+"");
//            sock = new Socket("192.168.110.10", 3389);
            pushtologs("Connecting To  Epower Doc socket  @  "+Host+" "+port+"");
            sock = new Socket(Host, port);
            System.out.println("Socket Done");
            pushtologs("Socket Done");
            Connected=true;
            conn = getConnection();
        }
        catch (Exception e) {
            try {
                Connected=false;
                sock.close();
                conn.close();
            }catch(Exception ee){}
            e.printStackTrace(System.out);
            System.out.println("Exception Creating Socket .........." + e.getMessage());
            pushtologs("Exception Creating Socket .........." + e.getMessage());
            final String[] a = null;
            try {

                Thread.sleep(20000L);
            }
            catch (InterruptedException ee) {
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
            String mrn = "";
            String flag = "";
            String flagvalue = "1";
            final String Param9 = "";
            System.out.println("Application SIGNON Successfull........");
            pushtologs("Application SIGNON Successfull........");
            while (true) {
                try {
                    Id = "";
                    msg = "";
                    mrn = "";
                    MRN = "";
                    hstmt = conn.createStatement();
                    hstmt2 = conn.createStatement();
                    Query = " SELECT id,msg,mrn,flag FROM oe.request WHERE status=0 and ClientIndex = "+clientid+" limit 2";
                    //System.out.println("Query request Tb: "+Query);
                    hrset = hstmt.executeQuery(Query);
                    while (hrset.next()) {
                        Id = hrset.getString(1);
                        TraceId = hrset.getString(1);
                        msg = hrset.getString(2);
                        mrn = hrset.getString(3);
                        flag = hrset.getString(4);
                        MRN = hrset.getString(3);

                        pushtologs("pull MRN "+mrn+" Successfull........");
                        pushtologs("pull MRN STATIC "+MRN+" Successfull........");

                        if (flag.compareTo("1") == 0) {
                            flagvalue = "0";
                        }
                        Date dNow = new Date(System.currentTimeMillis() - 7200000L);
                        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
                        String MSCID = ft.format(dNow);
                        switch (RequestType) {
                            case 1: {
                                sendmsg2(sock, msg, mrn, "0", conn, MSCID);
                                hstmt2.executeUpdate("UPDATE oe.request SET status=1,posttime=now(), MSCID = '"+MSCID+"' WHERE id=" + Id);
                                break;
                            }
                            case 2: {
                                sendmsg2(sock, msg, mrn, "1", conn, MSCID);
                                hstmt2.executeUpdate("UPDATE oe.request SET status=1,posttime=now(), MSCID = '"+MSCID+"' WHERE id=" + Id);
                                break;
                            }
                        }
                        Thread.sleep(750L);
                        final Odessa_hl7loopsender client = new Odessa_hl7loopsender(sock);
                        final Thread reader = new Thread(client, "Euro Client");
                        pushtologs("Creating  Thread To Read Response ..MRN."+mrn+".....IN");
                        System.out.println("Creating  Thread To Read Response ....MRN."+mrn+".....IN");
                        reader.start();
                    }
                    hrset.close();
                    hstmt.close();
                    hstmt2.close();

                }
                catch (SQLException ee2) {
                    System.out.println("Error in Conn: " + ee2.getMessage());
                    pushtologs("Error in Conn: " + ee2.getMessage());

                }
                final Date dt2 = new Date();
                if ((dt2.getTime() - StartTime) / 1000L > 45.0) {
                    final Odessa_hl7loopsender client2 = new Odessa_hl7loopsender(sock);
                    final Thread reader2 = new Thread(client2, "Euro Client");
                    System.out.println("Creating Thread To Read Response ..........");
                    pushtologs("Creating Thread To Read Response ..........OUT");
                    reader2.start();
                    StartTime = dt2.getTime();
                }
                Thread.sleep(8000L);
            }
        }
        catch (Exception e) {
            System.out.println("Excep in main..." + e.getMessage());
            pushtologs("Excep in main..." + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    public static void sendmsg2(final Socket sock, String msg, final String mrn, final String flag, final Connection conn, String MSCID) {
        Statement stmt = null;
        ResultSet rset = null;
        final Statement hstmt = null;
        final ResultSet hrset = null;
        Statement hstmt2 = null;
        String Query = "";
        String Title = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String MaritalStatus = "";
        String DOB = "";
        String Age = "";
        String gender = "";
        String Email = "";
        String PhNumber = "";
        String Address = "";
        String City = "";
        String State = "";
        String Country = "";
        String ZipCode = "";
        String SSN = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        String MRN = "";
        String DOS = "";
        String sync = "";
        String Id = "";
        int SelfPayChk = 0;
        int WorkersCompPolicy = 0;
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
        String PatientRelationshiptoSecondry = "";
        String NextofKinName = "";
        String RelationToPatientER = "";
        String PhoneNumberER = "";
        String Ethnicity = "";
        int VisitNumber = 0;
        int lenPRIInsurance = 0;
        try {
            String testMessage = "This is a test message that the client will transmit";
            Query = "Select Title, FirstName, MiddleInitial, LastName, MaritalStatus, MRN, DOB, Age, Gender, Email, PhNumber, Address, City, State, Country,  ZipCode, SSN, Occupation, Employer, EmpContact, PriCarePhy, ReasonVisit, SelfPayChk, CreatedDate,sync,ID, " +
                    "CASE WHEN Ethnicity = 1 THEN 'Hispanic' WHEN Ethnicity = 2 THEN 'Non-Hispanic' WHEN Ethnicity = 3 THEN 'Others' ELSE 'Others' END from "+clientdb+".PatientReg where MRN ='" + mrn.trim() + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            //System.out.println(Query);
            if (rset.next()) {
                Title = rset.getString(1);
                FirstName = rset.getString(2);
                MiddleInitial = rset.getString(3);
                LastName = rset.getString(4);
                MaritalStatus = rset.getString(5);
                MRN = rset.getString(6);
                DOB = rset.getString(7);
                Age = rset.getString(8);
                gender = rset.getString(9);
                Email = rset.getString(10);
                PhNumber = rset.getString(11);
                Address = rset.getString(12);
                City = rset.getString(13);
                State = rset.getString(14);
                Country = rset.getString(15);
                ZipCode = rset.getString(16);
                SSN = rset.getString(17);
                Occupation = rset.getString(18);
                Employer = rset.getString(19);
                EmpContact = rset.getString(20);
                PriCarePhy = rset.getString(21);
                ReasonVisit = rset.getString(22);
                SelfPayChk = rset.getInt(23);
                DOS = rset.getString(24);
                sync = rset.getString(25);
                Id = rset.getString(26);
                Ethnicity = rset.getString(27);
            }
            rset.close();
            stmt.close();

            Query = "Select MAX(VisitNumber) from "+clientdb+".PatientVisit where PatientRegId = "+Id;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            //System.out.println(Query);
            if (rset.next()) {
                VisitNumber = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            System.out.println("sql pass");
            final String MRN2 = "3109900";
            DOB = DOB.replace("-", "");
            if (gender.toUpperCase().compareTo("FEMALE") == 0) {
                gender = "F";
            }
            else {
                gender = "M";
            }
            System.out.println("G& M pass");
            System.out.println(MaritalStatus);
            MaritalStatus = MaritalStatus.substring(0, 1);
            testMessage = "MSH|^~\\&||665|ADT|3344|20200707020302||ADT^A04|20200707020302|P|2.3\r\nEVN|A04|20200707020302|||XXX^^^^^^^^488 \r\nPID|1||714629||Ali^test^d||19721027|F||W|209 HOPKINS ST^^LONDON^KY^41604|097|||ENGLISH|M|001|201963|987654321|||IRISH|MOBILE|||||||N \r\nNK1|1|MOON^KEITH^F|SPOUSE|209 HOPKINS ST^^LONDON^KY^41604|(555)123-4444|(555)123-8100|||||||||||||||||||||||EMERGENCY|MOON^KEITH^F \r\nPV1||3^E/R^02|||||194501^TOWNSHEND^PETE|194502^DALTREY^ROGER|194506^ROGERS^PAUL|E|||||||194501^TOWNSHEND^PETE|3||BB1||||||||||||||||G||||||||201603221352|201603221352\r\nAL1|1|MA|2484^PHARMACEUTICAL GLAZE \r\nAL1|2|DA|4558^13C UREA \r\nIN1|1|SELF-PAY^SELF PAY|SELF PAY|SELF PAY||PFO Sequence 99 Self P|||||||||4";
            String Synctype = "A04";
            if (sync.compareTo("1") == 0) {
                Synctype = "A08";
            }
            String IN1 = "IN1|1|SELF-PAY^SELF PAY|SELF PAY|SELF PAY||PFO Sequence 99 Self P|||||||||4";
            if (SelfPayChk == 1) {
                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), " +
                        "IFNULL(GrpNumber,'-'),  IFNULL(LTRIM(RTRIM(PriInsuranceName)),'-'), IFNULL(AddressIfDifferent,'-'), " +
                        "IFNULL(DATE_FORMAT(PrimaryDOB,'%Y-%m-%d'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), " +
                        "IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), " +
                        "IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%Y-%m-%d'),'-'),  " +
                        "IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-'),LENGTH(PriInsuranceName)," +
                        "(PriInsuranceName REGEXP '(^[[:space:]]|[[:space:]]$)') " +
                        "from "+clientdb+".InsuranceInfo  where PatientRegId = " + Id;
                //System.out.println("SelfPayChk" + Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    WorkersCompPolicy = rset.getInt(1);
                    MotorVehAccident = rset.getInt(2);
                    PriInsurance = rset.getString(3);
                    MemId = rset.getString(4);
                    GrpNumber = rset.getString(5);
                    PriInsuranceName = rset.getString(6).trim();
                    //PriInsuranceName = rset.getString(22).trim();
                    AddressIfDifferent = rset.getString(7);
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
                    GroupNumber_2 = rset.getString(20);
                    lenPRIInsurance = rset.getInt(22);
                }
                rset.close();
                stmt.close();

//                if (lenPRIInsurance != 0) {
                //this is where i change
                if (!PriInsuranceName.equals("-") && !PriInsuranceName.equals("")) {
                    Query = " Select PayerName from "+clientdb+".ProfessionalPayers where Id =  " + PriInsuranceName;
                    //System.out.println("PayerName " + Query);
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PriInsuranceName = rset.getString(1).trim();
                    }
                    rset.close();
                    stmt.close();
                }
                //IN1 = "IN1|1|" + MemId + "||" + PriInsurance + "||||" + GrpNumber + "|||||||||||||||||||||||||||||||||||\r\n";
//                IN1 = "IN1|1|||" + PriInsurance + "|^^^^|||" + GrpNumber + "||||||||||||||||||||||||||||"+MemId+"|||||||||||||"+MemId+"\r\n";
//                IN1 = "IN1|1|||BCBS TX|PO Box 660044^^Dallas^TX^752660044||||||||||G|VALANCE^LIBERTY^L|SELF|19840701|1 SHINBONE WAY^^TEXARKANA^TX^75501|Y||||||||||||P||||ZGN987654321|||||||F||||M\n";
//                IN1 = "IN1|1|||" + PriInsuranceName + "|^^^^||||||||||G|||"+GrpNumber+"||Y||||||||||||||||"+MemId+"|||||||F||||M\r\n";
                IN1 = "IN1|1|||" + PriInsuranceName + "|^^^^||||||||||G|||"+GrpNumber+"||Y||||||||||||||||"+MemId+"|||||||F||||M\r\n";
            }
            String Race = "";
            Query = "Select IFNULL(NextofKinName,''), IFNULL(RelationToPatient,''), IFNULL(PhoneNumber,'') from "+clientdb+".EmergencyInfo " +
                    "where PatientRegId = "+Id;
            //System.out.println("TAB " + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                NextofKinName = rset.getString(1);
                RelationToPatientER = rset.getString(2);
                PhoneNumberER = rset.getString(3);
            }
            rset.close();
            stmt.close();
            Query = "Select CASE WHEN Race = 1 THEN 'Black or African American' WHEN Race = 2 THEN 'American Indian' WHEN Race = 3 THEN 'Asian' WHEN Race = 4 THEN 'Native Hawaiian or Other Specific Islander' WHEN Race = 5 THEN 'White' WHEN Race = 6 THEN 'Others' ELSE 'Others' END " +
                    "from "+clientdb+".PatientReg_Details where PatientRegId = "+Id;
            //System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Race = rset.getString(1);
            }
            rset.close();
            stmt.close();

            PhoneNumberER = PhoneNumberER.replaceAll("-","");
            //PhNumber = PhNumber.replaceAll("-","");
//            msg = "MSH|^~\\&||665|ADT|3344|" + MSH7 + "||ADT^" + Synctype + "|" + MSH7 + "|P|2.3\r\n"
//                    + "EVN|" + Synctype + "|20200707020302|||XXX^^^^^^^^488 \r\n" + "PID|1||" + MRN + "||" + LastName + "^" + FirstName + "^" + MiddleInitial + "||" + DOB + "|" + gender + "||W|" + Address + "^^" + City + "^" + State + "^" + ZipCode + "|" + PhNumber + "|" + PhNumber + "||ENGLISH|M|001||" + SSN + "|||||||||||N \r\n"
//                    + "PV1||3^E/R^02|||||" + Address + "^^" + City + "^" + State + "^" + ZipCode + "|||||||||||||||||||||||||||||||||||||" + MSH7 + "|" + MSH7 + "\r\n"
//                    + IN1;
            String NK1 = "";
            NK1 = "NK1|1|"+""+"^"+NextofKinName+"^^|"+RelationToPatientER+"|^^^^|"+PhoneNumberER+"||EMERGENCY\r\n";
            String MSH7 = MSCID;
            msg = "MSH|^~\\&||665|ADT|3344|" + MSH7 + "||ADT^" + Synctype + "|" + MSH7 + "|P|2.3\r\n"
                    + "EVN|" + Synctype + "|"+MSH7+"|||XXX^^^^^^^^488 \r\n"
                    + "PID|1||" + MRN + "||"+LastName + "^" + FirstName + "^" + MiddleInitial + "||" + DOB + "|" + gender + "||"+Race+"| "
                    + Address + "^^" + City + "^" + State + "^" + ZipCode + "|" + "" +"|" + PhNumber + "^^CP^^^" + ""+"^"+ ""
                    + "|"+EmpContact+"^^PH^"+Email+"^^" + "" +"||"+MaritalStatus+"||"+""+"|"
                    + SSN +"|||"+Ethnicity+"\r\n"
                    +"PV1||3^E/R^02|EMERGENCY ROOM|||||||||||||||||||||||||||||||||||||||||"+MSH7+"|\r\n"
//                    +"GT1|1|"+"230"+"|"+"Test^Test^M"+"||"+""+"|"+"(222)555-5698"+"^^CP^^^"+""+"||"+""+"|"+""+"\r\n"
                    +"GT1|1|"+"230"+"|"+""+"||"+""+"|"+""+"^^CP^^^"+""+"||"+""+"|"+""+"\r\n"
                    + IN1
                    + NK1;
            final byte[] byteBuffer = msg.getBytes();
            sock.getOutputStream().write(byteBuffer);
            pushtologs("DEMO GRAPHIC UPDATE REQUEST Send Successfully...[" + new String(byteBuffer) + "] -- [" + byteBuffer.length + "]");
            System.out.println("DEMO GRAPHIC UPDATE REQUEST Send Successfully...[" + new String(byteBuffer) + "] -- [" + byteBuffer.length + "]");
        }
        catch (Exception ee) {
            System.out.println("Lower " + ee.getMessage());
            pushtologs("Lower " + ee.getMessage());
            try {
                hstmt2 = conn.createStatement();
                hstmt2.executeUpdate("UPDATE oe.request SET status=0,posttime=now() WHERE MSCID = '"+MSCID+"' ");
                hstmt2.close();
                Connected=false;
                sock.close();
                conn.close();
            } catch (Exception e) {
                // TODO: handle exception
            }

            final String[] a = null;
            try {
                Thread.sleep(20000L);
            }
            catch (InterruptedException eedd) {
                ee.printStackTrace();
            }
            Thread.currentThread().stop();

            main(a);
            pushtologs("going for Reconnection");

            System.out.println("going for Reconnection");

            String str = "";
            for (int i = 0; i < ee.getStackTrace().length; ++i) {
                str = str + ee.getStackTrace()[i] + "<br>";
            }
            System.out.println();
            System.out.println("Lower " + ee.getMessage());
            ee.getStackTrace();
        }
    }

    private static void pushtologs(String Message)
    {
        try
        {
            String UniqueId = "";
            UniqueId = TraceId;
            Date dt = GetDate();
            NumberFormat nf = new DecimalFormat("#00");

            String Eventtime=nf.format(dt.getYear() + 1900) + "_" + nf.format(dt.getMonth() + 1) + "_" + nf.format(dt.getDate()) +"_" + nf.format(dt.getHours()) + "_" + nf.format(dt.getMinutes()) +"_" + nf.format(dt.getSeconds());
            // System.out.println("CM " + version + " UID " + UniqueId + " -- " + Message);
            String FileName = clientid+"_"+clientdb+"_"+GetExceptionFileName();
            FileWriter fr = new FileWriter("/sftpdrive/tmp/"+ FileName, true);
            fr.write(version + ": Event " + Eventtime+ " UID " + UniqueId + "  Msg " + Message + "\r\n");
            fr.flush();
            fr.close();
        }
        catch (Exception e)
        {
            System.out.println("Unable to Generate Thread for Console Event " + e.getMessage());
        }
    }

    private static String GetExceptionFileName()
    {
        int temp = 0;
        try
        {
            Date dt = GetDate();
            NumberFormat nf = new DecimalFormat("#00");

            return nf.format(dt.getYear() + 1900) + "_" + nf.format(dt.getMonth() + 1) + "_" + nf.format(dt.getDate()) + ".log";
        }
        catch (Exception e)
        {
            return "invalid filename " + e.getMessage();
        }
    }

    private static Date GetDate()
    {
        try
        {
            return new Date();
        }
        catch (Exception localException) {}
        return null;
    }

    private static Connection getConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
//            final Connection connection = DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986");
//            final Connection connection = DriverManager.getConnection("jdbc:mysql://database-1.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986");
//            Connection connection = DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=12Wasljro402330AA1&password=Dev01kolkka");
            Connection connection = DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=webserver873849&password=Asljdpiwoeurj!!3498&autoReconnect=true");
            return connection;
        }
        catch (Exception e) {
            System.out.println("GetConnection: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void run() {
        byte[] lenbuf = new byte[2];
        Connection conn = null;
        Statement hstmt = null;
        Statement hstmt2 = null;
        ResultSet hrset = null;
        String Query = "";
        String Query2 = "";
        try {
            System.out.println("In Read Thread  ..........");
            pushtologs("In Read Thread  ..........");
            this.sock.getInputStream().read(lenbuf);
            final int size = (lenbuf[0] & 0xFF) << 8 | (lenbuf[1] & 0xFF);
            final byte[] buf = new byte[size];
            final int readsize = this.sock.getInputStream().read(buf);
            String Response_ADT = new String(buf);

            System.out.println("  READ   "+ Response_ADT);
            pushtologs("  READ   "+ Response_ADT);
            String bb  []= Response_ADT.split("\\|");
            if(bb.length>=9) {
                String MSCID = bb[9];
                //System.out.println("dddd "+bb[11]);
                System.out.println("  MSCID   "+ MSCID);
                pushtologs("  MSCID   "+ MSCID);
                Query = "update oe.request set Response = '"+Response_ADT+"', ResponseCode = 1 where Id = '" + TraceId+"'";
                conn = getConnection();
                System.out.println("TraceId: "+TraceId);
                System.out.println("STATIC MRN: "+MRN);
                pushtologs("Response updated TraceId: "+TraceId);
                hstmt = conn.createStatement();
                System.out.println("Query: "+Query);
                pushtologs("Query: "+Query);
                hstmt.executeUpdate(Query);
                hstmt.close();
                pushtologs("Response updated TraceId: "+TraceId);
                pushtologs("STATIC MRN: "+MRN);

                //Query2 = "update "+clientdb+".PatientReg set sync=1 where sync=0 and id=" + TraceId;
                Query2 = "update "+clientdb+".PatientReg set sync=1 where sync=0 and MRN=" + MRN;
                System.out.println("Query22222222: "+Query2);
                pushtologs("Query222222: "+Query2);
                hstmt2 = conn.createStatement();
                hstmt2.executeUpdate(Query2);
                hstmt2.close();
                TraceId="";
                MRN = "";
                conn.close();
            }


            System.out.println("Message received from Server: " + new String(buf));
            pushtologs("Message received from Server: " + new String(buf));
        }
        catch (Exception ex) {
            try{
                conn.close();
            }catch(Exception e){}
            ex.printStackTrace(System.out);
            System.out.println("Exception in Client Thread run method   : " + ex.getMessage());
            pushtologs("Exception in Client Thread run method   : " + ex.getMessage());
            final String[] a = null;
            try {
                Thread.sleep(20000L);
            }
            catch (InterruptedException e) {
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
            Connected=false;
            System.out.println("in Thread stop ....");
            pushtologs("in Thread stop ....");
        }
        catch (Exception ex) {
            System.out.println("Exception in Thread stop ...." + ex.getMessage());
            pushtologs("Exception in Thread stop ...." + ex.getMessage());
        }
        sock = null;
    }
}
