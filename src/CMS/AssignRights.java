//
// Decompiled by Procyon v0.5.36
//

package CMS;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import md.FacilityLogin;
import md.Services;

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
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class AssignRights extends HttpServlet {

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
        String ActionID;
        Services supp = new Services();

        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        int UserIndex = 0;
        UtilityHelper helper = new UtilityHelper();
        //FacilityLogin facilityLogin = new FacilityLogin();
        try {
            HttpSession session = request.getSession(false);

            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());

            try {
                boolean ValidSession = FacilityLogin.checkSession(out, request, context, response);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    return;
                }
                if (UserId.equals("")) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            } catch (Exception excp) {
                conn = null;
                out.println("Exception excp conn: " + excp.getMessage());
            }

            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);


            switch (ActionID) {
                case "GetInput":
                    this.GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "SaveRights":
                    this.SaveRights(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "GetRights":
                    this.GetRights(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "GetRightsForScreen":
                    this.GetRightsForScreen(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "ManagementDashboard");
                    Parser.SetField("ActionID", "GetInput");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }
        } catch (Exception e) {
            out.println("Exception in main... " + e.getMessage());
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

    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) throws IOException {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer users = new StringBuffer();
        StringBuffer checkboxes = new StringBuffer();
        List<Integer> Ordering = new ArrayList<Integer>();
        try {

            Query = "SELECT indexptr , username FROM oe.sysusers where usertype = 4 or usertype=7 or usertype=10";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            users.append("<option value='' selected disabled>Select Employee</option>");
            while (rset.next())
                users.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            rset.close();
            stmt.close();


            Query = "SELECT DISTINCT(Ordering) FROM oe.ScreenNames";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                Ordering.add(rset.getInt(1));
            rset.close();
            stmt.close();


            for (int i = 0; i < Ordering.size(); i++) {
                Query = "SELECT id,ScreenLevel , ScreenName, ScreenLink FROM oe.ScreenNames where Status=0 and Ordering = " + Ordering.get(i);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    if (rset.getString(2).equals("2")) {
                        checkboxes.append("<div class=\"form-check\">\n" +
//                                "  <input class=\"form-check-input\" type=\"checkbox\" value=\"\" id=\"rights_" + rset.getString(3).replaceAll(" ", "") + "\" onClick=\"UncheckAdmin();\">\n" +
//                                "  <label class=\"form-check-label\" for=\"rights_" + rset.getString(3).replaceAll(" ", "") + "\">\n" +
                                "  <input class=\"form-check-input\" type=\"checkbox\" value='" + rset.getString(4) + "' id=\"rights_" + rset.getString(1) + "\" onClick=\"UncheckAdmin();\" >\n" +
                                "  <label class=\"form-check-label\" for=\"rights_" + rset.getString(1) + "\">\n" +
                                " " + rset.getString(3) + "\n" +
                                "  </label>\n" +
                                "</div>");
                    } else if (rset.getString(2).equals("1")) {
                        checkboxes.append("<br><div class=\"row\">\n" +
                                "     <label for=\"\"><b>" + rset.getString(3) + " :</b></label>\n" +
                                " </div>");
                    }
                }
                rset.close();
                stmt.close();
            }

            checkboxes.append("<button type=\"button\" class=\"btn btn-primary\" onclick=\"SaveRights();\">\n" +
                    "                          Save\n" +
                    "                      </button>");


            Ordering.clear();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("users", String.valueOf(users));
            Parser.SetField("checkboxes", String.valueOf(checkboxes));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/AssignRights.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());

            Parsehtm Parser = new Parsehtm(request);

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void SaveRights(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) throws IOException {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer users = new StringBuffer();
        StringBuffer checkboxes = new StringBuffer();
        String[] rightsPolicy = new String[0];
        String Employee_id = null;
        String IsAdmin = null;


        try {
            rightsPolicy = request.getParameter("rightsPolicy").trim().split(",");
            Employee_id = request.getParameter("Employee_id").trim();
            IsAdmin = request.getParameter("IsAdmin").trim();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            int null_data_found = 0;


            Query = "SELECT COUNT(*) FROM oe.UserRights where SysUserID='" + Employee_id + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            while (rset.next()) {
                null_data_found = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            if (null_data_found == 0) {
                insertRights(Employee_id, rightsPolicy, IsAdmin, conn);
            } else {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "DELETE FROM oe.UserRights WHERE SysUserID = '" + Employee_id + "' ;");
                MainReceipt.executeUpdate();
                MainReceipt.close();
                insertRights(Employee_id, rightsPolicy, IsAdmin, conn);
            }


            out.println("1");
        } catch (Exception var11) {

            Parsehtm Parser = new Parsehtm(request);

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    private void insertRights(String Employee_id, String[] rightsPolicy, String IsAdmin, Connection conn) {
        try {
            for (int i = 0; i < rightsPolicy.length; i++) {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO oe.UserRights (SysUserID,ScreenIdx,IsAdmin) VALUE(?,?,?)");

                MainReceipt.setString(1, Employee_id);
                MainReceipt.setString(2, rightsPolicy[i]);
                MainReceipt.setString(3, IsAdmin);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void GetRights(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) throws IOException {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer users = new StringBuffer();
        StringBuffer checkboxes = new StringBuffer();
        String Employee_id = request.getParameter("Employee_id").trim();
        List<Integer> Ordering = new ArrayList<>();
        List<Integer> rightsPolicy = new ArrayList<>();

        try {
            int IsAdmin = 0;
            int null_data_found = 0;
            Query = "SELECT DISTINCT(Ordering) FROM oe.ScreenNames";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                Ordering.add(rset.getInt(1));
            rset.close();
            stmt.close();

            Query = "SELECT Count(*),isAdmin FROM oe.UserRights where SysUserID='" + Employee_id + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                null_data_found = rset.getInt(1);
                IsAdmin = rset.getInt(2);
            }
            rset.close();
            stmt.close();


            if (null_data_found > 0) {
                if (IsAdmin == 1) {
                    for (int i = 0; i < Ordering.size(); i++) {
                        Query = "SELECT id,ScreenLevel , ScreenName,ScreenLink FROM oe.ScreenNames where Status=0 and Ordering = " + Ordering.get(i);
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        while (rset.next()) {
                            if (rset.getString(2).equals("2")) {
                                checkboxes.append("<div class=\"form-check\">\n" +
                                        "  <input class=\"form-check-input\" type=\"checkbox\"  id=\"rights_" + rset.getString(1) + "\" onClick=\"UncheckAdmin();\" checked value=" + rset.getString(4) + ">\n" +
                                        "  <label class=\"form-check-label\" for=\"rights_" + rset.getString(1) + "\">\n" +
                                        " " + rset.getString(3) + "\n" +
                                        "  </label>\n" +
                                        "</div>");
                            } else if (rset.getString(2).equals("1")) {
                                checkboxes.append("<br><div class=\"row\">\n" +
                                        "     <label for=\"\"><b>" + rset.getString(3) + " :</b></label>\n" +
                                        " </div>");
                            }
                        }
                        rset.close();
                        stmt.close();
                    }

                    checkboxes.append("<button type=\"button\" class=\"btn btn-primary\" onclick=\"SaveRights();\">\n" +
                            "                          Save\n" +
                            "                      </button>");
                    out.println("1~" + checkboxes);
                } else {
                    Query = "SELECT ScreenIdx  FROM oe.UserRights where  SysUserID= " + Employee_id;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        rightsPolicy.add(rset.getInt(1));
                    }
                    rset.close();
                    stmt.close();

                    for (int i = 0; i < Ordering.size(); i++) {
                        Query = "SELECT id,ScreenLevel , ScreenName,ScreenLink FROM oe.ScreenNames where Status=0 and Ordering = " + Ordering.get(i);
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        while (rset.next()) {
                            if (rset.getString(2).equals("2")) {
                                if (rightsPolicy.contains(rset.getInt(1))) {
                                    checkboxes.append("<div class=\"form-check\">\n" +
                                            "  <input class=\"form-check-input\" type=\"checkbox\" value='" + rset.getString(4) + "' id=\"rights_" + rset.getString(1) + "\" onClick=\"UncheckAdmin();\" checked>\n" +
                                            "  <label class=\"form-check-label\" for=\"rights_" + rset.getString(1) + "\">\n" +
                                            " " + rset.getString(3) + "\n" +
                                            "  </label>\n" +
                                            "</div>");
                                } else {
                                    checkboxes.append("<div class=\"form-check\">\n" +
                                            "  <input class=\"form-check-input\" type=\"checkbox\" value='" + rset.getString(4) + "' id=\"rights_" + rset.getString(1) + "\" onClick=\"UncheckAdmin();\" >\n" +
                                            "  <label class=\"form-check-label\" for=\"rights_" + rset.getString(1) + "\">\n" +
                                            " " + rset.getString(3) + "\n" +
                                            "  </label>\n" +
                                            "</div>");
                                }
                            } else if (rset.getString(2).equals("1")) {
                                checkboxes.append("<br><div class=\"row\">\n" +
                                        "     <label for=\"\"><b>" + rset.getString(3) + " :</b></label>\n" +
                                        " </div>");
                            }

                        }
                        rset.close();
                        stmt.close();
                    }
                    checkboxes.append("<button type=\"button\" class=\"btn btn-primary\" onclick=\"SaveRights();\">\n" +
                            "                          Save\n" +
                            "                      </button>");
                    out.println("999~" + checkboxes);
                }
            } else {
                for (int i = 0; i < Ordering.size(); i++) {
                    Query = "SELECT id,ScreenLevel , ScreenName, ScreenLink FROM oe.ScreenNames where Status=0 and Ordering = " + Ordering.get(i);
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        if (rset.getString(2).equals("2")) {
                            checkboxes.append("<div class=\"form-check\">\n" +
                                    "  <input class=\"form-check-input\" type=\"checkbox\" value='" + rset.getString(4) + "' id=\"rights_" + rset.getString(1) + "\" onClick=\"UncheckAdmin();\">\n" +
                                    "  <label class=\"form-check-label\" for=\"rights_" + rset.getString(1) + "\">\n" +
                                    " " + rset.getString(3) + "\n" +
                                    "  </label>\n" +
                                    "</div>");
                        } else if (rset.getString(2).equals("1")) {
                            checkboxes.append("<br><div class=\"row\">\n" +
                                    "     <label for=\"\"><b>" + rset.getString(3) + " :</b></label>\n" +
                                    " </div>");
                        }
                    }
                    rset.close();
                    stmt.close();
                }

                checkboxes.append("<button type=\"button\" class=\"btn btn-primary\" onclick=\"SaveRights();\">\n" +
                        "                          Save\n" +
                        "                      </button>");
                out.println("999~" + checkboxes);
            }
            Ordering.clear();
            rightsPolicy.clear();
        } catch (Exception var11) {
            out.println(var11.getMessage());

            Parsehtm Parser = new Parsehtm(request);

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void GetRightsForScreen(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) throws IOException {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Employee_id = request.getParameter("Employee_id").trim();
        try {

            String rightsPolicy = "";
            int null_data_found = 0;

            Query = "SELECT COUNT(*) FROM oe.UserRights where SysUserID='" + Employee_id + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                null_data_found = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (null_data_found > 0) {
                Query = "SELECT" +
                        " b.ScreenName " +
                        " FROM " +
                        " `UserRights` a " +
                        "RIGHT JOIN ScreenNames b ON a.ScreenIdx = b.id " +
                        "WHERE " +
                        " a.SysUserID =" + Employee_id;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);

                while (rset.next()) {
                    rightsPolicy += rset.getString(1).replaceAll(" ", "") + "^";
                }
                rset.close();
                stmt.close();
                out.println(rightsPolicy);
                return;
            } else {
                out.println("0");
            }

        } catch (Exception var11) {

            out.println(var11.getMessage());
            Parsehtm Parser = new Parsehtm(request);

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

}
