package md;

import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
public class ajaxQueries extends HttpServlet {
    public void init(ServletConfig config)
            throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String ActionID = "";


        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        Connection conn = null;

        try {
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("Error", "Unable to connect. Our team is looking into it!");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            ActionID = request.getParameter("ActionID").trim();
            if (ActionID.equals("getLocations")) {
                getLocations(request, response, out, conn);
            } else if (ActionID.equals("getVisitNumber")) {
                getVisitNumber(request, conn, out);
            }
        } catch (Exception Ex) {

        }
        try {
            conn.close();
        } catch (Exception localException1) {
        }
        out.flush();
        out.close();
    }

    private void getLocations(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuilder doctorList = new StringBuilder();
        String LocationId = request.getParameter("LocationIdx");
        Query = "select Id,CONCAT('Dr. ',DoctorsFirstName,'',DoctorsLastName) " +
                "from roverlab.DoctorsList where LocationIdx=" + LocationId;
        try {
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            doctorList.append("<option value='' disabled>Select Doctor</option>");
            while (rset.next()) {
                doctorList.append("<option value=" + rset.getInt(1) + " selected>" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();
        } catch (Exception localException) {
        }
        out.println(doctorList);
        out.flush();
        out.close();
    }

    private void getVisitNumber(HttpServletRequest request, Connection conn, PrintWriter out) {
        String visitNumber = "";
        ResultSet rset = null;
        String Query = "";
        Statement stmt = null;
        int mrn = 0;
        int PatientRegId = 0;
        int FoundAddInfo = 0;
        int visitIdx = Integer.parseInt(request.getParameter("visitIdx").trim());
        int facilityIdx = Integer.parseInt(request.getParameter("facilityIdx").trim());

        int AdditionalInfoSelect = 0;
        int PatientCatagory = 0;
        int ReasonLeaving = 0;
        int RefPhysicianName = 0;
        int PatientStatus = 0;
        String COVIDTestDate = "";
        String RefName = "";
        String RefSourceName = "";
        String CovidTestNo = "";
        String COVIDStatus = "";
        String TestType = "";
        String Q_Filler = "";

        StringBuilder CovidBuffer = new StringBuilder();
        StringBuilder PatientCatagoryBuffer = new StringBuilder();
        StringBuilder ReasonLeavingBuffer = new StringBuilder();
        StringBuilder PatientStatusBuffer = new StringBuilder();
        try {
            String dbName = "";
            Query = "Select dbname from oe.clients where Id = " + facilityIdx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                dbName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Query = "SELECT VisitNumber,MRN,PatientRegId from " + dbName + ".PatientVisit " +
                    " WHERE Id = " + visitIdx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                visitNumber = rset.getString(1);
                mrn = rset.getInt(2);
                PatientRegId = rset.getInt(3);
            }
            rset.close();
            stmt.close();

            Query = "Select Count(*) from " + dbName + ".Patient_AdditionalInfo " +
                    "where PatientRegId = " + PatientRegId + " and VisitId = '" + visitIdx + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundAddInfo = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (facilityIdx == 39 || facilityIdx == 40) { // for Floresville and Schertz
                Q_Filler = ",TestType";
            }


            if (FoundAddInfo > 0) {
                Query = "Select IFNULL(AdditionalInfoSelect,0), IFNULL(DATE_FORMAT(CovidTestDate,'%Y-%m-%d'),''), " +
                        "IFNULL(PatientCatagory,0), IFNULL(RefName,''), IFNULL(ReasonLeaving,''), " +
                        "IFNULL(RefPhysician,''), IFNULL(RefSourceName,''), IFNULL(PatientStatus,''), " +
                        "IFNULL(CovidTestNo,'')," +
                        "CASE WHEN CovidStatus = '' THEN 'NONE' WHEN CovidStatus = 1 THEN 'POSITIVE' " +
                        "WHEN CovidStatus = 0 THEN 'NEGATIVE'  WHEN CovidStatus = -1 THEN 'NONE' ELSE 'NONE' END,CovidStatus  " + Q_Filler +
                        " from " + dbName + ".Patient_AdditionalInfo " +
                        " where PatientRegId = " + PatientRegId + " and " +
                        " VisitId = '" + visitIdx + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    AdditionalInfoSelect = rset.getInt(1);
                    COVIDTestDate = rset.getString(2);
                    PatientCatagory = rset.getInt(3);
                    RefName = rset.getString(4);
                    ReasonLeaving = rset.getInt(5);
                    RefPhysicianName = rset.getInt(6);
                    RefSourceName = rset.getString(7);
                    PatientStatus = rset.getInt(8);
                    CovidTestNo = rset.getString(9);
                    COVIDStatus = rset.getString(10);
                    COVIDStatus = rset.getString(11);
                    if (facilityIdx == 39 || facilityIdx == 40)
                        TestType = rset.getString(12);
                }
                rset.close();
                stmt.close();


            }
        } catch (Exception Ex) {
            Services.DumException("md - ajaxQueries", "getVisitNumber", request, Ex, getServletContext());
        }
/*        out.println("VN-" + mrn + "-" + visitNumber+"|"+FoundAddInfo+"|"+CovidBuffer+"|"+
                    COVIDTestDate+"|"+CovidTestNo+"|"+PatientCatagoryBuffer+"|"+
                    ReasonLeavingBuffer+"|"+PatientStatusBuffer);*/
        out.println("VN-" + mrn + "-" + visitNumber + "|" + FoundAddInfo + "|" + COVIDStatus + "|" +
                COVIDTestDate + "|" + CovidTestNo + "|" + PatientCatagory + "|" + RefName + "|" +
                ReasonLeaving + "|" + RefSourceName + "|" + PatientStatus + '|' + TestType);
    }


}
