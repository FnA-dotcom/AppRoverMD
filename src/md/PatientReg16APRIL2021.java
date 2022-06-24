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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Calendar;

@SuppressWarnings("Duplicates")
public class PatientReg16APRIL2021 extends HttpServlet {
    CallableStatement cStmt = null;
    ResultSet rset = null;
    String Query = "";
    Statement stmt = null;
    PreparedStatement pStmt = null;
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

    public void handleRequest_OLd(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        String UserId = "";
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        String Database = "";
        String Query = "";
        ResultSet rset = null;
        Statement stmt = null;
        int ClientId = 0;
        UserId = "";
        UserId = Services.GetCookie("UserId", request);
        UtilityHelper helper = new UtilityHelper();
        try {

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
        } catch (Exception ee) {
            out.println(ee.getMessage());
        }

        context = this.getServletContext();
        if (ActionID.equals("GetValues")) {
            this.GetValues(request, out, conn, context, Database, helper);
        } else if (ActionID.equals("SaveData")) {
            this.SaveData(request, out, conn, context, Database, UserId);
        } else if (ActionID.equals("EditValues_New")) {
            UserId = Services.GetCookie("UserId", request);
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Edit Values New from Patient Reg", "Click on View Edit Option from View Patients Option ", ClientId);
            this.EditValues_New(request, out, conn, context, Database, UserId);
        } else if (ActionID.equals("EditSave_New")) {
            UserId = Services.GetCookie("UserId", request);
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Edited Values NEW", "Save Edited Values NEWfor Patient ", ClientId);
            this.EditSave_New(request, out, conn, context, Database, UserId);
        } else if (ActionID.equals("CheckPatient")) {
            UserId = Services.GetCookie("UserId", request);
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Check Duplicate Patients", "Check if the Patient Exist ", ClientId);
            this.CheckPatient(request, out, conn, context, Database);
        } else if (ActionID.equals("ReasonVisits")) {
            UserId = Services.GetCookie("UserId", request);
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get ReasonVisit For Frontline", "Reason VisitS frontLine ", ClientId);
            this.ReasonVisits(request, out, conn, context, Database);
        }
        try {
            conn.close();


        } catch (Exception ex) {
        }
        out.flush();
        out.close();
    }

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String ActionID = "";

        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
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
            ActionID = request.getParameter("ActionID");

            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "GetValues":
                    this.GetValues(request, out, conn, context, DatabaseName, helper);
                    break;
                case "SaveData":
                    this.SaveData(request, out, conn, context, DatabaseName, UserId);
                    break;
                case "EditValues_New":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Edit Values New from Patient Reg", "Click on View Edit Option from View Patients Option ", FacilityIndex);
                    this.EditValues_New(request, out, conn, context, DatabaseName, UserId);
                    break;
                case "EditSave_New":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Edited Values NEW", "Save Edited Values NEWfor Patient ", FacilityIndex);
                    this.EditSave_New(request, out, conn, context, DatabaseName, UserId);
                    break;
                case "CheckPatient":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Check Duplicate Patients", "Check if the Patient Exist ", FacilityIndex);
                    this.CheckPatient(request, out, conn, context, DatabaseName);
                    break;
                case "ReasonVisits":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get ReasonVisit For Frontline", "Reason VisitS frontLine ", FacilityIndex);
                    this.ReasonVisits(request, out, conn, context, DatabaseName);
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

