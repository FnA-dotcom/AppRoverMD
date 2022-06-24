package Handheld;

import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import md.MergePdf;
import md.PDFtoImages;
import md.Services;
import org.apache.commons.io.FileUtils;

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
import java.net.InetAddress;
import java.sql.*;
import java.util.*;

@SuppressWarnings("Duplicates")
public class MobileSignPrint extends HttpServlet {
/*    private Statement stmt = null;
    private   ResultSet rset = null;
    private   String Query = "";
    private Connection conn = null;*/

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

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();
        MobileServices MS = new MobileServices();
        Connection conn = null;
        try {
            //MS.myMap;
            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }

            switch (ActionID) {
                case "MobSignPdf":
                    MobSignPdf(request, out, conn, context);
                    break;
                case "GetData":
                    GetData(request, response, out, conn);
                    break;
                case "GETINPUTERDallas":
                    GETINPUTERDallas(request, out, conn, context, response, "er.dallas", "ER_Dallas", 28, "");
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.flush();
            out.close();
        }
    }

    private void MobSignPdf(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext) {
        UtilityHelper helper = new UtilityHelper();
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        try {

            String MRN = "";
            String PatientName = "";
            String AUTHID = "";
            String dbName = "";
            String SendType = "1";
            InetAddress ip = InetAddress.getLocalHost();
            long unixTime = System.currentTimeMillis() / 1000L;
            UUID uuid = UUID.randomUUID();
            String pageCount = request.getParameter("pageCount");
            String outputFilePath = request.getParameter("outputFilePath");
            String FileName = request.getParameter("FileName");
            String PatientRegId = request.getParameter("PatientRegId");
            int FacilityIdx = Integer.parseInt(request.getParameter("FacilityIdx"));
            String UserId = request.getParameter("UserId");

            Query = "Select dbname from oe.clients where Id = " + FacilityIdx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                dbName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            int found = 0;
            found = helper.signPDFCheck(request, conn, servletContext, dbName, Integer.parseInt(PatientRegId));
            if (found > 0) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "This Bundle is already been signed!");
                Parser.SetField("FormName", "DownloadBundle");
                Parser.SetField("ActionID", "GETINPUTERDallas&ID=" + PatientRegId);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                return;
            }

            StringBuffer Style = new StringBuffer();
            StringBuffer ulTag = new StringBuffer();
            PDFtoImages pdftoImage = new PDFtoImages();
            new HashMap();
            HashMap<Integer, String> images_Map_final = pdftoImage.GetValues(request, out, conn, "", 0, outputFilePath, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/md/tmpImages/");
            Collection<String> values = images_Map_final.values();
            java.util.List<String> imagelist = new ArrayList(values);
//            out.println("Image Size --> " + imagelist.size() + "<br>");
            for (int i = 0; i < imagelist.size(); ++i) {
                Style.append(".desktop-image" + (i + 1) + " {\n\tbackground-image: url(" + (String) imagelist.get(i) + ");\n\tbackground-size: cover;\n\tbackground-position: center;\n\twidth: 600px;\n\theight: 800px;\n\tmargin: 0 auto;\n\tborder: 5px solid #0f0f10;\n\tposition: relative;\n}\n\n.desktop-image" + (i + 1) + ">button {\n\twidth: 20%;\n\tposition: relative;\n\ttop: 80%;\n\tleft: 30%;\n\t/*transform: translate(50%, -50%);*/\n}");
                ulTag.append("<div id=\"page" + (i + 1) + "\" class=\"images\">\n<div  class=\"desktop-image" + (i + 1) + "\">\n<button type=\"button\" class=\"btn btn-primary\" id=\"Sign" + (i + 1) + "\"  onclick=\"signhere(" + (i + 1) + ", this.id);\" class=\"mobile-image-stella\">Sign Here " + (i + 1) + "</button>\n<img id=\"canvasimg" + (i + 1) + "\" style=\"display:none;background-color:rgba(255,255,255,0.6);\">\n</div>\n</div>\n");
            }

            Query = "Select MRN, CONCAT(IFNULL(FirstName,''), ' ', IFNULL(MiddleInitial,''), ' ', IFNULL(LastName,'')) " +
                    "from " + dbName + ".PatientReg where ID = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                MRN = rset.getString(1);
                PatientName = rset.getString(2);
            }
            rset.close();
            stmt.close();
            //AUTHID = MRN+"-"+ip+"-"+unixTime; AUTHID is equals to Signature TEXT URLDATA FOR THAT SIGNATURE ONLY TAKE ONE SIGN HERE.

