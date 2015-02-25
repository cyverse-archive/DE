package org.iplantc.de.apps.client;

import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author jstroot
 */
public interface AppsToolbarView extends IsWidget,
                                         AppSelectionChangedEvent.AppSelectionChangedEventHandler,
                                         AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler,
                                         AppSearchResultLoadEvent.HasAppSearchResultLoadEventHandlers {

    void hideAppMenu();

    void hideWorkflowMenu();

    void init(AppsView.Presenter presenter, AppsView view);
}
