//
// Decompiled by Procyon v0.5.36
//

package md;

import DAL.TwilioSMSConfiguration;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;

@SuppressWarnings("Duplicates")
public class SendSMS extends HttpServlet {

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
        String UserId = "";
        int FacilityIndex = 0;
        int UserIndex = 0;
        String DatabaseName = "";
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();

        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();
        UtilityHelper helper = new UtilityHelper();
        TwilioSMSConfiguration smsConfiguration = new TwilioSMSConfiguration();
        try {
            HttpSession session = request.getSession(false);
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }

            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "GetInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Send SMS INPUT Screen", "Send SMS Open Input Screen", FacilityIndex);
                    GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, UserIndex, smsConfiguration);
                    break;
                case "SaveBundleForm":
                    SaveBundleForm(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "GetPatients":
                    GetPatients(request, out, conn, context);
                    break;
                case "GetDetails":
                    GetDetails(request, out, conn, context, UserIndex);
                    break;
                case "GetTemplates":
                    GetTemplates(request, out, conn, context);
                    break;
                case "SendSms":
                    SendSms(request, out, conn, context, smsConfiguration, UserIndex);
                    break;
                default:
                    out.println("Under Development");
                    break;
            }
        } catch (Exception Ex) {
            String str = "";
            for (int i = 0; i < Ex.getStackTrace().length; ++i) {
                str = str + Ex.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            helper.SendEmailWithAttachment("Error in SendSMS ** (handleRequest)", context, Ex, "SendSMS", "handleRequest", conn);
            Services.DumException("SendSMS", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
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

    void GetInputOLD(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, int UserIndex) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            int ID = 0;
            String MRN = "";
            String FirstName = "";
            String LastName = "";
            String PhNumber = "";
            String UserName = "";
            String ClientName = "";
            ID = Integer.parseInt(request.getParameter("ID").trim());

            Query = "Select IFNULL(MRN,''), IFNULL(FirstName,''), IFNULL(LastName,''), IFNULL(PhNumber,'') " +
                    "from " + Database + ".PatientReg where ID  = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                MRN = rset.getString(1);
                FirstName = rset.getString(2);
                LastName = rset.getString(3);
                PhNumber = rset.getString(4);
            }
            rset.close();
            stmt.close();

            Query = "Select IFNULL(username,'') from oe.sysusers where indexptr = " + UserIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                UserName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Query = "Select IFNULL(FullName,'') from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("MRN", String.valueOf(MRN));
            Parser.SetField("FirstName", String.valueOf(FirstName));
            Parser.SetField("LastName", String.valueOf(LastName));
            Parser.SetField("PhNumber", String.valueOf(PhNumber));
            Parser.SetField("ID", String.valueOf(ID));
            Parser.SetField("UserName", String.valueOf(UserName));
            Parser.SetField("ClientName", String.valueOf(ClientName));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/SendSMS.html");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, int UserIndex, TwilioSMSConfiguration smsConfiguration) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            StringBuilder Facilities;
            StringBuilder Template = new StringBuilder();
/*            String[] LiveFacilities = {"victoria", "oe_2", "nacogdoches", "longview", "oddasa", "ER_Dallas", "frontlin_er", "richmond"};
            Facilities.append("<option value=''  disabled selected >Please Select Facility</option>");
            for (String LiveFacility : LiveFacilities) {
                try {
                    Query = "Select Id,Name from oe.clients where dbname='" + LiveFacility + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        Facilities.append("<option value=" + rset.getString(1) + ">" + rset.getString(2) + "</option>");
                    }
                    rset.close();
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }*/
            Facilities = smsConfiguration.getFacilityList(request, conn, servletContext, UserIndex);
            Template = smsConfiguration.getSmsTemplate(request, conn, servletContext);

            String AdvocatePhNumber = "";
            Query = "Select IFNULL(AdvocatePhNumber,'') from oe.AdvocateSMSNumber where AdvocateIdx = " + UserIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                AdvocatePhNumber = rset.getString(1);
            }
            rset.close();
            stmt.close();
/*            Template.append("<option value='0'  selected >Please Select Template</option>");
            try {
                Query = "Select Id,Name from oe.SmsTemplates";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    Template.append("<option value=" + rset.getString(1) + ">" + rset.getString(2) + "</option>");
                }
                rset.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }*/

            String UserName = "";
            Query = "Select IFNULL(username,'') from oe.sysusers where indexptr = " + UserIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                UserName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Facilities", String.valueOf(Facilities));
            Parser.SetField("Template", String.valueOf(Template));
            Parser.SetField("UserName", UserName);
            Parser.SetField("AdvocatePhNumber", AdvocatePhNumber);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/SendSMS.html");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void GetPatients(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Database = "";


            String Patient = request.getParameter("Patient").trim();
            String Facility = request.getParameter("Facility").trim();
            StringBuilder PatientList = new StringBuilder();

            try {
                Query = "Select dbname from oe.clients" +
                        " where Id='" + Facility + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    Database = rset.getString(1);
                }
                rset.close();
                stmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            Query = "Select Id, MRN, FirstName, MiddleInitial, LastName, DOB, PhNumber,`Status`\n" +
                    "FROM " + Database + ".PatientReg \n" +
                    "WHERE status = 0 and CONCAT(MRN,FirstName,LastName,PhNumber) like '%" + Patient + "%'  ";
            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\" onchange=\"GetDetails(this.value);\">");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option value=-1> Please Select Below Patient </option>");
            while (rset.next())
                PatientList.append("<option value=" + rset.getInt(2) + ">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
            rset.close();
            stmt.close();
            PatientList.append("</select>");
            out.println(PatientList.toString());
        } catch (Exception ex) {

        }
    }

    void GetDetails(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, int UserIndex) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Database = "";


            String Patient = request.getParameter("Patient").trim();
            String Facility = request.getParameter("Facility").trim();

            try {
                Query = "Select dbname from oe.clients" +
                        " where Id='" + Facility + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    Database = rset.getString(1);
                }
                rset.close();
                stmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            String AdvocatePhNumber = "";
            Query = "Select IFNULL(AdvocatePhNumber,'') from oe.AdvocateSMSNumber where AdvocateIdx = " + UserIndex + " AND FacilityIdx=" + Facility;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                AdvocatePhNumber = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Query = "Select Id, MRN, FirstName, MiddleInitial, LastName, DOB, PhNumber,`Status`\n" +
                    "FROM " + Database + ".PatientReg \n" +
                    "WHERE status = 0 and MRN='" + Patient + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                out.println(rset.getString(2) + "~"
                        + rset.getString(3) + " " + rset.getString(4) + " " + rset.getString(5) + "~" +
                        rset.getString(7) + "~" + AdvocatePhNumber);
            else
                out.println("No Details Found! ~");
            rset.close();
            stmt.close();
        } catch (Exception ex) {
            out.println(ex.getMessage());
        }
    }

    private void GetTemplates(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Database = "";


            String ID = request.getParameter("Patient").trim();

            Query = "Select Body " +
                    "FROM oe.SmsTemplates \n" +
                    "WHERE Id='" + ID + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                out.println(rset.getString(1));
            else
                out.println("No Details Found! ~");
            rset.close();
            stmt.close();
        } catch (Exception ex) {
            out.println(ex.getMessage());
        }
    }


    private void SendSms(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, TwilioSMSConfiguration smsConfiguration, int advocateIdx) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Database = "";
            String[] result;
            String PtMRN = "0";
            int facilityIndex = Integer.parseInt(request.getParameter("Facility").trim());
            String Username = request.getParameter("User").trim();
            int Priority = Integer.parseInt(request.getParameter("Priority").trim());
            String PtName = request.getParameter("Name").trim();
            String Mrn = request.getParameter("Mrn").trim();
            if (request.getParameter("Mrn").length() != 0)
                PtMRN = request.getParameter("Mrn").trim();
