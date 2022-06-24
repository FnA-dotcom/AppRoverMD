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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Calendar;

@SuppressWarnings("Duplicates")
public class PatientUpdateInfo extends HttpServlet {

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
        Connection conn = null;
        int UserType = 0;
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
                UserType = Integer.parseInt(session.getAttribute("UserType").toString());

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
                case "GetInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update Patient Information", "Open Patient Screen Upadte Info", FacilityIndex);
                    GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, UserType);
                    break;
                case "AddNotes":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Notes ", "Save Notes", FacilityIndex);
                    AddNotes(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "DeleteNote":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Delete Notes ", "Deleting any Notes", FacilityIndex);
                    DeleteNote(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "UpdateDoctorId":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update Doctor Information ", "Save Doctors Information", FacilityIndex);
                    UpdateDoctorId(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "UpdateCovidStatus":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update COVID Information ", "Save COVID Information", FacilityIndex);
                    UpdateCovidStatus(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "UpdateDOS":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Update COVID Information ", "Save COVID Information", FacilityIndex);
                    UpdateDOS(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "AddAdditionalInfo":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "inserting Additional Info ", "Save Additional Info", FacilityIndex);
                    AddAdditionalInfo(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "AddNotesInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "inserting Additional Info ", "Save Additional Info", FacilityIndex);
                    AddNotesInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "getCovidHistory":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "inserting Additional Info ", "Save Additional Info", FacilityIndex);
                    getCovidHistory(request, conn, out, DatabaseName, context, helper);
                    break;
                default:
                    out.println("Under Development");
                    break;
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientUpdateInfo ** (handleRequest)", context, e, "PatientUpdateInfo", "handleRequest", conn);
            Services.DumException("PatientUpdateInfo", "Handle Request", request, e, getServletContext());
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
                helper.SendEmailWithAttachment("Error in PatientUpdateInfo ** (handleRequest -- SqlException)", context, e, "PatientUpdateInfo", "handleRequest", conn);
                Services.DumException("PatientUpdateInfo", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }

    private void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper, int userType) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query2 = "";
        String PatientName = "";
        String DOB = "";
        String PhNumber = "";
        String CovidStatusVL = "NONE";
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
        String Gender = "";
        int FoundAddInfo = 0;
        int SelfPayChk = 0;
        String WorkerCompPolicyChk = "0";
        String MotorVehicleAccidentChk = "0";
        String HealthInsuranceChk = "0";
        String InsuredStatus = "";
        String COVIDTestDate = "";
        int AdditionalInfoSelect = 0;
        int PatientCatagory = 0;
        int ReasonLeaving = 0;
        int PatientStatus = 0;
        String RefName = "";
        String CovidTestNo = "";
        String VisitNumber = "";
        String Style = "";
        int RefPhysicianName = 0;
        String RefSourceName = "";
        String PrimaryInsurance = "";
        String GroupNo = "";
        String MemId = "";
        String PatientInvoiceMRN = "";
        String VisitId = "";
        int ClaimCountInstitutional = 0;
        int ClaimCountProfessional = 0;
        int patAddInfo = 0;
        String transactionType = "";

        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer NotesList = new StringBuffer();
        StringBuffer AlertList = new StringBuffer();
        StringBuffer AlertListModal = new StringBuffer();
        StringBuffer PatientList = new StringBuffer();
        StringBuffer CovidBuffer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        StringBuffer visitDropDown = new StringBuffer();
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

        String facilityName = helper.getFacilityName(request, conn, servletContext, ClientId);

        int Notescount = 0;
        int Alertscount = 0;

        String priInsName = "";
        String secInsName = "";
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

            Query = "SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'),''), IFNULL(PhNumber,''), " +
                    "IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), " +
                    "CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'NONE' ELSE 'NONE' END, IFNULL(Address,''), " +
                    "IFNULL(City,''), IFNULL(State,''), IFNULL(County,''), IFNULL(ZipCode,''), IFNULL(DoctorsName,''), IFNULL(SelfPayChk,0),CASE WHEN Gender='male' then 'M' else 'F' END " +
                    "FROM " + Database + ".PatientReg where Status = 0 and ID = " + ID;
//            out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientName = rset.getString(1);
                DOB = rset.getString(2);
                PhNumber = rset.getString(3);
                MRN = rset.getString(4);
                ReasonVisit = rset.getString(5);
                DOS = rset.getString(7);
                //COVIDStatus = rset.getString(8);
                Address = rset.getString(9);
                City = rset.getString(10);
                State = rset.getString(11);
                County = rset.getString(12);
                ZipCode = rset.getString(13);
                DoctorsId = rset.getString(14);
                SelfPayChk = rset.getInt(15);
                Gender = rset.getString(16);
            }
            rset.close();
            stmt.close();

            Query = "Select IFNULL(MAX(VisitNumber),0), max(Id) " +
                    "from " + Database + ".PatientVisit where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                VisitNumber = rset.getString(1).trim();
                VisitId = rset.getString(2).trim();
            }
            rset.close();
            stmt.close();

            VisitNumber = "VN-" + MRN + "-" + VisitNumber;

            if (ClientId == 9 || ClientId == 28) {
                Query = "Select IFNULL(WorkersCompPolicyChk,0), IFNULL(MotorVehicleAccidentChk,0)," +
                        "IFNULL(HealthInsuranceChk,0) from " + Database + ".PatientReg_Details " +
                        "where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    WorkerCompPolicyChk = rset.getString(1);
                    MotorVehicleAccidentChk = rset.getString(2);
                    HealthInsuranceChk = rset.getString(3);
                }
                rset.close();
                stmt.close();

                if (!HealthInsuranceChk.equals("0")) {
                    InsuredStatus = "Insured";

                    Query = "SELECT HIPrimaryInsurance,IFNULL(SHISecondaryName,'-') " +
                            " FROM " + Database + ".Patient_HealthInsuranceInfo " +
                            " WHERE  PatientRegId = " + ID;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        priInsName = rset.getString(1).trim();
                        secInsName = rset.getString(2).trim();
                    }
                    rset.close();
                    stmt.close();
//                    Query = "Select IFNULL(HIPrimaryInsurance,'-'), IFNULL(HISubscriberGroupNo,''), IFNULL(HISubscriberPolicyNo,'') " +
//                            " from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = " + ID;
//                    stmt = conn.createStatement();
//                    rset = stmt.executeQuery(Query);
//                    if (rset.next()) {
//                        PrimaryInsurance = rset.getString(1);
//                        GroupNo = rset.getString(2);
//                        MemId = rset.getString(3);
//                    }
//                    rset.close();
//                    stmt.close();
                } else {
                    InsuredStatus = "Self Pay";
                }

            } else {
                if (SelfPayChk == 1) {
                    int PriInsCode = 0;
                    int SecInsCode = 0;
                    int CorpInsCode = 0;
                    InsuredStatus = "Insured";
                    Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0)," +
                            " IFNULL(PriInsuranceName,0),IFNULL(SecondryInsurance,0)," +
                            " IFNULL(CorporateAccountPriIns,0)" +
                            " from " + Database + ".InsuranceInfo where PatientRegId = " + ID;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        WorkerCompPolicyChk = rset.getString(1);
                        MotorVehicleAccidentChk = rset.getString(2);
                        PriInsCode = rset.getInt(3);
                        SecInsCode = rset.getInt(4);
                        CorpInsCode = rset.getInt(5);
                    }
                    rset.close();
                    stmt.close();


                    Query = "SELECT CONCAT(PayerId,' - ',LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') ))) AS InsName " +
                            " FROM " + Database + ".ProfessionalPayers WHERE id = " + PriInsCode;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next())
                        priInsName = rset.getString(1).trim();
                    rset.close();
                    stmt.close();
//                 	System.out.println("In getinput function");
//                    if(ClientId == 25) {
//                    System.out.println("in GetInput");

                    // for other isurances
                    if (PriInsCode == 8605) {
//                        System.out.println("in sanmarcos if ");
                        Query = "SELECT IFNULL(OtherInsuranceName,0) " +
                                " FROM " + Database + ".InsuranceInfo WHERE PriInsuranceName = " + PriInsCode;
//                        System.out.println("in sQuery of Priinsurence name ");

                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next())
                            priInsName = "Others - " + rset.getString(1).trim();
                        rset.close();
                        stmt.close();
//                        System.out.println("in sQuery of Priinsurence name " + Query);
                    }
//                    }
                    // for corporate insiurances 
                    if (PriInsCode == -999) {
//                        System.out.println("in sanmarcos if ");
                        Query = "SELECT IFNULL(Name,0) " +
                                " FROM " + Database + ".CorporateAccountIns WHERE id = " + CorpInsCode;
//                        System.out.println("in sQuery of corporateinsurance name ");

                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next())
                            priInsName = "Corporate - " + rset.getString(1).trim();
                        rset.close();
                        stmt.close();
