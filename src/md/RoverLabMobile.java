package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@SuppressWarnings("Duplicates")
public class RoverLabMobile extends HttpServlet {
    private final String USER_AGENT = "Mozilla/5.0";
    private Connection conn = null;
    private ResultSet rset = null;
    private String Query = "";
    private Statement stmt = null;

    public static int getAge(LocalDate dob) {
        LocalDate curDate = LocalDate.now();
        return Period.between(dob, curDate).getYears();
    }

    private static BufferedImage imageToBufferedImage(final Image image) {
        final BufferedImage bufferedImage =
                new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return bufferedImage;
    }

    public static Image makeColorTransparent(final BufferedImage im, final Color color) {
        final ImageFilter filter = new RGBImageFilter() {
            // the color we are looking for (white)... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFFFFFFFF;

            public final int filterRGB(final int x, final int y, final int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                } else {
                    // nothing to do
                    return rgb;
                }
            }
        };

        final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    public static boolean isValid(File in) throws IOException, InterruptedException {
        Image img = ImageIO.read(in);
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        int[] pixels = new int[w * h];
        PixelGrabber pg = new PixelGrabber(img, 0, 0, w, h, pixels, 0, w);
        pg.grabPixels();
        boolean isValid = false;
        for (int pixel : pixels) {
            Color color = new Color(pixel);
            if (color.getAlpha() == 0 || color.getRGB() != Color.WHITE.getRGB()) {
                isValid = true;
                break;
            }
        }
        return isValid;
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        requestHandler(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        requestHandler(request, response);
    }

    private void requestHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ActionID = "";
        int facilityIndex = 0;
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        UtilityHelper helper = new UtilityHelper();
        String dbName = "";
        String dirName = "";
        String userId = "";
        String PRF_name = "";


        try {
            ActionID = request.getParameter("RequestName");
            userId = request.getParameter("userId");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("Error", "Unable to connect. Our team is looking into it!");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }

            Query = "SELECT ClientIndex FROM oe.MobileUsers WHERE ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim('" + userId + "'))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                facilityIndex = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "SELECT dbname,DirectoryName,PRF_name FROM oe.clients WHERE Id = " + facilityIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                dbName = rset.getString(1).trim();
                dirName = rset.getString(2).trim();
                PRF_name = rset.getString(3).trim();
            }
            rset.close();
            stmt.close();

            PRF_name = "RoverLabMobile.html";
            switch (ActionID) {
                case "GetValues":
                    GetValues(request, out, conn, context, dbName, helper, facilityIndex, PRF_name, userId);
                    break;
                case "SaveData":
                    SaveData(request, out, conn, context, dbName, userId, helper, dirName);
                    break;
                case "GETINPUTRoverLab":
                    BundlePrimescope(request, out, this.conn, context, response, userId, dbName, facilityIndex, dirName);
                    break;
/*                case "SignPdf":
                    SignPdf(request, out, conn, context, userId, dbName, facilityIndex, dirName, helper, pageCount, outputFilePath, FileName, PatientRegId);
                    break;*/
                case "GetData":
                    GetData(request, response, out, conn, context, userId, dbName, facilityIndex);
                    break;
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in RoverLabMobile ** (handleRequest)", context, e, "RoverLabMobile", "handleRequest", conn);
            Services.DumException("PatientRegRoverLab", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                helper.SendEmailWithAttachment("Error in RoverLabMobile ** (handleRequest -- SqlException)", context, e, "RoverLabMobile", "handleRequest", conn);
                Services.DumException("PatientRegRoverLab", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }

    void GetValues(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String Database, UtilityHelper helper, int facilityIndex, String PRF_name, String userId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Date = "";
            StringBuffer Month = new StringBuffer();
            StringBuffer Day = new StringBuffer();
            StringBuffer Year = new StringBuffer();
            StringBuffer ProfessionalPayersList = new StringBuffer();

            Query = "Select Date_format(now(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Date = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers where id  in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955,64,1545,3646,8589,200,201,202,203,204,205,206,207,3649,5978,8206,4763,3465,3466,3467,3468,41,387,388,389,697,698,4757) AND Status != 100 group by PayerId";//where PayerName like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ProfessionalPayersList.append("<option value=''>Select Insurance</option>");
            while (rset.next()) {
                ProfessionalPayersList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();

            //Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers where id not in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955) group by PayerId ";//where PayerName not like '%Texas%'";
            Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers where PayerName like  '%Texas%' OR PayerName like '%TX%' AND Status != 100";//where PayerName not like '%Texas%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ProfessionalPayersList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();


            String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            int day = 1;
            int year = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = 1; i <= month.length; i++) {
                if (i < 10)
                    Month.append("<option value=0" + i + ">" + month[i - 1] + "</option>");
                else
                    Month.append("<option value=" + i + ">" + month[i - 1] + "</option>");
            }
            for (int i = 1; i <= 31; i++) {
                if (i < 10)
                    Day.append("<option value=0" + i + ">" + i + "</option>");
                else
                    Day.append("<option value=" + i + ">" + i + "</option>");
            }
            for (int i = 1901; i <= year; i++) {
                if (i == year) {
                    Year.append("<option value=" + i + " selected>" + i + "</option>");
                } else {
                    Year.append("<option value=" + i + ">" + i + "</option>");
                }
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Date", String.valueOf(Date));
            Parser.SetField("ClientIndex", String.valueOf(facilityIndex));
            Parser.SetField("ClientIndex_logo", String.valueOf(facilityIndex));
            Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
            Parser.SetField("ProfessionalPayersList2", String.valueOf(ProfessionalPayersList));
            Parser.SetField("userId", userId);
            Parser.SetField("Month", String.valueOf(Month));
            Parser.SetField("Day", String.valueOf(Day));
            Parser.SetField("Year", String.valueOf(Year));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/PRF_files/" + PRF_name);
        } catch (Exception ex) {
            System.out.println("ERROR IN MOBILE INPUT " + ex.getMessage());
        }
    }

    void BundlePrimescope(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
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
        String Address2 = "";
        String StreetAddress2 = "";
        String City = "";
        String State = "";
        String Country = "";
        String County = "";
        String ZipCode = "";
        String Ethnicity = "";
        String Race = "";
        String SSN = "";
        String SpCarePhy = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        int TravellingChk = 0;
        String TravelWhen = "";
        String TravelWhere = "";
        String TravelHowLong = "";
        int COVIDExposedChk = 0;
        int COVIDPositveChk = 0;
        String CovidPositiveDate = "";
        String CovidExpWhen = "";
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
        String SympHeadache = "0";
        String SympLossTaste = "0";
        String SympShortBreath = "0";
        String SympCongestion = "0";
        String AddInfoTextArea = "";
        String GuarantorName = "";
        String GuarantorDOB = "";
        String GuarantorNumber = "";
        String GuarantorSSN = "";
        String SympEyeConjunctivitis = "0";
        String PriCarePhy = "";
        String ReasonVisit = "";
        int WorkersCompPolicy = 0;
        int MotorVehAccident = 0;
        String PriInsurance = "";
        String MemId = "";
        String GrpNumber = "";
        String PriInsuranceName = "";
        String OtherInsuranceName = "";


        String PolicyHolder = "";
        String NoInsurance = "";
        String testtype = "";
        String Mid_turbinate_Nasal_Swab = "";
        String datetimeColected = "";
        String DateOnset = "";
        String Result = "";
        String specimentType = "";
        String specimenID = "";
        String ancestry = "";
        String datetimeofspecimen = "";
        String insuranceAgreement = "";
        String PolicyHolderDOB = "";
        String RelationshipToPH = "";
        String memberID = "";
        String PolicyType = "";

        String Providence = "";
        String PSSNtext = "";
        String SSLtext = "";
        String SIDtext = "";


        String ICD_10_J80 = "";
        String ICD_10_J20_8 = "";
        String ICD_10_J22 = "";
        String ICD_10_J1_89 = "";
        String ICD_10_J98_8 = "";
        String ICD_10_R05 = "";
        String ICD_10_R06_02 = "";
        String ICD_10_R50_9 = "";
        String ICD_10_Z20_828 = "";
        String ICD_10_Z03_818 = "";
        String ICD_10_B97_29 = "";
        String InternationalTravel = "";
        String closeContact = "";
        String Fever = "";
        String HowLongFever = "";
        String Cough = "";
        String HowLongCough = "";
        String breathShortness = "";
        String HowLongbreathShortness = "";
        String breathingDifficulty = "";
        String HowLongbreathingDifficulty = "";


        String ClinicName = "";
        String PhysicianName = "";
        String NPI = "";
        String AddressClinical = "";
        String CityClinical = "";
        String StateClinical = "";
        String ZipClinical = "";
        String Fax = "";
        String PhClinical = "";
        String EmailClinical = "";

        String SendReportTo = "";
        String PCFR = "";
        final int ID = Integer.parseInt(request.getParameter("ID").trim());
        String userId = request.getParameter("userId").trim();
        int facilityIndex = 0;
        String dbName = "";
        try {
            Query = "SELECT ClientIndex FROM oe.MobileUsers WHERE ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim('" + userId + "'))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                facilityIndex = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "SELECT dbname,DirectoryName,PRF_name FROM oe.clients WHERE Id = " + facilityIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                dbName = rset.getString(1).trim();
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
                Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), " +
                        " IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'), " +
                        " IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(City,'-'), IFNULL(State,'-'), IFNULL(ZipCode,'-')," +
                        " IFNULL(PhNumber,'-'),IFNULL(Email,'-'),  " +
                        " IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), IFNULL(Race,'-')," +
                        " IFNULL(Ethnicity,'-'), IFNULL(ClinicName,'-'), IFNULL(PhysicianName,'-'), IFNULL(NPI,'-'), IFNULL(IFNULL(AddressClinical,'-'),'-')," +
                        " IFNULL(CityClinical,'-') , IFNULL(StateClinical,'-'), IFNULL(ZipClinical,'-'), IFNULL(Fax,'-'), IFNULL(EmailClinical,'-')," +
                        " IFNULL(PhClinical,'-'), IFNULL(SpecimenType,'-'), DATE_FORMAT(DateTimeOfSpecimenCollection,'%m/%d/%Y %T')," +
                        " IFNULL(InsuranceCompany,'-'),IFNULL(GroupNumber,'-'), IFNULL(PolicyHolderName,'-')," +
                        " IFNULL(NoInsuranceAgreement,'-'), IFNULL(Providence,'-'), IFNULL(SocialSecurity,'-'), IFNULL(StateDriverLicense,'-'), IFNULL(StateID,'-')," +
                        " ICD_10_J80 ,ICD_10_J20_8 ,ICD_10_J22 ,ICD_10_J1_89 ,ICD_10_J98_8 ,ICD_10_R05 ,ICD_10_R06_02 ,ICD_10_R50_9 ,ICD_10_Z20_828 ,ICD_10_Z03_818 ,ICD_10_B97_29," +
                        " InternationalTravel ,closeContact ,Fever ,HowLongFever ,Cough ,HowLongCough ,breathShortness ,HowLongbreathShortness ,breathingDifficulty ,HowLongbreathingDifficulty," +
                        "  IFNULL(DATE_FORMAT(DateTimeCollected,'%m/%d/%Y %T'), '-') ,  IFNULL(DATE_FORMAT(DateOnSet,'%m/%d/%Y'), '-'), IFNULL(TestType,'-'), IFNULL(Result,'-')," +
                        " specimenID ,ancestry ,PolicyHolderDOB ,RelationshipToPH ,memberID ,PolicyType , SendReportTo" +
                        "  From " + dbName + ".PatientRegRoverLab Where ID = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    LastName = rset.getString(1);
                    FirstName = rset.getString(2);
                    MiddleInitial = rset.getString(3);
                    DOB = rset.getString(4);
                    gender = rset.getString(5);
                    Address = rset.getString(6);
                    City = rset.getString(7);
                    State = rset.getString(8);
                    ZipCode = rset.getString(9);
                    PhNumber = rset.getString(10);
                    Email = rset.getString(11);
                    Race = rset.getString(13);
                    Ethnicity = rset.getString(14);
                    ClinicName = rset.getString(15);
                    PhysicianName = rset.getString(16);
                    NPI = rset.getString(17);
                    AddressClinical = rset.getString(18);
                    CityClinical = rset.getString(19);
                    StateClinical = rset.getString(20);
                    ZipClinical = rset.getString(21);
                    Fax = rset.getString(22);
                    EmailClinical = rset.getString(23);
                    PhClinical = rset.getString(24);
                    specimentType = rset.getString(25);
                    datetimeofspecimen = rset.getString(26);
                    PriInsuranceName = rset.getString(27);
                    GrpNumber = rset.getString(28);
                    PolicyHolder = rset.getString(29);

                    NoInsurance = rset.getString(30);
                    Providence = rset.getString(31);
                    PSSNtext = rset.getString(32);
                    SSLtext = rset.getString(33);
                    SIDtext = rset.getString(34);

                    ICD_10_J80 = rset.getString(35);
                    ICD_10_J20_8 = rset.getString(36);
                    ICD_10_J22 = rset.getString(37);
                    ICD_10_J1_89 = rset.getString(38);
                    ICD_10_J98_8 = rset.getString(39);
                    ICD_10_R05 = rset.getString(40);
                    ICD_10_R06_02 = rset.getString(41);
                    ICD_10_R50_9 = rset.getString(42);
                    ICD_10_Z20_828 = rset.getString(43);
                    ICD_10_Z03_818 = rset.getString(44);
                    ICD_10_B97_29 = rset.getString(45);


                    InternationalTravel = rset.getString(46);
                    closeContact = rset.getString(47);
                    Fever = rset.getString(48);
                    HowLongFever = rset.getString(49);
                    Cough = rset.getString(50);
                    HowLongCough = rset.getString(51);
                    breathShortness = rset.getString(52);
                    HowLongbreathShortness = rset.getString(53);
                    breathingDifficulty = rset.getString(54);
                    HowLongbreathingDifficulty = rset.getString(55);

                    datetimeColected = rset.getString(56);
                    DateOnset = rset.getString(57);
                    testtype = rset.getString(58);
                    Result = rset.getString(59);

                    specimenID = rset.getString(60);
                    ancestry = rset.getString(61);
                    PolicyHolderDOB = rset.getString(62);
                    RelationshipToPH = rset.getString(63);
                    memberID = rset.getString(64);
                    PolicyType = rset.getString(65);
                    SendReportTo = rset.getString(66);


                }
                rset.close();
                stmt.close();


                Query = "Select PayerName from oe_2.ProfessionalPayers where Id = " + PriInsuranceName + "";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PriInsuranceName = rset.getString(1);
                }
                rset.close();
                stmt.close();

            } catch (Exception e) {
                out.println("Error In PateintReg:--" + e.getMessage());
                out.println(Query);
            }
//            if (SelfPayChk == 1) {
            String UID = "";
            com.itextpdf.text.Image SignImages = null;
            com.itextpdf.text.Image SignImages2 = null;
            File tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_0_" + ID + "_P.png");
            boolean exists = tmpDir.exists();
            out.print("exists " + exists);
            if (exists) {
                Query = "Select UID from " + Database + ".SignRequest where PatientRegId = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    UID = rset.getString(1);
                }
                rset.close();
                stmt.close();

