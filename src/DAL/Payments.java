package DAL;

import Handheld.UtilityHelper;
import md.Services;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Arrays;

@SuppressWarnings("Duplicates")
public class Payments extends HttpServlet {


    /**
     * Initialize global variables
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**
     * Process the HTTP Get request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = new PrintWriter(response.getOutputStream());
        out.println("<html>");
        out.println("<head><title>UtilityHelper</title></head>");
        out.println("<body>Hello From UtilityHelper doGet()");
        out.println("</body></html>");
        out.close();
    }

    /**
     * Process the HTTP Post request
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = new PrintWriter(response.getOutputStream());
        out.println("<html>");
        out.println("<head><title>UtilityHelper</title></head>");
        out.println("<body>");
        out.println("</body></html>");
        out.close();
    }

    public String[] getACHAuthConnect(Connection conn, int ClientId) throws SQLException {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        String EndPoint = "";
        String UserName = "";
        String Password = "";
        String MerchantId = "";
        String Currency = "";

        try {

            Query = "{CALL SP_GET_ACHAuthorization(?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setInt(1, ClientId);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                EndPoint = rset.getString(1);
                UserName = rset.getString(2);
                Password = rset.getString(3);
                MerchantId = rset.getString(4);
                Currency = rset.getString(5);
            }
            rset.close();
            cStmt.close();

/*            System.out.println(" EndPoint " + EndPoint);
            System.out.println(" UserName " + UserName);
            System.out.println(" Password " + Password);
            System.out.println(" MerchantId " + MerchantId);
            System.out.println(" Currency " + Currency);*/

        } catch (Exception Ex) {
            //Services.MobileExceptionDumps("UtilityHelper", "getAuthConnect -- SP -- 003 ", request, Ex, servletContext);
            return new String[]{"Exception Message: " + Ex.getMessage()};
        }
        return new String[]{EndPoint, UserName, Password, MerchantId, Currency};
    }

    public int checkCCCredentials(HttpServletRequest request, Connection conn, int ClientId, ServletContext servletContext) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        int foundCredentials = 0;
        try {
            Query = "Select IFNULL(COUNT(*),0) from oe.CardConnectCredentials " +
                    "WHERE ClientId = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                foundCredentials = rset.getInt(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Checking Credit Card Credentials Table ", servletContext, Ex, "DAL - Payments", "checkCCCredentials", conn);
            Services.DumException("DAL - Payments", "checkCCCredentials", request, Ex, getServletContext());
        }
        return foundCredentials;
    }

    public boolean addressVerification(HttpServletRequest request, Connection conn, String dbName, ServletContext servletContext, int mrn) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        boolean foundAddress = true;
        String Address = "";
        String City = "";
        String State = "";
        String ZipCode = "";
        String Country = "";
        try {
/*            Query = "SELECT IFNULL(COUNT(*),0) FROM " + dbName + ".PatientReg " +
                    "WHERE MRN = " + mrn + " AND (Address is null OR City is NULL or State is null or " +
                    "ZipCode is NULL or Country is NULL)";*/
            Query = "SELECT IFNULL(Address,'-'),IFNULL(City,'-'),IFNULL(State,'-')," +
                    "IFNULL(ZipCode,'-'),IFNULL(Country,'-') FROM " + dbName + ".PatientReg WHERE MRN = " + mrn;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Address = rset.getString(1);
                City = rset.getString(2);
                State = rset.getString(3);
                ZipCode = rset.getString(4);
                Country = rset.getString(5);
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in address Verification ", servletContext, Ex, "DAL - Payments", "addressVerification", conn);
            Services.DumException("DAL - Payments", "addressVerification", request, Ex, getServletContext());
        }
        if (Address.equals("") || Address.isEmpty() || Address.equals("-") ||
                City.equals("") || City.isEmpty() || City.equals("-") ||
                State.equals("") || State.isEmpty() || State.equals("-") ||
                ZipCode.equals("") || ZipCode.isEmpty() || ZipCode.equals("-") ||
                Country.equals("") || Country.isEmpty() || Country.equals("-"))
            foundAddress = false;

        return foundAddress;
    }

    public int checkBoltCredentials(HttpServletRequest request, Connection conn, int ClientId, ServletContext servletContext) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        int foundCredentials = 0;
        try {
/*            Query = "Select IFNULL(COUNT(*),0) from oe.BoltClientProperties WHERE ClientId = " + ClientId + " AND Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                foundCredentials = rset.getInt(1);
            }
            rset.close();
            stmt.close();*/
            Query = "{CALL SP_GET_BoltCredentials(?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setInt(1, ClientId);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                foundCredentials = rset.getInt(1);
            }
            rset.close();
            cStmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Checking Bolt Credentials Table ", servletContext, Ex, "DAL - Payments", "checkBoltCredentials", conn);
            Services.DumException("DAL - Payments", "checkBoltCredentials", request, Ex, getServletContext());
        }
        return foundCredentials;
    }

    public StringBuilder getDeviceList(HttpServletRequest request, Connection conn, ServletContext servletContext, int facilityIndex) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuilder DeviceList = new StringBuilder();

        try {
            Query = "{CALL SP_GET_deviceList(?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setInt(1, facilityIndex);
            rset = cStmt.executeQuery();
            while (rset.next()) {
                DeviceList.append("<option value=" + rset.getString(1) + ">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
            }
            rset.close();
            cStmt.close();
        } catch (Exception Ex) {
            Services.DumException("DAL - Payments", "getDeviceList ", request, Ex, servletContext);
        }
        return DeviceList;
    }

    public int boltFailures(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String DeviceS, String sessionResponse, String userId, String userIp, int flag) {
        PreparedStatement pStmt = null;
        UtilityHelper helper = new UtilityHelper();
        try {
            pStmt = conn.prepareStatement(
                    "INSERT INTO " + database + ".BoltDeviceConnectionFailures (DeviceId,SessionResponse,Status,CreatedDate,UserId,UserIP,Flag) " +
                            "VALUES (?,?,0,NOW(),?,?,?) ");
            pStmt.setString(1, DeviceS);
            pStmt.setString(2, sessionResponse);
            pStmt.setString(3, userId);
            pStmt.setString(4, userIp);
            pStmt.setInt(5, flag);
            pStmt.executeUpdate();
            pStmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in BoltDeviceConnectionFailures Table ", servletContext, Ex, "DAL - Payments", "boltFailures", conn);
            Services.DumException("DAL - Payments", "boltFailures", request, Ex, getServletContext());
        }
        return 1;
    }

    public String sessionActive(HttpServletRequest request, Connection conn, ServletContext servletContext, String database) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        StringBuilder DeviceList = new StringBuilder();
        String activeSession = "";

        try {
            Query = "Select SessionKey FROM " + database + ".ActiveSessionBolt ";
            System.out.println("Query of Session Active is present: " + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                activeSession = rset.getString(1);
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in sessionActive Table ", servletContext, Ex, "DAL - Payments", "sessionActive", conn);
            Services.DumException("DAL - Payments", "sessionActive", request, Ex, getServletContext());
        }
        return activeSession;
    }

    public void sessionInProgressUpdate(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, int isProgress, String sessionKey) {
        Statement stmt = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        try {
            Query = "UPDATE " + database + ".BoltSessionsList SET inProgressSession = " + isProgress + " " +
                    "WHERE SessionKey = '" + sessionKey + "' ";
            System.out.println("Query for UPDATING Session in Progress : " + Query);
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in sessionIn Progress Update ", servletContext, Ex, "DAL - Payments", "sessionInProgressUpdate", conn);
            Services.DumException("DAL - Payments", "sessionInProgressUpdate", request, Ex, getServletContext());
        }
    }

    public int isSessionInProgress(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String sessionKey) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        int isInProgress = 0;
        try {
            Query = "Select inProgressSession FROM " + database + ".BoltSessionsList WHERE SessionKey = '" + sessionKey + "' ";
            System.out.println("Query of Session in Progress Check: " + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                isInProgress = rset.getInt(1);
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in isSessionInProgress  ", servletContext, Ex, "DAL - Payments", "isSessionInProgress", conn);
            Services.DumException("DAL - Payments", "isSessionInProgress", request, Ex, getServletContext());
        }
        return isInProgress;
    }

    public int activeSessionBolt(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String deviceS, String sessionKey, String userId) {
        PreparedStatement pStmt = null;
        UtilityHelper helper = new UtilityHelper();
        try {
            pStmt = conn.prepareStatement(
                    "INSERT INTO " + database + ".ActiveSessionBolt (SessionKey,DeviceId,Status,CreatedDate, CreatedBy) " +
                            "VALUES (?,?,0,now(),?) ");
            pStmt.setString(1, sessionKey);
            pStmt.setString(2, deviceS);
            pStmt.setString(3, userId);
            pStmt.executeUpdate();
            pStmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in activeSessionBolt Table ", servletContext, Ex, "DAL - Payments", "activeSessionBolt", conn);
            Services.DumException("DAL - Payments", "activeSessionBolt", request, Ex, getServletContext());
        }
        return 1;
    }

    public int boltSessions(HttpServletRequest request, Connection conn, ServletContext servletContext,
                            String database, String deviceS, String sessionKey, String userId,
                            String invoiceNum, int MRN, int invoiceMstIdx) {
        PreparedStatement pStmt = null;
        UtilityHelper helper = new UtilityHelper();
        try {
            pStmt = conn.prepareStatement(
                    "INSERT INTO " + database + ".BoltSessionsList (SessionKey,InvoiceNo,MRN,Device," +
                            "Status,CreatedDate, CreatedBy,InvoiceMasterIdx) " +
                            "VALUES (?,?,?,?,0,now(),?,?) ");
            pStmt.setString(1, sessionKey);
            pStmt.setString(2, invoiceNum);
            pStmt.setInt(3, MRN);
            pStmt.setString(4, deviceS);
            pStmt.setString(5, userId);
            pStmt.setInt(6, invoiceMstIdx);
            pStmt.executeUpdate();
            pStmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in boltSessions Table ", servletContext, Ex, "DAL - Payments", "boltSessions", conn);
            Services.DumException("DAL - Payments", "boltSessions", request, Ex, getServletContext());
        }
        return 1;
    }

    public void updateBoltSessionDC(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String sessionKey) {
        Statement stmt = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        try {
            Query = "UPDATE " + database + ".BoltSessionsList SET isDisconnected = 1 WHERE SessionKey = '" + sessionKey + "' ";
            System.out.println("Query for UPDATING DC Col: " + Query);
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Deleting Session in Active Table ", servletContext, Ex, "DAL - Payments", "updateBoltSessionDC", conn);
            Services.DumException("DAL - Payments", "deleteActiveSessionByDeviceId", request, Ex, getServletContext());
        }
    }

    public void updateBoltSessionCancel(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String sessionKey) {
        Statement stmt = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        try {
            Query = "UPDATE " + database + ".BoltSessionsList SET isCancelled = 1 WHERE SessionKey = '" + sessionKey + "' ";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in update Bolt Session Cancel ", servletContext, Ex, "DAL - Payments", "updateBoltSessionCancel", conn);
            Services.DumException("DAL - Payments", "updateBoltSessionCancel", request, Ex, getServletContext());
        }
    }

    public void deleteActiveSessionByDeviceId(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String deviceS) {
        Statement stmt = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        try {
            Query = "DELETE FROM " + database + ".ActiveSessionBolt WHERE DeviceId = '" + deviceS + "' ";
            System.out.println("Deleting Active Session Bolt : " + Query);
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Deleting Session in Active Table ", servletContext, Ex, "DAL - Payments", "deleteActiveSessionByDeviceId", conn);
            Services.DumException("DAL - Payments", "deleteActiveSessionByDeviceId", request, Ex, getServletContext());
        }
    }

    public String[] checkActiveSessionBolt(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String deviceS) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        int FoundSession = 0;
        String existingSessionKey = "";
        try {
            //            Query = "Select COUNT(*) from " + Database + ".ActiveSessionBolt where ClientId = " + ClientId
//                    + " and ltrim(rtrim(UPPER(CreatedBy))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            Query = "Select IFNULL(COUNT(*),0),IFNULL(SessionKey,'') from " + database + ".ActiveSessionBolt WHERE DeviceId = '" + deviceS + "' ";
            System.out.println("Query for Active Session: " + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundSession = rset.getInt(1);
                existingSessionKey = rset.getString(2).trim();
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in check Active Session Bolt ", servletContext, Ex, "DAL - Payments", "checkActiveSessionBolt", conn);
            Services.DumException("DAL - Payments", "checkActiveSessionBolt", request, Ex, getServletContext());
        }
        return new String[]{String.valueOf(FoundSession), existingSessionKey};
    }

    public String[] boltConnectionProperties(HttpServletRequest request, Connection conn, ServletContext servletContext, int facilityIdx, String device) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        String BaseURL = "";
        String APIKey = "";
        String MerchantId = "";
        String HSN = "";
        String ConnectForce = "";
        String ReadcardIncludeSignature = "";
        String ReadcardAmountDisplay = "";
        String ReadcardIncludePin = "";
        String ReadcardBeep = "";
        String ReadcardAid = "";
        String ReadmanualIncludeSignature = "";
        String ReadmanualExpirationDate = "";
        String ReadmanualBeep = "";
        String AuthcardIncludeSignature = "";
        String AuthcardAmountDisplay = "";
        String AuthcardBeep = "";
        String AuthcardAuthMerchant = "";
        String AuthcardAid = "";
        String AuthcardIncludeAvs = "";
        String AuthcardIncludePin = "";
        String AuthcardCapture = "";
        String AuthmanualIncludeSignature = "";
        String AuthmanualAmountDisplay = "";
        String AuthmanualBeep = "";
        String AuthmanualAuthMerchant = "";
        String AuthmanualIncludeAvs = "";
        String AuthmanualIncludeCvv = "";
        String AuthmanualCapture = "";
        try {
            Query = "Select BaseURL, APIKey, MerchantId, HSN, ConnectForce, ReadcardIncludeSignature, ReadcardAmountDisplay, ReadcardIncludePin, ReadcardBeep, " +
                    "ReadcardAid, ReadmanualIncludeSignature, ReadmanualExpirationDate, ReadmanualBeep, AuthcardIncludeSignature, AuthcardAmountDisplay, " +
                    "AuthcardBeep, IFNULL(AuthcardAuthMerchant,''), AuthcardAid, AuthcardIncludeAvs, AuthcardIncludePin, AuthcardCapture, AuthmanualIncludeSignature, " +
                    "AuthmanualAmountDisplay, AuthmanualBeep, AuthmanualAuthMerchant, AuthmanualIncludeAvs, AuthmanualIncludeCvv, AuthmanualCapture " +
                    "from oe.BoltClientProperties where Status = 0 and ClientId = " + facilityIdx + " and DeviceHSN = '" + device + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                BaseURL = rset.getString(1);
                APIKey = rset.getString(2);
                MerchantId = rset.getString(3);
                HSN = rset.getString(4);
                ConnectForce = rset.getString(5);
                ReadcardIncludeSignature = rset.getString(6);
                ReadcardAmountDisplay = rset.getString(7);
                ReadcardIncludePin = rset.getString(8);
                ReadcardBeep = rset.getString(9);
                ReadcardAid = rset.getString(10);
                ReadmanualIncludeSignature = rset.getString(11);
                ReadmanualExpirationDate = rset.getString(12);
                ReadmanualBeep = rset.getString(13);
                AuthcardIncludeSignature = rset.getString(14);
                AuthcardAmountDisplay = rset.getString(15);
                AuthcardBeep = rset.getString(16);
                AuthcardAuthMerchant = rset.getString(17);
                AuthcardAid = rset.getString(18);
                AuthcardIncludeAvs = rset.getString(19);
                AuthcardIncludePin = rset.getString(20);
                AuthcardCapture = rset.getString(21);
                AuthmanualIncludeSignature = rset.getString(22);
                AuthmanualAmountDisplay = rset.getString(23);
                AuthmanualBeep = rset.getString(24);
                AuthmanualAuthMerchant = rset.getString(25);
                AuthmanualIncludeAvs = rset.getString(26);
                AuthmanualIncludeCvv = rset.getString(27);
                AuthmanualCapture = rset.getString(28);
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in bolt Connection Properties ", servletContext, Ex, "DAL - Payments", "boltConnectionProperties", conn);
            Services.DumException("DAL - Payments", "boltConnectionProperties", request, Ex, getServletContext());
        }
        return new String[]{BaseURL, APIKey, MerchantId, HSN, ConnectForce, ReadcardIncludeSignature,
                ReadcardAmountDisplay, ReadcardIncludePin, ReadcardBeep, ReadcardAid,
                ReadmanualIncludeSignature, ReadmanualExpirationDate, ReadmanualBeep, AuthcardIncludeSignature,
                AuthcardAmountDisplay, AuthcardBeep, AuthcardAuthMerchant, AuthcardAid, AuthcardIncludeAvs,
                AuthcardIncludePin, AuthcardCapture, AuthmanualIncludeSignature, AuthmanualAmountDisplay,
                AuthmanualBeep, AuthmanualAuthMerchant, AuthmanualIncludeAvs, AuthmanualIncludeCvv, AuthmanualCapture};
    }

    public Object[] sessionTimeCalculationIfIdle(HttpServletRequest request, Connection conn, ServletContext servletContext, String checkedSessionKey, String dbName) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        int timeStampDiff = 0;
        boolean checkedSession = false;
        try {
            Query = "SELECT IFNULL(TIMESTAMPDIFF(MINUTE,CreatedDate,NOW()),0) FROM " + dbName + ".ActiveSessionBolt " +
                    "WHERE SessionKey = '" + checkedSessionKey + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                timeStampDiff = rset.getInt(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in session Time Calculation If Idle ", servletContext, Ex, "DAL - Payments", "sessionTimeCalculationIfIdle", conn);
            Services.DumException("DAL - Payments", "sessionTimeCalculationIfIdle", request, Ex, getServletContext());
        }
        if (timeStampDiff > 7)
            checkedSession = true;

        return new Object[]{checkedSession, timeStampDiff};
    }

    public void paymentReceiptInsertion(HttpServletRequest request, Connection conn, ServletContext servletContext, String database,
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
            helper.SendEmailWithAttachment("Error in paymentReceiptInsertion Table ", servletContext, Ex, "DAL - Payments", "paymentReceiptInsertion", conn);
            Services.DumException("DAL - Payments", "paymentReceiptInsertion", request, Ex, getServletContext());
        }
    }


    public void updateInvoiceMaster(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, double PaidAmount,
                                    double ApprovedAmount, double BalAmount, int Paid, String PatientMRN, String InvoiceNo) {
        Statement stmt = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        try {
            Query = " UPDATE " + database + ".InvoiceMaster SET PaidAmount = '" + (PaidAmount + ApprovedAmount) + "', " +
                    "BalAmount = '" + (BalAmount - ApprovedAmount) + "', Paid = '" + Paid + "', PaymentDateTime = now() " +
                    " WHERE PatientMRN = " + PatientMRN + " AND InvoiceNo = '" + InvoiceNo + "' AND Status = 0 ";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in updating InvoiceMaster ", servletContext, Ex, "DAL - Payments", "updateInvoiceMaster", conn);
            Services.DumException("DAL - Payments", "updateInvoiceMaster", request, Ex, getServletContext());
        }
    }

    public void insertionJSONResponseBolt(HttpServletRequest request, Connection conn, ServletContext servletContext, String database,
                                          String InvoiceNo, String PatientMRN, String AuthCardRe, String UserId, String UserIP, String ActionID,
                                          double boltAmount, String boltDescription, String DeviceList, int payRecIdx, String responseType) {
        PreparedStatement pStmt = null;
        UtilityHelper helper = new UtilityHelper();
        try {
            pStmt = conn.prepareStatement(
                    "INSERT INTO " + database + ".JSON_Response (InvoiceNo,PatientMRN,JSON_Response,"
                            + " CreatedBy,CreatedDate,UserIP,ActionID,BoltAmountPaid,Description,DeviceList,PaymentReceiptIdx,ResponseType) " +
                            "VALUES (?,?,?,?,now(),?,?,?,?,?,?,?) ");
            pStmt.setString(1, InvoiceNo);
            pStmt.setString(2, PatientMRN);
            pStmt.setString(3, AuthCardRe);
            pStmt.setString(4, UserId);
            pStmt.setString(5, UserIP);
            pStmt.setString(6, ActionID);
            pStmt.setDouble(7, boltAmount);
            pStmt.setString(8, boltDescription);
            pStmt.setString(9, DeviceList);
            pStmt.setInt(10, payRecIdx);
            pStmt.setString(11, responseType);
            pStmt.executeUpdate();
            pStmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in insertionJSONResponseBolt Table ", servletContext, Ex, "DAL - Payments", "insertionJSONResponseBolt", conn);
            Services.DumException("DAL - Payments", "insertionJSONResponseBolt", request, Ex, getServletContext());
        }
    }


    public void insertInvoiceMasterHistory(HttpServletRequest request, Connection conn, ServletContext servletContext, String database,
                                           String PatientMRN, String InvoiceNo, String UserIP) {
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
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
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
                pStmt.setString(18, UserIP);//UserIP

                pStmt.executeUpdate();
                pStmt.close();
            }

        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in insertInvoiceMasterHistory Table ", servletContext, Ex, "DAL - Payments", "insertInvoiceMasterHistory", conn);
            Services.DumException("DAL - Payments", "insertInvoiceMasterHistory", request, Ex, getServletContext());
        }
    }

    public double getPaidAmount(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String InvoiceNo, String PatientMRN) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        double PaidAmount = 0.0D;
        try {
            Query = "Select PaidAmount from " + database + ".InvoiceMaster WHERE PatientMRN = " + PatientMRN + " AND " +
                    " InvoiceNo = '" + InvoiceNo + "' AND Status = 0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                PaidAmount = rset.getDouble(1);
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in getPaidAmount Table ", servletContext, Ex, "DAL - Payments", "getPaidAmount", conn);
            Services.DumException("DAL - Payments", "getPaidAmount", request, Ex, getServletContext());
        }
        return PaidAmount;
    }

    public double getBalAmount(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String InvoiceNo, String PatientMRN) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        double balAmount = 0.0D;
        try {
            Query = "Select BalAmount from " + database + ".InvoiceMaster WHERE PatientMRN = " + PatientMRN + " AND " +
                    " InvoiceNo = '" + InvoiceNo + "' AND Status = 0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                balAmount = rset.getDouble(1);
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in getBalAmount Table ", servletContext, Ex, "DAL - Payments", "getBalAmount", conn);
            Services.DumException("DAL - Payments", "getBalAmount", request, Ex, getServletContext());
        }
        return balAmount;
    }

    public Object[] getInvoiceMasterDetails(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String InvoiceNo, String PatientMRN) {
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
            helper.SendEmailWithAttachment("Error in getInvoiceMasterDetails Table ", servletContext, Ex, "DAL - Payments", "getInvoiceMasterDetails", conn);
            Services.DumException("DAL - Payments", "getInvoiceMasterDetails", request, Ex, getServletContext());
        }
        return new Object[]{PaidAmount, BalAmount, Paid, TotalAmount, InstallmentApplied, refundFlag, VoidFlag, masterIdx};
    }

    public void insertionCardConnectResponses(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String InvoiceNo, String PatientMRN,
                                              String ResponseText, String CVVResponse, String ResponseCode, String EntryMode, String AuthCode, String ResponseProc,
                                              String ResponseStatus, String RetRef, String Expiry, String AVS, String CreatedDate, int Status, int facilityIndex,
                                              String Token, String RetRefDate, String CVV2, String BinType, String Receipt, String ResponseType,
                                              String Remarks, double Amount, String AccountNo, String orderId, String commCard, String UserIP, String ActionID, String NameOnCard, int payRecIdx,
                                              String transactionFrom, String insuranceName, String insuranceSlip, String fileName, String userID) {

        PreparedStatement pStmt = null;
        UtilityHelper helper = new UtilityHelper();
        try {
            PreparedStatement MainReceipt = conn.prepareStatement(
                    "INSERT INTO " + database + ".CardConnectResponses(ResponseText,CVVResponse,ResponseCode,EntryMode," +
                            "AuthCode,ResponseProc,ResponseStatus,RetRef,Expiry,AVS,CreatedDate," +
                            "PatientMRN,InvoiceNo,ClientIndex, Token, RetRefDate, CVV2, BinType, Reciept, " +
                            "ResponseType,Remarks, Amount, AccountNo,orderId,commCard,UserIP,ActionID,NameOnCard,PaymentReceiptIdx," +
                            "TransactionFrom,InsuranceName,InsuranceSlip,FileName,CreatedBy,Status) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,NOW(),?,?,?,?,NOW(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            MainReceipt.setString(1, ResponseText);
            MainReceipt.setString(2, CVVResponse);
            MainReceipt.setString(3, ResponseCode);
            MainReceipt.setString(4, EntryMode);
            MainReceipt.setString(5, AuthCode);
            MainReceipt.setString(6, ResponseProc);
            MainReceipt.setString(7, ResponseStatus);
            MainReceipt.setString(8, RetRef);
            MainReceipt.setString(9, Expiry);
            MainReceipt.setString(10, AVS);
            MainReceipt.setString(11, PatientMRN);
            MainReceipt.setString(12, InvoiceNo);
            MainReceipt.setInt(13, facilityIndex);
            MainReceipt.setString(14, Token);
            MainReceipt.setString(15, CVV2);
            MainReceipt.setString(16, BinType);
            MainReceipt.setString(17, Receipt);
            MainReceipt.setString(18, ResponseType);
            MainReceipt.setString(19, Remarks);
            MainReceipt.setDouble(20, Amount);
            MainReceipt.setString(21, AccountNo);
            MainReceipt.setString(22, orderId);
            MainReceipt.setString(23, commCard);
            MainReceipt.setString(24, UserIP);
            MainReceipt.setString(25, ActionID);
            MainReceipt.setString(26, NameOnCard);
            MainReceipt.setInt(27, payRecIdx);
            MainReceipt.setString(28, transactionFrom);
            MainReceipt.setString(29, insuranceName);
            MainReceipt.setString(30, insuranceSlip);
            MainReceipt.setString(31, fileName);
            MainReceipt.setString(32, userID);
            MainReceipt.setInt(33, Status);
            MainReceipt.executeUpdate();
            //System.out.println("INSETION " + MainReceipt.toString());
            MainReceipt.close();

        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in insertionCardConnectResponses Table ", servletContext, Ex, "DAL - Payments", "insertionCardConnectResponses", conn);
            Services.DumException("DAL - Payments", "insertionCardConnectResponses", request, Ex, getServletContext());
        }
    }


    public int getInstallmentIdx(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String PatientMRN, String InvoiceNo) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int installmentIdx = 0;
        UtilityHelper helper = new UtilityHelper();
        try {
            Query = "Select Id from " + database + ".InstallmentPlan " +
                    "WHERE MRN = " + PatientMRN + " AND InvoiceNo = '" + InvoiceNo + "' AND Paid = 0 limit 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                installmentIdx = rset.getInt(1);
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in getInstallmentIdx ", servletContext, Ex, "DAL - Payments", "getInstallmentIdx", conn);
            Services.DumException("DAL - Payments", "getInstallmentIdx", request, Ex, getServletContext());
        }
        return installmentIdx;
    }

    public void updateInstallmentTable(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String PatientMRN, String InvoiceNo, int installmentIdx) {
        Statement stmt = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        try {
            Query = " UPDATE " + database + ".InstallmentPlan SET Paid = 1 " +
                    "WHERE MRN = " + PatientMRN + " AND InvoiceNo = '" + InvoiceNo + "' AND Id = " + installmentIdx + "";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in updateInstallmentTable ", servletContext, Ex, "DAL - Payments", "updateInstallmentTable", conn);
            Services.DumException("DAL - Payments", "updateInstallmentTable", request, Ex, getServletContext());
        }
    }

    public void insertCheckInfo(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, int PatientMRN, String InvoiceNo, int installmentFound,
                                String routing, String accountNo, String checkNumber, String checkDescription, double checkAmount, int PayMethod, String ActionID, String createdBy,
                                String UserIP, int facilityIndex, String RetRef, String[] response, String FileName, String InsuranceName, int Status) {
        PreparedStatement pStmt = null;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        try {
            pStmt = conn.prepareStatement(
                    "INSERT INTO " + database + ".CheckInfo (Routing,Account,CheckNo,Amount,Description,InvoiceNo,MRN," +
                            "PaymentMethod,InstallmentFound, CreatedBy,CreatedDate,UserIP,ActionID,isPaid,voidFlag,isRefund," +
                            "ResponseText,FileName,InsuranceName,Status) " +
                            " VALUES (?,?,?,?,?,?,?,?,?,?,NOW(),?,?,0,0,0,?,?,?,?) ");
            pStmt.setString(1, routing);
            pStmt.setString(2, accountNo);
            pStmt.setString(3, checkNumber);
            pStmt.setDouble(4, checkAmount);
            pStmt.setString(5, checkDescription);
            pStmt.setString(6, InvoiceNo);
            pStmt.setInt(7, PatientMRN);
            pStmt.setInt(8, PayMethod);
            pStmt.setInt(9, installmentFound);
            pStmt.setString(10, createdBy);
            pStmt.setString(11, UserIP);
            pStmt.setString(12, ActionID);
            pStmt.setString(13, Arrays.toString(response));
            pStmt.setString(14, FileName);
            pStmt.setString(15, InsuranceName);
            pStmt.setInt(16, Status);
            pStmt.executeUpdate();
            pStmt.close();

            int CheckPaymentIdx = 0;
            Query = "SELECT MAX(Id) FROM " + database + ".CheckInfo";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                CheckPaymentIdx = rset.getInt(1);
            rset.close();
            stmt.close();

            pStmt = conn.prepareStatement(
                    "INSERT INTO oe.CheckPosting (CheckPaymentIdx, CheckStatus, FacilityIndex, Status, CreatedDate, CreatedBy, UserIP, " +
                            "ActionID, PatientMRN, InvoiceNo, CheckResponse, ACHReference) " +
                            "VALUES (?,?,?,0,NOW(),?,?,?,?,?,?,?) ");
            pStmt.setInt(1, CheckPaymentIdx);
            pStmt.setInt(2, 0);
            pStmt.setInt(3, facilityIndex);
            pStmt.setString(4, createdBy);
            pStmt.setString(5, UserIP);
            pStmt.setString(6, ActionID);
            pStmt.setInt(7, PatientMRN);
            pStmt.setString(8, InvoiceNo);
            pStmt.setString(9, "Initial State (NO RESPONSE)");
            pStmt.setString(10, RetRef);
            pStmt.executeUpdate();

            pStmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in insertCheckInfo Table ", servletContext, Ex, "DAL - Payments", "insertCheckInfo", conn);
            Services.DumException("DAL - Payments", "insertCheckInfo", request, Ex, getServletContext());
        }
    }

    public void insertCashPayments(HttpServletRequest request, Connection conn, ServletContext servletContext, String database,
                                   String InvoiceNo, String PatientMRN, String UserId, String UserIP, String ActionID,
                                   double cashAmount, String RefNo) {
        PreparedStatement pStmt = null;
        UtilityHelper helper = new UtilityHelper();
        try {
            pStmt = conn.prepareStatement(
                    "INSERT INTO " + database + ".CashPayments (InvoiceNo,PatientMRN,Status,"
                            + " CreatedBy,CreatedDate,UserIP,ActionID,Amount,RefNo) " +
                            "VALUES (?,?,0,?,NOW(),?,?,?,?) ");
            pStmt.setString(1, InvoiceNo);
            pStmt.setString(2, PatientMRN);
            pStmt.setString(3, UserId);
            pStmt.setString(4, UserIP);
            pStmt.setString(5, ActionID);
            pStmt.setDouble(6, cashAmount);
            pStmt.setString(7, RefNo);
            pStmt.executeUpdate();
            pStmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in insertCashPayments Table ", servletContext, Ex, "DAL - Payments", "insertCashPayments", conn);
            Services.DumException("DAL - Payments", "insertCashPayments", request, Ex, getServletContext());
        }
    }


    public int getPaymentReceiptIndex(HttpServletRequest request, Connection conn, ServletContext servletContext, String database) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        int PayRecIdx = 0;
        try {
            Query = "Select MAX(Id) FROM " + database + ".PaymentReceiptInfo ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                PayRecIdx = rset.getInt(1);
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in getPaymentReceiptIndex  ", servletContext, Ex, "DAL - Payments", "getPaymentReceiptIndex", conn);
            Services.DumException("DAL - Payments", "getPaymentReceiptIndex", request, Ex, getServletContext());
        }
        return PayRecIdx;
    }


    public void insertRefundTransactionCC(HttpServletRequest request, Connection conn, ServletContext servletContext, String database,
                                          String Amount, String ResponseText, String OrderId, String ResponseCode, String MerchantId,
                                          String ResponseProc, String Receipt, String Currency, String ResponseStatus, String ReturnReference,
                                          int PatientMRN, String InvoiceNo) {
        PreparedStatement pStmt = null;
        UtilityHelper helper = new UtilityHelper();
        try {
/*            pStmt = conn.prepareStatement(
                    "INSERT INTO " + database + ".RefundTransactions (Amount, ResponseText, OrderId, ResponseCode, MerchantId, " +
                            "ResponseProc, Receipt,Currency,ResponseStatus,ReturnReference,Status,CreatedDate," +
                            "PatientMRN,InvoiceNo,OriginalRetRef,CardConnectResponseIndex,TransactionType,UserIP,ActionID,CreatedBy) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?,?,?,?,?,?,?) ");
            pStmt.setString(1, Response[1]);
            pStmt.setString(2, Response[2]);
            pStmt.setString(3, Response[3]);
            pStmt.setString(4, Response[7]);//ResponseCode
            pStmt.setString(5, Response[8]);//MerchantId
            pStmt.setString(6, Response[0]);//ResponseProc
            pStmt.setString(7, Response[4]);//Receipt
            pStmt.setString(8, Currency);//Currency
            pStmt.setString(9, Response[6]);//ResponseStatus
            pStmt.setString(10, Response[5]);//ReturnReference
            pStmt.setInt(11, PatientMRN);//PatientMRN
            pStmt.setString(12, InvoiceNo);//InvoiceNo
            pStmt.setString(13, RetRef);//OriginalRetRef
            pStmt.setInt(14, CardConnectIndx);//CardConnectResponseIndex
            pStmt.setString(15, "Card Connect");//TransactionType
            pStmt.setString(16, UserIP);
            pStmt.setString(17, "refundTransaction");
            pStmt.setString(18, UserId);*/
            pStmt.executeUpdate();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in insertRefundTransactionCC Table ", servletContext, Ex, "DAL - Payments", "insertCashPayments", conn);
            Services.DumException("DAL - Payments", "insertRefundTransactionCC", request, Ex, getServletContext());
        }
    }

    public void insertCheckInfo_LargePaymentCollection(HttpServletRequest request, Connection conn, ServletContext servletContext, String database,
                                                       String routing, String accountNo, String checkNumber, String checkDescription, double checkAmount, int PayMethod, String ActionID, String createdBy,
                                                       String UserIP, int facilityIndex, String RetRef, String InsuranceName, String FileName) {
        PreparedStatement pStmt = null;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        try {
            pStmt = conn.prepareStatement(
                    "INSERT INTO " + database + ".CheckInfo (Routing,Account,CheckNo,Amount,Description," +
                            "PaymentMethod, CreatedBy,CreatedDate,UserIP,ActionID,isPaid,voidFlag,isRefund,Status,InsuranceName,FileName) " +
                            " VALUES (?,?,?,?,?,?,?,NOW(),?,?,0,0,0,9,?,?) ");
            pStmt.setString(1, routing);
            pStmt.setString(2, accountNo);
            pStmt.setString(3, checkNumber);
            pStmt.setDouble(4, checkAmount);
            pStmt.setString(5, checkDescription);
            pStmt.setInt(6, PayMethod);
            pStmt.setString(7, createdBy);
            pStmt.setString(8, UserIP);
            pStmt.setString(9, ActionID);
            pStmt.setString(10, InsuranceName);
            pStmt.setString(11, FileName);
            pStmt.executeUpdate();
            pStmt.close();

            int CheckPaymentIdx = 0;
            Query = "SELECT MAX(Id) FROM " + database + ".CheckInfo";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                CheckPaymentIdx = rset.getInt(1);
            rset.close();
            stmt.close();

            pStmt = conn.prepareStatement(
                    "INSERT INTO oe.CheckPosting (CheckPaymentIdx, CheckStatus, FacilityIndex, Status, CreatedDate, CreatedBy, UserIP, " +
                            "ActionID, CheckResponse, ACHReference) " +
                            "VALUES (?,?,?,0,NOW(),?,?,?,?,?) ");
            pStmt.setInt(1, CheckPaymentIdx);
            pStmt.setInt(2, 0);
            pStmt.setInt(3, facilityIndex);
            pStmt.setString(4, createdBy);
            pStmt.setString(5, UserIP);
            pStmt.setString(6, ActionID);
            pStmt.setString(7, "Initial State (NO RESPONSE)");
            pStmt.setString(8, RetRef);
            pStmt.executeUpdate();

            pStmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in insertCheckInfo Table ", servletContext, Ex, "DAL - Payments", "insertCheckInfo_LargePaymentCollection", conn);
            Services.DumException("DAL - Payments", "insertCheckInfo_LargePaymentCollection", request, Ex, getServletContext());
        }
    }


    public StringBuilder showTransactionReportDatewise(HttpServletRequest request, Connection conn, ServletContext servletContext,
                                                       String startDate, String endDate, int facilityIndex, String dbName) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        UtilityHelper helper = new UtilityHelper();
        StringBuilder reportDisplay = new StringBuilder();
        int refundFlag = 0;
        int voidFlag = 0;
        String Status = "";

        try {
            Query = "{CALL SP_GET_TransactionDateWiseReport(?,?,?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, startDate);
            cStmt.setString(2, endDate);
            cStmt.setInt(3, facilityIndex);
            cStmt.setString(4, dbName);
            rset = cStmt.executeQuery();
            while (rset.next()) {
                refundFlag = 0;
                voidFlag = 0;
                refundFlag = rset.getInt(17);
                voidFlag = rset.getInt(18);
                if (refundFlag == 1) {
                    Status = "Refunded";
                    reportDisplay.append("<tr>");
                    reportDisplay.append("<td>" + rset.getString(5) + "</td>");//MRN

                    reportDisplay.append("<td>" + rset.getString(2) + "</td>");//NAME
                    reportDisplay.append("<td>$" + rset.getString(13) + "</td>");//Amount
                    reportDisplay.append("<td>" + rset.getString(14) + "</td>");//AccountNo
//                    TransactionReport.append("<td>" + rset.getString(6) + "</td>");//InvoiceNo
//                    TransactionReport.append("<td>" + rset.getString(19) + "</td>");//TransDate
//                    TransactionReport.append("<td>" + rset.getString(12) + "</td>");//Remarks
                    reportDisplay.append("<td>" + rset.getString(7) + "</td>");//ResponseText
//                TransactionReport.append("<td width=08%>" + rset.getString(8) + "</td>");//ResponseStatus
//                    reportDisplay.append("<td>" + rset.getString(9) + "</td>");//RetRef
                    reportDisplay.append("<td>" + rset.getString(10) + "</td>");//RetDate
                    reportDisplay.append("<td>" + Status + "</td>");//Status
                    reportDisplay.append("<td> ");
                    if (rset.getString(7).toUpperCase().equals("APPROVAL") && !rset.getString(13).equals("0.0")) {
                        reportDisplay.append("<button id=refundBtn onclick=\"refund(this.value)\" class=\"btn btn-primary btn-sm\" disabled title=\"Amount is refunded!\" value=" + rset.getInt(16) + " target=NewFrame1> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\"></i>[Refund]</font></button>");
                        reportDisplay.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" onclick=\"voidTransactions(this.value)\" title=\"Amount is refunded!\" disabled value=" + rset.getInt(16) + " target=NewFrame1 > <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i>[Void]</font></button>");
                        reportDisplay.append("<button id=printBtn class=\"btn btn-info btn-sm\" onclick=\"printReceipt(" + rset.getString(5) + ",'" + rset.getString(6) + "'," + rset.getInt(16) + ",'R','" + rset.getString(13) + "')\" value=" + rset.getInt(16) + " target=NewFrame1 > <font color = \"FFFFFF\"> <i class=\"fa fa-print\"></i>[Receipt]</font></button>");
                    } else if (rset.getString(13).equals("0.0") || rset.getString(13).equals("0")) {
                        reportDisplay.append("<button id=refundBtn title=\"Amount is zero!\" class=\"btn btn-primary btn-sm\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\" ></i>[Refund]</font></button>");
                        reportDisplay.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" title=\"Amount is zero!\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\" ></i>[Void]</font></button>");
                    } else {
                        reportDisplay.append("<button id=refundBtn class=\"btn btn-primary btn-sm\" data-toggle=\"modal\" data-target=\"#refundModal\" value=" + rset.getInt(1) + " disabled><i class=\"fa fa-plus\"></i>[Refund]</button>");
                        reportDisplay.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" value=" + rset.getInt(1) + " target=NewFrame1 disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i>[Void]</font></button>");
                    }
                    reportDisplay.append("</td>");
                    reportDisplay.append("</tr>");
                } else if (voidFlag == 1) {
                    Status = "Voided";
                    reportDisplay.append("<tr>");
                    reportDisplay.append("<td>" + rset.getString(5) + "</td>");//MRN

                    reportDisplay.append("<td>" + rset.getString(2) + "</td>");//NAME
                    reportDisplay.append("<td>$" + rset.getString(13) + "</td>");//Amount
                    reportDisplay.append("<td>" + rset.getString(14) + "</td>");//AccountNo
//                    TransactionReport.append("<td>" + rset.getString(6) + "</td>");//InvoiceNo
//                    TransactionReport.append("<td>" + rset.getString(19) + "</td>");//TransDate
//                    TransactionReport.append("<td>" + rset.getString(12) + "</td>");//Remarks
                    reportDisplay.append("<td>" + rset.getString(7) + "</td>");//ResponseText
//                TransactionReport.append("<td width=08%>" + rset.getString(8) + "</td>");//ResponseStatus
//                    reportDisplay.append("<td>" + rset.getString(9) + "</td>");//RetRef
                    reportDisplay.append("<td>" + rset.getString(10) + "</td>");//RetDate
                    reportDisplay.append("<td>" + Status + "</td>");//Status
                    reportDisplay.append("<td> ");
                    if (rset.getString(7).toUpperCase().equals("APPROVAL") && !rset.getString(13).equals("0.0")) {
                        reportDisplay.append("<button id=refundBtn onclick=\"refund(this.value)\" class=\"btn btn-primary btn-sm\" title=\"Amount is voided!\"  disabled value=" + rset.getInt(16) + " target=NewFrame1> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\"></i>[Refund]</font></button>");
                        reportDisplay.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" onclick=\"voidTransactions(this.value)\" title=\"Amount is voided!\"  disabled value=" + rset.getInt(16) + " target=NewFrame1 > <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i>[Void]</font></button>");
                        reportDisplay.append("<button id=printBtn class=\"btn btn-info btn-sm\" onclick=\"printReceipt(" + rset.getString(5) + ",'" + rset.getString(6) + "'," + rset.getInt(16) + ",'V','" + rset.getString(13) + "')\" value=" + rset.getInt(16) + " target=NewFrame1 > <font color = \"FFFFFF\"> <i class=\"fa fa-print\"></i>[Receipt]</font></button>");
                    } else if (rset.getString(13).equals("0.0") || rset.getString(13).equals("0")) {
                        reportDisplay.append("<button id=refundBtn title=\"Amount is zero!\" class=\"btn btn-primary btn-sm\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\" ></i>[Refund]</font></button>");
                        reportDisplay.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" title=\"Amount is zero!\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\" ></i>[Void]</font></button>");
                    } else {
                        reportDisplay.append("<button id=refundBtn class=\"btn btn-primary btn-sm\" data-toggle=\"modal\" data-target=\"#refundModal\" value=" + rset.getInt(1) + " disabled><i class=\"fa fa-plus\"></i>[Refund]</button>");
                        reportDisplay.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" value=" + rset.getInt(1) + " target=NewFrame1 disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i>[Void]</font></button>");
                    }
                    reportDisplay.append("</td>");
                    reportDisplay.append("</tr>");
                } else {
                    Status = "No Action";
                    reportDisplay.append("<tr>");
                    reportDisplay.append("<td>" + rset.getString(5) + "</td>");//MRN

                    reportDisplay.append("<td>" + rset.getString(2) + "</td>");//NAME
                    reportDisplay.append("<td>$" + rset.getString(13) + "</td>");//Amount
                    reportDisplay.append("<td>" + rset.getString(14) + "</td>");//AccountNo
//                    TransactionReport.append("<td>" + rset.getString(6) + "</td>");//InvoiceNo
//                    TransactionReport.append("<td>" + rset.getString(19) + "</td>");//TransDate
//                    TransactionReport.append("<td>" + rset.getString(12) + "</td>");//Remarks
                    reportDisplay.append("<td>" + rset.getString(7) + "</td>");//ResponseText
//                TransactionReport.append("<td width=08%>" + rset.getString(8) + "</td>");//ResponseStatus
//                    reportDisplay.append("<td>" + rset.getString(9) + "</td>");//RetRef
                    reportDisplay.append("<td>" + rset.getString(10) + "</td>");//RetDate
                    reportDisplay.append("<td>" + Status + "</td>");//Status
                    reportDisplay.append("<td> ");
                    if (rset.getString(7).toUpperCase().equals("APPROVAL") && !rset.getString(13).equals("0.0")) {
                        reportDisplay.append("<button id=refundBtn onclick=\"refund(this.value)\" class=\"btn btn-primary btn-sm\" value=" + rset.getInt(16) + " target=NewFrame1> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\"></i>[Refund]</font></button>");
                        reportDisplay.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" onclick=\"voidTransactions(this.value)\" value=" + rset.getInt(16) + " target=NewFrame1 > <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i>[Void]</font></button>");
//                        TransactionReport.append("<button id=printBtn class=\"btn btn-info btn-sm\" onclick=\"printReceipt()\" value=" + rset.getInt(16) + " target=NewFrame1 > <font color = \"FFFFFF\"> <i class=\"fa fa-print\"></i>[Print]</font></button>");
                    } else if (rset.getString(13).equals("0.0") || rset.getString(13).equals("0")) {
                        reportDisplay.append("<button id=refundBtn title=\"Amount is zero!\" class=\"btn btn-primary btn-sm\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\" ></i>[Refund]</font></button>");
                        reportDisplay.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" title=\"Amount is zero!\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\" ></i>[Void]</font></button>");
