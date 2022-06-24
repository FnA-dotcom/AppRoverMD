package Handheld;

import Parsehtm.Parsehtm;
import md.Services;
import org.apache.commons.io.IOUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Properties;

@SuppressWarnings("Duplicates")
public class UtilityHelper extends HttpServlet {

    //***************************************************************************************************************//
    public static void doLogMethodMessage(ServletContext servletcontext, String Method, String Message) {
        try {
            String FileName = GetLogPath(servletcontext) + GetExceptionFileName();
            Date dt = new Date();
            FileWriter fr = new FileWriter(FileName, true);
            fr.write(dt.toString() + " -- " + Method + " -- " + Message + "\r\n");
            fr.write("\r\n");
            fr.flush();
            fr.close();
        } catch (Exception e) {
        }
    }

    private static String GetExceptionFileName() {
        // File Name consist of Date
        // Format YYYY_MM_DD.log
        int temp = 0;

        try {
            Date dt = GetDate();
            NumberFormat nf = new DecimalFormat("#00");
            return nf.format(dt.getYear() + 1900) + "_" + nf.format(dt.getMonth() + 1) + "_" + nf.format(dt.getDate()) + ".log";
        } catch (Exception e) {
            return "invalid filename " + e.getMessage();
        }
    }

    private static String GetLogPath(ServletContext servletContext) {
        return servletContext.getInitParameter("general_messages_path");
    }

    private static Date GetDate() {
        try {
            return new Date();
        } catch (Exception e) {
            return null;
        }
    }

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

    String AppVersion(HttpServletRequest request, Connection conn, ServletContext servletContext) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        String AppVersion = "";

        try {
            Query = "{CALL SP_GET_AppVersion()}";
            cStmt = conn.prepareCall(Query);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                AppVersion = rset.getString(1);
            }
            rset.close();
            cStmt.close();
        } catch (Exception Ex) {
            Services.MobileExceptionDumps("UtilityHelper", "AppVersion -- SP -- 001 ", request, Ex, servletContext);
            AppVersion = "-1";
        }
        return AppVersion;
    }

    String[] getMobileUserInfo(HttpServletRequest request, String UserIdx, String Pwdx, Connection conn, ServletContext servletContext) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";

        int UserCount = 0;
        String UserName = "";
        String RegFormName = "";
        String QRegFormName = "";
        int ClientIndex = 0;

        try {
            Query = "{CALL SP_GET_MobUserCount(?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, UserIdx);
            cStmt.setString(2, Pwdx);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                UserCount = rset.getInt(1);
                UserName = rset.getString(2);
                RegFormName = rset.getString(3);
                QRegFormName = rset.getString(4);
                ClientIndex = rset.getInt(5);
            }
            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "getMobileUserInfo -- SP -- 002 ", request, Ex, servletContext);
        }

        return new String[]{String.valueOf(UserCount), UserName, RegFormName, QRegFormName, String.valueOf(ClientIndex)};

    }

    public String[] getAuthConnect(Connection conn, int FlagType, int ClientId) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        String EndPoint = "";
        String UserName = "";
        String Password = "";
        String MerchantId = "";
        String Currency = "";

        try {
            if (FlagType == 0)
                ClientId = 0;

            Query = "{CALL oe.SP_GET_AuthorizationConnect(?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setInt(1, FlagType);
            cStmt.setInt(2, ClientId);
            boolean hadResults = cStmt.execute();
            rset = cStmt.executeQuery();
            if (rset.next()) {
                EndPoint = rset.getString(1);
                UserName = rset.getString(2);
                Password = rset.getString(3);
                MerchantId = rset.getString(4);
                Currency = rset.getString(5);
            }

/*            System.out.println(" EndPoint " + EndPoint);
            System.out.println(" UserName " + UserName);
            System.out.println(" Password " + Password);
            System.out.println(" MerchantId " + MerchantId);
            System.out.println(" Currency " + Currency);*/

        } catch (Exception Ex) {
            //Services.MobileExceptionDumps("UtilityHelper", "getAuthConnect -- SP -- 003 ", request, Ex, servletContext);
            return new String[]{"Exception Message: " + Ex.getMessage()};
        }
