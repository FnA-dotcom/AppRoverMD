//
// Decompiled by Procyon v0.5.36
//

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

@SuppressWarnings("Duplicates")
public class PDFtoImages extends HttpServlet {
    private Connection conn = null;

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
        String UserId = "";
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();

        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        ResultSet rset = null;
        Statement stmt = null;
        int ClientId = 0;
        String Database = "";
        UserId = "";
        String Source = "";
        String Dest = "";
        String Query = "";
        try {
            UserId = Services.GetCookie("UserId", request);
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
        } catch (Exception ex) {
        }
        if (ActionID.equals("GetValues")) {
            this.GetValues(request, out, conn, Database, ClientId, Source, Dest);
        }
        try {
            conn.close();
        } catch (Exception ex2) {
        }
        out.flush();
        out.close();
    }

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        String Source = "";
        String Dest = "";
        String DirectoryName = "";
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
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

            if (ActionID.equals("GetValues")) {
                this.GetValues(request, out, conn, DatabaseName, FacilityIndex, Source, Dest);
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

    public HashMap<Integer, String> GetValues(HttpServletRequest request, PrintWriter out, Connection conn, String Database, int ClientId, String Source, String Dest) {

        HashMap<Integer, String> images_Map = new HashMap<Integer, String>();
        try {
            File sourceFile = new File(Source);
            File destinationFile = new File(Dest);

            if (sourceFile.exists()) {
                System.out.println("Images copied to Folder Location: " + destinationFile.getAbsolutePath());
                PDDocument document = PDDocument.load(sourceFile);
                PDFRenderer pdfRenderer = new PDFRenderer(document);

                int numberOfPages = document.getNumberOfPages();
                System.out.println("Total files to be converting -> " + numberOfPages);

                String fileName = sourceFile.getName().replace(".pdf", "");
                String fileExtension = "png";
                /*
                 * 600 dpi give good image clarity but size of each image is 2x times of 300 dpi.
                 * Ex:  1. For 300dpi 04-Request-Headers_2.png expected size is 797 KB
                 *      2. For 600dpi 04-Request-Headers_2.png expected size is 2.42 MB
                 */
                int dpi = 200;// use less dpi for to save more space in hard disk. For professional usage you can use more than 300dpi

                /*Query = "CREATE TABLE "+Database+".tmpTableImages (\n" +
                        "  `Id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                        "  `ImagePathName` text,\n" +
                        "  PRIMARY KEY (`Id`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();*/


                String ServerName = "";
                InetAddress ip = InetAddress.getLocalHost();
                String hostname = ip.getHostName();


                //front-rovermd-01 app1
                //dev-rover-01 dev1
                //front2 app
                switch (hostname) {
                    case "front-rovermd-01":
                        ServerName = "app1";
                        break;
                    case "dev-rover-01":
                        ServerName = "dev1";
                        break;
                    case "front2.rovermd.com":
                        ServerName = "app";
                        break;
                }

                for (int i = 0; i < numberOfPages; ++i) {
                    File outPutFile = new File(Dest + fileName + "_" + (i + 1) + "." + fileExtension);
                    BufferedImage bImage = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);
                    ImageIO.write(bImage, fileExtension, outPutFile);

                    images_Map.put(i, " \"https://" + ServerName + ".rovermd.com:8443/md/tmpImages/" + fileName + "_" + (i + 1) + "." + fileExtension + "\" ");
                    /*Query = "Insert into "+Database+".tmpTableImages (ImagePathName) values ('"+"/md/tmpImages/" + fileName +"_"+ (i+1) +"."+ fileExtension+"')";
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();*/
                }

                document.close();
                System.out.println("Converted Images are saved at -> " + destinationFile.getAbsolutePath());



                /*Query = "Select ImagePathName from "+Database+".tmpTableImages";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    ImagesNamePath += "\""+rset.getString(1) +"\""+ ",\n";
                }
                rset.close();
                stmt.close();

                Query = "DROP TABLE IF EXISTS "+Database+".tmpTableImages";
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

                System.out.println(ImagesNamePath);*/
//                Runtime.getRuntime().exec( "chmod -R 7777 "+Dest );
//                conn.close();

            } else {
                System.err.println(sourceFile.getName() + " File not exists");
            }

        } catch (Exception e) {
            out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
        return images_Map;
    }
}
