package org.iplantc.de.diskResource.client.search.views.cells;

import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GxtMockitoTestRunner;
import com.google.gwtmockito.fakes.FakeSimpleBeanEditorDriverProvider;

import com.sencha.gxt.widget.core.client.event.SelectEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

@RunWith(GxtMockitoTestRunner.class)
public class DiskResourceQueryFormNamePromptTest_NoEditorErrors {

    private DiskResourceQueryFormNamePrompt namePrompt;

    @Before public void setUp() {
        GwtMockito.useProviderForType(SimpleBeanEditorDriver.class, new FakeSimpleBeanEditorDriverProvider(false));
        namePrompt = new DiskResourceQueryFormNamePrompt();
    }

    /**
     * Verify the following when {@link DiskResourceQueryFormNamePrompt#cancelSaveFilterBtn} is clicked;<br/>
     */
    @Test public void testOnCancelSaveFilter_noErrors() {
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
    @Test public void testOnSaveFilterSelected_noErrors() {
        DiskResourceQueryFormNamePrompt spy = spy(namePrompt);
        spy.onSaveFilterSelected(mock(SelectEvent.class));

        // Verify that the appropriate event is fired
        verify(spy).fireEvent(any(SubmitDiskResourceQueryEvent.class));

        // Verify that the form is hidden
        verify(spy).hide();
    }

}