/*        finally {
            if (rset != null) {
                rset.close();
            }
            if (cStmt != null) {
                cStmt.close();
            }
            conn.close();
        }*/
        return new String[]{EndPoint, UserName, Password, MerchantId, Currency};
    }

    public String[] getBoltCredential(Connection conn, int FlagType) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        String Site = "";
        String URL = "";
        String Currency = "";

        try {
            Query = "{CALL SP_GET_MainBoltCredentials(?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setInt(1, FlagType);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                Site = rset.getString(1);
                URL = rset.getString(2);
                Currency = rset.getString(3);
            }
            rset.close();
            cStmt.close();
        } catch (Exception Ex) {
            //Services.MobileExceptionDumps("UtilityHelper", "getAuthConnect -- SP -- 003 ", request, Ex, servletContext);
            return new String[]{Ex.getMessage()};
        }
        return new String[]{Site, URL, Currency};
    }

    public String saveCardConnectResponse(HttpServletRequest request, int SelectedDriverIndex, String UserName, String UserId, String Password,
                                          String Phone, int Status, String CreatedDate, String CreatedBy, String UserType, Connection conn, ServletContext servletContext) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        String Result = "";
        try {
            Query = "{CALL SP_SAVE_MobileUser(?,?,?,?,?,?,?,?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setInt(1, SelectedDriverIndex);
            cStmt.setString(2, UserName);
            cStmt.setString(3, UserId);
            cStmt.setString(4, Password);
            cStmt.setString(5, Phone);
            cStmt.setInt(6, Status);
            cStmt.setString(7, CreatedDate);
            cStmt.setString(8, CreatedBy);
            cStmt.setString(9, UserType);
            rset = cStmt.executeQuery();
            rset.close();
            cStmt.close();

            Result = "Success";

        } catch (Exception Ex) {
            Services.MobileExceptionDumps("UtilityHelper", "saveCardConnectResponse -- SP -- 005 ", request, Ex, servletContext);
            Result = "-1";
        }
        return Result;
    }

    public Object[] getLoginInfo(HttpServletRequest request, String userId, String password, Connection conn, ServletContext servletContext) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        CallableStatement cStmt = null;

        int FoundUser = 0;
        int Found = 0;
        String Enabled = "";
        String UserType = "";
        int FacilityIndex = 0;
        String UserName = "";
        String menu = "";
        String FontColor = "";
        String ClientName = "";
        String DatabaseName = "";
        String DirectoryName = "";
        int UserIndex = 0;
        String PasswordExpiry = "";
        String CurrentDate = "";
        int MaxRetryAllowed = 0;
        int PRetry = 0;
        int LoginCount = 0;
        int PChangeDate = 0;
        int LocationIdx = 0;
        int isLocationAdmin = 0;
        int isPwdChanged = 0;
        try {
            /*
            Query = " Select COUNT(*), IFNULL(DATE_FORMAT(password_expiry,'%Y-%m-%d'),''), DATE_FORMAT(NOW(),'%Y-%m-%d'), IFNULL(MaxRetryAllowed,'5'), " +
                    " IFNULL(PRetry,'0'), IFNULL(LoginCount,0), " +
                    " IFNULL(datediff(DATE_FORMAT(NOW(),'%Y-%m-%d %T'),IFNULL(DATE_FORMAT(PChangeDate,'%Y-%m-%d %T'),'0000-00-00')),0), " +
                    " indexptr, LocationIdx  " +
                    " from oe.sysusers where ltrim(rtrim(UPPER(userid))) = ltrim(rtrim(UPPER('" + userId + "'))) ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundUser = rset.getInt(1);
                //PasswordExpiry = rset.getString(2);
                //CurrentDate = rset.getString(3);
                //MaxRetryAllowed = rset.getInt(4);
                //PRetry = rset.getInt(5);
                //LoginCount = rset.getInt(6);
                //PChangeDate = rset.getInt(7);
                //UserIndex = rset.getInt(8);
                //LocationIdx = rset.getInt(9);
            }
            rset.close();
            stmt.close();*/

            Query = "{CALL SP_GET_LoginInfo(?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, userId);
            cStmt.setString(2, password);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                Found = rset.getInt(1);
                UserType = rset.getString(2);
                Enabled = rset.getString(3);
                UserName = rset.getString(4);
                FacilityIndex = rset.getInt(5);
                menu = rset.getString(6);
                FontColor = rset.getString(7);
                ClientName = rset.getString(8);
                DatabaseName = rset.getString(9);
                DirectoryName = rset.getString(10);
                UserIndex = rset.getInt(12);
                PasswordExpiry = rset.getString(13);
                CurrentDate = rset.getString(14);
                MaxRetryAllowed = rset.getInt(15);
                PRetry = rset.getInt(16);
                LoginCount = rset.getInt(17);
                PChangeDate = rset.getInt(18);
                LocationIdx = rset.getInt(19);
                isLocationAdmin = rset.getInt(20);
                isPwdChanged = rset.getInt(21);
            }
            rset.close();
            cStmt.close();
        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "getLoginInfo -- SP -- 006 ", request, Ex, servletContext);
        }
        return new Object[]{Found, UserType, Enabled, UserName, FacilityIndex, menu, FontColor, ClientName,
                DatabaseName, DirectoryName, FoundUser, PasswordExpiry, CurrentDate, UserIndex,
                MaxRetryAllowed, PRetry, LoginCount, PChangeDate, LocationIdx, isLocationAdmin, isPwdChanged};
    }

    public boolean CheckUserId(final HttpServletRequest request, final String userId, final Connection conn, final ServletContext servletContext, String i) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int userChk = 0;
        boolean Found = false;
        try {
            Query = "Select COUNT(*) from oe.sysusers where ltrim(rtrim(UPPER(userid))) = ltrim(rtrim(UPPER('" + userId + "'))) AND usertype IN (" + i + ")";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                userChk = rset.getInt(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "CheckUserId -- NO SP --  ", request, Ex, servletContext);
        }
        Found = userChk > 0;

        return Found;
    }

    public boolean CheckPwd(final HttpServletRequest request, final String userId, final Connection conn, final ServletContext servletContext, String Password) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PwdChk = 0;
        boolean Found = false;
        try {
            Query = "Select COUNT(*) from oe.sysusers where ltrim(rtrim(UPPER(userid))) = ltrim(rtrim(UPPER('" + userId + "'))) and " +
                    "password = '" + Password + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PwdChk = rset.getInt(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "CheckPwd -- NO SP --  ", request, Ex, servletContext);
        }
        Found = PwdChk > 0;

        return Found;
    }

    public Object[] UpdatePRetry(final HttpServletRequest request, final String userId, final Connection conn, final ServletContext servletContext, String Password) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        int MaxRetryAllowed = 0;
        int PRetry = 0;
        int LoginCount = 0;
        try {
            stmt = conn.createStatement();
            Query = "Update oe.sysusers set PRetry = PRetry + 1 where ltrim(rtrim(UPPER(userid))) = ltrim(rtrim(UPPER('" + userId + "'))) ";
            stmt.execute(Query);

            Query = " insert into wrong_password_attempts " +
                    " (userid, password, entrydate, ipaddress, sourcetype, sourcedesc) " +
                    " values ('" + userId + "','" + Password + "',now(),'" + request.getRemoteAddr() + "',1,'Sys User')";

            stmt = conn.createStatement();
            stmt.executeUpdate(Query);

            Query = "Select IFNULL(MaxRetryAllowed,'5'), IFNULL(PRetry,'0'), IFNULL(LoginCount,0)  " +
                    "from oe.sysusers where ltrim(rtrim(UPPER(userid))) = ltrim(rtrim(UPPER('" + userId + "'))) ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                MaxRetryAllowed = rset.getInt(1);
                PRetry = rset.getInt(2);
                LoginCount = rset.getInt(3);
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "UpdatePRetry -- NO SP --  ", request, Ex, servletContext);
        }
        return new Object[]{MaxRetryAllowed, PRetry, LoginCount};
    }

    public void UpdateEnabledCol(final HttpServletRequest request, final String userId, final Connection conn, final ServletContext servletContext) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        try {
            stmt = conn.createStatement();
            Query = "Update oe.sysusers set enabled = 'N'  where ltrim(rtrim(UPPER(userid))) = ltrim(rtrim(UPPER('" + userId + "'))) ";
            stmt.execute(Query);
        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "UpdateEnabledCol -- NO SP --  ", request, Ex, servletContext);
        }
    }

    public void UpdateLoginCount(final HttpServletRequest request, final String userId, final Connection conn, final ServletContext servletContext) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        try {
            stmt = conn.createStatement();
            Query = "Update oe.sysusers set LoginCount = LoginCount + 1  where ltrim(rtrim(UPPER(userid))) = ltrim(rtrim(UPPER('" + userId + "'))) ";
            stmt.execute(Query);
        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "UpdateLoginCount -- NO SP --  ", request, Ex, servletContext);
        }
    }

    public int getNoOfTries_WrongAttempts(HttpServletRequest request, String UserId, Connection conn, ServletContext servletContext) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        int NoOfTries = 0;
        try {
            Query = "{CALL oe.SP_GET_noOfTries(?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, UserId);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                NoOfTries = rset.getInt(1);
            }
            rset.close();
            cStmt.close();
        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "getNoOfTries_WrongAttempts -- SP -- 008 ", request, Ex, servletContext);
            NoOfTries = -1;
        }
        return NoOfTries;
    }

    public String getCurrDate(HttpServletRequest request, Connection conn) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";

        String CurrDate = "";
        Query = "{CALL CurrentDate()}";
        try {
            cStmt = conn.prepareCall(Query);
            rset = cStmt.executeQuery();
            if (rset.next())
                CurrDate = rset.getString(1).trim();
            rset.close();
            cStmt.close();
        } catch (SQLException Ex) {
            Services.DumException("UtilityHelper", "getCurrDate -- SP -- 009 ", request, Ex, this.getServletContext());
            CurrDate = "-1";
        }
        return CurrDate;
    }

