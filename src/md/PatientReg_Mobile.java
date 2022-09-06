package md;


import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
import java.sql.*;
import java.util.Calendar;

@SuppressWarnings("Duplicates")
public class PatientReg_Mobile extends HttpServlet {
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

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        String UserId = "";
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter(response.getOutputStream());
        final Services supp = new Services();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        String Database = "";
        String Query = "";
        ResultSet rset = null;
        Statement stmt = null;
        int ClientId = 0;
        String DirectoryName = "";
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

            Query = "Select dbname,DirectoryName  from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Database = rset.getString(1);
                DirectoryName = rset.getString(2).trim();
            }
            rset.close();
            stmt.close();
        } catch (Exception ee) {
            out.println(ee.getMessage());
        }

        context = this.getServletContext();
        switch (ActionID) {
            case "GetValues":
                this.GetValues(request, out, conn, context, Database, helper);
                break;
            case "SaveData":
                this.SaveData(request, response, out, conn, context, Database, helper, DirectoryName);
                break;
            case "CheckPatient":
                //UserId = Services.GetCookie("UserId", request);
                //supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Check Duplicate Patients", "Check if the Patient Exist ", ClientId);
                CheckPatient(request, out, conn, context, Database);
                break;
            case "ReasonVisits":
                //UserId = Services.GetCookie("UserId", request);
                //supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get ReasonVisit For Frontline", "Reason VisitS frontLine ", ClientId);
                ReasonVisits(request, out, conn, context, Database);
                break;
        }
        try {
            conn.close();


        } catch (Exception ex) {
        }
        out.flush();
        out.close();
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
            Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers where id  in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955,64,1545,3646,8589,200,201,202,203,204,205,206,207,3649,5978,8206,4763,3465,3466,3467,3468,41,387,388,389,697,698,4757) AND Status != 100 group by PayerId";//where PayerName like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ProfessionalPayersList.append("<option value=''>Select Insurance</option>");
            while (rset.next()) {
                ProfessionalPayersList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();

            //Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers where id not in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955) group by PayerId ";//where PayerName not like '%Texas%'";
            Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers where PayerName like  '%Texas%' OR PayerName like '%TX%' AND Status != 100";//where PayerName not like '%Texas%'";
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
                if (i < 10)
                    Month.append("<option value=0" + i + ">" + month[i - 1] + "</option>");
                else
                    Month.append("<option value=" + i + ">" + month[i - 1] + "</option>");
            }
            for (int i = 1; i <= 31; i++) {
                if (i < 10)
                    Day.append("<option value=0" + i + ">" + i + "</option>");
                else
                    Day.append("<option value=" + i + ">" + i + "</option>");
            }
            for (int i = 1901; i <= year; i++) {
                if (i == year) {
                    Year.append("<option value=" + i + " selected>" + i + "</option>");
                } else {
                    Year.append("<option value=" + i + ">" + i + "</option>");
                }
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Date", String.valueOf(Date));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("ClientIndex_logo", String.valueOf(ClientIndex));
            Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
            Parser.SetField("ProfessionalPayersList2", String.valueOf(ProfessionalPayersList));
            Parser.SetField("Month", String.valueOf(Month));
            Parser.SetField("Day", String.valueOf(Day));
            Parser.SetField("Year", String.valueOf(Year));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/PRF_files/PatientRegForm_Facilities_Mobile.html");

//         if(ClientIndex == 8){
//             Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/PatientRegFormOrange.html");
//         }else if(ClientIndex == 9){
//             Parser.SetField("Date", String.valueOf(Date));
//             Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
//             Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/PatientRegFormVictoria.html");
//         }else if(ClientIndex == 10) {
//             Parser.SetField("Date", String.valueOf(Date));
//             Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
//             Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/PatientRegFormOddasa.html");
//         }
        } catch (Exception ex) {
        }
    }

    void SaveData(final HttpServletRequest request, HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext servletContext, String Database, UtilityHelper helper, String directoryName) {
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
        int COVIDPositveChk = 0;
        String CovidPositiveDate = "";
        String SympEyeConjunctivitis = "0";
        String PriCarePhy = "";
        String ReasonVisit = "";
        int WorkersCompPolicy = 0;
        int MotorVehAccident = 0;
        String PriInsurance = "";
        String MemId = "";
        String GrpNumber = "";
        String PriInsuranceName = "";
        String PriInsurerName = "";
        String PrimaryNumber = "";
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
        String UserId = "";

        String STDrelease = "";
        String releaseRecord = "";
        String Filter_WB_SW_A = "";
        String Filter_WB_SW_Q = "";


        String facilityName = helper.getFacilityName(request, conn, servletContext, ClientIndex);
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

                if (request.getParameter("COVIDPositiveChk") == null) {
                    COVIDPositveChk = 0;
                } else {
                    COVIDPositveChk = 1;
                }
                if (COVIDPositveChk == 1) {
                    if (request.getParameter("CovidPositiveDate") == null) {
                        CovidPositiveDate = "0000-00-00";
                    } else {
                        if (request.getParameter("CovidPositiveDate").equals("")) {
                            CovidPositiveDate = "0000-00-00";
                        } else {
                            CovidPositiveDate = request.getParameter("CovidPositiveDate").trim();
                        }
                    }
                }
                if (request.getParameter("TravellingChk") == null) {
                    TravellingChk = 0;
                } else {
                    TravellingChk = Integer.parseInt(request.getParameter("TravellingChk").trim());
                }
                if (TravellingChk == 1) {
                    if (request.getParameter("TravelWhen") == null) {
                        TravelWhen = "0000-00-00";
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
                if (request.getParameter("PriInsurerName") == null) {
                    PriInsurerName = "";
                } else {
                    PriInsurerName = request.getParameter("PriInsurerName").trim();
                }
                if (request.getParameter("PrimaryNumber") == null) {
                    PrimaryNumber = "";
                } else {
                    PrimaryNumber = request.getParameter("PrimaryNumber").trim();
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

                if(ClientIndex == 41 || ClientIndex == 42 ){
                    if (request.getParameter("releaseRecord") == null) {
                        releaseRecord = "0";
                    } else {
                        releaseRecord = request.getParameter("releaseRecord").trim();
                    }
                    if (request.getParameter("STDrelease") == null) {
                        STDrelease = "0";
                    } else {
                        STDrelease = request.getParameter("STDrelease").trim();
                    }

                    Filter_WB_SW_A = ",STDrelease,releaseRecord";
                    Filter_WB_SW_Q = ",?,?";
                }

            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg_Mobile ** (SaveData^^" + facilityName + " ##MES#001)", servletContext, e, "PatientReg_Mobile", "SaveData", conn);
                Services.DumException("SaveData --- 01", "PatientReg_Mobile ", request, e);
                return;
//                String str = "";
//                for (int i = 0; i < e.getStackTrace().length; ++i) {
//                    str = str + e.getStackTrace()[i] + "<br>";
//                }
//                out.println(str);
            }
//            out.println("TEST TABISH " + "AGTER PARMETER" + "<br>");
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


                Query = "Select UserId from oe.MobileUsers where ClientIndex = " + ClientIndex;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    UserId = rset.getString(1) + "_M";
                }
                rset.close();
                stmt.close();
//                out.println("TEST TABISH " + Query + "<br>");
//                System.out.println("Database: "+Database);
                try {
                    if (ReasonVisit.equals("")) {
                        out.println("Please Select the Reason of Visit from the DropDown that appears besides/below the ReasonVisit");
                        out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
                        return;
                    }

                    if (ClientIndex == 27 || ClientIndex == 29) {
//                        out.println("In ReasonVISIT: "+MRN+"<br>");
                        Query = "Select ReasonVisit from " + Database + ".ReasonVisits where Id = " + ReasonVisit;
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next()) {
                            ReasonVisit = rset.getString(1);
                        }
                        rset.close();
                        stmt.close();
                    }
//                out.println("AFTER Reason VISIT:  " + Query + "<br>");

                    if (MRN == 0) {
                        //out.println("In ZERO First" + MRN + "<br>");
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
//                out.println("TEST TABISH " + Query + "<br>");
                    if (String.valueOf(MRN).length() == 0) {
                        //out.println("In ZERO" + MRN + "<br>");
                        MRN = 310001;
                    } else if (String.valueOf(MRN).length() == 4) {
                        //out.println("In FOUR" + MRN + "<br>");
                        MRN = 310001;
                    } else if (String.valueOf(MRN).length() == 8) {
                        //out.println("In SIX" + MRN + "<br>");
                        MRN = 310001;
                    } else if (String.valueOf(MRN).length() == 6) {
                        //out.println("In SIX" + MRN + "<br>");
                        ++MRN;
                    }

                    if (String.valueOf(ClientIndex).length() == 1) {
                        ExtendedMRN = "100" + ClientIndex + MRN;
                    } else if (String.valueOf(ClientIndex).length() == 2) {
                        ExtendedMRN = "10" + ClientIndex + MRN;
                    } else if (String.valueOf(ClientIndex).length() == 3) {
                        ExtendedMRN = "1" + ClientIndex + MRN;
                    }
                } catch (Exception ex) {
                    helper.SendEmailWithAttachment("Error in PatientReg_Mobile ** (SaveData^^" + facilityName + " ##MES#002)", servletContext, ex, "PatientReg", "SaveData", conn);
                    Services.DumException("SaveData --- 02", "PatientReg_Mobile- ", request, ex);
                    return;

                }
//                System.out.println("MRN: "+MRN);
//                System.out.println("ExtendedMRN: "+ExtendedMRN);
            } catch (Exception e) {
                out.println("Error 1:" + e.getMessage() + Query);
            }
            //out.println("TEST TABISH " + "BEFORE CURRNT DATE" + "<br>");
            Query = "Select now()";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                CurrentDate = rset.getString(1);
            }
            rset.close();
            stmt.close();
            //out.println(CurrentDate + "<br>");
            UtilityHelper utilityHelper = new UtilityHelper();
            String ClientIp = utilityHelper.getClientIp(request);
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".PatientReg (ClientIndex,FirstName,LastName ,MiddleInitial,DOB,Age,Gender ,Email,PhNumber ," +
                                "Address,City ,State,Country,ZipCode,SSN,Occupation ,Employer ,EmpContact,PriCarePhy,ReasonVisit," +
                                "SelfPayChk,CreatedDate,Title, MaritalStatus,CreatedBy, MRN, Status, DateofService, ExtendedMRN, County, Ethnicity, Address2, StreetAddress2, " +
                                " EnterBy, EnterIP,RegisterFrom,ViewDate"+Filter_WB_SW_A+") " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,0,?,?,?,?,?,?,?,?,?,NOW()"+Filter_WB_SW_Q+") ");
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
                MainReceipt.setString(34, "Mobile Registration");
                if (ClientIndex == 41 || ClientIndex == 42) {
                    MainReceipt.setString(35, STDrelease);
                    MainReceipt.setString(36, releaseRecord);
                }
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg_Mobile ** Insertion PatientReg_Mobile Table (SaveData^^" + facilityName + " ##MES#003)", servletContext, ex, "PatientReg_Mobile", "SaveData", conn);
                Services.DumException("SaveData --- 03", "PatientReg_Mobile- ", request, ex);
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
                        "INSERT INTO " + Database + ".PatientVisit(MRN,PatientRegId,ReasonVisit,VisitNumber,DoctorId," +
                                "DateofService,CreatedDate,CreatedBy) VALUES (?,?,?,1,NULL,?,now(),?) ");
                MainReceipt.setInt(1, MRN);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, ReasonVisit);
                MainReceipt.setString(4, CurrentDate);
                MainReceipt.setString(5, "Out Patient");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg_Mobile ** Insertion PatientVisit Table (SaveData^^" + facilityName + " ##MES#004)", servletContext, ex, "PatientReg_Mobile", "SaveData", conn);
                Services.DumException("SaveData --- 04", "PatientReg_Mobile- ", request, ex);
                return;
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
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg_Mobile ** PatientVisit Table (SaveData^^" + facilityName + " ##MES#005)", servletContext, ex, "PatientReg_Mobile", "SaveData", conn);
                Services.DumException("SaveData --- 05", "PatientReg_Mobile- ", request, ex);
                return;
            }
            try {
                if (TravelWhen.equals(null) || TravelWhen.equals("") || TravelWhen.isEmpty())
                    TravelWhen = "0000-00-00";

                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".PatientReg_Details (PatientRegId,MRN,TravellingChk,TravelWhen,TravelWhere ,TravelHowLong,COVIDExposedChk, " +
                                " SympFever,SympBodyAches ,SympSoreThroat,SympFatigue ," +
                                " SympRash,SympVomiting ,SympDiarrhea,SympCough,SympRunnyNose,SympNausea,SympFluSymptoms ,SympEyeConjunctivitis, Race, CovidExpWhen, SpCarePhy, " +
                                " SympHeadache, SympLossTaste, SympShortBreath, SympCongestion, AddInfoTextArea, VisitId, GuarantorName, GuarantorDOB, " +
                                " GuarantorNumber, GuarantorSSN, COVIDPositveChk, CovidPositiveDate) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
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
                MainReceipt.setInt(33, COVIDPositveChk);
                if (CovidPositiveDate.length() > 1)
                    MainReceipt.setString(34, CovidPositiveDate);
                else
                    MainReceipt.setNull(34, Types.DATE);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg_Mobile ** PatientReg_Details Table (SaveData^^" + facilityName + " ##MES#006)", servletContext, ex, "PatientReg_Mobile", "SaveData", conn);
                Services.DumException("SaveData --- 06", "PatientReg_Mobile- ", request, ex);
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
                        } catch (Exception ex) {
                            helper.SendEmailWithAttachment("Error in PatientReg_Mobile ** PatientAdmissionBundle Table (SaveData^^" + facilityName + " ##MES#007)", servletContext, ex, "PatientReg_Mobile", "SaveData", conn);
                            Services.DumException("SaveData --- 07", "PatientReg_Mobile- ", request, ex);
                            return;
                        }
                    }

                    final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".InsuranceInfo(PatientRegId," +
                            "WorkersCompPolicy,MotorVehAccident,PriInsurance,MemId,GrpNumber,PriInsuranceName,AddressIfDifferent,PrimaryDOB," +
                            "PrimarySSN,PatientRelationtoPrimary,PrimaryOccupation,PrimaryEmployer,EmployerAddress,EmployerPhone,SecondryInsurance," +
                            "SubscriberName,SubscriberDOB,MemberID_2,GroupNumber_2,PatientRelationshiptoSecondry,CreatedDate,VisitId)" +
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
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg_Mobile ** Insertion InsuranceInfo Table (SaveData^^" + facilityName + " ##MES#008)", servletContext, ex, "PatientReg_Mobile", "SaveData", conn);
                Services.DumException("SaveData --- 08", "PatientReg_Mobile- ", request, ex);
                return;
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".EmergencyInfo (PatientRegId,NextofKinName,RelationToPatient,PhoneNumber," +
                                "LeaveMessage,Address,City,State,Country,ZipCode,CreatedDate,VisitId) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,now(),?) ");
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
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg_Mobile ** Insertion EmergencyInfo Table (SaveData^^" + facilityName + " ##MES#009)", servletContext, ex, "PatientReg_Mobile", "SaveData", conn);
                Services.DumException("SaveData --- 09", "PatientReg_Mobile- ", request, ex);
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
                } catch (Exception ex) {
                    helper.SendEmailWithAttachment("Error in PatientReg_Mobile ** Insertion RandomCheckInfo Table (SaveData^^" + facilityName + " ##MES#010)", servletContext, ex, "PatientReg_Mobile", "SaveData", conn);
                    Services.DumException("SaveData --- 10", "PatientReg_Mobile- ", request, ex);
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
                } catch (Exception ex) {
                    helper.SendEmailWithAttachment("Error in PatientReg_Mobile ** Insertion RandomCheckInfo Table (SaveData^^" + facilityName + " ##MES#011)", servletContext, ex, "PatientReg_Mobile", "SaveData", conn);
                    Services.DumException("SaveData --- 11", "PatientReg_Mobile- ", request, ex);
                    return;
                }
            }
            Query = "Select CONCAT(IFNULL(Title,''), ' ' , IFNULL(FirstName,''), ' ', IFNULL(MiddleInitial,''), ' ', IFNULL(LastName,'')) from " + Database + ".PatientReg where ID = " + PatientRegId;
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
            while (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();

            //Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers where id  in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955) group by PayerId";//where PayerName like '%Texas%'";
            //Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers where id  in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955) AND Status != 100  group by PayerId";//where PayerName like '%Texas%'";
            StringBuilder ProfessionalPayersList = new StringBuilder();
            Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers where id  in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955,64,1545,3646,8589,200,201,202,203,204,205,206,207,3649,5978,8206,4763,3465,3466,3467,3468,41,387,388,389,697,698,4757) AND Status != 100 group by PayerId";//where PayerName like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ProfessionalPayersList.append("<option value=''>Select Insurance</option>");
            while (rset.next()) {
                ProfessionalPayersList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();

            //Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers where id not in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955) group by PayerId ";//where PayerName not like '%Texas%'";
            Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers where PayerName like  '%Texas%' OR PayerName like '%TX%' AND Status != 100";//where PayerName not like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ProfessionalPayersList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();

/*            Query = "Select Id, PayerId, PayerName from oe_2.ProfessionalPayers where PayerName like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ProfessionalPayersList.append("<option class=Inner value=''>Select Insurance</option>");
            while (rset.next()) {
                ProfessionalPayersList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();

            Query = "Select Id, PayerId, PayerName from oe_2.ProfessionalPayers where PayerName not like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ProfessionalPayersList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();*/

            StringBuffer Month = new StringBuffer();
            StringBuffer Day = new StringBuffer();
            StringBuffer Year = new StringBuffer();

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

            if (ClientIndex == 19 || ClientIndex == 33) {
                String temp = SaveBundle_HopeER(request, out, conn, response, Database, ClientIndex, directoryName, PatientRegId, "REGISTRATION");
                System.out.println("temp " + temp);
//                .print();
                String[] arr = temp.split("~");
                String FileName = arr[2];
                String outputFilePath = arr[1];
                String pageCount = arr[0];
                Parsehtm Parser = new Parsehtm(request);
//                Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " We Have Registered You Successfully " + Message + ". Please walk to the front door and Press the buzzer.  DATED: " + Date);
                Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " We Have Registered You Successfully " + ". Please wait for further processing.  DATED: " + Date);
//                Parser.SetField("MRN", "MRN: " + MRN);
//                Parser.SetField("FormName", "PatientReg");
//                Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex);
                Parser.SetField("Date", String.valueOf(Date));
                Parser.SetField("ClientIndex_logo", String.valueOf(ClientIndex));
                Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
                Parser.SetField("PatientName", String.valueOf(PatientName));
                Parser.SetField("myVal", "1");
                Parser.SetField("UserId", UserId);
                Parser.SetField("Month", String.valueOf(Month));
                Parser.SetField("Day", String.valueOf(Day));
                Parser.SetField("Year", String.valueOf(Year));
                Parser.SetField("pageCount", String.valueOf(pageCount));
                Parser.SetField("FileName", String.valueOf(FileName));
                Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
                Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
                Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/MessageWillow.html");
            } else {
                final Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Date", String.valueOf(Date));
                Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
                Parser.SetField("ClientIndex_logo", String.valueOf(ClientIndex));
                Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
                Parser.SetField("PatientName", String.valueOf(PatientName));
                Parser.SetField("myVal", "1");
                Parser.SetField("Month", String.valueOf(Month));
                Parser.SetField("Day", String.valueOf(Day));
                Parser.SetField("Year", String.valueOf(Year));
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/PRF_files/PatientRegForm_Facilities_Mobile.html");
            }
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in PatientReg_Mobile ** MAIN CATCH (SaveData^^" + facilityName + " ##MES#012)", servletContext, ex, "PatientReg_Mobile", "SaveData", conn);
            Services.DumException("SaveData --- 12", "PatientReg_Mobile- ", request, ex);
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
            Query = "Select dbname from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
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
            System.out.println("Error in Getting Reasons:--" + e.getMessage());
        }


    }

    public String SaveBundle_HopeER(final HttpServletRequest request, final PrintWriter out, final Connection conn, final HttpServletResponse response, final String Database, final int ClientId, final String DirectoryName, int patientRegId, String SignedFrom) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        String Title = "";
        String FirstName = "";
        String FirstNameNoSpaces = "";
        String LastName = "";
        String MiddleInitial = "";
        String MaritalStatus = "";
        String DOB = "";
        String Age = "";
        String gender = "";
        String Email = "";
        String PhNumber = "";
        String Address = "";
        String CityStateZip = "";
        final String State = "";
        final String Country = "";
        final String ZipCode = "";
        String SSN = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        String MRN = "";
        int ClientIndex = 0;
        int pageCount = 0;
        String ClientName = "";
        String DOS = "";
        String DoctorId = null;
        String DoctorName = null;
        int WorkersCompPolicy = 0;
        String WorkersCompPolicyString = "Is this a worker\u2019s comp policy: YES/NO";
        int MotorVehAccident = 0;
        String MotorVehAccidentString = "Is this a Motor Vehicle Accident : YES/NO";
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
        final int LeaveMessageER = 0;
        String AddressER = "";
        final String CityER = "";
        final String StateER = "";
        String LeaveMessageERString = "";
        String CityStateZipER = "";
        final String CountryER = "";
        final String ZipCodeER = "";
        final String DateConcent = "";
        final String WitnessConcent = "";
        final String PatientBehalfConcent = "";
        final String RelativeSignConcent = "";
        final String DateConcent2 = "";
        final String WitnessConcent2 = "";
        final String PatientSignConcent = "";
        String ReturnPatient = "";
        String Google = "";
        String MapSearch = "";
        String Billboard = "";
        String OnlineReview = "";
        String TV = "";
        String Website = "";
        String BuildingSignDriveBy = "";
        String Facebook = "";
        String School = "";
        String School_text = "";
        String Twitter = "";
        String Magazine = "";
        String Magazine_text = "";
        String Newspaper = "";
        String Newspaper_text = "";
        String FamilyFriend = "";
        String FamilyFriend_text = "";
        String UrgentCare = "";
        String UrgentCare_text = "";
        String CommunityEvent = "";
        String CommunityEvent_text = "";
        final String Work = "";
        String Work_text = "";
        final String Physician = "";
        String Physician_text = "";
        final String Other = "";
        String Other_text = "";
        int SelfPayChk = 0;
        final int VerifyChkBox = 0;
        final int ID = patientRegId;// Integer.parseInt(request.getParameter("ID").trim());
        try {
            Query = "select date_format(now(),'%Y%m%d%H%i%s'), DATE_FORMAT(now(), '%m/%d/%Y'), DATE_FORMAT(now(), '%T')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DateTime = rset.getString(1);
                Date = rset.getString(2);
                Time = rset.getString(3);
            }
            rset.close();
            stmt.close();
            try {
                Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-'), IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), IFNULL(DoctorsName,'-')  From " + Database + ".PatientReg Where ID = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    PatientRegId = ID;
                    LastName = rset.getString(1).trim();
                    FirstName = rset.getString(2).trim();
                    FirstNameNoSpaces = FirstName.replaceAll("\\s+", "");
                    MiddleInitial = rset.getString(3).trim();
                    Title = rset.getString(4).trim();
                    MaritalStatus = rset.getString(5);
                    DOB = rset.getString(6);
                    Age = rset.getString(7);
                    gender = rset.getString(8);
                    Address = rset.getString(9);
                    CityStateZip = rset.getString(10);
                    PhNumber = rset.getString(11);
                    SSN = rset.getString(12);
                    Occupation = rset.getString(13);
                    Employer = rset.getString(14);
                    EmpContact = rset.getString(15);
                    PriCarePhy = rset.getString(16);
                    Email = rset.getString(17);
                    ReasonVisit = rset.getString(18);
                    SelfPayChk = rset.getInt(19);
                    MRN = rset.getString(20);
                    ClientIndex = rset.getInt(21);
                    DOS = rset.getString(22);
                    DoctorId = rset.getString(23);
                }
                rset.close();
                stmt.close();
                Query = "Select name from oe.clients where Id = " + ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClientName = rset.getString(1);
                }
                rset.close();
                stmt.close();
                if (!DoctorId.equals("-")) {
//                    out.println("Inside Get Doc Name");
                    Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        DoctorName = rset.getString(1);
                    }
                    rset.close();
                    stmt.close();
                } else {
//                    out.println("Inside Get Doc Name empty");
                    DoctorName = "";
                }
            } catch (Exception e) {
//                out.println("Error In PateintReg:--" + e.getMessage());
                out.println(Query);
            }
            if (SelfPayChk == 1) {
                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%m/%d/%Y'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from " + Database + ".InsuranceInfo  where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    WorkersCompPolicy = rset.getInt(1);
                    MotorVehAccident = rset.getInt(2);
                    if (WorkersCompPolicy == 0) {
                        WorkersCompPolicyString = "Is this a worker\u2019s comp policy: NO";
                    } else {
                        WorkersCompPolicyString = "Is this a worker\u2019s comp policy: YES";
                    }
                    if (MotorVehAccident == 0) {
                        MotorVehAccidentString = "Is this a Motor Vehicle Accident : NO";
                    } else {
                        MotorVehAccidentString = "Is this a Motor Vehicle Accident : YES";
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
            try {
                if (!PriInsuranceName.equals("-") || PriInsuranceName.equals("")) {
//                    out.println("Inside PriInsuranceName");
                    Query = "Select PayerName from " + Database + ".ProfessionalPayers where Id = " + PriInsuranceName;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PriInsuranceName = rset.getString(1);
                    }
                    rset.close();
                    stmt.close();
                }
            } catch (Exception e) {
//                out.println("Error is PriInsurance: " + e.getMessage());
//                out.println(Query);
            }
            Query = "Select IFNULL(NextofKinName,'-'), IFNULL(RelationToPatient,'-'), IFNULL(PhoneNumber,'-'), CASE WHEN LeaveMessage = 1 THEN 'YES' WHEN LeaveMessage = 0 THEN 'NO' ELSE ' YES / NO'END,  IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-') from " + Database + ".EmergencyInfo where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                NextofKinName = rset.getString(1);
                RelationToPatientER = rset.getString(2);
                PhoneNumberER = rset.getString(3);
                LeaveMessageERString = rset.getString(4);
                AddressER = rset.getString(5);
                CityStateZipER = rset.getString(6);
            }
            rset.close();
            stmt.close();
            Query = " Select ReturnPatient, Google, MapSearch, Billboard, OnlineReview, TV, Website, BuildingSignDriveBy, Facebook, School, IFNULL(School_text ,'-'), Twitter, Magazine, IFNULL(Magazine_text,'-'), Newspaper, IFNULL(Newspaper_text,'-'), FamilyFriend, IFNULL(FamilyFriend_text,'-'), UrgentCare, IFNULL(UrgentCare_text,'-'), CommunityEvent, IFNULL(CommunityEvent_text,'-'),  IFNULL(Work_text,'-'), IFNULL(Physician_text, '-'), IFNULL(Other_text,'-') from " + Database + ".RandomCheckInfo where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                if (rset.getInt(1) == 0) {
                    ReturnPatient = "";
                } else {
                    ReturnPatient = "YES";
                }
                if (rset.getInt(2) == 0) {
                    Google = "";
                } else {
                    Google = "YES";
                }
                if (rset.getInt(3) == 0) {
                    MapSearch = "";
                } else {
                    MapSearch = "YES";
                }
                if (rset.getInt(4) == 0) {
                    Billboard = "";
                } else {
                    Billboard = "YES";
                }
                if (rset.getInt(5) == 0) {
                    OnlineReview = "";
                } else {
                    OnlineReview = "YES";
                }
                if (rset.getInt(6) == 0) {
                    TV = "";
                } else {
                    TV = "YES";
                }
                if (rset.getInt(7) == 0) {
                    Website = "";
                } else {
                    Website = "YES";
                }
                if (rset.getInt(8) == 0) {
                    BuildingSignDriveBy = "";
                } else {
                    BuildingSignDriveBy = "YES";
                }
                if (rset.getInt(9) == 0) {
                    Facebook = "";
                } else {
                    Facebook = "YES";
                }
                if (rset.getInt(10) == 0) {
                    School = "";
                    School_text = "";
                } else {
                    School = "YES";
                    School_text = rset.getString(11);
                }
                if (rset.getInt(12) == 0) {
                    Twitter = "";
                } else {
                    Twitter = "YES";
                }
                if (rset.getInt(13) == 0) {
                    Magazine = "";
                    Magazine_text = "";
                } else {
                    Magazine = "YES";
                    Magazine_text = rset.getString(14);
                }
                if (rset.getInt(15) == 0) {
                    Newspaper = "";
                    Newspaper_text = "";
                } else {
                    Newspaper = "YES";
                    Newspaper_text = rset.getString(16);
                }
                if (rset.getInt(17) == 0) {
                    FamilyFriend = "";
                    FamilyFriend_text = "";
                } else {
                    FamilyFriend = "YES";
                    FamilyFriend_text = rset.getString(18);
                }
                if (rset.getInt(19) == 0) {
                    UrgentCare = "";
                    UrgentCare_text = "";
                } else {
                    UrgentCare = "YES";
                    UrgentCare_text = rset.getString(20);
                }
                if (rset.getInt(21) == 0) {
                    CommunityEvent = "";
                    CommunityEvent_text = "";
                } else {
                    CommunityEvent = "YES";
                    CommunityEvent_text = rset.getString(22);
                }
                if (rset.getString(23) == "" || rset.getString(23) == null) {
                    Work_text = "";
                } else {
                    Work_text = rset.getString(23);
                }
                if (rset.getString(24) == "" || rset.getString(24) == null) {
                    Physician_text = "";
                } else {
                    Physician_text = rset.getString(24);
                }
                if (rset.getString(25) == "" || rset.getString(25) == null) {
                    Other_text = "";
                } else {
                    Other_text = rset.getString(25);
                }
            }
            rset.close();
            stmt.close();
            String inputFilePath = "";
            final InetAddress ip = InetAddress.getLocalHost();
            final String hostname = ip.getHostName();
            System.out.println("Your current IP address : " + ip);
            System.out.println("Your current Hostname : " + hostname);
