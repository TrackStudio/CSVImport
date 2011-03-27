package com.trackstudio.gui.panel;

import com.trackstudio.component.*;
import com.trackstudio.csvimport.CSVImport;
import com.trackstudio.data.DataBean;
import com.trackstudio.gui.Wizard;
import com.trackstudio.gui.panel.impl.PanelImpl;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class Preview extends PanelImpl {
    private JPanel previewTablePanel;
    private CSVImport csv;
    private DataBean dataBean;

    public Preview(DataBean dataBean, CSVImport csv) {
        this.csv = csv;
        this.dataBean = dataBean;
    }

    public void init() {
        JPanel panelPreview = this;
        I18n.setLocale(this.dataBean.getCurrentLocale().toString());
        panelPreview.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelPreview.setLayout(new BorderLayout());
        previewTablePanel = new JPanel();
        previewTablePanel.setLayout(new BorderLayout());
        previewTablePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        drawTable();
        panelPreview.add(previewTablePanel, BorderLayout.CENTER);
        I18nLabel header = new I18nLabel("MSG_FILE_IS_LABEL");
        panelPreview.add(new Header(header), BorderLayout.NORTH);
    }

    @Override
    public void submitForm() {
    }

    @Override
    public String validateForm() {
        return Wizard.VATILDATE_FRUE;
    }

    @Override
    public void updateDataForm() {
    }

    public void drawTable() {
        csv.changeFileInfo(this.dataBean.getFilePath(), this.dataBean.getEncoding(), this.dataBean.getDelimiter());
        PagingModel pageModel = new PagingModel(csv);
        RowsNumbersListModel listModel = new RowsNumbersListModel(csv);
        JList rowHeader = new JList(listModel);
        JTable fileTable = new JTable(pageModel);
        fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        rowHeader.setFixedCellHeight(fileTable.getRowHeight());
        rowHeader.setCellRenderer(new RowHeaderRenderer(fileTable));
        JScrollPane jsp = Wizard.createPagingScrollPaneForTable(fileTable);
        jsp.setRowHeaderView(rowHeader);
        int margin = 5;
        for (int i = 0; i < fileTable.getColumnCount(); i++) {
            TableColumnModel colModel  =fileTable.getColumnModel();
            TableColumn col = colModel.getColumn(i);
            TableCellRenderer renderer = col.getHeaderRenderer();
            if (renderer == null) {
                renderer = fileTable.getTableHeader().getDefaultRenderer();
            }
            Component comp = renderer.getTableCellRendererComponent(fileTable, col.getHeaderValue(), false, false, 0, 0);
            int width = comp.getPreferredSize().width;
            renderer = fileTable.getCellRenderer(0, i);
            comp = renderer.getTableCellRendererComponent(fileTable, fileTable.getValueAt(0, i), false, false, 0, i);
            width = Math.max(width, comp.getPreferredSize().width);
            width += 2 * margin;
            col.setPreferredWidth(width);
        }
        previewTablePanel.removeAll();
        previewTablePanel.add(jsp, BorderLayout.CENTER );
    }
}
