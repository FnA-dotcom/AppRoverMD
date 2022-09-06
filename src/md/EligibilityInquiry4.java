package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class EligibilityInquiry4 extends HttpServlet {


    private static String userMessage = "";
    public static String access_token = "";
    public static String bodyinc = "";
    private static String statusCodeN = "";
    private static String ParameterName = "";
    private static String errorMessage = "";
    ObjectMapper mapper = new ObjectMapper();
    private Connection conn = null;
    private int countResend = 0;

    private static void ProcessNode(JsonNode node) {
        if (node.isArray()) {
            for (JsonNode objInArray : node)
                ProcessNode(objInArray);
        } else if (node.isContainerNode()) {
            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> field = it.next();
                if (((String) field.getKey()).compareTo("userMessage") == 0) {
                    System.out.println("userMessage: " + field.getValue());
                    userMessage = ((JsonNode) field.getValue()).toString().replace("\"", "");
                }
                if (((String) field.getKey()).compareTo("statusCode") == 0) {
                    System.out.println("statusCode: " + field.getValue());
                    statusCodeN = ((JsonNode) field.getValue()).toString().replace("\"", "");
                }
                if (((String) field.getKey()).compareTo("errors") == 0)
                    ProcessNode(field.getValue());
                if (((String) field.getKey()).compareTo("field") == 0) {
                    System.out.println("ParameterName: " + field.getValue());
                    ParameterName = ((JsonNode) field.getValue()).toString().replace("\"", "");
                }
                if (((String) field.getKey()).compareTo("errorMessage") == 0) {
                    System.out.println("errorMessage: " + field.getValue());
                    errorMessage = ((JsonNode) field.getValue()).toString().replace("\"", "");
                }
            }
        }
    }

    public static void getrequestdetails(HttpServletRequest request, Exception ee) {
        try {
            Enumeration<String> enum_1 = request.getParameterNames();
            while (enum_1.hasMoreElements()) {
                String name = enum_1.nextElement();
                String[] values = request.getParameterValues(name);
                if (values != null)
                    for (int i = 0; i < values.length; i++)
                        pushLogs(name + " (" + i + "): " + values[i], ee);
            }
        } catch (Exception exception) {
        }
    }

    private static void pushLogs(String Message, Exception exp) {
        try {
            Date dt = GetDate();
            NumberFormat nf = new DecimalFormat("#00");
            String eventTime = nf.format((dt.getYear() + 1900)) + "_" + nf.format((dt.getMonth() + 1)) + "_" + nf.format(dt.getDate()) + "_" + nf.format(dt.getHours()) + "_" + nf.format(dt.getMinutes()) + "_" + nf.format(dt.getSeconds());
            String FileName = "Availity_" + eventTime + "_" + GetExceptionFileName();
            FileWriter fr = new FileWriter("/sftpdrive/opt/FinalTrizettoLogs/" + FileName, true);
            fr.write(": Event " + eventTime + " ****  Msg " + Message + " **** Exception: " + exp + " \r\n");
            fr.flush();
            fr.close();
        } catch (Exception e) {
            System.out.println("Unable to Generate Thread for Console Event " + e.getMessage());
        }
    }

    private static Date GetDate() {
        try {
            return new Date();
        } catch (Exception exception) {
            return null;
        }
    }

    private static String GetExceptionFileName() {
        int temp = 0;
        try {
            Date dt = GetDate();
            NumberFormat nf = new DecimalFormat("#00");
            return nf.format((dt.getYear() + 1900)) + "_" + nf.format((dt.getMonth() + 1)) + "_" + nf.format(dt.getDate()) + ".log";
        } catch (Exception e) {
            return "invalid filename " + e.getMessage();
        }
    }

    public static String getcoveragebyid(String Body) {
        String responseBody = "";
        try {
            BufferedReader br;
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(Body);
            JsonNode Id = null;
            JsonNode coverages = null;
            JsonNode Currentdata = null;
            System.out.println(node);
            if (node.has("coverages")) {
                System.out.println("Inside coverages");
                coverages = node.get("coverages");
                Iterator<JsonNode> elements = coverages.elements();
                while (elements.hasNext()) {
                    Currentdata = elements.next();
                    System.out.println("bill = " + Currentdata.toString());
                }
                if (Currentdata.has("id")) {
                    Id = Currentdata.get("id");
                    System.out.println(Id);
                }
            }
            String IDS = Id.toString();
            IDS = IDS.replace("\"", "");
            String baseUrl = "https://api.availity.com/availity/v1/coverages/" + IDS;
            System.out.println(baseUrl);
            HttpURLConnection conn = (HttpURLConnection) (new URL(baseUrl)).openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("authorization", "Bearer " + access_token);
            conn.setReadTimeout(300000);
            int statusCode = conn.getResponseCode();
            System.out.println(statusCode);
            if (statusCode == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                output.append(line);
            conn.disconnect();
            responseBody = output.toString();
        } catch (Exception ee) {
            System.out.print("Exeption getCoverageId: " + ee.getMessage());
            return "Return Exception get CoverageId: " + ee.getMessage().toString();
        }
        return responseBody;
    }

    public static String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z,0-9:-]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        Services supp = new Services();
        ServletContext context = null;
        context = getServletContext();
        try {
            Parsehtm Parser;
            HttpSession session = request.getSession(false);
            UtilityHelper helper = new UtilityHelper();
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            String UserId = session.getAttribute("UserId").toString();
            String DatabaseName = session.getAttribute("DatabaseName").toString();
            int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            try {
                if (UserId.equals("")) {
                    Parsehtm parsehtm = new Parsehtm(request);
                    parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            } catch (Exception excp) {
                this.conn = null;
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("FormName", "ManagementDashboard");
                parsehtm.SetField("ActionID", "GetInput");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            String ActionID = request.getParameter("ActionID").trim();
            this.conn = Services.getMysqlConn(context);
            if (this.conn == null) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("Error", "Unable to connect. Our team is looking into it!");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "EligibilityGetInput":
                    supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Insurance Eligibility Inquiry 2", "Input Fields Details", FacilityIndex);
                    EligibilityGetInput(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "GetDetails":
                    supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Insurance Eligibility Inquiry 2 Patient Details", "Get Patient Details and Auto Fill the Input Fields", FacilityIndex);
                    GetDetails(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "GetResponse":
                    supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Insurance Eligibility Inquiry 2 Get Insurance Information", "Get Insurance Info, Response from finaltrizetto And eireponse Class", FacilityIndex);
                    GetResponse(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                default:
                    helper.deleteUserSession(request, this.conn, session.getId());
                    session.invalidate();
                    Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }
        } catch (Exception Ex) {
            Services.DumException("EligibilityInquiry2", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            out.println(Ex.getMessage());
            out.flush();
            out.close();
        } finally {
            try {
                if (this.conn != null)
                    this.conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.flush();
            out.close();
        }
    }

    private void EligibilityGetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        ResultSet rset = null;
        Statement stmt = null;
        String Query = "";
        String DOS = "";
        String SubscriberID = "";
        String FirstName = "";
        String LastName = "";
        String DOB = "";
        String GroupNo = "";
        String Gender = "";
        String DOBAvaility = "";
        String DOSAvaility = "";
        String NPI = "";
        String proname = "";
        String PatientMRN = "";
        int PatientRegId = 0;
        int InsuranceFound = 0;
        StringBuffer PatientList = new StringBuffer();
        StringBuffer ServiceType = new StringBuffer();
        StringBuffer PayProcedureList = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        StringBuffer ProfessionalPayersList = new StringBuffer();
        StringBuffer AvailityPayersList = new StringBuffer();
        String PatientId = request.getParameter("PatientId");
        ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
        try {
            Query = "Select MRN, CONCAT(Title, ' ' , FirstName, ' ', MiddleInitial, ' ', LastName) from " + Database + ".PatientReg where status = 0 and ID = " + PatientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientMRN = rset.getString(1);
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
            }
            rset.close();
            stmt.close();
            Query = "Select NPI,proname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                NPI = rset.getString(1);
                proname = rset.getString(2);
            }
            rset.close();
            stmt.close();
            Query = " Select ID, IFNULL(FirstName,''), IFNULL(LastName,''), IFNULL(DATE_FORMAT(DOB, '%Y%m%d'),''),IFNULL(DATE_FORMAT(DateofService,'%d-%m-%Y %T'),DATE_FORMAT(CreatedDate,'%d-%m-%Y %T')), IFNULL(gender,''), IFNULL(DATE_FORMAT(DOB, '%Y-%m-%d'),''), IFNULL(DATE_FORMAT(DateofService,'%Y-%m-%d'),DATE_FORMAT(CreatedDate,'%Y-%m-%d')) from " + Database + ".PatientReg  where ID = '" + PatientId + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientRegId = rset.getInt(1);
                FirstName = rset.getString(2);
                LastName = rset.getString(3);
                DOB = rset.getString(4);
                DOS = rset.getString(5);
                Gender = rset.getString(6).toUpperCase();
                DOBAvaility = rset.getString(7);
                DOSAvaility = rset.getString(8);
            }
            rset.close();
            stmt.close();
            Query = "Select IFNULL(PayerId,''), IFNULL(PayerName,'') from " + Database + ".AvailityPayerList where Status = 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option class=Inner value=''>Please Select Patient</option>");
            while (rset.next())
                AvailityPayersList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
            rset.close();
            stmt.close();
            Query = "Select COUNT(*) from " + Database + ".InsuranceInfo where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                InsuranceFound = rset.getInt(1);
            rset.close();
            stmt.close();
            if (InsuranceFound > 0) {
                Query = "Select GrpNumber, MemId from " + Database + ".InsuranceInfo where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    GroupNo = rset.getString(1);
                    SubscriberID = rset.getString(2);
                }
                rset.close();
                stmt.close();
            }
            if (ClientId == 9 || ClientId == 28) {
                Query = "Select COUNT(*) from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    InsuranceFound = rset.getInt(1);
                rset.close();
                stmt.close();
                if (InsuranceFound > 0) {
                    Query = "Select HISubscriberGroupNo,HISubscriberPolicyNo from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        GroupNo = rset.getString(1);
                        SubscriberID = rset.getString(2);
                    }
                    rset.close();
                    stmt.close();
                }
            }
            SubscriberID = SubscriberID.replace("-", "");
            Query = "Select IFNULL(PatientMRN,''), IFNULL(Name,''), IFNULL(DateofBirth,''),IFNULL(DateofService,''), IFNULL(PolicyStatus,''), IFNULL(strmsg,''),  IFNULL(InsuranceNum,''), Id from oe.EligibilityInquiry where ltrim(rtrim(UPPER(CreatedBy))) = ltrim(rtrim(UPPER('" + UserId + "')))  and PatientMRN = '" + PatientMRN + "' Group by DateofService";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                String DOB1 = "";
                String DOS1 = "";
                if (!DOS.equals(""))
                    DOS1 = rset.getString(4).substring(3, 5) + "/" + rset.getString(4).substring(0, 2) + "/" + rset.getString(4).substring(6, 10);
                CDRList.append("<tr><td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + DOS1 + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                if (rset.getString(6).equals("")) {
                    CDRList.append("<td align=left>No Response Found </td>\n");
                } else {
                    CDRList.append("<td align=left><a href=/md/md.EligibilityInquiryReport?ActionID=GetResponse&Id=" + rset.getString(8) + " target=\"_blank\">Eligibility Response</a></td>\n");
                }
                CDRList.append("</tr>");
            }
            rset.close();
            stmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("ServiceType", String.valueOf(ServiceType));
            Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
            Parser.SetField("AvailityPayersList", String.valueOf(AvailityPayersList));
            Parser.SetField("ClientIndex", String.valueOf(ClientId));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("DOS", String.valueOf(DOS));
            Parser.SetField("DOBAvaility", String.valueOf(DOBAvaility));
            Parser.SetField("DOSAvaility", String.valueOf(DOSAvaility));
            Parser.SetField("SubscriberID", String.valueOf(SubscriberID));
            Parser.SetField("FirstName", String.valueOf(FirstName));
            Parser.SetField("LastName", String.valueOf(LastName));
            Parser.SetField("DOB", String.valueOf(DOB));
            Parser.SetField("GroupNo", String.valueOf(GroupNo));
            Parser.SetField("Gender", String.valueOf(Gender));
            Parser.SetField("NPI", String.valueOf(NPI));
            Parser.SetField("proname", String.valueOf(proname));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("PatientRegId", String.valueOf(PatientId));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(servletContext)) + "Forms/EligibilityGetInput2_avail.html");
        } catch (Exception ex) {
            pushLogs("Error in EligibilityGetInput in main : ", ex);
            out.println("Error:-" + ex.getMessage());
        }
    }

    private void GetDetails(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        ResultSet rset = null;
        Statement stmt = null;
        String Query = "";
        String PatientId = request.getParameter("PatientId").trim();
        String DOS = "";
        String SubscriberID = "";
        String FirstName = "";
        String LastName = "";
        String DOB = "";
        String GroupNo = "";
        String Gender = "";
        String NPI = "";
        String proname = "";
        int PatientRegId = 0;
        int InsuranceFound = 0;
        try {
            Query = "Select NPI,proname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                NPI = rset.getString(1);
                proname = rset.getString(2);
            }
            rset.close();
            stmt.close();
            Query = " Select ID, FirstName, LastName, DATE_FORMAT(DOB, '%Y-%m-%d'),IFNULL(DATE_FORMAT(DateofService,'%Y-%m-%d'),DATE_FORMAT(CreatedDate,'%Y-%m-%d')), gender from " + Database + ".PatientReg  where MRN = '" + PatientId + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientRegId = rset.getInt(1);
                FirstName = rset.getString(2);
                LastName = rset.getString(3);
                DOB = rset.getString(4);
                DOS = rset.getString(5);
                Gender = rset.getString(6).toUpperCase();
            }
            rset.close();
            stmt.close();
            Query = "Select COUNT(*) from " + Database + ".InsuranceInfo where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                InsuranceFound = rset.getInt(1);
            rset.close();
            stmt.close();
            if (InsuranceFound > 0) {
                Query = "Select GrpNumber, MemId from " + Database + ".InsuranceInfo where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    GroupNo = rset.getString(1);
                    SubscriberID = rset.getString(2);
                }
                rset.close();
                stmt.close();
            }
            if (ClientId == 9 || ClientId == 28) {
                Query = "Select COUNT(*) from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    InsuranceFound = rset.getInt(1);
                rset.close();
                stmt.close();
                if (InsuranceFound > 0) {
                    Query = "Select HISubscriberGroupNo,HISubscriberPolicyNo from " + Database + ".Patient_HealthInsuranceInfo where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        GroupNo = rset.getString(1);
                        SubscriberID = rset.getString(2);
                    }
                    rset.close();
                    stmt.close();
                }
            }
            SubscriberID = SubscriberID.replace("-", "");
            out.println(String.valueOf(DOS) + "|" + SubscriberID + "|" + FirstName + "|" + LastName + "|" + DOB + "|" + GroupNo + "|" + Gender + "|" + NPI + "|" + proname);
        } catch (Exception ex) {
            pushLogs("Error in GetDetails in main : ", ex);
            out.println("Error:-" + ex.getMessage());
        }
    }

    private void GetResponse(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        ResultSet rset = null;
        Statement stmt = null;
        String Query = "";
        String GediPayerID = "";
        String NPI = "";
        String FirstName = "";
        String LastName = "";
        String InsuranceNum = "";
        String DOB = "";
        String Gender = "";
        String GPN = "";
        String DOS = "";
        String PatientId = "";
        String PatientMRN = "";
        String proname = "";
        int PatientRegId = 0;
        try {
            PatientRegId = Integer.parseInt(request.getParameter("PatientRegId").trim());
            GediPayerID = request.getParameter("ProfessionalPayerAvail").trim();
            NPI = request.getParameter("NPI").trim();
            FirstName = request.getParameter("FirstNameAvail").trim().replaceAll(" ", "");
            LastName = request.getParameter("LastNameAvail").trim().replaceAll(" ", "");
            InsuranceNum = request.getParameter("SubscriberIdAvail").trim().replaceAll(" ", "");
            DOB = request.getParameter("DOBAvail").trim();
            Gender = request.getParameter("GenderAvail").trim().replaceAll(" ", "");
            GPN = request.getParameter("GroupNoAvail").trim().replaceAll(" ", "");
            DOS = request.getParameter("DateofServiceAvail").trim();
            PatientId = request.getParameter("PatientIdAvail").trim();
            proname = request.getParameter("proname").trim().replaceAll(" ", "");
            if (Gender.toUpperCase().trim().equals("MALE")) {
                Gender = "M";
            } else {
                Gender = "F";
            }
            String Service_Type = request.getParameter("Service_TypeAvail").trim();
            if (Service_Type.equals("A")) {
                Service_Type = "52";
            } else if (Service_Type.equals("B")) {
                Service_Type = "86";
            }
            String ResponseType = "";
            String strMsg = "";
            strMsg = getinqury(GediPayerID, NPI, FirstName, LastName, InsuranceNum, DOB, Gender, GPN, PatientId, UserId, proname, Service_Type, DOS, PatientRegId);
            if (statusCodeN.equals("400")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", errorMessage + " Please Submit Again with  Different " + ParameterName + " Status CODE: " + statusCodeN);
                Parser.SetField("MRN", userMessage);
                Parser.SetField("FormName", "PatientUpdateInfo");
                Parser.SetField("ActionID", String.valueOf("GetInput&ID=" + PatientRegId));
                Parser.SetField("ClientIndex", String.valueOf(ClientId));
                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/Message.html");
            } else {
                out.println(strMsg);
                Query = "Select MRN " +
                        "from " + Database + ".PatientReg where status = 0 and ID = " + PatientRegId;
                stmt = conn.createStatement();
//                out.println(Query);
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    PatientMRN = rset.getString(1);
                }
                rset.close();
                stmt.close();
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO oe.EligibilityInquiry (PatientMRN,DateofService,TraceId ,PolicyStatus,strmsg, Name, DateofBirth, Gender, InsuranceNum, GediPayerId, CreatedBy, CreatedDate,ResponseType,FacilityIndex,EProvider) VALUES (?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?) ");
                    MainReceipt.setString(1, PatientMRN);
                    MainReceipt.setString(2, DOS);
                    MainReceipt.setString(3, avail_res1.SubmemberId);
                    MainReceipt.setString(4, avail_res1.PLANStatus);
                    MainReceipt.setString(5, strMsg);
                    MainReceipt.setString(6, String.valueOf(FirstName) + " " + LastName);
                    MainReceipt.setString(7, DOB);
                    MainReceipt.setString(8, Gender);
                    MainReceipt.setString(9, InsuranceNum);
                    MainReceipt.setString(10, GediPayerID);
                    MainReceipt.setString(11, UserId);
                    MainReceipt.setString(12, ResponseType);
                    MainReceipt.setInt(13, ClientId);
                    MainReceipt.setInt(14, 2);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception ex) {
                    System.out.println("Error while insertion --> Exception in GetResponse:- " + ex.getMessage());
                    out.close();
                    out.flush();
                    return;
                }
            }
        } catch (Exception ex) {
            pushLogs("EligibilityInquiry4 --> Exception in GetResponse:", ex);
            String str = "";
            for (int i = 0; i < (ex.getStackTrace()).length; i++)
                str = str + ex.getStackTrace()[i] + "<br>";
            out.println("EligibilityInquiry4 -->" + str);
            out.println("EligibilityInquiry2 --> Exception in GetResponse:- " + ex.getMessage());
            out.close();
            out.flush();
        }
    }

    private String connect() throws IOException {
        HttpResponse result = sendRequest("connect", "");

        return result.sessionKey;
    }

    private String getinqury(String GediPayerID, String NPI, String FirstName, String LastName, String InsuranceNum, String DOB, String Gender, String GPN, String PatientId, String UserId, String proname, String Service_Type, String DOS, int PatientRegId) throws IOException {
        HttpResponse result = getsendRequest("connect", "", GediPayerID, NPI, FirstName, LastName, InsuranceNum, DOB, Gender, GPN, PatientId, UserId, proname, Service_Type, DOS, PatientRegId);

        return result.body;
    }

    private HttpResponse getsendRequest(String operation, String body, String GediPayerID, String NPI, String FirstName, String LastName, String InsuranceNum, String DOB, String Gender, String GPN, String PatientId, String UserId, String proname, String Service_Type, String DOS, int PatientRegId) throws IOException {
        try {
            StringBuilder output = new StringBuilder();
            String Coveragebyid = "";
            connect();
            BufferedReader br;
            String baseUrl = "https://api.availity.com/availity/v1/coverages?payerId=BCBSF&providerNpi=1234567893&memberId=PBHR123456&patientLastName=Parker&patientFirstName=Peter&serviceType=98&patientBirthDate=1990-01-01&providerTaxId=123456789";
//19851116&asOfDate=09-04-2021 18:51:23
            baseUrl = "https://api.availity.com/availity/v1/coverages?payerId=" + GediPayerID + "&providerNpi=" + NPI + "&memberId=" + InsuranceNum + "&patientLastName=" + LastName + "&patientFirstName=" + FirstName + "&serviceType=" + Service_Type + "&patientBirthDate=" + DOB + "&asOfDate=" + DOS;//

            System.out.println(baseUrl);
            // String apiKey = this.props.getApiKey();
            HttpURLConnection conn = (HttpURLConnection) (new URL(baseUrl)).openConnection();
            conn.setDoOutput(true);


            conn.setRequestMethod("GET");
            // conn.setRequestProperty("Authorization", apiKey);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("authorization", "Bearer " + access_token);

//         conn.setReadTimeout(300000);
            conn.setReadTimeout(300000);
            conn.setReadTimeout(1000 * 10 * 3);
            conn.setConnectTimeout(35 * 1000);


            int statusCode = conn.getResponseCode();
            System.out.println("Status Cide here :-----" + statusCode);

//         System.out.println("Count resend: "+countResend);
            if (statusCode == 200) {
                statusCodeN = "";
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = br.readLine()) != null) {
                    output.append(line);
                }
                String responseBody = output.toString();
                System.out.println("-----------------------------1-----------------");
                System.out.println(responseBody);
                System.out.println("-------------------------------2---------------");

                String strMsg = getcoveragebyid(responseBody);
                System.out.println(strMsg);
                System.out.println("----------------------------------3------------");
                //Coveragebyid=strMsg;
                Coveragebyid = avail_res1.res1(strMsg);
                System.out.println(Coveragebyid);


            } else {
                System.out.println("Inside ELse here 1: ");
                if (statusCode == 202) {
                    output.append("<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<body class=\"skin-blue sidebar-mini\">\n" +
                            "<p> Try Clicking RELOAD Button OR If you want to Resubmit Kindly Go Back</p>\n" +
                            "<div class=\"col-md-3\">\n" +
                            "<div class=\"form-group\">\n" +
                            "<label><font color=\"black\">Relaod Page</font></label>\n" +
                            "<button onclick=\"history.go(0);\"> RELOAD </button>\n" +
                            "</div>\n" +
                            "</div>\n" +
                            "<br>\n" +
                            "<div class=\"col-md-3\">\n" +
                            "<div class=\"form-group\">\n" +
                            "<label><font color=\"black\">Go Back</font></label>\n" +
                            "<button onclick=\"history.back()\"> BACK </button>\n" +
                            "</div>\n" +
                            "</div>\n" +
                            "</body>\n" +
                            "</html>");

                    Coveragebyid = output.toString();

                    //getsendRequest("connect", "", GediPayerID, NPI, FirstName, LastName, InsuranceNum, DOB, Gender, GPN, PatientId, UserId, proname, Service_Type, DOS);
                } else {
//                InputStream in = new BufferedInputStream(conn.getErrorStream());
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                StringBuilder output2 = new StringBuilder();
//                String line;
//                while((line = reader.readLine()) != null) {
//                    output2.append(line);
//                }
//                System.out.println("OUTPUT 2: "+output2.toString());
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    //Here if no reposnswe blank check length here
                    System.out.println("Stage 1 REPOSENE except 200: " + br);
                    // Coveragebyid=br.toString();
                    String line;
                    while ((line = br.readLine()) != null) {
                        output.append(line);
                    }
                    Coveragebyid = output.toString();

                    //              send request again.
                    countResend++;
                    if (countResend < 5) {
                        getsendRequest("connect", "", GediPayerID, NPI, FirstName, LastName, InsuranceNum, DOB, Gender, GPN, PatientId, UserId, proname, Service_Type, DOS, PatientRegId);
                    } else {
                        countResend = 0;
                    }
                    if (statusCode == 400) {
                        JsonNode node = mapper.readTree(Coveragebyid);
                        ProcessNode(node);
                    }
                }
            }

            conn.disconnect();

