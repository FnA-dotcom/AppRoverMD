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
import java.sql.*;
import java.util.*;

@SuppressWarnings("Duplicates")
public class LabManifests extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ActionID = "";
        String UserId = "";
        String DatabaseName = "";
        String DirectoryName = "";
        HttpSession session = null;
        boolean validSession = false;
        int FacilityIndex = 0;
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
        Connection conn = null;
        String locationArray = "";
        try {
            Parsehtm Parser;
            session = request.getSession(false);
            validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            locationArray = session.getAttribute("LocationArray").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            DirectoryName = session.getAttribute("DirectoryName").toString();
            if (UserId.equals("")) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("Error", "Unable to connect. Our team is looking into it!");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }

            switch (ActionID) {
                case "GetInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "RoverLab Admission Bundle", "Download or View Admission Bundle", FacilityIndex);
                    GetInput(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, helper, locationArray);
                    break;
                case "SaveManifest":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Saving Manifests of Lab", "Saving.... Manifest", FacilityIndex);
                    SaveManifest(request, out, conn, context,response, UserId, DatabaseName, FacilityIndex, helper, DirectoryName);
                    break;
                case "manifestPDF":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Saving Manifests of Lab", "Saving.... Manifest", FacilityIndex);
                    manifestPDF(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, DirectoryName, 0);
                    break;
                default: {
                    helper.deleteUserSession(request, conn, session.getId());
                    session.invalidate();
                    Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
                }
            }
        } catch (Exception e) {
            if (conn == null) {
                conn = Services.getMysqlConn(context);
            }
            helper.SendEmailWithAttachment("Error in PatientReg ** (handleRequest)", context, e, "PatientReg", "handleRequest", conn);
            Services.DumException("PatientReg", "Handle Request", request, e, getServletContext());
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
                helper.SendEmailWithAttachment("Error in PatientReg ** (handleRequest -- SqlException)", context, e, "PatientReg", "handleRequest", conn);
                Services.DumException("PatientReg", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }

    private void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId, UtilityHelper helper, String locationArray) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;
        StringBuilder ManifestList = new StringBuilder();
        StringBuilder locationList = new StringBuilder();
        String Defaultlocation="";
        try {
            ArrayList<String> al = new ArrayList<>();
            al.add(locationArray);
            String list = Arrays.toString(al.toArray()).replace("[", "").replace("]", "");

            String locCondition = "";
            if (locationArray.length() > 1) {
                locCondition = " AND d.Id IN (" + list + ") ";
            }
            String Filter="";

            String _default="selected";
            Query = "Select Id, Location from roverlab.Locations WHERE Id IN (" + list + ") ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
           // locationList.append("<option value='-1' selected disabled>Select Location</option>");
            while (rset.next()) {

                locationList.append("<option value=" + rset.getString(1) + " "+_default+" >" + rset.getString(2) + "</option>");
                if(_default.compareTo("selected")==0) {
                	//locCondition = " AND d.Id IN (" +  rset.getString(1) + ") ";
                	Defaultlocation=" and d.Id="+rset.getString(1);
                	Filter=rset.getString(1);
                }
                _default="";
            }
            rset.close();
            stmt.close();

            if(request.getParameter("Loc_id")!=null) {

            	Defaultlocation=" and d.Id="+request.getParameter("Loc_id");
            	Filter=request.getParameter("Loc_id");
        	}

            Query = " SELECT CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " + //1
                    "DATE_FORMAT(a.DOB,'%m/%d/%Y'),  IFNULL(a.PhNumber,''), IFNULL(a.MRN,0), a.ID AS PatRegId, " +//5
                    "DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'),  " + //6
                    "CASE " +
                    "WHEN a.Insured = 'Yes' THEN 'Insured' " +
                    "WHEN a.Insured = 'NO' THEN 'Self Pay' " +
                    "ELSE 'Self Pay' " +
                    "END, b.OrderNum,b.OrderDate," + //9
                    "CASE " +
                    " WHEN a.Status = 0 THEN c.Status " +
                    " WHEN a.Status = 1 THEN c.Status" +
                    " WHEN a.Status = 2 THEN c.Status" +
                    " WHEN a.Status = 3 THEN c.Status " +
                    " ELSE 'Pending' END,d.Location " + //10
                    " FROM "+Database+".PatientReg a" +
                    " INNER JOIN "+Database+".TestOrder b ON a.ID = b.PatRegIdx " +
                    " INNER JOIN "+Database+".ListofStages c ON b.StageIdx = c.Id " +
                    " INNER JOIN "+Database+".Locations d ON a.TestingLocation = d.Id" +
                    " WHERE a.Status = 0 AND b.StageIdx = 0 " + Defaultlocation +
                    " ORDER BY a.CreatedDate DESC limit 500";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ManifestList.append("<tr>\n");
              //  ManifestList.append("<td align=left><input  class=\"checkSingle\" type=\"checkbox\" name=\"mList_" + rset.getInt(5) + "\" > \n");
      //          ManifestList.append("<td align=left><input  class=\"checkSingle\" type=\"checkbox\" id=\"mList_" + rset.getInt(5) + "\" > \n");
                ManifestList.append("<td align=left><input  class='checkSingle' type='checkbox' name='mList_"+rset.getInt(5)+"'  id='mList_" + rset.getInt(5) + "' > \n");

                ManifestList.append("<label for=\"mList_" + rset.getInt(5) + "\">" + rset.getString(8) + "</label><br></td>\n");//OrderID  class"form-check-input"
                ManifestList.append("<td align=left>" + rset.getString(4) + "</td>\n");//MRN
                ManifestList.append("<td align=left>" + rset.getString(1) + "</td>\n");//PatientName
//                ManifestList.append("<td align=left>" + rset.getString(2) + "</td>\n");//DOB
                ManifestList.append("<td align=left>" + rset.getString(3) + "</td>\n");//Num
                ManifestList.append("<td align=left>" + rset.getString(9) + "</td>\n");//DOS
                ManifestList.append("<td align=left>" + rset.getString(7) + "</td>\n");//TestName
                ManifestList.append("<td align=left>" + rset.getString(10) + "</td>\n");//Status
                ManifestList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-5\" onclick=\"UpdateInfoPatient(" + rset.getInt(5) + ")\">View</button></td>\n");
                ManifestList.append("<td align=left>" + rset.getString(11) + "</td>\n");//Status
                ManifestList.append("</tr>\n");
            }
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ManifestList", ManifestList.toString());
            Parser.SetField("UserId", UserId);
            Parser.SetField("Filter", Filter);
            Parser.SetField("Defaultlocation", Defaultlocation);
            Parser.SetField("locationList",locationList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/ManifestInput.html");

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in LabManifests ** (GetInput )", servletContext, e, "LabManifests", "GetInput", conn);
            Services.DumException("LabManifests", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "LabManifests");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

        }
    }

    void SaveManifest(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, HttpServletResponse response, final String UserId, final String Database, final int ClientId, UtilityHelper helper, String directoryName) throws ServletException, IOException {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
       // String[] selectedManifest = new String[0];
        String Defaultlocation = request.getParameter("Defaultlocation");
        int PatRegIdx = 0;
        String CurrDate="";
        try {
            UUID uuid = UUID.randomUUID();

            int seqNo = 0;
            try {
                Query = "Select IFNULL(MAX(Convert(Substring(ManifestNum,12,9),UNSIGNED INTEGER)),0) + 1 ,DATE_FORMAT(NOW(),'%Y%m%d') " +
                        "from "+Database+".ManifestsMaster ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    seqNo = rset.getInt(1);
                CurrDate=rset.getString(2);
                rset.close();
                stmt.close();
            } catch (Exception e) {
//                out.println("Error 3- :" + e.getMessage());
            }

            String manifestNumber = ClientId+"M-"+CurrDate+" - " + seqNo;
            UtilityHelper utilityHelper = new UtilityHelper();
            String userIP = utilityHelper.getClientIp(request);
           // selectedManifest = request.getParameter("manifestSelected").trim().split(",");
            PreparedStatement MainReceipt = conn.prepareStatement(
                    "INSERT INTO "+Database+".ManifestsMaster" +
                            " (ManifestNum, UID, Status, CreatedDate, CreatedBy,UserIP,isGeneratedPDF,LocationIdx)" +
                            " VALUES (?,?,0,NOW(),?,?,55,?) ");
            MainReceipt.setString(1, manifestNumber);
            MainReceipt.setString(2, String.valueOf(uuid));
            MainReceipt.setString(3, UserId);
            MainReceipt.setString(4, userIP);
            MainReceipt.setString(5, Defaultlocation);

            MainReceipt.executeUpdate();
            MainReceipt.close();

            int manifestIdx = 0;
            try {
                Query = "Select max(Id) from "+Database+".ManifestsMaster";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    manifestIdx = rset.getInt(1);
                rset.close();
                stmt.close();
            } catch (Exception e) {
//                out.println("Error 3- :" + e.getMessage());
            }

            int se=0;

            Enumeration keys = request.getParameterNames();
            while (keys.hasMoreElements() )
            {
               String key = (String)keys.nextElement();

               System.out.println(key);

               //To retrieve a single value

               if(key.startsWith("mList_"))
               {
            	 key=key.replace("mList_", "");
               String value = request.getParameter(key);
               System.out.println("adding"+ value);


               System.out.println("SELECTED MANIFEST : " + key + " <br> ");
               try {
                   Query = "SELECT Id FROM "+Database+".PatientReg " +
                           "WHERE Id = " +key;
                   stmt = conn.createStatement();
                   rset = stmt.executeQuery(Query);
                   if (rset.next())
                       PatRegIdx = rset.getInt(1);
                   stmt.close();
                   rset.close();
               } catch (SQLException e) {
                   e.printStackTrace();
               }

               MainReceipt = conn.prepareStatement(
                       " INSERT INTO "+Database+".ManifestDetails " +
                               "(OrderId, ManifestIdx, CreatedDate, Status,PatRegIdx) " +
                               "VALUES (?,?,NOW(),0,?) ");
               MainReceipt.setInt(1, Integer.parseInt(key));
               MainReceipt.setInt(2, manifestIdx);
               MainReceipt.setInt(3, PatRegIdx);
               MainReceipt.executeUpdate();
               MainReceipt.close();


               MainReceipt = conn.prepareStatement(
                       " UPDATE "+Database+".TestOrder SET StageIdx = 1 " +
                               "WHERE roverlab.TestOrder.PatRegIdx = ?");
               MainReceipt.setInt(1, PatRegIdx);
               MainReceipt.executeUpdate();
               MainReceipt.close();

               PatRegIdx = 0;


               }


               // If the same key has multiple values (check boxes)
              /* String[] valueArray = request.getParameterValues(key);

               for(int i = 0; i > valueArray.length; i++){
               System.out.println("VALUE ARRAY" + valueArray[i]);
               }*/
            }

           /* for (int i = 0; i < selectedManifest.length; i++) {
                if (selectedManifest[i].equals("on"))
                    continue;
                else {
                         }
            }*/

            System.out.println(manifestIdx);
            manifestPDF(request, out, conn, servletContext, response, UserId, Database, ClientId, directoryName,manifestIdx);

        } catch (Exception e) {
            out.println("0");
            e.printStackTrace();
        }
    }


    void manifestPDF(HttpServletRequest request, PrintWriter out, Connection conn,
                     ServletContext servletContext, HttpServletResponse response, String UserId,
                     String Database, int ClientId, String DirectoryName,int manifestid) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        String Query1 = "";
        int PatientRegId = 0;
        String DateTime = "";
        String Date = "";
        String Time = "";
        try {
            Query = "select date_format(now(),'%Y%m%d%H%i%s'), " +
                    "DATE_FORMAT(now(), '%m/%d/%Y'), DATE_FORMAT(now(), '%T')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DateTime = rset.getString(1);
                Date = rset.getString(2);
                Time = rset.getString(3);
            }
            rset.close();
            stmt.close();

            String inputFilePath = null;
            inputFilePath = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/" + DirectoryName + "/manifestFormat.pdf";