//            if (hostname.trim().equals("dev-rover-01")) {
//                inputFilePath = "";
//            } else {
//                inputFilePath = "/sftpdrive";
//            }
            System.out.println("Your current inputFilePath : " + inputFilePath);
            final String filename = FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf";
            inputFilePath = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/adminsaustin.pdf";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + filename;
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            pageCount = pdfReader.getNumberOfPages();
//            final GenerateBarCode barCode = new GenerateBarCode();
//            final String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
//            final Image image = Image.getInstance(BarCodeFilePath);
//            image.scaleAbsolute(150.0f, 30.0f);
            for (int i = 1; i <= pdfReader.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 750.0f);
                    pdfContentByte.showText("MRN: " + MRN);
                    pdfContentByte.endText();
//                    image.setAbsolutePosition(10.0f, 710.0f);
//                    pdfContentByte.addImage(image);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 570.0f);
                    pdfContentByte.showText(ReturnPatient);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 555.0f);
                    pdfContentByte.showText(Google);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 540.0f);
                    pdfContentByte.showText(MapSearch);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 525.0f);
                    pdfContentByte.showText(Billboard);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 505.0f);
                    pdfContentByte.showText(OnlineReview);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 490.0f);
                    pdfContentByte.showText(TV);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 470.0f);
                    pdfContentByte.showText(Website);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 455.0f);
                    pdfContentByte.showText(BuildingSignDriveBy);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 440.0f);
                    pdfContentByte.showText(Facebook);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 423.0f);
                    pdfContentByte.showText(School);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190.0f, 423.0f);
                    pdfContentByte.showText(School_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 405.0f);
                    pdfContentByte.showText(Twitter);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 385.0f);
                    pdfContentByte.showText(Magazine);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 385.0f);
                    pdfContentByte.showText(Magazine_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 365.0f);
                    pdfContentByte.showText(Newspaper);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 365.0f);
                    pdfContentByte.showText(Newspaper_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 350.0f);
                    pdfContentByte.showText(FamilyFriend);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 350.0f);
                    pdfContentByte.showText(FamilyFriend_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 336.0f);
                    pdfContentByte.showText(UrgentCare);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 336.0f);
                    pdfContentByte.showText(UrgentCare_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 320.0f);
                    pdfContentByte.showText(CommunityEvent);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250.0f, 320.0f);
                    pdfContentByte.showText(CommunityEvent_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0f, 265.0f);
                    pdfContentByte.showText(Work_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(180.0f, 252.0f);
                    pdfContentByte.showText(Physician_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0f, 233.0f);
                    pdfContentByte.showText(Other_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 125.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(480.0f, 125.0f);
                    pdfContentByte.showText(Time);
                    pdfContentByte.endText();
                }
                if (i == 2) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 750.0f);
                    pdfContentByte.showText("MRN: " + MRN);
                    pdfContentByte.endText();
