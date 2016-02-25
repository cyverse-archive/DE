package org.iplantc.de.notifications.client.views;

import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.notifications.client.events.NotificationGridRefreshEvent;
import org.iplantc.de.notifications.client.events.NotificationSelectionEvent;

import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.button.TextButton;

import java.util.List;

public interface NotificationView extends IsWidget,
                                          NotificationGridRefreshEvent.HasNotificationGridRefreshEventHandlers,
                                          NotificationSelectionEvent.HasNotificationSelectionEventHandlers {
    interface NotificationViewAppearance {

        String notifications();

        String refresh();

        String notificationDeleteFail();

        String category();

        int categoryColumnWidth();

        String messagesGridHeader();

        int messagesColumnWidth();

        String createdDateGridHeader();

        int createdDateColumnWidth();

    }

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {
        /**
         * Filters the list of notifications by a given Category.
         * 
         * @param category
         */
        public void filterBy(NotificationCategory category);

        /**
         * get default paging config
         * 
         * @return default FilterPagingLoadConfig
         */
        public FilterPagingLoadConfig buildDefaultLoadConfig();

        void setRefreshButton(TextButton refreshBtn);

        NotificationCategory getCurrentCategory();
    }

    /**
     * get current loader config
     * 
     * @return the current load config
     */
    public FilterPagingLoadConfig getCurrentLoadConfig();

    /**
     * Get list of selected notification
     * 
     * @return a list containing selected notification objects
     */
    public List<NotificationMessage> getSelectedItems();

    /**
     * loads notifications using given laod conig
     * 
     * @param config FilterPagingLoadConfig
     */
    public void loadNotifications(FilterPagingLoadConfig config);

    public void setLoader(
            PagingLoader<FilterPagingLoadConfig, PagingLoadResult<NotificationMessage>> loader);

    void setNorthWidget(IsWidget widget);

    void mask();

    void unmask();

    public TextButton getRefreshButton();

}