//          Insert Data in the SignRequest Table here.
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + dbName + ".SignRequest (MRN,Status,isSign,IP,CreatedBy,CreatedDate," +
                                " SendType,SignBy,UID, AUTHID, PatientRegId) VALUES (?,?,?,?,?,now(),?,?,?,?,?) ");
                MainReceipt.setString(1, MRN);
                MainReceipt.setInt(2, 0);
                MainReceipt.setInt(3, 0);
                MainReceipt.setString(4, ip.toString());
                MainReceipt.setString(5, "Mobile");
                MainReceipt.setString(6, SendType);
                MainReceipt.setString(7, "");
                MainReceipt.setString(8, uuid.toString());
                MainReceipt.setString(9, "");
                MainReceipt.setString(10, PatientRegId);
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
            Parser.SetField("FacilityIdx", String.valueOf(FacilityIdx));
            Parser.SetField("UserId", UserId);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MobSigningBundle.html");


        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Sign Print ^^ ** (SignPDF)", servletContext, e, "SignPrint", "SignPDF", conn);
            Services.DumException("SignPDF", "SignPDF", request, e, getServletContext());
        }
    }

    private void GetData(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String imagedataURL = "";
        String PatientRegId = "";
        String key = "";
        String UID = "";
        String MRN = "";
        String FacilityIdx = "";
        String UserId = "";
        String pageCount = "";
        String Message = "";
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
                } else if (key.startsWith("FacilityIdx")) {
                    FacilityIdx = (String) d.get(key);
                } else if (key.startsWith("UserId")) {
                    UserId = (String) d.get(key);
                }
            }
            PatientRegId = PatientRegId.substring(4);
            imagedataURL = imagedataURL.substring(4);
            UID = UID.substring(4);
            MRN = MRN.substring(4);
            pageCount = pageCount.substring(4);
            FacilityIdx = FacilityIdx.substring(4);
            UserId = UserId.substring(4);

            String[] imageURL = imagedataURL.split("\\~");

            BufferedImage image = null;
            byte[] imageByte;
            String dbName = "";
            String DirectoryName = "";
            Query = "Select dbname,DirectoryName from oe.clients where Id = " + FacilityIdx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                dbName = rset.getString(1);
                DirectoryName = rset.getString(2);
            }
            rset.close();
            stmt.close();

            for (int i = 0; i < imageURL.length; i++) {
                try {
                    byte[] imagedata = DatatypeConverter.parseBase64Binary(imageURL[i].substring(imageURL[i].indexOf(",") + 1));
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagedata));
                    ImageIO.write(bufferedImage, "png", new File("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_" + i + "_" + PatientRegId + ".png"));

                    String ImageTransparent = this.MakeTransparent(out, "/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_" + i + "_" + PatientRegId + ".png", "/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_" + i + "_" + PatientRegId + ".png");

                    if (ImageTransparent.trim().toUpperCase().equals("CONVERTED")) {
                        Message = " and Transparency DONE";
                    } else {
                        Message = " and Image Created";
                    }

                } catch (IOException e) {
                    out.println("Error in IO" + e.getStackTrace());
                }
            }


            Query = "UPDATE " + dbName + ".SignRequest SET isSign = 1 , SignBy = 'Mobile User', SignTime = NOW() " +
                    "WHERE PatientRegId = " + PatientRegId + " AND " +
                    "MRN = " + MRN + " AND UID = '" + UID + "' ";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            //Status = 2 -- Document is Signed!
            Query = "UPDATE " + dbName + ".RequestToMobile SET Status = 2 WHERE PatientRegIdx = " + PatientRegId;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

            try {
                String outputFilePath = "/md/tmpImages";
                File directory = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/md/tmpImages/");
                FileUtils.cleanDirectory(directory);
                System.out.println("File for Deletion : " + outputFilePath);
                File File = new File(outputFilePath);
                File.delete();
            } catch (IOException var37) {
                var37.printStackTrace();
            }
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

    private void GETINPUTERDallas(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final HttpServletResponse response, final String UserId, final String Database, final int ClientId, final String DirectoryName) {
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
        String LastNameNoSpace = "";
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
                Query = " Select IFNULL(LastName,'-'), IFNULL(FirstName,'-'), IFNULL(MiddleInitial,'-'), IFNULL(Title,'-'), " +
                        "IFNULL(MaritalStatus, '-'),  IFNULL(DATE_FORMAT(DOB,'%m/%d/%Y'), '-'),  IFNULL(Age, '0'), " +
                        "IFNULL(Gender, '-'), IFNULL(Address,'-'), IFNULL(CONCAT(City,' / ', State, ' / ', ZipCode),'-'), " +
                        "IFNULL(PhNumber,'-'), IFNULL(SSN,'-'), IFNULL(Occupation,'-'), IFNULL(Employer,'-'), IFNULL(EmpContact,'-'), " +
                        "IFNULL(PriCarePhy,'-'), IFNULL(Email,'-'),  IFNULL(ReasonVisit,'-'), IFNULL(SelfPayChk,0), IFNULL(MRN,0), " +
                        "ClientIndex, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), " +
                        "IFNULL(DoctorsName,'-')  From " + Database + ".PatientReg Where ID = " + ID;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientRegId = ID;
                    LastName = rset.getString(1).trim();
                    FirstName = rset.getString(2).trim();
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

                FirstNameNoSpaces = FirstName.replaceAll("\\s+", "");
                LastNameNoSpace = LastName.replaceAll(" ", "");

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
            Query = "Select  Ethnicity,Ethnicity_OthersText,EmployementChk,Employer,Occupation,EmpContact,PrimaryCarePhysicianChk," +
                    "PriCarePhy,ReasonVisit,PriCarePhyAddress,PriCarePhyCity,PriCarePhyState,PriCarePhyZipCode,PatientMinorChk," +
                    "GuarantorChk,GuarantorEmployer,GuarantorEmployerPhNumber,GuarantorEmployerAddress,GuarantorEmployerCity," +
                    "GuarantorEmployerState,GuarantorEmployerZipCode,CreatedDate,WorkersCompPolicyChk,MotorVehicleAccidentChk," +
                    "HealthInsuranceChk from " + Database + ".PatientReg_Details where PatientRegId = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
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
                    Query = "Select IFNULL(DATE_FORMAT(WCPDateofInjury,'%m/%d/%Y'),''), IFNULL(WCPCaseNo,''), IFNULL(WCPGroupNo,''), " +
                            "IFNULL(WCPMemberId,''), IFNULL(WCPInjuryRelatedAutoMotorAccident,''), IFNULL(WCPInjuryRelatedWorkRelated,''), " +
                            "IFNULL(WCPInjuryRelatedOtherAccident,''), IFNULL(WCPInjuryRelatedNoAccident,''), IFNULL(WCPInjuryOccurVehicle,''), " +
                            "IFNULL(WCPInjuryOccurWork,''), IFNULL(WCPInjuryOccurHome,''), IFNULL(WCPInjuryOccurOther,''), IFNULL(WCPInjuryDescription,''), " +
                            "IFNULL(WCPHRFirstName,''), IFNULL(WCPHRLastName,''), IFNULL(WCPHRPhoneNumber,''), IFNULL(WCPHRAddress,''), IFNULL(WCPHRCity,''), " +
                            "IFNULL(WCPHRState,''), IFNULL(WCPHRZipCode,''), IFNULL(WCPPlanName,''), IFNULL(WCPCarrierName,''), " +
                            "IFNULL(WCPPayerPhoneNumber,''), IFNULL(WCPCarrierAddress,''), IFNULL(WCPCarrierCity,''), IFNULL(WCPCarrierState,''), " +
                            "IFNULL(WCPCarrierZipCode,''), IFNULL(WCPAdjudicatorFirstName,''), IFNULL(WCPAdjudicatorLastName,''), " +
                            "IFNULL(WCPAdjudicatorPhoneNumber,''), IFNULL(WCPAdjudicatorFaxPhoneNumber,'') " +
                            "from " + Database + ".Patient_WorkCompPolicy where PatientRegId = " + ID;
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
            String UID = "";
            com.itextpdf.text.Image SignImages = null;
            final File tmpDir = new File("/sftpdrive/AdmissionBundlePdf/SignImg/ERDallas/img_0_" + ID + ".png");
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

                SignImages = com.itextpdf.text.Image.getInstance("/sftpdrive/AdmissionBundlePdf/SignImg/ERDallas/img_0_" + ID + ".png");
                SignImages.scaleAbsolute(80.0f, 30.0f);
                //outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastName + ID + "_" + UID + "_.pdf";
            } else {
                SignImages = null;
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

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100, 80f);
                        pdfContentByte.addImage(SignImages);
                    }

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

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100, 120.0f);
                        pdfContentByte.addImage(SignImages);
                    }

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

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100, 210.0f);
                        pdfContentByte.addImage(SignImages);
                    }

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

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(195, 140.0f);
                        pdfContentByte.addImage(SignImages);
                    }

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


                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100, 280.0f);
                        pdfContentByte.addImage(SignImages);
                    }

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

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100, 290.0f);
                        pdfContentByte.addImage(SignImages);
                    }

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

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(370, 180.0f);
                        pdfContentByte.addImage(SignImages);
                    }

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

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(210, 90.0f);
                        pdfContentByte2.addImage(SignImages);
                    }

                }
            }
            pdfStamper2.close();
            ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/GeneralForm_ERDallas.pdf";
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
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
                mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/WC_MVA_AUTHORIZATIONTOOBTAININFORMATION_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/WC_QUESTIONNAIRE_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
                if (WCPInjuryRelatedAutoMotorAccident.equals("1") || WCPInjuryOccurVehicle.equals("1")) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf", "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/WC_MVA_assignmentofproceeds_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
                }
            }
            /*else {
                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/GeneralForm_ERDallas.pdf";
            }*/
            if (MotorVehicleAccidentChk == 1) {
//                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/MVA_ASSIGNMENTOFPROCEEDS_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
//                ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
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
            }
