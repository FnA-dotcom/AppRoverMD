package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.text.DecimalFormat;

@SuppressWarnings("Duplicates")
public class InstallmentPlan extends HttpServlet {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = null;
    private PreparedStatement pStmt = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void handleRequestOLD(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        ResultSet rset = null;
        Statement stmt = null;
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        String Database = "";
        String Query = "";
        int ClientId = 0;
        String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        Services supp = new Services();
        ServletContext context = null;
        context = getServletContext();
        conn = Services.getMysqlConn(context);
        try {
            Cookie[] cookies = request.getCookies();
            UserId = Zone = Passwd = "";
            String UserName = "";
            int checkCookie = 0;
            for (int coky = 0; coky < cookies.length; coky++) {
                String cName = cookies[coky].getName();
                String cValue = cookies[coky].getValue();
                if (cName.equals("UserId"))
                    UserId = cValue;
                if (cName.equals("username"))
                    UserName = cValue;
            }
            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                ClientId = rset.getInt(1);
            rset.close();
            stmt.close();
            Query = "Select dbname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                Database = rset.getString(1);
            rset.close();
            stmt.close();
            if (ActionID.equals("GetInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Installment Plan Input", "Open Installment Plan Input Screem", ClientId);
                GetInput(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("SavePlan")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Plan Data", "Save Plan Details", ClientId);
                SavePlan(request, out, conn, context, UserId, Database, ClientId);
            } else {
                out.println("Under Development");
            }
            try {
                conn.close();
            } catch (Exception exception) {
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String Action;

        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
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

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            Action = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (Action) {
                case "GetInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), Action, "Get Installment Plan Input", "Open Installment Plan Input Screen", FacilityIndex);
                    GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "SavePlan":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), Action, "Save Installment Plan", "Saving Installment Plan", FacilityIndex);
                    SavePlan(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }
        } catch (Exception Ex) {
            //helper.SendEmail("Main Section", "Error in Transaction Report", "Message Body");
            helper.SendEmailWithAttachment("Error in InstallmentPlan ** (handleRequest)", context, Ex, "InstallmentPlan", "handleRequest", conn);
            Services.DumException("InstallmentPlan", "Handle Request", request, Ex, getServletContext());
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

    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String PatientMRN = "";
        String PatientName = "";
        double TotalAmount = 0.0;
        double PaidAmount = 0.0;
        double BalAmount = 0.0;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        StringBuffer InstallmentPlanBuffer = new StringBuffer();
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
        int InstallmentPlanFound = 0;
        int InstallmentPlanName = 0;
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int SNo = 1;
        try {
            Query = "Select a.PatientMRN, CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                    "a.TotalAmount, a.PaidAmount, a.BalAmount  " +
                    "from " + Database + ".InvoiceMaster a " +
                    "LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN where a.InvoiceNo = '" + InvoiceNo + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientMRN = rset.getString(1);
                PatientName = rset.getString(2);
                TotalAmount = rset.getDouble(3);
                PaidAmount = rset.getDouble(3) - rset.getDouble(5);
                BalAmount = rset.getDouble(5);
            }
            rset.close();
            stmt.close();

            if (BalAmount == 0.0D) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("Message", "Payment has already been paid!");
                parsehtm.SetField("FormName", "RegisteredPatients");
                parsehtm.SetField("ActionID", "CollectPayment");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                return;
            }

            Query = "Select COUNT(*), InstallmentPlan from " + Database + ".InstallmentPlan where MRN = '" + PatientMRN + "' and InvoiceNo = '" + InvoiceNo + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                InstallmentPlanFound = rset.getInt(1);
                InstallmentPlanName = rset.getInt(2);
            }
            rset.close();
            stmt.close();
            if (InstallmentPlanFound > 0) {
                Query = "Select IFNULL(MRN,''), IFNULL(InvoiceNo,''), IFNULL(PaymentAmount,0), IFNULL(DATE_FORMAT(PaymentDate,'%m/%d/%Y'),''), " +
                        " CASE WHEN PAID = 0 THEN 'Pending' WHEN Paid = 1 THEN 'Paid' ELSE 'Pending' END from " + Database + ".InstallmentPlan where MRN = '" + PatientMRN + "' and " +
                        " InvoiceNo = '" + InvoiceNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + PatientName + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    SNo++;
                }
                rset.close();
                stmt.close();

            }
            String disabled = "";
            if (InstallmentPlanFound > 0) {
                disabled = "disabled";
                if (InstallmentPlanName == 1) {
                    InstallmentPlanBuffer.append("<input type=\"text\" class=\"form-control\" name=\"InstallmentPlan\" id=\"InstallmentPlan\" value=\"Weekly\" readonly>");
                } else if (InstallmentPlanName == 2) {
                    InstallmentPlanBuffer.append("<input type=\"text\" class=\"form-control\" name=\"InstallmentPlan\" id=\"InstallmentPlan\" value=\"Monthly\" readonly>");
                } else if (InstallmentPlanName == 3) {
                    InstallmentPlanBuffer.append("<input type=\"text\" class=\"form-control\" name=\"InstallmentPlan\" id=\"InstallmentPlan\" value=\"Yearly\" readonly>");
                }
            } else {
                InstallmentPlanBuffer.append("<select class=\"form-control select2\" id=\"InstallmentPlan\" name=\"InstallmentPlan\" onchange=\"GetTimeline(this.value);\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t<option value=\"0\"> Please Select Any</option>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t<option value=\"1\"> Weekly</option>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t<option value=\"2\"> Monthly</option>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t<option value=\"3\"> Yearly</option>\n" +
                        "\t\t\t\t\t\t\t\t\t\t</select>");
            }

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.SetField("PatientMRN", String.valueOf(PatientMRN));
            Parser.SetField("TotalAmount", String.valueOf(numFormat.format(TotalAmount)));
            Parser.SetField("PaidAmount", String.valueOf(numFormat.format(PaidAmount)));
            Parser.SetField("BalAmount", String.valueOf(numFormat.format(BalAmount)));
            Parser.SetField("InvoiceNo", String.valueOf(InvoiceNo));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("disabledProp", String.valueOf(disabled));
            Parser.SetField("InstallmentPlanBuffer", String.valueOf(InstallmentPlanBuffer));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/InstallmentPlanInput.html");
        } catch (Exception var11) {
            out.println(Query);
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

    void SavePlan(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        int i = 0;
        int j = 0;
        int k = 0;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String PaymentDate = "";
        String PatientMRN = request.getParameter("PatientMRN").trim();
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
        String PlanString = request.getParameter("PlanString").trim();
        int PlanStringCount = Integer.parseInt(request.getParameter("PlanStringCount").trim());
        String InstallmentPlan = request.getParameter("InstallmentPlan").trim();
//        String PlanStartDate = request.getParameter("PlanStartDate").trim();
        String PaymentAmount = "";

//        out.println(PatientMRN +"<br>");
//        out.println(InvoiceNo +"<br>");
//        out.println("InstallmentPlan: "+InstallmentPlan +"<br>");
//        out.println(PlanStartDate +"<br>");
//        out.println(BalAmount +"<br>");
//        out.println("PlanStringCount " + PlanStringCount + "<br>");
//        out.println("*********************************************************** <br> ");
        try {

            //++i increments the number before the current expression is evaluated
            //i++ increments the number after the expression is evaluated
            String[] myInfo;
            myInfo = new String[0];
            myInfo = PlanString.split("\\~");
            String PlanTableInput[][] = new String[PlanStringCount][6];
            i = j = k = 0;
            //out.println("INfo Length " + myInfo.length + "<br> ");
            for (i = 1; i < myInfo.length; i++) {
                if (myInfo[i].length() <= 0)
                    continue;

                if (myInfo[i].substring(myInfo[i].indexOf("=") + 1).equals("^"))
                    PlanTableInput[k][j] = "-";
                else
                    PlanTableInput[k][j] = myInfo[i].substring(myInfo[i].indexOf("=") + 1);

                j++;
                if (j > 5) {
                    j = 0;
                    k++;
                }
            }

            for (i = 0; i < PlanStringCount; i++) {
                if (PlanTableInput[i][0].equals(null) || PlanTableInput[i][0].length() < 1 || PlanTableInput[i][0].isEmpty())
                    continue;
                PaymentDate = "";
                String[] PayDateArr = PlanTableInput[i][4].split("\\/");
                PaymentDate = PayDateArr[2] + "-" + PayDateArr[0] + "-" + PayDateArr[1];
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".InstallmentPlan (MRN, InvoiceNo, PaymentAmount, PaymentDate, Paid,"
                        + "Status, CreatedDate ,CreatedBy, InstallmentPlan) \n" + "VALUES (?,?,?,?,0,0,now(),?,?) ");
                MainReceipt.setString(1, PatientMRN);
                MainReceipt.setString(2, InvoiceNo);
                MainReceipt.setDouble(3, Double.parseDouble(PlanTableInput[i][3]));
                MainReceipt.setString(4, PaymentDate);
                MainReceipt.setString(5, UserId);
                MainReceipt.setString(6, InstallmentPlan);
                MainReceipt.executeUpdate();
                MainReceipt.close();

//                out.println("VAL 1--> " + PlanTableInput[i][0] + "<br>");
//                out.println("VAL 2--> " + PlanTableInput[i][1] + "<br>");
//                out.println("VAL 3 --> " + PlanTableInput[i][2] + "<br>");
//                out.println("Payment Amount: --> " + PlanTableInput[i][3] + "<br>");
//                out.println("VAL 6 --> " + PlanTableInput[i][5] + "<br>");
//                out.println("VAL 5 --> " + PlanTableInput[i][4] + "<br>");
            }

            int PatientRegId = 0;
            Query = "SELECT Id FROM " + Database + ".PatientReg WHERE MRN = " + PatientMRN;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientRegId = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Message", "Installment Plan Saved Successfully ");
            Parser.SetField("FormName", "PatientUpdateInfo");
            Parser.SetField("ActionID", "GetInput&ID=" + PatientRegId);
            Parser.GenerateHtml(out, "/sftpdrive/opt/Htmls/md/Exception/Success.html");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            String str = "";
            for (i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }

    }
}
