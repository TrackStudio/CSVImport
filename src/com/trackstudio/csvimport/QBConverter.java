package com.trackstudio.csvimport;

import au.com.bytecode.opencsv.CSVReader;
import com.trackstudio.data.DataBean;

import java.io.*;
import java.util.*;

public class QBConverter {
    private DataBean dataBean;
    private String[] header;
    private List<String[]> lines;
    private Map<String, String> keyHeader;

    public QBConverter(DataBean dataBean) {
        this.dataBean = dataBean;
    }

    public void updateData() throws IOException {
        readKeyHeader();
        Reader readerFile = new InputStreamReader(new FileInputStream(this.dataBean.getFilePath()), this.dataBean.getEncoding());
        CSVReader reader = new CSVReader(readerFile, this.dataBean.getDelimiter().charAt(0));
        this.header = reader.readNext();
        String [] nextLine;
        this.lines = new ArrayList<String[]>();
        while ((nextLine = reader.readNext()) != null) {
            this.lines.add(nextLine);
        }
    }

    private void readKeyHeader() throws IOException {
        keyHeader = new LinkedHashMap<String, String>();
        Properties prop = new Properties();
        prop.load(new FileInputStream(dataBean.getMappingFile()));
        for (Enumeration enums = prop.propertyNames();enums.hasMoreElements();){
            String name = enums.nextElement().toString();
            keyHeader.put(prop.getProperty(name), name);
        }
    }

    public String[] getHeaders() {
        String[] names = new String[header.length];
        int inx = 0;
        for (String name : header) {
            names[inx++]=keyHeader.get(name);
        }
        return names;
    }

    public List<String[]> getLines() {
        return lines;
    }
}
