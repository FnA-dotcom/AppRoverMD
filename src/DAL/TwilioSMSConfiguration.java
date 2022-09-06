package DAL;

import Handheld.UtilityHelper;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import md.Services;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.sql.*;

@SuppressWarnings("Duplicates")
public class TwilioSMSConfiguration {
    PreparedStatement pStmt = null;
    private Statement stmt = null;
    private CallableStatement cStmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private UtilityHelper helper = new UtilityHelper();

    private String[] getTwilioAuthorization(HttpServletRequest req, Connection conn, ServletContext context) {
        cStmt = null;
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
            helper.SendEmailWithAttachment("Error in getTwilioAuthorization ", context, Ex, "DAL - TwilioSMSConfiguration", "getTwilioAuthorization", conn);
            Services.DumException("TwilioSMSConfiguration", "getTwilioAuthorization -- SP -- 001 ", req, Ex, context);
            return new String[]{"Exception Message: " + Ex.getMessage()};
        }
        return new String[]{accountSID, authToken};
    }

    private String[] getSenderDetails(HttpServletRequest req, Connection conn, ServletContext context,
                                      int advocateIdx, int facilityIdx) {
        cStmt = null;
        rset = null;
        Query = "";
        String PhNumber = "";

        try {
            Query = "{CALL SP_GET_SMSSenderDetails(?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setInt(1, advocateIdx);
            cStmt.setInt(2, facilityIdx);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                PhNumber = rset.getString(1);
            }
//            System.out.println("Phone Number TWILIO " + PhNumber);
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in getSenderDetails ", context, Ex, "DAL - TwilioSMSConfiguration", "getSenderDetails", conn);
            Services.DumException("TwilioSMSConfiguration", "getSenderDetails -- SP -- 002 ", req, Ex, context);
            return new String[]{"Exception Message: " + Ex.getMessage()};
        }
        return new String[]{PhNumber};
    }

    public String[] sendTwilioMessages(HttpServletRequest req, Connection conn, ServletContext context,
                                       String smsBody, int facilityIdx, String sendNumber, int UserIdx,
                                       String dbName, String MRN, int smsIdx) {
        cStmt = null;
        rset = null;
        Query = "";
        String SMSSID = "";
        String body = "";
        String status = "";
        String msg = "";
        //int maxSMSIndex = 0;
        try {
            String[] AuthFactor = getTwilioAuthorization(req, conn, context);
            String[] SenderDetails = getSenderDetails(req, conn, context, UserIdx, facilityIdx);

            System.out.println("Sending Message....");
//            System.out.println(" FROM --> " + SenderDetails[0]);
            Twilio.init(AuthFactor[0], AuthFactor[1]);
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(sendNumber), // to
                    new com.twilio.type.PhoneNumber(SenderDetails[0]), // from
                    smsBody)
                    .create();
//            System.out.println("After sending the msg");
            SMSSID = message.getSid();
//            System.out.println("First Value --> " + SMSSID);
            body = message.getBody();
//            System.out.println("Second Value --> " + body);
            //returnedDateSMS = message.getDateCreated();
            //System.out.println("Third Value --> " + returnedDateSMS);
            status = String.valueOf(message.getStatus());
//            System.out.println("Fourth Value --> " + status);

//            System.out.println("SMSID " + SMSSID);
//            System.out.println("body " + body);
            //System.out.println("returnedDateSMS " + returnedDateSMS);
//            System.out.println("dbName " + dbName);

/*            if (facilityIdx != 999) {
                maxSMSIndex = getMaxSMSIndex(req, conn, context, facilityIdx, dbName);
                updateSMSInfoTable(req, conn, context, maxSMSIndex, SMSSID, dbName);
                saveSMSResponse(req, conn, context, SMSSID, body, "", status, 0, UserIdx, MRN, facilityIdx, dbName);
            } else {
                maxSMSIndex = getMaxSMSIndex(req, conn, context, "oe");
                updateSMSInfoTable(req, conn, context, maxSMSIndex, SMSSID, "oe");
                saveSMSResponse(req, conn, context, SMSSID, body, "", status, 0, UserIdx, MRN, facilityIdx, "oe");
            }*/
            //maxSMSIndex = getMaxSMSIndex(req, conn, context, facilityIdx, dbName);
            //updateSMSInfoTable(req, conn, context, maxSMSIndex, SMSSID, dbName);
            saveSMSResponse(req, conn, context, SMSSID, body, "", status, 0, UserIdx, MRN, facilityIdx, dbName);

            System.out.println("Message Sent...!");
            msg = "Success";
        } catch (Exception Ex) {
            try {
                // maxSMSIndex = getMaxSMSIndex(req, conn, context, facilityIdx, dbName);
/*                Query = "UPDATE oe.SMS_Info SET ReasonFailure = '"+Ex.getMessage()+"' WHERE Id = " + maxSMSIndex;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();*/
                pStmt = conn.prepareStatement("UPDATE " + dbName + ".SMS_Info SET ReasonFailure = ? WHERE Id = ?");
                pStmt.setString(1, Ex.getMessage());
                pStmt.setInt(2, smsIdx);
                pStmt.executeUpdate();
                pStmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("EXCEPTION --> " + Ex.getMessage());
            helper.SendEmailWithAttachment("Error in sendTwilioMessages ", context, Ex, "DAL - TwilioSMSConfiguration", "sendTwilioMessages", conn);
            Services.DumException("TwilioSMSConfiguration", "sendTwilioMessages -- SP -- 003 ", req, Ex, context);
            msg = Ex.getMessage();
        }
        return new String[]{msg, SMSSID};
    }

    public StringBuilder getFacilityList(HttpServletRequest request, Connection connection, ServletContext servletContext,
                                         int advocateIdx) {
        cStmt = null;
        rset = null;
        Query = "";
        CallableStatement callableStatement = null;
        ResultSet resultSet = null;
        String Query1 = "";
        StringBuilder facilityList = new StringBuilder();
        try {
            Query = "{CALL SP_GET_facilityListAdvocate(?)}";
            cStmt = connection.prepareCall(Query);
            cStmt.setInt(1, advocateIdx);
            rset = cStmt.executeQuery();
            facilityList.append("<option value='' selected>Select Facility</option>");
            while (rset.next()) {
                Query1 = "{CALL SP_GET_clientListAdvocate(?)}";
                callableStatement = connection.prepareCall(Query1);
                callableStatement.setInt(1, rset.getInt(1));
                resultSet = callableStatement.executeQuery();
                while (resultSet.next()) {
                    //facilityList.append("<option value=").append(resultSet.getInt(1)).append(">").append(resultSet.getString(2)).append("</option>");
                    facilityList.append("<option value='" + resultSet.getInt(1) + "~" + resultSet.getInt(3) + "'>" + resultSet.getString(2) + "</option>");
                }
                resultSet.close();
                callableStatement.close();
            }
            //facilityList.append("<option value='999'>Other</option>");
            rset.close();
            cStmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in getFacilityList ", servletContext, Ex, "DAL - TwilioSMSConfiguration", "getFacilityList", connection);
            Services.DumException("TwilioSMSConfiguration", "getFacilityList -- SP -- 004 ", request, Ex, servletContext);

        }
        return facilityList;
    }

    public StringBuilder getFacilityListSelected(HttpServletRequest request, Connection connection, ServletContext servletContext,
                                                 int facilityIdx) {
        cStmt = null;
        rset = null;
        Query = "";
        StringBuilder facilityList = new StringBuilder();
        try {
            Query = "{CALL SP_GET_clientListAdvocateWithoutFac()}";
            cStmt = connection.prepareCall(Query);
            rset = cStmt.executeQuery();
            facilityList.append("<option value='' selected>Select Facility</option>");
            while (rset.next()) {
                if (facilityIdx == rset.getInt(1)) {
                    facilityList.append("<option value='" + rset.getInt(1) + "~" + rset.getInt(3) + "' selected>" + rset.getString(2) + "</option>");
                } else {
                    facilityList.append("<option value='" + rset.getInt(1) + "~" + rset.getInt(3) + "' >" + rset.getString(2) + "</option>");
                }
            }
            rset.close();
            cStmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in getFacilityList ", servletContext, Ex, "DAL - TwilioSMSConfiguration", "getFacilityList", connection);
            Services.DumException("TwilioSMSConfiguration", "getFacilityList -- SP -- 004 ", request, Ex, servletContext);

        }
        return facilityList;
    }

    public StringBuilder getSmsTemplate(HttpServletRequest request, Connection connection, ServletContext servletContext) {
        cStmt = null;
        rset = null;
        Query = "";
        StringBuilder smsTemplate = new StringBuilder();
        try {
            Query = "{CALL SP_GET_smsTemplates()}";
            cStmt = connection.prepareCall(Query);
            rset = cStmt.executeQuery();
            smsTemplate.append("<option value='' selected>Select Template</option>");
            while (rset.next()) {
                smsTemplate.append("<option value=" + rset.getString(1) + ">" + rset.getString(2) + "</option>");
            }
            rset.close();
            cStmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in getSmsTemplate ", servletContext, Ex, "DAL - TwilioSMSConfiguration", "getSmsTemplate", connection);
            Services.DumException("TwilioSMSConfiguration", "getSmsTemplate -- SP -- 005 ", request, Ex, servletContext);

        }
        return smsTemplate;
    }


    public int getMaxSMSIndex(HttpServletRequest request, Connection conn, ServletContext servletContext,
                              int facilityIdx, String dbName) {
        cStmt = null;
        rset = null;
        Query = "";
        int maxIdx = 0;
        try {
            //Query = "{CALL SP_GET_maxSMSInfoIdx(?,?)}";
            Query = "{CALL SP_GET_maxSMSInfoIdxWithoutFacility(?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, dbName);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                maxIdx = rset.getInt(1);
            }
            rset.close();
            cStmt.close();
            System.out.println("MAX ID " + maxIdx);
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in getMaxSMSIndex ", servletContext, Ex, "DAL - TwilioSMSConfiguration", "getMaxSMSIndex", conn);
            Services.DumException("TwilioSMSConfiguration", "getMaxSMSIndex -- SP -- 006 ", request, Ex, servletContext);
            maxIdx = 0;
        }
        return maxIdx;
    }

    public void updateSMSInfoTable(HttpServletRequest request, Connection conn, ServletContext servletContext,
                                   int smsIdx, String SMSSiD, String dbName, String status) {
        cStmt = null;
        rset = null;
        Query = "";

        try {
            Query = "{CALL SP_UPDATE_SMSInfoTableSMSSiD(?,?,?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setInt(1, smsIdx);
            cStmt.setString(2, SMSSiD);
            cStmt.setString(3, dbName);
            cStmt.setString(4, status);
            rset = cStmt.executeQuery();
            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in updateSMSInfoTable ", servletContext, Ex, "DAL - TwilioSMSConfiguration", "updateSMSInfoTable", conn);
            Services.DumException("TwilioSMSConfiguration", "updateSMSInfoTable -- SP -- 007 ", request, Ex, servletContext);
        }
    }

    private void saveSMSResponse(HttpServletRequest request, Connection conn, ServletContext servletContext,
                                 String SMSsID, String SMSBody, String SMSDate, String SMSStatus, int Status,
                                 int CreatedBy, String MRN, int facilityIdx, String dbName) {
        stmt = null;
        rset = null;
        Query = "";

        try {
            Query = "{CALL SP_SAVE_SMSTwilioResponse(?,?,?,?,?,?,?,?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, SMSsID);
            cStmt.setString(2, "test");
            cStmt.setString(3, SMSDate);
            cStmt.setString(4, SMSStatus);
            cStmt.setInt(5, Status);
            cStmt.setInt(6, CreatedBy);
            cStmt.setString(7, MRN);
            cStmt.setInt(8, facilityIdx);
            cStmt.setString(9, dbName);

            rset = cStmt.executeQuery();

            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            System.out.println("Error while inserting in Twilio Response Table " + Ex.getMessage());
            helper.SendEmailWithAttachment("Error in saveSMSResponse ", servletContext, Ex, "DAL - TwilioSMSConfiguration", "saveSMSResponse", conn);
            Services.DumException("TwilioSMSConfiguration", "saveSMSResponse -- SP -- 008 ", request, Ex, servletContext);
        }
    }

    public String[] sendTwilioMessages_roverLab(HttpServletRequest req, Connection conn, ServletContext context,
                                                String smsBody, int facilityIdx, String sendNumber, int UserIdx,
                                                String dbName) {
        cStmt = null;
        rset = null;
        Query = "";
        String SMSSID = "";
        String body = "";
        String status = "";
        String msg = "";
        //int maxSMSIndex = 0;
        try {
            String AuthFactor[] = getTwilioAuthorization(req, conn, context);
            String SenderDetails = "+19724981837";// getSenderDetails(req, conn, context, UserIdx, facilityIdx);

            System.out.println("RoverLab Sending Message....");
//            System.out.println(" FROM --> " + SenderDetails[0]);
            Twilio.init(AuthFactor[0], AuthFactor[1]);
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(sendNumber), // to
                    new com.twilio.type.PhoneNumber(SenderDetails), // from
                    smsBody)
                    .create();
//            System.out.println("After sending the msg");
            SMSSID = message.getSid();
//            System.out.println("First Value --> " + SMSSID);
            body = message.getBody();
//            System.out.println("Second Value --> " + body);
            //returnedDateSMS = message.getDateCreated();
            //System.out.println("Third Value --> " + returnedDateSMS);
            status = String.valueOf(message.getStatus());
//            System.out.println("Fourth Value --> " + status);

//            System.out.println("SMSID " + SMSSID);
//            System.out.println("body " + body);
            //System.out.println("returnedDateSMS " + returnedDateSMS);
//            System.out.println("dbName " + dbName);

/*            if (facilityIdx != 999) {
                maxSMSIndex = getMaxSMSIndex(req, conn, context, facilityIdx, dbName);
                updateSMSInfoTable(req, conn, context, maxSMSIndex, SMSSID, dbName);
                saveSMSResponse(req, conn, context, SMSSID, body, "", status, 0, UserIdx, MRN, facilityIdx, dbName);
            } else {
                maxSMSIndex = getMaxSMSIndex(req, conn, context, "oe");
                updateSMSInfoTable(req, conn, context, maxSMSIndex, SMSSID, "oe");
                saveSMSResponse(req, conn, context, SMSSID, body, "", status, 0, UserIdx, MRN, facilityIdx, "oe");
            }*/
            //maxSMSIndex = getMaxSMSIndex(req, conn, context, facilityIdx, dbName);
            //updateSMSInfoTable(req, conn, context, maxSMSIndex, SMSSID, dbName);
//            saveSMSResponse(req, conn, context, SMSSID, body, "", status, 0, UserIdx, MRN, facilityIdx, dbName);

            System.out.println("RoverLab Message Sent...!");
            msg = "Success";
        } catch (Exception Ex) {
//            try {
//                // maxSMSIndex = getMaxSMSIndex(req, conn, context, facilityIdx, dbName);
///*                Query = "UPDATE oe.SMS_Info SET ReasonFailure = '"+Ex.getMessage()+"' WHERE Id = " + maxSMSIndex;
//                stmt = conn.createStatement();
//                stmt.executeUpdate(Query);
//                stmt.close();*/
////                pStmt = conn.prepareStatement("UPDATE " + dbName + ".PatientReg_SMS_Info SET ReasonFailure = ? WHERE Id = ?");
////                pStmt.setString(1, Ex.getMessage());
////                pStmt.setInt(2, smsIdx);
////                pStmt.executeUpdate();
////                pStmt.close();
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
            System.out.println("EXCEPTION --> " + Ex.getMessage());
            helper.SendEmailWithAttachment("Error in sendTwilioMessages_roverLab ", context, Ex, "DAL - TwilioSMSConfiguration", "sendTwilioMessages_roverLab", conn);
            Services.DumException("TwilioSMSConfiguration", "sendTwilioMessages_roverLab -- SP -- 009 ", req, Ex, context);
            msg = Ex.getMessage();
        }
        return new String[]{msg, SMSSID};
    }
/*    private String[] getPendingSMS(HttpServletRequest request, Connection conn, ServletContext servletContext) {
        cStmt = null;
        rset = null;
        Query = "";
        int maxIdx = 0;
        try {
            Query = "{CALL SP_GET_maxSMSInfoIdx(?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setInt(1, facilityIdx);
            cStmt.setString(2, dbName);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                maxIdx = rset.getInt(1);
            }
            rset.close();
            cStmt.close();
            System.out.println("MAX ID " + maxIdx);
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in getPendingSMS ", servletContext, Ex, "DAL - TwilioSMSConfiguration", "getPendingSMS", conn);
            Services.DumException("TwilioSMSConfiguration", "getPendingSMS -- SP -- 009 ", request, Ex, servletContext);
        }
    }*/
}
