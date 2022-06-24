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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class BundleForms extends HttpServlet {

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
        String UserId = "";
        int FacilityIndex = 0;
        String DatabaseName = "";
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();

        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();
        UtilityHelper helper = new UtilityHelper();

        try {
            HttpSession session = request.getSession(false);


            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

            if (session.getAttribute("UserId") != null || !session.getAttribute("UserId").equals("")) {
                UserId = session.getAttribute("UserId").toString();
                DatabaseName = session.getAttribute("DatabaseName").toString();
                FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());

                if (UserId.equals("")) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            }

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.equals("GetInput")) {
                GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("SaveBundleForm")) {
                SaveBundleForm(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else {
                out.println("Under Development");
            }
        } catch (Exception e) {
            out.println("Exception in main... " + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
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

    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String ID = request.getParameter("ID").trim();
            String pattern = null;

            Query = "SELECT Form_ids FROM " + Database + ".BundleForms where PatientRegId='" + ID + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                pattern = rset.getString(1);
            }
            rset.close();
            stmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ptrn", String.valueOf(pattern));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/BundleForms.html");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void SaveBundleForm(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            String ID = request.getParameter("ID").trim();
            String pattern = request.getParameter("pattern").trim();
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            int null_data_found = 0;


            Query = "SELECT COUNT(*) FROM " + Database + ".BundleForms where PatientRegId='" + ID + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            while (rset.next()) {
                null_data_found = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            if (null_data_found == 0) {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".BundleForms (PatientRegId,Form_ids) VALUE(?,?)");

                MainReceipt.setString(1, ID);
                MainReceipt.setString(2, pattern);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } else {
                PreparedStatement MainReceipt = conn.prepareStatement("UPDATE  " + Database + ".BundleForms  SET Form_ids = '" + pattern + "'  WHERE PatientRegId = '" + ID + "'");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }

            out.println("1");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

}