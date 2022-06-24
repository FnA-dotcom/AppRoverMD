package schedulers;


import Handheld.UtilityHelper;
import PaymentIntegrations.CardConnectRestClient;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


@SuppressWarnings("Duplicates")
public class CheckStatusUpdate {

    public static void main(String[] args) throws Exception {
        Connection conn = null;
        String Query = "";
        Statement stmt = null;
        ResultSet rset = null;

        String ENDPOINT = "";
        String USERNAME = "";
        String PASSWORD = "";
        String MerchantId = "";

        PreparedStatement ps = null;
        ResultSet rset1 = null;
        Date dt1 = new Date();
        long timestamp = dt1.getTime();
        String dbName = "";
        Date dt = new Date();
        String retrefFromDB = null;
        int FlagType = 2;
        int ClientId = 9;
        int Id = 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");

        conn = getConnection();
        String InquireString[];
        List<String> SettlementStatus = new ArrayList<String>();
        UtilityHelper helper = new UtilityHelper();

        ps = conn.prepareStatement("SELECT SettlementStatus  FROM oe.SettlementStatuses");
        rset = ps.executeQuery();
        while (rset.next()) {
            SettlementStatus.add(rset.getString(1));
        }
        rset.close();
        ps.close();


        try {
            /*            if ( SendProcessAlreadyRunning() ) {
                System.out.println("Unable to start Automatic Scheduler.Process is Already Running ");
                return;
            }*/
            System.out.println("\r\n Scheduler Time starts at " + new Date().toString() + "\r\n");

            Query = "Select Id, IFNULL(dbname,''), status from oe.clients where Id = 8  ";
            stmt = conn != null ? conn.createStatement() : null;
            rset = stmt != null ? stmt.executeQuery(Query) : null;
            while (rset != null && rset.next()) {
                ClientId = rset.getInt(1);
                dbName = rset.getString(2);
//                System.out.println("\r\n DATABASE FETCHED \r\n");
                String[] AuthConnect = helper.getAuthConnect(conn, FlagType, ClientId);
                ENDPOINT = AuthConnect[0];
                USERNAME = AuthConnect[1];
                PASSWORD = AuthConnect[2];
                MerchantId = AuthConnect[3];

//                System.out.println("\r\n CONN AUTHENTICATED \r\n");
                /*
                ps = conn.prepareStatement("SELECT a.Id,a.ResponseText " +
                        " FROM " + dbName + ".CheckInfo a " +
                        "INNER JOIN oe.CheckPosting b" +
                        " ON a.Id=b.CheckPaymentIdx " +
                        " WHERE b.CheckResponse <> 'Approved' ");
                  */
                ps = conn.prepareStatement("SELECT a.Id,a.ResponseText " +
                        " FROM " + dbName + ".CheckInfo a " +
                        " WHERE a.Status != 1 ");
                System.out.println("\r\n QUERY ->> " + ps.toString() + " \r\n");
                rset1 = ps.executeQuery();
                while (rset1.next()) {
                    Id = rset1.getInt(1);
                    retrefFromDB = rset1.getString(2).split(",")[12];
//                    System.out.println("\r\n retrefFromDB ->> " + retrefFromDB + " \r\n");
                    InquireString = InquireTransaction(ENDPOINT, USERNAME, PASSWORD, MerchantId, retrefFromDB.trim());

//                    System.out.println("ResponseText --> " + InquireString[1]);
//                    System.out.println("Setlstat --> " + InquireString[2]);
//                    System.out.println("CaptureDate --> " + InquireString[3]);
                    //InquireString[3] = df.format(InquireString[3]);
                    Date _CaptureDate = formatter1.parse(InquireString[3]);
                    InquireString[3] = df.format(_CaptureDate);
//                    System.out.println("FORMATTED CaptureDate --> " + df.format(_CaptureDate));
                    updateCheckInfo(conn, dbName, Id, InquireString[3]/*CaptureDate*/, InquireString[2]/*Setlstat*/, SettlementStatus);
                    updateCheckPosting(conn, Id, ClientId, InquireString, InquireString[2]/*Setlstat*/, InquireString[1]/*ResponseText*/, SettlementStatus);
                }
            }
            SettlementStatus.clear();
            System.out.println("Scheduler Time starts Ends  ....[" + dt.getTime() + "] " + timestamp + "\r\n");

        } catch (Exception e) {
            System.out.println("Check-Status-Update Ends in Exception ....[" + dt.getTime() + "] " + timestamp + "\r\n");
            System.out.println("Outer exception ... " + "-" + e.getMessage());
            DumpException("main", "exp", e);
        } finally {
            assert rset != null;
            rset.close();
            stmt.close();
            conn.close();
        }
    }

    private static void updateCheckPosting(Connection conn, int id, int clientId, String[] inquireString, String Setlstat, String ResponseText, List<String> settlementStatus) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE oe.CheckPosting " +
                    " SET Status = ? , CheckResponse = ? , ACHReference = ? " +
                    " WHERE CheckPaymentIdx = ? and FacilityIndex = ? ");
//            System.out.println("\r\n Update QUERY ->> " + ps.toString() + " \r\n");
            ps.setInt(1, settlementStatus.indexOf(Setlstat) != -1 ? settlementStatus.indexOf(Setlstat) + 1 : -1);
            ps.setString(2, ResponseText);
            ps.setString(3, Arrays.toString(inquireString));
            ps.setInt(4, id);
            ps.setInt(5, clientId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            DumpException("updateCheckPosting", "ERROR in updateCheckStatus", e);
        }
    }

    private static void updateCheckInfo(Connection conn, String dbName, int Id, String captureDate, String Setlstat, List<String> settlementStatus) {

        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE " + dbName + ".CheckInfo " +
                    " SET Status = ? , CaptureDate = ? " +
                    " WHERE Id = ? ");
