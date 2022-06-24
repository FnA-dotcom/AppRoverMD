package md;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

public class avail_res1 {

    static StringBuilder html = new StringBuilder();
    static StringBuilder RequestedServiceTypeBuff = new StringBuilder();
    static StringBuilder SubscriberBuff = new StringBuilder();
    static StringBuilder PatientBuff = new StringBuilder();
    static StringBuilder PayerBuff = new StringBuilder();
    static StringBuilder requestingProviderBuff = new StringBuilder();
    static StringBuilder plansBuff = new StringBuilder();
    static StringBuilder supplementalInformationBuff = new StringBuilder();
    static StringBuilder hasHRAInformationBuff = new StringBuilder();
    static StringBuilder benifitsBuff = new StringBuilder();
    static StringBuilder statusDetailBuff = new StringBuilder();
    static String SubFirstName = "";
    static String SubMiddleName = "";
    static String SubLastName = "";
    static String SubmemberId = "";
    static String Subgender = "";
    static String SubbirthDate = "";
    static String Subaddress = "";
    static String Subline = "";
    static String Subcity = "";
    static String Substate = "";
    static String SubstateCode = "";
    static String SubzipCode = "";
    static String GroupNumPlan = "";
    static String GroupNamePlan = "";
    static String CoverageStartDatePlan = "";
    static String CoverageEndDatePlan = "";
    static String payerNotesPlan = "";
    static String insuranceTypePlan = "";
    static String PLANStatus = "";
    static int closer = 0;
    static int i = 0;

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        try {

            PrintWriter out1 = new PrintWriter("C:\\Users\\alisa\\OneDrive\\Desktop\\logs\\r2.html");


            File file = new File("C:\\Users\\alisa\\OneDrive\\Desktop\\logs\\response.json");
            ObjectMapper mapper = new ObjectMapper();

            JsonNode node = mapper.readTree(file);
            html.append("<!DOCTYPE html><html><head><style>#customers {  font-family: Arial, Helvetica, sans-serif;  border-collapse: collapse;  width: 100%;}#customers td, #customers th {\r\n" +
                    "  border: 1px solid #ddd;  padding: 8px;}#customers tr:nth-child(even){background-color: #f2f2f2;}#customers tr:hover {background-color: #ddd;}#customers th \r\n" +
                    "  {  padding-top: 12px;  padding-bottom: 12px;  text-align: left;  background-color: #4CAF50;  color: white;}</style></head><body>");

            html.append("  <style>h1,h3 {text-align: center;} table {border-spacing: 0px;table-layout: fixed; margin-left: auto; margin-right: auto;}\r\n" +
                    "          th {color: green;border: 1px solid black;}td {border: 1px solid black;word-wrap: break-word;}</style>");

            // processNode(node);
            processNode_m(node, "start");
            html.append("</body>\r\n" + "</html>");
            //System.out.println(html);
            out1.print(html.toString());
            out1.close();


        } catch (Exception ee) {
            System.out.println(ee.getMessage());
        }
    }


    public static String res1(String body) {
        String res1_out = "";
        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode node = mapper.readTree(body);
//            html.append("<!DOCTYPE html><html><head><style>#customers {  font-family: Arial, Helvetica, sans-serif;  border-collapse: collapse;  width: 100%;}#customers td, #customers th {\r\n" +
//                    "  border: 1px solid #ddd;  padding: 8px;}#customers tr:nth-child(even){background-color: #f2f2f2;}#customers tr:hover {background-color: #ddd;}#customers th \r\n" +
//                    "  {  padding-top: 12px;  padding-bottom: 12px;  text-align: left;  background-color: #4CAF50;  color: white;}</style></head><body>");
//
//            html.append("  <style>h1,h3 {text-align: center;} table {border-spacing: 0px;table-layout: fixed; margin-left: auto; margin-right: auto;}\r\n" +
//                    "          th {color: green;border: 1px solid black;}td {border: 1px solid black;word-wrap: break-word;}</style>");
            try {
                processNode_m(node, "start");
            } catch (Exception ee) {
                String str = "";
                for (int i = 0; i < ee.getStackTrace().length; ++i) {
                    str = str + ee.getStackTrace()[i] + "<br>";
                }
                System.out.println("ERROR IN RES1 Method-- ProcessNode_m: -->" + str);
            }
            html.setLength(0);
            html.append("<!DOCTYPE html><html lang=\"en\">");
            html.append("<head>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "    <meta name=\"description\" content=\"\">\n" +
                    "    <meta name=\"author\" content=\"\">\n" +
                    "    <link rel=\"icon\" href=\"/md/images/favicon.ico\">\n" +
                    "    <title>Rovermd - </title>\n" +
                    "    <link rel=\"stylesheet\" href=\"/md/main/css/vendors_css.css\">\n" +
                    "    <link rel=\"stylesheet\" href=\"/md/main/css/style.css\">\n" +
                    "    <link rel=\"stylesheet\" href=\"/md/main/css/skin_color.css\">\n" +
                    "    <link href=\"/md/main/css/plugins/sweetalert/sweetalert.css\" rel=\"stylesheet\">\n" +
                    "</head>");
            html.append("<style> label{ color:black;}#TableRST{ display:none;}#TableSubscriber{display:none;}#TablePatient{display:none;}" +
                    "#TablePayer{display:none;}#TablerequestingProvider{display:none;}#Tableplans{display:none;}" +
                    "#TablesupplementalInformation{display:none;}#TablehasHRAInformation{display:none;}#TableplansBenifits{display:none;}");

            System.out.println("CoverageStartDatePlan:--" + CoverageStartDatePlan);
            System.out.println("CoverageEndDatePlan:--" + CoverageEndDatePlan);
            System.out.println("SubbirthDate:--" + SubbirthDate);
            if (!CoverageStartDatePlan.equals("")) {
                String[] Date = CoverageStartDatePlan.split("\\-");
                try {
                    CoverageStartDatePlan = GetMonthName(Date[1]) + " " + Date[2] + ", " + Date[0];
                } catch (Exception e) {
                    System.out.println("Error in CoverageStartDatePlan: " + e.getMessage());
                }
            }
            if (!CoverageEndDatePlan.equals("")) {
                String[] Date = CoverageEndDatePlan.split("\\-");
                try {
                    CoverageEndDatePlan = GetMonthName(Date[1]) + " " + Date[2] + ", " + Date[0];
                } catch (Exception e) {
                    System.out.println("Error in CoverageStartDatePlan: " + e.getMessage());
                }
            }
            if (!SubbirthDate.equals("")) {
                SubbirthDate = SubbirthDate.substring(0, 10).replaceAll("\"", "");
                String[] Date = SubbirthDate.split("\\-");
                try {
                    SubbirthDate = GetMonthName(Date[1]) + " " + Date[2] + ", " + Date[0];
                } catch (Exception e) {
                    System.out.println("Error in Subscrober DOB: " + e.getMessage());
                }
            }


            html.append(" </style>");
            html.append("<body class=\"hold-transition light-skin sidebar-mini theme-primary\">");
            html.append("<div class=\"wrapper\">\n" +
                    "    <div class=\"container-full\">\n" +
                    "    <section class=\"content\">");
            html.append("<div class=\"row\">\n" +
                    " <div class=\"col-2\"> </div> <div class=\"col-8\">\n" +
                    " <div class=\"box\">\n" +
                    " <div class=\"box-body\">");

            html.append("<button class=\"btn btn-xs btn-info\" onclick=\"window.print()\">PRINT</button>\n");

            html.append("<div class=\"col-xl-12 col-12\">\n" +
                    "<div class=\"box\">\n" +
                    "<div class=\"box-header\">\n" +
                    "<h4 class=\"box-title\">SUBCRIBER NAME: " + SubLastName + ", " + SubFirstName + " " + SubMiddleName + "</h4>\n" +
                    "<div class=\"box-controls pull-right\">\n" +
//                    "<button class=\"badge badge-success\" href=\"#\">"+PLANStatus+"</button>\n" +
                    "<span class=\"badge badge-success\">" + PLANStatus + "</span>" +
                    "</div>\n" +
                    "</div>\n" +
                    "<div class=\"box-body\">\n" +
                    "<p style=\"color:black;\">" + "<b>MEMBER ID:</b> " + SubmemberId + "<br>" + "<b> DOB: </b> " + SubbirthDate + "<br><b>GENDER:</b> " + Subgender + "" +
                    "<br><b>PLAN / COVERAGE DATE:</b> " + CoverageStartDatePlan + " - " + CoverageEndDatePlan + "</p>\n" +
                    "</div>\n" +
                    "</div>\n" +
                    "</div>");
//            html.append(SubLastName+", "+SubFirstName+ " "+ SubMiddleName);
//            html.append("<br>");
//            html.append("MEMBER ID: "+SubmemberId);
//            html.append("<br>");
//            html.append("GENDER: "+Subgender);


            html.append(RequestedServiceTypeBuff);
            html.append(SubscriberBuff);
            html.append(PatientBuff);
            html.append(PayerBuff);
            html.append(requestingProviderBuff);
            html.append(plansBuff);
            html.append(benifitsBuff);
//            html.append(statusDetailBuff);
            html.append(supplementalInformationBuff);

//            html.append(hasHRAInformationBuff);
//            html.append(SubLastName+", "+SubFirstName+" "+SubMiddleName);
//            html.append("\n");
//            html.append("MEMBER ID: "+SubmemberId+"\n");
//            html.append("\n");
//            html.append("Gender : "+Subgender+"\n");
//            html.append("DOB : "+SubbirthDate+"\n");
//            html.append("Address: "+"\n");
//            html.append(Subline+"\n");
//            html.append(Subcity+" " +Substate+" "+SubzipCode+"\n");

            html.append("</div></div></div> <div class=\"col-2\"> </div> </div>");
            html.append("</section></div></div>");
            html.append("<script src=\"/md/main/js/vendors.min.js\"></script>\n" +
                    "<script src=\"/md/assets/icons/feather-icons/feather.min.js\"></script>\t\n" +
                    "<script src=\"/md/assets/vendor_components/datatable/datatables.min.js\"></script>\n" +
                    "<script src=\"/md/main/js/template.js\"></script>\n" +
                    "<script src=\"/md/main/js/demo.js\"></script>\n" +
                    "<script src=\"/md/main/js/pages/data-table.js\"></script>\n" +
                    "<script src=\"/md/main/js/dist/sweetalert/sweetalert.min.js\"></script>");

            html.append("<script>function OpenTable(tableid){ if(document.getElementById(tableid).style.display == 'inline'){document.getElementById(tableid).style.display = 'none';}else{document.getElementById(tableid).style.display = 'inline'}}</script>");
            html.append("</body>");
            html.append("</html>");
            // processNode(node);
//            processNode_m(node,"start");
//            html.append("</body>\r\n" + 			 		"</html>");
            //System.out.println(html);
            //out1.print(html.toString());
        } catch (Exception ee) {
            System.out.println("ERROR IN RES1 Method: " + ee.getMessage());
            String str = "";
            for (int i = 0; i < ee.getStackTrace().length; ++i) {
                str = str + ee.getStackTrace()[i] + "<br>";
            }
            System.out.println("ERROR IN RES1 Method: -->" + str);

        }
        return html.toString();
    }

    private static void processNode_m(JsonNode node, String ty) {

        if (node.isArray()) {
            // if the node is a list of items,
            //  go through all the items and process them individually
            //System.out.println("=== Array start ===");

            for (final JsonNode objInArray : node) {

                // System.out.println("--- Array element start ---");
                // process the item in the array
                System.out.println("|||||||||" + objInArray.toString());
                processNode_m(objInArray, "Arry");
                // closer=1;
                //System.out.println("--- Array element end ---");
            }

            // System.out.println("=== Array end ===");
        } else if (node.isContainerNode()) {
            // if the node is an object,
            //  go through all fields within the object
            //System.out.println("/// Object start ///");


            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {


                Map.Entry<String, JsonNode> field = it.next();
//                if(field.getKey().compareTo("id")==0) {
//                    html.append("<table class=\"table table-hover\" id=\"1stTable\">");
//                    //html.append("<table>");ffff
//                    System.out.println("key: " + field.getKey());
//                    html.append("<td align=left>"+field.getKey()+"</td>\n");
//                    processNode_C(field.getValue(),"object");
//                }else if(field.getKey().compareTo("customerId")==0) {
//                    System.out.println("key: " + field.getKey());
//                    html.append("<td align=left>"+field.getKey()+"</td>\n");
//                    processNode_C(field.getValue(),"object");
//                }else if(field.getKey().compareTo("controlNumber")==0) {
//                    System.out.println("key: " + field.getKey());
//                    html.append("<td align=left>"+field.getKey()+"</td>\n");
//                    processNode_C(field.getValue(),"object");
//                }else if(field.getKey().compareTo("createdDate")==0) {
//                    System.out.println("key: " + field.getKey());
//                    html.append("<td align=left>"+field.getKey()+"</td>\n");
//                    processNode_C(field.getValue(),"object");
//                }else if(field.getKey().compareTo("expirationDate")==0) {
//                    System.out.println("key: " + field.getKey());
//                    html.append("<td align=left>"+field.getKey()+"</td>\n");
//                    processNode_C(field.getValue(),"object");
//                }else if(field.getKey().compareTo("asOfDate")==0) {
//                    System.out.println("key: " + field.getKey());
//                    html.append("<td align=left>"+field.getKey()+"</td>\n");
//                    processNode_C(field.getValue(),"object");
////                    html.append("</table>");
//                    html.append("</table></div><hr style=\"border-top: dotted 1px;\" />");
//                }
//
//                else
                if (field.getKey().compareTo("requestedServiceType") == 0) {
                    RequestedServiceTypeBuff.setLength(0);
                    RequestedServiceTypeBuff.append("<h3 onclick=\"OpenTable('TableRST')\">" + field.getKey().toUpperCase() + "</h3>\n");
                    processNode_1Array(field.getValue(), "TableRST", RequestedServiceTypeBuff);
                } else if (field.getKey().compareTo("subscriber") == 0) {

                    SubscriberBuff.setLength(0);
                    SubscriberBuff.append("<h3 onclick=\"OpenTable('TableSubscriber')\">" + field.getKey().toUpperCase() + "</h3>\n");
                    processSubscriberNode(field.getValue(), "SubInfo");
                    processNode_1Array(field.getValue(), "TableSubscriber", SubscriberBuff);
                } else if (field.getKey().compareTo("patient") == 0) {
                    PatientBuff.setLength(0);
                    PatientBuff.append("<h3 onclick=\"OpenTable('TablePatient')\">" + field.getKey().toUpperCase() + "</h3>\n");
                    processNode_1Array(field.getValue(), "TablePatient", PatientBuff);
                } else if (field.getKey().compareTo("payer") == 0) {
                    PayerBuff.setLength(0);
                    PayerBuff.append("<h3 onclick=\"OpenTable('TablePayer')\">" + field.getKey().toUpperCase() + "</h3>\n");
                    processNode_1Array(field.getValue(), "TablePayer", PayerBuff);
                } else if (field.getKey().compareTo("requestingProvider") == 0) {
                    requestingProviderBuff.setLength(0);
                    requestingProviderBuff.append("<h3 onclick=\"OpenTable('TablerequestingProvider')\">" + field.getKey().toUpperCase() + "</h3>\n");
                    processNode_1Array(field.getValue(), "TablerequestingProvider", requestingProviderBuff);
                } else if (field.getKey().compareTo("plans") == 0) {
                    plansBuff.setLength(0);
                    plansBuff.append("<h3 onclick=\"OpenTable('Tableplans')\">" + field.getKey().toUpperCase() + "</h3>\n");
                    processNode_1Plans(field.getValue(), "Tableplans", plansBuff);
                } else if (field.getKey().compareTo("supplementalInformation") == 0) {
                    supplementalInformationBuff.setLength(0);
                    supplementalInformationBuff.append("<h3 onclick=\"OpenTable('TablesupplementalInformation')\">" + field.getKey().toUpperCase() + "</h3>\n");
                    processNode_1Array(field.getValue(), "TablesupplementalInformation", supplementalInformationBuff);
                } else if (field.getKey().compareTo("hasHRAInformation") == 0) {
                    hasHRAInformationBuff.setLength(0);
                    hasHRAInformationBuff.append("<h3 onclick=\"OpenTable('TablehasHRAInformation')\">" + field.getKey().toUpperCase() + "</h3>\n");
                    processNode_1Array(field.getValue(), "TablehasHRAInformation", hasHRAInformationBuff);
                }
            }

            //System.out.println("/// Object end ///");
            // html.append("</table>\n");


        } else {
            // if node is a simple value (like string or int) so let's print it
            //html.append("<td align=left>"+node+"</td>\n");
            //System.out.println("value: " + node);
            //html.append("</tr>\n");
        }
    }


    private static void processNode_1Array(JsonNode node, String TableName, StringBuilder BuffName) {
        if (node.isArray()) {
            // if the node is a list of items,
            //  go through all the items and process them individually
            //System.out.println("=== Array start ===");

            for (final JsonNode objInArray : node) {
                processNode_1Array(objInArray, TableName, BuffName);
            }

        } else if (node.isContainerNode()) {

            Iterator<Map.Entry<String, JsonNode>> it = node.fields();

            BuffName.append("<div id=\"" + TableName + "\" class=\"table-responsive\" >");
            BuffName.append("<table class=\"table table-hover\">");

            while (it.hasNext()) {
                Map.Entry<String, JsonNode> field = it.next();

                if (field.getKey().compareTo("address") == 0) {
                    BuffName.append("<tr><td><h4>" + field.getKey().toUpperCase() + "</h4></td><td></td></tr>");
                    processNode_1Address(field.getValue(), TableName + "Address", BuffName);
                    continue;
                }

//                if(field.getKey().compareTo("benefits") == 0){
////                    BuffName.append("<tr><td><h4>"+field.getKey().toUpperCase()+"</h4></td><td></td></tr>");
//                    //benifitsBuff.setLength(0);
////                    benifitsBuff.append("<h3 onclick=\"OpenTable('"+TableName+"Benifits')\">BENIFITS AND COVERAGE DETAILS</h3>");
//                    //BuffName.append("<tr><td><h4 >"+field.getKey().toUpperCase()+"</h4></td><td></td></tr>");
//
//                    //processNode_Benifits(field.getValue(),TableName+"Benifits", BuffName);
//                    continue;
//                }

                BuffName.append("<tr>");
                BuffName.append("<td>" + field.getKey().toUpperCase() + "</td>");
                BuffName.append("<td>" + field.getValue() + "</td>");
                BuffName.append("</tr>");


            }
            //
            BuffName.append("</table></div>");

        } else {

//            System.out.println("VALUE REQUESTSERVICETYPE: : " + node);
        }

    }

    private static void processNode_1Plans(JsonNode node, String TableName, StringBuilder BuffName) {

        if (node.isArray()) {
            // if the node is a list of items,
            //  go through all the items and process them individually
            //System.out.println("=== Array start ===");

            for (final JsonNode objInArray : node) {
                processNode_1Plans(objInArray, TableName, BuffName);
            }

        } else if (node.isContainerNode()) {

            Iterator<Map.Entry<String, JsonNode>> it = node.fields();

//            BuffName.append("<div id=\""+TableName+"\" class=\"table-responsive\" >");
//            BuffName.append("<table class=\"table table-hover\">");

            BuffName.append("<div class=\"col-xl-12 col-12\" id='" + TableName + "'>\n" +
                    "<div class=\"box\">\n" +
                    "<div class=\"box-header\">\n");
            if (it.hasNext()) {
                Map.Entry<String, JsonNode> field = it.next();
                if (field.getKey().compareTo("status") == 0) {
                    BuffName.append("<h4 class=\"box-title\">" + field.getKey().toUpperCase() + "</h4> <div class=\"box-controls pull-right\">\n");
                    BuffName.append("<button class=\"btn btn-xs btn-success\" href=\"#\">" + field.getValue().toString().replaceAll("\"", "") + "</button>");
                    PLANStatus = field.getValue().toString().replaceAll("\"", "");
                }
            }
            BuffName.append("</div>\n" +
                    "</div>\n" +
                    "<div class=\"box-body\">\n");


            while (it.hasNext()) {
                Map.Entry<String, JsonNode> field = it.next();

                if (field.getKey().compareTo("benefits") == 0) {
                    benifitsBuff.setLength(0);
                    benifitsBuff.append("<h3 onclick=\"OpenTable('" + TableName + "Benifits')\">BENIFITS AND COVERAGE DETAILS</h3>\n");
                    benifitsBuff.append("<div id='" + TableName + "Benifits'>");
                    processNode_Benifits(field.getValue(), TableName + "Benifits", benifitsBuff);
                    benifitsBuff.append("</div>");
                    continue;
                }
                if (field.getKey().compareTo("groupNumber") == 0) {
                    GroupNumPlan = field.getValue().toString().replaceAll("\"", "");
                }
                if (field.getKey().compareTo("groupName") == 0) {
                    GroupNamePlan = field.getValue().toString().replaceAll("\"", "");
                }
                if (field.getKey().compareTo("coverageStartDate") == 0) {
                    CoverageStartDatePlan = field.getValue().toString().substring(0, 11).replaceAll("\"", "");

                }
                if (field.getKey().compareTo("coverageEndDate") == 0) {
                    CoverageEndDatePlan = field.getValue().toString().substring(0, 11).replaceAll("\"", "");
                }
                if (field.getKey().compareTo("insuranceType") == 0) {
                    insuranceTypePlan = field.getValue().toString().replaceAll("\"", "");
                }
                if (field.getKey().compareTo("payerNotes") == 0) {
                    payerNotesPlan = processNodePlanPayerNotes(field.getValue());
                }


            }
            BuffName.append("<p style=\"color:black\"><b>GROUP NUMBER:</b> " + GroupNumPlan + " <br> <b>GROUP NAME:</b> " + GroupNamePlan + " <br> " +
                    "<b>PLAN/COVERAGE DATE:</b> " + CoverageStartDatePlan + " - " + CoverageEndDatePlan + " <br> " +
                    "<b>INSURANCE TYPE:</b> " + insuranceTypePlan + " <br> <b>PAYER NOTES:</b> " + payerNotesPlan + "</p>\n");
            BuffName.append("</div></div></div>");
            //
//            BuffName.append("</table></div>");

        } else {

//            System.out.println("VALUE REQUESTSERVICETYPE: : " + node);
        }

    }

    private static void processNode_1Address(JsonNode node, String TableName, StringBuilder BuffName) {
        if (node.isArray()) {
            for (final JsonNode objInArray : node) {

                processNode_1Array(objInArray, TableName, BuffName);
            }
        } else if (node.isContainerNode()) {

            Iterator<Map.Entry<String, JsonNode>> it = node.fields();

            while (it.hasNext()) {
                Map.Entry<String, JsonNode> field = it.next();

                BuffName.append("<tr>");
                BuffName.append("<td>" + field.getKey().toUpperCase() + "</td>");
                BuffName.append("<td>" + field.getValue() + "</td>");
                BuffName.append("</tr>");
            }
        } else {

        }
    }

    private static String processNodePlanPayerNotes(JsonNode node) {
        payerNotesPlan = "";
        if (node.isArray()) {
            for (final JsonNode objInArray : node) {

                processNodePlanPayerNotes(objInArray);
            }
        } else if (node.isContainerNode()) {

            Iterator<Map.Entry<String, JsonNode>> it = node.fields();

            while (it.hasNext()) {
                Map.Entry<String, JsonNode> field = it.next();

                if (field.getKey().compareTo("type") == 0) {
                    payerNotesPlan += "TYPE: " + field.getValue().toString().replaceAll("\"", "").toUpperCase();
                }
                if (field.getKey().compareTo("message") == 0) {
                    payerNotesPlan += "  <br> <b>MESSAGE:</b> " + field.getValue().toString().replaceAll("\"", "");
                }
            }
        } else {
            payerNotesPlan += node.toString().replaceAll("\"", "");
        }
        return payerNotesPlan;
    }

    private static void processSubscriberNode(JsonNode node, String ty) {
        Iterator<Map.Entry<String, JsonNode>> it = node.fields();
//        subscriber.append("<table class=\"table table-hover\" id=\"2ndTable\">");
        while (it.hasNext()) {
//            subscriber.append("<tr>");
            Map.Entry<String, JsonNode> field = it.next();

            if (field.getKey().compareTo("firstName") == 0) {
                SubFirstName = field.getValue().toString().replace("\"", "");
            }
            if (field.getKey().compareTo("middleName") == 0) {
                SubMiddleName = field.getValue().toString().replace("\"", "");
            }
            if (field.getKey().compareTo("lastName") == 0) {
                SubLastName = field.getValue().toString().replace("\"", "");
            }
            if (field.getKey().compareTo("memberId") == 0) {
                SubmemberId = field.getValue().toString().replace("\"", "");
            }
            if (field.getKey().compareTo("gender") == 0) {
                Subgender = field.getValue().toString().replace("\"", "");
            }
            if (field.getKey().compareTo("birthDate") == 0) {
                SubbirthDate = field.getValue().toString().replace("\"", "");
            }
            if (field.getKey().compareTo("address") == 0) {
                processSubscriberNode(field.getValue(), "addressNode");
                Subaddress = field.getValue().toString();
            }
            if (field.getKey().compareTo("line1") == 0) {
                Subline = field.getValue().toString().replace("\"", "");
            }
            if (field.getKey().compareTo("city") == 0) {
                Subcity = field.getValue().toString().replace("\"", "");
            }
            if (field.getKey().compareTo("stateCode") == 0) {
                Substate = field.getValue().toString().replace("\"", "");
            }
            if (field.getKey().compareTo("zipCode") == 0) {
                SubzipCode = field.getValue().toString().replace("\"", "");
            }

            //System.out.println("FirstNAme---: "+FirstName);
//            subscriber.append("<td >"+ field.getKey().toUpperCase()+"</td>");
//            subscriber.append("<td >"+ field.getValue()+"</td></tr>");
        }
//        subscriber.append("</table>");

    }

    private static void processNode_Benifits(JsonNode node, String TableName, StringBuilder BuffName) {
        if (node.isArray()) {
            for (final JsonNode objInArray : node) {

                processNode_Benifits(objInArray, TableName, BuffName);
            }
        } else if (node.isContainerNode()) {

            Iterator<Map.Entry<String, JsonNode>> it = node.fields();

//            BuffName.append("<div id=\""+TableName+"\" class=\"table-responsive\" >");
//            BuffName.append("<table class=\"table table-hover\">");
//            BuffName.append("<table class=\"table table-hover\" id="+TableName+">");
            BuffName.append("<div class=\"col-xl-12 col-12\" >\n" +
                    "<div class=\"box\">\n" +
                    "<div class=\"box-header\">\n");
            if (it.hasNext()) {
                Map.Entry<String, JsonNode> field = it.next();
                if (field.getKey().compareTo("name") == 0) {
                    BuffName.append("<h4 class=\"box-title\">" + field.getValue().toString().toUpperCase().replaceAll("\"", ""));
                }
            }
            if (it.hasNext()) {
                Map.Entry<String, JsonNode> field = it.next();
                if (field.getKey().compareTo("type") == 0) {
                    BuffName.append("   -  " + field.getValue().toString().replaceAll("\"", "") + "</h4>\n");
                }
            }
            BuffName.append("<div class=\"box-controls pull-right\"><button class=\"btn btn-xs btn-success\" href=\"#\"></button></div>");
            BuffName.append("</div>\n" +
                    "<div class=\"box-body\"><p style=\"color:black\">\n");


            while (it.hasNext()) {
                Map.Entry<String, JsonNode> field = it.next();

                if (field.getKey().compareTo("statusDetails") == 0) {
                    BuffName.append("<tr><td><h4>" + field.getKey().toUpperCase() + "</h4></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetails", BuffName);
                    continue;
                }

                if (field.getKey().compareTo("amounts") == 0) {
                    BuffName.append("<tr><td><h4>" + field.getKey().toUpperCase() + "</h4></td><td></td></tr>");
                    processNodeStatusDetailsamount(field.getValue(), TableName + i + "StatusDetailsamounts", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("benefitDescriptions") == 0) {
                    BuffName.append("<tr><td><h4>" + field.getKey().toUpperCase() + "</h4></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetails", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("payerNotes") == 0) {
                    BuffName.append("<tr><td><h4>" + field.getKey().toUpperCase() + "</h4></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetails", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("nonCovered") == 0) {
                    BuffName.append("<tr><td><h4>" + field.getKey().toUpperCase() + "</h4></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetails", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("outOfNetwork") == 0) {
                    BuffName.append("<tr><td><h4>" + field.getKey().toUpperCase() + "</h4></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetails", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("inNetwork") == 0) {
                    BuffName.append("<tr><td><h4>" + field.getKey().toUpperCase() + "</h4></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetails", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("limitations") == 0) {
                    BuffName.append("<tr><td><h4>" + field.getKey().toUpperCase() + "</h4></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetails", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("unlimited") == 0) {
                    BuffName.append("<tr><td><h4>" + field.getKey().toUpperCase() + "</h4></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetails", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("contacts") == 0) {
                    BuffName.append("<tr><td><h4>" + field.getKey().toUpperCase() + "</h4></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetails", BuffName);
                    continue;
                }
                BuffName.append("<font color=\"black\">" + field.getKey().toUpperCase() + " :  " + field.getValue().toString().replace("\"", "") + "</font><br><hr>");
//                BuffName.append("<tr>");
//                BuffName.append("<td>"+field.getKey().toUpperCase()+"</td>");
//                BuffName.append("<td>"+field.getValue()+"</td>");
//                BuffName.append("</tr>");
            }

            BuffName.append("</p></div></div></div>");
//            BuffName.append("</table>");
            //i = i+1;
//            BuffName.append("</table></div>");

        } else {

        }
    }

    private static void processNodeStatusDetails(JsonNode node, String TableName, StringBuilder BuffName) {
        if (node.isArray()) {
            for (final JsonNode objInArray : node) {

                processNodeStatusDetails(objInArray, TableName, BuffName);
            }
        } else if (node.isContainerNode()) {

            Iterator<Map.Entry<String, JsonNode>> it = node.fields();

//            BuffName.append("<div id=\""+TableName+"\" class=\"table-responsive\" >");
//            BuffName.append("<table class=\"table table-hover\">");


            while (it.hasNext()) {
                Map.Entry<String, JsonNode> field = it.next();

                if (field.getKey().compareTo("noNetwork") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetailsnoNetwork", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("outOfPocket") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("notApplicableNetwork") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("nonCovered") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("limitations") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("unlimited") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("inNetwork") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("outOfNetwork") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetails(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }


                BuffName.append("<font color=\"black\">" + field.getKey().toUpperCase() + " :  " + field.getValue().toString().replace("\"", "") + "</font><br>");

//                BuffName.append("<tr>");
//                BuffName.append("<td>"+field.getKey().toUpperCase()+"</td>");
//                BuffName.append("<td>"+field.getValue()+"</td>");
//                BuffName.append("</tr>");
            }
            BuffName.append("<hr>");

//            BuffName.append("</table></div>");

        } else {

        }
    }

    private static void processNodeStatusDetailsamount(JsonNode node, String TableName, StringBuilder BuffName) {
        if (node.isArray()) {
            for (final JsonNode objInArray : node) {

                processNodeStatusDetailsamount(objInArray, TableName, BuffName);
            }
        } else if (node.isContainerNode()) {

            Iterator<Map.Entry<String, JsonNode>> it = node.fields();

            while (it.hasNext()) {
                Map.Entry<String, JsonNode> field = it.next();

                if (field.getKey().compareTo("noNetwork") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetailsamount(field.getValue(), TableName + "StatusDetailsnoNetwork", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("outOfPocket") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetailsamount(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("outOfNetwork") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetailsamount(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("notApplicableNetwork") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetailsamount(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("inNetwork") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetailsamount(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("coPayment") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetailsamount(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("deductibles") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetailsamount(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("coInsurance") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetailsamount(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }
                if (field.getKey().compareTo("nonCovered") == 0) {
                    BuffName.append("<tr><td><h5>" + field.getKey().toUpperCase() + "</h5></td><td></td></tr>");
                    processNodeStatusDetailsamount(field.getValue(), TableName + "StatusDetailsoutOfPocket", BuffName);
                    continue;
                }
//                if(field.getKey().compareTo("payerNotes") == 0){
//                    //BuffName.append("<tr><td><h5>"+field.getKey().toUpperCase()+"</h5></td><td></td></tr>");
//                    processNodeStatusDetailsamount(field.getValue(),TableName+"StatusDetailsoutOfPocket", BuffName);
//                    continue;
//                }


                BuffName.append("<font color=\"black\">" + field.getKey().toUpperCase() + " :  " + field.getValue().toString().replace("\"", "") + "</font><br>");

            }
            BuffName.append("<hr>");

        } else {

        }
    }


    private static void processNode_C(JsonNode node, String ty) {
        if (node.isArray()) {
            for (final JsonNode objInArray : node) {
                System.out.println("|||||||||" + objInArray.toString());
                processNode_C(objInArray, "Arry");
            }

        } else if (node.isContainerNode()) {
            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {


                Map.Entry<String, JsonNode> field = it.next();

                if (field.getKey().compareTo("primaryCareProvider") == 0) {
                    System.out.println("key222222222222222222222222222222: " + field.getKey());
                    //html.append("<h3>"+field.getKey().toUpperCase()+"</h3>\n");

                    processNode_C(field.getValue(), "object");
                } else if (field.getKey().compareTo("benefits") == 0) {
                    System.out.println("key222222222222222222222222222222: " + field.getKey());
                    //html.append("<h3>"+field.getKey().toUpperCase()+"</h3>\n");

                    processNode_C(field.getValue(), "object");
                } else {
                    System.out.println("key: " + field.getKey());
                    //html.append("<td align=left>"+field.getKey().toUpperCase()+"</td>\n");


                    // html.append("<td align=left>"+field.getValue()+"</td>\n");
                    //process every field in the array
                    //System.out.println();
                    processNode_C(field.getValue(), "object");
                }
            }

            //System.out.println("/// Object end ///");
//            html.append("</table></div><hr style=\"border-top: dotted 1px;\" />\n");


        } else {
            // if node is a simple value (like string or int) so let's print it
//            html.append("<td align=left>"+node+"</td>\n");
//            //System.out.println("value: " + node);
//            html.append("</tr>\n");
        }

    }


    private static void processNode_plan(JsonNode node, String ty) {
        if (node.isArray()) {
            // if the node is a list of items,
            //  go through all the items and process them individually
            //System.out.println("=== Array start ===");

            for (final JsonNode objInArray : node) {

                // System.out.println("--- Array element start ---");
                // process the item in the array
                System.out.println("|||||||||" + objInArray.toString());
                processNode_plan(objInArray, "Arry");
                // closer=1;
                //System.out.println("--- Array element end ---");
            }

            // System.out.println("=== Array end ===");
        } else if (node.isContainerNode()) {
            // if the node is an object,
            //  go through all fields within the object
            //System.out.println("/// Object start ///");

//            html.append("<table>\n");
            html.append("<div class=\"table-responsive\">\n");
            html.append("<table class=\"table table-hover\" id=" + ty + ">");
            html.append("<tr>\n");
            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {


                Map.Entry<String, JsonNode> field = it.next();


                if (field.getKey().compareTo("primaryCareProvider") == 0) {
                    System.out.println("key222222222222222222222222222222: " + field.getKey());
                    html.append("<h3>" + field.getKey().toUpperCase() + "</h3>\n");

                    processNode_C(field.getValue(), "object");
                } else if (field.getKey().compareTo("benefits") == 0) {
                    System.out.println("key222222222222222222222222222222: " + field.getKey());
                    html.append("<h3>" + field.getKey().toUpperCase() + "</h3>\n");

                    // processNode_plan(field.getValue(),"object");
                } else if (field.getKey().compareTo("statusDetails") == 0) {
                    System.out.println("key222222222222222222222222222222: " + field.getKey());
                    html.append("<h3>" + field.getKey().toUpperCase() + "</h3>\n");

                    processNode_plan(field.getValue(), "object");
                } else {
                    System.out.println("key: " + field.getKey());
                    html.append("<td align=left>" + field.getKey().toUpperCase() + "</td>\n");


                    // html.append("<td align=left>"+field.getValue()+"</td>\n");
                    //process every field in the array
                    //System.out.println();
                    processNode_plan(field.getValue(), "object");
                }
            }

            //System.out.println("/// Object end ///");
            html.append("</table></div><hr style=\"border-top: dotted 1px;\" />\n");


        } else {
            // if node is a simple value (like string or int) so let's print it
            html.append("<td align=left>" + node + "</td>\n");
            //System.out.println("value: " + node);
            html.append("</tr>\n");
        }

    }

    private static void processNode(JsonNode node) {

        if (node.isArray()) {
            // if the node is a list of items,
            //  go through all the items and process them individually
            System.out.println("=== Array start ===");

            for (final JsonNode objInArray : node) {

                System.out.println("--- Array element start ---");
                // process the item in the array

                processNode(objInArray);
                // closer=1;
                System.out.println("--- Array element end ---");
            }

            System.out.println("=== Array end ===");
        } else if (node.isContainerNode()) {
            // if the node is an object,
            //  go through all fields within the object
            System.out.println("/// Object start ///");


            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {


                Map.Entry<String, JsonNode> field = it.next();

                System.out.println("key: " + field.getKey());
                html.append("<h1> " + field.getKey().toUpperCase() + "</h1>");


                //process every field in the array
                processNode_l2(field.getValue());
            }

            System.out.println("/// Object end ///");


        } else {
            // if node is a simple value (like string or int) so let's print it
            html.append("<td align=left>" + node + "</td>\n");
            System.out.println("value: " + node);
            html.append("</tr>\n");
        }
    }

    private static void processNode_l2(JsonNode node) {

        if (node.isArray()) {
            // if the node is a list of items,
            //  go through all the items and process them individually
            System.out.println("=== Array start ===");

            for (final JsonNode objInArray : node) {

                System.out.println("--- Array element start ---");
                // process the item in the array

                processNode_l2(objInArray);
                //
                System.out.println("--- Array element end ---");
            }

            System.out.println("=== Array end ===");
        } else if (node.isContainerNode()) {
            // if the node is an object,
            //  go through all fields within the object
            System.out.println("/// Object start ///");


//            html.append("<table>\n");
            html.append("<div class=\"table-responsive\">\n");
            html.append("<table class=\"table table-hover\" id=\"processNode_12\">");

            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {

                html.append("<tr class=\"Inner\">\n");

                Map.Entry<String, JsonNode> field = it.next();


                if (closer == 1) {
                    System.out.println("key: " + field.getKey());
                    html.append("<h3>" + field.getKey().toUpperCase() + "<h3>\n");
                    closer = 0;
                } else {
                    System.out.println("key: " + field.getKey());
                    html.append("<td align=left>" + field.getKey().toUpperCase() + "</td>\n");
                }
                //process every field in the array
                processNode_l2(field.getValue());
            }
            html.append("</table><div>\n");
            System.out.println("/// Object end ///");
            closer = 1;


        } else {
            // if node is a simple value (like string or int) so let's print it
            html.append("<td align=left>" + node + "</td>\n");
            System.out.println("value: " + node);
            html.append("</tr>\n");
        }
    }

    private static String GetMonthName(String MonthNumber) {
        String MonthName = "";
        //String[] Months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        if (MonthNumber.length() == 1) {
            MonthNumber = "0" + MonthNumber;
        }
        switch (MonthNumber) {
            case "01":
                MonthName = "Jan";
                break;
            case "02":
                MonthName = "Feb";
                break;
            case "03":
                MonthName = "Mar";
                break;
            case "04":
                MonthName = "Apr";
                break;
            case "05":
                MonthName = "May";
                break;
            case "06":
                MonthName = "Jun";
                break;
            case "07":
                MonthName = "Jul";
                break;
            case "08":
                MonthName = "Aug";
                break;
            case "09":
                MonthName = "Sep";
                break;
            case "10":
                MonthName = "Oct";
                break;
            case "11":
                MonthName = "Nov";
                break;
            case "12":
                MonthName = "Dec";
                break;
            default:
                MonthName = "";
                break;
        }

        return MonthName;
    }
}