/*    public void updateUserPassword(HttpServletRequest request, Connection conn, int FacilityIndex, String updatedPassword, ServletContext servletContext) {
        cStmt = null;
        rset = null;
        Query = "";

        try {
            Query = "{CALL SP_UPDATE_PasswordUsers(?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setInt(1, FacilityIndex);
            cStmt.setString(2, updatedPassword);
            rset = cStmt.executeQuery();
            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "updateUserPassword -- SP -- 012 ", request, Ex, this.getServletContext());
        }
    }*/

    public void updateWrongEntriesNoOfTries(HttpServletRequest request, Connection conn, String userId, ServletContext servletContext) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";

        try {
            Query = "{CALL oe.SP_UPDATE_NoOfTriesWrongAttempted(?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, userId);
            rset = cStmt.executeQuery();
            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "updateWrongEntriesNoOfTries -- SP -- 010 ", request, Ex, this.getServletContext());
        }
    }

    public String[] loginUserDetails(HttpServletRequest request, Connection conn, String UserId, ServletContext servletContext) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        String UserName = "";
        String UserIdx = "";
        int FacilityIndex = 0;
        String ClientName = "";
        String CurrPassword = "";
        String menu = "";

        try {
            Query = "{CALL SP_GET_LoginUserDetails(?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, UserId);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                UserName = rset.getString(1);
                UserIdx = rset.getString(2);
                FacilityIndex = rset.getInt(3);
                ClientName = rset.getString(4);
                CurrPassword = rset.getString(5);
                menu = rset.getString(6);
            }
            rset.close();
            cStmt.close();
        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "loginUserDetails -- SP -- 011 ", request, Ex, servletContext);
            return new String[]{Ex.getMessage()};
        }
        return new String[]{UserName, UserIdx, String.valueOf(FacilityIndex), ClientName, CurrPassword, menu};
    }

    public void captureWebLogActivity(HttpServletRequest request, String userId, int FacilityIndex, String WebAction, Connection conn, ServletContext servletContext) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";

        try {
            String UserIP = getClientIp(request);
            String CurrDate = getCurrDate(request, conn);
            Query = "{CALL SP_SAVE_captureWebLogActivity(?,?,?,?,?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, userId);
            cStmt.setInt(2, FacilityIndex);
            cStmt.setString(3, WebAction);
            cStmt.setString(4, UserIP);
            cStmt.setInt(5, 0);
            cStmt.setString(6, CurrDate);

            rset = cStmt.executeQuery();

            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "captureWebLogActivity -- SP -- 013 ", request, Ex, servletContext);
        }
    }

    public boolean checkUserSession(HttpServletRequest request, String userId, int FacilityIndex, Connection conn, ServletContext servletContext) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        int sessionCheck = 0;
        try {

            Query = "{CALL SP_GET_UserCheckSession(?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setInt(1, FacilityIndex);
            cStmt.setString(2, userId);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                sessionCheck = rset.getInt(1);
            }
            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "checkUserSession -- SP -- 014 ", request, Ex, servletContext);
        }
        return sessionCheck > 0;
    }

    public void saveUserSession(HttpServletRequest request, String userId, int FacilityIndex, String sessionId, Connection conn, ServletContext servletContext) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";

        try {
            String UserIP = getClientIp(request);
            Query = "{CALL SP_SAVE_SessionUsers(?,?,?,?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, userId);
            cStmt.setInt(2, FacilityIndex);
            cStmt.setString(3, UserIP);
            cStmt.setInt(4, 0);
            cStmt.setString(5, sessionId);

            rset = cStmt.executeQuery();

            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "saveUserSession -- SP -- 015 ", request, Ex, servletContext);
        }
    }

    public void deleteUserSession(HttpServletRequest request, Connection conn, String sessionId) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";

        try {
            Query = "{CALL SP_DELETE_UserSessionLogs(?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, sessionId);
            rset = cStmt.executeQuery();
            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "deleteUserSession -- SP -- 016 ", request, Ex, this.getServletContext());
        }
    }

    public boolean checkBySessionId(HttpServletRequest request, Connection conn, String sessionId) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        int sessionCheck = 0;

        try {
            Query = "{CALL SP_GET_CheckBySession(?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, sessionId);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                sessionCheck = rset.getInt(1);
            }
            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "checkBySessionId -- SP -- 017 ", request, Ex, this.getServletContext());
        }
        return sessionCheck > 0;
    }

    public boolean checkSessionByLogInId(HttpServletRequest request, Connection conn, String UserId) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        int sessionCheck = 0;

        try {
            Query = "{CALL SP_GET_SessionCheckbyUserId(?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, UserId);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                sessionCheck = rset.getInt(1);
            }
            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "checkSessionByLogInId -- SP -- 018 ", request, Ex, this.getServletContext());
        }
        return sessionCheck > 0;
    }

    public void deleteUserSessionByUserId(HttpServletRequest request, Connection conn, String userId) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";

        try {
            Query = "{CALL SP_DELETE_UserSessionLogsByUserId(?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, userId);
            rset = cStmt.executeQuery();
            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "deleteUserSessionByUserId -- SP -- 019 ", request, Ex, this.getServletContext());
        }
    }

    public StringBuilder showTransactionReport(HttpServletRequest request, ServletContext servletContext, Connection conn, String fromDate, String endDate, int clientIndex, String dataBaseName) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuilder TransactionReport = new StringBuilder();
        int SrlNo = 1;

        try {
            Query = "{CALL SP_GET_TransactionDataDateWise(?,?,?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, fromDate);
            cStmt.setString(2, endDate);
            cStmt.setInt(3, clientIndex);
            cStmt.setString(4, dataBaseName);
            rset = cStmt.executeQuery();
            while (rset.next()) {
                TransactionReport.append("<tr>");
                TransactionReport.append("<td width=02%>" + SrlNo + "</td>");

                TransactionReport.append("<td width=08%>" + rset.getString(1) + "</td>");//NAME
//                TransactionReport.append("<td width=08%>" + rset.getString(2) + "</td>");//DOB
//                TransactionReport.append("<td width=08%>" + rset.getString(3) + "</td>");//Gender
                TransactionReport.append("<td width=08%>" + rset.getString(4) + "</td>");//InvoiceNo
                TransactionReport.append("<td width=08%>" + rset.getDouble(5) + "</td>");//TotalAmount
                TransactionReport.append("<td width=08%>" + rset.getDouble(6) + "</td>");//PaidAmount
//                TransactionReport.append("<td width=08%>" + rset.getDouble(7) + "</td>");//Paid
                TransactionReport.append("<td width=08%>" + rset.getDouble(8) + "</td>");//BalAmount
                TransactionReport.append("<td width=08%>" + rset.getString(9) + "</td>");//Remarks
                TransactionReport.append("<td width=08%>" + rset.getString(10) + "</td>");//ResponseText
                TransactionReport.append("<td width=08%>" + rset.getString(11) + "</td>");//ResponseStatus
                TransactionReport.append("<td width=08%>" + rset.getString(12) + "</td>");//RetRef
                TransactionReport.append("<td width=08%>" + rset.getString(13) + "</td>");//RetDate
                TransactionReport.append("<td width=42%> ");
                TransactionReport.append("<button id=saveStdBtn onclick=\"setRunIndex(this.value)\" class=\"btn btn-primary btn-sm\" data-toggle=\"modal\" data-target=\"#myModal\" value=" + rset.getString(10) + "><i class=\"fa fa-plus\"></i> [Refund] </button>&nbsp;&nbsp;");
                TransactionReport.append("<button id=deleteBtn onclick=\"addFields(this.value)\" class=\"btn btn-danger btn-sm myLink\" value=" + rset.getString(10) + " target=NewFrame1> <font color = \"FFFFFF\"> <i class=\"fa fa-trash-o\"></i> [Void] </font></button>&nbsp;&nbsp;");

                TransactionReport.append("</td>");
                TransactionReport.append("</tr>");
                ++SrlNo;
            }
            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "showTransactionReport -- SP -- 020 ", request, Ex, servletContext);
        }
        return TransactionReport;
    }

    public boolean checkingTransactionCredentials(HttpServletRequest request, ServletContext servletContext, Connection conn, String password) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        int found = 0;
        try {
            Query = "{CALL SP_GET_TransactionPasswords(?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, password);
            rset = cStmt.executeQuery();
            if (rset.next())
                found = rset.getInt(1);
            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "checkingTransactionCredentials -- SP -- 021 ", request, Ex, servletContext);
        }
        return found > 0;
    }

    public int SendEmail(String eSection, String eSubject, String eBody, String Email, Connection conn, ServletContext servletContext) {
        String HostName = "";
        String EmailUserId = "";
        String EmailPassword = "";
        String SMTP = "";
        String Port = "";
        String Authentication = "";
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        String emailHtmlFilePath = Services.GetEmailFilePath(servletContext);
        try {
            Query = "{CALL SP_GET_CredentialsEmail()}";
            cStmt = conn.prepareCall(Query);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                HostName = rset.getString(1);
                EmailUserId = rset.getString(2);
                EmailPassword = rset.getString(3);
                SMTP = rset.getString(4);
                Port = rset.getString(5);
                Authentication = rset.getString(6);
            }
            rset.close();
            cStmt.close();

/*            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", HostName);
            props.put("mail.smtp.port", Port);
            props.put("mail.smtp.auth", "true");*/

            System.out.println("*****************************************");
            System.out.println("HostName " + HostName);
            System.out.println("EmailUserId " + EmailUserId);
            System.out.println("EmailPassword " + EmailPassword);
            System.out.println("SMTP " + SMTP);
            System.out.println("Port " + Port);
            System.out.println("Authentication " + Authentication);
            System.out.println("*****************************************");

            System.out.println("Sending an Email....");
            Properties props = new Properties();
            props.put("mail.transport.protocol", SMTP);
            props.put("mail.smtp.host", HostName);
            props.put("mail.smtp.port", Port);
            props.put("mail.smtp.auth", Authentication);
            final String user = EmailUserId;//change accordingly
            final String password = EmailPassword;//change accordingly
            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user, password);
                        }
                    });
            //session.setDebug(true);
            MimeMessage message = new MimeMessage(session);
            message.setContent(eBody, "text/html");

            message.setFrom(new InternetAddress("App Rover <no-reply@rovermd.com>"));
            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(Email));
            message.setSentDate(new Date());
            // Set Subject: header field
            message.setSubject(eSubject);
            //Setting the email priority high
            //message.addHeader("X-Priority", "1");

            Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transport.close();
            System.out.println("1");
        } catch (Exception var18) {
            System.out.println("Error while Generating Email!!!");
            System.out.println(var18.getMessage());
        }
        return 1;
    }
