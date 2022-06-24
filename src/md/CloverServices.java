package md;

import DAL.Payments;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("Duplicates")
public class CloverServices extends HttpServlet {
    private BoltMaster2 boltMaster2 = new BoltMaster2();


    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        requestHandling(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        requestHandling(request, response);
    }

    private void requestHandling(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        PrintWriter out = new PrintWriter(response.getOutputStream());
        UtilityHelper helper = new UtilityHelper();
        Payments payments = new Payments();
        String ServiceRequests = request.getParameter("ServiceRequests").trim();
        ServletContext context;
        context = getServletContext();
        Connection conn = null;
        try {

            HttpSession session = request.getSession(false);
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.println(12 + "~" + "Web Session is expired!");
                out.flush();
                out.close();
                return;
            }

            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());

            if (UserId.equals("")) {
                out.println(12 + "~" + "Web Session is expired!");
//                String sessionKey = payments.sessionActive(request, conn, context, DatabaseName);
                //boltMaster2.stopSession(context, sessionKey);
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }

            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ServiceRequests) {
                case "getConnect":
                    getConnect(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper, payments);
                    break;
                case "DisconnectSession":
                    DisconnectSession(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper, payments);
                    break;
                case "performTransaction":
                    performTransaction(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper, payments);
                    break;
                case "ForceFullyDC":
                    ForceFullyDC(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper, payments);
                    break;
                case "cancelTransaction":
                    cancelTransaction(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper, payments);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }
        } catch (Exception Ex) {
            //helper.SendEmail("Main Section", "Error in Transaction Report", "Message Body");
            helper.SendEmailWithAttachment("Error in CloverServices ** (handleRequest)", context, Ex, "CloverServices", "handleRequest", conn);
            Services.DumException("CloverServices", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.flush();
            out.close();
        }
    }

    private void getConnect(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int facilityIdx, UtilityHelper helper, Payments payments) throws FileNotFoundException {

        String DeviceS = request.getParameter("DeviceS").trim();
        int MRN = Integer.parseInt(request.getParameter("x0Y61008").trim());
        String Invoice = request.getParameter("bYYf43e").trim();
        String[] returnedVal = null;
        int Flag = 0;
        String SessionKey = "";
        Object[] checkSess = new Object[0];
        String[] boltProp;
        try {
            String UserIP = helper.getClientIp(request);

            int checkCredentials = payments.checkBoltCredentials(request, conn, facilityIdx, servletContext);
            if (checkCredentials == 0) {
                payments.boltFailures(request, conn, servletContext, Database, DeviceS, "Clover credentials are not listed! Please contact System Administrator.", UserId, UserIP, 333);
                out.println("11~Clover credentials are not listed! Please contact System Administrator.");
                return;
            }
            try {
                checkSess = CheckActiveSession(conn, Database, UserId, facilityIdx, helper, request, out, servletContext, DeviceS, payments);
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in CloverServices ** (getConnect -- Check Active Session)", servletContext, Ex, "CloverServices", "getConnect", conn);
                Services.DumException("CloverServices", "getConnect", request, Ex, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "PayNow");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                out.flush();
                out.close();
            }

            System.out.println("Bool Val in Check Session *** " + checkSess[0]);
            System.out.println("Message in Check Session *** " + checkSess[1]);
            //session is already created. No need to connect with other session.
            //7-Sept-2021 (TABISH)
            //Changed time to 7 min -- 13Sept 21
            if (!Boolean.parseBoolean(String.valueOf(checkSess[0]))) {
                //_time = 7 - Integer.parseInt(String.valueOf(checkSess[1]));
                //out.println(22 + "~" + "Device is already connected!" + "~" + _time);
                out.println(23 + "~" + "Device is already connected!");
                payments.boltFailures(request, conn, servletContext, Database, DeviceS, "Device is already connected!", UserId, UserIP, 333);
                return;
            }

            try {
                //bm.init(DeviceS, ClientId, conn,servletContext);
                //returnedVal = bm.validateConnection(servletContext);
                //boltMaster2.initialize(DeviceS, ClientId, conn, servletContext);
                boltProp = payments.boltConnectionProperties(request, conn, servletContext, facilityIdx, DeviceS);
                returnedVal = boltMaster2.getSession(servletContext, boltProp[2], boltProp[3], boltProp[4], boltProp[0], boltProp[1]);
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in CloverServices ** (getConnect -- validate Connection)", servletContext, Ex, "CloverServices", "getConnect", conn);
                Services.DumException("CloverServices", "get Connect", request, Ex, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "PayNow");
                Parser.SetField("Message", "Connection is not validated. Please connect your device!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                out.flush();
                out.close();
            }

            System.out.println("returnedVal[0] --> " + returnedVal[0]);
            System.out.println("returnedVal[1] --> " + returnedVal[1]);
            System.out.println("returnedVal[2] --> " + returnedVal[2]);
            if (returnedVal[1] != null) {
//                bm.globalExpMsg = null;
//                bm.errorCode = null;
                boltMaster2.globalExpMsg = null;
                boltMaster2.errorCode = null;
//                System.out.println("ERROR CODE " + returnedVal[2]);
//                System.out.println("MSG " + returnedVal[1]);
                if (returnedVal[2].equals("6")) {
                    out.println(11 + "~" + "Device is not connected!");
                    payments.boltFailures(request, conn, servletContext, Database, DeviceS, "Device is not connected!", UserId, UserIP, 6);
                    payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceS);
                    return;
                } else if (returnedVal[2].equals("1")) {
                    out.println(11 + "~" + "Terminal Session is lost. Please reconnect!");
                    payments.boltFailures(request, conn, servletContext, Database, DeviceS, "Terminal Session is lost. Please reconnect!", UserId, UserIP, 11);
                    payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceS);
                    return;
                }
            }

            SessionKey = returnedVal[0];
            System.out.println("SESSION KEY IN CS " + SessionKey);
            // bm.stop();
            //b6FHY035 -- Session Key

            Object[] invoiceMaster = payments.getInvoiceMasterDetails(request, conn, servletContext, Database, Invoice, String.valueOf(MRN));
            if (SessionKey == null) {
                System.out.println("SESSION NOT ESTABLISHED!!!! ");
                String SessionResponse = "Session Key is required.It is null!";
                int ReturnValue = payments.boltFailures(request, conn, servletContext, Database, DeviceS, SessionResponse, UserId, UserIP, 10);
            } else if (SessionKey.equals("Error")) {
                System.out.println("SESSION NOT ESTABLISHED..!!!! ");
                String SessionResponse = "SessionKey header is required. Error appears not null!";
                int ReturnValue = payments.boltFailures(request, conn, servletContext, Database, DeviceS, SessionResponse, UserId, UserIP, 12);
            } else {
                int ReturnValue = payments.activeSessionBolt(request, conn, servletContext, Database, DeviceS, SessionKey, UserId);
                int myVal = payments.boltSessions(request, conn, servletContext, Database, DeviceS, SessionKey, UserId, Invoice, MRN, (int) invoiceMaster[7]);
                Flag = 1;
                System.out.println("SESSION ESTABLISHED...! myVal -->  " + myVal);
            }
            System.out.println("SESSION ESTABLISHED...! ");
            out.println(Flag + "~" + DeviceS + "~" + SessionKey);
        } catch (Exception Ex) {
/*            try {
                CheckActiveSession(conn, Database, UserId, ClientId, helper, request, out, servletContext, DeviceS, payments);
                //bm.stop();
            } catch (Exception er) {
            }*/
            payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceS);
            helper.SendEmailWithAttachment("Error in CloverServices ** (getConnect -- Main Catch )", servletContext, Ex, "CloverServices", "get Connect", conn);
            Services.DumException("CloverServices", "get Connect", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "PayNow");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
