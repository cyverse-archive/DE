package org.iplantc.de.admin.apps.client;

import org.iplantc.de.admin.apps.client.events.selection.AddCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.CategorizeAppSelected;
import org.iplantc.de.admin.apps.client.events.selection.DeleteCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.MoveCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.RenameCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.RestoreAppSelected;
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
                                              AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler,
                                              AppSelectionChangedEvent.AppSelectionChangedEventHandler {
    interface ToolbarAppearance {

        ImageResource addIcon();

        String add();

        String renameCategory();

        ImageResource renameCategoryIcon();

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
