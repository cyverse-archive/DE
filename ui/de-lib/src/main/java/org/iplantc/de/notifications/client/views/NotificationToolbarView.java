package org.iplantc.de.notifications.client.views;

import org.iplantc.de.client.models.notifications.NotificationCategory;

import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.button.TextButton;

/**
 * 
 * 
 * @author sriram
 * 
 */
public interface NotificationToolbarView extends IsWidget {

    public interface Presenter {

        void onFilterSelection(NotificationCategory cat);

        void onDeleteClicked();

        void onDeleteAllClicked();

    }

    void setDeleteButtonEnabled(boolean enabled);

    void setDeleteAllButtonEnabled(boolean enabled);

    void setPresenter(Presenter p);

    void setRefreshButton(TextButton refreshBtn);

    void setCurrentCategory(NotificationCategory category);

}