//            else if (MotorVehicleAccidentChk == 0) {
//                mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/MVA_ASSIGNMENTOFPROCEEDS.pdf", ClientId, MRN);
//                ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
//            } else {
//                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/GeneralForm_ERDallas.pdf";
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
                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/MEDICAIDSELFPAYAGREEMENT_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
                    }
                    if (GFIPMedicare == 1) {
                        mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/ABNformEnglish_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                        ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
                    }
                }
                if (HIPrimaryInsurance.trim().toUpperCase().equals("UNITED HEALTHCARE")) {
                    mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/UHCINSAPPEALFORMS_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
                    ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
                }
            }
//            else {
//                ResultPdf = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/GeneralForm_ERDallas.pdf";
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
//            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/FINANCIAL_HARDSHIP_RELIEF_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
//            ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/Medicalreleaseform_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
            ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
//            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/Marketing_Slips_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
//            ResultPdf = "/sftpdrive/AdmissionBundlePdf/Victoria/Result_" + ClientId + "_" + MRN + ".pdf";
//            mergePdf.GETINPUT(request, response, out, conn, Database, ResultPdf, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/ERDallas/TempDir/Medicalreleaseform_" + ClientId + "_" + MRN + ".pdf", ClientId, MRN);
//            ResultPdf = "/sftpdrive/AdmissionBundlePdf/ERDallas/Result_" + ClientId + "_" + MRN + ".pdf";
            String DOSDate = "";
            String DOSTime = "";
            DOSDate = DOS.substring(0, 10);
            DOSTime = DOS.substring(11, 19);
            String inputFilePath = "";
            inputFilePath = ResultPdf;
            //System.out.println("Result PDF: "+ResultPdf);


            //String outputFilePath = "/sftpdrive/AdmissionBundlePdf/ERDallas/" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf";
            String outputFilePath = "/sftpdrive/AdmissionBundlePdf/ERDallas/" + FirstNameNoSpaces + LastNameNoSpace + ID + "_" + ".pdf";
            final OutputStream fos3 = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader3 = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper3 = new PdfStamper(pdfReader3, fos3);
            final int pageCount = pdfReader3.getNumberOfPages();

            outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/" + FirstNameNoSpaces + LastNameNoSpace + ID + "_.pdf";
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
                    pdfContentByte3.showText(GuarantorEmployerAddress + "  " + GuarantorEmployerCity + " " + GuarantorEmployerState);
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

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(180, 80.0f);
                        pdfContentByte3.addImage(SignImages);
                    }

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

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(190, 145.0f);
                        pdfContentByte3.addImage(SignImages);
                    }

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

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(120, 270.0f);
                        pdfContentByte3.addImage(SignImages);
                    }

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(380.0f, 270.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 5) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(190, 70.0f);
                        pdfContentByte3.addImage(SignImages);
                    }

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

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(120, 360.0f);
                        pdfContentByte3.addImage(SignImages);
                    }

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

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(100, 160.0f);
                        pdfContentByte3.addImage(SignImages);
                    }

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(440.0f, 160.0f);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();
                }
                if (k == 8) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(410.0f, 130);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(140, 130.0f);
                        pdfContentByte3.addImage(SignImages);
                    }

                }

                if (k == 9) {
                    final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(k);

                    pdfContentByte3.beginText();
                    pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                    pdfContentByte3.setColorFill(BaseColor.BLACK);
                    pdfContentByte3.setTextMatrix(410.0f, 145);
                    pdfContentByte3.showText(Date);
                    pdfContentByte3.endText();

                    if (SignImages != null) {
                        SignImages.setAbsolutePosition(140, 145);
                        pdfContentByte3.addImage(SignImages);
                    }

                }
            }
            pdfStamper3.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("outputFilePath", outputFilePath);
            //Parser.SetField("imagelist", String.valueOf(imagelist));
            Parser.SetField("pageCount", String.valueOf(pageCount));
            Parser.SetField("PatientRegId", String.valueOf(ID));
            Parser.SetField("FileName", FirstNameNoSpaces + LastNameNoSpace + ID + "_.pdf");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/DownloadBundleHTML.html");

/*            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + FirstNameNoSpaces + LastName + ID + "_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }*/

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

}
