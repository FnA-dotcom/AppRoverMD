//
// Decompiled by Procyon v0.5.36
//

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
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
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("Duplicates")
public class FrontlineBundle extends HttpServlet {
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
        Statement stmt = null;
        ResultSet rset = null;
        Connection conn = null;
        String Query = "";
        String ActionID = "";
        final ServletContext context = this.getServletContext();
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        response.setContentType("text/html");
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
        try {
            final HttpSession session = request.getSession(false);
            final boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            final String UserId = session.getAttribute("UserId").toString();
            final String DatabaseName = session.getAttribute("DatabaseName").toString();
            final int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            final String DirectoryName = session.getAttribute("DirectoryName").toString();
            if (UserId.equals("")) {
                final Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                final Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }

            switch (ActionID) {
                case "GETINPUTFrontLine":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "FrontLine Admission Bundle", "Download or View Admission Bundle", FacilityIndex);
                    GETINPUTFrontLine(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName, helper);
                    break;
                case "GETINPUTFrontLine_ForSpanish":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "FrontLine Spanish Admission Bundle", "Download or View Admission Bundle", FacilityIndex);
                    GETINPUTFrontLine_ForSpanish(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName, helper);
                    break;
                case "SignPdf":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Victoria Admission Bundle", "Download or View Admission Bundle", FacilityIndex);
                    this.SignPdf(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName);
                    break;
                case "download_direct":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "GetPDF in Iframe", "DownloadPDF in IFRAME", FacilityIndex);
                    this.download_direct(request, response, out, conn);
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
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            out.flush();
            out.close();
        }
    }

    String GETINPUTFrontLine_Inside(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName, int patientRegId, String SignedFrom, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        String ResultPdf = "";
        final MergePdf mergePdf = new MergePdf();
        String Title = "";
        String FirstName = "";
        String FirstNameNoSpaces = "";
        String LastName = "";
        String MiddleInitial = "";
        String MaritalStatus = "";
        String DOB = "";
        String DOBForAge = "";

        String Age = "";
        String gender = "";
        String Email = "";
        String PhNumber = "";
        String Address = "";
        String StreetAddress2 = "";
        String CityStateZip = "";
        String State = "";
        String City = "";
        final String Country = "";
        String ZipCode = "";
        String SSN = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        String filename = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        String MRN = "";
        int ClientIndex = 0;
        String ClientName = "";
        String DOS = "";
        String DoctorId = null;
        String DoctorName = null;
        int FoundAddInfo = 0;
        String CovidTestDate = "";
        String Ethnicity = "";
        String Race = "";
        String TravellingChk = "";
        String TravelWhen = "";
        String TravelWhere = "";
        String TravelHowLong = "";
        String CovidExpWhen = "";
        String COVIDExposedChk = "";
        String SympFever = "";
        String SympBodyAches = "";
        String SympSoreThroat = "";
        String SympFatigue = "";
        String SympRash = "";
        String SympVomiting = "";
        String SympDiarrhea = "";
        String SympCough = "";
        String SympRunnyNose = "";
        String SympNausea = "";
        String SympFluSymptoms = "";
        String SympHeadache = "";
        String SympLossTaste = "";
        String SympShortBreath = "";
        String SympCongestion = "";
        String SympEyeConjunctivitis = "";
        String GuarantorName = "";
        String GuarantorDOB = "";
        String GuarantorSSN = "";
        int WorkersCompPolicy = 0;
        String WorkersCompPolicyString = "";
        int MotorVehAccident = 0;
        String MotorVehAccidentString = "";
        String PriInsurance = "";
        String MemId = "";
        String GrpNumber = "";
        String PriInsuranceName = "";
        String PriInsurerName = "";
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
        String DirectoryNameTow = "";
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
        String COVIDStatus = "";
        String VisitNumber = "";
        int SelfPayChk = 0;
        int VisitIndex = 0;
        int VerifyChkBox = 0;
        int ID = patientRegId;
        String VisitId = "";
        try {
            Query = "Select id FROM " + Database + ".PatientVisit where PatientRegId =" + ID + " ORDER BY CreatedDate DESC LIMIT 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                VisitIndex = rset.getInt(1);

            }
            rset.close();
            stmt.close();

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
                Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), " +
                        "IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), " +
                        "IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-'), " +
                        "IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), " +
                        "IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, " +
                        "IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), " +
                        "IFNULL(DoctorsName,'-'),  " +
                        "CASE WHEN Ethnicity = 1 THEN 'Hispanic or Latino' WHEN Ethnicity = 2 THEN ' Non Hispanic or Latino' " +
                        "WHEN Ethnicity = 3 THEN 'Others' ELSE 'Others' END, IFNULL(City,''), IFNULL(State,''), IFNULL(ZipCode,''), " +
                        "IFNULL(COVIDStatus,''), IFNULL(StreetAddress2,''),IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'),'')  From " + Database + ".PatientReg  Where ID = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
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
                    Ethnicity = rset.getString(24);
                    City = rset.getString(25);
                    State = rset.getString(26);
                    ZipCode = rset.getString(27);
                    COVIDStatus = rset.getString(28);
                    StreetAddress2 = rset.getString(29);
                    DOBForAge = rset.getString(30);
                }
                rset.close();
                stmt.close();

                if (!DOB.equals("")) {
                    Age = String.valueOf(helper.getAge(LocalDate.parse(DOBForAge)));
                }

                Query = "Select name from oe.clients where Id = " + ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClientName = rset.getString(1);
                }
                rset.close();
                stmt.close();

                if (!DoctorId.equals("-")) {
                    Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        DoctorName = rset.getString(1);
                    }
                    rset.close();
                    stmt.close();
                } else {
                    DoctorName = "";
                }
            } catch (Exception e) {
                out.println("Error In PateintReg:--" + e.getMessage());
                out.println(Query);
            }
            String CovidTest = "";
            Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundAddInfo = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (FoundAddInfo > 0) {
//                Query = "Select IFNULL(Date_format(CovidTestDate,'%m/%d/%Y'),'') from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
                Query = "Select CASE WHEN COVIDPositveChk = 1 THEN 'YES' WHEN COVIDPositveChk = 0 THEN 'NO' ELSE 'NO' END," +
                        "IFNULL(Date_format(CovidPositiveDate,'%m/%d/%Y'),'') " +
                        "from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    CovidTest = rset.getString(1);
                    CovidTestDate = rset.getString(2);
                }
                rset.close();
                stmt.close();
            }
            try {
                Query = " Select CASE WHEN TravellingChk = 1 THEN 'YES' WHEN TravellingChk = 0 THEN 'NO' ELSE 'NO' END,  " +
                        " IFNULL(DATE_FORMAT(TravelWhen,'%m/%d/%Y'),''),  IFNULL(TravelWhere,''), IFNULL(TravelHowLong,''), " +
                        " CASE WHEN COVIDExposedChk = 1 THEN 'YES' WHEN COVIDExposedChk = 0 THEN 'NO' ELSE 'NO' END,  " +
                        " CASE WHEN SympFever = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympBodyAches = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympSoreThroat = 1 THEN 'YES' ELSE '' END,  " +
                        " CASE WHEN SympFatigue = 1 THEN 'YES' ELSE '' END," +
                        " CASE WHEN SympRash = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympVomiting = 1 THEN 'YES' ELSE '' END,  " +
                        " CASE WHEN SympDiarrhea = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympCough = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympRunnyNose = 1 THEN 'YES' ELSE '' END,  " +
                        " CASE WHEN SympNausea = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympFluSymptoms = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympEyeConjunctivitis = 1 THEN 'YES' ELSE '' END,  " +
                        " CASE WHEN Race = 1 THEN 'African American' WHEN Race = 2 THEN 'American Indian or Alska Native' " +
                        " WHEN Race = 3 THEN 'Asian' WHEN Race = 4 THEN 'Native Hawaiian or Other Pacific Islander'  " +
                        " WHEN Race = 5 THEN 'White' WHEN Race = 6 THEN 'Others' ELSE 'Others' END, IFNULL(DATE_FORMAT(CovidExpWhen,'%m/%d/%Y'),''), " +
                        " CASE WHEN SympHeadache = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympLossTaste = 1 THEN 'YES' ELSE '' END," +
                        " CASE WHEN SympShortBreath = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympCongestion = 1 THEN 'YES' ELSE '' END, " +
                        " CONCAT(IFNULL(GuarantorFirstName,''),' ',IFNULL(GuarantorLastName,'')), IFNULL(DATE_FORMAT(GuarantorDOB,'%m/%d/%Y'),''),IFNULL(GuarantorSSN,'')" +
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
                    SympHeadache = rset.getString(20);
                    SympLossTaste = rset.getString(21);
                    SympShortBreath = rset.getString(22);
                    SympCongestion = rset.getString(23);
                    GuarantorName = rset.getString(24);
                    GuarantorDOB = rset.getString(25);
                    GuarantorSSN = rset.getString(26);
                }
                rset.close();
                stmt.close();
            } catch (Exception e2) {
                out.println("Error in getting PatientReg_Details Table : " + e2.getMessage());
            }
            if (SelfPayChk == 1) {
                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-')," +
                        "IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), " +
                        "IFNULL(DATE_FORMAT(PrimaryDOB,'%m/%d/%Y'),'-'), IFNULL(PrimarySSN,'-'),  " +
                        "IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), " +
                        "IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), " +
                        "CONCAT(IFNULL(SubscriberFirstName,'-'),' ',IFNULL(SubscriberLastName,'-')), " +
                        "IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), " +
                        "IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-')," +
                        "CONCAT( IFNULL(PriInsurerFirstName,''),' ', IFNULL(PriInsurerLastName,'')) " +
                        "from " + Database + ".InsuranceInfo  where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    WorkersCompPolicy = rset.getInt(1);
                    MotorVehAccident = rset.getInt(2);
                    if (WorkersCompPolicy == 0) {
                        WorkersCompPolicyString = "N";
                    } else {
                        WorkersCompPolicyString = "Y";
                    }
                    if (MotorVehAccident == 0) {
                        MotorVehAccidentString = "N";
                    } else {
                        MotorVehAccidentString = "Y";
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
                    PriInsurerName = rset.getString(21);
                }
                rset.close();
                stmt.close();
            }
            if (!PriInsuranceName.equals("")) {
                Query = "Select IFNULL(PayerName,'') from " + Database + ".ProfessionalPayers where id = " + PriInsuranceName;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PriInsuranceName = rset.getString(1);
                }
                rset.close();
                stmt.close();
            }
            Query = "Select IFNULL(NextofKinName,'-'), IFNULL(RelationToPatient,'-'), IFNULL(PhoneNumber,'-'), " +
                    "CASE WHEN LeaveMessage = 1 THEN 'YES' WHEN LeaveMessage = 0 THEN 'NO' ELSE ' YES / NO' END,  " +
                    "IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-') " +
                    "from " + Database + ".EmergencyInfo where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                NextofKinName = rset.getString(1);
                RelationToPatientER = rset.getString(2);
                PhoneNumberER = rset.getString(3);
                LeaveMessageERString = rset.getString(4);
                AddressER = rset.getString(5);
                CityStateZipER = rset.getString(6);
            }
            rset.close();
            stmt.close();

            Query = "Select MAX(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                VisitNumber = rset.getString(1).trim();
            }
            rset.close();
            stmt.close();

            VisitNumber = "VN-" + MRN + "-" + VisitNumber;
            Query = " Select ReturnPatient, Google, MapSearch, Billboard, OnlineReview, TV, Website, BuildingSignDriveBy, " +
                    "Facebook, School, IFNULL(School_text ,'-'), Twitter, Magazine, IFNULL(Magazine_text,'-'), " +
                    "Newspaper, IFNULL(Newspaper_text,'-'), FamilyFriend, IFNULL(FamilyFriend_text,'-'), UrgentCare, " +
                    "IFNULL(UrgentCare_text,'-'), CommunityEvent, IFNULL(CommunityEvent_text,'-'),  IFNULL(Work_text,'-')," +
                    "IFNULL(Physician_text, '-'), IFNULL(Other_text,'-') " +
                    "from " + Database + ".RandomCheckInfo where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
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

            String HearAboutUsString = "";
            if (ReturnPatient.toUpperCase().equals("YES")) {
                HearAboutUsString += "Return Patient, ";
            }
            if (Google.toUpperCase().equals("YES")) {
                HearAboutUsString += "Google, ";
            }
            if (MapSearch.toUpperCase().equals("YES")) {
                HearAboutUsString += "Map Search, ";
            }
            if (OnlineReview.toUpperCase().equals("YES")) {
                HearAboutUsString += "Online Review, ";
            }
            if (TV.toUpperCase().equals("YES")) {
                HearAboutUsString += "TV, ";
            }
            if (Website.toUpperCase().equals("YES")) {
                HearAboutUsString += "Website, ";
            }
            if (BuildingSignDriveBy.toUpperCase().equals("YES")) {
                HearAboutUsString += "Building Sign, ";
            }
            if (Facebook.toUpperCase().equals("YES")) {
                HearAboutUsString += "Facebook, ";
            }
            if (School.toUpperCase().equals("YES")) {
                HearAboutUsString += "School, ";
            }
            if (Twitter.toUpperCase().equals("YES")) {
                HearAboutUsString += "Twitter, ";
            }
            if (Magazine.toUpperCase().equals("YES")) {
                HearAboutUsString += "Magazine, ";
            }
            if (Newspaper.toUpperCase().equals("YES")) {
                HearAboutUsString += "Newspaper, ";
            }
            if (FamilyFriend.toUpperCase().equals("YES")) {
                HearAboutUsString += "Friend / Family, ";
            }
            if (UrgentCare.toUpperCase().equals("YES")) {
                HearAboutUsString += "Urgent Care, ";
            }
            if (CommunityEvent.toUpperCase().equals("YES")) {
                HearAboutUsString += "Comminuty Event, ";
            }
            if ("".toUpperCase().equals("YES")) {
                HearAboutUsString += "Work, ";
            }
            if ("".toUpperCase().equals("YES")) {
                HearAboutUsString += "Physician, ";
            }
            if ("".toUpperCase().equals("YES")) {
                HearAboutUsString += "Others ";
            }
            String SymptomsString = "";
            if (SympFever.toUpperCase().equals("YES")) {
                SymptomsString += "Fever, ";
            }
            if (SympBodyAches.toUpperCase().equals("YES")) {
                SymptomsString += "Body Aches, ";
            }
            if (SympSoreThroat.toUpperCase().equals("YES")) {
                SymptomsString += "Sore Throat, ";
            }
            if (SympFatigue.toUpperCase().equals("YES")) {
                SymptomsString += "Fatigue, ";
            }
            if (SympRash.toUpperCase().equals("YES")) {
                SymptomsString += "Rash, ";
            }
            if (SympVomiting.toUpperCase().equals("YES")) {
                SymptomsString += "Vomitting, ";
            }
            if (SympDiarrhea.toUpperCase().equals("YES")) {
                SymptomsString += "Diarrhea, ";
            }
            if (SympCough.toUpperCase().equals("YES")) {
                SymptomsString += "Cough, ";
            }
            if (SympRunnyNose.toUpperCase().equals("YES")) {
                SymptomsString += "Runny Nose, ";
            }
            if (SympNausea.toUpperCase().equals("YES")) {
                SymptomsString += "Nausea, ";
            }
            if (SympFluSymptoms.toUpperCase().equals("YES")) {
                SymptomsString += "Flu-like Symptoms, ";
            }
            if (SympEyeConjunctivitis.toUpperCase().equals("YES")) {
                SymptomsString += "Eye Conjunctivitis, ";
            }
            if (SympHeadache.toUpperCase().equals("YES")) {
                SymptomsString += "Headache, ";
            }
            if (SympLossTaste.toUpperCase().equals("YES")) {
                SymptomsString += "Loss of Taste/Smell, ";
            }
            if (SympShortBreath.toUpperCase().equals("YES")) {
                SymptomsString += "Short of Breathness, ";
            }
            if (SympCongestion.toUpperCase().equals("YES")) {
                SymptomsString += "Congestion ";
            }

            String inputFilePathTmp = "";
            String outputFilePathTmp = "";
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/FinancialHardShip.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf";
            OutputStream fos1 = new FileOutputStream(new File(outputFilePathTmp));
            PdfReader pdfReader1 = new PdfReader(inputFilePathTmp);
            PdfStamper pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(130.0f, 565.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);//patname
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(360.0f, 565.0f);
                    pdfContentByte.showText(DOS);//date req
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 540.0f);
                    pdfContentByte.showText("");//realtion
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 540.0f);
                    pdfContentByte.showText("");//guardian name
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0f, 512.0f);
                    pdfContentByte.showText(DOS);//DOS
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(75.0f, 462.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(75.0f, 434.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(75.0f, 408.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(75.0f, 380.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(245.0f, 462.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(245.0f, 434.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(245.0f, 408.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(245.0f, 380.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 308.0f);
                    pdfContentByte.showText("");//Other
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100.0f, 255.0f);
                    pdfContentByte.showText("");//recibido
                    pdfContentByte.endText();

//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(100.0f, 200.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }

//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(100.0f, 205.0f);
//                pdfContentByte2.showText("sign");
//                pdfContentByte2.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380.0f, 205.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420.0f, 143.0f);
                    pdfContentByte.showText("");//Dailylog
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(530.0f, 143.0f);
                    pdfContentByte.showText("");//By
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/PromptPayAgreement.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 425.0f);
                    pdfContentByte.showText("");//CovidExpWhen
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(30.0f, 258.0f);
                    pdfContentByte.showText("");//DOS//recibido
                    pdfContentByte.endText();

//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(50.0f, 210.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }

//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(100.0f, 215.0f);
//                pdfContentByte2.showText("Sign");//DOS
//                pdfContentByte2.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275.0f, 215.0f);
                    pdfContentByte.showText(DOS);//DOS
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();

            String pattern = null;
            Query = "SELECT Form_ids FROM " + Database + ".BundleForms where PatientRegId='" + ID + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                pattern = rset.getString(1);
            }
            rset.close();
            stmt.close();

            if (pattern != null) {
                String[] pat = pattern.split("\\^");//pat[0] -> mva, pat[1] -> prompt pay, pat[2] -> Financial Harship

                if (pat[0].equals("1")) {//mva
                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
                }

                if (pat[2].equals("1")) {// financial hardship
                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    if (ClientId == 27) {
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
                    } else {
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
                    }

                    if (pat[1].equals("1")) { //prompt pay
                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                        if (ClientId == 27) {
                            ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
                        } else {
                            ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
                        }
                    }

                }

                if (pat[1].equals("1") && !(pat[2].equals("1"))) {//prompt pay
                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    if (ClientId == 27) {
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
                    } else {
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
                    }
                }
            } else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
            }


            if (PriInsuranceName.toUpperCase().contains("UNITED HEALTHCARE")) {
                String inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/Commercial-Courtesy-Review-Auth-Form.pdf";
                String outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/Commercial-Courtesy-Review-Auth-Form_" + ClientId + "_" + MRN + ".pdf";

                ResultPdf = AttachUHC_Form(MemId, PrimaryDOB, PriInsurerName, DOS, PatientRelationtoPrimary, Date, outputFilePathTmp2, inputFilePathTmp2,
                        request, response, out, conn, Database, ResultPdf, DirectoryName, ClientId, MRN, mergePdf);
            }

            int found = 0;
            Query = "Select Count(*) from " + Database + ".BundleHistory where PatientRegId=" + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                found = rset.getInt(1);
            }
            stmt.close();
            rset.close();

            if (SignedFrom.contains("REGISTRATION")) {
                DirectoryNameTow = "REGISTRATION";
            } else if (SignedFrom.contains("VISIT")) {
                DirectoryNameTow = "VISIT";
            } else if (SignedFrom.contains("EDIT")) {
                DirectoryNameTow = "EDIT";
            }

            filename = FirstNameNoSpaces + LastName + ID + "_" + found + "_" + SignedFrom + ".pdf";

            String inputFilePath = "";
            String outputFilePath = "";
            String UID = "";
            inputFilePath = ResultPdf;
            outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + DirectoryNameTow + "/" + filename;
            final OutputStream fos2 = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader2 = new PdfReader(inputFilePath);
            final int pageCount = pdfReader2.getNumberOfPages();
            final PdfStamper pdfStamper2 = new PdfStamper(pdfReader2, fos2);
            Image SignImages = null;
            final File tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_0_" + ID + ".png");
            final boolean exists = tmpDir.exists();
            if (exists) {
                Query = "Select UID from " + Database + ".SignRequest where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    UID = rset.getString(1);
                }
                rset.close();
                stmt.close();

                SignImages = Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_0_" + ID + ".png");
                SignImages.scaleAbsolute(80.0f, 30.0f);
            } else {
                SignImages = null;
            }

            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(40.0f, 770.0f);
//                    pdfContentByte2.showText(UID);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(130.0f, 685.0f);
//                    pdfContentByte2.showText(ReasonVisit);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(392.0f, 685.0f);
//                    pdfContentByte2.showText(MotorVehAccidentString);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(456.0f, 685.0f);
//                    pdfContentByte2.showText(WorkersCompPolicyString);
//                    pdfContentByte2.endText();
//                    if (SelfPayChk == 1) {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("(Y)");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("(N)");
//                        pdfContentByte2.endText();
//                    } else if (SelfPayChk == 0) {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("(N)");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("(Y)");
//                        pdfContentByte2.endText();
//                    } else {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("");
//                        pdfContentByte2.endText();
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 660.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 660.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(520.0f, 660.0f);
//                    pdfContentByte2.showText(Age);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 620.0f);
//                    pdfContentByte2.showText(Address);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(300.0f, 620.0f);
//                    pdfContentByte2.showText(StreetAddress2);//ApptNo
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(460.0f, 620.0f);
//                    pdfContentByte2.showText(SSN);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 595.0f);
//                    pdfContentByte2.showText(City);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(260.0f, 595.0f);
//                    pdfContentByte2.showText(State);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 595.0f);
//                    pdfContentByte2.showText(ZipCode);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(110.0f, 575.0f);
//                    pdfContentByte2.showText(PhNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(250.0f, 575.0f);
//                    pdfContentByte2.showText(PhNumber);
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 575.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(100.0f, 550.0f);
//                    pdfContentByte2.showText(PrimaryEmployer);
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(395.0f, 550.0f);
//                    pdfContentByte2.showText(PrimaryOccupation);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 527.0f);
//                    pdfContentByte2.showText(EmployerAddress);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(409.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(500.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(105.0f, 505.0f);
//                    pdfContentByte2.showText(MaritalStatus);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(250.0f, 505.0f);
//                    pdfContentByte2.showText(gender);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 505.0f);
//                    pdfContentByte2.showText(Email);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 485.0f);
//                    pdfContentByte2.showText(NextofKinName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(360.0f, 485.0f);
//                    pdfContentByte2.showText(PhoneNumberER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(520.0f, 485.0f);
//                    pdfContentByte2.showText(RelationToPatientER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(100.0f, 463.0f);
//                    pdfContentByte2.showText(AddressER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(455.0f, 463.0f);
//                    pdfContentByte2.showText(" ,  , ");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(310.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorDOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(480.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorSSN);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 360.0f);
//                    pdfContentByte2.showText(PriInsurerName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(310.0f, 360);
//                    pdfContentByte2.showText(PrimaryDOB);//primary DOB
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(480.0f, 360);
//                    pdfContentByte2.showText(PrimarySSN);//Primary SSN
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 335.0f);
//                    pdfContentByte2.showText(PriInsuranceName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(445.0f, 335.0f);
//                    pdfContentByte2.showText(MemId);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(540.0f, 335.0f);
//                    pdfContentByte2.showText(GrpNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 223.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 195.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(200.0f, 215.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 192.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 134.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 123.0f);
//                    pdfContentByte2.showText(ClientName + "  Sex: " + gender);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 113.0f);
//                    pdfContentByte2.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 103.0f);
//                    pdfContentByte2.showText("MRN:" + MRN + "  DOS:" + DOS + "");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 93.0f);
//                    pdfContentByte2.showText("ACT#: " + VisitNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 83.0f);
//                    pdfContentByte2.showText("Dr. " + DoctorName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 2) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(190.0f, 142.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(425.0f, 100.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(170.0f, 90.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(425.0f, 83.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(430.0f, 63.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 3) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 613.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 583.0f);
//                    pdfContentByte2.showText(Address);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 550.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(370.0f, 550.0f);
//                    pdfContentByte2.showText(SSN);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(110.0f, 140.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(430.0f, 150.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 4) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(180.0f, 140.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 140.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 5) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(190.0f, 335.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(220.0f, 275.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 285.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 252.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 6) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 530.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 530.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 490.0f);
//                    pdfContentByte2.showText(PriInsuranceName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(170.0f, 446.0f);
//                    pdfContentByte2.showText(MemId);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 446.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(210.0f, 200.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 216.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 7) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 473.0f);
//                    pdfContentByte2.showText(CovidTest);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 473.0f);
//                    pdfContentByte2.showText(CovidTestDate);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 442.0f);
//                    pdfContentByte2.showText(COVIDExposedChk);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(525.0f, 442.0f);
//                    pdfContentByte2.showText(CovidExpWhen);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(325.0f, 415.0f);
//                    pdfContentByte2.showText(SymptomsString);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 390.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(150.0f, 110.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 115.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(110.0f, 65.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 65.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();


                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(150.0f, 655.0f);
