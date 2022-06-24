package availity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class test2 {

    static StringBuilder html = new StringBuilder();
    static StringBuilder headings = new StringBuilder();
    static int closer = 0;

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        try {
// one plan
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
                if (field.getKey().compareTo("plans") == 0) {
                    System.out.println("key: " + field.getKey());
                    html.append("<h1>" + field.getKey().toUpperCase() + "</h1>\n");

                    processNode_C(field.getValue(), "object");
                }

            }

            //System.out.println("/// Object end ///");
            // html.append("</table>\n");


        } else {
            // if node is a simple value (like string or int) so let's print it
            html.append("<td align=left  style=\"word-wrap: break-word\" >" + node + "</td>\n");
            //System.out.println("value: " + node);
            html.append("</tr>\n");
        }
    }

    private static void processNode_C(JsonNode node, String ty) {
        if (node.isArray()) {
            // if the node is a list of items,
            //  go through all the items and process them individually
            //System.out.println("=== Array start ===");

            for (final JsonNode objInArray : node) {

                // System.out.println("--- Array element start ---");
                // process the item in the array
                System.out.println("|||||||||" + objInArray.toString());
                processNode_C(objInArray, "Arry");
                // closer=1;
                //System.out.println("--- Array element end ---");
            }

            // System.out.println("=== Array end ===");
        } else if (node.isContainerNode()) {
            // if the node is an object,
            //  go through all fields within the object
            //System.out.println("/// Object start ///");

            //html.append("<table>\n");

            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {

                //	html.append("<tr>\n");
                Map.Entry<String, JsonNode> field = it.next();


                if (field.getKey().compareTo("primaryCareProvider") == 0) {
                    System.out.println("key: " + field.getKey());
                    html.append("<h1>" + field.getKey().toUpperCase() + "</h1>\n");
                    processNode_C(field.getValue(), "object");

                } else if (field.getKey().compareTo("benefits") == 0) {
                    System.out.println("key: " + field.getKey());
                    html.append("<h1>" + field.getKey().toUpperCase() + "</h1>\n");
                    processNode_benefits(field.getValue(), "object");

                }
            }

            //System.out.println("/// Object end ///");
            // html.append("</table>\n");


        } else {
            // if node is a simple value (like string or int) so let's print it
            html.append("<td align=left  style=\"word-wrap: break-word\" >" + node + "</td>\n");
            //System.out.println("value: " + node);
            html.append("</tr>\n");
        }

    }

    private static void processNode_benefits(JsonNode node, String ty) {

        String heading_s = "";
        if (node.isArray()) {
            // if the node is a list of items,
            //  go through all the items and process them individually
            //System.out.println("=== Array start ===");

            for (final JsonNode objInArray : node) {

                // System.out.println("--- Array element start ---");
                // process the item in the array
                System.out.println("11|||||||||" + objInArray.toString());
                processNode_benefits(objInArray, "Arry");
                // closer=1;
                //System.out.println("--- Array element end ---");
                break;
            }

            // System.out.println("=== Array end ===");
        } else if (node.isContainerNode()) {
            // if the node is an object,
            //  go through all fields within the object
            //System.out.println("/// Object start ///");

            html.append("<table>\n");
            html.append("<tr>\n");
            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {
                int stoploop = 0;

                Map.Entry<String, JsonNode> field = it.next();


                if (field.getKey().compareTo("primaryCareProvider") == 0) {
                    System.out.println("key: " + field.getKey());
                    html.append("<h1>" + field.getKey().toUpperCase() + "</h1>\n");

                    processNode_benefits(field.getValue(), "object");
                } else if (field.getKey().compareTo("payerNotes") == 0) {

                    processNode_payernote(field.getValue(), "payernote");
                    //html.append("<td align=left style=\"word-wrap: break-word\">"+field.getKey().toUpperCase()+"</td>\n");
                    //html.append("<td align=left style=\"word-wrap: break-word\">"+field.getValue()+"</td>\n");
                    stoploop = 1;
                } else if (field.getKey().compareTo("statusDetails") == 0) {
                    processNode_benefits_details(field.getValue(), field.getKey());

                } else if (field.getKey().compareTo("amounts") == 0) {
                    processNode_benefits_details(field.getValue(), "object");

                } else {
                    if (ty.compareTo("object") == 0) {

                        html.append("<h1>" + field.getKey().toUpperCase() + "</h1>\n");
                    } else if (field.getKey().compareTo("name") == 0) {
                        // html.append("<h1>"+field.getValue()+"</h1>\n");
                        heading_s = field.getValue().toString();
                    } else if (field.getKey().compareTo("type") == 0) {
                        // html.append("<h1>"+field.getValue()+"</h1>\n");
                        heading_s += "-" + field.getValue().toString();
                    } else if (field.getKey().compareTo("statusCode") == 0) {
                        // html.append("<h1>"+field.getValue()+"</h1>\n");
                        // heading_s+="-"+field.getValue().toString();
                    } else if (field.getKey().compareTo("status") == 0) {
                        // html.append("<h1>"+field.getValue()+"</h1>\n");
                        if (field.getValue().toString().startsWith("Active")) {
                            heading_s += "- <p style=\"color:red\">" + field.getValue().toString() + "</p>";
                        } else {
                            heading_s += "-" + field.getValue().toString();
                        }
                        html.append("<h3>" + heading_s.toUpperCase() + "</h3>\n");
                    } else {
                        System.out.println(ty + "key: " + field.getKey());
                        html.append("<td align=left style=\"word-wrap: break-word\">" + field.getKey().toUpperCase() + "</td>\n");


                    }


                }
            }

            //System.out.println("/// Object end ///");
            html.append("</table>\n");


        } else {
            // if node is a simple value (like string or int) so let's print it
            html.append("<td align=left style=\"word-wrap: break-word\">" + node + "</td>\n");
            //System.out.println("value: " + node);
            html.append("</tr>\n");
        }

    }

    @SuppressWarnings("unlikely-arg-type")
    private static void processNode_benefits_details(JsonNode node, String ty) {

        String heading_s = "";
        if (node.isArray()) {
            // if the node is a list of items,
            //  go through all the items and process them individually
            //System.out.println("=== Array start ===");

            for (final JsonNode objInArray : node) {

                // System.out.println("--- Array element start ---");
                // process the item in the array
                System.out.println("11|||||||||" + objInArray.toString());
                processNode_benefits_details(objInArray, "Arry");
                // closer=1;
                //System.out.println("--- Array element end ---");
            }

            // System.out.println("=== Array end ===");
        } else if (node.isContainerNode()) {
            // if the node is an object,
            //  go through all fields within the object
            //System.out.println("/// Object start ///");

            html.append("<table>\n");
            html.append("<tr>\n");
            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {
                int stoploop = 0;

                Map.Entry<String, JsonNode> field = it.next();


                if (field.getKey().compareTo("primaryCareProvider") == 0) {
                    System.out.println("key: " + field.getKey());
                    html.append("<h1>" + field.getKey().toUpperCase() + "</h1>\n");

                    processNode_benefits_details(field.getValue(), "object");
                } else if (field.getKey().compareTo("payerNotes") == 0) {

                    processNode_payernote(field.getValue(), "payernote");
                    stoploop = 1;

                } else if (field.getKey().compareTo("noNetwork") == 0) {

                    processNode_benefits_details(field.getValue(), "noNetwork");
                    stoploop = 1;

                } else {
                    if (ty.compareTo("object") == 0) {

                        html.append("<h1>" + field.getKey().toUpperCase() + "</h1>\n");
                    }
                    if (ty.compareTo("noNetwork") == 0) {

                        html.append("<h1>" + field.getKey().toUpperCase() + "</h1>\n");
                        // heading_s
                    } else {

                        String[] sampleArray = {"status", "insuranceType", "description", "amount", "level"};
                        System.out.println(ty + "key: " + field.getKey());
                        for (int i = 0; i < sampleArray.length; i++) {
                            if (sampleArray[i].equals(field.getKey())) {
                                //html.append("<td align=left style=\"word-wrap: break-word\">"+field.getKey().toUpperCase()+"</td>\n");
                                //html.append("<td align=left style=\"word-wrap: break-word\">"+field.getValue()+"</td>\n");

                            } else {
                                //
                            }
                        }


                        if (field.getKey().compareTo("STATUS") == 0) {

                        }
                        html.append("<td align=left style=\"word-wrap: break-word\">" + field.getKey().toUpperCase() + "</td>\n");


                    }

                    processNode_benefits_details(field.getValue(), field.getKey());
                    // html.append("<td align=left>"+field.getValue()+"</td>\n");
                    //process every field in the array
                    //System.out.println();


                }
            }

            //System.out.println("/// Object end ///");
            html.append("</table>\n");


        } else {
            // if node is a simple value (like string or int) so let's print it
            html.append("<td align=left style=\"word-wrap: break-word\">" + node + "</td>\n");
            //System.out.println("value: " + node);
            html.append("</tr>\n");
        }
    }


    private static void processNode_payernote(JsonNode node, String ty) {

        //html.append("<table>\n");
        html.append("<tr>\n");
        if (node.isArray()) {
            // if the node is a list of items,
            //  go through all the items and process them individually
            //System.out.println("=== Array start ===");

            for (final JsonNode objInArray : node) {

                // System.out.println("--- Array element start ---");
                // process the item in the array
                System.out.println("||Noiteeee||||||" + objInArray.toString());
                html.append("<td align=left>Payer Note</td>\n");
                processNode_plan(objInArray, "Arry");
                // closer=1;
                //System.out.println("--- Array element end ---");
            }

            // System.out.println("=== Array end ===");
        }
        html.append("</tr>\n");

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

            html.append("<table>\n");
            html.append("<tr>\n");
            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {


                Map.Entry<String, JsonNode> field = it.next();


                if (field.getKey().compareTo("primaryCareProvider") == 0) {
                    System.out.println("key222222222222222222222222222222: " + field.getKey());
                    html.append("<h1>" + field.getKey().toUpperCase() + "</h1>\n");

                    processNode_C(field.getValue(), "object");
                } else if (field.getKey().compareTo("benefits") == 0) {
                    System.out.println("key222222222222222222222222222222: " + field.getKey());
                    html.append("<h1>" + field.getKey().toUpperCase() + "</h1>\n");

                    processNode_plan(field.getValue(), "object");
                } else if (field.getKey().compareTo("statusDetails") == 0) {
                    System.out.println("key222222222222222222222222222222: " + field.getKey());
                    html.append("<h1>" + field.getKey().toUpperCase() + "</h1>\n");

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
            html.append("</table>\n");


        } else {
            // if node is a simple value (like string or int) so let's print it
            html.append("<td align=left  style=\"word-wrap: break-word\" >" + node + "</td>\n");
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


            html.append("<table>\n");

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
            html.append("</table>\n");
            System.out.println("/// Object end ///");
            closer = 1;


        } else {
            // if node is a simple value (like string or int) so let's print it
            html.append("<td align=left>" + node + "</td>\n");
            System.out.println("value: " + node);
            html.append("</tr>\n");
        }
    }

}