//            System.out.println("INPUT PATH " + inputFilePath);
            final String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/GeneratedFiles/GeneratedManifest_" + DateTime + ".pdf";
            final String outputFilePath2 = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/GeneratedFiles/GeneratedManifest_" + DateTime + ".png";
//            System.out.println("outputFilePath " + outputFilePath);
            final OutputStream fos = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            float yAxis = 650f;
            String charset = "UTF-8";

            HashMap<String, String>	 loc_liscode= new HashMap<String, String>();
            loc_liscode=LabManifestsRetrieve.listoflocation(conn, Database);

            if(request.getParameter("manifestIdx")!=null)
            {
            	manifestid=Integer.parseInt(request.getParameter("manifestIdx"));
            }

            Query = "SELECT Id,ManifestNum,UID,LocationIdx,CreatedDate,CreatedBy " +
                    "FROM "+Database+".ManifestsMaster " +
                    "WHERE id= "+manifestid;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                String url = rset.getString(2);
                String QrodeFilePath = QRcodeV2.generateQRcode_v1(url, outputFilePath2, charset, 80, 120);
                type1_02(pdfStamper, 1, QrodeFilePath, 250.0f, 710.0f);
                type1_01(pdfStamper, 1, url, 270.0F, 705.0F);
                type1_01(pdfStamper, 1, "Location "+loc_liscode.get(rset.getString(4)), 30.0F, 710.0F);
                type1_01(pdfStamper, 1, "Created Date: "+rset.getString(5), 450.0F, 710.0F);
                type1_01(pdfStamper, 1, "Created By: "+rset.getString(6), 450.0F, 700.0F);


                Query1 = "SELECT c.OrderNum, b.MRN," +
                        " CONCAT(IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,''))," +
                        " d.Status,e.Location, DATE_FORMAT(b.DOB,'%m/%d/%Y') " +
                        " FROM "+Database+".ManifestDetails a " +
                        " INNER JOIN "+Database+".PatientReg b ON a.PatRegIdx = b.ID " +
                        " INNER JOIN "+Database+".TestOrder c ON a.OrderId = c.Id " +
                        " INNER JOIN "+Database+".ListofStages d ON c.StageIdx = d.Id " +
                        " INNER JOIN "+Database+".Locations e ON b.TestingLocation = e.Id " +
                        " WHERE a.ManifestIdx = " + rset.getInt(1);
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                while (rset1.next()) {
//                    System.out.println("Y-Axis --> " + yAxis);
                    type1_01(pdfStamper, 1, rset1.getString(1), 35.0f, yAxis); //ORDER ID
//                    type1_01(pdfStamper, 1, rset.getString(2), 100.0f, yAxis);//Manifest
                    type1_01(pdfStamper, 1, rset1.getString(2), 100.0f, yAxis);//MRN
                    type1_01(pdfStamper, 1, rset1.getString(3), 155.0f, yAxis);//PatName
                    type1_01(pdfStamper, 1, rset1.getString(6), 250.0f, yAxis);//DOB
                    type1_01(pdfStamper, 1, rset1.getString(4), 320.0f, yAxis);//Stage
                    type1_01(pdfStamper, 1, rset1.getString(5), 405.0f, yAxis);//Location
                    yAxis = yAxis - 15;
                }
                rset1.close();
                stmt1.close();

                PreparedStatement MainReceipt = conn.prepareStatement(
                        " UPDATE "+Database+".ManifestsMaster SET isGeneratedPDF = 0,isRetrieved=1 " +
                                "WHERE roverlab.ManifestsMaster.Id = ?");
                MainReceipt.setInt(1, rset.getInt(1));
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }
            rset.close();
            stmt.close();

            pdfStamper.close();
            final File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=GeneratedManifest_" + DateTime + ".pdf");
            response.setContentLength((int) pdfFile.length());
            final FileInputStream fileInputStream = new FileInputStream(pdfFile);
            final OutputStream responseOutputStream = response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void type1_01(PdfStamper stamper, int pageno, String Data, Float x, Float y) throws DocumentException, IOException {

        PdfContentByte pdfContentByte = stamper.getOverContent(pageno);
        pdfContentByte.beginText();
        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
        pdfContentByte.setColorFill(BaseColor.BLACK);
        pdfContentByte.setTextMatrix(x, y);
        pdfContentByte.showText(Data);
        pdfContentByte.endText();

    }

    public void type1_02(PdfStamper stamper, int pageno, String Data, Float x, Float y) throws DocumentException, IOException {
        Image image = Image.getInstance(Data);
        PdfContentByte pdfContentByte = stamper.getOverContent(pageno);
        image.setAbsolutePosition(x, y);
        pdfContentByte.addImage(image);

    }
}
