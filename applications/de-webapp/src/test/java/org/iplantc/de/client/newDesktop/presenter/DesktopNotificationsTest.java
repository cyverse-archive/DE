package org.iplantc.de.client.newDesktop.presenter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwtmockito.GxtMockitoTestRunner;
import com.sencha.gxt.widget.core.client.WindowManager;
import com.sencha.gxt.widget.core.client.event.ShowContextMenuEvent;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.newDesktop.NewDesktopView;
import org.iplantc.de.client.newDesktop.presenter.util.MessagePoller;
import org.iplantc.de.client.newDesktop.views.NewDesktopViewImpl;
import org.iplantc.de.client.services.MessageServiceFacade;
import org.iplantc.de.client.sysmsgs.view.NewMessageView;
import org.iplantc.de.client.utils.NotifyInfo;
import org.iplantc.de.commons.client.requests.KeepaliveTimer;
import org.iplantc.de.resources.client.messages.IplantNewUserTourStrings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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

    @Before public void setup(){

    }

    @Test public void notificationsMarkedAsSeenOnNotificationMenuOpen() {
        NewDesktopViewImpl uut = new NewDesktopViewImpl(tourStringsMock, eventBusMock, windowMangerMock);
        uut.setPresenter(presenterMock);

        final ShowContextMenuEvent mockEvent = mock(ShowContextMenuEvent.class);
        uut.onShowNotificationMenu(mockEvent);

        verify(presenterMock).markAllNotificationsSeen();
        verifyNoMoreInteractions(presenterMock);
    }

    @Test public void recentNotificationsFetchedAtWebappInitialization() {
        NewDesktopPresenterImpl uut = new NewDesktopPresenterImpl(viewMock,
                                                                  globalEvntHndlrMock,
                windowEvntHndlrMock, eventBusMock, sysMsgPresenterMock, windowMangerMock, desktopWindowManagerMock, msgPollerMock, keepAliveTimerMock){
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

    @Test public void notificationPopupsAreShownForFirstTenUnseenMessages(){

        // FIXME JDS DesktopRewrite
    }

    @Test public void summaryNotificationPopupShownAfterTenMsgsShown(){

        // FIXME JDS DesktopRewrite
    }
}
