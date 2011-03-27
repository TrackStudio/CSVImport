/*
 * DateFormatter.java
 *
 * Created on 5 �������� 2008 �., 15:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.trackstudio.component;

/**
 *
 * @author User
 */


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.Serializable;


/**
 * This class provides operations for convertion Timestamp into String and vice-versa, using Locale and Timezone
 */

public class DateFormatter implements Serializable {

   // private static Log log = LogFactory.getLog(DateFormatter.class);
    private final String[] ignoreLocales = new String[]{"ar", "ar_AE","ar_BH", "ar_DZ", "ar_EG", "ar_IQ", "ar_JO", "ar_KW", "ar_LB", "ar_LY", "ar_MA", "ar_OM", "ar_QA", "ar_SA", "hi_IN", "ar_SD", "ko", "ko_KR", "th", "th_TH", "th_TH_TH", "zh", "zh_CN", "zh_HK", "zh_TW", "ar_SY", "ar_TN", "ar_YE", "el", "el_GR"};
    private TimeZone timezone = SimpleTimeZone.getDefault();
    private Locale locale = Locale.US;
    private static List<Locale> availableLocales = Arrays.asList(Locale.getAvailableLocales());
    private static List<Locale> allowedLocales;
    int[] intervalVal = {30, 60, 120, 180, 360, 720, 1440, 2880, 4320, 7200, 10080, 20160, 30240, 43200};
    String[] intervalStr = null;

    /**
     * returns Locale
     */
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * returns Timezone
     */
    public TimeZone getTimeZone() {
        return this.timezone;
    }

    /**
     * returns Calendar for the Locale and the Timezone
     */
    //dnikitin � ���� ������� ������ ����� � ����: ������� ������������ string-����, ��� ����
    //DataFormatter _���_ ��������� ����� � ������������ timezone, � ����� �������� ��� ���� � �����
    //��������� � ���� ����������� ������ ���(� ��������� ������� ���� ��������� � �� 3 ����).
    //���������� ������������������� ����. �.�. ���� ���� ����������, �� ������� ������ new GregorianCalendar();
    public Calendar getCalendar() {
        return new GregorianCalendar(this.timezone, this.locale);
    }