    void GetValues(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String Database, UtilityHelper helper) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Date = "";
            String PRF_name = "";
            StringBuffer Month = new StringBuffer();
            StringBuffer Day = new StringBuffer();
            StringBuffer Year = new StringBuffer();
            StringBuffer ProfessionalPayersList = new StringBuffer();
            int ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());
            Query = "Select Date_format(now(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Query = "Select PRF_name from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PRF_name = rset.getString(1);
            }
            rset.close();
            stmt.close();
            //Select Id, PayerId, LTRIM(rtrim(PayerName)) from oe_2.ProfessionalPayers where id  in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955) group by PayerId
            // Select Id, PayerId, LTRIM(rtrim(PayerName)) from ProfessionalPayers where PayerName like  '%Texas%'  or PayerName like   '%ALL%' group by PayerId

            //Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers where id in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955) group by PayerId";//where PayerName like '%Texas%'";
            Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from " + Database + ".ProfessionalPayers where id  in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955,64,1545,3646,8589,200,201,202,203,204,205,206,207,3649,5978,8206,4763,3465,3466,3467,3468,41,387,388,389,697,698,4757) AND Status != 100 group by PayerId";//where PayerName like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ProfessionalPayersList.append("<option value=''>Select Insurance</option>");
            while (rset.next()) {
                ProfessionalPayersList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();

            //Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers where id not in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955) group by PayerId ";//where PayerName not like '%Texas%'";
            Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from " + Database + ".ProfessionalPayers where PayerName like  '%Texas%' OR PayerName like '%TX%' AND Status != 100";//where PayerName not like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ProfessionalPayersList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();


            String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            int day = 1;
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

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Date", String.valueOf(Date));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("ClientIndex_logo", String.valueOf(ClientIndex));
            Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
            Parser.SetField("ProfessionalPayersList2", String.valueOf(ProfessionalPayersList));
            Parser.SetField("Month", String.valueOf(Month));
            Parser.SetField("Day", String.valueOf(Day));
            Parser.SetField("Year", String.valueOf(Year));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/PRF_files/" + PRF_name);
        } catch (Exception ex) {
            out.println(ex.getMessage());
        }
    }

    void SaveData(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String Database, String UserId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        int AddmissionBundle = 0;
        int ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());
        String Title = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String MaritalStatus = "";
        String DOB = "";
        String Age = "";
        String gender = "";
        String Email = "";
        String PhNumber = "";
        String Address = "";
        String Address2 = "";
        String StreetAddress2 = "";
        String City = "";
        String State = "";
        String Country = "";
        String County = "";
        String ZipCode = "";
        String Ethnicity = "";
        String Race = "";
        String SSN = "";
        String SpCarePhy = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        int TravellingChk = 0;
        String TravelWhen = "";
        String TravelWhere = "";
        String TravelHowLong = "";
        int COVIDExposedChk = 0;
        String CovidExpWhen = "";
        String SympFever = "0";
        String SympBodyAches = "0";
        String SympSoreThroat = "0";
        String SympFatigue = "0";
        String SympRash = "0";
        String SympVomiting = "0";
        String SympDiarrhea = "0";
        String SympCough = "0";
        String SympRunnyNose = "0";
        String SympNausea = "0";
        String SympFluSymptoms = "0";
        String SympHeadache = "0";
        String SympLossTaste = "0";
        String SympShortBreath = "0";
        String SympCongestion = "0";
        String AddInfoTextArea = "";
        String GuarantorName = "";
        String GuarantorDOB = "";
        String GuarantorNumber = "";
        String GuarantorSSN = "";
        String SympEyeConjunctivitis = "0";
        String PriCarePhy = "";
        String ReasonVisit = "";
        int WorkersCompPolicy = 0;
        int MotorVehAccident = 0;
        String PriInsurance = "";
        String MemId = "";
        String GrpNumber = "";
        String PriInsuranceName = "";
        String AddressIfDifferent = "";
        String PrimaryDOB = "";
        String PrimarySSN = "";
        String PatientRelationtoPrimary = "";
        String PrimaryOccupation = "";
        String PrimaryEmployer = "";
        String EmployerAddress = "";
        String EmployerPhone = "";
        String SecondryInsurance = "";
        String SubscriberName = "";
        String SubscriberDOB = "";
        String MemberID_2 = "";
        String GroupNumber_2 = "";
        String PatientRelationshiptoSecondry = "";
        String NextofKinName = "";
        String RelationToPatientER = "";
        String PhoneNumberER = "";
        int LeaveMessageER = 0;
        String AddressER = "";
        String CityER = "";
        String StateER = "";
        String CountryER = "";
        String ZipCodeER = "";
        int ReturnPatient = 0;
        int Google = 0;
        int MapSearch = 0;
        int Billboard = 0;
        int OnlineReview = 0;
        int TV = 0;
        int Website = 0;
        int BuildingSignDriveBy = 0;
        int Facebook = 0;
        int School = 0;
        String School_text = "";
        int Twitter = 0;
        int Magazine = 0;
        String Magazine_text = "";
        int Newspaper = 0;
        String Newspaper_text = "";
        int FamilyFriend = 0;
        String FamilyFriend_text = "";
        int UrgentCare = 0;
        String UrgentCare_text = "";
        int CommunityEvent = 0;
        String CommunityEvent_text = "";
        int Work = 0;
        String Work_text = "";
        int Physician = 0;
        String Physician_text = "";
        int Other = 0;
        String Other_text = "";
        int SelfPayChk = 0;
        //********fonrline Marketing Variable*********
        int FrVisitedBefore = 0;
        int FrFamiliyVisitedBefore = 0;
        int FrInternet = 0;
        int FrBillboard = 0;
        int FrGoogle = 0;
        int FrBuildingSignage = 0;
        int FrFacebook = 0;
        int FrLivesNear = 0;
        int FrTwitter = 0;
        int FrTV = 0;
        int FrMapSearch = 0;
        int FrEvent = 0;
        String FrPhysicianReferral = "";
        String FrNeurologyReferral = "";
        String FrUrgentCareReferral = "";
        String FrOrganizationReferral = "";
        String FrFriendFamily = "";

        String PatientName = "";
        String ExtendedMRN = "";
        String VisitId = "";
        int MRN = 0;
        String CurrentDate = "";

        try {

            try {
                if (request.getParameter("Title") == null) {
                    Title = "Mr";
                } else {
                    Title = request.getParameter("Title").trim();
                }
                if (request.getParameter("FirstName") == null) {
                    FirstName = "";
                } else {
                    FirstName = request.getParameter("FirstName").trim();
                }
                if (request.getParameter("LastName") == null) {
                    LastName = "Mr";
                } else {
                    LastName = request.getParameter("LastName").trim();
                }
                if (request.getParameter("MiddleInitial") == null) {
                    MiddleInitial = "";
                } else {
                    MiddleInitial = request.getParameter("MiddleInitial").trim();
                }
                if (request.getParameter("MaritalStatus") == null) {
                    MaritalStatus = "";
                } else {
                    MaritalStatus = request.getParameter("MaritalStatus").trim();
                }
                if (request.getParameter("DOB") == null) {
                    DOB = "0000-00-00";
                } else {
                    DOB = request.getParameter("DOB").trim();
//             DOB =  DOB.substring(6,10) + "-" + DOB.substring(0,2) + "-" + DOB.substring(3,5);
                }
                if (request.getParameter("Age") == null) {
                    Age = "0";
                } else {
                    Age = request.getParameter("Age").trim();
                }
                if (request.getParameter("gender") == null) {
                    gender = "male";
                } else {
                    gender = request.getParameter("gender").trim();
                }
                if (request.getParameter("Email") == null) {
                    Email = "";
                } else {
                    Email = request.getParameter("Email").trim();
                }
                if (request.getParameter("PhNumber") == null) {
                    PhNumber = "0";
                } else {
                    PhNumber = request.getParameter("PhNumber").trim();
                }
                if (request.getParameter("Address") == null) {
                    Address = "";
                } else {
                    Address = request.getParameter("Address").trim();
                }
                if (request.getParameter("Address2") == null) {
                    Address2 = "";
                } else {
                    Address2 = request.getParameter("Address2").trim();
                }
                if (request.getParameter("StreetAddress2") == null) {
                    StreetAddress2 = "";
                } else {
                    StreetAddress2 = request.getParameter("StreetAddress2").trim();
                }
                if (request.getParameter("City") == null) {
                    City = "";
                } else {
                    City = request.getParameter("City").trim();
                }
                if (request.getParameter("State") == null) {
                    State = "";
                } else {
                    State = request.getParameter("State").trim();
                }
                if (request.getParameter("Country") == null) {
                    Country = "";
                } else {
                    Country = request.getParameter("Country").trim();
                }
                if (request.getParameter("Ethnicity") == null) {
                    Ethnicity = "";
                } else {
                    Ethnicity = request.getParameter("Ethnicity").trim();
                }
                if (request.getParameter("Race") == null) {
                    Race = "";
                } else {
                    Race = request.getParameter("Race").trim();
                }


                if (request.getParameter("County") == null) {
                    County = "";
                } else {
                    County = request.getParameter("County").trim();
                }
                if (request.getParameter("ZipCode") == null) {
                    ZipCode = "";
                } else {
                    ZipCode = request.getParameter("ZipCode").trim();
                }
                if (request.getParameter("SSN") == null) {
                    SSN = "";
                } else {
                    SSN = request.getParameter("SSN").trim();
                }
                if (request.getParameter("SpCarePhy") == null) {
                    SpCarePhy = "";
                } else {
                    SpCarePhy = request.getParameter("SpCarePhy").trim();
                }
                if (request.getParameter("Occupation") == null) {
                    Occupation = "";
                } else {
                    Occupation = request.getParameter("Occupation").trim();
                }
                if (request.getParameter("Employer") == null) {
                    Employer = "";
                } else {
                    Employer = request.getParameter("Employer").trim();
                }
                if (request.getParameter("EmpContact") == null) {
                    EmpContact = "";
                } else {
                    EmpContact = request.getParameter("EmpContact").trim();
                }
                if (request.getParameter("GuarantorName") == null) {
                    GuarantorName = "";
                } else {
                    GuarantorName = request.getParameter("GuarantorName").trim();
                }
                if (request.getParameter("GuarantorDOB") == null) {
                    GuarantorDOB = "";
                } else {
                    GuarantorDOB = request.getParameter("GuarantorDOB").trim();
                }
                if (request.getParameter("GuarantorNumber") == null) {
                    GuarantorNumber = "";
                } else {
                    GuarantorNumber = request.getParameter("GuarantorNumber").trim();
                }
                if (request.getParameter("GuarantorSSN") == null) {
                    GuarantorSSN = "";
                } else {
                    GuarantorSSN = request.getParameter("GuarantorSSN").trim();
                }


                if (request.getParameter("TravellingChk") == null) {
                    TravellingChk = 0;
                } else {
                    TravellingChk = Integer.parseInt(request.getParameter("TravellingChk").trim());
                }
                if (TravellingChk == 1) {
                    if (request.getParameter("TravelWhen") == null) {
                        TravelWhen = "";
                    } else {
                        TravelWhen = request.getParameter("TravelWhen").trim();
                    }
                    if (request.getParameter("TravelWhere") == null) {
                        TravelWhere = "";
                    } else {
                        TravelWhere = request.getParameter("TravelWhere").trim();
                    }
                    if (request.getParameter("TravelHowLong") == null) {
                        TravelHowLong = "";
                    } else {
                        TravelHowLong = request.getParameter("TravelHowLong").trim();
                    }
                }
                if (request.getParameter("COVIDExposedChk") == null) {
                    COVIDExposedChk = 0;
                } else {
                    COVIDExposedChk = Integer.parseInt(request.getParameter("COVIDExposedChk").trim());
                }
                if (request.getParameter("CovidExpWhen") == null) {
                    CovidExpWhen = "";
                } else {
                    CovidExpWhen = request.getParameter("CovidExpWhen").trim();
                }
                if (request.getParameter("SympFever") == null) {
                    SympFever = "0";
                } else {
                    SympFever = "1";
                }
                if (request.getParameter("SympBodyAches") == null) {
                    SympBodyAches = "0";
                } else {
                    SympBodyAches = "1";
                }
                if (request.getParameter("SympSoreThroat") == null) {
                    SympSoreThroat = "0";
                } else {
                    SympSoreThroat = "1";
                }
                if (request.getParameter("SympFatigue") == null) {
                    SympFatigue = "0";
                } else {
                    SympFatigue = "1";
                }
                if (request.getParameter("SympRash") == null) {
                    SympRash = "0";
                } else {
                    SympRash = "1";
                }
                if (request.getParameter("SympVomiting") == null) {
                    SympVomiting = "0";
                } else {
                    SympVomiting = "1";
                }
                if (request.getParameter("SympDiarrhea") == null) {
                    SympDiarrhea = "0";
                } else {
                    SympDiarrhea = "1";
                }
                if (request.getParameter("SympCough") == null) {
                    SympCough = "0";
                } else {
                    SympCough = "1";
                }
                if (request.getParameter("SympRunnyNose") == null) {
                    SympRunnyNose = "0";
                } else {
                    SympRunnyNose = "1";
                }
                if (request.getParameter("SympNausea") == null) {
                    SympNausea = "0";
                } else {
                    SympNausea = "1";
                }
                if (request.getParameter("SympFluSymptoms") == null) {
                    SympFluSymptoms = "0";
                } else {
                    SympFluSymptoms = "1";
                }
                if (request.getParameter("SympHeadache") == null) {
                    SympHeadache = "0";
                } else {
                    SympHeadache = "1";
                }
                if (request.getParameter("SympLossTaste") == null) {
                    SympLossTaste = "0";
                } else {
                    SympLossTaste = "1";
                }
                if (request.getParameter("SympShortBreath") == null) {
                    SympShortBreath = "0";
                } else {
                    SympShortBreath = "1";
                }
                if (request.getParameter("SympCongestion") == null) {
                    SympCongestion = "0";
                } else {
                    SympCongestion = "1";
                }
                if (request.getParameter("AddInfoTextArea") == null) {
                    AddInfoTextArea = "";
                } else {
                    AddInfoTextArea = request.getParameter("AddInfoTextArea").trim();
                }

                if (request.getParameter("PriCarePhy") == null) {
                    PriCarePhy = "";
                } else {
                    PriCarePhy = request.getParameter("PriCarePhy").trim();
                }
                if (request.getParameter("ReasonVisit") == null) {
                    ReasonVisit = "";
                } else {
                    ReasonVisit = request.getParameter("ReasonVisit").trim();
                }

                if (request.getParameter("WorkersCompPolicy") == null) {
                    WorkersCompPolicy = 0;
                } else {
                    WorkersCompPolicy = Integer.parseInt(request.getParameter("WorkersCompPolicy").trim());
                }
                if (request.getParameter("MotorVehAccident") == null) {
                    MotorVehAccident = 0;
                } else {
                    MotorVehAccident = Integer.parseInt(request.getParameter("MotorVehAccident").trim());
                }
                if (request.getParameter("PriInsurance") == null) {
                    PriInsurance = "";
                } else {
                    PriInsurance = request.getParameter("PriInsurance").trim();
                }
                if (request.getParameter("MemId") == null) {
                    MemId = "";
                } else {
                    MemId = request.getParameter("MemId").trim();
                }
                if (request.getParameter("GrpNumber") == null) {
                    GrpNumber = "";
                } else {
                    GrpNumber = request.getParameter("GrpNumber").trim();
                }
                if (request.getParameter("PriInsuranceName") == null) {
                    PriInsuranceName = "";
                } else {
                    PriInsuranceName = request.getParameter("PriInsuranceName").trim();
                }
                if (request.getParameter("AddressIfDifferent") == null) {
                    AddressIfDifferent = "";
                } else {
                    AddressIfDifferent = request.getParameter("AddressIfDifferent").trim();
                }
                if (request.getParameter("PrimaryDOB") == null) {
                    PrimaryDOB = "";
                } else {
                    PrimaryDOB = request.getParameter("PrimaryDOB").trim();
                }
                if (request.getParameter("PrimarySSN") == null) {
                    PrimarySSN = "";
                } else {
                    PrimarySSN = request.getParameter("PrimarySSN").trim();
                }
                if (request.getParameter("PatientRelationtoPrimary") == null) {
                    PatientRelationtoPrimary = "0";
                } else {
                    PatientRelationtoPrimary = request.getParameter("PatientRelationtoPrimary").trim();
                }
                if (request.getParameter("PrimaryOccupation") == null) {
                    PrimaryOccupation = "";
                } else {
                    PrimaryOccupation = request.getParameter("PrimaryOccupation").trim();
                }
                if (request.getParameter("PrimaryEmployer") == null) {
                    PrimaryEmployer = "";
                } else {
                    PrimaryEmployer = request.getParameter("PrimaryEmployer").trim();
                }
                if (request.getParameter("EmployerAddress") == null) {
                    EmployerAddress = "";
                } else {
                    EmployerAddress = request.getParameter("EmployerAddress").trim();
                }
                if (request.getParameter("EmployerPhone") == null) {
                    EmployerPhone = "";
                } else {
                    EmployerPhone = request.getParameter("EmployerPhone").trim();
                }
                if (request.getParameter("SecondryInsurance") == null) {
                    SecondryInsurance = "0";
                } else {
                    SecondryInsurance = request.getParameter("SecondryInsurance").trim();
                }
                if (request.getParameter("SubscriberName") == null) {
                    SubscriberName = "";
                } else {
                    SubscriberName = request.getParameter("SubscriberName").trim();
                }
                if (request.getParameter("SubscriberDOB").equals(null) || request.getParameter("SubscriberDOB").equals("")) {
                    SubscriberDOB = "0000-00-00";
                } else {
                    SubscriberDOB = request.getParameter("SubscriberDOB").trim();
                }
                if (request.getParameter("MemberID_2") == null) {
                    MemberID_2 = "";
                } else {
                    MemberID_2 = request.getParameter("MemberID_2").trim();
                }
                if (request.getParameter("GroupNumber_2") == null) {
                    GroupNumber_2 = "";
                } else {
                    GroupNumber_2 = request.getParameter("GroupNumber_2").trim();
                }
                if (request.getParameter("PatientRelationshiptoSecondry") == null) {
                    PatientRelationshiptoSecondry = "";
                } else {
                    PatientRelationshiptoSecondry = request.getParameter("PatientRelationshiptoSecondry").trim();
                }
                if (ClientIndex == 10 || ClientIndex == 15) {
                    AddmissionBundle = Integer.parseInt(request.getParameter("AddmissionBundle").trim());
                }
                if (request.getParameter("NextofKinName") == null) {
                    NextofKinName = "";
                } else {
                    NextofKinName = request.getParameter("NextofKinName").trim();
                }
                if (request.getParameter("RelationToPatientER") == null) {
                    RelationToPatientER = "";
                } else {
                    RelationToPatientER = request.getParameter("RelationToPatientER").trim();
                }
                if (request.getParameter("PhoneNumberER") == null) {
                    PhoneNumberER = "";
                } else {
                    PhoneNumberER = request.getParameter("PhoneNumberER").trim();
                }
                if (request.getParameter("LeaveMessageER") == null) {
                    LeaveMessageER = 0;
                } else {
                    LeaveMessageER = Integer.parseInt(request.getParameter("LeaveMessageER").trim());
                }
                if (request.getParameter("AddressER") == null) {
                    AddressER = "";
                } else {
                    AddressER = request.getParameter("AddressER").trim();
                }
                if (request.getParameter("CityER") == null) {
                    CityER = "";
                } else {
                    CityER = request.getParameter("CityER").trim();
                }
                if (request.getParameter("StateER") == null) {
                    StateER = "";
                } else {
                    StateER = request.getParameter("StateER").trim();
                }
                if (request.getParameter("CountryER") == null) {
                    CountryER = "";
                } else {
                    CountryER = request.getParameter("CountryER").trim();
                }
                if (request.getParameter("ZipCodeER") == null) {
                    ZipCodeER = "";
                } else {
                    ZipCodeER = request.getParameter("ZipCodeER").trim();
                }
                if (request.getParameter("ReturnPatient") == null) {
                    ReturnPatient = 0;
                } else {
                    ReturnPatient = 1;
                }
                if (request.getParameter("Google") == null) {
                    Google = 0;
                } else {
                    Google = 1;
                }
                if (request.getParameter("MapSearch") == null) {
                    MapSearch = 0;
                } else {
                    MapSearch = 1;
                }
                if (request.getParameter("Billboard") == null) {
                    Billboard = 0;
                } else {
                    Billboard = 1;
                }
                if (request.getParameter("OnlineReview") == null) {
                    OnlineReview = 0;
                } else {
                    OnlineReview = 1;
                }
                if (request.getParameter("TV") == null) {
                    TV = 0;
                } else {
                    TV = 2;
                }
                if (request.getParameter("Website") == null) {
                    Website = 0;
                } else {
                    Website = 1;
                }
                if (request.getParameter("BuildingSignDriveBy") == null) {
                    BuildingSignDriveBy = 0;
                } else {
                    BuildingSignDriveBy = 1;
                }
                if (request.getParameter("Facebook") == null) {
                    Facebook = 0;
                } else {
                    Facebook = 1;
                }
                if (request.getParameter("School") == null) {
                    School = 0;
                    School_text = "-";
                } else {
                    School = 1;
                    if (request.getParameter("School_text").trim().equals(null) || request.getParameter("School_text").trim().equals("")) {
                        School_text = "-";
                    } else {
                        School_text = request.getParameter("School_text").trim();
                    }
                }
                if (request.getParameter("Twitter") == null) {
                    Twitter = 0;
                } else {
                    Twitter = 1;
                }
                if (request.getParameter("Magazine") == null) {
                    Magazine = 0;
                    Magazine_text = "";
                } else {
                    Magazine = 1;
                    if (request.getParameter("Magazine_text").trim().equals(null) || request.getParameter("Magazine_text").trim().equals("")) {
                        Magazine_text = "";
                    } else {
                        Magazine_text = request.getParameter("Magazine_text").trim();
                    }
                }
                if (request.getParameter("Newspaper") == null) {
                    Newspaper = 0;
                    Newspaper_text = "";
                } else {
                    Newspaper = 1;
                    if (request.getParameter("Newspaper_text").trim().equals(null) || request.getParameter("Newspaper_text").trim().equals("")) {
                        Newspaper_text = "";
                    } else {
                        Newspaper_text = request.getParameter("Newspaper_text").trim();
                    }
                }
                if (request.getParameter("FamilyFriend") == null) {
                    FamilyFriend = 0;
                    FamilyFriend_text = "";
                } else {
                    FamilyFriend = 1;
                    if (request.getParameter("FamilyFriend_text").trim().equals(null) || request.getParameter("FamilyFriend_text").trim().equals("")) {
                        FamilyFriend_text = "";
                    } else {
                        FamilyFriend_text = request.getParameter("FamilyFriend_text").trim();
                    }
                }
                if (request.getParameter("UrgentCare") == null) {
                    UrgentCare = 0;
                    UrgentCare_text = "";
                } else {
                    UrgentCare = 1;
                    if (request.getParameter("UrgentCare_text").trim().equals(null) || request.getParameter("UrgentCare_text").trim().equals("")) {
                        UrgentCare_text = "";
                    } else {
                        UrgentCare_text = request.getParameter("UrgentCare_text").trim();
                    }
                }
                if (request.getParameter("CommunityEvent") == null) {
                    CommunityEvent = 0;
                    CommunityEvent_text = "";
                } else {
                    CommunityEvent = 1;
                    if (request.getParameter("CommunityEvent_text").trim().equals(null) || request.getParameter("CommunityEvent_text").trim().equals("")) {
                        CommunityEvent_text = "";
                    } else {
                        CommunityEvent_text = request.getParameter("CommunityEvent_text").trim();
                    }
                }
                if (request.getParameter("Work") == null) {
                    Work = 0;
                    Work_text = "";
                } else {
                    Work = 1;
                    if (request.getParameter("Work_text").trim().equals(null) || request.getParameter("Work_text").trim().equals("")) {
                        Work_text = "";
                    } else {
                        Work_text = request.getParameter("Work_text").trim();
                    }
                }
                if (request.getParameter("Physician") == null) {
                    Physician = 0;
                    Physician_text = "";
                } else {
                    Physician = 1;
                    if (request.getParameter("Physician_text").trim().equals(null) || request.getParameter("Physician_text").trim().equals("")) {
                        Physician_text = "";
                    } else {
                        Physician_text = request.getParameter("Physician_text").trim();
                    }
                }
                if (request.getParameter("Other") == null) {
                    Other = 0;
                    Other_text = "";
                } else {
                    Other = 1;
                    if (request.getParameter("Other_text").trim().equals(null) || request.getParameter("Other_text").trim().equals("")) {
                        Other_text = "";
                    } else {
                        Other_text = request.getParameter("Other_text").trim();
                    }
                }

                if (request.getParameter("FrVisitedBefore") == null) {
                    FrVisitedBefore = 0;
                } else {
                    FrVisitedBefore = Integer.parseInt(request.getParameter("FrVisitedBefore").trim());
                }
                if (request.getParameter("FrFamiliyVisitedBefore") == null) {
                    FrFamiliyVisitedBefore = 0;
                } else {
                    FrFamiliyVisitedBefore = Integer.parseInt(request.getParameter("FrFamiliyVisitedBefore").trim());
                }

                if (request.getParameter("FrInternet") == null) {
                    FrInternet = 0;
                } else {
                    FrInternet = 1;
                }
                if (request.getParameter("FrBillboard") == null) {
                    FrBillboard = 0;
                } else {
                    FrBillboard = 1;
                }
                if (request.getParameter("FrGoogle") == null) {
                    FrGoogle = 0;
                } else {
                    FrGoogle = 1;
                }
                if (request.getParameter("FrBuildingSignage") == null) {
                    FrBuildingSignage = 0;
                } else {
                    FrBuildingSignage = 1;
                }
                if (request.getParameter("FrFacebook") == null) {
                    FrFacebook = 0;
                } else {
                    FrFacebook = 1;
                }
                if (request.getParameter("FrLivesNear") == null) {
                    FrLivesNear = 0;
                } else {
                    FrLivesNear = 1;
                }
                if (request.getParameter("FrTwitter") == null) {
                    FrTwitter = 0;
                } else {
                    FrTwitter = 1;
                }
                if (request.getParameter("FrTV") == null) {
                    FrTV = 0;
                } else {
                    FrTV = 1;
                }
                if (request.getParameter("FrMapSearch") == null) {
                    FrMapSearch = 0;
                } else {
                    FrMapSearch = 1;
                }
                if (request.getParameter("FrEvent") == null) {
                    FrEvent = 0;
                } else {
                    FrEvent = 1;
                }

                if (request.getParameter("FrPhysicianReferral") == null) {
                    FrPhysicianReferral = "";
                } else {
                    FrPhysicianReferral = request.getParameter("FrPhysicianReferral").trim();
                }
                if (request.getParameter("FrNeurologyReferral") == null) {
                    FrNeurologyReferral = "";
                } else {
                    FrNeurologyReferral = request.getParameter("FrNeurologyReferral").trim();
                }
                if (request.getParameter("FrUrgentCareReferral") == null) {
                    FrUrgentCareReferral = "";
                } else {
                    FrUrgentCareReferral = request.getParameter("FrUrgentCareReferral").trim();
                }
                if (request.getParameter("FrOrganizationReferral") == null) {
                    FrOrganizationReferral = "";
                } else {
                    FrOrganizationReferral = request.getParameter("FrOrganizationReferral").trim();
                }
                if (request.getParameter("FrFriendFamily") == null) {
                    FrFriendFamily = "";
                } else {
                    FrFriendFamily = request.getParameter("FrFriendFamily").trim();
                }

            } catch (Exception e) {
                Services.DumException("SaveData --- 01", "PatientReg- ", request, e);
                final Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg");
                Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex);
                Parser.SetField("Message", "MES#01");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            try {
                Query = "Select Id,dbname from oe.clients where ltrim(rtrim(UPPER(Id))) =  ltrim(rtrim(UPPER('" + ClientIndex + "')))";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClientIndex = rset.getInt(1);
                    Database = rset.getString(2);
                }
                rset.close();
                stmt.close();

                try {
                    if (ReasonVisit.equals("")) {
                        out.println("Please Select the Reason of Visit from the DropDown that appears besides/below the ReasonVisit");
                        out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
                        return;
                    }

                    if (ClientIndex == 27 || ClientIndex == 29) {
                        Query = "Select ReasonVisit from " + Database + ".ReasonVisits where Id = " + ReasonVisit;
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next()) {
                            ReasonVisit = rset.getString(1);
                        }
                        rset.close();
                        stmt.close();
                    }

                    if (MRN == 0) {
                        MRN = 310001;
                    }
//                if (ClientIndex == 9) {
                    Query = "Select MRN from " + Database + ".PatientReg order by ID desc limit 1 ";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        MRN = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();

                    if (String.valueOf(MRN).length() == 0) {
                        MRN = 310001;
                    } else if (String.valueOf(MRN).length() == 4) {
                        MRN = 310001;
                    } else if (String.valueOf(MRN).length() == 8) {
                        MRN = 310001;
                    } else if (String.valueOf(MRN).length() == 6) {
                        ++MRN;
                    }

                    if (String.valueOf(ClientIndex).length() == 1) {
                        ExtendedMRN = "100" + ClientIndex + MRN;
                    } else if (String.valueOf(ClientIndex).length() == 2) {
                        ExtendedMRN = "10" + ClientIndex + MRN;
                    } else if (String.valueOf(ClientIndex).length() == 3) {
                        ExtendedMRN = "1" + ClientIndex + MRN;
                    }
                } catch (Exception e) {
                    Services.DumException("SaveData --- 02", "PatientReg- ", request, e);
                    final Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg");
                    Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex);
                    Parser.SetField("Message", "MES#02");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    return;

                }

            } catch (Exception e) {
                out.println("Error 1:" + e.getMessage() + Query);
            }
            Query = "Select now()";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                CurrentDate = rset.getString(1);
            }
            rset.close();
            stmt.close();

            UtilityHelper utilityHelper = new UtilityHelper();
            String ClientIp = utilityHelper.getClientIp(request);
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".PatientReg (ClientIndex,FirstName,LastName ,MiddleInitial,DOB,Age,Gender ,Email,PhNumber ," +
                                "Address,City ,State,Country,ZipCode,SSN,Occupation ,Employer ,EmpContact,PriCarePhy,ReasonVisit," +
                                "SelfPayChk,CreatedDate,Title, MaritalStatus,CreatedBy, MRN, Status, DateofService, ExtendedMRN, County, Ethnicity, Address2, StreetAddress2, " +
                                " EnterBy, EnterIP) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,0,?,?,?,?,?,?,?,?) ");
                MainReceipt.setInt(1, ClientIndex);
                MainReceipt.setString(2, FirstName);
                MainReceipt.setString(3, LastName);
                MainReceipt.setString(4, MiddleInitial);
                MainReceipt.setString(5, DOB);
                MainReceipt.setString(6, Age);
                MainReceipt.setString(7, gender);
                MainReceipt.setString(8, Email);
                MainReceipt.setString(9, PhNumber);
                MainReceipt.setString(10, Address);
                MainReceipt.setString(11, City);
                MainReceipt.setString(12, State);
                MainReceipt.setString(13, Country);
                MainReceipt.setString(14, ZipCode);
                MainReceipt.setString(15, SSN);
                MainReceipt.setString(16, Occupation);
                MainReceipt.setString(17, Employer);
                MainReceipt.setString(18, EmpContact);
                MainReceipt.setString(19, PriCarePhy);
                MainReceipt.setString(20, ReasonVisit);
                MainReceipt.setInt(21, SelfPayChk);
                MainReceipt.setString(22, Title);
                MainReceipt.setString(23, MaritalStatus);
                MainReceipt.setString(24, "Out Patient");
                MainReceipt.setInt(25, MRN);
                MainReceipt.setString(26, CurrentDate);
                MainReceipt.setString(27, ExtendedMRN);
                MainReceipt.setString(28, County);
                MainReceipt.setString(29, Ethnicity);
                MainReceipt.setString(30, Address2);
                MainReceipt.setString(31, StreetAddress2);
                MainReceipt.setString(32, UserId);
                MainReceipt.setString(33, ClientIp);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 2- Insertion PatientReg Table :" + e.getMessage());
                return;
            }

            try {
                Query = "Select max(ID) from " + Database + ".PatientReg ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientRegId = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error 3- :" + e.getMessage());
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".PatientVisit(MRN,PatientRegId,ReasonVisit,VisitNumber,DoctorId,DateofService," +
                                "CreatedDate,CreatedBy) VALUES (?,?,?,1,NULL,?,now(),?) ");
                MainReceipt.setInt(1, MRN);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, ReasonVisit);
                MainReceipt.setString(4, CurrentDate);
                MainReceipt.setString(5, UserId);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 3.1 Insertion in table PatientVisit- :" + e.getMessage());
            }
            try {
                Query = "Select MAX(Id) from " + Database + ".PatientVisit where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    VisitId = rset.getString(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error 3.3 Getting VisitId from PatientVisit Table- :" + e.getMessage());
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".PatientReg_Details (PatientRegId,MRN,TravellingChk,TravelWhen,TravelWhere ,TravelHowLong,COVIDExposedChk, " +
                                " SympFever,SympBodyAches ,SympSoreThroat,SympFatigue ," +
                                " SympRash,SympVomiting ,SympDiarrhea,SympCough,SympRunnyNose,SympNausea,SympFluSymptoms ,SympEyeConjunctivitis, Race, CovidExpWhen, SpCarePhy, " +
                                " SympHeadache, SympLossTaste, SympShortBreath, SympCongestion, AddInfoTextArea, VisitId, GuarantorName, GuarantorDOB, " +
                                " GuarantorNumber, GuarantorSSN) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                MainReceipt.setInt(1, PatientRegId);
                MainReceipt.setInt(2, MRN);
                MainReceipt.setInt(3, TravellingChk);
                MainReceipt.setString(4, TravelWhen);
                MainReceipt.setString(5, TravelWhere);
                MainReceipt.setString(6, TravelHowLong);
                MainReceipt.setInt(7, COVIDExposedChk);
                MainReceipt.setString(8, SympFever); //YES
                MainReceipt.setString(9, SympBodyAches);//YES
                MainReceipt.setString(10, SympSoreThroat);//YES
                MainReceipt.setString(11, SympFatigue);
                MainReceipt.setString(12, SympRash);//YES
                MainReceipt.setString(13, SympVomiting);//YES
                MainReceipt.setString(14, SympDiarrhea);//YES
                MainReceipt.setString(15, SympCough);//YES
                MainReceipt.setString(16, SympRunnyNose);
                MainReceipt.setString(17, SympNausea);
                MainReceipt.setString(18, SympFluSymptoms);
                MainReceipt.setString(19, "");
                MainReceipt.setString(20, Race);
                MainReceipt.setString(21, CovidExpWhen);
                MainReceipt.setString(22, SpCarePhy);
                MainReceipt.setString(23, SympHeadache);
                MainReceipt.setString(24, SympLossTaste);
                MainReceipt.setString(25, SympShortBreath);
                MainReceipt.setString(26, SympCongestion);
                MainReceipt.setString(27, AddInfoTextArea);
                MainReceipt.setString(28, VisitId);
                MainReceipt.setString(29, GuarantorName);
                MainReceipt.setString(30, GuarantorDOB);
                MainReceipt.setString(31, GuarantorNumber);
                MainReceipt.setString(32, GuarantorSSN);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 10- Insertion PatientReg_Details Table :" + e.getMessage());
                return;
            }
            if (!PriInsuranceName.equals("")) {
                Query = "Update " + Database + ".PatientReg Set SelfPayChk = 1 where ID = " + PatientRegId;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

                Query = "Select SelfPayChk from " + Database + ".PatientReg where ID = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    SelfPayChk = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } else {
                Query = "Update " + Database + ".PatientReg Set SelfPayChk = 0 where ID = " + PatientRegId;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

                Query = "Select SelfPayChk from " + Database + ".PatientReg where ID = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    SelfPayChk = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }
            try {
                if (SelfPayChk == 1) {
                    if (ClientIndex == 10 || ClientIndex == 15) {
                        try {
                            final PreparedStatement MainReceipt = conn.prepareStatement(
                                    "INSERT INTO " + Database + ".PatientAdmissionBundle(PatientRegId,AdmissionBundle,CreatedDate) " +
                                            "VALUES (?,?,now()) ");
                            MainReceipt.setInt(1, PatientRegId);
                            MainReceipt.setInt(2, AddmissionBundle);
                            MainReceipt.executeUpdate();
                            MainReceipt.close();
                        } catch (Exception e) {
                            System.out.println("Error in PatientAdmissionBundle Table:-- " + e.getMessage());
                        }
                    }

                    final PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + Database + ".InsuranceInfo(PatientRegId,WorkersCompPolicy,MotorVehAccident,PriInsurance,MemId," +
                                    "GrpNumber,PriInsuranceName,AddressIfDifferent,PrimaryDOB,PrimarySSN,PatientRelationtoPrimary," +
                                    "PrimaryOccupation,PrimaryEmployer,EmployerAddress,EmployerPhone,SecondryInsurance,SubscriberName," +
                                    "SubscriberDOB,MemberID_2,GroupNumber_2,PatientRelationshiptoSecondry,CreatedDate,VisitId) " +
                                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?) ");
                    MainReceipt.setInt(1, PatientRegId);
                    MainReceipt.setInt(2, WorkersCompPolicy);
                    MainReceipt.setInt(3, MotorVehAccident);
                    MainReceipt.setString(4, PriInsurance);
                    MainReceipt.setString(5, MemId);
                    MainReceipt.setString(6, GrpNumber);
                    MainReceipt.setString(7, PriInsuranceName);
                    MainReceipt.setString(8, AddressIfDifferent);
                    MainReceipt.setString(9, PrimaryDOB);
                    MainReceipt.setString(10, PrimarySSN);
                    MainReceipt.setString(11, PatientRelationtoPrimary);
                    MainReceipt.setString(12, PrimaryOccupation);
                    MainReceipt.setString(13, PrimaryEmployer);
                    MainReceipt.setString(14, EmployerAddress);
                    MainReceipt.setString(15, EmployerPhone);
                    MainReceipt.setString(16, SecondryInsurance);
                    MainReceipt.setString(17, SubscriberName);
                    MainReceipt.setString(18, SubscriberDOB);
                    MainReceipt.setString(19, MemberID_2);
                    MainReceipt.setString(20, GroupNumber_2);
                    MainReceipt.setString(21, PatientRelationshiptoSecondry);
                    MainReceipt.setString(22, VisitId);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                }
            } catch (Exception e) {
                out.println("Error 4- Insertion InsuranceInfo Table :" + e.getMessage());
                return;
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".EmergencyInfo (PatientRegId,NextofKinName,RelationToPatient,PhoneNumber,LeaveMessage," +
                                "Address,City,State,Country,ZipCode,CreatedDate,VisitId) VALUES (?,?,?,?,?,?,?,?,?,?,now(),?) ");
                MainReceipt.setInt(1, PatientRegId);
                MainReceipt.setString(2, NextofKinName);
                MainReceipt.setString(3, RelationToPatientER);
                MainReceipt.setString(4, PhoneNumberER);
                MainReceipt.setInt(5, LeaveMessageER);
                MainReceipt.setString(6, AddressER);
                MainReceipt.setString(7, CityER);
                MainReceipt.setString(8, StateER);
                MainReceipt.setString(9, CountryER);
                MainReceipt.setString(10, ZipCodeER);
                MainReceipt.setString(11, VisitId);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 5- Insertion EmergencyInfo Table :" + e.getMessage());
                return;
            }
            if (ClientIndex == 27 || ClientIndex == 29) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".RandomCheckInfo (PatientRegId,FrVisitedBefore," +
                            " FrFamiliyVisitedBefore,FrInternet,FrBillboard,FrGoogle,FrBuildingSignage,FrFacebook,FrLivesNear,FrTwitter,FrTV,FrMapSearch,FrEvent," +
                            " FrPhysicianReferral, FrNeurologyReferral,FrUrgentCareReferral,FrOrganizationReferral,FrFriendFamily,CreatedDate,VisitId) " +
                            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?) ");
                    MainReceipt.setInt(1, PatientRegId);
                    MainReceipt.setInt(2, FrVisitedBefore);
                    MainReceipt.setInt(3, FrFamiliyVisitedBefore);
                    MainReceipt.setInt(4, FrInternet);
                    MainReceipt.setInt(5, FrBillboard);
                    MainReceipt.setInt(6, FrGoogle);
                    MainReceipt.setInt(7, FrBuildingSignage);
                    MainReceipt.setInt(8, FrFacebook);
                    MainReceipt.setInt(9, FrLivesNear);
                    MainReceipt.setInt(10, FrTwitter);
                    MainReceipt.setInt(11, FrTV);
                    MainReceipt.setInt(12, FrMapSearch);
                    MainReceipt.setInt(13, FrEvent);
                    MainReceipt.setString(14, FrPhysicianReferral);
                    MainReceipt.setString(15, FrNeurologyReferral);
                    MainReceipt.setString(16, FrUrgentCareReferral);
                    MainReceipt.setString(17, FrOrganizationReferral);
                    MainReceipt.setString(18, FrFriendFamily);
                    MainReceipt.setString(19, VisitId);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception e) {
                    out.println("Error 7- Insertion RandomCheckInfo Frontline Table :" + e.getMessage());
                    return;
                }

            } else {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + Database + ".RandomCheckInfo (PatientRegId,ReturnPatient,Google,MapSearch,Billboard,OnlineReview," +
                                    "TV,Website,BuildingSignDriveBy,Facebook,School,School_text,Twitter,Magazine,Magazine_text,Newspaper," +
                                    "Newspaper_text,FamilyFriend,FamilyFriend_text,UrgentCare,UrgentCare_text,CommunityEvent," +
                                    "CommunityEvent_text,Work_text,Physician_text,Other_text,CreatedDate,VisitId) " +
                                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?) ");
                    MainReceipt.setInt(1, PatientRegId);
                    MainReceipt.setInt(2, ReturnPatient);
                    MainReceipt.setInt(3, Google);
                    MainReceipt.setInt(4, MapSearch);
                    MainReceipt.setInt(5, Billboard);
                    MainReceipt.setInt(6, OnlineReview);
                    MainReceipt.setInt(7, TV);
                    MainReceipt.setInt(8, Website);
                    MainReceipt.setInt(9, BuildingSignDriveBy);
                    MainReceipt.setInt(10, Facebook);
                    MainReceipt.setInt(11, School);
                    MainReceipt.setString(12, School_text);
                    MainReceipt.setInt(13, Twitter);
                    MainReceipt.setInt(14, Magazine);
                    MainReceipt.setString(15, Magazine_text);
                    MainReceipt.setInt(16, Newspaper);
                    MainReceipt.setString(17, Newspaper_text);
                    MainReceipt.setInt(18, FamilyFriend);
                    MainReceipt.setString(19, FamilyFriend_text);
                    MainReceipt.setInt(20, UrgentCare);
                    MainReceipt.setString(21, UrgentCare_text);
                    MainReceipt.setInt(22, CommunityEvent);
                    MainReceipt.setString(23, CommunityEvent_text);
                    MainReceipt.setString(24, Work_text);
                    MainReceipt.setString(25, Physician_text);
                    MainReceipt.setString(26, Other_text);
                    MainReceipt.setString(27, VisitId);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception e) {
                    out.println("Error 7- Insertion RandomCheckInfo Table :" + e.getMessage());
                    return;
                }
            }
            Query = "Select CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName) from " + Database + ".PatientReg where ID = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            String Date = "";
            Query = "Select Date_format(now(),'%m/%d/%Y %T')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " We Have Registered You Successfully. Please Wait for Further Processing. " + Date);
            Parser.SetField("MRN", String.valueOf("MRN: " + MRN));
            Parser.SetField("FormName", String.valueOf("PatientReg"));
            Parser.SetField("ActionID", String.valueOf("GetValues&ClientIndex=" + ClientIndex));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
