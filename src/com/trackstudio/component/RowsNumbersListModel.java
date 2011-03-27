package com.trackstudio.component;

import com.trackstudio.csvimport.CSVImport;

import javax.swing.AbstractListModel;


public class RowsNumbersListModel extends AbstractListModel{
    protected CSVImport csv;

    public RowsNumbersListModel (CSVImport csv) {
        this.csv = csv;
    }


    public int getSize() {
        if (csv.getPreview()!=null) {
            return csv.getPreview().size();
        } else {
            return 0;
        }
    }

    public Object getElementAt(int index) {
        return index + 1;
    }
}

    

