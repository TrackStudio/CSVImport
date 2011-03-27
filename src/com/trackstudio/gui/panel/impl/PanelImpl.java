package com.trackstudio.gui.panel.impl;

import com.trackstudio.csvimport.CSVImport;
import com.trackstudio.data.DataBean;

import javax.swing.*;

public abstract class PanelImpl extends JPanel {
    private DataBean dataBean;
    private CSVImport csv;
    abstract public void submitForm();
    abstract public String validateForm();
    abstract public void updateDataForm();
    abstract public void init();

    public DataBean getDataBean() {
        return dataBean;
    }

    public void setDataBean(DataBean dataBean) {
        this.dataBean = dataBean;
    }

    public CSVImport getCsv() {
        return csv;
    }

    public void setCsv(CSVImport csv) {
        this.csv = csv;
    }
}
