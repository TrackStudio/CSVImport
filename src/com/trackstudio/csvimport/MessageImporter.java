package com.trackstudio.csvimport;

import com.trackstudio.component.DateFormatter;
import com.trackstudio.component.I18n;
import com.trackstudio.data.DataBean;
import com.trackstudio.soap.service.message.Message;
import com.trackstudio.component.FieldMap;

import java.text.ParseException;
import java.util.List;
import java.util.HashMap;

public class MessageImporter {
    Message message;
    DateFormatter dateformat;
    String[] headers;
    String parentField;
    String sessionId;
    boolean createNew;
    private DataBean dataBean;

    public MessageImporter(DataBean dataBean, Message message, DateFormatter f, String[] headers, String sessionId, boolean createNew, String parentField) {
        this.dataBean = dataBean;
        this.message = message;
        this.dateformat = f;
        this.headers = headers;
        this.parentField = parentField;
        this.sessionId = sessionId;
        this.createNew = createNew;
    }

    public ImportResult process(int current, String[] nextline, HashMap<String, String> taskMap) throws Exception {
        long operationActualBudget;
        long operationDeadline;
        long operationSubmitDate;
        long operationBudget;
        StringBuffer error = new StringBuffer();
        operationActualBudget = -1;
        operationDeadline = -1;
        operationSubmitDate = -1;
        operationBudget = -1;

        String s = logMessageLine("", current, nextline);

        String[] headersLocal = headers;
        if (FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.MSG_ABUDGET).length() != 0) {
            try {
                operationActualBudget = FieldChecker.parseBudget(FieldChecker.getFieldValue(nextline,headersLocal, FieldMap.MSG_ABUDGET));
            } catch (Exception e) {
                this.dataBean.setLog("ERROR: ", FieldMap.MSG_ABUDGET + " can not be parsed. See CSV file, line number " + String.valueOf(current));
                error.append("ERROR: ").append(I18n.getString("MSG_FIELD")).append(" \"").append(FieldMap.MSG_ABUDGET).append("\" ").append(I18n.getString("MSG_FIELD_CANNOT_BE_PARSED")).append(" ").append(String.valueOf(current));
                return new ImportResult(current,nextline, error.toString(), ImportResult.MESSAGE, false);

            }
        }

