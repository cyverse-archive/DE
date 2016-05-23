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

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeStyle;

import java.util.List;

/**
 * This view contains the hierarchical {@link AppCategory} tree, and fires off selection events.
 *
 * Created by jstroot on 3/5/15.
 * @author jstroot
 */
public interface AppCategoriesView extends IsWidget,
                                           IsMaskable,
                                           AppCategorySelectionChangedEvent.HasAppCategorySelectionChangedEventHandlers {
    interface AppCategoriesAppearance {

        String favServiceFailure();

        String fetchAppDetailsError(Throwable caught);

        String getAppCategoriesLoadingMask();

        String headingText();

        void setTreeIcons(TreeStyle style);

        String copyAppSuccessMessage(String appName);

        String workspaceTab();

        String hpcTab();
    }

    /**
     * This presenter is responsible for updating/maintaining the {@code TreeStore} used by the
     * view's tree. It is designed to listen to changes made to apps via Store handlers and other
     * events.
     *
     * {@link AppInfoSelectedEvent}s are handled by this presenter because of the necessity of
     * constructing and displaying the particular {@link App}'s associated categories. These
     * hierarchies are constructed from the presenter's {@code TreeStore}.
     *
     * The copy events are handled by this presenter in order to refresh the user's app development
     * category when the service call to copy the App completes.
     *
     * {@link AppFavoritedEvent}s are handled here for the sake of updating {@code AppCategory}
     * counts.
     */
    interface Presenter extends AppSearchResultLoadEvent.AppSearchResultLoadEventHandler,
                                CopyAppSelected.CopyAppSelectedHandler,
                                CopyWorkflowSelected.CopyWorkflowSelectedHandler,
                                AppCategorySelectionChangedEvent.HasAppCategorySelectionChangedEventHandlers {

        AppCategory getSelectedAppCategory();

        AppCategoriesView getView();

        void go(HasId selectedAppCategory, TabPanel tabPanel);
    }

    interface AppCategoryHierarchyProvider {
        List<String> getGroupHierarchy(TreeStore<AppCategory> treeStore, AppCategory appCategory);
    }

    Tree<AppCategory, String> getTree();

}
