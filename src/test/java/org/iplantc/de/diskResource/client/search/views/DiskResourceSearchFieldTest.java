package org.iplantc.de.diskResource.client.search.views;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.commons.client.events.SubmitTextSearchEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;

import com.google.gwtmockito.GxtMockitoTestRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class DiskResourceSearchFieldTest {

    @Mock
    SubmitDiskResourceQueryEvent submitQueryEventMock;

    @Test
    public void testDoSubmitDiskResourceQuery_FileQuery() {
        DiskResourceSearchField spy = spy(new DiskResourceSearchField());

        DiskResourceQueryTemplate queryMock = mock(DiskResourceQueryTemplate.class);
        String testFileQuery = "example";

        when(submitQueryEventMock.getQueryTemplate()).thenReturn(queryMock);
        when(queryMock.getFileQuery()).thenReturn(testFileQuery);

        // Call method under test
        spy.doSubmitDiskResourceQuery(submitQueryEventMock);

        ArgumentCaptor<String> query = ArgumentCaptor.forClass(String.class);
        verify(spy).setText(query.capture());
        assertEquals("Expected FileQuery text set as DiskResourceSearchField text", testFileQuery,
                query.getValue());
    }

    @Test
    public void testDoSubmitDiskResourceQuery_NoFileQuery() {
        DiskResourceSearchField spy = spy(new DiskResourceSearchField());

        // Call method under test
        spy.doSubmitDiskResourceQuery(submitQueryEventMock);

        verify(spy).clear();
    }

    @Test public void testOnSubmitTextSearch() {
        DiskResourceSearchField spy = spy(new DiskResourceSearchField());

        // Call method under test
        spy.onSubmitTextSearch(any(SubmitTextSearchEvent.class));
        
        verify(spy).finishEditing();
    }

}