/*    public int SendEmail(String eSection, String eSubject, String eBody, String Email) {
        //String Email1 = "alert@rovermd.com";
        String SMTP_HOST_NAME = "smtp.ionos.com";
        String Port = "587";
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.port", Port);
        props.put("mail.smtp.auth", "true");
        try {

            //Sending a HTML file in Email's
            StringWriter writer = new StringWriter();
            IOUtils.copy(new FileInputStream(new File("/sftpdrive/opt/Htmls/md/EmailFormats/EmailFormatter.html")), writer);

            Authenticator auth = new SMTPAuthenticator();
            Session mailSession = Session.getInstance(props, auth);
            //mailSession.setDebug(true);
            Transport transport = mailSession.getTransport();

            MimeMessage message = new MimeMessage(mailSession);
            //message.setContent(Body, "text/html");
            message.setContent(writer.toString(), "text/html");
            message.setSubject(eSubject);
            message.setFrom(new InternetAddress("App Rover <no-reply@rovermd.com>"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(Email));
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(Email2));
            message.setSentDate(new Date());
            transport.connect();
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transport.close();
            System.out.println("1");
        } catch (Exception var18) {
            System.out.println("Error while Generating Email!!!");
            System.out.println(var18.getMessage());
        }

        return 1;
    }*/

    public int SendEmailRoverLab(String eSection, String eSubject, String link, String Email, Connection conn, ServletContext servletContext, int MRN, String name, String facilityIndex) {
        String HostName = "";
        String EmailUserId = "";
        String EmailPassword = "";
        String SMTP = "";
        String Port = "";
        String Authentication = "";
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        String emailHtmlFilePath = Services.GetEmailFilePath(servletContext);
        try {
            Query = "{CALL SP_GET_CredentialsEmail()}";
            cStmt = conn.prepareCall(Query);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                HostName = rset.getString(1);
                EmailUserId = rset.getString(2);
                EmailPassword = rset.getString(3);
                SMTP = rset.getString(4);
                Port = rset.getString(5);
                Authentication = rset.getString(6);
            }
            rset.close();
            cStmt.close();

/*            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", HostName);
            props.put("mail.smtp.port", Port);
            props.put("mail.smtp.auth", "true");*/

            System.out.println("*****************************************");
            System.out.println("HostName " + HostName);
            System.out.println("EmailUserId " + EmailUserId);
            System.out.println("EmailPassword " + EmailPassword);
            System.out.println("SMTP " + SMTP);
            System.out.println("Port " + Port);
            System.out.println("Authentication " + Authentication);
            System.out.println("*****************************************");

            System.out.println("Sending an Email....");
            Properties props = new Properties();
            props.put("mail.transport.protocol", SMTP);
            props.put("mail.smtp.host", HostName);
            props.put("mail.smtp.port", Port);
            props.put("mail.smtp.auth", Authentication);
            final String user = EmailUserId;//change accordingly
            final String password = EmailPassword;//change accordingly
            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user, password);
                        }
                    });
            //session.setDebug(true);
            System.out.println("MRN " + MRN);
            StringWriter writer = new StringWriter();
            //
            IOUtils.copy(new FileInputStream(new File(emailHtmlFilePath + "LabRegistration.html")), writer);
            //writer.toString().replace("mrn",String.valueOf(MRN));
            System.out.println("LINK *** " + link);

            MimeMessage message = new MimeMessage(session);
            //message.setContent(eBody, "text/html");
            message.setContent(writer.toString().replace("mrn", String.valueOf(MRN) + ":" + facilityIndex), "text/html");

            message.setFrom(new InternetAddress("App Rover <no-reply@rovermd.com>"));
            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(Email));
            message.setSentDate(new Date());
            // Set Subject: header field
            message.setSubject(eSubject);
            //Setting the email priority high
            //message.addHeader("X-Priority", "1");

            Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transport.close();
            System.out.println("1");
        } catch (Exception var18) {
            System.out.println("Error while Generating Email!!!");
            System.out.println(var18.getMessage());
        }
        return 1;
    }

    public int SendEmailOLD(String eSection, String eSubject, String eBody, String Email, String facilityIndex) {
//        String Email1 = "m.mehmood@fam-llc.com";
//        String Email1 = "alert@rovermd.com";
        String SMTP_HOST_NAME = "smtp.ionos.com";
        String Port = "587";
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.port", Port);
        props.put("mail.smtp.auth", "true");
        try {
            System.out.println("Sending an Email....");
            //Sending a HTML file in Email's
//            StringWriter writer = new StringWriter();
//            IOUtils.copy(new FileInputStream(new File("/sftpdrive/opt/Htmls/md/EmailFormats/EmailFormatter.html")), writer);

            Authenticator auth = new SMTPAuthenticator();
            Session mailSession = Session.getInstance(props, auth);
            mailSession.setDebug(true);
            Transport transport = mailSession.getTransport();

            MimeMessage message = new MimeMessage(mailSession);
            //message.setContent(Body, "text/html");
            message.setContent(eBody, "text/html");
            message.setSubject(eSubject);
            // message.setFrom(new InternetAddress("App Rover <tabish.hafeez@fam-llc.com>"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(Email));
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(Email2));
            message.setSentDate(new Date());
            transport.connect();
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transport.close();
            System.out.println("1");
        } catch (Exception var18) {
            System.out.println("Error while Generating Email!!!");
            System.out.println(var18.getMessage());
            return 0;
        }

        return 1;
    }

    public void init() {

    }

    public String[] MasterConfig(Connection conn, HttpServletRequest request, ServletContext context) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String exp_period = "";
        String wrong_pass = "";
        String pass_history = "";
        String enforced_pass = "";
        String app_ppolicy = "";
        String exp_periodv = "";
        String enforced_passv = "";
        String app_ppolicyv = "";
        try {
            hstmt = conn.createStatement();
            Query = " select exp_period,wrong_pass,pass_history,enforced_pass,app_ppolicy," +
                    "entrydate from oe.SecurityMaster " +
                    "order by entrydate desc limit 1";
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                exp_period = hrset.getString(1);
                wrong_pass = hrset.getString(2);
                pass_history = hrset.getString(3);
                enforced_pass = hrset.getString(4);
                app_ppolicy = hrset.getString(5);
            }
            hrset.close();
            String ar[] = new String[5];
            ar[0] = exp_period;
            ar[1] = wrong_pass;
            ar[2] = pass_history;
            ar[3] = enforced_pass;
            ar[4] = app_ppolicy;
            return ar; //returning two values at once

        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "MasterConfig -- SP -- 010 ", request, Ex, context);
        }
        return null;
    }

    public void saveWrongPasswordAttempts(final HttpServletRequest request, final String userId, final String password, final int FacilityIndex, final int NoOfTries, final Connection conn, final ServletContext servletContext) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        try {
            String UserIP = getClientIp(request);
            String CurrDate = getCurrDate(request, conn);
            Query = "{CALL oe.SP_SAVE_WrongPasswordAttempts(?,?,?,?,?,?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, userId);
            cStmt.setString(2, password);
            cStmt.setInt(3, NoOfTries);
            cStmt.setString(4, UserIP);
            cStmt.setInt(5, FacilityIndex);
            cStmt.setInt(6, 0);
            cStmt.setString(7, CurrDate);
            rset = cStmt.executeQuery();

            rset.close();
            cStmt.close();
        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "saveWrongPasswordAttempts -- SP -- 007 ", request, Ex, servletContext);
        }
    }

    public int SendEmailWithAttachment(String eSubject, ServletContext servletContext, Exception exp, String ClassName, String FuncName, Connection conn) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        //String Email1 = "tabish.hafeez@fam-llc.com";//change accordingly
        String FilePath = Services.GetEmailLogsPath(servletContext);
        String emailHtmlFilePath = Services.GetEmailFilePath(servletContext);
        try {
            String HostName = "";
            String EmailUserId = "";
            String EmailPassword = "";
            String SMTP = "";
            String Port = "";
            String Authentication = "";
            String EmailTo = null;
            try {
                if (conn == null) {
                    conn = Services.getMysqlConn(servletContext);
                }
                Query = "{CALL SP_GET_CredentialsEmail()}";
                cStmt = conn != null ? conn.prepareCall(Query) : (CallableStatement) Services.getMysqlConn(servletContext);
                rset = cStmt != null ? cStmt.executeQuery() : null;
                if (rset != null && rset.next()) {
                    HostName = rset.getString(1);
                    EmailUserId = rset.getString(2);
                    EmailPassword = rset.getString(3);
                    SMTP = rset.getString(4);
                    Port = rset.getString(5);
                    Authentication = rset.getString(6);
                    EmailTo = rset.getString(7);
                }
                if (rset != null) {
                    rset.close();
                }
                if (cStmt != null) {
                    cStmt.close();
                }
            } catch (Exception Ex) {
                Ex.printStackTrace();
            }
            System.out.println("*****************************************");
            System.out.println("HostName " + HostName);
            System.out.println("EmailUserId " + EmailUserId);
            System.out.println("EmailPassword " + EmailPassword);
            System.out.println("SMTP " + SMTP);
            System.out.println("Port " + Port);
            System.out.println("Authentication " + Authentication);


            System.out.println("*****************************************");
            //1) get the session object

            Properties props = new Properties();
            props.put("mail.transport.protocol", SMTP);
            props.put("mail.smtp.host", HostName);
            props.put("mail.smtp.port", Port);
            props.put("mail.smtp.auth", Authentication);
            final String user = EmailUserId;//change accordingly
            final String password = EmailPassword;//change accordingly
            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user, password);
                        }
                    });


            //session.setDebug(true);
            //Sending a HTML file in Email's
            StringWriter writer = new StringWriter();
            //IOUtils.copy(new FileInputStream(new File("F://EmailFormatter.html")), writer);
            IOUtils.copy(new FileInputStream(new File(emailHtmlFilePath + "EmailFormatter.html")), writer);
