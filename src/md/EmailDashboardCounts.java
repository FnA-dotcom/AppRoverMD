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
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

//import com.itextpdf.text.pdf.PdfWriter;

@SuppressWarnings("Duplicates")
public class EmailDashboardCounts extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
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

            UtilityHelper helper = new UtilityHelper();






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
//                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Management Dashboard", "View Management Dashboard", FacilityIndex);
                this.GetInput(request, out, conn,context);
            }
            else if (ActionID.equals("DateFilter")) {
//                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Management Dashboard", "View Management Dashboard", FacilityIndex);
                this.DateFilter(request, out, conn,context);
            } else {

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


    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext) {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

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
        String Yesterday = "";
        String WEEK_START = "";
        String WEEK_END = "";

        String MONTH_START = "";
        String MONTH_END = "";
        String LAST_MONTH_START = "";
        String LAST_MONTH_END = "";

        int PatientCountOverAll_TODAY = 0;
        int PatientCountOverAll_TODAY_TOTAL = 0;
        int PatientCountOverAll_YESTERDAY = 0;
        int PatientCountOverAll_YESTERDAY_TOTAL = 0;

        int PatientCountOverAll_WEEKLY = 0;
        int PatientCountOverAll_WEEKLY_TOTAL = 0;

        int PatientCountOverAll_MONTHLY = 0;
        int PatientCountOverAll_MONTHLY_TOTAL = 0;

        int PatientCountOverAll_LAST_MONTHLY = 0;
        int PatientCountOverAll_LAST_MONTHLY_TOTAL = 0;

        String PatientsCurrentWeekDaily = "";

        int presentDayNumber=0;


        String[] LiveFacilities = {"victoria", "oe_2", "nacogdoches", "longview",  "oddasa","ER_Dallas", "frontlin_er","richmond"};

        double Patient_Differential=0.0;



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
                    " Date_format(NOW(),'%d')," +
                    "DATE_SUB(CURDATE(), INTERVAL 1 DAY) AS yesterday_date";


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
                Yesterday = rset.getString(9);
            }
            rset.close();
            stmt.close();

//            presentDayNumber -=1;



            for (int i = 0; i < LiveFacilities.length ; i++) {
                CDRList.append("<tr>\n");


                //Getting Name of Facilities
                Query = "Select Name from oe.clients where dbname='"+LiveFacilities[i]+"'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    CDRList.append("<td style=\"border: 1px solid black;\" >" + rset.getString(1) + "</td>\n");
                }
                rset.close();
                stmt.close();
            /*----------------------------------------------------------------------------------------------
             *                              TODAY
             * ---------------------------------------------------------------------------------------------
             */

//            Query = "Select COUNT(*) from " + LiveFacilities[i] + ".PatientVisit where CreatedDate >= '" + TODAY + " 00:00:00' and CreatedDate <= '" + TODAY + " 23:59:59'";
             Query = "Select COUNT(*) from " + LiveFacilities[i] + ".PatientVisit a INNER JOIN "+LiveFacilities[i]+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59' AND b.status=0";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_TODAY = rset.getInt(1);
                PatientCountOverAll_TODAY_TOTAL += rset.getInt(1);
                CDRList.append("<td style=\"border: 1px solid black;\" >" + PatientCountOverAll_TODAY + "</td>\n");

            }
            rset.close();
            stmt.close();

            /*----------------------------------------------------------------------------------------------
             *                              Yesterday
             * ---------------------------------------------------------------------------------------------
             */

//                Query = "Select COUNT(*) from " + LiveFacilities[i]  + ".PatientVisit where CreatedDate >= '" + Yesterday + " 00:00:00' and CreatedDate <= '" + Yesterday + " 23:59:59'";
                Query = "Select COUNT(*) from " + LiveFacilities[i] + ".PatientVisit a INNER JOIN "+LiveFacilities[i]+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + Yesterday + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + Yesterday + " 23:59:59' AND b.status=0";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountOverAll_YESTERDAY=rset.getInt(1);
                    PatientCountOverAll_YESTERDAY_TOTAL+=rset.getInt(1);
                    CDRList.append("<td  style=\"border: 1px solid black;\" >" + rset.getInt(1) + "</td>\n");

                }
                rset.close();
                stmt.close();





            /*----------------------------------------------------------------------------------------------
             *                              WEEKLY
             * ---------------------------------------------------------------------------------------------
             */

