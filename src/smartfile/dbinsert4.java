package smartfile;

import org.apache.commons.lang3.StringUtils;
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


public class dbinsert4 {

    static String PatientName = "";
    static String MRN = "";
    static String DOB = "";
    static String DOS = "";
    static String Acct = "";
    static String ChComplaint = "";
    static String firstname = "";
    static String lastname = "";
    static String printabledate = "";
    static String Age = "";
    static String gender = "";
    private static File destdir;
    private static ArrayList<String> AhomelistOfFiles = new ArrayList<String>();
    private static ArrayList<String> AremotelistOfFiles = new ArrayList<String>();
    private static ArrayList<String> AdblistOfFiles = new ArrayList<String>();

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String Database = "";

        String path2 = "";
        String path = "";

        ArrayList<String> dbfilename = new ArrayList<String>();
        ArrayList<String> dbpatient = new ArrayList<String>();
        ArrayList<String> remotefilelist = new ArrayList<String>();
        ArrayList<String> homefilelist = new ArrayList<String>();
        ArrayList<String> difffilelist = new ArrayList<String>();
        int clientid = 0;
        // int numRows =0;
        // int numCols =0;
        Connection conn = getConnectionlocal();

        Query = "select id,directory_1,remotedirectory,tablename,dbname from clients where status=0 and doctype='3' and id=10";
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
                //remotefilelist=getList(hrset.getString(3),AremotelistOfFiles);
                remotefilelist = getList(hrset.getString(2), AremotelistOfFiles);

                homefilelist = getList(hrset.getString(2), AhomelistOfFiles);
                System.out.println(hrset.getString(1) + " Name " + hrset.getString(2) + " |Remote " + remotefilelist.size() + " |Home " + homefilelist.size() + " |DB " + dbfilename.size());
                remotefilelist.removeAll(dbfilename);

                Database = hrset.getString(5);
                clientid = hrset.getInt(1);
                dbpatient = dbgetpatient(dbpatient, Database);


