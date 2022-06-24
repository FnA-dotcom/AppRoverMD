package schedulers;


import DAL.TwilioSMSConfiguration;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

@SuppressWarnings("Duplicates")
public class TwilioSMSScheduler {

    private static Connection conn = null;
    private static String Query = "";
    private static Statement stmt = null;
    private static ResultSet rset = null;
    private static TwilioSMSConfiguration smsConfiguration = new TwilioSMSConfiguration();

    public static void main(String[] args) throws Exception {
        String Query1 = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        Date dt1 = new Date();
        long timestamp = dt1.getTime();
        int smsIdx = 0;
        String dbName = "";
        Date dt = new Date();
        long StartTime = dt.getTime();
        String[] result;

        System.out.println("Message Sending Starts AT ....[" + StartTime + "] " + timestamp + "\r\n");

        try {
            /*            if ( SendProcessAlreadyRunning() ) {
                System.out.println("Unable to start Automatic Scheduler.Process is Already Running ");
                return;
            }*/
            FileWriter filewriter = new FileWriter("/sftpdrive/opt/SMSLogs/SendingLogs.log", true);
            filewriter.write("\r\n Scheduler Time starts at " + new Date().toString() + "\r\n");
            conn = getConnection();
//            Query = "Select Id, IFNULL(dbname,''), status from oe.clients where Id not in (23,32,33,36)  ";
            Query = "Select Id, IFNULL(dbname,''), status from oe.clients where status = 0  ";
            stmt = conn != null ? conn.createStatement() : null;
            rset = stmt != null ? stmt.executeQuery(Query) : null;
            while (rset != null && rset.next()) {
                dbName = rset.getString(2);
//                System.out.println("ID --> " + rset.getInt(1));
//                System.out.println("dbname --> " + rset.getString(2));

/*                Query1 = "SELECT FacilityIdx, Priority, PatientName, PatientMRN,Sms,PatientPhNumber,SentBy,Id,'1' " +
                        "FROM " + rset.getString(2) + ".SMS_Info  WHERE Priority in (1,2) AND isSchedulerSent = 0 " +
                        " UNION ALL \n" +
                        "SELECT FacilityIdx, Priority, PatientName, IFNULL(PatientMRN,0),Sms,PatientPhNumber,SentBy,Id,'2' " +
                        "FROM oe.SMS_Info  WHERE Priority in (1,2) AND isSchedulerSent = 0";*/
/*                if (rset.getInt(3) == 0) {
                    Query1 = "SELECT FacilityIdx, Priority, PatientName, PatientMRN,Sms,PatientPhNumber,SentBy,Id " +
                            " FROM " + rset.getString(2) + ".SMS_Info WHERE Priority in (1,2) AND isSchedulerSent = 0";
                } else {
                    Query1 = "SELECT FacilityIdx, Priority, PatientName, IFNULL(PatientMRN,0),Sms,PatientPhNumber,SentBy,Id " +
                            "FROM oe.SMS_Info  WHERE Priority in (1,2) AND isSchedulerSent = 0";
                }*/
                Query1 = "SELECT FacilityIdx, Priority, PatientName, PatientMRN,Sms,PatientPhNumber,SentBy,Id " +
                        " FROM " + rset.getString(2) + ".SMS_Info WHERE Priority in (1,2) AND isSchedulerSent = 0";
                System.out.println("Query1 " + Query1);
                filewriter.write("\r\n" + Query1 + "\r\n");
                stmt1 = conn != null ? conn.createStatement() : null;
                rset1 = stmt1 != null ? stmt1.executeQuery(Query1) : null;
                while (rset1 != null && rset1.next()) {
/*                    System.out.println("FacilityIdx --> " + rset1.getInt(1));
                    System.out.println("Priority --> " + rset1.getInt(2));
                    System.out.println("PatientName --> " + rset1.getString(3));
                    System.out.println("PatientMRN --> " + rset1.getInt(4));
                    System.out.println("Sms --> " + rset1.getString(5));
                    System.out.println("PatientPhNumber --> " + rset1.getString(6));
                    System.out.println("SentBy --> " + rset1.getString(7));*/
                    filewriter.write("\r\n ***** " + rset1.getInt(1) + " *****\r\n");
                    filewriter.write("\r\n ***** " + rset1.getInt(2) + " *****\r\n");
                    filewriter.write("\r\n ***** " + rset1.getString(3) + " *****\r\n");
                    filewriter.write("\r\n ***** " + rset1.getString(4) + " *****\r\n");
                    filewriter.write("\r\n ***** " + rset1.getString(5) + " *****\r\n");
                    filewriter.write("\r\n ***** " + rset1.getString(6) + " *****\r\n");
                    smsIdx = rset1.getInt(8);

                    result = smsConfiguration.sendTwilioMessages(null, conn, null, rset1.getString(5), rset.getInt(1), rset1.getString(6), rset1.getInt(7), rset.getString(2), rset1.getString(4), rset1.getInt(8));
                    if (result[0].equals("Success")) {
                        smsConfiguration.updateSMSInfoTable(null, conn, null, rset1.getInt(8), result[1], rset.getString(2), "0");
                    } else {
                        smsConfiguration.updateSMSInfoTable(null, conn, null, rset1.getInt(8), result[1], rset.getString(2), "999");
                        //updateErrorIsScheduler(conn, smsIdx, dbName);
                    }
                    updateIsScheduler(conn, rset1.getInt(8), rset.getString(2));

/*                    if (rset.getInt(3) == 0) {
                        result = smsConfiguration.sendTwilioMessages(null, conn, null, rset1.getString(5), rset.getInt(1), rset1.getString(6), rset1.getInt(7), rset.getString(2), rset1.getInt(4));
                        if (result[0].equals("Success")) {
                            smsConfiguration.updateSMSInfoTable(null, conn, null, rset1.getInt(8), result[1], rset.getString(2), "0");
                        } else {
                            smsConfiguration.updateSMSInfoTable(null, conn, null, rset1.getInt(8), result[1], rset.getString(2), "999");
                        }
                    } else if (rset.getInt(3) == 1) {
                        result = smsConfiguration.sendTwilioMessages(null, conn, null, rset1.getString(5), rset.getInt(1), rset1.getString(6), rset1.getInt(7), "oe", rset1.getInt(4));
                        if (result[0].equals("Success")) {
                            smsConfiguration.updateSMSInfoTable(null, conn, null, rset1.getInt(8), result[1], "oe", "0");
                        } else {
                            smsConfiguration.updateSMSInfoTable(null, conn, null, rset1.getInt(8), result[1], "oe", "999");
                        }
                    }*/

//                    System.out.println("IN INNER LOOP!!");
/*                    if (rset.getInt(3) == 0) {
                        // smsConfiguration.sendTwilioMessages(null, conn, null, rset1.getString(5), rset1.getInt(1), rset1.getString(6), rset1.getInt(7), rset.getString(2), rset1.getInt(4));
                        //Updating Scheduler Value
                        //smsConfiguration.updateSMSInfoSchedulerTable(conn, rset1.getInt(8), rset.getString(2));

                    } else {
                        // smsConfiguration.sendTwilioMessages(null, conn, null, rset1.getString(5), rset1.getInt(1), rset1.getString(6), rset1.getInt(7), "oe", rset1.getInt(4));
                        //Updating Scheduler Value
                        // smsConfiguration.updateSMSInfoSchedulerTable(conn, rset1.getInt(8), "oe");
                    }*/
                }
                assert rset1 != null;
                rset1.close();
                stmt1.close();
            }
            try {
                Query = "SELECT FacilityIdx, Priority, PatientName, IFNULL(PatientMRN,0),Sms,PatientPhNumber,SentBy,Id " +
                        " FROM oe.SMS_Info  WHERE Priority in (1,2) AND isSchedulerSent = 0";
                stmt = conn != null ? conn.createStatement() : null;
                rset = stmt != null ? stmt.executeQuery(Query) : null;
                while (rset != null && rset.next()) {
/*                System.out.println("OUTSIDE ** FacilityIdx --> " + rset.getInt(1));
                System.out.println("OUTSIDE ** Priority --> " + rset.getInt(2));
                System.out.println("OUTSIDE ** PatientName --> " + rset.getString(3));
                System.out.println("OUTSIDE ** PatientMRN --> " + rset.getInt(4));
                System.out.println("OUTSIDE ** Sms --> " + rset.getString(5));
                System.out.println("OUTSIDE ** PatientPhNumber --> " + rset.getString(6));
                System.out.println("OUTSIDE ** SentBy --> " + rset.getString(7));*/
                    smsIdx = rset.getInt(8);
                    result = smsConfiguration.sendTwilioMessages(null, conn, null, rset.getString(5), rset.getInt(1), rset.getString(6), rset.getInt(7), "oe", rset.getString(4), rset.getInt(8));
                    if (result[0].equals("Success")) {
                        smsConfiguration.updateSMSInfoTable(null, conn, null, rset.getInt(8), result[1], "oe", "0");
                    } else {
                        smsConfiguration.updateSMSInfoTable(null, conn, null, rset.getInt(8), result[1], "oe", "999");
                    }
                    updateIsScheduler(conn, rset.getInt(8), "oe");
                }
            } catch (Exception e) {
                updateErrorIsScheduler(conn, smsIdx, "oe");
                System.out.println("Message Sending Ends in Exception ....[" + dt.getTime() + "] " + timestamp + "\r\n");
                System.out.println("Outer exception ... " + "-" + e.getMessage());
                DumpException("main", "exp", e);
            }


            System.out.println("Sending Finished...");
            filewriter.flush();
            filewriter.close();
        } catch (Exception e) {
            updateErrorIsScheduler(conn, smsIdx, dbName);
            System.out.println("Message Sending Ends in Exception ....[" + dt.getTime() + "] " + timestamp + "\r\n");
            System.out.println("Outer exception ... " + "-" + e.getMessage());
            DumpException("main", "exp", e);
        } finally {
            assert rset != null;
            rset.close();
            stmt.close();
            conn.close();
        }
    }