//            Query = "Select COUNT(*) from " + LiveFacilities[i]  + ".PatientVisit where CreatedDate >= '" + WEEK_START + " 00:00:00' and CreatedDate <= '" + WEEK_END + " 23:59:59'";
            Query = "Select COUNT(*) from " + LiveFacilities[i] + ".PatientVisit a INNER JOIN "+LiveFacilities[i]+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59' AND b.status=0";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_WEEKLY=rset.getInt(1);
                PatientCountOverAll_WEEKLY_TOTAL+=rset.getInt(1);
                CDRList.append("<td style=\"border: 1px solid black;\" >" + rset.getInt(1) + "</td>\n");
            }
            rset.close();
            stmt.close();

            /*----------------------------------------------------------------------------------------------
             *                              MONTHLY
             * ---------------------------------------------------------------------------------------------
             */

//            Query = "Select COUNT(*) from " + LiveFacilities[i]  + ".PatientVisit where CreatedDate >= '" + MONTH_START + " 00:00:00' and CreatedDate <= '" + MONTH_END + " 23:59:59'";
            Query = "Select COUNT(*) from " + LiveFacilities[i] + ".PatientVisit a INNER JOIN "+LiveFacilities[i]+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' AND b.status=0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_MONTHLY =  rset.getInt(1);
                PatientCountOverAll_MONTHLY_TOTAL +=  rset.getInt(1);
                CDRList.append("<td style=\"border: 1px solid black;\" >" + rset.getInt(1) + "</td>\n");
            }
            rset.close();
            stmt.close();


            /*----------------------------------------------------------------------------------------------
             *                             LAST MONTH
             * ---------------------------------------------------------------------------------------------
             */

//            Query = "Select COUNT(*) from " + LiveFacilities[i]  + ".PatientVisit where CreatedDate >= '" + LAST_MONTH_START + " 00:00:00' and CreatedDate <= '" + LAST_MONTH_END + " 23:59:59'";
            Query = "Select COUNT(*) from " + LiveFacilities[i] + ".PatientVisit a INNER JOIN "+LiveFacilities[i]+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + LAST_MONTH_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + LAST_MONTH_END + " 23:59:59' AND b.status=0";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll_LAST_MONTHLY = rset.getInt(1);
                PatientCountOverAll_LAST_MONTHLY_TOTAL += rset.getInt(1);
                CDRList.append("<td style=\"border: 1px solid black;\" >" + rset.getInt(1) + "</td>\n");
            }
            rset.close();
            stmt.close();


                Patient_Differential = 0.0;

                double Patient_difference = 0;
                double AVG_difference = 0;
                double PatientCountOverAll_MONTHLY_AVG = (double)PatientCountOverAll_MONTHLY/(double)presentDayNumber;
                double PatientCountOverAll_LAST_MONTHLY_AVG = (double)PatientCountOverAll_LAST_MONTHLY/(double)30;

                if(PatientCountOverAll_LAST_MONTHLY_AVG==0){
                    CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\"> undefined </span> </td>\n");
                }else{
                    AVG_difference = ((PatientCountOverAll_MONTHLY_AVG/PatientCountOverAll_LAST_MONTHLY_AVG)*100)-100;

                    if(AVG_difference==0){
                        CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\">  "+String.format("%,.0f", AVG_difference)+"%</span> </td>\n");
                    }else if (AVG_difference>0){
                        CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill bg-success blink_me\" style=\"font-size: 100%;border-style: double;border-color: aquamarine;\"> +" + String.format("%,.0f", AVG_difference) + "% </span></td>\n");
                    }
                    else if (AVG_difference<0){
                        CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill bg-danger blink_me\" style=\"font-size: 100%;border-style: double;border-color: #fa0b20;\"> "+String.format("%,.0f", AVG_difference)+"%</span> </td>\n");
                    }
                }