//            Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/orange_2/Exception/Message.html");
            //System.out.println(String.valueOf(Services.GetHtmlPath(this.getServletContext())));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message.html");
        } catch (Exception e) {
            //System.out.println("ERROR HERE : "+e.getMessage());
            try {
                Services.DumException("SaveData --- 0N", "PatientReg- ", request, e);
                final Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg");
                Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex);
                Parser.SetField("Message", "MES#0N");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (Exception e2) {
                out.println(e2.getMessage());
            }
        }
    }

    void EditValues_New(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String Database, String UserId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int ClientIndex = 0;
        int PatientRegId = 0;
        String MRN = request.getParameter("MRN").trim();
        int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());

        String Title = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String MaritalStatus = "";
        String DOB = "";
        String Age = "";
        String gender = "";
        String Email = "";
        String PhNumber = "";
        String Address = "";
        String Address2 = "";
        String StreetAddress2 = "";
        String City = "";
        String State = "";
        String Country = "";
        String COVIDStatus = "";
        String DoctorName = "";
        String DateofService = null;
        String ZipCode = "";
        String SSN = "";
        String SpCarePhy = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        int WorkersCompPolicy = 0;
        int MotorVehAccident = 0;
        String PriInsurance = "";
        String MemId = "";
        String GrpNumber = "";
        String PriInsuranceName = "";
        String AddressIfDifferent = "";
        String PrimaryDOB = "";
        String PrimarySSN = "";
        String PatientRelationtoPrimary = "";
        String PrimaryOccupation = "";
        String PrimaryEmployer = "";
        String EmployerAddress = "";
        String EmployerPhone = "";
        String SecondryInsurance = "";
        String SubscriberName = "";
        String SubscriberDOB = "";
        String MemberID_2 = "";
        String GroupNumber_2 = "";
        String PatientRelationshiptoSecondry = "";
        String NextofKinName = "";
        String RelationToPatientER = "";
        String PhoneNumberER = "";
        int LeaveMessageER = 0;
        String AddressER = "";
        String CityER = "";
        String StateER = "";
        String CountryER = "";
        String ZipCodeER = "";
        String DateConcent = "";
        String WitnessConcent = "";
        String PatientBehalfConcent = "";
        String RelativeSignConcent = "";
        String DateConcent2 = "";
        String WitnessConcent2 = "";
        String PatientSignConcent = "";
        String Ethnicity = "";
        String Race = "";
        String County = "";
        String CovidExpWhen = "";
        String TravellingChk = "";
        String TravelWhen = "";
        String TravelWhere = "";
        String TravelHowLong = "";
        String COVIDExposedChk = "";
        String SympFever = "0";
        String SympBodyAches = "0";
        String SympSoreThroat = "0";
        String SympFatigue = "0";
        String SympRash = "0";
        String SympVomiting = "0";
        String SympDiarrhea = "0";
        String SympCough = "0";
        String SympRunnyNose = "0";
        String SympNausea = "0";
        String SympFluSymptoms = "0";
        String SympHeadache = "0";
        String SympLossTaste = "0";
        String SympShortBreath = "0";
        String SympCongestion = "0";
        String AddInfoTextArea = "0";
        String GuarantorName = "";
        String GuarantorDOB = "";
        String GuarantorNumber = "";
        String GuarantorSSN = "";
        String AddInfoTextAreaN = "0";
        //********fonrline Marketing Variable*********
        int FrVisitedBefore = 0;
        int FrFamiliyVisitedBefore = 0;
        int FrInternet = 0;
        int FrBillboard = 0;
        int FrGoogle = 0;
        int FrBuildingSignage = 0;
        int FrFacebook = 0;
        int FrLivesNear = 0;
        int FrTwitter = 0;
        int FrTV = 0;
        int FrMapSearch = 0;
        int FrEvent = 0;
        String FrPhysicianReferral = "";
        String FrNeurologyReferral = "";
        String FrUrgentCareReferral = "";
        String FrOrganizationReferral = "";
        String FrFriendFamily = "";

        StringBuilder FrVisitedBeforeN = new StringBuilder();
        StringBuilder FrFamiliyVisitedBeforeN = new StringBuilder();
        StringBuilder FrInternetN = new StringBuilder();
        StringBuilder FrBillboardN = new StringBuilder();
        StringBuilder FrGoogleN = new StringBuilder();
        StringBuilder FrBuildingSignageN = new StringBuilder();
        StringBuilder FrFacebookN = new StringBuilder();
        StringBuilder FrLivesNearN = new StringBuilder();
        StringBuilder FrTwitterN = new StringBuilder();
        StringBuilder FrTVN = new StringBuilder();
        StringBuilder FrMapSearchN = new StringBuilder();
        StringBuilder FrEventN = new StringBuilder();

        String SympEyeConjunctivitis = "0";
        StringBuffer ReturnPatient = new StringBuffer();
        StringBuffer Google = new StringBuffer();
        StringBuffer MapSearch = new StringBuffer();
        StringBuffer Billboard = new StringBuffer();
        StringBuffer OnlineReview = new StringBuffer();
        StringBuffer TV = new StringBuffer();
        StringBuffer Website = new StringBuffer();
        StringBuffer BuildingSignDriveBy = new StringBuffer();
        StringBuffer Facebook = new StringBuffer();
        StringBuffer School = new StringBuffer();
        String School_text = "";
        StringBuffer Twitter = new StringBuffer();
        StringBuffer Magazine = new StringBuffer();
        String Magazine_text = "";
        StringBuffer Newspaper = new StringBuffer();
        String Newspaper_text = "";
        StringBuffer FamilyFriend = new StringBuffer();
        String FamilyFriend_text = "";
        StringBuffer UrgentCare = new StringBuffer();
        String UrgentCare_text = "";
        StringBuffer CommunityEvent = new StringBuffer();
        String CommunityEvent_text = "";
        StringBuffer Work_textBuff = new StringBuffer();
        String Work_text = "";
        StringBuffer Physician_textBuff = new StringBuffer();
        String Physician_text = "";
        StringBuffer Other_textBuff = new StringBuffer();
        String Other_text = "";
        int SelfPayChk = 0;
        int VerifyChkBox = 0;
        String PatientName = "";
        String DOS = "";
        StringBuffer TitleBuff = new StringBuffer();
        StringBuffer COVIDStatusBuff = new StringBuffer();
        StringBuffer EthnicityBuff = new StringBuffer();
        StringBuffer RaceBuff = new StringBuffer();
        StringBuffer ReasonVisitBuff = new StringBuffer();
        StringBuffer ReasonVisitBuffN = new StringBuffer();
        StringBuffer PriInsuranceNameBuff = new StringBuffer();
        StringBuffer PriInsuranceBuff = new StringBuffer();
        StringBuffer SecondryInsuranceBuff = new StringBuffer();
        StringBuffer DoctorList = new StringBuffer();
        StringBuffer MaritalStatusBuff = new StringBuffer();
        StringBuffer PatientRelationBuff = new StringBuffer();
        StringBuffer CountryBuff = new StringBuffer();
        StringBuffer genderBuff = new StringBuffer();
        StringBuffer SelfPayChkBuff = new StringBuffer();
        StringBuffer MotorVehAccidentBuff = new StringBuffer();
        StringBuffer WorkersCompPolicyBuff = new StringBuffer();
        StringBuffer PatientRelationshiptoSecondryBuff = new StringBuffer();
        StringBuffer PatientRelationtoPrimaryBuff = new StringBuffer();
        StringBuffer CountryBuffER = new StringBuffer();
        StringBuffer LeaveMessageERBuff = new StringBuffer();
        StringBuffer TravellingChkBuff = new StringBuffer();
        StringBuffer COVIDExposedChkBuff = new StringBuffer();
        StringBuffer AdmissionBundleBuff = new StringBuffer();
        try {
            Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(City,'-'), IFNULL(State,'-'), IFNULL(ZipCode,'-'), IFNULL(PhNumber,'-'), IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(ID,0), ClientIndex, DATE_FORMAT(CreatedDate, '%d-%m-%Y'), IFNULL(Country,'-'), IFNULL(COVIDStatus, '-'), IFNULL(DoctorsName,'-'), IFNULL(DateofService, '-'), IFNULL(Ethnicity,''), IFNULL(County,''), IFNULL(Address2,''), IFNULL(StreetAddress2,'')   From " + Database + ".PatientReg Where MRN =" + MRN;
//         out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                LastName = rset.getString(1).trim();
                FirstName = rset.getString(2).trim();
                MiddleInitial = rset.getString(3).trim();
                Title = rset.getString(4).trim();
                MaritalStatus = rset.getString(5);
                DOB = rset.getString(6);
                Age = rset.getString(7);
                gender = rset.getString(8);
                Address = rset.getString(9);
                City = rset.getString(10);
                State = rset.getString(11);
                ZipCode = rset.getString(12);
                PhNumber = rset.getString(13);
                SSN = rset.getString(14);
                Occupation = rset.getString(15);
                Employer = rset.getString(16);
                EmpContact = rset.getString(17);
                PriCarePhy = rset.getString(18);
                Email = rset.getString(19);
                ReasonVisit = rset.getString(20);
                SelfPayChk = rset.getInt(21);
                PatientRegId = rset.getInt(22);
                ClientIndex = rset.getInt(23);
                DOS = rset.getString(24);
                Country = rset.getString(25);
                COVIDStatus = rset.getString(26);
                DoctorName = rset.getString(27);
                DateofService = rset.getString(28);
                Ethnicity = rset.getString(29);
                County = rset.getString(30);
                Address2 = rset.getString(31);
                StreetAddress2 = rset.getString(32);
            }
            rset.close();
            stmt.close();

            try {
                Query = "Select IFNULL(TravellingChk,0), IFNULL(DATE_FORMAT(TravelWhen,'%Y-%m-%d'),''), IFNULL(TravelWhere,''), IFNULL(TravelHowLong,''), IFNULL(COVIDExposedChk,0), IFNULL(SympFever,0), " +
                        " IFNULL(SympBodyAches,0), IFNULL(SympSoreThroat,0), IFNULL(SympFatigue,0), IFNULL(SympRash,0), IFNULL(SympVomiting,0), IFNULL(SympDiarrhea,0), IFNULL(SympCough,0), " +
                        " IFNULL(SympRunnyNose,0), IFNULL(SympNausea,0), IFNULL(SympFluSymptoms,0), IFNULL(SympEyeConjunctivitis,0), IFNULL(Race,''), " +
                        " IFNULL(DATE_FORMAT(CovidExpWhen,'%Y-%m-%d'),''), IFNULL(SpCarePhy,''), IFNULL(SympHeadache,0),IFNULL(SympLossTaste,0), IFNULL(SympShortBreath,0), " +
                        " IFNULL(SympCongestion,0), IFNULL(AddInfoTextArea,''),IFNULL(GuarantorName,''),IFNULL(GuarantorDOB,''), IFNULL(GuarantorNumber,''), " +
                        " IFNULL(GuarantorSSN,'')" +
                        " from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    TravellingChk = rset.getString(1);
                    TravelWhen = rset.getString(2);
                    TravelWhere = rset.getString(3);
                    TravelHowLong = rset.getString(4);
                    COVIDExposedChk = rset.getString(5);
                    SympFever = rset.getString(6);
                    SympBodyAches = rset.getString(7);
                    SympSoreThroat = rset.getString(8);
                    SympFatigue = rset.getString(9);
                    SympRash = rset.getString(10);
                    SympVomiting = rset.getString(11);
                    SympDiarrhea = rset.getString(12);
                    SympCough = rset.getString(13);
                    SympRunnyNose = rset.getString(14);
                    SympNausea = rset.getString(15);
                    SympFluSymptoms = rset.getString(16);
                    SympEyeConjunctivitis = rset.getString(17);
                    Race = rset.getString(18);
                    CovidExpWhen = rset.getString(19);
                    SpCarePhy = rset.getString(20);
                    SympHeadache = rset.getString(21);
                    SympLossTaste = rset.getString(22);
                    SympShortBreath = rset.getString(23);
                    SympCongestion = rset.getString(24);
                    AddInfoTextArea = rset.getString(25);
                    GuarantorName = rset.getString(26);
                    GuarantorDOB = rset.getString(27);
                    GuarantorNumber = rset.getString(28);
                    GuarantorSSN = rset.getString(29);

                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error in PAtient Reg Details Table: " + e.getMessage());
            }
            Query = "Select Title from " + Database + ".Title";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            TitleBuff.append("<option value=''>Select Title</option>");
            while (rset.next()) {
                if (Title.equals(rset.getString(1))) {
                    TitleBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(1) + "</option>");
                } else {
                    TitleBuff.append("<option value=" + rset.getString(1) + ">" + rset.getString(1) + "</option>");
                }
            }
            rset.close();
            stmt.close();

            Query = "Select Id, CONCAT(DoctorsLastName, ' , ', DoctorsFirstName) from " + Database + ".DoctorsList";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            DoctorList.append("<option value=''>Select Physician</option>");
            while (rset.next()) {
                if (DoctorName.equals(rset.getString(1))) {
                    DoctorList.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(2) + "</option>");
                } else {
                    DoctorList.append("<option value=" + rset.getString(1) + ">" + rset.getString(2) + "</option>");
                }
            }
            rset.close();
            stmt.close();

            if (Ethnicity.equals("1")) {
                EthnicityBuff.append("<div class=\"radio\">\n" +
                        "<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_HispanicOrLatino\" value=\"1\" checked required>\n" +
                        "<label for=\"Ethnicity_HispanicOrLatino\">Hispanic or Latino</label>                    \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_NonHispanicOrLatino\" value=\"2\" >\n" +
                        "<label for=\"Ethnicity_blackOrAfricanAmerican\">Non Hispanic or Latino</label>   \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_Others\"  value=\"3\">\n" +
                        "<label for=\"Ethnicity_Others\">Others</label> \n" +
                        "</div>\n");
            } else if (Ethnicity.equals("2")) {
                EthnicityBuff.append("<div class=\"radio\">\n" +
                        "<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_HispanicOrLatino\" value=\"1\" required >\n" +
                        "<label for=\"Ethnicity_HispanicOrLatino\">Hispanic or Latino</label>                    \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_NonHispanicOrLatino\" value=\"2\" checked >\n" +
                        "<label for=\"Ethnicity_blackOrAfricanAmerican\">Non Hispanic or Latino</label>   \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_Others\"  value=\"3\">\n" +
                        "<label for=\"Ethnicity_Others\">Others</label> \n" +
                        "</div>\n");
            } else if (Ethnicity.equals("3")) {
                EthnicityBuff.append("<div class=\"radio\">\n" +
                        "<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_HispanicOrLatino\" value=\"1\" required >\n" +
                        "<label for=\"Ethnicity_HispanicOrLatino\">Hispanic or Latino</label>                    \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_NonHispanicOrLatino\" value=\"2\" >\n" +
                        "<label for=\"Ethnicity_blackOrAfricanAmerican\">Non Hispanic or Latino</label>   \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_Others\"  value=\"3\" checked>\n" +
                        "<label for=\"Ethnicity_Others\">Others</label> \n" +
                        "</div>\n");
            } else {
                EthnicityBuff.append("<div class=\"radio\">\n" +
                        "<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_HispanicOrLatino\" value=\"1\" required >\n" +
                        "<label for=\"Ethnicity_HispanicOrLatino\">Hispanic or Latino</label>                    \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_NonHispanicOrLatino\" value=\"2\" >\n" +
                        "<label for=\"Ethnicity_blackOrAfricanAmerican\">Non Hispanic or Latino</label>   \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_Others\"  value=\"3\">\n" +
                        "<label for=\"Ethnicity_Others\">Others</label> \n" +
                        "</div>\n");
            }

            if (Race.equals("1")) {
                RaceBuff.append("<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_AfricanAmerican\" value=\"1\" checked required >\n" +
                        "<label for=\"Race_AfricanAmerican\">African American</label>                    \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_AmericanIndianOrAlaska\" value=\"2\" >\n" +
                        "<label for=\"Race_AmericanIndianOrAlaska\">American Indian or Alska Native</label>   \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_Asian\"  value=\"3\">\n" +
                        "<label for=\"Race_Asian\">Asian</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_NatHawaiianPacIslander\" value=\"4\">\n" +
                        "<label for=\"Race_NatHawaiianPacIslander\">Native Hawaiian or Other Pacific Islander</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_White\" value=\"5\">\n" +
                        "<label for=\"Race_White\">White</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_Other\" value=\"6\">\n" +
                        "<label for=\"Race_Other\">Other</label> \n" +
                        "</div>\n");
            } else if (Race.equals("2")) {
                RaceBuff.append("<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_AfricanAmerican\" value=\"1\" required >\n" +
                        "<label for=\"Race_AfricanAmerican\">African American</label>                    \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_AmericanIndianOrAlaska\" value=\"2\" checked>\n" +
                        "<label for=\"Race_AmericanIndianOrAlaska\">American Indian or Alska Native</label>   \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_Asian\"  value=\"3\">\n" +
                        "<label for=\"Race_Asian\">Asian</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_NatHawaiianPacIslander\" value=\"4\">\n" +
                        "<label for=\"Race_NatHawaiianPacIslander\">Native Hawaiian or Other Pacific Islander</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_White\" value=\"5\">\n" +
                        "<label for=\"Race_White\">White</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_Other\" value=\"6\">\n" +
                        "<label for=\"Race_Other\">Other</label> \n" +
                        "</div>\n");
            } else if (Race.equals("3")) {
                RaceBuff.append("<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_AfricanAmerican\" value=\"1\" required >\n" +
                        "<label for=\"Race_AfricanAmerican\">African American</label>                    \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_AmericanIndianOrAlaska\" value=\"2\" >\n" +
                        "<label for=\"Race_AmericanIndianOrAlaska\">American Indian or Alska Native</label>   \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_Asian\"  value=\"3\" checked>\n" +
                        "<label for=\"Race_Asian\">Asian</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_NatHawaiianPacIslander\" value=\"4\">\n" +
                        "<label for=\"Race_NatHawaiianPacIslander\">Native Hawaiian or Other Pacific Islander</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_White\" value=\"5\">\n" +
                        "<label for=\"Race_White\">White</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_Other\" value=\"6\">\n" +
                        "<label for=\"Race_Other\">Other</label> \n" +
                        "</div>\n");
            } else if (Race.equals("4")) {
                RaceBuff.append("<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_AfricanAmerican\" value=\"1\" required >\n" +
                        "<label for=\"Race_AfricanAmerican\">African American</label>                    \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_AmericanIndianOrAlaska\" value=\"2\" >\n" +
                        "<label for=\"Race_AmericanIndianOrAlaska\">American Indian or Alska Native</label>   \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_Asian\"  value=\"3\" >\n" +
                        "<label for=\"Race_Asian\">Asian</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_NatHawaiianPacIslander\" value=\"4\" checked>\n" +
                        "<label for=\"Race_NatHawaiianPacIslander\">Native Hawaiian or Other Pacific Islander</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_White\" value=\"5\">\n" +
                        "<label for=\"Race_White\">White</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_Other\" value=\"6\">\n" +
                        "<label for=\"Race_Other\">Other</label> \n" +
                        "</div>\n");
            } else if (Race.equals("5")) {
                RaceBuff.append("<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_AfricanAmerican\" value=\"1\" required >\n" +
                        "<label for=\"Race_AfricanAmerican\">African American</label>                    \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_AmericanIndianOrAlaska\" value=\"2\" >\n" +
                        "<label for=\"Race_AmericanIndianOrAlaska\">American Indian or Alska Native</label>   \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_Asian\"  value=\"3\" >\n" +
                        "<label for=\"Race_Asian\">Asian</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_NatHawaiianPacIslander\" value=\"4\" >\n" +
                        "<label for=\"Race_NatHawaiianPacIslander\">Native Hawaiian or Other Pacific Islander</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_White\" value=\"5\" checked>\n" +
                        "<label for=\"Race_White\">White</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_Other\" value=\"6\">\n" +
                        "<label for=\"Race_Other\">Other</label> \n" +
                        "</div>\n");
            } else if (Race.equals("6")) {
                RaceBuff.append("<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_AfricanAmerican\" value=\"1\" required >\n" +
                        "<label for=\"Race_AfricanAmerican\">African American</label>                    \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_AmericanIndianOrAlaska\" value=\"2\" >\n" +
                        "<label for=\"Race_AmericanIndianOrAlaska\">American Indian or Alska Native</label>   \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_Asian\"  value=\"3\" >\n" +
                        "<label for=\"Race_Asian\">Asian</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_NatHawaiianPacIslander\" value=\"4\" >\n" +
                        "<label for=\"Race_NatHawaiianPacIslander\">Native Hawaiian or Other Pacific Islander</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_White\" value=\"5\" >\n" +
                        "<label for=\"Race_White\">White</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_Other\" value=\"6\" checked>\n" +
                        "<label for=\"Race_Other\">Other</label> \n" +
                        "</div>\n");
            } else {
                RaceBuff.append("<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_AfricanAmerican\" value=\"1\" required >\n" +
                        "<label for=\"Race_AfricanAmerican\">African American</label>                    \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_AmericanIndianOrAlaska\" value=\"2\" >\n" +
                        "<label for=\"Race_AmericanIndianOrAlaska\">American Indian or Alska Native</label>   \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_Asian\"  value=\"3\" >\n" +
                        "<label for=\"Race_Asian\">Asian</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_NatHawaiianPacIslander\" value=\"4\" >\n" +
                        "<label for=\"Race_NatHawaiianPacIslander\">Native Hawaiian or Other Pacific Islander</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_White\" value=\"5\" >\n" +
                        "<label for=\"Race_White\">White</label> \n" +
                        "</div>\n" +
                        "<div class=\"radio\">\n" +
                        "<input name=\"Race\" type=\"radio\" id=\"Race_Other\" value=\"6\" >\n" +
                        "<label for=\"Race_Other\">Other</label> \n" +
                        "</div>\n");
            }

            if (TravellingChk.equals("1")) {
                TravellingChkBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"TravellingChk\" type=\"radio\" id=\"TravellingChkY\" value=\"1\" onclick=\"TravllingChk(this.value)\" checked required />\n" +
                        "<label for=\"TravellingChkY\">Yes</label>\n" +
                        "\n" +
                        "<input name=\"TravellingChk\" type=\"radio\" id=\"TravellingChkN\"value=\"0\" onclick=\"TravllingChk(this.value)\" />\n" +
                        "<label for=\"TravellingChkN\">No</label>\t\n" +
                        "</div>\n");
            } else if (TravellingChk.equals("0")) {
                TravellingChkBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"TravellingChk\" type=\"radio\" id=\"TravellingChkY\" value=\"1\" onclick=\"TravllingChk(this.value)\"  required />\n" +
                        "<label for=\"TravellingChkY\">Yes</label>\n" +
                        "\n" +
                        "<input name=\"TravellingChk\" type=\"radio\" id=\"TravellingChkN\"value=\"0\" onclick=\"TravllingChk(this.value)\" checked />\n" +
                        "<label for=\"TravellingChkN\">No</label>\t\n" +
                        "</div>\n");
            } else {
                TravellingChkBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"TravellingChk\" type=\"radio\" id=\"TravellingChkY\" value=\"1\" onclick=\"TravllingChk(this.value)\"  required />\n" +
                        "<label for=\"TravellingChkY\">Yes</label>\n" +
                        "\n" +
                        "<input name=\"TravellingChk\" type=\"radio\" id=\"TravellingChkN\"value=\"0\" onclick=\"TravllingChk(this.value)\" />\n" +
                        "<label for=\"TravellingChkN\">No</label>\t\n" +
                        "</div>\n");
            }

            if (COVIDExposedChk.equals("1")) {
                COVIDExposedChkBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"COVIDExposedChk\" type=\"radio\" id=\"COVIDExposedChkY\" value=\"1\" checked required />\n" +
                        "<label for=\"COVIDExposedChkY\">Yes</label>\n" +
                        "\n" +
                        "<input name=\"COVIDExposedChk\" type=\"radio\" id=\"COVIDExposedChkN\"value=\"0\" />\n" +
                        "<label for=\"COVIDExposedChkN\">No</label>\t\n" +
                        "</div>\t\t\t\t\t\t\t\t\t \n");
            } else if (COVIDExposedChk.equals("0")) {
                COVIDExposedChkBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"COVIDExposedChk\" type=\"radio\" id=\"COVIDExposedChkY\" value=\"1\"  required />\n" +
                        "<label for=\"COVIDExposedChkY\">Yes</label>\n" +
                        "\n" +
                        "<input name=\"COVIDExposedChk\" type=\"radio\" id=\"COVIDExposedChkN\"value=\"0\" checked />\n" +
                        "<label for=\"COVIDExposedChkN\">No</label>\t\n" +
                        "</div>\t\t\t\t\t\t\t\t\t \n");
            } else {
                COVIDExposedChkBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"COVIDExposedChk\" type=\"radio\" id=\"COVIDExposedChkY\" value=\"1\"  required />\n" +
                        "<label for=\"COVIDExposedChkY\">Yes</label>\n" +
                        "\n" +
                        "<input name=\"COVIDExposedChk\" type=\"radio\" id=\"COVIDExposedChkN\"value=\"0\" />\n" +
                        "<label for=\"COVIDExposedChkN\">No</label>\t\n" +
                        "</div>\t\t\t\t\t\t\t\t\t \n");
            }

            if (SympFever.equals("1")) {
                SympFever = "<input type=\"checkbox\" id=\"SympFever\" name=\"SympFever\" checked />";
            } else {
                SympFever = "<input type=\"checkbox\" id=\"SympFever\" name=\"SympFever\" />";
            }

            if (SympBodyAches.equals("1")) {
                SympBodyAches = "<input type=\"checkbox\" id=\"SympBodyAches\" name=\"SympBodyAches\" checked />";
            } else {
                SympBodyAches = "<input type=\"checkbox\" id=\"SympBodyAches\" name=\"SympBodyAches\" />";
            }

            if (SympSoreThroat.equals("1")) {
                SympSoreThroat = "<input type=\"checkbox\" id=\"SympSoreThroat\" name=\"SympSoreThroat\" checked />";
            } else {
                SympSoreThroat = "<input type=\"checkbox\" id=\"SympSoreThroat\" name=\"SympSoreThroat\" />";
            }

            if (SympFatigue.equals("1")) {
                SympFatigue = "<input type=\"checkbox\" id=\"SympFatigue\" name=\"SympFatigue\"  checked />";
            } else {
                SympFatigue = "<input type=\"checkbox\" id=\"SympFatigue\" name=\"SympFatigue\"  />";
            }

            if (SympRash.equals("1")) {
                SympRash = "<input type=\"checkbox\" id=\"SympRash\" name=\"SympRash\"  checked />";
            } else {
                SympRash = "<input type=\"checkbox\" id=\"SympRash\" name=\"SympRash\"  />";
            }

            if (SympVomiting.equals("1")) {
                SympVomiting = "<input type=\"checkbox\" id=\"SympVomiting\" name=\"SympVomiting\" checked />";
            } else {
                SympVomiting = "<input type=\"checkbox\" id=\"SympVomiting\" name=\"SympVomiting\" />";
            }

            if (SympDiarrhea.equals("1")) {
                SympDiarrhea = "<input type=\"checkbox\" id=\"SympDiarrhea\" name=\"SympDiarrhea\" checked />";
            } else {
                SympDiarrhea = "<input type=\"checkbox\" id=\"SympDiarrhea\" name=\"SympDiarrhea\" />";
            }

            if (SympCough.equals("1")) {
                SympCough = "<input type=\"checkbox\" id=\"SympCough\" name=\"SympCough\" checked />";
            } else {
                SympCough = "<input type=\"checkbox\" id=\"SympCough\" name=\"SympCough\" />";
            }

            if (SympRunnyNose.equals("1")) {
                SympRunnyNose = "<input type=\"checkbox\" id=\"SympRunnyNose\" name=\"SympRunnyNose\"  checked />";
            } else {
                SympRunnyNose = "<input type=\"checkbox\" id=\"SympRunnyNose\" name=\"SympRunnyNose\"  />";
            }

            if (SympNausea.equals("1")) {
                SympNausea = "<input type=\"checkbox\" id=\"SympNausea\" name=\"SympNausea\" checked  />";
            } else {
                SympNausea = "<input type=\"checkbox\" id=\"SympNausea\" name=\"SympNausea\"  />";
            }

            if (SympFluSymptoms.equals("1")) {
                SympFluSymptoms = "<input type=\"checkbox\" id=\"SympFluSymptoms\" name=\"SympFluSymptoms\"  checked/>";
            } else {
                SympFluSymptoms = "<input type=\"checkbox\" id=\"SympFluSymptoms\" name=\"SympFluSymptoms\"  />";
            }

            if (SympHeadache.equals("1")) {
                SympHeadache = "<input type=\"checkbox\" id=\"SympHeadache\" name=\"SympHeadache\"  checked/>";
            } else {
                SympHeadache = "<input type=\"checkbox\" id=\"SympHeadache\" name=\"SympHeadache\"  />";
            }

            if (SympLossTaste.equals("1")) {
                SympLossTaste = "<input type=\"checkbox\" id=\"SympLossTaste\" name=\"SympLossTaste\"  checked/>";
            } else {
                SympLossTaste = "<input type=\"checkbox\" id=\"SympLossTaste\" name=\"SympLossTaste\"  />";
            }

            if (SympShortBreath.equals("1")) {
                SympShortBreath = "<input type=\"checkbox\" id=\"SympShortBreath\" name=\"SympShortBreath\"  checked/>";
            } else {
                SympShortBreath = "<input type=\"checkbox\" id=\"SympShortBreath\" name=\"SympShortBreath\"  />";
            }

            if (SympCongestion.equals("1")) {
                SympCongestion = "<input type=\"checkbox\" id=\"SympCongestion\" name=\"SympCongestion\"  checked/>";
            } else {
                SympCongestion = "<input type=\"checkbox\" id=\"SympCongestion\" name=\"SympCongestion\"  />";
            }

            if (!AddInfoTextArea.equals("")) {
                AddInfoTextAreaN = "<textarea type=\"text\" class=\"form-control\" name=\"AddInfoTextArea\" id=\"AddInfoTextArea\" rows=\"3\" cols=\"5\">" + AddInfoTextArea + "</textarea>";
            } else {
                AddInfoTextAreaN = "<textarea type=\"text\" class=\"form-control\" name=\"AddInfoTextArea\" id=\"AddInfoTextArea\" rows=\"3\" cols=\"5\"></textarea>";
            }