/*            out.println("EXP " + Ex.getMessage());
            String str = "";
            int i = 0;
            for (i = 0; i < (Ex.getStackTrace()).length; i++)
                str = str + Ex.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
            //helper.SendEmail("Main Section", "Error in Transaction Report", "Message Body");

        }
    }

    private Object[] CheckActiveSession(Connection conn, String Database, String UserId, int facilityIdx, UtilityHelper helper, HttpServletRequest request, PrintWriter out, ServletContext servletContext, String DeviceId, Payments payments) throws IOException {
        String[] checkedSession;
        Object[] isSessionExist;
        int inProgress = 0;
        String[] boltProp;
        try {
            //Changed query - 26 MARCH 2021
            checkedSession = payments.checkActiveSessionBolt(request, conn, servletContext, Database, DeviceId);
            System.out.println("Found Session in CheckActiveSession " + checkedSession[0]);
            System.out.println("Existing Value " + checkedSession[1]);
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in CloverServices ** (CheckActiveSession --Error 1- Getting Data from Active Session Bolt Table)", servletContext, Ex, "CloverServices", "CheckActiveSession", conn);
            Services.DumException("CloverServices", "Check Active Session", request, Ex, getServletContext());
            out.flush();
            out.close();
            return new Object[]{false, "Error in Session Check. Please contact System Administrator!"};
        }
        if (Integer.parseInt(checkedSession[0]) > 0) {
            try {
                boltProp = payments.boltConnectionProperties(request, conn, servletContext, facilityIdx, DeviceId);
//                System.out.println("SESSION KEY IN ALREADY EXIST CHECK IN CAS " + checkedSession[1]);
                inProgress = payments.isSessionInProgress(request, conn, servletContext, Database, checkedSession[1]);
                System.out.println("In Progress : " + inProgress);
                if (inProgress > 0) {
                    //IF session is in progress then show user device is in use.
                    return new Object[]{false, "Device is in use"};
                } else {
                    //If session is not in progress state then delete the previous session and create a new conn
                    // On new device and show previous user that session has been disconnected.
//                    System.out.println("IN ALREADY PROGRESS SESSION IS FALSE &&&&& ");
                    String UserIP = helper.getClientIp(request);
                    payments.boltFailures(request, conn, servletContext, Database, DeviceId, "Idle Session has been disconnected!", UserId, UserIP, 20);
                    String sessionKey = payments.sessionActive(request, conn, servletContext, Database);
                    payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
                    boltMaster2.stopSession(servletContext, sessionKey, boltProp[2], boltProp[3], boltProp[0], boltProp[1]);
                    boltMaster2.globalExpMsg = null;
                    boltMaster2.errorCode = null;
                    return new Object[]{true, "Session has been disconnected!"};
                }
//                System.out.println("INITIAL SESSION IN PROGRESS IN CA FROM BoltMaster **** " + bm.sessionInProgress);
/*                if (!bm.sessionInProgress) {
                    //If session is not in progress state then delete the previous session and create a new conn
                    // On new device and show previous user that session has been disconnected.
//                    System.out.println("IN ALREADY PROGRESS SESSION IS FALSE &&&&& ");
                    payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
                    bm.stop(servletContext);
                    bm.globalExpMsg = null;
                    bm.errorCode = null;
                    bm.sessionKey = null;
                    return new Object[]{true, "Session has been disconnected!"};
                } else {
                    //IF session is in progress then show user device is in use.
                    return new Object[]{false, "Device is in use"};
                }*/
                //26-March-2021
                //Dont need to truncate table. Just need to delete the row on behalf of device id
