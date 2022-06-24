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

@SuppressWarnings("Duplicates")
public class PatientRegNEW24FEB2021 extends HttpServlet {
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

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId = "";
        int FacilityIndex = 0;
        String DatabaseName = "";
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
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

            if (session.getAttribute("UserId") != null || !session.getAttribute("UserId").equals("")) {
                UserId = session.getAttribute("UserId").toString();
                DatabaseName = session.getAttribute("DatabaseName").toString();
                FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());

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
                case "GetValues":
                    this.GetValues(request, out, conn, context, DatabaseName);
                    break;
                case "SaveData":
                    this.SaveData(request, out, conn, context, DatabaseName);
                    break;
                case "EditValues":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Edit Values from Patient Reg", "Click on View Edit Option from View Patients Option ", FacilityIndex);
                    this.EditValues(request, out, conn, context, DatabaseName);
                    break;
                case "EditValues_New":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Edit Values New from Patient Reg", "Click on View Edit Option from View Patients Option ", FacilityIndex);
                    this.EditValues_New(request, out, conn, context, DatabaseName);
                    break;
                case "EditSave":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Edited Values", "Save Edited Values for Patient ", FacilityIndex);
                    this.EditSave(request, out, conn, context, DatabaseName, FacilityIndex);
                    break;
                case "EditSave_New":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Edited Values NEW", "Save Edited Values NEWfor Patient ", FacilityIndex);
                    this.EditSave_New(request, out, conn, context, DatabaseName);
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


    void GetValues(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String Database) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Date = "";
            String PRF_name = "";
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
            Query = "Select Id, PayerId, PayerName from oe_2.ProfessionalPayers where id in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955) group by PayerId";//where PayerName like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ProfessionalPayersList.append("<option class=Inner value=''>Select Insurance</option>");
            while (rset.next()) {
                ProfessionalPayersList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();

            Query = "Select Id, PayerId, PayerName from oe_2.ProfessionalPayers where id not in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955) group by PayerId ";//where PayerName not like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ProfessionalPayersList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Date", String.valueOf(Date));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("ClientIndex_logo", String.valueOf(ClientIndex));
            Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
            Parser.SetField("ProfessionalPayersList2", String.valueOf(ProfessionalPayersList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/PRF_files/" + PRF_name);
        } catch (Exception ex) {
            out.println(ex.getMessage());
        }
    }

    void SaveData(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String Database) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
//     int ClientIndex = 0;
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
        int SelfPayChk = 0;
        int VerifyChkBox = 0;
        String PatientName = "";
        String ExtendedMRN = "";
        int MRN = 0;

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

            //out.println("Race: "+Race+ "<br>");

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
            if (request.getParameter("TravellingChk") == null) {
                TravellingChk = 0;
            } else {
                TravellingChk = 1;
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
                COVIDExposedChk = 1;
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
//            if (request.getParameter("SympEyeConjunctivitis") == null) {
//                SympEyeConjunctivitis = "0";
//            }
//            else {
//                SympEyeConjunctivitis = "1";
//            }

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
            if (request.getParameter("SelfPayChk") == null) {
                SelfPayChk = 0;
//                System.out.println(SelfPayChk);
            } else {
                SelfPayChk = 1;
//                System.out.println(SelfPayChk);
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
            if (request.getParameter("VerifyChkBox") == null) {
                VerifyChkBox = 0;
            } else {
                VerifyChkBox = 1;
            }
            if (request.getParameter("PatientSignConcent") == null) {
                PatientSignConcent = "";
            } else {
                PatientSignConcent = request.getParameter("PatientSignConcent").trim();
            }
            if (request.getParameter("DateConcent") == null) {
                DateConcent = "0000-00-00";
            } else {
                DateConcent = request.getParameter("DateConcent").trim();
            }
            if (request.getParameter("WitnessConcent") == null) {
                WitnessConcent = "";
            } else {
                WitnessConcent = request.getParameter("WitnessConcent").trim();
            }
            if (request.getParameter("PatientBehalfConcent") == null) {
                PatientBehalfConcent = "";
            } else {
                PatientBehalfConcent = request.getParameter("PatientBehalfConcent").trim();
            }
            if (request.getParameter("RelativeSignConcent") == null) {
                RelativeSignConcent = "";
            } else {
                RelativeSignConcent = request.getParameter("RelativeSignConcent").trim();
            }
            if (request.getParameter("DateConcent2") == null) {
                DateConcent2 = "0000-00-00";
            } else {
                DateConcent2 = request.getParameter("DateConcent2").trim();
            }
            if (request.getParameter("WitnessConcent2") == null) {
                WitnessConcent2 = "";
            } else {
                WitnessConcent2 = request.getParameter("WitnessConcent2").trim();
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
//                System.out.println("Database: "+Database);

                if (ClientIndex == 27) {
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
//                System.out.println("MRN: "+MRN);
//                System.out.println("ExtendedMRN: "+ExtendedMRN);
            } catch (Exception e) {
                out.println("Error 1:" + e.getMessage() + Query);
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".PatientReg (ClientIndex,FirstName,LastName ,MiddleInitial,DOB,Age,Gender ,Email,PhNumber ," +
                                "Address,City ,State,Country,ZipCode,SSN,Occupation ,Employer ,EmpContact,PriCarePhy,ReasonVisit," +
                                "SelfPayChk,CreatedDate,Title, MaritalStatus,CreatedBy, MRN, Status, DateofService, ExtendedMRN, County, Ethnicity, Address2, StreetAddress2) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,0,now(),?,?,?,?,?) ");
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
                MainReceipt.setString(26, ExtendedMRN);
                MainReceipt.setString(27, County);
                MainReceipt.setString(28, Ethnicity);
                MainReceipt.setString(29, Address2);
                MainReceipt.setString(30, StreetAddress2);
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
                        "INSERT INTO " + Database + ".PatientVisit(MRN,PatientRegId,ReasonVisit,VisitNumber,DoctorId,DateofService,CreatedDate," +
                                "CreatedBy) VALUES (?,?,?,1,NULL,now(),now(),?) ");
                MainReceipt.setInt(1, MRN);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, ReasonVisit);
                MainReceipt.setString(4, "OutPatient");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 3.1 Insertion in table PatientVisit- :" + e.getMessage());
            }

            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".PatientReg_Details (PatientRegId,MRN,TravellingChk,TravelWhen,TravelWhere ,TravelHowLong,COVIDExposedChk, " +
                                " SympFever,SympBodyAches ,SympSoreThroat,SympFatigue ," +
                                " SympRash,SympVomiting ,SympDiarrhea,SympCough,SympRunnyNose,SympNausea,SympFluSymptoms ,SympEyeConjunctivitis, Race, CovidExpWhen, SpCarePhy) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
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
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 10- Insertion PatientReg_Details Table :" + e.getMessage());
                return;
            }

