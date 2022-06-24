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
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
public class FloresvilleBundle extends HttpServlet {
/*    ResultSet rset;
    String Query;
    Statement stmt;
    private Connection conn;

    public FloresvilleBundle() {
        this.conn = null;
        this.rset = null;
        this.Query = "";
        this.stmt = null;
    }*/

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
        String ActionID = "";
        String DirectoryName = "";
        Connection conn = null;
        ResultSet rset = null;
        String Query = null;
        Statement stmt = null;
        final ServletContext context = getServletContext();
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        response.setContentType("text/html");
        final UtilityHelper helper = new UtilityHelper();
        final Services supp = new Services();
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
            Query = "Select dbname, IFNULL(DirectoryName,'') from oe.clients where Id = " + FacilityIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DirectoryName = rset.getString(2);
            }
            rset.close();
            stmt.close();

            if (ActionID.equals("GETINPUTfloresville")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "San Marcos Admission Bundle", "Download or View Admission Bundle", FacilityIndex);
                GETINPUTfloresville(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName);
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

    void GETINPUTfloresville(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
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
        String ResultPdf = "";
        String PriInsurerName = "";
        String[] PriInsurer;
        MergePdf mergePdf = new MergePdf();
        int SelfPayChk = 0;
        final int VerifyChkBox = 0;
        final int ID = Integer.parseInt(request.getParameter("ID").trim());
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
//                out.println(Query);
            }
//            if (SelfPayChk == 1) {
            Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%m/%d/%Y'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,''), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'-'),  IFNULL(PatientRelationshiptoSecondry,''), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-'), IFNULL(PriInsurerName,null) from " + Database + ".InsuranceInfo  where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                WorkersCompPolicy = rset.getInt(1);
                MotorVehAccident = rset.getInt(2);
                if (WorkersCompPolicy == 0) {
                    WorkersCompPolicyString = "NO";
                } else {
                    WorkersCompPolicyString = "YES";
                }
                if (MotorVehAccident == 0) {
                    MotorVehAccidentString = "NO";
                } else {
                    MotorVehAccidentString = "YES";
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
//            }
            try {
                if (SelfPayChk != 0 && !PriInsuranceName.equals("-") || !PriInsuranceName.equals("")) {
//                    out.println("Inside PriInsuranceName");
                    Query = "Select PayerName from oe_2.ProfessionalPayers where Id = " + PriInsuranceName;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PriInsuranceName = rset.getString(1);
                    }
                    rset.close();
                    stmt.close();

                    if (SelfPayChk != 0 && !SecondryInsurance.equals("-") || !SecondryInsurance.equals("")) {
//
                        Query = "Select PayerName from " + Database + ".ProfessionalPayers where Id = " + SecondryInsurance;
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next()) {
                            SecondryInsurance = rset.getString(1);
                        }
                        rset.close();
                        stmt.close();
                    }
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
            String HearAboutUsString = "";
            String HearAboutUsString2 = "";
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
                HearAboutUsString2 += "School, ";
            }
            if (Twitter.toUpperCase().equals("YES")) {
                HearAboutUsString2 += "Twitter, ";
            }
            if (Magazine.toUpperCase().equals("YES")) {
                HearAboutUsString2 += "Magazine, ";
            }
            if (Newspaper.toUpperCase().equals("YES")) {
                HearAboutUsString2 += "Newspaper, ";
            }
            if (FamilyFriend.toUpperCase().equals("YES")) {
                HearAboutUsString2 += "Friend / Family, ";
            }
            if (UrgentCare.toUpperCase().equals("YES")) {
                HearAboutUsString2 += "Urgent Care, ";
            }
            if (CommunityEvent.toUpperCase().equals("YES")) {
                HearAboutUsString2 += "Comminuty Event, ";
            }
            if ("".toUpperCase().equals("YES")) {
                HearAboutUsString2 += "Work, ";
            }
            if ("".toUpperCase().equals("YES")) {
                HearAboutUsString2 += "Physician, ";
            }
            if ("".toUpperCase().equals("YES")) {
                HearAboutUsString2 += "Others ";
            }
            rset.close();
            stmt.close();
            String inputFilePath = "";
            final InetAddress ip = InetAddress.getLocalHost();
            final String hostname = ip.getHostName();
            if (hostname.trim().equals("rover-01")) {
                inputFilePath = "";
            } else {
                inputFilePath = "/sftpdrive";
            }

            inputFilePath += "/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/PATIENTREGISTRATIONFORMFLORESVILLE.pdf";