//            System.out.println("\r\n Update QUERY ->> " + ps.toString() + " \r\n");
            ps.setInt(1, settlementStatus.indexOf(Setlstat) != -1 ? settlementStatus.indexOf(Setlstat) + 1 : -1);
            ps.setString(2, captureDate);
            ps.setInt(3, Id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            DumpException("updateCheckInfo", "ERROR in updateCheckStatus", e);
        }
    }

    private static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            return DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986&autoReconnect=true");
        } catch (Exception e) {
            System.out.println(e);
            DumpException("Connection Error", "exp", e);
        }
        return null;
    }

    private static void DumpException(String method, String message, Exception exception) {
        try {
            FileWriter filewriter = new FileWriter("/sftpdrive/opt/ChechStatusLogs/ChechStatusExceptions.log", true);

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

    public static String[] InquireTransaction(String EndPoint, String UserName, String Password,
                                              String MerchantId, String Retref) {

//        System.out.println("\nSending Inquire  Request (** SCHEDULER **) ........");

        String Amount;
        String ResponseText;
        String Setlstat;
        String Capturedate;
        String ResponseCode;
        String BatchID;
        String MerchID;
        String Token;
        String AuthCode;
        String ResponseProc;
        String Authdate;
        String Userfields;
        String Lastfour;
        String Voidable;
        String Name;
        String Currency;
        String Refundable;
        String Expiry;


//        System.out.println("***************** Inquire Transaction START (** SCHEDULER **) **************************");
//        System.out.println("EndPoint " + EndPoint);
//        System.out.println("UserName " + UserName);
//        System.out.println("Password " + Password);
//        System.out.println("MerchantId " + MerchantId);
//        System.out.println("Retref " + Retref);

//        System.out.println("***************** Inquire Transaction END (** SCHEDULER **) **************************");


//        System.out.println("%%%%%%%%%%%%% TABISH %%%%%%%%%%%%%%% (** SCHEDULER **)");
        // Create the REST client
        CardConnectRestClient client = new CardConnectRestClient(EndPoint, UserName, Password);
//        System.out.println("%%%%%%%%%%%%% TABISH 1111 %%%%%%%%%%%%%%% (** SCHEDULER **)");
        // Send an AuthTransaction request
        JSONObject response = client.inquireTransaction(MerchantId, Retref);
        if (response == null) {
            System.out.println("RESPONSE IS NULL IN CHECK INQUIRY !!");
            return new String[]{""};
        }
//        System.out.println("***************** Inquire Transaction RESPONSE START (** SCHEDULER **) **************************");
//        System.out.println("RESPONSE " + response);
        // Handle response
        Set<String> keys = response.keySet();
        for (String key : keys)
            System.out.println(key + ": " + response.get(key));

//        System.out.println("***************** Inquire Transaction RESPONSE END (** SCHEDULER **) **************************");

        Amount = (String) response.get("amount");
        ResponseText = (String) response.get("resptext");
        Setlstat = (String) response.get("setlstat");

        Capturedate = (String) response.get("capturedate");
        //Capturedate = df.format(Capturedate);

        ResponseCode = (String) response.get("respcode");
        BatchID = (String) response.get("batchid");
        MerchID = (String) response.get("merchid");
        Token = (String) response.get("token");
        AuthCode = (String) response.get("authcode");
        ResponseProc = (String) response.get("respproc");
        Authdate = (String) response.get("authdate");
        Userfields = (String) response.get("userfields");
        Lastfour = (String) response.get("lastfour");
        Voidable = (String) response.get("voidable");
        Name = (String) response.get("name");
        Currency = (String) response.get("currency");
        Refundable = (String) response.get("refundable");
        Expiry = (String) response.get("expiry");
        Retref = (String) response.get("retref");

//        System.out.println("Amount --> " + Amount);
//        System.out.println("ResponseText --> " + ResponseText);
//        System.out.println("Setlstat --> " + Setlstat);
//        System.out.println("Capturedate --> " + Capturedate);
//        System.out.println("ResponseCode --> " + ResponseCode);
//        System.out.println("BatchID --> " + BatchID);
//        System.out.println("MerchID --> " + MerchID);
//        System.out.println("Token --> " + Token);
//        System.out.println("AuthCode --> " + AuthCode);
//        System.out.println("ResponseProc --> " + ResponseProc);
//        System.out.println("Authdate --> " + Authdate);
//        System.out.println("Userfields --> " + Userfields);
//        System.out.println("Lastfour --> " + Lastfour);
//        System.out.println("Voidable --> " + Voidable);
//        System.out.println("Name --> " + Name);
//        System.out.println("Currency --> " + Currency);
//        System.out.println("Refundable --> " + Refundable);
//        System.out.println("Expiry --> " + Expiry);
//        System.out.println("Retref --> " + Retref);
//
//
//        System.out.println("\n****** Ending Inquire Request (** SCHEDULER **) ******");
//        System.out.println("Exiting....");
        return new String[]{Amount, ResponseText, Setlstat, Capturedate, ResponseCode, BatchID, MerchID, Token, AuthCode,
                ResponseProc, Authdate, Userfields, Lastfour, Voidable, Name, Currency, Refundable, Expiry, Retref};
    }

}