//            if(SympEyeConjunctivitis.equals("1")){
//                SympEyeConjunctivitis = "<input type=\"checkbox\" id=\"SympEyeConjunctivitis\" name=\"SympEyeConjunctivitis\" checked />";
//            }else{
//                SympEyeConjunctivitis = "<input type=\"checkbox\" id=\"SympEyeConjunctivitis\" name=\"SympEyeConjunctivitis\" />";
//            }

            if (COVIDStatus.equals("1")) {
                COVIDStatusBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"COVIDStatus_Chk\" type=\"radio\" id=\"COVIDStatus_ChkP\" value=\"1\" checked />\n" +
                        "<label for=\"COVIDStatus_ChkP\">Positive</label>\n" +
                        "\n" +
                        "<input name=\"COVIDStatus_Chk\" type=\"radio\" id=\"COVIDStatus_ChkN\"value=\"0\" />\n" +
                        "<label for=\"COVIDStatus_ChkN\">Negative</label>\n" +
                        "\n" +
                        "<input name=\"COVIDStatus_Chk\" type=\"radio\" id=\"COVIDStatus_ChkS\"value=\"-1\" />\n" +
                        "<label for=\"COVIDStatus_ChkS\">Suspected</label>\t\n" +
                        "</div>\t\t\t\t\t\t\t\t\t \n");
            } else if (COVIDStatus.equals("0")) {
                COVIDStatusBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"COVIDStatus_Chk\" type=\"radio\" id=\"COVIDStatus_ChkP\" value=\"1\"  />\n" +
                        "<label for=\"COVIDStatus_ChkP\">Positive</label>\n" +
                        "\n" +
                        "<input name=\"COVIDStatus_Chk\" type=\"radio\" id=\"COVIDStatus_ChkN\"value=\"0\" checked />\n" +
                        "<label for=\"COVIDStatus_ChkN\">Negative</label>\n" +
                        "\n" +
                        "<input name=\"COVIDStatus_Chk\" type=\"radio\" id=\"COVIDStatus_ChkS\"value=\"-1\" />\n" +
                        "<label for=\"COVIDStatus_ChkS\">Suspected</label>\t\n" +
                        "</div>\t\t\t\t\t\t\t\t\t \n");
            } else if (COVIDStatus.equals("-1")) {
                COVIDStatusBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"COVIDStatus_Chk\" type=\"radio\" id=\"COVIDStatus_ChkP\" value=\"1\"  />\n" +
                        "<label for=\"COVIDStatus_ChkP\">Positive</label>\n" +
                        "\n" +
                        "<input name=\"COVIDStatus_Chk\" type=\"radio\" id=\"COVIDStatus_ChkN\"value=\"0\"  />\n" +
                        "<label for=\"COVIDStatus_ChkN\">Negative</label>\n" +
                        "\n" +
                        "<input name=\"COVIDStatus_Chk\" type=\"radio\" id=\"COVIDStatus_ChkS\"value=\"-1\" checked />\n" +
                        "<label for=\"COVIDStatus_ChkS\">Suspected</label>\t\n" +
                        "</div>\t\t\t\t\t\t\t\t\t \n");
            } else {
                COVIDStatusBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"COVIDStatus_Chk\" type=\"radio\" id=\"COVIDStatus_ChkP\" value=\"1\"  />\n" +
                        "<label for=\"COVIDStatus_ChkP\">Positive</label>\n" +
                        "\n" +
                        "<input name=\"COVIDStatus_Chk\" type=\"radio\" id=\"COVIDStatus_ChkN\"value=\"0\"  />\n" +
                        "<label for=\"COVIDStatus_ChkN\">Negative</label>\n" +
                        "\n" +
                        "<input name=\"COVIDStatus_Chk\" type=\"radio\" id=\"COVIDStatus_ChkS\"value=\"-1\" />\n" +
                        "<label for=\"COVIDStatus_ChkS\">Suspected</label>\t\n" +
                        "</div>\t\t\t\t\t\t\t\t\t \n");
            }

            Query = "Select Country from " + Database + ".Country";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            CountryBuff.append("<option value=''>Select Country</option>");
            while (rset.next()) {
                if (Country.equals(rset.getString(1))) {
                    CountryBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(1) + "</option>");
                } else {
                    CountryBuff.append("<option value=" + rset.getString(1) + ">" + rset.getString(1) + "</option>");
                }
            }
            rset.close();
            stmt.close();
            Query = "Select MaritalStatus from " + Database + ".MaritalStatus";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                if (MaritalStatus.equals(rset.getString(1))) {
                    MaritalStatusBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(1) + "</option>");
                } else {
                    MaritalStatusBuff.append("<option value=" + rset.getString(1) + ">" + rset.getString(1) + "</option>");
                }
            }
            rset.close();
            stmt.close();
            if (gender.equals("male")) {
                genderBuff.append("<select class=\"form-control\" id=\"gender\" name=\"gender\" style=\"color:black;\" required>\n" +
                        "<option value=\"\">Select Gender</option>\n" +
                        "<option value=\"male\" selected>Male</option>\n" +
                        "<option value=\"female\">Female</option>\n" +
                        "</select>\n");
            } else if (gender.equals("female")) {
                genderBuff.append("<select class=\"form-control\" id=\"gender\" name=\"gender\" style=\"color:black;\" required>\n" +
                        "<option value=\"\">Select Gender</option>\n" +
                        "<option value=\"male\">Male</option>\n" +
                        "<option value=\"female\" selected>Female</option>\n" +
                        "</select>\n");
            } else {
                genderBuff.append("<select class=\"form-control\" id=\"gender\" name=\"gender\" style=\"color:black;\" required>\n" +
                        "<option value=\"\">Select Gender</option>\n" +
                        "<option value=\"male\">Male</option>\n" +
                        "<option value=\"female\">Female</option>\n" +
                        "</select>\n");
            }

            if (ReasonVisit.trim().toUpperCase().equals("ANTIBODY TEST") || ReasonVisit.trim().toUpperCase().equals("RAPID ANTIGEN TEST") || ReasonVisit.trim().toUpperCase().equals("COVID PCR")) {
                ReasonVisitBuff.append("<select class=\"form-control\" id=\"ReasonVisitSelect\" name=\"ReasonVisitSelect\" style=\"color:black;\" onchange=\"DisplayReasonVisitField(this.value);\" required>\n" +
                        "<option value=\"\">Select Reason of Visit</option>\n" +
                        "<option value=\"Others\">Emergency</option>\n" +
                        "<option value=\"COVID\" selected>COVID-19</option>\n" +
                        "</select> \n");
            } else {
                ReasonVisitBuff.append("<select class=\"form-control\" id=\"ReasonVisitSelect\" name=\"ReasonVisitSelect\" style=\"color:black;\" onchange=\"DisplayReasonVisitField(this.value);\" required>\n" +
                        "<option value=\"\">Select Reason of Visit</option>\n" +
                        "<option value=\"Others\" selected>Emergency</option>\n" +
                        "<option value=\"COVID\" >COVID-19</option>\n" +
                        "</select>\n");
            }
            if (ClientId == 27 || ClientId == 29) {
                if (ReasonVisit.trim().toUpperCase().equals("ANTIBODY TEST") || ReasonVisit.trim().toUpperCase().equals("RAPID ANTIGEN TEST") || ReasonVisit.trim().toUpperCase().equals("COVID PCR")) {
                    Query = "Select ReasonVisit, Id from " + Database + ".ReasonVisits WHERE Catagory = 'COVID'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    ReasonVisitBuffN.append("<label><font color=\"black\">Reason For Visit </font></label>");
                    ReasonVisitBuffN.append("<select class=\"form-control\" id=\"ReasonVisit\" name=\"ReasonVisit\" style=\"color:black;\" >");
                    ReasonVisitBuffN.append("<option value=\"\">Select Reason of Visit</option>\n");
                    while (rset.next()) {
                        if (ReasonVisit.trim().toUpperCase().equals(rset.getString(1).trim().toUpperCase())) {
                            ReasonVisitBuffN.append("<option value=" + rset.getString(2) + " selected>" + rset.getString(1) + "</option>");
                        } else {
                            ReasonVisitBuffN.append("<option value=" + rset.getString(2) + ">" + rset.getString(1) + "</option>");
                        }
                    }
                    rset.close();
                    stmt.close();
                    ReasonVisitBuffN.append("</select>");
                } else {
                    Query = "Select ReasonVisit, Id from " + Database + ".ReasonVisits WHERE Catagory = 'Others'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    ReasonVisitBuffN.append("<label><font color=\"black\">Reason For Visit </font></label>");
                    ReasonVisitBuffN.append("<select class=\"form-control\" id=\"ReasonVisit\" name=\"ReasonVisit\" style=\"color:black;\" >");
                    ReasonVisitBuffN.append("<option value=\"\">Select Reason of Visit</option>\n");
                    while (rset.next()) {
                        if (ReasonVisit.trim().toUpperCase().equals(rset.getString(1).trim().toUpperCase())) {
                            ReasonVisitBuffN.append("<option value=" + rset.getString(2) + " selected>" + rset.getString(1) + "</option>");
                        } else {
                            ReasonVisitBuffN.append("<option value=" + rset.getString(2) + ">" + rset.getString(1) + "</option>");
                        }
                    }
                    rset.close();
                    stmt.close();
                    ReasonVisitBuffN.append("</select>");

                }
            } else {
                ReasonVisitBuffN.append("<label><font color=\"black\">Reason For Visit </font></label>");
                ReasonVisitBuffN.append("<input type=\"text\" placeholder=\"\" class=\"form-control\"id=\"ReasonVisit\" name=\"ReasonVisit\" value=\"" + ReasonVisit.trim() + "\" >");
            }