        if (FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.MSG_DEADLINE).length() != 0) {
            try {
                operationDeadline = dateformat.parseToCalendar(FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.MSG_DEADLINE)).getTimeInMillis();
            } catch (ParseException e) {
                this.dataBean.setLog("ERROR: ", FieldMap.MSG_DEADLINE + " can not be parsed. See CSV file, line number " + String.valueOf(current) + " . " + I18n.getString("MSG_FIELD_DATE_PATTERN") + dateformat.getPattern());

                error.append("ERROR: ").append(I18n.getString("MSG_FIELD")).append(" \"").append(FieldMap.MSG_DEADLINE).append("\" ").append(I18n.getString("MSG_FIELD_CANNOT_BE_PARSED")).append(" ").append(String.valueOf(current)).append(" . ").append(I18n.getString("MSG_FIELD_DATE_PATTERN")).append(dateformat.getPattern());
                return new ImportResult(current,nextline, error.toString(), ImportResult.MESSAGE, false);

            }
        }

        if (FieldChecker.getFieldValue(nextline,headersLocal, FieldMap.MSG_SUBMITDATE).length() != 0) {
            try {

                operationSubmitDate = dateformat.parseToCalendar(FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.MSG_SUBMITDATE)).getTimeInMillis();
            } catch (ParseException e) {
                this.dataBean.setLog("ERROR: ", FieldMap.MSG_SUBMITDATE + " can not be parsed. See CSV file, line number " + String.valueOf(current) + " . " + I18n.getString("MSG_FIELD_DATE_PATTERN") + dateformat.getPattern());

                error.append("ERROR: ").append(I18n.getString("MSG_FIELD")).append(" \"").append(FieldMap.MSG_SUBMITDATE).append("\" ").append(I18n.getString("MSG_FIELD_CANNOT_BE_PARSED")).append(" ").append(String.valueOf(current)).append(" . ").append(I18n.getString("MSG_FIELD_DATE_PATTERN")).append(dateformat.getPattern());

                return new ImportResult(current, nextline, error.toString(), ImportResult.MESSAGE, false);

            }
        }

        if (FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.MSG_BUDGET).length() != 0) {
            try {

                operationBudget = FieldChecker.parseBudget(FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.MSG_BUDGET));
            } catch (Exception e) {
                this.dataBean.setLog("ERROR: ", FieldMap.MSG_BUDGET + " can not be parsed. See CSV file, line number " + String.valueOf(current));

                error.append("ERROR: ").append(I18n.getString("MSG_FIELD")).append(" \"").append(FieldMap.MSG_BUDGET).append("\" ").append(I18n.getString("MSG_FIELD_CANNOT_BE_PARSED")).append(" ").append(String.valueOf(current));

                return new ImportResult(current, nextline, error.toString(), ImportResult.MESSAGE, false);

            }
        }


        List<String> udfNames = FieldChecker.getUDFNames(headersLocal);


        if (!taskMap.isEmpty()) {
            String newNumber = taskMap.get(s);
            if (newNumber!=null) s = newNumber;
        }

        String m = message.importMessage(sessionId,
                s,
                FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.MSG_MSTATUS),
                FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.TEXT_MSG),
                operationActualBudget,
                FieldChecker.getFieldValue(nextline, headersLocal, FieldMap.MSG_HUSER_NAME),
                FieldChecker.getFieldValue(nextline, headersLocal,  FieldMap.MSG_MSTATUS),
                FieldChecker.getFieldValue(nextline, headersLocal,  FieldMap.MSG_RESOLUTION),
                FieldChecker.getFieldValue(nextline, headersLocal,  FieldMap.MSG_PRIORITY),
                operationDeadline,
                operationSubmitDate,
                FieldChecker.getFieldValue(nextline, headersLocal,  FieldMap.MSG_SUSER_NAME),
                operationBudget,
                udfNames,
                FieldChecker.getUDFValues(headersLocal, nextline)
        );


        List<Integer> errorCode = message.getMessageErrors();


        String str = "";
        for (Integer j: errorCode) {
            str = FieldChecker.getError(j) + "\n";
        }

        error.append(str);
        logMessageLine(error.toString(), current, nextline);
        if (m!=null && m.length()>0){
            return new ImportResult(current, nextline, m, ImportResult.MESSAGE, true);


        }else {
            return new ImportResult(current, nextline, error.toString(), ImportResult.MESSAGE, false);

        }
    }
    private String logMessageLine(String result, int current, String[] nextline) {
        String s = FieldChecker.getFieldValue(nextline, headers, FieldMap.MESSAGE_TASK);
        this.dataBean.setLog("Result: "+result+". Operation: send line " + Integer.toString(current) + " with values: "
                + FieldMap.MESSAGE_TASK + " [" + s + "] "
                + FieldMap.MSG_MSTATUS + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.MSG_MSTATUS) + "] "
                + FieldMap.TEXT_MSG + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.TEXT_MSG) + "] "
                + FieldMap.MSG_ABUDGET + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.MSG_ABUDGET) + "] "
                + FieldMap.MSG_HUSER_NAME + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.MSG_HUSER_NAME) + "] "
                + FieldMap.MSG_RESOLUTION + " [" + FieldChecker.getFieldValue(nextline, headers,  FieldMap.MSG_RESOLUTION) + "] "
                + FieldMap.MSG_PRIORITY + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.MSG_PRIORITY) + "] "
                + FieldMap.MSG_DEADLINE + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.MSG_DEADLINE) + "] "
                + FieldMap.MSG_SUBMITDATE + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.MSG_SUBMITDATE) + "] "
                + FieldMap.MSG_SUSER_NAME + " [" + FieldChecker.getFieldValue(nextline, headers,  FieldMap.MSG_SUSER_NAME) + "] "
                + FieldMap.MSG_BUDGET + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.MSG_BUDGET) + "] "
        );
        return s;
    }
}
