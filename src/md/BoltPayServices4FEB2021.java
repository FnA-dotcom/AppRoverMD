package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
public class BoltPayServices4FEB2021 extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private PreparedStatement pStmt = null;
    private Connection conn = null;

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        requestHandling(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        requestHandling(request, response);
    }

    public void requestHandling(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        int ClientId = 0;
        String Database = "";
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());

        String ServiceRequests = request.getParameter("ServiceRequests").trim();
        ServletContext context = null;
        context = this.getServletContext();
        Services supp = new Services();
        conn = Services.getMysqlConn(context);

        Cookie[] cookies = request.getCookies();
        Zone = (UserId = (Passwd = ""));
        final int checkCookie = 0;
        for (int coky = 0; coky < cookies.length; ++coky) {
            final String cName = cookies[coky].getName();
            final String cValue = cookies[coky].getValue();
            if (cName.equals("UserId")) {
                UserId = cValue;
            }
        }
        try {
            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientId = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select dbname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();


            try {
                UtilityHelper helper = new UtilityHelper();
                switch (ServiceRequests) {
                    case "GetDetails":
                        GetDetails(request, conn, helper, context, out, Database);
                        break;
                    default:
                        out.println("Under Development");
                        break;
                }
            } catch (Exception Ex) {
                out.println("Error in Main " + Ex.getMessage());
            }

        } catch (Exception e) {
            out.println("Error in DB Connection " + e.getMessage());
        }
        out.flush();
        out.close();
    }

    private void GetDetails(HttpServletRequest req, Connection conn, UtilityHelper helper, ServletContext context, PrintWriter out, String database) throws IOException {
        stmt = null;
        rset = null;
        Query = "";
        //Type Flag
        // 0 - Sandbox
        // 1 - Dev
        // 2 - Prod
        int FlagType = 0;

        String InvoiceNo = req.getParameter("x0Y61008").trim();

        try {
            String[] BoltConnect = helper.getBoltCredential(conn, FlagType);
            /*out.println("Site " + BoltConnect[0] + "<br>");
            out.println("URL " + BoltConnect[1] + "<br>");
            out.println("Currency " + BoltConnect[2] + "<br>");*/

            Parsehtm Parser = new Parsehtm(req);
            Parser.SetField("x0Y61008", InvoiceNo);
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/BoltFrontEnd.html");
        } catch (Exception Ex) {
            out.println("Error " + Ex.getMessage());
        }


    }
}
