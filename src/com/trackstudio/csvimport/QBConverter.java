package com.trackstudio.csvimport;

import au.com.bytecode.opencsv.CSVReader;
import com.trackstudio.data.DataBean;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class QBConverter {
    private DataBean dataBean;
    private String[] header;
    private List<String[]> lines = new ArrayList<String[]>();

    public QBConverter(DataBean dataBean) {
        this.dataBean = dataBean;
    }

    public void updateData() throws IOException {
        Reader readerFile = new InputStreamReader(new FileInputStream(this.dataBean.getFilePath()), this.dataBean.getEncoding());
        CSVReader reader = new CSVReader(readerFile, this.dataBean.getDelimiter().charAt(0));
        this.header = reader.readNext();
        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            this.lines.add(nextLine);
        }
    }

    public String[] getHeaders() {
        return header;
    }

    public List<String[]> getLines() {
        return lines;
    }
}
