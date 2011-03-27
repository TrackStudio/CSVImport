package com.trackstudio.csvimport;


import com.trackstudio.component.DateFormatter;
import com.trackstudio.component.I18n;
import com.trackstudio.data.DataBean;
import com.trackstudio.soap.service.find.TaskBean;
import com.trackstudio.soap.service.task.Task;
import com.trackstudio.component.FieldMap;

import java.text.ParseException;
import java.util.List;
import java.util.HashMap;

public class TaskImporter {
    Task task;
    DateFormatter dateformat;
    String[] headers;
    String parentField;
    String sessionId;
    private DataBean dataBean;

    boolean createNew;
    public TaskImporter(DataBean dataBean, Task task, DateFormatter f, String[] headers, String sessionId, boolean createNew, String parentField) {
        this.dataBean = dataBean;
        this.task = task;
        this.dateformat = f;
        this.headers = headers;
        this.parentField = parentField;
        this.sessionId = sessionId;
        this.createNew = createNew;

    }

    public ImportResult process(int current, String[] nextline, HashMap<String, String> taskMap) throws Exception {
        long taskBudget;
        long taskDeadline;
        long taskSubmitDate;
        long taskUpdateDate;
        long taskCloseDate;
        String newTaskNumber;

        StringBuffer error = new StringBuffer();

        taskBudget = 0;
        taskDeadline = -1;
        taskSubmitDate = -1;
        taskUpdateDate = -1;
        taskCloseDate = -1;
        String[] headersLocal = headers;
        if (createNew && FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_CATEGORY)==null) {
            this.dataBean.setLog("ERROR: Field '", FieldMap.TASK_CATEGORY + "' does not exists See CSV file, line number " + String.valueOf(current));
            error.append("ERROR: ").append(I18n.getString("MSG_FIELD")).append(" \"").append(FieldMap.TASK_CATEGORY).append("\" ").append(I18n.getString("MSG_TASK_INVALID_HEADER_MESSAGE")).append(" ").append(String.valueOf(current));
            return new ImportResult(current, nextline, error.toString(), ImportResult.TASK, false);

        }

        if (FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_BUDGET).length() != 0) {
            try {
                taskBudget = FieldChecker.parseBudget(FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_BUDGET));

            } catch (Exception e) {

                this.dataBean.setLog("ERROR: ", FieldMap.TASK_BUDGET + " can not be parsed. See CSV file, line number " + String.valueOf(current));
                error.append("ERROR: ").append(I18n.getString("MSG_FIELD")).append(" \"").append(FieldMap.TASK_BUDGET).append("\" ").append(I18n.getString("MSG_FIELD_CANNOT_BE_PARSED")).append(" ").append(String.valueOf(current));
                return new ImportResult(current, nextline, error.toString(), ImportResult.TASK, false);
            }
        }

        if (FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_DEADLINE).length() != 0) {
            try {

                taskDeadline = dateformat.parseToCalendar(FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_DEADLINE)).getTimeInMillis();
            } catch (ParseException e) {
                this.dataBean.setLog("ERROR: ", FieldMap.TASK_DEADLINE + " can not be parsed. See CSV file, line number " + String.valueOf(current) + " . The pattern for date fields is " + dateformat.getPattern());

                error.append("ERROR: ").append(I18n.getString("MSG_FIELD")).append(" \"").append(FieldMap.TASK_DEADLINE).append("\" ").append(I18n.getString("MSG_FIELD_CANNOT_BE_PARSED")).append(" ").append(String.valueOf(current)).append(I18n.getString("MSG_FIELD_DATE_PATTERN")).append(dateformat.getPattern());

                return new ImportResult(current, nextline, error.toString(), ImportResult.TASK, false);
            }
        }

        if (FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_SUBMITDATE).length() != 0) {
            try {
                taskSubmitDate = dateformat.parseToCalendar(FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_SUBMITDATE)).getTimeInMillis();
            } catch (ParseException e) {
                this.dataBean.setLog("ERROR: ", FieldMap.TASK_SUBMITDATE + " can not be parsed. See CSV file, line number " + String.valueOf(current) + " . " + I18n.getString("MSG_FIELD_DATE_PATTERN") + dateformat.getPattern());
                error.append("ERROR: ").append(I18n.getString("MSG_FIELD")).append(" \"").append(FieldMap.TASK_SUBMITDATE.getAltKey()).append("\" ").append(I18n.getString("MSG_FIELD_CANNOT_BE_PARSED")).append(" ").append(String.valueOf(current)).append(" . ").append(I18n.getString("MSG_FIELD_DATE_PATTERN")).append(dateformat.getPattern());

                return new ImportResult(current,nextline, error.toString(), ImportResult.TASK, false);
            }
        }

        if (FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_UPDATEDATE).length() != 0) {
            try {
                taskUpdateDate = dateformat.parseToCalendar(FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_UPDATEDATE)).getTimeInMillis();
            } catch (ParseException e) {
                this.dataBean.setLog("ERROR: ", FieldMap.TASK_UPDATEDATE + " can not be parsed. See CSV file, line number " + String.valueOf(current) + " . " + I18n.getString("MSG_FIELD_DATE_PATTERN") + dateformat.getPattern());
                error.append("ERROR: ").append(I18n.getString("MSG_FIELD")).append(" \"").append(FieldMap.TASK_UPDATEDATE).append("\" ").append(I18n.getString("MSG_FIELD_CANNOT_BE_PARSED")).append(" ").append(String.valueOf(current)).append(" . ").append(I18n.getString("MSG_FIELD_DATE_PATTERN")).append(dateformat.getPattern());
                return new ImportResult(current,nextline, error.toString(), ImportResult.TASK, false);
            }
        }
        if (FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_CLOSEDATE).length() != 0) {
            try {
                taskCloseDate = dateformat.parseToCalendar(FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_CLOSEDATE)).getTimeInMillis();
            } catch (ParseException e) {
                this.dataBean.setLog("ERROR: ", FieldMap.TASK_CLOSEDATE + " can not be parsed. See CSV file, line number " + String.valueOf(current) + " . " + I18n.getString("MSG_FIELD_DATE_PATTERN") + dateformat.getPattern());
                error.append("ERROR: ").append(I18n.getString("MSG_FIELD")).append(" \"").append(FieldMap.TASK_CLOSEDATE).append("\" ").append(I18n.getString("MSG_FIELD_CANNOT_BE_PARSED")).append(" ").append(String.valueOf(current)).append(" . ").append(I18n.getString("MSG_FIELD_DATE_PATTERN")).append(dateformat.getPattern());
                return new ImportResult(current, nextline, error.toString(), ImportResult.TASK, false);

            }
        }
        List<String> udfNames = FieldChecker.getUDFNames(headersLocal);
        try {

            String localParent = FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_PARENT);
            if (localParent==null || localParent.length()==0) localParent = parentField;
            // заменяем тот, что в файле, на тот, что проимпортировали
            if (!taskMap.isEmpty()) {
                String newNumber = taskMap.get(localParent);
                if (newNumber!=null) localParent = newNumber;
            }

            if (createNew && localParent != null && localParent.length() > 0 ) {
                if (task.findTaskByName(sessionId, localParent) == null) {
                    if (task.findTaskByNumber(sessionId, localParent) == null) {
                        throw new Exception("Task " + parentField +" not found");
                    }
                }
            }
            String oldTaskId = FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_NUMBER);

            String number = FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_NUMBER);
            newTaskNumber = task.importTask(sessionId,
                    createNew ? "": number,
                    FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_NAME),
                    FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_SHORTNAME),
                    FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_DESCRIPTION),
                    taskBudget,
                    taskDeadline,
                    FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_PRIORITY),
                    localParent,
                    FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.HUSER_NAME),
                    FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.HUSER_STATUS),
                    FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_CATEGORY),
                    FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.SUSER_NAME),
                    taskSubmitDate,
                    taskUpdateDate,
                    taskCloseDate,
                    FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_RESOLUTION),
                    FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_STATUS),
                    udfNames,
                    FieldChecker.getUDFValues(headersLocal, nextline)
            );
            taskMap.put(number, newTaskNumber);
            List<Integer> errorCode = task.getTaskErrors();//getErrorCode();