                SignImages = com.itextpdf.text.Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_0_" + ID + "_P.png");
                SignImages.scaleAbsolute(80.0f, 30.0f);
                tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_1_" + ID + "_HPC.png");
                exists = tmpDir.exists();
                out.print("exists " + exists);
                if (exists) {
                    SignImages2 = com.itextpdf.text.Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_1_" + ID + "_HPC.png");
                    SignImages2.scaleAbsolute(80.0f, 30.0f);
                }
                //outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + UID + "_.pdf";
            } else {
                SignImages = null;
                SignImages2 = null;
            }

            String inputFilePath = null;
            try {
                inputFilePath = "";
                final InetAddress ip = InetAddress.getLocalHost();
                final String hostname = ip.getHostName();
                if (hostname.trim().equals("rover-01")) {
                    inputFilePath = "";
                } else {
                    inputFilePath = "/sftpdrive";
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            inputFilePath += "/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/PrimeScopeReg.pdf";

            FirstNameNoSpaces = FirstName.replaceAll("\\s+", "");

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
//            out.println("inputFilePath -> "+inputFilePath);
//            out.println("outputFilePath -> "+outputFilePath);

            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {


                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);


                    /////////////////////////////////////Patient Information//////////////////////////////////////////////////
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45, 752); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName);//"First Name"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(165, 752); // set x and y co-ordinates
                    pdfContentByte.showText(LastName);//"Last Name"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260, 752); // set x and y co-ordinates
                    pdfContentByte.showText(MiddleInitial);//"Middle"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340, 752); // set x and y co-ordinates
                    pdfContentByte.showText(ClinicName);//"PrimeScope Diagnostics, LLC"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(53, 739); // set x and y co-ordinates
                    pdfContentByte.showText(Address);//"Address"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(355, 739); // set x and y co-ordinates
                    pdfContentByte.showText(PhysicianName);//"SHIRAZ PIRALI, M.D."); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500, 739); // set x and y co-ordinates
                    pdfContentByte.showText(NPI);//"1366464034"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45, 729); // set x and y co-ordinates
                    pdfContentByte.showText(City);//"City"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160, 729); // set x and y co-ordinates
                    pdfContentByte.showText(State);//"State"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250, 729); // set x and y co-ordinates
                    pdfContentByte.showText(ZipCode);//"Zip"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350, 729); // set x and y co-ordinates
                    pdfContentByte.showText(AddressClinical);//"11822 Westheimer Rd"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45, 719); // set x and y co-ordinates
                    pdfContentByte.showText(PhNumber);//"Phone"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(155, 719); // set x and y co-ordinates
                    pdfContentByte.showText(DOB);//"DOB"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260, 719); // set x and y co-ordinates
                    pdfContentByte.showText(gender);//"Gender"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340, 719); // set x and y co-ordinates
                    pdfContentByte.showText(CityClinical);//"HOUSTON"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(440, 719); // set x and y co-ordinates
                    pdfContentByte.showText(StateClinical);//"TX"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(520, 719); // set x and y co-ordinates
                    pdfContentByte.showText(ZipClinical);//"77077"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(55, 708); // set x and y co-ordinates
                    pdfContentByte.showText(ancestry);//"Ancestry"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340, 708); // set x and y co-ordinates
                    pdfContentByte.showText(PhClinical);//"(469)638-5512"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500, 708); // set x and y co-ordinates
                    pdfContentByte.showText(Fax);//"Fax"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45, 697); // set x and y co-ordinates
                    pdfContentByte.showText(Email);//"Email"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340, 697); // set x and y co-ordinates
                    pdfContentByte.showText(EmailClinical);//"DALLAS@HEAL360.COM"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(75, 671); // set x and y co-ordinates
                    pdfContentByte.showText(specimenID);//"Specimen ID"); // add the text
                    pdfContentByte.endText();
//                    out.println("Before ddatetimeofspecimen ->  "+datetimeofspecimen);


                    if (datetimeofspecimen != null) {
                        String[] temp = datetimeofspecimen.split(" ");
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(186, 671); // set x and y co-ordinates
                        pdfContentByte.showText(temp[0]);//"Date"); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(266, 671); // set x and y co-ordinates
                        pdfContentByte.showText(temp[1]);//"Time"); // add the text
                        pdfContentByte.endText();
                    }

//                    out.println("after ddatetimeofspecimen ->  "+datetimeofspecimen);


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(320, 660); // set x and y co-ordinates
                    pdfContentByte.showText(PriInsuranceName);//"Insurance"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80, 660); // set x and y co-ordinates
                    pdfContentByte.showText(specimentType);//"Specimen Type"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(385, 645); // set x and y co-ordinates
                    pdfContentByte.showText(PolicyHolder);//"Policy Holder Name"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(360, 632); // set x and y co-ordinates
                    pdfContentByte.showText(RelationshipToPH);//"Relationship"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(475, 632); // set x and y co-ordinates
                    pdfContentByte.showText(PolicyHolderDOB);//"DOB"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350, 616); // set x and y co-ordinates
                    pdfContentByte.showText(GrpNumber);//"Group#"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(487, 616); // set x and y co-ordinates
                    pdfContentByte.showText(memberID);//"Member#"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(358, 602); // set x and y co-ordinates
                    pdfContentByte.showText(PolicyType);//"Policy type"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150, 500); //set x and y co-ordinates
                    pdfContentByte.showText(NoInsurance);//"Y/N"); //  NOInsurance  add the text
                    pdfContentByte.endText();


                    if (Providence.equals("Patient Social Security")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(395, 553); // set x and y co-ordinates
                        pdfContentByte.showText(PSSNtext);//"PSS"); //  PSS add the text
                        pdfContentByte.endText();
                    } else if (Providence.equals("State Driver License")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(390, 537); // set x and y co-ordinates
                        pdfContentByte.showText(SSLtext);//"SDL"); //  SDL add the text
                        pdfContentByte.endText();
                    } else if (Providence.equals("State ID")) {
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(340, 521); // set x and y co-ordinates
                        pdfContentByte.showText(SIDtext);//"SID"); //  SID add the text
                        pdfContentByte.endText();
                    }


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(205, 443); // set x and y co-ordinates
                    pdfContentByte.showText(InternationalTravel);//"Y/N"); // international travel add the text for agreement
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430, 463); // set x and y co-ordinates
                    pdfContentByte.showText(closeContact);//"Y/N"); // closeContact travel add the text for agreement
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220, 431); // set x and y co-ordinates
                    pdfContentByte.showText(Fever);//"Y/N"); // Fever add the text for agreement
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(65, 421); // set x and y co-ordinates
                    pdfContentByte.showText(HowLongFever);//"How Long"); // Fever add the text for agreement
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(335, 431); // set x and y co-ordinates
                    pdfContentByte.showText(Cough);//"Y/N"); // cough add the text for agreement
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(347, 421); // set x and y co-ordinates
                    pdfContentByte.showText(HowLongCough);//"How Long"); // cough add the text for agreement
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(465, 431); // set x and y co-ordinates
                    pdfContentByte.showText(breathShortness);//"Y/N"); // Shortness add the text for agreement
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(434, 421); // set x and y co-ordinates
                    pdfContentByte.showText(HowLongbreathShortness);//"How Long"); // Shortness add the text for agreement
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(570, 431); // set x and y co-ordinates
                    pdfContentByte.showText(breathingDifficulty);//"Y/N"); // Difficulty add the text for agreement
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(538, 421); // set x and y co-ordinates
                    pdfContentByte.showText(HowLongbreathingDifficulty);//"How Long"); // Difficulty add the text for agreement
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(55, 225); // set x and y co-ordinates
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);// LastName, FirstName Middle"); // add the text
                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(320, 225); // set x and y co-ordinates
