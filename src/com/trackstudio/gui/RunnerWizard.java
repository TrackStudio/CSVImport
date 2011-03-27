package com.trackstudio.gui;

import com.trackstudio.csvimport.CSVImport;
import com.trackstudio.data.DataBean;
import com.trackstudio.gui.panel.*;

import javax.swing.*;

public class RunnerWizard {
    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DataBean dataBean = new DataBean();
                CSVImport csv = new CSVImport(dataBean);
                Wizard wizard = new Wizard(dataBean);
                wizard.addPanel(new Welcom(dataBean));
                wizard.addPanel(new Connect(dataBean, csv));
                wizard.addPanel(new ChooseFile(dataBean, csv));
                wizard.addPanel(new Preview(dataBean, csv));
                wizard.addPanel(new ChooseRoot(dataBean, csv));
                wizard.addPanel(new Result(dataBean, csv));
                wizard.addPanel(Wizard.CONSOLE, new Console(dataBean));
                wizard.init();
                wizard.setVisible(true);
            }
        });

    }
}
