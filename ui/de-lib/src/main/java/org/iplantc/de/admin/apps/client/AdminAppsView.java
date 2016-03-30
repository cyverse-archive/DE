package org.iplantc.de.admin.apps.client;

import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by jstroot on 4/21/14.
 * @author jstroot
 */
public interface AdminAppsView extends IsWidget {
    public interface AdminAppsViewAppearance {

        String avgUserRatingColumnLabel();

        int avgUserRatingColumnWidth();

        int integratedByColumnWidth();

        String integratedBy();

        String nameColumnLabel();

        int nameColumnWidth();
    }

    public interface AdminPresenter {
        interface AdminPresenterAppearance {

            String add();

            String addAppCategoryError(String name);

            String addCategoryPermissionError();

            String addCategoryPrompt();

            String appCategorizeSuccess(String name, List<String> groupNames);

            String confirmDeleteAppCategory(String name);

            String confirmDeleteAppTitle();

            String deleteAppCategoryError(String name);

            String deleteApplicationError(String name);

            String deleteCategoryPermissionError();

            String moveCategory();

            String moveCategoryError(String name);

            String noCategoriesSelected();

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

        void go(HasOneWidget container, HasId selectedAppCategory);

        void setViewDebugId(String baseId);
    }

}
