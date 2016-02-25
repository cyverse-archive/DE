package org.iplantc.de.theme.base.client.notifications;

import org.iplantc.de.notifications.client.views.NotificationView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author aramsey
 */
public class NotificationViewDefaultAppearance implements NotificationView.NotificationViewAppearance {

    private IplantDisplayStrings iplantDisplayStrings;
    private IplantErrorStrings iplantErrorStrings;

    public NotificationViewDefaultAppearance() {
        this(GWT.<IplantDisplayStrings>create(IplantDisplayStrings.class),
             (GWT.<IplantErrorStrings>create(IplantErrorStrings.class)));
    }

    public NotificationViewDefaultAppearance(IplantDisplayStrings iplantDisplayStrings,
                                             IplantErrorStrings iplantErrorStrings) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantErrorStrings = iplantErrorStrings;
    }

    @Override
    public String notifications() {
        return iplantDisplayStrings.notifications();
    }

    @Override
    public String refresh() {
        return iplantDisplayStrings.refresh();
    }

    @Override
    public String notificationDeleteFail() {
        return iplantErrorStrings.notificationDeletFail();
    }

    @Override
    public String category() {
        return iplantDisplayStrings.category();
    }

    @Override
    public int categoryColumnWidth() {
        return 100;
    }

    @Override
    public String messagesGridHeader() {
        return iplantDisplayStrings.messagesGridHeader();
    }

    @Override
    public int messagesColumnWidth() {
        return 420;
    }

    @Override
    public String createdDateGridHeader() {
        return iplantDisplayStrings.createdDateGridHeader();
    }

    @Override
    public int createdDateColumnWidth() {
        return 170;
    }
}