//                    image.setAbsolutePosition(10.0f, 710.0f);
//                    pdfContentByte.addImage(image);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 765.0f);
                    pdfContentByte.showText(LastName + " , " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 755.0f);
                    pdfContentByte.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 745.0f);
                    pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 735.0f);
                    pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 725.0f);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setTextMatrix(105.0f, 640.0f);
                    pdfContentByte.showText(LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350.0f, 640.0f);
                    pdfContentByte.showText(FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500.0f, 640.0f);
                    pdfContentByte.showText(MiddleInitial);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 605.0f);
                    pdfContentByte.showText("Title: " + Title);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 600.0f);
                    pdfContentByte.showText(MaritalStatus);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340.0f, 600.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(440.0f, 600.0f);
                    pdfContentByte.showText(Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(510.0f, 600.0f);
                    pdfContentByte.showText(gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 570.0f);
                    pdfContentByte.showText(Address);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260.0f, 570.0f);
                    pdfContentByte.showText(CityStateZip);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460.0f, 570.0f);
                    pdfContentByte.showText(PhNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50.0f, 540.0f);
                    pdfContentByte.showText(SSN);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0f, 540.0f);
                    pdfContentByte.showText(Occupation);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350.0f, 540.0f);
                    pdfContentByte.showText(Employer);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(470.0f, 540.0f);
                    pdfContentByte.showText(EmployerPhone);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 510.0f);
                    pdfContentByte.showText(PriCarePhy);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 510.0f);
                    pdfContentByte.showText(Email);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 510.0f);
                    pdfContentByte.showText(ReasonVisit);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 445.0f);
                    pdfContentByte.showText(WorkersCompPolicyString);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(330.0f, 445.0f);
                    pdfContentByte.showText(MotorVehAccidentString);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 415.0f);
                    pdfContentByte.showText(PriInsurance);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(280.0f, 415.0f);
                    pdfContentByte.showText(MemId);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420.0f, 415.0f);
                    pdfContentByte.showText(GrpNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 375.0f);
                    pdfContentByte.showText(PriInsuranceName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260.0f, 375.0f);
                    pdfContentByte.showText(AddressIfDifferent);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460.0f, 375.0f);
                    pdfContentByte.showText(CityStateZip);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 335.0f);
                    pdfContentByte.showText(PrimaryDOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 335.0f);
                    pdfContentByte.showText(PrimarySSN);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(270.0f, 335.0f);
                    pdfContentByte.showText(PatientRelationtoPrimary);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460.0f, 335.0f);
                    pdfContentByte.showText(PhNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 300.0f);
                    pdfContentByte.showText(PrimaryOccupation);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 300.0f);
                    pdfContentByte.showText(PrimaryEmployer);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260.0f, 300.0f);
                    pdfContentByte.showText(EmployerAddress);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460.0f, 300.0f);
                    pdfContentByte.showText(EmployerPhone);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 275.0f);
                    pdfContentByte.showText(SecondryInsurance);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260.0f, 275.0f);
                    pdfContentByte.showText(SubscriberName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420.0f, 275.0f);
                    pdfContentByte.showText(SubscriberDOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60.0f, 240.0f);
                    pdfContentByte.showText(PatientRelationshiptoSecondry);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260.0f, 240.0f);
                    pdfContentByte.showText(MemberID_2);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420.0f, 240.0f);
                    pdfContentByte.showText(GroupNumber_2);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50.0f, 190.0f);
                    pdfContentByte.showText(NextofKinName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 190.0f);
                    pdfContentByte.showText(RelationToPatientER);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350.0f, 190.0f);
                    pdfContentByte.showText(PhoneNumberER);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(510.0f, 190.0f);
                    pdfContentByte.showText(LeaveMessageERString);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 150.0f);
                    pdfContentByte.showText(AddressER);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350.0f, 150.0f);
                    pdfContentByte.showText(CityStateZipER);
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(440.0f, 75.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
                if (i == 3) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 750.0f);
                    pdfContentByte.showText("MRN: " + MRN);
                    pdfContentByte.endText();
