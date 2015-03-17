package org.iplantc.de.client.models.notifications;


import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.autobean.shared.Splittable;

public interface Notification {

    @PropertyName("seen")
    void setSeen(boolean seen);

    @PropertyName("seen")
    boolean isSeen();

    @PropertyName("message")
    void setMessage(NotificationMessage message);

    @PropertyName("message")
    NotificationMessage getMessage();

    @PropertyName("type")
    void setCategory(String category);

    @PropertyName("type")
    String getCategory();

    @PropertyName("payload")
    Splittable getNotificationPayload();

    @PropertyName("payload")
    void setNotificationPayload(Splittable payload);
}
