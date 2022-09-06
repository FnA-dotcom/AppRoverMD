package orange_2;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

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

public class DownloadBundle
  extends HttpServlet
{
  public void init(ServletConfig config)
    throws ServletException
  {
    super.init(config);
  }
  
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    handleRequest(request, response);
  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    handleRequest(request, response);
  }
  
  public void handleRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    Connection conn = null;
    ResultSet rset = null;
    Statement stmt = null;
    String Query = "";
    String Database = "";
    String UserId = "";String Zone = ""; String Passwd = "";
    String ActionID = request.getParameter("ActionID").trim();
    response.setContentType("text/html");
    PrintWriter out = new PrintWriter(response.getOutputStream());
    Services supp = new Services();
    ServletContext context = null;
    context = getServletContext();
    conn = Services.getMysqlConn(context);
    int ClientId = 0;

    try {
      Cookie[] cookies = request.getCookies();
      Zone = UserId = Passwd = "";
      int checkCookie = 0;
      for (int coky = 0; coky < cookies.length; coky++) {
        String cName = cookies[coky].getName();
        String cValue = cookies[coky].getValue();
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

      Query = "Select dbname from oe.clients where Id = "+ClientId;
      stmt = conn.createStatement();
      rset = stmt.executeQuery(Query);
      while (rset.next()) {
        Database = rset.getString(1);
      }
      rset.close();
      stmt.close();

//      if (ClientId == 8) {
//        Database = "oe_2";//orange
//      } else if (ClientId == 9) {
//        Database = "victoria";//victoria
//      } else if (ClientId == 10) {
//        Database = "oddasa";//oddasa
//      }

    }catch(Exception e){
      out.println(e.getMessage());
  }
    if (ActionID.equals("GETINPUT")) {
      supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Orange Admission Bundle", "Download or View Admission Bundle", ClientId);
      GETINPUT(request, out, conn, context, response, UserId, Database, ClientId);
    }else if (ActionID.equals("GETINPUTSAustin")) {
      supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "South Austin Admission Bundle", "Download or View Admission Bundle", ClientId);
      GETINPUTSAustin(request, out, conn, context, response, UserId, Database, ClientId);
    }else if (ActionID.equals("GETINPUTVictoria")) {
      supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Victoria Admission Bundle", "Download or View Admission Bundle", ClientId);
      GETINPUTVictoria(request, out, conn, context, response, UserId, Database, ClientId);
    }else if (ActionID.equals("GETINPUTOddasa")) {
      supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Odessa Admission Bundle", "Download or View Admission Bundle", ClientId);
      GETINPUTOddasa(request, out, conn, context, response, UserId, Database, ClientId);
    }
    try
    {
      conn.close();
    }
    catch (Exception var11) {}
    out.flush();
    out.close();
  }

  void GETINPUT(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId)
  {
    Statement stmt = null;
    ResultSet rset = null;
    String Query = "";
    int PatientRegId = 0 ;
    String DateTime = "";
    String Date = "";String Time = "";
    //------------------Initial Info Div Variables------------------
    String Title = "";String FirstName = "";String FirstNameNoSpaces = "";String LastName = "";String MiddleInitial = "";String MaritalStatus = "";String DOB = "";
    String Age = "";String gender = "";String Email = "";String PhNumber = "";String Address = "";String CityStateZip = "";String State = "";
    String Country = "";String ZipCode = "";String SSN = "";String Occupation = "";String Employer = "";String EmpContact = "";
    String PriCarePhy = "";String ReasonVisit = "";String MRN = "";int ClientIndex = 0;String ClientName = "";String DOS = "";String DoctorId = null;
    String DoctorName = null;
    //----------------Insuarnce Dive Wariable------------------
    int WorkersCompPolicy = 0;String WorkersCompPolicyString =  "Is this a worker’s comp policy: YES/NO";int MotorVehAccident = 0;
    String MotorVehAccidentString = "Is this a Motor Vehicle Accident : YES/NO";String PriInsurance = "";
    String MemId = "";String GrpNumber = "";String PriInsuranceName = "";String AddressIfDifferent = "";String PrimaryDOB = "";
    String PrimarySSN = "";String PatientRelationtoPrimary = "";String PrimaryOccupation = "";String PrimaryEmployer = "";
    String EmployerAddress = "";String EmployerPhone = "";String SecondryInsurance = "";String SubscriberName = "";String SubscriberDOB = "";
    String MemberID_2 = "";String GroupNumber_2 = "";String PatientRelationshiptoSecondry = "";
    //------------------EmergencyDiv Info Variables------------------
    String NextofKinName = "";String RelationToPatientER = "";String PhoneNumberER = "";int LeaveMessageER = 0;String AddressER = "";
    String CityER = "";String StateER = "";String LeaveMessageERString = "";String CityStateZipER = "";
    String CountryER = "";String ZipCodeER = "";
    //--------------Conecnt To Treatment Info varaible-----------------
    String DateConcent = "";String WitnessConcent = "";String PatientBehalfConcent = "";String RelativeSignConcent = "";String DateConcent2 = "";
    String WitnessConcent2 = "";String PatientSignConcent = "";
    //-----------------Some Random checks Info Variables---------------
    String ReturnPatient = "";String Google = "";String MapSearch = "";
    String Billboard = "";String OnlineReview = "";String TV = "";
    String Website = "";String BuildingSignDriveBy = "";String Facebook = "";
    String School = "";String School_text = "";String Twitter = "";String Magazine = "";String Magazine_text = "";
    String Newspaper = "";String Newspaper_text = "";String FamilyFriend = "";String FamilyFriend_text = "";String UrgentCare = "";String UrgentCare_text= "";
    String CommunityEvent = "";String CommunityEvent_text = "";String Work = "";String Work_text = "";String Physician = "";String Physician_text = "";
    String Other = "";String Other_text = "";
    //----------------------Some Check to Verify if set or not----------------------
    int SelfPayChk = 0;int VerifyChkBox = 0;

    int ID = Integer.parseInt(request.getParameter("ID").trim());

    try{
      Query = "select date_format(now(),'%Y%m%d%H%i%s'), DATE_FORMAT(now(), '%m/%d/%Y'), DATE_FORMAT(now(), '%T')";
      stmt = conn.createStatement();
      rset = stmt.executeQuery(Query);
      if(rset.next()) {
        DateTime = rset.getString(1);
        Date = rset.getString(2);
        Time = rset.getString(3);
      }
      rset.close();
      stmt.close();

      try {
        Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), IFNULL(MaritalStatus, '-'), " +
                " IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'), " +
                " IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-')," +
                " IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'), " +
                " IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), IFNULL(DoctorsName,'-') " +
                " From " + Database + ".PatientReg Where ID = " + ID;
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
      }catch(Exception e){
        out.println("Error In PateintReg:--"+e.getMessage());
        out.println(Query);
      }
      if(SelfPayChk == 1){
        Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'), " +
                " IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%m/%d/%Y'),'-'), IFNULL(PrimarySSN,'-'), " +
                " IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'), " +
                " IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'-'), " +
                " IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from "+Database+".InsuranceInfo " +
                " where PatientRegId = "+ID;
        stmt = conn.createStatement();
        rset = stmt.executeQuery(Query);
        while (rset.next()) {
          WorkersCompPolicy = rset.getInt(1);
          MotorVehAccident = rset.getInt(2);
          if(WorkersCompPolicy == 0){
            WorkersCompPolicyString = "Is this a worker’s comp policy: NO";
          }else{
            WorkersCompPolicyString = "Is this a worker’s comp policy: YES";
          }
          if(MotorVehAccident == 0){
            MotorVehAccidentString = "Is this a Motor Vehicle Accident : NO";
          }else{
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

      Query = "Select IFNULL(NextofKinName,'-'), IFNULL(RelationToPatient,'-'), IFNULL(PhoneNumber,'-'), " +
              "CASE WHEN LeaveMessage = 1 THEN 'YES' WHEN LeaveMessage = 0 THEN 'NO' ELSE ' YES / NO'END, " +
              " IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-') from "+Database+".EmergencyInfo where PatientRegId = "+ID;
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

      Query = " Select ReturnPatient, Google, MapSearch, Billboard, OnlineReview, TV, Website, BuildingSignDriveBy, Facebook, School," + //10
              " IFNULL(School_text ,'-'), Twitter, Magazine, IFNULL(Magazine_text,'-'), Newspaper, IFNULL(Newspaper_text,'-'), FamilyFriend," +//17
              " IFNULL(FamilyFriend_text,'-'), UrgentCare, IFNULL(UrgentCare_text,'-'), CommunityEvent, IFNULL(CommunityEvent_text,'-'), " +//22
              " IFNULL(Work_text,'-'), IFNULL(Physician_text, '-'), IFNULL(Other_text,'-') from "+Database+".RandomCheckInfo where PatientRegId = "+ID;//25
      stmt = conn.createStatement();
      rset = stmt.executeQuery(Query);
      while (rset.next()) {
        if(rset.getInt(1) == 0){
          ReturnPatient = "";
        }else{
          ReturnPatient = "YES";
        }
        if(rset.getInt(2) == 0){
          Google = "";
        }else{
          Google = "YES";
        }
        if(rset.getInt(3) == 0){
          MapSearch = "";
        }else{
          MapSearch = "YES";
        }
        if(rset.getInt(4) == 0){
          Billboard = "";
        }else{
          Billboard = "YES";
        }
        if(rset.getInt(5) == 0){
          OnlineReview = "";
        }else{
          OnlineReview = "YES";
        }
        if(rset.getInt(6) == 0){
          TV = "";
        }else{
          TV = "YES";
        }
        if(rset.getInt(7) == 0){
          Website = "";
        }else{
          Website = "YES";
        }
        if(rset.getInt(8) == 0){
          BuildingSignDriveBy = "";
        }else{
          BuildingSignDriveBy = "YES";
        }
        if(rset.getInt(9) == 0){
          Facebook = "";
        }else{
          Facebook = "YES";
        }
        if(rset.getInt(10) == 0){
          School = "";
          School_text = "";
        }else{
          School = "YES";
          School_text = rset.getString(11);
        }
        if(rset.getInt(12) == 0){
          Twitter = "";
        }else{
          Twitter = "YES";
        }
        if(rset.getInt(13) == 0){
          Magazine = "";
          Magazine_text = "";
        }else{
          Magazine = "YES";
          Magazine_text = rset.getString(14);
        }
        if(rset.getInt(15) == 0){
          Newspaper = "";
          Newspaper_text = "";
        }else{
          Newspaper = "YES";
          Newspaper_text = rset.getString(16);
        }
        if(rset.getInt(17) == 0){
          FamilyFriend = "";
          FamilyFriend_text = "";
        }else{
          FamilyFriend = "YES";
          FamilyFriend_text = rset.getString(18);
        }
        if(rset.getInt(19) == 0){
          UrgentCare = "";
          UrgentCare_text = "";
        }else{
          UrgentCare = "YES";
          UrgentCare_text = rset.getString(20);
        }
        if(rset.getInt(21) == 0){
          CommunityEvent = "";
          CommunityEvent_text = "";
        }else{
          CommunityEvent = "YES";
          CommunityEvent_text = rset.getString(22);
        }
        if(rset.getString(23) == "" || rset.getString(23) == null){
          Work_text = "";
        }else{
          Work_text = rset.getString(23);
        }
        if(rset.getString(24) == "" || rset.getString(24) == null){
          Physician_text = "";
        }else{
          Physician_text = rset.getString(24);
        }
        if(rset.getString(25) == "" || rset.getString(25) == null){
          Other_text = "";
        }else{
          Other_text = rset.getString(25);
        }
      }
      rset.close();
      stmt.close();

//      out.println("Other_text-:"+Other_text);
//      out.println("CommunityEvent-:"+CommunityEvent+"CommunityEvent_text-:"+CommunityEvent_text);
//      out.println("School_text-:"+School_text+"School_text:-"+School_text);
//      out.println("ReturnPatient-:"+ReturnPatient);
//      out.println("MotorVehAccidentString-:"+MotorVehAccidentString);
//      out.println("LastName-:"+LastName);
//      out.println("CityStateZipER-:"+CityStateZipER);


      String inputFilePath = "";
      if(ClientId == 8){
        inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/Admin.pdf"; // Existing file
      }else if(ClientId == 9){
        inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/adminvictoria.pdf"; // Existing file
      }else if(ClientId == 10){
        inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/adminoddasa.pdf"; // Existing file
      }

      String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Orange/"+FirstNameNoSpaces+LastName+ID+"_"+DateTime+".pdf"; // New file
      OutputStream fos = new FileOutputStream(new File(outputFilePath));

      //0, 800 will write text on TOP LEFT of pdf page
      //0, 0 will write text on BOTTOM LEFT of pdf page


      PdfReader pdfReader = new PdfReader(inputFilePath);
      PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

      GenerateBarCode barCode = new GenerateBarCode();
      String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);

      Image image = Image.getInstance(BarCodeFilePath);
      image.scaleAbsolute(150, 30); //Scale image's width and height

      for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
        if (i == 1) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);


          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 535); // set x and y co-ordinates
          pdfContentByte.showText(ReturnPatient); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 520); // set x and y co-ordinates
          pdfContentByte.showText(Google); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 500); // set x and y co-ordinates
          pdfContentByte.showText(MapSearch); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 485); // set x and y co-ordinates
          pdfContentByte.showText(Billboard); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 465); // set x and y co-ordinates
          pdfContentByte.showText(OnlineReview); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 450); // set x and y co-ordinates
          pdfContentByte.showText(TV); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 435); // set x and y co-ordinates
          pdfContentByte.showText(Website); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 415); // set x and y co-ordinates
          pdfContentByte.showText(BuildingSignDriveBy); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 398); // set x and y co-ordinates
          pdfContentByte.showText(Facebook); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 380); // set x and y co-ordinates
          pdfContentByte.showText(School); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(190, 380); // set x and y co-ordinates
          pdfContentByte.showText(School_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 365); // set x and y co-ordinates
          pdfContentByte.showText(Twitter); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 348); // set x and y co-ordinates
          pdfContentByte.showText(Magazine); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(200, 348); // set x and y co-ordinates
          pdfContentByte.showText(Magazine_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 330); // set x and y co-ordinates
          pdfContentByte.showText(Newspaper); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(220, 330); // set x and y co-ordinates
          pdfContentByte.showText(Newspaper_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 312); // set x and y co-ordinates
          pdfContentByte.showText(FamilyFriend); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(220, 312); // set x and y co-ordinates
          pdfContentByte.showText(FamilyFriend_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 295); // set x and y co-ordinates
          pdfContentByte.showText(UrgentCare); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(220, 295); // set x and y co-ordinates
          pdfContentByte.showText(UrgentCare_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 278); // set x and y co-ordinates
          pdfContentByte.showText(CommunityEvent); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(250, 278); // set x and y co-ordinates
          pdfContentByte.showText(CommunityEvent_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(150, 225); // set x and y co-ordinates
          pdfContentByte.showText(Work_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(180, 210); // set x and y co-ordinates
          pdfContentByte.showText(Physician_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(150, 195); // set x and y co-ordinates
          pdfContentByte.showText(Other_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(80, 85); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(480, 85); // set x and y co-ordinates
          pdfContentByte.showText(" "); // add the text
          pdfContentByte.endText();
        }

        if (i == 2) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

          // Add text in existing PDF
          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setTextMatrix(105, 640); // set x and y co-ordinates
          pdfContentByte.showText(LastName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(350, 640); // set x and y co-ordinates
          pdfContentByte.showText(FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(500, 640); // set x and y co-ordinates
          pdfContentByte.showText(MiddleInitial); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 605); // set x and y co-ordinates
          pdfContentByte.showText("Title: " + Title); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(200, 600); // set x and y co-ordinates
          pdfContentByte.showText(MaritalStatus); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(340, 600); // set x and y co-ordinates
          pdfContentByte.showText(DOB); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(440, 600); // set x and y co-ordinates
          pdfContentByte.showText(Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(510, 600); // set x and y co-ordinates
          pdfContentByte.showText(gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 570); // set x and y co-ordinates
          pdfContentByte.showText(Address); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(260, 570); // set x and y co-ordinates
          pdfContentByte.showText(CityStateZip); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(460, 570); // set x and y co-ordinates
          pdfContentByte.showText(PhNumber); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(50, 540); // set x and y co-ordinates
          pdfContentByte.showText(SSN); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(170, 540); // set x and y co-ordinates
          pdfContentByte.showText(Occupation); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(350, 540); // set x and y co-ordinates
          pdfContentByte.showText(Employer); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(470, 540); // set x and y co-ordinates
          pdfContentByte.showText(EmployerPhone); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 510); // set x and y co-ordinates
          pdfContentByte.showText(PriCarePhy); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(200, 510); // set x and y co-ordinates
          pdfContentByte.showText(Email); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(400, 510); // set x and y co-ordinates
          pdfContentByte.showText(ReasonVisit); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 445); // set x and y co-ordinates
          pdfContentByte.showText(WorkersCompPolicyString); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(330, 445); // set x and y co-ordinates
          pdfContentByte.showText(MotorVehAccidentString); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 415); // set x and y co-ordinates
          pdfContentByte.showText(PriInsurance); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(280, 415); // set x and y co-ordinates
          pdfContentByte.showText(MemId); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(420, 415); // set x and y co-ordinates
          pdfContentByte.showText(GrpNumber); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 375); // set x and y co-ordinates
          pdfContentByte.showText(PriInsuranceName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(260, 375); // set x and y co-ordinates
          pdfContentByte.showText(AddressIfDifferent); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(460, 375); // set x and y co-ordinates
          pdfContentByte.showText(CityStateZip); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 335); // set x and y co-ordinates
          pdfContentByte.showText(PrimaryDOB); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(160, 335); // set x and y co-ordinates
          pdfContentByte.showText(PrimarySSN); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(270, 335); // set x and y co-ordinates
          pdfContentByte.showText(PatientRelationtoPrimary); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(460, 335); // set x and y co-ordinates
          pdfContentByte.showText(PhNumber); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 300); // set x and y co-ordinates
          pdfContentByte.showText(PrimaryOccupation); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(160, 300); // set x and y co-ordinates
          pdfContentByte.showText(PrimaryEmployer); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(260, 300); // set x and y co-ordinates
          pdfContentByte.showText(EmployerAddress); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(460, 300); // set x and y co-ordinates
          pdfContentByte.showText(EmployerPhone); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 275); // set x and y co-ordinates
          pdfContentByte.showText(SecondryInsurance); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(260, 275); // set x and y co-ordinates
          pdfContentByte.showText(SubscriberName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(420, 275); // set x and y co-ordinates
          pdfContentByte.showText(SubscriberDOB); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(60, 240); // set x and y co-ordinates
          pdfContentByte.showText(PatientRelationshiptoSecondry); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(260, 240); // set x and y co-ordinates
          pdfContentByte.showText(MemberID_2); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(420, 240); // set x and y co-ordinates
          pdfContentByte.showText(GroupNumber_2); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(50, 190); // set x and y co-ordinates
          pdfContentByte.showText(NextofKinName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(200, 190); // set x and y co-ordinates
          pdfContentByte.showText(RelationToPatientER); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(350, 190); // set x and y co-ordinates
          pdfContentByte.showText(PhoneNumberER); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(510, 190); // set x and y co-ordinates
          pdfContentByte.showText(LeaveMessageERString); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 150); // set x and y co-ordinates
          pdfContentByte.showText(AddressER); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(350, 150); // set x and y co-ordinates
          pdfContentByte.showText(CityStateZipER); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(440, 75); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();
        }

        if (i == 3) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(325, 210); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(340, 130); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();
        }

        if (i == 4) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(285, 70); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();
        }

        if (i == 5) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(440, 395); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(330, 250); // set x and y co-ordinates
          pdfContentByte.showText(" "); // add the text Here Name of the Qualified Personal
          pdfContentByte.endText();
        }

        if (i == 6) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(95, 585); // set x and y co-ordinates
          pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(300, 585); // set x and y co-ordinates
          pdfContentByte.showText(DOB); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(490, 585); // set x and y co-ordinates
          pdfContentByte.showText(PatientRelationtoPrimary); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(120, 560); // set x and y co-ordinate
          pdfContentByte.showText(PriInsuranceName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(180, 535); // set x and y co-ordinates
          pdfContentByte.showText(MemId); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 510); // set x and y co-ordinates
          pdfContentByte.showText(DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 385); // set x and y co-ordinates
          pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName);
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size

          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(330, 210); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();
        }

        if (i == 7) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(130, 490); // set x and y co-ordinates
          pdfContentByte.showText(" "); // add the text Health Insurance
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 465); // set x and y co-ordinates
          pdfContentByte.showText(SubscriberName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(340, 465); // set x and y co-ordinates
          pdfContentByte.showText(SubscriberDOB); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 440); // set x and y co-ordinates
          pdfContentByte.showText(MemberID_2); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(260, 440); // set x and y co-ordinates
          pdfContentByte.showText(GroupNumber_2); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(120, 415); // set x and y co-ordinates
          pdfContentByte.showText(" "); // add the text Effective Date???
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(50, 240); // set x and y co-ordinates
          pdfContentByte.showText(" "); // add the text Printed Name of the person completing form????
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(430, 170); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(430, 130); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();
        }

        if (i == 8) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(50, 650); // set x and y co-ordinates
          pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 650); // set x and y co-ordinates
          pdfContentByte.showText(PhNumber); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(50, 610); // set x and y co-ordinates
          pdfContentByte.showText(DOB); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 610); // set x and y co-ordinates
          pdfContentByte.showText(SSN); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(130, 480); // set x and y co-ordinates
          pdfContentByte.showText(" "); // add the text Facility or Physician to receive information???
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(130, 440); // set x and y co-ordinates
          pdfContentByte.showText(""); // add the text PHONE NUMBER???
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(340, 440); // set x and y co-ordinates
          pdfContentByte.showText(""); // add the text FAX NUMBER???
          pdfContentByte.endText();


          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(130, 400); // set x and y co-ordinates
          pdfContentByte.showText(""); // add the text Address???
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(130, 360); // set x and y co-ordinates
          pdfContentByte.showText(""); // add the text CityStateZip???
          pdfContentByte.endText();

        }

        if (i == 9) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();
        }

        if (i == 10) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();
        }

        if (i == 11) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();
        }

      }
      pdfStamper.close(); //close pdfStamper


