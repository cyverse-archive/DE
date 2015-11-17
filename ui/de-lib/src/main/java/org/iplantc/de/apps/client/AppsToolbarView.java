package org.iplantc.de.apps.client;

import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.BeforeAppSearchEvent;
import org.iplantc.de.apps.client.events.selection.*;

import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;

/**
 * @author jstroot
 */
public interface AppsToolbarView extends IsWidget,
                                         HasHandlers,
                                         AppSelectionChangedEvent.AppSelectionChangedEventHandler,
                                         AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler,
                                         BeforeAppSearchEvent.HasBeforeAppSearchEventHandlers,
                                         AppSearchResultLoadEvent.HasAppSearchResultLoadEventHandlers,
                                         RunAppSelected.HasRunAppSelectedHandlers,
                                         CopyAppSelected.HasCopyAppSelectedHandlers,
                                         CopyWorkflowSelected.HasCopyWorkflowSelectedHandlers,
                                         CreateNewAppSelected.HasCreateNewAppSelectedHandlers,
                                         CreateNewWorkflowSelected.HasCreateNewWorkflowSelectedHandlers,
                                         DeleteAppsSelected.HasDeleteAppsSelectedHandlers,
                                         EditAppSelected.HasEditAppSelectedHandlers,
                                         EditWorkflowSelected.HasEditWorkflowSelectedHandlers,
                                RequestToolSelected.HasRequestToolSelectedHandlers,
                                ShareAppsSelected.HasShareAppSelectedHandlers {

    interface AppsToolbarAppearance {

        String appDeleteWarning();

        String failToRetrieveApp();

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

        String sharePublic();

        String shareCollab();

        String share();
    }

    interface Presenter extends BeforeLoadEvent.HasBeforeLoadHandlers<FilterPagingLoadConfig>,
                                AppSearchResultLoadEvent.HasAppSearchResultLoadEventHandlers {

        AppsToolbarView getView();
    }

    void hideAppMenu();

    void hideWorkflowMenu();
}