//                pdfContentByte2.showText(ReasonVisit);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(90.0f, 620.0f);
//                pdfContentByte2.showText(LastName + ", " + FirstName);//name
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(385.0f, 620.0f);
//                pdfContentByte2.showText(DOB);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(515.0f, 620.0f);
//                pdfContentByte2.showText(Age);
//                pdfContentByte2.endText();
//
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(90.0f, 585.0f);
//                pdfContentByte2.showText(Address);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(290.0f, 585.0f);
//                pdfContentByte2.showText(PhNumber);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(490.0f, 585.0f);
//                pdfContentByte2.showText(SSN);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(100.0f, 550.0f);
//                pdfContentByte2.showText(City);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(300.0f, 550.0f);
//                pdfContentByte2.showText(State);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(475.0f, 550.0f);
//                pdfContentByte2.showText(ZipCode);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(110.0f, 515.0f);
//                pdfContentByte2.showText(PhNumber);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(400.0f, 515.0f);
//                pdfContentByte2.showText(PhNumber);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(100.0f, 480.0f);
//                pdfContentByte2.showText(gender);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(300.0f, 480.0f);
//                pdfContentByte2.showText(Email);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(250.0f, 445.0f);
//                pdfContentByte2.showText(NextofKinName);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(405.0f, 445.0f);
//                pdfContentByte2.showText(PhoneNumberER);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(520.0f, 445.0f);
//                pdfContentByte2.showText(RelationToPatientER);
//                pdfContentByte2.endText();
//
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(200.0f, 350.0f);
//                pdfContentByte2.showText(GuarantorName);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(405.0f, 350.0f);
//                pdfContentByte2.showText(GuarantorDOB);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(520.0f, 350.0f);
//                pdfContentByte2.showText("");//Phone
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(200.0f, 315.0f);
//                pdfContentByte2.showText(PriInsurerName);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(370.0f, 315.0f);
//                pdfContentByte2.showText(PrimaryDOB);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(505.0f, 315.0f);
//                pdfContentByte2.showText(PrimarySSN);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(200.0f, 280.0f);
//                pdfContentByte2.showText(PriInsuranceName);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(405.0f, 280.0f);
//                pdfContentByte2.showText(MemId);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(505.0f, 280.0f);
//                pdfContentByte2.showText(GrpNumber);
//                pdfContentByte2.endText();
//
////                pdfContentByte2.beginText();
////                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
////                pdfContentByte2.setColorFill(BaseColor.BLACK);
////                pdfContentByte2.setTextMatrix(210.0f, 220);
////                pdfContentByte2.showText("pat/guar signature");
////                pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(210.0f, 210.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(445.0f, 220.0f);
//                pdfContentByte2.showText(DOS);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(445.0f, 110.0f);
//                pdfContentByte2.showText("");//witness
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(518.0f, 110.0f);
//                pdfContentByte2.showText(DOS);
//                pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//
//            }
//            if (j == 2) {
//                final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(190.0f, 300.0f);
//                pdfContentByte2.showText("");//spouse/fam
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(30.0f, 195.0f);
//                pdfContentByte2.showText("");//recibido
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(150.0f, 175.0f);
//                pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(445.0f, 175.0f);
//                pdfContentByte2.showText(DOS);
//                pdfContentByte2.endText();
//
//                if (SignImages != null) {
//                    SignImages.setAbsolutePosition(150.0f, 140.0f);
//                    pdfContentByte2.addImage(SignImages);
//                }
//
////                pdfContentByte2.beginText();
////                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
////                pdfContentByte2.setColorFill(BaseColor.BLACK);
////                pdfContentByte2.setTextMatrix(150.0f, 145.0f);
////                pdfContentByte2.showText("sign");
////                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(375.0f, 145.0f);
//                pdfContentByte2.showText("");//guraantor rel
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(465.0f, 145.0f);
//                pdfContentByte2.showText(DOS);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(250.0f, 120.0f);
//                pdfContentByte2.showText("");//"sign of consent
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(470.0f, 120.0f);
//                pdfContentByte2.showText(DOS);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(445.0f, 83.0f);
//                pdfContentByte2.showText("");//witness
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(518.0f, 83.0f);
//                pdfContentByte2.showText(DOS);
//                pdfContentByte2.endText();
//
//
//
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                pdfContentByte2.endText();
//            }
//            if (j == 3) {
//                final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(140.0f, 550.0f);
//                pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(140.0f, 510.0f);
//                pdfContentByte2.showText(DOB);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(470.0f, 510.0f);
//                pdfContentByte2.showText(DOS);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(165.0f, 430.0f);
//                pdfContentByte2.showText("");//check box
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(140.0f, 355.0f);
//                pdfContentByte2.showText(Address);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(140.0f, 284.0f);
//                pdfContentByte2.showText("");//fax
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(500.0f, 284.0f);
//                pdfContentByte2.showText("");//phone
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(100.0f, 228.0f);
//                pdfContentByte2.showText("");//authorizarion
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(30.0f, 157.0f);
//                pdfContentByte2.showText("");//recibido
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(390.0f, 127.0f);
//                pdfContentByte2.showText(DOS);
//                pdfContentByte2.endText();
//
//                if (SignImages != null) {
//                    SignImages.setAbsolutePosition(100.0f, 120.0f);
//                    pdfContentByte2.addImage(SignImages);
//                }
//
////                pdfContentByte2.beginText();
////                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
////                pdfContentByte2.setColorFill(BaseColor.BLACK);
////                pdfContentByte2.setTextMatrix(100.0f, 127.0f);
////                pdfContentByte2.showText("sign");
////                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);                pdfContentByte2.endText();
//            }
//            if (j == 4) {
//                final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(30.0f, 318.0f);
//                pdfContentByte2.showText("");//recibedo
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(150.0f, 292.0f);
//                pdfContentByte2.showText("");//Patname
//                pdfContentByte2.endText();
//
//                if (SignImages != null) {
//                    SignImages.setAbsolutePosition(150.0f, 247.0f);
//                    pdfContentByte2.addImage(SignImages);
//                }
//
////                pdfContentByte2.beginText();
////                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
////                pdfContentByte2.setColorFill(BaseColor.BLACK);
////                pdfContentByte2.setTextMatrix(150.0f, 247.0f);
////                pdfContentByte2.showText("");//sign
////                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(370.0f, 247.0f);
//                pdfContentByte2.showText(DOS);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(440.0f, 175.0f);
//                pdfContentByte2.showText("");//witness
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(520.0f, 175.0f);
//                pdfContentByte2.showText(DOS);
//                pdfContentByte2.endText();
//
//
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                pdfContentByte2.endText();
//            }
//            if (j == 5) {
//                final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(130.0f, 560.0f);
//                pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(345.0f, 560.0f);
//                pdfContentByte2.showText(DOB);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(130.0f, 525.0f);
//                pdfContentByte2.showText(PriInsuranceName);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(130.0f, 485.0f);
//                pdfContentByte2.showText(MemId);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(335.0f, 485.0f);
//                pdfContentByte2.showText(DOS);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(80.0f, 400.0f);
//                pdfContentByte2.showText("");//vow
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(500.0f, 410.0f);
//                pdfContentByte2.showText("");//recibido
//                pdfContentByte2.endText();
//
//
//                if (SignImages != null) {
//                    SignImages.setAbsolutePosition(130.0f, 305.0f);
//                    pdfContentByte2.addImage(SignImages);
//                }
//
////                pdfContentByte2.beginText();
////                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
////                pdfContentByte2.setColorFill(BaseColor.BLACK);
////                pdfContentByte2.setTextMatrix(130.0f, 312.0f);
////                pdfContentByte2.showText("sign");
////                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(325.0f, 312.0f);
//                pdfContentByte2.showText(DOS);
//                pdfContentByte2.endText();
//
//
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                pdfContentByte2.endText();
//            }

