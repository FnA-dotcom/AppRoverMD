//
// Decompiled by Procyon v0.5.36
//

package md;

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

public class PatientReg2_Mobile21APRIL2021 extends HttpServlet {
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
        try {
            context = this.getServletContext();
            conn = Services.getMysqlConn(context);
            if (ActionID.equals("Victoria_2")) {
                this.Victoria_2(request, out, conn, context);
            } else if (ActionID.equals("Victoria_22")) {
                this.Victoria_22(request, out, conn, context);
            } else if (ActionID.equals("SaveDataVictoria")) {
                this.SaveDataVictoria(request, out, conn, context, response);
            } else if (ActionID.equals("EditValues")) {
                this.EditValues(request, out, conn, context, response);
            } else if (ActionID.equals("EditSave")) {
                this.EditSave(request, out, conn, context, response);
            } else {
                out.println("Under Development!!!");
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

    void SaveDataVictoria(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Database = "";
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
                DOB = String.valueOf(String.valueOf(DOB.substring(6, 10))) + "-" + DOB.substring(0, 2) + "-" + DOB.substring(3, 5);
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
            Query = "Select Date_format(now(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();
            try {
                Query = "Select Id, dbname from oe.clients where ltrim(rtrim(UPPER(name))) = ltrim(rtrim(UPPER('" + ClientId + "')))";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    ClientIndex = rset.getInt(1);
                    Database = rset.getString(2);
                }
                rset.close();
                stmt.close();
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
            } catch (Exception e) {
                System.out.println("Error in getting MRN and ClientIndex" + e.getMessage());
            }
            try {
                if (!Email.equals(ConfirmEmail)) {
                    out.println("<!DOCTYPE html><html><body><p style=\"color:black;\">Please Put Email and Confirm Email Correctly and then Submit</p>");
                    out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
                    return;
                }
                Email = ConfirmEmail;
                final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".PatientReg (ClientIndex,FirstName,LastName ," + " MiddleInitial,DOB,Age,Gender ,Email,PhNumber ,Address,City ,State,Country,ZipCode,SSN,Occupation ,Employer ,EmpContact," + " PriCarePhy,ReasonVisit,SelfPayChk,CreatedDate,Title, MaritalStatus,CreatedBy, MRN, Status, ExtendedMRN, County) \n" + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,0,?,?) ");
                MainReceipt.setInt(1, ClientIndex);
                MainReceipt.setString(2, FirstName);
                MainReceipt.setString(3, LastName);
                MainReceipt.setString(4, MiddleInitial);
                MainReceipt.setString(5, DOB);
                MainReceipt.setString(6, Age);
                MainReceipt.setString(7, gender);
                MainReceipt.setString(8, Email);
                MainReceipt.setString(9, String.valueOf(String.valueOf(AreaCode)) + PhNumber);
                MainReceipt.setString(10, String.valueOf(String.valueOf(Address)) + " " + Address2);
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
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                Services.DumException("PatientReg2", "SaveDataVictoriaError 2- Insertion PatientReg Table :", request, e, this.getServletContext());
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
                Services.DumException("PatientReg2", "SaveDataVictoriaError 3- :", request, e, this.getServletContext());
                return;
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".PatientVisit(MRN,PatientRegId,ReasonVisit,VisitNumber,DoctorId,DateofService,CreatedDate,CreatedBy) \nVALUES (?,?,?,1,NULL,now(),now(),?) ");
                MainReceipt.setInt(1, MRN);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, ReasonVisit);
                MainReceipt.setString(4, "Out Patient");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                Services.DumException("PatientReg2", "SaveDataVictoriaError 3.1 Insertion in table PatientVisit- :", request, e, this.getServletContext());
                return;
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".PatientReg_Details (PatientRegId,Ethnicity," + "Race,EmployementChk,Employer,Occupation,EmpContact,PrimaryCarePhysicianChk,PriCarePhy,ReasonVisit," + "PriCarePhyAddress,PriCarePhyCity,PriCarePhyState,PriCarePhyZipCode,PatientMinorChk,GuarantorChk,GuarantorEmployer," + "GuarantorEmployerPhNumber,GuarantorEmployerAddress,GuarantorEmployerCity,GuarantorEmployerState,GuarantorEmployerZipCode," + "CreatedDate,WorkersCompPolicyChk,MotorVehicleAccidentChk,HealthInsuranceChk) \n" + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?) ");
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
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                Services.DumException("PatientReg2", "SaveDataVictoriaError 4- Insertion PatientReg_Details Table :", request, e, this.getServletContext());
                return;
            }
            if (WorkersCompPolicyChk.equals("1")) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".Patient_WorkCompPolicy (PatientRegId,WCPDateofInjury," + "WCPCaseNo,WCPGroupNo,WCPMemberId,WCPInjuryRelatedAutoMotorAccident,WCPInjuryRelatedWorkRelated,WCPInjuryRelatedOtherAccident," + "WCPInjuryRelatedNoAccident,WCPInjuryOccurVehicle,WCPInjuryOccurWork,WCPInjuryOccurHome,WCPInjuryOccurOther,WCPInjuryDescription," + "WCPHRFirstName,WCPHRLastName,WCPHRPhoneNumber,WCPHRAddress,WCPHRCity,WCPHRState,WCPHRZipCode,WCPPlanName,WCPCarrierName," + "WCPPayerPhoneNumber,WCPCarrierAddress,WCPCarrierCity,WCPCarrierState,WCPCarrierZipCode,WCPAdjudicatorFirstName,WCPAdjudicatorLastName," + "WCPAdjudicatorPhoneNumber,WCPAdjudicatorFaxPhoneNumber,CreatedDate) \n" + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
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
                    Services.DumException("PatientReg2", "SaveDataVictoriaError 7- Insertion Patient_HealthInsuranceInfo Table", request, e, this.getServletContext());
                    return;
                }
            }
            String PatientName = null;
            Query = "Select CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName) from " + Database + ".PatientReg where ID = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientName = rset.getString(1);
            }
            rset.close();
            stmt.close();
            String Message = "";
            String InsertCOVIDRegReply = "0";
            System.out.println(String.valueOf(String.valueOf(ReasonVisit)) + " Before");
            if (ClientIndex == 9) {
                if (ReasonVisit != null) {
                    ReasonVisit = ReasonVisit.replaceAll(" ", "");
                    System.out.println(String.valueOf(String.valueOf(ReasonVisit)) + " After");
                    if (ReasonVisit.toUpperCase().equals("COVIDTESTING")) {
                        InsertCOVIDRegReply = this.InsertCOVIDReg(request, response, out, conn, String.valueOf(PatientRegId));
                        if (Integer.parseInt(InsertCOVIDRegReply) > 0) {
                            Message = "COVID Form Also Registered Successfully.";
                        } else {
                            Message = "COVID Form Not Registered. ";
                        }
                    }
                }
            }

            final StringBuffer ProfessionalPayersList = new StringBuffer();
            Query = "Select Date_format(now(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
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


            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Date", String.valueOf(Date));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.SetField("myVal", "1");
            if (ClientIndex == 9) {
                Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(this.getServletContext()))) + "Forms/PatientRegFormVictoria_2.html");
            } else if (ClientIndex == 28) {
                Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(this.getServletContext()))) + "Forms/PatientRegFormERDallas.html");
            }
            //Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(this.getServletContext()))) + "Forms/"+PRFName+"_Mobile.html");