//                        System.out.println("in sQuery of Corporate insurance name " + Query);
                    }


                    Query = "SELECT CONCAT(PayerId,' - ',LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') ))) AS InsName " +
                            " FROM " + Database + ".ProfessionalPayers WHERE id = " + SecInsCode;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next())
                        secInsName = rset.getString(1).trim();
                    rset.close();
                    stmt.close();
                } else {
                    InsuredStatus = "Self Pay";
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

            Query = "Select Id, CONCAT(IFNULL(DoctorsLastName,''),', ',IFNULL(DoctorsFirstName,'')) " +
                    "from " + Database + ".DoctorsList where Status = 1 order by DoctorsLastName";
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


            Query = " Select IFNULL(MRN,''), IFNULL(Notes,''), IFNULL(DATE_FORMAT(CreatedDate,'%m/%d/%Y %T'),''), IFNULL(CreatedBy,''), Id  " +
                    " from " + Database + ".Notes where PatientRegId = " + ID + " and Status = 0 order by Id desc ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
//                NotesList.append("<tr>\n");
//                NotesList.append("<td align=left>"+rset.getString(2)+"</td>\n");
////                NotesList.append("<td align=left>" + rset.getString(2) + "</td>\n");
////                NotesList.append("<td align=left>" + rset.getString(4) + "</td>\n");
////                NotesList.append("<td align=left>" + rset.getString(3) + "</td>\n");
//                NotesList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteNote("+rset.getInt(5)+")\"></i></td>\n");
//                NotesList.append("</tr>\n");
                NotesList.append("<div class=\"box\" >\n" +
                        "\t\t\t\t  <div class=\"box-header\" style='height: 50%;'>\n" +
                        "\t\t\t\t\t<h5 class=\"box-title\" style='margin-right: 10%;'>" + rset.getString(2) + "" +
                        "<br><sub>Added by <u>" + rset.getString(4) + "</u> at <u>" + rset.getString(3) + "</u></sub></h5>\n" +
                        "\t\t\t\t\t<div class=\"box-controls pull-right\">\n" +

                        "\t\t\t\t\t  <button class=\"btn btn-xs btn-info\" onclick=\"DeleteNote(" + rset.getInt(5) + ")\"><i class=\"fa fa-trash\"></i></button>\n" +
                        "\t\t\t\t\t</div>                \n" +
                        "\t\t\t\t  </div>\t\t\t\t  \n" +
                        "\t\t\t\t</div>");
                Notescount++;
            }
            rset.close();
            stmt.close();


            Query = " Select IFNULL(MRN,''), IFNULL(Alerts,''), IFNULL(DATE_FORMAT(CreatedDate,'%m/%d/%Y %T'),''), IFNULL(CreatedBy,''), Id  " +
                    " from " + Database + ".Alerts where PatientRegId = " + ID + " and Status = 0 order by Id desc ";
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

                AlertListModal.append("<div class=\"box\" >\n" +
                        "\t\t\t\t  <div class=\"box-header\" style='height: 50%;'>\n" +
                        "\t\t\t\t\t<h5 class=\"box-title\" style='margin-right: 10%;'>" + rset.getString(2) + "" +
                        "<br><sub>Added by <u>" + rset.getString(4) + "</u> at <u>" + rset.getString(3) + "</u></sub></h5>\n" +
                        "\t\t\t\t\t<div class=\"box-controls pull-right\">\n" +
                        "\t\t\t\t\t</div>                \n" +
                        "\t\t\t\t  </div>\t\t\t\t  \n" +
                        "\t\t\t\t</div>");
                Alertscount++;
            }
            rset.close();
            stmt.close();

            Query = "Select Count(*) from " + Database + ".Patient_AdditionalInfo " +
                    "where PatientRegId = " + ID + " and VisitId = '" + VisitId + "'";
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
                Query = "Select IFNULL(AdditionalInfoSelect,0), IFNULL(DATE_FORMAT(CovidTestDate,'%Y-%m-%d'),''), " +
                        "IFNULL(PatientCatagory,0), IFNULL(RefName,''), IFNULL(ReasonLeaving,''), " +
                        "IFNULL(RefPhysician,''), IFNULL(RefSourceName,''), IFNULL(PatientStatus,''), " +
                        "IFNULL(CovidTestNo,'')," +
                        "CASE WHEN CovidStatus = '' THEN 'NONE' WHEN CovidStatus = 1 THEN 'POSITIVE' " +
                        "WHEN CovidStatus = 0 THEN 'NEGATIVE'  WHEN CovidStatus = -1 THEN 'NONE' ELSE 'NONE' END  " +
                        "from " + Database + ".Patient_AdditionalInfo where PatientRegId = " + ID + " and " +
                        "VisitId = '" + VisitId + "'";
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
                }
                rset.close();
                stmt.close();

            }
/*            switch (COVIDStatus.toUpperCase()) {
                case "POSITIVE":
                    CovidBuffer.append("<select class=\"form-control \" id=\"covid_status\" name=\"covid_status\"  style=\"color:black\">\n" +
                            "<option value=\"\"> Please Select Any</option>\n" +
                            "<option value=\"1\" selected> Positive</option>\n" +
                            "<option value=\"0\"> Negative</option>\n" +
                            "<option value=\"-1\"> None</option>\n" +
                            "</select>\n");
                    break;
                case "NEGATIVE":
                    CovidBuffer.append("<select class=\"form-control \" id=\"covid_status\" name=\"covid_status\"  style=\"color:black\">\n" +
                            "<option value=\"\"> Please Select Any</option>\n" +
                            "<option value=\"1\" > Positive</option>\n" +
                            "<option value=\"0\" selected> Negative</option>\n" +
                            "<option value=\"-1\"> None</option>\n" +
                            "</select>\n");
                    break;
                case "NONE":
                    CovidBuffer.append("<select class=\"form-control \" id=\"covid_status\" name=\"covid_status\"  style=\"color:black\">\n" +
                            "<option value=\"\"> Please Select Any</option>\n" +
                            "<option value=\"1\" > Positive</option>\n" +
                            "<option value=\"0\" > Negative</option>\n" +
                            "<option value=\"-1\" selected> None</option>\n" +
                            "</select>\n");
                    break;
                default:
                    CovidBuffer.append("<select class=\"form-control \" id=\"covid_status\" name=\"covid_status\"  style=\"color:black\">\n" +
                            "<option value=\"\"> Please Select Any</option>\n" +
                            "<option value=\"1\" > Positive</option>\n" +
                            "<option value=\"0\" > Negative</option>\n" +
                            "<option value=\"-1\"> None</option>\n" +
                            "</select>\n");
                    break;
            }*/
            CovidBuffer.append("<select class=\"form-control \" id=\"covid_status\" name=\"covid_status\"  style=\"color:black\">\n" +
                    "<option value=\"\"> Please Select Any</option>\n" +
                    "<option value=\"1\" > Positive</option>\n" +
                    "<option value=\"0\" > Negative</option>\n" +
                    "<option value=\"-1\"> None</option>\n" +
                    "</select>\n");
            //************************ CURRENTLY ON LIVE *********************

            if (ClientId == 9) {
//                CDRList.append("<thead style=\"color:black;\">\n<tr>\n<th >MRN</th>\n<th >Acct#</th>\n<th >Patient Name</th>\n<th >Date of Birth</th>\n<th >Number</th>\n<th >Reason of Visit</th>\n<th >Date of Service</th>\n<th >COVID Status</th>\n<th >CDC Status</th>\n</tr>\n</thead>\n<tbody style=\"color:black;\">\n");
                CDRList.append("<thead style=\"color:black;\">\n<tr>\n<th >Acct#</th>\n<th >Reason of Visit</th>\n<th >Date of Service</th>\n<th >COVID Status</th>\n<th>CDC Status</th>\n<th>T</th>\n<th>Tracker</th>\n<th >History</th>\n</tr>\n</thead>\n<tbody style=\"color:black;\">\n");
                Query = " Select CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                        "IFNULL(DATE_FORMAT(b.DOB,'%m/%d/%Y'),''), IFNULL(b.PhNumber,''),  IFNULL(a.ReasonVisit,'-'), " + //4
                        "IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')), " + //5
                        "CASE WHEN b.COVIDStatus = '' THEN 'NONE' WHEN b.COVIDStatus = 1 THEN 'POSITIVE' WHEN b.COVIDStatus = 0 THEN 'NEGATIVE'  " +
                        "WHEN b.COVIDStatus = -1 THEN 'NONE' ELSE 'UN-EXAMINED' END , a.MRN, b.Status, a.VisitNumber, a.Id,  " + //10
                        "CASE WHEN a.CDCFlag = 1 THEN 'Send to CDC' WHEN CDCFlag = 0 THEN 'Not Send' ELSE '' END, " + //11
                        "a.MRN, b.Status, a.VisitNumber , " + //14
                        "a.Id as VisitIdx, b.Id as PatRegIdx " + //16
                        "FROM " + Database + ".PatientVisit a  " +
                        " INNER JOIN " + Database + ".PatientReg b on a.PatientRegId = b.Id " +
                        " WHERE b.ID =  " + ID + " order by a.DateofService desc";
//                System.out.println("QUERY " + Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    Query2 = "SELECT COUNT(*),CASE " +
                            "WHEN a.CovidStatus = '' THEN 'NONE' " +
                            "WHEN a.CovidStatus = 1 THEN 'POSITIVE' " +
                            "WHEN a.CovidStatus = 0 THEN 'NEGATIVE'  " +
                            "WHEN a.CovidStatus = -1 THEN 'NONE' " +
                            "ELSE 'NONE' END,IFNULL(b.TransactionType,'') as TransactionType  " +
                            "FROM " + Database + ".Patient_AdditionalInfo a " +
                            " LEFT JOIN " + Database + ".TransactionTypes b ON a.ReasonLeaving = b.Id " +
                            "WHERE a.PatientRegId = " + ID + " AND " +
                            " a.VisitId = " + rset.getInt(10);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        patAddInfo = rset2.getInt(1);
                        CovidStatusVL = rset2.getString(2);
                        transactionType = rset2.getString(3);
                    }
                    rset2.close();
                    stmt2.close();

                    Query2 = "Select COUNT(*) from " + Database + ".ClaimInfoMaster where PatientRegId = " + ID + " and VisitId = " + rset.getInt(10) + " and ClaimType = 1";
