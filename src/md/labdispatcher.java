package md;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class labdispatcher {
    final static String className = "lab01";
    static String locationindex = "";

    public static void main(String[] args) {

        long StartTime = 0L;
        Connection conn = null;
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String Filepath = "d:\\temp\\";

        Filepath = "/sftpdrive/lab/orders/";
        String Database = "roverlab";
        String facilityid = "36";
        String facilityid_lis = "PD";
        String facilityname = "Primescope Diagnostics";
        String SampleNumber = "";

        String t_id, OrderNum, TestStatus, Narration, OrderDate, Physicianidx, Testidx, teststageidx, CollectionDateTime, Stageidx, PatRegidx;
        try {

			/*if (AppRunning()) // single threath only -- for Linux
			{
				System.out.println("Unable to start  Process Already Running ");
				return;
			}
			*/
            final Date dt = new Date();
            StartTime = dt.getTime();
            System.out.println("Starting   Client App ..........");
            System.out.println("Connecting to Modified server Ver. 1.5.2 Dated 10th Feb  2022 1502 hours");
            System.out.println("Connecting To  Database  @  localhost ");
            conn = getConnection();
            //System.out.println("Connection: " + conn);
            System.out.println("|||||||||||");

            HashMap<String, String> test_liscode = new HashMap<String, String>();
            HashMap<String, String> doc_liscode = new HashMap<String, String>();
            HashMap<String, String> loc_liscode = new HashMap<String, String>();

            test_liscode = listoftest(conn, Database);
            doc_liscode = listofDoc(conn, Database);
            loc_liscode = listoflocation(conn, Database);
            System.out.println(test_liscode);
            System.out.println(doc_liscode);
            Query = "select a.id,b.OrderNum,a.TestStatus,a.Narration,ifnull(a.Physicianidx,1),a.Testidx," +
                    "a.teststageidx,a.CollectionDateTime, b.Stageidx,b.PatRegidx,b.OrderDate,a.SampleNumber  " +
                    "FROM " + Database + ".Tests a ," + Database + ".TestOrder b " +
                    " WHERE a.Orderid= b.id and b.Stageidx=2 and "
                    + "b.status=0 "
                    + "and a.TestStatus is null "
                    + "and a.testidx !=4 limit 30";
//            System.out.println(Query);
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {

                t_id = hrset.getString(1);
                OrderNum = hrset.getString(2);
                TestStatus = hrset.getString(3);
                Narration = hrset.getString(4);
                Physicianidx = hrset.getString(5);
                Testidx = hrset.getString(6);
                teststageidx = hrset.getString(7);
                CollectionDateTime = hrset.getString(8);
                Stageidx = hrset.getString(9);
                PatRegidx = hrset.getString(10);
                OrderDate = hrset.getString(11);
                t_id = String.format("%010d", Integer.parseInt(t_id));
                OrderDate = dateconversion(OrderDate);
                CollectionDateTime = dateconversion(CollectionDateTime);
                SampleNumber = hrset.getString(12);

                locationindex = "";
                String PID_ = createPID(conn, Database, OrderNum, t_id, PatRegidx);
                String OBR_ = createOBR(conn, Database, OrderNum, t_id, PatRegidx, OrderDate, CollectionDateTime, test_liscode.get(Testidx));
                String MSH_ = createMSH(conn, Database, OrderDate, CollectionDateTime, facilityid, facilityname, facilityid_lis);

                String locationid_lis = loc_liscode.get(locationindex);

                String ORC_ = createORC(conn, Database, OrderNum, t_id, PatRegidx, OrderDate, doc_liscode.get(Physicianidx), facilityid_lis, locationid_lis);
                String ZUD_ = createZUD(conn, Database, OrderNum, t_id, PatRegidx, OrderDate, CollectionDateTime, test_liscode.get(Testidx), SampleNumber);

                System.out.println(MSH_);
                System.out.println(PID_);
                System.out.println(OBR_);
                System.out.println(ORC_);
                String FileName = Filepath + "" + OrderNum + "_" + t_id + "_" + OrderDate + "_" + GetFileNamedate() + ".HL7";

                createhl7file(conn, Database, FileName, OrderNum, MSH_, PID_, ORC_, OBR_, ZUD_);

            }
            Thread.sleep(500);
            System.exit(0);

        } catch (Exception e) {
            // TODO: handle exception
        }


    }

    public static String createMSH(Connection Conn, String Database, String OrderDate, String CollectionDateTime, String facilityid, String facilityname, String facilityid_lis) {
        String _MSH = "";
        try {


            Date now = new Date();
            Long longTime = new Long(now.getTime() / 1000);

            _MSH = "MSH|^~\\&|RoverMD|" + facilityid_lis + "|" + facilityid_lis + "|" + facilityname + "|" + OrderDate + "||ORM^O01|" + longTime + "|P|2.4";


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return _MSH;
    }

    public static String createOBR(Connection Conn, String Database, String orderid, String t_id, String PatRegidx, String OrderDate, String CollectionDateTime, String Testidx) {
        String _OBR = "";
        try {

            System.out.println(Testidx);
            String Testidx_temp[] = Testidx.split("\\|");


            //OBR|1|69852||2090|R|2022 02 08 0440|202202080440||||
            _OBR = "OBR|1|" + orderid + "||" + Testidx_temp[1] + "|R|" + OrderDate + "|" + CollectionDateTime + "||||";

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return _OBR;
    }

    public static String createZUD(Connection Conn, String Database, String orderid, String t_id, String PatRegidx, String OrderDate, String CollectionDateTime, String Testidx, String SampleNumber) {
        String _ZUD = "";
        try {

            if (SampleNumber == null) {
                SampleNumber = orderid;

            }

            //ZUD|1|R|789405714|Barcode ID
            // _ZUD="ZUD|1|R|"+orderid+"|Barcode ID";
            _ZUD = "ZUD|1|R|" + SampleNumber + "|Barcode ID";

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return _ZUD;
    }

    public static String createPID(Connection Conn, String Database, String orderid, String t_id, String PatRegidx) {
        String _PID = "";

        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
	   /* H	Hispanic or Latino
	    N	Not Hispanic or Latino
	    U	Unknown*/

        try {
            Query = "select id,MRN,FirstName,LastName ,MiddleInitial,DOB,Age,substr(Gender,1,1),Email,PhNumber ,Address,City ,State,County,ZipCode,Ethnicity,Race,TestingLocation from " + Database + ".PatientReg where id=" + PatRegidx;
            hstmt = Conn.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                //  PID|1|457841|354789||Mehmood^Mouhid||20001025|M||A|17154 butte creek^^Houston^TX^74986||4694980033||||||||H|
                _PID = "PID|1|" + hrset.getString(2) + "|||" + hrset.getString(4) + "^" + hrset.getString(3) + "||" + hrset.getString(6).replace("-", "") + "|" +
                        hrset.getString(8).toUpperCase() + "||" + getRace(hrset.getString(17)) + "|" + hrset.getString(11) + "^^" + hrset.getString(12) + "^" + hrset.getString(13) + "^" + hrset.getString(15) + "||" + hrset.getString(10).replace("-", "") +
                        "||" + hrset.getString(9).trim() + "||||||" + getEthnicity(hrset.getString(16)) + "|";
                locationindex = hrset.getString(18);
            }
            hrset.close();
            hstmt.close();
            return _PID;
        } catch (Exception localException) {

            System.out.println(localException.getMessage());
        }
        return _PID;
    }

    public static String createORC(Connection Conn, String Database, String orderid, String t_id, String PatRegidx, String OrderDate, String Physicianidx, String facilityid_lis, String locationid_lis) {
        String _ORC = "";

        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
	   /* H	Hispanic or Latino
	    N	Not Hispanic or Latino
	    U	Unknown*/

        try {

            String locationid_lis_x[] = locationid_lis.split("\\|");
            _ORC = "ORC|NW|" + orderid + "||||||" + locationid_lis_x[1] + "|" + OrderDate + "|||" + Physicianidx + "^^^^^NPI|" + locationid_lis_x[0] + "|||||||||||" + locationid_lis_x[2];


            return _ORC;
        } catch (Exception localException) {

            System.out.println(localException.getMessage());
        }
        return _ORC;
    }


    public static HashMap<String, String> listoftest(Connection Conn, String Database) {
        HashMap<String, String> hm = new HashMap();

        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String deviceid = "";
        String name = null;
        try {
            Query = "SELECT id,TestName,liscode FROM  " + Database + ".ListofTests ORDER BY Id";
            hstmt = Conn.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                hm.put(hrset.getString(1), hrset.getString(2) + "|" + hrset.getString(3));
            }
            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception localException) {
            System.out.println(localException.getMessage());
        }
        return hm;
    }

    public static HashMap<String, String> listoflocation(Connection Conn, String Database) {
        HashMap<String, String> hm = new HashMap();

        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String deviceid = "";
        String name = null;
        try {
            Query = "SELECT id,liscode,Location,concat(Address,'^^',City,'^',State,'^',Zip)	" +
                    " FROM  " + Database + ".Locations ORDER BY Id";
            hstmt = Conn.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                hm.put(hrset.getString(1), hrset.getString(2) + "|" + hrset.getString(3) + "|" + hrset.getString(4));
            }
            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception localException) {
        }
        return hm;
    }


    public static HashMap<String, String> listofDoc(Connection Conn, String Database) {
        HashMap<String, String> hm = new HashMap();

        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String deviceid = "";
        String name = null;
        try {


            Query = "SELECT id,DoctorsFirstName,DoctorsLastName,NPI,Address,city,state,zipcode FROM  " + Database + ".DoctorsList ORDER BY Id";
            hstmt = Conn.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                hm.put(hrset.getString(1), hrset.getString(4) + "^" + hrset.getString(2) + "^" + hrset.getString(3));
            }
            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception localException) {
        }
        return hm;
    }

    public static void createhl7file(Connection conn, String Database, String FileName, String OrderNum, String MSH_, String PID_, String ORC_, String OBR_, String ZUD_) {


        Statement hstmt = null;

        try {


            String Msg = MSH_;
            //Msg=Msg+"\n"+MSH+"\n";
            Msg = Msg + "\n" + PID_;
            Msg = Msg + "\n" + ZUD_;
            //Msg=Msg+"\n"+IN1;
            Msg = Msg + "\n" + ORC_;
            Msg = Msg + "\n" + OBR_;


            System.out.println(Msg);
            writefile(Msg, FileName);
            String sftpFile = "/Integration/Queue/";
            ChannelSftp newch = setupJsch();
            //boolean isuploaded=false;
            boolean isuploaded = uploadSftpFromPath(FileName, sftpFile);
            System.out.println(isuploaded);
            if (isuploaded) {

                hstmt = conn.createStatement();
                System.out.println("UPDATE " + Database + ".TestOrder SET status=3,UpdatedAt=now(),O_Filename='" + FileName + "' WHERE OrderNum='" + OrderNum + "'");
                hstmt.executeUpdate("UPDATE " + Database + ".TestOrder SET status=3,UpdatedAt=now(),O_Filename='" + FileName + "' WHERE OrderNum='" + OrderNum + "'");
                hstmt.close();
            }


            System.out.println(isuploaded + "|");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO: handle exception
        }

    }

    public static String dateconversion(String date) {

        String _date = "";
        try {


            Date Date_ = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date);
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddhhmm");
            _date = formatter1.format(Date_);

        } catch (Exception e) {
            // TODO: handle exception
        }
        return _date;

    }


    public static void writefile(String data, String FileName) {


        try {

            FileWriter fr = new FileWriter(FileName, true);
            fr.write(data);
            fr.flush();
            fr.close();

        } catch (Exception exception) {
        }
    }

    private static ChannelSftp setupJsch() throws JSchException {
        String username = "lab-integration";
        String password = "7Br0Wf09#iJGhy";
        String host = "sftp.rovermd.com";
        JSch jsch = new JSch();
        Session jschSession = jsch.getSession(username, host);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        jschSession.setConfig(config);
        jschSession.setPassword(password);
        jschSession.connect();
        return (ChannelSftp) jschSession.openChannel("sftp");
    }


    public static boolean uploadSftpFromPath(String localFile, String sftpFile) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = setupJsch();
        } catch (JSchException e) {
            // throw the exception
        }
        try {
            channelSftp.connect();
        } catch (JSchException e) {
            // throw the exception
        }
        try {
            channelSftp.put(localFile, sftpFile);
            System.out.println("Upload Complete");
        } catch (SftpException e) {
            // throw the exception
            System.out.println("ftp Error " + e.getMessage());
        }
        channelSftp.exit();
        return true;
    }

    private static String GetFileNamedate() {
        try {
            Date date = GetDate();
            DecimalFormat decimalformat = new DecimalFormat("#00");
            return decimalformat.format(date.getYear() + 1900) + "" + decimalformat.format(date.getMonth() + 1) + "" + decimalformat.format(date.getDate()) + "_" + decimalformat.format(date.getHours()) + "" + decimalformat.format(date.getMinutes()) + "" + decimalformat.format(date.getSeconds());
        } catch (Exception exception) {
            return "invalid filename " + exception.getMessage();
        }
    }

    private static Date GetDate() {
        try {
            Date date = new Date();
            return date;
        } catch (Exception _ex) {
            return null;
        }
    }

    public static String getRace(String race) {
        String _race = "";

        if (race.compareTo("Asian") == 0) {
            _race = "A";
        }
        if (race.compareTo("American Indian or Alaska Native") == 0) {
            _race = "I";
        }
        if (race.compareTo("White") == 0) {
            _race = "C";
        }
        if (race.compareTo("Black") == 0) {
            _race = "B";
        }
        if (race.compareTo("HISPANIC") == 0) {
            _race = "H";
        }


        return _race;
    }

    public static String getEthnicity(String Ethnic) {
        String _Ethnic = "";


        if (Ethnic.toUpperCase().trim().compareTo("Not Hispanic") == 0) {
            _Ethnic = "H";
        }
        if (Ethnic.toUpperCase().trim().compareTo("Latino") == 0) {
            _Ethnic = "N";
        } else {

            _Ethnic = "U";

        }


        return _Ethnic;
    }

    private static Connection getConnectionLocal() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            final Connection connection = DriverManager.getConnection("jdbc:mysql://3.238.87.114:33306/oe?user=rovermdadmin&password=atyu!ioujy1986");
            return connection;
        } catch (Exception e) {
            System.out.println("GetConnection: " + e.getMessage());
            return null;
        }
    }

    private static Connection getConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            final Connection connection = DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986");
            return connection;
        } catch (Exception e) {
            System.out.println("GetConnection: " + e.getMessage());
            return null;
        }
    }

    private static boolean AppRunning() {
        String s = null;
        int count = 0;
        try {

            Process p = Runtime.getRuntime().exec("ps aux");
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            while ((s = stdInput.readLine()) != null) {

                if (s.contains("md." + className)) count++;

            }


            if (count > 1)
                return true;
            else
                return false;
        } catch (Exception e) {
            System.out.println("Exception in AppRunning func. " + e.getMessage());
            return false;
        }
    }

    public boolean uploadSftpFromInputStream(InputStream localFile, String sftpFile) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = setupJsch();
        } catch (JSchException e) {
            // throw the exception
        }
        try {
            channelSftp.connect();
        } catch (JSchException e) {
            // throw the exception
        }
        try {
            channelSftp.put(localFile, sftpFile);
            System.out.println("Upload Complete");
        } catch (SftpException e) {
            // throw the exception
        }
        channelSftp.exit();
        return true;
    }

}
