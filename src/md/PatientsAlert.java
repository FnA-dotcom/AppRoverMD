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

@SuppressWarnings("Duplicates")
public class PatientsAlert extends HttpServlet {

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
            switch (ActionID) {
                case "AddAlert":
                    AddAlert(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "DeleteAlert":
                    DeleteAlert(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                default:
                    out.println("Under Development");
                    break;
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

    private void AddAlert(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {

        String Query = "";
        Statement stmt = null;
        ResultSet rset = null;
        StringBuilder AlertList = new StringBuilder();
        try {
            String Alerts = request.getParameter("Alerts").trim();
            String MRN = request.getParameter("MRN").trim();
            String PatientId = request.getParameter("PatientId").trim();
            PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".Alerts (ClientIndex,PatientRegId,MRN ,"
                    + " Alerts,CreatedDate,CreatedBy) \n"
                    + " VALUES (?,?,?,?,now(),?) ");
            MainReceipt.setInt(1, ClientId);
            MainReceipt.setString(2, PatientId);
            MainReceipt.setString(3, MRN);
            MainReceipt.setString(4, Alerts);
            MainReceipt.setString(5, UserId);
            MainReceipt.executeUpdate();
            MainReceipt.close();

            Query = " Select IFNULL(MRN,''), IFNULL(Alerts,''), IFNULL(DATE_FORMAT(CreatedDate,'%m/%d/%Y %T'),''), IFNULL(CreatedBy,''), Id  " +
                    " from " + Database + ".Alerts where PatientRegId = " + PatientId + " and Status = 0 order by Id desc ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            while (rset.next()) {
                AlertList.append("<div class=\"box\" >\n" +
                        "\t\t\t\t  <div class=\"box-header\" style='height: 50%;'>\n" +
                        "\t\t\t\t\t<h5 class=\"box-title\" style='margin-right: 10%;'>" + rset.getString(2) + "" +
                        "<br><sub>Added by <u>" + rset.getString(4) + "</u> at <u>" + rset.getString(3) + "</u></sub></h5>\n" +
                        "\t\t\t\t\t<div class=\"box-controls pull-right\">\n" +
                        "\t\t\t\t\t  <button class=\"btn btn-xs btn-info\" onclick=\"DeleteAlert(" + rset.getInt(5) + ")\"><i class=\"fa fa-trash\"></i></button>\n" +
                        "\t\t\t\t\t</div>                \n" +
                        "\t\t\t\t  </div>\t\t\t\t  \n" +
                        "\t\t\t\t</div>");
            }
            rset.close();
            stmt.close();

            out.println("1|" + AlertList.toString());
        } catch (Exception e) {
            out.println("Error: Alert Table " + e.getMessage());
            return;
        }


    }

    private void DeleteAlert(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int AlertId = Integer.parseInt(request.getParameter("ID").trim());

        try {
            Query = "Update " + Database + ".Alerts Set Status = 1 where ID = " + AlertId;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            out.println("1");

        } catch (Exception e) {
            out.println("Error: Updating Alerts Table " + e.getMessage());
            Services.DumException("PatientUpdateInfo", "DeleteAlerts 2- Updating Alerts able :", request, e, this.getServletContext());
            return;
        }


    }

}