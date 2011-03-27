package com.trackstudio.gui.panel;

import com.trackstudio.component.*;
import com.trackstudio.csvimport.CSVImport;
import com.trackstudio.csvimport.FieldChecker;
import com.trackstudio.data.DataBean;
import com.trackstudio.gui.Wizard;
import com.trackstudio.gui.panel.impl.PanelImpl;
import com.trackstudio.soap.service.find.TaskBean;
import com.trackstudio.soap.service.find.UserBean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChooseRoot extends PanelImpl {
    private DataBean dataBean;
    private JComboBox resultSearch = new JComboBox();
    private JTextField searchWorld = new JTextField(20);
    private CSVImport cvs;


    public ChooseRoot(DataBean dataBean, CSVImport cvs) {
        this.dataBean = dataBean;
        this.cvs = cvs;
    }

    @Override
    public void submitForm() {

    }

    @Override
    public void init() {
        JPanel panelChooseRoot = this;
        I18n.setLocale(this.dataBean.getCurrentLocale().toString());
        I18nButton searchBtn = new I18nButton("BTN_SEARCH");

        panelChooseRoot.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelChooseRoot.setLayout(new GraphPaperLayout(new Dimension(10, 17), 1, 1));

        I18nLabel header = new I18nLabel(I18n.getString("CHOOSE_ROOT"));
        JLabel resultRearchLabel = new JLabel(I18n.getString("RESULT_SEARCH"));
        I18nLabel helpLabel = new I18nLabel(I18n.getString("CHOOSE_ROOT_HELP"));
        helpLabel.setUI(new MultiLineLabelUI());
        JCheckBox createNewElement = new JCheckBox();
        I18nLabel updateDate = new I18nLabel(I18n.getString("MSG_CREATE_NEW"));
        panelChooseRoot.add(new Header(header), new Rectangle(0, 0, 10, 2));

        panelChooseRoot.add(helpLabel, new Rectangle(1, 3, 10, 2));
        panelChooseRoot.add(searchWorld, new Rectangle(1, 6, 4, 1));
        panelChooseRoot.add(searchBtn, new Rectangle(5, 6, 3, 1));
        panelChooseRoot.add(resultRearchLabel,new Rectangle(1, 8, 2, 1));
        panelChooseRoot.add(resultSearch, new Rectangle(3, 8, 3, 1));
        panelChooseRoot.add(createNewElement, new Rectangle(1, 10, 1, 1));
        updateDate.setUI(new MultiLineLabelUI());
        panelChooseRoot.add(updateDate, new Rectangle(2, 10, 10, 2));

        searchBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                try {
                    resultSearch.removeAllItems();
                    if (cvs.getHeaders() == null || !FieldChecker.isCorrectHeader(cvs.getHeaders())) {
                        JOptionPane.showMessageDialog(null, I18n.getString("MSG_INVALID_HEADER_MESSAGE"), "TrackStudio Enterprise", JOptionPane.WARNING_MESSAGE);
                    } else {
                        if (FieldChecker.checkTaskFieldNames(cvs.getHeaders()) || FieldChecker.checkMessagesFieldNames(cvs.getHeaders())) {
                            for (TaskBean taskBean : cvs.searchTasks(searchWorld.getText())) {
                                resultSearch.addItem(taskBean.getName()+" [#"+taskBean.getNumber()+"]");
                            }
                        } else if (FieldChecker.checkUserFieldNames(cvs.getHeaders())) {
                            for (UserBean userBean : cvs.searchUsers(searchWorld.getText())) {
                                resultSearch.addItem(userBean.getName()+" ("+userBean.getLogin()+")");
                            }
                        }
                    }
                    cvs.setRootElement(getResult());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }

        });
        createNewElement.setSelected(cvs.isCreateNewElement());
        createNewElement.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                cvs.setCreateNewElement(!abstractButton.isSelected());
            }
        });
    }

    private String getResult(){
        String result = resultSearch.getSelectedItem() != null ? resultSearch.getSelectedItem().toString() : "";
        if (result.lastIndexOf("[#") != -1) {
            return result.substring(result.lastIndexOf("[#")+"[#".length(), result.length()-1);
        } else if (result.indexOf("(") != -1) {
            return result.substring(result.lastIndexOf("(") + "(".length(), result.length()-1);
        } else {
            return null;
        }
    }

    @Override
    public String validateForm() {
        String result = resultSearch.getSelectedItem() != null ? resultSearch.getSelectedItem().toString() : "";
        if (result.length() == 0) {
            return I18n.getString("NO_SELECTED_ROOT_ELEMENT");
        }
        return Wizard.VATILDATE_FRUE;
    }

    @Override
    public void updateDataForm() {
    }
}