//            IOUtils.copy(new FileInputStream(new File("/sftpdrive/opt/Htmls/md/EmailFormats/EmailFormatter.html")), writer);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("App Rover <no-reply@rovermd.com>"));
            // Set To: header field of the header.
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress((EmailTo == null ? "tabish.hafeez@fam-llc.com" : EmailTo.equals("") ? "tabish.hafeez@fam-llc.com" : EmailTo)));
            EmailTo = (EmailTo == null ? "tabish.hafeez@fam-llc.com" : EmailTo.equals("") ? "tabish.hafeez@fam-llc.com" : EmailTo);
            System.out.println("EMAIL ADDRESS FROM UH --> " + EmailTo);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(EmailTo));
            // Set Subject: header field
            message.setSubject(eSubject);
            //Setting the email priority high
            message.addHeader("X-Priority", "1");

            Transport t = session.getTransport("smtp");
            t.connect();

            // attachement
            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            BodyPart attachmentBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(writer.toString(), "text/html"); // 5

            multipart.addBodyPart(messageBodyPart);
//session.setDebug(true);
            /************* MY PART *************/
            String FileName = "";
            try {
                FileName = FilePath + "myLogs.txt";
                //FileName = "/sftpdrive/opt/Htmls/md/logs/EmailLogs/myLogs.log";
                final FileWriter fr = new FileWriter(FileName, true);
                String str = "";
                for (int i = 0; i < exp.getStackTrace().length; ++i) {
                    str = String.valueOf(str) + exp.getStackTrace()[i] + "<br>";
                }
                //fr.write(new Date().toString() + "^" + ClassName + "^" + FuncName + "^" + exp.getMessage() + str + "\r\n");
                fr.write(new Date().toString() + "^" + ClassName + "^" + FuncName + "^" + exp.getMessage() + "\r\n");
                final PrintWriter pr = new PrintWriter(fr, true);
                exp.printStackTrace(pr);
                fr.write("\r\n");
                fr.flush();
                fr.close();
                pr.close();
            } catch (Exception ex) {
            }

            /**********************************/

            String fName = FilePath + "myLogs.txt";//change accordingly
            //String fName = "/sftpdrive/opt/Htmls/md/logs/EmailLogs/myLogs.log";//change accordingly
            // file path
            File filename = new File(fName);

            DataSource source = new FileDataSource(filename);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(filename.getName());
            multipart.addBodyPart(attachmentBodyPart);
            message.setContent(multipart);

            Transport.send(message);

            //Delete the file
            Files.deleteIfExists(Paths.get(fName));
            System.out.println("Email Sent..");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int SendEmail_RequestReport(Connection conn, String eSubject, String eBody) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";

        String HostName = "";
        String EmailUserId = "";
        String EmailPassword = "";
        String SMTP = "";
        String Port = "";
        String Authentication = "";
        String EmailTo = "";
        try {
            Query = "{CALL SP_GET_CredentialsEmail()}";
            cStmt = conn.prepareCall(Query);
            rset = cStmt.executeQuery();
            if (rset.next()) {
                HostName = rset.getString(1);
                EmailUserId = rset.getString(2);
                EmailPassword = rset.getString(3);
                SMTP = rset.getString(4);
                Port = rset.getString(5);
                Authentication = rset.getString(6);
                EmailTo = rset.getString(7);
            }
            rset.close();
            cStmt.close();

        } catch (Exception Ex) {
            Ex.printStackTrace();
        }


        String SMTP_HOST_NAME = "smtp.ionos.com";
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.port", Port);
        props.put("mail.smtp.auth", "true");


        try {
            //Sending a HTML file in Email's
//            StringWriter writer = new StringWriter();
//            IOUtils.copy(new FileInputStream(new File("/sftpdrive/opt/Htmls/md/EmailFormats/EmailFormatter.html")), writer);

            Authenticator auth = new SMTPAuthenticator();
            Session mailSession = Session.getInstance(props, auth);
            //mailSession.setDebug(true);
            Transport transport = mailSession.getTransport();

            MimeMessage message = new MimeMessage(mailSession);
            //message.setContent(Body, "text/html");
            message.setContent(eBody, "text/html");
            message.setSubject(eSubject);
            message.setFrom(new InternetAddress("App Rover <no-reply@rovermd.com>"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(EmailTo));
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(Email2));
            message.setSentDate(new Date());
            transport.connect();
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transport.close();
            System.out.println("1");
        } catch (Exception var18) {
            System.out.println("Error while Generating Email!!!");
            System.out.println(var18.getMessage());
        }

        return 1;
    }

    public String[] getPatientInfo(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String PatientMRN) {
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
            SendEmailWithAttachment("Error in getPatientInfo ", servletContext, Ex, "Handheld - UtilityHelper", "getPatientInfo", conn);
            Services.DumException("Handheld - Payments", "getPatientInfo", request, Ex, getServletContext());
        }
        return new String[]{FirstName, LastName, Email, City, State, ZipCode, Country, PhNumber, Address,
                Title, MiddleInitial, MaritalStatus, MRN, DOB, Age, Gender, SSN, Occupation, Employer, EmpContact,
                PriCarePhy, ReasonVisit, SelfPayChk, CreatedDate, Address2};
    }

    public int getPatientRegMRN(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, int PatientRegIdx) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientMRN = 0;

        try {
            Query = "SELECT MRN FROM " + database + ".PatientReg WHERE Id = " + PatientRegIdx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                PatientMRN = rset.getInt(1);
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            SendEmailWithAttachment("Error in PatientRegIdx ", servletContext, Ex, "Handheld - UtilityHelper", "PatientRegIdx", conn);
            Services.DumException("Handheld - Payments", "PatientRegIdx", request, Ex, getServletContext());
        }
        return PatientMRN;
    }

    public void updatePatientRegDate(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, int PatientRegIdx) {
        Statement stmt = null;
        String Query = "";

        try {
            Query = "UPDATE " + database + ".PatientReg SET ViewDate = NOW() WHERE Id = " + PatientRegIdx;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

        } catch (Exception Ex) {
            SendEmailWithAttachment("Error in updatePatientRegDate ", servletContext, Ex, "Handheld - UtilityHelper", "updatePatientRegDate", conn);
            Services.DumException("UtilityHelper", "updatePatientRegDate -- " + database + " ", request, Ex, this.getServletContext());
        }
    }

    public String getFacilityName(HttpServletRequest request, Connection conn, ServletContext servletContext, int facilityIndex) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String FacilityName = "";

        try {
            Query = "SELECT name from oe.clients WHERE Id = " + facilityIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                FacilityName = rset.getString(1);
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            SendEmailWithAttachment("Error in getFacilityName ", servletContext, Ex, "Handheld - UtilityHelper", "getFacilityName", conn);
            Services.DumException("Handheld - UtilityHelper", "getFacilityName", request, Ex, getServletContext());
        }
        return FacilityName;
    }

    public String[] printDateTime(HttpServletRequest request, Connection conn, ServletContext servletContext) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String printDate = "";
        String printTime = "";

        try {
            Query = "SELECT  DATE_FORMAT(NOW(),'%m/%d/%Y'), DATE_FORMAT(NOW(),'%h:%i:%s %p')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                printDate = rset.getString(1);
                printTime = rset.getString(2);

            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            SendEmailWithAttachment("Error in printDateTime ", servletContext, Ex, "Handheld - UtilityHelper", "printDateTime", conn);
            Services.DumException("Handheld - Payments", "printDateTime", request, Ex, getServletContext());
        }
        return new String[]{printDate, printTime};
    }

    public String getReceiptCounter(HttpServletRequest request, Connection conn, ServletContext servletContext, String databaseName) {
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
            SendEmailWithAttachment("Error in printDateTime ", servletContext, Ex, "Handheld - UtilityHelper", "printDateTime", conn);
            Services.DumException("Handheld - Payments", "printDateTime", request, Ex, getServletContext());
        }
        return receiptCounter;
    }

    public void saveRequestTable(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, String msg, String PatientMRN, int ClientId) {
        PreparedStatement pStmt = null;

        try {
            pStmt = conn.prepareStatement(
                    "INSERT INTO oe.request (msg, requestdate, RequestType, mrn, ClientIndex,CreatedBy,CreatedDate) VALUES (?,NOW(),1,?,?,?,NOW()) ");
            pStmt.setString(1, msg);
            pStmt.setString(2, String.valueOf(PatientMRN));
            pStmt.setInt(3, ClientId);
            pStmt.executeUpdate();
            pStmt.close();
        } catch (Exception Ex) {
            SendEmailWithAttachment("Error in saveRequestTable ", servletContext, Ex, "Handheld - UtilityHelper", "saveRequestTable", conn);
            Services.DumException("Handheld - UtilityHelper", "saveRequestTable", request, Ex, getServletContext());
        }
    }

    public String[] receiptClientData(HttpServletRequest request, Connection conn, ServletContext servletContext, int facilityIndex) {
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
            SendEmailWithAttachment("Error in receiptClientData ", servletContext, Ex, "Handheld - UtilityHelper", "receiptClientData", conn);
            Services.DumException("Handheld - Payments", "receiptClientData", request, Ex, getServletContext());
        }
        return new String[]{FullName, Address, Phone};
    }

    public int saveRequestEPD(HttpServletRequest request, String msg, int mrn, String requestDate, int facilityIndex, Connection conn, ServletContext servletContext) {
        ResultSet rset = null;
        String Query = "";
        CallableStatement cStmt = null;
        int Result;
        try {
            Query = "{CALL SP_SAVE_EPDRequest(?,?,?,?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, msg);
            cStmt.setInt(2, mrn);
            cStmt.setString(3, requestDate);
            cStmt.setInt(4, 1);
            cStmt.setInt(5, facilityIndex);

            rset = cStmt.executeQuery();

            rset.close();
            cStmt.close();

            Result = 1;
        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "saveRequestEPD -- SP -- 015 ", request, Ex, servletContext);
            Result = 0;
        }
        return Result;
    }

    public String[] getCardConnectData(HttpServletRequest request, Connection conn, ServletContext servletContext, int CardConnectIndx, String dbName) {
        ResultSet rset = null;
        String Query = "";
        Statement stmt = null;

        String Amount = "";
        String RetRef = "";
        String InvoiceNo = "";
        int PatientMRN = 0;
        int facilityIndex = 0;

        try {
            Query = "SELECT PatientMRN,Amount,RetRef,InvoiceNo,ClientIndex FROM " + dbName + ".CardConnectResponses " +
                    "WHERE Id = " + CardConnectIndx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientMRN = rset.getInt(1);
                Amount = rset.getString(2);
                RetRef = rset.getString(3);
                InvoiceNo = rset.getString(4);
                facilityIndex = rset.getInt(5);
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            SendEmailWithAttachment("Error in getCardConnectData ", servletContext, Ex, "Handheld - UtilityHelper", "receiptClientData", conn);
            Services.DumException("Handheld - UtilityHelper", "getCardConnectData", request, Ex, getServletContext());
        }
        return new String[]{String.valueOf(PatientMRN), Amount, RetRef, InvoiceNo, String.valueOf(facilityIndex)};
    }

    public boolean checkSession(HttpServletRequest request, ServletContext servletcontext, HttpSession session, PrintWriter out) {
        boolean validSession = true;
        if (request.getSession(false) == null) {
            try {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletcontext) + "Exception/SessionTimeOut.html");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            validSession = false;
        }
        return validSession;
    }

    public int getDifferenceDays(Date d1, Date d2) {
        int daysdiff = 0;
        long diff = d2.getTime() - d1.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
        daysdiff = (int) diffDays;
        return daysdiff;
    }

    public boolean AuthorizeScreen(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final int UserIndex, final Integer ScreenIndex) throws IOException {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer users = new StringBuffer();
        StringBuffer checkboxes = new StringBuffer();
        String IsAdmin = null;
        String rightsPolicy = null;
        String sysUser_index_ptr = null;
        int null_data_found = 0;


        try {


            Query = "SELECT COUNT(*) FROM oe.UserRights where SysUserID='" + UserIndex + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                null_data_found = rset.getInt(1);
            }
            rset.close();
            stmt.close();


//            out.println("null_data_found Query executed " + null_data_found);


            if (null_data_found > 0) {
                Query = "SELECT RightsPolicy,IsAdmin FROM oe.UserRights where SysUserID='" + UserIndex + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);

                while (rset.next()) {
                    rightsPolicy = rset.getString(1);
                    IsAdmin = rset.getString(2);
                }
                rset.close();
                stmt.close();

//                out.println("Rights Query executed isAdmin "+IsAdmin );


                if (IsAdmin.equals("1")) {
                    return true;
                }

                String[] pat = rightsPolicy.split("\\^");
                if (pat[ScreenIndex].equals("1")) {
                    return true;
                } else if (pat[ScreenIndex].equals("0")) {
                    return false;
                }
            } else {
                return false;
            }

        } catch (Exception e) {
//            out.println(e.getMessage());
            String str = "";
            for (int k = 0; k < e.getStackTrace().length; ++k) {
                str = str + e.getStackTrace()[k] + "<br>";
            }
            out.println(str);
            Parsehtm Parser = new Parsehtm(request);

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
        return false;
    }

    public void updateUserPassword(HttpServletRequest request, ServletContext servletContext, Connection conn, PrintWriter out, String NewPassword, String UserId) {
        String Query = "";
        Statement stmt = null;

        try {
            Query = "Update oe.sysusers set password = '" + NewPassword + "', PChangeDate = NOW() " +
                    "where ltrim(rtrim(UPPER(userid))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

        } catch (Exception Ex) {
            SendEmailWithAttachment("Error in updateUserPassword ", servletContext, Ex, "Handheld - UtilityHelper", "updatePatientRegDate", conn);
            Services.DumException("UtilityHelper", "updateUserPassword -- ", request, Ex, this.getServletContext());
        }
    }

    public int savePasswordLogs(HttpServletRequest request, Connection conn, ServletContext servletContext, String OldPassword, String NewPassword,
                                String UserId, int FacilityIndex) {
        ResultSet rset = null;
        String Query = "";
        CallableStatement cStmt = null;
        int Result;
        try {
            String UserIP = getClientIp(request);
            Query = "{CALL SP_SAVE_PasswordLogs(?,?,?,?,?,?)}";
            cStmt = conn.prepareCall(Query);
            cStmt.setString(1, OldPassword);
            cStmt.setString(2, NewPassword);
            cStmt.setString(3, UserId);
            cStmt.setInt(4, FacilityIndex);
            cStmt.setString(5, UserId);
            cStmt.setString(6, UserIP);

            rset = cStmt.executeQuery();

            rset.close();
            cStmt.close();

            Result = 1;
        } catch (Exception Ex) {
            Services.DumException("UtilityHelper", "savePasswordLogs -- SP -- 022 ", request, Ex, servletContext);
            Result = 0;
        }
        return Result;
    }

    public String getAdvocateLoginId(HttpServletRequest request, Connection conn, ServletContext servletContext, int UserIndex) {
        ResultSet rset = null;
        String Query = "";
        Statement stmt = null;
        String userId = "";

        try {
            Query = "SELECT userid from oe.sysusers WHERE indexptr = " + UserIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                userId = rset.getString(1);
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            SendEmailWithAttachment("Error in advocateLoginId ", servletContext, Ex, "Handheld - UtilityHelper", "getFacilityName", conn);
            Services.DumException("Handheld - UtilityHelper", "advocateLoginId", request, Ex, getServletContext());
        }
        return userId;
    }

    public int requestMobileCheck(HttpServletRequest request, Connection conn, ServletContext servletContext, String database,
                                  int PatientRegIdx, int MRN, int facilityIndex) {
        ResultSet rset = null;
        String Query = "";
        Statement stmt = null;
        int requestCheck = 0;

        try {
            Query = "SELECT COUNT(*) FROM " + database + ".RequestToMobile WHERE PatientRegIdx = " + PatientRegIdx + " AND MRN = " + MRN + " " +
                    "AND FacilityIndex = " + facilityIndex + " AND Status = 0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                requestCheck = rset.getInt(1);
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            SendEmailWithAttachment("Error in requestMobileCheck ", servletContext, Ex, "Handheld - UtilityHelper", "requestMobileCheck", conn);
            Services.DumException("Handheld ", "requestMobileCheck", request, Ex, getServletContext());
        }
        return requestCheck;
    }

    public int signPDFCheck(HttpServletRequest request, Connection conn, ServletContext servletContext, String database,
                            int PatientRegIdx) {
        ResultSet rset = null;
        String Query = "";
        Statement stmt = null;
        int found = 0;

        try {
            Query = "Select Count(*) from " + database + ".SignRequest " +
                    "where PatientRegId = " + PatientRegIdx + " AND isSign = 1";
            System.out.println("QUERY --> " + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                found = rset.getInt(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            SendEmailWithAttachment("Error in signPDFCheck ", servletContext, Ex, "Handheld - UtilityHelper", "signPDFCheck", conn);
            Services.DumException("Handheld ", "signPDFCheck", request, Ex, getServletContext());
        }
        return found;
    }

    public int signPDFCheckMobile(HttpServletRequest request, Connection conn, ServletContext servletContext, String database,
                                  int PatientRegIdx) {
        ResultSet rset = null;
        String Query = "";
        Statement stmt = null;
        int found = 0;

        try {
            Query = "Select Count(*) from " + database + ".SignRequest " +
                    "where PatientRegId = " + PatientRegIdx + " AND isSign = 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                found = rset.getInt(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            SendEmailWithAttachment("Error in signPDFCheck ", servletContext, Ex, "Handheld - UtilityHelper", "signPDFCheck", conn);
            Services.DumException("Handheld ", "signPDFCheck", request, Ex, getServletContext());
        }
        return found;
    }

    public String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

    public int SendEmailWithAttachment_ROVERLABOLD(String eSubject, ServletContext servletContext, Connection conn, String EmailTo, String filepath) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        //String Email1 = "tabish.hafeez@fam-llc.com";//change accordingly
        String FilePath = Services.GetEmailLogsPath(servletContext);
        //String emailHtmlFilePath = Services.GetEmailFilePath(servletContext);
        try {
            String HostName = "";
            String EmailUserId = "";
            String EmailPassword = "";
            String SMTP = "";
            String Port = "";
            String Authentication = "";
            try {
                if (conn == null) {
                    conn = Services.getMysqlConn(servletContext);
                }
                Query = "{CALL SP_GET_CredentialsEmail()}";
                cStmt = conn != null ? conn.prepareCall(Query) : (CallableStatement) Services.getMysqlConn(servletContext);
                rset = cStmt != null ? cStmt.executeQuery() : null;
                if (rset != null && rset.next()) {
                    HostName = rset.getString(1);
                    EmailUserId = rset.getString(2);
                    EmailPassword = rset.getString(3);
                    SMTP = rset.getString(4);
                    Port = rset.getString(5);
                    Authentication = rset.getString(6);
                }
                if (rset != null) {
                    rset.close();
                }
                if (cStmt != null) {
                    cStmt.close();
                }
            } catch (Exception Ex) {
                Ex.printStackTrace();
            }
            System.out.println("*****************************************");
            System.out.println("HostName " + HostName);
            System.out.println("EmailUserId " + EmailUserId);
            System.out.println("EmailPassword " + EmailPassword);
            System.out.println("SMTP " + SMTP);
            System.out.println("Port " + Port);
            System.out.println("Authentication " + Authentication);


            System.out.println("*****************************************");
            //1) get the session object

            Properties props = new Properties();
            props.put("mail.transport.protocol", SMTP);
            props.put("mail.smtp.host", HostName);
            props.put("mail.smtp.port", Port);
            props.put("mail.smtp.auth", Authentication);
            final String user = EmailUserId;//change accordingly
            final String password = EmailPassword;//change accordingly
            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user, password);
                        }
                    });


            //session.setDebug(true);
            //Sending a HTML file in Email's
            StringWriter writer = new StringWriter();
            //IOUtils.copy(new FileInputStream(new File("F://EmailFormatter.html")), writer);
