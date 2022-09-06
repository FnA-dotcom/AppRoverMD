package md;


import DAL.Payments;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LabReports extends HttpServlet {

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        response.setContentType("text/html");
        Services supp = new Services();
        UtilityHelper helper = new UtilityHelper();
        Payments payments = new Payments();
        try {
            HttpSession session = request.getSession(false);
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            String UserId = session.getAttribute("UserId").toString();
            String DatabaseName = session.getAttribute("DatabaseName").toString();
            int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            int UserType = Integer.parseInt(session.getAttribute("UserType").toString());
            if (UserId.equals("") || UserId.isEmpty()) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            String ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.equals("LabReport_Monthly")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Facility", "Click on the Facility Option", FacilityIndex);
                LabReport_Monthly(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("LabReport_Daily")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Facility", "Click on the Facility Option", FacilityIndex);
                LabReport_Daily(request, out, conn, context, UserId, DatabaseName, FacilityIndex,helper);
            } else if (ActionID.equals("GetReport_Monthly")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Facility", "Click on the Facility Option", FacilityIndex);
                GetReport_Monthly(request, out, conn, context, UserId, DatabaseName, FacilityIndex);

            }  else if (ActionID.equals("GetReport_Daily")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Facility", "Click on the Facility Option", FacilityIndex);
                GetReport_Daily(request, out, conn, context, UserId, DatabaseName, FacilityIndex);

            } else {
                helper.deleteUserSession(request, conn, session.getId());
                session.invalidate();
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in AddFacility ** (handleRequest)", context, e, "AddFacility", "handleRequest", conn);
            Services.DumException("AddFacility", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetReport");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                helper.SendEmailWithAttachment("Error in ManifestReport ** (handleRequest -- SqlException)", context, e, "ManifestReport", "handleRequest", conn);
                Services.DumException("ManifestReport", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetReport");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }


    void LabReport_Monthly(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int SNo = 1;
        StringBuffer CDRList = new StringBuffer();
        StringBuilder locationList = new StringBuilder();

        // DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        try {
            Query = "Select Id, Location from roverlab.Locations";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            locationList.append("<option value='-1' selected disabled>Select Location</option>");
            while (rset.next()) {
                locationList.append("<option value=" + rset.getString(1) + "  >" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();



            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("LocationList", locationList.toString());

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/LabReportDateWise_MONTHLY.html");
        } catch (Exception e) {
            //System.out.println("in the catch exception of GetReport Function ");
            //System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            //System.out.println(str);
        }
    }

    void LabReport_Daily(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int SNo = 1;
        StringBuffer CDRList = new StringBuffer();

        StringBuilder locationList = new StringBuilder();

        try {
            Query = "Select Id, Location from roverlab.Locations";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            locationList.append("<option value='-1' selected disabled>Select Location</option>");
            while (rset.next()) {
                locationList.append("<option value=" + rset.getString(1) + "  >" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("LocationList", locationList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/LabReportDateWise_DAILY.html");
        } catch (Exception e) {
            //System.out.println("in the catch exception of GetReport Function ");
            //System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            //System.out.println(str);
        }
    }

    void GetReport_Monthly(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query2 = "";


        int OrdersRegistered_TODAY = 0;
        int OrdersDispatched_TODAY = 0;
        int OrdersReceivedAtLab_TODAY = 0;
        double OrdersResultAnnounced_TODAY = 0.0f;
        double OrdersBacklog_TODAY = 0.0f;
        double Orders_TOTAL_TODAY = 0.0f;
        double SuccessRatio_TODAY = 0.0f;
        double BackLogRatio_TODAY = 0.0f;

        int TOTAL_OrdersRegistered_TODAY = 0;
        int TOTAL_OrdersDispatched_TODAY = 0;
        int TOTAL_OrdersReceivedAtLab_TODAY = 0;
        double TOTAL_OrdersResultAnnounced_TODAY = 0;
        double TOTAL_OrdersBacklog_TODAY = 0;
        double TOTAL_Orders_TOTAL_TODAY = 0;
        double TOTAL_SuccessRatio_TODAY = 0;
        double TOTAL_BackLogRatio_TODAY = 0;


        String DayDate = "";
        String DayDate2 = "";

        StringBuilder MRList = new StringBuilder();
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();

        String filter = "";
        String Join = "";

        String Location = request.getParameter("Location") != null ? request.getParameter("Location").trim() : null;

        StringBuilder locationList = new StringBuilder();
        try {
            Query = "Select Id, Location from roverlab.Locations";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            locationList.append("<option value='-1' selected disabled>Select Location</option>");
            while (rset.next()) {
                if(Location != null && Location.equals(rset.getString(1)))
                    locationList.append("<option value=" + rset.getString(1) + "  selected>" + rset.getString(2) + "</option>");
                else
                    locationList.append("<option value=" + rset.getString(1) + "  >" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if (Location != null){
            Join = " INNER JOIN " + Database + ".PatientReg b ON a.PatRegIdx=b.Id ";
            filter += " AND  b.TestingLocation = '" + Location + "' ";
        }





        try {


                Query = "select * from  (select adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date from \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4) v \n" +
                        "where selected_date >= '" + FromDate + "' and selected_date <= '" + ToDate + "' order by selected_date";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    DayDate = "";
                    DayDate2 = rset.getString(1);
                    MRList.append("<tr>");
                    DayDate = DayDate2.substring(5, 7) + "/" + DayDate2.substring(8, 10) + "/" + DayDate2.substring(0, 4);
                    MRList.append("<td align=left>" + DayDate + "</td>\n");

                    Query2 = "Select COUNT(*) from " + Database + ".TestOrder a " +
                            Join +
                            "where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + DayDate2 + " 00:00:00' " +
                            "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + DayDate2 + " 23:59:59' " +
                            "AND a.Status=0 " +
                            "AND a.StageIdx=0 " + filter;

                    System.out.println("QUERY ->> "+Query2);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        OrdersRegistered_TODAY = rset2.getInt(1);
                        MRList.append("<td align=left>" + OrdersRegistered_TODAY + "</td>\n");
                    }
                    rset2.close();
                    stmt2.close();



                    Query2 = "Select COUNT(*) from " + Database + ".TestOrder a " +
                            Join +
                            "where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + DayDate2 + " 00:00:00' " +
                            "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + DayDate2 + " 23:59:59' " +
                            "AND a.Status=0 " +
                            "AND a.StageIdx=1 "+ filter;
                    System.out.println("QUERY ->> "+Query2);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        OrdersDispatched_TODAY = rset2.getInt(1);
                        MRList.append("<td align=left>" + OrdersDispatched_TODAY + "</td>\n");

                    }
                    rset2.close();
                    stmt2.close();

                    Query2 = "Select COUNT(*) from " + Database + ".TestOrder a " +
                            Join +
                            "where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + DayDate2 + " 00:00:00' " +
                            "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + DayDate2 + " 23:59:59' " +
                            "AND a.Status=0 " +
                            "AND a.StageIdx=2 " + filter;
                    System.out.println("QUERY ->> "+Query2);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        OrdersReceivedAtLab_TODAY = rset2.getInt(1);
                        MRList.append("<td align=left>" + OrdersReceivedAtLab_TODAY + "</td>\n");

                    }
                    rset2.close();
                    stmt2.close();


                    Query2 = "Select COUNT(*) from " + Database + ".Tests c" +
                            " INNER JOIN " + Database + ".TestOrder a ON a.Id = c.OrderId" +
                            Join +
                            " where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + DayDate2 + " 00:00:00' " +
                            "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + DayDate2 + " 23:59:59' " +
                            "AND a.Status=0 " +
                            "AND c.TestStatus is not null " + filter;
                    System.out.println("QUERY ->> "+Query2);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        OrdersResultAnnounced_TODAY = rset2.getInt(1);
                        MRList.append("<td align=left>" + String.format("%,.0f",OrdersResultAnnounced_TODAY) + "</td>\n");

                    }
                    rset2.close();
                    stmt2.close();


                    Query2 = "Select COUNT(*) from " + Database + ".Tests c" +
                            " INNER JOIN " + Database + ".TestOrder a ON a.Id = c.OrderId" +
                            Join+
                            " where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + DayDate2 + " 00:00:00' " +
                            "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + DayDate2 + " 23:59:59' " +
                            "AND a.Status=0 " +
                            "AND c.TestStatus is null "+ filter;
                    System.out.println("QUERY ->> "+Query2);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        OrdersBacklog_TODAY = rset2.getInt(1);
                        MRList.append("<td align=left>" + String.format("%,.0f",OrdersBacklog_TODAY) + "</td>\n");

                    }
                    rset2.close();
                    stmt2.close();


                    Query2 = "Select COUNT(*) from " + Database + ".Tests c" +
                            " INNER JOIN " + Database + ".TestOrder a ON a.Id = c.OrderId" +
                            Join+
                            " where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + DayDate2 + " 00:00:00' " +
                            "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + DayDate2 + " 23:59:59' " +
                            "AND a.Status=0 "+ filter;
                    System.out.println("QUERY ->> "+Query2);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        Orders_TOTAL_TODAY = rset2.getInt(1);
                    }
                    rset2.close();
                    stmt2.close();

//                    SuccessRatio_TODAY=0.0f;
                    if(Orders_TOTAL_TODAY != 0.f)
                        SuccessRatio_TODAY = (OrdersResultAnnounced_TODAY / Orders_TOTAL_TODAY) * 100 ;
                    else
                        SuccessRatio_TODAY=0.0f;

                    //System.out.println("OrdersResultAnnounced_TODAY - > "+String.valueOf(OrdersResultAnnounced_TODAY));
                    //System.out.println("Orders_TOTAL_TODAY - > "+String.valueOf(Orders_TOTAL_TODAY));
                    //System.out.println("SuccessRatio_TODAY - > "+String.valueOf(SuccessRatio_TODAY));
                    MRList.append("<td align=left>" + String.valueOf(String.format("%,.2f",SuccessRatio_TODAY) +" %" )+ "</td>\n");

//                    BackLogRatio_TODAY=0.0f;
                    if(Orders_TOTAL_TODAY != 0.f)
                         BackLogRatio_TODAY = (OrdersBacklog_TODAY / Orders_TOTAL_TODAY) * 100 ;
                    else
                        BackLogRatio_TODAY=0.0f;
                    //System.out.println("BackLogRatio_TODAY - > "+String.valueOf(SuccessRatio_TODAY));

                    MRList.append("<td align=left>" + String.valueOf(String.format("%,.2f",BackLogRatio_TODAY) +" %") + "</td>\n");


                    MRList.append("<tr>");

                    TOTAL_OrdersRegistered_TODAY += OrdersRegistered_TODAY;
                    TOTAL_OrdersDispatched_TODAY += OrdersDispatched_TODAY;
                    TOTAL_OrdersReceivedAtLab_TODAY += OrdersReceivedAtLab_TODAY;
                    TOTAL_OrdersResultAnnounced_TODAY += OrdersResultAnnounced_TODAY;
                    TOTAL_OrdersBacklog_TODAY += OrdersBacklog_TODAY;
                    TOTAL_Orders_TOTAL_TODAY += Orders_TOTAL_TODAY;

                }

                TOTAL_SuccessRatio_TODAY = (TOTAL_OrdersResultAnnounced_TODAY/TOTAL_Orders_TOTAL_TODAY)*100;
                TOTAL_BackLogRatio_TODAY = (TOTAL_OrdersBacklog_TODAY/TOTAL_Orders_TOTAL_TODAY)*100;

                MRList.append("<tr style=\"background-color:#00FF00\">");
                MRList.append("<td align=left> <b>TOTAL:</b> </td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TOTAL_OrdersRegistered_TODAY + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TOTAL_OrdersDispatched_TODAY + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TOTAL_OrdersReceivedAtLab_TODAY + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + String.format("%,.0f",TOTAL_OrdersResultAnnounced_TODAY) + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + String.format("%,.0f",TOTAL_OrdersBacklog_TODAY) + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + String.format("%,.2f",TOTAL_SuccessRatio_TODAY) +" %" + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + String.format("%,.2f",TOTAL_BackLogRatio_TODAY) +" %" + "</b></font></td>\n");
                MRList.append("</tr>");
                rset.close();
                stmt.close();


                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("MRList", String.valueOf(MRList));
                Parser.SetField("FromDate", String.valueOf(FromDate));
                Parser.SetField("ToDate", String.valueOf(ToDate));
                Parser.SetField("Title", "Orders from "+FromDate+" to "+ToDate);
                Parser.SetField("LocationList", locationList.toString());
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/LabReportDateWise_MONTHLY.html");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println(str);
            return;
        }
    }

    void GetReport_Daily(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query2 = "";


        int OrdersRegistered_TODAY = 0;
        int OrdersDispatched_TODAY = 0;
        int OrdersReceivedAtLab_TODAY = 0;
        double OrdersResultAnnounced_TODAY = 0.0f;
        double OrdersBacklog_TODAY = 0.0f;
        double Orders_TOTAL_TODAY = 0.0f;
        double SuccessRatio_TODAY = 0.0f;
        double BackLogRatio_TODAY = 0.0f;

        int TOTAL_OrdersRegistered_TODAY = 0;
        int TOTAL_OrdersDispatched_TODAY = 0;
        int TOTAL_OrdersReceivedAtLab_TODAY = 0;
        double TOTAL_OrdersResultAnnounced_TODAY = 0;
        double TOTAL_OrdersBacklog_TODAY = 0;
        double TOTAL_Orders_TOTAL_TODAY = 0;
        double TOTAL_SuccessRatio_TODAY = 0;
        double TOTAL_BackLogRatio_TODAY = 0;


        String DayDate = "";
        String DayDate2 = "";
        StringBuilder MRList = new StringBuilder();
        String Date = request.getParameter("Date").trim();


        String filter = "";
        String Location = request.getParameter("Location") != null ? request.getParameter("Location").trim() : null;
        StringBuilder locationList = new StringBuilder();
        try {
            Query = "Select Id, Location from roverlab.Locations";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            locationList.append("<option value='-1' selected disabled>Select Location</option>");
            while (rset.next()) {
                if(Location != null && Location.equals(rset.getString(1)))
                    locationList.append("<option value=" + rset.getString(1) + "  selected>" + rset.getString(2) + "</option>");
                else
                    locationList.append("<option value=" + rset.getString(1) + "  >" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if (Location != null) filter += " WHERE Id = '" + Location + "' ";


        try {


                Query = "SELECT Id,IFNULL(Location,'') FROM "+ Database +".Locations "+filter;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    DayDate = "";
                    DayDate2 = rset.getString(1);
                    MRList.append("<tr>");

                    MRList.append("<td align=left>" + rset.getString(2) + "</td>\n");

                    Query2 = "Select COUNT(*) from " + Database + ".TestOrder a " +
                            "INNER JOIN  " + Database + ".PatientReg b ON a.PatRegIdx=b.id " +
                            " where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + Date + " 00:00:00' " +
                            "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + Date + " 23:59:59' " +
                            "AND a.Status=0 " +
                            "AND a.StageIdx=0" +
                            " AND b.TestingLocation="+rset.getString(1);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        OrdersRegistered_TODAY = rset2.getInt(1);
                        MRList.append("<td align=left>" + OrdersRegistered_TODAY + "</td>\n");
                    }
                    rset2.close();
                    stmt2.close();



                    Query2 = "Select COUNT(*) from " + Database + ".TestOrder a " +
                            "INNER JOIN  " + Database + ".PatientReg b ON a.PatRegIdx=b.id " +
                            "where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + Date + " 00:00:00' " +
                            "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + Date + " 23:59:59' " +
                            "AND a.Status=0 " +
                            "AND a.StageIdx=1 " +
                            "AND b.TestingLocation="+rset.getString(1);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        OrdersDispatched_TODAY = rset2.getInt(1);
                        MRList.append("<td align=left>" + OrdersDispatched_TODAY + "</td>\n");

                    }
                    rset2.close();
                    stmt2.close();

                    Query2 = "Select COUNT(*) from " + Database + ".TestOrder a " +
                            "INNER JOIN  " + Database + ".PatientReg b ON a.PatRegIdx=b.id " +
                            "where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + Date + " 00:00:00' " +
                            "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + Date + " 23:59:59' " +
                            "AND a.Status=0 " +
                            "AND a.StageIdx=2 " +
                            "AND b.TestingLocation="+rset.getString(1);
                    //System.out.println("Query2 - > "+Query2);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        OrdersReceivedAtLab_TODAY = rset2.getInt(1);
                        MRList.append("<td align=left>" + OrdersReceivedAtLab_TODAY + "</td>\n");

                    }
                    rset2.close();
                    stmt2.close();


                    Query2 = "Select COUNT(*) from " + Database + ".Tests a" +
                            " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                            " INNER JOIN  " + Database + ".PatientReg c ON b.PatRegIdx=c.id " +
                            " where DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + Date + " 00:00:00' " +
                            "AND DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + Date + " 23:59:59' " +
                            "AND b.Status=0 " +
                            "AND a.TestStatus is not null " +
                            "AND c.TestingLocation="+rset.getString(1);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        OrdersResultAnnounced_TODAY = rset2.getInt(1);
                        MRList.append("<td align=left>" + String.format("%,.0f",OrdersResultAnnounced_TODAY) + "</td>\n");

                    }
                    rset2.close();
                    stmt2.close();


                    Query2 = "Select COUNT(*) from " + Database + ".Tests a" +
                            " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                            " INNER JOIN " + Database + ".PatientReg c ON b.PatRegIdx=c.id " +
                            " where DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + Date + " 00:00:00' " +
                            "AND DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + Date + " 23:59:59' " +
                            "AND b.Status=0 " +
                            "AND a.TestStatus is null "+
                            "AND c.TestingLocation="+rset.getString(1);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        OrdersBacklog_TODAY = rset2.getInt(1);
                        MRList.append("<td align=left>" + String.format("%,.0f",OrdersBacklog_TODAY) + "</td>\n");

                    }
                    rset2.close();
                    stmt2.close();


                    Query2 = "Select COUNT(*) from " + Database + ".Tests a" +
                            " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                            " INNER JOIN  " + Database + ".PatientReg c ON b.PatRegIdx=c.id " +
                            " where DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + Date + " 00:00:00' " +
                            "AND DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + Date + " 23:59:59' " +
                            "AND b.Status=0 " +
                            "AND c.TestingLocation="+rset.getString(1);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        Orders_TOTAL_TODAY = rset2.getInt(1);
                    }
                    rset2.close();
                    stmt2.close();

//                    SuccessRatio_TODAY=0.0f;
                    if(Orders_TOTAL_TODAY != 0.f)
                        SuccessRatio_TODAY = (OrdersResultAnnounced_TODAY / Orders_TOTAL_TODAY) * 100 ;
                    else
                        SuccessRatio_TODAY=0.0f;


                    MRList.append("<td align=left>" + String.valueOf(String.format("%,.2f",SuccessRatio_TODAY) +" %" )+ "</td>\n");

//                    BackLogRatio_TODAY=0.0f;
                    if(Orders_TOTAL_TODAY != 0.f)
                         BackLogRatio_TODAY = (OrdersBacklog_TODAY / Orders_TOTAL_TODAY) * 100 ;
                    else
                        BackLogRatio_TODAY=0.0f;


                    MRList.append("<td align=left>" + String.valueOf(String.format("%,.2f",BackLogRatio_TODAY) +" %") + "</td>\n");


                    MRList.append("<tr>");

                    TOTAL_OrdersRegistered_TODAY += OrdersRegistered_TODAY;
                    TOTAL_OrdersDispatched_TODAY += OrdersDispatched_TODAY;
                    TOTAL_OrdersReceivedAtLab_TODAY += OrdersReceivedAtLab_TODAY;
                    TOTAL_OrdersResultAnnounced_TODAY += OrdersResultAnnounced_TODAY;
                    TOTAL_OrdersBacklog_TODAY += OrdersBacklog_TODAY;
                    TOTAL_Orders_TOTAL_TODAY += Orders_TOTAL_TODAY;

                }

                if(TOTAL_Orders_TOTAL_TODAY != 0.f)
                    TOTAL_SuccessRatio_TODAY = (TOTAL_OrdersResultAnnounced_TODAY/TOTAL_Orders_TOTAL_TODAY)*100;
                else
                    TOTAL_SuccessRatio_TODAY = 0.f;

                if(TOTAL_Orders_TOTAL_TODAY != 0.f)
                    TOTAL_BackLogRatio_TODAY = (TOTAL_OrdersBacklog_TODAY/TOTAL_Orders_TOTAL_TODAY)*100;
                else
                    TOTAL_BackLogRatio_TODAY = 0.f;

                MRList.append("<tr style=\"background-color:#00FF00\">");
                MRList.append("<td align=left> <b>TOTAL:</b> </td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TOTAL_OrdersRegistered_TODAY + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TOTAL_OrdersDispatched_TODAY + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TOTAL_OrdersReceivedAtLab_TODAY + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + String.format("%,.0f",TOTAL_OrdersResultAnnounced_TODAY) + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + String.format("%,.0f",TOTAL_OrdersBacklog_TODAY) + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + String.format("%,.2f",TOTAL_SuccessRatio_TODAY) +" %" + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + String.format("%,.2f",TOTAL_BackLogRatio_TODAY) +" %" + "</b></font></td>\n");
                MRList.append("</tr>");
                rset.close();
                stmt.close();


                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("MRList", String.valueOf(MRList));
                Parser.SetField("Date", String.valueOf(Date));
                Parser.SetField("Title", "Orders for "+Date);
                Parser.SetField("LocationList", locationList.toString());
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/LabReportDateWise_DAILY.html");

        } catch (Exception e) {
            //System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            //System.out.println(str);
            return;
        }
    }

}


