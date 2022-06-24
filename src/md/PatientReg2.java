//
// Decompiled by Procyon v0.5.36
//

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.*;

@SuppressWarnings("Duplicates")
public class PatientReg2 extends HttpServlet {

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
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter(response.getOutputStream());
        ServletContext context = null;
        UtilityHelper helper = new UtilityHelper();
        try {
            context = this.getServletContext();
            conn = Services.getMysqlConn(context);
            switch (ActionID) {
                case "Victoria_2":
                    this.Victoria_2(request, out, conn, context, helper);
                    break;
                case "Victoria_22":
                    Victoria_22(request, out, conn, context, helper);
                    break;
                case "SaveDataVictoria":
                    SaveDataVictoria(request, out, conn, context, response, helper);
                    break;
                case "PatientsDocUpload_Save":
                    PatientsDocUpload_Save(request, out, conn, context, response);
                    break;
                case "PatientsDocUpload_Update":
                    PatientsDocUpload_Update(request, out, conn, context, response);
                    break;
                case "EditValues":
                    EditValues(request, out, conn, context, response, helper);
                    break;
                case "EditSave":
                    EditSave(request, out, conn, context, response, helper);
                    break;
                case "CheckPatient":
                    this.CheckPatient(request, out, conn);
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

    void Victoria_2(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, UtilityHelper helper) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Date = "";
            final String PRFName = "";
            int ClientIndex = 0;
            final StringBuffer ProfessionalPayersList = new StringBuffer();
            final StringBuffer Month = new StringBuffer();
            final StringBuffer Day = new StringBuffer();
            final StringBuffer Year = new StringBuffer();
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
            final String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            final int day = 1;
            final int year = Calendar.getInstance().get(1);
            for (int i = 1; i <= month.length; ++i) {
                if (i < 10)
                    Month.append("<option value=0" + i + ">" + month[i - 1] + "</option>");
                else
                    Month.append("<option value=" + i + ">" + month[i - 1] + "</option>");
            }
            for (int i = 1; i <= 31; ++i) {
                if (i < 10)
                    Day.append("<option value=0" + i + ">" + i + "</option>");
                else
                    Day.append("<option value=" + i + ">" + i + "</option>");
            }
            for (int i = 1901; i <= year; ++i) {
                if (i == year) {
                    Year.append("<option value=" + i + " selected>" + i + "</option>");
                } else {
                    Year.append("<option value=" + i + ">" + i + "</option>");
                }
            }
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Date", String.valueOf(Date));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("Month", String.valueOf(Month));
            Parser.SetField("Day", String.valueOf(Day));
            Parser.SetField("Year", String.valueOf(Year));
            Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
            if (ClientIndex == 9) {
                //Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/PatientRegFormVictoria_2.html");
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/PatientRegFormVictoria_2_2.html");
            } else if (ClientIndex == 38) {
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/PatientRegFormAllyER.html");
            } else if (ClientIndex == 28) {
                Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(this.getServletContext()))) + "Forms/PatientRegFormERDallas.html");
            }
        } catch (Exception ex) {
            out.println(ex.getMessage());
            String str = "";
            for (int j = 0; j < ex.getStackTrace().length; ++j) {
                str = str + ex.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }

    void Victoria_22(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, UtilityHelper helper) {
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

    void SaveDataVictoria(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, UtilityHelper helper) throws FileNotFoundException {
        String facilityName = "";

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
        String Month = "00";
        String Day = "00";
        String Year = "0000";
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
        String AIIDateofAccident = "";
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
        String SHISecondaryName = "";
        String SHISubscriberFirstName = "";
        String SHISubscriberLastName = "";
        String SHISubscriberDOB = "";
        String SHISubscriberRelationtoPatient = "";
        String SHISubscriberGroupNo = "";
        String SHISubscriberPolicyNo = "";
        String MFFirstVisit = "";

        String MFReturnPat = "";
        String MFInternetFind = "";
        String Facebook = "";
        String MapSearch = "";
        String GoogleSearch = "";
        String VERWebsite = "";
        String WebsiteAds = "";
        String OnlineAdvertisements = "";
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
        String EmployerSentMe = "";
        String EmployerSentMe_text = "";
        String PatientCell = "";
        String MFPhysicianRefChk = "";
        String MFPhysician = "";
        String RecInitial = "";
        int VisitId = 0;
        try {

//                        "INSERT INTO oe.EligibilityInquiry (PatientMRN,DateofService,TraceId ,PolicyStatus,strmsg, " +
//                                "Name, DateofBirth, Gender, InsuranceNum, GediPayerId, CreatedBy, CreatedDate,ResponseType) " +
//                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,now(),?) ");
            PreparedStatement ps = conn.prepareStatement("Select Id, dbname,DirectoryName from oe.clients where ltrim(rtrim(UPPER(name))) = ?");
            ps.setString(1, ClientId.trim().toUpperCase());

            rset = ps.executeQuery();
            if (rset.next()) {
                ClientIndex = rset.getInt(1);
                Database = rset.getString(2);
                DirectoryName = rset.getString(3);
            }
            rset.close();
            ps.close();

//            System.out.println("query 1 converted");

            facilityName = helper.getFacilityName(request, conn, servletContext, ClientIndex);
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

            if (request.getParameter("Month") == null) {
                Month = "";
            } else {
                Month = request.getParameter("Month").trim();
            }
            if (request.getParameter("Day") == null) {
                Day = "";
            } else {
                Day = request.getParameter("Day").trim();
            }
            if (request.getParameter("Year") == null) {
                Year = "";
            } else {
                Year = request.getParameter("Year").trim();
            }
            DOB = Year + "-" + Month + "-" + Day;

//            if (request.getParameter("DOB") == null) {
//                DOB = "0000-00-00";
//            } else {
//                DOB = request.getParameter("DOB").trim();
//                DOB = String.valueOf(String.valueOf(DOB.substring(6, 10))) + "-" + DOB.substring(0, 2) + "-" + DOB.substring(3, 5);
//            }
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


            int PatientFound = 0;
            String FoundMRN = "";
            try {
                ps = conn.prepareStatement(" Select COUNT(*), IFNULL(MRN,0) from " + Database + ".PatientReg  " +
                        "where Status = 0 and ltrim(rtrim(UPPER(FirstName))) = ?  and " +
                        " ltrim(rtrim(UPPER(LastName))) = ? and DOB = ?");
                ps.setString(1, FirstName.trim().toUpperCase());
                ps.setString(2, LastName.trim().toUpperCase());
                ps.setString(3, DOB);

                rset = ps.executeQuery();
                if (rset.next()) {
                    PatientFound = rset.getInt(1);
                    FoundMRN = rset.getString(2);
                }
                rset.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("query " + ps.toString());
                System.out.println("Error -> " + e.getMessage());
            }

//            System.out.println("query 2 converted");


            if (PatientFound > 0) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Title", "Patient Already Found. MRN: " + FoundMRN);
                Parser.SetField("Text", "Please Proceed to Front Desk with the MRN.");
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientId + "");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Warning.html");
                return;
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

            if (request.getParameter("TestForTravelChk") == null) {
                TestForTravel = "0";
            } else {
                TestForTravel = request.getParameter("TestForTravelChk").trim();
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
//                    WCPDateofInjury = String.valueOf(String.valueOf(WCPDateofInjury.substring(6, 10))) + "-" + WCPDateofInjury.substring(0, 2) + "-" + WCPDateofInjury.substring(3, 5);
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
//                        AIIDateofAccident = String.valueOf(String.valueOf(AIIDateofAccident.substring(6, 10))) + "-" + AIIDateofAccident.substring(0, 2) + "-" + AIIDateofAccident.substring(3, 5);
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
                    if (request.getParameter("HISubscriberDOB").length() > 0) {
                        HISubscriberDOB = request.getParameter("HISubscriberDOB").trim();
                        //HISubscriberDOB = String.valueOf(String.valueOf(HISubscriberDOB.substring(6, 10))) + "-" + HISubscriberDOB.substring(0, 2) + "-" + HISubscriberDOB.substring(3, 5);
                    } else {
                        HISubscriberDOB = "0000-00-00";
                    }
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
                        if (request.getParameter("SHISubscriberDOB").length() > 0) {
                            SHISubscriberDOB = request.getParameter("SHISubscriberDOB").trim();
                            //SHISubscriberDOB = String.valueOf(String.valueOf(SHISubscriberDOB.substring(6, 10))) + "-" + SHISubscriberDOB.substring(0, 2) + "-" + SHISubscriberDOB.substring(3, 5);
                        } else {
                            SHISubscriberDOB = "0000-00-00";
                        }
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
            if (request.getParameter("OnlineAdvertisements") == null) {
                OnlineAdvertisements = "0";
            } else {
                OnlineAdvertisements = "1";
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
            if (request.getParameter("EmployerSentMe") == null) {
                EmployerSentMe = "0";
            } else {
                EmployerSentMe = "1";
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
            if (request.getParameter("EmployerSentMe_text") == null) {
                EmployerSentMe_text = "";
            } else {
                EmployerSentMe_text = request.getParameter("EmployerSentMe_text");
            }
            if (request.getParameter("MFPhysicianRefChk") == null) {
                MFPhysicianRefChk = "0";
            } else {
                MFPhysicianRefChk = request.getParameter("MFPhysicianRefChk");
            }
            if (request.getParameter("MFPhysician") == null) {
                MFPhysician = "";
            } else {
                MFPhysician = request.getParameter("MFPhysician");
            }
            if (request.getParameter("PatientCell") == null) {
                PatientCell = "";
            } else {
                PatientCell = request.getParameter("PatientCell").trim();
            }
            if (request.getParameter("RecInitial") == null) {
                RecInitial = "";
            } else {
                RecInitial = request.getParameter("RecInitial").trim();
            }
            ps = conn.prepareStatement("Select Date_format(now(),'%Y-%m-%d')");
            rset = ps.executeQuery();
            while (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            ps.close();
            try {

                ps = conn.prepareStatement("Select MRN from " + Database + ".PatientReg order by ID desc limit 1 ");
                rset = ps.executeQuery();
                if (rset.next()) {
                    MRN = rset.getInt(1);
                }
                rset.close();
                ps.close();
                if (String.valueOf(MRN).length() == 0) {
                    MRN = 310001;
                } else if (String.valueOf(MRN).length() == 4) {
                    MRN = 310001;
                } else if (String.valueOf(MRN).length() == 8) {
                    MRN = 310001;
                } else if (MRN == 0) {
                    MRN = 310001;
                } else if (String.valueOf(MRN).length() == 6) {
                    ++MRN;
                }
                if (ClientIndex == 8) {
                    ExtendedMRN = "1008" + MRN;
                } else if (ClientIndex == 9 || ClientIndex == 38) {
                    ExtendedMRN = "1009" + MRN;
                } else if (ClientIndex == 10) {
                    ExtendedMRN = "1010" + MRN;
                } else if (ClientIndex == 11) {
                    ExtendedMRN = "1011" + MRN;
                }
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria PatineReg2 Data get ^^" + facilityName + " ##MES#001)", servletContext, ex, "PatientReg2", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2", "MES#001", request, ex, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                Parser.SetField("Message", "MES#001");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }
            try {
                if (!Email.equals(ConfirmEmail)) {
                    out.println("<!DOCTYPE html><html><body><p style=\"color:black;\">Please Put Email and Confirm Email Correctly and then Submit</p>");
                    out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
                    return;
                }
                if (Age.equals("-1")) {
                    out.println("<!DOCTYPE html><html><body><p style=\"color:black;\">Please Select Date of Birth Correctly with Day, Month, Year and then Submit (AGE Cannot be -1)</p>");
                    out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
                    return;
                }
                Email = ConfirmEmail;
                UtilityHelper utilityHelper = new UtilityHelper();
                String ClientIp = utilityHelper.getClientIp(request);
                PreparedStatement MainReceipt = conn.prepareStatement(
                        " INSERT INTO " + Database + ".PatientReg (ClientIndex,FirstName,LastName , MiddleInitial,DOB,Age,Gender ,Email," +
                                "PhNumber ,Address,City ,State,Country,ZipCode,SSN,Occupation ,Employer ,EmpContact, PriCarePhy," +
                                "ReasonVisit,SelfPayChk,CreatedDate,Title, MaritalStatus,CreatedBy, MRN, Status, ExtendedMRN, County," +
                                "sync,RegisterFrom,ViewDate, EnterIP)" +
                                " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,0,?,?,0,?,NOW(),?) ");
                MainReceipt.setInt(1, ClientIndex);
                MainReceipt.setString(2, FirstName.toUpperCase());
                MainReceipt.setString(3, LastName.toUpperCase());
                MainReceipt.setString(4, MiddleInitial.toUpperCase());
                MainReceipt.setString(5, DOB);
                MainReceipt.setString(6, Age);
                MainReceipt.setString(7, gender.toUpperCase());
                MainReceipt.setString(8, Email);
                MainReceipt.setString(9, String.valueOf(String.valueOf(AreaCode)) + PhNumber);
                MainReceipt.setString(10, String.valueOf(String.valueOf(Address)) + " " + Address2);
                MainReceipt.setString(11, City.toUpperCase());
                MainReceipt.setString(12, State.toUpperCase());
                MainReceipt.setString(13, Country.toUpperCase());
                MainReceipt.setString(14, ZipCode);
                MainReceipt.setString(15, SSN);
                MainReceipt.setString(16, "");
                MainReceipt.setString(17, "");
                MainReceipt.setString(18, "");
                MainReceipt.setString(19, "");
                MainReceipt.setString(20, ReasonVisit);
                MainReceipt.setInt(21, 0);
                MainReceipt.setString(22, Title.toUpperCase());
                MainReceipt.setString(23, MaritalStatus.toUpperCase());
                MainReceipt.setString(24, "Out Patient");
                MainReceipt.setInt(25, MRN);
                MainReceipt.setString(26, ExtendedMRN);
                MainReceipt.setString(27, County.toUpperCase());
                MainReceipt.setString(28, "**Patient Reg2");
                MainReceipt.setString(29, ClientIp);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Insertion PatientReg Table ^^" + facilityName + " ##MES#002****)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2", "SaveDataVictoriaError 2- Insertion PatientReg Table :", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                Parser.SetField("Message", "MES#002");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }
            try {
                ps = conn.prepareStatement("Select max(ID) from " + Database + ".PatientReg ");
                rset = ps.executeQuery();
                if (rset.next()) {
                    PatientRegId = rset.getInt(1);
                }
                rset.close();
                ps.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria ^^" + facilityName + " ##MES#003)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2", "SaveDataVictoriaError 3- :", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                Parser.SetField("Message", "MES#003");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".PatientVisit(MRN,PatientRegId,ReasonVisit,VisitNumber,DoctorId,DateofService," +
                                "CreatedDate,CreatedBy) VALUES (?,?,?,1,NULL,now(),now(),?) ");
                MainReceipt.setInt(1, MRN);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, ReasonVisit);
                MainReceipt.setString(4, "Out Patient");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Insertion PatientVisit Table ^^" + facilityName + " ##MES#003)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2", "SaveDataVictoriaError 3.1 Insertion in table PatientVisit- :", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                Parser.SetField("Message", "MES#004");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                //return;
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
                        " EmpHealthChk, PregChk,TestForTravel) \n" +
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
                MainReceipt.setString(11, String.valueOf(String.valueOf(PriCarePhyAddress)) + " " + PriCarePhyAddress2);
                MainReceipt.setString(12, PriCarePhyCity);
                MainReceipt.setString(13, PriCarePhyState);
                MainReceipt.setString(14, PriCarePhyZipCode);
                MainReceipt.setInt(15, Integer.parseInt(PatientMinorChk));
                MainReceipt.setString(16, GuarantorChk);
                MainReceipt.setString(17, GuarantorEmployer);
                MainReceipt.setString(18, String.valueOf(String.valueOf(GuarantorEmployerAreaCode)) + GuarantorEmployerPhNumber);
                MainReceipt.setString(19, String.valueOf(String.valueOf(GuarantorEmployerAddress)) + " " + GuarantorEmployerAddress2);
                MainReceipt.setString(20, GuarantorEmployerCity);
                MainReceipt.setString(21, GuarantorEmployerState);
                MainReceipt.setString(22, GuarantorEmployerZipCode);
                MainReceipt.setInt(23, Integer.parseInt(WorkersCompPolicyChk));
                MainReceipt.setInt(24, Integer.parseInt(MotorVehicleAccidentChk));
                MainReceipt.setInt(25, Integer.parseInt(HealthInsuranceChk));
                MainReceipt.setInt(26, Integer.parseInt(SympChkCOVID));
                //MainReceipt.setString(27, DateSympOnset);
                if (!DateSympOnset.equals(""))
                    MainReceipt.setString(27, DateSympOnset);
                else
                    MainReceipt.setNull(27, Types.DATE);
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
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Insertion PatientReg_Details Table ^^" + facilityName + " ##MES#004)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                Parser.SetField("Message", "MES#005");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                Services.DumException("PatientReg2", "SaveDataVictoriaError 4- Insertion PatientReg_Details Table :", request, e, this.getServletContext());
                //return;
            }
            if (WorkersCompPolicyChk.equals("1")) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement(
                            " INSERT INTO " + Database + ".Patient_WorkCompPolicy (PatientRegId,WCPDateofInjury,WCPCaseNo," +
                                    "WCPGroupNo,WCPMemberId,WCPInjuryRelatedAutoMotorAccident,WCPInjuryRelatedWorkRelated," +
                                    "WCPInjuryRelatedOtherAccident,WCPInjuryRelatedNoAccident,WCPInjuryOccurVehicle," +
                                    "WCPInjuryOccurWork,WCPInjuryOccurHome,WCPInjuryOccurOther,WCPInjuryDescription," +
                                    "WCPHRFirstName,WCPHRLastName,WCPHRPhoneNumber,WCPHRAddress,WCPHRCity,WCPHRState," +
                                    "WCPHRZipCode,WCPPlanName,WCPCarrierName,WCPPayerPhoneNumber,WCPCarrierAddress," +
                                    "WCPCarrierCity,WCPCarrierState,WCPCarrierZipCode,WCPAdjudicatorFirstName," +
                                    "WCPAdjudicatorLastName,WCPAdjudicatorPhoneNumber,WCPAdjudicatorFaxPhoneNumber,CreatedDate)" +
                                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
                    MainReceipt.setInt(1, PatientRegId);
                    //MainReceipt.setString(2, WCPDateofInjury);
                    if (!WCPDateofInjury.equals(""))
                        MainReceipt.setString(2, WCPDateofInjury);
                    else
                        MainReceipt.setNull(2, Types.DATE);
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
                    MainReceipt.setString(17, String.valueOf(String.valueOf(WCPHRAreaCode)) + WCPHRPhoneNumber);
                    MainReceipt.setString(18, String.valueOf(String.valueOf(WCPHRAddress)) + " " + WCPHRAddress2);
                    MainReceipt.setString(19, WCPHRCity);
                    MainReceipt.setString(20, WCPHRState);
                    MainReceipt.setString(21, WCPHRZipCode);
                    MainReceipt.setString(22, WCPPlanName);
                    MainReceipt.setString(23, WCPCarrierName);
                    MainReceipt.setString(24, String.valueOf(String.valueOf(WCPPayerAreaCode)) + WCPPayerPhoneNumber);
                    MainReceipt.setString(25, String.valueOf(String.valueOf(WCPCarrierAddress)) + " " + WCPCarrierAddress2);
                    MainReceipt.setString(26, WCPCarrierCity);
                    MainReceipt.setString(27, WCPCarrierState);
                    MainReceipt.setString(28, WCPCarrierZipCode);
                    MainReceipt.setString(29, WCPAdjudicatorFirstName);
                    MainReceipt.setString(30, WCPAdjudicatorLastName);
                    MainReceipt.setString(31, String.valueOf(String.valueOf(WCPAdjudicatorAreaCode)) + WCPAdjudicatorPhoneNumber);
                    MainReceipt.setString(32, String.valueOf(String.valueOf(WCPAdjudicatorFaxAreaCode)) + WCPAdjudicatorFaxPhoneNumber);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception e) {
                    helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Insertion Patient_WorkCompPolicy Table ^^" + facilityName + " ##MES#005)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                    Services.DumException("PatientReg2", "SaveDataVictoriaError 5- Insertion Patient_WorkCompPolicy Table :", request, e, this.getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg2");
                    Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                    Parser.SetField("Message", "MES#006");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    //return;
                }
            }
            if (MotorVehicleAccidentChk.equals("1")) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + Database + ".Patient_AutoInsuranceInfo (PatientRegId,AutoInsuranceInformationChk," +
                                    "AIIDateofAccident,AIIAutoClaim,AIIAccidentLocationAddress,AIIAccidentLocationCity," +
                                    "AIIAccidentLocationState,AIIAccidentLocationZipCode,AIIRoleInAccident," +
                                    "AIITypeOfAutoIOnsurancePolicy,AIIPrefixforReponsibleParty,AIIFirstNameforReponsibleParty," +
                                    "AIIMiddleNameforReponsibleParty,AIILastNameforReponsibleParty,AIISuffixforReponsibleParty," +
                                    "AIICarrierResponsibleParty,AIICarrierResponsiblePartyAddress,AIICarrierResponsiblePartyCity," +
                                    "AIICarrierResponsiblePartyState,AIICarrierResponsiblePartyZipCode," +
                                    "AIICarrierResponsiblePartyPhoneNumber,AIICarrierResponsiblePartyPolicyNumber," +
                                    "AIIResponsiblePartyAutoMakeModel,AIIResponsiblePartyLicensePlate," +
                                    "AIIFirstNameOfYourPolicyHolder,AIILastNameOfYourPolicyHolder," +
                                    "AIINameAutoInsuranceOfYourVehicle,AIIYourInsuranceAddress,AIIYourInsuranceCity," +
                                    "AIIYourInsuranceState,AIIYourInsuranceZipCode,AIIYourInsurancePhoneNumber," +
                                    "AIIYourInsurancePolicyNo,AIIYourLicensePlate,AIIYourCarMakeModelYear,CreatedDate) " +
                                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
                    MainReceipt.setInt(1, PatientRegId);
                    MainReceipt.setInt(2, Integer.parseInt(AutoInsuranceInformationChk));
                    //MainReceipt.setString(3, AIIDateofAccident);
                    if (!AIIDateofAccident.equals(""))
                        MainReceipt.setString(3, AIIDateofAccident);
                    else
                        MainReceipt.setNull(3, Types.DATE);
                    MainReceipt.setString(4, AIIAutoClaim);
                    MainReceipt.setString(5, AIIAccidentLocationAddress + " " + AIIAccidentLocationAddress2);
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
                    MainReceipt.setString(17, AIICarrierResponsiblePartyAddress + " " + AIICarrierResponsiblePartyAddress2);
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
                    helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Insertion Patient_AutoInsuranceInfo Table ^^" + facilityName + " ##MES#006)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                    Services.DumException("PatientReg2", "SaveDataVictoriaError 6- Insertion Patient_AutoInsuranceInfo Table :", request, e, this.getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg2");
                    Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                    Parser.SetField("Message", "MES#007");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
//                    return;
                }
            }
            if (HealthInsuranceChk.equals("1")) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".Patient_HealthInsuranceInfo (PatientRegId,GovtFundedInsurancePlanChk,GFIPMedicare,GFIPMedicaid,GFIPCHIP,GFIPTricare,GFIPVHA,GFIPIndianHealth,InsuranceSubPatient,InsuranceSubGuarantor,InsuranceSubOther,HIPrimaryInsurance,HISubscriberFirstName,HISubscriberLastName,HISubscriberDOB,HISubscriberSSN,HISubscriberRelationtoPatient,HISubscriberGroupNo,HISubscriberPolicyNo,SecondHealthInsuranceChk,SHISecondaryName,SHISubscriberFirstName,SHISubscriberLastName,SHISubscriberRelationtoPatient,SHISubscriberGroupNo,SHISubscriberPolicyNo,CreatedDate) \n VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
                    MainReceipt.setInt(1, PatientRegId);
                    MainReceipt.setInt(2, Integer.parseInt(GovtFundedInsurancePlanChk));
                    MainReceipt.setInt(3, Integer.parseInt(GFIPMedicare));
                    MainReceipt.setInt(4, Integer.parseInt(GFIPMedicaid));
                    MainReceipt.setInt(5, Integer.parseInt(GFIPCHIP));
                    MainReceipt.setInt(6, Integer.parseInt(GFIPTricare));
                    MainReceipt.setInt(7, Integer.parseInt(GFIPVHA));
                    MainReceipt.setInt(8, Integer.parseInt(GFIPIndianHealth));
                    MainReceipt.setString(9, InsuranceSubPatient.toUpperCase());
                    MainReceipt.setString(10, InsuranceSubGuarantor.toUpperCase());
                    MainReceipt.setString(11, InsuranceSubOther.toUpperCase());
                    MainReceipt.setString(12, HIPrimaryInsurance.toUpperCase());
                    MainReceipt.setString(13, HISubscriberFirstName.toUpperCase());
                    MainReceipt.setString(14, HISubscriberLastName.toUpperCase());
                    MainReceipt.setString(15, HISubscriberDOB);
                    MainReceipt.setString(16, HISubscriberSSN);
                    MainReceipt.setString(17, HISubscriberRelationtoPatient.toUpperCase());
                    MainReceipt.setString(18, HISubscriberGroupNo);
                    MainReceipt.setString(19, HISubscriberPolicyNo);
                    MainReceipt.setInt(20, Integer.parseInt(SecondHealthInsuranceChk));
                    MainReceipt.setString(21, SHISecondaryName.toUpperCase());
                    MainReceipt.setString(22, SHISubscriberFirstName.toUpperCase());
                    MainReceipt.setString(23, SHISubscriberLastName.toUpperCase());
                    MainReceipt.setString(24, SHISubscriberRelationtoPatient.toUpperCase());
                    MainReceipt.setString(25, SHISubscriberGroupNo);
                    MainReceipt.setString(26, SHISubscriberPolicyNo);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception e) {
                    helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Insertion Patient_HealthInsuranceInfo Table ^^" + facilityName + " ##MES#007)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                    Services.DumException("PatientReg2", "SaveDataVictoriaError 7- Insertion Patient_HealthInsuranceInfo Table", request, e, this.getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg2");
                    Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                    Parser.SetField("Message", "MES#008");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
//                    return;
                }
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".MarketingInfo (PatientRegId," +
                        " MFFirstVisit,MFReturnPat,MFInternetFind,Facebook,MapSearch,GoogleSearch,VERWebsite,OnlineAdvertisements,OnlineReviews," +
                        " Twitter,LinkedIn,EmailBlast,YouTube,TV,Billboard,Radio,Brochure,DirectMail,CitizensDeTar,LiveWorkNearby,FamilyFriend," +
                        " FamilyFriend_text,UrgentCare,UrgentCare_text,NewspaperMagazine,NewspaperMagazine_text,School,School_text," +
                        " Hotel,Hotel_text,MFPhysician,CreatedDate,EmployerSentMe,EmployerSentMe_text,MFPhysicianRefChk,PatientCell,RecInitial,WebsiteAds) \n " +
                        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?) ");
                MainReceipt.setInt(1, PatientRegId);
                MainReceipt.setInt(2, Integer.parseInt(MFFirstVisit));
                MainReceipt.setInt(3, Integer.parseInt(MFReturnPat));
                MainReceipt.setInt(4, Integer.parseInt(MFInternetFind));
                MainReceipt.setString(5, Facebook);
                MainReceipt.setString(6, MapSearch);
                MainReceipt.setString(7, GoogleSearch);
                MainReceipt.setString(8, VERWebsite);
                MainReceipt.setString(9, OnlineAdvertisements);
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
                MainReceipt.setString(33, EmployerSentMe);
                MainReceipt.setString(34, EmployerSentMe_text);
                MainReceipt.setString(35, MFPhysicianRefChk);
                MainReceipt.setString(36, PatientCell);
                MainReceipt.setString(37, RecInitial);
                MainReceipt.setString(38, WebsiteAds);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Insertion MarketingInfo Table ^^" + facilityName + " ##MES#008)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2", "SaveDataVictoriaError 8- Insertion MarketingInfo Table", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                Parser.SetField("Message", "MES#009");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
//                return;
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
            if (ClientIndex == 9 && ReasonVisit != null) {
                ReasonVisit = ReasonVisit.replaceAll(" ", "");
                if (ReasonVisit.toUpperCase().equals("COVIDTESTING")) {
                    InsertCOVIDRegReply = this.InsertCOVIDReg(request, response, out, conn, String.valueOf(PatientRegId));
                    if (Integer.parseInt(InsertCOVIDRegReply) > 0) {
                        Message = "and COVID Form Also Registered Successfully.";
                        CDCFlag = "1";
                    } else {
                        Message = "and COVID Form Not Registered. ";
                        CDCFlag = "0";
                    }
                }
                Query = "Update victoria.PatientVisit set CDCFlag = '" + CDCFlag + "' where Id = " + VisitId;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            }
            if (ClientId.equals("Victoria-ER")) {
                String temp = SaveBundle_Victoria(request, out, conn, response, Database, ClientIndex, DirectoryName, PatientRegId, "REGISTRATION");
//                System.out.println("temp " + temp);
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
                Parser.SetField("pageCount", pageCount);
                Parser.SetField("FileName", String.valueOf(FileName));
                Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
                Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
                Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/MessageVictoria.html");
            } else {
                final Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Thank You " + PatientName + " We Have Registered You Successfully " + Message + ". Please walk to the front door and Press the buzzer.  DATED: " + Date);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("MRN", "MRN: " + MRN);
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientId);
                Parser.SetField("ClientId", ClientId);
                Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(servletContext))) + "Exception/Message.html");
            }

        } catch (Exception e2) {
            helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Main Catch ^^" + facilityName + " ##MES#010)", servletContext, e2, "PatientReg2", "SaveDataVictoria", conn);
            Services.DumException("PatientReg2", "SaveDataVictoria -- " + Query + " ", request, e2, this.getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "PatientReg2");
            Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
            Parser.SetField("Message", "MES#010");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void EditValues(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, UtilityHelper helper) throws FileNotFoundException {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Database = "";
            final String MRN = request.getParameter("MRN").trim();
            final int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
            int PatientRegId = 0;
            String Title = "";
            String FirstName = "";
            String LastName = "";
            String MiddleInitial = "";
            String DOB = "";
            String Age = "";
            String gender = "";
            String Email = "";
            String MaritalStatus = "";
            String AreaCode = "";
            String PhNumber = "";
            String Address = "";
            String City = "";
            String State = "";
            String Country = "";
            String County = "";
            String ZipCode = "";
            String Ethnicity = "";
            String Race = "";
            String SSN = "";
            String DoctorId = "";
            String EmployementChk = "0";
            String Employer = "";
            String Occupation = "";
            String EmpContact = "";
            String PrimaryCarePhysicianChk = "0";
            String PriCarePhy = "";
            String ReasonVisit = "";

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
            String EmpHealthChk = "0";
            String PregChk = "0";
            String TestForTravel = "0";


            String PriCarePhyAddress = "";
            String PriCarePhyCity = "";
            String PriCarePhyState = "";
            String PriCarePhyZipCode = "";
            String PatientMinorChk = "0";
            String GuarantorChk = "0";
            String GuarantorEmployer = "";
            String GuarantorEmployerAreaCode = "";
            String GuarantorEmployerPhNumber = "";
            String GuarantorEmployerAddress = "";
            String GuarantorEmployerCity = "";
            String GuarantorEmployerState = "";
            String GuarantorEmployerZipCode = "";
            String WorkersCompPolicyChk = "0";
            String WCPDateofInjury = "";
            String WCPCaseNo = "";
            String WCPGroupNo = "";
            String WCPMemberId = "";
            String WCPInjuryRelatedAutoMotorAccident = "0";
            String WCPInjuryRelatedWorkRelated = "0";
            String WCPInjuryRelatedOtherAccident = "0";
            String WCPInjuryRelatedNoAccident = "0";
            String WCPInjuryOccurVehicle = "0";
            String WCPInjuryOccurWork = "0";
            String WCPInjuryOccurHome = "0";
            String WCPInjuryOccurOther = "0";
            String WCPInjuryDescription = "";
            String WCPHRFirstName = "";
            String WCPHRLastName = "";
            String WCPHRAreaCode = "";
            String WCPHRPhoneNumber = "";
            String WCPHRAddress = "";
            String WCPHRCity = "";
            String WCPHRState = "";
            String WCPHRZipCode = "";
            String WCPPlanName = "";
            String WCPCarrierName = "";
            String WCPPayerAreaCode = "";
            String WCPPayerPhoneNumber = "";
            String WCPCarrierAddress = "";
            String WCPCarrierCity = "";
            String WCPCarrierState = "";
            String WCPCarrierZipCode = "";
            String WCPAdjudicatorFirstName = "";
            String WCPAdjudicatorLastName = "";
            String WCPAdjudicatorAreaCode = "";
            String WCPAdjudicatorPhoneNumber = "";
            String WCPAdjudicatorFaxAreaCode = "";
            String WCPAdjudicatorFaxPhoneNumber = "";
            String MotorVehicleAccidentChk = "0";
            String AutoInsuranceInformationChk = "0";
            String AIIDateofAccident = "";
            String AIIAutoClaim = "";
            String AIIAccidentLocationAddress = "";
            String AIIAccidentLocationCity = "";
            String AIIAccidentLocationState = "";
            String AIIAccidentLocationZipCode = "";
            String AIIRoleInAccident = "";
            String AIITypeOfAutoIOnsurancePolicy = "";
            String AIIPrefixforReponsibleParty = "";
            String AIIFirstNameforReponsibleParty = "";
            String AIIMiddleNameforReponsibleParty = "";
            String AIILastNameforReponsibleParty = "";
            String AIISuffixforReponsibleParty = "";
            String AIICarrierResponsibleParty = "";
            String AIICarrierResponsiblePartyAddress = "";
            String AIICarrierResponsiblePartyCity = "";
            String AIICarrierResponsiblePartyState = "";
            String AIICarrierResponsiblePartyZipCode = "";
            String AIICarrierResponsiblePartyAreaCode = "";
            String AIICarrierResponsiblePartyPhoneNumber = "";
            String AIICarrierResponsiblePartyPolicyNumber = "";
            String AIIResponsiblePartyAutoMakeModel = "";
            String AIIResponsiblePartyLicensePlate = "";
            String AIIFirstNameOfYourPolicyHolder = "";
            String AIILastNameOfYourPolicyHolder = "";
            String AIINameAutoInsuranceOfYourVehicle = "";
            String AIIYourInsuranceAddress = "";
            String AIIYourInsuranceCity = "";
            String AIIYourInsuranceState = "";
            String AIIYourInsuranceZipCode = "";
            String AIIYourInsuranceAreaCode = "";
            String AIIYourInsurancePhoneNumber = "";
            String AIIYourInsurancePolicyNo = "";
            String AIIYourLicensePlate = "";
            String AIIYourCarMakeModelYear = "";
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
            String HIPrimaryInsurance = "";
            String HISubscriberFirstName = "";
            String HISubscriberLastName = "";
            String HISubscriberDOB = "";
            String HISubscriberSSN = "";
            String HISubscriberRelationtoPatient = "";
            String HISubscriberGroupNo = "";
            String HISubscriberPolicyNo = "";
            String SecondHealthInsuranceChk = "";
            String SHISecondaryName = "";
            String SHISubscriberFirstName = "";
            String SHISubscriberLastName = "";
            String SHISubscriberRelationtoPatient = "";
            String SHISubscriberGroupNo = "";
            String SHISubscriberPolicyNo = "";

            String MFReturnPat = "";
            String MFFirstVisit = "";
            String MFInternetFind = "";
            String Facebook = "";
            String MapSearch = "";
            String GoogleSearch = "";
            String VERWebsite = "";
            String WebsiteAds = "";
            String OnlineAdvertisements = "";
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
            String EmployerSentMe = "";
            String EmployerSentMe_text = "";
            String PatientCell = "";
            String RecInitial = "";
            String MFPhysicianRefChk = "";
            String MFPhysician = "";
            String DOS = "";

            final StringBuffer TitleBuffer = new StringBuffer();
            final StringBuffer GenderBuffer = new StringBuffer();
            final StringBuffer MaritalStatusBuffer = new StringBuffer();
            final StringBuffer EthnicityBuffer = new StringBuffer();
            final StringBuffer RaceBuffer = new StringBuffer();
            final StringBuffer ReasonVisitBuffer = new StringBuffer();
            final StringBuffer EmploymentBuffer = new StringBuffer();
            final StringBuffer PriCarPhyBuffer = new StringBuffer();
            final StringBuffer PatinetMinorBuffer = new StringBuffer();
            final StringBuffer GuarantorBuffer = new StringBuffer();
            final StringBuffer WorkCompBuffer = new StringBuffer();
            final StringBuffer MotorVehicleBuffer = new StringBuffer();
            final StringBuffer AutoInsuranceBuffer = new StringBuffer();
            final StringBuffer HealthInsuranceBuffer = new StringBuffer();
            final StringBuffer GovtFundedInsuranceBuffer = new StringBuffer();
            final StringBuffer SecondHealthInsuranceBuffer = new StringBuffer();
            final StringBuffer AIITypeOfAutoIOnsurancePolicyBuffer = new StringBuffer();
            final StringBuffer AIIRoleInAccidentBuffer = new StringBuffer();
            final StringBuffer SHISubscriberRelationtoPatientBuffer = new StringBuffer();
            final StringBuffer SHISecondaryNameBuffer = new StringBuffer();
            final StringBuffer HISubscriberRelationtoPatientBuffer = new StringBuffer();
            final StringBuffer HIPrimaryInsuranceBuff = new StringBuffer();
            final StringBuffer DoctorsBuffer = new StringBuffer();
            StringBuilder SympChkCOVIDBuffer = new StringBuilder();
            StringBuilder EmpHealthChkBuffer = new StringBuilder();
            StringBuilder PregChkBuffer = new StringBuilder();
            StringBuilder TestForTravelChkBuffer = new StringBuilder();
            String Style = "";
            String IDFront = "";
            String InsuranceFront = "";
            String InsuranceBack = "";
            String facilityName = helper.getFacilityName(request, conn, servletContext, ClientId);
            try {
                Query = "Select dbname from oe.clients where Id = " + ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    Database = rset.getString(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg ** (EditValues Fetch DB Name ^^" + facilityName + " ##MES#001)", servletContext, ex, "PatientReg2", "EditValues", conn);
                Services.DumException("EditValues^^" + facilityName + " ##MES#001", "PatientReg2", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard_copy");
                Parser.SetField("ActionID", "GetInput");
                Parser.SetField("Message", "MES#001");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            try {
                Query = " Select IFNULL(Title,''), IFNULL(FirstName,''), IFNULL(LastName,''), IFNULL(MiddleInitial, ''), " +
                        "IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'),''),  IFNULL(Age,''), IFNULL(Gender, ''), IFNULL(Email,''), " +
                        "IFNULL(SUBSTRING(PhNumber, 1, 3),''),  IFNULL(SUBSTRING(PhNumber, 4, 10),''), IFNULL(Address,''), " +
                        "IFNULL(City,''), IFNULL(State,''), IFNULL(Country,''), IFNULL(ZipCode,''), IFNULL(SSN,''), " +
                        "IFNULL(Occupation,''), IFNULL(Employer,''), IFNULL(EmpContact, ''), IFNULL(PriCarePhy,''), " +
                        "IFNULL(ReasonVisit, ''), IFNULL(MaritalStatus, ''), IFNULL(DoctorsName,''),  " +
                        "IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y'),DATE_FORMAT(CreatedDate,'%m/%d/%Y')), " +
                        "IFNULL(County,''), ID ," +
                        "CASE WHEN IDFront_Status = 0 THEN IDFront ELSE NULL END, \n" +
                        "CASE WHEN InsuranceFront_Status = 0 THEN InsuranceFront ELSE NULL END, \n" +
                        "CASE WHEN InsuranceBack_Status = 0 THEN InsuranceBack ELSE NULL END \n" +
                        " from " + Database + ".PatientReg where MRN = '" + MRN + "' and status = 0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    Title = rset.getString(1);
                    FirstName = rset.getString(2);
                    LastName = rset.getString(3);
                    MiddleInitial = rset.getString(4);
                    DOB = rset.getString(5);
                    Age = rset.getString(6);
                    gender = rset.getString(7);
                    Email = rset.getString(8);
                    AreaCode = rset.getString(9);
                    PhNumber = rset.getString(10);
                    Address = rset.getString(11);
                    City = rset.getString(12);
                    State = rset.getString(13);
                    Country = rset.getString(14);
                    ZipCode = rset.getString(15);
                    SSN = rset.getString(16);
                    Occupation = rset.getString(17);
                    Employer = rset.getString(18);
                    EmpContact = rset.getString(19);
                    PriCarePhy = rset.getString(20);
                    ReasonVisit = rset.getString(21);
                    MaritalStatus = rset.getString(22);
                    DoctorId = rset.getString(23);
                    DOS = rset.getString(24);
                    County = rset.getString(25);
                    PatientRegId = rset.getInt(26);
                    IDFront = rset.getString(27);
                    InsuranceFront = rset.getString(28);
                    InsuranceBack = rset.getString(29);
                }
                rset.close();
                stmt.close();


                if (IDFront == null) {
                    IDFront = "/md/images_/Placeholders/doc-preview.jpg";
                } else {
                    IDFront = "md.RegisteredPatients?ActionID=download_direct&fname=" + IDFront + "&path=/sftpdrive/AdmissionBundlePdf/Attachment/Victoria/Uploads/";
                }

                if (InsuranceFront == null) {
                    InsuranceFront = "/md/images_/Placeholders/doc-preview.jpg";
                } else {
                    InsuranceFront = "md.RegisteredPatients?ActionID=download_direct&fname=" + InsuranceFront + "&path=/sftpdrive/AdmissionBundlePdf/Attachment/Victoria/Uploads/";
                }

                if (InsuranceBack == null) {
                    InsuranceBack = "/md/images_/Placeholders/doc-preview.jpg";
                } else {
                    InsuranceBack = "md.RegisteredPatients?ActionID=download_direct&fname=" + InsuranceBack + "&path=/sftpdrive/AdmissionBundlePdf/Attachment/Victoria/Uploads/";
                }


            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg ** (EditValues Getting Data from PatientReg Table ^^" + facilityName + " ##MES#002)", servletContext, ex, "PatientReg2", "EditValues", conn);
                Services.DumException("EditValues^^" + facilityName + " ##MES#002", "PatientReg2", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard_copy");
                Parser.SetField("ActionID", "GetInput");
                Parser.SetField("Message", "MES#002");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            try {
                Query = " Select IFNULL(Ethnicity,''), IFNULL(EmployementChk,'0'), IFNULL(Employer,''), IFNULL(Occupation, ''), IFNULL(EmpContact,''),  " +
                        " IFNULL(PrimaryCarePhysicianChk,'0'), IFNULL(PriCarePhy, ''), IFNULL(ReasonVisit,''), IFNULL(PriCarePhyAddress,''),  " +
                        " IFNULL(PriCarePhyCity,''),  IFNULL(PriCarePhyState,''), IFNULL(PriCarePhyZipCode,''), IFNULL(PatientMinorChk,'0'), " +
                        " IFNULL(GuarantorChk,'0'), IFNULL(GuarantorEmployer,''),  IFNULL(SUBSTRING(GuarantorEmployerPhNumber, 1, 3),''), " +
                        " IFNULL(GuarantorEmployerAddress,''), IFNULL(GuarantorEmployerCity,''), IFNULL(GuarantorEmployerState,''),  " +
                        " IFNULL(GuarantorEmployerZipCode, ''), IFNULL(WorkersCompPolicyChk,'0'), IFNULL(MotorVehicleAccidentChk, '0'), " +
                        " IFNULL(HealthInsuranceChk, '0'),  IFNULL(GuarantorEmployerPhNumber,''), IFNULL(Race,''),IFNULL(SympChkCOVID,0), " +
                        " IFNULL(DATE_FORMAT(DateSympOnset,'%Y-%m-%d'),''), IFNULL(SympFever,'0'), IFNULL(SympCough,'0'), IFNULL(SympShortBreath,'0'), " +
                        " IFNULL(SympFatigue,'0'), IFNULL(SympMuscBodyAches,'0'), IFNULL(SympHeadache,'0'), IFNULL(SympLossTaste,'0'), " +
                        " IFNULL(SympSoreThroat,'0'), IFNULL(SympCongestionRunNos,'0'), IFNULL(SympNauseaVomit,'0'), IFNULL(SympDiarrhea,'0'), " +
                        " IFNULL(SympPerPainChest,'0'), IFNULL(SympNewConfusion,'0'), IFNULL(SympInabWake,'0'), IFNULL(SympOthers,'0'), " +
                        " IFNULL(SympOthersTxt,'') , IFNULL(EmpHealthChk,'0'),IFNULL(PregChk,'0'),IFNULL(TestForTravel,'0') " +
                        "from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    Ethnicity = rset.getString(1);
                    EmployementChk = rset.getString(2);
                    Employer = rset.getString(3);
                    Occupation = rset.getString(4);
                    EmpContact = rset.getString(5);
                    PrimaryCarePhysicianChk = rset.getString(6);
                    PriCarePhy = rset.getString(7);
                    ReasonVisit = rset.getString(8);
                    PriCarePhyAddress = rset.getString(9);
                    PriCarePhyCity = rset.getString(10);
                    PriCarePhyState = rset.getString(11);
                    PriCarePhyZipCode = rset.getString(12);
                    PatientMinorChk = rset.getString(13);
                    GuarantorChk = rset.getString(14);
                    GuarantorEmployer = rset.getString(15);
                    GuarantorEmployerAreaCode = rset.getString(16);
                    GuarantorEmployerAddress = rset.getString(17);
                    GuarantorEmployerCity = rset.getString(18);
                    GuarantorEmployerState = rset.getString(19);
                    GuarantorEmployerZipCode = rset.getString(20);
                    WorkersCompPolicyChk = rset.getString(21);
                    MotorVehicleAccidentChk = rset.getString(22);
                    HealthInsuranceChk = rset.getString(23);
                    GuarantorEmployerPhNumber = rset.getString(24);
                    Race = rset.getString(25);
                    SympChkCOVID = rset.getString(26);
                    DateSympOnset = rset.getString(27);
                    SympFever = rset.getString(28);
                    SympCough = rset.getString(29);
                    SympShortBreath = rset.getString(30);
                    SympFatigue = rset.getString(31);
                    SympMuscBodyAches = rset.getString(32);
                    SympHeadache = rset.getString(33);
                    SympLossTaste = rset.getString(34);
                    SympSoreThroat = rset.getString(35);
                    SympCongestionRunNos = rset.getString(36);
                    SympNauseaVomit = rset.getString(37);
                    SympDiarrhea = rset.getString(38);
                    SympPerPainChest = rset.getString(39);
                    SympNewConfusion = rset.getString(40);
                    SympInabWake = rset.getString(41);
                    SympOthers = rset.getString(42);
                    SympOthersTxt = rset.getString(43);
                    EmpHealthChk = rset.getString(44);
                    PregChk = rset.getString(45);
                    TestForTravel = rset.getString(46);
                }
                rset.close();
                stmt.close();
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg ** (EditValues Getting Data from PatientReg_Details Table ^^" + facilityName + " ##MES#003)", servletContext, ex, "PatientReg2", "EditValues", conn);
                Services.DumException("EditValues^^" + facilityName + " ##MES#003", "PatientReg2", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard_copy");
                Parser.SetField("ActionID", "GetInput");
                Parser.SetField("Message", "MES#003");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            try {
                if (WorkersCompPolicyChk.equals("1")) {
                    Query = " Select IFNULL(DATE_FORMAT(WCPDateofInjury,'%Y-%m-%d'),''), IFNULL(WCPCaseNo,''), " +
                            "IFNULL(WCPGroupNo,''), IFNULL(WCPMemberId,''),  IFNULL(WCPInjuryRelatedAutoMotorAccident,''), " +
                            "IFNULL(WCPInjuryRelatedWorkRelated,''), IFNULL(WCPInjuryRelatedOtherAccident,''),  " +
                            "IFNULL(WCPInjuryRelatedNoAccident,''), IFNULL(WCPInjuryOccurVehicle,''), IFNULL(WCPInjuryOccurWork,''), " +
                            "IFNULL(WCPInjuryOccurHome,''),  IFNULL(WCPInjuryOccurOther,''), IFNULL(WCPInjuryDescription,''), " +
                            "IFNULL(WCPHRFirstName,''), IFNULL(WCPHRLastName,''),  IFNULL(SUBSTRING(WCPHRPhoneNumber, 1, 3),''), " +
                            "IFNULL(SUBSTRING(WCPHRPhoneNumber, 4, 10),''), IFNULL(WCPHRAddress,''), IFNULL(WCPHRCity,''),  " +
                            "IFNULL(WCPHRState,''), IFNULL(WCPHRZipCode,''), IFNULL(WCPPlanName,''), IFNULL(WCPCarrierName,''),  " +
                            "IFNULL(SUBSTRING(WCPPayerPhoneNumber, 1, 3),''), IFNULL(SUBSTRING(WCPPayerPhoneNumber, 4, 10),''), " +
                            "IFNULL(WCPCarrierAddress,''),  IFNULL(WCPCarrierCity,''), IFNULL(WCPCarrierState,''), " +
                            "IFNULL(WCPCarrierZipCode,''), IFNULL(WCPAdjudicatorFirstName,''),  IFNULL(WCPAdjudicatorLastName,''), " +
                            "IFNULL(SUBSTRING(WCPAdjudicatorPhoneNumber, 1, 3),''), IFNULL(SUBSTRING(WCPAdjudicatorPhoneNumber, 4, 10),''),  " +
                            "IFNULL(SUBSTRING(WCPAdjudicatorFaxPhoneNumber, 1, 3),''), " +
                            "IFNULL(SUBSTRING(WCPAdjudicatorFaxPhoneNumber, 4, 10),'')  " +
                            "from " + Database + ".Patient_WorkCompPolicy where PatientRegId = " + PatientRegId + " ";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        WCPDateofInjury = rset.getString(1);
                        WCPCaseNo = rset.getString(2);
                        WCPGroupNo = rset.getString(3);
                        WCPMemberId = rset.getString(4);
                        WCPInjuryRelatedAutoMotorAccident = rset.getString(5);
                        WCPInjuryRelatedWorkRelated = rset.getString(6);
                        WCPInjuryRelatedOtherAccident = rset.getString(7);
                        WCPInjuryRelatedNoAccident = rset.getString(8);
                        WCPInjuryOccurVehicle = rset.getString(9);
                        WCPInjuryOccurWork = rset.getString(10);
                        WCPInjuryOccurHome = rset.getString(11);
                        WCPInjuryOccurOther = rset.getString(12);
                        WCPInjuryDescription = rset.getString(13);
                        WCPHRFirstName = rset.getString(14);
                        WCPHRLastName = rset.getString(15);
                        WCPHRAreaCode = rset.getString(16);
                        WCPHRPhoneNumber = rset.getString(17);
                        WCPHRAddress = rset.getString(18);
                        WCPHRCity = rset.getString(19);
                        WCPHRState = rset.getString(20);
                        WCPHRZipCode = rset.getString(21);
                        WCPPlanName = rset.getString(22);
                        WCPCarrierName = rset.getString(23);
                        WCPPayerAreaCode = rset.getString(24);
                        WCPPayerPhoneNumber = rset.getString(25);
                        WCPCarrierAddress = rset.getString(26);
                        WCPCarrierCity = rset.getString(27);
                        WCPCarrierState = rset.getString(28);
                        WCPCarrierZipCode = rset.getString(29);
                        WCPAdjudicatorFirstName = rset.getString(30);
                        WCPAdjudicatorLastName = rset.getString(31);
                        WCPAdjudicatorAreaCode = rset.getString(32);
                        WCPAdjudicatorPhoneNumber = rset.getString(33);
                        WCPAdjudicatorFaxAreaCode = rset.getString(34);
                        WCPAdjudicatorFaxPhoneNumber = rset.getString(35);
                    }
                    rset.close();
                    stmt.close();
                }
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg ** (EditValues Getting Data from Patient_WorkCompPolicy Table ^^" + facilityName + " ##MES#004)", servletContext, ex, "PatientReg2", "EditValues", conn);
                Services.DumException("EditValues^^" + facilityName + " ##MES#004", "PatientReg2", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard_copy");
                Parser.SetField("ActionID", "GetInput");
                Parser.SetField("Message", "MES#003");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            try {
                if (MotorVehicleAccidentChk.equals("1")) {
                    Query = " SELECT IFNULL(AutoInsuranceInformationChk,''), IFNULL(DATE_FORMAT(AIIDateofAccident,'%Y-%m-%d'), ''), " +
                            "IFNULL(AIIAutoClaim,''),  IFNULL(AIIAccidentLocationAddress, ''), IFNULL(AIIAccidentLocationCity, ''), " +
                            "IFNULL(AIIAccidentLocationState,''), IFNULL(AIIAccidentLocationZipCode,''),  IFNULL(AIIRoleInAccident, ''), " +
                            "IFNULL(AIITypeOfAutoIOnsurancePolicy,''), IFNULL(AIIPrefixforReponsibleParty, ''), " +
                            "IFNULL(AIIFirstNameforReponsibleParty, ''),  IFNULL(AIIMiddleNameforReponsibleParty, ''), " +
                            "IFNULL(AIILastNameforReponsibleParty, ''), IFNULL(AIISuffixforReponsibleParty,''), " +
                            "IFNULL(AIICarrierResponsibleParty,''),  IFNULL(AIICarrierResponsiblePartyAddress,''), " +
                            "IFNULL(AIICarrierResponsiblePartyCity,''), IFNULL(AIICarrierResponsiblePartyState,''), " +
                            "IFNULL(AIICarrierResponsiblePartyZipCode,''),  IFNULL(SUBSTRING(AIICarrierResponsiblePartyPhoneNumber, 1, 3),'')," +
                            "IFNULL(SUBSTRING(AIICarrierResponsiblePartyPhoneNumber, 4, 10),''), " +
                            "IFNULL(AIICarrierResponsiblePartyPolicyNumber,''),  IFNULL(AIIResponsiblePartyAutoMakeModel, ''), " +
                            "IFNULL(AIIResponsiblePartyLicensePlate,''), IFNULL(AIIFirstNameOfYourPolicyHolder,''), " +
                            "IFNULL(AIILastNameOfYourPolicyHolder, ''),  IFNULL(AIINameAutoInsuranceOfYourVehicle,''), " +
                            "IFNULL(AIIYourInsuranceAddress, ''), IFNULL(AIIYourInsuranceCity,''), IFNULL(AIIYourInsuranceState,''),  " +
                            "IFNULL(AIIYourInsuranceZipCode, ''), IFNULL(SUBSTRING(AIIYourInsurancePhoneNumber, 1, 3),''), " +
                            "IFNULL(SUBSTRING(AIIYourInsurancePhoneNumber, 4, 10),''),  IFNULL(AIIYourInsurancePolicyNo,''), " +
                            "IFNULL(AIIYourLicensePlate,''), IFNULL(AIIYourCarMakeModelYear,'')  " +
                            "from " + Database + ".Patient_AutoInsuranceInfo where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        AutoInsuranceInformationChk = rset.getString(1);
                        AIIDateofAccident = rset.getString(2);
                        AIIAutoClaim = rset.getString(3);
                        AIIAccidentLocationAddress = rset.getString(4);
                        AIIAccidentLocationCity = rset.getString(5);
                        AIIAccidentLocationState = rset.getString(6);
                        AIIAccidentLocationZipCode = rset.getString(7);
                        AIIRoleInAccident = rset.getString(8);
                        AIITypeOfAutoIOnsurancePolicy = rset.getString(9);
                        AIIPrefixforReponsibleParty = rset.getString(10);
                        AIIFirstNameforReponsibleParty = rset.getString(11);
                        AIIMiddleNameforReponsibleParty = rset.getString(12);
                        AIILastNameforReponsibleParty = rset.getString(13);
                        AIISuffixforReponsibleParty = rset.getString(14);
                        AIICarrierResponsibleParty = rset.getString(15);
                        AIICarrierResponsiblePartyAddress = rset.getString(16);
                        AIICarrierResponsiblePartyCity = rset.getString(17);
                        AIICarrierResponsiblePartyState = rset.getString(18);
                        AIICarrierResponsiblePartyZipCode = rset.getString(19);
                        AIICarrierResponsiblePartyAreaCode = rset.getString(20);
                        AIICarrierResponsiblePartyPhoneNumber = rset.getString(21);
                        AIICarrierResponsiblePartyPolicyNumber = rset.getString(22);
                        AIIResponsiblePartyAutoMakeModel = rset.getString(23);
                        AIIResponsiblePartyLicensePlate = rset.getString(24);
                        AIIFirstNameOfYourPolicyHolder = rset.getString(25);
                        AIILastNameOfYourPolicyHolder = rset.getString(26);
                        AIINameAutoInsuranceOfYourVehicle = rset.getString(27);
                        AIIYourInsuranceAddress = rset.getString(28);
                        AIIYourInsuranceCity = rset.getString(29);
                        AIIYourInsuranceState = rset.getString(30);
                        AIIYourInsuranceZipCode = rset.getString(31);
                        AIIYourInsuranceAreaCode = rset.getString(32);
                        AIIYourInsurancePhoneNumber = rset.getString(33);
                        AIIYourInsurancePolicyNo = rset.getString(34);
                        AIIYourLicensePlate = rset.getString(35);
                        AIIYourCarMakeModelYear = rset.getString(36);
                    }
                    rset.close();
                    stmt.close();
                }
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg ** (EditValues Getting Data from Patient_AutoInsuranceInfo Table ^^" + facilityName + " ##MES#005)", servletContext, ex, "PatientReg2", "EditValues", conn);
                Services.DumException("EditValues^^" + facilityName + " ##MES#005", "PatientReg2", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard_copy");
                Parser.SetField("ActionID", "GetInput");
                Parser.SetField("Message", "MES#005");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            try {
                if (HealthInsuranceChk.equals("1")) {
                    Query = "Select IFNULL(GovtFundedInsurancePlanChk,0), IFNULL(GFIPMedicare,0), " +
                            "IFNULL(GFIPMedicaid,0), IFNULL(GFIPCHIP,0), IFNULL(GFIPTricare,0), IFNULL(GFIPVHA,0), " +
                            "IFNULL(GFIPIndianHealth,0), IFNULL(InsuranceSubPatient,''), " +
                            "IFNULL(InsuranceSubGuarantor,''), IFNULL(InsuranceSubOther,''), " +
                            "IFNULL(HIPrimaryInsurance, ''), IFNULL(HISubscriberFirstName,''), " +
                            "IFNULL(HISubscriberLastName,''), IFNULL(DATE_FORMAT(HISubscriberDOB,'%Y-%m-%d'),''), " +
                            "IFNULL(HISubscriberSSN,''), IFNULL(HISubscriberRelationtoPatient,''), " +
                            "IFNULL(HISubscriberGroupNo,''), IFNULL(HISubscriberPolicyNo,''), " +
                            "IFNULL(SecondHealthInsuranceChk,'0'), IFNULL(SHISecondaryName,''), " +
                            "IFNULL(SHISubscriberFirstName,''), IFNULL(SHISubscriberLastName,''), " +
                            "IFNULL(SHISubscriberRelationtoPatient,''), IFNULL(SHISubscriberGroupNo,''), " +
                            "IFNULL(SHISubscriberPolicyNo,'')  " +
                            "from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId =  " + PatientRegId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        GovtFundedInsurancePlanChk = rset.getString(1);
                        GFIPMedicare = rset.getString(2);
                        GFIPMedicaid = rset.getString(3);
                        GFIPCHIP = rset.getString(4);
                        GFIPTricare = rset.getString(5);
                        GFIPVHA = rset.getString(6);
                        GFIPIndianHealth = rset.getString(7);
                        InsuranceSubPatient = rset.getString(8);
                        InsuranceSubGuarantor = rset.getString(9);
                        InsuranceSubOther = rset.getString(10);
                        HIPrimaryInsurance = rset.getString(11);
                        HISubscriberFirstName = rset.getString(12);
                        HISubscriberLastName = rset.getString(13);
                        HISubscriberDOB = rset.getString(14);
                        HISubscriberSSN = rset.getString(15);
                        HISubscriberRelationtoPatient = rset.getString(16);
                        HISubscriberGroupNo = rset.getString(17);
                        HISubscriberPolicyNo = rset.getString(18);
                        SecondHealthInsuranceChk = rset.getString(19);
                        SHISecondaryName = rset.getString(20);
                        SHISubscriberFirstName = rset.getString(21);
                        SHISubscriberLastName = rset.getString(22);
                        SHISubscriberRelationtoPatient = rset.getString(23);
                        SHISubscriberGroupNo = rset.getString(24);
                        SHISubscriberPolicyNo = rset.getString(25);
                    }
                    rset.close();
                    stmt.close();
                }
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg ** (EditValues Getting Data from Patient_HealthInsuranceInfo Table ^^" + facilityName + " ##MES#006)", servletContext, ex, "PatientReg2", "EditValues", conn);
                Services.DumException("EditValues^^" + facilityName + " ##MES#006", "PatientReg2", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard_copy");
                Parser.SetField("ActionID", "GetInput");
                Parser.SetField("Message", "MES#006");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }

            try {
                Query = " Select IFNULL(MFFirstVisit,''),IFNULL(MFReturnPat,''),IFNULL(MFInternetFind,''),IFNULL(Facebook,''),IFNULL(MapSearch,'')," +
                        " IFNULL(GoogleSearch,''),IFNULL(VERWebsite,''),IFNULL(WebsiteAds,''),IFNULL(OnlineReviews,'')," +
                        " IFNULL(Twitter,''),IFNULL(LinkedIn,''),IFNULL(EmailBlast,''),IFNULL(YouTube,''),IFNULL(TV,''),IFNULL(Billboard,''),IFNULL(Radio,'')," +
                        " IFNULL(Brochure,''),IFNULL(DirectMail,''),IFNULL(CitizensDeTar,''),IFNULL(LiveWorkNearby,''),IFNULL(FamilyFriend,'')," +
                        " IFNULL(FamilyFriend_text,''),IFNULL(UrgentCare,''),IFNULL(UrgentCare_text,''),IFNULL(NewspaperMagazine,'')," +
                        " IFNULL(NewspaperMagazine_text,''),IFNULL(School,''),IFNULL(School_text,''),IFNULL(Hotel,''),IFNULL(Hotel_text,'')," +
                        " IFNULL(MFPhysician,''),IFNULL(OnlineAdvertisements,''),IFNULL(EmployerSentMe,''),IFNULL(EmployerSentMe_text,'')," +
                        " IFNULL(MFPhysicianRefChk,''),IFNULL(PatientCell,''),IFNULL(RecInitial,'') " +
                        " from " + Database + ".MarketingInfo where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    MFFirstVisit = rset.getString(1);
                    MFReturnPat = rset.getString(2);
                    MFInternetFind = rset.getString(3);
                    Facebook = rset.getString(4);
                    MapSearch = rset.getString(5);
                    GoogleSearch = rset.getString(6);
                    VERWebsite = rset.getString(7);
                    WebsiteAds = rset.getString(8);
                    OnlineReviews = rset.getString(9);
                    Twitter = rset.getString(10);
                    LinkedIn = rset.getString(11);
                    EmailBlast = rset.getString(12);
                    YouTube = rset.getString(13);
                    TV = rset.getString(14);
                    Billboard = rset.getString(15);
                    Radio = rset.getString(16);
                    Brochure = rset.getString(17);
                    DirectMail = rset.getString(18);
                    CitizensDeTar = rset.getString(19);
                    LiveWorkNearby = rset.getString(20);
                    FamilyFriend = rset.getString(21);
                    FamilyFriend_text = rset.getString(22);
                    UrgentCare = rset.getString(23);
                    UrgentCare_text = rset.getString(24);
                    NewspaperMagazine = rset.getString(25);
                    NewspaperMagazine_text = rset.getString(26);
                    School = rset.getString(27);
                    School_text = rset.getString(28);
                    Hotel = rset.getString(29);
                    Hotel_text = rset.getString(30);
                    MFPhysician = rset.getString(31);
                    OnlineAdvertisements = rset.getString(32);
                    EmployerSentMe = rset.getString(33);
                    EmployerSentMe_text = rset.getString(34);
                    MFPhysicianRefChk = rset.getString(35);
                    PatientCell = rset.getString(36);
                    RecInitial = rset.getString(37);
                }
                rset.close();
                stmt.close();
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg ** (EditValues Getting Data from PatientReg_Details Table ^^" + facilityName + " ##MES#007)", servletContext, ex, "PatientReg2", "EditValues", conn);
                Services.DumException("EditValues^^" + facilityName + " ##MES#007", "PatientReg2", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard_copy");
                Parser.SetField("ActionID", "GetInput");
                Parser.SetField("Message", "MES#007");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

            }

            if (HISubscriberDOB.equals("0000-00-00")) {
                HISubscriberDOB = "";
            }
            if (WCPDateofInjury.equals("00/00/0000")) {
                WCPDateofInjury = "";
            }
            if (AIIDateofAccident.equals("00/00/0000")) {
                AIIDateofAccident = "";
            }
            if (Title.toUpperCase().equals("MR")) {
                TitleBuffer.append("<select class=\"form-control\" id=\"Title\" name=\"Title\" style=\"color:black;\" required onchange=\"disableGenderOption(this.value);\" >\n<option value=\"\"  disabled >Select Title</option>\n<option value=\"Mr\" selected >Mr.</option>\n<option value=\"Miss\">Miss</option>\n<option value=\"Mrs\">Mrs</option>\n<option value=\"Ms\">Ms</option>\n</select>");
            } else if (Title.toUpperCase().equals("MISS")) {
                TitleBuffer.append("<select class=\"form-control\" id=\"Title\" name=\"Title\" style=\"color:black;\" required onchange=\"disableGenderOption(this.value);\">\n<option value=\"\"  disabled >Select Title</option>\n<option value=\"Mr\">Mr.</option>\n<option value=\"Miss\" selected >Miss</option>\n<option value=\"Mrs\">Mrs</option>\n<option value=\"Ms\">Ms</option>\n</select>");
            } else if (Title.toUpperCase().equals("MRS")) {
                TitleBuffer.append("<select class=\"form-control\" id=\"Title\" name=\"Title\" style=\"color:black;\" required onchange=\"disableGenderOption(this.value);\" >\n<option value=\"\" disabled >Select Title</option>\n<option value=\"Mr\">Mr.</option>\n<option value=\"Miss\">Miss</option>\n<option value=\"Mrs\" selected >Mrs</option>\n<option value=\"Ms\">Ms</option>\n</select>");
            } else if (Title.toUpperCase().equals("MS")) {
                TitleBuffer.append("<select class=\"form-control\" id=\"Title\" name=\"Title\" style=\"color:black;\" required onchange=\"disableGenderOption(this.value);\" >\n<option value=\"\"  disabled >Select Title</option>\n<option value=\"Mr\">Mr.</option>\n<option value=\"Miss\">Miss</option>\n<option value=\"Mrs\">Mrs</option>\n<option value=\"Ms\" selected >Ms</option>\n</select>");
            } else {
                TitleBuffer.append("<select class=\"form-control\" id=\"Title\" name=\"Title\" style=\"color:black;\" required onchange=\"disableGenderOption(this.value);\" >\n<option value=\"\"  disabled >Select Title</option>\n<option value=\"Mr\">Mr.</option>\n<option value=\"Miss\">Miss</option>\n<option value=\"Mrs\">Mrs</option>\n<option value=\"Ms\">Ms</option>\n</select>");
            }
            if (gender.toUpperCase().equals("MALE")) {
                GenderBuffer.append("<select class=\"form-control\" id=\"gender\" name=\"gender\" required style=\"color:black;\" required >\n<option value=\"\"  disabled >Select Gender</option>\n<option value=\"male\" selected>Male</option>\n<option value=\"female\"  disabled >Female</option>\n</select>");
            } else if (gender.toUpperCase().equals("FEMALE")) {
                GenderBuffer.append("<select class=\"form-control\" id=\"gender\" name=\"gender\" required style=\"color:black;\" required >\n<option value=\"\"  disabled >Select Gender</option>\n<option value=\"male\"  disabled >Male</option>\n<option value=\"female\" selected>Female</option>\n</select>");
            } else {
                GenderBuffer.append("<select class=\"form-control\" id=\"gender\" name=\"gender\" required style=\"color:black;\" required >\n<option value=\"\"  disabled >Select Gender</option>\n<option value=\"male\" >Male</option>\n<option value=\"female\" >Female</option>\n</select>");
            }
            if (MaritalStatus.toUpperCase().equals("SINGLE")) {
                MaritalStatusBuffer.append("<select class=\"form-control\" id=\"MaritalStatus\" name=\"MaritalStatus\" style=\"color:black;\">\n<option value=\"Single\" selected>Single</option>\n<option value=\"Mar\">Mar</option>\n<option value=\"Div\">Div</option>\n<option value=\"Sep\">Sep</option>\n<option value=\"Wid\">Wid</option>\n</select>");
            } else if (MaritalStatus.toUpperCase().equals("MAR")) {
                MaritalStatusBuffer.append("<select class=\"form-control\" id=\"MaritalStatus\" name=\"MaritalStatus\" style=\"color:black;\">\n<option value=\"Single\">Single</option>\n<option value=\"Mar\" selected>Mar</option>\n<option value=\"Div\">Div</option>\n<option value=\"Sep\">Sep</option>\n<option value=\"Wid\">Wid</option>\n</select>");
            } else if (MaritalStatus.toUpperCase().equals("DIV")) {
                MaritalStatusBuffer.append("<select class=\"form-control\" id=\"MaritalStatus\" name=\"MaritalStatus\" style=\"color:black;\">\n<option value=\"Single\">Single</option>\n<option value=\"Mar\">Mar</option>\n<option value=\"Div\" selected>Div</option>\n<option value=\"Sep\">Sep</option>\n<option value=\"Wid\">Wid</option>\n</select>");
            } else if (MaritalStatus.toUpperCase().equals("SEP")) {
                MaritalStatusBuffer.append("<select class=\"form-control\" id=\"MaritalStatus\" name=\"MaritalStatus\" style=\"color:black;\">\n<option value=\"Single\">Single</option>\n<option value=\"Mar\">Mar</option>\n<option value=\"Div\" >Div</option>\n<option value=\"Sep\" selected>Sep</option>\n<option value=\"Wid\">Wid</option>\n</select>");
            } else if (MaritalStatus.toUpperCase().equals("WID")) {
                MaritalStatusBuffer.append("<select class=\"form-control\" id=\"MaritalStatus\" name=\"MaritalStatus\" style=\"color:black;\">\n<option value=\"Single\">Single</option>\n<option value=\"Mar\">Mar</option>\n<option value=\"Div\" >Div</option>\n<option value=\"Sep\" >Sep</option>\n<option value=\"Wid\" selected>Wid</option>\n</select>");
            }
            if (Ethnicity.equals("1")) {
                EthnicityBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_hispanic\" value=\"1\" checked required>\n<label for=\"Ethnicity_hispanic\">Hispanic</label>\n</fieldset>\n<fieldset>\n<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_nonHispanic\" value=\"2\">\n<label for=\"Ethnicity_nonHispanic\">Non Hispanic</label>\n</fieldset>\n<fieldset>\n<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_unknown\" value=\"3\">\n<label for=\"Ethnicity_unknown\">Unknown</label>\n</fieldset>\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n</div>\n");
            } else if (Ethnicity.equals("2")) {
                EthnicityBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_hispanic\" value=\"1\" required>\n<label for=\"Ethnicity_hispanic\">Hispanic</label>\n</fieldset>\n<fieldset>\n<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_nonHispanic\" value=\"2\" checked>\n<label for=\"Ethnicity_nonHispanic\">Non Hispanic</label>\n</fieldset>\n<fieldset>\n<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_unknown\" value=\"3\">\n<label for=\"Ethnicity_unknown\">Unknown</label>\n</fieldset>\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n</div>\n");
            } else if (Ethnicity.equals("3")) {
                EthnicityBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_hispanic\" value=\"1\" required>\n<label for=\"Ethnicity_hispanic\">Hispanic</label>\n</fieldset>\n<fieldset>\n<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_nonHispanic\" value=\"2\">\n<label for=\"Ethnicity_nonHispanic\">Non Hispanic</label>\n</fieldset>\n<fieldset>\n<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_unknown\" value=\"3\" checked>\n<label for=\"Ethnicity_unknown\">Unknown</label>\n</fieldset>\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n</div>\n");
            } else {
                EthnicityBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_hispanic\" value=\"1\" required>\n<label for=\"Ethnicity_hispanic\">Hispanic</label>\n</fieldset>\n<fieldset>\n<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_nonHispanic\" value=\"2\">\n<label for=\"Ethnicity_nonHispanic\">Non Hispanic</label>\n</fieldset>\n<fieldset>\n<input name=\"Ethnicity\" type=\"radio\" id=\"Ethnicity_unknown\" value=\"3\">\n<label for=\"Ethnicity_unknown\">Unknown</label>\n</fieldset>\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n</div>\n");
            }
            if (Race.equals("1")) {
                RaceBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_asian\" value=\"1\" checked required>\n<label for=\"Race_asian\">Asian</label>\n</fieldset>\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_black\" value=\"2\">\n<label for=\"Race_black\">Black</label>\n</fieldset>\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_white\" value=\"3\">\n<label for=\"Race_white\">White</label>\n</fieldset>\t\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_other\" value=\"4\">\n<label for=\"Race_other\">Other</label>\n</fieldset>\t\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_unknown\" value=\"5\">\n<label for=\"Race_unknown\">Unknown</label>\n</fieldset>\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n</div>\n");
            } else if (Race.equals("2")) {
                RaceBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_asian\" value=\"1\" required>\n<label for=\"Race_asian\">Asian</label>\n</fieldset>\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_black\" value=\"2\" checked>\n<label for=\"Race_black\">Black</label>\n</fieldset>\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_white\" value=\"3\">\n<label for=\"Race_white\">White</label>\n</fieldset>\t\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_other\" value=\"4\">\n<label for=\"Race_other\">Other</label>\n</fieldset>\t\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_unknown\" value=\"5\">\n<label for=\"Race_unknown\">Unknown</label>\n</fieldset>\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n</div>\n");
            } else if (Race.equals("3")) {
                RaceBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_asian\" value=\"1\" required>\n<label for=\"Race_asian\">Asian</label>\n</fieldset>\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_black\" value=\"2\">\n<label for=\"Race_black\">Black</label>\n</fieldset>\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_white\" value=\"3\" checked>\n<label for=\"Race_white\">White</label>\n</fieldset>\t\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_other\" value=\"4\">\n<label for=\"Race_other\">Other</label>\n</fieldset>\t\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_unknown\" value=\"5\">\n<label for=\"Race_unknown\">Unknown</label>\n</fieldset>\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n</div>\n");
            } else if (Race.equals("4")) {
                RaceBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_asian\" value=\"1\" required>\n<label for=\"Race_asian\">Asian</label>\n</fieldset>\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_black\" value=\"2\">\n<label for=\"Race_black\">Black</label>\n</fieldset>\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_white\" value=\"3\">\n<label for=\"Race_white\">White</label>\n</fieldset>\t\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_other\" value=\"4\" checked>\n<label for=\"Race_other\">Other</label>\n</fieldset>\t\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_unknown\" value=\"5\">\n<label for=\"Race_unknown\">Unknown</label>\n</fieldset>\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n</div>\n");
            } else if (Race.equals("5")) {
                RaceBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_asian\" value=\"1\" required>\n<label for=\"Race_asian\">Asian</label>\n</fieldset>\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_black\" value=\"2\">\n<label for=\"Race_black\">Black</label>\n</fieldset>\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_white\" value=\"3\">\n<label for=\"Race_white\">White</label>\n</fieldset>\t\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_other\" value=\"4\">\n<label for=\"Race_other\">Other</label>\n</fieldset>\t\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_unknown\" value=\"5\" checked>\n<label for=\"Race_unknown\">Unknown</label>\n</fieldset>\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n</div>\n");
            } else {
                RaceBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_asian\" value=\"1\" required>\n<label for=\"Race_asian\">Asian</label>\n</fieldset>\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_black\" value=\"2\">\n<label for=\"Race_black\">Black</label>\n</fieldset>\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_white\" value=\"3\">\n<label for=\"Race_white\">White</label>\n</fieldset>\t\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_other\" value=\"4\">\n<label for=\"Race_other\">Other</label>\n</fieldset>\t\n<fieldset>\n<input name=\"Race\" type=\"radio\" id=\"Race_unknown\" value=\"5\">\n<label for=\"Race_unknown\">Unknown</label>\n</fieldset>\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n</div>\n");
            }
            if (!ReasonVisit.equals("COVID Testing")) {
                ReasonVisitBuffer.append("<select class=\"form-control\" id=\"ReasonVisitSelect\" name=\"ReasonVisitSelect\" style=\"color:black;\" onchange=\"DisplayReasonVisitField(this.value);\" required>\n<option value=\"\">Select Reason of Visit</option>\n<option value=\"COVID\" >COVID Testing</option>\n<option value=\"Others\" selected>Others</option>\n</select>");
                Style = " #CovidReasonDiv { display: none; }";
            } else {
                ReasonVisitBuffer.append("<select class=\"form-control\" id=\"ReasonVisitSelect\" name=\"ReasonVisitSelect\" style=\"color:black;\" onchange=\"DisplayReasonVisitField(this.value);\" required>\n<option value=\"\">Select Reason of Visit</option>\n<option value=\"COVID\" selected>COVID Testing</option>\n<option value=\"Others\" >Others</option>\n</select>");
                Style = " #CovidReasonDiv { display: block; }";
            }

            if (SympChkCOVID.equals("0")) {
                SympChkCOVIDBuffer.append("<div class=\"controls\">\n" +
                        "<fieldset>\n" +
                        "<input name=\"SympChkCOVID\" type=\"radio\" id=\"SympChkCOVID_Yes\" value=\"1\"  onclick=\"NoSymptoms(this.value);\">\n" +
                        "<label for=\"SympChkCOVID_Yes\">Yes</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"SympChkCOVID\" type=\"radio\" id=\"SympChkCOVID_No\" value=\"0\" checked onclick=\"NoSymptoms(this.value);\">\n" +
                        "<label for=\"SympChkCOVID_No\">No</label>\n" +
                        "</fieldset>\n" +
                        "</div>");

            } else if (SympChkCOVID.equals("1")) {
                SympChkCOVIDBuffer.append("<div class=\"controls\">\n" +
                        "<fieldset>\n" +
                        "<input name=\"SympChkCOVID\" type=\"radio\" id=\"SympChkCOVID_Yes\" value=\"1\" checked onclick=\"NoSymptoms(this.value);\">\n" +
                        "<label for=\"SympChkCOVID_Yes\">Yes</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"SympChkCOVID\" type=\"radio\" id=\"SympChkCOVID_No\" value=\"0\" onclick=\"NoSymptoms(this.value);\">\n" +
                        "<label for=\"SympChkCOVID_No\">No</label>\n" +
                        "</fieldset>\n" +
                        "</div>");

            } else {
                SympChkCOVIDBuffer.append("<div class=\"controls\">\n" +
                        "<fieldset>\n" +
                        "<input name=\"SympChkCOVID\" type=\"radio\" id=\"SympChkCOVID_Yes\" value=\"1\" >\n" +
                        "<label for=\"SympChkCOVID_Yes\">Yes</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"SympChkCOVID\" type=\"radio\" id=\"SympChkCOVID_No\" value=\"0\" >\n" +
                        "<label for=\"SympChkCOVID_No\">No</label>\n" +
                        "</fieldset>\n" +
                        "</div>");
            }

            if (SympFever.equals("1")) {
                SympFever = "<input type=\"checkbox\" id=\"SympFever\" name=\"SympFever\" checked />";
            } else {
                SympFever = "<input type=\"checkbox\" id=\"SympFever\" name=\"SympFever\" />";
            }
            if (SympCough.equals("1")) {
                SympCough = "<input type=\"checkbox\" id=\"SympCough\" name=\"SympCough\" checked />";
            } else {
                SympCough = "<input type=\"checkbox\" id=\"SympCough\" name=\"SympCough\" />";
            }
            if (SympShortBreath.equals("1")) {
                SympShortBreath = "<input type=\"checkbox\" id=\"SympShortBreath\" name=\"SympShortBreath\" checked/>";
            } else {
                SympShortBreath = "<input type=\"checkbox\" id=\"SympShortBreath\" name=\"SympShortBreath\" />";
            }
            if (SympFatigue.equals("1")) {
                SympFatigue = "<input type=\"checkbox\" id=\"SympFatigue\" name=\"SympFatigue\" checked/>";
            } else {
                SympFatigue = "<input type=\"checkbox\" id=\"SympFatigue\" name=\"SympFatigue\" />";
            }
            if (SympMuscBodyAches.equals("1")) {
                SympMuscBodyAches = "<input type=\"checkbox\" id=\"SympMuscBodyAches\" name=\"SympMuscBodyAches\" checked/>";
            } else {
                SympMuscBodyAches = "<input type=\"checkbox\" id=\"SympMuscBodyAches\" name=\"SympMuscBodyAches\" />";
            }
            if (SympHeadache.equals("1")) {
                SympHeadache = "<input type=\"checkbox\" id=\"SympHeadache\" name=\"SympHeadache\" checked/>";
            } else {
                SympHeadache = "<input type=\"checkbox\" id=\"SympHeadache\" name=\"SympHeadache\" />";
            }
            if (SympLossTaste.equals("1")) {
                SympLossTaste = "<input type=\"checkbox\" id=\"SympLossTaste\" name=\"SympLossTaste\" checked/>";
            } else {
                SympLossTaste = "<input type=\"checkbox\" id=\"SympLossTaste\" name=\"SympLossTaste\" />";
            }
            if (SympSoreThroat.equals("1")) {
                SympSoreThroat = "<input type=\"checkbox\" id=\"SympSoreThroat\" name=\"SympSoreThroat\" checked/>";
            } else {
                SympSoreThroat = "<input type=\"checkbox\" id=\"SympSoreThroat\" name=\"SympSoreThroat\" />";
            }
            if (SympCongestionRunNos.equals("1")) {
                SympCongestionRunNos = "<input type=\"checkbox\" id=\"SympCongestionRunNos\" name=\"SympCongestionRunNos\" checked/>";
            } else {
                SympCongestionRunNos = "<input type=\"checkbox\" id=\"SympCongestionRunNos\" name=\"SympCongestionRunNos\" />";
            }
            if (SympNauseaVomit.equals("1")) {
                SympNauseaVomit = "<input type=\"checkbox\" id=\"SympNauseaVomit\" name=\"SympNauseaVomit\" checked/>";
            } else {
                SympNauseaVomit = "<input type=\"checkbox\" id=\"SympNauseaVomit\" name=\"SympNauseaVomit\" />";
            }
            if (SympDiarrhea.equals("1")) {
                SympDiarrhea = "<input type=\"checkbox\" id=\"SympDiarrhea\" name=\"SympDiarrhea\" checked/>";
            } else {
                SympDiarrhea = "<input type=\"checkbox\" id=\"SympDiarrhea\" name=\"SympDiarrhea\" />";
            }
            if (SympPerPainChest.equals("1")) {
                SympPerPainChest = "<input type=\"checkbox\" id=\"SympPerPainChest\" name=\"SympPerPainChest\" checked/>";
            } else {
                SympPerPainChest = "<input type=\"checkbox\" id=\"SympPerPainChest\" name=\"SympPerPainChest\" />";
            }
            if (SympNewConfusion.equals("1")) {
                SympNewConfusion = "<input type=\"checkbox\" id=\"SympNewConfusion\" name=\"SympNewConfusion\" checked/>";
            } else {
                SympNewConfusion = "<input type=\"checkbox\" id=\"SympNewConfusion\" name=\"SympNewConfusion\"/>";
            }
            if (SympInabWake.equals("1")) {
                SympInabWake = "<input type=\"checkbox\" id=\"SympInabWake\" name=\"SympInabWake\" checkec/>";
            } else {
                SympInabWake = "<input type=\"checkbox\" id=\"SympInabWake\" name=\"SympInabWake\"/>";
            }
            if (SympOthers.equals("1")) {
                SympOthers = "<input type=\"checkbox\" id=\"SympOthers\"  name=\"SympOthers\" onclick=\"OpenOtherTxt();\" checked/>";
                Style = Style + " #SympOtherTxtDiv{display:block; }";
            } else {
                SympOthers = "<input type=\"checkbox\" id=\"SympOthers\"  name=\"SympOthers\" onclick=\"OpenOtherTxt();\"/>";
                Style = Style + " #SympOtherTxtDiv{display:none; }";
            }

            if (EmpHealthChk.equals("1")) {
                EmpHealthChkBuffer.append("<div class=\"controls\">\n" +
                        "<fieldset>\n" +
                        "<input name=\"EmpHealthChk\" type=\"radio\" id=\"EmpHealthChk_Yes\" value=\"1\" checked>\n" +
                        "<label for=\"EmpHealthChk_Yes\">Yes</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"EmpHealthChk\" type=\"radio\" id=\"EmpHealthChk_No\" value=\"0\">\n" +
                        "<label for=\"EmpHealthChk_No\">No</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"EmpHealthChk\" type=\"radio\" id=\"EmpHealthChk_UN\" value=\"-1\">\n" +
                        "<label for=\"EmpHealthChk_UN\">Unknown</label>\n" +
                        "</fieldset>\n" +
                        "</div>");
            } else if (EmpHealthChk.equals("0")) {
                EmpHealthChkBuffer.append("<div class=\"controls\">\n" +
                        "<fieldset>\n" +
                        "<input name=\"EmpHealthChk\" type=\"radio\" id=\"EmpHealthChk_Yes\" value=\"1\" >\n" +
                        "<label for=\"EmpHealthChk_Yes\">Yes</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"EmpHealthChk\" type=\"radio\" id=\"EmpHealthChk_No\" value=\"0\" checked>\n" +
                        "<label for=\"EmpHealthChk_No\">No</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"EmpHealthChk\" type=\"radio\" id=\"EmpHealthChk_UN\" value=\"-1\">\n" +
                        "<label for=\"EmpHealthChk_UN\">Unknown</label>\n" +
                        "</fieldset>\n" +
                        "</div>");
            } else if (EmpHealthChk.equals("-1")) {
                EmpHealthChkBuffer.append("<div class=\"controls\">\n" +
                        "<fieldset>\n" +
                        "<input name=\"EmpHealthChk\" type=\"radio\" id=\"EmpHealthChk_Yes\" value=\"1\" >\n" +
                        "<label for=\"EmpHealthChk_Yes\">Yes</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"EmpHealthChk\" type=\"radio\" id=\"EmpHealthChk_No\" value=\"0\" >\n" +
                        "<label for=\"EmpHealthChk_No\">No</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"EmpHealthChk\" type=\"radio\" id=\"EmpHealthChk_UN\" value=\"-1\" checked>\n" +
                        "<label for=\"EmpHealthChk_UN\">Unknown</label>\n" +
                        "</fieldset>\n" +
                        "</div>");
            } else {
                EmpHealthChkBuffer.append("<div class=\"controls\">\n" +
                        "<fieldset>\n" +
                        "<input name=\"EmpHealthChk\" type=\"radio\" id=\"EmpHealthChk_Yes\" value=\"1\" >\n" +
                        "<label for=\"EmpHealthChk_Yes\">Yes</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"EmpHealthChk\" type=\"radio\" id=\"EmpHealthChk_No\" value=\"0\" >\n" +
                        "<label for=\"EmpHealthChk_No\">No</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"EmpHealthChk\" type=\"radio\" id=\"EmpHealthChk_UN\" value=\"-1\" >\n" +
                        "<label for=\"EmpHealthChk_UN\">Unknown</label>\n" +
                        "</fieldset>\n" +
                        "</div>");
            }
            if (gender.trim().toUpperCase().equals("FEMALE") && PregChk.equals("1")) {
                PregChkBuffer.append("<label><font color=\"black\">Are you pregnant ?</font></label><div class=\"controls\">\n" +
                        "<fieldset>\n" +
                        "<input name=\"PregChk\" type=\"radio\" id=\"PregChk_Yes\" value=\"1\" checked>\n" +
                        "<label for=\"PregChk_Yes\">Yes</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"PregChk\" type=\"radio\" id=\"PregChk_No\" value=\"0\">\n" +
                        "<label for=\"PregChk_No\">No</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"PregChk\" type=\"radio\" id=\"PregChk_UN\" value=\"-1\">\n" +
                        "<label for=\"PregChk_UN\">Unknown</label>\n" +
                        "</fieldset>\n" +
                        "</div>");
            } else if (gender.trim().toUpperCase().equals("FEMALE") && PregChk.equals("0")) {
                PregChkBuffer.append("<label><font color=\"black\">Are you pregnant ?</font></label><div class=\"controls\">\n" +
                        "<fieldset>\n" +
                        "<input name=\"PregChk\" type=\"radio\" id=\"PregChk_Yes\" value=\"1\" >\n" +
                        "<label for=\"PregChk_Yes\">Yes</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"PregChk\" type=\"radio\" id=\"PregChk_No\" value=\"0\" checked>\n" +
                        "<label for=\"PregChk_No\">No</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"PregChk\" type=\"radio\" id=\"PregChk_UN\" value=\"-1\">\n" +
                        "<label for=\"PregChk_UN\">Unknown</label>\n" +
                        "</fieldset>\n" +
                        "</div>");
            } else if (gender.trim().toUpperCase().equals("FEMALE") && PregChk.equals("-1")) {
                PregChkBuffer.append("<label><font color=\"black\">Are you pregnant ?</font></label><div class=\"controls\">\n" +
                        "<fieldset>\n" +
                        "<input name=\"PregChk\" type=\"radio\" id=\"PregChk_Yes\" value=\"1\" >\n" +
                        "<label for=\"PregChk_Yes\">Yes</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"PregChk\" type=\"radio\" id=\"PregChk_No\" value=\"0\" >\n" +
                        "<label for=\"PregChk_No\">No</label>\n" +
                        "</fieldset>\n" +
                        "<fieldset>\n" +
                        "<input name=\"PregChk\" type=\"radio\" id=\"PregChk_UN\" value=\"-1\" checked>\n" +
                        "<label for=\"PregChk_UN\">Unknown</label>\n" +
                        "</fieldset>\n" +
                        "</div>");
            }


            if (TestForTravel.equals("1")) {
                TestForTravelChkBuffer.append("<div id=\"TestForTravel\" style=\"display: block;\">\n" +
                        "     <div >\n" +
                        "         <div class=\"form-group\">\n" +
                        "             <label><font color=\"black\">Is this covid test for travelling ?\n" +
                        "                 </font></label>\n" +
                        "             <div class=\"controls\">\n" +
                        " <fieldset> \n" +
                        "     <input name=\"TestForTravelChk\" type=\"radio\" \n" +
                        "            id=\"TestForTravelChk_Yes\" value=\"1\" checked>\n" +
                        "     <label for=\"TestForTravelChk_Yes\">Yes</label>\n" +
                        " </fieldset>\n" +
                        " <fieldset>\n" +
                        "     <input name=\"TestForTravelChk\" type=\"radio\"\n" +
                        "            id=\"TestForTravelChk_No\" value=\"0\" >\n" +
                        "     <label for=\"TestForTravelChk_No\">No</label>\n" +
                        " </fieldset>\n" +
                        "         </div>\n" +
                        "         </div>\n" +
                        "     </div>\n" +
                        " </div>");
            } else {
                TestForTravelChkBuffer.append("<div id=\"TestForTravel\" style=\"display: none;\">\n" +
                        "     <div >\n" +
                        "         <div class=\"form-group\">\n" +
                        "             <label><font color=\"black\">Is this covid test for travelling ?\n" +
                        "                 </font></label>\n" +
                        "             <div class=\"controls\">\n" +
                        " <fieldset> \n" +
                        "     <input name=\"TestForTravelChk\" type=\"radio\" \n" +
                        "            id=\"TestForTravelChk_Yes\" value=\"1\" >\n" +
                        "     <label for=\"TestForTravelChk_Yes\">Yes</label>\n" +
                        " </fieldset>\n" +
                        " <fieldset>\n" +
                        "     <input name=\"TestForTravelChk\" type=\"radio\"\n" +
                        "            id=\"TestForTravelChk_No\" value=\"0\" >\n" +
                        "     <label for=\"TestForTravelChk_No\">No</label>\n" +
                        " </fieldset>\n" +
                        "         </div>\n" +
                        "         </div>\n" +
                        "     </div>\n" +
                        " </div>");
            }


            if (EmployementChk.equals("1")) {
                EmploymentBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"EmployementChk\" type=\"radio\" id=\"EmployementChk_Yes\" value=\"1\" onLoad=\"EmployementDivShow(this.value)\" checked >\n<label for=\"EmployementChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"EmployementChk\" type=\"radio\" id=\"EmployementChk_No\" value=\"0\" onLoad=\"EmployementDivShow(this.value)\" >\n<label for=\"EmployementChk_No\">No</label>\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #EmployementDiv{ display : block; } ";
            } else if (EmployementChk.equals("0")) {
                EmploymentBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"EmployementChk\" type=\"radio\" id=\"EmployementChk_Yes\" value=\"1\" onLoad=\"EmployementDivShow(this.value)\"  >\n<label for=\"EmployementChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"EmployementChk\" type=\"radio\" id=\"EmployementChk_No\" value=\"0\" onLoad=\"EmployementDivShow(this.value)\" checked>\n<label for=\"EmployementChk_No\">No</label>\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #EmployementDiv{ display : none; } ";
            } else {
                EmploymentBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"EmployementChk\" type=\"radio\" id=\"EmployementChk_Yes\" value=\"1\" onLoad=\"EmployementDivShow(this.value)\"  >\n<label for=\"EmployementChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"EmployementChk\" type=\"radio\" id=\"EmployementChk_No\" value=\"0\" onLoad=\"EmployementDivShow(this.value)\" >\n<label for=\"EmployementChk_No\">No</label>\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #EmployementDiv{ display : none; } ";
            }
            if (PrimaryCarePhysicianChk.equals("1")) {
                PriCarPhyBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"PrimaryCarePhysicianChk\" type=\"radio\" id=\"PrimaryCarePhysicianChk_Yes\" value=\"1\" onclick=\"PrimaryCarePhysicianDivShow(this.value)\" checked  />\n<label for=\"PrimaryCarePhysicianChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"PrimaryCarePhysicianChk\" type=\"radio\" id=\"PrimaryCarePhysicianChk_No\" value=\"0\" onclick=\"PrimaryCarePhysicianDivShow(this.value)\" />\n<label for=\"PrimaryCarePhysicianChk_No\">No</label>\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #PrimaryCarePhysicianDiv{ display: block; }";
            } else if (PrimaryCarePhysicianChk.equals("0")) {
                PriCarPhyBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"PrimaryCarePhysicianChk\" type=\"radio\" id=\"PrimaryCarePhysicianChk_Yes\" value=\"1\" onclick=\"PrimaryCarePhysicianDivShow(this.value)\"   />\n<label for=\"PrimaryCarePhysicianChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"PrimaryCarePhysicianChk\" type=\"radio\" id=\"PrimaryCarePhysicianChk_No\" value=\"0\" onclick=\"PrimaryCarePhysicianDivShow(this.value)\" checked />\n<label for=\"PrimaryCarePhysicianChk_No\">No</label>\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #PrimaryCarePhysicianDiv{ display: none; }";
            } else {
                PriCarPhyBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"PrimaryCarePhysicianChk\" type=\"radio\" id=\"PrimaryCarePhysicianChk_Yes\" value=\"1\" onclick=\"PrimaryCarePhysicianDivShow(this.value)\"   />\n<label for=\"PrimaryCarePhysicianChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"PrimaryCarePhysicianChk\" type=\"radio\" id=\"PrimaryCarePhysicianChk_No\" value=\"0\" onclick=\"PrimaryCarePhysicianDivShow(this.value)\" />\n<label for=\"PrimaryCarePhysicianChk_No\">No</label>\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #PrimaryCarePhysicianDiv{ display: none; }";
            }
            if (PatientMinorChk.equals("1")) {
                PatinetMinorBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"PatientMinorChk\" type=\"radio\" id=\"PatientMinorChk_Yes\" value=\"1\" checked onclick=\"PatientMinorFieldsReq(this.value)\" required />\n<label for=\"PatientMinorChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"PatientMinorChk\" type=\"radio\" id=\"PatientMinorChk_No\" value=\"0\" onclick=\"PatientMinorFieldsReq(this.value)\" />\n<label for=\"PatientMinorChk_No\">No</label>\n</fieldset>\n</div>\n");
            } else if (PatientMinorChk.equals("0")) {
                PatinetMinorBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"PatientMinorChk\" type=\"radio\" id=\"PatientMinorChk_Yes\" value=\"1\" onclick=\"PatientMinorFieldsReq(this.value)\" required />\n<label for=\"PatientMinorChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"PatientMinorChk\" type=\"radio\" id=\"PatientMinorChk_No\" value=\"0\" onclick=\"PatientMinorFieldsReq(this.value)\" checked />\n<label for=\"PatientMinorChk_No\">No</label>\n</fieldset>\n</div>\n");
            } else {
                PatinetMinorBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"PatientMinorChk\" type=\"radio\" id=\"PatientMinorChk_Yes\" value=\"1\" onclick=\"PatientMinorFieldsReq(this.value)\" required />\n<label for=\"PatientMinorChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"PatientMinorChk\" type=\"radio\" id=\"PatientMinorChk_No\" value=\"0\" onclick=\"PatientMinorFieldsReq(this.value)\" />\n<label for=\"PatientMinorChk_No\">No</label>\n</fieldset>\n</div>\n");
            }
            if (GuarantorChk.equals("1")) {
                GuarantorBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_Patient\" value=\"1\" checked />\n<label for=\"GuarantorChk_Patient\">The Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_LegalGuardian\"value=\"2\"  />\n<label for=\"GuarantorChk_LegalGuardian\">Legal Guardian of the Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_Parent\"value=\"3\"  />\n<label for=\"GuarantorChk_Parent\">Parent of the Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_SpousePartner\"value=\"4\"  />\n<label for=\"GuarantorChk_SpousePartner\">Spouse/Partner of the Patient</label>\t\t\t\t\t\t\t\t\t\t\t\n</fieldset>\n</div>\n");
            } else if (GuarantorChk.equals("2")) {
                GuarantorBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_Patient\" value=\"1\"   />\n<label for=\"GuarantorChk_Patient\">The Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_LegalGuardian\"value=\"2\" checked  />\n<label for=\"GuarantorChk_LegalGuardian\">Legal Guardian of the Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_Parent\"value=\"3\"  />\n<label for=\"GuarantorChk_Parent\">Parent of the Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_SpousePartner\"value=\"4\"  />\n<label for=\"GuarantorChk_SpousePartner\">Spouse/Partner of the Patient</label>\t\t\t\t\t\t\t\t\t\t\t\n</fieldset>\n</div>\n");
            } else if (GuarantorChk.equals("3")) {
                GuarantorBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_Patient\" value=\"1\"   />\n<label for=\"GuarantorChk_Patient\">The Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_LegalGuardian\"value=\"2\"   />\n<label for=\"GuarantorChk_LegalGuardian\">Legal Guardian of the Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_Parent\"value=\"3\" checked />\n<label for=\"GuarantorChk_Parent\">Parent of the Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_SpousePartner\"value=\"4\"  />\n<label for=\"GuarantorChk_SpousePartner\">Spouse/Partner of the Patient</label>\t\t\t\t\t\t\t\t\t\t\t\n</fieldset>\n</div>\n");
            } else if (GuarantorChk.equals("4")) {
                GuarantorBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_Patient\" value=\"1\"   />\n<label for=\"GuarantorChk_Patient\">The Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_LegalGuardian\"value=\"2\"   />\n<label for=\"GuarantorChk_LegalGuardian\">Legal Guardian of the Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_Parent\"value=\"3\"  />\n<label for=\"GuarantorChk_Parent\">Parent of the Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_SpousePartner\"value=\"4\" checked />\n<label for=\"GuarantorChk_SpousePartner\">Spouse/Partner of the Patient</label>\t\t\t\t\t\t\t\t\t\t\t\n</fieldset>\n</div>\n");
            } else {
                GuarantorBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_Patient\" value=\"1\"   />\n<label for=\"GuarantorChk_Patient\">The Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_LegalGuardian\"value=\"2\"   />\n<label for=\"GuarantorChk_LegalGuardian\">Legal Guardian of the Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_Parent\"value=\"3\"  />\n<label for=\"GuarantorChk_Parent\">Parent of the Patient</label>\n</fieldset>\n<fieldset>\n<input name=\"GuarantorChk\" type=\"radio\" id=\"GuarantorChk_SpousePartner\"value=\"4\" />\n<label for=\"GuarantorChk_SpousePartner\">Spouse/Partner of the Patient</label>\t\t\t\t\t\t\t\t\t\t\t\n</fieldset>\n</div>\n");
            }
            if (WorkersCompPolicyChk.equals("1")) {
                WorkCompBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"WorkersCompPolicyChk\" type=\"radio\" id=\"WorkersCompPolicy_Yes\" value=\"1\" checked onclick=\"WorkersCompPolicyDivShow(this.value)\" required />\n<label for=\"WorkersCompPolicy_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"WorkersCompPolicyChk\" type=\"radio\" id=\"WorkersCompPolicy_No\"value=\"0\" onclick=\"WorkersCompPolicyDivShow(this.value)\" />\n<label for=\"WorkersCompPolicy_No\">No</label>\t\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #WorkersCompPolicyDiv{ display: block; }";
            } else {
                WorkCompBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"WorkersCompPolicyChk\" type=\"radio\" id=\"WorkersCompPolicy_Yes\" value=\"1\"  onclick=\"WorkersCompPolicyDivShow(this.value)\" required />\n<label for=\"WorkersCompPolicy_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"WorkersCompPolicyChk\" type=\"radio\" id=\"WorkersCompPolicy_No\"value=\"0\" checked onclick=\"WorkersCompPolicyDivShow(this.value)\" />\n<label for=\"WorkersCompPolicy_No\">No</label>\t\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #WorkersCompPolicyDiv{ display: none; }";
            }
            if (WCPInjuryRelatedAutoMotorAccident.equals("1")) {
                WCPInjuryRelatedAutoMotorAccident = "<input type=\"checkbox\" id=\"WCPInjuryRelatedAutoMotorAccident\" name=\"WCPInjuryRelatedAutoMotorAccident\" checked />";
            } else {
                WCPInjuryRelatedAutoMotorAccident = "<input type=\"checkbox\" id=\"WCPInjuryRelatedAutoMotorAccident\" name=\"WCPInjuryRelatedAutoMotorAccident\" />";
            }
            if (WCPInjuryRelatedWorkRelated.equals("1")) {
                WCPInjuryRelatedWorkRelated = "<input type=\"checkbox\" id=\"WCPInjuryRelatedWorkRelated\" name=\"WCPInjuryRelatedWorkRelated\" checked />";
            } else {
                WCPInjuryRelatedWorkRelated = "<input type=\"checkbox\" id=\"WCPInjuryRelatedWorkRelated\" name=\"WCPInjuryRelatedWorkRelated\" />";
            }
            if (WCPInjuryRelatedOtherAccident.equals("1")) {
                WCPInjuryRelatedOtherAccident = "<input type=\"checkbox\" id=\"WCPInjuryRelatedOtherAccident\" name=\"WCPInjuryRelatedOtherAccident\" checked/>";
            } else {
                WCPInjuryRelatedOtherAccident = "<input type=\"checkbox\" id=\"WCPInjuryRelatedOtherAccident\" name=\"WCPInjuryRelatedOtherAccident\" />";
            }
            if (WCPInjuryRelatedNoAccident.equals("1")) {
                WCPInjuryRelatedNoAccident = "<input type=\"checkbox\" id=\"WCPInjuryRelatedNoAccident\" name=\"WCPInjuryRelatedNoAccident\" checked />";
            } else {
                WCPInjuryRelatedNoAccident = "<input type=\"checkbox\" id=\"WCPInjuryRelatedNoAccident\" name=\"WCPInjuryRelatedNoAccident\" />";
            }
            if (WCPInjuryOccurVehicle.equals("1")) {
                WCPInjuryOccurVehicle = "<input type=\"checkbox\" id=\"WCPInjuryOccurVehicle\" name=\"WCPInjuryOccurVehicle\" checked/>";
            } else {
                WCPInjuryOccurVehicle = "<input type=\"checkbox\" id=\"WCPInjuryOccurVehicle\" name=\"WCPInjuryOccurVehicle\"/>";
            }
            if (WCPInjuryOccurWork.equals("1")) {
                WCPInjuryOccurWork = "<input type=\"checkbox\" id=\"WCPInjuryOccurWork\" name=\"WCPInjuryOccurWork\" checked />";
            } else {
                WCPInjuryOccurWork = "<input type=\"checkbox\" id=\"WCPInjuryOccurWork\" name=\"WCPInjuryOccurWork\" />";
            }
            if (WCPInjuryOccurHome.equals("1")) {
                WCPInjuryOccurHome = "<input type=\"checkbox\" id=\"WCPInjuryOccurHome\" name=\"WCPInjuryOccurHome\" checked />";
            } else {
                WCPInjuryOccurHome = "<input type=\"checkbox\" id=\"WCPInjuryOccurHome\" name=\"WCPInjuryOccurHome\" />";
            }
            if (WCPInjuryOccurOther.equals("1")) {
                WCPInjuryOccurOther = "<input type=\"checkbox\" id=\"WCPInjuryOccurOther\" name=\"WCPInjuryOccurOther\" checked />";
            } else {
                WCPInjuryOccurOther = "<input type=\"checkbox\" id=\"WCPInjuryOccurOther\" name=\"WCPInjuryOccurOther\" />";
            }
            if (MotorVehicleAccidentChk.equals("1")) {
                MotorVehicleBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"MotorVehicleAccidentChk\" type=\"radio\" id=\"MotorVehicleAccidentChk_Yes\" value=\"1\" onclick=\"MotorVehicleAccidentDivShow(this.value)\" checked required />\n<label for=\"MotorVehicleAccidentChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"MotorVehicleAccidentChk\" type=\"radio\" id=\"MotorVehicleAccidentChk_No\"value=\"0\" onclick=\"MotorVehicleAccidentDivShow(this.value)\" />\n<label for=\"MotorVehicleAccidentChk_No\">No</label>\t\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #MotorVehicleAccidentDiv{ display: block; }";
            } else {
                MotorVehicleBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"MotorVehicleAccidentChk\" type=\"radio\" id=\"MotorVehicleAccidentChk_Yes\" value=\"1\" onclick=\"MotorVehicleAccidentDivShow(this.value)\" required />\n<label for=\"MotorVehicleAccidentChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"MotorVehicleAccidentChk\" type=\"radio\" id=\"MotorVehicleAccidentChk_No\"value=\"0\" onclick=\"MotorVehicleAccidentDivShow(this.value)\" checked />\n<label for=\"MotorVehicleAccidentChk_No\">No</label>\t\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #MotorVehicleAccidentDiv{ display: none; }";
            }
            if (AutoInsuranceInformationChk.equals("1")) {
                AutoInsuranceBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"AutoInsuranceInformationChk\" type=\"radio\" id=\"AutoInsuranceInformationChk_Yes\" value=\"1\" checked onclick=\"AutoInsuranceInformationDivShow(this.value)\"/>\n<label for=\"AutoInsuranceInformationChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"AutoInsuranceInformationChk\" type=\"radio\" id=\"AutoInsuranceInformationChk_No\"value=\"0\" onclick=\"AutoInsuranceInformationDivShow(this.value)\" />\n<label for=\"AutoInsuranceInformationChk_No\">No</label>\t\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #AutoInsuranceInformationDiv1{ display: block; } #AutoInsuranceInformationDiv2{ display: block; }";
            } else if (AutoInsuranceInformationChk.equals("0")) {
                AutoInsuranceBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"AutoInsuranceInformationChk\" type=\"radio\" id=\"AutoInsuranceInformationChk_Yes\" value=\"1\"  onclick=\"AutoInsuranceInformationDivShow(this.value)\"/>\n<label for=\"AutoInsuranceInformationChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"AutoInsuranceInformationChk\" type=\"radio\" id=\"AutoInsuranceInformationChk_No\"value=\"0\" checked onclick=\"AutoInsuranceInformationDivShow(this.value)\" />\n<label for=\"AutoInsuranceInformationChk_No\">No</label>\t\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #AutoInsuranceInformationDiv1{ display: none; } #AutoInsuranceInformationDiv2{ display: block; } ";
            } else {
                AutoInsuranceBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"AutoInsuranceInformationChk\" type=\"radio\" id=\"AutoInsuranceInformationChk_Yes\" value=\"1\"  onclick=\"AutoInsuranceInformationDivShow(this.value)\"/>\n<label for=\"AutoInsuranceInformationChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"AutoInsuranceInformationChk\" type=\"radio\" id=\"AutoInsuranceInformationChk_No\"value=\"0\" onclick=\"AutoInsuranceInformationDivShow(this.value)\" />\n<label for=\"AutoInsuranceInformationChk_No\">No</label>\t\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #AutoInsuranceInformationDiv1{ display: none;} #AutoInsuranceInformationDiv2{ display: none; } ";
            }
            if (AIIRoleInAccident.toUpperCase().equals("DRIVER")) {
                AIIRoleInAccidentBuffer.append("<select class=\"form-control\" id=\"AIIRoleInAccident\" name=\"AIIRoleInAccident\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Driver\" selected>Driver</option>\n<option value=\"Passenger\">Passenger</option>\n</select>\n");
            } else if (AIIRoleInAccident.toUpperCase().equals("PASSENGER")) {
                AIIRoleInAccidentBuffer.append("<select class=\"form-control\" id=\"AIIRoleInAccident\" name=\"AIIRoleInAccident\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Driver\" >Driver</option>\n<option value=\"Passenger\" selected>Passenger</option>\n</select>\n");
            } else {
                AIIRoleInAccidentBuffer.append("<select class=\"form-control\" id=\"AIIRoleInAccident\" name=\"AIIRoleInAccident\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Driver\" >Driver</option>\n<option value=\"Passenger\">Passenger</option>\n</select>\n");
            }
            if (AIITypeOfAutoIOnsurancePolicy.toUpperCase().equals("PASSENGERVEHICLE")) {
                AIITypeOfAutoIOnsurancePolicyBuffer.append("<select class=\"form-control\" id=\"AIITypeOfAutoIOnsurancePolicy\" name=\"AIITypeOfAutoIOnsurancePolicy\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"PassengerVehicle\" selected>Passenger Vehicle</option>\n<option value=\"MotorCycle\">MotorCycle</option>\n</select>\n");
            } else if (AIITypeOfAutoIOnsurancePolicy.toUpperCase().equals("MOTORCYCLE")) {
                AIITypeOfAutoIOnsurancePolicyBuffer.append("<select class=\"form-control\" id=\"AIITypeOfAutoIOnsurancePolicy\" name=\"AIITypeOfAutoIOnsurancePolicy\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"PassengerVehicle\" >Passenger Vehicle</option>\n<option value=\"MotorCycle\" selected>MotorCycle</option>\n</select>\n");
            } else {
                AIITypeOfAutoIOnsurancePolicyBuffer.append("<select class=\"form-control\" id=\"AIITypeOfAutoIOnsurancePolicy\" name=\"AIITypeOfAutoIOnsurancePolicy\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"PassengerVehicle\" >Passenger Vehicle</option>\n<option value=\"MotorCycle\">MotorCycle</option>\n</select>\n");
            }
            if (HealthInsuranceChk.equals("1")) {
                HealthInsuranceBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"HealthInsuranceChk\" type=\"radio\" id=\"HealthInsuranceChk_Yes\" value=\"1\" checked onclick=\"HealthInsuranceDivShow(this.value)\" required />\n<label for=\"HealthInsuranceChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"HealthInsuranceChk\" type=\"radio\" id=\"HealthInsuranceChk_No\"value=\"0\" onclick=\"HealthInsuranceDivShow(this.value)\" />\n<label for=\"HealthInsuranceChk_No\">No</label>\t\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #HealthInsuranceDiv{ display: block; }";
            } else {
                HealthInsuranceBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"HealthInsuranceChk\" type=\"radio\" id=\"HealthInsuranceChk_Yes\" value=\"1\"  onclick=\"HealthInsuranceDivShow(this.value)\" required />\n<label for=\"HealthInsuranceChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"HealthInsuranceChk\" type=\"radio\" id=\"HealthInsuranceChk_No\"value=\"0\" checked onclick=\"HealthInsuranceDivShow(this.value)\" />\n<label for=\"HealthInsuranceChk_No\">No</label>\t\n</fieldset>\n</div>\n");
                Style = String.valueOf(String.valueOf(Style)) + " #HealthInsuranceDiv{ display: none; }";
            }
            if (GovtFundedInsurancePlanChk.equals("1")) {
                GovtFundedInsuranceBuffer.append("<div class=\"demo-radio-button\">\n<input name=\"GovtFundedInsurancePlanChk\" type=\"radio\" id=\"GovtFundedInsurancePlanChk_Yes\" value=\"1\" checked onclick=\"GovtFundedInsurancePlanDivShow(this.value)\"/>\n<label for=\"GovtFundedInsurancePlanChk_Yes\">Yes</label>\n\n<input name=\"GovtFundedInsurancePlanChk\" type=\"radio\" id=\"GovtFundedInsurancePlanChk_No\"value=\"0\" onclick=\"GovtFundedInsurancePlanDivShow(this.value)\" />\n<label for=\"GovtFundedInsurancePlanChk_No\">No</label>\t\n</div>\t\t\t\t\t\t\t\t\t \n");
                Style = String.valueOf(String.valueOf(Style)) + " #GovtFundedInsurancePlanDiv{ display: block; }";
            } else {
                GovtFundedInsuranceBuffer.append("<div class=\"demo-radio-button\">\n<input name=\"GovtFundedInsurancePlanChk\" type=\"radio\" id=\"GovtFundedInsurancePlanChk_Yes\" value=\"1\"  onclick=\"GovtFundedInsurancePlanDivShow(this.value)\"/>\n<label for=\"GovtFundedInsurancePlanChk_Yes\">Yes</label>\n\n<input name=\"GovtFundedInsurancePlanChk\" type=\"radio\" id=\"GovtFundedInsurancePlanChk_No\"value=\"0\" checked onclick=\"GovtFundedInsurancePlanDivShow(this.value)\" />\n<label for=\"GovtFundedInsurancePlanChk_No\">No</label>\t\n</div>\t\t\t\t\t\t\t\t\t \n");
                Style = String.valueOf(String.valueOf(Style)) + " #GovtFundedInsurancePlanDiv{ display: none; }";
            }
            if (GFIPMedicare.equals("1")) {
                GFIPMedicare = "<input type=\"checkbox\" id=\"GFIPMedicare\" name=\"GFIPMedicare\" checked/>";
            } else {
                GFIPMedicare = "<input type=\"checkbox\" id=\"GFIPMedicare\" name=\"GFIPMedicare\"/>";
            }
            if (GFIPMedicaid.equals("1")) {
                GFIPMedicaid = "<input type=\"checkbox\" id=\"GFIPMedicaid\" name=\"GFIPMedicaid\" checked/>";
            } else {
                GFIPMedicaid = "<input type=\"checkbox\" id=\"GFIPMedicaid\" name=\"GFIPMedicaid\"/>";
            }
            if (GFIPCHIP.equals("1")) {
                GFIPCHIP = "<input type=\"checkbox\" id=\"GFIPCHIP\" name=\"GFIPCHIP\" checked/>";
            } else {
                GFIPCHIP = "<input type=\"checkbox\" id=\"GFIPCHIP\" name=\"GFIPCHIP\" />";
            }
            if (GFIPTricare.equals("1")) {
                GFIPTricare = "<input type=\"checkbox\" id=\"GFIPTricare\" name=\"GFIPTricare\"  checked />";
            } else {
                GFIPTricare = "<input type=\"checkbox\" id=\"GFIPTricare\" name=\"GFIPTricare\"  />";
            }
            if (GFIPVHA.equals("1")) {
                GFIPVHA = "<input type=\"checkbox\" id=\"GFIPVHA\" name=\"GFIPVHA\" checked />";
            } else {
                GFIPVHA = "<input type=\"checkbox\" id=\"GFIPVHA\" name=\"GFIPVHA\" />";
            }
            if (GFIPIndianHealth.equals("1")) {
                GFIPIndianHealth = "<input type=\"checkbox\" id=\"GFIPIndianHealth\" name=\"GFIPIndianHealth\"  checked/>";
            } else {
                GFIPIndianHealth = "<input type=\"checkbox\" id=\"GFIPIndianHealth\" name=\"GFIPIndianHealth\"  />";
            }
            if (InsuranceSubPatient.equals("1")) {
                InsuranceSubPatient = "<input type=\"checkbox\" id=\"InsuranceSubPatient\" name=\"InsuranceSubPatient\" onclick=\"InsuranceSubPatientCheck();\" checked />";
            } else {
                InsuranceSubPatient = "<input type=\"checkbox\" id=\"InsuranceSubPatient\" name=\"InsuranceSubPatient\" onclick=\"InsuranceSubPatientCheck();\" />";
            }
            if (InsuranceSubGuarantor.equals("1")) {
                InsuranceSubGuarantor = "<input type=\"checkbox\" id=\"InsuranceSubGuarantor\" name=\"InsuranceSubGuarantor\" checked/>";
            } else {
                InsuranceSubGuarantor = "<input type=\"checkbox\" id=\"InsuranceSubGuarantor\" name=\"InsuranceSubGuarantor\" />";
            }
            if (InsuranceSubOther.equals("InsuranceSubOther")) {
                InsuranceSubOther = "<input type=\"checkbox\" id=\"InsuranceSubOther\" name=\"InsuranceSubOther\" checked />";
            } else {
                InsuranceSubOther = "<input type=\"checkbox\" id=\"InsuranceSubOther\" name=\"InsuranceSubOther\" />";
            }
            if (HISubscriberRelationtoPatient.toUpperCase().equals("SELF")) {
                HISubscriberRelationtoPatientBuffer.append("<select class=\"form-control\" id=\"HISubscriberRelationtoPatient\" name=\"HISubscriberRelationtoPatient\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Self\" selected>Self</option>\n<option value=\"Parent\" >Parent</option>\n</select>");
            } else if (HISubscriberRelationtoPatient.toUpperCase().equals("PARENT")) {
                HISubscriberRelationtoPatientBuffer.append("<select class=\"form-control\" id=\"HISubscriberRelationtoPatient\" name=\"HISubscriberRelationtoPatient\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Self\">Self</option>\n<option value=\"Parent\" selected>Parent</option>\n</select>");
            } else {
                HISubscriberRelationtoPatientBuffer.append("<select class=\"form-control\" id=\"HISubscriberRelationtoPatient\" name=\"HISubscriberRelationtoPatient\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Self\">Self</option>\n<option value=\"Parent\">Parent</option>\n</select>");
            }
            if (SecondHealthInsuranceChk.equals("1")) {
                SecondHealthInsuranceBuffer.append("<div class=\"demo-radio-button\">\n<input name=\"SecondHealthInsuranceChk\" type=\"radio\" id=\"SecondHealthInsuranceChk_Yes\" value=\"1\" checked onclick=\"SecondHealthInsuranceDivShow(this.value)\"/>\n<label for=\"SecondHealthInsuranceChk_Yes\">Yes</label>\n\n<input name=\"SecondHealthInsuranceChk\" type=\"radio\" id=\"SecondHealthInsuranceChk_No\"value=\"0\" onclick=\"SecondHealthInsuranceDivShow(this.value)\" />\n<label for=\"SecondHealthInsuranceChk_No\">No</label>\t\n</div>");
                Style = String.valueOf(String.valueOf(Style)) + " #SecondHealthInsuranceDiv{ display: block; }";
            } else if (SecondHealthInsuranceChk.equals("0")) {
                SecondHealthInsuranceBuffer.append("<div class=\"demo-radio-button\">\n<input name=\"SecondHealthInsuranceChk\" type=\"radio\" id=\"SecondHealthInsuranceChk_Yes\" value=\"1\"  onclick=\"SecondHealthInsuranceDivShow(this.value)\"/>\n<label for=\"SecondHealthInsuranceChk_Yes\">Yes</label>\n\n<input name=\"SecondHealthInsuranceChk\" type=\"radio\" id=\"SecondHealthInsuranceChk_No\"value=\"0\" checked onclick=\"SecondHealthInsuranceDivShow(this.value)\" />\n<label for=\"SecondHealthInsuranceChk_No\">No</label>\t\n</div>");
                Style = String.valueOf(String.valueOf(Style)) + " #SecondHealthInsuranceDiv{ display: none; }";
            } else {
                SecondHealthInsuranceBuffer.append("<div class=\"demo-radio-button\">\n<input name=\"SecondHealthInsuranceChk\" type=\"radio\" id=\"SecondHealthInsuranceChk_Yes\" value=\"1\"  onclick=\"SecondHealthInsuranceDivShow(this.value)\"/>\n<label for=\"SecondHealthInsuranceChk_Yes\">Yes</label>\n\n<input name=\"SecondHealthInsuranceChk\" type=\"radio\" id=\"SecondHealthInsuranceChk_No\"value=\"0\" onclick=\"SecondHealthInsuranceDivShow(this.value)\" />\n<label for=\"SecondHealthInsuranceChk_No\">No</label>\t\n</div>");
                Style = String.valueOf(String.valueOf(Style)) + " #SecondHealthInsuranceDiv{ display: none; }";
            }
            Query = "Select Value, PriInsurance from " + Database + ".Master_PriIns where Status = 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            SHISecondaryNameBuffer.append("<option value=''>Select One</option>");
            while (rset.next()) {
                if (SHISecondaryName.toUpperCase().equals(rset.getString(1).trim())) {
                    SHISecondaryNameBuffer.append("<option value='" + rset.getString(1) + "' selected>" + rset.getString(2) + "</option>");
                } else {
                    SHISecondaryNameBuffer.append("<option value='" + rset.getString(1) + "'>" + rset.getString(2) + "</option>");
                }
            }
            rset.close();
            stmt.close();
            if (SHISubscriberRelationtoPatient.toUpperCase().equals("SELF")) {
                SHISubscriberRelationtoPatientBuffer.append("<select class=\"form-control\" id=\"SHISubscriberRelationtoPatient\" name=\"SHISubscriberRelationtoPatient\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Self\" selected>Self</option>\n<option value=\"Parent\">Parent</option>\n</select>");
            } else if (SHISubscriberRelationtoPatient.toUpperCase().equals("PARENT")) {
                SHISubscriberRelationtoPatientBuffer.append("<select class=\"form-control\" id=\"SHISubscriberRelationtoPatient\" name=\"SHISubscriberRelationtoPatient\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Self\" >Self</option>\n<option value=\"Parent\" selected>Parent</option>\n</select>");
            } else {
                SHISubscriberRelationtoPatientBuffer.append("<select class=\"form-control\" id=\"SHISubscriberRelationtoPatient\" name=\"SHISubscriberRelationtoPatient\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Self\" >Self</option>\n<option value=\"Parent\">Parent</option>\n</select>");
            }
            Query = "Select Value, PriInsurance from " + Database + ".Master_PriIns where Status = 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            HIPrimaryInsuranceBuff.append("<option value=''>Select Insurance</option>");
            while (rset.next()) {
                if (HIPrimaryInsurance.equals(rset.getString(1).trim())) {
                    HIPrimaryInsuranceBuff.append("<option value='" + rset.getString(1) + "' selected>" + rset.getString(2) + "</option>");
                } else {
                    HIPrimaryInsuranceBuff.append("<option value='" + rset.getString(1) + "'>" + rset.getString(2) + "</option>");
                }
            }
            rset.close();
            stmt.close();

            Query = "Select Id, CONCAT(IFNULL(DoctorsLastName,''),', ',IFNULL(DoctorsFirstName,'')) from " + Database + ".DoctorsList where Status = 1 order by DoctorsLastName";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            DoctorsBuffer.append("<option value=''>Select Doctor</option>");
            while (rset.next()) {
                if (DoctorId.equals(rset.getString(1).trim())) {
                    DoctorsBuffer.append("<option value='" + rset.getString(1) + "' selected>" + rset.getString(2) + "</option>");
                } else {
                    DoctorsBuffer.append("<option value='" + rset.getString(1) + "'>" + rset.getString(2) + "</option>");
                }
            }
            rset.close();
            stmt.close();

            if (MFFirstVisit.equals("1")) {
                MFFirstVisit = "<div class=\"demo-radio-button\"> \n" +
                        "<input name=\"MFFirstVisit\" type=\"radio\" id=\"MFFirstVisit_Yes\" value=\"1\" checked/>\n" +
                        "<label for=\"MFFirstVisit_Yes\">Yes</label>\n" +
                        "<input name=\"MFFirstVisit\" type=\"radio\" id=\"MFFirstVisit_No\" value=\"0\"/>\n" +
                        "<label for=\"MFFirstVisit_No\">No</label>\n" +
                        "</div>\n";
            } else if (MFFirstVisit.equals("0")) {
                MFFirstVisit = "<div class=\"demo-radio-button\"> \n" +
                        "<input name=\"MFFirstVisit\" type=\"radio\" id=\"MFFirstVisit_Yes\" value=\"1\"/>\n" +
                        "<label for=\"MFFirstVisit_Yes\">Yes</label>\n" +
                        "<input name=\"MFFirstVisit\" type=\"radio\" id=\"MFFirstVisit_No\" value=\"0\" checked/>\n" +
                        "<label for=\"MFFirstVisit_No\">No</label>\n" +
                        "</div>\n";

            } else {
                MFFirstVisit = "<div class=\"demo-radio-button\"> \n" +
                        "<input name=\"MFFirstVisit\" type=\"radio\" id=\"MFFirstVisit_Yes\" value=\"1\"/>\n" +
                        "<label for=\"MFFirstVisit_Yes\">Yes</label>\n" +
                        "<input name=\"MFFirstVisit\" type=\"radio\" id=\"MFFirstVisit_No\" value=\"0\"/>\n" +
                        "<label for=\"MFFirstVisit_No\">No</label>\n" +
                        "</div>\n";
            }

            if (MFReturnPat.equals("1")) {
                MFReturnPat = "<div class=\"demo-radio-button\">\n" +
                        "<input name=\"MFReturnPat\" type=\"radio\" id=\"MFReturnPat_Yes\" value=\"1\" checked/>\n" +
                        "<label for=\"MFReturnPat_Yes\">Yes</label>\n" +
                        "<input name=\"MFReturnPat\" type=\"radio\" id=\"MFReturnPat_No\" value=\"0\"/>\n" +
                        "<label for=\"MFReturnPat_No\">No</label>\n" +
                        "</div>";
            } else if (MFReturnPat.equals("0")) {
                MFReturnPat = "<div class=\"demo-radio-button\">\n" +
                        "<input name=\"MFReturnPat\" type=\"radio\" id=\"MFReturnPat_Yes\" value=\"1\" />\n" +
                        "<label for=\"MFReturnPat_Yes\">Yes</label>\n" +
                        "<input name=\"MFReturnPat\" type=\"radio\" id=\"MFReturnPat_No\" value=\"0\" checked/>\n" +
                        "<label for=\"MFReturnPat_No\">No</label>\n" +
                        "</div>";
            } else {
                MFReturnPat = "<div class=\"demo-radio-button\">\n" +
                        "<input name=\"MFReturnPat\" type=\"radio\" id=\"MFReturnPat_Yes\" value=\"1\" />\n" +
                        "<label for=\"MFReturnPat_Yes\">Yes</label>\n" +
                        "<input name=\"MFReturnPat\" type=\"radio\" id=\"MFReturnPat_No\" value=\"0\" />\n" +
                        "<label for=\"MFReturnPat_No\">No</label>\n" +
                        "</div>";
            }
            if (MFInternetFind.equals("1")) {
                MFInternetFind = "<div class=\"demo-radio-button\">\n" +
                        "<input name=\"MFInternetFind\" type=\"radio\" id=\"MFInternetFind_Yes\" value=\"1\" checked/>\n" +
                        "<label for=\"MFInternetFind_Yes\">Yes</label>\n" +
                        "<input name=\"MFInternetFind\" type=\"radio\" id=\"MFInternetFind_No\" value=\"0\"/>\n" +
                        "<label for=\"MFInternetFind_No\">No</label>\n" +
                        "</div>";
            } else if (MFInternetFind.equals("0")) {
                MFInternetFind = "<div class=\"demo-radio-button\">\n" +
                        "<input name=\"MFInternetFind\" type=\"radio\" id=\"MFInternetFind_Yes\" value=\"1\" />\n" +
                        "<label for=\"MFInternetFind_Yes\">Yes</label>\n" +
                        "<input name=\"MFInternetFind\" type=\"radio\" id=\"MFInternetFind_No\" value=\"0\" checked/>\n" +
                        "<label for=\"MFInternetFind_No\">No</label>\n" +
                        "</div>";
            } else {
                MFInternetFind = "<div class=\"demo-radio-button\">\n" +
                        "<input name=\"MFInternetFind\" type=\"radio\" id=\"MFInternetFind_Yes\" value=\"1\" />\n" +
                        "<label for=\"MFInternetFind_Yes\">Yes</label>\n" +
                        "<input name=\"MFInternetFind\" type=\"radio\" id=\"MFInternetFind_No\" value=\"0\" />\n" +
                        "<label for=\"MFInternetFind_No\">No</label>\n" +
                        "</div>";
            }

            if (Facebook.equals("1")) {
                Facebook = "<input type=\"checkbox\" id=\"Facebook\" name=\"Facebook\" checked/>";
            } else {
                Facebook = "<input type=\"checkbox\" id=\"Facebook\" name=\"Facebook\" />";
            }
            if (MapSearch.equals("1")) {
                MapSearch = "<input type=\"checkbox\" id=\"MapSearch\" name=\"MapSearch\" checked/>";
            } else {
                MapSearch = "<input type=\"checkbox\" id=\"MapSearch\" name=\"MapSearch\" />";
            }
            if (GoogleSearch.equals("1")) {
                GoogleSearch = "<input type=\"checkbox\" id=\"GoogleSearch\" name=\"GoogleSearch\" checked/>";
            } else {
                GoogleSearch = "<input type=\"checkbox\" id=\"GoogleSearch\" name=\"GoogleSearch\" />";
            }
            if (VERWebsite.equals("1")) {
                VERWebsite = "<input type=\"checkbox\" id=\"VERWebsite\" name=\"VERWebsite\" checked/>";
            } else {
                VERWebsite = "<input type=\"checkbox\" id=\"VERWebsite\" name=\"VERWebsite\" />";
            }
            if (WebsiteAds.equals("1")) {
                WebsiteAds = "<input type=\"checkbox\" id=\"WebsiteAds\" name=\"WebsiteAds\" checked/>";
            } else {
                WebsiteAds = "<input type=\"checkbox\" id=\"WebsiteAds\" name=\"WebsiteAds\" />";
            }
            if (OnlineReviews.equals("1")) {
                OnlineReviews = "<input type=\"checkbox\" id=\"OnlineReviews\" name=\"OnlineReviews\" checked/>";
            } else {
                OnlineReviews = "<input type=\"checkbox\" id=\"OnlineReviews\" name=\"OnlineReviews\" />";
            }
            if (Twitter.equals("1")) {
                Twitter = "<input type=\"checkbox\" id=\"Twitter\" name=\"Twitter\" checked/>";
            } else {
                Twitter = "<input type=\"checkbox\" id=\"Twitter\" name=\"Twitter\" />";
            }
            if (LinkedIn.equals("1")) {
                LinkedIn = "<input type=\"checkbox\" id=\"LinkedIn\" name=\"LinkedIn\" checked/>";
            } else {
                LinkedIn = "<input type=\"checkbox\" id=\"LinkedIn\" name=\"LinkedIn\" />";
            }
            if (EmailBlast.equals("1")) {
                EmailBlast = "<input type=\"checkbox\" id=\"EmailBlast\" name=\"EmailBlast\" checked/>";
            } else {
                EmailBlast = "<input type=\"checkbox\" id=\"EmailBlast\" name=\"EmailBlast\" />";
            }
            if (YouTube.equals("1")) {
                YouTube = "<input type=\"checkbox\" id=\"YouTube\" name=\"YouTube\" checked/>";
            } else {
                YouTube = "<input type=\"checkbox\" id=\"YouTube\" name=\"YouTube\" />";
            }
            if (OnlineAdvertisements.equals("1")) {
                OnlineAdvertisements = "<input type=\"checkbox\" id=\"OnlineAdvertisements\" name=\"OnlineAdvertisements\" checked/>";
            } else {
                OnlineAdvertisements = "<input type=\"checkbox\" id=\"OnlineAdvertisements\" name=\"OnlineAdvertisements\" />";
            }
            if (TV.equals("1")) {
                TV = "<input type=\"checkbox\" id=\"TV\" name=\"TV\" checked/>";
            } else {
                TV = "<input type=\"checkbox\" id=\"TV\" name=\"TV\" />";
            }
            if (Billboard.equals("1")) {
                Billboard = "<input type=\"checkbox\" id=\"Billboard\" name=\"Billboard\" checked/>";
            } else {
                Billboard = "<input type=\"checkbox\" id=\"Billboard\" name=\"Billboard\" />";
            }
            if (Radio.equals("1")) {
                Radio = "<input type=\"checkbox\" id=\"Radio\" name=\"Radio\" checked/>";
            } else {
                Radio = "<input type=\"checkbox\" id=\"Radio\" name=\"Radio\" />";
            }
            if (Brochure.equals("1")) {
                Brochure = "<input type=\"checkbox\" id=\"Brochure\" name=\"Brochure\" checked/>";
            } else {
                Brochure = "<input type=\"checkbox\" id=\"Brochure\" name=\"Brochure\" />";
            }
            if (DirectMail.equals("1")) {
                DirectMail = "<input type=\"checkbox\" id=\"DirectMail\" name=\"DirectMail\" checked/>";
            } else {
                DirectMail = "<input type=\"checkbox\" id=\"DirectMail\" name=\"DirectMail\" />";
            }
            if (CitizensDeTar.equals("1")) {
                CitizensDeTar = "<input type=\"checkbox\" id=\"CitizensDeTar\" name=\"CitizensDeTar\" checked/>";
            } else {
                CitizensDeTar = "<input type=\"checkbox\" id=\"CitizensDeTar\" name=\"CitizensDeTar\" />";
            }
            if (LiveWorkNearby.equals("1")) {
                LiveWorkNearby = "<input type=\"checkbox\" id=\" LiveWorkNearby\" name=\"LiveWorkNearby\" checked/>";
            } else {
                LiveWorkNearby = "<input type=\"checkbox\" id=\" LiveWorkNearby\" name=\"LiveWorkNearby\" />";
            }
            if (FamilyFriend.equals("1")) {
                FamilyFriend = "<input type=\"checkbox\" id=\"FamilyFriend\" name=\"FamilyFriend\" checked/>";
            } else {
                FamilyFriend = "<input type=\"checkbox\" id=\"FamilyFriend\" name=\"FamilyFriend\" />";
            }
            if (UrgentCare.equals("1")) {
                UrgentCare = "<input type=\"checkbox\" id=\"UrgentCare\" name=\"UrgentCare\" checked/>";
            } else {
                UrgentCare = "<input type=\"checkbox\" id=\"UrgentCare\" name=\"UrgentCare\" />";
            }
            if (NewspaperMagazine.equals("1")) {
                NewspaperMagazine = "<input type=\"checkbox\" id=\"NewspaperMagazine\" name=\"NewspaperMagazine\" checked/>";
            } else {
                NewspaperMagazine = "<input type=\"checkbox\" id=\"NewspaperMagazine\" name=\"NewspaperMagazine\" />";
            }
            if (School.equals("1")) {
                School = "<input type=\"checkbox\" id=\"School\" name=\"School\" checked />";
            } else {
                School = "<input type=\"checkbox\" id=\"School\" name=\"School\"  />";
            }
            if (Hotel.equals("1")) {
                Hotel = "<input type=\"checkbox\" id=\"Hotel\" name=\"Hotel\" checked/>";
            } else {
                Hotel = "<input type=\"checkbox\" id=\"Hotel\" name=\"Hotel\" />";
            }
            if (EmployerSentMe.equals("1")) {
                EmployerSentMe = "<input type=\"checkbox\" id=\"EmployerSentMe\" name=\"EmployerSentMe\" checked/>";
            } else {
                EmployerSentMe = "<input type=\"checkbox\" id=\"EmployerSentMe\" name=\"EmployerSentMe\" />";
            }
            switch (MFPhysicianRefChk) {
                case "1":
                    MFPhysicianRefChk = "<div class=\"demo-radio-button\">\n" +
                            "<input name=\"MFPhysicianRefChk\" type=\"radio\" id=\"MFPhysicianRefChk_Yes\" value=\"1\" checked/>\n" +
                            "<label for=\"MFPhysicianRefChk_Yes\">Yes</label>\n" +
                            "<input name=\"MFPhysicianRefChk\" type=\"radio\" id=\"MFPhysicianRefChk_No\" value=\"0\"/>\n" +
                            "<label for=\"MFPhysicianRefChk_No\">No</label>\n" +
                            "</div>\n";
                    break;
                case "0":
                    MFPhysicianRefChk = "<div class=\"demo-radio-button\">\n" +
                            "<input name=\"MFPhysicianRefChk\" type=\"radio\" id=\"MFPhysicianRefChk_Yes\" value=\"1\" />\n" +
                            "<label for=\"MFPhysicianRefChk_Yes\">Yes</label>\n" +
                            "<input name=\"MFPhysicianRefChk\" type=\"radio\" id=\"MFPhysicianRefChk_No\" value=\"0\" checked/>\n" +
                            "<label for=\"MFPhysicianRefChk_No\">No</label>\n" +
                            "</div>\n";
                    break;
                default:
                    MFPhysicianRefChk = "<div class=\"demo-radio-button\">\n" +
                            "<input name=\"MFPhysicianRefChk\" type=\"radio\" id=\"MFPhysicianRefChk_Yes\" value=\"1\" />\n" +
                            "<label for=\"MFPhysicianRefChk_Yes\">Yes</label>\n" +
                            "<input name=\"MFPhysicianRefChk\" type=\"radio\" id=\"MFPhysicianRefChk_No\" value=\"0\" />\n" +
                            "<label for=\"MFPhysicianRefChk_No\">No</label>\n" +
                            "</div>\n";
                    break;
            }


            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("TitleBuffer", String.valueOf(TitleBuffer));
            Parser.SetField("FirstName", String.valueOf(FirstName));
            Parser.SetField("LastName", String.valueOf(LastName));
            Parser.SetField("MiddleInitial", String.valueOf(MiddleInitial));
            Parser.SetField("DOB", String.valueOf(DOB));
            Parser.SetField("Age", String.valueOf(Age));
            Parser.SetField("GenderBuffer", String.valueOf(GenderBuffer));
            Parser.SetField("Email", String.valueOf(Email));
            Parser.SetField("MaritalStatusBuffer", String.valueOf(MaritalStatusBuffer));
            Parser.SetField("AreaCode", String.valueOf(AreaCode));
            Parser.SetField("PhNumber", String.valueOf(PhNumber));
            Parser.SetField("DoctorsBuffer", String.valueOf(DoctorsBuffer));
            Parser.SetField("Address", String.valueOf(Address));
            Parser.SetField("City", String.valueOf(City));
            Parser.SetField("State", String.valueOf(State));
            Parser.SetField("County", String.valueOf(County));
            Parser.SetField("Country", String.valueOf(Country));
            Parser.SetField("ZipCode", String.valueOf(ZipCode));
            Parser.SetField("EthnicityBuffer", String.valueOf(EthnicityBuffer));
            Parser.SetField("RaceBuffer", String.valueOf(RaceBuffer));
            Parser.SetField("SSN", String.valueOf(SSN));
            Parser.SetField("ReasonVisitBuffer", String.valueOf(ReasonVisitBuffer));
            Parser.SetField("ReasonVisit", String.valueOf(ReasonVisit));

            Parser.SetField("SympChkCOVIDBuffer", String.valueOf(SympChkCOVIDBuffer));
            Parser.SetField("DateSympOnset", String.valueOf(DateSympOnset));
            Parser.SetField("SympFever", String.valueOf(SympFever));
            Parser.SetField("SympCough", String.valueOf(SympCough));
            Parser.SetField("SympShortBreath", String.valueOf(SympShortBreath));
            Parser.SetField("SympFatigue", String.valueOf(SympFatigue));
            Parser.SetField("SympMuscBodyAches", String.valueOf(SympMuscBodyAches));
            Parser.SetField("SympHeadache", String.valueOf(SympHeadache));
            Parser.SetField("SympLossTaste", String.valueOf(SympLossTaste));
            Parser.SetField("SympSoreThroat", String.valueOf(SympSoreThroat));
            Parser.SetField("SympCongestionRunNos", String.valueOf(SympCongestionRunNos));
            Parser.SetField("SympNauseaVomit", String.valueOf(SympNauseaVomit));
            Parser.SetField("SympDiarrhea", String.valueOf(SympDiarrhea));
            Parser.SetField("SympPerPainChest", String.valueOf(SympPerPainChest));
            Parser.SetField("SympNewConfusion", String.valueOf(SympNewConfusion));
            Parser.SetField("SympInabWake", String.valueOf(SympInabWake));
            Parser.SetField("SympOthers", String.valueOf(SympOthers));
            Parser.SetField("SympOthersTxt", String.valueOf(SympOthersTxt));
            Parser.SetField("EmpHealthChkBuffer", String.valueOf(EmpHealthChkBuffer));
            Parser.SetField("PregChkBuffer", String.valueOf(PregChkBuffer));
            Parser.SetField("TestForTravelChkBuffer", String.valueOf(TestForTravelChkBuffer));

            Parser.SetField("EmploymentBuffer", String.valueOf(EmploymentBuffer));
            Parser.SetField("Employer", String.valueOf(Employer));
            Parser.SetField("Occupation", String.valueOf(Occupation));
            Parser.SetField("EmpContact", String.valueOf(EmpContact));
            Parser.SetField("PriCarPhyBuffer", String.valueOf(PriCarPhyBuffer));
            Parser.SetField("PriCarePhy", String.valueOf(PriCarePhy));
            Parser.SetField("PriCarePhyAddress", String.valueOf(PriCarePhyAddress));
            Parser.SetField("PriCarePhyCity", String.valueOf(PriCarePhyCity));
            Parser.SetField("PriCarePhyState", String.valueOf(PriCarePhyState));
            Parser.SetField("PriCarePhyZipCode", String.valueOf(PriCarePhyZipCode));
            Parser.SetField("PatinetMinorBuffer", String.valueOf(PatinetMinorBuffer));
            Parser.SetField("GuarantorBuffer", String.valueOf(GuarantorBuffer));
            Parser.SetField("GuarantorEmployer", String.valueOf(GuarantorEmployer));
            Parser.SetField("GuarantorEmployerAreaCode", String.valueOf(GuarantorEmployerAreaCode));
            Parser.SetField("GuarantorEmployerPhNumber", String.valueOf(GuarantorEmployerPhNumber));
            Parser.SetField("GuarantorEmployerAddress", String.valueOf(GuarantorEmployerAddress));
            Parser.SetField("GuarantorEmployerCity", String.valueOf(GuarantorEmployerCity));
            Parser.SetField("GuarantorEmployerState", String.valueOf(GuarantorEmployerState));
            Parser.SetField("GuarantorEmployerZipCode", String.valueOf(GuarantorEmployerZipCode));
            Parser.SetField("WorkCompBuffer", String.valueOf(WorkCompBuffer));
            Parser.SetField("WCPDateofInjury", String.valueOf(WCPDateofInjury));
            Parser.SetField("WCPCaseNo", String.valueOf(WCPCaseNo));
            Parser.SetField("WCPGroupNo", String.valueOf(WCPGroupNo));
            Parser.SetField("WCPMemberId", String.valueOf(WCPMemberId));
            Parser.SetField("WCPInjuryRelatedAutoMotorAccident", String.valueOf(WCPInjuryRelatedAutoMotorAccident));
            Parser.SetField("WCPInjuryRelatedWorkRelated", String.valueOf(WCPInjuryRelatedWorkRelated));
            Parser.SetField("WCPInjuryRelatedOtherAccident", String.valueOf(WCPInjuryRelatedOtherAccident));
            Parser.SetField("WCPInjuryRelatedNoAccident", String.valueOf(WCPInjuryRelatedNoAccident));
            Parser.SetField("WCPInjuryOccurVehicle", String.valueOf(WCPInjuryOccurVehicle));
            Parser.SetField("WCPInjuryOccurWork", String.valueOf(WCPInjuryOccurWork));
            Parser.SetField("WCPInjuryOccurHome", String.valueOf(WCPInjuryOccurHome));
            Parser.SetField("WCPInjuryOccurOther", String.valueOf(WCPInjuryOccurOther));
            Parser.SetField("WCPInjuryDescription", String.valueOf(WCPInjuryDescription));
            Parser.SetField("WCPHRFirstName", String.valueOf(WCPHRFirstName));
            Parser.SetField("WCPHRLastName", String.valueOf(WCPHRLastName));
            Parser.SetField("WCPHRAreaCode", String.valueOf(WCPHRAreaCode));
            Parser.SetField("WCPHRPhoneNumber", String.valueOf(WCPHRPhoneNumber));
            Parser.SetField("WCPHRAddress", String.valueOf(WCPHRAddress));
            Parser.SetField("WCPHRCity", String.valueOf(WCPHRCity));
            Parser.SetField("WCPHRState", String.valueOf(WCPHRState));
            Parser.SetField("WCPHRZipCode", String.valueOf(WCPHRZipCode));
            Parser.SetField("WCPPlanName", String.valueOf(WCPPlanName));
            Parser.SetField("WCPCarrierName", String.valueOf(WCPCarrierName));
            Parser.SetField("WCPPayerAreaCode", String.valueOf(WCPPayerAreaCode));
            Parser.SetField("WCPPayerPhoneNumber", String.valueOf(WCPPayerPhoneNumber));
            Parser.SetField("WCPCarrierAddress", String.valueOf(WCPCarrierAddress));
            Parser.SetField("WCPCarrierCity", String.valueOf(WCPCarrierCity));
            Parser.SetField("WCPCarrierState", String.valueOf(WCPCarrierState));
            Parser.SetField("WCPCarrierZipCode", String.valueOf(WCPCarrierZipCode));
            Parser.SetField("WCPAdjudicatorFirstName", String.valueOf(WCPAdjudicatorFirstName));
            Parser.SetField("WCPAdjudicatorLastName", String.valueOf(WCPAdjudicatorLastName));
            Parser.SetField("WCPAdjudicatorAreaCode", String.valueOf(WCPAdjudicatorAreaCode));
            Parser.SetField("WCPAdjudicatorPhoneNumber", String.valueOf(WCPAdjudicatorPhoneNumber));
            Parser.SetField("WCPAdjudicatorFaxAreaCode", String.valueOf(WCPAdjudicatorFaxAreaCode));
            Parser.SetField("WCPAdjudicatorFaxPhoneNumber", String.valueOf(WCPAdjudicatorFaxPhoneNumber));
            Parser.SetField("MotorVehicleBuffer", String.valueOf(MotorVehicleBuffer));
            Parser.SetField("AutoInsuranceBuffer", String.valueOf(AutoInsuranceBuffer));
            Parser.SetField("AIIDateofAccident", String.valueOf(AIIDateofAccident));
            Parser.SetField("AIIAutoClaim", String.valueOf(AIIAutoClaim));
            Parser.SetField("AIIAccidentLocationAddress", String.valueOf(AIIAccidentLocationAddress));
            Parser.SetField("AIIAccidentLocationCity", String.valueOf(AIIAccidentLocationCity));
            Parser.SetField("AIIAccidentLocationState", String.valueOf(AIIAccidentLocationState));
            Parser.SetField("AIIAccidentLocationZipCode", String.valueOf(AIIAccidentLocationZipCode));
            Parser.SetField("AIIRoleInAccidentBuffer", String.valueOf(AIIRoleInAccidentBuffer));
            Parser.SetField("AIITypeOfAutoIOnsurancePolicyBuffer", String.valueOf(AIITypeOfAutoIOnsurancePolicyBuffer));
            Parser.SetField("AIIPrefixforReponsibleParty", String.valueOf(AIIPrefixforReponsibleParty));
            Parser.SetField("AIIFirstNameforReponsibleParty", String.valueOf(AIIFirstNameforReponsibleParty));
            Parser.SetField("AIILastNameforReponsibleParty", String.valueOf(AIILastNameforReponsibleParty));
            Parser.SetField("AIIMiddleNameforReponsibleParty", String.valueOf(AIIMiddleNameforReponsibleParty));
            Parser.SetField("AIISuffixforReponsibleParty", String.valueOf(AIISuffixforReponsibleParty));
            Parser.SetField("AIICarrierResponsibleParty", String.valueOf(AIICarrierResponsibleParty));
            Parser.SetField("AIICarrierResponsiblePartyAddress", String.valueOf(AIICarrierResponsiblePartyAddress));
            Parser.SetField("AIICarrierResponsiblePartyCity", String.valueOf(AIICarrierResponsiblePartyCity));
            Parser.SetField("AIICarrierResponsiblePartyState", String.valueOf(AIICarrierResponsiblePartyState));
            Parser.SetField("AIICarrierResponsiblePartyZipCode", String.valueOf(AIICarrierResponsiblePartyZipCode));
            Parser.SetField("AIICarrierResponsiblePartyAreaCode", String.valueOf(AIICarrierResponsiblePartyAreaCode));
            Parser.SetField("AIICarrierResponsiblePartyPhoneNumber", String.valueOf(AIICarrierResponsiblePartyPhoneNumber));
            Parser.SetField("AIICarrierResponsiblePartyPolicyNumber", String.valueOf(AIICarrierResponsiblePartyPolicyNumber));
            Parser.SetField("AIIResponsiblePartyAutoMakeModel", String.valueOf(AIIResponsiblePartyAutoMakeModel));
            Parser.SetField("AIIResponsiblePartyLicensePlate", String.valueOf(AIIResponsiblePartyLicensePlate));
            Parser.SetField("AIIFirstNameOfYourPolicyHolder", String.valueOf(AIIFirstNameOfYourPolicyHolder));
            Parser.SetField("AIILastNameOfYourPolicyHolder", String.valueOf(AIILastNameOfYourPolicyHolder));
            Parser.SetField("AIINameAutoInsuranceOfYourVehicle", String.valueOf(AIINameAutoInsuranceOfYourVehicle));
            Parser.SetField("AIIYourInsuranceAddress", String.valueOf(AIIYourInsuranceAddress));
            Parser.SetField("AIIYourInsuranceCity", String.valueOf(AIIYourInsuranceCity));
            Parser.SetField("AIIYourInsuranceState", String.valueOf(AIIYourInsuranceState));
            Parser.SetField("AIIYourInsuranceZipCode", String.valueOf(AIIYourInsuranceZipCode));
            Parser.SetField("AIIYourInsuranceAreaCode", String.valueOf(AIIYourInsuranceAreaCode));
            Parser.SetField("AIIYourInsurancePhoneNumber", String.valueOf(AIIYourInsurancePhoneNumber));
            Parser.SetField("AIIYourInsurancePolicyNo", String.valueOf(AIIYourInsurancePolicyNo));
            Parser.SetField("AIIYourLicensePlate", String.valueOf(AIIYourLicensePlate));
            Parser.SetField("AIIYourCarMakeModelYear", String.valueOf(AIIYourCarMakeModelYear));
            Parser.SetField("HealthInsuranceBuffer", String.valueOf(HealthInsuranceBuffer));
            Parser.SetField("GovtFundedInsuranceBuffer", String.valueOf(GovtFundedInsuranceBuffer));
            Parser.SetField("GFIPMedicare", String.valueOf(GFIPMedicare));
            Parser.SetField("GFIPMedicaid", String.valueOf(GFIPMedicaid));
            Parser.SetField("GFIPCHIP", String.valueOf(GFIPCHIP));
            Parser.SetField("GFIPTricare", String.valueOf(GFIPTricare));
            Parser.SetField("GFIPVHA", String.valueOf(GFIPVHA));
            Parser.SetField("GFIPIndianHealth", String.valueOf(GFIPIndianHealth));
            Parser.SetField("InsuranceSubPatient", String.valueOf(InsuranceSubPatient));
            Parser.SetField("InsuranceSubGuarantor", String.valueOf(InsuranceSubGuarantor));
            Parser.SetField("InsuranceSubOther", String.valueOf(InsuranceSubOther));
            Parser.SetField("HIPrimaryInsuranceBuff", String.valueOf(HIPrimaryInsuranceBuff));
            Parser.SetField("HISubscriberFirstName", String.valueOf(HISubscriberFirstName));
            Parser.SetField("HISubscriberLastName", String.valueOf(HISubscriberLastName));
            Parser.SetField("HISubscriberDOB", String.valueOf(HISubscriberDOB));
            Parser.SetField("HISubscriberSSN", String.valueOf(HISubscriberSSN));
            Parser.SetField("HISubscriberRelationtoPatientBuffer", String.valueOf(HISubscriberRelationtoPatientBuffer));
            Parser.SetField("HISubscriberGroupNo", String.valueOf(HISubscriberGroupNo));
            Parser.SetField("HISubscriberPolicyNo", String.valueOf(HISubscriberPolicyNo));
            Parser.SetField("SecondHealthInsuranceBuffer", String.valueOf(SecondHealthInsuranceBuffer));
            Parser.SetField("SHISecondaryNameBuffer", String.valueOf(SHISecondaryNameBuffer));
            Parser.SetField("SHISubscriberFirstName", String.valueOf(SHISubscriberFirstName));
            Parser.SetField("SHISubscriberLastName", String.valueOf(SHISubscriberLastName));
            Parser.SetField("SHISubscriberDOB", String.valueOf(""));
            Parser.SetField("SHISubscriberRelationtoPatientBuffer", String.valueOf(SHISubscriberRelationtoPatientBuffer));
            Parser.SetField("SHISubscriberGroupNo", String.valueOf(SHISubscriberGroupNo));
            Parser.SetField("SHISubscriberPolicyNo", String.valueOf(SHISubscriberPolicyNo));

            Parser.SetField("MFReturnPat", String.valueOf(MFReturnPat));
            Parser.SetField("MFFirstVisit", String.valueOf(MFFirstVisit));
            Parser.SetField("MFInternetFind", String.valueOf(MFInternetFind));
            Parser.SetField("Facebook", String.valueOf(Facebook));
            Parser.SetField("MapSearch", String.valueOf(MapSearch));
            Parser.SetField("GoogleSearch", String.valueOf(GoogleSearch));
            Parser.SetField("VERWebsite", String.valueOf(VERWebsite));
            Parser.SetField("WebsiteAds", String.valueOf(WebsiteAds));
            Parser.SetField("OnlineAdvertisements", String.valueOf(OnlineAdvertisements));
            Parser.SetField("OnlineReviews", String.valueOf(OnlineReviews));
            Parser.SetField("Twitter", String.valueOf(Twitter));
            Parser.SetField("LinkedIn", String.valueOf(LinkedIn));
            Parser.SetField("EmailBlast", String.valueOf(EmailBlast));
            Parser.SetField("YouTube", String.valueOf(YouTube));
            Parser.SetField("TV", String.valueOf(TV));
            Parser.SetField("Billboard", String.valueOf(Billboard));
            Parser.SetField("Radio", String.valueOf(Radio));
            Parser.SetField("Brochure", String.valueOf(Brochure));
            Parser.SetField("DirectMail", String.valueOf(DirectMail));
            Parser.SetField("CitizensDeTar", String.valueOf(CitizensDeTar));
            Parser.SetField("LiveWorkNearby", String.valueOf(LiveWorkNearby));
            Parser.SetField("FamilyFriend", String.valueOf(FamilyFriend));
            Parser.SetField("FamilyFriend_text", String.valueOf(FamilyFriend_text));
            Parser.SetField("UrgentCare", String.valueOf(UrgentCare));
            Parser.SetField("UrgentCare_text", String.valueOf(UrgentCare_text));
            Parser.SetField("NewspaperMagazine", String.valueOf(NewspaperMagazine));
            Parser.SetField("NewspaperMagazine_text", String.valueOf(NewspaperMagazine_text));
            Parser.SetField("School", String.valueOf(School));
            Parser.SetField("School_text", String.valueOf(School_text));
            Parser.SetField("Hotel", String.valueOf(Hotel));
            Parser.SetField("Hotel_text", String.valueOf(Hotel_text));
            Parser.SetField("EmployerSentMe", String.valueOf(EmployerSentMe));
            Parser.SetField("EmployerSentMe_text", String.valueOf(EmployerSentMe_text));
            Parser.SetField("PatientCell", String.valueOf(PatientCell));
            Parser.SetField("RecInitial", String.valueOf(RecInitial));
            Parser.SetField("MFPhysicianRefChk", String.valueOf(MFPhysicianRefChk));
            Parser.SetField("MFPhysician", String.valueOf(MFPhysician));

            Parser.SetField("MRN", String.valueOf(MRN));
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.SetField("Style", String.valueOf(Style));
            Parser.SetField("IDFront", String.valueOf(IDFront));
            Parser.SetField("InsuranceFront", String.valueOf(InsuranceFront));
            Parser.SetField("InsuranceBack", String.valueOf(InsuranceBack));


            if (ClientId == 9 || ClientId == 38) {
                Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/md/Forms/Edit/PatientRegFormVictoria_2_Edit.html");
            } else if (ClientId == 28) {
                Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/md/Forms/Edit/PatientRegFormERDallas_Edit.html");
            }
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in PatientReg ** (EditValues Main Catch ^^ ##MES#008)", servletContext, ex, "PatientReg2", "EditValues", conn);
            Services.DumException("EditValues^^ ##MES#008", "PatientReg2", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard_copy");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#008");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

        }
    }

    void EditSave(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, UtilityHelper helper) throws FileNotFoundException {
        PreparedStatement pStmt = null;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Database = "";
        final String MRN = request.getParameter("MRN").trim();
        final int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
        int PatientRegId = 0;
        final String Date = "";
        final int ClientIndex = 0;
        String Title = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String DOB = "";
        String Age = "";
        String gender = "";
        String Email = "";
        String ConfirmEmail = "";
        String MaritalStatus = "";
        String AreaCode = "";
        String PhNumber = "";
        String Address = "";
        String Address2 = "";
        String City = "";
        String State = "";
        String Country = "";
        String County = "";
        String ZipCode = "";
        String Ethnicity = "";
        String Race = "";
        String SSN = "";
        final String DoctorId = "";
        final String DOS = "";
        String EmployementChk = "0";
        String Employer = "";
        String Occupation = "";
        String EmpContact = "";
        String DoctorName = "";
        String PrimaryCarePhysicianChk = "0";
        String PriCarePhy = "";
        String ReasonVisit = "";

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
        String PriCarePhyCity = "";
        String PriCarePhyState = "";
        String PriCarePhyZipCode = "";
        String PatientMinorChk = "0";
        String GuarantorChk = "0";
        String GuarantorEmployer = "";
        String GuarantorEmployerAreaCode = "";
        String GuarantorEmployerPhNumber = "";
        String GuarantorEmployerAddress = "";
        String GuarantorEmployerAddress2 = "";
        String GuarantorEmployerCity = "";
        String GuarantorEmployerState = "";
        String GuarantorEmployerZipCode = "";
        String WorkersCompPolicyChk = "0";
        String WCPDateofInjury = "";
        String WCPCaseNo = "";
        String WCPGroupNo = "";
        String WCPMemberId = "";
        String WCPInjuryRelatedAutoMotorAccident = "0";
        String WCPInjuryRelatedWorkRelated = "0";
        String WCPInjuryRelatedOtherAccident = "0";
        String WCPInjuryRelatedNoAccident = "0";
        String WCPInjuryOccurVehicle = "0";
        String WCPInjuryOccurWork = "0";
        String WCPInjuryOccurHome = "0";
        String WCPInjuryOccurOther = "0";
        String WCPInjuryDescription = "";
        String WCPHRFirstName = "";
        String WCPHRLastName = "";
        String WCPHRAreaCode = "";
        String WCPHRPhoneNumber = "";
        String WCPHRAddress = "";
        String WCPHRAddress2 = "";
        String WCPHRCity = "";
        String WCPHRState = "";
        String WCPHRZipCode = "";
        String WCPPlanName = "";
        String WCPCarrierName = "";
        String WCPPayerAreaCode = "";
        String WCPPayerPhoneNumber = "";
        String WCPCarrierAddress = "";
        String WCPCarrierAddress2 = "";
        String WCPCarrierCity = "";
        String WCPCarrierState = "";
        String WCPCarrierZipCode = "";
        String WCPAdjudicatorFirstName = "";
        String WCPAdjudicatorLastName = "";
        String WCPAdjudicatorAreaCode = "";
        String WCPAdjudicatorPhoneNumber = "";
        String WCPAdjudicatorFaxAreaCode = "";
        String WCPAdjudicatorFaxPhoneNumber = "";
        String MotorVehicleAccidentChk = "0";
        String AutoInsuranceInformationChk = "0";
        String AIIDateofAccident = "";
        String AIIAutoClaim = "";
        String AIIAccidentLocationAddress = "";
        String AIIAccidentLocationAddress2 = "";
        String AIIAccidentLocationCity = "";
        String AIIAccidentLocationState = "";
        String AIIAccidentLocationZipCode = "";
        String AIIRoleInAccident = "";
        String AIITypeOfAutoIOnsurancePolicy = "";
        String AIIPrefixforReponsibleParty = "";
        String AIIFirstNameforReponsibleParty = "";
        String AIIMiddleNameforReponsibleParty = "";
        String AIILastNameforReponsibleParty = "";
        String AIISuffixforReponsibleParty = "";
        String AIICarrierResponsibleParty = "";
        String AIICarrierResponsiblePartyAddress = "";
        String AIICarrierResponsiblePartyAddress2 = "";
        String AIICarrierResponsiblePartyCity = "";
        String AIICarrierResponsiblePartyState = "";
        String AIICarrierResponsiblePartyZipCode = "";
        String AIICarrierResponsiblePartyAreaCode = "";
        String AIICarrierResponsiblePartyPhoneNumber = "";
        String AIICarrierResponsiblePartyPolicyNumber = "";
        String AIIResponsiblePartyAutoMakeModel = "";
        String AIIResponsiblePartyLicensePlate = "";
        String AIIFirstNameOfYourPolicyHolder = "";
        String AIILastNameOfYourPolicyHolder = "";
        String AIINameAutoInsuranceOfYourVehicle = "";
        String AIIYourInsuranceAddress = "";
        String AIIYourInsuranceAddress2 = "";
        String AIIYourInsuranceCity = "";
        String AIIYourInsuranceState = "";
        String AIIYourInsuranceZipCode = "";
        String AIIYourInsuranceAreaCode = "";
        String AIIYourInsurancePhoneNumber = "";
        String AIIYourInsurancePolicyNo = "";
        String AIIYourLicensePlate = "";
        String AIIYourCarMakeModelYear = "";
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
        String HIPrimaryInsurance = "";
        String HISubscriberFirstName = "";
        String HISubscriberLastName = "";
        String HISubscriberDOB = "";
        String HISubscriberSSN = "";
        String HISubscriberRelationtoPatient = "";
        String HISubscriberGroupNo = "";
        String HISubscriberPolicyNo = "";
        String SecondHealthInsuranceChk = "";
        String SHISecondaryName = "";
        String SHISubscriberFirstName = "";
        String SHISubscriberLastName = "";
        String SHISubscriberDOB = "";
        String SHISubscriberRelationtoPatient = "";
        String SHISubscriberGroupNo = "";
        String SHISubscriberPolicyNo = "";

        String MFReturnPat = "";
        String MFFirstVisit = "";
        String MFInternetFind = "";
        String Facebook = "";
        String MapSearch = "";
        String GoogleSearch = "";
        String VERWebsite = "";
        String WebsiteAds = "";
        String OnlineAdvertisements = "";
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
        String EmployerSentMe = "";
        String EmployerSentMe_text = "";
        String PatientCell = "";
        String RecInitial = "";
        String MFPhysicianRefChk = "";
        String MFPhysician = "";

        final StringBuffer TitleBuffer = new StringBuffer();
        final StringBuffer GenderBuffer = new StringBuffer();
        final StringBuffer MaritalStatusBuffer = new StringBuffer();
        final StringBuffer EthnicityBuffer = new StringBuffer();
        final StringBuffer ReasonVisitBuffer = new StringBuffer();
        final StringBuffer EmploymentBuffer = new StringBuffer();
        final StringBuffer PriCarPhyBuffer = new StringBuffer();
        final StringBuffer PatinetMinorBuffer = new StringBuffer();
        final StringBuffer GuarantorBuffer = new StringBuffer();
        final StringBuffer WorkCompBuffer = new StringBuffer();
        final StringBuffer InjuryorIllnesRelatedBuffer = new StringBuffer();
        final StringBuffer InjuryOccurBuffer = new StringBuffer();
        final StringBuffer MotorVehicleBuffer = new StringBuffer();
        final StringBuffer AutoInsuranceBuffer = new StringBuffer();
        final StringBuffer HealthInsuranceBuffer = new StringBuffer();
        final StringBuffer GovtFundedInsuranceBuffer = new StringBuffer();
        final StringBuffer GovtPlanBuffer = new StringBuffer();
        final StringBuffer InsuranceSubscriberBuffer = new StringBuffer();
        final StringBuffer SecondHealthInsuranceBuffer = new StringBuffer();
        final StringBuffer AIITypeOfAutoIOnsurancePolicyBuffer = new StringBuffer();
        final StringBuffer AIIRoleInAccidentBuffer = new StringBuffer();
        final StringBuffer SHISubscriberRelationtoPatientBuffer = new StringBuffer();
        final StringBuffer SHISecondaryNameBuffer = new StringBuffer();
        final StringBuffer HISubscriberRelationtoPatientBuffer = new StringBuffer();
        int WorkCompFound = 0;
        int MotorVehFound = 0;
        int HealthInsFound = 0;
        String facilityName = helper.getFacilityName(request, conn, servletContext, ClientId);
        try {
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
                if (request.getParameter("DOB") == null) {
                    DOB = "0000-00-00";
                } else {
                    DOB = request.getParameter("DOB").trim();
//                    DOB = String.valueOf(String.valueOf(DOB.substring(6, 10))) + "-" + DOB.substring(0, 2) + "-" + DOB.substring(3, 5);
                }
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
                if (request.getParameter("DoctorName") == null) {
                    DoctorName = "";
                } else {
                    DoctorName = request.getParameter("DoctorName").trim();
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
                    /*if (DateSympOnset.length() > 0)
                        //DateSympOnset = DateSympOnset.substring(6, 10) + "-" + DateSympOnset.substring(0, 2) + "-" + DateSympOnset.substring(3, 5);
                        DateSympOnset = request.getParameter("DateSympOnset").trim();
                    else
                        DateSympOnset = "0000-00-00";*/
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
//                        WCPDateofInjury = String.valueOf(String.valueOf(WCPDateofInjury.substring(6, 10))) + "-" + WCPDateofInjury.substring(0, 2) + "-" + WCPDateofInjury.substring(3, 5);
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
//                            AIIDateofAccident = String.valueOf(String.valueOf(AIIDateofAccident.substring(6, 10))) + "-" + AIIDateofAccident.substring(0, 2) + "-" + AIIDateofAccident.substring(3, 5);
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
                        if (request.getParameter("HISubscriberDOB").length() > 0) {
                            HISubscriberDOB = request.getParameter("HISubscriberDOB").trim();
                        } else {
                            HISubscriberDOB = "0000-00-00";
                        }
                        //HISubscriberDOB = String.valueOf(String.valueOf(HISubscriberDOB.substring(6, 10))) + "-" + HISubscriberDOB.substring(0, 2) + "-" + HISubscriberDOB.substring(3, 5);
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
                            if (request.getParameter("SHISubscriberDOB").length() > 0) {
                                SHISubscriberDOB = request.getParameter("SHISubscriberDOB").trim();
                            } else {
                                SHISubscriberDOB = "0000-00-00";
                            }
                            //SHISubscriberDOB = String.valueOf(String.valueOf(SHISubscriberDOB.substring(6, 10))) + "-" + SHISubscriberDOB.substring(0, 2) + "-" + SHISubscriberDOB.substring(3, 5);
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
                if (request.getParameter("OnlineAdvertisements") == null) {
                    OnlineAdvertisements = "0";
                } else {
                    OnlineAdvertisements = "1";
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
                if (request.getParameter("EmployerSentMe") == null) {
                    EmployerSentMe = "0";
                } else {
                    EmployerSentMe = "1";
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
                if (request.getParameter("EmployerSentMe_text") == null) {
                    EmployerSentMe_text = "";
                } else {
                    EmployerSentMe_text = request.getParameter("EmployerSentMe_text");
                }
                if (request.getParameter("MFPhysicianRefChk") == null) {
                    MFPhysicianRefChk = "0";
                } else {
                    MFPhysicianRefChk = request.getParameter("MFPhysicianRefChk").trim();
                }
                if (request.getParameter("MFPhysician") == null) {
                    MFPhysician = "";
                } else {
                    MFPhysician = request.getParameter("MFPhysician");
                }
                if (request.getParameter("PatientCell") == null) {
                    PatientCell = "";
                } else {
                    PatientCell = request.getParameter("PatientCell").trim();
                }
                if (request.getParameter("RecInitial") == null) {
                    RecInitial = "";
                } else {
                    RecInitial = request.getParameter("RecInitial").trim();
                }


            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (EditSave_New PatineReg Data get ^^" + facilityName + " ##MES#001)", servletContext, e, "PatientReg2", "EditSave_New", conn);
                Services.DumException("PatientReg2", "EditValueError in request getParameter", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "EditValues&MRN=" + MRN + "&ClientIndex=" + ClientId);
                Parser.SetField("Message", "MES#001");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }
            try {
                Query = "Select dbname from oe.clients where Id = " + ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    Database = rset.getString(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            try {
                Query = "Select ID from " + Database + ".PatientReg where MRN = '" + MRN + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    PatientRegId = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            try {
/*                Query = "Update " + Database + ".PatientReg Set FirstName = '" + FirstName.toUpperCase() + "', " +
                        "LastName = '" + LastName.toUpperCase() + "', MiddleInitial = '" + MiddleInitial.toUpperCase() + "', DOB = '" + DOB + "'," +
                        "Age = " + Age + ", Gender = '" + gender + "', Email = '" + Email + "', PhNumber = '" + AreaCode + PhNumber + "', " +
                        "Address = '" + Address + Address2 + "', City = '" + City.toUpperCase() + "', State = '" + State.toUpperCase() + "'," +
                        "Country = '" + Country.toUpperCase() + "', ZipCode = '" + ZipCode + "',SSN = '" + SSN + "'," +
                        "Occupation = '" + Occupation + "', Employer = '" + Employer + "', EmpContact = '" + EmpContact + "', " +
                        "PriCarePhy = '" + PriCarePhy + "',ReasonVisit = '" + ReasonVisit + "' , Title = '" + Title.toUpperCase() + "'," +
                        "MaritalStatus = '" + MaritalStatus.toUpperCase() + "',  DoctorsName = '" + DoctorName + "' ," +
                        "County = '" + County + "',VIewDate=NOW() " +
                        "where ID = " + PatientRegId;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();*/
                pStmt = conn.prepareStatement("Update " + Database + ".PatientReg Set FirstName = ?,LastName = ?, MiddleInitial = ?, DOB = ?," +
                        "Age = ?, Gender = ?, Email = ?, PhNumber = ?, Address = ?, City = ?, State = ?," +
                        "Country = ?, ZipCode = ?,SSN = ?,Occupation = ?, Employer = ?, EmpContact = ?, " +
                        "PriCarePhy = ?,ReasonVisit = ? , Title = ?,MaritalStatus = ?,  DoctorsName = ? ,County = ?,VIewDate=NOW() " +
                        "where ID = ?");
                pStmt.setString(1, FirstName.toUpperCase());
                pStmt.setString(2, LastName.toUpperCase());
                pStmt.setString(3, MiddleInitial.toUpperCase());
                pStmt.setString(4, DOB);
                pStmt.setString(5, Age);
                pStmt.setString(6, gender);
                pStmt.setString(7, Email);
                pStmt.setString(8, AreaCode + PhNumber);
                pStmt.setString(9, Address + Address2);
                pStmt.setString(10, City.toUpperCase());
                pStmt.setString(11, State.toUpperCase());
                pStmt.setString(12, Country.toUpperCase());
                pStmt.setString(13, ZipCode);
                pStmt.setString(14, SSN);
                pStmt.setString(15, Occupation);
                pStmt.setString(16, Employer);
                pStmt.setString(17, EmpContact);
                pStmt.setString(18, PriCarePhy);
                pStmt.setString(19, ReasonVisit);
                pStmt.setString(20, Title.toUpperCase());
                pStmt.setString(21, MaritalStatus.toUpperCase());
                pStmt.setString(22, DoctorName);
                pStmt.setString(23, County);
                pStmt.setInt(24, PatientRegId);
//                System.out.println("PAT REG  " + pStmt.toString());
                pStmt.executeUpdate();
                pStmt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (EditSave Updating PatientReg Table ^^" + facilityName + " ##MES#002)", servletContext, e, "PatientReg2", "EditSave", conn);
                Services.DumException("PatientReg2", "EditSave Updating PatientReg Table", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "EditValues&MRN=" + MRN + "&ClientIndex=" + ClientId);
                Parser.SetField("Message", "MES#002");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }
            try {
/*                Query = " Update " + Database + ".PatientReg_Details Set Ethnicity = '" + Ethnicity + "', Race = '" + Race + "', " +
                        " EmployementChk = '" + EmployementChk + "', Employer = '" + Employer + "', Occupation = '" + Occupation + "', " +
                        " EmpContact = '" + EmpContact + "', PrimaryCarePhysicianChk = '" + PrimaryCarePhysicianChk + "', " +
                        " PriCarePhy = '" + PriCarePhy + "', ReasonVisit = '" + ReasonVisit + "', PriCarePhyAddress = '" + PriCarePhyAddress + PriCarePhyAddress2 + "', " +
                        " PriCarePhyCity = '" + PriCarePhyCity + "', PriCarePhyState = '" + PriCarePhyState + "',PriCarePhyZipCode = '" + PriCarePhyZipCode + "'," +
                        " PatientMinorChk = '" + PatientMinorChk + "',GuarantorChk = '" + GuarantorChk + "',GuarantorEmployer = '" + GuarantorEmployer + "', " +
                        " GuarantorEmployerPhNumber = '" + GuarantorEmployerAreaCode + GuarantorEmployerPhNumber + "'," +
                        " GuarantorEmployerAddress = '" + GuarantorEmployerAddress + GuarantorEmployerAddress2 + "'," +
                        " GuarantorEmployerCity = '" + GuarantorEmployerCity + "' ,GuarantorEmployerState = '" + GuarantorEmployerState + "'," +
                        " GuarantorEmployerZipCode = '" + GuarantorEmployerZipCode + "', WorkersCompPolicyChk = '" + WorkersCompPolicyChk + "' ," +
                        " MotorVehicleAccidentChk = '" + MotorVehicleAccidentChk + "', HealthInsuranceChk = '" + HealthInsuranceChk + "', " +
                        " SympChkCOVID = '" + SympChkCOVID + "', DateSympOnset = '" + DateSympOnset + "', SympFever = '" + SympFever + "', SympCough = '" + SympCough + "', " +
                        " SympShortBreath = '" + SympShortBreath + "', SympFatigue = '" + SympFatigue + "', SympMuscBodyAches = '" + SympMuscBodyAches + "', " +
                        " SympHeadache = '" + SympHeadache + "', SympLossTaste = '" + SympLossTaste + "', SympSoreThroat = '" + SympSoreThroat + "', " +
                        " SympCongestionRunNos = '" + SympCongestionRunNos + "', SympNauseaVomit = '" + SympNauseaVomit + "', SympDiarrhea = '" + SympDiarrhea + "', " +
                        " SympPerPainChest = '" + SympPerPainChest + "', SympNewConfusion = '" + SympNewConfusion + "', SympInabWake = '" + SympInabWake + "', " +
                        " SympOthers = '" + SympOthers + "', SympOthersTxt = '" + SympOthersTxt + "', EmpHealthChk = '" + EmpHealthChk + "', PregChk = '" + PregChk + "' " +
                        " where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();*/
                int _patRegDetailChk = 0;
                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    _patRegDetailChk = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (_patRegDetailChk > 0) {
                    pStmt = conn.prepareStatement(" Update " + Database + ".PatientReg_Details Set Ethnicity = ?, Race = ?, " +
                            " EmployementChk = ?, Employer = ?, Occupation = ?, EmpContact = ?, PrimaryCarePhysicianChk = ?, " +
                            " PriCarePhy = ?, ReasonVisit = ?, PriCarePhyAddress = ?, PriCarePhyCity = ?, PriCarePhyState = ?,PriCarePhyZipCode = ?," +
                            " PatientMinorChk = ?,GuarantorChk = ?,GuarantorEmployer = ?,GuarantorEmployerPhNumber = ?,GuarantorEmployerAddress = ?," +
                            " GuarantorEmployerCity = ? ,GuarantorEmployerState = ?,GuarantorEmployerZipCode = ?, WorkersCompPolicyChk = ? ," +
                            " MotorVehicleAccidentChk =?, HealthInsuranceChk = ?, SympChkCOVID = ?, DateSympOnset = ?, SympFever = ?, SympCough = ?, " +
                            " SympShortBreath =?, SympFatigue = ?, SympMuscBodyAches = ?,SympHeadache = ?, SympLossTaste = ?, SympSoreThroat = ?, " +
                            " SympCongestionRunNos = ?, SympNauseaVomit = ?, SympDiarrhea = ?, SympPerPainChest = ?, SympNewConfusion = ?, SympInabWake = ?, " +
                            " SympOthers = ?, SympOthersTxt = ?, EmpHealthChk = ?, PregChk = ? , TestForTravel = ? " +
                            " where PatientRegId = ? ");
                    pStmt.setString(1, Ethnicity);
                    pStmt.setString(2, Race);
                    pStmt.setString(3, EmployementChk);
                    pStmt.setString(4, Employer);
                    pStmt.setString(5, Occupation);
                    pStmt.setString(6, EmpContact);
                    pStmt.setString(7, PrimaryCarePhysicianChk);
                    pStmt.setString(8, PriCarePhy);
                    pStmt.setString(9, ReasonVisit);
                    pStmt.setString(10, PriCarePhyAddress + PriCarePhyAddress2);
                    pStmt.setString(11, PriCarePhyCity);
                    pStmt.setString(12, PriCarePhyState);
                    pStmt.setString(13, PriCarePhyZipCode);
                    pStmt.setString(14, PatientMinorChk);
                    pStmt.setString(15, GuarantorChk);
                    pStmt.setString(16, GuarantorEmployer);
                    pStmt.setString(17, GuarantorEmployerAreaCode + GuarantorEmployerPhNumber);
                    pStmt.setString(18, GuarantorEmployerAddress + GuarantorEmployerAddress2);
                    pStmt.setString(19, GuarantorEmployerCity);
                    pStmt.setString(20, GuarantorEmployerState);
                    pStmt.setString(21, GuarantorEmployerZipCode);
                    pStmt.setString(22, WorkersCompPolicyChk);
                    pStmt.setString(23, MotorVehicleAccidentChk);
                    pStmt.setString(24, HealthInsuranceChk);
                    pStmt.setString(25, SympChkCOVID);
                    //pStmt.setString(26, DateSympOnset);
                    if (!DateSympOnset.equals(""))
                        pStmt.setString(26, DateSympOnset);
                    else
                        pStmt.setNull(26, Types.DATE);
                    pStmt.setString(27, SympFever);
                    pStmt.setString(28, SympCough);
                    pStmt.setString(29, SympShortBreath);
                    pStmt.setString(30, SympFatigue);
                    pStmt.setString(31, SympMuscBodyAches);
                    pStmt.setString(32, SympHeadache);
                    pStmt.setString(33, SympLossTaste);
                    pStmt.setString(34, SympSoreThroat);
                    pStmt.setString(35, SympCongestionRunNos);
                    pStmt.setString(36, SympNauseaVomit);
                    pStmt.setString(37, SympDiarrhea);
                    pStmt.setString(38, SympPerPainChest);
                    pStmt.setString(39, SympNewConfusion);
                    pStmt.setString(40, SympInabWake);
                    pStmt.setString(41, SympOthers);
                    pStmt.setString(42, SympOthersTxt);
                    pStmt.setString(43, EmpHealthChk);
                    pStmt.setString(44, PregChk);
                    pStmt.setString(45, TestForTravel);
                    pStmt.setInt(46, PatientRegId);
//                    System.out.println("PAT REG DETAILS " + pStmt.toString());
                    pStmt.executeUpdate();
                    pStmt.close();
                } else {
                    final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".PatientReg_Details (PatientRegId,Ethnicity,Race," +
                            " EmployementChk,Employer,Occupation,EmpContact,PrimaryCarePhysicianChk,PriCarePhy,ReasonVisit,PriCarePhyAddress,PriCarePhyCity,PriCarePhyState," +
                            " PriCarePhyZipCode,PatientMinorChk,GuarantorChk,GuarantorEmployer,GuarantorEmployerPhNumber,GuarantorEmployerAddress,GuarantorEmployerCity," +
                            " GuarantorEmployerState,GuarantorEmployerZipCode,CreatedDate,WorkersCompPolicyChk,MotorVehicleAccidentChk,HealthInsuranceChk, SympChkCOVID, " +
                            " DateSympOnset, SympFever, SympCough, SympShortBreath, SympFatigue, SympMuscBodyAches, SympHeadache, SympLossTaste, SympSoreThroat, " +
                            " SympCongestionRunNos, SympNauseaVomit, SympDiarrhea, SympPerPainChest, SympNewConfusion, SympInabWake, SympOthers, SympOthersTxt," +
                            " EmpHealthChk, PregChk,TestForTravel) \n" +
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
                    MainReceipt.setString(11, String.valueOf(String.valueOf(PriCarePhyAddress)) + " " + PriCarePhyAddress2);
                    MainReceipt.setString(12, PriCarePhyCity);
                    MainReceipt.setString(13, PriCarePhyState);
                    MainReceipt.setString(14, PriCarePhyZipCode);
                    MainReceipt.setInt(15, Integer.parseInt(PatientMinorChk));
                    MainReceipt.setString(16, GuarantorChk);
                    MainReceipt.setString(17, GuarantorEmployer);
                    MainReceipt.setString(18, String.valueOf(String.valueOf(GuarantorEmployerAreaCode)) + GuarantorEmployerPhNumber);
                    MainReceipt.setString(19, String.valueOf(String.valueOf(GuarantorEmployerAddress)) + " " + GuarantorEmployerAddress2);
                    MainReceipt.setString(20, GuarantorEmployerCity);
                    MainReceipt.setString(21, GuarantorEmployerState);
                    MainReceipt.setString(22, GuarantorEmployerZipCode);
                    MainReceipt.setInt(23, Integer.parseInt(WorkersCompPolicyChk));
                    MainReceipt.setInt(24, Integer.parseInt(MotorVehicleAccidentChk));
                    MainReceipt.setInt(25, Integer.parseInt(HealthInsuranceChk));
                    MainReceipt.setInt(26, Integer.parseInt(SympChkCOVID));
                    //MainReceipt.setString(27, DateSympOnset);
                    if (!DateSympOnset.equals(""))
                        MainReceipt.setString(27, DateSympOnset);
                    else
                        MainReceipt.setNull(27, Types.DATE);
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
                }
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (EditSave Updating PatientRegDetails Table ^^" + facilityName + " ##MES#003)", servletContext, e, "PatientReg2", "EditSave", conn);
                Services.DumException("PatientReg2", "EditSave Updating PatientRegDetails Table", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "EditValues&MRN=" + MRN + "&ClientIndex=" + ClientId);
                Parser.SetField("Message", "MES#003");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }
            if (WorkersCompPolicyChk.equals("1")) {
                try {
                    Query = "Select COUNT(*) from " + Database + ".Patient_WorkCompPolicy where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        WorkCompFound = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();
                } catch (Exception e) {
                    helper.SendEmailWithAttachment("Error in PatientReg2 ** (EditSave Getting Count from  Patient_WorkCompPolicy Table ^^" + facilityName + " ##MES#004)", servletContext, e, "PatientReg2", "EditSave", conn);
                    Services.DumException("PatientReg2", "EditSave Getting Count from  Patient_WorkCompPolicy Table", request, e, this.getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg2");
                    Parser.SetField("ActionID", "EditValues&MRN=" + MRN + "&ClientIndex=" + ClientId);
                    Parser.SetField("Message", "MES#004");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    return;
                }
                try {
                    if (WorkCompFound > 0) {
/*                        Query = "Update " + Database + ".Patient_WorkCompPolicy Set WCPDateofInjury = '" + WCPDateofInjury + "', " +
                                "WCPCaseNo = '" + WCPCaseNo + "', WCPGroupNo = '" + WCPGroupNo + "', WCPMemberId = '" + WCPMemberId + "', " +
                                "WCPInjuryRelatedAutoMotorAccident = '" + WCPInjuryRelatedAutoMotorAccident + "', " +
                                "WCPInjuryRelatedWorkRelated = '" + WCPInjuryRelatedWorkRelated + "', " +
                                "WCPInjuryRelatedOtherAccident = '" + WCPInjuryRelatedOtherAccident + "', " +
                                "WCPInjuryRelatedNoAccident = '" + WCPInjuryRelatedNoAccident + "', " +
                                "WCPInjuryOccurVehicle = '" + WCPInjuryOccurVehicle + "', " +
                                "WCPInjuryOccurWork = '" + WCPInjuryOccurWork + "', WCPInjuryOccurHome = '" + WCPInjuryOccurHome + "', " +
                                "WCPInjuryOccurOther = '" + WCPInjuryOccurOther + "',WCPInjuryDescription = '" + WCPInjuryDescription + "'," +
                                "WCPHRFirstName = '" + WCPHRFirstName + "',WCPHRLastName = '" + WCPHRLastName + "'," +
                                "WCPHRPhoneNumber = '" + WCPHRAreaCode + WCPHRPhoneNumber + "', " +
                                "WCPHRAddress = '" + WCPHRAddress + WCPHRAddress2 + "',WCPHRCity = '" + WCPHRCity + "'," +
                                "WCPHRState = '" + WCPHRState + "' ,WCPHRZipCode = '" + WCPHRZipCode + "'," +
                                "WCPPlanName = '" + WCPPlanName + "', WCPCarrierName = '" + WCPCarrierName + "' ," +
                                "WCPPayerPhoneNumber = '" + WCPPayerAreaCode + WCPPayerPhoneNumber + "', " +
                                "WCPCarrierAddress = '" + WCPCarrierAddress + WCPCarrierAddress2 + "', " +
                                "WCPCarrierCity= '" + WCPCarrierCity + "', WCPCarrierState = '" + WCPCarrierState + "', " +
                                "WCPCarrierZipCode= '" + WCPCarrierZipCode + "', WCPAdjudicatorFirstName='" + WCPAdjudicatorFirstName + "'," +
                                "WCPAdjudicatorLastName= '" + WCPAdjudicatorLastName + "', " +
                                "WCPAdjudicatorPhoneNumber = '" + WCPAdjudicatorAreaCode + WCPAdjudicatorPhoneNumber + "', " +
                                "WCPAdjudicatorFaxPhoneNumber='" + WCPAdjudicatorFaxAreaCode + WCPAdjudicatorFaxPhoneNumber + "' " +
                                "where PatientRegId = " + PatientRegId;
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();*/
                        pStmt = conn.prepareStatement("Update " + Database + ".Patient_WorkCompPolicy Set WCPDateofInjury = ?, " +
                                "WCPCaseNo = ?, WCPGroupNo = ?, WCPMemberId = ?, WCPInjuryRelatedAutoMotorAccident = ?, " +
                                "WCPInjuryRelatedWorkRelated = ?,WCPInjuryRelatedOtherAccident = ?, WCPInjuryRelatedNoAccident = ?, " +
                                "WCPInjuryOccurVehicle = ?, WCPInjuryOccurWork = ?, WCPInjuryOccurHome = ?, " +
                                "WCPInjuryOccurOther = ?, WCPInjuryDescription = ?,WCPHRFirstName = ?,WCPHRLastName = ?," +
                                "WCPHRPhoneNumber = ?, WCPHRAddress =?,WCPHRCity = ?,WCPHRState = ? ,WCPHRZipCode = ?," +
                                "WCPPlanName = ?, WCPCarrierName = ? ,WCPPayerPhoneNumber = ?, WCPCarrierAddress = ?, " +
                                "WCPCarrierCity= ?, WCPCarrierState = ?, WCPCarrierZipCode=?, WCPAdjudicatorFirstName=?," +
                                "WCPAdjudicatorLastName= ?, WCPAdjudicatorPhoneNumber = ?, WCPAdjudicatorFaxPhoneNumber=? " +
                                "where PatientRegId = ? ");
//                        pStmt.setString(1, WCPDateofInjury);
                        if (!WCPDateofInjury.equals(""))
                            pStmt.setString(1, WCPDateofInjury);
                        else
                            pStmt.setNull(1, Types.DATE);

                        pStmt.setString(2, WCPCaseNo);
                        pStmt.setString(3, WCPGroupNo);
                        pStmt.setString(4, WCPMemberId);
                        pStmt.setString(5, WCPInjuryRelatedAutoMotorAccident);
                        pStmt.setString(6, WCPInjuryRelatedWorkRelated);
                        pStmt.setString(7, WCPInjuryRelatedOtherAccident);
                        pStmt.setString(8, WCPInjuryRelatedNoAccident);
                        pStmt.setString(9, WCPInjuryOccurVehicle);
                        pStmt.setString(10, WCPInjuryOccurWork);
                        pStmt.setString(11, WCPInjuryOccurHome);
                        pStmt.setString(12, WCPInjuryOccurOther);
                        pStmt.setString(13, WCPInjuryDescription);
                        pStmt.setString(14, WCPHRFirstName);
                        pStmt.setString(15, WCPHRLastName);
                        pStmt.setString(16, WCPHRPhoneNumber);
                        pStmt.setString(17, WCPHRAddress + " " + WCPHRAddress2);
                        pStmt.setString(18, WCPHRCity);
                        pStmt.setString(19, WCPHRState);
                        pStmt.setString(20, WCPHRZipCode);
                        pStmt.setString(21, WCPPlanName);
                        pStmt.setString(22, WCPCarrierName);
                        pStmt.setString(23, WCPPayerPhoneNumber);
                        pStmt.setString(24, WCPCarrierAddress + " " + WCPCarrierAddress2);
                        pStmt.setString(25, WCPCarrierCity);
                        pStmt.setString(26, WCPCarrierState);
                        pStmt.setString(27, WCPCarrierZipCode);
                        pStmt.setString(28, WCPAdjudicatorFirstName);
                        pStmt.setString(29, WCPAdjudicatorLastName);
                        pStmt.setString(30, WCPAdjudicatorPhoneNumber);
                        pStmt.setString(31, WCPAdjudicatorFaxPhoneNumber);
                        pStmt.setInt(32, PatientRegId);
                        pStmt.executeUpdate();
                        pStmt.close();
                    } else {
                        try {
                            final PreparedStatement MainReceipt = conn.prepareStatement(
                                    " INSERT INTO " + Database + ".Patient_WorkCompPolicy (PatientRegId,WCPDateofInjury,WCPCaseNo," +
                                            "WCPGroupNo,WCPMemberId,WCPInjuryRelatedAutoMotorAccident,WCPInjuryRelatedWorkRelated," +
                                            "WCPInjuryRelatedOtherAccident,WCPInjuryRelatedNoAccident,WCPInjuryOccurVehicle," +
                                            "WCPInjuryOccurWork,WCPInjuryOccurHome,WCPInjuryOccurOther,WCPInjuryDescription,WCPHRFirstName," +
                                            "WCPHRLastName,WCPHRPhoneNumber,WCPHRAddress,WCPHRCity,WCPHRState,WCPHRZipCode,WCPPlanName," +
                                            "WCPCarrierName,WCPPayerPhoneNumber,WCPCarrierAddress,WCPCarrierCity,WCPCarrierState," +
                                            "WCPCarrierZipCode,WCPAdjudicatorFirstName,WCPAdjudicatorLastName,WCPAdjudicatorPhoneNumber," +
                                            "WCPAdjudicatorFaxPhoneNumber,CreatedDate)" +
                                            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
                            MainReceipt.setInt(1, PatientRegId);
                            //MainReceipt.setString(2, WCPDateofInjury);
                            if (!WCPDateofInjury.equals(""))
                                MainReceipt.setString(2, WCPDateofInjury);
                            else
                                MainReceipt.setNull(2, Types.DATE);
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
                            MainReceipt.setString(17, WCPHRPhoneNumber);
                            MainReceipt.setString(18, WCPHRAddress + " " + WCPHRAddress2);
                            MainReceipt.setString(19, WCPHRCity);
                            MainReceipt.setString(20, WCPHRState);
                            MainReceipt.setString(21, WCPHRZipCode);
                            MainReceipt.setString(22, WCPPlanName);
                            MainReceipt.setString(23, WCPCarrierName);
                            MainReceipt.setString(24, WCPPayerPhoneNumber);
                            MainReceipt.setString(25, WCPCarrierAddress + " " + WCPCarrierAddress2);
                            MainReceipt.setString(26, WCPCarrierCity);
                            MainReceipt.setString(27, WCPCarrierState);
                            MainReceipt.setString(28, WCPCarrierZipCode);
                            MainReceipt.setString(29, WCPAdjudicatorFirstName);
                            MainReceipt.setString(30, WCPAdjudicatorLastName);
                            MainReceipt.setString(31, WCPAdjudicatorPhoneNumber);
                            MainReceipt.setString(32, WCPAdjudicatorFaxPhoneNumber);
                            MainReceipt.executeUpdate();
                            MainReceipt.close();
                        } catch (Exception e) {
                            helper.SendEmailWithAttachment("Error in PatientReg2 ** (EditSave Insertion Patient_WorkCompPolicy Table ^^" + facilityName + " ##MES#005)", servletContext, e, "PatientReg2", "EditSave", conn);
                            Services.DumException("PatientReg2", "EditSave Insertion Patient_WorkCompPolicy Table", request, e, this.getServletContext());
                            Parsehtm Parser = new Parsehtm(request);
                            Parser.SetField("FormName", "PatientReg2");
                            Parser.SetField("ActionID", "EditValues&MRN=" + MRN + "&ClientIndex=" + ClientId);
                            Parser.SetField("Message", "MES#005");
                            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                            return;
                        }
                    }
                } catch (Exception e) {
                    helper.SendEmailWithAttachment("Error in PatientReg2 ** (EditSave updateing/Inserting Patinet Reg WOrkCompPOlicy Table ^^" + facilityName + " ##MES#006)", servletContext, e, "PatientReg2", "EditSave", conn);
                    Services.DumException("PatientReg2", "EditSave updateing/Inserting Patinet Reg WOrkCompPOlicy Table", request, e, this.getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg2");
                    Parser.SetField("ActionID", "EditValues&MRN=" + MRN + "&ClientIndex=" + ClientId);
                    Parser.SetField("Message", "MES#006");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    return;
                }
            }
            if (MotorVehicleAccidentChk.equals("1")) {
                try {
                    Query = "Select COUNT(*) from " + Database + ".Patient_AutoInsuranceInfo where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        MotorVehFound = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();
                } catch (Exception e) {
                    helper.SendEmailWithAttachment("Error in PatientReg2 ** (EGetting Count from  Patient_AutoInsuranceInfo Table ^^" + facilityName + " ##MES#007)", servletContext, e, "PatientReg2", "EditSave", conn);
                    Services.DumException("PatientReg2", "EditSave Getting Count from  Patient_AutoInsuranceInfo Table", request, e, this.getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg2");
                    Parser.SetField("ActionID", "EditValues&MRN=" + MRN + "&ClientIndex=" + ClientId);
                    Parser.SetField("Message", "MES#007");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    return;
                }
                try {
                    if (MotorVehFound > 0) {
/*                        Query = "Update " + Database + ".Patient_AutoInsuranceInfo " +
                                "Set AutoInsuranceInformationChk = '" + AutoInsuranceInformationChk + "', " +
                                "AIIDateofAccident = '" + AIIDateofAccident + "', AIIAutoClaim = '" + AIIAutoClaim + "', " +
                                "AIIAccidentLocationAddress = '" + AIIAccidentLocationAddress + AIIAccidentLocationAddress2 + "', " +
                                "AIIAccidentLocationCity = '" + AIIAccidentLocationCity + "', " +
                                "AIIAccidentLocationState = '" + AIIAccidentLocationState + "', " +
                                "AIIAccidentLocationZipCode = '" + AIIAccidentLocationZipCode + "', " +
                                "AIIRoleInAccident = '" + AIIRoleInAccident + "', " +
                                "AIITypeOfAutoIOnsurancePolicy = '" + AIITypeOfAutoIOnsurancePolicy + "', " +
                                "AIIPrefixforReponsibleParty = '" + AIIPrefixforReponsibleParty + "', " +
                                "AIIFirstNameforReponsibleParty = '" + AIIFirstNameforReponsibleParty + "', " +
                                "AIIMiddleNameforReponsibleParty = '" + AIIMiddleNameforReponsibleParty + "'," +
                                "AIILastNameforReponsibleParty = '" + AIILastNameforReponsibleParty + "'," +
                                "AIISuffixforReponsibleParty = '" + AIISuffixforReponsibleParty + "'," +
                                "AIICarrierResponsibleParty = '" + AIICarrierResponsibleParty + "'," +
                                "AIICarrierResponsiblePartyAddress = '" + AIICarrierResponsiblePartyAddress + AIICarrierResponsiblePartyAddress2 + "', " +
                                "AIICarrierResponsiblePartyCity = '" + AIICarrierResponsiblePartyCity + "'," +
                                "AIICarrierResponsiblePartyState = '" + AIICarrierResponsiblePartyState + "'," +
                                "AIICarrierResponsiblePartyZipCode = '" + AIICarrierResponsiblePartyZipCode + "' ," +
                                "AIICarrierResponsiblePartyPhoneNumber = '" + AIICarrierResponsiblePartyAreaCode + AIICarrierResponsiblePartyPhoneNumber + "'," +
                                "AIICarrierResponsiblePartyPolicyNumber = '" + AIICarrierResponsiblePartyPolicyNumber + "', " +
                                "AIIResponsiblePartyAutoMakeModel = '" + AIIResponsiblePartyAutoMakeModel + "' ," +
                                "AIIResponsiblePartyLicensePlate = '" + AIIResponsiblePartyLicensePlate + "', " +
                                "AIIFirstNameOfYourPolicyHolder = '" + AIIFirstNameOfYourPolicyHolder + "', " +
                                "AIILastNameOfYourPolicyHolder= '" + AIILastNameOfYourPolicyHolder + "', " +
                                "AIINameAutoInsuranceOfYourVehicle = '" + AIINameAutoInsuranceOfYourVehicle + "', " +
                                "AIIYourInsuranceAddress= '" + AIIYourInsuranceAddress + AIIYourInsuranceAddress2 + "', " +
                                "AIIYourInsuranceCity='" + AIIYourInsuranceCity + "', AIIYourInsuranceState= '" + AIIYourInsuranceState + "', " +
                                "AIIYourInsuranceZipCode = '" + AIIYourInsuranceZipCode + "', " +
                                "AIIYourInsurancePhoneNumber='" + AIIYourInsuranceAreaCode + AIIYourInsurancePhoneNumber + "',  " +
                                "AIIYourInsurancePolicyNo = '" + AIIYourInsurancePolicyNo + "', AIIYourLicensePlate = '" + AIIYourLicensePlate + "', " +
                                "AIIYourCarMakeModelYear = '" + AIIYourCarMakeModelYear + "' " +
                                "where PatientRegId = " + PatientRegId;
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();*/
                        pStmt = conn.prepareStatement("Update " + Database + ".Patient_AutoInsuranceInfo " +
                                "Set AutoInsuranceInformationChk = ?, AIIDateofAccident = ?, AIIAutoClaim = ?, " +
                                "AIIAccidentLocationAddress = ?, AIIAccidentLocationCity = ?, AIIAccidentLocationState = ?, " +
                                "AIIAccidentLocationZipCode = ?, AIIRoleInAccident = ?, AIITypeOfAutoIOnsurancePolicy = ?, " +
                                "AIIPrefixforReponsibleParty = ?, AIIFirstNameforReponsibleParty = ?, AIIMiddleNameforReponsibleParty = ?," +
                                "AIILastNameforReponsibleParty = ?,AIISuffixforReponsibleParty = ?,AIICarrierResponsibleParty = ?," +
                                "AIICarrierResponsiblePartyAddress = ?, AIICarrierResponsiblePartyCity = ?,AIICarrierResponsiblePartyState = ?," +
                                "AIICarrierResponsiblePartyZipCode = ? ,AIICarrierResponsiblePartyPhoneNumber = ?,AIICarrierResponsiblePartyPolicyNumber = ?, " +
                                "AIIResponsiblePartyAutoMakeModel = ? ,AIIResponsiblePartyLicensePlate = ?, AIIFirstNameOfYourPolicyHolder = ?, " +
                                "AIILastNameOfYourPolicyHolder= ?, AIINameAutoInsuranceOfYourVehicle = ?, AIIYourInsuranceAddress= ?, " +
                                "AIIYourInsuranceCity=?, AIIYourInsuranceState= ?, AIIYourInsuranceZipCode = ?, AIIYourInsurancePhoneNumber=?,  " +
                                "AIIYourInsurancePolicyNo = ?, AIIYourLicensePlate = ?, AIIYourCarMakeModelYear = ? " +
                                "where PatientRegId = ?");
                        pStmt.setInt(1, Integer.parseInt(AutoInsuranceInformationChk));
//                        pStmt.setString(2, AIIDateofAccident);
                        if (!AIIDateofAccident.equals(""))
                            pStmt.setString(2, AIIDateofAccident);
                        else
                            pStmt.setNull(2, Types.DATE);
                        pStmt.setString(3, AIIAutoClaim);
                        pStmt.setString(4, AIIAccidentLocationAddress + " " + AIIAccidentLocationAddress2);
                        pStmt.setString(5, AIIAccidentLocationCity);
                        pStmt.setString(6, AIIAccidentLocationState);
                        pStmt.setString(7, AIIAccidentLocationZipCode);
                        pStmt.setString(8, AIIRoleInAccident);
                        pStmt.setString(9, AIITypeOfAutoIOnsurancePolicy);
                        pStmt.setString(10, AIIPrefixforReponsibleParty);
                        pStmt.setString(11, AIIFirstNameforReponsibleParty);
                        pStmt.setString(12, AIIMiddleNameforReponsibleParty);
                        pStmt.setString(13, AIILastNameforReponsibleParty);
                        pStmt.setString(14, AIISuffixforReponsibleParty);
                        pStmt.setString(15, AIICarrierResponsibleParty);
                        pStmt.setString(16, AIICarrierResponsiblePartyAddress + " " + AIICarrierResponsiblePartyAddress2);
                        pStmt.setString(17, AIICarrierResponsiblePartyCity);
                        pStmt.setString(18, AIICarrierResponsiblePartyState);
                        pStmt.setString(19, AIICarrierResponsiblePartyZipCode);
                        pStmt.setString(20, AIICarrierResponsiblePartyPhoneNumber);
                        pStmt.setString(21, AIICarrierResponsiblePartyPolicyNumber);
                        pStmt.setString(22, AIIResponsiblePartyAutoMakeModel);
                        pStmt.setString(23, AIIResponsiblePartyLicensePlate);
                        pStmt.setString(24, AIIFirstNameOfYourPolicyHolder);
                        pStmt.setString(25, AIILastNameOfYourPolicyHolder);
                        pStmt.setString(26, AIINameAutoInsuranceOfYourVehicle);
                        pStmt.setString(27, AIIYourInsuranceAddress + " " + AIIYourInsuranceAddress2);
                        pStmt.setString(28, AIIYourInsuranceCity);
                        pStmt.setString(29, AIIYourInsuranceState);
                        pStmt.setString(30, AIIYourInsuranceZipCode);
                        pStmt.setString(31, AIIYourInsurancePhoneNumber);
                        pStmt.setString(32, AIIYourInsurancePolicyNo);
                        pStmt.setString(33, AIIYourLicensePlate);
                        pStmt.setString(34, AIIYourCarMakeModelYear);
                        pStmt.setInt(35, PatientRegId);
                        pStmt.executeUpdate();
                        pStmt.close();
                    } else {
                        try {
                            final PreparedStatement MainReceipt = conn.prepareStatement(
                                    " INSERT INTO " + Database + ".Patient_AutoInsuranceInfo (PatientRegId,AutoInsuranceInformationChk,AIIDateofAccident," +
                                            "AIIAutoClaim,AIIAccidentLocationAddress,AIIAccidentLocationCity,AIIAccidentLocationState," +
                                            "AIIAccidentLocationZipCode,AIIRoleInAccident,AIITypeOfAutoIOnsurancePolicy,AIIPrefixforReponsibleParty," +
                                            "AIIFirstNameforReponsibleParty,AIIMiddleNameforReponsibleParty,AIILastNameforReponsibleParty," +
                                            "AIISuffixforReponsibleParty,AIICarrierResponsibleParty,AIICarrierResponsiblePartyAddress," +
                                            "AIICarrierResponsiblePartyCity,AIICarrierResponsiblePartyState,AIICarrierResponsiblePartyZipCode," +
                                            "AIICarrierResponsiblePartyPhoneNumber,AIICarrierResponsiblePartyPolicyNumber,AIIResponsiblePartyAutoMakeModel," +
                                            "AIIResponsiblePartyLicensePlate,AIIFirstNameOfYourPolicyHolder,AIILastNameOfYourPolicyHolder," +
                                            "AIINameAutoInsuranceOfYourVehicle,AIIYourInsuranceAddress,AIIYourInsuranceCity,AIIYourInsuranceState," +
                                            "AIIYourInsuranceZipCode,AIIYourInsurancePhoneNumber,AIIYourInsurancePolicyNo,AIIYourLicensePlate," +
                                            "AIIYourCarMakeModelYear,CreatedDate)" +
                                            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
                            MainReceipt.setInt(1, PatientRegId);
                            MainReceipt.setInt(2, Integer.parseInt(AutoInsuranceInformationChk));
                            //MainReceipt.setString(3, AIIDateofAccident);
                            if (!AIIDateofAccident.equals(""))
                                MainReceipt.setString(3, AIIDateofAccident);
                            else
                                MainReceipt.setNull(3, Types.DATE);
                            MainReceipt.setString(4, AIIAutoClaim);
                            MainReceipt.setString(5, AIIAccidentLocationAddress + " " + AIIAccidentLocationAddress2);
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
                            MainReceipt.setString(17, AIICarrierResponsiblePartyAddress + " " + AIICarrierResponsiblePartyAddress2);
                            MainReceipt.setString(18, AIICarrierResponsiblePartyCity);
                            MainReceipt.setString(19, AIICarrierResponsiblePartyState);
                            MainReceipt.setString(20, AIICarrierResponsiblePartyZipCode);
                            MainReceipt.setString(21, AIICarrierResponsiblePartyPhoneNumber);
                            MainReceipt.setString(22, AIICarrierResponsiblePartyPolicyNumber);
                            MainReceipt.setString(23, AIIResponsiblePartyAutoMakeModel);
                            MainReceipt.setString(24, AIIResponsiblePartyLicensePlate);
                            MainReceipt.setString(25, AIIFirstNameOfYourPolicyHolder);
                            MainReceipt.setString(26, AIILastNameOfYourPolicyHolder);
                            MainReceipt.setString(27, AIINameAutoInsuranceOfYourVehicle);
                            MainReceipt.setString(28, AIIYourInsuranceAddress + " " + AIIYourInsuranceAddress2);
                            MainReceipt.setString(29, AIIYourInsuranceCity);
                            MainReceipt.setString(30, AIIYourInsuranceState);
                            MainReceipt.setString(31, AIIYourInsuranceZipCode);
                            MainReceipt.setString(32, AIIYourInsurancePhoneNumber);
                            MainReceipt.setString(33, AIIYourInsurancePolicyNo);
                            MainReceipt.setString(34, AIIYourLicensePlate);
                            MainReceipt.setString(35, AIIYourCarMakeModelYear);
                            MainReceipt.executeUpdate();
                            MainReceipt.close();
                        } catch (Exception e) {
                            helper.SendEmailWithAttachment("Error in PatientReg2 ** (Insertion Patient_AutoInsuranceInfo Table ^^" + facilityName + " ##MES#008)", servletContext, e, "PatientReg2", "EditSave", conn);
                            Services.DumException("PatientReg2", "EditSave Count from  Patient_AutoInsuranceInfo Table", request, e, this.getServletContext());
                            Parsehtm Parser = new Parsehtm(request);
                            Parser.SetField("FormName", "PatientReg2");
                            Parser.SetField("ActionID", "EditValues&MRN=" + MRN + "&ClientIndex=" + ClientId);
                            Parser.SetField("Message", "MES#008");
                            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                            return;
                        }
                    }
                } catch (Exception e) {
                    helper.SendEmailWithAttachment("Error in PatientReg2 ** (updateing Patinet Reg AUtoInsuranceInfo  Table ^^" + facilityName + " ##MES#009)", servletContext, e, "PatientReg2", "EditSave", conn);
                    Services.DumException("PatientReg2", "EditSave updating Patient Reg AUtoInsuranceInfo  Table", request, e, this.getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg2");
                    Parser.SetField("ActionID", "EditValues&MRN=" + MRN + "&ClientIndex=" + ClientId);
                    Parser.SetField("Message", "MES#009");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    return;

                }
            }
            if (HealthInsuranceChk.equals("1")) {
                try {
                    Query = "Select COUNT(*) from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        HealthInsFound = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();
                } catch (Exception e) {
                    helper.SendEmailWithAttachment("Error in PatientReg2 ** (EditValueError in Getting Count from  Patient_HealthInsuranceInfo tables ^^" + facilityName + " ##MES#010)", servletContext, e, "PatientReg2", "EditSave", conn);
                    Services.DumException("PatientReg2", "EditSave updating Patient Reg Patient_HealthInsuranceInfo  Table", request, e, this.getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg2");
                    Parser.SetField("ActionID", "EditValues&MRN=" + MRN + "&ClientIndex=" + ClientId);
                    Parser.SetField("Message", "MES#010");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    return;
                }
                try {
                    if (HealthInsFound > 0) {
/*                        Query = "Update " + Database + ".Patient_HealthInsuranceInfo Set GovtFundedInsurancePlanChk = '" + GovtFundedInsurancePlanChk + "', " +
                                "GFIPMedicare = '" + GFIPMedicare + "', GFIPMedicaid = '" + GFIPMedicaid + "', GFIPCHIP = '" + GFIPCHIP + "', " +
                                "GFIPTricare = '" + GFIPTricare + "', GFIPVHA = '" + GFIPVHA + "', GFIPIndianHealth = '" + GFIPIndianHealth + "', " +
                                "InsuranceSubPatient = '" + InsuranceSubPatient.toUpperCase() + "', " +
                                "InsuranceSubGuarantor = '" + InsuranceSubGuarantor.toUpperCase() + "', " +
                                "InsuranceSubOther = '" + InsuranceSubOther.toUpperCase() + "', " +
                                "HIPrimaryInsurance = '" + HIPrimaryInsurance.toUpperCase() + "', " +
                                "HISubscriberFirstName = '" + HISubscriberFirstName.toUpperCase() + "'," +
                                "HISubscriberLastName = '" + HISubscriberLastName.toUpperCase() + "',HISubscriberDOB = '" + HISubscriberDOB + "'," +
                                "HISubscriberSSN = '" + HISubscriberSSN + "',HISubscriberRelationtoPatient = '" + HISubscriberRelationtoPatient.toUpperCase() + "', " +
                                "HISubscriberGroupNo = '" + HISubscriberGroupNo + "',HISubscriberPolicyNo = '" + HISubscriberPolicyNo + "'," +
                                "SecondHealthInsuranceChk = '" + SecondHealthInsuranceChk + "' ,SHISecondaryName = '" + SHISecondaryName.toUpperCase() + "'," +
                                "SHISubscriberFirstName = '" + SHISubscriberFirstName.toUpperCase() + "', " +
                                "SHISubscriberLastName = '" + SHISubscriberLastName.toUpperCase() + "' ," +
                                "SHISubscriberRelationtoPatient = '" + SHISubscriberRelationtoPatient.toUpperCase() + "', " +
                                "SHISubscriberGroupNo = '" + SHISubscriberGroupNo + "', SHISubscriberPolicyNo= '" + SHISubscriberPolicyNo + "' " +
                                "where PatientRegId = " + PatientRegId;
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();*/
                        pStmt = conn.prepareStatement("Update " + Database + ".Patient_HealthInsuranceInfo Set GovtFundedInsurancePlanChk = ?, " +
                                "GFIPMedicare = ?, GFIPMedicaid = ?, GFIPCHIP = ?, GFIPTricare = ?, GFIPVHA = ?, GFIPIndianHealth = ?, " +
                                "InsuranceSubPatient = ?, InsuranceSubGuarantor = ?, InsuranceSubOther = ?, HIPrimaryInsurance = ?, " +
                                "HISubscriberFirstName = ?,HISubscriberLastName = ?,HISubscriberDOB = ?,HISubscriberSSN = ?," +
                                "HISubscriberRelationtoPatient = ?, HISubscriberGroupNo = ?,HISubscriberPolicyNo = ?," +
                                "SecondHealthInsuranceChk = ? ,SHISecondaryName = ?,SHISubscriberFirstName = ?, " +
                                "SHISubscriberLastName = ? ,SHISubscriberRelationtoPatient = ?, SHISubscriberGroupNo = ?, SHISubscriberPolicyNo= ? " +
                                "where PatientRegId = ?");
                        pStmt.setInt(1, Integer.parseInt(GovtFundedInsurancePlanChk));
                        pStmt.setInt(2, Integer.parseInt(GFIPMedicare));
                        pStmt.setInt(3, Integer.parseInt(GFIPMedicaid));
                        pStmt.setInt(4, Integer.parseInt(GFIPCHIP));
                        pStmt.setInt(5, Integer.parseInt(GFIPTricare));
                        pStmt.setInt(6, Integer.parseInt(GFIPVHA));
                        pStmt.setInt(7, Integer.parseInt(GFIPIndianHealth));
                        pStmt.setString(8, InsuranceSubPatient.toUpperCase());
                        pStmt.setString(9, InsuranceSubGuarantor.toUpperCase());
                        pStmt.setString(10, InsuranceSubOther.toUpperCase());
                        pStmt.setString(11, HIPrimaryInsurance.toUpperCase());
                        pStmt.setString(12, HISubscriberFirstName.toUpperCase());
                        pStmt.setString(13, HISubscriberLastName.toUpperCase());
                        pStmt.setString(14, HISubscriberDOB);
                        pStmt.setString(15, HISubscriberSSN);
                        pStmt.setString(16, HISubscriberRelationtoPatient.toUpperCase());
                        pStmt.setString(17, HISubscriberGroupNo);
                        pStmt.setString(18, HISubscriberPolicyNo);
                        pStmt.setInt(19, Integer.parseInt(SecondHealthInsuranceChk));
                        pStmt.setString(20, SHISecondaryName.toUpperCase());
                        pStmt.setString(21, SHISubscriberFirstName.toUpperCase());
                        pStmt.setString(22, SHISubscriberLastName.toUpperCase());
                        pStmt.setString(23, SHISubscriberRelationtoPatient.toUpperCase());
                        pStmt.setString(24, SHISubscriberGroupNo);
                        pStmt.setString(25, SHISubscriberPolicyNo);
                        pStmt.setInt(26, PatientRegId);
//                        System.out.println("HEALTH INSURANC --> " + pStmt.toString());
                        pStmt.executeUpdate();
                        pStmt.close();
                    } else {
                        try {
                            final PreparedStatement MainReceipt = conn.prepareStatement(
                                    " INSERT INTO " + Database + ".Patient_HealthInsuranceInfo (PatientRegId,GovtFundedInsurancePlanChk,GFIPMedicare,GFIPMedicaid," +
                                            "GFIPCHIP,GFIPTricare,GFIPVHA,GFIPIndianHealth,InsuranceSubPatient,InsuranceSubGuarantor,InsuranceSubOther," +
                                            "HIPrimaryInsurance,HISubscriberFirstName,HISubscriberLastName,HISubscriberDOB,HISubscriberSSN," +
                                            "HISubscriberRelationtoPatient,HISubscriberGroupNo,HISubscriberPolicyNo,SecondHealthInsuranceChk,SHISecondaryName," +
                                            "SHISubscriberFirstName,SHISubscriberLastName,SHISubscriberRelationtoPatient,SHISubscriberGroupNo," +
                                            "SHISubscriberPolicyNo,CreatedDate) " +
                                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
                            MainReceipt.setInt(1, PatientRegId);
                            MainReceipt.setInt(2, Integer.parseInt(GovtFundedInsurancePlanChk));
                            MainReceipt.setInt(3, Integer.parseInt(GFIPMedicare));
                            MainReceipt.setInt(4, Integer.parseInt(GFIPMedicaid));
                            MainReceipt.setInt(5, Integer.parseInt(GFIPCHIP));
                            MainReceipt.setInt(6, Integer.parseInt(GFIPTricare));
                            MainReceipt.setInt(7, Integer.parseInt(GFIPVHA));
                            MainReceipt.setInt(8, Integer.parseInt(GFIPIndianHealth));
                            MainReceipt.setString(9, InsuranceSubPatient.toUpperCase());
                            MainReceipt.setString(10, InsuranceSubGuarantor.toUpperCase());
                            MainReceipt.setString(11, InsuranceSubOther.toUpperCase());
                            MainReceipt.setString(12, HIPrimaryInsurance.toUpperCase());
                            MainReceipt.setString(13, HISubscriberFirstName.toUpperCase());
                            MainReceipt.setString(14, HISubscriberLastName.toUpperCase());
                            MainReceipt.setString(15, HISubscriberDOB);
                            MainReceipt.setString(16, HISubscriberSSN);
                            MainReceipt.setString(17, HISubscriberRelationtoPatient.toUpperCase());
                            MainReceipt.setString(18, HISubscriberGroupNo);
                            MainReceipt.setString(19, HISubscriberPolicyNo);
                            MainReceipt.setInt(20, Integer.parseInt(SecondHealthInsuranceChk));
                            MainReceipt.setString(21, SHISecondaryName.toUpperCase());
                            MainReceipt.setString(22, SHISubscriberFirstName.toUpperCase());
                            MainReceipt.setString(23, SHISubscriberLastName.toUpperCase());
                            MainReceipt.setString(24, SHISubscriberRelationtoPatient.toUpperCase());
                            MainReceipt.setString(25, SHISubscriberGroupNo);
                            MainReceipt.setString(26, SHISubscriberPolicyNo);
//                            System.out.println("HEALTH INSURANCE --> " + pStmt.toString());
                            MainReceipt.executeUpdate();
                            MainReceipt.close();
                        } catch (Exception e) {
                            helper.SendEmailWithAttachment("Error in PatientReg2 ** (EditValueError in Insertion Patient_HealthInsuranceInfo tables ^^" + facilityName + " ##MES#011)", servletContext, e, "PatientReg2", "EditSave", conn);
                            Services.DumException("PatientReg2", "EditValueError in Insertion Patient_HealthInsuranceInfo   Table", request, e, this.getServletContext());
                            Parsehtm Parser = new Parsehtm(request);
                            Parser.SetField("FormName", "PatientReg2");
                            Parser.SetField("ActionID", "EditValues&MRN=" + MRN + "&ClientIndex=" + ClientId);
                            Parser.SetField("Message", "MES#011");
                            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                            return;
                        }
                    }
                } catch (Exception e) {
                    helper.SendEmailWithAttachment("Error in PatientReg2 ** (EditValueError in Insertion Patient_HealthInsuranceInfo tables ^^" + facilityName + " ##MES#012)", servletContext, e, "PatientReg2", "EditSave", conn);
                    Services.DumException("PatientReg2", "EditValueError in Insertion Patient_HealthInsuranceInfo   Table", request, e, this.getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg2");
                    Parser.SetField("ActionID", "EditValues&MRN=" + MRN + "&ClientIndex=" + ClientId);
                    Parser.SetField("Message", "MES#012");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    return;
                }
            }
            try {
/*                Query = " Update " + Database + ".MarketingInfo Set MFFirstVisit = '" + MFFirstVisit + "', MFReturnPat = '" + MFReturnPat + "', MFInternetFind = '" + MFInternetFind + "', " +
                        " Facebook = '" + Facebook + "', MapSearch = '" + MapSearch + "', GoogleSearch = '" + GoogleSearch + "', VERWebsite = '" + VERWebsite + "', WebsiteAds = '" + WebsiteAds + "', " +
                        " OnlineReviews = '" + OnlineReviews + "', Twitter = '" + Twitter + "', LinkedIn = '" + LinkedIn + "', EmailBlast = '" + EmailBlast + "', YouTube = '" + YouTube + "', " +
                        " TV = '" + TV + "', Billboard = '" + Billboard + "', Radio = '" + Radio + "', Brochure = '" + Brochure + "', DirectMail = '" + DirectMail + "', CitizensDeTar = '" + CitizensDeTar + "', " +
                        " LiveWorkNearby = '" + LiveWorkNearby + "', FamilyFriend = '" + FamilyFriend + "', FamilyFriend_text = '" + FamilyFriend_text + "', UrgentCare = '" + UrgentCare + "', " +
                        " UrgentCare_text = '" + UrgentCare_text + "', NewspaperMagazine = '" + NewspaperMagazine + "', NewspaperMagazine_text = '" + NewspaperMagazine_text + "', " +
                        " School = '" + School + "', School_text = '" + School_text + "', Hotel = '" + Hotel + "', Hotel_text = '" + Hotel_text + "', MFPhysician = '" + MFPhysician + "', " +
                        " OnlineAdvertisements = '" + OnlineAdvertisements + "', EmployerSentMe = '" + EmployerSentMe + "', EmployerSentMe_text = '" + EmployerSentMe_text + "', " +
                        " MFPhysicianRefChk = '" + MFPhysicianRefChk + "', PatientCell = '" + PatientCell + "', RecInitial = '" + RecInitial + "' " +
                        " where PatientRegId =  " + PatientRegId;
                //System.out.println(Query);
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();*/
                pStmt = conn.prepareStatement(" Update " + Database + ".MarketingInfo Set MFFirstVisit = ?, MFReturnPat = ?, MFInternetFind = ?, " +
                        " Facebook = ?, MapSearch = ?, GoogleSearch = ?, VERWebsite = ?, WebsiteAds = ?, " +
                        " OnlineReviews = ?, Twitter = ?, LinkedIn = ?, EmailBlast = ?, YouTube = ?, " +
                        " TV = ?, Billboard = ?, Radio = ?, Brochure = ?, DirectMail = ?, CitizensDeTar = ?, " +
                        " LiveWorkNearby = ?, FamilyFriend = ?, FamilyFriend_text = ?, UrgentCare = ?, " +
                        " UrgentCare_text = ?, NewspaperMagazine = ?, NewspaperMagazine_text = ?, " +
                        " School = ?, School_text =?, Hotel = ?, Hotel_text = ?, MFPhysician = ?, " +
                        " OnlineAdvertisements = ?, EmployerSentMe = ?, EmployerSentMe_text = ?, " +
                        " MFPhysicianRefChk = ?, PatientCell = ?, RecInitial = ? " +
                        " where PatientRegId =  ?");
                pStmt.setString(1, MFFirstVisit);
                pStmt.setInt(2, Integer.parseInt(MFReturnPat));
                pStmt.setInt(3, Integer.parseInt(MFInternetFind));
                pStmt.setInt(4, Integer.parseInt(Facebook));
                pStmt.setInt(5, Integer.parseInt(MapSearch));
                pStmt.setInt(6, Integer.parseInt(GoogleSearch));
                pStmt.setInt(7, Integer.parseInt(VERWebsite));
                pStmt.setString(8, WebsiteAds);
                pStmt.setString(9, OnlineReviews);
                pStmt.setString(10, Twitter);
                pStmt.setString(11, LinkedIn);
                pStmt.setString(12, EmailBlast);
                pStmt.setString(13, YouTube);
                pStmt.setString(14, TV);
                pStmt.setString(15, Billboard);
                pStmt.setString(16, Radio);
                pStmt.setString(17, Brochure);
                pStmt.setString(18, DirectMail);
                pStmt.setString(19, CitizensDeTar);
                pStmt.setString(20, LiveWorkNearby);
                pStmt.setString(21, FamilyFriend);
                pStmt.setString(22, FamilyFriend_text);
                pStmt.setString(23, UrgentCare);
                pStmt.setString(24, UrgentCare_text);
                pStmt.setString(25, NewspaperMagazine);
                pStmt.setString(26, NewspaperMagazine_text);
                pStmt.setString(27, School);
                pStmt.setString(28, School_text);
                pStmt.setString(29, Hotel);
                pStmt.setString(30, Hotel_text);
                pStmt.setString(31, MFPhysician);
                pStmt.setString(32, OnlineAdvertisements);
                pStmt.setString(33, EmployerSentMe);
                pStmt.setString(34, EmployerSentMe_text);
                pStmt.setString(35, MFPhysicianRefChk);
                pStmt.setString(36, PatientCell);
                pStmt.setString(37, RecInitial);
                pStmt.setInt(38, PatientRegId);

            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (EditValueError in Update Marketing Info tables ^^" + facilityName + " ##MES#013)", servletContext, e, "PatientReg2", "EditSave", conn);
                Services.DumException("PatientReg2", "EditValueError in Update Marketing Info  Table", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "EditValues&MRN=" + MRN + "&ClientIndex=" + ClientId);
                Parser.SetField("Message", "MES#013");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }
            if (ClientId == 9) {
                int found = 0;
                Query = "Select Count(*) from " + Database + ".SignRequest where PatientRegId = " + PatientRegId + "";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    found = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (found > 0) {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + Database + ".SignRequest_History " +
                                    "SELECT * FROM " + Database + ".SignRequest " +
                                    "where PatientRegId = " + PatientRegId);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();


                    Query = "Update " + Database + ".SignRequest set isSign = 0 , SignedFrom='EDIT' where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }


                Query = "Select DirectoryName from oe.clients where ltrim(rtrim(UPPER(name))) = ltrim(rtrim(UPPER('" + ClientId + "')))";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                String DirectoryName = null;
                if (rset.next()) {
                    DirectoryName = rset.getString(1);
                }
                rset.close();
                stmt.close();

                String temp = SaveBundle_Victoria(request, out, conn, response, Database, ClientId, DirectoryName, PatientRegId, "EDIT");
//                System.out.println("temp " + temp);
//                .print();
                String[] arr = temp.split("~");
                String FileName = arr[2];
                String outputFilePath = arr[1];
                String pageCount = arr[0];
                Parsehtm Parser = new Parsehtm(request);
//                Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " We Have Registered You Successfully " + Message + ". Please walk to the front door and Press the buzzer.  DATED: " + Date);
                Parser.SetField("Message", "Data Successfully Updated. Please wait for further processing.");
                Parser.SetField("MRN", "DONE");
                Parser.SetField("FormName", "PatientReg");
                Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientId);
                Parser.SetField("pageCount", String.valueOf(pageCount));
                Parser.SetField("FileName", String.valueOf(FileName));
                Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
                Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
                Parser.SetField("ClientIndex", String.valueOf(ClientId));
                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/MessageVictoria.html");
            } else {
                final Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Data Successfully Updated ");
                Parser.SetField("FormName", String.valueOf("PatientUpdateInfo"));
                Parser.SetField("ActionID", String.valueOf("GetInput&ID=" + PatientRegId));
                Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(servletContext))) + "Exception/Success.html");
            }

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientReg2 ** (EditValueError in MAIN Catch ^^" + facilityName + " ##MES#015)", servletContext, e, "PatientReg2", "EditSave", conn);
            Services.DumException("PatientReg2", "EditValueError in MAIN Catch", request, e, this.getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "PatientReg2");
            Parser.SetField("ActionID", "EditValues&MRN=" + MRN + "&ClientIndex=" + ClientId);
            Parser.SetField("Message", "MES#015");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    private void CheckPatient(final HttpServletRequest request, final PrintWriter out, final Connection conn) {
        String Query = "";
        Statement stmt = null;
        ResultSet rset = null;
        String FirstName = "";
        String LastName = "";
        String Database = "";
        String DOB = "";
        int ClientIndex = 0;
        try {
            FirstName = request.getParameter("FirstName").trim();
            ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());
            LastName = request.getParameter("LastName").trim();
            DOB = request.getParameter("DOB").trim();

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
            Query = " Select COUNT(*), IFNULL(MRN,0) from " + Database + ".PatientReg  where Status = 0 and ltrim(rtrim(UPPER(FirstName))) = ltrim(rtrim(UPPER('" + FirstName.trim() + "')))  and ltrim(rtrim(UPPER(LastName))) = ltrim(rtrim(UPPER('" + LastName.trim() + "'))) and DOB = '" + DOB + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientFound = rset.getInt(1);
                FoundMRN = rset.getString(2);
            }
            rset.close();
            stmt.close();
            if (PatientFound > 0) {
                out.println(FoundMRN + "|" + PatientFound);
            }
        } catch (Exception e) {
            out.println("Error " + e.getMessage());
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
            Query = "Select IFNULL(FirstName,''), IFNULL(MiddleInitial,''), IFNULL(LastName,'')," +
                    " IFNULL(Email, ''), IFNULL(PhNumber,0),  IFNULL(DOB,'0000-00-00'), " +
                    "IFNULL(Gender,'M'), IFNULL(City,''),IFNULL(Address,''), IFNULL(State,'TX')," +
                    " IFNULL(ZipCode,''), IFNULL(County,'')  " +
                    "from victoria.PatientReg where ID = " + PatientRegId;
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
        if (Gender.toUpperCase().equals("MALE")) {
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
            PhNumber += "0";
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
//            System.out.println("Request: " + Request);
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
//            System.out.println("Reply: " + reply);
        } catch (Exception e) {
            final String Message = "0";
            Services.DumException("PatientReg2", "InsertCOVIDReg 0", request, e, this.getServletContext());
            return "0";
        }
        return reply;
    }

    public String SaveBundle_Victoria(final HttpServletRequest request, final PrintWriter out, final Connection conn, final HttpServletResponse response, final String Database, final int ClientId, final String DirectoryName, int patientRegId, String SignedFrom) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        MergePdf mergePdf = new MergePdf();
        int ID = patientRegId;//Integer.parseInt(request.getParameter("ID").trim());
        String Path1 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/";
        String Path2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/";
        String ResultPdf = "";
        String Title = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String DOB = "";
        String Age = "";
        String gender = "";
        String Email = "";
        String ConfirmEmail = "";
        String MaritalStatus = "";
        String AreaCode = "";
        String PhNumber = "";
        String Address = "";
        String Address2 = "";
        String City = "";
        String State = "";
        String ZipCode = "";
        String Ethnicity = "";
        String Ethnicity_OthersText = "";
        String SSN = "";
        String EmployementChk = "";
        String Employer = "";
        String Occupation = "";
        String EmpContact = "";
        String PrimaryCarePhysicianChk = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        String PriCarePhyAddress = "";
        String PriCarePhyAddress2 = "";
        String PriCarePhyCity = "";
        String PriCarePhyState = "";
        String PriCarePhyZipCode = "";
        String PatientMinorChk = "";
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
        String GuarantorChk = "";
        String Guarantor = "";
        String GuarantorDOB = "";
        String GuarantorSEX = "";
        String GuarantorSSN = "";
        String GuarantorAddress = "";
        String GuarantorPhoneNumber = "";
        String GuarantorEmployer = "";
        String GuarantorEmployerAreaCode = "";
        String GuarantorEmployerPhNumber = "";
        String GuarantorEmployerAddress = "";
        String GuarantorEmployerAddress2 = "";
        String GuarantorEmployerCity = "";
        String GuarantorEmployerState = "";
        String GuarantorEmployerZipCode = "";
        int WorkersCompPolicyChk = 0;
        String WorkCompPolicyStr = "";
        String WCPDateofInjury = "";
        String WCPCaseNo = "";
        String WCPGroupNo = "";
        String WCPMemberId = "";
        String WCPInjuryRelatedAutoMotorAccident = "";
        String WCPInjuryRelatedWorkRelated = "";
        String WCPInjuryRelatedOtherAccident = "";
        String WCPInjuryRelatedNoAccident = "";
        String WCPInjuryOccurVehicle = "";
        String WCPInjuryOccurWork = "";
        String WCPInjuryOccurHome = "";
        String WCPInjuryOccurOther = "";
        String WCPInjuryDescription = "";
        String WCPHRFirstName = "";
        String WCPHRLastName = "";
        final String WCPHRAreaCode = "";
        String WCPHRPhoneNumber = "";
        String WCPHRAddress = "";
        final String WCPHRAddress2 = "";
        String WCPHRCity = "";
        String WCPHRState = "";
        String WCPHRZipCode = "";
        String WCPPlanName = "";
        String WCPCarrierName = "";
        final String WCPPayerAreaCode = "";
        String WCPPayerPhoneNumber = "";
        String WCPCarrierAddress = "";
        final String WCPCarrierAddress2 = "";
        String WCPCarrierCity = "";
        String WCPCarrierState = "";
        String WCPCarrierZipCode = "";
        String WCPAdjudicatorFirstName = "";
        String WCPAdjudicatorLastName = "";
        final String WCPAdjudicatorAreaCode = "";
        String WCPAdjudicatorPhoneNumber = "";
        final String WCPAdjudicatorFaxAreaCode = "";
        String WCPAdjudicatorFaxPhoneNumber = "";
        int MotorVehicleAccidentChk = 0;
        String MotorVehAccidentStr = "";
        String AutoInsuranceInformationChk = "0";
        String AIIDateofAccident = "";
        String AIIAutoClaim = "";
        String AIIAccidentLocationAddress = "";
        final String AIIAccidentLocationAddress2 = "";
        String AIIAccidentLocationCity = "";
        String AIIAccidentLocationState = "";
        String AIIAccidentLocationZipCode = "";
        String AIIRoleInAccident = "";
        String AIITypeOfAutoIOnsurancePolicy = "";
        String AIIPrefixforReponsibleParty = "";
        String AIIFirstNameforReponsibleParty = "";
        String AIIMiddleNameforReponsibleParty = "";
        String AIILastNameforReponsibleParty = "";
        String AIISuffixforReponsibleParty = "";
        String AIICarrierResponsibleParty = "";
        String AIICarrierResponsiblePartyAddress = "";
        final String AIICarrierResponsiblePartyAddress2 = "";
        String AIICarrierResponsiblePartyCity = "";
        String AIICarrierResponsiblePartyState = "";
        String AIICarrierResponsiblePartyZipCode = "";
        final String AIICarrierResponsiblePartyAreaCode = "";
        String AIICarrierResponsiblePartyPhoneNumber = "";
        String AIICarrierResponsiblePartyPolicyNumber = "";
        String AIIResponsiblePartyAutoMakeModel = "";
        String AIIResponsiblePartyLicensePlate = "";
        String AIIFirstNameOfYourPolicyHolder = "";
        String AIILastNameOfYourPolicyHolder = "";
        String AIINameAutoInsuranceOfYourVehicle = "";
        String AIIYourInsuranceAddress = "";
        final String AIIYourInsuranceAddress2 = "";
        String AIIYourInsuranceCity = "";
        String AIIYourInsuranceState = "";
        String AIIYourInsuranceZipCode = "";
        final String AIIYourInsuranceAreaCode = "";
        String AIIYourInsurancePhoneNumber = "";
        String AIIYourInsurancePolicyNo = "";
        String AIIYourLicensePlate = "";
        String AIIYourCarMakeModelYear = "";
        int HealthInsuranceChk = 0;
        String GovtFundedInsurancePlanChk = "";
        int GFIPMedicare = 0;
        int GFIPMedicaid = 0;
        int GFIPCHIP = 0;
        int GFIPTricare = 0;
        int GFIPVHA = 0;
        int GFIPIndianHealth = 0;
        String InsuranceSubPatient = null;
        String InsuranceSubGuarantor = null;
        String InsuranceSubOther = null;
        String HIPrimaryInsurance = "";
        String HISubscriberFirstName = "";
        String HISubscriberLastName = "";
        String HISubscriberDOB = "";
        String HISubscriberSSN = "";
        String HISubscriberRelationtoPatient = "";
        String HISubscriberGroupNo = "";
        String HISubscriberPolicyNo = "";
        String SecondHealthInsuranceChk = "";
        String SHISecondaryName = "";
        String SHISubscriberFirstName = "";
        String SHISubscriberLastName = "";
        final String SHISubscriberDOB = "";
        String SHISubscriberRelationtoPatient = "";
        String SHISubscriberGroupNo = "";
        String SHISubscriberPolicyNo = "";
        int SelfPayChk = 0;
        String FirstNameNoSpaces = "";
        String LastNameNoSpace = "";
        String CityStateZip = "";
        final String Country = "";
        String MRN = "";
        int ClientIndex = 0;
        String ClientName = "";
        String DOS = "";
        String DoctorId = null;
        final String DoctorName = null;
        try {
            PreparedStatement ps = conn.prepareStatement("select date_format(now(),'%Y%m%d%H%i%s'), DATE_FORMAT(now(), '%m/%d/%Y'), DATE_FORMAT(now(), '%T')");
            rset = ps.executeQuery();
            if (rset.next()) {
                DateTime = rset.getString(1);
                Date = rset.getString(2);
                Time = rset.getString(3);
            }
            rset.close();
            ps.close();
            try {
                ps = conn.prepareStatement(" Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), " +
                        "IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-'), IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), IFNULL(DoctorsName,'-')  From " + Database + ".PatientReg Where ID = ?");
                ps.setInt(1, ID);
                rset = ps.executeQuery();
                while (rset.next()) {
                    PatientRegId = ID;
                    LastName = rset.getString(1).trim();
                    FirstName = rset.getString(2).trim();
                    FirstNameNoSpaces = FirstName.replaceAll("\\s+", "");
                    LastNameNoSpace = LastName.replaceAll(" ", "");
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
                ps.close();
                ps = conn.prepareStatement("Select name from oe.clients where Id = ?");
                ps.setInt(1, ClientId);
                rset = ps.executeQuery();
                if (rset.next()) {
                    ClientName = rset.getString(1);
                }
                rset.close();
                ps.close();
            } catch (Exception e) {
                out.println("Error In PateintReg:--" + e.getMessage());
                out.println(Query);
            }
            ps = conn.prepareStatement("Select  Ethnicity,Ethnicity_OthersText,EmployementChk,Employer,Occupation,EmpContact,PrimaryCarePhysicianChk,PriCarePhy,ReasonVisit,PriCarePhyAddress,PriCarePhyCity,PriCarePhyState,PriCarePhyZipCode,PatientMinorChk,GuarantorChk,GuarantorEmployer,GuarantorEmployerPhNumber,GuarantorEmployerAddress,GuarantorEmployerCity,GuarantorEmployerState,GuarantorEmployerZipCode,CreatedDate,WorkersCompPolicyChk,MotorVehicleAccidentChk,HealthInsuranceChk from " + Database + ".PatientReg_Details where PatientRegId = ? ");
            ps.setInt(1, ID);
            rset = ps.executeQuery();
            while (rset.next()) {
                Ethnicity = rset.getString(1);
                Ethnicity_OthersText = rset.getString(2);
                EmployementChk = rset.getString(3);
                Employer = rset.getString(4);
                Occupation = rset.getString(5);
                EmpContact = rset.getString(6);
                PrimaryCarePhysicianChk = rset.getString(7);
                PriCarePhy = rset.getString(8);
                if (ReasonVisit == null) {
                    ReasonVisit = rset.getString(9);
                }
                PriCarePhyAddress = rset.getString(10);
                PriCarePhyCity = rset.getString(11);
                PriCarePhyState = rset.getString(12);
                PriCarePhyZipCode = rset.getString(13);
                PatientMinorChk = rset.getString(14);
                GuarantorChk = rset.getString(15);
                GuarantorEmployer = rset.getString(16);
                GuarantorEmployerPhNumber = rset.getString(17);
                GuarantorEmployerAddress = rset.getString(18);
                GuarantorEmployerCity = rset.getString(19);
                GuarantorEmployerState = rset.getString(20);
                GuarantorEmployerZipCode = rset.getString(21);
                WorkersCompPolicyChk = rset.getInt(23);
                MotorVehicleAccidentChk = rset.getInt(24);
                HealthInsuranceChk = rset.getInt(25);
            }
            rset.close();
            ps.close();
            if (WorkersCompPolicyChk == 1) {
                try {
                    ps = conn.prepareStatement("Select IFNULL(DATE_FORMAT(WCPDateofInjury,'%m/%d/%Y'),''), IFNULL(WCPCaseNo,''), IFNULL(WCPGroupNo,''), IFNULL(WCPMemberId,''), IFNULL(WCPInjuryRelatedAutoMotorAccident,''), IFNULL(WCPInjuryRelatedWorkRelated,''), IFNULL(WCPInjuryRelatedOtherAccident,''), IFNULL(WCPInjuryRelatedNoAccident,''), IFNULL(WCPInjuryOccurVehicle,''), IFNULL(WCPInjuryOccurWork,''), IFNULL(WCPInjuryOccurHome,''), IFNULL(WCPInjuryOccurOther,''), IFNULL(WCPInjuryDescription,''), IFNULL(WCPHRFirstName,''), IFNULL(WCPHRLastName,''), IFNULL(WCPHRPhoneNumber,''), IFNULL(WCPHRAddress,''), IFNULL(WCPHRCity,''), IFNULL(WCPHRState,''), IFNULL(WCPHRZipCode,''), IFNULL(WCPPlanName,''), IFNULL(WCPCarrierName,''), IFNULL(WCPPayerPhoneNumber,''), IFNULL(WCPCarrierAddress,''), IFNULL(WCPCarrierCity,''), IFNULL(WCPCarrierState,''), IFNULL(WCPCarrierZipCode,''), IFNULL(WCPAdjudicatorFirstName,''), IFNULL(WCPAdjudicatorLastName,''), IFNULL(WCPAdjudicatorPhoneNumber,''), IFNULL(WCPAdjudicatorFaxPhoneNumber,'') from " + Database + ".Patient_WorkCompPolicy where PatientRegId = ?");
                    ps.setInt(1, ID);
                    rset = ps.executeQuery();
                    if (rset.next()) {
                        WCPDateofInjury = rset.getString(1);
                        WCPCaseNo = rset.getString(2);
                        WCPGroupNo = rset.getString(3);
                        WCPMemberId = rset.getString(4);
                        WCPInjuryRelatedAutoMotorAccident = rset.getString(5);
                        WCPInjuryRelatedWorkRelated = rset.getString(6);
                        WCPInjuryRelatedOtherAccident = rset.getString(7);
                        WCPInjuryRelatedNoAccident = rset.getString(8);
                        WCPInjuryOccurVehicle = rset.getString(9);
                        WCPInjuryOccurWork = rset.getString(10);
                        WCPInjuryOccurHome = rset.getString(11);
                        WCPInjuryOccurOther = rset.getString(12);
                        WCPInjuryDescription = rset.getString(13);
                        WCPHRFirstName = rset.getString(14);
                        WCPHRLastName = rset.getString(15);
                        WCPHRPhoneNumber = rset.getString(16);
                        WCPHRAddress = rset.getString(17);
                        WCPHRCity = rset.getString(18);
                        WCPHRState = rset.getString(19);
                        WCPHRZipCode = rset.getString(20);
                        WCPPlanName = rset.getString(21);
                        WCPCarrierName = rset.getString(22);
                        WCPPayerPhoneNumber = rset.getString(23);
                        WCPCarrierAddress = rset.getString(24);
                        WCPCarrierCity = rset.getString(25);
                        WCPCarrierState = rset.getString(26);
                        WCPCarrierZipCode = rset.getString(27);
                        WCPAdjudicatorFirstName = rset.getString(28);
                        WCPAdjudicatorLastName = rset.getString(29);
                        WCPAdjudicatorPhoneNumber = rset.getString(30);
                        WCPAdjudicatorFaxPhoneNumber = rset.getString(31);
                    }
                    rset.close();
                    ps.close();
                } catch (Exception e) {
                    System.out.println("Error in Getting Data from Patient_WorkCompPolicy");
                    Services.DumException("DownloadBundle", "GetINput Victoria", request, e, this.getServletContext());
                }
            }
            if (MotorVehicleAccidentChk == 1) {
                try {
                    ps = conn.prepareStatement("Select IFNULL(AutoInsuranceInformationChk,'0'), IFNULL(DATE_FORMAT(AIIDateofAccident,'%m/%d/%Y'),''), IFNULL(AIIAutoClaim,''), IFNULL(AIIAccidentLocationAddress,''), IFNULL(AIIAccidentLocationCity,''), IFNULL(AIIAccidentLocationState,''), IFNULL(AIIAccidentLocationZipCode,''), IFNULL(AIIRoleInAccident,''), IFNULL(AIITypeOfAutoIOnsurancePolicy,''), IFNULL(AIIPrefixforReponsibleParty,''), IFNULL(AIIFirstNameforReponsibleParty,''), IFNULL(AIIMiddleNameforReponsibleParty,''), IFNULL(AIILastNameforReponsibleParty,''), IFNULL(AIISuffixforReponsibleParty,''), IFNULL(AIICarrierResponsibleParty,''), IFNULL(AIICarrierResponsiblePartyAddress,''), IFNULL(AIICarrierResponsiblePartyCity,''), IFNULL(AIICarrierResponsiblePartyState,''), IFNULL(AIICarrierResponsiblePartyZipCode,''), IFNULL(AIICarrierResponsiblePartyPhoneNumber,''), IFNULL(AIICarrierResponsiblePartyPolicyNumber,''), IFNULL(AIIResponsiblePartyAutoMakeModel,''), IFNULL(AIIResponsiblePartyLicensePlate,''), IFNULL(AIIFirstNameOfYourPolicyHolder,''), IFNULL(AIILastNameOfYourPolicyHolder,''), IFNULL(AIINameAutoInsuranceOfYourVehicle,''), IFNULL(AIIYourInsuranceAddress,''), IFNULL(AIIYourInsuranceCity,''), IFNULL(AIIYourInsuranceState,''), IFNULL(AIIYourInsuranceZipCode,''), IFNULL(AIIYourInsurancePhoneNumber,''),IFNULL(AIIYourInsurancePolicyNo,''), IFNULL(AIIYourLicensePlate,''), IFNULL(AIIYourCarMakeModelYear,'') from " + Database + ".Patient_AutoInsuranceInfo where PatientRegId = ?");
                    ps.setInt(1, ID);
                    rset = ps.executeQuery();
                    if (rset.next()) {
                        AutoInsuranceInformationChk = rset.getString(1);
                        AIIDateofAccident = rset.getString(2);
                        AIIAutoClaim = rset.getString(3);
                        AIIAccidentLocationAddress = rset.getString(4);
                        AIIAccidentLocationCity = rset.getString(5);
                        AIIAccidentLocationState = rset.getString(6);
                        AIIAccidentLocationZipCode = rset.getString(7);
                        AIIRoleInAccident = rset.getString(8);
                        AIITypeOfAutoIOnsurancePolicy = rset.getString(9);
                        AIIPrefixforReponsibleParty = rset.getString(10);
                        AIIFirstNameforReponsibleParty = rset.getString(11);
                        AIIMiddleNameforReponsibleParty = rset.getString(12);
                        AIILastNameforReponsibleParty = rset.getString(13);
                        AIISuffixforReponsibleParty = rset.getString(14);
                        AIICarrierResponsibleParty = rset.getString(15);
                        AIICarrierResponsiblePartyAddress = rset.getString(16);
                        AIICarrierResponsiblePartyCity = rset.getString(17);
                        AIICarrierResponsiblePartyState = rset.getString(18);
                        AIICarrierResponsiblePartyZipCode = rset.getString(19);
                        AIICarrierResponsiblePartyPhoneNumber = rset.getString(20);
                        AIICarrierResponsiblePartyPolicyNumber = rset.getString(21);
                        AIIResponsiblePartyAutoMakeModel = rset.getString(22);
                        AIIResponsiblePartyLicensePlate = rset.getString(23);
                        AIIFirstNameOfYourPolicyHolder = rset.getString(24);
                        AIILastNameOfYourPolicyHolder = rset.getString(25);
                        AIINameAutoInsuranceOfYourVehicle = rset.getString(26);
                        AIIYourInsuranceAddress = rset.getString(27);
                        AIIYourInsuranceCity = rset.getString(28);
                        AIIYourInsuranceState = rset.getString(29);
                        AIIYourInsuranceZipCode = rset.getString(30);
                        AIIYourInsurancePhoneNumber = rset.getString(31);
                        AIIYourInsurancePolicyNo = rset.getString(32);
                        AIIYourLicensePlate = rset.getString(33);
                        AIIYourCarMakeModelYear = rset.getString(34);
                    }
                    rset.close();
                    ps.close();
                } catch (Exception e) {
                    System.out.println("Error in Getting Data from Patient_AutoInsuranceInfo");
                    Services.DumException("DownloadBundle", "GetINput Victoria", request, e, this.getServletContext());
                }
            }
            if (HealthInsuranceChk == 1) {
                try {
                    ps = conn.prepareStatement("Select IFNULL(GovtFundedInsurancePlanChk,'0'), IFNULL(GFIPMedicare,'0'), IFNULL(GFIPMedicaid,'0'), IFNULL(GFIPCHIP,'0'), IFNULL(GFIPTricare,'0'), IFNULL(GFIPVHA,'0'), IFNULL(GFIPIndianHealth,'0'), IFNULL(InsuranceSubPatient,''), IFNULL(InsuranceSubGuarantor,''), IFNULL(InsuranceSubOther,''), IFNULL(HIPrimaryInsurance,''), IFNULL(HISubscriberFirstName,''), IFNULL(HISubscriberLastName,''), IFNULL(HISubscriberDOB,''), IFNULL(HISubscriberSSN,''), IFNULL(HISubscriberRelationtoPatient,''), IFNULL(HISubscriberGroupNo,''), IFNULL(HISubscriberPolicyNo,''), IFNULL(SecondHealthInsuranceChk,''), IFNULL(SHISecondaryName,''), IFNULL(SHISubscriberFirstName,''), IFNULL(SHISubscriberLastName,''), IFNULL(SHISubscriberRelationtoPatient,''), IFNULL(SHISubscriberGroupNo,''), IFNULL(SHISubscriberPolicyNo,'')  from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = ?");
                    ps.setInt(1, ID);
                    rset = ps.executeQuery();
                    if (rset.next()) {
                        GovtFundedInsurancePlanChk = rset.getString(1);
                        GFIPMedicare = rset.getInt(2);
                        GFIPMedicaid = rset.getInt(3);
                        GFIPCHIP = rset.getInt(4);
                        GFIPTricare = rset.getInt(5);
                        GFIPVHA = rset.getInt(6);
                        GFIPIndianHealth = rset.getInt(7);
                        InsuranceSubPatient = rset.getString(8);
                        InsuranceSubGuarantor = rset.getString(9);
                        InsuranceSubOther = rset.getString(10);
                        HIPrimaryInsurance = rset.getString(11);
                        HISubscriberFirstName = rset.getString(12);
                        HISubscriberLastName = rset.getString(13);
                        HISubscriberDOB = rset.getString(14);
                        HISubscriberSSN = rset.getString(15);
                        HISubscriberRelationtoPatient = rset.getString(16);
                        HISubscriberGroupNo = rset.getString(17);
                        HISubscriberPolicyNo = rset.getString(18);
                        SecondHealthInsuranceChk = rset.getString(19);
                        SHISecondaryName = rset.getString(20);
                        SHISubscriberFirstName = rset.getString(21);
                        SHISubscriberLastName = rset.getString(22);
                        SHISubscriberRelationtoPatient = rset.getString(23);
                        SHISubscriberGroupNo = rset.getString(24);
                        SHISubscriberPolicyNo = rset.getString(25);
                    }
                    rset.close();
                    ps.close();
                } catch (Exception e) {
                    System.out.println("Error in Getting Data from Patient_HealthInsuraneInfo");
                    Services.DumException("DownloadBundle", "GetINput Victoria", request, e, this.getServletContext());
                }
            }
            if (Ethnicity.equals("1")) {
                Ethnicity = "Hispanic";
            } else if (Ethnicity.equals("2")) {
                Ethnicity = "Non-Hispanic";
            } else if (Ethnicity.equals("3")) {
                Ethnicity = "Unknown";
            }
            if (GuarantorChk.equals("1")) {
                Guarantor = "The Patient";
                GuarantorDOB = DOB;
                GuarantorSEX = gender;
                GuarantorSSN = SSN;
                GuarantorAddress = Address + "";
                GuarantorPhoneNumber = "" + PhNumber;
            } else if (GuarantorChk.equals("2")) {
                Guarantor = "Legal Guardian";
                GuarantorDOB = "-";
                GuarantorSEX = "-";
                GuarantorSSN = "-";
                GuarantorAddress = "-";
                GuarantorPhoneNumber = "-";
            } else if (GuarantorChk.equals("3")) {
                Guarantor = "Patient Parent";
                GuarantorDOB = "-";
                GuarantorSEX = "-";
                GuarantorSSN = "-";
                GuarantorAddress = "-";
                GuarantorPhoneNumber = "-";
            } else if (GuarantorChk.equals("2")) {
                Guarantor = "Spouse/Partner";
                GuarantorDOB = "-";
                GuarantorSEX = "-";
                GuarantorSSN = "-";
                GuarantorAddress = "-";
                GuarantorPhoneNumber = "-";
            }
            if (WorkersCompPolicyChk == 1) {
                WorkCompPolicyStr = "Yes";
            } else {
                WorkCompPolicyStr = "No";
            }
            if (MotorVehicleAccidentChk == 1) {
                MotorVehAccidentStr = "Yes";
            } else {
                MotorVehAccidentStr = "No";
            }
            if (HISubscriberDOB.equals("00/00/0000")) {
                HISubscriberDOB = "";
            }
            if (WCPDateofInjury.equals("00/00/0000")) {
                WCPDateofInjury = "";
            }
            if (AIIDateofAccident.equals("00/00/0000")) {
                AIIDateofAccident = "";
            }
            ps = conn.prepareStatement(" Select CASE WHEN MFFirstVisit = 1 THEN 'YES' ELSE '' END,  CASE WHEN MFReturnPat = 1 THEN 'YES' ELSE '' END,  CASE WHEN MFInternetFind = 1 THEN 'YES' ELSE '' END,  CASE WHEN Facebook = 1 THEN 'YES' ELSE '' END,  CASE WHEN MapSearch = 1 THEN 'YES' ELSE '' END,  CASE WHEN GoogleSearch = 1 THEN 'YES' ELSE '' END,  CASE WHEN VERWebsite = 1 THEN 'YES' ELSE '' END,  CASE WHEN WebsiteAds = 1 THEN 'YES' ELSE '' END, CASE WHEN OnlineReviews = 1 THEN 'YES' ELSE '' END, CASE WHEN Twitter = 1 THEN 'YES' ELSE '' END, CASE WHEN LinkedIn = 1 THEN 'YES' ELSE '' END, CASE WHEN EmailBlast = 1 THEN 'YES' ELSE '' END, CASE WHEN YouTube = 1 THEN 'YES' ELSE '' END, CASE WHEN TV = 1 THEN 'YES' ELSE '' END, CASE WHEN Billboard = 1 THEN 'YES' ELSE '' END, CASE WHEN Radio = 1 THEN 'YES' ELSE '' END, CASE WHEN Brochure = 1 THEN 'YES' ELSE '' END, CASE WHEN DirectMail = 1 THEN 'YES' ELSE '' END, CASE WHEN CitizensDeTar = 1 THEN 'YES' ELSE '' END, CASE WHEN LiveWorkNearby = 1 THEN 'YES' ELSE '' END, CASE WHEN FamilyFriend = 1 THEN 'YES' ELSE '' END, IFNULL(FamilyFriend_text,''),  CASE WHEN UrgentCare = 1 THEN 'YES' ELSE '' END, IFNULL(UrgentCare_text,''),  CASE WHEN NewspaperMagazine = 1 THEN 'YES' ELSE '' END, IFNULL(NewspaperMagazine_text,''),  CASE WHEN School = 1 THEN 'YES' ELSE '' END, IFNULL(School_text,''),  CASE WHEN Hotel = 1 THEN 'YES' ELSE '' END, IFNULL(Hotel_text,''), IFNULL(MFPhysician,'')  FROM " + Database + ".MarketingInfo WHERE PatientRegId = ?");
            ps.setInt(1, ID);
            rset = ps.executeQuery();
            if (rset.next()) {
                MFFirstVisit = rset.getString(1);
                MFReturnPat = rset.getString(2);
                MFInternetFind = rset.getString(3);
                Facebook = rset.getString(4);
                MapSearch = rset.getString(5);
                GoogleSearch = rset.getString(6);
                VERWebsite = rset.getString(7);
                WebsiteAds = rset.getString(8);
                OnlineReviews = rset.getString(9);
                Twitter = rset.getString(10);
                LinkedIn = rset.getString(11);
                EmailBlast = rset.getString(12);
                YouTube = rset.getString(13);
                TV = rset.getString(14);
                Billboard = rset.getString(15);
                Radio = rset.getString(16);
                Brochure = rset.getString(17);
                DirectMail = rset.getString(18);
                CitizensDeTar = rset.getString(19);
                LiveWorkNearby = rset.getString(20);
                FamilyFriend = rset.getString(21);
                FamilyFriend_text = rset.getString(22);
                UrgentCare = rset.getString(23);
                UrgentCare_text = rset.getString(24);
                NewspaperMagazine = rset.getString(25);
                NewspaperMagazine_text = rset.getString(26);
                School = rset.getString(27);
                School_text = rset.getString(28);
                Hotel = rset.getString(29);
                Hotel_text = rset.getString(30);
                MFPhysician = rset.getString(31);
            }
            rset.close();
            ps.close();

            String UID = "";
            Image SignImages = null;
            final File tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_0_" + ID + ".png");
            final boolean exists = tmpDir.exists();
            if (exists) {
                ps = conn.prepareStatement("Select UID from " + Database + ".SignRequest where PatientRegId = ?");
                ps.setInt(1, ID);
                rset = ps.executeQuery();
                if (rset.next()) {
                    UID = rset.getString(1);
                }
                rset.close();
                ps.close();

                SignImages = Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_0_" + ID + ".png");
                SignImages.scaleAbsolute(80.0f, 30.0f);
                //outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + UID + "_.pdf";
            } else {
                SignImages = null;
            }

            String inputFilePathTmp = "";
            String outputFilePathTmp = "";
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/ABNformEnglish.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/ABNformEnglish_" + ClientId + "_" + MRN + ".pdf";
            OutputStream fos1 = new FileOutputStream(new File(outputFilePathTmp));
            PdfReader pdfReader1 = new PdfReader(inputFilePathTmp);
            PdfStamper pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(110.0f, 80.0f);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410.0f, 80.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100.0f, 120.0f);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410.0f, 120.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(110.0f, 210.0f);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410.0f, 210.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/WC_QUESTIONNAIRE.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_QUESTIONNAIRE_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 700.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 675.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 650.0f);
                    pdfContentByte.showText(WCPMemberId);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 630.0f);
                    pdfContentByte.showText(WCPGroupNo);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 610.0f);
                    pdfContentByte.showText(WCPDateofInjury);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 585.0f);
                    pdfContentByte.showText(WCPCaseNo);
                    pdfContentByte.endText();
                    if (WCPInjuryRelatedAutoMotorAccident.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(175.0f, 535.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(175.0f, 535.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryRelatedWorkRelated.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(275.0f, 535.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(275.0f, 535.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryRelatedOtherAccident.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(380.0f, 535.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(380.0f, 535.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryRelatedNoAccident.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(475.0f, 535.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(475.0f, 535.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryOccurVehicle.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(220.0f, 515.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(220.0f, 515.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryOccurWork.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(280.0f, 515.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(280.0f, 515.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryOccurHome.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(340.0f, 515.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(340.0f, 515.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryOccurOther.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(400.0f, 515.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(400.0f, 515.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryDescription.length() <= 114) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 450.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(0, WCPInjuryDescription.length()));
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryDescription.length() > 114 && WCPInjuryDescription.length() <= 228) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 450.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(0, 114));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 435.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(114, WCPInjuryDescription.length()));
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryDescription.length() > 228 && WCPInjuryDescription.length() <= 342) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 450.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(0, 114));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 435.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(114, 228));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 420.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(228, WCPInjuryDescription.length()));
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryDescription.length() > 342 && WCPInjuryDescription.length() <= 456) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 450.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(0, 114));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 435.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(114, 228));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 420.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(228, 342));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 408.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(342, WCPInjuryDescription.length()));
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryDescription.length() > 456) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 450.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(0, 114));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 435.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(114, 228));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 420.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(228, 342));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 408.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(342, 456));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 393.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(456, WCPInjuryDescription.length()));
                        pdfContentByte.endText();
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 350.0f);
                    pdfContentByte.showText(WCPHRFirstName + " " + WCPHRLastName + " / " + WCPHRPhoneNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 325.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90.0f, 300.0f);
                    pdfContentByte.showText(WCPHRAddress);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 280.0f);
                    pdfContentByte.showText(WCPHRCity);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(230.0f, 280.0f);
                    pdfContentByte.showText(WCPHRState);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 260.0f);
                    pdfContentByte.showText(WCPHRZipCode);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 350.0f);
                    pdfContentByte.showText(WCPPlanName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 325.0f);
                    pdfContentByte.showText(WCPCarrierName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 300.0f);
                    pdfContentByte.showText(WCPPayerPhoneNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380.0f, 280.0f);
                    pdfContentByte.showText(WCPCarrierAddress);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(370.0f, 260.0f);
                    pdfContentByte.showText(WCPCarrierCity);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(470.0f, 260.0f);
                    pdfContentByte.showText(WCPCarrierState);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350.0f, 235.0f);
                    pdfContentByte.showText(WCPCarrierZipCode);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100.0f, 210.0f);
                    pdfContentByte.showText(WCPAdjudicatorFirstName + " " + WCPAdjudicatorLastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100.0f, 185.0f);
                    pdfContentByte.showText(WCPAdjudicatorPhoneNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100.0f, 165.0f);
                    pdfContentByte.showText(WCPAdjudicatorFaxPhoneNumber);
                    pdfContentByte.endText();
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(200.0f, 140.0f);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 140.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380.0f, 120.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/WC_MVA_assignmentofproceeds.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_assignmentofproceeds_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(55.0f, 625.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 447.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 130.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 80.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                }
                if (i == 2) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 710.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(210.0f, 680.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 630.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 480.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60.0f, 355.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(110.0f, 280.0f);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 280.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/MVA_ASSIGNMENTOFPROCEEDS.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MVA_ASSIGNMENTOFPROCEEDS_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 2) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60.0f, 355.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(110.0f, 290.0f);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 290.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/MVACLAIMFORM.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MVACLAIMFORM_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 640.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0f, 620.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 620.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 595.0f);
                    pdfContentByte.showText(Address);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(440.0f, 595.0f);
                    pdfContentByte.showText(PhNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(130.0f, 570.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(290.0f, 570.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 570.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(130.0f, 545.0f);
                    pdfContentByte.showText(AIIAutoClaim);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 522.0f);
                    pdfContentByte.showText(AIIDateofAccident);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(165.0f, 500.0f);
                    pdfContentByte.showText(AIIAccidentLocationAddress);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(300.0f, 500.0f);
                    pdfContentByte.showText(AIIAccidentLocationCity);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 500.0f);
                    pdfContentByte.showText(AIIAccidentLocationState);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(470.0f, 500.0f);
                    pdfContentByte.showText(AIIAccidentLocationZipCode);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(145.0f, 452.0f);
                    pdfContentByte.showText(AIIRoleInAccident);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250.0f, 352.0f);
                    pdfContentByte.showText(AIITypeOfAutoIOnsurancePolicy);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 320.0f);
                    pdfContentByte.showText(AIIPrefixforReponsibleParty + " " + AIIFirstNameforReponsibleParty + " " + AIIMiddleNameforReponsibleParty + " " + AIILastNameforReponsibleParty + " " + AIISuffixforReponsibleParty);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(280.0f, 300.0f);
                    pdfContentByte.showText(AIICarrierResponsibleParty);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0f, 280.0f);
                    pdfContentByte.showText(AIICarrierResponsiblePartyAddress);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 255.0f);
                    pdfContentByte.showText(AIICarrierResponsiblePartyCity);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(270.0f, 255.0f);
                    pdfContentByte.showText(AIICarrierResponsiblePartyState);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350.0f, 255.0f);
                    pdfContentByte.showText(AIICarrierResponsiblePartyZipCode);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(180.0f, 230.0f);
                    pdfContentByte.showText(AIICarrierResponsiblePartyPhoneNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(180.0f, 210.0f);
                    pdfContentByte.showText(AIICarrierResponsiblePartyPolicyNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 210.0f);
                    pdfContentByte.showText(AIIResponsiblePartyLicensePlate);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(240.0f, 190.0f);
                    pdfContentByte.showText(AIIResponsiblePartyAutoMakeModel);
                    pdfContentByte.endText();
                }
                if (i == 2) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(210.0f, 680.0f);
                    pdfContentByte.showText(AIIFirstNameOfYourPolicyHolder + " " + AIILastNameOfYourPolicyHolder);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250.0f, 650.0f);
                    pdfContentByte.showText(AIINameAutoInsuranceOfYourVehicle);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 630.0f);
                    pdfContentByte.showText(AIIYourInsuranceAddress);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 610.0f);
                    pdfContentByte.showText(AIIYourInsuranceCity);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(270.0f, 610.0f);
                    pdfContentByte.showText(AIIYourInsuranceState);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350.0f, 610.0f);
                    pdfContentByte.showText(AIIYourInsuranceZipCode);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(180.0f, 585.0f);
                    pdfContentByte.showText(AIIYourInsurancePhoneNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 565.0f);
                    pdfContentByte.showText(AIIYourInsurancePolicyNo);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 565.0f);
                    pdfContentByte.showText(AIIYourLicensePlate);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260.0f, 545.0f);
                    pdfContentByte.showText(AIIYourCarMakeModelYear);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/Medicalreleaseform.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/Medicalreleaseform_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(350.0f, 180.0f);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 570.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 552.0f);
                    pdfContentByte.showText(SSN);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 535.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500.0f, 180.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350.0f, 140.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            String inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/UHCINSAPPEALFORMS.pdf";
            String outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/UHCINSAPPEALFORMS_" + ClientId + "_" + MRN + ".pdf";
            FileOutputStream fos2 = new FileOutputStream(new File(outputFilePathTmp2));
            PdfReader pdfReader2 = new PdfReader(inputFilePathTmp2);
            PdfStamper pdfStamper2 = new PdfStamper(pdfReader2, (OutputStream) fos2);
            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(160.0f, 690.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 665.0f);
                    pdfContentByte2.showText(HISubscriberLastName + ", " + HISubscriberFirstName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 640.0f);
                    pdfContentByte2.showText(WCPMemberId);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 595.0f);
                    pdfContentByte2.showText("Victoria ED");
                    pdfContentByte2.endText();
                }
                if (j == 2) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(90.0f, 470.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName);
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();
            inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/MEDICAIDSELFPAYAGREEMENT.pdf";
            outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MEDICAIDSELFPAYAGREEMENT_" + ClientId + "_" + MRN + ".pdf";
            fos2 = new FileOutputStream(new File(outputFilePathTmp2));
            pdfReader2 = new PdfReader(inputFilePathTmp2);
            pdfStamper2 = new PdfStamper(pdfReader2, (OutputStream) fos2);
            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(175.0f, 90.0f);
                        pdfContentByte2.addImage(SignImages);
                    }
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(475.0f, 90.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();
            inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/FINANCIAL_HARDSHIP_RELIEF.pdf";
            outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/FINANCIAL_HARDSHIP_RELIEF_" + ClientId + "_" + MRN + ".pdf";
            fos2 = new FileOutputStream(new File(outputFilePathTmp2));
            pdfReader2 = new PdfReader(inputFilePathTmp2);
            pdfStamper2 = new PdfStamper(pdfReader2, (OutputStream) fos2);
            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(150.0f, 640.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName);
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(420.0f, 640.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(170.0f, 610.0f);
                    pdfContentByte2.showText(HISubscriberRelationtoPatient);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(430.0f, 610.0f);
                    pdfContentByte2.showText(HISubscriberFirstName + " " + HISubscriberLastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(250.0f, 580.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(35.0f, 520.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252.0f, 520.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(35.0f, 492.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252.0f, 492.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(35.0f, 462.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252.0f, 462.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(35.0f, 432.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252.0f, 432.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252.0f, 392.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(110.0f, 290.0f);
                        pdfContentByte2.addImage(SignImages);
                    }
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(342.0f, 290.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();
            inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/Marketing_Slips.pdf";
            outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/Marketing_Slips_" + ClientId + "_" + MRN + ".pdf";
            fos2 = new FileOutputStream(new File(outputFilePathTmp2));
            pdfReader2 = new PdfReader(inputFilePathTmp2);
            pdfStamper2 = new PdfStamper(pdfReader2, (OutputStream) fos2);
            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(155.0f, 698.0f);
                    pdfContentByte2.showText(MFFirstVisit);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(405.0f, 698.0f);
                    pdfContentByte2.showText(MFReturnPat);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(182.0f, 677.0f);
                    pdfContentByte2.showText(MFInternetFind);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 630.0f);
                    pdfContentByte2.showText(Facebook);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 630.0f);
                    pdfContentByte2.showText(TV);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 618.0f);
                    pdfContentByte2.showText(MapSearch);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 618.0f);
                    pdfContentByte2.showText(Billboard);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 605.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 605.0f);
                    pdfContentByte2.showText(Radio);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 590.0f);
                    pdfContentByte2.showText(GoogleSearch);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 590.0f);
                    pdfContentByte2.showText(FamilyFriend);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(370.0f, 590.0f);
                    pdfContentByte2.showText(FamilyFriend_text);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 575.0f);
                    pdfContentByte2.showText(YouTube);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 575.0f);
                    pdfContentByte2.showText(Brochure);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 562.0f);
                    pdfContentByte2.showText(WebsiteAds);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 562.0f);
                    pdfContentByte2.showText(DirectMail);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 550.0f);
                    pdfContentByte2.showText(LinkedIn);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 550.0f);
                    pdfContentByte2.showText(UrgentCare);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(370.0f, 550.0f);
                    pdfContentByte2.showText(UrgentCare_text);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 535.0f);
                    pdfContentByte2.showText(Twitter);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 535.0f);
                    pdfContentByte2.showText(School);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(370.0f, 535.0f);
                    pdfContentByte2.showText(School_text);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 522.0f);
                    pdfContentByte2.showText(OnlineReviews);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 522.0f);
                    pdfContentByte2.showText(NewspaperMagazine);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 522.0f);
                    pdfContentByte2.showText(NewspaperMagazine_text);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 510.0f);
                    pdfContentByte2.showText(EmailBlast);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 510.0f);
                    pdfContentByte2.showText(Hotel);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(340.0f, 510.0f);
                    pdfContentByte2.showText(Hotel_text);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 495.0f);
                    pdfContentByte2.showText(CitizensDeTar);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 483.0f);
                    pdfContentByte2.showText(LiveWorkNearby);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 470.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 470.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(310.0f, 440.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 426.0f);
                    pdfContentByte2.showText(MFPhysician);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(290.0f, 426.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();
            ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/GeneralForm_Victoria.pdf";
            if (WorkersCompPolicyChk == 1) {
                Query = "Select WCPInjuryRelatedAutoMotorAccident, WCPInjuryOccurVehicle from " + Database + ".Patient_WorkCompPolicy where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    WCPInjuryRelatedAutoMotorAccident = rset.getString(1);
                    WCPInjuryOccurVehicle = rset.getString(2);
                }
                rset.close();
                stmt.close();
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/GeneralForm_Victoria.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_QUESTIONNAIRE_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                if (WCPInjuryRelatedAutoMotorAccident.equals("1") || WCPInjuryOccurVehicle.equals("1")) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_assignmentofproceeds_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                }
            }
            if (WorkersCompPolicyChk == 0 && MotorVehicleAccidentChk == 1) {
                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MVA_ASSIGNMENTOFPROCEEDS_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
            }
            if (MotorVehicleAccidentChk == 1) {
                Query = "Select AutoInsuranceInformationChk from " + Database + ".Patient_AutoInsuranceInfo where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    AutoInsuranceInformationChk = rset.getString(1);
                }
                rset.close();
                stmt.close();
                if (AutoInsuranceInformationChk.equals("1")) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MVACLAIMFORM_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                }
            }
            if (HealthInsuranceChk == 1) {
                Query = "Select GFIPMedicare,GFIPMedicaid,GFIPCHIP,GFIPTricare,GFIPVHA,GFIPIndianHealth, GovtFundedInsurancePlanChk,IFNULL(HIPrimaryInsurance,''),IFNULL(HISubscriberFirstName,''), IFNULL(HISubscriberLastName,''), IFNULL(DATE_FORMAT(HISubscriberDOB,'%m/%d/%Y'),''), IFNULL(HISubscriberSSN,''), IFNULL(HISubscriberRelationtoPatient,''), IFNULL(HISubscriberGroupNo,''), IFNULL(HISubscriberPolicyNo,''),IFNULL(SHISecondaryName,''), IFNULL(SHISubscriberFirstName,''), IFNULL(SHISubscriberLastName,''), IFNULL(SHISubscriberRelationtoPatient,''), IFNULL(SHISubscriberGroupNo,''), IFNULL(SHISubscriberPolicyNo,'') from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    GFIPMedicare = rset.getInt(1);
                    GFIPMedicaid = rset.getInt(2);
                    GFIPCHIP = rset.getInt(3);
                    GFIPTricare = rset.getInt(4);
                    GFIPVHA = rset.getInt(5);
                    GFIPIndianHealth = rset.getInt(6);
                    GovtFundedInsurancePlanChk = rset.getString(7);
                    HIPrimaryInsurance = rset.getString(8);
                    HISubscriberFirstName = rset.getString(9);
                    HISubscriberLastName = rset.getString(10);
                    HISubscriberDOB = rset.getString(11);
                    HISubscriberSSN = rset.getString(12);
                    HISubscriberRelationtoPatient = rset.getString(13);
                    HISubscriberGroupNo = rset.getString(14);
                    HISubscriberPolicyNo = rset.getString(15);
                    SHISecondaryName = rset.getString(16);
                    SHISubscriberFirstName = rset.getString(17);
                    SHISubscriberLastName = rset.getString(18);
                    SHISubscriberRelationtoPatient = rset.getString(19);
                    SHISubscriberGroupNo = rset.getString(20);
                    SHISubscriberPolicyNo = rset.getString(21);
                }
                rset.close();
                stmt.close();
                if (GovtFundedInsurancePlanChk.equals("1")) {
                    if (GFIPMedicaid == 1 || GFIPCHIP == 1) {
                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MEDICAIDSELFPAYAGREEMENT_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                    }
                    if (GFIPMedicare == 1) {
                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/ABNformEnglish_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                    }
                }
                if (HIPrimaryInsurance.trim().toUpperCase().equals("UNITED HEALTHCARE")) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/UHCINSAPPEALFORMS_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                }
            }
            if (HISubscriberDOB.equals("00/00/0000")) {
                HISubscriberDOB = "";
            }
            if (WCPDateofInjury.equals("00/00/0000")) {
                WCPDateofInjury = "";
            }
            if (AIIDateofAccident.equals("00/00/0000")) {
                AIIDateofAccident = "";
            }
            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/FINANCIAL_HARDSHIP_RELIEF_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
            ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/Medicalreleaseform_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
            ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/Marketing_Slips_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
            ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
            String DOSDate = "";
            String DOSTime = "";
            DOSDate = DOS.substring(0, 10);
            DOSTime = DOS.substring(11, 19);
            String inputFilePath = "";
            inputFilePath = ResultPdf;

            int found = 0;
            Query = "Select Count(*) from " + Database + ".BundleHistory where PatientRegId=" + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                found = rset.getInt(1);
            }
            stmt.close();
            rset.close();
            String filename = null;

            filename = FirstNameNoSpaces + "_" + PatientRegId + "_" + found + "_" + SignedFrom + ".pdf";


            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Victoria/" + filename;
            final OutputStream fos3 = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader3 = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper3 = new PdfStamper(pdfReader3, fos3);


            int pageCount = pdfReader3.getNumberOfPages();
            for (int k = 1; k <= pdfReader3.getNumberOfPages(); ++k) {
                if (k == 1) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(390.0f, 668.0f);
                    pdfContentByte3.showText(DOSDate);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(490.0f, 668.0f);
                    pdfContentByte3.showText(DOSTime);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 600.0f);
                    pdfContentByte3.showText(LastName);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(200.0f, 600.0f);
                    pdfContentByte3.showText(FirstName);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(390.0f, 608.0f);
                    pdfContentByte3.showText(Title);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(450.0f, 608.0f);
                    pdfContentByte3.showText(MaritalStatus);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 570.0f);
                    pdfContentByte3.showText(Address);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(300.0f, 570.0f);
                    pdfContentByte3.showText(CityStateZip);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(450.0f, 570.0f);
                    pdfContentByte3.showText(PhNumber);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 540.0f);
                    pdfContentByte3.showText(SSN);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 540.0f);
                    pdfContentByte3.showText(DOB);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(320.0f, 540.0f);
                    pdfContentByte3.showText(Age);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(380.0f, 540.0f);
                    pdfContentByte3.showText(gender);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(435.0f, 540.0f);
                    pdfContentByte3.showText(Email);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 515.0f);
                    pdfContentByte3.showText(Ethnicity);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 515.0f);
                    pdfContentByte3.showText(Employer);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 515.0f);
                    pdfContentByte3.showText(Occupation);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 490.0f);
                    pdfContentByte3.showText("-");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 490.0f);
                    pdfContentByte3.showText("-");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 490.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 470.0f);
                    pdfContentByte3.showText(PriCarePhy);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(320.0f, 465.0f);
                    pdfContentByte3.showText(ReasonVisit);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 395.0f);
                    pdfContentByte3.showText(Guarantor);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(290.0f, 395.0f);
                    pdfContentByte3.showText(GuarantorDOB);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(360.0f, 395.0f);
                    pdfContentByte3.showText(GuarantorSEX);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(425.0f, 395.0f);
                    pdfContentByte3.showText(GuarantorSSN);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 365.0f);
                    pdfContentByte3.showText(GuarantorAddress);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(320.0f, 365.0f);
                    pdfContentByte3.showText(GuarantorPhoneNumber);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 335.0f);
                    pdfContentByte3.showText(GuarantorEmployer);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 335.0f);
                    pdfContentByte3.showText("" + GuarantorEmployerPhNumber);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 335.0f);
                    pdfContentByte3.showText(GuarantorEmployerAddress + "  " + GuarantorEmployerCity + " " + GuarantorEmployerState);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 310.0f);
                    pdfContentByte3.showText(WorkCompPolicyStr);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 310.0f);
                    pdfContentByte3.showText(MotorVehAccidentStr);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 310.0f);
                    pdfContentByte3.showText(AIIDateofAccident);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(100.0f, 285.0f);
                    pdfContentByte3.showText(HIPrimaryInsurance);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 260.0f);
                    pdfContentByte3.showText(HISubscriberFirstName + " " + HISubscriberLastName);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(290.0f, 260.0f);
                    pdfContentByte3.showText(HISubscriberDOB);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 260.0f);
                    pdfContentByte3.showText(HISubscriberSSN);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 244.0f);
                    pdfContentByte3.showText(HISubscriberRelationtoPatient);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 235.0f);
                    pdfContentByte3.showText(HISubscriberGroupNo);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 235.0f);
                    pdfContentByte3.showText(HISubscriberPolicyNo);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 205.0f);
                    pdfContentByte3.showText(SHISecondaryName);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 180.0f);
                    pdfContentByte3.showText(SHISubscriberFirstName + " " + SHISubscriberLastName);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 180.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 180.0f);
                    pdfContentByte3.showText("-");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 170.0f);
                    pdfContentByte3.showText(SHISubscriberRelationtoPatient);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 160.0f);
                    pdfContentByte3.showText(SHISubscriberGroupNo);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 160.0f);
                    pdfContentByte3.showText(SHISubscriberPolicyNo);
                    pdfContentByte3.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(150, 80.0f);
                        pdfContentByte3.addImage(SignImages);
                    }

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(410.0f, 80.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 2) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(150, 145.0f);
                        pdfContentByte3.addImage(SignImages);
                    }

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(350.0f, 145.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(350.0f, 105.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 4) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(70.0f, 350.0f);
                    pdfContentByte3.showText(FirstName + " " + LastName + " " + MiddleInitial);
                    pdfContentByte3.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(70.0f, 270.0f);
                        pdfContentByte3.addImage(SignImages);
                    }

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(380.0f, 270.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 5) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(150.0f, 70.0f);
                        pdfContentByte3.addImage(SignImages);
                    }
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(420.0f, 70.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 6) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(430.0f, 290.0f);
                    pdfContentByte3.showText(FirstName + " " + LastName + " " + MiddleInitial);
                    pdfContentByte3.endText();


                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(120.0f, 360.0f);
                        pdfContentByte3.addImage(SignImages);
                    }
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(440.0f, 360.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 7) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(120.0f, 590.0f);
                    pdfContentByte3.showText(FirstName + " " + LastName + " " + MiddleInitial);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(300.0f, 590.0f);
                    pdfContentByte3.showText(DOB);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(485.0f, 590.0f);
                    pdfContentByte3.showText(HISubscriberRelationtoPatient);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 565.0f);
                    pdfContentByte3.showText(HIPrimaryInsurance);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 540.0f);
                    pdfContentByte3.showText(HISubscriberPolicyNo);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 520.0f);
                    pdfContentByte3.showText(DOS);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 420.0f);
                    pdfContentByte3.showText(FirstName + " " + LastName + " " + MiddleInitial);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(380.0f, 420.0f);
                    pdfContentByte3.showText("Victoria ED");
                    pdfContentByte3.endText();


                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100.0f, 160.0f);
                        pdfContentByte3.addImage(SignImages);
                    }
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(440.0f, 160.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 8) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(220.0f, 425.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(155.0f, 400.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(410.0f, 400.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(155.0f, 380.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 380.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(115.0f, 158.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(140.0f, 130.0f);
                        pdfContentByte3.addImage(SignImages);
                    }
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 130.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 100.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 9) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100.0f, 150.0f);
                        pdfContentByte3.addImage(SignImages);
                    }
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 150.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
            }
            pdfStamper3.close();
            pdfStamper2.close();
            pdfStamper1.close();
            pdfReader1.close();
            pdfReader2.close();
            pdfReader3.close();

            if (SignedFrom.contains("SIGNED")) {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".BundleHistory (MRN ,PatientRegId ,BundleName ,CreatedDate,PgCount)" +
                                " VALUES (? ,? ,? ,now(),?) ");
                MainReceipt.setString(1, MRN);
                MainReceipt.setInt(2, ID);
                MainReceipt.setString(3, filename);
                MainReceipt.setInt(4, pdfReader3.getNumberOfPages());
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }

//
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("outputFilePath", outputFilePath);
////            Parser.SetField("imagelist", String.valueOf(imagelist));
//            Parser.SetField("pageCount", String.valueOf(pageCount));
//            Parser.SetField("PatientRegId", String.valueOf(ID));
//            Parser.SetField("FileName", FirstNameNoSpaces + LastNameNoSpace + ID + "_.pdf");
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/DownloadBundleHTML.html");

//            final File pdfFile = new File(outputFilePath);
//            response.setContentType("application/pdf");
//            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf");
//            response.setContentLength((int) pdfFile.length());
//            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
//            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
//            int bytes;
//            while ((bytes = fileInputStream.read()) != -1) {
//                responseOutputStream.write(bytes);
//            }
            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_QUESTIONNAIRE_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_assignmentofproceeds_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MVACLAIMFORM_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MVA_ASSIGNMENTOFPROCEEDS_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/ABNformEnglish_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/Medicalreleaseform_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/UHCINSAPPEALFORMS_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MEDICAIDSELFPAYAGREEMENT_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/FINANCIAL_HARDSHIP_RELIEF_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/Marketing_Slips_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf");
            File.delete();


            return pageCount + "~" + outputFilePath + "~" + filename;//FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf";
        } catch (Exception e) {
            UtilityHelper helper = new UtilityHelper();
            helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveBundle_Victoria PatineReg2 Data get ^^VICTORIA ##MES#001)", this.getServletContext(), e, "PatientReg2", "SaveBundle_Victoria", conn);
            Services.DumException("PatientReg2", "MES#001", request, e, this.getServletContext());
        }
        return "";
    }

    void PatientsDocUpload_Update(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String PatientId = "";
        String DocumentName = "";
        String UserId = "";
        String ClientIndex = "";
        String ClientId = "";
        String MRN = "";
        int PremisisId = 0;
        int PatientRegId = 0;
        String PatientMRN = "";
        String PatientName = "";
        String DirectoryName = "Victoria";
        String Database = "";
        String DocumentType = "";
        String VisitNo = "";
        String Path = null;
        String UploadPath = null;


        boolean IdFound = false;
        boolean FileFound = false;
        boolean insuranceF = false;
        boolean insuranceB = false;
//        boolean insurance = false;
        byte[] Data = null;
        String key = "";
        String filename = "";
        String IDs = "";
        String InsuranceIDsF = "";
        String InsuranceIDsB = "";
        String Idfront = "";
        String insuranceFront = "";
        String insuranceBack = "";
        String IdfrontName = "";
        String insuranceFrontName = "";
        String insuranceBackName = "";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("Select Id, dbname,DirectoryName from oe.clients where Id = 9");

            rset = ps.executeQuery();
            if (rset.next()) {
                PremisisId = rset.getInt(1);
                Database = rset.getString(2);
                DirectoryName = rset.getString(3);
            }
            rset.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Dictionary d = doUpload(request, response, out);
            Enumeration<String> en = d.keys();
            while (en.hasMoreElements()) {
                key = en.nextElement();
                FileFound = false;
//                System.out.println("KEY -> " + key);
                if (key.startsWith("ClientId")) {
                    ClientId = (String) d.get(key);
                } else if (key.startsWith("MRN")) {
                    MRN = (String) d.get(key);
                } else if ((key.startsWith("Idfront") && key.endsWith(".jpg")) || (key.startsWith("Idfront") && key.endsWith(".png"))) {
                    filename = key;
                    FileFound = true;
                    IdFound = true;
                    IDs = key;
//                    System.out.println("ID FOUND!!!!!!!!!!!!!!");
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                } else if ((key.startsWith("insuranceFront") && key.endsWith(".jpg")) || (key.startsWith("insuranceFront") && key.endsWith(".png"))) {
                    filename = key;
                    FileFound = true;
                    InsuranceIDsF = key;
                    insuranceF = true;
//                    System.out.println("INSURANCE ID FRONT FOUND!!!!!!!!!!!!!!");

                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                } else if ((key.startsWith("insuranceBack") && key.endsWith(".jpg")) || (key.startsWith("insuranceBack") && key.endsWith(".png"))) {
                    filename = key;
                    FileFound = true;
                    insuranceB = true;
                    InsuranceIDsB = key;

//                    System.out.println("INSURANCE ID BACK FOUND!!!!!!!!!!!!!!");

                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                }
//                System.out.println("FileFound : " + FileFound);
                if (FileFound) {
                    Path = "/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "/Uploads";
                    UploadPath = String.valueOf(Path) + "/";
//                    System.out.println("UploadPath : " + UploadPath);

                    filename = filename.replaceAll("\\s+", "");

                    File fe = new File(String.valueOf(String.valueOf(UploadPath)) + filename);
                    if (fe.exists()) {
//                        System.out.println("FILE ALREADY EXISTSS!!! with filename : " + filename);
                        fe.delete();
                    }

                    FileOutputStream fouts = new FileOutputStream(fe);
                    fouts.write(Data);
                    fouts.flush();
                    fouts.close();
                }
            }
            ClientId = ClientId.substring(4);
            MRN = MRN.substring(4);

//            System.out.println("PatientId : " + PatientId);
//            System.out.println("PremisisId : " + PremisisId);
//            System.out.println("Database : " + Database);
//            System.out.println("DirectoryName : " + DirectoryName);
//            System.out.println("MRN : " + MRN);

            if (!IDs.equals("")) {
                ps = conn.prepareStatement("UPDATE  " + Database + ".PatientReg  SET IDFront = ?  WHERE MRN = ?");
                ps.setString(1, IDs);
                ps.setString(2, MRN);
                ps.executeUpdate();
                ps.close();
            }
            if (!InsuranceIDsF.equals("")) {
                ps = conn.prepareStatement("UPDATE  " + Database + ".PatientReg  SET InsuranceFront = ?  WHERE MRN = ?");
                ps.setString(1, InsuranceIDsF);
                ps.setString(2, MRN);
                ps.executeUpdate();
                ps.close();
            }
            if (!InsuranceIDsB.equals("")) {
                ps = conn.prepareStatement("UPDATE  " + Database + ".PatientReg  SET InsuranceBack = ?  WHERE MRN = ?");
                ps.setString(1, InsuranceIDsB);
                ps.setString(2, MRN);
                ps.executeUpdate();
                ps.close();
            }
        } catch (Exception e2) {
            System.out.println("Error in Upload DOcuments!!");
            String str = "";
            for (int i = 0; i < (e2.getStackTrace()).length; i++)
                str = str + e2.getStackTrace()[i] + "<br>";
            System.out.println(str);
        }
    }

    void SaveDataVictoria_2_2(HttpServletRequest request, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, UtilityHelper helper, HashMap<String, String> valuemap, PrintWriter out, String IDs, String InsuranceIDsF, String InsuranceIDsB, String clientId) throws FileNotFoundException {
        String facilityName = "";

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Database = "";
        String DirectoryName = "";
        int MRN = 0;
        String ExtendedMRN = "0";
        int PatientRegId = 0;
        String Date = "";
        final String ClientId = clientId;//"";
        int ClientIndex = 0;
        String Title = null;
        String FirstName = null;
        String LastName = null;
        String MiddleInitial = null;
        String County = null;
        String DOB = null;
        String Month = "00";
        String Day = "00";
        String Year = "0000";
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
        String AIIDateofAccident = "";
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
        String SHISecondaryName = "";
        String SHISubscriberFirstName = "";
        String SHISubscriberLastName = "";
        String SHISubscriberDOB = "";
        String SHISubscriberRelationtoPatient = "";
        String SHISubscriberGroupNo = "";
        String SHISubscriberPolicyNo = "";
        String MFFirstVisit = "";

        String MFReturnPat = "";
        String MFInternetFind = "";
        String Facebook = "";
        String MapSearch = "";
        String GoogleSearch = "";
        String VERWebsite = "";
        String WebsiteAds = "";
        String OnlineAdvertisements = "";
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
        String EmployerSentMe = "";
        String EmployerSentMe_text = "";
        String PatientCell = "";
        String MFPhysicianRefChk = "";
        String MFPhysician = "";
        String RecInitial = "";
        int VisitId = 0;
        try {

//                        "INSERT INTO oe.EligibilityInquiry (PatientMRN,DateofService,TraceId ,PolicyStatus,strmsg, " +
//                                "Name, DateofBirth, Gender, InsuranceNum, GediPayerId, CreatedBy, CreatedDate,ResponseType) " +
//                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,now(),?) ");
            PreparedStatement ps = conn.prepareStatement("Select Id, dbname,DirectoryName from oe.clients where ltrim(rtrim(UPPER(name))) = ?");
            ps.setString(1, ClientId);

            rset = ps.executeQuery();
            if (rset.next()) {
                ClientIndex = rset.getInt(1);
                Database = rset.getString(2);
                DirectoryName = rset.getString(3);
            }
            rset.close();
            ps.close();

//            for (String name : valuemap.keySet()) {
//                String key = name.toString();
//                String value = valuemap.get(name).toString();
//                System.out.println(key + " " + value);
//            }

//            System.out.println("query 1 converted");
//            HttpServletRequest request;
            facilityName = helper.getFacilityName(request, conn, servletContext, ClientIndex);
            if (valuemap.get("Title") == null) {
                Title = "Mr.";
            } else {
                Title = valuemap.get("Title").trim();
            }
            if (valuemap.get("FirstName") == null) {
                FirstName = "";
            } else {
                FirstName = valuemap.get("FirstName").trim();
            }
            if (valuemap.get("LastName") == null) {
                LastName = "";
            } else {
                LastName = valuemap.get("LastName").trim();
            }
            if (valuemap.get("MiddleInitial") == null) {
                MiddleInitial = "";
            } else {
                MiddleInitial = valuemap.get("MiddleInitial").trim();
            }

            if (valuemap.get("Month") == null) {
                Month = "";
            } else {
                Month = valuemap.get("Month").trim();
            }
            if (valuemap.get("Day") == null) {
                Day = "";
            } else {
                Day = valuemap.get("Day").trim();
            }
            if (valuemap.get("Year") == null) {
                Year = "";
            } else {
                Year = valuemap.get("Year").trim();
            }
            DOB = Year + "-" + Month + "-" + Day;

//            if (valuemap.get("DOB") == null) {
//                DOB = "0000-00-00";
//            } else {
//                DOB = valuemap.get("DOB").trim();
//                DOB = String.valueOf(String.valueOf(DOB.substring(6, 10))) + "-" + DOB.substring(0, 2) + "-" + DOB.substring(3, 5);
//            }
            if (valuemap.get("Age") == null) {
                Age = "";
            } else {
                Age = valuemap.get("Age").trim();
            }
            if (valuemap.get("gender") == null) {
                gender = "";
            } else {
                gender = valuemap.get("gender").trim();
            }


            int PatientFound = 0;
            String FoundMRN = "";
            try {
                ps = conn.prepareStatement(" Select COUNT(*), IFNULL(MRN,0) from " + Database + ".PatientReg  " +
                        "where Status = 0 and ltrim(rtrim(UPPER(FirstName))) = ?  and " +
                        " ltrim(rtrim(UPPER(LastName))) = ? and DOB = ?");
                ps.setString(1, FirstName.trim().toUpperCase());
                ps.setString(2, LastName.trim().toUpperCase());
                ps.setString(3, DOB);

                rset = ps.executeQuery();
                if (rset.next()) {
                    PatientFound = rset.getInt(1);
                    FoundMRN = rset.getString(2);
                }
                rset.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("query " + ps.toString());
                System.out.println("Error -> " + e.getMessage());
            }

//            System.out.println("query 2 converted");


            if (PatientFound > 0) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Title", "Patient Already Found. MRN: " + FoundMRN);
                Parser.SetField("Text", "Please Proceed to Front Desk with the MRN.");
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientId + "");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Warning.html");
                return;
            }


            if (valuemap.get("Email") == null) {
                Email = "";
            } else {
                Email = valuemap.get("Email").trim();
            }
            if (valuemap.get("ConfirmEmail") == null) {
                ConfirmEmail = "";
            } else {
                ConfirmEmail = valuemap.get("ConfirmEmail").trim();
            }
            if (valuemap.get("MaritalStatus") == null) {
                MaritalStatus = "";
            } else {
                MaritalStatus = valuemap.get("MaritalStatus").trim();
            }
            if (valuemap.get("AreaCode") == null) {
                AreaCode = "";
            } else {
                AreaCode = valuemap.get("AreaCode").trim();
            }
            if (valuemap.get("PhNumber") == null) {
                PhNumber = "";
            } else {
                PhNumber = valuemap.get("PhNumber").trim();
            }
            if (valuemap.get("Address") == null) {
                Address = "";
            } else {
                Address = valuemap.get("Address").trim();
            }
            if (valuemap.get("Address2") == null) {
                Address2 = "";
            } else {
                Address2 = valuemap.get("Address2").trim();
            }
            if (valuemap.get("City") == null) {
                City = "";
            } else {
                City = valuemap.get("City").trim();
            }
            if (valuemap.get("State") == null) {
                State = "";
            } else {
                State = valuemap.get("State").trim();
            }
            if (valuemap.get("County") == null) {
                County = "";
            } else {
                County = valuemap.get("County").trim();
            }
            if (valuemap.get("Country") == null) {
                Country = "";
            } else {
                Country = valuemap.get("Country").trim();
            }
            if (valuemap.get("ZipCode") == null) {
                ZipCode = "";
            } else {
                ZipCode = valuemap.get("ZipCode").trim();
            }
            if (valuemap.get("Ethnicity") == null) {
                Ethnicity = "Not Specified";
            } else {
                Ethnicity = valuemap.get("Ethnicity").trim();
            }
            if (valuemap.get("Race") == null) {
                Race = "Not Specified";
            } else {
                Race = valuemap.get("Race").trim();
            }
            if (valuemap.get("SSN") == null) {
                SSN = "";
            } else {
                SSN = valuemap.get("SSN").trim();
            }
            if (valuemap.get("ReasonVisit") == null) {
                ReasonVisit = "";
            } else {
                ReasonVisit = valuemap.get("ReasonVisit").trim();
            }

            if (valuemap.get("SympChkCOVID") == null) {
                SympChkCOVID = "0";
            } else {
                SympChkCOVID = valuemap.get("SympChkCOVID").trim();
            }

            if (valuemap.get("TestForTravelChk") == null) {
                TestForTravel = "0";
            } else {
                TestForTravel = valuemap.get("TestForTravelChk").trim();
            }


            if (valuemap.get("DateSympOnset") == null) {
                DateSympOnset = "0000-00-00";
            } else {
                DateSympOnset = valuemap.get("DateSympOnset").trim();
//                if (DateSympOnset.length() <= 10)
//                    DateSympOnset = DateSympOnset.substring(6, 10) + "-" + DateSympOnset.substring(0, 2) + "-" + DateSympOnset.substring(3, 5);
            }

            if (valuemap.get("SympFever") == null) {
                SympFever = "0";
            } else {
                SympFever = "1";
            }
            if (valuemap.get("SympCough") == null) {
                SympCough = "0";
            } else {
                SympCough = "1";
            }
            if (valuemap.get("SympShortBreath") == null) {
                SympShortBreath = "0";
            } else {
                SympShortBreath = "1";
            }
            if (valuemap.get("SympFatigue") == null) {
                SympFatigue = "0";
            } else {
                SympFatigue = "1";
            }
            if (valuemap.get("SympMuscBodyAches") == null) {
                SympMuscBodyAches = "0";
            } else {
                SympMuscBodyAches = "1";
            }
            if (valuemap.get("SympHeadache") == null) {
                SympHeadache = "0";
            } else {
                SympHeadache = "1";
            }
            if (valuemap.get("SympLossTaste") == null) {
                SympLossTaste = "0";
            } else {
                SympLossTaste = "1";
            }
            if (valuemap.get("SympSoreThroat") == null) {
                SympSoreThroat = "0";
            } else {
                SympSoreThroat = "1";
            }
            if (valuemap.get("SympCongestionRunNos") == null) {
                SympCongestionRunNos = "0";
            } else {
                SympCongestionRunNos = "1";
            }
            if (valuemap.get("SympNauseaVomit") == null) {
                SympNauseaVomit = "0";
            } else {
                SympNauseaVomit = "1";
            }
            if (valuemap.get("SympDiarrhea") == null) {
                SympDiarrhea = "0";
            } else {
                SympDiarrhea = "1";
            }
            if (valuemap.get("SympPerPainChest") == null) {
                SympPerPainChest = "0";
            } else {
                SympPerPainChest = "1";
            }
            if (valuemap.get("SympNewConfusion") == null) {
                SympNewConfusion = "0";
            } else {
                SympNewConfusion = "1";
            }
            if (valuemap.get("SympInabWake") == null) {
                SympInabWake = "0";
            } else {
                SympInabWake = "1";
            }
            if (valuemap.get("SympOthers") == null) {
                SympOthers = "0";
            } else {
                SympOthers = "1";
            }
            if (valuemap.get("SympOthersTxt") == null) {
                SympOthersTxt = "";
            } else {
                SympOthersTxt = valuemap.get("SympOthersTxt").trim();
            }
            if (valuemap.get("EmpHealthChk") == null) {
                EmpHealthChk = "0";
            } else {
                EmpHealthChk = valuemap.get("EmpHealthChk").trim();
            }
            if (valuemap.get("PregChk") == null) {
                PregChk = "0";
            } else {
                PregChk = valuemap.get("PregChk").trim();
            }

            if (valuemap.get("EmployementChk") == null) {
                EmployementChk = "0";
            } else {
                EmployementChk = valuemap.get("EmployementChk").trim();
            }
            if (EmployementChk.equals("1")) {
                if (valuemap.get("Employer") == null) {
                    Employer = "";
                } else {
                    Employer = valuemap.get("Employer").trim();
                }
                if (valuemap.get("Occupation") == null) {
                    Occupation = "";
                } else {
                    Occupation = valuemap.get("Occupation").trim();
                }
                if (valuemap.get("EmpContact") == null) {
                    EmpContact = "";
                } else {
                    EmpContact = valuemap.get("EmpContact").trim();
                }
            }
            if (valuemap.get("PrimaryCarePhysicianChk") == null) {
                PrimaryCarePhysicianChk = "0";
            } else {
                PrimaryCarePhysicianChk = valuemap.get("PrimaryCarePhysicianChk").trim();
            }
            if (PrimaryCarePhysicianChk.equals("1")) {
                if (valuemap.get("PriCarePhy") == null) {
                    PriCarePhy = "";
                } else {
                    PriCarePhy = valuemap.get("PriCarePhy").trim();
                }
                if (valuemap.get("PriCarePhyAddress") == null) {
                    PriCarePhyAddress = "";
                } else {
                    PriCarePhyAddress = valuemap.get("PriCarePhyAddress").trim();
                }
                if (valuemap.get("PriCarePhyAddress2") == null) {
                    PriCarePhyAddress2 = "";
                } else {
                    PriCarePhyAddress2 = valuemap.get("PriCarePhyAddress2").trim();
                }
                if (valuemap.get("PriCarePhyCity") == null) {
                    PriCarePhyCity = "";
                } else {
                    PriCarePhyCity = valuemap.get("PriCarePhyCity").trim();
                }
                if (valuemap.get("PriCarePhyState") == null) {
                    PriCarePhyState = "";
                } else {
                    PriCarePhyState = valuemap.get("PriCarePhyState").trim();
                }
                if (valuemap.get("PriCarePhyZipCode") == null) {
                    PriCarePhyZipCode = "";
                } else {
                    PriCarePhyZipCode = valuemap.get("PriCarePhyZipCode").trim();
                }
            }
            if (valuemap.get("PatientMinorChk") == null) {
                PatientMinorChk = "0";
            } else {
                PatientMinorChk = valuemap.get("PatientMinorChk").trim();
            }
            if (valuemap.get("GuarantorChk") == null) {
                GuarantorChk = "0";
            } else {
                GuarantorChk = valuemap.get("GuarantorChk").trim();
            }
            if (valuemap.get("GuarantorEmployer") == null) {
                GuarantorEmployer = "";
            } else {
                GuarantorEmployer = valuemap.get("GuarantorEmployer").trim();
            }
            if (valuemap.get("GuarantorEmployerAreaCode") == null) {
                GuarantorEmployerAreaCode = "";
            } else {
                GuarantorEmployerAreaCode = valuemap.get("GuarantorEmployerAreaCode").trim();
            }
            if (valuemap.get("GuarantorEmployerPhNumber") == null) {
                GuarantorEmployerPhNumber = "";
            } else {
                GuarantorEmployerPhNumber = valuemap.get("GuarantorEmployerPhNumber").trim();
            }
            if (valuemap.get("GuarantorEmployerAddress") == null) {
                GuarantorEmployerAddress = "";
            } else {
                GuarantorEmployerAddress = valuemap.get("GuarantorEmployerAddress").trim();
            }
            if (valuemap.get("GuarantorEmployerAddress2") == null) {
                GuarantorEmployerAddress2 = "";
            } else {
                GuarantorEmployerAddress2 = valuemap.get("GuarantorEmployerAddress2").trim();
            }
            if (valuemap.get("GuarantorEmployerCity") == null) {
                GuarantorEmployerCity = "";
            } else {
                GuarantorEmployerCity = valuemap.get("GuarantorEmployerCity").trim();
            }
            if (valuemap.get("GuarantorEmployerState") == null) {
                GuarantorEmployerState = "";
            } else {
                GuarantorEmployerState = valuemap.get("GuarantorEmployerState").trim();
            }
            if (valuemap.get("GuarantorEmployerZipCode") == null) {
                GuarantorEmployerZipCode = "";
            } else {
                GuarantorEmployerZipCode = valuemap.get("GuarantorEmployerZipCode").trim();
            }
            if (valuemap.get("WorkersCompPolicyChk") == null) {
                WorkersCompPolicyChk = "0";
            } else {
                WorkersCompPolicyChk = valuemap.get("WorkersCompPolicyChk").trim();
            }
            if (WorkersCompPolicyChk.equals("1")) {
                if (valuemap.get("WCPDateofInjury") == null) {
                    WCPDateofInjury = "0000-00-00";
                } else {
                    WCPDateofInjury = valuemap.get("WCPDateofInjury").trim();
//                    WCPDateofInjury = String.valueOf(String.valueOf(WCPDateofInjury.substring(6, 10))) + "-" + WCPDateofInjury.substring(0, 2) + "-" + WCPDateofInjury.substring(3, 5);
                }
                if (valuemap.get("WCPCaseNo") == null) {
                    WCPCaseNo = "";
                } else {
                    WCPCaseNo = valuemap.get("WCPCaseNo").trim();
                }
                if (valuemap.get("WCPGroupNo") == null) {
                    WCPGroupNo = "";
                } else {
                    WCPGroupNo = valuemap.get("WCPGroupNo").trim();
                }
                if (valuemap.get("WCPMemberId") == null) {
                    WCPMemberId = "";
                } else {
                    WCPMemberId = valuemap.get("WCPMemberId").trim();
                }
                if (valuemap.get("WCPInjuryRelatedAutoMotorAccident") == null) {
                    WCPInjuryRelatedAutoMotorAccident = "0";
                } else {
                    WCPInjuryRelatedAutoMotorAccident = "1";
                }
                if (valuemap.get("WCPInjuryRelatedWorkRelated") == null) {
                    WCPInjuryRelatedWorkRelated = "0";
                } else {
                    WCPInjuryRelatedWorkRelated = "1";
                }
                if (valuemap.get("WCPInjuryRelatedOtherAccident") == null) {
                    WCPInjuryRelatedOtherAccident = "0";
                } else {
                    WCPInjuryRelatedOtherAccident = "1";
                }
                if (valuemap.get("WCPInjuryRelatedNoAccident") == null) {
                    WCPInjuryRelatedNoAccident = "0";
                } else {
                    WCPInjuryRelatedNoAccident = "1";
                }
                if (valuemap.get("WCPInjuryOccurVehicle") == null) {
                    WCPInjuryOccurVehicle = "0";
                } else {
                    WCPInjuryOccurVehicle = "1";
                }
                if (valuemap.get("WCPInjuryOccurWork") == null) {
                    WCPInjuryOccurWork = "0";
                } else {
                    WCPInjuryOccurWork = "1";
                }
                if (valuemap.get("WCPInjuryOccurHome") == null) {
                    WCPInjuryOccurHome = "0";
                } else {
                    WCPInjuryOccurHome = "1";
                }
                if (valuemap.get("WCPInjuryOccurOther") == null) {
                    WCPInjuryOccurOther = "0";
                } else {
                    WCPInjuryOccurOther = "1";
                }
                if (valuemap.get("WCPInjuryDescription") == null) {
                    WCPInjuryDescription = "";
                } else {
                    WCPInjuryDescription = valuemap.get("WCPInjuryDescription").trim();
                }
                if (valuemap.get("WCPHRFirstName") == null) {
                    WCPHRFirstName = "";
                } else {
                    WCPHRFirstName = valuemap.get("WCPHRFirstName").trim();
                }
                if (valuemap.get("WCPHRLastName") == null) {
                    WCPHRLastName = "";
                } else {
                    WCPHRLastName = valuemap.get("WCPHRLastName").trim();
                }
                if (valuemap.get("WCPHRAreaCode") == null) {
                    WCPHRAreaCode = "";
                } else {
                    WCPHRAreaCode = valuemap.get("WCPHRAreaCode").trim();
                }
                if (valuemap.get("WCPHRPhoneNumber") == null) {
                    WCPHRPhoneNumber = "";
                } else {
                    WCPHRPhoneNumber = valuemap.get("WCPHRPhoneNumber").trim();
                }
                if (valuemap.get("WCPHRAddress") == null) {
                    WCPHRAddress = "";
                } else {
                    WCPHRAddress = valuemap.get("WCPHRAddress").trim();
                }
                if (valuemap.get("WCPHRAddress2") == null) {
                    WCPHRAddress2 = "";
                } else {
                    WCPHRAddress2 = valuemap.get("WCPHRAddress2").trim();
                }
                if (valuemap.get("WCPHRCity") == null) {
                    WCPHRCity = "";
                } else {
                    WCPHRCity = valuemap.get("WCPHRCity").trim();
                }
                if (valuemap.get("WCPHRState") == null) {
                    WCPHRState = "";
                } else {
                    WCPHRState = valuemap.get("WCPHRState").trim();
                }
                if (valuemap.get("WCPHRZipCode") == null) {
                    WCPHRZipCode = "";
                } else {
                    WCPHRZipCode = valuemap.get("WCPHRZipCode").trim();
                }
                if (valuemap.get("WCPPlanName") == null) {
                    WCPPlanName = "";
                } else {
                    WCPPlanName = valuemap.get("WCPPlanName").trim();
                }
                if (valuemap.get("WCPCarrierName") == null) {
                    WCPCarrierName = "";
                } else {
                    WCPCarrierName = valuemap.get("WCPCarrierName").trim();
                }
                if (valuemap.get("WCPPayerAreaCode") == null) {
                    WCPPayerAreaCode = "";
                } else {
                    WCPPayerAreaCode = valuemap.get("WCPPayerAreaCode").trim();
                }
                if (valuemap.get("WCPPayerPhoneNumber") == null) {
                    WCPPayerPhoneNumber = "";
                } else {
                    WCPPayerPhoneNumber = valuemap.get("WCPPayerPhoneNumber").trim();
                }
                if (valuemap.get("WCPCarrierAddress") == null) {
                    WCPCarrierAddress = "";
                } else {
                    WCPCarrierAddress = valuemap.get("WCPCarrierAddress").trim();
                }
                if (valuemap.get("WCPCarrierAddress2") == null) {
                    WCPCarrierAddress2 = "";
                } else {
                    WCPCarrierAddress2 = valuemap.get("WCPCarrierAddress2").trim();
                }
                if (valuemap.get("WCPCarrierCity") == null) {
                    WCPCarrierCity = "";
                } else {
                    WCPCarrierCity = valuemap.get("WCPCarrierCity").trim();
                }
                if (valuemap.get("WCPCarrierState") == null) {
                    WCPCarrierState = "";
                } else {
                    WCPCarrierState = valuemap.get("WCPCarrierState").trim();
                }
                if (valuemap.get("WCPCarrierZipCode") == null) {
                    WCPCarrierZipCode = "";
                } else {
                    WCPCarrierZipCode = valuemap.get("WCPCarrierZipCode").trim();
                }
                if (valuemap.get("WCPAdjudicatorFirstName") == null) {
                    WCPAdjudicatorFirstName = "";
                } else {
                    WCPAdjudicatorFirstName = valuemap.get("WCPAdjudicatorFirstName").trim();
                }
                if (valuemap.get("WCPAdjudicatorLastName") == null) {
                    WCPAdjudicatorLastName = "";
                } else {
                    WCPAdjudicatorLastName = valuemap.get("WCPAdjudicatorLastName").trim();
                }
                if (valuemap.get("WCPAdjudicatorAreaCode") == null) {
                    WCPAdjudicatorAreaCode = "";
                } else {
                    WCPAdjudicatorAreaCode = valuemap.get("WCPAdjudicatorAreaCode").trim();
                }
                if (valuemap.get("WCPAdjudicatorPhoneNumber") == null) {
                    WCPAdjudicatorPhoneNumber = "";
                } else {
                    WCPAdjudicatorPhoneNumber = valuemap.get("WCPAdjudicatorPhoneNumber").trim();
                }
                if (valuemap.get("WCPAdjudicatorFaxAreaCode") == null) {
                    WCPAdjudicatorFaxAreaCode = "";
                } else {
                    WCPAdjudicatorFaxAreaCode = valuemap.get("WCPAdjudicatorFaxAreaCode").trim();
                }
                if (valuemap.get("WCPAdjudicatorFaxPhoneNumber") == null) {
                    WCPAdjudicatorFaxPhoneNumber = "";
                } else {
                    WCPAdjudicatorFaxPhoneNumber = valuemap.get("WCPAdjudicatorFaxPhoneNumber").trim();
                }
            }
            if (valuemap.get("MotorVehicleAccidentChk") == null) {
                MotorVehicleAccidentChk = "0";
            } else {
                MotorVehicleAccidentChk = valuemap.get("MotorVehicleAccidentChk").trim();
            }
            if (MotorVehicleAccidentChk.equals("1")) {
                if (valuemap.get("AutoInsuranceInformationChk") == null) {
                    AutoInsuranceInformationChk = "0";
                } else {
                    AutoInsuranceInformationChk = valuemap.get("AutoInsuranceInformationChk").trim();
                }
                if (AutoInsuranceInformationChk.equals("1")) {
                    if (valuemap.get("AIIDateofAccident") == null) {
                        AIIDateofAccident = "0000-00-00";
                    } else {
                        AIIDateofAccident = valuemap.get("AIIDateofAccident").trim();
//                        AIIDateofAccident = String.valueOf(String.valueOf(AIIDateofAccident.substring(6, 10))) + "-" + AIIDateofAccident.substring(0, 2) + "-" + AIIDateofAccident.substring(3, 5);
                    }
                    if (valuemap.get("AIIAutoClaim") == null) {
                        AIIAutoClaim = "";
                    } else {
                        AIIAutoClaim = valuemap.get("AIIAutoClaim").trim();
                    }
                    if (valuemap.get("AIIAccidentLocationAddress") == null) {
                        AIIAccidentLocationAddress = "";
                    } else {
                        AIIAccidentLocationAddress = valuemap.get("AIIAccidentLocationAddress").trim();
                    }
                    if (valuemap.get("AIIAccidentLocationAddress2") == null) {
                        AIIAccidentLocationAddress2 = "";
                    } else {
                        AIIAccidentLocationAddress2 = valuemap.get("AIIAccidentLocationAddress2").trim();
                    }
                    if (valuemap.get("AIIAccidentLocationCity") == null) {
                        AIIAccidentLocationCity = "";
                    } else {
                        AIIAccidentLocationCity = valuemap.get("AIIAccidentLocationCity").trim();
                    }
                    if (valuemap.get("AIIAccidentLocationState") == null) {
                        AIIAccidentLocationState = "";
                    } else {
                        AIIAccidentLocationState = valuemap.get("AIIAccidentLocationState").trim();
                    }
                    if (valuemap.get("AIIAccidentLocationZipCode") == null) {
                        AIIAccidentLocationZipCode = "";
                    } else {
                        AIIAccidentLocationZipCode = valuemap.get("AIIAccidentLocationZipCode").trim();
                    }
                    if (valuemap.get("AIIRoleInAccident") == null) {
                        AIIRoleInAccident = "";
                    } else {
                        AIIRoleInAccident = valuemap.get("AIIRoleInAccident").trim();
                    }
                    if (valuemap.get("AIITypeOfAutoIOnsurancePolicy") == null) {
                        AIITypeOfAutoIOnsurancePolicy = "";
                    } else {
                        AIITypeOfAutoIOnsurancePolicy = valuemap.get("AIITypeOfAutoIOnsurancePolicy").trim();
                    }
                    if (valuemap.get("AIIPrefixforReponsibleParty") == null) {
                        AIIPrefixforReponsibleParty = "";
                    } else {
                        AIIPrefixforReponsibleParty = valuemap.get("AIIPrefixforReponsibleParty").trim();
                    }
                    if (valuemap.get("AIIFirstNameforReponsibleParty") == null) {
                        AIIFirstNameforReponsibleParty = "";
                    } else {
                        AIIFirstNameforReponsibleParty = valuemap.get("AIIFirstNameforReponsibleParty").trim();
                    }
                    if (valuemap.get("AIIMiddleNameforReponsibleParty") == null) {
                        AIIMiddleNameforReponsibleParty = "";
                    } else {
                        AIIMiddleNameforReponsibleParty = valuemap.get("AIIMiddleNameforReponsibleParty").trim();
                    }
                    if (valuemap.get("AIILastNameforReponsibleParty") == null) {
                        AIILastNameforReponsibleParty = "";
                    } else {
                        AIILastNameforReponsibleParty = valuemap.get("AIILastNameforReponsibleParty").trim();
                    }
                    if (valuemap.get("AIISuffixforReponsibleParty") == null) {
                        AIISuffixforReponsibleParty = "";
                    } else {
                        AIISuffixforReponsibleParty = valuemap.get("AIISuffixforReponsibleParty").trim();
                    }
                    if (valuemap.get("AIICarrierResponsibleParty") == null) {
                        AIICarrierResponsibleParty = "";
                    } else {
                        AIICarrierResponsibleParty = valuemap.get("AIICarrierResponsibleParty").trim();
                    }
                    if (valuemap.get("AIICarrierResponsiblePartyAddress") == null) {
                        AIICarrierResponsiblePartyAddress = "";
                    } else {
                        AIICarrierResponsiblePartyAddress = valuemap.get("AIICarrierResponsiblePartyAddress").trim();
                    }
                    if (valuemap.get("AIICarrierResponsiblePartyAddress2") == null) {
                        AIICarrierResponsiblePartyAddress2 = "";
                    } else {
                        AIICarrierResponsiblePartyAddress2 = valuemap.get("AIICarrierResponsiblePartyAddress2").trim();
                    }
                    if (valuemap.get("AIICarrierResponsiblePartyCity") == null) {
                        AIICarrierResponsiblePartyCity = "";
                    } else {
                        AIICarrierResponsiblePartyCity = valuemap.get("AIICarrierResponsiblePartyCity").trim();
                    }
                    if (valuemap.get("AIICarrierResponsiblePartyState") == null) {
                        AIICarrierResponsiblePartyState = "";
                    } else {
                        AIICarrierResponsiblePartyState = valuemap.get("AIICarrierResponsiblePartyState").trim();
                    }
                    if (valuemap.get("AIICarrierResponsiblePartyZipCode") == null) {
                        AIICarrierResponsiblePartyZipCode = "";
                    } else {
                        AIICarrierResponsiblePartyZipCode = valuemap.get("AIICarrierResponsiblePartyZipCode").trim();
                    }
                    if (valuemap.get("AIICarrierResponsiblePartyAreaCode") == null) {
                        AIICarrierResponsiblePartyAreaCode = "";
                    } else {
                        AIICarrierResponsiblePartyAreaCode = valuemap.get("AIICarrierResponsiblePartyAreaCode").trim();
                    }
                    if (valuemap.get("AIICarrierResponsiblePartyPhoneNumber") == null) {
                        AIICarrierResponsiblePartyPhoneNumber = "";
                    } else {
                        AIICarrierResponsiblePartyPhoneNumber = valuemap.get("AIICarrierResponsiblePartyPhoneNumber").trim();
                    }
                    if (valuemap.get("AIICarrierResponsiblePartyPolicyNumber") == null) {
                        AIICarrierResponsiblePartyPolicyNumber = "";
                    } else {
                        AIICarrierResponsiblePartyPolicyNumber = valuemap.get("AIICarrierResponsiblePartyPolicyNumber").trim();
                    }
                    if (valuemap.get("AIIResponsiblePartyAutoMakeModel") == null) {
                        AIIResponsiblePartyAutoMakeModel = "";
                    } else {
                        AIIResponsiblePartyAutoMakeModel = valuemap.get("AIIResponsiblePartyAutoMakeModel").trim();
                    }
                    if (valuemap.get("AIIResponsiblePartyLicensePlate") == null) {
                        AIIResponsiblePartyLicensePlate = "";
                    } else {
                        AIIResponsiblePartyLicensePlate = valuemap.get("AIIResponsiblePartyLicensePlate").trim();
                    }
                    if (valuemap.get("AIIFirstNameOfYourPolicyHolder") == null) {
                        AIIFirstNameOfYourPolicyHolder = "";
                    } else {
                        AIIFirstNameOfYourPolicyHolder = valuemap.get("AIIFirstNameOfYourPolicyHolder").trim();
                    }
                    if (valuemap.get("AIILastNameOfYourPolicyHolder") == null) {
                        AIILastNameOfYourPolicyHolder = "";
                    } else {
                        AIILastNameOfYourPolicyHolder = valuemap.get("AIILastNameOfYourPolicyHolder").trim();
                    }
                    if (valuemap.get("AIINameAutoInsuranceOfYourVehicle") == null) {
                        AIINameAutoInsuranceOfYourVehicle = "";
                    } else {
                        AIINameAutoInsuranceOfYourVehicle = valuemap.get("AIINameAutoInsuranceOfYourVehicle").trim();
                    }
                    if (valuemap.get("AIIYourInsuranceAddress") == null) {
                        AIIYourInsuranceAddress = "";
                    } else {
                        AIIYourInsuranceAddress = valuemap.get("AIIYourInsuranceAddress").trim();
                    }
                    if (valuemap.get("AIIYourInsuranceAddress2") == null) {
                        AIIYourInsuranceAddress2 = "";
                    } else {
                        AIIYourInsuranceAddress2 = valuemap.get("AIIYourInsuranceAddress2").trim();
                    }
                    if (valuemap.get("AIIYourInsuranceCity") == null) {
                        AIIYourInsuranceCity = "";
                    } else {
                        AIIYourInsuranceCity = valuemap.get("AIIYourInsuranceCity").trim();
                    }
                    if (valuemap.get("AIIYourInsuranceState") == null) {
                        AIIYourInsuranceState = "";
                    } else {
                        AIIYourInsuranceState = valuemap.get("AIIYourInsuranceState").trim();
                    }
                    if (valuemap.get("AIIYourInsuranceZipCode") == null) {
                        AIIYourInsuranceZipCode = "";
                    } else {
                        AIIYourInsuranceZipCode = valuemap.get("AIIYourInsuranceZipCode").trim();
                    }
                    if (valuemap.get("AIIYourInsuranceAreaCode") == null) {
                        AIIYourInsuranceAreaCode = "";
                    } else {
                        AIIYourInsuranceAreaCode = valuemap.get("AIIYourInsuranceAreaCode").trim();
                    }
                    if (valuemap.get("AIIYourInsurancePhoneNumber") == null) {
                        AIIYourInsurancePhoneNumber = "";
                    } else {
                        AIIYourInsurancePhoneNumber = valuemap.get("AIIYourInsurancePhoneNumber").trim();
                    }
                    if (valuemap.get("AIIYourInsurancePolicyNo") == null) {
                        AIIYourInsurancePolicyNo = "";
                    } else {
                        AIIYourInsurancePolicyNo = valuemap.get("AIIYourInsurancePolicyNo").trim();
                    }
                    if (valuemap.get("AIIYourLicensePlate") == null) {
                        AIIYourLicensePlate = "";
                    } else {
                        AIIYourLicensePlate = valuemap.get("AIIYourLicensePlate").trim();
                    }
                    if (valuemap.get("AIIYourCarMakeModelYear") == null) {
                        AIIYourCarMakeModelYear = "";
                    } else {
                        AIIYourCarMakeModelYear = valuemap.get("AIIYourCarMakeModelYear").trim();
                    }
                } else {
                    if (valuemap.get("AIIResponsiblePartyLicensePlate") == null) {
                        AIIResponsiblePartyLicensePlate = "";
                    } else {
                        AIIResponsiblePartyLicensePlate = valuemap.get("AIIResponsiblePartyLicensePlate").trim();
                    }
                    if (valuemap.get("AIIFirstNameOfYourPolicyHolder") == null) {
                        AIIFirstNameOfYourPolicyHolder = "";
                    } else {
                        AIIFirstNameOfYourPolicyHolder = valuemap.get("AIIFirstNameOfYourPolicyHolder").trim();
                    }
                    if (valuemap.get("AIILastNameOfYourPolicyHolder") == null) {
                        AIILastNameOfYourPolicyHolder = "";
                    } else {
                        AIILastNameOfYourPolicyHolder = valuemap.get("AIILastNameOfYourPolicyHolder").trim();
                    }
                    if (valuemap.get("AIINameAutoInsuranceOfYourVehicle") == null) {
                        AIINameAutoInsuranceOfYourVehicle = "";
                    } else {
                        AIINameAutoInsuranceOfYourVehicle = valuemap.get("AIINameAutoInsuranceOfYourVehicle").trim();
                    }
                    if (valuemap.get("AIIYourInsuranceAddress") == null) {
                        AIIYourInsuranceAddress = "";
                    } else {
                        AIIYourInsuranceAddress = valuemap.get("AIIYourInsuranceAddress").trim();
                    }
                    if (valuemap.get("AIIYourInsuranceAddress2") == null) {
                        AIIYourInsuranceAddress2 = "";
                    } else {
                        AIIYourInsuranceAddress2 = valuemap.get("AIIYourInsuranceAddress2").trim();
                    }
                    if (valuemap.get("AIIYourInsuranceCity") == null) {
                        AIIYourInsuranceCity = "";
                    } else {
                        AIIYourInsuranceCity = valuemap.get("AIIYourInsuranceCity").trim();
                    }
                    if (valuemap.get("AIIYourInsuranceState") == null) {
                        AIIYourInsuranceState = "";
                    } else {
                        AIIYourInsuranceState = valuemap.get("AIIYourInsuranceState").trim();
                    }
                    if (valuemap.get("AIIYourInsuranceZipCode") == null) {
                        AIIYourInsuranceZipCode = "";
                    } else {
                        AIIYourInsuranceZipCode = valuemap.get("AIIYourInsuranceZipCode").trim();
                    }
                    if (valuemap.get("AIIYourInsuranceAreaCode") == null) {
                        AIIYourInsuranceAreaCode = "";
                    } else {
                        AIIYourInsuranceAreaCode = valuemap.get("AIIYourInsuranceAreaCode").trim();
                    }
                    if (valuemap.get("AIIYourInsurancePhoneNumber") == null) {
                        AIIYourInsurancePhoneNumber = "";
                    } else {
                        AIIYourInsurancePhoneNumber = valuemap.get("AIIYourInsurancePhoneNumber").trim();
                    }
                    if (valuemap.get("AIIYourInsurancePolicyNo") == null) {
                        AIIYourInsurancePolicyNo = "";
                    } else {
                        AIIYourInsurancePolicyNo = valuemap.get("AIIYourInsurancePolicyNo").trim();
                    }
                    if (valuemap.get("AIIYourLicensePlate") == null) {
                        AIIYourLicensePlate = "";
                    } else {
                        AIIYourLicensePlate = valuemap.get("AIIYourLicensePlate").trim();
                    }
                    if (valuemap.get("AIIYourCarMakeModelYear") == null) {
                        AIIYourCarMakeModelYear = "";
                    } else {
                        AIIYourCarMakeModelYear = valuemap.get("AIIYourCarMakeModelYear").trim();
                    }
                }
            }
            if (valuemap.get("HealthInsuranceChk") == null) {
                HealthInsuranceChk = "0";
            } else {
                HealthInsuranceChk = valuemap.get("HealthInsuranceChk").trim();
            }
            if (HealthInsuranceChk.equals("1")) {
                if (valuemap.get("GovtFundedInsurancePlanChk") == null) {
                    GovtFundedInsurancePlanChk = "0";
                } else {
                    GovtFundedInsurancePlanChk = valuemap.get("GovtFundedInsurancePlanChk").trim();
                }
                if (GovtFundedInsurancePlanChk.equals("1")) {
                    if (valuemap.get("GFIPMedicare") == null) {
                        GFIPMedicare = "0";
                    } else {
                        GFIPMedicare = "1";
                    }
                    if (valuemap.get("GFIPMedicaid") == null) {
                        GFIPMedicaid = "0";
                    } else {
                        GFIPMedicaid = "1";
                    }
                    if (valuemap.get("GFIPCHIP") == null) {
                        GFIPCHIP = "0";
                    } else {
                        GFIPCHIP = "1";
                    }
                    if (valuemap.get("GFIPTricare") == null) {
                        GFIPTricare = "0";
                    } else {
                        GFIPTricare = "1";
                    }
                    if (valuemap.get("GFIPVHA") == null) {
                        GFIPVHA = "0";
                    } else {
                        GFIPVHA = "1";
                    }
                    if (valuemap.get("GFIPIndianHealth") == null) {
                        GFIPIndianHealth = "0";
                    } else {
                        GFIPIndianHealth = "1";
                    }
                }
                if (valuemap.get("InsuranceSubPatient") == null) {
                    InsuranceSubPatient = "0";
                } else {
                    InsuranceSubPatient = "1";
                }
                if (valuemap.get("InsuranceSubGuarantor") == null) {
                    InsuranceSubGuarantor = "0";
                } else {
                    InsuranceSubGuarantor = "1";
                }
                if (valuemap.get("InsuranceSubOther") == null) {
                    InsuranceSubOther = "0";
                } else {
                    InsuranceSubOther = "1";
                }
                if (valuemap.get("HIPrimaryInsurance") == null) {
                    HIPrimaryInsurance = "";
                } else {
                    HIPrimaryInsurance = valuemap.get("HIPrimaryInsurance").trim();
                }
                if (valuemap.get("HISubscriberFirstName") == null) {
                    HISubscriberFirstName = "";
                } else {
                    HISubscriberFirstName = valuemap.get("HISubscriberFirstName").trim();
                }
                if (valuemap.get("HISubscriberLastName") == null) {
                    HISubscriberLastName = "";
                } else {
                    HISubscriberLastName = valuemap.get("HISubscriberLastName").trim();
                }
                if (valuemap.get("HISubscriberDOB") == null) {
                    HISubscriberDOB = "0000-00-00";
                } else {
                    if (valuemap.get("HISubscriberDOB").length() > 0) {
                        HISubscriberDOB = valuemap.get("HISubscriberDOB").trim();
                        //HISubscriberDOB = String.valueOf(String.valueOf(HISubscriberDOB.substring(6, 10))) + "-" + HISubscriberDOB.substring(0, 2) + "-" + HISubscriberDOB.substring(3, 5);
                    } else {
                        HISubscriberDOB = "0000-00-00";
                    }
                }
                if (valuemap.get("HISubscriberSSN") == null) {
                    HISubscriberSSN = "";
                } else {
                    HISubscriberSSN = valuemap.get("HISubscriberSSN").trim();
                }
                if (valuemap.get("HISubscriberRelationtoPatient") == null) {
                    HISubscriberRelationtoPatient = "";
                } else {
                    HISubscriberRelationtoPatient = valuemap.get("HISubscriberRelationtoPatient").trim();
                }
                if (valuemap.get("HISubscriberGroupNo") == null) {
                    HISubscriberGroupNo = "";
                } else {
                    HISubscriberGroupNo = valuemap.get("HISubscriberGroupNo").trim();
                }
                if (valuemap.get("HISubscriberPolicyNo") == null) {
                    HISubscriberPolicyNo = "";
                } else {
                    HISubscriberPolicyNo = valuemap.get("HISubscriberPolicyNo").trim();
                }
                if (valuemap.get("SecondHealthInsuranceChk") == null) {
                    SecondHealthInsuranceChk = "0";
                } else {
                    SecondHealthInsuranceChk = valuemap.get("SecondHealthInsuranceChk").trim();
                }
                if (SecondHealthInsuranceChk.equals("1")) {
                    if (valuemap.get("SHISecondaryName") == null) {
                        SHISecondaryName = "";
                    } else {
                        SHISecondaryName = valuemap.get("SHISecondaryName").trim();
                    }
                    if (valuemap.get("SHISubscriberFirstName") == null) {
                        SHISubscriberFirstName = "";
                    } else {
                        SHISubscriberFirstName = valuemap.get("SHISubscriberFirstName").trim();
                    }
                    if (valuemap.get("SHISubscriberLastName") == null) {
                        SHISubscriberLastName = "";
                    } else {
                        SHISubscriberLastName = valuemap.get("SHISubscriberLastName").trim();
                    }
                    if (valuemap.get("SHISubscriberDOB") == null) {
                        SHISubscriberDOB = "0000-00-00";
                    } else {
                        if (valuemap.get("SHISubscriberDOB").length() > 0) {
                            SHISubscriberDOB = valuemap.get("SHISubscriberDOB").trim();
                            //SHISubscriberDOB = String.valueOf(String.valueOf(SHISubscriberDOB.substring(6, 10))) + "-" + SHISubscriberDOB.substring(0, 2) + "-" + SHISubscriberDOB.substring(3, 5);
                        } else {
                            SHISubscriberDOB = "0000-00-00";
                        }
                    }
                    if (valuemap.get("SHISubscriberRelationtoPatient") == null) {
                        SHISubscriberRelationtoPatient = "";
                    } else {
                        SHISubscriberRelationtoPatient = valuemap.get("SHISubscriberRelationtoPatient").trim();
                    }
                    if (valuemap.get("SHISubscriberGroupNo") == null) {
                        SHISubscriberGroupNo = "";
                    } else {
                        SHISubscriberGroupNo = valuemap.get("SHISubscriberGroupNo").trim();
                    }
                    if (valuemap.get("SHISubscriberPolicyNo") == null) {
                        SHISubscriberPolicyNo = "";
                    } else {
                        SHISubscriberPolicyNo = valuemap.get("SHISubscriberPolicyNo").trim();
                    }
                }
            }
            if (valuemap.get("MFFirstVisit") == null) {
                MFFirstVisit = "0";
            } else {
                MFFirstVisit = valuemap.get("MFFirstVisit");
            }
            if (valuemap.get("MFReturnPat") == null) {
                MFReturnPat = "0";
            } else {
                MFReturnPat = valuemap.get("MFReturnPat");
            }
            if (valuemap.get("MFInternetFind") == null) {
                MFInternetFind = "0";
            } else {
                MFInternetFind = valuemap.get("MFInternetFind");
            }
            if (valuemap.get("Facebook") == null) {
                Facebook = "0";
            } else {
                Facebook = "1";
            }
            if (valuemap.get("MapSearch") == null) {
                MapSearch = "0";
            } else {
                MapSearch = "1";
            }
            if (valuemap.get("GoogleSearch") == null) {
                GoogleSearch = "0";
            } else {
                GoogleSearch = "1";
            }
            if (valuemap.get("VERWebsite") == null) {
                VERWebsite = "0";
            } else {
                VERWebsite = "1";
            }
            if (valuemap.get("OnlineAdvertisements") == null) {
                OnlineAdvertisements = "0";
            } else {
                OnlineAdvertisements = "1";
            }
            if (valuemap.get("WebsiteAds") == null) {
                WebsiteAds = "0";
            } else {
                WebsiteAds = "1";
            }
            if (valuemap.get("OnlineReviews") == null) {
                OnlineReviews = "0";
            } else {
                OnlineReviews = "1";
            }
            if (valuemap.get("Twitter") == null) {
                Twitter = "0";
            } else {
                Twitter = "1";
            }
            if (valuemap.get("LinkedIn") == null) {
                LinkedIn = "0";
            } else {
                LinkedIn = "1";
            }
            if (valuemap.get("EmailBlast") == null) {
                EmailBlast = "0";
            } else {
                EmailBlast = "1";
            }
            if (valuemap.get("YouTube") == null) {
                YouTube = "0";
            } else {
                YouTube = "1";
            }
            if (valuemap.get("TV") == null) {
                TV = "0";
            } else {
                TV = "1";
            }
            if (valuemap.get("Billboard") == null) {
                Billboard = "0";
            } else {
                Billboard = "1";
            }
            if (valuemap.get("Radio") == null) {
                Radio = "0";
            } else {
                Radio = "1";
            }
            if (valuemap.get("Brochure") == null) {
                Brochure = "0";
            } else {
                Brochure = "1";
            }
            if (valuemap.get("DirectMail") == null) {
                DirectMail = "0";
            } else {
                DirectMail = "1";
            }
            if (valuemap.get("CitizensDeTar") == null) {
                CitizensDeTar = "0";
            } else {
                CitizensDeTar = "1";
            }
            if (valuemap.get("LiveWorkNearby") == null) {
                LiveWorkNearby = "0";
            } else {
                LiveWorkNearby = "1";
            }
            if (valuemap.get("FamilyFriend") == null) {
                FamilyFriend = "0";
            } else {
                FamilyFriend = "1";
            }
            if (valuemap.get("UrgentCare") == null) {
                UrgentCare = "0";
            } else {
                UrgentCare = "1";
            }
            if (valuemap.get("NewspaperMagazine") == null) {
                NewspaperMagazine = "0";
            } else {
                NewspaperMagazine = "1";
            }
            if (valuemap.get("School") == null) {
                School = "0";
            } else {
                School = "1";
            }
            if (valuemap.get("Hotel") == null) {
                Hotel = "0";
            } else {
                Hotel = "1";
            }
            if (valuemap.get("EmployerSentMe") == null) {
                EmployerSentMe = "0";
            } else {
                EmployerSentMe = "1";
            }
            if (valuemap.get("FamilyFriend_text") == null) {
                FamilyFriend_text = "";
            } else {
                FamilyFriend_text = valuemap.get("FamilyFriend_text");
            }
            if (valuemap.get("UrgentCare_text") == null) {
                UrgentCare_text = "";
            } else {
                UrgentCare_text = valuemap.get("UrgentCare_text");
            }
            if (valuemap.get("NewspaperMagazine_text") == null) {
                NewspaperMagazine_text = "";
            } else {
                NewspaperMagazine_text = valuemap.get("NewspaperMagazine_text");
            }
            if (valuemap.get("School_text") == null) {
                School_text = "";
            } else {
                School_text = valuemap.get("School_text");
            }
            if (valuemap.get("Hotel_text") == null) {
                Hotel_text = "";
            } else {
                Hotel_text = valuemap.get("Hotel_text");
            }
            if (valuemap.get("EmployerSentMe_text") == null) {
                EmployerSentMe_text = "";
            } else {
                EmployerSentMe_text = valuemap.get("EmployerSentMe_text");
            }
            if (valuemap.get("MFPhysicianRefChk") == null) {
                MFPhysicianRefChk = "0";
            } else {
                MFPhysicianRefChk = valuemap.get("MFPhysicianRefChk");
            }
            if (valuemap.get("MFPhysician") == null) {
                MFPhysician = "";
            } else {
                MFPhysician = valuemap.get("MFPhysician");
            }
            if (valuemap.get("PatientCell") == null) {
                PatientCell = "";
            } else {
                PatientCell = valuemap.get("PatientCell").trim();
            }
            if (valuemap.get("RecInitial") == null) {
                RecInitial = "";
            } else {
                RecInitial = valuemap.get("RecInitial").trim();
            }
            ps = conn.prepareStatement("Select Date_format(now(),'%Y-%m-%d')");
            rset = ps.executeQuery();
            while (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            ps.close();
            try {

                ps = conn.prepareStatement("Select MRN from " + Database + ".PatientReg order by ID desc limit 1 ");
                rset = ps.executeQuery();
                if (rset.next()) {
                    MRN = rset.getInt(1);
                }
                rset.close();
                ps.close();
                if (String.valueOf(MRN).length() == 0) {
                    MRN = 310001;
                } else if (String.valueOf(MRN).length() == 4) {
                    MRN = 310001;
                } else if (String.valueOf(MRN).length() == 8) {
                    MRN = 310001;
                } else if (MRN == 0) {
                    MRN = 310001;
                } else if (String.valueOf(MRN).length() == 6) {
                    ++MRN;
                }
                if (ClientIndex == 8) {
                    ExtendedMRN = "1008" + MRN;
                } else if (ClientIndex == 9 || ClientIndex == 38) {
                    ExtendedMRN = "1009" + MRN;
                } else if (ClientIndex == 10) {
                    ExtendedMRN = "1010" + MRN;
                } else if (ClientIndex == 11) {
                    ExtendedMRN = "1011" + MRN;
                }
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria PatineReg2 Data get ^^" + facilityName + " ##MES#001)", servletContext, ex, "PatientReg2", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2", "MES#001", request, ex, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                Parser.SetField("Message", "MES#001");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }
            try {
                if (!Email.equals(ConfirmEmail)) {
                    out.println("<!DOCTYPE html><html><body><p style=\"color:black;\">Please Put Email and Confirm Email Correctly and then Submit</p>");
                    out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
                    return;
                }
                if (Age.equals("-1")) {
                    out.println("<!DOCTYPE html><html><body><p style=\"color:black;\">Please Select Date of Birth Correctly with Day, Month, Year and then Submit (AGE Cannot be -1)</p>");
                    out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
                    return;
                }
                Email = ConfirmEmail;
                UtilityHelper utilityHelper = new UtilityHelper();
                String ClientIp = utilityHelper.getClientIp(request);
                PreparedStatement MainReceipt = conn.prepareStatement(
                        " INSERT INTO " + Database + ".PatientReg (ClientIndex,FirstName,LastName , MiddleInitial,DOB,Age,Gender ,Email," +
                                "PhNumber ,Address,City ,State,Country,ZipCode,SSN,Occupation ,Employer ,EmpContact, PriCarePhy," +
                                "ReasonVisit,SelfPayChk,CreatedDate,Title, MaritalStatus,CreatedBy, MRN, Status, ExtendedMRN, County," +
                                "sync,RegisterFrom,ViewDate, EnterIP)" +
                                " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,0,?,?,0,?,NOW(),?) ");
                MainReceipt.setInt(1, ClientIndex);
                MainReceipt.setString(2, FirstName.toUpperCase());
                MainReceipt.setString(3, LastName.toUpperCase());
                MainReceipt.setString(4, MiddleInitial.toUpperCase());
                MainReceipt.setString(5, DOB);
                MainReceipt.setString(6, Age);
                MainReceipt.setString(7, gender.toUpperCase());
                MainReceipt.setString(8, Email);
                MainReceipt.setString(9, String.valueOf(String.valueOf(AreaCode)) + PhNumber);
                MainReceipt.setString(10, String.valueOf(String.valueOf(Address)) + " " + Address2);
                MainReceipt.setString(11, City.toUpperCase());
                MainReceipt.setString(12, State.toUpperCase());
                MainReceipt.setString(13, Country.toUpperCase());
                MainReceipt.setString(14, ZipCode);
                MainReceipt.setString(15, SSN);
                MainReceipt.setString(16, "");
                MainReceipt.setString(17, "");
                MainReceipt.setString(18, "");
                MainReceipt.setString(19, "");
                MainReceipt.setString(20, ReasonVisit);
                MainReceipt.setInt(21, 0);
                MainReceipt.setString(22, Title.toUpperCase());
                MainReceipt.setString(23, MaritalStatus.toUpperCase());
                MainReceipt.setString(24, "Out Patient");
                MainReceipt.setInt(25, MRN);
                MainReceipt.setString(26, ExtendedMRN);
                MainReceipt.setString(27, County.toUpperCase());
                MainReceipt.setString(28, "**Patient Reg2");
                MainReceipt.setString(29, ClientIp);
                MainReceipt.executeUpdate();
                MainReceipt.close();


            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Insertion PatientReg Table ^^" + facilityName + " ##MES#002****)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2", "SaveDataVictoriaError 2- Insertion PatientReg Table :", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                Parser.SetField("Message", "MES#002");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }
            try {
                ps = conn.prepareStatement("Select max(ID) from " + Database + ".PatientReg ");
                rset = ps.executeQuery();
                if (rset.next()) {
                    PatientRegId = rset.getInt(1);
                }
                rset.close();
                ps.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria ^^" + facilityName + " ##MES#003)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2", "SaveDataVictoriaError 3- :", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                Parser.SetField("Message", "MES#003");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }


            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".PatientVisit(MRN,PatientRegId,ReasonVisit,VisitNumber,DoctorId,DateofService," +
                                "CreatedDate,CreatedBy) VALUES (?,?,?,1,NULL,now(),now(),?) ");
                MainReceipt.setInt(1, MRN);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, ReasonVisit);
                MainReceipt.setString(4, "Out Patient");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Insertion PatientVisit Table ^^" + facilityName + " ##MES#003)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2", "SaveDataVictoriaError 3.1 Insertion in table PatientVisit- :", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                Parser.SetField("Message", "MES#004");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                //return;
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
                if (!IDs.equals("")) {
                    ps = conn.prepareStatement("INSERT INTO " + Database + ".PatientDocUpload (UploadDocumentName,FileName,CreatedBy,CreatedDate,Status,VisitIdx,PatientMRN,PatientRegId,PremisisId) VALUES (?,?,'PatientRegForm',NOW(),0,?,?,?,?)");
                    ps.setString(1, "ID Front");
                    ps.setString(2, IDs);
                    ps.setInt(3, VisitId);
                    ps.setInt(4, MRN);
                    ps.setInt(5, PatientRegId);
                    ps.setInt(6, ClientIndex);
                    ps.executeUpdate();
                    ps.close();
                }


                if (!InsuranceIDsF.equals("")) {
                    ps = conn.prepareStatement("INSERT INTO " + Database + ".PatientDocUpload (UploadDocumentName,FileName,CreatedBy,CreatedDate,Status,VisitIdx,PatientMRN,PatientRegId,PremisisId) VALUES (?,?,'PatientRegForm',NOW(),0,?,?,?,?)");
                    ps.setString(1, "Insurance ID Front");
                    ps.setString(2, InsuranceIDsF);
                    ps.setInt(3, VisitId);
                    ps.setInt(4, MRN);
                    ps.setInt(5, PatientRegId);
                    ps.setInt(6, ClientIndex);
                    ps.executeUpdate();
                    ps.close();
                }
                if (!InsuranceIDsB.equals("")) {
                    ps = conn.prepareStatement("INSERT INTO " + Database + ".PatientDocUpload (UploadDocumentName,FileName,CreatedBy,CreatedDate,Status,VisitIdx,PatientMRN,PatientRegId,PremisisId) VALUES (?,?,'PatientRegForm',NOW(),0,?,?,?,?)");
                    ps.setString(1, "Insurance ID Back");
                    ps.setString(2, InsuranceIDsB);
                    ps.setInt(3, VisitId);
                    ps.setInt(4, MRN);
                    ps.setInt(5, PatientRegId);
                    ps.setInt(6, ClientIndex);
                    ps.executeUpdate();
                    ps.close();
                }
            } catch (SQLException e) {
                System.out.println("PatientDocUpload Error ");
                System.out.println(e.getMessage());
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".PatientReg_Details (PatientRegId,Ethnicity,Race," +
                        " EmployementChk,Employer,Occupation,EmpContact,PrimaryCarePhysicianChk,PriCarePhy,ReasonVisit,PriCarePhyAddress,PriCarePhyCity,PriCarePhyState," +
                        " PriCarePhyZipCode,PatientMinorChk,GuarantorChk,GuarantorEmployer,GuarantorEmployerPhNumber,GuarantorEmployerAddress,GuarantorEmployerCity," +
                        " GuarantorEmployerState,GuarantorEmployerZipCode,CreatedDate,WorkersCompPolicyChk,MotorVehicleAccidentChk,HealthInsuranceChk, SympChkCOVID, " +
                        " DateSympOnset, SympFever, SympCough, SympShortBreath, SympFatigue, SympMuscBodyAches, SympHeadache, SympLossTaste, SympSoreThroat, " +
                        " SympCongestionRunNos, SympNauseaVomit, SympDiarrhea, SympPerPainChest, SympNewConfusion, SympInabWake, SympOthers, SympOthersTxt," +
                        " EmpHealthChk, PregChk,TestForTravel) \n" +
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
                MainReceipt.setString(11, String.valueOf(String.valueOf(PriCarePhyAddress)) + " " + PriCarePhyAddress2);
                MainReceipt.setString(12, PriCarePhyCity);
                MainReceipt.setString(13, PriCarePhyState);
                MainReceipt.setString(14, PriCarePhyZipCode);
                MainReceipt.setInt(15, Integer.parseInt(PatientMinorChk));
                MainReceipt.setString(16, GuarantorChk);
                MainReceipt.setString(17, GuarantorEmployer);
                MainReceipt.setString(18, String.valueOf(String.valueOf(GuarantorEmployerAreaCode)) + GuarantorEmployerPhNumber);
                MainReceipt.setString(19, String.valueOf(String.valueOf(GuarantorEmployerAddress)) + " " + GuarantorEmployerAddress2);
                MainReceipt.setString(20, GuarantorEmployerCity);
                MainReceipt.setString(21, GuarantorEmployerState);
                MainReceipt.setString(22, GuarantorEmployerZipCode);
                MainReceipt.setInt(23, Integer.parseInt(WorkersCompPolicyChk));
                MainReceipt.setInt(24, Integer.parseInt(MotorVehicleAccidentChk));
                MainReceipt.setInt(25, Integer.parseInt(HealthInsuranceChk));
                MainReceipt.setInt(26, Integer.parseInt(SympChkCOVID));
                //MainReceipt.setString(27, DateSympOnset);
                if (!DateSympOnset.equals(""))
                    MainReceipt.setString(27, DateSympOnset);
                else
                    MainReceipt.setNull(27, Types.DATE);
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
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Insertion PatientReg_Details Table ^^" + facilityName + " ##MES#004)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                Parser.SetField("Message", "MES#005");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                Services.DumException("PatientReg2", "SaveDataVictoriaError 4- Insertion PatientReg_Details Table :", request, e, this.getServletContext());
                //return;
            }
            if (WorkersCompPolicyChk.equals("1")) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement(
                            " INSERT INTO " + Database + ".Patient_WorkCompPolicy (PatientRegId,WCPDateofInjury,WCPCaseNo," +
                                    "WCPGroupNo,WCPMemberId,WCPInjuryRelatedAutoMotorAccident,WCPInjuryRelatedWorkRelated," +
                                    "WCPInjuryRelatedOtherAccident,WCPInjuryRelatedNoAccident,WCPInjuryOccurVehicle," +
                                    "WCPInjuryOccurWork,WCPInjuryOccurHome,WCPInjuryOccurOther,WCPInjuryDescription," +
                                    "WCPHRFirstName,WCPHRLastName,WCPHRPhoneNumber,WCPHRAddress,WCPHRCity,WCPHRState," +
                                    "WCPHRZipCode,WCPPlanName,WCPCarrierName,WCPPayerPhoneNumber,WCPCarrierAddress," +
                                    "WCPCarrierCity,WCPCarrierState,WCPCarrierZipCode,WCPAdjudicatorFirstName," +
                                    "WCPAdjudicatorLastName,WCPAdjudicatorPhoneNumber,WCPAdjudicatorFaxPhoneNumber,CreatedDate)" +
                                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
                    MainReceipt.setInt(1, PatientRegId);
                    //MainReceipt.setString(2, WCPDateofInjury);
                    if (!WCPDateofInjury.equals(""))
                        MainReceipt.setString(2, WCPDateofInjury);
                    else
                        MainReceipt.setNull(2, Types.DATE);
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
                    MainReceipt.setString(17, String.valueOf(String.valueOf(WCPHRAreaCode)) + WCPHRPhoneNumber);
                    MainReceipt.setString(18, String.valueOf(String.valueOf(WCPHRAddress)) + " " + WCPHRAddress2);
                    MainReceipt.setString(19, WCPHRCity);
                    MainReceipt.setString(20, WCPHRState);
                    MainReceipt.setString(21, WCPHRZipCode);
                    MainReceipt.setString(22, WCPPlanName);
                    MainReceipt.setString(23, WCPCarrierName);
                    MainReceipt.setString(24, String.valueOf(String.valueOf(WCPPayerAreaCode)) + WCPPayerPhoneNumber);
                    MainReceipt.setString(25, String.valueOf(String.valueOf(WCPCarrierAddress)) + " " + WCPCarrierAddress2);
                    MainReceipt.setString(26, WCPCarrierCity);
                    MainReceipt.setString(27, WCPCarrierState);
                    MainReceipt.setString(28, WCPCarrierZipCode);
                    MainReceipt.setString(29, WCPAdjudicatorFirstName);
                    MainReceipt.setString(30, WCPAdjudicatorLastName);
                    MainReceipt.setString(31, String.valueOf(String.valueOf(WCPAdjudicatorAreaCode)) + WCPAdjudicatorPhoneNumber);
                    MainReceipt.setString(32, String.valueOf(String.valueOf(WCPAdjudicatorFaxAreaCode)) + WCPAdjudicatorFaxPhoneNumber);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception e) {
                    helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Insertion Patient_WorkCompPolicy Table ^^" + facilityName + " ##MES#005)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                    Services.DumException("PatientReg2", "SaveDataVictoriaError 5- Insertion Patient_WorkCompPolicy Table :", request, e, this.getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg2");
                    Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                    Parser.SetField("Message", "MES#006");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    //return;
                }
            }
            if (MotorVehicleAccidentChk.equals("1")) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + Database + ".Patient_AutoInsuranceInfo (PatientRegId,AutoInsuranceInformationChk," +
                                    "AIIDateofAccident,AIIAutoClaim,AIIAccidentLocationAddress,AIIAccidentLocationCity," +
                                    "AIIAccidentLocationState,AIIAccidentLocationZipCode,AIIRoleInAccident," +
                                    "AIITypeOfAutoIOnsurancePolicy,AIIPrefixforReponsibleParty,AIIFirstNameforReponsibleParty," +
                                    "AIIMiddleNameforReponsibleParty,AIILastNameforReponsibleParty,AIISuffixforReponsibleParty," +
                                    "AIICarrierResponsibleParty,AIICarrierResponsiblePartyAddress,AIICarrierResponsiblePartyCity," +
                                    "AIICarrierResponsiblePartyState,AIICarrierResponsiblePartyZipCode," +
                                    "AIICarrierResponsiblePartyPhoneNumber,AIICarrierResponsiblePartyPolicyNumber," +
                                    "AIIResponsiblePartyAutoMakeModel,AIIResponsiblePartyLicensePlate," +
                                    "AIIFirstNameOfYourPolicyHolder,AIILastNameOfYourPolicyHolder," +
                                    "AIINameAutoInsuranceOfYourVehicle,AIIYourInsuranceAddress,AIIYourInsuranceCity," +
                                    "AIIYourInsuranceState,AIIYourInsuranceZipCode,AIIYourInsurancePhoneNumber," +
                                    "AIIYourInsurancePolicyNo,AIIYourLicensePlate,AIIYourCarMakeModelYear,CreatedDate) " +
                                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
                    MainReceipt.setInt(1, PatientRegId);
                    MainReceipt.setInt(2, Integer.parseInt(AutoInsuranceInformationChk));
                    //MainReceipt.setString(3, AIIDateofAccident);
                    if (!AIIDateofAccident.equals(""))
                        MainReceipt.setString(3, AIIDateofAccident);
                    else
                        MainReceipt.setNull(3, Types.DATE);
                    MainReceipt.setString(4, AIIAutoClaim);
                    MainReceipt.setString(5, AIIAccidentLocationAddress + " " + AIIAccidentLocationAddress2);
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
                    MainReceipt.setString(17, AIICarrierResponsiblePartyAddress + " " + AIICarrierResponsiblePartyAddress2);
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
                    helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Insertion Patient_AutoInsuranceInfo Table ^^" + facilityName + " ##MES#006)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                    Services.DumException("PatientReg2", "SaveDataVictoriaError 6- Insertion Patient_AutoInsuranceInfo Table :", request, e, this.getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg2");
                    Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                    Parser.SetField("Message", "MES#007");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
//                    return;
                }
            }
            if (HealthInsuranceChk.equals("1")) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".Patient_HealthInsuranceInfo (PatientRegId,GovtFundedInsurancePlanChk,GFIPMedicare,GFIPMedicaid,GFIPCHIP,GFIPTricare,GFIPVHA,GFIPIndianHealth,InsuranceSubPatient,InsuranceSubGuarantor,InsuranceSubOther,HIPrimaryInsurance,HISubscriberFirstName,HISubscriberLastName,HISubscriberDOB,HISubscriberSSN,HISubscriberRelationtoPatient,HISubscriberGroupNo,HISubscriberPolicyNo,SecondHealthInsuranceChk,SHISecondaryName,SHISubscriberFirstName,SHISubscriberLastName,SHISubscriberRelationtoPatient,SHISubscriberGroupNo,SHISubscriberPolicyNo,CreatedDate) \n VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
                    MainReceipt.setInt(1, PatientRegId);
                    MainReceipt.setInt(2, Integer.parseInt(GovtFundedInsurancePlanChk));
                    MainReceipt.setInt(3, Integer.parseInt(GFIPMedicare));
                    MainReceipt.setInt(4, Integer.parseInt(GFIPMedicaid));
                    MainReceipt.setInt(5, Integer.parseInt(GFIPCHIP));
                    MainReceipt.setInt(6, Integer.parseInt(GFIPTricare));
                    MainReceipt.setInt(7, Integer.parseInt(GFIPVHA));
                    MainReceipt.setInt(8, Integer.parseInt(GFIPIndianHealth));
                    MainReceipt.setString(9, InsuranceSubPatient.toUpperCase());
                    MainReceipt.setString(10, InsuranceSubGuarantor.toUpperCase());
                    MainReceipt.setString(11, InsuranceSubOther.toUpperCase());
                    MainReceipt.setString(12, HIPrimaryInsurance.toUpperCase());
                    MainReceipt.setString(13, HISubscriberFirstName.toUpperCase());
                    MainReceipt.setString(14, HISubscriberLastName.toUpperCase());
                    MainReceipt.setString(15, HISubscriberDOB);
                    MainReceipt.setString(16, HISubscriberSSN);
                    MainReceipt.setString(17, HISubscriberRelationtoPatient.toUpperCase());
                    MainReceipt.setString(18, HISubscriberGroupNo);
                    MainReceipt.setString(19, HISubscriberPolicyNo);
                    MainReceipt.setInt(20, Integer.parseInt(SecondHealthInsuranceChk));
                    MainReceipt.setString(21, SHISecondaryName.toUpperCase());
                    MainReceipt.setString(22, SHISubscriberFirstName.toUpperCase());
                    MainReceipt.setString(23, SHISubscriberLastName.toUpperCase());
                    MainReceipt.setString(24, SHISubscriberRelationtoPatient.toUpperCase());
                    MainReceipt.setString(25, SHISubscriberGroupNo);
                    MainReceipt.setString(26, SHISubscriberPolicyNo);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception e) {
                    helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Insertion Patient_HealthInsuranceInfo Table ^^" + facilityName + " ##MES#007)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                    Services.DumException("PatientReg2", "SaveDataVictoriaError 7- Insertion Patient_HealthInsuranceInfo Table", request, e, this.getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "PatientReg2");
                    Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                    Parser.SetField("Message", "MES#008");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
//                    return;
                }
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".MarketingInfo (PatientRegId," +
                        " MFFirstVisit,MFReturnPat,MFInternetFind,Facebook,MapSearch,GoogleSearch,VERWebsite,OnlineAdvertisements,OnlineReviews," +
                        " Twitter,LinkedIn,EmailBlast,YouTube,TV,Billboard,Radio,Brochure,DirectMail,CitizensDeTar,LiveWorkNearby,FamilyFriend," +
                        " FamilyFriend_text,UrgentCare,UrgentCare_text,NewspaperMagazine,NewspaperMagazine_text,School,School_text," +
                        " Hotel,Hotel_text,MFPhysician,CreatedDate,EmployerSentMe,EmployerSentMe_text,MFPhysicianRefChk,PatientCell,RecInitial,WebsiteAds) \n " +
                        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?) ");
                MainReceipt.setInt(1, PatientRegId);
                MainReceipt.setInt(2, Integer.parseInt(MFFirstVisit));
                MainReceipt.setInt(3, Integer.parseInt(MFReturnPat));
                MainReceipt.setInt(4, Integer.parseInt(MFInternetFind));
                MainReceipt.setString(5, Facebook);
                MainReceipt.setString(6, MapSearch);
                MainReceipt.setString(7, GoogleSearch);
                MainReceipt.setString(8, VERWebsite);
                MainReceipt.setString(9, OnlineAdvertisements);
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
                MainReceipt.setString(33, EmployerSentMe);
                MainReceipt.setString(34, EmployerSentMe_text);
                MainReceipt.setString(35, MFPhysicianRefChk);
                MainReceipt.setString(36, PatientCell);
                MainReceipt.setString(37, RecInitial);
                MainReceipt.setString(38, WebsiteAds);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Insertion MarketingInfo Table ^^" + facilityName + " ##MES#008)", servletContext, e, "PatientReg2", "SaveDataVictoria", conn);
                Services.DumException("PatientReg2", "SaveDataVictoriaError 8- Insertion MarketingInfo Table", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientReg2");
                Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
                Parser.SetField("Message", "MES#009");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
//                return;
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
            if (ClientIndex == 9 && ReasonVisit != null) {
                ReasonVisit = ReasonVisit.replaceAll(" ", "");
                if (ReasonVisit.toUpperCase().equals("COVIDTESTING")) {
                    InsertCOVIDRegReply = this.InsertCOVIDReg(request, response, out, conn, String.valueOf(PatientRegId));
                    if (Integer.parseInt(InsertCOVIDRegReply) > 0) {
                        Message = "and COVID Form Also Registered Successfully.";
                        CDCFlag = "1";
                    } else {
                        Message = "and COVID Form Not Registered. ";
                        CDCFlag = "0";
                    }
                }
                Query = "Update victoria.PatientVisit set CDCFlag = '" + CDCFlag + "' where Id = " + VisitId;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            }
            if (ClientId.equals("Victoria-ER") || ClientId.equals("Ally-ER")) {
                String temp = SaveBundle_Victoria(request, out, conn, response, Database, ClientIndex, DirectoryName, PatientRegId, "REGISTRATION");
//                System.out.println("temp " + temp);
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
            } else if (ClientId.equals("Ally-ER")) {
                String temp = GETINPUTAlly(request, out, conn, response, Database, ClientIndex, DirectoryName, PatientRegId);
//                System.out.println("temp " + temp);
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
                Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " We Have Registered You Successfully " + Message + ". Please walk to the front door and Press the buzzer.  DATED: " + Date);
                Parser.SetField("FormName", String.valueOf("PatientReg2"));
                Parser.SetField("MRN", String.valueOf("MRN: " + MRN));
                Parser.SetField("ActionID", String.valueOf("Victoria_2&ClientId=" + ClientId));
                Parser.SetField("ClientId", String.valueOf(ClientId));
                Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(servletContext))) + "Exception/Message.html");
            }

        } catch (Exception e2) {
            helper.SendEmailWithAttachment("Error in PatientReg2 ** (SaveDataVictoria Main Catch ^^" + facilityName + " ##MES#010)", servletContext, e2, "PatientReg2", "SaveDataVictoria", conn);
            Services.DumException("PatientReg2", "SaveDataVictoria -- " + Query + " ", request, e2, this.getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "PatientReg2");
            Parser.SetField("ActionID", "Victoria_2&ClientId=" + ClientIndex + "");
            Parser.SetField("Message", "MES#010");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    private Dictionary doUpload(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws Exception {
        try {
            UUID uuid = UUID.randomUUID();
            String boundary = request.getHeader("Content-Type");
            int pos = boundary.indexOf('=');
            boundary = boundary.substring(pos + 1);
            boundary = "--" + boundary;
            byte[] bytes = new byte[512];
            int state = 0;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            String name = null;
            String value = null;
            String filename = null;
            String contentType = null;
            Dictionary<Object, Object> fields = new Hashtable<>();
            ServletInputStream in = request.getInputStream();
            int i = in.readLine(bytes, 0, 512);
            for (; -1 != i; i = in.readLine(bytes, 0, 512)) {
                String st = new String(bytes, 0, i);
                if (st.startsWith(boundary)) {
                    state = 0;
                    if (name != null) {

//                        System.out.println("NAME *** " + name);
//                        System.out.println("filename *** " + filename);

                        if (value != null) {
                            fields.put(name, value.substring(0, value.length() - 2));
                        } else if (buffer.size() > 2) {
                            uuid = UUID.randomUUID();
                            filename = filename.replaceAll("\\s+", "");
                            fields.put(name + uuid + filename, buffer);
                        }
                        name = null;
                        value = null;
                        filename = null;
                        contentType = null;
                        buffer = new ByteArrayOutputStream();
                    }
                } else if (st.startsWith("Content-Disposition: form-data") && state == 0) {
                    StringTokenizer tokenizer = new StringTokenizer(st, ";=\"");
                    while (tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        if (token.startsWith(" name")) {
                            name = tokenizer.nextToken();
                            state = 2;
                            continue;
                        }
                        if (token.startsWith(" filename")) {
                            filename = tokenizer.nextToken();
                            StringTokenizer ftokenizer = new StringTokenizer(filename, "\\/:");
                            filename = ftokenizer.nextToken();
                            while (ftokenizer.hasMoreTokens())
                                filename = ftokenizer.nextToken();
                            state = 1;
                            break;
                        }
                    }
                } else if (st.startsWith("Content-Type") && state == 1) {
                    pos = st.indexOf(":");
                    st.substring(pos + 2, st.length() - 2);
                } else if (st.equals("\r\n") && state == 1) {
                    state = 3;
                } else if (st.equals("\r\n") && state == 2) {
                    state = 4;
                } else if (state == 4) {
                    value = String.valueOf(String.valueOf(value)) + st;
                } else if (state == 3) {
                    buffer.write(bytes, 0, i);
                }
            }
            return fields;
        } catch (Exception var20) {
            System.out.println("Error in Do Upload!!");
            String str = "";
            for (int i = 0; i < (var20.getStackTrace()).length; i++)
                str = str + var20.getStackTrace()[i] + "<br>";
            System.out.println(str);
            throw new Exception("Error In Do Upload " + var20.getMessage());
        }
    }

    void PatientsDocUpload_Save(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response) {
        String ClientId = "";
        String DirectoryName = "";
        String Path = null;
        String UploadPath = null;

        boolean FileFound = false;
        byte[] Data = null;
        String key = "";
        String filename = "";
        String IDs = "";
        String InsuranceIDsF = "";
        String InsuranceIDsB = "";
        String value = "";


        HashMap<String, String> valuemap = new HashMap<String, String>();


        try {
            Dictionary d = doUpload(request, response, out);
            Enumeration<String> en = d.keys();
            while (en.hasMoreElements()) {
                key = en.nextElement();
                FileFound = false;
                if (!(key.startsWith("Idfront") || key.startsWith("insuranceFront") || key.startsWith("insuranceBack"))) {
                    value = (String) d.get(key);
                    valuemap.put(key, value.substring(4));
                }
                if (key.startsWith("ClientId")) {
                    ClientId = (String) d.get(key);
                } else if ((key.startsWith("Idfront") && key.endsWith(".jpg")) || (key.startsWith("Idfront") && key.endsWith(".png"))) {
                    filename = key;
                    FileFound = true;
                    IDs = key;
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                } else if ((key.startsWith("insuranceFront") && key.endsWith(".jpg")) || (key.startsWith("insuranceFront") && key.endsWith(".png"))) {
                    filename = key;
                    FileFound = true;
                    InsuranceIDsF = key;

                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                } else if ((key.startsWith("insuranceBack") && key.endsWith(".jpg")) || (key.startsWith("insuranceBack") && key.endsWith(".png"))) {
                    filename = key;
                    FileFound = true;
                    InsuranceIDsB = key;
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                }
                if (FileFound) {
                    Path = "/sftpdrive/AdmissionBundlePdf/Attachment/Victoria/";
                    UploadPath = String.valueOf(Path) + "/";
                    filename = filename.replaceAll("\\s+", "");
                    File fe = new File(String.valueOf(String.valueOf(UploadPath)) + filename);
                    if (fe.exists())
                        fe.delete();
                    FileOutputStream fouts = new FileOutputStream(fe);
                    fouts.write(Data);
                    fouts.flush();
                    fouts.close();
                }
            }

            ClientId = ClientId.substring(4);

            UtilityHelper helper = new UtilityHelper();

            SaveDataVictoria_2_2(request, conn, servletContext, response, helper, valuemap, out, IDs, InsuranceIDsF, InsuranceIDsB, ClientId);
        } catch (Exception e2) {
            out.println("Error in Upload DOcuments!!" + e2.getMessage());
            String str = "";
            for (int i = 0; i < (e2.getStackTrace()).length; i++)
                str = str + e2.getStackTrace()[i] + "<br>";
            out.println(str);
        }
    }

    String GETINPUTAlly(final HttpServletRequest request, final PrintWriter out, final Connection conn, final HttpServletResponse response, final String Database, final int ClientId, final String DirectoryName, int patientRegId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;//patientRegId;
        String DateTime = "";
        String Date = "";
        String Time = "";
        MergePdf mergePdf = new MergePdf();
        int ID = patientRegId;//Integer.parseInt(request.getParameter("ID").trim());
        String Path1 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/";
        String Path2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/";
        String ResultPdf = "";
        String Title = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String DOB = "";
        String Age = "";
        String gender = "";
        String Email = "";
        String ConfirmEmail = "";
        String MaritalStatus = "";
        String AreaCode = "";
        String PhNumber = "";
        String Address = "";
        String Address2 = "";
        String City = "";
        String State = "";
        String ZipCode = "";
        String Ethnicity = "";
        String Ethnicity_OthersText = "";
        String SSN = "";
        String EmployementChk = "";
        String Employer = "";
        String Occupation = "";
        String EmpContact = "";
        String PrimaryCarePhysicianChk = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        String PriCarePhyAddress = "";
        String PriCarePhyAddress2 = "";
        String PriCarePhyCity = "";
        String PriCarePhyState = "";
        String PriCarePhyZipCode = "";
        String PatientMinorChk = "";
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
        String GuarantorChk = "";
        String Guarantor = "";
        String GuarantorDOB = "";
        String GuarantorSEX = "";
        String GuarantorSSN = "";
        String GuarantorAddress = "";
        String GuarantorPhoneNumber = "";
        String GuarantorEmployer = "";
        String GuarantorEmployerAreaCode = "";
        String GuarantorEmployerPhNumber = "";
        String GuarantorEmployerAddress = "";
        String GuarantorEmployerAddress2 = "";
        String GuarantorEmployerCity = "";
        String GuarantorEmployerState = "";
        String GuarantorEmployerZipCode = "";
        int WorkersCompPolicyChk = 0;
        String WorkCompPolicyStr = "";
        String WCPDateofInjury = "";
        String WCPCaseNo = "";
        String WCPGroupNo = "";
        String WCPMemberId = "";
        String WCPInjuryRelatedAutoMotorAccident = "";
        String WCPInjuryRelatedWorkRelated = "";
        String WCPInjuryRelatedOtherAccident = "";
        String WCPInjuryRelatedNoAccident = "";
        String WCPInjuryOccurVehicle = "";
        String WCPInjuryOccurWork = "";
        String WCPInjuryOccurHome = "";
        String WCPInjuryOccurOther = "";
        String WCPInjuryDescription = "";
        String WCPHRFirstName = "";
        String WCPHRLastName = "";
        final String WCPHRAreaCode = "";
        String WCPHRPhoneNumber = "";
        String WCPHRAddress = "";
        final String WCPHRAddress2 = "";
        String WCPHRCity = "";
        String WCPHRState = "";
        String WCPHRZipCode = "";
        String WCPPlanName = "";
        String WCPCarrierName = "";
        final String WCPPayerAreaCode = "";
        String WCPPayerPhoneNumber = "";
        String WCPCarrierAddress = "";
        final String WCPCarrierAddress2 = "";
        String WCPCarrierCity = "";
        String WCPCarrierState = "";
        String WCPCarrierZipCode = "";
        String WCPAdjudicatorFirstName = "";
        String WCPAdjudicatorLastName = "";
        final String WCPAdjudicatorAreaCode = "";
        String WCPAdjudicatorPhoneNumber = "";
        final String WCPAdjudicatorFaxAreaCode = "";
        String WCPAdjudicatorFaxPhoneNumber = "";
        int MotorVehicleAccidentChk = 0;
        String MotorVehAccidentStr = "";
        String AutoInsuranceInformationChk = "0";
        String AIIDateofAccident = "";
        String AIIAutoClaim = "";
        String AIIAccidentLocationAddress = "";
        final String AIIAccidentLocationAddress2 = "";
        String AIIAccidentLocationCity = "";
        String AIIAccidentLocationState = "";
        String AIIAccidentLocationZipCode = "";
        String AIIRoleInAccident = "";
        String AIITypeOfAutoIOnsurancePolicy = "";
        String AIIPrefixforReponsibleParty = "";
        String AIIFirstNameforReponsibleParty = "";
        String AIIMiddleNameforReponsibleParty = "";
        String AIILastNameforReponsibleParty = "";
        String AIISuffixforReponsibleParty = "";
        String AIICarrierResponsibleParty = "";
        String AIICarrierResponsiblePartyAddress = "";
        final String AIICarrierResponsiblePartyAddress2 = "";
        String AIICarrierResponsiblePartyCity = "";
        String AIICarrierResponsiblePartyState = "";
        String AIICarrierResponsiblePartyZipCode = "";
        final String AIICarrierResponsiblePartyAreaCode = "";
        String AIICarrierResponsiblePartyPhoneNumber = "";
        String AIICarrierResponsiblePartyPolicyNumber = "";
        String AIIResponsiblePartyAutoMakeModel = "";
        String AIIResponsiblePartyLicensePlate = "";
        String AIIFirstNameOfYourPolicyHolder = "";
        String AIILastNameOfYourPolicyHolder = "";
        String AIINameAutoInsuranceOfYourVehicle = "";
        String AIIYourInsuranceAddress = "";
        final String AIIYourInsuranceAddress2 = "";
        String AIIYourInsuranceCity = "";
        String AIIYourInsuranceState = "";
        String AIIYourInsuranceZipCode = "";
        final String AIIYourInsuranceAreaCode = "";
        String AIIYourInsurancePhoneNumber = "";
        String AIIYourInsurancePolicyNo = "";
        String AIIYourLicensePlate = "";
        String AIIYourCarMakeModelYear = "";
        int HealthInsuranceChk = 0;
        String GovtFundedInsurancePlanChk = "";
        int GFIPMedicare = 0;
        int GFIPMedicaid = 0;
        int GFIPCHIP = 0;
        int GFIPTricare = 0;
        int GFIPVHA = 0;
        int GFIPIndianHealth = 0;
        String InsuranceSubPatient = null;
        String InsuranceSubGuarantor = null;
        String InsuranceSubOther = null;
        String HIPrimaryInsurance = "";
        String HISubscriberFirstName = "";
        String HISubscriberLastName = "";
        String HISubscriberDOB = "";
        String HISubscriberSSN = "";
        String HISubscriberRelationtoPatient = "";
        String HISubscriberGroupNo = "";
        String HISubscriberPolicyNo = "";
        String SecondHealthInsuranceChk = "";
        String SHISecondaryName = "";
        String SHISubscriberFirstName = "";
        String SHISubscriberLastName = "";
        final String SHISubscriberDOB = "";
        String SHISubscriberRelationtoPatient = "";
        String SHISubscriberGroupNo = "";
        String SHISubscriberPolicyNo = "";
        int SelfPayChk = 0;
        String FirstNameNoSpaces = "";
        String LastNameNoSpace = "";
        String CityStateZip = "";
        final String Country = "";
        String MRN = "";
        int ClientIndex = 0;
        String ClientName = "";
        String DOS = "";
        String DoctorId = null;
        final String DoctorName = null;
        try {
            PreparedStatement ps = conn.prepareStatement("select date_format(now(),'%Y%m%d%H%i%s'), DATE_FORMAT(now(), '%m/%d/%Y'), DATE_FORMAT(now(), '%T')");
            rset = ps.executeQuery();
            if (rset.next()) {
                DateTime = rset.getString(1);
                Date = rset.getString(2);
                Time = rset.getString(3);
            }
            rset.close();
            ps.close();
            try {
                ps = conn.prepareStatement(" Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), " +
                        "IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-'), IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), IFNULL(DoctorsName,'-')  From " + Database + ".PatientReg Where ID = ? ");
                ps.setInt(1, ID);
                rset = ps.executeQuery();
                while (rset.next()) {
                    PatientRegId = ID;
                    LastName = rset.getString(1).trim();
                    FirstName = rset.getString(2).trim();
                    FirstNameNoSpaces = FirstName.replaceAll("\\s+", "");
                    LastNameNoSpace = LastName.replaceAll(" ", "");
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
                ps.close();
                ps = conn.prepareStatement("Select name from oe.clients where Id = ?");
                ps.setInt(1, ClientId);
                rset = ps.executeQuery();
                if (rset.next()) {
                    ClientName = rset.getString(1);
                }
                rset.close();
                ps.close();
            } catch (Exception e) {
                out.println("Error In PateintReg:--" + e.getMessage());
                out.println(ps.toString());
            }
            ps = conn.prepareStatement("Select  Ethnicity,Ethnicity_OthersText,EmployementChk,Employer,Occupation,EmpContact,PrimaryCarePhysicianChk,PriCarePhy,ReasonVisit,PriCarePhyAddress,PriCarePhyCity,PriCarePhyState,PriCarePhyZipCode,PatientMinorChk,GuarantorChk,GuarantorEmployer,GuarantorEmployerPhNumber,GuarantorEmployerAddress,GuarantorEmployerCity,GuarantorEmployerState,GuarantorEmployerZipCode,CreatedDate,WorkersCompPolicyChk,MotorVehicleAccidentChk,HealthInsuranceChk from " + Database + ".PatientReg_Details where PatientRegId = ?");
            ps.setInt(1, ID);
            rset = ps.executeQuery();
            while (rset.next()) {
                Ethnicity = rset.getString(1);
                Ethnicity_OthersText = rset.getString(2);
                EmployementChk = rset.getString(3);
                Employer = rset.getString(4);
                Occupation = rset.getString(5);
                EmpContact = rset.getString(6);
                PrimaryCarePhysicianChk = rset.getString(7);
                PriCarePhy = rset.getString(8);
                if (ReasonVisit == null) {
                    ReasonVisit = rset.getString(9);
                }
                PriCarePhyAddress = rset.getString(10);
                PriCarePhyCity = rset.getString(11);
                PriCarePhyState = rset.getString(12);
                PriCarePhyZipCode = rset.getString(13);
                PatientMinorChk = rset.getString(14);
                GuarantorChk = rset.getString(15);
                GuarantorEmployer = rset.getString(16);
                GuarantorEmployerPhNumber = rset.getString(17);
                GuarantorEmployerAddress = rset.getString(18);
                GuarantorEmployerCity = rset.getString(19);
                GuarantorEmployerState = rset.getString(20);
                GuarantorEmployerZipCode = rset.getString(21);
                WorkersCompPolicyChk = rset.getInt(23);
                MotorVehicleAccidentChk = rset.getInt(24);
                HealthInsuranceChk = rset.getInt(25);
            }
            rset.close();
            ps.close();
            if (WorkersCompPolicyChk == 1) {
                try {
                    ps = conn.prepareStatement("Select IFNULL(DATE_FORMAT(WCPDateofInjury,'%m/%d/%Y'),''), IFNULL(WCPCaseNo,''), IFNULL(WCPGroupNo,''), IFNULL(WCPMemberId,''), IFNULL(WCPInjuryRelatedAutoMotorAccident,''), IFNULL(WCPInjuryRelatedWorkRelated,''), IFNULL(WCPInjuryRelatedOtherAccident,''), IFNULL(WCPInjuryRelatedNoAccident,''), IFNULL(WCPInjuryOccurVehicle,''), IFNULL(WCPInjuryOccurWork,''), IFNULL(WCPInjuryOccurHome,''), IFNULL(WCPInjuryOccurOther,''), IFNULL(WCPInjuryDescription,''), IFNULL(WCPHRFirstName,''), IFNULL(WCPHRLastName,''), IFNULL(WCPHRPhoneNumber,''), IFNULL(WCPHRAddress,''), IFNULL(WCPHRCity,''), IFNULL(WCPHRState,''), IFNULL(WCPHRZipCode,''), IFNULL(WCPPlanName,''), IFNULL(WCPCarrierName,''), IFNULL(WCPPayerPhoneNumber,''), IFNULL(WCPCarrierAddress,''), IFNULL(WCPCarrierCity,''), IFNULL(WCPCarrierState,''), IFNULL(WCPCarrierZipCode,''), IFNULL(WCPAdjudicatorFirstName,''), IFNULL(WCPAdjudicatorLastName,''), IFNULL(WCPAdjudicatorPhoneNumber,''), IFNULL(WCPAdjudicatorFaxPhoneNumber,'') from " + Database + ".Patient_WorkCompPolicy where PatientRegId = ?");
                    ps.setInt(1, ID);
                    rset = ps.executeQuery();
                    if (rset.next()) {
                        WCPDateofInjury = rset.getString(1);
                        WCPCaseNo = rset.getString(2);
                        WCPGroupNo = rset.getString(3);
                        WCPMemberId = rset.getString(4);
                        WCPInjuryRelatedAutoMotorAccident = rset.getString(5);
                        WCPInjuryRelatedWorkRelated = rset.getString(6);
                        WCPInjuryRelatedOtherAccident = rset.getString(7);
                        WCPInjuryRelatedNoAccident = rset.getString(8);
                        WCPInjuryOccurVehicle = rset.getString(9);
                        WCPInjuryOccurWork = rset.getString(10);
                        WCPInjuryOccurHome = rset.getString(11);
                        WCPInjuryOccurOther = rset.getString(12);
                        WCPInjuryDescription = rset.getString(13);
                        WCPHRFirstName = rset.getString(14);
                        WCPHRLastName = rset.getString(15);
                        WCPHRPhoneNumber = rset.getString(16);
                        WCPHRAddress = rset.getString(17);
                        WCPHRCity = rset.getString(18);
                        WCPHRState = rset.getString(19);
                        WCPHRZipCode = rset.getString(20);
                        WCPPlanName = rset.getString(21);
                        WCPCarrierName = rset.getString(22);
                        WCPPayerPhoneNumber = rset.getString(23);
                        WCPCarrierAddress = rset.getString(24);
                        WCPCarrierCity = rset.getString(25);
                        WCPCarrierState = rset.getString(26);
                        WCPCarrierZipCode = rset.getString(27);
                        WCPAdjudicatorFirstName = rset.getString(28);
                        WCPAdjudicatorLastName = rset.getString(29);
                        WCPAdjudicatorPhoneNumber = rset.getString(30);
                        WCPAdjudicatorFaxPhoneNumber = rset.getString(31);
                    }
                    rset.close();
                    ps.close();
                } catch (Exception e) {
                    System.out.println("Error in Getting Data from Patient_WorkCompPolicy");
                    Services.DumException("DownloadBundle", "GetINput Victoria", request, e, this.getServletContext());
                }
            }
            if (MotorVehicleAccidentChk == 1) {
                try {
                    ps = conn.prepareStatement("Select IFNULL(AutoInsuranceInformationChk,'0'), IFNULL(DATE_FORMAT(AIIDateofAccident,'%m/%d/%Y'),''), IFNULL(AIIAutoClaim,''), IFNULL(AIIAccidentLocationAddress,''), IFNULL(AIIAccidentLocationCity,''), IFNULL(AIIAccidentLocationState,''), IFNULL(AIIAccidentLocationZipCode,''), IFNULL(AIIRoleInAccident,''), IFNULL(AIITypeOfAutoIOnsurancePolicy,''), IFNULL(AIIPrefixforReponsibleParty,''), IFNULL(AIIFirstNameforReponsibleParty,''), IFNULL(AIIMiddleNameforReponsibleParty,''), IFNULL(AIILastNameforReponsibleParty,''), IFNULL(AIISuffixforReponsibleParty,''), IFNULL(AIICarrierResponsibleParty,''), IFNULL(AIICarrierResponsiblePartyAddress,''), IFNULL(AIICarrierResponsiblePartyCity,''), IFNULL(AIICarrierResponsiblePartyState,''), IFNULL(AIICarrierResponsiblePartyZipCode,''), IFNULL(AIICarrierResponsiblePartyPhoneNumber,''), IFNULL(AIICarrierResponsiblePartyPolicyNumber,''), IFNULL(AIIResponsiblePartyAutoMakeModel,''), IFNULL(AIIResponsiblePartyLicensePlate,''), IFNULL(AIIFirstNameOfYourPolicyHolder,''), IFNULL(AIILastNameOfYourPolicyHolder,''), IFNULL(AIINameAutoInsuranceOfYourVehicle,''), IFNULL(AIIYourInsuranceAddress,''), IFNULL(AIIYourInsuranceCity,''), IFNULL(AIIYourInsuranceState,''), IFNULL(AIIYourInsuranceZipCode,''), IFNULL(AIIYourInsurancePhoneNumber,''),IFNULL(AIIYourInsurancePolicyNo,''), IFNULL(AIIYourLicensePlate,''), IFNULL(AIIYourCarMakeModelYear,'') from " + Database + ".Patient_AutoInsuranceInfo where PatientRegId = ?");
                    ps.setInt(1, ID);
                    rset = ps.executeQuery();
                    if (rset.next()) {
                        AutoInsuranceInformationChk = rset.getString(1);
                        AIIDateofAccident = rset.getString(2);
                        AIIAutoClaim = rset.getString(3);
                        AIIAccidentLocationAddress = rset.getString(4);
                        AIIAccidentLocationCity = rset.getString(5);
                        AIIAccidentLocationState = rset.getString(6);
                        AIIAccidentLocationZipCode = rset.getString(7);
                        AIIRoleInAccident = rset.getString(8);
                        AIITypeOfAutoIOnsurancePolicy = rset.getString(9);
                        AIIPrefixforReponsibleParty = rset.getString(10);
                        AIIFirstNameforReponsibleParty = rset.getString(11);
                        AIIMiddleNameforReponsibleParty = rset.getString(12);
                        AIILastNameforReponsibleParty = rset.getString(13);
                        AIISuffixforReponsibleParty = rset.getString(14);
                        AIICarrierResponsibleParty = rset.getString(15);
                        AIICarrierResponsiblePartyAddress = rset.getString(16);
                        AIICarrierResponsiblePartyCity = rset.getString(17);
                        AIICarrierResponsiblePartyState = rset.getString(18);
                        AIICarrierResponsiblePartyZipCode = rset.getString(19);
                        AIICarrierResponsiblePartyPhoneNumber = rset.getString(20);
                        AIICarrierResponsiblePartyPolicyNumber = rset.getString(21);
                        AIIResponsiblePartyAutoMakeModel = rset.getString(22);
                        AIIResponsiblePartyLicensePlate = rset.getString(23);
                        AIIFirstNameOfYourPolicyHolder = rset.getString(24);
                        AIILastNameOfYourPolicyHolder = rset.getString(25);
                        AIINameAutoInsuranceOfYourVehicle = rset.getString(26);
                        AIIYourInsuranceAddress = rset.getString(27);
                        AIIYourInsuranceCity = rset.getString(28);
                        AIIYourInsuranceState = rset.getString(29);
                        AIIYourInsuranceZipCode = rset.getString(30);
                        AIIYourInsurancePhoneNumber = rset.getString(31);
                        AIIYourInsurancePolicyNo = rset.getString(32);
                        AIIYourLicensePlate = rset.getString(33);
                        AIIYourCarMakeModelYear = rset.getString(34);
                    }
                    rset.close();
                    ps.close();
                } catch (Exception e) {
                    System.out.println("Error in Getting Data from Patient_AutoInsuranceInfo");
                    Services.DumException("DownloadBundle", "GetINput Victoria", request, e, this.getServletContext());
                }
            }
            if (HealthInsuranceChk == 1) {
                try {
                    ps = conn.prepareStatement("Select IFNULL(GovtFundedInsurancePlanChk,'0'), IFNULL(GFIPMedicare,'0'), IFNULL(GFIPMedicaid,'0'), IFNULL(GFIPCHIP,'0'), IFNULL(GFIPTricare,'0'), IFNULL(GFIPVHA,'0'), IFNULL(GFIPIndianHealth,'0'), IFNULL(InsuranceSubPatient,''), IFNULL(InsuranceSubGuarantor,''), IFNULL(InsuranceSubOther,''), IFNULL(HIPrimaryInsurance,''), IFNULL(HISubscriberFirstName,''), IFNULL(HISubscriberLastName,''), IFNULL(HISubscriberDOB,''), IFNULL(HISubscriberSSN,''), IFNULL(HISubscriberRelationtoPatient,''), IFNULL(HISubscriberGroupNo,''), IFNULL(HISubscriberPolicyNo,''), IFNULL(SecondHealthInsuranceChk,''), IFNULL(SHISecondaryName,''), IFNULL(SHISubscriberFirstName,''), IFNULL(SHISubscriberLastName,''), IFNULL(SHISubscriberRelationtoPatient,''), IFNULL(SHISubscriberGroupNo,''), IFNULL(SHISubscriberPolicyNo,'')  from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = ? ");
                    ps.setInt(1, ID);
                    rset = ps.executeQuery();
                    if (rset.next()) {
                        GovtFundedInsurancePlanChk = rset.getString(1);
                        GFIPMedicare = rset.getInt(2);
                        GFIPMedicaid = rset.getInt(3);
                        GFIPCHIP = rset.getInt(4);
                        GFIPTricare = rset.getInt(5);
                        GFIPVHA = rset.getInt(6);
                        GFIPIndianHealth = rset.getInt(7);
                        InsuranceSubPatient = rset.getString(8);
                        InsuranceSubGuarantor = rset.getString(9);
                        InsuranceSubOther = rset.getString(10);
                        HIPrimaryInsurance = rset.getString(11);
                        HISubscriberFirstName = rset.getString(12);
                        HISubscriberLastName = rset.getString(13);
                        HISubscriberDOB = rset.getString(14);
                        HISubscriberSSN = rset.getString(15);
                        HISubscriberRelationtoPatient = rset.getString(16);
                        HISubscriberGroupNo = rset.getString(17);
                        HISubscriberPolicyNo = rset.getString(18);
                        SecondHealthInsuranceChk = rset.getString(19);
                        SHISecondaryName = rset.getString(20);
                        SHISubscriberFirstName = rset.getString(21);
                        SHISubscriberLastName = rset.getString(22);
                        SHISubscriberRelationtoPatient = rset.getString(23);
                        SHISubscriberGroupNo = rset.getString(24);
                        SHISubscriberPolicyNo = rset.getString(25);
                    }
                    rset.close();
                    ps.close();
                } catch (Exception e) {
                    System.out.println("Error in Getting Data from Patient_HealthInsuraneInfo");
                    Services.DumException("DownloadBundle", "GetINput Victoria", request, e, this.getServletContext());
                }
            }
            if (Ethnicity.equals("1")) {
                Ethnicity = "Hispanic";
            } else if (Ethnicity.equals("2")) {
                Ethnicity = "Non-Hispanic";
            } else if (Ethnicity.equals("3")) {
                Ethnicity = "Unknown";
            }
            if (GuarantorChk.equals("1")) {
                Guarantor = "The Patient";
                GuarantorDOB = DOB;
                GuarantorSEX = gender;
                GuarantorSSN = SSN;
                GuarantorAddress = Address + "";
                GuarantorPhoneNumber = "" + PhNumber;
            } else if (GuarantorChk.equals("2")) {
                Guarantor = "Legal Guardian";
                GuarantorDOB = "-";
                GuarantorSEX = "-";
                GuarantorSSN = "-";
                GuarantorAddress = "-";
                GuarantorPhoneNumber = "-";
            } else if (GuarantorChk.equals("3")) {
                Guarantor = "Patient Parent";
                GuarantorDOB = "-";
                GuarantorSEX = "-";
                GuarantorSSN = "-";
                GuarantorAddress = "-";
                GuarantorPhoneNumber = "-";
            } else if (GuarantorChk.equals("2")) {
                Guarantor = "Spouse/Partner";
                GuarantorDOB = "-";
                GuarantorSEX = "-";
                GuarantorSSN = "-";
                GuarantorAddress = "-";
                GuarantorPhoneNumber = "-";
            }
            if (WorkersCompPolicyChk == 1) {
                WorkCompPolicyStr = "Yes";
            } else {
                WorkCompPolicyStr = "No";
            }
            if (MotorVehicleAccidentChk == 1) {
                MotorVehAccidentStr = "Yes";
            } else {
                MotorVehAccidentStr = "No";
            }
            if (HISubscriberDOB.equals("00/00/0000")) {
                HISubscriberDOB = "";
            }
            if (WCPDateofInjury.equals("00/00/0000")) {
                WCPDateofInjury = "";
            }
            if (AIIDateofAccident.equals("00/00/0000")) {
                AIIDateofAccident = "";
            }
            ps = conn.prepareStatement(" Select CASE WHEN MFFirstVisit = 1 THEN 'YES' ELSE '' END,  CASE WHEN MFReturnPat = 1 THEN 'YES' ELSE '' END,  CASE WHEN MFInternetFind = 1 THEN 'YES' ELSE '' END,  CASE WHEN Facebook = 1 THEN 'YES' ELSE '' END,  CASE WHEN MapSearch = 1 THEN 'YES' ELSE '' END,  CASE WHEN GoogleSearch = 1 THEN 'YES' ELSE '' END,  CASE WHEN VERWebsite = 1 THEN 'YES' ELSE '' END,  CASE WHEN WebsiteAds = 1 THEN 'YES' ELSE '' END, CASE WHEN OnlineReviews = 1 THEN 'YES' ELSE '' END, CASE WHEN Twitter = 1 THEN 'YES' ELSE '' END, CASE WHEN LinkedIn = 1 THEN 'YES' ELSE '' END, CASE WHEN EmailBlast = 1 THEN 'YES' ELSE '' END, CASE WHEN YouTube = 1 THEN 'YES' ELSE '' END, CASE WHEN TV = 1 THEN 'YES' ELSE '' END, CASE WHEN Billboard = 1 THEN 'YES' ELSE '' END, CASE WHEN Radio = 1 THEN 'YES' ELSE '' END, CASE WHEN Brochure = 1 THEN 'YES' ELSE '' END, CASE WHEN DirectMail = 1 THEN 'YES' ELSE '' END, CASE WHEN CitizensDeTar = 1 THEN 'YES' ELSE '' END, CASE WHEN LiveWorkNearby = 1 THEN 'YES' ELSE '' END, CASE WHEN FamilyFriend = 1 THEN 'YES' ELSE '' END, IFNULL(FamilyFriend_text,''),  CASE WHEN UrgentCare = 1 THEN 'YES' ELSE '' END, IFNULL(UrgentCare_text,''),  CASE WHEN NewspaperMagazine = 1 THEN 'YES' ELSE '' END, IFNULL(NewspaperMagazine_text,''),  CASE WHEN School = 1 THEN 'YES' ELSE '' END, IFNULL(School_text,''),  CASE WHEN Hotel = 1 THEN 'YES' ELSE '' END, IFNULL(Hotel_text,''), IFNULL(MFPhysician,'')  FROM " + Database + ".MarketingInfo WHERE PatientRegId = ?");
            ps.setInt(1, ID);
            rset = ps.executeQuery();
            if (rset.next()) {
                MFFirstVisit = rset.getString(1);
                MFReturnPat = rset.getString(2);
                MFInternetFind = rset.getString(3);
                Facebook = rset.getString(4);
                MapSearch = rset.getString(5);
                GoogleSearch = rset.getString(6);
                VERWebsite = rset.getString(7);
                WebsiteAds = rset.getString(8);
                OnlineReviews = rset.getString(9);
                Twitter = rset.getString(10);
                LinkedIn = rset.getString(11);
                EmailBlast = rset.getString(12);
                YouTube = rset.getString(13);
                TV = rset.getString(14);
                Billboard = rset.getString(15);
                Radio = rset.getString(16);
                Brochure = rset.getString(17);
                DirectMail = rset.getString(18);
                CitizensDeTar = rset.getString(19);
                LiveWorkNearby = rset.getString(20);
                FamilyFriend = rset.getString(21);
                FamilyFriend_text = rset.getString(22);
                UrgentCare = rset.getString(23);
                UrgentCare_text = rset.getString(24);
                NewspaperMagazine = rset.getString(25);
                NewspaperMagazine_text = rset.getString(26);
                School = rset.getString(27);
                School_text = rset.getString(28);
                Hotel = rset.getString(29);
                Hotel_text = rset.getString(30);
                MFPhysician = rset.getString(31);
            }
            rset.close();
            ps.close();

            String UID = "";
            Image SignImages = null;
            final File tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_0_" + ID + ".png");
            final boolean exists = tmpDir.exists();
            if (exists) {
                ps = conn.prepareStatement("Select UID from " + Database + ".SignRequest where PatientRegId = ?");
                ps.setInt(1, ID);
                rset = ps.executeQuery();
                if (rset.next()) {
                    UID = rset.getString(1);
                }
                rset.close();
                stmt.close();

                SignImages = Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_0_" + ID + ".png");
                SignImages.scaleAbsolute(80.0f, 30.0f);
                //outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + UID + "_.pdf";
            } else {
                SignImages = null;
            }

            String inputFilePathTmp = "";
            String outputFilePathTmp = "";
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/ABNformEnglish.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/ABNformEnglish_" + ClientId + "_" + MRN + ".pdf";
            OutputStream fos1 = new FileOutputStream(new File(outputFilePathTmp));
            PdfReader pdfReader1 = new PdfReader(inputFilePathTmp);
            PdfStamper pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(110.0f, 80.0f);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410.0f, 80.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100.0f, 120.0f);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410.0f, 120.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(110.0f, 210.0f);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410.0f, 210.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/WC_QUESTIONNAIRE.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/WC_QUESTIONNAIRE_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 700.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 675.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 650.0f);
                    pdfContentByte.showText(WCPMemberId);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 630.0f);
                    pdfContentByte.showText(WCPGroupNo);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 610.0f);
                    pdfContentByte.showText(WCPDateofInjury);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 585.0f);
                    pdfContentByte.showText(WCPCaseNo);
                    pdfContentByte.endText();
                    if (WCPInjuryRelatedAutoMotorAccident.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(175.0f, 535.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(175.0f, 535.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryRelatedWorkRelated.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(275.0f, 535.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(275.0f, 535.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryRelatedOtherAccident.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(380.0f, 535.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(380.0f, 535.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryRelatedNoAccident.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(475.0f, 535.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(475.0f, 535.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryOccurVehicle.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(220.0f, 515.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(220.0f, 515.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryOccurWork.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(280.0f, 515.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(280.0f, 515.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryOccurHome.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(340.0f, 515.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(340.0f, 515.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryOccurOther.equals("1")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(400.0f, 515.0f);
                        pdfContentByte.showText("Yes");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(400.0f, 515.0f);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryDescription.length() <= 114) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 450.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(0, WCPInjuryDescription.length()));
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryDescription.length() > 114 && WCPInjuryDescription.length() <= 228) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 450.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(0, 114));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 435.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(114, WCPInjuryDescription.length()));
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryDescription.length() > 228 && WCPInjuryDescription.length() <= 342) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 450.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(0, 114));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 435.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(114, 228));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 420.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(228, WCPInjuryDescription.length()));
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryDescription.length() > 342 && WCPInjuryDescription.length() <= 456) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 450.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(0, 114));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 435.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(114, 228));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 420.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(228, 342));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 408.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(342, WCPInjuryDescription.length()));
                        pdfContentByte.endText();
                    }
                    if (WCPInjuryDescription.length() > 456) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 450.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(0, 114));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 435.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(114, 228));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 420.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(228, 342));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 408.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(342, 456));
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(60.0f, 393.0f);
                        pdfContentByte.showText(WCPInjuryDescription.substring(456, WCPInjuryDescription.length()));
                        pdfContentByte.endText();
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 350.0f);
                    pdfContentByte.showText(WCPHRFirstName + " " + WCPHRLastName + " / " + WCPHRPhoneNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 325.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90.0f, 300.0f);
                    pdfContentByte.showText(WCPHRAddress);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 280.0f);
                    pdfContentByte.showText(WCPHRCity);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(230.0f, 280.0f);
                    pdfContentByte.showText(WCPHRState);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 260.0f);
                    pdfContentByte.showText(WCPHRZipCode);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 350.0f);
                    pdfContentByte.showText(WCPPlanName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 325.0f);
                    pdfContentByte.showText(WCPCarrierName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 300.0f);
                    pdfContentByte.showText(WCPPayerPhoneNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380.0f, 280.0f);
                    pdfContentByte.showText(WCPCarrierAddress);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(370.0f, 260.0f);
                    pdfContentByte.showText(WCPCarrierCity);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(470.0f, 260.0f);
                    pdfContentByte.showText(WCPCarrierState);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350.0f, 235.0f);
                    pdfContentByte.showText(WCPCarrierZipCode);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100.0f, 210.0f);
                    pdfContentByte.showText(WCPAdjudicatorFirstName + " " + WCPAdjudicatorLastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100.0f, 185.0f);
                    pdfContentByte.showText(WCPAdjudicatorPhoneNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100.0f, 165.0f);
                    pdfContentByte.showText(WCPAdjudicatorFaxPhoneNumber);
                    pdfContentByte.endText();
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(200.0f, 140.0f);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 140.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380.0f, 120.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/WC_MVA_assignmentofproceeds.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/WC_MVA_assignmentofproceeds_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(55.0f, 625.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 447.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 130.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 80.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                }
                if (i == 2) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 710.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(210.0f, 680.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 630.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 480.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60.0f, 355.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(110.0f, 280.0f);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 280.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/MVA_ASSIGNMENTOFPROCEEDS.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/MVA_ASSIGNMENTOFPROCEEDS_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 2) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60.0f, 355.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(110.0f, 290.0f);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 290.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/MVACLAIMFORM.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/MVACLAIMFORM_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 640.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0f, 620.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 620.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 595.0f);
                    pdfContentByte.showText(Address);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(440.0f, 595.0f);
                    pdfContentByte.showText(PhNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(130.0f, 570.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(290.0f, 570.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 570.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(130.0f, 545.0f);
                    pdfContentByte.showText(AIIAutoClaim);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 522.0f);
                    pdfContentByte.showText(AIIDateofAccident);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(165.0f, 500.0f);
                    pdfContentByte.showText(AIIAccidentLocationAddress);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(300.0f, 500.0f);
                    pdfContentByte.showText(AIIAccidentLocationCity);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 500.0f);
                    pdfContentByte.showText(AIIAccidentLocationState);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(470.0f, 500.0f);
                    pdfContentByte.showText(AIIAccidentLocationZipCode);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(145.0f, 452.0f);
                    pdfContentByte.showText(AIIRoleInAccident);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250.0f, 352.0f);
                    pdfContentByte.showText(AIITypeOfAutoIOnsurancePolicy);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 320.0f);
                    pdfContentByte.showText(AIIPrefixforReponsibleParty + " " + AIIFirstNameforReponsibleParty + " " + AIIMiddleNameforReponsibleParty + " " + AIILastNameforReponsibleParty + " " + AIISuffixforReponsibleParty);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(280.0f, 300.0f);
                    pdfContentByte.showText(AIICarrierResponsibleParty);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0f, 280.0f);
                    pdfContentByte.showText(AIICarrierResponsiblePartyAddress);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 255.0f);
                    pdfContentByte.showText(AIICarrierResponsiblePartyCity);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(270.0f, 255.0f);
                    pdfContentByte.showText(AIICarrierResponsiblePartyState);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350.0f, 255.0f);
                    pdfContentByte.showText(AIICarrierResponsiblePartyZipCode);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(180.0f, 230.0f);
                    pdfContentByte.showText(AIICarrierResponsiblePartyPhoneNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(180.0f, 210.0f);
                    pdfContentByte.showText(AIICarrierResponsiblePartyPolicyNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 210.0f);
                    pdfContentByte.showText(AIIResponsiblePartyLicensePlate);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(240.0f, 190.0f);
                    pdfContentByte.showText(AIIResponsiblePartyAutoMakeModel);
                    pdfContentByte.endText();
                }
                if (i == 2) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(210.0f, 680.0f);
                    pdfContentByte.showText(AIIFirstNameOfYourPolicyHolder + " " + AIILastNameOfYourPolicyHolder);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250.0f, 650.0f);
                    pdfContentByte.showText(AIINameAutoInsuranceOfYourVehicle);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 630.0f);
                    pdfContentByte.showText(AIIYourInsuranceAddress);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0f, 610.0f);
                    pdfContentByte.showText(AIIYourInsuranceCity);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(270.0f, 610.0f);
                    pdfContentByte.showText(AIIYourInsuranceState);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350.0f, 610.0f);
                    pdfContentByte.showText(AIIYourInsuranceZipCode);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(180.0f, 585.0f);
                    pdfContentByte.showText(AIIYourInsurancePhoneNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 565.0f);
                    pdfContentByte.showText(AIIYourInsurancePolicyNo);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 565.0f);
                    pdfContentByte.showText(AIIYourLicensePlate);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260.0f, 545.0f);
                    pdfContentByte.showText(AIIYourCarMakeModelYear);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/Medicalreleaseform.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/Medicalreleaseform_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(350.0f, 180.0f);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 570.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 552.0f);
                    pdfContentByte.showText(SSN);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 535.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500.0f, 180.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350.0f, 140.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            String inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/UHCINSAPPEALFORMS.pdf";
            String outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/UHCINSAPPEALFORMS_" + ClientId + "_" + MRN + ".pdf";
            FileOutputStream fos2 = new FileOutputStream(new File(outputFilePathTmp2));
            PdfReader pdfReader2 = new PdfReader(inputFilePathTmp2);
            PdfStamper pdfStamper2 = new PdfStamper(pdfReader2, (OutputStream) fos2);
            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(160.0f, 690.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 665.0f);
                    pdfContentByte2.showText(HISubscriberLastName + ", " + HISubscriberFirstName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 640.0f);
                    pdfContentByte2.showText(WCPMemberId);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 595.0f);
                    pdfContentByte2.showText("Victoria ED");
                    pdfContentByte2.endText();
                }
                if (j == 2) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(90.0f, 470.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName);
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();
            inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/MEDICAIDSELFPAYAGREEMENT.pdf";
            outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/MEDICAIDSELFPAYAGREEMENT_" + ClientId + "_" + MRN + ".pdf";
            fos2 = new FileOutputStream(new File(outputFilePathTmp2));
            pdfReader2 = new PdfReader(inputFilePathTmp2);
            pdfStamper2 = new PdfStamper(pdfReader2, (OutputStream) fos2);
            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(175.0f, 90.0f);
                        pdfContentByte2.addImage(SignImages);
                    }
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(475.0f, 90.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();
            inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/FINANCIAL_HARDSHIP_RELIEF.pdf";
            outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/FINANCIAL_HARDSHIP_RELIEF_" + ClientId + "_" + MRN + ".pdf";
            fos2 = new FileOutputStream(new File(outputFilePathTmp2));
            pdfReader2 = new PdfReader(inputFilePathTmp2);
            pdfStamper2 = new PdfStamper(pdfReader2, (OutputStream) fos2);
            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(150.0f, 640.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName);
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(420.0f, 640.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(170.0f, 610.0f);
                    pdfContentByte2.showText(HISubscriberRelationtoPatient);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(430.0f, 610.0f);
                    pdfContentByte2.showText(HISubscriberFirstName + " " + HISubscriberLastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(250.0f, 580.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(35.0f, 520.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252.0f, 520.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(35.0f, 492.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252.0f, 492.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(35.0f, 462.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252.0f, 462.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(35.0f, 432.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252.0f, 432.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252.0f, 392.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(110.0f, 290.0f);
                        pdfContentByte2.addImage(SignImages);
                    }
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(342.0f, 290.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();
            inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/Marketing_Slips.pdf";
            outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/Marketing_Slips_" + ClientId + "_" + MRN + ".pdf";
            fos2 = new FileOutputStream(new File(outputFilePathTmp2));
            pdfReader2 = new PdfReader(inputFilePathTmp2);
            pdfStamper2 = new PdfStamper(pdfReader2, (OutputStream) fos2);
            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(155.0f, 698.0f);
                    pdfContentByte2.showText(MFFirstVisit);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(405.0f, 698.0f);
                    pdfContentByte2.showText(MFReturnPat);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(182.0f, 677.0f);
                    pdfContentByte2.showText(MFInternetFind);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 630.0f);
                    pdfContentByte2.showText(Facebook);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 630.0f);
                    pdfContentByte2.showText(TV);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 618.0f);
                    pdfContentByte2.showText(MapSearch);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 618.0f);
                    pdfContentByte2.showText(Billboard);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 605.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 605.0f);
                    pdfContentByte2.showText(Radio);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 590.0f);
                    pdfContentByte2.showText(GoogleSearch);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 590.0f);
                    pdfContentByte2.showText(FamilyFriend);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(370.0f, 590.0f);
                    pdfContentByte2.showText(FamilyFriend_text);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 575.0f);
                    pdfContentByte2.showText(YouTube);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 575.0f);
                    pdfContentByte2.showText(Brochure);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 562.0f);
                    pdfContentByte2.showText(WebsiteAds);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 562.0f);
                    pdfContentByte2.showText(DirectMail);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 550.0f);
                    pdfContentByte2.showText(LinkedIn);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 550.0f);
                    pdfContentByte2.showText(UrgentCare);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(370.0f, 550.0f);
                    pdfContentByte2.showText(UrgentCare_text);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 535.0f);
                    pdfContentByte2.showText(Twitter);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 535.0f);
                    pdfContentByte2.showText(School);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(370.0f, 535.0f);
                    pdfContentByte2.showText(School_text);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 522.0f);
                    pdfContentByte2.showText(OnlineReviews);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 522.0f);
                    pdfContentByte2.showText(NewspaperMagazine);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 522.0f);
                    pdfContentByte2.showText(NewspaperMagazine_text);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(20.0f, 510.0f);
                    pdfContentByte2.showText(EmailBlast);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 510.0f);
                    pdfContentByte2.showText(Hotel);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(340.0f, 510.0f);
                    pdfContentByte2.showText(Hotel_text);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 495.0f);
                    pdfContentByte2.showText(CitizensDeTar);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 483.0f);
                    pdfContentByte2.showText(LiveWorkNearby);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 470.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 470.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(310.0f, 440.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 426.0f);
                    pdfContentByte2.showText(MFPhysician);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(290.0f, 426.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();
            ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/GeneralForm_Victoria.pdf";
            if (WorkersCompPolicyChk == 1) {
                Query = "Select WCPInjuryRelatedAutoMotorAccident, WCPInjuryOccurVehicle from " + Database + ".Patient_WorkCompPolicy where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    WCPInjuryRelatedAutoMotorAccident = rset.getString(1);
                    WCPInjuryOccurVehicle = rset.getString(2);
                }
                rset.close();
                stmt.close();
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/GeneralForm_Victoria.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";
                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/WC_QUESTIONNAIRE_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";
                if (WCPInjuryRelatedAutoMotorAccident.equals("1") || WCPInjuryOccurVehicle.equals("1")) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/WC_MVA_assignmentofproceeds_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";
                }
            }
            if (WorkersCompPolicyChk == 0 && MotorVehicleAccidentChk == 1) {
                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/MVA_ASSIGNMENTOFPROCEEDS_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";
            }
            if (MotorVehicleAccidentChk == 1) {
                Query = "Select AutoInsuranceInformationChk from " + Database + ".Patient_AutoInsuranceInfo where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    AutoInsuranceInformationChk = rset.getString(1);
                }
                rset.close();
                stmt.close();
                if (AutoInsuranceInformationChk.equals("1")) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/MVACLAIMFORM_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";
                }
            }
            if (HealthInsuranceChk == 1) {
                Query = "Select GFIPMedicare,GFIPMedicaid,GFIPCHIP,GFIPTricare,GFIPVHA,GFIPIndianHealth, GovtFundedInsurancePlanChk,IFNULL(HIPrimaryInsurance,''),IFNULL(HISubscriberFirstName,''), IFNULL(HISubscriberLastName,''), IFNULL(DATE_FORMAT(HISubscriberDOB,'%m/%d/%Y'),''), IFNULL(HISubscriberSSN,''), IFNULL(HISubscriberRelationtoPatient,''), IFNULL(HISubscriberGroupNo,''), IFNULL(HISubscriberPolicyNo,''),IFNULL(SHISecondaryName,''), IFNULL(SHISubscriberFirstName,''), IFNULL(SHISubscriberLastName,''), IFNULL(SHISubscriberRelationtoPatient,''), IFNULL(SHISubscriberGroupNo,''), IFNULL(SHISubscriberPolicyNo,'') from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    GFIPMedicare = rset.getInt(1);
                    GFIPMedicaid = rset.getInt(2);
                    GFIPCHIP = rset.getInt(3);
                    GFIPTricare = rset.getInt(4);
                    GFIPVHA = rset.getInt(5);
                    GFIPIndianHealth = rset.getInt(6);
                    GovtFundedInsurancePlanChk = rset.getString(7);
                    HIPrimaryInsurance = rset.getString(8);
                    HISubscriberFirstName = rset.getString(9);
                    HISubscriberLastName = rset.getString(10);
                    HISubscriberDOB = rset.getString(11);
                    HISubscriberSSN = rset.getString(12);
                    HISubscriberRelationtoPatient = rset.getString(13);
                    HISubscriberGroupNo = rset.getString(14);
                    HISubscriberPolicyNo = rset.getString(15);
                    SHISecondaryName = rset.getString(16);
                    SHISubscriberFirstName = rset.getString(17);
                    SHISubscriberLastName = rset.getString(18);
                    SHISubscriberRelationtoPatient = rset.getString(19);
                    SHISubscriberGroupNo = rset.getString(20);
                    SHISubscriberPolicyNo = rset.getString(21);
                }
                rset.close();
                stmt.close();
                if (GovtFundedInsurancePlanChk.equals("1")) {
                    if (GFIPMedicaid == 1 || GFIPCHIP == 1) {
                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/MEDICAIDSELFPAYAGREEMENT_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";
                    }
                    if (GFIPMedicare == 1) {
                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/ABNformEnglish_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";
                    }
                }
                if (HIPrimaryInsurance.trim().toUpperCase().equals("UNITED HEALTHCARE")) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/UHCINSAPPEALFORMS_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";
                }
            }
            if (HISubscriberDOB.equals("00/00/0000")) {
                HISubscriberDOB = "";
            }
            if (WCPDateofInjury.equals("00/00/0000")) {
                WCPDateofInjury = "";
            }
            if (AIIDateofAccident.equals("00/00/0000")) {
                AIIDateofAccident = "";
            }
            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/FINANCIAL_HARDSHIP_RELIEF_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
            ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";
            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/Medicalreleaseform_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
            ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";
            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/Marketing_Slips_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
            ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";
            String DOSDate = "";
            String DOSTime = "";
            DOSDate = DOS.substring(0, 10);
            DOSTime = DOS.substring(11, 19);
            String inputFilePath = "";
            inputFilePath = ResultPdf;
            final String filename = FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf";
            final OutputStream fos3 = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader3 = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper3 = new PdfStamper(pdfReader3, fos3);


            int pageCount = pdfReader3.getNumberOfPages();
            for (int k = 1; k <= pdfReader3.getNumberOfPages(); ++k) {
                if (k == 1) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(390.0f, 668.0f);
                    pdfContentByte3.showText(DOSDate);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(490.0f, 668.0f);
                    pdfContentByte3.showText(DOSTime);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 600.0f);
                    pdfContentByte3.showText(LastName);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(200.0f, 600.0f);
                    pdfContentByte3.showText(FirstName);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(390.0f, 608.0f);
                    pdfContentByte3.showText(Title);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(450.0f, 608.0f);
                    pdfContentByte3.showText(MaritalStatus);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 570.0f);
                    pdfContentByte3.showText(Address);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(300.0f, 570.0f);
                    pdfContentByte3.showText(CityStateZip);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(450.0f, 570.0f);
                    pdfContentByte3.showText(PhNumber);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 540.0f);
                    pdfContentByte3.showText(SSN);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 540.0f);
                    pdfContentByte3.showText(DOB);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(320.0f, 540.0f);
                    pdfContentByte3.showText(Age);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(380.0f, 540.0f);
                    pdfContentByte3.showText(gender);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(435.0f, 540.0f);
                    pdfContentByte3.showText(Email);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 515.0f);
                    pdfContentByte3.showText(Ethnicity);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 515.0f);
                    pdfContentByte3.showText(Employer);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 515.0f);
                    pdfContentByte3.showText(Occupation);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 490.0f);
                    pdfContentByte3.showText("-");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 490.0f);
                    pdfContentByte3.showText("-");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 490.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 470.0f);
                    pdfContentByte3.showText(PriCarePhy);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(320.0f, 465.0f);
                    pdfContentByte3.showText(ReasonVisit);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 395.0f);
                    pdfContentByte3.showText(Guarantor);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(290.0f, 395.0f);
                    pdfContentByte3.showText(GuarantorDOB);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(360.0f, 395.0f);
                    pdfContentByte3.showText(GuarantorSEX);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(425.0f, 395.0f);
                    pdfContentByte3.showText(GuarantorSSN);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 365.0f);
                    pdfContentByte3.showText(GuarantorAddress);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(320.0f, 365.0f);
                    pdfContentByte3.showText(GuarantorPhoneNumber);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 335.0f);
                    pdfContentByte3.showText(GuarantorEmployer);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 335.0f);
                    pdfContentByte3.showText("" + GuarantorEmployerPhNumber);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 335.0f);
                    pdfContentByte3.showText(GuarantorEmployerAddress + "  " + GuarantorEmployerCity + " " + GuarantorEmployerState);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 310.0f);
                    pdfContentByte3.showText(WorkCompPolicyStr);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 310.0f);
                    pdfContentByte3.showText(MotorVehAccidentStr);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 310.0f);
                    pdfContentByte3.showText(AIIDateofAccident);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(100.0f, 285.0f);
                    pdfContentByte3.showText(HIPrimaryInsurance);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 260.0f);
                    pdfContentByte3.showText(HISubscriberFirstName + " " + HISubscriberLastName);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(290.0f, 260.0f);
                    pdfContentByte3.showText(HISubscriberDOB);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 260.0f);
                    pdfContentByte3.showText(HISubscriberSSN);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 244.0f);
                    pdfContentByte3.showText(HISubscriberRelationtoPatient);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 235.0f);
                    pdfContentByte3.showText(HISubscriberGroupNo);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 235.0f);
                    pdfContentByte3.showText(HISubscriberPolicyNo);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 205.0f);
                    pdfContentByte3.showText(SHISecondaryName);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(50.0f, 180.0f);
                    pdfContentByte3.showText(SHISubscriberFirstName + " " + SHISubscriberLastName);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 180.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 180.0f);
                    pdfContentByte3.showText("-");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 170.0f);
                    pdfContentByte3.showText(SHISubscriberRelationtoPatient);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 160.0f);
                    pdfContentByte3.showText(SHISubscriberGroupNo);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 160.0f);
                    pdfContentByte3.showText(SHISubscriberPolicyNo);
                    pdfContentByte3.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(150, 80.0f);
                        pdfContentByte3.addImage(SignImages);
                    }

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(410.0f, 80.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 2) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(165, 145.0f);
                        pdfContentByte3.addImage(SignImages);
                    }

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(350.0f, 145.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(350.0f, 105.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 4) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(70.0f, 350.0f);
                    pdfContentByte3.showText(FirstName + " " + LastName + " " + MiddleInitial);
                    pdfContentByte3.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(70.0f, 270.0f);
                        pdfContentByte3.addImage(SignImages);
                    }

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(380.0f, 270.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 5) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(165.0f, 70.0f);
                        pdfContentByte3.addImage(SignImages);
                    }
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(420.0f, 70.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 6) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(430.0f, 290.0f);
                    pdfContentByte3.showText(FirstName + " " + LastName + " " + MiddleInitial);
                    pdfContentByte3.endText();


                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(120.0f, 360.0f);
                        pdfContentByte3.addImage(SignImages);
                    }
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(440.0f, 360.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 7) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(120.0f, 590.0f);
                    pdfContentByte3.showText(FirstName + " " + LastName + " " + MiddleInitial);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(300.0f, 590.0f);
                    pdfContentByte3.showText(DOB);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(485.0f, 590.0f);
                    pdfContentByte3.showText(HISubscriberRelationtoPatient);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 565.0f);
                    pdfContentByte3.showText(HIPrimaryInsurance);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 540.0f);
                    pdfContentByte3.showText(HISubscriberPolicyNo);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 520.0f);
                    pdfContentByte3.showText(DOS);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 420.0f);
                    pdfContentByte3.showText(FirstName + " " + LastName + " " + MiddleInitial);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(380.0f, 420.0f);
                    pdfContentByte3.showText("Victoria ED");
                    pdfContentByte3.endText();


                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100.0f, 160.0f);
                        pdfContentByte3.addImage(SignImages);
                    }
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(440.0f, 160.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 8) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(220.0f, 425.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(155.0f, 400.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(410.0f, 400.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(155.0f, 380.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 380.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(115.0f, 158.0f);
                    pdfContentByte3.showText("");
                    pdfContentByte3.endText();
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(140.0f, 130.0f);
                        pdfContentByte3.addImage(SignImages);
                    }
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 130.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400.0f, 100.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 9) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100.0f, 150.0f);
                        pdfContentByte3.addImage(SignImages);
                    }
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230.0f, 150.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
            }
            pdfStamper3.close();
            pdfStamper2.close();
            pdfStamper1.close();
            pdfReader1.close();
            pdfReader2.close();
            pdfReader3.close();


//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("outputFilePath", outputFilePath);
////            Parser.SetField("imagelist", String.valueOf(imagelist));
//            Parser.SetField("pageCount", String.valueOf(pageCount));
//            Parser.SetField("PatientRegId", String.valueOf(ID));
//            Parser.SetField("FileName", FirstNameNoSpaces + LastNameNoSpace + ID + "_.pdf");
//            Parser.SetField("ClientID", String.valueOf(ClientId));
//
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/DownloadBundleHTML.html");

//            final File pdfFile = new File(outputFilePath);
//            response.setContentType("application/pdf");
//            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf");
//            response.setContentLength((int) pdfFile.length());
//            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
//            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
//            int bytes;
//            while ((bytes = fileInputStream.read()) != -1) {
//                responseOutputStream.write(bytes);
//            }
            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/WC_QUESTIONNAIRE_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/WC_MVA_assignmentofproceeds_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/MVACLAIMFORM_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/MVA_ASSIGNMENTOFPROCEEDS_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/ABNformEnglish_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/Medicalreleaseform_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/UHCINSAPPEALFORMS_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/MEDICAIDSELFPAYAGREEMENT_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/FINANCIAL_HARDSHIP_RELIEF_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/AllyPdf/TempDir/Marketing_Slips_" + ClientId + "_" + MRN + ".pdf");
            File.delete();

            return pageCount + "~" + outputFilePath + "~" + filename;//FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf";

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

}