//                    System.out.println("ClaimCountInstitutional " + Query2);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        ClaimCountInstitutional = rset2.getInt(1);
                    }
                    rset2.close();
                    stmt2.close();
                    Query2 = "Select COUNT(*) from " + Database + ".ClaimInfoMaster where PatientRegId = " + ID + " and VisitId = " + rset.getInt(10) + " and ClaimType = 2 ";
//                    System.out.println("ClaimCountProfessional " + Query2);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        ClaimCountProfessional = rset2.getInt(1);
                    }
                    rset2.close();
                    stmt2.close();

                    CDRList.append("<tr>");
//                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");//MRN
                    CDRList.append("<td align=left>VN-" + MRN + "-" + rset.getString(9) + "</td>\n");//Acct#
//                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");//PatName
//                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");//DOB
//                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");//Number
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");//ReasonVisit
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");//DOS
                    CDRList.append("<td align=left>" + CovidStatusVL + "</td>\n");//COVID Status
                    CDRList.append("<td align=left>" + rset.getString(11) + "</td>\n");//CDC Flag
                    CDRList.append("<td align=left>" + transactionType + "</td>\n");//Transaction Type
                    CDRList.append("<td><button id=addCovidDetail class=\"btn btn-primary btn-sm\" data-toggle=\"modal\" data-target=\"#modal-left\" onclick=\"covidTracking(this.value)\" value=" + rset.getInt(15) + ">Tracker</button>");

                    CDRList.append("<a class=\"btn fa fa-file-pdf-o pdfIcon mb-2 tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Bundle\" href=/md/md.DownloadBundle?ActionID=" + BundleFnName + "&ID=" + ID + " target=\"_blank\" rel=\"noopener noreferrer\"></a>");

                    if (userType == 12) {
                        CDRList.append("<br><a class='btn-sm btn btn-primary' href=/md/md.InsClaimTesting?ActionID=AddInfo&VisitId=" + rset.getInt(10) + "&PatientRegId=" + ID + "&AcctNo=VN-" + MRN + "-" + rset.getString(9) + "&ClaimType=1>Institutional<span class=\"badge badge-pill badge-danger\">" + ClaimCountInstitutional + "</Span></a>");
                        CDRList.append("<br><a class='btn-sm btn btn-primary' href=/md/md.AddInfo?ActionID=AddinfoProf&VisitId=" + rset.getInt(10) + "&PatientRegId=" + ID + "&AcctNo=VN-" + MRN + "-" + rset.getString(9) + "&ClaimType=2>Professional<span class=\"badge badge-pill badge-danger\">" + ClaimCountProfessional + "</span></a>");
                    }
                    CDRList.append("</td>");
                    if (patAddInfo > 0)
                        CDRList.append("<td><a class='btn-sm btn btn-primary' href=/md/md.PatientUpdateInfo?ActionID=getCovidHistory&patRegIdx=" + rset.getString(16) + "&visitIdx=" + rset.getInt(15) + "> History</a></td>\n");
                    else
                        CDRList.append("<td>No History</td>\n");
                    CDRList.append("</tr>");
                }
                rset.close();
                stmt.close();
                CDRList.append("</tbody>");
            } else {
                //CDRList.append("<thead style=\"color:black;\">\n<tr>\n<th >MRN</th>\n<th >Acct#</th>\n<th >Patient Name</th>\n<th >Date of Birth</th>\n<th >Number</th>\n<th >Reason of Visit</th>\n<th >Date of Service</th>\n<th >COVID Status</th>\n</tr>\n</thead>\n<tbody style=\"color:black;\">\n");
                CDRList.append("<thead style=\"color:black;\">\n<tr>\n<th >Acct#</th>\n<th >Reason of Visit</th>\n<th >Date of Service</th>\n<th>COVID Status</th>\n<th>T Type</th>\n<th>Action</th>\n<th>History</th>\n</tr>\n</thead>\n<tbody style=\"color:black;\">\n");
                Query = " Select CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                        "IFNULL(DATE_FORMAT(b.DOB,'%m/%d/%Y'),''), IFNULL(b.PhNumber,''),  IFNULL(a.ReasonVisit,'-'), " +
                        "IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')), " +
                        "CASE WHEN b.COVIDStatus = '' THEN 'NONE' WHEN b.COVIDStatus = 1 THEN 'POSITIVE' WHEN b.COVIDStatus = 0 THEN 'NEGATIVE'  " +
                        "WHEN b.COVIDStatus = -1 THEN 'NONE' ELSE 'UN-EXAMINED' END , a.MRN, b.Status, a.VisitNumber, " +
                        "a.Id as VisitIdx, b.Id as PatRegIdx " +
                        " FROM " + Database + ".PatientVisit a  " +
                        " INNER JOIN " + Database + ".PatientReg b on a.PatientRegId = b.Id " +
                        " WHERE b.ID =  " + ID + " " +
                        " ORDER BY a.DateofService desc";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    Query2 = "SELECT COUNT(*),CASE " +
                            "WHEN a.CovidStatus = '' THEN 'NONE' " +
                            "WHEN a.CovidStatus = 1 THEN 'POSITIVE' " +
                            "WHEN a.CovidStatus = 0 THEN 'NEGATIVE'  " +
                            "WHEN a.CovidStatus = -1 THEN 'NONE' " +
                            "ELSE 'NONE' END,IFNULL(b.TransactionType,'') as TransactionType  " +
                            "FROM " + Database + ".Patient_AdditionalInfo a " +
                            " LEFT JOIN " + Database + ".TransactionTypes b ON a.ReasonLeaving = b.Id " +
                            "WHERE a.PatientRegId = " + ID + " AND " +
                            " a.VisitId = " + rset.getInt(10);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        patAddInfo = rset2.getInt(1);
                        CovidStatusVL = rset2.getString(2);
                        transactionType = rset2.getString(3);
                    }
                    rset2.close();
                    stmt2.close();

                    Query2 = "Select COUNT(*) from " + Database + ".ClaimInfoMaster where PatientRegId = " + ID + " and VisitId = " + rset.getInt(10) + " and ClaimType = 1";
//                    System.out.println("ClaimCountInstitutional " + Query2);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        ClaimCountInstitutional = rset2.getInt(1);
                    }
                    rset2.close();
                    stmt2.close();
                    Query2 = "Select COUNT(*) from " + Database + ".ClaimInfoMaster where PatientRegId = " + ID + " and VisitId = " + rset.getInt(10) + " and ClaimType = 2 ";
//                    System.out.println("ClaimCountProfessional " + Query2);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        ClaimCountProfessional = rset2.getInt(1);
                    }
                    rset2.close();
                    stmt2.close();

                    CDRList.append("<tr>");
//                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n"); //MRN
                    CDRList.append("<td align=left>VN-" + MRN + "-" + rset.getString(9) + "</td>\n"); //Acct#
