package org.iplantc.de.apps.client.views.widgets;

import org.iplantc.de.apps.client.views.widgets.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppGroup;

import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * FIXME JDS Ensure that all buttons have the appropriate debug ids.
 * 
 * @author jstroot
 * 
 */
public interface AppsViewToolbar extends IsWidget {
    public interface Presenter {
        interface Builder extends org.iplantc.de.commons.client.presenter.Presenter {
            Builder hideToolbarAppButton();

            Builder hideToolbarWorkFlowButton();

            void go(HasOneWidget container, final AppGroup selectedAppGroup, final App selectedApp);
        }

        public Builder builder();

        AppsViewToolbar getToolbar();

        void onAppInfoClicked();

        void onRequestToolClicked();

        void onCopyClicked();

        void onDeleteClicked();

        void submitClicked();

        void createNewAppClicked();

        void createWorkflowClicked();

        void onEditClicked();

        public void onAppRunClick();

        AppSearchRpcProxy getAppSearchRpcProxy();

        List<AppGroup> getGroupHierarchy(AppGroup grp);

        List<String> computeGroupHirarchy(AppGroup ag);
    }

    void setPresenter(Presenter presenter);

    void setEditAppMenuItemEnabled(boolean enabled);

    void setSubmitAppMenuItemEnabled(boolean enabled);

    void setDeleteAppMenuItemEnabled(boolean enabled);

    void setCopyAppMenuItemEnabled(boolean enabled);

    void setAppRunMenuItemEnabled(boolean enabled);

    void setEditWorkflowMenuItemEnabled(boolean enabled);

    void setSubmitWorkflowMenuItemEnabled(boolean enabled);

    void setDeleteWorkflowMenuItemEnabled(boolean enabled);

    void setCopyWorkflowMenuItemEnabled(boolean enabled);

    void setWorkflowRunMenuItemEnabled(boolean enabled);

    void setAppMenuEnabled(boolean enabled);

    void setWorkflowMenuEnabled(boolean enabled);

    void hideAppMenu();

    void hideWorkflowMenu();

    AppSearchRpcProxy getAppSearchRpcProxy();

}
