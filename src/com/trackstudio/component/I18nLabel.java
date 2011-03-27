package com.trackstudio.component;

import javax.swing.*;

public class I18nLabel extends JLabel {
        private String oldText=null;
        public I18nLabel(String text) {
            super(text);
        }

        public I18nLabel(String text, Icon icon, int horizontalAlignment) {
            super(text, icon, horizontalAlignment);
        }

        public String getText() {
            String text= I18n.getString(super.getText());
            if (text.equals(oldText)) return text;
            else{
                oldText = text;
                revalidate();
                return text;
            }
        }
    }
