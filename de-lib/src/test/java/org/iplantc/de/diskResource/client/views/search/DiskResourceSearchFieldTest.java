package org.iplantc.de.diskResource.client.views.search;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.commons.client.events.SubmitTextSearchEvent;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.views.search.cells.DiskResourceSearchCell;
import org.iplantc.de.diskResource.client.views.search.DiskResourceSearchField;

import com.google.gwtmockito.GxtMockitoTestRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class DiskResourceSearchFieldTest {

    @Mock SubmitDiskResourceQueryEvent submitQueryEventMock;
    @Mock DiskResourceSearchCell searchCellMock;

    @Test public void testDoSubmitDiskResourceQuery_FileQuery() {
        DiskResourceSearchField spy = spy(new DiskResourceSearchField(searchCellMock));

        DiskResourceQueryTemplate queryMock = mock(DiskResourceQueryTemplate.class);
        String testFileQuery = "example";

        when(submitQueryEventMock.getQueryTemplate()).thenReturn(queryMock);
        when(queryMock.getFileQuery()).thenReturn(testFileQuery);

        // Call method under test
        spy.doSubmitDiskResourceQuery(submitQueryEventMock);

        verify(spy).clear();
        assertEquals(null, spy.getValue());
    }

    @Test public void testDoSubmitDiskResourceQuery_NoFileQuery() {
        DiskResourceSearchField spy = spy(new DiskResourceSearchField(searchCellMock));

        // Call method under test
        spy.doSubmitDiskResourceQuery(submitQueryEventMock);

        verify(spy).clear();
    }

    @Test public void testOnSubmitTextSearch() {
        DiskResourceSearchField spy = spy(new DiskResourceSearchField(searchCellMock));

        // Call method under test
        spy.onSubmitTextSearch(any(SubmitTextSearchEvent.class));
        
        verify(spy).finishEditing();
    }

}