//                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");//Patient Name
//                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");//Date of Birth
//                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");//Number
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");//Reason of Visit
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");//DOS
                    CDRList.append("<td align=left>" + CovidStatusVL + "</td>\n");//COVID Status
                    CDRList.append("<td align=left>" + transactionType + "</td>\n");//Transaction Type

                    CDRList.append("<td>");
                    CDRList.append("<button id=addCovidDetail class=\"btn btn-primary btn-sm\" data-toggle=\"modal\" data-target=\"#modal-left\" onclick=\"covidTracking(this.value)\" value=" + rset.getInt(10) + "> Tracker</button>");
                    if (ClientId == 38) {
                        CDRList.append("<a class=\"btn fa fa-file-pdf-o pdfIcon mb-2 tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Bundle\" href=/md/md.DownloadBundle?ActionID=" + BundleFnName + "&ID=" + ID + " target=\"_blank\" rel=\"noopener noreferrer\"></a>");
                    } else if (ClientId == 25) {
                        CDRList.append("<a class=\"btn fa fa-file-pdf-o pdfIcon mb-2 tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Bundle\" href=/md/md.SanMarcosBundle?ActionID=" + BundleFnName + "&ID=" + ID + " target=\"_blank\" rel=\"noopener noreferrer\"></a>");
                    } else if (ClientId == 41 || ClientId == 42 || ClientId == 43) {
                        CDRList.append("<a class=\"btn fa fa-file-pdf-o pdfIcon mb-2 tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Bundle\" href=/md/md.LifeSaversBundle?ActionID=" + BundleFnName + "&ID=" + ID + " target=\"_blank\" rel=\"noopener noreferrer\"></a>");
                    } else if (ClientId == 40) {
                        CDRList.append("<a class=\"btn fa fa-file-pdf-o pdfIcon mb-2 tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Bundle\" href=/md/md.FloresvilleBundle?ActionID=" + BundleFnName + "&ID=" + ID + " target=\"_blank\" rel=\"noopener noreferrer\"></a>");
                    } else if (ClientId == 39) {
                        CDRList.append("<a class=\"btn fa fa-file-pdf-o pdfIcon mb-2 tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Bundle\" href=/md/md.SchertzBundle?ActionID=" + BundleFnName + "&ID=" + ID + " target=\"_blank\" rel=\"noopener noreferrer\"></a>");
                    } else if (ClientId == 27 || ClientId == 29) {
                        CDRList.append("<a class=\"btn fa fa-file-pdf-o pdfIcon mb-2 tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Bundle\" href=/md/md.FrontlineBundle?ActionID=" + BundleFnName + "&ID=" + ID + " target=\"_blank\" rel=\"noopener noreferrer\"></a>");
                    } else {
                        CDRList.append("<a class=\"btn fa fa-file-pdf-o pdfIcon mb-2 tooltip-demo\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Click for Bundle\" href=/md/md.DownloadBundle?ActionID=" + BundleFnName + "&ID=" + ID + " target=\"_blank\" rel=\"noopener noreferrer\"></a>");
                    }

                    if (userType == 12) {
                        CDRList.append("<br><a class='btn-sm btn btn-primary' href=/md/md.InsClaimTesting?ActionID=Addinfo&VisitId=" + rset.getInt(10) + "&PatientRegId=" + ID + "&AcctNo=VN-" + MRN + "-" + rset.getString(9) + "&ClaimType=1>Institutional<span class=\"badge badge-pill badge-danger\">" + ClaimCountInstitutional + "</Span></a>");
                        CDRList.append("<br><a class='btn-sm btn btn-primary' href=/md/md.AddInfo?ActionID=AddinfoProf&VisitId=" + rset.getInt(10) + "&PatientRegId=" + ID + "&AcctNo=VN-" + MRN + "-" + rset.getString(9) + "&ClaimType=2>Professional<span class=\"badge badge-pill badge-danger\">" + ClaimCountProfessional + "</span></a>");
                    }
                    CDRList.append("</td>");

                    if (patAddInfo > 0)
                        CDRList.append("<td><a class='btn-sm btn btn-primary' href=/md/md.PatientUpdateInfo?ActionID=getCovidHistory&patRegIdx=" + rset.getString(11) + "&visitIdx=" + rset.getInt(10) + "> History</a></td>\n");
                    else
                        CDRList.append("<td>No History</td>\n");
                    CDRList.append("</tr>");
                }
                rset.close();
                stmt.close();
                CDRList.append("</tbody>");
            }
            //************************ CURRENTLY ON LIVE *********************

            //***************************** FOR CLAIM *******************************************
