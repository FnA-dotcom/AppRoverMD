//
// Decompiled by Procyon v0.5.36
//

package orange_2;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

//import com.itextpdf.text.pdf.PdfWriter;

public class GenerateBarCode extends HttpServlet {
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
        ResultSet rset = null;
        Statement stmt = null;
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        String Database = "";
        String Query = "";
        int ClientId = 0;
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();
//    conn = supp.getMysqlConn();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);

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
//      if(ClientId == 8){
//        Database = "oe_2";
//      }else if(ClientId == 9){
//        Database = "victoria";
//      }else if(ClientId == 10){
//        Database = "oddasa";
//      }

            String MRN = "";

            if (ActionID.equals("GetBarCode")) {
                this.GetBarCode(request, out, conn, context, UserId, Database, ClientId, MRN);
            }
            try {
                conn.close();
            } catch (Exception ex) {
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }

    }

    String GetBarCode(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId, String MRN) {
        Statement stmt = null;
        ResultSet rset = null;
//    if(MRN.length() == 6) {
//      MRN = "000000" + MRN;
//    }else if(MRN.length() == 4){
//      MRN = "00000000" + MRN;
//    }else if(MRN.length() == 8){
//      MRN = "0000" + MRN;
//    }
        int SNo = 1;
        String filePath = null;
        try {
            //String MRN = "000000310023";
            filePath = "/opt/apache-tomcat-7.0.65/webapps/orange_2/BarCode/BarCode" + MRN + ".png";

            QRCode BarCode = new QRCode();
            String Success = BarCode.genbarcode(filePath, MRN, 10, 20);
            if (Success == "1") {
                out.println("BarCode Generated Successfully");
            } else {
                out.println("Error in generating Bar Code");
            }
        } catch (Exception var11) {
//      Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
        return filePath;
    }

}
