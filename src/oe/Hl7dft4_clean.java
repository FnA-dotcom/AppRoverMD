// 
// Decompiled by Procyon v0.5.36
// 

package oe;

import ca.uhn.hl7v2.model.*;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.SegmentFinder;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.ValidationContext;
import ca.uhn.hl7v2.validation.impl.NoValidation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class Hl7dft4_clean {
    static int ee;
    static StringBuffer FTI1;
    static StringBuffer FTI2;
    static StringBuffer FTIfinal;
    static StringBuffer DGI1;
    static StringBuffer DGI2;
    static StringBuffer DGIfinal;
    static StringBuffer OBX1;
    static StringBuffer OBX2;
    static StringBuffer OBXfinal;
    static StringBuffer PID1;
    static StringBuffer PID2;
    static StringBuffer PIDfinal;
    static StringBuffer IN11;
    static StringBuffer IN12;
    static StringBuffer IN1final;
    static StringBuffer PV11;
    static StringBuffer PV12;
    static StringBuffer PV1final;
    static StringBuffer GT11;
    static StringBuffer GT12;
    static StringBuffer GT1final;

    static {
        Hl7dft4_clean.ee = 0;
        Hl7dft4_clean.FTI1 = new StringBuffer();
        Hl7dft4_clean.FTI2 = new StringBuffer();
        Hl7dft4_clean.FTIfinal = new StringBuffer();
        Hl7dft4_clean.DGI1 = new StringBuffer();
        Hl7dft4_clean.DGI2 = new StringBuffer();
        Hl7dft4_clean.DGIfinal = new StringBuffer();
        Hl7dft4_clean.OBX1 = new StringBuffer();
        Hl7dft4_clean.OBX2 = new StringBuffer();
        Hl7dft4_clean.OBXfinal = new StringBuffer();
        Hl7dft4_clean.PID1 = new StringBuffer();
        Hl7dft4_clean.PID2 = new StringBuffer();
        Hl7dft4_clean.PIDfinal = new StringBuffer();
        Hl7dft4_clean.IN11 = new StringBuffer();
        Hl7dft4_clean.IN12 = new StringBuffer();
        Hl7dft4_clean.IN1final = new StringBuffer();
        Hl7dft4_clean.PV11 = new StringBuffer();
        Hl7dft4_clean.PV12 = new StringBuffer();
        Hl7dft4_clean.PV1final = new StringBuffer();
        Hl7dft4_clean.GT11 = new StringBuffer();
        Hl7dft4_clean.GT12 = new StringBuffer();
        Hl7dft4_clean.GT1final = new StringBuffer();
    }

    public static void main(final String[] args) {
        try {
            final String Filname = "Z:\\HL7\\billing\\billExport_5eb2b4d4e4b0f8792670b127_1.txt";
            final InputStream is = new FileInputStream(Filname);
            final BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            final StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append("\r\n");
                line = buf.readLine();
            }
            final String fileAsString = sb.toString();
            final PipeParser ourPipeParser = new PipeParser();
            ourPipeParser.setValidationContext((ValidationContext) new NoValidation());
            final Message hl7Message = ourPipeParser.parse(fileAsString.trim());
            extractValues(hl7Message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, String> dbgetList(final String version) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final Connection conn = getConnectionlocal();
        final ArrayList list = null;
        final HashMap<String, String> sflist = new HashMap<String, String>();
        Query = "select code,decode from segment_field where hl7v='" + version + "'";
        try {
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                sflist.put(hrset.getString(1).trim(), hrset.getString(2).trim());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sflist;
    }

    public static List<HL7Value> extractValues(final Message message) throws Exception {
        final Terser terser = new Terser(message);
        final SegmentFinder finder = terser.getFinder();
        final List<HL7Value> values = new ArrayList<HL7Value>();
        int childNr = 1;
        while (finder.hasNextChild()) {
            finder.nextChild();
            final Structure[] structures = finder.getCurrentChildReps();
            for (int i = 0; i < structures.length; ++i) {
                final Structure structure = structures[i];
                Hl7dft4_clean.IN11.delete(0, Hl7dft4_clean.IN11.length());
                Hl7dft4_clean.IN12.delete(0, Hl7dft4_clean.IN12.length());
                Hl7dft4_clean.PID1.delete(0, Hl7dft4_clean.PID1.length());
                Hl7dft4_clean.PID2.delete(0, Hl7dft4_clean.PID2.length());
                Hl7dft4_clean.DGI1.delete(0, Hl7dft4_clean.DGI1.length());
                Hl7dft4_clean.DGI2.delete(0, Hl7dft4_clean.DGI2.length());
                Hl7dft4_clean.PV11.delete(0, Hl7dft4_clean.PV11.length());
                Hl7dft4_clean.PV12.delete(0, Hl7dft4_clean.PV12.length());
                Hl7dft4_clean.FTI1.delete(0, Hl7dft4_clean.FTI1.length());
                Hl7dft4_clean.FTI2.delete(0, Hl7dft4_clean.FTI2.length());
                Hl7dft4_clean.GT11.delete(0, Hl7dft4_clean.GT11.length());
                Hl7dft4_clean.GT12.delete(0, Hl7dft4_clean.GT12.length());
                parseStructure(values, message, terser, structure, Integer.toString(childNr));
                parseStructure2(values, message, terser, structure, Integer.toString(childNr));
            }
            ++childNr;
        }
        return values;
    }

    private static void parseStructure(final List<HL7Value> values, final Message message, final Terser terser, final Structure structure, final String structureNumber) throws Exception {
        final Map<String, List<String>> nameMap = (Map<String, List<String>>) NamesUtil.getInstance().getMap();
        if (structure instanceof Segment) {
            final Segment segment = (Segment) structure;
            segment.getClass().getName().contains("PV1");
            segment.getClass().getName().contains("FT1");
            segment.getClass().getName().contains("DG1");
            segment.getClass().getName().contains("PID");
            segment.getClass().getName().contains("IN1");
            final String[] names = segment.getNames();
            for (int n = 1; n <= segment.numFields(); ++n) {
                final Type[] types = segment.getField(n);
                for (int t = 0; t < types.length; ++t) {
                    for (int nrComponents = Terser.numComponents(types[t]), c = 1; c <= nrComponents; ++c) {
                        for (int nrSub = Terser.numSubComponents(types[t], c), sc = 1; sc <= nrSub; ++sc) {
                            final String string = Terser.get(segment, n, t, c, sc);
                            final Primitive primitive = Terser.getPrimitive(types[t], c, sc);
                            String description = "-";
                            final List<String> list = nameMap.get(types[t].getName());
                            if (list != null && c - 1 < list.size()) {
                                description = list.get(c - 1);
                            }
                            final Group group = structure.getParent();
                            final Group rootGroup = (Group) structure.getMessage();
                            final String coordinates = String.valueOf(n) + "." + (t + 1) + "." + c + "." + sc;
                            if (NVL(string, "").length() != 0) {
                                final HL7Value value = new HL7Value(message.getVersion(), rootGroup.getName(), group.getName(), structure.getName(), structureNumber, names[n - 1], coordinates, types[t].getName(), description, string);
                                values.add(value);
                                if (structure.getName().compareTo("FT1") == 0) {
                                    Hl7dft4_clean.FTI1.append(" <th>" + names[n - 1] + "-" + description + "</th>");
                                    Hl7dft4_clean.FTI2.append(" <td>" + string + "</td>");
                                }
                                if (structure.getName().compareTo("DG1") == 0) {
                                    Hl7dft4_clean.DGI1.append(" <td>" + names[n - 1] + "-" + description + "</td>");
                                    Hl7dft4_clean.DGI2.append(" <td>" + string + "</td>");
                                }
                                if (structure.getName().compareTo("PID") == 0) {
                                    Hl7dft4_clean.PID1.append(" <td>" + names[n - 1] + "-" + description + "</td>");
                                    Hl7dft4_clean.PID2.append(" <td>" + string + "</td>");
                                }
                                if (structure.getName().compareTo("IN1") == 0) {
                                    Hl7dft4_clean.IN11.append(" <td>" + names[n - 1] + "-" + description + "</td>");
                                    Hl7dft4_clean.IN12.append(" <td>" + string + "<td>");
                                }
                                if (structure.getName().compareTo("PV1") == 0) {
                                    Hl7dft4_clean.PV11.append(" <th>" + names[n - 1] + "-" + description + "</th>");
                                    Hl7dft4_clean.PV12.append(" <td>" + string + "</td>");
                                }
                                if (structure.getName().compareTo("GT1") == 0) {
                                    Hl7dft4_clean.GT11.append(" <th>" + names[n - 1] + "-" + description + "</th>");
                                    Hl7dft4_clean.GT12.append(" <td>" + string + "</td>");
                                }
                                ++Hl7dft4_clean.ee;
                            }
                        }
                    }
                }
            }
            if (structure.getName().compareTo("FT1") == 0) {
                Hl7dft4_clean.FTIfinal.append("<thead>");
                Hl7dft4_clean.FTIfinal.append("<tr>");
                Hl7dft4_clean.FTIfinal.append(Hl7dft4_clean.FTI1.toString());
                Hl7dft4_clean.FTIfinal.append("</tr>");
                Hl7dft4_clean.FTIfinal.append("</thead>");
                Hl7dft4_clean.FTIfinal.append("<tbody>");
                Hl7dft4_clean.FTIfinal.append("<tr>");
                Hl7dft4_clean.FTIfinal.append(Hl7dft4_clean.FTI2.toString());
                Hl7dft4_clean.FTIfinal.append("</tr>");
                Hl7dft4_clean.FTIfinal.append("</tbody>");
            } else if (structure.getName().compareTo("DG1") == 0) {
                Hl7dft4_clean.DGIfinal.append("<tr>");
                Hl7dft4_clean.DGIfinal.append(Hl7dft4_clean.DGI1.toString());
                Hl7dft4_clean.DGIfinal.append("</tr>");
                Hl7dft4_clean.DGIfinal.append("<tr>");
                Hl7dft4_clean.DGIfinal.append(Hl7dft4_clean.DGI2.toString());
                Hl7dft4_clean.DGIfinal.append("</tr>");
            } else if (structure.getName().compareTo("PID") == 0) {
                Hl7dft4_clean.PIDfinal.append("<tr>");
                Hl7dft4_clean.PIDfinal.append(Hl7dft4_clean.PID1.toString());
                Hl7dft4_clean.PIDfinal.append("</tr>");
                Hl7dft4_clean.PIDfinal.append("<tr>");
                Hl7dft4_clean.PIDfinal.append(Hl7dft4_clean.PID2.toString());
                Hl7dft4_clean.PIDfinal.append("</tr>");
            } else if (structure.getName().compareTo("IN1") == 0) {
                Hl7dft4_clean.IN1final.append("<tr>");
                Hl7dft4_clean.IN1final.append(Hl7dft4_clean.IN11.toString());
                Hl7dft4_clean.IN1final.append("</tr>");
                Hl7dft4_clean.IN1final.append("<tr>");
                Hl7dft4_clean.IN1final.append(Hl7dft4_clean.IN12.toString());
                Hl7dft4_clean.IN1final.append("</tr>");
            } else if (structure.getName().compareTo("PV1") == 0) {
                Hl7dft4_clean.PV1final.append("<tr>");
                Hl7dft4_clean.PV1final.append("<tr>");
                Hl7dft4_clean.PV1final.append(Hl7dft4_clean.PV11.toString());
                Hl7dft4_clean.PV1final.append("</tr>");
                Hl7dft4_clean.PV1final.append("<tr>");
                Hl7dft4_clean.PV1final.append(Hl7dft4_clean.PV12.toString());
                Hl7dft4_clean.PV1final.append("</tr>");
            } else if (structure.getName().compareTo("GT1") == 0) {
                Hl7dft4_clean.GT1final.append("<tr>");
                Hl7dft4_clean.GT1final.append("<tr>");
                Hl7dft4_clean.GT1final.append(Hl7dft4_clean.GT11.toString());
                Hl7dft4_clean.GT1final.append("</tr>");
                Hl7dft4_clean.GT1final.append("<tr>");
                Hl7dft4_clean.GT1final.append(Hl7dft4_clean.GT12.toString());
                Hl7dft4_clean.GT1final.append("</tr>");
            }
        } else {
            if (!(structure instanceof Group)) {
                throw new Exception("oops, not handled yet!");
            }
            final Group group2 = (Group) structure;
            final String[] names = group2.getNames();
            for (int n = 1; n <= names.length; ++n) {
                final String name = names[n - 1];
                final Structure subStructure = group2.get(name);
                parseStructure(values, message, terser, subStructure, String.valueOf(structureNumber) + "." + n);
            }
        }
    }

    private static void parseStructure2(final List<HL7Value> values, final Message message, final Terser terser, final Structure structure, final String structureNumber) throws Exception {
        final Map<String, List<String>> nameMap = (Map<String, List<String>>) NamesUtil.getInstance().getMap();
        if (structure instanceof Segment) {
            final Segment segment = (Segment) structure;
            if (segment.getClass().getName().contains("OBX")) {
                final String[] names = segment.getNames();
                for (int n = 1; n <= segment.numFields(); ++n) {
                    final Type[] types = segment.getField(n);
                    for (int t = 0; t < types.length; ++t) {
                        for (int nrComponents = Terser.numComponents(types[t]), c = 1; c <= nrComponents; ++c) {
                            for (int nrSub = Terser.numSubComponents(types[t], c), sc = 1; sc <= nrSub; ++sc) {
                                final String string = Terser.get(segment, n, t, c, sc);
                                final Primitive primitive = Terser.getPrimitive(types[t], c, sc);
                                String description = "-";
                                final List<String> list = nameMap.get(types[t].getName());
                                if (list != null && c - 1 < list.size()) {
                                    description = list.get(c - 1);
                                }
                                final Group group = structure.getParent();
                                final Group rootGroup = (Group) structure.getMessage();
                                final String coordinates = String.valueOf(n) + "." + (t + 1) + "." + c + "." + sc;
                                if (NVL(string, "").length() != 0) {
                                    final HL7Value value = new HL7Value(message.getVersion(), rootGroup.getName(), group.getName(), structure.getName(), structureNumber, names[n - 1], coordinates, types[t].getName(), description, string);
                                    values.add(value);
                                    Hl7dft4_clean.OBXfinal.append("<p>");
                                    Hl7dft4_clean.OBXfinal.append(string);
                                    Hl7dft4_clean.OBXfinal.append("");
                                    Hl7dft4_clean.OBXfinal.append("</p>");
                                    ++Hl7dft4_clean.ee;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (!(structure instanceof Group)) {
                throw new Exception("oops, not handled yet!");
            }
            final Group group2 = (Group) structure;
            final String[] names = group2.getNames();
            for (int n = 1; n <= names.length; ++n) {
                final String name = names[n - 1];
                final Structure subStructure = group2.get(name);
                parseStructure2(values, message, terser, subStructure, String.valueOf(structureNumber) + "." + n);
            }
        }
    }

    public static String NVL(final String source, final String def) {
        if (source == null || source.length() == 0) {
            return def;
        }
        return source;
    }

    private static Connection getConnectionlocal() {
        try {
            try {
                Class.forName("org.mariadb.jdbc.Driver").newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (ClassNotFoundException e3) {
                e3.printStackTrace();
            }
            final Connection connection = DriverManager.getConnection("jdbc:mysql://54.80.137.178/oe?user=abdf890092&password=980293339jjjj");
            return connection;
        } catch (Exception e4) {
            System.out.println("PL" + e4.getMessage());
            return null;
        }
    }

    private static String datainsert(final String filename, final String dosdate, final String mr, final String epowertime, final String path, final String client) {
        String success = "0";
        try {
            final Connection conn = getConnectionlocal();
            final PreparedStatement MainReceipt1 = conn.prepareStatement(" Insert IGNORE  into oe.filelogs_sftp_test (target,clientdirectory,filename,acc,dosdate,epowertime,entrydate) values  (?,?,?,?,?,?,now()) ");
            final String[] temp = path.split("/");
            final SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmm");
            final SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmm");
            final Date d1 = format1.parse(dosdate);
            final Date d2 = format2.parse(epowertime);
            final String pattern = "yyyy-MM-dd HH:mm:ss";
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            final String date = simpleDateFormat.format(d1);
            final String date2 = simpleDateFormat.format(d2);
            System.out.println(date);
            System.out.println(date2);
            MainReceipt1.setString(1, path);
            MainReceipt1.setString(2, client);
            MainReceipt1.setString(3, filename);
            MainReceipt1.setString(4, mr);
            MainReceipt1.setString(5, date);
            MainReceipt1.setString(6, date2);
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

    private static String datainsert_new(final String filename, final String dosdate, final String acc, final String epowertime, final String path, final String firstname, final String lastname, final String mrn, final String client) {
        String success = "0";
        try {
            final Connection conn = getConnectionlocal();
            final PreparedStatement MainReceipt1 = conn.prepareStatement(" Insert IGNORE  into oe.filelogs_sftp (target,clientdirectory,filename,acc,dosdate,epowertime,entrydate,firstname,lastname,mrn) values  (?,?,?,?,?,?,now(),?,?,?) ");
            final String[] temp = path.split("/");
            final SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmm");
            final SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmm");
            final Date d1 = format1.parse(dosdate);
            final Date d2 = format2.parse(epowertime);
            final String pattern = "yyyy-MM-dd HH:mm:ss";
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            final String date = simpleDateFormat.format(d1);
            final String date2 = simpleDateFormat.format(d2);
            System.out.println(date);
            System.out.println(date2);
            MainReceipt1.setString(1, path);
            MainReceipt1.setString(2, client);
            MainReceipt1.setString(3, filename);
            MainReceipt1.setString(4, acc);
            MainReceipt1.setString(5, date);
            MainReceipt1.setString(6, date2);
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
}
