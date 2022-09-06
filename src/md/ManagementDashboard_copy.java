//
// Decompiled by Procyon v0.5.36
//

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import com.itextpdf.text.pdf.PdfWriter;

@SuppressWarnings("Duplicates")
public class ManagementDashboard_copy extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    Integer ScreenIndex = 1;
    private Connection conn = null;

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        String FontColor;
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        Services supp = new Services();
        int UserIndex=0;
        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();

        try {
            HttpSession session = request.getSession(false);
            UtilityHelper helper = new UtilityHelper();

            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FontColor = session.getAttribute("FontColor").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());



            try {
/*                boolean ValidSession = FacilityLogin.checkSession(out, request, context, response);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    return;
                }*/
                if (UserId.equals("")) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            } catch (Exception excp) {
                conn = null;
                out.println("Exception excp conn: " + excp.getMessage());
            }


            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);


//            if(!helper.AuthorizeScreen(request,out,conn,context,UserIndex,this.ScreenIndex)){
////                out.println("You are not Authorized to access this page");
//                Parsehtm Parser = new Parsehtm(request);
//                Parser.SetField("Message", "You are not Authorized to access this page");
//                Parser.SetField("FormName", "ManagementDashboard");
//                Parser.SetField("ActionID", "GetInput");
//                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
//                return;
//            }


            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.equals("GetInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Management Dashboard", "View Management Dashboard", FacilityIndex);
                this.GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, FontColor);
            } else if (ActionID.equals("DashBoardDateWise")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Management Dashboard", "View Management Dashboard", FacilityIndex);
                this.DashBoardDateWise(request, out, conn, context, UserId, DatabaseName, FacilityIndex, FontColor);
            }
            else {
                helper.deleteUserSession(request, conn, session.getId());
                //Invalidating Session.
                session.invalidate();
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
            }
        } catch (Exception e) {
            out.println("Exception in main... " + e.getMessage());
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


    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId, String FontColor) {
        SupportiveMethods suppMethods = new SupportiveMethods();

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        String Query1 = "";
//        int PatientCount = 0;
        String FromDate = "";
        String ToDate = "";
        String _FromEndDate = "";
        String _ToEndDate = "";
        StringBuffer CDRList = new StringBuffer();

//        StringBuffer DoctorsList = new StringBuffer();
        StringBuffer DoctorsData = new StringBuffer();
        StringBuffer DoctorNames = new StringBuffer();

        StringBuffer Marketing_Data = new StringBuffer();
        StringBuffer marketing_LABEL = new StringBuffer();

        String PatientsCurrentMonthDaily = "";
        String PatientCountAgeWise = "";
        String CurrentYear = "";
        String DateNow = "";
        String PatientCountMonthly = "";
        String CurrentDate = "";
        int PatientCountCurrentMonth = 0;
        int PatientCountOverAll = 0;
        int PatientCountMale = 0;
        int PatientCountFemale = 0;
        int PatientCountSelfPay = 0;
        int PatientCountInsured = 0;
        int PatientCountWC = 0;
        int PatientCountMVA = 0;
        int PatientCountCOVID = 0;

        int PatientCountToday = 0;


        String TODAY = "";
        int PatientCountOverAll_TODAY = 0;
        int PatientCountInsured_TODAY = 0;
        int PatientCountSelfPay_TODAY = 0;
        int PatientCountWC_TODAY = 0;
        int PatientCountMVA_TODAY = 0;
        int PatientCountCOVID_TODAY = 0;



        String WEEK_START = "";
        String WEEK_END = "";
        int PatientCountOverAll_WEEKLY = 0;
        int PatientCountInsured_WEEKLY = 0;
        int PatientCountSelfPay_WEEKLY = 0;
        int PatientCountWC_WEEKLY = 0;
        int PatientCountMVA_WEEKLY = 0;
        int PatientCountCOVID_WEEKLY = 0;

        String MONTH_START = "";
        String MONTH_END = "";
        int PatientCountOverAll_MONTHLY = 0;
        int PatientCountInsured_MONTHLY = 0;
        int PatientCountSelfPay_MONTHLY = 0;
        int PatientCountWC_MONTHLY = 0;
        int PatientCountMVA_MONTHLY = 0;
        int PatientCountCOVID_MONTHLY= 0;

        String LAST_MONTH_START = "";
        String LAST_MONTH_END = "";
        int PatientCountOverAll_LAST_MONTHLY = 0;

        String PatientsCurrentWeekDaily = "";

        int presentDayNumber=0;


        try {

            /*----------------------------------------------------------------------------------------------
             *                             Getting Time Variables
             * ---------------------------------------------------------------------------------------------
             */
                Query = "Select DATE_FORMAT(NOW(),'%Y-%m-%d'), " +
                        "DATE_ADD(DATE_FORMAT(NOW(),'%Y-%m-%d'), INTERVAL(1-DAYOFWEEK(DATE_FORMAT(NOW(),'%Y-%m-%d'))) DAY), " +
                        "DATE_ADD(DATE_ADD(DATE_FORMAT(NOW(),'%Y-%m-%d'), INTERVAL(1-DAYOFWEEK(DATE_FORMAT(NOW(),'%Y-%m-%d'))) DAY),INTERVAL 6 DAY), " +
                        "DATE_SUB(LAST_DAY(NOW()),INTERVAL DAY(LAST_DAY(NOW()))- 1 DAY) AS 'FIRST DAY OF CURRENT MONTH', " +
                        "LAST_DAY(NOW())," +
                        "DATE_SUB(LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH),INTERVAL DAY(LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH))- 1 DAY) AS 'FIRST DAY OF LAST MONTH'," +
                        "LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH )," +
                        " Date_format(NOW(),'%d')";


                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    TODAY = rset.getString(1);
                    WEEK_START = rset.getString(2);
                    WEEK_END = rset.getString(3);
                    MONTH_START = rset.getString(4);
                    MONTH_END = rset.getString(5);
                    LAST_MONTH_START = rset.getString(6);
                    LAST_MONTH_END = rset.getString(7);
                    presentDayNumber = rset.getInt(8);
                }
                rset.close();
                stmt.close();



            /*----------------------------------------------------------------------------------------------
             *                              TODAY
             * ---------------------------------------------------------------------------------------------
             */

            Query = "Select COUNT(*) from " + Database + ".PatientVisit a INNER JOIN "+Database+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59' AND b.status=0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_TODAY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            //INSURED
            if (ClientId == 9 || ClientId == 28) {
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where d.HealthInsuranceChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountInsured_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                        "where PatientReg.Status = 0 and PatientReg.SelfPayChk = 1 and " +
                        "PatientVisit.DateOfService >= '" + TODAY + " 00:00:00' and PatientVisit.DateOfService <= '" + TODAY + " 23:59:59'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountInsured_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //SELFPAY
            if (ClientId == 9 || ClientId == 28) {
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where d.HealthInsuranceChk = 0 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountSelfPay_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                        "where PatientReg.Status = 0 and PatientReg.SelfPayChk = 0 and " +
                        "PatientVisit.DateOfService >= '" + TODAY + " 00:00:00' and PatientVisit.DateOfService <= '" + TODAY + " 23:59:59'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountSelfPay_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //MVA
            if (ClientId == 9 || ClientId == 28) {
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d " +
                        "LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        "  where MotorVehicleAccidentChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMVA_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo d " +
                        "LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId" +
                        " INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where MotorVehAccident = 1 " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMVA_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //WC
            if (ClientId == 9 || ClientId == 28) {
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId  " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where WorkersCompPolicyChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountWC_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo d " +
                        "LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        " INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where WorkersCompPolicy = 1 " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountWC_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //COVID
            Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                    "where PatientReg.Status = 0 and PatientReg.COVIDStatus = 1 and " +
                    "PatientVisit.DateOfService >= '" + TODAY + " 00:00:00' and PatientVisit.DateOfService <= '" + TODAY + " 23:59:59'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountCOVID_TODAY = rset.getInt(1);
            }
            rset.close();
            stmt.close();





            /*----------------------------------------------------------------------------------------------
             *                              WEEKLY
             * ---------------------------------------------------------------------------------------------
             */

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit where DateOfService >= '" + WEEK_START + " 00:00:00' and DateOfService <= '" + WEEK_END + " 23:59:59'";
            Query = "Select COUNT(*) from " + Database + ".PatientVisit a INNER JOIN "+Database+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59' AND b.status=0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_WEEKLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            //INSURED
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId " +
//                        "where PatientReg_Details.HealthInsuranceChk = 1 \n" +
//                        "and  PatientVisit.DateOfService >= '" + WEEK_START + " 00:00:00' and PatientVisit.DateOfService <= '" + WEEK_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where d.HealthInsuranceChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountInsured_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".PatientReg " +
                        "LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                        "where PatientReg.Status = 0 and PatientReg.SelfPayChk = 1 and " +
                        "PatientVisit.DateOfService >= '" + WEEK_START + " 00:00:00' and PatientVisit.DateOfService <= '" + WEEK_END + " 23:59:59'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountInsured_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //SELFPAY
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId " +
//                        "where PatientReg_Details.HealthInsuranceChk = 0 \n" +
//                        "and  PatientVisit.DateOfService >= '" + WEEK_START + " 00:00:00' and PatientVisit.DateOfService <= '" + WEEK_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where d.HealthInsuranceChk = 0 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountSelfPay_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                        "where PatientReg.Status = 0 and PatientReg.SelfPayChk = 0 and " +
                        "PatientVisit.DateOfService >= '" + WEEK_START + " 00:00:00' and PatientVisit.DateOfService <= '" + WEEK_END + " 23:59:59'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountSelfPay_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //MVA
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN  " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId   where MotorVehicleAccidentChk = 1 \n" +
//                        "and  PatientVisit.DateOfService >= '" + WEEK_START + " 00:00:00' and PatientVisit.DateOfService <= '" + WEEK_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d " +
                        "LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        "  where MotorVehicleAccidentChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMVA_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo d" +
                        " LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId" +
                        " INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where MotorVehAccident = 1 " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMVA_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //WC
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN  " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId   where WorkersCompPolicyChk = 1 \n" +
//                        "and  PatientVisit.DateOfService >= '" + WEEK_START + " 00:00:00' and PatientVisit.DateOfService <= '" +  + " 23:59:59'";

                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId  " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where WorkersCompPolicyChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountWC_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo d" +
                        " LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        " INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where WorkersCompPolicy = 1 " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountWC_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //COVID
            Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                    "where PatientReg.Status = 0 and PatientReg.COVIDStatus = 1 and " +
                    "PatientVisit.DateOfService >= '" + WEEK_START + " 00:00:00' and PatientVisit.DateOfService <= '" + WEEK_END + " 23:59:59'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountCOVID_WEEKLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();




            /*----------------------------------------------------------------------------------------------
             *                              MONTHLY
             * ---------------------------------------------------------------------------------------------
             */

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit where DateOfService >= '" + MONTH_START + " 00:00:00' and DateOfService <= '" + MONTH_END + " 23:59:59'";
            Query = "Select COUNT(*) from " + Database + ".PatientVisit a INNER JOIN "+Database+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' AND b.status=0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_MONTHLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            //INSURED
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId " +
//                        "where PatientReg_Details.HealthInsuranceChk = 1 \n" +
//                        "and  PatientVisit.DateOfService >= '" + MONTH_START + " 00:00:00' and PatientVisit.DateOfService <= '" + MONTH_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where d.HealthInsuranceChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountInsured_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                        "where PatientReg.Status = 0 and PatientReg.SelfPayChk = 1 and " +
                        "PatientVisit.DateOfService >= '" + MONTH_START + " 00:00:00' and PatientVisit.DateOfService <= '" + MONTH_END + " 23:59:59'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountInsured_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //SELFPAY
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId " +
//                        "where PatientReg_Details.HealthInsuranceChk = 0 \n" +
//                        "and  PatientVisit.DateOfService >= '" + MONTH_START + " 00:00:00' and PatientVisit.DateOfService <= '" + MONTH_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where d.HealthInsuranceChk = 0 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountSelfPay_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                        "where PatientReg.Status = 0 and PatientReg.SelfPayChk = 0 and " +
                        "PatientVisit.DateOfService >= '" + MONTH_START + " 00:00:00' and PatientVisit.DateOfService <= '" + MONTH_END + " 23:59:59'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountSelfPay_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //MVA
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN  " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId   where MotorVehicleAccidentChk = 1 \n" +
//                        "and  PatientVisit.DateOfService >= '" + MONTH_START + " 00:00:00' and PatientVisit.DateOfService <= '" + MONTH_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d " +
                        "LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        "  where MotorVehicleAccidentChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMVA_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo d" +
                        " LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId" +
                        " INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where MotorVehAccident = 1 " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMVA_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //WC
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN  " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId   where WorkersCompPolicyChk = 1 \n" +
//                        "and  PatientVisit.DateOfService >= '" + MONTH_START + " 00:00:00' and PatientVisit.DateOfService <= '" + MONTH_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId  " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where WorkersCompPolicyChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountWC_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo d" +
                        " LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where WorkersCompPolicy = 1 " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountWC_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //COVID
            Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                    "where PatientReg.Status = 0 and PatientReg.COVIDStatus = 1 and " +
                    "PatientVisit.DateOfService >= '" + MONTH_START + " 00:00:00' and PatientVisit.DateOfService <= '" + MONTH_END + " 23:59:59'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountCOVID_MONTHLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            /*----------------------------------------------------------------------------------------------
             *                             LAST MONTH
             * ---------------------------------------------------------------------------------------------
             */

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit where DateOfService >= '" + LAST_MONTH_START + " 00:00:00' and DateOfService <= '" + LAST_MONTH_END + " 23:59:59'";
            Query = "Select COUNT(*) from " + Database + ".PatientVisit a INNER JOIN "+Database+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + LAST_MONTH_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + LAST_MONTH_END + " 23:59:59' AND b.status=0";;

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_LAST_MONTHLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            /*----------------------------------------------------------------------------------------------
             *                             WEEKLY TREND
             * ---------------------------------------------------------------------------------------------
             */
            Query = "SELECT * FROM  (SELECT adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date FROM \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t0, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t4) v \n" +
                    "WHERE selected_date >= '" + WEEK_START + "' AND selected_date <= '" + WEEK_END + "' ORDER BY selected_date";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                _FromEndDate = rset.getString(1);
                _ToEndDate = rset.getString(1);
//                Query1 = "Select COUNT(*) from " + Database + ".PatientVisit  WHERE DateOfService >= '" + _FromEndDate + " 00:00:00' and DateOfService <= '" + _ToEndDate + " 23:59:59'";
                Query1 = "Select COUNT(*) from " + Database + ".PatientVisit a INNER JOIN "+Database+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + _FromEndDate + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + _ToEndDate + " 23:59:59' AND b.status=0";;
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                while (rset1.next()) {
                    PatientsCurrentWeekDaily += rset1.getInt(1) + " , ";
                }
                rset1.close();
                stmt1.close();

            }
            rset.close();
            stmt.close();

            if (PatientsCurrentWeekDaily.endsWith(",")) {
                PatientsCurrentWeekDaily = PatientsCurrentWeekDaily.substring(0, PatientsCurrentWeekDaily.length() - 1);
            }





//            Query = "Select Id,Concat(DoctorsLastName, ',', DoctorsFirstName) from " + Database + ".DoctorsList where Status = 1";
//            //out.println(Query);
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            while (rset.next()) {
//                DoctorsList.append("<div class=\"d-flex align-items-center justify-content-between my-15 pr-20\">");
//                DoctorsList.append("<h5 class=\"my-0\"><i class=\"mr-5 w-20 fa fa-user-md\"></i>" + rset.getString(2) + "</h5>");
//
//                Query1 = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and DoctorsName = " + rset.getInt(1);
//                stmt1 = conn.createStatement();
//                rset1 = stmt1.executeQuery(Query1);
//                if (rset1.next()) {
//                    DoctorsList.append("<p class=\"mb-0\">" + rset1.getInt(1) + "</p>");
//                }
//                rset1.close();
//                stmt1.close();
//
//                DoctorsList.append("</div>");
//            }
//            rset.close();
//            stmt.close();

            /*----------------------------------------------------------------------------------------------
             *                             Doctors Data
             * ---------------------------------------------------------------------------------------------
             */
            Query = "Select Id,Concat('Dr.',DoctorsLastName, ', ', DoctorsFirstName) from " + Database + ".DoctorsList where Status = 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Query1 = "Select COUNT(*) from " + Database + ".PatientVisit where  DoctorId = " + rset.getInt(1) + " and  CreatedDate between '" + MONTH_START + " 00:00:00' and '" + MONTH_END + " 23:59:59' ";
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                DoctorNames.append("'"+rset.getString(2)+"',");
                if (rset1.next()) {
                    DoctorsData.append("{value:"+rset1.getString(1)+" , name:'"+rset.getString(2)+"'},");
                }
                rset1.close();
                stmt1.close();


            }
            rset.close();
            stmt.close();

            /*----------------------------------------------------------------------------------------------
             *                            Marketing Data
             * ---------------------------------------------------------------------------------------------
             */

            if (ClientId == 27 || ClientId == 29) {
                String[] marketingLabels = {"VisitedBefore","FamilyVisitedBefore","Internet","Billboard",
                        "Google","BuildingSignage","Facebook","LivesNear","Twitter","Tv","MapSearch","Event"};






                Query = " Select SUM(FrVisitedBefore),SUM(FrFamiliyVisitedBefore),SUM(FrInternet),SUM(FrBillboard),SUM(FrGoogle),SUM(FrBuildingSignage),SUM(FrFacebook),SUM(FrLivesNear)," +
                        " SUM(FrTwitter),SUM(FrTV),SUM(FrMapSearch),SUM(FrEvent) " +//12
                        " from " + Database + ".RandomCheckInfo where CreatedDate between '" + MONTH_START + " 00:00:00' and '" + MONTH_END + " 23:59:59' ";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
//                    marketingLabels[0] = rset.getLong(1);

                    marketing_LABEL.append("\"VisitedBefore\",\"FamilyVisitedBefore\",\"Internet\",\"Billboard\",\"Google\",\"BuildingSignage\",\"Facebook\",\"LivesNear\",\"Twitter\",\"Tv\",\"MapSearch\",\"Event\"");
                    Marketing_Data.append("{value:"+rset.getLong(1)+" , name:'"+ marketingLabels[0]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(2)+" , name:'"+ marketingLabels[1]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(3)+" , name:'"+ marketingLabels[2]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(4)+" , name:'"+ marketingLabels[3]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(5)+" , name:'"+ marketingLabels[4]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(6)+" , name:'"+ marketingLabels[5]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(7)+" , name:'"+ marketingLabels[6]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(8)+" , name:'"+ marketingLabels[7]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(9)+" , name:'"+ marketingLabels[8]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(10)+" , name:'"+ marketingLabels[9]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(11)+" , name:'"+ marketingLabels[10]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(12)+" , name:'"+ marketingLabels[11]+"'},");
                }
                rset.close();
                stmt.close();




            } else if (ClientId == 9 || ClientId == 28) {
                //TotalCountVariables
                long TotalMFFirstVisit = 0;
                long TotalMFReturnPat = 0;
                long TotalMFInternetFind = 0;
                long TotalFacebook = 0;
                long TotalMapSearch = 0;
                long TotalGoogleSearch = 0;
                long TotalVERWebsite = 0;
                long TotalWebsiteAds = 0;
                long TotalOnlineReviews = 0;
                long TotalTwitter = 0;
                long TotalLinkedIn = 0;
                long TotalEmailBlast = 0;
                long TotalYouTube = 0;
                long TotalTV = 0;
                long TotalBillboard = 0;
                long TotalRadio = 0;
                long TotalBrochure = 0;
                long TotalDirectMail = 0;
                long TotalCitizensDeTar = 0;
                long TotalLiveWorkNearby = 0;
                long TotalFamilyFriend = 0;
                long TotalUrgentCare = 0;
                long TotalNewspaperMagazine = 0;
                long TotalSchool = 0;
                long TotalHotel = 0;

                String[] marketingLabels = {"First Visit","Return Patient","Internet","Facebook","MapSearch",
                        "Google Search","VERWebsite","Website Ads","Online Reviews","Twitter","LinkedIn ","EmailBlast","YouTube","TV","Billboard","Radio","Brochure","DirectMail","Citizens DeTar",
                        "Live/Work Nearby","Family/Friend","UrgentCare","Newspaper/Magazine","School","Hotel"};


                Query = " Select SUM(MFFirstVisit),SUM(MFReturnPat),SUM(MFInternetFind),SUM(Facebook),SUM(MapSearch),SUM(GoogleSearch),SUM(VERWebsite),SUM(WebsiteAds)," +
                        " SUM(OnlineReviews),SUM(Twitter),SUM(LinkedIn),SUM(EmailBlast),SUM(YouTube),SUM(TV),SUM(Billboard),SUM(Radio),SUM(Brochure),SUM(DirectMail)," +
                        " SUM(CitizensDeTar),SUM(LiveWorkNearby),SUM(FamilyFriend),SUM(UrgentCare),SUM(NewspaperMagazine),SUM(School),SUM(Hotel) " +//25
                        " from " + Database + ".MarketingInfo where CreatedDate between '" + MONTH_START + " 00:00:00' and '" + MONTH_END + " 23:59:59'  ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    marketing_LABEL.append("\"First Visit\",\"Return Patient\",\"Internet\",\"Facebook\",\"MapSearch\",\"Google Search\",\"VERWebsite\",\"Website Ads\",\"Online Reviews\",\"Twitter\",\"LinkedIn \",\"EmailBlast\",\"YouTube\",\"TV\",\"Billboard\",\"Radio\",\"Brochure\",\"DirectMail\",\"Citizens DeTar\",\"Live/Work Nearby\",\"Family/Friend\",\"UrgentCare\",\"Newspaper/Magazine\",\"School\",\"Hotel\"");
                    Marketing_Data.append("{value:"+rset.getLong(1)+" , name:'"+ marketingLabels[0]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(2)+" , name:'"+ marketingLabels[1]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(3)+" , name:'"+ marketingLabels[2]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(4)+" , name:'"+ marketingLabels[3]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(5)+" , name:'"+ marketingLabels[4]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(6)+" , name:'"+ marketingLabels[5]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(7)+" , name:'"+ marketingLabels[6]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(8)+" , name:'"+ marketingLabels[7]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(9)+" , name:'"+ marketingLabels[8]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(10)+" , name:'"+ marketingLabels[9]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(11)+" , name:'"+ marketingLabels[10]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(12)+" , name:'"+ marketingLabels[11]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(13)+" , name:'"+ marketingLabels[12]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(14)+" , name:'"+ marketingLabels[13]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(15)+" , name:'"+ marketingLabels[14]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(16)+" , name:'"+ marketingLabels[15]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(17)+" , name:'"+ marketingLabels[16]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(18)+" , name:'"+ marketingLabels[17]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(19)+" , name:'"+ marketingLabels[18]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(20)+" , name:'"+ marketingLabels[19]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(21)+" , name:'"+ marketingLabels[20]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(22)+" , name:'"+ marketingLabels[21]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(23)+" , name:'"+ marketingLabels[22]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(24)+" , name:'"+ marketingLabels[23]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(25)+" , name:'"+ marketingLabels[24]+"'},");
                }
                rset.close();
                stmt.close();

            } else {

                long TotalReturnPatient = 0;
                long TotalGoogle = 0;
                long TotalMapSearch = 0;
                long TotalBillboard = 0;
                long TotalOnlineReview = 0;
                long TotalTV = 0;
                long TotalWebsite = 0;
                long TotalBuildingSignDriveBy = 0;
                long TotalFacebook = 0;
                long TotalSchool = 0;
                long TotalTwitter = 0;
                long TotalMagazine = 0;
                long TotalNewspaper = 0;
                long TotalFamilyFriend = 0;
                long TotalUrgentCare = 0;
                long TotalCommunityEvent = 0;
                String[] marketingLabels = {"Return Patient" ,"Google","Map Search","Billboard","Online Review","TV","Website","Building Sign/DriveBy","Facebook","School",
                        "Twitter","Magazine","Newspaper","Family/Friend","UrgentCare","Community/Event"};


                Query = " Select SUM(ReturnPatient),SUM(Google),SUM(MapSearch),SUM(Billboard),SUM(OnlineReview),SUM(TV),SUM(Website),SUM(BuildingSignDriveBy)," +
                        " SUM(Facebook),SUM(School),SUM(Twitter),SUM(Magazine),SUM(Newspaper),SUM(FamilyFriend),SUM(UrgentCare),SUM(CommunityEvent) " +//16
                        " from " + Database + ".RandomCheckInfo where CreatedDate between '" + MONTH_START + " 00:00:00' and '" + MONTH_END + " 23:59:59'  ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {

                    marketing_LABEL.append("\"Return Patient\" ,\"Google\",\"Map Search\",\"Billboard\",\"Online Review\",\"TV\",\"Website\",\"Building Sign/DriveBy\",\"Facebook\",\"School\",\"Twitter\",\"Magazine\",\"Newspaper\",\"Family/Friend\",\"UrgentCare\",\"Community/Event\"");
                    Marketing_Data.append("{value:"+rset.getLong(1)+" , name:'"+ marketingLabels[0]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(2)+" , name:'"+ marketingLabels[1]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(3)+" , name:'"+ marketingLabels[2]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(4)+" , name:'"+ marketingLabels[3]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(5)+" , name:'"+ marketingLabels[4]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(6)+" , name:'"+ marketingLabels[5]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(7)+" , name:'"+ marketingLabels[6]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(8)+" , name:'"+ marketingLabels[7]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(9)+" , name:'"+ marketingLabels[8]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(10)+" , name:'"+ marketingLabels[9]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(11)+" , name:'"+ marketingLabels[10]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(12)+" , name:'"+ marketingLabels[11]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(13)+" , name:'"+ marketingLabels[12]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(14)+" , name:'"+ marketingLabels[13]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(15)+" , name:'"+ marketingLabels[14]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(16)+" , name:'"+ marketingLabels[15]+"'},");
                }
                rset.close();
                stmt.close();


            }