//                    pdfContentByte.showText("Sign Image"); // add the text
//                    pdfContentByte.endText();


                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(320, 225);
                        pdfContentByte.addImage(SignImages);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(510, 225); // set x and y co-ordinates
                    pdfContentByte.showText(Date);//"DATE"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(105, 145); // set x and y co-ordinates
                    pdfContentByte.showText(SendReportTo);//"Send Report to"); // add the text
                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(200, 125); // set x and y co-ordinates
//                    pdfContentByte.showText("Healthcare Provider Sign Image"); // add the text
//                    pdfContentByte.endText();
                    out.println("Healthcare sign");
                    if (SignImages2 != null) {
                        SignImages2.setAbsolutePosition(200, 125);
                        pdfContentByte.addImage(SignImages2);
                    }
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(450, 125); // set x and y co-ordinates
                    pdfContentByte.showText(Date);//"DATE"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(15, 85); // set x and y co-ordinates
                    pdfContentByte.showText(testtype);//"Test Type"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190, 85); // set x and y co-ordinates
                    pdfContentByte.showText(datetimeColected);//"Date & time collected"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(320, 85); // set x and y co-ordinates
                    pdfContentByte.showText(DateOnset);//"Date of Onset"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430, 85); // set x and y co-ordinates
                    pdfContentByte.showText(Result);//"Result"); // add the text
                    pdfContentByte.endText();

                }

            }
