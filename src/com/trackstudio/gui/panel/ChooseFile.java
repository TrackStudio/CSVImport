package com.trackstudio.gui.panel;

import com.trackstudio.component.*;
import com.trackstudio.csvimport.CSVImport;
import com.trackstudio.data.DataBean;
import com.trackstudio.data.Pair;
import com.trackstudio.gui.Wizard;
import com.trackstudio.gui.panel.impl.PanelImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.util.*;

public class ChooseFile extends PanelImpl {
    private DataBean dataBean;
    private String filePath, fileMapping;
    private JTextField filePathField;
    private JTextField mappingFileField;
    private CSVImport cvs;
    private JComboBox encodingCombo;
    private Set<String> encodingsShortList, encodingsFullList;
    private JCheckBox moreEncodingCheckBox;
    private Locale currentLocale;
    private JComboBox delimiterCombo;
    private JComboBox timezoneBox;
    private final static String COMMA = ",";
    private final static String SEMICOLON = ";";

    public ChooseFile(DataBean dataBean, CSVImport cvs) {
        this.dataBean = dataBean;
        this.setDataBean(dataBean);
        this.cvs = cvs;
        fillEncodings();
    }

    public void init() {
        JPanel panelChooseFile = this;
        currentLocale = this.dataBean.getCurrentLocale();
        I18n.setLocale(this.dataBean.getCurrentLocale().toString());
        panelChooseFile.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelChooseFile.setLayout(new GraphPaperLayout(new Dimension(10, 17), 1, 1));
        fileMapping = TSProperties.getInstance().getTrackStudioProperty(TSProperties.TRACKSTUDIO_FILE_MAPPING);
        if (this.dataBean.getMappingFile() != null && this.dataBean.getMappingFile().length() != 0) {
            fileMapping = this.dataBean.getMappingFile();
        }
        mappingFileField = new JTextField();
        mappingFileField.setText(fileMapping);
        cvs.setMapping(fileMapping);
        mappingFileField.setEditable(false);
        I18nButton chooseMappingButton = new I18nButton();
        chooseMappingButton.setText("MSG_MAPPING_BUTTON");
        chooseMappingButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc;
                if (fileMapping != null) {
                    fc = new JFileChooser(fileMapping);
                } else {
                    fc = new JFileChooser(".");
                }

                CSVFileFilter filter = new CSVFileFilter();
                filter.addType("properties");
                filter.setDescription("Mapping files");
                fc.addChoosableFileFilter(filter);
                fc.setFileFilter(filter);
                int rVal = fc.showOpenDialog(null);
                if (rVal == JFileChooser.APPROVE_OPTION){
                    String path = fc.getSelectedFile().getAbsolutePath();
                    mappingFileField.setText(path);
                    fileMapping = fc.getSelectedFile().getParent();
                    cvs.setMapping(path);
                } else {

                }

            }
        });
        filePath = TSProperties.getInstance().getTrackStudioProperty(TSProperties.TRACKSTUDIO_FILE_DATA);
        if (this.dataBean.getFilePath() != null && this.dataBean.getFilePath().length() != 0) {
            filePath = this.dataBean.getFilePath();
        }
        filePathField = new JTextField();
        filePathField.setEditable(false);
        filePathField.setText(filePath);
        I18nButton chooseFileButton = new I18nButton();
        chooseFileButton.setText("MSG_BROWSE_BUTTON");
        chooseFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc;
                if (filePath != null) {
                    fc = new JFileChooser(filePath);
                } else {
                    fc = new JFileChooser(".");
                }

                CSVFileFilter filter = new CSVFileFilter();
                filter.addType("csv");
                filter.setDescription("CSV files");
                fc.addChoosableFileFilter(filter);
                fc.setFileFilter(filter);
                int rVal = fc.showOpenDialog(null);
                if (rVal == JFileChooser.APPROVE_OPTION)
                    filePathField.setText(fc.getSelectedFile().getAbsolutePath());
                if (fc.getSelectedFile() != null) {
                    filePath = fc.getSelectedFile().getParent();
                }
            }
        });

        I18nLabel delimiterLabel = new I18nLabel("MSG_DELIMITER_LABEL");
        I18nLabel charsetLabel = new I18nLabel("MSG_CHARSET_LABEL");
        I18nLabel timezoneLabel = new I18nLabel("MSG_TIMEZONE");
        I18nLabel localeLabel = new I18nLabel("MSG_LOCALE");
        I18nLabel chooseFileLabel = new I18nLabel("CHOOSE_FILE");
        encodingCombo = new JComboBox();
        encodingCombo.setEditable(true);
        encodingCombo.setSelectedItem(this.dataBean.getEncoding());
        fillEncoding(false);
        moreEncodingCheckBox = new I18nCheckBox("MSG_CHECKBOXCHARSET_LABEL", false);
        moreEncodingCheckBox.setSelected(this.dataBean.isMoreEncoding());
        moreEncodingCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (moreEncodingCheckBox.isSelected())
                    fillEncoding(true);
                else
                    fillEncoding(false);
            }
        });

        delimiterCombo = new JComboBox();
        Pair comma = new Pair(COMMA, "," + " (" + I18n.getString("MSG_DELIMITER_SIGN_COMMA") + ")");
        Pair semicolon = new Pair(SEMICOLON, ";" + " (" + I18n.getString("MSG_DELIMITER_SIGN_SEMICOLON") + ")");
        delimiterCombo.addItem(comma);
        delimiterCombo.addItem(semicolon);
        String delimiter = TSProperties.getInstance().getTrackStudioProperty(TSProperties.TRACKSTUDIO_DELIMITER_PROPERTY);
        if (this.dataBean.getDelimiter() != null) {
            delimiter = this.dataBean.getDelimiter();
        }
        if (delimiter != null && delimiter.length() != 0) {
            if (delimiter.equals(COMMA)) {
                delimiterCombo.setSelectedItem(comma);
            } else if (delimiter.equals(SEMICOLON)) {
                delimiterCombo.setSelectedItem(semicolon);
            }
        }
        Locale[] locales = Locale.getAvailableLocales();

        JComboBox localeBox = new JComboBox();
        localeBox.setRenderer(new DefaultListCellRenderer(){
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Locale l = (Locale)value;
                DateFormatter df = new DateFormatter(SimpleTimeZone.getDefault(), l);
                return   super.getListCellRendererComponent(list, l.getDisplayName(currentLocale) + " ("+df.parse(Calendar.getInstance())+")", index, isSelected, cellHasFocus);



            }
        });
        localeBox.setEditable(false);
        TreeSet<Locale> sortedLocales =new TreeSet<Locale>(new LocaleSorter(currentLocale));
        sortedLocales.addAll(Arrays.asList(locales));
        for (Locale l : sortedLocales){

            localeBox.addItem(l);
            if (l.equals(currentLocale)) localeBox.setSelectedIndex(localeBox.getModel().getSize()-1);
        }

        timezoneBox = new JComboBox();
        TreeSet<TimeZone> sortedZones =new TreeSet<TimeZone>(new TimeZoneSorter(currentLocale));
        String[] tz =TimeZone.getAvailableIDs();

        for (String t: tz){
            sortedZones.add(SimpleTimeZone.getTimeZone(t));
        }
        for (TimeZone l : sortedZones){
            timezoneBox.addItem(l);
            if (l.equals(this.dataBean.getTimezone())) {
                timezoneBox.setSelectedIndex(timezoneBox.getModel().getSize()-1);
            }

        }
        timezoneBox.setRenderer(new DefaultListCellRenderer(){
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                TimeZone l = (TimeZone)value;
                return   super.getListCellRendererComponent(list, l.getDisplayName(currentLocale), index, isSelected, cellHasFocus);

            }
        });
        timezoneBox.setEditable(false);

        I18nButton saveButton = new I18nButton(I18n.getString("MSG_SAVE_SETTINGS_BUTTON"));
        saveButton.setIcon(Wizard.getIcon("save16x16.gif"));
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveChanges();
            }
        });

        panelChooseFile.add(new Header(chooseFileLabel), new Rectangle(0, 0, 10, 2));

        panelChooseFile.add(mappingFileField, new Rectangle(1, 4, 8, 1));
        panelChooseFile.add(chooseMappingButton, new Rectangle(5, 5, 4, 1));

        panelChooseFile.add(filePathField, new Rectangle(1, 6, 8, 1));
        panelChooseFile.add(chooseFileButton, new Rectangle(5, 7, 4, 1));

        panelChooseFile.add(charsetLabel, new Rectangle(1, 8, 2, 1));
        panelChooseFile.add(encodingCombo, new Rectangle(3, 8, 2, 1));
        panelChooseFile.add(moreEncodingCheckBox, new Rectangle(5, 8, 3, 1));

        panelChooseFile.add(delimiterLabel, new Rectangle(1, 10, 2, 1));
        panelChooseFile.add(delimiterCombo, new Rectangle(3, 10, 3, 1));

        panelChooseFile.add(timezoneLabel, new Rectangle(1, 12, 2, 1));
        panelChooseFile.add(timezoneBox, new Rectangle(3, 12, 3, 1));

        panelChooseFile.add(localeLabel, new Rectangle(1, 14, 3, 1));
        panelChooseFile.add(localeBox, new Rectangle(4, 14, 4, 1));

        panelChooseFile.add(saveButton, new Rectangle(5, 16, 4, 1));
    }

    private void fillEncoding(boolean isMoreChecked) {
        encodingCombo.removeAllItems();
        if (isMoreChecked)
        {
            for (String enc: encodingsFullList){
                encodingCombo.addItem(enc);
            }
        }
        else for (String enc : encodingsShortList){
            encodingCombo.addItem(enc);
        }
        String charset = TSProperties.getInstance().getTrackStudioProperty(TSProperties.TRACKSTUDIO_ENCODING_PROPERTY);
        encodingCombo.setSelectedItem(charset);
    }

    private void fillEncodings() {
        encodingsFullList = new HashSet<String>();
        encodingsShortList = new HashSet<String>();
        encodingsShortList.add(Charset.forName(System.getProperty("file.encoding")).displayName(new Locale(I18n.getLocale())));
        encodingsShortList.add(Charset.forName("ISO-8859-1").displayName(new Locale(I18n.getLocale())));
        encodingsShortList.add(Charset.forName("US-ASCII").displayName(new Locale(I18n.getLocale())));
        encodingsShortList.add(Charset.forName("UTF-8").displayName(new Locale(I18n.getLocale())));
        String jdk = System.getProperty("java.specification.version");
        if (!(jdk.startsWith("1.2") || jdk.startsWith("1.3") || jdk.startsWith("1.1") || jdk.startsWith("1.1")))
            encodingsFullList = Charset.availableCharsets().keySet();
        else {
            encodingsFullList = encodingsShortList;
        }

    }

    @Override
    public void submitForm() {
        this.dataBean.setFilePath(filePathField.getText());
        this.dataBean.setEncoding(encodingCombo.getSelectedItem().toString());
        Pair delimiter = (Pair) delimiterCombo.getSelectedItem();
        this.dataBean.setDelimiter(delimiter.getKey());
        this.dataBean.setTimezone((TimeZone) timezoneBox.getSelectedItem());
        this.dataBean.setMappingFile(mappingFileField.getText());
        this.dataBean.setMoreEncoding(moreEncodingCheckBox.isSelected());
    }

    @Override
    public String validateForm() {
        if (filePathField.getText().length() == 0) {
            return I18n.getString("MSG_CHOOSE_FILE_LABEL");
        }
        if (filePathField.getText().length() == 0) {
            return I18n.getString("MSG_CHOOSE_FILE_LABEL");
        }
        return Wizard.VATILDATE_FRUE;
    }

    @Override
    public void updateDataForm() {
    }

    public void saveChanges() {
        submitForm();
        this.dataBean.setLog(I18n.getString("SAVING_CONFIG"));
        this.dataBean.setLog("delimiter = " + this.dataBean.getDelimiter());
        TSProperties.getInstance().setTrackStudioProperty(TSProperties.TRACKSTUDIO_DELIMITER_PROPERTY, this.dataBean.getDelimiter());
        this.dataBean.setLog("encoding = " + this.dataBean.getEncoding());      
        TSProperties.getInstance().setTrackStudioProperty(TSProperties.TRACKSTUDIO_ENCODING_PROPERTY, this.dataBean.getEncoding());
        this.dataBean.setLog("url = " + this.dataBean.getUrl());
        TSProperties.getInstance().setTrackStudioProperty(TSProperties.TRACKSTUDIO_URL_PROPERTY, this.dataBean.getUrl());
        this.dataBean.setLog("login = " + this.dataBean.getLogin());
        TSProperties.getInstance().setTrackStudioProperty(TSProperties.TRACKSTUDIO_LOGIN_PROPERTY, this.dataBean.getLogin());
        this.dataBean.setLog("password = ***");
        TSProperties.getInstance().setTrackStudioProperty(TSProperties.TRACKSTUDIO_PASSWORD_PROPERTY, this.dataBean.getPassword());
        this.dataBean.setLog("file data = " + this.dataBean.getFilePath());
        TSProperties.getInstance().setTrackStudioProperty(TSProperties.TRACKSTUDIO_FILE_DATA, this.dataBean.getFilePath());
        this.dataBean.setLog("file mapping = " + this.dataBean.getMappingFile());
        TSProperties.getInstance().setTrackStudioProperty(TSProperties.TRACKSTUDIO_FILE_MAPPING, this.dataBean.getMappingFile());
        this.dataBean.setLog("timezone = " + this.dataBean.getTimezone());
        TSProperties.getInstance().setTrackStudioProperty(TSProperties.TRACKSTUDIO_TIMEZONE_PROPERTY, this.dataBean.getTimezone().getID());

        TSProperties.getInstance().updateConfig();
    }
}