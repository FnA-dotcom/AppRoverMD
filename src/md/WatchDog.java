

package md;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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

public class WatchDog extends HttpServlet {
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

        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();
//    conn = supp.getMysqlConn();
        try {
            ServletContext context = null;
            context = this.getServletContext();
            conn = supp.getMysqlConn(context);
            //response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            if (conn == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                conn.close();
                //response.getWriter().println("<html><body><p>400</p></body></html>");
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                conn.close();
                //response.getWriter().println("<html><body><p>OK</p></body></html>");
            }


        } catch (Exception e) {

        }
    }


}