//            out.println("Printing Done");
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
//            out.println(e.getMessage());
            System.out.println(e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            System.out.println(str);
        }
    }

    void SaveData(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database, String UserId, UtilityHelper helper, String DirectoryName) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int PatientRegId = 0;
        int AddmissionBundle = 0;
        int ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());
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
        String Address2 = "";
        String StreetAddress2 = "";
        String City = "";
        String State = "";
        String Country = "";
        String County = "";
        String ZipCode = "";
        String Ethnicity = "";
        String Race = "";
        String SSN = "";
        String SpCarePhy = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        int TravellingChk = 0;
        String TravelWhen = "";
        String TravelWhere = "";
        String TravelHowLong = "";
        int COVIDExposedChk = 0;
        int COVIDPositveChk = 0;
        String CovidPositiveDate = "";
        String CovidExpWhen = "";
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
        String SympHeadache = "0";
        String SympLossTaste = "0";
        String SympShortBreath = "0";
        String SympCongestion = "0";
        String AddInfoTextArea = "";
        String GuarantorName = "";
        String GuarantorDOB = "";
        String GuarantorNumber = "";
        String GuarantorSSN = "";
        String SympEyeConjunctivitis = "0";
        String PriCarePhy = "";
        String ReasonVisit = "";
        int WorkersCompPolicy = 0;
        int MotorVehAccident = 0;
        String PriInsurance = "";
        String MemId = "";
        String GrpNumber = "";
        String PriInsuranceName = "";
        String OtherInsuranceName = "";


        String PolicyHolder = "";
        String NoInsurance = "";
        String Covid_19_PCR = "";
        String Mid_turbinate_Nasal_Swab = "";
        String datetimeColected = "";
        String DateOnset = "";
        String COVID_Result = "";
        String specimentType = "";
        String specimenID = "";
        String ancestry = "";
        String datetimeofspecimen = "";
        String insuranceAgreement = "";
        String PolicyHolderDOB = "";
        String RelationshipToPH = "";
        String memberID = "";
        String PolicyType = "";

        String Providence = "";
        String PSSNtext = "";
        String SSLtext = "";
        String SIDtext = "";


        String ICD_10_J80 = "";
        String ICD_10_J20_8 = "";
        String ICD_10_J22 = "";
        String ICD_10_J1_89 = "";
        String ICD_10_J98_8 = "";
        String ICD_10_R05 = "";
        String ICD_10_R06_02 = "";
        String ICD_10_R50_9 = "";
        String ICD_10_Z20_828 = "";
        String ICD_10_Z03_818 = "";
        String ICD_10_B97_29 = "";
        String InternationalTravel = "";
        String closeContact = "";
        String Fever = "";
        String HowLongFever = "";
        String Cough = "";
        String HowLongCough = "";
        String breathShortness = "";
        String HowLongbreathShortness = "";
        String breathingDifficulty = "";
        String HowLongbreathingDifficulty = "";


        String ClinicName = "";
        String PhysicianName = "";
        String NPI = "";
        String AddressClinical = "";
        String CityClinical = "";
        String StateClinical = "";
        String ZipClinical = "";
        String Fax = "";
        String PhClinical = "";

        String EmailClinical = "";

        String SendReportTo = "";
        String PCFR = "";
        String TestType = "";


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
        int FrVisitedBefore = 0;
        int FrFamiliyVisitedBefore = 0;
        int FrInternet = 0;
        int FrBillboard = 0;
        int FrGoogle = 0;
        int FrBuildingSignage = 0;
        int FrFacebook = 0;
        int FrLivesNear = 0;
        int FrTwitter = 0;
        int FrTV = 0;
        int FrMapSearch = 0;
        int FrEvent = 0;
        String FrPhysicianReferral = "";
        String FrNeurologyReferral = "";
        String FrUrgentCareReferral = "";
        String FrOrganizationReferral = "";
        String FrFriendFamily = "";
        String PatientName = "";
        String ExtendedMRN = "";
        String VisitId = "";
        int MRN = 0;
        String Month = "";
        String Day = "";
        String Year = "";
        String facilityName = helper.getFacilityName(request, conn, servletContext, ClientIndex);
        try {

            try {
                try {
                    if (request.getParameter("Title") == null) {
                        Title = "";
                    } else {
                        Title = request.getParameter("Title").trim();
                    }
                    if (request.getParameter("FirstName") == null) {
                        FirstName = "";
                    } else {
                        FirstName = request.getParameter("FirstName").trim();
//                    System.out.println("FirstName => "+FirstName);
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
/*                    if (request.getParameter("Age") == null) {
                        Age = "0";
                    } else {
                        Age = request.getParameter("Age").trim();
                    }*/
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
                    if (request.getParameter("Address2") == null) {
                        Address2 = "";
                    } else {
                        Address2 = request.getParameter("Address2").trim();
                    }
                    if (request.getParameter("StreetAddress2") == null) {
                        StreetAddress2 = "";
                    } else {
                        StreetAddress2 = request.getParameter("StreetAddress2").trim();
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
                        Country = "";
                    } else {
                        Country = request.getParameter("Country").trim();
                    }
                    if (request.getParameter("Ethnicity") == null) {
                        Ethnicity = "";
                    } else {
                        Ethnicity = request.getParameter("Ethnicity").trim();
                    }
                    if (request.getParameter("Race") == null) {
                        Race = "";
                    } else {
                        Race = request.getParameter("Race").trim();
                    }
                    if (request.getParameter("County") == null) {
                        County = "";
                    } else {
                        County = request.getParameter("County").trim();
                    }
                    if (request.getParameter("ZipCode") == null) {
                        ZipCode = "";
                    } else {
                        ZipCode = request.getParameter("ZipCode").trim();
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
                    if (request.getParameter("PolicyHolder") == null) {
                        PolicyHolder = "";
                    } else {
                        PolicyHolder = request.getParameter("PolicyHolder").trim();
                    }

                    if (request.getParameter("NoInsurance") == null) {
                        NoInsurance = "0";
                    } else {
                        NoInsurance = request.getParameter("NoInsurance").trim();
                    }

                    if (request.getParameter("Covid_19_PCR") == null) {
                        Covid_19_PCR = "0";
                    } else {
                        Covid_19_PCR = request.getParameter("Covid_19_PCR").trim();
                    }

                    if (request.getParameter("Mid_turbinate_Nasal_Swab") == null) {
                        Mid_turbinate_Nasal_Swab = "0";
                    } else {
                        Mid_turbinate_Nasal_Swab = request.getParameter("Mid_turbinate_Nasal_Swab").trim();
                    }

                    if (request.getParameter("datetime") == null) {
                        datetimeColected = null;
                    } else {
                        datetimeColected = request.getParameter("datetime").trim();
                    }

                    if (request.getParameter("DateOnset") == null) {
                        DateOnset = null;
                    } else {
                        DateOnset = request.getParameter("DateOnset").trim();
                    }

                    if (request.getParameter("COVID_Result") == null) {
                        COVID_Result = "";
                    } else {
                        COVID_Result = request.getParameter("COVID_Result").trim();
                    }

                    if (request.getParameter("specimentType") == null) {
                        specimentType = "";
                    } else {
                        specimentType = request.getParameter("specimentType").trim();
                    }

                    if (request.getParameter("datetimeofspecimen") == null) {
                        datetimeofspecimen = "";
                    } else {
                        datetimeofspecimen = request.getParameter("datetimeofspecimen").trim();
                    }

                    if (request.getParameter("insuranceAgreement") == null) {
                        insuranceAgreement = "No";
                    } else {
                        insuranceAgreement = request.getParameter("insuranceAgreement").trim();
                    }

                    if (request.getParameter("Providence") == null) {
                        Providence = "";
                    } else {
                        Providence = request.getParameter("Providence").trim();
                    }

                    if (request.getParameter("PSSNtext") == null) {
                        PSSNtext = "";
                    } else {
                        PSSNtext = request.getParameter("PSSNtext").trim();
                    }
                    if (request.getParameter("SSLtext") == null) {
                        SSLtext = "";
                    } else {
                        SSLtext = request.getParameter("SSLtext").trim();
                    }
                    if (request.getParameter("SIDtext") == null) {
                        SIDtext = "";
                    } else {
                        SIDtext = request.getParameter("SIDtext").trim();
                    }


                    if (request.getParameter("J80") == null) {
                        ICD_10_J80 = "No";
                    } else {
                        ICD_10_J80 = request.getParameter("J80").trim();
                    }

                    if (request.getParameter("J20.8") == null) {
                        ICD_10_J20_8 = "No";
                    } else {
                        ICD_10_J20_8 = request.getParameter("J20.8").trim();
                    }

                    if (request.getParameter("J22") == null) {
                        ICD_10_J22 = "No";
                    } else {
                        ICD_10_J22 = request.getParameter("J22").trim();
                    }

                    if (request.getParameter("J1.89") == null) {
                        ICD_10_J1_89 = "No";
                    } else {
                        ICD_10_J1_89 = request.getParameter("J1.89").trim();
                    }

                    if (request.getParameter("J98.8") == null) {
                        ICD_10_J98_8 = "No";
                    } else {
                        ICD_10_J98_8 = request.getParameter("J98.8").trim();
                    }

                    if (request.getParameter("R05") == null) {
                        ICD_10_R05 = "No";
                    } else {
                        ICD_10_R05 = request.getParameter("R05").trim();
                    }

                    if (request.getParameter("R06.02") == null) {
                        ICD_10_R06_02 = "No";
                    } else {
                        ICD_10_R06_02 = request.getParameter("R06.02").trim();
                    }

                    if (request.getParameter("R50.9") == null) {
                        ICD_10_R50_9 = "No";
                    } else {
                        ICD_10_R50_9 = request.getParameter("R50.9").trim();
                    }

                    if (request.getParameter("Z20.828") == null) {
                        ICD_10_Z20_828 = "No";
                    } else {
                        ICD_10_Z20_828 = request.getParameter("Z20.828").trim();
                    }
                    if (request.getParameter("Z03.818") == null) {
                        ICD_10_Z03_818 = "No";
                    } else {
                        ICD_10_Z03_818 = request.getParameter("Z03.818").trim();
                    }

                    if (request.getParameter("B97.29") == null) {
                        ICD_10_B97_29 = "No";
                    } else {
                        ICD_10_B97_29 = request.getParameter("B97.29").trim();
                    }

                    if (request.getParameter("InternationalTravel") == null) {
                        InternationalTravel = "";
                    } else {
                        InternationalTravel = request.getParameter("InternationalTravel").trim();
                    }


                    if (request.getParameter("closeContact") == null) {
                        closeContact = "";
                    } else {
                        closeContact = request.getParameter("closeContact").trim();
                    }

                    if (request.getParameter("Fever") == null) {
                        Fever = "";
                    } else {
                        Fever = request.getParameter("Fever").trim();
                    }
                    if (request.getParameter("HowLongFever") == null) {
                        HowLongFever = "";
                    } else {
                        HowLongFever = request.getParameter("HowLongFever").trim();
                    }


                    if (request.getParameter("Cough") == null) {
                        Cough = "";
                    } else {
                        Cough = request.getParameter("Cough").trim();
                    }
                    if (request.getParameter("HowLongCough") == null) {
                        HowLongCough = "";
                    } else {
                        HowLongCough = request.getParameter("HowLongCough").trim();
                    }

                    if (request.getParameter("breathShortness") == null) {
                        breathShortness = "";
                    } else {
                        breathShortness = request.getParameter("breathShortness").trim();
                    }

                    if (request.getParameter("HowLongbreathShortness") == null) {
                        HowLongbreathShortness = "";
                    } else {
                        HowLongbreathShortness = request.getParameter("HowLongbreathShortness").trim();
                    }

                    if (request.getParameter("breathingDifficulty") == null) {
                        breathingDifficulty = "";
                    } else {
                        breathingDifficulty = request.getParameter("breathingDifficulty").trim();
                    }

                    if (request.getParameter("HowLongbreathingDifficulty") == null) {
                        HowLongbreathingDifficulty = "";
                    } else {
                        HowLongbreathingDifficulty = request.getParameter("HowLongbreathingDifficulty").trim();
                    }

                    if (request.getParameter("ClinicName") == null) {
                        ClinicName = "";
                    } else {
                        ClinicName = request.getParameter("ClinicName").trim();
                    }

                    if (request.getParameter("PhysicianName") == null) {
                        PhysicianName = "";
                    } else {
                        PhysicianName = request.getParameter("PhysicianName").trim();
                    }

                    if (request.getParameter("NPI") == null) {
                        NPI = "";
                    } else {
                        NPI = request.getParameter("NPI").trim();
                    }

                    if (request.getParameter("AddressClinical") == null) {
                        AddressClinical = "";
                    } else {
                        AddressClinical = request.getParameter("AddressClinical").trim();
                    }

                    if (request.getParameter("CityClinical") == null) {
                        CityClinical = "";
                    } else {
                        CityClinical = request.getParameter("CityClinical").trim();
                    }

                    if (request.getParameter("StateClinical") == null) {
                        StateClinical = "";
                    } else {
                        StateClinical = request.getParameter("StateClinical").trim();
                    }
                    if (request.getParameter("ZipClinical") == null) {
                        ZipClinical = "";
                    } else {
                        ZipClinical = request.getParameter("ZipClinical").trim();
                    }


                    if (request.getParameter("Fax") == null) {
                        Fax = "";
                    } else {
                        Fax = request.getParameter("Fax").trim();
                    }

                    if (request.getParameter("PhClinical") == null) {
                        PhClinical = "";
                    } else {
                        PhClinical = request.getParameter("PhClinical").trim();
                    }

                    if (request.getParameter("EmailClinical") == null) {
                        EmailClinical = "";
                    } else {
                        EmailClinical = request.getParameter("EmailClinical").trim();
                    }

                    if (request.getParameter("SendReportTo") == null) {
                        SendReportTo = "";
                    } else {
                        SendReportTo = request.getParameter("SendReportTo").trim();
                    }

                    if (request.getParameter("PCFR") == null) {
                        PCFR = "";
                    } else {
                        PCFR = request.getParameter("PCFR").trim();
                    }

                    if (request.getParameter("TestType") == null) {
                        TestType = "";
                    } else {
                        TestType = request.getParameter("TestType").trim();
                    }

                    if (request.getParameter("specimenID") == null) {
                        specimenID = "";
                    } else {
                        specimenID = request.getParameter("specimenID").trim();
                    }
                    if (request.getParameter("Ancestry") == null) {
                        ancestry = "";
                    } else {
                        ancestry = request.getParameter("Ancestry").trim();
                        if (ancestry.equals("Other")) {
                            ancestry = request.getParameter("OtherText").trim();
                        }
                    }
                    if (request.getParameter("PolicyHolderDOB") == null) {
                        PolicyHolderDOB = "";
                    } else {
                        PolicyHolderDOB = request.getParameter("PolicyHolderDOB").trim();
                    }
                    if (request.getParameter("RelationshipToPH") == null) {
                        RelationshipToPH = "";
                    } else {
                        RelationshipToPH = request.getParameter("RelationshipToPH").trim();
                        if (RelationshipToPH.equals("relationOther")) {
                            RelationshipToPH = request.getParameter("OtherText2").trim();
                        }

                    }
                    if (request.getParameter("memberID") == null) {
                        memberID = "";
                    } else {
                        memberID = request.getParameter("memberID").trim();
                    }
                    if (request.getParameter("PolicyType") == null) {
                        PolicyType = "";
                    } else {
                        PolicyType = request.getParameter("PolicyType").trim();
                    }
                    Year = request.getParameter("Year").trim();
                    Month = request.getParameter("Month").trim();
                    Day = request.getParameter("Day").trim();

                    DOB = Year + "-" + Month + "-" + Day;
                } catch (Exception ex) {
                    helper.SendEmailWithAttachment("Error in RoverLabMobile ** (SaveData^^" + facilityName + " ##MES#002)", servletContext, ex, "RoverLabMobile", "SaveData", conn);
                    Services.DumException("SaveData^^" + facilityName + "", "PatientReg##MES#002 ", request, ex);
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "RoverLabMobile");
                    Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
                    Parser.SetField("Message", "MES#002");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    return;
                }
                try {
                    Query = "Select Id,dbname from oe.clients where ltrim(rtrim(UPPER(Id))) =  ltrim(rtrim(UPPER('" + ClientIndex + "')))";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        ClientIndex = rset.getInt(1);
                        Database = rset.getString(2);
                    }
                    rset.close();
                    stmt.close();

                    if (MRN == 0)
                        MRN = 310001;
                    Query = "Select MRN from " + Database + ".PatientRegRoverLab order by ID desc limit 1 ";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next())
                        MRN = rset.getInt(1);
                    rset.close();
                    stmt.close();
                    if (String.valueOf(MRN).length() == 0) {
                        MRN = 310001;
                    } else if (String.valueOf(MRN).length() == 4) {
                        MRN = 310001;
                    } else if (String.valueOf(MRN).length() == 8) {
                        MRN = 310001;
                    } else if (String.valueOf(MRN).length() == 6) {
                        MRN++;
                    }
                    if (String.valueOf(ClientIndex).length() == 1) {
                        ExtendedMRN = "100" + ClientIndex + MRN;
                    } else if (String.valueOf(ClientIndex).length() == 2) {
                        ExtendedMRN = "10" + ClientIndex + MRN;
                    } else if (String.valueOf(ClientIndex).length() == 3) {
                        ExtendedMRN = "1" + ClientIndex + MRN;
                    }
                } catch (Exception ex) {
                    helper.SendEmailWithAttachment("Error in RoverLabMobile ** (SaveData^^" + facilityName + " ##MES#003)", servletContext, ex, "RoverLabMobile", "SaveData", conn);
                    Services.DumException("SaveData^^" + facilityName + " ##MES#003", "PatientRegRoverLab ", request, ex);
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "RoverLabMobile");
                    Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
                    Parser.SetField("Message", "MES#003");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    return;
                }
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in RoverLabMobile ** (SaveData^^" + facilityName + " ##MES#004)", servletContext, ex, "RoverLabMobile", "SaveData", conn);
                Services.DumException("SaveData^^" + facilityName + " ##MES#004", "PatientRegRoverLab ", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RoverLabMobile");
                Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
                Parser.SetField("Message", "MES#004");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }

            UtilityHelper utilityHelper = new UtilityHelper();
            String ClientIp = utilityHelper.getClientIp(request);
            int _Age = getAge(LocalDate.parse(DOB));
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".PatientRegRoverLab (ClientIndex,FirstName,LastName ,MiddleInitial,DOB,Age,Gender ,Email,PhNumber ," +
                                "Address,City ,State,Country,ZipCode," +
                                "CreatedDate,Title, MaritalStatus, MRN, Status, DateofService, ExtendedMRN, County, Ethnicity,Race,NoInsurance," +
                                "InsuranceCompany,GroupNumber,PolicyHolderName,TestType,Mid_TurbinateNasalSwab,DateTimeCollected,DateOnSet,Result," +
                                "SpecimenType, DateTimeOfSpecimenCollection, NoInsuranceAgreement, ICD_10_J80, ICD_10_J20_8, ICD_10_J22, ICD_10_J1_89, ICD_10_J98_8, ICD_10_R05, ICD_10_R06_02, ICD_10_R50_9" +
                                ",ICD_10_Z20_828, ICD_10_Z03_818, ICD_10_B97_29, InternationalTravel, closeContact, Fever, HowLongFever, Cough, HowLongCough, breathShortness, HowLongbreathShortness ,breathingDifficulty, HowLongbreathingDifficulty, " +
                                "Providence, SocialSecurity, StateDriverLicense,StateID,ClinicName ,PhysicianName ,NPI ,AddressClinical ,CityClinical ,StateClinical ,ZipClinical ,Fax ,EmailClinical ,PatientConsent ,SendReportTo, PhClinical," +
                                "specimenID ,ancestry ,PolicyHolderDOB ,RelationshipToPH ,memberID ,PolicyType )  " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,'0',now(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,?,?,? ,? ,? ,? ,? ,?) ");
                MainReceipt.setInt(1, ClientIndex);
                MainReceipt.setString(2, FirstName);
                MainReceipt.setString(3, LastName);
                MainReceipt.setString(4, MiddleInitial);
                MainReceipt.setString(5, DOB);
                MainReceipt.setInt(6, _Age);
                MainReceipt.setString(7, gender);
                MainReceipt.setString(8, Email);
                MainReceipt.setString(9, PhNumber);
                MainReceipt.setString(10, Address);
                MainReceipt.setString(11, City);
                MainReceipt.setString(12, State);
                MainReceipt.setString(13, Country);
                MainReceipt.setString(14, ZipCode);
                MainReceipt.setString(15, Title);
                MainReceipt.setString(16, MaritalStatus);
                MainReceipt.setInt(17, MRN);
                MainReceipt.setString(18, ExtendedMRN);
                MainReceipt.setString(19, County);
                MainReceipt.setString(20, Ethnicity);
                MainReceipt.setString(21, Race);
                MainReceipt.setString(22, NoInsurance);
                MainReceipt.setString(23, PriInsuranceName);
                MainReceipt.setString(24, GrpNumber);
                MainReceipt.setString(25, PolicyHolder);
                MainReceipt.setString(26, TestType);


                MainReceipt.setString(27, Mid_turbinate_Nasal_Swab);
                MainReceipt.setString(28, datetimeColected);
                MainReceipt.setString(29, DateOnset);
                MainReceipt.setString(30, COVID_Result);
                MainReceipt.setString(31, specimentType);
                MainReceipt.setString(32, datetimeofspecimen);
                MainReceipt.setString(33, insuranceAgreement);
                MainReceipt.setString(34, ICD_10_J80);
                MainReceipt.setString(35, ICD_10_J20_8);
                MainReceipt.setString(36, ICD_10_J22);
                MainReceipt.setString(37, ICD_10_J1_89);
                MainReceipt.setString(38, ICD_10_J98_8);
                MainReceipt.setString(39, ICD_10_R05);
                MainReceipt.setString(40, ICD_10_R06_02);
                MainReceipt.setString(41, ICD_10_R50_9);
                MainReceipt.setString(42, ICD_10_Z20_828);
                MainReceipt.setString(43, ICD_10_Z03_818);
                MainReceipt.setString(44, ICD_10_B97_29);
                MainReceipt.setString(45, InternationalTravel);
                MainReceipt.setString(46, closeContact);
                MainReceipt.setString(47, Fever);
                MainReceipt.setString(48, HowLongFever);
                MainReceipt.setString(49, Cough);
                MainReceipt.setString(50, HowLongCough);
                MainReceipt.setString(51, breathShortness);
                MainReceipt.setString(52, HowLongbreathShortness);
                MainReceipt.setString(53, breathingDifficulty);
                MainReceipt.setString(54, HowLongbreathingDifficulty);

                MainReceipt.setString(55, Providence);
                MainReceipt.setString(56, PSSNtext);
                MainReceipt.setString(57, SSLtext);
                MainReceipt.setString(58, SIDtext);

                MainReceipt.setString(59, ClinicName);
                MainReceipt.setString(60, PhysicianName);
                MainReceipt.setString(61, NPI);
                MainReceipt.setString(62, AddressClinical);
                MainReceipt.setString(63, CityClinical);
                MainReceipt.setString(64, StateClinical);
                MainReceipt.setString(65, ZipClinical);
                MainReceipt.setString(66, Fax);
                MainReceipt.setString(67, EmailClinical);
                MainReceipt.setString(68, PCFR);
                MainReceipt.setString(69, SendReportTo);
                MainReceipt.setString(70, PhClinical);
                MainReceipt.setString(71, specimenID);
                MainReceipt.setString(72, ancestry);
                MainReceipt.setString(73, PolicyHolderDOB);
                MainReceipt.setString(74, RelationshipToPH);
                MainReceipt.setString(75, memberID);
                MainReceipt.setString(76, PolicyType);


                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception ex) {
                helper.SendEmailWithAttachment("Error in RoverLabMobile ** (SaveData Insertion PatientRegRoverLab^^" + facilityName + " ##MES#005)", servletContext, ex, "RoverLabMobile", "SaveData", conn);
                Services.DumException("SaveData^^" + facilityName + " ##MES#005", "PatientRegRoverLab ", request, ex);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RoverLabMobile");
                Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientIndex + "");
                Parser.SetField("Message", "MES#005");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }

            Query = "Select CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName),id from " + Database + ".PatientRegRoverLab " +
                    "where MRN = " + MRN;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientName = rset.getString(1);
                PatientRegId = rset.getInt(2);
            }
            rset.close();
            stmt.close();

            String Date = "";
            Query = "Select Date_format(now(),'%m/%d/%Y %T')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                Date = rset.getString(1);
            rset.close();
            stmt.close();


            String temp = SaveBundle(request, out, this.conn, Database, DirectoryName, PatientRegId);
            String[] arr = temp.split("~");
            String FileName = arr[2];
            String outputFilePath = arr[1];
            String pageCount = arr[0];

          /*  Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Thank You " + PatientName + " We Have Registered You Successfully. Please Wait for Further Processing. " + Date);
            Parser.SetField("MRN", "MRN: " + MRN);
            Parser.SetField("FormName", "RoverLabMobile");
            Parser.SetField("ActionID", "GETINPUTRoverLab&ID=" + PatientRegId + "&userId=" + UserId);
            Parser.SetField("pageCount", String.valueOf(pageCount));
            Parser.SetField("FileName", String.valueOf(FileName));
            Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
            Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("userId", UserId);
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/MessageRoverLabMobile.html");
*/
            ///md/md.PatientRegRoverLab?ActionID=SignPdf&pageCount=1&outputFilePath=/sftpdrive/AdmissionBundlePdf/roverlab/test_100_20210816152629.pdf&FileName=test_100_20210816152629.pdf&PatientRegId=100
