package org.iplantc.de.desktop.client.views;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.desktop.client.views.widgets.DesktopIconButton;
import org.iplantc.de.resources.client.messages.IplantNewUserTourStrings;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GxtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

import com.sencha.gxt.widget.core.client.WindowManager;
import com.sencha.gxt.widget.core.client.event.RegisterEvent;
import com.sencha.gxt.widget.core.client.event.ShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.UnregisterEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
@WithClassesToStub(DesktopIconButton.class)
public class DesktopNotifications_DesktopViewTest {

    @Mock RegisterEvent<Widget> registerEventMock;
    @Mock IplantNewUserTourStrings tourStringsMock;
    @Mock UnregisterEvent<Widget> unregisterEventMock;
    @Mock WindowManager windowManagerMock;
    @Mock DesktopView.Presenter mockPresenter;
    @Mock DesktopIconButton notificationsBtnMock;

    private DesktopViewImpl uut;

    @Before
    public void setUp() {
        uut = new DesktopViewImpl(tourStringsMock, windowManagerMock);
        uut.notificationsBtn = notificationsBtnMock;
        verifyViewInit(uut);
        uut.setPresenter(mockPresenter);
    }

    @Test public void notificationsMarkedSeenWhenNotificationBtnSelectedWithLessThan10Unseen() {

        uut.unseenNotificationCount = 9;

        uut.onNotificationMenuClicked(mock(ShowContextMenuEvent.class));
        verify(mockPresenter).doMarkAllSeen(eq(false));
        verifyNoMoreInteractions(mockPresenter);
    }

    @Test public void notificationsNotMarkedSeenWhenNotificationBtnSelectedWithGreaterThan10Unseen() {

        uut.unseenNotificationCount = 11;

        uut.onNotificationMenuClicked(mock(ShowContextMenuEvent.class));
        verify(mockPresenter, never()).doMarkAllSeen(anyBoolean());
        verifyNoMoreInteractions(mockPresenter);
    }

    @Test public void notificationMenuHidesOnSeeAllNotifications() {
        uut.hideNotificationMenu();
        verify(notificationsBtnMock).hideMenu();
        verifyNoMoreInteractions(notificationsBtnMock, mockPresenter);
    }

    private void verifyViewInit(DesktopViewImpl uut) {
        verify(windowManagerMock).addRegisterHandler(eq(uut));
        verify(windowManagerMock).addUnregisterHandler(eq(uut));
    }
}
