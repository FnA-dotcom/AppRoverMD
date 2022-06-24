package md;

import org.w3c.dom.CharacterData;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class eiresponse extends HttpServlet {

    public static String emsg = "";
    public static String msg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static StringBuffer CDRList = new StringBuffer();
    public static StringBuffer patientname = new StringBuffer();
    public static StringBuffer relationship = new StringBuffer();
    public static StringBuffer subscriberaddinfo = new StringBuffer();
    public static StringBuffer trace_number = new StringBuffer();
    public static StringBuffer trace_id = new StringBuffer();
    public static StringBuffer finaloutput = new StringBuffer();


    public static void parmsg(String msg2) {

        try {
            finaloutput.setLength(0);
            patientname.setLength(0);
            CDRList.setLength(0);
            relationship.setLength(0);
            subscriberaddinfo.setLength(0);
            trace_number.setLength(0);
            trace_id.setLength(0);


            //File inputFile = new File("D:\\xmlFile.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            //Document doc = dBuilder.parse(inputFile);
            Document doc = dBuilder.parse((new InputSource(new StringReader(msg2))));
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

                            NodeList patientname = eElement.getElementsByTagName("patientname");
                            visitChildNodes(patientname);

                            NodeList relationship = eElement.getElementsByTagName("relationship");
                            visitChildNodes(relationship);

                            NodeList subscriberaddinfo = eElement.getElementsByTagName("subscriberaddinfo");
                            visitChildNodes(subscriberaddinfo);

                            NodeList trace_number = eElement.getElementsByTagName("trace_number");
                            visitChildNodes(trace_number);

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

                            NodeList subscribername = eElement.getElementsByTagName("subscribername");
                            if (subscribername.getLength() > 0) {
                                patientname.append("<h2>subscribername</h2>");
                                patientname.append("<table>\n");
                                visitChildNodes_sub(subscribername);
                                patientname.append("</table>\n");
                                patientname.append("<BR><BR><BR>\n");
                            }

                            NodeList relationship = eElement.getElementsByTagName("relationship");
                            if (relationship.getLength() > 0) {
                                patientname.append("<h2>relationship</h2>");
                                patientname.append("<table>\n");
                                visitChildNodes_sub(relationship);
                                patientname.append("</table>\n");
                                patientname.append("<BR><BR><BR>\n");
                            }

                            NodeList rejection = eElement.getElementsByTagName("rejection");
                            if (rejection.getLength() > 0) {
                                patientname.append("<h2>rejection</h2>");
                                patientname.append("<table>\n");
                                //  NodeList relationship = eElement.getElementsByTagName("relationship");
                                visitChildNodes_sub(rejection);
                                patientname.append("</table>\n");
                                patientname.append("<BR><BR><BR>\n");
                            }
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
                                patientname.append("<h2>subscriberid</h2>");
                                patientname.append("<table>\n");

                                patientname.append("<tr class=\"Inner\">\n");
                                patientname.append("<td align=left>" + eElement.getElementsByTagName("subscriberid").item(0).getNodeName() + "</td>\n");
                                patientname.append("<td align=left>" + eElement.getElementsByTagName("subscriberid").item(0).getTextContent() + "</td>\n");
                                patientname.append("</tr>\n");
                                patientname.append("</table>\n");
                                patientname.append("<BR><BR><BR>\n");

                            }


                        }
                    }


                    patientname.append("<h2>infosource</h2>");
                    patientname.append("<table>\n");
                    NodeList list3 = doc2.getElementsByTagName("infosource");
                    visitChildNodes_sub(list3);
                    patientname.append("</table>\n");
                    patientname.append("<BR><BR><BR>\n");

                    patientname.append("<h2>inforeceiver</h2>");
                    patientname.append("<table>\n");
                    NodeList list4 = doc2.getElementsByTagName("inforeceiver");
                    visitChildNodes_sub(list4);
                    patientname.append("</table>\n");
                    patientname.append("<BR><BR><BR>\n");

                    //System.out.println(CDRList.toString());
                    StringBuffer style = new StringBuffer();

                    //  PrintWriter out = new PrintWriter(finaltrizetto.outputHtml);
                    style.append("<head><style>table {  font-family: arial, sans-serif;  border-collapse: collapse;  width: 100%;} ");
                    style.append("td, th {  border: 1px solid #dddddd;  text-align: left;  padding: 8px;}");
                    style.append(" tr:nth-child(even) {  background-color: #dddddd;}  tr:nth-child(odd) {  background-color: #5A8DEE;} body {background-color: powderblue;}\r\n" +
                            "h2   {color: Black;background-color:#FF5B5C}h2 {text-transform: capitalize; text-align: center;}</style></head>");
                    // out.write("<html><body>"+style.toString() +patientname.toString()+ CDRList.toString() + "</body></html>");
                    finaloutput.append("<html><body>" + style.toString() + patientname.toString() + CDRList.toString() + "</body></html>");


                } else {
                    StringBuffer style = new StringBuffer();

                    //PrintWriter out = new PrintWriter(finaltrizetto.outputHtml);
                    System.out.println("ppp " + doc.getElementsByTagName("SuccessCode").item(0).getTextContent());
                    patientname.append("<h2>" + doc.getElementsByTagName("SuccessCode").item(0).getTextContent() + "</h2>");
                    style.append("<head><style>table {  font-family: arial, sans-serif;  border-collapse: collapse;  width: 100%;} ");
                    style.append("td, th {  border: 1px solid #dddddd;  text-align: left;  padding: 8px;}");
                    style.append(" tr:nth-child(even) {  background-color: #dddddd;}</style></head>");
                    finaloutput.append("<html><body>" + style.toString() + patientname.toString() + CDRList.toString() + "</body></html>");

                }

            } else {
                StringBuffer style = new StringBuffer();
                //finaltrizetto.outputHtml.reset();
                //PrintWriter out = new PrintWriter(finaltrizetto.outputHtml);
                patientname.append("<h2>ERROR</h2>");

                style.append("<head><style>table {  font-family: arial, sans-serif;  border-collapse: collapse;  width: 100%;} ");
                style.append("td, th {  border: 1px solid #dddddd;  text-align: left;  padding: 8px;}");
                style.append(" tr:nth-child(even) {  background-color: #dddddd;}</style></head>");
                finaloutput.append("<html><body>" + style.toString() + patientname.toString() + CDRList.toString() + "</body></html>");


            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
    }

    private static void visitChildNodes(NodeList nList) {
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                // System.out.println("Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent());
                //Check all attributes
                // finalmsg= node.getTextContent();

                if (node.getNodeName().compareTo("benefit") == 0) {
                    CDRList.append("<h2>Benefit</h2>");
                    CDRList.append("<table>\n");
                    visitChildbenefit(node.getChildNodes());
                    CDRList.append("</table>\n");
                    CDRList.append("<BR><BR><BR>\n");
                    //System.out.println("-----------------------");
                }
                if (node.getNodeName().compareTo("patientname") == 0) {
                    patientname.append("<h2>patientname</h2>");
                    patientname.append("<table>\n");
                    visitChildNodes_single(node.getChildNodes());
                    patientname.append("</table>\n");
                    patientname.append("<BR><BR><BR>\n");
                }
                if (node.getNodeName().compareTo("relationship") == 0) {
                    patientname.append("<h2>relationship</h2>");
                    patientname.append("<table>\n");
                    visitChildNodes_single(node.getChildNodes());
                    patientname.append("</table>\n");
                    patientname.append("<BR><BR><BR>\n");

                }
                if (node.getNodeName().compareTo("subscriberaddinfo") == 0) {
                    patientname.append("<h2>subscriberaddinfo</h2>");
                    patientname.append("<table>\n");
                    visitChildNodes_single(node.getChildNodes());
                    patientname.append("</table>\n");
                    patientname.append("<BR><BR><BR>\n");

                }
                if (node.getNodeName().compareTo("trace_number") == 0) {
                    patientname.append("<h2>trace_id</h2>");
                    patientname.append("<table>\n");
                    patientname.append("<tr class=\"Inner\">\n");
                    patientname.append("<td align=left>" + node.getNodeName() + "</td>\n");
                    patientname.append("<td align=left>" + node.getTextContent() + "</td>\n");
                    patientname.append("</tr>\n");
                    //node.getTextContent();
                    //visitChildNodes_single(node.getChildNodes());
                    patientname.append("</table>\n");
                    patientname.append("<BR><BR><BR>\n");

                }
                if (node.getNodeName().compareTo("trace_id") == 0) {
                    patientname.append("<h2>trace_id</h2>");
                    patientname.append("<table>\n");
                    patientname.append("<tr class=\"Inner\">\n");
                    patientname.append("<td align=left>" + node.getNodeName() + "</td>\n");
                    patientname.append("<td align=left>" + node.getTextContent() + "</td>\n");
                    patientname.append("</tr>\n");
                    //node.getTextContent();
                    //visitChildNodes_single(node.getChildNodes());
                    patientname.append("</table>\n");
                    patientname.append("<BR><BR><BR>\n");
                }

                if (node.getNodeName().compareTo("subscribername") == 0) {
                    System.out.println("Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent());
                    visitChildNodes(node.getChildNodes());
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
            }
        }
    }


    private static void visitChildNodes_sub(NodeList nList) {
        // patientname.append("<tr class=\"Inner\">\n");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                //System.out.println("Node Name_SSS = " + node.getNodeName() + "; Value = " + node.getTextContent());
                //Check all attributes
                // finalmsg= node.getTextContent();


                visitChildNodes_sub_sub(node.getChildNodes());
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
                //  System.out.println("Node Name_SSS_sub = " + node.getNodeName() + "; Value = " + node.getTextContent());
                //Check all attributes
                // finalmsg= node.getTextContent();

                patientname.append("<td align=left>" + node.getNodeName() + "</td>\n");
                patientname.append("<td align=left>" + node.getTextContent() + "</td>\n");
                visitChildNodes_sub(node.getChildNodes());


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
                //System.out.println("Node Name_S = " + node.getNodeName() + "; Value = " + node.getTextContent());
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

    private static void visitChildbenefit(NodeList nList) {

        CDRList.append("<tr class=\"Inner\">\n");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                //  System.out.println("Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent());
                //Check all attributes
                // finalmsg= node.getTextContent();

                //  CDRList.append("<td align=left>" + SNo + "</td>\n");

                CDRList.append("<td align=left>" + node.getNodeName() + "</td>\n");
                CDRList.append("<td align=left>" + node.getTextContent() + "</td>\n");

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