//            if (ClientId == 9) {
//                CDRList.append("<thead style=\"color:black;\">\n<tr>\n<th >MRN</th>\n<th >Acct#</th>\n<th >Patient Name</th>\n<th >Date of Birth</th>\n<th >Number</th>\n<th >Reason of Visit</th>\n<th >Date of Service</th>\n<th >COVID Status</th>\n<th >CDC Status</th>\n</tr>\n");
//                if (userType == 10 || userType == 7) {
//                    CDRList.append("<th>Claims</th>");
//                }
//                CDRList.append("</tr></thead>\n<tbody style=\"color:black;\">\n");
//                Query = " Select CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
//                        "IFNULL(DATE_FORMAT(b.DOB,'%m/%d/%Y'),''), IFNULL(b.PhNumber,''),  IFNULL(a.ReasonVisit,'-'), " +
//                        "IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')), " +
//                        "CASE WHEN b.COVIDStatus = '' THEN 'NONE' WHEN b.COVIDStatus = 1 THEN 'POSITIVE' WHEN b.COVIDStatus = 0 THEN 'NEGATIVE'  " +
//                        "WHEN b.COVIDStatus = -1 THEN 'NONE' ELSE 'UN-EXAMINED' END , a.MRN, b.Status, a.VisitNumber, a.Id,  " +
//                        "CASE WHEN a.CDCFlag = 1 THEN 'Send to CDC' WHEN CDCFlag = 0 THEN 'Not Send' ELSE '' END " +
//                        "from " + Database + ".PatientVisit a  " +
//                        "Left Join " + Database + ".PatientReg b on a.PatientRegId = b.Id " +
//                        "where b.ID =  " + ID + " order by a.DateofService desc";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                while (rset.next()) {
//                    ClaimCountInstitutional = 0;
//                    ClaimCountProfessional = 0;
//                    Query2 = "Select CASE WHEN CovidStatus = '' THEN 'NONE' WHEN CovidStatus = 1 THEN 'POSITIVE' " +
//                            "WHEN CovidStatus = 0 THEN 'NEGATIVE'  " +
//                            "WHEN CovidStatus = -1 THEN 'NONE' ELSE 'NONE' END  " +
//                            "from " + Database + ".Patient_AdditionalInfo " +
//                            "where PatientRegId = " + ID + " and VisitId = " + rset.getInt(10);
//                    stmt2 = conn.createStatement();
//                    rset2 = stmt2.executeQuery(Query2);
//                    if (rset2.next()) {
//                        CovidStatusVL = rset2.getString(1);
//                    }
//                    rset2.close();
//                    stmt2.close();
//                    Query2 = "Select COUNT(*) from " + Database + ".ClaimInfoMaster where PatientRegId = " + ID + " and VisitId = " + rset.getInt(10) + " and ClaimType = 1";
////                    System.out.println("ClaimCountInstitutional " + Query2);
//                    stmt2 = conn.createStatement();
//                    rset2 = stmt2.executeQuery(Query2);
//                    if (rset2.next()) {
//                        ClaimCountInstitutional = rset2.getInt(1);
//                    }
//                    rset2.close();
//                    stmt2.close();
//                    Query2 = "Select COUNT(*) from " + Database + ".ClaimInfoMaster where PatientRegId = " + ID + " and VisitId = " + rset.getInt(10) + " and ClaimType = 2 ";
////                    System.out.println("ClaimCountProfessional " + Query2);
//                    stmt2 = conn.createStatement();
//                    rset2 = stmt2.executeQuery(Query2);
//                    if (rset2.next()) {
//                        ClaimCountProfessional = rset2.getInt(1);
//                    }
//                    rset2.close();
//                    stmt2.close();
//                    CDRList.append("<tr>");
//                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
//                    CDRList.append("<td align=left>VN-" + MRN + "-" + rset.getString(9) + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
//                    CDRList.append("<td align=left>" + CovidStatusVL + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(11) + "</td>\n");
//                    if (userType == 10 || userType == 7) {
//                        CDRList.append("<td align=left width=20%>");
////                        CDRList.append("<a class=\"waves-effect waves-light btn btn-info mb-5\" href=/md/md.AddInfo?ActionID=GetClaims&VisitId=" + rset.getInt(10) + "&PatientRegId=" + ID + "&AcctNo=VN-" + MRN + "-" + rset.getString(9) + ">Claim<i class=\"badge badge-pill badge-danger\">" + ClaimCount + "</i></a>");
//                        CDRList.append("<a class=\"'btn-sm btn btn-primary\" href=/md/md.AddInfo?ActionID=Addinfo&VisitId=" + rset.getInt(10) + "&PatientRegId=" + ID + "&AcctNo=VN-" + MRN + "-" + rset.getString(9) + "&ClaimType=1>Institutional<span class=\"badge badge-pill badge-danger\">" + ClaimCountInstitutional + "</Span></a>");
//                        CDRList.append("<a class='btn-sm btn btn-primary' href=/md/md.AddInfo?ActionID=AddinfoProf&VisitId=" + rset.getInt(10) + "&PatientRegId=" + ID + "&AcctNo=VN-" + MRN + "-" + rset.getString(9) + "&ClaimType=2>Professional<span class=\"badge badge-pill badge-danger\">" + ClaimCountProfessional + "</span></a>");
//                        CDRList.append("</td>");
//                    }
//                    CDRList.append("</tr>");
//                }
//                rset.close();
//                stmt.close();
//                CDRList.append("</tbody>");
//            } else {
//                CDRList.append("<thead style=\"color:black;\">\n<tr>\n<th >MRN</th>\n<th >Acct#</th>\n<th >Patient Name</th>\n<th >Date of Birth</th>\n<th >Number</th>\n<th >Reason of Visit</th>\n<th >Date of Service</th>\n<th >COVID Status</th>\n");
//                if (userType == 10 || userType == 7) {
//                    CDRList.append("<th>Claims</th>");
//                }
//                CDRList.append("</tr></thead>\n<tbody style=\"color:black;\">\n");
//                Query = " Select CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
//                        "IFNULL(DATE_FORMAT(b.DOB,'%m/%d/%Y'),''), IFNULL(b.PhNumber,''),  IFNULL(a.ReasonVisit,'-'), " +
//                        "IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')), " +
//                        "CASE WHEN b.COVIDStatus = '' THEN 'NONE' WHEN b.COVIDStatus = 1 THEN 'POSITIVE' WHEN b.COVIDStatus = 0 THEN 'NEGATIVE'  " +
//                        "WHEN b.COVIDStatus = -1 THEN 'NONE' ELSE 'UN-EXAMINED' END , a.MRN, b.Status, a.VisitNumber, a.Id " +
//                        "from " + Database + ".PatientVisit a  " +
//                        "Left Join " + Database + ".PatientReg b on a.PatientRegId = b.Id " +
//                        "where b.ID =  " + ID + " order by a.DateofService desc";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                while (rset.next()) {
//                    ClaimCountInstitutional = 0;
//                    ClaimCountProfessional = 0;
//                    Query2 = "Select CASE WHEN CovidStatus = '' THEN 'NONE' WHEN CovidStatus = 1 THEN 'POSITIVE' WHEN CovidStatus = 0 THEN 'NEGATIVE'  WHEN CovidStatus = -1 THEN 'NONE' ELSE 'NONE' END  from " + Database + ".Patient_AdditionalInfo where PatientRegId = " + ID + " and VisitId = " + rset.getInt(10);
//                    stmt2 = conn.createStatement();
//                    rset2 = stmt2.executeQuery(Query2);
//                    if (rset2.next()) {
//                        CovidStatusVL = rset2.getString(1);
//                    }
//                    rset2.close();
//                    stmt2.close();
//                    Query2 = "Select COUNT(*) from " + Database + ".ClaimInfoMaster where PatientRegId = " + ID + " and VisitId = " + rset.getInt(10) + " and ClaimType = 1 ";
//                    stmt2 = conn.createStatement();
//                    rset2 = stmt2.executeQuery(Query2);
//                    if (rset2.next()) {
//                        ClaimCountInstitutional = rset2.getInt(1);
//                    }
//                    rset2.close();
//                    stmt2.close();
//                    Query2 = "Select COUNT(*) from " + Database + ".ClaimInfoMaster where PatientRegId = " + ID + " and VisitId = " + rset.getInt(10) + " and ClaimType = 2";
//                    stmt2 = conn.createStatement();
//                    rset2 = stmt2.executeQuery(Query2);
//                    if (rset2.next()) {
//                        ClaimCountProfessional = rset2.getInt(1);
//                    }
//                    rset2.close();
//                    stmt2.close();
//                    CDRList.append("<tr>");
//                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
//                    CDRList.append("<td align=left>VN-" + MRN + "-" + rset.getString(9) + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
//                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
//                    CDRList.append("<td align=left>" + CovidStatusVL + "</td>\n");
//                    if (userType == 10 || userType == 7) {
//                        CDRList.append("<td align=left width=20%>");
////                        CDRList.append("<a class=\"waves-effect waves-light btn btn-info mb-5\" href=\"/md/md.AddInfo?ActionID=GetClaims&VisitId=" + rset.getInt(10) + "&PatientRegId=" + ID + "&AcctNo=VN-" + MRN + "-" + rset.getString(9) + "\" target=\"_blank\">Claims<i class=\"badge badge-pill badge-danger\">" + ClaimCount + "</i></a>");
//                        CDRList.append("<a class=\"btn-sm btn btn-primary\" href=\"/md/md.InsClaimTesting?ActionID=Addinfo&VisitId=" + rset.getInt(10) + "&PatientRegId=" + ID + "&AcctNo=VN-" + MRN + "-" + rset.getString(9) + "&ClaimType=1\" target=\"_blank\">Institutional<span class=\"badge badge-pill badge-danger\">" + ClaimCountInstitutional + "</span></a>");
//                        CDRList.append("<a class='btn-sm btn btn-primary' href=\"/md/md.AddInfo?ActionID=AddinfoProf&VisitId=" + rset.getInt(10) + "&PatientRegId=" + ID + "&AcctNo=VN-" + MRN + "-" + rset.getString(9) + "&ClaimType=2\" target=\"_blank\">Professional<span class=\"badge badge-pill badge-danger\">" + ClaimCountProfessional + "</span></a>");
//                        CDRList.append("</td>");
//                    }
//                    CDRList.append("</tr>");
//                }
//                rset.close();
//                stmt.close();
//                CDRList.append("</tbody>");
//            }
            //***************************** FOR CLAIM *******************************************]

            /*Query = " Select CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), IFNULL(DATE_FORMAT(b.DOB,'%m/%d/%Y'),''), IFNULL(b.PhNumber,''), " +
                    " IFNULL(a.ReasonVisit,'-'), IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T'))," +
                    " CASE WHEN b.COVIDStatus = '' THEN 'NONE' WHEN b.COVIDStatus = 1 THEN 'POSITIVE' WHEN b.COVIDStatus = 0 THEN 'NEGATIVE' " +
                    " WHEN b.COVIDStatus = -1 THEN 'NONE' ELSE 'UN-EXAMINED' END , a.MRN, b.Status, a.VisitNumber, a.Id" +
                    " from " + Database + ".PatientVisit a " +
                    " Left Join " + Database + ".PatientReg b on a.PatientRegId = b.Id where b.ID =  " + ID + " order by a.DateofService desc";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Query2 = "Select CASE WHEN CovidStatus = '' THEN 'NONE' WHEN CovidStatus = 1 THEN 'POSITIVE' WHEN CovidStatus = 0 THEN 'NEGATIVE'  WHEN CovidStatus = -1 THEN 'NONE' ELSE 'NONE' END " +
                        " from " + Database + ".Patient_AdditionalInfo where PatientRegId = " + ID + " and VisitId = " + rset.getInt(10);
                stmt2 = conn.createStatement();
                rset2 = stmt2.executeQuery(Query2);
                if (rset2.next()) {
                    CovidStatusVL = rset2.getString(1);
                }
                rset2.close();
                stmt2.close();

                CDRList.append("<tr>");
                //CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                CDRList.append("<td align=left>" + "VN-" + MRN + "-" + rset.getString(9) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
//                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + CovidStatusVL + "</td>\n");
                CDRList.append("</tr>");
                // SNo++;

            }
            rset.close();
            stmt.close();*/


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

            //zur start vistDropdown
            Query = "Select id,CONCAT(ReasonVisit,' | ',DateofService)as visit from " + Database + ".PatientVisit where mrn=" + MRN;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            //  visitDropDown.append("<option value=\"\" selected> </option>");
            while (rset.next()) {
                visitDropDown.append("<option value=\"" + rset.getInt(1) + "\" selected>" + rset.getString(2) + " </option>");

            }
            rset.close();
            stmt.close();
            //zur end visit Dropdown

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

/*
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
*/
            PatientCatagoryBuffer.append("<select class=\"form-control\" id=\"PatientCatagory\" name=\"PatientCatagory\" >\n" +
                    "<option value=\"\" selected>Select Any</option> \n" +
                    "<option value=\"1\" >VIP </option> \n" +
                    "<option value=\"2\" >SVIP </option>\n" +
                    "</select>\n");