//      response.setContentType("application/pdf");
//      out = response.getWriter();
//      String filepath = outputFilePath;
//      response.setHeader("Content-Disposition", "attachment; filename=" + filepath + ";");
//      FileInputStream fileOut = new FileInputStream(FirstNameNoSpaces+LastName+ID+"_"+DateTime+".pdf");
//      fileOut.close();
//      out.close();

      File pdfFile = new File(outputFilePath);
      response.setContentType("application/pdf");
      response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces+LastName+ID+"_"+DateTime+".pdf");
      response.setContentLength((int) pdfFile.length());

      FileInputStream fileInputStream = new FileInputStream(pdfFile);
      OutputStream responseOutputStream = response.getOutputStream();
      int bytes;
      while ((bytes = fileInputStream.read()) != -1) {
        responseOutputStream.write(bytes);
      }

    }catch(Exception e){
      out.println(e.getMessage());
    }
  }


  void GETINPUTVictoria(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId)
  {
    Statement stmt = null;
    ResultSet rset = null;
    String Query = "";
    int PatientRegId = 0 ;
    String DateTime = "";
    String Date = "";String Time = "";
    MergePdf mergePdf = new MergePdf();
    int ID = Integer.parseInt(request.getParameter("ID").trim());
    String Path1 = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/VictoriaPdf/";
    String Path2 = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/VictoriaPdf/";
    String ResultPdf = "";
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
    String EmployementChk = null;
    String Employer = null;
    String Occupation = null;
    String EmpContact = null;
    String PrimaryCarePhysicianChk = null;
    String PriCarePhy = null;
    String ReasonVisit = null;
    String PriCarePhyAddress = null;
    String PriCarePhyAddress2 = null;
    String PriCarePhyCity = null;
    String PriCarePhyState = null;
    String PriCarePhyZipCode = null;
    String PatientMinorChk = null;
    String GuarantorChk = null;
    String GuarantorEmployer = null;
    String GuarantorEmployerAreaCode = null;
    String GuarantorEmployerPhNumber = null;
    String GuarantorEmployerAddress = null;
    String GuarantorEmployerAddress2 = null;
    String GuarantorEmployerCity = null;
    String GuarantorEmployerState = null;
    String GuarantorEmployerZipCode = null;
    int WorkersCompPolicyChk = 0;
    String WCPDateofInjury = null;
    String WCPCaseNo = null;
    String WCPGroupNo = null;
    String WCPMemberId = null;
    String WCPInjuryRelatedAutoMotorAccident = null;
    String WCPInjuryRelatedWorkRelated = null;
    String WCPInjuryRelatedOtherAccident = null;
    String WCPInjuryRelatedNoAccident = null;
    String WCPInjuryOccurVehicle = null;
    String WCPInjuryOccurWork = null;
    String WCPInjuryOccurHome = null;
    String WCPInjuryOccurOther = null;
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
    int MotorVehicleAccidentChk = 0;
    String AutoInsuranceInformationChk = null;
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
    int HealthInsuranceChk = 0;
    String GovtFundedInsurancePlanChk = null;
    int GFIPMedicare = 0;
    int GFIPMedicaid = 0;
    int GFIPCHIP = 0;
    int GFIPTricare = 0;
    int GFIPVHA = 0;
    int GFIPIndianHealth = 0;
    String InsuranceSubPatient = null;
    String InsuranceSubGuarantor = null;
    String InsuranceSubOther = null;
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

    //------------------Initial Info Div Variables------------------
//    String Title = "";String FirstName = "";
    String FirstNameNoSpaces = "";
//    String LastName = "";String MiddleInitial = "";String MaritalStatus = "";String DOB = "";
//    String Age = "";String gender = "";String Email = "";String PhNumber = "";String Address = "";
    String CityStateZip = "";
//    String State = "";
    String Country = "";
//    String ZipCode = "";String SSN = "";String Occupation = "";String Employer = "";String EmpContact = "";
//    String PriCarePhy = "";String ReasonVisit = "";
    String MRN = "";int ClientIndex = 0;String ClientName = "";String DOS = "";String DoctorId = null;
    String DoctorName = null;
    //----------------Insuarnce Dive Wariable------------------
    int WorkersCompPolicy = 0;String WorkersCompPolicyString =  "Is this a worker’s comp policy: YES/NO";int MotorVehAccident = 0;
    String MotorVehAccidentString = "Is this a Motor Vehicle Accident : YES/NO";String PriInsurance = "";
    String MemId = "";String GrpNumber = "";String PriInsuranceName = "";String AddressIfDifferent = "";String PrimaryDOB = "";
    String PrimarySSN = "";String PatientRelationtoPrimary = "";String PrimaryOccupation = "";String PrimaryEmployer = "";
    String EmployerAddress = "";String EmployerPhone = "";String SecondryInsurance = "";String SubscriberName = "";String SubscriberDOB = "";
    String MemberID_2 = "";String GroupNumber_2 = "";String PatientRelationshiptoSecondry = "";
    //------------------EmergencyDiv Info Variables------------------
    String NextofKinName = "";String RelationToPatientER = "";String PhoneNumberER = "";int LeaveMessageER = 0;String AddressER = "";
    String CityER = "";String StateER = "";String LeaveMessageERString = "";String CityStateZipER = "";
    String CountryER = "";String ZipCodeER = "";
    //--------------Conecnt To Treatment Info varaible-----------------
    String DateConcent = "";String WitnessConcent = "";String PatientBehalfConcent = "";String RelativeSignConcent = "";String DateConcent2 = "";
    String WitnessConcent2 = "";String PatientSignConcent = "";
    //-----------------Some Random checks Info Variables---------------
    String ReturnPatient = "";String Google = "";String MapSearch = "";
    String Billboard = "";String OnlineReview = "";String TV = "";
    String Website = "";String BuildingSignDriveBy = "";String Facebook = "";
    String School = "";String School_text = "";String Twitter = "";String Magazine = "";String Magazine_text = "";
    String Newspaper = "";String Newspaper_text = "";String FamilyFriend = "";String FamilyFriend_text = "";String UrgentCare = "";String UrgentCare_text= "";
    String CommunityEvent = "";String CommunityEvent_text = "";String Work = "";String Work_text = "";String Physician = "";String Physician_text = "";
    String Other = "";String Other_text = "";
    //----------------------Some Check to Verify if set or not----------------------
    int SelfPayChk = 0;int VerifyChkBox = 0;





    try{
      Query = "select date_format(now(),'%Y%m%d%H%i%s'), DATE_FORMAT(now(), '%d-%m-%Y'), DATE_FORMAT(now(), '%T')";
      stmt = conn.createStatement();
      rset = stmt.executeQuery(Query);
      if(rset.next()) {
        DateTime = rset.getString(1);
        Date = rset.getString(2);
        Time = rset.getString(3);
      }
      rset.close();
      stmt.close();

      try {
        Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), IFNULL(MaritalStatus, '-'), " +
                " IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'), " +
                " IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-')," +
                " IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'), " +
                " IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, DATE_FORMAT(CreatedDate, '%d-%m-%Y'), IFNULL(DoctorsName,'-') " +
                " From " + Database + ".PatientReg Where ID = " + ID;
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

//        Query = "Select CONCAT(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Id = " + DoctorId;
//        stmt = conn.createStatement();
//        rset = stmt.executeQuery(Query);
//        while (rset.next()) {
//          DoctorName = rset.getString(1);
//        }
//        rset.close();
//        stmt.close();
      }catch(Exception e){
        out.println("Error In PateintReg:--"+e.getMessage());
        out.println(Query);
      }

      Query = "Select Ethnicity,Ethnicity_OthersText,EmployementChk,Employer,Occupation,EmpContact,PrimaryCarePhysicianChk,PriCarePhy,ReasonVisit," +
              "PriCarePhyAddress,PriCarePhyCity,PriCarePhyState,PriCarePhyZipCode,PatientMinorChk,GuarantorChk,GuarantorEmployer,GuarantorEmployerPhNumber," +
              "GuarantorEmployerAddress,GuarantorEmployerCity,GuarantorEmployerState,GuarantorEmployerZipCode,CreatedDate,WorkersCompPolicyChk,MotorVehicleAccidentChk," +
              "HealthInsuranceChk from "+Database+".PatientReg_Details where PatientRegId = "+ID;
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
        if(ReasonVisit == null){
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
//        CreatedDate = rset.getString(22);
        WorkersCompPolicyChk = rset.getInt(23);
        MotorVehicleAccidentChk = rset.getInt(24);
        HealthInsuranceChk = rset.getInt(25);

      }
      rset.close();
      stmt.close();

      if(Ethnicity.equals("1")){
        Ethnicity = "White";
      }else if(Ethnicity.equals("2")){
        Ethnicity = "Black or African American";
      }else if(Ethnicity.equals("3")){
        Ethnicity = "Latino or Hispanic";
      }else if(Ethnicity.equals("4")){
        Ethnicity = "Asian";
      }else if(Ethnicity.equals("5")){
        Ethnicity = "Others";
      }


      if(WorkersCompPolicyChk == 1){
        Query = "Select WCPInjuryRelatedAutoMotorAccident, WCPInjuryOccurVehicle from "+Database+".Patient_WorkCompPolicy where PatientRegId = "+ID;
        stmt = conn.createStatement();
        rset = stmt.executeQuery(Query);
        while (rset.next()) {
          WCPInjuryRelatedAutoMotorAccident = rset.getString(1);
          WCPInjuryOccurVehicle = rset.getString(2);
        }
        rset.close();
        stmt.close();

        mergePdf.GETINPUT(request, response, out, conn, Database, Path1+"GeneralForm_Victoria.pdf", Path2+"WC_MVA_AUTHORIZATIONTODISCLOSEMEDICALINFORMATION.pdf");
        mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/Victoria/Result.pdf", Path2+"WC_MVA_AUTHORIZATIONTOOBTAININFORMATION.pdf");
        mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/Victoria/Result.pdf", Path2+"WC_QUESTIONNAIRE.pdf");
        ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result.pdf";

        if(WCPInjuryRelatedAutoMotorAccident.equals("1") || WCPInjuryOccurVehicle.equals("1")){
          mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/Victoria/Result.pdf", Path2+"WC_MVA_assignmentofproceeds.pdf");
          ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result.pdf";
        }
      }else {
        ResultPdf = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/VictoriaPdf/GeneralForm_Victoria.pdf";
      }

      if(MotorVehicleAccidentChk == 1){
        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, Path2+"MVA_ASSIGNMENTOFPROCEEDS.pdf");
        ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result.pdf";

        Query = "Select AutoInsuranceInformationChk from "+Database+".Patient_AutoInsuranceInfo where PatientRegId = "+ID;
        stmt = conn.createStatement();
        rset = stmt.executeQuery(Query);
        while (rset.next()) {
          AutoInsuranceInformationChk = rset.getString(1);
        }
        rset.close();
        stmt.close();

        if(AutoInsuranceInformationChk.equals("1")){
          mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, Path2+"MVACLAIMFORM.pdf");
          ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result.pdf";
        }
      }
      else if(MotorVehicleAccidentChk == 0){
        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, Path2+"MVA_ASSIGNMENTOFPROCEEDS.pdf");
        ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result.pdf";
      }
      else{
        ResultPdf = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/VictoriaPdf/GeneralForm_Victoria.pdf";
      }


      if(HealthInsuranceChk == 1){
        Query = "Select GFIPMedicare,GFIPMedicaid,GFIPCHIP,GFIPTricare,GFIPVHA,GFIPIndianHealth, GovtFundedInsurancePlanChk from "+ Database+".Patient_HealthInsuranceInfo where PatientRegId = "+ID;
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
        }
        rset.close();
        stmt.close();

        if(GovtFundedInsurancePlanChk.equals("1")){
          if(GFIPMedicaid == 1 || GFIPCHIP == 1){
            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, Path2+"MEDICAIDSELFPAYAGREEMENT.pdf");
            ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result.pdf";
          }
          if (GFIPMedicare == 1){
            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, Path2+"ABNformEnglish.pdf");
            ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result.pdf";
          }
        }
      }else{
        ResultPdf = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/VictoriaPdf/GeneralForm_Victoria.pdf";
      }

      mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, Path2+"Medicalreleaseform.pdf");
      ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result.pdf";

      String inputFilePath = "";