                for (int i = 0; i < remotefilelist.size(); i++) {
                    boolean readable = false;
                    String filename = null;
                    filename = remotefilelist.get(i);
                    path = hrset.getString(2);

                    //Reaable not
                    readable = ReadPdfGetData(filename, path);

                    if (readable) {


                        System.out.println(filename + "|" + DOS + "|" + Acct + "|" + printabledate + "|" + path + "|" + firstname + "|" + lastname + "|" + MRN + "|" + hrset.getString(1));
                        String bb = datainsert_new(filename, DOS, Acct, printabledate, path, firstname, lastname, MRN, hrset.getString(1));
                        String ExtendedMRN = clientid * 1000 + "" + MRN;


                        String dd = datainsert_new_PatientReg(filename, Age, DOB, gender, ExtendedMRN, ChComplaint, DOS, Acct, printabledate, path, firstname, lastname, MRN, Integer.toString(clientid), Database, dbpatient);


                    } else {


                        System.out.println(remotefilelist.get(i));
                        String MR[] = remotefilelist.get(i).split("_");

                        String dosdate = null;
                        String acc = null;
                        String epowertime = null;
                        String firstname = null;
                        String lastname = null;
                        String mrn = null;
                        //String PDFData[] = new String[]


                        System.out.println(MR.length);

                        if (MR.length == 5) {
                            //date of service MRN ACC_ Chart date //new
                            // 405_40927_201912010629_202002141420_.pdf
                            // 405_40224_201911240413_202002121407_.pdf
                            //639_9864_202007300035_202009291115_.pdf
                            System.out.println("5 block");
                            System.out.println(MR[0] + "|" + MR[1] + "|" + MR[2] + "|" + MR[3] + "|" + MR[4]);

                            dosdate = MR[2];
                            mrn = "";
                            acc = MR[1];
                            epowertime = MR[3];

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

    public static ArrayList<String> dbgetpatient(ArrayList list2, String Database) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        Connection conn = getConnectionlocal();

        Query = "select concat(firstname,'|',lastname,'|',mrn) from " + Database + ".PatientReg ";
        try {
            hstmt = conn.createStatement();

            hrset = hstmt.executeQuery(Query);
            // System.out.println(Query);


            int dbi = 0;
            while (hrset.next()) {
                list2.add(hrset.getString(1));

            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list2;
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
            MainReceipt1.setString(5, dosdate);
            MainReceipt1.setString(6, epowertime);
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


    private static String datainsert_new_PatientReg(String filename, String Age, String DOB, String gender, String ExtendedMRN, String ReasonVisit, String dosdate, String acc, String epowertime, String path, String firstname, String lastname, String mrn, String client, String Database, ArrayList plist) {
        String success = "0";
        Connection conn = getConnectionlocal();
        int PatientRegId = 0;
        try {


            if (plist.contains(firstname + "|" + lastname + "|" + mrn)) {
                System.out.println("already exsit");
            } else {


                System.out.println(firstname + "|" + lastname + "|" + mrn);
                plist.add(firstname + "|" + lastname + "|" + mrn);
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".PatientReg "
                        + "(ClientIndex,FirstName,LastName ,DOB,Age,Gender, ReasonVisit,CreatedBy, MRN, Status,DateofService, ExtendedMRN,MiddleInitial,PhNumber,CreatedDate) "
                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?, now()) ", Statement.RETURN_GENERATED_KEYS);
                MainReceipt.setString(1, client);
                MainReceipt.setString(2, firstname);
                MainReceipt.setString(3, lastname);
                MainReceipt.setString(4, DOB);
                MainReceipt.setString(5, Age);
                if (gender.compareTo("M") == 0) {
                    gender = "male";
                } else {
                    gender = "female";
                }

                MainReceipt.setString(6, gender);
                MainReceipt.setString(7, ReasonVisit);
                MainReceipt.setString(8, "Auto");
                MainReceipt.setString(9, mrn);
                MainReceipt.setString(10, "0");
                MainReceipt.setString(11, dosdate);
                MainReceipt.setString(12, ExtendedMRN);
                MainReceipt.setString(13, "");
                MainReceipt.setString(14, "");
                // MainReceipt.executeUpdate();
                // MainReceipt.close();
                int affectedRows = MainReceipt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }

                try {

                    ResultSet rs = MainReceipt.getGeneratedKeys();
                    if (rs.next()) {
                        PatientRegId = rs.getInt(1);

                    }
                    MainReceipt.close();

                    String cc = datainsert_new_Patient_visit(ReasonVisit, dosdate, acc, epowertime, mrn, Database, PatientRegId);
                    success = "1";
                } catch (Exception ee) {
                    System.out.println(ee.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error 2- Insertion PatientReg Table :" + e.getMessage());
            return e.getMessage();
        }
        return success;
    }


    private static String datainsert_new_Patient_visit(String ReasonVisit, String dosdate, String acc, String epowertime, String mrn, String Database, int PatientRegId) {
        String success = "0";
        Connection conn = getConnectionlocal();
        try {
            System.out.println(Database + "###############PatientRegId ####" + PatientRegId);
            final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".PatientVisit"
                    + "(MRN,PatientRegId,ReasonVisit,VisitNumber,DoctorId,DateofService,CreatedBy,CreatedDate) VALUES (?,?,?,?,?,?,?,now()) ");
            MainReceipt.setString(1, MRN);
            MainReceipt.setInt(2, PatientRegId);
            MainReceipt.setString(3, ReasonVisit);
            MainReceipt.setString(4, Acct);
            MainReceipt.setString(5, "0");
            MainReceipt.setString(6, dosdate);
            MainReceipt.setString(7, "Auto");

            MainReceipt.executeUpdate();
            MainReceipt.close();
        } catch (Exception e) {
            System.out.println("Error 3.1 Insertion in table PatientVisit- :" + e.getMessage());
        }
        return success;
    }

    private static boolean ReadPdfGetData(String FileName, String Path) {

        try {
            gender = "";
            Age = "";
            MRN = "";
            DOB = "";
            DOS = "";
            Acct = "";
            ChComplaint = "";
            firstname = "";
            lastname = "";
            printabledate = "";

            try (PDDocument document = PDDocument.load(new File(Path + "/" + FileName))) {
                //document.getPage(0);
                document.getClass();

                if (!document.isEncrypted()) {

                    PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                    stripper.setSortByPosition(true);

                    PDFTextStripper tStripper = new PDFTextStripper();
                    tStripper.getStartPage();

                    String pdfFileInText = tStripper.getText(document);
//                System.out.println("pdfFileInText: " + pdfFileInText);
                    int ii = 0;
                    int iChComplaint = 0;
                    // split by whitespace
                    String lines[] = pdfFileInText.split("\\r?\\n");

                    for (String line : lines) {

                        if (line.startsWith("Patient:")) {
                            String PatientNameRaw = line;
                            String PtArr[] = PatientNameRaw.split("\\s+");
                            lastname = PtArr[1];
                            lastname = StringUtils.chop(lastname);
                            firstname = PtArr[2];
                        }
                        if (line.startsWith("DOB:")) {
                            String DOBRaw = line;
                            String DOBArr[] = DOBRaw.split("\\s+");
                            DOB = DOBArr[1];
                            String DOBFormatArr[] = DOB.split("\\/");
                            DOB = DOBFormatArr[2] + "-" + DOBFormatArr[0] + "-" + DOBFormatArr[1];
                            Age = DOBArr[2];
                            String genderAR[] = DOBArr[3].split("/");
                            gender = genderAR[1];
                        }
                        if (line.startsWith("MRN:")) {
                            String MRNRaw = line;
                            String MRNAR[] = MRNRaw.split("\\s+");
                            MRN = MRNAR[1];


                        }
                        if (line.startsWith("Acct #:")) {
                            String AcctRaw = line;
                            String AcctArr[] = AcctRaw.split("\\s+");
                            Acct = AcctArr[2];
                            String printabledateRaw = AcctArr[4];
                            String printabledateArr[] = printabledateRaw.split("\\/");
                            printabledate = printabledateArr[2] + "-" + printabledateArr[0] + "-" + printabledateArr[1] + " " + AcctArr[5] + ":00";
                        }
                        if (line.startsWith("DOS:")) {
                            String DOSRaw = line;
                            String DOSArr[] = DOSRaw.split("\\s+");
                            DOS = DOSArr[1];
                            String DOSFormatArr[] = DOS.split("\\/");
                            DOS = DOSFormatArr[2] + "-" + DOSFormatArr[0] + "-" + DOSFormatArr[1] + " " + DOSArr[2] + ":00";
                        }
                        if (line.startsWith("CHIEF COMPLAINT:")) {

                            iChComplaint = ii + 1;
                        }
                        if (iChComplaint == ii) {
                            ChComplaint = line;
                        }

                        if (MRN.matches("[a-zA-Z]+")) {
                            MRN = "0";
                        }

                        ii++;
                    }
                } else {
                    return false;

                }
            }

        } catch (Exception ee) {
            System.out.println(ee.getLocalizedMessage());
            return false;

        }
        return true;
    }

    public static boolean isStringOnlyAlphabet(String str) {
        return ((str != null) && (!str.equals("")) && (str.matches("^[a-zA-Z]*$")));
    }
}