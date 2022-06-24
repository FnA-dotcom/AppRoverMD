//
// Decompiled by Procyon v0.5.36
//

package md;

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

public class payacount extends HttpServlet {
    static String DOS = "";
    static String Acct = "";
    static String printabledate = "";

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
//    conn = Services.getMysqlConn();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        try {
//            final Cookie[] cookies = request.getCookies();
//            UserId = (Zone = (Passwd = ""));
//            String UserName = "";
//            final int checkCookie = 0;
//            for (int coky = 0; coky < cookies.length; ++coky) {
//                final String cName = cookies[coky].getName();
//                final String cValue = cookies[coky].getValue();
//                if (cName.equals("UserId")) {
//                    UserId = cValue;
//                }
//            }
//            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            while (rset.next()) {
//                ClientId = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select dbname from oe.clients where Id = " + ClientId;
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            while (rset.next()) {
//                Database = rset.getString(1);
//            }
//            rset.close();
//            stmt.close();

            if (ActionID.equals("paynow")) {
                //supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Installment Plan Input", "Open Installment Plan Input Screem", ClientId);
                this.paynow(request, out, conn, context, UserId, Database, ClientId);
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
            out.println("Error in Handle: " + e.getMessage());
        }
    }


    void paynow(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
//
        try {
            String merchant_id = request.getParameter("merchant_id").trim();
            String employee_id = request.getParameter("employee_id").trim();
            String client_id = request.getParameter("client_id").trim();
//            String access_token = request.getParameter("access_token").trim();

            out.println("merchant_id: " + merchant_id + "<br>");
            out.println("employee_id: " + employee_id + "<br>");
            out.println("client_id: " + client_id + "<br>");
//            out.println("access_token: " + access_token + "<br>");

//            final Parsehtm Parser = new Parsehtm(request);
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/InstallmentPlanInput.html");
        } catch (Exception var11) {
            out.println("Error : " + var11.getMessage());
        }
    }


}