//            Query = "SELECT DATE_SUB(LAST_DAY(NOW()),INTERVAL DAY(LAST_DAY(NOW()))- 1 DAY) AS 'FIRST DAY OF CURRENT MONTH', LAST_DAY( now() ), YEAR(NOW())";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                FromDate = rset.getString(1);
//                ToDate = rset.getString(2);
//            }
//            rset.close();
//            stmt.close();

//            Query = "SELECT DATE_FORMAT(NOW(),'%Y-%m-%d'), DATE_FORMAT(NOW(),'%m/%d/%Y')";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                CurrentDate = rset.getString(1);
//                DateNow = rset.getString(2);
//            }
//            rset.close();
//            stmt.close();

            Query = "SELECT * FROM  (SELECT adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date FROM \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t0, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t4) v \n" +
                    "WHERE selected_date >= '" + MONTH_START + "' AND selected_date <= '" + MONTH_END + "' ORDER BY selected_date";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                _FromEndDate = rset.getString(1);
                _ToEndDate = rset.getString(1);
//                Query1 = "Select COUNT(*) from " + Database + ".PatientVisit  WHERE DateOfService >= '" + _FromEndDate + " 00:00:00' and DateOfService <= '" + _ToEndDate + " 23:59:59'";
                Query1 = "Select COUNT(*) from " + Database + ".PatientVisit a INNER JOIN "+Database+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + _FromEndDate + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + _ToEndDate + " 23:59:59' AND b.status=0";;
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                while (rset1.next()) {
                    PatientsCurrentMonthDaily += rset1.getInt(1) + " , ";
                }
                rset1.close();
                stmt1.close();

            }
            rset.close();
            stmt.close();

            if (PatientsCurrentMonthDaily.endsWith(",")) {
                PatientsCurrentMonthDaily = PatientsCurrentMonthDaily.substring(0, PatientsCurrentMonthDaily.length() - 1);
            }

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit where   DateOfService >= '" + FromDate + " 00:00:00' and DateOfService <= '" + ToDate + " 23:59:59'";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountCurrentMonth = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit ";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountOverAll = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit ";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountMale = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientVisit ";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountFemale = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();
//
//            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where HealthInsuranceChk = 0";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountSelfPay = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            } else {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and SelfPayChk = 0";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountSelfPay = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            }
//
//
//            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where HealthInsuranceChk = 1";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountInsured = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            } else {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and SelfPayChk = 1";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountInsured = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            }
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 0 and Age <= 5";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 6 and Age <= 10";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 11 and Age <= 15";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 16 and Age <= 20";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 21 and Age <= 25";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 26 and Age <= 30";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 31 and Age <= 35";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 36 and Age <= 40";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age > 40 ";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//
//            if (PatientCountAgeWise.endsWith(",")) {
//                PatientCountAgeWise = PatientCountAgeWise.substring(0, PatientCountAgeWise.length() - 1);
//            }

            Query = "SELECT YEAR(NOW())";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                CurrentYear = rset.getString(1);
            }
            rset.close();
            stmt.close();

            String[] Months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

            for (int i = 0; i <= 11; i++) {
//                Query = "Select COUNT(*) from " + Database + ".PatientVisit where  DATE_FORMAT(DateOfService,'%Y-%m') = '" + CurrentYear + "-" + Months[i] + "'";
                Query ="Select COUNT(*) from " + Database + ".PatientVisit a INNER JOIN "+Database+".PatientReg b ON a.PatientRegId=b.ID where  DATE_FORMAT(DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s'),'%Y-%m') = '" + CurrentYear + "-" + Months[i] + "' AND b.status=0";;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMonthly += rset.getString(1) + ",";
                }
                rset.close();
                stmt.close();
            }
            if (PatientCountMonthly.endsWith(",")) {
                PatientCountMonthly = PatientCountMonthly.substring(0, PatientCountMonthly.length() - 1);
            }