//                        TransactionReport.append("<button id=printBtn class=\"btn btn-info btn-sm\" title=\"Amount is zero!\" disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\" ></i>[Print]</font></button>");
                    } else {
                        reportDisplay.append("<button id=refundBtn class=\"btn btn-primary btn-sm\" data-toggle=\"modal\" data-target=\"#refundModal\" value=" + rset.getInt(1) + " disabled><i class=\"fa fa-plus\"></i>[Refund]</button>");
                        reportDisplay.append("<button id=voidBtn class=\"btn btn-danger btn-sm\" value=" + rset.getInt(1) + " target=NewFrame1 disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i>[Void]</font></button>");
//                        TransactionReport.append("<button id=printBtn class=\"btn btn-info btn-sm\" value=" + rset.getInt(1) + " target=NewFrame1 disabled> <font color = \"FFFFFF\"> <i class=\"fa fa-print\"></i>[Print]</font></button>");
                    }
                    reportDisplay.append("</td>");
                    reportDisplay.append("</tr>");
                }
            }
            rset.close();
            cStmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in showTransactionReportDatewise  ", servletContext, Ex, "DAL - Payments", "showTransactionReportDatewise", conn);
            Services.DumException("DAL - Payments", "showTransactionReportDatewise ", request, Ex, servletContext);
        }
        return reportDisplay;
    }
}

//    SELECT COUNT(*),MRN FROM PatientReg
//    GROUP BY MRN
//        HAVING COUNT(MRN) > 1