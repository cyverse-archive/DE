package org.iplantc.de.client.models.apps;

import java.util.Comparator;

public class AppNameComparator implements Comparator<App> {
    @Override
    public int compare(App arg0, App arg1) {
        return arg0.getName().compareTo(arg1.getName());
    }
}
