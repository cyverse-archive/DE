package org.iplantc.de.client.models.notifications;

/**
 * Represents a notification category.
 * 
 * XXX JDS If these enum fields were the same name as what comes in (e.g. ANALYSIS could be
 * Analysis), then they could be deserialized directly into the autobean.
 */
public enum NotificationCategory {
    /** All notification categories */
    ALL(NotificationCategoryConstants.INSTANCE.notificationCategoryAll()),
    /** System notifications */
    SYSTEM(NotificationCategoryConstants.INSTANCE.notificationCategorySystem()),
    /** Data notifications */
    DATA(NotificationCategoryConstants.INSTANCE.notificationCategoryData()),
    /** Analysis notifications */
    ANALYSIS(NotificationCategoryConstants.INSTANCE.notificationCategoryAnalysis()),
    /** tool rquest status update notification */
    TOOLREQUEST(NotificationCategoryConstants.INSTANCE.toolRequest()),

    /** unseen notifications */
    NEW(NotificationCategoryConstants.INSTANCE.notificationCategoryUnseen());

    private String displayText;

    private NotificationCategory(String displayText) {
        this.displayText = displayText;
    }

    /**
     * Null-safe and case insensitive variant of valueOf(String)
     *
     * @param typeString
     * @return
     */
    public static NotificationCategory fromTypeString(String typeString) {
        if (typeString == null || typeString.isEmpty()) {
            return null;
        }
        String temp = typeString.replaceAll("\\s", "");
        return valueOf(temp.toUpperCase());
    }

    @Override
    public String toString() {
        return displayText;
    }
}