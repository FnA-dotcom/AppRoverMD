package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;

@SuppressWarnings("Duplicates")
public class PrintFaceSheet extends HttpServlet {
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void HandleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();
        Connection conn = null;
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
            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            try {
                if (UserId.equals("")) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            } catch (Exception excp) {
                conn = null;
                out.println("Exception excp conn: " + excp.getMessage());
            }

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.compareTo("GETINPUTFrontline") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for Orange and Odessa", FacilityIndex);
                this.GETINPUTFrontline(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex,helper);
            } else if (ActionID.compareTo("GETINPUTSanMarcos") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for Orange and Odessa", FacilityIndex);
                this.GETINPUTSanMarcos(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex,helper);
            } else if (ActionID.compareTo("GETINPUTVictoria") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Print Labels Option ", "Click on Print Lable Option from View Patients Tab for Orange and Odessa", FacilityIndex);
                this.GETINPUTVictoria(request, response, out, conn, DatabaseName, context, UserId, FacilityIndex,helper);
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

    public static int getAge(LocalDate dob) {
        LocalDate curDate = LocalDate.now();
        return Period.between(dob, curDate).getYears();
    }

    private void GETINPUTSanMarcos(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String Database, ServletContext servletContext, String UserId, int ClientId, UtilityHelper helper) {
        final String ID = request.getParameter("PatientId");
        Statement stmt = null;
        ResultSet rset = null;
        String VisitNumber = "";
        String Query = "";
        String DateTime = "";
        String ClientName = "";
        String ClientAddress = "";
        String ClientPhone = "";
        String DoctorsName = "";
        String DoctorsId = "";
        String ClientIndex = null;
        String FirstName = null;
        String LastName = null;
        String MiddleInitial = null;
        String DOB = null;
        String Age = "";
        String Gender = null;
        String MRN = null;
        String SSN = null;
        String CreatedDate = null;
        String Address = null;
        String StreetAddress2 = null;
        String Email = "";
        String Ethnicity = "";
        String PhNumber = "";
        String State = "";
        String City = "";
        String ZipCode = "";
        String DOBForAge = "";
        String DirectoryName = "";
        int SelfPayChk = 0;

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

        try {
            Query = "Select IFNULL(FullName,''), IFNULL(Address,''), IFNULL(Phone,'') " +
                    "from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientName = rset.getString(1);
                ClientAddress = rset.getString(2);
                ClientPhone = rset.getString(3);
            }
            rset.close();
            stmt.close();

            Query = " SELECT ID, ClientIndex, IFNULL(FirstName,''), IFNULL(LastName,''), IFNULL(MiddleInitial,''), IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'),''), IFNULL(Age,''), " +
                    " IFNULL(Gender,''), IFNULL(MRN,''), IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y'),DATE_FORMAT(CreatedDate,'%m/%d/%Y')),date_format(now(),'%Y%m%d%H%i%s'), " +
                    " IFNULL(DoctorsName, ''), IFNULL(SSN,''), IFNULL(Address,''), IFNULL(Email,''), IFNULL(Ethnicity,''),IFNULL(PhNumber,''), IFNULL(City,''), IFNULL(State,'')," +
                    " IFNULL(ZipCode,''), IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'),''), IFNULL(SelfPayChk,0), IFNULL(StreetAddress2,'') FROM   " + Database + ".PatientReg where ID= " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientIndex = rset.getString(2);
                FirstName = rset.getString(3);
                LastName = rset.getString(4);
                MiddleInitial = rset.getString(5);
                DOB = rset.getString(6);
                Age = rset.getString(7);
                Gender = rset.getString(8);
                MRN = rset.getString(9);
                CreatedDate = rset.getString(10);
                DateTime = rset.getString(11);
                DoctorsId = rset.getString(12);
                SSN = rset.getString(13);
                Address = rset.getString(14);
                Email = rset.getString(15);
                Ethnicity = rset.getString(16);
                PhNumber = rset.getString(17);
                City = rset.getString(18);
                State = rset.getString(19);
                ZipCode = rset.getString(20);
                DOBForAge = rset.getString(21);
                SelfPayChk = rset.getInt(22);
                StreetAddress2 = rset.getString(23);

            }
            rset.close();
            stmt.close();

            if (!DOB.equals("")) {
                Age = String.valueOf(helper.getAge(LocalDate.parse(DOBForAge)));
            } else {
                Age = "0";
            }

            if (!DoctorsId.equals("")) {
                Query = "Select IFNULL(DoctorsFirstName,''), IFNULL(DoctorsLastName,'') " +
                        "from " + Database + ".DoctorsList where Id = " + DoctorsId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    DoctorsName = "Dr. " + rset.getString(1) + " " + rset.getString(2);
                }
                rset.close();
                stmt.close();
            }


        } catch (Exception ex3) {
        }
        try {
            String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + DateTime + ".pdf";
            OutputStream fos = new FileOutputStream(new File(outputFilePath));
            PdfReader pdfReader = new PdfReader("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FaceSheet_SanMarcos.pdf");

            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);


            Query = "Select MAX(VisitNumber) from " + Database + ".PatientVisit " +
                    "where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                VisitNumber = rset.getString(1).trim();
            }
            rset.close();
            stmt.close();
            VisitNumber = "VN-" + MRN + "-" + VisitNumber;


            String Race = "";

            Query = "Select IFNULL(Race,'0') from " + Database + ".PatientReg_Details " +
                    "where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Race = rset.getString(1);
            }
            rset.close();
            stmt.close();

            switch (Race) {
                case "0":
                    Race = "Not Specified";
                    break;
                case "1":
                    Race = "African American";
                    break;
                case "2":
                    Race = "American Indian or Alaska Native";
                    break;
                case "3":
                    Race = "Asian";
                    break;
                case "4":
                    Race = "Native Hawaiian or Other Pacific Islander";
                    break;
                case "5":
                    Race = "White";
                    break;
                case "6":
                    Race = "Other";
                    break;
                default:
                    Race = "Not Specified";
                    break;
            }


            switch (Ethnicity) {
                case "0":
                    Ethnicity = "Not Specified";
                    break;
                case "1":
                    Ethnicity = "Hispanic or Latino";
                    break;
                case "2":
                    Ethnicity = "Non Hispanic or Latino";
                    break;
                case "3":
                    Ethnicity = "Others";
                    break;
                default:
                    Ethnicity = "Not Specified";
                    break;
            }

            String PersonName = null;
            String PersonAddress = null;
            String PersonPhoneNumber = null;
            String PersonRelationToPatient = null;
            String CityER = "";
            String StateER = "";
            String ZipCodeER = "";

            try {
                Query = "Select IFNULL(NextofKinName,''), IFNULL(Address,''), IFNULL(PhoneNumber,''), " +
                        "IFNULL(RelationToPatient,''), IFNULL(City,''), " +
                        "IFNULL(State,''), IFNULL(ZipCode,'') from " + Database + ".EmergencyInfo " +
                        "where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PersonName = rset.getString(1);
                    PersonAddress = rset.getString(2);
                    PersonPhoneNumber = rset.getString(3);
                    PersonRelationToPatient = rset.getString(4);
                    CityER = rset.getString(5);
                    StateER = rset.getString(6);
                    ZipCodeER = rset.getString(7);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                e.getStackTrace();
            }

            String PriInsurance = "";
            String PayerName = "";
            String MemId = "";
            String GrpNumber = "";
            String PatientRelationtoPrimary = "";
            String PriInsurerName = "";
            String PrimaryDOB = "";
            String PrimarySSN = "";
            String SecondInsurance = "";
            String SecondPayerName = "";
            String SecondMemId = "";
            String SecondGrpNumber = "";
            String SecondDOB = "";
            String SubscriberName = "";
            String SecondPatientRelationtoPrimary = "";

            //IFNULL(PatientRelationtoPrimary,'')
            if (SelfPayChk == 1) {
                Query = "Select IFNULL(PriInsuranceName,''), IFNULL(MemId,''), IFNULL(GrpNumber,'') ," +
                        "CASE WHEN PatientRelationtoPrimary = -1 THEN '' ELSE PatientRelationtoPrimary END, IFNULL(PriInsurerName,'')," +
                        "IFNULL(DATE_FORMAT(PrimaryDOB,'%m/%d/%Y'),'00/00/0000')," +
                        "IFNULL(PrimarySSN,'')  " +
                        "from " + Database + ".InsuranceInfo where PatientRegId = " + ID;
                try {
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PriInsurance = rset.getString(1);
                        MemId = rset.getString(2);
                        GrpNumber = rset.getString(3);
                        PatientRelationtoPrimary = rset.getString(4);
                        PriInsurerName = rset.getString(5);
                        PrimaryDOB = rset.getString(6);
                        PrimarySSN = rset.getString(7);
                    }
                    rset.close();
                    stmt.close();
                } catch (Exception e) {
                    e.getStackTrace();
                }

                try {
                    if (!PriInsurance.equals("")) {
                        Query = "Select IFNULL(PayerName,'') from " + Database + ".ProfessionalPayers " +
                                "where id = " + PriInsurance;
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next()) {
                            PayerName = rset.getString(1);
                        }
                        rset.close();
                        stmt.close();
                    }
                } catch (Exception e) {
                    e.getStackTrace();
                }
                //IFNULL(PatientRelationshiptoSecondry,'')
                Query = "Select IFNULL(SecondryInsurance,''), IFNULL(MemberID_2,''), " +
                        "IFNULL(GroupNumber_2,'') ," +
                        "CASE WHEN PatientRelationshiptoSecondry = -1 THEN '' ELSE PatientRelationshiptoSecondry END ," +
                        "IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'00/00/0000'),SubscriberName  " +
                        "from " + Database + ".InsuranceInfo where PatientRegId = " + ID;
                try {
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        SecondInsurance = rset.getString(1);
                        SecondMemId = rset.getString(2);
                        SecondGrpNumber = rset.getString(3);
                        SecondPatientRelationtoPrimary = rset.getString(4);
                        SecondDOB = rset.getString(5);
                        SubscriberName = rset.getString(6);
                    }
                    rset.close();
                    stmt.close();
                } catch (Exception e) {
                    e.getStackTrace();
                }

                try {
                    if (!SecondInsurance.equals("")) {
                        Query = "SELECT IFNULL(PayerName,'') " +
                                "FROM " + Database + ".ProfessionalPayers " +
                                "WHERE id = " + SecondInsurance;
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next()) {
                            SecondPayerName = rset.getString(1);
                        }
                        rset.close();
                        stmt.close();
                    }
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }

            Query = " Select ReturnPatient, Google, MapSearch, Billboard, OnlineReview, TV, " +
                    "Website, BuildingSignDriveBy, Facebook, School, IFNULL(School_text ,'-'), " +
                    "Twitter, Magazine, IFNULL(Magazine_text,'-'), Newspaper, " +
                    "IFNULL(Newspaper_text,'-'), FamilyFriend, IFNULL(FamilyFriend_text,'-'), " +
                    "UrgentCare, IFNULL(UrgentCare_text,'-'), CommunityEvent, " +
                    "IFNULL(CommunityEvent_text,'-'),  IFNULL(Work_text,'-'), " +
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

            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 12); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.showTextAligned(Element.ALIGN_CENTER, ClientName.toUpperCase(), 280, 740, 0);// add the text//BILLING Provider  facility TAX ID
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 12); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.showTextAligned(Element.ALIGN_CENTER, ClientAddress.toUpperCase(), 280, 725, 0);// add the text//BILLING Provider  facility TAX ID
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 12); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.showTextAligned(Element.ALIGN_CENTER, ClientPhone, 280, 710, 0);// add the text//BILLING Provider  facility TAX ID
                    pdfContentByte.endText();


                    /////////////////////////////////////Patient Information//////////////////////////////////////////////////
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90, 633); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName + " " + LastName);//("Patient Name"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 620); // set x and y co-ordinates
                    pdfContentByte.showText(SSN);//("SSN"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 607); // set x and y co-ordinates
                    pdfContentByte.showText(StreetAddress2 + " " + Address);//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 597); // set x and y co-ordinates
                    pdfContentByte.showText(City + ", " + State + ", " + ZipCode);//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 580); // set x and y co-ordinates
                    pdfContentByte.showText(Email);//("Email"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90, 567); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Alternate Id"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 552); // set x and y co-ordinates
                    pdfContentByte.showText("");//("PCP"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 539); // set x and y co-ordinates
                    pdfContentByte.showText(Race);//("Race"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(270, 635); // set x and y co-ordinates
                    pdfContentByte.showText(Age);//("Age"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 635); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Home Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275, 622); // set x and y co-ordinates
                    pdfContentByte.showText(Gender);//("Gender"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 622); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Work Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(265, 608); // set x and y co-ordinates
                    pdfContentByte.showText(DOB);//("DOB"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(405, 608); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PhNumber));//("Mobile Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(265, 596); // set x and y co-ordinates
                    pdfContentByte.showText(CreatedDate);//("DOS"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(385, 596); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Religion"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(265, 582); // set x and y co-ordinates
                    pdfContentByte.showText(MRN);//("MRN"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(423, 582); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Employement Status"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(280, 569); // set x and y co-ordinates
                    pdfContentByte.showText(VisitNumber);//("Acct No."); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(385, 569); // set x and y co-ordinates
                    pdfContentByte.showText(Ethnicity);//("Ethnicity"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420, 557); // set x and y co-ordinates
                    pdfContentByte.showText(HearAboutUsString);//("How heard about us!"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420, 551); // set x and y co-ordinates
                    pdfContentByte.showText(HearAboutUsString2);//("How heard about us!"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(345, 540); // set x and y co-ordinates
                    pdfContentByte.showText("ER Physician: " + DoctorsName);//(ER Physician NAME
                    pdfContentByte.endText();
                    ///////////////////////////////////////////////////////////////////////////////////////


