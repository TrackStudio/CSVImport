package com.trackstudio.csvimport;


//import com.trackstudio.gui.GUI;
//import com.trackstudio.gui.I18n;
import com.trackstudio.component.I18n;
import com.trackstudio.data.DataBean;
import com.trackstudio.soap.service.find.Find;
import com.trackstudio.soap.service.find.UserBean;
import com.trackstudio.soap.service.message.Message;
import com.trackstudio.soap.service.find.TaskBean;
import com.trackstudio.soap.service.message.MessageService;
import com.trackstudio.soap.service.task.Task;
import com.trackstudio.soap.service.task.TaskService;
import com.trackstudio.soap.service.user.User;
import com.trackstudio.soap.service.user.UserService;
import com.trackstudio.soap.service.find.FindService;

import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.net.MalformedURLException;

import java.net.URL;
import java.rmi.RemoteException;

import java.util.*;
import java.lang.Exception;

public class CSVImport {

    private int readed;
    private String url;
    private String rootElement;
    private boolean createNewElement = true;
    private DataBean dataBean;

    public CSVImport(DataBean dataBean) {
        this.dataBean = dataBean;
    }

    public String getRootElement() {
        return rootElement;
    }

    public void setRootElement(String rootElement) {
        this.rootElement = rootElement;
    }

    public boolean isCreateNewElement() {
        return createNewElement;
    }

    public void setCreateNewElement(boolean createNewElement) {
        this.createNewElement = createNewElement;
    }

    public abstract class Searcher<T> {
        protected String searchString;
        CSVImport csv;

        public Searcher(CSVImport csv, String searchString) {
            this.searchString = searchString;
            this.csv = csv;
        }

        public abstract T[] search() throws RemoteException;


    }

    public int getReaded() {
        return readed;
    }

    private boolean isAuthenticate = false;

    //   private GUI gui;
    private long size;


    public QBConverter getReader() {
        return reader;
    }//private String[][] dataValues;

    private QBConverter reader;

    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    private String password;
    private String login;
    private String fileName, encoding, delimiter, mapping;


    private List<String[]> preview = null;

    public List<String[]> getPreview() {
        try {
            if (preview == null) {
                initPreview();
            }
        } catch (IOException ioe) {
            this.dataBean.setLog(ioe);
        }
        return preview;
    }


//    public CSVImport(GUI g) {
//        this.gui = g;
//
//    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public void changeFileInfo(String fileName, String encoding, String delimiter) {
        try {
            this.dataBean.setLog(I18n.getString("CHANGE_FILE_INFO"));
            this.fileName = fileName;
            this.encoding = encoding;
            this.delimiter = delimiter;
            refreshCSVData();
            initPreview();
        } catch (Exception ioe) {
            this.dataBean.setLog(ioe);
        }
    }

    public void changeConnectionSettings(String serviceURL, String login, String password) throws Exception {
        this.dataBean.setLog(I18n.getString("CHANGING_CONNECTION_SETTINGS"));
        this.login = login;
        this.password = password;
        this.url = serviceURL;
        refreshConnection();
    }


    public String[] getHeaders() {
        return this.reader.getHeaders();
    }


    public boolean checkDelimiter() {
        return false;
    }


    public long getSize() {
        return size;
    }

    public void refreshCSVData() throws IOException {
        this.dataBean.setLog(I18n.getString("REFRESHING_CSV_DATA"));
        File f = new File(fileName);
        if (f.exists()) size = f.length();
        this.reader = new QBConverter(new InputStreamReader(new FileInputStream(fileName), encoding), delimiter.charAt(0), this.mapping);
        // this.reader.setHeaders( this.reader.readNext());

    }

    private void initPreview() throws IOException {
        readed = -1;
        preview = new ArrayList<String[]>();
        String[] nextline;
        while (readed < 100 && (nextline = reader.readNext()) != null) {
            if (nextline.length > 0) {
                preview.add(nextline);
                readed++;
            }
        }
    }


    public int getDataValueColumnsLength() {
        return getHeaders().length;
    }

    public User getUserService() throws MalformedURLException {
        UserService service = new UserService(new URL(url + User.class.getSimpleName() + "?wsdl"), new QName("http://user.service.soap.trackstudio.com/", "UserService"));
        return service.getUserPort();
    }

    public Message getMessageService() throws MalformedURLException {
        MessageService service = new MessageService(new URL(url + Message.class.getSimpleName() + "?wsdl"), new QName("http://message.service.soap.trackstudio.com/", "MessageService"));
        return service.getMessagePort();
    }

    public Task getTaskService() throws MalformedURLException {
        TaskService service = new TaskService(new URL(url + Task.class.getSimpleName() + "?wsdl"), new QName("http://task.service.soap.trackstudio.com/", "TaskService"));
        return service.getTaskPort();
    }

    private Find getFindService() throws MalformedURLException {
        FindService service = new FindService(new URL(url + Find.class.getSimpleName() + "?wsdl"), new QName("http://find.service.soap.trackstudio.com/", "FindService"));
        return service.getFindPort();
    }


    public boolean refreshConnection() throws MalformedURLException, Exception {
        this.dataBean.setLog(I18n.getString("REFRESHING_CONNECTION"));
        this.sessionId = getUserService().authenticate(login, password);
        return sessionId != null;
    }


    public boolean getAuthenticate() {
        return isAuthenticate;
    }


    public com.trackstudio.soap.service.task.TaskBean findTask(String number) {
        try {
            return getTaskService().findTaskByNumber(sessionId, number);
        } catch (MalformedURLException m) {
            this.dataBean.setLog(m);
        } catch (Exception e) {
            this.dataBean.setLog(e);
        }
        return null;
    }

    public TaskBean getTask(String id) {
        try {
            return getFindService().findTaskById(sessionId, id);
        } catch (MalformedURLException m) {
            this.dataBean.setLog(m);
        } catch (Exception e) {
            this.dataBean.setLog(e);
        }
        return null;
    }


    public List<TaskBean> searchTasks(String key) throws Exception {
        Map<String, TaskBean> tasks = new HashMap<String, TaskBean>();
        for (TaskBean task : getFindService().searchTasks(sessionId, key)) {
            tasks.put(task.getId(), task);
        }
        List<TaskBean> taskList = new ArrayList<TaskBean>();
        for (Map.Entry<String, TaskBean> entry : tasks.entrySet()) {
            taskList.add(entry.getValue());
        }
        return taskList;
    }

    public List<UserBean> searchUsers(String key) throws Exception {
        Map<String, UserBean> users = new HashMap<String, UserBean>();
        for (UserBean user : getFindService().searchUsers(sessionId, key)) {
            users.put(user.getLogin(), user);
        }
        List<UserBean> userList = new ArrayList<UserBean>();
        for (Map.Entry<String, UserBean> entry : users.entrySet()) {
            userList.add(entry.getValue());
        }
        return userList;
    }
}
