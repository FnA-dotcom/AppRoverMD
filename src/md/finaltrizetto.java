package md;

import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class finaltrizetto {


    /*public static String GediPayerID = null;
    public static String NPI = null;
    public static String FirstName = null;
    public static String LastName = null;
    public static String InsuranceNum = null;
    public static String DOB = null;
    public static String Gender = null;
    public static String GPN = null; //Group No */
    //   public static ByteArrayOutputStream outputHtml = new ByteArrayOutputStream();
    public static String finalResponse = null;
    // SAAJ - SOAP Client Testing
    public static void main(String args[]) {
        /*
            The example below requests from the Web Service at:
             http://www.webservicex.net/uszip.asmx?op=GetInfoByCity


            To call other WS, change the parameters below, which are:
             - the SOAP Endpoint URL (that is, where the service is responding from)
             - the SOAP Action

            Also change the contents of the method createSoapEnvelope() in this class. It constructs
             the inner part of the SOAP envelope that is actually sent.
         */
        String soapEndpointUrl = "https://services.gatewayedi.com/eligibility/service.asmx";
        String soapAction = "GatewayEDI.WebServices";

        // callSoapWebService(soapEndpointUrl, soapAction);
    }



    public static void callSoapWebService(String soapEndpointUrl, String soapAction, String GediPayerID,String NPI,String FirstName,String LastName,String InsuranceNum,String DOB,String Gender,String GPN  ) {
        try {

            // outputHtml.reset();
            String GPNmsg="";
            // Create SOAP Connection
            if(GPN!=null)
            {
                if(GPN.length()!=0)
                {
                    GPNmsg="<gat:MyNameValue><gat:Name>GroupNumber</gat:Name><gat:Value>"+GPN+"</gat:Value></gat:MyNameValue>";
                }

            }
            String msg="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:gat=\"GatewayEDI.WebServices\">"+
                    "<soapenv:Header><gat:AuthSOAPHeader><gat:User>4FRG</gat:User><gat:Password>waz3adro</gat:Password></gat:AuthSOAPHeader></soapenv:Header>"+
                    "<soapenv:Body><gat:DoInquiry><gat:Inquiry><gat:Parameters>"+
                    "<gat:MyNameValue><gat:Name>GediPayerID</gat:Name><gat:Value>"+GediPayerID+"</gat:Value></gat:MyNameValue>"+
                    "<gat:MyNameValue><gat:Name>NPI</gat:Name><gat:Value>"+NPI+"</gat:Value></gat:MyNameValue>"+
                    "<gat:MyNameValue><gat:Name>ProviderLastName</gat:Name><gat:Value>GOLDEN TRIANGLE EMERGENCY PHYSICIANS</gat:Value></gat:MyNameValue>"+
                    "<gat:MyNameValue><gat:Name>InsuredFirstName</gat:Name><gat:Value>"+FirstName+"</gat:Value></gat:MyNameValue>"+
                    "<gat:MyNameValue><gat:Name>InsuredLastName</gat:Name><gat:Value>"+LastName+"</gat:Value></gat:MyNameValue>"+
                    "<gat:MyNameValue><gat:Name>InsuranceNum</gat:Name><gat:Value>"+InsuranceNum+"</gat:Value></gat:MyNameValue>"+
                    "<gat:MyNameValue><gat:Name>InsuredDob</gat:Name><gat:Value>"+DOB+"</gat:Value></gat:MyNameValue>"+
                    GPNmsg+
                    "<gat:MyNameValue><gat:Name>InsuredGender</gat:Name><gat:Value>"+Gender+"</gat:Value></gat:MyNameValue>"+
                    "<gat:MyNameValue><gat:Name>ServiceTypeCode</gat:Name><gat:Value>52</gat:Value></gat:MyNameValue>"+
                    "</gat:Parameters><gat:ResponseDataType>Xml</gat:ResponseDataType></gat:Inquiry></gat:DoInquiry></soapenv:Body></soapenv:Envelope>";



            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction,msg), soapEndpointUrl);

            // Print the SOAP Response
            System.out.println("Response SOAP Message:");
            soapResponse.writeTo(System.out);
            //soapResponse.writeTo("Host:services.gatewayedi.com\r\n");
            System.out.println();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.reset();
            soapResponse.writeTo(out);
            String strMsg = new String(out.toByteArray());
            //  strMsg=strMsg.replace("/(&lt;)/g","<").replace("/(&gt;)/g",">");
            System.out.println(replaceChars(strMsg));

            soapConnection.close();

            eiresponse.parmsg(strMsg);






        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
        }
    }






    private static SOAPMessage createSOAPRequest(String soapAction,String msg) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();




        InputStream is = new ByteArrayInputStream(msg.getBytes());
        SOAPMessage request = MessageFactory.newInstance().createMessage(null, is);

        MimeHeaders headers = request.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction+"/DoInquiry");
        request.saveChanges();
      
        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        request.writeTo(System.out);
        System.out.println("\n");

        return request;
    }


    private static String replaceChars(String xml)
    {
        // xml = xml.replaceAll("(?:<)(?<=<)(\/?\w*)(?=.*(?<=<\/html))(?:>)", "&lt;$1&gt;");
        xml = xml.replaceAll("&lt;","<");
        xml = xml.replaceAll("&gt;",">");
        return xml;
    }

}