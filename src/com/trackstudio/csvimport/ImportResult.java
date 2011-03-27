package com.trackstudio.csvimport;


public class ImportResult{
      public static final int TASK=1;
      public static final int USER=2;
      public static final int MESSAGE=3;
      private int stringNumber;
      private String comment;
      private boolean OK;
      private int type;
      String[] line;

      public int getType() {
          return type;
      }

      public void setType(int type) {
          this.type = type;
      }

      public int getStringNumber() {
          return stringNumber;
      }

      public void setStringNumber(int stringNumber) {
          this.stringNumber = stringNumber;
      }

      public String getComment() {
          return comment;
      }

      public void setComment(String comment) {
          this.comment = comment;
      }

      public boolean isOK() {
          return OK;
      }

      public void setOK(boolean OK) {
          this.OK = OK;
      }

      public ImportResult(int stringNumber, String[] line, String comment, int type, boolean OK) {
          this.stringNumber = stringNumber;
          this.comment = comment;
          this.OK = OK;
          this.type = type;
          this.line = line;
      }

    public String[] getLine() {
        return line;
    }

    public void setLine(String[] line) {
        this.line = line;
    }
}