//        if (MotorVehAccident == 1) {
//            mergePdf.GETINPUT(request, response, out, conn, Database, inputFilePath, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/MVAForm.pdf", ClientId, MRN);
//            inputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf";
//        }

            if (PriInsuranceName.toUpperCase().contains("UNITED HEALTHCARE")) {
//                System.out.println("PriInsuranceName -> "+PriInsuranceName);

                String inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/Commercial-Courtesy-Review-Auth-Form.pdf";
                String outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/TempDir/Commercial-Courtesy-Review-Auth-Form_" + ClientId + "_" + MRN + ".pdf";

                ResultPdf = AttachUHC_Form(MemId, PrimaryDOB, PriInsurerName, DOS, PatientRelationtoPrimary, Date, outputFilePathTmp2, inputFilePathTmp2,
                        request, response, out, conn, Database, inputFilePath, DirectoryName, ClientId, MRN, mergePdf);
                inputFilePath = ResultPdf;
            }

            if (SecondryInsurance.toUpperCase().contains("UNITED HEALTHCARE")) {
//                System.out.println("PriInsuranceName -> "+PriInsuranceName);

                String inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/Commercial-Courtesy-Review-Auth-Form.pdf";
                String outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/TempDir/Commercial-Courtesy-Review-Auth-Form_" + ClientId + "_" + MRN + ".pdf";

                ResultPdf = AttachUHC_Form(MemberID_2, SubscriberDOB, SubscriberName, DOS, PatientRelationshiptoSecondry, Date, outputFilePathTmp2, inputFilePathTmp2,
                        request, response, out, conn, Database, inputFilePath, DirectoryName, ClientId, MRN, mergePdf);

                inputFilePath = ResultPdf;
            }


            String UID = "";
            Image SignImages = null;
            final File tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_0_" + ID + ".png");
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

                SignImages = Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_0_" + ID + ".png");
                SignImages.scaleAbsolute(80.0f, 30.0f);
                //outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + UID + "_.pdf";
            } else {
                SignImages = null;
            }

            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf";
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
//            final GenerateBarCode barCode = new GenerateBarCode();
//            final String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
//            final Image image = Image.getInstance(BarCodeFilePath);
//            image.scaleAbsolute(150.0f, 30.0f);
            // loop on all the PDF pages
            // i is the pdfPageNumber
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);


                    /////////////////////////////////////Patient Information//////////////////////////////////////////////////
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80, 700); // set x and y co-ordinates
                    pdfContentByte.showText(Date);//"Date "); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 655); // set x and y co-ordinates
                    pdfContentByte.showText(LastName);//"Last Name"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(185, 655); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName);//"First Name"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(310, 655); // set x and y co-ordinates
                    pdfContentByte.showText(MiddleInitial);//"Middle"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(355, 668); // set x and y co-ordinates
                    pdfContentByte.showText("Title: ");//"Title"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(365, 655); // set x and y co-ordinates
                    pdfContentByte.showText(Title);//"Title"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(450, 655); // set x and y co-ordinates
                    pdfContentByte.showText(MaritalStatus);//"Marital Status"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 620); // set x and y co-ordinates
                    pdfContentByte.showText(SSN);//"SSN"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(240, 620); // set x and y co-ordinates
                    pdfContentByte.showText(PhNumber);//"PhNumber"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(365, 620); // set x and y co-ordinates
                    pdfContentByte.showText(DOB);//"BirthDate"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(480, 620); // set x and y co-ordinates
                    pdfContentByte.showText(gender);//"Sex"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 590); // set x and y co-ordinates
                    pdfContentByte.showText(Address);//"Address"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(240, 590); // set x and y co-ordinates
                    pdfContentByte.showText(CityStateZip);//"City/State/Zip"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420, 590); // set x and y co-ordinates
                    pdfContentByte.showText(Email);//"Email"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 555); // set x and y co-ordinates
                    pdfContentByte.showText(Employer);//"Employer"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(240, 555); // set x and y co-ordinates
                    pdfContentByte.showText(EmployerPhone);//"Employer Ph"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(375, 555); // set x and y co-ordinates
                    pdfContentByte.showText(EmployerAddress);//"Employer Address"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 525); // set x and y co-ordinates
                    pdfContentByte.showText(Physician);//"PCP"); // add the text
                    pdfContentByte.endText();
