package schedulers;


import DAL.TwilioSMSConfiguration;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Date;

@SuppressWarnings("Duplicates")
public class DailyVisitsCountSMS {

    private static Connection conn = null;
    private static String Query = "";
    private static Statement stmt = null;
    private static ResultSet rset = null;
    private static TwilioSMSConfiguration smsConfiguration = new TwilioSMSConfiguration();

    public static void main(String[] args) throws Exception {
        String Query1 = "";
        String TODAY = "";
        String YesterdayDate = "";
        String CurrDateTime = "";
        String MONTH_START = "";
        String MONTH_END = "";
        String smsBody = "";

        int PatientCountOverAll_TODAY_Schertz = 0;
        int PatientCountOverAll_TODAY_Floresville = 0;

        int PatientCountInsured_TODAY_Schertz = 0;
        int PatientCountInsured_TODAY_Floresville = 0;

        int PatientCountSelfPay_TODAY_Schertz = 0;
        int PatientCountSelfPay_TODAY_Floresville = 0;

        int PatientCountOverAll_MONTHLY_Schertz = 0;
        int PatientCountOverAll_MONTHLY_Floresville = 0;

        int PatientCountOverAll_MONTHLYInsured_Schertz = 0;
        int PatientCountOverAll_MONTHLYInsured_Floresville = 0;

        int PatientCountOverAll_MONTHLYSelfPay_Schertz = 0;
        int PatientCountOverAll_MONTHLYSelfPay_Floresville = 0;
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
//            FileWriter filewriter = new FileWriter("/sftpdrive/opt/SMSLogs/SendingLogs.log", true);
//            filewriter.write("\r\n Scheduler Time starts at " + new Date().toString() + "\r\n");
            conn = getConnection();

            Query = "Select DATE_FORMAT(NOW(),'%Y-%m-%d')," +
                    "DATE_SUB(CURDATE(), INTERVAL 1 DAY) AS yesterday_date," +
                    "DATE_FORMAT(NOW(),'%d-%b-%Y %H:%m:%s')," +
                    "DATE_SUB(LAST_DAY(NOW()),INTERVAL DAY(LAST_DAY(NOW()))- 1 DAY) AS 'FIRST DAY OF CURRENT MONTH'," +
                    "LAST_DAY(NOW()) ";
            stmt = conn != null ? conn.createStatement() : null;
            rset = stmt != null ? stmt.executeQuery(Query) : null;
            if (rset.next()) {
                TODAY = rset.getString(1);
                YesterdayDate = rset.getString(2);
                CurrDateTime = rset.getString(3);
                MONTH_START = rset.getString(4);
                MONTH_END = rset.getString(5);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from schertz.PatientVisit a " +
                    "INNER JOIN schertz.PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' " +
                    " AND DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59' AND b.status=0";
            stmt = conn != null ? conn.createStatement() : null;
            rset = stmt != null ? stmt.executeQuery(Query) : null;
            if (rset.next()) {
                PatientCountOverAll_TODAY_Schertz = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from floresville.PatientVisit a " +
                    "INNER JOIN floresville.PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' " +
                    " AND DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59' AND b.status=0";
            stmt = conn != null ? conn.createStatement() : null;
            rset = stmt != null ? stmt.executeQuery(Query) : null;
            if (rset.next()) {
                PatientCountOverAll_TODAY_Floresville = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            //******************************************************************************

            Query = "Select COUNT(*) from schertz.PatientReg " +
                    " LEFT JOIN schertz.PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                    "where PatientReg.Status = 0 " +
                    " AND PatientReg.SelfPayChk = 1 " +
                    " AND PatientVisit.DateOfService >= '" + TODAY + " 00:00:00' " +
                    " AND PatientVisit.DateOfService <= '" + TODAY + " 23:59:59'";
            stmt = conn != null ? conn.createStatement() : null;
            rset = stmt != null ? stmt.executeQuery(Query) : null;
            if (rset.next()) {
                PatientCountInsured_TODAY_Schertz = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from floresville.PatientReg " +
                    "LEFT JOIN floresville.PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                    "where PatientReg.Status = 0 " +
                    " AND PatientReg.SelfPayChk = 1 " +
                    " AND PatientVisit.DateOfService >= '" + TODAY + " 00:00:00' " +
                    " AND PatientVisit.DateOfService <= '" + TODAY + " 23:59:59'";
            stmt = conn != null ? conn.createStatement() : null;
            rset = stmt != null ? stmt.executeQuery(Query) : null;
            if (rset.next()) {
                PatientCountInsured_TODAY_Floresville = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from schertz.PatientReg " +
                    "LEFT JOIN schertz.PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                    "where PatientReg.Status = 0 " +
                    " AND PatientReg.SelfPayChk = 0 " +
                    " AND PatientVisit.DateOfService >= '" + TODAY + " 00:00:00' " +
                    " AND PatientVisit.DateOfService <= '" + TODAY + " 23:59:59'";
            stmt = conn != null ? conn.createStatement() : null;
            rset = stmt != null ? stmt.executeQuery(Query) : null;
            if (rset.next()) {
                PatientCountSelfPay_TODAY_Schertz = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from floresville.PatientReg " +
                    "LEFT JOIN floresville.PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                    "where PatientReg.Status = 0 " +
                    " AND PatientReg.SelfPayChk = 0 " +
                    " AND PatientVisit.DateOfService >= '" + TODAY + " 00:00:00' " +
                    " AND PatientVisit.DateOfService <= '" + TODAY + " 23:59:59'";
            stmt = conn != null ? conn.createStatement() : null;
            rset = stmt != null ? stmt.executeQuery(Query) : null;
            if (rset.next()) {
                PatientCountSelfPay_TODAY_Floresville = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            //************************ TOTAL MONTHLY COUNTS ************************
            Query = "Select COUNT(*) from schertz.PatientVisit a " +
                    "INNER JOIN schertz.PatientReg b ON a.PatientRegId=b.ID " +
                    "where " +
                    "DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' and " +
                    "DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' AND " +
                    "b.status=0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_MONTHLY_Schertz = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from floresville.PatientVisit a " +
                    "INNER JOIN floresville.PatientReg b ON a.PatientRegId=b.ID " +
                    "where " +
                    "DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' and " +
                    "DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' AND " +
                    "b.status=0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_MONTHLY_Floresville = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            //***********************************************************************//

            //************************ TOTAL MONTHLY INSURED COUNTS ************************
            Query = "Select COUNT(*) from schertz.PatientVisit a " +
                    "INNER JOIN schertz.PatientReg b ON a.PatientRegId=b.ID " +
                    "where " +
                    "DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' and " +
                    "DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' AND " +
                    "b.status=0 AND b.SelfPayChk = 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_MONTHLYInsured_Schertz = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from floresville.PatientVisit a " +
                    "INNER JOIN floresville.PatientReg b ON a.PatientRegId=b.ID " +
                    "where " +
                    "DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' and " +
                    "DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' AND " +
                    "b.status=0 AND b.SelfPayChk = 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_MONTHLYInsured_Floresville = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            //***********************************************************************

            //************************ TOTAL MONTHLY INSURED COUNTS ************************
            Query = "Select COUNT(*) from schertz.PatientVisit a " +
                    "INNER JOIN schertz.PatientReg b ON a.PatientRegId=b.ID " +
                    "where " +
                    "DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' and " +
                    "DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' AND " +
                    "b.status=0 AND b.SelfPayChk = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_MONTHLYSelfPay_Schertz = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from floresville.PatientVisit a " +
                    "INNER JOIN floresville.PatientReg b ON a.PatientRegId=b.ID " +
                    "where " +
                    "DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' and " +
                    "DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' AND " +
                    "b.status=0 AND b.SelfPayChk = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_MONTHLYSelfPay_Floresville = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            //***********************************************************************

/*
            smsBody = "************* TODAY COUNT "+TODAY+" *************     \n" +
                      "Facility    ||    Total    ||    Insured    ||    SelfPay     \n" +
                      "Schertz     ||     " + PatientCountOverAll_TODAY_Schertz + "      ||       " + PatientCountInsured_TODAY_Schertz + "      ||       " + PatientCountSelfPay_TODAY_Schertz + "       \n" +
                      "Floresville ||     " + PatientCountOverAll_TODAY_Floresville + "      ||       " + PatientCountInsured_TODAY_Floresville + "      ||       " + PatientCountSelfPay_TODAY_Floresville + "       \n";
*/
//            System.out.println("PatientCountOverAll_MONTHLY_Schertz --> " + PatientCountOverAll_MONTHLY_Schertz);
//            System.out.println("PatientCountOverAll_MONTHLYInsured_Schertz  --> " + PatientCountOverAll_MONTHLYInsured_Schertz);
//            System.out.println("PatientCountOverAll_MONTHLYSelfPay_Schertz --> " + PatientCountOverAll_MONTHLYSelfPay_Schertz);
//            System.out.println("PatientCountInsured_TODAY_Schertz --> " + PatientCountInsured_TODAY_Schertz);
//            System.out.println("PatientCountSelfPay_TODAY_Schertz --> " + PatientCountSelfPay_TODAY_Schertz);
//            System.out.println("PatientCountOverAll_TODAY_Schertz --> " + PatientCountOverAll_TODAY_Schertz);

            smsBody = "**" + TODAY + "**\n" +
                    "------------\n" +
                    "**Schertz(T:" + PatientCountOverAll_MONTHLY_Schertz + "|I:" + PatientCountOverAll_MONTHLYInsured_Schertz + "|S:" + PatientCountOverAll_MONTHLYSelfPay_Schertz + ")**\n" +
                    "Insured   " + PatientCountInsured_TODAY_Schertz + "\n" +
                    "SelfPay   " + PatientCountSelfPay_TODAY_Schertz + "\n" +
                    "Total       " + PatientCountOverAll_TODAY_Schertz + "\n" +
                    "------------\n" +
                    "**Floresv(T:" + PatientCountOverAll_MONTHLY_Floresville + "|I:" + PatientCountOverAll_MONTHLYInsured_Floresville + "|S:" + PatientCountOverAll_MONTHLYSelfPay_Floresville + ")**\n" +
                    "Insured   " + PatientCountInsured_TODAY_Floresville + "\n" +
                    "SelfPay   " + PatientCountSelfPay_TODAY_Floresville + "\n" +
                    "Total       " + PatientCountOverAll_TODAY_Floresville + "\n" +
                    "------------\n" +
                    " " + CurrDateTime + " \n";
////            Query = "Select Id, IFNULL(dbname,''), status from oe.clients where Id not in (23,32,33,36)  ";
//            Query = "Select Id, IFNULL(dbname,''), status from oe.clients where status = 0  ";

//            final String ACCOUNT_SID = "AC60325bc1c82a680a9b654baf076df3d8";
//            final String AUTH_TOKEN = "edcaf2fbbf9d7dd2659d33eb0d41eadf";

            String AuthFactor[] = getTwilioAuthorization(conn);
//            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Twilio.init(AuthFactor[0], AuthFactor[1]);
            Message message = Message.creator(
//                    new com.twilio.type.PhoneNumber("+17084154105"), // Fawad
                            new com.twilio.type.PhoneNumber("+14372344164"), // Ali
//                    new com.twilio.type.PhoneNumber("+14694980033"), // to ME
                            new com.twilio.type.PhoneNumber("+19724981837"), // from
                            smsBody)
                    .create();

/*            System.out.println("Error Code --> " + message.getErrorCode());

            System.out.println("get Error Message --> " + message.getErrorMessage());
            System.out.println("Price --> " + message.getPrice());
            System.out.println("Price --> " + message.getDateCreated());
            System.out.println("Price --> " + message.getDateSent());
            System.out.println("Price --> " + message.getDateUpdated());
            System.out.println("Status --> " + message.getStatus());*/
            System.out.println("Get Body --> \n" + message.getBody());
            System.out.println("Date Sent At --> " + message.getDateSent());
            System.out.println("Status --> " + message.getStatus());
            System.out.println("Message Sent...!");
        } catch (Exception e) {
//            updateErrorIsScheduler(conn, smsIdx, dbName);
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

    private static String[] getTwilioAuthorization(Connection conn) {
        CallableStatement cStmt = null;
        rset = null;
        Query = "";
        String accountSID = "";
        String authToken = "";

        try {
            Query = "{CALL SP_GET_TwilioAuthorization()}";
            cStmt = conn.prepareCall(Query);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                accountSID = rset.getString(1);
                authToken = rset.getString(2);
            }
//            System.out.println("Account SID " + accountSID);
//            System.out.println("Auth Token " + authToken);
        } catch (Exception Ex) {
//            helper.SendEmailWithAttachment("Error in getTwilioAuthorization ", context, Ex, "DAL - TwilioSMSConfiguration", "getTwilioAuthorization", conn);
//            Services.DumException("TwilioSMSConfiguration", "getTwilioAuthorization -- SP -- 001 ", req, Ex, context);
            return new String[]{"Exception Message: " + Ex.getMessage()};
        }
        return new String[]{accountSID, authToken};
    }

}
