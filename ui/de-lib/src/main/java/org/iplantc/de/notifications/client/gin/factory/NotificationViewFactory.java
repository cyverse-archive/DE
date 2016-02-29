package org.iplantc.de.notifications.client.gin.factory;

import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.notifications.client.views.NotificationView;

import com.sencha.gxt.data.shared.ListStore;

/**
 * @author aramsey
 */
public interface NotificationViewFactory {

    NotificationView create(ListStore<NotificationMessage> listStore);
}
