package org.iplantc.de.diskResource.client.search.views.cells;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GxtMockitoTestRunner;
import com.google.gwtmockito.fakes.FakeSimpleBeanEditorDriverProvider;

import com.sencha.gxt.widget.core.client.event.SelectEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

@RunWith(GxtMockitoTestRunner.class)
public class DiskResourceQueryFormNamePromptTest_WithEditorErrors {


    private DiskResourceQueryFormNamePrompt namePrompt;

    @Before public void setUp() {
        GwtMockito.useProviderForType(SimpleBeanEditorDriver.class, new FakeSimpleBeanEditorDriverProvider(true));
        namePrompt = new DiskResourceQueryFormNamePrompt();
    }

    /**
     * Verify the following when {@link DiskResourceQueryFormNamePrompt#cancelSaveFilterBtn} is clicked;<br/>
     */
    @Test public void testOnCancelSaveFilter_withErrors() {
        final String originalName = "originalName";
        namePrompt.originalName = originalName;
        DiskResourceQueryFormNamePrompt spy = spy(namePrompt);

        spy.onCancelSaveFilter(mock(SelectEvent.class));

        final ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(spy.name).setValue(stringCaptor.capture());
        assertEquals("Verify that the name field is reset", originalName, stringCaptor.getValue());

        // Verify that the form is hidden
        verify(spy).hide();
    }

    /**
     * Verify the following when {@link DiskResourceQueryFormNamePrompt#saveFilterBtn} is clicked;<br/>
     */
    @Test public void testOnSaveFilterSelected_withErrors() {
        DiskResourceQueryFormNamePrompt spy = spy(namePrompt);
        spy.onSaveFilterSelected(mock(SelectEvent.class));

        // Verify that no events are fired
        verify(spy, never()).fireEvent(any(GwtEvent.class));

        // Verify that the form is not hidden
        verify(spy, never()).hide();
    }


}