//            if (SelfPayChk == 1) {
//                SelfPayChkBuff.append("<input type=\"checkbox\" id=\"SelfPayChk\" name=\"SelfPayChk\" onclick=\"InsuranceShow(this)\" checked />");
//            } else {
//                SelfPayChkBuff.append("<input type=\"checkbox\" id=\"SelfPayChk\" name=\"SelfPayChk\" onclick=\"InsuranceShow(this)\" />");
//            }


            if (SelfPayChk == 1) {
                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%Y-%m-%d'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%Y-%m-%d'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from " + Database + ".InsuranceInfo  where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    WorkersCompPolicy = rset.getInt(1);
                    MotorVehAccident = rset.getInt(2);

                    PriInsurance = rset.getString(3);
                    MemId = rset.getString(4);
                    GrpNumber = rset.getString(5);
                    PriInsuranceName = rset.getString(6);
                    AddressIfDifferent = rset.getString(7);
                    PrimaryDOB = rset.getString(8);
                    PrimarySSN = rset.getString(9);
                    PatientRelationtoPrimary = rset.getString(10);
                    PrimaryOccupation = rset.getString(11);
                    PrimaryEmployer = rset.getString(12);
                    EmployerAddress = rset.getString(13);
                    EmployerPhone = rset.getString(14);
                    SecondryInsurance = rset.getString(15);
                    SubscriberName = rset.getString(16);
                    SubscriberDOB = rset.getString(17);
                    PatientRelationshiptoSecondry = rset.getString(18);
                    MemberID_2 = rset.getString(19);
                    GroupNumber_2 = rset.getString(20);
                }
                rset.close();
                stmt.close();
            }
            if (WorkersCompPolicy == 0) {
                WorkersCompPolicyBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"WorkersCompPolicy\" type=\"radio\" id=\"WorkersCompPolicyY\" value=\"1\" />\n" +
                        "<label for=\"WorkersCompPolicyY\">Yes</label>\n" +
                        "\n" +
                        "<input name=\"WorkersCompPolicy\" type=\"radio\" id=\"WorkersCompPolicyN\"value=\"0\" checked/>\n" +
                        "<label for=\"WorkersCompPolicyN\">No</label>\t\n" +
                        "</div> \n");
            } else if (WorkersCompPolicy == 1) {
                WorkersCompPolicyBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"WorkersCompPolicy\" type=\"radio\" id=\"WorkersCompPolicyY\" value=\"1\" checked />\n" +
                        "<label for=\"WorkersCompPolicyY\">Yes</label>\n" +
                        "\n" +
                        "<input name=\"WorkersCompPolicy\" type=\"radio\" id=\"WorkersCompPolicyN\"value=\"0\" />\n" +
                        "<label for=\"WorkersCompPolicyN\">No</label>\t\n" +
                        "</div> \n");
            } else {
                WorkersCompPolicyBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"WorkersCompPolicy\" type=\"radio\" id=\"WorkersCompPolicyY\" value=\"1\" />\n" +
                        "<label for=\"WorkersCompPolicyY\">Yes</label>\n" +
                        "\n" +
                        "<input name=\"WorkersCompPolicy\" type=\"radio\" id=\"WorkersCompPolicyN\"value=\"0\" />\n" +
                        "<label for=\"WorkersCompPolicyN\">No</label>\t\n" +
                        "</div> \n");
            }
            if (MotorVehAccident == 0) {
                MotorVehAccidentBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"MotorVehAccident\" type=\"radio\" id=\"MotorVehAccidentY\" value=\"1\" />\n" +
                        "<label for=\"MotorVehAccidentY\">Yes</label>\n" +
                        "\n" +
                        "<input name=\"MotorVehAccident\" type=\"radio\" id=\"MotorVehAccidentN\"value=\"0\" checked/>\n" +
                        "<label for=\"MotorVehAccidentN\">No</label>\t\n" +
                        "</div>");
            } else if (MotorVehAccident == 1) {
                MotorVehAccidentBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"MotorVehAccident\" type=\"radio\" id=\"MotorVehAccidentY\" value=\"1\" checked />\n" +
                        "<label for=\"MotorVehAccidentY\">Yes</label>\n" +
                        "\n" +
                        "<input name=\"MotorVehAccident\" type=\"radio\" id=\"MotorVehAccidentN\"value=\"0\" />\n" +
                        "<label for=\"MotorVehAccidentN\">No</label>\t\n" +
                        "</div>");
            } else {
                MotorVehAccidentBuff.append("<div class=\"demo-radio-button\">\n" +
                        "<input name=\"MotorVehAccident\" type=\"radio\" id=\"MotorVehAccidentY\" value=\"1\" />\n" +
                        "<label for=\"MotorVehAccidentY\">Yes</label>\n" +
                        "\n" +
                        "<input name=\"MotorVehAccident\" type=\"radio\" id=\"MotorVehAccidentN\"value=\"0\" />\n" +
                        "<label for=\"MotorVehAccidentN\">No</label>\t\n" +
                        "</div>");
            }
            Query = "Select Id, PayerId, PayerName from " + Database + ".ProfessionalPayers";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PriInsuranceNameBuff.append("<option value=''>--------------</option>");
            while (rset.next()) {
                if (PriInsuranceName.equals(rset.getString(1))) {
                    PriInsuranceNameBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(3) + "</option>");
                } else {
                    PriInsuranceNameBuff.append("<option value=" + rset.getString(1) + ">" + rset.getString(3) + "</option>");
                }
            }
            rset.close();
            stmt.close();

            Query = "Select Id, PayerId, PayerName from " + Database + ".ProfessionalPayers";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            SecondryInsuranceBuff.append("<option value=''>--------------</option>");
            while (rset.next()) {
                if (SecondryInsurance.equals(rset.getString(1))) {
                    SecondryInsuranceBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(3) + "</option>");
                } else {
                    SecondryInsuranceBuff.append("<option value=" + rset.getString(1) + ">" + rset.getString(3) + "</option>");
                }
            }
            rset.close();
            stmt.close();
            Query = "Select PatientRelation from " + Database + ".PatientRelation";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            //PatientRelationtoPrimaryBuff.append("<option value=\"\">Select One</option>");
            while (rset.next()) {
                if (PatientRelationtoPrimary.equals(rset.getString(1))) {
                    PatientRelationtoPrimaryBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(1) + "</option>");
                }
                PatientRelationtoPrimaryBuff.append("<option value=" + rset.getString(1) + " >" + rset.getString(1) + "</option>");
            }
            rset.close();
            stmt.close();
            Query = "Select PatientRelation from " + Database + ".PatientRelation";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            //PatientRelationshiptoSecondryBuff.append("<option value=\"\">Select One</option>");
            while (rset.next()) {
                if (PatientRelationshiptoSecondry.equals(rset.getString(1))) {
                    PatientRelationshiptoSecondryBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(1) + "</option>");
                }
                PatientRelationshiptoSecondryBuff.append("<option value=" + rset.getString(1) + " >" + rset.getString(1) + "</option>");
            }
            rset.close();
            stmt.close();

            if (ClientId == 10 || ClientId == 15) {
                Query = "Select IFNULL(AdmissionBundle,'0') from " + Database + ".PatientAdmissionBundle where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    if (rset.getInt(1) == 1) {
                        AdmissionBundleBuff.append("<select class=\"form-control select2\" id=\"AddmissionBundle\" name=\"AddmissionBundle\" style=\"color:black;\" required>\n" +
                                "<option value=\"0\">Please Select Any </option>\n" +
                                "<option value=\"1\" selected>Aetna Insurance </option>\n" +
                                "<option value=\"2\">Blue Cross Blue Shield</option>\n" +
                                "<option value=\"3\">United Healthcare </option>\n" +
                                "<option value=\"4\">Other Insurance </option>\n" +
                                "</select>\t\t\t\t\t\t\t\t\t  \n");
                    } else if (rset.getInt(1) == 2) {
                        AdmissionBundleBuff.append("<select class=\"form-control select2\" id=\"AddmissionBundle\" name=\"AddmissionBundle\" style=\"color:black;\" required>\n" +
                                "<option value=\"0\">Please Select Any </option>\n" +
                                "<option value=\"1\" >Aetna Insurance </option>\n" +
                                "<option value=\"2\" selected>Blue Cross Blue Shield</option>\n" +
                                "<option value=\"3\">United Healthcare </option>\n" +
                                "<option value=\"4\">Other Insurance </option>\n" +
                                "</select>\t\t\t\t\t\t\t\t\t  \n");
                    } else if (rset.getInt(1) == 3) {
                        AdmissionBundleBuff.append("<select class=\"form-control select2\" id=\"AddmissionBundle\" name=\"AddmissionBundle\" style=\"color:black;\" required>\n" +
                                "<option value=\"0\">Please Select Any </option>\n" +
                                "<option value=\"1\" >Aetna Insurance </option>\n" +
                                "<option value=\"2\" >Blue Cross Blue Shield</option>\n" +
                                "<option value=\"3\" selected>United Healthcare </option>\n" +
                                "<option value=\"4\">Other Insurance </option>\n" +
                                "</select>\t\t\t\t\t\t\t\t\t  \n");
                    } else if (rset.getInt(1) == 4) {
                        AdmissionBundleBuff.append("<select class=\"form-control select2\" id=\"AddmissionBundle\" name=\"AddmissionBundle\" style=\"color:black;\" required>\n" +
                                "<option value=\"0\">Please Select Any </option>\n" +
                                "<option value=\"1\" >Aetna Insurance </option>\n" +
                                "<option value=\"2\" >Blue Cross Blue Shield</option>\n" +
                                "<option value=\"3\" >United Healthcare </option>\n" +
                                "<option value=\"4\" selected>Other Insurance </option>\n" +
                                "</select>\t\t\t\t\t\t\t\t\t  \n");
                    } else {
                        AdmissionBundleBuff.append("<select class=\"form-control select2\" id=\"AddmissionBundle\" name=\"AddmissionBundle\" style=\"color:black;\" required>\n" +
                                "<option value=\"0\">Please Select Any </option>\n" +
                                "<option value=\"1\" >Aetna Insurance </option>\n" +
                                "<option value=\"2\" >Blue Cross Blue Shield</option>\n" +
                                "<option value=\"3\" >United Healthcare </option>\n" +
                                "<option value=\"4\" >Other Insurance </option>\n" +
                                "</select>\t\t\t\t\t\t\t\t\t  \n");
                    }
                }
                rset.close();
                stmt.close();
            }

            if (PriInsurance.trim().equals("Medicare")) {
                PriInsuranceBuff.append("<select class=\"form-control\" id=\"PriInsurance\" name=\"PriInsurance\" style=\"color:black;\">\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
                        "<option value=\"\">Select one</option>\n" +
                        "<option value=\"Medicare\" selected>Medicare</option>\n" +
                        "<option value=\"Medicaid\">Medicaid</option>\n" +
                        "<option value=\"Others\">Others</option>\n" +
                        "</select>");
            } else if (PriInsurance.trim().equals("Medicaid")) {
                PriInsuranceBuff.append("<select class=\"form-control\" id=\"PriInsurance\" name=\"PriInsurance\" style=\"color:black;\">\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
                        "<option value=\"\">Select one</option>\n" +
                        "<option value=\"Medicare\" >Medicare</option>\n" +
                        "<option value=\"Medicaid\" selected>Medicaid</option>\n" +
                        "<option value=\"Others\">Others</option>\n" +
                        "</select>");
            } else if (PriInsurance.trim().equals("Others")) {
                PriInsuranceBuff.append("<select class=\"form-control\" id=\"PriInsurance\" name=\"PriInsurance\" style=\"color:black;\">\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
                        "<option value=\"\">Select one</option>\n" +
                        "<option value=\"Medicare\" >Medicare</option>\n" +
                        "<option value=\"Medicaid\" >Medicaid</option>\n" +
                        "<option value=\"Others\" selected>Others</option>\n" +
                        "</select>");
            } else {
                PriInsuranceBuff.append("<select class=\"form-control\" id=\"PriInsurance\" name=\"PriInsurance\" style=\"color:black;\">\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
                        "<option value=\"\">Select one</option>\n" +
                        "<option value=\"Medicare\" >Medicare</option>\n" +
                        "<option value=\"Medicaid\" >Medicaid</option>\n" +
                        "<option value=\"Others\" >Others</option>\n" +
                        "</select>");
            }

            Query = " Select IFNULL(NextofKinName,'-'), IFNULL(RelationToPatient,'-'), IFNULL(PhoneNumber,'-'),  IFNULL(LeaveMessage,0), IFNULL(Address,'-'), IFNULL(City,'-'), IFNULL(State,'-'), IFNULL(Country,'-'), IFNULL(ZipCode,'-')  from " + Database + ".EmergencyInfo where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                NextofKinName = rset.getString(1);
                RelationToPatientER = rset.getString(2);
                PhoneNumberER = rset.getString(3);
                LeaveMessageER = rset.getInt(4);
                if (LeaveMessageER == 0) {
                    LeaveMessageERBuff.append("<select class=\"form-control\" id=\"LeaveMessageER\" name=\"LeaveMessageER\" style=\"color:black;\">\n" +
                            "<option value=\"0\" selected>No</option>\n" +
                            "<option value=\"1\">Yes</option>\n" +
                            "</select>\t\t\t\t\t\t\t\t  \n");
                } else {
                    LeaveMessageERBuff.append("<select class=\"form-control\" id=\"LeaveMessageER\" name=\"LeaveMessageER\" style=\"color:black;\">\n" +
                            "<option value=\"0\" >No</option>\n" +
                            "<option value=\"1\" selected>Yes</option>\n" +
                            "</select>\t\t\t\t\t\t\t\t  \n");
                }
                AddressER = rset.getString(5);
                CityER = rset.getString(6);
                StateER = rset.getString(7);
                CountryER = rset.getString(8);
                ZipCodeER = rset.getString(9);
            }
            rset.close();
            stmt.close();
            Query = "Select Country from " + Database + ".Country";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            CountryBuffER.append("<option value=''>Select Country</option>");
            while (rset.next()) {
                if (CountryER.equals(rset.getString(1))) {
                    CountryBuffER.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(1) + "</option>");
                } else {
                    CountryBuffER.append("<option value=" + rset.getString(1) + ">" + rset.getString(1) + "</option>");
                }
            }
            rset.close();
            stmt.close();
            Query = " Select IFNULL(PatientSign,'-'), DATE_FORMAT(Date,'%Y-%m-%d'), IFNULL(Witness,'-'), IFNULL(PatientBehalfSign,'-'), IFNULL(RelativeSign,'-'), DATE_FORMAT(Date2,'%Y-%m-%d'), IFNULL(Witness2,'-') from " + Database + ".ConcentToTreatmentInfo  Where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientSignConcent = rset.getString(1);
                DateConcent = rset.getString(2);
                WitnessConcent = rset.getString(3);
                PatientBehalfConcent = rset.getString(4);
                RelativeSignConcent = rset.getString(5);
                DateConcent2 = rset.getString(6);
                WitnessConcent2 = rset.getString(7);
            }
            rset.close();
            stmt.close();
            if (ClientId == 27 || ClientId == 29) {
                Query = " Select FrVisitedBefore, FrFamiliyVisitedBefore, FrInternet, FrBillboard, FrGoogle, FrBuildingSignage, FrFacebook, FrLivesNear," +
                        "  FrTwitter, FrTV, FrMapSearch, FrEvent, IFNULL(FrPhysicianReferral,''), IFNULL(FrNeurologyReferral,'-'), IFNULL(FrUrgentCareReferral,''), " +
                        " IFNULL(FrOrganizationReferral,''), IFNULL(FrFriendFamily,'-') from " + Database + ".RandomCheckInfo where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    if (rset.getInt(1) == 0) {
                        FrVisitedBeforeN.append("<div class=\"demo-radio-button\">\n" +
                                "<input name=\"FrVisitedBefore\" type=\"radio\" id=\"FrVisitedBeforeY\" value=\"1\" />\n" +
                                "<label for=\"FrVisitedBeforeY\">Yes</label>\n" +
                                "\n" +
                                "<input name=\"FrVisitedBefore\" type=\"radio\" id=\"FrVisitedBeforeN\"value=\"0\" checked/>\n" +
                                "<label for=\"FrVisitedBeforeN\">No</label>\t\n" +
                                "</div>\t\t\t\t\t\t\t\t\t \n");
                    } else if (rset.getInt(1) == 1) {
                        FrVisitedBeforeN.append("<div class=\"demo-radio-button\">\n" +
                                "<input name=\"FrVisitedBefore\" type=\"radio\" id=\"FrVisitedBeforeY\" value=\"1\" checked/>\n" +
                                "<label for=\"FrVisitedBeforeY\">Yes</label>\n" +
                                "\n" +
                                "<input name=\"FrVisitedBefore\" type=\"radio\" id=\"FrVisitedBeforeN\"value=\"0\" />\n" +
                                "<label for=\"FrVisitedBeforeN\">No</label>\t\n" +
                                "</div>\t\t\t\t\t\t\t\t\t \n");
                    } else {
                        FrVisitedBeforeN.append("<div class=\"demo-radio-button\">\n" +
                                "<input name=\"FrVisitedBefore\" type=\"radio\" id=\"FrVisitedBeforeY\" value=\"1\" />\n" +
                                "<label for=\"FrVisitedBeforeY\">Yes</label>\n" +
                                "\n" +
                                "<input name=\"FrVisitedBefore\" type=\"radio\" id=\"FrVisitedBeforeN\"value=\"0\" />\n" +
                                "<label for=\"FrVisitedBeforeN\">No</label>\t\n" +
                                "</div>\t\t\t\t\t\t\t\t\t \n");
                    }

                    if (rset.getInt(2) == 0) {
                        FrFamiliyVisitedBeforeN.append("<div class=\"demo-radio-button\">\n" +
                                "<input name=\"FrFamiliyVisitedBefore\" type=\"radio\" id=\"FrFamiliyVisitedBeforeY\" value=\"1\" />\n" +
                                "<label for=\"FrFamiliyVisitedBeforeY\">Yes</label>\n" +
                                "\n" +
                                "<input name=\"FrFamiliyVisitedBefore\" type=\"radio\" id=\"FrFamiliyVisitedBeforeN\"value=\"0\" checked />\n" +
                                "<label for=\"FrFamiliyVisitedBeforeN\">No</label>\t\n" +
                                "</div>\t\t\t\t\t\t\t\t\t \n");
                    } else if (rset.getInt(2) == 1) {
                        FrFamiliyVisitedBeforeN.append("<div class=\"demo-radio-button\">\n" +
                                "<input name=\"FrFamiliyVisitedBefore\" type=\"radio\" id=\"FrFamiliyVisitedBeforeY\" value=\"1\" checked/>\n" +
                                "<label for=\"FrFamiliyVisitedBeforeY\">Yes</label>\n" +
                                "\n" +
                                "<input name=\"FrFamiliyVisitedBefore\" type=\"radio\" id=\"FrFamiliyVisitedBeforeN\"value=\"0\"  />\n" +
                                "<label for=\"FrFamiliyVisitedBeforeN\">No</label>\t\n" +
                                "</div>\t\t\t\t\t\t\t\t\t \n");
                    } else {
                        FrFamiliyVisitedBeforeN.append("<div class=\"demo-radio-button\">\n" +
                                "<input name=\"FrFamiliyVisitedBefore\" type=\"radio\" id=\"FrFamiliyVisitedBeforeY\" value=\"1\" />\n" +
                                "<label for=\"FrFamiliyVisitedBeforeY\">Yes</label>\n" +
                                "\n" +
                                "<input name=\"FrFamiliyVisitedBefore\" type=\"radio\" id=\"FrFamiliyVisitedBeforeN\"value=\"0\"  />\n" +
                                "<label for=\"FrFamiliyVisitedBeforeN\">No</label>\t\n" +
                                "</div>\t\t\t\t\t\t\t\t\t \n");
                    }
                    if (rset.getInt(3) == 1) {
                        FrInternetN.append("<input type=\"checkbox\" id=\"FrInternet\" name=\"FrInternet\" checked/>");
                    } else {
                        FrInternetN.append("<input type=\"checkbox\" id=\"FrInternet\" name=\"FrInternet\"/>");
                    }
                    if (rset.getInt(4) == 1) {
                        FrBillboardN.append("<input type=\"checkbox\" id=\"FrBillboard\" name=\"FrBillboard\" checked/>");
                    } else {
                        FrBillboardN.append("<input type=\"checkbox\" id=\"FrBillboard\" name=\"FrBillboard\" />");
                    }
                    if (rset.getInt(5) == 1) {
                        FrGoogleN.append("<input type=\"checkbox\" id=\"FrGoogle\" name=\"FrGoogle\" checked/>");
                    } else {
                        FrGoogleN.append("<input type=\"checkbox\" id=\"FrGoogle\" name=\"FrGoogle\" />");
                    }
                    if (rset.getInt(6) == 1) {
                        FrBuildingSignageN.append("<input type=\"checkbox\" id=\"FrBuildingSignage\" name=\"FrBuildingSignage\" checked/>");
                    } else {
                        FrBuildingSignageN.append("<input type=\"checkbox\" id=\"FrBuildingSignage\" name=\"FrBuildingSignage\" />");
                    }
                    if (rset.getInt(7) == 1) {
                        FrFacebookN.append("<input type=\"checkbox\" id=\"FrFacebook\" name=\"FrFacebook\" checked/>");
                    } else {
                        FrFacebookN.append("<input type=\"checkbox\" id=\"FrFacebook\" name=\"FrFacebook\" />");
                    }
                    if (rset.getInt(8) == 1) {
                        FrLivesNearN.append("<input type=\"checkbox\" id=\"FrLivesNear\" name=\"FrLivesNear\" checked/>");
                    } else {
                        FrLivesNearN.append("<input type=\"checkbox\" id=\"FrLivesNear\" name=\"FrLivesNear\" checked/>");
                    }
                    if (rset.getInt(9) == 1) {
                        FrTwitterN.append("<input type=\"checkbox\" id=\"FrTwitter\" name=\"FrTwitter\" checked/>");
                    } else {
                        FrTwitterN.append("<input type=\"checkbox\" id=\"FrTwitter\" name=\"FrTwitter\" checked/>");
                    }
                    if (rset.getInt(10) == 1) {
                        FrTVN.append("<input type=\"checkbox\" id=\"FrTV\" name=\"FrTV\" checked/>");
                    } else {
                        FrTVN.append("<input type=\"checkbox\" id=\"FrTV\" name=\"FrTV\" />");
                    }
                    if (rset.getInt(11) == 1) {
                        FrMapSearchN.append("<input type=\"checkbox\" id=\"FrMapSearch\" name=\"FrMapSearch\" checked/>");
                    } else {
                        FrMapSearchN.append("<input type=\"checkbox\" id=\"FrMapSearch\" name=\"FrMapSearch\" />");
                    }
                    if (rset.getInt(12) == 1) {
                        FrEventN.append("<input type=\"checkbox\" id=\"FrEvent\" name=\"FrEvent\" checked/>");
                    } else {
                        FrEventN.append("<input type=\"checkbox\" id=\"FrEvent\" name=\"FrEvent\" />");
                    }
                    FrPhysicianReferral = rset.getString(13).trim();
                    FrNeurologyReferral = rset.getString(14).trim();
                    FrUrgentCareReferral = rset.getString(15).trim();
                    FrOrganizationReferral = rset.getString(16).trim();
                    FrFriendFamily = rset.getString(17).trim();
                }
                rset.close();
                stmt.close();

            }
            Query = " Select ReturnPatient, Google, MapSearch, Billboard, OnlineReview, TV, Website, BuildingSignDriveBy, Facebook, School, IFNULL(School_text ,'-'), Twitter, Magazine, IFNULL(Magazine_text,'-'), Newspaper, IFNULL(Newspaper_text,'-'), FamilyFriend, IFNULL(FamilyFriend_text,'-'), UrgentCare, IFNULL(UrgentCare_text,'-'), CommunityEvent, IFNULL(CommunityEvent_text,'-'),  Work_text, Physician_text, Other_text from " + Database + ".RandomCheckInfo where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                if (rset.getInt(1) == 0) {
                    ReturnPatient.append("<input type=\"checkbox\" id=\"ReturnPatient\" name=\"ReturnPatient\"/>");
                } else {
                    ReturnPatient.append("<input type=\"checkbox\" id=\"ReturnPatient\" name=\"ReturnPatient\" checked/>");
                }
                if (rset.getInt(2) == 0) {
                    Google.append("<input type=\"checkbox\" id=\"Google\" name=\"Google\">");
                } else {
                    Google.append("<input type=\"checkbox\" id=\"Google\" name=\"Google\" checked>");
                }
                if (rset.getInt(3) == 0) {
                    MapSearch.append("<input type=\"checkbox\" id=\"MapSearch\" name=\"MapSearch\">");
                } else {
                    MapSearch.append("<input type=\"checkbox\" id=\"MapSearch\" name=\"MapSearch\" checked>");
                }
                if (rset.getInt(4) == 0) {
                    Billboard.append("<input type=\"checkbox\" id=\"Billboard\" name=\"Billboard\">");
                } else {
                    Billboard.append("<input type=\"checkbox\" id=\"Billboard\" name=\"Billboard\" checked>");
                }
                if (rset.getInt(5) == 0) {
                    OnlineReview.append("<input type=\"checkbox\" id=\"OnlineReview\" name=\"OnlineReview\">");
                } else {
                    OnlineReview.append("<input type=\"checkbox\" id=\"OnlineReview\" name=\"OnlineReview\" checked>");
                }
                if (rset.getInt(6) == 0) {
                    TV.append("<input type=\"checkbox\" id=\"TV\" name=\"TV\">");
                } else {
                    TV.append("<input type=\"checkbox\" id=\"TV\" name=\"TV\" checked > ");
                }
                if (rset.getInt(7) == 0) {
                    Website.append("<input type=\"checkbox\" id=\"Website\" name=\"Website\">");
                } else {
                    Website.append("<input type=\"checkbox\" id=\"Website\" name=\"Website\" checked>");
                }
                if (rset.getInt(8) == 0) {
                    BuildingSignDriveBy.append("<input type=\"checkbox\" id=\"BuildingSignDriveBy\" name=\"BuildingSignDriveBy\">");
                } else {
                    BuildingSignDriveBy.append("<input type=\"checkbox\" id=\"BuildingSignDriveBy\" name=\"BuildingSignDriveBy\" checked>");
                }
                if (rset.getInt(9) == 0) {
                    Facebook.append("<input type=\"checkbox\" id=\"Facebook\" name=\"Facebook\">");
                } else {
                    Facebook.append("<input type=\"checkbox\" id=\"Facebook\" name=\"Facebook\" checked>");
                }
                if (rset.getInt(10) == 0) {
                    School.append("<input type=\"checkbox\" id=\"School\" name=\"School\">");
                    School_text = "";
                } else {
                    School.append("<input type=\"checkbox\" id=\"School\" name=\"School\" checked>");
                    School_text = rset.getString(11);
                }
                if (rset.getInt(12) == 0) {
                    Twitter.append("<input type=\"checkbox\" id=\"Twitter\" name=\"Twitter\">");
                } else {
                    Twitter.append("<input type=\"checkbox\" id=\"Twitter\" name=\"Twitter\" checked>");
                }
                if (rset.getInt(13) == 0) {
                    Magazine.append("<input type=\"checkbox\" id=\"Magazine\" name=\"Magazine\">");
                    Magazine_text = "";
                } else {
                    Magazine.append("<input type=\"checkbox\" id=\"Magazine\" name=\"Magazine\" checked>");
                    Magazine_text = rset.getString(14);
                }
                if (rset.getInt(15) == 0) {
                    Newspaper.append("<input type=\"checkbox\" id=\"Newspaper\" name=\"Newspaper\">");
                    Newspaper_text = "";
                } else {
                    Newspaper.append("<input type=\"checkbox\" id=\"Newspaper\" name=\"Newspaper\" checked>");
                    Newspaper_text = rset.getString(16);
                }
                if (rset.getInt(17) == 0) {
                    FamilyFriend.append("<input type=\"checkbox\" id=\"FamilyFriend\" name=\"FamilyFriend\">");
                    FamilyFriend_text = "";
                } else {
                    FamilyFriend.append("<input type=\"checkbox\" id=\"FamilyFriend\" name=\"FamilyFriend\" checked>");
                    FamilyFriend_text = rset.getString(18);
                }
                if (rset.getInt(19) == 0) {
                    UrgentCare.append("<input type=\"checkbox\" id=\"UrgentCare\" name=\"UrgentCare\">");
                    UrgentCare_text = "";
                } else {
                    UrgentCare.append("<input type=\"checkbox\" id=\"UrgentCare\" name=\"UrgentCare\" checked>");
                    UrgentCare_text = rset.getString(20);
                }
                if (rset.getInt(21) == 0) {
                    CommunityEvent.append("<input type=\"checkbox\" id=\"CommunityEvent\">");
                    CommunityEvent_text = "";
                } else {
                    CommunityEvent.append("<input type=\"checkbox\" id=\"CommunityEvent\" checked>");
                    CommunityEvent_text = rset.getString(22);
                }
                if (rset.getString(23) == "" || rset.getString(23) == null || rset.getString(23).equals("")) {
//                    System.out.println("Physician_text IF condition");
                    Work_text = "";
                    Work_textBuff.append("<input type=\"checkbox\" id=\"Work\" name=\"Work\">");
                } else {
//                    System.out.println("Physician_text Else condition");
                    Work_textBuff.append("<input type=\"checkbox\" id=\"Work\" name=\"Work\" checked>");
                    Work_text = rset.getString(23);
                }
                if (rset.getString(24) == "" || rset.getString(24) == null || rset.getString(24).equals("")) {
//                    System.out.println("Physician_text IF condition");
                    Physician_text = "";
                    Physician_textBuff.append("<input type=\"checkbox\" id=\"Physician\" name=\"Physician\">");
                } else {
//                    System.out.println("Physician_text else condition");
                    Physician_text = rset.getString(24);
                    Physician_textBuff.append("<input type=\"checkbox\" id=\"Physician\" name=\"Physician\" checked>");
                }
                if (rset.getString(25) == "" || rset.getString(25) == null || rset.getString(25).equals("")) {
//                    System.out.println("OtherText IF condition");
                    Other_text = "";
                    Other_textBuff.append("<input type=\"checkbox\" id=\"Other\" name=\"Other\">");
                } else {
//                    System.out.println("OtherText else condition");
                    Other_text = rset.getString(25);
                    Other_textBuff.append("<input type=\"checkbox\" id=\"Other\" name=\"Other\" checked>");
                }
            }
            rset.close();
            stmt.close();

            String Date = "";
            Query = "Select Date_format(now(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("LastName", String.valueOf(LastName));
            Parser.SetField("FirstName", String.valueOf(FirstName));
            Parser.SetField("MiddleInitial", String.valueOf(MiddleInitial));
            Parser.SetField("TitleBuff", String.valueOf(TitleBuff));
            Parser.SetField("DOB", String.valueOf(DOB));
            Parser.SetField("Age", String.valueOf(Age));
            Parser.SetField("genderBuff", String.valueOf(genderBuff));
            Parser.SetField("Email", String.valueOf(Email));
            Parser.SetField("MaritalStatusBuff", String.valueOf(MaritalStatusBuff));
            Parser.SetField("PhNumber", String.valueOf(PhNumber));
            Parser.SetField("Address", String.valueOf(Address));
            Parser.SetField("Address2", String.valueOf(Address2));
            Parser.SetField("StreetAddress2", String.valueOf(StreetAddress2));
            Parser.SetField("City", String.valueOf(City));
            Parser.SetField("State", String.valueOf(State));
            Parser.SetField("County", String.valueOf(County));
            Parser.SetField("Country", String.valueOf(Country));
            Parser.SetField("ZipCode", String.valueOf(ZipCode));
            Parser.SetField("EthnicityBuff", String.valueOf(EthnicityBuff));
            Parser.SetField("DoctorList", String.valueOf(DoctorList));
            Parser.SetField("COVIDStatusBuff", String.valueOf(COVIDStatusBuff));
            Parser.SetField("RaceBuff", String.valueOf(RaceBuff));
            Parser.SetField("SSN", String.valueOf(SSN));
            Parser.SetField("SpCarePhy", String.valueOf(SpCarePhy));
            Parser.SetField("ReasonVisitBuff", String.valueOf(ReasonVisitBuff));
            Parser.SetField("ReasonVisitBuffN", String.valueOf(ReasonVisitBuffN));
            Parser.SetField("ReasonVisit", String.valueOf(ReasonVisit));
            Parser.SetField("PriCarePhy", String.valueOf(PriCarePhy));
            Parser.SetField("Employer", String.valueOf(Employer));
            Parser.SetField("Occupation", String.valueOf(Occupation));
            Parser.SetField("EmpContact", String.valueOf(EmpContact));
            Parser.SetField("TravellingChkBuff", String.valueOf(TravellingChkBuff));
            Parser.SetField("TravelWhen", String.valueOf(TravelWhen));
            Parser.SetField("TravelWhere", String.valueOf(TravelWhere));
            Parser.SetField("TravelHowLong", String.valueOf(TravelHowLong));
            Parser.SetField("COVIDExposedChkBuff", String.valueOf(COVIDExposedChkBuff));
            Parser.SetField("CovidExpWhen", String.valueOf(CovidExpWhen));
            Parser.SetField("SympFever", String.valueOf(SympFever));
            Parser.SetField("SympBodyAches", String.valueOf(SympBodyAches));
            Parser.SetField("SympSoreThroat", String.valueOf(SympSoreThroat));
            Parser.SetField("SympFatigue", String.valueOf(SympFatigue));
            Parser.SetField("SympRash", String.valueOf(SympRash));
            Parser.SetField("SympVomiting", String.valueOf(SympVomiting));
            Parser.SetField("SympDiarrhea", String.valueOf(SympDiarrhea));
            Parser.SetField("SympCough", String.valueOf(SympCough));
            Parser.SetField("SympRunnyNose", String.valueOf(SympRunnyNose));
            Parser.SetField("SympNausea", String.valueOf(SympNausea));
            Parser.SetField("SympFluSymptoms", String.valueOf(SympFluSymptoms));
            Parser.SetField("SympHeadache", String.valueOf(SympHeadache));
            Parser.SetField("SympLossTaste", String.valueOf(SympLossTaste));
            Parser.SetField("SympShortBreath", String.valueOf(SympShortBreath));
            Parser.SetField("SympCongestion", String.valueOf(SympCongestion));
            Parser.SetField("AddInfoTextAreaN", String.valueOf(AddInfoTextAreaN));
            Parser.SetField("GuarantorName", String.valueOf(GuarantorName));
            Parser.SetField("GuarantorDOB", String.valueOf(GuarantorDOB));
            Parser.SetField("GuarantorNumber", String.valueOf(GuarantorNumber));
            Parser.SetField("GuarantorSSN", String.valueOf(GuarantorSSN));