//            final Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " We Have Registered You Successfully. Please Wait for Further Processing. " + Message + " <br>DATED: " + Date);
//            Parser.SetField("FormName", String.valueOf("PatientReg2"));
//            Parser.SetField("ActionID", String.valueOf("Victoria_2&ClientId=Victoria"));
//            Parser.SetField("ClientId", String.valueOf(ClientId));
//            Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/orange_2/Exception/Message.html");
        } catch (Exception e2) {
            Services.DumException("PatientReg2", "SaveDataVictoria", request, e2, this.getServletContext());
            out.println("Error Found. Kindly Fill Required Fields !!!");
            out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
        }
    }

    void EditValues(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Database = "";
            final String MRN = request.getParameter("MRN").trim();
            final int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
            final String ExtendedMRN = "0";
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
            final String ConfirmEmail = "";
            String MaritalStatus = "";
            String AreaCode = "";
            String PhNumber = "";
            String Address = "";
            final String Address2 = "";
            String City = "";
            String State = "";
            String Country = "";
            String County = "";
            String ZipCode = "";
            String Ethnicity = "";
            String Race = "";
            String SSN = "";
            String DoctorId = "";
            String DOS = "";
            String EmployementChk = "0";
            String Employer = "";
            String Occupation = "";
            String EmpContact = "";
            String PrimaryCarePhysicianChk = "0";
            String PriCarePhy = "";
            String ReasonVisit = "";
            String PriCarePhyAddress = "";
            final String PriCarePhyAddress2 = "";
            String PriCarePhyCity = "";
            String PriCarePhyState = "";
            String PriCarePhyZipCode = "";
            String PatientMinorChk = "0";
            String GuarantorChk = "0";
            String GuarantorEmployer = "";
            String GuarantorEmployerAreaCode = "";
            String GuarantorEmployerPhNumber = "";
            String GuarantorEmployerAddress = "";
            final String GuarantorEmployerAddress2 = "";
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
            final String WCPHRAddress2 = "";
            String WCPHRCity = "";
            String WCPHRState = "";
            String WCPHRZipCode = "";
            String WCPPlanName = "";
            String WCPCarrierName = "";
            String WCPPayerAreaCode = "";
            String WCPPayerPhoneNumber = "";
            String WCPCarrierAddress = "";
            final String WCPCarrierAddress2 = "";
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
            String AIICarrierResponsiblePartyAreaCode = "";
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
            final String SHISubscriberDOB = "";
            String SHISubscriberRelationtoPatient = "";
            String SHISubscriberGroupNo = "";
            String SHISubscriberPolicyNo = "";
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
            final StringBuffer HIPrimaryInsuranceBuff = new StringBuffer();
            String Style = "";
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
                Query = " Select IFNULL(Title,''), IFNULL(FirstName,''), IFNULL(LastName,''), IFNULL(MiddleInitial, ''), IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'),''),  IFNULL(Age,''), IFNULL(Gender, ''), IFNULL(Email,''), IFNULL(SUBSTRING(PhNumber, 1, 3),''),  IFNULL(SUBSTRING(PhNumber, 4, 10),''), IFNULL(Address,''), IFNULL(City,''), IFNULL(State,''), IFNULL(Country,''), IFNULL(ZipCode,''), IFNULL(SSN,''), IFNULL(Occupation,''), IFNULL(Employer,''), IFNULL(EmpContact, ''), IFNULL(PriCarePhy,''), IFNULL(ReasonVisit, ''), IFNULL(MaritalStatus, ''), IFNULL(DoctorsName,''),  IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y'),DATE_FORMAT(CreatedDate,'%m/%d/%Y')),IFNULL(County,''), ID from " + Database + ".PatientReg where MRN = '" + MRN + "'";
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
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error in Getting Data from PatientReg Table: " + e.getMessage());
            }
            try {
                Query = " Select IFNULL(Ethnicity,''), IFNULL(EmployementChk,'0'), IFNULL(Employer,''), IFNULL(Occupation, ''), IFNULL(EmpContact,''),  IFNULL(PrimaryCarePhysicianChk,'0'), IFNULL(PriCarePhy, ''), IFNULL(ReasonVisit,''), IFNULL(PriCarePhyAddress,''),  IFNULL(PriCarePhyCity,''),  IFNULL(PriCarePhyState,''), IFNULL(PriCarePhyZipCode,''), IFNULL(PatientMinorChk,'0'), IFNULL(GuarantorChk,'0'), IFNULL(GuarantorEmployer,''),  IFNULL(SUBSTRING(GuarantorEmployerPhNumber, 1, 3),''), IFNULL(GuarantorEmployerAddress,''), IFNULL(GuarantorEmployerCity,''), IFNULL(GuarantorEmployerState,''),  IFNULL(GuarantorEmployerZipCode, ''), IFNULL(WorkersCompPolicyChk,'0'), IFNULL(MotorVehicleAccidentChk, '0'), IFNULL(HealthInsuranceChk, '0'),  IFNULL(GuarantorEmployerPhNumber,''), IFNULL(Race,'')  from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
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
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error in Getting Data from PatientReg_Details Table: " + e.getMessage());
            }
            try {
                if (WorkersCompPolicyChk.equals("1")) {
                    Query = " Select IFNULL(DATE_FORMAT(WCPDateofInjury,'%m/%d/%Y'),''), IFNULL(WCPCaseNo,''), IFNULL(WCPGroupNo,''), IFNULL(WCPMemberId,''),  IFNULL(WCPInjuryRelatedAutoMotorAccident,''), IFNULL(WCPInjuryRelatedWorkRelated,''), IFNULL(WCPInjuryRelatedOtherAccident,''),  IFNULL(WCPInjuryRelatedNoAccident,''), IFNULL(WCPInjuryOccurVehicle,''), IFNULL(WCPInjuryOccurWork,''), IFNULL(WCPInjuryOccurHome,''),  IFNULL(WCPInjuryOccurOther,''), IFNULL(WCPInjuryDescription,''), IFNULL(WCPHRFirstName,''), IFNULL(WCPHRLastName,''),  IFNULL(SUBSTRING(WCPHRPhoneNumber, 1, 3),''), IFNULL(SUBSTRING(WCPHRPhoneNumber, 4, 10),''), IFNULL(WCPHRAddress,''), IFNULL(WCPHRCity,''),  IFNULL(WCPHRState,''), IFNULL(WCPHRZipCode,''), IFNULL(WCPPlanName,''), IFNULL(WCPCarrierName,''),  IFNULL(SUBSTRING(WCPPayerPhoneNumber, 1, 3),''), IFNULL(SUBSTRING(WCPPayerPhoneNumber, 4, 10),''), IFNULL(WCPCarrierAddress,''),  IFNULL(WCPCarrierCity,''), IFNULL(WCPCarrierState,''), IFNULL(WCPCarrierZipCode,''), IFNULL(WCPAdjudicatorFirstName,''),  IFNULL(WCPAdjudicatorLastName,''), IFNULL(SUBSTRING(WCPAdjudicatorPhoneNumber, 1, 3),''), IFNULL(SUBSTRING(WCPAdjudicatorPhoneNumber, 4, 10),''),  IFNULL(SUBSTRING(WCPAdjudicatorFaxPhoneNumber, 1, 3),''), IFNULL(SUBSTRING(WCPAdjudicatorFaxPhoneNumber, 4, 10),'')  from " + Database + ".Patient_WorkCompPolicy where PatientRegId = " + PatientRegId + " ";
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
            } catch (Exception e) {
                System.out.println("Error in Getting Data from Patient_WorkCompPolicy Table: " + e.getMessage());
            }
            try {
                if (MotorVehicleAccidentChk.equals("1")) {
                    Query = " SELECT IFNULL(AutoInsuranceInformationChk,''), IFNULL(DATE_FORMAT(AIIDateofAccident,'%m/%d/%Y'), ''), IFNULL(AIIAutoClaim,''),  IFNULL(AIIAccidentLocationAddress, ''), IFNULL(AIIAccidentLocationCity, ''), IFNULL(AIIAccidentLocationState,''), IFNULL(AIIAccidentLocationZipCode,''),  IFNULL(AIIRoleInAccident, ''), IFNULL(AIITypeOfAutoIOnsurancePolicy,''), IFNULL(AIIPrefixforReponsibleParty, ''), IFNULL(AIIFirstNameforReponsibleParty, ''),  IFNULL(AIIMiddleNameforReponsibleParty, ''), IFNULL(AIILastNameforReponsibleParty, ''), IFNULL(AIISuffixforReponsibleParty,''), IFNULL(AIICarrierResponsibleParty,''),  IFNULL(AIICarrierResponsiblePartyAddress,''), IFNULL(AIICarrierResponsiblePartyCity,''), IFNULL(AIICarrierResponsiblePartyState,''), IFNULL(AIICarrierResponsiblePartyZipCode,''),  IFNULL(SUBSTRING(AIICarrierResponsiblePartyPhoneNumber, 1, 3),''),IFNULL(SUBSTRING(AIICarrierResponsiblePartyPhoneNumber, 4, 10),''), IFNULL(AIICarrierResponsiblePartyPolicyNumber,''),  IFNULL(AIIResponsiblePartyAutoMakeModel, ''), IFNULL(AIIResponsiblePartyLicensePlate,''), IFNULL(AIIFirstNameOfYourPolicyHolder,''), IFNULL(AIILastNameOfYourPolicyHolder, ''),  IFNULL(AIINameAutoInsuranceOfYourVehicle,''), IFNULL(AIIYourInsuranceAddress, ''), IFNULL(AIIYourInsuranceCity,''), IFNULL(AIIYourInsuranceState,''),  IFNULL(AIIYourInsuranceZipCode, ''), IFNULL(SUBSTRING(AIIYourInsurancePhoneNumber, 1, 3),''), IFNULL(SUBSTRING(AIIYourInsurancePhoneNumber, 4, 10),''),  IFNULL(AIIYourInsurancePolicyNo,''), IFNULL(AIIYourLicensePlate,''), IFNULL(AIIYourCarMakeModelYear,'')  from " + Database + ".Patient_AutoInsuranceInfo where PatientRegId = " + PatientRegId;
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
            } catch (Exception e) {
                System.out.println("Error in Getting Data from Patient_AutoInsuranceInfo Table: " + e.getMessage());
            }
            try {
                if (HealthInsuranceChk.equals("1")) {
                    Query = "Select IFNULL(GovtFundedInsurancePlanChk,0), IFNULL(GFIPMedicare,0), IFNULL(GFIPMedicaid,0), IFNULL(GFIPCHIP,0), IFNULL(GFIPTricare,0), IFNULL(GFIPVHA,0), IFNULL(GFIPIndianHealth,0), IFNULL(InsuranceSubPatient,''), IFNULL(InsuranceSubGuarantor,''), IFNULL(InsuranceSubOther,''), IFNULL(HIPrimaryInsurance, ''), IFNULL(HISubscriberFirstName,''), IFNULL(HISubscriberLastName,''), IFNULL(DATE_FORMAT(HISubscriberDOB,'%m/%d/%Y'),''), IFNULL(HISubscriberSSN,''), IFNULL(HISubscriberRelationtoPatient,''), IFNULL(HISubscriberGroupNo,''), IFNULL(HISubscriberPolicyNo,''), IFNULL(SecondHealthInsuranceChk,'0'), IFNULL(SHISecondaryName,''), IFNULL(SHISubscriberFirstName,''), IFNULL(SHISubscriberLastName,''), IFNULL(SHISubscriberRelationtoPatient,''), IFNULL(SHISubscriberGroupNo,''), IFNULL(SHISubscriberPolicyNo,'')  from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId =  " + PatientRegId;
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
            } catch (Exception e) {
                System.out.println("Error in Getting Data from Patient_HealthInsuranceInfo Table: " + e.getMessage());
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
            if (Title.equals("Mr")) {
                TitleBuffer.append("<select class=\"form-control\" id=\"Title\" name=\"Title\" style=\"color:black;\" required>\n<option value=\"\">Select Title</option>\n<option value=\"Mr\" selected >Mr.</option>\n<option value=\"Miss\">Miss</option>\n<option value=\"Mrs\">Mrs</option>\n<option value=\"Ms\">Ms</option>\n</select>");
            } else if (Title.equals("Miss")) {
                TitleBuffer.append("<select class=\"form-control\" id=\"Title\" name=\"Title\" style=\"color:black;\" required>\n<option value=\"\">Select Title</option>\n<option value=\"Mr\">Mr.</option>\n<option value=\"Miss\" selected >Miss</option>\n<option value=\"Mrs\">Mrs</option>\n<option value=\"Ms\">Ms</option>\n</select>");
            } else if (Title.equals("Mrs")) {
                TitleBuffer.append("<select class=\"form-control\" id=\"Title\" name=\"Title\" style=\"color:black;\" required>\n<option value=\"\">Select Title</option>\n<option value=\"Mr\">Mr.</option>\n<option value=\"Miss\">Miss</option>\n<option value=\"Mrs\" selected >Mrs</option>\n<option value=\"Ms\">Ms</option>\n</select>");
            } else if (Title.equals("Ms")) {
                TitleBuffer.append("<select class=\"form-control\" id=\"Title\" name=\"Title\" style=\"color:black;\" required>\n<option value=\"\">Select Title</option>\n<option value=\"Mr\">Mr.</option>\n<option value=\"Miss\">Miss</option>\n<option value=\"Mrs\">Mrs</option>\n<option value=\"Ms\" selected >Ms</option>\n</select>");
            } else {
                TitleBuffer.append("<select class=\"form-control\" id=\"Title\" name=\"Title\" style=\"color:black;\" required>\n<option value=\"\">Select Title</option>\n<option value=\"Mr\">Mr.</option>\n<option value=\"Miss\">Miss</option>\n<option value=\"Mrs\">Mrs</option>\n<option value=\"Ms\">Ms</option>\n</select>");
            }
            if (gender.equals("male")) {
                GenderBuffer.append("<select class=\"form-control\" id=\"gender\" name=\"gender\" required style=\"color:black;\" required >\n<option value=\"\">Select Gender</option>\n<option value=\"male\" selected>Male</option>\n<option value=\"female\">Female</option>\n</select>");
            } else if (gender.equals("female")) {
                GenderBuffer.append("<select class=\"form-control\" id=\"gender\" name=\"gender\" required style=\"color:black;\" required >\n<option value=\"\">Select Gender</option>\n<option value=\"male\" >Male</option>\n<option value=\"female\" selected>Female</option>\n</select>");
            } else {
                GenderBuffer.append("<select class=\"form-control\" id=\"gender\" name=\"gender\" required style=\"color:black;\" required >\n<option value=\"\">Select Gender</option>\n<option value=\"male\" >Male</option>\n<option value=\"female\" >Female</option>\n</select>");
            }
            if (MaritalStatus.equals("Single")) {
                MaritalStatusBuffer.append("<select class=\"form-control\" id=\"MaritalStatus\" name=\"MaritalStatus\" style=\"color:black;\">\n<option value=\"Single\" selected>Single</option>\n<option value=\"Mar\">Mar</option>\n<option value=\"Div\">Div</option>\n<option value=\"Sep\">Sep</option>\n<option value=\"Wid\">Wid</option>\n</select>");
            } else if (MaritalStatus.equals("Mar")) {
                MaritalStatusBuffer.append("<select class=\"form-control\" id=\"MaritalStatus\" name=\"MaritalStatus\" style=\"color:black;\">\n<option value=\"Single\">Single</option>\n<option value=\"Mar\" selected>Mar</option>\n<option value=\"Div\">Div</option>\n<option value=\"Sep\">Sep</option>\n<option value=\"Wid\">Wid</option>\n</select>");
            } else if (MaritalStatus.equals("Div")) {
                MaritalStatusBuffer.append("<select class=\"form-control\" id=\"MaritalStatus\" name=\"MaritalStatus\" style=\"color:black;\">\n<option value=\"Single\">Single</option>\n<option value=\"Mar\">Mar</option>\n<option value=\"Div\" selected>Div</option>\n<option value=\"Sep\">Sep</option>\n<option value=\"Wid\">Wid</option>\n</select>");
            } else if (MaritalStatus.equals("Sep")) {
                MaritalStatusBuffer.append("<select class=\"form-control\" id=\"MaritalStatus\" name=\"MaritalStatus\" style=\"color:black;\">\n<option value=\"Single\">Single</option>\n<option value=\"Mar\">Mar</option>\n<option value=\"Div\" >Div</option>\n<option value=\"Sep\" selected>Sep</option>\n<option value=\"Wid\">Wid</option>\n</select>");
            } else if (MaritalStatus.equals("Wid")) {
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
            } else {
                ReasonVisitBuffer.append("<select class=\"form-control\" id=\"ReasonVisitSelect\" name=\"ReasonVisitSelect\" style=\"color:black;\" onchange=\"DisplayReasonVisitField(this.value);\" required>\n<option value=\"\">Select Reason of Visit</option>\n<option value=\"COVID\" selected>COVID Testing</option>\n<option value=\"Others\" >Others</option>\n</select>");
            }
            if (EmployementChk.equals("1")) {
                EmploymentBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"EmployementChk\" type=\"radio\" id=\"EmployementChk_Yes\" value=\"1\" onLoad=\"EmployementDivShow(this.value)\" checked >\n<label for=\"EmployementChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"EmployementChk\" type=\"radio\" id=\"EmployementChk_No\" value=\"0\" onLoad=\"EmployementDivShow(this.value)\" >\n<label for=\"EmployementChk_No\">No</label>\n</fieldset>\n</div>\n");
                Style = " #EmployementDiv{ display : block; } ";
            } else if (EmployementChk.equals("0")) {
                EmploymentBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"EmployementChk\" type=\"radio\" id=\"EmployementChk_Yes\" value=\"1\" onLoad=\"EmployementDivShow(this.value)\"  >\n<label for=\"EmployementChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"EmployementChk\" type=\"radio\" id=\"EmployementChk_No\" value=\"0\" onLoad=\"EmployementDivShow(this.value)\" checked>\n<label for=\"EmployementChk_No\">No</label>\n</fieldset>\n</div>\n");
                Style = " #EmployementDiv{ display : none; } ";
            } else {
                EmploymentBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"EmployementChk\" type=\"radio\" id=\"EmployementChk_Yes\" value=\"1\" onLoad=\"EmployementDivShow(this.value)\"  >\n<label for=\"EmployementChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"EmployementChk\" type=\"radio\" id=\"EmployementChk_No\" value=\"0\" onLoad=\"EmployementDivShow(this.value)\" >\n<label for=\"EmployementChk_No\">No</label>\n</fieldset>\n</div>\n");
                Style = " #EmployementDiv{ display : none; } ";
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
                PatinetMinorBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"PatientMinorChk\" type=\"radio\" id=\"PatientMinorChk_Yes\" value=\"1\" checked required />\n<label for=\"PatientMinorChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"PatientMinorChk\" type=\"radio\" id=\"PatientMinorChk_No\" value=\"0\" />\n<label for=\"PatientMinorChk_No\">No</label>\n</fieldset>\n</div>\n");
            } else if (PatientMinorChk.equals("0")) {
                PatinetMinorBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"PatientMinorChk\" type=\"radio\" id=\"PatientMinorChk_Yes\" value=\"1\"  required />\n<label for=\"PatientMinorChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"PatientMinorChk\" type=\"radio\" id=\"PatientMinorChk_No\" value=\"0\" checked />\n<label for=\"PatientMinorChk_No\">No</label>\n</fieldset>\n</div>\n");
            } else {
                PatinetMinorBuffer.append("<div class=\"controls\">\n<fieldset>\n<input name=\"PatientMinorChk\" type=\"radio\" id=\"PatientMinorChk_Yes\" value=\"1\"  required />\n<label for=\"PatientMinorChk_Yes\">Yes</label>\n</fieldset>\n<fieldset>\n<input name=\"PatientMinorChk\" type=\"radio\" id=\"PatientMinorChk_No\" value=\"0\" />\n<label for=\"PatientMinorChk_No\">No</label>\n</fieldset>\n</div>\n");
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
            if (AIIRoleInAccident.equals("Driver")) {
                AIIRoleInAccidentBuffer.append("<select class=\"form-control\" id=\"AIIRoleInAccident\" name=\"AIIRoleInAccident\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Driver\" selected>Driver</option>\n<option value=\"Passenger\">Passenger</option>\n</select>\n");
            } else if (AIIRoleInAccident.equals("Passenger")) {
                AIIRoleInAccidentBuffer.append("<select class=\"form-control\" id=\"AIIRoleInAccident\" name=\"AIIRoleInAccident\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Driver\" >Driver</option>\n<option value=\"Passenger\" selected>Passenger</option>\n</select>\n");
            } else {
                AIIRoleInAccidentBuffer.append("<select class=\"form-control\" id=\"AIIRoleInAccident\" name=\"AIIRoleInAccident\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Driver\" >Driver</option>\n<option value=\"Passenger\">Passenger</option>\n</select>\n");
            }
            if (AIITypeOfAutoIOnsurancePolicy.equals("PassengerVehicle")) {
                AIITypeOfAutoIOnsurancePolicyBuffer.append("<select class=\"form-control\" id=\"AIITypeOfAutoIOnsurancePolicy\" name=\"AIITypeOfAutoIOnsurancePolicy\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"PassengerVehicle\" selected>Passenger Vehicle</option>\n<option value=\"MotorCycle\">MotorCycle</option>\n</select>\n");
            } else if (AIITypeOfAutoIOnsurancePolicy.equals("MotorCycle")) {
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
            if (HISubscriberRelationtoPatient.equals("Self")) {
                HISubscriberRelationtoPatientBuffer.append("<select class=\"form-control\" id=\"HISubscriberRelationtoPatient\" name=\"HISubscriberRelationtoPatient\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Self\" selected>Self</option>\n<option value=\"Parent\" >Parent</option>\n</select>");
            } else if (HISubscriberRelationtoPatient.equals("Parent")) {
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
            if (SHISecondaryName.equals("Aetna")) {
                SHISecondaryNameBuffer.append("<select class=\"form-control\" id=\"SHISecondaryName\" name=\"SHISecondaryName\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Aetna\" selected>Aetna</option>\n<option value=\"BCBS\">BCBS</option>\n</select>");
            } else if (SHISecondaryName.equals("BCBS")) {
                SHISecondaryNameBuffer.append("<select class=\"form-control\" id=\"SHISecondaryName\" name=\"SHISecondaryName\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Aetna\" >Aetna</option>\n<option value=\"BCBS\"selected >BCBS</option>\n</select>");
            } else {
                SHISecondaryNameBuffer.append("<select class=\"form-control\" id=\"SHISecondaryName\" name=\"SHISecondaryName\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Aetna\" >Aetna</option>\n<option value=\"BCBS\">BCBS</option>\n</select>");
            }
            if (SHISubscriberRelationtoPatient.equals("Self")) {
                SHISubscriberRelationtoPatientBuffer.append("<select class=\"form-control\" id=\"SHISubscriberRelationtoPatient\" name=\"SHISubscriberRelationtoPatient\" style=\"color:black;\">\n<option value=\"\">Select One</option>\n<option value=\"Self\" selected>Self</option>\n<option value=\"Parent\">Parent</option>\n</select>");
            } else if (SHISubscriberRelationtoPatient.equals("Parent")) {
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
            Parser.SetField("MRN", String.valueOf(MRN));
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.SetField("Style", String.valueOf(Style));
            Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/md/Forms/Edit/PatientRegFormVictoria_2_Edit.html");
        } catch (Exception e2) {
            Services.DumException("PatientReg2", "EditValues", request, e2, this.getServletContext());
        }
    }

    void EditSave(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Database = "";
            final String MRN = request.getParameter("MRN").trim();
            final int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
            final String ExtendedMRN = "0";
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
            String PrimaryCarePhysicianChk = "0";
            String PriCarePhy = "";
            String ReasonVisit = "";
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
                    DOB = String.valueOf(String.valueOf(DOB.substring(6, 10))) + "-" + DOB.substring(0, 2) + "-" + DOB.substring(3, 5);
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
            } catch (Exception e) {
                Services.DumException("PatientReg2", "EditValueError in request getParameter", request, e, this.getServletContext());
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
                Query = "Update " + Database + ".PatientReg Set FirstName = '" + FirstName + "', LastName = '" + LastName + "', MiddleInitial = '" + MiddleInitial + "', DOB = '" + DOB + "', Age = " + Age + ", Gender = '" + gender + "', Email = '" + Email + "', PhNumber = '" + AreaCode + PhNumber + "', Address = '" + Address + Address2 + "', City = '" + City + "', " + "State = '" + State + "', Country = '" + Country + "',ZipCode = '" + ZipCode + "',SSN = '" + SSN + "',Occupation = '" + Occupation + "',Employer = '" + Employer + "', " + "EmpContact = '" + EmpContact + "',PriCarePhy = '" + PriCarePhy + "',ReasonVisit = '" + ReasonVisit + "' ,Title = '" + Title + "',MaritalStatus = '" + MaritalStatus + "', " + "DoctorsName = '" + "0" + "' ,County = '" + County + "' where ID = " + PatientRegId;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                Services.DumException("PatientReg2", "EditValueError in updateing Patinet Reg INformation :", request, e, this.getServletContext());
            }
            try {
                Query = "Update " + Database + ".PatientReg_Details Set Ethnicity = '" + Ethnicity + "', Race = '" + Race + "', EmployementChk = '" + EmployementChk + "', Employer = '" + Employer + "', " + "Occupation = '" + Occupation + "', EmpContact = '" + EmpContact + "', PrimaryCarePhysicianChk = '" + PrimaryCarePhysicianChk + "', PriCarePhy = '" + PriCarePhy + "', ReasonVisit = '" + ReasonVisit + "', PriCarePhyAddress = '" + PriCarePhyAddress + PriCarePhyAddress2 + "', " + "PriCarePhyCity = '" + PriCarePhyCity + "', PriCarePhyState = '" + PriCarePhyState + "',PriCarePhyZipCode = '" + PriCarePhyZipCode + "',PatientMinorChk = '" + PatientMinorChk + "',GuarantorChk = '" + GuarantorChk + "',GuarantorEmployer = '" + GuarantorEmployer + "', " + "GuarantorEmployerPhNumber = '" + GuarantorEmployerAreaCode + GuarantorEmployerPhNumber + "',GuarantorEmployerAddress = '" + GuarantorEmployerAddress + GuarantorEmployerAddress2 + "',GuarantorEmployerCity = '" + GuarantorEmployerCity + "' ,GuarantorEmployerState = '" + GuarantorEmployerState + "',GuarantorEmployerZipCode = '" + GuarantorEmployerZipCode + "', " + "WorkersCompPolicyChk = '" + WorkersCompPolicyChk + "' ,MotorVehicleAccidentChk = '" + MotorVehicleAccidentChk + "', HealthInsuranceChk = '" + HealthInsuranceChk + "' where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                Services.DumException("PatientReg2", "EditValueError in updateing Patinet Reg Details tables:", request, e, this.getServletContext());
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
                    Services.DumException("PatientReg2", "EditValueError in Getting Count from  Patient_WorkCompPolicy tables: ", request, e, this.getServletContext());
                }
                try {
                    if (WorkCompFound > 0) {
                        Query = "Update " + Database + ".Patient_WorkCompPolicy Set WCPDateofInjury = '" + WCPDateofInjury + "', WCPCaseNo = '" + WCPCaseNo + "', WCPGroupNo = '" + WCPGroupNo + "', WCPMemberId = '" + WCPMemberId + "', " + "WCPInjuryRelatedAutoMotorAccident = '" + WCPInjuryRelatedAutoMotorAccident + "', WCPInjuryRelatedWorkRelated = '" + WCPInjuryRelatedWorkRelated + "', WCPInjuryRelatedOtherAccident = '" + WCPInjuryRelatedOtherAccident + "', WCPInjuryRelatedNoAccident = '" + WCPInjuryRelatedNoAccident + "', WCPInjuryOccurVehicle = '" + WCPInjuryOccurVehicle + "', WCPInjuryOccurWork = '" + WCPInjuryOccurWork + "', " + "WCPInjuryOccurHome = '" + WCPInjuryOccurHome + "', WCPInjuryOccurOther = '" + WCPInjuryOccurOther + "',WCPInjuryDescription = '" + WCPInjuryDescription + "',WCPHRFirstName = '" + WCPHRFirstName + "',WCPHRLastName = '" + WCPHRLastName + "',WCPHRPhoneNumber = '" + WCPHRAreaCode + WCPHRPhoneNumber + "', " + "WCPHRAddress = '" + WCPHRAddress + WCPHRAddress2 + "',WCPHRCity = '" + WCPHRCity + "',WCPHRState = '" + WCPHRState + "' ,WCPHRZipCode = '" + WCPHRZipCode + "',WCPPlanName = '" + WCPPlanName + "', " + "WCPCarrierName = '" + WCPCarrierName + "' ,WCPPayerPhoneNumber = '" + WCPPayerAreaCode + WCPPayerPhoneNumber + "', WCPCarrierAddress = '" + WCPCarrierAddress + WCPCarrierAddress2 + "', WCPCarrierCity= '" + WCPCarrierCity + "', WCPCarrierState = '" + WCPCarrierState + "', WCPCarrierZipCode= '" + WCPCarrierZipCode + "', WCPAdjudicatorFirstName='" + WCPAdjudicatorFirstName + "', WCPAdjudicatorLastName= '" + WCPAdjudicatorLastName + "', WCPAdjudicatorPhoneNumber = '" + WCPAdjudicatorAreaCode + WCPAdjudicatorPhoneNumber + "', WCPAdjudicatorFaxPhoneNumber='" + WCPAdjudicatorFaxAreaCode + WCPAdjudicatorFaxPhoneNumber + "' where PatientRegId = " + PatientRegId;
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();
                    } else {
                        try {
                            final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".Patient_WorkCompPolicy (PatientRegId,WCPDateofInjury," + "WCPCaseNo,WCPGroupNo,WCPMemberId,WCPInjuryRelatedAutoMotorAccident,WCPInjuryRelatedWorkRelated,WCPInjuryRelatedOtherAccident," + "WCPInjuryRelatedNoAccident,WCPInjuryOccurVehicle,WCPInjuryOccurWork,WCPInjuryOccurHome,WCPInjuryOccurOther,WCPInjuryDescription," + "WCPHRFirstName,WCPHRLastName,WCPHRPhoneNumber,WCPHRAddress,WCPHRCity,WCPHRState,WCPHRZipCode,WCPPlanName,WCPCarrierName," + "WCPPayerPhoneNumber,WCPCarrierAddress,WCPCarrierCity,WCPCarrierState,WCPCarrierZipCode,WCPAdjudicatorFirstName,WCPAdjudicatorLastName," + "WCPAdjudicatorPhoneNumber,WCPAdjudicatorFaxPhoneNumber,CreatedDate) \n" + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
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
                            MainReceipt.setString(17, WCPHRPhoneNumber);
                            MainReceipt.setString(18, String.valueOf(String.valueOf(WCPHRAddress)) + " " + WCPHRAddress2);
                            MainReceipt.setString(19, WCPHRCity);
                            MainReceipt.setString(20, WCPHRState);
                            MainReceipt.setString(21, WCPHRZipCode);
                            MainReceipt.setString(22, WCPPlanName);
                            MainReceipt.setString(23, WCPCarrierName);
                            MainReceipt.setString(24, WCPPayerPhoneNumber);
                            MainReceipt.setString(25, String.valueOf(String.valueOf(WCPCarrierAddress)) + " " + WCPCarrierAddress2);
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
                            Services.DumException("PatientReg2", "SaveDataVictoriaError 5- Insertion Patient_WorkCompPolicy Table :", request, e, this.getServletContext());
                            return;
                        }
                    }
                } catch (Exception e) {
                    Services.DumException("PatientReg2", "EditValueError in updateing/Inserting Patinet Reg WOrkCompPOlicy tables: ", request, e, this.getServletContext());
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
                    Services.DumException("PatientReg2", "EditValueError in Getting Count from  Patient_AutoInsuranceInfo tables: ", request, e, this.getServletContext());
                }
                try {
                    if (MotorVehFound > 0) {
                        Query = "Update " + Database + ".Patient_AutoInsuranceInfo Set AutoInsuranceInformationChk = '" + AutoInsuranceInformationChk + "', AIIDateofAccident = '" + AIIDateofAccident + "', AIIAutoClaim = '" + AIIAutoClaim + "', AIIAccidentLocationAddress = '" + AIIAccidentLocationAddress + AIIAccidentLocationAddress2 + "', " + "AIIAccidentLocationCity = '" + AIIAccidentLocationCity + "', AIIAccidentLocationState = '" + AIIAccidentLocationState + "', AIIAccidentLocationZipCode = '" + AIIAccidentLocationZipCode + "', AIIRoleInAccident = '" + AIIRoleInAccident + "', AIITypeOfAutoIOnsurancePolicy = '" + AIITypeOfAutoIOnsurancePolicy + "', AIIPrefixforReponsibleParty = '" + AIIPrefixforReponsibleParty + "', " + "AIIFirstNameforReponsibleParty = '" + AIIFirstNameforReponsibleParty + "', AIIMiddleNameforReponsibleParty = '" + AIIMiddleNameforReponsibleParty + "',AIILastNameforReponsibleParty = '" + AIILastNameforReponsibleParty + "',AIISuffixforReponsibleParty = '" + AIISuffixforReponsibleParty + "',AIICarrierResponsibleParty = '" + AIICarrierResponsibleParty + "',AIICarrierResponsiblePartyAddress = '" + AIICarrierResponsiblePartyAddress + AIICarrierResponsiblePartyAddress2 + "', " + "AIICarrierResponsiblePartyCity = '" + AIICarrierResponsiblePartyCity + "',AIICarrierResponsiblePartyState = '" + AIICarrierResponsiblePartyState + "',AIICarrierResponsiblePartyZipCode = '" + AIICarrierResponsiblePartyZipCode + "' ,AIICarrierResponsiblePartyPhoneNumber = '" + AIICarrierResponsiblePartyAreaCode + AIICarrierResponsiblePartyPhoneNumber + "',AIICarrierResponsiblePartyPolicyNumber = '" + AIICarrierResponsiblePartyPolicyNumber + "', " + "AIIResponsiblePartyAutoMakeModel = '" + AIIResponsiblePartyAutoMakeModel + "' ,AIIResponsiblePartyLicensePlate = '" + AIIResponsiblePartyLicensePlate + "', AIIFirstNameOfYourPolicyHolder = '" + AIIFirstNameOfYourPolicyHolder + "', AIILastNameOfYourPolicyHolder= '" + AIILastNameOfYourPolicyHolder + "', AIINameAutoInsuranceOfYourVehicle = '" + AIINameAutoInsuranceOfYourVehicle + "', AIIYourInsuranceAddress= '" + AIIYourInsuranceAddress + AIIYourInsuranceAddress2 + "', AIIYourInsuranceCity='" + AIIYourInsuranceCity + "', AIIYourInsuranceState= '" + AIIYourInsuranceState + "', AIIYourInsuranceZipCode = '" + AIIYourInsuranceZipCode + "', AIIYourInsurancePhoneNumber='" + AIIYourInsuranceAreaCode + AIIYourInsurancePhoneNumber + "', " + " AIIYourInsurancePolicyNo = '" + AIIYourInsurancePolicyNo + "', AIIYourLicensePlate = '" + AIIYourLicensePlate + "', AIIYourCarMakeModelYear = '" + AIIYourCarMakeModelYear + "' where PatientRegId = " + PatientRegId;
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();
                    } else {
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
                            MainReceipt.setString(21, AIICarrierResponsiblePartyPhoneNumber);
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
                            MainReceipt.setString(32, AIIYourInsurancePhoneNumber);
                            MainReceipt.setString(33, AIIYourInsurancePolicyNo);
                            MainReceipt.setString(34, AIIYourLicensePlate);
                            MainReceipt.setString(35, AIIYourCarMakeModelYear);
                            MainReceipt.executeUpdate();
                            MainReceipt.close();
                        } catch (Exception e) {
                            Services.DumException("PatientReg2", "SaveDataVictoriaError 6- Insertion Patient_AutoInsuranceInfo Table :", request, e, this.getServletContext());
                            return;
                        }
                    }
                } catch (Exception e) {
                    Services.DumException("PatientReg2", "EditValue Error in updateing Patinet Reg AUtoInsuranceInfo tables: ", request, e, this.getServletContext());
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
                    Services.DumException("PatientReg2", "EditValueError in Getting Count from  Patient_AutoInsuranceInfo tables: ", request, e, this.getServletContext());
                }
                try {
                    if (HealthInsFound > 0) {
                        Query = "Update " + Database + ".Patient_HealthInsuranceInfo Set GovtFundedInsurancePlanChk = '" + GovtFundedInsurancePlanChk + "', GFIPMedicare = '" + GFIPMedicare + "', GFIPMedicaid = '" + GFIPMedicaid + "', GFIPCHIP = '" + GFIPCHIP + "', " + "GFIPTricare = '" + GFIPTricare + "', GFIPVHA = '" + GFIPVHA + "', GFIPIndianHealth = '" + GFIPIndianHealth + "', InsuranceSubPatient = '" + InsuranceSubPatient + "', InsuranceSubGuarantor = '" + InsuranceSubGuarantor + "', InsuranceSubOther = '" + InsuranceSubOther + "', " + "HIPrimaryInsurance = '" + HIPrimaryInsurance + "', HISubscriberFirstName = '" + HISubscriberFirstName + "',HISubscriberLastName = '" + HISubscriberLastName + "',HISubscriberDOB = '" + HISubscriberDOB + "',HISubscriberSSN = '" + HISubscriberSSN + "',HISubscriberRelationtoPatient = '" + HISubscriberRelationtoPatient + "', " + "HISubscriberGroupNo = '" + HISubscriberGroupNo + "',HISubscriberPolicyNo = '" + HISubscriberPolicyNo + "',SecondHealthInsuranceChk = '" + SecondHealthInsuranceChk + "' ,SHISecondaryName = '" + SHISecondaryName + "',SHISubscriberFirstName = '" + SHISubscriberFirstName + "', " + "SHISubscriberLastName = '" + SHISubscriberLastName + "' ,SHISubscriberRelationtoPatient = '" + SHISubscriberRelationtoPatient + "', SHISubscriberGroupNo = '" + SHISubscriberGroupNo + "', SHISubscriberPolicyNo= '" + SHISubscriberPolicyNo + "' where PatientRegId = " + PatientRegId;
                        System.out.println(Query);
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();
                    } else {
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
                            Services.DumException("PatientReg2", "SaveDataVictoriaError 7- Insertion Patient_HealthInsuranceInfo Table", request, e, this.getServletContext());
                            return;
                        }
                    }
                } catch (Exception e) {
                    Services.DumException("PatientReg2", "EditValue Error in updateing Patinet Reg HealthInsuranceInfo tables: ", request, e, this.getServletContext());
                }
            }
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Data Successfully Updated ");
            Parser.SetField("FormName", String.valueOf("RegisteredPatients"));
            Parser.SetField("ActionID", String.valueOf("ShowReport"));
            Parser.GenerateHtml(out, String.valueOf(String.valueOf(Services.GetHtmlPath(servletContext))) + "Exception/Success.html");
        } catch (Exception e2) {
            Services.DumException("PatientReg2", "EditValue", request, e2, this.getServletContext());
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

            System.out.println("Request: " + Request);
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