//////////////////////////////////////////////////Person Contact////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 505); // set x and y co-ordinates
                    pdfContentByte.showText(PersonName);//("Name"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(72, 491); // set x and y co-ordinates
                    pdfContentByte.showText(PersonAddress);//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(72, 481); // set x and y co-ordinates
                    pdfContentByte.showText(CityER + ", " + StateER + ", " + ZipCodeER);//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(63, 465); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Email"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(280, 520); // set x and y co-ordinates
                    pdfContentByte.showText("");//("H. Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(270, 508); // set x and y co-ordinates
                    pdfContentByte.showText(PersonPhoneNumber);//("Mobile"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(285, 495); // set x and y co-ordinates
                    pdfContentByte.showText("");//("W. Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260, 480); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Ext"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 520); // set x and y co-ordinates
                    pdfContentByte.showText(PersonRelationToPatient);//("Relationship"); // add the text
                    pdfContentByte.endText();

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                    //////////////////////////Insurance Info///////////////////////////////////////

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(78, 411); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Decription"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(269, 412); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Ins.ID"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 414); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance No."); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(85, 398); // set x and y co-ordinates
                    pdfContentByte.showText(PayerName);//("InsCompany"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410, 398); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Carrier Key"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410, 400); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance Class"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(65, 383); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(289, 385); // set x and y co-ordinates
                    pdfContentByte.showText("");//(String.valueOf(GrpNumber));//("Plan Code"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410, 387); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance Type"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(408, 374); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance Fax"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 360); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Attention To"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(74, 359); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Phone/ext"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(83, 322); // set x and y co-ordinates
                    pdfContentByte.showText(PatientRelationtoPrimary);//("Relationship"); // add the text
                    pdfContentByte.endText();

                    if (PatientRelationtoPrimary.toUpperCase().equals("SELF")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(275, 322); // set x and y co-ordinates
                        pdfContentByte.showText(Gender);//("Gender"); // add the text
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(275, 322); // set x and y co-ordinates
                        pdfContentByte.showText("");//("Gender"); // add the text
                        pdfContentByte.endText();
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 325); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Home Phone"); // add the text
                    pdfContentByte.endText();

                    if (PatientRelationtoPrimary.toUpperCase().equals("SELF")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(83, 309); // set x and y co-ordinates
                        pdfContentByte.showText(FirstName + " " + LastName);//("Full Name"); // add the text
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(275, 309); // set x and y co-ordinates
                        pdfContentByte.showText(DOB);//("DOB"); // add the text
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(83, 309); // set x and y co-ordinates
                        pdfContentByte.showText(PriInsurerName);//("Full Name"); // add the text
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(275, 309); // set x and y co-ordinates
                        pdfContentByte.showText(PrimaryDOB);//("DOB"); // add the text
                        pdfContentByte.endText();
                    }

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 309); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Work Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(83, 296); // set x and y co-ordinates
                    pdfContentByte.showText(StreetAddress2 + " " + Address);//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(83, 286); // set x and y co-ordinates
                    pdfContentByte.showText(City + ", " + State + ", " + ZipCode);//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(285, 295); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Start Date"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(403, 297); // set x and y co-ordinates
                    pdfContentByte.showText(PhNumber);//("Mobile Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(285, 283); // set x and y co-ordinates
                    pdfContentByte.showText("");//("End Date"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(405, 285); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Authorization"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390, 272); // set x and y co-ordinates
                    pdfContentByte.showText(MemId);//("Policy ID"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390, 259); // set x and y co-ordinates
                    pdfContentByte.showText(GrpNumber);//("Group ID"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(53, 254); // set x and y co-ordinates
                    pdfContentByte.showText(PrimarySSN);//("SSN"); // add the text
                    pdfContentByte.endText();
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    ////////////////////////// SecondaryInsurance Info///////////////////////////////////////

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(78, 211); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Decription"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(269, 212); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Ins.ID"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 214); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance No."); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(86, 183); // set x and y co-ordinates
                    pdfContentByte.showText(SecondPayerName);//("InsCompany"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410, 195); // set x and y co-ordinates
                    pdfContentByte.showText("");//(String.valueOf(MemId));//("Carrier Key"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410, 200); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance Class"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(65, 183); // set x and y co-ordinates
                    pdfContentByte.showText("");//String.valueOf(Address));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(289, 185); // set x and y co-ordinates
                    pdfContentByte.showText("");//(String.valueOf(GrpNumber));//("Plan Code"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410, 187); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance Type"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(408, 174); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance Fax"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 160); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Attention To"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(74, 159); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Phone/ext"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(85, 116); // set x and y co-ordinates
                    pdfContentByte.showText(SecondPatientRelationtoPrimary);//("Relationship"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275, 119); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Gender"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 125); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Home Phone"); // add the text
                    pdfContentByte.endText();

                    if (PatientRelationtoPrimary.toUpperCase().equals("SELF")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(83, 106); // set x and y co-ordinates
                        pdfContentByte.showText(FirstName + " " + LastName);//("Full Name"); // add the text
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(83, 106); // set x and y co-ordinates
                        pdfContentByte.showText(SubscriberName);//("Full Name"); // add the text
                        pdfContentByte.endText();
                    }

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275, 106); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(SecondDOB));//("DOB"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 109); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Work Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(83, 93); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(83, 83); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(285, 95); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Start Date"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(403, 96); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Mobile Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(285, 83); // set x and y co-ordinates
                    pdfContentByte.showText("");//("End Date"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(405, 85); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Authorization"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390, 73); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(SecondMemId));//("Policy ID"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390, 61); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(SecondGrpNumber));//("Group ID"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(53, 60); // set x and y co-ordinates
                    pdfContentByte.showText("");//("SSN"); // add the text
                    pdfContentByte.endText();

                }


            }
            pdfStamper.close();
            pdfReader.close();

            File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + MRN + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            FileInputStream fileInputStream = new FileInputStream(pdfFile);
            OutputStream responseOutputStream = response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }

        } catch (Exception ex2) {
            String str = "";
            out.println(ex2.getMessage());
            for (int j = 0; j < ex2.getStackTrace().length; ++j) {
                str = str + ex2.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }

    private void GETINPUTVictoria(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String Database, ServletContext servletContext, String UserId, int ClientId, UtilityHelper helper) {
        final String ID = request.getParameter("PatientId");
        Statement stmt = null;
        ResultSet rset = null;
        String VisitNumber = "";
        String Query = "";
        String DateTime = "";
        String ClientName = "";
        String DoctorsName = "";
        String DoctorsId = "";
        String ClientIndex = null;
        String FirstName = null;
        String LastName = null;
        String MiddleInitial = null;
        String DOB = null;
        String Age = "";
        String Gender = null;
        String MRN = null;
        String SSN = null;
        String CreatedDate = null;
        String Address = null;
        String Email = "";
        String Ethnicity = "";
        String PhNumber = "";
        String State = "";
        String City = "";
        String ZipCode = "";
        String DirectoryName = "";
        try {
            Query = " SELECT ID, ClientIndex, IFNULL(FirstName,''), IFNULL(LastName,''), IFNULL(MiddleInitial,''), IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'),''), IFNULL(Age,''), " +
                    " IFNULL(Gender,''), IFNULL(MRN,''), IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y'),DATE_FORMAT(CreatedDate,'%m/%d/%Y')),date_format(now(),'%Y%m%d%H%i%s'), " +
                    " IFNULL(DoctorsName, '-'), IFNULL(SSN,''), IFNULL(Address,''), IFNULL(Email,''),IFNULL(PhNumber,''), IFNULL(City,''), IFNULL(State,'')," +
                    " IFNULL(ZipCode,'') FROM   " + Database + ".PatientReg where ID= " + ID;
//            System.out.println("QUery 1: "+Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientIndex = rset.getString(2);
                FirstName = rset.getString(3);
                LastName = rset.getString(4);
                MiddleInitial = rset.getString(5);
                DOB = rset.getString(6);
                Age = rset.getString(7);
                Gender = rset.getString(8);
                MRN = rset.getString(9);
                CreatedDate = rset.getString(10);
                DateTime = rset.getString(11);
                DoctorsId = rset.getString(12);
                SSN = rset.getString(13);
                Address = rset.getString(14);
                Email = rset.getString(15);
//                Ethnicity = rset.getString(16);
                PhNumber = rset.getString(16);
                City = rset.getString(17);
                State = rset.getString(18);
                ZipCode = rset.getString(19);

            }
            rset.close();
            stmt.close();

        } catch (Exception ex3) {
            System.out.println("Error 1" + ex3.getMessage());
        }
        try {
            String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + DateTime + ".pdf";
            OutputStream fos = new FileOutputStream(new File(outputFilePath));
            PdfReader pdfReader = new PdfReader("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FaceSheet_Frontline.pdf");

            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);


            try {
                Query = "Select MAX(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + ID;
//                System.out.println("QUery 2: " + Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    VisitNumber = rset.getString(1).trim();
                }
                rset.close();
                stmt.close();
            } catch (Exception ex3) {
                System.out.println("Error 2" + ex3.getMessage());
            }
            VisitNumber = "VN-" + MRN + "-" + VisitNumber;


            String Race = "";

            try {
                Query = "Select IFNULL(Race,'0'), IFNULL(Ethnicity,0) from " + Database + ".PatientReg_Details where PatientRegId = " + ID;
//                System.out.println("QUery 3: " + Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    Race = rset.getString(1);
                    Ethnicity = rset.getString(2);
                }
                rset.close();
                stmt.close();
            } catch (Exception ex3) {
                System.out.println("Error 3" + ex3.getMessage());
            }

            switch (Race) {
                case "0":
                    Race = "Not Specified";
                    break;
                case "1":
                    Race = "African American";
                    break;
                case "2":
                    Race = "American Indian or Alaska Native";
                    break;
                case "3":
                    Race = "Asian";
                    break;
                case "4":
                    Race = "Native Hawaiian or Other Pacific Islander";
                    break;
                case "5":
                    Race = "White";
                    break;
                case "6":
                    Race = "Other";
                    break;
                default:
                    Race = "Not Specified";
                    break;
            }


            if (Ethnicity.equals("0")) {
                Ethnicity = "Not Specified";
            } else if (Ethnicity.equals("1")) {
                Ethnicity = "Hispanic or Latino";
            } else if (Ethnicity.equals("2")) {
                Ethnicity = "Non Hispanic or Latino";
            } else if (Ethnicity.equals("3")) {
                Ethnicity = "Others";
            } else {
                Ethnicity = "Not Specified";
            }

            String PersonName = "";
            String PersonAddress = "";
            String PersonPhoneNumber = "";
            String PersonRelationToPatient = "";
            String CityER = "";
            String StateER = "";
            String ZipCodeER = "";