//            Parser.SetField("SympEyeConjunctivitis", String.valueOf(SympEyeConjunctivitis));
            Parser.SetField("SelfPayChkBuff", String.valueOf(SelfPayChkBuff));
            Parser.SetField("WorkersCompPolicyBuff", String.valueOf(WorkersCompPolicyBuff));
            Parser.SetField("MotorVehAccidentBuff", String.valueOf(MotorVehAccidentBuff));
            Parser.SetField("AdmissionBundleBuff", String.valueOf(AdmissionBundleBuff));
            Parser.SetField("PriInsuranceNameBuff", String.valueOf(PriInsuranceNameBuff));
            Parser.SetField("PriInsuranceBuff", String.valueOf(PriInsuranceBuff));
            Parser.SetField("MemId", String.valueOf(MemId));
            Parser.SetField("GrpNumber", String.valueOf(GrpNumber));
            Parser.SetField("AddressIfDifferent", String.valueOf(AddressIfDifferent));
            Parser.SetField("PrimaryDOB", String.valueOf(PrimaryDOB));
            Parser.SetField("PrimarySSN", String.valueOf(PrimarySSN));
            Parser.SetField("PatientRelationtoPrimaryBuff", String.valueOf(PatientRelationtoPrimaryBuff));
            Parser.SetField("PrimaryOccupation", String.valueOf(PrimaryOccupation));
            Parser.SetField("PrimaryEmployer", String.valueOf(PrimaryEmployer));
            Parser.SetField("EmployerAddress", String.valueOf(EmployerAddress));
            Parser.SetField("EmployerPhone", String.valueOf(EmployerPhone));
            Parser.SetField("SecondryInsuranceBuff", String.valueOf(SecondryInsuranceBuff));
            Parser.SetField("SubscriberName", String.valueOf(SubscriberName));
            Parser.SetField("SubscriberDOB", String.valueOf(SubscriberDOB));
            Parser.SetField("MemberID_2", String.valueOf(MemberID_2));
            Parser.SetField("GroupNumber_2", String.valueOf(GroupNumber_2));
            Parser.SetField("PatientRelationshiptoSecondryBuff", String.valueOf(PatientRelationshiptoSecondryBuff));
            Parser.SetField("NextofKinName", String.valueOf(NextofKinName));
            Parser.SetField("RelationToPatientER", String.valueOf(RelationToPatientER));
            Parser.SetField("PhoneNumberER", String.valueOf(PhoneNumberER));
            Parser.SetField("LeaveMessageERBuff", String.valueOf(LeaveMessageERBuff));
            Parser.SetField("AddressER", String.valueOf(AddressER));
            Parser.SetField("CityER", String.valueOf(CityER));
            Parser.SetField("StateER", String.valueOf(StateER));
            Parser.SetField("CountryBuffER", String.valueOf(CountryBuffER));
            Parser.SetField("ZipCodeER", String.valueOf(ZipCodeER));

            Parser.SetField("ReturnPatient", String.valueOf(ReturnPatient));
            Parser.SetField("Google", String.valueOf(Google));
            Parser.SetField("MapSearch", String.valueOf(MapSearch));
            Parser.SetField("Billboard", String.valueOf(Billboard));
            Parser.SetField("OnlineReview", String.valueOf(OnlineReview));
            Parser.SetField("TV", String.valueOf(TV));
            Parser.SetField("Website", String.valueOf(Website));
            Parser.SetField("BuildingSignDriveBy", String.valueOf(BuildingSignDriveBy));
            Parser.SetField("Facebook", String.valueOf(Facebook));
            Parser.SetField("School", String.valueOf(School));
            Parser.SetField("School_text", String.valueOf(School_text));
            Parser.SetField("Twitter", String.valueOf(Twitter));
            Parser.SetField("Magazine", String.valueOf(Magazine));
            Parser.SetField("Magazine_text", String.valueOf(Magazine_text));
            Parser.SetField("Newspaper", String.valueOf(Newspaper));
            Parser.SetField("Newspaper_text", String.valueOf(Newspaper_text));
            Parser.SetField("FamilyFriend", String.valueOf(FamilyFriend));
            Parser.SetField("FamilyFriend_text", String.valueOf(FamilyFriend_text));
            Parser.SetField("UrgentCare", String.valueOf(UrgentCare));
            Parser.SetField("UrgentCare_text", String.valueOf(UrgentCare_text));
            Parser.SetField("CommunityEvent", String.valueOf(CommunityEvent));
            Parser.SetField("CommunityEvent_text", String.valueOf(CommunityEvent_text));
            Parser.SetField("Work_textBuff", String.valueOf(Work_textBuff));
            Parser.SetField("Work_text", String.valueOf(Work_text));
            Parser.SetField("Physician_textBuff", String.valueOf(Physician_textBuff));
            Parser.SetField("Physician_text", String.valueOf(Physician_text));
            Parser.SetField("Other_textBuff", String.valueOf(Other_textBuff));
            Parser.SetField("Other_text", String.valueOf(Other_text));

            Parser.SetField("FrVisitedBeforeN", String.valueOf(FrVisitedBeforeN));
            Parser.SetField("FrFamiliyVisitedBeforeN", String.valueOf(FrFamiliyVisitedBeforeN));
            Parser.SetField("FrInternetN", String.valueOf(FrInternetN));
            Parser.SetField("FrBillboardN", String.valueOf(FrBillboardN));
            Parser.SetField("FrGoogleN", String.valueOf(FrGoogleN));
            Parser.SetField("FrBuildingSignageN", String.valueOf(FrBuildingSignageN));
            Parser.SetField("FrFacebookN", String.valueOf(FrFacebookN));
            Parser.SetField("FrLivesNearN", String.valueOf(FrLivesNearN));
            Parser.SetField("FrTwitterN", String.valueOf(FrTwitterN));
            Parser.SetField("FrTVN", String.valueOf(FrTVN));
            Parser.SetField("FrMapSearchN", String.valueOf(FrMapSearchN));
            Parser.SetField("FrEventN", String.valueOf(FrEventN));
            Parser.SetField("FrPhysicianReferral", String.valueOf(FrPhysicianReferral));
            Parser.SetField("FrNeurologyReferral", String.valueOf(FrNeurologyReferral));
            Parser.SetField("FrUrgentCareReferral", String.valueOf(FrUrgentCareReferral));
            Parser.SetField("FrOrganizationReferral", String.valueOf(FrOrganizationReferral));
            Parser.SetField("FrFriendFamily", String.valueOf(FrFriendFamily));

            Parser.SetField("MRN", String.valueOf(MRN));
            Parser.SetField("ClientIndex_logo", String.valueOf(ClientId));
            Parser.SetField("ClientIndex", String.valueOf(ClientId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/Edit/PatientRegForm_Facilities_Edit.html");
//            Parser.GenerateHtml(out,  "/sftpdrive/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_Facilities_Edit.html");
//            if(ClientId == 8){
//                Parser.GenerateHtml(out,  "/sftpdrive/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditOrange.html");
//            }else if(ClientId == 9){
//                Parser.GenerateHtml(out,  "/sftpdrive/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditVictoria.html");
//            }else if(ClientId == 10) {
//                Parser.GenerateHtml(out,  "/sftpdrive/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditOddasa.html");
//            }else if(ClientIndex == 12){
//                Parser.GenerateHtml(out,  "/sftpdrive/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditSAustin.html");
//            }else if(ClientIndex == 15){
//                Parser.GenerateHtml(out,  "/sftpdrive/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditSublime.html");
//            }else if(ClientIndex == 23){
//                Parser.GenerateHtml(out,  "/sftpdrive/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditDemo.html");
//            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    void EditSave_New(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String Database, String UserId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        int ClientIndex = 0;
        String ClientId = request.getParameter("ClientIndex").trim();
        String MRN = request.getParameter("MRN").trim();
        String Title = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String MaritalStatus = "";
        String DOB = "";
        String Age = "";
        String gender = "";
        String Email = "";
        String PhNumber = "";
        String Address = "";
        String Address2 = "";
        String County = "";
        String Ethnicity = "";
        String Race = "";
        String StreetAddress2 = "";
        String City = "";
        String State = "";
        String Country = "";
        String DoctorName = "";
        String ZipCode = "";
        String SSN = "";
        String SpCarePhy = "";
        int TravellingChk = 0;
        String TravelWhen = "";
        String TravelWhere = "";
        String TravelHowLong = "";
        String CovidExpWhen = "";
        int COVIDExposedChk = 0;
        String SympFever = "0";
        String SympBodyAches = "0";
        String SympSoreThroat = "0";
        String SympFatigue = "0";
        String SympRash = "0";
        String SympVomiting = "0";
        String SympDiarrhea = "0";
        String SympCough = "0";
        String SympRunnyNose = "0";
        String SympNausea = "0";
        String SympFluSymptoms = "0";
        String SympHeadache = "0";
        String SympLossTaste = "0";
        String SympShortBreath = "0";
        String SympCongestion = "0";
        String GuarantorName = "";
        String GuarantorDOB = "";
        String GuarantorNumber = "";
        String GuarantorSSN = "";
        String AddInfoTextArea = "";
        String SympEyeConjunctivitis = "0";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        int WorkersCompPolicy = 0;
        int MotorVehAccident = 0;
        String PriInsurance = "";
        String MemId = "";
        String GrpNumber = "";
        String PriInsuranceName = "";
        String AddressIfDifferent = "";
        String PrimaryDOB = "";
        String PrimarySSN = "";
        String PatientRelationtoPrimary = "";
        String PrimaryOccupation = "";
        String PrimaryEmployer = "";
        String EmployerAddress = "";
        String EmployerPhone = "";
        String SecondryInsurance = "";
        String SubscriberName = "";
        String SubscriberDOB = "";
        String MemberID_2 = "";
        String GroupNumber_2 = "";
        String PatientRelationshiptoSecondry = "";
        String NextofKinName = "";
        String RelationToPatientER = "";
        String PhoneNumberER = "";
        int LeaveMessageER = 0;
        String AddressER = "";
        String CityER = "";
        String StateER = "";
        String CountryER = "";
        String ZipCodeER = "";
        String DateConcent = "";
        String WitnessConcent = "";
        String PatientBehalfConcent = "";
        String RelativeSignConcent = "";
        String DateConcent2 = "";
        String WitnessConcent2 = "";
        String PatientSignConcent = "";
        int ReturnPatient = 0;
        int Google = 0;
        int MapSearch = 0;
        int Billboard = 0;
        int OnlineReview = 0;
        int TV = 0;
        int Website = 0;
        int BuildingSignDriveBy = 0;
        int Facebook = 0;
        int School = 0;
        String School_text = "";
        int Twitter = 0;
        int Magazine = 0;
        String Magazine_text = "";
        int Newspaper = 0;
        String Newspaper_text = "";
        int FamilyFriend = 0;
        String FamilyFriend_text = "";
        int UrgentCare = 0;
        String UrgentCare_text = "";
        int CommunityEvent = 0;
        String CommunityEvent_text = "";
        int Work = 0;
        String Work_text = "";
        int Physician = 0;
        String Physician_text = "";
        int Other = 0;
        String Other_text = "";
        int FrVisitedBefore = 0;
        int FrFamiliyVisitedBefore = 0;
        int FrInternet = 0;
        int FrBillboard = 0;
        int FrGoogle = 0;
        int FrBuildingSignage = 0;
        int FrFacebook = 0;
        int FrLivesNear = 0;
        int FrTwitter = 0;
        int FrTV = 0;
        int FrMapSearch = 0;
        int FrEvent = 0;
        String FrPhysicianReferral = "";
        String FrNeurologyReferral = "";
        String FrUrgentCareReferral = "";
        String FrOrganizationReferral = "";
        String FrFriendFamily = "";
        int SelfPayChk = 0;
        int AddmissionBundle = 0;
        int VerifyChkBox = 0;
        final String PatientName = "";
        int ID = 0;
        String COVIDStatus = "0";
        String DateofService = null;
        try {
            if (request.getParameter("Title") == null) {
                Title = "Mr";
            } else {
                Title = request.getParameter("Title").trim();
            }
            if (request.getParameter("FirstName") == null) {
                FirstName = "";
            } else {
                FirstName = request.getParameter("FirstName").trim();
            }
            if (request.getParameter("LastName") == null) {
                LastName = "Mr";
            } else {
                LastName = request.getParameter("LastName").trim();
            }
            if (request.getParameter("MiddleInitial") == null) {
                MiddleInitial = "";
            } else {
                MiddleInitial = request.getParameter("MiddleInitial").trim();
            }
            if (request.getParameter("MaritalStatus") == null) {
                MaritalStatus = "";
            } else {
                MaritalStatus = request.getParameter("MaritalStatus").trim();
            }
            if (request.getParameter("DOB") == null) {
                DOB = "0000-00-00";
            } else {
                DOB = request.getParameter("DOB").trim();
            }
            if (request.getParameter("Age") == null) {
                Age = "0";
            } else {
                Age = request.getParameter("Age").trim();
            }
            if (request.getParameter("gender") == null) {
                gender = "male";
            } else {
                gender = request.getParameter("gender").trim();
            }
            if (request.getParameter("Email") == null) {
                Email = "";
            } else {
                Email = request.getParameter("Email").trim();
            }
            if (request.getParameter("PhNumber") == null) {
                PhNumber = "0";
            } else {
                PhNumber = request.getParameter("PhNumber").trim();
            }
            if (request.getParameter("Address") == null) {
                Address = "";
            } else {
                Address = request.getParameter("Address").trim();
            }
            if (request.getParameter("Address2") == null) {
                Address2 = "";
            } else {
                Address2 = request.getParameter("Address2").trim();
            }
            if (request.getParameter("StreetAddress2") == null) {
                StreetAddress2 = "";
            } else {
                StreetAddress2 = request.getParameter("StreetAddress2").trim();
            }
            if (request.getParameter("City") == null) {
                City = "";
            } else {
                City = request.getParameter("City").trim();
            }
            if (request.getParameter("State") == null) {
                State = "";
            } else {
                State = request.getParameter("State").trim();
            }
            if (request.getParameter("County") == null) {
                County = "";
            } else {
                County = request.getParameter("County").trim();
            }
            if (request.getParameter("Country") == null) {
                Country = "";
            } else {
                Country = request.getParameter("Country").trim();
            }
            if (request.getParameter("ZipCode") == null) {
                ZipCode = "";
            } else {
                ZipCode = request.getParameter("ZipCode").trim();
            }
            if (request.getParameter("SSN") == null) {
                SSN = "";
            } else {
                SSN = request.getParameter("SSN").trim();
            }
            if (request.getParameter("SpCarePhy") == null) {
                SpCarePhy = "";
            } else {
                SpCarePhy = request.getParameter("SpCarePhy").trim();
            }
            if (request.getParameter("Ethnicity") == null) {
                Ethnicity = "";
            } else {
                Ethnicity = request.getParameter("Ethnicity").trim();
            }
            if (request.getParameter("Race") == null) {
                Race = "";
            } else {
                Race = request.getParameter("Race").trim();
            }
            if (request.getParameter("Occupation") == null) {
                Occupation = "";
            } else {
                Occupation = request.getParameter("Occupation").trim();
            }
            if (request.getParameter("Employer") == null) {
                Employer = "";
            } else {
                Employer = request.getParameter("Employer").trim();
            }
            if (request.getParameter("EmpContact") == null) {
                EmpContact = "";
            } else {
                EmpContact = request.getParameter("EmpContact").trim();
            }
            if (request.getParameter("GuarantorName") == null) {
                GuarantorName = "";
            } else {
                GuarantorName = request.getParameter("GuarantorName").trim();
            }
            if (request.getParameter("GuarantorDOB") == null) {
                GuarantorDOB = "";
            } else {
                GuarantorDOB = request.getParameter("GuarantorDOB").trim();
            }
            if (request.getParameter("GuarantorNumber") == null) {
                GuarantorNumber = "";
            } else {
                GuarantorNumber = request.getParameter("GuarantorNumber").trim();
            }
            if (request.getParameter("GuarantorSSN") == null) {
                GuarantorSSN = "";
            } else {
                GuarantorSSN = request.getParameter("GuarantorSSN").trim();
            }

            if (request.getParameter("PriCarePhy") == null) {
                PriCarePhy = "";
            } else {
                PriCarePhy = request.getParameter("PriCarePhy").trim();
            }
            if (request.getParameter("ReasonVisit") == null) {
                ReasonVisit = "";
            } else {
                ReasonVisit = request.getParameter("ReasonVisit").trim();
            }

            if (request.getParameter("TravellingChk") == null) {
                TravellingChk = 0;
            } else {
                TravellingChk = Integer.parseInt(request.getParameter("TravellingChk").trim());
            }
            if (TravellingChk == 1) {
                if (request.getParameter("TravelWhen") == null) {
                    TravelWhen = "";
                } else {
                    TravelWhen = request.getParameter("TravelWhen").trim();
                }
                if (request.getParameter("TravelWhere") == null) {
                    TravelWhere = "";
                } else {
                    TravelWhere = request.getParameter("TravelWhere").trim();
                }
                if (request.getParameter("TravelHowLong") == null) {
                    TravelHowLong = "";
                } else {
                    TravelHowLong = request.getParameter("TravelHowLong").trim();
                }
            }
            if (request.getParameter("COVIDExposedChk") == null) {
                COVIDExposedChk = 0;
            } else {
                COVIDExposedChk = Integer.parseInt(request.getParameter("COVIDExposedChk").trim());
            }
            if (request.getParameter("CovidExpWhen") == null) {
                CovidExpWhen = "";
            } else {
                CovidExpWhen = request.getParameter("CovidExpWhen").trim();
            }
            if (request.getParameter("SympFever") == null) {
                SympFever = "0";
            } else {
                SympFever = "1";
            }
            if (request.getParameter("SympBodyAches") == null) {
                SympBodyAches = "0";
            } else {
                SympBodyAches = "1";
            }
            if (request.getParameter("SympSoreThroat") == null) {
                SympSoreThroat = "0";
            } else {
                SympSoreThroat = "1";
            }
            if (request.getParameter("SympFatigue") == null) {
                SympFatigue = "0";
            } else {
                SympFatigue = "1";
            }
            if (request.getParameter("SympRash") == null) {
                SympRash = "0";
            } else {
                SympRash = "1";
            }
            if (request.getParameter("SympVomiting") == null) {
                SympVomiting = "0";
            } else {
                SympVomiting = "1";
            }
            if (request.getParameter("SympDiarrhea") == null) {
                SympDiarrhea = "0";
            } else {
                SympDiarrhea = "1";
            }
            if (request.getParameter("SympCough") == null) {
                SympCough = "0";
            } else {
                SympCough = "1";
            }
            if (request.getParameter("SympRunnyNose") == null) {
                SympRunnyNose = "0";
            } else {
                SympRunnyNose = "1";
            }
            if (request.getParameter("SympNausea") == null) {
                SympNausea = "0";
            } else {
                SympNausea = "1";
            }
            if (request.getParameter("SympFluSymptoms") == null) {
                SympFluSymptoms = "0";
            } else {
                SympFluSymptoms = "1";
            }
            if (request.getParameter("SympHeadache") == null) {
                SympHeadache = "0";
            } else {
                SympHeadache = "1";
            }
            if (request.getParameter("SympLossTaste") == null) {
                SympLossTaste = "0";
            } else {
                SympLossTaste = "1";
            }
            if (request.getParameter("SympShortBreath") == null) {
                SympShortBreath = "0";
            } else {
                SympShortBreath = "1";
            }
            if (request.getParameter("SympCongestion") == null) {
                SympCongestion = "0";
            } else {
                SympCongestion = "1";
            }
            if (request.getParameter("AddInfoTextArea") == null) {
                AddInfoTextArea = "";
            } else {
                AddInfoTextArea = request.getParameter("AddInfoTextArea").trim();
            }
//            if (request.getParameter("SympEyeConjunctivitis") == null) {
//                SympEyeConjunctivitis = "0";
//            }
//            else {
//                SympEyeConjunctivitis = "1";
//            }

            if (request.getParameter("COVIDStatus_Chk") == null) {
                COVIDStatus = "-1";
            } else {
                COVIDStatus = request.getParameter("COVIDStatus_Chk").trim();
            }
//            if (request.getParameter("DoctorName") == null) {
//                DoctorName = "0";
//            } else if (request.getParameter("DoctorName").equals("")) {
//                DoctorName = "0";
//            } else {
//                DoctorName = request.getParameter("DoctorName").trim();
//            }
            System.out.println(DoctorName + "-----:DoctorsName");
//            if (request.getParameter("DateofService") == null) {
//                DateofService = "";
//            } else {
//                DateofService = request.getParameter("DateofService").trim();
//            }
            if (request.getParameter("WorkersCompPolicy") == null) {
                WorkersCompPolicy = 0;
            } else {
                WorkersCompPolicy = Integer.parseInt(request.getParameter("WorkersCompPolicy").trim());
            }
            if (request.getParameter("MotorVehAccident") == null) {
                MotorVehAccident = 0;
            } else {
                MotorVehAccident = Integer.parseInt(request.getParameter("MotorVehAccident").trim());
            }
            if (request.getParameter("PriInsurance") == null) {
                PriInsurance = "";
            } else {
                PriInsurance = request.getParameter("PriInsurance").trim();
            }
            if (request.getParameter("AddmissionBundle") == null) {
                AddmissionBundle = 0;
            } else {
                AddmissionBundle = Integer.parseInt(request.getParameter("AddmissionBundle").trim());
            }
            if (request.getParameter("MemId") == null) {
                MemId = "";
            } else {
                MemId = request.getParameter("MemId").trim();
            }
            if (request.getParameter("GrpNumber") == null) {
                GrpNumber = "";
            } else {
                GrpNumber = request.getParameter("GrpNumber").trim();
            }
            if (request.getParameter("PriInsuranceName") == null) {
                PriInsuranceName = "";
            } else {
                PriInsuranceName = request.getParameter("PriInsuranceName").trim();
            }
            if (request.getParameter("AddressIfDifferent") == null) {
                AddressIfDifferent = "";
            } else {
                AddressIfDifferent = request.getParameter("AddressIfDifferent").trim();
            }
            if (request.getParameter("PrimaryDOB") == null) {
                PrimaryDOB = "0000-00-00";
            } else {
                PrimaryDOB = request.getParameter("PrimaryDOB").trim();
            }
//             System.out.println(PrimaryDOB);
            if (request.getParameter("PrimarySSN") == null) {
                PrimarySSN = "";
            } else {
                PrimarySSN = request.getParameter("PrimarySSN").trim();
            }
            if (request.getParameter("PatientRelationtoPrimary") == null) {
                PatientRelationtoPrimary = "0";
            } else {
                PatientRelationtoPrimary = request.getParameter("PatientRelationtoPrimary").trim();
            }
            if (request.getParameter("PrimaryOccupation") == null) {
                PrimaryOccupation = "";
            } else {
                PrimaryOccupation = request.getParameter("PrimaryOccupation").trim();
            }
            if (request.getParameter("PrimaryEmployer") == null) {
                PrimaryEmployer = "";
            } else {
                PrimaryEmployer = request.getParameter("PrimaryEmployer").trim();
            }
            if (request.getParameter("EmployerAddress") == null) {
                EmployerAddress = "";
            } else {
                EmployerAddress = request.getParameter("EmployerAddress").trim();
            }
            if (request.getParameter("EmployerPhone") == null) {
                EmployerPhone = "";
            } else {
                EmployerPhone = request.getParameter("EmployerPhone").trim();
            }
            if (request.getParameter("SecondryInsurance") == null) {
                SecondryInsurance = "0";
            } else {
                SecondryInsurance = request.getParameter("SecondryInsurance").trim();
            }
            if (request.getParameter("SubscriberName") == null) {
                SubscriberName = "";
            } else {
                SubscriberName = request.getParameter("SubscriberName").trim();
            }
            if (request.getParameter("SubscriberDOB") == null) {
                SubscriberDOB = "0000-00-00";
            } else {
                SubscriberDOB = request.getParameter("DOB").trim();
            }
            if (request.getParameter("MemberID_2") == null) {
                MemberID_2 = "";
            } else {
                MemberID_2 = request.getParameter("MemberID_2").trim();
            }
            if (request.getParameter("GroupNumber_2") == null) {
                GroupNumber_2 = "";
            } else {
                GroupNumber_2 = request.getParameter("GroupNumber_2").trim();
            }
            if (request.getParameter("PatientRelationshiptoSecondry") == null) {
                PatientRelationshiptoSecondry = "";
            } else {
                PatientRelationshiptoSecondry = request.getParameter("PatientRelationshiptoSecondry").trim();
            }
//            if (request.getParameter("SelfPayChk") == null) {
//                SelfPayChk = 0;
//            } else {
//                SelfPayChk = 1;
//
//            }
            if (request.getParameter("NextofKinName") == null) {
                NextofKinName = "";
            } else {
                NextofKinName = request.getParameter("NextofKinName").trim();
            }
            if (request.getParameter("RelationToPatientER") == null) {
                RelationToPatientER = "";
            } else {
                RelationToPatientER = request.getParameter("RelationToPatientER").trim();
            }
            if (request.getParameter("PhoneNumberER") == null) {
                PhoneNumberER = "";
            } else {
                PhoneNumberER = request.getParameter("PhoneNumberER").trim();
            }
            if (request.getParameter("LeaveMessageER") == null) {
                LeaveMessageER = 0;
            } else {
                LeaveMessageER = Integer.parseInt(request.getParameter("LeaveMessageER").trim());
            }
            if (request.getParameter("AddressER") == null) {
                AddressER = "";
            } else {
                AddressER = request.getParameter("AddressER").trim();
            }
            if (request.getParameter("CityER") == null) {
                CityER = "";
            } else {
                CityER = request.getParameter("CityER").trim();
            }
            if (request.getParameter("StateER") == null) {
                StateER = "";
            } else {
                StateER = request.getParameter("StateER").trim();
            }
            if (request.getParameter("CountryER") == null) {
                CountryER = "";
            } else {
                CountryER = request.getParameter("CountryER").trim();
            }
            if (request.getParameter("ZipCodeER") == null) {
                ZipCodeER = "";
            } else {
                ZipCodeER = request.getParameter("ZipCodeER").trim();
            }

            if (request.getParameter("ReturnPatient") == null) {
                ReturnPatient = 0;
            } else {
                ReturnPatient = 1;
            }
            if (request.getParameter("Google") == null) {
                Google = 0;
            } else {
                Google = 1;
            }
            if (request.getParameter("MapSearch") == null) {
                MapSearch = 0;
            } else {
                MapSearch = 1;
            }
            if (request.getParameter("Billboard") == null) {
                Billboard = 0;
            } else {
                Billboard = 1;
            }
            if (request.getParameter("OnlineReview") == null) {
                OnlineReview = 0;
            } else {
                OnlineReview = 1;
            }
            if (request.getParameter("TV") == null) {
                TV = 0;
            } else {
                TV = 2;
            }
            if (request.getParameter("Website") == null) {
                Website = 0;
            } else {
                Website = 1;
            }
            if (request.getParameter("BuildingSignDriveBy") == null) {
                BuildingSignDriveBy = 0;
            } else {
                BuildingSignDriveBy = 1;
            }
            if (request.getParameter("Facebook") == null) {
                Facebook = 0;
            } else {
                Facebook = 1;
            }
            if (request.getParameter("School") == null) {
                School = 0;
                School_text = "-";
            } else {
                School = 1;
                if (request.getParameter("School_text") == null) {
                    School_text = "-";
                } else {
                    School_text = request.getParameter("School_text").trim();
                }
            }
            if (request.getParameter("Twitter") == null) {
                Twitter = 0;
            } else {
                Twitter = 1;
            }
            if (request.getParameter("Magazine") == null) {
                Magazine = 0;
                Magazine_text = "";
            } else {
                Magazine = 1;
                if (request.getParameter("Magazine_text") == null) {
                    Magazine_text = "";
                } else {
                    Magazine_text = request.getParameter("Magazine_text").trim();
                }
            }
            if (request.getParameter("Newspaper") == null) {
                Newspaper = 0;
                Newspaper_text = "";
            } else {
                Newspaper = 1;
                if (request.getParameter("Newspaper_text") == null) {
                    Newspaper_text = "";
                } else {
                    Newspaper_text = request.getParameter("Newspaper_text").trim();
                }
            }
            if (request.getParameter("FamilyFriend") == null) {
                FamilyFriend = 0;
                FamilyFriend_text = "";
            } else {
                FamilyFriend = 1;
                if (request.getParameter("FamilyFriend_text") == null) {
                    FamilyFriend_text = "";
                } else {
                    FamilyFriend_text = request.getParameter("FamilyFriend_text").trim();
                }
            }
            if (request.getParameter("UrgentCare") == null) {
                UrgentCare = 0;
                UrgentCare_text = "";
            } else {
                UrgentCare = 1;
                if (request.getParameter("UrgentCare_text") == null) {
                    UrgentCare_text = "";
                } else {
                    UrgentCare_text = request.getParameter("UrgentCare_text").trim();
                }
            }
            if (request.getParameter("CommunityEvent") == null) {
                CommunityEvent = 0;
                CommunityEvent_text = "";
            } else {
                CommunityEvent = 1;
                if (request.getParameter("CommunityEvent_text") == null) {
                    CommunityEvent_text = "";
                } else {
                    CommunityEvent_text = request.getParameter("CommunityEvent_text").trim();
                }
            }
            if (request.getParameter("Work") == null) {
                Work = 0;
                Work_text = "";
            } else {
                Work = 1;
                if (request.getParameter("Work_text") == null) {
                    Work_text = "";
                } else {
                    Work_text = request.getParameter("Work_text").trim();
                }
            }
            if (request.getParameter("Physician") == null) {
                Physician = 0;
                Physician_text = "";
            } else {
                Physician = 1;
                if (request.getParameter("Physician_text") == null) {
                    Physician_text = "";
                } else {
                    Physician_text = request.getParameter("Physician_text").trim();
                }
            }
            if (request.getParameter("Other") == null) {
                Other = 0;
                Other_text = "";
            } else {
                Other = 1;
                if (request.getParameter("Other_text") == null) {
                    Other_text = "";
                } else {
                    Other_text = request.getParameter("Other_text").trim();
                }
            }

            if (request.getParameter("FrVisitedBefore") == null) {
                FrVisitedBefore = 0;
            } else {
                FrVisitedBefore = Integer.parseInt(request.getParameter("FrVisitedBefore").trim());
            }
            if (request.getParameter("FrFamiliyVisitedBefore") == null) {
                FrFamiliyVisitedBefore = 0;
            } else {
                FrFamiliyVisitedBefore = Integer.parseInt(request.getParameter("FrFamiliyVisitedBefore").trim());
            }

            if (request.getParameter("FrInternet") == null) {
                FrInternet = 0;
            } else {
                FrInternet = 1;
            }
            if (request.getParameter("FrBillboard") == null) {
                FrBillboard = 0;
            } else {
                FrBillboard = 1;
            }
            if (request.getParameter("FrGoogle") == null) {
                FrGoogle = 0;
            } else {
                FrGoogle = 1;
            }
            if (request.getParameter("FrBuildingSignage") == null) {
                FrBuildingSignage = 0;
            } else {
                FrBuildingSignage = 1;
            }
            if (request.getParameter("FrFacebook") == null) {
                FrFacebook = 0;
            } else {
                FrFacebook = 1;
            }
            if (request.getParameter("FrLivesNear") == null) {
                FrLivesNear = 0;
            } else {
                FrLivesNear = 1;
            }
            if (request.getParameter("FrTwitter") == null) {
                FrTwitter = 0;
            } else {
                FrTwitter = 1;
            }
            if (request.getParameter("FrTV") == null) {
                FrTV = 0;
            } else {
                FrTV = 1;
            }
            if (request.getParameter("FrMapSearch") == null) {
                FrMapSearch = 0;
            } else {
                FrMapSearch = 1;
            }
            if (request.getParameter("FrEvent") == null) {
                FrEvent = 0;
            } else {
                FrEvent = 1;
            }

            if (request.getParameter("FrPhysicianReferral") == null) {
                FrPhysicianReferral = "";
            } else {
                FrPhysicianReferral = request.getParameter("FrPhysicianReferral").trim();
            }
            if (request.getParameter("FrNeurologyReferral") == null) {
                FrNeurologyReferral = "";
            } else {
                FrNeurologyReferral = request.getParameter("FrNeurologyReferral").trim();
            }
            if (request.getParameter("FrUrgentCareReferral") == null) {
                FrUrgentCareReferral = "";
            } else {
                FrUrgentCareReferral = request.getParameter("FrUrgentCareReferral").trim();
            }
            if (request.getParameter("FrOrganizationReferral") == null) {
                FrOrganizationReferral = "";
            } else {
                FrOrganizationReferral = request.getParameter("FrOrganizationReferral").trim();
            }
            if (request.getParameter("FrFriendFamily") == null) {
                FrFriendFamily = "";
            } else {
                FrFriendFamily = request.getParameter("FrFriendFamily").trim();
            }

            if (ClientId.equals("27") || ClientId.equals("29")) {
                Query = "Select ReasonVisit from " + Database + ".ReasonVisits where Id = " + ReasonVisit;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ReasonVisit = rset.getString(1);
                }
                rset.close();
                stmt.close();
            }

            int MaxVisitNumber = 0;
            int VisitId = 0;
            try {
                Query = "Select ID from " + Database + ".PatientReg where MRN = " + MRN;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    ID = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error in getting PatineReg Data get:--" + e.getMessage() + Query);
            }
            try {

                insertPatientRegHistory(request, conn, servletContext, Database, ID);

                Query = " UPDATE " + Database + ".PatientReg SET Title ='" + Title + "', FirstName = '" + FirstName + "', LastName = '" + LastName + "', MiddleInitial = '" + MiddleInitial + "', " + " DOB = '" + DOB + "', Age = '" + Age + "', Gender = '" + gender + "', Email = '" + Email + "', PhNumber = '" + PhNumber + "', Address = '" + Address + "', City = '" + City + "', " + " State = '" + State + "', Country = '" + Country + "', ZipCode = '" + ZipCode + "', SSN = '" + SSN + "', Occupation = '" + Occupation + "', Employer = '" + Employer + "', " + " EmpContact = '" + EmpContact + "', PriCarePhy = '" + PriCarePhy + "', ReasonVisit = '" + ReasonVisit + "', MaritalStatus = '" + MaritalStatus + "', " +
                        " Ethnicity = '" + Ethnicity + "', County = '" + County + "', Address2 = '" + Address2 + "', StreetAddress2 = '" + StreetAddress2 + "' WHERE ID = " + ID;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Updating PatientReg Table:-" + e.getMessage());
            }


            try {
                Query = "Select max(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    MaxVisitNumber = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                Query = "Select Id from " + Database + ".PatientVisit where PatientRegId = " + ID + " and VisitNumber = " + MaxVisitNumber;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    VisitId = rset.getInt(1);
                }
                rset.close();
                stmt.close();

            } catch (Exception e) {
                out.println("Error in getting VisitNumber From PatientVisit" + e.getMessage());
            }

            try {
                Query = "UPDATE " + Database + ".PatientVisit SET ReasonVisit ='" + ReasonVisit + "' WHERE PatientRegId = " + ID + " and VisitNumber = " + MaxVisitNumber;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Updating PatientVisit Table:-" + e.getMessage());
            }

            try {
                insertPatientReg_DetailsHistory(request, conn, servletContext, Database, ID);

                Query = "Update " + Database + ".PatientReg_Details set TravellingChk = '" + TravellingChk + "',TravelWhen = '" + TravelWhen + "',TravelWhere = '" + TravelWhere + "', " +
                        " TravelHowLong = '" + TravelHowLong + "',COVIDExposedChk = '" + COVIDExposedChk + "',SympFever = '" + SympFever + "',SympBodyAches = '" + SympBodyAches + "'," +
                        " SympSoreThroat = '" + SympSoreThroat + "',SympFatigue = '" + SympFatigue + "',SympRash = '" + SympRash + "',SympVomiting = '" + SympVomiting + "'," +
                        " SympDiarrhea = '" + SympDiarrhea + "',SympCough = '" + SympCough + "',SympRunnyNose = '" + SympRunnyNose + "',SympNausea = '" + SympNausea + "'," +
                        " SympFluSymptoms = '" + SympFluSymptoms + "' ,Race = '" + Race + "', " +
                        " CovidExpWhen = '" + CovidExpWhen + "', SpCarePhy = '" + SpCarePhy + "', SympHeadache = '" + SympHeadache + "', " +
                        " SympLossTaste = '" + SympLossTaste + "', SympShortBreath = '" + SympShortBreath + "', SympCongestion = '" + SympCongestion + "', " +
                        " AddInfoTextArea = '" + AddInfoTextArea + "', GuarantorName = '" + GuarantorName + "', GuarantorDOB = '" + GuarantorDOB + "', " +
                        " GuarantorNumber = '" + GuarantorNumber + "', GuarantorSSN = '" + GuarantorSSN + "' WHERE PatientRegId = " + ID;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error on Update PatientReg_Details:- " + e.getMessage());
            }

            try {
                int InsuranceDataFound = 0;
//                System.out.println("PriInsuranceName: "+PriInsuranceName);
                if (!PriInsuranceName.equals("")) {
                    Query = "Update " + Database + ".PatientReg Set SelfPayChk = 1 where ID = " + ID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();

                    Query = "Select SelfPayChk from " + Database + ".PatientReg where ID = " + ID;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        SelfPayChk = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();
                } else {
                    Query = "Update " + Database + ".PatientReg Set SelfPayChk = 0 where ID = " + PatientRegId;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();

                    Query = "Select SelfPayChk from " + Database + ".PatientReg where ID = " + PatientRegId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        SelfPayChk = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();
                }


                if (SelfPayChk == 1) {
//                    System.out.println("INSIDE SelfPayChk: "+SelfPayChk);
                    int PatientAdmissionBundleCount = 0;
                    if (ClientId.equals("10") || ClientId.equals("15")) {
                        Query = "Select COUNT(*) from " + Database + ".PatientAdmissionBundle where PatientRegId = " + ID;
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        while (rset.next()) {
                            PatientAdmissionBundleCount = rset.getInt(1);
                        }
                        rset.close();
                        stmt.close();
                        if (PatientAdmissionBundleCount > 0) {
                            Query = "Update " + Database + ".PatientAdmissionBundle set AddmissionBundle = '" + AddmissionBundle + "' where PatientRegId = " + ID;
                            stmt = conn.createStatement();
                            stmt.executeUpdate(Query);
                            stmt.close();
                        } else {
                            final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".PatientAdmissionBundle(PatientRegId,AdmissionBundle,CreatedDate) \nVALUES (?,?,now()) ");
                            MainReceipt.setInt(1, PatientRegId);
                            MainReceipt.setInt(2, AddmissionBundle);
                            MainReceipt.executeUpdate();
                            MainReceipt.close();

                        }

                    }

                    Query = "Select COUNT(*) from " + Database + ".InsuranceInfo WHERE PatientRegId = " + ID;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        InsuranceDataFound = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();
                    System.out.println("InsuranceDataFound VAR:  " + InsuranceDataFound);

                    if (InsuranceDataFound > 0) {

                        //Here Insert into Insurancehistory
                        insertInsuranceInfoHistory(request, conn, servletContext, Database, ID);

                        Query = "UPDATE " + Database + ".InsuranceInfo SET WorkersCompPolicy = " + WorkersCompPolicy + ", MotorVehAccident = " + MotorVehAccident + ", PriInsurance = '" + PriInsurance + "', " + " MemId = '" + MemId + "', GrpNumber = '" + GrpNumber + "', PriInsuranceName = '" + PriInsuranceName + "', AddressIfDifferent = '" + AddressIfDifferent + "', " + " PrimaryDOB = '" + PrimaryDOB + "', PrimarySSN = '" + PrimarySSN + "', PatientRelationtoPrimary = '" + PatientRelationtoPrimary + "', PrimaryOccupation = '" + PrimaryOccupation + "', " + " PrimaryEmployer = '" + PrimaryEmployer + "', EmployerAddress = '" + EmployerAddress + "', EmployerPhone = '" + EmployerPhone + "', SecondryInsurance = '" + SecondryInsurance + "', " + " SubscriberName = '" + SubscriberName + "', SubscriberDOB = '" + SubscriberDOB + "', MemberID_2 = '" + MemberID_2 + "', GroupNumber_2 = '" + GroupNumber_2 + "', " + " PatientRelationshiptoSecondry = '" + PatientRelationshiptoSecondry + "' WHERE PatientRegId = " + ID;
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();

                    } else {
                        //Insert Insurance Info Here
                        try {
                            PreparedStatement MainReceipt = conn.prepareStatement(
                                    "INSERT INTO " + Database + ".InsuranceInfo(PatientRegId,WorkersCompPolicy,MotorVehAccident,PriInsurance,MemId,GrpNumber," +
                                            "PriInsuranceName,AddressIfDifferent,PrimaryDOB,PrimarySSN,PatientRelationtoPrimary,PrimaryOccupation,PrimaryEmployer," +
                                            "EmployerAddress,EmployerPhone,SecondryInsurance,SubscriberName,SubscriberDOB,MemberID_2,GroupNumber_2," +
                                            "PatientRelationshiptoSecondry,CreatedDate,VisitId) " +
                                            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?) ");
                            MainReceipt.setInt(1, ID);//PAtientRegId
                            MainReceipt.setInt(2, WorkersCompPolicy);
                            MainReceipt.setInt(3, MotorVehAccident);
                            MainReceipt.setString(4, PriInsurance);
                            MainReceipt.setString(5, MemId);
                            MainReceipt.setString(6, GrpNumber);
                            MainReceipt.setString(7, PriInsuranceName);
                            MainReceipt.setString(8, AddressIfDifferent);
                            MainReceipt.setString(9, PrimaryDOB);
                            MainReceipt.setString(10, PrimarySSN);
                            MainReceipt.setString(11, PatientRelationtoPrimary);
                            MainReceipt.setString(12, PrimaryOccupation);
                            MainReceipt.setString(13, PrimaryEmployer);
                            MainReceipt.setString(14, EmployerAddress);
                            MainReceipt.setString(15, EmployerPhone);
                            MainReceipt.setString(16, SecondryInsurance);
                            MainReceipt.setString(17, SubscriberName);
                            MainReceipt.setString(18, SubscriberDOB);
                            MainReceipt.setString(19, MemberID_2);
                            MainReceipt.setString(20, GroupNumber_2);
                            MainReceipt.setString(21, PatientRelationshiptoSecondry);
                            MainReceipt.setString(22, String.valueOf(VisitId));
                            MainReceipt.executeUpdate();
//                            System.out.println("QUERY --> " + MainReceipt + "<br>");
                            MainReceipt.close();
                        } catch (Exception e) {
                            out.println("Error in INserting InsuranceiNFO Data: " + e.getMessage());
                        }

                    }
                }

//                System.out.println("OUTSIDE SelfPayChk: "+SelfPayChk);

            } catch (Exception e) {
                out.println("Error in Updating Insurance info Table :--" + e.getMessage());
            }
            try {

                insertEmergencyInfoHistory(request, conn, servletContext, Database, ID);

                Query = "Update " + Database + ".EmergencyInfo set NextofKinName = '" + NextofKinName + "', RelationToPatient = '" + RelationToPatientER + "', PhoneNumber = '" + PhoneNumberER + "', " + " LeaveMessage = " + LeaveMessageER + ", Address = '" + AddressER + "', City = '" + CityER + "', State = '" + StateER + "', Country = '" + CountryER + "', ZipCode = '" + ZipCodeER + "' " + " where PatientRegId = " + ID;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Updating Emergency Info Table:-- " + e.getMessage());
            }