/*            if (ReasonLeaving == 1) {
                ReasonLeavingBuffer.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" >\n" +
                        "<option value=\"\">Select Any</option> \n" +
                        "<option value=\"1\" selected>MSE </option> \n" +
                        "<option value=\"2\">AMA </option>\n" +
                        "<option value=\"3\">LWBS </option>\n" +
                        "<option value=\"4\">Eloped </option>\n" +
                        "</select>\n");
            } else if (ReasonLeaving == 2) {
                ReasonLeavingBuffer.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" >\n" +
                        "<option value=\"\">Select Any</option> \n" +
                        "<option value=\"1\" >MSE </option> \n" +
                        "<option value=\"2\"selected >AMA </option>\n" +
                        "<option value=\"3\">LWBS </option>\n" +
                        "<option value=\"4\">Eloped </option>\n" +
                        "</select>\n");
            } else if (ReasonLeaving == 3) {
                ReasonLeavingBuffer.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" >\n" +
                        "<option value=\"\">Select Any</option> \n" +
                        "<option value=\"1\" >MSE </option> \n" +
                        "<option value=\"2\" >AMA </option>\n" +
                        "<option value=\"3\" selected>LWBS </option>\n" +
                        "<option value=\"4\" >Eloped </option>\n" +
                        "</select>\n");
            } else if (ReasonLeaving == 4) {
                ReasonLeavingBuffer.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" >\n" +
                        "<option value=\"\">Select Any</option> \n" +
                        "<option value=\"1\" >MSE </option> \n" +
                        "<option value=\"2\" >AMA </option>\n" +
                        "<option value=\"3\" >LWBS </option>\n" +
                        "<option value=\"4\" selected>Eloped </option>\n" +
                        "</select>\n");
            } else {
                ReasonLeavingBuffer.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" >\n" +
                        "<option value=\"\" selected>Select Any</option> \n" +
                        "<option value=\"1\" >MSE </option> \n" +
                        "<option value=\"2\" >AMA </option>\n" +
                        "<option value=\"3\" >LWBS </option>\n" +
                        "<option value=\"4\" >Eloped </option>\n" +
                        "</select>\n");
            }*/

            Query = "Select Id, TransactionType from " + Database + ".TransactionTypes";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ReasonLeavingBuffer.append("<option value=\"\" selected>Select Any </option>");
            while (rset.next()) {
                ReasonLeavingBuffer.append("<option value=\"" + rset.getInt(1) + "\">" + rset.getString(2) + " </option>");
            }
            rset.close();
            stmt.close();


