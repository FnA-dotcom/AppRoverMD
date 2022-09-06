//
// Decompiled by Procyon v0.5.36
//

package oe;

import java.net.Socket;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Sublime_hl7loopsender implements Runnable
{
    private static boolean SignonDone;
    private Socket sock;
    private boolean done;
    private static int TraceNo;

    public Sublime_hl7loopsender(final Socket socket) {
        this.done = false;
        this.sock = socket;
        Sublime_hl7loopsender.TraceNo = 0;
    }

    static {
        Sublime_hl7loopsender.SignonDone = false;
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
            System.out.println("Starting   Client App ..........");
            System.out.println("Connecting to Modified server Ver. 2.5.2 Dated 7th Feb  2014 1502 hours");
            final int port = 10076;
            final boolean useTls = false;
            final String Host = "172.18.82.10";
            System.out.println("Connecting To  Database  @  localhost ");
            conn = getConnection();
            System.out.println("Connection: " + conn);
            System.out.println("Connecting To  Epower Doc socket  @  172.18.82.10 8083");
            sock = new Socket("172.18.82.10", 8083);
            System.out.println("|||||||||||");
        }
        catch (Exception e) {
            try {
                conn.close();
            }catch(Exception ee){}
            e.printStackTrace(System.out);
            System.out.println("Exception Creating Socket .........." + e.getMessage());
            final String[] a = null;
            try {
                Thread.sleep(20000L);
            }
            catch (InterruptedException ee) {
                ee.printStackTrace();
            }
            main(a);
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
            while (true) {
                try {
                    hstmt = conn.createStatement();
                    hstmt2 = conn.createStatement();
                    //Query = " SELECT id,msg,mrn,flag FROM request WHERE status=0 and ClientIndex = 15 ORDER BY requestdate";
                    Query = " SELECT id,msg,mrn,flag FROM request WHERE status=0 and ClientIndex = 15 LIMIT 2";
                    hrset = hstmt.executeQuery(Query);
                    while (hrset.next()) {
                        Id = hrset.getString(1);
                        msg = hrset.getString(2);
                        mrn = hrset.getString(3);
                        flag = hrset.getString(4);
                        if (flag.compareTo("1") == 0) {
                            flagvalue = "0";
                        }
                        switch (RequestType) {
                            case 1: {
                                sendmsg2(sock, msg, mrn, "0", conn);
                                hstmt2.executeUpdate("UPDATE request SET status=1,posttime=now() WHERE id=" + Id);
                                break;
                            }
                            case 2: {
                                sendmsg2(sock, msg, mrn, "1", conn);
                                hstmt2.executeUpdate("UPDATE request SET status=1,posttime=now() WHERE id=" + Id);
                                break;
                            }
                        }
                        Thread.sleep(750L);
                        final Sublime_hl7loopsender client = new Sublime_hl7loopsender(sock);
                        final Thread reader = new Thread(client, "Euro Client");
                        System.out.println("Creating  Thread To Read Response ..........");
                        reader.start();
                    }
                    hrset.close();
                    hstmt.close();
                    hstmt2.close();
                }
                catch (SQLException ee2) {
                    System.out.println("Error in Conn: " + ee2.getMessage());
                }
                final Date dt2 = new Date();
                if ((dt2.getTime() - StartTime) / 1000L > 45.0) {
                    final Sublime_hl7loopsender client2 = new Sublime_hl7loopsender(sock);
                    final Thread reader2 = new Thread(client2, "Euro Client");
                    System.out.println("Creating Thread To Read Response ..........");
                    reader2.start();
                    StartTime = dt2.getTime();
                }
                Thread.sleep(8000L);
            }
        }
        catch (Exception e) {
            System.out.println("Excep in main..." + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    public static void sendmsg2(final Socket sock, String msg, final String mrn, final String flag, final Connection conn) {
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
        final String NextofKinName = "";
        final String RelationToPatientER = "";
        final String PhoneNumberER = "";
        try {
            String testMessage = "This is a test message that the client will transmit";
            Query = "Select Title, FirstName, MiddleInitial, LastName, MaritalStatus, MRN, DOB, Age, Gender, Email, PhNumber, Address, City, State, Country,  ZipCode, SSN, Occupation, Employer, EmpContact, PriCarePhy, ReasonVisit, SelfPayChk, CreatedDate,sync,ID, Address2 from sublime.PatientReg where MRN ='" + mrn.trim() + "'";
            System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            System.out.println(Query);
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
                Address = rset.getString(12) + " " + rset.getString(26);
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
            }
            rset.close();
            stmt.close();
            System.out.println("sql pass");
            final Date dNow = new Date(System.currentTimeMillis() - 7200000L);
            final SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
            final String MSH7 = ft.format(dNow);
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
            MaritalStatus = MaritalStatus.substring(1, 1);
            testMessage = "MSH|^~\\&||665|ADT|3344|20200707020302||ADT^A04|20200707020302|P|2.3\r\nEVN|A04|20200707020302|||XXX^^^^^^^^488 \r\nPID|1||714629||Ali^test^d||19721027|F||W|209 HOPKINS ST^^LONDON^KY^41604|097|||ENGLISH|M|001|201963|987654321|||IRISH|MOBILE|||||||N \r\nNK1|1|MOON^KEITH^F|SPOUSE|209 HOPKINS ST^^LONDON^KY^41604|(555)123-4444|(555)123-8100|||||||||||||||||||||||EMERGENCY|MOON^KEITH^F \r\nPV1||3^E/R^02|||||194501^TOWNSHEND^PETE|194502^DALTREY^ROGER|194506^ROGERS^PAUL|E|||||||194501^TOWNSHEND^PETE|3||BB1||||||||||||||||G||||||||201603221352|201603221352\r\nAL1|1|MA|2484^PHARMACEUTICAL GLAZE \r\nAL1|2|DA|4558^13C UREA \r\nIN1|1|SELF-PAY^SELF PAY|SELF PAY|SELF PAY||PFO Sequence 99 Self P|||||||||4";
            String Synctype = "A04";
            if (sync.compareTo("1") == 0) {
                Synctype = "A08";
            }
            String IN1 = "IN1|1|SELF-PAY^SELF PAY|SELF PAY|SELF PAY||PFO Sequence 99 Self P|||||||||4";
            if (SelfPayChk == 1) {
                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%Y-%m-%d'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%Y-%m-%d'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from sublime.InsuranceInfo  where PatientRegId = " + Id;
                System.out.println(Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    WorkersCompPolicy = rset.getInt(1);
                    MotorVehAccident = rset.getInt(2);
                    PriInsurance = rset.getString(3);
                    MemId = rset.getString(4);
                    GrpNumber = rset.getString(5);
                    PriInsuranceName = rset.getString(6);
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
                }
                rset.close();
                stmt.close();
                if (PriInsuranceName.compareTo("-") == 0) {
                    Query = " Select PayerName from sublime.ProfessionalPayers where Id =  " + PriInsuranceName;
                    System.out.println(Query);
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        PriInsuranceName = rset.getString(1);
                    }
                    rset.close();
                    stmt.close();
                }
                IN1 = "IN1|1|" + MemId + "||" + PriInsurance + "||||" + GrpNumber + "|||||||||||||||||||||||||||||||||||";
            }
            msg = "MSH|^~\\&||665|ADT|3344|" + MSH7 + "||ADT^" + Synctype + "|" + MSH7 + "|P|2.3\r\n"
                    + "EVN|" + Synctype + "|20200707020302|||XXX^^^^^^^^488 \r\n" + "PID|1||" + MRN + "||" + LastName + "^" + FirstName + "^" + MiddleInitial + "||" + DOB + "|" + gender + "||W|" + Address + "^^" + City + "^" + State + "^" + ZipCode + "|" + PhNumber + "|" + PhNumber + "||ENGLISH|M|001||" + SSN + "|||||||||||N \r\n"
                    + "PV1||3^E/R^02|||||" + Address + "^^" + City + "^" + State + "^" + ZipCode + "|||||||||||||||||||||||||||||||||||||" + MSH7 + "|" + MSH7 + "\r\n"
                    + IN1;
            hstmt2 = conn.createStatement();
            hstmt2.executeUpdate("update sublime.PatientReg set sync=1 where sync=0 and id=" + Id);
            final byte[] byteBuffer = msg.getBytes();
            sock.getOutputStream().write(byteBuffer);
            System.out.println("DEMO GRAPHIC UPDATE REQUEST Send Successfully...[" + new String(byteBuffer) + "] -- [" + byteBuffer.length + "]");
        }
        catch (Exception ee) {
            System.out.println("Lower " + ee.getMessage());
            ee.getStackTrace();
        }
    }

    private static Connection getConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
//            final Connection connection = DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986");
//            final Connection connection = DriverManager.getConnection("jdbc:mysql://database-1.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986");
            Connection connection = DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=webserver873849&password=Asljdpiwoeurj!!3498");
            return connection;
        }
        catch (Exception e) {
            System.out.println("GetConnection: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void run() {
        final byte[] lenbuf = new byte[2];
        final Connection conn = null;
        final Statement hstmt = null;
        final ResultSet hrset = null;
        final String Query = "";
        try {
            System.out.println("In Read Thread  ..........");
            this.sock.getInputStream().read(lenbuf);
            final int size = (lenbuf[0] & 0xFF) << 8 | (lenbuf[1] & 0xFF);
            final byte[] buf = new byte[size];
            final int readsize = this.sock.getInputStream().read(buf);
            System.out.println("Message received from Server: " + new String(buf));
        }
        catch (Exception ex) {
            ex.printStackTrace(System.out);
            System.out.println("Exception in Client Thread run method   : " + ex.getMessage());
            final String[] a = null;
            try {
                Thread.sleep(20000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("going for Reconnection");
            }
            main(a);
        }
        System.out.println("Read Thread Ends....");
    }

    protected void stop() {
        this.done = true;
        try {
            System.out.println("in Thread stop ....");
        }
        catch (Exception ex) {
            System.out.println("Exception in Thread stop ...." + ex.getMessage());
        }
        this.sock = null;
    }
}
