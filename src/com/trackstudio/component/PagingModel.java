package com.trackstudio.component;

/**
 *
 * @author Sunny

 * A larger table model that performs "paging" of its data. This model
 * reports a small number of rows (like 100 or so) as a "page" of data. You
 * can switch pages to view all of the rows as needed using the pageDown()
 * and pageUp() methods. Presumably, access to the other pages of data is
 * dictated by other GUI elements such as up/down buttons, or maybe a text
 * field that allows you to enter the page number you want to display.
 */
import com.trackstudio.csvimport.CSVImport;

import javax.swing.table.*;
import java.util.List;

public class PagingModel extends AbstractTableModel {
    protected CSVImport csv;

    public PagingModel(CSVImport csv) {
        this.csv = csv;
    }

    public int getRowCount() {
        if (csv.getPreview()!=null)
            return csv.getPreview().size();
        else return 0;
    }

    public int getColumnCount() {
        return csv.getHeaders().length;
    }

    public Object getValueAt(int row, int col) {
        if (row< getRowCount() && col< getColumnCount())
            try{
                return csv.getPreview().get(row)[col];
            } catch (ArrayIndexOutOfBoundsException aiob){
                System.out.println("row="+row+" , col="+col);
                return null;
            }
        else return null;
    }


    public String getColumnName(int col) {
        if (csv.getHeaders()!=null)
            return csv.getHeaders()[col];
        return null;
    }
}
