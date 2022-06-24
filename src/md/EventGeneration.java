package md;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.text.Format;
import java.text.SimpleDateFormat;

@SuppressWarnings("Duplicates")
public class EventGeneration extends HttpServlet {


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.Services(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.Services(request, response);
    }

    private void Services(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
//        PrintWriter out = new PrintWriter(response.getOutputStream());
        //content type must be set to text/event-stream
        response.setContentType("text/event-stream, charset=UTF-8");
        //cache must be set to no-cache
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expires", "-1"); // Proxies.
//        //encoding is set to UTF-8
//        request.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            String ActionID = request.getParameter("Response");

            if (ActionID.equals("GetAction")) {
                ResponseDisplay(request, writer, conn);
            }
        } catch (Exception var15) {
            System.out.println("Exception in main... " + var15.getMessage());
            System.out.flush();
            System.out.close();
            return;
        }

        System.out.flush();
        System.out.close();
    }

    private void ResponseDisplay(HttpServletRequest request, PrintWriter writer, Connection conn) {

        try {
            final HttpSession session = request.getSession(false);
            final String SessionId = session.getId();
            final long SessionInactive = session.getLastAccessedTime();

            Date d = new Date(SessionInactive);
            Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");

            writer.write("data: " + SessionId + "|" + format.format(d));
//            writer.write("data: " + Online + "|" + LogOut + "|" + Offline + "|" + UnAssignedJobs + "|" + AssignedJobs + "|" + TotalTech + "|" + TotalTechStatus.toString() + "\n\n");
        } catch (Exception Ex) {
            writer.write("ERROR " + Ex.getMessage());
            System.out.println("ERROR SHOWN " + Ex.getMessage());
            System.out.flush();
            System.out.close();
        } finally {
            writer.flush();
            writer.close();
        }
    }
}
