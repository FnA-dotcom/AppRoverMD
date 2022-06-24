//
// Decompiled by Procyon v0.5.36
//

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Calendar;

@SuppressWarnings("Duplicates")
public class PatientUpdateInfo21APRIL2021 extends HttpServlet {
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

    public void handleRequestold(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        ResultSet rset = null;
        Statement stmt = null;
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        String Database = "";
        String Query = "";
        int ClientId = 0;
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();
//    conn = Services.getMysqlConn();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        try {
            final Cookie[] cookies = request.getCookies();
            UserId = (Zone = (Passwd = ""));
            String UserName = "";
            final int checkCookie = 0;
            for (int coky = 0; coky < cookies.length; ++coky) {
                final String cName = cookies[coky].getName();
                final String cValue = cookies[coky].getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
                }
                if (cName.equals("username")) {
                    UserName = cValue;
                }
            }
            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientId = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select dbname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();

            if (ActionID.equals("GetInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update Patient Information", "Open Patient Screen Upadte Info", ClientId);
                GetInput(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("AddNotes")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Notes ", "Save Notes", ClientId);
                AddNotes(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("DeleteNote")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Delete Notes ", "Deleting any Notes", ClientId);
                DeleteNote(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("UpdateDoctorId")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update Doctor Information ", "Save Doctors Information", ClientId);
                UpdateDoctorId(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("UpdateCovidStatus")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update COVID Information ", "Save COVID Information", ClientId);
                UpdateCovidStatus(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("UpdateDOS")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update COVID Information ", "Save COVID Information", ClientId);
                UpdateDOS(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("AddAdditionalInfo")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "inserting Additional Info ", "Save Additional Info", ClientId);
                AddAdditionalInfo(request, out, conn, context, UserId, Database, ClientId);
            } else {
                out.println("Under Development");
            }
            try {
                conn.close();
            } catch (Exception ex) {
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();

        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();

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

            try {
/*                boolean ValidSession = FacilityLogin.checkSession(out, request, context, response);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    return;
                }*/
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

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "GetInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update Patient Information", "Open Patient Screen Upadte Info", FacilityIndex);
                    GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "AddNotes":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Notes ", "Save Notes", FacilityIndex);
                    AddNotes(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "UpdateDoctorId":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update Doctor Information ", "Save Doctors Information", FacilityIndex);
                    UpdateDoctorId(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "UpdateCovidStatus":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update COVID Information ", "Save COVID Information", FacilityIndex);
                    UpdateCovidStatus(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "AddAdditionalInfo":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "inserting Additional Info ", "Save Additional Info", FacilityIndex);
                    AddAdditionalInfo(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "UpdateDOS":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update COVID Information ", "Save COVID Information", FacilityIndex);
                    UpdateDOS(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "DeleteNote":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Delete Notes ", "Deleting any Notes", FacilityIndex);
                    DeleteNote(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
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

    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String PatientName = "";
        String DOB = "";
        String PhNumber = "";
        String MRN = "";
        String ReasonVisit = "";
        String DOS = "";
        String COVIDStatus = "";
        String Address = "";
        String City = "";
        String State = "";
        String County = "";
        String ZipCode = "";
        String DoctorsId = "";
        String BundleFnName = "";
        String LabelFnName = "";
        int FoundAddInfo = 0;
        int SelfPayChk = 0;
        String WorkerCompPolicyChk = "0";
        String MotorVehicleAccidentChk = "0";
        String COVIDTestDate = "";
        int AdditionalInfoSelect = 0;
        int PatientCatagory = 0;
        int ReasonLeaving = 0;
        int PatientStatus = 0;
        String RefName = "";
        String CovidTestNo = "";
        String Style = "";
        int RefPhysicianName = 0;
        String RefSourceName = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer NotesList = new StringBuffer();
        StringBuffer CovidBuffer = new StringBuffer();
        StringBuffer DoctorsBuffer = new StringBuffer();
        StringBuffer AdditionalInfoSelectBuffer = new StringBuffer();
        StringBuffer PatientCatagoryBuffer = new StringBuffer();
        StringBuffer ReasonLeavingBuffer = new StringBuffer();
        StringBuffer PatientStatusBuffer = new StringBuffer();
        StringBuffer RefPhysicianNameBuffer = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Day = new StringBuffer();
        StringBuffer Year = new StringBuffer();
        StringBuffer Hours = new StringBuffer();
        StringBuffer Mins = new StringBuffer();
        String ID = request.getParameter("ID").trim();
        int SNo = 1;
        try {
            Query = "Select Bundle_FnName, Label_FnName from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                BundleFnName = rset.getString(1);
                LabelFnName = rset.getString(2);
            }
            rset.close();
            stmt.close();

            Query = "SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), DATE_FORMAT(DOB,'%m/%d/%Y'), IFNULL(PhNumber,''), " +
                    "IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), " +
                    "CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'NONE' ELSE 'NONE' END, IFNULL(Address,''), " +
                    "IFNULL(City,''), IFNULL(State,''), IFNULL(County,''), IFNULL(ZipCode,''), IFNULL(DoctorsName,''), IFNULL(SelfPayChk,0) " +
                    "FROM " + Database + ".PatientReg where Status = 0 and ID = " + ID;
            //out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientName = rset.getString(1);
                DOB = rset.getString(2);
                PhNumber = rset.getString(3);
                MRN = rset.getString(4);
                ReasonVisit = rset.getString(5);
                DOS = rset.getString(7);
                COVIDStatus = rset.getString(8);
                Address = rset.getString(9);
                City = rset.getString(10);
                State = rset.getString(11);
                County = rset.getString(12);
                ZipCode = rset.getString(13);
                DoctorsId = rset.getString(14);
                SelfPayChk = rset.getInt(15);
            }
            rset.close();
            stmt.close();

            if (ClientId == 9 || ClientId == 28) {
                Query = "Select IFNULL(WorkersCompPolicyChk,0), IFNULL(MotorVehicleAccidentChk,0) from " + Database + ".PatientReg_Details where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    WorkerCompPolicyChk = rset.getString(1);
                    MotorVehicleAccidentChk = rset.getString(2);
                }
                rset.close();
                stmt.close();
            } else {
                if (SelfPayChk == 1) {
                    Query = "Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0) from " + Database + ".InsuranceInfo where PatientRegId = " + ID;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        WorkerCompPolicyChk = rset.getString(1);
                        MotorVehicleAccidentChk = rset.getString(2);
                    }
                    rset.close();
                    stmt.close();
                }

            }

            if (WorkerCompPolicyChk.equals("0")) {
                WorkerCompPolicyChk = "NO";
            } else {
                WorkerCompPolicyChk = "YES";
            }

            if (MotorVehicleAccidentChk.equals("0")) {
                MotorVehicleAccidentChk = "NO";
            } else {
                MotorVehicleAccidentChk = "YES";
            }

            Query = "Select Id, CONCAT(IFNULL(DoctorsLastName,''),', ',IFNULL(DoctorsFirstName,'')) from " + Database + ".DoctorsList where Status = 1 order by DoctorsLastName";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            DoctorsBuffer.append("<option value=''>Select Doctor</option>");
            while (rset.next()) {
                if (DoctorsId.equals(rset.getString(1).trim())) {
                    DoctorsBuffer.append("<option value='" + rset.getString(1) + "' selected>" + rset.getString(2) + "</option>");
                } else {
                    DoctorsBuffer.append("<option value='" + rset.getString(1) + "'>" + rset.getString(2) + "</option>");
                }
            }
            rset.close();
            stmt.close();

            if (COVIDStatus.toUpperCase().equals("POSITIVE")) {
                CovidBuffer.append("<select class=\"form-control \" id=\"PayMethod\" name=\"PayMethod\" onchange=\"SaveCovidStatus(this.value)\">\n" +
                        "<option value=\"\"> Please Select Any</option>\n" +
                        "<option value=\"1\" selected> Positive</option>\n" +
                        "<option value=\"0\"> Negative</option>\n" +
                        "<option value=\"-1\"> None</option>\n" +
                        "</select>\n");
            } else if (COVIDStatus.toUpperCase().equals("NEGATIVE")) {
                CovidBuffer.append("<select class=\"form-control \" id=\"PayMethod\" name=\"PayMethod\" onchange=\"SaveCovidStatus(this.value)\">\n" +
                        "<option value=\"\"> Please Select Any</option>\n" +
                        "<option value=\"1\" > Positive</option>\n" +
                        "<option value=\"0\" selected> Negative</option>\n" +
                        "<option value=\"-1\"> None</option>\n" +
                        "</select>\n");
            } else if (COVIDStatus.toUpperCase().equals("SUSPECTED")) {
                CovidBuffer.append("<select class=\"form-control \" id=\"PayMethod\" name=\"PayMethod\" onchange=\"SaveCovidStatus(this.value)\">\n" +
                        "<option value=\"\"> Please Select Any</option>\n" +
                        "<option value=\"1\" > Positive</option>\n" +
                        "<option value=\"0\" > Negative</option>\n" +
                        "<option value=\"-1\" selected> None</option>\n" +
                        "</select>\n");
            } else {
                CovidBuffer.append("<select class=\"form-control \" id=\"PayMethod\" name=\"PayMethod\" onchange=\"SaveCovidStatus(this.value)\">\n" +
                        "<option value=\"\"> Please Select Any</option>\n" +
                        "<option value=\"1\" > Positive</option>\n" +
                        "<option value=\"0\" > Negative</option>\n" +
                        "<option value=\"-1\"> None</option>\n" +
                        "</select>\n");
            }


            Query = " Select IFNULL(MRN,''), IFNULL(Notes,''), IFNULL(DATE_FORMAT(CreatedDate,'%m/%d/%Y %T'),''), IFNULL(CreatedBy,''), Id  " +
                    " from " + Database + ".Notes where PatientRegId = " + ID + " and Status = 0 order by Id desc ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//                out.println(Query);
            while (rset.next()) {
                NotesList.append("<tr><td align=left>" + SNo + "</td>\n");
                NotesList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                NotesList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                NotesList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                NotesList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                NotesList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteNote(" + rset.getInt(5) + ")\"></i></td>\n");
                NotesList.append("</tr>\n");
                SNo++;
            }
            rset.close();
            stmt.close();

            Query = "Select Count(*) from " + Database + ".Patient_AdditionalInfo where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundAddInfo = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            Style = "#AdditionalInformation{\n" +
                    " display : none;\n" +
                    "}";
            if (FoundAddInfo > 0) {
                Style = "#AdditionalInformation{\n" +
                        " display : block;\n" +
                        "}";
                Query = "Select IFNULL(AdditionalInfoSelect,0), IFNULL(DATE_FORMAT(CovidTestDate,'%Y-%m-%d'),''), IFNULL(PatientCatagory,0), IFNULL(RefName,''), IFNULL(ReasonLeaving,''), " +
                        "IFNULL(RefPhysician,''), IFNULL(RefSourceName,''), IFNULL(PatientStatus,''), IFNULL(CovidTestNo,'') from " + Database + ".Patient_AdditionalInfo where PatientRegId = " + ID;
                //out.println(Query);
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
                }
                rset.close();
                stmt.close();

            }
            Query = "Select Id, RefPhysicianName from " + Database + ".RefPhysicianName";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            RefPhysicianNameBuffer.append("<option value=\"\" selected>None </option>");
            while (rset.next()) {
                if (rset.getInt(1) == RefPhysicianName) {
                    RefPhysicianNameBuffer.append("<option value=\"" + rset.getInt(1) + "\" selected>" + rset.getString(2) + " </option>");
                } else {
                    RefPhysicianNameBuffer.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + " </option>");
                }
            }
            rset.close();
            stmt.close();

            if (AdditionalInfoSelect == 1) {
                AdditionalInfoSelectBuffer.append("<select class=\"form-control\" id=\"AdditionalInfoSelect\" name=\"AdditionalInfoSelect\" onchange=\"OpenAdditonalInfoTab(this.value)\">\n" +
                        "<option value=\"\">SELECT ANY</option> \n" +
                        "<option value=\"1\" selected>YES </option> \n" +
                        "<option value=\"0\" >NO </option> \n" +
                        "</select>\n");
            } else if (AdditionalInfoSelect == 0) {
                AdditionalInfoSelectBuffer.append("<select class=\"form-control\" id=\"AdditionalInfoSelect\" name=\"AdditionalInfoSelect\" onchange=\"OpenAdditonalInfoTab(this.value)\">\n" +
                        "<option value=\"\">SELECT ANY</option> \n" +
                        "<option value=\"1\" >YES </option> \n" +
                        "<option value=\"0\" selected>NO </option> \n" +
                        "</select>\n");
            } else {
                AdditionalInfoSelectBuffer.append("<select class=\"form-control\" id=\"AdditionalInfoSelect\" name=\"AdditionalInfoSelect\" onchange=\"OpenAdditonalInfoTab(this.value)\">\n" +
                        "<option value=\"\">SELECT ANY</option> \n" +
                        "<option value=\"1\" >YES </option> \n" +
                        "<option value=\"0\" selected>NO </option> \n" +
                        "</select>\n");
            }

            if (PatientCatagory == 1) {
                PatientCatagoryBuffer.append("<select class=\"form-control\" id=\"PatientCatagory\" name=\"PatientCatagory\" >\n" +
                        "<option value=\"\">Select Any</option> \n" +
                        "<option value=\"1\" selected>VIP </option> \n" +
                        "<option value=\"2\">SVIP </option>\n" +
                        "</select>\n");
            } else if (PatientCatagory == 2) {
                PatientCatagoryBuffer.append("<select class=\"form-control\" id=\"PatientCatagory\" name=\"PatientCatagory\" >\n" +
                        "<option value=\"\">Select Any</option> \n" +
                        "<option value=\"1\" >VIP </option> \n" +
                        "<option value=\"2\" selected>SVIP </option>\n" +
                        "</select>\n");
            } else {
                PatientCatagoryBuffer.append("<select class=\"form-control\" id=\"PatientCatagory\" name=\"PatientCatagory\" >\n" +
                        "<option value=\"\" selected>Select Any</option> \n" +
                        "<option value=\"1\" >VIP </option> \n" +
                        "<option value=\"2\" >SVIP </option>\n" +
                        "</select>\n");
            }

            if (ReasonLeaving == 1) {
                ReasonLeavingBuffer.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" >\n" +
                        "<option value=\"\">Select Any</option> \n" +
                        "<option value=\"1\" selected>MSE </option> \n" +
                        "<option value=\"2\">AMA </option>\n" +
                        "<option value=\"3\">LWBS </option>\n" +
                        "</select>\n");
            } else if (ReasonLeaving == 2) {
                ReasonLeavingBuffer.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" >\n" +
                        "<option value=\"\">Select Any</option> \n" +
                        "<option value=\"1\" >MSE </option> \n" +
                        "<option value=\"2\"selected >AMA </option>\n" +
                        "<option value=\"3\">LWBS </option>\n" +
                        "</select>\n");
            } else if (ReasonLeaving == 3) {
                ReasonLeavingBuffer.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" >\n" +
                        "<option value=\"\">Select Any</option> \n" +
                        "<option value=\"1\" >MSE </option> \n" +
                        "<option value=\"2\" >AMA </option>\n" +
                        "<option value=\"3\" selected>LWBS </option>\n" +
                        "</select>\n");
            } else {
                ReasonLeavingBuffer.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" >\n" +
                        "<option value=\"\" selected>Select Any</option> \n" +
                        "<option value=\"1\" >MSE </option> \n" +
                        "<option value=\"2\" >AMA </option>\n" +
                        "<option value=\"3\" >LWBS </option>\n" +
                        "</select>\n");
            }

            if (PatientStatus == 1) {
                PatientStatusBuffer.append("<select class=\"form-control\" id=\"PatientStatus\" name=\"PatientStatus\" >\n" +
                        "<option value=\"\">Select Any </option>\n" +
                        "<option value=\"2\">OBS</option>\n" +
                        "<option value=\"1\" selected>Transferred</option>\n" +
                        "<option value=\"3\" >OBS/Transferred</option>\n" +
                        "</select>\n");
            } else if (PatientStatus == 2) {
                PatientStatusBuffer.append("<select class=\"form-control\" id=\"PatientStatus\" name=\"PatientStatus\" >\n" +
                        "<option value=\"\">Select Any </option>\n" +
                        "<option value=\"2\" selected>OBS</option>\n" +
                        "<option value=\"1\" >Transferred</option>\n" +
                        "<option value=\"3\" >OBS/Transferred</option>\n" +
                        "</select>\n");
            } else if (PatientStatus == 3) {
                PatientStatusBuffer.append("<select class=\"form-control\" id=\"PatientStatus\" name=\"PatientStatus\" >\n" +
                        "<option value=\"\">Select Any </option>\n" +
                        "<option value=\"2\" >OBS</option>\n" +
                        "<option value=\"1\" >Transferred</option>\n" +
                        "<option value=\"3\" selected >OBS/Transferred</option>\n" +
                        "</select>\n");
            } else {
                PatientStatusBuffer.append("<select class=\"form-control\" id=\"PatientStatus\" name=\"PatientStatus\" >\n" +
                        "<option value=\"\" selected>Select Any </option>\n" +
                        "<option value=\"2\" >OBS</option>\n" +
                        "<option value=\"1\" >Transferred</option>\n" +
                        "<option value=\"3\" >OBS/Transferred</option>\n" +
                        "</select>\n");
            }

            String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            int year = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = 1; i <= month.length; i++) {
                Month.append("<option value=" + i + ">" + month[i - 1] + "</option>");
            }
            for (int i = 1; i <= 31; i++) {
                Day.append("<option value=" + i + ">" + i + "</option>");
            }
            for (int i = 1901; i <= year; i++) {
                if (i == year) {
                    Year.append("<option value=" + i + " selected>" + i + "</option>");
                } else {
                    Year.append("<option value=" + i + ">" + i + "</option>");
                }
            }
            for (int i = 1; i <= 23; i++) {
                Hours.append("<option value=" + i + ">" + i + "</option>");
            }
            for (int i = 1; i <= 59; i++) {
                Mins.append("<option value=" + i + ">" + i + "</option>");
            }

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.SetField("DOB", String.valueOf(DOB));
            Parser.SetField("PhNumber", String.valueOf(PhNumber));
            Parser.SetField("MRN", String.valueOf(MRN));
            Parser.SetField("ReasonVisit", String.valueOf(ReasonVisit));
            Parser.SetField("DOS", String.valueOf(DOS));
            Parser.SetField("COVIDStatus", String.valueOf(COVIDStatus));
            Parser.SetField("COVIDStatus", String.valueOf(COVIDStatus));
            Parser.SetField("WorkerCompPolicyChk", String.valueOf(WorkerCompPolicyChk));
            Parser.SetField("MotorVehicleAccidentChk", String.valueOf(MotorVehicleAccidentChk));
            Parser.SetField("Address", String.valueOf(Address));
            Parser.SetField("City", String.valueOf(City));
            Parser.SetField("State", String.valueOf(State));
            Parser.SetField("County", String.valueOf(County));
            Parser.SetField("ZipCode", String.valueOf(ZipCode));
            Parser.SetField("ID", String.valueOf(ID));
            Parser.SetField("BundleFnName", String.valueOf(BundleFnName));
            Parser.SetField("LabelFnName", String.valueOf(LabelFnName));
            Parser.SetField("DoctorsBuffer", String.valueOf(DoctorsBuffer));
            Parser.SetField("CovidBuffer", String.valueOf(CovidBuffer));
            Parser.SetField("Style", String.valueOf(Style));

