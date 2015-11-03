package org.iplantc.de.commons.client.views.window.configs;

public interface TabularFileViewerWindowConfig extends FileViewerWindowConfig {

    int getColumns();

    void setColumns(int noOfColumns);

    void setSeparator(String separator);

    String getSeparator();

}
