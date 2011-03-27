
package com.trackstudio.component;

import com.trackstudio.csvimport.CSVImport;
import com.trackstudio.csvimport.ImportResult;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
public class RowsNumbersListResultsModel extends AbstractListModel{
    List<ImportResult> results;
    public RowsNumbersListResultsModel(List<ImportResult> r) {
        super();
        results = r;
    }
    public int getSize(){
        return results.size();
    }

    public List<ImportResult> getResults() {
        return results;
    }

    public Object getElementAt(int index) {
        if (getResults()!=null){
            JLabel label;
            if ((getResults().get(index)!=null) && (getResults().get(index).isOK())) {
                label = new JLabel(Integer.toString(index+1),getIcon("yes.gif"),JLabel.LEFT);
            }else {
                label = new JLabel(Integer.toString(index+1),getIcon("no.gif"),JLabel.LEFT);
            }
            label.setIconTextGap(4);
            label.setHorizontalAlignment(JLabel.LEFT);
            return  label;
        } else {
            return null;
        }
    }

    public static ImageIcon getIcon(String file) {
        URL systemResource = ClassLoader.getSystemResource("images/" + file);
        if (systemResource != null)
            return new ImageIcon(systemResource);
        else return null;
    }
}

    

