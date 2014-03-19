package org.iplantc.de.diskResource.client.search.views.cells;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;

import com.google.gwtmockito.GxtMockitoTestRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

/**
 * @author jstroot
 * 
 */
@RunWith(GxtMockitoTestRunner.class)
public class DiskResourceSearchCellTest {

    private DiskResourceSearchCell unitUnderTest;

    @Before public void setUp() {
        unitUnderTest = new DiskResourceSearchCell();

    }

    @Test public void testDoSubmitDiskResourceQuery_Case1() {
        SubmitDiskResourceQueryEvent mockEvent = mock(SubmitDiskResourceQueryEvent.class);
        final DiskResourceQueryTemplate mockTemplate = mock(DiskResourceQueryTemplate.class);
        final String expectedName = "original name";
        when(mockEvent.getQueryTemplate()).thenReturn(mockTemplate);
        when(mockTemplate.isSaved()).thenReturn(true);
        when(mockTemplate.getName()).thenReturn(expectedName);

        DiskResourceSearchCell spy = spy(unitUnderTest);

        spy.doSubmitDiskResourceQuery(mockEvent);
        // Verify for record keeping
        verify(spy).doSubmitDiskResourceQuery(any(SubmitDiskResourceQueryEvent.class));

        verify(mockEvent).getQueryTemplate();
        verify(mockTemplate).isSaved();
        verify(mockTemplate).getName();
        ArgumentCaptor<SaveDiskResourceQueryEvent> sdrqCaptor = ArgumentCaptor.forClass(SaveDiskResourceQueryEvent.class);
        verify(spy).fireEvent(sdrqCaptor.capture());

        assertEquals("Verify the query template passed to the Save event", mockTemplate, sdrqCaptor.getValue().getQueryTemplate());
        assertEquals("Verify the original name passed to the Save event", expectedName, sdrqCaptor.getValue().getOriginalName());

        verifyNoMoreInteractions(spy, mockEvent, mockTemplate);
    }

    @Test public void testDoSubmitDiskResourceQuery_Case2() {
        SubmitDiskResourceQueryEvent mockEvent = mock(SubmitDiskResourceQueryEvent.class);
        final DiskResourceQueryTemplate mockTemplate = mock(DiskResourceQueryTemplate.class);
        when(mockEvent.getQueryTemplate()).thenReturn(mockTemplate);
        when(mockTemplate.isSaved()).thenReturn(false);

        DiskResourceSearchCell spy = spy(unitUnderTest);

        spy.doSubmitDiskResourceQuery(mockEvent);
        // Verify for record keeping
        verify(spy).doSubmitDiskResourceQuery(any(SubmitDiskResourceQueryEvent.class));

        verify(mockEvent).getQueryTemplate();
        verify(mockTemplate).isSaved();
        verify(spy).fireEvent(eq(mockEvent));

        verifyNoMoreInteractions(spy, mockEvent, mockTemplate);
    }

    @Test public void testDoSaveDiskResourceQueryTemplate_Case1() {
        SaveDiskResourceQueryEvent mockEvent = mock(SaveDiskResourceQueryEvent.class);

        DiskResourceSearchCell spy = spy(unitUnderTest);

        spy.doSaveDiskResourceQueryTemplate(mockEvent);
        // Verify for record keeping
        verify(spy).doSaveDiskResourceQueryTemplate(any(SaveDiskResourceQueryEvent.class));

        verify(spy).fireEvent(eq(mockEvent));
        verifyNoMoreInteractions(spy, mockEvent);
    }

}