//                    image.setAbsolutePosition(10.0f, 710.0f);
//                    pdfContentByte.addImage(image);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 765.0f);
                    pdfContentByte.showText(LastName + " , " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 755.0f);
                    pdfContentByte.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 745.0f);
                    pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 735.0f);
                    pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 725.0f);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(325.0f, 210.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340.0f, 130.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
                if (i == 4) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 750.0f);
                    pdfContentByte.showText("MRN: " + MRN);
                    pdfContentByte.endText();
//                    image.setAbsolutePosition(10.0f, 710.0f);
//                    pdfContentByte.addImage(image);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 765.0f);
                    pdfContentByte.showText(LastName + " , " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 755.0f);
                    pdfContentByte.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 745.0f);
                    pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 735.0f);
                    pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 725.0f);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(285.0f, 120.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
                if (i == 5) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 750.0f);
                    pdfContentByte.showText("MRN: " + MRN);
                    pdfContentByte.endText();
//                    image.setAbsolutePosition(10.0f, 710.0f);
//                    pdfContentByte.addImage(image);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 765.0f);
                    pdfContentByte.showText(LastName + " , " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 755.0f);
                    pdfContentByte.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 745.0f);
                    pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 735.0f);
                    pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 725.0f);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(440.0f, 390.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(330.0f, 250.0f);
                    pdfContentByte.showText(" ");
                    pdfContentByte.endText();
                }
                if (i == 6) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 750.0f);
                    pdfContentByte.showText("MRN: " + MRN);
                    pdfContentByte.endText();
