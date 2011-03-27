/*
 * FileFilter.java
 *
 * Created on 24 ���� 2008 �., 13:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.trackstudio.component;

/**
 *
 * @author Sunny
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class CSVFileFilter extends javax.swing.filechooser.FileFilter {
    protected String description;
    protected ArrayList exts = new ArrayList();

    public void addType(String s) {
        exts.add(s);
    }

    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        else if (f.isFile())
            for (Iterator it = exts.iterator(); it.hasNext();)
                if (f.getName().endsWith((String) it.next()))
                    return true;
        return false;
    }

    public void setDescription(String s) {
        description = s;
    }

    public String getDescription() {
        return description;
    }
}