//            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where WorkersCompPolicyChk = 1 ";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountWC = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            } else {
//                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo where WorkersCompPolicy = 1 ";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountWC = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            }
//
//            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where MotorVehicleAccidentChk = 1 ";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountMVA = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            } else {
//                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo where MotorVehAccident = 1 ";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountMVA = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            }

//            //Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and ltrim(rtrim(UPPER(ReasonVisit))) =  'COVID TESTING'";
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and COVIDStatus = 1";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountCOVID = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();

//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and DATE_FORMAT(DateOfService,'%Y-%m-%d') = '" + CurrentDate + "' ";
////      out.println(Query);
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountToday = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();

//      System.out.println(PatientCountMonthly);

            final Parsehtm Parser = new Parsehtm(request);
//
//            presentDayNumber -=1;
//
//            if(presentDayNumber==0)
//            {
//                presentDayNumber=1;
//            }
            double Patient_Differential = 0.0;
            double Patient_difference = 0;
            double AVG_difference = 0;
            String Differential_badge="";
            double PatientCountOverAll_MONTHLY_AVG = (double)PatientCountOverAll_MONTHLY/(double)presentDayNumber;
            double PatientCountOverAll_LAST_MONTHLY_AVG = (double)PatientCountOverAll_LAST_MONTHLY/(double)30;

            if(PatientCountOverAll_LAST_MONTHLY_AVG==0){
                CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\"> undefined </span> </td>\n");
            }else{
                AVG_difference = ((PatientCountOverAll_MONTHLY_AVG/PatientCountOverAll_LAST_MONTHLY_AVG)*100)-100;

                if(AVG_difference==0){
                    Differential_badge = "<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"float: right;font-size: 100%;border-style: double;\">  "+String.format("%,.0f", AVG_difference)+"%</span> </td>\n";
                }else if (AVG_difference>0){
                    Differential_badge = "<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill bg-success blink_me\" style=\"float: right;font-size: 100%;border-style: double;border-color: aquamarine;\"> +" + String.format("%,.0f", AVG_difference) + "% </span></td>\n";
                }
                else if (AVG_difference<0){
                    Differential_badge = "<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill bg-danger blink_me\" style=\"float: right;font-size: 100%;border-style: double;border-color: #fa0b20;\"> "+String.format("%,.0f", AVG_difference)+"%</span> </td>\n";
                }
            }

