//
// Decompiled by Procyon v0.5.36
//

package md;

import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class DownloadBundle_TEST extends HttpServlet {
    private static boolean ReadPdfGetData(final String FileName, final String Path) {
        try (final PDDocument document = PDDocument.load(new File(Path + "/" + FileName))) {
            document.getClass();
            if (document.isEncrypted()) {
                return false;
            }
            final PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            final PDFTextStripper tStripper = new PDFTextStripper();
            tStripper.getStartPage();
            final String pdfFileInText = tStripper.getText(document);
            final String[] lines;
            final String[] array;
            final String[] split = array = (lines = pdfFileInText.split("\\r?\\n"));
            for (final String line : array) {
                if (line.toUpperCase().trim().contains("SIGNATURE")) {
                }
            }
        } catch (Exception ee) {
            System.out.println(ee.getLocalizedMessage());
            return false;
        }
        return true;
    }

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
        ResultSet rset = null;
        Statement stmt = null;
        String Query = "";
        String Database = "";
        String DirectoryName = "";
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        int ClientId = 0;
        try {
            final Cookie[] cookies = request.getCookies();
            Zone = (UserId = (Passwd = ""));
            final int checkCookie = 0;
            for (int coky = 0; coky < cookies.length; ++coky) {
                final String cName = cookies[coky].getName();
                final String cValue = cookies[coky].getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
                }
            }
            Query = "SELECT ClientId FROM oe.sysusers WHERE ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientId = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            Query = "Select dbname, IFNULL(DirectoryName,'') from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Database = rset.getString(1);
                DirectoryName = rset.getString(2);
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
        if (ActionID.equals("GETINPUTVictoria")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Victoria Admission Bundle", "Download or View Admission Bundle", ClientId);
            this.GETINPUTVictoria(request, out, conn, context, response, UserId, Database, ClientId, DirectoryName);
        }
        try {
            conn.close();
        } catch (Exception ex) {
        }
        out.flush();
        out.close();
    }

    void GETINPUTVictoria(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        final MergePdf mergePdf = new MergePdf();
        final int ID = Integer.parseInt(request.getParameter("ID").trim());
        final String Path1 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/";
        final String Path2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/";
        String ResultPdf = "";
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
        final String AreaCode = "";
        String PhNumber = "";
        String Address = "";
        final String Address2 = "";
        final String City = "";
        final String State = "";
        final String ZipCode = "";
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
        final String PriCarePhyAddress2 = "";
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
        final String GuarantorEmployerAreaCode = "";
        String GuarantorEmployerPhNumber = "";
        String GuarantorEmployerAddress = "";
        final String GuarantorEmployerAddress2 = "";
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
        String CityStateZip = "";
        final String Country = "";
        String MRN = "";
        int ClientIndex = 0;
        String ClientName = "";
        String DOS = "";
        String DoctorId = null;
        final String DoctorName = null;
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
            } catch (Exception e) {
                out.println("Error In PateintReg:--" + e.getMessage());
                out.println(Query);
            }
            Query = "Select  Ethnicity,Ethnicity_OthersText,EmployementChk,Employer,Occupation,EmpContact,PrimaryCarePhysicianChk,PriCarePhy,ReasonVisit,PriCarePhyAddress,PriCarePhyCity,PriCarePhyState,PriCarePhyZipCode,PatientMinorChk,GuarantorChk,GuarantorEmployer,GuarantorEmployerPhNumber,GuarantorEmployerAddress,GuarantorEmployerCity,GuarantorEmployerState,GuarantorEmployerZipCode,CreatedDate,WorkersCompPolicyChk,MotorVehicleAccidentChk,HealthInsuranceChk from " + Database + ".PatientReg_Details where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
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
            stmt.close();
            if (WorkersCompPolicyChk == 1) {
                try {
                    Query = "Select IFNULL(DATE_FORMAT(WCPDateofInjury,'%m/%d/%Y'),''), IFNULL(WCPCaseNo,''), IFNULL(WCPGroupNo,''), IFNULL(WCPMemberId,''), IFNULL(WCPInjuryRelatedAutoMotorAccident,''), IFNULL(WCPInjuryRelatedWorkRelated,''), IFNULL(WCPInjuryRelatedOtherAccident,''), IFNULL(WCPInjuryRelatedNoAccident,''), IFNULL(WCPInjuryOccurVehicle,''), IFNULL(WCPInjuryOccurWork,''), IFNULL(WCPInjuryOccurHome,''), IFNULL(WCPInjuryOccurOther,''), IFNULL(WCPInjuryDescription,''), IFNULL(WCPHRFirstName,''), IFNULL(WCPHRLastName,''), IFNULL(WCPHRPhoneNumber,''), IFNULL(WCPHRAddress,''), IFNULL(WCPHRCity,''), IFNULL(WCPHRState,''), IFNULL(WCPHRZipCode,''), IFNULL(WCPPlanName,''), IFNULL(WCPCarrierName,''), IFNULL(WCPPayerPhoneNumber,''), IFNULL(WCPCarrierAddress,''), IFNULL(WCPCarrierCity,''), IFNULL(WCPCarrierState,''), IFNULL(WCPCarrierZipCode,''), IFNULL(WCPAdjudicatorFirstName,''), IFNULL(WCPAdjudicatorLastName,''), IFNULL(WCPAdjudicatorPhoneNumber,''), IFNULL(WCPAdjudicatorFaxPhoneNumber,'') from " + Database + ".Patient_WorkCompPolicy where PatientRegId = " + ID;
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
                    stmt.close();
                } catch (Exception e) {
                    System.out.println("Error in Getting Data from Patient_WorkCompPolicy");
                    Services.DumException("DownloadBundle_OLD_15042021", "GetINput Victoria", request, e, this.getServletContext());
                }
            }
            if (MotorVehicleAccidentChk == 1) {
                try {
                    Query = "Select IFNULL(AutoInsuranceInformationChk,'0'), IFNULL(DATE_FORMAT(AIIDateofAccident,'%m/%d/%Y'),''), IFNULL(AIIAutoClaim,''), IFNULL(AIIAccidentLocationAddress,''), IFNULL(AIIAccidentLocationCity,''), IFNULL(AIIAccidentLocationState,''), IFNULL(AIIAccidentLocationZipCode,''), IFNULL(AIIRoleInAccident,''), IFNULL(AIITypeOfAutoIOnsurancePolicy,''), IFNULL(AIIPrefixforReponsibleParty,''), IFNULL(AIIFirstNameforReponsibleParty,''), IFNULL(AIIMiddleNameforReponsibleParty,''), IFNULL(AIILastNameforReponsibleParty,''), IFNULL(AIISuffixforReponsibleParty,''), IFNULL(AIICarrierResponsibleParty,''), IFNULL(AIICarrierResponsiblePartyAddress,''), IFNULL(AIICarrierResponsiblePartyCity,''), IFNULL(AIICarrierResponsiblePartyState,''), IFNULL(AIICarrierResponsiblePartyZipCode,''), IFNULL(AIICarrierResponsiblePartyPhoneNumber,''), IFNULL(AIICarrierResponsiblePartyPolicyNumber,''), IFNULL(AIIResponsiblePartyAutoMakeModel,''), IFNULL(AIIResponsiblePartyLicensePlate,''), IFNULL(AIIFirstNameOfYourPolicyHolder,''), IFNULL(AIILastNameOfYourPolicyHolder,''), IFNULL(AIINameAutoInsuranceOfYourVehicle,''), IFNULL(AIIYourInsuranceAddress,''), IFNULL(AIIYourInsuranceCity,''), IFNULL(AIIYourInsuranceState,''), IFNULL(AIIYourInsuranceZipCode,''), IFNULL(AIIYourInsurancePhoneNumber,''),IFNULL(AIIYourInsurancePolicyNo,''), IFNULL(AIIYourLicensePlate,''), IFNULL(AIIYourCarMakeModelYear,'') from " + Database + ".Patient_AutoInsuranceInfo where PatientRegId = " + ID;
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
                    stmt.close();
                } catch (Exception e) {
                    System.out.println("Error in Getting Data from Patient_AutoInsuranceInfo");
                    Services.DumException("DownloadBundle_OLD_15042021", "GetINput Victoria", request, e, this.getServletContext());
                }
            }
            if (HealthInsuranceChk == 1) {
                try {
                    Query = "Select IFNULL(GovtFundedInsurancePlanChk,'0'), IFNULL(GFIPMedicare,'0'), IFNULL(GFIPMedicaid,'0'), IFNULL(GFIPCHIP,'0'), IFNULL(GFIPTricare,'0'), IFNULL(GFIPVHA,'0'), IFNULL(GFIPIndianHealth,'0'), IFNULL(InsuranceSubPatient,''), IFNULL(InsuranceSubGuarantor,''), IFNULL(InsuranceSubOther,''), IFNULL(HIPrimaryInsurance,''), IFNULL(HISubscriberFirstName,''), IFNULL(HISubscriberLastName,''), IFNULL(HISubscriberDOB,''), IFNULL(HISubscriberSSN,''), IFNULL(HISubscriberRelationtoPatient,''), IFNULL(HISubscriberGroupNo,''), IFNULL(HISubscriberPolicyNo,''), IFNULL(SecondHealthInsuranceChk,''), IFNULL(SHISecondaryName,''), IFNULL(SHISubscriberFirstName,''), IFNULL(SHISubscriberLastName,''), IFNULL(SHISubscriberRelationtoPatient,''), IFNULL(SHISubscriberGroupNo,''), IFNULL(SHISubscriberPolicyNo,'')  from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = " + ID;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
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
                    stmt.close();
                } catch (Exception e) {
                    System.out.println("Error in Getting Data from Patient_HealthInsuraneInfo");
                    Services.DumException("DownloadBundle_OLD_15042021", "GetINput Victoria", request, e, this.getServletContext());
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

            Query = " Select CASE WHEN MFFirstVisit = 1 THEN 'YES' ELSE '' END, " +
                    " CASE WHEN MFReturnPat = 1 THEN 'YES' ELSE '' END, " +
                    " CASE WHEN MFInternetFind = 1 THEN 'YES' ELSE '' END, " +
                    " CASE WHEN Facebook = 1 THEN 'YES' ELSE '' END, " +
                    " CASE WHEN MapSearch = 1 THEN 'YES' ELSE '' END, " +
                    " CASE WHEN GoogleSearch = 1 THEN 'YES' ELSE '' END, " +
                    " CASE WHEN VERWebsite = 1 THEN 'YES' ELSE '' END, " +
                    " CASE WHEN WebsiteAds = 1 THEN 'YES' ELSE '' END," +
                    " CASE WHEN OnlineReviews = 1 THEN 'YES' ELSE '' END," +
                    " CASE WHEN Twitter = 1 THEN 'YES' ELSE '' END," +
                    " CASE WHEN LinkedIn = 1 THEN 'YES' ELSE '' END," +
                    " CASE WHEN EmailBlast = 1 THEN 'YES' ELSE '' END," +
                    " CASE WHEN YouTube = 1 THEN 'YES' ELSE '' END," +
                    " CASE WHEN TV = 1 THEN 'YES' ELSE '' END," +
                    " CASE WHEN Billboard = 1 THEN 'YES' ELSE '' END," +
                    " CASE WHEN Radio = 1 THEN 'YES' ELSE '' END," +
                    " CASE WHEN Brochure = 1 THEN 'YES' ELSE '' END," +
                    " CASE WHEN DirectMail = 1 THEN 'YES' ELSE '' END," +
                    " CASE WHEN CitizensDeTar = 1 THEN 'YES' ELSE '' END," +
                    " CASE WHEN LiveWorkNearby = 1 THEN 'YES' ELSE '' END," +
                    " CASE WHEN FamilyFriend = 1 THEN 'YES' ELSE '' END, IFNULL(FamilyFriend_text,''), " +
                    " CASE WHEN UrgentCare = 1 THEN 'YES' ELSE '' END, IFNULL(UrgentCare_text,''), " +
                    " CASE WHEN NewspaperMagazine = 1 THEN 'YES' ELSE '' END, IFNULL(NewspaperMagazine_text,''), " +
                    " CASE WHEN School = 1 THEN 'YES' ELSE '' END, IFNULL(School_text,''), " +
                    " CASE WHEN Hotel = 1 THEN 'YES' ELSE '' END, IFNULL(Hotel_text,''), IFNULL(MFPhysician,'') " +
                    " FROM " + Database + ".MarketingInfo WHERE PatientRegId = " + ID;
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
            }
            rset.close();
            stmt.close();

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
                /*if (i == 1) {
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
                    pdfContentByte.setTextMatrix(45.0f, 135.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 82.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                }*/
                if (i == 2) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(45.0f, 710.0f);
//                    pdfContentByte.showText(LastName + ", " + FirstName);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(210.0f, 680.0f);
//                    pdfContentByte.showText(LastName + ", " + FirstName);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(45.0f, 630.0f);
//                    pdfContentByte.showText(LastName + ", " + FirstName);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(45.0f, 480.0f);
//                    pdfContentByte.showText(LastName + ", " + FirstName);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60.0f, 355.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
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
                    pdfContentByte2.setTextMatrix(140.0f, 595);
                    pdfContentByte2.showText("Victoria ED");
                    pdfContentByte2.endText();
                }
                if (j == 2) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(90, 470);
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
                    PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(150, 640); // set x and y co-ordinates
                    pdfContentByte2.showText(LastName + ", " + FirstName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(420, 640); // set x and y co-ordinates
                    pdfContentByte2.showText(Date); // add the text
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(170, 610); // set x and y co-ordinates
                    pdfContentByte2.showText(HISubscriberRelationtoPatient); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(430, 610); // set x and y co-ordinates
                    pdfContentByte2.showText(HISubscriberFirstName + " " + HISubscriberLastName); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(250, 580); // set x and y co-ordinates
                    pdfContentByte2.showText(DOS); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(35, 520); // set x and y co-ordinates
                    pdfContentByte2.showText(""); // add the textLos
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252, 520); // set x and y co-ordinates
                    pdfContentByte2.showText(""); // add the textDec
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(35, 492); // set x and y co-ordinates
                    pdfContentByte2.showText(""); // add the textDecDec
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252, 492); // set x and y co-ordinates
                    pdfContentByte2.showText(""); // add the textCar
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(35, 462); // set x and y co-ordinates
                    pdfContentByte2.showText(""); // add the textFur
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252, 462); // set x and y co-ordinates
                    pdfContentByte2.showText(""); // add the textInc
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(35, 432); // set x and y co-ordinates
                    pdfContentByte2.showText(""); // add the textLos
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252, 432); // set x and y co-ordinates
                    pdfContentByte2.showText(""); // add the textQua
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(252, 392); // set x and y co-ordinates
                    pdfContentByte2.showText(""); // add the textBriefly Explain
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(342, 290); // set x and y co-ordinates
                    pdfContentByte2.showText(Date); // add the text
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();