//                    image.setAbsolutePosition(10.0f, 710.0f);
//                    pdfContentByte.addImage(image);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 765.0f);
                    pdfContentByte.showText(LastName + " , " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 755.0f);
                    pdfContentByte.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 745.0f);
                    pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 735.0f);
                    pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 725.0f);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(95.0f, 550.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(300.0f, 550.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(490.0f, 550.0f);
                    pdfContentByte.showText(PatientRelationtoPrimary);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(120.0f, 525.0f);
                    pdfContentByte.showText(PriInsuranceName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(180.0f, 500.0f);
                    pdfContentByte.showText(MemId);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 470.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 365.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(330.0f, 200.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();

                }
                if (i == 7) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 750.0f);
                    pdfContentByte.showText("MRN: " + MRN);
                    pdfContentByte.endText();
//                    image.setAbsolutePosition(10.0f, 710.0f);
//                    pdfContentByte.addImage(image);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 765.0f);
                    pdfContentByte.showText(LastName + " , " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 755.0f);
                    pdfContentByte.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 745.0f);
                    pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 735.0f);
                    pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 725.0f);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(130.0f, 490.0f);
                    pdfContentByte.showText(" ");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 465.0f);
                    pdfContentByte.showText(SubscriberName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340.0f, 465.0f);
                    pdfContentByte.showText(SubscriberDOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 440.0f);
                    pdfContentByte.showText(MemberID_2);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260.0f, 440.0f);
                    pdfContentByte.showText(GroupNumber_2);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(120.0f, 415.0f);
                    pdfContentByte.showText(" ");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50.0f, 240.0f);
                    pdfContentByte.showText(" ");
                    pdfContentByte.endText();

                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 130.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 100.0f);
                    pdfContentByte.showText(Date);
                }
                if (i == 8) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 750.0f);
                    pdfContentByte.showText("MRN: " + MRN);
                    pdfContentByte.endText();