//            if(PatientCountOverAll_MONTHLY_AVG > PatientCountOverAll_LAST_MONTHLY_AVG){
//                //INCREASE
//                Patient_difference = PatientCountOverAll_MONTHLY_AVG - PatientCountOverAll_LAST_MONTHLY_AVG;
//                if (PatientCountOverAll_LAST_MONTHLY_AVG==0){
//                    Differential_badge = "<span class=\"badge badge-pill bg-success blink_me\" style=\"float: right; font-size: 100%;border-style: double;border-color: aquamarine;\"> +100% Increase</span>";
//                }else{
//                Patient_Differential = (((double)Patient_difference/(double)PatientCountOverAll_LAST_MONTHLY_AVG))*100;
//                if(Patient_Differential==0){
//                    CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\">  "+String.format("%,.0f", Patient_Differential)+"%</span> </td>\n");
//                }else{
//                    Differential_badge = "<span class=\"badge badge-pill bg-success blink_me\" style=\"float: right; font-size: 100%;border-style: double;border-color: aquamarine;\"> +"+String.format("%,.0f", Patient_Differential)+"% Increase</span>";
//                }
//                }
//            }else{
//                //DECREASE
//                Patient_difference = PatientCountOverAll_LAST_MONTHLY_AVG - PatientCountOverAll_MONTHLY_AVG;
//                if(PatientCountOverAll_MONTHLY_AVG==0){
//                    Differential_badge = "<span class=\"badge badge-pill bg-danger blink_me\" style=\"float: right; font-size: 100%; border-style: double;border-color: #fa0b20;\"> -100% Decrease</span>";
//                }else{
//                    Patient_Differential = (((double)Patient_difference/(double)PatientCountOverAll_MONTHLY_AVG))*100;
//                    if(Patient_Differential==0){
//                        CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\">  "+String.format("%,.0f", Patient_Differential)+"%</span> </td>\n");
//                    }else{
//                        Differential_badge = "<span class=\"badge badge-pill bg-danger blink_me\" style=\"float: right; font-size: 100%; border-style: double;border-color: #fa0b20;\"> -"+String.format("%,.0f", Patient_Differential)+"% Decrease</span>";
//                    }
//                }
//            }
            Parser.SetField("Differential_badge", String.valueOf(Differential_badge));

            //TODAY
            Parser.SetField("PatientCountOverAll_TODAY",String.valueOf(PatientCountOverAll_TODAY));
            Parser.SetField("PatientCountInsured_TODAY",String.valueOf(PatientCountInsured_TODAY));
            Parser.SetField("PatientCountSelfPay_TODAY",String.valueOf(PatientCountSelfPay_TODAY));
            Parser.SetField("PatientCountWC_TODAY",String.valueOf(PatientCountWC_TODAY));
            Parser.SetField("PatientCountMVA_TODAY",String.valueOf(PatientCountMVA_TODAY));
            Parser.SetField("PatientCountCOVID_TODAY",String.valueOf(PatientCountCOVID_TODAY));

            //WEEKLY
            Parser.SetField("PatientCountOverAll_WEEKLY",String.valueOf(PatientCountOverAll_WEEKLY));
            Parser.SetField("PatientCountInsured_WEEKLY",String.valueOf(PatientCountInsured_WEEKLY));
            Parser.SetField("PatientCountSelfPay_WEEKLY",String.valueOf(PatientCountSelfPay_WEEKLY));
            Parser.SetField("PatientCountWC_WEEKLY",String.valueOf(PatientCountWC_WEEKLY));
            Parser.SetField("PatientCountMVA_WEEKLY",String.valueOf(PatientCountMVA_WEEKLY));
            Parser.SetField("PatientCountCOVID_WEEKLY",String.valueOf(PatientCountCOVID_WEEKLY));


            //MONTHLY
            Parser.SetField("PatientCountOverAll_MONTHLY",String.valueOf(PatientCountOverAll_MONTHLY));
            Parser.SetField("PatientCountInsured_MONTHLY",String.valueOf(PatientCountInsured_MONTHLY));
            Parser.SetField("PatientCountSelfPay_MONTHLY",String.valueOf(PatientCountSelfPay_MONTHLY));
            Parser.SetField("PatientCountWC_MONTHLY",String.valueOf(PatientCountWC_MONTHLY));
            Parser.SetField("PatientCountMVA_MONTHLY",String.valueOf(PatientCountMVA_MONTHLY));
            Parser.SetField("PatientCountCOVID_MONTHLY",String.valueOf(PatientCountCOVID_MONTHLY));
//            Parser.SetField("PatientCountOverAll_MONTHLY_AVG",String.valueOf(PatientCountOverAll_MONTHLY_AVG));
            Parser.SetField("PatientCountOverAll_MONTHLY_AVG",String.valueOf(String.format("%,.2f", PatientCountOverAll_MONTHLY_AVG)));


            //LAST MONTH
            Parser.SetField("PatientCountOverAll_LAST_MONTHLY",String.valueOf(PatientCountOverAll_LAST_MONTHLY));
//            Parser.SetField("PatientCountOverAll_LAST_MONTHLY_AVG",String.valueOf(PatientCountOverAll_LAST_MONTHLY_AVG));
            Parser.SetField("PatientCountOverAll_LAST_MONTHLY_AVG",String.valueOf(String.format("%,.2f", PatientCountOverAll_LAST_MONTHLY_AVG)));


            //WEEKLY TREND
            Parser.SetField("PatientsCurrentWeekDaily",String.valueOf(PatientsCurrentWeekDaily));

            //AVERAGE COVID PATIENTS
            if(PatientCountOverAll_MONTHLY==0){
                Parser.SetField("Avg_Covid_Patients",String.valueOf("0"));
            }else {
                int Avg_Covid_Patients = ((PatientCountCOVID_MONTHLY * 100) / PatientCountOverAll_MONTHLY);
                Parser.SetField("Avg_Covid_Patients", String.valueOf(Avg_Covid_Patients));
            }

            //MARKETING
            Parser.SetField("Marketing_Data",String.valueOf(Marketing_Data));
            Parser.SetField("marketing_LABEL",String.valueOf(marketing_LABEL));



//            Parser.SetField("Header", String.valueOf(Header));
//            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
//            Parser.SetField("Footer", String.valueOf(Footer));
//            Parser.SetField("DoctorsList", String.valueOf(DoctorsList));
            Parser.SetField("DoctorsData", String.valueOf(DoctorsData));
            Parser.SetField("DoctorNames", String.valueOf(DoctorNames));
            Parser.SetField("PatientsCurrentMonthDaily", String.valueOf(PatientsCurrentMonthDaily));
//            Parser.SetField("PatientCountCurrentMonth", String.valueOf(PatientCountCurrentMonth));
//            Parser.SetField("PatientCountOverAll", String.valueOf(PatientCountOverAll));
//            Parser.SetField("PatientCountMale", String.valueOf(PatientCountMale));
//            Parser.SetField("PatientCountFemale", String.valueOf(PatientCountFemale));
//            Parser.SetField("PatientCountSelfPay", String.valueOf(PatientCountSelfPay));
//            Parser.SetField("PatientCountInsured", String.valueOf(PatientCountInsured));
//            Parser.SetField("PatientCountAgeWise", String.valueOf(PatientCountAgeWise));
            Parser.SetField("PatientCountMonthly", String.valueOf(PatientCountMonthly));
