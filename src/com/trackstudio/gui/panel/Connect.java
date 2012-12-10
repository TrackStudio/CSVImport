package com.trackstudio.gui.panel;

import com.trackstudio.component.I18n;
import com.trackstudio.component.I18nButton;
import com.trackstudio.component.I18nLabel;
import com.trackstudio.csvimport.CSVImport;
import com.trackstudio.data.DataBean;
import com.trackstudio.component.GraphPaperLayout;
import com.trackstudio.gui.Wizard;
import com.trackstudio.gui.panel.impl.PanelImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.SocketException;

public class Connect extends PanelImpl {
    private DataBean dataBean;
    private JTextField urlField = new JTextField(10);
    private JTextField loginField = new JTextField(10);
    private JPasswordField passwordField = new JPasswordField(10);
    private I18nButton testConnectionButton;
    private CSVImport csv;
    private String message;
    private CheckConnection action;

    public Connect(DataBean dataBean, CSVImport csv) {
        this.dataBean = dataBean;
        this.csv = csv;
        this.setDataBean(dataBean);
    }


    public void init() {
        JPanel jPanel = this;
        message = I18n.getString("TEST_CONNECT_CHECKED");
        I18n.setLocale(this.dataBean.getCurrentLocale().toString());
        jPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel.setLayout(new GraphPaperLayout(new Dimension(10, 17), 1, 1));

        I18nLabel header = new I18nLabel("MSG_SETUP_CONNECTION_PARAMETERS_LABEL");
        jPanel.add(new Header(header), new Rectangle(0, 0, 10, 3));

        I18nLabel urlLabel = new I18nLabel("MSG_URL_LABEL");
        jPanel.add(urlLabel, new Rectangle(1, 5, 2, 1));
        urlField.setText(this.dataBean.getUrl());
        jPanel.add(urlField, new Rectangle(2, 5, 8, 1));

        I18nLabel loginLabel = new I18nLabel("MSG_LOGIN_LABEL");
        jPanel.add(loginLabel, new Rectangle(1, 7, 2, 1));
        loginField.setText(this.dataBean.getLogin());
        jPanel.add(loginField, new Rectangle(3, 7, 4, 1));

        I18nLabel passwordLabel = new I18nLabel("MSG_PWD_LABEL");
        jPanel.add(passwordLabel, new Rectangle(1, 9, 2, 1));
        passwordField.setText(this.dataBean.getPassword());
        jPanel.add(passwordField, new Rectangle(3, 9, 4, 1));

        testConnectionButton = new I18nButton("MSG_TEST_CONNECTION");
        action = new CheckConnection();
        testConnectionButton.addActionListener(action);
        jPanel.add(testConnectionButton, new Rectangle(7, 12, 3, 1));
    }

    @Override
    public void submitForm() {
        dataBean.setUrl(urlField.getText());
        dataBean.setLogin(loginField.getText());
        dataBean.setPassword(String.valueOf(passwordField.getPassword()));
        this.setDataBean(dataBean);
    }

    @Override
    public String validateForm() {
        action.checkConnectin();
        return message;
    }

    @Override
    public void updateDataForm() {
    }

    private class CheckConnection implements ActionListener {
        private boolean showMessage = true;

        public void checkConnectin() {
            showMessage = false;
            actionPerformed(null);
            showMessage = true;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                if ((urlField.getText().length() == 0)) {
                    if (showMessage) {
                        JOptionPane.showMessageDialog(testConnectionButton.getParent(), I18n.getString("MSG_URL_FIELD_IS_EMPTY_MESSAGE")/*URL Field is empty"*/, "Trackstudio Enterprise", JOptionPane.ERROR_MESSAGE);
                    }
                } else if ((loginField.getText().length() == 0)) {
                    if (showMessage) {
                        JOptionPane.showMessageDialog(testConnectionButton.getParent(), I18n.getString("MSG_LOGIN_FIELD_IS_EMPTY_MESSAGE")/*"Login Field is empty"*/, "Trackstudio Enterprise", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    csv.changeConnectionSettings(urlField.getText(), loginField.getText(), new String(passwordField.getPassword()));
                    if (showMessage) {
                        JOptionPane.showMessageDialog(testConnectionButton.getParent(), I18n.getString("MSG_SUCCESSFULL_CONNECTION_MESSAGE")/*"Successful Connection"*/, "Trackstudio Enterprise", JOptionPane.INFORMATION_MESSAGE);
                    }
                    message = Wizard.VATILDATE_FRUE;
                }
            } catch (MalformedURLException mex) {
                JOptionPane.showMessageDialog(testConnectionButton.getParent(), I18n.getString("MSG_INVALID_URL_MESSAGE") + "\n" + mex.getMessage()/*"Invalid URL"*/, "Trackstudio Enterprise", JOptionPane.ERROR_MESSAGE);
                dataBean.setLog(mex);
                dataBean.setLog("ERROR:" + mex.getMessage());
            } catch (java.net.ConnectException exc) {
                JOptionPane.showMessageDialog(null, I18n.getString("AUTHENTICATION_FAILED") /*"Connection exception"*/, "Trackstudio Enterprise", JOptionPane.ERROR_MESSAGE);
                dataBean.setLog("ERROR:" + exc.getMessage());
                dataBean.setLog(exc);
            } catch (SocketException exs) {
                JOptionPane.showMessageDialog(testConnectionButton.getParent(), I18n.getString("MSG_CONNECTION_ERROR_MESSAGE") + "\n" + exs.getMessage()/*"Connection error"*/, "Trackstudio Enterprise", JOptionPane.ERROR_MESSAGE);
                dataBean.setLog("ERROR:" + exs.getMessage());
                dataBean.setLog(exs);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(testConnectionButton.getParent(), ex.getMessage(), "Trackstudio Enterprise", JOptionPane.ERROR_MESSAGE);
                dataBean.setLog(ex);
            }
        }
    }
}