//                    image.setAbsolutePosition(10.0f, 710.0f);
//                    pdfContentByte.addImage(image);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 765.0f);
                    pdfContentByte.showText(LastName + " , " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 755.0f);
                    pdfContentByte.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 745.0f);
                    pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 735.0f);
                    pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 725.0f);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50.0f, 620.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 620.0f);
                    pdfContentByte.showText(PhNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50.0f, 580.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 580.0f);
                    pdfContentByte.showText(SSN);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(130.0f, 480.0f);
                    pdfContentByte.showText(" ");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(130.0f, 440.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340.0f, 440.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(130.0f, 400.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(130.0f, 360.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 130.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
                if (i == 9) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 750.0f);
                    pdfContentByte.showText("MRN: " + MRN);
                    pdfContentByte.endText();
//                    image.setAbsolutePosition(10.0f, 710.0f);
//                    pdfContentByte.addImage(image);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 765.0f);
                    pdfContentByte.showText(LastName + " , " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 755.0f);
                    pdfContentByte.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 745.0f);
                    pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 735.0f);
                    pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 725.0f);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();


                }
                if (i == 10) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 750.0f);
                    pdfContentByte.showText("MRN: " + MRN);
                    pdfContentByte.endText();
//                    image.setAbsolutePosition(10.0f, 710.0f);
//                    pdfContentByte.addImage(image);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 765.0f);
                    pdfContentByte.showText(LastName + " , " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 755.0f);
                    pdfContentByte.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 745.0f);
                    pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 735.0f);
                    pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 725.0f);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();
                }
                if (i == 11) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 750.0f);
                    pdfContentByte.showText("MRN: " + MRN);
                    pdfContentByte.endText();
//                    image.setAbsolutePosition(10.0f, 710.0f);
//                    pdfContentByte.addImage(image);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 765.0f);
                    pdfContentByte.showText(LastName + " , " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 755.0f);
                    pdfContentByte.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 745.0f);
                    pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 735.0f);
                    pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 725.0f);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 90.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper.close();
            pdfReader.close();
//            final File pdfFile = new File(outputFilePath);
////            response.setContentType("application/pdf");
////            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
////            response.setContentLength((int) pdfFile.length());
//            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
//            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
//            int bytes;
//            while ((bytes = fileInputStream.read()) != -1) {
//                responseOutputStream.write(bytes);
//            }

            return pageCount + "~" + outputFilePath + "~" + filename;//FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf";
        } catch (Exception e) {
//            out.println(e.getMessage());
        }
        return "";
    }

}