//                if(PatientCountOverAll_MONTHLY_AVG > PatientCountOverAll_LAST_MONTHLY_AVG){
//                    //INCREASE
//                    Patient_difference = PatientCountOverAll_MONTHLY_AVG - PatientCountOverAll_LAST_MONTHLY_AVG;
//                    if (PatientCountOverAll_LAST_MONTHLY_AVG==0){
//                        CDRList.append("<td style=\"border: 1px solid black;\" ><span class=\"badge badge-pill bg-success blink_me\" style=\"font-size: 100%;border-style: double;border-color: aquamarine;\"> + 100% </span></td>\n");
//
//                    }else{
//                        Patient_Differential = (((double)Patient_difference/(double)PatientCountOverAll_LAST_MONTHLY_AVG))*100;
//                        if(Patient_Differential==0){
//                            CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\">  "+String.format("%,.0f", Patient_Differential)+"%</span> </td>\n");
//                        }else {
//                            CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill bg-success blink_me\" style=\"font-size: 100%;border-style: double;border-color: aquamarine;\"> +" + String.format("%,.0f", Patient_Differential) + "% </span></td>\n");
//                        }
//                    }
//                }else{
//                    //DECREASE
//                    Patient_difference = PatientCountOverAll_LAST_MONTHLY_AVG - PatientCountOverAll_MONTHLY_AVG;
//                    if(PatientCountOverAll_MONTHLY_AVG==0){
//                        CDRList.append("<td ><span class=\"badge badge-pill bg-danger blink_me\" style=\"font-size: 100%;border-style: double;border-color: #fa0b20;\"> - 100%  </span></td>\n");
//                    }else{
//                        Patient_Differential = (((double)Patient_difference/(double)PatientCountOverAll_MONTHLY_AVG))*100;
//                        if(Patient_Differential==0){
//                            CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\">  "+String.format("%,.0f", Patient_Differential)+"%</span> </td>\n");
//                        }else{
//                            CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill bg-danger blink_me\" style=\"font-size: 100%;border-style: double;border-color: #fa0b20;\"> - "+String.format("%,.0f", Patient_Differential)+"%</span> </td>\n");
//                        }
//                    }
//                }

                CDRList.append("<td style=\"border: 1px solid black;\"  >" + String.format("%,.2f", PatientCountOverAll_MONTHLY_AVG)  + "</td>\n");

                CDRList.append("</tr>\n");
            }

            CDRList.append("<tr style=\"border: 4px dotted black;font-weight: 900;\">\n");
            CDRList.append("<td > TOTAL </td>\n");
            CDRList.append("<td  >" + PatientCountOverAll_TODAY_TOTAL + "</td>\n");
            CDRList.append("<td  >" + PatientCountOverAll_YESTERDAY_TOTAL + "</td>\n");
            CDRList.append("<td  >" + PatientCountOverAll_WEEKLY_TOTAL + "</td>\n");
            CDRList.append("<td  >" + PatientCountOverAll_MONTHLY_TOTAL + "</td>\n");
            CDRList.append("<td  >" + PatientCountOverAll_LAST_MONTHLY_TOTAL + "</td>\n");
            CDRList.append("<td > - </td>\n");
            CDRList.append("<td  > - </td>\n");
            CDRList.append("</tr>\n");







            final Parsehtm Parser = new Parsehtm(request);