            try {
                if (SelfPayChk == 1) {

                    if (ClientIndex == 10 || ClientIndex == 15) {
                        try {
                            final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".PatientAdmissionBundle(PatientRegId,AdmissionBundle,CreatedDate) \nVALUES (?,?,now()) ");
                            MainReceipt.setInt(1, PatientRegId);
                            MainReceipt.setInt(2, AddmissionBundle);
                            MainReceipt.executeUpdate();
                            MainReceipt.close();
                        } catch (Exception e) {
                            System.out.println("Error in PatientAdmissionBundle Table:-- " + e.getMessage());
                        }
                    }

                    final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".InsuranceInfo(PatientRegId,WorkersCompPolicy,MotorVehAccident,PriInsurance,MemId,GrpNumber,PriInsuranceName,AddressIfDifferent,PrimaryDOB,PrimarySSN,PatientRelationtoPrimary,PrimaryOccupation,PrimaryEmployer,EmployerAddress,EmployerPhone,SecondryInsurance,SubscriberName,SubscriberDOB,MemberID_2,GroupNumber_2,PatientRelationshiptoSecondry,CreatedDate) \nVALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
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
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                }
            } catch (Exception e) {
                out.println("Error 4- Insertion InsuranceInfo Table :" + e.getMessage());
                return;
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".EmergencyInfo (PatientRegId,NextofKinName,RelationToPatient,PhoneNumber,LeaveMessage,Address,City,State,Country,ZipCode,CreatedDate) \nVALUES (?,?,?,?,?,?,?,?,?,?,now()) ");
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
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 5- Insertion EmergencyInfo Table :" + e.getMessage());
                return;
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".ConcentToTreatmentInfo (PatientRegId,PatientSign,Date,Witness,PatientBehalfSign,RelativeSign,Date2,Witness2,CreatedDate) \nVALUES (?,?,?,?,?,?,?,?,now()) ");
                MainReceipt.setInt(1, PatientRegId);
                MainReceipt.setString(2, PatientSignConcent);
                MainReceipt.setString(3, DateConcent);
                MainReceipt.setString(4, WitnessConcent);
                MainReceipt.setString(5, PatientBehalfConcent);
                MainReceipt.setString(6, RelativeSignConcent);
                MainReceipt.setString(7, DateConcent2);
                MainReceipt.setString(8, WitnessConcent2);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 6- Insertion ConcentToTreatmentInfo Table :" + e.getMessage());
                return;
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".RandomCheckInfo (PatientRegId,ReturnPatient,Google,MapSearch,Billboard,OnlineReview,TV,Website,BuildingSignDriveBy,Facebook,School,School_text,Twitter,Magazine,Magazine_text,Newspaper,Newspaper_text,FamilyFriend,FamilyFriend_text,UrgentCare,UrgentCare_text,CommunityEvent,CommunityEvent_text,Work_text,Physician_text,Other_text,CreatedDate) \nVALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
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
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 7- Insertion RandomCheckInfo Table :" + e.getMessage());
                return;
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
            Query = "Select Date_format(now(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " We Have Registered You Successfully. Please Wait for Further Processing. " + "" + " <br>DATED: " + Date);
            Parser.SetField("FormName", String.valueOf("PatientReg"));
            Parser.SetField("ActionID", String.valueOf("GetValues&ClientIndex=" + ClientIndex));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
