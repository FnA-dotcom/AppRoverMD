//
// Decompiled by Procyon v0.5.36
//

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Image;
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
import javax.servlet.http.*;
import java.io.*;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("Duplicates")
public class DownloadBundle13MARCH2021 extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private Connection conn = null;

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

    public void handleRequestold(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
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
        if (ActionID.equals("GETINPUT")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Orange Admission Bundle", "Download or View Admission Bundle", ClientId);
            this.GETINPUT(request, out, conn, context, response, UserId, Database, ClientId, DirectoryName);
        } else if (ActionID.equals("GETINPUTSAustin")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "South Austin Admission Bundle", "Download or View Admission Bundle", ClientId);
            this.GETINPUTSAustin(request, out, conn, context, response, UserId, Database, ClientId, DirectoryName);
        } else if (ActionID.equals("GETINPUTSublime")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Sublime Admission Bundle", "Download or View Admission Bundle", ClientId);
            this.GETINPUTSublime(request, out, conn, context, response, UserId, Database, ClientId, DirectoryName);
        } else if (ActionID.equals("GETINPUTVictoria")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Victoria Admission Bundle", "Download or View Admission Bundle", ClientId);
            this.GETINPUTVictoria(request, out, conn, context, response, UserId, Database, ClientId, DirectoryName);
        } else if (ActionID.equals("GETINPUTOddasa")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Odessa Admission Bundle", "Download or View Admission Bundle", ClientId);
            this.GETINPUTOddasa(request, out, conn, context, response, UserId, Database, ClientId, DirectoryName);
        } else if (ActionID.equals("GETINPUTConcho")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Concho Valley Admission Bundle", "Download or View Admission Bundle", ClientId);
            this.GETINPUTConcho(request, out, conn, context, response, UserId, Database, ClientId, DirectoryName);
        } else if (ActionID.equals("GETINPUTLongView")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Excel ER LongView Admission Bundle", "Download or View Admission Bundle", ClientId);
            this.GETINPUTLongView(request, out, conn, context, response, UserId, Database, ClientId, DirectoryName);
        } else if (ActionID.equals("GETINPUTNacogdoches")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Excel ER Nacogdoches Admission Bundle", "Download or View Admission Bundle", ClientId);
            this.GETINPUTNacogdoches(request, out, conn, context, response, UserId, Database, ClientId, DirectoryName);
        } else if (ActionID.equals("GETINPUTFrontLine")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Front Line ER Admission Bundle", "Download or View Admission Bundle", ClientId);
            this.GETINPUTFrontLine(request, out, conn, context, response, UserId, Database, ClientId, DirectoryName);
        } else if (ActionID.equals("GETINPUTERDallas")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Victoria Admission Bundle", "Download or View Admission Bundle", ClientId);
            this.GETINPUTERDallas(request, out, conn, context, response, UserId, Database, ClientId, DirectoryName);
        } else if (ActionID.equals("SignPdf")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Victoria Admission Bundle", "Download or View Admission Bundle", ClientId);
            this.SignPdf(request, out, conn, context, response, UserId, Database, ClientId, DirectoryName);
        } else if (ActionID.equals("download_direct")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "GetPDF in Iframe", "DownloadPDF in IFRAME", ClientId);
            this.download_direct(request, response, out, conn);
        }
        try {
            conn.close();
        } catch (Exception ex) {
        }
        out.flush();
        out.close();
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String DatabaseName = "";
        String DirectoryName = "";
        int FacilityIndex = 0;
        String UserId = "";
        String ActionID;

        response.setContentType("text/html");
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();
        ServletContext context = null;
        context = getServletContext();
        int ClientId = 0;

        try {
            HttpSession session = request.getSession(false);
            UtilityHelper helper = new UtilityHelper();

            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            DirectoryName = session.getAttribute("DirectoryName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());

            try {
/*
                boolean ValidSession = FacilityLogin.checkSession(out, request, context, response);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    return;
                }
*/
                if (UserId.equals("")) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            } catch (Exception var15) {
                this.conn = null;
                out.println("Exception excp conn: " + var15.getMessage());
            }
            ActionID = request.getParameter("ActionID").trim();
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.equals("GETINPUT")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Orange Admission Bundle", "Download or View Admission Bundle", ClientId);
                GETINPUT(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName);
            } else if (ActionID.equals("GETINPUTSAustin")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "South Austin Admission Bundle", "Download or View Admission Bundle", ClientId);
                GETINPUTSAustin(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName);
            } else if (ActionID.equals("GETINPUTSublime")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Sublime Admission Bundle", "Download or View Admission Bundle", ClientId);
                GETINPUTSublime(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName);
            } else if (ActionID.equals("GETINPUTVictoria")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Victoria Admission Bundle", "Download or View Admission Bundle", ClientId);
                GETINPUTVictoria(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName);
            } else if (ActionID.equals("GETINPUTOddasa")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Odessa Admission Bundle", "Download or View Admission Bundle", ClientId);
                GETINPUTOddasa(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName);
            } else if (ActionID.equals("GETINPUTConcho")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Concho Valley Admission Bundle", "Download or View Admission Bundle", ClientId);
                GETINPUTConcho(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName);
            } else if (ActionID.equals("GETINPUTLongView")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Excel ER LongView Admission Bundle", "Download or View Admission Bundle", ClientId);
                GETINPUTLongView(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName);
            } else if (ActionID.equals("GETINPUTNacogdoches")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Excel ER Nacogdoches Admission Bundle", "Download or View Admission Bundle", ClientId);
                GETINPUTNacogdoches(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName);
            } else if (ActionID.equals("GETINPUTFrontLine")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Front Line ER Admission Bundle", "Download or View Admission Bundle", ClientId);
                GETINPUTFrontLine(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName);
            } else if (ActionID.equals("GETINPUTERDallas")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Victoria Admission Bundle", "Download or View Admission Bundle", ClientId);
                GETINPUTERDallas(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName);
            } else if (ActionID.equals("download_direct")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "GetPDF in Iframe", "DownloadPDF in IFRAME", ClientId);
                download_direct(request, response, out, conn);
            } else {
                helper.deleteUserSession(request, conn, session.getId());
                //Invalidating Session.
                session.invalidate();
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
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

    void GETINPUT(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
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
                Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    DoctorName = rset.getString(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error In PateintReg:--" + e.getMessage());
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

                if (!PriInsuranceName.equals("-") && !PriInsuranceName.equals("") && !PriInsuranceName.equals("1")) {
                    Query = " Select PayerName from " + Database + ".ProfessionalPayers where Id =  " + PriInsuranceName;
                    System.out.println(Query);
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        PriInsuranceName = rset.getString(1);
                    }
                    rset.close();
                    stmt.close();
                }
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
            inputFilePath = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/Admin.pdf";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf";
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

/*            final GenerateBarCode barCode = new GenerateBarCode();
            final String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
            final Image image = Image.getInstance(BarCodeFilePath);
            image.scaleAbsolute(150.0f, 30.0f);*/

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
                    pdfContentByte.setTextMatrix(110.0f, 535.0f);
                    pdfContentByte.showText(ReturnPatient);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 520.0f);
                    pdfContentByte.showText(Google);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 500.0f);
                    pdfContentByte.showText(MapSearch);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 485.0f);
                    pdfContentByte.showText(Billboard);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 465.0f);
                    pdfContentByte.showText(OnlineReview);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 450.0f);
                    pdfContentByte.showText(TV);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 435.0f);
                    pdfContentByte.showText(Website);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 415.0f);
                    pdfContentByte.showText(BuildingSignDriveBy);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 398.0f);
                    pdfContentByte.showText(Facebook);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 380.0f);
                    pdfContentByte.showText(School);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190.0f, 380.0f);
                    pdfContentByte.showText(School_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 365.0f);
                    pdfContentByte.showText(Twitter);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 348.0f);
                    pdfContentByte.showText(Magazine);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 348.0f);
                    pdfContentByte.showText(Magazine_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 330.0f);
                    pdfContentByte.showText(Newspaper);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 330.0f);
                    pdfContentByte.showText(Newspaper_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 312.0f);
                    pdfContentByte.showText(FamilyFriend);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 312.0f);
                    pdfContentByte.showText(FamilyFriend_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 295.0f);
                    pdfContentByte.showText(UrgentCare);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 295.0f);
                    pdfContentByte.showText(UrgentCare_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 278.0f);
                    pdfContentByte.showText(CommunityEvent);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250.0f, 278.0f);
                    pdfContentByte.showText(CommunityEvent_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0f, 225.0f);
                    pdfContentByte.showText(Work_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(180.0f, 210.0f);
                    pdfContentByte.showText(Physician_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0f, 195.0f);
                    pdfContentByte.showText(Other_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 85.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(480.0f, 85.0f);
                    pdfContentByte.showText(" ");
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
                    if (PriInsuranceName.equals("1")) {
                        pdfContentByte.showText(PriInsurance);
                    } else {
                        pdfContentByte.showText(PriInsuranceName);
                    }
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
                    pdfContentByte.showText("");//PriInsuranceName
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
                    pdfContentByte.setTextMatrix(285.0f, 70.0f);
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
                    pdfContentByte.setTextMatrix(440.0f, 395.0f);
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
                    pdfContentByte.setTextMatrix(95.0f, 585.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(300.0f, 585.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(490.0f, 585.0f);
                    pdfContentByte.showText(PatientRelationtoPrimary);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(120.0f, 560.0f);
                    pdfContentByte.showText(PriInsuranceName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(180.0f, 535.0f);
                    pdfContentByte.showText(MemId);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 510.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 385.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(330.0f, 210.0f);
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
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 170.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 130.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
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
                    pdfContentByte.setTextMatrix(50.0f, 650.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 650.0f);
                    pdfContentByte.showText(PhNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50.0f, 610.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 610.0f);
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
                }
                if (i == 12) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(123.0f, 638.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(435.0f, 638.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0f, 589.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0f, 580.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(329.0f, 300.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
        } catch (Exception e) {
            out.println(e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
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
                    Services.DumException("DownloadBundle", "GetINput Victoria", request, e, this.getServletContext());
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
                    Services.DumException("DownloadBundle", "GetINput Victoria", request, e, this.getServletContext());
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
                    pdfContentByte.setTextMatrix(45.0f, 135.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 82.0f);
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
                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/Victoria/Result.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/Victoria/Result.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_QUESTIONNAIRE_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                if (WCPInjuryRelatedAutoMotorAccident.equals("1") || WCPInjuryOccurVehicle.equals("1")) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/Victoria/Result.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_assignmentofproceeds_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
                }
            } else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/GeneralForm_Victoria.pdf";
            }
            if (MotorVehicleAccidentChk == 1) {
                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/MVA_ASSIGNMENTOFPROCEEDS_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
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
            } else if (MotorVehicleAccidentChk == 0) {
                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/MVA_ASSIGNMENTOFPROCEEDS.pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
            } else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/GeneralForm_Victoria.pdf";
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
                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/MEDICAIDSELFPAYAGREEMENT_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
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
            } else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/GeneralForm_Victoria.pdf";
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

            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/Marketing_Slips.pdf", ClientId, MRN);
            ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";

            String DOSDate = "";
            String DOSTime = "";
            DOSDate = DOS.substring(0, 10);
            DOSTime = DOS.substring(11, 19);
            String inputFilePath = "";
            inputFilePath = ResultPdf;
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
                    pdfContentByte3.setTextMatrix(450.0f, 590.0f);
                    pdfContentByte3.showText("");
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
                    pdfContentByte3.showText(HISubscriberGroupNo);
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
                    pdfContentByte3.showText(HIPrimaryInsurance);
                    pdfContentByte3.endText();

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(155, 400);
                    pdfContentByte3.showText(HISubscriberFirstName + " " + HISubscriberLastName);
                    pdfContentByte3.endText();

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(410, 400);
                    pdfContentByte3.showText(HISubscriberDOB);
                    pdfContentByte3.endText();

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(155, 380);
                    pdfContentByte3.showText(HISubscriberPolicyNo);
                    pdfContentByte3.endText();

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(400, 380);
                    pdfContentByte3.showText(HISubscriberGroupNo);
                    pdfContentByte3.endText();

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(115, 158);
                    pdfContentByte3.showText(HISubscriberFirstName + " " + HISubscriberLastName);
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
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_QUESTIONNAIRE_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/VictoriaPdf/TempDir/WC_MVA_assignmentofproceeds_" + ClientId + "_" + MRN + ".pdf");
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void GETINPUTOddasa(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        MergePdf mergePdf = new MergePdf();
        final String Path1 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/";
        final String Path2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/";
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
        String DoctorId = "0";
        String DoctorName = "";
        int WorkersCompPolicy = 0;
        String WorkersCompPolicyString = "Is this a worker\u2019s comp policy: YES/NO";
        int MotorVehAccident = 0;
        String MotorVehAccidentString = "Is this a Motor Vehicle Accident : YES/NO";
        String PriInsurance = "";
        String MemId = "";
        String GrpNumber = "";
        String PriInsuranceName = "";
        String _PriInsuranceName = "";
        String AddressIfDifferent = "";
        String PrimaryDOB = "";
        String PrimarySSN = "";
        String PatientRelationtoPrimary = "";
        String PrimaryOccupation = "";
        String PrimaryEmployer = "";
        String EmployerAddress = "";
        String EmployerPhone = "";
        String SecondryInsurance = "";
        String _SecondryInsurance = "";
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
        final int ID = Integer.parseInt(request.getParameter("ID").trim());
        try {
            Query = "SELECT date_format(now(),'%Y%m%d%H%i%s'), DATE_FORMAT(now(), '%d-%m-%Y'), DATE_FORMAT(now(), '%T')";
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
                try {
                    Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%d-%m-%Y'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-'), IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, DATE_FORMAT(CreatedDate, '%d-%m-%Y'), IFNULL(DoctorsName,'-')  From " + Database + ".PatientReg Where ID = " + ID;
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
                        Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorId;
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        while (rset.next()) {
                            DoctorName = rset.getString(1);
                        }
                        rset.close();
                        stmt.close();
                    }
                } catch (Exception e) {
                    out.println("Error In Doctors Name:--" + e.getMessage());
                    out.println(Query);
                }
                if (SelfPayChk == 1) {
                    Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%d-%m-%Y'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%d-%m-%Y'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from " + Database + ".InsuranceInfo  where PatientRegId = " + ID;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        WorkersCompPolicy = rset.getInt(1);
                        MotorVehAccident = rset.getInt(2);
                        if (WorkersCompPolicy == 0) {
                            WorkersCompPolicyString = "Is this a workers comp policy: NO";
                        } else {
                            WorkersCompPolicyString = "Is this a workers comp policy: YES";
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
                if (SelfPayChk != 0) {
                    if (!PriInsuranceName.equals("-") && !PriInsuranceName.equals("")) {
                        Query = " Select PayerName from " + Database + ".ProfessionalPayers where Id =  " + PriInsuranceName;
                        System.out.println(Query);
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        while (rset.next()) {
                            _PriInsuranceName = rset.getString(1);
                        }
                        rset.close();
                        stmt.close();
                    }
                }
                if (!SecondryInsurance.equals("-") && !SecondryInsurance.equals("")) {
                    Query = "Select PayerName from " + Database + ".ProfessionalPayers where id = " + SecondryInsurance;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        _SecondryInsurance = rset.getString(1);
                    }
                    rset.close();
                    stmt.close();
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
            } catch (Exception e) {
                String str = "";
                for (int i = 0; i < e.getStackTrace().length; ++i) {
                    str = str + e.getStackTrace()[i] + "<br>";
                }
                out.println(str);
            }


            String inputFilePathTmp = "";
            String outputFilePathTmp = "";
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/Aetna.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/TempDir/Aetna_" + ClientId + "_" + MRN + ".pdf";
            OutputStream fos1 = new FileOutputStream(new File(outputFilePathTmp));
            PdfReader pdfReader1 = new PdfReader(inputFilePathTmp);
            PdfStamper pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); i++) {
                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100, 680); // set x and y co-ordinates
                    pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430, 680); // set x and y co-ordinates
                    pdfContentByte.showText(MemId); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(120, 655); // set x and y co-ordinates
                    pdfContentByte.showText(""); // add the textProvider of Service
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(120, 628); // set x and y co-ordinates
                    pdfContentByte.showText(DOS); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 605); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.WHITE);
                    pdfContentByte.setTextMatrix(120, 190); // set x and y co-ordinates
                    pdfContentByte.showText("SignHereAA"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(450, 190); // set x and y co-ordinates
                    pdfContentByte.showText(Date); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 160); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
                    pdfContentByte.showText("Excel-ER-Odessa   Sex: " + gender); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
                    pdfContentByte.showText("DOB: " + DOB + "    Age: (" + Age + ")"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
                    pdfContentByte.showText("MRN: " + MRN + "  DOS: " + DOS); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
                    pdfContentByte.showText("Dr." + DoctorName); // add the text
                    pdfContentByte.endText();

                }
            }
            pdfStamper1.close(); //close pdfStamper

            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/BCBS.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/TempDir/BCBS_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); i++) {
                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190, 545); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190, 532); // set x and y co-ordinates
                    pdfContentByte.showText(GrpNumber); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220, 518); // set x and y co-ordinates
                    pdfContentByte.showText(MemId); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200, 507); // set x and y co-ordinates
                    pdfContentByte.showText(""); // add the textProvoder Name
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(320, 495); // set x and y co-ordinates
                    pdfContentByte.showText(""); // add the textClaim No
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200, 483); // set x and y co-ordinates
                    pdfContentByte.showText(DOS); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.WHITE);
                    pdfContentByte.setTextMatrix(150, 283); // set x and y co-ordinates
                    pdfContentByte.showText("SignHereAA"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410, 283); // set x and y co-ordinates
                    pdfContentByte.showText(Date); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(130, 250); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 250); // set x and y co-ordinates
                    pdfContentByte.showText(""); // add the textRealtion to Patient
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
                    pdfContentByte.showText("Excel-ER-Odessa   Sex: " + gender); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
                    pdfContentByte.showText("DOB: " + DOB + "    Age: (" + Age + ")"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
                    pdfContentByte.showText("MRN: " + MRN + "  DOS: " + DOS); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 20); // set x and y co-ordinates
                    pdfContentByte.showText("Dr." + DoctorName); // add the text
                    pdfContentByte.endText();

                }
            }
            pdfStamper1.close(); //close pdfStamper


            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/UHC.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/TempDir/UHC_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); i++) {
                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150, 605); // set x and y co-ordinates
                    pdfContentByte.showText(Date); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190, 585); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170, 563); // set x and y co-ordinates
                    pdfContentByte.showText(MemId); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190, 540); // set x and y co-ordinates
                    pdfContentByte.showText(""); // add the textProvider Name
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150, 320); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
                    pdfContentByte.showText("Excel-ER-Odessa   Sex: " + gender); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
                    pdfContentByte.showText("DOB: " + DOB + "    Age: (" + Age + ")"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
                    pdfContentByte.showText("MRN: " + MRN + "  DOS: " + DOS); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 20); // set x and y co-ordinates
                    pdfContentByte.showText("Dr." + DoctorName); // add the text
                    pdfContentByte.endText();

                }
            }
            pdfStamper1.close(); //close pdfStamper


            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/other_insurance.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/TempDir/other_insurance_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); i++) {
                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(120, 665); // set x and y co-ordinates
                    pdfContentByte.showText(DOS); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190, 615); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150, 563); // set x and y co-ordinates
                    pdfContentByte.showText(PriInsuranceName); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150, 510); // set x and y co-ordinates
                    pdfContentByte.showText(MemId); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
                    pdfContentByte.showText("Excel ER-Odessa   Sex: " + gender); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
                    pdfContentByte.showText("DOB: " + DOB + "    Age: (" + Age + ")"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
                    pdfContentByte.showText("MRN: " + MRN + "  DOS: " + DOS); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 20); // set x and y co-ordinates
                    pdfContentByte.showText("Dr." + DoctorName); // add the text
                    pdfContentByte.endText();

                }
            }
            pdfStamper1.close(); //close pdfStamper

            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/Prompt_Pay_Agreement_Template.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/TempDir/Prompt_Pay_Agreement_Template_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); i++) {
                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(425, 175); // set x and y co-ordinates
                    pdfContentByte.showText(DOS); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100, 133); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380, 740); // set x and y co-ordinates
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380, 730); // set x and y co-ordinates
                    pdfContentByte.showText("Excel ER-Odessa   Sex: " + gender); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380, 720); // set x and y co-ordinates
                    pdfContentByte.showText("DOB: " + DOB + "    Age: (" + Age + ")"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380, 710); // set x and y co-ordinates
                    pdfContentByte.showText("MRN: " + MRN + "  DOS: " + DOS); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380, 700); // set x and y co-ordinates
                    pdfContentByte.showText("Dr." + DoctorName); // add the text
                    pdfContentByte.endText();

                }
            }
            pdfStamper1.close(); //close pdfStamper


            String ResultPdf = "";
            String inputFilePath = "";
            int AdmissionBundle = 0;
            if (SelfPayChk == 1) {
                try {
                    Query = "Select AdmissionBundle from " + Database + ".PatientAdmissionBundle where PatientRegId = " + ID;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        AdmissionBundle = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();
                } catch (Exception e2) {
                    out.println(e2.getMessage());
                }
                if (AdmissionBundle == 1) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/GeneralFormOdessa.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/TempDir/Aetna_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Odessa/Result_" + ClientId + "_" + MRN + ".pdf";
                } else if (AdmissionBundle == 2) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/GeneralFormOdessa.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/TempDir/BCBS_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Odessa/Result_" + ClientId + "_" + MRN + ".pdf";
                } else if (AdmissionBundle == 3) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/GeneralFormOdessa.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/TempDir/UHC_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Odessa/Result_" + ClientId + "_" + MRN + ".pdf";
                } else if (AdmissionBundle == 4) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/GeneralFormOdessa.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/TempDir/other_insurance_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Odessa/Result_" + ClientId + "_" + MRN + ".pdf";
                } else {
                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/GeneralFormOdessa.pdf";
                }

            } else if (SelfPayChk == 0) {
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/GeneralFormOdessa.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/TempDir/Prompt_Pay_Agreement_Template_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/Odessa/Result_" + ClientId + "_" + MRN + ".pdf";
            } else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/OdessaPdfs/GeneralFormOdessa.pdf";
            }
            //out.println(inputFilePath);
            inputFilePath = ResultPdf;
            String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + MRN + "_" + DateTime + ".pdf";
            OutputStream fos = new FileOutputStream(new File(outputFilePath));
            PdfReader pdfReader = new PdfReader(inputFilePath);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            for (int j = 1; j <= pdfReader.getNumberOfPages(); ++j) {
                if (j == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(j);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50.0f, 715.0f);
                    pdfContentByte.showText(LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 715.0f);
                    pdfContentByte.showText(FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(310.0f, 715.0f);
                    pdfContentByte.showText(MiddleInitial);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(360.0f, 715.0f);
                    pdfContentByte.showText(Title);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460.0f, 715.0f);
                    pdfContentByte.showText(MaritalStatus);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50.0f, 685.0f);
                    pdfContentByte.showText(SSN);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 685.0f);
                    pdfContentByte.showText(PhNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(360.0f, 685.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(480.0f, 685.0f);
                    pdfContentByte.showText(gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 9.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 652.0f);
                    pdfContentByte.showText(Address);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(240.0f, 652.0f);
                    pdfContentByte.showText(CityStateZip);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410.0f, 655.0f);
                    pdfContentByte.showText(Email);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 622.0f);
                    pdfContentByte.showText(Employer);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 622.0f);
                    pdfContentByte.showText(EmployerPhone);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(360.0f, 622.0f);
                    pdfContentByte.showText(Occupation);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 595.0f);
                    pdfContentByte.showText(PriCarePhy);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 595.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 452);
                    if (WorkersCompPolicy == 0 && MotorVehAccident == 0) {
                        pdfContentByte.showText("Is this due to MVA or Worker Comp : NO");
                    } else {
                        pdfContentByte.showText("Is this due to MVA or Worker Comp : NO");
                    }
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190.0f, 430);
                    pdfContentByte.showText(_PriInsuranceName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 400.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190.0f, 395);
                    pdfContentByte.showText(SubscriberName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(310.0f, 395);
                    pdfContentByte.showText(PrimarySSN);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460.0f, 395);
                    pdfContentByte.showText(SubscriberDOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 360);
                    pdfContentByte.showText(MemId);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 360);
                    pdfContentByte.showText(GrpNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 360);
                    pdfContentByte.showText(PatientRelationtoPrimary);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(480.0f, 360);
                    pdfContentByte.showText(PhNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 336);
                    pdfContentByte.showText(_SecondryInsurance);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 300);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190.0f, 300);
                    pdfContentByte.showText(SubscriberName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(310.0f, 300);
                    pdfContentByte.showText(PrimarySSN);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460.0f, 300);
                    pdfContentByte.showText(SubscriberDOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(35.0f, 270);
                    pdfContentByte.showText(MemberID_2);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 270);
                    pdfContentByte.showText(GroupNumber_2);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 270);
                    pdfContentByte.showText(PatientRelationshiptoSecondry);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 80);
                    pdfContentByte.showText(NextofKinName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(320.0f, 80);
                    pdfContentByte.showText(RelationToPatientER);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 80.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500.0f, 80);
                    pdfContentByte.showText(PhoneNumberER);
                    pdfContentByte.endText();
                }
                if (j == 2) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(j);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 66);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 56);
                    pdfContentByte.showText(ClientName + "   Sex: " + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 46);
                    pdfContentByte.showText("DOB: " + DOB + "    Age: (" + Age + ")");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 36);
                    pdfContentByte.showText("MRN: " + MRN + "    DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 26);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();
                }
                if (j == 3) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(j);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 66);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 56);
                    pdfContentByte.showText(ClientName + "   Sex: " + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 46);
                    pdfContentByte.showText("DOB: " + DOB + "    Age: (" + Age + ")");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 36);
                    pdfContentByte.showText("MRN: " + MRN + "    DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 26);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();
                }
                if (j == 4) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(j);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80, 160);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 160);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 66);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 56);
                    pdfContentByte.showText(ClientName + "   Sex: " + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 46);
                    pdfContentByte.showText("DOB: " + DOB + "    Age: (" + Age + ")");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 36);
                    pdfContentByte.showText("MRN: " + MRN + "    DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 26);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();
                }
                if (j == 5) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(j);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150, 535);
                    pdfContentByte.showText(_PriInsuranceName);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(120, 510);
                    pdfContentByte.showText("");//Subscriber Name
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 510);
                    pdfContentByte.showText(PrimaryDOB);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(120, 480);
                    pdfContentByte.showText(MemId);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 480);
                    pdfContentByte.showText(GrpNumber);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 265);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 210);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(310, 315);
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 66);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 56);
                    pdfContentByte.showText(ClientName + "   Sex: " + gender);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 46);
                    pdfContentByte.showText("DOB: " + DOB + "    Age: (" + Age + ")");
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 36);
                    pdfContentByte.showText("MRN: " + MRN + "    DOS: " + DOS);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 26);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();
                }

                if (j == 6) {
                    final PdfContentByte pdfContentByte = pdfStamper.getOverContent(j);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 455);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 375);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 66);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 56);
                    pdfContentByte.showText(ClientName + "   Sex: " + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 46);
                    pdfContentByte.showText("DOB: " + DOB + "    Age: (" + Age + ")");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 36);
                    pdfContentByte.showText("MRN: " + MRN + "    DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40.0f, 26);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();
                }
            }
            pdfStamper.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + MRN + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }

            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/TempDir/UHC_" + ClientId + "_" + MRN + ".pdf");
            File.delete();

            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/TempDir/other_insurance_" + ClientId + "_" + MRN + ".pdf");
            File.delete();

            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/TempDir/BCBS_" + ClientId + "_" + MRN + ".pdf");
            File.delete();

            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/TempDir/Aetna_" + ClientId + "_" + MRN + ".pdf");
            File.delete();

            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/TempDir/Prompt_Pay_Agreement_Template_" + ClientId + "_" + MRN + ".pdf");
            File.delete();

        } catch (Exception e) {
            out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    void GETINPUTSAustin(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
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
                    out.println("Inside Get Doc Name");
                    Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        DoctorName = rset.getString(1);
                    }
                    rset.close();
                    stmt.close();
                } else {
                    out.println("Inside Get Doc Name empty");
                    DoctorName = "";
                }
            } catch (Exception e) {
                out.println("Error In PateintReg:--" + e.getMessage());
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
                    out.println("Inside PriInsuranceName");
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
                out.println("Error is PriInsurance: " + e.getMessage());
                out.println(Query);
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
            if (hostname.trim().equals("romver-01")) {
                inputFilePath = "";
            } else {
                inputFilePath = "/sftpdrive";
            }
            System.out.println("Your current inputFilePath : " + inputFilePath);
            inputFilePath += "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/adminsaustin.pdf";
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf";
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            final GenerateBarCode barCode = new GenerateBarCode();
            //final String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
//            String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
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
                    pdfContentByte.setTextMatrix(110.0f, 535.0f);
                    pdfContentByte.showText(ReturnPatient);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 520.0f);
                    pdfContentByte.showText(Google);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 500.0f);
                    pdfContentByte.showText(MapSearch);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 485.0f);
                    pdfContentByte.showText(Billboard);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 465.0f);
                    pdfContentByte.showText(OnlineReview);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 450.0f);
                    pdfContentByte.showText(TV);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 435.0f);
                    pdfContentByte.showText(Website);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 415.0f);
                    pdfContentByte.showText(BuildingSignDriveBy);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 398.0f);
                    pdfContentByte.showText(Facebook);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 380.0f);
                    pdfContentByte.showText(School);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190.0f, 380.0f);
                    pdfContentByte.showText(School_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 365.0f);
                    pdfContentByte.showText(Twitter);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 348.0f);
                    pdfContentByte.showText(Magazine);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 348.0f);
                    pdfContentByte.showText(Magazine_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 330.0f);
                    pdfContentByte.showText(Newspaper);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 330.0f);
                    pdfContentByte.showText(Newspaper_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 312.0f);
                    pdfContentByte.showText(FamilyFriend);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 312.0f);
                    pdfContentByte.showText(FamilyFriend_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 295.0f);
                    pdfContentByte.showText(UrgentCare);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 295.0f);
                    pdfContentByte.showText(UrgentCare_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 278.0f);
                    pdfContentByte.showText(CommunityEvent);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250.0f, 278.0f);
                    pdfContentByte.showText(CommunityEvent_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0f, 225.0f);
                    pdfContentByte.showText(Work_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(180.0f, 210.0f);
                    pdfContentByte.showText(Physician_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0f, 195.0f);
                    pdfContentByte.showText(Other_text);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 85.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(480.0f, 85.0f);
                    pdfContentByte.showText(" ");
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
                    pdfContentByte.setTextMatrix(285.0f, 70.0f);
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
                    pdfContentByte.setTextMatrix(440.0f, 395.0f);
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
                    pdfContentByte.setTextMatrix(95.0f, 585.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(300.0f, 585.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(490.0f, 585.0f);
                    pdfContentByte.showText(PatientRelationtoPrimary);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(120.0f, 560.0f);
                    pdfContentByte.showText(PriInsuranceName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(180.0f, 535.0f);
                    pdfContentByte.showText(MemId);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 510.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 385.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(330.0f, 210.0f);
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
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 170.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Bold", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0f, 130.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
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
                    pdfContentByte.setTextMatrix(50.0f, 650.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 650.0f);
                    pdfContentByte.showText(PhNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50.0f, 610.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390.0f, 610.0f);
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
                }
            }
            pdfStamper.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    void GETINPUTSublime(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        final MergePdf mergePdf = new MergePdf();
        String ResultPdf = "";
        final String Path1 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/";
        final String Path2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/";
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
        String State = "";
        final String Country = "";
        String ZipCode = "";
        String SSN = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        String City = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        String MRN = "";
        int ClientIndex = 0;
        String ClientName = "";
        String DOS = "";
        String DoctorId = null;
        String DoctorName = null;
        String Ethnicity = "";
        String Race = "";
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
        String CovidExpWhen = "";
        String SpCarePhy = "";
        String SympEyeConjunctivitis = "";
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
        int AdmissionBundle = 0;
        String AdmissionBundleCoName = "";
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
                Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-'), IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')),  IFNULL(DoctorsName,''), IFNULL(City,''), IFNULL(State,''), IFNULL(ZipCode,''),  CASE WHEN Ethnicity = 1 THEN 'Hispanic or Latino' WHEN Ethnicity = 2 THEN ' Non Hispanic or Latino' WHEN Ethnicity = 3 THEN 'Others' ELSE 'Others' END  From " + Database + ".PatientReg Where ID = " + ID;
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
                    City = rset.getString(24);
                    State = rset.getString(25);
                    ZipCode = rset.getString(26);
                    Ethnicity = rset.getString(27);
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
                if (!DoctorId.equals("")) {
                    Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        DoctorName = rset.getString(1);
                    }
                    rset.close();
                    stmt.close();
                }
            } catch (Exception e) {
                out.println("Error In PateintReg:--" + e.getMessage());
                out.println(Query);
            }
            try {
                Query = "Select IFNULL(TravellingChk,0), IFNULL(DATE_FORMAT(TravelWhen,'%Y-%m-%d'),''), IFNULL(TravelWhere,''), IFNULL(TravelHowLong,''), IFNULL(COVIDExposedChk,0), IFNULL(SympFever,0), IFNULL(SympBodyAches,0), IFNULL(SympSoreThroat,0), IFNULL(SympFatigue,0), IFNULL(SympRash,0), IFNULL(SympVomiting,0), IFNULL(SympDiarrhea,0), IFNULL(SympCough,0), IFNULL(SympRunnyNose,0), IFNULL(SympNausea,0), IFNULL(SympFluSymptoms,0), IFNULL(SympEyeConjunctivitis,0), IFNULL(Race,''), IFNULL(DATE_FORMAT(CovidExpWhen,'%Y-%m-%d'),''), IFNULL(SpCarePhy,'')  from " + Database + ".PatientReg_Details where MRN = " + MRN;
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
                out.println("Error in getting PatientReg_Details Table : " + e.getMessage());
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
            if (ClientId == 15) {
                Query = " Select IFNULL(AdmissionBundle,0), CASE WHEN AdmissionBundle = 1 THEN 'Aetna Insurance' WHEN AdmissionBundle = 2 THEN 'Blue Cross Blue Shield'  WHEN AdmissionBundle = 3 THEN 'United Healthcare' WHEN AdmissionBundle = 4 THEN 'Other Insurance' ELSE 'Other Insurance' END  from " + Database + ".PatientAdmissionBundle where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    AdmissionBundle = rset.getInt(1);
                    AdmissionBundleCoName = rset.getString(2);
                }
                rset.close();
                stmt.close();
            }
            String inputFilePathTmp = "";
            String outputFilePathTmp = "";
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/Prompt_Pay_Agreement.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/Prompt_Pay_Agreement_" + ClientId + "_" + MRN + ".pdf";
            OutputStream fos1 = new FileOutputStream(new File(outputFilePathTmp));
            PdfReader pdfReader1 = new PdfReader(inputFilePathTmp);
            PdfStamper pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420.0f, 280.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(120.0f, 235.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + LastName);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/Workers_Comp_Patient_Form.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/Workers_Comp_Patient_Form_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0f, 660.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(370.0f, 660.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(480.0f, 660.0f);
                    pdfContentByte.showText(SSN);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0f, 635.0f);
                    pdfContentByte.showText(Address);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(330.0f, 635.0f);
                    pdfContentByte.showText(City);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420.0f, 635.0f);
                    pdfContentByte.showText(State);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500.0f, 635.0f);
                    pdfContentByte.showText(ZipCode);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0f, 585.0f);
                    pdfContentByte.showText(Employer);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0f, 560.0f);
                    pdfContentByte.showText(EmployerAddress);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0f, 535.0f);
                    pdfContentByte.showText(Employer);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(440.0f, 535.0f);
                    pdfContentByte.showText(EmployerPhone);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0f, 510.0f);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420.0f, 210.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/UNITED_HEALTHCARE_INSURANCE_FORM.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/UNITED_HEALTHCARE_INSURANCE_FORM_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(120.0f, 673.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(370.0f, 673.0f);
                    pdfContentByte.showText(PrimaryDOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(450.0f, 675.0f);
                    pdfContentByte.showText(MemId);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 648.0f);
                    pdfContentByte.showText(Address);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(370.0f, 650.0f);
                    pdfContentByte.showText(City);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(445.0f, 650.0f);
                    pdfContentByte.showText(State);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(480.0f, 650.0f);
                    pdfContentByte.showText(PhNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 550.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100.0f, 525.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100.0f, 498.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(450.0f, 200.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/Other_Appeal_Form.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/Other_Appeal_Form_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 655.0f);
                    pdfContentByte.showText(AdmissionBundleCoName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 542.0f);
                    pdfContentByte.showText(Title + " " + FirstName + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 520.0f);
                    pdfContentByte.showText(GrpNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 500.0f);
                    pdfContentByte.showText(SubscriberName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160.0f, 440.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380.0f, 220.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100.0f, 190.0f);
                    pdfContentByte.showText(" " + FirstName + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380.0f, 122.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100.0f, 90.0f);
                    pdfContentByte.showText(" " + FirstName + " " + LastName);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/BCBS_Appeal_Form.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/BCBS_Appeal_Form_" + ClientId + "_" + MRN + ".pdf";
            final FileOutputStream fos2 = new FileOutputStream(new File(outputFilePathTmp));
            final PdfReader pdfReader2 = new PdfReader(inputFilePathTmp);
            final PdfStamper pdfStamper2 = new PdfStamper(pdfReader2, (OutputStream) fos2);
            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(160.0f, 570.0f);
                    pdfContentByte2.showText(FirstName + " " + LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(160.0f, 550.0f);
                    pdfContentByte2.showText(GrpNumber);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(160.0f, 525.0f);
                    pdfContentByte2.showText(SubscriberName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(160.0f, 465.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(380.0f, 220.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 185.0f);
                    pdfContentByte2.showText(FirstName + " " + LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(380.0f, 112.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 75.0f);
                    pdfContentByte2.showText(FirstName + " " + LastName);
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();
            final String inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/Aetna_Appeal_Form.pdf";
            final String outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/Aetna_Appeal_Form_" + ClientId + "_" + MRN + ".pdf";
            final FileOutputStream fos3 = new FileOutputStream(new File(outputFilePathTmp2));
            final PdfReader pdfReader3 = new PdfReader("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/Aetna_Appeal_Form.pdf");
            final PdfStamper pdfStamper3 = new PdfStamper(pdfReader3, (OutputStream) fos3);
            for (int k = 1; k <= pdfReader3.getNumberOfPages(); ++k) {
                if (k == 1) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(140.0f, 645.0f);
                    pdfContentByte3.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(140.0f, 620.0f);
                    pdfContentByte3.showText("SUBLIME CARE EMERGENCY ROOM");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(140.0f, 585.0f);
                    pdfContentByte3.showText(DOS + "    ER SERVICES");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(120.0f, 565.0f);
                    pdfContentByte3.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(120.0f, 535.0f);
                    pdfContentByte3.showText("SUBLIME CARE EMERGENCY ROOM");
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(450.0f, 170.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(110.0f, 145.0f);
                    pdfContentByte3.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte3.endText();
                }
            }
            pdfStamper3.close();
            if (SelfPayChk == 0) {
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/GeneralForm_Sublime.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/Prompt_Pay_Agreement_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/Sublime/Result_" + ClientId + "_" + MRN + ".pdf";
            } else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/GeneralForm_Sublime.pdf";
                if (WorkersCompPolicy == 1) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/Workers_Comp_Patient_Form_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Sublime/Result_" + ClientId + "_" + MRN + ".pdf";
                } else {
                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/GeneralForm_Sublime.pdf";
                }
                if (AdmissionBundle == 1) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/Aetna_Appeal_Form_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Sublime/Result_" + ClientId + "_" + MRN + ".pdf";
                } else if (AdmissionBundle == 2) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/BCBS_Appeal_Form_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Sublime/Result_" + ClientId + "_" + MRN + ".pdf";
                } else if (AdmissionBundle == 3) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/UNITED_HEALTHCARE_INSURANCE_FORM_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Sublime/Result_" + ClientId + "_" + MRN + ".pdf";
                } else if (AdmissionBundle == 4) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/Other_Appeal_Form_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/Sublime/Result_" + ClientId + "_" + MRN + ".pdf";
                } else {
                    ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/GeneralForm_Sublime.pdf";
                }
            }
            String inputFilePath = "";
            inputFilePath = ResultPdf;
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + MRN + "_" + DateTime + ".pdf";
            final OutputStream fos4 = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader4 = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper4 = new PdfStamper(pdfReader4, fos4);