//            Parser.SetField("Differential_badge", String.valueOf(Patient_Differential));
//
//            //TODAY
//            Parser.SetField("PatientCountOverAll_TODAY",String.valueOf(PatientCountOverAll_TODAY));
//
//
//            //WEEKLY
//            Parser.SetField("PatientCountOverAll_WEEKLY",String.valueOf(PatientCountOverAll_WEEKLY));
//
//
//            //MONTHLY
//            Parser.SetField("PatientCountOverAll_MONTHLY",String.valueOf(PatientCountOverAll_MONTHLY));
//
//
//            //LAST MONTH
//            Parser.SetField("PatientCountOverAll_LAST_MONTHLY",String.valueOf(PatientCountOverAll_LAST_MONTHLY));
//
//
//
//
//
//
//
//            Parser.SetField("PatientCountMonthly", String.valueOf(PatientCountMonthly));
//            Parser.SetField("UserId", String.valueOf(UserId));
//            Parser.SetField("FontColor", String.valueOf(FontColor));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("DATE", String.valueOf(TODAY));

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/DashBoardCounts.html");
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

    void DateFilter(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext) {


        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String Date = request.getParameter("Date");

//        String Date = sdf.format(new Date(Date1));



        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

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
        String Yesterday = "";
        String WEEK_START = "";
        String WEEK_END = "";

        String MONTH_START = "";
        String MONTH_END = "";
        String LAST_MONTH_START = "";
        String LAST_MONTH_END = "";

        int PatientCountOverAll_TODAY = 0;
        int PatientCountOverAll_TODAY_TOTAL = 0;
        int PatientCountOverAll_YESTERDAY = 0;
        int PatientCountOverAll_YESTERDAY_TOTAL = 0;

        int PatientCountOverAll_WEEKLY = 0;
        int PatientCountOverAll_WEEKLY_TOTAL = 0;

        int PatientCountOverAll_MONTHLY = 0;
        int PatientCountOverAll_MONTHLY_TOTAL = 0;

        int PatientCountOverAll_LAST_MONTHLY = 0;
        int PatientCountOverAll_LAST_MONTHLY_TOTAL = 0;

        String PatientsCurrentWeekDaily = "";

        int presentDayNumber=0;


        String[] LiveFacilities = {"victoria", "oe_2", "nacogdoches", "longview",  "oddasa","ER_Dallas", "frontlin_er","richmond"};

        double Patient_Differential=0.0;



        try {

            /*----------------------------------------------------------------------------------------------
             *                             Getting Time Variables
             * ---------------------------------------------------------------------------------------------
             */
            Query = "Select DATE_FORMAT('"+Date+"','%Y-%m-%d'), " +
                    "DATE_ADD(DATE_FORMAT('"+Date+"','%Y-%m-%d'), INTERVAL(1-DAYOFWEEK(DATE_FORMAT('"+Date+"','%Y-%m-%d'))) DAY), " +
                    "DATE_ADD(DATE_ADD(DATE_FORMAT('"+Date+"','%Y-%m-%d'), INTERVAL(1-DAYOFWEEK(DATE_FORMAT('"+Date+"','%Y-%m-%d'))) DAY),INTERVAL 6 DAY), " +
                    "DATE_SUB(LAST_DAY('"+Date+"'),INTERVAL DAY(LAST_DAY('"+Date+"'))- 1 DAY) AS 'FIRST DAY OF CURRENT MONTH', " +
                    "LAST_DAY('"+Date+"')," +
                    "DATE_SUB(LAST_DAY(DATE_FORMAT('"+Date+"','%Y-%m-%d') - INTERVAL 1 MONTH),INTERVAL DAY(LAST_DAY(DATE_FORMAT('"+Date+"','%Y-%m-%d') - INTERVAL 1 MONTH))- 1 DAY) AS 'FIRST DAY OF LAST MONTH'," +
                    "LAST_DAY(DATE_FORMAT('"+Date+"','%Y-%m-%d') - INTERVAL 1 MONTH )," +
                    " Date_format('"+Date+"','%d')," +
                    "DATE_SUB('"+Date+"', INTERVAL 1 DAY) AS yesterday_date," +
                    "Now()";

//            out.println("Query : " +Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                TODAY = rset.getString(1);
                WEEK_START = rset.getString(2);
                WEEK_END = rset.getString(3);
                MONTH_START = rset.getString(4);
                MONTH_END = TODAY;
//                MONTH_END = rset.getString(5);
                LAST_MONTH_START = rset.getString(6);
                LAST_MONTH_END = rset.getString(7);
                presentDayNumber = rset.getInt(8);
                Yesterday = rset.getString(9);
                DateNow=rset.getString(10);
            }
            rset.close();
            stmt.close();

//            presentDayNumber -=1;

            if(TODAY == null){
                out.println("Invalid Date \n Please follow this format (yyyy-mm-dd)");
                return;
            }
            if(TODAY.compareTo(DateNow)>0){
                out.println("Your given Date i.e "+Date+" is in Future  \nPlease enter PRESENT or PAST Date\nPlease follow this format (yyyy-mm-dd)");
                return;
            }



            for (int i = 0; i < LiveFacilities.length ; i++) {
                CDRList.append("<tr>\n");


                //Getting Name of Facilities
                Query = "Select Name from oe.clients where dbname='"+LiveFacilities[i]+"'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    CDRList.append("<td style=\"border: 1px solid black;\" >" + rset.getString(1) + "</td>\n");
                }
                rset.close();
                stmt.close();
                /*----------------------------------------------------------------------------------------------
                 *                              TODAY
                 * ---------------------------------------------------------------------------------------------
                 */

