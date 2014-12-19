package org.iplantc.de.desktop.client.presenter;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.systemMessages.client.events.NewSystemMessagesEvent;
import org.iplantc.de.client.models.notifications.Counts;
import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.client.services.MessageServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This task requests the message counts from the backend
 */
final class GetMessageCounts implements Runnable {

    private final EventBus eventBus;
    private final MessageServiceFacade messageServiceFacade;
    private final DesktopView view;
    private final DesktopPresenterImpl presenter;

    GetMessageCounts(final EventBus eventBus,
                     final MessageServiceFacade messageServiceFacade,
                     final DesktopView view,
                     final DesktopPresenterImpl presenter) {
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
        presenter.fetchRecentNotifications(unseenNoteCnt);
        view.setUnseenNotificationCount(unseenNoteCnt);

        final int unseenSysMsgCnt = counts.getUnseenSystemMessageCount();
        view.setUnseenSystemMessageCount(unseenSysMsgCnt);

        if (counts.getNewSystemMessageCount() > 0) {
            eventBus.fireEvent(new NewSystemMessagesEvent());
        }
    }

}
