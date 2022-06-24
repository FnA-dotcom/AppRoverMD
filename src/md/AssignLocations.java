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
public class AssignLocations extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        serviceHandling(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        serviceHandling(request, response);
    }

    public void serviceHandling(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String Action;
        Connection conn = null;
        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        String UserIndex;
        String DatabaseName;
        UtilityHelper helper = new UtilityHelper();
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
            UserIndex = session.getAttribute("UserIndex").toString();

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            Action = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (Action) {
                case "showData":
                    showData(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex);
                    break;
                case "SaveData":
                    SaveData(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex);
                    break;
                case "getLocations":
                    getLocations(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in AssignLocations ** (handleRequest)", context, Ex, "AssignLocations", "handleRequest", conn);
            Services.DumException("AssignLocations", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
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

    private void showData(HttpServletRequest request, PrintWriter out, Connection conn,
                          ServletContext servletContext, String userId, String database,
                          UtilityHelper helper, int facilityIndex) {
        String facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;
        StringBuilder UserList = new StringBuilder();

        try {
            Query = "Select indexptr, IFNULL(username,'') from oe.sysusers " +
                    "where clientid = " + facilityIndex + " AND usertype = 11 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            UserList.append("<option value=''> Select User </option>");
            while (rset.next()) {
                UserList.append("<option value=" + rset.getInt(1) + "> " + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserList", UserList.toString());

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "MasterDef/assignLocation.html");

        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Assigning Location ^^ (Occurred At : " + facilityName + ") ** (showData)", servletContext, Ex, "AssignLocations", "showData", conn);
            Services.DumException("AssignLocations", "showData", request, Ex, getServletContext());
        }
    }

    private void getLocations(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;

        try {
            StringBuilder locationListsCHK = new StringBuilder();
            int userIdx = Integer.parseInt(request.getParameter("userIdx"));
            String LocationIdx = "";

            Query = "SELECT a.LocationIndex,b.Location FROM roverlab.Location_mapping a " +
                    " INNER JOIN roverlab.Locations b ON a.LocationIndex = b.Id " +
                    " WHERE a.UserIndex = " + userIdx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                LocationIdx += rset.getString(1) + ",";
                locationListsCHK.append("<input type=\"checkbox\" id=\"Facility_" + rset.getInt(1) + "\" name=\"Facility_" + rset.getInt(1) + "\" class=\"filled-in chk-col-info\" checked/> <label for=\"Facility_" + rset.getInt(1) + "\">" + rset.getString(2) + "</label> \t");
            }
            rset.close();
            stmt.close();

            if (!LocationIdx.equals("")) {
                LocationIdx = LocationIdx.substring(0, LocationIdx.length() - 1);
            }

            if (!LocationIdx.equals("")) {
                Query = " Select Id, IFNULL(Location,'') from roverlab.Locations " +
                        "where Id not in (" + LocationIdx + ") ";
            } else {
                Query = "SELECT Id,Location FROM roverlab.Locations ";
            }
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                locationListsCHK.append("<input type=\"checkbox\" id=\"Facility_" + rset.getInt(1) + "\" name=\"Facility_" + rset.getInt(1) + "\" class=\"filled-in chk-col-info\"/> <label for=\"Facility_" + rset.getInt(1) + "\">" + rset.getString(2) + "</label> \t");
            }
            rset.close();
            stmt.close();

            out.println(locationListsCHK.toString());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void SaveData(HttpServletRequest request, PrintWriter out, Connection conn,
                         ServletContext context, String UserId, String Database,
                         UtilityHelper helper, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;
        String Locations = "";
        PreparedStatement pStmt = null;
        StringBuilder UserList = new StringBuilder();
        try {
            int userIdx = Integer.parseInt(request.getParameter("UserIdx").trim());

            Query = "Delete from roverlab.Location_mapping where UserIndex = " + userIdx;
            System.out.println(Query);
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            Query = "SELECT Id,Location FROM roverlab.Locations ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Locations = "";
                Locations = CheckCheckBoxValue(request, "Facility_" + rset.getInt(1));
                if (Locations.equals("1")) {
                    pStmt = conn.prepareStatement(
                            "INSERT INTO roverlab.Location_mapping(UserIndex, LocationIndex, Status) " +
                                    " VALUES (?,?,0) ");
                    pStmt.setInt(1, userIdx);
                    pStmt.setInt(2, rset.getInt(1));
                    pStmt.executeUpdate();
                    pStmt.close();
                }
            }
            rset.close();
            stmt.close();

            Query = "Select indexptr, IFNULL(username,'') from oe.sysusers " +
                    "where clientid = " + ClientId + " AND usertype = 11 ";
            System.out.println("SEC " + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            UserList.append("<option value=''> Select User </option>");
            while (rset.next()) {
                UserList.append("<option value=" + rset.getInt(1) + "> " + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Locations has been assinged!");
            Parser.SetField("FormName", "AssignLocations");
            Parser.SetField("ActionID", "showData");
            Parser.SetField("ClientIndex", String.valueOf(ClientId));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/Message.html");

        } catch (Exception e) {
            //out.println(e.getMessage());
            System.out.println(e.getMessage());
            return;
        }
    }

    private String CheckCheckBoxValue(HttpServletRequest request, String VariableName) {
        try {

            if (request.getParameter(VariableName) == null) {
                //System.out.println("isnide IF:-"+VariableName);
                VariableName = "0";
            } else {
//                System.out.println("isnide ELSE:-"+VariableName);
                VariableName = request.getParameter(VariableName).trim();
                if (VariableName.equals("on")) {
//                    System.out.println("Iside ON--"+VariableName);
                    VariableName = "1";
                } else {
//                    System.out.println("Iside NOT ON--"+VariableName);
                    VariableName = "0";
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage() + "VariableName" + VariableName);
        }
        return VariableName;
    }
}