//            Parser.SetField("PatientCountWC", String.valueOf(PatientCountWC));
//            Parser.SetField("PatientCountMVA", String.valueOf(PatientCountMVA));
//            Parser.SetField("PatientCountCOVID", String.valueOf(PatientCountCOVID));
//            Parser.SetField("PatientCountToday", String.valueOf(PatientCountToday));
//            Parser.SetField("Date", String.valueOf(DateNow));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("FontColor", String.valueOf(FontColor));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ManagementDashboard_copy.html");
        } catch (Exception var11) {
            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void DashBoardDateWise(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId, String FontColor) {
        SupportiveMethods suppMethods = new SupportiveMethods();

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        String Query1 = "";
//        int PatientCount = 0;
        String FromDate = "";
        String ToDate = "";
        String _FromEndDate = "";
        String _ToEndDate = "";
        StringBuffer CDRList = new StringBuffer();

//        StringBuffer DoctorsList = new StringBuffer();
        StringBuffer DoctorsData = new StringBuffer();
        StringBuffer DoctorNames = new StringBuffer();

        StringBuffer Marketing_Data = new StringBuffer();
        StringBuffer marketing_LABEL = new StringBuffer();

        String PatientsCurrentMonthDaily = "";
        String PatientCountAgeWise = "";
        String CurrentYear = "";
        String DateNow = "";
        String PatientCountMonthly = "";
        String CurrentDate = "";
        int PatientCountCurrentMonth = 0;
        int PatientCountOverAll = 0;
        int PatientCountMale = 0;
        int PatientCountFemale = 0;
        int PatientCountSelfPay = 0;
        int PatientCountInsured = 0;
        int PatientCountWC = 0;
        int PatientCountMVA = 0;
        int PatientCountCOVID = 0;

        int PatientCountToday = 0;


        String TODAY = "";
        int PatientCountOverAll_TODAY = 0;
        int PatientCountInsured_TODAY = 0;
        int PatientCountSelfPay_TODAY = 0;
        int PatientCountWC_TODAY = 0;
        int PatientCountMVA_TODAY = 0;
        int PatientCountCOVID_TODAY = 0;



        String WEEK_START = "";
        String WEEK_END = "";
        int PatientCountOverAll_WEEKLY = 0;
        int PatientCountInsured_WEEKLY = 0;
        int PatientCountSelfPay_WEEKLY = 0;
        int PatientCountWC_WEEKLY = 0;
        int PatientCountMVA_WEEKLY = 0;
        int PatientCountCOVID_WEEKLY = 0;

        String MONTH_START = "";
        String MONTH_END = "";
        int PatientCountOverAll_MONTHLY = 0;
        int PatientCountInsured_MONTHLY = 0;
        int PatientCountSelfPay_MONTHLY = 0;
        int PatientCountWC_MONTHLY = 0;
        int PatientCountMVA_MONTHLY = 0;
        int PatientCountCOVID_MONTHLY= 0;

        String LAST_MONTH_START = "";
        String LAST_MONTH_END = "";
        int PatientCountOverAll_LAST_MONTHLY = 0;

        String PatientsCurrentWeekDaily = "";

        int presentDayNumber=0;


        try {

            /*----------------------------------------------------------------------------------------------
             *                             Getting Time Variables
             * ---------------------------------------------------------------------------------------------
             */
                Query = "Select DATE_FORMAT(NOW(),'%Y-%m-%d'), " +
                        "DATE_ADD(DATE_FORMAT(NOW(),'%Y-%m-%d'), INTERVAL(1-DAYOFWEEK(DATE_FORMAT(NOW(),'%Y-%m-%d'))) DAY), " +
                        "DATE_ADD(DATE_ADD(DATE_FORMAT(NOW(),'%Y-%m-%d'), INTERVAL(1-DAYOFWEEK(DATE_FORMAT(NOW(),'%Y-%m-%d'))) DAY),INTERVAL 6 DAY), " +
                        "DATE_SUB(LAST_DAY(NOW()),INTERVAL DAY(LAST_DAY(NOW()))- 1 DAY) AS 'FIRST DAY OF CURRENT MONTH', " +
                        "LAST_DAY(NOW())," +
                        "DATE_SUB(LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH),INTERVAL DAY(LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH))- 1 DAY) AS 'FIRST DAY OF LAST MONTH'," +
                        "LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH )," +
                        " Date_format(NOW(),'%d'),";


                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    TODAY = rset.getString(1);
                    WEEK_START = rset.getString(2);
                    WEEK_END = rset.getString(3);
                    MONTH_START = rset.getString(4);
                    MONTH_END = rset.getString(5);
                    LAST_MONTH_START = rset.getString(6);
                    LAST_MONTH_END = rset.getString(7);
                    presentDayNumber = rset.getInt(8);
                }
                rset.close();
                stmt.close();



            /*----------------------------------------------------------------------------------------------
             *                              TODAY
             * ---------------------------------------------------------------------------------------------
             */

            Query = "Select COUNT(*) from " + Database + ".PatientVisit a INNER JOIN "+Database+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59' AND b.status=0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_TODAY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            //INSURED
            if (ClientId == 9 || ClientId == 28) {
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where d.HealthInsuranceChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountInsured_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                        "where PatientReg.Status = 0 and PatientReg.SelfPayChk = 1 and " +
                        "PatientVisit.DateOfService >= '" + TODAY + " 00:00:00' and PatientVisit.DateOfService <= '" + TODAY + " 23:59:59'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountInsured_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //SELFPAY
            if (ClientId == 9 || ClientId == 28) {
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where d.HealthInsuranceChk = 0 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountSelfPay_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                        "where PatientReg.Status = 0 and PatientReg.SelfPayChk = 0 and " +
                        "PatientVisit.DateOfService >= '" + TODAY + " 00:00:00' and PatientVisit.DateOfService <= '" + TODAY + " 23:59:59'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountSelfPay_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //MVA
            if (ClientId == 9 || ClientId == 28) {
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d " +
                        "LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        "  where MotorVehicleAccidentChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMVA_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo d " +
                        "LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId" +
                        " INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where MotorVehAccident = 1 " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMVA_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //WC
            if (ClientId == 9 || ClientId == 28) {
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId  " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where WorkersCompPolicyChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountWC_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo d " +
                        "LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        " INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where WorkersCompPolicy = 1 " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountWC_TODAY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //COVID
            Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                    "where PatientReg.Status = 0 and PatientReg.COVIDStatus = 1 and " +
                    "PatientVisit.DateOfService >= '" + TODAY + " 00:00:00' and PatientVisit.DateOfService <= '" + TODAY + " 23:59:59'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountCOVID_TODAY = rset.getInt(1);
            }
            rset.close();
            stmt.close();





            /*----------------------------------------------------------------------------------------------
             *                              WEEKLY
             * ---------------------------------------------------------------------------------------------
             */

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit where DateOfService >= '" + WEEK_START + " 00:00:00' and DateOfService <= '" + WEEK_END + " 23:59:59'";
            Query = "Select COUNT(*) from " + Database + ".PatientVisit a INNER JOIN "+Database+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59' AND b.status=0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_WEEKLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            //INSURED
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId " +
//                        "where PatientReg_Details.HealthInsuranceChk = 1 \n" +
//                        "and  PatientVisit.DateOfService >= '" + WEEK_START + " 00:00:00' and PatientVisit.DateOfService <= '" + WEEK_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where d.HealthInsuranceChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountInsured_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".PatientReg " +
                        "LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                        "where PatientReg.Status = 0 and PatientReg.SelfPayChk = 1 and " +
                        "PatientVisit.DateOfService >= '" + WEEK_START + " 00:00:00' and PatientVisit.DateOfService <= '" + WEEK_END + " 23:59:59'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountInsured_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //SELFPAY
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId " +
//                        "where PatientReg_Details.HealthInsuranceChk = 0 \n" +
//                        "and  PatientVisit.DateOfService >= '" + WEEK_START + " 00:00:00' and PatientVisit.DateOfService <= '" + WEEK_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where d.HealthInsuranceChk = 0 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountSelfPay_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                        "where PatientReg.Status = 0 and PatientReg.SelfPayChk = 0 and " +
                        "PatientVisit.DateOfService >= '" + WEEK_START + " 00:00:00' and PatientVisit.DateOfService <= '" + WEEK_END + " 23:59:59'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountSelfPay_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //MVA
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN  " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId   where MotorVehicleAccidentChk = 1 \n" +
//                        "and  PatientVisit.DateOfService >= '" + WEEK_START + " 00:00:00' and PatientVisit.DateOfService <= '" + WEEK_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d " +
                        "LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        "  where MotorVehicleAccidentChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMVA_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo d" +
                        " LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId" +
                        " INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where MotorVehAccident = 1 " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMVA_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //WC
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN  " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId   where WorkersCompPolicyChk = 1 \n" +
//                        "and  PatientVisit.DateOfService >= '" + WEEK_START + " 00:00:00' and PatientVisit.DateOfService <= '" +  + " 23:59:59'";

                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId  " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where WorkersCompPolicyChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountWC_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo d" +
                        " LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        " INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where WorkersCompPolicy = 1 " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountWC_WEEKLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //COVID
            Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                    "where PatientReg.Status = 0 and PatientReg.COVIDStatus = 1 and " +
                    "PatientVisit.DateOfService >= '" + WEEK_START + " 00:00:00' and PatientVisit.DateOfService <= '" + WEEK_END + " 23:59:59'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountCOVID_WEEKLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();




            /*----------------------------------------------------------------------------------------------
             *                              MONTHLY
             * ---------------------------------------------------------------------------------------------
             */

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit where DateOfService >= '" + MONTH_START + " 00:00:00' and DateOfService <= '" + MONTH_END + " 23:59:59'";
            Query = "Select COUNT(*) from " + Database + ".PatientVisit a INNER JOIN "+Database+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' AND b.status=0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_MONTHLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            //INSURED
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId " +
//                        "where PatientReg_Details.HealthInsuranceChk = 1 \n" +
//                        "and  PatientVisit.DateOfService >= '" + MONTH_START + " 00:00:00' and PatientVisit.DateOfService <= '" + MONTH_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where d.HealthInsuranceChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountInsured_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                        "where PatientReg.Status = 0 and PatientReg.SelfPayChk = 1 and " +
                        "PatientVisit.DateOfService >= '" + MONTH_START + " 00:00:00' and PatientVisit.DateOfService <= '" + MONTH_END + " 23:59:59'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountInsured_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //SELFPAY
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId " +
//                        "where PatientReg_Details.HealthInsuranceChk = 0 \n" +
//                        "and  PatientVisit.DateOfService >= '" + MONTH_START + " 00:00:00' and PatientVisit.DateOfService <= '" + MONTH_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where d.HealthInsuranceChk = 0 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountSelfPay_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                        "where PatientReg.Status = 0 and PatientReg.SelfPayChk = 0 and " +
                        "PatientVisit.DateOfService >= '" + MONTH_START + " 00:00:00' and PatientVisit.DateOfService <= '" + MONTH_END + " 23:59:59'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountSelfPay_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //MVA
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN  " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId   where MotorVehicleAccidentChk = 1 \n" +
//                        "and  PatientVisit.DateOfService >= '" + MONTH_START + " 00:00:00' and PatientVisit.DateOfService <= '" + MONTH_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d " +
                        "LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        "  where MotorVehicleAccidentChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00'" +
                        " and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMVA_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo d" +
                        " LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId" +
                        " INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where MotorVehAccident = 1 " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMVA_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //WC
            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details LEFT JOIN  " + Database + ".PatientVisit ON PatientReg_Details.PatientRegId=PatientVisit.PatientRegId   where WorkersCompPolicyChk = 1 \n" +
//                        "and  PatientVisit.DateOfService >= '" + MONTH_START + " 00:00:00' and PatientVisit.DateOfService <= '" + MONTH_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details d" +
                        " LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId  " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where WorkersCompPolicyChk = 1 \n" +
                        "and  DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountWC_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo d" +
                        " LEFT JOIN  " + Database + ".PatientVisit a ON d.PatientRegId=a.PatientRegId " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID" +
                        " where WorkersCompPolicy = 1 " +
                        "and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59'" +
                        " AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountWC_MONTHLY = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            //COVID
            Query = "Select COUNT(*) from " + Database + ".PatientReg LEFT JOIN " + Database + ".PatientVisit ON PatientReg.ID=PatientVisit.PatientRegId \n" +
                    "where PatientReg.Status = 0 and PatientReg.COVIDStatus = 1 and " +
                    "PatientVisit.DateOfService >= '" + MONTH_START + " 00:00:00' and PatientVisit.DateOfService <= '" + MONTH_END + " 23:59:59'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountCOVID_MONTHLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            /*----------------------------------------------------------------------------------------------
             *                             LAST MONTH
             * ---------------------------------------------------------------------------------------------
             */

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit where DateOfService >= '" + LAST_MONTH_START + " 00:00:00' and DateOfService <= '" + LAST_MONTH_END + " 23:59:59'";
            Query = "Select COUNT(*) from " + Database + ".PatientVisit a INNER JOIN "+Database+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + LAST_MONTH_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + LAST_MONTH_END + " 23:59:59' AND b.status=0";;

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_LAST_MONTHLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            /*----------------------------------------------------------------------------------------------
             *                             WEEKLY TREND
             * ---------------------------------------------------------------------------------------------
             */
            Query = "SELECT * FROM  (SELECT adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date FROM \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t0, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t4) v \n" +
                    "WHERE selected_date >= '" + WEEK_START + "' AND selected_date <= '" + WEEK_END + "' ORDER BY selected_date";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                _FromEndDate = rset.getString(1);
                _ToEndDate = rset.getString(1);
//                Query1 = "Select COUNT(*) from " + Database + ".PatientVisit  WHERE DateOfService >= '" + _FromEndDate + " 00:00:00' and DateOfService <= '" + _ToEndDate + " 23:59:59'";
                Query1 = "Select COUNT(*) from " + Database + ".PatientVisit a INNER JOIN "+Database+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + _FromEndDate + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + _ToEndDate + " 23:59:59' AND b.status=0";;
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                while (rset1.next()) {
                    PatientsCurrentWeekDaily += rset1.getInt(1) + " , ";
                }
                rset1.close();
                stmt1.close();

            }
            rset.close();
            stmt.close();

            if (PatientsCurrentWeekDaily.endsWith(",")) {
                PatientsCurrentWeekDaily = PatientsCurrentWeekDaily.substring(0, PatientsCurrentWeekDaily.length() - 1);
            }





//            Query = "Select Id,Concat(DoctorsLastName, ',', DoctorsFirstName) from " + Database + ".DoctorsList where Status = 1";
//            //out.println(Query);
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            while (rset.next()) {
//                DoctorsList.append("<div class=\"d-flex align-items-center justify-content-between my-15 pr-20\">");
//                DoctorsList.append("<h5 class=\"my-0\"><i class=\"mr-5 w-20 fa fa-user-md\"></i>" + rset.getString(2) + "</h5>");
//
//                Query1 = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and DoctorsName = " + rset.getInt(1);
//                stmt1 = conn.createStatement();
//                rset1 = stmt1.executeQuery(Query1);
//                if (rset1.next()) {
//                    DoctorsList.append("<p class=\"mb-0\">" + rset1.getInt(1) + "</p>");
//                }
//                rset1.close();
//                stmt1.close();
//
//                DoctorsList.append("</div>");
//            }
//            rset.close();
//            stmt.close();

            /*----------------------------------------------------------------------------------------------
             *                             Doctors Data
             * ---------------------------------------------------------------------------------------------
             */
            Query = "Select Id,Concat('Dr.',DoctorsLastName, ', ', DoctorsFirstName) from " + Database + ".DoctorsList where Status = 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Query1 = "Select COUNT(*) from " + Database + ".PatientVisit where  DoctorId = " + rset.getInt(1) + " and  CreatedDate between '" + MONTH_START + " 00:00:00' and '" + MONTH_END + " 23:59:59' ";
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                DoctorNames.append("'"+rset.getString(2)+"',");
                if (rset1.next()) {
                    DoctorsData.append("{value:"+rset1.getString(1)+" , name:'"+rset.getString(2)+"'},");
                }
                rset1.close();
                stmt1.close();


            }
            rset.close();
            stmt.close();

            /*----------------------------------------------------------------------------------------------
             *                            Marketing Data
             * ---------------------------------------------------------------------------------------------
             */

            if (ClientId == 27 || ClientId == 29) {
                String[] marketingLabels = {"VisitedBefore","FamilyVisitedBefore","Internet","Billboard",
                        "Google","BuildingSignage","Facebook","LivesNear","Twitter","Tv","MapSearch","Event"};






                Query = " Select SUM(FrVisitedBefore),SUM(FrFamiliyVisitedBefore),SUM(FrInternet),SUM(FrBillboard),SUM(FrGoogle),SUM(FrBuildingSignage),SUM(FrFacebook),SUM(FrLivesNear)," +
                        " SUM(FrTwitter),SUM(FrTV),SUM(FrMapSearch),SUM(FrEvent) " +//12
                        " from " + Database + ".RandomCheckInfo where CreatedDate between '" + MONTH_START + " 00:00:00' and '" + MONTH_END + " 23:59:59' ";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
//                    marketingLabels[0] = rset.getLong(1);

                    marketing_LABEL.append("\"VisitedBefore\",\"FamilyVisitedBefore\",\"Internet\",\"Billboard\",\"Google\",\"BuildingSignage\",\"Facebook\",\"LivesNear\",\"Twitter\",\"Tv\",\"MapSearch\",\"Event\"");
                    Marketing_Data.append("{value:"+rset.getLong(1)+" , name:'"+ marketingLabels[0]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(2)+" , name:'"+ marketingLabels[1]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(3)+" , name:'"+ marketingLabels[2]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(4)+" , name:'"+ marketingLabels[3]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(5)+" , name:'"+ marketingLabels[4]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(6)+" , name:'"+ marketingLabels[5]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(7)+" , name:'"+ marketingLabels[6]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(8)+" , name:'"+ marketingLabels[7]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(9)+" , name:'"+ marketingLabels[8]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(10)+" , name:'"+ marketingLabels[9]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(11)+" , name:'"+ marketingLabels[10]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(12)+" , name:'"+ marketingLabels[11]+"'},");
                }
                rset.close();
                stmt.close();




            } else if (ClientId == 9 || ClientId == 28) {
                //TotalCountVariables
                long TotalMFFirstVisit = 0;
                long TotalMFReturnPat = 0;
                long TotalMFInternetFind = 0;
                long TotalFacebook = 0;
                long TotalMapSearch = 0;
                long TotalGoogleSearch = 0;
                long TotalVERWebsite = 0;
                long TotalWebsiteAds = 0;
                long TotalOnlineReviews = 0;
                long TotalTwitter = 0;
                long TotalLinkedIn = 0;
                long TotalEmailBlast = 0;
                long TotalYouTube = 0;
                long TotalTV = 0;
                long TotalBillboard = 0;
                long TotalRadio = 0;
                long TotalBrochure = 0;
                long TotalDirectMail = 0;
                long TotalCitizensDeTar = 0;
                long TotalLiveWorkNearby = 0;
                long TotalFamilyFriend = 0;
                long TotalUrgentCare = 0;
                long TotalNewspaperMagazine = 0;
                long TotalSchool = 0;
                long TotalHotel = 0;

                String[] marketingLabels = {"First Visit","Return Patient","Internet","Facebook","MapSearch",
                        "Google Search","VERWebsite","Website Ads","Online Reviews","Twitter","LinkedIn ","EmailBlast","YouTube","TV","Billboard","Radio","Brochure","DirectMail","Citizens DeTar",
                        "Live/Work Nearby","Family/Friend","UrgentCare","Newspaper/Magazine","School","Hotel"};


                Query = " Select SUM(MFFirstVisit),SUM(MFReturnPat),SUM(MFInternetFind),SUM(Facebook),SUM(MapSearch),SUM(GoogleSearch),SUM(VERWebsite),SUM(WebsiteAds)," +
                        " SUM(OnlineReviews),SUM(Twitter),SUM(LinkedIn),SUM(EmailBlast),SUM(YouTube),SUM(TV),SUM(Billboard),SUM(Radio),SUM(Brochure),SUM(DirectMail)," +
                        " SUM(CitizensDeTar),SUM(LiveWorkNearby),SUM(FamilyFriend),SUM(UrgentCare),SUM(NewspaperMagazine),SUM(School),SUM(Hotel) " +//25
                        " from " + Database + ".MarketingInfo where CreatedDate between '" + MONTH_START + " 00:00:00' and '" + MONTH_END + " 23:59:59'  ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    marketing_LABEL.append("\"First Visit\",\"Return Patient\",\"Internet\",\"Facebook\",\"MapSearch\",\"Google Search\",\"VERWebsite\",\"Website Ads\",\"Online Reviews\",\"Twitter\",\"LinkedIn \",\"EmailBlast\",\"YouTube\",\"TV\",\"Billboard\",\"Radio\",\"Brochure\",\"DirectMail\",\"Citizens DeTar\",\"Live/Work Nearby\",\"Family/Friend\",\"UrgentCare\",\"Newspaper/Magazine\",\"School\",\"Hotel\"");
                    Marketing_Data.append("{value:"+rset.getLong(1)+" , name:'"+ marketingLabels[0]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(2)+" , name:'"+ marketingLabels[1]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(3)+" , name:'"+ marketingLabels[2]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(4)+" , name:'"+ marketingLabels[3]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(5)+" , name:'"+ marketingLabels[4]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(6)+" , name:'"+ marketingLabels[5]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(7)+" , name:'"+ marketingLabels[6]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(8)+" , name:'"+ marketingLabels[7]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(9)+" , name:'"+ marketingLabels[8]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(10)+" , name:'"+ marketingLabels[9]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(11)+" , name:'"+ marketingLabels[10]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(12)+" , name:'"+ marketingLabels[11]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(13)+" , name:'"+ marketingLabels[12]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(14)+" , name:'"+ marketingLabels[13]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(15)+" , name:'"+ marketingLabels[14]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(16)+" , name:'"+ marketingLabels[15]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(17)+" , name:'"+ marketingLabels[16]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(18)+" , name:'"+ marketingLabels[17]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(19)+" , name:'"+ marketingLabels[18]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(20)+" , name:'"+ marketingLabels[19]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(21)+" , name:'"+ marketingLabels[20]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(22)+" , name:'"+ marketingLabels[21]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(23)+" , name:'"+ marketingLabels[22]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(24)+" , name:'"+ marketingLabels[23]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(25)+" , name:'"+ marketingLabels[24]+"'},");
                }
                rset.close();
                stmt.close();

            } else {

                long TotalReturnPatient = 0;
                long TotalGoogle = 0;
                long TotalMapSearch = 0;
                long TotalBillboard = 0;
                long TotalOnlineReview = 0;
                long TotalTV = 0;
                long TotalWebsite = 0;
                long TotalBuildingSignDriveBy = 0;
                long TotalFacebook = 0;
                long TotalSchool = 0;
                long TotalTwitter = 0;
                long TotalMagazine = 0;
                long TotalNewspaper = 0;
                long TotalFamilyFriend = 0;
                long TotalUrgentCare = 0;
                long TotalCommunityEvent = 0;
                String[] marketingLabels = {"Return Patient" ,"Google","Map Search","Billboard","Online Review","TV","Website","Building Sign/DriveBy","Facebook","School",
                        "Twitter","Magazine","Newspaper","Family/Friend","UrgentCare","Community/Event"};


                Query = " Select SUM(ReturnPatient),SUM(Google),SUM(MapSearch),SUM(Billboard),SUM(OnlineReview),SUM(TV),SUM(Website),SUM(BuildingSignDriveBy)," +
                        " SUM(Facebook),SUM(School),SUM(Twitter),SUM(Magazine),SUM(Newspaper),SUM(FamilyFriend),SUM(UrgentCare),SUM(CommunityEvent) " +//16
                        " from " + Database + ".RandomCheckInfo where CreatedDate between '" + MONTH_START + " 00:00:00' and '" + MONTH_END + " 23:59:59'  ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {

                    marketing_LABEL.append("\"Return Patient\" ,\"Google\",\"Map Search\",\"Billboard\",\"Online Review\",\"TV\",\"Website\",\"Building Sign/DriveBy\",\"Facebook\",\"School\",\"Twitter\",\"Magazine\",\"Newspaper\",\"Family/Friend\",\"UrgentCare\",\"Community/Event\"");
                    Marketing_Data.append("{value:"+rset.getLong(1)+" , name:'"+ marketingLabels[0]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(2)+" , name:'"+ marketingLabels[1]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(3)+" , name:'"+ marketingLabels[2]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(4)+" , name:'"+ marketingLabels[3]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(5)+" , name:'"+ marketingLabels[4]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(6)+" , name:'"+ marketingLabels[5]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(7)+" , name:'"+ marketingLabels[6]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(8)+" , name:'"+ marketingLabels[7]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(9)+" , name:'"+ marketingLabels[8]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(10)+" , name:'"+ marketingLabels[9]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(11)+" , name:'"+ marketingLabels[10]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(12)+" , name:'"+ marketingLabels[11]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(13)+" , name:'"+ marketingLabels[12]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(14)+" , name:'"+ marketingLabels[13]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(15)+" , name:'"+ marketingLabels[14]+"'},");
                    Marketing_Data.append("{value:"+rset.getLong(16)+" , name:'"+ marketingLabels[15]+"'},");
                }
                rset.close();
                stmt.close();


            }





