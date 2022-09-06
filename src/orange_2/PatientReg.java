package orange_2;


import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PatientReg extends HttpServlet
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
     String UserId = "";
     final String ActionID = request.getParameter("ActionID").trim();
     response.setContentType("text/html");
     final PrintWriter out = new PrintWriter((OutputStream)response.getOutputStream());
     final Services supp = new Services();
     ServletContext context = null;
     context = this.getServletContext();
     conn = Services.getMysqlConn(context);
     String Database="";
     String Query = "";
     ResultSet rset = null;
     Statement stmt = null;
     int ClientId=0;
     UserId="";
     UserId = Services.GetCookie("UserId", request);
     try {
    	
    	 Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('"+UserId+"')))";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         while (rset.next()) {
           ClientId = rset.getInt(1);
         }
         rset.close();
         stmt.close();

        Query = "Select dbname from oe.clients where Id = "+ClientId;
        stmt = conn.createStatement();
        rset = stmt.executeQuery(Query);
        while (rset.next()) {
            Database = rset.getString(1);
        }
        rset.close();
        stmt.close();
//         if(ClientId == 8){
//           Database = "oe_2";
//         }else if(ClientId == 9){
//           Database = "victoria";
//         }else if(ClientId == 10){
//           Database = "oddasa";
//         }
     }
     catch(Exception ee) {}
     
     context = this.getServletContext();
     if (ActionID.equals("GetValues")) {
         this.GetValues(request, out, conn, context,Database);
     }
     else if (ActionID.equals("SaveData")) {
         this.SaveData(request, out, conn, context,Database);
     }
     else if (ActionID.equals("EditValues")) {
         supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Edit Values from Patient Reg", "Click on View Edit Option from View Patients Option ", ClientId);
         this.EditValues(request, out, conn, context,Database);
     }
     else if (ActionID.equals("EditSave")) {
         supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Edited Values", "Save Edited Values for Patient ", ClientId);
         this.EditSave(request, out, conn, context,Database);
     }
     try {
         conn.close();
         
         
         
         
     }
     catch (Exception ex) {}
     out.flush();
     out.close();
 }
 
 void GetValues(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext,String Database) {
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
         if(ClientIndex == 8){
             Parser.SetField("Date", String.valueOf(Date));
             Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
             Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/PatientRegFormOrange.html");
         }else if(ClientIndex == 9){
             Parser.SetField("Date", String.valueOf(Date));
             Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
             Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/PatientRegFormVictoria.html");
         }else if(ClientIndex == 10) {
             Parser.SetField("Date", String.valueOf(Date));
             Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
             Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/PatientRegFormOddasa.html");
         }
     }
     catch (Exception ex) {}
 }
 
 void SaveData(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext,String Database) {
     Statement stmt = null;
     ResultSet rset = null;
     String Query = "";
   
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
     String MRN = "0";
     try {
         if (request.getParameter("Title").trim().equals(null) || request.getParameter("Title").trim().equals("")) {
             Title = "Mr";
         }
         else {
             Title = request.getParameter("Title").trim();
         }
         if (request.getParameter("FirstName").trim().equals(null) || request.getParameter("FirstName").trim().equals("")) {
             FirstName = "";
         }
         else {
             FirstName = request.getParameter("FirstName").trim();
         }
         if (request.getParameter("LastName").trim().equals(null) || request.getParameter("LastName").trim().equals("")) {
             LastName = "Mr";
         }
         else {
             LastName = request.getParameter("LastName").trim();
         }
         if (request.getParameter("MiddleInitial").trim().equals(null) || request.getParameter("MiddleInitial").trim().equals("")) {
             MiddleInitial = "";
         }
         else {
             MiddleInitial = request.getParameter("MiddleInitial").trim();
         }
         if (request.getParameter("MaritalStatus").trim().equals(null) || request.getParameter("MaritalStatus").trim().equals("")) {
             MaritalStatus = "";
         }
         else {
             MaritalStatus = request.getParameter("MaritalStatus").trim();
         }
         if (request.getParameter("DOB").trim().equals(null) || request.getParameter("DOB").trim().equals("")) {
             DOB = "";
         }
         else {
             DOB = request.getParameter("DOB").trim();
         }
         if (request.getParameter("Age").trim().equals(null) || request.getParameter("Age").trim().equals("")) {
             Age = "0";
         }
         else {
             Age = request.getParameter("Age").trim();
         }
         if (request.getParameter("gender").trim().equals(null) || request.getParameter("gender").trim().equals("")) {
             gender = "male";
         }
         else {
             gender = request.getParameter("gender").trim();
         }
         if (request.getParameter("Email").trim().equals(null) || request.getParameter("Email").trim().equals("")) {
             Email = "";
         }
         else {
             Email = request.getParameter("Email").trim();
         }
         if (request.getParameter("PhNumber").trim().equals(null) || request.getParameter("PhNumber").trim().equals("")) {
             PhNumber = "0";
         }
         else {
             PhNumber = request.getParameter("PhNumber").trim();
         }
         if (request.getParameter("Address").trim().equals(null) || request.getParameter("Address").trim().equals("")) {
             Address = "";
         }
         else {
             Address = request.getParameter("Address").trim();
         }
         if (request.getParameter("City").trim().equals(null) || request.getParameter("City").trim().equals("")) {
             City = "";
         }
         else {
             City = request.getParameter("City").trim();
         }
         if (request.getParameter("State").trim().equals(null) || request.getParameter("State").trim().equals("")) {
             State = "";
         }
         else {
             State = request.getParameter("State").trim();
         }
         if (request.getParameter("Country").trim().equals(null) || request.getParameter("Country").trim().equals("")) {
             Country = "Mr";
         }
         else {
             Country = request.getParameter("Country").trim();
         }
         if (request.getParameter("ZipCode").trim().equals(null) || request.getParameter("ZipCode").trim().equals("")) {
             ZipCode = "";
         }
         else {
             ZipCode = request.getParameter("ZipCode").trim();
         }
         if (request.getParameter("SSN").trim().equals(null) || request.getParameter("SSN").trim().equals("")) {
             SSN = "";
         }
         else {
             SSN = request.getParameter("SSN").trim();
         }
         if (request.getParameter("Occupation").trim().equals(null) || request.getParameter("Occupation").trim().equals("")) {
             Occupation = "";
         }
         else {
             Occupation = request.getParameter("Occupation").trim();
         }
         if (request.getParameter("Employer").trim().equals(null) || request.getParameter("Employer").trim().equals("")) {
             Employer = "";
         }
         else {
             Employer = request.getParameter("Employer").trim();
         }
         if (request.getParameter("EmpContact").trim().equals(null) || request.getParameter("EmpContact").trim().equals("")) {
             EmpContact = "";
         }
         else {
             EmpContact = request.getParameter("EmpContact").trim();
         }
         if (request.getParameter("PriCarePhy").trim().equals(null) || request.getParameter("PriCarePhy").trim().equals("")) {
             PriCarePhy = "";
         }
         else {
             PriCarePhy = request.getParameter("PriCarePhy").trim();
         }
         if (request.getParameter("ReasonVisit").trim().equals(null) || request.getParameter("ReasonVisit").trim().equals("")) {
             ReasonVisit = "";
         }
         else {
             ReasonVisit = request.getParameter("ReasonVisit").trim();
         }
         if (request.getParameter("SelfPayChk") == null) {
             SelfPayChk = 0;
         }
         else {
             SelfPayChk = 1;
             if (request.getParameter("WorkersCompPolicy").trim().equals(null) || request.getParameter("WorkersCompPolicy").trim().equals("")) {
                 WorkersCompPolicy = 0;
             }
             else {
                 WorkersCompPolicy = Integer.parseInt(request.getParameter("WorkersCompPolicy").trim());
             }
             if (request.getParameter("MotorVehAccident").trim().equals(null) || request.getParameter("MotorVehAccident").trim().equals("")) {
                 MotorVehAccident = 0;
             }
             else {
                 MotorVehAccident = Integer.parseInt(request.getParameter("MotorVehAccident").trim());
             }
             if (request.getParameter("PriInsurance").trim().equals(null) || request.getParameter("PriInsurance").trim().equals("")) {
                 PriInsurance = "";
             }
             else {
                 PriInsurance = request.getParameter("PriInsurance").trim();
             }
             if (request.getParameter("MemId").trim().equals(null) || request.getParameter("MemId").trim().equals("")) {
                 MemId = "";
             }
             else {
                 MemId = request.getParameter("MemId").trim();
             }
             if (request.getParameter("GrpNumber").trim().equals(null) || request.getParameter("GrpNumber").trim().equals("")) {
                 GrpNumber = "";
             }
             else {
                 GrpNumber = request.getParameter("GrpNumber").trim();
             }
             if (request.getParameter("PriInsuranceName").trim().equals(null) || request.getParameter("PriInsuranceName").trim().equals("")) {
                 PriInsuranceName = "";
             }
             else {
                 PriInsuranceName = request.getParameter("PriInsuranceName").trim();
             }
             if (request.getParameter("AddressIfDifferent").trim().equals(null) || request.getParameter("AddressIfDifferent").trim().equals("")) {
                 AddressIfDifferent = "";
             }
             else {
                 AddressIfDifferent = request.getParameter("AddressIfDifferent").trim();
             }
             if (request.getParameter("PrimaryDOB").trim().equals(null) || request.getParameter("PrimaryDOB").trim().equals("")) {
                 PrimaryDOB = "";
             }
             else {
                 PrimaryDOB = request.getParameter("PrimaryDOB").trim();
             }
             if (request.getParameter("PrimarySSN").trim().equals(null) || request.getParameter("PrimarySSN").trim().equals("")) {
                 PrimarySSN = "";
             }
             else {
                 PrimarySSN = request.getParameter("PrimarySSN").trim();
             }
             if (request.getParameter("PatientRelationtoPrimary").trim().equals(null) || request.getParameter("PatientRelationtoPrimary").trim().equals("")) {
                 PatientRelationtoPrimary = "0";
             }
             else {
                 PatientRelationtoPrimary = request.getParameter("PatientRelationtoPrimary").trim();
             }
             if (request.getParameter("PrimaryOccupation").trim().equals(null) || request.getParameter("PrimaryOccupation").trim().equals("")) {
                 PrimaryOccupation = "";
             }
             else {
                 PrimaryOccupation = request.getParameter("PrimaryOccupation").trim();
             }
             if (request.getParameter("PrimaryEmployer").trim().equals(null) || request.getParameter("PrimaryEmployer").trim().equals("")) {
                 PrimaryEmployer = "";
             }
             else {
                 PrimaryEmployer = request.getParameter("PrimaryEmployer").trim();
             }
             if (request.getParameter("EmployerAddress").trim().equals(null) || request.getParameter("EmployerAddress").trim().equals("")) {
                 EmployerAddress = "";
             }
             else {
                 EmployerAddress = request.getParameter("EmployerAddress").trim();
             }
             if (request.getParameter("EmployerPhone").trim().equals(null) || request.getParameter("EmployerPhone").trim().equals("")) {
                 EmployerPhone = "";
             }
             else {
                 EmployerPhone = request.getParameter("EmployerPhone").trim();
             }
             if (request.getParameter("SecondryInsurance").trim().equals(null) || request.getParameter("SecondryInsurance").trim().equals("")) {
                 SecondryInsurance = "0";
             }
             else {
                 SecondryInsurance = request.getParameter("SecondryInsurance").trim();
             }
             if (request.getParameter("SubscriberName").trim().equals(null) || request.getParameter("SubscriberName").trim().equals("")) {
                 SubscriberName = "";
             }
             else {
                 SubscriberName = request.getParameter("SubscriberName").trim();
             }
             if (request.getParameter("SubscriberDOB").trim().equals(null) || request.getParameter("SubscriberDOB").trim().equals("")) {
                 SubscriberDOB = "0000-00-00";
             }
             else {
                 SubscriberDOB = request.getParameter("SubscriberDOB").trim();
             }
             if (request.getParameter("MemberID_2").trim().equals(null) || request.getParameter("MemberID_2").trim().equals("")) {
                 MemberID_2 = "";
             }
             else {
                 MemberID_2 = request.getParameter("MemberID_2").trim();
             }
             if (request.getParameter("GroupNumber_2").trim().equals(null) || request.getParameter("GroupNumber_2").trim().equals("")) {
                 GroupNumber_2 = "";
             }
             else {
                 GroupNumber_2 = request.getParameter("GroupNumber_2").trim();
             }
             if (request.getParameter("PatientRelationshiptoSecondry").trim().equals(null) || request.getParameter("PatientRelationshiptoSecondry").trim().equals("")) {
                 PatientRelationshiptoSecondry = "";
             }
             else {
                 PatientRelationshiptoSecondry = request.getParameter("PatientRelationshiptoSecondry").trim();
             }
         }
         if (request.getParameter("NextofKinName").trim().equals(null) || request.getParameter("NextofKinName").trim().equals("")) {
             NextofKinName = "";
         }
         else {
             NextofKinName = request.getParameter("NextofKinName").trim();
         }
         if (request.getParameter("RelationToPatientER").trim().equals(null) || request.getParameter("RelationToPatientER").trim().equals("")) {
             RelationToPatientER = "";
         }
         else {
             RelationToPatientER = request.getParameter("RelationToPatientER").trim();
         }
         if (request.getParameter("PhoneNumberER").trim().equals(null) || request.getParameter("PhoneNumberER").trim().equals("")) {
             PhoneNumberER = "";
         }
         else {
             PhoneNumberER = request.getParameter("PhoneNumberER").trim();
         }
         if (request.getParameter("LeaveMessageER") == null) {
             LeaveMessageER = 0;
         }
         else {
             LeaveMessageER = Integer.parseInt(request.getParameter("LeaveMessageER").trim());
         }
         if (request.getParameter("AddressER").trim().equals(null) || request.getParameter("AddressER").trim().equals("")) {
             AddressER = "";
         }
         else {
             AddressER = request.getParameter("AddressER").trim();
         }
         if (request.getParameter("CityER").trim().equals(null) || request.getParameter("CityER").trim().equals("")) {
             CityER = "";
         }
         else {
             CityER = request.getParameter("CityER").trim();
         }
         if (request.getParameter("StateER").trim().equals(null) || request.getParameter("StateER").trim().equals("")) {
             StateER = "";
         }
         else {
             StateER = request.getParameter("StateER").trim();
         }
         if (request.getParameter("CountryER").trim().equals(null) || request.getParameter("CountryER").trim().equals("")) {
             CountryER = "";
         }
         else {
             CountryER = request.getParameter("CountryER").trim();
         }
         if (request.getParameter("ZipCodeER").trim().equals(null) || request.getParameter("ZipCodeER").trim().equals("")) {
             ZipCodeER = "";
         }
         else {
             ZipCodeER = request.getParameter("ZipCodeER").trim();
         }
         if (request.getParameter("VerifyChkBox").trim().equals(null) || request.getParameter("VerifyChkBox").trim().equals("")) {
             VerifyChkBox = 0;
         }
         else {
             VerifyChkBox = 1;
         }
         if (request.getParameter("PatientSignConcent").trim().equals(null) || request.getParameter("PatientSignConcent").trim().equals("")) {
             PatientSignConcent = "";
         }
         else {
             PatientSignConcent = request.getParameter("PatientSignConcent").trim();
         }
         if (request.getParameter("DateConcent").trim().equals(null) || request.getParameter("DateConcent").trim().equals("")) {
             DateConcent = "";
         }
         else {
             DateConcent = request.getParameter("DateConcent").trim();
         }
         if (request.getParameter("WitnessConcent").trim().equals(null) || request.getParameter("WitnessConcent").trim().equals("")) {
             WitnessConcent = "";
         }
         else {
             WitnessConcent = request.getParameter("WitnessConcent").trim();
         }
         if (request.getParameter("PatientBehalfConcent").trim().equals(null) || request.getParameter("PatientBehalfConcent").trim().equals("")) {
             PatientBehalfConcent = "";
         }
         else {
             PatientBehalfConcent = request.getParameter("PatientBehalfConcent").trim();
         }
         if (request.getParameter("RelativeSignConcent").trim().equals(null) || request.getParameter("RelativeSignConcent").trim().equals("")) {
             RelativeSignConcent = "";
         }
         else {
             RelativeSignConcent = request.getParameter("RelativeSignConcent").trim();
         }
         if (request.getParameter("DateConcent2").trim().equals(null) || request.getParameter("DateConcent2").trim().equals("")) {
             DateConcent2 = "";
         }
         else {
             DateConcent2 = request.getParameter("DateConcent2").trim();
         }
         if (request.getParameter("WitnessConcent2").trim().equals(null) || request.getParameter("WitnessConcent2").trim().equals("")) {
             WitnessConcent2 = "";
         }
         else {
             WitnessConcent2 = request.getParameter("WitnessConcent2").trim();
         }
         if (request.getParameter("ReturnPatient") == null) {
             ReturnPatient = 0;
         }
         else {
             ReturnPatient = 1;
         }
         if (request.getParameter("Google") == null) {
             Google = 0;
         }
         else {
             Google = 1;
         }
         if (request.getParameter("MapSearch") == null) {
             MapSearch = 0;
         }
         else {
             MapSearch = 1;
         }
         if (request.getParameter("Billboard") == null) {
             Billboard = 0;
         }
         else {
             Billboard = 1;
         }
         if (request.getParameter("OnlineReview") == null) {
             OnlineReview = 0;
         }
         else {
             OnlineReview = 1;
         }
         if (request.getParameter("TV") == null) {
             TV = 0;
         }
         else {
             TV = 2;
         }
         if (request.getParameter("Website") == null) {
             Website = 0;
         }
         else {
             Website = 1;
         }
         if (request.getParameter("BuildingSignDriveBy") == null) {
             BuildingSignDriveBy = 0;
         }
         else {
             BuildingSignDriveBy = 1;
         }
         if (request.getParameter("Facebook") == null) {
             Facebook = 0;
         }
         else {
             Facebook = 1;
         }
         if (request.getParameter("School") == null) {
             School = 0;
             School_text = "-";
         }
         else {
             School = 1;
             if (request.getParameter("School_text").trim().equals(null) || request.getParameter("School_text").trim().equals("")) {
                 School_text = "-";
             }
             else {
                 School_text = request.getParameter("School_text").trim();
             }
         }
         if (request.getParameter("Twitter") == null) {
             Twitter = 0;
         }
         else {
             Twitter = 1;
         }
         if (request.getParameter("Magazine") == null) {
             Magazine = 0;
             Magazine_text = "";
         }
         else {
             Magazine = 1;
             if (request.getParameter("Magazine_text").trim().equals(null) || request.getParameter("Magazine_text").trim().equals("")) {
                 Magazine_text = "";
             }
             else {
                 Magazine_text = request.getParameter("Magazine_text").trim();
             }
         }
         if (request.getParameter("Newspaper") == null) {
             Newspaper = 0;
             Newspaper_text = "";
         }
         else {
             Newspaper = 1;
             if (request.getParameter("Newspaper_text").trim().equals(null) || request.getParameter("Newspaper_text").trim().equals("")) {
                 Newspaper_text = "";
             }
             else {
                 Newspaper_text = request.getParameter("Newspaper_text").trim();
             }
         }
         if (request.getParameter("FamilyFriend") == null) {
             FamilyFriend = 0;
             FamilyFriend_text = "";
         }
         else {
             FamilyFriend = 1;
             if (request.getParameter("FamilyFriend_text").trim().equals(null) || request.getParameter("FamilyFriend_text").trim().equals("")) {
                 FamilyFriend_text = "";
             }
             else {
                 FamilyFriend_text = request.getParameter("FamilyFriend_text").trim();
             }
         }
         if (request.getParameter("UrgentCare") == null) {
             UrgentCare = 0;
             UrgentCare_text = "";
         }
         else {
             UrgentCare = 1;
             if (request.getParameter("UrgentCare_text").trim().equals(null) || request.getParameter("UrgentCare_text").trim().equals("")) {
                 UrgentCare_text = "";
             }
             else {
                 UrgentCare_text = request.getParameter("UrgentCare_text").trim();
             }
         }
         if (request.getParameter("CommunityEvent") == null) {
             CommunityEvent = 0;
             CommunityEvent_text = "";
         }
         else {
             CommunityEvent = 1;
             if (request.getParameter("CommunityEvent_text").trim().equals(null) || request.getParameter("CommunityEvent_text").trim().equals("")) {
                 CommunityEvent_text = "";
             }
             else {
                 CommunityEvent_text = request.getParameter("CommunityEvent_text").trim();
             }
         }
         if (request.getParameter("Work") == null) {
             Work = 0;
             Work_text = "";
         }
         else {
             Work = 1;
             if (request.getParameter("Work_text").trim().equals(null) || request.getParameter("Work_text").trim().equals("")) {
                 Work_text = "";
             }
             else {
                 Work_text = request.getParameter("Work_text").trim();
             }
         }
         if (request.getParameter("Physician") == null) {
             Physician = 0;
             Physician_text = "";
         }
         else {
             Physician = 1;
             if (request.getParameter("Physician_text").trim().equals(null) || request.getParameter("Physician_text").trim().equals("")) {
                 Physician_text = "";
             }
             else {
                 Physician_text = request.getParameter("Physician_text").trim();
             }
         }
         if (request.getParameter("Other") == null) {
             Other = 0;
             Other_text = "";
         }
         else {
             Other = 1;
             if (request.getParameter("Other_text").trim().equals(null) || request.getParameter("Other_text").trim().equals("")) {
                 Other_text = "";
             }
             else {
                 Other_text = request.getParameter("Other_text").trim();
             }
         }
         try {
             Query = "Select Id from oe.clients where ltrim(rtrim(UPPER(name))) =  ltrim(rtrim(UPPER('" + ClientId + "')))";
             stmt = conn.createStatement();
             rset = stmt.executeQuery(Query);
             if (rset.next()) {
                 ClientIndex = rset.getInt(1);
             }
             rset.close();
             stmt.close();
             if(ClientIndex == 8){
                 Database = "oe_2";//orange
             }else if(ClientIndex == 9){
                 Database = "victoria"; //victoria
             }else if(ClientIndex == 10){
                 Database = "oddasa"; //oddasa
             }
             Query = "Select MAX(MRN) + 1  from "+Database+".PatientReg ";
             stmt = conn.createStatement();
             rset = stmt.executeQuery(Query);
             if (rset.next()) {
                 MRN = rset.getString(1);
             }
             rset.close();
             stmt.close();
             if (MRN == null) {
                 MRN = "310001";
             }
         }
         catch (Exception e) {
             out.println("Error 1:" + e.getMessage() + Query);
         }
         try {
             final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO "+Database+".PatientReg (ClientIndex,FirstName,LastName ,MiddleInitial,DOB,Age,Gender ,Email,PhNumber ,Address,City ,State,Country,ZipCode,SSN,Occupation ,Employer ,EmpContact,PriCarePhy,ReasonVisit,SelfPayChk,CreatedDate,Title, MaritalStatus,CreatedBy, MRN) \nVALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?) ");
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
             MainReceipt.setString(25, MRN);
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
         }
         try {
             if (SelfPayChk == 1) {
                 final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO "+Database+".InsuranceInfo(PatientRegId,WorkersCompPolicy,MotorVehAccident,PriInsurance,MemId,GrpNumber,PriInsuranceName,AddressIfDifferent,PrimaryDOB,PrimarySSN,PatientRelationtoPrimary,PrimaryOccupation,PrimaryEmployer,EmployerAddress,EmployerPhone,SecondryInsurance,SubscriberName,SubscriberDOB,MemberID_2,GroupNumber_2,PatientRelationshiptoSecondry,CreatedDate) \nVALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
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
         }
         catch (Exception e) {
             out.println("Error 4- Insertion InsuranceInfo Table :" + e.getMessage());
             return;
         }
         try {
             final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO "+Database+".EmergencyInfo (PatientRegId,NextofKinName,RelationToPatient,PhoneNumber,LeaveMessage,Address,City,State,Country,ZipCode,CreatedDate) \nVALUES (?,?,?,?,?,?,?,?,?,?,now()) ");
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
         }
         catch (Exception e) {
             out.println("Error 5- Insertion EmergencyInfo Table :" + e.getMessage());
             return;
         }
         try {
             final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO "+Database+".ConcentToTreatmentInfo (PatientRegId,PatientSign,Date,Witness,PatientBehalfSign,RelativeSign,Date2,Witness2,CreatedDate) \nVALUES (?,?,?,?,?,?,?,?,now()) ");
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
         }
         catch (Exception e) {
             out.println("Error 6- Insertion ConcentToTreatmentInfo Table :" + e.getMessage());
             return;
         }
         try {
             final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO "+Database+".RandomCheckInfo (PatientRegId,ReturnPatient,Google,MapSearch,Billboard,OnlineReview,TV,Website,BuildingSignDriveBy,Facebook,School,School_text,Twitter,Magazine,Magazine_text,Newspaper,Newspaper_text,FamilyFriend,FamilyFriend_text,UrgentCare,UrgentCare_text,CommunityEvent,CommunityEvent_text,Work_text,Physician_text,Other_text,CreatedDate) \nVALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
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
         }
         catch (Exception e) {
             out.println("Error 7- Insertion RandomCheckInfo Table :" + e.getMessage());
             return;
         }
         Query = "Select CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName) from "+Database+".PatientReg where ID = " + PatientRegId;
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         if (rset.next()) {
             PatientName = rset.getString(1);
         }
         rset.close();
         stmt.close();
         String Date = "";
         Query = "Select Date_format(now(),'%Y-%m-%d')";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         while (rset.next()) {
             Date = rset.getString(1);
         }
         rset.close();
         stmt.close();
         final Parsehtm Parser = new Parsehtm(request);
         Parser.SetField("PatientName", String.valueOf(PatientName));
         Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message_Success.html");
     }
     catch (Exception e) {
         System.out.println(e.getMessage());
         String str = "";
         for (int i = 0; i < e.getStackTrace().length; ++i) {
             str = str + e.getStackTrace()[i] + "<br>";
         }
         out.println(str);
     }
 }
 
 void EditValues(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext,String Database) {
     Statement stmt = null;
     ResultSet rset = null;
     String Query = "";
   
     int ClientIndex = 0;
     int PatientRegId = 0;
     final String MRN = request.getParameter("MRN").trim();
     int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());

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
     String COVIDStatus = "";
     String DoctorName = "";
     String DateofService = null;
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
     final StringBuffer ReturnPatient = new StringBuffer();
     final StringBuffer Google = new StringBuffer();
     final StringBuffer MapSearch = new StringBuffer();
     final StringBuffer Billboard = new StringBuffer();
     final StringBuffer OnlineReview = new StringBuffer();
     final StringBuffer TV = new StringBuffer();
     final StringBuffer Website = new StringBuffer();
     final StringBuffer BuildingSignDriveBy = new StringBuffer();
     final StringBuffer Facebook = new StringBuffer();
     final StringBuffer School = new StringBuffer();
     String School_text = "";
     final StringBuffer Twitter = new StringBuffer();
     final StringBuffer Magazine = new StringBuffer();
     String Magazine_text = "";
     final StringBuffer Newspaper = new StringBuffer();
     String Newspaper_text = "";
     final StringBuffer FamilyFriend = new StringBuffer();
     String FamilyFriend_text = "";
     final StringBuffer UrgentCare = new StringBuffer();
     String UrgentCare_text = "";
     final StringBuffer CommunityEvent = new StringBuffer();
     String CommunityEvent_text = "";
     final StringBuffer Work_textBuff = new StringBuffer();
     String Work_text = "";
     final StringBuffer Physician_textBuff = new StringBuffer();
     String Physician_text = "";
     final StringBuffer Other_textBuff = new StringBuffer();
     String Other_text = "";
     int SelfPayChk = 0;
     final int VerifyChkBox = 0;
     String PatientName = "";
     String DOS = "";
     final StringBuffer TitleBuff = new StringBuffer();
     final StringBuffer PriInsuranceNameBuff = new StringBuffer();
     final StringBuffer SecondryInsuranceBuff = new StringBuffer();
     final StringBuffer DoctorList = new StringBuffer();
     final StringBuffer MaritalStatusBuff = new StringBuffer();
     final StringBuffer PatientRelationBuff = new StringBuffer();
     final StringBuffer CountryBuff = new StringBuffer();
     final StringBuffer genderBuffMale = new StringBuffer();
     final StringBuffer genderBuffFemale = new StringBuffer();
     final StringBuffer SelfPayChkBuff = new StringBuffer();
     final StringBuffer WorkersCompPolicyBuffYes = new StringBuffer();
     final StringBuffer MotorVehAccidentBuffYes = new StringBuffer();
     final StringBuffer WorkersCompPolicyBuffNo = new StringBuffer();
     final StringBuffer MotorVehAccidentBuffNo = new StringBuffer();
     final StringBuffer PatientRelationshiptoSecondryBuff = new StringBuffer();
     final StringBuffer PatientRelationtoPrimaryBuff = new StringBuffer();
     final StringBuffer CountryBuffER = new StringBuffer();
     final StringBuffer LeaveMessageERBuffYes = new StringBuffer();
     final StringBuffer LeaveMessageERBuffNo = new StringBuffer();
     try {
         Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'), '-'),  IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(City,'-'), IFNULL(State,'-'), IFNULL(ZipCode,'-'), IFNULL(PhNumber,'-'), IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(ID,0), ClientIndex, DATE_FORMAT(CreatedDate, '%d-%m-%Y'), IFNULL(Country,'-'), IFNULL(COVIDStatus, '-'), IFNULL(DoctorsName,'-'), IFNULL(DateofService, '-')   From "+Database+".PatientReg Where MRN =" + MRN;
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         while (rset.next()) {
             LastName = rset.getString(1).trim();
             FirstName = rset.getString(2).trim();
             MiddleInitial = rset.getString(3).trim();
             Title = rset.getString(4).trim();
             MaritalStatus = rset.getString(5);
             DOB = rset.getString(6);
             Age = rset.getString(7);
             gender = rset.getString(8);
             Address = rset.getString(9);
             City = rset.getString(10);
             State = rset.getString(11);
             ZipCode = rset.getString(12);
             PhNumber = rset.getString(13);
             SSN = rset.getString(14);
             Occupation = rset.getString(15);
             Employer = rset.getString(16);
             EmpContact = rset.getString(17);
             PriCarePhy = rset.getString(18);
             Email = rset.getString(19);
             ReasonVisit = rset.getString(20);
             SelfPayChk = rset.getInt(21);
             PatientRegId = rset.getInt(22);
             ClientIndex = rset.getInt(23);
             DOS = rset.getString(24);
             Country = rset.getString(25);
             COVIDStatus = rset.getString(26);
             DoctorName = rset.getString(27);
             DateofService = rset.getString(28);
         }
         rset.close();
         stmt.close();
         Query = "Select Title from "+Database+".Title";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         TitleBuff.append("<option value='-1'>Select Title</option>");
         while (rset.next()) {
             if (Title.equals(rset.getString(1))) {
                 TitleBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(1) + "</option>");
             }
             else {
                 TitleBuff.append("<option value=" + rset.getString(1) + ">" + rset.getString(1) + "</option>");
             }
         }
         rset.close();
         stmt.close();

         Query = "Select Id, CONCAT(DoctorsLastName, ' , ', DoctorsFirstName) from "+Database+".DoctorsList";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         DoctorList.append("<option value='-1'>Select Physician</option>");
         while (rset.next()) {
             if (DoctorName.equals(rset.getString(1))) {
                 DoctorList.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(2) + "</option>");
             }
             else {
                 DoctorList.append("<option value=" + rset.getString(1) + ">" + rset.getString(2) + "</option>");
             }
         }
         rset.close();
         stmt.close();



         Query = "Select Country from "+Database+".Country";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         CountryBuff.append("<option value='-1'>Select Country</option>");
         while (rset.next()) {
             if (Country.equals(rset.getString(1))) {
                 CountryBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(1) + "</option>");
             }
             else {
                 CountryBuff.append("<option value=" + rset.getString(1) + ">" + rset.getString(1) + "</option>");
             }
         }
         rset.close();
         stmt.close();
         Query = "Select MaritalStatus from "+Database+".MaritalStatus";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         while (rset.next()) {
             if (MaritalStatus.equals(rset.getString(1))) {
                 MaritalStatusBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(1) + "</option>");
             }
             else {
                 MaritalStatusBuff.append("<option value=" + rset.getString(1) + ">" + rset.getString(1) + "</option>");
             }
         }
         rset.close();
         stmt.close();
         if (gender.equals("male")) {
             genderBuffMale.append("<input type=\"radio\" name=\"gender\" id=\"genderM\" value=\"male\" checked> Male<br>");
             genderBuffFemale.append("<input type=\"radio\" name=\"gender\" id=\"genderN\" value=\"female\"> Female<br>");
         }
         else {
             genderBuffMale.append("<input type=\"radio\" name=\"gender\" id=\"genderM\" value=\"male\" > Male<br>");
             genderBuffFemale.append("<input type=\"radio\" name=\"gender\" id=\"genderN\" value=\"female\" checked> Female<br>");
         }
         if (SelfPayChk == 1) {
             SelfPayChkBuff.append("<input type=\"checkbox\" id=\"SelfPayChk\" name=\"SelfPayChk\"  checked />");
         }
         else {
             SelfPayChkBuff.append("<input type=\"checkbox\" id=\"SelfPayChk\" name=\"SelfPayChk\"  />");
         }
         if (SelfPayChk == 1) {
             Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'),  IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%Y-%m-%d'),'-'), IFNULL(PrimarySSN,'-'),  IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'),  IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%Y-%m-%d'),'-'),  IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from "+Database+".InsuranceInfo  where PatientRegId = " + PatientRegId;
             stmt = conn.createStatement();
             rset = stmt.executeQuery(Query);
             while (rset.next()) {
                 WorkersCompPolicy = rset.getInt(1);
                 MotorVehAccident = rset.getInt(2);
                 if (WorkersCompPolicy == 0) {
                     WorkersCompPolicyBuffNo.append("<input type=\"radio\" name=\"WorkersCompPolicy\" id=\"WorkersCompPolicyN\" value=\"0\" checked> No<br>");
                     WorkersCompPolicyBuffYes.append("<input type=\"radio\" name=\"WorkersCompPolicy\" id=\"WorkersCompPolicyY\" value=\"1\" > Yes<br>");
                 }
                 else {
                     WorkersCompPolicyBuffYes.append("<input type=\"radio\" name=\"WorkersCompPolicy\" id=\"WorkersCompPolicyY\" value=\"1\" checked > Yes<br>");
                     WorkersCompPolicyBuffNo.append("<input type=\"radio\" name=\"WorkersCompPolicy\" id=\"WorkersCompPolicyN\" value=\"0\" > No<br>");
                 }
                 if (MotorVehAccident == 0) {
                     MotorVehAccidentBuffNo.append("<input type=\"radio\" name=\"MotorVehAccident\" id=\"MotorVehAccidentN\" value=\"0\" checked> No<br>");
                     MotorVehAccidentBuffYes.append("<input type=\"radio\" name=\"MotorVehAccident\" id=\"MotorVehAccidentY\" value=\"1\" > Yes<br>");
                 }
                 else {
                     MotorVehAccidentBuffYes.append("<input type=\"radio\" name=\"MotorVehAccident\" id=\"MotorVehAccidentY\" value=\"1\" checked> Yes<br>");
                     MotorVehAccidentBuffNo.append("<input type=\"radio\" name=\"MotorVehAccident\" id=\"MotorVehAccidentN\" value=\"0\" > No<br>");
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
         Query = "Select Id, PayerId, PayerName from "+Database+".ProfessionalPayers";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         while (rset.next()) {
             if (PriInsuranceName.equals(rset.getString(1))) {
                 PriInsuranceNameBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(3) + "</option>");
             }
             else {
                 PriInsuranceNameBuff.append("<option value=" + rset.getString(1) + ">" + rset.getString(3) + "</option>");
             }
         }
         rset.close();
         stmt.close();

         Query = "Select Id, PayerId, PayerName from "+Database+".ProfessionalPayers";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         while (rset.next()) {
             if (SecondryInsurance.equals(rset.getString(1))) {
                 SecondryInsuranceBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(3) + "</option>");
             }
             else {
                 SecondryInsuranceBuff.append("<option value=" + rset.getString(1) + ">" + rset.getString(3) + "</option>");
             }
         }
         rset.close();
         stmt.close();
         Query = "Select PatientRelation from "+Database+".PatientRelation";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         while (rset.next()) {
             if (PatientRelationtoPrimary.equals(rset.getString(1))) {
                 PatientRelationtoPrimaryBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(1) + "</option>");
             }
             PatientRelationtoPrimaryBuff.append("<option value=" + rset.getString(1) + " >" + rset.getString(1) + "</option>");
         }
         rset.close();
         stmt.close();
         Query = "Select PatientRelation from "+Database+".PatientRelation";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         while (rset.next()) {
             if (PatientRelationshiptoSecondry.equals(rset.getString(1))) {
                 PatientRelationshiptoSecondryBuff.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(1) + "</option>");
             }
             PatientRelationshiptoSecondryBuff.append("<option value=" + rset.getString(1) + " >" + rset.getString(1) + "</option>");
         }
         rset.close();
         stmt.close();
         Query = " Select IFNULL(NextofKinName,'-'), IFNULL(RelationToPatient,'-'), IFNULL(PhoneNumber,'-'),  IFNULL(LeaveMessage,0), IFNULL(Address,'-'), IFNULL(City,'-'), IFNULL(State,'-'), IFNULL(Country,'-'), IFNULL(ZipCode,'-')  from "+Database+".EmergencyInfo where PatientRegId = " + PatientRegId;
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         while (rset.next()) {
             NextofKinName = rset.getString(1);
             RelationToPatientER = rset.getString(2);
             PhoneNumberER = rset.getString(3);
             LeaveMessageER = rset.getInt(4);
             if (LeaveMessageER == 0) {
                 LeaveMessageERBuffNo.append("<input type=\"radio\" name=\"LeaveMessageER\" id=\"LeaveMessageERN\" value=\"0\" checked> No<br>");
                 LeaveMessageERBuffYes.append("<input type=\"radio\" name=\"LeaveMessageER\" id=\"LeaveMessageERY\" value=\"1\" > Yes<br>");
             }
             else {
                 LeaveMessageERBuffYes.append("<input type=\"radio\" name=\"LeaveMessageER\" id=\"LeaveMessageERY\" value=\"1\" checked> Yes<br>");
                 LeaveMessageERBuffNo.append("<input type=\"radio\" name=\"LeaveMessageER\" id=\"LeaveMessageERN\" value=\"0\" > No<br>");
             }
             AddressER = rset.getString(5);
             CityER = rset.getString(6);
             StateER = rset.getString(7);
             CountryER = rset.getString(8);
             ZipCodeER = rset.getString(9);
         }
         rset.close();
         stmt.close();
         Query = "Select Country from "+Database+".Country";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         CountryBuffER.append("<option value='-1'>Select Country</option>");
         while (rset.next()) {
             if (CountryER.equals(rset.getString(1))) {
                 CountryBuffER.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(1) + "</option>");
             }
             else {
                 CountryBuffER.append("<option value=" + rset.getString(1) + ">" + rset.getString(1) + "</option>");
             }
         }
         rset.close();
         stmt.close();
         Query = " Select IFNULL(PatientSign,'-'), DATE_FORMAT(Date,'%Y-%m-%d'), IFNULL(Witness,'-'), IFNULL(PatientBehalfSign,'-'), IFNULL(RelativeSign,'-'), DATE_FORMAT(Date2,'%Y-%m-%d'), IFNULL(Witness2,'-') from "+Database+".ConcentToTreatmentInfo  Where PatientRegId = " + PatientRegId;
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         while (rset.next()) {
             PatientSignConcent = rset.getString(1);
             DateConcent = rset.getString(2);
             WitnessConcent = rset.getString(3);
             PatientBehalfConcent = rset.getString(4);
             RelativeSignConcent = rset.getString(5);
             DateConcent2 = rset.getString(6);
             WitnessConcent2 = rset.getString(7);
         }
         rset.close();
         stmt.close();
         Query = " Select ReturnPatient, Google, MapSearch, Billboard, OnlineReview, TV, Website, BuildingSignDriveBy, Facebook, School, IFNULL(School_text ,'-'), Twitter, Magazine, IFNULL(Magazine_text,'-'), Newspaper, IFNULL(Newspaper_text,'-'), FamilyFriend, IFNULL(FamilyFriend_text,'-'), UrgentCare, IFNULL(UrgentCare_text,'-'), CommunityEvent, IFNULL(CommunityEvent_text,'-'),  Work_text, Physician_text, Other_text from "+Database+".RandomCheckInfo where PatientRegId = " + PatientRegId;
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         while (rset.next()) {
             if (rset.getInt(1) == 0) {
                 ReturnPatient.append("<input type=\"checkbox\" id=\"ReturnPatient\" name=\"ReturnPatient\">");
             }
             else {
                 ReturnPatient.append("<input type=\"checkbox\" id=\"ReturnPatient\" name=\"ReturnPatient\" checked>");
             }
             if (rset.getInt(2) == 0) {
                 Google.append("<input type=\"checkbox\" id=\"Google\" name=\"Google\">");
             }
             else {
                 Google.append("<input type=\"checkbox\" id=\"Google\" name=\"Google\" checked>");
             }
             if (rset.getInt(3) == 0) {
                 MapSearch.append("<input type=\"checkbox\" id=\"MapSearch\" name=\"MapSearch\">");
             }
             else {
                 MapSearch.append("<input type=\"checkbox\" id=\"MapSearch\" name=\"MapSearch\" checked>");
             }
             if (rset.getInt(4) == 0) {
                 Billboard.append("<input type=\"checkbox\" id=\"Billboard\" name=\"Billboard\">");
             }
             else {
                 Billboard.append("<input type=\"checkbox\" id=\"Billboard\" name=\"Billboard\" checked>");
             }
             if (rset.getInt(5) == 0) {
                 OnlineReview.append("<input type=\"checkbox\" id=\"OnlineReview\" name=\"OnlineReview\">");
             }
             else {
                 OnlineReview.append("<input type=\"checkbox\" id=\"OnlineReview\" name=\"OnlineReview\" checked>");
             }
             if (rset.getInt(6) == 0) {
                 TV.append("<input type=\"checkbox\" id=\"TV\" name=\"TV\">");
             }
             else {
                 TV.append("<input type=\"checkbox\" id=\"TV\" name=\"TV\"> checked");
             }
             if (rset.getInt(7) == 0) {
                 Website.append("<input type=\"checkbox\" id=\"Website\" name=\"Website\">");
             }
             else {
                 Website.append("<input type=\"checkbox\" id=\"Website\" name=\"Website\" checked>");
             }
             if (rset.getInt(8) == 0) {
                 BuildingSignDriveBy.append("<input type=\"checkbox\" id=\"BuildingSignDriveBy\" name=\"BuildingSignDriveBy\">");
             }
             else {
                 BuildingSignDriveBy.append("<input type=\"checkbox\" id=\"BuildingSignDriveBy\" name=\"BuildingSignDriveBy\" checked>");
             }
             if (rset.getInt(9) == 0) {
                 Facebook.append("<input type=\"checkbox\" id=\"Facebook\" name=\"Facebook\">");
             }
             else {
                 Facebook.append("<input type=\"checkbox\" id=\"Facebook\" name=\"Facebook\" checked>");
             }
             if (rset.getInt(10) == 0) {
                 School.append("<input type=\"checkbox\" id=\"School\" name=\"School\">");
                 School_text = "";
             }
             else {
                 School.append("<input type=\"checkbox\" id=\"School\" name=\"School\" checked>");
                 School_text = rset.getString(11);
             }
             if (rset.getInt(12) == 0) {
                 Twitter.append("<input type=\"checkbox\" id=\"Twitter\" name=\"Twitter\">");
             }
             else {
                 Twitter.append("<input type=\"checkbox\" id=\"Twitter\" name=\"Twitter\" checked>");
             }
             if (rset.getInt(13) == 0) {
                 Magazine.append("<input type=\"checkbox\" id=\"Magazine\" name=\"Magazine\">");
                 Magazine_text = "";
             }
             else {
                 Magazine.append("<input type=\"checkbox\" id=\"Magazine\" name=\"Magazine\" checked>");
                 Magazine_text = rset.getString(14);
             }
             if (rset.getInt(15) == 0) {
                 Newspaper.append("<input type=\"checkbox\" id=\"Newspaper\" name=\"Newspaper\">");
                 Newspaper_text = "";
             }
             else {
                 Newspaper.append("<input type=\"checkbox\" id=\"Newspaper\" name=\"Newspaper\" checked>");
                 Newspaper_text = rset.getString(16);
             }
             if (rset.getInt(17) == 0) {
                 FamilyFriend.append("<input type=\"checkbox\" id=\"FamilyFriend\" name=\"FamilyFriend\">");
                 FamilyFriend_text = "";
             }
             else {
                 FamilyFriend.append("<input type=\"checkbox\" id=\"FamilyFriend\" name=\"FamilyFriend\" checked>");
                 FamilyFriend_text = rset.getString(18);
             }
             if (rset.getInt(19) == 0) {
                 UrgentCare.append("<input type=\"checkbox\" id=\"UrgentCare\" name=\"UrgentCare\">");
                 UrgentCare_text = "";
             }
             else {
                 UrgentCare.append("<input type=\"checkbox\" id=\"UrgentCare\" name=\"UrgentCare\" checked>");
                 UrgentCare_text = rset.getString(20);
             }
             if (rset.getInt(21) == 0) {
                 CommunityEvent.append("<input type=\"checkbox\" id=\"CommunityEvent\">");
                 CommunityEvent_text = "";
             }
             else {
                 CommunityEvent.append("<input type=\"checkbox\" id=\"CommunityEvent\" checked>");
                 CommunityEvent_text = rset.getString(22);
             }
             if (rset.getString(23) == "" || rset.getString(23) == null || rset.getString(23).equals("")) {
                 System.out.println("Physician_text IF condition");
                 Work_text = "";
                 Work_textBuff.append("<input type=\"checkbox\" id=\"Work\" name=\"Work\">");
             }
             else {
                 System.out.println("Physician_text Else condition");
                 Work_textBuff.append("<input type=\"checkbox\" id=\"Work\" name=\"Work\" checked>");
                 Work_text = rset.getString(23);
             }
             if (rset.getString(24) == "" || rset.getString(24) == null || rset.getString(24).equals("")) {
                 System.out.println("Physician_text IF condition");
                 Physician_text = "";
                 Physician_textBuff.append("<input type=\"checkbox\" id=\"Physician\" name=\"Physician\">");
             }
             else {
                 System.out.println("Physician_text else condition");
                 Physician_text = rset.getString(24);
                 Physician_textBuff.append("<input type=\"checkbox\" id=\"Physician\" name=\"Physician\" checked>");
             }
             if (rset.getString(25) == "" || rset.getString(25) == null || rset.getString(25).equals("")) {
                 System.out.println("OtherText IF condition");
                 Other_text = "";
                 Other_textBuff.append("<input type=\"checkbox\" id=\"Other\" name=\"Other\">");
             }
             else {
                 System.out.println("OtherText else condition");
                 Other_text = rset.getString(25);
                 Other_textBuff.append("<input type=\"checkbox\" id=\"Other\" name=\"Other\" checked>");
             }
         }
         rset.close();
         stmt.close();
         Query = "Select CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName) from "+Database+".PatientReg where ID = " + PatientRegId;
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         if (rset.next()) {
             PatientName = rset.getString(1);
         }
         rset.close();
         stmt.close();
         String Date = "";
         Query = "Select Date_format(now(),'%Y-%m-%d')";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(Query);
         while (rset.next()) {
             Date = rset.getString(1);
         }
         rset.close();
         stmt.close();
         final Parsehtm Parser = new Parsehtm(request);
         Parser.SetField("LastName", String.valueOf(LastName));
         Parser.SetField("FirstName", String.valueOf(FirstName));
         Parser.SetField("MiddleInitial", String.valueOf(MiddleInitial));
         Parser.SetField("TitleBuff", String.valueOf(TitleBuff));
         Parser.SetField("DoctorList", String.valueOf(DoctorList));
         Parser.SetField("MaritalStatusBuff", String.valueOf(MaritalStatusBuff));
         Parser.SetField("DOB", String.valueOf(DOB));
         Parser.SetField("Age", String.valueOf(Age));
         Parser.SetField("genderBuffMale", String.valueOf(genderBuffMale));
         Parser.SetField("genderBuffFemale", String.valueOf(genderBuffFemale));
         Parser.SetField("Address", String.valueOf(Address));
         Parser.SetField("City", String.valueOf(City));
         Parser.SetField("State", String.valueOf(State));
         Parser.SetField("ZipCode", String.valueOf(ZipCode));
         Parser.SetField("PhNumber", String.valueOf(PhNumber));
         Parser.SetField("SSN", String.valueOf(SSN));
         Parser.SetField("Occupation", String.valueOf(Occupation));
         Parser.SetField("Employer", String.valueOf(Employer));
         Parser.SetField("EmpContact", String.valueOf(EmpContact));
         Parser.SetField("PriCarePhy", String.valueOf(PriCarePhy));
         Parser.SetField("Email", String.valueOf(Email));
         Parser.SetField("ReasonVisit", String.valueOf(ReasonVisit));
         Parser.SetField("SelfPayChkBuff", String.valueOf(SelfPayChkBuff));
         Parser.SetField("CountryBuff", String.valueOf(CountryBuff));
         Parser.SetField("WorkersCompPolicyBuffNo", String.valueOf(WorkersCompPolicyBuffNo));
         Parser.SetField("WorkersCompPolicyBuffYes", String.valueOf(WorkersCompPolicyBuffYes));
         Parser.SetField("MotorVehAccidentBuffYes", String.valueOf(MotorVehAccidentBuffYes));
         Parser.SetField("MotorVehAccidentBuffNo", String.valueOf(MotorVehAccidentBuffNo));
         Parser.SetField("PriInsurance", String.valueOf(PriInsurance));
         Parser.SetField("MemId", String.valueOf(MemId));
         Parser.SetField("GrpNumber", String.valueOf(GrpNumber));
         Parser.SetField("PriInsuranceName", String.valueOf(PriInsuranceNameBuff));
         Parser.SetField("AddressIfDifferent", String.valueOf(AddressIfDifferent));
         Parser.SetField("PrimaryDOB", String.valueOf(PrimaryDOB));
         Parser.SetField("PrimarySSN", String.valueOf(PrimarySSN));
         Parser.SetField("PrimaryOccupation", String.valueOf(PrimaryOccupation));
         Parser.SetField("PrimaryEmployer", String.valueOf(PrimaryEmployer));
         Parser.SetField("EmployerAddress", String.valueOf(EmployerAddress));
         Parser.SetField("EmployerPhone", String.valueOf(EmployerPhone));
         Parser.SetField("SecondryInsurance", String.valueOf(SecondryInsuranceBuff));
         Parser.SetField("SubscriberName", String.valueOf(SubscriberName));
         Parser.SetField("SubscriberDOB", String.valueOf(SubscriberDOB));
         Parser.SetField("MemberID_2", String.valueOf(MemberID_2));
         Parser.SetField("GroupNumber_2", String.valueOf(GroupNumber_2));
         Parser.SetField("PatientRelationtoPrimaryBuff", String.valueOf(PatientRelationtoPrimaryBuff));
         Parser.SetField("PatientRelationshiptoSecondryBuff", String.valueOf(PatientRelationshiptoSecondryBuff));
         Parser.SetField("LeaveMessageERBuffNo", String.valueOf(LeaveMessageERBuffNo));
         Parser.SetField("LeaveMessageERBuffYes", String.valueOf(LeaveMessageERBuffYes));
         Parser.SetField("NextofKinName", String.valueOf(NextofKinName));
         Parser.SetField("RelationToPatientER", String.valueOf(RelationToPatientER));
         Parser.SetField("PhoneNumberER", String.valueOf(PhoneNumberER));
         Parser.SetField("AddressER", String.valueOf(AddressER));
         Parser.SetField("CityER", String.valueOf(CityER));
         Parser.SetField("StateER", String.valueOf(StateER));
         Parser.SetField("ZipCodeER", String.valueOf(ZipCodeER));
         Parser.SetField("CountryBuffER", String.valueOf(CountryBuffER));
         Parser.SetField("PatientSignConcent", String.valueOf(PatientSignConcent));
         Parser.SetField("DateConcent", String.valueOf(DateConcent));
         Parser.SetField("WitnessConcent", String.valueOf(WitnessConcent));
         Parser.SetField("PatientBehalfConcent", String.valueOf(PatientBehalfConcent));
         Parser.SetField("RelativeSignConcent", String.valueOf(RelativeSignConcent));
         Parser.SetField("DateConcent2", String.valueOf(DateConcent2));
         Parser.SetField("WitnessConcent2", String.valueOf(WitnessConcent2));
         Parser.SetField("ReturnPatient", String.valueOf(ReturnPatient));
         Parser.SetField("Google", String.valueOf(Google));
         Parser.SetField("MapSearch", String.valueOf(MapSearch));
         Parser.SetField("Billboard", String.valueOf(Billboard));
         Parser.SetField("OnlineReview", String.valueOf(OnlineReview));
         Parser.SetField("TV", String.valueOf(TV));
         Parser.SetField("Website", String.valueOf(Website));
         Parser.SetField("BuildingSignDriveBy", String.valueOf(BuildingSignDriveBy));
         Parser.SetField("Facebook", String.valueOf(Facebook));
         Parser.SetField("School", String.valueOf(School));
         Parser.SetField("School_text", String.valueOf(School_text));
         Parser.SetField("Twitter", String.valueOf(Twitter));
         Parser.SetField("Magazine", String.valueOf(Magazine));
         Parser.SetField("Magazine_text", String.valueOf(Magazine_text));
         Parser.SetField("Newspaper", String.valueOf(Newspaper));
         Parser.SetField("Newspaper_text", String.valueOf(Newspaper_text));
         Parser.SetField("FamilyFriend", String.valueOf(FamilyFriend));
         Parser.SetField("FamilyFriend_text", String.valueOf(FamilyFriend_text));
         Parser.SetField("UrgentCare", String.valueOf(UrgentCare));
         Parser.SetField("UrgentCare_text", String.valueOf(UrgentCare_text));
         Parser.SetField("CommunityEvent", String.valueOf(CommunityEvent));
         Parser.SetField("CommunityEvent_text", String.valueOf(CommunityEvent_text));
         Parser.SetField("Work_textBuff", String.valueOf(Work_textBuff));
         Parser.SetField("Work_text", String.valueOf(Work_text));
         Parser.SetField("Physician_textBuff", String.valueOf(Physician_textBuff));
         Parser.SetField("Physician_text", String.valueOf(Physician_text));
         Parser.SetField("Other_textBuff", String.valueOf(Other_textBuff));
         Parser.SetField("Other_text", String.valueOf(Other_text));
         Parser.SetField("MRN", String.valueOf(MRN));
         if(ClientId == 8){
             Parser.GenerateHtml(out,  "/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditOrange.html");
         }else if(ClientId == 9){
             Parser.GenerateHtml(out,  "/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditVictoria.html");
         }else if(ClientId == 10) {
             Parser.GenerateHtml(out,  "/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditOddasa.html");
         }else if(ClientIndex == 12){
             Parser.GenerateHtml(out,  "/opt/Htmls/oe_2/Forms/Edit/PatientRegForm_EditSAustin.html");
         }
     }
     catch (Exception e) {
         System.out.println(e.getMessage());
         String str = "";
         for (int i = 0; i < e.getStackTrace().length; ++i) {
             str = str + e.getStackTrace()[i] + "<br>";
         }
         out.println(str);
     }
 }
 
 void EditSave(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext,String Database) {
     Statement stmt = null;
     ResultSet rset = null;
     String Query = "";
     final int ClientIndex = 0;
     final int PatientRegId = 0;
     final String ClientId = request.getParameter("ClientId").trim();
     final String MRN = request.getParameter("MRN").trim();
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
     String DoctorName = "";
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
     final String PatientName = "";
     int ID = 0;
     String COVIDStatus = "0";
     String DateofService  = null;
     try {
         if (request.getParameter("Title") == null) {
             Title = "Mr";
         }
         else {
             Title = request.getParameter("Title").trim();
         }
         if (request.getParameter("FirstName") == null) {
             FirstName = "";
         }
         else {
             FirstName = request.getParameter("FirstName").trim();
         }
         if (request.getParameter("LastName") == null) {
             LastName = "Mr";
         }
         else {
             LastName = request.getParameter("LastName").trim();
         }
         if (request.getParameter("MiddleInitial") == null) {
             MiddleInitial = "";
         }
         else {
             MiddleInitial = request.getParameter("MiddleInitial").trim();
         }
         if (request.getParameter("MaritalStatus") == null) {
             MaritalStatus = "";
         }
         else {
             MaritalStatus = request.getParameter("MaritalStatus").trim();
         }
         if (request.getParameter("DOB") == null) {
             DOB = "0000-00-00";
         }
         else {
             DOB = request.getParameter("DOB").trim();
         }
         if (request.getParameter("Age") == null) {
             Age = "0";
         }
         else {
             Age = request.getParameter("Age").trim();
         }
         if (request.getParameter("gender") == null) {
             gender = "male";
         }
         else {
             gender = request.getParameter("gender").trim();
         }
         if (request.getParameter("Email") == null) {
             Email = "";
         }
         else {
             Email = request.getParameter("Email").trim();
         }
         if (request.getParameter("PhNumber") == null) {
             PhNumber = "0";
         }
         else {
             PhNumber = request.getParameter("PhNumber").trim();
         }
         if (request.getParameter("Address") == null) {
             Address = "";
         }
         else {
             Address = request.getParameter("Address").trim();
         }
         if (request.getParameter("City") == null) {
             City = "";
         }
         else {
             City = request.getParameter("City").trim();
         }
         if (request.getParameter("State") == null) {
             State = "";
         }
         else {
             State = request.getParameter("State").trim();
         }
         if (request.getParameter("Country") == null) {
             Country = "";
         }
         else {
             Country = request.getParameter("Country").trim();
         }
         if (request.getParameter("ZipCode") == null) {
             ZipCode = "";
         }
         else {
             ZipCode = request.getParameter("ZipCode").trim();
         }
         if (request.getParameter("SSN") == null) {
             SSN = "";
         }
         else {
             SSN = request.getParameter("SSN").trim();
         }
         if (request.getParameter("Occupation") == null) {
             Occupation = "";
         }
         else {
             Occupation = request.getParameter("Occupation").trim();
         }
         if (request.getParameter("Employer") == null) {
             Employer = "";
         }
         else {
             Employer = request.getParameter("Employer").trim();
         }
         if (request.getParameter("EmpContact") == null) {
             EmpContact = "";
         }
         else {
             EmpContact = request.getParameter("EmpContact").trim();
         }
         if (request.getParameter("PriCarePhy") == null) {
             PriCarePhy = "";
         }
         else {
             PriCarePhy = request.getParameter("PriCarePhy").trim();
         }
         if (request.getParameter("ReasonVisit") == null) {
             ReasonVisit = "";
         }
         else {
             ReasonVisit = request.getParameter("ReasonVisit").trim();
         }
         if (request.getParameter("COVIDStatus_Chk") == null) {
             COVIDStatus = "-1";
         }
         else {
             COVIDStatus = request.getParameter("COVIDStatus_Chk").trim();
         }
         if (request.getParameter("DoctorName") == null) {
             DoctorName = "0";
         }
         else {
             DoctorName = request.getParameter("DoctorName").trim();
         }
         if (request.getParameter("DateofService") == null) {
             DateofService = "now()";
         }
         else {
             DateofService = request.getParameter("DateofService").trim();
         }
         if (request.getParameter("SelfPayChk") == null) {
             SelfPayChk = 0;
         }
         else {
             SelfPayChk = 1;
             if (request.getParameter("WorkersCompPolicy") == null) {
                 WorkersCompPolicy = 0;
             }
             else {
                 WorkersCompPolicy = Integer.parseInt(request.getParameter("WorkersCompPolicy").trim());
             }
             if (request.getParameter("MotorVehAccident") == null) {
                 MotorVehAccident = 0;
             }
             else {
                 MotorVehAccident = Integer.parseInt(request.getParameter("MotorVehAccident").trim());
             }
             if (request.getParameter("PriInsurance") == null) {
                 PriInsurance = "";
             }
             else {
                 PriInsurance = request.getParameter("PriInsurance").trim();
             }
             if (request.getParameter("MemId") == null) {
                 MemId = "";
             }
             else {
                 MemId = request.getParameter("MemId").trim();
             }
             if (request.getParameter("GrpNumber") == null) {
                 GrpNumber = "";
             }
             else {
                 GrpNumber = request.getParameter("GrpNumber").trim();
             }
             if (request.getParameter("PriInsuranceName") == null) {
                 PriInsuranceName = "";
             }
             else {
                 PriInsuranceName = request.getParameter("PriInsuranceName").trim();
             }
             if (request.getParameter("AddressIfDifferent") == null) {
                 AddressIfDifferent = "";
             }
             else {
                 AddressIfDifferent = request.getParameter("AddressIfDifferent").trim();
             }
             if (request.getParameter("PrimaryDOB") == null) {
                 PrimaryDOB = "0000-00-00";
             }
             else {
                 PrimaryDOB = request.getParameter("PrimaryDOB").trim();
             }
//             System.out.println(PrimaryDOB);
             if (request.getParameter("PrimarySSN") == null) {
                 PrimarySSN = "";
             }
             else {
                 PrimarySSN = request.getParameter("PrimarySSN").trim();
             }
             if (request.getParameter("PatientRelationtoPrimary") == null) {
                 PatientRelationtoPrimary = "0";
             }
             else {
                 PatientRelationtoPrimary = request.getParameter("PatientRelationtoPrimary").trim();
             }
             if (request.getParameter("PrimaryOccupation") == null) {
                 PrimaryOccupation = "";
             }
             else {
                 PrimaryOccupation = request.getParameter("PrimaryOccupation").trim();
             }
             if (request.getParameter("PrimaryEmployer") == null) {
                 PrimaryEmployer = "";
             }
             else {
                 PrimaryEmployer = request.getParameter("PrimaryEmployer").trim();
             }
             if (request.getParameter("EmployerAddress") == null) {
                 EmployerAddress = "";
             }
             else {
                 EmployerAddress = request.getParameter("EmployerAddress").trim();
             }
             if (request.getParameter("EmployerPhone") == null) {
                 EmployerPhone = "";
             }
             else {
                 EmployerPhone = request.getParameter("EmployerPhone").trim();
             }
             if (request.getParameter("SecondryInsurance") == null) {
                 SecondryInsurance = "0";
             }
             else {
                 SecondryInsurance = request.getParameter("SecondryInsurance").trim();
             }
             if (request.getParameter("SubscriberName") == null) {
                 SubscriberName = "";
             }
             else {
                 SubscriberName = request.getParameter("SubscriberName").trim();
             }
             if (request.getParameter("SubscriberDOB") == null) {
                 SubscriberDOB = "0000-00-00";
             }
             else {
                 SubscriberDOB = request.getParameter("DOB").trim();
             }
             if (request.getParameter("MemberID_2") == null) {
                 MemberID_2 = "";
             }
             else {
                 MemberID_2 = request.getParameter("MemberID_2").trim();
             }
             if (request.getParameter("GroupNumber_2") == null) {
                 GroupNumber_2 = "";
             }
             else {
                 GroupNumber_2 = request.getParameter("GroupNumber_2").trim();
             }
             if (request.getParameter("PatientRelationshiptoSecondry") == null) {
                 PatientRelationshiptoSecondry = "";
             }
             else {
                 PatientRelationshiptoSecondry = request.getParameter("PatientRelationshiptoSecondry").trim();
             }
         }
         if (request.getParameter("NextofKinName") == null) {
             NextofKinName = "";
         }
         else {
             NextofKinName = request.getParameter("NextofKinName").trim();
         }
         if (request.getParameter("RelationToPatientER") == null) {
             RelationToPatientER = "";
         }
         else {
             RelationToPatientER = request.getParameter("RelationToPatientER").trim();
         }
         if (request.getParameter("PhoneNumberER") == null) {
             PhoneNumberER = "";
         }
         else {
             PhoneNumberER = request.getParameter("PhoneNumberER").trim();
         }
         if (request.getParameter("LeaveMessageER") == null) {
             LeaveMessageER = 0;
         }
         else {
             LeaveMessageER = Integer.parseInt(request.getParameter("LeaveMessageER").trim());
         }
         if (request.getParameter("AddressER") == null) {
             AddressER = "";
         }
         else {
             AddressER = request.getParameter("AddressER").trim();
         }
         if (request.getParameter("CityER") == null) {
             CityER = "";
         }
         else {
             CityER = request.getParameter("CityER").trim();
         }
         if (request.getParameter("StateER") == null) {
             StateER = "";
         }
         else {
             StateER = request.getParameter("StateER").trim();
         }
         if (request.getParameter("CountryER") == null) {
             CountryER = "";
         }
         else {
             CountryER = request.getParameter("CountryER").trim();
         }
         if (request.getParameter("ZipCodeER") == null) {
             ZipCodeER = "";
         }
         else {
             ZipCodeER = request.getParameter("ZipCodeER").trim();
         }
         if (request.getParameter("VerifyChkBox") == null) {
             VerifyChkBox = 0;
         }
         else {
             VerifyChkBox = 1;
         }
         if (request.getParameter("PatientSignConcent") == null) {
             PatientSignConcent = "";
         }
         else {
             PatientSignConcent = request.getParameter("PatientSignConcent").trim();
         }
         if (request.getParameter("DateConcent") == null) {
             DateConcent = "now()";
         }
         else {
             DateConcent = request.getParameter("DateConcent").trim();
         }
         if (request.getParameter("WitnessConcent") == null) {
             WitnessConcent = "";
         }
         else {
             WitnessConcent = request.getParameter("WitnessConcent").trim();
         }
         if (request.getParameter("PatientBehalfConcent") == null) {
             PatientBehalfConcent = "";
         }
         else {
             PatientBehalfConcent = request.getParameter("PatientBehalfConcent").trim();
         }
         if (request.getParameter("RelativeSignConcent") == null) {
             RelativeSignConcent = "";
         }
         else {
             RelativeSignConcent = request.getParameter("RelativeSignConcent").trim();
         }
         if (request.getParameter("DateConcent2") == null) {
             DateConcent2 = "now()";
         }
         else {
             DateConcent2 = request.getParameter("DateConcent2").trim();
         }
         if (request.getParameter("WitnessConcent2") == null) {
             WitnessConcent2 = "";
         }
         else {
             WitnessConcent2 = request.getParameter("WitnessConcent2").trim();
         }
         if (request.getParameter("ReturnPatient") == null) {
             ReturnPatient = 0;
         }
         else {
             ReturnPatient = 1;
         }
         if (request.getParameter("Google") == null) {
             Google = 0;
         }
         else {
             Google = 1;
         }
         if (request.getParameter("MapSearch") == null) {
             MapSearch = 0;
         }
         else {
             MapSearch = 1;
         }
         if (request.getParameter("Billboard") == null) {
             Billboard = 0;
         }
         else {
             Billboard = 1;
         }
         if (request.getParameter("OnlineReview") == null) {
             OnlineReview = 0;
         }
         else {
             OnlineReview = 1;
         }
         if (request.getParameter("TV") == null) {
             TV = 0;
         }
         else {
             TV = 2;
         }
         if (request.getParameter("Website") == null) {
             Website = 0;
         }
         else {
             Website = 1;
         }
         if (request.getParameter("BuildingSignDriveBy") == null) {
             BuildingSignDriveBy = 0;
         }
         else {
             BuildingSignDriveBy = 1;
         }
         if (request.getParameter("Facebook") == null) {
             Facebook = 0;
         }
         else {
             Facebook = 1;
         }
         if (request.getParameter("School") == null) {
             School = 0;
             School_text = "-";
         }
         else {
             School = 1;
             if (request.getParameter("School_text") == null) {
                 School_text = "-";
             }
             else {
                 School_text = request.getParameter("School_text").trim();
             }
         }
         if (request.getParameter("Twitter") == null) {
             Twitter = 0;
         }
         else {
             Twitter = 1;
         }
         if (request.getParameter("Magazine") == null) {
             Magazine = 0;
             Magazine_text = "";
         }
         else {
             Magazine = 1;
             if (request.getParameter("Magazine_text") == null ) {
                 Magazine_text = "";
             }
             else {
                 Magazine_text = request.getParameter("Magazine_text").trim();
             }
         }
         if (request.getParameter("Newspaper") == null) {
             Newspaper = 0;
             Newspaper_text = "";
         }
         else {
             Newspaper = 1;
             if (request.getParameter("Newspaper_text") == null) {
                 Newspaper_text = "";
             }
             else {
                 Newspaper_text = request.getParameter("Newspaper_text").trim();
             }
         }
         if (request.getParameter("FamilyFriend") == null) {
             FamilyFriend = 0;
             FamilyFriend_text = "";
         }
         else {
             FamilyFriend = 1;
             if (request.getParameter("FamilyFriend_text") == null) {
                 FamilyFriend_text = "";
             }
             else {
                 FamilyFriend_text = request.getParameter("FamilyFriend_text").trim();
             }
         }
         if (request.getParameter("UrgentCare") == null) {
             UrgentCare = 0;
             UrgentCare_text = "";
         }
         else {
             UrgentCare = 1;
             if (request.getParameter("UrgentCare_text") == null) {
                 UrgentCare_text = "";
             }
             else {
                 UrgentCare_text = request.getParameter("UrgentCare_text").trim();
             }
         }
         if (request.getParameter("CommunityEvent") == null) {
             CommunityEvent = 0;
             CommunityEvent_text = "";
         }
         else {
             CommunityEvent = 1;
             if (request.getParameter("CommunityEvent_text") == null) {
                 CommunityEvent_text = "";
             }
             else {
                 CommunityEvent_text = request.getParameter("CommunityEvent_text").trim();
             }
         }
         if (request.getParameter("Work") == null) {
             Work = 0;
             Work_text = "";
         }
         else {
             Work = 1;
             if (request.getParameter("Work_text") == null) {
                 Work_text = "";
             }
             else {
                 Work_text = request.getParameter("Work_text").trim();
             }
         }
         if (request.getParameter("Physician") == null) {
             Physician = 0;
             Physician_text = "";
         }
         else {
             Physician = 1;
             if (request.getParameter("Physician_text") == null) {
                 Physician_text = "";
             }
             else {
                 Physician_text = request.getParameter("Physician_text").trim();
             }
         }
         if (request.getParameter("Other") == null) {
             Other = 0;
             Other_text = "";
         }
         else {
             Other = 1;
             if (request.getParameter("Other_text") == null) {
                 Other_text = "";
             }
             else {
                 Other_text = request.getParameter("Other_text").trim();
             }
         }
         int MaxVisitNumber= 0;
         try {
             Query = "Select ID from "+Database+".PatientReg where MRN = " + MRN;
             stmt = conn.createStatement();
             rset = stmt.executeQuery(Query);
             while (rset.next()) {
                 ID = rset.getInt(1);
             }
             rset.close();
             stmt.close();
         }
         catch (Exception e) {
             out.println("Error in getting PatineReg Data get:--" + e.getMessage() + Query);
         }
         try {
             Query = "UPDATE "+Database+".PatientReg SET Title ='" + Title + "', FirstName = '" + FirstName + "', LastName = '" + LastName + "', MiddleInitial = '" + MiddleInitial + "', " + " DOB = '" + DOB + "', Age = '" + Age + "', Gender = '" + gender + "', Email = '" + Email + "', PhNumber = '" + PhNumber + "', Address = '" + Address + "', City = '" + City + "', " + " State = '" + State + "', Country = '" + Country + "', ZipCode = '" + ZipCode + "', SSN = '" + SSN + "', Occupation = '" + Occupation + "', Employer = '" + Employer + "', " + " EmpContact = '" + EmpContact + "', PriCarePhy = '" + PriCarePhy + "', ReasonVisit = '" + ReasonVisit + "', SelfPayChk = '" + SelfPayChk + "', MaritalStatus = '" + MaritalStatus + "', " + " COVIDStatus = '" + COVIDStatus + "', DoctorsName = '"+DoctorName+"', DateofService = '"+DateofService+"' WHERE ID = " + ID;
             stmt = conn.createStatement();
             stmt.executeUpdate(Query);
             stmt.close();
         }
         catch (Exception e) {
             out.println("Error in Updating PatientReg Table:-" + e.getMessage());
         }

         try{
             Query = "Select max(VisitNumber) from "+Database+".PatientVisit where PatientRegId = "+ ID;
             stmt = conn.createStatement();
             rset = stmt.executeQuery(Query);
             while (rset.next()) {
                 MaxVisitNumber = rset.getInt(1);
             }
             rset.close();
             stmt.close();
         }catch(Exception e){
             out.println("Error in getting VisitNumber From PatientVisit" + e.getMessage());
         }

         try {
             Query = "UPDATE "+Database+".PatientVisit SET ReasonVisit ='" + ReasonVisit + "', DoctorId = '" + DoctorName + "', DateofService = '" + DateofService + "' WHERE PatientRegId = " + ID + " and VisitNumber = "+ MaxVisitNumber;
             stmt = conn.createStatement();
             stmt.executeUpdate(Query);
             stmt.close();
         }
         catch (Exception e) {
             out.println("Error in Updating PatientVisit Table:-" + e.getMessage());
         }

         try {
             if (SelfPayChk == 1) {
                 Query = "UPDATE "+Database+".InsuranceInfo SET WorkersCompPolicy = " + WorkersCompPolicy + ", MotorVehAccident = " + MotorVehAccident + ", PriInsurance = '" + PriInsurance + "', " + " MemId = '" + MemId + "', GrpNumber = '" + GrpNumber + "', PriInsuranceName = '" + PriInsuranceName + "', AddressIfDifferent = '" + AddressIfDifferent + "', " + " PrimaryDOB = '" + PrimaryDOB + "', PrimarySSN = '" + PrimarySSN + "', PatientRelationtoPrimary = '" + PatientRelationtoPrimary + "', PrimaryOccupation = '" + PrimaryOccupation + "', " + " PrimaryEmployer = '" + PrimaryEmployer + "', EmployerAddress = '" + EmployerAddress + "', EmployerPhone = '" + EmployerPhone + "', SecondryInsurance = '" + SecondryInsurance + "', " + " SubscriberName = '" + SubscriberName + "', SubscriberDOB = '" + SubscriberDOB + "', MemberID_2 = '" + MemberID_2 + "', GroupNumber_2 = '" + GroupNumber_2 + "', " + " PatientRelationshiptoSecondry = '" + PatientRelationshiptoSecondry + "' WHERE PatientRegId = " + ID;
                 stmt = conn.createStatement();
                 stmt.executeUpdate(Query);
                 stmt.close();
             }
         }
         catch (Exception e) {
             out.println("Error in Updating Insurance info Table :--" + e.getMessage());
         }
         try {
             Query = "Update "+Database+".EmergencyInfo set NextofKinName = '" + NextofKinName + "', RelationToPatient = '" + RelationToPatientER + "', PhoneNumber = '" + PhoneNumberER + "', " + " LeaveMessage = " + LeaveMessageER + ", Address = '" + AddressER + "', City = '" + CityER + "', State = '" + StateER + "', Country = '" + CountryER + "', ZipCode = '" + ZipCodeER + "' " + " where PatientRegId = " + ID;
             stmt = conn.createStatement();
             stmt.executeUpdate(Query);
             stmt.close();
         }
         catch (Exception e) {
             out.println("Error in Updating Emergency Info Table:-- " + e.getMessage());
         }
         try {
             Query = "Update "+Database+".ConcentToTreatmentInfo set PatientSign = '" + PatientSignConcent + "', Date = '" + DateConcent + "', Witness = '" + WitnessConcent + "', " + " PatientBehalfSign = '" + PatientBehalfConcent + "', RelativeSign = '" + RelativeSignConcent + "', Date2 = '" + DateConcent2 + "', Witness2 = '" + WitnessConcent2 + "' " + "Where PatientRegId = " + ID;
             stmt = conn.createStatement();
             stmt.executeUpdate(Query);
             stmt.close();
         }
         catch (Exception e) {
             out.println("Error in Updating ConcentToTreatmentInfo Table:-- " + e.getMessage());
         }
         try {
             Query = "Update "+Database+".RandomCheckInfo set ReturnPatient = " + ReturnPatient + ", Google = " + Google + ", MapSearch = " + MapSearch + ", Billboard = " + Billboard + ", " + " OnlineReview = " + OnlineReview + ", TV = " + TV + ", Website = " + Website + ", BuildingSignDriveBy = " + BuildingSignDriveBy + ", Facebook = " + Facebook + ", " + " School = " + School + ", School_text = '" + School_text + "', Twitter = " + Twitter + ", Magazine = " + Magazine + ", Magazine_text = '" + Magazine_text + "', " + " Newspaper = " + Newspaper + ", Newspaper_text = '" + Newspaper_text + "', FamilyFriend = " + FamilyFriend + ", FamilyFriend_text = '" + FamilyFriend_text + "', " + " UrgentCare = " + UrgentCare + ", UrgentCare_text = '" + UrgentCare_text + "', CommunityEvent = " + CommunityEvent + ", CommunityEvent_text = '" + CommunityEvent_text + "', " + " Work_text = '" + Work_text + "', Physician_text = '" + Physician_text + "', Other_text = '" + Other_text + "' where PatientRegId = " + ID;
             stmt = conn.createStatement();
             stmt.executeUpdate(Query);
             stmt.close();
         }
         catch (Exception e) {
             out.println("Error in Updating RandomCheckInfo Table:-- " + e.getMessage());
         }
         final Parsehtm Parser = new Parsehtm(request);
         Parser.SetField("PatientName", String.valueOf(PatientName));
         Parser.GenerateHtml(out, "/opt/Htmls/oe_2/Exception/Message_Success.html");
     }
     catch (Exception e) {
         out.println("Error in Updating:-" + e.getMessage());
         String str = "";
         for (int i = 0; i < e.getStackTrace().length; ++i) {
             str = str + e.getStackTrace()[i] + "<br>";
         }
         out.println(str);
     }
 }
}
