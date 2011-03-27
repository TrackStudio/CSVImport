

package com.trackstudio.component;


import com.trackstudio.csvimport.ImportResult;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Tor
 */
public class RowsNumbersSEListModel extends AbstractListModel{

    public List<Integer> stringNumbers = new ArrayList<Integer>();
    List<ImportResult> results;
    boolean state;

    public List<ImportResult> getResults() {
        return results;
    }

    public RowsNumbersSEListModel (List<ImportResult> results, boolean errors) {
        this.results = results;
        this.state = errors;
        if (getResults() != null){
            if (!state){
                int i=0;
                for (ImportResult c: getResults()){
                    if (c.isOK()) {
                        stringNumbers.add(c.getStringNumber());
                    }
                }
            } else {
                int i=0;
                for (ImportResult c: getResults()){
                    if (!c.isOK()) {
                        stringNumbers.add(c.getStringNumber());
                    }
                }
            }
        }
    }

    public int getSize() {
        return stringNumbers.size();
    }

    public Object getElementAt(int index) {

        JLabel label= null;


        Integer integer = stringNumbers.get(index);
        if (integer!=null){
            if (!state){
                label = new JLabel(integer.toString(), getIcon("yes.gif"),JLabel.LEFT);
            }
            else {
                label = new JLabel(integer.toString(), getIcon("no.gif"),JLabel.LEFT);
            }
        }
        return  label;


    }

    public static ImageIcon getIcon(String file) {

        URL systemResource = ClassLoader.getSystemResource("images/" + file);
        if (systemResource != null)
            return new ImageIcon(systemResource);
        else return null;
    }
}

    

