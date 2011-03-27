package com.trackstudio.component;

/**
 *
 * @author Sunny
 */
// A larger table model that performs "paging" of its data. This model
// reports a small number of rows (like 100 or so) as a "page" of data. You
// can switch pages to view all of the rows as needed using the pageDown()
// and pageUp() methods. Presumably, access to the other pages of data is
// dictated by other GUI elements such as up/down buttons, or maybe a text
// field that allows you to enter the page number you want to display.
//
import com.trackstudio.csvimport.ImportResult;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SEPagingModel extends AbstractTableModel {
    private boolean state = false;
    ArrayList<ImportResult> success, failed;
    private int columnCount = 0;
    public ArrayList<ImportResult> getSuccess() {
        return success;
    }

    public void setSuccess(ArrayList<ImportResult> success) {
        this.success = success;
    }

    public ArrayList<ImportResult> getFailed() {
        return failed;
    }

    public void setFailed(ArrayList<ImportResult> failed) {
        this.failed = failed;
    }

    public int getSize(){
        if (state) return failed.size();
        else return success.size();
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public SEPagingModel(List<ImportResult> r, boolean errors) {

        super();
        this.success = new ArrayList<ImportResult>();
        this.failed = new ArrayList<ImportResult>();
        this.state = errors;
        if (r!=null && !r.isEmpty()){
            columnCount = r.get(0).getLine().length;
        }
        for (ImportResult c: r) {
            if (c.isOK()) {
                success.add(c);
            } else  {
                failed.add(c);
            }
        }

    }

    public int getRowCount() {
        return getSize();
    }

    public int getColumnCount() {
        return columnCount;
    }

    public Object getValueAt(int row, int col) {
        if (state) {
            if (row>=0 && row<failed.size())
                return failed.get(row).getLine()[col];
        } else {
            if (row>=0 && row<success.size())
                return success.get(row).getLine()[col];
        }
        return null;
    }
}
