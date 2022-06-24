package oe;

import Parsehtm.Parsehtm;
import com.sun.xml.internal.messaging.saaj.util.Base64;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
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

@SuppressWarnings("Duplicates")
public class PatientChart extends HttpServlet {
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
        Iterator<Map.Entry> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            if (entry.getKey().equals(Integer.valueOf(clientIndex)))
                clientname = entry.getValue().toString();
        }
        return clientname;
    }

    public static String filestatuslist(HashMap clientlist, int clientIndex, int indexptr) {
        StringBuffer listofstatus = new StringBuffer();
        listofstatus.append("<select id=\"StatusAction\" onChange=\"updatestatus(" + indexptr + "\\" + clientlist.toString() + ")\">");
        for (int i = 0; i < clientlist.size(); i++) {
            if (i == indexptr) {
                listofstatus.append("<option  value=" + i + " selected >" + clientlist.get(Integer.valueOf(i)) + "</option>");
            } else {
                listofstatus.append("<option  value=" + i + "  >" + clientlist.get(Integer.valueOf(i)) + "</option>");
            }
        }
        return listofstatus.toString();
    }

    public static HashMap<Integer, String> Clientlist(String aa, Connection Conn) {
        HashMap<Integer, String> hm = new HashMap<>();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,name from clients where status=0 ";
            hstmt = Conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                id = hrset.getInt(1);
                name = hrset.getString(2);
                hm.put(Integer.valueOf(id), name);
            }
            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception e) {
            return hm;
        }
    }

    public static HashMap<Integer, String> Insurancelist(String aa, Connection Conn) {
        HashMap<Integer, String> hm = new HashMap<>();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,Name from Insurancelist where status=0";
            hstmt = Conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                id = hrset.getInt(1);
                name = hrset.getString(2);
                hm.put(Integer.valueOf(id), name);
            }
            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception e) {
            return hm;
        }
    }

    public static HashMap<Integer, String> claim_status_list(String aa, Connection Conn) {
        HashMap<Integer, String> hm = new HashMap<>();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,descname from claim_status_list where status=0";
            hstmt = Conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                id = hrset.getInt(1);
                name = hrset.getString(2);
                hm.put(Integer.valueOf(id), name);
            }
            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception e) {
            return hm;
        }
    }

    public static HashMap<Integer, String> claim_ppt_list(String aa, Connection Conn) {
        HashMap<Integer, String> hm = new HashMap<>();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,descname from claim_ppt_list where status=0";
            hstmt = Conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                id = hrset.getInt(1);
                name = hrset.getString(2);
                hm.put(Integer.valueOf(id), name);
            }
            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception e) {
            return hm;
        }
    }

    public static HashMap<Integer, String> Userlist(String aa, Connection Conn) {
        HashMap<Integer, String> hm = new HashMap<>();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select indexptr,username from sysusers where enabled='Y'";
            hstmt = Conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                id = hrset.getInt(1);
                name = hrset.getString(2);
                hm.put(Integer.valueOf(id), name);
            }
            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception e) {
            return hm;
        }
    }

    public static HashMap<Integer, String> statuslist(String aa, Connection Conn) {
        HashMap<Integer, String> hm = new HashMap<>();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,name from filestatuslist where status='0'";
            hstmt = Conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                id = hrset.getInt(1);
                name = hrset.getString(2);
                hm.put(Integer.valueOf(id), name);
            }
            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception e) {
            return hm;
        }
    }

    public static StringBuffer GetNotes(int claimid, Connection conn, String mrn) {
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query2 = "";
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        try {
            Query2 = "Select a.Id, a.note, b.username, Date_format(a.createddate,'%m/%d/%Y %T') from oe.claim_note a left join oe.sysusers b on a.userindex = b.indexptr  where a.claimid = '" + claimid + "' and a.mrn = '" + mrn + "' order by a.Id desc ";
            stmt2 = conn.createStatement();
            rset2 = stmt2.executeQuery(Query2);
            while (rset2.next()) {
                if (rset2.getInt(4) > 0) {
                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + claimid + "</td>\n");
                    CDRList.append("<td align=left>" + mrn + "</td>\n");
                    CDRList.append("<td align=left>" + rset2.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset2.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset2.getString(4) + "</td>\n");
                    CDRList.append("</tr>");
                    SNo++;
                }
            }
            rset2.close();
            stmt2.close();
        } catch (Exception ee) {
            System.out.println("Error in getting notes: " + ee.getMessage());
        }
        return CDRList;
    }

    public static void GetNotes_AJAX(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query2 = "";
        StringBuffer CDRList = new StringBuffer();
        String claimid = request.getParameter("claimid");
        String mrn = request.getParameter("mrn");
        int SNo = 1;
        try {
            Query2 = "Select a.Id, a.note, b.username, Date_format(a.createddate,'%m/%d/%Y %T') from oe.claim_note a left join oe.sysusers b on a.userindex = b.indexptr  where a.claimid = '" + claimid + "' and a.mrn = '" + mrn + "' order by a.Id desc ";
            stmt2 = conn.createStatement();
            rset2 = stmt2.executeQuery(Query2);
            while (rset2.next()) {
                if (rset2.getInt(4) > 0) {
                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + claimid + "</td>\n");
                    CDRList.append("<td align=left>" + mrn + "</td>\n");
                    CDRList.append("<td align=left>" + rset2.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset2.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset2.getString(4) + "</td>\n");
                    CDRList.append("</tr>");
                    SNo++;
                }
            }
            rset2.close();
            stmt2.close();
            out.println(CDRList);
        } catch (Exception ee) {
            System.out.println("Error in getting notes: " + ee.getMessage());
        }
    }

    public static String logging(int userindex, int filestatus, int indexptr, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        try {
            hstmt = conn.createStatement();
            Query = " insert into fileactivity(fileindex,created,userindex,filestatus)  values('" + indexptr + "',now()," + userindex + ",'" + filestatus + "') ";
            hstmt.execute(Query);
        } catch (Exception exception) {
        }
        return null;
    }

    public static String createnote(int userindex, String note, int claimid, Connection conn, String mrn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        try {
            hstmt = conn.createStatement();
            Query = " insert into claim_note(note,userindex,createddate,claimid, mrn)  values('" + note + "','" + userindex + "',now(), '" + claimid + "', '" + mrn + "') ";
            hstmt.execute(Query);
        } catch (Exception exception) {
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
        } catch (Exception exception) {
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
            boolean Valid = (hrset.getInt(1) <= 0);
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

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void HandleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        Connection conn2 = null;
        String Action = null;
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        response.setContentType("text/html");
        String Stage = "";
        try {
            if (request.getParameter("Action") == null && request.getContentType().startsWith("multipart/form-data")) {
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
            Class.forName("com.mysql.jdbc.Driver").newInstance();
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
            if (Action.compareTo("showPatientChartDetails") == 0) {
                showchartdetails_new(request, response, out, conn);
            } else if (Action.compareTo("PatientChartReport_Input") == 0) {
                PatientChartReport_Input(request, response, out, conn);
            } else if (Action.compareTo("PatientChartReport") == 0) {
                PatientChartReport(request, response, out, conn);
            } else if (Action.compareTo("showchartlogs") == 0) {
                showchartlog(request, response, out, conn);
            } else if (Action.compareTo("Addinfo") == 0) {
                Addinfo(request, response, out, conn);
            } else if (Action.compareTo("download") == 0) {
                download(request, response, out, conn);
            } else if (Action.compareTo("download_direct") == 0) {
                download_direct(request, response, out, conn);
            } else if (Action.compareTo("Addinfosave") == 0) {
                Addinfosave(request, response, out, conn);
            } else if (Action.compareTo("updatestatus") == 0) {
                updatestatus(request, response, out, conn);
            } else if (Action.compareTo("GetNotes_AJAX") == 0) {
                GetNotes_AJAX(request, response, out, conn);
            } else if (Action.compareTo("PatientStatusReport_Input") == 0) {
                PatientStatusReport_Input(request, response, out, conn);
            } else if (Action.compareTo("PatientStatusReport") == 0) {
                PatientStatusReport(request, response, out, conn);
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
            URL url = new URL("https://app.smartfile.com/api/2/path/data/shares/FED%20Accounts/Physicians-Premier-Bastrop/Charts/459_37259_201910191546_201910231416_.pdf?download=true&ui=1");
            URLConnection uc = url.openConnection();
            System.out.println("https://app.smartfile.com/api/2/path/data/shares/FED%20Accounts/Physicians-Premier-Bastrop/Charts/459_37259_201910191546_201910231416_.pdf?download=true&ui=1");
            uc.setRequestProperty("X-Requested-With", "Curl");
            uc.setRequestProperty("Content-Type", "application/json");
            String userpass = "username:password";
            String userCredentials = "DlYTlsOY249jBise60b9y4r0emCXqB:dpgwmrFiJJnPojMNnkS14wgKTJjSIo";
            new Base64();
            String basicAuth = "Basic " + new String(Base64.encode("DlYTlsOY249jBise60b9y4r0emCXqB:dpgwmrFiJJnPojMNnkS14wgKTJjSIo".getBytes()));
            uc.setRequestProperty("Authorization", basicAuth);
            uc.setRequestProperty("postman-token", "9da9db4f-8248-e387-0fdd-018c98cb6f92");
            uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            out.println(in.toString());
            String line = in.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null)
                line = in.readLine();
            String fileAsString = sb.toString();
            out.println(fileAsString);
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
        }
    }

    public void download(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        String FileName = request.getParameter("fname");
        String path = request.getParameter("path");
        String indexptr = request.getParameter("indexptr");
        String RecordingPath = "";
        int n = FileName.length();
        char last = FileName.charAt(n - 1);
        if (path.endsWith("/")) {
            RecordingPath = path + FileName;
        } else {
            RecordingPath = path + "/" + FileName;
        }
        String UserId = Services.GetCookie("UserId", request).trim();
        String userindex = Services.GetCookie("userindex", request).trim();
        try {
            String[] mrnArr = FileName.split("\\_");
            String mrn = mrnArr[1];
            System.out.println(mrn + " :MRN");
            String note = "Download pdf charts";
            createnote(Integer.parseInt(userindex), "Download pdf charts", Integer.parseInt(indexptr), conn, mrn);
            logging(Integer.parseInt(userindex), 2, Integer.parseInt(indexptr), conn);
            markuser(Integer.parseInt(userindex), 2, Integer.parseInt(indexptr), conn);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "Inline;filename=" + FileName);
            FileInputStream fin = new FileInputStream(RecordingPath);
            byte[] content = new byte[fin.available()];
            fin.read(content);
            fin.close();
            ServletOutputStream servletOutputStream = response.getOutputStream();
            servletOutputStream.write(content);
            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }

    public void download_direct(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        String FileName = request.getParameter("fname");
        String path = request.getParameter("path");
        String indexptr = request.getParameter("indexptr");
        String RecordingPath = "";
        if (path.endsWith("/")) {
            RecordingPath = path + FileName;
        } else {
            RecordingPath = path + "/" + FileName;
        }
        String userindex = Services.GetCookie("userindex", request).trim();
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "Inline;filename=" + FileName);
            FileInputStream fin = new FileInputStream(RecordingPath);
            byte[] content = new byte[fin.available()];
            fin.read(content);
            fin.close();
            ServletOutputStream servletOutputStream = response.getOutputStream();
            servletOutputStream.write(content);
            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }

    /*public void hl7chart(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        String FileName = request.getParameter("fname");
        String path = request.getParameter("path");
        String indexptr = request.getParameter("indexptr");
        String RecordingPath = path + FileName;
        try {
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
            ourPipeParser.setValidationContext((ValidationContext)new NoValidation());
            Message hl7Message = ourPipeParser.parse(fileAsString.trim());
            Hl7dft4_clean.OBXfinal.delete(0, Hl7dft4_clean.OBXfinal.length());
            Hl7dft4_clean.PV1final.delete(0, Hl7dft4_clean.PV1final.length());
            Hl7dft4_clean.IN1final.delete(0, Hl7dft4_clean.IN1final.length());
            Hl7dft4_clean.PIDfinal.delete(0, Hl7dft4_clean.PIDfinal.length());
            Hl7dft4_clean.DGIfinal.delete(0, Hl7dft4_clean.DGIfinal.length());
            Hl7dft4_clean.FTIfinal.delete(0, Hl7dft4_clean.FTIfinal.length());
            Hl7dft4_clean.GT1final.delete(0, Hl7dft4_clean.GT1final.length());
            Hl7dft4_clean.extractValues(hl7Message);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PIDfinal", Hl7dft4_clean.PIDfinal.toString());
            Parser.SetField("PV1final", Hl7dft4_clean.PV1final.toString());
            Parser.SetField("IN1final", Hl7dft4_clean.IN1final.toString());
            Parser.SetField("FTIfinal", Hl7dft4_clean.FTIfinal.toString());
            Parser.SetField("DGIfinal", Hl7dft4_clean.DGIfinal.toString());
            Parser.SetField("OBXfinal", Hl7dft4_clean.OBXfinal.toString());
            Parser.SetField("GT1final", Hl7dft4_clean.GT1final.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/HL7chartReport.html");
        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }
*/
    public void openpdf(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        String FileName = request.getParameter("fname");
        String path = request.getParameter("path");
        String indexptr = request.getParameter("indexptr");
        String RecordingPath = path + FileName;
        String UserId = Services.GetCookie("UserId", request).trim();
        String userindex = Services.GetCookie("userindex", request).trim();
        try {
            String file = "http://54.80.137.178:83/oe/oe.chartreport?Action=download&fname=" + FileName + "&path=" + path + "&indexptr=" + indexptr;
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("file", file.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/openpdf.html");
        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }

    public void showchartdetails_new(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        DecimalFormat df2 = new DecimalFormat("#,###.00");
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        String UserId = Services.GetCookie("UserId", request).trim();
        String custid = request.getParameter("fileindex");
        String StatusAction = request.getParameter("StatusAction");
        String AgentId = "-1";
        String dosdate = request.getParameter("dosdate");
        String DToDate = "";
        String DFromDate = "";
        String Condition1 = "";
        String Condition2 = "";
        String Stage = "0";
        String clientname = null;
        String processby = null;
        String Id = "";
        int SNo = 0;
        try {
            Stage = "13e";
            DToDate = dosdate.substring(0, 19);
            DFromDate = dosdate.substring(22, 41);
            if (custid.compareTo("-1") == 0) {
                Condition1 = "";
            } else {
                Condition1 = " and clientdirectory='" + custid + "'";
            }
            if (StatusAction.compareTo("-1") == 0) {
                Condition2 = "";
            } else {
                Condition2 = " and filestatus = " + StatusAction + " and liststatus = " + StatusAction + " ";
            }
            Query = " SELECT id,entrydate, clientdirectory, acc, dosdate,substr(epowertime,1,16), " +
                    "processed, processby, filestatus, liststatus,\n ifnull(firstname,'-'), " +
                    "ifnull(lastname,'-'), ifnull(mrn,'-') FROM filelogs_sftp " +
                    "WHERE dosdate BETWEEN '" + DToDate + "' AND '" + DFromDate + "'  " + Condition1 + " " + Condition2 + " GROUP BY mrn,acc,dosdate ORDER BY dosdate";
            HashMap<Integer, String> clientlist = new HashMap<>();
            clientlist.clear();
            clientlist = Clientlist("aaa", conn);
            HashMap<Integer, String> userlist = new HashMap<>();
            userlist.clear();
            userlist = Userlist("aaa", conn);
            HashMap<Integer, String> statuslistdrop = new HashMap<>();
            statuslistdrop.clear();
            statuslistdrop = statuslist("aa", conn);
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                SNo++;
                CDRList.append("<tr class=\"Inner\">\n");
                CDRList.append("<td align=center>" + SNo + "</td>\n");
                Id = hrset.getString(1);
                CDRList.append("<td align=left>" + hrset.getString(11) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(12) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(13) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + getclientname(clientlist, hrset.getInt(3)) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(6) + "</td>\n");
                if (hrset.getString(8).compareTo("0") == 0) {
                    processby = "<font color=\"red\">Fresh</font>";
                } else {
                    processby = "<p style=\"color:rgb(255,255,0); background:black\">" + getclientname(userlist, hrset.getInt(8)) + "</p>";
                }
                CDRList.append("<td align=left>" + processby + "</td>\n");
                Stage = "1";
                StringBuffer listofstatus = new StringBuffer();
                listofstatus.append("<select id=\"StatusAction" + hrset.getInt(1) + "\" onChange=\"updatestatus(" + hrset.getInt(1) + ")\">");
                for (int i = 0; i <= statuslistdrop.size(); i++) {
                    if (i == hrset.getInt(10)) {
                        listofstatus.append("<option  value=" + i + " selected >" + (String) statuslistdrop.get(Integer.valueOf(i)) + "</option>");
                    } else {
                        listofstatus.append("<option  value=" + i + "  >" + (String) statuslistdrop.get(Integer.valueOf(i)) + "</option>");
                    }
                }
                CDRList.append("<td align=left><div id=\"cstatus" + Id + "\" >" + getclientname(statuslistdrop, hrset.getInt(9)) + "</div></td>\n");
                CDRList.append("<td align=left>" + listofstatus + "</td>\n");
                Stage = "0111";
                Stage = "2";
                CDRList.append("<td><a href=https://rovermd.com:8443/oe/oe.PatientChart?Action=Addinfo&indexptr=" + hrset.getString(1) + "&mrn=" + hrset.getString(13) + " target='_blank'>Add info</a></td>");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                CDRList.append("</tr>\n");
            }
            hrset.close();
            hstmt.close();
            Stage = "3";
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", dosdate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/showPatientChartReport.html");
        } catch (Exception e) {
            out.println(Stage + "Unable to process the request..." + e.getMessage());
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
        StringBuffer PdfList = new StringBuffer();
        StringBuffer ChartsList = new StringBuffer();
        StringBuffer ClaimHistory = new StringBuffer();
        StringBuffer CovidList = new StringBuffer();
        StringBuilder ClaimFollowup = new StringBuilder();
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
        String paymenttaken = "";
        String calledstatus = "";
        String CovidStatus = "";
        String TimeOut = "";
        String aa = "0";
        int SNo = 1;
        try {
            note = "Open Claim info and load pdf";
            createnote(Integer.parseInt(userindex), note, Integer.parseInt(indexptr), conn, mrn);
            logging(Integer.parseInt(userindex), 2, Integer.parseInt(indexptr), conn);
            markuser(Integer.parseInt(userindex), 2, Integer.parseInt(indexptr), conn);

            Query = "select Id,target,entrydate,clientdirectory,filename,acc,dosdate,epowertime," +
                    "processed,processby,filestatus,liststatus,ifnull(firstname,'-'),ifnull(lastname,'-')," +
                    "ifnull(mrn,'-') from filelogs_sftp " +
                    "where Id=" + indexptr;
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
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

            Query = "select ifnull(phone,''),ifnull(email,''),ifnull(address,''),ifnull(claimamount,''), " +
                    "ifnull(charges,''),ifnull(dosdate,''),ifnull(entrydate,''),ifnull(claimstatus,'-'), " +
                    "ifnull(patientstatus,'-'),ifnull(insurance,'-'),ifnull(chiofcomplaint,''), " +
                    "ifnull(cmdref,''),ifnull(Remarks,''),ifnull(LMailed,''),ifnull(createddate,''), " +
                    "createdby, ifnull(calledstatus,''), ifnull(paymenttaken,''), ifnull(CovidStatus,'0'), " +
                    "ifnull(TimeOut,'')  from claim_info_master " +
                    "where claimid=" + indexptr;
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                Phone = hrset.getString(1);
                email = hrset.getString(2);
                aa = "1";
                address = hrset.getString(3);
                claimamount = hrset.getString(4);
                charges = hrset.getString(5);
                editentrydate = hrset.getString(7);
                claimstatus = hrset.getString(8);
                patientstatus = hrset.getString(9);
                insurance = hrset.getString(10);
                chiofcomplaint = hrset.getString(11);
                cmdref = hrset.getString(12);
                Remarks = hrset.getString(13);
                Mailed = hrset.getString(14);
                editby = hrset.getString(16);
                calledstatus = hrset.getString(17);
                paymenttaken = hrset.getString(18);
                CovidStatus = hrset.getString(19);
                TimeOut = hrset.getString(20);
                aa = "2";
            }
            hrset.close();
            hstmt.close();

            if (Mailed.equals("1")) {
                Mailed = "<input type=\"checkbox\" id=\"LMailed\" name=\"LMailed\" value=\"1\" checked>";
            } else {
                Mailed = "<input type=\"checkbox\" id=\"LMailed\" name=\"LMailed\" value=\"1\">";
            }
            if (calledstatus.equals("1")) {
                calledstatus = "<input type=\"checkbox\" id=\"callstatus\" name=\"callstatus\" value=\"1\" checked>";
            } else {
                calledstatus = "<input type=\"checkbox\" id=\"callstatus\" name=\"callstatus\" value=\"1\">";
            }
            HashMap<Integer, String> hm = new HashMap<>();
            hm = Insurancelist("a", conn);
            Set<Map.Entry<Integer, String>> set = hm.entrySet();
            for (Map.Entry<Integer, String> entry : set) {
                if (!insurance.equals("-") && entry.getKey().equals(Integer.valueOf(Integer.parseInt(insurance)))) {
                    Insurancelist.append("<option class=Inner value=\"" + entry.getKey() + "\" selected >" + entry.getValue().toString() + "</option>");
                    continue;
                }
                Insurancelist.append("<option class=Inner value=\"" + entry.getKey() + "\"  >" + entry.getValue().toString() + "</option>");
            }
            HashMap<Integer, String> hm1 = new HashMap<>();
            hm1 = claim_ppt_list("a", conn);
            Set<Map.Entry<Integer, String>> set1 = hm1.entrySet();
            for (Map.Entry<Integer, String> entry1 : set1) {
                if (!patientstatus.equals("-") && entry1.getKey().equals(Integer.valueOf(Integer.parseInt(patientstatus)))) {
                    claimpptlist.append("<option class=Inner value=\"" + entry1.getKey() + "\" selected>" + entry1.getValue().toString() + "</option>");
                    continue;
                }
                claimpptlist.append("<option class=Inner value=\"" + entry1.getKey() + "\"  >" + entry1.getValue().toString() + "</option>");
            }
            HashMap<Integer, String> hm2 = new HashMap<>();
            hm2 = claim_status_list("a", conn);
            Set<Map.Entry<Integer, String>> set2 = hm2.entrySet();
            for (Map.Entry<Integer, String> entry2 : set2) {
                if (!claimstatus.equals("-") && entry2.getKey().equals(Integer.valueOf(Integer.parseInt(claimstatus)))) {
                    claimstatuslist.append("<option class=Inner value=\"" + entry2.getKey() + "\" selected>" + entry2.getValue().toString() + "</option>");
                    continue;
                }
                claimstatuslist.append("<option class=Inner value=\"" + entry2.getKey() + "\"  >" + entry2.getValue().toString() + "</option>");
            }
            Query = "Select Id,CovidStatus from CovidStatus";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                if (hrset.getString(1).equals(CovidStatus)) {
                    CovidList.append("<option class=Inner value='" + hrset.getString(1) + "' selected>" + hrset.getString(2) + "</option>");
                    continue;
                }
                CovidList.append("<option class=Inner value='" + hrset.getString(1) + "'>" + hrset.getString(2) + "</option>");
            }
            aa = "3";
            String textboxvalue = "<script>document.getElementById('Remarks').value = '" + Remarks + "';</script>";
            String targetName = "";
            String FileName = "";
            try {
                Query = "Select target, filename, dosdate from oe.filelogs_sftp " +
                        "where mrn = '" + mrn + "' and clientdirectory = " + clientid + " ";
                hstmt = conn.createStatement();
                hrset = hstmt.executeQuery(Query);
                while (hrset.next()) {
                    targetName = hrset.getString(1);
                    FileName = hrset.getString(2);
                    PdfList.append("<tr><td><a href=https://rovermd.com:8443/oe/oe.PatientChart?Action=download&fname=" + hrset.getString(2) + "&path=" + hrset.getString(1) + "&indexptr=" + indexptr + " target='PdfFiles'>" + hrset.getString(3) + "</a></td></tr>");
                    SNo++;
                }
                ChartsList.append("<div class=\"col-md-8\">\n<iframe id=\"PdfFiles\" name=\"PdfFiles\" src=\"https://rovermd.com:8443/oe/oe.PatientChart?Action=download_direct&path=" + targetName + "&indexptr=" + indexptr + "&fname=" + FileName + "&embedded=true\" frameborder=\"0\"  height=\"900px\" width=\"950\">\n</iframe>\n</div>");
            } catch (Exception e) {
                out.println(e.getMessage());
            }
            try {
                Query = "Select IFNULL(a.claimid,''), IFNULL(a.mrn,''), IFNULL(a.charges,''), " +
                        "IFNULL(a.claimamount,''), IFNULL(a.paymenttaken,''), IFNULL(a.chiofcomplaint,''),  " +
                        "IFNULL(a.cmdref,''), IFNULL(b.descname,''), IFNULL(c.descname,''), IFNULL(d.Name,''), " +
                        "IFNULL(e.CovidStatus,''), IFNULL(a.TimeOut,''),  IFNULL(LMailed,''), " +
                        "IFNULL(calledstatus,''), IFNULL(Remarks,'')  " +
                        "from  oe.claim_info_master_history a  " +
                        "LEFT JOIN claim_status_list b on a.claimstatus = b.Id  " +
                        "LEFT JOIN claim_ppt_list c on a.patientstatus = c.Id  " +
                        "LEFT JOIN Insurancelist d on a.insurance = d.id  " +
                        "LEFT JOIN CovidStatus e on a.CovidStatus = e.Id  " +
                        "where a.claimId = " + indexptr + " and mrn = '" + mrn + "'";
                hstmt = conn.createStatement();
                hrset = hstmt.executeQuery(Query);
                while (hrset.next()) {
                    if (hrset.getString(13).equals("1")) {
                        Mailed = "Mailed";
                    } else {
                        Mailed = "-";
                    }
                    if (hrset.getString(14).equals("1")) {
                        calledstatus = "Called";
                    } else {
                        calledstatus = "-";
                    }
                    ClaimHistory.append("<tr class=\"Inner\">\n");
                    ClaimHistory.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                    ClaimHistory.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                    ClaimHistory.append("<td align=left>" + hrset.getString(3) + "</td>\n");
                    ClaimHistory.append("<td align=left>" + hrset.getString(4) + "</td>\n");
                    ClaimHistory.append("<td align=left>" + hrset.getString(5) + "</td>\n");
                    ClaimHistory.append("<td align=left>" + hrset.getString(6) + "</td>\n");
                    ClaimHistory.append("<td align=left>" + hrset.getString(7) + "</td>\n");
                    ClaimHistory.append("<td align=left>" + hrset.getString(8) + "</td>\n");
                    ClaimHistory.append("<td align=left>" + hrset.getString(9) + "</td>\n");
                    ClaimHistory.append("<td align=left>" + hrset.getString(10) + "</td>\n");
                    ClaimHistory.append("<td align=left>" + hrset.getString(11) + "</td>\n");
                    ClaimHistory.append("<td align=left>" + hrset.getString(12) + "</td>\n");
                    ClaimHistory.append("<td align=left>" + Mailed + "</td>\n");
                    ClaimHistory.append("<td align=left>" + calledstatus + "</td>\n");
                    ClaimHistory.append("<td align=left>" + hrset.getString(15) + "</td>\n");
                    ClaimHistory.append("</tr>\n");
                }
                hrset.close();
                hstmt.close();
            } catch (Exception e) {
                out.println("Error in getting Claim History" + e.getMessage());
            }

            SNo = 1;
            Query = "SELECT a.FollowupRemarks,DATE_FORMAT(a.FollowupDateTime,'%d-%b-%Y'),a.FollowupEnterBy, \n" +
                    "a.MRN,b.`name`\n" +
                    "FROM oe.ClaimFollowups a \n" +
                    "INNER JOIN oe.clients b ON a.FacilityIdx = b.Id\n" +
                    "WHERE a.ClaimIdx = " + indexptr;
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                ClaimFollowup.append("<tr><td align=left>" + SNo + "</td>\n");
                ClaimFollowup.append("<td align=left>" + hrset.getString(1) + "</td>\n");//FollowupRemarks
                ClaimFollowup.append("<td align=left>" + hrset.getString(2) + "</td>\n");//FollowupDateTime
                ClaimFollowup.append("<td align=left>" + hrset.getString(3) + "</td>\n");//FollowupEnterBy
                ClaimFollowup.append("<td align=left>" + hrset.getInt(4) + "</td>\n");//MRN
                ClaimFollowup.append("<td align=left>" + hrset.getString(5) + "</td>\n");//Facility
                ClaimFollowup.append("</tr>");
                SNo++;
            }
            hrset.close();
            hstmt.close();

            StringBuffer NotesList = GetNotes(Integer.parseInt(indexptr), conn, mrn);
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
            Parser.SetField("PdfList", PdfList.toString());
            Parser.SetField("ChartsList", ChartsList.toString());
            Parser.SetField("CovidList", CovidList.toString());
            aa = "5";
            Parser.SetField("chiofcomplaint", chiofcomplaint.toString());
            Parser.SetField("cmdref", cmdref.toString());
            Parser.SetField("Remarks", Remarks.toString());
            Parser.SetField("Mailed", Mailed.toString());
            Parser.SetField("calledstatus", calledstatus.toString());
            Parser.SetField("paymenttaken", paymenttaken.toString());
            Parser.SetField("CovidStatus", CovidStatus.toString());
            Parser.SetField("TimeOut", TimeOut.toString());
            Parser.SetField("clientid", clientid.toString());
            Parser.SetField("NotesList", NotesList.toString());
            Parser.SetField("ClaimHistory", ClaimHistory.toString());
            Parser.SetField("ClaimFollowup", ClaimFollowup.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/AddinfoPatientChart.html");
        } catch (Exception e2) {
            out.println(aa + " Unable to process the request..." + e2.getMessage());
            String str = "";
            for (int j = 0; j < (e2.getStackTrace()).length; j++)
                str = str + e2.getStackTrace()[j] + "<br>";
            out.println(str);
        }
    }

    public void Addinfosave(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        StringBuffer Day = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Year = new StringBuffer();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        Statement hstmt2 = null;
        ResultSet hrset2 = null;
        String Query2 = "";
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
        String Mailed = request.getParameter("LMailed");
        String callstatus = request.getParameter("callstatus");
        String paymenttaken = request.getParameter("paymenttaken");
        String clientid = request.getParameter("clientid");
        String CovidStatus = request.getParameter("CovidStatus");
        String TimeOut = request.getParameter("TimeOut");
        String UserId = Services.GetCookie("UserId", request).trim();
        String userindex = Services.GetCookie("userindex", request).trim();
        String note = "";
        try {
            try {
                note = "update claim info";
                createnote(Integer.parseInt(userindex), note, Integer.parseInt(indexptr), conn, mrn);
                logging(Integer.parseInt(userindex), 3, Integer.parseInt(indexptr), conn);
                markuser(Integer.parseInt(userindex), 3, Integer.parseInt(indexptr), conn);
                Query2 = "delete from oe.claim_info_master where  claimid= '" + indexptr + "'";
                hstmt2 = conn.createStatement();
                hstmt2.executeUpdate(Query2);
                hstmt2.close();
                PreparedStatement MainReceipt1 = conn.prepareStatement("INSERT INTO claim_info_master(claimid,mrn,acc,firstname,lastname,phone,email,address,claimamount,charges,entrydate,claimstatus,patientstatus,insurance,chiofcomplaint,cmdref,Remarks,LMailed,createdby,dosdate,clientid,createddate, calledstatus, paymenttaken, CovidStatus, TimeOut) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?)");
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
                MainReceipt1.setString(22, callstatus);
                MainReceipt1.setString(23, paymenttaken);
                MainReceipt1.setString(24, CovidStatus);
                MainReceipt1.setString(25, TimeOut);
                MainReceipt1.executeUpdate();
                MainReceipt1.close();
                PreparedStatement MainReceipt2 = conn.prepareStatement("INSERT INTO claim_info_master_history(claimid,mrn,acc,firstname,lastname,phone,email,address,claimamount,charges,entrydate,claimstatus,patientstatus,insurance,chiofcomplaint,cmdref,Remarks,LMailed,createdby,createddate, calledstatus, paymenttaken, CovidStatus, TimeOut) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,? )");
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
                MainReceipt2.setString(20, callstatus);
                MainReceipt2.setString(21, paymenttaken);
                MainReceipt2.setString(22, CovidStatus);
                MainReceipt2.setString(23, TimeOut);
                MainReceipt2.executeUpdate();
                MainReceipt2.close();
                Query = " Update oe.filelogs_sftp   Set firstname  = '" + firstname.trim() + "' , lastname  = '" + lastname.trim() + "' , mrn  = '" + mrn.trim() + "' , Acc  = '" + Acc.trim() + "'  Where id ='" + indexptr.trim() + "'";
                hstmt = conn.createStatement();
                hstmt.executeUpdate(Query);
                conn.close();
            } catch (Exception ee) {
                out.println("Error is :" + ee.getMessage());
                String str = "";
                for (int i = 0; i < (ee.getStackTrace()).length; i++)
                    str = str + ee.getStackTrace()[i] + "<br>";
                out.println(str);
            }
            int nextindexptr = Integer.parseInt(indexptr) + 1;
            String htmlredirect = "<!DOCTYPE html><html><body><script>setTimeout(function(){ window.location.href = 'https://rovermd.com:8443/oe/oe.PatientChart?Action=Addinfo&indexptr=" + nextindexptr + "&mrn=" + mrn + "';}, 2000);</script><p>Record Updated. Please Wait</p></body></html>";
            out.println(htmlredirect);
        } catch (Exception e) {
            out.println("Unable to process the request...");
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
        StringBuffer listofstatus = new StringBuffer();
        try {
            ClientList.append("<option class=Inner value=\"-1\"> All </option>");
            HashMap<Integer, String> hm = new HashMap<>();
            hm = Clientlist("a", conn);
            Set<Map.Entry<Integer, String>> set = hm.entrySet();
            Iterator<Map.Entry<Integer, String>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry entry = it.next();
                ClientList.append("<option class=Inner value=\"" + entry.getKey() + "\">" + entry.getValue().toString() + "</option>");
            }
            HashMap<Integer, String> statuslistdrop = new HashMap<>();
            statuslistdrop = statuslist("aa", conn);
            for (int i = 0; i <= statuslistdrop.size(); i++)
                listofstatus.append("<option  value=" + i + "  >" + (String) statuslistdrop.get(Integer.valueOf(i)) + "</option>");
            Services.GetCalendar(Day, Month, Year);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DayList", Day.toString());
            Parser.SetField("MonthList", Month.toString());
            Parser.SetField("ClientList", ClientList.toString());
            Parser.SetField("listofstatus", listofstatus.toString());
            Parser.SetField("YearList", Year.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/PatientChartLog.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
        }
    }

    public void PatientChartReport_Input(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        StringBuffer Day = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Year = new StringBuffer();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer ClientList = new StringBuffer();
        StringBuffer claimstatuslist = new StringBuffer();
        StringBuffer UserList = new StringBuffer();
        StringBuffer FileStatusList = new StringBuffer();
        try {
            ClientList.append("<option class=Inner value=\"-1\"> All </option>");
            HashMap<Integer, String> hm = new HashMap<>();
            hm = Clientlist("a", conn);
            Set<Map.Entry<Integer, String>> set = hm.entrySet();
            Iterator<Map.Entry<Integer, String>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry entry = it.next();
                ClientList.append("<option class=Inner value=\"" + entry.getKey() + "\">" + entry.getValue().toString() + "</option>");
            }
            HashMap<Integer, String> hm2 = new HashMap<>();
            hm2 = claim_status_list("a", conn);
            Set<Map.Entry<Integer, String>> set2 = hm2.entrySet();
            Iterator<Map.Entry<Integer, String>> it2 = set2.iterator();
            while (it2.hasNext()) {
                Map.Entry entry2 = it2.next();
                claimstatuslist.append("<option class=Inner value=\"" + entry2.getKey() + "\"  >" + entry2.getValue().toString() + "</option>");
            }
            Query = "Select userid, username from sysusers";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next())
                UserList.append("<option class=Inner value=" + hrset.getString(1) + ">" + hrset.getString(2) + "</option>");
            hrset.close();
            hstmt.close();
            Query = "Select Id, name from filestatuslist";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next())
                FileStatusList.append("<option class=Inner value=" + hrset.getString(1) + ">" + hrset.getString(2) + "</option>");
            hrset.close();
            hstmt.close();
            Services.GetCalendar(Day, Month, Year);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DayList", Day.toString());
            Parser.SetField("MonthList", Month.toString());
            Parser.SetField("ClientList", ClientList.toString());
            Parser.SetField("claimstatus", claimstatuslist.toString());
            Parser.SetField("FileStatusList", FileStatusList.toString());
            Parser.SetField("UserList", UserList.toString());
            Parser.SetField("YearList", Year.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/PatientChartReport_Input.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            out.println(str);
        }
    }

    public void PatientChartReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query = "";
        String Query2 = "";
        DecimalFormat df2 = new DecimalFormat("#,###.00");
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        String UserId = Services.GetCookie("UserId", request).trim();
        String custid = request.getParameter("fileindex");
        String User = request.getParameter("User").trim();
        String claimstatus = request.getParameter("claimstatus");
        String filestatus = request.getParameter("filestatus");
        String AgentId = "-1";
        String dosdate = request.getParameter("dosdate");
        String DToDate = "";
        String DFromDate = "";
        String Condition1 = "";
        String Stage = "0";
        String clientname = null;
        String processby = null;
        String Id = "";
        String Mailed = "";
        String calledstatus = "";
        int SNo = 0;
        String UserCondition = "";
        String ClaimStatusCondition = "";
        String FileStatusCondition = "";
        try {
            Stage = "13e";
            DToDate = dosdate.substring(0, 19);
            DFromDate = dosdate.substring(22, 41);
            if (custid.compareTo("-1") == 0) {
                Condition1 = "";
            } else {
                Condition1 = " and a.clientdirectory='" + custid + "'";
            }
            if (User.equals("-1")) {
                UserCondition = " ";
            } else {
                UserCondition = " and b.createdby = '" + User + "' ";
            }
            if (claimstatus.equals("-1")) {
                ClaimStatusCondition = " ";
            } else {
                ClaimStatusCondition = " and b.claimStatus = '" + claimstatus + "' ";
            }
            if (filestatus.equals("-1")) {
                FileStatusCondition = " ";
            } else {
                FileStatusCondition = " and a.filestatus = '" + filestatus + "'";
            }
            Query = " Select a.id, a.dosdate, ifnull(a.firstname,''), ifnull(a.lastname,''), ifnull(c.Name,''), ifnull(b.address,''), ifnull(b.phone,''), ifnull(b.paymenttaken,''),  ifnull(b.claimamount,''), ifnull(d.descname,''), ifnull(b.createdby,''), ifnull(e.CovidStatus,''), ifnull(b.LMailed,''), ifnull(b.calledstatus,''), ifnull(b.Remarks,''),  ifnull(b.charges,''), ifnull(a.mrn,''), ifnull(f.name,'')   from oe.filelogs_sftp a  LEFT JOIN oe.claim_info_master b on a.Id = b.claimid  LEFT JOIN oe.Insurancelist c on b.insurance = c.id  LEFT JOIN claim_status_list d on b.claimstatus = d.Id  LEFT JOIN CovidStatus e on b.CovidStatus = e.Id  LEFT JOIN filestatuslist f on a.filestatus = f.Id  WHERE a.dosdate BETWEEN '" + DToDate + "' AND '" + DFromDate + "' " + Condition1 + UserCondition + ClaimStatusCondition + FileStatusCondition + " GROUP BY a.mrn, a.acc, a.dosdate order by dosdate";
            System.out.println(Query);
            stmt2 = conn.createStatement();
            rset2 = stmt2.executeQuery(Query);
            while (rset2.next()) {
                if (rset2.getString(13).equals("1")) {
                    Mailed = "<input type=\"checkbox\" id=\"LMailed\" name=\"LMailed\" value=\"1\" checked disabled>";
                } else {
                    Mailed = "<input type=\"checkbox\" id=\"LMailed\" name=\"LMailed\" value=\"1\" disabled>";
                }
                if (rset2.getString(14).equals("1")) {
                    calledstatus = "<input type=\"checkbox\" id=\"callstatus\" name=\"callstatus\" value=\"1\" checked disabled>";
                } else {
                    calledstatus = "<input type=\"checkbox\" id=\"callstatus\" name=\"callstatus\" value=\"1\" disabled>";
                }
                CDRList.append("<tr class=\"Inner\">\n");
                CDRList.append("<td align=center>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(7) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(8) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(9) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(10) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(11) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(12) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(16) + "</td>\n");
                CDRList.append("<td align=left>" + Mailed + "</td>\n");
                CDRList.append("<td align=left>" + calledstatus + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(15) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(18) + "</td>\n");
                CDRList.append("<td align=left><button type=\"button\" id=\"btnStep2\" class=\"btn btn-primary\" onclick=\"openNotesModal(" + rset2.getInt(1) + "," + rset2.getString(17) + ");\"> Notes </button></td>\n");
                CDRList.append("</tr>\n");
                SNo++;
            }
            stmt2.close();
            rset2.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", dosdate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/PatientChartReport.html");
        } catch (Exception e) {
            out.println(Stage + "Unable to process the request..." + e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            out.println(str);
        }
    }

    public void PatientStatusReport_Input(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        StringBuffer Day = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Year = new StringBuffer();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer ClientList = new StringBuffer();
        StringBuffer claimstatuslist = new StringBuffer();
        StringBuffer UserList = new StringBuffer();
        StringBuffer FileStatusList = new StringBuffer();
        try {
            ClientList.append("<option class=Inner value=\"-1\"> All </option>");
            HashMap<Integer, String> hm = new HashMap<>();
            hm = Clientlist("a", conn);
            Set<Map.Entry<Integer, String>> set = hm.entrySet();
            Iterator<Map.Entry<Integer, String>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry entry = it.next();
                ClientList.append("<option class=Inner value=\"" + entry.getKey() + "\">" + entry.getValue().toString() + "</option>");
            }
            HashMap<Integer, String> hm2 = new HashMap<>();
            hm2 = claim_status_list("a", conn);
            Set<Map.Entry<Integer, String>> set2 = hm2.entrySet();
            Iterator<Map.Entry<Integer, String>> it2 = set2.iterator();
            while (it2.hasNext()) {
                Map.Entry entry2 = it2.next();
                claimstatuslist.append("<option class=Inner value=\"" + entry2.getKey() + "\"  >" + entry2.getValue().toString() + "</option>");
            }
            Query = "Select userid, username from sysusers";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next())
                UserList.append("<option class=Inner value=" + hrset.getString(1) + ">" + hrset.getString(2) + "</option>");
            hrset.close();
            hstmt.close();
            Query = "Select Id, name from filestatuslist";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next())
                FileStatusList.append("<option class=Inner value=" + hrset.getString(1) + ">" + hrset.getString(2) + "</option>");
            hrset.close();
            hstmt.close();
            Services.GetCalendar(Day, Month, Year);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DayList", Day.toString());
            Parser.SetField("MonthList", Month.toString());
            Parser.SetField("ClientList", ClientList.toString());
            Parser.SetField("claimstatus", claimstatuslist.toString());
            Parser.SetField("FileStatusList", FileStatusList.toString());
            Parser.SetField("UserList", UserList.toString());
            Parser.SetField("YearList", Year.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/PatientStatusReport_Input.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            out.println(str);
        }
    }

    public void PatientStatusReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query = "";
        String Query2 = "";
        DecimalFormat df2 = new DecimalFormat("#,###.00");
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        String UserId = Services.GetCookie("UserId", request).trim();
        String custid = request.getParameter("fileindex");
        String User = request.getParameter("User").trim();
        String claimstatus = request.getParameter("claimstatus");
        String filestatus = request.getParameter("filestatus");
        String AgentId = "-1";
        String dosdate = request.getParameter("dosdate");
        String DToDate = "";
        String DFromDate = "";
        String Condition1 = "";
        String Stage = "0";
        String clientname = null;
        String processby = null;
        String Id = "";
        String Mailed = "";
        String calledstatus = "";
        int SNo = 0;
        String UserCondition = "";
        String ClaimStatusCondition = "";
        String FileStatusCondition = "";
        try {
            Stage = "13e";
            DToDate = dosdate.substring(0, 19);
            DFromDate = dosdate.substring(22, 41);
            if (custid.compareTo("-1") == 0) {
                Condition1 = "";
            } else {
                Condition1 = " and a.clientdirectory='" + custid + "'";
            }
            if (User.equals("-1")) {
                UserCondition = " ";
            } else {
                UserCondition = " and b.createdby = '" + User + "' ";
            }
            if (claimstatus.equals("-1")) {
                ClaimStatusCondition = " ";
            } else {
                ClaimStatusCondition = " and b.claimStatus = '" + claimstatus + "' ";
            }
            if (filestatus.equals("-1")) {
                FileStatusCondition = " ";
            } else {
                FileStatusCondition = " and a.filestatus = '" + filestatus + "'";
            }
            Query = " Select a.id, a.dosdate, ifnull(a.firstname,''), ifnull(a.lastname,''), ifnull(c.Name,''), ifnull(b.address,''), ifnull(b.phone,''), ifnull(b.paymenttaken,''),  ifnull(b.claimamount,''), ifnull(d.descname,''), ifnull(b.createdby,''), ifnull(e.CovidStatus,''), ifnull(b.LMailed,''), ifnull(b.calledstatus,''), ifnull(b.Remarks,''),  ifnull(b.charges,''), ifnull(a.mrn,''),ifnull(f.name,''), ifnull(b.createddate,'')   from oe.filelogs_sftp a  LEFT JOIN oe.claim_info_master b on a.Id = b.claimid  LEFT JOIN oe.Insurancelist c on b.insurance = c.id  LEFT JOIN claim_status_list d on b.claimstatus = d.Id  LEFT JOIN CovidStatus e on b.CovidStatus = e.Id  LEFT JOIN filestatuslist f on a.filestatus = f.Id  WHERE a.dosdate BETWEEN '" + DToDate + "' AND '" + DFromDate + "' " + Condition1 + UserCondition + ClaimStatusCondition + FileStatusCondition + " GROUP BY a.mrn, a.acc, a.dosdate order by dosdate";
            System.out.println(Query);
            stmt2 = conn.createStatement();
            rset2 = stmt2.executeQuery(Query);
            while (rset2.next()) {
                CDRList.append("<tr class=\"Inner\">\n");
                CDRList.append("<td align=center>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(10) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(18) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(19) + "</td>\n");
                CDRList.append("<td align=left><button type=\"button\" id=\"btnStep2\" class=\"btn btn-primary\" onclick=\"openNotesModal(" + rset2.getInt(1) + "," + rset2.getString(17) + ");\"> Notes </button></td>\n");
                CDRList.append("</tr>\n");
                SNo++;
            }
            stmt2.close();
            rset2.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", dosdate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/PatientStatusReport.html");
        } catch (Exception e) {
            out.println(Stage + "Unable to process the request..." + e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            out.println(str);
        }
    }

    public void updatestatus(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String mrn = "";
        String indexptr = request.getParameter("indexptr");
        String statusindex = request.getParameter("statusindex");
        String UserId = Services.GetCookie("UserId", request).trim();
        String userindex = Services.GetCookie("userindex", request).trim();
        try {
            Query = "Select MRN from filelogs_sftp where Id = " + indexptr;
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next())
                mrn = hrset.getString(1);
            hrset.close();
            hstmt.close();
            hstmt = conn.createStatement();
            Query = " Update oe.filelogs_sftp   Set filestatus  = '" + statusindex.trim() + "' , liststatus  = '" + statusindex.trim() + "' Where id ='" + indexptr.trim() + "'";
            hstmt.executeUpdate(Query);
            hstmt.close();
            String note = "Update File/ Claim Status";
            createnote(Integer.parseInt(userindex), "Update File/ Claim Status", Integer.parseInt(indexptr), conn, mrn);
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
        String Acc = request.getParameter("Acc");
        String Phone = request.getParameter("Phone");
        String email = request.getParameter("email");
        String insurance = request.getParameter("insurance");
        String cmdref = request.getParameter("cmdref");
        String clientid = request.getParameter("clientid");
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
            HashMap<Integer, String> clientlist = new HashMap<>();
            clientlist = Clientlist("aaa", conn);
            ToDate = ForDate.substring(0, 19);
            FromDate = ForDate.substring(22, 41);
            dToDate = dosdate.substring(0, 19);
            dFromDate = dosdate.substring(22, 41);
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
            Query = "select claimid,ifnull(firstname,''),ifnull(lastname,''),ifnull(dosdate,''),ifnull(mrn,''),ifnull(cmdref,''),ifnull(charges,''),ifnull(phone,''),ifnull(email,''),ifnull(address,'')\r\n,ifnull(createddate,''),createdby,ifnull(LMailed,''),clientid from claim_info_master where entrydate between '" + ToDate + "' and '" + FromDate + "'  " + PhoneCondition + " " + emailCondition + " " + firstnameCondition + " " + lastnameCondition + " " + mrnCondition + " " + "" + " " + clientCondition + "";
            String Created = "";
            hstmt = conn2.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                SNo++;
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
                if (hrset.getString(13).compareTo("1") == 0)
                    mailedstatus = "Sent";
                CDRList.append("<td align=left>" + mailedstatus + "</td>\n");
                CDRList.append("<td align=left>" + getclientname(clientlist, hrset.getInt(14)) + "</td>\n");
                CDRList.append("<td><a href=http://54.80.137.178:83/oe/oe.chartreport?Action=Addinfo&indexptr=" + hrset.getString(1) + "&mrn=" + hrset.getString(6) + " target='_blank'>View detatails</a></td>");
                CDRList.append("</tr>\n");
            }
            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", ForDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/showclaimdetails.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
        }
    }

    public void DashBoard(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, Connection conn2) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query2 = "";
        int TotalCallsToday = 0;
        int AnsweredCall = 0;
        int MissedCalls = 0;
        int OutboundCalls = 0;
        int CallBack = 0;
        int calltoday = 0;
        int todaynotrancation = 0;
        double todayamount = 0.0D;
        int todayerror = 0;
        int monthlynotrancation = 0;
        int monthlyerror = 0;
        double monthlyamount = 0.0D;
        int monthlycall = 0;
        String UserId = Services.GetCookie("UserId", request);
        UserId = UserId.substring(1);
        try {
            String pattern = "#,###.###";
            DecimalFormat decimalFormat = new DecimalFormat("#,###.###");
            decimalFormat.setGroupingSize(3);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("calltoday", String.valueOf(0));
            Parser.SetField("todaynotrancation", String.valueOf(0));
            Parser.SetField("todayamount", "$ " + String.valueOf(decimalFormat.format(0.0D)));
            Parser.SetField("todayerror", String.valueOf(0));
            Parser.SetField("monthlynotrancation", String.valueOf(0));
            Parser.SetField("monthlyerror", String.valueOf(0));
            Parser.SetField("monthlyamount", "$ " + String.valueOf(decimalFormat.format(0.0D)));
            Parser.SetField("monthlycall", String.valueOf(0));
            Parser.SetField("TotalCallsToday", String.valueOf(0));
            Parser.SetField("AnsweredCall", String.valueOf(0));
            Parser.SetField("MissedCalls", String.valueOf(0));
            Parser.SetField("OutboundCalls", String.valueOf(0));
            Parser.SetField("CallBack", String.valueOf(0));
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Dashboards/GraphicalMainDashboard.html");
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
        String Condition2 = "";
        int SNo = 0;
        try {
            ToDate = ForDate.substring(0, 19);
            FromDate = ForDate.substring(22, 41);
            if (cli.length() > 0)
                Condition = " and callerid like '%" + cli + "%' ";
            if (custid.length() > 0)
                Condition2 = " and customerid like '%" + custid + "%' ";
            Query = "select id,callerid,customerid,argument,json1,json2,parsedmsg,entrydate,amount,ifnull(file,'-'),substr(entrydate,1,10) from IVRLOG where entrydate between '" + ToDate + "' and '" + FromDate + "'  " + Condition + " " + Condition2 + " group by customerid order by entrydate ";
            String Created = "";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                String success = "0";
                success = getcount(hrset.getString(3), hrset.getString(11), conn);
                if (success.compareTo("0") == 0) {
                    SNo++;
                    String attempt = attemps(hrset.getString(3), hrset.getString(11), conn);
                    CDRList.append("<tr class=\"Inner\">\n");
                    CDRList.append("<td align=center>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + attempt + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(7) + "%</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(8) + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(9) + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(10) + "</td>\n");
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
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/showivrReport_error.html");
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
        String Condition2 = "";
        int SNo = 0;
        try {
            ToDate = ForDate.substring(0, 19);
            FromDate = ForDate.substring(22, 41);
            if (cli.length() > 0)
                Condition = " and callerid like '%" + cli + "%' ";
            if (custid.length() > 0)
                Condition2 = " and customerid like '%" + custid + "%' ";
            Query = "select id,callerid,customerid,balance,zipentered,cust_zip,entrydate,ifnull(uid,'-'),substr(entrydate,1,10) from IVRBALANCELOG where entrydate between '" + ToDate + "' and '" + FromDate + "'  " + Condition + " " + Condition2 + " group by customerid order by entrydate ";
            String Created = "";
            String Remarks = "";
            String bistatus = "Success";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                Remarks = "";
                String success = "0";
                bistatus = "Success";
                SNo++;
                String attempt = attemps(hrset.getString(3), hrset.getString(9), conn);
                CDRList.append("<tr class=\"Inner\">\n");
                CDRList.append("<td align=center>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(7) + "%</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(8) + "</td>\n");
                if (hrset.getString(3).length() <= 9)
                    Remarks = "Short Customer ID\n";
                if (hrset.getString(3).length() >= 12)
                    Remarks = Remarks + "To long Customer ID\n";
                if (hrset.getString(4).length() == 1) {
                    Remarks = Remarks + "Balance Inquiry Failed\n";
                    bistatus = "Failed";
                }
                if (hrset.getString(4).length() == 0) {
                    Remarks = Remarks + "Balance Inquiry Failed\n";
                    bistatus = "Failed";
                }
                CDRList.append("<td align=left>" + Remarks + "</td>\n");
                CDRList.append("<td align=left>" + bistatus + "</td>\n");
                CDRList.append("</tr>\n");
            }
            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", ForDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/showivrReport_balance.html");
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
        String Condition2 = "";
        String amount = "";
        int SNo = 0;
        try {
            ToDate = ForDate.substring(0, 19);
            FromDate = ForDate.substring(22, 41);
            Query = "select ROUND(sum(amount),2),parsedmsg,count(*) from IVRLOG  where entrydate between '" + ToDate + "' and '" + FromDate + "'   group by parsedmsg";
            String Created = "";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                SNo++;
                amount = hrset.getString(1);
                CDRList.append("<tr class=\"Inner\">\n");
                CDRList.append("<td align=center>" + SNo + "</td>\n");
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
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/showivrReportsum.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
        }
    }

    public StringBuffer insurancecompanylist(Connection conn) {
        return null;
    }
}
