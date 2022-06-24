// 
// Decompiled by Procyon v0.5.36
// 

package oe;

import Parsehtm.Parsehtm;
import org.apache.xmlbeans.impl.util.Base64;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
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

//import ca.uhn.hl7v2.model.Message;
/*import ca.uhn.hl7v2.validation.ValidationContext;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import ca.uhn.hl7v2.parser.PipeParser;*/
//import org.apache.commons.codec.binary.Base64;

@SuppressWarnings("Duplicates")
public class PatientChart6AUG2021 extends HttpServlet {
    public String host;

    public PatientChart6AUG2021() {
        this.host = "http://ourenergyllc.com";
    }

    public static String getOnlyDigits(final String s) {
        final Pattern pattern = Pattern.compile("[^0-9.]");
        final Matcher matcher = pattern.matcher(s);
        final String number = matcher.replaceAll("");
        return number;
    }

    public static String getclientname(final HashMap clientlist, final int clientIndex) {
        String clientname = null;
        Set set = clientlist.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            // out.println(entry.getKey() + ":" + entry.getValue()+"\n");
            if (entry.getKey().equals(clientIndex)) {
                // out.println(entry.getKey() + ":" + entry.getValue());
                clientname = entry.getValue().toString();
            }
        }
        return clientname;
    }

    public static String filestatuslist(final HashMap clientlist, final int clientIndex, final int indexptr) {
        final StringBuffer listofstatus = new StringBuffer();
        listofstatus.append("<select id=\"StatusAction\" onChange=\"updatestatus(" + indexptr + "\\" + clientlist.toString() + ")\">");
        for (int i = 0; i < clientlist.size(); ++i) {
            if (i == indexptr) {
                listofstatus.append("<option  value=" + i + " selected >" + clientlist.get(i) + "</option>");
            } else {
                listofstatus.append("<option  value=" + i + "  >" + clientlist.get(i) + "</option>");
            }
        }
        return listofstatus.toString();
    }

    public static HashMap<Integer, String> Clientlist(final String aa, final Connection Conn) {
        final HashMap<Integer, String> hm = new HashMap<Integer, String>();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,name from clients where status=0 ";//and Id not in (9,10)";
            hstmt = Conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
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

    public static HashMap<Integer, String> Insurancelist(final String aa, final Connection Conn) {
        final HashMap<Integer, String> hm = new HashMap<Integer, String>();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,Name from Insurancelist where status=0";
            hstmt = Conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
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

    public static HashMap<Integer, String> claim_status_list(final String aa, final Connection Conn) {
        final HashMap<Integer, String> hm = new HashMap<Integer, String>();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,descname from claim_status_list where status=0";
            hstmt = Conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
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

    public static HashMap<Integer, String> claim_ppt_list(final String aa, final Connection Conn) {
        final HashMap<Integer, String> hm = new HashMap<Integer, String>();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,descname from claim_ppt_list where status=0";
            hstmt = Conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
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

    public static HashMap<Integer, String> Userlist(final String aa, final Connection Conn) {
        final HashMap<Integer, String> hm = new HashMap<Integer, String>();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select indexptr,username from sysusers where enabled='Y'";
            hstmt = Conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
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

    public static HashMap<Integer, String> statuslist(final String aa, final Connection Conn) {
        final HashMap<Integer, String> hm = new HashMap<Integer, String>();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,name from filestatuslist where status='0'";
            hstmt = Conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
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

    public static StringBuffer GetNotes(final int claimid, final Connection conn, final String mrn) {
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query2 = "";
        final StringBuffer CDRList = new StringBuffer();
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
                    ++SNo;
                }
            }
            rset2.close();
            stmt2.close();
        } catch (Exception ee) {
            System.out.println("Error in getting notes: " + ee.getMessage());
        }
        return CDRList;
    }

    public static void GetNotes_AJAX(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query2 = "";
        final StringBuffer CDRList = new StringBuffer();
        final String claimid = request.getParameter("claimid");
        final String mrn = request.getParameter("mrn");
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
                    ++SNo;
                }
            }
            rset2.close();
            stmt2.close();
            out.println(CDRList);
        } catch (Exception ee) {
            System.out.println("Error in getting notes: " + ee.getMessage());
        }
    }

    public static String logging(final int userindex, final int filestatus, final int indexptr, final Connection conn) {
        Statement hstmt = null;
        final ResultSet hrset = null;
        String Query = "";
        try {
            hstmt = conn.createStatement();
            Query = " insert into fileactivity(fileindex,created,userindex,filestatus)  values('" + indexptr + "',now()," + userindex + ",'" + filestatus + "') ";
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
            Query = " insert into claim_note(note,userindex,createddate,claimid, mrn)  values('" + note + "','" + userindex + "',now(), '" + claimid + "', '" + mrn + "') ";
            hstmt.execute(Query);
        } catch (Exception ex) {
        }
        return null;
    }

    public static String markuser(final int userindex, final int filestatus, final int indexptr, final Connection conn) {
        Statement hstmt = null;
        final ResultSet hrset = null;
        String Query = "";
        try {
            hstmt = conn.createStatement();
            Query = " update  filelogs_sftp set processby=" + userindex + " where processby=0 and id=" + indexptr;
            hstmt.execute(Query);
        } catch (Exception ex) {
        }
        return null;
    }

    public static boolean CheckDates(final String FromDate, final String ToDate, final Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int Days = 0;
        try {
            Query = " select unix_timestamp('" + FromDate + "') - unix_timestamp('" + ToDate + "') ";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            hrset.next();
            final boolean Valid = hrset.getInt(1) <= 0;
            hrset.close();
            hstmt.close();
            return Valid;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getcount(final String cid, final String day, final Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int Days = 0;
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

    public static String attemps(final String cid, final String day, final Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int Days = 0;
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
        Connection conn2 = null;
        String Action = null;
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        response.setContentType("text/html");
        String Stage = "";
        try {
            if (request.getParameter("Action") == null && request.getContentType().startsWith("multipart/form-data")) {
                Action = "Step2";
            } else {
                Action = request.getParameter("Action");
            }
            if (Action.compareTo("download_direct") != 0) {
                final boolean ValidSession = Services.checkSession(out, request);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    conn.close();
                    return;
                }
            }
            final String UserId = Services.GetCookie("UserId", request);
            if (UserId == "") {
                out.println("Your session has been expired, please login again.");
                out.flush();
                return;
            }
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Stage = "1";
            conn = Services.GetConnection(this.getServletContext(), 1);
            conn2 = Services.GetConnection(this.getServletContext(), 1);
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
                this.showchartdetails_new(request, response, out, conn);
            } else if (Action.compareTo("PatientChartReport_Input") == 0) {
                this.PatientChartReport_Input(request, response, out, conn);
            } else if (Action.compareTo("PatientChartReport") == 0) {
                this.PatientChartReport(request, response, out, conn);
            } else if (Action.compareTo("showchartlogs") == 0) {
                this.showchartlog(request, response, out, conn);
            } else if (Action.compareTo("Addinfo") == 0) {
                this.Addinfo(request, response, out, conn);
            } else if (Action.compareTo("download") == 0) {
                this.download(request, response, out, conn);
            } else if (Action.compareTo("download_direct") == 0) {
                this.download_direct(request, response, out, conn);
            } else if (Action.compareTo("Addinfosave") == 0) {
                this.Addinfosave(request, response, out, conn);
            } else if (Action.compareTo("updatestatus") == 0) {
                this.updatestatus(request, response, out, conn);
            } else if (Action.compareTo("GetNotes_AJAX") == 0) {
                GetNotes_AJAX(request, response, out, conn);
            } else if (Action.compareTo("PatientStatusReport_Input") == 0) {
                this.PatientStatusReport_Input(request, response, out, conn);
            } else if (Action.compareTo("PatientStatusReport") == 0) {
                this.PatientStatusReport(request, response, out, conn);
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

    public void download1(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        final String fileindex = request.getParameter("fname");
        final String cid = "";
        final String link = "";
        final String Customername = "";
        final String extdate = "";
        final String dncid = "";
        final String zipcode = "";
        final String premise_id = "";
        final String email = "";
        try {
            final String stringUrl = "https://app.smartfile.com/api/2/path/data/shares/FED%20Accounts/Physicians-Premier-Bastrop/Charts/459_37259_201910191546_201910231416_.pdf?download=true&ui=1";
            final URL url = new URL(stringUrl);
            final URLConnection uc = url.openConnection();
            System.out.println(stringUrl);
            uc.setRequestProperty("X-Requested-With", "Curl");
            uc.setRequestProperty("Content-Type", "application/json");
            final String userpass = "username:password";
            final String userCredentials = "DlYTlsOY249jBise60b9y4r0emCXqB:dpgwmrFiJJnPojMNnkS14wgKTJjSIo";
            final String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
            uc.setRequestProperty("Authorization", basicAuth);
            uc.setRequestProperty("postman-token", "9da9db4f-8248-e387-0fdd-018c98cb6f92");
            uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
            final BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            out.println(in.toString());
            String line = in.readLine();
            final StringBuilder sb = new StringBuilder();
            while (line != null) {
                line = in.readLine();
            }
            final String fileAsString = sb.toString();
            out.println(fileAsString);
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
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
        final String UserId = Services.GetCookie("UserId", request).trim();
        final String userindex = Services.GetCookie("userindex", request).trim();
        try {
            final String[] mrnArr = FileName.split("\\_");
            final String mrn = mrnArr[1];
            System.out.println(mrn + " :MRN");
            final String note = "Download pdf charts";
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
        final String userindex = Services.GetCookie("userindex", request).trim();
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

    /* public void hl7chart(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
         final String FileName = request.getParameter("fname");
         final String path = request.getParameter("path");
         final String indexptr = request.getParameter("indexptr");
         final String RecordingPath = path + FileName;
         try {
             final String Filname = path + "" + FileName;
             final InputStream is = new FileInputStream(Filname);
             final BufferedReader buf = new BufferedReader(new InputStreamReader(is));
             String line = buf.readLine();
             final StringBuilder sb = new StringBuilder();
             while (line != null) {
                 sb.append(line).append("\r\n");
                 line = buf.readLine();
             }
             final String fileAsString = sb.toString();
             final PipeParser ourPipeParser = new PipeParser();
             ourPipeParser.setValidationContext((ValidationContext)new NoValidation());
             final Message hl7Message = ourPipeParser.parse(fileAsString.trim());
             Hl7dft4_clean.OBXfinal.delete(0, Hl7dft4_clean.OBXfinal.length());
             Hl7dft4_clean.PV1final.delete(0, Hl7dft4_clean.PV1final.length());
             Hl7dft4_clean.IN1final.delete(0, Hl7dft4_clean.IN1final.length());
             Hl7dft4_clean.PIDfinal.delete(0, Hl7dft4_clean.PIDfinal.length());
             Hl7dft4_clean.DGIfinal.delete(0, Hl7dft4_clean.DGIfinal.length());
             Hl7dft4_clean.FTIfinal.delete(0, Hl7dft4_clean.FTIfinal.length());
             Hl7dft4_clean.GT1final.delete(0, Hl7dft4_clean.GT1final.length());
             Hl7dft4_clean.extractValues(hl7Message);
             final Parsehtm Parser = new Parsehtm(request);
             Parser.SetField("PIDfinal", Hl7dft4_clean.PIDfinal.toString());
             Parser.SetField("PV1final", Hl7dft4_clean.PV1final.toString());
             Parser.SetField("IN1final", Hl7dft4_clean.IN1final.toString());
             Parser.SetField("FTIfinal", Hl7dft4_clean.FTIfinal.toString());
             Parser.SetField("DGIfinal", Hl7dft4_clean.DGIfinal.toString());
             Parser.SetField("OBXfinal", Hl7dft4_clean.OBXfinal.toString());
             Parser.SetField("GT1final", Hl7dft4_clean.GT1final.toString());
             Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/HL7chartReport.html");
         }
         catch (Exception e) {
             out.println("Unable to process request ..." + e.getMessage());
         }
     }
     */
    public void openpdf(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        final String FileName = request.getParameter("fname");
        final String path = request.getParameter("path");
        final String indexptr = request.getParameter("indexptr");
        final String RecordingPath = path + FileName;
        final String UserId = Services.GetCookie("UserId", request).trim();
        final String userindex = Services.GetCookie("userindex", request).trim();
        try {
            final String file = "http://54.80.137.178:83/oe/oe.chartreport?Action=download&fname=" + FileName + "&path=" + path + "&indexptr=" + indexptr;
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("file", file.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/openpdf.html");
        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }

    public void showchartdetails_new(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final DecimalFormat df2 = new DecimalFormat("#,###.00");
        final StringBuffer CDRList = new StringBuffer();
        final StringBuffer Response = new StringBuffer();
        final String UserId = Services.GetCookie("UserId", request).trim();
        final String custid = request.getParameter("fileindex");
        final String AgentId = "-1";
        final String dosdate = request.getParameter("dosdate");
        String DToDate = "";
        String DFromDate = "";
        String Condition1 = "";
        String Stage = "0";
        final String clientname = null;
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
            Query = " SELECT id,entrydate, clientdirectory, acc, dosdate,substr(epowertime,1,16), processed, processby, " +
                    "filestatus, liststatus, ifnull(firstname,'-'), ifnull(lastname,'-'), ifnull(mrn,'-') " +
                    "FROM filelogs_sftp " +
                    "WHERE dosdate BETWEEN '" + DToDate + "' AND '" + DFromDate + "' " + " " + Condition1 + " " +
                    "GROUP BY mrn,acc,dosdate ORDER BY dosdate";
            final String Created = "";
            HashMap<Integer, String> clientlist = new HashMap<Integer, String>();
            clientlist = Clientlist("aaa", conn);
            HashMap<Integer, String> userlist = new HashMap<Integer, String>();
            userlist = Userlist("aaa", conn);
            HashMap<Integer, String> statuslistdrop = new HashMap<Integer, String>();
            statuslistdrop = statuslist("aa", conn);
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                ++SNo;
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
                final StringBuffer listofstatus = new StringBuffer();
                listofstatus.append("<select id=\"StatusAction" + hrset.getInt(1) + "\" onChange=\"updatestatus(" + hrset.getInt(1) + ")\">");
                for (int i = 0; i <= statuslistdrop.size(); ++i) {
                    if (i == hrset.getInt(10)) {
                        listofstatus.append("<option  value=" + i + " selected >" + statuslistdrop.get(i) + "</option>");
                    } else {
                        listofstatus.append("<option  value=" + i + "  >" + statuslistdrop.get(i) + "</option>");
                    }
                }
                CDRList.append("<td align=left><div id=\"cstatus" + Id + "\" >" + getclientname(statuslistdrop, hrset.getInt(9)) + "</div></td>\n");
                CDRList.append("<td align=left>" + (Object) listofstatus + "</td>\n");
                Stage = "0111";
                Stage = "2";
                CDRList.append("<td><a href=https://rovermd.com:8443/oe/oe.PatientChart6AUG2021?Action=Addinfo&indexptr=" + hrset.getString(1) + "&mrn=" + hrset.getString(13) + " target='_blank'>Add info</a></td>");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                CDRList.append("</tr>\n");
            }
            hrset.close();
            hstmt.close();
            Stage = "3";
            CDRList.append("</table>\n");
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", dosdate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showPatientChartReport.html");
        } catch (Exception e) {
            out.println(Stage + "Unable to process the request..." + e.getMessage());
        }
    }

    public void Addinfo(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        final StringBuffer Day = new StringBuffer();
        final StringBuffer Month = new StringBuffer();
        final StringBuffer Year = new StringBuffer();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final String indexptr = request.getParameter("indexptr");
        String mrn = request.getParameter("mrn");
        String firstname = "";
        String lastname = "";
        String Acc = "";
        String dosdate = "";
        String filename = "";
        String target = "";
        String entrydate = "";
        String clientid = "";
        final String ChargeMasterTableName = "";
        final StringBuffer Insurancelist = new StringBuffer();
        final StringBuffer claimstatuslist = new StringBuffer();
        final StringBuffer claimpptlist = new StringBuffer();
        final StringBuffer PatientStatus = new StringBuffer();
        final StringBuffer PdfList = new StringBuffer();
        final StringBuffer ChartsList = new StringBuffer();
        final StringBuffer CovidList = new StringBuffer();
        final String userindex = Services.GetCookie("userindex", request).trim();
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
        String aa = "0";
        int SNo = 1;
        try {
            note = "Open Claim info and load pdf";
            createnote(Integer.parseInt(userindex), note, Integer.parseInt(indexptr), conn, mrn);
            logging(Integer.parseInt(userindex), 2, Integer.parseInt(indexptr), conn);
            markuser(Integer.parseInt(userindex), 2, Integer.parseInt(indexptr), conn);
            Query = "select Id,target,entrydate,clientdirectory,filename,acc,dosdate,epowertime,processed,processby,filestatus,liststatus,ifnull(firstname,'-'),ifnull(lastname,'-'),ifnull(mrn,'-') from filelogs_sftp where Id=" + indexptr;
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
            Query = "select ifnull(phone,''),ifnull(email,''),ifnull(address,''),ifnull(claimamount,''),ifnull(charges,''),ifnull(dosdate,''),ifnull(entrydate,''),ifnull(claimstatus,''),ifnull(patientstatus,''),ifnull(insurance,''),ifnull(chiofcomplaint,''),ifnull(cmdref,''),ifnull(Remarks,''),ifnull(LMailed,''),ifnull(createddate,''),createdby, ifnull(calledstatus,''), ifnull(paymenttaken,''), ifnull(CovidStatus,'0')  from claim_info_master where claimid=" + indexptr;
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
            Query = "Select Id,CovidStatus from CovidStatus";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                if (hrset.getString(1).equals(CovidStatus)) {
                    CovidList.append("<option class=Inner value='" + hrset.getString(1) + "' selected>" + hrset.getString(2) + "</option>");
                } else {
                    CovidList.append("<option class=Inner value='" + hrset.getString(1) + "'>" + hrset.getString(2) + "</option>");
                }
            }
            aa = "3";
            final String textboxvalue = "<script>document.getElementById('Remarks').value = '" + Remarks + "';</script>";
            try {
                Query = "Select target, filename from oe.filelogs_sftp where mrn = '" + mrn + "'";
                hstmt = conn.createStatement();
                hrset = hstmt.executeQuery(Query);
                while (hrset.next()) {
                    PdfList.append("<tr><td align=left>" + SNo + "</td>\n");
                    PdfList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                    PdfList.append("<td><a href=https://rovermd.com:8443/oe/oe.PatientChart6AUG2021?Action=download&fname=" + hrset.getString(2) + "&path=" + hrset.getString(1) + "&indexptr=" + indexptr + " target='_blank'>download</a></td>");
                    ChartsList.append("<div class=\"col-md-6\">\n<iframe src=\"https://rovermd.com:8443/oe/oe.PatientChart6AUG2021?Action=download_direct&path=" + hrset.getString(1) + "&indexptr=" + indexptr + "&fname=" + hrset.getString(2) + "&embedded=true\" frameborder=\"0\"  height=\"900px\" width=\"800\">\n" + "</iframe>\n" + "</div>");
                    ++SNo;
                }
            } catch (Exception e) {
                out.println(e.getMessage());
            }
            final StringBuffer NotesList = GetNotes(Integer.parseInt(indexptr), conn, mrn);
            Services.GetCalendar(Day, Month, Year);
            final Parsehtm Parser = new Parsehtm(request);
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
            Parser.SetField("clientid", clientid.toString());
            Parser.SetField("NotesList", NotesList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/AddinfoPatientChart.html");
        } catch (Exception e2) {
            out.println(aa + " Unable to process the request..." + e2.getMessage());
        }
    }

    public void Addinfosave(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        final StringBuffer Day = new StringBuffer();
        final StringBuffer Month = new StringBuffer();
        final StringBuffer Year = new StringBuffer();
        Statement hstmt = null;
        final ResultSet hrset = null;
        String Query = "";
        Statement hstmt2 = null;
        final ResultSet hrset2 = null;
        String Query2 = "";
        final String indexptr = request.getParameter("indexptr");
        final String mrn = request.getParameter("mrn");
        final String firstname = request.getParameter("firstname");
        final String lastname = request.getParameter("lastname");
        final String dosdate = request.getParameter("dosdate");
        final String entrydate = request.getParameter("entrydate");
        final String Acc = request.getParameter("Acc");
        final String Phone = request.getParameter("Phone");
        final String email = request.getParameter("email");
        final String address = request.getParameter("address");
        final String charges = request.getParameter("charges");
        final String claimamount = request.getParameter("claimamount");
        final String claimstatus = request.getParameter("claimstatus");
        final String patientstatus = request.getParameter("patientstatus");
        final String insurance = request.getParameter("insurance");
        final String chiofcomplaint = request.getParameter("chiofcomplaint");
        final String cmdref = request.getParameter("cmdref");
        final String Remarks = request.getParameter("Remarks");
        final String Mailed = request.getParameter("LMailed");
        final String callstatus = request.getParameter("callstatus");
        final String paymenttaken = request.getParameter("paymenttaken");
        final String clientid = request.getParameter("clientid");
        final String CovidStatus = request.getParameter("CovidStatus");
        final String UserId = Services.GetCookie("UserId", request).trim();
        final String userindex = Services.GetCookie("userindex", request).trim();
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
                final PreparedStatement MainReceipt1 = conn.prepareStatement("INSERT INTO claim_info_master(claimid,mrn,acc,firstname,lastname,phone,email,address,claimamount,charges,entrydate,claimstatus,patientstatus,insurance,chiofcomplaint,cmdref,Remarks,LMailed,createdby,dosdate,clientid,createddate, calledstatus, paymenttaken, CovidStatus) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?)");
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
                MainReceipt1.executeUpdate();
                MainReceipt1.close();
                final PreparedStatement MainReceipt2 = conn.prepareStatement("INSERT INTO claim_info_master_history(claimid,mrn,acc,firstname,lastname,phone,email,address,claimamount,charges,entrydate,claimstatus,patientstatus,insurance,chiofcomplaint,cmdref,Remarks,LMailed,createdby,createddate, calledstatus, paymenttaken, CovidStatus) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,? )");
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
                MainReceipt2.executeUpdate();
                MainReceipt2.close();
                Query = " Update oe.filelogs_sftp   Set firstname  = '" + firstname.trim() + "'" + " , lastname  = '" + lastname.trim() + "'" + " , mrn  = '" + mrn.trim() + "'" + " , Acc  = '" + Acc.trim() + "'" + " Where id ='" + indexptr.trim() + "'";
                hstmt = conn.createStatement();
                hstmt.executeUpdate(Query);
                conn.close();
            } catch (Exception ee) {
                out.println("Error is :" + ee.getMessage());
                String str = "";
                for (int i = 0; i < ee.getStackTrace().length; ++i) {
                    str = str + ee.getStackTrace()[i] + "<br>";
                }
                out.println(str);
            }
            final int nextindexptr = Integer.parseInt(indexptr) + 1;
            final String htmlredirect = "<!DOCTYPE html><html><body><script>setTimeout(function(){ window.location.href = 'https://rovermd.com:8443/oe/oe.PatientChart6AUG2021?Action=Addinfo&indexptr=" + indexptr + "&mrn=" + mrn + "';}, 2000);</script><p>Record Updated. Please Wait</p></body></html>";
            out.println(htmlredirect);
        } catch (Exception e) {
            out.println("Unable to process the request...");
        }
    }

    public void showchartlog(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        final StringBuffer Day = new StringBuffer();
        final StringBuffer Month = new StringBuffer();
        final StringBuffer Year = new StringBuffer();
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        final StringBuffer ClientList = new StringBuffer();
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

            String CurrDate = "";
            Query = "SELECT DATE_FORMAT(NOW(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                CurrDate = rset.getString(1).trim();
            rset.close();
            stmt.close();

            CurrDate = CurrDate + " 00:00:00" + " - " + CurrDate + " 23:59:59";

            Services.GetCalendar(Day, Month, Year);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DayList", Day.toString());
            Parser.SetField("MonthList", Month.toString());
            Parser.SetField("ClientList", ClientList.toString());
            Parser.SetField("YearList", Year.toString());
            Parser.SetField("CurrDate", CurrDate);
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/PatientChartLog.html");
        } catch (Exception e) {
            out.println("Unable to process the request...");
        }
    }

    public void PatientChartReport_Input(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        final StringBuffer Day = new StringBuffer();
        final StringBuffer Month = new StringBuffer();
        final StringBuffer Year = new StringBuffer();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final StringBuffer ClientList = new StringBuffer();
        final StringBuffer claimstatuslist = new StringBuffer();
        final StringBuffer UserList = new StringBuffer();
        final StringBuffer FileStatusList = new StringBuffer();
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

            HashMap<Integer, String> hm2 = new HashMap<Integer, String>();
            hm2 = claim_status_list("a", conn);
            Set set2 = hm2.entrySet();
            Iterator it2 = set2.iterator();
            while (it2.hasNext()) {
                Map.Entry entry2 = (Map.Entry) it2.next();
                claimstatuslist.append("<option class=Inner value=\"" + entry2.getKey() + "\"  >" + entry2.getValue().toString() + "</option>");
            }
            Query = "Select userid, username from sysusers";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                UserList.append("<option class=Inner value=" + hrset.getString(1) + ">" + hrset.getString(2) + "</option>");
            }
            hrset.close();
            hstmt.close();
            Query = "Select Id, name from filestatuslist";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                FileStatusList.append("<option class=Inner value=" + hrset.getString(1) + ">" + hrset.getString(2) + "</option>");
            }
            hrset.close();
            hstmt.close();
            Services.GetCalendar(Day, Month, Year);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DayList", Day.toString());
            Parser.SetField("MonthList", Month.toString());
            Parser.SetField("ClientList", ClientList.toString());
            Parser.SetField("claimstatus", claimstatuslist.toString());
            Parser.SetField("FileStatusList", FileStatusList.toString());
            Parser.SetField("UserList", UserList.toString());
            Parser.SetField("YearList", Year.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/PatientChartReport_Input.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    public void PatientChartReport(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query = "";
        final String Query2 = "";
        final DecimalFormat df2 = new DecimalFormat("#,###.00");
        final StringBuffer CDRList = new StringBuffer();
        final StringBuffer Response = new StringBuffer();
        final String UserId = Services.GetCookie("UserId", request).trim();
        final String custid = request.getParameter("fileindex");
        final String User = request.getParameter("User").trim();
        final String claimstatus = request.getParameter("claimstatus");
        final String filestatus = request.getParameter("filestatus");
        final String AgentId = "-1";
        final String dosdate = request.getParameter("dosdate");
        String DToDate = "";
        String DFromDate = "";
        String Condition1 = "";
        String Stage = "0";
        final String clientname = null;
        final String processby = null;
        final String Id = "";
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
            Query = " Select a.id, a.dosdate, ifnull(a.firstname,''), ifnull(a.lastname,''), ifnull(c.Name,''), ifnull(b.address,''), " +
                    "ifnull(b.phone,''), ifnull(b.paymenttaken,''),  ifnull(b.claimamount,''), ifnull(d.descname,''), " +
                    "ifnull(b.createdby,''), ifnull(e.CovidStatus,''), ifnull(b.LMailed,''), ifnull(b.calledstatus,''), " +
                    "ifnull(b.Remarks,''),  ifnull(b.charges,''), ifnull(a.mrn,''), ifnull(f.name,'')   " +
                    "from oe.filelogs_sftp a  " +
                    "LEFT JOIN oe.claim_info_master b on a.Id = b.claimid  " +
                    "LEFT JOIN oe.Insurancelist c on b.insurance = c.id  " +
                    "LEFT JOIN claim_status_list d on b.claimstatus = d.Id  " +
                    "LEFT JOIN CovidStatus e on b.CovidStatus = e.Id  " +
                    "LEFT JOIN filestatuslist f on a.filestatus = f.Id  " +
                    "WHERE a.dosdate BETWEEN '" + DToDate + "' AND '" + DFromDate + "' " + Condition1 + UserCondition + ClaimStatusCondition + FileStatusCondition + " GROUP BY a.mrn, a.acc, a.dosdate order by dosdate";
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
                ++SNo;
            }
            stmt2.close();
            rset2.close();
            CDRList.append("</table>\n");
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", dosdate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/PatientChartReport.html");
        } catch (Exception e) {
            out.println(Stage + "Unable to process the request..." + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    public void PatientStatusReport_Input(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        final StringBuffer Day = new StringBuffer();
        final StringBuffer Month = new StringBuffer();
        final StringBuffer Year = new StringBuffer();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final StringBuffer ClientList = new StringBuffer();
        final StringBuffer claimstatuslist = new StringBuffer();
        final StringBuffer UserList = new StringBuffer();
        final StringBuffer FileStatusList = new StringBuffer();
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

            HashMap<Integer, String> hm2 = new HashMap<Integer, String>();
            hm2 = claim_status_list("a", conn);
            Set set2 = hm2.entrySet();
            Iterator it2 = set2.iterator();
            while (it2.hasNext()) {
                Map.Entry entry2 = (Map.Entry) it2.next();
                claimstatuslist.append("<option class=Inner value=\"" + entry2.getKey() + "\"  >" + entry2.getValue().toString() + "</option>");
            }
            Query = "Select userid, username from sysusers";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                UserList.append("<option class=Inner value=" + hrset.getString(1) + ">" + hrset.getString(2) + "</option>");
            }
            hrset.close();
            hstmt.close();
            Query = "Select Id, name from filestatuslist";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                FileStatusList.append("<option class=Inner value=" + hrset.getString(1) + ">" + hrset.getString(2) + "</option>");
            }
            hrset.close();
            hstmt.close();
            Services.GetCalendar(Day, Month, Year);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DayList", Day.toString());
            Parser.SetField("MonthList", Month.toString());
            Parser.SetField("ClientList", ClientList.toString());
            Parser.SetField("claimstatus", claimstatuslist.toString());
            Parser.SetField("FileStatusList", FileStatusList.toString());
            Parser.SetField("UserList", UserList.toString());
            Parser.SetField("YearList", Year.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/PatientStatusReport_Input.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    public void PatientStatusReport(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query = "";
        final String Query2 = "";
        final DecimalFormat df2 = new DecimalFormat("#,###.00");
        final StringBuffer CDRList = new StringBuffer();
        final StringBuffer Response = new StringBuffer();
        final String UserId = Services.GetCookie("UserId", request).trim();
        final String custid = request.getParameter("fileindex");
        final String User = request.getParameter("User").trim();
        final String claimstatus = request.getParameter("claimstatus");
        final String filestatus = request.getParameter("filestatus");
        final String AgentId = "-1";
        final String dosdate = request.getParameter("dosdate");
        String DToDate = "";
        String DFromDate = "";
        String Condition1 = "";
        String Stage = "0";
        final String clientname = null;
        final String processby = null;
        final String Id = "";
        final String Mailed = "";
        final String calledstatus = "";
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
                ++SNo;
            }
            stmt2.close();
            rset2.close();
            CDRList.append("</table>\n");
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", dosdate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/PatientStatusReport.html");
        } catch (Exception e) {
            out.println(Stage + "Unable to process the request..." + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    public void updatestatus(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String mrn = "";
        final String indexptr = request.getParameter("indexptr");
        final String statusindex = request.getParameter("statusindex");
        final String UserId = Services.GetCookie("UserId", request).trim();
        final String userindex = Services.GetCookie("userindex", request).trim();
        try {
            Query = "Select MRN from filelogs_sftp where Id = " + indexptr;
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                mrn = hrset.getString(1);
            }
            hrset.close();
            hstmt.close();
            hstmt = conn.createStatement();
            Query = " Update oe.filelogs_sftp   Set filestatus  = '" + statusindex.trim() + "'" + " , liststatus  = '" + statusindex.trim() + "'" + " Where id ='" + indexptr.trim() + "'";
            hstmt.executeUpdate(Query);
            hstmt.close();
            final String note = "Update File/ Claim Status";
            createnote(Integer.parseInt(userindex), note, Integer.parseInt(indexptr), conn, mrn);
            logging(Integer.parseInt(userindex), Integer.parseInt(statusindex), Integer.parseInt(indexptr), conn);
            markuser(Integer.parseInt(userindex), Integer.parseInt(statusindex), Integer.parseInt(indexptr), conn);
            out.println("done");
        } catch (Exception ee) {
            out.println(ee.getMessage());
        }
    }

    public void showclaimdetails(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final Connection conn2) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final DecimalFormat df2 = new DecimalFormat("#,###.00");
        final StringBuffer CDRList = new StringBuffer();
        final StringBuffer Response = new StringBuffer();
        final String UserId = Services.GetCookie("UserId", request).trim();
        final String indexptr = request.getParameter("indexptr");
        final String mrn = request.getParameter("mrn");
        final String firstname = request.getParameter("firstname");
        final String lastname = request.getParameter("lastname");
        final String Acc = request.getParameter("Acc");
        final String Phone = request.getParameter("Phone");
        final String email = request.getParameter("email");
        final String insurance = request.getParameter("insurance");
        String cmdref = request.getParameter("cmdref");
        final String clientid = request.getParameter("clientid");
        final String AgentId = "-1";
        final String dosdate = request.getParameter("dosdate");
        final String ForDate = request.getParameter("StartDateT");
        String ToDate = "";
        String FromDate = "";
        String dToDate = "";
        String dFromDate = "";
        final String AgentIdChk = "";
        String PhoneCondition = "";
        String firstnameCondition = "";
        String lastnameCondition = "";
        String emailCondition = "";
        String mrnCondition = "";
        final String cmdrefCondition = "";
        String clientCondition = "";
        int SNo = 0;
        try {
            HashMap<Integer, String> clientlist = new HashMap<Integer, String>();
            clientlist = Clientlist("aaa", conn);
            ToDate = ForDate.substring(0, 19);
            FromDate = ForDate.substring(22, 41);
            dToDate = dosdate.substring(0, 19);
            dFromDate = dosdate.substring(22, 41);
            if (Phone.length() > 0) {
                PhoneCondition = " and Phone like '%" + Phone + "%' ";
            }
            if (firstname.length() > 0) {
                firstnameCondition = " and firstname like '%" + firstname + "%' ";
            }
            if (lastname.length() > 0) {
                lastnameCondition = " and lastname like '%" + lastname + "%' ";
            }
            if (email.length() > 0) {
                emailCondition = " and email like '%" + email + "%' ";
            }
            if (mrn.length() > 0) {
                mrnCondition = " and mrn like '%" + mrn + "%' ";
            }
            if (cmdref.length() > 0) {
                cmdref = " and cmdref like '%" + cmdref + "%' ";
            }
            if (clientid.compareTo("-1") != 0) {
                clientCondition = " and clientid = '" + clientid + "' ";
            }
            Query = "select id,callerid,customerid,argument,json1,json2,parsedmsg,entrydate,amount from IVRLOG where entrydate between '" + ToDate + "' and '" + FromDate + "'  " + PhoneCondition + " " + emailCondition + " order by entrydate";
            Query = "select claimid,ifnull(firstname,''),ifnull(lastname,''),ifnull(dosdate,''),ifnull(mrn,''),ifnull(cmdref,''),ifnull(charges,''),ifnull(phone,''),ifnull(email,''),ifnull(address,'')\r\n,ifnull(createddate,''),createdby,ifnull(LMailed,''),clientid from claim_info_master where entrydate between '" + ToDate + "' and '" + FromDate + "'  " + "" + PhoneCondition + " " + emailCondition + " " + firstnameCondition + " " + lastnameCondition + " " + mrnCondition + " " + cmdrefCondition + " " + clientCondition + "";
            final String Created = "";
            hstmt = conn2.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
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
                CDRList.append("</tr>\n");
            }
            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", ForDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showclaimdetails.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
        }
    }

    public void DashBoard(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final Connection conn2) {
        final Statement stmt = null;
        final ResultSet rset = null;
        final String Query = "";
        final Statement stmt2 = null;
        final ResultSet rset2 = null;
        final String Query2 = "";
        final int TotalCallsToday = 0;
        final int AnsweredCall = 0;
        final int MissedCalls = 0;
        final int OutboundCalls = 0;
        final int CallBack = 0;
        final int calltoday = 0;
        final int todaynotrancation = 0;
        final double todayamount = 0.0;
        final int todayerror = 0;
        final int monthlynotrancation = 0;
        final int monthlyerror = 0;
        final double monthlyamount = 0.0;
        final int monthlycall = 0;
        String UserId = Services.GetCookie("UserId", request);
        UserId = UserId.substring(1);
        try {
            final String pattern = "#,###.###";
            final DecimalFormat decimalFormat = new DecimalFormat(pattern);
            decimalFormat.setGroupingSize(3);
            final Parsehtm Parser = new Parsehtm(request);
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
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Dashboards/GraphicalMainDashboard.html");
        } catch (Exception var11) {
            Services.DumException("DashBoard", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            out.flush();
            out.close();
        }
    }

    public void showivrdetails_error(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final String fileindex = request.getParameter("fileindex");
        final DecimalFormat df2 = new DecimalFormat("#,###.00");
        final StringBuffer CDRList = new StringBuffer();
        final StringBuffer Response = new StringBuffer();
        final String UserId = Services.GetCookie("UserId", request).trim();
        final String cli = request.getParameter("cli");
        final String custid = request.getParameter("custid");
        final String AgentId = "-1";
        final String ForDate = request.getParameter("StartDateT");
        String ToDate = "";
        String FromDate = "";
        final String AgentIdChk = "";
        String Condition = "";
        String Condition2 = "";
        int SNo = 0;
        try {
            ToDate = ForDate.substring(0, 19);
            FromDate = ForDate.substring(22, 41);
            if (cli.length() > 0) {
                Condition = " and callerid like '%" + cli + "%' ";
            }
            if (custid.length() > 0) {
                Condition2 = " and customerid like '%" + custid + "%' ";
            }
            Query = "select id,callerid,customerid,argument,json1,json2,parsedmsg,entrydate,amount,ifnull(file,'-'),substr(entrydate,1,10) from IVRLOG where entrydate between '" + ToDate + "' and '" + FromDate + "'  " + Condition + " " + Condition2 + " group by customerid order by entrydate ";
            final String Created = "";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                String success = "0";
                success = getcount(hrset.getString(3), hrset.getString(11), conn);
                if (success.compareTo("0") == 0) {
                    ++SNo;
                    final String attempt = attemps(hrset.getString(3), hrset.getString(11), conn);
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
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", ForDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showivrReport_error.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
        }
    }

    public void showivrdetails_balance(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final String fileindex = request.getParameter("fileindex");
        final DecimalFormat df2 = new DecimalFormat("#,###.00");
        final StringBuffer CDRList = new StringBuffer();
        final StringBuffer Response = new StringBuffer();
        final String UserId = Services.GetCookie("UserId", request).trim();
        final String cli = request.getParameter("cli");
        final String custid = request.getParameter("custid");
        final String AgentId = "-1";
        final String ForDate = request.getParameter("StartDateT");
        String ToDate = "";
        String FromDate = "";
        final String AgentIdChk = "";
        String Condition = "";
        String Condition2 = "";
        int SNo = 0;
        try {
            ToDate = ForDate.substring(0, 19);
            FromDate = ForDate.substring(22, 41);
            if (cli.length() > 0) {
                Condition = " and callerid like '%" + cli + "%' ";
            }
            if (custid.length() > 0) {
                Condition2 = " and customerid like '%" + custid + "%' ";
            }
            Query = "select id,callerid,customerid,balance,zipentered,cust_zip,entrydate,ifnull(uid,'-'),substr(entrydate,1,10) from IVRBALANCELOG where entrydate between '" + ToDate + "' and '" + FromDate + "'  " + Condition + " " + Condition2 + " group by customerid order by entrydate ";
            final String Created = "";
            String Remarks = "";
            String bistatus = "Success";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                Remarks = "";
                final String success = "0";
                bistatus = "Success";
                ++SNo;
                final String attempt = attemps(hrset.getString(3), hrset.getString(9), conn);
                CDRList.append("<tr class=\"Inner\">\n");
                CDRList.append("<td align=center>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(6) + "</td>\n");
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
                CDRList.append("</tr>\n");
            }
            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", ForDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showivrReport_balance.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
        }
    }

    public void showivrdetailssum(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final String fileindex = request.getParameter("fileindex");
        final DecimalFormat df2 = new DecimalFormat("#,###.00");
        final StringBuffer CDRList = new StringBuffer();
        final StringBuffer Response = new StringBuffer();
        final String UserId = Services.GetCookie("UserId", request).trim();
        final String AgentId = "-1";
        final String ForDate = request.getParameter("StartDateT");
        String ToDate = "";
        String FromDate = "";
        final String AgentIdChk = "";
        final String Condition = "";
        final String Condition2 = "";
        String amount = "";
        int SNo = 0;
        try {
            ToDate = ForDate.substring(0, 19);
            FromDate = ForDate.substring(22, 41);
            Query = "select ROUND(sum(amount),2),parsedmsg,count(*) from IVRLOG  where entrydate between '" + ToDate + "' and '" + FromDate + "'   group by parsedmsg";
            final String Created = "";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                ++SNo;
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
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("Created", ForDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showivrReportsum.html");
        } catch (Exception e) {
            out.println("Unable to process the request..." + e.getMessage());
        }
    }

    public StringBuffer insurancecompanylist(final Connection conn) {
        return null;
    }
}