//            Query = "Select COUNT(*) from " + LiveFacilities[i] + ".PatientVisit where CreatedDate >= '" + TODAY + " 00:00:00' and CreatedDate <= '" + TODAY + " 23:59:59'";
                Query = "Select COUNT(*) from " + LiveFacilities[i] + ".PatientVisit a INNER JOIN "+LiveFacilities[i]+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59' AND b.status=0";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountOverAll_TODAY = rset.getInt(1);
                    PatientCountOverAll_TODAY_TOTAL += rset.getInt(1);
                    CDRList.append("<td style=\"border: 1px solid black;\" >" + PatientCountOverAll_TODAY + "</td>\n");

                }
                rset.close();
                stmt.close();

                /*----------------------------------------------------------------------------------------------
                 *                              Yesterday
                 * ---------------------------------------------------------------------------------------------
                 */

//                Query = "Select COUNT(*) from " + LiveFacilities[i]  + ".PatientVisit where CreatedDate >= '" + Yesterday + " 00:00:00' and CreatedDate <= '" + Yesterday + " 23:59:59'";
                Query = "Select COUNT(*) from " + LiveFacilities[i] + ".PatientVisit a INNER JOIN "+LiveFacilities[i]+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + Yesterday + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + Yesterday + " 23:59:59' AND b.status=0";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountOverAll_YESTERDAY=rset.getInt(1);
                    PatientCountOverAll_YESTERDAY_TOTAL+=rset.getInt(1);
                    CDRList.append("<td  style=\"border: 1px solid black;\" >" + rset.getInt(1) + "</td>\n");

                }
                rset.close();
                stmt.close();





                /*----------------------------------------------------------------------------------------------
                 *                              WEEKLY
                 * ---------------------------------------------------------------------------------------------
                 */

//            Query = "Select COUNT(*) from " + LiveFacilities[i]  + ".PatientVisit where CreatedDate >= '" + WEEK_START + " 00:00:00' and CreatedDate <= '" + WEEK_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + LiveFacilities[i] + ".PatientVisit a INNER JOIN "+LiveFacilities[i]+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59' AND b.status=0";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountOverAll_WEEKLY=rset.getInt(1);
                    PatientCountOverAll_WEEKLY_TOTAL+=rset.getInt(1);
                    CDRList.append("<td style=\"border: 1px solid black;\" >" + rset.getInt(1) + "</td>\n");
                }
                rset.close();
                stmt.close();

                /*----------------------------------------------------------------------------------------------
                 *                              MONTHLY
                 * ---------------------------------------------------------------------------------------------
                 */

