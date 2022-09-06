package md;

import org.w3c.dom.CharacterData;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;

@SuppressWarnings("Duplicates")
public class eiresponseTab {
    public static String emsg = "";
    public static String msg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static StringBuffer CDRList = new StringBuffer();
    public static StringBuffer patientname = new StringBuffer();
    public static StringBuffer relationship = new StringBuffer();
    public static StringBuffer subscriberaddinfo = new StringBuffer();
    public static StringBuffer trace_number = new StringBuffer();
    public static StringBuffer otherinfo = new StringBuffer();
    static int benefittable = 1;
    public static StringBuffer trace_id = new StringBuffer();
    public static String policystatus = "";
    public static String planinfo = "";
    public static String validtill = "";
    public static String traceNo = "";

    public static StringBuffer finaloutput = new StringBuffer();

    public static void main(String[] args) {

        try {


            File inputFile = new File("D:\\nw.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);

            doc.getDocumentElement().normalize();


            String restVal = "-";
            NodeList restElmLst = doc.getElementsByTagName("SuccessCode");

            if (restElmLst.getLength() > 0) {
                // restVal = doc.getElementsByTagName("SuccessCode").item(0).getTextContent();

                if (doc.getElementsByTagName("SuccessCode").item(0).getTextContent().compareTo("Success") == 0) {

                    System.out.println("dddd" + restVal);
                    System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

                    System.out.println(doc.getElementsByTagName("SuccessCode").item(0).getNodeName());

                    System.out.println(doc.getElementsByTagName("SuccessCode").item(0).getTextContent());
                    System.out.println("----------------------------");


                    System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                    NodeList nList = doc.getElementsByTagName("ResponseAsXml");

                    System.out.println("----------------------------");
                    emsg = "";
                    msg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                    NodeList nodes = doc.getElementsByTagName("DoInquiryResult");
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Element element = (Element) nodes.item(i);
                        NodeList title = element.getElementsByTagName("ResponseAsXml");
                        Element line = (Element) title.item(0);

                        System.out.println("Title: " + getCharacterDataFromElement(line));
                        emsg = getCharacterDataFromElement(line);
                    }
                    System.out.println(emsg.trim().length());
                    if (emsg.trim().length() == 0) {

                        System.out.println("|||||||||||zero");

                    }
                    msg = msg + emsg;

                    System.out.println(msg);



/*
                 //File inputFile = new File("D:\\xmlFile1.xml");
                 DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
                 DocumentBuilder dBuilder2 = dbFactory2.newDocumentBuilder();
                 Document doc2 = dBuilder2.parse((new InputSource(new StringReader(msg))));
                 //  Document doc2 = dBuilder2.parse(inputFile);
                 doc2.getDocumentElement().normalize();
*/
                    DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder2 = dbFactory2.newDocumentBuilder();
                    Document doc2 = null;
                    System.out.println("llll" + emsg.trim());
                    //  msg=msg+emsg;
                    if (emsg.trim().length() != 0) {

                        //File inputFile = new File("D:\\xmlFile1.xml");
                        System.out.println("########### " + msg.trim());
                        doc2 = dBuilder2.parse((new InputSource(new StringReader(msg))));
                        doc2.getDocumentElement().normalize();
                    } else {
                        doc2 = doc;
                        System.out.println("|||||||||||zero");
                    }

                    NodeList list = doc2.getElementsByTagName("dependent");
                    for (int i = 0; i < list.getLength(); i++) {
                        // visitChildNodes(list);
                        Node nNode = list.item(i);
                        if (nNode.getNodeType() == 1) {
                            Element eElement = (Element) nNode;


                            //out.println("--------------AIR_ITM--------------");
                            NodeList benefit = eElement.getElementsByTagName("benefit");
                            visitChildNodes(benefit);

                            NodeList patientname_n = eElement.getElementsByTagName("patientname");
                            visitChildNodes(patientname_n);
                            patientname.append("<tr class=\"Inner\">\n");
                            patientname.append("<td align=left>" + eElement.getElementsByTagName("sex").item(0).getNodeName() + "</td>\n");
                            patientname.append("<td align=left>" + eElement.getElementsByTagName("sex").item(0).getTextContent() + "</td>\n");
                            patientname.append("</tr>\n");
                            patientname.append("<tr class=\"Inner\">\n");
                            patientname.append("<td align=left>" + eElement.getElementsByTagName("date-of-birth").item(0).getNodeName() + "</td>\n");
                            patientname.append("<td align=left>" + eElement.getElementsByTagName("date-of-birth").item(0).getTextContent() + "</td>\n");
                            patientname.append("</tr>\n");
                            patientname.append("</table>\n");
                            patientname.append("\n");


                            NodeList relationship = eElement.getElementsByTagName("relationship");
                            visitChildNodes(relationship);

                            NodeList trace_number = eElement.getElementsByTagName("trace_number");
                            visitChildNodes(trace_number);


                            NodeList date_plan = eElement.getElementsByTagName("date");
                            visitChildNodes(date_plan);


                            NodeList subscriberaddinfo = eElement.getElementsByTagName("subscriberaddinfo");
                            visitChildNodes(subscriberaddinfo);

                            NodeList trace_id = eElement.getElementsByTagName("trace_id");
                            visitChildNodes(trace_id);


                        }
                    }


                    NodeList list2 = doc2.getElementsByTagName("subscriber");
                    for (int i = 0; i < list2.getLength(); i++) {
                        // visitChildNodes(list);
                        Node nNode2 = list2.item(i);
                        if (nNode2.getNodeType() == 1) {
                            Element eElement = (Element) nNode2;

                            NodeList patientname_n = eElement.getElementsByTagName("patientname");
                            visitChildNodes(patientname_n);
                            patientname.append("<tr class=\"Inner\">\n");
                            //   patientname.append("<td align=left>" +eElement.getElementsByTagName("sex").item(0).getNodeName() + "</td>\n");
                            // patientname.append("<td align=left>" +eElement.getElementsByTagName("sex").item(0).getTextContent() + "</td>\n");
                            patientname.append("</tr>\n");
                            patientname.append("<tr class=\"Inner\">\n");
                            // patientname.append("<td align=left>" +eElement.getElementsByTagName("date-of-birth").item(0).getNodeName() + "</td>\n");
                            // patientname.append("<td align=left>" +eElement.getElementsByTagName("date-of-birth").item(0).getTextContent() + "</td>\n");
                            patientname.append("</tr>\n");
                            patientname.append("</table>\n");
                            patientname.append("\n");

                            NodeList subscribername = eElement.getElementsByTagName("subscribername");
                            if (subscribername.getLength() > 0) {
                                // patientname.append("<h2>Subscriber</h2>");
                                //patientname.append("<h2 onclick='myFunction(\"PR01\");' >Patient relationship</h2>");
                                //patientname.append("<table id='PR01'>\n");
                                patientname.append("<h2 class=\"accordion\" onclick='myFunction(\"SI01\");'>Subscriber Info</h2>");
                                patientname.append("<table id='SI01'>\n");
                                visitChildNodes_sub(subscribername);
                                patientname.append("</table>\n");
                                patientname.append("\n");
                            }


                            NodeList relationship = eElement.getElementsByTagName("relationship");
                            if (relationship.getLength() > 0) {
                                patientname.append("<h2 class=\"accordion\" onclick='myFunction(\"RI02\");'>Relationship to Insured</h2>");
                                patientname.append("<table id='RI02' >\n");
                                visitChildNodes_sub(relationship);
                                patientname.append("</table>\n");
                                patientname.append("\n");
                            }

                            NodeList rejection = eElement.getElementsByTagName("rejection");
                            if (rejection.getLength() > 0) {
                                patientname.append("<h2 class=\"accordion\" onclick='myFunction(\"RJ02\"); >rejection</h2>");
                                patientname.append("<table id='RJ02'>\n");
                                //  NodeList relationship = eElement.getElementsByTagName("relationship");
                                visitChildNodes_sub(rejection);
                                patientname.append("</table>\n");
                                patientname.append("\n");
                            }


                            NodeList benefit = eElement.getElementsByTagName("benefit");
                            visitChildNodes(benefit);
                         /*patientname.append("<h2>subscriberid</h2>");
                         patientname.append("<table>\n");

                         patientname.append("<tr class=\"Inner\">\n");
                         patientname.append("<td align=left>" +eElement.getElementsByTagName("subscriberid").item(0).getNodeName() + "</td>\n");
                         patientname.append("<td align=left>" +eElement.getElementsByTagName("subscriberid").item(0).getTextContent() + "</td>\n");
                         patientname.append("</tr>\n");
                         patientname.append("</table>\n");
                         patientname.append("<BR><BR><BR>\n");*/
                            NodeList SUBI = eElement.getElementsByTagName("subscriberid");
                            if (SUBI.getLength() > 0) {
                                // patientname.append("<h2>subscriberid</h2>");
                                // 	patientname.append("<table>\n");

                                otherinfo.append("<tr class=\"Inner\">\n");
                                otherinfo.append("<td align=left>" + eElement.getElementsByTagName("subscriberid").item(0).getNodeName() + "</td>\n");
                                otherinfo.append("<td align=left>" + eElement.getElementsByTagName("subscriberid").item(0).getTextContent() + "</td>\n");
                                otherinfo.append("</tr>\n");
                                otherinfo.append("</table>\n");
                                otherinfo.append("\n");

                            } else {
                                otherinfo.append("</table>\n");
                                otherinfo.append("\n");
                            }


                        }
                    }


                    patientname.append("<h2 class=\"accordion\" onclick='myFunction(\"INFO01\");'>infosource</h2>");
                    patientname.append("<table id='INFO01'>\n");
                    NodeList list3 = doc2.getElementsByTagName("infosource");
                    visitChildNodes_sub(list3);
                    patientname.append("</table>\n");
                    patientname.append("\n");

                    patientname.append("<h2 class=\"accordion\" onclick='myFunction(\"INFOR01\");'>inforeceiver</h2>");
                    patientname.append("<table id='INFOR01'>\n");
                    NodeList list4 = doc2.getElementsByTagName("inforeceiver");
                    visitChildNodes_sub(list4);
                    patientname.append("</table>\n");
                    patientname.append("\n");


                    //System.out.println(CDRList.toString());
                    StringBuffer style = new StringBuffer();
                    PrintWriter out = new PrintWriter(new File("d:\\xmlFile2223.html"));
                    style.append("<head><meta http-equiv=\"Content-type\" content=\"text/html;charset=UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n");
                    style.append("<link href=\"eiblue.css\" rel=\"stylesheet\" type=\"text/css\" />");

                    // style.append("<style>\ntable {  font-family: arial, sans-serif;  border-collapse: collapse;  width: 100%;} \n");
                    // style.append("td, th {  border: 1px solid #dddddd;  text-align: left;  padding: 8px;}\n");
                    // style.append(".accordion {  background-color: #eee;  color: #444;  cursor: pointer;  padding: 18px;  width: 100%;  border: none;  text-align: left;  outline: none;  font-size: 15px;  transition: 0.4s;}\n");
                    // style.append(".accordion:after {  content: \"\002B\";  color: #777;  font-weight: bold;  float: left;  margin-right: 10px;}\n");
                    //style.append(".active:after {  content: \"\2212\";}\n");
                    // style.append(" tr:nth-child(even) {  background-color: #dddddd;}\n</style>");
                    style.append("</head>");
                    // style.append(".collapsible { background-color: #777; color: white;cursor: pointer; padding: 18px; width: 100%;border: none;text-align: left;outline: none; font-size: 15px;}");
                    //  style.append(".active, .collapsible:hover {background-color: #555;	}");
                    // style.append(".content { padding: 0 18px; display: none; overflow: hidden;background-color: #f1f1f1;}");
                    style.append("<script type='text/javascript' src='https://code.jquery.com/jquery-3.5.1.min.js'></script>	\n");
                    // style.append("<script type='text/javascript'>\n	$(document).ready(function(){$('#m1d').hide();$('#m2d').hide();$('#m3d').hide();\n");
                    //style.append("$('#m1').on('click', function (e) { e.preventDefault();var elem = $(this).next('#m1d')elem.toggle('slow');});}</script>\n");

                    // style.append("<style type='text/css'>\n.mainmune{text-align:center;color:#99FF00;background-color:#787878;font-family:Verdana;font-size:16px;font-weight:bold;height:35px;border:5px solid #FFFF00;}\n");
                    // style.append(".submune{text-align:left;color:#FFFF00;	background-color:#989898;font-family:Verdana;font-size:14px;font-weight:bold;margin-left:50px;}\n</style>\n");
                    String aa = "";
                    for (int ii = 0; ii < benefittable; ii++) {
                        aa = aa + "$('#m1d" + ii + "').hide();";
                    }
                    aa = aa + "$('#INFOR01').hide();";
                    aa = aa + "$('#INFO01').hide();";
                    aa = aa + "$('#RI02').hide();";
                    aa = aa + "$('#RJ02').hide();";
                    aa = aa + "$('#SI01').hide();";
                    aa = aa + "$('#PR01').hide();";
                    style.append("<script type='text/javascript' src='https://code.jquery.com/jquery-3.5.1.min.js'></script>\n");
                    style.append("<script type='text/javascript'>$(document).ready(function(){" + aa + "});	</script> \n");
                    style.append("<script>function myFunction(s) {  var x = document.getElementById(s);  if (x.style.display === 'none') {    x.style.display = 'block';  } else {    x.style.display = 'none';  } } </script>\n");

                    style.append("</head>\n");

                    String newStr = patientname.toString().replaceAll("inforeceiver", "Provider");
                    newStr = newStr.toString().replaceAll("inforeceiver", "Provider");
                    newStr = newStr.toString().replaceAll("infosource", "Payer");
                    newStr = newStr.toString().replaceAll("patientname", "Patient Information");
                    newStr = newStr.toString().replaceAll("first", "First Name");
                    newStr = newStr.toString().replaceAll("middle", "Middle Name");
                    newStr = newStr.toString().replaceAll("last", "Last Name");
                    newStr = newStr.toString().replaceAll("patientaddress", "Address");
                    newStr = newStr.toString().replaceAll("patientcity", "City");
                    newStr = newStr.toString().replaceAll("patientstate", "State");
                    newStr = newStr.toString().replaceAll("patientzip", "Zip Code");
                    newStr = newStr.toString().replaceAll("sex", "Sex");
                    newStr = newStr.toString().replaceAll("date-of-birth", "DOB");

                    String newStr2 = CDRList.toString();
                    newStr2 = newStr2.toString().replaceAll("info", "info");
                    newStr2 = newStr2.toString().replaceAll("coveragelevel", "Coverage Level");
                    newStr2 = newStr2.toString().replaceAll("servicetype", "Service Type");
                    newStr2 = newStr2.toString().replaceAll("servicetypecode", "Service Type Code");
                    newStr2 = newStr2.toString().replaceAll("insurancetype", "Insurance Type");
                    newStr2 = newStr2.toString().replaceAll("insurancetypecode", "Insurance Type Code");
                    newStr2 = newStr2.toString().replaceAll("plancoveragedescription", "Plan Coverage Description");
                    newStr2 = newStr2.toString().replaceAll("datequalifier", "Date Qualifier");
                    newStr2 = newStr2.toString().replaceAll("date-of-service", "Date of Service");
                    newStr2 = newStr2.toString().replaceAll("time_period_qualifier", "Time Period Qualifier");
                    newStr2 = newStr2.toString().replaceAll("benefitamount", "Benefit Amount(\\$)");
                    newStr2 = newStr2.toString().replaceAll("message", "Message");
                    newStr2 = newStr2.toString().replaceAll("yes_no_response_code", "Response Code");
                    newStr2 = newStr2.toString().replaceAll("plannetworkindicator", "Plan Network");

                    String newStr3 = otherinfo.toString();
                    newStr3 = newStr3.toString().replaceAll("trace_number", "Trace Number");
                    newStr3 = newStr3.toString().replaceAll("date-of-service", "Date of Service");
                    newStr3 = newStr3.toString().replaceAll("trace_number", "Trace Number");
                    newStr3 = newStr3.toString().replaceAll("trace_id", "Trace Id");
                    newStr3 = newStr3.toString().replaceAll("subscriberid", "Subscriber Id");

                    out.write("<html>" + style.toString() + " <body>\n" + newStr3.toString() + newStr.toString() + newStr2.toString() + "</body></html>");
                    out.flush();
                    out.close();
                } else {
                    System.out.println("firstelse");

                    System.out.println("dddd" + restVal);
                    System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

                    System.out.println(doc.getElementsByTagName("SuccessCode").item(0).getNodeName());

                    System.out.println(doc.getElementsByTagName("SuccessCode").item(0).getTextContent());
                    System.out.println(doc.getElementsByTagName("ExtraProcessingInfo").item(0).getTextContent());


                    System.out.println("----------------------------");


                    patientname.append((doc.getElementsByTagName("SuccessCode").item(0).getTextContent()));
                    patientname.append((doc.getElementsByTagName("ExtraProcessingInfo").item(0).getTextContent()));

                    StringBuffer style = new StringBuffer();
                    PrintWriter out = new PrintWriter(new File("d:\\xmlFile2223.html"));
                    patientname.append(doc.getElementsByTagName("SuccessCode").item(0).getTextContent().compareTo("Success"));
                    style.append("<head><style>table {  font-family: arial, sans-serif;  border-collapse: collapse;  width: 100%;} ");
                    style.append("td, th {  border: 1px solid #dddddd;  text-align: left;  padding: 8px;}");
                    style.append(" tr:nth-child(even) {  background-color: #dddddd;}</style></head>");
                    out.write("<html>" + style.toString() + "<body>" + patientname.toString() + CDRList.toString() + "</body></html>");
                    out.flush();
                    out.close();
                }

            } else {
                StringBuffer style = new StringBuffer();
                PrintWriter out = new PrintWriter(new File("d:\\xmlFile2223.html"));
                style.append("<head><style>table {  font-family: arial, sans-serif;  border-collapse: collapse;  width: 100%;} ");
                style.append("td, th {  border: 1px solid #dddddd;  text-align: left;  padding: 8px;}");
                style.append(" tr:nth-child(even) {  background-color: #dddddd;}</style></head>");
                out.write("<html><body>" + style.toString() + patientname.toString() + CDRList.toString() + "</body></html>");
                out.flush();
                out.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parmsg(String msg2) {

        try {
            finaloutput.setLength(0);
            patientname.setLength(0);
            CDRList.setLength(0);
            relationship.setLength(0);
            subscriberaddinfo.setLength(0);
            trace_number.setLength(0);
            trace_id.setLength(0);
            otherinfo.setLength(0);

            policystatus = "";
            planinfo = "";
            validtill = "";
            traceNo = "";

            //File inputFile = new File("D:\\xmlFile.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            //Document doc = dBuilder.parse(inputFile);
            Document doc = dBuilder.parse((new InputSource(new StringReader(msg2))));
            doc.getDocumentElement().normalize();


            String restVal = "-";

            NodeList restElmLst = doc.getElementsByTagName("SuccessCode");

            if (restElmLst.getLength() > 0) {

                System.out.println("In First IF  *************");
                if (doc.getElementsByTagName("SuccessCode").item(0).getTextContent().compareTo("Success") == 0) {
//                    System.out.println("In INNER First IF  *************");
//                    System.out.println("Starting ****** " + restVal);
//                    System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

//                    System.out.println("SuccessCode " + doc.getElementsByTagName("SuccessCode").item(0).getNodeName());

//                    System.out.println(doc.getElementsByTagName("SuccessCode").item(0).getTextContent());
//                    System.out.println("----------------------------");


                    System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                    NodeList nList = doc.getElementsByTagName("ResponseAsXml");

//                    System.out.println("----------------------------");

                    emsg = "";
                    msg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                    NodeList nodes = doc.getElementsByTagName("DoInquiryResult");
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Element element = (Element) nodes.item(i);
                        NodeList title = element.getElementsByTagName("ResponseAsXml");
                        Element line = (Element) title.item(0);
                        //System.out.println("Title: " + getCharacterDataFromElement(line));
                        emsg = getCharacterDataFromElement(line);
                    }
                    msg = msg + emsg;
//                    System.out.println("----------------------------");

//                    System.out.println("EMSG --> " + emsg);


//                    System.out.println("----------------------------");

                    DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder2 = dbFactory2.newDocumentBuilder();
                    Document doc2 = null;
                    if (emsg.trim().length() != 0) {
                        System.out.println("########### " + msg.trim());
                        doc2 = dBuilder2.parse((new InputSource(new StringReader(msg))));
                        doc2.getDocumentElement().normalize();
                    } else {
                        doc2 = doc;
                    }

                    System.out.println("----------------------------");

                    NodeList list = doc2.getElementsByTagName("dependent");
                    for (int i = 0; i < list.getLength(); i++) {
                        Node nNode = list.item(i);
                        if (nNode.getNodeType() == 1) {
                            Element eElement = (Element) nNode;

                            NodeList benefit = eElement.getElementsByTagName("benefit");
                            if (benefit.getLength() > 0) {
                                visitChildNodes(benefit);
                            }

                            NodeList patientname_n = eElement.getElementsByTagName("patientname");
                            if (patientname_n.getLength() > 0) {
                                visitChildNodes(patientname_n);
                                System.out.println();
                                System.out.println("******************************");
                                System.out.println(" GENDER TAG " + eElement.getElementsByTagName("sex").item(0).getNodeName());
                                System.out.println(" GENDER VAL " + eElement.getElementsByTagName("sex").item(0).getTextContent());
                                System.out.println("******************************");
                                System.out.println();
                                System.out.println("******************************");
                                System.out.println(" DOB TAG " + eElement.getElementsByTagName("date-of-birth").item(0).getNodeName());
                                System.out.println(" DOB VAL " + eElement.getElementsByTagName("date-of-birth").item(0).getTextContent());
                                System.out.println("******************************");
                                System.out.println();
                                patientname.append("<tr class=\"Inner\">\n");
                                patientname.append("<td align=left>" + eElement.getElementsByTagName("sex").item(0).getNodeName() + "</td>\n");
                                patientname.append("<td align=left>" + eElement.getElementsByTagName("sex").item(0).getTextContent() + "</td>\n");
                                patientname.append("</tr>\n");
                                patientname.append("<tr class=\"Inner\">\n");
                                patientname.append("<td align=left>" + eElement.getElementsByTagName("date-of-birth").item(0).getNodeName() + "</td>\n");
                                patientname.append("<td align=left>" + eElement.getElementsByTagName("date-of-birth").item(0).getTextContent() + "</td>\n");
                                patientname.append("</tr>\n");
                                patientname.append("</table>\n");
                                patientname.append("\n");
                            }




                            NodeList relationship = eElement.getElementsByTagName("relationship");
                            if (relationship.getLength() > 0) {
                                visitChildNodes(relationship);
                            }



                            System.out.println();
                            System.out.println("******************************");
                            System.out.println(" trace_number TAG " + eElement.getElementsByTagName("trace_number").item(0).getNodeName());
                            System.out.println(" trace_number VAL " + eElement.getElementsByTagName("trace_number").item(0).getTextContent());
                            System.out.println("******************************");
                            System.out.println();
                            otherinfo.append("<h2 class=\"accordion\" onclick='myFunction(\"OI01\");' >Info other ( " + policystatus + ")</h2>");
                            otherinfo.append("<table id=\"OI01\">\n");
                            otherinfo.append("<tr class=\"Inner\">\n");
                            otherinfo.append("<td align=left>Policy Status</td>\n");
                            otherinfo.append("<td align=left>" + policystatus + "</td>\n");
                            otherinfo.append("</tr>\n");
                            otherinfo.append("<tr class=\"Inner\">\n");
                            otherinfo.append("<td align=left>" + eElement.getElementsByTagName("trace_number").item(0).getNodeName() + "</td>\n");
                            otherinfo.append("<td align=left>" + eElement.getElementsByTagName("trace_number").item(0).getTextContent() + "</td>\n");
                            otherinfo.append("</tr>\n");



                            NodeList date_plan = eElement.getElementsByTagName("date");
                            if (date_plan.getLength() > 0) {
                                visitChildNodes(date_plan);
                            }




                            System.out.println();
                            System.out.println("******************************");
                            System.out.println(" trace_id TAG " + eElement.getElementsByTagName("trace_id").item(0).getNodeName());
                            System.out.println(" trace_id VAL " + eElement.getElementsByTagName("trace_id").item(0).getTextContent());
                            System.out.println("******************************");
                            System.out.println();
                            otherinfo.append("<tr class=\"Inner\">\n");
                            otherinfo.append("<td align=left>" + eElement.getElementsByTagName("trace_id").item(0).getNodeName() + "</td>\n");
                            otherinfo.append("<td align=left>" + eElement.getElementsByTagName("trace_id").item(0).getTextContent() + "</td>\n");
                            traceNo = eElement.getElementsByTagName("trace_id").item(0).getTextContent();
                            otherinfo.append("</tr>\n");
                            System.out.println("**********************************************");





                            NodeList subscriberaddinfo = eElement.getElementsByTagName("subscriberaddinfo");
                            if (subscriberaddinfo.getLength() > 0) {
                                visitChildNodes(subscriberaddinfo);
                            }




                            NodeList trace_number = eElement.getElementsByTagName("trace_number");
                            if (trace_number.getLength() > 0) {
                                visitChildNodes(trace_number);
                            }


                            NodeList trace_id = eElement.getElementsByTagName("trace_id");
                            if (trace_id.getLength() > 0) {
                                visitChildNodes(trace_id);
                            }

                        }
                    }
                    System.out.println("----------------------------");

                    // YAHAN SAY START KARNA HY ONE BY ONE
                    NodeList list2 = doc2.getElementsByTagName("subscriber");
                    for (int i = 0; i < list2.getLength(); i++) {
                        // visitChildNodes(list);
                        Node nNode2 = list2.item(i);
                        if (nNode2.getNodeType() == 1) {
                            Element eElement = (Element) nNode2;

                            NodeList patientname_n = eElement.getElementsByTagName("patientname");
                            if (patientname_n.getLength() > 0) {
                                visitChildNodes(patientname_n);
                                patientname.append("<tr class=\"Inner\">\n");
                                patientname.append("<td align=left>" + eElement.getElementsByTagName("sex").item(0).getNodeName() + "</td>\n");
                                patientname.append("<td align=left>" + eElement.getElementsByTagName("sex").item(0).getTextContent() + "</td>\n");
                                patientname.append("</tr>\n");
                                patientname.append("<tr class=\"Inner\">\n");
                                patientname.append("<td align=left>" + eElement.getElementsByTagName("date-of-birth").item(0).getNodeName() + "</td>\n");
                                patientname.append("<td align=left>" + eElement.getElementsByTagName("date-of-birth").item(0).getTextContent() + "</td>\n");
                                patientname.append("</tr>\n");
                                patientname.append("</table>\n");
                                patientname.append("\n");
                            }


                            NodeList subscribername = eElement.getElementsByTagName("subscribername");
                            if (subscribername.getLength() > 0) {
                                // patientname.append("<h2>Subscriber</h2>");
                                //patientname.append("<h2 onclick='myFunction(\"PR01\");' >Patient relationship</h2>");
                                //patientname.append("<table id='PR01'>\n");
                                patientname.append("<h2 class=\"accordion\" onclick='myFunction(\"SI01\");'>Subscriber Info</h2>");
                                patientname.append("<table id='SI01'>\n");
                                visitChildNodes_sub(subscribername);
                                patientname.append("</table>\n");
                                patientname.append("\n");
                            }


                            NodeList relationship = eElement.getElementsByTagName("relationship");
                            if (relationship.getLength() > 0) {
                                patientname.append("<h2 class=\"accordion\" onclick='myFunction(\"RI02\");'>Relationship to Insured</h2>");
                                patientname.append("<table id='RI02' >\n");
                                visitChildNodes_sub(relationship);
                                patientname.append("</table>\n");
                                patientname.append("\n");
                            }

                            NodeList rejection = eElement.getElementsByTagName("rejection");
                            if (rejection.getLength() > 0) {
                                patientname.append("<h2 class=\"accordion\" onclick='myFunction(\"RJ02\"); >rejection</h2>");
                                patientname.append("<table id='RJ02'>\n");
                                //  NodeList relationship = eElement.getElementsByTagName("relationship");
                                visitChildNodes_sub(rejection);
                                patientname.append("</table>\n");
                                patientname.append("\n");
                            }

                            NodeList benefit = eElement.getElementsByTagName("benefit");
                            if (benefit.getLength() > 0) {
                                visitChildNodes(benefit);
                            }

                            //8th Jan 2021
                            //Commented by tabish . This information is already been set and displayed below
                            /*patientname.append("<h2>subscriberid</h2>");
                            patientname.append("<table>\n");

                            patientname.append("<tr class=\"Inner\">\n");
                            patientname.append("<td align=left>" + eElement.getElementsByTagName("subscriberid").item(0).getNodeName() + "</td>\n");
                            patientname.append("<td align=left>" + eElement.getElementsByTagName("subscriberid").item(0).getTextContent() + "</td>\n");
                            patientname.append("</tr>\n");
                            patientname.append("</table>\n");
                            patientname.append("<BR><BR><BR>\n");*/

                            NodeList SUBI = eElement.getElementsByTagName("subscriberid");
                            if (SUBI.getLength() > 0) {
                                // patientname.append("<h2>subscriberid</h2>");
                                // 	patientname.append("<table>\n");
                                otherinfo.append("<tr class=\"Inner\">\n");
                                otherinfo.append("<td align=left>" + eElement.getElementsByTagName("subscriberid").item(0).getNodeName() + "</td>\n");
                                otherinfo.append("<td align=left>" + eElement.getElementsByTagName("subscriberid").item(0).getTextContent() + "</td>\n");
                                otherinfo.append("</tr>\n");
                                otherinfo.append("</table>\n");
                                otherinfo.append("\n");
                            } else {
                                otherinfo.append("</table>\n");
                                otherinfo.append("\n");
                            }
                        }
                    }


                    // *****************************************************************************************************
                    patientname.append("<h2 class=\"accordion\" onclick='myFunction(\"INFO01\");'>infosource</h2>");
                    patientname.append("<table id='INFO01'>\n");
                    NodeList list3 = doc2.getElementsByTagName("infosource");
                    visitChildNodes_sub(list3);
                    patientname.append("</table>\n");
                    patientname.append("\n");

                    patientname.append("<h2 class=\"accordion\" onclick='myFunction(\"INFOR01\");'>inforeceiver</h2>");
                    patientname.append("<table id='INFOR01'>\n");
                    NodeList list4 = doc2.getElementsByTagName("inforeceiver");
                    visitChildNodes_sub(list4);
                    patientname.append("</table>\n");
                    patientname.append("\n");
                    //********************************************************************************************************


                    // *****************************************************************************************************
                    //System.out.println(CDRList.toString());
                    StringBuilder style = new StringBuilder();
                    //PrintWriter out = new PrintWriter(new File("d:\\xmlFile2223.html"));
                    style.append("<head><meta http-equiv=\"Content-type\" content=\"text/html;charset=UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n");
                    style.append("<link href=\"eiblue.css\" rel=\"stylesheet\" type=\"text/css\" />");

                    // style.append("<style>\ntable {  font-family: arial, sans-serif;  border-collapse: collapse;  width: 100%;} \n");
                    // style.append("td, th {  border: 1px solid #dddddd;  text-align: left;  padding: 8px;}\n");
                    // style.append(".accordion {  background-color: #eee;  color: #444;  cursor: pointer;  padding: 18px;  width: 100%;  border: none;  text-align: left;  outline: none;  font-size: 15px;  transition: 0.4s;}\n");
                    // style.append(".accordion:after {  content: \"\002B\";  color: #777;  font-weight: bold;  float: left;  margin-right: 10px;}\n");
                    //style.append(".active:after {  content: \"\2212\";}\n");
                    // style.append(" tr:nth-child(even) {  background-color: #dddddd;}\n</style>");
                    style.append("</head>");
                    // style.append(".collapsible { background-color: #777; color: white;cursor: pointer; padding: 18px; width: 100%;border: none;text-align: left;outline: none; font-size: 15px;}");
                    //  style.append(".active, .collapsible:hover {background-color: #555;	}");
                    // style.append(".content { padding: 0 18px; display: none; overflow: hidden;background-color: #f1f1f1;}");
                    style.append("<script type='text/javascript' src='https://code.jquery.com/jquery-3.5.1.min.js'></script>	\n");
                    // style.append("<script type='text/javascript'>\n	$(document).ready(function(){$('#m1d').hide();$('#m2d').hide();$('#m3d').hide();\n");
                    //style.append("$('#m1').on('click', function (e) { e.preventDefault();var elem = $(this).next('#m1d')elem.toggle('slow');});}</script>\n");

                    // style.append("<style type='text/css'>\n.mainmune{text-align:center;color:#99FF00;background-color:#787878;font-family:Verdana;font-size:16px;font-weight:bold;height:35px;border:5px solid #FFFF00;}\n");
                    // style.append(".submune{text-align:left;color:#FFFF00;	background-color:#989898;font-family:Verdana;font-size:14px;font-weight:bold;margin-left:50px;}\n</style>\n");
                    String aa = "";
                    for (int ii = 0; ii < benefittable; ii++) {
                        aa = aa + "$('#m1d" + ii + "').hide();";
                    }
                    aa = aa + "$('#INFOR01').hide();";
                    aa = aa + "$('#INFO01').hide();";
                    aa = aa + "$('#RI02').hide();";
                    aa = aa + "$('#RJ02').hide();";
                    aa = aa + "$('#SI01').hide();";
                    aa = aa + "$('#PR01').hide();";
                    aa = aa + "$('#OI01').hide();";
                    aa = aa + "$('#PN01').hide();";
                    style.append("<script type='text/javascript' src='https://code.jquery.com/jquery-3.5.1.min.js'></script>\n");
                    style.append("<script type='text/javascript'>$(document).ready(function(){" + aa + "});	</script> \n");
                    style.append("<script>function myFunction(s) {  var x = document.getElementById(s);  if (x.style.display === 'none') {    x.style.display = 'block';  } else {    x.style.display = 'none';  } } </script>\n");

                    style.append("</head>\n");

                    String newStr = patientname.toString().replaceAll("inforeceiver", "Provider");
//                    newStr = newStr.replaceAll("inforeceiver", "Provider");
                    newStr = newStr.replaceAll("infosource", "Payer");
                    newStr = newStr.replaceAll("patientname", "Patient Information");
                    newStr = newStr.replaceAll("first", "First Name");
                    newStr = newStr.replaceAll("middle", "Middle Name");
                    newStr = newStr.replaceAll("last", "Last Name");
                    newStr = newStr.replaceAll("patientaddress", "Address");
                    newStr = newStr.replaceAll("patientcity", "City");
                    newStr = newStr.replaceAll("patientstate", "State");
                    newStr = newStr.replaceAll("patientzip", "Zip Code");
                    newStr = newStr.replaceAll("sex", "Sex");
                    newStr = newStr.replaceAll("date-of-birth", "DOB");

                    String newStr2 = CDRList.toString();
                    newStr2 = newStr2.replaceAll("info", "info");
                    newStr2 = newStr2.replaceAll("coveragelevel", "Coverage Level");
                    newStr2 = newStr2.replaceAll("servicetype", "Service Type");
                    newStr2 = newStr2.replaceAll("servicetypecode", "Service Type Code");
                    newStr2 = newStr2.replaceAll("insurancetype", "Insurance Type");
                    newStr2 = newStr2.replaceAll("insurancetypecode", "Insurance Type Code");
                    newStr2 = newStr2.replaceAll("plancoveragedescription", "Plan Coverage Description");
                    newStr2 = newStr2.replaceAll("datequalifier", "Date Qualifier");
                    newStr2 = newStr2.replaceAll("date-of-service", "Date of Service");
                    newStr2 = newStr2.replaceAll("time_period_qualifier", "Time Period Qualifier");
                    newStr2 = newStr2.replaceAll("benefitamount", "Benefit Amount(\\$)");
                    newStr2 = newStr2.replaceAll("message", "Message");
                    newStr2 = newStr2.replaceAll("yes_no_response_code", "Response Code");
                    newStr2 = newStr2.replaceAll("plannetworkindicator", "Plan Network");

                    String newStr3 = otherinfo.toString();
                    newStr3 = newStr3.replaceAll("trace_number", "Trace Number");
                    newStr3 = newStr3.replaceAll("date-of-service", "Date of Service");
                    newStr3 = newStr3.replaceAll("trace_number", "Trace Number");
                    newStr3 = newStr3.replaceAll("trace_id", "Trace Id");
                    newStr3 = newStr3.replaceAll("subscriberid", "Subscriber Id");

                    finaloutput.append("<html>" + style.toString() + " <body>\n" + newStr3 + newStr + newStr2 + "</body></html>");
                    //out.flush();
                    //out.close();
                    // *****************************************************************************************************
                } else {
                    System.out.println("IN INNER ELSE ******");
                    //Error
                    patientname.append((doc.getElementsByTagName("SuccessCode").item(0).getTextContent()));
                    patientname.append((doc.getElementsByTagName("ExtraProcessingInfo").item(0).getTextContent()));

                    StringBuffer style = new StringBuffer();
                    // PrintWriter out = new PrintWriter(new File("d:\\xmlFile2223.html"));
                    patientname.append(doc.getElementsByTagName("SuccessCode").item(0).getTextContent().compareTo("Success"));
                    style.append("<head><style>table {  font-family: arial, sans-serif;  border-collapse: collapse;  width: 100%;} ");
                    style.append("td, th {  border: 1px solid #dddddd;  text-align: left;  padding: 8px;}");
                    style.append(" tr:nth-child(even) {  background-color: #dddddd;}</style></head>");
                    finaloutput.append("<html>" + style.toString() + "<body>" + patientname.toString() + CDRList.toString() + "</body></html>");
                    //out.flush();
                    //out.close();
                }
            } else {
                System.out.println("IN FIRST ELSE *************** ");
                StringBuffer style = new StringBuffer();
                // PrintWriter out = new PrintWriter(new File("d:\\xmlFile2223.html"));
                style.append("<head><style>table {  font-family: arial, sans-serif;  border-collapse: collapse;  width: 100%;} ");
                style.append("td, th {  border: 1px solid #dddddd;  text-align: left;  padding: 8px;}");
                style.append(" tr:nth-child(even) {  background-color: #dddddd;}</style></head>");
                finaloutput.append("<html><body>" + style.toString() + patientname.toString() + CDRList.toString() + "</body></html>");
                // out.flush();
                // out.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void visitChildNodes(NodeList nList) {

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
//                System.out.println("Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent());
                //Check all attributes
                int header = 0;

                if (node.getNodeName().compareTo("benefit") == 0) {
                    System.out.println();
                    System.out.println(" ******* IN BENEFIT ****** ");
                    visitChildbenefit(node.getChildNodes(), header);
                    CDRList.append("</table>\n");
                    CDRList.append("\n");
                    benefittable++;
                    System.out.println("**********************************************");
                }
                if (node.getNodeName().compareTo("patientname") == 0) {
                    System.out.println();
                    System.out.println(" ******* IN patient name ****** ");
                    patientname.append("<h2 class=\"accordion\"  onclick='myFunction(\"PN01\");' >patientname</h2>");
                    patientname.append("<table id='PN01'>\n");
                    visitChildNodes_single(node.getChildNodes());
                    // patientname.append("</table>\n");
                    // patientname.append("<BR><BR><BR>\n");
                    System.out.println("**********************************************");
                }
                if (node.getNodeName().compareTo("relationship") == 0) {
                    System.out.println();
                    System.out.println(" ******* IN relationship Visit Nodes  ****** ");
                    patientname.append("<h2 class=\"accordion\" onclick='myFunction(\"PR01\");' >Patient relationship</h2>");
                    patientname.append("<table id='PR01'>\n");
                    visitChildNodes_single(node.getChildNodes());
                    patientname.append("</table>\n");
                    patientname.append("\n");
                    System.out.println("**********************************************");

                }
                if (node.getNodeName().compareTo("date") == 0) {
                    System.out.println();
                    System.out.println(" ******* IN date Visit Nodes  ****** ");
                    //patientname.append("<h2>patientname</h2>");
                    //patientname.append("<table>\n");
                    visitChildNodes_single_otherinfo(node.getChildNodes());
                    // patientname.append("</table>\n");
                    // patientname.append("<BR><BR><BR>\n");
                    // otherinfo.append("</table>\n");
                    //  otherinfo.append("<BR><BR><BR>\n");
                    System.out.println("**********************************************");
                }

                if (node.getNodeName().compareTo("subscriberaddinfo") == 0) {
                    System.out.println();
                    System.out.println(" ******* IN subscriber add info Visit Nodes  ****** ");
                    //patientname.append("<h2>subscriberaddinfo</h2>");
                    // patientname.append("<table>\n");
                    visitChildNodes_single_otherinfo(node.getChildNodes());
                    //patientname.append("</table>\n");
                    //patientname.append("<BR><BR><BR>\n");
                    System.out.println("**********************************************");

                }

                if (node.getNodeName().compareTo("subscribername") == 0) {
                    System.out.println();
                    System.out.println(" ******* IN subscriber name Visit Nodes  ****** ");
//                    System.out.println("Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent());
                    visitChildNodes(node.getChildNodes());
                    System.out.println("**********************************************");
                }
 /*              if (node.getNodeName().compareTo("trace_number") == 0) {
                    System.out.println(" ******* IN trace_number Visit Nodes  ****** ");
                    otherinfo.append("<h2 class=\"accordion\" onclick='myFunction(\"OI01\");' >Info other ( " + policystatus + ")</h2>");
                    otherinfo.append("<table id=\"OI01\">\n");
                    otherinfo.append("<tr class=\"Inner\">\n");
                    otherinfo.append("<td align=left>Policy Status</td>\n");
                    otherinfo.append("<td align=left>" + policystatus + "</td>\n");
                    otherinfo.append("</tr>\n");
                    otherinfo.append("<tr class=\"Inner\">\n");
                    otherinfo.append("<td align=left>" + node.getNodeName() + "</td>\n");
                    otherinfo.append("<td align=left>" + node.getTextContent() + "</td>\n");
                    otherinfo.append("</tr>\n");
                    //node.getTextContent();
                    //visitChildNodes_single(node.getChildNodes());
                    System.out.println("**********************************************");
                }

                if (node.getNodeName().compareTo("trace_id") == 0) {
                    // patientname.append("<h2>trace_id</h2>");
                    // patientname.append("<table>\n");
                    System.out.println(" ******* IN trace_number Visit Nodes  ****** ");
                    otherinfo.append("<tr class=\"Inner\">\n");
                    otherinfo.append("<td align=left>" + node.getNodeName() + "</td>\n");
                    otherinfo.append("<td align=left>" + node.getTextContent() + "</td>\n");
                    traceNo = node.getTextContent();
                    otherinfo.append("</tr>\n");
                    //node.getTextContent();
                    //visitChildNodes_single(node.getChildNodes());
                    //patientname.append("</table>\n");
                    //patientname.append("<BR><BR><BR>\n");
                    System.out.println("**********************************************");
                }
*/

                if (node.hasChildNodes()) {
                    //We got more childs; Let's visit them as well
                    //  visitChildNodes(node.getChildNodes());
                }
                if (node.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node tempNode = nodeMap.item(i);
                        System.out.println("Attr name : " + tempNode.getNodeName() + "; Value = " + tempNode.getNodeValue());
                    }
                    if (node.hasChildNodes()) {
                        //We got more childs; Let's visit them as well
                        visitChildNodes(node.getChildNodes());
                    }

                }

            }
        }
    }


    private static void visitChildNodes_sub(NodeList nList) {
        // patientname.append("<tr class=\"Inner\">\n");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent());
                //Check all attributes
                // finalmsg= node.getTextContent();

                System.out.println();
                System.out.println(" ******* IN subscriber name ****** ");
                visitChildNodes_sub_sub(node.getChildNodes());
                System.out.println("**********************************************");
         /*
          if(node.getNodeName().compareTo("infosource")==0)
          {

          	patientname.append("<td align=left>" +node.getNodeName() + "</td>\n");
          	patientname.append("<td align=left>" +node.getTextContent() + "</td>\n");

          } if(node.getNodeName().compareTo("inforeceiver")==0)
          {

          	patientname.append("<td align=left>" +node.getNodeName() + "</td>\n");
          	patientname.append("<td align=left>" +node.getTextContent() + "</td>\n");

          }
          */

                if (node.hasChildNodes()) {
                    //We got more childs; Let's visit them as well
                    //visitChildNodes_sub(node.getChildNodes());
                }
                if (node.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node tempNode = nodeMap.item(i);
                        System.out.println("Attr name : " + tempNode.getNodeName() + "; Value = " + tempNode.getNodeValue());
                    }
                    if (node.hasChildNodes()) {
                        //We got more childs; Let's visit them as well
                        visitChildNodes_sub_sub(node.getChildNodes());
                    }

                }
                // patientname.append("</tr>\n");
            }
        }
    }

    private static void visitChildNodes_sub_sub(NodeList nList) {
        patientname.append("<tr class=\"Inner\">\n");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Node Name_subscribername_sub = " + node.getNodeName() + "; Value = " + node.getTextContent());
                //Check all attributes
                // finalmsg= node.getTextContent();

                System.out.println();
                System.out.println(" ******* IN subscriber_SUB name ****** ");
                patientname.append("<td align=left>" + node.getNodeName() + "</td>\n");
                patientname.append("<td align=left>" + node.getTextContent() + "</td>\n");
                visitChildNodes_sub(node.getChildNodes());
                System.out.println("**********************************************");

                if (node.hasChildNodes()) {
                    //We got more childs; Let's visit them as well
                    //visitChildNodes_sub(node.getChildNodes());
                }
                if (node.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node tempNode = nodeMap.item(i);
                        System.out.println("Attr name : " + tempNode.getNodeName() + "; Value = " + tempNode.getNodeValue());
                    }
                    if (node.hasChildNodes()) {
                        //We got more childs; Let's visit them as well
                        visitChildNodes_sub(node.getChildNodes());
                    }

                }
                patientname.append("</tr>\n");
            }
        }
    }

    private static void visitChildNodes_single(NodeList nList) {
        patientname.append("<tr class=\"Inner\">\n");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Node Name_S = " + node.getNodeName() + "; Value = " + node.getTextContent());
                //Check all attributes
                // finalmsg= node.getTextContent();
                patientname.append("<td align=left>" + node.getNodeName() + "</td>\n");
                patientname.append("<td align=left>" + node.getTextContent() + "</td>\n");
                visitChildNodes(node.getChildNodes());

                if (node.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node tempNode = nodeMap.item(i);
                        System.out.println("Attr name : " + tempNode.getNodeName() + "; Value = " + tempNode.getNodeValue());
                    }
                    if (node.hasChildNodes()) {
                        //We got more childs; Let's visit them as well
                        visitChildNodes(node.getChildNodes());
                    }

                }
                patientname.append("</tr>\n");
            }
        }
    }

    private static void visitChildNodes_single_otherinfo(NodeList nList) {
        otherinfo.append("<tr class=\"Inner\">\n");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Node Name_S = " + node.getNodeName() + "; Value = " + node.getTextContent());
                //Check all attributes
                // finalmsg= node.getTextContent();
                otherinfo.append("<td align=left>" + node.getNodeName() + "</td>\n");
                otherinfo.append("<td align=left>" + node.getTextContent() + "</td>\n");
                visitChildNodes(node.getChildNodes());

                if (node.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node tempNode = nodeMap.item(i);
                        System.out.println("Attr name : " + tempNode.getNodeName() + "; Value = " + tempNode.getNodeValue());
                    }
                    if (node.hasChildNodes()) {
                        //We got more childs; Let's visit them as well
                        visitChildNodes(node.getChildNodes());
                    }

                }
                otherinfo.append("</tr>\n");
            }
        }
    }

    private static void visitChildbenefit(NodeList nList, int header) {

        if (header == 0) {
            header = 1;
        } else {

        }
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent());
                //Check all attributes
                // finalmsg= node.getTextContent();

                //  CDRList.append("<td align=left>" + SNo + "</td>\n");
                if (node.getNodeName().compareTo("info") == 0) {
                    CDRList.append("<h2 class=\"accordion\" onclick='myFunction(\"m1d" + benefittable + "\");' >" + node.getTextContent() + "</h2>");
                    CDRList.append("<table id='m1d" + benefittable + "'>\n");
                    CDRList.append("<tr>\n");
                    CDRList.append("<td align=left >" + node.getNodeName() + "</td>\n");
                    CDRList.append("<td align=left >" + node.getTextContent() + "</td>\n");
                    if (node.getTextContent().contains("Active")) {
                        policystatus = "<font color='green'>" + node.getTextContent() + "</font>";
                    } else if (node.getTextContent().contains("Active Coverage")) {
                        policystatus = "<font color='green'>" + node.getTextContent() + "</font>";
                    }

                } else {
                    CDRList.append("<tr>\n");
                    CDRList.append("<td align=left >" + node.getNodeName() + "</td>\n");
                    CDRList.append("<td align=left >" + node.getTextContent() + "</td>\n");
                }
              /*if(node.getNodeName().compareTo("servicetypecode")==0)
              {
                  //visitChildNodes(node.getChildNodes());
                  CDRList.append("<td align=left>" +node.getNodeName() + "</td>\n");
                  CDRList.append("<td align=left>||" +node.getTextContent() + "</td>\n");

              }if(node.getNodeName().compareTo("percent")==0) {
              CDRList.append("<td align=left>" +node.getNodeName() + "</td>\n");

              double  ii=0.0;
             // ii=Integer.parseInt(node.getTextContent());
            //  ii=ii*1;
             // ii=ii*100;
              CDRList.append("<td align=left>|"+ii+"|" +node.getTextContent() + "</td>\n");
          }


          else {
              CDRList.append("<td align=left>" +node.getNodeName() + "</td>\n");
              CDRList.append("<td align=left>" +node.getTextContent() + "</td>\n");
          }

*/
                if (node.getNodeName().compareTo("benefit") == 0) {
                    //visitChildNodes(node.getChildNodes());
                }
                if (node.hasChildNodes()) {
                    //We got more childs; Let's visit them as well
                    //  visitChildNodes(node.getChildNodes());
                }
                if (node.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node tempNode = nodeMap.item(i);
                        System.out.println("Attr name : " + tempNode.getNodeName() + "; Value = " + tempNode.getNodeValue());
                    }
                    if (node.hasChildNodes()) {
                        //We got more childs; Let's visit them as well
                        visitChildNodes(node.getChildNodes());
                    }
                }
                CDRList.append("</tr>\n");
            }
        }
    }


    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }
}
