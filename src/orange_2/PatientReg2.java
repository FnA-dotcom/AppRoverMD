// 
// Decompiled by Procyon v0.5.36
// 

package orange_2;

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
import java.util.HashMap;

public class PatientReg2 extends HttpServlet
{
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
        final PrintWriter out = new PrintWriter((OutputStream)response.getOutputStream());
        final Services supp = new Services();
        ServletContext context = null;
        try {
            context = this.getServletContext();
            conn = Services.getMysqlConn(context);
            if (ActionID.equals("Victoria_2")) {
                this.Victoria_2(request, out, conn, context);
            } else if (ActionID.equals("SaveDataVictoria")) {
                this.SaveDataVictoria(request, out, conn, context, response);
            } else if (ActionID.equals("EditValues")) {
                this.EditValues(request, out, conn, context, response);
            }

            try {
                conn.close();
            } catch (Exception ex) {
            }
            out.flush();
            out.close();
        }catch(Exception e){
            out.println("Error: "+e.getMessage());
        }
    }
    


    void Victoria_2(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Date = "";
            int ClientIndex = 0;
            String ClientId = request.getParameter("ClientId").trim();
            Query = "Select Date_format(now(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Query = "Select Id from oe.clients where ltrim(rtrim(UPPER(name))) = ltrim(rtrim(UPPER('"+ClientId+"')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientIndex = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Date", String.valueOf(Date));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/PatientRegFormVictoria_2.html");
        }
        catch (Exception ex) {}
    }

    void SaveDataVictoria(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, HttpServletResponse response){
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Database = "";
            int MRN = 0;
            String ExtendedMRN = "0";
            int PatientRegId = 0;
            String Date = "";
            String ClientId = request.getParameter("ClientId").trim();
            int ClientIndex = 0;
            String Title = null;
            String FirstName = null;
            String LastName = null;
            String MiddleInitial = null;
            String County  = null;
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
            String Ethnicity_OthersText = null;
            String SSN = null;
            String EmployementChk = "0";
            String Employer = null;
            String Occupation = null;
            String EmpContact = null;
            String PrimaryCarePhysicianChk = "0";
            String PriCarePhy = null;
            String ReasonVisit = null;
            String PriCarePhyAddress = null;
            String PriCarePhyAddress2 = null;
            String PriCarePhyCity = null;
            String PriCarePhyState = null;
            String PriCarePhyZipCode = null;
            String PatientMinorChk = "0";
            String GuarantorChk = "0";
            String GuarantorEmployer = null;
            String GuarantorEmployerAreaCode = null;
            String GuarantorEmployerPhNumber = null;
            String GuarantorEmployerAddress = null;
            String GuarantorEmployerAddress2 = null;
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
            String WCPHRAddress = null;
            String WCPHRAddress2 = null;
            String WCPHRCity = null;
            String WCPHRState = null;
            String WCPHRZipCode = null;
            String WCPPlanName = null;
            String WCPCarrierName = null;
            String WCPPayerAreaCode = null;
            String WCPPayerPhoneNumber = null;
            String WCPCarrierAddress = null;
            String WCPCarrierAddress2 = null;
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
                DOB =  DOB.substring(6,10) + "-" + DOB.substring(0,2) + "-" + DOB.substring(3,5);                
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
            if (Ethnicity.equals("5")) {
                Ethnicity_OthersText = request.getParameter("Ethnicity_OthersText").trim();
            } else {
                Ethnicity_OthersText = "-";
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
                    WCPDateofInjury =  WCPDateofInjury.substring(6,10) + "-" + WCPDateofInjury.substring(0,2) + "-" + WCPDateofInjury.substring(3,5); 
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
                        AIIDateofAccident =  AIIDateofAccident.substring(6,10) + "-" + AIIDateofAccident.substring(0,2) + "-" + AIIDateofAccident.substring(3,5);
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
                    //AII div2
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

                }else{
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
            if(request.getParameter("HealthInsuranceChk") == null){
                HealthInsuranceChk = "0";
            }else{
                HealthInsuranceChk = request.getParameter("HealthInsuranceChk").trim();
            }
            if(HealthInsuranceChk.equals("1")){
                if(request.getParameter("GovtFundedInsurancePlanChk") == null){
                    GovtFundedInsurancePlanChk = "0";
                }else{
                    GovtFundedInsurancePlanChk = request.getParameter("GovtFundedInsurancePlanChk").trim();
                }
                if(GovtFundedInsurancePlanChk.equals("1")){
                    if(request.getParameter("GFIPMedicare") == null){
                        GFIPMedicare = "0";
                    }else{
                        GFIPMedicare = "1";
                    }
                    if(request.getParameter("GFIPMedicaid") == null){
                        GFIPMedicaid = "0";
                    }else{
                        GFIPMedicaid = "1";
                    }
                    if(request.getParameter("GFIPCHIP") == null){
                        GFIPCHIP = "0";
                    }else{
                        GFIPCHIP = "1";
                    }
                    if(request.getParameter("GFIPTricare") == null){
                        GFIPTricare = "0";
                    }else{
                        GFIPTricare = "1";
                    }
                    if(request.getParameter("GFIPVHA") == null){
                        GFIPVHA = "0";
                    }else{
                        GFIPVHA = "1";
                    }
                    if(request.getParameter("GFIPIndianHealth") == null){
                        GFIPIndianHealth = "0";
                    }else{
                        GFIPIndianHealth = "1";
                    }
                }
                if(request.getParameter("InsuranceSubPatient") == null){
                    InsuranceSubPatient = "0";
                }else{
                    InsuranceSubPatient = "1";
                }
                if(request.getParameter("InsuranceSubGuarantor") == null){
                    InsuranceSubGuarantor = "0";
                }else{
                    InsuranceSubGuarantor = "1";
                }
                if(request.getParameter("InsuranceSubOther") == null){
                    InsuranceSubOther = "0";
                }else{
                    InsuranceSubOther = "1";
                }
                if(request.getParameter("HIPrimaryInsurance") == null){
                    HIPrimaryInsurance = "";
                }else{
                    HIPrimaryInsurance = request.getParameter("HIPrimaryInsurance").trim();
                }
                if(request.getParameter("HISubscriberFirstName") == null){
                    HISubscriberFirstName = "";
                }else{
                    HISubscriberFirstName = request.getParameter("HISubscriberFirstName").trim();
                }
                if(request.getParameter("HISubscriberLastName") == null){
                    HISubscriberLastName = "";
                }else{
                    HISubscriberLastName = request.getParameter("HISubscriberLastName").trim();
                }
                if(request.getParameter("HISubscriberDOB") == null){
                    HISubscriberDOB = "0000-00-00";
                }else{
                    HISubscriberDOB = request.getParameter("HISubscriberDOB").trim();
                    HISubscriberDOB =  HISubscriberDOB.substring(6,10) + "-" + HISubscriberDOB.substring(0,2) + "-" + HISubscriberDOB.substring(3,5);
                }
                if(request.getParameter("HISubscriberSSN") == null){
                    HISubscriberSSN = "";
                }else{
                    HISubscriberSSN = request.getParameter("HISubscriberSSN").trim();
                }
                if(request.getParameter("HISubscriberRelationtoPatient") == null){
                    HISubscriberRelationtoPatient = "";
                }else{
                    HISubscriberRelationtoPatient = request.getParameter("HISubscriberRelationtoPatient").trim();
                }
                if(request.getParameter("HISubscriberGroupNo") == null){
                    HISubscriberGroupNo = "";
                }else{
                    HISubscriberGroupNo = request.getParameter("HISubscriberGroupNo").trim();
                }
                if(request.getParameter("HISubscriberPolicyNo") == null){
                    HISubscriberPolicyNo = "";
                }else{
                    HISubscriberPolicyNo = request.getParameter("HISubscriberPolicyNo").trim();
                }
                if(request.getParameter("SecondHealthInsuranceChk") == null){
                    SecondHealthInsuranceChk = "1";
                }else{
                    SecondHealthInsuranceChk = request.getParameter("SecondHealthInsuranceChk").trim();
                }
                if(SecondHealthInsuranceChk.equals("1")) {
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
                        SHISubscriberDOB =  SHISubscriberDOB.substring(6,10) + "-" + SHISubscriberDOB.substring(0,2) + "-" + SHISubscriberDOB.substring(3,5);
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
/*
            out.println("ClientId :--"+ClientId );
            out.println("Title :--"+Title );
            out.println("FirstName :--"+FirstName );
            out.println("LastName :--"+LastName );
            out.println("MiddleInitial :--"+MiddleInitial );
            out.println("DOB :--"+DOB );
            out.println("Age :--"+Age );
            out.println("gender :--"+gender );
            out.println("Email :--"+Email );
            out.println("ConfirmEmail :--"+ConfirmEmail );
            out.println("MaritalStatus :--"+MaritalStatus );
            out.println("AreaCode :--"+AreaCode );
            out.println("PhNumber :--"+PhNumber );
            out.println("Address :--"+Address );
            out.println("Address2 :--"+Address2 );
            out.println("City :--"+City );
            out.println("State :--"+State );
            out.println("ZipCode :--"+ZipCode );
            out.println("Ethnicity :--"+Ethnicity );
            out.println("Ethnicity_OthersText :--"+Ethnicity_OthersText );
            out.println("SSN :--"+SSN );
            out.println("EmployementChk :--"+EmployementChk );
            out.println("Employer :--"+Employer );
            out.println("Occupation :--"+Occupation );
            out.println("EmpContact :--"+EmpContact );
            out.println("PrimaryCarePhysicianChk :--"+PrimaryCarePhysicianChk );
            out.println("PriCarePhy :--"+PriCarePhy );
            out.println("ReasonVisit :--"+ReasonVisit );
            out.println("PriCarePhyAddress :--"+PriCarePhyAddress );
            out.println("PriCarePhyAddress2 :--"+PriCarePhyAddress2 );
            out.println("PriCarePhyCity :--"+PriCarePhyCity );
            out.println("PriCarePhyState :--"+PriCarePhyState );
            out.println("PriCarePhyZipCode :--"+PriCarePhyZipCode );
            out.println("PatientMinorChk :--"+PatientMinorChk );
            out.println("GuarantorChk :--"+GuarantorChk );
            out.println("GuarantorEmployer :--"+GuarantorEmployer );
            out.println("GuarantorEmployerAreaCode :--"+GuarantorEmployerAreaCode );
            out.println("GuarantorEmployerPhNumber :--"+GuarantorEmployerPhNumber );
            out.println("GuarantorEmployerAddress :--"+GuarantorEmployerAddress );
            out.println("GuarantorEmployerAddress2 :--"+GuarantorEmployerAddress2 );
            out.println("GuarantorEmployerCity :--"+GuarantorEmployerCity );
            out.println("GuarantorEmployerState :--"+GuarantorEmployerState );
            out.println("GuarantorEmployerZipCode :--"+GuarantorEmployerZipCode );
            out.println("WorkersCompPolicyChk :--"+WorkersCompPolicyChk );
            out.println("WCPDateofInjury :--"+WCPDateofInjury );
            out.println("WCPCaseNo :--"+WCPCaseNo );
            out.println("WCPGroupNo :--"+WCPGroupNo );
            out.println("WCPMemberId :--"+WCPMemberId );
            out.println("WCPInjuryRelatedAutoMotorAccident :--"+WCPInjuryRelatedAutoMotorAccident );
            out.println("WCPInjuryRelatedWorkRelated :--"+WCPInjuryRelatedWorkRelated );
            out.println("WCPInjuryRelatedOtherAccident :--"+WCPInjuryRelatedOtherAccident );
            out.println("WCPInjuryRelatedNoAccident :--"+WCPInjuryRelatedNoAccident );
            out.println("WCPInjuryOccurVehicle :--"+WCPInjuryOccurVehicle );
            out.println("WCPInjuryOccurWork :--"+WCPInjuryOccurWork );
            out.println("WCPInjuryOccurHome :--"+WCPInjuryOccurHome );
            out.println("WCPInjuryOccurOther :--"+WCPInjuryOccurOther );
            out.println("WCPInjuryDescription :--"+WCPInjuryDescription );
            out.println("WCPHRFirstName :--"+WCPHRFirstName );
            out.println("WCPHRLastName :--"+WCPHRLastName );
            out.println("WCPHRAreaCode :--"+WCPHRAreaCode );
            out.println("WCPHRPhoneNumber :--"+WCPHRPhoneNumber );
            out.println("WCPHRAddress :--"+WCPHRAddress );
            out.println("WCPHRAddress2 :--"+WCPHRAddress2 );
            out.println("WCPHRCity :--"+WCPHRCity );
            out.println("WCPHRState :--"+WCPHRState );
            out.println("WCPHRZipCode :--"+WCPHRZipCode );
            out.println("WCPPlanName :--"+WCPPlanName );
            out.println("WCPCarrierName :--"+WCPCarrierName );
            out.println("WCPPayerAreaCode :--"+WCPPayerAreaCode );
            out.println("WCPPayerPhoneNumber :--"+WCPPayerPhoneNumber );
            out.println("WCPCarrierAddress :--"+WCPCarrierAddress );
            out.println("WCPCarrierAddress2 :--"+WCPCarrierAddress2 );
            out.println("WCPCarrierCity :--"+WCPCarrierCity );
            out.println("WCPCarrierState :--"+WCPCarrierState );
            out.println("WCPCarrierZipCode :--"+WCPCarrierZipCode );
            out.println("WCPAdjudicatorFirstName :--"+WCPAdjudicatorFirstName );
            out.println("WCPAdjudicatorLastName :--"+WCPAdjudicatorLastName );
            out.println("WCPAdjudicatorAreaCode :--"+WCPAdjudicatorAreaCode );
            out.println("WCPAdjudicatorPhoneNumber :--"+WCPAdjudicatorPhoneNumber );
            out.println("WCPAdjudicatorFaxAreaCode :--"+WCPAdjudicatorFaxAreaCode );
            out.println("WCPAdjudicatorFaxPhoneNumber :--"+WCPAdjudicatorFaxPhoneNumber );
            out.println("MotorVehicleAccidentChk :--"+MotorVehicleAccidentChk );
            out.println("AutoInsuranceInformationChk :--"+AutoInsuranceInformationChk );
            out.println("AIIDateofAccident :--"+AIIDateofAccident );
            out.println("AIIAutoClaim :--"+AIIAutoClaim );
            out.println("AIIAccidentLocationAddress :--"+AIIAccidentLocationAddress );
            out.println("AIIAccidentLocationAddress2 :--"+AIIAccidentLocationAddress2 );
            out.println("AIIAccidentLocationCity :--"+AIIAccidentLocationCity );
            out.println("AIIAccidentLocationState :--"+AIIAccidentLocationState );
            out.println("AIIAccidentLocationZipCode :--"+AIIAccidentLocationZipCode );
            out.println("AIIRoleInAccident :--"+AIIRoleInAccident );
            out.println("AIITypeOfAutoIOnsurancePolicy :--"+AIITypeOfAutoIOnsurancePolicy );
            out.println("AIIPrefixforReponsibleParty :--"+AIIPrefixforReponsibleParty );
            out.println("AIIFirstNameforReponsibleParty :--"+AIIFirstNameforReponsibleParty );
            out.println("AIIMiddleNameforReponsibleParty :--"+AIIMiddleNameforReponsibleParty );
            out.println("AIILastNameforReponsibleParty :--"+AIILastNameforReponsibleParty );
            out.println("AIISuffixforReponsibleParty :--"+AIISuffixforReponsibleParty );
            out.println("AIICarrierResponsibleParty :--"+AIICarrierResponsibleParty );
            out.println("AIICarrierResponsiblePartyAddress :--"+AIICarrierResponsiblePartyAddress );
            out.println("AIICarrierResponsiblePartyAddress2 :--"+AIICarrierResponsiblePartyAddress2 );
            out.println("AIICarrierResponsiblePartyCity :--"+AIICarrierResponsiblePartyCity );
            out.println("AIICarrierResponsiblePartyState :--"+AIICarrierResponsiblePartyState );
            out.println("AIICarrierResponsiblePartyZipCode :--"+AIICarrierResponsiblePartyZipCode );
            out.println("AIICarrierResponsiblePartyAreaCode :--"+AIICarrierResponsiblePartyAreaCode );
            out.println("AIICarrierResponsiblePartyPhoneNumber :--"+AIICarrierResponsiblePartyPhoneNumber );
            out.println("AIICarrierResponsiblePartyPolicyNumber :--"+AIICarrierResponsiblePartyPolicyNumber );
            out.println("AIIResponsiblePartyAutoMakeModel :--"+AIIResponsiblePartyAutoMakeModel );
            out.println("AIIResponsiblePartyLicensePlate :--"+AIIResponsiblePartyLicensePlate );
            out.println("AIIFirstNameOfYourPolicyHolder :--"+AIIFirstNameOfYourPolicyHolder );
            out.println("AIILastNameOfYourPolicyHolder :--"+AIILastNameOfYourPolicyHolder );
            out.println("AIINameAutoInsuranceOfYourVehicle :--"+AIINameAutoInsuranceOfYourVehicle );
            out.println("AIIYourInsuranceAddress :--"+AIIYourInsuranceAddress );
            out.println("AIIYourInsuranceAddress2 :--"+AIIYourInsuranceAddress2 );
            out.println("AIIYourInsuranceCity :--"+AIIYourInsuranceCity );
            out.println("AIIYourInsuranceState :--"+AIIYourInsuranceState );
            out.println("AIIYourInsuranceZipCode :--"+AIIYourInsuranceZipCode );
            out.println("AIIYourInsuranceAreaCode :--"+AIIYourInsuranceAreaCode );
            out.println("AIIYourInsurancePhoneNumber :--"+AIIYourInsurancePhoneNumber );
            out.println("AIIYourInsurancePolicyNo :--"+AIIYourInsurancePolicyNo );
            out.println("AIIYourLicensePlate :--"+AIIYourLicensePlate );
            out.println("AIIYourCarMakeModelYear :--"+AIIYourCarMakeModelYear );
            out.println("HealthInsuranceChk :--"+HealthInsuranceChk );
            out.println("GovtFundedInsurancePlanChk :--"+GovtFundedInsurancePlanChk );
            out.println("GFIPMedicare :--"+GFIPMedicare );
            out.println("GFIPMedicaid :--"+GFIPMedicaid );
            out.println("GFIPCHIP :--"+GFIPCHIP );
            out.println("GFIPTricare :--"+GFIPTricare );
            out.println("GFIPVHA :--"+GFIPVHA );
            out.println("GFIPIndianHealth :--"+GFIPIndianHealth );
            out.println("InsuranceSubPatient :--"+InsuranceSubPatient );
            out.println("InsuranceSubGuarantor :--"+InsuranceSubGuarantor );
            out.println("InsuranceSubOther :--"+InsuranceSubOther );
            out.println("HIPrimaryInsurance :--"+HIPrimaryInsurance );
            out.println("HISubscriberFirstName :--"+HISubscriberFirstName );
            out.println("HISubscriberLastName :--"+HISubscriberLastName );
            out.println("HISubscriberDOB :--"+HISubscriberDOB );
            out.println("HISubscriberSSN :--"+HISubscriberSSN );
            out.println("HISubscriberRelationtoPatient :--"+HISubscriberRelationtoPatient );
            out.println("HISubscriberGroupNo :--"+HISubscriberGroupNo );
            out.println("HISubscriberPolicyNo :--"+HISubscriberPolicyNo );
            out.println("SecondHealthInsuranceChk :--"+SecondHealthInsuranceChk );
            out.println("SHISecondaryName :--"+SHISecondaryName );
            out.println("SHISubscriberFirstName :--"+SHISubscriberFirstName );
            out.println("SHISubscriberLastName :--"+SHISubscriberLastName );
            out.println("SHISubscriberDOB :--"+SHISubscriberDOB );
            out.println("SHISubscriberRelationtoPatient :--"+SHISubscriberRelationtoPatient );
            out.println("SHISubscriberGroupNo :--"+SHISubscriberGroupNo );
            out.println("SHISubscriberPolicyNo :--"+SHISubscriberPolicyNo );*/

            
            Query = "Select Date_format(now(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();
            try {
                Query = "Select Id, dbname from oe.clients where ltrim(rtrim(UPPER(name))) = ltrim(rtrim(UPPER('"+ClientId+"')))";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    ClientIndex = rset.getInt(1);
                    Database = rset.getString(2);
                }
                rset.close();
                stmt.close();

//                if(ClientIndex == 8){
//                    Database = "oe_2";//orange
//                }else if(ClientIndex == 9){
//                    Database = "victoria"; //victoria
//                }else if(ClientIndex == 10){
//                    Database = "oddasa"; //oddasa
//                }

//                Query = "SELECT MAX(MRN) + 1  FROM " + Database + ".PatientReg ";
                Query = "Select MRN from "+Database+".PatientReg order by ID desc limit 1 ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    MRN = rset.getInt(1);
                }
                rset.close();
                stmt.close();
                if (String.valueOf(MRN).length() == 0) {
                    MRN = 310001;
                }else if(String.valueOf(MRN).length() == 4){
                    MRN = 310001;
                }else if(String.valueOf(MRN).length() == 8){
                    MRN = 310001;
                }else if(String.valueOf(MRN).length() == 6) {
                    MRN = MRN + 1;
                }

                if(ClientIndex == 8){
                    ExtendedMRN = "1008"+MRN;
                }else if(ClientIndex == 9){
                    ExtendedMRN = "1009"+MRN;
                }else if(ClientIndex == 10){
                    ExtendedMRN = "1010"+MRN;
                }else if(ClientIndex == 11){
                    ExtendedMRN = "1011"+MRN;
                }

            }catch(Exception e){
                System.out.println("Error in getting MRN and ClientIndex"+e.getMessage());
            }

            try {
                if(Email.equals(ConfirmEmail)){
                    Email = ConfirmEmail;
                }else{
                    out.println("<!DOCTYPE html><html><body><p style=\"color:black;\">Please Put Email and Confirm Email Correctly and then Submit</p>");
                    //  out.println("<br>Request has been send to ERM , Find Patient MRN "+MRN);
                    out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
                    return;
                }
                final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO "+Database+".PatientReg (ClientIndex,FirstName,LastName ," +
                        " MiddleInitial,DOB,Age,Gender ,Email,PhNumber ,Address,City ,State,Country,ZipCode,SSN,Occupation ,Employer ,EmpContact," +
                        " PriCarePhy,ReasonVisit,SelfPayChk,CreatedDate,Title, MaritalStatus,CreatedBy, MRN, Status, ExtendedMRN, County) \n" +
                        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,0,?,?) ");
                MainReceipt.setInt(1, ClientIndex);
                MainReceipt.setString(2, FirstName);
                MainReceipt.setString(3, LastName);
                MainReceipt.setString(4, MiddleInitial);
                MainReceipt.setString(5, DOB);
                MainReceipt.setString(6, Age);
                MainReceipt.setString(7, gender);
                MainReceipt.setString(8, Email);
                MainReceipt.setString(9, AreaCode + PhNumber);
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
                MainReceipt.setInt(25,MRN);
                MainReceipt.setString(26, ExtendedMRN);
                MainReceipt.setString(27, County);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }
            catch (Exception e) {
                out.println("Error 2- Insertion PatientReg Table :" + e.getMessage());
                return;
            }
            try {
                Query = "Select max(ID) from "+Database+".PatientReg ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientRegId = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }
            catch (Exception e) {
                out.println("Error 3- :" + e.getMessage());
                return;
            }

            try{
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO "+Database+".PatientVisit(MRN,PatientRegId,ReasonVisit,VisitNumber,DoctorId,DateofService,CreatedDate,CreatedBy) \nVALUES (?,?,?,1,NULL,now(),now(),?) ");
                MainReceipt.setInt(1, MRN);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, ReasonVisit);
                MainReceipt.setString(4, "Out Patient");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }catch(Exception e){
                out.println("Error 3.1 Insertion in table PatientVisit- :" + e.getMessage());
            }

            try{
                final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO "+Database+".PatientReg_Details (PatientRegId,Ethnicity," +
                        "Ethnicity_OthersText,EmployementChk,Employer,Occupation,EmpContact,PrimaryCarePhysicianChk,PriCarePhy,ReasonVisit," +
                        "PriCarePhyAddress,PriCarePhyCity,PriCarePhyState,PriCarePhyZipCode,PatientMinorChk,GuarantorChk,GuarantorEmployer," +
                        "GuarantorEmployerPhNumber,GuarantorEmployerAddress,GuarantorEmployerCity,GuarantorEmployerState,GuarantorEmployerZipCode," +
                        "CreatedDate,WorkersCompPolicyChk,MotorVehicleAccidentChk,HealthInsuranceChk) \n" +
                        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?) ");
                MainReceipt.setInt(1, PatientRegId);
                MainReceipt.setString(2, Ethnicity);
                MainReceipt.setString(3, Ethnicity_OthersText);
                MainReceipt.setInt(4, Integer.parseInt(EmployementChk));
                MainReceipt.setString(5, Employer);
                MainReceipt.setString(6, Occupation);
                MainReceipt.setString(7, EmpContact);
                MainReceipt.setInt(8, Integer.parseInt(PrimaryCarePhysicianChk));
                MainReceipt.setString(9, PriCarePhy);
                MainReceipt.setString(10, ReasonVisit);
                MainReceipt.setString(11, PriCarePhyAddress + " "+ PriCarePhyAddress2);
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
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }
            catch (Exception e) {
                out.println("Error 4- Insertion PatientReg_Details Table :" + e.getMessage());
                return;
            }

            if(WorkersCompPolicyChk.equals("1")){
                try{
                    final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO "+Database+".Patient_WorkCompPolicy (PatientRegId,WCPDateofInjury," +
                            "WCPCaseNo,WCPGroupNo,WCPMemberId,WCPInjuryRelatedAutoMotorAccident,WCPInjuryRelatedWorkRelated,WCPInjuryRelatedOtherAccident," +
                            "WCPInjuryRelatedNoAccident,WCPInjuryOccurVehicle,WCPInjuryOccurWork,WCPInjuryOccurHome,WCPInjuryOccurOther,WCPInjuryDescription," +
                            "WCPHRFirstName,WCPHRLastName,WCPHRPhoneNumber,WCPHRAddress,WCPHRCity,WCPHRState,WCPHRZipCode,WCPPlanName,WCPCarrierName," +
                            "WCPPayerPhoneNumber,WCPCarrierAddress,WCPCarrierCity,WCPCarrierState,WCPCarrierZipCode,WCPAdjudicatorFirstName,WCPAdjudicatorLastName," +
                            "WCPAdjudicatorPhoneNumber,WCPAdjudicatorFaxPhoneNumber,CreatedDate) \n" +
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
                    MainReceipt.setString(18, WCPHRAddress + " " +WCPHRAddress2);
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
                    MainReceipt.setString(31, WCPAdjudicatorAreaCode + WCPAdjudicatorPhoneNumber);
                    MainReceipt.setString(32, WCPAdjudicatorFaxAreaCode + WCPAdjudicatorFaxPhoneNumber);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                }catch(Exception e){
                    out.println("Error 5- Insertion Patient_WorkCompPolicy Table :" + e.getMessage());
                    return;
                }
            }
            if(MotorVehicleAccidentChk.equals("1")){
                try{
                    final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO "+Database+".Patient_AutoInsuranceInfo (PatientRegId," +
                            "AutoInsuranceInformationChk,AIIDateofAccident,AIIAutoClaim,AIIAccidentLocationAddress,AIIAccidentLocationCity,AIIAccidentLocationState," +
                            "AIIAccidentLocationZipCode,AIIRoleInAccident,AIITypeOfAutoIOnsurancePolicy,AIIPrefixforReponsibleParty,AIIFirstNameforReponsibleParty," +
                            "AIIMiddleNameforReponsibleParty,AIILastNameforReponsibleParty,AIISuffixforReponsibleParty,AIICarrierResponsibleParty," +
                            "AIICarrierResponsiblePartyAddress,AIICarrierResponsiblePartyCity,AIICarrierResponsiblePartyState,AIICarrierResponsiblePartyZipCode," +
                            "AIICarrierResponsiblePartyPhoneNumber,AIICarrierResponsiblePartyPolicyNumber,AIIResponsiblePartyAutoMakeModel," +
                            "AIIResponsiblePartyLicensePlate,AIIFirstNameOfYourPolicyHolder,AIILastNameOfYourPolicyHolder,AIINameAutoInsuranceOfYourVehicle," +
                            "AIIYourInsuranceAddress,AIIYourInsuranceCity,AIIYourInsuranceState,AIIYourInsuranceZipCode,AIIYourInsurancePhoneNumber," +
                            "AIIYourInsurancePolicyNo,AIIYourLicensePlate,AIIYourCarMakeModelYear,CreatedDate) \n" +
                            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
                    MainReceipt.setInt(1, PatientRegId);
                    MainReceipt.setInt(2, Integer.parseInt(AutoInsuranceInformationChk));
                    MainReceipt.setString(3, AIIDateofAccident);
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
                    MainReceipt.setString(17, AIICarrierResponsiblePartyAddress+ " " + AIICarrierResponsiblePartyAddress2);
                    MainReceipt.setString(18, AIICarrierResponsiblePartyCity);
                    MainReceipt.setString(19, AIICarrierResponsiblePartyState);
                    MainReceipt.setString(20, AIICarrierResponsiblePartyZipCode);
                    MainReceipt.setString(21, AIICarrierResponsiblePartyAreaCode + AIICarrierResponsiblePartyPhoneNumber);
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
                    MainReceipt.setString(32, AIIYourInsuranceAreaCode + AIIYourInsurancePhoneNumber);
                    MainReceipt.setString(33, AIIYourInsurancePolicyNo);
                    MainReceipt.setString(34, AIIYourLicensePlate);
                    MainReceipt.setString(35, AIIYourCarMakeModelYear);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                }catch(Exception e){
                    out.println("Error 6- Insertion Patient_AutoInsuranceInfo Table :" + e.getMessage());
                    return;
                }
            }

            if(HealthInsuranceChk.equals("1")){
                try{
                    final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO "+Database+".Patient_HealthInsuranceInfo (PatientRegId," +
                            "GovtFundedInsurancePlanChk,GFIPMedicare,GFIPMedicaid,GFIPCHIP,GFIPTricare,GFIPVHA,GFIPIndianHealth,InsuranceSubPatient," +
                            "InsuranceSubGuarantor,InsuranceSubOther,HIPrimaryInsurance,HISubscriberFirstName,HISubscriberLastName,HISubscriberDOB,HISubscriberSSN," +
                            "HISubscriberRelationtoPatient,HISubscriberGroupNo,HISubscriberPolicyNo,SecondHealthInsuranceChk,SHISecondaryName," +
                            "SHISubscriberFirstName,SHISubscriberLastName,SHISubscriberRelationtoPatient,SHISubscriberGroupNo,SHISubscriberPolicyNo,CreatedDate) \n" +
                            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
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

                }catch(Exception e){
                    out.println("Error 7- Insertion Patient_HealthInsuranceInfo Table :" + e.getMessage());
                    String str = "";
                    for (int i = 0; i < e.getStackTrace().length; ++i) {
                        str = str + e.getStackTrace()[i] + "<br>";
                    }
                    out.println(str);
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
            System.out.println(ReasonVisit+" Before");
            if(ReasonVisit != null) {
                ReasonVisit = ReasonVisit.replaceAll(" ", "");
                System.out.println(ReasonVisit + " After");
                if (ReasonVisit.toUpperCase().equals("COVIDTESTING")) {
                    InsertCOVIDRegReply = this.InsertCOVIDReg(request, response, out, conn, String.valueOf(PatientRegId));
                    if (Integer.parseInt(InsertCOVIDRegReply) > 0) {
                        Message = "COVID Form Also Registered Successfully.";
                    } else {
                        Message = "COVID Form Not Registered. ";
                    }
                }
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " We Have Registered You Successfully. Please Wait for Further Processing. " + Message + " <br>DATED: " + Date);
            Parser.SetField("FormName", String.valueOf("PatientReg2"));
            Parser.SetField("ActionID", String.valueOf("Victoria_2&ClientId=Victoria"));
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.GenerateHtml(out, "/opt/Htmls/orange_2/Exception/Message.html");

        }
        catch (Exception e) {
            out.println("Error found: "+e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);

        }
    }

    void EditValues(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, HttpServletResponse response){
        try{

            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Database = "";
            String MRN = request.getParameter("MRN").trim();
            int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
            String ExtendedMRN = "0";
            int PatientRegId = 0;
            String Date = "";
            int ClientIndex = 0;
            String Title = null;
            String FirstName = null;
            String LastName = null;
            String MiddleInitial = null;
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
            String Ethnicity = null;
            String Ethnicity_OthersText = null;
            String SSN = null;
            String EmployementChk = "0";
            String Employer = null;
            String Occupation = null;
            String EmpContact = null;
            String PrimaryCarePhysicianChk = "0";
            String PriCarePhy = null;
            String ReasonVisit = null;
            String PriCarePhyAddress = null;
            String PriCarePhyAddress2 = null;
            String PriCarePhyCity = null;
            String PriCarePhyState = null;
            String PriCarePhyZipCode = null;
            String PatientMinorChk = "0";
            String GuarantorChk = "0";
            String GuarantorEmployer = null;
            String GuarantorEmployerAreaCode = null;
            String GuarantorEmployerPhNumber = null;
            String GuarantorEmployerAddress = null;
            String GuarantorEmployerAddress2 = null;
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
            String WCPHRAddress = null;
            String WCPHRAddress2 = null;
            String WCPHRCity = null;
            String WCPHRState = null;
            String WCPHRZipCode = null;
            String WCPPlanName = null;
            String WCPCarrierName = null;
            String WCPPayerAreaCode = null;
            String WCPPayerPhoneNumber = null;
            String WCPCarrierAddress = null;
            String WCPCarrierAddress2 = null;
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

            try{
                Query = "Select dbname from oe.clients where Id = "+ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    Database = rset.getString(1);
                }
                rset.close();
                stmt.close();
            }catch(Exception e){
                System.out.println(e.getMessage());
            }

            try {
                Query = " Select IFNULL(Title,''), IFNULL(FirstName,''), IFNULL(LastName,''), IFNULL(MiddleInitial, ''), IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'),''), " +
                        " IFNULL(Age,''), IFNULL(Gender, ''), IFNULL(Email,''), IFNULL(SUBSTRING(PhNumber, 1, 3),''),  IFNULL(SUBSTRING(PhNumber, 4, 10),'')," +
                        " IFNULL(Address,''), IFNULL(City,''), IFNULL(State,''), IFNULL(Country,''), IFNULL(ZipCode,''), IFNULL(SSN,''), IFNULL(Occupation,''), IFNULL(Employer,'')," +
                        " IFNULL(EmpContact, ''), IFNULL(PriCarePhy,''), IFNULL(ReasonVisit, ''), IFNULL(MaritalStatus, ''), IFNULL(DoctorsName,''), " +
                        " IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y'),DATE_FORMAT(CreatedDate,'%m/%d/%Y')) from "+Database+".PatientReg where MRN = '"+MRN+"'";
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
                   Email = rset.getString(7);
                   AreaCode  = rset.getString(7);
                   PhNumber  = rset.getString(7);
                   Address  = rset.getString(7);
                   City  = rset.getString(7);
                   State  = rset.getString(7);

                }
                rset.close();
                stmt.close();
            }catch(Exception e){
                out.println(e.getMessage());
            }



        }catch(Exception e){
            out.println(e.getMessage());
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
        String City = "";
        String Address = "";
        String State = "";
        String ZipCode = "";
        String reply = "";
        try {
            Query = "Select IFNULL(FirstName,''), IFNULL(MiddleInitial,''), IFNULL(LastName,''), IFNULL(Email, ''), IFNULL(PhNumber,0), IFNULL(DOB,'0000-00-00'), IFNULL(Gender,'M'), IFNULL(City,''),IFNULL(Address,''), IFNULL(State,'TX'), IFNULL(ZipCode,'') from victoria.PatientReg where ID = " + PatientRegId;
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
            }
            rset.close();
            stmt.close();
        }
        catch (Exception e) {
            out.println("Error in Getting Data from PatientReg table" + e.getMessage());
        }
        if (Gender.equals("male")) {
            Gender = "M";
        }
        else {
            Gender = "F";
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
            responseJSON.put("City", City);
            responseJSON.put("County", "Unknown");
            responseJSON.put("Street", Address);
            responseJSON.put("StateCode", State);
            responseJSON.put("IsValidDOB", true);
            responseJSON.put("Zipcode", ZipCode);
            Request = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString((Object)responseJSON);

//            out.println(Request);

            final String BaseURL = "https://victoriacovid.com/api/CovidPatient/CreatePatient/?UserId=1";
            final String Mask = "";
            final URL url = new URL(BaseURL);
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
            //System.out.println(size);
            final byte[] response2 = new byte[size];
            is.read(response2);
            reply = new String(response2);
            reply = reply.trim();
            //out.println(reply + "--:reply");
        }
        catch (Exception e) {
            String Message = "0";

            System.out.println("Error in Sending Data API COVID VICTORIA: " + e.getMessage());
            System.out.println("Message" + Message);
            return Message;
            //out.println("Error in Sending Data API COVID VICTORIA: " + e.getMessage());
//            String str = "";
//            for (int i = 0; i < e.getStackTrace().length; ++i) {
//                str = str + e.getStackTrace()[i] + "<br>";
//            }
//            System.out.println(str);

        }
        return reply;
    }

}