/*            String url = "https://app.rovermd.com:8443/md/md.RoverLabMobile?RequestName=SignPdf&pageCount=" + pageCount + "&outputFilePath=" + outputFilePath + "&FileName=" + FileName + "&PatientRegId=" + PatientRegId;
////            out.println("URL --> " + url);
            String myResponse = sendPOST(url, "");
            out.println(myResponse);*/

            SignPdf(request, out, conn, servletContext, UserId, Database, ClientIndex, DirectoryName, helper, pageCount, outputFilePath, FileName, PatientRegId);

        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RoverLabMobile ** (SaveData Main Catch^^" + facilityName + " ##MES#006)", servletContext, ex, "RoverLabMobile", "SaveData", conn);
            Services.DumException("SaveData^^" + facilityName + " ##MES#006", "RoverLabMobile ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RoverLabMobile");
            Parser.SetField("ActionID", "GetValues&ClientIndex=\"+ClientIndex+\"");
            Parser.SetField("Message", "MES#006");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void SignPdf(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, String DirectoryName, UtilityHelper helper, String pageCount, String outputFilePath, String FileName, int PatientRegId) {
        try {

            int MRN = 0;
            String PatientName = "";
            String AUTHID = "";
            String SendType = "1";
            InetAddress ip = InetAddress.getLocalHost();
            long unixTime = System.currentTimeMillis() / 1000L;
            UUID uuid = UUID.randomUUID();
/*            String pageCount = request.getParameter("pageCount");
            String outputFilePath = request.getParameter("outputFilePath");
            String FileName = request.getParameter("FileName");
            int PatientRegId = Integer.parseInt(request.getParameter("PatientRegId"));*/

            int found = 0;
            int isExist = 0;
/*            Query = "Select Count(*) from ER_Dallas.SignRequest where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                found = rset.getInt(1);
            }
            rset.close();
            stmt.close();*/

            //MRN = helper.getPatientRegMRN(request, conn, servletContext, "roverlab", PatientRegId);

            try {
                Query = "SELECT MRN FROM roverlab.PatientRegRoverLab WHERE Id = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    MRN = rset.getInt(1);
                rset.close();
                stmt.close();
            } catch (Exception Ex) {
                Services.DumException("Handheld - Payments", "PatientRegIdx", request, Ex, getServletContext());
            }
//            found = helper.signPDFCheck(request, conn, servletContext, "ER_Dallas", PatientRegId);
            try {
                Query = "Select Count(*) from roverlab.SignRequest where PatientRegId = " + PatientRegId + " AND isSign = 1";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    found = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception Ex) {

                Services.DumException("Handheld ", "signPDFCheck", request, Ex, getServletContext());
            }

//            isExist = helper.signPDFCheckMobile(request, conn, servletContext, "ER_Dallas", PatientRegId);

            try {
                Query = "Select Count(*) from roverlab.SignRequest where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    isExist = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception Ex) {
//                SendEmailWithAttachment("Error in signPDFCheck ", servletContext, Ex, "Handheld - UtilityHelper", "signPDFCheck", conn);
                Services.DumException("Handheld ", "signPDFCheck", request, Ex, getServletContext());
            }
            if (found > 0) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "This Bundle is already been signed!");
                Parser.SetField("FormName", "DownloadBundle");
                Parser.SetField("ActionID", "GETINPUTERDallas&ID=" + PatientRegId);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                return;
            }

            if (isExist > 0) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Bundle has already been sent for Signing");
                Parser.SetField("FormName", "DownloadBundle");
                Parser.SetField("ActionID", "GETINPUTERDallas&ID=" + PatientRegId);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                return;
            }

