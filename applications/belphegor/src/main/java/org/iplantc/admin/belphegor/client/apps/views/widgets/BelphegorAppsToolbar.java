package org.iplantc.admin.belphegor.client.apps.views.widgets;

import org.iplantc.de.apps.client.views.widgets.proxy.AppSearchRpcProxy;

import com.google.gwt.user.client.ui.IsWidget;

public interface BelphegorAppsToolbar extends IsWidget {
    public interface Presenter {

        void onAddAppGroupClicked();

        void onRenameAppGroupClicked();

        void onDeleteClicked();

        void onRestoreAppClicked();

        void onCategorizeAppClicked();
    }

    void setAddAppGroupButtonEnabled(boolean enabled);

    void setRenameAppGroupButtonEnabled(boolean enabled);

    void setDeleteButtonEnabled(boolean enabled);

    void setRestoreButtonEnabled(boolean enabled);

    void setCategorizeButtonEnabled(boolean enabled);

    void setPresenter(Presenter presenter);

    AppSearchRpcProxy getAppSearchRpcProxy();
}