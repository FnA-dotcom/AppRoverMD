// 
// Decompiled by Procyon v0.5.36
// 

package md;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
public class MergePdf extends HttpServlet {
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.HandleRequest(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.HandleRequest(request, response);
    }

    public void HandleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        String Action = null;
        final StringBuffer Response = new StringBuffer();
        final PrintWriter out = new PrintWriter(response.getOutputStream());
        String Database = "";
        ResultSet rset = null;
        Statement stmt = null;
        int ClientId = 0;
        String UserId = "";
        String Query = "";
        final String Path1 = "";
        final String Path2 = "";
        String MRN = "";
        ServletContext context = null;
        try {
            context = this.getServletContext();
            final String constring = Services.ConnString(this.getServletContext(), 1);
            conn = Services.GetConnection(this.getServletContext(), 1);
            if (conn == null) {
                out.println("Unable to connect with Database...1");
                out.println(constring);
                out.flush();
                out.close();
                return;
            }
            final String mysql_server = context.getInitParameter("mysql_server");
            final String mysql_dbuser = "oe_2";
            final String mysqlusr = context.getInitParameter("mysqlusr");
            final String mysqlpwd = context.getInitParameter("mysqlpwd");
            UserId = Services.GetCookie("UserId", request);
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://" + mysql_server + "/" + mysql_dbuser + "?user=" + mysqlusr + "&password=" + mysqlpwd + "");
            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientId = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select dbname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception excp) {
            conn = null;
            System.out.println("Exception excp conn: " + excp.getMessage());
        }
        try {
            if (request.getParameter("ActionID") == null) {
                Action = "Home";
                return;
            }
            Action = request.getParameter("ActionID").trim();
            if (Action.compareTo("GETINPUT") == 0) {
                this.GETINPUT(request, response, out, conn, Database, Path1, Path2, ClientId, MRN);
            }
        } catch (Exception e) {
            out.println("Exception in Main ... ");
            out.flush();
            out.close();
            return;
        }
        out.flush();
        out.close();
    }

    public void GETINPUT(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final String Database, final String Path1, final String Path2, int ClientId, String MRN) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String DirectoryName = "";
            Query = "Select DirectoryName from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DirectoryName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            final File file1 = new File(Path1);
            final PDDocument doc1 = PDDocument.load(file1);
            final File file2 = new File(Path2);
            final PDDocument doc2 = PDDocument.load(file2);
            final PDFMergerUtility PDFmerger = new PDFMergerUtility();
            PDFmerger.setDestinationFileName("/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/Result_" + ClientId + "_" + MRN + ".pdf");
            PDFmerger.addSource(file1);
            PDFmerger.addSource(file2);
            PDFmerger.mergeDocuments();
//            System.out.println("Documents merged Abid");
//            System.out.println("Path1: "+Path1);
//            System.out.println("Path2: "+Path2);
            doc1.close();
            doc2.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }
}