//            try {
//                Query = "Select IFNULL(NextofKinName,''), IFNULL(Address,''), IFNULL(PhoneNumber,''), IFNULL(RelationToPatient,''), IFNULL(City,''), " +
//                        " IFNULL(State,''), IFNULL(ZipCode,'') from " + Database + ".EmergencyInfo where PatientRegId = " + ID;
//                System.out.println("QUery 4: "+Query);
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PersonName = rset.getString(1);
//                    PersonAddress = rset.getString(2);
//                    PersonPhoneNumber = rset.getString(3);
//                    PersonRelationToPatient = rset.getString(4);
//                    CityER = rset.getString(5);
//                    StateER = rset.getString(6);
//                    ZipCodeER = rset.getString(7);
//                }
//                rset.close();
//                stmt.close();
//            } catch (Exception e) {
//                e.getStackTrace();
//            }

            String PriInsurance = null;
            String PayerName = null;

            String MemId = null;
            String GrpNumber = null;
            String PatientRelationtoPrimary = null;

            try {
                Query = "Select  IFNULL(HIPrimaryInsurance,''), IFNULL(HISubscriberRelationtoPatient,''), IFNULL(HISubscriberGroupNo,''), IFNULL(HISubscriberPolicyNo,'')  from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PayerName = rset.getString(1);
                    PatientRelationtoPrimary = rset.getString(2);
                    GrpNumber = rset.getString(3);
                    MemId = rset.getString(4);
                }
                rset.close();
                stmt.close();
            } catch (Exception ex3) {
                System.out.println("Error 5" + ex3.getMessage());
            }


            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    if (ClientId == 27) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(236, 740); // set x and y co-ordinates
                        pdfContentByte.showText("Frontline ER Whiterock"); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(244, 725); // set x and y co-ordinates
                        pdfContentByte.showText("7331 Gaston Rd"); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(244, 710); // set x and y co-ordinates
                        pdfContentByte.showText("Dallas, TX 75214"); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(246, 695); // set x and y co-ordinates
                        pdfContentByte.showText("214-499-9555"); // add the text
                        pdfContentByte.endText();
                    } else if (ClientId == 29) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(236, 740); // set x and y co-ordinates
                        pdfContentByte.showText("Frontline ER Richmond"); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(244, 725); // set x and y co-ordinates
                        pdfContentByte.showText("7051 FM 1464 Rd"); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(244, 710); // set x and y co-ordinates
                        pdfContentByte.showText("Richmond, TX 77407"); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(246, 695); // set x and y co-ordinates
                        pdfContentByte.showText("281-766-3811"); // add the text
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(236, 740); // set x and y co-ordinates
                        pdfContentByte.showText("  "); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(244, 725); // set x and y co-ordinates
                        pdfContentByte.showText(" "); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(244, 710); // set x and y co-ordinates
                        pdfContentByte.showText(" "); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(246, 695); // set x and y co-ordinates
                        pdfContentByte.showText(" "); // add the text
                        pdfContentByte.endText();
                    }

                    /////////////////////////////////////Patient Information//////////////////////////////////////////////////
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90, 633); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(FirstName) + " " + LastName);//("Patient Name"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 620); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(SSN));//("SSN"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 607); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(Address));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 597); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(City + ", " + State + ", " + ZipCode));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 580); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(Email));//("Email"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90, 567); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Alternate Id"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 552); // set x and y co-ordinates
                    pdfContentByte.showText("");//("PCP"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 539); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(Race));//("Race"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(270, 635); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(Age));//("Age"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 635); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Home Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275, 622); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(Gender));//("Gender"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 622); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Work Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(265, 608); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(DOB));//("DOB"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(405, 608); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PhNumber));//("Mobile Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(265, 596); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(CreatedDate));//("DOS"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(385, 596); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Religion"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(265, 582); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(MRN));//("MRN"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(423, 582); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Employement Status"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(280, 569); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(VisitNumber));//("Acct No."); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(385, 569); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(Ethnicity));//("Ethnicity"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420, 554); // set x and y co-ordinates
                    pdfContentByte.showText("");//("How heard about us!"); // add the text
                    pdfContentByte.endText();
                    ///////////////////////////////////////////////////////////////////////////////////////


