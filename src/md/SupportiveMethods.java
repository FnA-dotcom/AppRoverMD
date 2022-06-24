

package md;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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

@SuppressWarnings("Duplicates")
public class SupportiveMethods extends HttpServlet {
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
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();
//    conn = supp.getMysqlConn();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);

        try {
            Cookie[] cookies = request.getCookies();
            Zone = UserId = Passwd = "";
            int checkCookie = 0;
            for (int coky = 0; coky < cookies.length; coky++) {
                String cName = cookies[coky].getName();
                String cValue = cookies[coky].getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
                }
            }

            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientId = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select dbname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();
//      if(ClientId == 8){
//        Database = "oe_2";
//      }else if(ClientId == 9){
//        Database = "victoria";
//      }else if(ClientId == 10){
//        Database = "oddasa";
//      }


            if (ActionID.equals("Header")) {
                this.Header(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("LeftSideBarMenu")) {
                this.LeftSideBarMenu(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("Footer")) {
                this.Footer(request, out, conn, context, UserId, Database, ClientId);
            }
            try {
                conn.close();
            } catch (Exception ex) {
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }

    }


    public StringBuffer Header(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        StringBuffer header = new StringBuffer();
        try {
            header.append("<header class=\"main-header\">\n" +
                    "\t\t<div class=\"d-flex align-items-center logo-box justify-content-between\">\n" +
                    "\t\t\t<a href=\"#\" class=\"waves-effect waves-light nav-link rounded d-none d-md-inline-block mx-10 push-btn\" data-toggle=\"push-menu\" role=\"button\">\n" +
                    "\t\t\t\t<i class=\"ti-menu\"></i>\n" +
                    "\t\t\t</a>\t\n" +
                    "\t\t\t\n" +
                    "\t\t\t  <div class=\"logo-lg\">\n" +
                    "\t\t\t\t  \n" +
                    "\t\t\t\t  <span class=\"dark-logo\"><img src=\"/orange/images/logo-light-text.png\" alt=\"logo\"></span>\n" +
                    "\t\t\t  </div>\n" +
                    "\t\t\t\t\n" +
                    "\t\t</div>  \n" +
                    "\t\n" +
                    "\t\t<nav class=\"navbar navbar-static-top pl-10\">\n" +
                    "\t\t \n" +
                    "\t\t  <div class=\"app-menu\">\n" +
                    "\t\t\t<ul class=\"header-megamenu nav\">\n" +
                    "\t\t\t\t<li class=\"btn-group nav-item d-md-none\">\n" +
                    "\t\t\t\t\t<a href=\"#\" class=\"waves-effect waves-light nav-link rounded push-btn\" data-toggle=\"push-menu\" role=\"button\">\n" +
                    "\t\t\t\t\t\t<i class=\"ti-menu\"></i>\n" +
                    "\t\t\t\t\t</a>\n" +
                    "\t\t\t\t</li>\t\t\t\n" +
                    "\t\t\t</ul> \n" +
                    "\t\t  </div>\n" +
                    "\t\t\t\n" +
                    "\t\t  <div class=\"navbar-custom-menu r-side\">\n" +
                    "\t\t\t<ul class=\"nav navbar-nav\">\t\n" +
                    "\t\t\t\t<li class=\"btn-group nav-item d-lg-inline-flex d-none\">\n" +
                    "\t\t\t\t\t<a href=\"#\" data-provide=\"fullscreen\" class=\"waves-effect waves-light nav-link rounded full-screen\" title=\"Full Screen\">\n" +
                    "\t\t\t\t\t\t<i class=\"ti-fullscreen\"></i>\n" +
                    "\t\t\t\t\t</a>\n" +
                    "\t\t\t\t</li>\t  \n" +
                    "\t\t\t\t\n" +
                    "\t\t\t  \n" +
                    "\t\t\t  \n" +
                    "\t\t\t  <li class=\"dropdown user user-menu\">\n" +
                    "\t\t\t\t<a href=\"#\" class=\"waves-effect waves-light dropdown-toggle\" data-toggle=\"dropdown\" title=\"User\">\n" +
                    "\t\t\t\t\t<i class=\"ti-user\"></i>\n" +
                    "\t\t\t\t</a>\n" +
                    "\t\t\t\t<ul class=\"dropdown-menu animated flipInX\">\n" +
                    "\t\t\t\t  <li class=\"user-body\">\n" +
                    "\t\t\t\t\t <a class=\"dropdown-item\" href=\"#\"><i class=\"ti-user text-muted mr-2\"></i>Profile</a>\n" +
                    "\t\t\t\t\t <a class=\"dropdown-item\" href=\"#\"><i class=\"ti-wallet text-muted mr-2\"></i> My Wallet </a>\n" +
                    "\t\t\t\t\t <a class=\"dropdown-item\" href=\"#\"><i class=\"ti-settings text-muted mr-2\"></i> Settings</a>\n" +
                    "\t\t\t\t\t <div class=\"dropdown-divider\"></div>\n" +
                    "\t\t\t\t\t <a class=\"dropdown-item\" href=\"/orange_2/orange_2.Login_old?Action=Logout\"><i class=\"ti-lock text-muted mr-2\"></i> Logout </a>\n" +
                    "\t\t\t\t  </li>\n" +
                    "\t\t\t\t</ul>\n" +
                    "\t\t\t  </li>\t\n" +
                    "\t\t\t  \n" +
                    "\t\t\t  \n" +
                    "\t\t\t  <li>\n" +
                    "\t\t\t\t  <a href=\"#\" data-toggle=\"control-sidebar\" title=\"Setting\" class=\"waves-effect waves-light\">\n" +
                    "\t\t\t\t\t<i class=\"ti-settings\"></i>\n" +
                    "\t\t\t\t  </a>\n" +
                    "\t\t\t  </li>\n" +
                    "\t\t\t\t\n" +
                    "\t\t\t</ul>\n" +
                    "\t\t  </div>\n" +
                    "\t\t</nav>\n" +
                    "\t</header>");
        } catch (Exception var11) {
            header.append(var11.getMessage());
            header.append("Error in LeadStatus");
            out.flush();
            out.close();
        }
        return header;
    }

    public StringBuffer LeftSideBarMenu(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        StringBuffer asideLeft = new StringBuffer();
        String UserName = "";
        try {
            if (ClientId == 8) {
                UserName = "Orange Triangle";//orange
            } else if (ClientId == 9) {
                UserName = "Victoria";//victoria
            } else if (ClientId == 10) {
                UserName = "Odessa";//oddasa
            }
            asideLeft.append("<aside class=\"main-sidebar\">\n" +
                    "\t\n" +
                    "\t\t<section class=\"sidebar\">\t\n" +
                    "\t\t\t<div class=\"user-profile px-10 py-15\">\n" +
                    "\t\t\t\t<div class=\"d-flex align-items-center\">\t\t\t\n" +
                    "\t\t\t\t\t<div class=\"image\">\n" +
                    "\t\t\t\t\t  <img src=\"/orange/images/avatar/1.jpg\" class=\"avatar avatar-lg\" alt=\"User Image\">\n" +
                    "\t\t\t\t\t</div>\n" +
                    "\t\t\t\t\t<div class=\"info ml-10\">\n" +
                    "\t\t\t\t\t\t<p class=\"mb-0\">Welcome</p>\n" +
                    "\t\t\t\t\t\t<h5 class=\"mb-0\">" + UserName + "</h5>\n" +
                    "\t\t\t\t\t</div>\n" +
                    "\t\t\t\t</div>\n" +
                    "\t\t\t</div>\t\n" +
                    "\t\t\t\n" +
                    "\n" +
                    "\t\t  <ul class=\"sidebar-menu\" data-widget=\"tree\">\n" +
                    "\t\t\t\n" +
                    "\t\t\t<li>\n" +
                    "\t\t\t  <a href=\"/orange_2/orange_2.ManagementDashboard?ActionID=GetInput\" target=\"NewFrame1\">\n" +
                    "\t\t\t\t<i class=\"ti-dashboard\"></i>\n" +
                    "\t\t\t\t<span>Dashboard </span>\n" +
                    "\t\t\t  </a>\n" +
                    "\t\t\t</li>\t\t\t\n" +
                    "\t\t\t<li class=\"treeview\">\n" +
                    "\t\t\t  <a href=\"#\">\n" +
                    "\t\t\t\t<i class=\"ti-layout-grid2\"></i>\n" +
                    "\t\t\t\t<span>Patient Services</span>\n" +
                    "\t\t\t\t<span class=\"pull-right-container\">\n" +
                    "\t\t\t\t  <i class=\"fa fa-angle-right pull-right\"></i>\n" +
                    "\t\t\t\t</span>\n" +
                    "\t\t\t  </a>\n" +
                    "\t\t\t  <ul class=\"treeview-menu\">\n" +
                    "\t\t\t  <input type=\"hidden\" id=\"ClientId\" name=\"ClientId\" value=\"@@ClientId@@\">\n" +
                    "\t\t\t\t<li><a href=\"/orange_2/orange_2.QuickReg_NewMRN?ActionID=GetValues&ClientId=Orange\" target=\"\"><i class=\"ti-more\"></i>Quick Registration </a></li>\n" +
                    "\t\t\t\t<li><a href=\"/oe_2/oe_2.PatientReg?ActionID=GetValues&ClientId=@@ClientId@@\" target=\"NewFrame1\"><i class=\"ti-more\"></i>Patient Registration</a></li>\n" +
                    "\t\t\t\t<li><a href=\"/orange_2/orange_2.RegisteredPatients_old?ActionID=ShowReport\" target=\"NewFrame1\"><i class=\"ti-more\"></i>View Patient</a></li>\n" +
                    "\t\t\t\t<li><a href=\"/orange_2/orange_2.RegisteredPatients_old?ActionID=PatientsDocUpload\" target=\"NewFrame1\"><i class=\"ti-more\"></i>Upload Documents</a></li>\n" +
                    "\t\t\t\t<li><a href=\"/orange_2/orange_2.RegisteredPatients_old?ActionID=ViewDocuments_Input\" target=\"NewFrame1\"><i class=\"ti-more\"></i>View Documents</a></li>\n" +
                    "\t\t\t\t<li><a href=\"/orange_2/orange_2.EligibilityInquiry?ActionID=EligibilityGetInput \" target=\"NewFrame1\"><i class=\"ti-more\"></i>Eligibility Inquiry</font></a></li>\n" +
                    "\t\t\t\t<li><a href=\"/orange_2/orange_2.EligibilityInquiry2?ActionID=EligibilityGetInput \" target=\"NewFrame1\"><i class=\"ti-more\"></i>Eligibility Inquiry 2</a></li>\n" +
                    "\t\t\t\t<li><a href=\"/orange_2/orange_2.RegisteredPatients_old?ActionID=AddDoctors \" target=\"NewFrame1\"><i class=\"ti-more\"></i>Add Doctor</a></li>\n" +
                    "\t\t\t\t<li><a href=\"/orange_2/orange_2.RegisteredPatients_old?ActionID=AddAlergyInfo \" target=\"NewFrame1\"><i class=\"ti-more\"></i>Add Allergy Info</a></li>\n" +
                    "\t\t\t\t<li><a href=\"/orange_2/orange_2.PatientVisit?ActionID=SearchPatient \" target=\"NewFrame1\"><i class=\"ti-more\"></i>Search Old Patients</a></li>\n" +
                    "\t\t\t\t\n" +
                    "\t\t\t  </ul>\n" +
                    "\t\t\t</li>\n" +
                    "\t\t\t<li class=\"treeview\">\n" +
                    "\t\t\t  <a href=\"#\">\n" +
                    "\t\t\t\t<i class=\"ti-layout-grid2\"></i>\n" +
                    "\t\t\t\t<span>Payments</span>\n" +
                    "\t\t\t\t<span class=\"pull-right-container\">\n" +
                    "\t\t\t\t  <i class=\"fa fa-angle-right pull-right\"></i>\n" +
                    "\t\t\t\t</span>\n" +
                    "\t\t\t  </a>\n" +
                    "\t\t\t  <ul class=\"treeview-menu\">\t\n" +
                    "\t\t\t\t<li><a href=\"/orange_2/orange_2.RegisteredPatients_old?ActionID=CreateInvoiceInput\" target=\"NewFrame1\"><i class=\"ti-more\"></i>Create Invoice</a></li>\n" +
                    "\t\t\t\t<li><a href=\"/orange_2/orange_2.RegisteredPatients_old?ActionID=CollectPayment\" target=\"NewFrame1\"><i class=\"ti-more\"></i>Collect Payment</a></li>\n" +
                    "\t\t\t\t<li><a href=\"/orange_2/orange_2.RegisteredPatients_old?ActionID=TransactionReport_Input \" target=\"NewFrame1\"><i class=\"ti-more\"></i>Transaction Report</a></li>\n" +
                    "\t\t\t\t\n" +
                    "\t\t\t  </ul>\n" +
                    "\t\t\t</li>\n" +
                    "\t\t\t\n" +
                    "\t\t\t\t  \n" +
                    "\t\t  </ul>\n" +
                    "\t\t</section>\n" +
                    "\t\t<div class=\"sidebar-footer\">\n" +
                    "\t\t\t\n" +
                    "\t\t\t<a href=\"/orange_2/orange_2.Login_old?Action=Logout\" class=\"link\" data-toggle=\"tooltip\" title=\"\" data-original-title=\"Logout\"><i class=\"ti-lock\"></i></a>\n" +
                    "\t\t</div>\n" +
                    "\t</aside>");
        } catch (Exception var11) {
            asideLeft.append(var11.getMessage());
            asideLeft.append("Error in LeadStatus");
            out.flush();
            out.close();
        }
        return asideLeft;
    }

    public StringBuffer Footer(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        StringBuffer footer = new StringBuffer();
        try {
            footer.append("<footer class=\"main-footer\">\n" +
                    "    <div class=\"pull-right d-none d-sm-inline-block\">\n" +
                    "        <ul class=\"nav nav-primary nav-dotted nav-dot-separated justify-content-center justify-content-md-end\">\n" +
                    "\t\t  <li class=\"nav-item\">\n" +
                    "\t\t  </li>\n" +
                    "\t\t  <li class=\"nav-item\">\n" +
                    "\t\t  </li>\n" +
                    "\t\t</ul>\n" +
                    "    </div>\n" +
                    "\t  &copy; 2020 <a href=\"\">CopyRight &copy;</a> 2020. All Rights Reserved.\n" +
                    "  </footer>");
            footer.append("<aside class=\"control-sidebar\">\n" +
                    "\t  \n" +
                    "\t<div class=\"rpanel-title\"><span class=\"pull-right btn btn-circle btn-danger\"><i class=\"ion ion-close text-white\" data-toggle=\"control-sidebar\"></i></span> </div>\n" +
                    "    <ul class=\"nav nav-tabs control-sidebar-tabs\">\n" +
                    "      <li class=\"nav-item\"><a href=\"#control-sidebar-home-tab\" data-toggle=\"tab\"><i class=\"\"></i></a></li>\n" +
                    "    </ul>\n" +
                    "    <div class=\"tab-content\">\n" +
                    "      <div class=\"tab-pane\" id=\"control-sidebar-home-tab\">\n" +
                    "       \n" +
                    "      </div>\n" +
                    "      <div class=\"tab-pane\" id=\"control-sidebar-settings-tab\">\n" +
                    "          \n" +
                    "      </div>\n" +
                    "    </div>\n" +
                    "  </aside>");
            footer.append("<div class=\"control-sidebar-bg\"></div>");
        } catch (Exception var11) {
            footer.append(var11.getMessage());
            footer.append("Error in LeadStatus");
            out.flush();
            out.close();
            out.flush();
            out.close();
        }
        return footer;
    }

}
