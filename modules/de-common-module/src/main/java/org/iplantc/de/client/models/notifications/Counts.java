package org.iplantc.de.client.models.notifications;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * Wraps the response for a count-messages call when the seen parameter is false.
 */
public interface Counts {

    /**
     * the number of unseen notifications
     */
    @PropertyName("user-total")
    int getUnseenNotificationCount();

    /**
     * the total number of active system messages that have not been dismissed
     */
    @PropertyName("system-total")
    int getSystemMessageCount();

    /**
     * the number of active system messages that have not been marked as received.
     */
    @PropertyName("system-total-new")
    int getNewSystemMessageCount();

    /**
     * the number of active system messages that have not been marked as seen.
     */
    @PropertyName("system-total-unseen")
    int getUnseenSystemMessageCount();

}
