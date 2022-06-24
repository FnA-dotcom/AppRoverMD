package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import org.apache.commons.io.FileUtils;

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
import java.sql.*;
import java.util.*;

@SuppressWarnings("Duplicates")
public class SignPrint extends HttpServlet {

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
//        System.out.println("File " + in + " isValid -> " + isValid);
        return isValid;
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
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        String UserId;
        int FacilityIndex;
        String DatabaseName;
        String Source = "";
        String Dest = "";
        String DirectoryName = "";
        String Bundle_Name = "";
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


            Query = "Select Bundle_FnName from oe.clients where Id = " + FacilityIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Bundle_Name = rset.getString(1);
            }
            rset.close();
            stmt.close();


            if (ActionID.equals("SignPdf")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Signing", "Download or View Admission Bundle", FacilityIndex);
                SignPdf(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName, helper, Bundle_Name);
            } else if (ActionID.equals("isSigned")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "isSigned", "isSigned", FacilityIndex);
                isSigned(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName, helper, Bundle_Name);
            } else if (ActionID.equals("GetData") && request.getContentType().startsWith("multipart/form-data")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Input", "Click on Search Old Patient Option", FacilityIndex);
                GetData(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, DirectoryName, Bundle_Name);
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

    void SignPdf(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId, String DirectoryName, UtilityHelper helper, String bundle_Name) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";

            double[][] Victoria_Sign_Cordinate = {{86, 26}, {77, 28}, {95, 30}, {61, 11}, {87, 31},
                    {50, 17}, {75, 15}, {80.8, 26.7}, {77.1, 11}, {60.8, 14.5},
                    {73.8, 57.4}, {77.6, 42}, {94, 30}, {60, 14}, {80, 30},
                    {80, 30}, {60.6, 15}, {73.7, 57}, {80, 30}};
            int MRN = 0;
            String PatientName = "";
            String Facility = "";
            String AUTHID = "";
            String SendType = "0";
            InetAddress ip = InetAddress.getLocalHost();
            long unixTime = System.currentTimeMillis() / 1000L;
            UUID uuid = UUID.randomUUID();
            String pageCount = request.getParameter("pageCount");
            String outputFilePath = request.getParameter("outputFilePath");
            String FileName = request.getParameter("FileName");
            String rqType = request.getParameter("rqtype");
//            System.out.println("RQ TYPE IN Sign Print ** " + rqType);
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
            Facility = helper.getFacilityName(request, conn, servletContext, ClientId);
//            System.out.println("ClientId -> " + ClientId);
//            System.out.println("Database -> " + Database);

            if (ClientId == 19 || ClientId == 28 || ClientId == 39 || ClientId == 40 || ClientId == 41 || ClientId == 42 || ClientId == 43) {
                isExist = helper.signPDFCheckMobile(request, conn, servletContext, Database, PatientRegId);
                if (isExist > 0) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Message", "Bundle has already been sent for Signing");
                    Parser.SetField("FormName", "DownloadBundle");
                    Parser.SetField("ActionID", bundle_Name + "&ID=" + PatientRegId);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                    return;
                }
                int requestCheck = helper.requestMobileCheck(request, conn, servletContext, Database, PatientRegId, MRN, ClientId);
                if (requestCheck > 0) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Message", "This Bundle is already been sent to Mobile for Signing. Please sign from Mobile!");
                    Parser.SetField("FormName", "DownloadBundle");
                    Parser.SetField("ActionID", bundle_Name + "&ID=" + PatientRegId);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                    return;
                }
            }

            found = helper.signPDFCheck(request, conn, servletContext, Database, PatientRegId);
            if (found > 0) {
//                out.println("0");
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "This Bundle is already been signed!");
                Parser.SetField("FormName", "DownloadBundle");
                Parser.SetField("ActionID", bundle_Name + "&ID=" + PatientRegId);
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
                if (ClientId == 9) {
                    Style.append(".desktop-image" + (i + 1) + " {\n\tbackground-image: url(" + (String) imagelist.get(i) + ");\n\tbackground-size: cover;\n\tbackground-position: center;\n\twidth: 600px;\n\theight: 800px;\n\tmargin: 0 auto;\n\tborder: 5px solid #0f0f10;\n\tposition: relative;\n}\n\n.desktop-image" + (i + 1) + ">button {\n\twidth: 20%;\n\tposition: relative;\n\ttop: " + Victoria_Sign_Cordinate[i][0] + "%;\n\tleft: " + Victoria_Sign_Cordinate[i][1] + "%;\n\t/*transform: translate(50%, -50%);*/\n}");
                    ulTag.append("<div id=\"page" + (i + 1) + "\" class=\"images\">\n<div  class=\"desktop-image" + (i + 1) + "\">\n<button type=\"button\" class=\"btn btn-primary\" id=\"Sign" + (i + 1) + "\"  onclick=\"signhere(" + (i + 1) + ", this.id);\" class=\"mobile-image-stella\">Sign Here " + (i + 1) + "</button>\n<img id=\"canvasimg" + (i + 1) + "\" style=\"display:none;background-color:rgba(255,255,255,0.6);\">\n</div>\n</div>\n");
                } else {
                    Style.append(".desktop-image" + (i + 1) + " {\n\tbackground-image: url(" + (String) imagelist.get(i) + ");\n\tbackground-size: cover;\n\tbackground-position: center;\n\twidth: 600px;\n\theight: 800px;\n\tmargin: 0 auto;\n\tborder: 5px solid #0f0f10;\n\tposition: relative;\n}\n\n.desktop-image" + (i + 1) + ">button {\n\twidth: 20%;\n\tposition: relative;\n\ttop: 80%;\n\tleft: 30%;\n\t/*transform: translate(50%, -50%);*/\n}");
                    ulTag.append("<div id=\"page" + (i + 1) + "\" class=\"images\">\n<div  class=\"desktop-image" + (i + 1) + "\">\n<button type=\"button\" class=\"btn btn-primary\" id=\"Sign" + (i + 1) + "\"  onclick=\"signhere(" + (i + 1) + ", this.id);\" class=\"mobile-image-stella\">Sign Here " + (i + 1) + "</button>\n<img id=\"canvasimg" + (i + 1) + "\" style=\"display:none;background-color:rgba(255,255,255,0.6);\">\n</div>\n</div>\n");
                }
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

                Query = "Select Count(*) from " + Database + ".SignRequest " +
                        "where PatientRegId = " + PatientRegId + "";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    found = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (found > 0) {
                    Query = "Update " + Database + ".SignRequest set isSign = 0 , Status = 0, IP= '" + ip.toString() + "' , CreatedBy ='" + UserId + "' , CreatedDate=now(), " +
                            " SendType = " + SendType + " , SignBy=null , UID='" + uuid.toString() + "' , AUTHID=null, PatientRegId=" + PatientRegId +
                            " where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                } else {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + Database + ".SignRequest (MRN,Status,isSign,IP,CreatedBy,CreatedDate," +
                                    " SendType,SignBy,UID, AUTHID, PatientRegId,SignedFrom,SignCount) VALUES (?,?,?,?,?,now(),?,?,?,?,?,'REGISTRATION',0) ");
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
                }
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
            Parser.SetField("Facility", String.valueOf(Facility));
            Parser.SetField("rqtype", rqType);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/SigningBundle.html");

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Sign Print ^^ ** (SignPDF)", servletContext, e, "SignPrint", "SignPDF", conn);
            Services.DumException("SignPDF", "SignPDF", request, e, getServletContext());
        }


    }

    void isSigned(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId, String DirectoryName, UtilityHelper helper, String bundle_Name) {
        try {

            int PatientRegId = Integer.parseInt(request.getParameter("PatientRegId"));
            int found = 0;
            found = helper.signPDFCheck(request, conn, servletContext, Database, PatientRegId);
            if (found > 0) {
                out.println("1");
            } else {
                out.println("0");
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Sign Print ^^ ** (isSigned)", servletContext, e, "SignPrint", "SignPDF", conn);
            Services.DumException("isSigned", "isSigned", request, e, getServletContext());
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

    void GetData(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, String DirectoryName, String bundle_Name) {
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
        String outputFilePath = "";
        String rqType = "";
        String WEB = "";
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
                } else if (key.startsWith("outputFilePath")) {
                    outputFilePath = (String) d.get(key);
                } else if (key.startsWith("rqtype")) {
                    rqType = (String) d.get(key);
                }
            }

//            System.out.println("*** INI RQTYPE " + rqType);
            PatientRegId = PatientRegId.substring(4);
            imagedataURL = imagedataURL.substring(4);
            UID = UID.substring(4);
            MRN = MRN.substring(4);
            pageCount = pageCount.substring(4);
            outputFilePath = outputFilePath.substring(4);
//            rqType = rqType.substring(4);

            String[] imageURL = imagedataURL.split("\\~");

            BufferedImage image = null;
            byte[] imageByte;
            for (int i = 0; i < imageURL.length; i++) {
//                out.println(imageURL[i]);
                try {
                    byte[] imagedata = DatatypeConverter.parseBase64Binary(imageURL[i].substring(imageURL[i].indexOf(",") + 1));
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagedata));
                    //ImageIO.write(bufferedImage, "png", new File("/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_" + i + "_" + PatientRegId + ".png"));
                    ImageIO.write(bufferedImage, "png", new File("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_" + i + "_" + PatientRegId + ".png"));


                    if (isValid(new File("/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_" + i + "_" + PatientRegId + ".png"))) {
                        //String ImageTransparent = this.MakeTransparent(out, "/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_" + i + "_" + PatientRegId + ".png", "/sftpdrive/AdmissionBundlePdf/SignImg/frontline/img_" + i + "_" + PatientRegId + ".png");
                        String ImageTransparent = this.MakeTransparent(out, "/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_" + i + "_" + PatientRegId + ".png", "/sftpdrive/AdmissionBundlePdf/SignImg/" + DirectoryName + "/img_" + i + "_" + PatientRegId + ".png");
                        if (ImageTransparent.trim().toUpperCase().equals("CONVERTED")) {
                            Message = " and Transparency DONE";
                        } else {
                            Message = " and Image Created";
                        }
                    } else {


                        Query = "DELETE FROM " + Database + ".SignRequest WHERE PatientRegId = '" + PatientRegId + "' ";
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();
                        Parsehtm Parser = new Parsehtm(request);
                        Parser.SetField("Message", "Invalid Signature, Please Try Again!");
                        Parser.SetField("MRN", "Invalid Signature");
//                        Parser.SetField("FormName", "PatientReg");
//                        Parser.SetField("ActionID", "GetValues&ClientIndex=36");
                        Parser.SetField("pageCount", String.valueOf(pageCount));
                        Parser.SetField("FileName", String.valueOf(outputFilePath));
                        Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
                        Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
                        Parser.SetField("ClientIndex", String.valueOf(ClientId));
                        Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/MessageVictoria.html");
                        return;
                    }

                } catch (IOException e) {
                    out.println("Error in IO" + e.getStackTrace());
                }
                break;
            }

            Query = "UPDATE " + Database + ".SignRequest SET isSign = 1 , SignBy = '" + UserId + "', SignTime = NOW() , SignCount=SignCount+1  " +
                    "WHERE PatientRegId = " + PatientRegId + " AND " +
                    "MRN = " + MRN + " AND UID = '" + UID + "' ";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();


            if (ClientId == 9) {

                String signedFrom = null;
                Query = "Select SignedFrom  from " + Database + ".SignRequest where PatientRegId=" + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    signedFrom = rset.getString(1);
                }
                stmt.close();
                rset.close();

                PatientReg2 ptr2 = new PatientReg2();
                ptr2.SaveBundle_Victoria(request, out, conn, response, Database, ClientId, DirectoryName, Integer.parseInt(PatientRegId), signedFrom + "SIGNED");
            }

            Parsehtm Parser = new Parsehtm(request);
            if (ClientId == 41 || ClientId == 42 || ClientId == 43 || ClientId == 39 || ClientId == 40 || ClientId == 19 || ClientId == 45) {

                PreparedStatement ps = conn.prepareStatement("SELECT website from oe.ClientsWebsite where clientID=?");
                ps.setInt(1, ClientId);
                rset = ps.executeQuery();
                if (rset.next()) {
                    WEB = rset.getString(1);
                }
                ps.close();
                rset.close();

//                System.out.println("**** RQ TYPE **** " + rqType);
//                Parser.SetField("Message", "Done! Signed PDF is Ready " + Message);
//                Parser.SetField("FormName", "DownloadBundle");
//                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message_W.html");            if(
                System.out.println("rqType ->> " + rqType);
                if (rqType.equals("nullGetValues")) {
                    Parser.SetField("Message", "Thank You for Registration ");
                    Parser.SetField("WEB", WEB);
                    Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message_SumWill.html");
                } else {
                    Parser.SetField("Message", "Done! Signed PDF is Ready " + Message);
                    Parser.SetField("FormName", "PatientUpdateInfo");
                    Parser.SetField("ActionID", "GetInput&ID=" + PatientRegId);
                    Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message.html");

                }

                /*if (UserId.contains("Mobile")) {
                    Parser.SetField("Message", "Thank You for Registration ");
//                Parser.SetField("FormName", "LifeSaversBundle");
//                Parser.SetField("ActionID", bundle_Name + "&ID=" + PatientRegId);
//                    Parser.SetField("FormName", "PatientUpdateInfo");
//                    Parser.SetField("ActionID",  "GetInput&ID=" + PatientRegId);
                    Parser.SetField("WEB", WEB);
                    Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message_SumWill.html");
                } else {
//                    Parser.SetField("Message", "Done! Signed PDF is Ready " + Message);
//                Parser.SetField("FormName", "DownloadBundle");
//                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message_W.html");
                    Parser.SetField("Message", "Done! Signed PDF is Ready " + Message);
//                Parser.SetField("FormName", "LifeSaversBundle");
//                Parser.SetField("ActionID", bundle_Name + "&ID=" + PatientRegId);
                    Parser.SetField("FormName", "PatientUpdateInfo");
                    Parser.SetField("ActionID", "GetInput&ID=" + PatientRegId);
                    Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message.html");
                }*/

            } else {
                Parser.SetField("Message", "Done! Signed PDF is Ready " + Message);
                Parser.SetField("FormName", "PatientUpdateInfo");
                Parser.SetField("ActionID", "GetInput&ID=" + PatientRegId);
                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message.html");
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

        try {
            File directory = new File("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/md/tmpImages/");
            FileUtils.cleanDirectory(directory);

//            System.out.println("File for Deletion : " + outputFilePath);
            File File = new File(outputFilePath);
            File.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendToTablet(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

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
            int found = 0;
            found = helper.signPDFCheckMobile(request, conn, servletContext, Database, PatientRegId);

            if (found > 0) {
                out.println("11");
                return;
            }
            int isExist = 0;
            isExist = helper.signPDFCheckMobile(request, conn, servletContext, Database, PatientRegId);
            if (isExist > 0) {
                out.println("12");
                return;
            }


            MRN = helper.getPatientRegMRN(request, conn, servletContext, Database, PatientRegId);

            String UserIP = helper.getClientIp(request);

            int requestCheck = helper.requestMobileCheck(request, conn, servletContext, Database, PatientRegId, MRN, ClientId);
            if (requestCheck == 0) {
                //Status = 0 -- Means it is a fresh request
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".RequestToMobile (UID, MRN, FacilityIndex, OutputPath, pageCount, FileName, " +
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
