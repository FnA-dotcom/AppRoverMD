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
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CovidLogReport extends HttpServlet {
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
        ResultSet rset = null;
        Statement stmt = null;
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        String Database = "";
        String Query = "";
        int ClientId = 0;
        String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        Services supp = new Services();
        ServletContext context = null;
        context = getServletContext();
        UtilityHelper helper = new UtilityHelper();
        conn = Services.getMysqlConn(context);
        HttpSession session = request.getSession(false);
        boolean validSession = helper.checkSession(request, context, session, out);
        if (!validSession) {
            out.flush();
            out.close();
            return;
        }
        UserId = session.getAttribute("UserId").toString();
        Database = session.getAttribute("DatabaseName").toString();
        ClientId = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
        try {
            if (ActionID.equals("GetReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Covid Log report", "Open frontlin_er Covid Log Report Screen", ClientId);
                GetReport(request, out, conn, context, UserId, Database, ClientId, helper);
            } else if (ActionID.equals("GetCovidFilterReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Covid Log report", "Open frontlin_er Covid Log Report Screen", ClientId);
                GetCovidFilterReport(request, out, conn, context, UserId, Database, ClientId, helper);
            } else {
                out.println("Under Development");
            }
            try {
                conn.close();
            } catch (Exception exception) {
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    void GetReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer CDRList = new StringBuffer();
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int SNo = 1;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = formatter.format(date);
        try {
            Query = " SELECT\r\n\tIFNULL(a.FirstName, ''),\r\n\tIFNULL(a.LastName, ''),\r\n\tIFNULL(\r\n\t\tDATE_FORMAT(a.DOB, '%m/%d/%Y'),\"\"),\r\n\t\r\n\tCASE\r\nWHEN b.CovidStatus = 0 THEN\r\n\t\"NEGATIVE\"\r\nWHEN b.CovidStatus = 1 THEN\r\n\t\"POSITIVE\"\r\nWHEN b.CovidStatus = - 1 THEN\r\n\t\"SUSPECTED\"\r\nELSE\r\n\t\"UNEXAMINED\"\r\nEND,\r\n IFNULL(\r\n\tDATE_FORMAT(b.CovidTestDate, '%m/%d/%Y'),\r\n\t''\r\n),\r\n IFNULL(d.PayerName, '')\r\nFROM\r\n\t" + Database + ".PatientReg a\r\nLEFT JOIN " + Database + ".Patient_AdditionalInfo b ON a.ID = b.PatientRegId\r\nLEFT JOIN " + Database + ".InsuranceInfo c ON a.ID = c.PatientRegId\r\nLEFT JOIN " + Database + ".ProfessionalPayers d ON c.PriInsuranceName = d.Id\r\nwhere a.status = 0 and b.CovidTestDate = '" + today + "' order by b.CovidTestDate DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr>");
                CDRList.append("<td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("today", String.valueOf(today));
            Parser.SetField("searchdatefrom", String.valueOf(today));
            Parser.SetField("searchdateto", String.valueOf(today));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportCovidStatus.html");
        } catch (Exception e) {
            out.println("0|");
            try {
                helper.SendEmailWithAttachment("Error in CovidLogReport ", servletContext, e, "CovidLogReport", "skipEPDrequest", conn);
                Services.DumException("CovidLogReport", "GetInput ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientUpdateInfo");
                Parser.SetField("ActionID", "GetReport");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (Exception exception) {
            }
        }
    }

    void GetCovidFilterReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = formatter.format(date);
        try {
            Query = "SELECT\r\n\tIFNULL(a.FirstName, \"\"),\r\n\tIFNULL(a.LastName, \"\"),\r\n\tIFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),\"\"),\r\n\r\nCASE \r\nWHEN b.CovidStatus = 0 THEN 'NEGATIVE'\r\nWHEN b.CovidStatus = 1 THEN 'POSITIVE'\r\nWHEN b.CovidStatus = -1 THEN 'SUSPECTED'\r\nELSE 'UNEXAMINED' END,\r\n\tIFNULL(DATE_FORMAT(b.CovidTestDate, '%m/%d/%Y'),\"\"),\r\n\tIFNULL(d.PayerName, \"\")\r\nFROM\r\n\t" + Database + ".PatientReg a\r\nLEFT JOIN " + Database + ".Patient_AdditionalInfo b\r\nON a.ID = b.PatientRegId\r\nLEFT JOIN " + Database + ".InsuranceInfo c \r\nON a.ID = c.PatientRegId\r\nLEFT JOIN " + Database + ".ProfessionalPayers d\r\nON   d.Id = c.PriInsuranceName where a.Status=0 AND b.CovidTestDate BETWEEN '" + FromDate + "' AND '" + ToDate + "' order by b.CovidTestDate DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr>");
                CDRList.append("<td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("today", String.valueOf(today));
            Parser.SetField("searchdatefrom", String.valueOf(FromDate));
            Parser.SetField("searchdateto", String.valueOf(ToDate));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportCovidStatus.html");
        } catch (Exception e) {
            out.println("0|");
            try {
                helper.SendEmailWithAttachment("Error in CovidLogReport ", servletContext, e, "CovidLogReport", "skipEPDrequest", conn);
                Services.DumException("CovidLogReport", "GetInput ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientUpdateInfo");
                Parser.SetField("ActionID", "GetCovidFilterReport");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (Exception exception) {
            }
        }
    }
}