//            Query = "SELECT DATE_SUB(LAST_DAY(NOW()),INTERVAL DAY(LAST_DAY(NOW()))- 1 DAY) AS 'FIRST DAY OF CURRENT MONTH', LAST_DAY( now() ), YEAR(NOW())";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                FromDate = rset.getString(1);
//                ToDate = rset.getString(2);
//            }
//            rset.close();
//            stmt.close();

//            Query = "SELECT DATE_FORMAT(NOW(),'%Y-%m-%d'), DATE_FORMAT(NOW(),'%m/%d/%Y')";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                CurrentDate = rset.getString(1);
//                DateNow = rset.getString(2);
//            }
//            rset.close();
//            stmt.close();

            Query = "SELECT * FROM  (SELECT adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date FROM \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t0, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t4) v \n" +
                    "WHERE selected_date >= '" + MONTH_START + "' AND selected_date <= '" + MONTH_END + "' ORDER BY selected_date";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                _FromEndDate = rset.getString(1);
                _ToEndDate = rset.getString(1);
//                Query1 = "Select COUNT(*) from " + Database + ".PatientVisit  WHERE DateOfService >= '" + _FromEndDate + " 00:00:00' and DateOfService <= '" + _ToEndDate + " 23:59:59'";
                Query1 = "Select COUNT(*) from " + Database + ".PatientVisit a INNER JOIN "+Database+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + _FromEndDate + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + _ToEndDate + " 23:59:59' AND b.status=0";;
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                while (rset1.next()) {
                    PatientsCurrentMonthDaily += rset1.getInt(1) + " , ";
                }
                rset1.close();
                stmt1.close();

            }
            rset.close();
            stmt.close();

            if (PatientsCurrentMonthDaily.endsWith(",")) {
                PatientsCurrentMonthDaily = PatientsCurrentMonthDaily.substring(0, PatientsCurrentMonthDaily.length() - 1);
            }

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit where   DateOfService >= '" + FromDate + " 00:00:00' and DateOfService <= '" + ToDate + " 23:59:59'";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountCurrentMonth = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit ";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountOverAll = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit ";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountMale = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientVisit ";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountFemale = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();
//
//            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where HealthInsuranceChk = 0";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountSelfPay = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            } else {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and SelfPayChk = 0";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountSelfPay = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            }
//
//
//            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where HealthInsuranceChk = 1";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountInsured = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            } else {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and SelfPayChk = 1";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountInsured = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            }
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 0 and Age <= 5";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 6 and Age <= 10";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 11 and Age <= 15";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 16 and Age <= 20";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 21 and Age <= 25";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 26 and Age <= 30";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 31 and Age <= 35";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 36 and Age <= 40";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age > 40 ";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//
//            if (PatientCountAgeWise.endsWith(",")) {
//                PatientCountAgeWise = PatientCountAgeWise.substring(0, PatientCountAgeWise.length() - 1);
//            }

            Query = "SELECT YEAR(NOW())";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                CurrentYear = rset.getString(1);
            }
            rset.close();
            stmt.close();

            String[] Months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

            for (int i = 0; i <= 11; i++) {
//                Query = "Select COUNT(*) from " + Database + ".PatientVisit where  DATE_FORMAT(DateOfService,'%Y-%m') = '" + CurrentYear + "-" + Months[i] + "'";
                Query ="Select COUNT(*) from " + Database + ".PatientVisit a INNER JOIN "+Database+".PatientReg b ON a.PatientRegId=b.ID where  DATE_FORMAT(DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s'),'%Y-%m') = '" + CurrentYear + "-" + Months[i] + "' AND b.status=0";;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMonthly += rset.getString(1) + ",";
                }
                rset.close();
                stmt.close();
            }
            if (PatientCountMonthly.endsWith(",")) {
                PatientCountMonthly = PatientCountMonthly.substring(0, PatientCountMonthly.length() - 1);
            }

