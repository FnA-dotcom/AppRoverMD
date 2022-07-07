package smartfile;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class dbinsert {

    private static File destdir;
    private static ArrayList<String> AhomelistOfFiles = new ArrayList<String>();
    private static ArrayList<String> AremotelistOfFiles = new ArrayList<String>();
    private static ArrayList<String> AdblistOfFiles = new ArrayList<String>();

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";

        String path2 = "/sftpdrive/users/smartfile2/Physicians-Premier-Bastrop";
        String path = "/sftpdrive/users/epowerdoc/Physicians-Premier-Bastrop/";

        ArrayList<String> dbfilename = new ArrayList<String>();
        ArrayList<String> remotefilelist = new ArrayList<String>();
        ArrayList<String> homefilelist = new ArrayList<String>();
        ArrayList<String> difffilelist = new ArrayList<String>();
        int clientid = 0;
        // int numRows =0;
        // int numCols =0;
        Connection conn = getConnectionlocal();

        Query = "select id,directory_1,remotedirectory,tablename from clients where status=0 and doctype='1'";
        try {
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            System.out.println(Query);
            while (hrset.next()) {
                AhomelistOfFiles.removeAll(AhomelistOfFiles);
                AremotelistOfFiles.removeAll(AremotelistOfFiles);
                AdblistOfFiles.removeAll(AdblistOfFiles);
                difffilelist.removeAll(difffilelist);

                dbfilename = dbgetList(AdblistOfFiles, hrset.getString(1));
                remotefilelist = getList(hrset.getString(3), AremotelistOfFiles);
                homefilelist = getList(hrset.getString(2), AhomelistOfFiles);
                System.out.println(hrset.getString(1) + " Name " + hrset.getString(2) + " |Remote " + remotefilelist.size() + " |Home " + homefilelist.size() + " |DB " + dbfilename.size());
                remotefilelist.removeAll(dbfilename);


                for (int i = 0; i < remotefilelist.size(); i++) {
                    System.out.println(remotefilelist.get(i));
                    String MR[] = remotefilelist.get(i).split("_");
                    String filename = null;
                    String dosdate = null;
                    String acc = null;
                    String epowertime = null;
                    String firstname = null;
                    String lastname = null;
                    String mrn = null;
                    //String PDFData[] = new String[]
                    path = hrset.getString(2);

                    System.out.println(MR.length);

                    if (MR.length == 5) {
                        //date of service MRN ACC_ Chart date //new
                        // 405_40927_201912010629_202002141420_.pdf
                        // 405_40224_201911240413_202002121407_.pdf
                        //639_9864_202007300035_202009291115_.pdf
                        System.out.println("5 block");
                        System.out.println(MR[0] + "|" + MR[1] + "|" + MR[2] + "|" + MR[3] + "|" + MR[4]);


                        filename = remotefilelist.get(i);
                        dosdate = MR[2];
                        mrn = "";
                        acc = MR[1];
                        epowertime = MR[3];
                        String PdfData = ReadPdfGetData(filename, path);
                        if (!PdfData.equals("0")) {
//                            String lines[] = pdfFileInText.split
                        }
                        String bb = datainsert(filename, dosdate, acc, epowertime, path, hrset.getString(1));

                    }


                    if (MR.length == 8) {

                        System.out.println("8 block");
                        filename = remotefilelist.get(i);
                        dosdate = MR[5];
                        mrn = MR[1];
                        firstname = MR[2];
                        lastname = MR[3];
                        epowertime = MR[6];
                        acc = MR[4];

                        String bb = datainsert_new(filename, dosdate, acc, epowertime, path, firstname, lastname, mrn, hrset.getString(1));
                    }

                }

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    public static ArrayList<String> getList(String path, ArrayList list) {


        System.out.println("||||" + path.toString());
        File home = new File(path);
        File[] homelistOfFiles = home.listFiles();
        System.out.println(homelistOfFiles.toString());
        Arrays.sort(homelistOfFiles);
        for (int i = 0; i < homelistOfFiles.length; i++) {
            if (homelistOfFiles[i].isFile()) {
                if (homelistOfFiles[i].getName().toLowerCase().endsWith(".pdf")) {

                    list.add(homelistOfFiles[i].getName());
                }

            }
        }

        return list;
    }


    public static ArrayList<String> dbgetList(ArrayList list, String g_testdata) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        Connection conn = getConnectionlocal();

        Query = "select filename from filelogs_sftp where clientdirectory=" + g_testdata;
        try {
            hstmt = conn.createStatement();

            hrset = hstmt.executeQuery(Query);
            // System.out.println(Query);


            int dbi = 0;
            while (hrset.next()) {
                list.add(hrset.getString(1));

            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }

    private static Connection getConnectionlocal() {
        try {
            try {
                Class.forName("org.mariadb.jdbc.Driver").newInstance();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Connection connection = DriverManager.getConnection("jdbc:mysql://54.167.174.84/oe?user=abdf890092&password=980293339jjjj");

            //Connection connection = DriverManager.getConnection("jdbc:mysql://54.80.137.178/oe?user=abdf890092&password=980293339jjjj");
            return connection;
        } catch (Exception e) {
            System.out.println("PL" + e.getMessage());
            return null;
        }
    }


    private static void copyFileUsingJava7Files(String source, String dest) throws IOException {

        Path sourcePath = Paths.get(source);
        Path targetPath = Paths.get(dest);
        try {
            Files.copy(sourcePath, targetPath);
        } catch (IOException ee) {

        }
    }

    private static String datainsert(String filename, String dosdate, String mr, String epowertime, String path, String client) {
        String success = "0";
        try {
            Connection conn = getConnectionlocal();

            PreparedStatement MainReceipt1 = conn
                    .prepareStatement(" Insert IGNORE into oe.filelogs_sftp (target,clientdirectory,filename,acc,dosdate,epowertime,entrydate)" +
                            " values (?,?,?,?,?,?,now()) ");

            String temp[] = path.split("/");

            // 2019 03 11 17 52
            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmm"); // first example
            SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmm"); // second example

            Date d1 = format1.parse(dosdate);
            Date d2 = format2.parse(epowertime);

            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            String date = simpleDateFormat.format(d1);
            String date1 = simpleDateFormat.format(d2);
            System.out.println(date);
            System.out.println(date1);

            MainReceipt1.setString(1, path);
            MainReceipt1.setString(2, client);
            MainReceipt1.setString(3, filename);
            MainReceipt1.setString(4, mr);
            MainReceipt1.setString(5, date);
            MainReceipt1.setString(6, date1);

            MainReceipt1.executeUpdate();
            MainReceipt1.close();
            conn.close();
            success = "1";

        } catch (Exception ee) {
            System.out.println(ee.getLocalizedMessage());
            success = "0";
        }
        return success;

    }

    private static String datainsert_new(String filename, String dosdate, String acc, String epowertime, String path, String firstname, String lastname, String mrn, String client) {
        String success = "0";
        try {
            Connection conn = getConnectionlocal();

            PreparedStatement MainReceipt1 = conn
                    .prepareStatement(" Insert IGNORE into oe.filelogs_sftp (target,clientdirectory,filename,acc,dosdate,epowertime,entrydate,firstname,lastname,mrn)" +
                            " values (?,?,?,?,?,?,now(),?,?,?) ");

            String temp[] = path.split("/");

            // 2019 03 11 17 52
            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmm"); // first example
            SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmm"); // second example

            Date d1 = format1.parse(dosdate);
            Date d2 = format2.parse(epowertime);

            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            String date = simpleDateFormat.format(d1);
            String date1 = simpleDateFormat.format(d2);
            System.out.println(date);
            System.out.println(date1);


            MainReceipt1.setString(1, path);
            MainReceipt1.setString(2, client);
            MainReceipt1.setString(3, filename);
            MainReceipt1.setString(4, acc);
            MainReceipt1.setString(5, date);
            MainReceipt1.setString(6, date1);
            MainReceipt1.setString(7, firstname);
            MainReceipt1.setString(8, lastname);
            MainReceipt1.setString(9, mrn);

            MainReceipt1.executeUpdate();
            MainReceipt1.close();
            conn.close();
            success = "1";

        } catch (Exception ee) {
            System.out.println(ee.getLocalizedMessage());
            success = "0";
        }
        return success;

    }


    private static String ReadPdfGetData(String FileName, String Path) {
        String success = "0";
        try {
            String PatientName = "";
            String MRN = "";
            String DOB = "";
            String DOS = "";
            String Acct = "";
            String ChComplaint = "";
            try (PDDocument document = PDDocument.load(new File(Path + "/" + FileName))) {
                document.getClass();
                if (!document.isEncrypted()) {
                    PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                    stripper.setSortByPosition(true);
                    PDFTextStripper tStripper = new PDFTextStripper();
                    tStripper.getStartPage();
                    String pdfFileInText = tStripper.getText(document);

                    // split by whitespace
                    String lines[] = pdfFileInText.split("\\r?\\n");
                    ChComplaint = lines[12];
                    for (String line : lines) {
                        if (line.startsWith("Patient:")) {
                            PatientName = line;
                        }
                        if (line.startsWith("DOB:")) {
                            DOB = line;
                        }
                        if (line.startsWith("MRN:")) {
                            MRN = line;
                        }
                        if (line.startsWith("Acct #:")) {
                            Acct = line;
                        }
                        if (line.startsWith("DOS:")) {
                            DOS = line;
                        }
                    }
                }
            }
            success = PatientName + "|" + DOB + "|" + MRN + "|" + Acct + "|" + DOS + "|" + ChComplaint;
        } catch (Exception ee) {
            System.out.println(ee.getLocalizedMessage());
            success = "0";
        }
        return success;
    }

}