//            /sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/Marketing_Slips_"+ ClientId + "_" + MRN + ".pdf
            inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/Marketing_Slips.pdf";
            outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/Marketing_Slips_" + ClientId + "_" + MRN + ".pdf";
            fos2 = new FileOutputStream(new File(outputFilePathTmp2));
            pdfReader2 = new PdfReader(inputFilePathTmp2);
            pdfStamper2 = new PdfStamper(pdfReader2, (OutputStream) fos2);
            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
                    PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(180, 680); // set x and y co-ordinates
                    pdfContentByte2.showText(MFFirstVisit); // add the text
                    pdfContentByte2.endText();

//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(225, 680); // set x and y co-ordinates
//                    pdfContentByte2.showText("Tick"); // add the text
//                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(430, 680); // set x and y co-ordinates
                    pdfContentByte2.showText(MFReturnPat); // add the text
                    pdfContentByte2.endText();

//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(475, 680); // set x and y co-ordinates
//                    pdfContentByte2.showText("Tick"); // add the text
//                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(205, 660); // set x and y co-ordinates
                    pdfContentByte2.showText(MFInternetFind); // add the text
                    pdfContentByte2.endText();

//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(250, 660); // set x and y co-ordinates
//                    pdfContentByte2.showText("Tick"); // add the text
//                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(43, 612); // set x and y co-ordinates
                    pdfContentByte2.showText(Facebook); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(293, 612); // set x and y co-ordinates
                    pdfContentByte2.showText(TV); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(43, 597); // set x and y co-ordinates
                    pdfContentByte2.showText(MapSearch); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(293, 597); // set x and y co-ordinates
                    pdfContentByte2.showText(Billboard); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(43, 585); // set x and y co-ordinates
                    pdfContentByte2.showText(GoogleSearch); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(293, 585); // set x and y co-ordinates
                    pdfContentByte2.showText(Radio); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(43, 572); // set x and y co-ordinates
                    pdfContentByte2.showText(VERWebsite); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(293, 572); // set x and y co-ordinates
                    pdfContentByte2.showText(FamilyFriend); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(383, 572); // set x and y co-ordinates
                    pdfContentByte2.showText(FamilyFriend_text); // add the text
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(43, 558); // set x and y co-ordinates
                    pdfContentByte2.showText(WebsiteAds); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(293, 558); // set x and y co-ordinates
                    pdfContentByte2.showText(Brochure); // add the text
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(43, 545); // set x and y co-ordinates
                    pdfContentByte2.showText(OnlineReviews); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(293, 545); // set x and y co-ordinates
                    pdfContentByte2.showText(DirectMail); // add the text
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(43, 530); // set x and y co-ordinates
                    pdfContentByte2.showText(Twitter); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(293, 530); // set x and y co-ordinates
                    pdfContentByte2.showText(UrgentCare); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(383, 530); // set x and y co-ordinates
                    pdfContentByte2.showText(UrgentCare_text); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(43, 517); // set x and y co-ordinates
                    pdfContentByte2.showText(LinkedIn); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(293, 517); // set x and y co-ordinates
                    pdfContentByte2.showText(School); // add the text
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(383, 517); // set x and y co-ordinates
                    pdfContentByte2.showText(School_text); // add the text
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(43, 504); // set x and y co-ordinates
                    pdfContentByte2.showText(EmailBlast); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(293, 504); // set x and y co-ordinates
                    pdfContentByte2.showText(NewspaperMagazine); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(453, 504); // set x and y co-ordinates
                    pdfContentByte2.showText(NewspaperMagazine_text); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(293, 491); // set x and y co-ordinates
                    pdfContentByte2.showText(Hotel); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(373, 491); // set x and y co-ordinates
                    pdfContentByte2.showText(Hotel_text); // add the text
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(43, 478); // set x and y co-ordinates
                    pdfContentByte2.showText(YouTube); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(293, 478); // set x and y co-ordinates
                    pdfContentByte2.showText(CitizensDeTar); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(293, 465); // set x and y co-ordinates
                    pdfContentByte2.showText(LiveWorkNearby); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(113, 410); // set x and y co-ordinates
                    pdfContentByte2.showText(MFPhysician); // add the text
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(383, 410); // set x and y co-ordinates
                    pdfContentByte2.showText(PhNumber); // add the text
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();

            ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/GeneralForm_Victoria.pdf";
            if (WorkersCompPolicyChk == 1) {
                //System.out.println("Inside worker comp 1");
                Query = "Select WCPInjuryRelatedAutoMotorAccident, WCPInjuryOccurVehicle from " + Database + ".Patient_WorkCompPolicy where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    WCPInjuryRelatedAutoMotorAccident = rset.getString(1);
                    WCPInjuryOccurVehicle = rset.getString(2);
                }
                rset.close();
                stmt.close();

                //System.out.println("Stage 1");
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/GeneralForm_Victoria.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
//                //System.out.println("Stage 2");
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
//                //System.out.println("Stage 3");
                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_QUESTIONNAIRE_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                if (WCPInjuryRelatedAutoMotorAccident.equals("1") || WCPInjuryOccurVehicle.equals("1")) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_assignmentofproceeds_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                    //System.out.println("Stage 4");
                }
            }
