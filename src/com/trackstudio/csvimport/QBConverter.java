package com.trackstudio.csvimport;

import au.com.bytecode.opencsv.CSVReader;

import java.io.*;
import java.util.Properties;
import java.util.ArrayList;


public class QBConverter extends CSVReader {
    private Properties properties;
    private Integer[] placeholders; // позиции в исходной строке. -1 - без позиции
    private String[] substitutions; //подстановки значений. Либо значение, либо null
    protected String[] headers;
    protected int bytesReaded;
    public QBConverter(Reader reader, char c, String mappingFile) {
        super(reader, c);
        properties = new Properties();
        if (mappingFile!=null){
            try {
                FileInputStream fis = new FileInputStream(mappingFile);
                InputStreamReader in = new InputStreamReader(fis, "UTF-8");
                properties.load(in);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.headers = new String[]{};
        this.substitutions = new String[]{};
        this.placeholders = new Integer[]{};
        String[] nextline = new String[]{};
        try{
            nextline =  super.readNext();
            // это те заголовки, что есть в файле. Сейчас будем маппить.

        } catch (IOException i){
            i.printStackTrace();
        }
        ArrayList<String> st  = new ArrayList<String>();
        ArrayList<String> subs  = new ArrayList<String>();
        ArrayList<Integer> phds  = new ArrayList<Integer>();
        if (!properties.isEmpty())
            for (String p: properties.stringPropertyNames()){
                Object o = properties.get(p);
                if (o !=null && properties.get(p).toString().length()>0){
                    st.add(p);

                    String s = o.toString();
                    if (s.startsWith("\"") && s.endsWith("\"")) {
                        subs.add(s.substring(1, s.length()-1));
                    } else {
                        subs.add(s);
                    }
                    phds.add(-1);
                    for (int k=0; k<nextline.length; k++){
                        if (s.equals(nextline[k])){
                            phds.set(phds.size()-1,k);
                        }

                    }

                }
            } else
        {
            this.headers = nextline;
        }
        this.headers = st.toArray(this.headers);
        this.substitutions = subs.toArray(this.substitutions);
        this.placeholders = phds.toArray(this.placeholders);


    }

    public String[] getHeaders(){
        return this.headers;
    }

    public int getBytesReaded() {
        return bytesReaded;
    }

    @Override
    public String[] readNext() throws IOException {
        String[] nextline =  super.readNext();
        if (nextline!=null){
            int c =0;
            for (String line: nextline){
                c += line.getBytes().length;
            }


            if (nextline.length==1 && nextline[0].length()==0) return nextline;
            else{
                String[] newline = new String[headers.length];
                for (int k=0; k<headers.length; k++){
                    if (this.placeholders.length>0){
                        if (this.placeholders[k]>-1){
                            newline[k]=nextline[this.placeholders[k]];

                        }
                        else{
                            newline[k]=this.substitutions[k];

                        }
                    }
                    else
                        newline = nextline;
                }

                bytesReaded = c;

                return newline;
            }
        }
        else return null;
    }
}