//            int requestCheck = helper.requestMobileCheck(request, conn, servletContext, Database, PatientRegId, MRN, ClientId);
//            if (requestCheck > 0) {
//                Parsehtm Parser = new Parsehtm(request);
//                Parser.SetField("Message", "This Bundle is already been sent to Mobile for Signing. Please sign from Mobile!");
//                Parser.SetField("FormName", "DownloadBundle");
//                Parser.SetField("ActionID", "GETINPUTERDallas&ID=" + PatientRegId);
//                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
//                return;
//            }

            StringBuffer Style = new StringBuffer();
            StringBuffer ulTag = new StringBuffer();
            PDFtoImages pdftoImage = new PDFtoImages();
            new HashMap();
            HashMap<Integer, String> images_Map_final = pdftoImage.GetValues(request, out, conn, Database, ClientId, outputFilePath, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/md/tmpImages/");
            Collection<String> values = images_Map_final.values();
            java.util.List<String> imagelist = new ArrayList(values);

            for (int i = 0; i < imagelist.size(); ++i) {
                Style.append(".desktop-image" + (i + 1) + " {\n\tbackground-image: url(" + (String) imagelist.get(i) + ");\n\tbackground-size: cover;\n\tbackground-position: center;\n\twidth: 600px;\n\theight: 840px;\n\tmargin: 0 auto;\n\tborder: 5px solid #0f0f10;\n\tposition: relative;\n}\n\n.desktop-image" + (i + 1) + "> #Sign" + (i + 1) + "{\n\t\n\tposition: relative;\n\ttop: 70%;\n\tleft: 50%;\n\t/*transform: translate(50%, -50%);*/\n}.desktop-image" + (i + 1) + ">#Sign" + (i + 2) + "{\n\t\n\tposition: relative;\n\ttop: 76.4%;\n\tleft: 31.5%;\n\t/*transform: translate(50%, -50%);*/\n}");
                ulTag.append("<div id=\"page" + (i + 1) + "\" class=\"images\"><div  class=\"desktop-image" + (i + 1) + "\"><button type=\"button\" class=\"btn btn-primary\" id=\"Sign" + (i + 1) + "\"  onclick=\"signhere(" + (i + 1) + ", this.id);\" class=\"mobile-image-stella\">Patients Sign</button>\n<img id=\"canvasimg" + (i + 1) + "\" style=\"display:none;background-color:rgba(255,255,255,0.6);\">\n<button type=\"button\" class=\"btn btn-primary\" id=\"Sign" + (i + 2) + "\"  onclick=\"signhere(" + (i + 2) + ", this.id);\" class=\"mobile-image-stella\">HealthCare Provider's Sign</button>\n<img id=\"canvasimg" + (i + 2) + "\" style=\"display:none;background-color:rgba(255,255,255,0.6);\">\n</div>\n</div>\n");
            }

//          Insert Data in the SignRequest Table here.
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO roverlab.SignRequest (MRN,Status,isSign,IP,CreatedBy,CreatedDate," +
                                " SendType,SignBy,UID, AUTHID, PatientRegId) VALUES (?,?,?,?,?,now(),?,?,?,?,?) ");
                MainReceipt.setInt(1, MRN);
                MainReceipt.setInt(2, 0);
                MainReceipt.setInt(3, 0);
                MainReceipt.setString(4, ip.toString());
                MainReceipt.setString(5, UserId);
                MainReceipt.setString(6, SendType);
                MainReceipt.setString(7, "");
                MainReceipt.setString(8, uuid.toString());
                MainReceipt.setString(9, "");
                MainReceipt.setInt(10, PatientRegId);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in Sign Print ^^ ** (SignPDF)", servletContext, e, "SignPrint", "SignPDF", conn);
                Services.DumException("SignPDF", "SignPDF", request, e, getServletContext());
                return;
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
            Parser.SetField("imagelist", String.valueOf(imagelist));
            Parser.SetField("ulTag", String.valueOf(ulTag));
            Parser.SetField("Style", String.valueOf(Style));
            Parser.SetField("pageCount", String.valueOf(pageCount));
            Parser.SetField("FileName", String.valueOf(FileName));
            Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
            Parser.SetField("UID", String.valueOf(uuid));
            Parser.SetField("MRN", String.valueOf(MRN));
            Parser.SetField("userId", UserId);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/SigningBundleRoverLabMobile.html");

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Sign Print ^^ ** (SignPDF)", servletContext, e, "SignPrint", "SignPDF", conn);
            Services.DumException("SignPDF", "SignPDF", request, e, getServletContext());
        }


    }

    void GetData(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        String Message = "";
        String imagedataURL = "";
        String PatientRegId = "";
        boolean FileFound = false;
        byte[] Data = null;
        String key = "";
        String UID = "";
        String MRN = "";
        String pageCount = "";
        String Order = "";
        String outputFilePath = "";
        try {
            Dictionary d = doUpload(request, response, out);
            Enumeration en = d.keys();
            while (en.hasMoreElements()) {
                key = (String) en.nextElement();
                if (key.startsWith("PatientRegId")) {
                    PatientRegId = (String) d.get(key);
                } else if (key.startsWith("imagedataURLbtnIdHdn")) {
                    imagedataURL = (String) d.get(key);
                } else if (key.startsWith("UID")) {
                    UID = (String) d.get(key);
                } else if (key.startsWith("MRN")) {
                    MRN = (String) d.get(key);
                } else if (key.startsWith("pageCount")) {
                    pageCount = (String) d.get(key);
                } else if (key.startsWith("Order")) {
                    Order = (String) d.get(key);
                } else if (key.startsWith("outputFilePath")) {
                    outputFilePath = (String) d.get(key);
                }
            }
            PatientRegId = PatientRegId.substring(4);
            imagedataURL = imagedataURL.substring(4);
            UID = UID.substring(4);
            MRN = MRN.substring(4);
            pageCount = pageCount.substring(4);
            Order = Order.substring(4);
            outputFilePath = outputFilePath.substring(4);

            String[] imageURL = imagedataURL.split("\\~");
            String[] Ordering = Order.split("\\~");

            BufferedImage image = null;
            byte[] imageByte;
            for (int i = 0; i < imageURL.length; i++) {
//                out.println(imageURL[i]);
                try {
                    byte[] imagedata = DatatypeConverter.parseBase64Binary(imageURL[i].substring(imageURL[i].indexOf(",") + 1));
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagedata));
                    //ImageIO.write(bufferedImage, "png", new File("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_" + i + "_" + PatientRegId + ".png"));
                    ImageIO.write(bufferedImage, "png", new File("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_" + i + "_" + PatientRegId + "_" + Ordering[i] + ".png"));

//                    out.println("Result => "+ isValid(new File("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_" + i + "_" + PatientRegId + "_"+Ordering[i]+".png")));// isValid(in);
//                    out.print("HEre ");
                    //String ImageTransparent = this.MakeTransparent(out, "/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_" + i + "_" + PatientRegId + ".png", "/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_" + i + "_" + PatientRegId + ".png");
                    if (isValid(new File("/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_" + i + "_" + PatientRegId + "_" + Ordering[i] + ".png"))) {
                        String ImageTransparent = this.MakeTransparent(out, "/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_" + i + "_" + PatientRegId + "_" + Ordering[i] + ".png", "/sftpdrive/AdmissionBundlePdf/SignImg/roverlab/img_" + i + "_" + PatientRegId + "_" + Ordering[i] + ".png");
                        if (ImageTransparent.trim().toUpperCase().equals("CONVERTED")) {
                            Message = " and Transparency DONE";
                        } else {
                            Message = " and Image Created";
                        }
                    } else {
                        Parsehtm Parser = new Parsehtm(request);
                        Parser.SetField("Message", "Invalid Signature, Please Try Again!");
                        Parser.SetField("MRN", "Invalid Signature");
//                        Parser.SetField("FormName", "PatientReg");
//                        Parser.SetField("ActionID", "GetValues&ClientIndex=36");
                        Parser.SetField("pageCount", String.valueOf(pageCount));
                        Parser.SetField("FileName", String.valueOf(outputFilePath));
                        Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
                        Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
                        Parser.SetField("ClientIndex", "36");
                        Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/MessageRoverLab.html");
                        return;
                    }
//                    out.print("\n After ");


                } catch (IOException e) {
                    out.println("Error in IO" + e.getStackTrace());
                }
            }

            Query = "UPDATE roverlab.SignRequest SET isSign = 1 , SignBy = '" + UserId + "', SignTime = NOW() " +
                    "WHERE PatientRegId = " + PatientRegId + " AND " +
                    "MRN = " + MRN + " AND UID = '" + UID + "' ";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();


/*
            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("Message", "Done! Signed PDF is Ready " + Message);
            Parser.SetField("Message", "Form has been updated successfully!");
            Parser.SetField("FormName", "RoverLabMobile");
            Parser.SetField("ActionID", "GetValues&userId=m.lab");
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/MessageLabMobile.html");
*/
            String url = "https://app.rovermd.com:8443/md/md.RoverLabMobile?RequestName=GetValues&userId=m.lab";
            String myResponse = sendPOST(url, "");
            out.println(myResponse);
