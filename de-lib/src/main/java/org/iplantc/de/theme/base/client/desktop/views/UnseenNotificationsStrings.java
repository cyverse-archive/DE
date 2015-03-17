package org.iplantc.de.theme.base.client.desktop.views;

import com.google.gwt.i18n.client.Messages;

/**
 * Created by jstroot on 8/6/14.
 */
public interface UnseenNotificationsStrings extends Messages {
    String allNotifications();

    String markAllAsSeen();

    String newNotificationsLink(int unseenCount);

    String noNewNotifications();
}
