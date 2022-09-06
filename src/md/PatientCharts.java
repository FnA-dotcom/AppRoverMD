//
// Decompiled by Procyon v0.5.36
//

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import com.itextpdf.text.pdf.PdfWriter;
@SuppressWarnings("Duplicates")
public class PatientCharts extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
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

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId;
        int FacilityIndex;
        String DatabaseName;
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

            try {
/*                boolean ValidSession = FacilityLogin.checkSession(out, request, context, response);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    return;
                }*/
                if (UserId.equals("")) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            } catch (Exception excp) {
                conn = null;
                out.println("Exception excp conn: " + excp.getMessage());
            }

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "GetChartList":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Charts List", "View Charts List", FacilityIndex);
                    this.GetChartList(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "download":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Download Chart", "Click on download Button", FacilityIndex);
                    this.download(request, response, out, conn);
                    break;
                case "download_direct":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View Chart Option", "View Charts in IFRAME", FacilityIndex);
                    this.download_direct(request, response, out, conn);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
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


    private void GetChartList(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int SNo = 1;
        String MRN = request.getParameter("MRN").trim();
        int Clientdirectory = Integer.parseInt(request.getParameter("clientdirectory").trim());
        StringBuffer ChartList = new StringBuffer();
        StringBuffer ShowCharts = new StringBuffer();
        try {

            Query = "Select target,filename,Id from oe.filelogs_sftp where MRN = '" + MRN + "' and clientdirectory = " + Clientdirectory;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ChartList.append("<tr><td align=left>" + SNo + "</td>\n");
                ChartList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                ChartList.append("<td><a href=https://rovermd.com:8443/md/md.PatientCharts?ActionID=download&fname=" + rset.getString(2) + "&path=" + rset.getString(1) + "&indexptr=" + rset.getInt(3) + " target='_blank'>download</a></td>");
                ShowCharts.append("<div class=\"col-md-6\">\n<iframe src=\"https://rovermd.com:8443/md/md.PatientCharts?ActionID=download_direct&path=" + rset.getString(1) + "&indexptr=" + rset.getInt(3) + "&fname=" + rset.getString(2) + "&embedded=true\" frameborder=\"0\"  height=\"900px\" width=\"800\">\n" + "</iframe>\n" + "</div>");
                SNo++;
            }
            rset.close();
            stmt.close();

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ChartList", String.valueOf(ChartList));
            Parser.SetField("ShowCharts", String.valueOf(ShowCharts));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ChartList.html");

        } catch (Exception var11) {
            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
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

    public void download(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        final String FileName = request.getParameter("fname");
        final String path = request.getParameter("path");
        final String indexptr = request.getParameter("indexptr");
        String RecordingPath = "";
        final int n = FileName.length();
        final char last = FileName.charAt(n - 1);
        if (path.endsWith("/")) {
            RecordingPath = path + FileName;
        } else {
            RecordingPath = path + "/" + FileName;
        }
        String userindex = Services.GetCookie("userindex", request).trim();
//    System.out.println("userindex: "+userindex);
        try {
            String[] mrnArr = FileName.split("\\_");
            String mrn = mrnArr[1];
//      System.out.println(mrn + " :MRN");
            String note = "Download pdf charts";
            createnote(Integer.parseInt(userindex), note, Integer.parseInt(indexptr), conn, mrn);
            logging(Integer.parseInt(userindex), 2, Integer.parseInt(indexptr), conn);
            markuser(Integer.parseInt(userindex), 2, Integer.parseInt(indexptr), conn);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "Inline;filename=" + FileName);
            final FileInputStream fin = new FileInputStream(RecordingPath);
            final byte[] content = new byte[fin.available()];
            fin.read(content);
            fin.close();
            final OutputStream os = (OutputStream) response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }

    public void download_direct(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        final String FileName = request.getParameter("fname");
        final String path = request.getParameter("path");
        final String indexptr = request.getParameter("indexptr");
        final String RecordingPath = path + FileName;
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "Inline;filename=" + FileName);
            final FileInputStream fin = new FileInputStream(RecordingPath);
            final byte[] content = new byte[fin.available()];
            fin.read(content);
            fin.close();
            final OutputStream os = (OutputStream) response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }

    public static String logging(final int userindex, final int filestatus, final int indexptr, final Connection conn) {
        Statement hstmt = null;
        final ResultSet hrset = null;
        String Query = "";
        try {
            hstmt = conn.createStatement();
            Query = " insert into oe.fileactivity(fileindex,created,userindex,filestatus)  values('" + indexptr + "',now()," + userindex + ",'" + filestatus + "') ";
            hstmt.execute(Query);
        } catch (Exception ex) {
        }
        return null;
    }

    public static String createnote(final int userindex, final String note, final int claimid, final Connection conn, final String mrn) {
        Statement hstmt = null;
        final ResultSet hrset = null;
        String Query = "";
        try {
            hstmt = conn.createStatement();
            Query = " insert into oe.claim_note(note,userindex,createddate,claimid, mrn)  values('" + note + "','" + userindex + "',now(), '" + claimid + "', '" + mrn + "') ";
            hstmt.execute(Query);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public static String markuser(final int userindex, final int filestatus, final int indexptr, final Connection conn) {
        Statement hstmt = null;
        final ResultSet hrset = null;
        String Query = "";
        try {
            hstmt = conn.createStatement();
            Query = " update  oe.filelogs_sftp set processby=" + userindex + " where processby=0 and id=" + indexptr;
            hstmt.execute(Query);
        } catch (Exception ex) {
        }
        return null;
    }
}
