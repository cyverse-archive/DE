package org.iplantc.de.client.models.notifications;

import org.iplantc.de.client.models.HasId;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * 
 * Notification bean
 * 
 * @author sriram
 * 
 */
public interface NotificationMessage extends HasId{

    @PropertyName("id")
    void setId(String id);

    @PropertyName("id")
    String getId();

    @PropertyName("text")
    String getMessage();

    @PropertyName("text")
    void setMessage(String message);

    @PropertyName("timestamp")
    void setTimestamp(long date);

    @PropertyName("timestamp")
    long getTimestamp();

    @PropertyName("type")
    NotificationCategory getCategory();

    @PropertyName("type")
    void setCategory(NotificationCategory type);

    String getContext();

    void setContext(String context);

    void setSeen(boolean seen);

    boolean isSeen();
}
