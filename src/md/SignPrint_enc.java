

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.InetAddress;
import java.security.Key;
import java.sql.*;
import java.util.*;

//import com.itextpdf.text.pdf.PdfWriter;
@SuppressWarnings("Duplicates")
public class SignPrint_enc extends HttpServlet {
    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[]{'T', '#', '3', 'B', '3', '$', 'T',
                    '$', '3', 'C', 'r', '3', 't', 'K', '3', 'Q'};
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private Connection conn = null;

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

    public static byte[] encrypt(byte[] Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data);
//        byte[] encryptedValue = new BASE64Encoder().encode(Data);
        return encVal;
    }

    public static byte[] decrypt(byte[] encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
//        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(encryptedData);
//        String decryptedValue = new String(decValue);
        return decValue;
    }

    private static Key generateKey() {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
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
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        String Source = "";
        String Dest = "";
        String DirectoryName = "";
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();

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
            DirectoryName = session.getAttribute("DirectoryName").toString();

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }

            if (ActionID.equals("SignPdf")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Signing", "Download or View Admission Bundle", FacilityIndex);
                SignPdf(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName, helper);
            } else if (ActionID.equals("GetData") && request.getContentType().startsWith("multipart/form-data")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Input", "Click on Search Old Patient Option", FacilityIndex);
                GetData(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("MakeTransparent")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Make Images Transparent", "Make Images Transparent", FacilityIndex);
                MakeTransparent(out, Source, Dest);
            } else if (ActionID.equals("requestGenerateForTablet")) {
                sendToTablet(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
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

    void SignPdf(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId, String DirectoryName, UtilityHelper helper) {
        try {

            int MRN = 0;
            String PatientName = "";
            String AUTHID = "";
            String SendType = "1";
            InetAddress ip = InetAddress.getLocalHost();
            long unixTime = System.currentTimeMillis() / 1000L;
            UUID uuid = UUID.randomUUID();
            String pageCount = request.getParameter("pageCount");
            String outputFilePath = request.getParameter("outputFilePath");
            String FileName = request.getParameter("FileName");
            int PatientRegId = Integer.parseInt(request.getParameter("PatientRegId"));

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

            MRN = helper.getPatientRegMRN(request, conn, servletContext, Database, PatientRegId);

            found = helper.signPDFCheck(request, conn, servletContext, "ER_Dallas", PatientRegId);
            isExist = helper.signPDFCheckMobile(request, conn, servletContext, "ER_Dallas", PatientRegId);

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

            int requestCheck = helper.requestMobileCheck(request, conn, servletContext, Database, PatientRegId, MRN, ClientId);
            if (requestCheck > 0) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "This Bundle is already been sent to Mobile for Signing. Please sign from Mobile!");
                Parser.SetField("FormName", "DownloadBundle");
                Parser.SetField("ActionID", "GETINPUTERDallas&ID=" + PatientRegId);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                return;
            }

            StringBuffer Style = new StringBuffer();
            StringBuffer ulTag = new StringBuffer();
            PDFtoImages pdftoImage = new PDFtoImages();
            new HashMap();
            HashMap<Integer, String> images_Map_final = pdftoImage.GetValues(request, out, conn, Database, ClientId, outputFilePath, "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/md/tmpImages/");
            Collection<String> values = images_Map_final.values();
            java.util.List<String> imagelist = new ArrayList(values);

            for (int i = 0; i < imagelist.size(); ++i) {
                Style.append(".desktop-image" + (i + 1) + " {\n\tbackground-image: url(" + imagelist.get(i) + ");\n\tbackground-size: cover;\n\tbackground-position: center;\n\twidth: 600px;\n\theight: 800px;\n\tmargin: 0 auto;\n\tborder: 5px solid #0f0f10;\n\tposition: relative;\n}\n\n.desktop-image" + (i + 1) + ">button {\n\twidth: 20%;\n\tposition: relative;\n\ttop: 80%;\n\tleft: 30%;\n\t/*transform: translate(50%, -50%);*/\n}");
                ulTag.append("<div id=\"page" + (i + 1) + "\" class=\"images\">\n<div  class=\"desktop-image" + (i + 1) + "\">\n<button type=\"button\" class=\"btn btn-primary\" id=\"Sign" + (i + 1) + "\"  onclick=\"signhere(" + (i + 1) + ", this.id);\" class=\"mobile-image-stella\">Sign Here " + (i + 1) + "</button>\n<img id=\"canvasimg" + (i + 1) + "\" style=\"display:none;background-color:rgba(255,255,255,0.6);\">\n</div>\n</div>\n");
            }

/*            Query = "Select MRN, CONCAT(IFNULL(FirstName,''), ' ', IFNULL(MiddleInitial,''), ' ', IFNULL(LastName,'')) " +
                    "from ER_Dallas.PatientReg where ID = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                MRN = rset.getString(1);
                PatientName = rset.getString(2);
            }
            rset.close();
            stmt.close();*/
            //AUTHID = MRN+"-"+ip+"-"+unixTime; AUTHID is equals to Dignature TEXT URLDATA FOR THAT SIGNATURE ONLY TAKE ONE SIGN HERE.

//          Insert Data in the SignRequest Table here.
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO ER_Dallas.SignRequest (MRN,Status,isSign,IP,CreatedBy,CreatedDate," +
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
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/SigningBundle.html");

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
                }
            }
            PatientRegId = PatientRegId.substring(4);
            imagedataURL = imagedataURL.substring(4);
            UID = UID.substring(4);
            MRN = MRN.substring(4);
            pageCount = pageCount.substring(4);

            String[] imageURL = imagedataURL.split("\\~");

            BufferedImage image = null;
            byte[] imageByte;
            for (int i = 0; i < imageURL.length; i++) {
//                out.println(imageURL[i]);
                try {
                    byte[] imagedata = DatatypeConverter.parseBase64Binary(imageURL[i].substring(imageURL[i].indexOf(",") + 1));
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagedata));
                    //ImageIO.write(bufferedImage, "png", new File("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_" + i + "_" + PatientRegId + ".png"));
                    ImageIO.write(bufferedImage, "png", new File("/sftpdrive/AdmissionBundlePdf/SignImg/erDallas/img_" + i + "_" + PatientRegId + ".png"));

                    //String ImageTransparent = this.MakeTransparent(out, "/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_" + i + "_" + PatientRegId + ".png", "/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_" + i + "_" + PatientRegId + ".png");
                    String ImageTransparent = this.MakeTransparent(out, "/sftpdrive/AdmissionBundlePdf/SignImg/erDallas/img_" + i + "_" + PatientRegId + ".png", "/sftpdrive/AdmissionBundlePdf/SignImg/erDallas/img_" + i + "_" + PatientRegId + ".png");

                    if (ImageTransparent.trim().toUpperCase().equals("CONVERTED")) {
                        Message = " and Transparency DONE";
                    } else {
                        Message = " and Image Created";
                    }

                } catch (IOException e) {
                    out.println("Error in IO" + e.getStackTrace());
                }
                break;
            }

            Query = "UPDATE ER_Dallas.SignRequest SET isSign = 1 , SignBy = '" + UserId + "', SignTime = NOW() " +
                    "WHERE PatientRegId = " + PatientRegId + " AND " +
                    "MRN = " + MRN + " AND UID = '" + UID + "' ";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Done! Signed PDF is Ready " + Message);
            Parser.SetField("FormName", "DownloadBundle");
            Parser.SetField("ActionID", "GETINPUTERDallas&ID=" + PatientRegId);
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message.html");

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
        try {
            File directory = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/md/tmpImages/");
            FileUtils.cleanDirectory(directory);
        } catch (IOException e) {
            e.printStackTrace();
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

            FileInputStream fis = new FileInputStream(outputFileName);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            data = encrypt(data);
            FileOutputStream fos = new FileOutputStream(outputFileName);
            fos.write(data);
            fos.close();
            fis.close();

        } catch (Exception e) {
            out.println(e.getMessage());
        }
        return "CONVERTED";
    }

    private void sendToTablet(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) {
        int MRN = 0;

        UUID uuid = UUID.randomUUID();
        String pageCount = request.getParameter("pageCount");
        String outputFilePath = request.getParameter("outputFilePath");
        String FileName = request.getParameter("FileName");
        int PatientRegId = Integer.parseInt(request.getParameter("PatientRegId"));

        try {
/*            Query = "Select MRN, CONCAT(IFNULL(FirstName,''), ' ', IFNULL(MiddleInitial,''), ' ', IFNULL(LastName,'')) " +
                    "from frontlin_er.PatientReg where ID = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                MRN = rset.getString(1);
                PatientName = rset.getString(2);
            }
            rset.close();
            stmt.close();*/

            int isExist = 0;
            isExist = helper.signPDFCheckMobile(request, conn, servletContext, "ER_Dallas", PatientRegId);
            if (isExist > 0) {
                out.println("12");
                return;
            }
            int found = 0;
            found = helper.signPDFCheckMobile(request, conn, servletContext, "ER_Dallas", PatientRegId);

            if (found > 0) {
                out.println("11");
                return;
            }


            MRN = helper.getPatientRegMRN(request, conn, servletContext, Database, PatientRegId);

            String UserIP = helper.getClientIp(request);

            int requestCheck = helper.requestMobileCheck(request, conn, servletContext, Database, PatientRegId, MRN, ClientId);
            if (requestCheck == 0) {
                //Status = 0 -- Means it is a fresh request
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO ER_Dallas.RequestToMobile (UID, MRN, FacilityIndex, OutputPath, pageCount, FileName, " +
                                "PatientRegIdx,Status,CreatedDate,UserIP) VALUES (?,?,?,?,?,?,?,0,NOW(),?) ");
                MainReceipt.setString(1, uuid.toString());
                MainReceipt.setInt(2, MRN);
                MainReceipt.setInt(3, ClientId);
                MainReceipt.setString(4, outputFilePath);
                MainReceipt.setString(5, pageCount);
                MainReceipt.setString(6, FileName);
                MainReceipt.setInt(7, PatientRegId);
                MainReceipt.setString(8, UserIP);
                MainReceipt.executeUpdate();
                MainReceipt.close();

                out.println("1");
            } else {
                out.println("9");
            }
        } catch (Exception e) {
            out.println("0");
            out.println("Error in sendToTablet Method: " + e.getMessage());
        }
    }
}
