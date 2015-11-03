package org.iplantc.de.admin.apps.client;

import org.iplantc.de.admin.apps.client.events.selection.AddCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.CategorizeAppSelected;
import org.iplantc.de.admin.apps.client.events.selection.DeleteCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.MoveCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.RenameCategorySelected;
import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;

import com.sencha.gxt.data.shared.event.StoreRemoveEvent;

import java.util.List;

/**
 * Created by jstroot on 3/9/15.
 * @author jstroot
 */
public interface AdminCategoriesView extends AppCategoriesView {

    interface Presenter extends AddCategorySelected.AddCategorySelectedHandler,
                                RenameCategorySelected.RenameCategorySelectedHandler,
                                DeleteCategorySelected.DeleteCategorySelectedHandler,
                                StoreRemoveEvent.StoreRemoveHandler<App>,
                                CategorizeAppSelected.CategorizeAppSelectedHandler,
                                MoveCategorySelected.MoveCategorySelectedHandler,
                                AppSearchResultLoadEvent.AppSearchResultLoadEventHandler {

        interface Appearance {

            String add();

            String addAppCategoryError(String name);

            String addCategoryLoadingMask();

            String addCategoryPermissionError();

            String addCategoryPrompt();

            String appCategorizeSuccess(String name, List<String> groupNames);

            String categorizeAppLoadingMask();

            String confirmDeleteAppCategory(String name);

            String confirmDeleteAppCategoryWarning();

            String deleteAppCategoryError(String name);

            String deleteAppCategoryLoadingMask();

            String deleteCategoryPermissionError();

            String getAppCategoriesLoadingMask();

            String getAppDetailsLoadingMask();

            String invalidMoveMsg();

            String moveCategory();

            String moveCategoryError(String name);

            String noCategoriesSelected();

            String renameAppCategoryLoadingMask();

            String renameCategory();

            String renameCategoryError(String name);

            String renamePrompt();

            String selectCategories(String name);

            String submit();
        }

        AppCategoriesView getView();

        void go(HasId selectedAppCategory);
    }
}
