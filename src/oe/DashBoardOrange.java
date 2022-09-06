package oe;


import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashBoardOrange
  extends HttpServlet
{
  private static final String IMG_DIR = null;
  
  public void init(ServletConfig config)
    throws ServletException
  {
    super.init(config);
  }
  
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    handleRequest(request, response);
  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    handleRequest(request, response);
  }
  
  public void handleRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    Connection conn = null;
    Connection conn2 = null;
    String UserId = "";
    String ActionID = request.getParameter("ActionID").trim();
    response.setContentType("text/html");
    PrintWriter out = new PrintWriter(response.getOutputStream());
    Services supp = new Services();
    try {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
//		   conn = DriverManager.getConnection("jdbc:mysql://132.148.155.201/oe?user=cdrasterisk444&password=cdrasterisk999");
		   conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/oe_2?user=oe&password=abc1234oe");

		   
		   
	} catch (Exception e) {
		  Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, e);
		    
	
	}
  
    ServletContext context = null;
    context = getServletContext();
    
    conn2 = Services.GetConnection(getServletContext(),1);
    if (conn2 == null)
    {
      out.println("Unable to get DB connection...");
      out.flush();
      return;
    }
    if (ActionID.equals("GetInput")) {
      GetInput(request, out, conn, context,conn2);
    }
    try
    {
      conn.close();
    }
    catch (Exception var11) {}
    out.flush();
    out.close();
  }
  
  void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, Connection conn2)
  {
    Statement stmt = null;
    ResultSet rset = null;
    String Query = "";
    Statement stmt1 = null;
    ResultSet rset1 = null;
    String Query1 = "";
    String DateNow = "";
    
    int NoofPatientsToday = 0;
    int NoofPatientsMonthly = 0;
    int SelfPayToday = 0;
    int SelfPayMonthly = 0;
    int InsuranceToday = 0;
    int InsuranceMonthly = 0;
    int FOCToday = 0;
    int FOCMonthly = 0;
    
    //String UserId = Services.GetCookie("UserId", request);
    //UserId = UserId.substring(1);
    try
    {
      Query = "Select DATE_FORMAT(now(), '%d-%m-%Y %T')";
      stmt = conn.createStatement();
      rset = stmt.executeQuery(Query);
      if (rset.next()) {
        DateNow  = rset.getString(1);
      }
      rset.close();
      stmt.close();



      Query = "Select COUNT(*) from oe_2.PatientReg where DATE_FORMAT(CreatedDate,'%Y-%m-%d') = DATE_FORMAT(now(),'%Y-%m-%d')" ;
      stmt = conn.createStatement();
      rset = stmt.executeQuery(Query);
      if (rset.next()) {
        NoofPatientsToday = rset.getInt(1);
      }
      rset.close();
      stmt.close();

      Query = " Select COUNT(*) from oe_2.PatientReg where DATE_FORMAT(CreatedDate,'%Y-%m-%d') = DATE_FORMAT(now(),'%Y-%m-%d') and SelfPayChk = 0" ;
      stmt = conn2.createStatement();
      rset = stmt.executeQuery(Query);
      if (rset.next()) {
            SelfPayToday = rset.getInt(1);
      }
      rset.close();
      stmt.close();

      Query = "Select COUNT(*) from oe_2.InsuranceInfo where DATE_FORMAT(CreatedDate,'%Y-%m-%d') = DATE_FORMAT(now(),'%Y-%m-%d') ";
      stmt = conn.createStatement();
      rset = stmt.executeQuery(Query);
      if (rset.next()) {
        InsuranceToday = rset.getInt(1);
      }
      rset.close();
      stmt.close();

//      *************************FOC****************************
//      Query = " ";
//      stmt = conn.createStatement();
//      rset = stmt.executeQuery(Query);
//      if (rset.next()) {
//        FOCToday = rset.getInt(1);
//      }
//      rset.close();
//      stmt.close();

//        ===================================monthly Work===================================
      Query = "Select COUNT(*) from oe_2.PatientReg where DATE_FORMAT(CreatedDate,'%Y-%m') = DATE_FORMAT(now(),'%Y-%m')" ;
      stmt = conn.createStatement();
      rset = stmt.executeQuery(Query);
      if (rset.next()) {
        NoofPatientsMonthly = rset.getInt(1);
      }
      rset.close();
      stmt.close();

      Query = " Select COUNT(*) from oe_2.PatientReg where DATE_FORMAT(CreatedDate,'%Y-%m') = DATE_FORMAT(now(),'%Y-%m') and SelfPayChk = 0" ;
      stmt = conn2.createStatement();
      rset = stmt.executeQuery(Query);
      if (rset.next()) {
        SelfPayMonthly = rset.getInt(1);
      }
      rset.close();
      stmt.close();

      Query = "Select COUNT(*) from oe_2.InsuranceInfo where DATE_FORMAT(CreatedDate,'%Y-%m') = DATE_FORMAT(now(),'%Y-%m') ";
      stmt = conn.createStatement();
      rset = stmt.executeQuery(Query);
      if (rset.next()) {
        InsuranceMonthly = rset.getInt(1);
      }
      rset.close();
      stmt.close();

      //      *************************FOC****************************
//      Query = " ";
//      stmt = conn.createStatement();
//      rset = stmt.executeQuery(Query);
//      if (rset.next()) {
//        FOCMonthly = rset.getInt(1);
//      }
//      rset.close();
//      stmt.close();
            
      Parsehtm Parser = new Parsehtm(request);
      Parser.SetField("NoofPatientsToday", String.valueOf(NoofPatientsToday));
      Parser.SetField("SelfPayToday", String.valueOf(SelfPayToday));
      Parser.SetField("InsuranceToday", String.valueOf(InsuranceToday));
      Parser.SetField("FOCToday", String.valueOf(FOCToday));

      Parser.SetField("NoofPatientsMonthly", String.valueOf(NoofPatientsMonthly));
      Parser.SetField("SelfPayMonthly", String.valueOf(SelfPayMonthly));
      Parser.SetField("InsuranceMonthly", String.valueOf(InsuranceMonthly));
      Parser.SetField("FOCMonthly", String.valueOf(FOCMonthly));
      Parser.SetField("DateNow", String.valueOf(DateNow));

      Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Dashboards/GraphicalMainDashboard.html");
    }
    catch (Exception var11)
    {
      Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
      out.println(var11.getMessage());
      out.flush();
      out.close();
    }
  }
  
  public void ShowGrid(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
  {
    String Query = "";
    Statement hstmt = null;
    ResultSet hrset = null;
    String Rows = "";
    String strSQL = "";
    String sysDate = "";
    String tableRows = "";
    try
    {
      hstmt = conn.createStatement();
      Query = "SELECT Date_Format(Now(),'%Y-%m-%d')";
      for (hrset = hstmt.executeQuery(Query); hrset.next();) {
        sysDate = hrset.getString(1);
      }
      hrset.close();
      hstmt.close();
      strSQL = " SELECT   concat(a.agentid,' (',ac.AgentId,')'), if(ag.phonetype=2,concat(ag.agentname,' (',ag.phonenumber,')'), ag.agentname) agentname,  Date_Format(ac.callstarttime,'%Y-%m-%d %h:%i:%s') callstart,  Date_Format(a.logintime ,'%Y-%m-%d %h:%i:%s') login,ac.callednumber  ,  CASE IfNull(cq.companyId,-1)     When -1 then   'Unknown'    When 1 then     'CSO'    When 2 then     'HEMAYAH'    When 3 then  'PRIMUS'    When 5 then     'Nadra '  Else    'Unknown'  End,TIME_TO_SEC(TIMEDIFF(Date_Format(now(),'%Y-%m-%d %h:%i:%s'),Date_Format(ac.callstarttime,'%Y-%m-%d %h:%i:%s')))/60 as Min, ifnull(TIME_TO_SEC(TIMEDIFF(Date_Format(ac.callstarttime,'%Y-%m-%d %h:%i:%s'),Date_Format(ac.registertime,'%Y-%m-%d %h:%i:%s'))),0)/60 as delay,Date_Format(ac.registertime,'%Y-%m-%d %h:%i:%s') FROM   activecalls ac, activeagents a,callsqueue cq,   agents ag  WHERE ac.agentid = a.sipid   and   ac.userid = ag.agentid  and   a.agentid = ag.agentid  and   ac.uniqueid = cq.uniqueid  AND   Date_Format(ac.callstarttime,'%Y-%m-%d')= '" + 
      
      sysDate + "' ";
      
      hstmt = conn.createStatement();
      hrset = hstmt.executeQuery(strSQL);
      tableRows = " <TABLE cellSpacing=1 cellPadding=1 width=100% border=1>   <tr bgColor=\"#040369\">    <td colspan=8 align=center>   <font color=#FFFFFF size=5 face=Verdana, Arial, Helvetica, sans-serif><strong>   Active Calls     (Inbound)           </strong></font>       </td>   </tr>   <tr bgcolor=#e2e2e2>   <td width=13% align=center><font color=#040369 size=3 face=Verdana, Arial, Helvetica, sans-serif><strong>Agent Id</strong></font></td>   <td width=15% align=center><font color=#040369 size=3 face=Verdana, Arial, Helvetica, sans-serif><strong>Agent Name</strong></font></td>  <td width=20% align=center><font color=#040369 size=3 face=Verdana, Arial, Helvetica, sans-serif><strong>Call Assign Time</strong></font></td>  <td width=20% align=center><font color=#040369 size=3 face=Verdana, Arial, Helvetica, sans-serif><strong>Call Start Time</strong></font></td>  <td width=20% align=center><font color=#040369 size=3 face=Verdana, Arial, Helvetica, sans-serif><strong>Call Duration</strong></font></td>   <td width=20% align=center><font color=#040369 size=3 face=Verdana, Arial, Helvetica, sans-serif><strong>Caller Number</strong></font></td>  <td width=20% align=center><font color=#040369 size=3 face=Verdana, Arial, Helvetica, sans-serif><strong>Delay</strong></font></td>   </tr>";
      while (hrset.next()) {
        tableRows = 
               tableRows + "<tr>" + " <td align=center><font color=#000099 size=2 face=Verdana, Arial, Helvetica, sans-serif>" + hrset.getString(1) + "</font></td>" + " <td align=center><font color=#000099 size=2 face=Verdana, Arial, Helvetica, sans-serif>" + hrset.getString(2) + " </font></td>" + " <td align=center><font color=#000099 size=2 face=Verdana, Arial, Helvetica, sans-serif>" + hrset.getString(9) + "</font></td>" + "<td align=center><font color=#000099 size=2 face=Verdana, Arial, Helvetica, sans-serif>" + hrset.getString(3) + "</font></td>" + "<td align=center><font color=#000099 size=2 face=Verdana, Arial, Helvetica, sans-serif>" + hrset.getString(7) + "</font></td>" + "<td align=center><font color=#000099 size=2 face=Verdana, Arial, Helvetica, sans-serif>" + hrset.getString(5) + " (" + hrset.getString(6) + ")</font></td>" + "<td align=center><font color=#000099 size=2 face=Verdana, Arial, Helvetica, sans-serif>" + hrset.getString(8) + "</font></td>" + "</tr>";
      }
      tableRows = tableRows + "</table>";
      hrset.close();
      hstmt.close();
      conn.close();
      out.println(tableRows);
    }
    catch (Exception e)
    {
      out.println(e.getMessage());
    }
  }
  
}