//            final GenerateBarCode barCode = new GenerateBarCode();
//            final String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
//            final Image image = Image.getInstance(BarCodeFilePath);
//            image.scaleAbsolute(150.0f, 30.0f);
            for (int l = 1; l <= pdfReader4.getNumberOfPages(); ++l) {
                if (l == 1) {
                    final PdfContentByte pdfContentByte4 = pdfStamper4.getOverContent(l);
//                    image.setAbsolutePosition(10.0f, 750.0f);
//                    pdfContentByte4.addImage(image);
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(110.0f, 710.0f);
                    pdfContentByte4.showText(LastName);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(340.0f, 710.0f);
                    pdfContentByte4.showText(FirstName);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(530.0f, 710.0f);
                    pdfContentByte4.showText(MiddleInitial);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(70.0f, 692.0f);
                    pdfContentByte4.showText(DOB);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(175.0f, 692.0f);
                    pdfContentByte4.showText(SSN);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(365.0f, 692.0f);
                    pdfContentByte4.showText("(" + gender + ")");
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(580.0f, 692.0f);
                    pdfContentByte4.showText("(" + MaritalStatus + ")");
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(160.0f, 665.0f);
                    pdfContentByte4.showText("(" + Race + ")");
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(365.0f, 655.0f);
                    pdfContentByte4.showText("(" + Ethnicity + ")");
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(85.0f, 642.0f);
                    pdfContentByte4.showText(Address);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(350.0f, 642.0f);
                    pdfContentByte4.showText(City);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(460.0f, 642.0f);
                    pdfContentByte4.showText(State);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(530.0f, 642.0f);
                    pdfContentByte4.showText(ZipCode);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(110.0f, 628.0f);
                    pdfContentByte4.showText(PhNumber);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(350.0f, 628.0f);
                    pdfContentByte4.showText(PhNumber);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(110.0f, 615.0f);
                    pdfContentByte4.showText(Employer);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(350.0f, 615.0f);
                    pdfContentByte4.showText(EmpContact);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(80.0f, 602.0f);
                    pdfContentByte4.showText(Email);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(350.0f, 602.0f);
                    pdfContentByte4.showText(PriCarePhy);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(405.0f, 232.0f);
                    pdfContentByte4.showText("---Patient Relationship (" + PatientRelationtoPrimary + ")");
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(110.0f, 222.0f);
                    pdfContentByte4.showText(LastName);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(320.0f, 222.0f);
                    pdfContentByte4.showText(FirstName);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(445.0f, 222.0f);
                    pdfContentByte4.showText(MiddleInitial);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(80.0f, 205.0f);
                    pdfContentByte4.showText(PrimaryDOB);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(190.0f, 205.0f);
                    pdfContentByte4.showText(PrimarySSN);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(380.0f, 205.0f);
                    pdfContentByte4.showText("(" + gender + ")");
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(85.0f, 190.0f);
                    pdfContentByte4.showText(AddressIfDifferent);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(300.0f, 190.0f);
                    pdfContentByte4.showText(City);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(405.0f, 190.0f);
                    pdfContentByte4.showText(State);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(445.0f, 190.0f);
                    pdfContentByte4.showText(ZipCode);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(100.0f, 175.0f);
                    pdfContentByte4.showText(PhNumber);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(345.0f, 175.0f);
                    pdfContentByte4.showText(PhNumber);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(100.0f, 160.0f);
                    pdfContentByte4.showText(PrimaryEmployer);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(345.0f, 160.0f);
                    pdfContentByte4.showText(EmployerPhone);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(440.0f, 70.0f);
                    pdfContentByte4.showText(Date);
                    pdfContentByte4.endText();
                }
                if (l == 2) {
                    String MM = "";
                    String DD = "";
                    String YYYY = "";
                    final String[] SplitDate = Date.split("\\/");
                    MM = SplitDate[0];
                    DD = SplitDate[1];
                    YYYY = SplitDate[2];
                    final PdfContentByte pdfContentByte5 = pdfStamper4.getOverContent(l);
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(105.0f, 640.0f);
                    pdfContentByte5.showText(MM);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(145.0f, 640.0f);
                    pdfContentByte5.showText(DD);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(175.0f, 640.0f);
                    pdfContentByte5.showText(YYYY);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(300.0f, 638.0f);
                    pdfContentByte5.showText("(" + gender + ")");
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(175.0f, 590.0f);
                    pdfContentByte5.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(105.0f, 572.0f);
                    pdfContentByte5.showText(MM);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(145.0f, 572.0f);
                    pdfContentByte5.showText(DD);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(175.0f, 572.0f);
                    pdfContentByte5.showText(YYYY);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(450.0f, 572.0f);
                    pdfContentByte5.showText(DOS);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(125.0f, 558.0f);
                    pdfContentByte5.showText(Address);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(410.0f, 558.0f);
                    pdfContentByte5.showText(PhNumber);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(150.0f, 542.0f);
                    pdfContentByte5.showText(CityStateZip);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(180.0f, 495.0f);
                    pdfContentByte5.showText(ReasonVisit);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(180.0f, 440.0f);
                    pdfContentByte5.showText(Email);
                    pdfContentByte5.endText();
                    if (TravellingChk.trim().toUpperCase().equals("YES")) {
                        pdfContentByte5.beginText();
                        pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte5.setColorFill(BaseColor.BLACK);
                        pdfContentByte5.setTextMatrix(455.0f, 388.0f);
                        pdfContentByte5.showText("YES");
                        pdfContentByte5.endText();
                    } else if (TravellingChk.trim().toUpperCase().equals("NO")) {
                        pdfContentByte5.beginText();
                        pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte5.setColorFill(BaseColor.BLACK);
                        pdfContentByte5.setTextMatrix(527.0f, 388.0f);
                        pdfContentByte5.showText("NO");
                        pdfContentByte5.endText();
                    } else {
                        pdfContentByte5.beginText();
                        pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte5.setColorFill(BaseColor.BLACK);
                        pdfContentByte5.setTextMatrix(455.0f, 388.0f);
                        pdfContentByte5.showText("");
                        pdfContentByte5.endText();
                        pdfContentByte5.beginText();
                        pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte5.setColorFill(BaseColor.BLACK);
                        pdfContentByte5.setTextMatrix(527.0f, 388.0f);
                        pdfContentByte5.showText("");
                        pdfContentByte5.endText();
                    }
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(160.0f, 373.0f);
                    pdfContentByte5.showText(TravelWhere);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(280.0f, 373.0f);
                    pdfContentByte5.showText(TravelWhen);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(390.0f, 373.0f);
                    pdfContentByte5.showText(TravelHowLong);
                    pdfContentByte5.endText();
                    if (COVIDExposedChk.trim().toUpperCase().equals("YES")) {
                        pdfContentByte5.beginText();
                        pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte5.setColorFill(BaseColor.BLACK);
                        pdfContentByte5.setTextMatrix(505.0f, 320.0f);
                        pdfContentByte5.showText("YES");
                        pdfContentByte5.endText();
                    } else if (COVIDExposedChk.trim().toUpperCase().equals("NO")) {
                        pdfContentByte5.beginText();
                        pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte5.setColorFill(BaseColor.BLACK);
                        pdfContentByte5.setTextMatrix(545.0f, 320.0f);
                        pdfContentByte5.showText("NO");
                        pdfContentByte5.endText();
                    } else {
                        pdfContentByte5.beginText();
                        pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte5.setColorFill(BaseColor.BLACK);
                        pdfContentByte5.setTextMatrix(505.0f, 320.0f);
                        pdfContentByte5.showText("");
                        pdfContentByte5.endText();
                        pdfContentByte5.beginText();
                        pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte5.setColorFill(BaseColor.BLACK);
                        pdfContentByte5.setTextMatrix(545.0f, 320.0f);
                        pdfContentByte5.showText("NO");
                        pdfContentByte5.endText();
                    }
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(85.0f, 220.0f);
                    pdfContentByte5.showText(SympFever);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(230.0f, 220.0f);
                    pdfContentByte5.showText(SympRash);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(335.0f, 220.0f);
                    pdfContentByte5.showText(SympCough);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(85.0f, 205.0f);
                    pdfContentByte5.showText(SympBodyAches);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(230.0f, 205.0f);
                    pdfContentByte5.showText(SympVomiting);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(335.0f, 205.0f);
                    pdfContentByte5.showText(SympRunnyNose);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(85.0f, 188.0f);
                    pdfContentByte5.showText(SympSoreThroat);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(230.0f, 188.0f);
                    pdfContentByte5.showText(SympDiarrhea);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(335.0f, 188.0f);
                    pdfContentByte5.showText(SympNausea);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(85.0f, 170.0f);
                    pdfContentByte5.showText(SympFluSymptoms);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(230.0f, 170.0f);
                    pdfContentByte5.showText(SympCough);
                    pdfContentByte5.endText();
                    pdfContentByte5.beginText();
                    pdfContentByte5.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte5.setColorFill(BaseColor.BLACK);
                    pdfContentByte5.setTextMatrix(335.0f, 170.0f);
                    pdfContentByte5.showText(SympEyeConjunctivitis);
                    pdfContentByte5.endText();
                }
                if (l == 3) {
                    final PdfContentByte pdfContentByte4 = pdfStamper4.getOverContent(l);
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(110.0f, 175.0f);
                    pdfContentByte4.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(420.0f, 110.0f);
                    pdfContentByte4.showText(Date);
                    pdfContentByte4.endText();
                }
                if (l == 4) {
                    final PdfContentByte pdfContentByte4 = pdfStamper4.getOverContent(l);
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(110.0f, 225.0f);
                    pdfContentByte4.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(450.0f, 175.0f);
                    pdfContentByte4.showText(Date);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(450.0f, 150.0f);
                    pdfContentByte4.showText(Date);
                    pdfContentByte4.endText();
                }
                if (l == 5) {
                    final PdfContentByte pdfContentByte4 = pdfStamper4.getOverContent(l);
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(110.0f, 225.0f);
                    pdfContentByte4.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(450.0f, 135.0f);
                    pdfContentByte4.showText(Date);
                    pdfContentByte4.endText();
                }
                if (l == 8) {
                    final PdfContentByte pdfContentByte4 = pdfStamper4.getOverContent(l);
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(385.0f, 50.0f);
                    pdfContentByte4.showText(Date);
                    pdfContentByte4.endText();
                }
                if (l == 9) {
                    final PdfContentByte pdfContentByte4 = pdfStamper4.getOverContent(l);
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(130.0f, 440.0f);
                    pdfContentByte4.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(440.0f, 395.0f);
                    pdfContentByte4.showText(Date);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(130.0f, 170.0f);
                    pdfContentByte4.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte4.endText();
                    pdfContentByte4.beginText();
                    pdfContentByte4.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte4.setColorFill(BaseColor.BLACK);
                    pdfContentByte4.setTextMatrix(440.0f, 130.0f);
                    pdfContentByte4.showText(Date);
                    pdfContentByte4.endText();
                }
            }
            pdfStamper4.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/Workers_Comp_Patient_Form_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/UNITED_HEALTHCARE_INSURANCE_FORM_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/Prompt_Pay_Agreement_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/Other_Appeal_Form_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/BCBS_Appeal_Form_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/SublimePdfs/TempDir/Aetna_Appeal_Form_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    void GETINPUTConcho(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        String ResultPdf = "";
        final MergePdf mergePdf = new MergePdf();
        final String Path1 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/";
        final String Path2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/";
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
        String Ethnicity = "";
        String Race = "";
        String TravellingChk = "";
        String TravelWhen = "";
        String TravelWhere = "";
        String TravelHowLong = "";
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
        String SympEyeConjunctivitis = "";
        int WorkersCompPolicy = 0;
        String WorkersCompPolicyString = "";
        int MotorVehAccident = 0;
        String MotorVehAccidentString = "";
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
                Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-'), IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), IFNULL(DoctorsName,'-'),  CASE WHEN Ethnicity = 1 THEN 'Hispanic or Latino' WHEN Ethnicity = 2 THEN ' Non Hispanic or Latino' WHEN Ethnicity = 3 THEN 'Others' ELSE 'Others' END  From " + Database + ".PatientReg Where ID = " + ID;
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
                    Ethnicity = rset.getString(24);
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
                Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    DoctorName = rset.getString(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error In PateintReg:--" + e.getMessage());
                out.println(Query);
            }
            try {
                Query = " Select CASE WHEN TravellingChk = 1 THEN 'YES' WHEN TravellingChk = 0 THEN 'NO' ELSE 'NO' END, IFNULL(DATE_FORMAT(TravelWhen,'%m/%d/%Y'),''),  IFNULL(TravelWhere,''), IFNULL(TravelHowLong,''), CASE WHEN COVIDExposedChk = 1 THEN 'YES' WHEN COVIDExposedChk = 0 THEN 'NO' ELSE 'NO' END,  CASE WHEN SympFever = 1 THEN 'YES' ELSE '' END, CASE WHEN SympBodyAches = 1 THEN 'YES' ELSE '' END, CASE WHEN SympSoreThroat = 1 THEN 'YES' ELSE '' END,  CASE WHEN SympFatigue = 1 THEN 'YES' ELSE '' END,CASE WHEN SympRash = 1 THEN 'YES' ELSE '' END, CASE WHEN SympVomiting = 1 THEN 'YES' ELSE '' END,  CASE WHEN SympDiarrhea = 1 THEN 'YES' ELSE '' END, CASE WHEN SympCough = 1 THEN 'YES' ELSE '' END, CASE WHEN SympRunnyNose = 1 THEN 'YES' ELSE '' END,  CASE WHEN SympNausea = 1 THEN 'YES' ELSE '' END, CASE WHEN SympFluSymptoms = 1 THEN 'YES' ELSE '' END, CASE WHEN SympEyeConjunctivitis = 1 THEN 'YES' ELSE '' END,  CASE WHEN Race = 1 THEN 'African American' WHEN Race = 2 THEN 'American Indian or Alska Native' WHEN Race = 3 THEN 'Asian' WHEN Race = 4 THEN 'Native Hawaiian or Other Pacific Islander'  WHEN Race = 5 THEN 'White' WHEN Race = 6 THEN 'Others' ELSE 'Others' END  from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
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
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error in getting PatientReg_Details Table : " + e.getMessage());
            }
            if (SelfPayChk == 1) {
                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%m/%d/%Y'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from " + Database + ".InsuranceInfo  where PatientRegId = " + ID;
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
            String inputFilePathTmp = "";
            String outputFilePathTmp = "";
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/Prompt_Pay_Agreement.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/TempDir/Prompt_Pay_Agreement_" + ClientId + "_" + MRN + ".pdf";
            OutputStream fos1 = new FileOutputStream(new File(outputFilePathTmp));
            PdfReader pdfReader1 = new PdfReader(inputFilePathTmp);
            PdfStamper pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420.0f, 245.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 185.0f);
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/Medicaid_ABN.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/TempDir/Medicaid_ABN_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(415.0f, 745.0f);
                    pdfContentByte.showText(LastName + " , " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(415.0f, 735.0f);
                    pdfContentByte.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(415.0f, 725.0f);
                    pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(415.0f, 715.0f);
                    pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(415.0f, 705.0f);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420.0f, 70.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 70.0f);
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/Medicare_ABN.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/TempDir/Medicare_ABN_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(415.0f, 745.0f);
                    pdfContentByte.showText(LastName + " , " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(415.0f, 735.0f);
                    pdfContentByte.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(415.0f, 725.0f);
                    pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(415.0f, 715.0f);
                    pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(415.0f, 705.0f);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420.0f, 70.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 70.0f);
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/Benefit_appeal.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/TempDir/Benefit_appeal_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0f, 535.0f);
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0f, 535.0f);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220.0f, 505.0f);
                    pdfContentByte.showText(PatientRelationtoPrimary);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(440.0f, 505.0f);
                    pdfContentByte.showText(PriInsuranceName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(240.0f, 475.0f);
                    pdfContentByte.showText(MemId);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0f, 445.0f);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0f, 375.0f);
                    pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420.0f, 140.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            if (SelfPayChk == 0) {
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/GeneralForm_Concho.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/TempDir/Prompt_Pay_Agreement_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/concho/Result_" + ClientId + "_" + MRN + ".pdf";
            } else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/GeneralForm_Concho.pdf";
                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/TempDir/Benefit_appeal_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/concho/Result_" + ClientId + "_" + MRN + ".pdf";
                if (PriInsurance.trim().toUpperCase().equals("MEDICAID")) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/TempDir/Medicaid_ABN_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/concho/Result_" + ClientId + "_" + MRN + ".pdf";
                } else if (PriInsurance.trim().toUpperCase().equals("MEDICARE")) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/TempDir/Medicare_ABN_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/concho/Result_" + ClientId + "_" + MRN + ".pdf";
                } else {
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/concho/Result_" + ClientId + "_" + MRN + ".pdf";
                }
            }
            String inputFilePath = "";
            inputFilePath = ResultPdf;
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf";
            final OutputStream fos2 = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader2 = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper2 = new PdfStamper(pdfReader2, fos2);