//////////////////////////////////////////////////Person Contact////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 505); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PersonName));//("Name"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(72, 491); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PersonAddress));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(72, 481); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(CityER + ", " + StateER + ", " + ZipCodeER));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(63, 465); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Email"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(280, 520); // set x and y co-ordinates
                    pdfContentByte.showText("");//("H. Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(270, 508); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PersonPhoneNumber));//("Mobile"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(285, 495); // set x and y co-ordinates
                    pdfContentByte.showText("");//("W. Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260, 480); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Ext"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 520); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PersonRelationToPatient));//("Relationship"); // add the text
                    pdfContentByte.endText();

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                    //////////////////////////Insurance Info///////////////////////////////////////

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(78, 411); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Decription"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(269, 412); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Ins.ID"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 414); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance No."); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(85, 398); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PayerName));//("InsCompany"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410, 398); // set x and y co-ordinates
                    pdfContentByte.showText("");//(String.valueOf(MemId));//("Carrier Key"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410, 400); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance Class"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(65, 383); // set x and y co-ordinates
                    pdfContentByte.showText("");//String.valueOf(Address));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(289, 385); // set x and y co-ordinates
                    pdfContentByte.showText("");//(String.valueOf(GrpNumber));//("Plan Code"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410, 387); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance Type"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(408, 374); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance Fax"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 360); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Attention To"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(74, 359); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Phone/ext"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(83, 322); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PatientRelationtoPrimary));//("Relationship"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275, 322); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(Gender));//("Gender"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 325); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Home Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(83, 309); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(FirstName) + " " + LastName);//("Full Name"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275, 309); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(DOB));//("DOB"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 309); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Work Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(83, 296); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(Address));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(83, 286); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(City + ", " + State + ", " + ZipCode));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(285, 295); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Start Date"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(403, 297); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PhNumber));//("Mobile Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(285, 283); // set x and y co-ordinates
                    pdfContentByte.showText("");//("End Date"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(405, 285); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Authorization"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390, 272); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(MemId));//("Policy ID"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390, 259); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(GrpNumber));//("Group ID"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(53, 254); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(SSN));//("SSN"); // add the text
                    pdfContentByte.endText();
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                }


            }
            pdfStamper.close();

            File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + MRN + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            FileInputStream fileInputStream = new FileInputStream(pdfFile);
            OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }

        } catch (Exception ex2) {
            String str = "";
            out.println(ex2.getMessage());
            for (int j = 0; j < ex2.getStackTrace().length; ++j) {
                str = str + ex2.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }

    private void GETINPUTFrontline(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String Database, ServletContext servletContext, String UserId, int ClientId, UtilityHelper helper) {
        final String ID = request.getParameter("PatientId");
        Statement stmt = null;
        ResultSet rset = null;
        String VisitNumber = "";
        String Query = "";
        String DateTime = "";
        String ClientName = "";
        String ClientAddress = "";
        String ClientPhone = "";
        String DoctorsName = "";
        String DoctorsId = "";
        String ClientIndex = null;
        String FirstName = null;
        String LastName = null;
        String MiddleInitial = null;
        String DOB = null;
        String Age = "";
        String Gender = null;
        String MRN = null;
        String SSN = null;
        String CreatedDate = null;
        String Address = null;
        String StreetAddress2 = null;
        String Email = "";
        String Ethnicity = "";
        String PhNumber = "";
        String State = "";
        String City = "";
        String ZipCode = "";
        String DOBForAge = "";
        String DirectoryName = "";
        String SecondInsurance = "";
        String SecondPayerName = "";
        String SecondMemId = "";
        String SecondGrpNumber = "";
        String SecondDOB = "";
        String SubscriberName = "";
        String SecondPatientRelationtoPrimary = "";
        int SelfPayChk = 0;
        try {

            Query = "Select IFNULL(FullName,''), IFNULL(Address,''), IFNULL(Phone,'') from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientName = rset.getString(1);
                ClientAddress = rset.getString(2);
                ClientPhone = rset.getString(3);


            }
            rset.close();
            stmt.close();

            Query = " SELECT ID, ClientIndex, IFNULL(FirstName,''), IFNULL(LastName,''), IFNULL(MiddleInitial,''), IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'),''), IFNULL(Age,''), " +
                    " IFNULL(Gender,''), IFNULL(MRN,''), IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y'),DATE_FORMAT(CreatedDate,'%m/%d/%Y')),date_format(now(),'%Y%m%d%H%i%s'), " +
                    " IFNULL(DoctorsName, ''), IFNULL(SSN,''), IFNULL(Address,''), IFNULL(Email,''), IFNULL(Ethnicity,''),IFNULL(PhNumber,''), IFNULL(City,''), IFNULL(State,'')," +
                    " IFNULL(ZipCode,''), IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'),''), IFNULL(SelfPayChk,0), IFNULL(StreetAddress2,'') " +
                    "FROM   " + Database + ".PatientReg where ID= " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientIndex = rset.getString(2);
                FirstName = rset.getString(3);
                LastName = rset.getString(4);
                MiddleInitial = rset.getString(5);
                DOB = rset.getString(6);
                Age = rset.getString(7);
                Gender = rset.getString(8);
                MRN = rset.getString(9);
                CreatedDate = rset.getString(10);
                DateTime = rset.getString(11);
                DoctorsId = rset.getString(12);
                SSN = rset.getString(13);
                Address = rset.getString(14);
                Email = rset.getString(15);
                Ethnicity = rset.getString(16);
                PhNumber = rset.getString(17);
                City = rset.getString(18);
                State = rset.getString(19);
                ZipCode = rset.getString(20);
                DOBForAge = rset.getString(21);
                SelfPayChk = rset.getInt(22);
                StreetAddress2 = rset.getString(23);

            }
            rset.close();
            stmt.close();

            if (!DOB.equals("")) {
                Age = String.valueOf(helper.getAge(LocalDate.parse(DOBForAge)));
            } else {
                Age = "0";
            }

            if (!DoctorsId.equals("")) {
                Query = "Select IFNULL(DoctorsFirstName,''), IFNULL(DoctorsLastName,'') from " + Database + ".DoctorsList where Id = " + DoctorsId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    DoctorsName = "Dr. " + rset.getString(1) + " " + rset.getString(2);
                }
                rset.close();
                stmt.close();
            }


        } catch (Exception ex3) {
        }
        try {
            String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Labels/" + DirectoryName + "/" + MRN + LastName + ID + "_" + DateTime + ".pdf";
            OutputStream fos = new FileOutputStream(new File(outputFilePath));
            PdfReader pdfReader = new PdfReader("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/FaceSheet_Frontline.pdf");

            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);


            Query = "Select MAX(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + ID;
