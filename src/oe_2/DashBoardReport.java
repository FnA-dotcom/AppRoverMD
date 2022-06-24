package oe_2;

import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashBoardReport
        extends HttpServlet {
    public void init(ServletConfig config)
            throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection conn = null;
        String UserId = "";
        String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();

        ServletContext context = null;
        context = getServletContext();
        conn = Services.getMysqlConn(context);
        if (ActionID.equals("GetValues")) {
            GetValues(request, out, conn, context);
        } else if (ActionID.equals("ShowDashBoard")) {
            ShowDashBoard(request, out, conn, context);
        }
        try {
            conn.close();
        } catch (Exception var11) {
        }
        out.flush();
        out.close();
    }

    void GetValues(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Dashboards/DashBoardInput.html");
        } catch (Exception e) {
        }
    }

    void ShowDashBoard(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        String Query1 = "";

        int TotalCallsToday = 0;
        int AnsweredCall = 0;
        int MissedCalls = 0;
        int OutboundCalls = 0;
        int CallBack = 0;
        String UserId = Services.GetCookie("UserId", request);
        UserId = UserId.substring(1);
        String StartDateTime = request.getParameter("StartDateT").trim();
        String FMonth = StartDateTime.substring(0, 2);
        String FDay = StartDateTime.substring(3, 5);
        String FYear = StartDateTime.substring(6, 10);
        String FHour = StartDateTime.substring(11, 13);
        String FMin = StartDateTime.substring(14, 16);

        String EMonth = StartDateTime.substring(22, 24);
        String EDay = StartDateTime.substring(25, 27);
        String EYear = StartDateTime.substring(28, 32);
        String EHour = StartDateTime.substring(33, 35);
        String EMin = StartDateTime.substring(36, 38);
        String StartDate = FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00";
        String EndDate = EYear + "-" + EMonth + "-" + EDay + " " + EHour + ":" + EMin + ":59";
        try {
            Query = "select count(*) as Total from cdr where dcontext='Inbound' and lastapp='Dial' and  DATE_FORMAT(calldate,'%Y-%m-%d') >= DATE_FORMAT('" + StartDate + "','%Y-%m-%d')  and " + " DATE_FORMAT(calldate,'%Y-%m-%d') <= DATE_FORMAT('" + EndDate + "','%Y-%m-%d')  and" + " calltype !='EXTENSION CAll' AND companyid='" + UserId + "' ";


            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                TotalCallsToday = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "select count(*) as ANSWERED from cdr where dcontext='Inbound' and lastapp='Dial' and  Disposition='ANSWERED' and  DATE_FORMAT(calldate,'%Y-%m-%d') >= DATE_FORMAT('" + StartDate + "','%Y-%m-%d')  and " + " DATE_FORMAT(calldate,'%Y-%m-%d') <= DATE_FORMAT('" + EndDate + "','%Y-%m-%d')  and " + "companyid='" + UserId + "' and calltype !='EXTENSION CAll' ";


            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                AnsweredCall = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "select count(*) as NOANSWER from cdr where dcontext='Inbound' and lastapp='Dial' and  Disposition='NO ANSWER' and  DATE_FORMAT(calldate,'%Y-%m-%d') >= DATE_FORMAT('" + StartDate + "','%Y-%m-%d')  and " + " DATE_FORMAT(calldate,'%Y-%m-%d') <= DATE_FORMAT('" + EndDate + "','%Y-%m-%d')  and " + " companyid='" + UserId + "' and calltype !='EXTENSION CAll'  ";


            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                MissedCalls = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "select count(*) as ANSWEREDOUT from cdr where dcontext='Outbound' and lastapp='Dial'  and Disposition='ANSWERED' and  DATE_FORMAT(calldate,'%Y-%m-%d') >= DATE_FORMAT('" + StartDate + "','%Y-%m-%d')  and " + " DATE_FORMAT(calldate,'%Y-%m-%d') <= DATE_FORMAT('" + EndDate + "','%Y-%m-%d')  and " + " companyid='" + UserId + "' and calltype !='EXTENSION CAll'  ";


            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OutboundCalls = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "select count(*) as NOANSWEROUT from cdr where dcontext='Outbound' and lastapp='Dial'  and Disposition='NO ANSWER' and  DATE_FORMAT(calldate,'%Y-%m-%d') >= DATE_FORMAT('" + StartDate + "','%Y-%m-%d')  and " + " DATE_FORMAT(calldate,'%Y-%m-%d') <= DATE_FORMAT('" + EndDate + "','%Y-%m-%d')  and " + " companyid='" + UserId + "' and calltype !='EXTENSION CAll'  ";


            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                CallBack = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            int Value1 = 0;
            int Value2 = 0;
            int Value3 = 0;
            int Value4 = 0;
            int Value5 = 0;
            StringBuilder RptExtensionDash = new StringBuilder();


            UserId = Services.GetCookie("UserId", request);
            Query = "select name,substr(name,5,8) from asteriskrealtime.sip_buddies  where companyid='" + UserId + "' order by id";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                RptExtensionDash.append("<tr>");
                RptExtensionDash.append("<td align=left>" + rset.getString(1) + "</td>\n");
                Query1 = " SELECT count(*) as totalExtensionCalls FROM asteriskcdrdb.cdr  WHERE extension='" + rset.getString(1) + "' AND " + " DATE_FORMAT(calldate,'%Y-%m-%d') >= DATE_FORMAT('" + StartDate + "','%Y-%m-%d')  and " + " DATE_FORMAT(calldate,'%Y-%m-%d') <= DATE_FORMAT('" + EndDate + "','%Y-%m-%d')  and " + " companyid='" + UserId.substring(1) + "'  ";


                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                if (rset1.next()) {
                    RptExtensionDash.append("<td align=left>" + rset1.getString(1) + "</td>\n");
                }
                rset1.close();
                stmt1.close();

                Query1 = " SELECT count(*) as totalOutboundCalls FROM asteriskcdrdb.cdr  WHERE extension='" + rset.getString(1) + "' AND " + " DATE_FORMAT(calldate,'%Y-%m-%d') >= DATE_FORMAT('" + StartDate + "','%Y-%m-%d')  and " + " DATE_FORMAT(calldate,'%Y-%m-%d') <= DATE_FORMAT('" + EndDate + "','%Y-%m-%d')  and " + " companyid='" + UserId.substring(1) + "' AND dcontext='Outbound' AND " + " calltype !='EXTENSION CAll' ";


                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                if (rset1.next()) {
                    RptExtensionDash.append("<td align=left>" + rset1.getString(1) + "</td>\n");
                }
                rset1.close();
                stmt1.close();

                Query1 = " SELECT count(*) as totalInboundCalls FROM asteriskcdrdb.cdr  WHERE extension='" + rset.getString(1) + "' AND " + " DATE_FORMAT(calldate,'%Y-%m-%d') >= DATE_FORMAT('" + StartDate + "','%Y-%m-%d')  and " + " DATE_FORMAT(calldate,'%Y-%m-%d') <= DATE_FORMAT('" + EndDate + "','%Y-%m-%d')  and " + " companyid='" + UserId.substring(1) + "' AND dcontext='Inbound' AND calltype !='EXTENSION CAll' ";


                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                if (rset1.next()) {
                    RptExtensionDash.append("<td align=left>" + rset1.getString(1) + "</td>\n");
                }
                rset1.close();
                stmt1.close();

                Query = "select count(*) as NOANSWER from cdr where dcontext='Inbound' and lastapp='Dial' and  Disposition='NO ANSWER' AND companyid='" + UserId.substring(1) + "' and " + " DATE_FORMAT(calldate,'%Y-%m-%d') >= DATE_FORMAT('" + StartDate + "','%Y-%m-%d')  and " + " DATE_FORMAT(calldate,'%Y-%m-%d') <= DATE_FORMAT('" + EndDate + "','%Y-%m-%d')  and " + " calltype !='EXTENSION CAll' AND extension='" + rset.getString(1) + "' ";


                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                if (rset1.next()) {
                    RptExtensionDash.append("<td align=left>" + rset1.getString(1) + "</td>\n");
                }
                rset1.close();
                stmt1.close();


                RptExtensionDash.append("</tr>");
            }
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("TotalCallsToday", String.valueOf(TotalCallsToday));
            Parser.SetField("AnsweredCall", String.valueOf(AnsweredCall));
            Parser.SetField("MissedCalls", String.valueOf(MissedCalls));
            Parser.SetField("OutboundCalls", String.valueOf(OutboundCalls));
            Parser.SetField("CallBack", String.valueOf(CallBack));
            Parser.SetField("ExtensionDash", RptExtensionDash.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Dashboards/GraphicalMainDashboard.html");
        } catch (Exception var11) {
            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            out.flush();
            out.close();
        }
    }
}
