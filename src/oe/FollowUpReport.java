//
// Decompiled by Procyon v0.5.36
//

package oe;

import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class FollowUpReport extends HttpServlet {
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
        Connection conn = null;
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter(response.getOutputStream());
        final Services supp = new Services();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        final Cookie[] cookies = request.getCookies();
        UserId = (Zone = (Passwd = ""));
        final int checkCookie = 0;
        for (int coky = 0; coky < cookies.length; ++coky) {
            final String cName = cookies[coky].getName();
            final String cValue = cookies[coky].getValue();
            if (cName.equals("UserId")) {
                UserId = cValue;
            }
        }
        if (ActionID.equals("GetReport")) {
            this.GetReport(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("GetReportInput")) {
            this.GetReportInput(request, out, conn, context, UserId, response);
        } else {
            out.println("Under Development");
        }
        try {
            conn.close();
        } catch (Exception ex) {
        }
        out.flush();
        out.close();
    }

    void GetReportInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final HttpServletResponse response) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final StringBuffer Clients = new StringBuffer();
        try {
            Query = "Select id,name from oe.clients where status = 0";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            System.out.println(Query);
            Clients.append("<option value=\"-1\">------ All -----</option>");
            while (hrset.next()) {
                Clients.append("<option value=" + hrset.getInt(1) + ">" + hrset.getString(2) + "</option>");
            }
            hrset.close();
            hstmt.close();
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Clients", Clients.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/FollowUpReport.html");
        } catch (Exception e) {
            out.println("Error in getting Report: " + e.getMessage());
        }
    }

    void GetReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final HttpServletResponse response) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        Statement hstmt2 = null;
        ResultSet hrset2 = null;
        String Query2 = "";
        final int requestedMonthInt = Integer.parseInt(request.getParameter("Month").trim());
        String clientid = "";
        String clientname = "";
        final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "Novermber", "December"};
        final String requestedYearString = request.getParameter("Year").trim();
        String requestedMonthString = request.getParameter("Month").trim();
        final String Clients = request.getParameter("Clients").trim();
        int monthcount = 0;
        long TotalCount = 0L;
        if (requestedMonthString.length() == 1) {
            requestedMonthString = "0" + requestedMonthString;
        }
        int requestedMonthLength = 0;
        if (requestedMonthInt == 1 || requestedMonthInt == 3 || requestedMonthInt == 5 || requestedMonthInt == 7 || requestedMonthInt == 8 || requestedMonthInt == 10 || requestedMonthInt == 12) {
            requestedMonthLength = 31;
        } else if (requestedMonthInt == 2) {
            requestedMonthLength = 28;
        } else {
            requestedMonthLength = 30;
        }
        try {
            final StringBuffer List = new StringBuffer();
            final StringBuffer ClientsBuffer = new StringBuffer();
            Query = "Select id,name from oe.clients where status = 0";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            System.out.println(Query);
            ClientsBuffer.append("<option value=\"-1\">------ All -----</option>");
            while (hrset.next()) {
                ClientsBuffer.append("<option value=" + hrset.getInt(1) + ">" + hrset.getString(2) + "</option>");
            }
            hrset.close();
            hstmt.close();
            List.append("<table width=\"100%\" id=\"ReportTable\" class=\"table table-bordered table-striped\">");
            List.append("<thead>");
            List.append("<tr>");
            List.append("<th style=\"width:30%\">ClientName</th> ");
            for (int i = 1; i <= requestedMonthLength; ++i) {
                List.append("<th style=\"width:02%\">" + requestedMonthString + "/" + String.format("%02d", i) + "/" + requestedYearString + "</th> ");
            }
            List.append("<th style=\"width:08%\">Total</th> ");
            List.append("</tr>");
            List.append("</thead>");
            if (Clients.equals("-1")) {
                Query = "SELECT id,name,directory_1,remotedirectory,tablename FROM oe.clients WHERE id NOT IN (1,9,10,12)";
            } else {
                Query = "SELECT id,name,directory_1,remotedirectory,tablename FROM oe.clients WHERE id = '" + Clients + "'";
            }
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            System.out.println(Query);
            while (hrset.next()) {
                clientid = hrset.getString(1);
                clientname = hrset.getString(2);
                List.append(" <tbody>");
                List.append("<tr>");
                List.append("<td align=left>" + clientname + "</td>");
                monthcount = 0;
                for (int i = 1; i <= requestedMonthLength; ++i) {
                    Query2 = "SELECT count(*) FROM (SELECT DISTINCT firstname, lastname,substr(dosdate,1,10) FROM filelogs_sftp  WHERE clientdirectory= " + clientid + " AND substr(dosdate,1,10)='" + requestedYearString + "-" + requestedMonthString + "-" + String.format("%02d", i) + "' ) t";
                    System.out.println(Query2);
                    hstmt2 = conn.createStatement();
                    hrset2 = hstmt2.executeQuery(Query2);
                    hrset2.next();
                    List.append("<td align=left>" + hrset2.getInt(1) + "</td>");
                    monthcount += hrset2.getInt(1);
                    hrset2.close();
                    hstmt2.close();
                }
                List.append("<td align=left>" + monthcount + "</td>");
                List.append("</tr>");
                TotalCount += monthcount;
            }
            List.append("<tr style=\"background-color:#D9DDFC\">");
            List.append("<td align=left></td>");
            List.append("<td colspan='" + requestedMonthLength + "' style=\"width:02%;text-align:center;font-weight: bold;\">Complete Total</td> ");
            List.append("<td align=left style=\"font-weight: bold;\">" + TotalCount + "</td>");
            List.append("</tr>");
            List.append("</tbody>");
            List.append("</table>");
            String MonthName = "";
            if (Integer.parseInt(requestedMonthString) >= 1 && Integer.parseInt(requestedMonthString) <= 12) {
                MonthName = months[Integer.parseInt(requestedMonthString) - 1];
            }
            out.println(List + "|" + "Month Name: " + MonthName);
        } catch (Exception e) {
            out.println("Error in getting Report: " + e.getMessage());
        }
    }
}
