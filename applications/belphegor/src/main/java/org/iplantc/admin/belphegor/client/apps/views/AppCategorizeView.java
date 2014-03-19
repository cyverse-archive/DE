package org.iplantc.admin.belphegor.client.apps.views;

import org.iplantc.de.client.models.apps.AppGroup;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

public interface AppCategorizeView extends IsWidget {

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {
        void setAppGroups(List<AppGroup> children);
    }

    void setAppGroups(List<AppGroup> children);

    List<AppGroup> getSelectedGroups();

    void setSelectedGroups(List<AppGroup> groups);

    void removeGroupWithId(String groupId);
}