//            System.out.println("QUery 2: "+Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                VisitNumber = rset.getString(1).trim();
            }
            rset.close();
            stmt.close();
            VisitNumber = "VN-" + MRN + "-" + VisitNumber;


            String Race = "";

            Query = "Select IFNULL(Race,'0') from " + Database + ".PatientReg_Details where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Race = rset.getString(1);
            }
            rset.close();
            stmt.close();

            switch (Race) {
                case "1":
                    Race = "African American";
                    break;
                case "2":
                    Race = "American Indian or Alaska Native";
                    break;
                case "3":
                    Race = "Asian";
                    break;
                case "4":
                    Race = "Native Hawaiian or Other Pacific Islander";
                    break;
                case "5":
                    Race = "White";
                    break;
                case "6":
                    Race = "Other";
                    break;
                default:
                    Race = "Not Specified";
                    break;
            }


            switch (Ethnicity) {
                case "1":
                    Ethnicity = "Hispanic or Latino";
                    break;
                case "2":
                    Ethnicity = "Non Hispanic or Latino";
                    break;
                case "3":
                    Ethnicity = "Others";
                    break;
                default:
                    Ethnicity = "Not Specified";
                    break;
            }

            String PersonName = null;
            String PersonAddress = null;
            String PersonPhoneNumber = null;
            String PersonRelationToPatient = null;
            String CityER = "";
            String StateER = "";
            String ZipCodeER = "";

            try {
                Query = "Select IFNULL(NextofKinName,''), IFNULL(Address,''), IFNULL(PhoneNumber,''), IFNULL(RelationToPatient,''), IFNULL(City,''), " +
                        " IFNULL(State,''), IFNULL(ZipCode,'') from " + Database + ".EmergencyInfo where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PersonName = rset.getString(1);
                    PersonAddress = rset.getString(2);
                    PersonPhoneNumber = rset.getString(3);
                    PersonRelationToPatient = rset.getString(4);
                    CityER = rset.getString(5);
                    StateER = rset.getString(6);
                    ZipCodeER = rset.getString(7);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                e.getStackTrace();
            }

            String PriInsurance = "";
            String PayerName = "";

            String MemId = "";
            String GrpNumber = "";
            String PatientRelationtoPrimary = "";
            String PriInsurerName = "";

            Query = "Select IFNULL(PriInsuranceName,''), IFNULL(MemId,''), IFNULL(GrpNumber,'') ," +
                    "IFNULL(PatientRelationtoPrimary,''), IFNULL(PriInsurerName,'')  " +
                    "from " + Database + ".InsuranceInfo where PatientRegId = " + ID;
            if (SelfPayChk == 1) {
                try {
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PriInsurance = rset.getString(1);
                        MemId = rset.getString(2);
                        GrpNumber = rset.getString(3);
                        PatientRelationtoPrimary = rset.getString(4);
                        PriInsurerName = rset.getString(5);
                    }
                    rset.close();
                    stmt.close();
                } catch (Exception e) {
                    e.getStackTrace();
                }


                try {
                    if (!PriInsurance.equals("")) {
                        Query = "Select IFNULL(PayerName,'') from " + Database + ".ProfessionalPayers where id = " + PriInsurance;
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next()) {
                            PayerName = rset.getString(1);
                        }
                        rset.close();
                        stmt.close();
                    }
                } catch (Exception e) {
                    e.getStackTrace();
                }


                Query = "Select IFNULL(SecondryInsurance,''), IFNULL(MemberID_2,''), " +
                        "IFNULL(GroupNumber_2,'') ," +
                        "CASE WHEN PatientRelationshiptoSecondry = -1 THEN '' ELSE PatientRelationshiptoSecondry END ," +
                        "IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'00/00/0000'),IFNULL(SubscriberName,'')  " +
                        "from " + Database + ".InsuranceInfo where PatientRegId = " + ID;
