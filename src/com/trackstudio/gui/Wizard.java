package com.trackstudio.gui;

import com.trackstudio.component.GraphPaperLayout;
import com.trackstudio.component.I18n;
import com.trackstudio.component.I18nButton;
import com.trackstudio.component.TSProperties;
import com.trackstudio.csvimport.CSVImport;
import com.trackstudio.data.DataBean;
import com.trackstudio.gui.panel.impl.PanelImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.*;
import java.util.List;

public class Wizard extends JFrame {
    private DataBean dataBean;
    private JPanel controlPanel = new JPanel();
    private JPanel contentPanel = new JPanel();
    private JButton nextButton;
    private JButton previewButton;
    private Map<Integer, PanelImpl> panels = new HashMap<Integer, PanelImpl>();
    private Integer nowPanel = 1;
    private CSVImport csv;
    public JComboBox localeUICombo;
    private List<String> availableUILocales;
    public static final Integer CONSOLE = -1;
    public static final String VATILDATE_FRUE = "true";
    private JButton logsButton, exitButton;
    private Wizard wizard;
    private JDialog console;

    public Wizard(DataBean dataBean) throws HeadlessException {
        wizard = this;
        wizard.dataBean = dataBean;
        csv = new CSVImport(wizard.dataBean);
        String currentLocaleString = TSProperties.getInstance().getTrackStudioProperty(TSProperties.TRACKSTUDIO_LOCALE_PROPERTY);
        if (currentLocaleString==null) {
            wizard.dataBean.setLocale(Locale.getDefault());
        } else {
            wizard.dataBean.setLocale(new Locale(currentLocaleString));
        }
        nextButton = new I18nButton("MSG_NEXT_BUTTON");
        nextButton.setIcon(getIcon("next16x16.gif"));
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (updateDate(true)) {
                    ++nowPanel;
                    if (nowPanel == panels.size() -1) {
                        previewButton.setText(I18n.getString("BEGIN_WIZARD"));
                    }
                    showPanel(nowPanel);
                }
            }
        });
        previewButton = new I18nButton("MSG_BACK_BUTTON");
        previewButton.setIcon(getIcon("back16x16.gif"));
        previewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (nowPanel == panels.size() -1) {
                    nowPanel = 1;
                    previewButton.setText(I18n.getString("MSG_BACK_BUTTON"));
                    showPanel(nowPanel);
                } else {
                    showPanel(--nowPanel);
                }
            }
        });

        localeUICombo = new JComboBox();
        localeUICombo.setEditable(false);
        availableUILocales = getAvailableUILocales();
        localeUICombo.addActionListener(new UILanguageListener());
        for (String loc : availableUILocales) {
            localeUICombo.addItem(loc);
        }
        logsButton = new I18nButton("LOG");
        logsButton.setIcon(getIcon("icon-info.gif"));
        logsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                console = new JDialog(wizard, I18n.getString("MSG_TITLE"), false);
                console.setModal(true);
                console.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                console.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent evt) {
                        console.setVisible(false);
                        console.dispose();
                    }
                });
                PanelImpl consolePanel = panels.get(Wizard.CONSOLE);
                consolePanel.setDataBean(wizard.dataBean);
                consolePanel.setCsv(csv);
                consolePanel.updateDataForm();
                consolePanel.init();
                console.getContentPane().add(consolePanel);
                console.setSize(550, 450);
                centre(console);
                console.setVisible(true);
            }
        });
        wizard.dataBean.setLogsButton(logsButton);
        exitButton = new I18nButton("MSG_EXIT_BUTTON");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                int result = JOptionPane.showConfirmDialog(wizard, I18n.getString("MSG_EXIT"), I18n.getString("MSG_PRODUCT"), JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    closePerform();
                }
            }
        });
    }

    public void init() {
        wizard.setTitle(I18n.getString("MSG_TITLE"));
        wizard.setResizable(false);
        showPanel(nowPanel);
        wizard.add(contentPanel, BorderLayout.CENTER);
        JSeparator separator = new JSeparator();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        controlPanel.setLayout(new GraphPaperLayout(new Dimension(20, 3), 1, 1));
        controlPanel.add(separator, new Rectangle(0, 0, 21, 1));
        controlPanel.add(localeUICombo, new Rectangle(0, 1, 2, 1));
        controlPanel.add(logsButton, new Rectangle(2, 1, 3, 1));
        controlPanel.add(previewButton, new Rectangle(8, 1, 4, 1));
        controlPanel.add(nextButton, new Rectangle(12, 1, 4, 1));
        controlPanel.add(exitButton, new Rectangle(17, 1, 3, 1));
        wizard.add(controlPanel, BorderLayout.PAGE_END);
        wizard.setSize(700, 500);
        centre(wizard);
        wizard.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        wizard.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closePerform();
            }
        });
    }

    public void addPanel(PanelImpl panel) {
        int count = panels.size();
        ++count;
        panels.put(count, panel);
    }

    public void addPanel(Integer number, PanelImpl panel) {
        panels.put(number, panel);
    }

    public boolean updateDate(boolean validate) {
        PanelImpl panel = panels.get(nowPanel);
        String message = panel.validateForm();
        if (Wizard.VATILDATE_FRUE.equals(message) || !validate) {
            panel.submitForm();
            wizard.dataBean = panel.getDataBean();
            return true;
        } else {
            JOptionPane.showMessageDialog(wizard, message, "Trackstudio Enterprise", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void showPanel(Integer number) {
        boolean showConsole = !number.equals(Wizard.CONSOLE);
        if ((number > 0 && number <= panels.size()) || !showConsole) {
            contentPanel.removeAll();
            PanelImpl panel = panels.get(number);
            panel.setDataBean(wizard.dataBean);
            panel.setCsv(csv);
            panel.updateDataForm();
            panel.removeAll();
            contentPanel.setLayout(new BorderLayout());
            panel.init();
            contentPanel.add(panel, BorderLayout.CENTER);
            contentPanel.updateUI();
        }
        if (!showConsole) {
            previewButton.setEnabled(false);
            nextButton.setEnabled(false);
        } else {
            previewButton.setEnabled(!(--number <= 0));
            nextButton.setEnabled(!(++number >= panels.size()-1));
        }

    }


    public static void centre(Window w) {
        Dimension us = w.getSize();
        Dimension them = Toolkit.getDefaultToolkit().getScreenSize();
        int newX = (them.width - us.width) / 2;
        int newY = (them.height - us.height) / 2;
        w.setLocation(newX, newY);

    }

    public void closePerform() {
        wizard.setVisible(false);
        wizard.dispose();
        System.exit(0);
    }

    public static ImageIcon getIcon(String file) {
        URL systemResource = ClassLoader.getSystemResource("resources/images/" + file);
        if (systemResource != null) {
            return new ImageIcon(systemResource);
        } else {
            return null;
        }
    }

    public static JScrollPane createPagingScrollPaneForTable(JTable jt) {
        JScrollPane jsp = new JScrollPane(jt);
        jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        return jsp;
    }

    private List<String> getAvailableUILocales() {
        Set<String> s = I18n.getInstance().getAvailableLocales();
        if (s != null) {
            return new ArrayList<String>(s);
        } else {
            return new ArrayList<String>();
        }
    }

    class UILanguageListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int ind = localeUICombo.getSelectedIndex();
            dataBean.setCurrentLocale(new Locale(availableUILocales.get(ind)));
            dataBean.setLog(dataBean.getCurrentLocale().toString());
            dataBean.setLog(I18n.getString("LOCALE_CHANGED", new String[]{dataBean.getCurrentLocale().toString()}));
            updateUIComponents();
        }
    }

    private void updateUIComponents() {
        wizard.setTitle(I18n.getString("MSG_TITLE"));
        validate();
        repaint();
        showPanel(nowPanel);
    }
}
