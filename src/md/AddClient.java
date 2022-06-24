package md;

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

@SuppressWarnings("Duplicates")
public class AddClient extends HttpServlet {

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private PreparedStatement pStmt = null;

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
        String DatabaseName = "";
        String UserIndex = "";
        String DirectoryName = "";
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();

        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();
        UtilityHelper helper = new UtilityHelper();

        try {
            HttpSession session = request.getSession(false);

            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

            if (session.getAttribute("UserId") != null || !session.getAttribute("UserId").equals("")) {
                UserId = session.getAttribute("UserId").toString();
                DatabaseName = session.getAttribute("DatabaseName").toString();
                FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
                UserIndex = session.getAttribute("UserIndex").toString();

                if (UserId.equals("")) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            }

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }

            if (ActionID.equals("GetInput")) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get Clients Input", "Getting Clients Data", FacilityIndex);
                GetInput(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("EditClient")) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Edit Perticular CLient", "Getting Data for particular client", FacilityIndex);
                EditClient(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else {
                out.println("Under Development ,Please Contact System Administrator!");
            }
        } catch (Exception e) {
            out.println("Exception in main... " + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
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


    public void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {
        try {
            stmt = null;
            rset = null;
            Query = "";
            StringBuilder ClientList = new StringBuilder();

            Query = " Select name, CONCAT(IFNULL(Address,''), ' ' ,IFNULL(City,''), ' ' , IFNULL(State,''), ' ' , IFNULL(ZipCode,'')), IFNULL(Phone,''), " +
                    " IFNULL(DirectoryName,''), IFNULL(dbname,''), IFNULL(NPI,''), IFNULL(proname,''), Id, " +
                    " CASE WHEN Status = 0 THEN 'ON ROVER or Active' WHEN Status = 1 THEN 'NON ROVER or InActive' ELSE '' END" +
                    " from oe.clients ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientList.append("<tr>");
                ClientList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                ClientList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                ClientList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                ClientList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                ClientList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                ClientList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                ClientList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                ClientList.append("<td align=left>" + rset.getString(9) + "</td>\n");//Client_Status
                ClientList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-5\" onclick=\"EditClient(" + rset.getInt(8) + ")\">EDIT</button></td>\n");
                ClientList.append("</tr>");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ClientList", String.valueOf(ClientList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/AddClient.html");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //out.println(e.getMessage());
        }
    }

    public void EditClient(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {
        try {
            stmt = null;
            rset = null;
            Query = "";
            int Id = Integer.parseInt(request.getParameter("id").trim());
            String name = "";
            String Address = "";
            String City = "";
            String State = "";
            String ZipCode = "";
            String Phone = "";
            String DirectoryName = "";
            String dbName = "";
            String proname = "";
            String NPI = "";
            String Status = "";

            Query = " Select name, IFNULL(Address,'') ,IFNULL(City,'') , IFNULL(State,'') , IFNULL(ZipCode,''), IFNULL(Phone,''), " +
                    " IFNULL(DirectoryName,''), IFNULL(dbname,''), IFNULL(NPI,''), IFNULL(proname,''), Status " +
                    " from oe.clients where Id = " + Id;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                name = rset.getString(1);
                Address = rset.getString(2);
                City = rset.getString(3);
                State = rset.getString(4);
                ZipCode = rset.getString(5);
                Phone = rset.getString(6);
                DirectoryName = rset.getString(7);
                dbName = rset.getString(8);
                NPI = rset.getString(9);
                proname = rset.getString(10);
                Status = rset.getString(11);
            }
            rset.close();
            stmt.close();

            out.println(name + "|" + Address + "|" + City + "|" + State + "|" + ZipCode + "|" + Phone + "|" + DirectoryName + "|" + dbName + "|" + NPI + "|" + proname + "|" + Status + "|" + Id);

        } catch (Exception e) {
            out.println("Something went wrong please Contact System administrator");
            System.out.println(e.getMessage());
            //out.println(e.getMessage());
        }
    }