//            Query = "Select COUNT(*) from " + LiveFacilities[i]  + ".PatientVisit where CreatedDate >= '" + MONTH_START + " 00:00:00' and CreatedDate <= '" + MONTH_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + LiveFacilities[i] + ".PatientVisit a INNER JOIN "+LiveFacilities[i]+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' AND b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountOverAll_MONTHLY =  rset.getInt(1);
                    PatientCountOverAll_MONTHLY_TOTAL +=  rset.getInt(1);
                    CDRList.append("<td style=\"border: 1px solid black;\" >" + rset.getInt(1) + "</td>\n");
                }
                rset.close();
                stmt.close();


                /*----------------------------------------------------------------------------------------------
                 *                             LAST MONTH
                 * ---------------------------------------------------------------------------------------------
                 */

//            Query = "Select COUNT(*) from " + LiveFacilities[i]  + ".PatientVisit where CreatedDate >= '" + LAST_MONTH_START + " 00:00:00' and CreatedDate <= '" + LAST_MONTH_END + " 23:59:59'";
                Query = "Select COUNT(*) from " + LiveFacilities[i] + ".PatientVisit a INNER JOIN "+LiveFacilities[i]+".PatientReg b ON a.PatientRegId=b.ID where DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + LAST_MONTH_START + " 00:00:00' and DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + LAST_MONTH_END + " 23:59:59' AND b.status=0";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountOverAll_LAST_MONTHLY = rset.getInt(1);
                    PatientCountOverAll_LAST_MONTHLY_TOTAL += rset.getInt(1);
                    CDRList.append("<td style=\"border: 1px solid black;\" >" + rset.getInt(1) + "</td>\n");
                }
                rset.close();
                stmt.close();

                Patient_Differential = 0.0;
//                if(presentDayNumber==0)
//                {
//                    presentDayNumber=1;
//                }
                double Patient_difference = 0;
                double AVG_difference = 0;
                double PatientCountOverAll_MONTHLY_AVG = (double)PatientCountOverAll_MONTHLY/(double)presentDayNumber;
                double PatientCountOverAll_LAST_MONTHLY_AVG = (double)PatientCountOverAll_LAST_MONTHLY/(double)30;


                if(PatientCountOverAll_LAST_MONTHLY_AVG==0){
                    CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\"> undefined </span> </td>\n");
                }else{
                    AVG_difference = ((PatientCountOverAll_MONTHLY_AVG/PatientCountOverAll_LAST_MONTHLY_AVG)*100)-100;

                    if(AVG_difference==0){
                        CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\">  "+String.format("%,.0f", AVG_difference)+"%</span> </td>\n");
                    }else if (AVG_difference>0){
                        CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill bg-success blink_me\" style=\"font-size: 100%;border-style: double;border-color: aquamarine;\"> +" + String.format("%,.0f", AVG_difference) + "% </span></td>\n");
                    }
                    else if (AVG_difference<0){
                        CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill bg-danger blink_me\" style=\"font-size: 100%;border-style: double;border-color: #fa0b20;\"> "+String.format("%,.0f", AVG_difference)+"%</span> </td>\n");
                    }
                }