//            else {
//                //System.out.println("ELSE worker comp 0");
//                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/GeneralForm_Victoria.pdf";
//            }
            if (WorkersCompPolicyChk == 0 && MotorVehicleAccidentChk == 1) {
                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MVA_ASSIGNMENTOFPROCEEDS_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
            }
            if (MotorVehicleAccidentChk == 1) {
//                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MVA_ASSIGNMENTOFPROCEEDS_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
//                ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
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
//            else {
//                System.out.println("Inside Else MVA 1");
//                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/GeneralForm_Victoria.pdf";
//            }
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
                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/MEDICAIDSELFPAYAGREEMENT_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                    }
                    if (GFIPMedicare == 1) {
                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/ABNformEnglish_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                    }
                }
                if (HIPrimaryInsurance.trim().toUpperCase().equals("UNITED HEALTHCARE")) {
                    System.out.println("Inside United HealthCare");
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/UHCINSAPPEALFORMS_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                }
            }
//            else {
//                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/GeneralForm_Victoria.pdf";
//            }
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
//            System.out.println("inputFilePath:---"+inputFilePath);
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Victoria/" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf";
            final OutputStream fos3 = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader3 = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper3 = new PdfStamper(pdfReader3, fos3);
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
                    pdfContentByte3.showText(GuarantorEmployerAddress + " " + "" + " " + GuarantorEmployerCity + " " + GuarantorEmployerState);
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
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(410.0f, 80.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 2) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
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
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(380.0f, 270.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 5) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
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
                    pdfContentByte3.setTextMatrix(485, 590.0f);
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
                    pdfContentByte3.setTextMatrix(380, 420.0f);
                    pdfContentByte3.showText("Victoria ED");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(440.0f, 160.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }

                if (k == 8) {
                    PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(220, 425);
                    pdfContentByte3.showText("");//SHISecondaryName
                    pdfContentByte3.endText();

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(155, 400);
                    pdfContentByte3.showText("");//SHISubscriberFirstName++SHISubscriberLastName
                    pdfContentByte3.endText();

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(410, 400);
                    pdfContentByte3.showText("");//SHISubscriberDOB
                    pdfContentByte3.endText();

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(155, 380);
                    pdfContentByte3.showText("");//SHISubscriberPolicyNo
                    pdfContentByte3.endText();

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400, 380);
                    pdfContentByte3.showText("");//SHISubscriberGroupNo
                    pdfContentByte3.endText();

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(115, 158);
                    pdfContentByte3.showText("");//HISubscriberFirstNameHISubscriberLastName
                    pdfContentByte3.endText();

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400, 130);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400, 100);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 9) {
                    PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(230, 150);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }

            }
            pdfStamper3.close();
            File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            FileInputStream fileInputStream = new FileInputStream(pdfFile);
            OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