//            IOUtils.copy(new FileInputStream(new File(emailHtmlFilePath + "EmailFormatter.html")), writer);
//            IOUtils.copy(new FileInputStream(new File("/sftpdrive/opt/Htmls/md/EmailFormats/EmailFormatter.html")), writer);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("ROVER LAB <no-reply@roverlab.com>"));
            // Set To: header field of the header.
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress((EmailTo == null ? "tabish.hafeez@fam-llc.com" : EmailTo.equals("") ? "tabish.hafeez@fam-llc.com" : EmailTo)));
            EmailTo = (EmailTo == null ? "tabish.hafeez@fam-llc.com" : EmailTo.equals("") ? "tabish.hafeez@fam-llc.com" : EmailTo);
            System.out.println("EMAIL ADDRESS FROM UH --> " + EmailTo);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(EmailTo));
            // Set Subject: header field
            message.setSubject(eSubject);
            //Setting the email priority high
            message.addHeader("X-Priority", "1");

            Transport t = session.getTransport("smtp");
            t.connect();

            // attachement
            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            BodyPart attachmentBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(writer.toString(), "text/html"); // 5

            multipart.addBodyPart(messageBodyPart);
//session.setDebug(true);


            //String filepathc = "/sftpdrive/opt/Htmls/md/logs/EmailLogs/myLogs.log";//change accordingly
