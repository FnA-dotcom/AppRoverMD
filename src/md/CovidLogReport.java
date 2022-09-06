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
/*    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";*/
//
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
        ResultSet rset = null;
        Statement stmt = null;
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        String Database = "";
        String Query = "";
        int ClientId = 0;
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();
        ServletContext context = null;
        context = this.getServletContext();
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
                this.GetReport(request, out, conn, context, UserId, Database, ClientId, helper);
            
    
            }else if (ActionID.equals("GetCovidFilterReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Covid Log report", "Open frontlin_er Covid Log Report Screen", ClientId);
               this.GetCovidFilterReport(request, out, conn, context, UserId, Database, ClientId, helper);
            }
            else {
                out.println("Under Development");
            }
            try {
                conn.close();
            } catch (Exception ex) {
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }


    void GetReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) {
        SupportiveMethods suppMethods = new SupportiveMethods();

        StringBuffer CDRList = new StringBuffer();
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
   
        int SNo = 1;
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    	Date date = new Date();
String today= formatter.format(date);
if(ClientId == 27 || ClientId == 29) {
        try {

     
  
            
//        	  CDRList.append("<div class=\"table-responsive\">");  
//            CDRList.append("<table	id=\"complex_header\" class=\"table table-striped table-bordered display\" style=\"width:100%\">");            
//            CDRList.append("<thead  style=\"color:black;\" >");
//            CDRList.append("<tr>");
//            CDRList.append("<td align=left>First Name</td>");
//            CDRList.append("<td align=left>Last Name</td>");
//            CDRList.append("<td align=left>Date of Birth</td>");
//            CDRList.append("<td align=left>Covid Test Date</td>");
//            CDRList.append("<td align=left>Covid Status</td>");
//            CDRList.append("<td align=left>Insurrance</td>");
//            CDRList.append("</tr>"); 
//            CDRList.append("</thead>");
//            CDRList.append("<tbody  style=\"color:black;\">");
            
            
            Query=    " SELECT\r\n"
            		+ "	IFNULL(a.FirstName, ''),\r\n"
            		+ "	IFNULL(a.LastName, ''),\r\n"
            		+ "	IFNULL(\r\n"
            		+ "		DATE_FORMAT(a.DOB, '%m/%d/%Y'),\"\"),\r\n"
            		+ "	\r\n"
            		+ "	CASE\r\n"
            		+ "WHEN b.CovidStatus = 0 THEN\r\n"
            		+ "	\"NEGATIVE\"\r\n"
            		+ "WHEN b.CovidStatus = 1 THEN\r\n"
            		+ "	\"POSITIVE\"\r\n"
            		+ "WHEN b.CovidStatus = - 1 THEN\r\n"
            		+ "	\"SUSPECTED\"\r\n"
            		+ "ELSE\r\n"
            		+ "	\"UNEXAMINED\"\r\n"
            		+ "END,\r\n"
            		+ " IFNULL(\r\n"
            		+ "	DATE_FORMAT(b.CovidTestDate, '%m/%d/%Y'),\r\n"
            		+ "	''\r\n"
            		+ "),\r\n"
            		+ " IFNULL(d.PayerName, '')\r\n"
            		+ "FROM\r\n"
            		+ "	"+Database+".PatientReg a\r\n"
            		+ "LEFT JOIN "+Database+".Patient_AdditionalInfo b ON a.ID = b.PatientRegId\r\n"
            		+ "LEFT JOIN "+Database+".InsuranceInfo c ON a.ID = c.PatientRegId\r\n"
            		+ "LEFT JOIN "+Database+".ProfessionalPayers d ON c.PriInsuranceName = d.Id\r\n"
            		+ "where a.status = 0 and b.CovidTestDate = '"+today+"' order by b.CovidTestDate DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

//            CDRList.append("</thead >");            
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

                   
//        
//            CDRList.append("</tbody >");
//            CDRList.append("</table >");
//            CDRList.append("</div >");
      
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
            } catch (Exception e2) {
            }
//
        }
}
    }

    void GetCovidFilterReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) {
        SupportiveMethods suppMethods = new SupportiveMethods();
    
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
//        String CovidStatus = request.getParameter("CovidStatus").trim();
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    	Date date = new Date();
String today= formatter.format(date);
if(ClientId == 27 || ClientId == 29) {
        try {
//        	 CDRList.append("<div class=\"table-responsive\">"); 
//        	    CDRList.append("<table	id=\"complex_header\" class=\"table table-striped table-bordered display\" style=\"width:100%\">");            
//                CDRList.append("<thead  style=\"color:black;\" >");
//        
//            CDRList.append("<tr>");
//            CDRList.append("<td align=left>First Name</td>");
//            CDRList.append("<td align=left>Last Name</td>");
//            CDRList.append("<td align=left>Date of Birth</td>");
//            CDRList.append("<td align=left>Covid Test Date</td>");
//            CDRList.append("<td align=left>Covid Status</td>");
//            CDRList.append("<td align=left>Insurrance</td>");
//            CDRList.append("</tr>");
//            CDRList.append("</thead >");
//            CDRList.append("<tbody  style=\"color:black;\">");
            Query = "SELECT\r\n"
            		+ "	IFNULL(a.FirstName, \"\"),\r\n"
            		+ "	IFNULL(a.LastName, \"\"),\r\n"
            		+ "	IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),\"\"),\r\n"
            		+ "\r\n"
            		+ "CASE \r\n"
            		+ "WHEN b.CovidStatus = 0 THEN 'NEGATIVE'\r\n"
            		+ "WHEN b.CovidStatus = 1 THEN 'POSITIVE'\r\n"
            		+ "WHEN b.CovidStatus = -1 THEN 'SUSPECTED'\r\n"
            		+ "ELSE 'UNEXAMINED' END,\r\n"
            		+ "	IFNULL(DATE_FORMAT(b.CovidTestDate, '%m/%d/%Y'),\"\"),\r\n"
            		+ "	IFNULL(d.PayerName, \"\")\r\n"
            		+ "FROM\r\n"
            		+ "	"+Database+".PatientReg a\r\n"
            		+ "LEFT JOIN "+Database+".Patient_AdditionalInfo b\r\n"
            		+ "ON a.ID = b.PatientRegId\r\n"
            		+ "LEFT JOIN "+Database+".InsuranceInfo c \r\n"
            		+ "ON a.ID = c.PatientRegId\r\n"
            		+ "LEFT JOIN "+Database+".ProfessionalPayers d\r\n"
            		+ "ON   d.Id = c.PriInsuranceName where a.Status=0 AND b.CovidTestDate BETWEEN '"+FromDate+"' AND '"+ToDate+"' order by b.CovidTestDate DESC";
            //out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            while (rset.next()) {
                CDRList.append("<tr>");
                CDRList.append("<td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");//PhNumber
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
//            CDRList.append("</tbody >");
//            CDRList.append("</table >");
//  out.println("CDRList");
        } catch (Exception e) {
            out.println("0|");
            try {
                helper.SendEmailWithAttachment("Error in CovidLogReport ", servletContext, e, "CovidLogReport", "skipEPDrequest", conn);
                Services.DumException("CovidLogReport", "GetInput ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientUpdateInfo");
                Parser.SetField("ActionID", "GetCovidFilterReport");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (Exception e2) {
            }
//
        }
}
    
    }

    }
