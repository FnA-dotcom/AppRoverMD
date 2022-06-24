package md;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class hl7rm extends HttpServlet {


    public void init(ServletConfig config)
            throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void HandleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String Action = null;
        String clientid = "0";
        Action = request.getParameter("Action");
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text");
        ServletContext context = null;

        try {
            if (Action.compareTo("hl7request") == 0) {
                hl7request(request, out, clientid, context);
            }
            if (Action.compareTo("hl7check") == 0) {
                hl7check(request, response, out);
            }
        } catch (Exception localException) {
        } finally {
            out.flush();
            out.close();
        }
    }

    public void hl7request(HttpServletRequest request, PrintWriter out, String clientid, ServletContext context) {
        try {
            //String clientid = request.getParameter("clientid").trim();
//            ServletContext context = getServletContext();
            out.println(context.toString());
            Hashtable datasync = null;
            if (context.getAttribute("tm01") == null) {
                datasync = new Hashtable();
                datasync.put(clientid, "1");
                context.setAttribute("tm01", datasync);
            } else {
                datasync = (Hashtable) context.getAttribute("tm01");
                datasync.put(clientid, "1");
                context.setAttribute("tm01", datasync);
            }
            Set set = datasync.entrySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();

                out.println(entry.getKey() + ":" + entry.getValue() + "\n");

            }

        } catch (Exception e) {
            Services.DumException("UploadExcelNew", "Step1", request, e);
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
//            out.println("Error Occured. Contact System Administrator... " + e.getMessage());
//            out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()>");
            return;
        }
    }

    public void hl7check(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            PatientInfo patientInfo = new PatientInfo();
            ServletContext context = patientInfo.context;
            String clientid = request.getParameter("clientid").trim();


            Hashtable datasync = (Hashtable) context.getAttribute("tm01");

            Set set = datasync.entrySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                //String Valuew=entry.getValue().toString().startsWith();
                if (entry.getValue().toString().startsWith("<!doctype")) {

                } else {

                    if (entry.getKey().equals(clientid)) {
                        out.println(entry.getValue());
                        datasync = (Hashtable) context.getAttribute("tm01");
                        datasync.put(clientid, 0);
                        context.setAttribute("tm01", datasync);
                    }
                }
             /*if(entry.getKey().equals(id)){
             out.println(entry.getKey() + ":" + entry.getValue());
             }*/
            }
            // datasync.clear();
            context.setAttribute("tm01", datasync);


        } catch (Exception e) {
            Services.DumException("UploadExcelNew", "Step1", request, e);
            out.println("Error Occured. Contact System Administrator... " + e.getMessage());
            out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()>");
            return;
        }
    }


}