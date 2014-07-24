package org.iplantc.de.client.newDesktop.presenter;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.WindowType;
import org.iplantc.de.client.newDesktop.NewDesktopView;
import org.iplantc.de.client.newDesktop.presenter.util.MessagePoller;
import org.iplantc.de.client.sysmsgs.view.NewMessageView;
import org.iplantc.de.commons.client.requests.KeepaliveTimer;

import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.WindowManager;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class NewDesktopPresenterImplTest {

    @Mock DesktopWindowManager desktopWindowManagerMock;
    @Mock EventBus eventBusMock;
    @Mock DesktopPresenterEventHandler globalEventHandlerMock;
    @Mock KeepaliveTimer keepaliveTimerMock;
    @Mock MessagePoller messagePollerMock;
    @Mock NewMessageView.Presenter sysMsgPresenterMock;
    @Mock NewDesktopView viewMock;
    @Mock DesktopPresenterWindowEventHandler windowEventHandlerMock;
    @Mock WindowManager windowManagerMock;

    private NewDesktopPresenterImpl uut;
    @Before public void setUp() {
        uut = new NewDesktopPresenterImpl(viewMock,
                                          globalEventHandlerMock,
                                          windowEventHandlerMock,
                                          eventBusMock,
                                          sysMsgPresenterMock,
                                          windowManagerMock,
                                          desktopWindowManagerMock,
                                          messagePollerMock,
                                          keepaliveTimerMock);

    }

    @Test public void testOnAboutClick() {
        uut.onAboutClick();
        verify(desktopWindowManagerMock).show(eq(WindowType.ABOUT));
        verifyNoMoreInteractions(desktopWindowManagerMock);
    }

    @Test public void testOnAnalysesWinBtnSelect() {
        uut.onAnalysesWinBtnSelect();
        verify(desktopWindowManagerMock).show(eq(WindowType.ANALYSES));
        verifyNoMoreInteractions(desktopWindowManagerMock);
    }

    @Test public void testOnAppsWinBtnSelect() {
        uut.onAppsWinBtnSelect();
        verify(desktopWindowManagerMock).show(eq(WindowType.APPS));
        verifyNoMoreInteractions(desktopWindowManagerMock);
    }

    @Test public void testOnDataWinBtnSelect() {
        uut.onDataWinBtnSelect();
        verify(desktopWindowManagerMock).show(eq(WindowType.DATA));
        verifyNoMoreInteractions(desktopWindowManagerMock);
    }

    @Test public void testOnSystemMessagesClick() {
        uut.onSystemMessagesClick();
        verify(desktopWindowManagerMock).show(WindowType.SYSTEM_MESSAGES);
        verifyNoMoreInteractions(desktopWindowManagerMock);
    }



}