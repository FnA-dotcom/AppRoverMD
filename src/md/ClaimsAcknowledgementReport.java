package md;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import java.util.Vector;

public class ClaimsAcknowledgementReport {

    public static void main(String[] args) throws SQLException {
        Connection conn = null;
        ResultSet hrset = null;
        String Inst_username = null, Inst_pwd = null, Prof_username = null, Prof_pwd = null;
        PreparedStatement ps = null;

        String DIRECTORY = "schertz";
        String SFTP_DIR = "/ReceiveFiles/";
        String EDI_DIR = "schertz";
        String DATABASE = "schertz";
        String claimIdx = null;
        String EDI_FILE_NAME = null;
//        String EDI_FILE_NAME = "EDI_Test.txt";

//        System.out.println("args " + args[0]);

        try {


            conn = getConnection();
            System.out.println("Connection: " + conn);

            ps = conn.prepareStatement("SELECT Inst_Username_Availity,Inst_Pwd_Availity,dbname,DirectoryName,Prof_Username_Availity,Prof_Pwd_Availity FROM oe.clients WHERE id=39");//facilityIdx
            hrset = ps.executeQuery();
            if (hrset.next()) {
                Inst_username = hrset.getString(1);
                Inst_pwd = hrset.getString(2);
                DATABASE = hrset.getString(3);
                DIRECTORY = hrset.getString(4);
                Prof_username = hrset.getString(5);
                Prof_pwd = hrset.getString(6);
            }
            hrset.close();
            ps.close();

            System.out.println("Connecting ChannelSftp");
            final ChannelSftp Inst_ch = setupJsch(Inst_username, Inst_pwd);
            final ChannelSftp Prof_ch = setupJsch(Prof_username, Prof_pwd);
            System.out.println("Connected ChannelSftp");


            readSftpFromPath(SFTP_DIR, Prof_ch,DATABASE,conn,"Professional");
            readSftpFromPath(SFTP_DIR, Inst_ch,DATABASE,conn,"Institutional");

//            if (uploadFile(DIRECTORY, EDI_FILE_NAME, SFTP_FILE, Prof_ch, EDI_DIR)) {
//                updateSentStatus(conn, DATABASE, claimIdx);
//
//            }


            Inst_ch.exit();
            Prof_ch.exit();
            System.exit(1);

        } catch (Exception e) {
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println(str);

        }


    }

    private static ChannelSftp setupJsch(String _username, String _pwd) throws JSchException {
//        String username = "SchertzInst";
        final String username = _username;
//        String password = "7Br0Wf09#iJGhy";
        final String password = _pwd;
        String host = "ftp.availity.com";
        JSch jsch = new JSch();
        Session jschSession = jsch.getSession(username, host);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        jschSession.setConfig(config);
        jschSession.setPassword(password);
        jschSession.setPort(9922);
        jschSession.connect();
        return (ChannelSftp) jschSession.openChannel("sftp");
    }

    private static boolean readSftpFromPath(String sftpDir, ChannelSftp newch, String DATABASE, Connection conn,String ClaimType) throws JSchException, SftpException, IOException {
        newch.connect();
        int claimCounter=0;
        newch.cd(sftpDir);
        Vector filelist = newch.ls(sftpDir);
        for(int i=0; i<filelist.size();i++){
            ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) filelist.get(i);
            if (entry.getFilename().startsWith("EBR") && isNotRead(conn,DATABASE,entry.getFilename(),ClaimType)) {
                Scanner myReader = new Scanner( newch.get(sftpDir+"/"+entry.getFilename()));
                String data = "";
                String fileControllNumber = null;
                String PCN = null;
                String Comment = null;
                String[] dataArr = null;
                int lineCounter=1;
                while (myReader.hasNextLine()) {
                    data = myReader.nextLine();
                    dataArr = data.split("\\|");
                    if(lineCounter==1)
                        fileControllNumber = dataArr[5];

                    if(lineCounter==3) {
                        PCN = dataArr[4];
                    }

                    if (lineCounter == 4) {
                        if (dataArr[0].compareTo("3c") == 0) {
                            Comment = dataArr[4];
                            claimCounter++;
                            System.out.println("fileControllNumber ->  " + fileControllNumber);
                            System.out.println("PCN ->  " + PCN);
                            System.out.println("Comment ->  " + Comment);
                            lineCounter = 0;
                        }else if(dataArr[0].compareTo("3e") == 0 && !myReader.hasNextLine()){
                            Comment = dataArr[4];
                            System.out.println("Comment ->  " + Comment);
                            lineCounter=0;
                            claimCounter++;
                        }else if(dataArr[0].compareTo("3e") == 0){
                            while(myReader.hasNextLine()){
                                data = myReader.nextLine();
                                dataArr = data.split("\\|");
                                Comment = dataArr[4];
                                System.out.println("Comment ->  " + Comment);
                            }
                        }
                    }
                    data+=data;
                    lineCounter++;
                }
                myReader.close();
                insertFileName(entry.getFilename(),DATABASE,conn,data,ClaimType);
                System.out.println("claimCounter ->  "+claimCounter);
                data="";
            }

        }
        return true;
    }

    private static boolean isNotRead(Connection conn, String database, String Filename,String ClaimType) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM "+database+".Claim_Acknowledgementfiles WHERE FileName=? AND isRead=1 AND ClaimType=?");
            ps.setString(1,Filename);
            ps.setString(2,ClaimType);
            ResultSet rset = ps.executeQuery();
            if(rset.next()){
                if(rset.getInt(1)==0);
                    return true;
            }
            rset.close();
            ps.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void insertFileName(String Filename, String database, Connection conn, String Content,String ClaimType) {
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO "+database+".Claim_Acknowledgementfiles (Filename,Content,isRead,ReadAt,ClaimType) VALUES(?,?,1,NOW(),?)");
            ps.setString(1,Filename);
            ps.setString(2,Content);
            ps.setString(3,ClaimType);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            return DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986&autoReconnect=true");
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

}
