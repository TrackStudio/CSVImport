package com.trackstudio.csvimport;

import au.com.bytecode.opencsv.CSVReader;

import javax.swing.*;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.io.IOException;


import com.trackstudio.component.FieldMap;
import com.trackstudio.component.DateFormatter;
import com.trackstudio.data.DataBean;
import com.trackstudio.soap.service.task.Task;
import com.trackstudio.soap.service.message.Message;
import com.trackstudio.soap.service.user.User;

public abstract class ImportWorker<T>  extends SwingWorker<List<T>, T> {

    QBConverter reader;
    TaskImporter task;
    UserImporter user;
    MessageImporter message;
    private long size; // all job
    private long completion;
    private String[] getHeaders() {
        return reader.getHeaders();
    }

    public void setSize(long size) {
        this.size = size;
    }

    public ImportWorker(DataBean dataBean, QBConverter reader, Task task, User user, Message message, DateFormatter f, String sessionId, boolean updateTask, String parentField) {
        this.reader = reader;
        this.task = new TaskImporter(dataBean, task, f, getHeaders(), sessionId, updateTask, parentField);
        this.user = new UserImporter(dataBean, user, f, getHeaders(), sessionId, parentField);
        this.message = new MessageImporter(dataBean, message, f, getHeaders(), sessionId, updateTask, parentField);
    }

    @Override
    protected List<T> doInBackground() throws Exception {
        completion = 0L;
        HashMap<String, String> taskMap = new HashMap<String, String>();
        List<T> results = new ArrayList<T>();
        String[] nextline;
        int current = 0;
        while ((nextline = reader.readNext()) != null) {
            current++;
            ImportResult r = null;
            if (nextline.length==1 && nextline[0].length()==0) {
                return results;
            }
            completion += reader.getBytesReaded();
            String oldNumber = FieldChecker.getFieldValue(nextline, getHeaders(), FieldMap.TASK_NUMBER);
            if ((FieldChecker.getFieldValue(nextline, getHeaders(), FieldMap.TASK_NAME).length() != 0) || (oldNumber.length() != 0)) {
                r = task.process(current, nextline, taskMap);
            } else if (FieldChecker.getFieldValue(nextline, getHeaders(), FieldMap.MESSAGE_TASK).length()!=0) {
                r = message.process(current, nextline, taskMap);
            } else if (FieldChecker.getFieldValue(nextline, getHeaders(), FieldMap.USER_LOGIN).length() != 0 && FieldChecker.getFieldValue(nextline, getHeaders(), FieldMap.USER_NAME).length() != 0) {
                r = user.process(current, nextline);
            } if (r!=null) {
                results.add((T)r);
                int progress1 = (int) (100 * ((float) completion / (float) size));
                setProgress(progress1);
                publish((T)r);
            }
        }
        return results;

    }


}
