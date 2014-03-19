package org.iplantc.de.apps.integration.client.view;

import com.google.gwt.user.client.ui.IsWidget;

public interface AppEditorToolbar extends IsWidget {

    public interface Presenter {

        void onArgumentOrderClicked();

        void onPreviewJsonClicked();

        void onPreviewUiClicked();

        /**
         * Submits the changed app to the server.
         */
        void onSaveClicked();

    }

    void setPresenter(AppEditorToolbar.Presenter presenter);

}
