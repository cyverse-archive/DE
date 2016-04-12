package org.iplantc.de.desktop.client.presenter;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationList;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.services.MessageServiceFacade;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.requests.KeepaliveTimer;
import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.desktop.client.presenter.util.MessagePoller;
import org.iplantc.de.desktop.client.views.widgets.UnseenNotificationsView;
import org.iplantc.de.notifications.client.utils.NotifyInfo;
import org.iplantc.de.resources.client.messages.IplantNewUserTourStrings;
import org.iplantc.de.systemMessages.client.view.NewMessageView;

import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwtmockito.GxtMockitoTestRunner;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.WindowManager;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.List;

/**
 * @author jstroot
 */
@RunWith(GxtMockitoTestRunner.class)
public class DesktopNotifications_PresenterTest {

    @Mock IplantNewUserTourStrings tourStringsMock;
    @Mock EventBus eventBusMock;
    @Mock WindowManager windowMangerMock;
    @Mock DesktopView.Presenter presenterMock;
    @Mock
    DesktopView viewMock;
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
        DesktopPresenterImpl uut = new DesktopPresenterImpl(viewMock,
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
                // Test stub, Do nothing
            }

            @Override
            void initKBShortCuts() {
                // Test stub, Do nothing
            }

            @Override
            void processQueryStrings() {
                // Test stub, Do nothing
            }
        };

        uut.messageServiceFacade = mock(MessageServiceFacade.class);
        uut.deProperties = mock(DEProperties.class);
        uut.postBootstrap(mock(Panel.class));
        verify(uut.messageServiceFacade).getRecentMessages(Matchers.<AsyncCallback<NotificationList>>any());
        // TODO JDS Expand test to verify that notification store is updated
    }

    @Test public void notificationMarkedAsSeenWhenSelected() {

        DesktopPresenterImpl uut = new DesktopPresenterImpl(viewMock,
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
        DesktopPresenterImpl testPresenter = spy(new DesktopPresenterImpl(viewMock,
                                                                            globalEvntHndlrMock,
                                                                            windowEvntHndlrMock,
                                                                            eventBusMock,
                                                                            sysMsgPresenterMock,
                                                                            windowMangerMock,
                                                                            desktopWindowManagerMock,
                                                                            msgPollerMock,
                                                                            keepAliveTimerMock));
        testPresenter.messageServiceFacade = mock(MessageServiceFacade.class);
        testPresenter.announcer = mock(IplantAnnouncer.class);
        testPresenter.appearance = mock(DesktopView.Presenter.DesktopPresenterAppearance.class);
        when(testPresenter.appearance.markAllAsSeenSuccess()).thenReturn("Mock success");

        when(viewMock.getNotificationStore()).thenReturn(msgStoreMock);
        when(msgStoreMock.getAll()).thenReturn(Lists.<NotificationMessage>newArrayList());
        UnseenNotificationsView testUnseenNotification = new UnseenNotificationsView();
        testUnseenNotification.setPresenter(testPresenter);

        testUnseenNotification.onMarkAllSeenClicked(mock(ClickEvent.class));
        verify(testPresenter).doMarkAllSeen(eq(true));
        verify(testPresenter.messageServiceFacade).markAllNotificationsSeen(voidAsyncCaptor.capture());
        voidAsyncCaptor.getValue().onSuccess(null);
        verify(viewMock).setUnseenNotificationCount(eq(0));
        // TODO JDS Expand test to verify that notification store is updated
    }



}
