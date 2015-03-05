package org.iplantc.de.apps.client;

import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;

/**
 * Created by jstroot on 3/5/15.
 * @author jstroot
 */
public interface AppsGridView {
    interface AppsGridAppearance {

    }

    interface Presenter extends AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler,
                                AppSearchResultLoadEvent.AppSearchResultLoadEventHandler {

    }
}
