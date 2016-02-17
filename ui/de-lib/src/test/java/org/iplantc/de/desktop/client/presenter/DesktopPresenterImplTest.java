package org.iplantc.de.desktop.client.presenter;

import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.desktop.client.presenter.util.MessagePoller;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.WindowType;
import org.iplantc.de.commons.client.requests.KeepaliveTimer;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.systemMessages.client.view.NewMessageView;

import com.google.gwt.dom.client.Element;
import com.google.gwtmockito.GxtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

import com.sencha.gxt.widget.core.client.WindowManager;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
@WithClassesToStub(ConfigFactory.class)
public class DesktopPresenterImplTest {

    @Mock DesktopWindowManager desktopWindowManagerMock;
    @Mock EventBus eventBusMock;
    @Mock DesktopPresenterEventHandler globalEventHandlerMock;
    @Mock KeepaliveTimer keepaliveTimerMock;
    @Mock MessagePoller messagePollerMock;
    @Mock NewMessageView.Presenter sysMsgPresenterMock;
    @Mock
    DesktopView viewMock;
    @Mock DesktopPresenterWindowEventHandler windowEventHandlerMock;
    @Mock WindowManager windowManagerMock;

    private DesktopPresenterImpl uut;
    @Before public void setUp() {
        uut = new DesktopPresenterImpl(viewMock,
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
        verify(desktopWindowManagerMock).setDesktopContainer(any(Element.class));
        uut.onAboutClick();
        verify(desktopWindowManagerMock).show(eq(WindowType.ABOUT));
        verifyNoMoreInteractions(desktopWindowManagerMock);
    }

    @Test public void testOnAnalysesWinBtnSelect() {
        verify(desktopWindowManagerMock).setDesktopContainer(any(Element.class));
        uut.onAnalysesWinBtnSelect();
        verify(desktopWindowManagerMock).show(eq(WindowType.ANALYSES));
        verifyNoMoreInteractions(desktopWindowManagerMock);
    }

    @Test public void testOnAppsWinBtnSelect() {
        verify(desktopWindowManagerMock).setDesktopContainer(any(Element.class));
        uut.onAppsWinBtnSelect();
        verify(desktopWindowManagerMock).show(eq(WindowType.APPS));
        verifyNoMoreInteractions(desktopWindowManagerMock);
    }

    @Test public void testOnDataWinBtnSelect() {
        verify(desktopWindowManagerMock).setDesktopContainer(any(Element.class));
        uut.onDataWinBtnSelect();
        verify(desktopWindowManagerMock).show(eq(WindowType.DATA));
        verifyNoMoreInteractions(desktopWindowManagerMock);
    }

    @Test public void testOnSystemMessagesClick() {
        verify(desktopWindowManagerMock).setDesktopContainer(any(Element.class));
        uut.onSystemMessagesClick();
        verify(desktopWindowManagerMock).show(WindowType.SYSTEM_MESSAGES);
        verifyNoMoreInteractions(desktopWindowManagerMock);
    }

    @Test public void testDoSeeAllNotifications() {
        uut.doSeeAllNotifications();
        verify(viewMock).hideNotificationMenu();
    }

    @Test public void testDoSeeNewNotifications() {
        uut.doSeeNewNotifications();
        verify(viewMock).hideNotificationMenu();
    }

}
