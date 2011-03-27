package com.trackstudio.data;

import com.trackstudio.component.TSProperties;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.TimeZone;

public class DataBean {
    private String login;
    private String password;
    private String filePath;
    private String log = "";
    private String url;
    private Locale locale;
    private String encoding;
    private String delimiter;
    private TimeZone timezone;
    private Locale currentLocale;
    private String mappingFile;
    private boolean moreEncoding = false;

    public DataBean() {
        this.delimiter = TSProperties.getInstance().getTrackStudioProperty(TSProperties.TRACKSTUDIO_DELIMITER_PROPERTY);
        this.encoding = TSProperties.getInstance().getTrackStudioProperty(TSProperties.TRACKSTUDIO_ENCODING_PROPERTY);
        this.url = TSProperties.getInstance().getTrackStudioProperty(TSProperties.TRACKSTUDIO_URL_PROPERTY);
        this.login = TSProperties.getInstance().getTrackStudioProperty(TSProperties.TRACKSTUDIO_LOGIN_PROPERTY);
        String localeProperty = TSProperties.getInstance().getTrackStudioProperty(TSProperties.TRACKSTUDIO_LOCALE_PROPERTY);
        this.currentLocale = localeProperty != null && localeProperty.length() != 0 ? new Locale(localeProperty) : Locale.ENGLISH;
    }

    public boolean isMoreEncoding() {
        return moreEncoding;
    }

    public void setMoreEncoding(boolean moreEncoding) {
        this.moreEncoding = moreEncoding;
    }

    public String getMappingFile() {
        return mappingFile;
    }

    public void setMappingFile(String mappingFile) {
        this.mappingFile = mappingFile;
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }

    public TimeZone getTimezone() {
        return timezone;
    }

    public void setTimezone(TimeZone timezone) {
        this.timezone = timezone;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log += log +"\n";
    }

    public void setLog(String header, String log) {
        this.log += header + "\n\t" + log +"\n";
    }

    public void setLog(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            this.log += sw.toString() + "\n";
        }
        catch(Exception ex) {
            this.log += ex.getMessage();
        }
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFilePath() {
        return filePath != null ? filePath : "";
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
