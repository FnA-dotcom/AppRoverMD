//
// Decompiled by Procyon v0.5.36
//

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
public class ERA_Posting extends HttpServlet {


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
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();
        Connection conn = null;


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
                GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetData")) {
                GetData(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetPayersAll")) {
                GetPayersAll(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetPayersActive")) {
                GetPayersActive(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetProviders")) {
                GetProviders(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else {
                out.println("Under Development");
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

    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "ERA/ERA_1.html");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    void GetData(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            ResultSet rset = null;
            String FromDateERA = !request.getParameter("FromDateERA").equals("") ? request.getParameter("FromDateERA").trim() : null;
            String ToDateERA = !request.getParameter("ToDateERA").equals("") ? request.getParameter("ToDateERA").trim() : null;
            String keywordfilter = !request.getParameter("keywordfilter").equals("") ? request.getParameter("keywordfilter").trim() : null;
            String PayerFilter = !request.getParameter("PayerFilter").equals("") ? request.getParameter("PayerFilter").trim() : null;
            String chkAmt = !request.getParameter("chkAmt").equals("") ? request.getParameter("chkAmt").trim() : null;
            String CheckDate = !request.getParameter("CheckDate").equals("") ? request.getParameter("CheckDate").trim() : null;
            String aplChecks = !request.getParameter("aplChecks").equals("") ? request.getParameter("aplChecks").trim() : null;
            StringBuffer era = new StringBuffer();
            boolean found = false;
            PreparedStatement ps = null;
            String filter = "";
            Integer filterCount = 0;

            System.out.println("FromDateERA ->> " + FromDateERA);
            System.out.println("ToDateERA ->> " + ToDateERA);
            System.out.println("keywordfilter ->> " + keywordfilter);
            System.out.println("PayerFilter ->> " + PayerFilter);
            System.out.println("chkAmt ->> " + chkAmt);
            System.out.println("CheckDate ->> " + CheckDate);
            System.out.println("aplChecks ->> " + aplChecks);
            System.out.println("data collected ");
//            if(FromDateERA!=null && ToDateERA!=null && keywordfilter!=null && PayerFilter!=null && chkAmt!=null && CheckDate!=null && aplChecks!=null){
//                ps = conn.prepareStatement("Select Applied ,Payer ,ReportDate ,Submitter ,FileName ,Checks ,Amount ,Payments ,CheckDate FROM oddasa.ERA_TABLE " +
//                                "WHERE " +
//                                "(ReportDate BETWEEN DATE_FORMAT(?,'%m/%d/%Y') AND DATE_FORMAT(?,'%m/%d/%Y')) "+
//                                "AND (CONCAT(Payer,' ',FileName,' ', Submitter,' ' ) LIKE ?) " +
//                                "AND (Payer LIKE ? ) " +
//                                "AND (Amount = ? ) " +
//                                "AND (CheckDate = DATE_FORMAT(?,'%m/%d/%Y') ) "+
//                                "ORDER BY ReportDate DESC "
//                );
//                ps.setString(1,FromDateERA);
//                ps.setString(2,ToDateERA);
//                ps.setString(3,"%" + keywordfilter + "%");
//                ps.setString(4,"%" + PayerFilter + "%");
//                ps.setString(5,chkAmt);
//                ps.setString(6,CheckDate);
//                System.out.println("Query : "+ps.toString());
//                rset = ps.executeQuery();
//            }

            if (FromDateERA != null || ToDateERA != null || keywordfilter != null || PayerFilter != null || chkAmt != null || CheckDate != null || aplChecks == null) {
                filter += " WHERE ";
                if (FromDateERA != null) {
                    filter += " ReportDate >= DATE_FORMAT('" + FromDateERA + "','%m/%d/%Y') ";
                    filterCount++;
                }
                if (ToDateERA != null) {
                    if (filterCount > 0) {
                        filter += " AND ReportDate <= DATE_FORMAT('" + ToDateERA + "','%m/%d/%Y')";
                        filterCount++;
                    } else {
                        filter += " ReportDate <= DATE_FORMAT('" + ToDateERA + "','%m/%d/%Y')";
                    }

                }
                if (keywordfilter != null) {
                    if (filterCount > 0) {
                        filter += " AND CONCAT(Payer,' ',FileName,' ', Submitter,' ' ) LIKE '%" + keywordfilter + "%' ";
                        filterCount++;
                    } else {
                        filter += " CONCAT(Payer,' ',FileName,' ', Submitter,' ' ) LIKE '%" + keywordfilter + "%' ";
                    }

                }
                if (PayerFilter != null) {
                    if (filterCount > 0) {
                        filter += " AND Payer LIKE '%" + PayerFilter + "%' ";
                        filterCount++;
                    } else {
                        filter += " Payer LIKE '%" + PayerFilter + "%' ";
                    }

                }
                if (chkAmt != null) {
                    if (filterCount > 0) {
                        filter += " AND Amount = '" + chkAmt + "' ";
                        filterCount++;
                    } else {
                        filter += "  Amount = '" + chkAmt + "' ";
                    }

                }
                if (CheckDate != null) {
                    if (filterCount > 0) {
                        filter += " AND CheckDate = DATE_FORMAT('" + CheckDate + "','%m/%d/%Y') ";
                        filterCount++;
                    } else {
                        filter += "  CheckDate = DATE_FORMAT('" + CheckDate + "','%m/%d/%Y') ";
                    }

                }
                if (aplChecks == null) {
                    if (filterCount > 0) {
                        filter += " AND Applied = 'N' ";
                        filterCount++;
                    } else {
                        filter += "  Applied = 'N' ";
                    }
                }
            } else {
                filter = "";
            }
            ps = conn.prepareStatement("Select Applied ,Payer ,ReportDate ,Submitter ,FileName ,Checks ,Amount ,Payments ,CheckDate FROM oddasa.ERA_TABLE "
                    + filter +
                    " ORDER BY ReportDate DESC "
            );
//            ps.setString(1,filter);
            System.out.println("Query : " + ps.toString());
            rset = ps.executeQuery();

            while (rset.next()) {
                era.append("<tr onclick=\"getRowVal(this);OpenBarToggle('control_sidebar');\">");

                if (rset.getString(1).equals("Y"))
                    era.append("<td style=\"text-align: center;\" ><i class=\"fa fa-check\" aria-hidden=\"true\"></i></td>");
                else
                    era.append("<td></td>");

                era.append("<td>" + rset.getString(2) + "</td>");
                era.append("<td>" + rset.getString(3) + "</td>");
                era.append("<td>" + rset.getString(4) + "</td>");
                era.append("<td>" + rset.getString(5) + "</td>");
                era.append("<td>" + rset.getString(6) + "</td>");
                era.append("<td>$" + rset.getString(7) + "</td>");
                era.append("<td>" + rset.getString(8) + "</td>");
                era.append("<td>" + rset.getString(9) + "</td>");
                era.append("</tr>");
                found = true;
            }
            if (!found) {
                out.println("0");
                return;
            }
            ps.close();

            out.println(era);
        } catch (Exception e) {
//            e.getStackTrace();
            System.out.println(e.getMessage());
        }
    }


    void GetPayersAll(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            ResultSet rset = null;
            PreparedStatement ps = null;
            StringBuffer payers = new StringBuffer();

            ps = conn.prepareStatement("Select PayerName ,InActive ,PlanName ,Address ,Prof_CPID ,Inst_CPID ,Refference_No FROM oddasa.ERA_PAYERS" +
                    " ORDER BY PayerName ASC "
            );

            rset = ps.executeQuery();

            while (rset.next()) {
                payers.append("<tr onclick=\"getRowValPayer(this);\">");

                payers.append("<td>" + rset.getString(1) + "</td>");

                if (rset.getString(2).equals("Y"))
                    payers.append("<td style=\"text-align: center;\" ><i class=\"fa fa-check\" aria-hidden=\"true\"></i></td>");
                else
                    payers.append("<td></td>");

                payers.append("<td>" + rset.getString(3) + "</td>");
                payers.append("<td>" + rset.getString(4) + "</td>");
                payers.append("<td>" + rset.getString(5) + "</td>");
                payers.append("<td>" + rset.getString(6) + "</td>");
                payers.append("<td>" + rset.getString(7) + "</td>");
                payers.append("</tr>");
            }
            ps.close();

            out.println(payers);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void GetProviders(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            ResultSet rset = null;
            PreparedStatement ps = null;
            StringBuffer payers = new StringBuffer();

            ps = conn.prepareStatement("Select Name  ,NPI ,Practice ,Submitter_No ,Tax_ID ,Prof_Mode,Inst_Mode FROM oddasa.ERA_PROVIDERS" +
                    " ORDER BY Name ASC "
            );

            rset = ps.executeQuery();

            while (rset.next()) {
                payers.append("<tr onclick=\"getRowValProvider(this);\">");
                payers.append("<td>" + rset.getString(1) + "</td>");
                payers.append("<td>" + rset.getString(2) + "</td>");
                payers.append("<td>" + rset.getString(3) + "</td>");
                payers.append("<td>" + rset.getString(4) + "</td>");
                payers.append("<td>" + rset.getString(5) + "</td>");
                payers.append("<td>" + rset.getString(6) + "</td>");
                payers.append("<td>" + rset.getString(7) + "</td>");
                payers.append("</tr>");
            }
            ps.close();

            out.println(payers);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void GetPayersActive(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            ResultSet rset = null;
            PreparedStatement ps = null;
            StringBuffer payers = new StringBuffer();

            ps = conn.prepareStatement("Select PayerName  ,PlanName ,Address ,Prof_CPID ,Inst_CPID ,Refference_No FROM oddasa.ERA_PAYERS " +
                    " WHERE InActive='N' " +
                    " ORDER BY PayerName ASC "
            );

            rset = ps.executeQuery();

            while (rset.next()) {
                payers.append("<tr onclick=\"getRowValPayer(this);\">");

                payers.append("<td>" + rset.getString(1) + "</td>");
                payers.append("<td>" + rset.getString(2) + "</td>");
                payers.append("<td>" + rset.getString(3) + "</td>");
                payers.append("<td>" + rset.getString(4) + "</td>");
                payers.append("<td>" + rset.getString(5) + "</td>");
                payers.append("<td>" + rset.getString(6) + "</td>");
                payers.append("</tr>");
            }
            ps.close();

            out.println(payers);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}