//            final GenerateBarCode barCode = new GenerateBarCode();
//            final String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
//            final Image image = Image.getInstance(BarCodeFilePath);
//            image.scaleAbsolute(150.0f, 30.0f);
            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(105.0f, 650.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(500.0f, 650.0f);
                    pdfContentByte2.showText(Time);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(105.0f, 605.0f);
                    pdfContentByte2.showText(LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(285.0f, 605.0f);
                    pdfContentByte2.showText(FirstName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(440.0f, 605.0f);
                    pdfContentByte2.showText(MiddleInitial);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(510.0f, 605.0f);
                    pdfContentByte2.showText("Title: " + Title);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(105.0f, 580.0f);
                    pdfContentByte2.showText(Ethnicity);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(300.0f, 580.0f);
                    pdfContentByte2.showText(Race);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(105.0f, 545.0f);
                    pdfContentByte2.showText(SSN);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(210.0f, 545.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(310.0f, 545.0f);
                    pdfContentByte2.showText(Age);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(365.0f, 545.0f);
                    pdfContentByte2.showText(gender);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(425.0f, 545.0f);
                    pdfContentByte2.showText(MaritalStatus);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(50.0f, 515.0f);
                    pdfContentByte2.showText(Address);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(310.0f, 515.0f);
                    pdfContentByte2.showText(CityStateZip);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 490.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(230.0f, 490.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(375.0f, 490.0f);
                    pdfContentByte2.showText(Email);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 460.0f);
                    pdfContentByte2.showText(Employer);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(230.0f, 460.0f);
                    pdfContentByte2.showText(Occupation);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 460.0f);
                    pdfContentByte2.showText(EmpContact);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(110.0f, 430.0f);
                    pdfContentByte2.showText(PriCarePhy);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(320.0f, 430.0f);
                    pdfContentByte2.showText(ReasonVisit);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(200.0f, 385.0f);
                    pdfContentByte2.showText(WorkersCompPolicyString);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(450.0f, 385.0f);
                    pdfContentByte2.showText(MotorVehAccidentString);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 360.0f);
                    pdfContentByte2.showText(PriInsurance);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(280.0f, 360.0f);
                    pdfContentByte2.showText(MemId);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(420.0f, 360.0f);
                    pdfContentByte2.showText(GrpNumber);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 335.0f);
                    pdfContentByte2.showText(PriInsuranceName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(220.0f, 335.0f);
                    pdfContentByte2.showText(AddressIfDifferent);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(420.0f, 335.0f);
                    pdfContentByte2.showText(CityStateZip);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 307.0f);
                    pdfContentByte2.showText(PrimaryDOB);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(160.0f, 307.0f);
                    pdfContentByte2.showText(PrimarySSN);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 307.0f);
                    pdfContentByte2.showText(PatientRelationtoPrimary);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(460.0f, 307.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 282.0f);
                    pdfContentByte2.showText(PrimaryOccupation);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(170.0f, 282.0f);
                    pdfContentByte2.showText(PrimaryEmployer);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(285.0f, 282.0f);
                    pdfContentByte2.showText(EmployerAddress);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(445.0f, 282.0f);
                    pdfContentByte2.showText(EmployerPhone);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 255.0f);
                    pdfContentByte2.showText(SecondryInsurance);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(260.0f, 255.0f);
                    pdfContentByte2.showText(SubscriberName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(445.0f, 255.0f);
                    pdfContentByte2.showText(SubscriberDOB);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 227.0f);
                    pdfContentByte2.showText(PatientRelationshiptoSecondry);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(260.0f, 227.0f);
                    pdfContentByte2.showText(MemberID_2);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(420.0f, 227.0f);
                    pdfContentByte2.showText(GroupNumber_2);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 185.0f);
                    pdfContentByte2.showText(NextofKinName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(225.0f, 185.0f);
                    pdfContentByte2.showText(RelationToPatientER);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(380.0f, 185.0f);
                    pdfContentByte2.showText(PhoneNumberER);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(450.0f, 105.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
                if (j == 2) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(100.0f, 675.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(425.0f, 675.0f);
                    pdfContentByte2.showText(gender);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(100.0f, 640.0f);
                    pdfContentByte2.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(425.0f, 640.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(450.0f, 605.0f);
                    pdfContentByte2.showText(SympFever);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(450.0f, 570.0f);
                    pdfContentByte2.showText(SympCough);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(450.0f, 535.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(450.0f, 500.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(450.0f, 465.0f);
                    pdfContentByte2.showText(TravellingChk);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(180.0f, 435.0f);
                    pdfContentByte2.showText(TravelWhere);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(450.0f, 310.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(450.0f, 265.0f);
                    pdfContentByte2.showText(COVIDExposedChk);
                    pdfContentByte2.endText();
                }
                if (j == 3) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(100.0f, 250.0f);
                    pdfContentByte2.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(425.0f, 210.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
                if (j == 4) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(325.0f, 145.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(340.0f, 100.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
                if (j == 5) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 170.0f);
                    pdfContentByte2.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(430.0f, 120.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(430.0f, 75.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
                if (j == 6) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(425.0f, 385.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
                if (j == 7) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 120.0f);
                    pdfContentByte2.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(450.0f, 80.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
                if (j == 11) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(450.0f, 280.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
                if (j == 12) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 605.0f);
                    pdfContentByte2.showText(Google);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(410.0f, 605.0f);
                    pdfContentByte2.showText(Website);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 592.0f);
                    pdfContentByte2.showText(TV);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 577.0f);
                    pdfContentByte2.showText(Facebook);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 548.0f);
                    pdfContentByte2.showText(Billboard);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 548.0f);
                    pdfContentByte2.showText(BuildingSignDriveBy);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(270.0f, 533.0f);
                    pdfContentByte2.showText(FamilyFriend);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 533.0f);
                    pdfContentByte2.showText(UrgentCare);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 519.0f);
                    pdfContentByte2.showText(Physician_text);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(240.0f, 490.0f);
                    pdfContentByte2.showText(Other_text);
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/TempDir/Prompt_Pay_Agreement_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/TempDir/Medicaid_ABN_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/TempDir/Medicare_ABN_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ConchoPdfs/TempDir/Benefit_appeal_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
        } catch (Exception e) {
            out.println(e.getMessage());
            String str = "";
            for (int k = 0; k < e.getStackTrace().length; ++k) {
                str = str + e.getStackTrace()[k] + "<br>";
            }
            out.println(str);
        }
    }

    void GETINPUTLongView(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        String ResultPdf = "";
        MergePdf mergePdf = new MergePdf();
        final String Path1 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/";
        final String Path2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/";
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
        String Ethnicity = "";
        String Race = "";
        String TravellingChk = "";
        String TravelWhen = "";
        String TravelWhere = "";
        String TravelHowLong = "";
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
        String SympEyeConjunctivitis = "";
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
                Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-'), IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), IFNULL(DoctorsName,'-'),  CASE WHEN Ethnicity = 1 THEN 'Hispanic or Latino' WHEN Ethnicity = 2 THEN ' Non Hispanic or Latino' WHEN Ethnicity = 3 THEN 'Others' ELSE 'Others' END  From " + Database + ".PatientReg Where ID = " + ID;
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
                    Ethnicity = rset.getString(24);
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
            try {
                Query = " Select CASE WHEN TravellingChk = 1 THEN 'YES' WHEN TravellingChk = 0 THEN 'NO' ELSE 'NO' END, IFNULL(DATE_FORMAT(TravelWhen,'%m/%d/%Y'),''),  IFNULL(TravelWhere,''), IFNULL(TravelHowLong,''), CASE WHEN COVIDExposedChk = 1 THEN 'YES' WHEN COVIDExposedChk = 0 THEN 'NO' ELSE 'NO' END,  CASE WHEN SympFever = 1 THEN 'YES' ELSE '' END, CASE WHEN SympBodyAches = 1 THEN 'YES' ELSE '' END, CASE WHEN SympSoreThroat = 1 THEN 'YES' ELSE '' END,  CASE WHEN SympFatigue = 1 THEN 'YES' ELSE '' END,CASE WHEN SympRash = 1 THEN 'YES' ELSE '' END, CASE WHEN SympVomiting = 1 THEN 'YES' ELSE '' END,  CASE WHEN SympDiarrhea = 1 THEN 'YES' ELSE '' END, CASE WHEN SympCough = 1 THEN 'YES' ELSE '' END, CASE WHEN SympRunnyNose = 1 THEN 'YES' ELSE '' END,  CASE WHEN SympNausea = 1 THEN 'YES' ELSE '' END, CASE WHEN SympFluSymptoms = 1 THEN 'YES' ELSE '' END, CASE WHEN SympEyeConjunctivitis = 1 THEN 'YES' ELSE '' END,  CASE WHEN Race = 1 THEN 'African American' WHEN Race = 2 THEN 'American Indian or Alska Native' WHEN Race = 3 THEN 'Asian' WHEN Race = 4 THEN 'Native Hawaiian or Other Pacific Islander'  WHEN Race = 5 THEN 'White' WHEN Race = 6 THEN 'Others' ELSE 'Others' END  from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
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
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error in getting PatientReg_Details Table : " + e.getMessage());
            }
            if (SelfPayChk == 1) {
                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%m/%d/%Y'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from " + Database + ".InsuranceInfo  where PatientRegId = " + ID;
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
                }
                rset.close();
                stmt.close();
            }
            if (!PriInsuranceName.equals("")) {
                Query = "Select IFNULL(PayerName,'') from " + Database + ".ProfessionalPayers where Id = " + PriInsuranceName;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PriInsuranceName = rset.getString(1);
                }
                rset.close();
                stmt.close();
                Query = "Select IFNULL(PayerName,'') from " + Database + ".ProfessionalPayers where Id = " + SecondryInsurance;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    SecondryInsurance = rset.getString(1);
                }
                rset.close();
                stmt.close();
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
            String inputFilePathTmp = "";
            String outputFilePathTmp = "";
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/FINANCIAL_HARDSHIP_RELIEF.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/TempDir/FINANCIAL_HARDSHIP_RELIEF_" + ClientId + "_" + MRN + ".pdf";
            OutputStream fos1 = new FileOutputStream(new File(outputFilePathTmp));
            PdfReader pdfReader1 = new PdfReader(inputFilePathTmp);
            PdfStamper pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setTextMatrix(370.0f, 330.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 270.0f);
                    pdfContentByte.showText(LastName + " , " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 260.0f);
                    pdfContentByte.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 250.0f);
                    pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 240.0f);
                    pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 230.0f);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/PT_FORM_HB2041_DISCLOSURE_ONLY_Excel_LONGVIEW.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/TempDir/PT_FORM_HB2041_DISCLOSURE_ONLY_Excel_LONGVIEW_" + ClientId + "_" + MRN + ".pdf";
            fos1 = new FileOutputStream(new File(outputFilePathTmp));
            pdfReader1 = new PdfReader(inputFilePathTmp);
            pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 230.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setTextMatrix(370.0f, 230.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            if (SelfPayChk == 0) {
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/GeneralForm_LongView.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/TempDir/FINANCIAL_HARDSHIP_RELIEF_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/exceler/Result_" + ClientId + "_" + MRN + ".pdf";
            } else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/GeneralForm_LongView.pdf";
                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/TempDir/PT_FORM_HB2041_DISCLOSURE_ONLY_Excel_LONGVIEW_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/exceler/Result_" + ClientId + "_" + MRN + ".pdf";
            }
            String inputFilePath = "";
            inputFilePath = ResultPdf;
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf";
            final OutputStream fos2 = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader2 = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper2 = new PdfStamper(pdfReader2, fos2);
