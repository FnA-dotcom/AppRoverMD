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
public class CreateLabUser extends HttpServlet {
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
                case "createUser":
                    createUser(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex);
                    break;
                case "saveLabUser":
                    saveLabUser(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex);
                    break;

                case "editLabUser":
                    editLabUser(request, out, conn);
                    break;
                case "updateLabUser":
                    updateLabUser(request, out, conn);
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
            helper.SendEmailWithAttachment("Error in CreateLabUser ** (handleRequest)", context, Ex, "CreateLabUser", "handleRequest", conn);
            Services.DumException("CreateLabUser", "Handle Request", request, Ex, getServletContext());
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

    private void createUser(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;
        Statement stmt1 = null;
        ResultSet rset1 = null;
        String Query1 = null;
        String facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        StringBuilder LabUsers = new StringBuilder();
        StringBuilder Location = new StringBuilder();
        int SerialNo = 1;
        String LocationName = "";
        try {
            Query = "SELECT Id, CONCAT(Location , '-' , Address ,' ',State )  FROM roverlab.Locations ORDER BY Location";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            Location.append("<option value=''>Select Location</option>");
            while (rset.next()) {
                Location.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Query = "SELECT a.indexptr,a.userid,a.password,a.firstname,a.lastname," +
                    "CASE WHEN a.Status = 0 THEN 'Active' ELSE 'InActive' END " +
                    " FROM oe.sysusers a " +
                    " WHERE a.usertype = 11 " +
                    " ORDER BY a.create_date DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
//                LocationName = "";
//                Query1 = "SELECT LocationIndex, b.Location " +
//                        " FROM roverlab.Location_mapping a " +
//                        " INNER JOIN roverlab.Locations b ON a.LocationIndex = b.Id " +
//                        " WHERE UserIndex = " + rset.getInt(1);
//                stmt1 = conn.createStatement();
//                rset1 = stmt1.executeQuery(Query1);
//                while (rset1.next()){
//                    LocationName = rset1.getString(2);
//                }
//                rset1.close();
//                stmt1.close();

                LabUsers.append("<tr>");
                LabUsers.append("<td width=10%>" + rset.getString(4) + " " + rset.getString(5) + " </td>"); //name
//                LabUsers.append("<td width=10%> " + rset.getString(6) + "</td>"); //Location
                LabUsers.append("<td width=10%> " + rset.getString(2) + "</td>"); //User ID
                LabUsers.append("<td width=10%> " + rset.getString(3) + "</td>"); //PWD
                LabUsers.append("<td width=10%> " + rset.getString(6) + "</td>"); //Status
                LabUsers.append("<td width=10%><i class=\"fa fa-edit\" data-toggle=\"modal\" data-target=\"#myModal\" onClick=\"editRow(" + rset.getInt(1) + ")\"></i></td>");
                LabUsers.append("</tr>");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("LabUsers", LabUsers.toString());
            Parser.SetField("Location", Location.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "MasterDef/createLabUser.html");

        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Create Lab User ^^ (Occurred At : " + facilityName + ") ** (createUser)", servletContext, Ex, "CreateLabUser", "createUser", conn);
            Services.DumException("CreateLabUser", "createUser", request, Ex, getServletContext());
        }
    }

    private void saveLabUser(HttpServletRequest request, PrintWriter out, Connection conn,
                             ServletContext servletContext, String userId, String database,
                             UtilityHelper helper, int facilityIndex) {
        String facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        PreparedStatement pStmt = null;
        String Query = "";
        Statement stmt = null;
        ResultSet resultSet = null;
        try {
            String firstname = request.getParameter("firstname").trim();
            String lastname = request.getParameter("lastname").trim();
            String email = request.getParameter("email").trim();
            String inputUserID = request.getParameter("userId").trim();
            String pwd = request.getParameter("pwd").trim();
            String isAdminCheck = request.getParameter("isAdmin") == null ? "0" : "1";
            String passwordEnc = FacilityLogin.encrypt(pwd);

            pStmt = conn.prepareStatement(
                    "INSERT INTO oe.sysusers (userid, firstname, lastname, password, " +
                            "companyname, create_date, password_expiry, email, " +
                            "clientid, status, MaxRetryAllowed, PRetry, LoginCount," +
                            "usertype,isAdmin, enabled, oe.sysusers.username) " +
                            "VALUES (?,?,?,?,'Labs',NOW(),'2022-12-31',?,?,0,5,0,1,11,?,'Y',?) ");
            pStmt.setString(1, inputUserID);
            pStmt.setString(2, firstname);
            pStmt.setString(3, lastname);
            pStmt.setString(4, passwordEnc);
            pStmt.setString(5, email);
            pStmt.setInt(6, facilityIndex);
            pStmt.setString(7, isAdminCheck);
            pStmt.setString(8, firstname + " " + lastname);
            pStmt.executeUpdate();
            pStmt.close();

            int userIndex = 0;
            Query = "SELECT MAX(indexptr) FROM oe.sysusers ";
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery(Query);
            if (resultSet.next())
                userIndex = resultSet.getInt(1);
            resultSet.close();
            stmt.close();

            for (int i = 0; i < request.getParameterValues("Location").length; i++) {
                pStmt = conn.prepareStatement(
                        "INSERT INTO roverlab.Location_mapping (UserIndex, LocationIndex, Status) " +
                                "VALUES (?,?,0)");
                pStmt.setInt(1, userIndex);
                pStmt.setInt(2, Integer.parseInt(request.getParameterValues("Location")[i]));
                pStmt.executeUpdate();
                pStmt.close();
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("MRN", "Message");
            Parser.SetField("Message", "User Saved!");
            Parser.SetField("FormName", "CreateLabUser");
            Parser.SetField("ActionID", "createUser");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");


        } catch (Exception Ex) {
            out.println("0");
            helper.SendEmailWithAttachment("Error in Create Lab User ^^ (Occurred At : " + facilityName + ") ** (saveLabUser)", servletContext, Ex, "CreateLabUser", "saveLabUser", conn);
            Services.DumException("CreateLabUser", "saveLabUser", request, Ex, getServletContext());
        }

    }

    void editLabUser(HttpServletRequest request, PrintWriter out, Connection conn) {
        try {
            String Query = null;
            Statement stmt = null;
            ResultSet rset = null;
            StringBuffer CDRList = new StringBuffer();
            String idxptr = request.getParameter("str");

            Query = "SELECT firstname,lastname,userid,email,usertype,password,clientid,indexptr FROM oe.sysusers where indexptr = " + idxptr + " and status = 0 ORDER BY create_date DESC";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);


            while (rset.next()) {
                String passdec = FacilityLogin.decrypt(rset.getString(6));
                CDRList.append(rset.getString(1) + "~" + rset.getString(2) + "~" + rset.getString(3) + "~" +
                        "" + rset.getString(4) + "~" + rset.getString(5) + "~" + passdec + "~" + rset.getString(7) + "~" + rset.getString(8));
            }
            rset.close();
            stmt.close();

            out.println(CDRList);
        } catch (Exception var11) {
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void updateLabUser(HttpServletRequest request, PrintWriter out, Connection conn) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = null;
            String firstname = request.getParameter("firstname");
            String lastname = request.getParameter("lastname");
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String pwd = request.getParameter("pwd");
            String Company = "";
            String clientId = "";
            String user_type = request.getParameter("user_type");
            String userId = request.getParameter("userId");
            String idxptr = request.getParameter("idxptr");


            String passwordEnc = FacilityLogin.encrypt(pwd);

//            if (user_type.equals("4")) {
            Company = request.getParameter("Company");
            clientId = request.getParameter("clientId");

            if (Company.equals("")) {
                PreparedStatement ps = conn.prepareStatement("SELECT name FROM oe.clients WHERE id=" + clientId);
                System.out.println("Query : " + ps.toString());
                rset = ps.executeQuery();
                if (rset.next()) {
                    Company = rset.getString(1);
                }
                rset.close();
                ps.close();
            }

            Query = "UPDATE oe.sysusers SET userid='" + userId + "',firstname='" + firstname + "',lastname='" + lastname + "'," +
                    "password='" + passwordEnc + "',username='" + username + "',companyname='" + Company + "',usertype='" + user_type + "'," +
                    "create_date=now(),email='" + email + "',clientid='" + clientId + "' WHERE indexptr='" + idxptr + "'";
            System.out.println("Update Query " + Query);
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);

            stmt.close();
//            } else {
//                Company = request.getParameter("Company");
//                clientId = request.getParameter("clientId");
//                Query = "UPDATE oe.sysusers SET userid='" + userId + "',firstname='" + firstname + "',lastname='" + lastname + "'," +
//                        "password='" + passwordEnc + "',username='" + username + "',companyname='" + Company + "',usertype='" + user_type + "'," +
//                        "create_date=now(),email='" + email + "' WHERE indexptr='" + idxptr + "'";
//
//                stmt = conn.createStatement();
//                stmt.executeUpdate(Query);
//
//                stmt.close();
//            }
            out.println("1");
        } catch (Exception var11) {
            System.out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            System.out.println(str);
            out.flush();
            out.close();
        }
    }


}
