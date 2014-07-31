package org.iplantc.de.client.newDesktop.views;

import org.iplantc.de.client.newDesktop.views.widgets.TaskBar;
import org.iplantc.de.client.newDesktop.views.widgets.TaskButton;
import org.iplantc.de.client.views.windows.IPlantWindowInterface;
import org.iplantc.de.resources.client.messages.IplantNewUserTourStrings;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.WindowManager;
import com.sencha.gxt.widget.core.client.event.RegisterEvent;
import com.sencha.gxt.widget.core.client.event.UnregisterEvent;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.List;

@RunWith(GxtMockitoTestRunner.class)
public class NewDesktopViewImplTest {

    @Mock
    IplantNewUserTourStrings tourStringsMock;
    @Mock
    WindowManager windowManagerMock;
    @Mock
    RegisterEvent<Widget> registerEventMock;
    @Mock
    UnregisterEvent<Widget> unregisterEventMock;

    @Test public void viewAddsTaskButtonWhenWindowIsRegistered() {
        NewDesktopViewImpl uut = new NewDesktopViewImpl(tourStringsMock, windowManagerMock);
        uut.taskBar = mock(TaskBar.class);

        TaskButton mockTaskButton = mock(TaskButton.class);
        List<TaskButton> tbList = Lists.newArrayList(mockTaskButton);
        when(uut.taskBar.getButtons()).thenReturn(tbList);

        final Window window = mock(Window.class);
        when(registerEventMock.getItem()).thenReturn(window);
        uut.onRegister(registerEventMock);
        verify(uut.taskBar).addTaskButton(eq(window));
    }

    @Test public void viewDoesNotAddNewTaskButtonsForExistingWindows() {
        NewDesktopViewImpl uut = new NewDesktopViewImpl(tourStringsMock, windowManagerMock);
        uut.taskBar = mock(TaskBar.class);

        TaskButton mockTaskButton = mock(TaskButton.class);
        List<TaskButton> tbList = Lists.newArrayList(mockTaskButton);
        when(uut.taskBar.getButtons()).thenReturn(tbList);

        final Window window = mock(Window.class);
        when(registerEventMock.getItem()).thenReturn(window);
        when(mockTaskButton.getWindow()).thenReturn(window);
        uut.onRegister(registerEventMock);
        verify(uut.taskBar, never()).addTaskButton(eq(window));
    }

    @Test public void viewRemovesTaskButtonWhenWindowIsUnregisteredAndNotMinimized() {
        NewDesktopViewImpl uut = new NewDesktopViewImpl(tourStringsMock, windowManagerMock);
        uut.taskBar = mock(TaskBar.class);

        TaskButton mockTaskButton = mock(TaskButton.class);
        List<TaskButton> tbList = Lists.newArrayList(mockTaskButton);
        when(uut.taskBar.getButtons()).thenReturn(tbList);

        final Window window = mock(Window.class, withSettings().extraInterfaces(IPlantWindowInterface.class));
        when(unregisterEventMock.getItem()).thenReturn(window);
        when(mockTaskButton.getWindow()).thenReturn(window);
        uut.onUnregister(unregisterEventMock);
        verify(mockTaskButton).getWindow();
        verify(uut.taskBar).removeTaskButton(eq(mockTaskButton));
        verifyNoMoreInteractions(mockTaskButton);
    }

    @Test public void viewRemovesTaskButtonWhenWindowIsUnregisteredAndMinimized() {
        NewDesktopViewImpl uut = new NewDesktopViewImpl(tourStringsMock, windowManagerMock);
        uut.taskBar = mock(TaskBar.class);

        TaskButton mockTaskButton = mock(TaskButton.class);
        List<TaskButton> tbList = Lists.newArrayList(mockTaskButton);
        when(uut.taskBar.getButtons()).thenReturn(tbList);

        final Window window = mock(Window.class, withSettings().extraInterfaces(IPlantWindowInterface.class));
        when(((IPlantWindowInterface)window).isMinimized()).thenReturn(true);
        when(unregisterEventMock.getItem()).thenReturn(window);
        when(mockTaskButton.getWindow()).thenReturn(window);
        uut.onUnregister(unregisterEventMock);
        verify(mockTaskButton).getWindow();
        verify(uut.taskBar, never()).removeTaskButton(any(TaskButton.class));
        verifyNoMoreInteractions(mockTaskButton);
    }
}