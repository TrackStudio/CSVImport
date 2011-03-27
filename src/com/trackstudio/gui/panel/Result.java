package com.trackstudio.gui.panel;

import com.trackstudio.component.*;
import com.trackstudio.csvimport.CSVImport;
import com.trackstudio.csvimport.ImportResult;
import com.trackstudio.csvimport.ImportWorker;
import com.trackstudio.data.DataBean;
import com.trackstudio.gui.Wizard;
import com.trackstudio.gui.panel.impl.PanelImpl;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Result extends PanelImpl {
    private JPanel resultsTablePanel;
    private static JTextArea resultsConsole;
    public JScrollPane scrolledResultsConsole;
    private List<ImportResult> results;
    private JTable resultsTable;
    private DataBean dataBean;
    private CSVImport cvs;
    private ListSelectionListener selectionListaner;
    private static final int ALL = 0, SUCCESS = 1, FAIL = 2;
    private JList rowHeader = new JList();
    private JTabbedPane tabbedPane;
    public JProgressBar importProgress;
    private JDialog createDbDialog;

    public Result(DataBean dataBean, CSVImport cvs) {
        this.dataBean = dataBean;
        this.cvs = cvs;
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

    @Override
    public void init() {
        showImportProgressDialog();
        JPanel panelResult = this;
        I18n.setLocale(this.dataBean.getCurrentLocale().toString());
        panelResult.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelResult.setLayout(new BorderLayout());
        I18nLabel header = new I18nLabel("MSG_RESULTS_LABEL");
        panelResult.add(new Header(header), BorderLayout.NORTH);

        resultsTablePanel = new JPanel();
        resultsTablePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        resultsTablePanel.setLayout(new BorderLayout());
        panelResult.add(resultsTablePanel, BorderLayout.CENTER);

        JPanel consolePanel = new JPanel();
        consolePanel.setLayout(new BorderLayout());

        resultsConsole = new JTextArea();
        resultsConsole.setTabSize(5);
        resultsConsole.setRows(4);
        resultsConsole.setAutoscrolls(true);
        scrolledResultsConsole = new JScrollPane(resultsConsole, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrolledResultsConsole.setAutoscrolls(true);
        consolePanel.add(scrolledResultsConsole, BorderLayout.CENTER);
        panelResult.add(consolePanel, BorderLayout.SOUTH);
    }


    public List<ImportResult> getResults() {
        return results;
    }

    public void showFilteredResults(int filter) {
        int errorCount = 0;
        for(ImportResult r: results){
            if (!r.isOK()) {
                errorCount++;
            }
        }
        resultsTable = new JTable();
        selectionListaner = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                showMessageResults(resultsTable.getSelectedRow());
            }
        };
        changeModel(filter);
        JScrollPane scrollPane = Wizard.createPagingScrollPaneForTable(resultsTable);
        resultsTablePanel.removeAll();
        tabbedPane = new JTabbedPane();
        int success = results.size()-errorCount;
        tabbedPane.addTab(I18n.getString("MSG_ALL_FILTER") + " (" + results.size() + ")", null, null, I18n.getString("MSG_ALL_FILTER"));
        tabbedPane.addTab(I18n.getString("MSG_SUCCESS_FILTER") + " (" + success + ")", null, null, I18n.getString("MSG_SUCCESS_FILTER"));
        tabbedPane.addTab(I18n.getString("MSG_ERROR_FILTER") + " (" + (errorCount) + ")", null, null, I18n.getString("MSG_ERROR_FILTER"));
        tabbedPane.setIconAt(1, Wizard.getIcon("yes.gif"));
        tabbedPane.setIconAt(2, Wizard.getIcon("no.gif"));
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                int c = tabbedPane.getSelectedIndex();
                if ((c != -1)) {
                    changeModel(c);
                }
            }
        });
        tabbedPane.setComponentAt(0, scrollPane);
        tabbedPane.setSelectedIndex(filter);
        resultsTablePanel.add(tabbedPane, BorderLayout.CENTER);
        resultsTablePanel.revalidate();
    }

    public void showMessage(String message, String result) {
        resultsConsole.setText("");
        resultsConsole.setText(MessageFormat.format(message, result));
        this.dataBean.setLog(MessageFormat.format(message, result));
    }

    public void showMessageResults(int selectedRow) {
        if (selectedRow != -1) {
            ImportResult result = getResults().get(selectedRow);
            if ((result.isOK())) {
                if (result.getType() == ImportResult.TASK) {
                    if (result.getComment().startsWith("_"))
                        showMessage(I18n.getString("MSG_TASK_WAS_EDITED"), result.getComment().substring(1));
                    else if (result.isOK())
                        showMessage(I18n.getString("MSG_TASK_WAS_CREATED"), result.getComment());
                } else if (result.getType() == ImportResult.USER) {
                    if (result.getComment().startsWith("_"))
                        showMessage(I18n.getString("MSG_USER_WAS_EDITED"),  result.getComment().substring(1));
                    else
                        showMessage(I18n.getString("MSG_USER_WAS_CREATED"),  result.getComment());
                } else if (result.getType() == ImportResult.MESSAGE) {
                    resultsConsole.setText(I18n.getString("MSG_OPERATION_WAS_CREATED"));
                }
            } else {
                resultsConsole.setText( result.getComment());
            }
            resultsConsole.repaint();
        } else {
            resultsConsole.setText("");
            resultsConsole.repaint();
        }
    }

    private void changeModel(int filter) {
        if (filter == ALL) {
            PagingModel pageModel = new PagingModel(cvs);
            resultsTable.setModel(pageModel);
        } else if ((filter == SUCCESS) || (filter == FAIL)) {
            SEPagingModel successpageModel = new SEPagingModel(getResults(), filter == FAIL) {
                public String getColumnName(int col) {
                    if (cvs.getHeaders() != null) {
                        return cvs.getHeaders()[col];
                    }
                    return null;
                }
            };
            resultsTable.setModel(successpageModel);
        }
        resultsTable.getSelectionModel().addListSelectionListener(selectionListaner);
    }

    public void showImportProgressDialog() {
        this.dataBean.setLog(I18n.getString("START_IMPORTING"));
        importProgress = new JProgressBar();
        importProgress.setMinimum(0);
        importProgress.setMaximum(100);
        importProgress.setValue(0);
        importProgress.setStringPainted(true);
        importProgress.setVisible(false);

        createDbDialog = new JDialog((JFrame) this.getParent(), I18n.getString("MSG_PRODUCT"), false);

        createDbDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        createDbDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                createDbDialog.setVisible(false);
                createDbDialog.dispose();

            }
        });
        Container dialogCp = createDbDialog.getContentPane();

        JPanel dialogPanel = new JPanel();

        dialogPanel.setLayout(new BorderLayout());
        dialogPanel.setPreferredSize(new Dimension(320, 30));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        //dialogPanel.add(importProgress, BorderLayout.CENTER);
        dialogPanel.add(new I18nLabel("MSG_CONNECTING"), BorderLayout.CENTER);
        dialogCp.add(dialogPanel);

        //createDbDialog.setSize(100,50);
        createDbDialog.pack();
        Wizard.centre(createDbDialog);
        createDbDialog.setVisible(true);
        dialogPanel.add(importProgress, BorderLayout.CENTER);
        try {
            try {
                if (cvs.getSessionId() == null) {
                    cvs.refreshConnection();
                }

                cvs.refreshCSVData();
                DateFormatter dateFormatter = new DateFormatter(this.dataBean.getTimezone(), this.dataBean.getLocale());
                ImportWorker<ImportResult> w = new ImportWorker<ImportResult>(this.dataBean, cvs.getReader(), cvs.getTaskService(), cvs.getUserService(), cvs.getMessageService(), dateFormatter, cvs.getSessionId(), cvs.isCreateNewElement(), cvs.getRootElement()){
                    @Override
                    protected void process(List<ImportResult> chunks) {
                        if (chunks!=null && !chunks.isEmpty())
                            if (getProgress()>0){
                                importProgress.setValue(getProgress());
                            }
                        importProgress.revalidate();
                    }

                    @Override
                    protected void done() {
                        try {
                            results = get();
                            createDbDialog.setVisible(false);
                            createDbDialog.dispose();
                            showFilteredResults(ALL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                };
                w.setSize(cvs.getSize());
                importProgress.setVisible(true);
                w.execute();
            } catch (MalformedURLException mex) {
                deactivateProgressDialog();
                JOptionPane.showMessageDialog(null, I18n.getString("MSG_INVALID_URL_MESSAGE")/*"Invalid URL"*/, "Trackstudio Enterprise", JOptionPane.ERROR_MESSAGE);
                this.dataBean.setLog("ERROR:" + I18n.getString("MSG_INVALID_URL_MESSAGE"));
                this.dataBean.setLog(mex);
            } catch (java.net.ConnectException exc) {
                deactivateProgressDialog();
                JOptionPane.showMessageDialog(null, I18n.getString("MSG_CONNECTION_ERROR_MESSAGE") + "\n" + exc.getMessage()/*"Connection exception"*/, "Trackstudio Enterprise", JOptionPane.ERROR_MESSAGE);
                this.dataBean.setLog("ERROR:" + I18n.getString("MSG_CONNECTION_ERROR_MESSAGE"));
                this.dataBean.setLog(exc);
            } catch (SocketException exc2) {
                deactivateProgressDialog();
                JOptionPane.showMessageDialog(null, I18n.getString("MSG_CONNECTION_ERROR_MESSAGE") + "\n" + exc2.getMessage()/*"Connection exception"*/, "Trackstudio Enterprise", JOptionPane.ERROR_MESSAGE);
                this.dataBean.setLog("ERROR:" + I18n.getString("MSG_CONNECTION_ERROR_MESSAGE"));
                this.dataBean.setLog(exc2);
            } catch (Exception e) {
                this.dataBean.setLog("ERROR:" + e.getMessage());
                this.dataBean.setLog(e);
                deactivateProgressDialog();
            }
        } catch (Exception e) {
            this.dataBean.setLog(e);
        }
        importProgress.setValue(0);
        importProgress.revalidate();
    }

    public void deactivateProgressDialog() {
        createDbDialog.setVisible(false);
        createDbDialog.dispose();
    }
}