//      if(ClientId == 8){
//        inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/Admin.pdf"; // Existing file
//      }else if(ClientId == 9){
//        inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/adminvictoria.pdf"; // Existing file
//      }else if(ClientId == 10){
//        inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/adminoddasa.pdf"; // Existing file
//      }
//
      inputFilePath = ResultPdf;
      String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Victoria/"+FirstNameNoSpaces+LastName+ID+"_"+DateTime+".pdf"; // New file
      OutputStream fos = new FileOutputStream(new File(outputFilePath));

      //0, 800 will write text on TOP LEFT of pdf page
      //0, 0 will write text on BOTTOM LEFT of pdf page


      PdfReader pdfReader = new PdfReader(inputFilePath);
      PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

      for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
        if (i == 1) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(70, 600); // set x and y co-ordinates
          pdfContentByte.showText(LastName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(150, 600); // set x and y co-ordinates
          pdfContentByte.showText(FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 608); // set x and y co-ordinates
          pdfContentByte.showText(Title); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(450, 608); // set x and y co-ordinates
          pdfContentByte.showText(MaritalStatus); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(50, 570); // set x and y co-ordinates
          pdfContentByte.showText(Address); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(300, 570); // set x and y co-ordinates
          pdfContentByte.showText(CityStateZip); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(450, 570); // set x and y co-ordinates
          pdfContentByte.showText(PhNumber); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(50, 540); // set x and y co-ordinates
          pdfContentByte.showText(SSN); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(230, 540); // set x and y co-ordinates
          pdfContentByte.showText(DOB); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(320, 540); // set x and y co-ordinates
          pdfContentByte.showText(Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(380, 540); // set x and y co-ordinates
          pdfContentByte.showText(gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(435, 540); // set x and y co-ordinates
          pdfContentByte.showText(Email); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(50, 515); // set x and y co-ordinates
          pdfContentByte.showText(Ethnicity); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(230, 515); // set x and y co-ordinates
          pdfContentByte.showText(Employer); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(400, 515); // set x and y co-ordinates
          pdfContentByte.showText(Occupation); // add the text
          pdfContentByte.endText();
        }

      }
      pdfStamper.close(); //close pdfStamper


//      response.setContentType("application/pdf");
//      out = response.getWriter();
//      String filepath = outputFilePath;
//      response.setHeader("Content-Disposition", "attachment; filename=" + filepath + ";");
//      FileInputStream fileOut = new FileInputStream(FirstNameNoSpaces+LastName+ID+"_"+DateTime+".pdf");
//      fileOut.close();
//      out.close();

      File pdfFile = new File(outputFilePath);
      response.setContentType("application/pdf");
      response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces+LastName+ID+"_"+DateTime+".pdf");
      response.setContentLength((int) pdfFile.length());

      FileInputStream fileInputStream = new FileInputStream(pdfFile);
      OutputStream responseOutputStream = response.getOutputStream();
      int bytes;
      while ((bytes = fileInputStream.read()) != -1) {
        responseOutputStream.write(bytes);
      }

    }catch(Exception e){
      out.println(e.getMessage());
    }
  }


  void GETINPUTOddasa(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId)
  {
    Statement stmt = null;
    ResultSet rset = null;
    String Query = "";
    int PatientRegId = 0 ;
    String DateTime = "";
    String Date = "";String Time = "";
    //------------------Initial Info Div Variables------------------
    String Title = "";String FirstName = "";String FirstNameNoSpaces = "";String LastName = "";String MiddleInitial = "";String MaritalStatus = "";String DOB = "";
    String Age = "";String gender = "";String Email = "";String PhNumber = "";String Address = "";String CityStateZip = "";String State = "";
    String Country = "";String ZipCode = "";String SSN = "";String Occupation = "";String Employer = "";String EmpContact = "";
    String PriCarePhy = "";String ReasonVisit = "";String MRN = "";int ClientIndex = 0;String ClientName = "";String DOS = "";String DoctorId = null;
    String DoctorName = null;
    //----------------Insuarnce Dive Wariable------------------
    int WorkersCompPolicy = 0;String WorkersCompPolicyString =  "Is this a worker’s comp policy: YES/NO";int MotorVehAccident = 0;
    String MotorVehAccidentString = "Is this a Motor Vehicle Accident : YES/NO";String PriInsurance = "";
    String MemId = "";String GrpNumber = "";int PriInsuranceName = 0;String _PriInsuranceName = "";String AddressIfDifferent = "";String PrimaryDOB = "";
    String PrimarySSN = "";String PatientRelationtoPrimary = "";String PrimaryOccupation = "";String PrimaryEmployer = "";
    String EmployerAddress = "";String EmployerPhone = "";String SecondryInsurance = "";String _SecondryInsurance = "";String SubscriberName = "";String SubscriberDOB = "";
    String MemberID_2 = "";String GroupNumber_2 = "";String PatientRelationshiptoSecondry = "";
    //------------------EmergencyDiv Info Variables------------------
    String NextofKinName = "";String RelationToPatientER = "";String PhoneNumberER = "";int LeaveMessageER = 0;String AddressER = "";
    String CityER = "";String StateER = "";String LeaveMessageERString = "";String CityStateZipER = "";
    String CountryER = "";String ZipCodeER = "";
    //--------------Conecnt To Treatment Info varaible-----------------
    String DateConcent = "";String WitnessConcent = "";String PatientBehalfConcent = "";String RelativeSignConcent = "";String DateConcent2 = "";
    String WitnessConcent2 = "";String PatientSignConcent = "";
    //-----------------Some Random checks Info Variables---------------
    String ReturnPatient = "";String Google = "";String MapSearch = "";
    String Billboard = "";String OnlineReview = "";String TV = "";
    String Website = "";String BuildingSignDriveBy = "";String Facebook = "";
    String School = "";String School_text = "";String Twitter = "";String Magazine = "";String Magazine_text = "";
    String Newspaper = "";String Newspaper_text = "";String FamilyFriend = "";String FamilyFriend_text = "";String UrgentCare = "";String UrgentCare_text= "";
    String CommunityEvent = "";String CommunityEvent_text = "";String Work = "";String Work_text = "";String Physician = "";String Physician_text = "";
    String Other = "";String Other_text = "";
    //----------------------Some Check to Verify if set or not----------------------
    int SelfPayChk = 0;int VerifyChkBox = 0;

    int ID = Integer.parseInt(request.getParameter("ID").trim());

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
          Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), IFNULL(MaritalStatus, '-'), " +
                  " IFNULL(DATE_FORMAT(DOB,'%d-%m-%Y'), '-'), " +
                  " IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-')," +
                  " IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'), " +
                  " IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, DATE_FORMAT(CreatedDate, '%d-%m-%Y'), IFNULL(DoctorsName,'-') " +
                  " From " + Database + ".PatientReg Where ID = " + ID;
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
          Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'), " +
                  " IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%d-%m-%Y'),'-'), IFNULL(PrimarySSN,'-'), " +
                  " IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'), " +
                  " IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%d-%m-%Y'),'-'), " +
                  " IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from " + Database + ".InsuranceInfo " +
                  " where PatientRegId = " + ID;
          stmt = conn.createStatement();
          rset = stmt.executeQuery(Query);
          while (rset.next()) {
            WorkersCompPolicy = rset.getInt(1);
            MotorVehAccident = rset.getInt(2);
            if (WorkersCompPolicy == 0) {
              WorkersCompPolicyString = "Is this a worker’s comp policy: NO";
            } else {
              WorkersCompPolicyString = "Is this a worker’s comp policy: YES";
            }
            if (MotorVehAccident == 0) {
              MotorVehAccidentString = "Is this a Motor Vehicle Accident : NO";
            } else {
              MotorVehAccidentString = "Is this a Motor Vehicle Accident : YES";
            }
            PriInsurance = rset.getString(3);
            MemId = rset.getString(4);
            GrpNumber = rset.getString(5);
            PriInsuranceName = rset.getInt(6);
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
          Query = "Select PayerName from " + Database + ".ProfessionalPayers where id = " + PriInsuranceName;
          stmt = conn.createStatement();
          rset = stmt.executeQuery(Query);
          if (rset.next()) {
            _PriInsuranceName = rset.getString(1);
          }
          rset.close();
          stmt.close();
        }
        if (!SecondryInsurance.equals("-1")) {
          Query = "Select PayerName from " + Database + ".ProfessionalPayers where id = " + SecondryInsurance;
          stmt = conn.createStatement();
          rset = stmt.executeQuery(Query);
          if (rset.next()) {
            _SecondryInsurance = rset.getString(1);
          }
          rset.close();
          stmt.close();
        }

        Query = "Select IFNULL(NextofKinName,'-'), IFNULL(RelationToPatient,'-'), IFNULL(PhoneNumber,'-'), " +
                "CASE WHEN LeaveMessage = 1 THEN 'YES' WHEN LeaveMessage = 0 THEN 'NO' ELSE ' YES / NO'END, " +
                " IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-') from " + Database + ".EmergencyInfo where PatientRegId = " + ID;
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

        Query = " Select ReturnPatient, Google, MapSearch, Billboard, OnlineReview, TV, Website, BuildingSignDriveBy, Facebook, School," + //10
                " IFNULL(School_text ,'-'), Twitter, Magazine, IFNULL(Magazine_text,'-'), Newspaper, IFNULL(Newspaper_text,'-'), FamilyFriend," +//17
                " IFNULL(FamilyFriend_text,'-'), UrgentCare, IFNULL(UrgentCare_text,'-'), CommunityEvent, IFNULL(CommunityEvent_text,'-'), " +//22
                " IFNULL(Work_text,'-'), IFNULL(Physician_text, '-'), IFNULL(Other_text,'-') from " + Database + ".RandomCheckInfo where PatientRegId = " + ID;//25
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
      }catch(Exception e){
        String str = "";
        for (int i = 0; i < e.getStackTrace().length; ++i) {
          str = str + e.getStackTrace()[i] + "<br>";
        }
        out.println(str);
      }



      String inputFilePath = "";
      int AdmissionBundle = 0;
      if(SelfPayChk == 0){
        inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/adminOddasa_SelfPay.pdf"; // Existing file
      }else if( SelfPayChk == 1){
        Query = "Select AdmissionBundle from "+Database+".PatientAdmissionBundle where PatientRegId = "+ID;
        stmt = conn.createStatement();
        rset = stmt.executeQuery(Query);
        if (rset.next()) {
          AdmissionBundle = rset.getInt(1);
        }
        rset.close();
        stmt.close();

        if(AdmissionBundle == 1){
          inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/adminOddasa_AetnaInsurance.pdf";
        }else if(AdmissionBundle == 2){
          inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/adminOddasa_BlueCrossBlueShield.pdf";
        }else if (AdmissionBundle == 3){
          inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/adminOddasa_UnitedHealthcare.pdf";
        }else if(AdmissionBundle == 4){
          inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/adminOddasa_OtherInsurance.pdf";
        }
      }
      //inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/Admin.pdf"; // Existing file

      String outputFilePath = "/sftpdrive/AdmissionBundlePdf/Orange/"+FirstNameNoSpaces+LastName+ID+"_"+MRN+"_"+DateTime+".pdf"; // New file
      OutputStream fos = new FileOutputStream(new File(outputFilePath));

      //0, 800 will write text on TOP LEFT of pdf page
      //0, 0 will write text on BOTTOM LEFT of pdf page


      PdfReader pdfReader = new PdfReader(inputFilePath);
      PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

      for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

        if(SelfPayChk == 0){
          if( i == 1) {
            PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(50, 715); // set x and y co-ordinates
            pdfContentByte.showText(LastName); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(200, 715); // set x and y co-ordinates
            pdfContentByte.showText(FirstName); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(310, 715); // set x and y co-ordinates
            pdfContentByte.showText(MiddleInitial); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(360, 715); // set x and y co-ordinates
            pdfContentByte.showText(Title); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(460, 715); // set x and y co-ordinates
            pdfContentByte.showText(MaritalStatus); // add the text
            pdfContentByte.endText();


            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(50, 685); // set x and y co-ordinates
            pdfContentByte.showText(SSN); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(220, 685); // set x and y co-ordinates
            pdfContentByte.showText(PhNumber); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(360, 685); // set x and y co-ordinates
            pdfContentByte.showText(DOB); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(480, 685); // set x and y co-ordinates
            pdfContentByte.showText(gender); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(35, 652); // set x and y co-ordinates
            pdfContentByte.showText(Address); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(240, 652); // set x and y co-ordinates
            pdfContentByte.showText(CityStateZip); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(410, 655); // set x and y co-ordinates
            pdfContentByte.showText(Email); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(35, 622); // set x and y co-ordinates
            pdfContentByte.showText(Employer); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(220, 622); // set x and y co-ordinates
            pdfContentByte.showText(EmployerPhone); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(360, 622); // set x and y co-ordinates
            pdfContentByte.showText(Occupation); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(35, 595); // set x and y co-ordinates
            pdfContentByte.showText(PriCarePhy); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(220, 595); // set x and y co-ordinates
            pdfContentByte.showText(" "); // add the text //"Phone Pri Care Phy"
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(210, 462); // set x and y co-ordinates
            if(WorkersCompPolicy == 0 && MotorVehAccident == 0) {
              pdfContentByte.showText("No"); // add the text // iIs this visit due to a work or auto accident?
            }else {
              pdfContentByte.showText("Yes");
            }
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(190, 440); // set x and y co-ordinates
            pdfContentByte.showText(_PriInsuranceName); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(35, 400); // set x and y co-ordinates
            pdfContentByte.showText(""); // add the text //Subscriber Last Name
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(190, 400); // set x and y co-ordinates
            pdfContentByte.showText(SubscriberName); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(310, 400); // set x and y co-ordinates
            pdfContentByte.showText(PrimarySSN); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(460, 400); // set x and y co-ordinates
            pdfContentByte.showText(SubscriberDOB); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(35, 365); // set x and y co-ordinates
            pdfContentByte.showText(MemId); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(220, 365); // set x and y co-ordinates
            pdfContentByte.showText(GrpNumber); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(390, 365); // set x and y co-ordinates
            pdfContentByte.showText(PatientRelationtoPrimary); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(480, 365); // set x and y co-ordinates
            pdfContentByte.showText(PhNumber); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(220, 345); // set x and y co-ordinates
            pdfContentByte.showText(_SecondryInsurance); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(35, 310); // set x and y co-ordinates
            pdfContentByte.showText(""); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(190, 310); // set x and y co-ordinates
            pdfContentByte.showText(SubscriberName); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(310, 310); // set x and y co-ordinates
            pdfContentByte.showText(PrimarySSN); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(460, 310); // set x and y co-ordinates
            pdfContentByte.showText(SubscriberDOB); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(35, 280); // set x and y co-ordinates
            pdfContentByte.showText(MemberID_2); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(220, 280); // set x and y co-ordinates
            pdfContentByte.showText(GroupNumber_2); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(390, 280); // set x and y co-ordinates
            pdfContentByte.showText(PatientRelationshiptoSecondry); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 85); // set x and y co-ordinates
            pdfContentByte.showText(NextofKinName); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(320, 85); // set x and y co-ordinates
            pdfContentByte.showText(RelationToPatientER); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(430, 80); // set x and y co-ordinates
            pdfContentByte.showText(" "); // add the text //Contact DOB ER
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(500, 85); // set x and y co-ordinates
            pdfContentByte.showText(PhoneNumberER); // add the text
            pdfContentByte.endText();

          }

          if (i == 2){
            PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 76); // set x and y co-ordinates
            pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 66); // set x and y co-ordinates
            pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 56); // set x and y co-ordinates
            pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 46); // set x and y co-ordinates
            pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 36); // set x and y co-ordinates
            pdfContentByte.showText("Dr. "+DoctorName); // add the text
            pdfContentByte.endText();

          }

          if (i == 3){
            PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 76); // set x and y co-ordinates
            pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 66); // set x and y co-ordinates
            pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 56); // set x and y co-ordinates
            pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 46); // set x and y co-ordinates
            pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 36); // set x and y co-ordinates
            pdfContentByte.showText("Dr. "+DoctorName); // add the text
            pdfContentByte.endText();

          }

          if (i == 4){
            PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
            pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
            pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
            pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
            pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
            pdfContentByte.showText("Dr. "+DoctorName); // add the text
            pdfContentByte.endText();

          }

          if (i == 5){
            PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(110, 470); // set x and y co-ordinates
            pdfContentByte.showText(Title +" "+ FirstName +" "+ MiddleInitial + " " + LastName);
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(435, 470); // set x and y co-ordinates
            pdfContentByte.showText(SubscriberDOB); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(110, 440); // set x and y co-ordinates
            pdfContentByte.showText(MemId); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(420, 440); // set x and y co-ordinates
            pdfContentByte.showText(GrpNumber); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(340, 250); // set x and y co-ordinates
            pdfContentByte.showText(Title +" "+ FirstName +" "+ MiddleInitial + " " + LastName);
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
            pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial);
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
            pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
            pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
            pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
            pdfContentByte.endText();

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
            pdfContentByte.setColorFill(BaseColor.BLACK);
            pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
            pdfContentByte.showText("Dr. "+DoctorName); // add the text
            pdfContentByte.endText();

          }


        }else if(SelfPayChk == 1){

          if(AdmissionBundle == 1){
            //Aetna Bundle Here
            if( i == 1) {
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(50, 715); // set x and y co-ordinates
              pdfContentByte.showText(LastName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(200, 715); // set x and y co-ordinates
              pdfContentByte.showText(FirstName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(310, 715); // set x and y co-ordinates
              pdfContentByte.showText(MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(360, 715); // set x and y co-ordinates
              pdfContentByte.showText(Title); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(460, 715); // set x and y co-ordinates
              pdfContentByte.showText(MaritalStatus); // add the text
              pdfContentByte.endText();


              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(50, 685); // set x and y co-ordinates
              pdfContentByte.showText(SSN); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 685); // set x and y co-ordinates
              pdfContentByte.showText(PhNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(360, 685); // set x and y co-ordinates
              pdfContentByte.showText(DOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(480, 685); // set x and y co-ordinates
              pdfContentByte.showText(gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 652); // set x and y co-ordinates
              pdfContentByte.showText(Address); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(240, 652); // set x and y co-ordinates
              pdfContentByte.showText(CityStateZip); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(410, 655); // set x and y co-ordinates
              pdfContentByte.showText(Email); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 622); // set x and y co-ordinates
              pdfContentByte.showText(Employer); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 622); // set x and y co-ordinates
              pdfContentByte.showText(EmployerPhone); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(360, 622); // set x and y co-ordinates
              pdfContentByte.showText(Occupation); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 595); // set x and y co-ordinates
              pdfContentByte.showText(PriCarePhy); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 595); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text Phone Pri Care Phy
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(210, 462); // set x and y co-ordinates
              if(WorkersCompPolicy == 0 && MotorVehAccident == 0) {
                pdfContentByte.showText("No"); // add the text // iIs this visit due to a work or auto accident?
              }else {
                pdfContentByte.showText("Yes");// add the text // iIs this visit due to a work or auto accident?
              }
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(190, 440); // set x and y co-ordinates
              pdfContentByte.showText(_PriInsuranceName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 400); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text Subscriber Last Name
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(190, 400); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(310, 400); // set x and y co-ordinates
              pdfContentByte.showText(PrimarySSN); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(460, 400); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberDOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 365); // set x and y co-ordinates
              pdfContentByte.showText(MemId); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 365); // set x and y co-ordinates
              pdfContentByte.showText(GrpNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(390, 365); // set x and y co-ordinates
              pdfContentByte.showText(PatientRelationtoPrimary); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(480, 365); // set x and y co-ordinates
              pdfContentByte.showText(PhNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 345); // set x and y co-ordinates
              pdfContentByte.showText(_SecondryInsurance); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 310); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text Subscriber Last Name
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(190, 310); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(310, 310); // set x and y co-ordinates
              pdfContentByte.showText(PrimarySSN); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(460, 310); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberDOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 280); // set x and y co-ordinates
              pdfContentByte.showText(MemberID_2); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 280); // set x and y co-ordinates
              pdfContentByte.showText(GroupNumber_2); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(390, 280); // set x and y co-ordinates
              pdfContentByte.showText(PatientRelationshiptoSecondry); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 85); // set x and y co-ordinates
              pdfContentByte.showText(NextofKinName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(320, 85); // set x and y co-ordinates
              pdfContentByte.showText(RelationToPatientER); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(430, 80); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text Contact DOB
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(500, 85); // set x and y co-ordinates
              pdfContentByte.showText(PhoneNumberER); // add the text
              pdfContentByte.endText();

            }

            if (i == 2){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 76); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 66); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 56); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 46); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 36); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 3){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 76); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 66); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 56); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 46); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 36); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 4){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 5){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(90, 510); // set x and y co-ordinates
              pdfContentByte.showText( Title +" " + FirstName +" " + MiddleInitial + " " + LastName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(380, 510); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberDOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(90, 480); // set x and y co-ordinates
              pdfContentByte.showText(MemId); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(370, 480); // set x and y co-ordinates
              pdfContentByte.showText(GrpNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(300, 315); // set x and y co-ordinates
              pdfContentByte.showText( FirstName +" " + MiddleInitial + " " + LastName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 6){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 7){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 680); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text Aetna Member Name
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 655); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text //Provider of Service
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 630); // set x and y co-ordinates
              pdfContentByte.showText(FirstName +" "+ " " + MiddleInitial + " " + LastName + ",  DOS: "+ DOS); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 610); // set x and y co-ordinates
              pdfContentByte.showText(FirstName +" "+ " " + MiddleInitial + " " + LastName ); // add the text //pdfContentByte.showText("Patient Name / Aetna Member Name "); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 575); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text //Print the name of the person who is being authorized to act on the member’s behalf
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 165); // set x and y co-ordinates
              pdfContentByte.showText(FirstName +" "+ " " + MiddleInitial + " " + LastName );// pdfContentByte.showText("Print Member Name / Patient Name "); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

          }else if(AdmissionBundle == 2){
            //Blucross Blue Sheild Bundle Here
            if( i == 1) {
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(50, 715); // set x and y co-ordinates
              pdfContentByte.showText(LastName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(200, 715); // set x and y co-ordinates
              pdfContentByte.showText(FirstName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(310, 715); // set x and y co-ordinates
              pdfContentByte.showText(MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(360, 715); // set x and y co-ordinates
              pdfContentByte.showText(Title); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(460, 715); // set x and y co-ordinates
              pdfContentByte.showText(MaritalStatus); // add the text
              pdfContentByte.endText();


              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(50, 685); // set x and y co-ordinates
              pdfContentByte.showText(SSN); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 685); // set x and y co-ordinates
              pdfContentByte.showText(PhNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(360, 685); // set x and y co-ordinates
              pdfContentByte.showText(DOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(480, 685); // set x and y co-ordinates
              pdfContentByte.showText(gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 652); // set x and y co-ordinates
              pdfContentByte.showText(Address); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(240, 652); // set x and y co-ordinates
              pdfContentByte.showText(CityStateZip); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(410, 655); // set x and y co-ordinates
              pdfContentByte.showText(Email); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 622); // set x and y co-ordinates
              pdfContentByte.showText(Employer); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 622); // set x and y co-ordinates
              pdfContentByte.showText(EmployerPhone); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(360, 622); // set x and y co-ordinates
              pdfContentByte.showText(Occupation); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 595); // set x and y co-ordinates
              pdfContentByte.showText(PriCarePhy); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 595); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text //Phone Pri Care Phy
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(210, 462); // set x and y co-ordinates
              if(WorkersCompPolicy == 0 && MotorVehAccident == 0) {
                pdfContentByte.showText("No"); // add the text // iIs this visit due to a work or auto accident?
              }else {
                pdfContentByte.showText("Yes"); //add the text // iIs this visit due to a work or auto accident?
              }
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(190, 440); // set x and y co-ordinates
              pdfContentByte.showText(_PriInsuranceName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 400); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text //Subscriber Last Name
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(190, 400); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(310, 400); // set x and y co-ordinates
              pdfContentByte.showText(PrimarySSN); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(460, 400); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberDOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 365); // set x and y co-ordinates
              pdfContentByte.showText(MemId); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 365); // set x and y co-ordinates
              pdfContentByte.showText(GrpNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(390, 365); // set x and y co-ordinates
              pdfContentByte.showText(PatientRelationtoPrimary); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(480, 365); // set x and y co-ordinates
              pdfContentByte.showText(PhNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 345); // set x and y co-ordinates
              pdfContentByte.showText(_SecondryInsurance); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 310); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text //Subscriber Last Name
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(190, 310); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(310, 310); // set x and y co-ordinates
              pdfContentByte.showText(PrimarySSN); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(460, 310); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberDOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 280); // set x and y co-ordinates
              pdfContentByte.showText(MemberID_2); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 280); // set x and y co-ordinates
              pdfContentByte.showText(GroupNumber_2); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(390, 280); // set x and y co-ordinates
              pdfContentByte.showText(PatientRelationshiptoSecondry); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 85); // set x and y co-ordinates
              pdfContentByte.showText(NextofKinName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(320, 85); // set x and y co-ordinates
              pdfContentByte.showText(RelationToPatientER); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(430, 80); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text Contact DOB
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(500, 85); // set x and y co-ordinates
              pdfContentByte.showText(PhoneNumberER); // add the text
              pdfContentByte.endText();

            }

            if (i == 2){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 76); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 66); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 56); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 46); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 36); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 3){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 76); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 66); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 56); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 46); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 36); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 4){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 5){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(110, 475); // set x and y co-ordinates
              pdfContentByte.showText(FirstName +" "+MiddleInitial+ " " + LastName ); // add the text// pdfContentByte.showText("Subscriber/Patient Name "); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(390, 475); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberDOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(110, 450); // set x and y co-ordinates
              pdfContentByte.showText(MemId); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(380, 450); // set x and y co-ordinates
              pdfContentByte.showText(GrpNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(310, 280); // set x and y co-ordinates
              pdfContentByte.showText(FirstName +" "+MiddleInitial+ " " + LastName ); // pdfContentByte.showText("Printed Name of the Person Completing Form "); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 6){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 7){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(200, 545); // set x and y co-ordinates
              pdfContentByte.showText(FirstName +" "+MiddleInitial+ " " + LastName ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(200, 532); // set x and y co-ordinates
              pdfContentByte.showText(GrpNumber); // add the text //Provider of Service
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 520); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text // Subscriber Number/Patient Number
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(200, 508); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text //Provider Name
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(310, 495); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text //Claim Number
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(200, 482); // set x and y co-ordinates
              pdfContentByte.showText(DOS); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

          }else if (AdmissionBundle == 3){
            //United Health Care Bundle Here
            if( i == 1) {
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(50, 715); // set x and y co-ordinates
              pdfContentByte.showText(LastName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(200, 715); // set x and y co-ordinates
              pdfContentByte.showText(FirstName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(310, 715); // set x and y co-ordinates
              pdfContentByte.showText(MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(360, 715); // set x and y co-ordinates
              pdfContentByte.showText(Title); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(460, 715); // set x and y co-ordinates
              pdfContentByte.showText(MaritalStatus); // add the text
              pdfContentByte.endText();


              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(50, 685); // set x and y co-ordinates
              pdfContentByte.showText(SSN); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 685); // set x and y co-ordinates
              pdfContentByte.showText(PhNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(360, 685); // set x and y co-ordinates
              pdfContentByte.showText(DOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(480, 685); // set x and y co-ordinates
              pdfContentByte.showText(gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 652); // set x and y co-ordinates
              pdfContentByte.showText(Address); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(240, 652); // set x and y co-ordinates
              pdfContentByte.showText(CityStateZip); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(410, 655); // set x and y co-ordinates
              pdfContentByte.showText(Email); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 622); // set x and y co-ordinates
              pdfContentByte.showText(Employer); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 622); // set x and y co-ordinates
              pdfContentByte.showText(EmployerPhone); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(360, 622); // set x and y co-ordinates
              pdfContentByte.showText(Occupation); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 595); // set x and y co-ordinates
              pdfContentByte.showText(PriCarePhy); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 595); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text Phone Pri Care Phy
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(210, 462); // set x and y co-ordinates
              if(WorkersCompPolicy == 0 && MotorVehAccident == 0) {
                pdfContentByte.showText("No"); // add the text // iIs this visit due to a work or auto accident?
              }else {
                pdfContentByte.showText("Yes"); // add the text // iIs this visit due to a work or auto accident?
              }
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(190, 440); // set x and y co-ordinates
              pdfContentByte.showText(_PriInsuranceName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 400); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text Subscriber Last Name
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(190, 400); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(310, 400); // set x and y co-ordinates
              pdfContentByte.showText(PrimarySSN); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(460, 400); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberDOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 365); // set x and y co-ordinates
              pdfContentByte.showText(MemId); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 365); // set x and y co-ordinates
              pdfContentByte.showText(GrpNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(390, 365); // set x and y co-ordinates
              pdfContentByte.showText(PatientRelationtoPrimary); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(480, 365); // set x and y co-ordinates
              pdfContentByte.showText(PhNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 345); // set x and y co-ordinates
              pdfContentByte.showText(_SecondryInsurance); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 310); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text Subscriber Last Name
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(190, 310); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(310, 310); // set x and y co-ordinates
              pdfContentByte.showText(PrimarySSN); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(460, 310); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberDOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 280); // set x and y co-ordinates
              pdfContentByte.showText(MemberID_2); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 280); // set x and y co-ordinates
              pdfContentByte.showText(GroupNumber_2); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(390, 280); // set x and y co-ordinates
              pdfContentByte.showText(PatientRelationshiptoSecondry); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 85); // set x and y co-ordinates
              pdfContentByte.showText(NextofKinName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(320, 85); // set x and y co-ordinates
              pdfContentByte.showText(RelationToPatientER); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(430, 80); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text Contact DOB
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(500, 85); // set x and y co-ordinates
              pdfContentByte.showText(PhoneNumberER); // add the text
              pdfContentByte.endText();

            }

            if (i == 2){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 76); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 66); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 56); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 46); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 36); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 3){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 76); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 66); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 56); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 46); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 36); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 4){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 5){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(90, 505); // set x and y co-ordinates
              pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName ); // add the text //pdfContentByte.showText("Subscriber/Patient Name "); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(380, 505); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberDOB); // add the text //"Subscriber/Patient DOB "
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(110, 480); // set x and y co-ordinates
              pdfContentByte.showText(MemId); // add the text //Subscriber/Patient Member Id
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(375, 480); // set x and y co-ordinates
              pdfContentByte.showText(GrpNumber); // add the text /Subscriber/Patient Group No
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(295, 312); // set x and y co-ordinates
              pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName );// pdfContentByte.showText("Printed Name of the Person Completing Form "); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 6){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 7){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(160, 610); // set x and y co-ordinates
              pdfContentByte.showText(DOS); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(180, 590); // set x and y co-ordinates
              pdfContentByte.showText(FirstName + " " + MiddleInitial + " " + LastName );// pdfContentByte.showText("Member Name/ Patient Name"); // add the text //Provider of Service
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(160, 565); // set x and y co-ordinates
              pdfContentByte.showText(MemId); // add the text "Member Id Number "
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(180, 540); // set x and y co-ordinates
              pdfContentByte.showText("  "); // add the text Autorizer Name:
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(210, 530); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text Concerning Name/ or Any Other Info
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

          }else if(AdmissionBundle == 4){
            // Other Insurance Bundle Here
            if( i == 1) {
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(50, 715); // set x and y co-ordinates
              pdfContentByte.showText(LastName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(200, 715); // set x and y co-ordinates
              pdfContentByte.showText(FirstName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(310, 715); // set x and y co-ordinates
              pdfContentByte.showText(MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(360, 715); // set x and y co-ordinates
              pdfContentByte.showText(Title); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(460, 715); // set x and y co-ordinates
              pdfContentByte.showText(MaritalStatus); // add the text
              pdfContentByte.endText();


              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(50, 685); // set x and y co-ordinates
              pdfContentByte.showText(SSN); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 685); // set x and y co-ordinates
              pdfContentByte.showText(PhNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(360, 685); // set x and y co-ordinates
              pdfContentByte.showText(DOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(480, 685); // set x and y co-ordinates
              pdfContentByte.showText(gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 652); // set x and y co-ordinates
              pdfContentByte.showText(Address); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(240, 652); // set x and y co-ordinates
              pdfContentByte.showText(CityStateZip); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(410, 655); // set x and y co-ordinates
              pdfContentByte.showText(Email); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 622); // set x and y co-ordinates
              pdfContentByte.showText(Employer); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 622); // set x and y co-ordinates
              pdfContentByte.showText(EmployerPhone); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(360, 622); // set x and y co-ordinates
              pdfContentByte.showText(Occupation); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 595); // set x and y co-ordinates
              pdfContentByte.showText(PriCarePhy); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 595); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text //"Phone Pri Care Phy"
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(210, 462); // set x and y co-ordinates
              if(WorkersCompPolicy == 0 && MotorVehAccident == 0) {
                pdfContentByte.showText("No"); // add the text // iIs this visit due to a work or auto accident?
              }else {
                pdfContentByte.showText("Yes"); // add the text // iIs this visit due to a work or auto accident?
              }
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(190, 440); // set x and y co-ordinates
              pdfContentByte.showText(_PriInsuranceName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 400); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text Subscriber Last Name
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(190, 400); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(310, 400); // set x and y co-ordinates
              pdfContentByte.showText(PrimarySSN); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(460, 400); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberDOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 365); // set x and y co-ordinates
              pdfContentByte.showText(MemId); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 365); // set x and y co-ordinates
              pdfContentByte.showText(GrpNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(390, 365); // set x and y co-ordinates
              pdfContentByte.showText(PatientRelationtoPrimary); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(480, 365); // set x and y co-ordinates
              pdfContentByte.showText(PhNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 345); // set x and y co-ordinates
              pdfContentByte.showText(_SecondryInsurance); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 310); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text Subscriber Last Name
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(190, 310); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(310, 310); // set x and y co-ordinates
              pdfContentByte.showText(PrimarySSN); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(460, 310); // set x and y co-ordinates
              pdfContentByte.showText(SubscriberDOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(35, 280); // set x and y co-ordinates
              pdfContentByte.showText(MemberID_2); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(220, 280); // set x and y co-ordinates
              pdfContentByte.showText(GroupNumber_2); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(390, 280); // set x and y co-ordinates
              pdfContentByte.showText(PatientRelationshiptoSecondry); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 85); // set x and y co-ordinates
              pdfContentByte.showText(NextofKinName); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(320, 85); // set x and y co-ordinates
              pdfContentByte.showText(RelationToPatientER); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(430, 80); // set x and y co-ordinates
              pdfContentByte.showText(" "); // add the text Contact DOB
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(500, 85); // set x and y co-ordinates
              pdfContentByte.showText(PhoneNumberER); // add the text
              pdfContentByte.endText();

            }

            if (i == 2){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 76); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 66); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 56); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 46); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 36); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 3){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 76); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 66); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 56); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 46); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 36); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 4){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 5){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(90, 505); // set x and y co-ordinates
              pdfContentByte.showText(FirstName+", "+MiddleInitial + " " + LastName );  // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(380, 505); // set x and y co-ordinates
              pdfContentByte.showText(DOB); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(110, 480); // set x and y co-ordinates
              pdfContentByte.showText(MemId); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(375, 480); // set x and y co-ordinates
              pdfContentByte.showText(GrpNumber); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(295, 312); // set x and y co-ordinates
              pdfContentByte.showText(FirstName+", "+MiddleInitial + " " + LastName );  // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 6){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }

            if (i == 7){
              PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(90, 665); // set x and y co-ordinates
              pdfContentByte.showText(DOS); // add the text // "Date of Visit/ Date Of Service:"
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(140, 620); // set x and y co-ordinates
              pdfContentByte.showText(FirstName+", "+MiddleInitial + " " + LastName );// pdfContentByte.showText("Member Name/ Patient Name"); // add the text //Provider of Service
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(130, 570); // set x and y co-ordinates
              pdfContentByte.showText(_PriInsuranceName); // add the text "Primary Insurance Name "
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(130, 520); // set x and y co-ordinates
              pdfContentByte.showText(MemId); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 70); // set x and y co-ordinates
              pdfContentByte.showText(LastName +", "+FirstName + " " + MiddleInitial); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 60); // set x and y co-ordinates
              pdfContentByte.showText(ClientName + "   Sex: " + gender); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 50); // set x and y co-ordinates
              pdfContentByte.showText("DOB: "+DOB+"    Age: ("+Age+")"); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 40); // set x and y co-ordinates
              pdfContentByte.showText("MRN: "+MRN+"    DOS: "+DOS ); // add the text
              pdfContentByte.endText();

              pdfContentByte.beginText();
              pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
              pdfContentByte.setColorFill(BaseColor.BLACK);
              pdfContentByte.setTextMatrix(40, 30); // set x and y co-ordinates
              pdfContentByte.showText("Dr. "+DoctorName); // add the text
              pdfContentByte.endText();

            }
          }






        }





      }
      pdfStamper.close(); //close pdfStamper