/*            if (PatientStatus == 1) {
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
            }*/
            PatientStatusBuffer.append("<select class=\"form-control\" id=\"PatientStatus\" name=\"PatientStatus\" >\n" +
                    "<option value=\"\" selected>Select Any </option>\n" +
                    "<option value=\"2\" >OBS</option>\n" +
                    "<option value=\"1\" >Transferred</option>\n" +
                    "<option value=\"3\" >OBS/Transferred</option>\n" +
                    "</select>\n");

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
            Parser.SetField("Gender", String.valueOf(Gender));
            Parser.SetField("DOB", String.valueOf(DOB));
            Parser.SetField("PhNumber", String.valueOf(PhNumber));
            Parser.SetField("MRN", String.valueOf(MRN));
            Parser.SetField("VisitNumber", String.valueOf(VisitNumber));
            Parser.SetField("ReasonVisit", String.valueOf(ReasonVisit));
            Parser.SetField("DOS", String.valueOf(DOS));
            Parser.SetField("COVIDStatus", String.valueOf(COVIDStatus));
            Parser.SetField("COVIDStatus", String.valueOf(COVIDStatus));
            Parser.SetField("WorkerCompPolicyChk", String.valueOf(WorkerCompPolicyChk));
            Parser.SetField("MotorVehicleAccidentChk", String.valueOf(MotorVehicleAccidentChk));
            Parser.SetField("InsuredStatus", String.valueOf(InsuredStatus));
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
            Parser.SetField("VisitId", String.valueOf(VisitId));
            Parser.SetField("Style", String.valueOf(Style));

            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("RefPhysicianNameBuffer", String.valueOf(RefPhysicianNameBuffer));
            Parser.SetField("PatientStatusBuffer", String.valueOf(PatientStatusBuffer));
            Parser.SetField("PatientCatagoryBuffer", String.valueOf(PatientCatagoryBuffer));
            Parser.SetField("ReasonLeavingBuffer", String.valueOf(ReasonLeavingBuffer));
            //zzzz
            Parser.SetField("visitDropDown", String.valueOf(visitDropDown));
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
            Parser.SetField("Notescount", String.valueOf(Notescount));

            Parser.SetField("AlertList", String.valueOf(AlertList));
            Parser.SetField("AlertListModal", String.valueOf(AlertListModal));
            Parser.SetField("Alertscount", String.valueOf(Alertscount));

            //3-FEB-2022
            Parser.SetField("PrimaryInsuranceName", priInsName);
            Parser.SetField("SecondaryInsuranceName", secInsName);

            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("PatientInvoiceMRN", String.valueOf(PatientInvoiceMRN));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/PatientUpdateInfo.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientUpdateInfo ** (GetInput^^ " + facilityName + " ^^ Patient Reg Idx --> " + ID + ")", servletContext, e, "PatientUpdateInfo", "GetInput", conn);
            Services.DumException("PatientUpdateInfo", "GetInput", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
/*        finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                helper.SendEmailWithAttachment("Error in PatientUpdateInfo ** (handleRequest -- SqlException)", servletContext, e, "PatientUpdateInfo", "handleRequest", conn);
                Services.DumException("PatientUpdateInfo", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }*/
    }

    private void AddNotesInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String PatientInvoiceMRN = "";
        int SNo = 1;
        String PatientId = request.getParameter("PatientId").trim();
        StringBuilder PatientList = new StringBuilder();
        StringBuilder NotesList = new StringBuilder();
        try {
            Query = "SELECT ID, CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), MRN, DATE_FORMAT(DOB,'%m/%d/%Y') FROM " + Database + ".PatientReg where Status = 0 and ID = " + PatientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientInvoiceMRN = rset.getString(3);
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ") (" + rset.getString(4) + ")</option>");
            }
            rset.close();
            stmt.close();

            Query = " Select IFNULL(MRN,''), IFNULL(Notes,''), IFNULL(DATE_FORMAT(CreatedDate,'%m/%d/%Y %T'),''), IFNULL(CreatedBy,''), Id  " +
                    " from " + Database + ".Notes where PatientRegId = " + PatientId + " and Status = 0 order by Id desc ";
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

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("NotesList", String.valueOf(NotesList));
            Parser.SetField("PatientInvoiceMRN", String.valueOf(PatientInvoiceMRN));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/GetInputNotes_copy.html");

        } catch (Exception e) {
            Services.DumException("PatientUpdateInfo", "AddnotesInput :", request, e, this.getServletContext());
            return;
        }


    }

    private void AddNotes(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {

        String Query = "";
        Statement stmt = null;
        ResultSet rset = null;
        StringBuilder NotesList = new StringBuilder();
        try {
            String Notes = request.getParameter("Notes").trim();
            String MRN = request.getParameter("MRN").trim();
            String PatientId = request.getParameter("PatientId").trim();
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

            Query = " Select IFNULL(MRN,''), IFNULL(Notes,''), IFNULL(DATE_FORMAT(CreatedDate,'%m/%d/%Y %T'),''), IFNULL(CreatedBy,''), Id  " +
                    " from " + Database + ".Notes where PatientRegId = " + PatientId + " and Status = 0 order by Id desc ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
/*            NotesList.append("\n" +
                    "<div class=\"table-responsive table-wrapper-scroll-y my-custom-scrollbar\">\n" +
                    "<table id=\"complex_header\" class=\"table table-striped table-bordered display\" style=\"width:100%;\">\t\t\t\t\t\t\t\n" +
                    "<thead style=\"color:black;\">\n" +
                    "<tr>\n" +
                    "<th >Notes</th>\n" +
                    "<th ></th>\n" +
                    "</tr>\n" +
                    "</thead>\n" +
                    "<tbody style=\"color:black;\"> \n");*/
            while (rset.next()) {
                NotesList.append("<div class=\"box\" >\n" +
                        "\t\t\t\t  <div class=\"box-header\" style='height: 50%;'>\n" +
                        "\t\t\t\t\t<h5 class=\"box-title\" style='margin-right: 10%;'>" + rset.getString(2) + "" +
                        "<br><sub>Added by <u>" + rset.getString(4) + "</u> at <u>" + rset.getString(3) + "</u></sub></h5>\n" +
                        "\t\t\t\t\t<div class=\"box-controls pull-right\">\n" +
                        "\t\t\t\t\t  <button class=\"btn btn-xs btn-info\" onclick=\"DeleteNote(" + rset.getInt(5) + ")\"><i class=\"fa fa-trash\"></i></button>\n" +
                        "\t\t\t\t\t</div>                \n" +
                        "\t\t\t\t  </div>\t\t\t\t  \n" +
                        "\t\t\t\t</div>");
            }
//            NotesList.append("</tbody></table></div>");
            rset.close();
            stmt.close();

            out.println("1|" + NotesList.toString());
            /*Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", String.valueOf("Notes Successfully Added"));
            Parser.SetField("FormName", String.valueOf("PatientUpdateInfo"));
            Parser.SetField("ActionID", String.valueOf("GetInput&ID="+PatientId));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Success.html");*/

        } catch (Exception e) {
            out.println("Error: Insertion Notes Table " + e.getMessage());
            Services.DumException("PatientUpdateInfo", "AddNotes 2- Insertion Notes Table :", request, e, this.getServletContext());
            return;
        }


    }

    private void DeleteNote(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
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

    private void UpdateDoctorId(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int MaxVisitNumber = 0;
        String DoctorsName = request.getParameter("DoctorsName").trim();
        String PatientRegId = request.getParameter("PatientRegId").trim();

        try {
            Query = "Update " + Database + ".PatientReg Set DoctorsName = '" + DoctorsName + "' where ID = " + PatientRegId;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            try {
                Query = "Select max(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    MaxVisitNumber = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error in getting VisitNumber From PatientVisit" + e.getMessage());
            }

            try {
                Query = "UPDATE " + Database + ".PatientVisit SET DoctorId ='" + DoctorsName + "' WHERE PatientRegId = " + PatientRegId + " and VisitNumber = " + MaxVisitNumber;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error in Updating PatientVisit Table:-" + e.getMessage());
            }

            out.println("1");

        } catch (Exception e) {
            out.println("Error: Updating Patient Reg Table " + e.getMessage());
            Services.DumException("PatientUpdateInfo", "AddNotes 2- Updating Patient RegT able :", request, e, this.getServletContext());
            return;
        }


    }

    private void UpdateCovidStatus(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
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

    private void AddAdditionalInfoOLD(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
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
        String COVIDStatus = request.getParameter("COVIDStatus").trim();
        String VisitId = request.getParameter("VisitId").trim();

        try {
            if (CovidTestDate == "") {
                CovidTestDate = "0000-00-00";
            }
            if (COVIDStatus == "") {
                COVIDStatus = "";
            }
            if (ReasonLeaving == "") {
                ReasonLeaving = "0";
            }
            if (PatientCatagory == "") {
                PatientCatagory = "0";
            }
            if (RefPhysician == "") {
                RefPhysician = "0";
            }
            if (PatientStatus == "") {
                PatientStatus = "0";
            }

            Query = "Update " + Database + ".PatientReg Set COVIDStatus = '" + COVIDStatus + "' " +
                    "where ID = " + PatientRegId;

            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
            //zzzz
            Query = "Select COUNT(*) from " + Database + ".Patient_AdditionalInfo " +
                    "where PatientRegId = " + PatientRegId + " and VisitId = '" + VisitId + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundadditionalInfo = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (FoundadditionalInfo > 0) {
                Query = "Update " + Database + ".Patient_AdditionalInfo Set AdditionalInfoSelect = '" + AdditionalInfoSelect + "', CovidTestDate = '" + CovidTestDate + "', " +
                        "PatientCatagory = '" + PatientCatagory + "', RefName = '" + RefName + "', ReasonLeaving = '" + ReasonLeaving + "', RefPhysician = '" + RefPhysician + "', " +
                        " RefSourceName = '" + RefSourceName + "', PatientStatus = '" + PatientStatus + "', CovidTestNo = '" + CovidTestNo + "', CovidStatus = '" + COVIDStatus + "' " +
                        " where PatientRegId = " + PatientRegId + " and VisitId = '" + VisitId + "'";
//                System.out.println(Query);
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

            } else {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        " INSERT INTO " + Database + ".Patient_AdditionalInfo (PatientRegId," +
                                "AdditionalInfoSelect,CovidTestDate,PatientCatagory,RefName,ReasonLeaving,RefPhysician," +
                                "RefSourceName,PatientStatus,CreatedDate,CreatedBy, CovidTestNo, CovidStatus, VisitId) \n" +
                                " VALUES (?,?,?,?,?,?,?,?,?,now(),?,?,?,?) ");
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
                MainReceipt.setString(12, COVIDStatus);
                MainReceipt.setString(13, VisitId);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }
            out.println("1");

        } catch (Exception e) {
            //out.println("Error: Inserting Patient Additional Info Table " + e.getMessage());
            Services.DumException("PatientUpdateInfo", "AddNotes 2- Inserting Patient Additional Info :", request, e, this.getServletContext());
            helper.SendEmailWithAttachment("Error in PatientUpdateInfo ** (AddAdditionalInfo)", servletContext, e, "PatientUpdateInfo", "AddAdditionalInfo", conn);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

        }


    }

    private void AddAdditionalInfo(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
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
        String COVIDStatus = request.getParameter("COVIDStatus").trim();
        String VisitId = request.getParameter("VisitId").trim();

        try {


            if (CovidTestDate == "") {
                CovidTestDate = "0000-00-00";
            }
            if (COVIDStatus == "") {
                COVIDStatus = "";
            }
            if (ReasonLeaving == "") {
                ReasonLeaving = "0";
            }
            if (PatientCatagory == "") {
                PatientCatagory = "0";
            }
            if (RefPhysician == "") {
                RefPhysician = "0";
            }
            if (PatientStatus == "") {
                PatientStatus = "0";
            }

            Query = "Update " + Database + ".PatientReg Set COVIDStatus = '" + COVIDStatus + "' " +
                    "where ID = " + PatientRegId;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
            //zzzz
            int patientAdditionalIdx = 0;
            Query = "Select COUNT(*),Id from " + Database + ".Patient_AdditionalInfo " +
                    "where PatientRegId = " + PatientRegId + " and " +
                    "VisitId = '" + VisitId + "'";
//            System.out.println("SELECT Patient_AdditionalInfo --- " + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundadditionalInfo = rset.getInt(1);
                patientAdditionalIdx = rset.getInt(2);
            }
            rset.close();
            stmt.close();
//            System.out.println("FoundadditionalInfo " + FoundadditionalInfo);
            if (FoundadditionalInfo > 0) {

                Query = "SELECT PatientRegId,AdditionalInfoSelect,CovidTestDate,PatientCatagory,RefName," +
                        "ReasonLeaving,RefPhysician,RefSourceName,PatientStatus,CreatedDate,CreatedBy, " +
                        "CovidTestNo, CovidStatus, VisitId,Id " +
                        " FROM " + Database + ".Patient_AdditionalInfo" +
                        " WHERE PatientRegId = " + PatientRegId + " and VisitId = '" + VisitId + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            " INSERT INTO " + Database + ".Patient_AdditionalInfoHistory (PatientRegId," +
                                    "AdditionalInfoSelect,CovidTestDate,PatientCatagory,RefName,ReasonLeaving,RefPhysician," +
                                    "RefSourceName,PatientStatus,CreatedDate,CreatedBy, CovidTestNo, CovidStatus, VisitId,MainIdx) \n" +
                                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                    MainReceipt.setString(1, rset.getString("PatientRegId"));
                    MainReceipt.setString(2, rset.getString("AdditionalInfoSelect"));
                    MainReceipt.setString(3, rset.getString("CovidTestDate"));
                    MainReceipt.setString(4, rset.getString("PatientCatagory"));
                    MainReceipt.setString(5, rset.getString("RefName"));
                    MainReceipt.setString(6, rset.getString("ReasonLeaving"));
                    MainReceipt.setString(7, rset.getString("RefPhysician"));
                    MainReceipt.setString(8, rset.getString("RefSourceName"));
                    MainReceipt.setString(9, rset.getString("PatientStatus"));
                    MainReceipt.setString(10, rset.getString("CreatedDate"));
                    MainReceipt.setString(11, rset.getString("CreatedBy"));
                    MainReceipt.setString(12, rset.getString("CovidTestNo"));
                    MainReceipt.setString(13, rset.getString("CovidStatus"));
                    MainReceipt.setString(14, rset.getString("VisitId"));
                    MainReceipt.setInt(15, patientAdditionalIdx);
                    MainReceipt.executeUpdate();
//                    System.out.println("AAA --> " + MainReceipt.toString());
                    MainReceipt.close();
                }
                rset.close();
                stmt.close();


                Query = "Update " + Database + ".Patient_AdditionalInfo Set AdditionalInfoSelect = '" + AdditionalInfoSelect + "', CovidTestDate = '" + CovidTestDate + "', " +
                        "PatientCatagory = '" + PatientCatagory + "', RefName = '" + RefName + "', ReasonLeaving = '" + ReasonLeaving + "', RefPhysician = '" + RefPhysician + "', " +
                        " RefSourceName = '" + RefSourceName + "', PatientStatus = '" + PatientStatus + "', CovidTestNo = '" + CovidTestNo + "', CovidStatus = '" + COVIDStatus + "' " +
                        " where PatientRegId = " + PatientRegId + " and VisitId = '" + VisitId + "'";
//                System.out.println("UPDATE Patient_AdditionalInfo --- " + Query);
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

            } else {

                PreparedStatement MainReceipt = conn.prepareStatement(
                        " INSERT INTO " + Database + ".Patient_AdditionalInfo (PatientRegId," +
                                "AdditionalInfoSelect,CovidTestDate,PatientCatagory,RefName,ReasonLeaving,RefPhysician," +
                                "RefSourceName,PatientStatus,CreatedDate,CreatedBy, CovidTestNo, CovidStatus, VisitId) \n" +
                                " VALUES (?,?,?,?,?,?,?,?,?,now(),?,?,?,?) ");
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
                MainReceipt.setString(12, COVIDStatus);
                MainReceipt.setString(13, VisitId);
                MainReceipt.executeUpdate();
//                System.out.println("INSERTION  --- " + MainReceipt.toString());
                MainReceipt.close();
            }
            out.println("1");

        } catch (Exception e) {
            //out.println("Error: Inserting Patient Additional Info Table " + e.getMessage());
            Services.DumException("PatientUpdateInfo", "AddNotes 2- Inserting Patient Additional Info :", request, e, this.getServletContext());
            helper.SendEmailWithAttachment("Error in PatientUpdateInfo ** (AddAdditionalInfo)", servletContext, e, "PatientUpdateInfo", "AddAdditionalInfo", conn);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

        }


    }

    private void UpdateDOS(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int MaxVisitNumber = 0;


        try {
            String DOSN = request.getParameter("DOSN").trim();
            String PatientRegId = request.getParameter("PatientRegId").trim();

            Query = "Update " + Database + ".PatientReg Set DateofService = '" + DOSN + "'," +
                    "EditBy='" + UserId + "', Edittime = NOW() where ID = " + PatientRegId;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            try {
                Query = "Select max(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    MaxVisitNumber = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error in getting VisitNumber From PatientVisit" + e.getMessage());
            }

            try {
                Query = "UPDATE " + Database + ".PatientVisit SET DateofService ='" + DOSN + "' WHERE PatientRegId = " + PatientRegId + " and VisitNumber = " + MaxVisitNumber;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error in Updating PatientVisit Table:-" + e.getMessage());
            }


            out.println("1");

        } catch (Exception e) {
            out.println("Error: Updating DOS, Patient Reg Table " + e.getMessage());
            Services.DumException("PatientUpdateInfo", "Udpate Date of Service Method :", request, e, this.getServletContext());
            return;
        }


    }

    private void getCovidHistory(HttpServletRequest request, Connection conn, PrintWriter out, String dbName, ServletContext servletContext, UtilityHelper helper) throws FileNotFoundException {
        ResultSet rset = null;
        String Query = "";
        Statement stmt = null;
        int patRegIdx = Integer.parseInt(request.getParameter("patRegIdx").trim());
        int visitIdx = Integer.parseInt(request.getParameter("visitIdx").trim());

        StringBuilder covidHistory = new StringBuilder();
        try {


            Query = "SELECT CASE " +
                    "WHEN a.CovidStatus = '' THEN 'NONE' " +
                    "WHEN a.CovidStatus = 1 THEN 'POSITIVE' " +
                    "WHEN a.CovidStatus = 0 THEN 'NEGATIVE'  " +
                    "WHEN a.CovidStatus = -1 THEN 'NONE' " +
                    "ELSE 'NONE' END, b.MRN," +
                    "CONCAT(IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,''))," +
                    "a.CovidTestNo," +
                    "CASE " +
                    "WHEN a.ReasonLeaving = 1 THEN 'MSE'" +
                    "WHEN a.ReasonLeaving = 2 THEN 'AMA'" +
                    "WHEN a.ReasonLeaving = 3 THEN 'LWBS'" +
                    "WHEN a.ReasonLeaving = 4 THEN 'LWBS'" +
                    "ELSE 'NONE' END," +
                    "a.RefSourceName,DATE_FORMAT(a.CovidTestDate,'%m-%d-%Y')," +
                    "c.VisitNumber   " +
                    "FROM " + dbName + ".Patient_AdditionalInfo a " +
                    " INNER JOIN " + dbName + ".PatientReg b ON a.PatientRegId = b.Id " +
                    " INNER JOIN " + dbName + ".PatientVisit c ON c.Id = a.VisitId " +
                    " WHERE a.PatientRegId = " + patRegIdx + " AND a.VisitId = " + visitIdx;
//            System.out.println("QQ" + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                covidHistory.append("<tr>\n");
                covidHistory.append("<td align=left>" + rset.getString(2) + "</td>\n");//MRN
                covidHistory.append("<td align=left>" + rset.getString(3) + "</td>\n");//PatName
                covidHistory.append("<td align=left>VN-" + rset.getString(2) + "-" + rset.getString(8) + "</td>\n");//VisitNum
                covidHistory.append("<td align=left>" + rset.getString(1) + "</td>\n");//COVIDStatus
                covidHistory.append("<td align=left>" + rset.getString(7) + "</td>\n");//COVIDDate
                covidHistory.append("<td align=left>" + rset.getString(4) + "</td>\n");//CovidTestNo
//                covidHistory.append("<td align=left>" + rset.getString(5) + "</td>\n");//ReasonLeaving
//                covidHistory.append("<td align=left>" + rset.getString(6) + "</td>\n");//RefSourceName
                covidHistory.append("</tr>\n");
            }
            rset.close();
            stmt.close();

            Query = "SELECT CASE " +
                    "WHEN a.CovidStatus = '' THEN 'NONE' " +
                    "WHEN a.CovidStatus = 1 THEN 'POSITIVE' " +
                    "WHEN a.CovidStatus = 0 THEN 'NEGATIVE'  " +
                    "WHEN a.CovidStatus = -1 THEN 'NONE' " +
                    "ELSE 'NONE' END, b.MRN," +
                    "CONCAT(IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,''))," +
                    "a.CovidTestNo," +
                    "CASE " +
                    "WHEN a.ReasonLeaving = 1 THEN 'MSE'" +
                    "WHEN a.ReasonLeaving = 2 THEN 'AMA'" +
                    "WHEN a.ReasonLeaving = 3 THEN 'LWBS'" +
                    "WHEN a.ReasonLeaving = 4 THEN 'LWBS'" +
                    "ELSE 'NONE' END," +
                    "a.RefSourceName,DATE_FORMAT(a.CovidTestDate,'%m-%d-%Y'),  " +
                    "c.VisitNumber   " +
                    "FROM " + dbName + ".Patient_AdditionalInfoHistory a " +
                    " INNER JOIN " + dbName + ".PatientReg b ON a.PatientRegId = b.Id " +
                    " INNER JOIN " + dbName + ".PatientVisit c ON c.Id = a.VisitId " +
                    " WHERE a.PatientRegId = " + patRegIdx + " AND a.VisitId = " + visitIdx;
//            System.out.println("QQ" + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                covidHistory.append("<tr>\n");
                covidHistory.append("<td align=left>" + rset.getString(2) + "</td>\n");//MRN
                covidHistory.append("<td align=left>" + rset.getString(3) + "</td>\n");//PatName
                covidHistory.append("<td align=left>VN-" + rset.getString(2) + "-" + rset.getString(8) + "</td>\n");//VisitNum
                covidHistory.append("<td align=left>" + rset.getString(1) + "</td>\n");//COVIDStatus
                covidHistory.append("<td align=left>" + rset.getString(7) + "</td>\n");//COVIDDate
                covidHistory.append("<td align=left>" + rset.getString(4) + "</td>\n");//CovidTestNo
//                covidHistory.append("<td align=left>" + rset.getString(5) + "</td>\n");//ReasonLeaving
//                covidHistory.append("<td align=left>" + rset.getString(6) + "</td>\n");//RefSourceName
                covidHistory.append("</tr>\n");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("covidHistory", String.valueOf(covidHistory));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/covidHistoryReport.html");

        } catch (Exception Ex) {
            Services.DumException("PatientUpdateInfo", "getCovidHistory - Show COVID History :", request, Ex, this.getServletContext());
            helper.SendEmailWithAttachment("Error in PatientUpdateInfo ** (AddAdditionalInfo)", servletContext, Ex, "PatientUpdateInfo", "AddAdditionalInfo", conn);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }
}
