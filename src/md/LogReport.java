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
import java.sql.*;

public class LogReport extends HttpServlet {
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
        try {
            if (ActionID.equals("GetReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get request table report", "Open oe request Report Screen", ClientId);
                this.GetReport(request, out, conn, context, UserId, Database, ClientId, helper);
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
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String ClaimNo=request.getParameter("ClaimNo").trim();
        int SNo =0;
        try {


     //
            CDRList.append("<div class=\"table-responsive\">");
            CDRList.append("<table id=\"example\" class=\"table table-striped mb-0\" style=\"width:100%\">");
            CDRList.append("<thead style=\"color:black;\">");
            CDRList.append("<tr  >");
            CDRList.append("<th style=\"font-size:18px;font-weight:bold\">Task</th>");
            CDRList.append("<th style=\"font-size:18px;font-weight:bold\">Action</th>");
            CDRList.append("<th style=\"font-size:18px;font-weight:bold\">Created Date </th>");
            CDRList.append("<th style=\"font-size:18px;font-weight:bold\">UserID </th>");
            CDRList.append("</tr>");
            CDRList.append("<tbody style=\"color:black;\">");
            try {
                PreparedStatement ps = conn.prepareStatement("SELECT RuleText,Action,b.userid,CreatedAt" +
                        " FROM "+Database+".Claim_AuditTrails a " +
                        " INNER JOIN oe.sysusers b ON a.UserID=b.indexptr " +
                        " where ClaimNo=? ORDER BY CreatedAt DESC");
                ps.setString(1,ClaimNo);
                rset = ps.executeQuery();
                while(rset.next()){
                    CDRList.append("<tr  >");
                    CDRList.append("<td >"+rset.getString(1)+"</td>");
                    CDRList.append("<td >"+rset.getString(2)+"</td>");
                    CDRList.append("<td >"+rset.getString(3)+"</td>");
                    CDRList.append("<td >"+rset.getString(4)+"</td>");
                    CDRList.append("</tr>");
                }
                rset.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("ClaimNo", String.valueOf(ClaimNo));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/LogReport.html");
   
            
        }catch (Exception var11) {
            out.println(var11.getMessage());
        }
    }
}
