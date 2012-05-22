package com.trackstudio.gui.panel;

import com.trackstudio.component.I18n;
import com.trackstudio.component.I18nLabel;
import com.trackstudio.data.DataBean;
import com.trackstudio.component.GraphPaperLayout;
import com.trackstudio.gui.Wizard;
import com.trackstudio.gui.panel.impl.PanelImpl;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

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

        File file = new File("./etc/readme_"+this.dataBean.getCurrentLocale().toString()+".html");
        if (file.exists()) {
            String text = readFile(file, "UTF-8");
            editorPane.setText(text);
        }
        editorPane.updateUI();
        JScrollPane scroller = new JScrollPane();
        scroller.getViewport().add(editorPane);
        scroller.updateUI();
        jPanel.add(scroller, new Rectangle(4, 3, 16, 17));

        I18nLabel continueLabel = new I18nLabel("PRESS_CONTINUE");
        jPanel.add(continueLabel, new Rectangle(4, 19, 10, 1));
    }

    public synchronized String readFile(File file, String encoding) {
        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(file), encoding);
            while (scanner.hasNextLine()){
                text.append(scanner.nextLine()).append(NL);
            }
        } catch (Exception e) {
            dataBean.setLog(e);
        } finally{
            if (scanner != null) {
                scanner.close();
            }
        }
        return text.toString();
    }
}