//            int PtMRN = Integer.parseInt(!request.getParameter("Mrn").isEmpty() ? request.getParameter("Mrn").trim() : request.getParameter("Mrn") != null ? request.getParameter("Mrn").trim() : "0");
            String PtPhNumber = request.getParameter("Ph").trim();
            String Sms = request.getParameter("Sms").trim();

            try {
                HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
                Query = "Select Id,dbname from oe.clients where status = 0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    hashMap.put(rset.getInt(1), rset.getString(2));
                }
                rset.close();
                stmt.close();

                Database = hashMap.get(facilityIndex);

                if (Database == null)
                    Database = "oe";
            } catch (Exception e) {
                e.printStackTrace();
            }
            insertionSMSInfo(Database, facilityIndex, advocateIdx, Priority, PtName, PtMRN, PtPhNumber, Sms, Username, 0);
            int maxId = smsConfiguration.getMaxSMSIndex(request, conn, servletContext, facilityIndex, Database);

            if (Priority == 3) {
                result = smsConfiguration.sendTwilioMessages(request, conn, servletContext, Sms, facilityIndex, PtPhNumber, advocateIdx, Database, PtMRN, maxId);
                if (result[0].equals("Success")) {
                    smsConfiguration.updateSMSInfoTable(request, conn, servletContext, maxId, result[1], Database, "0");
                    out.println("1");
                } else {
                    smsConfiguration.updateSMSInfoTable(request, conn, servletContext, maxId, result[1], Database, "999");
                    //insertionSMSInfo(Database, facilityIndex, advocateIdx, Priority, PtName, PtMRN, PtPhNumber, Sms, Username, 999);
                    out.println("2~" + result[0] + "");
                }
            } else {
                smsConfiguration.updateSMSInfoTable(request, conn, servletContext, maxId, "", Database, "0");
                //insertionSMSInfo(Database, facilityIndex, advocateIdx, Priority, PtName, PtMRN, PtPhNumber, Sms, Username, 0);
                out.println("1");
            }
        } catch (Exception ex) {
            out.println(ex.getMessage());
            ex.getStackTrace();
            String str = "";
            for (int i = 0; i < ex.getStackTrace().length; ++i) {
                str = str + ex.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    void SaveBundleForm(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            String ID = request.getParameter("ID").trim();
            String pattern = request.getParameter("pattern").trim();
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            int null_data_found = 0;


            Query = "SELECT COUNT(*) FROM " + Database + ".BundleForms where PatientRegId='" + ID + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            if (rset.next()) {
                null_data_found = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            if (null_data_found == 0) {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".BundleForms (PatientRegId,Form_ids) VALUE(?,?)");

                MainReceipt.setString(1, ID);
                MainReceipt.setString(2, pattern);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } else {
                PreparedStatement MainReceipt = conn.prepareStatement("UPDATE  " + Database + ".BundleForms  SET Form_ids = '" + pattern + "'  WHERE PatientRegId = '" + ID + "'");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }

            out.println("1");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void insertionSMSInfo(String Database, int facilityIndex, int advocateIdx, int Priority,
                                  String PtName, String PtMRN, String PtPhNumber, String Sms, String Username,
                                  int status) {
        PreparedStatement MainReceipt = null;
        try {
            //Database = facilityIndex == 999 ? "oe" : Database;
            MainReceipt = conn.prepareStatement(
                    "INSERT INTO " + Database + ".SMS_Info (FacilityIdx,SentBy,SentAt ,Priority,PatientName," +
                            "PatientMRN,PatientPhNumber,Status,Sms,Username) " +
                            " VALUES (?,?,now(),?,?,?,?,?,?,?) ");
            MainReceipt.setInt(1, facilityIndex);
            MainReceipt.setInt(2, advocateIdx);
            MainReceipt.setInt(3, Priority);
            MainReceipt.setString(4, PtName);
            MainReceipt.setString(5, PtMRN);
            MainReceipt.setString(6, PtPhNumber);
            MainReceipt.setInt(7, status);
            MainReceipt.setString(8, Sms);
            MainReceipt.setString(9, Username);
            MainReceipt.executeUpdate();
            MainReceipt.close();
        } catch (Exception ex) {
            System.out.println("EXCEPTION in Saving Record" + ex.getMessage());
        }
    }
}