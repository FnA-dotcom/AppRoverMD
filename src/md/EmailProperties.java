package md;

import java.util.Properties;

public class EmailProperties {
    private String SMTP_HOST_NAME;
    private String Port;
    private String SMTP_AUTH_USER;
    private String SMTP_AUTH_PWD;

    public EmailProperties(Properties properties){
        this.SMTP_HOST_NAME = properties.getProperty("HostName");
        this.Port = properties.getProperty("Port");
        this.SMTP_AUTH_USER = properties.getProperty("AuthUser");
        this.SMTP_AUTH_PWD = properties.getProperty("AuthPWD");
    }

    public String getSMTP_HOST_NAME() {
        return this.SMTP_HOST_NAME;
    }

    public String getPort() {
        return Port;
    }

    public String getSMTP_AUTH_USER() {
        return SMTP_AUTH_USER;
    }

    public String getSMTP_AUTH_PWD() {
        return SMTP_AUTH_PWD;
    }

}
