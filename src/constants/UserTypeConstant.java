package constants;

public enum UserTypeConstant {

    AR("1"),QA("2"),POSTING("3"),
    FACILITY_USER("4"),BILLERS("6"),FAM_USERS("7"),SUPER_ADMIN("9"),
    ADVOCATES("10"),LOCATION_USERS("11");
    private String value;

    UserTypeConstant(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