//            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where WorkersCompPolicyChk = 1 ";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountWC = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            } else {
//                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo where WorkersCompPolicy = 1 ";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountWC = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            }
//
//            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where MotorVehicleAccidentChk = 1 ";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountMVA = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            } else {
//                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo where MotorVehAccident = 1 ";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountMVA = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            }

//            //Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and ltrim(rtrim(UPPER(ReasonVisit))) =  'COVID TESTING'";
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and COVIDStatus = 1";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountCOVID = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();

//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and DATE_FORMAT(DateOfService,'%Y-%m-%d') = '" + CurrentDate + "' ";
////      out.println(Query);
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountToday = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();

//      System.out.println(PatientCountMonthly);

            final Parsehtm Parser = new Parsehtm(request);
//
//            presentDayNumber -=1;
//
//            if(presentDayNumber==0)
//            {
//                presentDayNumber=1;
//            }
            double Patient_Differential = 0.0;
            double Patient_difference = 0;
            double AVG_difference = 0;
            String Differential_badge="";
            double PatientCountOverAll_MONTHLY_AVG = (double)PatientCountOverAll_MONTHLY/(double)presentDayNumber;
            double PatientCountOverAll_LAST_MONTHLY_AVG = (double)PatientCountOverAll_LAST_MONTHLY/(double)30;

            if(PatientCountOverAll_LAST_MONTHLY_AVG==0){
                CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\"> undefined </span> </td>\n");
            }else{
                AVG_difference = ((PatientCountOverAll_MONTHLY_AVG/PatientCountOverAll_LAST_MONTHLY_AVG)*100)-100;

                if(AVG_difference==0){
                    Differential_badge = "<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"float: right;font-size: 100%;border-style: double;\">  "+String.format("%,.0f", AVG_difference)+"%</span> </td>\n";
                }else if (AVG_difference>0){
                    Differential_badge = "<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill bg-success blink_me\" style=\"float: right;font-size: 100%;border-style: double;border-color: aquamarine;\"> +" + String.format("%,.0f", AVG_difference) + "% </span></td>\n";
                }
                else if (AVG_difference<0){
                    Differential_badge = "<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill bg-danger blink_me\" style=\"float: right;font-size: 100%;border-style: double;border-color: #fa0b20;\"> "+String.format("%,.0f", AVG_difference)+"%</span> </td>\n";
                }
            }

