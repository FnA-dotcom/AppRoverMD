package CMS;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import static org.apache.tomcat.jni.Time.now;

@SuppressWarnings("Duplicates")
public class ComplaintRegistration extends HttpServlet {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = null;
    private PreparedStatement pStmt = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String Action;

        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        try {
            HttpSession session = request.getSession(false);
            UtilityHelper helper = new UtilityHelper();

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
//            out.println("+Action: "+Action);
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (Action) {
                case "Dashboard":
//                    out.println("Dashboard"+Action);
                    Dashboard(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "Register_Complaint":
//                    out.println("Register Complaint: "+Action);
                    RegisterComplaint(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "getSubActivities":
//                    out.println("Register Complaint: "+Action);
                    getSubActivities(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "getAssignTeams":
//                    out.println("Register Complaint: "+Action);
                    getAssignTeams(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "getAssignMembers":
//                    out.println("Register Complaint: "+Action);
                    getAssignMembers(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "insertComplaint":
                    out.println("Register Complaint: " + Action);
                    insertComplaint(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "FollowUp":
//                    out.println("FollowUp"+Action);
                    FollowUp(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "Assign":
//                    out.println("Assign"+Action);
                    Assign(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "Closure":
//                    out.println("Closure"+Action);
                    Closure(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "GetDetails":
//                    out.println("Closure"+Action);
                    GetDetails(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "GetDetailsFollowUp":
//                    out.println("Closure"+Action);
                    GetDetailsFollowUp(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "GetDetailsClosure":
//                    out.println("Closure"+Action);
                    GetDetailsClosure(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;

                case "SaveComplaint":
//                    out.println("Closure"+Action);
                    SaveComplaint(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "SaveComplaintClosure":
//                    out.println("Closure"+Action);
                    SaveComplaintClosure(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "TeamMember":
//                    out.println("Closure"+Action);
                    TeamMember(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "AddTeam":
//                    out.println("Closure"+Action);
                    AddTeam(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "AddMember":
//                    out.println("Closure"+Action);
                    AddMember(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "ActivitySubActivity":
//                    out.println("Closure"+Action);
                    ActivitySubActivity(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "AddActivity":
//                    out.println("Closure"+Action);
                    AddActivity(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "AddSubActivity":
//                    out.println("Closure"+Action);
                    AddSubActivity(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
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

    private void Dashboard(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws ServletException {
//        out.println("inside  Dashboard");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        int Resolve = 0;
        int Pending = 0;
        int Responded = 0;
        int Patient_Services = 0;
        int Payments = 0;
        int Transactions = 0;
        int Reports = 0;
        int Documents = 0;
        int[] myNum = new int[4];

        try {
//            out.println("inside  Dashboard try ");
            Query = "SELECT Activity,Sub_Activity,Priority,Brief_Description,Details,`Status`,Date_created,Activity_ID FROM CMS.Complaints ORDER BY Date_created DESC";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//            out.println("inside  Dashboard query executed.... ");
            while (rset.next()) {
                if (rset.getString(1).equals("Patient_Services")) {
                    Patient_Services++;
                } else if (rset.getString(1).equals("Payments")) {
                    Payments++;
                } else if (rset.getString(1).equals("Transactions")) {
                    Transactions++;
                } else if (rset.getString(1).equals("Reports")) {
                    Reports++;
                } else if (rset.getString(1).equals("Documents")) {
                    Documents++;
                }

                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                if (rset.getString(6).equals("Hold/Unassigned")) {
                    CDRList.append("<td align=left><span class=\"badge badge-warning\"> New </span></td>\n");
                    Responded++;
                } else if (rset.getString(6).equals("Resolve")) {
                    CDRList.append("<td align=left><span class=\"badge badge-success\"> Completed </span></td>\n");
                    Resolve++;
                } else {
                    CDRList.append("<td align=left><span class=\"badge badge-danger\"> Pending</span></td>\n");
                    Pending++;
                }
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                SNo++;
            }


            try {
                Query = "SELECT Assign_to_Member,COUNT(*) FROM CMS.ComplaintsAssignment GROUP BY Assign_to_Member ORDER BY Assign_to_Member DESC LIMIT 4";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                int i = 0;
                while (rset.next()) {
                    myNum[i] = rset.getInt(2);
                    i++;
                }

            } catch (Exception e) {
            }
//            out.println("inside  Dashboard Data Fetched.... ");

            rset.close();
            stmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("SNo", String.valueOf(SNo - 1));
            Parser.SetField("Responded", String.valueOf(Responded));
            Parser.SetField("Resolve", String.valueOf(Resolve));
            Parser.SetField("Pending", String.valueOf(Pending));
            Parser.SetField("Patient_Services", String.valueOf(Patient_Services));
            Parser.SetField("Payments", String.valueOf(Payments));
            Parser.SetField("Transactions", String.valueOf(Transactions));
            Parser.SetField("Reports", String.valueOf(Reports));
            Parser.SetField("Documents", String.valueOf(Documents));

            Parser.SetField("ZA", String.valueOf(myNum[0]));
            Parser.SetField("TH", String.valueOf(myNum[1]));
            Parser.SetField("M2", String.valueOf(myNum[2]));
            Parser.SetField("AA", String.valueOf(myNum[3]));


//            out.println("inside  Dashboard Generating Html.... ");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "CMS/Complaint/Dashboard.html");
//            out.println("inside  Dashboard Generated Html.... ");

        } catch (Exception e) {
            e.getStackTrace();
        }


    }

    private void RegisterComplaint(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws IOException {
        DateFormat df = new SimpleDateFormat("yyyyddMM");
        Date currentDate = new Date();

        String Activity_ID = null;
        String split = null;
        int num = 0;
        try {
            Query = "Select Activity_ID from CMS.Complaints ORDER BY Date_created DESC LIMIT 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Activity_ID = rset.getString(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            e.getStackTrace();
        }

        if (df.format(currentDate).equals(Activity_ID.substring(4, 12))) {
            split = Activity_ID.substring(13);
            num = Integer.parseInt(split) + 1;
            Activity_ID = "ACT#" + df.format(currentDate) + "_" + String.format("%03d", num);
        } else {
            Activity_ID = "ACT#" + df.format(currentDate) + "_001";
        }

        StringBuffer activities = new StringBuffer();
        try {
//            out.println("Before Query: "+Query);
            Query = "SELECT DISTINCT(Activities) FROM CMS.`ComplaintsRegistrationActivities`";
//            out.println("Query: "+Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//        out.println("query executed");
            activities.append("<option value=\"\" selected disabled>Select Activity Type</option>\n");
            while (rset.next()) {
//                out.println("inside while loop");
                activities.append("<option value=\"" + rset.getString(1).trim() + "\">" + rset.getString(1).trim() + "</option>\n");
//                out.println("outside while loop");
            }
//            out.println("outside while loop");
//        out.println(
//                request.getParameter("operation")
//        );
//            out.println("Query: "+Query);
        } catch (Exception e) {
            e.getStackTrace();
        }


        Parsehtm Parser = new Parsehtm(request);
        Parser.SetField("Activity_ID", Activity_ID);
        Parser.SetField("Activities", String.valueOf(activities));
        Parser.SetField("data", "Password is Incorrect or you are not authorized!!");
        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "CMS/Complaint/RegisterComplaint.html");
    }

    private void getSubActivities(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) {
//        out.println("getSubActivities: ");
        StringBuffer Sub_activities = new StringBuffer();
        try {
//            out.println("Before Query: "+Query);
            Query = "Select IFNULL(Sub_Activities,'No SubActivity Found') from CMS.ComplaintsRegistrationActivities where Activities = '" + request.getParameter("operation") + "'";
//            out.println("Query: "+Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//        out.println("query executed");
            Sub_activities.append("<select class=\"form-control\" id=\"Sub_Activity_Type\" name=\"Sub_Activity_Type\" required>");
            Sub_activities.append("<option value=\"\" selected disabled>Select Sub Activity Type</option>\n");
            while (rset.next()) {
//                out.println("inside while loop");
                Sub_activities.append("<option value=\"" + rset.getString(1).trim() + "\">" + rset.getString(1).trim() + "</option>\n");
//                out.println("outside while loop");
            }
//            out.println("outside while loop");
            Sub_activities.append("</select>");
//        out.println(
//                request.getParameter("operation")
//        );
//            out.println("Query: "+Query);
            out.println(Sub_activities);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void getAssignTeams(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) {
//        out.println("getSubActivities: ");
        StringBuffer Sub_activities = new StringBuffer();
        try {
//            out.println("Before Query: "+Query);
            Query = "Select DISTINCT(Team) from CMS.ComplaintsRegistrationTeams";
//            out.println("Query: "+Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//        out.println("query executed");
            Sub_activities.append("<label for=\"example-search-input\" class=\"col-form-label\">Assign to Team <span class=\"text-danger\">*</span></label>");
            Sub_activities.append("<select class=\"form-control\" id=\"AssignTo\" name=\"AssignTo\" onchange=\"dynamicSelectMembers(this.value)\" required>");
            Sub_activities.append("<option value=\"\" selected disabled>Select Team</option>\n");
            while (rset.next()) {
//                out.println("inside while loop");
                Sub_activities.append("<option value=\"" + rset.getString(1).trim() + "\">" + rset.getString(1).trim() + "</option>\n");
//                out.println("outside while loop");
            }
//            out.println("outside while loop");
            Sub_activities.append("</select>");
//        out.println(
//                request.getParameter("operation")
//        );
//            out.println("Query: "+Query);
            out.println(Sub_activities);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void getAssignMembers(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) {
//        out.println("getSubActivities: ");
        StringBuffer Sub_activities = new StringBuffer();
        try {
//            out.println("Before Query: "+Query);
            Query = "Select Members from CMS.ComplaintsRegistrationTeams WHERE Team='" + request.getParameter("operation") + "'";
//            out.println("Query: "+Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//        out.println("query executed");
            Sub_activities.append("<label for=\"example-search-input\" class=\"col-form-label\">Assign to Member</label>");
            Sub_activities.append("<select class=\"form-control\" id=\"AssignToMember\" name=\"AssignToMember\" >");
            Sub_activities.append("<option value=\"\" selected disabled>Select Member</option>\n");
            while (rset.next()) {
//                out.println("inside while loop");
                Sub_activities.append("<option value=\"" + rset.getString(1).trim() + "\">" + rset.getString(1).trim() + "</option>\n");
//                out.println("outside while loop");
            }
//            out.println("outside while loop");
            Sub_activities.append("</select>");
//        out.println(
//                request.getParameter("operation")
//        );
//            out.println("Query: "+Query);
            out.println(Sub_activities);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void insertComplaint(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) {
        out.println("insertComplaint inside...");
        String activity = request.getParameter("activity");
        String Activity_ID = request.getParameter("Activity_ID");
        String sub_activity = request.getParameter("sub_activity");
        String priority = request.getParameter("priority");
        String brief_desc = request.getParameter("brief_desc");
        String details = request.getParameter("details");
        String Action = request.getParameter("Action");
        out.println("getting data from frontend");
        try {

            PreparedStatement MainReceipt = conn.prepareStatement(
                    "INSERT INTO CMS.Complaints (Activity,Sub_Activity,Priority,Brief_Description,Details,Status,Date_created,Activity_ID) VALUE(?,?,?,?,?,?,now(),?)");

            MainReceipt.setString(1, activity);
            MainReceipt.setString(2, sub_activity);
            MainReceipt.setString(3, priority);
            MainReceipt.setString(4, brief_desc);
            MainReceipt.setString(5, details);
            MainReceipt.setString(6, Action);

            MainReceipt.setString(7, Activity_ID);

            MainReceipt.executeUpdate();
            MainReceipt.close();

            if (Action.equals("Assign")) {
                String Assign_To = request.getParameter("AssignTo");

                String AssignToMember = request.getParameter("AssignToMember");

                MainReceipt = conn.prepareStatement("INSERT INTO CMS.ComplaintsAssignment (Activity_ID,Assign_TO,Assign_to_Member,Assigned_Date) VALUE(?,?,?,now())");
                MainReceipt.setString(1, Activity_ID);
                MainReceipt.setString(2, Assign_To);
                MainReceipt.setString(3, AssignToMember);

                MainReceipt.executeUpdate();
                MainReceipt.close();
            }
//            helper.SendEmail_complaint("testing","Complaint is Registered","<h1>Complaint is registered in the following Module:</h1> <ul><li><h5>Activity: "+activity+"</h5></li><li><h5> Sub Activity: "+sub_activity+"</h5></li><li><h5> Priority: "+priority+"</h5></li>" +
//                    "<ul>" +
//                    "<h6><font color=\"red\">Please review it at your earliest and do the needful before it get escalated!!</font></h6>");
            out.println("getting data from frontend completed!!!!!!!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            out.println(e.getStackTrace());
        }
    }

    private void FollowUp(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws ServletException {
//        out.println("inside FollowUp");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        String activity_id = null;
        String[] act;
        List<String> list = new ArrayList<>();
        try {
//            out.println("inside  FollowUp try ");
            Query = "SELECT Complaints.Activity, Complaints.Sub_Activity,Complaints.Priority,Complaints.Brief_Description,Complaints.Details,Complaints.`Status`,Complaints.Date_created,Complaints.Activity_ID," +
                    "ComplaintsAssignment.Assign_TO,ComplaintsAssignment.Assigned_Date " +
                    " FROM CMS.Complaints " +
                    "INNER JOIN CMS.ComplaintsAssignment" +
                    " ON Complaints.Activity_ID=ComplaintsAssignment.Activity_ID " +
                    "WHERE `Status` = \"Assign\" ORDER BY Assigned_Date DESC";
//            out.println("inside  FollowUp query executing.... Query: "+Query);

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//            out.println("inside  FollowUp query executed.... ");
            while (rset.next()) {
                if (!(list.contains(rset.getString(8)))) {
                    list.add(rset.getString(8));
                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left><span class=\"badge badge-danger\"> Pending</span></td>\n");
                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    activity_id = rset.getString(8);
                    act = activity_id.split("#");

                    CDRList.append("<td align=left><a class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-5\" href=\"/md/CMS.ComplaintRegistration?ActionID=GetDetailsFollowUp&ID=" + act[0] + "&act=" + act[1] + "\">Detail</button></td>\n");

                    SNo++;
                }
            }
//            out.println("inside  FollowUp Data Fetched.... ");

            rset.close();
            stmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "CMS/Complaint/FollowUpComplaint.html");

        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    private void Assign(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws ServletException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;

        String activity_id = null;
        String[] act;
        try {
//            out.println("inside  Dashboard try ");
            Query = "SELECT Activity,Sub_Activity,Priority,Brief_Description,Details,`Status`,Date_created,Activity_ID FROM CMS.Complaints WHERE `Status` = \"Hold/Unassigned\" ORDER BY Date_created DESC";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//            out.println("inside  Dashboard query executed.... ");
            while (rset.next()) {
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                if (rset.getString(6).equals("Hold/Unassigned")) {
                    CDRList.append("<td align=left><span class=\"badge badge-warning\"> New </span></td>\n");
                } else if (rset.getString(6).equals("Resolve")) {
                    CDRList.append("<td align=left><span class=\"badge badge-success\"> Completed </span></td>\n");
                } else {
                    CDRList.append("<td align=left><span class=\"badge badge-danger\"> Pending</span></td>\n");
                }
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                activity_id = rset.getString(8);
                act = activity_id.split("#");

                CDRList.append("<td align=left><a class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-5\" href=\"/md/CMS.ComplaintRegistration?ActionID=GetDetails&ID=" + act[0] + "&act=" + act[1] + "\">Detail</button></td>\n");

                SNo++;
            }
//            out.println("inside  Dashboard Data Fetched.... ");

            rset.close();
            stmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "CMS/Complaint/AssignComplaint.html");

        } catch (Exception e) {
            e.getStackTrace();
        }


    }

    private void GetDetails(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws ServletException {
        String ID = request.getParameter("ID");
        String act = request.getParameter("act");

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String activity = null;
        String Activity_ID = null;
        String sub_activity = null;
        String priority = null;
        String brief_desc = null;
        String details = null;
        String Action = null;


        try {
            Query = "Select Activity_ID,Activity,Sub_Activity,Priority,Brief_Description,Details,`Status`  from CMS.Complaints WHERE Activity_ID='" + ID + "#" + act + "'";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            while (rset.next()) {

                Activity_ID = rset.getString(1);
                activity = rset.getString(2);
                sub_activity = rset.getString(3);
                priority = rset.getString(4);
                brief_desc = rset.getString(5);
                details = rset.getString(6);
                Action = rset.getString(7);

            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Activity_ID", Activity_ID);
            Parser.SetField("activity", activity);
            Parser.SetField("sub_activity", sub_activity);
            Parser.SetField("priority", priority);
            Parser.SetField("brief_desc", brief_desc);
            Parser.SetField("details", details);
            Parser.SetField("Action", Action);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "CMS/Complaint/ComplaintDetails.html");
        } catch (Exception e) {
            e.getStackTrace();
        }


    }

    private void GetDetailsFollowUp(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws ServletException {
//        out.println("inside GetDetailsFollowUp");
        String ID = request.getParameter("ID");
        String act = request.getParameter("act");

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String activity = null;
        String Activity_ID = null;
        String sub_activity = null;
        String priority = null;
        String brief_desc = null;
        String details = null;
        String Action = null;
        String Assign_TO = null;
        String Remarks = null;
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;

        try {
//            out.println("inside GetDetailsFollowUp try");

            Query = "SELECT Complaints.Activity, Complaints.Sub_Activity,Complaints.Priority,Complaints.Brief_Description,Complaints.Details,Complaints.`Status`,Complaints.Activity_ID," +
                    "ComplaintsAssignment.Assign_TO,IFNULL(ComplaintsAssignment.Remarks,'No Remarks') " +
                    " FROM CMS.Complaints " +
                    "INNER JOIN CMS.ComplaintsAssignment" +
                    " ON Complaints.Activity_ID=ComplaintsAssignment.Activity_ID " +
                    "WHERE Complaints.Activity_ID='" + ID + "#" + act + "'";

//            out.println("inside GetDetailsFollowUp Query executing... \n Query"+Query);

//            Query = "Select Activity_ID,Activity,Sub_Activity,Priority,Brief_Description,Details,`Status`  from CMS.Complaints WHERE Activity_ID='"+ID+"#"+act+"'";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//            out.println("inside GetDetailsFollowUp Query executed...");
            while (rset.next()) {

                Activity_ID = rset.getString(7);
                activity = rset.getString(1);
                sub_activity = rset.getString(2);
                priority = rset.getString(3);
                brief_desc = rset.getString(4);
                details = rset.getString(5);
                Action = rset.getString(6);
                Assign_TO = rset.getString(8);
                Remarks = rset.getString(9);

            }

            try {
                Query = "SELECT Assign_TO,Assigned_Date,IFNULL(Assign_to_Member,'Whole Team'),IFNULL(Remarks,'No Remarks') FROM CMS.ComplaintsAssignment where Activity_ID='" + ID + "#" + act + "' ORDER BY Assigned_Date DESC;";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);

                while (rset.next()) {
                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    SNo++;
                }
            } catch (Exception e) {
                e.getStackTrace();
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("Activity_ID", Activity_ID);
            Parser.SetField("activity", activity);
            Parser.SetField("sub_activity", sub_activity);
            Parser.SetField("priority", priority);
            Parser.SetField("brief_desc", brief_desc);
            Parser.SetField("details", details);
            Parser.SetField("Action", Action);
            Parser.SetField("Assign_TO", Assign_TO);
            Parser.SetField("Remarks", Remarks);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "CMS/Complaint/ComplaintDetailsFollowUp.html");
        } catch (Exception e) {
            e.getStackTrace();
        }


    }


    private void SaveComplaint(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws ServletException {
        Statement stmt = null;
        ResultSet rset = null;
        String Activity_ID = request.getParameter("Activity_ID");
        String Remarks = request.getParameter("Remarks");
        String Assign_To = request.getParameter("AssignTo");

        String activity = request.getParameter("activity");
        String sub_activity = request.getParameter("sub_activity");
        String priority = request.getParameter("priority");
        String brief_desc = request.getParameter("brief_desc");
        String details = request.getParameter("details");
        String Action = request.getParameter("Action");

        String AssignToMember = request.getParameter("AssignToMember");
        out.println("inside Save Complaints");
        try {
            out.println("inside Save Complaints try");

            PreparedStatement MainReceipt = conn.prepareStatement(
                    "INSERT INTO CMS.ComplaintsAssignment (Activity_ID,Assign_TO,Assign_to_Member,Remarks,Assigned_Date) VALUE(?,?,?,?,now())");
            out.println("inside Save Complaints Executing Query:" + MainReceipt);

            MainReceipt.setString(1, Activity_ID);
            MainReceipt.setString(2, Assign_To);
            MainReceipt.setString(3, AssignToMember);
            MainReceipt.setString(4, Remarks);
            out.println("inside Save Complaints try Query executing Query :" + MainReceipt);

            MainReceipt.executeUpdate();
            out.println("inside Save Complaints try Query executing complete");

            MainReceipt.close();


            Query = "UPDATE CMS.Complaints SET Status = '" + Action + "' WHERE Activity_ID = '" + Activity_ID + "'";
//            out.println("inside Save Complaints try update Query executing Query :"+Query);
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
//            out.println("inside Save Complaints try Query executing complete");

            stmt.close();


//
//            helper.SendEmail_complaint("testing","Complaint is Assigned","<h1>Complaint is assigned:</h1> <ul><li><h5>Activity ID: "+Activity_ID+"</h5></li><li><h5>  Activity: "+activity+"</h5></li>" +
//                    "<li><h5>  Sub Activity: "+sub_activity+"</h5></li>" +"<li><h5>  Priority: "+priority+"</h5></li>"+ "<li><h5>  Assigned To: "+Assign_To+"</h5></li>"+"<li><h5>  Remarks: "+Remarks+"</h5></li>"+
//                    "<ul>" +
//                    "<h6><font color=\"red\">Please review it at your earliest and do the needful before it get escalated!!</font></h6>");
//            out.println("inside Save Complaints Email Sent  ");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Your Assignment has been successfully saved");
            Parser.SetField("FormName", "ComplaintRegistration");
            Parser.SetField("ActionID", "Dashboard");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void Closure(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws ServletException {
//        out.println("inside Closure");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        String activity_id = null;
        String[] act;
        List<String> list = new ArrayList<>();
        try {
//            out.println("inside  Closure try ");
            Query = "SELECT Complaints.Activity, Complaints.Sub_Activity,Complaints.Priority,Complaints.Brief_Description,Complaints.Details,Complaints.`Status`,Complaints.Date_created,Complaints.Activity_ID,\n" +
                    "ComplaintsAssignment.Assign_TO,ComplaintsAssignment.Assigned_Date\n" +
                    "FROM CMS.Complaints\n" +
                    "Left JOIN CMS.ComplaintsAssignment\n" +
                    " ON Complaints.Activity_ID=ComplaintsAssignment.Activity_ID\n" +
                    " WHERE (`Status` = 'Hold/Unassigned')OR(`Status` = 'Assign')  \n" +
                    "ORDER BY COALESCE(Assigned_Date,Date_created) DESC";
//            out.println("inside  Closure query executing.... Query: "+Query);

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//            out.println("inside  Closure query executed.... ");
            while (rset.next()) {
                if (!(list.contains(rset.getString(8)))) {
                    list.add(rset.getString(8));
                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    if (rset.getString(6).equals("Hold/Unassigned")) {
                        CDRList.append("<td align=left><span class=\"badge badge-warning\"> New </span></td>\n");
                    } else {
                        CDRList.append("<td align=left><span class=\"badge badge-danger\"> Pending</span></td>\n");
                    }

                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    if (rset.getString(6).equals("Hold/Unassigned")) {
                        CDRList.append("<td align=left> Not Assigned Yet </td>\n");
                        CDRList.append("<td align=left> N/A </td>\n");
                    } else {
                        CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    }


                    activity_id = rset.getString(8);
                    act = activity_id.split("#");

                    CDRList.append("<td align=left><a class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-5\" href=\"/md/CMS.ComplaintRegistration?ActionID=GetDetailsClosure&ID=" + act[0] + "&act=" + act[1] + "\">Detail</button></td>\n");

                    SNo++;
                }
            }
//            out.println("inside  Closure Data Fetched.... ");

            rset.close();
            stmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "CMS/Complaint/ClosureComplaint.html");

        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    private void GetDetailsClosure(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws ServletException {
//        out.println("inside GetDetailsFollowUp");
        String ID = request.getParameter("ID");
        String act = request.getParameter("act");

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String activity = null;
        String Activity_ID = null;
        String sub_activity = null;
        String priority = null;
        String brief_desc = null;
        String details = null;
        String Action = null;
        String Assign_TO = null;
        String Remarks = null;
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;

        try {
//            out.println("inside GetDetailsFollowUp try");

            Query = "SELECT Complaints.Activity, Complaints.Sub_Activity,Complaints.Priority,Complaints.Brief_Description,Complaints.Details,Complaints.`Status`,Complaints.Activity_ID," +
                    "IFNULL(ComplaintsAssignment.Assign_TO,'Not Assigned Yet'),IFNULL(ComplaintsAssignment.Remarks,'No Remarks') " +
                    " FROM CMS.Complaints " +
                    "LEFT JOIN CMS.ComplaintsAssignment" +
                    " ON Complaints.Activity_ID=ComplaintsAssignment.Activity_ID " +
                    "WHERE Complaints.Activity_ID='" + ID + "#" + act + "'";

//            out.println("inside GetDetailsFollowUp Query executing... \n Query"+Query);

//            Query = "Select Activity_ID,Activity,Sub_Activity,Priority,Brief_Description,Details,`Status`  from CMS.Complaints WHERE Activity_ID='"+ID+"#"+act+"'";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//            out.println("inside GetDetailsFollowUp Query executed...");
            while (rset.next()) {

                Activity_ID = rset.getString(7);
                activity = rset.getString(1);
                sub_activity = rset.getString(2);
                priority = rset.getString(3);
                brief_desc = rset.getString(4);
                details = rset.getString(5);
                Action = rset.getString(6);
                Assign_TO = rset.getString(8);
//                out.println("Assign_TO: "+Assign_TO);
                Remarks = rset.getString(9);
//                out.println("Remarks: "+Remarks);


            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Activity_ID", Activity_ID);
            Parser.SetField("activity", activity);
            Parser.SetField("sub_activity", sub_activity);
            Parser.SetField("priority", priority);
            Parser.SetField("brief_desc", brief_desc);
            Parser.SetField("details", details);
            Parser.SetField("Action", Action);
            Parser.SetField("Assign_TO", Assign_TO);
            Parser.SetField("Remarks", Remarks);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "CMS/Complaint/ComplaintDetailsClosure.html");
        } catch (Exception e) {
            e.getStackTrace();
        }


    }

    private void SaveComplaintClosure(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws ServletException {
        Statement stmt = null;
        ResultSet rset = null;
        String Activity_ID = request.getParameter("Activity_ID");
        String Remarks = request.getParameter("Remarks");
        String Assign_To = request.getParameter("AssignTo");

        String activity = request.getParameter("activity");
        String sub_activity = request.getParameter("sub_activity");
        String priority = request.getParameter("priority");
        String brief_desc = request.getParameter("brief_desc");
        String details = request.getParameter("details");
        String Action = request.getParameter("Action");
        String AssignToMember = request.getParameter("AssignToMember");
//        out.println("inside Save Complaints");
        try {
//            out.println("inside Save Complaints try");
            if (Action.equals("Assign")) {
                String re_Assign = request.getParameter("re_Assign");
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO CMS.ComplaintsAssignment (Activity_ID,Assign_TO,Assign_to_Member,Remarks,Assigned_Date) VALUE(?,?,?,?,now())");
//            out.println("inside Save Complaints Executing Query:" + MainReceipt);

                MainReceipt.setString(1, Activity_ID);
                MainReceipt.setString(2, Assign_To);
                MainReceipt.setString(3, AssignToMember);
                MainReceipt.setString(4, Remarks);
//            out.println("inside Save Complaints try Query executing Query :"+ MainReceipt);

                MainReceipt.executeUpdate();
//            out.println("inside Save Complaints try Query executing complete");

                MainReceipt.close();


                Query = "UPDATE CMS.Complaints SET Status = '" + Action + "' WHERE Activity_ID = '" + Activity_ID + "'";
//            out.println("inside Save Complaints try update Query executing Query :"+Query);
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
//            out.println("inside Save Complaints try Query executing complete");

                stmt.close();
//                helper.SendEmail_complaint("testing","Complaint is Re-Assigned","<h1>Complaint is re-assigned:</h1> <ul><li><h5>Activity ID: "+Activity_ID+"</h5></li><li><h5>  Activity: "+activity+"</h5></li>" +
//                        "<li><h5>  Sub Activity: "+sub_activity+"</h5></li>" +"<li><h5>  Priority: "+priority+"</h5></li>"+ "<li><h5>  Assigned To: "+Assign_To+"</h5></li>"+"<li><h5>  Remarks: "+Remarks+"</h5></li>"+
//                        "<ul>" +
//                        "<h6><font color=\"red\">Please review it at your earliest and do the needful before it get escalated!!</font></h6>");
            } else {
                Query = "UPDATE CMS.Complaints SET Status = '" + Action + "' WHERE Activity_ID = '" + Activity_ID + "'";
//            out.println("inside Save Complaints try update Query executing Query :"+Query);
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
//            out.println("inside Save Complaints try Query executing complete");

                stmt.close();
//                helper.SendEmail_complaint("testing","Complaint is Closed","<h1>Complaint is closed:</h1> <ul><li><h5>Activity ID: "+Activity_ID+"</h5></li><li><h5>  Activity: "+activity+"</h5></li>" +
//                        "<li><h5>  Sub Activity: "+sub_activity+"</h5></li>" +"<li><h5>  Priority: "+priority+"</h5></li>"+ "<li><h5>  Assigned To: "+Assign_To+"</h5></li>"+"<li><h5>  Remarks: "+Remarks+"</h5></li>"+
//                        "<ul>" +
//                        "<h6><font color=\"red\">Please review it at your earliest and do the needful before it get escalated!!</font></h6>");
            }


//            out.println("inside Save Complaints Email Sent  ");
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("Message", "Your Assignment has been successfully saved");
//            Parser.SetField("FormName", "ComplaintRegistration");
//            Parser.SetField("ActionID", "Dashboard");
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void TeamMember(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws ServletException {
        Statement stmt = null;
        ResultSet rset = null;
        ResultSet rset1 = null;
        Statement stmt1 = null;
        String Query = "";
        StringBuffer CDRList = new StringBuffer();

        try {
            Query = "SELECT DISTINCT(Team) FROM CMS.`ComplaintsRegistrationTeams`";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr><td><div class=\"panel-group\" style=\"\n" +
                        "    padding-top: 21px;\n" +
                        "\">\n" +
                        "  <div class=\"panel panel-default\">\n" +
                        "    <div class=\"panel-heading\">\n" +
                        "      <h4 class=\"panel-title\">\n" +
                        "        <a data-toggle=\"collapse\" href=\"#" + rset.getString(1) + "\">" + rset.getString(1) + "</a>" +
                        "</h4></div>" +
                        "<div id=\"" + rset.getString(1) + "\" class=\"panel-collapse collapse\">" +
                        "<ul class=\"list-group\">");
//                CDRList.append(rset.getString(1));
                Query = "SELECT IFNULL(Members,'Have No Member') FROM CMS.`ComplaintsRegistrationTeams` WHERE Team='" + rset.getString(1) + "'";
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query);
                while (rset1.next()) {
                    CDRList.append("<li class=\"list-group-item\">" + rset1.getString(1) + "</li>");
                }
                CDRList.append("</ul>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t    </div>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t  </div>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t</td>" +
                        "<td><input type=\"button\" class=\"btn btn-outline-primary\" data-toggle=\"modal\" name=\'" + rset.getString(1) + "\' data-target=\"#myModal1\" title=\"Add Member\" id=\"AddMember\" name=\"AddMem\" Value=\"+ Add Member\"></td>\n" +
                        "\t\t\t\t\t\t\t\t\t\t</tr>");

            }
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "CMS/Complaint/AddTeamMember.html");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void AddTeam(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws IOException {
        String Add_Team = request.getParameter("Add_Team");

        try {
            PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO CMS.`ComplaintsRegistrationTeams` (Team) VALUE(?)");
            MainReceipt.setString(1, Add_Team);
            MainReceipt.executeUpdate();
            MainReceipt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "CMS/Complaint/AddTeamMember.html");
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private void AddMember(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws IOException {
        String Add_Team = request.getParameter("Add_Team");
        String Add_Member = request.getParameter("Add_Member");
        int null_data_found = 0;
        try {

            Query = "SELECT Count(*) from CMS.ComplaintsRegistrationTeams where Team = \'" + Add_Team + "\' and Members Is NULL";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            while (rset.next()) {
                null_data_found = rset.getInt(1);
            }

            stmt.close();

            if (null_data_found > 0) {
                PreparedStatement MainReceipt = conn.prepareStatement("UPDATE CMS.ComplaintsRegistrationTeams SET Members = \'" + Add_Member + "\'  WHERE Team = \'" + Add_Team + "\' and Members is NULL");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } else {
                PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO CMS.`ComplaintsRegistrationTeams` (Team,Members) VALUE(?,?)");
                MainReceipt.setString(1, Add_Team);
                MainReceipt.setString(2, Add_Member);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "CMS/Complaint/AddTeamMember.html");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    private void ActivitySubActivity(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws ServletException {
        Statement stmt = null;
        ResultSet rset = null;
        ResultSet rset1 = null;
        Statement stmt1 = null;
        String Query = "";
        StringBuffer CDRList = new StringBuffer();

        try {
            Query = "SELECT DISTINCT(Activities) FROM CMS.`ComplaintsRegistrationActivities`";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr><td><div class=\"panel-group\" style=\"\n" +
                        "    padding-top: 21px;\n" +
                        "\">\n" +
                        "  <div class=\"panel panel-default\">\n" +
                        "    <div class=\"panel-heading\">\n" +
                        "      <h4 class=\"panel-title\">\n" +
                        "        <a data-toggle=\"collapse\" href=\"#" + rset.getString(1) + "\">" + rset.getString(1) + "</a>" +
                        "</h4></div>" +
                        "<div id=\"" + rset.getString(1) + "\" class=\"panel-collapse collapse\">" +
                        "<ul class=\"list-group\">");
//                CDRList.append(rset.getString(1));
                Query = "SELECT IFNULL(Sub_Activities,'Have No Sub Activity') FROM CMS.`ComplaintsRegistrationActivities` WHERE Activities='" + rset.getString(1) + "'";
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query);
                while (rset1.next()) {
                    CDRList.append("<li class=\"list-group-item\">" + rset1.getString(1) + "</li>");
                }
                CDRList.append("</ul>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t    </div>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t  </div>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t</td>" +
                        "<td><input type=\"button\" class=\"btn btn-outline-primary\" data-toggle=\"modal\" name=\'" + rset.getString(1) + "\' data-target=\"#myModal1\" title=\"Add SubActivity\" id=\"AddSubActivity\" name=\"AddSubActivity\" Value=\"+ Add SubActivity\"></td>\n" +
                        "\t\t\t\t\t\t\t\t\t\t</tr>");

            }
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "CMS/Complaint/AddActivitySubActivity.html");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void AddActivity(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws IOException {
        String Add_Activity = request.getParameter("Add_Activity");

        try {
            PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO CMS.`ComplaintsRegistrationActivities` (Activities) VALUE(?)");
            MainReceipt.setString(1, Add_Activity);
            MainReceipt.executeUpdate();
            MainReceipt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "CMS/Complaint/AddActivitySubActivity.html");
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private void AddSubActivity(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) throws IOException {
        String Add_Activity = request.getParameter("Add_Activity");
        String Add_SubActivity = request.getParameter("Add_SubActivity");
        int null_data_found = 0;
        try {

            Query = "SELECT Count(*) from CMS.ComplaintsRegistrationActivities where Activities = \'" + Add_Activity + "\' and Sub_Activities Is NULL";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            while (rset.next()) {
                null_data_found = rset.getInt(1);
            }

            stmt.close();

            if (null_data_found > 0) {
                PreparedStatement MainReceipt = conn.prepareStatement("UPDATE CMS.ComplaintsRegistrationActivities SET Sub_Activities = \'" + Add_SubActivity + "\'  WHERE Activities = \'" + Add_Activity + "\' and Sub_Activities is NULL");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } else {
                PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO CMS.`ComplaintsRegistrationActivities` (Activities,Sub_Activities) VALUE(?,?)");
                MainReceipt.setString(1, Add_Activity);
                MainReceipt.setString(2, Add_SubActivity);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "CMS/Complaint/AddActivitySubActivity.html");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }
}
