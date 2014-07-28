package org.iplantc.de.client.newDesktop.presenter;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.newDesktop.NewDesktopView;
import org.iplantc.de.client.newDesktop.presenter.util.MessagePoller;
import org.iplantc.de.client.newDesktop.views.widgets.UnseenNotificationsView;
import org.iplantc.de.client.services.MessageServiceFacade;
import org.iplantc.de.client.sysmsgs.view.NewMessageView;
import org.iplantc.de.client.utils.NotifyInfo;
import org.iplantc.de.commons.client.requests.KeepaliveTimer;
import org.iplantc.de.resources.client.messages.IplantNewUserTourStrings;

import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwtmockito.GxtMockitoTestRunner;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.WindowManager;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

/**
 * Created by jstroot on 7/23/14.
 */
@RunWith(GxtMockitoTestRunner.class)
public class DesktopNotificationsTest {

    @Mock IplantNewUserTourStrings tourStringsMock;
    @Mock EventBus eventBusMock;
    @Mock WindowManager windowMangerMock;
    @Mock NewDesktopView.Presenter presenterMock;
    @Mock NewDesktopView viewMock;
    @Mock DesktopPresenterEventHandler globalEvntHndlrMock;
    @Mock DesktopPresenterWindowEventHandler windowEvntHndlrMock;
    @Mock NewMessageView.Presenter sysMsgPresenterMock;
    @Mock DesktopWindowManager desktopWindowManagerMock;
    @Mock MessagePoller msgPollerMock;
    @Mock KeepaliveTimer keepAliveTimerMock;
    @Mock NotifyInfo notifyInfoMock;

    @Mock ListStore<NotificationMessage> msgStoreMock;

    @Captor ArgumentCaptor<AsyncCallback<String>> stringAsyncCaptor;
    @Captor ArgumentCaptor<AsyncCallback<Void>> voidAsyncCaptor;

    @Before public void setup(){

    }

    @Test public void recentNotificationsFetchedAtWebappInitialization() {
        NewDesktopPresenterImpl uut = new NewDesktopPresenterImpl(viewMock,
                                                                  globalEvntHndlrMock,
                                                                  windowEvntHndlrMock,
                                                                  eventBusMock,
                                                                  sysMsgPresenterMock,
                                                                  windowMangerMock,
                                                                  desktopWindowManagerMock,
                                                                  msgPollerMock,
                                                                  keepAliveTimerMock){
            @Override
            void setBrowserContextMenuEnabled(boolean enabled) {
            }

            @Override
            void initKBShortCuts() {
            }

            @Override
            void processQueryStrings() {
            }
        };

        uut.messageServiceFacade = mock(MessageServiceFacade.class);
        uut.deProperties = mock(DEProperties.class);
        uut.postBootstrap(mock(Panel.class));
        verify(uut.messageServiceFacade).getRecentMessages(any(AsyncCallback.class));
        // TODO JDS Expand test to verify that notification store is updated
    }

    @Test public void notificationMarkedAsSeenWhenSelected() {

        NewDesktopPresenterImpl uut = new NewDesktopPresenterImpl(viewMock,
                                                                  globalEvntHndlrMock,
                                                                  windowEvntHndlrMock,
                                                                  eventBusMock,
                                                                  sysMsgPresenterMock,
                                                                  windowMangerMock,
                                                                  desktopWindowManagerMock,
                                                                  msgPollerMock,
                                                                  keepAliveTimerMock);
        uut.messageServiceFacade = mock(MessageServiceFacade.class);

        final NotificationMessage mockMsg = mock(NotificationMessage.class);
        final NotificationCategory category = NotificationCategory.ALL;
        when(mockMsg.getCategory()).thenReturn(category);
        when(mockMsg.getContext()).thenReturn("context");
        uut.onNotificationSelected(mockMsg);

        when(viewMock.getNotificationStore()).thenReturn(msgStoreMock);
        when(msgStoreMock.getAll()).thenReturn(Lists.<NotificationMessage>newArrayList());
        verify(uut.messageServiceFacade).markAsSeen(eq(mockMsg), stringAsyncCaptor.capture());
        Splittable parent = StringQuoter.createSplittable();
        StringQuoter.create("1").assign(parent, "count");
        stringAsyncCaptor.getValue().onSuccess(parent.getPayload());
        verify(mockMsg).setSeen(eq(true));
        verify(msgStoreMock).update(eq(mockMsg));
    }

    @Test public void allNotificationsMarkedAsSeenWhenMarkAllSeenLinkClicked() {
        NewDesktopPresenterImpl testPresenter = spy(new NewDesktopPresenterImpl(viewMock,
                                                                            globalEvntHndlrMock,
                                                                            windowEvntHndlrMock,
                                                                            eventBusMock,
                                                                            sysMsgPresenterMock,
                                                                            windowMangerMock,
                                                                            desktopWindowManagerMock,
                                                                            msgPollerMock,
                                                                            keepAliveTimerMock));
        testPresenter.messageServiceFacade = mock(MessageServiceFacade.class);

        UnseenNotificationsView testUnseenNotification = new UnseenNotificationsView();
        testUnseenNotification.setPresenter(testPresenter);

        testUnseenNotification.onMarkAllSeenClicked(mock(ClickEvent.class));
        verify(testPresenter).doMarkAllSeen();
        verify(testPresenter.messageServiceFacade).markAllNotificationsSeen(voidAsyncCaptor.capture());
        voidAsyncCaptor.getValue().onSuccess(null);
        verify(viewMock).setUnseenNotificationCount(eq(0));
        // TODO JDS Expand test to verify that notification store is updated
    }

}
