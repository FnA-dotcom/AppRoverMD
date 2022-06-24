//
// Decompiled by Procyon v0.5.36
//

package oe;

import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

//import com.itextpdf.text.pdf.PdfWriter;

public class ClaimCoding extends HttpServlet {
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
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();

        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);

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


        if (ActionID.equals("GetCodes")) {
            this.GetCodes(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("GetCodeDetails")) {
            this.GetCodeDetails(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("GetCodingStarttime")) {
            this.GetCodingStarttime(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("SaveCodingData")) {
            this.SaveCodingData(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("CodingPdf")) {
            this.CodingPdf(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("CodingReport_Input")) {
            this.CodingReport_Input(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("CodingReport")) {
            this.CodingReport(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("CodeWiseReport_Input")) {
            this.CodeWiseReport_Input(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("CodeWiseReport")) {
            this.CodeWiseReport(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("CodeCountWiseReport")) {
            this.CodeCountWiseReport(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("GetContactInfo")) {
            this.GetContactInfo(request, out, conn, context, UserId, response);
        } else if (ActionID.compareTo("download_direct") == 0) {
            this.download_direct(request, response, out, conn);
        } else if (ActionID.equals("GetModes")) {
            this.GetModes(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("GetRevCode")) {
            this.GetRevCode(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("GetCodingList")) {
            this.GetCodingList(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("GetDiagnosisCodes")) {
            this.GetDiagnosisCodes(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("GetCPTCodes")) {
            this.GetCPTCodes(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("GetModifierCodes")) {
            this.GetModifierCodes(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("GetDiagnosisCodes_Table")) {
            this.GetDiagnosisCodes_Table(request, out, conn, context, UserId, response);
        } else {
            out.println("Under Development");
        }
        try {
            conn.close();
        } catch (Exception ex) {
        }
        out.flush();
        out.close();
    }

    void GetCodes(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        try {
            String Code = request.getParameter("Code").trim();
            int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
            StringBuffer CodeList = new StringBuffer();
            String TableName = "";


            if (ClientId == 8) {
                TableName = "ChargeMaster_GTEC_Orange";
            } else {
                TableName = "ChargeMaster_GTEC_Orange";
            }


            Query = "Select Id, CPTCode, ShortDescription, Price from oe." + TableName + " where CPTCode like '%" + Code + "%' OR ShortDescription like '%" + Code + "%' ";
//      Query = "Select CPTCode , ShortDescription , Price from oe."+ TableName;
            CodeList.append("<select class=\"form-control select2\" id=\"Code\" name=\"Code\" onchange=\"GetCodeDetails(this.value);\" >");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//      CodeList.append("<table id=\"CPTCodesTable\" class=\"table table-bordered table-striped\">");
//      CodeList.append("<thead>");
//      CodeList.append("<tr>");
//      CodeList.append("<th>Code</th>");
//      CodeList.append("<th>Description</th>");
//      CodeList.append("<th>Price</th>");
//      CodeList.append("</tr>");
//      CodeList.append("</thead>");
//      CodeList.append("<tbody >");
            while (rset.next()) {
//        CodeList.append("<tr onclick=\"GetCodesCPTCode('"+rset.getString(1)+"')\">");
//        CodeList.append("<td align=left >" + rset.getString(1) + "</td>\n");
//        CodeList.append("<td align=left >" + rset.getString(2) + "</td>\n");
//        CodeList.append("<td align=left>" + rset.getString(3) + "</td>\n");
//        CodeList.append("</tr>");
                CodeList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getDouble(4) + "</option>");
            }
            rset.close();
            stmt.close();
//      CodeList.append("</tbody>");
//      CodeList.append("</table>");

            out.println(CodeList.toString());

        } catch (Exception e) {
            out.println("Error in getting Codes: " + e.getMessage());
        }
    }

    void GetCodeDetails(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        try {
            String Code = request.getParameter("Code").trim();
            int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
            String Acc = (request.getParameter("Acc").trim());
            String mrn = (request.getParameter("mrn").trim());
            String TableName = "";
            String CPTCode = "";
            String CPTDescription = "";
            String Price = "";
            String StartDateTime = "";
            int ClaimCodingMasterID = 0;

            if (ClientId == 8) {
                TableName = "ChargeMaster_GTEC_Orange";
            } else {
                TableName = "ChargeMaster_GTEC_Orange";
            }
//      try{
//        Query = "Select max(Id) from oe.ClaimCoding_Master where MRN = '"+mrn+"' and Acc = '"+Acc+"' and Status = 0";
//        stmt = conn.createStatement();
//        rset = stmt.executeQuery(Query);
//        if (rset.next()) {
//          ClaimCodingMasterID = rset.getInt(1);
//        }
//        rset.close();
//        stmt.close();
//      }catch(Exception e){
//        out.println("Error in getting ClaimCoding_Master ID: "+ e.getMessage());
//      }
//
//      System.out.println(ClaimCodingMasterID);
//      try {
//        if(ClaimCodingMasterID != 0) {
//          Query = " Update oe.ClaimCoding_Master set Status = 1 where Id = "+ClaimCodingMasterID;
//          System.out.println(Query);
//          stmt = conn.createStatement();
//          stmt.executeUpdate(Query);
//          stmt.close();
//
//          Query = " Update oe.ClaimCoding_Detail set Status = 1 where Id = "+ClaimCodingMasterID;
//          System.out.println(Query);
//          stmt = conn.createStatement();
//          stmt.executeUpdate(Query);
//          stmt.close();
//        }
//      } catch (Exception e) {
//        System.out.println("Updating ClaimCoding Master and Details Table Info:" + e.getMessage());
//        //return
//      }
//
//      if(ClaimCodingMasterID == 0) {
//        try {
//          final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO oe.ClaimCoding_Master (MRN,Acc,UserId ,Starttime,Endtime,CreatedDate, Status, ClientId) \n" +
//                  "VALUES (?,?,?,now(),now(),now(),0,?) ");
//          MainReceipt.setString(1, mrn);
//          MainReceipt.setString(2, Acc);
//          MainReceipt.setString(3, UserId);
//          MainReceipt.setInt(4, ClientId);
//          MainReceipt.executeUpdate();
//          MainReceipt.close();
//        } catch (Exception e) {
//          System.out.println("Error 2- Insertion ClaimCoding Master Table :" + e.getMessage());
//          //return;
//        }
//
//        Query = "Select max(Id) from oe.ClaimCoding_Master where MRN = '"+mrn+"' and Acc = '"+Acc+"' and Status = 0";
//        stmt = conn.createStatement();
//        rset = stmt.executeQuery(Query);
//        if (rset.next()) {
//          ClaimCodingMasterID = rset.getInt(1);
//        }
//        rset.close();
//        stmt.close();
//
//      }
//      else{
//        try {
//          Query = "Update ClaimCoding_Master set Starttime = now(), UserId = '"+UserId+"' where Id = "+ClaimCodingMasterID;
//          stmt = conn.createStatement();
//          stmt.executeUpdate(Query);
//          stmt.close();
//        } catch (Exception e) {
//          System.out.println("Updating ClaimCoding Master Table Info:" + e.getMessage());
//          //return;
//        }
//      }

            try {
                Query = "Select Id, CPTCode, ShortDescription, Price from oe." + TableName + " where CPTCode = '" + Code + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    CPTCode = rset.getString(2);
                    CPTDescription = rset.getString(3);
                    Price = rset.getString(4);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error in getting Code Details: " + e.getMessage());
            }
            out.println(CPTCode.toString() + "|" + CPTDescription + "|" + Price);

        } catch (Exception e) {
            out.println("Error in getting Codes Details: " + e.getMessage());
        }
    }

    void GetCodingStarttime(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        ResultSet rset = null;
        Statement stmt = null;
        String CodingStarttime = "";
        String Query = "";
        try {
            Query = "Select NOW()";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                CodingStarttime = rset.getString(1);
            }
            rset.close();
            stmt.close();

            out.println(CodingStarttime);

        } catch (Exception e) {
            out.println("Error in Getting Current Date time" + e.getMessage());
        }
    }

    void SaveCodingData(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int i = 0;
        int j = 0;
        int k = 0;
        String FirstName = "";
        String LastName = "";
        double TotalAmount = 0.0;
        double PricePerCPTCode = 0.0;
        String Mod1 = "", Mod2 = "", Mod3 = "", Mod4 = "";
        String RevCode = "";
        String ClaimStatus = "";
        try {
            String CodingString = request.getParameter("CodingString");
            String Acc = (request.getParameter("Acc").trim());
            String mrn = (request.getParameter("mrn").trim());
//      int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
//      int ClaimCodingMasterID = Integer.parseInt(request.getParameter("ClaimCodingMasterID").trim());

            try {
                Query = "Select IFNULL(firstname,'-'), IFNULL(lastname,'-') from oe.filelogs_sftp where MRN = '" + mrn + "' and acc = '" + Acc + "' ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    FirstName = rset.getString(1);
                    LastName = rset.getString(2);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error in Getting oe.filelogs_sftp Data: " + e.getMessage());
            }
//*****************ROW COUNT OF EVERY TABLE > 0 THEN WE WILL GET START SPLITTING THE DATA FROM HERE******************

            String Path = "/opt/apache-tomcat-7.0.65/webapps/oe/CodingPdf/";
            String FileName = FirstName + "_" + LastName + "_" + mrn + "_" + Acc + ".pdf";

//      String[] myInfo1 = new String[0];
//      myInfo1 = CodingString.split("\\^");
//      int RowCount = Integer.parseInt(myInfo1[1].substring(myInfo1[1].indexOf("=") + 1));
//      String[][] CodingTableInput = new String[RowCount][10];
//      for (j = (i = (k = 0)), i = 2; i < myInfo1.length; ++i) {
//        if (myInfo1[i].length() > 0) {
//          CodingTableInput[k][j] = myInfo1[i].substring(myInfo1[i].indexOf("=") + 1);
//          if (++j > 9) {
//            j = 0;
//            ++k;
//          }
//        }
//      }
//      for(i = 0; i < RowCount ; i++){
//        System.out.println("Unit Price: "+Double.parseDouble(CodingTableInput[i][7]));
//      }
//
//      for(i = 0; i < RowCount ; i++){
//        TotalAmount += (Double.parseDouble(CodingTableInput[i][7]) * Integer.parseInt(CodingTableInput[i][8]));
//      }
//      System.out.println("TotalAmount: "+TotalAmount);
//
//
//      try {
//        Query = " Update oe.ClaimCoding_Master set Endtime = now() , FileName = '"+FileName+"', Path = '"+Path+"', TotalAmount = '"+TotalAmount+"' where Status = 0 and Id = "+ClaimCodingMasterID ;
//        stmt = conn.createStatement();
//        stmt.executeUpdate(Query);
//        stmt.close();
//      } catch (Exception e) {
//        System.out.println("Updating ClaimCoding Master Table Info:" + e.getMessage());
//        //return;
//      }
//
//      try {
//        for (i = 0; i < RowCount; i++) {
//          if(CodingTableInput[i][2] == null){
//            Mod1 = "";
//          }else {
//            Mod1 = CodingTableInput[i][2];
//          }
//          if(CodingTableInput[i][3] == null){
//            Mod2 = "";
//          }else{
//            Mod2 = CodingTableInput[i][3];
//          }
//          if(CodingTableInput[i][4] == null){
//            Mod3 = "";
//          }else{
//            Mod3 = CodingTableInput[i][4];
//          }
//          if(CodingTableInput[i][5] == null){
//            Mod4 = "";
//          }else{
//            Mod4 = CodingTableInput[i][5];
//          }
//          if(CodingTableInput[i][6] == null){
//            RevCode = "";
//          }else{
//            RevCode = CodingTableInput[i][6];
//          }
//          if(CodingTableInput[i][9] == null){
//            ClaimStatus = "";
//          }else{
//            ClaimStatus = CodingTableInput[i][9];
//          }
//
//          final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO oe.ClaimCoding_Detail (ClaimCoding_MasterId, MRN,Acc,UserId ,CodeId,Price,Status,CreatedDate, Mod1, Mod2, Mod3, Mod4, RevCode, Unit, ClaimStatus) \n" +
//                  "VALUES (?,?,?,?,?,?,0,now(),?,?,?,?,?,?,?) ");
//          MainReceipt.setInt(1, ClaimCodingMasterID);
//          MainReceipt.setString(2, mrn);
//          MainReceipt.setString(3, Acc);
//          MainReceipt.setString(4, UserId);
//          MainReceipt.setString(5, CodingTableInput[i][1]);
//          MainReceipt.setString(6, CodingTableInput[i][7]);
//          MainReceipt.setString(7, Mod1);
//          MainReceipt.setString(8, Mod2);
//          MainReceipt.setString(9, Mod3);
//          MainReceipt.setString(10, Mod4);
//          MainReceipt.setString(11, RevCode);
//          MainReceipt.setString(12, CodingTableInput[i][8]);
//          MainReceipt.setString(13, ClaimStatus);
//          MainReceipt.executeUpdate();
//          MainReceipt.close();
//        }
//
//      } catch (Exception e) {
//        System.out.println("Error 2- Insertion ClaimCoding_Detail Table :" + e.getMessage());
//        //return;
//      }
//
//      out.println("1");
        } catch (Exception e) {
            out.println("Error in Saving Coding Data : " + e.getMessage());
            String str = "";
            for (i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    void CodingPdf(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String FirstName = "";
        String LastName = "";
        String DOS = "";
        int SNo = 1;
        double TotalAmount = 0.0;
        int ClaimCodingMasterId = 0;
        String ClientName = "";
        try {
            String Acc = request.getParameter("Acc").trim();
            String mrn = request.getParameter("mrn").trim();
            int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
            StringBuffer PreviousCoding = new StringBuffer();
            String TableName = "";
            if (ClientId == 8) {
                TableName = "ChargeMaster_GTEC_Orange";
            } else {
                TableName = "ChargeMaster_GTEC_Orange";
            }

            try {
                Query = "Select name from oe.clients where Id = " + ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClientName = rset.getString(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error in getting ClientName" + e.getMessage());
            }


            try {
                Query = "Select Id from oe.ClaimCoding_Master where status = 0 and MRN = '" + mrn + "' and Acc = '" + Acc + "' and ClientId = " + ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClaimCodingMasterId = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error in Getting ClaimCoding_Master Id: " + e.getMessage());
            }

            try {
                Query = "Select IFNULL(firstname,'-'), IFNULL(lastname,'-'), IFNULL(DATE_FORMAT(dosdate,'%m/%d/%Y %H:%i'),'-') from oe.filelogs_sftp where MRN = '" + mrn + "' and acc = '" + Acc + "' ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    FirstName = rset.getString(1);
                    LastName = rset.getString(2);
                    DOS = rset.getString(3);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error in Getting oe.filelogs_sftp Data: " + e.getMessage());
            }


            String inputFilePath = "";
            inputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/UB04_Form.pdf"; // Existing file

            String outputFilePath = "/opt/apache-tomcat-7.0.65/webapps/oe/TemplatePdf/" + FirstName + LastName + "_" + mrn + "_" + Acc + ".pdf"; // New file
            OutputStream fos = new FileOutputStream(new File(outputFilePath));


            PdfReader pdfReader = new PdfReader(inputFilePath);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                if (i == 1) {
                    PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);


                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(15, 820); // set x and y co-ordinates
                    pdfContentByte.showText("GOLDEN TRIANGLE EMERGENCY CE"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(15, 808); // set x and y co-ordinates
                    pdfContentByte.showText("3107 EDGAR BROWN DRIVE"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(15, 795); // set x and y co-ordinates
                    pdfContentByte.showText("ORANGE, TX 77630-5347"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(15, 785); // set x and y co-ordinates
                    pdfContentByte.showText("(409)237-5870"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(22, 760); // set x and y co-ordinates
                    pdfContentByte.showText(LastName + " , " + FirstName); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(245, 760); // set x and y co-ordinates
                    pdfContentByte.showText("ORANGE"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(460, 760); // set x and y co-ordinates
                    pdfContentByte.showText("TX"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(500, 760); // set x and y co-ordinates
                    pdfContentByte.showText("77630"); // add the text
                    pdfContentByte.endText();

                    pdfContentByte.beginText();
                    pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                    pdfContentByte.setColorFill(BaseColor.BLACK);
                    pdfContentByte.setTextMatrix(550, 808); // set x and y co-ordinates
                    pdfContentByte.showText("131"); // add the text
                    pdfContentByte.endText();


                }


            }
            pdfStamper.close(); //close pdfStamper


//      response.setContentType("application/pdf");
//      out = response.getWriter();
//      String filepath = outputFilePath;
//      response.setHeader("Content-Disposition", "attachment; filename=" + filepath + ";");
//      FileInputStream fileOut = new FileInputStream(FirstNameNoSpaces+LastName+ID+"_"+DateTime+".pdf");
//      fileOut.close();
//      out.close();

            File pdfFile = new File(outputFilePath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + FirstName + LastName + "_" + mrn + "_" + Acc + ".pdf");
            response.setContentLength((int) pdfFile.length());

            FileInputStream fileInputStream = new FileInputStream(pdfFile);
            OutputStream responseOutputStream = response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }


        } catch (Exception e) {
            out.println("Error in getting Codes: " + e.getMessage());
        }
    }

    void CodingReport_Input(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer UserList = new StringBuffer();
        try {
            Query = "Select userid, CONCAT(firstname, ' ', lastname) from oe.sysusers";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            UserList.append("<option value=-1> All </option>");
            while (rset.next()) {
                UserList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserList", UserList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/CodingReportSearch.html");
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    void CodingReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int SNo = 1;
        StringBuffer CDRList = new StringBuffer();
        StringBuffer UserList = new StringBuffer();
        String StartDateT = request.getParameter("StartDateT").trim();
        UserId = request.getParameter("UserId").trim();
        String FMonth = StartDateT.substring(0, 2);
        String FDay = StartDateT.substring(3, 5);
        String FYear = StartDateT.substring(6, 10);
        String FHour = StartDateT.substring(11, 13);
        String FMin = StartDateT.substring(14, 16);
        String EMonth = StartDateT.substring(22, 24);
        String EDay = StartDateT.substring(25, 27);
        String EYear = StartDateT.substring(28, 32);
        String EHour = StartDateT.substring(33, 35);
        String EMin = StartDateT.substring(36, 38);
        String StartDate = FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00";
        String EndDate = EYear + "-" + EMonth + "-" + EDay + " " + EHour + ":" + EMin + ":59";
        String UserCondition = "";

        if (UserId.equals("-1")) {
            UserCondition = "";
        } else {
            UserCondition = " and a.UserId = '" + UserId + "'";
        }

        try {
            Query = "Select userid, CONCAT(firstname, ' ', lastname) from oe.sysusers";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            UserList.append("<option value=-1> All </option>");
            while (rset.next()) {
                UserList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Query = " Select  a.MRN, CONCAT(c.lastname, ' ', c.firstname), e.name, DATE_FORMAT(c.dosdate,'%m/%d/%Y %T'), DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'), a.UserId, b.cmdref, a.Path as CodepdfPath , a.FileName as CodePdfFName, " +
                    " c.target as ChartPdfPath, c.filename as ChartpdfFName, TIMEDIFF(a.Endtime,a.Starttime), a.TotalAmount from oe.ClaimCoding_Master a " +
                    " LEFT JOIN oe.claim_info_master b on a.MRN = b.mrn " +
                    " LEFT JOIN oe.filelogs_sftp c on c.MRN = a.MRN " +
                    //" LEFT JOIN oe.ClaimCoding_Detail d on a.Id = d.ClaimCoding_MasterId " +
                    " LEFT JOIN oe.clients e on c.clientdirectory = e.Id" +
                    " where a.CreatedDate >= '" + StartDate + "' and a.CreatedDate <= '" + EndDate + "' " + UserCondition;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                if (rset.getString(1) != null) {
                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    CDRList.append("<td><a href=https://rovermd.com:8443/oe/oe.ClaimCoding?ActionID=download_direct&fname=" + rset.getString(11) + "&path=" + rset.getString(10) + " target='_blank'>Chart Report</a></td>");//Chartpdf
                    CDRList.append("<td><a href=https://rovermd.com:8443/oe/oe.ClaimCoding?ActionID=download_direct&fname=" + rset.getString(9) + "&path=" + rset.getString(8) + " target='_blank'>Code Report</a></td>");//Codepdf
                    CDRList.append("<td align=left>" + rset.getString(12) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(13) + "</td>\n");
                    CDRList.append("</tr>");
                    ++SNo;
                } else {
                }
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("UserList", UserList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/CodingReportSearch.html");
        } catch (Exception e) {
            out.println("Error in getting Coding Report: " + e.getMessage());
        }
    }

    void CodeWiseReport_Input(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer UserList = new StringBuffer();
        StringBuffer CodeList = new StringBuffer();
        try {
            Query = "Select userid, CONCAT(firstname, ' ', lastname) from oe.sysusers";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            UserList.append("<option value=-1> All </option>");
            while (rset.next()) {
                UserList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Query = "Select Id, CONCAT(CPTCode, ' | ', ShortDescription , ' | ', Price), CPTCode from oe.ChargeMaster_GTEC_Orange order by Id";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            CodeList.append("<option value=-1> All </option>");
            while (rset.next()) {
                CodeList.append("<option class=Inner value=\"" + rset.getString(3) + "\">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserList", UserList.toString());
            Parser.SetField("CodeList", CodeList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/CodeWiseReportSearch.html");
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    void CodeWiseReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null, stmt2 = null;
        ResultSet rset = null, rset2 = null;
        String Query = "", Query2 = "";
        int SNo = 1;
        StringBuffer CDRList = new StringBuffer();
        StringBuffer UserList = new StringBuffer();
        String StartDateT = request.getParameter("StartDateT").trim();
        UserId = request.getParameter("UserId").trim();
        String Code = request.getParameter("Code").trim();
        String FMonth = StartDateT.substring(0, 2);
        String FDay = StartDateT.substring(3, 5);
        String FYear = StartDateT.substring(6, 10);
        String FHour = StartDateT.substring(11, 13);
        String FMin = StartDateT.substring(14, 16);
        String EMonth = StartDateT.substring(22, 24);
        String EDay = StartDateT.substring(25, 27);
        String EYear = StartDateT.substring(28, 32);
        String EHour = StartDateT.substring(33, 35);
        String EMin = StartDateT.substring(36, 38);
        String StartDate = FYear + "-" + FMonth + "-" + FDay + " " + FHour + ":" + FMin + ":00";
        String EndDate = EYear + "-" + EMonth + "-" + EDay + " " + EHour + ":" + EMin + ":59";
        String StartDateN = FYear + "-" + FMonth + "-" + FDay;
        String EndDateN = EYear + "-" + EMonth + "-" + EDay;
        String UserCondition = "";
        String CodeCondition = "";

        if (UserId.equals("-1")) {
            UserCondition = "";
        } else {
            UserCondition = " and UserId = '" + UserId + "'";
        }


        try {
            if (Code.equals("-1")) {
                Query = "SELECT Id, CPTCode, ShortDescription, Price FROM oe.ChargeMaster_GTEC_Orange";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    Query2 = "Select COUNT(*), CodeId, MRN from oe.ClaimCoding_Detail where CodeId = '" + rset.getString(2) + "' and CreatedDate >= '" + StartDate + "' and CreatedDate <= '" + EndDate + "' " + UserCondition;
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    while (rset2.next()) {
                        if (rset2.getInt(1) > 0) {
                            CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                            CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                            CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                            CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                            CDRList.append("<td><a href=https://rovermd.com:8443/oe/oe.ClaimCoding?ActionID=CodeCountWiseReport&CodeId=" + rset2.getString(2) + "&StartDate=" + StartDateN + "&EndDate=" + EndDateN + " target='_blank'>" + rset2.getInt(1) + "</a></td>");//Count
                            SNo++;
                        }
                    }
                    rset2.close();
                    stmt2.close();
                }
                rset.close();
                stmt.close();
            } else {
                Query2 = " Select b.Id, b.CPTCode, b.ShortDescription, b.Price, COUNT(*), a.MRN from oe.ClaimCoding_Detail a " +
                        " LEFT JOIN oe.ChargeMaster_GTEC_Orange b on a.CodeId = b.CPTCode " +
                        " where a.CodeId = '" + Code + "' and a.CreatedDate >= '" + StartDate + "' and a.CreatedDate <= '" + EndDate + "' " + UserCondition;
                stmt2 = conn.createStatement();
                rset2 = stmt2.executeQuery(Query2);
                while (rset2.next()) {
                    if (rset2.getInt(4) > 0) {
                        CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                        CDRList.append("<td align=left>" + rset2.getString(2) + "</td>\n");
                        CDRList.append("<td align=left>" + rset2.getString(3) + "</td>\n");
                        CDRList.append("<td align=left>" + rset2.getString(4) + "</td>\n");
                        CDRList.append("<td><a href=https://rovermd.com:8443/oe/oe.ClaimCoding?ActionID=CodeCountWiseReport&CodeId=" + Code + "&StartDate=" + StartDateN + "&EndDate=" + EndDateN + " target='_blank'>" + rset2.getInt(5) + "</a></td>");//Count

                        SNo++;
                    }
                }
                rset2.close();
                stmt2.close();
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/CodeWiseReport.html");
        } catch (Exception e) {
            out.println("Error in getting Coding Report: " + e.getMessage() + Query + Query2);
        }
    }

    void CodeCountWiseReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int SNo = 1;
        StringBuffer CDRList = new StringBuffer();
        String CodeId = request.getParameter("CodeId").trim();
        String StartDate = request.getParameter("StartDate").trim();
        String EndDate = request.getParameter("EndDate").trim();

        try {
            Query = " Select  a.MRN, CONCAT(c.lastname, ' ', c.firstname), e.name, DATE_FORMAT(c.dosdate,'%m/%d/%Y %T'), DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'), a.UserId, b.cmdref, a.Path as CodepdfPath , a.FileName as CodePdfFName, " +
                    " c.target as ChartPdfPath, c.filename as ChartpdfFName, TIMEDIFF(a.Endtime,a.Starttime), a.TotalAmount from oe.ClaimCoding_Master a " +
                    " LEFT JOIN oe.claim_info_master b on a.MRN = b.mrn " +
                    " LEFT JOIN oe.filelogs_sftp c on c.MRN = a.MRN " +
                    " LEFT JOIN oe.ClaimCoding_Detail d on a.Id = d.ClaimCoding_MasterId " +
                    " LEFT JOIN oe.clients e on c.clientdirectory = e.Id" +
                    " where a.CreatedDate >= '" + StartDate + " 00:00:00' and a.CreatedDate <= '" + EndDate + " 23:59:59' and d.CodeId = " + CodeId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                if (rset.getString(1) != null) {
                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    CDRList.append("<td><a href=https://rovermd.com:8443/oe/oe.ClaimCoding?ActionID=download_direct&fname=" + rset.getString(11) + "&path=" + rset.getString(10) + " target='_blank'>Chart Report</a></td>");//Chartpdf
                    CDRList.append("<td><a href=https://rovermd.com:8443/oe/oe.ClaimCoding?ActionID=download_direct&fname=" + rset.getString(9) + "&path=" + rset.getString(8) + " target='_blank'>Code Report</a></td>");//Codepdf
                    CDRList.append("<td align=left>" + rset.getString(12) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(13) + "</td>\n");
                    CDRList.append("</tr>");
                    ++SNo;
                } else {
                }
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", CDRList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/CodingReportCountWIse.html");
        } catch (Exception e) {
            out.println("Error in getting Coding Report: " + e.getMessage());
        }
    }

    void GetContactInfo(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String PhNumber = "";
        String Email = "";
        String Address = "";
        try {
            String mrn = request.getParameter("mrn").trim();
            int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
            String Database = "";

            if (ClientId == 8) {
                Database = "oe_2";
            } else if (ClientId == 9) {
                Database = "Victoria";
            } else if (ClientId == 10) {
                Database = "Oddasa";
            }

            Query = "Select IFNULL(PhNumber,'-'), IFNULL(Email,'-'), IFNULL(Address,'-') from " + Database + ".PatientReg where MRN = '" + mrn + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PhNumber = rset.getString(1);
                Email = rset.getString(2);
                Address = rset.getString(3);
            }
            rset.close();
            stmt.close();

            out.println(PhNumber.toString() + "|" + Email + "|" + Address);

        } catch (Exception e) {
            out.println("Error in getting Codes: " + e.getMessage());
        }
    }

    void GetModes(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        try {
            String Mod_Text = request.getParameter("Mod_Text").trim();
            int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
            int Flag = Integer.parseInt(request.getParameter("Flag").trim());

            StringBuffer ModList = new StringBuffer();
            String TableName = "";

            if (ClientId == 8) {
                TableName = "ModifierCodes";
            } else {
                TableName = "ModifierCodes";
            }

            Query = "Select Id, Code, Description from oe." + TableName + " where Status = 1 and Code like '%" + Mod_Text + "%' OR Description  like '%" + Mod_Text + "%' ";
            if (Flag == 1) {
                ModList.append("<select class=\"form-control select2\" id=\"Mod1\" name=\"Mod1\"  >");
            } else if (Flag == 2) {
                ModList.append("<select class=\"form-control select2\" id=\"Mod2\" name=\"Mod2\"  >");
            } else if (Flag == 3) {
                ModList.append("<select class=\"form-control select2\" id=\"Mod3\" name=\"Mod3\"  >");
            } else if (Flag == 4) {
                ModList.append("<select class=\"form-control select2\" id=\"Mod4\" name=\"Mod4\"  >");
            }
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ModList.append("<option value=-1> Please Select From Below Codes </option>");
            while (rset.next()) {
                ModList.append("<option class=Inner value=\"" + rset.getString(2) + "\">" + rset.getString(2) + " | " + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();
            ModList.append("</select>");
            out.println(ModList.toString());

        } catch (Exception e) {
            out.println("Error in getting Codes: " + e.getMessage());
        }
    }

    void GetRevCode(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        try {
            String RevCode_Text = request.getParameter("RevCode_Text").trim();
            int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());

            StringBuffer RevCodeList = new StringBuffer();
            String TableName = "";

            if (ClientId == 8) {
                TableName = "RevenueCode";
            } else {
                TableName = "RevenueCode";
            }

            Query = "Select Id, Codes, Description from oe." + TableName + " where Status = 1 and Codes like '%" + RevCode_Text + "%' OR Description  like '%" + RevCode_Text + "%' OR Category  like '%" + RevCode_Text + "%' ";
            RevCodeList.append("<select class=\"form-control select2\" id=\"RevCode\" name=\"RevCode\"  >");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            RevCodeList.append("<option value=-1> Please Select From Below Codes </option>");
            while (rset.next()) {
                RevCodeList.append("<option class=Inner value=\"" + rset.getString(2) + "\">" + rset.getString(2) + " | " + rset.getString(3) + "</option>");
            }
            rset.close();
            stmt.close();
            RevCodeList.append("</select>");
            out.println(RevCodeList.toString());

        } catch (Exception e) {
            out.println("Error in getting Codes: " + e.getMessage());
        }
    }

    void GetCodingList(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int SNo = 1;
        int Found = 0;
        Double TotalAmount = 0.0;
        int ClaimCodingMasterId = 0;
        StringBuffer CodingList = new StringBuffer();
        String mrn = request.getParameter("mrn").trim();
        int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
        String Acc = request.getParameter("Acc").trim();
        String UserCondition = "";

        try {
            Query = "Select COUNT(*) from oe.ClaimCoding_Detail where MRN = '" + mrn + "' and Acc = '" + Acc + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Found = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            if (Found > 0) {
                Query = " Select a.MRN , a.CodeId , IFNULL(a.Mod1,'') , IFNULL(a.Mod2,''), IFNULL(a.Mod3,''), IFNULL(a.Mod4,''), IFNULL(a.RevCode,''), b.ShortDescription, " +
                        " a.Price, a.Unit, IFNULL(a.ClaimStatus, ''), IFNULL(a.ClaimCoding_MasterId,0) from oe.ClaimCoding_Detail a " +
                        " LEFT JOIN oe.ChargeMaster_GTEC_Orange b on a.CodeId = b.CPTCode where a.MRN = '" + mrn + "' and a.Acc = '" + Acc + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    ClaimCodingMasterId = rset.getInt(12);
                    TotalAmount += rset.getDouble(9);
                    CodingList.append("<tr><td align=left>" + rset.getString(1) + "</td>\n");
                    CodingList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CodingList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CodingList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CodingList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CodingList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                    CodingList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    CodingList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                    CodingList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    CodingList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    CodingList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    CodingList.append("<td align=left>" + "Drug Info" + "</td>\n");
                    CodingList.append("<td align=left>" + "<i class='fa fa-trash-o fa-fw' aria-hidden='true' onClick='DeleteInfo(this)'></i>" + "</td>\n");
                    CodingList.append("</tr>");
                }
                rset.close();
                stmt.close();
                out.println(CodingList + "|" + String.valueOf(TotalAmount) + "|" + ClaimCodingMasterId);
            } else {

                out.println("0");
            }
        } catch (Exception e) {
            out.println("Error in getting Coding List: " + e.getMessage());
        }
    }

    void GetDiagnosisCodes(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        try {
            String Code = request.getParameter("Code").trim();
            int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
            String DiagnosisCode = "";
            String Description = "";


            Query = "Select Code, Description from oe.DiagnosisCodes where Code = '" + Code + "'  ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DiagnosisCode = rset.getString(1);
                Description = rset.getString(2);
            }
            rset.close();
            stmt.close();
            out.println(DiagnosisCode.toString() + "|" + Description);

        } catch (Exception e) {
            out.println("Error in getting Diagnosis Codes: " + e.getMessage());
        }
    }

    void GetCPTCodes(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        try {
            int ClientId = Integer.parseInt(request.getParameter("ClientId").trim());
            StringBuffer CodeList = new StringBuffer();
            String ChargeMasterTableName = "";

            Query = "Select ChargeMasterTableName from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ChargeMasterTableName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Query = "Select Id, CPTCode, ShortDescription, Price from oe." + ChargeMasterTableName;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            CodeList.append("<table id=\"CPTCodesTable\" class=\"table table-bordered table-striped\">");
            CodeList.append("<thead>");
            CodeList.append("<tr>");
            CodeList.append("<th>Code</th>");
            CodeList.append("<th>Description</th>");
            CodeList.append("<th>Price</th>");
            CodeList.append("</tr>");
            CodeList.append("</thead>");
            CodeList.append("<tbody >");
            while (rset.next()) {
                CodeList.append("<tr onclick=\"GetCPTCodes('" + rset.getString(2) + "')\">");
                CodeList.append("<td align=left >" + rset.getString(2) + "</td>\n");
                CodeList.append("<td align=left >" + rset.getString(3) + "</td>\n");
                CodeList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CodeList.append("</tr>");
            }
            rset.close();
            stmt.close();
            CodeList.append("</tbody>");
            CodeList.append("</table>");

            out.println(CodeList.toString());
        } catch (Exception e) {
            out.println("Error in getting Codes: " + e.getMessage());
        }
    }

    void GetModifierCodes(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        try {
            String ModTextBoxId = request.getParameter("ModTextBoxId").trim();
            StringBuffer ModifierList = new StringBuffer();

            Query = "Select Id, Code, Description, Case WHEN Status = 1 THEN 'Active' ELSE 'InActive' END from oe.ModifierCodes";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ModifierList.append("<table id=\"ModifierCodesTable\" class=\"table table-bordered table-striped\">");
            ModifierList.append("<thead>");
            ModifierList.append("<tr>");
            ModifierList.append("<th>Code</th>");
            ModifierList.append("<th>Description</th>");
            ModifierList.append("<th>Status</th>");
            ModifierList.append("</tr>");
            ModifierList.append("</thead>");
            ModifierList.append("<tbody >");
            while (rset.next()) {
                ModifierList.append("<tr onclick=\"GetModifierCode('" + rset.getString(2) + "' , '" + ModTextBoxId + "')\">");
                ModifierList.append("<td align=left >" + rset.getString(2) + "</td>\n");
                ModifierList.append("<td align=left >" + rset.getString(3) + "</td>\n");
                ModifierList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                ModifierList.append("</tr>");
            }
            rset.close();
            stmt.close();
            ModifierList.append("</tbody>");
            ModifierList.append("</table>");

            out.println(ModifierList.toString());
        } catch (Exception e) {
            out.println("Error in getting Codes: " + e.getMessage());
        }
    }

    void GetDiagnosisCodes_Table(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        try {
            //String DiagnosisCodesTextBoxId = request.getParameter("DiagnosisCodesTextBoxId").trim();
            String txtDiagnosisCodesSearch = request.getParameter("txtDiagnosisCodesSearch").trim();
            StringBuffer DiagnosisCodesList = new StringBuffer();

            Query = "Select Id, Code, Description, Case WHEN Status = 1 THEN 'Active' ELSE 'InActive' END from oe.DiagnosisCodes where Code like '%" + txtDiagnosisCodesSearch + "%' OR Description like '%" + txtDiagnosisCodesSearch + "%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            DiagnosisCodesList.append("<table id=\"DiagnosisCodesTable\" class=\"table table-bordered table-striped\">");
            DiagnosisCodesList.append("<thead>");
            DiagnosisCodesList.append("<tr>");
            DiagnosisCodesList.append("<th>Code</th>");
            DiagnosisCodesList.append("<th>Description</th>");
            DiagnosisCodesList.append("<th>Status</th>");
            DiagnosisCodesList.append("</tr>");
            DiagnosisCodesList.append("</thead>");
            DiagnosisCodesList.append("<tbody >");
            while (rset.next()) {
                DiagnosisCodesList.append("<tr onclick=\"GetDiagnosisCode('" + rset.getString(2) + "')\">");
                DiagnosisCodesList.append("<td align=left >" + rset.getString(2) + "</td>\n");
                DiagnosisCodesList.append("<td align=left >" + rset.getString(3) + "</td>\n");
                DiagnosisCodesList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                DiagnosisCodesList.append("</tr>");
            }
            rset.close();
            stmt.close();
            DiagnosisCodesList.append("</tbody>");
            DiagnosisCodesList.append("</table>");

            out.println(DiagnosisCodesList.toString());
        } catch (Exception e) {
            out.println("Error in getting Codes: " + e.getMessage());
        }
    }

    public void download_direct(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn) {
        final String FileName = request.getParameter("fname");
        final String path = request.getParameter("path");
        final String RecordingPath = String.valueOf(path) + FileName;
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
}
