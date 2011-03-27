package com.trackstudio.component;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
* User: winzard
* Date: 09.06.2009
* Time: 15:20:38
* To change this template use File | Settings | File Templates.
*/
public class I18nCheckBox extends JCheckBox {
    private String oldText=null;
    public String getText() {
        String text= I18n.getString(super.getText());
            if (text.equals(oldText)) return text;
            else{
                oldText = text;
            revalidate();
            return text;
            }

    }

    public I18nCheckBox() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public I18nCheckBox(Icon icon) {
        super(icon);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public I18nCheckBox(Icon icon, boolean selected) {
        super(icon, selected);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public I18nCheckBox(String text) {
        super(text);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public I18nCheckBox(Action a) {
        super(a);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public I18nCheckBox(String text, boolean selected) {
        super(text, selected);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public I18nCheckBox(String text, Icon icon) {
        super(text, icon);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public I18nCheckBox(String text, Icon icon, boolean selected) {
        super(text, icon, selected);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
