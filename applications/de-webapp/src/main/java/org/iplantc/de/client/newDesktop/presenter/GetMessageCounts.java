package org.iplantc.de.client.newDesktop.presenter;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.NewSystemMessagesEvent;
import org.iplantc.de.client.events.NotificationCountUpdateEvent;
import org.iplantc.de.client.events.SystemMessageCountUpdateEvent;
import org.iplantc.de.client.models.notifications.Counts;
import org.iplantc.de.client.newDesktop.NewDesktopView;
import org.iplantc.de.client.services.MessageServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This task requests the message counts from the backend
 */
final class GetMessageCounts implements Runnable {

    private final EventBus eventBus;
    private final MessageServiceFacade messageServiceFacade;
    private final NewDesktopView view;
    private final NewDesktopPresenterImpl presenter;

    GetMessageCounts(final EventBus eventBus,
                     final MessageServiceFacade messageServiceFacade,
                     final NewDesktopView view,
                     final NewDesktopPresenterImpl presenter) {
        this.eventBus = eventBus;
        this.messageServiceFacade = messageServiceFacade;
        this.view = view;
        this.presenter = presenter;
    }

    @Override
    public void run() {
        messageServiceFacade.getMessageCounts(new AsyncCallback<Counts>() {
            @Override
            public void onFailure(final Throwable caught) {
            }

            @Override
            public void onSuccess(final Counts cnts) {
                fireEvents(cnts);
            }
        });
    }

    private void fireEvents(final Counts counts) {
        final int unseenNoteCnt = counts.getUnseenNotificationCount();
        view.setUnseenNotificationCount(unseenNoteCnt);
        presenter.fetchRecentNotifications();
        // fetch the unseen messages

        final int unseenSysMsgCnt = counts.getUnseenSystemMessageCount();
        // Fire event for unseen notification counts
        eventBus.fireEvent(new NotificationCountUpdateEvent(unseenNoteCnt));
        // Fire event for system message counts
        eventBus.fireEvent(new SystemMessageCountUpdateEvent(unseenSysMsgCnt));

        if (counts.getNewSystemMessageCount() > 0) {
            eventBus.fireEvent(new NewSystemMessagesEvent());
        }
    }

}