//                pdfContentByte.beginText();
//                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                pdfContentByte.setColorFill(BaseColor.BLACK);
//                pdfContentByte.setTextMatrix(240, 525); // set x and y co-ordinates
//                pdfContentByte.showText("PhNumber"); // add the text
//                pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 495); // set x and y co-ordinates
                    pdfContentByte.showText(HearAboutUsString);//"Heard about us1"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 485); // set x and y co-ordinates
                    pdfContentByte.showText(HearAboutUsString2);//"Heard about us2"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 425); // set x and y co-ordinates
                    pdfContentByte.showText(PriInsuranceName);//"Primary Insurance"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 400); // set x and y co-ordinates
                    pdfContentByte.showText(PriInsurerName);//"Subscriber’s Last Name"); // add the text
                    pdfContentByte.endText();
//                pdfContentByte.beginText();
//                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                pdfContentByte.setColorFill(BaseColor.BLACK);
//                pdfContentByte.setTextMatrix(200, 400); // set x and y co-ordinates
//                pdfContentByte.showText("First"); // add the text
//                pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(325, 400); // set x and y co-ordinates
                    pdfContentByte.showText(PrimarySSN);//"Subscriber’s Social Security"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460, 400); // set x and y co-ordinates
                    pdfContentByte.showText(PrimaryDOB);//"Subscriber’s Birth Date:"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 372); // set x and y co-ordinates
                    pdfContentByte.showText(MemId);//"Member Id Number"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(240, 372); // set x and y co-ordinates
                    pdfContentByte.showText(GrpNumber);//"Group Number"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380, 372); // set x and y co-ordinates
                    pdfContentByte.showText(PatientRelationtoPrimary);//"Patient’s relationship"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 350); // set x and y co-ordinates
                    pdfContentByte.showText(SecondryInsurance);//"Secondary Insurance"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 320); // set x and y co-ordinates
                    pdfContentByte.showText(SubscriberName);//"Subscriber’s Last Name"); // add the text
                    pdfContentByte.endText();

//                pdfContentByte.beginText();
//                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                pdfContentByte.setColorFill(BaseColor.BLACK);
//                pdfContentByte.setTextMatrix(200, 320); // set x and y co-ordinates
//                pdfContentByte.showText("First"); // add the text
//                pdfContentByte.endText();
//                pdfContentByte.beginText();
//                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                pdfContentByte.setColorFill(BaseColor.BLACK);
//                pdfContentByte.setTextMatrix(325, 320); // set x and y co-ordinates
//                pdfContentByte.showText("Subscriber’s Social Security"); // add the text
//                pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460, 320); // set x and y co-ordinates
                    pdfContentByte.showText(SubscriberDOB);//"Subscriber’s Birth Date:"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 292); // set x and y co-ordinates
                    pdfContentByte.showText(MemberID_2);//"Member Id Number"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(240, 292); // set x and y co-ordinates
                    pdfContentByte.showText(GroupNumber_2);//"Group Number"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380, 292); // set x and y co-ordinates
                    pdfContentByte.showText(PatientRelationshiptoSecondry);//"Patient’s relationship"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 105); // set x and y co-ordinates
                    pdfContentByte.showText(NextofKinName);//"Name of Relative"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(290, 105); // set x and y co-ordinates
                    pdfContentByte.showText(RelationToPatientER);//"Relationship"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410, 105); // set x and y co-ordinates
                    pdfContentByte.showText(PhoneNumberER);//"Home Phone"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(520, 105); // set x and y co-ordinates
                    pdfContentByte.showText(LeaveMessageERString);//"Leave Msg"); // add the text
                    pdfContentByte.endText();
                }

                if (i == 2) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(510, 115); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName);//"Patient’s Initial"); // add the text
                    pdfContentByte.endText();
                }

                if (i == 3) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(70, 170);
                        pdfContentByte.addImage(SignImages);
                    }
