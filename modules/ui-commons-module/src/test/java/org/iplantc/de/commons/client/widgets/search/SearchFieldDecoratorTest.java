package org.iplantc.de.commons.client.widgets.search;

import org.iplantc.de.commons.client.events.SubmitTextSearchEvent;
import org.iplantc.de.commons.client.widgets.search.SearchFieldDecorator.SearchFieldDelayedTask;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.form.TextField;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class SearchFieldDecoratorTest {

    @Mock TextField textFieldMock;

    private SearchFieldDecorator<TextField> unitUnderTest;

    @Before public void setUp() {
        unitUnderTest = new SearchFieldDecorator<TextField>(textFieldMock);
    }

    @Test public void testSearchFieldDecoratorConstructor() {
        verify(textFieldMock).addKeyUpHandler(eq(unitUnderTest));
        verifyNoMoreInteractions(textFieldMock);
    }

    /**
     * Modifier key
     */
    @Test public void testOnKeyUp_Case1() {
        final SearchFieldDecorator<TextField>.SearchFieldDelayedTask delayedTaskMock = mock(SearchFieldDelayedTask.class);
        unitUnderTest = new SearchFieldDecorator<TextField>(textFieldMock, delayedTaskMock);

        KeyUpEvent mockEvent = mock(KeyUpEvent.class);
        when(mockEvent.getNativeKeyCode()).thenReturn(KeyCodes.KEY_ENTER);
        unitUnderTest.onKeyUp(mockEvent);

        verifyZeroInteractions(delayedTaskMock);
    }
    
    /**
     * arrow key
     */
    @Test public void testOnKeyUp_Case2() {
        final SearchFieldDecorator<TextField>.SearchFieldDelayedTask delayedTaskMock = mock(SearchFieldDelayedTask.class);
        unitUnderTest = new SearchFieldDecorator<TextField>(textFieldMock, delayedTaskMock);

        KeyUpEvent mockEvent = mock(KeyUpEvent.class);
        when(mockEvent.getNativeKeyCode()).thenReturn(KeyCodes.KEY_DOWN);
        unitUnderTest.onKeyUp(mockEvent);

        verifyZeroInteractions(delayedTaskMock);
    }

    /**
     * non-arrow, non-modifier
     */
    @Test public void testOnKeyUp_Case3() {
        final SearchFieldDecorator<TextField>.SearchFieldDelayedTask delayedTaskMock = mock(SearchFieldDelayedTask.class);
        unitUnderTest = new SearchFieldDecorator<TextField>(textFieldMock, delayedTaskMock);

        KeyUpEvent mockEvent = mock(KeyUpEvent.class);
        // Send 'e'
        when(mockEvent.getNativeKeyCode()).thenReturn(69);
        unitUnderTest.onKeyUp(mockEvent);

        verify(delayedTaskMock).delay(unitUnderTest.queryDelay);
    }

    /**
     * over minchars
     */
    @Test public void testSearchFieldDelayedTaskOnExecute_Case1() {
        when(textFieldMock.getText()).thenReturn("123");
        
        final HasHandlers hasHandlersMock = mock(HasHandlers.class);
        SearchFieldDecorator<TextField>.SearchFieldDelayedTask taskUnderTest = unitUnderTest.new SearchFieldDelayedTask(textFieldMock, hasHandlersMock, unitUnderTest.minChars);
        // Verify for record keeping
        verify(textFieldMock).addKeyUpHandler(any(KeyUpHandler.class));

        taskUnderTest.onExecute();
        verify(textFieldMock).getText();
        verify(hasHandlersMock).fireEvent(any(SubmitTextSearchEvent.class));

        verifyNoMoreInteractions(textFieldMock, hasHandlersMock);
    }
    
    /**
     * under minchars
     */
    @Test public void testSearchFieldDelayedTaskOnExecute_Case2() {
        when(textFieldMock.getText()).thenReturn("12");

        final HasHandlers hasHandlersMock = mock(HasHandlers.class);
        SearchFieldDecorator<TextField>.SearchFieldDelayedTask taskUnderTest = unitUnderTest.new SearchFieldDelayedTask(textFieldMock, hasHandlersMock, unitUnderTest.minChars);
        // Verify for record keeping
        verify(textFieldMock).addKeyUpHandler(any(KeyUpHandler.class));

        taskUnderTest.onExecute();
        verify(textFieldMock).getText();
        verify(hasHandlersMock, never()).fireEvent(any(GwtEvent.class));

        verifyNoMoreInteractions(textFieldMock, hasHandlersMock);
    }

}