//             file path

            System.out.println("SEND EMAIL FILE PATH " + filepath);
            File filename = new File(filepath);

            DataSource source = new FileDataSource(filename);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(filename.getName());
            multipart.addBodyPart(attachmentBodyPart);
            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Email Sent..");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int SendEmailWithAttachment_ROVERLAB(String eSubject, ServletContext servletContext, Connection conn, String EmailTo, String filepath, String OID, String MRN) {
        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        //String Email1 = "tabish.hafeez@fam-llc.com";//change accordingly
        String FilePath = Services.GetEmailLogsPath(servletContext);
        String emailHtmlFilePath = Services.GetEmailFilePath(servletContext);
        try {
            String HostName = "";
            String EmailUserId = "";
            String EmailPassword = "";
            String SMTP = "";
            String Port = "";
            String Authentication = "";
            try {
                if (conn == null) {
                    conn = Services.getMysqlConn(servletContext);
                }
                Query = "{CALL SP_GET_CredentialsEmail()}";
                cStmt = conn != null ? conn.prepareCall(Query) : (CallableStatement) Services.getMysqlConn(servletContext);
                rset = cStmt != null ? cStmt.executeQuery() : null;
                if (rset != null && rset.next()) {
                    HostName = rset.getString(1);
                    EmailUserId = rset.getString(2);
                    EmailPassword = rset.getString(3);
                    SMTP = rset.getString(4);
                    Port = rset.getString(5);
                    Authentication = rset.getString(6);
                }
                if (rset != null) {
                    rset.close();
                }
                if (cStmt != null) {
                    cStmt.close();
                }
            } catch (Exception Ex) {
                Ex.printStackTrace();
            }
            System.out.println("*****************************************");
            System.out.println("HostName " + HostName);
            System.out.println("EmailUserId " + EmailUserId);
            System.out.println("EmailPassword " + EmailPassword);
            System.out.println("SMTP " + SMTP);
            System.out.println("Port " + Port);
            System.out.println("Authentication " + Authentication);


            System.out.println("*****************************************");
            //1) get the session object

            Properties props = new Properties();
            props.put("mail.transport.protocol", SMTP);
            props.put("mail.smtp.host", HostName);
            props.put("mail.smtp.port", Port);
            props.put("mail.smtp.auth", Authentication);
            final String user = EmailUserId;//change accordingly
            final String password = EmailPassword;//change accordingly
            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user, password);
                        }
                    });


            //session.setDebug(true);
            //Sending a HTML file in Email's
            StringWriter writer = new StringWriter();
