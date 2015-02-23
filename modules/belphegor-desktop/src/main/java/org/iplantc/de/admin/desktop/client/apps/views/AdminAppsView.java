package org.iplantc.de.admin.desktop.client.apps.views;

import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent.HasAppSelectionChangedEventHandlers;
import org.iplantc.de.apps.client.AppsView;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by jstroot on 4/21/14.
 * @author jstroot
 */
public interface AdminAppsView extends AppsView {
    public interface AdminAppsViewAppearance {

        String avgUserRatingColumnLabel();

        int avgUserRatingColumnWidth();

        int integratedByColumnWidth();

        String integratedby();

        String nameColumnLabel();

        int nameColumnWidth();
    }
    public interface AdminPresenter extends Presenter {
        interface AdminPresenterAppearance {

            String add();

            String addAppCategoryError(String name);

            String addCategoryLoadingMask();

            String addCategoryPermissionError();

            String addCategoryPrompt();

            String appCategorizeSuccess(String name, List<String> groupNames);

            String categorizeAppLoadingMask();

            String confirmDeleteAppCategory(String name);

            String confirmDeleteAppCategoryWarning();

            String confirmDeleteAppTitle();

            String confirmDeleteAppWarning();

            String deleteAppCategoryError(String name);

            String deleteAppCategoryLoadingMask();

            String deleteAppLoadingMask();

            String deleteApplicationError(String name);

            String deleteCategoryPermissionError();

            String getAppDetailsLoadingMask();

            String invalidMoveMsg();

            String moveCategory();

            String moveCategoryError(String name);

            String noCategoriesSelected();

            String renameCategory();

            String renameAppCategoryLoadingMask();

            String renameCategoryError(String name);

            String renamePrompt();

            String restoreAppFailureMsg(String name);

            String restoreAppFailureMsgTitle();

            String restoreAppSuccessMsg(String name, String s);

            String restoreAppSuccessMsgTitle();

            String selectCategories(String name);

            String submit();

            String updateApplicationError();
        }

        boolean canMoveAppCategory(AppCategory parentCategory, AppCategory childCategory);

        boolean canMoveApp(final AppCategory parentGroup, final App app);

        void moveAppCategory(final AppCategory parentCategory, final AppCategory childCategory);

        void moveApp(final AppCategory parentCategory, final App app);

        void onAddAppCategoryClicked();

        void onRenameAppCategoryClicked();

        void onMoveCategoryClicked();

        void onDeleteCatClicked();

        void onDeleteAppClicked();

        void onRestoreAppClicked();

        void onCategorizeAppClicked();

    }

    interface Toolbar extends IsWidget {
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

        void init(AdminPresenter presenter,
                  AdminAppsView appView,
                  HasAppSelectionChangedEventHandlers hasAppSelectionChangedEventHandlers,
                  AppCategorySelectionChangedEvent.HasAppCategorySelectionChangedEventHandlers hasAppCategorySelectionChangedEventHandlers);
    }
}