/*                payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
                isSessionExist = payments.sessionTimeCalculationIfIdle(request, conn, servletContext, checkedSession[1], Database);
                System.out.println("isSessionExist[0] " + isSessionExist[0]);
                if (Boolean.parseBoolean(String.valueOf(isSessionExist[0])))
                    payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
                System.out.println("Boolean VAL --> " + isSessionExist[0]);
                System.out.println("Time (MIN) VAL --> " + isSessionExist[1]);*/
                //return new Object[]{isSessionExist[0], isSessionExist[1]};
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in CloverServices ** (CheckActiveSession -- Error 2- Updating/Truncate Table Active Session Bolt Table)", servletContext, Ex, "BoltPayServices", "CheckActiveSession", conn);
                Services.DumException("CloverServices", "Get Details", request, Ex, getServletContext());
/*                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "PayNow");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                */
                out.flush();
                out.close();
                return new Object[]{false, "Error in Session Check. Please contact System Administrator!"};
            }
        }
        return new Object[]{true, ""};
    }

    private void CheckActiveSessionOLD(Connection conn, String Database, String UserId, int ClientId, UtilityHelper helper, HttpServletRequest request, PrintWriter out, ServletContext servletContext, String DeviceId, Payments payments) throws IOException {
        /*String[] checkedSession;
        Query = "";
        stmt = null;
        rset = null;
        Object[] isSessionExist;
        try {
            //Changed query - 26 MARCH 2021
            checkedSession = payments.checkActiveSessionBolt(request, conn, servletContext, Database, DeviceId);
            System.out.println("Found Session in CheckActiveSession " + checkedSession[0]);
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in CloverServices ** (CheckActiveSession --Error 1- Getting Data from Active Session Bolt Table)", servletContext, Ex, "CloverServices", "CheckActiveSession", conn);
            Services.DumException("CloverServices", "Check Active Session", request, Ex, getServletContext());

*//*            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "PayNow");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            *//*
            out.flush();
            out.close();
            return new Object[]{false, "Error in Session Check. Please contact System Administrator!"};
        }
        if (Integer.parseInt(checkedSession[0]) > 0) {
            try {
//                System.out.println("INITIAL SESSION IN PROGRESS IN CA FROM BoltMaster **** " + bm.sessionInProgress);
                if (!bm.sessionInProgress) {
                    //If session is not in progress state then delete the previous session and create a new conn
                    // On new device and show previous user that session has been disconnected.
//                    System.out.println("IN ALREADY PROGRESS SESSION IS FALSE &&&&& ");
                    payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
                    bm.stop(servletContext);
                    bm.globalExpMsg = null;
                    bm.errorCode = null;
                    bm.sessionKey = null;
                    return new Object[]{true, "Session has been disconnected!"};
                } else {
                    //IF session is in progress then show user device is in use.
                    return new Object[]{false, "Device is in use"};
                }
*//*                //26-March-2021
                //Dont need to truncate table. Just need to delete the row on behalf of device id
                //payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
                isSessionExist = payments.sessionTimeCalculationIfIdle(request, conn, servletContext, checkedSession[1], Database);
                System.out.println("isSessionExist[0] " + isSessionExist[0]);
                if (Boolean.parseBoolean(String.valueOf(isSessionExist[0])))
                    payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
                System.out.println("Boolean VAL --> " + isSessionExist[0]);
                System.out.println("Time (MIN) VAL --> " + isSessionExist[1]);*//*
                //return new Object[]{isSessionExist[0], isSessionExist[1]};
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in CloverServices ** (CheckActiveSession -- Error 2- Updating/Truncate Table Active Session Bolt Table)", servletContext, Ex, "BoltPayServices", "CheckActiveSession", conn);
                Services.DumException("CloverServices", "Get Details", request, Ex, getServletContext());
*//*                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "PayNow");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                *//*
                out.flush();
                out.close();
                return new Object[]{false, "Error in Session Check. Please contact System Administrator!"};
            }
        }
        return new Object[]{true, ""};*/
    }

    private void DisconnectSession(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int facilityIndex, UtilityHelper helper, Payments payments) throws IOException {
        String facilityName = "";
        String oiPyRe3Q = request.getParameter("oiPyRe3Q").trim();
        String b6FHY035 = request.getParameter("b6FHY035").trim();
        String DeviceId = request.getParameter("DeviceS").trim();
        try {

            System.out.println("NORMAL DC PERFORMED");
            facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
            String sessionKey = payments.sessionActive(request, conn, servletContext, Database);
            //CheckActiveSession(conn, Database, UserId, facilityIndex, helper, request, out, servletContext, oiPyRe3Q, payments);
            payments.updateBoltSessionDC(request, conn, servletContext, Database, sessionKey);
            payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
            payments.sessionInProgressUpdate(request, conn, servletContext, Database, 0, sessionKey);
            String[] boltProp = payments.boltConnectionProperties(request, conn, servletContext, facilityIndex, DeviceId);
//            System.out.println("Session Key Sending from DC Method in CS --> " + sessionKey);
            boltMaster2.stopSession(servletContext, sessionKey, boltProp[2], boltProp[3], boltProp[0], boltProp[1]);
            boltMaster2.globalExpMsg = null;
            boltMaster2.errorCode = null;
            //bm.sessionKey = null;
            //bm.sessionInProgress = false;
            out.println("100");
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in CloverServices ^^ (Occurred At : " + facilityName + ") ** (DisconnectSession -- Error 01 - Exception ERROR in DC Transaction method)", servletContext, Ex, "CloverServices", "DisconnectSession", conn);
            Services.DumException("CloverServices", "Disconnect Session", request, Ex, getServletContext());
            payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
            out.flush();
            out.close();
        }
    }

    private void ForceFullyDC(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int facilityIndex, UtilityHelper helper, Payments payments) throws IOException {
        String facilityName = "";
//        String oiPyRe3Q = request.getParameter("oiPyRe3Q").trim();
        //      String b6FHY035 = request.getParameter("b6FHY035").trim();
        String DeviceId = request.getParameter("DeviceS").trim();
        try {

            facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
            String sessionKey = payments.sessionActive(request, conn, servletContext, Database);
            System.out.println("SESSION KEY IN FORCEFULLY DC --> " + sessionKey);
            System.out.println("FORCEFULLY DC PERFORMED");
            //CheckActiveSession(conn, Database, UserId, facilityIndex, helper, request, out, servletContext, oiPyRe3Q, payments);
            payments.updateBoltSessionDC(request, conn, servletContext, Database, sessionKey);
            payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
            payments.sessionInProgressUpdate(request, conn, servletContext, Database, 0, sessionKey);
            String[] boltProp = payments.boltConnectionProperties(request, conn, servletContext, facilityIndex, DeviceId);

            boltMaster2.stopSession(servletContext, sessionKey, boltProp[2], boltProp[3], boltProp[0], boltProp[1]);
            boltMaster2.globalExpMsg = null;
            boltMaster2.errorCode = null;
//            bm.sessionKey = null;
//            bm.sessionInProgress = false;
            out.println("100");
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in CloverServices ^^ (Occurred At : " + facilityName + ") ** (ForceFullyDC -- Error 01 - Exception ERROR in DC Transaction method)", servletContext, Ex, "CloverServices", "ForceFullyDC", conn);
            Services.DumException("CloverServices", "Force Fully DC", request, Ex, getServletContext());
//            String sessionKey = payments.sessionActive(request,conn,servletContext,Database);
//            payments.updateBoltSessionDC(request, conn, servletContext, Database, sessionKey);
            payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
            out.flush();
            out.close();
        }
    }

    private void ForceFullyDCFromPT(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int facilityIndex, UtilityHelper helper, Payments payments, String DeviceId) throws IOException {
        String facilityName = "";
//        String oiPyRe3Q = request.getParameter("oiPyRe3Q").trim();
        //      String b6FHY035 = request.getParameter("b6FHY035").trim();
        // String DeviceId = request.getParameter("DeviceS").trim();
        try {

            facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
            String sessionKey = payments.sessionActive(request, conn, servletContext, Database);
            System.out.println("SESSION KEY IN FORCEFULLY DC --> " + sessionKey);
            System.out.println("FORCEFULLY DC PERFORMED");
            //CheckActiveSession(conn, Database, UserId, facilityIndex, helper, request, out, servletContext, oiPyRe3Q, payments);
            payments.updateBoltSessionDC(request, conn, servletContext, Database, sessionKey);
            payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
            payments.sessionInProgressUpdate(request, conn, servletContext, Database, 0, sessionKey);
            String[] boltProp = payments.boltConnectionProperties(request, conn, servletContext, facilityIndex, DeviceId);

            boltMaster2.stopSession(servletContext, sessionKey, boltProp[2], boltProp[3], boltProp[0], boltProp[1]);
            boltMaster2.globalExpMsg = null;
            boltMaster2.errorCode = null;
//            bm.sessionKey = null;
//            bm.sessionInProgress = false;
            out.println("100");
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in CloverServices ^^ (Occurred At : " + facilityName + ") ** (ForceFullyDC -- Error 01 - Exception ERROR in DC Transaction method)", servletContext, Ex, "CloverServices", "ForceFullyDC", conn);
            Services.DumException("CloverServices", "Force Fully DC", request, Ex, getServletContext());
//            String sessionKey = payments.sessionActive(request,conn,servletContext,Database);
//            payments.updateBoltSessionDC(request, conn, servletContext, Database, sessionKey);
            payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
            out.flush();
            out.close();
        }
    }


    private void performTransaction(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int facilityIndex, UtilityHelper helper, Payments payments) throws IOException, ParseException {
        String InvoiceNo = "";
        String PatientMRN = "";
        int Paid = 0;
        double TotalAmount = 0.0;
        double PaidAmount = 0.0;
        double BalAmount = 0.0;
        int InstallmentPlanId = 0;
        String UserIP = "";
        String facilityName = "";
        int PayRecIdx = 0;
        String[] AuthCardRe;

        PatientMRN = request.getParameter("x0Y61008").trim();
        InvoiceNo = request.getParameter("InvoiceNo").trim();
        String BoltAmount = request.getParameter("pUxQ210Ol").trim().replaceAll(",", "");
        String boltDescription = request.getParameter("boltDescription").trim().replaceAll(",", "");
        String DeviceList = request.getParameter("DeviceList").trim().replaceAll(",", "");
        int InstallmentPlanFound = Integer.parseInt(request.getParameter("InstallmentCount").trim());

        //Should take card no (TOKEN NO)

        if (BoltAmount.contains(".")) {
            String parts[] = BoltAmount.split("\\.");
            if (parts[1].length() == 1) {
                parts[1] = parts[1] + "0";
            } else if (parts[1].length() > 2) {
                parts[1] = parts[1].substring(0, 2);
            }
            if (parts[0].equals("0")) {
                BoltAmount = parts[1];
            } else {
                BoltAmount = parts[0] + parts[1];
            }
        } else {
            BoltAmount = BoltAmount + "00";
        }


        JSONParser parser = new JSONParser();
        Object obj = null;
        facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        try {
            int checkCredentials = payments.checkBoltCredentials(request, conn, facilityIndex, servletContext);
            if (checkCredentials == 0) {
                out.println("11~No Device Listed! Please contact System Administrator.");
                return;
            }

            UserIP = helper.getClientIp(request);
            Object[] invoiceMaster = payments.getInvoiceMasterDetails(request, conn, servletContext, Database, InvoiceNo, PatientMRN);
            PaidAmount = (double) invoiceMaster[0];
            BalAmount = (double) invoiceMaster[1];
            TotalAmount = (double) invoiceMaster[3];

/*            if (Double.parseDouble(BoltAmount) > BalAmount) {
                out.println("11~Amount should not be greater than Balance Due!");
                return;
            }*/
            if (BalAmount == 0) {
                out.println("11~No Balance amount left to pay!");
                return;
            }
            String sessionKey = payments.sessionActive(request, conn, servletContext, Database);
            //Updating the session in progress col in Bolt Session List
            payments.sessionInProgressUpdate(request, conn, servletContext, Database, 1, sessionKey);
            //sessionInProgress = true;
            String[] boltProp = payments.boltConnectionProperties(request, conn, servletContext, facilityIndex, DeviceList);
            AuthCardRe = boltMaster2.authCard(BoltAmount, servletContext, sessionKey, boltProp[2], boltProp[3], boltProp[13], boltProp[14], boltProp[15], boltProp[17], boltProp[18], boltProp[19], boltProp[20], boltProp[0], boltProp[1]);
//            System.out.println("SECOND FUNC SESSION IN PROGRESS IN PT FROM BoltMaster **** " + bm.sessionInProgress);
            if (AuthCardRe == null) {
                //bm.sessionInProgress = false;
                System.out.println("Auth Card Response is null!!");
                payments.sessionInProgressUpdate(request, conn, servletContext, Database, 0, sessionKey);
                payments.boltFailures(request, conn, servletContext, Database, DeviceList, "NO RESPONSE FROM THE HOST!", UserId, UserIP, 555);
                payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceList);
                payments.updateBoltSessionDC(request, conn, servletContext, Database, sessionKey);
                out.println(11 + "~" + "NO RESPONSE FROM THE HOST!!!!");
                return;
            }
//            System.out.println("AuthCard[0] " + AuthCardRe[0]);
            System.out.println("AuthCard[1] " + AuthCardRe[1]);
            System.out.println("AuthCard[2] " + AuthCardRe[2]);
            if (AuthCardRe[2] != null) {
                boltMaster2.errorCode = null;
                boltMaster2.globalExpMsg = null;
                //bm.sessionInProgress = false;
                payments.sessionInProgressUpdate(request, conn, servletContext, Database, 0, sessionKey);
                switch (AuthCardRe[2]) {
                    case "1":
                        payments.boltFailures(request, conn, servletContext, Database, DeviceList, "Terminal request timed out!", UserId, UserIP, 1);
                        payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceList);
                        payments.updateBoltSessionDC(request, conn, servletContext, Database, sessionKey);
                        boltMaster2.stopSession(servletContext, sessionKey, boltProp[2], boltProp[3], boltProp[0], boltProp[1]);
                        out.println(13 + "~" + "Terminal request timed out!");
                        return;
                    case "8":
                        payments.boltFailures(request, conn, servletContext, Database, DeviceList, "Command Cancelled!", UserId, UserIP, 8);
                        payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceList);
                        payments.updateBoltSessionCancel(request, conn, servletContext, Database, sessionKey);
                        boltMaster2.haltSession(servletContext, sessionKey, boltProp[2], boltProp[3], boltProp[0], boltProp[1]);
                        out.println(8 + "~" + "Command Cancelled!");
                        return;
                    case "9":
                        payments.boltFailures(request, conn, servletContext, Database, DeviceList, "Device is in merchant mode!", UserId, UserIP, 9);
                        payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceList);
                        payments.updateBoltSessionDC(request, conn, servletContext, Database, sessionKey);
                        boltMaster2.stopSession(servletContext, sessionKey, boltProp[2], boltProp[3], boltProp[0], boltProp[1]);
                        out.println(11 + "~" + "Device is in merchant mode!");
                        return;
                    case "7":
                        payments.boltFailures(request, conn, servletContext, Database, DeviceList, AuthCardRe[1], UserId, UserIP, 7);
                        payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceList);
                        payments.updateBoltSessionDC(request, conn, servletContext, Database, sessionKey);
                        boltMaster2.stopSession(servletContext, sessionKey, boltProp[2], boltProp[3], boltProp[0], boltProp[1]);
                        out.println(11 + "~" + AuthCardRe[1]);
                        return;
                    default:
                        payments.boltFailures(request, conn, servletContext, Database, DeviceList, AuthCardRe[1], UserId, UserIP, 101);
                        payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceList);
                        payments.updateBoltSessionDC(request, conn, servletContext, Database, sessionKey);
                        boltMaster2.stopSession(servletContext, sessionKey, boltProp[2], boltProp[3], boltProp[0], boltProp[1]);
                        out.println(11 + "~" + AuthCardRe[1]);
                        return;
                }
            }
            obj = parser.parse("[" + AuthCardRe[0] + "]");
            JSONArray array = (JSONArray) obj;
            JSONObject obj2 = (JSONObject) array.get(0);
            String token = (String) obj2.get("token");
            String expiry = (String) obj2.get("expiry");
            String name = (String) obj2.get("name");
            String batchid = (String) obj2.get("batchid");
            String retref = (String) obj2.get("retref");
            String avsresp = (String) obj2.get("avsresp");
            String respproc = (String) obj2.get("respproc");
            String amount = (String) obj2.get("amount");
            String ResponseText = (String) obj2.get("resptext");
            String authcode = (String) obj2.get("authcode");
            String respCode = (String) obj2.get("respcode");
            String merchid = (String) obj2.get("merchid");
            String cvvresp = (String) obj2.get("cvvresp");
            String respstat = (String) obj2.get("respstat");
            String emvTagData = (String) obj2.get("emvTagData");
            String orderid = (String) obj2.get("orderid");
            String entrymode = (String) obj2.get("entrymode");
            String bintype = (String) obj2.get("bintype");