//            response.sendRedirect("https://app.rovermd.com:8443/md/md.RoverLabMobile?RequestName=GetValues&userId=m.lab");
        } catch (Exception var11) {
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
    }

    String MakeTransparent(PrintWriter out, String inputFileName, String outputFileName) {

        try {
//            String inputFileName = "C:\\Users\\abid_\\Desktop\\download.png";
//            int decimalPosition = inputFileName.lastIndexOf(".");
//            String outputFileName = inputFileName+ ".png";

            //out.println("Copying file " + inputFileName + " to " + outputFileName);

            File in = new File(inputFileName);


            BufferedImage source = ImageIO.read(in);

            int color = source.getRGB(0, 0);
            Image imageWithTransparency = makeColorTransparent(source, new Color(color));

            BufferedImage transparentImage = imageToBufferedImage(imageWithTransparency);

            File file = new File(outputFileName);
            ImageIO.write(transparentImage, "PNG", file);

        } catch (Exception e) {
            out.println(e.getMessage());
        }
        return "CONVERTED";
    }

    private Dictionary doUpload(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out) throws Exception {
        try {
            String boundary = request.getHeader("Content-Type");
            int pos = boundary.indexOf(61);
            boundary = boundary.substring(pos + 1);
            boundary = "--" + boundary;
            final byte[] bytes = new byte[512];
            int state = 0;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            String name = null;
            String value = null;
            String filename = null;
            String contentType = null;
            final Dictionary fields = new Hashtable();
            final ServletInputStream in = request.getInputStream();
            for (int i = in.readLine(bytes, 0, 512); -1 != i; i = in.readLine(bytes, 0, 512)) {
                final String st = new String(bytes, 0, i);
                if (st.startsWith(boundary)) {
                    state = 0;
                    if (name != null) {
                        if (value != null) {
                            fields.put(name, value.substring(0, value.length() - 2));
                        } else if (buffer.size() > 2) {
                            fields.put(filename, buffer);
                        }
                        name = null;
                        value = null;
                        filename = null;
                        contentType = null;
                        buffer = new ByteArrayOutputStream();
                    }
                } else if (st.startsWith("Content-Disposition: form-data") && state == 0) {
                    final StringTokenizer tokenizer = new StringTokenizer(st, ";=\"");
                    while (tokenizer.hasMoreTokens()) {
                        final String token = tokenizer.nextToken();
                        if (token.startsWith(" name")) {
                            name = tokenizer.nextToken();
                            state = 2;
                        } else {
                            if (token.startsWith(" filename")) {
                                filename = tokenizer.nextToken();
                                final StringTokenizer ftokenizer = new StringTokenizer(filename, "\\/:");
                                filename = ftokenizer.nextToken();
                                while (ftokenizer.hasMoreTokens()) {
                                    filename = ftokenizer.nextToken();
                                }
                                state = 1;
                                break;
                            }
                            continue;
                        }
                    }
                } else if (st.startsWith("Content-Type") && state == 1) {
                    pos = st.indexOf(":");
                    st.substring(pos + 2, st.length() - 2);
                } else if (st.equals("\r\n") && state == 1) {
                    state = 3;
                } else if (st.equals("\r\n") && state == 2) {
                    state = 4;
                } else if (state == 4) {
                    value = String.valueOf(String.valueOf(value)) + st;
                } else if (state == 3) {
                    buffer.write(bytes, 0, i);
                }
            }
            return fields;
        } catch (Exception var20) {
            throw new Exception("Error In Do Upload " + var20.getMessage());
        }
    }

    String SaveBundle(final HttpServletRequest request, final PrintWriter out, final Connection conn, final String Database, final String DirectoryName, int IDpatient) {
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
        String Address2 = "";
        String StreetAddress2 = "";
        String City = "";
        String State = "";
        String Country = "";
        String County = "";
        String ZipCode = "";
        String Ethnicity = "";
        String Race = "";
        String SSN = "";
        String SpCarePhy = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        int TravellingChk = 0;
        String TravelWhen = "";
        String TravelWhere = "";
        String TravelHowLong = "";
        int COVIDExposedChk = 0;
        int COVIDPositveChk = 0;
        String CovidPositiveDate = "";
        String CovidExpWhen = "";
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
        String SympHeadache = "0";
        String SympLossTaste = "0";
        String SympShortBreath = "0";
        String SympCongestion = "0";
        String AddInfoTextArea = "";
        String GuarantorName = "";
        String GuarantorDOB = "";
        String GuarantorNumber = "";
        String GuarantorSSN = "";
        String SympEyeConjunctivitis = "0";
        String PriCarePhy = "";
        String ReasonVisit = "";
        int WorkersCompPolicy = 0;
        int MotorVehAccident = 0;
        String PriInsurance = "";
        String MemId = "";
        String GrpNumber = "";
        String PriInsuranceName = "";
        String OtherInsuranceName = "";


        String PolicyHolder = "";
        String NoInsurance = "";
        String testtype = "";
        String Mid_turbinate_Nasal_Swab = "";
        String datetimeColected = "";
        String DateOnset = "";
        String Result = "";
        String specimentType = "";
        String specimenID = "";
        String ancestry = "";
        String datetimeofspecimen = "";
        String insuranceAgreement = "";
        String PolicyHolderDOB = "";
        String RelationshipToPH = "";
        String memberID = "";
        String PolicyType = "";

        String Providence = "";
        String PSSNtext = "";
        String SSLtext = "";
        String SIDtext = "";


        String ICD_10_J80 = "";
        String ICD_10_J20_8 = "";
        String ICD_10_J22 = "";
        String ICD_10_J1_89 = "";
        String ICD_10_J98_8 = "";
        String ICD_10_R05 = "";
        String ICD_10_R06_02 = "";
        String ICD_10_R50_9 = "";
        String ICD_10_Z20_828 = "";
        String ICD_10_Z03_818 = "";
        String ICD_10_B97_29 = "";
        String InternationalTravel = "";
        String closeContact = "";
        String Fever = "";
        String HowLongFever = "";
        String Cough = "";
        String HowLongCough = "";
        String breathShortness = "";
        String HowLongbreathShortness = "";
        String breathingDifficulty = "";
        String HowLongbreathingDifficulty = "";


        String ClinicName = "";
        String PhysicianName = "";
        String NPI = "";
        String AddressClinical = "";
        String CityClinical = "";
        String StateClinical = "";
        String ZipClinical = "";
        String Fax = "";
        String PhClinical = "";
        String EmailClinical = "";

        String SendReportTo = "";
        String PCFR = "";
        final int ID = IDpatient;//Integer.parseInt(request.getParameter("ID").trim());
//        out.println("ID -> "+ID);
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
                Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), " +
                        " IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'), " +
                        " IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(City,'-'), IFNULL(State,'-'), IFNULL(ZipCode,'-')," +
                        " IFNULL(PhNumber,'-'),IFNULL(Email,'-'),  " +
                        " IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), IFNULL(Race,'-')," +
                        " IFNULL(Ethnicity,'-'), IFNULL(ClinicName,'-'), IFNULL(PhysicianName,'-'), IFNULL(NPI,'-'), IFNULL(IFNULL(AddressClinical,'-'),'-')," +
                        " IFNULL(CityClinical,'-') , IFNULL(StateClinical,'-'), IFNULL(ZipClinical,'-'), IFNULL(Fax,'-'), IFNULL(EmailClinical,'-')," +
                        " IFNULL(PhClinical,'-'), IFNULL(SpecimenType,'-'), DATE_FORMAT(DateTimeOfSpecimenCollection,'%m/%d/%Y %T')," +
                        " IFNULL(InsuranceCompany,'-'),IFNULL(GroupNumber,'-'), IFNULL(PolicyHolderName,'-')," +
                        " IFNULL(NoInsuranceAgreement,'-'), IFNULL(Providence,'-'), IFNULL(SocialSecurity,'-'), IFNULL(StateDriverLicense,'-'), IFNULL(StateID,'-')," +
                        " ICD_10_J80 ,ICD_10_J20_8 ,ICD_10_J22 ,ICD_10_J1_89 ,ICD_10_J98_8 ,ICD_10_R05 ,ICD_10_R06_02 ,ICD_10_R50_9 ,ICD_10_Z20_828 ,ICD_10_Z03_818 ,ICD_10_B97_29," +
                        " InternationalTravel ,closeContact ,Fever ,HowLongFever ,Cough ,HowLongCough ,breathShortness ,HowLongbreathShortness ,breathingDifficulty ,HowLongbreathingDifficulty," +
                        "  IFNULL(DATE_FORMAT(DateTimeCollected,'%m/%d/%Y %T'), '-') ,  IFNULL(DATE_FORMAT(DateOnSet,'%m/%d/%Y'), '-'), IFNULL(TestType,'-'), IFNULL(Result,'-')," +
                        " specimenID ,ancestry ,PolicyHolderDOB ,RelationshipToPH ,memberID ,PolicyType, SendReportTo" +
                        "  From " + Database + ".PatientRegRoverLab Where ID = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    LastName = rset.getString(1);
                    FirstName = rset.getString(2);
                    MiddleInitial = rset.getString(3);
                    DOB = rset.getString(4);
                    gender = rset.getString(5);
                    Address = rset.getString(6);
                    City = rset.getString(7);
                    State = rset.getString(8);
                    ZipCode = rset.getString(9);
                    PhNumber = rset.getString(10);
                    Email = rset.getString(11);
                    Race = rset.getString(13);
                    Ethnicity = rset.getString(14);
                    ClinicName = rset.getString(15);
                    PhysicianName = rset.getString(16);
                    NPI = rset.getString(17);
                    AddressClinical = rset.getString(18);
                    CityClinical = rset.getString(19);
                    StateClinical = rset.getString(20);
                    ZipClinical = rset.getString(21);
                    Fax = rset.getString(22);
                    EmailClinical = rset.getString(23);
                    PhClinical = rset.getString(24);
                    specimentType = rset.getString(25);
                    datetimeofspecimen = rset.getString(26);
                    PriInsuranceName = rset.getString(27);
//                    out.println("Insurance => "+PriInsuranceName);
                    GrpNumber = rset.getString(28);
                    PolicyHolder = rset.getString(29);

                    NoInsurance = rset.getString(30);
                    Providence = rset.getString(31);
                    PSSNtext = rset.getString(32);
                    SSLtext = rset.getString(33);
                    SIDtext = rset.getString(34);

                    ICD_10_J80 = rset.getString(35);
                    ICD_10_J20_8 = rset.getString(36);
                    ICD_10_J22 = rset.getString(37);
                    ICD_10_J1_89 = rset.getString(38);
                    ICD_10_J98_8 = rset.getString(39);
                    ICD_10_R05 = rset.getString(40);
                    ICD_10_R06_02 = rset.getString(41);
                    ICD_10_R50_9 = rset.getString(42);
                    ICD_10_Z20_828 = rset.getString(43);
                    ICD_10_Z03_818 = rset.getString(44);
                    ICD_10_B97_29 = rset.getString(45);


                    InternationalTravel = rset.getString(46);
                    closeContact = rset.getString(47);
                    Fever = rset.getString(48);
                    HowLongFever = rset.getString(49);
                    Cough = rset.getString(50);
                    HowLongCough = rset.getString(51);
                    breathShortness = rset.getString(52);
                    HowLongbreathShortness = rset.getString(53);
                    breathingDifficulty = rset.getString(54);
                    HowLongbreathingDifficulty = rset.getString(55);

                    datetimeColected = rset.getString(56);
                    DateOnset = rset.getString(57);
                    testtype = rset.getString(58);
                    Result = rset.getString(59);

                    specimenID = rset.getString(60);
                    ancestry = rset.getString(61);
                    PolicyHolderDOB = rset.getString(62);
                    RelationshipToPH = rset.getString(63);
                    memberID = rset.getString(64);
                    PolicyType = rset.getString(65);
                    SendReportTo = rset.getString(66);
                }
                rset.close();
                stmt.close();

                if (!PriInsuranceName.equals("")) {
                    try {
                        Query = "Select PayerName from oe_2.ProfessionalPayers where Id = " + PriInsuranceName + "";
                        out.print("Query => " + Query);
//                    out.print("PriInsuranceName => " + PriInsuranceName );
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next()) {
                            PriInsuranceName = rset.getString(1);
                        }
                        rset.close();
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                out.println("Error In PateintRegROVERLAB SAVEBUNDLE:--" + e.getMessage());
//                out.println(Query);
            }
