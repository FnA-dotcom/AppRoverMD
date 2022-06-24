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
import javax.servlet.http.Cookie;
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

public class QuickReg_abid_11_09_2020 extends HttpServlet {
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
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        int ClientId = 0;
        String Database = "";
        String Query = "";
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        try {
            final Cookie[] cookies = request.getCookies();
            UserId = (Zone = (Passwd = ""));
            final int checkCookie = 0;
            for (int coky = 0; coky < cookies.length; ++coky) {
                final String cName = cookies[coky].getName();
                final String cValue = cookies[coky].getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
                }
            }
            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientId = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            Query = "Select dbname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
        if (ActionID.equals("GetValues")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "QuickReg_abid_11_09_2020 Option", "Click on Quick Reg Option", ClientId);
            GetValues(request, out, conn, context, UserId);
        } else if (ActionID.equals("SaveData")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "QuickReg_abid_11_09_2020 Option", "Save the data From Quick Reg Option", ClientId);
            SaveData(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("Victoria_2")) {
            supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Victoria_2 Data Save", "Save the data From Quick Reg Option VICTORIA", ClientId);
            Victoria_2(request, out, conn, context, UserId, response);
        }
        try {
            conn.close();
        } catch (Exception ex) {
        }
        out.flush();
        out.close();
    }

    void GetValues(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId) {
        try {
            final SupportiveMethods suppMethods = new SupportiveMethods();
            StringBuffer LeftSideBarMenu = new StringBuffer();
            StringBuffer Header = new StringBuffer();
            StringBuffer Footer = new StringBuffer();
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Date = "";
            int ClientIndex = 0;
            String Database = "";
            final StringBuffer ProfessionalPayersList = new StringBuffer();
            final String ClientId = request.getParameter("ClientId").trim();
            final StringBuffer DoctorList = new StringBuffer();
            Query = "Select Date_format(now(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();
            Query = "Select Id,dbname from oe.clients where ltrim(rtrim(UPPER(name))) = ltrim(rtrim(UPPER('" + ClientId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientIndex = rset.getInt(1);
                Database = rset.getString(2);
            }
            rset.close();
            stmt.close();
            Query = "Select Id, PayerId, PayerName from " + Database + ".ProfessionalPayers where PayerName like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ProfessionalPayersList.append("<option class=Inner value='-1'>Select Insurance</option>");
            while (rset.next()) {
                ProfessionalPayersList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();
            Query = "Select Id, PayerId, PayerName from " + Database + ".ProfessionalPayers where PayerName not like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ProfessionalPayersList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();
            Query = "Select Id, CONCAT(DoctorsLastName, ' , ', DoctorsFirstName) from " + Database + ".DoctorsList";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                DoctorList.append("<option value=" + rset.getString(1) + ">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientIndex);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientIndex);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientIndex);
            final Parsehtm Parser = new Parsehtm(request);
            if (ClientIndex == 8) {
                Parser.SetField("Date", String.valueOf(Date));
                Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
                Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.SetField("DoctorList", String.valueOf(DoctorList));
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/QuickPatientRegFormOrange.html");
            } else if (ClientIndex == 9) {
                Parser.SetField("Date", String.valueOf(Date));
                Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
                Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.SetField("DoctorList", String.valueOf(DoctorList));
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/QuickPatientRegFormVictoria.html");
            } else if (ClientIndex == 10) {
                Parser.SetField("Date", String.valueOf(Date));
                Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
                Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.SetField("DoctorList", String.valueOf(DoctorList));
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/QuickPatientRegFormOddasa.html");
            } else if (ClientIndex == 12) {
                Parser.SetField("Date", String.valueOf(Date));
                Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
                Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.SetField("DoctorList", String.valueOf(DoctorList));
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/QuickPatientRegFormSAustin.html");
            } else if (ClientIndex == 15) {
                Parser.SetField("Date", String.valueOf(Date));
                Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
                Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.SetField("DoctorList", String.valueOf(DoctorList));
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/QuickPatientRegFormSublime.html");
            }
        } catch (Exception ex) {
        }
    }

    void SaveData(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Database = "";
        int ClientIndex = 0;
        int PatientRegId = 0;
        final String ClientId = request.getParameter("ClientId").trim();
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
        String City = "";
        String State = "";
        String Country = "";
        String ZipCode = "";
        String SSN = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        int WorkersCompPolicy = 0;
        int MotorVehAccident = 0;
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
        int LeaveMessageER = 0;
        String AddressER = "";
        String CityER = "";
        String StateER = "";
        String CountryER = "";
        String ZipCodeER = "";
        String DateConcent = "";
        String WitnessConcent = "";
        String PatientBehalfConcent = "";
        String RelativeSignConcent = "";
        String DateConcent2 = "";
        String WitnessConcent2 = "";
        String PatientSignConcent = "";
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
        int VerifyChkBox = 0;
        String PatientName = "";
        int DoctorsId = 0;
        int MRN = 0;
        String ExtendedMRN = "0";

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
                }
                if (request.getParameter("Age").trim() == null) {
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
                    Country = "Mr";
                } else {
                    Country = request.getParameter("Country").trim();
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
                if (request.getParameter("ReasonVisit") == null) {
                    ReasonVisit = "";
                } else {
                    ReasonVisit = request.getParameter("ReasonVisit").trim();
                }
                if (request.getParameter("DoctorName") == null) {
                    DoctorsId = 0;
                } else {
                    DoctorsId = Integer.parseInt(request.getParameter("DoctorName").trim());
                }
                if (request.getParameter("SelfPayChk") == null) {
                    SelfPayChk = 0;
                } else {
                    SelfPayChk = 1;
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
                    if (request.getParameter("AddressIfDifferent") == null) {
                        AddressIfDifferent = "";
                    } else {
                        AddressIfDifferent = request.getParameter("AddressIfDifferent").trim();
                    }
                    if (request.getParameter("PrimaryDOB") == null) {
                        PrimaryDOB = "0000-00-00";
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
                    if (request.getParameter("SubscriberDOB") == null) {
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
                if (request.getParameter("VerifyChkBox") == null) {
                    VerifyChkBox = 0;
                } else {
                    VerifyChkBox = 1;
                }
                if (request.getParameter("PatientSignConcent") == null) {
                    PatientSignConcent = "";
                } else {
                    PatientSignConcent = request.getParameter("PatientSignConcent").trim();
                }
                if (request.getParameter("DateConcent") == null) {
                    DateConcent = "0000-00-00";
                } else {
                    DateConcent = request.getParameter("DateConcent").trim();
                }
                if (request.getParameter("WitnessConcent") == null) {
                    WitnessConcent = "";
                } else {
                    WitnessConcent = request.getParameter("WitnessConcent").trim();
                }
                if (request.getParameter("PatientBehalfConcent") == null) {
                    PatientBehalfConcent = "";
                } else {
                    PatientBehalfConcent = request.getParameter("PatientBehalfConcent").trim();
                }
                if (request.getParameter("RelativeSignConcent") == null) {
                    RelativeSignConcent = "";
                } else {
                    RelativeSignConcent = request.getParameter("RelativeSignConcent").trim();
                }
                if (request.getParameter("DateConcent2") == null) {
                    DateConcent2 = "0000-00-00";
                } else {
                    DateConcent2 = request.getParameter("DateConcent2").trim();
                }
                if (request.getParameter("WitnessConcent2") == null) {
                    WitnessConcent2 = "";
                } else {
                    WitnessConcent2 = request.getParameter("WitnessConcent2").trim();
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
                    if (request.getParameter("School_text") == null) {
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
                    if (request.getParameter("Magazine_text") == null) {
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
                    if (request.getParameter("Newspaper_text") == null) {
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
                    if (request.getParameter("FamilyFriend_text") == null) {
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
                    if (request.getParameter("UrgentCare_text") == null) {
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
                    if (request.getParameter("CommunityEvent_text") == null) {
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
                    if (request.getParameter("Work_text").trim() == null) {
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
                    if (request.getParameter("Physician_text") == null) {
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
                    if (request.getParameter("Other_text") == null) {
                        Other_text = "";
                    } else {
                        Other_text = request.getParameter("Other_text").trim();
                    }
                }
            } catch (Exception e) {
                out.println(e.getMessage());
                String str = "";
                for (int i = 0; i < e.getStackTrace().length; ++i) {
                    str = str + e.getStackTrace()[i] + "<br>";
                }
                out.println(str);
            }
            try {
                Query = "Select Id, dbname from oe.clients where ltrim(rtrim(UPPER(name))) =  ltrim(rtrim(UPPER('" + ClientId + "')))";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClientIndex = rset.getInt(1);
                    Database = rset.getString(2);
                }
                rset.close();
                stmt.close();
                Query = "Select MAX(MRN) + 1  from " + Database + ".PatientReg ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    MRN = rset.getInt(1);
                }
                rset.close();
                stmt.close();
                if (MRN == 0) {
                    MRN = 310001;
                }
                out.println(MRN);
                if (ClientIndex == 9) {
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
                }
                if (ClientIndex == 8) {
                    ExtendedMRN = "1008" + MRN;
                } else if (ClientIndex == 9) {
                    ExtendedMRN = "1009" + MRN;
                } else if (ClientIndex == 10) {
                    ExtendedMRN = "1010" + MRN;
                } else if (ClientIndex == 11) {
                    ExtendedMRN = "1011" + MRN;
                } else if (ClientIndex == 12) {
                    ExtendedMRN = "1012" + MRN;
                }
            } catch (Exception e) {
                out.println("Error 1:" + e.getMessage() + Query);
            }

            try {
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".PatientReg (ClientIndex,FirstName,LastName ,MiddleInitial,DOB,Age,Gender ,Email,PhNumber ,Address,City ,State,Country,ZipCode,SSN,Occupation ,Employer ,EmpContact,PriCarePhy,ReasonVisit,SelfPayChk,CreatedDate,Title, MaritalStatus,CreatedBy, MRN, Status, DoctorsName, DateofService, ExtendedMRN) \nVALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,0,?, now(),?) ");
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
                MainReceipt.setInt(26, DoctorsId);
                MainReceipt.setString(27, ExtendedMRN);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 2- Insertion PatientReg Table :" + e.getMessage());
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
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".PatientVisit(MRN,PatientRegId,ReasonVisit,VisitNumber,DoctorId,DateofService,CreatedDate,CreatedBy) \nVALUES (?,?,?,1,NULL,now(),now(),?) ");
                MainReceipt.setInt(1, MRN);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, ReasonVisit);
                MainReceipt.setString(4, "Out Patient");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 3.1 Insertion in table PatientVisit- :" + e.getMessage());
            }
            try {
                if (SelfPayChk == 1) {
                    final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".InsuranceInfo(PatientRegId,WorkersCompPolicy,MotorVehAccident,PriInsurance,MemId,GrpNumber,PriInsuranceName,AddressIfDifferent,PrimaryDOB,PrimarySSN,PatientRelationtoPrimary,PrimaryOccupation,PrimaryEmployer,EmployerAddress,EmployerPhone,SecondryInsurance,SubscriberName,SubscriberDOB,MemberID_2,GroupNumber_2,PatientRelationshiptoSecondry,CreatedDate) \nVALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
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
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                }
            } catch (Exception e) {
                out.println("Error 4- Insertion InsuranceInfo Table :" + e.getMessage());
                return;
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".EmergencyInfo (PatientRegId,NextofKinName,RelationToPatient,PhoneNumber,LeaveMessage,Address,City,State,Country,ZipCode,CreatedDate) \nVALUES (?,?,?,?,?,?,?,?,?,?,now()) ");
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
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 5- Insertion EmergencyInfo Table :" + e.getMessage());
                return;
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".ConcentToTreatmentInfo (PatientRegId,PatientSign,Date,Witness,PatientBehalfSign,RelativeSign,Date2,Witness2,CreatedDate) \nVALUES (?,?,?,?,?,?,?,?,now()) ");
                MainReceipt.setInt(1, PatientRegId);
                MainReceipt.setString(2, PatientSignConcent);
                MainReceipt.setString(3, DateConcent);
                MainReceipt.setString(4, WitnessConcent);
                MainReceipt.setString(5, PatientBehalfConcent);
                MainReceipt.setString(6, RelativeSignConcent);
                MainReceipt.setString(7, DateConcent2);
                MainReceipt.setString(8, WitnessConcent2);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 6- Insertion ConcentToTreatmentInfo Table :" + e.getMessage());
                return;
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".RandomCheckInfo (PatientRegId,ReturnPatient,Google,MapSearch,Billboard,OnlineReview,TV,Website,BuildingSignDriveBy,Facebook,School,School_text,Twitter,Magazine,Magazine_text,Newspaper,Newspaper_text,FamilyFriend,FamilyFriend_text,UrgentCare,UrgentCare_text,CommunityEvent,CommunityEvent_text,Work_text,Physician_text,Other_text,CreatedDate) \nVALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
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
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 7- Insertion RandomCheckInfo Table :" + e.getMessage());
                return;
            }
            Query = "Select CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName) from " + Database + ".PatientReg where ID = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientName = rset.getString(1);
            }
            rset.close();
            stmt.close();
            String Date = "";
            Query = "Select Date_format(now(),'%Y-%m-%d %T')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();
            String Message = "";
            if (ClientIndex == 9) {
                String InsertCOVIDRegReply = "0";
                ReasonVisit = ReasonVisit.replaceAll(" ", "");
                if (ReasonVisit.toUpperCase().equals("COVIDTESTING")) {
                    InsertCOVIDRegReply = this.InsertCOVIDReg(request, response, out, conn, String.valueOf(PatientRegId));
                    if (Integer.parseInt(InsertCOVIDRegReply) > 0) {
                        Message = "COVID Form Also Registered Successfully.";
                    } else {
                        Message = "COVID Form Not Registered. ";
                    }
                }
            }
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Thank You " + String.valueOf(PatientName) + " We Have Registered You Successfully. Please Wait for Further Processing. " + Message + " <br>DATED: " + Date);
            Parser.SetField("FormName", String.valueOf("QuickReg_abid_11_09_2020"));
            Parser.SetField("ActionID", String.valueOf("GetValues"));
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/orange_2/Exception/Message.html");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    void Victoria_2(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, HttpServletResponse response) {
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
//                DOB =  DOB.substring(6,10) + "-" + DOB.substring(0,2) + "-" + DOB.substring(3,5);
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
                    WCPDateofInjury = WCPDateofInjury.substring(6, 10) + "-" + WCPDateofInjury.substring(0, 2) + "-" + WCPDateofInjury.substring(3, 5);
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
                        AIIDateofAccident = AIIDateofAccident.substring(6, 10) + "-" + AIIDateofAccident.substring(0, 2) + "-" + AIIDateofAccident.substring(3, 5);
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
                    HISubscriberDOB = HISubscriberDOB.substring(6, 10) + "-" + HISubscriberDOB.substring(0, 2) + "-" + HISubscriberDOB.substring(3, 5);
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
                    SecondHealthInsuranceChk = "1";
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
                        SHISubscriberDOB = SHISubscriberDOB.substring(6, 10) + "-" + SHISubscriberDOB.substring(0, 2) + "-" + SHISubscriberDOB.substring(3, 5);
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

//                Query = "SELECT MAX(MRN) + 1  FROM " + Database + ".PatientReg ";
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
                    MRN = MRN + 1;
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
                if (Email.equals(ConfirmEmail)) {
                    Email = ConfirmEmail;
                } else {
                    out.println("<!DOCTYPE html><html><body><p style=\"color:black;\">Please Put Email and Confirm Email Correctly and then Submit</p>");
                    //  out.println("<br>Request has been send to ERM , Find Patient MRN "+MRN);
                    out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
                    return;
                }
                final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".PatientReg (ClientIndex,FirstName,LastName ," +
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
                MainReceipt.setInt(25, MRN);
                MainReceipt.setString(26, ExtendedMRN);
                MainReceipt.setString(27, County);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 2- Insertion PatientReg Table :" + e.getMessage());
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
                out.println("Error 3.1 Insertion in table PatientVisit- :" + e.getMessage());
            }

            try {
                final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".PatientReg_Details (PatientRegId,Ethnicity," +
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
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 4- Insertion PatientReg_Details Table :" + e.getMessage());
                return;
            }

            if (WorkersCompPolicyChk.equals("1")) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".Patient_WorkCompPolicy (PatientRegId,WCPDateofInjury," +
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
                    MainReceipt.setString(31, WCPAdjudicatorAreaCode + WCPAdjudicatorPhoneNumber);
                    MainReceipt.setString(32, WCPAdjudicatorFaxAreaCode + WCPAdjudicatorFaxPhoneNumber);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                } catch (Exception e) {
                    out.println("Error 5- Insertion Patient_WorkCompPolicy Table :" + e.getMessage());
                    return;
                }
            }
            if (MotorVehicleAccidentChk.equals("1")) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".Patient_AutoInsuranceInfo (PatientRegId," +
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
                    MainReceipt.setString(17, AIICarrierResponsiblePartyAddress + " " + AIICarrierResponsiblePartyAddress2);
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

                } catch (Exception e) {
                    out.println("Error 6- Insertion Patient_AutoInsuranceInfo Table :" + e.getMessage());
                    return;
                }
            }

            if (HealthInsuranceChk.equals("1")) {
                try {
                    final PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".Patient_HealthInsuranceInfo (PatientRegId," +
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

                } catch (Exception e) {
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
            System.out.println(ReasonVisit + " Before");
            if (ReasonVisit != null) {
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
            Parser.SetField("FormName", String.valueOf("QuickReg_abid_11_09_2020"));
            Parser.SetField("ActionID", String.valueOf("ActionID=GetValues&ClientId=Victoria"));
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/orange_2/Exception/Message.html");
        } catch (Exception ex) {
            out.println(ex.getMessage());
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
        } catch (Exception e) {
            out.println("Error in Getting Data from PatientReg table" + e.getMessage());
        }
        if (Gender.equals("male")) {
            Gender = "M";
        } else {
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
            Request = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString((Object) responseJSON);

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
        } catch (Exception e) {
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
