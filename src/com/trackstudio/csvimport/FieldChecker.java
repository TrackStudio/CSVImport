package com.trackstudio.csvimport;

import com.trackstudio.component.FieldMap;
import com.trackstudio.component.I18n;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class FieldChecker {
    private static final int TASK_CATEGORY_DOES_NOT_EXIST_ERROR_CODE = 101;//ERROR
    private static final int TASK_PRIORITY_DOES_NOT_EXIST_ERROR_CODE = 102; //WARNING
    private static final int TASK_PARENT_DOES_NOT_EXIST_ERROR_CODE = 103;//WARNING
    private static final int TASK_RESOLUTION_DOES_NOT_EXIST_ERROR_CODE = 104; //WARNING
    private static final int TASK_HANDLER_USER_DOES_NOT_EXIST_ERROR_CODE = 105;//WARNING
    private static final int TASK_SUBMITTER_USER_DOES_NOT_EXIST_ERROR_CODE = 106;//WARNING
    private static final int USER_PARENT_USER_DOES_NOT_EXIST_ERROR_CODE = 201;//ERROR
    private static final int USER_DEFAULT_PROJECT_DOES_NOT_EXIST_ERROR_CODE = 202; //WARNING
    private static final int USER_DEFAULT_PRSTATUS_DOES_NOT_EXIST_ERROR_CODE = 203;//WARNING
    private static final int MESSAGE_HANDLER_USER_DOES_NOT_EXIST_ERROR_CODE = 301;//ERROR
    private static final int MESSAGE_SUBMITER_USER_DOES_NOT_EXIST_ERROR_CODE = 302; //WARNING
    private static final int MESSAGE_TASK_DOES_NOT_EXIST_ERROR_CODE = 303;//WARNING
    private static final int MESSAGE_PRIORITY_DOES_NOT_EXIST_ERROR_CODE = 304;
    private static final int MESSAGE_MESSAGE_TYPE_DOES_NOT_EXIST_ERROR_CODE = 305;
    private static final int MESSAGE_MESSAGE_RESOLUTION_DOES_NOT_EXIST_ERROR_CODE = 306;
    public static final int ERR_TASK_INVALID_HEADER_MESSAGE=1;
     private static final int ERROR_PRSTATUS_NAME_IS_NOT_UNIQUE = 204; //WARNING

    public static int findFieldNumberByName(String[] fieldNames, FieldMap fieldName) {
        for (int i = 0; i < fieldNames.length; i++)
            if (fieldNames[i].equals(fieldName.getAltKey())) return i;
        return -1;
    }

    public static String getFieldValue(String[] values, String[] fieldNames,  FieldMap fieldName) {
        int column = findFieldNumberByName(fieldNames, fieldName);
        if (column != -1) return values[column];
        else return "";
    }

    //�������� ������������ ����
    public static boolean checkValidTaskFieldNames(String[] values) {
        return (findFieldNumberByName(values, FieldMap.TASK_NUMBER) != -1) ||
                ((findFieldNumberByName(values, FieldMap.TASK_NAME) != -1) && (findFieldNumberByName(values, FieldMap.TASK_CATEGORY) != -1));
    }

    public static boolean checkValidUserFieldNames(String[] values) {
        return (findFieldNumberByName(values, FieldMap.USER_LOGIN) != -1) ||
                ((findFieldNumberByName(values, FieldMap.USER_NAME) != -1) && (findFieldNumberByName(values, FieldMap.USER_STATUS) != -1));
    }

    public static boolean checkValidMessageFieldNames(String[] values) {
        return (findFieldNumberByName(values, FieldMap.MESSAGE_TASK) != -1) &&
                (findFieldNumberByName(values, FieldMap.MSG_MSTATUS) != -1);
    }

    public static  boolean checkTaskFieldNames(String[] values){
        List<String> list = Arrays.asList(values);
        for (FieldMap f: FieldMap.taskFields)
        if (list.contains(f.getAltKey())) return true;
        return false;

    }

    public static boolean checkMessagesFieldNames(String[] values) {
        List<String> list = Arrays.asList(values);
        for (FieldMap f: FieldMap.messageFields)
        if (list.contains(f.getAltKey())) return true;
        return false;
    }

    public static boolean checkUserFieldNames(String[] values)  {
        List<String> list = Arrays.asList(values);
        for (FieldMap f: FieldMap.userFields )
        if (list.contains(f.getAltKey())) return true;
        return false;
    }

    public static  boolean isCorrectHeader(String[] values) {
        return checkTaskFieldNames(values) || checkUserFieldNames(values) || checkMessagesFieldNames(values);
    }

    public static boolean isNeedUserField(String[] values) {

        if (checkUserFieldNames(values)) {
            if (findFieldNumberByName(values, FieldMap.USER_PARENT) == -1)
                return true;
        }
        return false;
    }

    public static boolean isNeedTaskField(String[] values) {

        return findFieldNumberByName(values, FieldMap.TASK_PARENT) == -1;
    }

    public static List<String> getUDFValues(String[] fieldNames, String[] fieldValues) {
        ArrayList<String> udfValues = new ArrayList<String>();
        for (int i = 0; i < fieldNames.length; i++) {
            boolean isConstant = false;
            for (FieldMap taskConst : FieldMap.taskFields) {
                if (fieldNames[i].toLowerCase().equals(taskConst.getAltKey().toLowerCase())) isConstant = true;
            }
            for (FieldMap userConst : FieldMap.userFields) {
                if (fieldNames[i].toLowerCase().equals(userConst.getAltKey().toLowerCase())) isConstant = true;
            }
            for (FieldMap operationConst : FieldMap.messageFields) {
                if (fieldNames[i].toLowerCase().equals(operationConst.getAltKey().toLowerCase())) isConstant = true;
            }
            if (!isConstant) {
                udfValues.add(fieldValues[i]);
            }
        }
        return udfValues;
    }

    public static String printUDFFields(String[] fieldNames, String[] fieldValues) {
        String result = "";
        for (int i = 0; i < fieldNames.length; i++) {
            boolean isConstant = false;
            for (FieldMap taskConst : FieldMap.taskFields) {
                if (fieldNames[i].toLowerCase().equals(taskConst.getAltKey().toLowerCase())) isConstant = true;
            }
            for (FieldMap userConst : FieldMap.userFields) {
                if (fieldNames[i].toLowerCase().equals(userConst.getAltKey().toLowerCase())) isConstant = true;
            }
            for (FieldMap operationConst : FieldMap.messageFields) {
                if (fieldNames[i].toLowerCase().equals(operationConst.getAltKey().toLowerCase())) isConstant = true;
            }
            if (!isConstant) {
                result += fieldNames[i] + " [" + fieldValues[i] + "] ";
            }
        }
        return result;
    }

    public static List<String> getUDFNames(String[] fieldNames) {
        ArrayList<String> udfNames = new ArrayList<String>();
        int i = 0;
        while (i < fieldNames.length) {
            boolean isConstant = false;
            for (FieldMap taskConst : FieldMap.taskFields) {
                if (fieldNames[i].toLowerCase().equals(taskConst.getAltKey().toLowerCase())) isConstant = true;
            }
            for (FieldMap userConst : FieldMap.userFields) {
                if (fieldNames[i].toLowerCase().equals(userConst.getAltKey().toLowerCase())) isConstant = true;
            }
            for (FieldMap operationConst : FieldMap.messageFields) {
                if (fieldNames[i].toLowerCase().equals(operationConst.getAltKey().toLowerCase())) isConstant = true;
            }
            if (!isConstant) {
                udfNames.add(fieldNames[i]);
            }
            i++;
        }
        return udfNames;
    }

    public static String getError(int code) {
        switch (code) {

            case TASK_CATEGORY_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("TASK_CATEGORY_DOES_NOT_EXIST_ERROR");// "Task Category Does Not Exist";
            case TASK_PRIORITY_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("TASK_PRIORITY_DOES_NOT_EXIST_ERROR");//"Task Priority Does Not Exist";
            case TASK_PARENT_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("TASK_PARENT_DOES_NOT_EXIST_ERROR");//"Task Parent Does Not Exist";
            case TASK_RESOLUTION_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("TASK_RESOLUTION_DOES_NOT_EXIST_ERROR");//"Task Resolution Does Not Exist";
            case TASK_HANDLER_USER_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("TASK_HANDLER_USER_DOES_NOT_EXIST_ERROR");//"Task Handler User Does Not Exist";
            case TASK_SUBMITTER_USER_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("TASK_SUBMITTER_USER_DOES_NOT_EXIST_ERROR");//"Task Submitter User Does Not Exist";

            case USER_PARENT_USER_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("USER_PARENT_USER_DOES_NOT_EXIST_ERROR");//"User Parent User Does Not Exist";
            case USER_DEFAULT_PROJECT_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("USER_DEFAULT_PROJECT_DOES_NOT_EXIST_ERROR");//"User Default Project Does Not Exist";
            case USER_DEFAULT_PRSTATUS_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("USER_DEFAULT_PRSTATUS_DOES_NOT_EXIST_ERROR");//"User Group Does Not Exist";

            case MESSAGE_HANDLER_USER_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("MESSAGE_HANDLER_USER_DOES_NOT_EXIST_ERROR");//"Operation Handler User Does Not Exist";
            case MESSAGE_SUBMITER_USER_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("MESSAGE_SUBMITER_USER_DOES_NOT_EXIST_ERROR");//"Operation Submitter User Does Not Exist";
            case MESSAGE_TASK_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("FieldMap.MSG_MESSAGE_TASK_DOES_NOT_EXIST_ERROR");//"Operation Task Does Not Exist";
            case MESSAGE_PRIORITY_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("MESSAGE_PRIORITY_DOES_NOT_EXIST_ERROR");//"Operation Priority Does Not Exist";
            case MESSAGE_MESSAGE_TYPE_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("MESSAGE_MESSAGE_TYPE_DOES_NOT_EXIST_ERROR");//"Operation Type Does Not Exist";
            case MESSAGE_MESSAGE_RESOLUTION_DOES_NOT_EXIST_ERROR_CODE:
                return I18n.getString("MESSAGE_MESSAGE_RESOLUTION_DOES_NOT_EXIST_ERROR");//"Operation Resolution Does Not Exist";
            case ERROR_PRSTATUS_NAME_IS_NOT_UNIQUE:
                return I18n.getString("ERROR_PRSTATUS_NAME_IS_NOT_UNIQUE");
        }
        return "";
    }

    public static long parseBudget(String strBudget) throws NumberFormatException {
        return Long.parseLong(strBudget.trim());
    }
}