//            if(PatientCountOverAll_MONTHLY_AVG > PatientCountOverAll_LAST_MONTHLY_AVG){
//                //INCREASE
//                Patient_difference = PatientCountOverAll_MONTHLY_AVG - PatientCountOverAll_LAST_MONTHLY_AVG;
//                if (PatientCountOverAll_LAST_MONTHLY_AVG==0){
//                    Differential_badge = "<span class=\"badge badge-pill bg-success blink_me\" style=\"float: right; font-size: 100%;border-style: double;border-color: aquamarine;\"> +100% Increase</span>";
//                }else{
//                Patient_Differential = (((double)Patient_difference/(double)PatientCountOverAll_LAST_MONTHLY_AVG))*100;
//                if(Patient_Differential==0){
//                    CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\">  "+String.format("%,.0f", Patient_Differential)+"%</span> </td>\n");
//                }else{
//                    Differential_badge = "<span class=\"badge badge-pill bg-success blink_me\" style=\"float: right; font-size: 100%;border-style: double;border-color: aquamarine;\"> +"+String.format("%,.0f", Patient_Differential)+"% Increase</span>";
//                }
//                }
//            }else{
//                //DECREASE
//                Patient_difference = PatientCountOverAll_LAST_MONTHLY_AVG - PatientCountOverAll_MONTHLY_AVG;
//                if(PatientCountOverAll_MONTHLY_AVG==0){
//                    Differential_badge = "<span class=\"badge badge-pill bg-danger blink_me\" style=\"float: right; font-size: 100%; border-style: double;border-color: #fa0b20;\"> -100% Decrease</span>";
//                }else{
//                    Patient_Differential = (((double)Patient_difference/(double)PatientCountOverAll_MONTHLY_AVG))*100;
//                    if(Patient_Differential==0){
//                        CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\">  "+String.format("%,.0f", Patient_Differential)+"%</span> </td>\n");
//                    }else{
//                        Differential_badge = "<span class=\"badge badge-pill bg-danger blink_me\" style=\"float: right; font-size: 100%; border-style: double;border-color: #fa0b20;\"> -"+String.format("%,.0f", Patient_Differential)+"% Decrease</span>";
//                    }
//                }
//            }
            Parser.SetField("Differential_badge", String.valueOf(Differential_badge));

            //TODAY
            Parser.SetField("PatientCountOverAll_TODAY",String.valueOf(PatientCountOverAll_TODAY));
            Parser.SetField("PatientCountInsured_TODAY",String.valueOf(PatientCountInsured_TODAY));
            Parser.SetField("PatientCountSelfPay_TODAY",String.valueOf(PatientCountSelfPay_TODAY));
            Parser.SetField("PatientCountWC_TODAY",String.valueOf(PatientCountWC_TODAY));
            Parser.SetField("PatientCountMVA_TODAY",String.valueOf(PatientCountMVA_TODAY));
            Parser.SetField("PatientCountCOVID_TODAY",String.valueOf(PatientCountCOVID_TODAY));

            //WEEKLY
            Parser.SetField("PatientCountOverAll_WEEKLY",String.valueOf(PatientCountOverAll_WEEKLY));
            Parser.SetField("PatientCountInsured_WEEKLY",String.valueOf(PatientCountInsured_WEEKLY));
            Parser.SetField("PatientCountSelfPay_WEEKLY",String.valueOf(PatientCountSelfPay_WEEKLY));
            Parser.SetField("PatientCountWC_WEEKLY",String.valueOf(PatientCountWC_WEEKLY));
            Parser.SetField("PatientCountMVA_WEEKLY",String.valueOf(PatientCountMVA_WEEKLY));
            Parser.SetField("PatientCountCOVID_WEEKLY",String.valueOf(PatientCountCOVID_WEEKLY));


            //MONTHLY
            Parser.SetField("PatientCountOverAll_MONTHLY",String.valueOf(PatientCountOverAll_MONTHLY));
            Parser.SetField("PatientCountInsured_MONTHLY",String.valueOf(PatientCountInsured_MONTHLY));
            Parser.SetField("PatientCountSelfPay_MONTHLY",String.valueOf(PatientCountSelfPay_MONTHLY));
            Parser.SetField("PatientCountWC_MONTHLY",String.valueOf(PatientCountWC_MONTHLY));
            Parser.SetField("PatientCountMVA_MONTHLY",String.valueOf(PatientCountMVA_MONTHLY));
            Parser.SetField("PatientCountCOVID_MONTHLY",String.valueOf(PatientCountCOVID_MONTHLY));
//            Parser.SetField("PatientCountOverAll_MONTHLY_AVG",String.valueOf(PatientCountOverAll_MONTHLY_AVG));
            Parser.SetField("PatientCountOverAll_MONTHLY_AVG",String.valueOf(String.format("%,.2f", PatientCountOverAll_MONTHLY_AVG)));


            //LAST MONTH
            Parser.SetField("PatientCountOverAll_LAST_MONTHLY",String.valueOf(PatientCountOverAll_LAST_MONTHLY));
//            Parser.SetField("PatientCountOverAll_LAST_MONTHLY_AVG",String.valueOf(PatientCountOverAll_LAST_MONTHLY_AVG));
            Parser.SetField("PatientCountOverAll_LAST_MONTHLY_AVG",String.valueOf(String.format("%,.2f", PatientCountOverAll_LAST_MONTHLY_AVG)));


            //WEEKLY TREND
            Parser.SetField("PatientsCurrentWeekDaily",String.valueOf(PatientsCurrentWeekDaily));

            //AVERAGE COVID PATIENTS
            if(PatientCountOverAll_MONTHLY==0){
                Parser.SetField("Avg_Covid_Patients",String.valueOf("0"));
            }else {
                int Avg_Covid_Patients = ((PatientCountCOVID_MONTHLY * 100) / PatientCountOverAll_MONTHLY);
                Parser.SetField("Avg_Covid_Patients", String.valueOf(Avg_Covid_Patients));
            }

            //MARKETING
            Parser.SetField("Marketing_Data",String.valueOf(Marketing_Data));
            Parser.SetField("marketing_LABEL",String.valueOf(marketing_LABEL));



//            Parser.SetField("Header", String.valueOf(Header));
//            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
//            Parser.SetField("Footer", String.valueOf(Footer));
//            Parser.SetField("DoctorsList", String.valueOf(DoctorsList));
            Parser.SetField("DoctorsData", String.valueOf(DoctorsData));
            Parser.SetField("DoctorNames", String.valueOf(DoctorNames));
            Parser.SetField("PatientsCurrentMonthDaily", String.valueOf(PatientsCurrentMonthDaily));
//            Parser.SetField("PatientCountCurrentMonth", String.valueOf(PatientCountCurrentMonth));
//            Parser.SetField("PatientCountOverAll", String.valueOf(PatientCountOverAll));
//            Parser.SetField("PatientCountMale", String.valueOf(PatientCountMale));
//            Parser.SetField("PatientCountFemale", String.valueOf(PatientCountFemale));
//            Parser.SetField("PatientCountSelfPay", String.valueOf(PatientCountSelfPay));
//            Parser.SetField("PatientCountInsured", String.valueOf(PatientCountInsured));
//            Parser.SetField("PatientCountAgeWise", String.valueOf(PatientCountAgeWise));
            Parser.SetField("PatientCountMonthly", String.valueOf(PatientCountMonthly));
//            Parser.SetField("PatientCountWC", String.valueOf(PatientCountWC));
//            Parser.SetField("PatientCountMVA", String.valueOf(PatientCountMVA));
//            Parser.SetField("PatientCountCOVID", String.valueOf(PatientCountCOVID));
//            Parser.SetField("PatientCountToday", String.valueOf(PatientCountToday));
//            Parser.SetField("Date", String.valueOf(DateNow));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("FontColor", String.valueOf(FontColor));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/ManagementDashboard_copy_REPORT.html");
        } catch (Exception var11) {
            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
    }

}
