package md;

import com.jcraft.jsch.*;

import java.io.FileWriter;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Date;

public class lab01 {

    public static void main(String[] args) {

        String Filepath = "e:\\temp\\";
        String orderid = "402575";
        String mrn = "95147522";
        String orderdate = "20220215";
        String ordertime = "1030";
        String PID_3_mrn = "9874520";
        String Firstname = "John";
        String Lastname = "Doe Seventh";

        String DOB = "19951025"; //YYYYMMDD
        String Gender = "M"; //F or M
        String NPI = "1234567890";
        String DR_Firstname = "PROVIDER";
        String DR_Lastname = "TEST";
        String Barcode = "789405714";


        // B 		AFRICAN AMERICAN
        //A 		ASIAN
        //C 		CAUCASIAN
        //H		HISPANIC
        //I		NATIVE AMERICAN
        String Race = "A";
        String Address = "17154 butte creek";
        String City = "Houston";
        String State = "TX";
        String zipcode = "74986";
        String hphone = "4694980033";
        String wphone = "";
        String Maritalstatus = "M";
        //S		SINGLE
        //M		MARRIED
        //D		DIVORCED
        //W		WIDOWED
        //X		SEPARATED
        String Ethinicity = "H";

        try {

            String FileName = Filepath + "" + orderid + "_" + mrn + "_" + orderdate + "_" + ordertime + "_" + GetFileNamedate() + ".HL7";
            String MSH = "MSH|^~\\&|Test RS|TESTRS|TESTRS|Primescope Diagnostics|" + orderdate + "" + ordertime + "||ORM^O01|6621488399|P|2.4";
            String PID = "PID|1|" + PID_3_mrn + "|" + mrn + "||" + Lastname + "^" + Firstname + "||" + DOB + "|" + Gender + "||" + Race + "|" + Address + "^^" + City + "^" + State + "^" + zipcode + "||" + hphone + "||||||||" + Ethinicity + "|";
            String ZUD = "ZUD|1|R|" + Barcode + "|Barcode ID";
            String IN1 = "IN1|1|TEST|123456|TEST INSURANCE|Street No.7^Highway 5^Albany^NY^12202|(123)456-7890|(123)456-7890|ABC12345678||||20210208|20220221||||Self|09/12/1998||||||||||||||||||xyz123456|";
            String ORC = "ORC|NW|" + orderid + "||||||Test RS|" + orderdate + "" + ordertime + "|||" + NPI + "^" + DR_Lastname + "^" + DR_Firstname + "^^^^^NPI|TESTRS|||||||||||Street No. 5 4498^^Albany^NY^12202";
            String OBR = "OBR|1|" + orderid + "||2090|R|" + orderdate + "" + ordertime + "|" + orderdate + "" + ordertime + "||||";

            String Msg = MSH;
            //Msg=Msg+"\n"+MSH+"\n";
            Msg = Msg + "\n" + PID;
            Msg = Msg + "\n" + ZUD;
            Msg = Msg + "\n" + IN1;
            Msg = Msg + "\n" + ORC;
            Msg = Msg + "\n" + OBR;

            System.out.println(Msg);
            writefile(Msg, FileName);
            String sftpFile = "/Integration/Queue/";
            ChannelSftp newch = setupJsch();
            boolean isuploaded = uploadSftpFromPath(FileName, sftpFile);
            System.out.println(isuploaded);

        } catch (Exception e) {
            // TODO: handle exception
        }

    }


    public static void writefile(String data, String FileName) {


        try {

            FileWriter fr = new FileWriter(FileName, true);
            fr.write(data);
            fr.flush();
            fr.close();

        } catch (Exception exception) {
        }
    }

    private static ChannelSftp setupJsch() throws JSchException {
        String username = "lab-integration";
        String password = "7Br0Wf09#iJGhy";
        String host = "sftp.rovermd.com";
        JSch jsch = new JSch();
        Session jschSession = jsch.getSession(username, host);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        jschSession.setConfig(config);
        jschSession.setPassword(password);
        jschSession.connect();
        return (ChannelSftp) jschSession.openChannel("sftp");
    }


    public static boolean uploadSftpFromPath(String localFile, String sftpFile) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = setupJsch();
        } catch (JSchException e) {
            // throw the exception
        }
        try {
            channelSftp.connect();
        } catch (JSchException e) {
            // throw the exception
        }
        try {
            channelSftp.put(localFile, sftpFile);
            System.out.println("Upload Complete");
        } catch (SftpException e) {
            // throw the exception
        }
        channelSftp.exit();
        return true;
    }

    private static String GetFileNamedate() {
        try {
            Date date = GetDate();
            DecimalFormat decimalformat = new DecimalFormat("#00");
            return decimalformat.format(date.getYear() + 1900) + "" + decimalformat.format(date.getMonth() + 1) + "" + decimalformat.format(date.getDate()) + "_" + decimalformat.format(date.getHours()) + "" + decimalformat.format(date.getMinutes()) + "" + decimalformat.format(date.getSeconds());
        } catch (Exception exception) {
            return "invalid filename " + exception.getMessage();
        }
    }

    private static Date GetDate() {
        try {
            Date date = new Date();
            return date;
        } catch (Exception _ex) {
            return null;
        }
    }

    public boolean uploadSftpFromInputStream(InputStream localFile, String sftpFile) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = setupJsch();
        } catch (JSchException e) {
            // throw the exception
        }
        try {
            channelSftp.connect();
        } catch (JSchException e) {
            // throw the exception
        }
        try {
            channelSftp.put(localFile, sftpFile);
            System.out.println("Upload Complete");
        } catch (SftpException e) {
            // throw the exception
        }
        channelSftp.exit();
        return true;
    }


}
