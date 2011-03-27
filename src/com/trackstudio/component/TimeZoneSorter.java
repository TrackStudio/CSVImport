package com.trackstudio.component;

import java.util.*;

public class TimeZoneSorter implements Comparator<TimeZone> {
    private Locale current;

    public TimeZoneSorter(Locale current) {
        this.current = current;
    }

    public int compare(TimeZone o1, TimeZone o2) {
        return o1.getDisplayName(current).compareTo(o2.getDisplayName(current));
    }
}