//            final GenerateBarCode barCode = new GenerateBarCode();
//            final String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
//            final Image image = Image.getInstance(BarCodeFilePath);
//            image.scaleAbsolute(150.0f, 30.0f);
            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(80.0f, 705.0f);
                    pdfContentByte2.showText(LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(200.0f, 705.0f);
                    pdfContentByte2.showText(FirstName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(310.0f, 705.0f);
                    pdfContentByte2.showText(MiddleInitial);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(365.0f, 705.0f);
                    pdfContentByte2.showText("Title: " + Title);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(510.0f, 705.0f);
                    pdfContentByte2.showText(MaritalStatus);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 675.0f);
                    pdfContentByte2.showText(SSN);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(260.0f, 675.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(350.0f, 675.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(510.0f, 675.0f);
                    pdfContentByte2.showText(gender);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(40.0f, 640.0f);
                    pdfContentByte2.showText(Address);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(255.0f, 640.0f);
                    pdfContentByte2.showText(CityStateZip);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(410.0f, 645.0f);
                    pdfContentByte2.showText(Email);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 615.0f);
                    pdfContentByte2.showText(Employer);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(230.0f, 615.0f);
                    pdfContentByte2.showText(EmpContact);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 615.0f);
                    pdfContentByte2.showText(Occupation);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 585.0f);
                    pdfContentByte2.showText(PriCarePhy);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(230.0f, 585.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 495.0f);
                    pdfContentByte2.showText("Random CHecks COmplete String here");
                    pdfContentByte2.endText();
                    if (WorkersCompPolicy == 1 && MotorVehAccident == 0) {
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(440.0f, 452.0f);
                        pdfContentByte2.showText("YES (WCP) and NO (MVA)");
                        pdfContentByte2.endText();
                    } else if (WorkersCompPolicy == 0 && MotorVehAccident == 1) {
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(440.0f, 452.0f);
                        pdfContentByte2.showText("NO (WCP) and YES (MVA)");
                        pdfContentByte2.endText();
                    } else if (WorkersCompPolicy == 0 && MotorVehAccident == 0) {
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(440.0f, 452.0f);
                        pdfContentByte2.showText("NO (WCP) and NO (MVA)");
                        pdfContentByte2.endText();
                    } else if (WorkersCompPolicy == 1 && MotorVehAccident == 1) {
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(440.0f, 452.0f);
                        pdfContentByte2.showText("YES (WCP) and YES (MVA)");
                        pdfContentByte2.endText();
                    }
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(180.0f, 430.0f);
                    pdfContentByte2.showText(PriInsuranceName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 395.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(180.0f, 395.0f);
                    pdfContentByte2.showText(SubscriberName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(330.0f, 395.0f);
                    pdfContentByte2.showText(PrimarySSN);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(470.0f, 395.0f);
                    pdfContentByte2.showText(SubscriberDOB);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 360.0f);
                    pdfContentByte2.showText(MemId);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(240.0f, 360.0f);
                    pdfContentByte2.showText(GrpNumber);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(390.0f, 360.0f);
                    pdfContentByte2.showText(PatientRelationtoPrimary);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(480.0f, 360.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(315.0f, 330.0f);
                    pdfContentByte2.showText(SecondryInsurance);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(70.0f, 305.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(185.0f, 305.0f);
                    pdfContentByte2.showText(SubscriberName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(350.0f, 305.0f);
                    pdfContentByte2.showText(PrimarySSN);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(460.0f, 305.0f);
                    pdfContentByte2.showText(SubscriberDOB);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 270.0f);
                    pdfContentByte2.showText(MemberID_2);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(230.0f, 270.0f);
                    pdfContentByte2.showText(GroupNumber_2);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(360.0f, 270.0f);
                    pdfContentByte2.showText(PatientRelationshiptoSecondry);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 80.0f);
                    pdfContentByte2.showText(NextofKinName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(285.0f, 80.0f);
                    pdfContentByte2.showText(RelationToPatientER);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(395.0f, 80.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(500.0f, 80.0f);
                    pdfContentByte2.showText(PhoneNumberER);
                    pdfContentByte2.endText();
                }
                if (j == 2) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 80.0f);
                    pdfContentByte2.showText(LastName + " , " + FirstName + " " + MiddleInitial);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 70.0f);
                    pdfContentByte2.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 60.0f);
                    pdfContentByte2.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 50.0f);
                    pdfContentByte2.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 40.0f);
                    pdfContentByte2.showText("Dr. " + DoctorName);
                    pdfContentByte2.endText();
                }
                if (j == 3) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 80.0f);
                    pdfContentByte2.showText(LastName + " , " + FirstName + " " + MiddleInitial);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 70.0f);
                    pdfContentByte2.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 60.0f);
                    pdfContentByte2.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 50.0f);
                    pdfContentByte2.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 40.0f);
                    pdfContentByte2.showText("Dr. " + DoctorName);
                    pdfContentByte2.endText();
                }
                if (j == 4) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 160.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 160.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(440.0f, 120.0f);
                    pdfContentByte2.showText(RelationToPatientER);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 80.0f);
                    pdfContentByte2.showText(LastName + " , " + FirstName + " " + MiddleInitial);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 70.0f);
                    pdfContentByte2.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 60.0f);
                    pdfContentByte2.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 50.0f);
                    pdfContentByte2.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 40.0f);
                    pdfContentByte2.showText("Dr. " + DoctorName);
                    pdfContentByte2.endText();
                }
                if (j == 5) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(425.0f, 275.0f);
                    pdfContentByte2.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(440.0f, 240.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(440.0f, 200.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
                if (j == 6) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(135.0f, 730.0f);
                    pdfContentByte2.showText(LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(425.0f, 730.0f);
                    pdfContentByte2.showText(FirstName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(120.0f, 715.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(415.0f, 715.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(90.0f, 700.0f);
                    pdfContentByte2.showText(Address);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 700.0f);
                    pdfContentByte2.showText(CityStateZip);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 552.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(445.0f, 130.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(280.0f, 115.0f);
                    pdfContentByte2.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte2.endText();
                }
                if (j == 7) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    if (PatientRelationtoPrimary.equals("Self")) {
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(120.0f, 645.0f);
                        pdfContentByte2.showText(FirstName + " " + MiddleInitial + " " + LastName);
                        pdfContentByte2.endText();
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(360.0f, 645.0f);
                        pdfContentByte2.showText(PhNumber);
                        pdfContentByte2.endText();
                    } else {
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(120.0f, 645.0f);
                        pdfContentByte2.showText(NextofKinName);
                        pdfContentByte2.endText();
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(360.0f, 645.0f);
                        pdfContentByte2.showText(PhoneNumberER);
                        pdfContentByte2.endText();
                    }
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(120.0f, 612.0f);
                    pdfContentByte2.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(120.0f, 465.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(410.0f, 150.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/TempDir/PT_FORM_HB2041_DISCLOSURE_ONLY_Excel_LONGVIEW_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/LongViewPdfs/TempDir/FINANCIAL_HARDSHIP_RELIEF_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
        } catch (Exception e) {
            out.println(e.getMessage());
            String str = "";
            for (int k = 0; k < e.getStackTrace().length; ++k) {
                str = str + e.getStackTrace()[k] + "<br>";
            }
            out.println(str);
        }
    }

    void GETINPUTNacogdoches(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        String ResultPdf = "";
        final MergePdf mergePdf = new MergePdf();
        final String Path1 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/NacogdochesPdfs/";
        final String Path2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/NacogdochesPdfs/";
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
        String Ethnicity = "";
        String Race = "";
        String TravellingChk = "";
        String TravelWhen = "";
        String TravelWhere = "";
        String TravelHowLong = "";
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
        String SympEyeConjunctivitis = "";
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
                Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-'), IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), IFNULL(DoctorsName,'-'),  CASE WHEN Ethnicity = 1 THEN 'Hispanic or Latino' WHEN Ethnicity = 2 THEN ' Non Hispanic or Latino' WHEN Ethnicity = 3 THEN 'Others' ELSE 'Others' END  From " + Database + ".PatientReg Where ID = " + ID;
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
                    Ethnicity = rset.getString(24);
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
            try {
                Query = " Select CASE WHEN TravellingChk = 1 THEN 'YES' WHEN TravellingChk = 0 THEN 'NO' ELSE 'NO' END, IFNULL(DATE_FORMAT(TravelWhen,'%m/%d/%Y'),''),  IFNULL(TravelWhere,''), IFNULL(TravelHowLong,''), CASE WHEN COVIDExposedChk = 1 THEN 'YES' WHEN COVIDExposedChk = 0 THEN 'NO' ELSE 'NO' END,  CASE WHEN SympFever = 1 THEN 'YES' ELSE '' END, CASE WHEN SympBodyAches = 1 THEN 'YES' ELSE '' END, CASE WHEN SympSoreThroat = 1 THEN 'YES' ELSE '' END,  CASE WHEN SympFatigue = 1 THEN 'YES' ELSE '' END,CASE WHEN SympRash = 1 THEN 'YES' ELSE '' END, CASE WHEN SympVomiting = 1 THEN 'YES' ELSE '' END,  CASE WHEN SympDiarrhea = 1 THEN 'YES' ELSE '' END, CASE WHEN SympCough = 1 THEN 'YES' ELSE '' END, CASE WHEN SympRunnyNose = 1 THEN 'YES' ELSE '' END,  CASE WHEN SympNausea = 1 THEN 'YES' ELSE '' END, CASE WHEN SympFluSymptoms = 1 THEN 'YES' ELSE '' END, CASE WHEN SympEyeConjunctivitis = 1 THEN 'YES' ELSE '' END,  CASE WHEN Race = 1 THEN 'African American' WHEN Race = 2 THEN 'American Indian or Alska Native' WHEN Race = 3 THEN 'Asian' WHEN Race = 4 THEN 'Native Hawaiian or Other Pacific Islander'  WHEN Race = 5 THEN 'White' WHEN Race = 6 THEN 'Others' ELSE 'Others' END  from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
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
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error in getting PatientReg_Details Table : " + e.getMessage());
            }
            if (SelfPayChk == 1) {
                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%m/%d/%Y'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from " + Database + ".InsuranceInfo  where PatientRegId = " + ID;
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
                Query = "Select IFNULL(PayerName,'') from " + Database + ".ProfessionalPayers where id = " + SecondryInsurance;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    SecondryInsurance = rset.getString(1);
                }
                rset.close();
                stmt.close();
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
            String inputFilePathTmp = "";
            String outputFilePathTmp = "";
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/NacogdochesPdfs/FINANCIAL_HARDSHIP_RELIEF.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/NacogdochesPdfs/TempDir/FINANCIAL_HARDSHIP_RELIEF_" + ClientId + "_" + MRN + ".pdf";
            final OutputStream fos1 = new FileOutputStream(new File(outputFilePathTmp));
            final PdfReader pdfReader1 = new PdfReader(inputFilePathTmp);
            final PdfStamper pdfStamper1 = new PdfStamper(pdfReader1, fos1);
            for (int i = 1; i <= pdfReader1.getNumberOfPages(); ++i) {
                if (i == 1) {
                    final PdfContentByte pdfContentByte = pdfStamper1.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setTextMatrix(370.0f, 330.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 270.0f);
                    pdfContentByte.showText(LastName + " , " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 260.0f);
                    pdfContentByte.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 250.0f);
                    pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 240.0f);
                    pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0f, 230.0f);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            if (SelfPayChk == 0) {
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/NacogdochesPdfs/GeneralForm_Nacogdoches.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/NacogdochesPdfs/TempDir/FINANCIAL_HARDSHIP_RELIEF_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/nacogdoches/Result_" + ClientId + "_" + MRN + ".pdf";
            } else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/NacogdochesPdfs/GeneralForm_Nacogdoches.pdf";
            }
            String inputFilePath = "";
            inputFilePath = ResultPdf;
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf";
            final OutputStream fos2 = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader2 = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper2 = new PdfStamper(pdfReader2, fos2);
//            final GenerateBarCode barCode = new GenerateBarCode();
//            final String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
//            final Image image = Image.getInstance(BarCodeFilePath);
//            image.scaleAbsolute(150.0f, 30.0f);
            for (int j = 1; j <= pdfReader2.getNumberOfPages(); ++j) {
                if (j == 1) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setTextMatrix(80.0f, 705.0f);
                    pdfContentByte2.showText(LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(200.0f, 705.0f);
                    pdfContentByte2.showText(FirstName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(310.0f, 705.0f);
                    pdfContentByte2.showText(MiddleInitial);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(365.0f, 705.0f);
                    pdfContentByte2.showText("Title: " + Title);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(510.0f, 705.0f);
                    pdfContentByte2.showText(MaritalStatus);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 675.0f);
                    pdfContentByte2.showText(SSN);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(260.0f, 675.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(350.0f, 675.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(510.0f, 675.0f);
                    pdfContentByte2.showText(gender);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(40.0f, 640.0f);
                    pdfContentByte2.showText(Address);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(255.0f, 640.0f);
                    pdfContentByte2.showText(CityStateZip);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(410.0f, 645.0f);
                    pdfContentByte2.showText(Email);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 615.0f);
                    pdfContentByte2.showText(Employer);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(230.0f, 615.0f);
                    pdfContentByte2.showText(EmpContact);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 615.0f);
                    pdfContentByte2.showText(Occupation);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 585.0f);
                    pdfContentByte2.showText(PriCarePhy);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(230.0f, 585.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 495.0f);
                    pdfContentByte2.showText("Random CHecks COmplete String here");
                    pdfContentByte2.endText();
                    if (WorkersCompPolicy == 1 && MotorVehAccident == 0) {
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(440.0f, 452.0f);
                        pdfContentByte2.showText("YES (WCP) and NO (MVA)");
                        pdfContentByte2.endText();
                    } else if (WorkersCompPolicy == 0 && MotorVehAccident == 1) {
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(440.0f, 452.0f);
                        pdfContentByte2.showText("NO (WCP) and YES (MVA)");
                        pdfContentByte2.endText();
                    } else if (WorkersCompPolicy == 0 && MotorVehAccident == 0) {
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(440.0f, 452.0f);
                        pdfContentByte2.showText("NO (WCP) and NO (MVA)");
                        pdfContentByte2.endText();
                    } else if (WorkersCompPolicy == 1 && MotorVehAccident == 1) {
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(440.0f, 452.0f);
                        pdfContentByte2.showText("YES (WCP) and YES (MVA)");
                        pdfContentByte2.endText();
                    }
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(180.0f, 430.0f);
                    pdfContentByte2.showText(PriInsuranceName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 395.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(180.0f, 395.0f);
                    pdfContentByte2.showText(SubscriberName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(330.0f, 395.0f);
                    pdfContentByte2.showText(PrimarySSN);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(470.0f, 395.0f);
                    pdfContentByte2.showText(SubscriberDOB);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(80.0f, 360.0f);
                    pdfContentByte2.showText(MemId);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(240.0f, 360.0f);
                    pdfContentByte2.showText(GrpNumber);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(390.0f, 360.0f);
                    pdfContentByte2.showText(PatientRelationtoPrimary);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(480.0f, 360.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(315.0f, 330.0f);
                    pdfContentByte2.showText(SecondryInsurance);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(70.0f, 305.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(185.0f, 305.0f);
                    pdfContentByte2.showText(SubscriberName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(350.0f, 305.0f);
                    pdfContentByte2.showText(PrimarySSN);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(460.0f, 305.0f);
                    pdfContentByte2.showText(SubscriberDOB);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 270.0f);
                    pdfContentByte2.showText(MemberID_2);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(230.0f, 270.0f);
                    pdfContentByte2.showText(GroupNumber_2);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(360.0f, 270.0f);
                    pdfContentByte2.showText(PatientRelationshiptoSecondry);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 80.0f);
                    pdfContentByte2.showText(NextofKinName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(285.0f, 80.0f);
                    pdfContentByte2.showText(RelationToPatientER);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(395.0f, 80.0f);
                    pdfContentByte2.showText("");
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(500.0f, 80.0f);
                    pdfContentByte2.showText(PhoneNumberER);
                    pdfContentByte2.endText();
                }
                if (j == 2) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 80.0f);
                    pdfContentByte2.showText(LastName + " , " + FirstName + " " + MiddleInitial);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 70.0f);
                    pdfContentByte2.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 60.0f);
                    pdfContentByte2.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 50.0f);
                    pdfContentByte2.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 40.0f);
                    pdfContentByte2.showText("Dr. " + DoctorName);
                    pdfContentByte2.endText();
                }
                if (j == 3) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 80.0f);
                    pdfContentByte2.showText(LastName + " , " + FirstName + " " + MiddleInitial);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 70.0f);
                    pdfContentByte2.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 60.0f);
                    pdfContentByte2.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 50.0f);
                    pdfContentByte2.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 40.0f);
                    pdfContentByte2.showText("Dr. " + DoctorName);
                    pdfContentByte2.endText();
                }
                if (j == 4) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(100.0f, 160.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 160.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(440.0f, 120.0f);
                    pdfContentByte2.showText(NextofKinName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 80.0f);
                    pdfContentByte2.showText(LastName + " , " + FirstName + " " + MiddleInitial);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 70.0f);
                    pdfContentByte2.showText(ClientName + "        Sex:" + gender);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 60.0f);
                    pdfContentByte2.showText("DOB: " + DOB + "        Age:" + Age);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 50.0f);
                    pdfContentByte2.showText("MRN: " + MRN + "        DOS: " + DOS);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 8.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(60.0f, 40.0f);
                    pdfContentByte2.showText("Dr. " + DoctorName);
                    pdfContentByte2.endText();
                }
                if (j == 5) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(425.0f, 275.0f);
                    pdfContentByte2.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(440.0f, 240.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(440.0f, 200.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
                if (j == 6) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(135.0f, 730.0f);
                    pdfContentByte2.showText(LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(425.0f, 730.0f);
                    pdfContentByte2.showText(FirstName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(120.0f, 715.0f);
                    pdfContentByte2.showText(DOB);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(415.0f, 715.0f);
                    pdfContentByte2.showText(PhNumber);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(90.0f, 700.0f);
                    pdfContentByte2.showText(Address);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(400.0f, 700.0f);
                    pdfContentByte2.showText(CityStateZip);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(140.0f, 552.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(445.0f, 130.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(280.0f, 115.0f);
                    pdfContentByte2.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte2.endText();
                }
                if (j == 7) {
                    final PdfContentByte pdfContentByte2 = pdfStamper2.getOverContent(j);
                    if (PatientRelationtoPrimary.equals("Self")) {
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(120.0f, 645.0f);
                        pdfContentByte2.showText(FirstName + " " + MiddleInitial + " " + LastName);
                        pdfContentByte2.endText();
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(360.0f, 645.0f);
                        pdfContentByte2.showText(PhNumber);
                        pdfContentByte2.endText();
                    } else {
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(120.0f, 645.0f);
                        pdfContentByte2.showText(NextofKinName);
                        pdfContentByte2.endText();
                        pdfContentByte2.beginText();
                        pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                        pdfContentByte2.setColorFill(BaseColor.BLACK);
                        pdfContentByte2.setTextMatrix(360.0f, 645.0f);
                        pdfContentByte2.showText(PhoneNumberER);
                        pdfContentByte2.endText();
                    }
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(120.0f, 612.0f);
                    pdfContentByte2.showText(FirstName + " " + MiddleInitial + " " + LastName);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(120.0f, 465.0f);
                    pdfContentByte2.showText(DOS);
                    pdfContentByte2.endText();
                    pdfContentByte2.beginText();
                    pdfContentByte2.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte2.setColorFill(BaseColor.BLACK);
                    pdfContentByte2.setTextMatrix(410.0f, 150.0f);
                    pdfContentByte2.showText(Date);
                    pdfContentByte2.endText();
                }
            }
            pdfStamper2.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/NacogdochesPdfs/TempDir/PT_FORM_HB2041_DISCLOSURE_ONLY_Excel_LONGVIEW_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/NacogdochesPdfs/TempDir/FINANCIAL_HARDSHIP_RELIEF_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    void GETINPUTFrontLine(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        final String ResultPdf = "";
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
        String CityStateZip = "";
        String State = "";
        String City = "";
        final String Country = "";
        String ZipCode = "";
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
        String SympEyeConjunctivitis = "";
        int WorkersCompPolicy = 0;
        String WorkersCompPolicyString = "";
        int MotorVehAccident = 0;
        String MotorVehAccidentString = "";
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
        String COVIDStatus = "";
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
                Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-'), IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), IFNULL(DoctorsName,'-'),  CASE WHEN Ethnicity = 1 THEN 'Hispanic or Latino' WHEN Ethnicity = 2 THEN ' Non Hispanic or Latino' WHEN Ethnicity = 3 THEN 'Others' ELSE 'Others' END, IFNULL(City,''), IFNULL(State,''), IFNULL(ZipCode,''), IFNULL(COVIDStatus,'')  From " + Database + ".PatientReg Where ID = " + ID;
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
                    Ethnicity = rset.getString(24);
                    City = rset.getString(25);
                    State = rset.getString(26);
                    ZipCode = rset.getString(27);
                    COVIDStatus = rset.getString(28);
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
            if (COVIDStatus.equals("")) {
                CovidTest = "NO";
            } else if (COVIDStatus == null) {
                CovidTest = "NO";
            } else {
                CovidTest = "YES";
            }
            Query = "Select COUNT(*) from " + Database + ".Patient_AdditionalInfo where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundAddInfo = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            if (FoundAddInfo > 0) {
                Query = "Select IFNULL(Date_format(CovidTestDate,'%m/%d/%Y'),'') from " + Database + ".Patient_AdditionalInfo where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    CovidTestDate = rset.getString(1);
                }
                rset.close();
                stmt.close();
                out.println(Query);
            }
            try {
                Query = " Select CASE WHEN TravellingChk = 1 THEN 'YES' WHEN TravellingChk = 0 THEN 'NO' ELSE 'NO' END, IFNULL(DATE_FORMAT(TravelWhen,'%m/%d/%Y'),''),  IFNULL(TravelWhere,''), IFNULL(TravelHowLong,''), CASE WHEN COVIDExposedChk = 1 THEN 'YES' WHEN COVIDExposedChk = 0 THEN 'NO' ELSE 'NO' END,  CASE WHEN SympFever = 1 THEN 'YES' ELSE '' END, CASE WHEN SympBodyAches = 1 THEN 'YES' ELSE '' END, CASE WHEN SympSoreThroat = 1 THEN 'YES' ELSE '' END,  CASE WHEN SympFatigue = 1 THEN 'YES' ELSE '' END,CASE WHEN SympRash = 1 THEN 'YES' ELSE '' END, CASE WHEN SympVomiting = 1 THEN 'YES' ELSE '' END,  CASE WHEN SympDiarrhea = 1 THEN 'YES' ELSE '' END, CASE WHEN SympCough = 1 THEN 'YES' ELSE '' END, CASE WHEN SympRunnyNose = 1 THEN 'YES' ELSE '' END,  CASE WHEN SympNausea = 1 THEN 'YES' ELSE '' END, CASE WHEN SympFluSymptoms = 1 THEN 'YES' ELSE '' END, CASE WHEN SympEyeConjunctivitis = 1 THEN 'YES' ELSE '' END,  CASE WHEN Race = 1 THEN 'African American' WHEN Race = 2 THEN 'American Indian or Alska Native' WHEN Race = 3 THEN 'Asian' WHEN Race = 4 THEN 'Native Hawaiian or Other Pacific Islander'  WHEN Race = 5 THEN 'White' WHEN Race = 6 THEN 'Others' ELSE 'Others' END, IFNULL(CovidExpWhen,'') from " + Database + ".PatientReg_Details where PatientRegId = " + PatientRegId;
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
                }
                rset.close();
                stmt.close();
            } catch (Exception e2) {
                out.println("Error in getting PatientReg_Details Table : " + e2.getMessage());
            }


            if (SelfPayChk == 1) {
                Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%m/%d/%Y'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from " + Database + ".InsuranceInfo  where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
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
                SymptomsString += "Eye Conjunctivitis ";
            }
            String inputFilePath = "";
            String outputFilePath = "";
            String UID = "";
            inputFilePath = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FrontLineER/GeneralForm_FrontLineER.pdf";
            outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_.pdf";
            OutputStream fos = new FileOutputStream(new File(outputFilePath));
            PdfReader pdfReader = new PdfReader(inputFilePath);
            int pageCount = pdfReader.getNumberOfPages();
            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
//            GenerateBarCode barCode = new GenerateBarCode();
//            String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);
//            Image image = Image.getInstance(BarCodeFilePath);
//            image.scaleAbsolute(150.0f, 30.0f);
            Image SignImages = null;
            File tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_0_" + ID + ".png");
            boolean exists = tmpDir.exists();
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
                SignImages.scaleAbsolute(80, 30); //Scale image's width and height
                outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + UID + "_.pdf";
            } else {
                SignImages = null;
            }
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(40, 770);
                    pdfContentByte.showText(UID);
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(130.0F, 660.0F);
                    pdfContentByte.showText(ReasonVisit);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(388.0F, 660.0F);
                    pdfContentByte.showText(MotorVehAccidentString);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(453.0F, 660.0F);
                    pdfContentByte.showText(WorkersCompPolicyString);
                    pdfContentByte.endText();
                    if (SelfPayChk == 1) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(486.0F, 660.0F);
                        pdfContentByte.showText("(Y)");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(560.0F, 660.0F);
                        pdfContentByte.showText("(N)");
                        pdfContentByte.endText();
                    } else if (SelfPayChk == 0) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(486.0F, 660.0F);
                        pdfContentByte.showText("(N)");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(560.0F, 660.0F);
                        pdfContentByte.showText("(Y)");
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(486.0F, 660.0F);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(560.0F, 660.0F);
                        pdfContentByte.showText("");
                        pdfContentByte.endText();
                    }

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90.0F, 635.0F);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380.0F, 635.0F);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(520.0F, 635.0F);
                    pdfContentByte.showText(Age);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90.0F, 595.0F);
                    pdfContentByte.showText(Address);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(300.0F, 595.0F);
                    pdfContentByte.showText(" ");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460.0F, 595.0F);
                    pdfContentByte.showText(SSN);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0F, 570.0F);
                    pdfContentByte.showText(City);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260.0F, 570.0F);
                    pdfContentByte.showText(State);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(380.0F, 570.0F);
                    pdfContentByte.showText(ZipCode);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0F, 550.0F);
                    pdfContentByte.showText(PhNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250.0F, 550.0F);
                    pdfContentByte.showText(PhNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(450.0F, 550.0F);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(100.0F, 525.0F);
                    pdfContentByte.showText(Employer);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(395.0F, 525.0F);
                    pdfContentByte.showText(Occupation);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90.0F, 435.0F);
                    pdfContentByte.showText(EmployerAddress);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90.0F, 435.0F);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(409.0F, 502.0F);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500.0F, 502.0F);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(105.0F, 480.0F);
                    pdfContentByte.showText(MaritalStatus);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250.0F, 480.0F);
                    pdfContentByte.showText(gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0F, 480.0F);
                    pdfContentByte.showText(Email);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0F, 460.0F);
                    pdfContentByte.showText(NextofKinName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(360.0F, 460.0F);
                    pdfContentByte.showText(PhoneNumberER);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(520.0F, 460.0F);
                    pdfContentByte.showText(RelationToPatientER);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0F, 367.0F);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(310.0F, 367.0F);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(480.0F, 367.0F);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0F, 335.0F);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(310.0F, 335.0F);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(480.0F, 335.0F);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0F, 305.0F);
                    pdfContentByte.showText(PriInsuranceName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(320.0F, 305.0F);
                    pdfContentByte.showText(MemId);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(480.0F, 305.0F);
                    pdfContentByte.showText(GrpNumber);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(450.0F, 198.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0F, 170.0F);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(200, 195); //Set position for image in PDF
                        pdfContentByte.addImage(SignImages);
                    }

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410.0F, 167.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0F, 134.0F);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0F, 123.0F);
                    pdfContentByte.showText(ClientName + "  Sex: " + gender);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0F, 113.0F);
                    pdfContentByte.showText("DOB:" + DOB + "  Age:(" + Age + ")");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0F, 103.0F);
                    pdfContentByte.showText("MRN:" + MRN + "  DOS:" + DOS + "");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0F, 93.0F);
                    pdfContentByte.showText("Dr. " + DoctorName);
                    pdfContentByte.endText();

                }

                if (i == 2) {

                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190.0F, 142.0F);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(470.0F, 100.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();


                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(170, 90); //Set position for image in PDF
                        pdfContentByte.addImage(SignImages);
                    }

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(470.0F, 83.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(470.0F, 63.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();

                }

                if (i == 3) {

                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0F, 613.0F);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0F, 580.0F);
                    pdfContentByte.showText(Address);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(140.0F, 550.0F);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(370.0F, 550.0F);
                    pdfContentByte.showText(SSN);
                    pdfContentByte.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(110, 150); //Set position for image in PDF
                        pdfContentByte.addImage(SignImages);
                    }


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430.0F, 150.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();

                }

                if (i == 4) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190.0F, 149.0F);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(200, 100); //Set position for image in PDF
                        pdfContentByte.addImage(SignImages);
                    }

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(470.0F, 100.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(470.0F, 125.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();

                }

                if (i == 5) {

                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190.0F, 325.0F);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(220, 275); //Set position for image in PDF
                        pdfContentByte.addImage(SignImages);
                    }

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(470.0F, 275.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(470.0F, 242.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }

                if (i == 6) {

                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150.0F, 547.0F);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0F, 547.0F);
                    pdfContentByte.showText(DOB);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(200.0F, 505.0F);
                    pdfContentByte.showText(PriInsuranceName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0F, 460.0F);
                    pdfContentByte.showText(MemId);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410.0F, 460.0F);
                    pdfContentByte.showText(DOS);
                    pdfContentByte.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(210, 230); //Set position for image in PDF
                        pdfContentByte.addImage(SignImages);
                    }

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0F, 230.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();

                }

                if (i == 7) {

                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0F, 473.0F);
                    pdfContentByte.showText(CovidTest);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(450.0F, 473.0F);
                    pdfContentByte.showText(CovidTestDate);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80.0F, 442.0F);
                    pdfContentByte.showText(COVIDExposedChk);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(525.0F, 442.0F);
                    pdfContentByte.showText(CovidExpWhen);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(325.0F, 415.0F);
                    pdfContentByte.showText(SymptomsString);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0F, 390.0F);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(210, 115); //Set position for image in PDF
                        pdfContentByte.addImage(SignImages);
                    }

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0F, 115.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(110.0F, 65.0F);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400.0F, 65.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();

                }

                if (i == 8) {

                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(123.0F, 613.0F);
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(435.0F, 613.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0F, 589.0F);
                    pdfContentByte.showText("");
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(170.0F, 559.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(130, 305); //Set position for image in PDF
                        pdfContentByte.addImage(SignImages);
                    }

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(329.0F, 305.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0F);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(329.0F, 119.0F);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }

            }
            pdfStamper.close(); //close pdfStamper
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }

