package org.iplantc.de.client.views.windows.configs;

import org.iplantc.de.client.models.notifications.NotificationCategory;

public interface NotifyWindowConfig extends WindowConfig {

    NotificationCategory getSortCategory();

    void setSortCategory(NotificationCategory category);
}
