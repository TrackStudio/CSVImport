package com.trackstudio.gui.panel;

import com.trackstudio.component.GraphPaperLayout;
import com.trackstudio.data.DataBean;
import com.trackstudio.gui.panel.impl.PanelImpl;

import javax.swing.*;
import java.awt.*;

public class Console extends PanelImpl {
    private DataBean dataBean;
    private JTextArea console = new JTextArea(25, 60);

    public Console(DataBean dataBean) {
        this.dataBean = dataBean;
        JPanel jPanel = this;
        jPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel.setLayout(new GraphPaperLayout(new Dimension(5, 5), 1, 1));
        console.setText(this.dataBean.getLog());
        JScrollPane scrollingArea = new JScrollPane(console);
        jPanel.add(scrollingArea, new Rectangle(0, 0, 5, 5));
    }

    @Override
    public void submitForm() {

    }

    @Override
    public String validateForm() {
        return "";
    }

    @Override
    public void updateDataForm() {
    }

    @Override
    public void init() {
        console.setText(this.dataBean.getLog());
    }
}
