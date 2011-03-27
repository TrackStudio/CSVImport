package com.trackstudio.component;

import java.util.Comparator;
import java.util.Locale;

public class LocaleSorter implements Comparator<Locale> {
    private Locale current;

    public LocaleSorter(Locale current) {
        this.current = current;
    }

    public int compare(Locale o1, Locale o2) {
        return o1.getDisplayName(current).compareTo(o2.getDisplayName(current));
    }
}
