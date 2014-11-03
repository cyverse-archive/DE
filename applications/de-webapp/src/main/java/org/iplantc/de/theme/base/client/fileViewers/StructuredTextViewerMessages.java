package org.iplantc.de.theme.base.client.fileViewers;

import com.google.gwt.i18n.client.Messages;

public interface StructuredTextViewerMessages extends Messages {
    @Key("sampleColumnText")
    String sampleColumnText(int i);

    @Key("defaultViewName")
    String defaultViewName();

    @Key("viewName")
    String viewName(String fileName);

    @Key("gridToolTip")
    String gridToolTip();
}
