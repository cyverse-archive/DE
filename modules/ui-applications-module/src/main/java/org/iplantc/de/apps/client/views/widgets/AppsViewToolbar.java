package org.iplantc.de.apps.client.views.widgets;

import org.iplantc.de.apps.client.views.widgets.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppGroup;

import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * FIXME JDS Ensure that all buttons have the appropriate debug ids.
 * 
 * @author jstroot
 * 
 */
public interface AppsViewToolbar extends IsWidget {
    public interface Presenter {
        interface Builder extends org.iplantc.de.commons.client.presenter.Presenter {
            Builder hideToolbarButtonCreate();

            Builder hideToolbarButtonCopy();

            Builder hideToolbarButtonEdit();

            Builder hideToolbarButtonDelete();

            Builder hideToolbarButtonSubmit();

            Builder hideToolbarButtonRequestTool();
            
            void go(HasOneWidget container, final AppGroup selectedAppGroup, final App selectedApp);

            Builder hideToolbarMenuEdit();
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
    }

    void setPresenter(Presenter presenter);

    void setEditButtonEnabled(boolean enabled);

    void setSubmitButtonEnabled(boolean enabled);

    void setDeleteButtonEnabled(boolean enabled);

    void setCopyButtonEnabled(boolean enabled);

    void setAppRunButtonEnabled(boolean enabled);

    void setCreateButtonVisible(boolean visible);

    void setCopyButtonVisible(boolean visible);

    void setEditButtonVisible(boolean visible);

    void setDeleteButtonVisible(boolean visible);

    void setSubmitButtonVisible(boolean visible);

    void setRequestToolButtonVisible(boolean visible);

    AppSearchRpcProxy getAppSearchRpcProxy();

    void setEditMenuEnabled(boolean enabled);

    void setEditMenuVisible(boolean visible);
}