//            if (j == 6) {
//
//                final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(130.0f, 565.0f);
//                pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);//patname
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(360.0f, 565.0f);
//                pdfContentByte2.showText(DOS);//date req
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(160.0f, 540.0f);
//                pdfContentByte2.showText("");//realtion
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(400.0f, 540.0f);
//                pdfContentByte2.showText("");//guardian name
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(150.0f, 512.0f);
//                pdfContentByte2.showText(DOS);//DOS
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(75.0f, 462.0f);
//                pdfContentByte2.showText("");//Y
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(75.0f, 434.0f);
//                pdfContentByte2.showText("");//Y
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(75.0f, 408.0f);
//                pdfContentByte2.showText("");//Y
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(75.0f, 380.0f);
//                pdfContentByte2.showText("");//Y
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(245.0f, 462.0f);
//                pdfContentByte2.showText("");//Y
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(245.0f, 434.0f);
//                pdfContentByte2.showText("");//Y
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(245.0f, 408.0f);
//                pdfContentByte2.showText("");//Y
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(245.0f, 380.0f);
//                pdfContentByte2.showText("");//Y
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(200.0f, 308.0f);
//                pdfContentByte2.showText("");//Other
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(100.0f, 255.0f);
//                pdfContentByte2.showText("");//recibido
//                pdfContentByte2.endText();
//
//                if (SignImages != null) {
//                    SignImages.setAbsolutePosition(100.0f, 200.0f);
//                    pdfContentByte2.addImage(SignImages);
//                }
//
////                pdfContentByte2.beginText();
////                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
////                pdfContentByte2.setColorFill(BaseColor.BLACK);
////                pdfContentByte2.setTextMatrix(100.0f, 205.0f);
////                pdfContentByte2.showText("sign");
////                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(380.0f, 205.0f);
//                pdfContentByte2.showText(DOS);
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(420.0f, 143.0f);
//                pdfContentByte2.showText("");//Dailylog
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(530.0f, 143.0f);
//                pdfContentByte2.showText("");//By
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                pdfContentByte2.endText();
//            }
//            if (j == 7) {
//                final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(430.0f, 425.0f);
//                pdfContentByte2.showText("");//CovidExpWhen
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(30.0f, 258.0f);
//                pdfContentByte2.showText("");//DOS//recibido
//                pdfContentByte2.endText();
//
//                if (SignImages != null) {
//                    SignImages.setAbsolutePosition(50.0f, 210.0f);
//                    pdfContentByte2.addImage(SignImages);
//                }
//
////                pdfContentByte2.beginText();
////                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
////                pdfContentByte2.setColorFill(BaseColor.BLACK);
////                pdfContentByte2.setTextMatrix(100.0f, 215.0f);
////                pdfContentByte2.showText("Sign");//DOS
////                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(275.0f, 215.0f);
//                pdfContentByte2.showText(DOS);//DOS
//                pdfContentByte2.endText();
//
//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                pdfContentByte2.endText();
//                }


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(150.0f, 655.0f);
                    pdfContentByte2.showText(ReasonVisit);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(90.0f, 620.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName);//name
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(385.0f, 620.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(515.0f, 620.0f);
                    pdfContentByte2.showText(Age);
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(90.0f, 585.0f);
                    pdfContentByte2.showText(Address);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(290.0f, 585.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(490.0f, 585.0f);
                    pdfContentByte2.showText(SSN);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 550.0f);
                    pdfContentByte2.showText(City);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(300.0f, 550.0f);
                    pdfContentByte2.showText(State);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(475.0f, 550.0f);
                    pdfContentByte2.showText(ZipCode);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(110.0f, 515.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 515.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 480.0f);
                    pdfContentByte2.showText(gender);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(300.0f, 480.0f);
                    pdfContentByte2.showText(Email);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(250.0f, 445.0f);
                    pdfContentByte2.showText(NextofKinName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(405.0f, 445.0f);
                    pdfContentByte2.showText(PhoneNumberER);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(520.0f, 445.0f);
                    pdfContentByte2.showText(RelationToPatientER);
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(200.0f, 350.0f);
                    pdfContentByte2.showText(GuarantorName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(405.0f, 350.0f);
                    pdfContentByte2.showText(GuarantorDOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(520.0f, 350.0f);
                    pdfContentByte2.showText("");//Phone
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(200.0f, 315.0f);
                    pdfContentByte2.showText(PriInsurerName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(345.0f, 315.0f);
                    pdfContentByte2.showText(PrimaryDOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(505.0f, 315.0f);
                    pdfContentByte2.showText(PrimarySSN);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(165.0f, 280.0f);
                    pdfContentByte2.showText(PriInsuranceName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(405.0f, 280.0f);
                    pdfContentByte2.showText(MemId);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(505.0f, 280.0f);
                    pdfContentByte2.showText(GrpNumber);
                    pdfContentByte2.endText();

//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(210.0f, 220);
//                pdfContentByte2.showText("pat/guar signature");
//                pdfContentByte2.endText();
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(210.0f, 210.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(445.0f, 220.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(445.0f, 110.0f);
                    pdfContentByte2.showText("");//witness
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(518.0f, 110.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();

                }
                if (j == 2) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(190.0f, 300.0f);
                    pdfContentByte2.showText("");//spouse/fam
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(30.0f, 195.0f);
                    pdfContentByte2.showText("");//recibido
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(150.0f, 175.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(445.0f, 175.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(150.0f, 140.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(150.0f, 145.0f);
//                pdfContentByte2.showText("sign");
//                pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(375.0f, 145.0f);
                    pdfContentByte2.showText("");//guraantor rel
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(465.0f, 145.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(250.0f, 120.0f);
                    pdfContentByte2.showText("");//"sign of consent
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(470.0f, 120.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(445.0f, 83.0f);
                    pdfContentByte2.showText("");//witness
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(518.0f, 83.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();
                }
                if (j == 3) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 550.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 510.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(470.0f, 510.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(165.0f, 430.0f);
                    pdfContentByte2.showText("");//check box
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 355.0f);
                    pdfContentByte2.showText(Address);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 284.0f);
                    pdfContentByte2.showText("");//fax
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(500.0f, 284.0f);
                    pdfContentByte2.showText("");//phone
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 228.0f);
                    pdfContentByte2.showText("");//authorizarion
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(30.0f, 157.0f);
                    pdfContentByte2.showText("");//recibido
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(390.0f, 127.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100.0f, 120.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(100.0f, 127.0f);
//                pdfContentByte2.showText("sign");
//                pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();
                }
                if (j == 4) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(30.0f, 318.0f);
                    pdfContentByte2.showText("");//recibedo
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(150.0f, 292.0f);
                    pdfContentByte2.showText("");//Patname
                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(150.0f, 247.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(150.0f, 247.0f);
//                pdfContentByte2.showText("");//sign
//                pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(370.0f, 247.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(440.0f, 175.0f);
                    pdfContentByte2.showText("");//witness
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(520.0f, 175.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();
                }
                if (j == 5) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(130.0f, 560.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(345.0f, 560.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(130.0f, 525.0f);
                    pdfContentByte2.showText(PriInsuranceName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(130.0f, 485.0f);
                    pdfContentByte2.showText(MemId);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(335.0f, 485.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 400.0f);
                    pdfContentByte2.showText("");//vow
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(500.0f, 410.0f);
                    pdfContentByte2.showText("");//recibido
                    pdfContentByte2.endText();


                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(130.0f, 305.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(130.0f, 312.0f);
//                pdfContentByte2.showText("sign");
//                pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(325.0f, 312.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();

            PreparedStatement MainReceipt = conn.prepareStatement(
                    "INSERT INTO " + Database + ".BundleHistory (MRN ,PatientRegId ,BundleName ,CreatedDate,PgCount,VisitIndex )" +
                            " VALUES (? ,? ,? ,now(),?,?) ");
            MainReceipt.setString(1, MRN);
            MainReceipt.setInt(2, ID);
            MainReceipt.setString(3, filename);
            MainReceipt.setInt(4, pageCount);
            MainReceipt.setInt(5, VisitIndex);
            MainReceipt.executeUpdate();
            MainReceipt.close();

            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf");
            File.delete();

            return pageCount + "~" + outputFilePath + "~" + filename;
        } catch (Exception e) {
            System.out.println("in the catch exception of FrontlineBundleinside Function ");
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println(str);

        }
        return "";

    }

    //    void GETINPUTFrontLine(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
//        Statement stmt = null;
//        ResultSet rset = null;
//        String Query = "";
//        int PatientRegId = 0;
//        String DateTime = "";
//        String Date = "";
//        String Time = "";
//        String ResultPdf = "";
//        final MergePdf mergePdf = new MergePdf();
//        String Title = "";
//        String FirstName = "";
//        String FirstNameNoSpaces = "";
//        String LastName = "";
//        String MiddleInitial = "";
//        String MaritalStatus = "";
//        String DOB = "";
//        String Age = "";
//        String gender = "";
//        String Email = "";
//        String PhNumber = "";
//        String Address = "";
//        String StreetAddress2 = "";
//        String CityStateZip = "";
//        String State = "";
//        String City = "";
//        final String Country = "";
//        String ZipCode = "";
//        String SSN = "";
//        String Occupation = "";
//        String Employer = "";
//        String EmpContact = "";
//        String PriCarePhy = "";
//        String ReasonVisit = "";
//        String MRN = "";
//        int ClientIndex = 0;
//        String ClientName = "";
//        String DOS = "";
//        String DoctorId = null;
//        String DoctorName = null;
//        int FoundAddInfo = 0;
//        String CovidTestDate = "";
//        String Ethnicity = "";
//        String Race = "";
//        String TravellingChk = "";
//        String TravelWhen = "";
//        String TravelWhere = "";
//        String TravelHowLong = "";
//        String CovidExpWhen = "";
//        String COVIDExposedChk = "";
//        String SympFever = "";
//        String SympBodyAches = "";
//        String SympSoreThroat = "";
//        String SympFatigue = "";
//        String SympRash = "";
//        String SympVomiting = "";
//        String SympDiarrhea = "";
//        String SympCough = "";
//        String SympRunnyNose = "";
//        String SympNausea = "";
//        String SympFluSymptoms = "";
//        String SympHeadache = "";
//        String SympLossTaste = "";
//        String SympShortBreath = "";
//        String SympCongestion = "";
//        String SympEyeConjunctivitis = "";
//        String GuarantorName = "";
//        String GuarantorDOB = "";
//        String GuarantorSSN = "";
//        int WorkersCompPolicy = 0;
//        String WorkersCompPolicyString = "";
//        int MotorVehAccident = 0;
//        String MotorVehAccidentString = "";
//        String PriInsurance = "";
//        String MemId = "";
//        String GrpNumber = "";
//        String PriInsuranceName = "";
//        String PriInsurerName = "";
//        String AddressIfDifferent = "";
//        String PrimaryDOB = "";
//        String PrimarySSN = "";
//        String PatientRelationtoPrimary = "";
//        String PrimaryOccupation = "";
//        String PrimaryEmployer = "";
//        String EmployerAddress = "";
//        String EmployerPhone = "";
//        String SecondryInsurance = "";
//        String SubscriberName = "";
//        String SubscriberDOB = "";
//        String MemberID_2 = "";
//        String GroupNumber_2 = "";
//        String PatientRelationshiptoSecondry = "";
//        String NextofKinName = "";
//        String RelationToPatientER = "";
//        String PhoneNumberER = "";
//        final int LeaveMessageER = 0;
//        String AddressER = "";
//        final String CityER = "";
//        final String StateER = "";
//        String LeaveMessageERString = "";
//        String CityStateZipER = "";
//        final String CountryER = "";
//        final String ZipCodeER = "";
//        final String DateConcent = "";
//        final String WitnessConcent = "";
//        final String PatientBehalfConcent = "";
//        final String RelativeSignConcent = "";
//        final String DateConcent2 = "";
//        final String WitnessConcent2 = "";
//        final String PatientSignConcent = "";
//        String ReturnPatient = "";
//        String Google = "";
//        String MapSearch = "";
//        String Billboard = "";
//        String OnlineReview = "";
//        String TV = "";
//        String Website = "";
//        String BuildingSignDriveBy = "";
//        String Facebook = "";
//        String School = "";
//        String School_text = "";
//        String Twitter = "";
//        String Magazine = "";
//        String Magazine_text = "";
//        String Newspaper = "";
//        String Newspaper_text = "";
//        String FamilyFriend = "";
//        String FamilyFriend_text = "";
//        String UrgentCare = "";
//        String UrgentCare_text = "";
//        String CommunityEvent = "";
//        String CommunityEvent_text = "";
//        final String Work = "";
//        String Work_text = "";
//        final String Physician = "";
//        String Physician_text = "";
//        final String Other = "";
//        String Other_text = "";
//        String COVIDStatus = "";
//        String VisitNumber = "";
//        int SelfPayChk = 0;
//        int VerifyChkBox = 0;
//        int ID = Integer.parseInt(request.getParameter("ID").trim());
//        String VisitId = request.getParameter("VisitId");
//        try {
//            Query = "select date_format(now(),'%Y%m%d%H%i%s'), DATE_FORMAT(now(), '%m/%d/%Y'), DATE_FORMAT(now(), '%T')";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                DateTime = rset.getString(1);
//                Date = rset.getString(2);
//                Time = rset.getString(3);
//            }
//            rset.close();
//            stmt.close();
//            try {
//                Query = " Select IFNULL(a.LastName,'-'), IFNULL(a.FirstName,'-'), IFNULL(a.MiddleInitial,'-'), IFNULL(a.Title,'-'), " +
//                        "IFNULL(a.MaritalStatus, '-'),  IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'), '-'),  IFNULL(a.Age, '0'), IFNULL(a.Gender, '-'), " +
//                        "IFNULL(a.Address,'-'), IFNULL(CONCAT(a.City,' / ', a.State, ' / ', a.ZipCode),'-'), IFNULL(a.PhNumber,'-'), " +
//                        "IFNULL(a.SSN,'-'), IFNULL(a.Occupation,'-'), IFNULL(a.Employer,'-'), IFNULL(a.EmpContact,'-'), IFNULL(a.PriCarePhy,'-'), " +
//                        "IFNULL(a.Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(a.SelfPayChk,0), IFNULL(a.MRN,0), a.ClientIndex, " +
//                        "IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T')), " +
//                        "IFNULL(a.DoctorsName,'-'),  " +
//                        "CASE WHEN a.Ethnicity = 1 THEN 'Hispanic or Latino' WHEN a.Ethnicity = 2 THEN ' Non Hispanic or Latino' " +
//                        "WHEN a.Ethnicity = 3 THEN 'Others' ELSE 'Others' END, IFNULL(a.City,''), IFNULL(a.State,''), IFNULL(a.ZipCode,''), " +
//                        "IFNULL(a.COVIDStatus,''), IFNULL(a.StreetAddress2,'')  From " + Database + ".PatientReg a inner join "+Database+".PatientVisit b ON a.ID = b.PatientRegId Where a.ID = " + ID +" AND  b.Id="+ VisitId;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                while (rset.next()) {
//                    PatientRegId = ID;
//                    LastName = rset.getString(1).trim();
//                    FirstName = rset.getString(2).trim();
//                    FirstNameNoSpaces = FirstName.replaceAll("\\s+", "");
//                    MiddleInitial = rset.getString(3).trim();
//                    Title = rset.getString(4).trim();
//                    MaritalStatus = rset.getString(5);
//                    DOB = rset.getString(6);
//                    Age = rset.getString(7);
//                    gender = rset.getString(8);
//                    Address = rset.getString(9);
//                    CityStateZip = rset.getString(10);
//                    PhNumber = rset.getString(11);
//                    SSN = rset.getString(12);
//                    Occupation = rset.getString(13);
//                    Employer = rset.getString(14);
//                    EmpContact = rset.getString(15);
//                    PriCarePhy = rset.getString(16);
//                    Email = rset.getString(17);
//                    ReasonVisit = rset.getString(18);
//                    SelfPayChk = rset.getInt(19);
//                    MRN = rset.getString(20);
//                    ClientIndex = rset.getInt(21);
//                    DOS = rset.getString(22);
//                    DoctorId = rset.getString(23);
//                    Ethnicity = rset.getString(24);
//                    City = rset.getString(25);
//                    State = rset.getString(26);
//                    ZipCode = rset.getString(27);
//                    COVIDStatus = rset.getString(28);
//                    StreetAddress2 = rset.getString(29);
//                }
//                rset.close();
//                stmt.close();
//                Query = "Select name from oe.clients where Id = " + ClientId;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    ClientName = rset.getString(1);
//                }
//                rset.close();
//                stmt.close();
//                if (!DoctorId.equals("-")) {
//                    Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorId;
//                    stmt = conn.createStatement();
//                    rset = stmt.executeQuery(Query);
//                    while (rset.next()) {
//                        DoctorName = rset.getString(1);
//                    }
//                    rset.close();
//                    stmt.close();
//                } else {
//                    DoctorName = "";
//                }
//            } catch (Exception e) {
//                out.println("Error In PateintReg:--" + e.getMessage());
//                out.println(Query);
//            }
//            String CovidTest = "";
////            if (COVIDStatus.equals("1")) {
////                CovidTest = "YES";
////            } else if (COVIDStatus.equals("0")) {
////                CovidTest = "NO";
////            } else {
////                CovidTest = "";
////            }
//            Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                FoundAddInfo = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();
//            if (FoundAddInfo > 0) {
////                Query = "Select IFNULL(Date_format(CovidTestDate,'%m/%d/%Y'),'') from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
//                Query = "Select CASE WHEN COVIDPositveChk = 1 THEN 'YES' WHEN COVIDPositveChk = 0 THEN 'NO' ELSE 'NO' END,IFNULL(Date_format(CovidPositiveDate,'%m/%d/%Y'),'') from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    CovidTest = rset.getString(1);
//                    CovidTestDate = rset.getString(2);
//                }
//                rset.close();
//                stmt.close();
//                //out.println(Query);
//            }
//            try {
//                Query = " Select CASE WHEN TravellingChk = 1 THEN 'YES' WHEN TravellingChk = 0 THEN 'NO' ELSE 'NO' END,  " +
//                        " IFNULL(DATE_FORMAT(TravelWhen,'%m/%d/%Y'),''),  IFNULL(TravelWhere,''), IFNULL(TravelHowLong,''), " +
//                        " CASE WHEN COVIDExposedChk = 1 THEN 'YES' WHEN COVIDExposedChk = 0 THEN 'NO' ELSE 'NO' END,  " +
//                        " CASE WHEN SympFever = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympBodyAches = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympSoreThroat = 1 THEN 'YES' ELSE '' END,  " +
//                        " CASE WHEN SympFatigue = 1 THEN 'YES' ELSE '' END," +
//                        " CASE WHEN SympRash = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympVomiting = 1 THEN 'YES' ELSE '' END,  " +
//                        " CASE WHEN SympDiarrhea = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympCough = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympRunnyNose = 1 THEN 'YES' ELSE '' END,  " +
//                        " CASE WHEN SympNausea = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympFluSymptoms = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympEyeConjunctivitis = 1 THEN 'YES' ELSE '' END,  " +
//                        " CASE WHEN Race = 1 THEN 'African American' WHEN Race = 2 THEN 'American Indian or Alska Native' " +
//                        " WHEN Race = 3 THEN 'Asian' WHEN Race = 4 THEN 'Native Hawaiian or Other Pacific Islander'  " +
//                        " WHEN Race = 5 THEN 'White' WHEN Race = 6 THEN 'Others' ELSE 'Others' END, IFNULL(DATE_FORMAT(CovidExpWhen,'%m/%d/%Y'),''), " +
//                        " CASE WHEN SympHeadache = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympLossTaste = 1 THEN 'YES' ELSE '' END," +
//                        " CASE WHEN SympShortBreath = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympCongestion = 1 THEN 'YES' ELSE '' END, " +
//                        " CONCAT(IFNULL(GuarantorFirstName,''),' ',IFNULL(GuarantorLastName,'')), IFNULL(DATE_FORMAT(GuarantorDOB,'%m/%d/%Y'),''),IFNULL(GuarantorSSN,'')" +
//                        " from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    TravellingChk = rset.getString(1);
//                    TravelWhen = rset.getString(2);
//                    TravelWhere = rset.getString(3);
//                    TravelHowLong = rset.getString(4);
//                    COVIDExposedChk = rset.getString(5);
//                    SympFever = rset.getString(6);
//                    SympBodyAches = rset.getString(7);
//                    SympSoreThroat = rset.getString(8);
//                    SympFatigue = rset.getString(9);
//                    SympRash = rset.getString(10);
//                    SympVomiting = rset.getString(11);
//                    SympDiarrhea = rset.getString(12);
//                    SympCough = rset.getString(13);
//                    SympRunnyNose = rset.getString(14);
//                    SympNausea = rset.getString(15);
//                    SympFluSymptoms = rset.getString(16);
//                    SympEyeConjunctivitis = rset.getString(17);
//                    Race = rset.getString(18);
//                    CovidExpWhen = rset.getString(19);
//                    SympHeadache = rset.getString(20);
//                    SympLossTaste = rset.getString(21);
//                    SympShortBreath = rset.getString(22);
//                    SympCongestion = rset.getString(23);
//                    GuarantorName = rset.getString(24);
//                    GuarantorDOB = rset.getString(25);
//                    GuarantorSSN = rset.getString(26);
//                }
//                rset.close();
//                stmt.close();
//            } catch (Exception e2) {
//                out.println("Error in getting PatientReg_Details Table : " + e2.getMessage());
//            }
//            if (SelfPayChk == 1) {
//                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%m/%d/%Y'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), CONCAT(IFNULL(SubscriberFirstName,'-'),' ',IFNULL(SubscriberLastName,'-')), IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-'),CONCAT( IFNULL(PriInsurerFirstName,''),' ', IFNULL(PriInsurerLastName,'')) from " + Database + ".InsuranceInfo  where PatientRegId = " + ID;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                while (rset.next()) {
//                    WorkersCompPolicy = rset.getInt(1);
//                    MotorVehAccident = rset.getInt(2);
//                    if (WorkersCompPolicy == 0) {
//                        WorkersCompPolicyString = "N";
//                    } else {
//                        WorkersCompPolicyString = "Y";
//                    }
//                    if (MotorVehAccident == 0) {
//                        MotorVehAccidentString = "N";
//                    } else {
//                        MotorVehAccidentString = "Y";
//                    }
//                    PriInsurance = rset.getString(3);
//                    MemId = rset.getString(4);
//                    GrpNumber = rset.getString(5);
//                    PriInsuranceName = rset.getString(6);
//                    AddressIfDifferent = rset.getString(7);
//                    PrimaryDOB = rset.getString(8);
//                    PrimarySSN = rset.getString(9);
//                    PatientRelationtoPrimary = rset.getString(10);
//                    PrimaryOccupation = rset.getString(11);
//                    PrimaryEmployer = rset.getString(12);
//                    EmployerAddress = rset.getString(13);
//                    EmployerPhone = rset.getString(14);
//                    SecondryInsurance = rset.getString(15);
//                    SubscriberName = rset.getString(16);
//                    SubscriberDOB = rset.getString(17);
//                    PatientRelationshiptoSecondry = rset.getString(18);
//                    MemberID_2 = rset.getString(19);
//                    GroupNumber_2 = rset.getString(20);
//                    PriInsurerName = rset.getString(21);
//                }
//                rset.close();
//                stmt.close();
//            }
//            if (!PriInsuranceName.equals("")) {
//                Query = "Select IFNULL(PayerName,'') from " + Database + ".ProfessionalPayers where id = " + PriInsuranceName;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PriInsuranceName = rset.getString(1);
//                }
//                rset.close();
//                stmt.close();
//            }
//            Query = "Select IFNULL(NextofKinName,'-'), IFNULL(RelationToPatient,'-'), IFNULL(PhoneNumber,'-'), CASE WHEN LeaveMessage = 1 THEN 'YES' WHEN LeaveMessage = 0 THEN 'NO' ELSE ' YES / NO'END,  IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-') from " + Database + ".EmergencyInfo where PatientRegId = " + ID;
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            while (rset.next()) {
//                NextofKinName = rset.getString(1);
//                RelationToPatientER = rset.getString(2);
//                PhoneNumberER = rset.getString(3);
//                LeaveMessageERString = rset.getString(4);
//                AddressER = rset.getString(5);
//                CityStateZipER = rset.getString(6);
//            }
//            rset.close();
//            stmt.close();
//            Query = "Select MAX(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + ID;
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                VisitNumber = rset.getString(1).trim();
//            }
//            rset.close();
//            stmt.close();
//            VisitNumber = "VN-" + MRN + "-" + VisitNumber;
//            Query = " Select ReturnPatient, Google, MapSearch, Billboard, OnlineReview, TV, Website, BuildingSignDriveBy, Facebook, School, IFNULL(School_text ,'-'), Twitter, Magazine, IFNULL(Magazine_text,'-'), Newspaper, IFNULL(Newspaper_text,'-'), FamilyFriend, IFNULL(FamilyFriend_text,'-'), UrgentCare, IFNULL(UrgentCare_text,'-'), CommunityEvent, IFNULL(CommunityEvent_text,'-'),  IFNULL(Work_text,'-'), IFNULL(Physician_text, '-'), IFNULL(Other_text,'-') from " + Database + ".RandomCheckInfo where PatientRegId = " + ID;
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            while (rset.next()) {
//                if (rset.getInt(1) == 0) {
//                    ReturnPatient = "";
//                } else {
//                    ReturnPatient = "YES";
//                }
//                if (rset.getInt(2) == 0) {
//                    Google = "";
//                } else {
//                    Google = "YES";
//                }
//                if (rset.getInt(3) == 0) {
//                    MapSearch = "";
//                } else {
//                    MapSearch = "YES";
//                }
//                if (rset.getInt(4) == 0) {
//                    Billboard = "";
//                } else {
//                    Billboard = "YES";
//                }
//                if (rset.getInt(5) == 0) {
//                    OnlineReview = "";
//                } else {
//                    OnlineReview = "YES";
//                }
//                if (rset.getInt(6) == 0) {
//                    TV = "";
//                } else {
//                    TV = "YES";
//                }
//                if (rset.getInt(7) == 0) {
//                    Website = "";
//                } else {
//                    Website = "YES";
//                }
//                if (rset.getInt(8) == 0) {
//                    BuildingSignDriveBy = "";
//                } else {
//                    BuildingSignDriveBy = "YES";
//                }
//                if (rset.getInt(9) == 0) {
//                    Facebook = "";
//                } else {
//                    Facebook = "YES";
//                }
//                if (rset.getInt(10) == 0) {
//                    School = "";
//                    School_text = "";
//                } else {
//                    School = "YES";
//                    School_text = rset.getString(11);
//                }
//                if (rset.getInt(12) == 0) {
//                    Twitter = "";
//                } else {
//                    Twitter = "YES";
//                }
//                if (rset.getInt(13) == 0) {
//                    Magazine = "";
//                    Magazine_text = "";
//                } else {
//                    Magazine = "YES";
//                    Magazine_text = rset.getString(14);
//                }
//                if (rset.getInt(15) == 0) {
//                    Newspaper = "";
//                    Newspaper_text = "";
//                } else {
//                    Newspaper = "YES";
//                    Newspaper_text = rset.getString(16);
//                }
//                if (rset.getInt(17) == 0) {
//                    FamilyFriend = "";
//                    FamilyFriend_text = "";
//                } else {
//                    FamilyFriend = "YES";
//                    FamilyFriend_text = rset.getString(18);
//                }
//                if (rset.getInt(19) == 0) {
//                    UrgentCare = "";
//                    UrgentCare_text = "";
//                } else {
//                    UrgentCare = "YES";
//                    UrgentCare_text = rset.getString(20);
//                }
//                if (rset.getInt(21) == 0) {
//                    CommunityEvent = "";
//                    CommunityEvent_text = "";
//                } else {
//                    CommunityEvent = "YES";
//                    CommunityEvent_text = rset.getString(22);
//                }
//                if (rset.getString(23) == "" || rset.getString(23) == null) {
//                    Work_text = "";
//                } else {
//                    Work_text = rset.getString(23);
//                }
//                if (rset.getString(24) == "" || rset.getString(24) == null) {
//                    Physician_text = "";
//                } else {
//                    Physician_text = rset.getString(24);
//                }
//                if (rset.getString(25) == "" || rset.getString(25) == null) {
//                    Other_text = "";
//                } else {
//                    Other_text = rset.getString(25);
//                }
//            }
//            rset.close();
//            stmt.close();
//            String HearAboutUsString = "";
//            if (ReturnPatient.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Return Patient, ";
//            }
//            if (Google.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Google, ";
//            }
//            if (MapSearch.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Map Search, ";
//            }
//            if (OnlineReview.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Online Review, ";
//            }
//            if (TV.toUpperCase().equals("YES")) {
//                HearAboutUsString += "TV, ";
//            }
//            if (Website.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Website, ";
//            }
//            if (BuildingSignDriveBy.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Building Sign, ";
//            }
//            if (Facebook.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Facebook, ";
//            }
//            if (School.toUpperCase().equals("YES")) {
//                HearAboutUsString += "School, ";
//            }
//            if (Twitter.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Twitter, ";
//            }
//            if (Magazine.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Magazine, ";
//            }
//            if (Newspaper.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Newspaper, ";
//            }
//            if (FamilyFriend.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Friend / Family, ";
//            }
//            if (UrgentCare.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Urgent Care, ";
//            }
//            if (CommunityEvent.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Comminuty Event, ";
//            }
//            if ("".toUpperCase().equals("YES")) {
//                HearAboutUsString += "Work, ";
//            }
//            if ("".toUpperCase().equals("YES")) {
//                HearAboutUsString += "Physician, ";
//            }
//            if ("".toUpperCase().equals("YES")) {
//                HearAboutUsString += "Others ";
//            }
//            String SymptomsString = "";
//            if (SympFever.toUpperCase().equals("YES")) {
//                SymptomsString += "Fever, ";
//            }
//            if (SympBodyAches.toUpperCase().equals("YES")) {
//                SymptomsString += "Body Aches, ";
//            }
//            if (SympSoreThroat.toUpperCase().equals("YES")) {
//                SymptomsString += "Sore Throat, ";
//            }
//            if (SympFatigue.toUpperCase().equals("YES")) {
//                SymptomsString += "Fatigue, ";
//            }
//            if (SympRash.toUpperCase().equals("YES")) {
//                SymptomsString += "Rash, ";
//            }
//            if (SympVomiting.toUpperCase().equals("YES")) {
//                SymptomsString += "Vomitting, ";
//            }
//            if (SympDiarrhea.toUpperCase().equals("YES")) {
//                SymptomsString += "Diarrhea, ";
//            }
//            if (SympCough.toUpperCase().equals("YES")) {
//                SymptomsString += "Cough, ";
//            }
//            if (SympRunnyNose.toUpperCase().equals("YES")) {
//                SymptomsString += "Runny Nose, ";
//            }
//            if (SympNausea.toUpperCase().equals("YES")) {
//                SymptomsString += "Nausea, ";
//            }
//            if (SympFluSymptoms.toUpperCase().equals("YES")) {
//                SymptomsString += "Flu-like Symptoms, ";
//            }
//            if (SympEyeConjunctivitis.toUpperCase().equals("YES")) {
//                SymptomsString += "Eye Conjunctivitis, ";
//            }
//            if (SympHeadache.toUpperCase().equals("YES")) {
//                SymptomsString += "Headache, ";
//            }
//            if (SympLossTaste.toUpperCase().equals("YES")) {
//                SymptomsString += "Loss of Taste/Smell, ";
//            }
//            if (SympShortBreath.toUpperCase().equals("YES")) {
//                SymptomsString += "Short of Breathness, ";
//            }
//            if (SympCongestion.toUpperCase().equals("YES")) {
//                SymptomsString += "Congestion ";
//            }
//
//            String inputFilePathTmp = "";
//            String outputFilePathTmp = "";
//            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/FinancialHardShip.pdf";
//            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf";
//            OutputStream fos1 = new FileOutputStream(new File(outputFilePathTmp));
//            PdfReader pdfReader1 = new PdfReader(inputFilePathTmp);
//            PdfStamper pdfStamper1 = new PdfStamper(pdfReader1, fos1);
//            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
//                if (i == 1) {
//                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(123.0f, 605.0f);
//                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(400.0f, 605.0f);
//                    pdfContentByte.showText(DOS);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(170.0f, 500.0f);
//                    pdfContentByte.showText("");
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(170.0f, 550.0f);
//                    pdfContentByte.showText(DOS);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(295.0f, 300.0f);
//                    pdfContentByte.showText(DOS);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(329.0f, 110.0f);
//                    pdfContentByte.showText(DOS);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte.endText();
//                }
//            }
//            pdfStamper1.close();
//            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/PromptPayAgreement.pdf";
//            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf";
//            fos1 = new FileOutputStream(new File(outputFilePathTmp));
//            pdfReader1 = new PdfReader(inputFilePathTmp);
//            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
//            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
//                if (i == 1) {
//                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
////                    if (ClientId == 27) {
////                        pdfContentByte.beginText();
////                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
////                        pdfContentByte.setColorFill(BaseColor.BLACK);
////                        pdfContentByte.setTextMatrix(175.0f, 645.0f);
////                        pdfContentByte.showText("Frontline ER White Rock.");
////                        pdfContentByte.endText();
////                    } else if (ClientId == 29) {
////                        pdfContentByte.beginText();
////                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
////                        pdfContentByte.setColorFill(BaseColor.BLACK);
////                        pdfContentByte.setTextMatrix(175.0f, 645.0f);
////                        pdfContentByte.showText("Frontline ER Richmond.");
////                        pdfContentByte.endText();
////                    }
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(315.0f, 300.0f);
//                    pdfContentByte.showText(DOS);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte.endText();
//                }
//            }
//            pdfStamper1.close();
////            if (SelfPayChk == 1) {
////                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
////                if (ClientId == 27) {
////                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
////                } else {
////                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
////                }
////            } else {
////                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
////                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
////                if (ClientId == 27) {
////                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
////                } else {
////                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
////                }
////            }
//
//
//            String pattern = null;
//            Statement stmt1 = null;
//            ResultSet rset1 = null;
//            String Query1 = "";
//
//            Query1 = "SELECT Form_ids FROM " + Database + ".BundleForms where PatientRegId='" + ID + "'";
//            stmt1 = conn.createStatement();
//            rset1 = stmt1.executeQuery(Query1);
//            while (rset1.next()) {
//                pattern = rset1.getString(1);
//            }
//            rset1.close();
//            stmt1.close();
//
//            if (pattern != null) {
//                String[] pat = pattern.split("\\^");//pat[0] -> mva, pat[1] -> prompt pay, pat[2] -> Financial Harship
//                out.println(pat[0]);
//
//                if (pat[0].equals("1")) {//mva
//                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
//                }
//
//                if (pat[2].equals("1")) {// financial hardship
//                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
//                    if (ClientId == 27) {
//                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
//                    } else {
//                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
//                    }
//
//                    if (pat[1].equals("1")) {
//                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
//                        if (ClientId == 27) {
//                            ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
//                        } else {
//                            ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
//                        }
//                    }
//
//                }
//
//                if (pat[1].equals("1") && !(pat[2].equals("1"))) {//prompt pay
//                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
//                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
//                    if (ClientId == 27) {
//                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
//                    } else {
//                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
//                    }
//                }
//            } else {
//                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
//            }
//
//
//            if (PriInsuranceName.toUpperCase().contains("UNITED HEALTHCARE")) {
////                System.out.println("PriInsuranceName -> "+PriInsuranceName);
//
//                String inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/Commercial-Courtesy-Review-Auth-Form.pdf";
//                String outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/Commercial-Courtesy-Review-Auth-Form_" + ClientId + "_" + MRN + ".pdf";
//
//                ResultPdf = AttachUHC_Form(MemId, PrimaryDOB, PriInsurerName, DOS, PatientRelationtoPrimary, Date, outputFilePathTmp2, inputFilePathTmp2,
//                        request, response, out, conn, Database, ResultPdf, DirectoryName, ClientId, MRN, mergePdf);
//            }
//
//            String inputFilePath = "";
//            String outputFilePath = "";
//            String UID = "";
//            inputFilePath = ResultPdf;
//            outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_.pdf";
//            final OutputStream fos2 = new FileOutputStream(new File(outputFilePath));
//            final PdfReader pdfReader2 = new PdfReader(inputFilePath);
//            final int pageCount = pdfReader2.getNumberOfPages();
//            final PdfStamper pdfStamper2 = new PdfStamper(pdfReader2, fos2);
//            Image SignImages = null;
//            final File tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_0_" + ID + ".png");
//            final boolean exists = tmpDir.exists();
//            if (exists) {
//                Query = "Select UID from " + Database + ".SignRequest where PatientRegId = " + ID;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    UID = rset.getString(1);
//                }
//                rset.close();
//                stmt.close();
//
//                SignImages = Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_0_" + ID + ".png");
//                SignImages.scaleAbsolute(80.0f, 30.0f);
//                //outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + UID + "_.pdf";
//                outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_.pdf";
//            } else {
//                SignImages = null;
//            }
//
//            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
//                if (j == 1) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(40.0f, 770.0f);
//                    pdfContentByte2.showText(UID);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(130.0f, 685.0f);
//                    pdfContentByte2.showText(ReasonVisit);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(392.0f, 685.0f);
//                    pdfContentByte2.showText(MotorVehAccidentString);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(456.0f, 685.0f);
//                    pdfContentByte2.showText(WorkersCompPolicyString);
//                    pdfContentByte2.endText();
//                    if (SelfPayChk == 1) {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("(Y)");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("(N)");
//                        pdfContentByte2.endText();
//                    } else if (SelfPayChk == 0) {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("(N)");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("(Y)");
//                        pdfContentByte2.endText();
//                    } else {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("");
//                        pdfContentByte2.endText();
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 660.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 660.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(520.0f, 660.0f);
//                    pdfContentByte2.showText(Age);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 620.0f);
//                    pdfContentByte2.showText(Address);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(300.0f, 620.0f);
//                    pdfContentByte2.showText(StreetAddress2);//ApptNo
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(460.0f, 620.0f);
//                    pdfContentByte2.showText(SSN);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 595.0f);
//                    pdfContentByte2.showText(City);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(260.0f, 595.0f);
//                    pdfContentByte2.showText(State);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 595.0f);
//                    pdfContentByte2.showText(ZipCode);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(110.0f, 575.0f);
//                    pdfContentByte2.showText(PhNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(250.0f, 575.0f);
//                    pdfContentByte2.showText(PhNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 575.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(100.0f, 550.0f);
//                    pdfContentByte2.showText(PrimaryEmployer);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(395.0f, 550.0f);
//                    pdfContentByte2.showText(PrimaryOccupation);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 527.0f);
//                    pdfContentByte2.showText(EmployerAddress);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(409.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(500.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(105.0f, 505.0f);
//                    pdfContentByte2.showText(MaritalStatus);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(250.0f, 505.0f);
//                    pdfContentByte2.showText(gender);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 505.0f);
//                    pdfContentByte2.showText(Email);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 485.0f);
//                    pdfContentByte2.showText(NextofKinName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(360.0f, 485.0f);
//                    pdfContentByte2.showText(PhoneNumberER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(520.0f, 485.0f);
//                    pdfContentByte2.showText(RelationToPatientER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(100.0f, 463.0f);
//                    pdfContentByte2.showText(AddressER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(455.0f, 463.0f);
//                    pdfContentByte2.showText(" ,  , ");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(310.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorDOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(480.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorSSN);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 360.0f);
//                    pdfContentByte2.showText(PriInsurerName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(310.0f, 360);
//                    pdfContentByte2.showText(PrimaryDOB);//primary DOB
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(480.0f, 360);
//                    pdfContentByte2.showText(PrimarySSN);//Primary SSN
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 335.0f);
//                    pdfContentByte2.showText(PriInsuranceName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(445.0f, 335.0f);
//                    pdfContentByte2.showText(MemId);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(540.0f, 335.0f);
//                    pdfContentByte2.showText(GrpNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 223.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 195.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(200.0f, 195.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 192.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 134.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 123.0f);
//                    pdfContentByte2.showText(ClientName + "  Sex: " + gender);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 113.0f);
//                    pdfContentByte2.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 103.0f);
//                    pdfContentByte2.showText("MRN:" + MRN + "  DOS:" + DOS + "");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 93.0f);
//                    pdfContentByte2.showText("ACT#: " + VisitNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 83.0f);
//                    pdfContentByte2.showText("Dr. " + DoctorName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 2) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(190.0f, 142.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(425.0f, 100.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(170.0f, 90.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(425.0f, 83.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(430.0f, 63.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 3) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 613.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 583.0f);
//                    pdfContentByte2.showText(Address);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 550.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(370.0f, 550.0f);
//                    pdfContentByte2.showText(SSN);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(110.0f, 150.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(430.0f, 150.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 4) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(180.0f, 140.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 140.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 5) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(190.0f, 335.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(220.0f, 275.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 285.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 252.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 6) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 530.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 530.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 490.0f);
//                    pdfContentByte2.showText(PriInsuranceName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(170.0f, 446.0f);
//                    pdfContentByte2.showText(MemId);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 446.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(210.0f, 230.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 216.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 7) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 473.0f);
//                    pdfContentByte2.showText(CovidTest);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 473.0f);
//                    pdfContentByte2.showText(CovidTestDate);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 442.0f);
//                    pdfContentByte2.showText(COVIDExposedChk);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(525.0f, 442.0f);
//                    pdfContentByte2.showText(CovidExpWhen);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(325.0f, 415.0f);
//                    pdfContentByte2.showText(SymptomsString);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 390.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(210.0f, 115.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 115.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(110.0f, 65.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 65.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//            }
//            pdfStamper2.close();
//
////            Parsehtm Parser = new Parsehtm(request);
////            Parser.SetField("outputFilePath", outputFilePath);
////            //Parser.SetField("imagelist", String.valueOf(imagelist));
////            Parser.SetField("pageCount", String.valueOf(pageCount));
////            Parser.SetField("PatientRegId", String.valueOf(ID));
////            Parser.SetField("FileName", FirstNameNoSpaces + LastName + ID + "_.pdf");
////            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/DownloadBundleHTML.html");
//
//            final File pdfFile = new File(outputFilePath);
//            response.setContentType("application/pdf");
//            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
//            response.setContentLength((int) pdfFile.length());
//            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
//            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
//            int bytes;
//            while ((bytes = fileInputStream.read()) != -1) {
//                responseOutputStream.write(bytes);
//            }
//            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//
//
//
//
//        } catch (Exception e) {
//            out.println(e.getMessage());
//            String str = "";
//            for (int k = 0; k < e.getStackTrace().length; ++k) {
//                str = str + e.getStackTrace()[k] + "<br>";
//            }
//            out.println(str);
//        }
//    }

    void GETINPUTFrontLine_Bundle(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName, int patientRegId, String SignedFrom, int PatientVisitNumber, UtilityHelper helper) {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        String ResultPdf = "";
        final MergePdf mergePdf = new MergePdf();
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
        String StreetAddress2 = "";
        String CityStateZip = "";
        String State = "";
        String City = "";
        final String Country = "";
        String ZipCode = "";
        String SSN = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        String filename = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        String MRN = "";
        int ClientIndex = 0;
        String ClientName = "";
        String DOS = "";
        String DoctorId = null;
        String DoctorName = null;
        int FoundAddInfo = 0;
        String CovidTestDate = "";
        String Ethnicity = "";
        String Race = "";
        String TravellingChk = "";
        String TravelWhen = "";
        String TravelWhere = "";
        String TravelHowLong = "";
        String CovidExpWhen = "";
        String COVIDExposedChk = "";
        String SympFever = "";
        String SympBodyAches = "";
        String SympSoreThroat = "";
        String SympFatigue = "";
        String SympRash = "";
        String SympVomiting = "";
        String SympDiarrhea = "";
        String SympCough = "";
        String SympRunnyNose = "";
        String SympNausea = "";
        String SympFluSymptoms = "";
        String SympHeadache = "";
        String SympLossTaste = "";
        String SympShortBreath = "";
        String SympCongestion = "";
        String SympEyeConjunctivitis = "";
        String GuarantorName = "";
        String GuarantorDOB = "";
        String GuarantorSSN = "";
        int WorkersCompPolicy = 0;
        String WorkersCompPolicyString = "";
        int MotorVehAccident = 0;
        String MotorVehAccidentString = "";
        String PriInsurance = "";
        String MemId = "";
        String GrpNumber = "";
        String PriInsuranceName = "";
        String PriInsurerName = "";
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
        String DirectoryNameTow = "";
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
        String COVIDStatus = "";
        String VisitNumber = "";
        int SelfPayChk = 0;
        int VisitIndex = PatientVisitNumber;
        int VerifyChkBox = 0;
        int ID = patientRegId;
        String DOBForAge = "";
        String VisitId = "";
        try {
//            Query = "Select id FROM " + Database + ".PatientVisit ORDER BY CreatedDate DESC LIMIT 1";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                VisitIndex = rset.getInt(1);
//
//            }
//            rset.close();
//            stmt.close();

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
                Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), " +
                        "IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), " +
                        "IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-'), " +
                        "IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), " +
                        "IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, " +
                        "IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), " +
                        "IFNULL(DoctorsName,'-'),  " +
                        "CASE WHEN Ethnicity = 1 THEN 'Hispanic or Latino' WHEN Ethnicity = 2 THEN ' Non Hispanic or Latino' " +
                        "WHEN Ethnicity = 3 THEN 'Others' ELSE 'Others' END, IFNULL(City,''), IFNULL(State,''), IFNULL(ZipCode,''), " +
                        "IFNULL(COVIDStatus,''), IFNULL(StreetAddress2,''),IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'),'')  From " + Database + ".PatientReg  Where ID = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
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
                    Ethnicity = rset.getString(24);
                    City = rset.getString(25);
                    State = rset.getString(26);
                    ZipCode = rset.getString(27);
                    COVIDStatus = rset.getString(28);
                    StreetAddress2 = rset.getString(29);
                    DOBForAge = rset.getString(30);
                }
                rset.close();
                stmt.close();

                if (!DOB.equals("")) {
                    Age = String.valueOf(helper.getAge(LocalDate.parse(DOBForAge)));
                }
                Query = "Select name from oe.clients where Id = " + ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClientName = rset.getString(1);
                }
                rset.close();
                stmt.close();

                if (!DoctorId.equals("-")) {
                    Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        DoctorName = rset.getString(1);
                    }
                    rset.close();
                    stmt.close();
                } else {
                    DoctorName = "";
                }
            } catch (Exception e) {
                out.println("Error In PateintReg:--" + e.getMessage());
                out.println(Query);
            }
            String CovidTest = "";
            Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundAddInfo = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (FoundAddInfo > 0) {
//                Query = "Select IFNULL(Date_format(CovidTestDate,'%m/%d/%Y'),'') from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
                Query = "Select CASE WHEN COVIDPositveChk = 1 THEN 'YES' WHEN COVIDPositveChk = 0 THEN 'NO' ELSE 'NO' END," +
                        "IFNULL(Date_format(CovidPositiveDate,'%m/%d/%Y'),'') " +
                        "from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    CovidTest = rset.getString(1);
                    CovidTestDate = rset.getString(2);
                }
                rset.close();
                stmt.close();
            }
            try {
                Query = " Select CASE WHEN TravellingChk = 1 THEN 'YES' WHEN TravellingChk = 0 THEN 'NO' ELSE 'NO' END,  " +
                        " IFNULL(DATE_FORMAT(TravelWhen,'%m/%d/%Y'),''),  IFNULL(TravelWhere,''), IFNULL(TravelHowLong,''), " +
                        " CASE WHEN COVIDExposedChk = 1 THEN 'YES' WHEN COVIDExposedChk = 0 THEN 'NO' ELSE 'NO' END,  " +
                        " CASE WHEN SympFever = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympBodyAches = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympSoreThroat = 1 THEN 'YES' ELSE '' END,  " +
                        " CASE WHEN SympFatigue = 1 THEN 'YES' ELSE '' END," +
                        " CASE WHEN SympRash = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympVomiting = 1 THEN 'YES' ELSE '' END,  " +
                        " CASE WHEN SympDiarrhea = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympCough = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympRunnyNose = 1 THEN 'YES' ELSE '' END,  " +
                        " CASE WHEN SympNausea = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympFluSymptoms = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympEyeConjunctivitis = 1 THEN 'YES' ELSE '' END,  " +
                        " CASE WHEN Race = 1 THEN 'African American' WHEN Race = 2 THEN 'American Indian or Alska Native' " +
                        " WHEN Race = 3 THEN 'Asian' WHEN Race = 4 THEN 'Native Hawaiian or Other Pacific Islander'  " +
                        " WHEN Race = 5 THEN 'White' WHEN Race = 6 THEN 'Others' ELSE 'Others' END, IFNULL(DATE_FORMAT(CovidExpWhen,'%m/%d/%Y'),''), " +
                        " CASE WHEN SympHeadache = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympLossTaste = 1 THEN 'YES' ELSE '' END," +
                        " CASE WHEN SympShortBreath = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympCongestion = 1 THEN 'YES' ELSE '' END, " +
                        " CONCAT(IFNULL(GuarantorFirstName,''),' ',IFNULL(GuarantorLastName,'')), IFNULL(DATE_FORMAT(GuarantorDOB,'%m/%d/%Y'),''),IFNULL(GuarantorSSN,'')" +
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
                    SympHeadache = rset.getString(20);
                    SympLossTaste = rset.getString(21);
                    SympShortBreath = rset.getString(22);
                    SympCongestion = rset.getString(23);
                    GuarantorName = rset.getString(24);
                    GuarantorDOB = rset.getString(25);
                    GuarantorSSN = rset.getString(26);
                }
                rset.close();
                stmt.close();
            } catch (Exception e2) {
                out.println("Error in getting PatientReg_Details Table : " + e2.getMessage());
            }
            if (SelfPayChk == 1) {
                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-')," +
                        "IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), " +
                        "IFNULL(DATE_FORMAT(PrimaryDOB,'%m/%d/%Y'),'-'), IFNULL(PrimarySSN,'-'),  " +
                        "IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), " +
                        "IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), " +
                        "CONCAT(IFNULL(SubscriberFirstName,'-'),' ',IFNULL(SubscriberLastName,'-')), " +
                        "IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), " +
                        "IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-')," +
                        "CONCAT( IFNULL(PriInsurerFirstName,''),' ', IFNULL(PriInsurerLastName,'')) " +
                        "from " + Database + ".InsuranceInfo  where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    WorkersCompPolicy = rset.getInt(1);
                    MotorVehAccident = rset.getInt(2);
                    if (WorkersCompPolicy == 0) {
                        WorkersCompPolicyString = "N";
                    } else {
                        WorkersCompPolicyString = "Y";
                    }
                    if (MotorVehAccident == 0) {
                        MotorVehAccidentString = "N";
                    } else {
                        MotorVehAccidentString = "Y";
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
                    PriInsurerName = rset.getString(21);
                }
                rset.close();
                stmt.close();
            }
            if (!PriInsuranceName.equals("")) {
                Query = "Select IFNULL(PayerName,'') from " + Database + ".ProfessionalPayers where id = " + PriInsuranceName;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PriInsuranceName = rset.getString(1);
                }
                rset.close();
                stmt.close();
            }
            Query = "Select IFNULL(NextofKinName,'-'), IFNULL(RelationToPatient,'-'), IFNULL(PhoneNumber,'-'), " +
                    "CASE WHEN LeaveMessage = 1 THEN 'YES' WHEN LeaveMessage = 0 THEN 'NO' ELSE ' YES / NO' END,  " +
                    "IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-') " +
                    "from " + Database + ".EmergencyInfo where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                NextofKinName = rset.getString(1);
                RelationToPatientER = rset.getString(2);
                PhoneNumberER = rset.getString(3);
                LeaveMessageERString = rset.getString(4);
                AddressER = rset.getString(5);
                CityStateZipER = rset.getString(6);
            }
            rset.close();
            stmt.close();

            Query = "Select MAX(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                VisitNumber = rset.getString(1).trim();
            }
            rset.close();
            stmt.close();

            VisitNumber = "VN-" + MRN + "-" + VisitNumber;
            Query = " Select ReturnPatient, Google, MapSearch, Billboard, OnlineReview, TV, Website, BuildingSignDriveBy, " +
                    "Facebook, School, IFNULL(School_text ,'-'), Twitter, Magazine, IFNULL(Magazine_text,'-'), " +
                    "Newspaper, IFNULL(Newspaper_text,'-'), FamilyFriend, IFNULL(FamilyFriend_text,'-'), UrgentCare, " +
                    "IFNULL(UrgentCare_text,'-'), CommunityEvent, IFNULL(CommunityEvent_text,'-'),  IFNULL(Work_text,'-')," +
                    "IFNULL(Physician_text, '-'), IFNULL(Other_text,'-') " +
                    "from " + Database + ".RandomCheckInfo where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
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

            String HearAboutUsString = "";
            if (ReturnPatient.toUpperCase().equals("YES")) {
                HearAboutUsString += "Return Patient, ";
            }
            if (Google.toUpperCase().equals("YES")) {
                HearAboutUsString += "Google, ";
            }
            if (MapSearch.toUpperCase().equals("YES")) {
                HearAboutUsString += "Map Search, ";
            }
            if (OnlineReview.toUpperCase().equals("YES")) {
                HearAboutUsString += "Online Review, ";
            }
            if (TV.toUpperCase().equals("YES")) {
                HearAboutUsString += "TV, ";
            }
            if (Website.toUpperCase().equals("YES")) {
                HearAboutUsString += "Website, ";
            }
            if (BuildingSignDriveBy.toUpperCase().equals("YES")) {
                HearAboutUsString += "Building Sign, ";
            }
            if (Facebook.toUpperCase().equals("YES")) {
                HearAboutUsString += "Facebook, ";
            }
            if (School.toUpperCase().equals("YES")) {
                HearAboutUsString += "School, ";
            }
            if (Twitter.toUpperCase().equals("YES")) {
                HearAboutUsString += "Twitter, ";
            }
            if (Magazine.toUpperCase().equals("YES")) {
                HearAboutUsString += "Magazine, ";
            }
            if (Newspaper.toUpperCase().equals("YES")) {
                HearAboutUsString += "Newspaper, ";
            }
            if (FamilyFriend.toUpperCase().equals("YES")) {
                HearAboutUsString += "Friend / Family, ";
            }
            if (UrgentCare.toUpperCase().equals("YES")) {
                HearAboutUsString += "Urgent Care, ";
            }
            if (CommunityEvent.toUpperCase().equals("YES")) {
                HearAboutUsString += "Comminuty Event, ";
            }
            if ("".toUpperCase().equals("YES")) {
                HearAboutUsString += "Work, ";
            }
            if ("".toUpperCase().equals("YES")) {
                HearAboutUsString += "Physician, ";
            }
            if ("".toUpperCase().equals("YES")) {
                HearAboutUsString += "Others ";
            }
            String SymptomsString = "";
            if (SympFever.toUpperCase().equals("YES")) {
                SymptomsString += "Fever, ";
            }
            if (SympBodyAches.toUpperCase().equals("YES")) {
                SymptomsString += "Body Aches, ";
            }
            if (SympSoreThroat.toUpperCase().equals("YES")) {
                SymptomsString += "Sore Throat, ";
            }
            if (SympFatigue.toUpperCase().equals("YES")) {
                SymptomsString += "Fatigue, ";
            }
            if (SympRash.toUpperCase().equals("YES")) {
                SymptomsString += "Rash, ";
            }
            if (SympVomiting.toUpperCase().equals("YES")) {
                SymptomsString += "Vomitting, ";
            }
            if (SympDiarrhea.toUpperCase().equals("YES")) {
                SymptomsString += "Diarrhea, ";
            }
            if (SympCough.toUpperCase().equals("YES")) {
                SymptomsString += "Cough, ";
            }
            if (SympRunnyNose.toUpperCase().equals("YES")) {
                SymptomsString += "Runny Nose, ";
            }
            if (SympNausea.toUpperCase().equals("YES")) {
                SymptomsString += "Nausea, ";
            }
            if (SympFluSymptoms.toUpperCase().equals("YES")) {
                SymptomsString += "Flu-like Symptoms, ";
            }
            if (SympEyeConjunctivitis.toUpperCase().equals("YES")) {
                SymptomsString += "Eye Conjunctivitis, ";
            }
            if (SympHeadache.toUpperCase().equals("YES")) {
                SymptomsString += "Headache, ";
            }
            if (SympLossTaste.toUpperCase().equals("YES")) {
                SymptomsString += "Loss of Taste/Smell, ";
            }
            if (SympShortBreath.toUpperCase().equals("YES")) {
                SymptomsString += "Short of Breathness, ";
            }
            if (SympCongestion.toUpperCase().equals("YES")) {
                SymptomsString += "Congestion ";
            }


            String inputFilePathTmp = "";
            String outputFilePathTmp = "";
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/FinancialHardShip.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf";
            OutputStream fos1 = new FileOutputStream(new File(outputFilePathTmp));
            PdfReader pdfReader1 = new PdfReader(inputFilePathTmp);
            PdfStamper pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(130.0f, 565.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);//patname
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(360.0f, 565.0f);
                    pdfContentByte.showText(DOS);//date req
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 540.0f);
                    pdfContentByte.showText("");//realtion
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 540.0f);
                    pdfContentByte.showText("");//guardian name
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0f, 512.0f);
                    pdfContentByte.showText(DOS);//DOS
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(75.0f, 462.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(75.0f, 434.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(75.0f, 408.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(75.0f, 380.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(245.0f, 462.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(245.0f, 434.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(245.0f, 408.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(245.0f, 380.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 308.0f);
                    pdfContentByte.showText("");//Other
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100.0f, 255.0f);
                    pdfContentByte.showText("");//recibido
                    pdfContentByte.endText();

//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(100.0f, 200.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }

//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(100.0f, 205.0f);
//                pdfContentByte2.showText("sign");
//                pdfContentByte2.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380.0f, 205.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420.0f, 143.0f);
                    pdfContentByte.showText("");//Dailylog
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(530.0f, 143.0f);
                    pdfContentByte.showText("");//By
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/PromptPayAgreement.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 425.0f);
                    pdfContentByte.showText("");//CovidExpWhen
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(30.0f, 258.0f);
                    pdfContentByte.showText("");//DOS//recibido
                    pdfContentByte.endText();

//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(50.0f, 210.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }

//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(100.0f, 215.0f);
//                pdfContentByte2.showText("Sign");//DOS
//                pdfContentByte2.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275.0f, 215.0f);
                    pdfContentByte.showText(DOS);//DOS
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();

            String pattern = null;
            Query = "SELECT Form_ids FROM " + Database + ".BundleForms where PatientRegId='" + ID + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                pattern = rset.getString(1);
            }
            rset.close();
            stmt.close();

            if (pattern != null) {
                String[] pat = pattern.split("\\^");//pat[0] -> mva, pat[1] -> prompt pay, pat[2] -> Financial Harship

                if (pat[0].equals("1")) {//mva
                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
                }

                if (pat[2].equals("1")) {// financial hardship
                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    if (ClientId == 27) {
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
                    } else {
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
                    }

                    if (pat[1].equals("1")) { //prompt pay
                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                        if (ClientId == 27) {
                            ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
                        } else {
                            ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
                        }
                    }

                }

                if (pat[1].equals("1") && !(pat[2].equals("1"))) {//prompt pay
                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    if (ClientId == 27) {
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
                    } else {
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
                    }
                }
            } else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
            }


            if (PriInsuranceName.toUpperCase().contains("UNITED HEALTHCARE")) {
                String inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/Commercial-Courtesy-Review-Auth-Form.pdf";
                String outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/Commercial-Courtesy-Review-Auth-Form_" + ClientId + "_" + MRN + ".pdf";

                ResultPdf = AttachUHC_Form(MemId, PrimaryDOB, PriInsurerName, DOS, PatientRelationtoPrimary, Date, outputFilePathTmp2, inputFilePathTmp2,
                        request, response, out, conn, Database, ResultPdf, DirectoryName, ClientId, MRN, mergePdf);
            }

            int found = 0;
            Query = "Select Count(*) from " + Database + ".BundleHistory where PatientRegId=" + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                found = rset.getInt(1);
            }
            stmt.close();
            rset.close();

            if (SignedFrom.contains("REGISTRATION")) {
                DirectoryNameTow = "REGISTRATION";
            } else if (SignedFrom.contains("VISIT")) {
                DirectoryNameTow = "VISIT";
            } else if (SignedFrom.contains("EDIT")) {
                DirectoryNameTow = "EDIT";
            }

            filename = FirstNameNoSpaces + LastName + ID + "_" + found + "_" + SignedFrom + ".pdf";

            String inputFilePath = "";
            String outputFilePath = "";
            String UID = "";
            inputFilePath = ResultPdf;
            outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + DirectoryNameTow + "/" + filename;
            final OutputStream fos2 = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader2 = new PdfReader(inputFilePath);
            final int pageCount = pdfReader2.getNumberOfPages();
            final PdfStamper pdfStamper2 = new PdfStamper(pdfReader2, fos2);
            Image SignImages = null;
            final File tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_0_" + ID + ".png");
            final boolean exists = tmpDir.exists();
            if (exists) {
                Query = "Select UID from " + Database + ".SignRequest where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    UID = rset.getString(1);
                }
                rset.close();
                stmt.close();

                SignImages = Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_0_" + ID + ".png");
                SignImages.scaleAbsolute(80.0f, 30.0f);
            } else {
                SignImages = null;
            }

            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(40.0f, 770.0f);
//                    pdfContentByte2.showText(UID);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(130.0f, 685.0f);
//                    pdfContentByte2.showText(ReasonVisit);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(392.0f, 685.0f);
//                    pdfContentByte2.showText(MotorVehAccidentString);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(456.0f, 685.0f);
//                    pdfContentByte2.showText(WorkersCompPolicyString);
//                    pdfContentByte2.endText();
//                    if (SelfPayChk == 1) {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("(Y)");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("(N)");
//                        pdfContentByte2.endText();
//                    } else if (SelfPayChk == 0) {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("(N)");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("(Y)");
//                        pdfContentByte2.endText();
//                    } else {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("");
//                        pdfContentByte2.endText();
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 660.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 660.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(520.0f, 660.0f);
//                    pdfContentByte2.showText(Age);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 620.0f);
//                    pdfContentByte2.showText(Address);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(300.0f, 620.0f);
//                    pdfContentByte2.showText(StreetAddress2);//ApptNo
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(460.0f, 620.0f);
//                    pdfContentByte2.showText(SSN);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 595.0f);
//                    pdfContentByte2.showText(City);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(260.0f, 595.0f);
//                    pdfContentByte2.showText(State);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 595.0f);
//                    pdfContentByte2.showText(ZipCode);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(110.0f, 575.0f);
//                    pdfContentByte2.showText(PhNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(250.0f, 575.0f);
//                    pdfContentByte2.showText(PhNumber);
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 575.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(100.0f, 550.0f);
//                    pdfContentByte2.showText(PrimaryEmployer);
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(395.0f, 550.0f);
//                    pdfContentByte2.showText(PrimaryOccupation);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 527.0f);
//                    pdfContentByte2.showText(EmployerAddress);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(409.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(500.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(105.0f, 505.0f);
//                    pdfContentByte2.showText(MaritalStatus);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(250.0f, 505.0f);
//                    pdfContentByte2.showText(gender);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 505.0f);
//                    pdfContentByte2.showText(Email);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 485.0f);
//                    pdfContentByte2.showText(NextofKinName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(360.0f, 485.0f);
//                    pdfContentByte2.showText(PhoneNumberER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(520.0f, 485.0f);
//                    pdfContentByte2.showText(RelationToPatientER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(100.0f, 463.0f);
//                    pdfContentByte2.showText(AddressER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(455.0f, 463.0f);
//                    pdfContentByte2.showText(" ,  , ");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(310.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorDOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(480.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorSSN);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 360.0f);
//                    pdfContentByte2.showText(PriInsurerName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(310.0f, 360);
//                    pdfContentByte2.showText(PrimaryDOB);//primary DOB
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(480.0f, 360);
//                    pdfContentByte2.showText(PrimarySSN);//Primary SSN
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 335.0f);
//                    pdfContentByte2.showText(PriInsuranceName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(445.0f, 335.0f);
//                    pdfContentByte2.showText(MemId);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(540.0f, 335.0f);
//                    pdfContentByte2.showText(GrpNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 223.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 195.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(200.0f, 215.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 192.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 134.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 123.0f);
//                    pdfContentByte2.showText(ClientName + "  Sex: " + gender);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 113.0f);
//                    pdfContentByte2.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 103.0f);
//                    pdfContentByte2.showText("MRN:" + MRN + "  DOS:" + DOS + "");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 93.0f);
//                    pdfContentByte2.showText("ACT#: " + VisitNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 83.0f);
//                    pdfContentByte2.showText("Dr. " + DoctorName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 2) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(190.0f, 142.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(425.0f, 100.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(170.0f, 90.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(425.0f, 83.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(430.0f, 63.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 3) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 613.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 583.0f);
//                    pdfContentByte2.showText(Address);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 550.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(370.0f, 550.0f);
//                    pdfContentByte2.showText(SSN);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(110.0f, 140.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(430.0f, 150.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 4) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(180.0f, 140.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 140.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 5) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(190.0f, 335.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(220.0f, 275.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 285.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 252.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 6) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 530.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 530.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 490.0f);
//                    pdfContentByte2.showText(PriInsuranceName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(170.0f, 446.0f);
//                    pdfContentByte2.showText(MemId);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 446.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(210.0f, 200.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 216.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 7) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 473.0f);
//                    pdfContentByte2.showText(CovidTest);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 473.0f);
//                    pdfContentByte2.showText(CovidTestDate);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 442.0f);
//                    pdfContentByte2.showText(COVIDExposedChk);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(525.0f, 442.0f);
//                    pdfContentByte2.showText(CovidExpWhen);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(325.0f, 415.0f);
//                    pdfContentByte2.showText(SymptomsString);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 390.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(150.0f, 110.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 115.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(110.0f, 65.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 65.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();


                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(150.0f, 655.0f);
                    pdfContentByte2.showText(ReasonVisit);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(90.0f, 620.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName);//name
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(385.0f, 620.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(515.0f, 620.0f);
                    pdfContentByte2.showText(Age);
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(90.0f, 585.0f);
                    pdfContentByte2.showText(Address);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(290.0f, 585.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(490.0f, 585.0f);
                    pdfContentByte2.showText(SSN);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 550.0f);
                    pdfContentByte2.showText(City);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(300.0f, 550.0f);
                    pdfContentByte2.showText(State);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(475.0f, 550.0f);
                    pdfContentByte2.showText(ZipCode);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(110.0f, 515.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 515.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 480.0f);
                    pdfContentByte2.showText(gender);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(300.0f, 480.0f);
                    pdfContentByte2.showText(Email);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(250.0f, 445.0f);
                    pdfContentByte2.showText(NextofKinName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(405.0f, 445.0f);
                    pdfContentByte2.showText(PhoneNumberER);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(520.0f, 445.0f);
                    pdfContentByte2.showText(RelationToPatientER);
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(200.0f, 350.0f);
                    pdfContentByte2.showText(GuarantorName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(405.0f, 350.0f);
                    pdfContentByte2.showText(GuarantorDOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(520.0f, 350.0f);
                    pdfContentByte2.showText("");//Phone
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(165.0f, 315.0f);
                    pdfContentByte2.showText(PriInsurerName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(345.0f, 315.0f);
                    pdfContentByte2.showText(PrimaryDOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(505.0f, 315.0f);
                    pdfContentByte2.showText(PrimarySSN);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(165.0f, 280.0f);
                    pdfContentByte2.showText(PriInsuranceName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(405.0f, 280.0f);
                    pdfContentByte2.showText(MemId);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(505.0f, 280.0f);
                    pdfContentByte2.showText(GrpNumber);
                    pdfContentByte2.endText();

//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(210.0f, 220);
//                pdfContentByte2.showText("pat/guar signature");
//                pdfContentByte2.endText();
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(210.0f, 210.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(445.0f, 220.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(445.0f, 110.0f);
                    pdfContentByte2.showText("");//witness
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(518.0f, 110.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();

                }
                if (j == 2) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(190.0f, 300.0f);
                    pdfContentByte2.showText("");//spouse/fam
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(30.0f, 195.0f);
                    pdfContentByte2.showText("");//recibido
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(150.0f, 175.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(445.0f, 175.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(150.0f, 140.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(150.0f, 145.0f);
//                pdfContentByte2.showText("sign");
//                pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(375.0f, 145.0f);
                    pdfContentByte2.showText("");//guraantor rel
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(465.0f, 145.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(250.0f, 120.0f);
                    pdfContentByte2.showText("");//"sign of consent
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(470.0f, 120.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(445.0f, 83.0f);
                    pdfContentByte2.showText("");//witness
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(518.0f, 83.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();
                }
                if (j == 3) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 550.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 510.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(470.0f, 510.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(165.0f, 430.0f);
                    pdfContentByte2.showText("");//check box
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 355.0f);
                    pdfContentByte2.showText(Address);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 284.0f);
                    pdfContentByte2.showText("");//fax
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(500.0f, 284.0f);
                    pdfContentByte2.showText("");//phone
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 228.0f);
                    pdfContentByte2.showText("");//authorizarion
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(30.0f, 157.0f);
                    pdfContentByte2.showText("");//recibido
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(390.0f, 127.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100.0f, 120.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(100.0f, 127.0f);
//                pdfContentByte2.showText("sign");
//                pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();
                }
                if (j == 4) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(30.0f, 318.0f);
                    pdfContentByte2.showText("");//recibedo
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(150.0f, 292.0f);
                    pdfContentByte2.showText("");//Patname
                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(150.0f, 247.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(150.0f, 247.0f);
//                pdfContentByte2.showText("");//sign
//                pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(370.0f, 247.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(440.0f, 175.0f);
                    pdfContentByte2.showText("");//witness
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(520.0f, 175.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();
                }
                if (j == 5) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(130.0f, 560.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(345.0f, 560.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(130.0f, 525.0f);
                    pdfContentByte2.showText(PriInsuranceName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(130.0f, 485.0f);
                    pdfContentByte2.showText(MemId);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(335.0f, 485.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 400.0f);
                    pdfContentByte2.showText("");//vow
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(500.0f, 410.0f);
                    pdfContentByte2.showText("");//recibido
                    pdfContentByte2.endText();


                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(130.0f, 305.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

//                pdfContentByte2.beginText();
//                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                pdfContentByte2.setColorFill(BaseColor.BLACK);
//                pdfContentByte2.setTextMatrix(130.0f, 312.0f);
//                pdfContentByte2.showText("sign");
//                pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(325.0f, 312.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();
                }
                if (j == 6) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(130.0f, 565.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);//patname
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(360.0f, 565.0f);
//                    pdfContentByte2.showText(DOS);//date req
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(160.0f, 540.0f);
//                    pdfContentByte2.showText("");//realtion
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 540.0f);
//                    pdfContentByte2.showText("");//guardian name
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 512.0f);
//                    pdfContentByte2.showText(DOS);//DOS
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(75.0f, 462.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(75.0f, 434.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(75.0f, 408.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(75.0f, 380.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(245.0f, 462.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(245.0f, 434.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(245.0f, 408.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(245.0f, 380.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 308.0f);
//                    pdfContentByte2.showText("");//Other
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(100.0f, 255.0f);
//                    pdfContentByte2.showText("");//recibido
//                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100.0f, 200.0f);
                        pdfContentByte2.addImage(SignImages);
                    }


//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 205.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(420.0f, 143.0f);
//                    pdfContentByte2.showText("");//Dailylog
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(530.0f, 143.0f);
//                    pdfContentByte2.showText("");//By
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
                }
                if (j == 7) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(430.0f, 425.0f);
//                    pdfContentByte2.showText("");//CovidExpWhen
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(30.0f, 258.0f);
//                    pdfContentByte2.showText("");//DOS//recibido
//                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(50.0f, 210.0f);
                        pdfContentByte2.addImage(SignImages);
                    }


//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(275.0f, 215.0f);
//                    pdfContentByte2.showText(DOS);//DOS
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();

            PreparedStatement MainReceipt = conn.prepareStatement(
                    "INSERT INTO " + Database + ".BundleHistory (MRN ,PatientRegId ,BundleName ,CreatedDate,PgCount,VisitIndex )" +
                            " VALUES (? ,? ,? ,now(),?,?) ");
            MainReceipt.setString(1, MRN);
            MainReceipt.setInt(2, ID);
            MainReceipt.setString(3, filename);
            MainReceipt.setInt(4, pageCount);
            MainReceipt.setInt(5, VisitIndex);
            MainReceipt.executeUpdate();
            MainReceipt.close();

            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("outputFilePath", outputFilePath);
            Parser.SetField("VisitId", String.valueOf(VisitIndex));
            Parser.SetField("pageCount", String.valueOf(pageCount));
            Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
            Parser.SetField("FileName", filename);
            Parser.SetField("lang", "1");
            Parser.SetField("ClientID", String.valueOf(ClientId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/DownloadBundleHTML.html");

        } catch (Exception e) {
            System.out.println("in the catch exception of FrontlineBundleinside Function ");
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println(str);

        }

    }

    void GETINPUTFrontLine(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String filename = null;
        String outputFilePath = null;
        String pageCount = null;
        String lang = "";
        String SignedFrom = "";
        int PatientRegId = Integer.parseInt(request.getParameter("ID"));
        int VisitId = Integer.parseInt(request.getParameter("VisitId"));

        try {

            try {
                Query = "Select SignedFrom from " + Database + ".SignRequest where PatientRegId=" + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    SignedFrom = rset.getNString(1);
                } else {
                    SignedFrom = "REGISTRATION";
                }
                stmt.close();
                rset.close();


                Query = "Select BundleName,PgCount from " + Database + ".BundleHistory where PatientRegId=" + PatientRegId + "  And VisitIndex =" + VisitId + " AND BundleName NOT LIKE '%SPANISH%' ORDER BY CreatedDate DESC LIMIT 1";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);

                if (rset.next()) {
                    filename = rset.getString(1);
                    pageCount = rset.getString(2);

                    if (filename.contains("REGISTRATION"))
                        outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/REGISTRATION/" + filename;

                    else if (filename.contains("VISIT"))
                        outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/VISIT/" + filename;

                    else if (filename.contains("EDIT"))
                        outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/EDIT/" + filename;


                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("outputFilePath", outputFilePath);
                    Parser.SetField("pageCount", String.valueOf(pageCount));
                    Parser.SetField("VisitId", String.valueOf(VisitId));
                    Parser.SetField("lang", "1");
                    Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
                    Parser.SetField("FileName", filename);
                    Parser.SetField("ClientID", String.valueOf(ClientId));
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/DownloadBundleHTML.html");
                } else {
                    GETINPUTFrontLine_Bundle(request, out, conn, servletContext, response, UserId, Database, ClientId, DirectoryName, PatientRegId, SignedFrom, VisitId, helper);
                }
                stmt.close();
                rset.close();
            } catch (SQLException e) {
                System.out.println("in the catch exception of frontlinebundle Function ");
                System.out.println(e.getMessage());
                String str = "";
                for (int i = 0; i < e.getStackTrace().length; ++i) {
                    str = str + e.getStackTrace()[i] + "<br>";
                }
                System.out.println(str);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    void GETINPUTFrontLine_InsideForSpanish(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName, int patientRegId, String SignedFrom, int PatientVisitNumber, UtilityHelper helper) {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        String ResultPdf = "";
        final MergePdf mergePdf = new MergePdf();
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
        String StreetAddress2 = "";
        String CityStateZip = "";
        String State = "";
        String City = "";
        String DOBForAge = "";
        final String Country = "";
        String ZipCode = "";
        String SSN = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        String filename = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        String MRN = "";
        int ClientIndex = 0;
        String ClientName = "";
        String DOS = "";
        String DoctorId = null;
        String DoctorName = null;
        int FoundAddInfo = 0;
        String CovidTestDate = "";
        String Ethnicity = "";
        String Race = "";
        String TravellingChk = "";
        String TravelWhen = "";
        String TravelWhere = "";
        String TravelHowLong = "";
        String CovidExpWhen = "";
        String COVIDExposedChk = "";
        String SympFever = "";
        String SympBodyAches = "";
        String SympSoreThroat = "";
        String SympFatigue = "";
        String SympRash = "";
        String SympVomiting = "";
        String SympDiarrhea = "";
        String SympCough = "";
        String SympRunnyNose = "";
        String SympNausea = "";
        String SympFluSymptoms = "";
        String SympHeadache = "";
        String SympLossTaste = "";
        String SympShortBreath = "";
        String SympCongestion = "";
        String SympEyeConjunctivitis = "";
        String GuarantorName = "";
        String GuarantorDOB = "";
        String GuarantorSSN = "";
        int WorkersCompPolicy = 0;
        String WorkersCompPolicyString = "";
        int MotorVehAccident = 0;
        String MotorVehAccidentString = "";
        String PriInsurance = "";
        String MemId = "";
        String GrpNumber = "";
        String PriInsuranceName = "";
        String PriInsurerName = "";
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
        String DirectoryNameTow = "";
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
        String COVIDStatus = "";
        String VisitNumber = "";
        int SelfPayChk = 0;
        int VisitIndex = PatientVisitNumber;
        int VerifyChkBox = 0;
        int ID = patientRegId;
        String VisitId = "";
        try {
//            Query = "Select id FROM " + Database + ".PatientVisit ORDER BY CreatedDate DESC LIMIT 1";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                VisitIndex = rset.getInt(1);
//
//            }
//            rset.close();
//            stmt.close();

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
                Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), " +
                        "IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), " +
                        "IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-'), " +
                        "IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), " +
                        "IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, " +
                        "IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), " +
                        "IFNULL(DoctorsName,'-'),  " +
                        "CASE WHEN Ethnicity = 1 THEN 'Hispanic or Latino' WHEN Ethnicity = 2 THEN ' Non Hispanic or Latino' " +
                        "WHEN Ethnicity = 3 THEN 'Others' ELSE 'Others' END, IFNULL(City,''), IFNULL(State,''), IFNULL(ZipCode,''), " +
                        "IFNULL(COVIDStatus,''), IFNULL(StreetAddress2,''),IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'),'')  From " + Database + ".PatientReg  Where ID = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
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
                    Ethnicity = rset.getString(24);
                    City = rset.getString(25);
                    State = rset.getString(26);
                    ZipCode = rset.getString(27);
                    COVIDStatus = rset.getString(28);
                    StreetAddress2 = rset.getString(29);
                    DOBForAge = rset.getString(30);
                }
                rset.close();
                stmt.close();
                if (!DOB.equals("")) {
                    Age = String.valueOf(helper.getAge(LocalDate.parse(DOBForAge)));
                }
                Query = "Select name from oe.clients where Id = " + ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClientName = rset.getString(1);
                }
                rset.close();
                stmt.close();

                if (!DoctorId.equals("-")) {
                    Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        DoctorName = rset.getString(1);
                    }
                    rset.close();
                    stmt.close();
                } else {
                    DoctorName = "";
                }
            } catch (Exception e) {
                out.println("Error In PateintReg:--" + e.getMessage());
                out.println(Query);
            }
            String CovidTest = "";
            Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundAddInfo = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (FoundAddInfo > 0) {
//                Query = "Select IFNULL(Date_format(CovidTestDate,'%m/%d/%Y'),'') from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
                Query = "Select CASE WHEN COVIDPositveChk = 1 THEN 'YES' WHEN COVIDPositveChk = 0 THEN 'NO' ELSE 'NO' END," +
                        "IFNULL(Date_format(CovidPositiveDate,'%m/%d/%Y'),'') " +
                        "from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    CovidTest = rset.getString(1);
                    CovidTestDate = rset.getString(2);
                }
                rset.close();
                stmt.close();
            }
            try {
                Query = " Select CASE WHEN TravellingChk = 1 THEN 'YES' WHEN TravellingChk = 0 THEN 'NO' ELSE 'NO' END,  " +
                        " IFNULL(DATE_FORMAT(TravelWhen,'%m/%d/%Y'),''),  IFNULL(TravelWhere,''), IFNULL(TravelHowLong,''), " +
                        " CASE WHEN COVIDExposedChk = 1 THEN 'YES' WHEN COVIDExposedChk = 0 THEN 'NO' ELSE 'NO' END,  " +
                        " CASE WHEN SympFever = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympBodyAches = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympSoreThroat = 1 THEN 'YES' ELSE '' END,  " +
                        " CASE WHEN SympFatigue = 1 THEN 'YES' ELSE '' END," +
                        " CASE WHEN SympRash = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympVomiting = 1 THEN 'YES' ELSE '' END,  " +
                        " CASE WHEN SympDiarrhea = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympCough = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympRunnyNose = 1 THEN 'YES' ELSE '' END,  " +
                        " CASE WHEN SympNausea = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympFluSymptoms = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympEyeConjunctivitis = 1 THEN 'YES' ELSE '' END,  " +
                        " CASE WHEN Race = 1 THEN 'African American' WHEN Race = 2 THEN 'American Indian or Alska Native' " +
                        " WHEN Race = 3 THEN 'Asian' WHEN Race = 4 THEN 'Native Hawaiian or Other Pacific Islander'  " +
                        " WHEN Race = 5 THEN 'White' WHEN Race = 6 THEN 'Others' ELSE 'Others' END, IFNULL(DATE_FORMAT(CovidExpWhen,'%m/%d/%Y'),''), " +
                        " CASE WHEN SympHeadache = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympLossTaste = 1 THEN 'YES' ELSE '' END," +
                        " CASE WHEN SympShortBreath = 1 THEN 'YES' ELSE '' END, " +
                        " CASE WHEN SympCongestion = 1 THEN 'YES' ELSE '' END, " +
                        " CONCAT(IFNULL(GuarantorFirstName,''),' ',IFNULL(GuarantorLastName,'')), IFNULL(DATE_FORMAT(GuarantorDOB,'%m/%d/%Y'),''),IFNULL(GuarantorSSN,'')" +
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
                    SympHeadache = rset.getString(20);
                    SympLossTaste = rset.getString(21);
                    SympShortBreath = rset.getString(22);
                    SympCongestion = rset.getString(23);
                    GuarantorName = rset.getString(24);
                    GuarantorDOB = rset.getString(25);
                    GuarantorSSN = rset.getString(26);
                }
                rset.close();
                stmt.close();
            } catch (Exception e2) {
                out.println("Error in getting PatientReg_Details Table : " + e2.getMessage());
            }
            if (SelfPayChk == 1) {
                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-')," +
                        "IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), " +
                        "IFNULL(DATE_FORMAT(PrimaryDOB,'%m/%d/%Y'),'-'), IFNULL(PrimarySSN,'-'),  " +
                        "IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), " +
                        "IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), " +
                        "CONCAT(IFNULL(SubscriberFirstName,'-'),' ',IFNULL(SubscriberLastName,'-')), " +
                        "IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), " +
                        "IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-')," +
                        "CONCAT( IFNULL(PriInsurerFirstName,''),' ', IFNULL(PriInsurerLastName,'')) " +
                        "from " + Database + ".InsuranceInfo  where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    WorkersCompPolicy = rset.getInt(1);
                    MotorVehAccident = rset.getInt(2);
                    if (WorkersCompPolicy == 0) {
                        WorkersCompPolicyString = "N";
                    } else {
                        WorkersCompPolicyString = "Y";
                    }
                    if (MotorVehAccident == 0) {
                        MotorVehAccidentString = "N";
                    } else {
                        MotorVehAccidentString = "Y";
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
                    PriInsurerName = rset.getString(21);
                }
                rset.close();
                stmt.close();
            }
            if (!PriInsuranceName.equals("")) {
                Query = "Select IFNULL(PayerName,'') from " + Database + ".ProfessionalPayers where id = " + PriInsuranceName;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PriInsuranceName = rset.getString(1);
                }
                rset.close();
                stmt.close();
            }
            Query = "Select IFNULL(NextofKinName,'-'), IFNULL(RelationToPatient,'-'), IFNULL(PhoneNumber,'-'), " +
                    "CASE WHEN LeaveMessage = 1 THEN 'YES' WHEN LeaveMessage = 0 THEN 'NO' ELSE ' YES / NO' END,  " +
                    "IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-') " +
                    "from " + Database + ".EmergencyInfo where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                NextofKinName = rset.getString(1);
                RelationToPatientER = rset.getString(2);
                PhoneNumberER = rset.getString(3);
                LeaveMessageERString = rset.getString(4);
                AddressER = rset.getString(5);
                CityStateZipER = rset.getString(6);
            }
            rset.close();
            stmt.close();

            Query = "Select MAX(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                VisitNumber = rset.getString(1).trim();
            }
            rset.close();
            stmt.close();

            VisitNumber = "VN-" + MRN + "-" + VisitNumber;
            Query = " Select ReturnPatient, Google, MapSearch, Billboard, OnlineReview, TV, Website, BuildingSignDriveBy, " +
                    "Facebook, School, IFNULL(School_text ,'-'), Twitter, Magazine, IFNULL(Magazine_text,'-'), " +
                    "Newspaper, IFNULL(Newspaper_text,'-'), FamilyFriend, IFNULL(FamilyFriend_text,'-'), UrgentCare, " +
                    "IFNULL(UrgentCare_text,'-'), CommunityEvent, IFNULL(CommunityEvent_text,'-'),  IFNULL(Work_text,'-')," +
                    "IFNULL(Physician_text, '-'), IFNULL(Other_text,'-') " +
                    "from " + Database + ".RandomCheckInfo where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
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

            String HearAboutUsString = "";
            if (ReturnPatient.toUpperCase().equals("YES")) {
                HearAboutUsString += "Return Patient, ";
            }
            if (Google.toUpperCase().equals("YES")) {
                HearAboutUsString += "Google, ";
            }
            if (MapSearch.toUpperCase().equals("YES")) {
                HearAboutUsString += "Map Search, ";
            }
            if (OnlineReview.toUpperCase().equals("YES")) {
                HearAboutUsString += "Online Review, ";
            }
            if (TV.toUpperCase().equals("YES")) {
                HearAboutUsString += "TV, ";
            }
            if (Website.toUpperCase().equals("YES")) {
                HearAboutUsString += "Website, ";
            }
            if (BuildingSignDriveBy.toUpperCase().equals("YES")) {
                HearAboutUsString += "Building Sign, ";
            }
            if (Facebook.toUpperCase().equals("YES")) {
                HearAboutUsString += "Facebook, ";
            }
            if (School.toUpperCase().equals("YES")) {
                HearAboutUsString += "School, ";
            }
            if (Twitter.toUpperCase().equals("YES")) {
                HearAboutUsString += "Twitter, ";
            }
            if (Magazine.toUpperCase().equals("YES")) {
                HearAboutUsString += "Magazine, ";
            }
            if (Newspaper.toUpperCase().equals("YES")) {
                HearAboutUsString += "Newspaper, ";
            }
            if (FamilyFriend.toUpperCase().equals("YES")) {
                HearAboutUsString += "Friend / Family, ";
            }
            if (UrgentCare.toUpperCase().equals("YES")) {
                HearAboutUsString += "Urgent Care, ";
            }
            if (CommunityEvent.toUpperCase().equals("YES")) {
                HearAboutUsString += "Comminuty Event, ";
            }
            if ("".toUpperCase().equals("YES")) {
                HearAboutUsString += "Work, ";
            }
            if ("".toUpperCase().equals("YES")) {
                HearAboutUsString += "Physician, ";
            }
            if ("".toUpperCase().equals("YES")) {
                HearAboutUsString += "Others ";
            }
            String SymptomsString = "";
            if (SympFever.toUpperCase().equals("YES")) {
                SymptomsString += "Fever, ";
            }
            if (SympBodyAches.toUpperCase().equals("YES")) {
                SymptomsString += "Body Aches, ";
            }
            if (SympSoreThroat.toUpperCase().equals("YES")) {
                SymptomsString += "Sore Throat, ";
            }
            if (SympFatigue.toUpperCase().equals("YES")) {
                SymptomsString += "Fatigue, ";
            }
            if (SympRash.toUpperCase().equals("YES")) {
                SymptomsString += "Rash, ";
            }
            if (SympVomiting.toUpperCase().equals("YES")) {
                SymptomsString += "Vomitting, ";
            }
            if (SympDiarrhea.toUpperCase().equals("YES")) {
                SymptomsString += "Diarrhea, ";
            }
            if (SympCough.toUpperCase().equals("YES")) {
                SymptomsString += "Cough, ";
            }
            if (SympRunnyNose.toUpperCase().equals("YES")) {
                SymptomsString += "Runny Nose, ";
            }
            if (SympNausea.toUpperCase().equals("YES")) {
                SymptomsString += "Nausea, ";
            }
            if (SympFluSymptoms.toUpperCase().equals("YES")) {
                SymptomsString += "Flu-like Symptoms, ";
            }
            if (SympEyeConjunctivitis.toUpperCase().equals("YES")) {
                SymptomsString += "Eye Conjunctivitis, ";
            }
            if (SympHeadache.toUpperCase().equals("YES")) {
                SymptomsString += "Headache, ";
            }
            if (SympLossTaste.toUpperCase().equals("YES")) {
                SymptomsString += "Loss of Taste/Smell, ";
            }
            if (SympShortBreath.toUpperCase().equals("YES")) {
                SymptomsString += "Short of Breathness, ";
            }
            if (SympCongestion.toUpperCase().equals("YES")) {
                SymptomsString += "Congestion ";
            }

            String inputFilePathTmp = "";
            String outputFilePathTmp = "";
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/FinancialHardShip_SPANISH.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_SPANISH_" + ClientId + "_" + MRN + ".pdf";
            OutputStream fos1 = new FileOutputStream(new File(outputFilePathTmp));
            PdfReader pdfReader1 = new PdfReader(inputFilePathTmp);
            PdfStamper pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 582.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 582.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0f, 558.0f);
                    pdfContentByte.showText("");//realtion
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420.0f, 558.0f);
                    pdfContentByte.showText("");//guardian name
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0f, 530.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60.0f, 482.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60.0f, 454.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60.0f, 430.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60.0f, 402.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275.0f, 482.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275.0f, 454.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275.0f, 430.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275.0f, 402.0f);
                    pdfContentByte.showText("");//Y
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 345.0f);
                    pdfContentByte.showText("");//OTRO
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 308.0f);
                    pdfContentByte.showText("");//Other
                    pdfContentByte.endText();

//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(100.0f, 227.0f);
//                    pdfContentByte.showText("");//sign
//                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(360.0f, 225.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460.0f, 134.0f);
                    pdfContentByte.showText("");//diario
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(525.0f, 134.0f);
                    pdfContentByte.showText("");//by
                    pdfContentByte.endText();

//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/PromptPayAgreement_SPANISH.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_SPANISH_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(315.0f, 300.0f);
//                    pdfContentByte.showText(DOS);
//                    pdfContentByte.endText();
//
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460.0f, 393.0f);
                    pdfContentByte.showText("$");
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(55.0f, 200.0f);
                    pdfContentByte.showText("");//recebido
                    pdfContentByte.endText();

//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(100.0f, 155.0f);
//                    pdfContentByte.showText("");//sign
//                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(360.0f, 155.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();

//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte.endText();


                }
            }
            pdfStamper1.close();

            String pattern = null;
            Query = "SELECT Form_ids FROM " + Database + ".BundleForms where PatientRegId='" + ID + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                pattern = rset.getString(1);
            }
            rset.close();
            stmt.close();

            if (pattern != null) {
                String[] pat = pattern.split("\\^");//pat[0] -> mva, pat[1] -> prompt pay, pat[2] -> Financial Harship
                if (pat[0].equals("1")) {//mva
                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER_SPANISH.pdf";
                }

                if (pat[2].equals("1")) {// financial hardship
                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER_SPANISH.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_SPANISH_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    if (ClientId == 27) {
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
                    } else {
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
                    }

                    if (pat[1].equals("1")) { //prompt pay
                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_SPANISH_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                        if (ClientId == 27) {
                            ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
                        } else {
                            ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
                        }
                    }

                }

                if (pat[1].equals("1") && !(pat[2].equals("1"))) {//prompt pay
                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER_SPANISH.pdf";
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_SPANISH_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    if (ClientId == 27) {
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
                    } else {
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
                    }
                }
            } else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER_SPANISH.pdf";
            }


            if (PriInsuranceName.toUpperCase().contains("UNITED HEALTHCARE")) {
                String inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/Commercial-Courtesy-Review-Auth-Form_ES.pdf";
                String outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/Commercial-Courtesy-Review-Auth-Form_ES_" + ClientId + "_" + MRN + ".pdf";

                ResultPdf = AttachUHC_Form_ES(MemId, PrimaryDOB, PriInsurerName, DOS, PatientRelationtoPrimary, Date, outputFilePathTmp2, inputFilePathTmp2,
                        request, response, out, conn, Database, ResultPdf, DirectoryName, ClientId, MRN, mergePdf);
            }

            int found = 0;
            Query = "Select Count(*) from " + Database + ".BundleHistory where PatientRegId=" + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                found = rset.getInt(1);
            }
            stmt.close();
            rset.close();

            if (SignedFrom.contains("REGISTRATION")) {
                DirectoryNameTow = "REGISTRATION";
            } else if (SignedFrom.contains("VISIT")) {
                DirectoryNameTow = "VISIT";
            } else if (SignedFrom.contains("EDIT")) {
                DirectoryNameTow = "EDIT";
            }

            filename = FirstNameNoSpaces + LastName + ID + "_" + found + "_" + SignedFrom + "_SPANISH.pdf";

            String inputFilePath = "";
            String outputFilePath = "";
            String UID = "";
            inputFilePath = ResultPdf;
            outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + DirectoryNameTow + "/" + filename;
            final OutputStream fos2 = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader2 = new PdfReader(inputFilePath);
            final int pageCount = pdfReader2.getNumberOfPages();
            final PdfStamper pdfStamper2 = new PdfStamper(pdfReader2, fos2);
            Image SignImages = null;
            final File tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_0_" + ID + ".png");
            final boolean exists = tmpDir.exists();
            if (exists) {
                Query = "Select UID from " + Database + ".SignRequest where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    UID = rset.getString(1);
                }
                rset.close();
                stmt.close();

                SignImages = Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_0_" + ID + ".png");
                SignImages.scaleAbsolute(80.0f, 30.0f);
            } else {
                SignImages = null;
            }

            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(40.0f, 770.0f);
//                    pdfContentByte2.showText(UID);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(130.0f, 685.0f);
//                    pdfContentByte2.showText(ReasonVisit);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(392.0f, 685.0f);
//                    pdfContentByte2.showText(MotorVehAccidentString);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(456.0f, 685.0f);
//                    pdfContentByte2.showText(WorkersCompPolicyString);
//                    pdfContentByte2.endText();
//                    if (SelfPayChk == 1) {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("(Y)");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("(N)");
//                        pdfContentByte2.endText();
//                    } else if (SelfPayChk == 0) {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("(N)");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("(Y)");
//                        pdfContentByte2.endText();
//                    } else {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("");
//                        pdfContentByte2.endText();
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 660.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 660.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(520.0f, 660.0f);
//                    pdfContentByte2.showText(Age);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 620.0f);
//                    pdfContentByte2.showText(Address);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(300.0f, 620.0f);
//                    pdfContentByte2.showText(StreetAddress2);//ApptNo
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(460.0f, 620.0f);
//                    pdfContentByte2.showText(SSN);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 595.0f);
//                    pdfContentByte2.showText(City);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(260.0f, 595.0f);
//                    pdfContentByte2.showText(State);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 595.0f);
//                    pdfContentByte2.showText(ZipCode);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(110.0f, 575.0f);
//                    pdfContentByte2.showText(PhNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(250.0f, 575.0f);
//                    pdfContentByte2.showText(PhNumber);
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 575.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(100.0f, 550.0f);
//                    pdfContentByte2.showText(PrimaryEmployer);
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(395.0f, 550.0f);
//                    pdfContentByte2.showText(PrimaryOccupation);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 527.0f);
//                    pdfContentByte2.showText(EmployerAddress);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(409.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(500.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(105.0f, 505.0f);
//                    pdfContentByte2.showText(MaritalStatus);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(250.0f, 505.0f);
//                    pdfContentByte2.showText(gender);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 505.0f);
//                    pdfContentByte2.showText(Email);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 485.0f);
//                    pdfContentByte2.showText(NextofKinName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(360.0f, 485.0f);
//                    pdfContentByte2.showText(PhoneNumberER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(520.0f, 485.0f);
//                    pdfContentByte2.showText(RelationToPatientER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(100.0f, 463.0f);
//                    pdfContentByte2.showText(AddressER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(455.0f, 463.0f);
//                    pdfContentByte2.showText(" ,  , ");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(310.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorDOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(480.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorSSN);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 360.0f);
//                    pdfContentByte2.showText(PriInsurerName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(310.0f, 360);
//                    pdfContentByte2.showText(PrimaryDOB);//primary DOB
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(480.0f, 360);
//                    pdfContentByte2.showText(PrimarySSN);//Primary SSN
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 335.0f);
//                    pdfContentByte2.showText(PriInsuranceName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(445.0f, 335.0f);
//                    pdfContentByte2.showText(MemId);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(540.0f, 335.0f);
//                    pdfContentByte2.showText(GrpNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 223.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 195.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(200.0f, 215.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 192.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 134.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 123.0f);
//                    pdfContentByte2.showText(ClientName + "  Sex: " + gender);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 113.0f);
//                    pdfContentByte2.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 103.0f);
//                    pdfContentByte2.showText("MRN:" + MRN + "  DOS:" + DOS + "");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 93.0f);
//                    pdfContentByte2.showText("ACT#: " + VisitNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 83.0f);
//                    pdfContentByte2.showText("Dr. " + DoctorName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 2) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(190.0f, 142.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(425.0f, 100.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(170.0f, 90.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(425.0f, 83.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(430.0f, 63.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 3) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 613.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 583.0f);
//                    pdfContentByte2.showText(Address);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 550.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(370.0f, 550.0f);
//                    pdfContentByte2.showText(SSN);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(110.0f, 140.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(430.0f, 150.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 4) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(180.0f, 140.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 140.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 5) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(190.0f, 335.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(220.0f, 275.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 285.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 252.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 6) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 530.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 530.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 490.0f);
//                    pdfContentByte2.showText(PriInsuranceName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(170.0f, 446.0f);
//                    pdfContentByte2.showText(MemId);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 446.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(210.0f, 200.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 216.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 7) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 473.0f);
//                    pdfContentByte2.showText(CovidTest);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 473.0f);
//                    pdfContentByte2.showText(CovidTestDate);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 442.0f);
//                    pdfContentByte2.showText(COVIDExposedChk);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(525.0f, 442.0f);
//                    pdfContentByte2.showText(CovidExpWhen);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(325.0f, 415.0f);
//                    pdfContentByte2.showText(SymptomsString);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 390.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(150.0f, 110.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 115.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(110.0f, 65.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 65.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();


                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(150.0f, 655.0f);
                    pdfContentByte2.showText(ReasonVisit);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(90.0f, 620.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName);//name
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(445.0f, 620.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(515.0f, 620.0f);
                    pdfContentByte2.showText(Age);
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(110.0f, 585.0f);
                    pdfContentByte2.showText(Address);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(355.0f, 585.0f);
                    pdfContentByte2.showText("");//dep
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(490.0f, 585.0f);
                    pdfContentByte2.showText(SSN);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 550.0f);
                    pdfContentByte2.showText(City);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(320.0f, 550.0f);
                    pdfContentByte2.showText(State);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(475.0f, 550.0f);
                    pdfContentByte2.showText(ZipCode);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(130.0f, 515.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 515.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 480.0f);
                    pdfContentByte2.showText(gender);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(300.0f, 480.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(230.0f, 445.0f);
                    pdfContentByte2.showText(PhoneNumberER);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(405.0f, 445.0f);
                    pdfContentByte2.showText(PhoneNumberER);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(520.0f, 445.0f);
                    pdfContentByte2.showText(RelationToPatientER);
                    pdfContentByte2.endText();


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(200.0f, 350.0f);
                    pdfContentByte2.showText(GuarantorName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(420.0f, 350.0f);
                    pdfContentByte2.showText(GuarantorDOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(520.0f, 350.0f);
                    pdfContentByte2.showText("");//Phone
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(200.0f, 315.0f);
                    pdfContentByte2.showText(PriInsurerName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(395.0f, 315.0f);
                    pdfContentByte2.showText(PrimaryDOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(510.0f, 315.0f);
                    pdfContentByte2.showText(PrimarySSN);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(165.0f, 280.0f);
                    pdfContentByte2.showText(PriInsuranceName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(410.0f, 280.0f);
                    pdfContentByte2.showText("");//Subscriber
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(535.0f, 280.0f);
                    pdfContentByte2.showText(GrpNumber);
                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(205.0f, 235.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(205.0f, 235.0f);
//                    pdfContentByte2.showText("sign");
//                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(450.0f, 235.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(460.0f, 100.0f);
                    pdfContentByte2.showText("");//Iniciales
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(540.0f, 100.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();


                }
                if (j == 2) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(190.0f, 240.0f);
                    pdfContentByte2.showText("");//spouse/fam
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(180.0f, 135.0f);
                    pdfContentByte2.showText("");//pname
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(500.0f, 135.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(150.0f, 111.0f);
                    pdfContentByte2.showText("");//guarantor
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(320.0f, 111.0f);
                    pdfContentByte2.showText("");//firma
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(440.0f, 111.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(290.0f, 87.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(290.0f, 87.0f);
//                    pdfContentByte2.showText("");//sign
//                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(440.0f, 87.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(460.0f, 45.0f);
                    pdfContentByte2.showText("");//testingo
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(540.0f, 45.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();

                }
                if (j == 3) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);


                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 533.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 495.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(500.0f, 495.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(157.0f, 420.0f);
                    pdfContentByte2.showText("");//checkbox
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 350.0f);
                    pdfContentByte2.showText(Address);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 279.0f);
                    pdfContentByte2.showText("");//fax
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(500.0f, 279.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(430.0f, 240.0f);
                    pdfContentByte2.showText("");//authorizarion
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(40.0f, 155.0f);
                    pdfContentByte2.showText("");//recibido
                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100.0f, 128.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(100.0f, 128.0f);
//                    pdfContentByte2.showText("sign");
//                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(370.0f, 128.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();


                }
                if (j == 4) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(360.0f, 283.0f);
                    pdfContentByte2.showText("");//Nombre
                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(210.0f, 245.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(210.0f, 245.0f);
//                    pdfContentByte2.showText("sign");
//                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(350.0f, 245.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(455.0f, 167.0f);
                    pdfContentByte2.showText("");//Iniciales
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(530.0f, 167.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();

                }
                if (j == 5) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(130.0f, 568.0f);
                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(345.0f, 568.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(130.0f, 530.0f);
                    pdfContentByte2.showText(PriInsuranceName);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(130.0f, 493.0f);
                    pdfContentByte2.showText(MemId);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(365.0f, 493.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(180.0f, 410.0f);
                    pdfContentByte2.showText("");//vow
                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(150.0f, 310.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 310.0f);
//                    pdfContentByte2.showText("Sign");
//                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(320.0f, 310.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();

                }
                if (j == 6) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(160.0f, 582.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 582.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(170.0f, 558.0f);
//                    pdfContentByte2.showText("");//realtion
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(420.0f, 558.0f);
//                    pdfContentByte2.showText("");//guardian name
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(170.0f, 530.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(60.0f, 482.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(60.0f, 454.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(60.0f, 430.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(60.0f, 402.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(275.0f, 482.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(275.0f, 454.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(275.0f, 430.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(275.0f, 402.0f);
//                    pdfContentByte2.showText("");//Y
//                    pdfContentByte2.endText();
//
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 345.0f);
//                    pdfContentByte2.showText("");//OTRO
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 308.0f);
//                    pdfContentByte2.showText("");//Other
//                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100.0f, 227.0f);
                        pdfContentByte2.addImage(SignImages);
                    }


//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(360.0f, 225.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(460.0f, 134.0f);
//                    pdfContentByte2.showText("");//diario
//                    pdfContentByte2.endText();
//
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(525.0f, 134.0f);
//                    pdfContentByte2.showText("");//by
//                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();

                }
                if (j == 7) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(460.0f, 393.0f);
//                    pdfContentByte2.showText("");//$
//                    pdfContentByte2.endText();
//
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(55.0f, 200.0f);
//                    pdfContentByte2.showText("");//recebido
//                    pdfContentByte2.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100.0f, 155.0f);
                        pdfContentByte2.addImage(SignImages);
                    }


//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(360.0f, 155.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();

                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
                    pdfContentByte2.endText();


                }
            }
            pdfStamper2.close();

            PreparedStatement MainReceipt = conn.prepareStatement(
                    "INSERT INTO " + Database + ".BundleHistory (MRN ,PatientRegId ,BundleName ,CreatedDate,PgCount,VisitIndex )" +
                            " VALUES (? ,? ,? ,now(),?,?) ");
            MainReceipt.setString(1, MRN);
            MainReceipt.setInt(2, ID);
            MainReceipt.setString(3, filename);
            MainReceipt.setInt(4, pageCount);
            MainReceipt.setInt(5, VisitIndex);
            MainReceipt.executeUpdate();
            MainReceipt.close();

            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_SPANISH_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_SPANISH_" + ClientId + "_" + MRN + ".pdf");
            File.delete();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("outputFilePath", outputFilePath);
            Parser.SetField("pageCount", String.valueOf(pageCount));
            Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
            Parser.SetField("VisitId", String.valueOf(VisitIndex));
            Parser.SetField("FileName", filename);
            Parser.SetField("lang", "2");
            Parser.SetField("ClientID", String.valueOf(ClientId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/DownloadBundleHTML.html");
        } catch (Exception e) {
            System.out.println("in the catch exception of FrontlineBundleinside Function ");
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println(str);

        }


    }

    //    void GETINPUTFrontLine(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
//        Statement stmt = null;
//        ResultSet rset = null;
//        String Query = "";
//        int PatientRegId = 0;
//        String DateTime = "";
//        String Date = "";
//        String Time = "";
//        String ResultPdf = "";
//        final MergePdf mergePdf = new MergePdf();
//        String Title = "";
//        String FirstName = "";
//        String FirstNameNoSpaces = "";
//        String LastName = "";
//        String MiddleInitial = "";
//        String MaritalStatus = "";
//        String DOB = "";
//        String Age = "";
//        String gender = "";
//        String Email = "";
//        String PhNumber = "";
//        String Address = "";
//        String StreetAddress2 = "";
//        String CityStateZip = "";
//        String State = "";
//        String City = "";
//        final String Country = "";
//        String ZipCode = "";
//        String SSN = "";
//        String Occupation = "";
//        String Employer = "";
//        String EmpContact = "";
//        String PriCarePhy = "";
//        String ReasonVisit = "";
//        String MRN = "";
//        int ClientIndex = 0;
//        String ClientName = "";
//        String DOS = "";
//        String DoctorId = null;
//        String DoctorName = null;
//        int FoundAddInfo = 0;
//        String CovidTestDate = "";
//        String Ethnicity = "";
//        String Race = "";
//        String TravellingChk = "";
//        String TravelWhen = "";
//        String TravelWhere = "";
//        String TravelHowLong = "";
//        String CovidExpWhen = "";
//        String COVIDExposedChk = "";
//        String SympFever = "";
//        String SympBodyAches = "";
//        String SympSoreThroat = "";
//        String SympFatigue = "";
//        String SympRash = "";
//        String SympVomiting = "";
//        String SympDiarrhea = "";
//        String SympCough = "";
//        String SympRunnyNose = "";
//        String SympNausea = "";
//        String SympFluSymptoms = "";
//        String SympHeadache = "";
//        String SympLossTaste = "";
//        String SympShortBreath = "";
//        String SympCongestion = "";
//        String SympEyeConjunctivitis = "";
//        String GuarantorName = "";
//        String GuarantorDOB = "";
//        String GuarantorSSN = "";
//        int WorkersCompPolicy = 0;
//        String WorkersCompPolicyString = "";
//        int MotorVehAccident = 0;
//        String MotorVehAccidentString = "";
//        String PriInsurance = "";
//        String MemId = "";
//        String GrpNumber = "";
//        String PriInsuranceName = "";
//        String PriInsurerName = "";
//        String AddressIfDifferent = "";
//        String PrimaryDOB = "";
//        String PrimarySSN = "";
//        String PatientRelationtoPrimary = "";
//        String PrimaryOccupation = "";
//        String PrimaryEmployer = "";
//        String EmployerAddress = "";
//        String EmployerPhone = "";
//        String SecondryInsurance = "";
//        String SubscriberName = "";
//        String SubscriberDOB = "";
//        String MemberID_2 = "";
//        String GroupNumber_2 = "";
//        String PatientRelationshiptoSecondry = "";
//        String NextofKinName = "";
//        String RelationToPatientER = "";
//        String PhoneNumberER = "";
//        final int LeaveMessageER = 0;
//        String AddressER = "";
//        final String CityER = "";
//        final String StateER = "";
//        String LeaveMessageERString = "";
//        String CityStateZipER = "";
//        final String CountryER = "";
//        final String ZipCodeER = "";
//        final String DateConcent = "";
//        final String WitnessConcent = "";
//        final String PatientBehalfConcent = "";
//        final String RelativeSignConcent = "";
//        final String DateConcent2 = "";
//        final String WitnessConcent2 = "";
//        final String PatientSignConcent = "";
//        String ReturnPatient = "";
//        String Google = "";
//        String MapSearch = "";
//        String Billboard = "";
//        String OnlineReview = "";
//        String TV = "";
//        String Website = "";
//        String BuildingSignDriveBy = "";
//        String Facebook = "";
//        String School = "";
//        String School_text = "";
//        String Twitter = "";
//        String Magazine = "";
//        String Magazine_text = "";
//        String Newspaper = "";
//        String Newspaper_text = "";
//        String FamilyFriend = "";
//        String FamilyFriend_text = "";
//        String UrgentCare = "";
//        String UrgentCare_text = "";
//        String CommunityEvent = "";
//        String CommunityEvent_text = "";
//        final String Work = "";
//        String Work_text = "";
//        final String Physician = "";
//        String Physician_text = "";
//        final String Other = "";
//        String Other_text = "";
//        String COVIDStatus = "";
//        String VisitNumber = "";
//        int SelfPayChk = 0;
//        int VerifyChkBox = 0;
//        int ID = Integer.parseInt(request.getParameter("ID").trim());
//        String VisitId = request.getParameter("VisitId");
//        try {
//            Query = "select date_format(now(),'%Y%m%d%H%i%s'), DATE_FORMAT(now(), '%m/%d/%Y'), DATE_FORMAT(now(), '%T')";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                DateTime = rset.getString(1);
//                Date = rset.getString(2);
//                Time = rset.getString(3);
//            }
//            rset.close();
//            stmt.close();
//            try {
//                Query = " Select IFNULL(a.LastName,'-'), IFNULL(a.FirstName,'-'), IFNULL(a.MiddleInitial,'-'), IFNULL(a.Title,'-'), " +
//                        "IFNULL(a.MaritalStatus, '-'),  IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'), '-'),  IFNULL(a.Age, '0'), IFNULL(a.Gender, '-'), " +
//                        "IFNULL(a.Address,'-'), IFNULL(CONCAT(a.City,' / ', a.State, ' / ', a.ZipCode),'-'), IFNULL(a.PhNumber,'-'), " +
//                        "IFNULL(a.SSN,'-'), IFNULL(a.Occupation,'-'), IFNULL(a.Employer,'-'), IFNULL(a.EmpContact,'-'), IFNULL(a.PriCarePhy,'-'), " +
//                        "IFNULL(a.Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(a.SelfPayChk,0), IFNULL(a.MRN,0), a.ClientIndex, " +
//                        "IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T')), " +
//                        "IFNULL(a.DoctorsName,'-'),  " +
//                        "CASE WHEN a.Ethnicity = 1 THEN 'Hispanic or Latino' WHEN a.Ethnicity = 2 THEN ' Non Hispanic or Latino' " +
//                        "WHEN a.Ethnicity = 3 THEN 'Others' ELSE 'Others' END, IFNULL(a.City,''), IFNULL(a.State,''), IFNULL(a.ZipCode,''), " +
//                        "IFNULL(a.COVIDStatus,''), IFNULL(a.StreetAddress2,'')  From " + Database + ".PatientReg a inner join "+Database+".PatientVisit b ON a.ID = b.PatientRegId Where a.ID = " + ID +" AND  b.Id="+ VisitId;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                while (rset.next()) {
//                    PatientRegId = ID;
//                    LastName = rset.getString(1).trim();
//                    FirstName = rset.getString(2).trim();
//                    FirstNameNoSpaces = FirstName.replaceAll("\\s+", "");
//                    MiddleInitial = rset.getString(3).trim();
//                    Title = rset.getString(4).trim();
//                    MaritalStatus = rset.getString(5);
//                    DOB = rset.getString(6);
//                    Age = rset.getString(7);
//                    gender = rset.getString(8);
//                    Address = rset.getString(9);
//                    CityStateZip = rset.getString(10);
//                    PhNumber = rset.getString(11);
//                    SSN = rset.getString(12);
//                    Occupation = rset.getString(13);
//                    Employer = rset.getString(14);
//                    EmpContact = rset.getString(15);
//                    PriCarePhy = rset.getString(16);
//                    Email = rset.getString(17);
//                    ReasonVisit = rset.getString(18);
//                    SelfPayChk = rset.getInt(19);
//                    MRN = rset.getString(20);
//                    ClientIndex = rset.getInt(21);
//                    DOS = rset.getString(22);
//                    DoctorId = rset.getString(23);
//                    Ethnicity = rset.getString(24);
//                    City = rset.getString(25);
//                    State = rset.getString(26);
//                    ZipCode = rset.getString(27);
//                    COVIDStatus = rset.getString(28);
//                    StreetAddress2 = rset.getString(29);
//                }
//                rset.close();
//                stmt.close();
//                Query = "Select name from oe.clients where Id = " + ClientId;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    ClientName = rset.getString(1);
//                }
//                rset.close();
//                stmt.close();
//                if (!DoctorId.equals("-")) {
//                    Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorId;
//                    stmt = conn.createStatement();
//                    rset = stmt.executeQuery(Query);
//                    while (rset.next()) {
//                        DoctorName = rset.getString(1);
//                    }
//                    rset.close();
//                    stmt.close();
//                } else {
//                    DoctorName = "";
//                }
//            } catch (Exception e) {
//                out.println("Error In PateintReg:--" + e.getMessage());
//                out.println(Query);
//            }
//            String CovidTest = "";
////            if (COVIDStatus.equals("1")) {
////                CovidTest = "YES";
////            } else if (COVIDStatus.equals("0")) {
////                CovidTest = "NO";
////            } else {
////                CovidTest = "";
////            }
//            Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                FoundAddInfo = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();
//            if (FoundAddInfo > 0) {
////                Query = "Select IFNULL(Date_format(CovidTestDate,'%m/%d/%Y'),'') from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
//                Query = "Select CASE WHEN COVIDPositveChk = 1 THEN 'YES' WHEN COVIDPositveChk = 0 THEN 'NO' ELSE 'NO' END,IFNULL(Date_format(CovidPositiveDate,'%m/%d/%Y'),'') from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    CovidTest = rset.getString(1);
//                    CovidTestDate = rset.getString(2);
//                }
//                rset.close();
//                stmt.close();
//                //out.println(Query);
//            }
//            try {
//                Query = " Select CASE WHEN TravellingChk = 1 THEN 'YES' WHEN TravellingChk = 0 THEN 'NO' ELSE 'NO' END,  " +
//                        " IFNULL(DATE_FORMAT(TravelWhen,'%m/%d/%Y'),''),  IFNULL(TravelWhere,''), IFNULL(TravelHowLong,''), " +
//                        " CASE WHEN COVIDExposedChk = 1 THEN 'YES' WHEN COVIDExposedChk = 0 THEN 'NO' ELSE 'NO' END,  " +
//                        " CASE WHEN SympFever = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympBodyAches = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympSoreThroat = 1 THEN 'YES' ELSE '' END,  " +
//                        " CASE WHEN SympFatigue = 1 THEN 'YES' ELSE '' END," +
//                        " CASE WHEN SympRash = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympVomiting = 1 THEN 'YES' ELSE '' END,  " +
//                        " CASE WHEN SympDiarrhea = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympCough = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympRunnyNose = 1 THEN 'YES' ELSE '' END,  " +
//                        " CASE WHEN SympNausea = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympFluSymptoms = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympEyeConjunctivitis = 1 THEN 'YES' ELSE '' END,  " +
//                        " CASE WHEN Race = 1 THEN 'African American' WHEN Race = 2 THEN 'American Indian or Alska Native' " +
//                        " WHEN Race = 3 THEN 'Asian' WHEN Race = 4 THEN 'Native Hawaiian or Other Pacific Islander'  " +
//                        " WHEN Race = 5 THEN 'White' WHEN Race = 6 THEN 'Others' ELSE 'Others' END, IFNULL(DATE_FORMAT(CovidExpWhen,'%m/%d/%Y'),''), " +
//                        " CASE WHEN SympHeadache = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympLossTaste = 1 THEN 'YES' ELSE '' END," +
//                        " CASE WHEN SympShortBreath = 1 THEN 'YES' ELSE '' END, " +
//                        " CASE WHEN SympCongestion = 1 THEN 'YES' ELSE '' END, " +
//                        " CONCAT(IFNULL(GuarantorFirstName,''),' ',IFNULL(GuarantorLastName,'')), IFNULL(DATE_FORMAT(GuarantorDOB,'%m/%d/%Y'),''),IFNULL(GuarantorSSN,'')" +
//                        " from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    TravellingChk = rset.getString(1);
//                    TravelWhen = rset.getString(2);
//                    TravelWhere = rset.getString(3);
//                    TravelHowLong = rset.getString(4);
//                    COVIDExposedChk = rset.getString(5);
//                    SympFever = rset.getString(6);
//                    SympBodyAches = rset.getString(7);
//                    SympSoreThroat = rset.getString(8);
//                    SympFatigue = rset.getString(9);
//                    SympRash = rset.getString(10);
//                    SympVomiting = rset.getString(11);
//                    SympDiarrhea = rset.getString(12);
//                    SympCough = rset.getString(13);
//                    SympRunnyNose = rset.getString(14);
//                    SympNausea = rset.getString(15);
//                    SympFluSymptoms = rset.getString(16);
//                    SympEyeConjunctivitis = rset.getString(17);
//                    Race = rset.getString(18);
//                    CovidExpWhen = rset.getString(19);
//                    SympHeadache = rset.getString(20);
//                    SympLossTaste = rset.getString(21);
//                    SympShortBreath = rset.getString(22);
//                    SympCongestion = rset.getString(23);
//                    GuarantorName = rset.getString(24);
//                    GuarantorDOB = rset.getString(25);
//                    GuarantorSSN = rset.getString(26);
//                }
//                rset.close();
//                stmt.close();
//            } catch (Exception e2) {
//                out.println("Error in getting PatientReg_Details Table : " + e2.getMessage());
//            }
//            if (SelfPayChk == 1) {
//                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%m/%d/%Y'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), CONCAT(IFNULL(SubscriberFirstName,'-'),' ',IFNULL(SubscriberLastName,'-')), IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-'),CONCAT( IFNULL(PriInsurerFirstName,''),' ', IFNULL(PriInsurerLastName,'')) from " + Database + ".InsuranceInfo  where PatientRegId = " + ID;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                while (rset.next()) {
//                    WorkersCompPolicy = rset.getInt(1);
//                    MotorVehAccident = rset.getInt(2);
//                    if (WorkersCompPolicy == 0) {
//                        WorkersCompPolicyString = "N";
//                    } else {
//                        WorkersCompPolicyString = "Y";
//                    }
//                    if (MotorVehAccident == 0) {
//                        MotorVehAccidentString = "N";
//                    } else {
//                        MotorVehAccidentString = "Y";
//                    }
//                    PriInsurance = rset.getString(3);
//                    MemId = rset.getString(4);
//                    GrpNumber = rset.getString(5);
//                    PriInsuranceName = rset.getString(6);
//                    AddressIfDifferent = rset.getString(7);
//                    PrimaryDOB = rset.getString(8);
//                    PrimarySSN = rset.getString(9);
//                    PatientRelationtoPrimary = rset.getString(10);
//                    PrimaryOccupation = rset.getString(11);
//                    PrimaryEmployer = rset.getString(12);
//                    EmployerAddress = rset.getString(13);
//                    EmployerPhone = rset.getString(14);
//                    SecondryInsurance = rset.getString(15);
//                    SubscriberName = rset.getString(16);
//                    SubscriberDOB = rset.getString(17);
//                    PatientRelationshiptoSecondry = rset.getString(18);
//                    MemberID_2 = rset.getString(19);
//                    GroupNumber_2 = rset.getString(20);
//                    PriInsurerName = rset.getString(21);
//                }
//                rset.close();
//                stmt.close();
//            }
//            if (!PriInsuranceName.equals("")) {
//                Query = "Select IFNULL(PayerName,'') from " + Database + ".ProfessionalPayers where id = " + PriInsuranceName;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PriInsuranceName = rset.getString(1);
//                }
//                rset.close();
//                stmt.close();
//            }
//            Query = "Select IFNULL(NextofKinName,'-'), IFNULL(RelationToPatient,'-'), IFNULL(PhoneNumber,'-'), CASE WHEN LeaveMessage = 1 THEN 'YES' WHEN LeaveMessage = 0 THEN 'NO' ELSE ' YES / NO'END,  IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-') from " + Database + ".EmergencyInfo where PatientRegId = " + ID;
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            while (rset.next()) {
//                NextofKinName = rset.getString(1);
//                RelationToPatientER = rset.getString(2);
//                PhoneNumberER = rset.getString(3);
//                LeaveMessageERString = rset.getString(4);
//                AddressER = rset.getString(5);
//                CityStateZipER = rset.getString(6);
//            }
//            rset.close();
//            stmt.close();
//            Query = "Select MAX(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + ID;
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                VisitNumber = rset.getString(1).trim();
//            }
//            rset.close();
//            stmt.close();
//            VisitNumber = "VN-" + MRN + "-" + VisitNumber;
//            Query = " Select ReturnPatient, Google, MapSearch, Billboard, OnlineReview, TV, Website, BuildingSignDriveBy, Facebook, School, IFNULL(School_text ,'-'), Twitter, Magazine, IFNULL(Magazine_text,'-'), Newspaper, IFNULL(Newspaper_text,'-'), FamilyFriend, IFNULL(FamilyFriend_text,'-'), UrgentCare, IFNULL(UrgentCare_text,'-'), CommunityEvent, IFNULL(CommunityEvent_text,'-'),  IFNULL(Work_text,'-'), IFNULL(Physician_text, '-'), IFNULL(Other_text,'-') from " + Database + ".RandomCheckInfo where PatientRegId = " + ID;
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            while (rset.next()) {
//                if (rset.getInt(1) == 0) {
//                    ReturnPatient = "";
//                } else {
//                    ReturnPatient = "YES";
//                }
//                if (rset.getInt(2) == 0) {
//                    Google = "";
//                } else {
//                    Google = "YES";
//                }
//                if (rset.getInt(3) == 0) {
//                    MapSearch = "";
//                } else {
//                    MapSearch = "YES";
//                }
//                if (rset.getInt(4) == 0) {
//                    Billboard = "";
//                } else {
//                    Billboard = "YES";
//                }
//                if (rset.getInt(5) == 0) {
//                    OnlineReview = "";
//                } else {
//                    OnlineReview = "YES";
//                }
//                if (rset.getInt(6) == 0) {
//                    TV = "";
//                } else {
//                    TV = "YES";
//                }
//                if (rset.getInt(7) == 0) {
//                    Website = "";
//                } else {
//                    Website = "YES";
//                }
//                if (rset.getInt(8) == 0) {
//                    BuildingSignDriveBy = "";
//                } else {
//                    BuildingSignDriveBy = "YES";
//                }
//                if (rset.getInt(9) == 0) {
//                    Facebook = "";
//                } else {
//                    Facebook = "YES";
//                }
//                if (rset.getInt(10) == 0) {
//                    School = "";
//                    School_text = "";
//                } else {
//                    School = "YES";
//                    School_text = rset.getString(11);
//                }
//                if (rset.getInt(12) == 0) {
//                    Twitter = "";
//                } else {
//                    Twitter = "YES";
//                }
//                if (rset.getInt(13) == 0) {
//                    Magazine = "";
//                    Magazine_text = "";
//                } else {
//                    Magazine = "YES";
//                    Magazine_text = rset.getString(14);
//                }
//                if (rset.getInt(15) == 0) {
//                    Newspaper = "";
//                    Newspaper_text = "";
//                } else {
//                    Newspaper = "YES";
//                    Newspaper_text = rset.getString(16);
//                }
//                if (rset.getInt(17) == 0) {
//                    FamilyFriend = "";
//                    FamilyFriend_text = "";
//                } else {
//                    FamilyFriend = "YES";
//                    FamilyFriend_text = rset.getString(18);
//                }
//                if (rset.getInt(19) == 0) {
//                    UrgentCare = "";
//                    UrgentCare_text = "";
//                } else {
//                    UrgentCare = "YES";
//                    UrgentCare_text = rset.getString(20);
//                }
//                if (rset.getInt(21) == 0) {
//                    CommunityEvent = "";
//                    CommunityEvent_text = "";
//                } else {
//                    CommunityEvent = "YES";
//                    CommunityEvent_text = rset.getString(22);
//                }
//                if (rset.getString(23) == "" || rset.getString(23) == null) {
//                    Work_text = "";
//                } else {
//                    Work_text = rset.getString(23);
//                }
//                if (rset.getString(24) == "" || rset.getString(24) == null) {
//                    Physician_text = "";
//                } else {
//                    Physician_text = rset.getString(24);
//                }
//                if (rset.getString(25) == "" || rset.getString(25) == null) {
//                    Other_text = "";
//                } else {
//                    Other_text = rset.getString(25);
//                }
//            }
//            rset.close();
//            stmt.close();
//            String HearAboutUsString = "";
//            if (ReturnPatient.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Return Patient, ";
//            }
//            if (Google.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Google, ";
//            }
//            if (MapSearch.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Map Search, ";
//            }
//            if (OnlineReview.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Online Review, ";
//            }
//            if (TV.toUpperCase().equals("YES")) {
//                HearAboutUsString += "TV, ";
//            }
//            if (Website.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Website, ";
//            }
//            if (BuildingSignDriveBy.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Building Sign, ";
//            }
//            if (Facebook.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Facebook, ";
//            }
//            if (School.toUpperCase().equals("YES")) {
//                HearAboutUsString += "School, ";
//            }
//            if (Twitter.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Twitter, ";
//            }
//            if (Magazine.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Magazine, ";
//            }
//            if (Newspaper.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Newspaper, ";
//            }
//            if (FamilyFriend.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Friend / Family, ";
//            }
//            if (UrgentCare.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Urgent Care, ";
//            }
//            if (CommunityEvent.toUpperCase().equals("YES")) {
//                HearAboutUsString += "Comminuty Event, ";
//            }
//            if ("".toUpperCase().equals("YES")) {
//                HearAboutUsString += "Work, ";
//            }
//            if ("".toUpperCase().equals("YES")) {
//                HearAboutUsString += "Physician, ";
//            }
//            if ("".toUpperCase().equals("YES")) {
//                HearAboutUsString += "Others ";
//            }
//            String SymptomsString = "";
//            if (SympFever.toUpperCase().equals("YES")) {
//                SymptomsString += "Fever, ";
//            }
//            if (SympBodyAches.toUpperCase().equals("YES")) {
//                SymptomsString += "Body Aches, ";
//            }
//            if (SympSoreThroat.toUpperCase().equals("YES")) {
//                SymptomsString += "Sore Throat, ";
//            }
//            if (SympFatigue.toUpperCase().equals("YES")) {
//                SymptomsString += "Fatigue, ";
//            }
//            if (SympRash.toUpperCase().equals("YES")) {
//                SymptomsString += "Rash, ";
//            }
//            if (SympVomiting.toUpperCase().equals("YES")) {
//                SymptomsString += "Vomitting, ";
//            }
//            if (SympDiarrhea.toUpperCase().equals("YES")) {
//                SymptomsString += "Diarrhea, ";
//            }
//            if (SympCough.toUpperCase().equals("YES")) {
//                SymptomsString += "Cough, ";
//            }
//            if (SympRunnyNose.toUpperCase().equals("YES")) {
//                SymptomsString += "Runny Nose, ";
//            }
//            if (SympNausea.toUpperCase().equals("YES")) {
//                SymptomsString += "Nausea, ";
//            }
//            if (SympFluSymptoms.toUpperCase().equals("YES")) {
//                SymptomsString += "Flu-like Symptoms, ";
//            }
//            if (SympEyeConjunctivitis.toUpperCase().equals("YES")) {
//                SymptomsString += "Eye Conjunctivitis, ";
//            }
//            if (SympHeadache.toUpperCase().equals("YES")) {
//                SymptomsString += "Headache, ";
//            }
//            if (SympLossTaste.toUpperCase().equals("YES")) {
//                SymptomsString += "Loss of Taste/Smell, ";
//            }
//            if (SympShortBreath.toUpperCase().equals("YES")) {
//                SymptomsString += "Short of Breathness, ";
//            }
//            if (SympCongestion.toUpperCase().equals("YES")) {
//                SymptomsString += "Congestion ";
//            }
//
//            String inputFilePathTmp = "";
//            String outputFilePathTmp = "";
//            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/FinancialHardShip.pdf";
//            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf";
//            OutputStream fos1 = new FileOutputStream(new File(outputFilePathTmp));
//            PdfReader pdfReader1 = new PdfReader(inputFilePathTmp);
//            PdfStamper pdfStamper1 = new PdfStamper(pdfReader1, fos1);
//            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
//                if (i == 1) {
//                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(123.0f, 605.0f);
//                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(400.0f, 605.0f);
//                    pdfContentByte.showText(DOS);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(170.0f, 500.0f);
//                    pdfContentByte.showText("");
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(170.0f, 550.0f);
//                    pdfContentByte.showText(DOS);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(295.0f, 300.0f);
//                    pdfContentByte.showText(DOS);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(329.0f, 110.0f);
//                    pdfContentByte.showText(DOS);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte.endText();
//                }
//            }
//            pdfStamper1.close();
//            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/PromptPayAgreement.pdf";
//            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf";
//            fos1 = new FileOutputStream(new File(outputFilePathTmp));
//            pdfReader1 = new PdfReader(inputFilePathTmp);
//            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
//            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
//                if (i == 1) {
//                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
////                    if (ClientId == 27) {
////                        pdfContentByte.beginText();
////                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
////                        pdfContentByte.setColorFill(BaseColor.BLACK);
////                        pdfContentByte.setTextMatrix(175.0f, 645.0f);
////                        pdfContentByte.showText("Frontline ER White Rock.");
////                        pdfContentByte.endText();
////                    } else if (ClientId == 29) {
////                        pdfContentByte.beginText();
////                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
////                        pdfContentByte.setColorFill(BaseColor.BLACK);
////                        pdfContentByte.setTextMatrix(175.0f, 645.0f);
////                        pdfContentByte.showText("Frontline ER Richmond.");
////                        pdfContentByte.endText();
////                    }
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(315.0f, 300.0f);
//                    pdfContentByte.showText(DOS);
//                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte.endText();
//                }
//            }
//            pdfStamper1.close();
////            if (SelfPayChk == 1) {
////                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
////                if (ClientId == 27) {
////                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
////                } else {
////                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
////                }
////            } else {
////                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
////                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
////                if (ClientId == 27) {
////                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
////                } else {
////                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
////                }
////            }
//
//
//            String pattern = null;
//            Statement stmt1 = null;
//            ResultSet rset1 = null;
//            String Query1 = "";
//
//            Query1 = "SELECT Form_ids FROM " + Database + ".BundleForms where PatientRegId='" + ID + "'";
//            stmt1 = conn.createStatement();
//            rset1 = stmt1.executeQuery(Query1);
//            while (rset1.next()) {
//                pattern = rset1.getString(1);
//            }
//            rset1.close();
//            stmt1.close();
//
//            if (pattern != null) {
//                String[] pat = pattern.split("\\^");//pat[0] -> mva, pat[1] -> prompt pay, pat[2] -> Financial Harship
//                out.println(pat[0]);
//
//                if (pat[0].equals("1")) {//mva
//                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
//                }
//
//                if (pat[2].equals("1")) {// financial hardship
//                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
//                    if (ClientId == 27) {
//                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
//                    } else {
//                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
//                    }
//
//                    if (pat[1].equals("1")) {
//                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
//                        if (ClientId == 27) {
//                            ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
//                        } else {
//                            ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
//                        }
//                    }
//
//                }
//
//                if (pat[1].equals("1") && !(pat[2].equals("1"))) {//prompt pay
//                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
//                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
//                    if (ClientId == 27) {
//                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
//                    } else {
//                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
//                    }
//                }
//            } else {
//                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
//            }
//
//
//            if (PriInsuranceName.toUpperCase().contains("UNITED HEALTHCARE")) {
////                System.out.println("PriInsuranceName -> "+PriInsuranceName);
//
//                String inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/Commercial-Courtesy-Review-Auth-Form.pdf";
//                String outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/Commercial-Courtesy-Review-Auth-Form_" + ClientId + "_" + MRN + ".pdf";
//
//                ResultPdf = AttachUHC_Form(MemId, PrimaryDOB, PriInsurerName, DOS, PatientRelationtoPrimary, Date, outputFilePathTmp2, inputFilePathTmp2,
//                        request, response, out, conn, Database, ResultPdf, DirectoryName, ClientId, MRN, mergePdf);
//            }
//
//            String inputFilePath = "";
//            String outputFilePath = "";
//            String UID = "";
//            inputFilePath = ResultPdf;
//            outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_.pdf";
//            final OutputStream fos2 = new FileOutputStream(new File(outputFilePath));
//            final PdfReader pdfReader2 = new PdfReader(inputFilePath);
//            final int pageCount = pdfReader2.getNumberOfPages();
//            final PdfStamper pdfStamper2 = new PdfStamper(pdfReader2, fos2);
//            Image SignImages = null;
//            final File tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_0_" + ID + ".png");
//            final boolean exists = tmpDir.exists();
//            if (exists) {
//                Query = "Select UID from " + Database + ".SignRequest where PatientRegId = " + ID;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    UID = rset.getString(1);
//                }
//                rset.close();
//                stmt.close();
//
//                SignImages = Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_0_" + ID + ".png");
//                SignImages.scaleAbsolute(80.0f, 30.0f);
//                //outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + UID + "_.pdf";
//                outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_.pdf";
//            } else {
//                SignImages = null;
//            }
//
//            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
//                if (j == 1) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(40.0f, 770.0f);
//                    pdfContentByte2.showText(UID);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(130.0f, 685.0f);
//                    pdfContentByte2.showText(ReasonVisit);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(392.0f, 685.0f);
//                    pdfContentByte2.showText(MotorVehAccidentString);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(456.0f, 685.0f);
//                    pdfContentByte2.showText(WorkersCompPolicyString);
//                    pdfContentByte2.endText();
//                    if (SelfPayChk == 1) {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("(Y)");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("(N)");
//                        pdfContentByte2.endText();
//                    } else if (SelfPayChk == 0) {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("(N)");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("(Y)");
//                        pdfContentByte2.endText();
//                    } else {
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(486.0f, 685.0f);
//                        pdfContentByte2.showText("");
//                        pdfContentByte2.endText();
//                        pdfContentByte2.beginText();
//                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                        pdfContentByte2.setColorFill(BaseColor.BLACK);
//                        pdfContentByte2.setTextMatrix(560.0f, 685.0f);
//                        pdfContentByte2.showText("");
//                        pdfContentByte2.endText();
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 660.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 660.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(520.0f, 660.0f);
//                    pdfContentByte2.showText(Age);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 620.0f);
//                    pdfContentByte2.showText(Address);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(300.0f, 620.0f);
//                    pdfContentByte2.showText(StreetAddress2);//ApptNo
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(460.0f, 620.0f);
//                    pdfContentByte2.showText(SSN);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 595.0f);
//                    pdfContentByte2.showText(City);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(260.0f, 595.0f);
//                    pdfContentByte2.showText(State);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 595.0f);
//                    pdfContentByte2.showText(ZipCode);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(110.0f, 575.0f);
//                    pdfContentByte2.showText(PhNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(250.0f, 575.0f);
//                    pdfContentByte2.showText(PhNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 575.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(100.0f, 550.0f);
//                    pdfContentByte2.showText(PrimaryEmployer);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(395.0f, 550.0f);
//                    pdfContentByte2.showText(PrimaryOccupation);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 527.0f);
//                    pdfContentByte2.showText(EmployerAddress);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(90.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(409.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(500.0f, 527.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(105.0f, 505.0f);
//                    pdfContentByte2.showText(MaritalStatus);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(250.0f, 505.0f);
//                    pdfContentByte2.showText(gender);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 505.0f);
//                    pdfContentByte2.showText(Email);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 485.0f);
//                    pdfContentByte2.showText(NextofKinName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(360.0f, 485.0f);
//                    pdfContentByte2.showText(PhoneNumberER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(520.0f, 485.0f);
//                    pdfContentByte2.showText(RelationToPatientER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(100.0f, 463.0f);
//                    pdfContentByte2.showText(AddressER);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(455.0f, 463.0f);
//                    pdfContentByte2.showText(" ,  , ");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(310.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorDOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(480.0f, 392.0f);
//                    pdfContentByte2.showText(GuarantorSSN);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 360.0f);
//                    pdfContentByte2.showText(PriInsurerName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(310.0f, 360);
//                    pdfContentByte2.showText(PrimaryDOB);//primary DOB
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(480.0f, 360);
//                    pdfContentByte2.showText(PrimarySSN);//Primary SSN
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 335.0f);
//                    pdfContentByte2.showText(PriInsuranceName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(445.0f, 335.0f);
//                    pdfContentByte2.showText(MemId);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(540.0f, 335.0f);
//                    pdfContentByte2.showText(GrpNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 223.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 195.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(200.0f, 195.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 192.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 134.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 123.0f);
//                    pdfContentByte2.showText(ClientName + "  Sex: " + gender);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 113.0f);
//                    pdfContentByte2.showText("DOB:" + DOB + "  Age:(" + Age + ")");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 103.0f);
//                    pdfContentByte2.showText("MRN:" + MRN + "  DOS:" + DOS + "");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 93.0f);
//                    pdfContentByte2.showText("ACT#: " + VisitNumber);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 83.0f);
//                    pdfContentByte2.showText("Dr. " + DoctorName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 2) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(190.0f, 142.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(425.0f, 100.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(170.0f, 90.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(425.0f, 83.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(430.0f, 63.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 3) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 613.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 583.0f);
//                    pdfContentByte2.showText(Address);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(140.0f, 550.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(370.0f, 550.0f);
//                    pdfContentByte2.showText(SSN);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(110.0f, 150.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(430.0f, 150.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 4) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(180.0f, 140.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 140.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 5) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(190.0f, 335.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(220.0f, 275.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 285.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 252.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 6) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(150.0f, 530.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 530.0f);
//                    pdfContentByte2.showText(DOB);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(200.0f, 490.0f);
//                    pdfContentByte2.showText(PriInsuranceName);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(170.0f, 446.0f);
//                    pdfContentByte2.showText(MemId);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(410.0f, 446.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(210.0f, 230.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 216.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//                if (j == 7) {
//                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 473.0f);
//                    pdfContentByte2.showText(CovidTest);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(450.0f, 473.0f);
//                    pdfContentByte2.showText(CovidTestDate);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(80.0f, 442.0f);
//                    pdfContentByte2.showText(COVIDExposedChk);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(525.0f, 442.0f);
//                    pdfContentByte2.showText(CovidExpWhen);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(325.0f, 415.0f);
//                    pdfContentByte2.showText(SymptomsString);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(400.0f, 390.0f);
//                    pdfContentByte2.showText("");
//                    pdfContentByte2.endText();
//                    if (SignImages != null) {
//                        SignImages.setAbsolutePosition(210.0f, 115.0f);
//                        pdfContentByte2.addImage(SignImages);
//                    }
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 115.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(110.0f, 65.0f);
//                    pdfContentByte2.showText(LastName + ", " + FirstName + " " + MiddleInitial);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.setTextMatrix(380.0f, 65.0f);
//                    pdfContentByte2.showText(DOS);
//                    pdfContentByte2.endText();
//                    pdfContentByte2.beginText();
//                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte2.setColorFill(BaseColor.BLACK);
//                    pdfContentByte2.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte2.endText();
//                }
//            }
//            pdfStamper2.close();
//
////            Parsehtm Parser = new Parsehtm(request);
////            Parser.SetField("outputFilePath", outputFilePath);
////            //Parser.SetField("imagelist", String.valueOf(imagelist));
////            Parser.SetField("pageCount", String.valueOf(pageCount));
////            Parser.SetField("PatientRegId", String.valueOf(ID));
////            Parser.SetField("FileName", FirstNameNoSpaces + LastName + ID + "_.pdf");
////            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/DownloadBundleHTML.html");
//
//            final File pdfFile = new File(outputFilePath);
//            response.setContentType("application/pdf");
//            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
//            response.setContentLength((int) pdfFile.length());
//            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
//            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
//            int bytes;
//            while ((bytes = fileInputStream.read()) != -1) {
//                responseOutputStream.write(bytes);
//            }
//            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf");
//            File.delete();
//
//
//
//
//        } catch (Exception e) {
//            out.println(e.getMessage());
//            String str = "";
//            for (int k = 0; k < e.getStackTrace().length; ++k) {
//                str = str + e.getStackTrace()[k] + "<br>";
//            }
//            out.println(str);
//        }
//    }

    void GETINPUTFrontLine_ForSpanish(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String lang = "";
        String filename = null;
        String outputFilePath = null;
        String pageCount = null;
        String SignedFrom = "REGISTRATION";
        String BundleName = "";
        int PatientRegId = Integer.parseInt(request.getParameter("ID"));
        int VisitId = Integer.parseInt(request.getParameter("VisitId"));

        try {

            try {
                Query = "Select BundleName from " + Database + ".BundleHistory where PatientRegId=" + PatientRegId + "  And VisitIndex =" + VisitId + " ORDER BY CreatedDate DESC LIMIT 1";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    BundleName = rset.getString(1);
                }
                stmt.close();
                rset.close();

                if (BundleName.contains("EDIT") || BundleName.contains("VISIT")) {
                    GETINPUTFrontLine_InsideForSpanish(request, out, conn, servletContext, response, UserId, Database, ClientId, DirectoryName, PatientRegId, SignedFrom, VisitId, helper);
                    return;
                }

                Query = "Select BundleName,PgCount from " + Database + ".BundleHistory where PatientRegId=" + PatientRegId + " And VisitIndex =" + VisitId + " AND BundleName Like '%SPANISH%' ORDER BY CreatedDate DESC LIMIT 1";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);

                if (rset.next()) {
                    filename = rset.getString(1);
                    pageCount = rset.getString(2);

                    if (filename.contains("REGISTRATIONSIGNED") || filename.contains("REGISTRATION")) {
                        outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/REGISTRATION/" + filename;

                    } else if (filename.contains("VISITSIGNED") || filename.contains("VISIT")) {
                        outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/VISIT/" + filename;
                    } else if (filename.contains("EDITSIGNED") || filename.contains("EDIT")) {
                        outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/EDIT/" + filename;
                    }


                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("outputFilePath", outputFilePath);
                    Parser.SetField("pageCount", String.valueOf(pageCount));
                    Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
                    Parser.SetField("VisitId", String.valueOf(VisitId));
                    Parser.SetField("FileName", filename);

                    Parser.SetField("lang", "2");
                    Parser.SetField("ClientID", String.valueOf(ClientId));
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/DownloadBundleHTML.html");
                } else {
                    GETINPUTFrontLine_InsideForSpanish(request, out, conn, servletContext, response, UserId, Database, ClientId, DirectoryName, PatientRegId, SignedFrom, VisitId, helper);
                }
                stmt.close();
                rset.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("error ->>" + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

    private String AttachUHC_Form(String MemID, String DOB, String Name, String DOS, String RelationtoPatient, String Date, String outputFilePath, String inputFile, HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String Database, String ResultPdf, String DirectoryName, int ClientId, String MRN, MergePdf mergePdf) throws IOException {
        try {
            FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
            PdfReader pdfReader = new PdfReader(inputFile);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, (OutputStream) fos);
            for (int j = 1; j <= pdfReader.getNumberOfPages(); ++j) {

                if (j == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(j);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(450, 678); // set x and y co-ordinates
                    pdfContentByte.showText(MemID);//"Member ID Number "); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(365, 678); // set x and y co-ordinates
                    pdfContentByte.showText(DOB);//"DOB"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90, 678); // set x and y co-ordinates
                    pdfContentByte.showText(Name);//"Member Name "); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90, 573); // set x and y co-ordinates
                    pdfContentByte.showText(DOS);//"Date Of Service"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110, 535); // set x and y co-ordinates
                    pdfContentByte.showText(Name);//"Member Name"); //  PATIENT BEFORE add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460, 140); // set x and y co-ordinates
                    pdfContentByte.showText(Date);//"Date"); // Other (Please Specify)   add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90, 98); // set x and y co-ordinates
                    pdfContentByte.showText(RelationtoPatient);//"Relation to Subscriber"); // Other (Please Specify)   add the text
                    pdfContentByte.endText();

                }
            }
            pdfStamper.close();

            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, outputFilePath/*"/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/Commercial-Courtesy-Review-Auth-Form_" + ClientId + "_" + MRN + ".pdf"*/, ClientId, MRN);
            ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";

            return ResultPdf;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }


//
//    private String AttachAdditional_Forms_Frontline(String MemID, String DOB, String Name, String DOS, String RelationtoPatient, String Date, String outputFilePath, String inputFile, HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String Database, String ResultPdf, String DirectoryName, int ClientId, String MRN, MergePdf mergePdf) throws IOException {
//        try {
//            String inputFilePathTmp = "";
//            String outputFilePathTmp = "";
//            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/FinancialHardShip.pdf";
//            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf";
//            OutputStream fos1 = new FileOutputStream(new File(outputFilePathTmp));
//            PdfReader pdfReader1 = new PdfReader(inputFilePathTmp);
//            PdfStamper pdfStamper1 = new PdfStamper(pdfReader1, fos1);
//            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
//                if (i == 1) {
//                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(130.0f, 565.0f);
//                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);//patname
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(360.0f, 565.0f);
//                    pdfContentByte.showText(DOS);//date req
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(160.0f, 540.0f);
//                    pdfContentByte.showText("");//realtion
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(400.0f, 540.0f);
//                    pdfContentByte.showText("");//guardian name
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(150.0f, 512.0f);
//                    pdfContentByte.showText(DOS);//DOS
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(75.0f, 462.0f);
//                    pdfContentByte.showText("");//Y
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(75.0f, 434.0f);
//                    pdfContentByte.showText("");//Y
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(75.0f, 408.0f);
//                    pdfContentByte.showText("");//Y
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(75.0f, 380.0f);
//                    pdfContentByte.showText("");//Y
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(245.0f, 462.0f);
//                    pdfContentByte.showText("");//Y
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(245.0f, 434.0f);
//                    pdfContentByte.showText("");//Y
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(245.0f, 408.0f);
//                    pdfContentByte.showText("");//Y
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(245.0f, 380.0f);
//                    pdfContentByte.showText("");//Y
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(200.0f, 308.0f);
//                    pdfContentByte.showText("");//Other
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(100.0f, 255.0f);
//                    pdfContentByte.showText("");//recibido
//                    pdfContentByte.endText();
//
////                    if (SignImages != null) {
////                        SignImages.setAbsolutePosition(100.0f, 200.0f);
////                        pdfContentByte2.addImage(SignImages);
////                    }
//
////                pdfContentByte2.beginText();
////                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
////                pdfContentByte2.setColorFill(BaseColor.BLACK);
////                pdfContentByte2.setTextMatrix(100.0f, 205.0f);
////                pdfContentByte2.showText("sign");
////                pdfContentByte2.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(380.0f, 205.0f);
//                    pdfContentByte.showText(DOS);
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(420.0f, 143.0f);
//                    pdfContentByte.showText("");//Dailylog
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(530.0f, 143.0f);
//                    pdfContentByte.showText("");//By
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte.endText();
//                }
//            }
//            pdfStamper1.close();
//            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/PromptPayAgreement.pdf";
//            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf";
//            fos1 = new FileOutputStream(new File(outputFilePathTmp));
//            pdfReader1 = new PdfReader(inputFilePathTmp);
//            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
//            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
//                if (i == 1) {
//                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(430.0f, 425.0f);
//                    pdfContentByte.showText("");//CovidExpWhen
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(30.0f, 258.0f);
//                    pdfContentByte.showText("");//DOS//recibido
//                    pdfContentByte.endText();
//
////                    if (SignImages != null) {
////                        SignImages.setAbsolutePosition(50.0f, 210.0f);
////                        pdfContentByte2.addImage(SignImages);
////                    }
//
////                pdfContentByte2.beginText();
////                pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
////                pdfContentByte2.setColorFill(BaseColor.BLACK);
////                pdfContentByte2.setTextMatrix(100.0f, 215.0f);
////                pdfContentByte2.showText("Sign");//DOS
////                pdfContentByte2.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(275.0f, 215.0f);
//                    pdfContentByte.showText(DOS);//DOS
//                    pdfContentByte.endText();
//
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.showTextAligned(0, LastName + " , " + FirstName + "    DOB: " + DOB + "    Sex: " + gender + "      DOS: " + DOS + "     MRN: " + MRN + "      ACCT#: " + VisitNumber, 600.0f, 150.0f, 90.0f);
//                    pdfContentByte.endText();
//                }
//            }
//            pdfStamper1.close();
//
//            String pattern = null;
//            Query = "SELECT Form_ids FROM " + Database + ".BundleForms where PatientRegId='" + ID + "'";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                pattern = rset.getString(1);
//            }
//            rset.close();
//            stmt.close();
//
//            if (pattern != null) {
//                String[] pat = pattern.split("\\^");//pat[0] -> mva, pat[1] -> prompt pay, pat[2] -> Financial Harship
////                out.println(pat[0]);
//                System.out.println("here are the paterns ");
//                System.out.println("pat0 ="+pat[0]);
//                System.out.println("pat1= "+pat[1]);
//                System.out.println("pat 2 = "+pat[2]);
//                if (pat[0].equals("1")) {//mva
//                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
//                }
//
//                if (pat[2].equals("1")) {// financial hardship
//                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/FinancialHardShip_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
//                    if (ClientId == 27) {
//                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
//                    } else {
//                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
//                    }
//
//                    if (pat[1].equals("1")) { //prompt pay
//                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
//                        if (ClientId == 27) {
//                            ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
//                        } else {
//                            ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
//                        }
//                    }
//
//                }
//
//                if (pat[1].equals("1") && !(pat[2].equals("1"))) {//prompt pay
//                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
//                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/TempDir/PromptPayAgreement_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
//                    if (ClientId == 27) {
//                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/frontline/Result_" + ClientId + "_" + MRN + ".pdf";
//                    } else {
//                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/richmond/Result_" + ClientId + "_" + MRN + ".pdf";
//                    }
//                }
//            } else {
//                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
//            }
//
//            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, outputFilePath/*"/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/Commercial-Courtesy-Review-Auth-Form_" + ClientId + "_" + MRN + ".pdf"*/, ClientId, MRN);
//            ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";
//
//            return ResultPdf;
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//        return null;
//
//    }
//
//


    private String AttachUHC_Form_ES(String MemID, String DOB, String Name, String DOS, String RelationtoPatient, String Date, String outputFilePath, String inputFile, HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String Database, String ResultPdf, String DirectoryName, int ClientId, String MRN, MergePdf mergePdf) throws IOException {
        try {
            FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
            PdfReader pdfReader = new PdfReader(inputFile);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, (OutputStream) fos);
            for (int j = 1; j <= pdfReader.getNumberOfPages(); ++j) {

                if (j == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(j);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(450, 682); // set x and y co-ordinates
                    pdfContentByte.showText(MemID);//"Member ID Number "); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(365, 682); // set x and y co-ordinates
                    pdfContentByte.showText(DOB);//"DOB"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90, 682); // set x and y co-ordinates
                    pdfContentByte.showText(Name);//"Member Name "); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90, 629); // set x and y co-ordinates
                    pdfContentByte.showText("ClientAddress");//"Client Address "); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(365, 629); // set x and y co-ordinates
                    pdfContentByte.showText("ClientCity");//"City"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(445, 629); // set x and y co-ordinates
                    pdfContentByte.showText("ClientState");//"State"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(485, 629); // set x and y co-ordinates
                    pdfContentByte.showText("ClientZipCode");//"Zip"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90, 604); // set x and y co-ordinates
                    pdfContentByte.showText("ClientName");//"Zip"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90, 575); // set x and y co-ordinates
                    pdfContentByte.showText(DOS);//"Date Of Service"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110, 535); // set x and y co-ordinates
                    pdfContentByte.showText(Name);//"Member Name"); //  PATIENT BEFORE add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460, 140); // set x and y co-ordinates
                    pdfContentByte.showText(Date);//"Date"); // Other (Please Specify)   add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90, 98); // set x and y co-ordinates
                    pdfContentByte.showText(RelationtoPatient);//"Relation to Subscriber"); // Other (Please Specify)   add the text
                    pdfContentByte.endText();

                }
            }
            pdfStamper.close();
            pdfReader.close();
            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, outputFilePath/*"/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/Commercial-Courtesy-Review-Auth-Form_" + ClientId + "_" + MRN + ".pdf"*/, ClientId, MRN);
            ResultPdf = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";

            return ResultPdf;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }


}