    private static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
//            return DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=webserver873849&password=Asljdpiwoeurj!!3498&autoReconnect=true");
            return DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986&autoReconnect=true");
        } catch (Exception e) {
            System.out.println(e);
            DumpException("Connection Error", "exp", e);
        }
        return null;
    }

    private static boolean SendProcessAlreadyRunning() {
        String s = null;
        int count = 0;
        try {
            Process p = Runtime.getRuntime().exec("ps aux");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            while ((s = stdInput.readLine()) != null) {
                if (s.contains("FalconSchedulers.AutoLogOut")) {
                    count++;
                }
            }
            return count > 1;
        } catch (Exception e) {
            System.out.println("Exception in Auto Service func. " + e.getMessage());
        }
        return false;
    }

    private static void DumpException(String method, String message, Exception exception) {
        String s2 = "";
        try {
            FileWriter filewriter = new FileWriter("/sftpdrive/opt/SMSLogs/SMSExceptions.log", true);

            filewriter.write(new Date().toString() + "^" + method + "^" + message + "^" + exception.getMessage() + "\r\n");

            PrintWriter printwriter = new PrintWriter(filewriter, true);
            exception.printStackTrace(printwriter);
            filewriter.write("\r\n");
            filewriter.flush();
            filewriter.close();
            printwriter.close();
        } catch (Exception localException) {
        }
    }

    private static void updateIsScheduler(Connection conn, int smsIdx, String dbName) {
        String Query2 = "";
        Statement statement = null;

        try {
            Query2 = "UPDATE " + dbName + ".SMS_Info SET isSchedulerSent = 1, sentSchedulerDate = NOW() WHERE Id = " + smsIdx;
            System.out.println("Query222 --> " + Query2);
            statement = conn.createStatement();
            statement.executeUpdate(Query2);
            statement.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            DumpException("Connection Error", "exp", e);
        }
    }

    private static void updateErrorIsScheduler(Connection conn, int smsIdx, String dbName) {
        String Query2 = "";
        Statement statement = null;

        try {
            Query2 = "UPDATE " + dbName + ".SMS_Info SET isSchedulerSent = 999, sentSchedulerDate = NOW() WHERE Id = " + smsIdx;
            System.out.println("Query222 --> " + Query2);
            statement = conn.createStatement();
            statement.executeUpdate(Query2);
            statement.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            DumpException("Connection Error", "exp", e);
        }
    }
}