//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
//            //Parser.SetField("imagelist", String.valueOf(imagelist));
//            Parser.SetField("pageCount", String.valueOf(pageCount));
//            Parser.SetField("PatientRegId", String.valueOf(ID));
//            Parser.SetField("FileName", String.valueOf(FirstNameNoSpaces+LastName+ID+"_.pdf"));
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/DownloadBundleHTML.html");


        } catch (Exception e) {
            out.println(e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }

    void GETINPUTERDallas(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        final MergePdf mergePdf = new MergePdf();
        final int ID = Integer.parseInt(request.getParameter("ID").trim());
        final String Path1 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/";
        final String Path2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/";
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
                    Services.DumException("DownloadBundle", "GetINput ERDallas", request, e, this.getServletContext());
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
                    Services.DumException("DownloadBundle", "GetINput ER Dallas", request, e, this.getServletContext());
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
                    Services.DumException("DownloadBundle", "GetINput ER Dallas", request, e, this.getServletContext());
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
            String inputFilePathTmp = "";
            String outputFilePathTmp = "";
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/ABNformEnglish.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/ABNformEnglish_" + ClientId + "_" + MRN + ".pdf";
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
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION_" + ClientId + "_" + MRN + ".pdf";
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
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION_" + ClientId + "_" + MRN + ".pdf";
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
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/WC_QUESTIONNAIRE.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/WC_QUESTIONNAIRE_" + ClientId + "_" + MRN + ".pdf";
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
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/WC_MVA_assignmentofproceeds.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/WC_MVA_assignmentofproceeds_" + ClientId + "_" + MRN + ".pdf";
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
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/MVA_ASSIGNMENTOFPROCEEDS.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/MVA_ASSIGNMENTOFPROCEEDS_" + ClientId + "_" + MRN + ".pdf";
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
                    pdfContentByte.setTextMatrix(45.0f, 135.0f);
                    pdfContentByte.showText(LastName + ", " + FirstName);
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45.0f, 82.0f);
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
                    pdfContentByte.setTextMatrix(430.0f, 290.0f);
                    pdfContentByte.showText(Date);
                    pdfContentByte.endText();
                }
            }
            pdfStamper1.close();
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/MVACLAIMFORM.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/MVACLAIMFORM_" + ClientId + "_" + MRN + ".pdf";
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
            inputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/Medicalreleaseform.pdf";
            outputFilePathTmp = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/Medicalreleaseform_" + ClientId + "_" + MRN + ".pdf";
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
            String inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/UHCINSAPPEALFORMS.pdf";
            String outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/UHCINSAPPEALFORMS_" + ClientId + "_" + MRN + ".pdf";
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
                }
            }
            pdfStamper2.close();
            inputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/MEDICAIDSELFPAYAGREEMENT.pdf";
            outputFilePathTmp2 = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/MEDICAIDSELFPAYAGREEMENT_" + ClientId + "_" + MRN + ".pdf";
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
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/GeneralForm_ERDallas.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/ERDallas/Result.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/ERDallas/Result.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/WC_QUESTIONNAIRE_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
                if (WCPInjuryRelatedAutoMotorAccident.equals("1") || WCPInjuryOccurVehicle.equals("1")) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/ERDallas/Result.pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/WC_MVA_assignmentofproceeds_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
                }
            } else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/GeneralForm_ERDallas.pdf";
            }
            if (MotorVehicleAccidentChk == 1) {
                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/MVA_ASSIGNMENTOFPROCEEDS_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
                Query = "Select AutoInsuranceInformationChk from " + Database + ".Patient_AutoInsuranceInfo where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    AutoInsuranceInformationChk = rset.getString(1);
                }
                rset.close();
                stmt.close();
                if (AutoInsuranceInformationChk.equals("1")) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/MVACLAIMFORM_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
                }
            } else if (MotorVehicleAccidentChk == 0) {
                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/MVA_ASSIGNMENTOFPROCEEDS.pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
            } else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/GeneralForm_ERDallas.pdf";
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
                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/MEDICAIDSELFPAYAGREEMENT_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
                    }
                    if (GFIPMedicare == 1) {
                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/ABNformEnglish_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
                    }
                }
                if (HIPrimaryInsurance.trim().toUpperCase().equals("UNITED HEALTHCARE")) {
                    System.out.println("Inside United HealthCare");
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/UHCINSAPPEALFORMS_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
                }
            } else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/GeneralForm_ERDallas.pdf";
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
            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/Medicalreleaseform_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
            ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
            String DOSDate = "";
            String DOSTime = "";
            DOSDate = DOS.substring(0, 10);
            DOSTime = DOS.substring(11, 19);
            String inputFilePath = "";
            inputFilePath = ResultPdf;
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/ERDallas/" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf";
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
                    pdfContentByte3.setTextMatrix(70.0f, 600.0f);
                    pdfContentByte3.showText(LastName);
                    pdfContentByte3.endText();
                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(150.0f, 600.0f);
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
                    pdfContentByte3.setTextMatrix(450.0f, 590.0f);
                    pdfContentByte3.showText("");
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
                    pdfContentByte3.showText(HISubscriberGroupNo);
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
                    pdfContentByte3.setTextMatrix(440.0f, 160.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
            }
            pdfStamper3.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
            File File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/WC_QUESTIONNAIRE_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/WC_MVA_assignmentofproceeds_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/MVACLAIMFORM_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/MVA_ASSIGNMENTOFPROCEEDS_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/ABNformEnglish_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/Medicalreleaseform_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/UHCINSAPPEALFORMS_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
            File = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/MEDICAIDSELFPAYAGREEMENT_" + ClientId + "_" + MRN + ".pdf");
            File.delete();
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