//                CONCAT(SubscriberFirstName,' ',SubscriberLastName)
                try {
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        SecondInsurance = rset.getString(1);
                        SecondMemId = rset.getString(2);
                        SecondGrpNumber = rset.getString(3);
                        SecondPatientRelationtoPrimary = rset.getString(4);
                        SecondDOB = rset.getString(5);
                        SubscriberName = rset.getString(6);
                    }
                    rset.close();
                    stmt.close();
                } catch (Exception e) {
                    e.getStackTrace();
                }

                try {
                    if (!SecondInsurance.equals("")) {
                        Query = "SELECT IFNULL(PayerName,'') FROM " + Database + ".ProfessionalPayers " +
                                " WHERE id = " + SecondInsurance;
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next()) {
                            SecondPayerName = rset.getString(1);
                        }
                        rset.close();
                        stmt.close();
                    }
                } catch (Exception e) {
                    e.getStackTrace();
                }

                if (PayerName.length() > 30)
                    PayerName = PayerName.substring(0, 30);
                else if (PayerName.length() > 20)
                    PayerName = PayerName.substring(0, 20);
                else
                    PayerName = PayerName.substring(0, PayerName.length());


                if (SecondPayerName.length() > 30)
                    SecondPayerName = SecondPayerName.substring(0, 30);
                else if (SecondPayerName.length() > 20)
                    SecondPayerName = SecondPayerName.substring(0, 20);
                else
                    SecondPayerName = SecondPayerName.substring(0, SecondPayerName.length());
            }

            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                    if (ClientId == 27) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(236, 740); // set x and y co-ordinates
                        pdfContentByte.showText("Frontline ER Whiterock"); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(244, 725); // set x and y co-ordinates
                        pdfContentByte.showText("7331 Gaston Rd"); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(244, 710); // set x and y co-ordinates
                        pdfContentByte.showText("Dallas, TX 75214"); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(246, 695); // set x and y co-ordinates
                        pdfContentByte.showText("214-499-9555"); // add the text
                        pdfContentByte.endText();
                    } else if (ClientId == 29) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(236, 740); // set x and y co-ordinates
                        pdfContentByte.showText("Frontline ER Richmond"); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(244, 725); // set x and y co-ordinates
                        pdfContentByte.showText("7051 FM 1464 Rd"); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(244, 710); // set x and y co-ordinates
                        pdfContentByte.showText("Richmond, TX 77407"); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(246, 695); // set x and y co-ordinates
                        pdfContentByte.showText("281-766-3811"); // add the text
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 12); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.showTextAligned(Element.ALIGN_CENTER, ClientName.toUpperCase(), 280, 740, 0);// add the text//BILLING Provider  facility TAX ID
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 12); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.showTextAligned(Element.ALIGN_CENTER, ClientAddress.toUpperCase(), 280, 725, 0);// add the text//BILLING Provider  facility TAX ID
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 12); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.showTextAligned(Element.ALIGN_CENTER, ClientPhone, 280, 710, 0);// add the text//BILLING Provider  facility TAX ID
                        pdfContentByte.endText();
                    }

                    /////////////////////////////////////Patient Information//////////////////////////////////////////////////
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90, 633); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(FirstName) + " " + LastName);//("Patient Name"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(50, 620); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(SSN));//("SSN"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 607); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(StreetAddress2 + " " + Address));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(70, 597); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(City + ", " + State + ", " + ZipCode));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 580); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(Email));//("Email"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(90, 567); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Alternate Id"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 552); // set x and y co-ordinates
                    pdfContentByte.showText("");//("PCP"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 539); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(Race));//("Race"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(270, 635); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(Age));//("Age"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 635); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Home Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275, 622); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(Gender));//("Gender"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 622); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Work Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(265, 608); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(DOB));//("DOB"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(405, 608); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PhNumber));//("Mobile Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(265, 596); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(CreatedDate));//("DOS"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(385, 596); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Religion"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(265, 582); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(MRN));//("MRN"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(423, 582); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Employement Status"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(280, 569); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(VisitNumber));//("Acct No."); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(385, 569); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(Ethnicity));//("Ethnicity"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(420, 554); // set x and y co-ordinates
                    pdfContentByte.showText("");//("How heard about us!"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(345, 540); // set x and y co-ordinates
                    pdfContentByte.showText("ER Physician: " + DoctorsName);//(ER Physician NAME
                    pdfContentByte.endText();
                    ///////////////////////////////////////////////////////////////////////////////////////


