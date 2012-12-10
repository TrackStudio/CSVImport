package com.trackstudio.component;

import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;


import javax.swing.event.EventListenerList;
import java.io.*;

import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class TSProperties {
    public static final String filePrefix = "file://";
    class SortedProperties extends Properties {
        public SortedProperties() {
        }

        public SortedProperties(Properties defaults) {
            super(defaults);
        }

        public synchronized Enumeration keys() {
            Enumeration keysEnum = super.keys();
            Vector keyList = new Vector();
            while (keysEnum.hasMoreElements()) {
                keyList.add(keysEnum.nextElement());
            }
            Collections.sort(keyList);
            return keyList.elements();
        }
        

        public synchronized void store(OutputStream out, String comments) throws IOException {
            BufferedWriter awriter;
            awriter = new BufferedWriter(new OutputStreamWriter(out, "8859_1"));
            if (comments != null)
                writeln(awriter, "#" + comments);
            writeln(awriter, "#" + new Date().toString());
            for (Enumeration e = keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                String val = (String) get(key);
                key = saveConvert(key, true);

                /* No need to escape embedded and trailing spaces for value, hence
             * pass false to flag.
             */
                val = saveConvert(val, false);
                writeln(awriter, key + "=" + val);
            }
            awriter.flush();
        }

        private void writeln(BufferedWriter bw, String s) throws IOException {
            bw.write(s);
            bw.newLine();
        }

        private String saveConvert(String theString, boolean escapeSpace) {
            int len = theString.length();
            int bufLen = len * 2;
            if (bufLen < 0) {
                bufLen = Integer.MAX_VALUE;
            }
            StringBuffer outBuffer = new StringBuffer(bufLen);

            for (int x = 0; x < len; x++) {
                char aChar = theString.charAt(x);
                // Handle common case first, selecting largest block that
                // avoids the specials below
                if ((aChar > 61) && (aChar < 127)) {
                    if (aChar == '\\') {
                        outBuffer.append('\\');
                        outBuffer.append('\\');
                        continue;
                    }
                    outBuffer.append(aChar);
                    continue;
                }
                switch (aChar) {
                    case' ':
                        if (x == 0 || escapeSpace)
                            outBuffer.append('\\');
                        outBuffer.append(' ');
                        break;
                    case'\t':
                        outBuffer.append('\\');
                        outBuffer.append('t');
                        break;
                    case'\n':
                        outBuffer.append('\\');
                        outBuffer.append('n');
                        break;
                    case'\r':
                        outBuffer.append('\\');
                        outBuffer.append('r');
                        break;
                    case'\f':
                        outBuffer.append('\\');
                        outBuffer.append('f');
                        break;
                    case'=': // Fall through
                    case':': // Fall through
                    case'#': // Fall through
                    case'!':
                        outBuffer.append('\\');
                        outBuffer.append(aChar);
                        break;
                    default:
                        if ((aChar < 0x0020) || (aChar > 0x007e)) {
                            outBuffer.append('\\');
                            outBuffer.append('u');
                            outBuffer.append(toHex((aChar >> 12) & 0xF));
                            outBuffer.append(toHex((aChar >> 8) & 0xF));
                            outBuffer.append(toHex((aChar >> 4) & 0xF));
                            outBuffer.append(toHex(aChar & 0xF));
                        } else {
                            outBuffer.append(aChar);
                        }
                }
            }
            return outBuffer.toString();
        }
        private char toHex(int nibble) {
	        return hexDigit[(nibble & 0xF)];
        }
        private final char[] hexDigit = {
	        '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
        };
    }

    private EventListenerList eventListenerList = new EventListenerList();
    private boolean eventsEnabled = true;


    public boolean isEventsEnabled() {
        return eventsEnabled;
    }

    public void setEventsEnabled(boolean eventsEnabled) {
        this.eventsEnabled = eventsEnabled;
    }

    public void addPropertyChangeEventListener(PropertyChangeListener l) {
        eventListenerList.add(PropertyChangeListener.class, l);
    }

    public void removePropertyChangeEventListener(PropertyChangeListener l) {
        eventListenerList.remove(PropertyChangeListener.class, l);
    }

   /* public void addTSPropertiesStateChangeEventListener(TSPropertiesStateChangeEventListener l) {
        eventListenerList.add(TSPropertiesStateChangeEventListener.class, l);
    }

    public void removeTSPropertiesStateChangeEventListener(TSPropertiesStateChangeEventListener l) {
        eventListenerList.remove(TSPropertiesStateChangeEventListener.class, l);
    }*/


    protected void firePropertyChangeEvent(String property, String oldValue, String newValue) {
        Object[] listeners = eventListenerList.getListeners(PropertyChangeListener.class);
        PropertyChangeEvent fooEvent = null;
        for (int i = 0; i < listeners.length; i++) {
            if (fooEvent == null)
                fooEvent = new PropertyChangeEvent(instance, property, oldValue, newValue);
            ((PropertyChangeListener) listeners[i]).propertyChange(fooEvent);
        }

    }

    /*protected void fireTSPropertiesStateChangeEvent() {
        // Guaranteed to return a non-null array
        Object[] listeners = eventListenerList.getListeners(TSPropertiesStateChangeEventListener.class);
        TSPropertiesStateChangeEvent fooEvent = null;
        for (int i = 0; i < listeners.length; i++) {
            if (fooEvent == null)
                fooEvent = new TSPropertiesStateChangeEvent(this);
            ((TSPropertiesStateChangeEventListener) listeners[i]).stateChanged(fooEvent);
        }

    }*/

    private static TSProperties instance;

    private SortedProperties properties;

    private boolean trackStudioDirty = false; 
    private boolean mailDirty = false;
    private boolean databaseDirty = false;
    private boolean securityDirty = false;
    private boolean SCMDirty = false;
    private boolean licenseDirty = false;
    private boolean smanDirty = false;
    
    public static final String SLASH4 = "\\\\";
    public static final String SLASH2 = "\\";

    public static final String TRACKSTUDIO_TIMEZONE_PROPERTY = "trackstudio.timezone";
    public static final String TRACKSTUDIO_ENCODING_PROPERTY = "trackstudio.encoding";
    public static final String TRACKSTUDIO_LOCALE_PROPERTY = "trackstudio.defaultLocale";

    public static final String TRACKSTUDIO_DELIMITER_PROPERTY = "trackstudio.delimiter";
    public static final String TRACKSTUDIO_URL_PROPERTY = "trackstudio.siteURL";
    public static final String TRACKSTUDIO_LOGIN_PROPERTY = "trackstudio.login";
    public static final String TRACKSTUDIO_FILE_DATA = "trackstudio.file.data";
    public static final String TRACKSTUDIO_FILE_MAPPING = "trackstudio.file.mapping";
    public static final String TRACKSTUDIO_PASSWORD_PROPERTY = "trackstudio.password";

    public static final String COMMENT_PREFIX = "#";
   
   
    public static final String YES = "yes";
    public static final String NO = "no";
    public static final String TRUE = "true";
    public static final String FALSE = "false";


   

    private void setTrackStudioDirty(boolean trackStudioDirty) {
        this.trackStudioDirty = trackStudioDirty;
     //   fireTSPropertiesStateChangeEvent();
    }

    
    
    private final String PROPERTIES_PATH = getPropertiesPath();    
    private final String PROPERTY_URI = PROPERTIES_PATH + "trackstudio.properties";
    
    
    private TSProperties() {
        setEventsEnabled(false);
        createTrackStudioProperties();
        loadParametersFromConfig();
        
    }

   
    public static TSProperties getInstance() {
        if (instance == null)
            instance = new TSProperties();
        return instance;
    }


    private void loadFromProp() {
        try {
            properties.load(new BufferedInputStream(new FileInputStream(PROPERTY_URI.substring(filePrefix.length()))));
                DOMParser parser = new DOMParser();
                parser.setEntityResolver(new EntityResolver() {
                    public InputSource resolveEntity(String publicId, String systemId) {
                        try {
                            return new InputSource(new ByteArrayInputStream("".getBytes()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

   
    private void updatePropConfig() {
        //GUI.logger.println(I18n.getString("UPDATING_CONFIG"));
        try {         
            String result = getPropertiesFileContentCommented(PROPERTY_URI);
            SortedProperties tmp = new SortedProperties();
            
            tmp.setProperty(TRACKSTUDIO_ENCODING_PROPERTY, properties.getProperty(TRACKSTUDIO_ENCODING_PROPERTY));
            tmp.setProperty(TRACKSTUDIO_LOCALE_PROPERTY, properties.getProperty(TRACKSTUDIO_LOCALE_PROPERTY));
            tmp.setProperty(TRACKSTUDIO_DELIMITER_PROPERTY,properties.getProperty(TRACKSTUDIO_DELIMITER_PROPERTY));
            tmp.setProperty(TRACKSTUDIO_LOGIN_PROPERTY,properties.getProperty(TRACKSTUDIO_LOGIN_PROPERTY));
            tmp.setProperty(TRACKSTUDIO_PASSWORD_PROPERTY,properties.getProperty(TRACKSTUDIO_PASSWORD_PROPERTY));
            tmp.setProperty(TRACKSTUDIO_URL_PROPERTY,properties.getProperty(TRACKSTUDIO_URL_PROPERTY));
            tmp.setProperty(TRACKSTUDIO_FILE_DATA, properties.getProperty(TRACKSTUDIO_FILE_DATA));
            tmp.setProperty(TRACKSTUDIO_FILE_MAPPING, properties.getProperty(TRACKSTUDIO_FILE_MAPPING));
            tmp.setProperty(TRACKSTUDIO_TIMEZONE_PROPERTY, properties.getProperty(TRACKSTUDIO_TIMEZONE_PROPERTY));

            savePropFile(PROPERTY_URI, tmp, result);

            {
                DOMParser parser = new DOMParser();
                parser.setEntityResolver(new EntityResolver() {
                    public InputSource resolveEntity(String publicId, String systemId) {
                        try {
                            return new InputSource(new ByteArrayInputStream("".getBytes()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
              
            }

        } catch (Exception e) {
            e.printStackTrace();
          //  if (ServerManager.log != null) ServerManager.log.debug(e);
        }
        setTrackStudioDirty(false);
    }

   
        
    
    public void loadParametersFromConfig() {
       // Logger.consoleDebug("loadParametersFromConfig called");
        //sman
        loadFromProp();
       
       
    }


    public void updateConfig() {
    	//Logger.consoleDebug("updateConfig called");
        //if (isTrackStudioDirty())
            updatePropConfig();
        
    }


    private String getPropertiesFileContentCommented(String uri) {
        StringBuffer result = new StringBuffer();
        try {
            String buf;
            BufferedReader br = new BufferedReader(new FileReader(uri.substring(filePrefix.length())));
            if (uri.equals(PROPERTY_URI)) {
                while ((buf = br.readLine()) != null) {
                    String trim = buf.trim();
                    if (trim.length() > 0 && !trim.startsWith(COMMENT_PREFIX)) {
                        if (trim.toLowerCase(Locale.ENGLISH).startsWith(TRACKSTUDIO_URL_PROPERTY.toLowerCase(Locale.ENGLISH)) ||
                                trim.startsWith(TRACKSTUDIO_TIMEZONE_PROPERTY) ||
                                trim.startsWith(TRACKSTUDIO_ENCODING_PROPERTY) ||
                                trim.startsWith(TRACKSTUDIO_LOCALE_PROPERTY))
                            result.append(COMMENT_PREFIX);
                    }
                    result.append(buf);
                    result.append("\n");
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    private void savePropFile(String uri, SortedProperties content, String oldContent) {
        FileOutputStream out = null;
        try {

            out = new FileOutputStream(uri.substring(filePrefix.length()));
            content.store(new BufferedOutputStream(out), /*oldContent*/null);
        } catch (Exception e) {
            e.printStackTrace();
          //  if (ServerManager.log != null) ServerManager.log.debug(e);
        }
        finally{
            if (out!=null) try {
                out.close();
            } catch (IOException e) {/* Empty */}
        }
    }


    private void createTrackStudioProperties() {
        this.properties = new SortedProperties();
        properties.setProperty(TRACKSTUDIO_URL_PROPERTY, "");
        properties.setProperty(TRACKSTUDIO_DELIMITER_PROPERTY, "");
        properties.setProperty(TRACKSTUDIO_ENCODING_PROPERTY, "");
        properties.setProperty(TRACKSTUDIO_LOGIN_PROPERTY, "");
        properties.setProperty(TRACKSTUDIO_PASSWORD_PROPERTY, "");
        properties.setProperty(TRACKSTUDIO_LOCALE_PROPERTY, "");
        properties.setProperty(TRACKSTUDIO_FILE_DATA, "");
        properties.setProperty(TRACKSTUDIO_FILE_MAPPING, "");
        properties.setProperty(TRACKSTUDIO_TIMEZONE_PROPERTY, "");
    }
    
    public void setTrackStudioProperty(String property, String value) {
        String oldProperty = getTrackStudioProperty(property);
        if (value != null && !value.equals(oldProperty)) {
            properties.setProperty(property, value);
            /*if (isEventsEnabled()) {
                setTrackStudioDirty(true);*/
                firePropertyChangeEvent(property, oldProperty, value);
            //}
        }
    }


    public String getTrackStudioProperty(String property) {
        return properties.getProperty(property);
    }
    
   //�������� ��� ��� ����!!!
  private static String getPropertiesPath() {
        String result;
        String trackstudioHome = System.getProperty("Home");
        if (trackstudioHome != null)
            result = filePrefix + trackstudioHome + "/";
        else {           
            result = filePrefix+"./etc/";
        }
        return result;
    }  

}
