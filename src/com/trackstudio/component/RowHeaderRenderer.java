package com.trackstudio.component;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class RowHeaderRenderer extends JLabel implements ListCellRenderer {

    public RowHeaderRenderer(JTable table) {
        JTableHeader header = table.getTableHeader();
        setOpaque(true);
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setHorizontalAlignment(CENTER);
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setFont(header.getFont());

    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value == null) {
            this.setText("");
            return this;
        } else {
            if (value instanceof JLabel) {
                String s = ((JLabel) value).getText();
                this.setText(s);
                this.setIcon(((JLabel) value).getIcon());

            } else {
                String s = value.toString();
                this.setText(s);
            }
        }
        return this;
    }
}
