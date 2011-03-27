/*
 * I18n.java
 *
 * Created on 10 ���� 2008 �., 14:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.trackstudio.component;

import java.text.MessageFormat;
import java.util.*;


/**
 * Used for localization based on java.util.ResourceBundle
 */
public class I18n {
    public static final String DEFAULT = "en";
    public static final String LANGFILE = "csvimport";
    private static String currentLocale;
    /*
    private static String makePath(String a) {
        
       return "resources/lang/language_" + a + ".properties";
       
    }
    */




    protected HashMap<String, PropertyResourceBundle> resources;

    protected ResourceBundle getResource(String language){
        if (this.resources.containsKey(language)) {
            return this.resources.get(language);
        } else
            return this.resources.get(DEFAULT);
    }

    private static I18n i18n;

    public Set<String> getAvailableLocales(){
        return this.resources.keySet();
    }

    private I18n() {
        this.resources = new HashMap<String, PropertyResourceBundle>();

        Locale[] ls = Locale.getAvailableLocales();
        for (Locale lc: ls){
            try{
                PropertyResourceBundle myResources = (PropertyResourceBundle) PropertyResourceBundle.getBundle(LANGFILE, lc);
                if (myResources.getLocale().equals(lc)) this.resources.put(lc.toString(), myResources);
            } catch (MissingResourceException me){
                //no language
            }
        }
    }

    public static void loadConfig() {
        if (i18n == null) {
            i18n = new I18n();
        }
    }

    public static I18n getInstance(){
        if (i18n == null) {
            loadConfig();

        }
        return i18n;
    }


    public static String getString(Locale locale, String key){
        return getString(locale.toString(), key);
    }


    public static String getString(String key) {
        return getString(currentLocale, key);
    }

    public static String getString(String locale, String key) {

        try {
            return I18n.getInstance().getResource(locale).getString(key);

        } catch(NullPointerException ne)
        {
            return key;
        }
        catch (MissingResourceException me){
            return key;
        }
        catch (Exception e){
            e.printStackTrace();
            return "ERROR:"+e.getMessage();

        }

    }

    public static String getString(String locale, String key, Object[] params) {
        MessageFormat form = new MessageFormat(getString(locale, key));
        return form.format(params);

    }

    public static String getString(String key, Object[] params)  {
        return getString(currentLocale,key,params);

    }

    public static void setLocale(String locale) {
        currentLocale = locale;
    }

    public static String getLocale() {
        if (currentLocale == null) return DEFAULT;
        if (currentLocale.equals("")) return DEFAULT;
        return currentLocale;
    }

}
