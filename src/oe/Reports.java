package oe;

import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Enumeration;

public class Reports extends HttpServlet
{
	NumberFormat nf1;
	
	String outbound = "";
  	String inbound = "";
  	String ob_calling = "";

    public Reports()
    {
        nf1 = NumberFormat.getInstance();
    }

    public static boolean CheckDates(String FromDate, String ToDate, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        try
        {
            Query = " select unix_timestamp('" + FromDate + "') - unix_timestamp('" + ToDate + "') ";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            hrset.next();
            boolean Valid = hrset.getInt(1) <= 0;
            hrset.close();
            hstmt.close();
            return Valid;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HandleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HandleRequest(request, response);
    }
    
    public void HandleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Connection conn = null;
        Connection conn1 = null;

        String Action = null;
        StringBuffer Response = new StringBuffer();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");

        try
        {
            if(request.getParameter("Action") == null)
            {
                Action = "Home";
                return;
            }

            Action = request.getParameter("Action");

            outbound = Services.getInitParams("outbound", this.getServletContext());
            inbound = Services.getInitParams("inbound", this.getServletContext());
            ob_calling = Services.getInitParams("ob_calling", this.getServletContext());


            if(Action.compareTo("showCDR") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCDR(request, response, out, conn);
            }
            else if(Action.compareTo("showCDRServiceWise") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCDRServiceWise(request, response, out, conn);
            }
            else if(Action.compareTo("showCDRReportServiceWise") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCDRReportServiceWise (request, response, out, conn);
            }
            else if(Action.compareTo("showCDRReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCDRReport(request, response, out, conn);
            }
            else if(Action.compareTo("ScheduleReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                ScheduleReport(request, response, out, conn);
            }
            else if(Action.compareTo("ScheduleReportShow") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                ScheduleReportShow(request, response, out, conn);
            }
            else if(Action.compareTo("showCallFor") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCallFor(request, response, out, conn);
            }
            else if(Action.compareTo("showCallForReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCallForReport(request, response, out, conn);
            }
            else if(Action.compareTo("showCallSummary") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCallSummary(request, response, out, conn);
            }
            else if(Action.compareTo("showCallSummaryReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCallSummaryReport(request, response, out, conn);
            }
            else if(Action.compareTo("showAgentTalkTime") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showAgentTalkTime(request, response, out, conn);
            }
            else if(Action.compareTo("showAgentTalkTimeReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showAgentTalkTimeReport(request, response, out, conn);
            }
            else if(Action.compareTo("showComplaints") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showComplaints(request, response, out, conn);
            }
            else if(Action.compareTo("showComplaintsReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showComplaintsReport(request, response, out, conn);
            }
            else if(Action.compareTo("showWBH") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showWBH(request, response, out, conn);
            }
            else if(Action.compareTo("showWBHReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showWBHReport(request, response, out, conn);
            }
            else if(Action.compareTo("showUC") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showUC(request, response, out, conn);
            }
            else if(Action.compareTo("showUCReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showUCReport(request, response, out, conn);
            }
            else if(Action.compareTo("showUCS") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showUCS(request, response, out, conn);
            }
            else if(Action.compareTo("showUCSHW") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showUCSHW(request, response, out, conn);
            }
            else if(Action.compareTo("showUCSReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showUCSReport(request, response, out, conn);
            }
            else if(Action.compareTo("showUCSHWReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showUCSHWReport(request, response, out, conn);
            }
            else if(Action.compareTo("showCPS") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCPS(request, response, out, conn);
            }
            else if(Action.compareTo("showCPSReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCPSReport(request, response, out, conn);
            }
            else if(Action.compareTo("showCPD") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCPD(request, response, out, conn);
            }
            else if(Action.compareTo("showCPDReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCPDReport(request, response, out, conn);
            }
            else if(Action.compareTo("ViewCDRs") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                ViewCDRs(request, response, out, conn);
            }
            else if(Action.compareTo("ViewRecordedMessages") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                ViewRecordedMessages(request, response, out, conn);
            }
            else if(Action.compareTo("UpdateStats") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                UpdateStats(request, response, out, conn);
            }
            else if(Action.compareTo("PlayAudioFile") == 0)
            {
                PlayAudioFile(request, response, out);
            }
            else if(Action.compareTo("showWBH2") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showWBH2(request, response, out, conn);
            }
            else if(Action.compareTo("showWBHReport2") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showWBHReport2(request, response, out, conn);
            }
            else if(Action.compareTo("ViewCDRsVoice") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                ViewCDRsVoice(request, response, out, conn);
            }
            else if(Action.compareTo("ViewRecordings") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                ViewRecordings(request, response, out, conn);
            }
            else if(Action.compareTo("UpdateStatsVoiceRecordings") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                UpdateStatsVoiceRecordings(request, response, out, conn);
            }
            else if(Action.compareTo("PlayAudioFileVoice") == 0)
            {
                PlayAudioFileVoice(request, response, out);
            }
            else if(Action.compareTo("showHourWiseQueue") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showHourWiseQueue(request, response, out, conn);
            }
            else if(Action.compareTo("showAvgQueueTimeRpt") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showAvgQueueTimeRpt(request, response, out, conn);
            }
            else if(Action.compareTo("showCustomerProfile") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCustomerProfile(request, response, out, conn);
            }
            else if(Action.compareTo("showCustomerProfileRpt") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCustomerProfileRpt(request, response, out, conn);
            }
            else if(Action.compareTo("showCityCDR") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCityCDR(request, response, out, conn);
            }
            else if(Action.compareTo("showCityCDRReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showCityCDRReport(request, response, out, conn);
            }
            else if(Action.compareTo("showAgentSummary") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showAgentSummary(request, response, out, conn);
            }
            else if(Action.compareTo("showAgentSummaryReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showAgentSummaryReport(request, response, out, conn);
            }
            else if(Action.compareTo("showAgentAway") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showAgentAway(request, response, out, conn);
            }
            else if(Action.compareTo("showAgentAwayReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showAgentAwayReport(request, response, out, conn);
            }

            else if(Action.compareTo("ViewCDRsVoice2") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),2);
                conn1 = Services.GetConnection(this.getServletContext(),3);
                ViewCDRsVoice2(request, response, out, conn);
            }
            else if(Action.compareTo("ViewRecordings2") == 0)
            {
            	conn = Services.GetConnection(this.getServletContext(),2);
                conn1 = Services.GetConnection(this.getServletContext(),3);
                ViewRecordings2(request, response, out, conn, conn1);
            }
            else if(Action.compareTo("UpdateStatsVoiceRecordings2") == 0)
            {
            	conn = Services.GetConnection(this.getServletContext(),2);
                conn1 = Services.GetConnection(this.getServletContext(),3);
                UpdateStatsVoiceRecordings2(request, response, out, conn);
            }
            else if(Action.compareTo("PlayAudioFileVoice2") == 0)
            {
                PlayAudioFileVoice2(request, response, out);
            }
            else if(Action.compareTo("showThreshHold") == 0)
            {
            	 conn = Services.GetConnection(this.getServletContext(),1);
            	 showThreshHold(request, response, out, conn);
            }
            else if(Action.compareTo("showThreshHoldReport") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                showThreshHoldReport(request, response, out, conn);
            }
            else if(Action.compareTo("useridHtml") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                useridHtml(request, response, out, conn);
            }
            else if(Action.compareTo("useripHtml") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                useripHtml(request, response, out, conn);
            }
            else if(Action.compareTo("createUser") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                createUser(request, response, out, conn);
            }
            else if(Action.compareTo("createUserIP") == 0)
            {
                conn = Services.GetConnection(this.getServletContext(),1);
                createUserIP(request, response, out, conn);
            }
            else
            {
                out.println("Under Development ... " + Action);
            }

            conn.close();

            out.flush();
            out.close();
        }
        catch(Exception e)
        {
        	out.println("Exception in main... " + e.getMessage());
            out.flush();
            out.close();
            return;
        }

        out.flush();
        out.close();
    }
  
    public void createUserIP(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String id = request.getParameter("id");
        String ip = request.getParameter("ip");
        String sipid1 = request.getParameter("sipid");

        int sipid=Integer.parseInt(sipid1);
       // int ip=Integer.parseInt(ip1);

        try
        {
        	/* hstmt = conn.createStatement();
    		 Query =" select count(*) from tes_inbound.sip_accounts where ip='"+ip+"'";
    	        hrset = hstmt.executeQuery(Query);
    	        hrset.next();
    	        int checkIp=hrset.getInt(1);
    	        hrset.close();

    	        if (checkIp!=0)
    	        {
    	        	 out.println("IP Already Registered.");
    	        	 return;
    	        }

        	*/
        	 hstmt = conn.createStatement();
        	 Query ="insert into tes_inbound.sip_accounts (sipid,ip) " +
        	 		"values ('"+sipid+"','"+ip+"')" ;
              hstmt.execute(Query);
              hstmt.close();

              /*
               hstmt = conn.createStatement();
         	   Query ="update tes_inbound.agents set cli='"+sipid+"' where agentid='"+id+"'";
         	   hstmt.executeUpdate(Query);
               hstmt.close();
             */

              out.println("IP Updated Successfully.");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...123");
        }
    }
  
    public void createUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String id = request.getParameter("id");
        String pass = request.getParameter("pass");
        String name = request.getParameter("name");


        try
        {
        	 hstmt = conn.createStatement();
        	 Query ="insert into tes_inbound.agents (agentid, AgentName, password, LoggedIn, rights, phonetype, phonenumber, agentonjob, indexptr, cli, agenttype, call_allowed) " +
        	 		"values ('"+id+"','"+name+"','"+pass+"','N','111','1',null,'Y',1,null,1,3)" ;
              hstmt.execute(Query);
              hstmt.close();

              hstmt = conn.createStatement();
              Query="insert into tesob_calling.faysal_users (userid,username) " +
              		"values ('"+id+"','"+name+"')";
              hstmt.execute(Query);
              hstmt.close();

              hstmt = conn.createStatement();
              Query="insert into tesoutbound.users (USERID, USERTYPE, PASSWORD, USERNAME, BLOCKED, RIGHTS, DEFINEBY, DEFINEDATE, UPCHK, ccuid, CITYID) " +
              		"values ('"+id+"',2,'"+pass+"','"+name+"','N','01111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111'," +
              				"'ADMIN',now(),2,1,1)";
              hstmt.execute(Query);
              hstmt.close();



              hstmt = conn.createStatement();
              Query="insert into tesoutbound.user_workgroup (USERID, WORKGROUPID) " +
              		"values ('"+id+"',1)";
              hstmt.execute(Query);
              hstmt.close();


              hstmt = conn.createStatement();
              Query="insert into tesoutbound.user_service (USERID, SERVICEID) values " +
              		"('"+id+"',1)";
              hstmt.execute(Query);
              hstmt.close();

              out.println("User Created Successfully.");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }
  
    public void useripHtml(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";

        try
        {

            Parsehtm Parser = new Parsehtm(request);

            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/useripHtml.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }
    
    public void useridHtml(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";

        try
        {

            Parsehtm Parser = new Parsehtm(request);

            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/useridHtml.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }
    
    public void showThreshHoldReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String FHour = request.getParameter("FHour");
        String FMin = request.getParameter("FMin");
        String FSec = request.getParameter("FSec");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String THour = request.getParameter("THour");
        String TMin = request.getParameter("TMin");
        String TSec = request.getParameter("TSec");
        String Calling = request.getParameter("Calling");
        String AgentId = request.getParameter("AgentId");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        String AgentIdChk = "";
        String Condition = "";
        //StringBuffer CDRList = new StringBuffer();
        //StringBuffer Response = new StringBuffer();

        long TimeDiff=0;
        String Min="";
        String Sec="";

        try
        {

            if(FDay.length() < 2)
                FDay = "0" + FDay;

            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;


            FromDate = FYear + "-" + FMonth + "-" + FDay;
           // ToDate = TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59";
            //ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00) to (" + TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59)";

           // out.println("OKK-");
            hstmt = conn.createStatement();
  		    Query ="SELECT COUNT(*) FROM un_attended_calls  WHERE TIMESTAMPDIFF(SECOND,calltime,endtime)<15 and assigntime like '"+FromDate+ "%'";
  	        hrset = hstmt.executeQuery(Query);
  	        hrset.next();
  	        String before15=hrset.getString(1);
  	        hrset.close();

  	        hstmt = conn.createStatement();
		    Query ="SELECT COUNT(*) FROM un_attended_calls where assigntime like '"+FromDate+ "%'";
	        hrset = hstmt.executeQuery(Query);
	        hrset.next();
	        String total=hrset.getString(1);
	        hrset.close();

	        hstmt = conn.createStatement();
  		    Query ="SELECT COUNT(*) FROM un_attended_calls  WHERE TIMESTAMPDIFF(SECOND,calltime,endtime)>15 and assigntime like '"+FromDate+ "%'";
  	        hrset = hstmt.executeQuery(Query);
  	        hrset.next();
  	        String after15=hrset.getString(1);
  	        hrset.close();


            Parsehtm Parser = new Parsehtm(request);
           Parser.SetField("after15", after15);
            Parser.SetField("before15", before15);
            Parser.SetField("total", total);
            Parser.SetField("date", FromDate);
           // Parser.SetField("ForDate", ForDate);
            //Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showThreshHoldReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }
    
    public void showThreshHold(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer FHour = new StringBuffer();
        StringBuffer FMin = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer THour = new StringBuffer();
        StringBuffer TMin = new StringBuffer();
        StringBuffer TSec = new StringBuffer();
        StringBuffer FSec = new StringBuffer();
        StringBuffer AgentList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Query = " select agentid from agents where agenttype=1 order by agentid ";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            AgentList.append("<option class=Inner value=\"-1\"> All </option>");
            for(; hrset.next(); AgentList.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(1) + "</option>"));
            hrset.close();
            hstmt.close();

            Services.GetCalendar(FDay, FMonth, FYear, FHour, FMin);
            Services.GetCalendar(TDay, TMonth, TYear, THour, TMin);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("AgentList", AgentList.toString());
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("FHourList", FHour.toString());
            Parser.SetField("FMinList", FMin.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.SetField("THourList", FHour.toString());
            Parser.SetField("TMinList", FMin.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showThreshHold.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showCDRServiceWise(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer FHour = new StringBuffer();
        StringBuffer FMin = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer THour = new StringBuffer();
        StringBuffer TMin = new StringBuffer();
        StringBuffer AgentList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Query = " select agentid from agents where agenttype=1 order by agentid ";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            AgentList.append("<option class=Inner value=\"-1\"> All </option>");
            for(; hrset.next(); AgentList.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(1) + "</option>"));
            hrset.close();
            hstmt.close();
            Services.GetCalendar(FDay, FMonth, FYear, FHour, FMin);
            Services.GetCalendar(TDay, TMonth, TYear, THour, TMin);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("AgentList", AgentList.toString());
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("FHourList", FHour.toString());
            Parser.SetField("FMinList", FMin.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.SetField("THourList", FHour.toString());
            Parser.SetField("TMinList", FMin.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showCDRServiceWise.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showCDR(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer FHour = new StringBuffer();
        StringBuffer FMin = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer THour = new StringBuffer();
        StringBuffer TMin = new StringBuffer();
        StringBuffer AgentList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
           /* Query = " select agentid from agents where agenttype=1 order by agentid ";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            AgentList.append("<option class=Inner value=\"-1\"> All </option>");
            for(; hrset.next(); AgentList.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(1) + "</option>"));
            hrset.close();
            hstmt.close();
           */ Services.GetCalendar(FDay, FMonth, FYear, FHour, FMin);
            Services.GetCalendar(TDay, TMonth, TYear, THour, TMin);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("AgentList", AgentList.toString());
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("FHourList", FHour.toString());
            Parser.SetField("FMinList", FMin.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.SetField("THourList", FHour.toString());
            Parser.SetField("TMinList", FMin.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showCDR.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }
    
    public void showCDRReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String FHour = request.getParameter("FHour");
        String FMin = request.getParameter("FMin");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String THour = request.getParameter("THour");
        String TMin = request.getParameter("TMin");
        String Calling = request.getParameter("Calling");
        String AgentId = "-1";
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        String AgentIdChk = "";
        String Condition = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        String UserId = Services.GetCookie("UserId", request).trim();

        long TimeDiff=0;
        String Min="";
        String Sec="";

        try
        {
            if(FDay.length() < 2)
                FDay = "0" + FDay;
            if(TDay.length() < 2)
                TDay = "0" + TDay;
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;

            FromDate = FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00";
            ToDate = TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59";
            ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00) to (" + TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59)";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            if(Calling.length() > 0)
                Condition = " and src like '%" + Calling + "%' ";

            if(!AgentId.equals("-1"))
                AgentIdChk = " and b.agentid='" + AgentId + "'";
            else
                AgentIdChk = "";

            int SNo = 0;
            Query = " select b.agentid, a.callingnumber, DATE_FORMAT(a.starttime, '%Y-%m-%d %T'), DATE_FORMAT(a.endtime, '%Y-%m-%d %T'), " +
            		//" round(TIMESTAMPDIFF(SECOND,a.starttime,a.endtime)/60) " +
            		"  TIMESTAMPDIFF(SECOND,a.starttime,a.endtime) " +
            		" from cdr a, agents b  where a.userid=b.agentid and b.agenttype=1 " + AgentIdChk +
            		" and a.calltime between '" + FromDate + "' and '" + ToDate + "' " + Condition + " order by a.calltime ";

            Query = "select calldate,clid,src,dst,duration,disposition,uniqueid,mixmon from asteriskcdrdb.cdr " +
            		"where calldate between '" + FromDate + "' and '" + ToDate + "' " + Condition + " and companyid='"+UserId+"'  order by calldate";


            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next();)
            {
            	TimeDiff = hrset.getLong(5);

            	Min  = String.valueOf(TimeDiff/60);
            	Sec  = String.valueOf(TimeDiff  - ((TimeDiff/60)*60) );

            	if (Min.trim().length()==1)
            	      Min ="0"+Min;

            	if (Sec.trim().length()==1)
            		Sec ="0"+Sec;

                if(++SNo % 2 == 0)
                {
                    CDRList.append("<tr class=\"Inner\">\n");
                    CDRList.append("<td align=center>" + SNo + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(1) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(2) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(3) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(4) + "</td>\n");
                    CDRList.append("<td align=center>" + Min +":"+ Sec +   "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(6) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(7) + "</td>\n");
                  //  CDRList.append("<td align=left>" + hrset.getString(8) + "</td>\n");
                    CDRList.append("</tr>\n");
                } else
                {
                    CDRList.append("<tr class=\"Inner\">\n");
                    CDRList.append("<td align=center>" + SNo + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(1) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(2) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(3) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(4) + "</td>\n");
                    CDRList.append("<td align=center>" + Min +":"+ Sec +   "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(6) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(7) + "</td>\n");
                    CDRList.append("</tr>\n");
                }
            }
            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showCDRReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }
    
    public void ScheduleReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer FHour = new StringBuffer();
        StringBuffer FMin = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer THour = new StringBuffer();
        StringBuffer TMin = new StringBuffer();
        StringBuffer AgentList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
           /* Query = " select agentid from agents where agenttype=1 order by agentid ";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            AgentList.append("<option class=Inner value=\"-1\"> All </option>");
            for(; hrset.next(); AgentList.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(1) + "</option>"));
            hrset.close();
            hstmt.close();
           */ Services.GetCalendar(FDay, FMonth, FYear, FHour, FMin);
            Services.GetCalendar(TDay, TMonth, TYear, THour, TMin);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("AgentList", AgentList.toString());
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("FHourList", FHour.toString());
            Parser.SetField("FMinList", FMin.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.SetField("THourList", FHour.toString());
            Parser.SetField("TMinList", FMin.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ScheduleReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }
    
    public void ScheduleReportShow(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String FHour = request.getParameter("FHour");
        String FMin = request.getParameter("FMin");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String THour = request.getParameter("THour");
        String TMin = request.getParameter("TMin");
        String Calling = request.getParameter("Calling");
        String AgentId = "-1";
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        String AgentIdChk = "";
        String Condition = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();

        long TimeDiff=0;
        String Min="";
        String Sec="";

        try
        {
            if(FDay.length() < 2)
                FDay = "0" + FDay;
            if(TDay.length() < 2)
                TDay = "0" + TDay;
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;

            FromDate = FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00";
            ToDate = TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59";
            ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00) to (" + TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59)";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            if(Calling.length() > 0)
                Condition = " and mobile like '%" + Calling + "%' ";

            if(!AgentId.equals("-1"))
                AgentIdChk = " and b.agentid='" + AgentId + "'";
            else
                AgentIdChk = "";

            int SNo = 0;
                  Query = "Select a.numberindex,a.mobile,a.posttime,a.calltime,a.called,a.dialstatus,a.retry,a.lastretrytime,a.CUST_NAME,b.inputdesc from numbers a,inputs b  where posttime >= '"+FromDate+"' AND posttime <= '"+ToDate+"' " + Condition + " and a.input1=b.sno order by numberindex";


            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next();)
            {


                if(++SNo % 2 == 0)
                {
                    CDRList.append("<tr class=\"Inner\">\n");
                    CDRList.append("<td align=center>" + SNo + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(1) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(9) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(2) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(3) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(4) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(5) +  "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(6) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(7) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(8) + "</td>\n");
                   // CDRList.append("<td align=center>" + hrset.getString(9) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(10) + "</td>\n");
                    CDRList.append("</tr>\n");
                } else
                {
                    CDRList.append("<tr class=\"Inner\">\n");
                    CDRList.append("<td align=center>" + SNo + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(1) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(9) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(2) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(3) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(4) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(5) +  "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(6) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(7) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(8) + "</td>\n");
                   // CDRList.append("<td align=center>" + hrset.getString(9) + "</td>\n");
                    CDRList.append("<td align=center>" + hrset.getString(10) + "</td>\n");
                    CDRList.append("</tr>\n");
                }
            }
            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ScheduleReportShow.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }
    
    public void showCDRReportServiceWise (HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String FHour = request.getParameter("FHour");
        String FMin = request.getParameter("FMin");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String THour = request.getParameter("THour");
        String TMin = request.getParameter("TMin");

        String AgentId = request.getParameter("AgentId");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        String AgentIdChk = "";
        String Condition = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            if(FDay.length() < 2)
                FDay = "0" + FDay;
            if(TDay.length() < 2)
                TDay = "0" + TDay;
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;

            FromDate = FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00";
            ToDate = TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59";
            ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00) to (" + TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59)";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }


            if(!AgentId.equals("-1"))
                AgentIdChk = " and b.agentid='" + AgentId + "'";
            else
                AgentIdChk = "";

            int SNo = 0;
            Query = " select companyid ,count(*) " +
            		" from cdr a " +
            		" where  a.calltime between '" + FromDate +
            		"' and '" + ToDate + "' " + Condition + "   group by companyid   order by a.calltime ";
            hstmt = conn.createStatement();
            String Company="";
            for(hrset = hstmt.executeQuery(Query); hrset.next();)
            {
                    CDRList.append("<tr class=\"InnerGrey\">\n");
                    CDRList.append("<td align=left>" + ++SNo + "</td>\n");
                    if (hrset.getString(1).compareTo("1")==0)
                    CDRList.append("<td align=left>Telecard</td>\n");
                    else if (hrset.getString(1).compareTo("3")==0)
                        CDRList.append("<td align=left>SCO</td>\n");
                    else if (hrset.getString(1).compareTo("7")==0)
                        CDRList.append("<td align=left>Smart Office</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");

                    CDRList.append("</tr>\n");
                }

            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showCDRReportServiceWise.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
            e.printStackTrace(out);
        }
    }

    public void showCallFor(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Services.GetCalendar(FDay, FMonth, FYear);
            Services.GetCalendar(TDay, TMonth, TYear);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showCallFor.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showCallForReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String CallFor = request.getParameter("CallFor");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        String CallTypeChk = "";
        String ReportFor = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            if(FDay.length() < 2)
                FDay = "0" + FDay;
            if(TDay.length() < 2)
                TDay = "0" + TDay;
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;

            FromDate = FYear + "-" + FMonth + "-" + FDay + " 00:00:00";
            ToDate = TYear + "-" + TMonth + "-" + TDay + " 23:59:59";
            ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + ") to (" + TYear + "-" + TMonth + "-" + TDay + ")";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            if(!CallFor.equals("-1"))
                CallTypeChk = " and ifnull(a.menuid,0)=" + CallFor;
            else
                CallTypeChk = "";
            try
            {
                int option = Integer.parseInt(CallFor);
                switch(option)
                {
                case 0: // '\0'
                    ReportFor = "CRO";
                    break;

                case 1: // '\001'
                    ReportFor = "Flight Reservation";
                    break;

                case 2: // '\002'
                    ReportFor = "Flight Schedule";
                    break;

                case 3: // '\003'
                    ReportFor = "Flight Inquiry";
                    break;

                case 4: // '\004'
                    ReportFor = "Record Message";
                    break;

                default:
                    ReportFor = "All";
                    break;
                }
            }
            catch(Exception exception) { }
            int SNo = 0;
            Query = " select c.agentid, a.callingnumber,  DATE_FORMAT(a.calltime, '%Y-%m-%d %T'), b.description  from cdr a, calltype b, agents c  where a.userid=c.agentid and c.agenttype=1  and b.calltype=ifnull(a.menuid,0) " + CallTypeChk + " and a.calltime between '" + FromDate + "' and '" + ToDate + "' " + " order by a.calltime ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next(); CDRList.append("</tr>\n"))
            {
                if(++SNo % 2 == 0)
                    CDRList.append("<tr class=\"Inner\">\n");
                else
                    CDRList.append("<tr class=\"InnerGrey\">\n");
                CDRList.append("<td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(4) + "</td>\n");
            }

            hrset.close();
            hstmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("ReportFor", ReportFor);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showCallForReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showCallSummary(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Services.GetCalendar(FDay, FMonth, FYear);
            Services.GetCalendar(TDay, TMonth, TYear);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showCallSummary.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showCallSummaryReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String CallFor = request.getParameter("CallFor");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        String CallTypeChk = "";
        String ReportFor = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            if(FDay.length() < 2)
                FDay = "0" + FDay;
            if(TDay.length() < 2)
                TDay = "0" + TDay;
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;

            FromDate = FYear + "-" + FMonth + "-" + FDay + " 00:00:00";
            ToDate = TYear + "-" + TMonth + "-" + TDay + " 23:59:59";
            ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + ") to (" + TYear + "-" + TMonth + "-" + TDay + ")";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            if(!CallFor.equals("-1"))
                CallTypeChk = " and ifnull(a.menuid,0)=" + CallFor;
            else
                CallTypeChk = "";
            try
            {
                int option = Integer.parseInt(CallFor);
                switch(option)
                {
                case 0: // '\0'
                    ReportFor = "CRO";
                    break;

                case 1: // '\001'
                    ReportFor = "Flight Reservation";
                    break;

                case 2: // '\002'
                    ReportFor = "Flight Schedule";
                    break;

                case 3: // '\003'
                    ReportFor = "Flight Inquiry";
                    break;

                case 4: // '\004'
                    ReportFor = "Record Message";
                    break;

                default:
                    ReportFor = "All";
                    break;
                }
            }
            catch(Exception exception) { }
            int SNo = 0;
            Query = " select b.description, count(*)  from cdr a, calltype b  where b.calltype=ifnull(a.menuid,0) " + CallTypeChk + " and a.calltime between '" + FromDate + "' and '" + ToDate + "' " + " group by b.description " + " order by b.description ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next(); CDRList.append("</tr>\n"))
            {
                if(++SNo % 2 == 0)
                    CDRList.append("<tr class=\"Inner\">\n");
                else
                    CDRList.append("<tr class=\"InnerGrey\">\n");
                CDRList.append("<td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
            }

            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("ReportFor", ReportFor);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showCallSummaryReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showAgentTalkTime(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer AgentList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Query = " select agentid from agents where agenttype=1 order by agentid ";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            AgentList.append("<option class=Inner value=\"-1\"> All </option>");
            for(; hrset.next(); AgentList.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(1) + "</option>"));
            hrset.close();
            hstmt.close();
            Services.GetCalendar(FDay, FMonth, FYear);
            Services.GetCalendar(TDay, TMonth, TYear);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("AgentList", AgentList.toString());
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showAgentTalkTime.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showAgentTalkTimeReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String AgentId = request.getParameter("AgentId");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        String AgentIdChk = "";
        String ReportFor = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            if(FDay.length() < 2)
                FDay = "0" + FDay;
            if(TDay.length() < 2)
                TDay = "0" + TDay;
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;

            FromDate = FYear + "-" + FMonth + "-" + FDay + " 00:00:00";
            ToDate = TYear + "-" + TMonth + "-" + TDay + " 23:59:59";
            ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + ") to (" + TYear + "-" + TMonth + "-" + TDay + ")";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            if(!AgentId.equals("-1"))
            {
                AgentIdChk = " and b.agentid='" + AgentId + "'";
                ReportFor = AgentId;
            } else
            {
                AgentIdChk = "";
                ReportFor = "All Agents";
            }
            int SNo = 0;
            Query = " select b.agentid, round(sum(TIMESTAMPDIFF(SECOND,starttime,endtime))/60)  from cdr a, agents b  where a.userid=b.agentid and b.agenttype=1  and a.calltime between '" + FromDate + "' and '" + ToDate + "' " + AgentIdChk + " group by b.agentid " + " order by b.agentid ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next(); CDRList.append("</tr>\n"))
            {
                if(++SNo % 2 == 0)
                    CDRList.append("<tr class=\"Inner\">\n");
                else
                    CDRList.append("<tr class=\"InnerGrey\">\n");
                CDRList.append("<td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                CDRList.append("<td align=right>" + hrset.getString(2) + "</td>\n");
            }

            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("ReportFor", ReportFor);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showAgentTalkTimeReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showComplaints(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Services.GetCalendar(FDay, FMonth, FYear);
            Services.GetCalendar(TDay, TMonth, TYear);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showComplaints.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showComplaintsReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String CallType = request.getParameter("CallType");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        String CallTypeChk = "";
        String ReportFor = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            if(FDay.length() < 2)
                FDay = "0" + FDay;
            if(TDay.length() < 2)
                TDay = "0" + TDay;
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;

            FromDate = FYear + "-" + FMonth + "-" + FDay + " 00:00:00";
            ToDate = TYear + "-" + TMonth + "-" + TDay + " 23:59:59";
            ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + ") to (" + TYear + "-" + TMonth + "-" + TDay + ")";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            if(!CallType.equals("-1"))
                CallTypeChk = " and ifnull(a.calltype,1)=" + CallType;
            else
                CallTypeChk = "";
            try
            {
                int option = Integer.parseInt(CallType);
                switch(option)
                {
                case 1: // '\001'
                    ReportFor = "Inquiry";
                    break;

                case 2: // '\002'
                    ReportFor = "Complaints";
                    break;

                case 3: // '\003'
                    ReportFor = "Suggestion";
                    break;

                default:
                    ReportFor = "All";
                    break;
                }
            }
            catch(Exception exception) { }
            int SNo = 0;
            int Call = 2;
            String AgentId = "";
            String Caller = "";
            String CallFrom = "";
            String CallDesc = "";
            String CallDate = "";
            String Remarks = "";
            Query = " select IFNULL(callername,'N/A'), IFNULL(callernumber,'N/A'),  IFNULL(calltype,1), IFNULL(DATE_FORMAT(dated,'%Y-%m-%d %T'),'N/A'),  IFNULL(remarks,'N/A'), AgentId  from complaints  where DATE_FORMAT(dated,'%Y-%m-%d %T') between '" + FromDate + "' and '" + ToDate + "' " + CallTypeChk + " order by dated ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next(); CDRList.append("</tr>\n"))
            {
                AgentId = Caller = CallFrom = CallDesc = CallDate = Remarks = "N/A";
                Call = 2;
                if(++SNo % 2 == 0)
                    CDRList.append("<tr class=\"Inner\">\n");
                else
                    CDRList.append("<tr class=\"InnerGrey\">\n");
                Caller = hrset.getString(1);
                CallFrom = hrset.getString(2);
                Call = hrset.getInt(3);
                CallDate = hrset.getString(4);
                Remarks = hrset.getString(5);
                AgentId = hrset.getString(6);
                switch(Call)
                {
                case 1: // '\001'
                    CallDesc = "Inquiry";
                    break;

                case 2: // '\002'
                    CallDesc = "Complaints";
                    break;

                case 3: // '\003'
                    CallDesc = "Suggestion";
                    break;

                default:
                    CallDesc = "All";
                    break;
                }
                CDRList.append("<td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + AgentId + "</td>\n");
                CDRList.append("<td align=left>" + Caller + "</td>\n");
                CDRList.append("<td align=left>" + CallFrom + "</td>\n");
                CDRList.append("<td align=left>" + CallDesc + "</td>\n");
                CDRList.append("<td align=left>" + CallDate + "</td>\n");
                CDRList.append("<td align=left>" + Remarks + "</td>\n");
            }

            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("ReportFor", ReportFor);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showComplaintsReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showWBH(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer FHour = new StringBuffer();
        StringBuffer FMin = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer THour = new StringBuffer();
        StringBuffer TMin = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Services.GetCalendar(FDay, FMonth, FYear, FHour, FMin);
            Services.GetCalendar(TDay, TMonth, TYear, THour, TMin);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("FHourList", FHour.toString());
            Parser.SetField("FMinList", FMin.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.SetField("THourList", FHour.toString());
            Parser.SetField("TMinList", FMin.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showWBH.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showWBHReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String FHour = request.getParameter("FHour");
        String FMin = request.getParameter("FMin");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String THour = request.getParameter("THour");
        String TMin = request.getParameter("TMin");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        StringBuffer WBHList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            if(FDay.length() < 2)
                FDay = "0" + FDay;
            if(TDay.length() < 2)
                TDay = "0" + TDay;
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;

            FromDate = FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00";
            ToDate = TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59";
            ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00) to (" + TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59)";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            int SNo = 0;
            Query = " select date1, sum(attended), sum(unattended), sum(talktime),  " +
            		" round((sum(attended)/(sum(attended)+sum(unattended)))*100) SL, sum(acd) " +
            		" from " +
            		"(  select callday date1, count(*) attended, 0 unattended,  " +
            		" round(sum(unix_timestamp(endtime)-unix_timestamp(starttime))/60) talktime,  " +
            		" round((sum(unix_timestamp(endtime)-unix_timestamp(starttime))/60)/count(*),2) acd " +
            		"  from cdr  where calltime between '" + FromDate + "' and '" + ToDate + "' " +
            		" group by callday " +
            		" union all " +
            		" select callday date1, 0 attended, count(*) unattended, " +
            		" 0 talktime, 0  acd " + " from un_attended_calls " + " where calltime between '" + FromDate +
            		"' and '" + ToDate + "' " +
            		" group by callday " + " ) a group by date1 order by date1 ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next(); WBHList.append("</tr>\n"))
            {
                if(++SNo % 2 == 0)
                    WBHList.append("<tr class=\"Inner\">\n");
                else
                    WBHList.append("<tr class=\"InnerGrey\">\n");
                WBHList.append("<td align=left>" + SNo + "</td>\n");
                WBHList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                WBHList.append("<td align=right>" + hrset.getString(2) + "</td>\n");
                WBHList.append("<td align=right>" + hrset.getString(3) + "</td>\n");
                WBHList.append("<td align=right>" + hrset.getString(4) + "</td>\n");
                WBHList.append("<td align=right>" + hrset.getString(5) + " %</td>\n");
                WBHList.append("<td align=right>" + hrset.getString(6) + "</td>\n");
            }

            hrset.close();
            hstmt.close();
            WBHList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("WBHList", WBHList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showWBHReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showUC(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer FHour = new StringBuffer();
        StringBuffer FMin = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer THour = new StringBuffer();
        StringBuffer TMin = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Services.GetCalendar(FDay, FMonth, FYear, FHour, FMin);
            Services.GetCalendar(TDay, TMonth, TYear, THour, TMin);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("FHourList", FHour.toString());
            Parser.SetField("FMinList", FMin.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.SetField("THourList", FHour.toString());
            Parser.SetField("TMinList", FMin.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showUC.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showUCReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String FHour = request.getParameter("FHour");
        String FMin = request.getParameter("FMin");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String THour = request.getParameter("THour");
        String TMin = request.getParameter("TMin");
        String CallStatus = request.getParameter("CallStatus");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        String AssignChk = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            if(FDay.length() < 2)
                FDay = "0" + FDay;
            if(TDay.length() < 2)
                TDay = "0" + TDay;
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;

            FromDate = FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00";
            ToDate = TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59";
            ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00) to (" + TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59)";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            if(CallStatus.equals("-1"))
                AssignChk = "";
            else
                AssignChk = " and assigned=" + CallStatus;
            int SNo = 0;
            Query = " select CASE WHEN agentid = 'null' THEN 'N/A' ELSE agentid END agent, " +
            		" callerid,  CASE WHEN agentid = 'null' THEN 'Unassigned' ELSE 'Assigned' END assigned,  " +
            		" DATE_FORMAT(calltime, '%Y-%m-%d %T') starttime, DATE_FORMAT(endtime, '%Y-%m-%d %T') endtime, " +
            		" TIMESTAMPDIFF(SECOND,calltime,endtime) duration  " +
            		" from un_attended_calls  where ignoredcall='N' " +
            		"and calltime between '" + FromDate + "' and '" + ToDate + "' " +
            		AssignChk + " order by calltime ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next(); CDRList.append("</tr>\n"))
            {
                if(++SNo % 2 == 0)
                    CDRList.append("<tr class=\"Inner\">\n");
                else
                    CDRList.append("<tr class=\"InnerGrey\">\n");
                CDRList.append("<td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(5) + "</td>\n");
                CDRList.append("<td align=right>" + hrset.getString(6) + "</td>\n");
            }

            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showUCReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }
    
    public void showUCS(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer FHour = new StringBuffer();
        StringBuffer FMin = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer THour = new StringBuffer();
        StringBuffer TMin = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Services.GetCalendar(FDay, FMonth, FYear, FHour, FMin);
            Services.GetCalendar(TDay, TMonth, TYear, THour, TMin);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("FHourList", FHour.toString());
            Parser.SetField("FMinList", FMin.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.SetField("THourList", FHour.toString());
            Parser.SetField("TMinList", FMin.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showUCS.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showUCSHW(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer FHour = new StringBuffer();
        StringBuffer FMin = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer THour = new StringBuffer();
        StringBuffer TMin = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Services.GetCalendar(FDay, FMonth, FYear, FHour, FMin);
            Services.GetCalendar(TDay, TMonth, TYear, THour, TMin);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("FHourList", FHour.toString());
            Parser.SetField("FMinList", FMin.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.SetField("THourList", FHour.toString());
            Parser.SetField("TMinList", FMin.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showUCSHW.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }
    
    public void showUCSReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String FHour = request.getParameter("FHour");
        String FMin = request.getParameter("FMin");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String THour = request.getParameter("THour");
        String TMin = request.getParameter("TMin");
        String CallStatus = request.getParameter("CallStatus");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        String AssignChk = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            if(FDay.length() < 2)
                FDay = "0" + FDay;
            if(TDay.length() < 2)
                TDay = "0" + TDay;
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;

            FromDate = FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00";
            ToDate = TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59";
            ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00) to (" + TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59)";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            if(CallStatus.equals("-1"))
                AssignChk = "";
            else
                AssignChk = " and assigned=" + CallStatus;
            int SNo = 0;
            Query = " select CASE WHEN agentid = 'null' THEN 'n/a' ELSE agentid END, " +
            		" CASE WHEN  agentid = 'null' THEN 'Unassigned' ELSE 'Assigned' END,  count(*), sum(TIMESTAMPDIFF(SECOND,calltime,endtime)/60) duration  "+
                    " from un_attended_calls  where ignoredcall='N' and calltime between '" + FromDate +
                    "' and '" + ToDate + "' " + AssignChk +
                    " group by CASE WHEN agentid = 'null' THEN 'n/a' ELSE agentid END, "
                    + " CASE WHEN  agentid = 'null' THEN 'Unassigned' ELSE 'Assigned' END " + " order by 1 ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next(); CDRList.append("</tr>\n"))
            {
                if(++SNo % 2 == 0)
                    CDRList.append("<tr class=\"Inner\">\n");
                else
                    CDRList.append("<tr class=\"InnerGrey\">\n");
                CDRList.append("<td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                CDRList.append("<td align=right>" + hrset.getString(3) + "</td>\n");
                CDRList.append("<td align=right>" + hrset.getString(4) + "</td>\n");
            }

            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showUCSReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showUCSHWReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String FHour = request.getParameter("FHour");
        String FMin = request.getParameter("FMin");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String THour = request.getParameter("THour");
        String TMin = request.getParameter("TMin");
        String CallStatus = request.getParameter("CallStatus");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        String AssignChk = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            if(FDay.length() < 2)
                FDay = "0" + FDay;
            if(TDay.length() < 2)
                TDay = "0" + TDay;
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;

            FromDate = FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00";
            ToDate = TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59";
            ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00) to (" + TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59)";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            if(CallStatus.equals("-1"))
                AssignChk = "";
            else
                AssignChk = " and assigned=" + CallStatus;
            int SNo = 0;
            Query = " select  substr(calltime,12,2),count(*), sum(TIMESTAMPDIFF(SECOND,calltime,endtime)/60) duration  "+
                    " from un_attended_calls  where ignoredcall='N' and  calltime between '" + FromDate +
                    "' and '" + ToDate + "' " + AssignChk +
                    " group by substr(calltime,12,2)   order by 1 ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next(); CDRList.append("</tr>\n"))
            {
                if(++SNo % 2 == 0)
                    CDRList.append("<tr class=\"Inner\">\n");
                else
                    CDRList.append("<tr class=\"InnerGrey\">\n");
                CDRList.append("<td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                CDRList.append("<td align=right>" + hrset.getString(3) + "</td>\n");
            }

            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showUCSHWReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showCPS(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer FHour = new StringBuffer();
        StringBuffer FMin = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer THour = new StringBuffer();
        StringBuffer TMin = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Services.GetCalendar(FDay, FMonth, FYear, FHour, FMin);
            Services.GetCalendar(TDay, TMonth, TYear, THour, TMin);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("FHourList", FHour.toString());
            Parser.SetField("FMinList", FMin.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.SetField("THourList", FHour.toString());
            Parser.SetField("TMinList", FMin.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showCPS.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showCPSReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String FHour = request.getParameter("FHour");
        String FMin = request.getParameter("FMin");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String THour = request.getParameter("THour");
        String TMin = request.getParameter("TMin");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        StringBuffer CPSList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            if(FDay.length() < 2)
                FDay = "0" + FDay;
            if(TDay.length() < 2)
                TDay = "0" + TDay;
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;

            FromDate = FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00";
            ToDate = TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59";
            ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00) to (" + TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59)";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

        //    select count(* )from cdr where  abluserid='cc_umair' and substr(starttime,1,10)='2014-06-12';

            int SNo = 0;
            Query = " select upper(agent), sum(cr) calls_recv, round(sum(tt),2) talk_time,  round(sum(acd),2) avg_call_dur, round(sum(lc),2) longest_call,  sum(uc) ua_calls, round((sum(cr)/(sum(cr)+sum(uc)))*100) SL  from (  select a.agentname agent, count(*) cr,  sum((unix_timestamp(endtime)-unix_timestamp(starttime))/60) tt,  sum((unix_timestamp(endtime)-unix_timestamp(starttime))/60)/count(*) acd, max((unix_timestamp(endtime)-unix_timestamp(starttime))/60) lc, 0 uc " +
            		" from agents a,cdr c where a.agentid = c.userid  and c.calltime between '" + FromDate + "' and '" + ToDate + "' " + " group by a.agentname " + " union all " + " select a.agentname agent, 0 cr, 0 tt, 0 acd, 0 lc, count(*) uc " + " from agents a, un_attended_calls c " + " where a.agentid = c.agentid " + " and c.assigned=1 " + " and c.calltime between '" + FromDate + "' and '" + ToDate + "' " + " group by a.agentname ) a " + " group by upper(agent) order by upper(agent) ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next(); CPSList.append("</tr>\n"))
            {
                if(++SNo % 2 == 0)
                    CPSList.append("<tr class=\"Inner\">\n");
                else
                    CPSList.append("<tr class=\"InnerGrey\">\n");
                CPSList.append("<td align=left>" + SNo + "</td>\n");
                CPSList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                CPSList.append("<td align=right>" + hrset.getString(2) + "</td>\n");
                CPSList.append("<td align=right>" + hrset.getString(3) + "</td>\n");
                CPSList.append("<td align=right>" + hrset.getString(8) + "</td>\n");
                CPSList.append("<td align=right>" + hrset.getString(4) + "</td>\n");
                CPSList.append("<td align=right>" + hrset.getString(5) + "</td>\n");
                CPSList.append("<td align=right>" + hrset.getString(6) + "</td>\n");
                CPSList.append("<td align=right>" + hrset.getString(7) + " %</td>\n");
            }

            hrset.close();
            hstmt.close();
            CPSList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("CPSList", CPSList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showCPSReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showCPD(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer FHour = new StringBuffer();
        StringBuffer FMin = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer THour = new StringBuffer();
        StringBuffer TMin = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Services.GetCalendar(FDay, FMonth, FYear, FHour, FMin);
            Services.GetCalendar(TDay, TMonth, TYear, THour, TMin);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("FHourList", FHour.toString());
            Parser.SetField("FMinList", FMin.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.SetField("THourList", FHour.toString());
            Parser.SetField("TMinList", FMin.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showCPD.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showCPDReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String FHour = request.getParameter("FHour");
        String FMin = request.getParameter("FMin");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String THour = request.getParameter("THour");
        String TMin = request.getParameter("TMin");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        StringBuffer CPDList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            if(FDay.length() < 2)
                FDay = "0" + FDay;
            if(TDay.length() < 2)
                TDay = "0" + TDay;
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;

            FromDate = FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00";
            ToDate = TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59";
            ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00) to (" + TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59)";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            int SNo = 0;
            Query = " select upper(a.agentname) agent, count(*) calls_received,  round(sum((unix_timestamp(endtime)-unix_timestamp(starttime))/60),2) talktime,  " +
            		" (min(starttime),'%Y-%m-%d %T') firstcall,  DATE_FORMAT(max(endtime),'%Y-%m-%d %T') lastcall,  " +
            		" round(sum((unix_timestamp(endtime)-unix_timestamp(starttime))/60)/count(*),2) acd, " +
            		" round(max((unix_timestamp(endtime)-unix_timestamp(starttime))/60),2) longestcall, " +
            		" round((unix_timestamp(max(endtime))-unix_timestamp(min(starttime)))/60/60,2) workhours " +
            		" from agents a, cdr c  where a.agentid = c.userid  " +
            		" and c.calltime between '" + FromDate + "' and '" + ToDate + "' " +
            		" group by upper(a.agentname) order by upper(a.agentname) ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next(); CPDList.append("</tr>\n"))
            {
                if(++SNo % 2 == 0)
                    CPDList.append("<tr class=\"Inner\">\n");
                else
                    CPDList.append("<tr class=\"InnerGrey\">\n");
                CPDList.append("<td align=left>" + SNo + "</td>\n");
                CPDList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                CPDList.append("<td align=right>" + hrset.getString(2) + "</td>\n");
                CPDList.append("<td align=right>" + hrset.getString(3) + "</td>\n");
                CPDList.append("<td align=left>" + hrset.getString(4) + "</td>\n");
                CPDList.append("<td align=left>" + hrset.getString(5) + "</td>\n");
                CPDList.append("<td align=right>" + hrset.getString(6) + "</td>\n");
                CPDList.append("<td align=right>" + hrset.getString(7) + "</td>\n");
                CPDList.append("<td align=right>" + hrset.getString(8) + "</td>\n");
            }

            hrset.close();
            hstmt.close();
            CPDList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("CPDList", CPDList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showCPDReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void ViewCDRs(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer SDay = new StringBuffer();
        StringBuffer SMonth = new StringBuffer();
        StringBuffer SYear = new StringBuffer();
        StringBuffer EDay = new StringBuffer();
        StringBuffer EMonth = new StringBuffer();
        StringBuffer EYear = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Services.GetCalendar(SDay, SMonth, SYear);
            Services.GetCalendar(EDay, EMonth, EYear);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("SDayList", SDay.toString());
            Parser.SetField("SMonthList", SMonth.toString());
            Parser.SetField("SYearList", SYear.toString());
            Parser.SetField("EDayList", EDay.toString());
            Parser.SetField("EMonthList", EMonth.toString());
            Parser.SetField("EYearList", EYear.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SearchRecordedMessages.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void ViewRecordedMessages(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String SDay = "";
        String SMonth = "";
        String EDay = "";
        String EMonth = "";
        String FromDate = "";
        String ToDate = "";
        String ReportType = request.getParameter("ddlReportType");
        StringBuffer Response = new StringBuffer();
        String CampaignTitle = "";
        String StartDate = "";
        String EndDate = "";
        String RegionDesc = "";
        String Campaign = "";
        String Region = "";
        String CallStatusChk = "";
        StartDate = FromDate;
        EndDate = ToDate;
        String UserId = Services.GetCookie("UserId", request).trim();
        String UserID = request.getParameter("ddlUsers");
        StringBuffer RptRows = new StringBuffer();
        int chkDate = 0;
        int sno = 0;
        int SNo = 1;
        String f1 = "";

        try
        {
            SDay = request.getParameter("SDay");
            SMonth = request.getParameter("SMonth");
            EDay = request.getParameter("EDay");
            EMonth = request.getParameter("EMonth");

            if(SDay.length() == 1)
                SDay = "0" + SDay;
            if(SMonth.length() == 1)
                SMonth = "0" + SMonth;
            if(EDay.length() == 1)
                EDay = "0" + EDay;
            if(EMonth.length() == 1)
                EMonth = "0" + EMonth;

            FromDate = request.getParameter("SYear") + "-" + SMonth + "-" + SDay;
            FromDate = FromDate + " " + "00:00:00";
            ToDate = request.getParameter("EYear") + "-" + EMonth + "-" + EDay;
            ToDate = ToDate + " " + "23:59:59";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... >From Date ["+FromDate+"] must be lesser than To Date ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            hstmt = conn.createStatement();
            //and src="+UserId+"
            Query = " select calldate,src,userfield,duration from asteriskcdrdb.cdr " +
            		" where disposition='ANSWERED' and dst='"+UserId+"' and calldate between '" + FromDate + "' and '" + ToDate + "' ";
            for(hrset = hstmt.executeQuery(Query); hrset.next(); RptRows.append("</tr>"))
            {
                f1 = URLEncoder.encode(hrset.getString(3));
                RptRows.append("<tr align=\"center\">");
             //   RptRows.append("<td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"><input type=checkbox name=chk" + hrset.getString(3) + " value=" + hrset.getString(3) + "></font></td>");
                RptRows.append("<td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + ++sno + "</font></td>");
                RptRows.append("<td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(1) + "</font></td>");
                RptRows.append("<td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(2) + "</font></td>");
                RptRows.append("<td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(4) + "</font></td>");
                //  RptRows.append("<td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" +UserId + "</font></td>");
            //    RptRows.append("<td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"><a href=/pbx/pbx.Reports?Action=PlayAudioFile&file="+hrset.getString(3).trim()+">Play</a></font></td>");

                RptRows.append("<td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"><a href=/pbx/pbx.Reports?Action=PlayAudioFile&file="+hrset.getString(3).trim()+" target=\"popup\" onclick=\"window.open('/pbx/pbx.Reports?Action=PlayAudioFile&file="+hrset.getString(3).trim()+"\"','name','width=600,height=400')\"> Play</a></font></td>");
                RptRows.append("</tr>\n");
            }

            hrset.close();
            hstmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("RptRows", RptRows.toString());

            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ViewVoiceCDRs.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process request .... " + e.getMessage());
        }
    }

    public void UpdateStats(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String ZIPPath = "/opt/htmls/ccadmin/logs/zipfiles/";
        String FileName = "";
        String RecFile = "";
        String Query = "";
        String RecDirectory = "/var/spool/recordings/Vtrack_inbound/";
        String UserId = Services.GetCookie("UserId", request);
        int i = 0;
        String sourcePath = "";
        String targetPath = "";
        String f1 = "";
        Enumeration en = request.getParameterNames();
        String str = "";
        String chkValue = "";
        int flag = 0;
        String password = "";
        String timestamp = "";
        try
        {
            hstmt = conn.createStatement();
            Query = "select date_format(now(),'%d%m%Y%H%i%s')";
            hrset = hstmt.executeQuery(Query);
            if(hrset.next())
                timestamp = hrset.getString(1);
            hstmt.close();
            HttpSession session = request.getSession(true);
            FileWriter fr = new FileWriter("/opt/htmls/ccadmin/logs/RecFile.log", true);
            fr.write("**************************************************\r\n");
            fr.write("Dated : " + (new Date()).toString() + "\r\n");
            FileName = UserId + "_" + timestamp + ".zip";
           // sourcePath = "/var/spool/recordings/Vtrack_inbound/recorded_messages/";

            sourcePath = this.getServletContext().getInitParameter("inbound_recorded_messages_dir") ;
            targetPath = ZIPPath;
            while(en.hasMoreElements())
            {
                str = (String)en.nextElement();
                if(str.startsWith("chk"))
                {
                    f1 = request.getParameter(str).trim();
                    if(i == 0)
                    {
                        RecFile = "tar -cf  " + targetPath + FileName + " -C " + sourcePath + " " + f1 + ".gsm ";
                        Runtime.getRuntime().exec(RecFile);
                    } else
                    {
                        RecFile = "tar -rf  " + targetPath + FileName + " -C " + sourcePath + " " + f1 + ".gsm ";
                        Runtime.getRuntime().exec(RecFile);
                    }
                    i++;
                    fr.write("RecFile : " + RecFile + "\r\n");
                    Thread.sleep(500L);
                }
            }

            fr.write("**************************************************\r\n");
            fr.flush();
            fr.close();
            Thread.sleep(5500L);
            response.setContentType("application/x-tar");
            response.setHeader("Content-Disposition", "attachment;filename=" + FileName);
            FileInputStream fin = new FileInputStream(ZIPPath + FileName);
            byte content[] = new byte[fin.available()];
            fin.read(content);
            fin.close();
            Thread.sleep(5500L);
            OutputStream os = response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
        }
        catch(Exception e)
        {
            e.printStackTrace(out);
            out.println("Unable to fetch data ... : " + e.getMessage());
        }
    }

    public void PlayAudioFile1(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
    {
        String FileName = request.getParameter("file") + ".wav";

     //   String RecordingPath = this.getServletContext().getInitParameter("inbound_recorded_messages_dir") + FileName;

        String RecordingPath = FileName;
        try
        {
            response.setContentType("audio/x-wav");
            response.setHeader("Content-Disposition", "attachment;filename=" + FileName);

            FileInputStream fin = new FileInputStream(RecordingPath);
            byte content[] = new byte[fin.available()];
            fin.read(content);
            fin.close();
            OutputStream os = response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
        }
        catch(Exception e)
        {
            e.printStackTrace(out);
            out.println("Unable to fetch data ... : " + e.getMessage());
        }
    }
    
    public void PlayAudioFile(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
    {
        String FileName = request.getParameter("file") + ".wav";;
        String RecordingPath 	= "/var/spool/recordings/tesoutbound/"+FileName;

        try
        {
            response.setContentType("audio/x-wav");

            FileInputStream fin = new FileInputStream(FileName);
            byte content[] = new byte[fin.available()];
            fin.read(content);
            fin.close();

            OutputStream os = response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
        }
        catch(Exception e)
        {
        	out.println("Unable to process request ..." + e.getMessage());
        }
    }

    public void showWBH2(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Services.GetCalendar(FDay, FMonth, FYear);
            Services.GetCalendar(TDay, TMonth, TYear);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showWBH2.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showWBHReport2(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String FDay = "01";
        String TDay = "";
        String FTime = "00:00:00";
        String TTime = "23:59:59";
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        String LD = "";
        StringBuffer WBHList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;
            LD = TYear + "-" + TMonth + "-" + FDay;
            Query = " select date_format(LAST_DAY('" + LD + "'),'%d') ";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if(hrset.next())
                TDay = hrset.getString(1);
            hrset.close();
            hstmt.close();
            FromDate = FYear + "-" + FMonth + "-" + FDay + " " + FTime;
            ToDate = TYear + "-" + TMonth + "-" + TDay + " " + TTime;
            ForDate = "(" + FYear + "-" + FMonth + ") to (" + TYear + "-" + TMonth + ")";
            int SNo = 0;
            Query = " select date1, sum(attended), sum(unattended), sum(talktime),  " +
            		" round((sum(attended)/(sum(attended)+sum(unattended)))*100) SL, sum(acd) " +
            		" from (  " +
            		" select DATE_FORMAT(calltime, '%Y-%m') date1, count(*) attended, 0 unattended,  " +
            		" round(sum(unix_timestamp(endtime)-unix_timestamp(starttime))/60) talktime,  " +
            		" round((sum(unix_timestamp(endtime)-unix_timestamp(starttime))/60)/count(*),2) acd  " +
            		" from cdr  where calltime between '" + FromDate + "' " +
            		" and '" + ToDate + "' " + " group by DATE_FORMAT(calltime, '%Y-%m') " +
            		" union all " +
            		" select DATE_FORMAT(calltime, '%Y-%m') date1, 0 attended, count(*) unattended, " +
            		" 0 talktime, 0  acd " + " from un_attended_calls " +
            		" where calltime between '" + FromDate + "' and '" + ToDate + "' " +
            		" group by DATE_FORMAT(calltime, '%Y-%m') " + " ) a group by date1 order by date1 ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next(); WBHList.append("</tr>\n"))
            {
                if(++SNo % 2 == 0)
                    WBHList.append("<tr class=\"Inner\">\n");
                else
                    WBHList.append("<tr class=\"InnerGrey\">\n");
                WBHList.append("<td align=left>" + SNo + "</td>\n");
                WBHList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                WBHList.append("<td align=right>" + hrset.getString(2) + "</td>\n");
                WBHList.append("<td align=right>" + hrset.getString(3) + "</td>\n");
                WBHList.append("<td align=right>" + hrset.getString(4) + "</td>\n");
                WBHList.append("<td align=right>" + hrset.getString(5) + " %</td>\n");
                WBHList.append("<td align=right>" + hrset.getString(6) + "</td>\n");
            }

            hrset.close();
            hstmt.close();
            WBHList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("WBHList", WBHList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showWBHReport2.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void ViewCDRsVoice(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer SDay = new StringBuffer();
        StringBuffer SMonth = new StringBuffer();
        StringBuffer SYear = new StringBuffer();
        StringBuffer EDay = new StringBuffer();
        StringBuffer EMonth = new StringBuffer();
        StringBuffer EYear = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        StringBuffer AgentList = new StringBuffer();
        try
        {
            Services.GetCalendar(SDay, SMonth, SYear);
            Services.GetCalendar(EDay, EMonth, EYear);

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("AgentList", AgentList.toString());
            Parser.SetField("SDayList", SDay.toString());
            Parser.SetField("SMonthList", SMonth.toString());
            Parser.SetField("SYearList", SYear.toString());
            Parser.SetField("EDayList", EDay.toString());
            Parser.SetField("EMonthList", EMonth.toString());
            Parser.SetField("EYearList", EYear.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SearchRecordings.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void ViewRecordings(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String SDay = "";
        String SMonth = "";
        String EDay = "";
        String EMonth = "";
        String FromDate = "";
        String ToDate = "";
        String AgentID = request.getParameter("AgentId");
        String ReportType = request.getParameter("ddlReportType");
        StringBuffer Response = new StringBuffer();
        String CampaignTitle = "";
        String StartDate = "";
        String EndDate = "";
        String RegionDesc = "";
        String Campaign = "";
        String Region = "";
        String CallStatusChk = "";
        StartDate = FromDate;
        EndDate = ToDate;
        String UserID = request.getParameter("ddlUsers");
        StringBuffer RptRows = new StringBuffer();
        int chkDate = 0;
        int sno = 0;
        int SNo = 1;
        String f1 = "";
        try
        {
            SDay = request.getParameter("SDay");
            SMonth = request.getParameter("SMonth");
            EDay = request.getParameter("EDay");
            EMonth = request.getParameter("EMonth");

            if(SDay.length() == 1)
                SDay = "0" + SDay;
            if(SMonth.length() == 1)
                SMonth = "0" + SMonth;
            if(EDay.length() == 1)
                EDay = "0" + EDay;
            if(EMonth.length() == 1)
                EMonth = "0" + EMonth;

            FromDate = request.getParameter("SYear") + "-" + SMonth + "-" + SDay;
            FromDate = FromDate + " " + "00:00:00";
            ToDate = request.getParameter("EYear") + "-" + EMonth + "-" + EDay;
            ToDate = ToDate + " " + "23:59:59";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... >From Date ["+FromDate+"] must be lesser than To Date ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }
            int duration=0, min=0, sec=0;
            hstmt = conn.createStatement();
            if(AgentID.compareTo("ALL") == 0)
                Query = " select \tb.AgentName, a.callingnumber, c.description, " +
                		" Date_Format(a.starttime,'%Y-%m-%d %H:%i:%s'), " +
                		" Date_Format(a.endtime,'%Y-%m-%d %H:%i:%s'), " +
                		" TIMESTAMPDIFF(second,starttime,endtime), a.filename " +
                		" from \t\t"+inbound+".cdr a, "+inbound+".agents b, "+inbound+".calltype c  " +
                		" where \tb.agentid = a.userid  and \t\ta.menuid = c.calltype  " +
                		" and \t\tstarttime between '" + FromDate + "' and '" + ToDate + "' " +
                		" order by \tstarttime ";
            else
                Query = " select \tb.AgentName, a.callingnumber, c.description, " +
                		" Date_Format(a.starttime,'%Y-%m-%d %H:%i:%s'), " +
                		" Date_Format(a.endtime,'%Y-%m-%d %H:%i:%s'), " +
                		" TIMESTAMPDIFF(second,starttime,endtime), a.filename  " +
                		" from \t\t"+inbound+".cdr a, "+inbound+".agents b, "+inbound+".calltype c " +
                		" where \tb.agentid = a.userid  and \t\ta.menuid = c.calltype " +
                		" and \t\tstarttime between '" + FromDate + "' and '" + ToDate + "'" +
                		 " and a.userid='" + AgentID.trim() + "' " + " order by \tstarttime ";
            for(hrset = hstmt.executeQuery(Query); hrset.next(); RptRows.append("</tr>"))
            {
                f1 = URLEncoder.encode(hrset.getString(7));
                duration = hrset.getInt(6);
				min=duration/60;
				sec=duration%60;

                RptRows.append("<tr align=\"center\">");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"><input type=checkbox name=chk" + hrset.getString(7) + " value=" + hrset.getString(7) + "></font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + ++sno + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(1) + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(2) + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(3) + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(4) + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(5) + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">"+min+":"+sec+"</font></td>");
                RptRows.append(" \t<td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"><a href=/ccadmin/ccadmin.Reports?Action=PlayAudioFileVoice&file=" + hrset.getString(7) + ">" + hrset.getString(7) + "</a></font></td>");
            }

            hrset.close();
            hstmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("RptRows", RptRows.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ViewRecordings.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process request .... " + e.getMessage());
        }
    }

    public void UpdateStatsVoiceRecordings(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String ZIPPath = "/opt/htmls/ccadmin/logs/zipfiles/";
        String FileName = "";
        String RecFile = "";
        String Query = "";
        String RecDirectory =   this.getServletContext().getInitParameter("inbound_rec_dir");
        String UserId = Services.GetCookie("UserId", request);
        int i = 0;
        String sourcePath = "";
        String targetPath = "";
        String f1 = "";
        Enumeration en = request.getParameterNames();
        String str = "";
        String chkValue = "";
        int flag = 0;
        String password = "";
        String timestamp = "";
        try
        {
            hstmt = conn.createStatement();
            Query = "select date_format(now(),'%d%m%Y%H%i%s')";
            hrset = hstmt.executeQuery(Query);
            if(hrset.next())
                timestamp = hrset.getString(1);
            hstmt.close();
            HttpSession session = request.getSession(true);
            FileWriter fr = new FileWriter("/opt/htmls/ccadmin/logs/RecFile.log", true);
            fr.write("**************************************************\r\n");
            fr.write("Dated : " + (new Date()).toString() + "\r\n");
            FileName = UserId + "_" + timestamp + ".zip";
           // sourcePath = "/var/spool/recordings/Vtrack_inbound/";
            sourcePath = RecDirectory;
            targetPath = ZIPPath;
            while(en.hasMoreElements())
            {
                str = (String)en.nextElement();
                if(str.startsWith("chk"))
                {
                    f1 = request.getParameter(str).trim();
                    if(i == 0)
                    {
                        RecFile = "tar -cf  " + targetPath + FileName + " -C " + sourcePath + " " + f1;
                        Runtime.getRuntime().exec(RecFile);
                    } else
                    {
                        RecFile = "tar -rf  " + targetPath + FileName + " -C " + sourcePath + " " + f1;
                        Runtime.getRuntime().exec(RecFile);
                    }
                    i++;
                    fr.write("RecFile : " + RecFile + "\r\n");
                    Thread.sleep(500L);

                    if(i > 10)
                        throw new Exception(" Maximum of 10 recrodings can be downloaded at a time .... ");
                }
            }
            fr.write("**************************************************\r\n");
            fr.flush();
            fr.close();
            Thread.sleep(5500L);

            response.setContentType("application/x-tar");
            response.setHeader("Content-Disposition", "attachment;filename=" + FileName);
            FileInputStream fin = new FileInputStream(ZIPPath + FileName);
            byte content[] = new byte[fin.available()];
            fin.read(content);
            fin.close();
            Thread.sleep(5500L);
            OutputStream os = response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
        }
        catch(Exception e)
        {
        	out.println("Unable to process request ... : " + e.getMessage());
        }
    }

    public void PlayAudioFileVoice(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
    {
        String FileName = request.getParameter("file");
        String RecordingPath = this.getServletContext().getInitParameter("inbound_rec_dir") + FileName;
        try
        {
            response.setContentType("audio/x-gsm");
            response.setHeader("Content-Disposition", "attachment;filename=" + FileName);

            FileInputStream fin = new FileInputStream(RecordingPath);
            byte content[] = new byte[fin.available()];
            fin.read(content);
            fin.close();
            OutputStream os = response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
        }
        catch(Exception e)
        {
            e.printStackTrace(out);
            out.println("Unable to fetch data ... : " + e.getMessage());
        }
    }

    public void showHourWiseQueue(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer SDay = new StringBuffer();
        StringBuffer SMonth = new StringBuffer();
        StringBuffer SYear = new StringBuffer();
        StringBuffer EDay = new StringBuffer();
        StringBuffer EMonth = new StringBuffer();
        StringBuffer EYear = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        StringBuffer AgentList = new StringBuffer();
        try
        {
            Services.GetCalendar(SDay, SMonth, SYear);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("SDayList", SDay.toString());
            Parser.SetField("SMonthList", SMonth.toString());
            Parser.SetField("SYearList", SYear.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "showAvgQueueTime.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showAvgQueueTimeRpt(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String strSQL = "";
        String tableRows = "";
        String v_frommonth = "";
        String v_fromyear = "";
        String v_fromdate = "";
        String v_FromCompleteDate = "";
        String v_tomonth = "";
        String v_toyear = "";
        String v_todate = "";
        String v_toCompleteDate = "";
        String v_CompanyID = "";
        String SDay = "";
        String SMonth = "";
        String EDay = "";
        String EMonth = "";
        String FromDate = "";
        String ToDate = "";
        StringBuffer RptRows = new StringBuffer();
        int sno = 1;
        try
        {
            SDay = request.getParameter("SDay");
            SMonth = request.getParameter("SMonth");
            if(SDay.length() == 1)
                SDay = "0" + SDay;
            if(SMonth.length() == 1)
                SMonth = "0" + SMonth;
            FromDate = request.getParameter("SYear") + "-" + SMonth + "-" + SDay;
            hstmt = conn.createStatement();
            strSQL = " SELECT c.callday dt1, Date_Format(c.starttime,'%H') t1, " +
            		" IFNULL(sum(unix_timestamp(starttime) - unix_timestamp(customerintime)),0)/count(*) " +
            		" FROM "+inbound+".cdr c " +
            		" WHERE callday = '" + FromDate + "' " +
            		" GROUP BY c.callday, Date_Format(c.starttime,'%H') ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(strSQL);
            		hrset.next();
            		RptRows.append("<tr><td align=center><strong><font size=2 face=Calibri>" + sno++ +
            				"</font></strong></td>" + "<td align=center><strong><font size=2 face=Calibri>"
            				+ hrset.getString(1) + "</font></strong></td>" + "<td align=center><strong><font size=2 face=Calibri>" + nf1.format(hrset.getLong(2)) + "</font></strong></td>" + "<td align=right><strong><font size=2 face=Calibri>" + nf1.format(hrset.getDouble(3)) + "</font></strong></td>" + "</tr>"));
            hrset.close();
            hstmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("RptRows", RptRows.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "showAvgQueueTimeRpt.html");
        }
        catch(Exception e)
        {
            out.println(" Unable to process request ... " + e.getMessage());
        }
    }

    public void showCustomerProfile(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer SDay = new StringBuffer();
        StringBuffer SMonth = new StringBuffer();
        StringBuffer SYear = new StringBuffer();
        StringBuffer EDay = new StringBuffer();
        StringBuffer EMonth = new StringBuffer();
        StringBuffer EYear = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        StringBuffer AgentList = new StringBuffer();
        try
        {
            Services.GetCalendar(SDay, SMonth, SYear);
            Services.GetCalendar(EDay, EMonth, EYear);
            hstmt = conn.createStatement();
            Query = " select agentid from agents where agenttype=1 order by agentid ";
            hrset = hstmt.executeQuery(Query);
            AgentList.append("<option class=Inner value=\"ALL\"> All </option>");
            for(; hrset.next(); AgentList.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(1) + "</option>"));
            hrset.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("AgentList", AgentList.toString());
            Parser.SetField("SDayList", SDay.toString());
            Parser.SetField("SMonthList", SMonth.toString());
            Parser.SetField("SYearList", SYear.toString());
            Parser.SetField("EDayList", EDay.toString());
            Parser.SetField("EMonthList", EMonth.toString());
            Parser.SetField("EYearList", EYear.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "showCustomerProfile.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showCustomerProfileRpt(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String SDay = "";
        String SMonth = "";
        String EDay = "";
        String EMonth = "";
        String FromDate = "";
        String ToDate = "";
        String ReportType = request.getParameter("ddlReportType");
        String CallingNumber = request.getParameter("txtCallingNumber");
        StringBuffer Response = new StringBuffer();
        String CampaignTitle = "";
        String StartDate = "";
        String EndDate = "";
        String RegionDesc = "";
        String Campaign = "";
        String Region = "";
        String CallStatusChk = "";
        StartDate = FromDate;
        EndDate = ToDate;
        String UserID = request.getParameter("ddlUsers");
        StringBuffer RptRows = new StringBuffer();
        int chkDate = 0;
        int sno = 0;
        int SNo = 1;
        String f1 = "";
        try
        {
            SDay = request.getParameter("SDay");
            SMonth = request.getParameter("SMonth");
            EDay = request.getParameter("EDay");
            EMonth = request.getParameter("EMonth");

            if(SDay.length() == 1)
                SDay = "0" + SDay;
            if(SMonth.length() == 1)
                SMonth = "0" + SMonth;
            if(EDay.length() == 1)
                EDay = "0" + EDay;
            if(EMonth.length() == 1)
                EMonth = "0" + EMonth;

            FromDate = request.getParameter("SYear") + "-" + SMonth + "-" + SDay;
            FromDate = FromDate + " " + "00:00:00";
            ToDate = request.getParameter("EYear") + "-" + EMonth + "-" + EDay;
            ToDate = ToDate + " " + "23:59:59";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... >From Date ["+FromDate+"] must be lesser than To Date ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            hstmt = conn.createStatement();
            if(CallingNumber.length() == 0)
                Query = " select \tcallername, callingnumber, case when address='' then 'n/a' when address=' ' then 'n/a' else address end, " +
                		" receivedby, Date_Format(entrydate,'%Y-%m-%d %H:%i:%s') " +
                		" from "+inbound+".caller_details  where \tentrydate between '" + FromDate + "' and '" + ToDate + "' " +
                		" order by \tcallername ";
            else
                Query = " select \tcallername, callingnumber, case when address='' then 'n/a' when address=' ' then 'n/a' else address end, " +
                		" receivedby, Date_Format(entrydate,'%Y-%m-%d %H:%i:%s') " +
                		" from "+inbound+".caller_details  where \tentrydate between '" + FromDate + "' and '" + ToDate + "' and callingnumber like '%" + CallingNumber + "%' " +
                		" order by \tentrydate ";

            for(hrset = hstmt.executeQuery(Query); hrset.next(); RptRows.append("</tr>"))
            {
                RptRows.append("<tr align=\"center\">");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + ++sno + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(1) + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(2) + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(3) + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(4) + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(5) + "</font></td>");
            }

            hrset.close();
            hstmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("RptRows", RptRows.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ViewCustomerProfile.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process request .... " + e.getMessage());
        }
    }

    public void showCityCDR(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer FDay = new StringBuffer();
        StringBuffer FMonth = new StringBuffer();
        StringBuffer FYear = new StringBuffer();
        StringBuffer FHour = new StringBuffer();
        StringBuffer FMin = new StringBuffer();
        StringBuffer TDay = new StringBuffer();
        StringBuffer TMonth = new StringBuffer();
        StringBuffer TYear = new StringBuffer();
        StringBuffer THour = new StringBuffer();
        StringBuffer TMin = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Services.GetCalendar(FDay, FMonth, FYear, FHour, FMin);
            Services.GetCalendar(TDay, TMonth, TYear, THour, TMin);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FDayList", FDay.toString());
            Parser.SetField("FMonthList", FMonth.toString());
            Parser.SetField("FYearList", FYear.toString());
            Parser.SetField("FHourList", FHour.toString());
            Parser.SetField("FMinList", FMin.toString());
            Parser.SetField("TDayList", TDay.toString());
            Parser.SetField("TMonthList", TMonth.toString());
            Parser.SetField("TYearList", TYear.toString());
            Parser.SetField("THourList", FHour.toString());
            Parser.SetField("TMinList", FMin.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showCityCDR.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showCityCDRReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String FDay = request.getParameter("FDay");
        String FMonth = request.getParameter("FMonth");
        String FYear = request.getParameter("FYear");
        String FHour = request.getParameter("FHour");
        String FMin = request.getParameter("FMin");
        String TDay = request.getParameter("TDay");
        String TMonth = request.getParameter("TMonth");
        String TYear = request.getParameter("TYear");
        String THour = request.getParameter("THour");
        String TMin = request.getParameter("TMin");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            if(FDay.length() < 2)
                FDay = "0" + FDay;
            if(TDay.length() < 2)
                TDay = "0" + TDay;
            if(FMonth.length() < 2)
                FMonth = "0" + FMonth;
            if(TMonth.length() < 2)
                TMonth = "0" + TMonth;

            FromDate = FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00";
            ToDate = TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59";
            ForDate = "(" + FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00) to (" + TYear + "-" + TMonth + "-" + TDay + " " + THour + ":" + TMin + ":59)";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            int SNo = 0;
            Query = " select b.cityname, count(*) from cdrdetail a, city b  where a.citycode=b.citycode  and a.starttime between '" + FromDate + "' and '" + ToDate + "' " + " and a.dialstatus='ANSWER' " + " group by b.cityname order by b.cityname ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next();)
                if(++SNo % 2 == 0)
                {
                    CDRList.append("<tr class=\"Inner\">\n");
                    CDRList.append("<td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                    CDRList.append("<td align=right>" + hrset.getString(2) + "</td>\n");
                    CDRList.append("</tr>\n");
                } else
                {
                    CDRList.append("<tr class=\"InnerGrey\">\n");
                    CDRList.append("<td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                    CDRList.append("<td align=right>" + hrset.getString(2) + "</td>\n");
                    CDRList.append("</tr>\n");
                }

            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showCityCDRReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showAgentSummary(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer Day = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Year = new StringBuffer();
        StringBuffer AgentList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Query = " select agentid from agents where agenttype=1 order by agentid ";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            AgentList.append("<option class=Inner value=\"-1\"> All </option>");
            for(; hrset.next(); AgentList.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(1) + "</option>"));
            hrset.close();
            hstmt.close();
            Services.GetCalendar(Day, Month, Year);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("AgentList", AgentList.toString());
            Parser.SetField("DayList", Day.toString());
            Parser.SetField("MonthList", Month.toString());
            Parser.SetField("YearList", Year.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showAgentSummary.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showAgentSummaryReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        Statement hstmt2 = null;
        ResultSet hrset2 = null;
        String Query2 = "";
        String Day = request.getParameter("Day");
        String Month = request.getParameter("Month");
        String Year = request.getParameter("Year");
        String AgentId = request.getParameter("AgentId");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        String AgentIdChk = "";
        String ReportFor = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            if(Day.length() < 2)
                Day = "0" + Day;
            if(Month.length() < 2)
                Month = "0" + Month;
            FromDate = Year + "-" + Month + "-" + Day + " 00:00:00";
            ToDate = Year + "-" + Month + "-" + Day + " 23:59:59";
            ForDate = "(" + Year + "-" + Month + "-" + Day + ")";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            if(!AgentId.equals("-1"))
            {
                AgentIdChk = " and b.agentid='" + AgentId + "'";
                ReportFor = AgentId;
            } else
            {
                AgentIdChk = "";
                ReportFor = "All Agents";
            }
            CDRList.append("<tr align=center class=\"tableSubHeader\">");
            CDRList.append("<td><b>S.No.</b></td>");
            CDRList.append("<td><b>Agent Name</b></td>");
            CDRList.append("<td><b>Login_old Time</b></td>");
            CDRList.append("<td><b>Logout Time</b></td>");
            CDRList.append("<td><b>Agent Login_old IP</b></td>");
            CDRList.append("<td><b>Away Time</b></td>");
            CDRList.append("<td><b>Received Calls</b></td>");
            CDRList.append("<td><b>Talk Time</b></td>");
            CDRList.append("<td><b>ACD<br>(Minutes)</b></td>");
            CDRList.append("<td><b>Longest Call</b></td>");
            CDRList.append("<td><b>Unattended Calls</b></td>");
            CDRList.append("<td><b>Work Hours</b></td>");
            CDRList.append("<td><b>Service Level</b></td>");
            CDRList.append("</tr>");
            int SNo = 0;
            int i = 0;
            String AgentCode = "";
            String AgentName = "";
            String LoginTime = "-";
            String LogoutTime = "-";
            String AgentIP = "-";
            double AwayTime = 0.0D;
            double recvcall = 0.0D;
            double talktime = 0.0D;
            double acd = 0.0D;
            double longcall = 0.0D;
            double workhrs = 0.0D;
            double unattend = 0.0D;
            double SL = 0.0D;
            Query = " select agentid, agentname, sum(attended) recvcall, sum(talktime) talktime, sum(acd) acd,  sum(longestcall) longcall, sum(workhours) workhrs, sum(unattended) unattend,  round((sum(attended)/(sum(attended)+sum(unattended)))*100) SL  from (  select a.userid agentid, b.agentname,  count(*) attended,  round(sum(TIMESTAMPDIFF(SECOND,starttime,endtime))/60,2) talktime,  round((sum(TIMESTAMPDIFF(SECOND,starttime,endtime))/60)/count(*),2) acd,  round(max(TIMESTAMPDIFF(SECOND,starttime,endtime)/60),2) longestcall,  round((TIMESTAMPDIFF(SECOND,min(starttime),max(endtime))/60)/60,2) workhours,  0 unattended  from cdr a, agents b  where a.userid=b.agentid and b.agenttype=1  and a.calltime between '" + FromDate + "' and '" + ToDate + "' " + AgentIdChk + " group by a.userid, b.agentname " + " union all " + " select a.agentid, b.agentname, 0 attended, 0 talktime, 0 acd, " + " 0 longestcall, 0 workhours, count(*) unattended " + " from un_attended_calls a, agents b " + " where a.agentid=b.agentid and b.agenttype=1 " + " and a.calltime between '" + FromDate + "' and '" + ToDate + "' " + AgentIdChk + " group by a.agentid, b.agentname " + " ) tmp " + " group by agentid, agentname " + " order by agentname ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next(); CDRList.append("</tr>\n"))
            {
                AgentCode = AgentName = "";
                LoginTime = LogoutTime = AgentIP = "-";
                recvcall = talktime = acd = longcall = workhrs = unattend = SL = 0.0D;
                AwayTime = 0.0D;
                AgentCode = hrset.getString(1);
                AgentName = hrset.getString(2);
                recvcall = hrset.getDouble(3);
                talktime = hrset.getDouble(4);
                acd = hrset.getDouble(5);
                longcall = hrset.getDouble(6);
                workhrs = hrset.getDouble(7);
                unattend = hrset.getDouble(8);
                SL = hrset.getDouble(9);
                hstmt2 = conn.createStatement();
                i = 0;
                Query2 = " select date_format(min(dated),'%Y-%m-%d %T'),ip from logindetails " +
                		" where status=0  and dated between '" + FromDate + "' and '" + ToDate + "' " +
                		" and userid='" + AgentCode + "' group by ip order by ip ";
                for(hrset2 = hstmt2.executeQuery(Query2); hrset2.next();)
                {
                    if(i == 0)
                        LoginTime = hrset2.getString(1);
                    else
                        LoginTime = LoginTime + ", " + hrset2.getString(1);
                    i++;
                }

                hrset2.close();
                if(LoginTime == null)
                    LoginTime = "-";
                i = 0;
                Query2 = " select date_format(max(dated),'%Y-%m-%d %T'),ip from logindetails" +
                		" where status=-1  and dated between '" + FromDate + "' and '" + ToDate + "' " +
                		" and userid='" + AgentCode + "' group by ip order by ip ";
                for(hrset2 = hstmt2.executeQuery(Query2); hrset2.next();)
                {
                    if(i == 0)
                        LogoutTime = hrset2.getString(1);
                    else
                        LogoutTime = LogoutTime + ", " + hrset2.getString(1);
                    i++;
                }

                hrset2.close();
                if(LogoutTime == null)
                    LogoutTime = "-";
                i = 0;
                Query2 = " select distinct ip from logindetails  where dated between '" + FromDate + "' and '" + ToDate + "' " + " and userid='" + AgentCode + "' order by ip ";
                for(hrset2 = hstmt2.executeQuery(Query2); hrset2.next();)
                {
                    if(i == 0)
                        AgentIP = hrset2.getString(1);
                    else
                        AgentIP = AgentIP + ", " + hrset2.getString(1);
                    i++;
                }

                hrset2.close();
                hstmt2.close();
                if(AgentIP == null)
                    AgentIP = "-";
                if(++SNo % 2 == 0)
                    CDRList.append("<tr class=\"Inner\">\n");
                else
                    CDRList.append("<tr class=\"InnerGrey\">\n");
                CDRList.append("<td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + AgentName + "</td>\n");
                CDRList.append("<td align=left>" + LoginTime + "</td>\n");
                CDRList.append("<td align=left>" + LogoutTime + "</td>\n");
                CDRList.append("<td align=left>" + AgentIP + "</td>\n");
                CDRList.append("<td align=right>" + nf1.format(AwayTime) + "</td>\n");
                CDRList.append("<td align=right>" + nf1.format(recvcall) + "</td>\n");
                CDRList.append("<td align=right>" + nf1.format(talktime) + "</td>\n");
                CDRList.append("<td align=right>" + nf1.format(acd) + "</td>\n");
                CDRList.append("<td align=right>" + nf1.format(longcall) + "</td>\n");
                CDRList.append("<td align=right>" + nf1.format(unattend) + "</td>\n");
                CDRList.append("<td align=right>" + nf1.format(workhrs) + "</td>\n");
                CDRList.append("<td align=right>" + nf1.format(SL) + "</td>\n");
            }

            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("ReportFor", ReportFor);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showAgentSummaryReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showAgentAway(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer Day = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Year = new StringBuffer();
        StringBuffer AgentList = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        try
        {
            Query = " select agentid from agents where agenttype=1 order by agentid ";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            AgentList.append("<option class=Inner value=\"-1\"> All </option>");
            for(; hrset.next(); AgentList.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(1) + "</option>"));
            hrset.close();
            hstmt.close();
            Services.GetCalendar(Day, Month, Year);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("AgentList", AgentList.toString());
            Parser.SetField("DayList", Day.toString());
            Parser.SetField("MonthList", Month.toString());
            Parser.SetField("YearList", Year.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showAgentAway.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void showAgentAwayReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String Day = request.getParameter("Day");
        String Month = request.getParameter("Month");
        String Year = request.getParameter("Year");
        String AgentId = request.getParameter("AgentId");
        String FromDate = "";
        String ToDate = "";
        String ForDate = "";
        String AgentIdChk = "";
        String ReportFor = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuffer Response = new StringBuffer();


        long TimeDiff=0;
        String Min="";
        String Sec="";
        try
        {
            if(Day.length() < 2)
                Day = "0" + Day;
            if(Month.length() < 2)
                Month = "0" + Month;

            FromDate = Year + "-" + Month + "-" + Day + " 00:00:00";
            ToDate = Year + "-" + Month + "-" + Day + " 23:59:59";
            ForDate = "(" + Year + "-" + Month + "-" + Day + ")";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... ["+FromDate+"] must be lesser than ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            if(!AgentId.equals("-1"))
            {
                AgentIdChk = " and a.agentid='" + AgentId + "'";
                ReportFor = AgentId;
            } else
            {
                AgentIdChk = "";
                ReportFor = "All Agents";
            }
            CDRList.append("<tr align=center class=\"tableSubHeader\">");
            CDRList.append("<td><b>S.No.</b></td>");
            CDRList.append("<td><b>Agent Name</b></td>");
            CDRList.append("<td><b>Agent ID</b></td>");
            CDRList.append("<td><b>Start Time</b></td>");
            CDRList.append("<td><b>End Time</b></td>");
            CDRList.append("<td><b>Duration <br> (Min.)</b></td>");
            CDRList.append("<td><b>Reason</b></td>");
            CDRList.append("</tr>");
            int SNo = 0;
            Query = " select a.agentname, b.userid, date_format(b.starttime,'%Y-%m-%d %T'),  " +
            		" date_format(b.endtime,'%Y-%m-%d %T'), c.description,  " +
            		" round(TIMESTAMPDIFF(SECOND,starttime,endtime)/60,2)  " +
            		" from agents a, agent_away b, logindetails_status c  " +
            		" where a.agentid=b.userid and b.reasonid=c.status  " +
            		" and b.starttime between '" + FromDate + "' and '" + ToDate + "' " + AgentIdChk +
            		" order by a.agentname, date_format(b.starttime,'%Y-%m-%d %T') ";
            hstmt = conn.createStatement();
            for(hrset = hstmt.executeQuery(Query); hrset.next(); CDRList.append("</tr>\n"))
            {
            	TimeDiff = hrset.getLong(6);

            	Min  = String.valueOf(TimeDiff/60);
            	Sec  = String.valueOf(TimeDiff  - ((TimeDiff/60)*60) );

            	if (Min.trim().length()==1)
            	      Min ="0"+Min;

            	if (Sec.trim().length()==1)
            		Sec ="0"+Sec;

                if(++SNo % 2 == 0)
                    CDRList.append("<tr class=\"Inner\">\n");
                else
                    CDRList.append("<tr class=\"InnerGrey\">\n");
                CDRList.append("<td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(4) + "</td>\n");
                CDRList.append("<td align=right>"+ Min +":"+ Sec +"</td>\n");
                CDRList.append("<td align=left>" + hrset.getString(5) + "</td>\n");
            }

            hrset.close();
            hstmt.close();
            CDRList.append("</table>\n");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ForDate", ForDate);
            Parser.SetField("ReportFor", ReportFor);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/showAgentAwayReport.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }
    
    public void ViewCDRsVoice2(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        
        StringBuffer SDay = new StringBuffer();
        StringBuffer SMonth = new StringBuffer();
        StringBuffer SYear = new StringBuffer();
        StringBuffer EDay = new StringBuffer();
        StringBuffer EMonth = new StringBuffer();
        StringBuffer EYear = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        StringBuffer AgentList = new StringBuffer();
        
        try
        {
            Services.GetCalendar(SDay, SMonth, SYear);
            Services.GetCalendar(EDay, EMonth, EYear);
            
            hstmt = conn.createStatement();
            Query = " select userid, username from users order by userid ";
            hrset = hstmt.executeQuery(Query);
            AgentList.append("<option class=Inner value=\"ALL\"> All </option>");
            for(; hrset.next(); AgentList.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(1) + "</option>"));
            hrset.close();
            hstmt.close();
            
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("AgentList", AgentList.toString());
            Parser.SetField("SDayList", SDay.toString());
            Parser.SetField("SMonthList", SMonth.toString());
            Parser.SetField("SYearList", SYear.toString());
            Parser.SetField("EDayList", EDay.toString());
            Parser.SetField("EMonthList", EMonth.toString());
            Parser.SetField("EYearList", EYear.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "SearchRecordings2.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process the request...");
        }
    }

    public void ViewRecordings2(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, Connection conn1)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        
        String SDay = "";
        String SMonth = "";
        String EDay = "";
        String EMonth = "";
        String FromDate = "";
        String ToDate = "";
        
        String AgentID = request.getParameter("AgentId");
        String ReportType = request.getParameter("ddlReportType");
        String UserID = request.getParameter("ddlUsers");
        StringBuffer RptRows = new StringBuffer();
        StringBuffer Response = new StringBuffer();
        
        String CampaignTitle = "";
        String StartDate = "";
        String EndDate = "";
        String RegionDesc = "";
        String Campaign = "";
        String Region = "";
        String CallStatusChk = "";
        StartDate = FromDate;
        EndDate = ToDate;
        int chkDate = 0;
        int sno = 0;
        int SNo = 1;
        String f1 = "";
        String FileName="";
        
        try
        {
            SDay = request.getParameter("SDay");
            SMonth = request.getParameter("SMonth");
            EDay = request.getParameter("EDay");
            EMonth = request.getParameter("EMonth");

            if(SDay.length() == 1)
                SDay = "0" + SDay;
            if(SMonth.length() == 1)
                SMonth = "0" + SMonth;
            if(EDay.length() == 1)
                EDay = "0" + EDay;
            if(EMonth.length() == 1)
                EMonth = "0" + EMonth;

            FromDate = request.getParameter("SYear") + "-" + SMonth + "-" + SDay;
            FromDate = FromDate + " " + "00:00:00";
            ToDate = request.getParameter("EYear") + "-" + EMonth + "-" + EDay;
            ToDate = ToDate + " " + "23:59:59";

            boolean flag=CheckDates(FromDate, ToDate, conn);
            if(!flag)
            {
            	out.println("<font color=red>Invalid Dates selection ... >From Date ["+FromDate+"] must be lesser than To Date ["+ToDate+"]</font>");
            	out.flush();
            	return;
            }

            hstmt = conn1.createStatement();
            int duration=0, min=0, sec=0;
            if(AgentID.compareTo("ALL") == 0)
                Query = " select abluserid, callednumber, " +
                		" date_format(starttime,'%Y-%m-%d %H:%i:%s'), " +
                		" date_format(endtime,'%Y-%m-%d %H:%i:%s'), " +
                		" TIMESTAMPDIFF(second,starttime,endtime), filename " +
                		" from cdr " +
                		" WHERE starttime >= '" + FromDate + "' AND starttime <= '" + ToDate + "' " +
            			" AND DIALSTATUS='ANSWER' " +
            			" order by starttime ";
            else
            	Query = " select abluserid, callednumber, " +
		        		" date_format(starttime,'%Y-%m-%d %H:%i:%s'), " +
		        		" date_format(endtime,'%Y-%m-%d %H:%i:%s'), " +
		        		" TIMESTAMPDIFF(second,starttime,endtime), filename " +
		        		" from cdr " +
		        		" WHERE starttime >= '" + FromDate + "' AND starttime <= '" + ToDate + "' " +
		    			" AND DIALSTATUS='ANSWER' " +
		    			" AND abluserid='" + AgentID + "' " +
		    			" order by starttime ";

            for(hrset = hstmt.executeQuery(Query); hrset.next(); RptRows.append("</tr>"))
            {
            	FileName=hrset.getString(6).trim()+".gsm";
            	
                f1 = URLEncoder.encode(FileName);
                duration = hrset.getInt(5);
				min=duration/60;
				sec=duration%60;
 
            
                RptRows.append("<tr align=\"center\">");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"><input type=checkbox name=chk" + FileName + " value=" + FileName + "></font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + ++sno + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(1) + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(2) + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(3) + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">" + hrset.getString(4) + "</font></td>");
                RptRows.append("    <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"> "+min+":"+sec+"</font></td>");
                RptRows.append(" \t<td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"><a href=/ccadmin/ccadmin.Reports?Action=PlayAudioFileVoice2&file=" + FileName + ">" + FileName + "</a></font></td>");
            }
            hrset.close();
            hstmt.close();
            
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("RptRows", RptRows.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ViewRecordings2.html");
        }
        catch(Exception e)
        {
            out.println("Unable to process request .... " + e.getMessage());
        }
    }

    public void UpdateStatsVoiceRecordings2(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn)
    {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        
        String ZIPPath = "/opt/htmls/ccadmin/logs/zipfiles/";
        String FileName = "";
        String RecFile = "";
        
        String RecDirectory = this.getServletContext().getInitParameter("outbound_rec_dir");
        
        String UserId = Services.GetCookie("UserId", request);
        
        int i = 0;
        String sourcePath = "";
        String targetPath = "";
        String f1 = "";
        Enumeration en = request.getParameterNames();
        String str = "";
        String chkValue = "";
        int flag = 0;
        String password = "";
        String timestamp = "";
        try
        {
            hstmt = conn.createStatement();
            Query = "select date_format(now(),'%d%m%Y%H%i%s')";
            hrset = hstmt.executeQuery(Query);
            if(hrset.next())
                timestamp = hrset.getString(1);
            hstmt.close();
            HttpSession session = request.getSession(true);
            FileWriter fr = new FileWriter("/opt/htmls/ccadmin/logs/RecFile.log", true);
            fr.write("**************************************************\r\n");
            fr.write("Dated : " + (new Date()).toString() + "\r\n");
            FileName = UserId + "_" + timestamp + ".zip";
           // sourcePath = "/var/spool/recordings/Vtrack_inbound/";
            sourcePath = RecDirectory;
            targetPath = ZIPPath;
            while(en.hasMoreElements())
            {
                str = (String)en.nextElement();
                if(str.startsWith("chk"))
                {
                    f1 = request.getParameter(str).trim();
                    if(i == 0)
                    {
                        RecFile = "tar -cf  " + targetPath + FileName + " -C " + sourcePath + " " + f1;
                        Runtime.getRuntime().exec(RecFile);
                    } else
                    {
                        RecFile = "tar -rf  " + targetPath + FileName + " -C " + sourcePath + " " + f1;
                        Runtime.getRuntime().exec(RecFile);
                    }
                    i++;
                    fr.write("RecFile : " + RecFile + "\r\n");
                    Thread.sleep(500L);

                    if(i > 10)
                        throw new Exception(" Maximum of 10 recordings can be downloaded at a time .... ");
                }
            }
            fr.write("**************************************************\r\n");
            fr.flush();
            fr.close();
            Thread.sleep(5500L);

            response.setContentType("application/x-tar");
            response.setHeader("Content-Disposition", "attachment;filename=" + FileName);
            FileInputStream fin = new FileInputStream(ZIPPath + FileName);
            byte content[] = new byte[fin.available()];
            fin.read(content);
            fin.close();
            
            Thread.sleep(5500L);
            
            OutputStream os = response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
        }
        catch(Exception e)
        {
        	out.println("Unable to process request ... : " + e.getMessage());
        }
    }

    public void PlayAudioFileVoice2(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
    {
        String FileName = request.getParameter("file");
        String RecordingPath = this.getServletContext().getInitParameter("outbound_rec_dir") + FileName;
        try
        {
            response.setContentType("audio/x-gsm");
            response.setHeader("Content-Disposition", "attachment;filename=" + FileName);

            FileInputStream fin = new FileInputStream(RecordingPath);
            byte content[] = new byte[fin.available()];
            fin.read(content);
            fin.close();
            
            OutputStream os = response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
        }
        catch(Exception e)
        {
            out.println("Unable to fetch data ... : " + e.getMessage());
        }
    }
    
}