/*            System.out.println("amount " + amount + " <br>");
            System.out.println("token " + token + " <br>");
            System.out.println("expiry " + expiry + " <br>");
            System.out.println("name " + name + " <br>");
            System.out.println("batchid " + batchid + " <br>");
            System.out.println("retref " + retref + " <br>");
            System.out.println("avsresp " + avsresp + " <br>");
            System.out.println("respproc " + respproc + " <br>");

            System.out.println("resptext " + ResponseText + " <br>");
            System.out.println("authcode " + authcode + " <br>");
            System.out.println("respcode " + respCode + " <br>");
            System.out.println("merchid " + merchid + " <br>");
            System.out.println("cvvresp " + cvvresp + " <br>");
            System.out.println("emvTagData " + emvTagData + " <br>");
            System.out.println("orderid " + orderid + " <br>");
            System.out.println("respstat " + respstat + " <br>");
            System.out.println("entrymode " + entrymode + " <br>");
            System.out.println("bintype " + bintype + " <br>");*/

/*            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);

            JsonNode jsonNode = objectMapper.readTree(String.valueOf(AuthCardRe[0]));  */

            if (ResponseText.equals("Approval") && respCode.equals("000")) {
                if (BalAmount == Double.parseDouble(amount)) {
                    Paid = 1;
                }
                payments.insertInvoiceMasterHistory(request, conn, servletContext, Database, PatientMRN, InvoiceNo, UserIP);

                payments.updateInvoiceMaster(request, conn, servletContext, Database, PaidAmount, Double.parseDouble(amount), BalAmount, Paid, PatientMRN, InvoiceNo);

                String receiptCounter = helper.getReceiptCounter(request, conn, servletContext, Database);
                payments.paymentReceiptInsertion(request, conn, servletContext, Database, PatientMRN, InvoiceNo, TotalAmount, Paid, "BOLT", "BOLT PAYMENT", "3", BalAmount, UserId, UserIP, "CloverServices-PerformTransaction", Double.parseDouble(amount), receiptCounter, "Print From Device ** BOLT");

                if (InstallmentPlanFound > 0) {
                    InstallmentPlanId = payments.getInstallmentIdx(request, conn, servletContext, Database, PatientMRN, InvoiceNo);

                    payments.updateInstallmentTable(request, conn, servletContext, Database, PatientMRN, InvoiceNo, InstallmentPlanId);
                }
                PayRecIdx = payments.getPaymentReceiptIndex(request, conn, servletContext, Database);
                payments.insertionJSONResponseBolt(request, conn, servletContext, Database, InvoiceNo, PatientMRN, AuthCardRe[0], UserId, UserIP, "BoltPayServices ** performTransaction", Double.parseDouble(amount), boltDescription, DeviceList, PayRecIdx, "SUCCESS");

                //bm.sessionInProgress = false;
//                payments.sessionInProgressUpdate(request, conn, servletContext, Database, 0, sessionKey);
                out.println("000~" + ResponseText + "~" + retref);
                System.out.println("000~" + ResponseText + "~" + retref);
                System.out.println("Going For Forcefully DC...");
                ForceFullyDCFromPT(request, conn, servletContext, out, Database, UserId, facilityIndex, helper, payments, DeviceList);
            } else {
                //bm.sessionInProgress = false;
//                payments.sessionInProgressUpdate(request, conn, servletContext, Database, 0, sessionKey);
                payments.insertionJSONResponseBolt(request, conn, servletContext, Database, InvoiceNo, PatientMRN, AuthCardRe[0], UserId, UserIP, "BoltPayServices ** performTransaction", 0, boltDescription, DeviceList, 0, "ERROR");
                payments.boltFailures(request, conn, servletContext, Database, DeviceList, ResponseText, UserId, UserIP, 444);
//                payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceList);
//                payments.updateBoltSessionDC(request, conn, servletContext, Database, sessionKey);
                out.println("09~" + ResponseText);
                System.out.println("09~" + ResponseText);
                System.out.println("Going For Forcefully DC...");
                ForceFullyDCFromPT(request, conn, servletContext, out, Database, UserId, facilityIndex, helper, payments, DeviceList);
            }

            /*
//            System.out.println("BOLT amount " + amount);


            String respCode = null;
//            String DateTime = null;
            String ApprovedAmount = null;
//            String CardAddress1 = null;
//            String CardAddress2 = null;
//            String CardPhone = null;
//            String CardName = null;
//            JsonNode receiptData = null;
            if (jsonNode.has("resptext")) {
                resptext = jsonNode.get("resptext").toString();
                respCode = jsonNode.get("respcode").toString();
                resptext = resptext.substring(1, resptext.length() - 1);
                respCode = respCode.substring(1, respCode.length() - 1);

                if (resptext.equals("Approval") && respCode.equals("000")) {
                    ApprovedAmount = jsonNode.get("amount").toString();
                    ApprovedAmount = ApprovedAmount.substring(1, ApprovedAmount.length() - 1);

                    if (BalAmount == Double.parseDouble(ApprovedAmount)) {
                        Paid = 1;
                    }
                    //System.out.println("BOLT ApprovedAmount " + ApprovedAmount);

                    payments.insertInvoiceMasterHistory(request, conn, servletContext, Database, PatientMRN, InvoiceNo, UserIP);

                    payments.updateInvoiceMaster(request, conn, servletContext, Database, PaidAmount, Double.parseDouble(ApprovedAmount), BalAmount, Paid, PatientMRN, InvoiceNo);

                    String receiptCounter = helper.getReceiptCounter(request, conn, servletContext, Database);
                    payments.paymentReceiptInsertion(request, conn, servletContext, Database, PatientMRN, InvoiceNo, TotalAmount, Paid, "BOLT", "BOLT PAYMENT", "3", BalAmount, UserId, UserIP, "CloverServices-PerformTransaction", Double.parseDouble(ApprovedAmount), receiptCounter, "Print From Device ** BOLT");

                    if (InstallmentPlanFound > 0) {
                        InstallmentPlanId = payments.getInstallmentIdx(request, conn, servletContext, Database, PatientMRN, InvoiceNo);

                        payments.updateInstallmentTable(request, conn, servletContext, Database, PatientMRN, InvoiceNo, InstallmentPlanId);
                    }
                    PayRecIdx = payments.getPaymentReceiptIndex(request, conn, servletContext, Database);
                    payments.insertionJSONResponseBolt(request, conn, servletContext, Database, InvoiceNo, PatientMRN, AuthCardRe, UserId, UserIP, "BoltPayServices ** performTransaction", Double.parseDouble(ApprovedAmount), boltDescription, DeviceList, PayRecIdx, "SUCCESS");

                    out.println("success~" + resptext + "~" + retref);
                } else {
                    //Error or Declined will treated After This
                    payments.insertionJSONResponseBolt(request, conn, servletContext, Database, InvoiceNo, PatientMRN, AuthCardRe, UserId, UserIP, "BoltPayServices ** performTransaction", 0, boltDescription, DeviceList, 0, "ERROR");
                    out.println("09~" + resptext);//Transaction Declined.
                }
            } else {
                out.println("jsonvalidatefail~" + resptext);//Transaction Declined.
            }
*/
        } catch (Exception Ex) {
            payments.boltFailures(request, conn, servletContext, Database, DeviceList, Ex.getMessage(), UserId, UserIP, 9);
            out.println("09~" + Ex.getMessage());
            helper.SendEmailWithAttachment("Error in CloverServices ^^ (Occurred At : " + facilityName + ") ** (performTransaction -- Error 01 - Exception ERROR in perform Transaction method)", servletContext, Ex, "CloverServices", "performTransaction", conn);
            Services.DumException("CloverServices", "perform Transaction", request, Ex, getServletContext());
            String sessionKey = payments.sessionActive(request, conn, servletContext, Database);
            payments.updateBoltSessionDC(request, conn, servletContext, Database, sessionKey);
            payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceList);
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("FormName", "RegisteredPatients");
//            Parser.SetField("ActionID", "PayNow&InvoiceNo='" + InvoiceNo + "'");
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
    }

    private void cancelTransaction(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int facilityIndex, UtilityHelper helper, Payments payments) throws IOException {
        String facilityName = "";
        String oiPyRe3Q = request.getParameter("oiPyRe3Q").trim();
        String b6FHY035 = request.getParameter("b6FHY035").trim();
        String DeviceId = request.getParameter("DeviceS").trim();
        try {

            System.out.println("Transaction is Cancelled..");
            facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
            String sessionKey = payments.sessionActive(request, conn, servletContext, Database);
            //CheckActiveSession(conn, Database, UserId, facilityIndex, helper, request, out, servletContext, oiPyRe3Q, payments);
            //payments.updateBoltSessionDC(request, conn, servletContext, Database, b6FHY035);
            payments.updateBoltSessionCancel(request, conn, servletContext, Database, sessionKey);
            payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
            payments.sessionInProgressUpdate(request, conn, servletContext, Database, 0, sessionKey);
            String[] boltProp = payments.boltConnectionProperties(request, conn, servletContext, facilityIndex, DeviceId);

            boltMaster2.haltSession(servletContext, sessionKey, boltProp[2], boltProp[3], boltProp[0], boltProp[1]);
            boltMaster2.globalExpMsg = null;
            boltMaster2.errorCode = null;
//            bm.sessionKey = null;
//            bm.sessionInProgress = false;
            out.println("100");
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in CloverServices ^^ (Occurred At : " + facilityName + ") ** (cancelTransaction -- Error 01 - Exception ERROR in cancel Transaction method)", servletContext, Ex, "CloverServices", "cancelTransaction", conn);
            Services.DumException("CloverServices", "cancel Transaction", request, Ex, getServletContext());
//            String sessionKey = payments.sessionActive(request,conn,servletContext,Database);
//            payments.updateBoltSessionDC(request, conn, servletContext, Database, sessionKey);
            payments.deleteActiveSessionByDeviceId(request, conn, servletContext, Database, DeviceId);
            out.flush();
            out.close();
        }
    }

    /*public static void getrequestdetails(HttpServletRequest request,Exception ee) {

        String path = request.getRequestURI();
        String query = request.getQueryString();
        String context = request.getContextPath();
        String servlet = request.getServletPath();
        String info = request.getPathInfo();

        try {

            Enumeration<String> enum_1 = request.getParameterNames();
            while (enum_1.hasMoreElements()) {
                String name = (String) enum_1.nextElement();
                String values[] = request.getParameterValues(name);
                if (values != null) {
                    for (int i = 0; i < values.length; i++) {
                        //pushLogs(name + " (" + i + "): " + values[i],ee);
                    }
                }
            }

        }catch(Exception eee) {



        }

    }*/

/*    private void insertionBoltResponse(String Database, int facilityIndex, int advocateIdx, int Priority, String PtName, int PtMRN, String PtPhNumber, String Sms, String Username, int status) {
        PreparedStatement MainReceipt = null;
        try {
            MainReceipt = conn.prepareStatement(
                    "INSERT INTO oe_2.BoltResponseWithoutPerformingActions(BoltResponse, MRN, InvoiceNo, " +
                            "AmountFromWebPage, DescriptionFromWebPage, DeviceList, InstallementPlan, " +
                            "CreatedDate, Status, CreatedBy) " +
                            "VALUES ()");
        } catch (Exception ex) {
            System.out.println("EXCEPTION in Saving Record" + ex.getMessage());
        }
    }*/
}