//                if(PatientCountOverAll_MONTHLY_AVG > PatientCountOverAll_LAST_MONTHLY_AVG){
//                    //INCREASE
//                    Patient_difference = PatientCountOverAll_MONTHLY_AVG - PatientCountOverAll_LAST_MONTHLY_AVG;
//                    if (PatientCountOverAll_LAST_MONTHLY_AVG==0){
//                        CDRList.append("<td style=\"border: 1px solid black;\" ><span class=\"badge badge-pill bg-success blink_me\" style=\"font-size: 100%;border-style: double;border-color: aquamarine;\"> + 100% </span></td>\n");
//
//                    }else{
//                        Patient_Differential = (((double)Patient_difference/(double)PatientCountOverAll_LAST_MONTHLY_AVG))*100;
//                        if(Patient_Differential==0){
//                            CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\">  "+String.format("%,.0f", Patient_Differential)+"%</span> </td>\n");
//                        }else{
//                            CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill bg-success blink_me\" style=\"font-size: 100%;border-style: double;border-color: aquamarine;\"> +"+String.format("%,.0f", Patient_Differential)+"% </span></td>\n");
//                        }
//                    }
//                }else{
//                    //DECREASE
//                    Patient_difference = PatientCountOverAll_LAST_MONTHLY_AVG - PatientCountOverAll_MONTHLY_AVG;
//                    if(PatientCountOverAll_MONTHLY_AVG==0){
//                        CDRList.append("<td ><span class=\"badge badge-pill bg-danger blink_me\" style=\"font-size: 100%;border-style: double;border-color: #fa0b20;\"> - 100%  </span></td>\n");
//                    }else{
//
//                        Patient_Differential = (((double)Patient_difference/(double)PatientCountOverAll_MONTHLY_AVG))*100;
//                        if(Patient_Differential==0){
//                            CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\">  "+String.format("%,.0f", Patient_Differential)+"%</span> </td>\n");
//                        }
//                        else{
//                            CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill bg-danger blink_me\" style=\"font-size: 100%;border-style: double;border-color: #fa0b20;\"> - "+String.format("%,.0f", Patient_Differential)+"%</span> </td>\n");
//
//                        }
//                    }
//                }

//                out.println("Patient_difference =" + Patient_difference);
//                out.println("Patient_Differential =" + Patient_Differential);
//                out.println("PatientCountOverAll_LAST_MONTHLY_AVG =" + PatientCountOverAll_LAST_MONTHLY_AVG);
//                out.println("PatientCountOverAll_MONTHLY_AVG =" + PatientCountOverAll_MONTHLY_AVG);

                CDRList.append("<td style=\"border: 1px solid black;\"  >" + String.format("%,.2f", PatientCountOverAll_MONTHLY_AVG)  + "</td>\n");

                CDRList.append("</tr>\n");
            }

            CDRList.append("<tr style=\"border: 4px dotted black;font-weight: 900;\">\n");
            CDRList.append("<td > TOTAL </td>\n");
            CDRList.append("<td  >" + PatientCountOverAll_TODAY_TOTAL + "</td>\n");
            CDRList.append("<td  >" + PatientCountOverAll_YESTERDAY_TOTAL + "</td>\n");
            CDRList.append("<td  >" + PatientCountOverAll_WEEKLY_TOTAL + "</td>\n");
            CDRList.append("<td  >" + PatientCountOverAll_MONTHLY_TOTAL + "</td>\n");
            CDRList.append("<td  >" + PatientCountOverAll_LAST_MONTHLY_TOTAL + "</td>\n");
            CDRList.append("<td > - </td>\n");
            CDRList.append("<td  > - </td>\n");
            CDRList.append("</tr>\n");







            final Parsehtm Parser = new Parsehtm(request);


//            Parser.SetField("Differential_badge", String.valueOf(Patient_Differential));
//
//            //TODAY
//            Parser.SetField("PatientCountOverAll_TODAY",String.valueOf(PatientCountOverAll_TODAY));
//
//
//            //WEEKLY
//            Parser.SetField("PatientCountOverAll_WEEKLY",String.valueOf(PatientCountOverAll_WEEKLY));
//
//
//            //MONTHLY
//            Parser.SetField("PatientCountOverAll_MONTHLY",String.valueOf(PatientCountOverAll_MONTHLY));
//
//
//            //LAST MONTH
//            Parser.SetField("PatientCountOverAll_LAST_MONTHLY",String.valueOf(PatientCountOverAll_LAST_MONTHLY));
//
//
//
//
//
//
//
//            Parser.SetField("PatientCountMonthly", String.valueOf(PatientCountMonthly));
//            Parser.SetField("UserId", String.valueOf(UserId));
//            Parser.SetField("FontColor", String.valueOf(FontColor));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("DATE", String.valueOf(TODAY));

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/DashBoardCounts.html");
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