//         String responseBody = output.toString();
//         System.out.println("Stage 4");
            //here we have to add if thwe cogerage id is not get from thwe first API....... **********
//         String Coveragebyid = getcoveragebyid(responseBody);
//         System.out.println("HERE COVERAGE ID: "+Coveragebyid);
            return new HttpResponse(Coveragebyid.toString(), Coveragebyid.toString());
        } catch (IOException e) {
            System.out.println("Exception getSendRequest: " + e.getMessage());
            throw e;
        } finally {


        }
    }

    private HttpResponse sendRequest(String operation, String body) throws IOException {
        try {
            BufferedReader br;
            String client_id = URLEncoder.encode("deea5802-6af3-4daa-a26c-ffd5cee32fdb", StandardCharsets.UTF_8.toString());
            String client_secret = URLEncoder.encode("gV7nQ6pW2qM8eS2eC1rI2hB8tQ4nC1tD3xN7rL5bE1uB2xS4vU", StandardCharsets.UTF_8.toString());

            String operational = "scope=hipaa&grant_type=client_credentials&client_id=" + client_id + "&client_secret=" + client_secret + "";

            String baseUrl = "https://api.availity.com/availity/v1/token";
            System.out.println(baseUrl);
            // String apiKey = this.props.getApiKey();
            HttpURLConnection conn = (HttpURLConnection) (new URL(baseUrl)).openConnection();
            conn.setDoOutput(true);


            conn.setRequestMethod("POST");
            // conn.setRequestProperty("Authorization", apiKey);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "application/json");


            conn.setReadTimeout(300000);


            OutputStream os = conn.getOutputStream();
            os.write(operational.getBytes());
            os.flush();


            int statusCode = conn.getResponseCode();
            if (statusCode == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                output.append(line);
            }


            conn.disconnect();

            String responseBody = output.toString();
	     /* if (statusCode != 200) {
	        //this.logger.log(Level.SEVERE, () -> String.format("Response status: %s, body:%n%s", new Object[] { Integer.valueOf(statusCode), responseBody }));
	        Map<String, String> fields = parseJsonString(responseBody);
	        throw new IOException((String)fields.get("errorMessage"));
	      } */
            Map<String, String> fields = parseJsonString(responseBody);
            System.out.println(fields);
            access_token = (String) fields.get("\"access_token");
            System.out.println(access_token);


            return new HttpResponse(output.toString(), access_token);
        } catch (IOException e) {

            System.out.println(e.getMessage());
            throw e;
        } finally {


        }
    }

    private Map<String, String> parseJsonString(String json) throws IOException {
        String noBrackets = json.replaceAll("\\{", "").replaceAll("\\}", "");
        return (Map<String, String>) Arrays.<String>stream(noBrackets.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
                .collect(Collectors.toMap(s -> s.split("\":")[0].substring(1), s -> {
                    String value = s.split("\":")[1];
                    if (value.startsWith("\""))
                        value = value.substring(1);
                    if (value.endsWith("\""))
                        value = value.substring(0, value.length() - 1);
                    return value;
                }));
    }

    private class HttpResponse {
        private String body;


        private String sessionKey;


        public HttpResponse(String body, String sessionKey) {
            this.body = body;
            this.sessionKey = sessionKey;
        }
    }
}
