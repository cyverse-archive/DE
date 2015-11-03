package org.iplantc.de.desktop.client.views;

import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.resources.client.messages.IplantNewUserTourStrings;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.WindowManager;
import com.sencha.gxt.widget.core.client.event.RegisterEvent;
import com.sencha.gxt.widget.core.client.event.ShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.UnregisterEvent;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class DesktopNotifications_DesktopViewTest {

    @Mock RegisterEvent<Widget> registerEventMock;
    @Mock IplantNewUserTourStrings tourStringsMock;
    @Mock UnregisterEvent<Widget> unregisterEventMock;
    @Mock WindowManager windowManagerMock;

    @Test public void notificationsMarkedSeenWhenNotificationBtnSelectedWithLessThan10Unseen() {
        DesktopViewImpl uut = new DesktopViewImpl(tourStringsMock, windowManagerMock);
        verifyViewInit(uut);
        final DesktopView.Presenter mockPresenter = mock(DesktopView.Presenter.class);
        uut.setPresenter(mockPresenter);
        uut.unseenNotificationCount = 9;

        uut.onNotificationMenuClicked(mock(ShowContextMenuEvent.class));
        verify(mockPresenter).doMarkAllSeen(eq(false));
        verifyNoMoreInteractions(mockPresenter);
    }

    @Test public void notificationsNotMarkedSeenWhenNotificationBtnSelectedWithGreaterThan10Unseen() {
        DesktopViewImpl uut = new DesktopViewImpl(tourStringsMock, windowManagerMock);
        verifyViewInit(uut);
        final DesktopView.Presenter mockPresenter = mock(DesktopView.Presenter.class);
        uut.setPresenter(mockPresenter);
        uut.unseenNotificationCount = 11;

        uut.onNotificationMenuClicked(mock(ShowContextMenuEvent.class));
        verify(mockPresenter, never()).doMarkAllSeen(anyBoolean());
        verifyNoMoreInteractions(mockPresenter);
    }

    private void verifyViewInit(DesktopViewImpl uut) {
        verify(windowManagerMock).addRegisterHandler(eq(uut));
        verify(windowManagerMock).addUnregisterHandler(eq(uut));
    }
}