    /**
     * returns DateFormat object for this DateFormatter
     */
    public DateFormat getDateFormat() {
        DateFormat f = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, this.getLocale());
        f.setTimeZone(timezone);
        f.setCalendar(this.getCalendar());
        return f;
    }

    DateFormat getShortDateFormat() {
        DateFormat f = SimpleDateFormat.getDateInstance(DateFormat.SHORT, this.getLocale());
        
        f.setCalendar(this.getCalendar());
        return f;
    }

    //todo tdc108 ����� �������� � ������� Timestamp, � �� � �������, � ������� �������� DateFormat.
    public Calendar parseToCalendar(String param) throws Exception {
      //  log.trace("parse");
      //  log.debug("param: (" + param + ')');
        if (param == null)
            return null;

        try {
            DateFormat df = this.getDateFormat();
            String str = param.trim(); // delete spaces
            Calendar c = new GregorianCalendar();
            c.setTime(df.parse(str));
            return c;
        } catch (Exception e) {
            try {
                DateFormat df2 = this.getShortDateFormat();
                String str2 = param.trim(); // delete spaces
                Calendar c = new GregorianCalendar();
                c.setTime(df2.parse(str2));
                return c;
            } catch (Exception ex) {
                try {
                    Calendar c = new GregorianCalendar();
                    c.setTimeInMillis(Timestamp.valueOf(param).getTime());
                    return c;
                } catch (Exception e2) {
                    throw new Exception("Can't parse this date: "+ param);
                }
            }

        }
    }

    /**
     * converts Timestamp into String
     */
    public String parse(Timestamp param) {
        if (param == null)
            return null;

        DateFormat df = this.getDateFormat();
        return df.format(param);
    }

    public String parse(Calendar param) {
        if (param == null)
            return null;
        DateFormat df = this.getDateFormat();
        df.setTimeZone(timezone);
        return df.format(param.getTime());
    }
    public DateFormatter(TimeZone tz, Locale lc) {

        this.timezone = tz;
        this.locale = lc!=null ? lc: Locale.ENGLISH;
        for(int i =0; i < ignoreLocales.length; i++)
            if(ignoreLocales[i].equals(lc.toString())) {
                this.locale = Locale.US;
                break;
            }
        this.intervalStr = new String[]{I18n.getString(locale, "MSG_MIN30"),
                                        I18n.getString(locale, "MSG_HOUR1"),
                                        I18n.getString(locale, "MSG_HOUR2"),
                                        I18n.getString(locale, "MSG_HOUR3"),
                                        I18n.getString(locale, "MSG_HOUR6"),
                                        I18n.getString(locale, "MSG_HOUR12"),
                                        I18n.getString(locale, "MSG_DAY1"),
                                        I18n.getString(locale, "MSG_DAY2"),
                                        I18n.getString(locale, "MSG_DAY3"),
                                        I18n.getString(locale, "MSG_DAY5"),
                                        I18n.getString(locale, "MSG_WEEK1"),
                                        I18n.getString(locale, "MSG_WEEK2"),
                                        I18n.getString(locale, "MSG_WEEK3"),
                                        I18n.getString(locale, "MSG_DAYS30")};
    }

    /**
     * Language is always lower case, and country is always upper case. If the language is missing, the string will begin with an underbar. If both the language and country fields are missing, this function will return the empty string, even if the variant field is filled in (you can't have a locale with just a variant-- the variant must accompany a valid language or country code). Examples: "en", "de_DE", "_GB", "en_US_WIN", "de__POSIX", "fr_MAC"
     *
     * @param lc
     */
    public static Locale toLocale(String lc) {
        if (lc == null || lc.length() == 0) return Locale.US;
        StringTokenizer tk = new StringTokenizer(lc, "_", false);
        if (tk.countTokens() < 1 && lc.length() == 2) return new Locale(lc, "", "");
        String lang = "";
        lang = tk.nextToken();
        String country = "";
        if (tk.hasMoreTokens()) country = tk.nextToken();
        String variant = "";
        if (tk.hasMoreTokens()) variant = tk.nextToken();
        return new Locale(lang, country, variant);
    }


    public static TimeZone getTimeZoneFromString(String tz) {
        TimeZone z = SimpleTimeZone.getDefault();
        if (tz != null && tz.length() > 0) {
            z = SimpleTimeZone.getTimeZone(tz);
        }
        return z;

    }

    public static Locale getLocaleFromString(String lc) {
        Locale l = null;
        if (lc != null && lc.length() > 0) {
            List<Locale> lcs = getAvailableLocales();
            for (Locale n: lcs) {
                if (n.toString().equals(lc))
                    l = n;
            }
        }
        return l;
    }


    public DateFormatter(String tz, String lc)
            throws Exception {
        
        this(getTimeZoneFromString(tz), getLocaleFromString(lc));
    }

    public String getPattern() {

        return ((SimpleDateFormat) this.getDateFormat()).toPattern();
    }

    /*public String getPattern2() {

        HTMLEncoder coder = new HTMLEncoder(((SimpleDateFormat) this.getDateFormat()).toPattern());
        coder.replace("yyyy","%Y");
        coder.replace("yy","%y");
        if(coder.getResult().indexOf("%y") == -1)
            coder.replace( "y","%y");
        coder.replace("dd","%d");
        if(coder.getResult().indexOf("%d") == -1)
            coder.replace("d","%e");
        coder.replace("MM","%m");
        coder.replace("M","%z");//dnikitin ��� �������� �� ����� ������ ����������� ��� �������� �� �����
        coder.replace( "HH","%H");
        if(coder.getResult().indexOf("%H") == -1)
            coder.replace( "H","%k");
        coder.replace( "hh","%I");
        coder.replace( "h","%l");
        coder.replace( "mm","%M");
        coder.replace( "ss","%S");

        //HTMLEncoder.replace(sb, "M","%m");

        coder.replace( "a","%p");
        //HTMLEncoder.replace(sb, "A","%P");
        return coder.getResult().toString();
    }*/

    public String getIntervalSelectTag(int interval)
            throws Exception {
      //  log.trace("getIntervalSelectTag1(" + interval + ')');
        StringBuffer result = new StringBuffer();
        result.append("<select name='interval'>");
        boolean selected = false;
        for (int i = 0; i < intervalVal.length; i++) {
            if (interval == intervalVal[i]) {
                selected = true;
                result.append("<option selected='selected' value='").append(intervalVal[i]).append("'>").append(intervalStr[i]).append("</option>");
            } else
                result.append("<option value='").append(intervalVal[i]).append("'>").append(intervalStr[i]).append("</option>");
        }
        if (!selected)
            result.append("<option selected='selected' value='").append(interval).append("'>").append(interval).append(' ').append(I18n.getString(locale, "MSG_MIN")).append("</option>");
        result.append("</select>");
        return result.toString();
    }

    public String getInterval(int interval)
            throws Exception {
        for (int i = 0; i < intervalVal.length; i++)
            if (interval == intervalVal[i])
                return intervalStr[i];
        return interval + " " + I18n.getString(locale, "MSG_MIN");
    }

    public static List<Locale> getAvailableLocales() {
        return availableLocales;
    }

 /*   public static List<Locale> getAllowedLocales() {
        if (allowedLocales==null){
            allowedLocales = new ArrayList<Locale>();
            ArrayList<String> allowed = Config.getInstance().getAllowedLocales();
            if (allowed!=null) for (String p: allowed){
                Locale l = getLocaleFromString(p);
                    if (l!=null) allowedLocales.add(l);
            }
        }
        if (allowedLocales.isEmpty()) return availableLocales;
        else return allowedLocales;
    }*/

    public static void main(String[] args) {

        try
        {
            DateFormatter df = new DateFormatter(TimeZone.getDefault(), new Locale("ru"));
            
            String strDate = "1.1.1 0:0";
            
            //System.out.println(df.parse
                    System.out.println(df.parseToCalendar(strDate).getTimeInMillis());
            
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        
       /* for(int i = 0; i < Locale.getAvailableLocales().length;i++){
            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getAvailableLocales()[i]);
            System.out.println(sdf.toPattern());
            System.out.println(sdf.format(new Date(System.currentTimeMillis())));
        }*/
    }

}