//////////////////////////////////////////////////Person Contact////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(60, 505); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PersonName));//("Name"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(72, 491); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PersonAddress));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(72, 481); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(CityER + ", " + StateER + ", " + ZipCodeER));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(63, 465); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Email"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(280, 520); // set x and y co-ordinates
                    pdfContentByte.showText("");//("H. Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(270, 508); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PersonPhoneNumber));//("Mobile"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(285, 495); // set x and y co-ordinates
                    pdfContentByte.showText("");//("W. Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260, 480); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Ext"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 520); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PersonRelationToPatient));//("Relationship"); // add the text
                    pdfContentByte.endText();

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                    //////////////////////////Insurance Info///////////////////////////////////////

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(78, 411); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Decription"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(269, 412); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Ins.ID"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 414); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance No."); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(85, 398); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PayerName));//("InsCompany"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410, 398); // set x and y co-ordinates
                    pdfContentByte.showText("");//(String.valueOf(MemId));//("Carrier Key"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410, 400); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance Class"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(65, 383); // set x and y co-ordinates
                    pdfContentByte.showText("");//String.valueOf(Address));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(289, 385); // set x and y co-ordinates
                    pdfContentByte.showText("");//(String.valueOf(GrpNumber));//("Plan Code"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(410, 387); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance Type"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(408, 374); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Insurance Fax"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 360); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Attention To"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(74, 359); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Phone/ext"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(83, 322); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PatientRelationtoPrimary));//("Relationship"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275, 322); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(Gender));//("Gender"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 325); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Home Phone"); // add the text
                    pdfContentByte.endText();

                    if (PatientRelationtoPrimary.toUpperCase().equals("SELF")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(83, 309); // set x and y co-ordinates
                        pdfContentByte.showText(String.valueOf(FirstName + " " + LastName));//("Full Name"); // add the text
                        pdfContentByte.endText();
                    } else {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(83, 309); // set x and y co-ordinates
                        pdfContentByte.showText(String.valueOf(PriInsurerName));//("Full Name"); // add the text
                        pdfContentByte.endText();
                    }

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(275, 309); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(DOB));//("DOB"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(400, 309); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Work Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(83, 296); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(StreetAddress2 + " " + Address));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(83, 286); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(City + ", " + State + ", " + ZipCode));//("Address"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(285, 295); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Start Date"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(403, 297); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(PhNumber));//("Mobile Phone"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(285, 283); // set x and y co-ordinates
                    pdfContentByte.showText("");//("End Date"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(405, 285); // set x and y co-ordinates
                    pdfContentByte.showText("");//("Authorization"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390, 272); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(MemId));//("Policy ID"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(390, 259); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(GrpNumber));//("Group ID"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(53, 254); // set x and y co-ordinates
                    pdfContentByte.showText(String.valueOf(SSN));//("SSN"); // add the text
                    pdfContentByte.endText();
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////// Secondary insurance /////////////////////////////////////////////
                    //if (ClientId == 27) {
                        if (!SecondInsurance.equals("")) {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(78, 211); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Decription"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(269, 212); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Ins.ID"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(400, 214); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Insurance No."); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(86, 183); // set x and y co-ordinates
                            pdfContentByte.showText(SecondPayerName);//("InsCompany"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(410, 195); // set x and y co-ordinates
                            pdfContentByte.showText("");//(String.valueOf(MemId));//("Carrier Key"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(410, 200); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Insurance Class"); // add the text
                            pdfContentByte.endText();


                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(65, 183); // set x and y co-ordinates
                            pdfContentByte.showText("");//String.valueOf(Address));//("Address"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(289, 185); // set x and y co-ordinates
                            pdfContentByte.showText("");//(String.valueOf(GrpNumber));//("Plan Code"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(410, 187); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Insurance Type"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(408, 174); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Insurance Fax"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(400, 160); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Attention To"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(74, 159); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Phone/ext"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(85, 116); // set x and y co-ordinates
                            pdfContentByte.showText(SecondPatientRelationtoPrimary);//("Relationship"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(275, 119); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Gender"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(400, 125); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Home Phone"); // add the text
                            pdfContentByte.endText();

                            if (PatientRelationtoPrimary.toUpperCase().equals("SELF")) {
                                pdfContentByte.beginText();
                                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                                pdfContentByte.setColorFill(BaseColor.BLACK);
                                pdfContentByte.setTextMatrix(83, 106); // set x and y co-ordinates
                                pdfContentByte.showText(FirstName + " " + LastName);//("Full Name"); // add the text
                                pdfContentByte.endText();
                            } else {
                                pdfContentByte.beginText();
                                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                                pdfContentByte.setColorFill(BaseColor.BLACK);
                                pdfContentByte.setTextMatrix(83, 106); // set x and y co-ordinates
                                pdfContentByte.showText(SubscriberName);//("Full Name"); // add the text
                                pdfContentByte.endText();
                            }

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(275, 106); // set x and y co-ordinates
                            pdfContentByte.showText(String.valueOf(SecondDOB));//("DOB"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(400, 109); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Work Phone"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(83, 93); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Address"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(83, 83); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Address"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(285, 95); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Start Date"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(403, 96); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Mobile Phone"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(285, 83); // set x and y co-ordinates
                            pdfContentByte.showText("");//("End Date"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(405, 85); // set x and y co-ordinates
                            pdfContentByte.showText("");//("Authorization"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(390, 73); // set x and y co-ordinates
                            pdfContentByte.showText(String.valueOf(SecondMemId));//("Policy ID"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(390, 61); // set x and y co-ordinates
                            pdfContentByte.showText(String.valueOf(SecondGrpNumber));//("Group ID"); // add the text
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(53, 60); // set x and y co-ordinates
                            pdfContentByte.showText("");//("SSN"); // add the text
                            pdfContentByte.endText();

                        }
                    //}

                }


            }
            pdfStamper.close();

            File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + MRN + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            FileInputStream fileInputStream = new FileInputStream(pdfFile);
            OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }

        } catch (Exception ex2) {
            String str = "";
            out.println(ex2.getMessage());
            for (int j = 0; j < ex2.getStackTrace().length; ++j) {
                str = str + ex2.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }


    }

}
