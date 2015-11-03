package org.iplantc.de.desktop.client.views.widgets;

import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.Header;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent;
import com.sencha.gxt.widget.core.client.event.MinimizeEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class TaskButtonTest {

    @Mock Window windowMock;
    @Mock TaskButtonCell.TaskButtonCellAppearance<Boolean> cellAppearanceMock;
    @Mock Header headerMock;
    @Mock TaskButtonCell cellMock;

    @Test public void buttonIsDepressedWhenWindowIsMinimized() {
        when(cellMock.getAppearance()).thenReturn(cellAppearanceMock);
        when(cellAppearanceMock.getMaxTextLength()).thenReturn(50);
        when(headerMock.getText()).thenReturn("Mock window");
        when(windowMock.getHeader()).thenReturn(headerMock);

        TaskButton uut = spy(new TaskButton(windowMock, cellMock));
        uut.onMinimize(mock(MinimizeEvent.class));

        verify(uut).setValue(eq(true), eq(false));
    }

    @Test public void buttonIsNotDepressedWhenWindowIsMaximized() {
        when(cellMock.getAppearance()).thenReturn(cellAppearanceMock);
        when(cellAppearanceMock.getMaxTextLength()).thenReturn(50);
        when(headerMock.getText()).thenReturn("Mock window");
        when(windowMock.getHeader()).thenReturn(headerMock);

        TaskButton uut = spy(new TaskButton(windowMock, cellMock));
        uut.onMaximize(mock(MaximizeEvent.class));

        verify(uut).setValue(eq(false), eq(false));
    }

    @Test public void buttonIsNotDepressedWhenWindowIsShown() {
         when(cellMock.getAppearance()).thenReturn(cellAppearanceMock);
        when(cellAppearanceMock.getMaxTextLength()).thenReturn(50);
        when(headerMock.getText()).thenReturn("Mock window");
        when(windowMock.getHeader()).thenReturn(headerMock);

        TaskButton uut = spy(new TaskButton(windowMock, cellMock));
        uut.onShow(mock(ShowEvent.class));

        verify(uut).setValue(eq(false), eq(false));
    }

}