//            try {
//                Query = "Update " + Database + ".ConcentToTreatmentInfo set PatientSign = '" + PatientSignConcent + "', Date = '" + DateConcent + "', Witness = '" + WitnessConcent + "', " + " PatientBehalfSign = '" + PatientBehalfConcent + "', RelativeSign = '" + RelativeSignConcent + "', Date2 = '" + DateConcent2 + "', Witness2 = '" + WitnessConcent2 + "' " + "Where PatientRegId = " + ID;
//                stmt = conn.createStatement();
//                stmt.executeUpdate(Query);
//                stmt.close();
//            } catch (Exception e) {
//                out.println("Error in Updating ConcentToTreatmentInfo Table:-- " + e.getMessage());
//            }
            try {
                if (ClientId.equals("27") || ClientId.equals("29")) {

                    insertRandomCheckInfoHistory(request, conn, servletContext, Database, ID);

                    Query = "Update " + Database + ".RandomCheckInfo set FrVisitedBefore = " + FrVisitedBefore + ", FrFamiliyVisitedBefore = " + FrFamiliyVisitedBefore + ", " +
                            " FrInternet = " + FrInternet + ", FrBillboard = " + FrBillboard + ", " + " FrGoogle = " + FrGoogle + ", FrBuildingSignage = " + FrBuildingSignage + ", " +
                            " FrFacebook = " + FrFacebook + ", FrLivesNear = " + FrLivesNear + ", FrTwitter = " + FrTwitter + ", " +
                            " FrTV = " + FrTV + ", FrMapSearch = " + FrMapSearch + ", FrEvent = " + FrEvent + ", FrPhysicianReferral = '" + FrPhysicianReferral + "' , " +
                            " FrNeurologyReferral = '" + FrNeurologyReferral + "', " + " FrUrgentCareReferral = '" + FrUrgentCareReferral + "', " +
                            " FrOrganizationReferral = '" + FrOrganizationReferral + "', FrFriendFamily = '" + FrFriendFamily + "' where PatientRegId = " + ID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();

                } else {

                    insertRandomCheckInfoHistory(request, conn, servletContext, Database, ID);

                    Query = "Update " + Database + ".RandomCheckInfo set ReturnPatient = " + ReturnPatient + ", Google = " + Google + ", " +
                            "MapSearch = " + MapSearch + ", Billboard = " + Billboard + ", " + " OnlineReview = " + OnlineReview + ", TV = " + TV + ", " +
                            "Website = " + Website + ", BuildingSignDriveBy = " + BuildingSignDriveBy + ", Facebook = " + Facebook + ", " +
                            " School = " + School + ", School_text = '" + School_text + "', Twitter = " + Twitter + ", Magazine = "
                            + Magazine + ", Magazine_text = '" + Magazine_text + "', " + " Newspaper = " + Newspaper + ", Newspaper_text = '"
                            + Newspaper_text + "', FamilyFriend = " + FamilyFriend + ", FamilyFriend_text = '" + FamilyFriend_text + "', "
                            + " UrgentCare = " + UrgentCare + ", UrgentCare_text = '" + UrgentCare_text + "', CommunityEvent = " + CommunityEvent + ", " +
                            "CommunityEvent_text = '" + CommunityEvent_text + "', " + " Work_text = '" + Work_text + "', " +
                            "Physician_text = '" + Physician_text + "', Other_text = '" + Other_text + "' where PatientRegId = " + ID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }
            } catch (Exception e) {
                out.println("Error in Updating RandomCheckInfo Table:-- " + e.getMessage());
            }