//            if (SelfPayChk == 1) {

            String inputFilePath = null;
            try {
                inputFilePath = "";
                final InetAddress ip = InetAddress.getLocalHost();
                final String hostname = ip.getHostName();
                if (hostname.trim().equals("rover-01")) {
                    inputFilePath = "";
                } else {
                    inputFilePath = "/sftpdrive";
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            inputFilePath += "/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/PrimeScopeReg.pdf";
            FirstNameNoSpaces = FirstName.replaceAll("\\s+", "");
            String filename = FirstNameNoSpaces + "_" + ID + "_" + DateTime + ".pdf";
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
//            out.println("inputFilePath -> "+inputFilePath);
//            out.println("outputFilePath -> "+outputFilePath);


//            out.println("Before pdf");
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
//                out.println("Inside For loop pdf");
                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
//                    out.println("Inside IF  pdf");

                    /////////////////////////////////////Patient Information//////////////////////////////////////////////////
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45, 752); // set x and y co-ordinates
                    pdfContentByte.showText(FirstName);//"First Name"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(165, 752); // set x and y co-ordinates
                    pdfContentByte.showText(LastName);//"Last Name"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260, 752); // set x and y co-ordinates
                    pdfContentByte.showText(MiddleInitial);//"Middle"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340, 752); // set x and y co-ordinates
                    pdfContentByte.showText(ClinicName);//"PrimeScope Diagnostics, LLC"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(53, 739); // set x and y co-ordinates
                    pdfContentByte.showText(Address);//"Address"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(355, 739); // set x and y co-ordinates
                    pdfContentByte.showText(PhysicianName);//"SHIRAZ PIRALI, M.D."); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500, 739); // set x and y co-ordinates
                    pdfContentByte.showText(NPI);//"1366464034"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45, 729); // set x and y co-ordinates
                    pdfContentByte.showText(City);//"City"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(160, 729); // set x and y co-ordinates
                    pdfContentByte.showText(State);//"State"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(250, 729); // set x and y co-ordinates
                    pdfContentByte.showText(ZipCode);//"Zip"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350, 729); // set x and y co-ordinates
                    pdfContentByte.showText(AddressClinical);//"11822 Westheimer Rd"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45, 719); // set x and y co-ordinates
                    pdfContentByte.showText(PhNumber);//"Phone"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(155, 719); // set x and y co-ordinates
                    pdfContentByte.showText(DOB);//"DOB"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(260, 719); // set x and y co-ordinates
                    pdfContentByte.showText(gender);//"Gender"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340, 719); // set x and y co-ordinates
                    pdfContentByte.showText(CityClinical);//"HOUSTON"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(440, 719); // set x and y co-ordinates
                    pdfContentByte.showText(StateClinical);//"TX"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(520, 719); // set x and y co-ordinates
                    pdfContentByte.showText(ZipClinical);//"77077"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(55, 708); // set x and y co-ordinates
                    pdfContentByte.showText(ancestry);//"Ancestry"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340, 708); // set x and y co-ordinates
                    pdfContentByte.showText(PhClinical);//"(469)638-5512"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500, 708); // set x and y co-ordinates
                    pdfContentByte.showText(Fax);//"Fax"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(45, 697); // set x and y co-ordinates
                    pdfContentByte.showText(Email);//"Email"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(340, 697); // set x and y co-ordinates
                    pdfContentByte.showText(EmailClinical);//"DALLAS@HEAL360.COM"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(75, 671); // set x and y co-ordinates
                    pdfContentByte.showText(specimenID);//"Specimen ID"); // add the text
                    pdfContentByte.endText();
//                    out.println("Before datetimeofspecimen pdf \n");
//                    out.println("datetimeofspecimen " + datetimeofspecimen);

                    if (datetimeofspecimen != null) {
                        String[] temp = datetimeofspecimen.split(" ");
//                        out.println("After datetimeofspecimen pdf");
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(186, 671); // set x and y co-ordinates
                        pdfContentByte.showText(temp[0]);//"Date"); // add the text
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(266, 671); // set x and y co-ordinates
                        pdfContentByte.showText(temp[1]);//"Time"); // add the text
                        pdfContentByte.endText();
                    }
//                    out.println("Before Insurance pdf");
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(320, 660); // set x and y co-ordinates
                    pdfContentByte.showText(PriInsuranceName);//"Insurance"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(80, 660); // set x and y co-ordinates
                    pdfContentByte.showText(specimentType);//"Specimen Type"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(385, 645); // set x and y co-ordinates
                    pdfContentByte.showText(PolicyHolder);//"Policy Holder Name"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(360, 632); // set x and y co-ordinates
                    pdfContentByte.showText(RelationshipToPH);//"Relationship"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(475, 632); // set x and y co-ordinates
                    pdfContentByte.showText(PolicyHolderDOB);//"DOB"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(350, 616); // set x and y co-ordinates
                    pdfContentByte.showText(GrpNumber);//"Group#"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(487, 616); // set x and y co-ordinates
                    pdfContentByte.showText(memberID);//"Member#"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(358, 602); // set x and y co-ordinates
                    pdfContentByte.showText(PolicyType);//"Policy type"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(150, 500); //set x and y co-ordinates
                    pdfContentByte.showText(NoInsurance);//"Y/N"); //  NOInsurance  add the text
                    pdfContentByte.endText();


                    switch (Providence) {
                        case "Patient Social Security":
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name

                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(395, 553); // set x and y co-ordinates

                            pdfContentByte.showText(PSSNtext);//"PSS"); //  PSS add the text

                            pdfContentByte.endText();
                            break;
                        case "State Driver License":
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name

                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(390, 537); // set x and y co-ordinates

                            pdfContentByte.showText(SSLtext);//"SDL"); //  SDL add the text

                            pdfContentByte.endText();
                            break;
                        case "State ID":
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(340, 521); // set x and y co-ordinates

                            pdfContentByte.showText(SIDtext);//"SID"); //  SID add the text

                            pdfContentByte.endText();
                            break;
                    }


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(205, 443); // set x and y co-ordinates
                    pdfContentByte.showText(InternationalTravel);//"Y/N"); // international travel add the text for agreement
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430, 463); // set x and y co-ordinates
                    pdfContentByte.showText(closeContact);//"Y/N"); // closeContact travel add the text for agreement
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(220, 431); // set x and y co-ordinates
                    pdfContentByte.showText(Fever);//"Y/N"); // Fever add the text for agreement
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(65, 421); // set x and y co-ordinates
                    pdfContentByte.showText(HowLongFever);//"How Long"); // Fever add the text for agreement
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(335, 431); // set x and y co-ordinates
                    pdfContentByte.showText(Cough);//"Y/N"); // cough add the text for agreement
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(347, 421); // set x and y co-ordinates
                    pdfContentByte.showText(HowLongCough);//"How Long"); // cough add the text for agreement
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(465, 431); // set x and y co-ordinates
                    pdfContentByte.showText(breathShortness);//"Y/N"); // Shortness add the text for agreement
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(434, 421); // set x and y co-ordinates
                    pdfContentByte.showText(HowLongbreathShortness);//"How Long"); // Shortness add the text for agreement
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(570, 431); // set x and y co-ordinates
                    pdfContentByte.showText(breathingDifficulty);//"Y/N"); // Difficulty add the text for agreement
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(538, 421); // set x and y co-ordinates
                    pdfContentByte.showText(HowLongbreathingDifficulty);//"How Long"); // Difficulty add the text for agreement
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(55, 225); // set x and y co-ordinates
                    pdfContentByte.showText(LastName + ", " + FirstName + " " + MiddleInitial);// LastName, FirstName Middle"); // add the text
                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(320, 225); // set x and y co-ordinates
//                    pdfContentByte.showText("Sign Image"); // add the text
//                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(510, 225); // set x and y co-ordinates
                    pdfContentByte.showText(Date);//"DATE"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(105, 145); // set x and y co-ordinates
                    pdfContentByte.showText(SendReportTo);//"Send Report to"); // add the text
                    pdfContentByte.endText();
//                    pdfContentByte.beginText();
//                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                    pdfContentByte.setColorFill(BaseColor.BLACK);
//                    pdfContentByte.setTextMatrix(200, 125); // set x and y co-ordinates
//                    pdfContentByte.showText("Healthcare Provider Sign Image"); // add the text
//                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(450, 125); // set x and y co-ordinates
                    pdfContentByte.showText(Date);//"DATE"); // add the text
                    pdfContentByte.endText();


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(15, 85); // set x and y co-ordinates
                    pdfContentByte.showText(testtype);//"Test Type"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(190, 85); // set x and y co-ordinates
                    pdfContentByte.showText(datetimeColected);//"Date & time collected"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(320, 85); // set x and y co-ordinates
                    pdfContentByte.showText(DateOnset);//"Date of Onset"); // add the text
                    pdfContentByte.endText();
                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(430, 85); // set x and y co-ordinates
                    pdfContentByte.showText(Result);//"Result"); // add the text
                    pdfContentByte.endText();

                }

            }
//            out.println("Printing Done");
            pdfStamper.close();
//                final File pdfFile = new File(outputFilePath);
//                response.setContentType("application/pdf");
//                response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
//                response.setContentLength((int) pdfFile.length());
//                final FileInputStream fileInputStream = new FileInputStream(pdfFile);
//                final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
//                int bytes;
//                while ((bytes = fileInputStream.read()) != -1) {
//                    responseOutputStream.write(bytes);
//                }

//            out.print("Mouhid Here\n");
            return pdfReader.getNumberOfPages() + "~" + outputFilePath + "~" + filename;
        } catch (Exception e) {
//            out.println(e.getMessage());
            System.out.println("ERROR " + e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            System.out.println(str);
        }
//        out.print("NOT Mouhid Here\n");
        return "";
    }

    private String sendPOST(String POST_URL, String POST_PARAMS) throws IOException {
        URL obj = new URL(POST_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);

        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            //System.out.println(response.toString());
            return response.toString();
        } else {
            //System.out.println("POST request not worked");
            return "POST request not worked";
        }
    }
}