//                pdfContentByte.beginText();
//                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                pdfContentByte.setColorFill(BaseColor.BLACK);
//                pdfContentByte.setTextMatrix(70, 170); // set x and y co-ordinates
//                pdfContentByte.showText("Signature"); // add the text
//                pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80, 120); // set x and y co-ordinates
                    pdfContentByte.showText(Date);//"Date"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390, 120); // set x and y co-ordinates
                    pdfContentByte.showText(Date);//"Date"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(440, 90); // set x and y co-ordinates
                    pdfContentByte.showText(PatientRelationtoPrimary);//"Relationship"); // add the text
                    pdfContentByte.endText();

                }

                if (i == 4) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(155, 540); // set x and y co-ordinates
                    pdfContentByte.showText(SecondryInsurance);//"Secondary health insurance"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(145, 505); // set x and y co-ordinates
                    pdfContentByte.showText(SubscriberName);//"Subscriber Name"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460, 505); // set x and y co-ordinates
                    pdfContentByte.showText(SubscriberDOB);//"DOB"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(145, 480); // set x and y co-ordinates
                    pdfContentByte.showText(MemberID_2);//"Member ID"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(145, 450); // set x and y co-ordinates
                    pdfContentByte.showText(GroupNumber_2);//"Group No"); // add the text
                    pdfContentByte.endText();


                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(180, 225);
                        pdfContentByte.addImage(SignImages);
                    }
//                pdfContentByte.beginText();
//                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                pdfContentByte.setColorFill(BaseColor.BLACK);
//                pdfContentByte.setTextMatrix(180, 225); // set x and y co-ordinates
//                pdfContentByte.showText("Signature"); // add the text
//                pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(435, 225); // set x and y co-ordinates
                    pdfContentByte.showText(Date);//"Date"); // add the text
                    pdfContentByte.endText();


                }

                if (i == 5) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80, 370); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName);//"Patient Name"); // add the text
                    pdfContentByte.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(120, 300);
                        pdfContentByte.addImage(SignImages);
                    }
                }

                if (i == 6) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(150, 140);
                        pdfContentByte.addImage(SignImages);
                    }
                }

                if (i == 7) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(150, 185);
                        pdfContentByte.addImage(SignImages);
                    }
                }

            }
            pdfStamper.close();
            pdfReader.close();
//        final File pdfFile = new File(outputFilePath);
//        response.setContentType("application/pdf");
//        response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
//        response.setContentLength((int) pdfFile.length());
//        final FileInputStream fileInputStream = new FileInputStream(pdfFile);
//        final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
//        int bytes;
//        while ((bytes = fileInputStream.read()) != -1) {
//            responseOutputStream.write(bytes);
//        }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("outputFilePath", outputFilePath);
//            Parser.SetField("imagelist", String.valueOf(imagelist));
            Parser.SetField("pageCount", String.valueOf(pdfReader.getNumberOfPages()));
            Parser.SetField("PatientRegId", String.valueOf(ID));
            Parser.SetField("FileName", FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
            Parser.SetField("ClientID", String.valueOf(ClientId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/DownloadBundleHTML.html");

        } catch (Exception e) {
//            out.println(e.getMessage());
            System.out.println(e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            System.out.println(str);
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

}
