package org.iplantc.de.apps.client.views;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.models.apps.AppRefLink;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.TreeStore;

import java.util.List;

public interface SubmitAppForPublicUseView extends IsWidget {

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {
        void onSubmit();

        void go(HasOneWidget container, App selectedApp, AsyncCallback<String> callback);
    }

    TreeStore<AppGroup> getTreeStore();

    void expandAppGroups();

    JSONObject toJson();

    App getSelectedApp();

    boolean validate();

    public void loadReferences(List<AppRefLink> refs);

    void setSelectedApp(App selectedApp);
}