//            /md/md.PatientUpdateInfo_old?ActionID=GetInput&ID=' + ID
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " Information has been Updated ");
            Parser.SetField("FormName", String.valueOf("PatientUpdateInfo"));
            Parser.SetField("MRN", String.valueOf("MRN: " + MRN));
            Parser.SetField("ActionID", "GetInput&ID=" + ID);
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message.html");
//            final Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("PatientName", String.valueOf(PatientName));
//            Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/oe_2/Exception/Message_Success.html");
        } catch (Exception e) {
            out.println("Error in Updating:-" + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    private void CheckPatient(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database) {
        String Query = "";
        Statement stmt = null;
        ResultSet rset = null;
        String FirstName = "";
        String LastName = "";
        String DOB = "";
        int ClientIndex = 0;
        FirstName = request.getParameter("FirstName").trim();
        LastName = request.getParameter("LastName").trim();
        DOB = request.getParameter("DOB").trim();
        ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());
        //DOB = String.valueOf(String.valueOf(DOB.substring(6, 10))) + "-" + DOB.substring(0, 2) + "-" + DOB.substring(3, 5);

        try {

            Query = "Select dbname from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();

            int PatientFound = 0;
            String FoundMRN = "";
            Query = " Select COUNT(*), IFNULL(MRN,0) from " + Database + ".PatientReg " +
                    " where Status = 0 and ltrim(rtrim(UPPER(FirstName))) = ltrim(rtrim(UPPER('" + FirstName.trim() + "'))) " +
                    " and ltrim(rtrim(UPPER(LastName))) = ltrim(rtrim(UPPER('" + LastName.trim() + "'))) and DOB = '" + DOB + "'";
            //out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientFound = rset.getInt(1);
                FoundMRN = rset.getString(2);
            }
            rset.close();
            stmt.close();

            if (PatientFound > 0) {
                //true
                out.println(FoundMRN + "|" + PatientFound);
                return;
            }

        } catch (Exception e) {
            out.println("Error " + e.getMessage());
            return;
        }

    }

    private void ReasonVisits(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer ReasonVisitS = new StringBuffer();

        try {
            int ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());
            String ReasonVisit = request.getParameter("ReasonVisitSelect").trim();
            Query = "Select dbname from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();


            if (ClientIndex == 27 || ClientIndex == 29) {
                ReasonVisitS.append("<label><font color=\"black\">Reason For Visit </font></label>");
                ReasonVisitS.append("<select class=\"form-control\" id=\"ReasonVisit\" name=\"ReasonVisit\" style=\"color:black;\" >");
                ReasonVisitS.append("<option value=\"\">Select Reason of Visit</option>\n");
                Query = "Select Id,ReasonVisit from " + Database + ".ReasonVisits where ltrim(rtrim(UPPER(Catagory))) = ltrim(rtrim(UPPER('" + ReasonVisit + "')))";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    ReasonVisitS.append("<option value=\"" + rset.getString(1).trim() + "\">" + rset.getString(2).trim() + "</option>\n");
                }
                rset.close();
                stmt.close();
                ReasonVisitS.append("</select>");

            } else {
                if (ReasonVisit.toUpperCase().trim().equals("COVID")) {
                    ReasonVisitS.append("<label><font color=\"black\">Reason For Visit </font></label>");
                    ReasonVisitS.append("<input type=\"text\" placeholder=\"\" class=\"form-control\"id=\"ReasonVisit\" name=\"ReasonVisit\" value=\"COVID Testing\" readonly>");
                } else {
                    ReasonVisitS.append("<label><font color=\"black\">Reason For Visit </font></label>");
                    ReasonVisitS.append("<input type=\"text\" placeholder=\"\" class=\"form-control\"id=\"ReasonVisit\" name=\"ReasonVisit\" >");
                }
            }

            out.println(ReasonVisitS);
        } catch (Exception e) {
            out.println("1");
            out.println(Query);
            System.out.println("Error in Getting Reasons:--" + e.getStackTrace());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    public void insertPatientRegHistory(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, int ID) {
        pStmt = null;
        stmt = null;
        rset = null;
        Query = null;

        try {

            Query = " SELECT ClientIndex,FirstName,LastName,MiddleInitial,DOB,Age,Gender,\n" +//7
                    "Email,PhNumber,Address,City,State,Country,ZipCode,SSN,Occupation,Employer,\n" +//17
                    "EmpContact,PriCarePhy,ReasonVisit,SelfPayChk,CreatedDate,Title,MaritalStatus,\n" +//24
                    "CreatedBy,MRN,COVIDStatus,Status,DoctorsName,sync,DateofService,ExtendedMRN,\n" +//32
                    "Ethnicity,County,Address2,StreetAddress2,Race,EnterBy,EnterType,EnterIP " +//40
                    " FROM " + database + ".PatientReg WHERE ID = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                pStmt = conn.prepareStatement(
                        "INSERT INTO " + database + ".PatientRegHistory (OldPatientRegId,ClientIndex,FirstName,LastName,MiddleInitial,DOB,Age,Gender," +//8
                                " Email,PhNumber,Address,City," +//12
                                " State,Country,ZipCode,SSN,Occupation,\n" +//17
                                " Employer,EmpContact,PriCarePhy,ReasonVisit,SelfPayChk,CreatedDate,Title,MaritalStatus,CreatedBy,MRN,COVIDStatus,Status,\n" +//29
                                " DoctorsName,sync,DateofService,ExtendedMRN,Ethnicity,County,Address2,StreetAddress2,Race,EnterBy,EnterType,EnterIP)" +//41
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                pStmt.setInt(1, ID); //OldPatientRegId
                pStmt.setInt(2, rset.getInt(1)); //ClientIndex
                pStmt.setString(3, rset.getString(2)); //FirstName
                pStmt.setString(4, rset.getString(3)); //LastName
                pStmt.setString(5, rset.getString(4)); //MiddleInitial
                pStmt.setString(6, rset.getString(5)); //DOB
                pStmt.setInt(7, rset.getInt(6)); //Age
                pStmt.setString(8, rset.getString(7)); //Gender
                pStmt.setString(9, rset.getString(8)); //Email
                pStmt.setString(10, rset.getString(9)); //PhNumber
                pStmt.setString(11, rset.getString(10)); //Address
                pStmt.setString(12, rset.getString(11)); //City
                pStmt.setString(13, rset.getString(12)); //State
                pStmt.setString(14, rset.getString(13)); //Country
                pStmt.setString(15, rset.getString(14)); //ZipCode
                pStmt.setString(16, rset.getString(15)); //SSN
                pStmt.setString(17, rset.getString(16)); //Occupation
                pStmt.setString(18, rset.getString(17)); //Employer
                pStmt.setString(19, rset.getString(18)); //EmpContact
                pStmt.setString(20, rset.getString(19)); //PriCarePhy
                pStmt.setString(21, rset.getString(20)); //ReasonVisit
                pStmt.setInt(22, rset.getInt(21)); //SelfPayChk
                pStmt.setString(23, rset.getString(22)); //CreatedDate
                pStmt.setString(24, rset.getString(23)); //Title
                pStmt.setString(25, rset.getString(24)); //MaritalStatus
                pStmt.setString(26, rset.getString(25)); //CreatedBy
                pStmt.setInt(27, rset.getInt(26)); //MRN
                pStmt.setString(28, rset.getString(27)); //COVIDStatus
                pStmt.setInt(29, rset.getInt(28)); //Status
                pStmt.setString(30, rset.getString(29)); //DoctorsName
                pStmt.setString(31, rset.getString(30)); //sync
                pStmt.setString(32, rset.getString(31)); //DateofService
                pStmt.setString(33, rset.getString(32)); //ExtendedMRN
                pStmt.setString(34, rset.getString(33)); //Ethnicity
                pStmt.setString(35, rset.getString(34)); //County
                pStmt.setString(36, rset.getString(35)); //Address2
                pStmt.setString(37, rset.getString(36)); //StreetAddress2
                pStmt.setString(38, rset.getString(37)); //Race
                pStmt.setString(39, rset.getString(38)); //EnterBy
                pStmt.setString(40, rset.getString(39)); //EnterType
                pStmt.setString(41, rset.getString(40)); //EnterIP
                pStmt.executeUpdate();
                pStmt.close();
            }

        } catch (Exception Ex) {
            //helper.SendEmailWithAttachment("Error in insertInvoiceMasterHistory Table ", servletContext, Ex, "DAL - Payments", "insertInvoiceMasterHistory", conn);
            Services.DumException("EditSave - PatientReg", "insertPatientRegHistory", request, Ex, getServletContext());
        }
    }

    public void insertPatientReg_DetailsHistory(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, int PatientRegId) {
        pStmt = null;
        stmt = null;
        rset = null;
        Query = null;

        try {

            Query = " SELECT ID,PatientRegId,MRN,TravellingChk,TravelWhen,TravelWhere,TravelHowLong,COVIDExposedChk,\n" +//8
                    " SympFever,SympBodyAches,SympSoreThroat,SympFatigue,SympRash,SympVomiting,SympDiarrhea,\n" +//15
                    " SympCough,SympRunnyNose,SympNausea,SympFluSymptoms,SympEyeConjunctivitis,Race,CovidExpWhen,\n" +//22
                    " SpCarePhy,SympHeadache,SympLossTaste,SympShortBreath,AddInfoTextArea,SympCongestion,Ethnicity,\n" +//29
                    " GuarantorName,GuarantorDOB,GuarantorNumber,GuarantorSSN,VisitId " +//34
                    " FROM " + database + ".PatientReg_Details WHERE PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                pStmt = conn.prepareStatement(
                        "INSERT INTO " + database + ".PatientReg_DetailsHistory (OldPatientReg_DetailsID,PatientRegId,MRN,TravellingChk,TravelWhen," +//5
                                " TravelWhere,TravelHowLong,COVIDExposedChk,SympFever,\n" +//9
                                " SympBodyAches,SympSoreThroat,SympFatigue,SympRash,SympVomiting,SympDiarrhea,SympCough,SympRunnyNose,\n" +//17
                                " SympNausea,SympFluSymptoms,SympEyeConjunctivitis,Race,CovidExpWhen,SpCarePhy,SympHeadache,SympLossTaste,\n" +//25
                                " SympShortBreath,AddInfoTextArea,SympCongestion,Ethnicity,GuarantorName,GuarantorDOB,\n" +//31
                                " GuarantorNumber,GuarantorSSN,VisitId)" +//33
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                pStmt.setInt(1, rset.getInt(1)); //OldPatientReg_DetailsID
                pStmt.setInt(2, PatientRegId); //PatientRegId
                pStmt.setInt(3, rset.getInt(3)); //MRN
                pStmt.setInt(4, rset.getInt(4)); //TravellingChk
                pStmt.setString(5, rset.getString(5)); //TravelWhen
                pStmt.setString(6, rset.getString(6)); //TravelWhere
                pStmt.setString(7, rset.getString(7)); //TravelHowLong
                pStmt.setInt(8, rset.getInt(8)); //COVIDExposedChk
                pStmt.setString(9, rset.getString(9)); //SympFever
                pStmt.setString(10, rset.getString(10)); //SympBodyAches
                pStmt.setString(11, rset.getString(11)); //SympSoreThroat
                pStmt.setString(12, rset.getString(12)); //SympFatigue
                pStmt.setString(13, rset.getString(13)); //SympRash
                pStmt.setString(14, rset.getString(14)); //SympVomiting
                pStmt.setString(15, rset.getString(15)); //SympDiarrhea
                pStmt.setString(16, rset.getString(16)); //SympCough
                pStmt.setString(17, rset.getString(17)); //SympRunnyNose
                pStmt.setString(18, rset.getString(18)); //SympNausea
                pStmt.setString(19, rset.getString(19)); //SympFluSymptoms
                pStmt.setString(20, rset.getString(20)); //SympEyeConjunctivitis
                pStmt.setString(21, rset.getString(21)); //Race
                pStmt.setString(22, rset.getString(22)); //CovidExpWhen
                pStmt.setString(23, rset.getString(23)); //SpCarePhy
                pStmt.setString(24, rset.getString(24)); //SympHeadache
                pStmt.setString(25, rset.getString(25)); //SympLossTaste
                pStmt.setString(26, rset.getString(26)); //SympShortBreath
                pStmt.setString(27, rset.getString(27)); //AddInfoTextArea
                pStmt.setString(28, rset.getString(28)); //SympCongestion
                pStmt.setString(29, rset.getString(29)); //Ethnicity
                pStmt.setString(30, rset.getString(30)); //GuarantorName
                pStmt.setString(31, rset.getString(31)); //GuarantorDOB
                pStmt.setString(32, rset.getString(32)); //GuarantorNumber
                pStmt.setString(33, rset.getString(33)); //GuarantorSSN
                pStmt.setInt(34, rset.getInt(34)); //VisitId

                pStmt.executeUpdate();
                pStmt.close();
            }

        } catch (Exception Ex) {
            //helper.SendEmailWithAttachment("Error in insertInvoiceMasterHistory Table ", servletContext, Ex, "DAL - Payments", "insertInvoiceMasterHistory", conn);
            Services.DumException("EditSave - PatientReg_Details", "insertPatientReg_DeatailsHistory", request, Ex, getServletContext());
        }
    }


    public void insertInsuranceInfoHistory(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, int PatientRegId) {
        pStmt = null;
        stmt = null;
        rset = null;
        Query = null;

        try {

            Query = " SELECT ID,PatientRegId,WorkersCompPolicy,MotorVehAccident,PriInsurance,MemId,GrpNumber,PriInsuranceName,\n" +
                    "AddressIfDifferent,PrimaryDOB,PrimarySSN,PatientRelationtoPrimary,PrimaryOccupation,PrimaryEmployer,\n" +
                    "EmployerAddress,EmployerPhone,SecondryInsurance,SubscriberName,SubscriberDOB,MemberID_2,GroupNumber_2,\n" +
                    "PatientRelationshiptoSecondry,CreatedDate,VisitId " +//33
                    " FROM " + database + ".InsuranceInfo WHERE PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                pStmt = conn.prepareStatement(
                        "INSERT INTO " + database + ".InsuranceInfoHistory (OldInsuranceInfoID,PatientRegId,WorkersCompPolicy,MotorVehAccident,\n" +
                                "PriInsurance,MemId,GrpNumber,PriInsuranceName,AddressIfDifferent,PrimaryDOB,PrimarySSN,\n" +
                                "PatientRelationtoPrimary,PrimaryOccupation,PrimaryEmployer,EmployerAddress,EmployerPhone,\n" +
                                "SecondryInsurance,SubscriberName,SubscriberDOB,MemberID_2,GroupNumber_2,PatientRelationshiptoSecondry,\n" +
                                "CreatedDate,VisitId\n)" +//33
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                pStmt.setInt(1, rset.getInt(1)); //OldInsuranceInfoID
                pStmt.setInt(2, PatientRegId); //PatientRegId
                pStmt.setInt(3, rset.getInt(3)); //WorkersCompPolicy
                pStmt.setInt(4, rset.getInt(4)); //MotorVehAccident
                pStmt.setString(5, rset.getString(5)); //PriInsurance
                pStmt.setString(6, rset.getString(6)); //MemId
                pStmt.setString(7, rset.getString(7)); //GrpNumber
                pStmt.setString(8, rset.getString(8)); //PriInsuranceName
                pStmt.setString(9, rset.getString(9)); //AddressIfDifferent
                pStmt.setString(10, rset.getString(10)); //PrimaryDOB
                pStmt.setString(11, rset.getString(11)); //PrimarySSN
                pStmt.setString(12, rset.getString(12)); //PatientRelationtoPrimary
                pStmt.setString(13, rset.getString(13)); //PrimaryOccupation
                pStmt.setString(14, rset.getString(14)); //PrimaryEmployer
                pStmt.setString(15, rset.getString(15)); //EmployerAddress
                pStmt.setString(16, rset.getString(16)); //EmployerPhone
                pStmt.setString(17, rset.getString(17)); //SecondryInsurance
                pStmt.setString(18, rset.getString(18)); //SubscriberName
                pStmt.setString(19, rset.getString(19)); //SubscriberDOB
                pStmt.setString(20, rset.getString(20)); //MemberID_2
                pStmt.setString(21, rset.getString(21)); //GroupNumber_2
                pStmt.setString(22, rset.getString(22)); //PatientRelationshiptoSecondry
                pStmt.setString(23, rset.getString(23)); //CreatedDate
                pStmt.setInt(24, rset.getInt(24)); //VisitId

                pStmt.executeUpdate();
                pStmt.close();
            }

        } catch (Exception Ex) {
            //helper.SendEmailWithAttachment("Error in insertInvoiceMasterHistory Table ", servletContext, Ex, "DAL - Payments", "insertInvoiceMasterHistory", conn);
            Services.DumException("EditSave - InsuranceInfo", "insertInsuranceInfoHistory", request, Ex, getServletContext());
        }
    }

    public void insertEmergencyInfoHistory(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, int PatientRegId) {
        pStmt = null;
        stmt = null;
        rset = null;
        Query = null;

        try {

            Query = " SELECT ID,PatientRegId,NextofKinName,RelationToPatient,PhoneNumber,LeaveMessage,Address,\n" +
                    "City,State,Country,ZipCode,CreatedDate,VisitId\n" +
                    " FROM " + database + ".EmergencyInfo WHERE PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                pStmt = conn.prepareStatement(
                        "INSERT INTO " + database + ".EmergencyInfoHistory (OldEmergencyInfoID,PatientRegId,NextofKinName,RelationToPatient," +
                                "PhoneNumber,LeaveMessage,Address,City,State,Country,ZipCode,CreatedDate,VisitId)" +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                pStmt.setInt(1, rset.getInt(1)); //OldEmergencyInfoID
                pStmt.setInt(2, PatientRegId); //PatientRegId
                pStmt.setString(3, rset.getString(3)); //NextofKinName
                pStmt.setString(4, rset.getString(4)); //RelationToPatient
                pStmt.setString(5, rset.getString(5)); //PhoneNumber
                pStmt.setInt(6, rset.getInt(6)); //LeaveMessage
                pStmt.setString(7, rset.getString(7)); //Address
                pStmt.setString(8, rset.getString(8)); //City
                pStmt.setString(9, rset.getString(9)); //State
                pStmt.setString(10, rset.getString(10)); //Country
                pStmt.setString(11, rset.getString(11)); //ZipCode
                pStmt.setString(12, rset.getString(12)); //CreatedDate
                pStmt.setInt(13, rset.getInt(13)); //VisitId

                pStmt.executeUpdate();
                pStmt.close();
            }

        } catch (Exception Ex) {
            //helper.SendEmailWithAttachment("Error in insertInvoiceMasterHistory Table ", servletContext, Ex, "DAL - Payments", "insertInvoiceMasterHistory", conn);
            Services.DumException("EditSave - InsuranceInfo", "insertInsuranceInfoHistory", request, Ex, getServletContext());
        }
    }

    public void insertRandomCheckInfoHistory(HttpServletRequest request, Connection conn, ServletContext servletContext, String database, int PatientRegId) {
        pStmt = null;
        stmt = null;
        rset = null;
        Query = null;

        try {

            Query = " SELECT ID,PatientRegId,ReturnPatient,Google,MapSearch,Billboard,OnlineReview,TV,Website,BuildingSignDriveBy,Facebook,\n" +
                    "School,School_text,Twitter,Magazine,Magazine_text,Newspaper,Newspaper_text,FamilyFriend,FamilyFriend_text,\n" +
                    "UrgentCare,UrgentCare_text,CommunityEvent,CommunityEvent_text,Work_text,Physician_text,Other_text,CreatedDate,\n" +
                    "FrVisitedBefore,FrFamiliyVisitedBefore,FrInternet,FrBillboard,FrGoogle,FrBuildingSignage,FrFacebook,FrLivesNear,\n" +
                    "FrTwitter,FrTV,FrMapSearch,FrEvent,FrPhysicianReferral,FrNeurologyReferral,FrUrgentCareReferral,\n" +
                    "FrOrganizationReferral,FrFriendFamily,VisitId" +
                    " FROM " + database + ".RandomCheckInfo WHERE PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                pStmt = conn.prepareStatement(
                        "INSERT INTO " + database + ".RandomCheckInfoHistory (OldRCIID,PatientRegId,ReturnPatient,Google,MapSearch,Billboard," +//6
                                "OnlineReview,TV,Website,BuildingSignDriveBy,Facebook,School,School_text,Twitter,Magazine,Magazine_text," +
                                "Newspaper,Newspaper_text,FamilyFriend,FamilyFriend_text,UrgentCare,UrgentCare_text,CommunityEvent," +
                                "CommunityEvent_text,Work_text,Physician_text,Other_text,CreatedDate,FrVisitedBefore,FrFamiliyVisitedBefore," +
                                "FrInternet,FrBillboard,FrGoogle,FrBuildingSignage,FrFacebook,FrLivesNear,FrTwitter,FrTV,FrMapSearch," +
                                "FrEvent,FrPhysicianReferral,FrNeurologyReferral,FrUrgentCareReferral,FrOrganizationReferral,FrFriendFamily," +
                                "VisitId)" +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                pStmt.setInt(1, rset.getInt(1)); //OldRCIID
                pStmt.setInt(2, PatientRegId); //PatientRegId
                pStmt.setInt(3, rset.getInt(3)); //ReturnPatient
                pStmt.setInt(4, rset.getInt(4)); //Google
                pStmt.setInt(5, rset.getInt(5)); //MapSearch
                pStmt.setInt(6, rset.getInt(6)); //Billboard
                pStmt.setInt(7, rset.getInt(7)); //OnlineReview
                pStmt.setInt(8, rset.getInt(8)); //TV
                pStmt.setInt(9, rset.getInt(9)); //Website
                pStmt.setInt(10, rset.getInt(10)); //BuildingSignDriveBy
                pStmt.setInt(11, rset.getInt(11)); //Facebook
                pStmt.setInt(12, rset.getInt(12)); //School
                pStmt.setString(13, rset.getString(13)); //School_text
                pStmt.setInt(14, rset.getInt(14)); //Twitter
                pStmt.setInt(15, rset.getInt(15)); //Magazine
                pStmt.setString(16, rset.getString(16)); //Magazine_text
                pStmt.setInt(17, rset.getInt(17)); //Newspaper
                pStmt.setString(18, rset.getString(18)); //Newspaper_text
                pStmt.setInt(19, rset.getInt(19)); //FamilyFriend
                pStmt.setString(20, rset.getString(20)); //FamilyFriend_text
                pStmt.setInt(21, rset.getInt(21)); //UrgentCare
                pStmt.setString(22, rset.getString(22)); //UrgentCare_text
                pStmt.setInt(23, rset.getInt(23)); //CommunityEvent
                pStmt.setString(24, rset.getString(24)); //CommunityEvent_text
                pStmt.setInt(25, rset.getInt(25)); //Work_text
                pStmt.setString(26, rset.getString(26)); //Physician_text
                pStmt.setString(27, rset.getString(27)); //Other_text
                pStmt.setString(28, rset.getString(28)); //CreatedDate
                pStmt.setInt(29, rset.getInt(29)); //FrVisitedBefore
                pStmt.setInt(30, rset.getInt(30)); //FrFamiliyVisitedBefore
                pStmt.setInt(31, rset.getInt(31)); //FrInternet
                pStmt.setInt(32, rset.getInt(32)); //FrBillboard
                pStmt.setInt(33, rset.getInt(33)); //FrGoogle
                pStmt.setInt(34, rset.getInt(34)); //FrBuildingSignage
                pStmt.setInt(35, rset.getInt(35)); //FrFacebook
                pStmt.setInt(36, rset.getInt(36)); //FrLivesNear
                pStmt.setInt(37, rset.getInt(37)); //FrTwitter
                pStmt.setInt(38, rset.getInt(38)); //FrTV
                pStmt.setInt(39, rset.getInt(39)); //FrMapSearch
                pStmt.setInt(40, rset.getInt(40)); //FrEvent
                pStmt.setString(41, rset.getString(41)); //FrPhysicianReferral
                pStmt.setString(42, rset.getString(42)); //FrNeurologyReferral
                pStmt.setString(43, rset.getString(43)); //FrUrgentCareReferral
                pStmt.setString(44, rset.getString(44)); //FrOrganizationReferral
                pStmt.setString(45, rset.getString(45)); //FrFriendFamily
                pStmt.setInt(46, rset.getInt(46)); //VisitId

                pStmt.executeUpdate();
                pStmt.close();
            }

        } catch (Exception Ex) {
            //helper.SendEmailWithAttachment("Error in insertInvoiceMasterHistory Table ", servletContext, Ex, "DAL - Payments", "insertInvoiceMasterHistory", conn);
            Services.DumException("EditSave - InsuranceInfo", "insertInsuranceInfoHistory", request, Ex, getServletContext());
        }
    }

}
