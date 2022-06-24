// 
// Decompiled by Procyon v0.5.36
// 

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;

@SuppressWarnings("Duplicates")
public class PatientReg2_Mobile extends HttpServlet {
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
        final String UserId = "";
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();
        ServletContext context = null;
        UtilityHelper helper = new UtilityHelper();
        try {
            context = this.getServletContext();
            conn = Services.getMysqlConn(context);
            switch (ActionID) {
                case "Victoria_2":
                    this.Victoria_2(request, out, conn, context);
                    break;
                case "Victoria_22":
                    this.Victoria_22(request, out, conn, context);
                    break;
                case "SaveDataVictoria":
                    this.SaveDataVictoria(request, out, conn, context, response, helper);
                    break;
                default:
                    out.println("Under Development!!!");
                    break;
            }
            try {
                conn.close();
            } catch (Exception ex) {
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    void Victoria_2(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Date = "";
            String PRFName = "";
            int ClientIndex = 0;
            final StringBuffer ProfessionalPayersList = new StringBuffer();
            StringBuffer Month = new StringBuffer();
            StringBuffer Day = new StringBuffer();
            StringBuffer Year = new StringBuffer();
            String ClientId = request.getParameter("ClientId").trim();
            //out.println(ClientId);
            Query = "Select Date_format(now(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();
            Query = "Select Id from oe.clients where ltrim(rtrim(UPPER(name))) = ltrim(rtrim(UPPER('" + ClientId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientIndex = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            Query = "Select Id, PayerId, PayerName from oe_2.ProfessionalPayers where PayerName like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ProfessionalPayersList.append("<option class=Inner value='-1'>Select Insurance</option>");
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
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Date", String.valueOf(Date));
            Parser.SetField("ClientIndex", String.valueOf(ClientId));
            Parser.SetField("Month", String.valueOf(Month));
            Parser.SetField("Day", String.valueOf(Day));
            Parser.SetField("Year", String.valueOf(Year));
            Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
            if (ClientIndex == 9) {
                Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(this.getServletContext()))) + "Forms/PatientRegFormVictoria_2_Mobile.html");
            } else if (ClientIndex == 28) {
                Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(this.getServletContext()))) + "Forms/PatientRegFormERDallas_Mobile.html");
            }
        } catch (Exception ex) {
            out.println("Error:" + ex.getMessage());
        }
    }

    void Victoria_22(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Date = "";
            int ClientIndex = 0;
            final StringBuffer ProfessionalPayersList = new StringBuffer();
            final String ClientId = request.getParameter("ClientId").trim();
            Query = "Select Date_format(now(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();
            Query = "Select Id from oe.clients where ltrim(rtrim(UPPER(name))) = ltrim(rtrim(UPPER('" + ClientId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientIndex = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            Query = "Select Id, PayerId, PayerName from oe_2.ProfessionalPayers where PayerName like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ProfessionalPayersList.append("<option class=Inner value='-1'>Select Insurance</option>");
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
            stmt.close();
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Date", String.valueOf(Date));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
            Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(this.getServletContext()))) + "Forms/PatientRegFormVictoria_22.html");
        } catch (Exception ex) {
        }
    }

    void SaveDataVictoria(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, UtilityHelper helper) {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Database = "";
        String DirectoryName = "";
        int MRN = 0;
        String ExtendedMRN = "0";
        int PatientRegId = 0;
        String Date = "";
        final String ClientId = request.getParameter("ClientId").trim();
        int ClientIndex = 0;
        String Title = null;
        String FirstName = null;
        String LastName = null;
        String MiddleInitial = null;
        String County = null;
        String DOB = null;
        String Age = null;
        String gender = null;
        String Email = null;
        String ConfirmEmail = null;
        String MaritalStatus = null;
        String AreaCode = null;
        String PhNumber = null;
        String Address = null;
        String Address2 = null;
        String City = null;
        String State = null;
        String ZipCode = null;
        String Country = null;
        String Ethnicity = null;
        String Race = null;
        final String Ethnicity_OthersText = null;
        String SSN = null;
        String EmployementChk = "0";
        String Employer = null;
        String Occupation = null;
        String EmpContact = null;
        String PrimaryCarePhysicianChk = "0";
        String PriCarePhy = null;
        String ReasonVisit = null;

        String SympChkCOVID = "0";
        String DateSympOnset = null;
        String SympFever = "0";
        String SympCough = "0";
        String SympShortBreath = "0";
        String SympFatigue = "0";
        String SympMuscBodyAches = "0";
        String SympHeadache = "0";
        String SympLossTaste = "0";
        String SympSoreThroat = "0";
        String SympCongestionRunNos = "0";
        String SympNauseaVomit = "0";
        String SympDiarrhea = "0";
        String SympPerPainChest = "0";
        String SympNewConfusion = "0";
        String SympInabWake = "0";
        String SympOthers = "0";
        String SympOthersTxt = "";
        String EmpHealthChk = null;
        String PregChk = null;
        String TestForTravel = null;


        String PriCarePhyAddress = "";
        String PriCarePhyAddress2 = "";
        String PriCarePhyCity = null;
        String PriCarePhyState = null;
        String PriCarePhyZipCode = null;
        String PatientMinorChk = "0";
        String GuarantorChk = "0";
        String GuarantorEmployer = null;
        String GuarantorEmployerAreaCode = null;
        String GuarantorEmployerPhNumber = null;
        String GuarantorEmployerAddress = "";
        String GuarantorEmployerAddress2 = "";
        String GuarantorEmployerCity = null;
        String GuarantorEmployerState = null;
        String GuarantorEmployerZipCode = null;
        String WorkersCompPolicyChk = "0";
        String WCPDateofInjury = null;
        String WCPCaseNo = null;
        String WCPGroupNo = null;
        String WCPMemberId = null;
        String WCPInjuryRelatedAutoMotorAccident = "0";
        String WCPInjuryRelatedWorkRelated = "0";
        String WCPInjuryRelatedOtherAccident = "0";
        String WCPInjuryRelatedNoAccident = "0";
        String WCPInjuryOccurVehicle = "0";
        String WCPInjuryOccurWork = "0";
        String WCPInjuryOccurHome = "0";
        String WCPInjuryOccurOther = "0";
        String WCPInjuryDescription = null;
        String WCPHRFirstName = null;
        String WCPHRLastName = null;
        String WCPHRAreaCode = null;
        String WCPHRPhoneNumber = null;
        String WCPHRAddress = "";
        String WCPHRAddress2 = "";
        String WCPHRCity = null;
        String WCPHRState = null;
        String WCPHRZipCode = null;
        String WCPPlanName = null;
        String WCPCarrierName = null;
        String WCPPayerAreaCode = null;
        String WCPPayerPhoneNumber = null;
        String WCPCarrierAddress = "";
        String WCPCarrierAddress2 = "";
        String WCPCarrierCity = null;
        String WCPCarrierState = null;
        String WCPCarrierZipCode = null;
        String WCPAdjudicatorFirstName = null;
        String WCPAdjudicatorLastName = null;
        String WCPAdjudicatorAreaCode = null;
        String WCPAdjudicatorPhoneNumber = null;
        String WCPAdjudicatorFaxAreaCode = null;
        String WCPAdjudicatorFaxPhoneNumber = null;
        String MotorVehicleAccidentChk = "0";
        String AutoInsuranceInformationChk = "0";
        String AIIDateofAccident = null;
        String AIIAutoClaim = null;
        String AIIAccidentLocationAddress = null;
        String AIIAccidentLocationAddress2 = null;
        String AIIAccidentLocationCity = null;
        String AIIAccidentLocationState = null;
        String AIIAccidentLocationZipCode = null;
        String AIIRoleInAccident = null;
        String AIITypeOfAutoIOnsurancePolicy = null;
        String AIIPrefixforReponsibleParty = null;
        String AIIFirstNameforReponsibleParty = null;
        String AIIMiddleNameforReponsibleParty = null;
        String AIILastNameforReponsibleParty = null;
        String AIISuffixforReponsibleParty = null;
        String AIICarrierResponsibleParty = null;
        String AIICarrierResponsiblePartyAddress = null;
        String AIICarrierResponsiblePartyAddress2 = null;
        String AIICarrierResponsiblePartyCity = null;
        String AIICarrierResponsiblePartyState = null;
        String AIICarrierResponsiblePartyZipCode = null;
        String AIICarrierResponsiblePartyAreaCode = null;
        String AIICarrierResponsiblePartyPhoneNumber = null;
        String AIICarrierResponsiblePartyPolicyNumber = null;
        String AIIResponsiblePartyAutoMakeModel = null;
        String AIIResponsiblePartyLicensePlate = null;
        String AIIFirstNameOfYourPolicyHolder = null;
        String AIILastNameOfYourPolicyHolder = null;
        String AIINameAutoInsuranceOfYourVehicle = null;
        String AIIYourInsuranceAddress = null;
        String AIIYourInsuranceAddress2 = null;
        String AIIYourInsuranceCity = null;
        String AIIYourInsuranceState = null;
        String AIIYourInsuranceZipCode = null;
        String AIIYourInsuranceAreaCode = null;
        String AIIYourInsurancePhoneNumber = null;
        String AIIYourInsurancePolicyNo = null;
        String AIIYourLicensePlate = null;
        String AIIYourCarMakeModelYear = null;
        String HealthInsuranceChk = "0";
        String GovtFundedInsurancePlanChk = "0";
        String GFIPMedicare = "0";
        String GFIPMedicaid = "0";
        String GFIPCHIP = "0";
        String GFIPTricare = "0";
        String GFIPVHA = "0";
        String GFIPIndianHealth = "0";
        String InsuranceSubPatient = "0";
        String InsuranceSubGuarantor = "0";
        String InsuranceSubOther = "0";
        String HIPrimaryInsurance = null;
        String HISubscriberFirstName = null;
        String HISubscriberLastName = null;
        String HISubscriberDOB = null;
        String HISubscriberSSN = null;
        String HISubscriberRelationtoPatient = null;
        String HISubscriberGroupNo = null;
        String HISubscriberPolicyNo = null;
        String SecondHealthInsuranceChk = null;
        String SHISecondaryName = null;
        String SHISubscriberFirstName = null;
        String SHISubscriberLastName = null;
        String SHISubscriberDOB = null;
        String SHISubscriberRelationtoPatient = null;
        String SHISubscriberGroupNo = null;
        String SHISubscriberPolicyNo = null;
        //marketing variable
        String MFFirstVisit = "";
        String MFReturnPat = "";
        String MFInternetFind = "";
        String Facebook = "";
        String MapSearch = "";
        String GoogleSearch = "";
        String VERWebsite = "";
        String WebsiteAds = "";
        String OnlineReviews = "";
        String Twitter = "";
        String LinkedIn = "";
        String EmailBlast = "";
        String YouTube = "";
        String TV = "";
        String Billboard = "";
        String Radio = "";
        String Brochure = "";
        String DirectMail = "";
        String CitizensDeTar = "";
        String LiveWorkNearby = "";
        String FamilyFriend = "";
        String FamilyFriend_text = "";
        String UrgentCare = "";
        String UrgentCare_text = "";
        String NewspaperMagazine = "";
        String NewspaperMagazine_text = "";
        String School = "";
        String School_text = "";
        String Hotel = "";
        String Hotel_text = "";
        String MFPhysician = "";
        String facilityName = "";
        String Year = "";
        String Month = "";
        String Day = "";
        int VisitId = 0;
        try {
            if (request.getParameter("Title") == null) {
                Title = "Mr.";
            } else {
                Title = request.getParameter("Title").trim();
            }
            if (request.getParameter("FirstName") == null) {
                FirstName = "";
            } else {
                FirstName = request.getParameter("FirstName").trim();
            }
            if (request.getParameter("LastName") == null) {
                LastName = "";
            } else {
                LastName = request.getParameter("LastName").trim();
            }
            if (request.getParameter("MiddleInitial") == null) {
                MiddleInitial = "";
            } else {
                MiddleInitial = request.getParameter("MiddleInitial").trim();
            }
/*            if (request.getParameter("DOB") == null) {
                DOB = "0000-00-00";
            } else {
                DOB = request.getParameter("DOB").trim();
                DOB = DOB.substring(6, 10) + "-" + DOB.substring(0, 2) + "-" + DOB.substring(3, 5);
            }*/
            Year = request.getParameter("Year").trim();
            Month = request.getParameter("Month").trim();
            Day = request.getParameter("Day").trim();
            DOB = Year + "-" + Month + "-" + Day;
            if (request.getParameter("Age") == null) {
                Age = "";
            } else {
                Age = request.getParameter("Age").trim();
            }
            if (request.getParameter("gender") == null) {
                gender = "";
            } else {
                gender = request.getParameter("gender").trim();
            }
            if (request.getParameter("Email") == null) {
                Email = "";
            } else {
                Email = request.getParameter("Email").trim();
            }
            if (request.getParameter("ConfirmEmail") == null) {
                ConfirmEmail = "";
            } else {
                ConfirmEmail = request.getParameter("ConfirmEmail").trim();
            }
            if (request.getParameter("MaritalStatus") == null) {
                MaritalStatus = "";
            } else {
                MaritalStatus = request.getParameter("MaritalStatus").trim();
            }
            if (request.getParameter("AreaCode") == null) {
                AreaCode = "";
            } else {
                AreaCode = request.getParameter("AreaCode").trim();
            }
            if (request.getParameter("PhNumber") == null) {
                PhNumber = "";
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
            if (request.getParameter("Ethnicity") == null) {
                Ethnicity = "Not Specified";
            } else {
                Ethnicity = request.getParameter("Ethnicity").trim();
            }
            if (request.getParameter("Race") == null) {
                Race = "Not Specified";
            } else {
                Race = request.getParameter("Race").trim();
            }
            if (request.getParameter("SSN") == null) {
                SSN = "";
            } else {
                SSN = request.getParameter("SSN").trim();
            }
            if (request.getParameter("ReasonVisit") == null) {
                ReasonVisit = "";
            } else {
                ReasonVisit = request.getParameter("ReasonVisit").trim();
            }


            if (request.getParameter("SympChkCOVID") == null) {
                SympChkCOVID = "0";
            } else {
                SympChkCOVID = request.getParameter("SympChkCOVID").trim();
            }
            if (request.getParameter("DateSympOnset") == null) {
                DateSympOnset = "0000-00-00";
            } else {
                DateSympOnset = request.getParameter("DateSympOnset").trim();
//                if (DateSympOnset.length() <= 10)
//                    DateSympOnset = DateSympOnset.substring(6, 10) + "-" + DateSympOnset.substring(0, 2) + "-" + DateSympOnset.substring(3, 5);
            }

            if (request.getParameter("SympFever") == null) {
                SympFever = "0";
            } else {
                SympFever = "1";
            }
            if (request.getParameter("SympCough") == null) {
                SympCough = "0";
            } else {
                SympCough = "1";
            }
            if (request.getParameter("SympShortBreath") == null) {
                SympShortBreath = "0";
            } else {
                SympShortBreath = "1";
            }
            if (request.getParameter("SympFatigue") == null) {
                SympFatigue = "0";
            } else {
                SympFatigue = "1";
            }
            if (request.getParameter("SympMuscBodyAches") == null) {
                SympMuscBodyAches = "0";
            } else {
                SympMuscBodyAches = "1";
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
            if (request.getParameter("SympSoreThroat") == null) {
                SympSoreThroat = "0";
            } else {
                SympSoreThroat = "1";
            }
            if (request.getParameter("SympCongestionRunNos") == null) {
                SympCongestionRunNos = "0";
            } else {
                SympCongestionRunNos = "1";
            }
            if (request.getParameter("SympNauseaVomit") == null) {
                SympNauseaVomit = "0";
            } else {
                SympNauseaVomit = "1";
            }
            if (request.getParameter("SympDiarrhea") == null) {
                SympDiarrhea = "0";
            } else {
                SympDiarrhea = "1";
            }
            if (request.getParameter("SympPerPainChest") == null) {
                SympPerPainChest = "0";
            } else {
                SympPerPainChest = "1";
            }
            if (request.getParameter("SympNewConfusion") == null) {
                SympNewConfusion = "0";
            } else {
                SympNewConfusion = "1";
            }
            if (request.getParameter("SympInabWake") == null) {
                SympInabWake = "0";
            } else {
                SympInabWake = "1";
            }
            if (request.getParameter("SympOthers") == null) {
                SympOthers = "0";
            } else {
                SympOthers = "1";
            }
            if (request.getParameter("SympOthersTxt") == null) {
                SympOthersTxt = "";
            } else {
                SympOthersTxt = request.getParameter("SympOthersTxt").trim();
            }
            if (request.getParameter("EmpHealthChk") == null) {
                EmpHealthChk = "0";
            } else {
                EmpHealthChk = request.getParameter("EmpHealthChk").trim();
            }
            if (request.getParameter("PregChk") == null) {
                PregChk = "0";
            } else {
                PregChk = request.getParameter("PregChk").trim();
            }

            if (request.getParameter("TestForTravelChk") == null) {
                TestForTravel = "0";
            } else {
                TestForTravel = request.getParameter("TestForTravelChk").trim();
            }


            if (request.getParameter("EmployementChk") == null) {
                EmployementChk = "0";
            } else {
                EmployementChk = request.getParameter("EmployementChk").trim();
            }
            if (EmployementChk.equals("1")) {
                if (request.getParameter("Employer") == null) {
                    Employer = "";
                } else {
                    Employer = request.getParameter("Employer").trim();
                }
                if (request.getParameter("Occupation") == null) {
                    Occupation = "";
                } else {
                    Occupation = request.getParameter("Occupation").trim();
                }
                if (request.getParameter("EmpContact") == null) {
                    EmpContact = "";
                } else {
                    EmpContact = request.getParameter("EmpContact").trim();
                }
            }
            if (request.getParameter("PrimaryCarePhysicianChk") == null) {
                PrimaryCarePhysicianChk = "0";
            } else {
                PrimaryCarePhysicianChk = request.getParameter("PrimaryCarePhysicianChk").trim();
            }
            if (PrimaryCarePhysicianChk.equals("1")) {
                if (request.getParameter("PriCarePhy") == null) {
                    PriCarePhy = "";
                } else {
                    PriCarePhy = request.getParameter("PriCarePhy").trim();
                }
                if (request.getParameter("PriCarePhyAddress") == null) {
                    PriCarePhyAddress = "";
                } else {
                    PriCarePhyAddress = request.getParameter("PriCarePhyAddress").trim();
                }
                if (request.getParameter("PriCarePhyAddress2") == null) {
                    PriCarePhyAddress2 = "";
                } else {
                    PriCarePhyAddress2 = request.getParameter("PriCarePhyAddress2").trim();
                }
                if (request.getParameter("PriCarePhyCity") == null) {
                    PriCarePhyCity = "";
                } else {
                    PriCarePhyCity = request.getParameter("PriCarePhyCity").trim();
                }
                if (request.getParameter("PriCarePhyState") == null) {
                    PriCarePhyState = "";
                } else {
                    PriCarePhyState = request.getParameter("PriCarePhyState").trim();
                }
                if (request.getParameter("PriCarePhyZipCode") == null) {
                    PriCarePhyZipCode = "";
                } else {
                    PriCarePhyZipCode = request.getParameter("PriCarePhyZipCode").trim();
                }
            }
            if (request.getParameter("PatientMinorChk") == null) {
                PatientMinorChk = "0";
            } else {
                PatientMinorChk = request.getParameter("PatientMinorChk").trim();
            }
            if (request.getParameter("GuarantorChk") == null) {
                GuarantorChk = "0";
            } else {
                GuarantorChk = request.getParameter("GuarantorChk").trim();
            }
            if (request.getParameter("GuarantorEmployer") == null) {
                GuarantorEmployer = "";
            } else {
                GuarantorEmployer = request.getParameter("GuarantorEmployer").trim();
            }
            if (request.getParameter("GuarantorEmployerAreaCode") == null) {
                GuarantorEmployerAreaCode = "";
            } else {
                GuarantorEmployerAreaCode = request.getParameter("GuarantorEmployerAreaCode").trim();
            }
            if (request.getParameter("GuarantorEmployerPhNumber") == null) {
                GuarantorEmployerPhNumber = "";
            } else {
                GuarantorEmployerPhNumber = request.getParameter("GuarantorEmployerPhNumber").trim();
            }
            if (request.getParameter("GuarantorEmployerAddress") == null) {
                GuarantorEmployerAddress = "";
            } else {
                GuarantorEmployerAddress = request.getParameter("GuarantorEmployerAddress").trim();
            }
            if (request.getParameter("GuarantorEmployerAddress2") == null) {
                GuarantorEmployerAddress2 = "";
            } else {
                GuarantorEmployerAddress2 = request.getParameter("GuarantorEmployerAddress2").trim();
            }
            if (request.getParameter("GuarantorEmployerCity") == null) {
                GuarantorEmployerCity = "";
            } else {
                GuarantorEmployerCity = request.getParameter("GuarantorEmployerCity").trim();
            }
            if (request.getParameter("GuarantorEmployerState") == null) {
                GuarantorEmployerState = "";
            } else {
                GuarantorEmployerState = request.getParameter("GuarantorEmployerState").trim();
            }
            if (request.getParameter("GuarantorEmployerZipCode") == null) {
                GuarantorEmployerZipCode = "";
            } else {
                GuarantorEmployerZipCode = request.getParameter("GuarantorEmployerZipCode").trim();
            }
            if (request.getParameter("WorkersCompPolicyChk") == null) {
                WorkersCompPolicyChk = "0";
            } else {
                WorkersCompPolicyChk = request.getParameter("WorkersCompPolicyChk").trim();
            }
            if (WorkersCompPolicyChk.equals("1")) {
                if (request.getParameter("WCPDateofInjury") == null) {
                    WCPDateofInjury = "0000-00-00";
                } else {
                    WCPDateofInjury = request.getParameter("WCPDateofInjury").trim();
                    WCPDateofInjury = String.valueOf(String.valueOf(WCPDateofInjury.substring(6, 10))) + "-" + WCPDateofInjury.substring(0, 2) + "-" + WCPDateofInjury.substring(3, 5);
                }
                if (request.getParameter("WCPCaseNo") == null) {
                    WCPCaseNo = "";
                } else {
                    WCPCaseNo = request.getParameter("WCPCaseNo").trim();
                }
                if (request.getParameter("WCPGroupNo") == null) {
                    WCPGroupNo = "";
                } else {
                    WCPGroupNo = request.getParameter("WCPGroupNo").trim();
                }
                if (request.getParameter("WCPMemberId") == null) {
                    WCPMemberId = "";
                } else {
                    WCPMemberId = request.getParameter("WCPMemberId").trim();
                }
                if (request.getParameter("WCPInjuryRelatedAutoMotorAccident") == null) {
                    WCPInjuryRelatedAutoMotorAccident = "0";
                } else {
                    WCPInjuryRelatedAutoMotorAccident = "1";
                }
                if (request.getParameter("WCPInjuryRelatedWorkRelated") == null) {
                    WCPInjuryRelatedWorkRelated = "0";
                } else {
                    WCPInjuryRelatedWorkRelated = "1";
                }
                if (request.getParameter("WCPInjuryRelatedOtherAccident") == null) {
                    WCPInjuryRelatedOtherAccident = "0";
                } else {
                    WCPInjuryRelatedOtherAccident = "1";
                }
                if (request.getParameter("WCPInjuryRelatedNoAccident") == null) {
                    WCPInjuryRelatedNoAccident = "0";
                } else {
                    WCPInjuryRelatedNoAccident = "1";
                }
                if (request.getParameter("WCPInjuryOccurVehicle") == null) {
                    WCPInjuryOccurVehicle = "0";
                } else {
                    WCPInjuryOccurVehicle = "1";
                }
                if (request.getParameter("WCPInjuryOccurWork") == null) {
                    WCPInjuryOccurWork = "0";
                } else {
                    WCPInjuryOccurWork = "1";
                }
                if (request.getParameter("WCPInjuryOccurHome") == null) {
                    WCPInjuryOccurHome = "0";
                } else {
                    WCPInjuryOccurHome = "1";
                }
                if (request.getParameter("WCPInjuryOccurOther") == null) {
                    WCPInjuryOccurOther = "0";
                } else {
                    WCPInjuryOccurOther = "1";
                }
                if (request.getParameter("WCPInjuryDescription") == null) {
                    WCPInjuryDescription = "";
                } else {
                    WCPInjuryDescription = request.getParameter("WCPInjuryDescription").trim();
                }
                if (request.getParameter("WCPHRFirstName") == null) {
                    WCPHRFirstName = "";
                } else {
                    WCPHRFirstName = request.getParameter("WCPHRFirstName").trim();
                }
                if (request.getParameter("WCPHRLastName") == null) {
                    WCPHRLastName = "";
                } else {
                    WCPHRLastName = request.getParameter("WCPHRLastName").trim();
                }
                if (request.getParameter("WCPHRAreaCode") == null) {
                    WCPHRAreaCode = "";
                } else {
                    WCPHRAreaCode = request.getParameter("WCPHRAreaCode").trim();
                }
                if (request.getParameter("WCPHRPhoneNumber") == null) {
                    WCPHRPhoneNumber = "";
                } else {
                    WCPHRPhoneNumber = request.getParameter("WCPHRPhoneNumber").trim();
                }
                if (request.getParameter("WCPHRAddress") == null) {
                    WCPHRAddress = "";
                } else {
                    WCPHRAddress = request.getParameter("WCPHRAddress").trim();
                }
                if (request.getParameter("WCPHRAddress2") == null) {
                    WCPHRAddress2 = "";
                } else {
                    WCPHRAddress2 = request.getParameter("WCPHRAddress2").trim();
                }
                if (request.getParameter("WCPHRCity") == null) {
                    WCPHRCity = "";
                } else {
                    WCPHRCity = request.getParameter("WCPHRCity").trim();
                }
                if (request.getParameter("WCPHRState") == null) {
                    WCPHRState = "";
                } else {
                    WCPHRState = request.getParameter("WCPHRState").trim();
                }
                if (request.getParameter("WCPHRZipCode") == null) {
                    WCPHRZipCode = "";
                } else {
                    WCPHRZipCode = request.getParameter("WCPHRZipCode").trim();
                }
                if (request.getParameter("WCPPlanName") == null) {
                    WCPPlanName = "";
                } else {
                    WCPPlanName = request.getParameter("WCPPlanName").trim();
                }
                if (request.getParameter("WCPCarrierName") == null) {
                    WCPCarrierName = "";
                } else {
                    WCPCarrierName = request.getParameter("WCPCarrierName").trim();
                }
                if (request.getParameter("WCPPayerAreaCode") == null) {
                    WCPPayerAreaCode = "";
                } else {
                    WCPPayerAreaCode = request.getParameter("WCPPayerAreaCode").trim();
                }
                if (request.getParameter("WCPPayerPhoneNumber") == null) {
                    WCPPayerPhoneNumber = "";
                } else {
                    WCPPayerPhoneNumber = request.getParameter("WCPPayerPhoneNumber").trim();
                }
                if (request.getParameter("WCPCarrierAddress") == null) {
                    WCPCarrierAddress = "";
                } else {
                    WCPCarrierAddress = request.getParameter("WCPCarrierAddress").trim();
                }
                if (request.getParameter("WCPCarrierAddress2") == null) {
                    WCPCarrierAddress2 = "";
                } else {
                    WCPCarrierAddress2 = request.getParameter("WCPCarrierAddress2").trim();
                }
                if (request.getParameter("WCPCarrierCity") == null) {
                    WCPCarrierCity = "";
                } else {
                    WCPCarrierCity = request.getParameter("WCPCarrierCity").trim();
                }
                if (request.getParameter("WCPCarrierState") == null) {
                    WCPCarrierState = "";
                } else {
                    WCPCarrierState = request.getParameter("WCPCarrierState").trim();
                }
                if (request.getParameter("WCPCarrierZipCode") == null) {
                    WCPCarrierZipCode = "";
                } else {
                    WCPCarrierZipCode = request.getParameter("WCPCarrierZipCode").trim();
                }
                if (request.getParameter("WCPAdjudicatorFirstName") == null) {
                    WCPAdjudicatorFirstName = "";
                } else {
                    WCPAdjudicatorFirstName = request.getParameter("WCPAdjudicatorFirstName").trim();
                }
                if (request.getParameter("WCPAdjudicatorLastName") == null) {
                    WCPAdjudicatorLastName = "";
                } else {
                    WCPAdjudicatorLastName = request.getParameter("WCPAdjudicatorLastName").trim();
                }
                if (request.getParameter("WCPAdjudicatorAreaCode") == null) {
                    WCPAdjudicatorAreaCode = "";
                } else {
                    WCPAdjudicatorAreaCode = request.getParameter("WCPAdjudicatorAreaCode").trim();
                }
                if (request.getParameter("WCPAdjudicatorPhoneNumber") == null) {
                    WCPAdjudicatorPhoneNumber = "";
                } else {
                    WCPAdjudicatorPhoneNumber = request.getParameter("WCPAdjudicatorPhoneNumber").trim();
                }
                if (request.getParameter("WCPAdjudicatorFaxAreaCode") == null) {
                    WCPAdjudicatorFaxAreaCode = "";
                } else {
                    WCPAdjudicatorFaxAreaCode = request.getParameter("WCPAdjudicatorFaxAreaCode").trim();
                }
                if (request.getParameter("WCPAdjudicatorFaxPhoneNumber") == null) {
                    WCPAdjudicatorFaxPhoneNumber = "";
                } else {
                    WCPAdjudicatorFaxPhoneNumber = request.getParameter("WCPAdjudicatorFaxPhoneNumber").trim();
                }
            }
            if (request.getParameter("MotorVehicleAccidentChk") == null) {
                MotorVehicleAccidentChk = "0";
            } else {
                MotorVehicleAccidentChk = request.getParameter("MotorVehicleAccidentChk").trim();
            }
            if (MotorVehicleAccidentChk.equals("1")) {
                if (request.getParameter("AutoInsuranceInformationChk") == null) {
                    AutoInsuranceInformationChk = "0";
                } else {
                    AutoInsuranceInformationChk = request.getParameter("AutoInsuranceInformationChk").trim();
                }
                if (AutoInsuranceInformationChk.equals("1")) {
                    if (request.getParameter("AIIDateofAccident") == null) {
                        AIIDateofAccident = "0000-00-00";
                    } else {
                        AIIDateofAccident = request.getParameter("AIIDateofAccident").trim();
                        AIIDateofAccident = String.valueOf(String.valueOf(AIIDateofAccident.substring(6, 10))) + "-" + AIIDateofAccident.substring(0, 2) + "-" + AIIDateofAccident.substring(3, 5);
                    }
                    if (request.getParameter("AIIAutoClaim") == null) {
                        AIIAutoClaim = "";
                    } else {
                        AIIAutoClaim = request.getParameter("AIIAutoClaim").trim();
                    }
                    if (request.getParameter("AIIAccidentLocationAddress") == null) {
                        AIIAccidentLocationAddress = "";
                    } else {
                        AIIAccidentLocationAddress = request.getParameter("AIIAccidentLocationAddress").trim();
                    }
                    if (request.getParameter("AIIAccidentLocationAddress2") == null) {
                        AIIAccidentLocationAddress2 = "";
                    } else {
                        AIIAccidentLocationAddress2 = request.getParameter("AIIAccidentLocationAddress2").trim();
                    }
                    if (request.getParameter("AIIAccidentLocationCity") == null) {
                        AIIAccidentLocationCity = "";
                    } else {
                        AIIAccidentLocationCity = request.getParameter("AIIAccidentLocationCity").trim();
                    }
                    if (request.getParameter("AIIAccidentLocationState") == null) {
                        AIIAccidentLocationState = "";
                    } else {
                        AIIAccidentLocationState = request.getParameter("AIIAccidentLocationState").trim();
                    }
                    if (request.getParameter("AIIAccidentLocationZipCode") == null) {
                        AIIAccidentLocationZipCode = "";
                    } else {
                        AIIAccidentLocationZipCode = request.getParameter("AIIAccidentLocationZipCode").trim();
                    }
                    if (request.getParameter("AIIRoleInAccident") == null) {
                        AIIRoleInAccident = "";
                    } else {
                        AIIRoleInAccident = request.getParameter("AIIRoleInAccident").trim();
                    }
                    if (request.getParameter("AIITypeOfAutoIOnsurancePolicy") == null) {
                        AIITypeOfAutoIOnsurancePolicy = "";
                    } else {
                        AIITypeOfAutoIOnsurancePolicy = request.getParameter("AIITypeOfAutoIOnsurancePolicy").trim();
                    }
                    if (request.getParameter("AIIPrefixforReponsibleParty") == null) {
                        AIIPrefixforReponsibleParty = "";
                    } else {
                        AIIPrefixforReponsibleParty = request.getParameter("AIIPrefixforReponsibleParty").trim();
                    }
                    if (request.getParameter("AIIFirstNameforReponsibleParty") == null) {
                        AIIFirstNameforReponsibleParty = "";
                    } else {
                        AIIFirstNameforReponsibleParty = request.getParameter("AIIFirstNameforReponsibleParty").trim();
                    }
                    if (request.getParameter("AIIMiddleNameforReponsibleParty") == null) {
                        AIIMiddleNameforReponsibleParty = "";
                    } else {
                        AIIMiddleNameforReponsibleParty = request.getParameter("AIIMiddleNameforReponsibleParty").trim();
                    }
                    if (request.getParameter("AIILastNameforReponsibleParty") == null) {
                        AIILastNameforReponsibleParty = "";
                    } else {
                        AIILastNameforReponsibleParty = request.getParameter("AIILastNameforReponsibleParty").trim();
                    }
                    if (request.getParameter("AIISuffixforReponsibleParty") == null) {
                        AIISuffixforReponsibleParty = "";
                    } else {
                        AIISuffixforReponsibleParty = request.getParameter("AIISuffixforReponsibleParty").trim();
                    }
                    if (request.getParameter("AIICarrierResponsibleParty") == null) {
                        AIICarrierResponsibleParty = "";
                    } else {
                        AIICarrierResponsibleParty = request.getParameter("AIICarrierResponsibleParty").trim();
                    }
                    if (request.getParameter("AIICarrierResponsiblePartyAddress") == null) {
                        AIICarrierResponsiblePartyAddress = "";
                    } else {
                        AIICarrierResponsiblePartyAddress = request.getParameter("AIICarrierResponsiblePartyAddress").trim();
                    }
                    if (request.getParameter("AIICarrierResponsiblePartyAddress2") == null) {
                        AIICarrierResponsiblePartyAddress2 = "";
                    } else {
                        AIICarrierResponsiblePartyAddress2 = request.getParameter("AIICarrierResponsiblePartyAddress2").trim();
                    }
                    if (request.getParameter("AIICarrierResponsiblePartyCity") == null) {
                        AIICarrierResponsiblePartyCity = "";
                    } else {
                        AIICarrierResponsiblePartyCity = request.getParameter("AIICarrierResponsiblePartyCity").trim();
                    }
                    if (request.getParameter("AIICarrierResponsiblePartyState") == null) {
                        AIICarrierResponsiblePartyState = "";
                    } else {
                        AIICarrierResponsiblePartyState = request.getParameter("AIICarrierResponsiblePartyState").trim();
                    }
                    if (request.getParameter("AIICarrierResponsiblePartyZipCode") == null) {
                        AIICarrierResponsiblePartyZipCode = "";
                    } else {
                        AIICarrierResponsiblePartyZipCode = request.getParameter("AIICarrierResponsiblePartyZipCode").trim();
                    }
                    if (request.getParameter("AIICarrierResponsiblePartyAreaCode") == null) {
                        AIICarrierResponsiblePartyAreaCode = "";
                    } else {
                        AIICarrierResponsiblePartyAreaCode = request.getParameter("AIICarrierResponsiblePartyAreaCode").trim();
                    }
                    if (request.getParameter("AIICarrierResponsiblePartyPhoneNumber") == null) {
                        AIICarrierResponsiblePartyPhoneNumber = "";
                    } else {
                        AIICarrierResponsiblePartyPhoneNumber = request.getParameter("AIICarrierResponsiblePartyPhoneNumber").trim();
                    }
                    if (request.getParameter("AIICarrierResponsiblePartyPolicyNumber") == null) {
                        AIICarrierResponsiblePartyPolicyNumber = "";
                    } else {
                        AIICarrierResponsiblePartyPolicyNumber = request.getParameter("AIICarrierResponsiblePartyPolicyNumber").trim();
                    }
                    if (request.getParameter("AIIResponsiblePartyAutoMakeModel") == null) {
                        AIIResponsiblePartyAutoMakeModel = "";
                    } else {
                        AIIResponsiblePartyAutoMakeModel = request.getParameter("AIIResponsiblePartyAutoMakeModel").trim();
                    }
                    if (request.getParameter("AIIResponsiblePartyLicensePlate") == null) {
                        AIIResponsiblePartyLicensePlate = "";
                    } else {
                        AIIResponsiblePartyLicensePlate = request.getParameter("AIIResponsiblePartyLicensePlate").trim();
                    }
                    if (request.getParameter("AIIFirstNameOfYourPolicyHolder") == null) {
                        AIIFirstNameOfYourPolicyHolder = "";
                    } else {
                        AIIFirstNameOfYourPolicyHolder = request.getParameter("AIIFirstNameOfYourPolicyHolder").trim();
                    }
                    if (request.getParameter("AIILastNameOfYourPolicyHolder") == null) {
                        AIILastNameOfYourPolicyHolder = "";
                    } else {
                        AIILastNameOfYourPolicyHolder = request.getParameter("AIILastNameOfYourPolicyHolder").trim();
                    }
                    if (request.getParameter("AIINameAutoInsuranceOfYourVehicle") == null) {
                        AIINameAutoInsuranceOfYourVehicle = "";
                    } else {
                        AIINameAutoInsuranceOfYourVehicle = request.getParameter("AIINameAutoInsuranceOfYourVehicle").trim();
                    }
                    if (request.getParameter("AIIYourInsuranceAddress") == null) {
                        AIIYourInsuranceAddress = "";
                    } else {
                        AIIYourInsuranceAddress = request.getParameter("AIIYourInsuranceAddress").trim();
                    }
                    if (request.getParameter("AIIYourInsuranceAddress2") == null) {
                        AIIYourInsuranceAddress2 = "";
                    } else {
                        AIIYourInsuranceAddress2 = request.getParameter("AIIYourInsuranceAddress2").trim();
                    }
                    if (request.getParameter("AIIYourInsuranceCity") == null) {
                        AIIYourInsuranceCity = "";
                    } else {
                        AIIYourInsuranceCity = request.getParameter("AIIYourInsuranceCity").trim();
                    }
                    if (request.getParameter("AIIYourInsuranceState") == null) {
                        AIIYourInsuranceState = "";
                    } else {
                        AIIYourInsuranceState = request.getParameter("AIIYourInsuranceState").trim();
                    }
                    if (request.getParameter("AIIYourInsuranceZipCode") == null) {
                        AIIYourInsuranceZipCode = "";
                    } else {
                        AIIYourInsuranceZipCode = request.getParameter("AIIYourInsuranceZipCode").trim();
                    }
                    if (request.getParameter("AIIYourInsuranceAreaCode") == null) {
                        AIIYourInsuranceAreaCode = "";
                    } else {
                        AIIYourInsuranceAreaCode = request.getParameter("AIIYourInsuranceAreaCode").trim();
                    }
                    if (request.getParameter("AIIYourInsurancePhoneNumber") == null) {
                        AIIYourInsurancePhoneNumber = "";
                    } else {
                        AIIYourInsurancePhoneNumber = request.getParameter("AIIYourInsurancePhoneNumber").trim();
                    }
                    if (request.getParameter("AIIYourInsurancePolicyNo") == null) {
                        AIIYourInsurancePolicyNo = "";
                    } else {
                        AIIYourInsurancePolicyNo = request.getParameter("AIIYourInsurancePolicyNo").trim();
                    }
                    if (request.getParameter("AIIYourLicensePlate") == null) {
                        AIIYourLicensePlate = "";
                    } else {
                        AIIYourLicensePlate = request.getParameter("AIIYourLicensePlate").trim();
                    }
                    if (request.getParameter("AIIYourCarMakeModelYear") == null) {
                        AIIYourCarMakeModelYear = "";
                    } else {
                        AIIYourCarMakeModelYear = request.getParameter("AIIYourCarMakeModelYear").trim();
                    }
                } else {
                    if (request.getParameter("AIIResponsiblePartyLicensePlate") == null) {
                        AIIResponsiblePartyLicensePlate = "";
                    } else {
                        AIIResponsiblePartyLicensePlate = request.getParameter("AIIResponsiblePartyLicensePlate").trim();
                    }
                    if (request.getParameter("AIIFirstNameOfYourPolicyHolder") == null) {
                        AIIFirstNameOfYourPolicyHolder = "";
                    } else {
                        AIIFirstNameOfYourPolicyHolder = request.getParameter("AIIFirstNameOfYourPolicyHolder").trim();
                    }
                    if (request.getParameter("AIILastNameOfYourPolicyHolder") == null) {
                        AIILastNameOfYourPolicyHolder = "";
                    } else {
                        AIILastNameOfYourPolicyHolder = request.getParameter("AIILastNameOfYourPolicyHolder").trim();
                    }
                    if (request.getParameter("AIINameAutoInsuranceOfYourVehicle") == null) {
                        AIINameAutoInsuranceOfYourVehicle = "";
                    } else {
                        AIINameAutoInsuranceOfYourVehicle = request.getParameter("AIINameAutoInsuranceOfYourVehicle").trim();
                    }
                    if (request.getParameter("AIIYourInsuranceAddress") == null) {
                        AIIYourInsuranceAddress = "";
                    } else {
                        AIIYourInsuranceAddress = request.getParameter("AIIYourInsuranceAddress").trim();
                    }
                    if (request.getParameter("AIIYourInsuranceAddress2") == null) {
                        AIIYourInsuranceAddress2 = "";
                    } else {
                        AIIYourInsuranceAddress2 = request.getParameter("AIIYourInsuranceAddress2").trim();
                    }
                    if (request.getParameter("AIIYourInsuranceCity") == null) {
                        AIIYourInsuranceCity = "";
                    } else {
                        AIIYourInsuranceCity = request.getParameter("AIIYourInsuranceCity").trim();
                    }
                    if (request.getParameter("AIIYourInsuranceState") == null) {
                        AIIYourInsuranceState = "";
                    } else {
                        AIIYourInsuranceState = request.getParameter("AIIYourInsuranceState").trim();
                    }
                    if (request.getParameter("AIIYourInsuranceZipCode") == null) {
                        AIIYourInsuranceZipCode = "";
                    } else {
                        AIIYourInsuranceZipCode = request.getParameter("AIIYourInsuranceZipCode").trim();
                    }
                    if (request.getParameter("AIIYourInsuranceAreaCode") == null) {
                        AIIYourInsuranceAreaCode = "";
                    } else {
                        AIIYourInsuranceAreaCode = request.getParameter("AIIYourInsuranceAreaCode").trim();
                    }
                    if (request.getParameter("AIIYourInsurancePhoneNumber") == null) {
                        AIIYourInsurancePhoneNumber = "";
                    } else {
                        AIIYourInsurancePhoneNumber = request.getParameter("AIIYourInsurancePhoneNumber").trim();
                    }
                    if (request.getParameter("AIIYourInsurancePolicyNo") == null) {
                        AIIYourInsurancePolicyNo = "";
                    } else {
                        AIIYourInsurancePolicyNo = request.getParameter("AIIYourInsurancePolicyNo").trim();
                    }
                    if (request.getParameter("AIIYourLicensePlate") == null) {
                        AIIYourLicensePlate = "";
                    } else {
                        AIIYourLicensePlate = request.getParameter("AIIYourLicensePlate").trim();
                    }
                    if (request.getParameter("AIIYourCarMakeModelYear") == null) {
                        AIIYourCarMakeModelYear = "";
                    } else {
                        AIIYourCarMakeModelYear = request.getParameter("AIIYourCarMakeModelYear").trim();
                    }
                }
            }
            if (request.getParameter("HealthInsuranceChk") == null) {
                HealthInsuranceChk = "0";
            } else {
                HealthInsuranceChk = request.getParameter("HealthInsuranceChk").trim();
            }
            if (HealthInsuranceChk.equals("1")) {
                if (request.getParameter("GovtFundedInsurancePlanChk") == null) {
                    GovtFundedInsurancePlanChk = "0";
                } else {
                    GovtFundedInsurancePlanChk = request.getParameter("GovtFundedInsurancePlanChk").trim();
                }
                if (GovtFundedInsurancePlanChk.equals("1")) {
                    if (request.getParameter("GFIPMedicare") == null) {
                        GFIPMedicare = "0";
                    } else {
                        GFIPMedicare = "1";
                    }
                    if (request.getParameter("GFIPMedicaid") == null) {
                        GFIPMedicaid = "0";
                    } else {
                        GFIPMedicaid = "1";
                    }
                    if (request.getParameter("GFIPCHIP") == null) {
                        GFIPCHIP = "0";
                    } else {
                        GFIPCHIP = "1";
                    }
                    if (request.getParameter("GFIPTricare") == null) {
                        GFIPTricare = "0";
                    } else {
                        GFIPTricare = "1";
                    }
                    if (request.getParameter("GFIPVHA") == null) {
                        GFIPVHA = "0";
                    } else {
                        GFIPVHA = "1";
                    }
                    if (request.getParameter("GFIPIndianHealth") == null) {
                        GFIPIndianHealth = "0";
                    } else {
                        GFIPIndianHealth = "1";
                    }
                }
                if (request.getParameter("InsuranceSubPatient") == null) {
                    InsuranceSubPatient = "0";
                } else {
                    InsuranceSubPatient = "1";
                }
                if (request.getParameter("InsuranceSubGuarantor") == null) {
                    InsuranceSubGuarantor = "0";
                } else {
                    InsuranceSubGuarantor = "1";
                }
                if (request.getParameter("InsuranceSubOther") == null) {
                    InsuranceSubOther = "0";
                } else {
                    InsuranceSubOther = "1";
                }
                if (request.getParameter("HIPrimaryInsurance") == null) {
                    HIPrimaryInsurance = "";
                } else {
                    HIPrimaryInsurance = request.getParameter("HIPrimaryInsurance").trim();
                }
                if (request.getParameter("HISubscriberFirstName") == null) {
                    HISubscriberFirstName = "";
                } else {
                    HISubscriberFirstName = request.getParameter("HISubscriberFirstName").trim();
                }
                if (request.getParameter("HISubscriberLastName") == null) {
                    HISubscriberLastName = "";
                } else {
                    HISubscriberLastName = request.getParameter("HISubscriberLastName").trim();
                }
                if (request.getParameter("HISubscriberDOB") == null) {
                    HISubscriberDOB = "0000-00-00";
                } else {
                    HISubscriberDOB = request.getParameter("HISubscriberDOB").trim();
                    HISubscriberDOB = String.valueOf(String.valueOf(HISubscriberDOB.substring(6, 10))) + "-" + HISubscriberDOB.substring(0, 2) + "-" + HISubscriberDOB.substring(3, 5);
                }
                if (request.getParameter("HISubscriberSSN") == null) {
                    HISubscriberSSN = "";
                } else {
                    HISubscriberSSN = request.getParameter("HISubscriberSSN").trim();
                }
                if (request.getParameter("HISubscriberRelationtoPatient") == null) {
                    HISubscriberRelationtoPatient = "";
                } else {
                    HISubscriberRelationtoPatient = request.getParameter("HISubscriberRelationtoPatient").trim();
                }
                if (request.getParameter("HISubscriberGroupNo") == null) {
                    HISubscriberGroupNo = "";
                } else {
                    HISubscriberGroupNo = request.getParameter("HISubscriberGroupNo").trim();
                }
                if (request.getParameter("HISubscriberPolicyNo") == null) {
                    HISubscriberPolicyNo = "";
                } else {
                    HISubscriberPolicyNo = request.getParameter("HISubscriberPolicyNo").trim();
                }
                if (request.getParameter("SecondHealthInsuranceChk") == null) {
                    SecondHealthInsuranceChk = "0";
                } else {
                    SecondHealthInsuranceChk = request.getParameter("SecondHealthInsuranceChk").trim();
                }
                if (SecondHealthInsuranceChk.equals("1")) {
                    if (request.getParameter("SHISecondaryName") == null) {
                        SHISecondaryName = "";
                    } else {
                        SHISecondaryName = request.getParameter("SHISecondaryName").trim();
                    }
                    if (request.getParameter("SHISubscriberFirstName") == null) {
                        SHISubscriberFirstName = "";
                    } else {
                        SHISubscriberFirstName = request.getParameter("SHISubscriberFirstName").trim();
                    }
                    if (request.getParameter("SHISubscriberLastName") == null) {
                        SHISubscriberLastName = "";
                    } else {
                        SHISubscriberLastName = request.getParameter("SHISubscriberLastName").trim();
                    }
                    if (request.getParameter("SHISubscriberDOB") == null) {
                        SHISubscriberDOB = "0000-00-00";
                    } else {
                        SHISubscriberDOB = request.getParameter("SHISubscriberDOB").trim();
                        SHISubscriberDOB = String.valueOf(String.valueOf(SHISubscriberDOB.substring(6, 10))) + "-" + SHISubscriberDOB.substring(0, 2) + "-" + SHISubscriberDOB.substring(3, 5);
                    }
                    if (request.getParameter("SHISubscriberRelationtoPatient") == null) {
                        SHISubscriberRelationtoPatient = "";
                    } else {
                        SHISubscriberRelationtoPatient = request.getParameter("SHISubscriberRelationtoPatient").trim();
                    }
                    if (request.getParameter("SHISubscriberGroupNo") == null) {
                        SHISubscriberGroupNo = "";
                    } else {
                        SHISubscriberGroupNo = request.getParameter("SHISubscriberGroupNo").trim();
                    }
                    if (request.getParameter("SHISubscriberPolicyNo") == null) {
                        SHISubscriberPolicyNo = "";
                    } else {
                        SHISubscriberPolicyNo = request.getParameter("SHISubscriberPolicyNo").trim();
                    }
                }
            }

            if (request.getParameter("MFFirstVisit") == null) {
                MFFirstVisit = "0";
            } else {
                MFFirstVisit = request.getParameter("MFFirstVisit");
            }
            if (request.getParameter("MFReturnPat") == null) {
                MFReturnPat = "0";
            } else {
                MFReturnPat = request.getParameter("MFReturnPat");
            }
            if (request.getParameter("MFInternetFind") == null) {
                MFInternetFind = "0";
            } else {
                MFInternetFind = request.getParameter("MFInternetFind");
            }

            if (request.getParameter("Facebook") == null) {
                Facebook = "0";
            } else {
                Facebook = "1";
            }
            if (request.getParameter("MapSearch") == null) {
                MapSearch = "0";
            } else {
                MapSearch = "1";
            }
            if (request.getParameter("GoogleSearch") == null) {
                GoogleSearch = "0";
            } else {
                GoogleSearch = "1";
            }
            if (request.getParameter("VERWebsite") == null) {
                VERWebsite = "0";
            } else {
                VERWebsite = "1";
            }
            if (request.getParameter("WebsiteAds") == null) {
                WebsiteAds = "0";
            } else {
                WebsiteAds = "1";
            }
            if (request.getParameter("OnlineReviews") == null) {
                OnlineReviews = "0";
            } else {
                OnlineReviews = "1";
            }
            if (request.getParameter("Twitter") == null) {
                Twitter = "0";
            } else {
                Twitter = "1";
            }
            if (request.getParameter("LinkedIn") == null) {
                LinkedIn = "0";
            } else {
                LinkedIn = "1";
            }
            if (request.getParameter("EmailBlast") == null) {
                EmailBlast = "0";
            } else {
                EmailBlast = "1";
            }
            if (request.getParameter("YouTube") == null) {
                YouTube = "0";
            } else {
                YouTube = "1";
            }
            if (request.getParameter("TV") == null) {
                TV = "0";
            } else {
                TV = "1";
            }
            if (request.getParameter("Billboard") == null) {
                Billboard = "0";
            } else {
                Billboard = "1";
            }
            if (request.getParameter("Radio") == null) {
                Radio = "0";
            } else {
                Radio = "1";
            }
            if (request.getParameter("Brochure") == null) {
                Brochure = "0";
            } else {
                Brochure = "1";
            }
            if (request.getParameter("DirectMail") == null) {
                DirectMail = "0";
            } else {
                DirectMail = "1";
            }
            if (request.getParameter("CitizensDeTar") == null) {
                CitizensDeTar = "0";
            } else {
                CitizensDeTar = "1";
            }
            if (request.getParameter("LiveWorkNearby") == null) {
                LiveWorkNearby = "0";
            } else {
                LiveWorkNearby = "1";
            }
            if (request.getParameter("FamilyFriend") == null) {
                FamilyFriend = "0";
            } else {
                FamilyFriend = "1";
            }
            if (request.getParameter("UrgentCare") == null) {
                UrgentCare = "0";
            } else {
                UrgentCare = "1";
            }
            if (request.getParameter("NewspaperMagazine") == null) {
                NewspaperMagazine = "0";
            } else {
                NewspaperMagazine = "1";
            }
            if (request.getParameter("School") == null) {
                School = "0";
            } else {
                School = "1";
            }
            if (request.getParameter("Hotel") == null) {
                Hotel = "0";
            } else {
                Hotel = "1";
            }
            if (request.getParameter("FamilyFriend_text") == null) {
                FamilyFriend_text = "";
            } else {
                FamilyFriend_text = request.getParameter("FamilyFriend_text");
            }
            if (request.getParameter("UrgentCare_text") == null) {
                UrgentCare_text = "";
            } else {
                UrgentCare_text = request.getParameter("UrgentCare_text");
            }
            if (request.getParameter("NewspaperMagazine_text") == null) {
                NewspaperMagazine_text = "";
            } else {
                NewspaperMagazine_text = request.getParameter("NewspaperMagazine_text");
            }
            if (request.getParameter("School_text") == null) {
                School_text = "";
            } else {
                School_text = request.getParameter("School_text");
            }
            if (request.getParameter("Hotel_text") == null) {
                Hotel_text = "";
            } else {
                Hotel_text = request.getParameter("Hotel_text");
            }
            if (request.getParameter("MFPhysician") == null) {
                MFPhysician = "";
            } else {
                MFPhysician = request.getParameter("MFPhysician");
            }

            Query = "Select Date_format(now(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();
            try {
                Query = "Select Id, dbname, DirectoryName from oe.clients where ltrim(rtrim(UPPER(name))) = ltrim(rtrim(UPPER('" + ClientId + "')))";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClientIndex = rset.getInt(1);
                    Database = rset.getString(2);
                    DirectoryName = rset.getString(3);
                }
                rset.close();
                stmt.close();

                facilityName = helper.getFacilityName(request, conn, servletContext, ClientIndex);

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
                if (ClientIndex == 8) {
                    ExtendedMRN = "1008" + MRN;
                } else if (ClientIndex == 9) {
                    ExtendedMRN = "1009" + MRN;
                } else if (ClientIndex == 10) {
                    ExtendedMRN = "1010" + MRN;
                } else if (ClientIndex == 11) {
                    ExtendedMRN = "1011" + MRN;
                }
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg2_Mobile ** (SaveDataVictoria PatineReg2_Mobile Data get ^^" + facilityName + " ##MES#001)", servletContext, ex, "PatientReg2_Mobile", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2_Mobile", "MES#001", request, ex, this.getServletContext());
                return;
            }
            try {
                if (!Email.equals(ConfirmEmail)) {
                    out.println("<!DOCTYPE html><html><body><p style=\"color:black;\">Please Put Email and Confirm Email Correctly and then Submit</p>");
                    out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
                    return;
                }
                Email = ConfirmEmail;
                UtilityHelper utilityHelper = new UtilityHelper();
                String ClientIp = utilityHelper.getClientIp(request);
                final PreparedStatement MainReceipt = conn.prepareStatement(
                        " INSERT INTO " + Database + ".PatientReg (ClientIndex,FirstName,LastName ,MiddleInitial,DOB,Age,Gender ,Email," +
                                "PhNumber ,Address,City ,State,Country,ZipCode,SSN,Occupation ,Employer ,EmpContact, PriCarePhy,ReasonVisit," +
                                "SelfPayChk,CreatedDate,Title, MaritalStatus,CreatedBy, MRN, Status, ExtendedMRN, County,RegisterFrom,ViewDate," +
                                "EnterBy, EnterIP) \n" +
                                " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,0,?,?,?,NOW(),?,?) ");
                MainReceipt.setInt(1, ClientIndex);
                MainReceipt.setString(2, FirstName);
                MainReceipt.setString(3, LastName);
                MainReceipt.setString(4, MiddleInitial);
                MainReceipt.setString(5, DOB);
                MainReceipt.setString(6, Age);
                MainReceipt.setString(7, gender);
                MainReceipt.setString(8, Email);
                MainReceipt.setString(9, AreaCode + "" + PhNumber);
                MainReceipt.setString(10, Address + " " + Address2);
                MainReceipt.setString(11, City);
                MainReceipt.setString(12, State);
                MainReceipt.setString(13, Country);
                MainReceipt.setString(14, ZipCode);
                MainReceipt.setString(15, SSN);
                MainReceipt.setString(16, "");
                MainReceipt.setString(17, "");
                MainReceipt.setString(18, "");
                MainReceipt.setString(19, "");
                MainReceipt.setString(20, ReasonVisit);
                MainReceipt.setInt(21, 0);
                MainReceipt.setString(22, Title);
                MainReceipt.setString(23, MaritalStatus);
                MainReceipt.setString(24, "Out Patient");
                MainReceipt.setInt(25, MRN);
                MainReceipt.setString(26, ExtendedMRN);
                MainReceipt.setString(27, County);
                MainReceipt.setString(28, "Victoria Mobile App ");
                MainReceipt.setString(29, "Mobile User");
                MainReceipt.setString(30, ClientIp);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg2_Mobile ** (SaveDataVictoria Insertion PatientReg Table ^^" + facilityName + " ##MES#002)", servletContext, ex, "PatientReg2_Mobile", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2", "SaveDataVictoriaError 2- Insertion PatientReg Table :", request, ex, getServletContext());
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
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg2_Mobile ** (SaveDataVictoria ^^" + facilityName + " ##MES#003)", servletContext, ex, "PatientReg2_Mobile", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2", "SaveDataVictoriaError 3- :", request, ex, this.getServletContext());
                return;
            }
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".PatientVisit(MRN,PatientRegId,ReasonVisit,VisitNumber,DoctorId,DateofService,CreatedDate,CreatedBy) " +
                                " VALUES (?,?,?,1,NULL,now(),now(),?) ");
                MainReceipt.setInt(1, MRN);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, ReasonVisit);
                MainReceipt.setString(4, "Out Patient");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2_Mobile ** (SaveDataVictoria Insertion PatientVisit Table ^^" + facilityName + " ##MES#003)", servletContext, e, "PatientReg2_Mobile", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2", "SaveDataVictoriaError 3.1 Insertion in table PatientVisit- :", request, e, this.getServletContext());
                return;
            }
            try {
                Query = "Select MAX(Id) from " + Database + ".PatientVisit";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    VisitId = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".PatientReg_Details (PatientRegId,Ethnicity,Race," +
                        " EmployementChk,Employer,Occupation,EmpContact,PrimaryCarePhysicianChk,PriCarePhy,ReasonVisit,PriCarePhyAddress,PriCarePhyCity,PriCarePhyState," +
                        " PriCarePhyZipCode,PatientMinorChk,GuarantorChk,GuarantorEmployer,GuarantorEmployerPhNumber,GuarantorEmployerAddress,GuarantorEmployerCity," +
                        " GuarantorEmployerState,GuarantorEmployerZipCode,CreatedDate,WorkersCompPolicyChk,MotorVehicleAccidentChk,HealthInsuranceChk, SympChkCOVID, " +
                        " DateSympOnset, SympFever, SympCough, SympShortBreath, SympFatigue, SympMuscBodyAches, SympHeadache, SympLossTaste, SympSoreThroat, " +
                        " SympCongestionRunNos, SympNauseaVomit, SympDiarrhea, SympPerPainChest, SympNewConfusion, SympInabWake, SympOthers, SympOthersTxt," +
                        " EmpHealthChk, PregChk, TestForTravel) \n" +
                        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                MainReceipt.setInt(1, PatientRegId);
                MainReceipt.setString(2, Ethnicity);
                MainReceipt.setString(3, Race);
                MainReceipt.setInt(4, Integer.parseInt(EmployementChk));
                MainReceipt.setString(5, Employer);
                MainReceipt.setString(6, Occupation);
                MainReceipt.setString(7, EmpContact);
                MainReceipt.setInt(8, Integer.parseInt(PrimaryCarePhysicianChk));
                MainReceipt.setString(9, PriCarePhy);
                MainReceipt.setString(10, ReasonVisit);
                MainReceipt.setString(11, PriCarePhyAddress + " " + PriCarePhyAddress2);
                MainReceipt.setString(12, PriCarePhyCity);
                MainReceipt.setString(13, PriCarePhyState);
                MainReceipt.setString(14, PriCarePhyZipCode);
                MainReceipt.setInt(15, Integer.parseInt(PatientMinorChk));
                MainReceipt.setString(16, GuarantorChk);
                MainReceipt.setString(17, GuarantorEmployer);
                MainReceipt.setString(18, GuarantorEmployerAreaCode + GuarantorEmployerPhNumber);
                MainReceipt.setString(19, GuarantorEmployerAddress + " " + GuarantorEmployerAddress2);
                MainReceipt.setString(20, GuarantorEmployerCity);
                MainReceipt.setString(21, GuarantorEmployerState);
                MainReceipt.setString(22, GuarantorEmployerZipCode);
                MainReceipt.setInt(23, Integer.parseInt(WorkersCompPolicyChk));
                MainReceipt.setInt(24, Integer.parseInt(MotorVehicleAccidentChk));
                MainReceipt.setInt(25, Integer.parseInt(HealthInsuranceChk));
                MainReceipt.setInt(26, Integer.parseInt(SympChkCOVID));
                MainReceipt.setString(27, DateSympOnset);
                MainReceipt.setString(28, SympFever);
                MainReceipt.setString(29, SympCough);
                MainReceipt.setString(30, SympShortBreath);
                MainReceipt.setString(31, SympFatigue);
                MainReceipt.setString(32, SympMuscBodyAches);
                MainReceipt.setString(33, SympHeadache);
                MainReceipt.setString(34, SympLossTaste);
                MainReceipt.setString(35, SympLossTaste);
                MainReceipt.setString(36, SympCongestionRunNos);
                MainReceipt.setString(37, SympNauseaVomit);
                MainReceipt.setString(38, SympDiarrhea);
                MainReceipt.setString(39, SympPerPainChest);
                MainReceipt.setString(40, SympNewConfusion);
                MainReceipt.setString(41, SympInabWake);
                MainReceipt.setString(42, SympOthers);
                MainReceipt.setString(43, SympOthersTxt);
                MainReceipt.setString(44, EmpHealthChk);
                MainReceipt.setString(45, PregChk);
                MainReceipt.setString(46, TestForTravel);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2_Mobile ** (SaveDataVictoria Insertion PatientReg_Details Table ^^" + facilityName + " ##MES#004)", servletContext, e, "PatientReg2_Mobile", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2_Mobile", "SaveDataVictoriaError 4- Insertion PatientReg_Details Table :", request, e, this.getServletContext());
                return;
            }
            if (WorkersCompPolicyChk.equals("1")) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement(
                            " INSERT INTO " + Database + ".Patient_WorkCompPolicy (PatientRegId,WCPDateofInjury,WCPCaseNo,WCPGroupNo,WCPMemberId," +
                                    "WCPInjuryRelatedAutoMotorAccident,WCPInjuryRelatedWorkRelated,WCPInjuryRelatedOtherAccident," +
                                    "WCPInjuryRelatedNoAccident,WCPInjuryOccurVehicle,WCPInjuryOccurWork,WCPInjuryOccurHome,WCPInjuryOccurOther," +
                                    "WCPInjuryDescription,WCPHRFirstName,WCPHRLastName,WCPHRPhoneNumber,WCPHRAddress,WCPHRCity,WCPHRState,WCPHRZipCode," +
                                    "WCPPlanName,WCPCarrierName,WCPPayerPhoneNumber,WCPCarrierAddress,WCPCarrierCity,WCPCarrierState,WCPCarrierZipCode," +
                                    "WCPAdjudicatorFirstName,WCPAdjudicatorLastName,WCPAdjudicatorPhoneNumber,WCPAdjudicatorFaxPhoneNumber,CreatedDate) \n" +
                                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
                    MainReceipt.setInt(1, PatientRegId);
                    MainReceipt.setString(2, WCPDateofInjury);
                    MainReceipt.setString(3, WCPCaseNo);
                    MainReceipt.setString(4, WCPGroupNo);
                    MainReceipt.setString(5, WCPMemberId);
                    MainReceipt.setString(6, WCPInjuryRelatedAutoMotorAccident);
                    MainReceipt.setString(7, WCPInjuryRelatedWorkRelated);
                    MainReceipt.setString(8, WCPInjuryRelatedOtherAccident);
                    MainReceipt.setString(9, WCPInjuryRelatedNoAccident);
                    MainReceipt.setString(10, WCPInjuryOccurVehicle);
                    MainReceipt.setString(11, WCPInjuryOccurWork);
                    MainReceipt.setString(12, WCPInjuryOccurHome);
                    MainReceipt.setString(13, WCPInjuryOccurOther);
                    MainReceipt.setString(14, WCPInjuryDescription);
                    MainReceipt.setString(15, WCPHRFirstName);
                    MainReceipt.setString(16, WCPHRLastName);
                    MainReceipt.setString(17, WCPHRAreaCode + WCPHRPhoneNumber);
                    MainReceipt.setString(18, WCPHRAddress + " " + WCPHRAddress2);
                    MainReceipt.setString(19, WCPHRCity);
                    MainReceipt.setString(20, WCPHRState);
                    MainReceipt.setString(21, WCPHRZipCode);
                    MainReceipt.setString(22, WCPPlanName);
                    MainReceipt.setString(23, WCPCarrierName);
                    MainReceipt.setString(24, WCPPayerAreaCode + WCPPayerPhoneNumber);
                    MainReceipt.setString(25, WCPCarrierAddress + " " + WCPCarrierAddress2);
                    MainReceipt.setString(26, WCPCarrierCity);
                    MainReceipt.setString(27, WCPCarrierState);
                    MainReceipt.setString(28, WCPCarrierZipCode);
                    MainReceipt.setString(29, WCPAdjudicatorFirstName);
                    MainReceipt.setString(30, WCPAdjudicatorLastName);
                    MainReceipt.setString(31, WCPAdjudicatorAreaCode + "" + WCPAdjudicatorPhoneNumber);
                    MainReceipt.setString(32, WCPAdjudicatorFaxAreaCode + "" + WCPAdjudicatorFaxPhoneNumber);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception e) {
                    helper.SendEmailWithAttachment("Error in PatientReg2_Mobile ** (SaveDataVictoria Insertion Patient_WorkCompPolicy Table ^^" + facilityName + " ##MES#005)", servletContext, e, "PatientReg2_Mobile", "SaveDataVictoria", conn);
                    Services.DumException("PatientReg2", "SaveDataVictoriaError 5- Insertion Patient_WorkCompPolicy Table :", request, e, this.getServletContext());
                    return;
                }
            }
            if (MotorVehicleAccidentChk.equals("1")) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".Patient_AutoInsuranceInfo (PatientRegId," + "AutoInsuranceInformationChk,AIIDateofAccident,AIIAutoClaim,AIIAccidentLocationAddress,AIIAccidentLocationCity,AIIAccidentLocationState," + "AIIAccidentLocationZipCode,AIIRoleInAccident,AIITypeOfAutoIOnsurancePolicy,AIIPrefixforReponsibleParty,AIIFirstNameforReponsibleParty," + "AIIMiddleNameforReponsibleParty,AIILastNameforReponsibleParty,AIISuffixforReponsibleParty,AIICarrierResponsibleParty," + "AIICarrierResponsiblePartyAddress,AIICarrierResponsiblePartyCity,AIICarrierResponsiblePartyState,AIICarrierResponsiblePartyZipCode," + "AIICarrierResponsiblePartyPhoneNumber,AIICarrierResponsiblePartyPolicyNumber,AIIResponsiblePartyAutoMakeModel," + "AIIResponsiblePartyLicensePlate,AIIFirstNameOfYourPolicyHolder,AIILastNameOfYourPolicyHolder,AIINameAutoInsuranceOfYourVehicle," + "AIIYourInsuranceAddress,AIIYourInsuranceCity,AIIYourInsuranceState,AIIYourInsuranceZipCode,AIIYourInsurancePhoneNumber," + "AIIYourInsurancePolicyNo,AIIYourLicensePlate,AIIYourCarMakeModelYear,CreatedDate) \n" + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
                    MainReceipt.setInt(1, PatientRegId);
                    MainReceipt.setInt(2, Integer.parseInt(AutoInsuranceInformationChk));
                    MainReceipt.setString(3, AIIDateofAccident);
                    MainReceipt.setString(4, AIIAutoClaim);
                    MainReceipt.setString(5, String.valueOf(String.valueOf(AIIAccidentLocationAddress)) + " " + AIIAccidentLocationAddress2);
                    MainReceipt.setString(6, AIIAccidentLocationCity);
                    MainReceipt.setString(7, AIIAccidentLocationState);
                    MainReceipt.setString(8, AIIAccidentLocationZipCode);
                    MainReceipt.setString(9, AIIRoleInAccident);
                    MainReceipt.setString(10, AIITypeOfAutoIOnsurancePolicy);
                    MainReceipt.setString(11, AIIPrefixforReponsibleParty);
                    MainReceipt.setString(12, AIIFirstNameforReponsibleParty);
                    MainReceipt.setString(13, AIIMiddleNameforReponsibleParty);
                    MainReceipt.setString(14, AIILastNameforReponsibleParty);
                    MainReceipt.setString(15, AIISuffixforReponsibleParty);
                    MainReceipt.setString(16, AIICarrierResponsibleParty);
                    MainReceipt.setString(17, String.valueOf(String.valueOf(AIICarrierResponsiblePartyAddress)) + " " + AIICarrierResponsiblePartyAddress2);
                    MainReceipt.setString(18, AIICarrierResponsiblePartyCity);
                    MainReceipt.setString(19, AIICarrierResponsiblePartyState);
                    MainReceipt.setString(20, AIICarrierResponsiblePartyZipCode);
                    MainReceipt.setString(21, String.valueOf(String.valueOf(AIICarrierResponsiblePartyAreaCode)) + AIICarrierResponsiblePartyPhoneNumber);
                    MainReceipt.setString(22, AIICarrierResponsiblePartyPolicyNumber);
                    MainReceipt.setString(23, AIIResponsiblePartyAutoMakeModel);
                    MainReceipt.setString(24, AIIResponsiblePartyLicensePlate);
                    MainReceipt.setString(25, AIIFirstNameOfYourPolicyHolder);
                    MainReceipt.setString(26, AIILastNameOfYourPolicyHolder);
                    MainReceipt.setString(27, AIINameAutoInsuranceOfYourVehicle);
                    MainReceipt.setString(28, String.valueOf(String.valueOf(AIIYourInsuranceAddress)) + " " + AIIYourInsuranceAddress2);
                    MainReceipt.setString(29, AIIYourInsuranceCity);
                    MainReceipt.setString(30, AIIYourInsuranceState);
                    MainReceipt.setString(31, AIIYourInsuranceZipCode);
                    MainReceipt.setString(32, String.valueOf(String.valueOf(AIIYourInsuranceAreaCode)) + AIIYourInsurancePhoneNumber);
                    MainReceipt.setString(33, AIIYourInsurancePolicyNo);
                    MainReceipt.setString(34, AIIYourLicensePlate);
                    MainReceipt.setString(35, AIIYourCarMakeModelYear);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception e) {
                    helper.SendEmailWithAttachment("Error in PatientReg2_Mobile ** (SaveDataVictoria Insertion Patient_AutoInsuranceInfo Table ^^" + facilityName + " ##MES#006)", servletContext, e, "PatientReg2_Mobile", "SaveDataVictoria", conn);
                    Services.DumException("PatientReg2", "SaveDataVictoriaError 6- Insertion Patient_AutoInsuranceInfo Table :", request, e, this.getServletContext());
                    return;
                }
            }
            if (HealthInsuranceChk.equals("1")) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".Patient_HealthInsuranceInfo (PatientRegId," + "GovtFundedInsurancePlanChk,GFIPMedicare,GFIPMedicaid,GFIPCHIP,GFIPTricare,GFIPVHA,GFIPIndianHealth,InsuranceSubPatient," + "InsuranceSubGuarantor,InsuranceSubOther,HIPrimaryInsurance,HISubscriberFirstName,HISubscriberLastName,HISubscriberDOB,HISubscriberSSN," + "HISubscriberRelationtoPatient,HISubscriberGroupNo,HISubscriberPolicyNo,SecondHealthInsuranceChk,SHISecondaryName," + "SHISubscriberFirstName,SHISubscriberLastName,SHISubscriberRelationtoPatient,SHISubscriberGroupNo,SHISubscriberPolicyNo,CreatedDate) \n" + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
                    MainReceipt.setInt(1, PatientRegId);
                    MainReceipt.setInt(2, Integer.parseInt(GovtFundedInsurancePlanChk));
                    MainReceipt.setInt(3, Integer.parseInt(GFIPMedicare));
                    MainReceipt.setInt(4, Integer.parseInt(GFIPMedicaid));
                    MainReceipt.setInt(5, Integer.parseInt(GFIPCHIP));
                    MainReceipt.setInt(6, Integer.parseInt(GFIPTricare));
                    MainReceipt.setInt(7, Integer.parseInt(GFIPVHA));
                    MainReceipt.setInt(8, Integer.parseInt(GFIPIndianHealth));
                    MainReceipt.setString(9, InsuranceSubPatient);
                    MainReceipt.setString(10, InsuranceSubGuarantor);
                    MainReceipt.setString(11, InsuranceSubOther);
                    MainReceipt.setString(12, HIPrimaryInsurance);
                    MainReceipt.setString(13, HISubscriberFirstName);
                    MainReceipt.setString(14, HISubscriberLastName);
                    MainReceipt.setString(15, HISubscriberDOB);
                    MainReceipt.setString(16, HISubscriberSSN);
                    MainReceipt.setString(17, HISubscriberRelationtoPatient);
                    MainReceipt.setString(18, HISubscriberGroupNo);
                    MainReceipt.setString(19, HISubscriberPolicyNo);
                    MainReceipt.setInt(20, Integer.parseInt(SecondHealthInsuranceChk));
                    MainReceipt.setString(21, SHISecondaryName);
                    MainReceipt.setString(22, SHISubscriberFirstName);
                    MainReceipt.setString(23, SHISubscriberLastName);
                    MainReceipt.setString(24, SHISubscriberRelationtoPatient);
                    MainReceipt.setString(25, SHISubscriberGroupNo);
                    MainReceipt.setString(26, SHISubscriberPolicyNo);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception e) {
                    helper.SendEmailWithAttachment("Error in PatientReg2_Mobile ** (SaveDataVictoria Insertion Patient_HealthInsuranceInfo Table ^^" + facilityName + " ##MES#007)", servletContext, e, "PatientReg2_Mobile", "SaveDataVictoria", conn);
                    Services.DumException("PatientReg2_Mobile", "SaveDataVictoriaError 7- Insertion Patient_HealthInsuranceInfo Table", request, e, this.getServletContext());
                    return;
                }
            }

            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(
                        " INSERT INTO " + Database + ".MarketingInfo (PatientRegId," +
                                "MFFirstVisit,MFReturnPat,MFInternetFind,Facebook,MapSearch,GoogleSearch," +
                                "VERWebsite,WebsiteAds,OnlineReviews,Twitter," +
                                "LinkedIn,EmailBlast,YouTube,TV,Billboard,Radio,Brochure,DirectMail,CitizensDeTar,LiveWorkNearby," +
                                "FamilyFriend,FamilyFriend_text,UrgentCare,UrgentCare_text,NewspaperMagazine,NewspaperMagazine_text," +
                                "School,School_text,Hotel,Hotel_text,MFPhysician,CreatedDate) \n" +
                                " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
                MainReceipt.setInt(1, PatientRegId);
                MainReceipt.setInt(2, Integer.parseInt(MFFirstVisit));
                MainReceipt.setInt(3, Integer.parseInt(MFReturnPat));
                MainReceipt.setInt(4, Integer.parseInt(MFInternetFind));
                MainReceipt.setString(5, Facebook);
                MainReceipt.setString(6, MapSearch);
                MainReceipt.setString(7, GoogleSearch);
                MainReceipt.setString(8, VERWebsite);
                MainReceipt.setString(9, WebsiteAds);
                MainReceipt.setString(10, OnlineReviews);
                MainReceipt.setString(11, Twitter);
                MainReceipt.setString(12, LinkedIn);
                MainReceipt.setString(13, EmailBlast);
                MainReceipt.setString(14, YouTube);
                MainReceipt.setString(15, TV);
                MainReceipt.setString(16, Billboard);
                MainReceipt.setString(17, Radio);
                MainReceipt.setString(18, Brochure);
                MainReceipt.setString(19, DirectMail);
                MainReceipt.setString(20, CitizensDeTar);
                MainReceipt.setString(21, LiveWorkNearby);
                MainReceipt.setString(22, FamilyFriend);
                MainReceipt.setString(23, FamilyFriend_text);
                MainReceipt.setString(24, UrgentCare);
                MainReceipt.setString(25, UrgentCare_text);
                MainReceipt.setString(26, NewspaperMagazine);
                MainReceipt.setString(27, NewspaperMagazine_text);
                MainReceipt.setString(28, School);
                MainReceipt.setString(29, School_text);
                MainReceipt.setString(30, Hotel);
                MainReceipt.setString(31, Hotel_text);
                MainReceipt.setString(32, MFPhysician);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2_Mobile ** (SaveDataVictoria Insertion MarketingInfo Table ^^" + facilityName + " ##MES#008)", servletContext, e, "PatientReg2_Mobile", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2_Mobile", "SaveDataVictoriaError 8- Insertion MarketingInfo Table", request, e, this.getServletContext());
                return;
            }
            String PatientName = null;
            Query = "Select CONCAT(IFNULL(Title,''), ' ' , IFNULL(FirstName,''), ' ', IFNULL(MiddleInitial,''), ' ', IFNULL(LastName,'')) from " + Database + ".PatientReg where ID = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientName = rset.getString(1);
            }
            rset.close();
            stmt.close();
            String Message = "";
            String CDCFlag = "0";
            String InsertCOVIDRegReply = "0";
            System.out.println(String.valueOf(String.valueOf(ReasonVisit)) + " Before");
            if (ClientIndex == 9) {
                if (ReasonVisit != null) {
                    ReasonVisit = ReasonVisit.replaceAll(" ", "");
                    System.out.println(String.valueOf(String.valueOf(ReasonVisit)) + " After");
                    if (ReasonVisit.toUpperCase().equals("COVIDTESTING")) {
                        InsertCOVIDRegReply = InsertCOVIDReg(request, response, out, conn, String.valueOf(PatientRegId));
                        if (Integer.parseInt(InsertCOVIDRegReply) > 0) {
                            Message = "COVID Form Also Registered Successfully.";
                            CDCFlag = "1";
                        } else {
                            Message = "COVID Form Not Registered. ";
                            CDCFlag = "0";
                        }
                    }
                }
                Query = "Update victoria.PatientVisit set CDCFlag = '" + CDCFlag + "' where Id = " + VisitId;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            }

            final StringBuffer ProfessionalPayersList = new StringBuffer();
            Query = "Select Date_format(now(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Query = "Select Id, PayerId, PayerName from oe_2.ProfessionalPayers where PayerName like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ProfessionalPayersList.append("<option class=Inner value='-1'>Select Insurance</option>");
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
            stmt.close();
            if (ClientId.equals("Victoria-ER")) {
                PatientReg2 ptr2 = new PatientReg2();
                String temp = ptr2.SaveBundle_Victoria(request, out, conn, response, Database, ClientIndex, DirectoryName, PatientRegId, "REGISTRATION");
                System.out.println("temp " + temp);
//                .print();
                String[] arr = temp.split("~");
                String FileName = arr[2];
                String outputFilePath = arr[1];
                String pageCount = arr[0];
                Parsehtm Parser = new Parsehtm(request);
//                Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " We Have Registered You Successfully " + Message + ". Please walk to the front door and Press the buzzer.  DATED: " + Date);
                Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " We Have Registered You Successfully " + Message + ". Please wait for further processing.  DATED: " + Date);
                Parser.SetField("MRN", "MRN: " + MRN);
                Parser.SetField("FormName", "PatientReg");
                Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex);
                Parser.SetField("pageCount", String.valueOf(pageCount));
                Parser.SetField("FileName", String.valueOf(FileName));
                Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
                Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
                Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/MessageVictoria.html");
            } else {
                final Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Date", String.valueOf(Date));
                Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
                Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
                Parser.SetField("PatientName", String.valueOf(PatientName));
                Parser.SetField("myVal", "1");
                Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(this.getServletContext()))) + "Forms/PatientRegFormERDallas.html");
            }