//      response.setContentType("application/pdf");
//      out = response.getWriter();
//      String filepath = outputFilePath;
//      response.setHeader("Content-Disposition", "attachment; filename=" + filepath + ";");
//      FileInputStream fileOut = new FileInputStream(FirstNameNoSpaces+LastName+ID+"_"+DateTime+".pdf");
//      fileOut.close();
//      out.close();

      File pdfFile = new File(outputFilePath);
      response.setContentType("application/pdf");
      response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces+LastName+ID+"_"+MRN+"_"+DateTime+".pdf");
      response.setContentLength((int) pdfFile.length());

      FileInputStream fileInputStream = new FileInputStream(pdfFile);
      OutputStream responseOutputStream = response.getOutputStream();
      int bytes;
      while ((bytes = fileInputStream.read()) != -1) {
        responseOutputStream.write(bytes);
      }

    }catch(Exception e){
      out.println(e.getMessage());
    }
  }


  void GETINPUTSAustin(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId)
  {
    Statement stmt = null;
    ResultSet rset = null;
    String Query = "";
    int PatientRegId = 0 ;
    String DateTime = "";
    String Date = "";String Time = "";
    //------------------Initial Info Div Variables------------------
    String Title = "";String FirstName = "";String FirstNameNoSpaces = "";String LastName = "";String MiddleInitial = "";String MaritalStatus = "";String DOB = "";
    String Age = "";String gender = "";String Email = "";String PhNumber = "";String Address = "";String CityStateZip = "";String State = "";
    String Country = "";String ZipCode = "";String SSN = "";String Occupation = "";String Employer = "";String EmpContact = "";
    String PriCarePhy = "";String ReasonVisit = "";String MRN = "";int ClientIndex = 0;String ClientName = "";String DOS = "";String DoctorId = null;
    String DoctorName = null;
    //----------------Insuarnce Dive Wariable------------------
    int WorkersCompPolicy = 0;String WorkersCompPolicyString =  "Is this a worker’s comp policy: YES/NO";int MotorVehAccident = 0;
    String MotorVehAccidentString = "Is this a Motor Vehicle Accident : YES/NO";String PriInsurance = "";
    String MemId = "";String GrpNumber = "";String PriInsuranceName = "";String AddressIfDifferent = "";String PrimaryDOB = "";
    String PrimarySSN = "";String PatientRelationtoPrimary = "";String PrimaryOccupation = "";String PrimaryEmployer = "";
    String EmployerAddress = "";String EmployerPhone = "";String SecondryInsurance = "";String SubscriberName = "";String SubscriberDOB = "";
    String MemberID_2 = "";String GroupNumber_2 = "";String PatientRelationshiptoSecondry = "";
    //------------------EmergencyDiv Info Variables------------------
    String NextofKinName = "";String RelationToPatientER = "";String PhoneNumberER = "";int LeaveMessageER = 0;String AddressER = "";
    String CityER = "";String StateER = "";String LeaveMessageERString = "";String CityStateZipER = "";
    String CountryER = "";String ZipCodeER = "";
    //--------------Conecnt To Treatment Info varaible-----------------
    String DateConcent = "";String WitnessConcent = "";String PatientBehalfConcent = "";String RelativeSignConcent = "";String DateConcent2 = "";
    String WitnessConcent2 = "";String PatientSignConcent = "";
    //-----------------Some Random checks Info Variables---------------
    String ReturnPatient = "";String Google = "";String MapSearch = "";
    String Billboard = "";String OnlineReview = "";String TV = "";
    String Website = "";String BuildingSignDriveBy = "";String Facebook = "";
    String School = "";String School_text = "";String Twitter = "";String Magazine = "";String Magazine_text = "";
    String Newspaper = "";String Newspaper_text = "";String FamilyFriend = "";String FamilyFriend_text = "";String UrgentCare = "";String UrgentCare_text= "";
    String CommunityEvent = "";String CommunityEvent_text = "";String Work = "";String Work_text = "";String Physician = "";String Physician_text = "";
    String Other = "";String Other_text = "";
    //----------------------Some Check to Verify if set or not----------------------
    int SelfPayChk = 0;int VerifyChkBox = 0;

    int ID = Integer.parseInt(request.getParameter("ID").trim());

    try{
      Query = "select date_format(now(),'%Y%m%d%H%i%s'), DATE_FORMAT(now(), '%m/%d/%Y'), DATE_FORMAT(now(), '%T')";
      stmt = conn.createStatement();
      rset = stmt.executeQuery(Query);
      if(rset.next()) {
        DateTime = rset.getString(1);
        Date = rset.getString(2);
        Time = rset.getString(3);
      }
      rset.close();
      stmt.close();

      try {
        Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), IFNULL(MaritalStatus, '-'), " +
                " IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'), " +
                " IFNULL(Age, '0'), IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), IFNULL(PhNumber,'-')," +
                " IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'), " +
                " IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), ClientIndex, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), IFNULL(DoctorsName,'-') " +
                " From " + Database + ".PatientReg Where ID = " + ID;
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
      }catch(Exception e){
        out.println("Error In PateintReg:--"+e.getMessage());
        out.println(Query);
      }
      if(SelfPayChk == 1){
        Query = " Select IFNULL(WorkersCompPolicy,0), IFNULL(MotorVehAccident,0), IFNULL(PriInsurance,'-'),IFNULL(MemId,'-'), IFNULL(GrpNumber,'-'), " +
                " IFNULL(PriInsuranceName,'-'), IFNULL(AddressIfDifferent,'-'), IFNULL(DATE_FORMAT(PrimaryDOB,'%m/%d/%Y'),'-'), IFNULL(PrimarySSN,'-'), " +
                " IFNULL(PatientRelationtoPrimary,'-'), IFNULL(PrimaryOccupation,'-'), IFNULL(PrimaryEmployer,'-'), IFNULL(EmployerAddress,'-'), " +
                " IFNULL(EmployerPhone, '-'), IFNULL(SecondryInsurance,'-'), IFNULL(SubscriberName,'-'), IFNULL(DATE_FORMAT(SubscriberDOB,'%m/%d/%Y'),'-'), " +
                " IFNULL(PatientRelationshiptoSecondry,'-'), IFNULL(MemberID_2,'-'), IFNULL(GroupNumber_2,'-') from "+Database+".InsuranceInfo " +
                " where PatientRegId = "+ID;
        stmt = conn.createStatement();
        rset = stmt.executeQuery(Query);
        while (rset.next()) {
          WorkersCompPolicy = rset.getInt(1);
          MotorVehAccident = rset.getInt(2);
          if(WorkersCompPolicy == 0){
            WorkersCompPolicyString = "Is this a worker’s comp policy: NO";
          }else{
            WorkersCompPolicyString = "Is this a worker’s comp policy: YES";
          }
          if(MotorVehAccident == 0){
            MotorVehAccidentString = "Is this a Motor Vehicle Accident : NO";
          }else{
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

      Query = "Select IFNULL(NextofKinName,'-'), IFNULL(RelationToPatient,'-'), IFNULL(PhoneNumber,'-'), " +
              "CASE WHEN LeaveMessage = 1 THEN 'YES' WHEN LeaveMessage = 0 THEN 'NO' ELSE ' YES / NO'END, " +
              " IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-') from "+Database+".EmergencyInfo where PatientRegId = "+ID;
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

      Query = " Select ReturnPatient, Google, MapSearch, Billboard, OnlineReview, TV, Website, BuildingSignDriveBy, Facebook, School," + //10
              " IFNULL(School_text ,'-'), Twitter, Magazine, IFNULL(Magazine_text,'-'), Newspaper, IFNULL(Newspaper_text,'-'), FamilyFriend," +//17
              " IFNULL(FamilyFriend_text,'-'), UrgentCare, IFNULL(UrgentCare_text,'-'), CommunityEvent, IFNULL(CommunityEvent_text,'-'), " +//22
              " IFNULL(Work_text,'-'), IFNULL(Physician_text, '-'), IFNULL(Other_text,'-') from "+Database+".RandomCheckInfo where PatientRegId = "+ID;//25
      stmt = conn.createStatement();
      rset = stmt.executeQuery(Query);
      while (rset.next()) {
        if(rset.getInt(1) == 0){
          ReturnPatient = "";
        }else{
          ReturnPatient = "YES";
        }
        if(rset.getInt(2) == 0){
          Google = "";
        }else{
          Google = "YES";
        }
        if(rset.getInt(3) == 0){
          MapSearch = "";
        }else{
          MapSearch = "YES";
        }
        if(rset.getInt(4) == 0){
          Billboard = "";
        }else{
          Billboard = "YES";
        }
        if(rset.getInt(5) == 0){
          OnlineReview = "";
        }else{
          OnlineReview = "YES";
        }
        if(rset.getInt(6) == 0){
          TV = "";
        }else{
          TV = "YES";
        }
        if(rset.getInt(7) == 0){
          Website = "";
        }else{
          Website = "YES";
        }
        if(rset.getInt(8) == 0){
          BuildingSignDriveBy = "";
        }else{
          BuildingSignDriveBy = "YES";
        }
        if(rset.getInt(9) == 0){
          Facebook = "";
        }else{
          Facebook = "YES";
        }
        if(rset.getInt(10) == 0){
          School = "";
          School_text = "";
        }else{
          School = "YES";
          School_text = rset.getString(11);
        }
        if(rset.getInt(12) == 0){
          Twitter = "";
        }else{
          Twitter = "YES";
        }
        if(rset.getInt(13) == 0){
          Magazine = "";
          Magazine_text = "";
        }else{
          Magazine = "YES";
          Magazine_text = rset.getString(14);
        }
        if(rset.getInt(15) == 0){
          Newspaper = "";
          Newspaper_text = "";
        }else{
          Newspaper = "YES";
          Newspaper_text = rset.getString(16);
        }
        if(rset.getInt(17) == 0){
          FamilyFriend = "";
          FamilyFriend_text = "";
        }else{
          FamilyFriend = "YES";
          FamilyFriend_text = rset.getString(18);
        }
        if(rset.getInt(19) == 0){
          UrgentCare = "";
          UrgentCare_text = "";
        }else{
          UrgentCare = "YES";
          UrgentCare_text = rset.getString(20);
        }
        if(rset.getInt(21) == 0){
          CommunityEvent = "";
          CommunityEvent_text = "";
        }else{
          CommunityEvent = "YES";
          CommunityEvent_text = rset.getString(22);
        }
        if(rset.getString(23) == "" || rset.getString(23) == null){
          Work_text = "";
        }else{
          Work_text = rset.getString(23);
        }
        if(rset.getString(24) == "" || rset.getString(24) == null){
          Physician_text = "";
        }else{
          Physician_text = rset.getString(24);
        }
        if(rset.getString(25) == "" || rset.getString(25) == null){
          Other_text = "";
        }else{
          Other_text = rset.getString(25);
        }
      }
      rset.close();
      stmt.close();

//      out.println("Other_text-:"+Other_text);
//      out.println("CommunityEvent-:"+CommunityEvent+"CommunityEvent_text-:"+CommunityEvent_text);
//      out.println("School_text-:"+School_text+"School_text:-"+School_text);
//      out.println("ReturnPatient-:"+ReturnPatient);
//      out.println("MotorVehAccidentString-:"+MotorVehAccidentString);
//      out.println("LastName-:"+LastName);
//      out.println("CityStateZipER-:"+CityStateZipER);


      String inputFilePath = "";
      if(ClientId == 8){
        inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/Admin.pdf"; // Existing file
      }else if(ClientId == 9){
        inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/adminvictoria.pdf"; // Existing file
      }else if(ClientId == 10){
        inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/adminoddasa.pdf"; // Existing file
      }else if(ClientId == 12){
        inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/adminsaustin.pdf"; // Existing file
      }

      String outputFilePath = "/sftpdrive/AdmissionBundlePdf/SAustin/"+FirstNameNoSpaces+LastName+ID+"_"+DateTime+".pdf"; // New file
      OutputStream fos = new FileOutputStream(new File(outputFilePath));

      //0, 800 will write text on TOP LEFT of pdf page
      //0, 0 will write text on BOTTOM LEFT of pdf page


      PdfReader pdfReader = new PdfReader(inputFilePath);
      PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

      GenerateBarCode barCode = new GenerateBarCode();
      String BarCodeFilePath = barCode.GetBarCode(request, out, conn, servletContext, UserId, Database, ClientId, MRN);

      Image image = Image.getInstance(BarCodeFilePath);
      image.scaleAbsolute(150, 30); //Scale image's width and height

      for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
        if (i == 1) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);


          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 535); // set x and y co-ordinates
          pdfContentByte.showText(ReturnPatient); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 520); // set x and y co-ordinates
          pdfContentByte.showText(Google); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 500); // set x and y co-ordinates
          pdfContentByte.showText(MapSearch); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 485); // set x and y co-ordinates
          pdfContentByte.showText(Billboard); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 465); // set x and y co-ordinates
          pdfContentByte.showText(OnlineReview); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 450); // set x and y co-ordinates
          pdfContentByte.showText(TV); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 435); // set x and y co-ordinates
          pdfContentByte.showText(Website); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 415); // set x and y co-ordinates
          pdfContentByte.showText(BuildingSignDriveBy); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 398); // set x and y co-ordinates
          pdfContentByte.showText(Facebook); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 380); // set x and y co-ordinates
          pdfContentByte.showText(School); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(190, 380); // set x and y co-ordinates
          pdfContentByte.showText(School_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 365); // set x and y co-ordinates
          pdfContentByte.showText(Twitter); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 348); // set x and y co-ordinates
          pdfContentByte.showText(Magazine); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(200, 348); // set x and y co-ordinates
          pdfContentByte.showText(Magazine_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 330); // set x and y co-ordinates
          pdfContentByte.showText(Newspaper); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(220, 330); // set x and y co-ordinates
          pdfContentByte.showText(Newspaper_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 312); // set x and y co-ordinates
          pdfContentByte.showText(FamilyFriend); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(220, 312); // set x and y co-ordinates
          pdfContentByte.showText(FamilyFriend_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 295); // set x and y co-ordinates
          pdfContentByte.showText(UrgentCare); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(220, 295); // set x and y co-ordinates
          pdfContentByte.showText(UrgentCare_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 278); // set x and y co-ordinates
          pdfContentByte.showText(CommunityEvent); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(250, 278); // set x and y co-ordinates
          pdfContentByte.showText(CommunityEvent_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(150, 225); // set x and y co-ordinates
          pdfContentByte.showText(Work_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(180, 210); // set x and y co-ordinates
          pdfContentByte.showText(Physician_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(150, 195); // set x and y co-ordinates
          pdfContentByte.showText(Other_text); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(80, 85); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(480, 85); // set x and y co-ordinates
          pdfContentByte.showText(" "); // add the text
          pdfContentByte.endText();
        }

        if (i == 2) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

          // Add text in existing PDF
          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setTextMatrix(105, 640); // set x and y co-ordinates
          pdfContentByte.showText(LastName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(350, 640); // set x and y co-ordinates
          pdfContentByte.showText(FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(500, 640); // set x and y co-ordinates
          pdfContentByte.showText(MiddleInitial); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 605); // set x and y co-ordinates
          pdfContentByte.showText("Title: " + Title); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(200, 600); // set x and y co-ordinates
          pdfContentByte.showText(MaritalStatus); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(340, 600); // set x and y co-ordinates
          pdfContentByte.showText(DOB); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(440, 600); // set x and y co-ordinates
          pdfContentByte.showText(Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(510, 600); // set x and y co-ordinates
          pdfContentByte.showText(gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 570); // set x and y co-ordinates
          pdfContentByte.showText(Address); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(260, 570); // set x and y co-ordinates
          pdfContentByte.showText(CityStateZip); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(460, 570); // set x and y co-ordinates
          pdfContentByte.showText(PhNumber); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(50, 540); // set x and y co-ordinates
          pdfContentByte.showText(SSN); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(170, 540); // set x and y co-ordinates
          pdfContentByte.showText(Occupation); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(350, 540); // set x and y co-ordinates
          pdfContentByte.showText(Employer); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(470, 540); // set x and y co-ordinates
          pdfContentByte.showText(EmployerPhone); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 510); // set x and y co-ordinates
          pdfContentByte.showText(PriCarePhy); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(200, 510); // set x and y co-ordinates
          pdfContentByte.showText(Email); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(400, 510); // set x and y co-ordinates
          pdfContentByte.showText(ReasonVisit); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 445); // set x and y co-ordinates
          pdfContentByte.showText(WorkersCompPolicyString); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(330, 445); // set x and y co-ordinates
          pdfContentByte.showText(MotorVehAccidentString); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 415); // set x and y co-ordinates
          pdfContentByte.showText(PriInsurance); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(280, 415); // set x and y co-ordinates
          pdfContentByte.showText(MemId); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(420, 415); // set x and y co-ordinates
          pdfContentByte.showText(GrpNumber); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 375); // set x and y co-ordinates
          pdfContentByte.showText(PriInsuranceName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(260, 375); // set x and y co-ordinates
          pdfContentByte.showText(AddressIfDifferent); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(460, 375); // set x and y co-ordinates
          pdfContentByte.showText(CityStateZip); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 335); // set x and y co-ordinates
          pdfContentByte.showText(PrimaryDOB); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(160, 335); // set x and y co-ordinates
          pdfContentByte.showText(PrimarySSN); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(270, 335); // set x and y co-ordinates
          pdfContentByte.showText(PatientRelationtoPrimary); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(460, 335); // set x and y co-ordinates
          pdfContentByte.showText(PhNumber); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 300); // set x and y co-ordinates
          pdfContentByte.showText(PrimaryOccupation); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(160, 300); // set x and y co-ordinates
          pdfContentByte.showText(PrimaryEmployer); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(260, 300); // set x and y co-ordinates
          pdfContentByte.showText(EmployerAddress); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(460, 300); // set x and y co-ordinates
          pdfContentByte.showText(EmployerPhone); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 275); // set x and y co-ordinates
          pdfContentByte.showText(SecondryInsurance); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(260, 275); // set x and y co-ordinates
          pdfContentByte.showText(SubscriberName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(420, 275); // set x and y co-ordinates
          pdfContentByte.showText(SubscriberDOB); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(60, 240); // set x and y co-ordinates
          pdfContentByte.showText(PatientRelationshiptoSecondry); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(260, 240); // set x and y co-ordinates
          pdfContentByte.showText(MemberID_2); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(420, 240); // set x and y co-ordinates
          pdfContentByte.showText(GroupNumber_2); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(50, 190); // set x and y co-ordinates
          pdfContentByte.showText(NextofKinName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(200, 190); // set x and y co-ordinates
          pdfContentByte.showText(RelationToPatientER); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(350, 190); // set x and y co-ordinates
          pdfContentByte.showText(PhoneNumberER); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(510, 190); // set x and y co-ordinates
          pdfContentByte.showText(LeaveMessageERString); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(40, 150); // set x and y co-ordinates
          pdfContentByte.showText(AddressER); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(350, 150); // set x and y co-ordinates
          pdfContentByte.showText(CityStateZipER); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(440, 75); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();
        }

        if (i == 3) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(325, 210); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(340, 130); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();
        }

        if (i == 4) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(285, 70); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();
        }

        if (i == 5) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(440, 395); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(330, 250); // set x and y co-ordinates
          pdfContentByte.showText(" "); // add the text Here Name of the Qualified Personal
          pdfContentByte.endText();
        }

        if (i == 6) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(95, 585); // set x and y co-ordinates
          pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(300, 585); // set x and y co-ordinates
          pdfContentByte.showText(DOB); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(490, 585); // set x and y co-ordinates
          pdfContentByte.showText(PatientRelationtoPrimary); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(120, 560); // set x and y co-ordinate
          pdfContentByte.showText(PriInsuranceName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(180, 535); // set x and y co-ordinates
          pdfContentByte.showText(MemId); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 510); // set x and y co-ordinates
          pdfContentByte.showText(DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 385); // set x and y co-ordinates
          pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName);
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size

          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(330, 210); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();
        }

        if (i == 7) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(130, 490); // set x and y co-ordinates
          pdfContentByte.showText(" "); // add the text Health Insurance
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 465); // set x and y co-ordinates
          pdfContentByte.showText(SubscriberName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(340, 465); // set x and y co-ordinates
          pdfContentByte.showText(SubscriberDOB); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(110, 440); // set x and y co-ordinates
          pdfContentByte.showText(MemberID_2); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(260, 440); // set x and y co-ordinates
          pdfContentByte.showText(GroupNumber_2); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(120, 415); // set x and y co-ordinates
          pdfContentByte.showText(" "); // add the text Effective Date???
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(50, 240); // set x and y co-ordinates
          pdfContentByte.showText(" "); // add the text Printed Name of the person completing form????
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(430, 170); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(430, 130); // set x and y co-ordinates
          pdfContentByte.showText(Date); // add the text
          pdfContentByte.endText();
        }

        if (i == 8) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(50, 650); // set x and y co-ordinates
          pdfContentByte.showText(Title + " " + FirstName + " " + MiddleInitial + " " + LastName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 650); // set x and y co-ordinates
          pdfContentByte.showText(PhNumber); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(50, 610); // set x and y co-ordinates
          pdfContentByte.showText(DOB); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 610); // set x and y co-ordinates
          pdfContentByte.showText(SSN); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(130, 480); // set x and y co-ordinates
          pdfContentByte.showText(" "); // add the text Facility or Physician to receive information???
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(130, 440); // set x and y co-ordinates
          pdfContentByte.showText(""); // add the text PHONE NUMBER???
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(340, 440); // set x and y co-ordinates
          pdfContentByte.showText(""); // add the text FAX NUMBER???
          pdfContentByte.endText();


          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(130, 400); // set x and y co-ordinates
          pdfContentByte.showText(""); // add the text Address???
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set font and size
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(130, 360); // set x and y co-ordinates
          pdfContentByte.showText(""); // add the text CityStateZip???
          pdfContentByte.endText();

        }

        if (i == 9) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();
        }

        if (i == 10) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();
        }

        if (i == 11) {
          PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(35, 750); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN); // add the text
          pdfContentByte.endText();

          image.setAbsolutePosition(10, 710); //Set position for image in PDF
          pdfContentByte.addImage(image);

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 765); // set x and y co-ordinates
          pdfContentByte.showText(LastName + " , " + FirstName); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 755); // set x and y co-ordinates
          pdfContentByte.showText(ClientName + "        Sex:" + gender); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 745); // set x and y co-ordinates
          pdfContentByte.showText("DOB: " + DOB + "        Age:" + Age); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 735); // set x and y co-ordinates
          pdfContentByte.showText("MRN: " + MRN + "        DOS: " + DOS); // add the text
          pdfContentByte.endText();

          pdfContentByte.beginText();
          pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 8); // set fonts zine and name
          pdfContentByte.setColorFill(BaseColor.BLACK);
          pdfContentByte.setTextMatrix(390, 725); // set x and y co-ordinates
          pdfContentByte.showText("Dr. " + DoctorName); // add the text
          pdfContentByte.endText();
        }

      }
      pdfStamper.close(); //close pdfStamper


//      response.setContentType("application/pdf");
//      out = response.getWriter();
//      String filepath = outputFilePath;
//      response.setHeader("Content-Disposition", "attachment; filename=" + filepath + ";");
//      FileInputStream fileOut = new FileInputStream(FirstNameNoSpaces+LastName+ID+"_"+DateTime+".pdf");
//      fileOut.close();
//      out.close();

      File pdfFile = new File(outputFilePath);
      response.setContentType("application/pdf");
      response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces+LastName+ID+"_"+DateTime+".pdf");
      response.setContentLength((int) pdfFile.length());

      FileInputStream fileInputStream = new FileInputStream(pdfFile);
      OutputStream responseOutputStream = response.getOutputStream();
      int bytes;
      while ((bytes = fileInputStream.read()) != -1) {
        responseOutputStream.write(bytes);
      }

    }catch(Exception e){
      out.println(e.getMessage());
    }
  }
}
