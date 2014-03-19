package org.iplantc.de.client.models.notifications;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface NotificationCategoryConstants extends Constants {

    static final NotificationCategoryConstants INSTANCE = GWT.create(NotificationCategoryConstants.class);

    String notificationCategoryUnseen();

    String toolRequest();

    String notificationCategoryAnalysis();

    String notificationCategoryData();

    String notificationCategorySystem();

    String notificationCategoryAll();
}
