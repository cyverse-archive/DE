/**
 * 
 */
package org.iplantc.de.commons.client.widgets;

import com.google.gwt.uibinder.client.UiConstructor;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;

/**
 * @author sriram
 * 
 */
public class DEPagingToolbar extends PagingToolBar {

    @UiConstructor
    public DEPagingToolbar(int pageSize) {
        super(pageSize);
    }

    public TextButton getRefreshButton() {
        return refresh;
    }

}
