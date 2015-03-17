package org.iplantc.de.admin.apps.client;

import org.iplantc.de.admin.apps.client.events.selection.AddCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.CategorizeAppSelected;
import org.iplantc.de.admin.apps.client.events.selection.DeleteCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.MoveCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.RenameCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.RestoreAppSelected;
import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.BeforeAppSearchEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;

import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author jstroot
 */
public interface AdminAppsToolbarView extends IsWidget,
                                              HasHandlers,
                                              AddCategorySelected.HasAddCategorySelectedHandlers,
                                              CategorizeAppSelected.HasCategorizeAppSelectedHandlers,
                                              DeleteCategorySelected.HasDeleteCategorySelectedHandlers,
                                              DeleteAppsSelected.HasDeleteAppsSelectedHandlers,
                                              MoveCategorySelected.HasMoveCategorySelectedHandlers,
                                              RenameCategorySelected.HasRenameCategorySelectedHandlers,
                                              RestoreAppSelected.HasRestoreAppSelectedHandlers,
                                              BeforeAppSearchEvent.HasBeforeAppSearchEventHandlers,
                                              AppSearchResultLoadEvent.HasAppSearchResultLoadEventHandlers,
                                              AppSelectionChangedEvent.AppSelectionChangedEventHandler,
                                              AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler {
    interface ToolbarAppearance extends AppsToolbarView.AppsToolbarAppearance {

        String addCategoryPrompt();

        ImageResource addIcon();

        String add();

        String confirmDeleteAppCategory(String name);

        String confirmDeleteAppCategoryWarning();

        String confirmDeleteAppTitle();

        String confirmDeleteAppWarning();

        String renameCategory();

        ImageResource renameCategoryIcon();

        String renamePrompt();

        String restoreApp();

        ImageResource restoreAppIcon();

        String deleteCategory();

        ImageResource deleteIcon();

        String categorizeApp();

        ImageResource categoryAppIcon();

        ImageResource deleteAppIcon();

        String deleteApp();

        String moveCategory();

        ImageResource moveCategoryIcon();
    }

    interface Presenter {

        AdminAppsToolbarView getView();

    }
}
