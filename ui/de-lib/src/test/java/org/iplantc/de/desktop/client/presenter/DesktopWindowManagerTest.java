package org.iplantc.de.desktop.client.presenter;

import org.iplantc.de.client.models.WindowType;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.desktop.client.views.windows.IplantWindowBase;
import org.iplantc.de.desktop.client.views.windows.util.WindowFactory;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.WindowManager;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.List;
import java.util.Stack;

@RunWith(GxtMockitoTestRunner.class)
public class DesktopWindowManagerTest {

    @Mock WindowManager windowManagerMock;
    @Mock WindowFactory windowFactoryMock;

    private DesktopWindowManager uut;

    @Before public void setup() {
        uut = new DesktopWindowManager(windowManagerMock, windowFactoryMock);
    }

    @Ignore("Test is not longer valid with code-splitting in the method. REFACTOR NEEDED.")
    @Test public void windowBroughtToFrontWhenShown_show_config() {
        final Window mockWindow = mock(Window.class);
        when(mockWindow.isVisible()).thenReturn(true);
//        uut = new DesktopWindowManager(windowManagerMock, windowFactoryMock) {
//            @Override
//            Window getOrCreateWindow(WindowConfig config) {
//                return mockWindow;
//            }
//        };
        WindowConfig wc = mock(WindowConfig.class);
        uut.show(wc, false);
        verify(mockWindow).show();
        verify(windowManagerMock).bringToFront(eq(mockWindow));
    }


    @Ignore("Test is not longer valid with code-splitting in the method. REFACTOR NEEDED.")
    @Test public void showExistingWindowShowsExistingWindow() {
        final Window oldWindowMock = mock(Window.class);
        final WindowConfig newConfigMock = mock(WindowConfig.class);
        final IplantWindowBase newWindowMock = mock(IplantWindowBase.class);
        final List<Widget> existingWindowList = Lists.newArrayList();
        existingWindowList.add(oldWindowMock);

        when(oldWindowMock.getStateId()).thenReturn("firstId");
        when(newConfigMock.getWindowType()).thenReturn(WindowType.ABOUT);
        when(newConfigMock.getTag()).thenReturn("testTag");
        when(windowManagerMock.getWindows()).thenReturn(existingWindowList);
//        when(windowFactoryMock.build(newConfigMock)).thenReturn(newWindowMock);

        uut.show(newConfigMock, false);
        verify(newWindowMock).show();
        verify(windowManagerMock).bringToFront(eq(newWindowMock));
    }

    /**
     * When there are multiple open windows of the same {@code WindowType}, verify that when
     * multiple calls are made to {@link DesktopWindowManager#show(WindowType)} with the same
     * {@code WindowType}, that each of those windows is shown in reverse visible stack order.
     *
     * The visible stack is a record which is kept of all open iPlant windows in order of when they
     * were last focused.
     */
    @Ignore("Test is not longer valid with code-splitting in the method. REFACTOR NEEDED.")
    @Test public void multipleSimilarWindowTypesCycleToFront_show_type() {
        // Set up current window stack
        Window window1 = mock(Window.class);
        Window window2 = mock(Window.class);
        Window window3 = mock(Window.class);
        Window window4 = mock(Window.class);

        // Set all visible
        when(window1.isVisible()).thenReturn(true);
        when(window2.isVisible()).thenReturn(true);
        when(window3.isVisible()).thenReturn(true);
        when(window4.isVisible()).thenReturn(true);
        // Set state ids
        when(window1.getStateId()).thenReturn(WindowType.ABOUT.toString());
        when(window2.getStateId()).thenReturn(WindowType.DATA.toString() + "1");
        when(window3.getStateId()).thenReturn(WindowType.DATA.toString() + "2");
        when(window4.getStateId()).thenReturn(WindowType.ANALYSES.toString());

        /* When data window is focused, next data window will be shown */
        final Stack<Widget> windowStack = new Stack<>();
        windowStack.push(window2);
        windowStack.push(window3);

        when(windowManagerMock.getStack()).thenReturn(windowStack);
        uut.show(WindowType.DATA);
        verify(windowManagerMock).bringToFront(eq(window2));

        windowStack.clear();
        windowStack.push(window1);
        windowStack.push(window4);
        windowStack.push(window2);
        windowStack.push(window3);

        uut.show(WindowType.DATA);
        verify(windowManagerMock, times(2)).bringToFront(eq(window2));

        /* When data window is NOT focused, data window highest in stack is shown */
        windowStack.clear();
        windowStack.push(window1);
        windowStack.push(window2);
        windowStack.push(window3);
        windowStack.push(window4);

        uut.show(WindowType.DATA);
        verify(windowManagerMock).bringToFront(eq(window3));

    }

    @Test public void closeActiveWindowOnlyClosesActiveWindow() {
        // Set up current window stack
        Window window1 = mock(Window.class);
        Window window2 = mock(Window.class);
        Window window3 = mock(Window.class);
        Window window4 = mock(Window.class);

        final Stack<Widget> windowStack = new Stack<>();
        windowStack.push(window1);
        windowStack.push(window2);
        windowStack.push(window3);
        windowStack.push(window4);

        when(windowManagerMock.getStack()).thenReturn(windowStack);

        uut.closeActiveWindow();
        verify(windowManagerMock).getStack();
        verify(window1, never()).hide();
        verify(window2, never()).hide();
        verify(window3, never()).hide();
        verify(window4).hide();

        verifyNoMoreInteractions(windowManagerMock);

    }

}
