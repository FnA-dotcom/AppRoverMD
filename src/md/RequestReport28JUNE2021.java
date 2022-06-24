//
// Decompiled by Procyon v0.5.36
//

package md;

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
import java.sql.Statement;

public class RequestReport28JUNE2021 extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";


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
        conn = Services.getMysqlConn(context);
        try {
            if (ActionID.equals("GetReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get request table report", "Open oe request Report Screen", ClientId);
                this.GetReport(request, out, conn, context, UserId, Database, ClientId);
            } else {
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


    void GetReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        try {


            CDRList.append("<div class=\"table-responsive\">");
            CDRList.append("<table class=\"table table-striped mb-0\" style=\"width:100%\">");
            CDRList.append("<thead style=\"color:black;\">");
            CDRList.append("<tr>");
            CDRList.append("<th >Id</th>");
            CDRList.append("<th >ClientIndex</th>");
            CDRList.append("<th >MSG</th>");
            CDRList.append("<th >RequestDate</th>");
            CDRList.append("<th >PostTime</th>");
            CDRList.append("<th >Status</th>");
            CDRList.append("<th >RequestType</th>");
            CDRList.append("<th >MRN</th>");
            CDRList.append("<th >Flag</th>");
            CDRList.append("<th >Response</th>");
            CDRList.append("<th >ResponseCode</th>");
            CDRList.append("<th >MSCID</th>");
            CDRList.append("</tr>");
            CDRList.append("<tbody style=\"color:black;\">");

            Query = "Select a.Id, a.msg, a.requestdate, a.posttime, a.status, a.RequestType, a.mrn, a.flag, a.ClientIndex,  a.Response, " +
                    "a.ResponseCode, a.MSCID,b.name \n" +
                    " from oe.request a " +
                    " STRAIGHT_JOIN oe.clients b ON a.ClientIndex = b.Id " +
                    " order by Id desc limit 50";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                if (rset.getString(10) == null) {
                    CDRList.append("<tr style=\"background-color: #FF7C60;\"><td align=left>" + rset.getInt(1) + "</td>\n");
                    //CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(13) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");

                    CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(12) + "</td>\n");
                    CDRList.append("</tr>");
                } else {
                    CDRList.append("<tr style=\"background-color: 60FF66;\"><td align=left>" + rset.getInt(1) + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(13) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");

                    CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(12) + "</td>\n");
                    CDRList.append("</tr>");
                }
            }
            rset.close();
            stmt.close();

            CDRList.append("</tbody>");
            CDRList.append("</table>");
            CDRList.append("</div>");

            conn.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/RequestTableReport.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
        }
    }

}


