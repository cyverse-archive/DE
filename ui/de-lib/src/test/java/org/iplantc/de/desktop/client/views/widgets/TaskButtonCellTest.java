package org.iplantc.de.desktop.client.views.widgets;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.WindowManager;

import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class TaskButtonCellTest {

    @Mock TaskButtonCell.TaskButtonCellAppearance<Boolean> mockAppearance;
    @Mock Window mockWindow;
    @Mock WindowManager mockWindowManager;
    @Mock ValueUpdater<Boolean> mockValueUpdater;
    private TaskButtonCell uut;

    @Before public void setup() {
        uut = new TaskButtonCell(mockAppearance, mockWindow, mockWindowManager);
    }
    @Test public void taskButtonShowsNonVisibleWindowOnSelect() {
        when(mockWindow.isVisible()).thenReturn(false);
        uut.onClick(mock(Cell.Context.class), mock(XElement.class), false, mock(NativeEvent.class), mockValueUpdater);
        verify(mockWindow).isVisible();
        verify(mockWindow).show();
        verifyNoMoreInteractions(mockWindow);
    }

    @Test public void taskButtonMinimizesWindowWhenActiveOnSelect() {
        when(mockWindow.isVisible()).thenReturn(true);
        when(mockWindowManager.getActive()).thenReturn(mockWindow);
        uut.onClick(mock(Cell.Context.class), mock(XElement.class), false, mock(NativeEvent.class), mockValueUpdater);
        verify(mockWindow).isVisible();
        verify(mockWindow).minimize();
        verifyNoMoreInteractions(mockWindow);
    }

    @Test public void taskButtonToFrontsNonActiveVisibleWindowOnSelect() {
        when(mockWindow.isVisible()).thenReturn(true);
        when(mockWindowManager.getActive()).thenReturn(mock(Window.class));

        // Create spy
        TaskButtonCell spy = spy(uut);
        spy.onClick(mock(Cell.Context.class), mock(XElement.class), false, mock(NativeEvent.class), mockValueUpdater);
        verify(mockWindow).isVisible();
        verify(mockWindow).toFront();
        /* Verify that the button state is set to true. This is to prevent the button
           from looking like it is depressed */
        verify(spy).callSuperOnClick(any(Cell.Context.class),
                                     any(XElement.class),
                                     any(NativeEvent.class),
                                     any(ValueUpdater.class),
                                     eq(true));
        verifyNoMoreInteractions(mockWindow);
    }
}