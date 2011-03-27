package com.trackstudio.gui.panel;

import com.trackstudio.component.I18nLabel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Header extends JPanel {
    public Header(I18nLabel header) {
        JPanel panelHeader = this;
        panelHeader.setSize(700, 100);
        header.setFont(new Font("MS Sans Serif", Font.BOLD, 14));
        panelHeader.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        panelHeader.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelHeader.add(header, BorderLayout.CENTER);
    }
}
