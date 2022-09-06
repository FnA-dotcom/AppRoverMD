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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@SuppressWarnings("Duplicates")
public class LabManifestsRetrieve extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ActionID = "";
        String UserId = "";
        String DatabaseName = "";
        String DirectoryName = "";
        HttpSession session = null;
        boolean validSession = false;
        int FacilityIndex = 0;
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
        Connection conn = null;
        String locationArray = "";
        try {
            Parsehtm Parser;
            session = request.getSession(false);
            validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            locationArray = session.getAttribute("LocationArray").toString();
            DirectoryName = session.getAttribute("DirectoryName").toString();
            if (UserId.equals("")) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("Error", "Unable to connect. Our team is looking into it!");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }

            switch (ActionID) {
                case "GetInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "RoverLab Admission Bundle", "Download or View Admission Bundle", FacilityIndex);
                    GetInput(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, helper, locationArray);
                    break;
                case "GetInput_view":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "RoverLab Admission Bundle", "Download or View Admission Bundle", FacilityIndex);
                    GetInput_view(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, helper, locationArray);
                    break;
                case "SaveRetrieveManifest":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Saving Retrieved Manifests of Lab", "Saving..Retrieved.. Manifest", FacilityIndex);
                    SaveRetrieveManifest(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, DirectoryName);
                    break;

                default: {
                    helper.deleteUserSession(request, conn, session.getId());
                    session.invalidate();
                    Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
                }
            }
        } catch (Exception e) {
            if (conn == null) {
                conn = Services.getMysqlConn(context);
            }
            helper.SendEmailWithAttachment("Error in LabManifestsRetrieve ** (handleRequest)", context, e, "LabManifestsRetrieve", "handleRequest", conn);
            Services.DumException("LabManifestsRetrieve", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                helper.SendEmailWithAttachment("Error in LabManifestsRetrieve ** (handleRequest -- SqlException)", context, e, "LabManifestsRetrieve", "handleRequest", conn);
                Services.DumException("LabManifestsRetrieve", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }

    private void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId, UtilityHelper helper, String locationArray) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;
        StringBuilder ManifestRetrieve = new StringBuilder();
        String manifestNum = "";
        try {

            HashMap<String, String> loc_liscode = new HashMap<String, String>();
            loc_liscode = listoflocation(conn, Database);
            ArrayList<String> al = new ArrayList<>();
            al.add(locationArray);
            String list = Arrays.toString(al.toArray()).replace("[", "").replace("]", "");

            String locCondition = "";
            if (locationArray.length() > 1) {
                locCondition = " AND f.Id IN (" + list + ") ";
            }

/*            Query = " SELECT CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " + //1
                    "DATE_FORMAT(a.DOB,'%m/%d/%Y'),  IFNULL(a.PhNumber,''), IFNULL(a.MRN,0), a.ID AS PatRegId, " +//5
                    "DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'),  " + //6
                    "CASE " +
                    "WHEN a.Insured = 'Yes' THEN 'Insured' " +
                    "WHEN a.Insured = 'NO' THEN 'Self Pay' " +
                    "ELSE 'Self Pay' " +
                    "END, b.OrderNum,b.OrderDate," + //9
                    "CASE " +
                    " WHEN a.Status = 0 THEN c.Status " +
                    " WHEN a.Status = 1 THEN c.Status" +
                    " WHEN a.Status = 2 THEN c.Status" +
                    " WHEN a.Status = 3 THEN c.Status " +
                    " ELSE 'Pending' END,d.Location " + //10
                    " FROM roverlab.PatientReg a" +
                    " INNER JOIN roverlab.TestOrder b ON a.ID = b.PatRegIdx " +
                    " INNER JOIN roverlab.ListofStages c ON b.StageIdx = c.Id " +
                    " INNER JOIN roverlab.Locations d ON a.TestingLocation = d.Id " +
                    " WHERE a.Status = 0 AND b.StageIdx = 1" +
                    " ORDER BY a.CreatedDate DESC limit 500";*/
            Query = "SELECT a.ManifestNum, c.OrderNum, " +
                    "CASE " +
                    " WHEN a.Status = 0 THEN d.Status " +
                    " WHEN a.Status = 1 THEN d.Status" +
                    " WHEN a.Status = 2 THEN d.Status" +
                    " WHEN a.Status = 3 THEN d.Status " +
                    " ELSE 'Pending' END,a.Id  " + //4
                    " FROM " + Database + ".ManifestsMaster a " +
                    " INNER JOIN " + Database + ".ManifestDetails b ON a.Id = b.ManifestIdx " +
                    " INNER JOIN " + Database + ".TestOrder c ON b.OrderId = c.Id " +
                    " INNER JOIN " + Database + ".ListofStages d ON c.StageIdx=d.Id  " +
                    " INNER JOIN " + Database + ".PatientReg e ON b.PatRegIdx = e.Id " +
                    " INNER JOIN " + Database + ".Locations f ON e.TestingLocation = f.Id " +
                    " WHERE a.isRetrieved = 1 " + locCondition + " ORDER BY a.CreatedDate ";

            Query = "select ManifestNum,locationIdx,CreatedDate,id,Createdby," +
                    "HOUR(TIMEDIFF(CreatedDate,now())) from "
                    + " " + Database + ".ManifestsMaster where isRetrieved=1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ManifestRetrieve.append("<tr>\n");
                // if (!manifestNum.equals(rset.getString(1))) {
                ManifestRetrieve.append("<td align=left><input  class=\"checkSingle\" type=\"checkbox\" id=\"mList_" + rset.getInt(4) + "\" > \n");
                ManifestRetrieve.append("<label for=\"mList_" + rset.getInt(4) + "\">" + rset.getString(1) + "</label>\n");//Manifest
                ManifestRetrieve.append("<a class=\"btn fa fa-file-pdf-o pdfIcon mb-2 tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Manifest\" href=/md/md.LabManifests?ActionID=manifestPDF&manifestIdx=" + rset.getInt(4) + " target=\"_blank\" rel=\"noopener noreferrer\"></a></td> \n");

                ManifestRetrieve.append("<td align=left>" + getsamplecount(conn, Database, rset.getString(4)) + "</td>\n");//OrderId
                ManifestRetrieve.append("<td align=left>" + loc_liscode.get(rset.getString(2)) + "</td>\n");//Status
                // } else {
                // ManifestRetrieve.append("<td align=left> </td>");
                ManifestRetrieve.append("<td align=left>" + rset.getString(3) + "</td>\n");//OrderId
                ManifestRetrieve.append("<td align=left>" + rset.getString(5) + "</td>\n");//Status
                ManifestRetrieve.append("<td align=left>" + rset.getString(6) + "Hrs</td>\n");//Status

                //}
                ManifestRetrieve.append("</tr>\n");
                manifestNum = rset.getString(1).trim();
            }
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ManifestRetrieve", ManifestRetrieve.toString());
            Parser.SetField("UserId", UserId);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/ManifestRetrieveInput_2.html");

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in LabManifestsRetrieve ** (GetInput )", servletContext, e, "LabManifestsRetrieve", "GetInput", conn);
            Services.DumException("LabManifestsRetrieve", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "LabManifestsRetrieve");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

        }
    }


    private void GetInput_view(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId, UtilityHelper helper, String locationArray) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;
        StringBuilder ManifestRetrieve = new StringBuilder();
        String manifestNum = "";
        StringBuilder locationList = new StringBuilder();
        String Defaultlocation = "";
        try {

            HashMap<String, String> loc_liscode = new HashMap<String, String>();
            loc_liscode = listoflocation(conn, Database);
            ArrayList<String> al = new ArrayList<>();
            al.add(locationArray);
            String list = Arrays.toString(al.toArray()).replace("[", "").replace("]", "");

            String locCondition = "";
            if (locationArray.length() > 1) {
                locCondition = " AND f.Id IN (" + list + ") ";
            }

            String Filter = "";


            String _default = "selected";
            Query = "Select Id, Location from roverlab.Locations WHERE Id IN (" + list + ") ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            // locationList.append("<option value='-1' selected disabled>Select Location</option>");
            while (rset.next()) {

                locationList.append("<option value=" + rset.getString(1) + " " + _default + " >" + rset.getString(2) + "</option>");
                if (_default.compareTo("selected") == 0) {
                    //locCondition = " AND d.Id IN (" +  rset.getString(1) + ") ";
                    Defaultlocation = " where locationIdx=" + rset.getString(1);
                    Filter = rset.getString(1);
                }
                _default = "";
            }
            rset.close();
            stmt.close();

            if (request.getParameter("Loc_id") != null) {

                Defaultlocation = " where locationIdx=" + request.getParameter("Loc_id");
                Filter = request.getParameter("Loc_id");
            }

/*            Query = " SELECT CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " + //1
                    "DATE_FORMAT(a.DOB,'%m/%d/%Y'),  IFNULL(a.PhNumber,''), IFNULL(a.MRN,0), a.ID AS PatRegId, " +//5
                    "DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'),  " + //6
                    "CASE " +
                    "WHEN a.Insured = 'Yes' THEN 'Insured' " +
                    "WHEN a.Insured = 'NO' THEN 'Self Pay' " +
                    "ELSE 'Self Pay' " +
                    "END, b.OrderNum,b.OrderDate," + //9
                    "CASE " +
                    " WHEN a.Status = 0 THEN c.Status " +
                    " WHEN a.Status = 1 THEN c.Status" +
                    " WHEN a.Status = 2 THEN c.Status" +
                    " WHEN a.Status = 3 THEN c.Status " +
                    " ELSE 'Pending' END,d.Location " + //10
                    " FROM roverlab.PatientReg a" +
                    " INNER JOIN roverlab.TestOrder b ON a.ID = b.PatRegIdx " +
                    " INNER JOIN roverlab.ListofStages c ON b.StageIdx = c.Id " +
                    " INNER JOIN roverlab.Locations d ON a.TestingLocation = d.Id " +
                    " WHERE a.Status = 0 AND b.StageIdx = 1" +
                    " ORDER BY a.CreatedDate DESC limit 500";*/
            Query = "SELECT a.ManifestNum, c.OrderNum, " +
                    "CASE " +
                    " WHEN a.Status = 0 THEN d.Status " +
                    " WHEN a.Status = 1 THEN d.Status" +
                    " WHEN a.Status = 2 THEN d.Status" +
                    " WHEN a.Status = 3 THEN d.Status " +
                    " ELSE 'Pending' END,a.Id  " + //4
                    " FROM " + Database + ".ManifestsMaster a " +
                    " INNER JOIN " + Database + ".ManifestDetails b ON a.Id = b.ManifestIdx " +
                    " INNER JOIN " + Database + ".TestOrder c ON b.OrderId = c.Id " +
                    " INNER JOIN " + Database + ".ListofStages d ON c.StageIdx=d.Id  " +
                    " INNER JOIN " + Database + ".PatientReg e ON b.PatRegIdx = e.Id " +
                    " INNER JOIN " + Database + ".Locations f ON e.TestingLocation = f.Id " +
                    " WHERE a.isRetrieved = 1 " + Defaultlocation + " ORDER BY a.CreatedDate ";

            Query = "select ManifestNum,locationIdx,CreatedDate,id,Createdby,HOUR(TIMEDIFF(CreatedDate,now())) from "
                    + " " + Database + ".ManifestsMaster where isRetrieved=1";
            Query = "select ManifestNum,locationIdx,CreatedDate,id,Createdby,HOUR(TIMEDIFF(CreatedDate,now())),ifnull(RetrievedDate,'-'),\r\n" +
                    "ifnull(RetrievedBy,'-'),isRetrieved,ifnull(HOUR(TIMEDIFF(CreatedDate,RetrievedDate)),'-')" +
                    " from " + Database + ".ManifestsMaster  " + Defaultlocation;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ManifestRetrieve.append("<tr>\n");
                // if (!manifestNum.equals(rset.getString(1))) {
                ManifestRetrieve.append("<td align=left> \n");
                ManifestRetrieve.append("<label for=\"mList_" + rset.getInt(4) + "\">" + rset.getString(1) + "</label>\n");//Manifest
                ManifestRetrieve.append("<a class=\"btn fa fa-file-pdf-o pdfIcon mb-2 tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Manifest\" href=/md/md.LabManifests?ActionID=manifestPDF&manifestIdx=" + rset.getInt(4) + " target=\"_blank\" rel=\"noopener noreferrer\"></a></td> \n");

                ManifestRetrieve.append("<td align=left>" + getsamplecount(conn, Database, rset.getString(4)) + "</td>\n");//OrderId
                ManifestRetrieve.append("<td align=left>" + loc_liscode.get(rset.getString(2)) + "</td>\n");//Status
                // } else {
                // ManifestRetrieve.append("<td align=left> </td>");
                ManifestRetrieve.append("<td align=left>" + rset.getString(3) + "</td>\n");//OrderId
                ManifestRetrieve.append("<td align=left>" + rset.getString(5) + "</td>\n");//Status
                ManifestRetrieve.append("<td align=left>" + rset.getString(7) + "</td>\n");//Status
                ManifestRetrieve.append("<td align=left>" + rset.getString(8) + "</td>\n");//Status
                if (rset.getInt(9) == 1) {
                    ManifestRetrieve.append("<td align=left>" + rset.getString(6) + "Hrs</td>\n");//Status
                } else {
                    ManifestRetrieve.append("<td align=left>" + rset.getString(10) + "Hrs</td>\n");//Status
                }
                //}
                ManifestRetrieve.append("</tr>\n");
                manifestNum = rset.getString(1).trim();
            }
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ManifestRetrieve", ManifestRetrieve.toString());
            Parser.SetField("UserId", UserId);
            Parser.SetField("Filter", Filter);
            Parser.SetField("Defaultlocation", Defaultlocation);
            Parser.SetField("locationList", locationList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/ManifestRetrieveInput_View.html");

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in LabManifestsRetrieve ** (GetInput )", servletContext, e, "LabManifestsRetrieve", "GetInput", conn);
            Services.DumException("LabManifestsRetrieve", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "LabManifestsRetrieve");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

        }
    }

    public static String getsamplecount(Connection Conn, String Database, String Manifestid) {
        String _getsamplecount = "";

        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String deviceid = "";
        String name = null;
        try {
            Query = "select count(*)  FROM  " + Database + ".ManifestDetails  where Manifestidx=" + Manifestid;
            hstmt = Conn.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                _getsamplecount = hrset.getString(1);
            }
            hrset.close();
            hstmt.close();
            return _getsamplecount;
        } catch (Exception localException) {
        }
        return _getsamplecount;
    }

    public StringBuilder locationlist(Connection conn) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuilder _locationList = new StringBuilder();
        try {


            Query = "Select Id, Location from roverlab.Locations";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            _locationList.append("<option value='' selected disabled>Select Location</option>");
            while (rset.next())
                _locationList.append("<option value=" + rset.getString(1) + "  >" + rset.getString(2) + "</option>");
            rset.close();
            stmt.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return _locationList;

    }


    public static HashMap<String, String> listoflocation(Connection Conn, String Database) {
        HashMap<String, String> hm = new HashMap();

        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String deviceid = "";
        String name = null;
        try {
            Query = "SELECT id,Location,concat(Address,'^^',City,'^',State,'^',Zip)	" +
                    " FROM  " + Database + ".Locations ORDER BY Id";
            hstmt = Conn.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                hm.put(hrset.getString(1), hrset.getString(2));
            }
            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception localException) {
        }
        return hm;
    }


    private void GetInput_org(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId, UtilityHelper helper, String locationArray) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;
        StringBuilder ManifestRetrieve = new StringBuilder();
        String manifestNum = "";
        try {
            ArrayList<String> al = new ArrayList<>();
            al.add(locationArray);
            String list = Arrays.toString(al.toArray()).replace("[", "").replace("]", "");

            String locCondition = "";
            if (locationArray.length() > 1) {
                locCondition = " AND f.Id IN (" + list + ") ";
            }

/*            Query = " SELECT CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " + //1
                    "DATE_FORMAT(a.DOB,'%m/%d/%Y'),  IFNULL(a.PhNumber,''), IFNULL(a.MRN,0), a.ID AS PatRegId, " +//5
                    "DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'),  " + //6
                    "CASE " +
                    "WHEN a.Insured = 'Yes' THEN 'Insured' " +
                    "WHEN a.Insured = 'NO' THEN 'Self Pay' " +
                    "ELSE 'Self Pay' " +
                    "END, b.OrderNum,b.OrderDate," + //9
                    "CASE " +
                    " WHEN a.Status = 0 THEN c.Status " +
                    " WHEN a.Status = 1 THEN c.Status" +
                    " WHEN a.Status = 2 THEN c.Status" +
                    " WHEN a.Status = 3 THEN c.Status " +
                    " ELSE 'Pending' END,d.Location " + //10
                    " FROM roverlab.PatientReg a" +
                    " INNER JOIN roverlab.TestOrder b ON a.ID = b.PatRegIdx " +
                    " INNER JOIN roverlab.ListofStages c ON b.StageIdx = c.Id " +
                    " INNER JOIN roverlab.Locations d ON a.TestingLocation = d.Id " +
                    " WHERE a.Status = 0 AND b.StageIdx = 1" +
                    " ORDER BY a.CreatedDate DESC limit 500";*/
            Query = "SELECT a.ManifestNum, c.OrderNum, " +
                    "CASE " +
                    " WHEN a.Status = 0 THEN d.Status " +
                    " WHEN a.Status = 1 THEN d.Status" +
                    " WHEN a.Status = 2 THEN d.Status" +
                    " WHEN a.Status = 3 THEN d.Status " +
                    " ELSE 'Pending' END,a.Id  " + //4
                    " FROM " + Database + ".ManifestsMaster a " +
                    " INNER JOIN " + Database + ".ManifestDetails b ON a.Id = b.ManifestIdx " +
                    " INNER JOIN " + Database + "..TestOrder c ON b.OrderId = c.Id " +
                    " INNER JOIN " + Database + "..ListofStages d ON c.StageIdx=d.Id  " +
                    " INNER JOIN " + Database + "..PatientReg e ON b.PatRegIdx = e.Id " +
                    " INNER JOIN " + Database + "..Locations f ON e.TestingLocation = f.Id " +
                    " WHERE a.isRetrieved = 1 " + locCondition + " ORDER BY a.CreatedDate ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ManifestRetrieve.append("<tr>\n");
                if (!manifestNum.equals(rset.getString(1))) {
                    ManifestRetrieve.append("<td align=left><input  class=\"checkSingle\" type=\"checkbox\" id=\"mList_" + rset.getInt(4) + "\" > \n");
                    ManifestRetrieve.append("<label for=\"mList_" + rset.getInt(4) + "\">" + rset.getString(1) + "</label><br></td>\n");//Manifest
                    ManifestRetrieve.append("<td align=left>" + rset.getString(2) + "</td>\n");//OrderId
                    ManifestRetrieve.append("<td align=left>" + rset.getString(3) + "</td>\n");//Status
                } else {
                    ManifestRetrieve.append("<td align=left> </td>");
                    ManifestRetrieve.append("<td align=left>" + rset.getString(2) + "</td>\n");//OrderId
                    ManifestRetrieve.append("<td align=left>" + rset.getString(3) + "</td>\n");//Status
                }
                ManifestRetrieve.append("</tr>\n");
                manifestNum = rset.getString(1).trim();
            }
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ManifestRetrieve", ManifestRetrieve.toString());
            Parser.SetField("UserId", UserId);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/ManifestRetrieveInput.html");

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in LabManifestsRetrieve ** (GetInput )", servletContext, e, "LabManifestsRetrieve", "GetInput", conn);
            Services.DumException("LabManifestsRetrieve", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "LabManifestsRetrieve");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

        }
    }


    private void SaveRetrieveManifest(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper, String directoryName) throws ServletException, IOException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String[] selectedManifest = new String[0];

        try {
            selectedManifest = request.getParameter("manifestSelected").trim().split(",");
            for (int i = 0; i < selectedManifest.length; i++) {
                if (selectedManifest[i].equals("on"))
                    continue;
                else {
                    System.out.println("SELECTED MANIFEST " + selectedManifest[i]);
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            " UPDATE roverlab.ManifestsMaster SET isRetrieved=2," +
                                    "RetrievedDate=NOW(),RetrievedBy=? " +
                                    "WHERE " + Database + ".ManifestsMaster.Id = ?");
                    MainReceipt.setString(1, UserId);
                    MainReceipt.setInt(2, Integer.parseInt(selectedManifest[i]));
                    System.out.println("UPDATE QUERY Manifest" + MainReceipt.toString());
                    MainReceipt.executeUpdate();
                    MainReceipt.close();


                    Query = "SELECT OrderId FROM " + Database + ".ManifestDetails " +
                            "WHERE ManifestIdx = " + selectedManifest[i];
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        MainReceipt = conn.prepareStatement(
                                " UPDATE " + Database + ".TestOrder SET StageIdx = 2,UpdatedAt=NOW(),UpdatedBy=? " +
                                        "WHERE " + Database + ".TestOrder.Id = ?");
                        MainReceipt.setString(1, UserId + "-Retrieved");
                        MainReceipt.setInt(2, rset.getInt(1));
                        System.out.println("UPDATE QUERY " + MainReceipt.toString());
                        MainReceipt.executeUpdate();
                        MainReceipt.close();
                    }
                    rset.close();
                    stmt.close();
                }
            }
            out.println("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
