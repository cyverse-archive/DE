package org.iplantc.de.apps.client;

import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppInfoSelectedEvent;
import org.iplantc.de.apps.client.events.selection.CopyAppSelected;
import org.iplantc.de.apps.client.events.selection.CopyWorkflowSelected;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;

import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeStyle;

import java.util.List;

/**
 * Created by jstroot on 3/5/15.
 * @author jstroot
 */
public interface AppCategoriesView extends IsWidget,
                                           IsMaskable,
                                           AppCategorySelectionChangedEvent.HasAppCategorySelectionChangedEventHandlers {
    interface AppCategoriesAppearance {

        String headingText();

        void setTreeIcons(TreeStyle style);
    }

    interface Presenter extends AppInfoSelectedEvent.AppInfoSelectedEventHandler,
                                AppSearchResultLoadEvent.AppSearchResultLoadEventHandler,
                                StoreAddEvent.StoreAddHandler<App>,
                                StoreRemoveEvent.StoreRemoveHandler<App>,
                                CopyAppSelected.CopyAppSelectedHandler,
                                CopyWorkflowSelected.CopyWorkflowSelectedHandler,
                                StoreUpdateEvent.StoreUpdateHandler<App>,
                                AppFavoritedEvent.AppFavoritedEventHandler {

        AppCategory getSelectedAppCategory();

        AppCategoriesView getView();

        void go(HasId selectedAppCategory);
    }

    interface AppCategoryHierarchyProvider {
        List<String> getGroupHierarchy(AppCategory appCategory);
    }

    Tree<AppCategory, String> getTree();

}