//            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_QUESTIONNAIRE_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_assignmentofproceeds_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MVACLAIMFORM_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MVA_ASSIGNMENTOFPROCEEDS_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/ABNformEnglish_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/Medicalreleaseform_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/UHCINSAPPEALFORMS_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MEDICAIDSELFPAYAGREEMENT_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/FINANCIAL_HARDSHIP_RELIEF_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/Marketing_Slips_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void SignPdf(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
        try {
            final String pageCount = request.getParameter("pageCount");
            final String outputFilePath = request.getParameter("outputFilePath");
            final String FileName = request.getParameter("FileName");
            final PDFtoImages pdftoImage = new PDFtoImages();
            HashMap<Integer, String> images_Map_final = new HashMap<Integer, String>();
            images_Map_final = (HashMap<Integer, String>) pdftoImage.GetValues(request, out, conn, Database, ClientId, outputFilePath, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/md/tmpImages/");
            final Collection<String> values = images_Map_final.values();
            final List<String> imagelist = new ArrayList<String>(values);
            System.out.println("imagelist Names: " + imagelist);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
            Parser.SetField("imagelist", String.valueOf(imagelist));
            Parser.SetField("pageCount", String.valueOf(pageCount));
            Parser.SetField("FileName", String.valueOf(FileName));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/SigningBundle.html");
        } catch (Exception e) {
            out.println("Error in SignPdf Method: " + e.getMessage());
        }
    }

    public void download_direct(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        final String path = request.getParameter("path");
        final String FileName = request.getParameter("FileName");
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "Inline;filename=" + FileName);
            final FileInputStream fin = new FileInputStream(path);
            final byte[] content = new byte[fin.available()];
            fin.read(content);
            fin.close();
            final OutputStream os = (OutputStream) response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }
}
