package org.iplantc.de.commons.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;

/**
 * This is an extension of the Sencha TabPanel with the ability to add static/debug IDs
 * to the tabs within the TabPanel.
 * @author aramsey
 */
public class DETabPanel extends TabPanel {

    public DETabPanel() {
        this(GWT.<TabPanelAppearance> create(TabPanelAppearance.class));
    }

    public DETabPanel(TabPanelAppearance appearance) {
        super(appearance);
    }

    public void add(Widget widget, TabItemConfig config, String debugId){
        super.add(widget,config);
        setTabDebugId(widget, debugId);
    }

    public void insert(Widget widget, int index, TabItemConfig config, String debugId) {
        super.insert(widget, index, config);
        setTabDebugId(widget, debugId);
    }

    public void setTabDebugId(Widget widget, String debugId) {
        XElement item = findItem(getWidgetIndex(widget));
        item.setId(debugId);
    }
}
