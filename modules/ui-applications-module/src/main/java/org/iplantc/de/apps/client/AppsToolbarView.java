package org.iplantc.de.apps.client;

import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author jstroot
 */
public interface AppsToolbarView extends IsWidget,
                                         AppSelectionChangedEvent.AppSelectionChangedEventHandler,
                                         AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler,
                                         AppSearchResultLoadEvent.HasAppSearchResultLoadEventHandlers {

    interface AppsToolbarAppearance {

        String appDeleteWarning();

        String appSearchLoadingMask();

        String submitForPublicUse();

        String applications();

        String run();

        ImageResource runIcon();

        String newApp();

        ImageResource addIcon();

        String requestTool();

        String copy();

        ImageResource copyIcon();

        String editMenuItem();

        ImageResource editIcon();

        String delete();

        ImageResource deleteIcon();

        String shareMenuItem();

        ImageResource submitForPublicIcon();

        String warning();

        String workflow();

        String useWf();

        String searchApps();
    }

    void hideAppMenu();

    void hideWorkflowMenu();

    void init(AppsView.Presenter presenter, AppsView view);
}