    public void SaveData(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {
        try {
            stmt = null;
            rset = null;
            Query = "";
            int MaxClientId = 0;
            int Id = Integer.parseInt(request.getParameter("id").trim());
            int EditFlag = Integer.parseInt(request.getParameter("EditFlag").trim());
            String name = request.getParameter("name").trim();
            String Address = request.getParameter("Address").trim();
            String City = request.getParameter("City").trim();
            String State = request.getParameter("State").trim();
            String ZipCode = request.getParameter("ZipCode").trim();
            String Phone = request.getParameter("Phone").trim();
            String DirectoryName = request.getParameter("DirectoryName").trim();
            String dbName = request.getParameter("dbname").trim();
            String proname = request.getParameter("proname").trim();
            String NPI = request.getParameter("NPI").trim();
            String Status = request.getParameter("Status").trim();

            if (EditFlag == 0) {
                //INSERT HERE and do all the things like database creation, table creation, directory creation in all the drives,
                try {
                    Query = "Select MAX(Id) + 1 from oe.clients ";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        MaxClientId = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO oe.clients (name,directory_1,status,remotedirectory,tablename,doctype,dbname,NPI,proname,ChargeMasterTableName," +
                                    "menu,QRF_name,PRF_name,Bundle_FnName,Label_FnName,FontColor,DirectoryName,FullName,Address,Phone,City,State,ZipCode) " +
                                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                    MainReceipt.setString(1, name);
                    MainReceipt.setString(2, "/sftpdrive/users/epowerdoc/" + name.replaceAll(" ", "") + "/Charts");
                    MainReceipt.setInt(3, Integer.parseInt(Status));
                    MainReceipt.setString(4, "/sftpdrive/users/epowerdoc/" + name.replaceAll(" ", "") + "/Charts");
                    MainReceipt.setString(5, "chart_" + MaxClientId);
                    MainReceipt.setString(6, "1");//doctype
                    MainReceipt.setString(7, dbName);
                    MainReceipt.setString(8, NPI);
                    MainReceipt.setString(9, proname);
                    MainReceipt.setString(10, "");//ChrageMasterTable_name
                    MainReceipt.setString(11, "ob_admin_main_Facility.html");
                    MainReceipt.setString(12, "QuickPatientRegForm_Facilities.html");
                    MainReceipt.setString(13, "PatientRegForm_Facilities.html");
                    MainReceipt.setString(14, "GETINPUT");//bundlefunction_name
                    MainReceipt.setString(15, "GETINPUT");//Label_function_Name
                    MainReceipt.setString(16, "white");//Font_color
                    MainReceipt.setString(17, DirectoryName);
                    MainReceipt.setString(18, name);
                    MainReceipt.setString(19, Address);
                    MainReceipt.setString(20, Phone);
                    MainReceipt.setString(21, City);
                    MainReceipt.setString(22, State);
                    MainReceipt.setString(23, ZipCode);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception ex) {
                    helper.SendEmailWithAttachment("Error in AddClient ** (SaveData Insertion clients^^ ##MES#001)", context, ex, "AddClient", "SaveData", conn);
                    Services.DumException("SaveData^^  ##MES#001", "AddClient ", request, ex);
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "AddClient");
                    Parser.SetField("ActionID", "GetInput");
                    Parser.SetField("Message", "MES#001");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
                    return;
                }

                Query = "Show databases";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    if (rset.getString(1).equals(dbName)) {
                        out.println("Database Name Already Exist, Please write any other name");
                        break;
                    }
                }
                rset.close();
                stmt.close();

                Query = "CREATE DATABASE " + dbName + " CHARACTER SET latin1 COLLATE latin1_swedish_ci;";
                stmt = conn.createStatement();
                stmt.executeQuery(Query);
                stmt.close();

                //RUnning Script file for the DATABASE
/*
                ScriptRunner sr = new ScriptRunner(conn);
                //Creating a reader object
                Reader reader = new BufferedReader(new FileReader("/sftpdrive/opt/TabScripts.sql"));
                //Running the script
                sr.runScript(reader);
*/


            } else {
                //UPDATE HERE

            }

        } catch (Exception e) {
            out.println("Something went wrong please Contact System administrator");
            System.out.println(e.getMessage());
            //out.println(e.getMessage());
        }
    }


}