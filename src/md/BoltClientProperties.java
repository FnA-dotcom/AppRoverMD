package md;


import java.util.Properties;


public class BoltClientProperties {
    private String baseUrl;
    private String apiKey;
    private String merchantId;
    private String hsn;
    private boolean forceConnect;
    private boolean readCardDisplayAmount;
    private boolean readCardIncludeSignature;
    private boolean readCardIncludePin;
    private boolean readCardBeep;
    private String readCardAid;
    private boolean readManualIncludeSignature;
    private boolean readManualIncludeExpirationDate;
    private boolean readManualBeep;
    private boolean authCardIncludeSignature;
    private boolean authCardDisplayAmount;
    private boolean authCardBeep;
    private String authCardAuthMerchId;
    private String authCardAid;
    private boolean authCardIncludeAvs;
    private boolean authCardIncludePin;
    private boolean authCardCapture;
    private boolean authManualIncludeSignature;
    private boolean authManualDisplayAmount;
    private boolean authManualBeep;
    private String authManualAuthMerchId;
    private boolean authManualIncludeAvs;
    private boolean authManualIncludeCvv;
    private boolean authManualCapture;

    public BoltClientProperties(Properties properties) {
        this.baseUrl = properties.getProperty("base.url");
        this.apiKey = properties.getProperty("api.key");
        this.merchantId = properties.getProperty("merchant.id");
        this.hsn = properties.getProperty("hsn");
        this.forceConnect = Boolean.parseBoolean(properties.getProperty("connect.force"));
        this.readCardDisplayAmount = Boolean.valueOf(properties.getProperty("readcard.amount.display")).booleanValue();
        this.readCardIncludeSignature = Boolean.valueOf(properties.getProperty("readcard.include.signature")).booleanValue();
        this.readCardIncludePin = Boolean.valueOf(properties.getProperty("readcard.include.pin")).booleanValue();
        this.readCardBeep = Boolean.valueOf(properties.getProperty("readcard.beep")).booleanValue();
        this.readCardAid = properties.getProperty("readcard.aid");
        this.readManualIncludeSignature = Boolean.valueOf(properties.getProperty("readmanual.include.signature")).booleanValue();
        this.readManualIncludeExpirationDate = Boolean.valueOf(properties.getProperty("readmanual.expiration.date")).booleanValue();
        this.readManualBeep = Boolean.valueOf(properties.getProperty("readmanual.beep")).booleanValue();
        this.authCardIncludeSignature = Boolean.valueOf(properties.getProperty("authcard.include.signature")).booleanValue();
        this.authCardDisplayAmount = Boolean.valueOf(properties.getProperty("authcard.amount.display")).booleanValue();
        this.authCardBeep = Boolean.valueOf(properties.getProperty("authcard.beep")).booleanValue();
        this.authCardAuthMerchId = properties.getProperty("authcard.auth.merchant.id");
        this.authCardAid = properties.getProperty("authcard.aid");
        this.authCardIncludeAvs = Boolean.valueOf(properties.getProperty("authcard.include.avs")).booleanValue();
        this.authCardIncludePin = Boolean.valueOf(properties.getProperty("authcard.include.pin")).booleanValue();
        this.authCardCapture = Boolean.valueOf(properties.getProperty("authcard.capture")).booleanValue();
        this.authManualIncludeSignature = Boolean.valueOf(properties.getProperty("authmanual.include.signature")).booleanValue();
        this.authManualDisplayAmount = Boolean.valueOf(properties.getProperty("authmanual.amount.display")).booleanValue();
        this.authManualBeep = Boolean.valueOf(properties.getProperty("authmanual.beep")).booleanValue();
        this.authManualAuthMerchId = properties.getProperty("authmanual.auth.merchant.id");
        this.authManualIncludeAvs = Boolean.valueOf(properties.getProperty("authmanual.include.avs")).booleanValue();
        this.authManualIncludeCvv = Boolean.valueOf(properties.getProperty("authmanual.include.cvv")).booleanValue();
        this.authManualCapture = Boolean.valueOf(properties.getProperty("authmanual.capture")).booleanValue();
    }


    public String getBaseUrl() {
        return this.baseUrl;
    }


    public String getApiKey() {
        return this.apiKey;
    }


    public String getMerchantId() {
        return this.merchantId;
    }


    public String getHsn() {
        return this.hsn;
    }


    public boolean isForceConnect() {
        return this.forceConnect;
    }


    public boolean isReadCardDisplayAmount() {
        return this.readCardDisplayAmount;
    }


    public boolean isReadCardIncludeSignature() {
        return this.readCardIncludeSignature;
    }


    public boolean isReadCardIncludePin() {
        return this.readCardIncludePin;
    }


    public boolean isReadCardBeep() {
        return this.readCardBeep;
    }


    public String getReadCardAid() {
        return this.readCardAid;
    }


    public boolean isReadManualIncludeSignature() {
        return this.readManualIncludeSignature;
    }


    public boolean isReadManualIncludeExpirationDate() {
        return this.readManualIncludeExpirationDate;
    }


    public boolean isReadManualBeep() {
        return this.readManualBeep;
    }


    public boolean isAuthCardIncludeSignature() {
        return this.authCardIncludeSignature;
    }


    public boolean isAuthCardDisplayAmount() {
        return this.authCardDisplayAmount;
    }


    public boolean isAuthCardBeep() {
        return this.authCardBeep;
    }


    public String getAuthCardAuthMerchId() {
        return this.authCardAuthMerchId;
    }


    public String getAuthCardAid() {
        return this.authCardAid;
    }


    public boolean isAuthCardIncludeAvs() {
        return this.authCardIncludeAvs;
    }


    public boolean isAuthCardIncludePin() {
        return this.authCardIncludePin;
    }


    public boolean isAuthCardCapture() {
        return this.authCardCapture;
    }


    public boolean isAuthManualIncludeSignature() {
        return this.authManualIncludeSignature;
    }


    public boolean isAuthManualDisplayAmount() {
        return this.authManualDisplayAmount;
    }


    public boolean isAuthManualBeep() {
        return this.authManualBeep;
    }


    public String getAuthManualAuthMerchId() {
        return this.authManualAuthMerchId;
    }


    public boolean isAuthManualIncludeAvs() {
        return this.authManualIncludeAvs;
    }


    public boolean isAuthManualIncludeCvv() {
        return this.authManualIncludeCvv;
    }


    public boolean isAuthManualCapture() {
        return this.authManualCapture;
    }
}
