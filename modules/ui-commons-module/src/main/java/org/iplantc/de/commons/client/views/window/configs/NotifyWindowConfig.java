package org.iplantc.de.commons.client.views.window.configs;

import org.iplantc.de.client.models.notifications.NotificationCategory;

public interface NotifyWindowConfig extends WindowConfig {

    NotificationCategory getSortCategory();

    void setSortCategory(NotificationCategory category);
}