//            IOUtils.copy(new FileInputStream(new File("F://EmailFormatter.html")), writer);
            IOUtils.copy(new FileInputStream(new File(emailHtmlFilePath + "Results.html")), writer);
//            IOUtils.copy(new FileInputStream(new File("/sftpdrive/opt/Htmls/md/EmailFormats/EmailFormatter.html")), writer);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("PrimeScope Diagnostic <no-reply@rovermd.com>"));
            // Set To: header field of the header.
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress((EmailTo == null ? "tabish.hafeez@fam-llc.com" : EmailTo.equals("") ? "tabish.hafeez@fam-llc.com" : EmailTo)));
            EmailTo = (EmailTo == null ? "tabish.hafeez@fam-llc.com" : EmailTo.equals("") ? "tabish.hafeez@fam-llc.com" : EmailTo);
            System.out.println("EMAIL ADDRESS FROM UH --> " + EmailTo);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(EmailTo));
            // Set Subject: header field
            message.setSubject(eSubject);
            //Setting the email priority high
            message.addHeader("X-Priority", "1");

            Transport t = session.getTransport("smtp");
            t.connect();

            // attachement
            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            BodyPart attachmentBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(writer.toString().replace("URL$$", "https://app1.rovermd.com:8443/md/md.result?ActionID=GetInput&oid=" + OID + "&m=" + MRN + ""), "text/html"); // 5
            //message.setContent(writer.toString().replace("mrn",String.valueOf(MRN)), "text/html");

            multipart.addBodyPart(messageBodyPart);
//session.setDebug(true);


            //String filepathc = "/sftpdrive/opt/Htmls/md/logs/EmailLogs/myLogs.log";//change accordingly
//             file path

            System.out.println("SEND EMAIL FILE PATH " + filepath);
            File filename = new File(filepath);

            DataSource source = new FileDataSource(filename);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(filename.getName());
            multipart.addBodyPart(attachmentBodyPart);
            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Email Sent..");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private class SMTPAuthenticator extends Authenticator {
        private SMTPAuthenticator() {
        }

        public PasswordAuthentication getPasswordAuthentication() {
            String SMTP_HOST_NAME = "smtp.ionos.com";
            String SMTP_AUTH_USER = "alert@rovermd.com";
            String SMTP_AUTH_PWD = "Ale$Rtr0VeMd(Com";

            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PWD;
            return new PasswordAuthentication(username, password);
        }
    }
}
