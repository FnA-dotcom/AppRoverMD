// 
// Decompiled by Procyon v0.5.36
// 

package oe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NamesUtil {
    private static NamesUtil util;
    private Map<String, List<String>> map;

    private NamesUtil() {
        this.map = new HashMap<String, List<String>>();
        this.populateHD();
        this.populateDT();
        this.populateID();
        this.populateIS();
        this.populateSI();
        this.populateVID();
        this.populatePT();
        this.populateMSG();
        this.populateTS();
        this.populateST();
        this.populateXAD();
        this.populateCE();
        this.populateCX();
        this.populateXPN();
        this.populateXCN();
        this.populateXTN();
        this.populateXON();
        this.populatePL();
        this.populatePTA();
        this.populateRMC();
        this.populateEI();
        this.populateFC();
        this.populateCK();
        this.populateCM_MSG();
        this.populateCM_PAT_ID();
        this.populatePN();
        this.populateCN();
        this.populateTN();
        this.populateNM();
        this.populateAD();
        this.populateCQ();
        this.populateCM_LICENSE_NO();
        this.populateCM_INTERNAL_LOCATION();
    }

    public static NamesUtil getInstance() {
        if (NamesUtil.util == null) {
            NamesUtil.util = new NamesUtil();
        }
        return NamesUtil.util;
    }

    private void populateST() {
        final List<String> list = new ArrayList<String>();
        list.add("single value");
        this.map.put("ST", list);
    }

    private void populateTN() {
        final List<String> list = new ArrayList<String>();
        list.add("telephone number");
        this.map.put("TN", list);
    }

    private void populateCQ() {
        final List<String> list = new ArrayList<String>();
        list.add("quantity");
        list.add("units");
        this.map.put("CQ", list);
    }

    private void populateCM_INTERNAL_LOCATION() {
        final List<String> list = new ArrayList<String>();
        list.add("nurse unit (Station)");
        list.add("Room");
        list.add("Bed");
        list.add("Facility ID");
        list.add("Bed Status");
        list.add("Etage");
        list.add("Klinik");
        list.add("Zentrum");
        this.map.put("CM_INTERNAL_LOCATION", list);
    }

    private void populateCM_LICENSE_NO() {
        final List<String> list = new ArrayList<String>();
        list.add("License Number");
        list.add("issuing state,province,country");
        this.map.put("CM_LICENSE_NO", list);
    }

    private void populateNM() {
        final List<String> list = new ArrayList<String>();
        list.add("name");
        this.map.put("NM", list);
    }

    private void populateAD() {
        final List<String> list = new ArrayList<String>();
        list.add("street address");
        list.add("other designation");
        list.add("city");
        list.add("state or province");
        list.add("zip or postal code");
        list.add("country");
        list.add("address type");
        list.add("other geographic designation");
        this.map.put("AD", list);
    }

    private void populateFC() {
        final List<String> list = new ArrayList<String>();
        list.add("Financial Class");
        list.add("Effective Date");
        this.map.put("FC", list);
    }

    private void populateCN() {
        final List<String> list = new ArrayList<String>();
        list.add("ID number");
        list.add("family name");
        list.add("given name");
        list.add("second and further given names or initials thereof");
        list.add("suffix (e.g., JR or III)");
        list.add("prefix (e.g., DR)");
        list.add("degree (e.g., MD)");
        list.add("source table");
        list.add("assigning authority");
        this.map.put("CN", list);
    }

    private void populatePN() {
        final List<String> list = new ArrayList<String>();
        list.add("family name");
        list.add("given name");
        list.add("second and further given names or initials thereof");
        list.add("suffix (e.g., JR or III)");
        list.add("prefix (e.g., DR)");
        list.add("degree (e.g., MD)");
        this.map.put("PN", list);
    }

    private void populateCM_PAT_ID() {
        final List<String> list = new ArrayList<String>();
        list.add("ID number");
        list.add("Check digit");
        list.add("Check digit scheme");
        list.add("Facility ID");
        list.add("type");
        this.map.put("CM_PAT_ID", list);
    }

    private void populateCM_MSG() {
        final List<String> list = new ArrayList<String>();
        list.add("message type");
        list.add("Trigger Event");
        this.map.put("CM_MSG", list);
    }

    private void populateCK() {
        final List<String> list = new ArrayList<String>();
        list.add("ID number");
        list.add("check digit");
        list.add("code identifying the check digit scheme employed");
        list.add("assigning authority");
        this.map.put("CK", list);
    }

    private void populateEI() {
        final List<String> list = new ArrayList<String>();
        list.add("entity identifier");
        list.add("namespace ID");
        list.add("universal ID");
        list.add("universal ID type");
        this.map.put("EI", list);
    }

    private void populatePTA() {
        final List<String> list = new ArrayList<String>();
        list.add("policy type");
        list.add("amount class");
        list.add("amount");
        this.map.put("PTA", list);
    }

    private void populateRMC() {
        final List<String> list = new ArrayList<String>();
        list.add("room type");
        list.add("amount type");
        list.add("coverage amount");
        this.map.put("RMC", list);
    }

    private void populateDT() {
        final List<String> list = new ArrayList<String>();
        list.add("date-value");
        this.map.put("DT", list);
    }

    private void populateID() {
        final List<String> list = new ArrayList<String>();
        list.add("single value");
        this.map.put("ID", list);
    }

    private void populateIS() {
        final List<String> list = new ArrayList<String>();
        list.add("code");
        list.add("description");
        this.map.put("IS", list);
    }

    private void populateSI() {
        final List<String> list = new ArrayList<String>();
        list.add("single value");
        this.map.put("SI", list);
    }

    private void populateCX() {
        final List<String> list = new ArrayList<String>();
        list.add("ID");
        list.add("check digit");
        list.add("code identifying the check digit scheme employed");
        list.add("assigning authority");
        list.add("identifier type code");
        list.add("assigning facility");
        list.add("effective date");
        list.add("expiration date");
        this.map.put("CX", list);
    }

    private void populatePT() {
        final List<String> list = new ArrayList<String>();
        list.add("processing ID");
        list.add("processing mode");
        this.map.put("PT", list);
    }

    private void populateVID() {
        final List<String> list = new ArrayList<String>();
        list.add("version ID");
        list.add("internationalization code");
        list.add("international version ID");
        this.map.put("VID", list);
    }

    private void populateMSG() {
        final List<String> list = new ArrayList<String>();
        list.add("message type");
        list.add("trigger event");
        list.add("message structure");
        this.map.put("MSG", list);
    }

    private void populateTS() {
        final List<String> list = new ArrayList<String>();
        list.add("time of an event");
        list.add("degree of precision");
        this.map.put("TS", list);
    }

    private void populateHD() {
        final List<String> list = new ArrayList<String>();
        list.add("namespace ID");
        list.add("universal ID");
        list.add("universal ID type");
        this.map.put("HD", list);
    }

    private void populateXAD() {
        final List<String> list = new ArrayList<String>();
        list.add("street address");
        list.add("other designation");
        list.add("city");
        list.add("state or province");
        list.add("zip or postal code");
        list.add("country");
        list.add("address type");
        list.add("other geographic designation");
        list.add("county/parish code");
        list.add("census tract");
        list.add("address representation code");
        list.add("address validity range");
        this.map.put("XAD", list);
    }

    private void populateCE() {
        final List<String> list = new ArrayList<String>();
        list.add("identifier");
        list.add("text");
        list.add("name of coding system");
        list.add("alternate identifier");
        list.add("alternate text");
        list.add("name of alternate coding system");
        this.map.put("CE", list);
    }

    private void populateXPN() {
        final List<String> list = new ArrayList<String>();
        list.add("family name");
        list.add("given name");
        list.add("second and further given names or initials thereof");
        list.add("suffix (e.g., JR or III)");
        list.add("prefix (e.g., DR)");
        list.add("degree (e.g., MD)");
        list.add("name type code");
        list.add("Name Representation code");
        list.add("name context");
        list.add("name validity range");
        list.add("name assembly order");
        this.map.put("XPN", list);
    }

    private void populateXCN() {
        final List<String> list = new ArrayList<String>();
        list.add("family name");
        list.add("ID number");
        list.add("family name");
        list.add("given name");
        list.add("second and further given names or initials thereof");
        list.add("suffix (e.g., JR or III)");
        list.add("prefix (e.g., DR)");
        list.add("degree (e.g., MD)");
        list.add("source table");
        list.add("assigning authority");
        list.add("name type code");
        list.add("identifier check digit");
        list.add("code identifying the check digit scheme employed");
        list.add("identifier type code");
        list.add("assigning facility");
        list.add("Name Representation code");
        list.add("name context");
        list.add("name validity range");
        list.add("name assembly order");
        this.map.put("XCN", list);
    }

    private void populatePL() {
        final List<String> list = new ArrayList<String>();
        list.add("family name");
        list.add("point of care");
        list.add("room");
        list.add("bed");
        list.add("facility");
        list.add("location status");
        list.add("person location type");
        list.add("building");
        list.add("floor");
        list.add("Location description");
        this.map.put("PL", list);
    }

    private void populateXTN() {
        final List<String> list = new ArrayList<String>();
        list.add("[(999)] 999-9999 [X99999][C any text]");
        list.add("telecommunication use code");
        list.add("telecommunication equipment type");
        list.add("Email address");
        list.add("Country Code");
        list.add("Area/city code");
        list.add("Phone number");
        list.add("Extension");
        list.add("any text");
        this.map.put("XTN", list);
    }

    private void populateXON() {
        final List<String> list = new ArrayList<String>();
        list.add("organization name");
        list.add("organization name type code");
        list.add("ID number");
        list.add("check digit");
        list.add("code identifying the check digit scheme employed");
        list.add("assigning authority");
        list.add("identifier type code");
        list.add("assigning facility ID");
        list.add("Name Representation code");
        this.map.put("XON", list);
    }

    public Map<String, List<String>> getMap() {
        return this.map;
    }
}
