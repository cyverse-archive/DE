package org.iplantc.de.diskResource.client.search.views.cells;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;

import com.google.gwtmockito.GxtMockitoTestRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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

    @Test public void testDoSubmitDiskResourceQuery_Case2() {
        SubmitDiskResourceQueryEvent mockEvent = mock(SubmitDiskResourceQueryEvent.class);
        final DiskResourceQueryTemplate mockTemplate = mock(DiskResourceQueryTemplate.class);
        when(mockEvent.getQueryTemplate()).thenReturn(mockTemplate);
        when(mockTemplate.isSaved()).thenReturn(false);

        DiskResourceSearchCell spy = spy(unitUnderTest);

        spy.doSubmitDiskResourceQuery(mockEvent);
        // Verify for record keeping
        verify(spy).doSubmitDiskResourceQuery(any(SubmitDiskResourceQueryEvent.class));

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
