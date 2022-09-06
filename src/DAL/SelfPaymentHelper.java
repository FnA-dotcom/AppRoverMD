package DAL;

import Handheld.UtilityHelper;
import md.Services;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
public class SelfPaymentHelper {
    private CallableStatement cStmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private Statement stmt = null;
    private UtilityHelper helper = new UtilityHelper();

    public int saveInitiateRequestPaymentPortal(HttpServletRequest request, Connection conn, ServletContext servletContext,
                                                String SentEmail, int PatientMRN, int PatientRegIdx,
                                                int webStatus, String CreatedDate, String SentBy, String dbName) {
        cStmt = null;
        rset = null;
        Query = "";
        int Result;
        try {
            String UserIP = helper.getClientIp(request);
            Query = "{CALL SP_SAVE_RequestInitiate(?,?,?,?,?,?,?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, SentEmail);
            cStmt.setInt(2, PatientMRN);
            cStmt.setInt(3, PatientRegIdx);
            cStmt.setInt(4, webStatus);
            cStmt.setString(5, CreatedDate);
            cStmt.setString(6, SentBy);
            cStmt.setString(7, UserIP);
            cStmt.setString(8, dbName);

            rset = cStmt.executeQuery();

            rset.close();
            cStmt.close();

            Result = 1;
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in saveInitiateRequestPaymentPortal ", servletContext, Ex, "DAL - SelfPaymentPortal", "saveInitiateRequestPaymentPortal", conn);
            Services.DumException("SelfPaymentPortal", "saveInitiateRequestPaymentPortal -- SP -- 001 ", request, Ex, servletContext);
            Result = 0;
        }
        return Result;
    }

    public String[] getFacilityDetails(HttpServletRequest request, Connection conn, ServletContext servletContext,
                                       int facilityIndex) {
        cStmt = null;
        rset = null;
        Query = "";
        String dbName = "";
        String facilityName = "";
        try {
            Query = "{CALL SP_GET_getPaymentFacilityDetails(?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setInt(1, facilityIndex);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                dbName = rset.getString(1);
                facilityName = rset.getString(2);
            }
            rset.close();
            cStmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in getFacilityDetails ", servletContext, Ex, "DAL - SelfPaymentPortal", "getFacilityDetails", conn);
            Services.DumException("SelfPaymentPortal", "getFacilityDetails -- SP -- 002", request, Ex, servletContext);
        }
        return new String[]{dbName, facilityName};
    }
}