//            if (ClientIndex == 9) {
//                final Parsehtm Parser = new Parsehtm(request);
//                Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(this.getServletContext()))) + "Forms/PatientRegFormVictoria_2.html");
//            } else if (ClientIndex == 28) {
//                final Parsehtm Parser = new Parsehtm(request);
//                Parser.SetField("Date", String.valueOf(Date));
//                Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
//                Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
//                Parser.SetField("PatientName", String.valueOf(PatientName));
//                Parser.SetField("myVal", "1");
//                Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(this.getServletContext()))) + "Forms/PatientRegFormERDallas.html");
//            }
            //Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(this.getServletContext()))) + "Forms/"+PRFName+"_Mobile.html");
//            final Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " We Have Registered You Successfully. Please Wait for Further Processing. " + Message + " <br>DATED: " + Date);
//            Parser.SetField("FormName", String.valueOf("PatientReg2"));
//            Parser.SetField("ActionID", String.valueOf("Victoria_2&ClientId=Victoria"));
//            Parser.SetField("ClientId", String.valueOf(ClientId));
//            Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/orange_2/Exception/Message.html");
        } catch (Exception e2) {
            helper.SendEmailWithAttachment("Error in PatientReg2_Mobile ** (SaveDataVictoria Main Catch ^^" + facilityName + " ##MES#009)", servletContext, e2, "PatientReg2_Mobile", "SaveDataVictoria", conn);
            Services.DumException("PatientReg2", "SaveDataVictoria", request, e2, this.getServletContext());
            out.println("Error Found. Kindly Fill Required Fields !!!");
            out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
        }
    }

    private String InsertCOVIDReg(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final String PatientRegId) throws JsonProcessingException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Request = "";
        final HashMap<String, Object> responseJSON = new HashMap<String, Object>();
        final ObjectMapper jsonMapper = new ObjectMapper();
        String FirstName = "";
        String MiddleInitial = "";
        String LastName = "";
        String Email = "";
        String PhNumber = "";
        String DOB = "";
        String Gender = "";
        String Ethnicity = "";
        String Race = "";
        String City = "";
        String Address = "";
        String State = "";
        String ZipCode = "";
        String County = "";
        String reply = "";
        try {
            Query = "Select IFNULL(FirstName,''), IFNULL(MiddleInitial,''), IFNULL(LastName,''), IFNULL(Email, ''), IFNULL(PhNumber,0), IFNULL(DOB,'0000-00-00'), IFNULL(Gender,'M'), IFNULL(City,''),IFNULL(Address,''), IFNULL(State,'TX'), IFNULL(ZipCode,''), IFNULL(County,'') from victoria.PatientReg where ID = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FirstName = rset.getString(1).trim();
                MiddleInitial = rset.getString(2).trim();
                LastName = rset.getString(3).trim();
                Email = rset.getString(4).trim();
                PhNumber = rset.getString(5).trim();
                DOB = rset.getString(6).trim();
                Gender = rset.getString(7).trim();
                City = rset.getString(8).trim();
                Address = rset.getString(9).trim();
                State = rset.getString(10).trim();
                ZipCode = rset.getString(11).trim();
                County = rset.getString(12);
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            Services.DumException("PatientReg2", "InsertCOVIDRegError in Getting Data from PatientReg table", request, e, this.getServletContext());
        }
        try {
            Query = "Select IFNULL(Ethnicity,''), IFNULL(Race,'') from victoria.PatientReg_Details where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Ethnicity = rset.getString(1);
                Race = rset.getString(2);
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            Services.DumException("PatientReg2", "InsertCOVIDRegError in Getting Data from PatientReg_Details table", request, e, this.getServletContext());
        }
        if (Gender.equals("male")) {
            Gender = "M";
        } else {
            Gender = "F";
        }

        if (Ethnicity.equals("1")) {
            Ethnicity = "H";
        } else if (Ethnicity.equals("2")) {
            Ethnicity = "NH";
        } else if (Ethnicity.equals("3")) {
            Ethnicity = "U";
        } else {
            Ethnicity = "U";
        }
        if (Race.equals("1")) {
            Race = "A";
        } else if (Race.equals("2")) {
            Race = "B";
        } else if (Race.equals("3")) {
            Race = "W";
        } else if (Race.equals("4")) {
            Race = "O";
        } else if (Race.equals("5")) {
            Race = "U";
        } else {
            Race = "U";
        }
        if (MiddleInitial.length() > 1) {
            MiddleInitial = MiddleInitial.substring(0, 1);
        }
        if (PhNumber.contains("-")) {
            PhNumber = PhNumber.replaceAll("-", "");
        }
        if (PhNumber.length() < 10) {
            PhNumber = PhNumber + "0";
        }
        try {
            responseJSON.put("FirstName", FirstName);
            responseJSON.put("MiddleName", MiddleInitial);
            responseJSON.put("LastName", LastName);
            responseJSON.put("Email", Email);
            responseJSON.put("ConfirmEmail", Email);
            responseJSON.put("Mobile", PhNumber);
            responseJSON.put("DOB", DOB);
            responseJSON.put("Sex", Gender);
            responseJSON.put("Ethnicity", Ethnicity);
            responseJSON.put("Race", Race);
            responseJSON.put("City", City);
            responseJSON.put("County", County);
            responseJSON.put("Street", Address);
            responseJSON.put("StateCode", State);
            responseJSON.put("IsValidDOB", true);
            responseJSON.put("Zipcode", ZipCode);
            Request = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString((Object) responseJSON);

            //System.out.println("Request: " + Request);
            final String BaseURL = "https://victoriacovid.com/api/CovidPatient/CreatePatient/?UserId=1";
            final String Mask = "";
            final URL url = new URL("https://victoriacovid.com/api/CovidPatient/CreatePatient/?UserId=1");
            final URLConnection uc = url.openConnection();
            uc.setReadTimeout(15000);
            uc.setConnectTimeout(17000);
            uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            uc.setRequestProperty("Accept", "application/json");
            uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
            uc.setAllowUserInteraction(false);
            uc.setDoOutput(true);
            final OutputStream os = uc.getOutputStream();
            os.write(Request.getBytes("UTF-8"));
            os.close();
            uc.connect();
            final InputStream is = uc.getInputStream();
            final int size = is.available();
            final byte[] response2 = new byte[size];
            is.read(response2);
            reply = new String(response2);
            reply = reply.trim();

            System.out.println("Reply: " + reply);
        } catch (Exception e) {
            final String Message = "0";
            Services.DumException("PatientReg2", "InsertCOVIDReg 0", request, e, this.getServletContext());
            return "0";
        }
        return reply;
    }
}
