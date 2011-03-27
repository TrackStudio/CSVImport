package com.trackstudio.gui.panel;

import com.trackstudio.component.I18n;
import com.trackstudio.component.I18nLabel;
import com.trackstudio.data.DataBean;
import com.trackstudio.component.GraphPaperLayout;
import com.trackstudio.gui.Wizard;
import com.trackstudio.gui.panel.impl.PanelImpl;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Welcom extends PanelImpl {
    private DataBean dataBean;

    public Welcom(DataBean dataBean) {
        this.setDataBean(dataBean);
        this.dataBean = dataBean;
    }

    @Override
    public void submitForm() {

    }

    @Override
    public String validateForm() {
        return Wizard.VATILDATE_FRUE;
    }

    @Override
    public void updateDataForm() {
    }

    @Override
    public void init() {
        JPanel jPanel = this;
        I18n.setLocale(this.dataBean.getCurrentLocale().toString());
        jPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel.setLayout(new GraphPaperLayout(new Dimension(20, 20), 1, 1));
        ImageIcon icon = Wizard.getIcon("logo.png");
        JLabel labelIcon = new JLabel();
        labelIcon.setIcon(icon);
        jPanel.add(labelIcon, new Rectangle(1, 0, 10, 20));

        I18nLabel header = new I18nLabel("TITLE_WELCOM");
        jPanel.add(header, new Rectangle(4, 1, 10, 1));

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);

        try {
            File file = new File("./etc/readme_"+this.dataBean.getCurrentLocale().toString()+".html");
            if (file.exists()) {
                String text = readFileAsString(file);
                text = new String(text.getBytes(), "windows-1251");
                editorPane.setText(text);
            }
        } catch (IOException e) {
            this.dataBean.setLog(e);
        }
        editorPane.updateUI();
        JScrollPane scroller = new JScrollPane();
        scroller.getViewport().add(editorPane);
        scroller.updateUI();
        jPanel.add(scroller, new Rectangle(4, 3, 16, 17));

        I18nLabel continueLabel = new I18nLabel("PRESS_CONTINUE");
        jPanel.add(continueLabel, new Rectangle(4, 19, 10, 1));
    }

    private static String readFileAsString(File file) throws IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        char[] buf = new char[1024];
        int numRead;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }
}