            Parser.SetField("RefPhysicianNameBuffer", String.valueOf(RefPhysicianNameBuffer));
            Parser.SetField("PatientStatusBuffer", String.valueOf(PatientStatusBuffer));
            Parser.SetField("PatientCatagoryBuffer", String.valueOf(PatientCatagoryBuffer));
            Parser.SetField("ReasonLeavingBuffer", String.valueOf(ReasonLeavingBuffer));
            Parser.SetField("RefSourceName", String.valueOf(RefSourceName));
            Parser.SetField("COVIDTestDate", String.valueOf(COVIDTestDate));
            Parser.SetField("CovidTestNo", String.valueOf(CovidTestNo));
            Parser.SetField("RefName", String.valueOf(RefName));
            Parser.SetField("AdditionalInfoSelectBuffer", String.valueOf(AdditionalInfoSelectBuffer));

            Parser.SetField("Month", String.valueOf(Month));
            Parser.SetField("Day", String.valueOf(Day));
            Parser.SetField("Year", String.valueOf(Year));
            Parser.SetField("Hours", String.valueOf(Hours));
            Parser.SetField("Mins", String.valueOf(Mins));
            Parser.SetField("NotesList", String.valueOf(NotesList));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/PatientUpdateInfo.html");
        } catch (Exception var11) {
            out.println(Query);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void AddNotes(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        String Notes = request.getParameter("Notes").trim();
        String MRN = request.getParameter("MRN").trim();
        String PatientId = request.getParameter("PatientId").trim();
        try {
            PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".Notes (ClientIndex,PatientRegId,MRN ,"
                    + " Notes,CreatedDate,CreatedBy) \n"
                    + " VALUES (?,?,?,?,now(),?) ");
            MainReceipt.setInt(1, ClientId);
            MainReceipt.setString(2, PatientId);
            MainReceipt.setString(3, MRN);
            MainReceipt.setString(4, Notes);
            MainReceipt.setString(5, UserId);
            MainReceipt.executeUpdate();
            MainReceipt.close();

            out.println("1");

        } catch (Exception e) {
            out.println("Error: Insertion Notes Table " + e.getMessage());
            Services.DumException("PatientUpdateInfo", "AddNotes 2- Insertion Notes Table :", request, e, this.getServletContext());
            return;
        }


    }

    void DeleteNote(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int NoteId = Integer.parseInt(request.getParameter("ID").trim());

        try {
            Query = "Update " + Database + ".Notes Set Status = 1 where ID = " + NoteId;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            out.println("1");

        } catch (Exception e) {
            out.println("Error: Updating Notes Table " + e.getMessage());
            Services.DumException("PatientUpdateInfo", "AddNotes 2- Updating Note able :", request, e, this.getServletContext());
            return;
        }


    }

    void UpdateDoctorId(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String DoctorsName = request.getParameter("DoctorsName").trim();
        String PatientRegId = request.getParameter("PatientRegId").trim();

        try {
            Query = "Update " + Database + ".PatientReg Set DoctorsName = '" + DoctorsName + "' where ID = " + PatientRegId;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            out.println("1");

        } catch (Exception e) {
            out.println("Error: Updating Patient Reg Table " + e.getMessage());
            Services.DumException("PatientUpdateInfo", "AddNotes 2- Updating Patient RegT able :", request, e, this.getServletContext());
            return;
        }


    }

    void UpdateCovidStatus(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String CovidStatus = request.getParameter("CovidStatus").trim();
        String PatientRegId = request.getParameter("PatientRegId").trim();

        try {
            Query = "Update " + Database + ".PatientReg Set COVIDStatus = '" + CovidStatus + "' where ID = " + PatientRegId;

            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            out.println("1");

        } catch (Exception e) {
            out.println("Error: Updating Patient Reg Table " + e.getMessage());
            Services.DumException("PatientUpdateInfo", "AddNotes 2- Updating Patient RegT able :", request, e, this.getServletContext());
            return;
        }


    }


    void AddAdditionalInfo(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int FoundadditionalInfo = 0;
        String AdditionalInfoSelect = request.getParameter("AdditionalInfoSelect").trim();
        String CovidTestDate = request.getParameter("CovidTestDate").trim();
        String CovidTestNo = request.getParameter("CovidTestNo").trim();
        String PatientCatagory = request.getParameter("PatientCatagory").trim();
        String RefName = request.getParameter("RefName").trim();
        String ReasonLeaving = request.getParameter("ReasonLeaving").trim();
        String RefPhysician = request.getParameter("RefPhysician").trim();
        String RefSourceName = request.getParameter("RefSourceName").trim();
        String PatientStatus = request.getParameter("PatientStatus").trim();
        String PatientRegId = request.getParameter("PatientRegId").trim();

        try {

            if (CovidTestDate == "") {
                CovidTestDate = null;
            }

            Query = "Select COUNT(*) from " + Database + ".Patient_AdditionalInfo where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//            System.out.println(Query);
            while (rset.next()) {
                FoundadditionalInfo = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (FoundadditionalInfo > 0) {
                Query = "Update " + Database + ".Patient_AdditionalInfo Set AdditionalInfoSelect = '" + AdditionalInfoSelect + "', CovidTestDate = '" + CovidTestDate + "', " +
                        "PatientCatagory = '" + PatientCatagory + "', RefName = '" + RefName + "', ReasonLeaving = '" + ReasonLeaving + "', RefPhysician = '" + RefPhysician + "', " +
                        " RefSourceName = '" + RefSourceName + "', PatientStatus = '" + PatientStatus + "', CovidTestNo = '" + CovidTestNo + "' where PatientRegId = " + PatientRegId;
//                System.out.println(Query);
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } else {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        " INSERT INTO " + Database + ".Patient_AdditionalInfo (PatientRegId," +
                                "AdditionalInfoSelect,CovidTestDate,PatientCatagory,RefName,ReasonLeaving,RefPhysician," +
                                "RefSourceName,PatientStatus,CreatedDate,CreatedBy, CovidTestNo) \n" +
                                " VALUES (?,?,?,?,?,?,?,?,?,now(),?,?) ");
                MainReceipt.setString(1, PatientRegId);
                MainReceipt.setString(2, AdditionalInfoSelect);
                MainReceipt.setString(3, CovidTestDate);
                MainReceipt.setString(4, PatientCatagory);
                MainReceipt.setString(5, RefName);
                MainReceipt.setString(6, ReasonLeaving);
                MainReceipt.setString(7, RefPhysician);
                MainReceipt.setString(8, RefSourceName);
                MainReceipt.setString(9, PatientStatus);
                MainReceipt.setString(10, UserId);
                MainReceipt.setString(11, CovidTestNo);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }
            out.println("1");

        } catch (Exception e) {
            out.println("Error: Inserting PAtientr Addistional Info Table " + e.getMessage());
            Services.DumException("PatientUpdateInfo", "AddNotes 2- Inserting PAtientr Addistional Info :", request, e, this.getServletContext());
            return;
        }


    }

    void UpdateDOS(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";


        try {
            String DOSN = request.getParameter("DOSN").trim();
            String PatientRegId = request.getParameter("PatientRegId").trim();

            Query = "Update " + Database + ".PatientReg Set DateofService = '" + DOSN + "' where ID = " + PatientRegId;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            out.println("1");

        } catch (Exception e) {
            out.println("Error: Updating DOS, Patient Reg Table " + e.getMessage());
            Services.DumException("PatientUpdateInfo", "Udpate Date of Service Method :", request, e, this.getServletContext());
            return;
        }


    }
}
