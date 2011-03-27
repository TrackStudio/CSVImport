package com.trackstudio.component;

import javax.swing.*;


public class I18nButton extends JButton {
        private String oldText=null;
        public I18nButton(String text) {
            super(text);
        }

        public I18nButton(String text, Icon icon) {
            super(text, icon);
        }

        public I18nButton() {
        }

        public I18nButton(Icon icon) {
            super(icon);
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
