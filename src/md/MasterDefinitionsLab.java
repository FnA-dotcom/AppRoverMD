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
public class MasterDefinitionsLab extends HttpServlet {

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
                case "addClient":
                    addClient(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex);
                    break;
                case "saveClient":
                    saveClient(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex);
                    break;
                case "getClients":
                    getClients(request, out, conn, context);
                    break;

                case "addLocation":
                    addLocation(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex);
                    break;
                case "saveLocation":
                    saveLocation(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex);
                    break;
                case "getLocations":
                    getLocation(request, out, conn, context);
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
            //helper.SendEmail("Main Section", "Error in Transaction Report", "Message Body");
            helper.SendEmailWithAttachment("Error in MasterDefinition ** (handleRequest)", context, Ex, "MasterDefinition", "handleRequest", conn);
            Services.DumException("MasterDefinitionLab", "Handle Request", request, Ex, getServletContext());
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

    private void addClient(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;
        String facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        StringBuilder Clients = new StringBuilder();
        int SerialNo = 1;
        try {
            Query = "SELECT Id,ClientName,CASE WHEN Status = 0 THEN 'Active' ELSE 'InActive' END " +
                    " FROM roverlab.Clients ORDER BY ClientName ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Clients.append("<tr>");
                Clients.append("<td width=02%>" + SerialNo + "</td>");
                Clients.append("<td width=10%> " + rset.getString(2) + "</td>");
                Clients.append("<td width=10%> " + rset.getString(3) + "</td>");
                Clients.append("<td width=10%><i class=\"fa fa-edit\" data-toggle=\"modal\" data-target=\"#myModal\" onClick=\"editRow(" + rset.getInt(1) + ")\"></i></td>");
                Clients.append("</tr>");
                SerialNo++;
            }
            rset.close();
            stmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Clients", Clients.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "MasterDef/addClients.html");

        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Master Definition ^^ (Occurred At : " + facilityName + ") ** (GetInput)", servletContext, Ex, "MasterDefinition", "addClient", conn);
            Services.DumException("MasterDefinition", "addClient", request, Ex, getServletContext());
        }
    }

    private void saveClient(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex) {
        String Query = null;
        String facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        PreparedStatement pStmt = null;
        try {
            String ClientName = request.getParameter("ClientName").trim();
            int Status = Integer.parseInt(request.getParameter("Status").trim());
            int count = Integer.parseInt(request.getParameter("Count").trim());

            if (count == 0) {
                pStmt = conn.prepareStatement(
                        "INSERT INTO roverlab.Clients (ClientName, Status, CreatedBy, CreatedDate) " +
                                "VALUES (?,?,?,NOW()) ");
                pStmt.setString(1, ClientName);
                pStmt.setInt(2, Status);
                pStmt.setString(3, userId);
                pStmt.executeUpdate();
                pStmt.close();
                out.println("1");
            } else {
                Query = request.getParameter("ClientIndex").trim();
                int ClientIndex = Integer.parseInt(Query);

                pStmt = conn.prepareStatement(
                        "UPDATE roverlab.Clients SET ClientName = ?,Status = ?, " +
                                "ModifyBy = ?,ModifiyDate = NOW() WHERE Id = ? ");
                pStmt.setString(1, ClientName);
                pStmt.setInt(2, Status);
                pStmt.setString(3, userId);
                pStmt.setInt(4, ClientIndex);
                pStmt.executeUpdate();
                pStmt.close();
                out.println("1");
            }

        } catch (Exception Ex) {
            out.println("0");
            helper.SendEmailWithAttachment("Error in Master Definition ^^ (Occurred At : " + facilityName + ") ** (saveClient)", servletContext, Ex, "MasterDefinition", "saveClient", conn);
            Services.DumException("MasterDefinition", "saveClient", request, Ex, getServletContext());
        }

    }

    private void getClients(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext) {

        String Query = "";
        Statement stmt = null;
        ResultSet rset = null;
        String Status = "";
        String chk = "";
        String ClientName = "";
        Query = request.getParameter("ClientIndex").trim();
        int ClientIndex = Integer.parseInt(Query);

        try {
            Query = "Select Id,ClientName,Status from roverlab.Clients where Id=" + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientName = rset.getString(2);
                Status = rset.getString(3);
            }
            rset.close();
            stmt.close();

            out.println(ClientName + "|" + ClientIndex + "|" + Status);
        } catch (Exception var32) {
            chk = "13";
            out.println(String.valueOf(chk));
            out.flush();
            out.close();
            return;
        }
        out.flush();
        out.close();
    }


    private void addLocation(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;
        String facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        StringBuilder Locations = new StringBuilder();
        int SerialNo = 1;
        try {
            Query = "SELECT Id,Location,Address,State,Zip," +
                    "CASE WHEN Status = 0 THEN 'Active' ELSE 'InActive' END " +
                    " FROM roverlab.Locations ORDER BY Location ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Locations.append("<tr>");
                Locations.append("<td width=02%>" + SerialNo + "</td>");
                Locations.append("<td width=10%> " + rset.getString(2) + "</td>");//Location
                Locations.append("<td width=10%> " + rset.getString(3) + "</td>");//Address
                Locations.append("<td width=10%> " + rset.getString(4) + "</td>");//State
                Locations.append("<td width=10%> " + rset.getString(5) + "</td>");//Zip
                Locations.append("<td width=10%> " + rset.getString(6) + "</td>");//Status
                Locations.append("<td width=10%><i class=\"fa fa-edit\" data-toggle=\"modal\" data-target=\"#myModal\" onClick=\"editRow(" + rset.getInt(1) + ")\"></i></td>");
                Locations.append("</tr>");
                SerialNo++;
            }
            rset.close();
            stmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Locations", Locations.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "MasterDef/addLocations.html");

        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Master Definition ^^ (Occurred At : " + facilityName + ") ** (GetInput)", servletContext, Ex, "MasterDefinition", "addClient", conn);
            Services.DumException("MasterDefinition", "addClient", request, Ex, getServletContext());
        }
    }

    private void saveLocation(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex) {
        String Query = null;
        String facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        PreparedStatement pStmt = null;
        try {
            String LocationName = request.getParameter("LocationName").trim();
            String Address = request.getParameter("Address").trim();
            String State = request.getParameter("State").trim();
            String Zip = request.getParameter("Zip").trim();
            int Status = Integer.parseInt(request.getParameter("Status").trim());
            int count = Integer.parseInt(request.getParameter("Count").trim());

            if (count == 0) {
                pStmt = conn.prepareStatement(
                        "INSERT INTO roverlab.Locations (Location, Address, State, Zip, " +
                                "Status, CreatedBy, CreatedDate) " +
                                "VALUES (?,?,?,?,?,?,NOW()) ");
                pStmt.setString(1, LocationName);
                pStmt.setString(2, Address);
                pStmt.setString(3, State);
                pStmt.setString(4, Zip);
                pStmt.setInt(5, Status);
                pStmt.setString(6, userId);
                pStmt.executeUpdate();
                pStmt.close();
                out.println("1");
            } else {
                Query = request.getParameter("LocationIdx").trim();
                int LocationIdx = Integer.parseInt(Query);

                pStmt = conn.prepareStatement(
                        "UPDATE roverlab.Locations SET Location = ?,Address=?,State = ?, " +
                                "Zip=?,Status = ?,ModifyBy = ?,ModifyDate = NOW() " +
                                "WHERE Id = ? ");
                pStmt.setString(1, LocationName);
                pStmt.setString(2, Address);
                pStmt.setString(3, State);
                pStmt.setString(4, Zip);
                pStmt.setInt(5, Status);
                pStmt.setString(6, userId);
                pStmt.setInt(7, LocationIdx);
                pStmt.executeUpdate();
                pStmt.close();
                out.println("1");
            }

        } catch (Exception Ex) {
            out.println("0");
            helper.SendEmailWithAttachment("Error in Master Definition ^^ (Occurred At : " + facilityName + ") ** (saveLocation)", servletContext, Ex, "MasterDefinition", "saveLocation", conn);
            Services.DumException("MasterDefinition", "saveLocation", request, Ex, getServletContext());
        }

    }

    private void getLocation(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext) {

        String Query = "";
        Statement stmt = null;
        ResultSet rset = null;
        String Status = "";
        String chk = "";
        String LocationName = "";
        String Address = "";
        String State = "";
        String Zip = "";
        Query = request.getParameter("LocationIndex").trim();
        int LocationIndex = Integer.parseInt(Query);

        try {
            Query = "Select Id,Location,Address,State,Zip,Status " +
                    "from roverlab.Locations where Id=" + LocationIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                LocationName = rset.getString(2);
                Address = rset.getString(2);
                State = rset.getString(2);
                Zip = rset.getString(2);
                Status = rset.getString(3);
            }
            rset.close();
            stmt.close();

            out.println("'" + LocationName + "'|'" + Address + "'|'" + State + "'|" + Zip + "|" + LocationIndex + "|" + Status);
        } catch (Exception var32) {
            chk = "13";
            out.println(String.valueOf(chk));
            out.flush();
            out.close();
            return;
        }
        out.flush();
        out.close();
    }

}
