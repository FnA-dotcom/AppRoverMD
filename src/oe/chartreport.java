package oe;

import Parsehtm.Parsehtm;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import org.apache.xmlbeans.impl.util.Base64;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
import org.apache.commons.codec.binary.Base64;*/

@SuppressWarnings("Duplicates")
public class chartreport
        extends HttpServlet {
    public String host = "http://ourenergyllc.com";

    public static String getOnlyDigits(String s) {
        Pattern pattern = Pattern.compile("[^0-9.]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }

    public static String getclientname(HashMap clientlist, int clientIndex) {
        String clientname = null;
        Set set = clientlist.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            // out.println(entry.getKey() + ":" + entry.getValue()+"\n");
            if (entry.getKey().equals(clientIndex)) {
                // out.println(entry.getKey() + ":" + entry.getValue());
                clientname = entry.getValue().toString();
            } /*
             * else{
             *
             * clientname=Integer.toString(clientIndex); }
             */
        }

        return clientname;
    }

    public static String filestatuslist(HashMap clientlist, int clientIndex, int indexptr) {
        // String clientname=null;
        StringBuffer listofstatus = new StringBuffer();
        listofstatus.append("<select id=\"StatusAction\" onChange=\"updatestatus(" + indexptr + "\\" + clientlist.toString() + ")\">");


        for (int i = 0; i < clientlist.size(); i++) {
            if (i == indexptr) {
                listofstatus.append("<option  value=" + i + " selected >" + clientlist.get(i) + "</option>");
            } else {
                listofstatus.append("<option  value=" + i + "  >" + clientlist.get(i) + "</option>");
            }
        }


        return listofstatus.toString();
    }

    public static HashMap<Integer, String> Clientlist(String aa, Connection Conn) {

        HashMap<Integer, String> hm = new HashMap<Integer, String>();


        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,name from clients where status=0 ";//and Id not in (9,10)" ;
            hstmt = Conn.createStatement();

            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                id = hrset.getInt(1);
                name = hrset.getString(2);
                hm.put(id, name);

            }


            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception e) {
            return hm;
        }
    }

    public static HashMap<Integer, String> Insurancelist(String aa, Connection Conn) {

        HashMap<Integer, String> hm = new HashMap<Integer, String>();


        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,Name from Insurancelist where status=0";
            hstmt = Conn.createStatement();

            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                id = hrset.getInt(1);
                name = hrset.getString(2);
                hm.put(id, name);

            }


            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception e) {
            return hm;
        }
    }

    public static HashMap<Integer, String> claim_status_list(String aa, Connection Conn) {

        HashMap<Integer, String> hm = new HashMap<Integer, String>();


        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,descname from claim_status_list where status=0";
            hstmt = Conn.createStatement();

            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                id = hrset.getInt(1);
                name = hrset.getString(2);
                hm.put(id, name);

            }


            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception e) {
            return hm;
        }
    }

    public static HashMap<Integer, String> claim_ppt_list(String aa, Connection Conn) {

        HashMap<Integer, String> hm = new HashMap<Integer, String>();


        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,descname from claim_ppt_list where status=0";
            hstmt = Conn.createStatement();

            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                id = hrset.getInt(1);
                name = hrset.getString(2);
                hm.put(id, name);

            }


            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception e) {
            return hm;
        }
    }

    public static HashMap<Integer, String> Userlist(String aa, Connection Conn) {

        HashMap<Integer, String> hm = new HashMap<Integer, String>();


        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select indexptr,username from sysusers where enabled='Y'";
            hstmt = Conn.createStatement();

            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                id = hrset.getInt(1);
                name = hrset.getString(2);
                hm.put(id, name);

            }


            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception e) {
            return hm;
        }
    }

    public static HashMap<Integer, String> statuslist(String aa, Connection Conn) {

        HashMap<Integer, String> hm = new HashMap<Integer, String>();


        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,name from filestatuslist where status='0'";
            hstmt = Conn.createStatement();

            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                id = hrset.getInt(1);
                name = hrset.getString(2);
                hm.put(id, name);

            }


            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception e) {
            return hm;
        }
    }

    public static String logging(int userindex, int filestatus, int indexptr, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        try {
            hstmt = conn.createStatement();
            Query = " insert into fileactivity(fileindex,created,userindex,filestatus) " +
                    " values('" + indexptr + "',now()," + userindex + ",'" + filestatus + "') ";
            hstmt.execute(Query);
        } catch (Exception ee) {


        }

        return null;
    }

    public static String createnote(int userindex, String note, int indexptr, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        try {
            hstmt = conn.createStatement();
            Query = " insert into claim_note(note,userindex,createddate) " +
                    " values('" + note + "','" + userindex + "',now()) ";
            hstmt.execute(Query);
        } catch (Exception ee) {


        }

        return null;
    }

    public static String markuser(int userindex, int filestatus, int indexptr, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        try {
            hstmt = conn.createStatement();
            Query = " update  filelogs_sftp set processby=" + userindex + " where processby=0 and id=" + indexptr;
            hstmt.execute(Query);
        } catch (Exception ee) {


        }

        return null;
    }

    public static boolean CheckDates(String FromDate, String ToDate, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        try {
            Query = " select unix_timestamp('" + FromDate + "') - unix_timestamp('" + ToDate + "') ";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            hrset.next();
            boolean Valid = hrset.getInt(1) <= 0;
            hrset.close();
            hstmt.close();
            return Valid;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getcount(String cid, String day, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        String found = "0";
        try {
            Query = "select count(*) from IVRLOG where customerid='" + cid + "' and parsedmsg in ('Sucess','Success') and substr(entrydate,1,10)='" + day + "'";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            hrset.next();
            found = hrset.getString(1);
            hrset.close();
            hstmt.close();
            return found;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String attemps(String cid, String day, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        String found = "0";
        try {
            Query = "select count(*) from IVRLOG where customerid='" + cid + "' and parsedmsg='failed'  and substr(entrydate,1,10)='" + day + "'";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            hrset.next();
            found = hrset.getString(1);
            hrset.close();
            hstmt.close();
            return found;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

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
        Connection conn = null;
        Connection conn2 = null;
        String Action = null;
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String Stage = "";

        try {


            if ((request.getParameter("Action") == null) && (request.getContentType().startsWith("multipart/form-data"))) {
                Action = "Step2";
            } else {
                Action = request.getParameter("Action");
            }
            if (Action.compareTo("download_direct") != 0) {
                boolean ValidSession = Services.checkSession(out, request);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    conn.close();
                    return;
                }
            }
            String UserId = Services.GetCookie("UserId", request);
            if (UserId == "") {
                out.println("Your session has been expired, please login again.");
                out.flush();
                return;
            }
            // DriverManager.registerDriver(new Driver());
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            //conn = DriverManager.getConnection("jdbc:mysql://132.148.155.201/oe?user=cdrasterisk444&password=cdrasterisk999");
            Stage = "1";
            conn = Services.GetConnection(getServletContext(), 1);
            conn2 = Services.GetConnection(getServletContext(), 1);
            if (conn == null) {
                out.println("Unable to get DB connection...");
                out.flush();
                return;
            }
            if (conn2 == null) {
                out.println("Unable to get DB connection...");
                out.flush();
                return;
            }
            if (Action.compareTo("showchartdetails") == 0) {
                showchartdetails_new(request, response, out, conn);
            } else if (Action.compareTo("showchartlogs") == 0) {
                showchartlog(request, response, out, conn);
            } else if (Action.compareTo("Addinfo") == 0) {
                Addinfo(request, response, out, conn);
            } else if (Action.compareTo("Addinfohl7") == 0) {
                Addinfohl7(request, response, out, conn);
            } else if (Action.compareTo("Addinfosave") == 0) {
                Addinfosave(request, response, out, conn);
            } else if (Action.compareTo("download_direct") == 0) {
                download_direct(request, response, out, conn);
            } else if (Action.compareTo("hl7chart") == 0) {
                hl7chart(request, response, out, conn);
            } else if (Action.compareTo("updatestatus") == 0) {
                updatestatus(request, response, out, conn);
            } else if (Action.compareTo("openpdf") == 0) {
                openpdf(request, response, out, conn);
            } else if (Action.compareTo("download") == 0) {
                download(request, response, out, conn);
            } else if (Action.compareTo("showclaimlogs") == 0) {
                showclaimlogs(request, response, out, conn);
            } else if (Action.compareTo("showclaimdetails") == 0) {
                showclaimdetails(request, response, out, conn, conn2);
            } else if (Action.compareTo("DashBoard") == 0) {
                DashBoard(request, response, out, conn, conn2);
            }

            //old
            else if (Action.compareTo("showivrdetails_error") == 0) {
                showivrdetails_error(request, response, out, conn);
            } else if (Action.compareTo("showivrdetails_balance") == 0) {
                showivrdetails_balance(request, response, out, conn);
            } else if (Action.compareTo("showchartlogs") == 0) {
                showchartlog(request, response, out, conn);
            } else if (Action.compareTo("showivrlogs_error") == 0) {
                showivrlogs_error(request, response, out, conn);
            } else if (Action.compareTo("showivrlogs_balance") == 0) {
                showivrlogs_balance(request, response, out, conn);
            } else if (Action.compareTo("showivrlogssum") == 0) {
                showivrlogssum(request, response, out, conn);
            } else if (Action.compareTo("showivrdetailssum") == 0) {
                showivrdetailssum(request, response, out, conn);
            } else {
                out.println(" Under Development ... " + Action);
            }
            Stage = "4";

            conn.close();

            out.flush();
            out.close();
        } catch (Exception e) {
            out.println("Exception in main: " + e.getMessage() + Stage);

            out.flush();
            out.close();
            return;
        }
        out.flush();
        out.close();
    }

    public void download1(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {


        String fileindex = request.getParameter("fname");

        String cid = "";

        String link = "";
        String Customername = "";
        String extdate = "";
        String dncid = "";
        String zipcode = "";
        String premise_id = "";
        String email = "";


        try {

            String stringUrl = "https://app.smartfile.com/api/2/path/data/shares/FED%20Accounts/Physicians-Premier-Bastrop/Charts/459_37259_201910191546_201910231416_.pdf?download=true&ui=1";
            //String stringUrl = "https://app.smartfile.com/api/2/activity/?format=json&action=Path%20Written&action=path%20created&timestamp_min="+aa+"&timestamp_max="+Endtime+"&page="+p;
            URL url = new URL(stringUrl);
            URLConnection uc = url.openConnection();
            System.out.println(stringUrl);
            uc.setRequestProperty("X-Requested-With", "Curl");
            uc.setRequestProperty("Content-Type", "application/json");
            String userpass = "username" + ":" + "password";
            String userCredentials = "DlYTlsOY249jBise60b9y4r0emCXqB:dpgwmrFiJJnPojMNnkS14wgKTJjSIo";

            String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
            uc.setRequestProperty("Authorization", basicAuth);
            uc.setRequestProperty("postman-token", "9da9db4f-8248-e387-0fdd-018c98cb6f92");
            uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
            //uc.setConnectTimeout(30000);
            //uc.setReadTimeout(30000);
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            out.println(in.toString());
            /*
             * String line_f; String strRet = ""; while ((line_f = in.readLine()) != null) {
             * strRet += line_f; } //System.out.println(strRet); out.println(fileAsString);
             */
            //InputStream is = new FileInputStream("/opt/logs/oe/paylog/"+fileindex);
            //BufferedReader buf = new BufferedReader(new InputStreamReader(is));

            String line = in.readLine();
            StringBuilder sb = new StringBuilder();

            while (line != null) {
                //  sb.append(line).append("\n");
                line = in.readLine();
            }

            String fileAsString = sb.toString();
            out.println(fileAsString);
            //System.out.println("Contents : " + fileAsString);


         /* Parsehtm Parser = new Parsehtm(request);
          Parser.SetField("cid", cid.toString());
          Parser.SetField("Customername", Customername.toString());
          Parser.SetField("email", email.toString());
          Parser.SetField("dncid", dncid.toString());
          Parser.SetField("extdate", extdate.toString());
          Parser.SetField("amount", link.toString());
          Parser.SetField("premise_id", premise_id.toString());
          Parser.SetField("link", fileAsString.toString());
          Parser.SetField("zipcode", zipcode.toString());
          Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/PastDue_web.html");
*/
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
        }
    }

    public void download(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        String FileName = request.getParameter("fname");
        String path = request.getParameter("path");
        String indexptr = request.getParameter("indexptr");

        int n = FileName.length();
        // Last character of a string
        char last = FileName.charAt(n - 1);

        String RecordingPath = path + "/" + FileName;
        String UserId = Services.GetCookie("UserId", request).trim();
        String userindex = Services.GetCookie("userindex", request).trim();


        try {

            logging(Integer.parseInt(userindex), 2, Integer.parseInt(indexptr), conn);
            markuser(Integer.parseInt(userindex), 2, Integer.parseInt(indexptr), conn);

            response.setContentType("application/pdf");
            // response.setContentType("audio/x-gsm");
            response.setHeader("Content-Disposition", "Inline;filename=" + FileName);

            FileInputStream fin = new FileInputStream(RecordingPath);
            byte[] content = new byte[fin.available()];
            fin.read(content);
            fin.close();
            OutputStream os = response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();


        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }

    public void download_direct(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        String FileName = request.getParameter("fname");
        String path = request.getParameter("path");
        String indexptr = request.getParameter("indexptr");
        String RecordingPath = path + FileName;
        //String UserId = Services.GetCookie("UserId", request).trim();
        //String userindex = Services.GetCookie("userindex", request).trim();


        try {

            //logging(Integer.parseInt(userindex),2, Integer.parseInt(indexptr) , conn);
            //markuser(Integer.parseInt(userindex),2, Integer.parseInt(indexptr), conn);

            response.setContentType("application/pdf");
            // response.setContentType("audio/x-gsm");
            response.setHeader("Content-Disposition", "Inline;filename=" + FileName);

            FileInputStream fin = new FileInputStream(RecordingPath);
            byte[] content = new byte[fin.available()];
            fin.read(content);
            fin.close();
            OutputStream os = response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
          /*File file = new File(RecordingPath);
          String mimeType = getServletContext().getMimeType(file.getName());
          response.setHeader("Content-Type", mimeType);
          response.setHeader("Content-Length", String.valueOf(file.length()));
          response.setHeader("Content-Disposition", "embed; filename=\"fileName.pdf\"");
          Files.copy(file.toPath(), response.getOutputStream());*/


        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }

    public void hl7chart(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        String FileName = request.getParameter("fname");
        String path = request.getParameter("path");
        String indexptr = request.getParameter("indexptr");
        String RecordingPath = path + FileName;
        //String UserId = Services.GetCookie("UserId", request).trim();
        //String userindex = Services.GetCookie("userindex", request).trim();


        try {

            //logging(Integer.parseInt(userindex),2, Integer.parseInt(indexptr) , conn);
            //markuser(Integer.parseInt(userindex),2, Integer.parseInt(indexptr), conn);


            String Filname = path + "" + FileName;
            InputStream is = new FileInputStream(Filname);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));

            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();

            while (line != null) {
                sb.append(line).append("\r\n");
                line = buf.readLine();
            }

            String fileAsString = sb.toString();

            PipeParser ourPipeParser = new PipeParser();
            ourPipeParser.setValidationContext(new NoValidation());

            Message hl7Message = ourPipeParser.parse(fileAsString.trim());

            Hl7dft4_clean.OBXfinal.delete(0, Hl7dft4_clean.OBXfinal.length());
            Hl7dft4_clean.PV1final.delete(0, Hl7dft4_clean.PV1final.length());
            Hl7dft4_clean.IN1final.delete(0, Hl7dft4_clean.IN1final.length());
            Hl7dft4_clean.PIDfinal.delete(0, Hl7dft4_clean.PIDfinal.length());
            Hl7dft4_clean.DGIfinal.delete(0, Hl7dft4_clean.DGIfinal.length());
            Hl7dft4_clean.FTIfinal.delete(0, Hl7dft4_clean.FTIfinal.length());
            Hl7dft4_clean.GT1final.delete(0, Hl7dft4_clean.GT1final.length());


            Hl7dft4_clean.extractValues(hl7Message);
              /*out.println(Hl7dft4_clean.PIDfinal);
              out.println(Hl7dft4_clean.PV1final);
              out.println(Hl7dft4_clean.IN1final);
              out.println(Hl7dft4_clean.FTIfinal);
              out.println(Hl7dft4_clean.DGIfinal);
              out.println(Hl7dft4_clean.OBXfinal);*/


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PIDfinal", Hl7dft4_clean.PIDfinal.toString());
            Parser.SetField("PV1final", Hl7dft4_clean.PV1final.toString());
            Parser.SetField("IN1final", Hl7dft4_clean.IN1final.toString());
            Parser.SetField("FTIfinal", Hl7dft4_clean.FTIfinal.toString());
            Parser.SetField("DGIfinal", Hl7dft4_clean.DGIfinal.toString());
            Parser.SetField("OBXfinal", Hl7dft4_clean.OBXfinal.toString());
            Parser.SetField("GT1final", Hl7dft4_clean.GT1final.toString());

            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/HL7chartReport.html");


        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }

    public void openpdf(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        // String file = request.getParameter("file");

        String FileName = request.getParameter("fname");
        String path = request.getParameter("path");
        String indexptr = request.getParameter("indexptr");
        String RecordingPath = path + FileName;
        String UserId = Services.GetCookie("UserId", request).trim();
        String userindex = Services.GetCookie("userindex", request).trim();


        try {

            //  logging(Integer.parseInt(userindex),2, Integer.parseInt(indexptr) , conn);
            // markuser(Integer.parseInt(userindex),2, Integer.parseInt(indexptr), conn);
            //out.println(file);
            String file = "http://54.80.137.178:83/oe/oe.chartreport?Action=download&fname=" + FileName + "&path=" + path + "&indexptr=" + indexptr;

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("file", file.toString());

            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/openpdf.html");


        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }

    public void showchartlog(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {

        StringBuffer Day = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Year = new StringBuffer();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer ClientList = new StringBuffer();

        try {
    	 /* Query = "select Id,name from clients where status=0";
          hstmt = conn.createStatement();
          hrset = hstmt.executeQuery(Query);
          ClientList.append("<option class=Inner value=\"-1\"> All </option>");
          while (hrset.next())
  			{
           ClientList.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(2) + "</option>");
  			}
          hrset.close();
          hstmt.close();*/
            ClientList.append("<option class=Inner value=\"-1\"> All </option>");
            HashMap<Integer, String> hm = new HashMap<Integer, String>();
            hm = Clientlist("a", conn);
            Set set = hm.entrySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                ClientList.append("<option class=Inner value=\"" + entry.getKey() + "\">" + entry.getValue().toString() + "</option>");


            }


            Services.GetCalendar(Day, Month, Year);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DayList", Day.toString());
            Parser.SetField("MonthList", Month.toString());
            Parser.SetField("ClientList", ClientList.toString());
            Parser.SetField("YearList", Year.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showchartlog.html");
        } catch (Exception e) {
            out.println("Unable to process the request...");
        }
    }

    public void Addinfo(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        StringBuffer Day = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Year = new StringBuffer();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String indexptr = request.getParameter("indexptr");
        String mrn = request.getParameter("mrn");
        String firstname = "";
        String lastname = "";
        String Acc = "";
        String dosdate = "";
        String filename = "";
        String target = "";
        String entrydate = "";
        String clientid = "";
        String ChargeMasterTableName = "";
        StringBuffer Insurancelist = new StringBuffer();
        StringBuffer claimstatuslist = new StringBuffer();
        StringBuffer claimpptlist = new StringBuffer();
        StringBuffer PatientStatus = new StringBuffer();
        StringBuffer DiagnosisCodesList = new StringBuffer();
        StringBuffer CPTCodesList = new StringBuffer();
        String userindex = Services.GetCookie("userindex", request).trim();
        String note = "";

        String Phone = "";
        String email = "";
        String address = "";
        String charges = "0";
        String claimamount = "0";
        String claimstatus = "0";
        String patientstatus = "0";
        String insurance = "0";
        String chiofcomplaint = "";
        String cmdref = "";
        String Remarks = "";
        String Mailed = "";
        String editby = "";
        String editentrydate = "";
        String aa = "0";

        try {
            note = "Open Add info and load pdf";
            createnote(Integer.parseInt(userindex), note, Integer.parseInt(indexptr), conn);
            logging(Integer.parseInt(userindex), 2, Integer.parseInt(indexptr), conn);
            markuser(Integer.parseInt(userindex), 2, Integer.parseInt(indexptr), conn);
            Query = "select Id,target,entrydate,clientdirectory,filename,acc,dosdate,epowertime,processed,processby,"
                    + "filestatus,liststatus,ifnull(firstname,'-'),ifnull(lastname,'-'),ifnull(mrn,'-') "
                    + "from filelogs_sftp where Id=" + indexptr;
            // out.println(Query);
            hstmt = conn.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                firstname = hrset.getString(13);
                lastname = hrset.getString(14);
                Acc = hrset.getString(6);
                dosdate = hrset.getString(7);
                entrydate = hrset.getString(3);
                target = hrset.getString(2);
                mrn = hrset.getString(15);
                filename = hrset.getString(5);
                clientid = hrset.getString(4);
            }
            hrset.close();
            hstmt.close();
            // Insurancelist.append("<option class=Inner value=\"-1\"> All </option>");
            // Insurancelist.append("<option class=Inner value=\"" + entry.getKey() + "\">" + entry.getValue().toString() + "</option>");

            Query = "select ifnull(phone,''),ifnull(email,''),ifnull(address,''),ifnull(claimamount,''),ifnull(charges,''),ifnull(dosdate,''),ifnull(entrydate,''),ifnull(claimstatus,''),ifnull(patientstatus,''),ifnull(insurance,''),"
                    + "ifnull(chiofcomplaint,''),ifnull(cmdref,''),ifnull(Remarks,''),ifnull(LMailed,''),ifnull(createddate,''),createdby from claim_info_master where claimid=" + indexptr;

            hstmt = conn.createStatement();
            //  out.println(Query);
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                Phone = hrset.getString(1);
                email = hrset.getString(2);
                aa = "1";
                address = hrset.getString(3);
                claimamount = hrset.getString(4);
                charges = hrset.getString(5);
                //dosdate = hrset.getString(6) ;
                editentrydate = hrset.getString(7);
                claimstatus = hrset.getString(8);
                patientstatus = hrset.getString(9);
                insurance = hrset.getString(10);
                chiofcomplaint = hrset.getString(11);
                cmdref = hrset.getString(12);
                Remarks = hrset.getString(13);
                Mailed = hrset.getString(14);
                editby = hrset.getString(16);

                aa = "2";
            }
            hrset.close();
            hstmt.close();

            HashMap<Integer, String> hm = new HashMap<Integer, String>();
            hm = Insurancelist("a", conn);
            Set set = hm.entrySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if (entry.getKey().equals(Integer.parseInt(insurance))) {
                    Insurancelist.append("<option class=Inner value=\"" + entry.getKey() + "\" selected >" + entry.getValue().toString() + "</option>");
                } else {
                    Insurancelist.append("<option class=Inner value=\"" + entry.getKey() + "\"  >" + entry.getValue().toString() + "</option>");
                }
            }
            HashMap<Integer, String> hm1 = new HashMap<Integer, String>();
            hm1 = claim_ppt_list("a", conn);
            Set set1 = hm1.entrySet();
            Iterator it1 = set1.iterator();
            while (it1.hasNext()) {
                Map.Entry entry1 = (Map.Entry) it1.next();
                if (entry1.getKey().equals(Integer.parseInt(patientstatus))) {
                    claimpptlist.append("<option class=Inner value=\"" + entry1.getKey() + "\" selected>" + entry1.getValue().toString() + "</option>");
                } else {
                    claimpptlist.append("<option class=Inner value=\"" + entry1.getKey() + "\"  >" + entry1.getValue().toString() + "</option>");
                }
            }
            HashMap<Integer, String> hm2 = new HashMap<Integer, String>();
            hm2 = claim_status_list("a", conn);
            Set set2 = hm2.entrySet();
            Iterator it2 = set2.iterator();
            while (it2.hasNext()) {
                Map.Entry entry2 = (Map.Entry) it2.next();
                if (entry2.getKey().equals(Integer.parseInt(claimstatus))) {
                    claimstatuslist.append("<option class=Inner value=\"" + entry2.getKey() + "\" selected>" + entry2.getValue().toString() + "</option>");
                } else {
                    claimstatuslist.append("<option class=Inner value=\"" + entry2.getKey() + "\"  >" + entry2.getValue().toString() + "</option>");
                }
            }
            aa = "3";
            String textboxvalue = "<script>document.getElementById('Remarks').value = '" + Remarks + "';</script>";

            Query = "Select Code, Description from oe.PatientDischargeStatus where Status = 1";
            hstmt = conn.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                PatientStatus.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(1) + " - " + hrset.getString(2) + "</option>");
            }
            hrset.close();
            ;
            hstmt.close();

//          Query = "Select Code, Description, CASE WHEN Status = 1 THEN 'Active' ELSE 'INACTIVE' END from oe.DiagnosisCodes where Status = 1";
//          hstmt = conn.createStatement();
//          for(hrset = hstmt.executeQuery(Query); hrset.next();)
//          {
//              DiagnosisCodesList.append("<tr onclick=\"GetDiagnosisCode('"+hrset.getString(1)+"')\">");
//              DiagnosisCodesList.append("<td align=left >" + hrset.getString(1) + "</td>\n");
//              DiagnosisCodesList.append("<td align=left >" + hrset.getString(2) + "</td>\n");
//              DiagnosisCodesList.append("<td align=left>" + hrset.getString(3) + "</td>\n");
//              DiagnosisCodesList.append("</tr>");
//          }
//          hrset.close();
//          hstmt.close();
//
//          Query = "Select ChargeMasterTableName from oe.clients where Id = '"+clientid+"'";
//          hstmt = conn.createStatement();
//          hrset = hstmt.executeQuery(Query);
//          if(hrset.next()){
//              ChargeMasterTableName = hrset.getString(1);
//          }
//          hrset.close();
//          hstmt.close();
//
//          Query = "Select Id, CPTCode, ShortDescription, Price from oe."+ChargeMasterTableName+" ";
//          hstmt = conn.createStatement();
//          for(hrset = hstmt.executeQuery(Query); hrset.next();)
//          {
//              CPTCodesList.append("<tr onclick=\"GetCPTCodes('"+hrset.getString(1)+"')\">");
//              CPTCodesList.append("<td align=left >" + hrset.getString(2) + "</td>\n");
//              CPTCodesList.append("<td align=left >" + hrset.getString(3) + "</td>\n");
//              CPTCodesList.append("<td align=left>" + hrset.getString(4) + "</td>\n");
//              CPTCodesList.append("</tr>");
//          }
//          hrset.close();
//          hstmt.close();


            Services.GetCalendar(Day, Month, Year);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("firstname", firstname.toString());
            Parser.SetField("indexptr", indexptr.toString());
            Parser.SetField("lastname", lastname.toString());
            Parser.SetField("Acc", Acc.toString());
            Parser.SetField("dosdate", dosdate.toString());
            Parser.SetField("entrydate", entrydate.toString());
            Parser.SetField("target", target.toString());
            Parser.SetField("mrn", mrn.toString());
            Parser.SetField("textboxvalue", textboxvalue.toString());
            Parser.SetField("filename", filename.toString());
            Parser.SetField("Insurancelist", Insurancelist.toString());
            Parser.SetField("dosdate", dosdate.toString());
            Parser.SetField("editentrydate", editentrydate.toString());
            Parser.SetField("Acc", Acc.toString());
            aa = "4";
            Parser.SetField("Phone", Phone.toString());
            Parser.SetField("email", email.toString());
            Parser.SetField("address", address.toString());
            Parser.SetField("charges", charges.toString());
            Parser.SetField("claimamount", claimamount.toString());
            Parser.SetField("claimstatus", claimstatus.toString());
            Parser.SetField("patientstatus", patientstatus.toString());
            Parser.SetField("insurance", insurance.toString());
            Parser.SetField("claimpptlist", claimpptlist.toString());
            Parser.SetField("claimstatuslist", claimstatuslist.toString());
            Parser.SetField("PatientStatus", PatientStatus.toString());
//          Parser.SetField("DiagnosisCodesList",DiagnosisCodesList.toString());
//          Parser.SetField("CPTCodesList",CPTCodesList.toString());
            aa = "5";
            Parser.SetField("chiofcomplaint", chiofcomplaint.toString());
            Parser.SetField("cmdref", cmdref.toString());
            Parser.SetField("Remarks", Remarks.toString());
            Parser.SetField("Mailed", Mailed.toString());
            Parser.SetField("clientid", clientid.toString());

            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/Addinfo.html");
        } catch (Exception e) {
            out.println(aa + " Unable to process the request..." + e.getMessage());
        }
    }

    public void Addinfohl7(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {

        StringBuffer Day = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Year = new StringBuffer();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String indexptr = request.getParameter("indexptr");
        String mrn = request.getParameter("mrn");
        String firstname = "";
        String lastname = "";
        String Acc = "";
        String dosdate = "";
        String filename = "";
        String target = "";
        String entrydate = "";
        String clientid = "";
        StringBuffer Insurancelist = new StringBuffer();
        StringBuffer claimstatuslist = new StringBuffer();
        StringBuffer claimpptlist = new StringBuffer();
        String userindex = Services.GetCookie("userindex", request).trim();
        String note = "";

        String Phone = "";
        String email = "";
        String address = "";
        String charges = "0";
        String claimamount = "0";
        String claimstatus = "0";
        String patientstatus = "0";
        String insurance = "0";
        String chiofcomplaint = "";
        String cmdref = "";
        String Remarks = "";
        String Mailed = "";
        String editby = "";
        String editentrydate = "";
        String aa = "0";

        try {
            note = "Open Add info and load pdf";
            createnote(Integer.parseInt(userindex), note, Integer.parseInt(indexptr), conn);
            logging(Integer.parseInt(userindex), 2, Integer.parseInt(indexptr), conn);
            markuser(Integer.parseInt(userindex), 2, Integer.parseInt(indexptr), conn);
            Query = "select Id,target,entrydate,clientdirectory,filename,acc,dosdate,epowertime,processed,processby,"
                    + "filestatus,liststatus,ifnull(firstname,'-'),ifnull(lastname,'-'),ifnull(mrn,'-') "
                    + "from filelogs_sftp where Id=" + indexptr;


            // out.println(Query);


            hstmt = conn.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                firstname = hrset.getString(13);
                lastname = hrset.getString(14);
                Acc = hrset.getString(6);
                dosdate = hrset.getString(7);
                entrydate = hrset.getString(3);
                target = hrset.getString(2);
                mrn = hrset.getString(15);
                filename = hrset.getString(5);
                clientid = hrset.getString(4);
            }
            hrset.close();
            hstmt.close();
            // Insurancelist.append("<option class=Inner value=\"-1\"> All </option>");
            // Insurancelist.append("<option class=Inner value=\"" + entry.getKey() + "\">" + entry.getValue().toString() + "</option>");


            Query = "select ifnull(phone,''),ifnull(email,''),ifnull(address,''),ifnull(claimamount,''),ifnull(charges,''),ifnull(dosdate,''),ifnull(entrydate,''),ifnull(claimstatus,''),ifnull(patientstatus,''),ifnull(insurance,''),"
                    + "ifnull(chiofcomplaint,''),ifnull(cmdref,''),ifnull(Remarks,''),ifnull(LMailed,''),ifnull(createddate,''),createdby from claim_info_master where claimid=" + indexptr;

            hstmt = conn.createStatement();
            //  out.println(Query);
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                Phone = hrset.getString(1);
                email = hrset.getString(2);
                aa = "1";
                address = hrset.getString(3);
                claimamount = hrset.getString(4);
                charges = hrset.getString(5);
                //dosdate = hrset.getString(6) ;
                editentrydate = hrset.getString(7);
                claimstatus = hrset.getString(8);
                patientstatus = hrset.getString(9);
                insurance = hrset.getString(10);
                chiofcomplaint = hrset.getString(11);
                cmdref = hrset.getString(12);
                Remarks = hrset.getString(13);
                Mailed = hrset.getString(14);
                editby = hrset.getString(16);

                aa = "2";

            }


            hrset.close();
            hstmt.close();

            HashMap<Integer, String> hm = new HashMap<Integer, String>();
            hm = Insurancelist("a", conn);
            Set set = hm.entrySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if (entry.getKey().equals(Integer.parseInt(insurance))) {
                    Insurancelist.append("<option class=Inner value=\"" + entry.getKey() + "\" selected >" + entry.getValue().toString() + "</option>");
                } else {
                    Insurancelist.append("<option class=Inner value=\"" + entry.getKey() + "\"  >" + entry.getValue().toString() + "</option>");

                }

            }

            HashMap<Integer, String> hm1 = new HashMap<Integer, String>();
            hm1 = claim_ppt_list("a", conn);
            Set set1 = hm1.entrySet();
            Iterator it1 = set1.iterator();
            while (it1.hasNext()) {
                Map.Entry entry1 = (Map.Entry) it1.next();
                if (entry1.getKey().equals(Integer.parseInt(patientstatus))) {
                    claimpptlist.append("<option class=Inner value=\"" + entry1.getKey() + "\" selected>" + entry1.getValue().toString() + "</option>");
                } else {
                    claimpptlist.append("<option class=Inner value=\"" + entry1.getKey() + "\"  >" + entry1.getValue().toString() + "</option>");

                }

            }

            HashMap<Integer, String> hm2 = new HashMap<Integer, String>();
            hm2 = claim_status_list("a", conn);
            Set set2 = hm2.entrySet();
            Iterator it2 = set2.iterator();
            while (it2.hasNext()) {
                Map.Entry entry2 = (Map.Entry) it2.next();
                if (entry2.getKey().equals(Integer.parseInt(claimstatus))) {
                    claimstatuslist.append("<option class=Inner value=\"" + entry2.getKey() + "\" selected>" + entry2.getValue().toString() + "</option>");
                } else {
                    claimstatuslist.append("<option class=Inner value=\"" + entry2.getKey() + "\"  >" + entry2.getValue().toString() + "</option>");

                }

            }
            aa = "3";
            String textboxvalue = "<script>document.getElementById('Remarks').value = '" + Remarks + "';</script>";

            Services.GetCalendar(Day, Month, Year);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("firstname", firstname.toString());
            Parser.SetField("indexptr", indexptr.toString());
            Parser.SetField("lastname", lastname.toString());
            Parser.SetField("Acc", Acc.toString());
            Parser.SetField("dosdate", dosdate.toString());
            Parser.SetField("entrydate", entrydate.toString());
            Parser.SetField("target", target.toString());
            Parser.SetField("mrn", mrn.toString());
            Parser.SetField("textboxvalue", textboxvalue.toString());
            Parser.SetField("filename", filename.toString());
            Parser.SetField("Insurancelist", Insurancelist.toString());
            Parser.SetField("dosdate", dosdate.toString());
            Parser.SetField("editentrydate", editentrydate.toString());
            Parser.SetField("Acc", Acc.toString());
            aa = "4";
            Parser.SetField("Phone", Phone.toString());
            Parser.SetField("email", email.toString());
            Parser.SetField("address", address.toString());
            Parser.SetField("charges", charges.toString());
            Parser.SetField("claimamount", claimamount.toString());
            Parser.SetField("claimstatus", claimstatus.toString());
            Parser.SetField("patientstatus", patientstatus.toString());
            Parser.SetField("insurance", insurance.toString());
            Parser.SetField("claimpptlist", claimpptlist.toString());
            Parser.SetField("claimstatuslist", claimstatuslist.toString());
            aa = "5";
            Parser.SetField("chiofcomplaint", chiofcomplaint.toString());
            Parser.SetField("cmdref", cmdref.toString());
            Parser.SetField("Remarks", Remarks.toString());
            Parser.SetField("Mailed", Mailed.toString());
            Parser.SetField("clientid", clientid.toString());

            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/Addinfohl7.html");
        } catch (Exception e) {
            out.println(aa + " Unable to process the request..." + e.getMessage());
        }
    }

    public void Addinfosave(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {

        StringBuffer Day = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Year = new StringBuffer();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        Statement hstmt1 = null;
        ResultSet hrset1 = null;
        String Query1 = "";
        int SaveUpdateFlag = Integer.parseInt(request.getParameter("SaveUpdateFlag").trim());
        String indexptr = request.getParameter("indexptr");
        String mrn = request.getParameter("mrn");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String dosdate = request.getParameter("dosdate");
        String entrydate = request.getParameter("entrydate");
        String Acc = request.getParameter("Acc");
        String Phone = request.getParameter("Phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String charges = request.getParameter("charges");
        String claimamount = request.getParameter("claimamount");
        String claimstatus = request.getParameter("claimstatus");
        String patientstatus = request.getParameter("patientstatus");
        String insurance = request.getParameter("insurance");
        String chiofcomplaint = request.getParameter("chiofcomplaint");
        String cmdref = request.getParameter("cmdref");
        String Remarks = request.getParameter("Remarks");
        String Mailed = request.getParameter("Mailed");
        String TypeBillText = request.getParameter("TypeBillText");
        String clientid = request.getParameter("clientid");
        String UserId = Services.GetCookie("UserId", request).trim();
        String userindex = Services.GetCookie("userindex", request).trim();
        StringBuffer Insurancelist = new StringBuffer();
        String note = "";

        try {
            try {
                if (SaveUpdateFlag == 0) {
                    //Just save new data in the tables as per the record not deletion req here.... for Claim Coding Data and tables data
                    //************************** claimid == indexptr ************************
                    note = "update claim info ";
                    createnote(Integer.parseInt(userindex), note, Integer.parseInt(indexptr), conn);
                    logging(Integer.parseInt(userindex), 3, Integer.parseInt(indexptr), conn);
                    markuser(Integer.parseInt(userindex), 3, Integer.parseInt(indexptr), conn);

                    Query1 = "delete from oe.claim_info_master where  claimid= '" + indexptr + "'";
                    //System.out.println(Query1);

                    hstmt1 = conn.createStatement();
                    hstmt1.executeUpdate(Query1);
                    hstmt1.close();

                    PreparedStatement MainReceipt1 = conn
                            .prepareStatement("INSERT INTO claim_info_master(claimid,mrn,acc,firstname,lastname,phone,email," +
                                    "address,claimamount,charges,entrydate,claimstatus,patientstatus,insurance," +
                                    "chiofcomplaint,cmdref,Remarks,LMailed,createdby,dosdate,clientid,createddate,typeofbill) " +
                                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),? )");

                    MainReceipt1.setString(1, indexptr);
                    MainReceipt1.setString(2, mrn);
                    MainReceipt1.setString(3, Acc);
                    MainReceipt1.setString(4, firstname);
                    MainReceipt1.setString(5, lastname);
                    MainReceipt1.setString(6, Phone);
                    MainReceipt1.setString(7, email);
                    MainReceipt1.setString(8, address);
                    MainReceipt1.setString(9, claimamount);
                    MainReceipt1.setString(10, charges);
                    MainReceipt1.setString(11, entrydate);
                    MainReceipt1.setString(12, claimstatus);
                    MainReceipt1.setString(13, patientstatus);
                    MainReceipt1.setString(14, insurance);
                    MainReceipt1.setString(15, chiofcomplaint);
                    MainReceipt1.setString(16, cmdref);
                    MainReceipt1.setString(17, Remarks);
                    MainReceipt1.setString(18, Mailed);
                    MainReceipt1.setString(19, UserId);
                    MainReceipt1.setString(20, dosdate);
                    MainReceipt1.setString(21, clientid);
                    MainReceipt1.setString(22, TypeBillText);

                    MainReceipt1.executeUpdate();
                    MainReceipt1.close();

                    PreparedStatement MainReceipt2 = conn
                            .prepareStatement("INSERT INTO claim_info_master_history(claimid,mrn,acc,firstname,lastname,phone,email," +
                                    "address,claimamount,charges,entrydate,claimstatus,patientstatus,insurance," +
                                    "chiofcomplaint,cmdref,Remarks,LMailed,createdby,createddate) " +
                                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now() )");
                    MainReceipt2.setString(1, indexptr);
                    MainReceipt2.setString(2, mrn);
                    MainReceipt2.setString(3, Acc);
                    MainReceipt2.setString(4, firstname);
                    MainReceipt2.setString(5, lastname);
                    MainReceipt2.setString(6, Phone);
                    MainReceipt2.setString(7, email);
                    MainReceipt2.setString(8, address);
                    MainReceipt2.setString(9, claimamount);
                    MainReceipt2.setString(10, charges);
                    MainReceipt2.setString(11, entrydate);
                    MainReceipt2.setString(12, claimstatus);
                    MainReceipt2.setString(13, patientstatus);
                    MainReceipt2.setString(14, insurance);
                    MainReceipt2.setString(15, chiofcomplaint);
                    MainReceipt2.setString(16, cmdref);
                    MainReceipt2.setString(17, Remarks);
                    MainReceipt2.setString(18, Mailed);
                    MainReceipt2.setString(19, UserId);

                    MainReceipt2.executeUpdate();
                    MainReceipt2.close();


                    Query = " Update oe.filelogs_sftp  " +
                            " Set firstname  = '" + firstname.trim() + "'" +
                            " , lastname  = '" + lastname.trim() + "'" +
                            " , mrn  = '" + mrn.trim() + "'" +
                            " , Acc  = '" + Acc.trim() + "'" +
                            " Where id ='" + indexptr.trim() + "'";
                    hstmt = conn.createStatement();
                    hstmt.executeUpdate(Query);
                    //	out.println(Query);
                    conn.close();
                } else {
//                 is k sath sath Claimcdoing Detail aur baqi sb but not claimcoding master ka data delete kro and save krao new data
                    //************************** claimid == indexptr ************************
                    note = "update claim info ";
                    createnote(Integer.parseInt(userindex), note, Integer.parseInt(indexptr), conn);
                    logging(Integer.parseInt(userindex), 3, Integer.parseInt(indexptr), conn);
                    markuser(Integer.parseInt(userindex), 3, Integer.parseInt(indexptr), conn);

                    Query1 = "delete from oe.claim_info_master where  claimid= '" + indexptr + "'";
                    //System.out.println(Query1);

                    hstmt1 = conn.createStatement();
                    hstmt1.executeUpdate(Query1);
                    hstmt1.close();

                    PreparedStatement MainReceipt1 = conn
                            .prepareStatement("INSERT INTO claim_info_master(claimid,mrn,acc,firstname,lastname,phone,email," +
                                    "address,claimamount,charges,entrydate,claimstatus,patientstatus,insurance," +
                                    "chiofcomplaint,cmdref,Remarks,LMailed,createdby,dosdate,clientid,createddate,typeofbill) " +
                                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),? )");

                    MainReceipt1.setString(1, indexptr);
                    MainReceipt1.setString(2, mrn);
                    MainReceipt1.setString(3, Acc);
                    MainReceipt1.setString(4, firstname);
                    MainReceipt1.setString(5, lastname);
                    MainReceipt1.setString(6, Phone);
                    MainReceipt1.setString(7, email);
                    MainReceipt1.setString(8, address);
                    MainReceipt1.setString(9, claimamount);
                    MainReceipt1.setString(10, charges);
                    MainReceipt1.setString(11, entrydate);
                    MainReceipt1.setString(12, claimstatus);
                    MainReceipt1.setString(13, patientstatus);
                    MainReceipt1.setString(14, insurance);
                    MainReceipt1.setString(15, chiofcomplaint);
                    MainReceipt1.setString(16, cmdref);
                    MainReceipt1.setString(17, Remarks);
                    MainReceipt1.setString(18, Mailed);
                    MainReceipt1.setString(19, UserId);
                    MainReceipt1.setString(20, dosdate);
                    MainReceipt1.setString(21, clientid);
                    MainReceipt1.setString(22, TypeBillText);

                    MainReceipt1.executeUpdate();
                    MainReceipt1.close();

                    PreparedStatement MainReceipt2 = conn
                            .prepareStatement("INSERT INTO claim_info_master_history(claimid,mrn,acc,firstname,lastname,phone,email," +
                                    "address,claimamount,charges,entrydate,claimstatus,patientstatus,insurance," +
                                    "chiofcomplaint,cmdref,Remarks,LMailed,createdby,createddate) " +
                                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now() )");
                    MainReceipt2.setString(1, indexptr);
                    MainReceipt2.setString(2, mrn);
                    MainReceipt2.setString(3, Acc);
                    MainReceipt2.setString(4, firstname);
                    MainReceipt2.setString(5, lastname);
                    MainReceipt2.setString(6, Phone);
                    MainReceipt2.setString(7, email);
                    MainReceipt2.setString(8, address);
                    MainReceipt2.setString(9, claimamount);
                    MainReceipt2.setString(10, charges);
                    MainReceipt2.setString(11, entrydate);
                    MainReceipt2.setString(12, claimstatus);
                    MainReceipt2.setString(13, patientstatus);
                    MainReceipt2.setString(14, insurance);
                    MainReceipt2.setString(15, chiofcomplaint);
                    MainReceipt2.setString(16, cmdref);
                    MainReceipt2.setString(17, Remarks);
                    MainReceipt2.setString(18, Mailed);
                    MainReceipt2.setString(19, UserId);

                    MainReceipt2.executeUpdate();
                    MainReceipt2.close();


                    Query = " Update oe.filelogs_sftp  " +
                            " Set firstname  = '" + firstname.trim() + "'" +
                            " , lastname  = '" + lastname.trim() + "'" +
                            " , mrn  = '" + mrn.trim() + "'" +
                            " , Acc  = '" + Acc.trim() + "'" +
                            " Where id ='" + indexptr.trim() + "'";
                    hstmt = conn.createStatement();
                    hstmt.executeUpdate(Query);
                    //	out.println(Query);
                    conn.close();

                }
            } catch (Exception ee) {
                out.println(ee.getMessage());
            }
            int nextindexptr = Integer.parseInt(indexptr) + 1;

            String htmlredirect = "<!DOCTYPE html><html><body><script>setTimeout(function(){ window.location.href = 'https://rovermd.com:8443/oe/oe.chartreport?Action=Addinfo&indexptr=" + nextindexptr + "&mrn=-';}, 2000);</script><p>Going on next chart. redirects after 5 seconds.</p></body></html>";
            out.println(htmlredirect);
        } catch (Exception e) {
            out.println("Unable to process the request...");
        }
    }

    public void showclaimlogs(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {

        StringBuffer Day = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Year = new StringBuffer();
        StringBuffer ClientList = new StringBuffer();

        try {

            ClientList.append("<option class=Inner value=\"-1\"> All </option>");
            HashMap<Integer, String> hm = new HashMap<Integer, String>();
            hm = Clientlist("a", conn);
            Set set = hm.entrySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                ClientList.append("<option class=Inner value=\"" + entry.getKey() + "\">" + entry.getValue().toString() + "</option>");


            }

            Services.GetCalendar(Day, Month, Year);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DayList", Day.toString());
            Parser.SetField("ClientList", ClientList.toString());
            Parser.SetField("MonthList", Month.toString());
            Parser.SetField("YearList", Year.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showclaimlogs.html");
        } catch (Exception e) {
            out.println("Unable to process the request...");
        }
    }

    public void showivrlogs_error(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {

        StringBuffer Day = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Year = new StringBuffer();

        try {

            Services.GetCalendar(Day, Month, Year);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DayList", Day.toString());
            Parser.SetField("MonthList", Month.toString());
            Parser.SetField("YearList", Year.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showivrlog_error.html");
        } catch (Exception e) {
            out.println("Unable to process the request...");
        }
    }

    public void showivrlogs_balance(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {

        StringBuffer Day = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Year = new StringBuffer();

        try {

            Services.GetCalendar(Day, Month, Year);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DayList", Day.toString());
            Parser.SetField("MonthList", Month.toString());
            Parser.SetField("YearList", Year.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showivrlog_balance.html");
        } catch (Exception e) {
            out.println("Unable to process the request...");
        }
    }

    public void showivrlogssum(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {

        StringBuffer Day = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Year = new StringBuffer();

        try {

            Services.GetCalendar(Day, Month, Year);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DayList", Day.toString());
            Parser.SetField("MonthList", Month.toString());
            Parser.SetField("YearList", Year.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showivrlogsum.html");
        } catch (Exception e) {
            out.println("Unable to process the request...");
        }
    }

    public void showchartdetails(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";

        String fileindex = request.getParameter("fileindex");
        DecimalFormat df2 = new DecimalFormat("#,###.00");
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        String UserId = Services.GetCookie("UserId", request).trim();


        String Acct = request.getParameter("Acct");
        String custid = request.getParameter("fileindex");
        String AgentId = "-1";
        String ForDate = request.getParameter("StartDateT");
        String dosdate = request.getParameter("dosdate");
        String ToDate = "";
        String FromDate = "";
        String DToDate = "";
        String DFromDate = "";
        String AgentIdChk = "";
        String Condition = "";
        String Condition1 = "";
        String Stage = "0";


        int SNo = 0;

        try {

            out.println(ForDate);
            out.println(dosdate);
            out.println(ToDate);
            out.println(FromDate);
            Stage = "13e";
            ToDate = ForDate.substring(0, 19);
            FromDate = ForDate.substring(22, 41);

            DToDate = dosdate.substring(0, 19);
            DFromDate = dosdate.substring(22, 41);


            // out.println(ForDate);
            // out.println(ToDate);
            // out.println(FromDate);

            if (Acct.length() > 0)
                Condition = " and Acct like '%" + Acct + "%' ";
            // if(Acct.length() > 0)
            //   Condition1 = " and Acct like '%" + Acct + "%' ";

            if (custid.compareTo("-1") == 0) {
                Condition1 = "";
            } else {
                Condition1 = " and clientdirectory='" + custid + "'";
            }


            //  Query = "select id,callerid,customerid,argument,json1,json2,parsedmsg,entrydate,amount,ifnull(file,'-') from IVRLOG where entrydate between '"+ToDate+"' and '" + FromDate +"'  "+Condition+" "+Condition1+" order by entrydate";

            Query = "select Id, indexptr, timestamp,user,action ,result ,target ,address ,connection_type ,size, country_code, seq_id, uid,isdir,automation_rule_name, entrydate,clientdirectory, filename,Acct,chartdate,chartdate2 from filelogs"
//          		+ " where entrydate between '"+ToDate+"' and '" + FromDate +"'  "+Condition+" "+Condition1+" order by entrydate";
                    + " where entrydate between '" + ToDate + "' and '" + FromDate + "'  " + Condition + " " + Condition1 + " order by entrydate";


            out.println(Query);
            String Created = "";


            hstmt = conn.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {

                ++SNo;

                CDRList.append("<tr class=\"Inner\">\n");
                CDRList.append("<td align=center>" + SNo + "</td>\n");
                // CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(7) + "%</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(8) + "</td>\n");
                Stage = "1";
                CDRList.append("<td align=left>" + hrset.getString(9) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(10) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(11) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(12) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(13) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(14) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(15) + "</td>\n");
                Stage = "0111";
                CDRList.append("<td align=left>" + hrset.getString(16) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(17) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(18) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(19) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(20) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(21) + "</td>\n");
                //    CDRList.append("<td align=left>" + hrset.getString(15) + "</td>\n");
                Stage = "2";

                CDRList.append("<td><a href=http://18.191.170.153:83/oe/oe.chartreport?Action=download&fname=" + hrset.getString(7) + " target='_blank'>download</a></td>");
                CDRList.append("<input type='checkbox' name='vehicle1' ></td>");

                CDRList.append("</tr>\n");


            }
            hrset.close();
            hstmt.close();
            Stage = "3";
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", ForDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showchartReport.html");
        } catch (Exception e) {
            out.println(Stage + "Unable to process the request..." + e.getMessage());
        }
    }

    @SuppressWarnings("unlikely-arg-type")
    public void showchartdetails_new(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";

        String fileindex = request.getParameter("fileindex");
        DecimalFormat df2 = new DecimalFormat("#,###.00");
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        String UserId = Services.GetCookie("UserId", request).trim();


        String Acct = request.getParameter("Acct");
        String custid = request.getParameter("fileindex");
        String AgentId = "-1";
        String ForDate = request.getParameter("StartDateT");
        String dosdate = request.getParameter("dosdate");
        String ToDate = "";
        String FromDate = "";
        String DToDate = "";
        String DFromDate = "";
        String AgentIdChk = "";
        String Condition = "";
        String Condition1 = "";
        String Stage = "0";
        String clientname = null;
        String processby = null;
        String Id = "";

        int SNo = 0;

        try {

            //out.println(ForDate);
            //out.println(dosdate);
            //out.println(ToDate);
            //out.println(FromDate);
            Stage = "13e";
            ToDate = ForDate.substring(0, 19);
            FromDate = ForDate.substring(22, 41);

            DToDate = dosdate.substring(0, 19);
            DFromDate = dosdate.substring(22, 41);


            // out.println(ForDate);
            // out.println(ToDate);
            // out.println(FromDate);

            if (Acct.length() > 0)
                Condition = " and Acct like '%" + Acct + "%' ";
            // if(Acct.length() > 0)
            //   Condition1 = " and Acct like '%" + Acct + "%' ";

            if (custid.compareTo("-1") == 0) {
                Condition1 = "";
            } else {
                Condition1 = " and clientdirectory='" + custid + "'";
            }


            //  Query = "select id,callerid,customerid,argument,json1,json2,parsedmsg,entrydate,amount,ifnull(file,'-') from IVRLOG where entrydate between '"+ToDate+"' and '" + FromDate +"'  "+Condition+" "+Condition1+" order by entrydate";

            Query = "select Id, indexptr, timestamp,user,action ,result ,target ,address ,connection_type ,size, country_code, seq_id, uid,isdir,automation_rule_name, entrydate,clientdirectory, filename,Acct,chartdate,chartdate2 from filelogs"
//          		+ " where entrydate between '"+ToDate+"' and '" + FromDate +"'  "+Condition+" "+Condition1+" order by entrydate";
                    + " where entrydate between '" + ToDate + "' and '" + FromDate + "'  " + Condition + " " + Condition1 + " order by entrydate";

            Query = "select Id,target,entrydate,clientdirectory,filename,acc,dosdate,epowertime,processed,processby,filestatus,liststatus,ifnull(firstname,'-'),ifnull(lastname,'-'),ifnull(mrn,'-') "
                    + "from filelogs_sftp where dosdate between '" + DToDate + "' and '" + DFromDate + "'  " + Condition + " " + Condition1 + " order by dosdate";
//          out.println(Query);
            String Created = "";
            HashMap<Integer, String> clientlist = new HashMap<Integer, String>();
            clientlist = Clientlist("aaa", conn);
            HashMap<Integer, String> userlist = new HashMap<Integer, String>();
            userlist = Userlist("aaa", conn);
            HashMap<Integer, String> statuslistdrop = new HashMap<Integer, String>();
            statuslistdrop = statuslist("aa", conn);
            //out.println(statuslistdrop);
            hstmt = conn.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {

                ++SNo;


                CDRList.append("<tr class=\"Inner\">\n");
                CDRList.append("<td align=center>" + SNo + "</td>\n");
                // CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                Id = hrset.getString(1);
                CDRList.append("<td align=left>" + hrset.getString(13) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(14) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(15) + "</td>\n");


                CDRList.append("<td align=left>" + hrset.getString(6) + "</td>\n");

                CDRList.append("<td align=left>" + getclientname(clientlist, hrset.getInt(4)) + "</td>\n");

                CDRList.append("<td align=left>" + hrset.getString(7) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(8) + "</td>\n");


                if (hrset.getString(10).compareTo("0") == 0) {
                    processby = "<font color=\"red\">Fresh</font>";
                } else {
                    processby = "<p style=\"color:rgb(255,255,0); background:black\">" + getclientname(userlist, hrset.getInt(10)) + "</p>";
                    // processby="<font color=\"#FFFF00\">"+getclientname(userlist,hrset.getInt(10))+"</font>";
                }
                CDRList.append("<td align=left>" + processby + "</td>\n");
                Stage = "1";
                //StringBuffer listofstatus = new StringBuffer();

                // listofstatus.append(filestatuslist(statuslistdrop,hrset.getInt(11),hrset.getInt(1)));

                StringBuffer listofstatus = new StringBuffer();
                listofstatus.append("<select id=\"StatusAction" + hrset.getInt(1) + "\" onChange=\"updatestatus(" + hrset.getInt(1) + ")\">");

                //  out.println(statuslistdrop.size());
                for (int i = 0; i <= statuslistdrop.size(); i++) {
                    if (i == hrset.getInt(12)) {
                        listofstatus.append("<option  value=" + i + " selected >" + statuslistdrop.get(i) + "</option>");
                    } else {
                        listofstatus.append("<option  value=" + i + "  >" + statuslistdrop.get(i) + "</option>");
                    }
                }

                // CDRList.append("<td align=left>" +getclientname(statuslist,hrset.getInt(11)) + "</td>\n");
                CDRList.append("<td align=left><div id=\"cstatus" + Id + "\" >" + getclientname(statuslistdrop, hrset.getInt(11)) + "</div></td>\n");
                CDRList.append("<td align=left>" + listofstatus + "</td>\n");
                //  CDRList.append("<td align=left>" + hrset.getString(11) + "</td>\n");
                //  CDRList.append("<td align=left>" + hrset.getString(12) + "</td>\n");

                Stage = "0111";

                //    CDRList.append("<td align=left>" + hrset.getString(15) + "</td>\n");
                Stage = "2";
                String file = "https://rovermd.com:8443/oe/oe.chartreport?Action=download&fname=" + hrset.getString(5) + "&path=" + hrset.getString(2) + "&indexptr=" + hrset.getInt(1);
                file = URLEncoder.encode(file);
                //  CDRList.append("<td><a href=http://54.80.137.178:83/oe/oe.chartreport?Action=openpdf&file="+file+" target='_blank'>download</a></td>");

                if ((hrset.getString(5).endsWith(".pdf")) || (hrset.getString(5).endsWith(".PDF"))) {
                    CDRList.append("<td><a href=https://rovermd.com:8443/oe/oe.chartreport?Action=download&fname=" + hrset.getString(5) + "&path=" + hrset.getString(2) + "&indexptr=" + hrset.getInt(1) + " target='_blank'>download</a></td>");
                    CDRList.append("<td><a href=https://rovermd.com:8443/oe/oe.chartreport?Action=Addinfo&indexptr=" + hrset.getString(1) + "&mrn=" + hrset.getString(15) + " target='_blank'>Add info</a></td>");
                } else {
                    CDRList.append("<td><a href=https://rovermd.com:8443/oe/oe.chartreport?Action=downloadhl7&fname=" + hrset.getString(5) + "&path=" + hrset.getString(2) + "&indexptr=" + hrset.getInt(1) + " target='_blank'>download HL7</a></td>");
                    CDRList.append("<td><a href=https://rovermd.com:8443/oe/oe.chartreport?Action=Addinfohl7&indexptr=" + hrset.getString(1) + "&mrn=" + hrset.getString(15) + " target='_blank'>Add infoh</a></td>");
                }

                CDRList.append("<td align=left>" + hrset.getString(3) + "</td>\n");
                //  CDRList.append("<input type='checkbox' name='vehicle1' ></td>");
                CDRList.append("<td align=left>" + hrset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                CDRList.append("</tr>\n");


            }
            hrset.close();
            hstmt.close();
            Stage = "3";
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", ForDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showchartReport.html");
        } catch (Exception e) {
            out.println(Stage + "Unable to process the request..." + e.getMessage());
        }
    }

    public void updatestatus(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String indexptr = request.getParameter("indexptr");
        String statusindex = request.getParameter("statusindex");
        String UserId = Services.GetCookie("UserId", request).trim();
        String userindex = Services.GetCookie("userindex", request).trim();
        try {
            hstmt = conn.createStatement();
            Query = " Update oe.filelogs_sftp  " +
                    " Set filestatus  = '" + statusindex.trim() + "'" +
                    " , liststatus  = '" + statusindex.trim() + "'" +
                    " Where id ='" + indexptr.trim() + "'";
            hstmt.executeUpdate(Query);

            hstmt.close();
            logging(Integer.parseInt(userindex), Integer.parseInt(statusindex), Integer.parseInt(indexptr), conn);
            markuser(Integer.parseInt(userindex), Integer.parseInt(statusindex), Integer.parseInt(indexptr), conn);
            out.println("done");
        } catch (Exception ee) {
            out.println(ee.getMessage());
        }

    }

    public void showclaimdetails(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, Connection conn2) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";


        DecimalFormat df2 = new DecimalFormat("#,###.00");
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        String UserId = Services.GetCookie("UserId", request).trim();
        String indexptr = request.getParameter("indexptr");
        String mrn = request.getParameter("mrn");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        // String dosdate = request.getParameter("dosdate");
        String Acc = request.getParameter("Acc");
        String Phone = request.getParameter("Phone");
        String email = request.getParameter("email");

        String insurance = request.getParameter("insurance");
        String cmdref = request.getParameter("cmdref");
        String clientid = request.getParameter("clientid");

        //String Mailed = request.getParameter("Mailed");

        String AgentId = "-1";
        String dosdate = request.getParameter("dosdate");
        String ForDate = request.getParameter("StartDateT");
        String ToDate = "";
        String FromDate = "";
        String dToDate = "";
        String dFromDate = "";
        String AgentIdChk = "";
        String PhoneCondition = "";
        String firstnameCondition = "";
        String lastnameCondition = "";
        String emailCondition = "";
        String mrnCondition = "";
        String cmdrefCondition = "";
        String clientCondition = "";


        int SNo = 0;

        try {
            HashMap<Integer, String> clientlist = new HashMap<Integer, String>();
            clientlist = Clientlist("aaa", conn);

            //out.println(ForDate);
            // out.println(dosdate);
            // out.println(FromDate);
            ToDate = ForDate.substring(0, 19);
            FromDate = ForDate.substring(22, 41);
            dToDate = dosdate.substring(0, 19);
            dFromDate = dosdate.substring(22, 41);

            // out.println(ForDate);
            // out.println(ToDate);
            // out.println(FromDate);

            if (Phone.length() > 0)
                PhoneCondition = " and Phone like '%" + Phone + "%' ";
            if (firstname.length() > 0)
                firstnameCondition = " and firstname like '%" + firstname + "%' ";
            if (lastname.length() > 0)
                lastnameCondition = " and lastname like '%" + lastname + "%' ";
            if (email.length() > 0)
                emailCondition = " and email like '%" + email + "%' ";
            if (mrn.length() > 0)
                mrnCondition = " and mrn like '%" + mrn + "%' ";
            if (cmdref.length() > 0)
                cmdref = " and cmdref like '%" + cmdref + "%' ";
            if (clientid.compareTo("-1") != 0)
                clientCondition = " and clientid = '" + clientid + "' ";


            Query = "select id,callerid,customerid,argument,json1,json2,parsedmsg,entrydate,amount from IVRLOG where entrydate between '" + ToDate + "' and '" + FromDate + "'  " + PhoneCondition + " " + emailCondition + " order by entrydate";

            Query = "select claimid,ifnull(firstname,''),ifnull(lastname,''),ifnull(dosdate,''),ifnull(mrn,''),ifnull(cmdref,''),ifnull(charges,''),ifnull(phone,''),ifnull(email,''),ifnull(address,'')\r\n" +
                    ",ifnull(createddate,''),createdby,ifnull(LMailed,''),clientid from claim_info_master " +
                    "where entrydate between '" + ToDate + "' and '" + FromDate + "'  "
                    + "" + PhoneCondition + " " + emailCondition + " " + firstnameCondition + " " + lastnameCondition + " " + mrnCondition + " " + cmdrefCondition + " " + clientCondition + "";
            //out.println(Query);
            String Created = "";


            hstmt = conn2.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {

                ++SNo;

                CDRList.append("<tr class=\"Inner\">\n");
                CDRList.append("<td align=center>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(7) + "%</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(8) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(9) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(10) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(11) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(12) + "</td>\n");

                String mailedstatus = "Not Done";
                if (hrset.getString(13).compareTo("1") == 0) {
                    mailedstatus = "Sent";
                }
                CDRList.append("<td align=left>" + mailedstatus + "</td>\n");

                CDRList.append("<td align=left>" + getclientname(clientlist, hrset.getInt(14)) + "</td>\n");
                CDRList.append("<td><a href=http://54.80.137.178:83/oe/oe.chartreport?Action=Addinfo&indexptr=" + hrset.getString(1) + "&mrn=" + hrset.getString(6) + " target='_blank'>View detatails</a></td>");

                //  CDRList.append("<td align=left>" + hrset.getString(10) + "</td>\n");
                //    CDRList.append("<td align=left>" + hrset.getString(15) + "</td>\n");


                //CDRList.append("<td><a href=http://ourenergyllc.com:83/oe/oe.ivrreport?Action=checkemail&fname="+hrset.getString(10)+" target='_blank'>Check Logs</a></td>");


                CDRList.append("</tr>\n");


            }
            hrset.close();
            hstmt.close();

            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", ForDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showclaimdetails.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
        }
    }

    public void DashBoard(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, Connection conn2) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        String Query1 = "";

        int TotalCallsToday = 0;
        int AnsweredCall = 0;
        int MissedCalls = 0;
        int OutboundCalls = 0;
        int CallBack = 0;
        int calltoday = 0;
        int todaynotrancation = 0;
        double todayamount = 0.0d;
        int todayerror = 0;
        int monthlynotrancation = 0;
        int monthlyerror = 0;
        double monthlyamount = 0.0d;
        int monthlycall = 0;

        String UserId = Services.GetCookie("UserId", request);
        UserId = UserId.substring(1);
        try {
     /* Query = "Select Sum(sucess), Sum(failed), Sum(Recievied) From \r\n" +
      		"(select count(*) as sucess, 0 failed,sum(amount) Recievied from IVRLOG where parsedmsg in ('Sucess','Success') and substr(entrydate,1,10) =substr(now(),1,10) " +
      		"union all " +
      		" select 0 as sucess, count(*) failed,0 Recievied from IVRLOG where parsedmsg='failed' and substr(entrydate,1,10) =substr(now(),1,10)) a " ;


      stmt = conn.createStatement();
      rset = stmt.executeQuery(Query);
      if (rset.next()) {
    	  todaynotrancation = rset.getInt(1);
    	  todayerror = rset.getInt(2);
    	  todayamount = rset.getInt(3);

      }
      rset.close();
      stmt.close();


      Query = "Select Sum(sucess), Sum(failed), Sum(Recievied) From \r\n" +
		"(select count(*) as sucess, 0 failed,sum(amount) Recievied from IVRLOG where parsedmsg in ('Sucess','Success') and substr(entrydate,1,10) =substr(now(),1,10) " +
		"union all " +
		" select 0 as sucess, count(*) failed,0 Recievied from IVRLOG where parsedmsg='failed' and substr(entrydate,1,10) =substr(now(),1,10)) a " ;


		stmt = conn.createStatement();
		rset = stmt.executeQuery(Query);
		if (rset.next()) {
			  todaynotrancation =todaynotrancation+ rset.getInt(1);
			  todayerror =todayerror+ rset.getInt(2);
			  todayamount =todayamount+ rset.getInt(3);

		}
		rset.close();
		stmt.close();

        Query = "select count(*) from asteriskcdrdb.cdr where substr(calldate,1,10)=substr(now(),1,10) and dcontext='OEIVR'";

        stmt = conn.createStatement();
        rset = stmt.executeQuery(Query);
        if (rset.next()) {

      	calltoday = rset.getInt(1);


        }
        rset.close();
        stmt.close();

        // month
        Query = "Select Sum(sucess), Sum(failed), Sum(Recievied) From \r\n" +
          		"(select count(*) as sucess, 0 failed,sum(amount) Recievied from IVRLOG where parsedmsg in ('Sucess','Success') and substr(entrydate,1,7) =substr(now(),1,7)\r\n" +
          		"union all\r\n" +
          		"select 0 as sucess, count(*) failed,0 Recievied from IVRLOG where parsedmsg='failed' and substr(entrydate,1,7) =substr(now(),1,7)) a\r\n" ;


          stmt = conn.createStatement();
          rset = stmt.executeQuery(Query);
          if (rset.next()) {
        	  monthlynotrancation = rset.getInt(1);
        	  monthlyerror = rset.getInt(2);
        	  monthlyamount = rset.getInt(3);

          }
          rset.close();
          stmt.close();


          Query = "Select Sum(sucess), Sum(failed), Sum(Recievied) From \r\n" +
    		"(select count(*) as sucess, 0 failed,sum(amount) Recievied from IVRLOG where parsedmsg in ('Sucess','Success') and substr(entrydate,1,7) =substr(now(),1,7)\r\n" +
    		"union all\r\n" +
    		"select 0 as sucess, count(*) failed,0 Recievied from IVRLOG where parsedmsg='failed' and substr(entrydate,1,7) =substr(now(),1,7)) a\r\n" ;


    stmt = conn2.createStatement();
    rset = stmt.executeQuery(Query);
    if (rset.next()) {
  	  monthlynotrancation =monthlynotrancation+ rset.getInt(1);
  	  monthlyerror =monthlyerror+ rset.getInt(2);
  	  monthlyamount =monthlyamount+ rset.getInt(3);

    }
    rset.close();
    stmt.close();

            Query = "select count(*) from asteriskcdrdb.cdr where substr(calldate,1,7)=substr(now(),1,7) and dcontext='OEIVR'";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {

            	monthlycall = rset.getInt(1);


            }
            rset.close();
            stmt.close();

           // String.format("%,d", todayamount);
         // String.format("%,d", monthlyamount);
              */
            String pattern = "#,###.###";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            decimalFormat.setGroupingSize(3);

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("calltoday", String.valueOf(calltoday));

            Parser.SetField("todaynotrancation", String.valueOf(todaynotrancation));
            Parser.SetField("todayamount", "$ " + String.valueOf(decimalFormat.format(todayamount)));
            Parser.SetField("todayerror", String.valueOf(todayerror));

            Parser.SetField("monthlynotrancation", String.valueOf(monthlynotrancation));
            Parser.SetField("monthlyerror", String.valueOf(monthlyerror));
            Parser.SetField("monthlyamount", "$ " + String.valueOf(decimalFormat.format(monthlyamount)));
            Parser.SetField("monthlycall", String.valueOf(monthlycall));

            Parser.SetField("TotalCallsToday", String.valueOf(TotalCallsToday));
            Parser.SetField("AnsweredCall", String.valueOf(AnsweredCall));
            Parser.SetField("MissedCalls", String.valueOf(MissedCalls));
            Parser.SetField("OutboundCalls", String.valueOf(OutboundCalls));
            Parser.SetField("CallBack", String.valueOf(CallBack));
            // Parser.SetField("ExtensionDash", ExtensionDash.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Dashboards/GraphicalMainDashboard.html");
        } catch (Exception var11) {
            Services.DumException("DashBoard", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            out.flush();
            out.close();
        }
    }

    public void showivrdetails_error(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";

        String fileindex = request.getParameter("fileindex");
        DecimalFormat df2 = new DecimalFormat("#,###.00");
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        String UserId = Services.GetCookie("UserId", request).trim();


        String cli = request.getParameter("cli");
        String custid = request.getParameter("custid");
        String AgentId = "-1";
        String ForDate = request.getParameter("StartDateT");
        String ToDate = "";
        String FromDate = "";
        String AgentIdChk = "";
        String Condition = "";
        String Condition1 = "";


        int SNo = 0;

        try {

            ToDate = ForDate.substring(0, 19);
            FromDate = ForDate.substring(22, 41);

            // out.println(ForDate);
            // out.println(ToDate);
            // out.println(FromDate);

            if (cli.length() > 0)
                Condition = " and callerid like '%" + cli + "%' ";
            if (custid.length() > 0)
                Condition1 = " and customerid like '%" + custid + "%' ";


            Query = "select id,callerid,customerid,argument,json1,json2,parsedmsg,entrydate,amount,ifnull(file,'-'),substr(entrydate,1,10) from IVRLOG where entrydate between '" + ToDate + "' and '" + FromDate + "'  " + Condition + " " + Condition1 + " group by customerid order by entrydate ";
            // out.println(Query);
            String Created = "";


            hstmt = conn.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {

                String success = "0";
                success = getcount(hrset.getString(3), hrset.getString(11), conn);
                if (success.compareTo("0") == 0) {
                    ++SNo;
                    String attempt = attemps(hrset.getString(3), hrset.getString(11), conn);
                    CDRList.append("<tr class=\"Inner\">\n");
                    CDRList.append("<td align=center>" + SNo + "</td>\n");
                    // CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + attempt + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(7) + "%</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(8) + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(9) + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(10) + "</td>\n");
                    //    CDRList.append("<td align=left>" + hrset.getString(15) + "</td>\n");


                    CDRList.append("<td><a href=http://132.148.155.201:83/oe/oe.ivrreport?Action=checkemail&fname=" + hrset.getString(10) + " target='_blank'>Check Logs</a></td>");


                    CDRList.append("</tr>\n");

                }
            }
            hrset.close();
            hstmt.close();

            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", ForDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showivrReport_error.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
        }
    }

    public void showivrdetails_balance(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";

        String fileindex = request.getParameter("fileindex");
        DecimalFormat df2 = new DecimalFormat("#,###.00");
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        String UserId = Services.GetCookie("UserId", request).trim();


        String cli = request.getParameter("cli");
        String custid = request.getParameter("custid");
        String AgentId = "-1";
        String ForDate = request.getParameter("StartDateT");
        String ToDate = "";
        String FromDate = "";
        String AgentIdChk = "";
        String Condition = "";
        String Condition1 = "";


        int SNo = 0;

        try {

            ToDate = ForDate.substring(0, 19);
            FromDate = ForDate.substring(22, 41);

            // out.println(ForDate);
            // out.println(ToDate);
            // out.println(FromDate);

            if (cli.length() > 0)
                Condition = " and callerid like '%" + cli + "%' ";
            if (custid.length() > 0)
                Condition1 = " and customerid like '%" + custid + "%' ";


            Query = "select id,callerid,customerid,balance,zipentered,cust_zip,entrydate,ifnull(uid,'-'),substr(entrydate,1,10) from IVRBALANCELOG where entrydate between '" + ToDate + "' and '" + FromDate + "'  " + Condition + " " + Condition1 + " group by customerid order by entrydate ";
            // out.println(Query);
            String Created = "";
            String Remarks = "";
            String bistatus = "Success";
            hstmt = conn.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                Remarks = "";
                String success = "0";
                bistatus = "Success";
        	  /* if(success.compareTo("0")==0)
        	  {*/
                ++SNo;
                String attempt = attemps(hrset.getString(3), hrset.getString(9), conn);
                CDRList.append("<tr class=\"Inner\">\n");
                CDRList.append("<td align=center>" + SNo + "</td>\n");
                //CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(6) + "</td>\n");
                // CDRList.append("<td align=left>" +attempt+ "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(7) + "%</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(8) + "</td>\n");
                if (hrset.getString(3).length() <= 9) {
                    Remarks = "Short Customer ID\n";
                }
                if (hrset.getString(3).length() >= 12) {
                    Remarks += "To long Customer ID\n";
                }
                if (hrset.getString(4).length() == 1) {
                    Remarks += "Balance Inquiry Failed\n";
                    bistatus = "Failed";
                }
                if (hrset.getString(4).length() == 0) {
                    Remarks += "Balance Inquiry Failed\n";
                    bistatus = "Failed";
                }

                CDRList.append("<td align=left>" + Remarks + "</td>\n");
                CDRList.append("<td align=left>" + bistatus + "</td>\n");
                  /*success=getcount(hrset.getString(3),hrset.getString(9),conn);
                  if(success.compareTo("0")==0)
                  {
                	  CDRList.append("<td align=left>Payment Failed</td>\n");
                  }else{
                	  CDRList.append("<td align=left>Payment Successfull</td>\n");
                  }*/

                //  CDRList.append("<td><a href=http://132.148.155.201:83/oe/oe.ivrreport?Action=checkemail&fname="+hrset.getString(10)+" target='_blank'>Check Logs</a></td>");


                CDRList.append("</tr>\n");

                //}
            }
            hrset.close();
            hstmt.close();

            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", ForDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showivrReport_balance.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
        }
    }

    public void showivrdetailssum(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";

        String fileindex = request.getParameter("fileindex");
        DecimalFormat df2 = new DecimalFormat("#,###.00");
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        String UserId = Services.GetCookie("UserId", request).trim();


        String AgentId = "-1";
        String ForDate = request.getParameter("StartDateT");
        String ToDate = "";
        String FromDate = "";
        String AgentIdChk = "";
        String Condition = "";
        String Condition1 = "";
        String amount = "";


        int SNo = 0;

        try {

            ToDate = ForDate.substring(0, 19);
            FromDate = ForDate.substring(22, 41);

            // out.println(ForDate);
            // out.println(ToDate);
            // out.println(FromDate);


            Query = "select ROUND(sum(amount),2),parsedmsg,count(*) from IVRLOG  where entrydate between '" + ToDate + "' and '" + FromDate + "'   group by parsedmsg";
            // out.println(Query);
            String Created = "";


            hstmt = conn.createStatement();
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {

                ++SNo;
                amount = hrset.getString(1);
                CDRList.append("<tr class=\"Inner\">\n");
                CDRList.append("<td align=center>" + SNo + "</td>\n");
                // CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                CDRList.append("</tr>\n");


            }
            hrset.close();
            hstmt.close();

            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", ForDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showivrReportsum.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
        }
    }

    public StringBuffer insurancecompanylist(Connection conn) {


        return null;

    }


}
