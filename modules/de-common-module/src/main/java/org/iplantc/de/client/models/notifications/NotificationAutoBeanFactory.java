package org.iplantc.de.client.models.notifications;


import org.iplantc.de.client.models.notifications.payload.PayloadAnalysis;
import org.iplantc.de.client.models.notifications.payload.PayloadData;
import org.iplantc.de.client.models.notifications.payload.PayloadToolRequest;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * the factory for creating the auto bean wrappers of the notification messages
 */
public interface NotificationAutoBeanFactory extends AutoBeanFactory {

    /**
     * the response of a count-messages call when the seen parameter is false.
     */
    AutoBean<Counts> getCounts();

    AutoBean<NotificationMessage> getNotificationMessage();

    AutoBean<NotificationList> getNotificationList();

    AutoBean<Notification> getNotification();

    AutoBean<PayloadAnalysis> getNotificationPayloadAnalysis();

    AutoBean<PayloadData> getNotificationPayloadData();

    AutoBean<PayloadToolRequest> getNotificationToolRequestContext();
}
