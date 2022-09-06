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

@SuppressWarnings("Duplicates")
public class AdFacAssign extends HttpServlet {

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private PreparedStatement pStmt = null;

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
        String UserIndex = "";
        String DirectoryName = "";
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
                UserIndex = session.getAttribute("UserIndex").toString();

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
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Advocate Facility Assignment", "Assigning Facilities to Advocates", FacilityIndex);
                GetInput(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("GetFacilities")) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get Facilities", "Get Facility List", FacilityIndex);
                GetFacilities(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("SaveData")) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get Facilities", "Get Facility List", FacilityIndex);
                SaveData(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
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


    public void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {

        try {
            StringBuilder AdvocateList = new StringBuilder();
            StringBuilder SentFromPhNList = new StringBuilder();

            Query = "Select indexptr, IFNULL(username,'') from oe.sysusers where usertype in (7,10)";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            AdvocateList.append("<option value=''> Select Advocate </option>");
            while (rset.next()) {
                AdvocateList.append("<option value=" + rset.getInt(1) + "> " + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();

            Query = "SELECT IFNULL(SenderNumber,'') FROM oe.TwolioSenderPhN where Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            SentFromPhNList.append("<option value=''> Select Sender Number </option>");
            while (rset.next()) {
                SentFromPhNList.append("<option value=" + rset.getString(1) + "> " + rset.getString(1) + " </option>");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("AdvocateList", String.valueOf(AdvocateList));
            Parser.SetField("SentFromPhNList", String.valueOf(SentFromPhNList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/AdvocateFacilityAssignment.html");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //out.println(e.getMessage());
        }
    }

    public void GetFacilities(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {
        try {
            String AdvocatePhN = "";
            String twilioPhN = "";
            String FacIdx = "";
            StringBuilder FacChkboxList = new StringBuilder();
            StringBuilder SentFromPhNList = new StringBuilder();
            int AdvocateIdx = Integer.parseInt(request.getParameter("AdvocateIdx"));

            Query = "Select IFNULL(AdvocatePhNumber,''),IFNULL(PhNumber,'') from oe.AdvocateSMSNumber where AdvocateIdx = " + AdvocateIdx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()){
                AdvocatePhN = rset.getString(1);
                twilioPhN = rset.getString(2);
            }
            rset.close();
            stmt.close();

            //Query = "Select Id, IFNULL(name,'') from oe.clients ";
            Query = " Select a.Id, IFNULL(a.name,'') from oe.clients a \n" +
                    "LEFT JOIN oe.AdvocateSMSNumber b on b.FacilityIdx = a.Id \n" +
                    "where b.status = 0 and b.AdvocateIdx =  " + AdvocateIdx;//+ " and FacilityIdx = "+rset.getInt(1);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                FacIdx += rset.getString(1) + ",";
                FacChkboxList.append("<input type=\"checkbox\" id=\"Facility_" + rset.getInt(1) + "\" name=\"Facility_" + rset.getInt(1) + "\" class=\"filled-in chk-col-info\" checked /> <label for=\"Facility_" + rset.getInt(1) + "\">" + rset.getString(2) + "</label> \t");
                //out.println("Checked--"+rset.getString(2));
            }
            rset.close();
            stmt.close();

            if (!FacIdx.equals("")) {
                FacIdx = FacIdx.substring(0, FacIdx.length() - 1);
            }
            //FacIdx = FacIdx.replace(FacIdx.substring(FacIdx.length()-1), "");

            //out.println(FacIdx);

            if (!FacIdx.equals("")) {
                Query = " Select Id, IFNULL(name,'') from oe.clients where Id not in (" + FacIdx + ") ";
            } else {
                Query = "Select Id, IFNULL(name,'') from oe.clients ";
            }
            //System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                FacChkboxList.append("<input type=\"checkbox\" id=\"Facility_" + rset.getInt(1) + "\" name=\"Facility_" + rset.getInt(1) + "\" class=\"filled-in chk-col-info\" /> <label for=\"Facility_" + rset.getInt(1) + "\">" + rset.getString(2) + "</label> \t");
                //out.println("Checked--"+rset.getString(2));
            }
            rset.close();
            stmt.close();


            Query = "SELECT IFNULL(SenderNumber,'') FROM oe.TwolioSenderPhN where Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            SentFromPhNList.append("<option value=''> Select Sender Number </option>");
            while (rset.next()) {
                if(twilioPhN.equals(rset.getString(1)))
                    SentFromPhNList.append("<option value=" + rset.getString(1) + " selected> " + rset.getString(1) + " </option>");
                else
                    SentFromPhNList.append("<option value=" + rset.getString(1) + "> " + rset.getString(1) + " </option>");
            }
            rset.close();
            stmt.close();

            out.println(AdvocatePhN + "|" + FacChkboxList.toString()+ "|" + SentFromPhNList.toString());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void SaveData(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {
        try {
            StringBuilder AdvocateList = new StringBuilder();
            StringBuilder SentFromPhNList = new StringBuilder();
            int AdvocateIdx = 0;
            String AdvocatePhN = "";
            String SentFromPhN = "";
            String Facility = "";

            AdvocateIdx = Integer.parseInt(request.getParameter("AdvocateIdx").trim());
            SentFromPhN = request.getParameter("SentFromPhN").trim();
            AdvocatePhN = request.getParameter("AdvocatePhN").trim();

            //get old data and insert into history table
            InsertAdvocateSMSNumberHistory(request, conn, context, Database, AdvocateIdx, out, helper);
            //Delete  wbole record for that user
            Query = "Delete from oe.AdvocateSMSNumber where AdvocateIdx = " + AdvocateIdx;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            Query = "Select id from oe.clients";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Facility = "";
                Facility = CheckCheckBoxValue(request, "Facility_" + rset.getInt(1));
                if (Facility.equals("1")) {
                    pStmt = conn.prepareStatement(
                            "INSERT INTO oe.AdvocateSMSNumber(AdvocateIdx,FacilityIdx,PhNumber,Status," +
                                    "CreatedDate, AdvocatePhNumber) " +
                                    " VALUES (?,?,?,0,NOW(),?) ");
                    pStmt.setInt(1, AdvocateIdx);
                    pStmt.setInt(2, rset.getInt(1));//FacilityIdx
                    pStmt.setString(3, SentFromPhN);
                    pStmt.setString(4, AdvocatePhN);
                    pStmt.executeUpdate();
                    pStmt.close();
                }
            }
            rset.close();
            stmt.close();

            Query = "Select indexptr, IFNULL(username,'') from oe.sysusers where usertype in (7,10)";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            AdvocateList.append("<option value=''> Select Advocate </option>");
            while (rset.next()) {
                AdvocateList.append("<option value=" + rset.getInt(1) + "> " + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();

            Query = "SELECT IFNULL(SenderNumber,'') FROM oe.TwolioSenderPhN where Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            SentFromPhNList.append("<option value=''> Select Sender Number </option>");
            while (rset.next()) {
                SentFromPhNList.append("<option value=" + rset.getString(1) + "> " + rset.getString(1) + " </option>");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Information has been Updated!");
            Parser.SetField("FormName", "AdFacAssign");
            Parser.SetField("MRN", "MRN: " + "");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("ClientIndex", String.valueOf(ClientId));
            Parser.SetField("PatientName", String.valueOf(""));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/Message.html");

//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("AdvocateList", String.valueOf(AdvocateList));
//            Parser.SetField("SentFromPhNList", String.valueOf(SentFromPhNList));
//            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/AdvocateFacilityAssignment.html");
        } catch (Exception e) {
            //out.println(e.getMessage());
            System.out.println(e.getMessage());
            return;
        }
    }

    private void InsertAdvocateSMSNumberHistory(HttpServletRequest request, Connection conn, ServletContext context, String Database, int AdvocateIdx, PrintWriter out, UtilityHelper helper) throws FileNotFoundException {
        try {
            Query = "Select Id, IFNULL(AdvocateIdx,''), IFNULL(FacilityIdx,''), IFNULL(PhNumber,''), IFNULL(Status,0), IFNULL(CreatedDate,''), " +
                    "IFNULL(AdvocatePhNumber,'') from oe.AdvocateSMSNumber where AdvocateIdx = " + AdvocateIdx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                pStmt = conn.prepareStatement(
                        "INSERT INTO oe.AdvocateSMSNumber_History(OldAdvocateSMSNumberId,AdvocateIdx,FacilityIdx,PhNumber," +
                                "Status, CreatedDateOld,AdvocatePhNumber,CreatedDateN) " +
                                " VALUES (?,?,?,?,?,?,?,NOW()) ");
                pStmt.setInt(1, rset.getInt(1));
                pStmt.setInt(2, AdvocateIdx);
                pStmt.setInt(3, rset.getInt(3));
                pStmt.setString(4, rset.getString(4));
                pStmt.setInt(5, rset.getInt(5));
                pStmt.setString(6, rset.getString(6));
                pStmt.setString(7, rset.getString(7));
                pStmt.executeUpdate();
                pStmt.close();
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            //out.println("Error in Inserting AdvoacteSMSNumber_History"+e.getMessage());
            System.out.println("Error in Inserting AdvoacteSMSNumber_History" + e.getMessage());
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