//            Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/orange_2/Exception/Message.html");
            //System.out.println(String.valueOf(Services.GetHtmlPath(this.getServletContext())));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message.html");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    void EditValues(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String Database) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        int ClientIndex = 0;
        int PatientRegId = 0;
        final String MRN = request.getParameter("MRN").trim();
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
        String City = "";
        String State = "";
        String Country = "";
        String COVIDStatus = "";
        String DoctorName = "";
        String DateofService = null;
        String ZipCode = "";
        String SSN = "";
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
        final StringBuffer ReturnPatient = new StringBuffer();
        final StringBuffer Google = new StringBuffer();
        final StringBuffer MapSearch = new StringBuffer();
        final StringBuffer Billboard = new StringBuffer();
        final StringBuffer OnlineReview = new StringBuffer();
        final StringBuffer TV = new StringBuffer();
        final StringBuffer Website = new StringBuffer();
        final StringBuffer BuildingSignDriveBy = new StringBuffer();
        final StringBuffer Facebook = new StringBuffer();
        final StringBuffer School = new StringBuffer();
        String School_text = "";
        final StringBuffer Twitter = new StringBuffer();
        final StringBuffer Magazine = new StringBuffer();
        String Magazine_text = "";
        final StringBuffer Newspaper = new StringBuffer();
        String Newspaper_text = "";
        final StringBuffer FamilyFriend = new StringBuffer();
        String FamilyFriend_text = "";
        final StringBuffer UrgentCare = new StringBuffer();
        String UrgentCare_text = "";
        final StringBuffer CommunityEvent = new StringBuffer();
        String CommunityEvent_text = "";
        final StringBuffer Work_textBuff = new StringBuffer();
        String Work_text = "";
        final StringBuffer Physician_textBuff = new StringBuffer();
        String Physician_text = "";
        final StringBuffer Other_textBuff = new StringBuffer();
        String Other_text = "";
        int SelfPayChk = 0;
        final int VerifyChkBox = 0;
        String PatientName = "";
        String DOS = "";
        String Race = "";
        final StringBuffer TitleBuff = new StringBuffer();
        final StringBuffer RaceBuffer = new StringBuffer();
        final StringBuffer PriInsuranceNameBuff = new StringBuffer();
        final StringBuffer SecondryInsuranceBuff = new StringBuffer();
        final StringBuffer DoctorList = new StringBuffer();
        final StringBuffer MaritalStatusBuff = new StringBuffer();
        final StringBuffer PatientRelationBuff = new StringBuffer();
        final StringBuffer CountryBuff = new StringBuffer();
        final StringBuffer genderBuffMale = new StringBuffer();
        final StringBuffer genderBuffFemale = new StringBuffer();
        final StringBuffer SelfPayChkBuff = new StringBuffer();
        final StringBuffer WorkersCompPolicyBuffYes = new StringBuffer();
        final StringBuffer MotorVehAccidentBuffYes = new StringBuffer();
        final StringBuffer WorkersCompPolicyBuffNo = new StringBuffer();
        final StringBuffer MotorVehAccidentBuffNo = new StringBuffer();
        final StringBuffer PatientRelationshiptoSecondryBuff = new StringBuffer();
        final StringBuffer PatientRelationtoPrimaryBuff = new StringBuffer();
        final StringBuffer CountryBuffER = new StringBuffer();
        final StringBuffer LeaveMessageERBuffYes = new StringBuffer();
        final StringBuffer LeaveMessageERBuffNo = new StringBuffer();
        try {
            Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(City,'-'), IFNULL(State,'-'), IFNULL(ZipCode,'-'), IFNULL(PhNumber,'-'), IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(ID,0), ClientIndex, DATE_FORMAT(CreatedDate, '%d-%m-%Y'), IFNULL(Country,'-'), IFNULL(COVIDStatus, '-'), IFNULL(DoctorsName,'-'), IFNULL(DateofService, '-'), IFNULL(Race,'')   From " + Database + ".PatientReg Where MRN =" + MRN;
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
                Race = rset.getString(29);
            }
            rset.close();
            stmt.close();
            Query = "Select Title from " + Database + ".Title";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            TitleBuff.append("<option value='-1'>Select Title</option>");
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
            DoctorList.append("<option value='-1'>Select Physician</option>");
            while (rset.next()) {
                if (DoctorName.equals(rset.getString(1))) {
                    DoctorList.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(2) + "</option>");
                } else {
                    DoctorList.append("<option value=" + rset.getString(1) + ">" + rset.getString(2) + "</option>");
                }
            }
            rset.close();
            stmt.close();


            Query = "Select Country from " + Database + ".Country";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            CountryBuff.append("<option value='-1'>Select Country</option>");
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
            Query = "Select Value, Race from " + "oe_2" + ".Race";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                if (Race.equals(rset.getString(1))) {
                    RaceBuffer.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(2) + "</option>");
                } else {
                    RaceBuffer.append("<option value=" + rset.getString(1) + ">" + rset.getString(2) + "</option>");
                }
            }
            rset.close();
            stmt.close();
            if (gender.equals("male")) {
                genderBuffMale.append("<input type=\"radio\" name=\"gender\" id=\"genderM\" value=\"male\" checked> Male<br>");
                genderBuffFemale.append("<input type=\"radio\" name=\"gender\" id=\"genderN\" value=\"female\"> Female<br>");
            } else {
                genderBuffMale.append("<input type=\"radio\" name=\"gender\" id=\"genderM\" value=\"male\" > Male<br>");
                genderBuffFemale.append("<input type=\"radio\" name=\"gender\" id=\"genderN\" value=\"female\" checked> Female<br>");
            }
            if (SelfPayChk == 1) {
                SelfPayChkBuff.append("<input type=\"checkbox\" id=\"SelfPayChk\" name=\"SelfPayChk\"  checked />");
            } else {
                SelfPayChkBuff.append("<input type=\"checkbox\" id=\"SelfPayChk\" name=\"SelfPayChk\"  />");
            }
            if (SelfPayChk == 1) {
                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%Y-%m-%d'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%Y-%m-%d'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from " + Database + ".InsuranceInfo  where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    WorkersCompPolicy = rset.getInt(1);
                    MotorVehAccident = rset.getInt(2);
                    if (WorkersCompPolicy == 0) {
                        WorkersCompPolicyBuffNo.append("<input type=\"radio\" name=\"WorkersCompPolicy\" id=\"WorkersCompPolicyN\" value=\"0\" checked> No<br>");
                        WorkersCompPolicyBuffYes.append("<input type=\"radio\" name=\"WorkersCompPolicy\" id=\"WorkersCompPolicyY\" value=\"1\" > Yes<br>");
                    } else {
                        WorkersCompPolicyBuffYes.append("<input type=\"radio\" name=\"WorkersCompPolicy\" id=\"WorkersCompPolicyY\" value=\"1\" checked > Yes<br>");
                        WorkersCompPolicyBuffNo.append("<input type=\"radio\" name=\"WorkersCompPolicy\" id=\"WorkersCompPolicyN\" value=\"0\" > No<br>");
                    }
                    if (MotorVehAccident == 0) {
                        MotorVehAccidentBuffNo.append("<input type=\"radio\" name=\"MotorVehAccident\" id=\"MotorVehAccidentN\" value=\"0\" checked> No<br>");
                        MotorVehAccidentBuffYes.append("<input type=\"radio\" name=\"MotorVehAccident\" id=\"MotorVehAccidentY\" value=\"1\" > Yes<br>");
                    } else {
                        MotorVehAccidentBuffYes.append("<input type=\"radio\" name=\"MotorVehAccident\" id=\"MotorVehAccidentY\" value=\"1\" checked> Yes<br>");
                        MotorVehAccidentBuffNo.append("<input type=\"radio\" name=\"MotorVehAccident\" id=\"MotorVehAccidentN\" value=\"0\" > No<br>");
                    }
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
            Query = "Select Id, PayerId, PayerName from " + Database + ".ProfessionalPayers";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
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
            while (rset.next()) {
                if (PatientRelationshiptoSecondry.equals(rset.getString(1))) {
                    PatientRelationshiptoSecondryBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(1) + "</option>");
                }
                PatientRelationshiptoSecondryBuff.append("<option value=" + rset.getString(1) + " >" + rset.getString(1) + "</option>");
            }
            rset.close();
            stmt.close();
            Query = " Select IFNULL(NextofKinName,'-'), IFNULL(RelationToPatient,'-'), IFNULL(PhoneNumber,'-'),  IFNULL(LeaveMessage,0), IFNULL(Address,'-'), IFNULL(City,'-'), IFNULL(State,'-'), IFNULL(Country,'-'), IFNULL(ZipCode,'-')  from " + Database + ".EmergencyInfo where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                NextofKinName = rset.getString(1);
                RelationToPatientER = rset.getString(2);
                PhoneNumberER = rset.getString(3);
                LeaveMessageER = rset.getInt(4);
                if (LeaveMessageER == 0) {
                    LeaveMessageERBuffNo.append("<input type=\"radio\" name=\"LeaveMessageER\" id=\"LeaveMessageERN\" value=\"0\" checked> No<br>");
                    LeaveMessageERBuffYes.append("<input type=\"radio\" name=\"LeaveMessageER\" id=\"LeaveMessageERY\" value=\"1\" > Yes<br>");
                } else {
                    LeaveMessageERBuffYes.append("<input type=\"radio\" name=\"LeaveMessageER\" id=\"LeaveMessageERY\" value=\"1\" checked> Yes<br>");
                    LeaveMessageERBuffNo.append("<input type=\"radio\" name=\"LeaveMessageER\" id=\"LeaveMessageERN\" value=\"0\" > No<br>");
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
            CountryBuffER.append("<option value='-1'>Select Country</option>");
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
            Query = " Select ReturnPatient, Google, MapSearch, Billboard, OnlineReview, TV, Website, BuildingSignDriveBy, Facebook, School, IFNULL(School_text ,'-'), Twitter, Magazine, IFNULL(Magazine_text,'-'), Newspaper, IFNULL(Newspaper_text,'-'), FamilyFriend, IFNULL(FamilyFriend_text,'-'), UrgentCare, IFNULL(UrgentCare_text,'-'), CommunityEvent, IFNULL(CommunityEvent_text,'-'),  Work_text, Physician_text, Other_text from " + Database + ".RandomCheckInfo where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                if (rset.getInt(1) == 0) {
                    ReturnPatient.append("<input type=\"checkbox\" id=\"ReturnPatient\" name=\"ReturnPatient\">");
                } else {
                    ReturnPatient.append("<input type=\"checkbox\" id=\"ReturnPatient\" name=\"ReturnPatient\" checked>");
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
                    TV.append("<input type=\"checkbox\" id=\"TV\" name=\"TV\"> checked");
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
                    System.out.println("Physician_text IF condition");
                    Work_text = "";
                    Work_textBuff.append("<input type=\"checkbox\" id=\"Work\" name=\"Work\">");
                } else {
                    System.out.println("Physician_text Else condition");
                    Work_textBuff.append("<input type=\"checkbox\" id=\"Work\" name=\"Work\" checked>");
                    Work_text = rset.getString(23);
                }
                if (rset.getString(24) == "" || rset.getString(24) == null || rset.getString(24).equals("")) {
                    System.out.println("Physician_text IF condition");
                    Physician_text = "";
                    Physician_textBuff.append("<input type=\"checkbox\" id=\"Physician\" name=\"Physician\">");
                } else {
                    System.out.println("Physician_text else condition");
                    Physician_text = rset.getString(24);
                    Physician_textBuff.append("<input type=\"checkbox\" id=\"Physician\" name=\"Physician\" checked>");
                }
                if (rset.getString(25) == "" || rset.getString(25) == null || rset.getString(25).equals("")) {
                    System.out.println("OtherText IF condition");
                    Other_text = "";
                    Other_textBuff.append("<input type=\"checkbox\" id=\"Other\" name=\"Other\">");
                } else {
                    System.out.println("OtherText else condition");
                    Other_text = rset.getString(25);
                    Other_textBuff.append("<input type=\"checkbox\" id=\"Other\" name=\"Other\" checked>");
                }
            }
            rset.close();
            stmt.close();
            Query = "Select CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName) from " + Database + ".PatientReg where ID = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientName = rset.getString(1);
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
            Parser.SetField("DoctorList", String.valueOf(DoctorList));
            Parser.SetField("MaritalStatusBuff", String.valueOf(MaritalStatusBuff));
            Parser.SetField("DOB", String.valueOf(DOB));
            Parser.SetField("Age", String.valueOf(Age));
            Parser.SetField("genderBuffMale", String.valueOf(genderBuffMale));
            Parser.SetField("genderBuffFemale", String.valueOf(genderBuffFemale));
            Parser.SetField("Address", String.valueOf(Address));
            Parser.SetField("City", String.valueOf(City));
            Parser.SetField("State", String.valueOf(State));
            Parser.SetField("ZipCode", String.valueOf(ZipCode));
            Parser.SetField("PhNumber", String.valueOf(PhNumber));
            Parser.SetField("RaceBuffer", String.valueOf(RaceBuffer));
            Parser.SetField("SSN", String.valueOf(SSN));
            Parser.SetField("Occupation", String.valueOf(Occupation));
            Parser.SetField("Employer", String.valueOf(Employer));
            Parser.SetField("EmpContact", String.valueOf(EmpContact));
            Parser.SetField("PriCarePhy", String.valueOf(PriCarePhy));
            Parser.SetField("Email", String.valueOf(Email));
            Parser.SetField("ReasonVisit", String.valueOf(ReasonVisit));
            Parser.SetField("SelfPayChkBuff", String.valueOf(SelfPayChkBuff));
            Parser.SetField("CountryBuff", String.valueOf(CountryBuff));
            Parser.SetField("WorkersCompPolicyBuffNo", String.valueOf(WorkersCompPolicyBuffNo));
            Parser.SetField("WorkersCompPolicyBuffYes", String.valueOf(WorkersCompPolicyBuffYes));
            Parser.SetField("MotorVehAccidentBuffYes", String.valueOf(MotorVehAccidentBuffYes));
            Parser.SetField("MotorVehAccidentBuffNo", String.valueOf(MotorVehAccidentBuffNo));
            Parser.SetField("PriInsurance", String.valueOf(PriInsurance));
            Parser.SetField("MemId", String.valueOf(MemId));
            Parser.SetField("GrpNumber", String.valueOf(GrpNumber));
            Parser.SetField("PriInsuranceName", String.valueOf(PriInsuranceNameBuff));
            Parser.SetField("AddressIfDifferent", String.valueOf(AddressIfDifferent));
            Parser.SetField("PrimaryDOB", String.valueOf(PrimaryDOB));
            Parser.SetField("PrimarySSN", String.valueOf(PrimarySSN));
            Parser.SetField("PrimaryOccupation", String.valueOf(PrimaryOccupation));
            Parser.SetField("PrimaryEmployer", String.valueOf(PrimaryEmployer));
            Parser.SetField("EmployerAddress", String.valueOf(EmployerAddress));
            Parser.SetField("EmployerPhone", String.valueOf(EmployerPhone));
            Parser.SetField("SecondryInsurance", String.valueOf(SecondryInsuranceBuff));
            Parser.SetField("SubscriberName", String.valueOf(SubscriberName));
            Parser.SetField("SubscriberDOB", String.valueOf(SubscriberDOB));
            Parser.SetField("MemberID_2", String.valueOf(MemberID_2));
            Parser.SetField("GroupNumber_2", String.valueOf(GroupNumber_2));
            Parser.SetField("PatientRelationtoPrimaryBuff", String.valueOf(PatientRelationtoPrimaryBuff));
            Parser.SetField("PatientRelationshiptoSecondryBuff", String.valueOf(PatientRelationshiptoSecondryBuff));
            Parser.SetField("LeaveMessageERBuffNo", String.valueOf(LeaveMessageERBuffNo));
            Parser.SetField("LeaveMessageERBuffYes", String.valueOf(LeaveMessageERBuffYes));
            Parser.SetField("NextofKinName", String.valueOf(NextofKinName));
            Parser.SetField("RelationToPatientER", String.valueOf(RelationToPatientER));
            Parser.SetField("PhoneNumberER", String.valueOf(PhoneNumberER));
            Parser.SetField("AddressER", String.valueOf(AddressER));
            Parser.SetField("CityER", String.valueOf(CityER));
            Parser.SetField("StateER", String.valueOf(StateER));
            Parser.SetField("ZipCodeER", String.valueOf(ZipCodeER));
            Parser.SetField("CountryBuffER", String.valueOf(CountryBuffER));
            Parser.SetField("PatientSignConcent", String.valueOf(PatientSignConcent));
            Parser.SetField("DateConcent", String.valueOf(DateConcent));
            Parser.SetField("WitnessConcent", String.valueOf(WitnessConcent));
            Parser.SetField("PatientBehalfConcent", String.valueOf(PatientBehalfConcent));
            Parser.SetField("RelativeSignConcent", String.valueOf(RelativeSignConcent));
            Parser.SetField("DateConcent2", String.valueOf(DateConcent2));
            Parser.SetField("WitnessConcent2", String.valueOf(WitnessConcent2));
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
            Parser.SetField("MRN", String.valueOf(MRN));
            if (ClientId == 8) {
                Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditOrange.html");
            } else if (ClientId == 9) {
                Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditVictoria.html");
            } else if (ClientId == 10) {
                Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditOddasa.html");
            } else if (ClientIndex == 12) {
                Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditSAustin.html");
            } else if (ClientIndex == 15) {
                Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditSublime.html");
            } else if (ClientIndex == 23) {
                Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditDemo.html");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    void EditSave(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String Database, int ClientIndex) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        //int ClientIndex = 0;
        final int PatientRegId = 0;
        final String ClientId = request.getParameter("ClientId").trim();
        final String MRN = request.getParameter("MRN").trim();
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
        String Race = "";
        String Address = "";
        String City = "";
        String State = "";
        String Country = "";
        String DoctorName = "";
        String ZipCode = "";
        String SSN = "";
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
        int SelfPayChk = 0;
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
            if (request.getParameter("Race") == null) {
                Race = "0";
            } else {
                Race = request.getParameter("Race").trim();
            }
            if (request.getParameter("Address") == null) {
                Address = "";
            } else {
                Address = request.getParameter("Address").trim();
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
            if (request.getParameter("COVIDStatus_Chk") == null) {
                COVIDStatus = "-1";
            } else {
                COVIDStatus = request.getParameter("COVIDStatus_Chk").trim();
            }
            if (request.getParameter("DoctorName") == null) {
                DoctorName = "0";
            } else {
                DoctorName = request.getParameter("DoctorName").trim();
            }
            if (request.getParameter("DateofService") == null) {
                DateofService = "now()";
            } else {
                DateofService = request.getParameter("DateofService").trim();
            }
            if (request.getParameter("SelfPayChk") == null) {
                SelfPayChk = 0;
            } else {
                SelfPayChk = 1;
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
            if (request.getParameter("VerifyChkBox") == null) {
                VerifyChkBox = 0;
            } else {
                VerifyChkBox = 1;
            }
            if (request.getParameter("PatientSignConcent") == null) {
                PatientSignConcent = "";
            } else {
                PatientSignConcent = request.getParameter("PatientSignConcent").trim();
            }
            if (request.getParameter("DateConcent") == null) {
                DateConcent = "now()";
            } else {
                DateConcent = request.getParameter("DateConcent").trim();
            }
            if (request.getParameter("WitnessConcent") == null) {
                WitnessConcent = "";
            } else {
                WitnessConcent = request.getParameter("WitnessConcent").trim();
            }
            if (request.getParameter("PatientBehalfConcent") == null) {
                PatientBehalfConcent = "";
            } else {
                PatientBehalfConcent = request.getParameter("PatientBehalfConcent").trim();
            }
            if (request.getParameter("RelativeSignConcent") == null) {
                RelativeSignConcent = "";
            } else {
                RelativeSignConcent = request.getParameter("RelativeSignConcent").trim();
            }
            if (request.getParameter("DateConcent2") == null) {
                DateConcent2 = "now()";
            } else {
                DateConcent2 = request.getParameter("DateConcent2").trim();
            }
            if (request.getParameter("WitnessConcent2") == null) {
                WitnessConcent2 = "";
            } else {
                WitnessConcent2 = request.getParameter("WitnessConcent2").trim();
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
            int MaxVisitNumber = 0;
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
                Query = "UPDATE " + Database + ".PatientReg SET Title ='" + Title + "', FirstName = '" + FirstName + "', LastName = '" + LastName + "', MiddleInitial = '" + MiddleInitial + "', " + " DOB = '" + DOB + "', Age = '" + Age + "', Gender = '" + gender + "', Email = '" + Email + "', PhNumber = '" + PhNumber + "', Address = '" + Address + "', City = '" + City + "', " + " State = '" + State + "', Country = '" + Country + "', ZipCode = '" + ZipCode + "', SSN = '" + SSN + "', Occupation = '" + Occupation + "', Employer = '" + Employer + "', " + " EmpContact = '" + EmpContact + "', PriCarePhy = '" + PriCarePhy + "', ReasonVisit = '" + ReasonVisit + "', SelfPayChk = '" + SelfPayChk + "', MaritalStatus = '" + MaritalStatus + "', " + " COVIDStatus = '" + COVIDStatus + "', DoctorsName = '" + DoctorName + "', DateofService = '" + DateofService + "', Race = '" + Race + "' WHERE ID = " + ID;
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
            } catch (Exception e) {
                out.println("Error in getting VisitNumber From PatientVisit" + e.getMessage());
            }

            try {
                Query = "UPDATE " + Database + ".PatientVisit SET ReasonVisit ='" + ReasonVisit + "', DoctorId = '" + DoctorName + "', DateofService = '" + DateofService + "' WHERE PatientRegId = " + ID + " and VisitNumber = " + MaxVisitNumber;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Updating PatientVisit Table:-" + e.getMessage());
            }

            try {
                if (SelfPayChk == 1) {
                    Query = "UPDATE " + Database + ".InsuranceInfo SET WorkersCompPolicy = " + WorkersCompPolicy + ", MotorVehAccident = " + MotorVehAccident + ", PriInsurance = '" + PriInsurance + "', " + " MemId = '" + MemId + "', GrpNumber = '" + GrpNumber + "', PriInsuranceName = '" + PriInsuranceName + "', AddressIfDifferent = '" + AddressIfDifferent + "', " + " PrimaryDOB = '" + PrimaryDOB + "', PrimarySSN = '" + PrimarySSN + "', PatientRelationtoPrimary = '" + PatientRelationtoPrimary + "', PrimaryOccupation = '" + PrimaryOccupation + "', " + " PrimaryEmployer = '" + PrimaryEmployer + "', EmployerAddress = '" + EmployerAddress + "', EmployerPhone = '" + EmployerPhone + "', SecondryInsurance = '" + SecondryInsurance + "', " + " SubscriberName = '" + SubscriberName + "', SubscriberDOB = '" + SubscriberDOB + "', MemberID_2 = '" + MemberID_2 + "', GroupNumber_2 = '" + GroupNumber_2 + "', " + " PatientRelationshiptoSecondry = '" + PatientRelationshiptoSecondry + "' WHERE PatientRegId = " + ID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }
            } catch (Exception e) {
                out.println("Error in Updating Insurance info Table :--" + e.getMessage());
            }
            try {
                Query = "Update " + Database + ".EmergencyInfo set NextofKinName = '" + NextofKinName + "', RelationToPatient = '" + RelationToPatientER + "', PhoneNumber = '" + PhoneNumberER + "', " + " LeaveMessage = " + LeaveMessageER + ", Address = '" + AddressER + "', City = '" + CityER + "', State = '" + StateER + "', Country = '" + CountryER + "', ZipCode = '" + ZipCodeER + "' " + " where PatientRegId = " + ID;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Updating Emergency Info Table:-- " + e.getMessage());
            }
            try {
                Query = "Update " + Database + ".ConcentToTreatmentInfo set PatientSign = '" + PatientSignConcent + "', Date = '" + DateConcent + "', Witness = '" + WitnessConcent + "', " + " PatientBehalfSign = '" + PatientBehalfConcent + "', RelativeSign = '" + RelativeSignConcent + "', Date2 = '" + DateConcent2 + "', Witness2 = '" + WitnessConcent2 + "' " + "Where PatientRegId = " + ID;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Updating ConcentToTreatmentInfo Table:-- " + e.getMessage());
            }
            try {
                Query = "Update " + Database + ".RandomCheckInfo set ReturnPatient = " + ReturnPatient + ", Google = " + Google + ", MapSearch = " + MapSearch + ", Billboard = " + Billboard + ", " + " OnlineReview = " + OnlineReview + ", TV = " + TV + ", Website = " + Website + ", BuildingSignDriveBy = " + BuildingSignDriveBy + ", Facebook = " + Facebook + ", " + " School = " + School + ", School_text = '" + School_text + "', Twitter = " + Twitter + ", Magazine = " + Magazine + ", Magazine_text = '" + Magazine_text + "', " + " Newspaper = " + Newspaper + ", Newspaper_text = '" + Newspaper_text + "', FamilyFriend = " + FamilyFriend + ", FamilyFriend_text = '" + FamilyFriend_text + "', " + " UrgentCare = " + UrgentCare + ", UrgentCare_text = '" + UrgentCare_text + "', CommunityEvent = " + CommunityEvent + ", CommunityEvent_text = '" + CommunityEvent_text + "', " + " Work_text = '" + Work_text + "', Physician_text = '" + Physician_text + "', Other_text = '" + Other_text + "' where PatientRegId = " + ID;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Updating RandomCheckInfo Table:-- " + e.getMessage());
            }
//            final Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("PatientName", String.valueOf(PatientName));
//            Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/oe_2/Exception/Message_Success.html");
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " Information has been Updated ");
            Parser.SetField("FormName", String.valueOf("PatientUpdateInfo_old"));
            Parser.SetField("ActionID", "GetInput&ID=" + ID);
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message.html");
        } catch (Exception e) {
            out.println("Error in Updating:-" + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }


    void EditValues_New(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String Database) {
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
                        "IFNULL(SympBodyAches,0), IFNULL(SympSoreThroat,0), IFNULL(SympFatigue,0), IFNULL(SympRash,0), IFNULL(SympVomiting,0), IFNULL(SympDiarrhea,0), IFNULL(SympCough,0), " +
                        "IFNULL(SympRunnyNose,0), IFNULL(SympNausea,0), IFNULL(SympFluSymptoms,0), IFNULL(SympEyeConjunctivitis,0), IFNULL(Race,''), " +
                        "IFNULL(DATE_FORMAT(CovidExpWhen,'%Y-%m-%d'),''), IFNULL(SpCarePhy,'') " +
                        " from " + Database + ".PatientReg_Details where MRN = " + MRN;
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

                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error in PAtient Reg Details Table: " + e.getMessage());
            }
            Query = "Select Title from " + Database + ".Title";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            TitleBuff.append("<option value='-1'>Select Title</option>");
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
            CountryBuff.append("<option value='-1'>Select Country</option>");
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

            if (ReasonVisit.trim().toUpperCase().equals("COVID TESTING")) {
                ReasonVisitBuff.append("<select class=\"form-control\" id=\"ReasonVisitSelect\" name=\"ReasonVisitSelect\" style=\"color:black;\" onchange=\"DisplayReasonVisitField(this.value);\" required>\n" +
                        "<option value=\"-1\">Select Reason of Visit</option>\n" +
                        "<option value=\"Others\">Emergency</option>\n" +
                        "<option value=\"COVID\" selected>COVID Testing</option>\n" +
                        "</select> \n");
            } else {
                ReasonVisitBuff.append("<select class=\"form-control\" id=\"ReasonVisitSelect\" name=\"ReasonVisitSelect\" style=\"color:black;\" onchange=\"DisplayReasonVisitField(this.value);\" required>\n" +
                        "<option value=\"-1\">Select Reason of Visit</option>\n" +
                        "<option value=\"Others\" selected>Emergency</option>\n" +
                        "<option value=\"COVID\" >COVID Testing</option>\n" +
                        "</select>\n");
            }

            if (ClientId == 27) {
                Query = "Select ReasonVisit, Id from " + Database + ".ReasonVisits ";//where ltrim(rtrim(UPPER(Catagory))) = ltrim(rtrim(UPPER('" + ReasonVisit + "')))";"
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                ReasonVisitBuffN.append("<label><font color=\"black\">Reason For Visit </font></label>");
                ReasonVisitBuffN.append("<select class=\"form-control\" id=\"ReasonVisit\" name=\"ReasonVisit\" style=\"color:black;\" >");
                ReasonVisitBuffN.append("<option value=\"\">Select Reason of Visit</option>\n");
                while (rset.next()) {
                    if (ReasonVisit.trim().equals(rset.getString(1).trim())) {
                        ReasonVisitBuffN.append("<option value=" + rset.getString(2) + " selected>" + rset.getString(1) + "</option>");
                    } else {
                        ReasonVisitBuffN.append("<option value=" + rset.getString(2) + ">" + rset.getString(1) + "</option>");
                    }
                }
                rset.close();
                stmt.close();
                ReasonVisitBuffN.append("</select>");
            } else {
                ReasonVisitBuffN.append("<label><font color=\"black\">Reason For Visit </font></label>");
                ReasonVisitBuffN.append("<input type=\"text\" placeholder=\"\" class=\"form-control\"id=\"ReasonVisit\" name=\"ReasonVisit\" value=" + ReasonVisit + ">");
            }

            if (SelfPayChk == 1) {
                SelfPayChkBuff.append("<input type=\"checkbox\" id=\"SelfPayChk\" name=\"SelfPayChk\" onclick=\"InsuranceShow(this)\" checked />");
            } else {
                SelfPayChkBuff.append("<input type=\"checkbox\" id=\"SelfPayChk\" name=\"SelfPayChk\" onclick=\"InsuranceShow(this)\" />");
            }
            if (SelfPayChk == 1) {
                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%Y-%m-%d'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%Y-%m-%d'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from " + Database + ".InsuranceInfo  where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    WorkersCompPolicy = rset.getInt(1);
                    MotorVehAccident = rset.getInt(2);
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
            Query = "Select Id, PayerId, PayerName from " + Database + ".ProfessionalPayers";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PriInsuranceNameBuff.append("<option value='-1'>--------------</option>");
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
            SecondryInsuranceBuff.append("<option value='-1'>--------------</option>");
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
            CountryBuffER.append("<option value='-1'>Select Country</option>");
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

    void EditSave_New(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String Database) {
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
                TravellingChk = 1;
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
                COVIDExposedChk = 1;
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
            if (request.getParameter("DoctorName") == null) {
                DoctorName = "0";
            } else if (request.getParameter("DoctorName").equals("")) {
                DoctorName = "0";
            } else {
                DoctorName = request.getParameter("DoctorName").trim();
            }
            System.out.println(DoctorName + "-----:DoctorsName");
            if (request.getParameter("DateofService") == null) {
                DateofService = "now()";
            } else {
                DateofService = request.getParameter("DateofService").trim();
            }
            if (request.getParameter("SelfPayChk") == null) {
                SelfPayChk = 0;
            } else {
                SelfPayChk = 1;
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
//            if (request.getParameter("VerifyChkBox") == null) {
//                VerifyChkBox = 0;
//            }
//            else {
//                VerifyChkBox = 1;
//            }
            if (request.getParameter("PatientSignConcent") == null) {
                PatientSignConcent = "";
            } else {
                PatientSignConcent = request.getParameter("PatientSignConcent").trim();
            }
            if (request.getParameter("DateConcent") == null) {
                DateConcent = "now()";
            } else {
                DateConcent = request.getParameter("DateConcent").trim();
            }
            if (request.getParameter("WitnessConcent") == null) {
                WitnessConcent = "";
            } else {
                WitnessConcent = request.getParameter("WitnessConcent").trim();
            }
            if (request.getParameter("PatientBehalfConcent") == null) {
                PatientBehalfConcent = "";
            } else {
                PatientBehalfConcent = request.getParameter("PatientBehalfConcent").trim();
            }
            if (request.getParameter("RelativeSignConcent") == null) {
                RelativeSignConcent = "";
            } else {
                RelativeSignConcent = request.getParameter("RelativeSignConcent").trim();
            }
            if (request.getParameter("DateConcent2") == null) {
                DateConcent2 = "now()";
            } else {
                DateConcent2 = request.getParameter("DateConcent2").trim();
            }
            if (request.getParameter("WitnessConcent2") == null) {
                WitnessConcent2 = "";
            } else {
                WitnessConcent2 = request.getParameter("WitnessConcent2").trim();
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

            if (ClientId.equals("27")) {
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
                Query = "UPDATE " + Database + ".PatientReg SET Title ='" + Title + "', FirstName = '" + FirstName + "', LastName = '" + LastName + "', MiddleInitial = '" + MiddleInitial + "', " + " DOB = '" + DOB + "', Age = '" + Age + "', Gender = '" + gender + "', Email = '" + Email + "', PhNumber = '" + PhNumber + "', Address = '" + Address + "', City = '" + City + "', " + " State = '" + State + "', Country = '" + Country + "', ZipCode = '" + ZipCode + "', SSN = '" + SSN + "', Occupation = '" + Occupation + "', Employer = '" + Employer + "', " + " EmpContact = '" + EmpContact + "', PriCarePhy = '" + PriCarePhy + "', ReasonVisit = '" + ReasonVisit + "', SelfPayChk = '" + SelfPayChk + "', MaritalStatus = '" + MaritalStatus + "', " + " COVIDStatus = '" + COVIDStatus + "', DoctorsName = '" + DoctorName + "', DateofService = '" + DateofService + "', " +
                        " Ethnicity = '" + Ethnicity + "', County = '" + County + "', Address2 = '" + Address2 + "', StreetAddress2 = '" + StreetAddress2 + "' WHERE ID = " + ID;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Updating PatientReg Table:-" + e.getMessage());
            }


            try {
                Query = "Update " + Database + ".PatientReg_Details set TravellingChk = '" + TravellingChk + "',TravelWhen = '" + TravelWhen + "',TravelWhere = '" + TravelWhere + "', " +
                        " TravelHowLong = '" + TravelHowLong + "',COVIDExposedChk = '" + COVIDExposedChk + "',SympFever = '" + SympFever + "',SympBodyAches = '" + SympBodyAches + "'," +
                        " SympSoreThroat = '" + SympSoreThroat + "',SympFatigue = '" + SympFatigue + "',SympRash = '" + SympRash + "',SympVomiting = '" + SympVomiting + "'," +
                        " SympDiarrhea = '" + SympDiarrhea + "',SympCough = '" + SympCough + "',SympRunnyNose = '" + SympRunnyNose + "',SympNausea = '" + SympNausea + "'," +
                        " SympFluSymptoms = '" + SympFluSymptoms + "' ,Race = '" + Race + "', " +
                        " CovidExpWhen = '" + CovidExpWhen + "', SpCarePhy = '" + SpCarePhy + "'  WHERE PatientRegId = " + ID;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error on Update PatientReg_Details:- " + e.getMessage());
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
            } catch (Exception e) {
                out.println("Error in getting VisitNumber From PatientVisit" + e.getMessage());
            }

            try {
                Query = "UPDATE " + Database + ".PatientVisit SET ReasonVisit ='" + ReasonVisit + "', DoctorId = '" + DoctorName + "', DateofService = '" + DateofService + "' WHERE PatientRegId = " + ID + " and VisitNumber = " + MaxVisitNumber;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Updating PatientVisit Table:-" + e.getMessage());
            }

            try {
                int InsuranceDataFound = 0;


                if (SelfPayChk == 1) {
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
                                            "PatientRelationshiptoSecondry,CreatedDate) " +
                                            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
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
                            MainReceipt.executeUpdate();
//                            System.out.println("QUERY --> " + MainReceipt + "<br>");
                            MainReceipt.close();
                        } catch (Exception e) {
                            out.println("Error in INserting InsuranceiNFO Data: " + e.getMessage());
                        }

                    }
                }

            } catch (Exception e) {
                out.println("Error in Updating Insurance info Table :--" + e.getMessage());
            }
            try {
                Query = "Update " + Database + ".EmergencyInfo set NextofKinName = '" + NextofKinName + "', RelationToPatient = '" + RelationToPatientER + "', PhoneNumber = '" + PhoneNumberER + "', " + " LeaveMessage = " + LeaveMessageER + ", Address = '" + AddressER + "', City = '" + CityER + "', State = '" + StateER + "', Country = '" + CountryER + "', ZipCode = '" + ZipCodeER + "' " + " where PatientRegId = " + ID;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Updating Emergency Info Table:-- " + e.getMessage());
            }
            try {
                Query = "Update " + Database + ".ConcentToTreatmentInfo set PatientSign = '" + PatientSignConcent + "', Date = '" + DateConcent + "', Witness = '" + WitnessConcent + "', " + " PatientBehalfSign = '" + PatientBehalfConcent + "', RelativeSign = '" + RelativeSignConcent + "', Date2 = '" + DateConcent2 + "', Witness2 = '" + WitnessConcent2 + "' " + "Where PatientRegId = " + ID;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Updating ConcentToTreatmentInfo Table:-- " + e.getMessage());
            }
            try {
                Query = "Update " + Database + ".RandomCheckInfo set ReturnPatient = " + ReturnPatient + ", Google = " + Google + ", MapSearch = " + MapSearch + ", Billboard = " + Billboard + ", " + " OnlineReview = " + OnlineReview + ", TV = " + TV + ", Website = " + Website + ", BuildingSignDriveBy = " + BuildingSignDriveBy + ", Facebook = " + Facebook + ", " + " School = " + School + ", School_text = '" + School_text + "', Twitter = " + Twitter + ", Magazine = " + Magazine + ", Magazine_text = '" + Magazine_text + "', " + " Newspaper = " + Newspaper + ", Newspaper_text = '" + Newspaper_text + "', FamilyFriend = " + FamilyFriend + ", FamilyFriend_text = '" + FamilyFriend_text + "', " + " UrgentCare = " + UrgentCare + ", UrgentCare_text = '" + UrgentCare_text + "', CommunityEvent = " + CommunityEvent + ", CommunityEvent_text = '" + CommunityEvent_text + "', " + " Work_text = '" + Work_text + "', Physician_text = '" + Physician_text + "', Other_text = '" + Other_text + "' where PatientRegId = " + ID;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Updating RandomCheckInfo Table:-- " + e.getMessage());
            }
//            /md/md.PatientUpdateInfo_old?ActionID=GetInput&ID=' + ID
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " Information has been Updated ");
            Parser.SetField("FormName", String.valueOf("PatientUpdateInfo"));
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
        FirstName = request.getParameter("FirstName").trim();
        LastName = request.getParameter("LastName").trim();
        DOB = request.getParameter("DOB").trim();
        //DOB = String.valueOf(String.valueOf(DOB.substring(6, 10))) + "-" + DOB.substring(0, 2) + "-" + DOB.substring(3, 5);

        try {
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
        int ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());
        String ReasonVisit = request.getParameter("ReasonVisitSelect").trim();
        try {
            if (ClientIndex == 27) {
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
            System.out.println("Error in Getting Reasons:--" + e.getStackTrace());
        }


    }

}
