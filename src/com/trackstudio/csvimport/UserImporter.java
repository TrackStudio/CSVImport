package com.trackstudio.csvimport;

import com.trackstudio.component.DateFormatter;
import com.trackstudio.component.I18n;
import com.trackstudio.data.DataBean;
import com.trackstudio.soap.service.user.User;
import com.trackstudio.component.FieldMap;

import java.text.ParseException;
import java.util.List;

public class UserImporter {
    User user;
    DateFormatter dateformat;
    String[] headers;
    String parentField;
    String sessionId;
    private DataBean dataBean;

    public UserImporter(DataBean dataBean, User user, DateFormatter f, String[] headers, String sessionId, String parentField) {
        this.dataBean = dataBean;
        this.user = user;
        this.dateformat = f;
        this.headers = headers;
        this.parentField = parentField;
        this.sessionId = sessionId;
    }

    public ImportResult process(int current, String[] nextline) throws Exception {
        long userExpireDate;
        boolean userIsActive;
        boolean userShowHelp;
        String userId;
        StringBuffer error = new StringBuffer();
        userExpireDate = -1;
        userIsActive = true;
        userShowHelp = true;

        if (FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_EXPIREDATE).length() != 0) {
            try {
                userExpireDate = dateformat.parseToCalendar(FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_EXPIREDATE)).getTimeInMillis();
            } catch (ParseException e) {
                this.dataBean.setLog("ERROR: ", FieldMap.USER_EXPIREDATE + " can not be parsed. See CSV file, line number " + String.valueOf(current) + " . " + I18n.getString("MSG_FIELD_DATE_PATTERN") + dateformat.getPattern());
                error.append("ERROR: ").append(I18n.getString("MSG_FIELD")).append(" \"").append(FieldMap.USER_EXPIREDATE).append("\" ").append(I18n.getString("MSG_FIELD_CANNOT_BE_PARSED")).append(" ").append(String.valueOf(current)).append(" . ").append(I18n.getString("MSG_FIELD_DATE_PATTERN")).append(dateformat.getPattern());
                return new ImportResult(current, nextline, error.toString(), ImportResult.USER, false);
            }
        }

        if (FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_ACTIVE).length() != 0) {
            try {

                userIsActive = Boolean.parseBoolean(FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_ACTIVE));
            } catch (NumberFormatException e) {
                this.dataBean.setLog("ERROR: ", FieldMap.USER_ACTIVE + " can not be parsed. See CSV file, line number " + String.valueOf(current));
                error.append("ERROR: ").append(I18n.getString("MSG_FIELD")).append(" \"").append(FieldMap.USER_ACTIVE).append("\" ").append(I18n.getString("MSG_FIELD_CANNOT_BE_PARSED")).append(" ").append(String.valueOf(current));
                return new ImportResult(current,nextline, error.toString(), ImportResult.USER, false);

            }
        }
        List<String> udfNames = FieldChecker.getUDFNames(headers);
        String localParent = FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_PARENT);
        if (localParent==null || localParent.length()==0) localParent = parentField;
        if (user.findUserIdByQuickGo(sessionId, localParent)==null) throw new Exception("User with login "+parentField+" not found");
        String oldUserId = FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_LOGIN);
        userId = user.importUser(
                sessionId,
                FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_LOGIN),
                FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_NAME),
                FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_TEL),
                FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_EMAIL),
                FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_STATUS),
                FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_TIMEZONE),
                FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_TEMPLATE),
                FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_LOCALE),
                FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_COMPANY),
                "" /* email type */,
                FieldChecker.getFieldValue(nextline, headers, FieldMap.DEFAULT_PROJECT),
                userExpireDate,
                "",
                userIsActive,
                userShowHelp,
                "" /* html editor*/,
                ""/* tree mode*/,
                localParent,
                FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_PASSWORD),
                udfNames,
                FieldChecker.getUDFValues(headers, nextline)
        );


        List<Integer> errorCode = user.getUserErrors();
        String str = "";
        for (Integer j: errorCode) {
            str = FieldChecker.getError( j) + "\n";
        }

        error.append(str);

        logUserLine(error.toString(), current, nextline);
        if (userId != null && userId.length() != 0) if (userId.compareToIgnoreCase(oldUserId) == 0) {
            return new ImportResult(current,nextline, "_"+userId, ImportResult.USER, true);

        } else {
            return new ImportResult(current,nextline, userId, ImportResult.USER, true);

        }
        else{
            return new ImportResult(current,nextline, error.toString(), ImportResult.USER, false);

        }

    }
    private void logUserLine(String result, int current, String[] nextline) {
        if (result.length() == 0) {
            result = "OK";
        }
        this.dataBean.setLog("Result: "+result+". User: send line " + Integer.toString(current) + " with values: "
                + FieldMap.USER_LOGIN + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_LOGIN) + "] "
                + FieldMap.USER_NAME + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_NAME) + "] "
                + FieldMap.USER_TEL + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_TEL) + "] "
                + FieldMap.USER_EMAIL + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_EMAIL) + "] "
                + FieldMap.USER_STATUS + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_STATUS) + "] "
                + FieldMap.USER_TIMEZONE + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_TIMEZONE) + "] "
                + FieldMap.USER_LOCALE + " [" + FieldChecker.getFieldValue(nextline, headers,FieldMap.USER_LOCALE) + "] "
                + FieldMap.USER_COMPANY + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_COMPANY) + "] "
                + FieldMap.USER_EXPIREDATE + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_EXPIREDATE) + "] "
                + FieldMap.DEFAULT_PROJECT + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.DEFAULT_PROJECT) + "] "
                + FieldMap.USER_ACTIVE + " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_ACTIVE) + "] "
                + FieldMap.USER_PARENT+ " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_PARENT) + "] "
                + FieldMap.USER_TEMPLATE+ " [" + FieldChecker.getFieldValue(nextline, headers, FieldMap.USER_TEMPLATE) + "] "
        );
    }

}
