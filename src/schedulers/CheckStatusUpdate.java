package schedulers;


import Handheld.UtilityHelper;
import PaymentIntegrations.CardConnectRestClient;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;


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
        int facilityIndex = 0;
        int Id = 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
        String InvoiceNo = "";
        String PatientMRN = "";
        double PaidAmount = 0.0;
        double BalAmount = 0.0;
        double TotalAmount = 0.0;
        double CheckAmount = 0.0;
        int Paid = 0;
        String receipt = "";
        String Description = "";
        String printDate = "";
        String printTime = "";


        conn = getConnection();
        String[] InquireString;
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
            System.out.println("\r\n Scheduler Time starts at " + new Date().toString() + "\r\n");

            Query = "Select Id, IFNULL(dbname,''), status from oe.clients where Id IN (39,40)  ";
            stmt = conn != null ? conn.createStatement() : null;
            rset = stmt != null ? stmt.executeQuery(Query) : null;
            while (rset != null && rset.next()) {
                facilityIndex = rset.getInt(1);
                dbName = rset.getString(2);
                String[] AuthConnect = helper.getAuthConnect(conn, FlagType, facilityIndex);
                ENDPOINT = AuthConnect[0];
                USERNAME = AuthConnect[1];
                PASSWORD = AuthConnect[2];
                MerchantId = AuthConnect[3];

                ps = conn.prepareStatement("SELECT a.Id,a.ResponseText, a.InvoiceNo, a.MRN , a.Amount, a.Description," +
                        "DATE_FORMAT(NOW(),'%m/%d/%Y'), DATE_FORMAT(NOW(),'%h:%i:%s %p')," +
                        "DATE_FORMAT(NOW(),'%m/%d/%Y'), DATE_FORMAT(NOW(),'%h:%i:%s %p') " +
                        " FROM " + dbName + ".CheckInfo a " +
                        " WHERE a.Status != 1 ");
                rset1 = ps.executeQuery();
                while (rset1.next()) {
                    Id = rset1.getInt(1);
                    retrefFromDB = rset1.getString(2).split(",")[12];
                    InvoiceNo = rset1.getString(3).trim();
                    PatientMRN = rset1.getString(4).trim();
                    CheckAmount = rset1.getDouble(5);
                    Description = rset1.getString(6).trim();
                    printDate = rset1.getString(7).trim();
                    printTime = rset1.getString(8).trim();

                    InquireString = InquireTransaction(ENDPOINT, USERNAME, PASSWORD, MerchantId, retrefFromDB.trim());

                    //InquireString[3] = df.format(InquireString[3]);
                    Date _CaptureDate = formatter1.parse(InquireString[3]);
                    InquireString[3] = df.format(_CaptureDate);
                    if (InquireString[2].equals("Accepted")) {
                        String FullName = "";
                        String Address = "";
                        String Phone = "";
                        FullName = receiptClientData(conn, facilityIndex)[0];
                        Address = receiptClientData(conn, facilityIndex)[1];
                        Phone = receiptClientData(conn, facilityIndex)[2];

                        String[] PatientInfo = getPatientInfo(conn, dbName, PatientMRN);
                        String FName = PatientInfo[0];
                        String LName = PatientInfo[1];
                        String Name = FName + " " + LName;

                        Object[] invoiceMaster = getInvoiceMasterDetails(conn, dbName, InvoiceNo, PatientMRN);
                        PaidAmount = (double) invoiceMaster[0];
                        BalAmount = (double) invoiceMaster[1];
                        TotalAmount = (double) invoiceMaster[3];

                        if (CheckAmount == BalAmount) {
                            Paid = 1;
                        }
                        String receiptCounter = getReceiptCounter(conn, dbName);

                        insertInvoiceMasterHistory(conn, dbName, PatientMRN, InvoiceNo);

                        updateInvoiceMaster(conn, dbName, PaidAmount, CheckAmount, BalAmount, Paid, PatientMRN, InvoiceNo);

                        receipt = "<div id='printThis'><div id='mystyle' style='width: 377.68px;border: 1px; border-style: solid; border-color: black;'><div class='receipt' style='margin-top:20px;text-align: center;'><p style='margin:0px;'>" + FullName + "</p><p style='margin:0px;'>" + Address + "</p><p style='margin:0px;'>" + Phone + "</p></div><div class='receipt' style='margin-left: 5px; text-align:left !important'><p style='margin:0px;'>Invoice #: " + InvoiceNo + "</p> <p style='margin:0px;'>Receipt #: " + receiptCounter + "</p>   <p style='margin:0px;'>Status: " + InquireString[2] + "</p><p style='margin:0px;'>Balance : $" + BalAmount + "</p></div><div class='receipt' style='text-align:left !important'><span style='margin-left: 5px'>" + printDate + "</span> <span style='margin-left: 166px'>" + printTime + "</span></div><div class='receipt' style='text-align:left !important'><span style='margin-left: 5px'>Amount</span> <span style='margin-left: 199px'>$" + CheckAmount + "</span></div><div class='receipt' style='margin-left: 5px;text-align:left !important'><p style='margin:0px;'>Method:  Check </p><p style='margin:0px;'>" + Name + "</p></div><div class='receipt' style='text-align: center;'>" + InquireString[1] + "</div><div class='receipt' style='margin-bottom: 15px; text-align: center;'>Thank you. Please come again</div>  <div class='reciept' style='text-align: right;font-size:12px;'>Printed By Rover</div>    </div></div>";

                        paymentReceiptInsertion(conn, dbName, PatientMRN, InvoiceNo, TotalAmount, Paid, InvoiceNo, Description, "5", BalAmount, "scheduler", "UserIP", "CheckStatusUpdateScheduler", CheckAmount, receiptCounter, receipt);

                        updateCheckInfo(conn, dbName, Id, InquireString[3]/*CaptureDate*/, InquireString[2]/*Setlstat*/, SettlementStatus);
                        updateCheckPosting(conn, Id, facilityIndex, InquireString, InquireString[2]/*Setlstat*/, InquireString[1]/*ResponseText*/, SettlementStatus);

                    }
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
            FileWriter filewriter = new FileWriter("/sftpdrive/opt/CheckStatusLogs/CheckStatusExceptions.log", true);

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

    public static Object[] getInvoiceMasterDetails(Connection conn, String database, String InvoiceNo, String PatientMRN) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        double PaidAmount = 0.0D;
        double BalAmount = 0.0D;
        double TotalAmount = 0.0D;
        int Paid = 0;
        int InstallmentApplied = 0;
        int refundFlag = 0;
        int VoidFlag = 0;
        int masterIdx = 0;
        try {
            Query = "Select PaidAmount,BalAmount,Paid,TotalAmount,InstallmentApplied,refundFlag,VoidFlag,Id " +
                    "FROM " + database + ".InvoiceMaster WHERE PatientMRN = " + PatientMRN + " AND " +
                    " InvoiceNo = '" + InvoiceNo + "' AND Status = 0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PaidAmount = rset.getDouble(1);
                BalAmount = rset.getDouble(2);
                Paid = rset.getInt(3);
                TotalAmount = rset.getDouble(4);
                InstallmentApplied = rset.getInt(5);
                refundFlag = rset.getInt(6);
                VoidFlag = rset.getInt(7);
                masterIdx = rset.getInt(8);
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {

        }
        return new Object[]{PaidAmount, BalAmount, Paid, TotalAmount, InstallmentApplied, refundFlag, VoidFlag, masterIdx};
    }

    public static String[] receiptClientData(Connection conn, int facilityIndex) {
        ResultSet rset = null;
        String Query = "";
        Statement stmt = null;
        String FullName = "";
        String Address = "";
        String Phone = "";

        try {
            Query = "SELECT FullName,Address,Phone FROM oe.clients WHERE Id = " + facilityIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FullName = rset.getString(1);
                Address = rset.getString(2);
                Phone = rset.getString(3);

            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {

        }
        return new String[]{FullName, Address, Phone};
    }

    public static void insertInvoiceMasterHistory(Connection conn, String database,
                                                  String PatientMRN, String InvoiceNo) {
        PreparedStatement pStmt = null;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();

        try {
            Query = "SELECT Id,PatientMRN,InvoiceNo,TotalAmount,PaidAmount,BalAmount,Paid,PaymentDateTime,InvoiceCreatedBy,CreatedDate,Status," +
                    "InstallmentApplied,refundFlag,RefundDateTime,VoidFlag,VoidDateTime,CreatedBy " +
                    "FROM " + database + ".InvoiceMaster WHERE PatientMRN = " + PatientMRN + " AND InvoiceNo = '" + InvoiceNo + "' AND Status = 0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                pStmt = conn.prepareStatement(
                        "INSERT INTO " + database + ".InvoiceMasterHistory (OldMasterID,PatientMRN, InvoiceNo, TotalAmount, PaidAmount, BalAmount, Paid, PaymentDateTime, " +
                                "InvoiceCreatedBy, CreatedDate, Status,InstallmentApplied,refundFlag,RefundDateTime,VoidFlag,VoidDateTime,CreatedBy,UserIP )" +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'From Scheduler') ");
                pStmt.setInt(1, rset.getInt(1)); //OldMasterID
                pStmt.setString(2, rset.getString(2)); //PatientMRN
                pStmt.setString(3, rset.getString(3)); //InvoiceNo
                pStmt.setDouble(4, rset.getDouble(4)); //TotalAmount
                pStmt.setDouble(5, rset.getDouble(5)); //PaidAmount
                pStmt.setDouble(6, rset.getDouble(6)); //BalAmount
                pStmt.setInt(7, rset.getInt(7)); //Paid
                pStmt.setString(8, rset.getString(8));//PaymentDateTime
                pStmt.setString(9, rset.getString(9));//InvoiceCreatedBy
                pStmt.setString(10, rset.getString(10));//CreatedDate
                pStmt.setInt(11, rset.getInt(11));//Status
                pStmt.setInt(12, rset.getInt(12));//InstallmentApplied
                pStmt.setInt(13, rset.getInt(13));//refundFlag
                pStmt.setString(14, rset.getString(14));//RefundDateTime
                pStmt.setInt(15, rset.getInt(15));//VoidFlag
                pStmt.setString(16, rset.getString(16));//VoidDateTime
                pStmt.setString(17, rset.getString(17));//CreatedBy

                pStmt.executeUpdate();
                pStmt.close();
            }

        } catch (Exception Ex) {

        }
    }

    public static void updateInvoiceMaster(Connection conn, String database, double PaidAmount,
                                           double ApprovedAmount, double BalAmount, int Paid, String PatientMRN, String InvoiceNo) {
        Statement stmt = null;
        String Query = "";
        try {
            Query = " UPDATE " + database + ".InvoiceMaster SET PaidAmount = '" + (PaidAmount + ApprovedAmount) + "', " +
                    "BalAmount = '" + (BalAmount - ApprovedAmount) + "', Paid = '" + Paid + "', PaymentDateTime = now() " +
                    " WHERE PatientMRN = " + PatientMRN + " AND InvoiceNo = '" + InvoiceNo + "' AND Status = 0 ";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
        } catch (Exception Ex) {

        }
    }

    public static void paymentReceiptInsertion(Connection conn, String database,
                                               String PatientMRN, String InvoiceNo, double TotalAmount, int Paid, String RefNo,
                                               String Remarks, String PayMethod, double BalAmount, String CreatedBy, String UserIP,
                                               String ActionID, double ApprovedAmount, String ReceiptNo, String Receipt) {
        PreparedStatement pStmt = null;
        UtilityHelper helper = new UtilityHelper();
        try {
            pStmt = conn.prepareStatement(
                    "INSERT INTO " + database + ".PaymentReceiptInfo (PatientMRN,InvoiceNo,TotalAmount," +
                            "PaidAmount,Paid,RefNo,Remarks,PayMethod,CreatedDate, BalAmount,CreatedBy,UserIP,ActionID,ReceiptNo,Receipt)" +
                            " VALUES (?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?) ");
            pStmt.setString(1, PatientMRN);
            pStmt.setString(2, InvoiceNo);
            pStmt.setDouble(3, TotalAmount);
            pStmt.setDouble(4, ApprovedAmount);
            pStmt.setInt(5, Paid);
            pStmt.setString(6, RefNo);//ref No
            pStmt.setString(7, Remarks);//remarks
            pStmt.setString(8, PayMethod);// Pay Method
            pStmt.setDouble(9, BalAmount - ApprovedAmount);
            pStmt.setString(10, CreatedBy);
            pStmt.setString(11, UserIP);
            pStmt.setString(12, ActionID);
            pStmt.setString(13, ReceiptNo);
            pStmt.setString(14, Receipt);
            pStmt.executeUpdate();
            pStmt.close();
        } catch (Exception Ex) {

        }
    }

    public static String getReceiptCounter(Connection conn, String databaseName) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String receiptCounter = "";

        try {
            Query = "SELECT SUBSTRING(IFNULL(MAX(Convert(ReceiptNo ,UNSIGNED INTEGER)),0)+100000001,2,8) " +
                    "FROM " + databaseName + ".PaymentReceiptInfo";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                receiptCounter = rset.getString(1);

            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {

        }
        return receiptCounter;
    }


    public static String[] getPatientInfo(Connection conn, String database, String PatientMRN) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        String FirstName = "";
        String LastName = "";
        String Address = "";
        String City = "";
        String State = "";
        String ZipCode = "";
        String Country = "";
        String PhNumber = "";
        String Email = "";
        String Title = "";
        String MiddleInitial = "";
        String MaritalStatus = "";
        String MRN = "";
        String DOB = "";
        String Age = "";
        String Gender = "";
        String SSN = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        String SelfPayChk = "";
        String CreatedDate = "";
        String Address2 = "";

        try {
            Query = "SELECT FirstName, LastName, IFNULL(Email,''), IFNULL(City,'N/A'), IFNULL(State,'N/A'), IFNULL(ZipCode,'N/A'), " + //6
                    "IFNULL(Country,'N/A'), IFNULL(PhNumber,'N/A'), IFNULL(Address,'N/A')," + //9
                    "IFNULL(Title,'N/A'),IFNULL(MiddleInitial,'N/A'),IFNULL(MaritalStatus,'N/A'),IFNULL(MRN,'N/A')," + //13
                    "IFNULL(DOB,'N/A'), IFNULL(Age,'N/A'), IFNULL(Gender,'N/A'), " + //16
                    "IFNULL(SSN,'N/A'), IFNULL(Occupation,'N/A'), IFNULL(Employer,'N/A'), IFNULL(EmpContact,'N/A'), " + //20
                    "IFNULL(PriCarePhy,'N/A'), IFNULL(ReasonVisit,'N/A'), IFNULL(SelfPayChk,'N/A')," + //23
                    "IFNULL(CreatedDate,'N/A'), IFNULL(Address2,'N/A')  " + //25
                    "FROM " + database + ".PatientReg  " +
                    "WHERE MRN = '" + PatientMRN + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FirstName = rset.getString(1);
                LastName = rset.getString(2);
                Email = rset.getString(3);
                City = rset.getString(4);
                State = rset.getString(5);
                ZipCode = rset.getString(6);
                Country = rset.getString(7);
                PhNumber = rset.getString(8);
                Address = rset.getString(9);

                Title = rset.getString(10);
                MiddleInitial = rset.getString(11);
                MaritalStatus = rset.getString(12);
                MRN = rset.getString(13);
                DOB = rset.getString(14);
                Age = rset.getString(15);
                Gender = rset.getString(16);
                SSN = rset.getString(17);
                Occupation = rset.getString(18);
                Employer = rset.getString(19);
                EmpContact = rset.getString(20);
                PriCarePhy = rset.getString(21);
                ReasonVisit = rset.getString(22);
                SelfPayChk = rset.getString(23);
                CreatedDate = rset.getString(24);
                Address2 = rset.getString(25);
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {

        }
        return new String[]{FirstName, LastName, Email, City, State, ZipCode, Country, PhNumber, Address,
                Title, MiddleInitial, MaritalStatus, MRN, DOB, Age, Gender, SSN, Occupation, Employer, EmpContact,
                PriCarePhy, ReasonVisit, SelfPayChk, CreatedDate, Address2};
    }

}