//
            String str = "";
            for (Integer anErrorCode : errorCode) {

                str = FieldChecker.getError( anErrorCode) + "\n";
            }

            error.append(str);
            logTaskLine(error.toString(), current, nextline);
            if (newTaskNumber != null && newTaskNumber.length() != 0) {
                if ((newTaskNumber.compareToIgnoreCase(oldTaskId) == 0)) {
                    return new ImportResult(current,nextline, "_"+newTaskNumber, ImportResult.TASK, true);
                } else {
                    return new ImportResult(current,nextline, newTaskNumber, ImportResult.TASK, true);
                }
            } else {
                return new ImportResult(current,nextline, error.toString(), ImportResult.TASK, false);
            }


        } catch (Exception e) {
            error.append(e.getMessage());
            this.dataBean.setLog(e);
            return new ImportResult(current,nextline, error.toString(), ImportResult.TASK, false);
            //continue;
        }
    }


    private void logTaskLine(String result, int current, String[] nextline) {
        String[] headersLocal = headers;
        if (result.length() == 0) {
            result = "OK";
        }
        this.dataBean.setLog("Result: "+result+". Task: send line " + current + " with values: "
                + FieldMap.TASK_NUMBER + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_NUMBER) + "] "
                + FieldMap.TASK_NAME + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_NAME) + "] "
                + FieldMap.TASK_SHORTNAME + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_SHORTNAME) + "] "
                + FieldMap.TASK_DESCRIPTION + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_DESCRIPTION) + "] "
                + FieldMap.TASK_BUDGET + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_BUDGET) + "] "
                + FieldMap.TASK_ABUDGET + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_ABUDGET) + "] "
                + FieldMap.TASK_DEADLINE + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_DEADLINE) + "] "
                + FieldMap.TASK_PRIORITY + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_PRIORITY) + "] "
                + FieldMap.TASK_PARENT + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_PARENT) + "] "
                + FieldMap.HUSER_NAME + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.HUSER_NAME) + "] "
                + FieldMap.HUSER_STATUS + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.HUSER_STATUS) + "] "
                + FieldMap.TASK_CATEGORY + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_CATEGORY) + "] "
                + FieldMap.SUSER_NAME + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.SUSER_NAME) + "] "
                + FieldMap.TASK_SUBMITDATE + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_SUBMITDATE) + "] "
                + FieldMap.TASK_UPDATEDATE + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_UPDATEDATE) + "] "
                + FieldMap.TASK_CLOSEDATE + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_CLOSEDATE) + "] "
                + FieldMap.TASK_RESOLUTION + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_RESOLUTION) + "] "
                + FieldMap.TASK_STATUS + " [" + FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TASK_STATUS) + "] "
        );
    }
}
