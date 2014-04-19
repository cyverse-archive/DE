package org.iplantc.de.diskResource.client.search.presenter.impl;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.events.FolderSelectedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectedEvent.FolderSelectedEventHandler;
import org.iplantc.de.diskResource.client.events.FolderSelectedEvent.HasFolderSelectedEventHandlers;
import org.iplantc.de.diskResource.client.search.events.DeleteSavedSearchEvent;
import org.iplantc.de.diskResource.client.search.events.DeleteSavedSearchEvent.HasDeleteSavedSearchEventHandlers;
import org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent.SaveDiskResourceQueryEventHandler;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler;
import org.iplantc.de.diskResource.client.search.events.UpdateSavedSearchesEvent;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.TreeStore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author jstroot
 * 
 */
@SuppressWarnings("nls")
@RunWith(GxtMockitoTestRunner.class)
public class DataSearchPresenterImplTest {

    @Mock DiskResourceSearchField viewMock;
    @Mock EventBus mockEventBus;
    @Mock SearchServiceFacade searchService;
    @Mock IplantAnnouncer announcer;

    @Captor ArgumentCaptor<List<DiskResourceQueryTemplate>> drqtListCaptor;
    @Captor ArgumentCaptor<AsyncCallback<List<DiskResourceQueryTemplate>>> stringAsyncCbCaptor;
    @Captor ArgumentCaptor<AsyncCallback<List<DiskResourceQueryTemplate>>> drqtListAsyncCaptor;

    private DataSearchPresenterImpl dsPresenter;

    @Before public void setUp() {
        dsPresenter = new DataSearchPresenterImpl(searchService, announcer, mockEventBus);
        dsPresenter.searchField = viewMock;
    }

    /**
     * Verifies that nothing will occur if a query template's name is null
     * 
     * @see org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter#doSaveDiskResourceQueryTemplate(org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent)
     */
    @Test public void testDoSaveDiskResourceQueryTemplate_Case1() {
        DataSearchPresenterImpl spy = spy(dsPresenter);
        SaveDiskResourceQueryEvent mockEvent = mock(SaveDiskResourceQueryEvent.class);
        DiskResourceQueryTemplate mockTemplate = mock(DiskResourceQueryTemplate.class);
        // This mock will return null for a call to getName() by default
        when(mockEvent.getQueryTemplate()).thenReturn(mockTemplate);

        spy.doSaveDiskResourceQueryTemplate(mockEvent);
        // Perform verify for record keeping
        verify(spy).doSaveDiskResourceQueryTemplate(eq(mockEvent));

        verifyZeroInteractions(searchService);

        verifyNoMoreInteractions(spy);
    }

    /**
     * Verifies that nothing will occur if a query template's name is an empty string.
     * 
     * @see org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter#doSaveDiskResourceQueryTemplate(org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent)
     */
    @Test public void testDoSaveDiskResourceQueryTemplate_Case2() {
        DataSearchPresenterImpl spy = spy(dsPresenter);
        SaveDiskResourceQueryEvent mockEvent = mock(SaveDiskResourceQueryEvent.class);
        DiskResourceQueryTemplate mockTemplate = mock(DiskResourceQueryTemplate.class);
        when(mockEvent.getQueryTemplate()).thenReturn(mockTemplate);

        when(mockTemplate.getName()).thenReturn("");
        when(mockEvent.getQueryTemplate()).thenReturn(mockTemplate);

        spy.doSaveDiskResourceQueryTemplate(mockEvent);
        // Perform verify for record keeping
        verify(spy).doSaveDiskResourceQueryTemplate(eq(mockEvent));

        verifyZeroInteractions(searchService);

        verifyNoMoreInteractions(spy);
    }

    /**
     * Verifies that an existing query template will be replaced when a request to save a template of the
     * same name is received.
     * 
     * @see org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter#doSaveDiskResourceQueryTemplate(org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent)
     */
    @Test public void testDoSaveDiskResourceQueryTemplate_Case3() {
        DataSearchPresenterImpl spy = spy(dsPresenter);
        SaveDiskResourceQueryEvent mockEvent = mock(SaveDiskResourceQueryEvent.class);

        /* ================ Save first template =================== */
        DiskResourceQueryTemplate mockTemplate = mock(DiskResourceQueryTemplate.class);
        when(mockTemplate.getName()).thenReturn("firstMock");
        when(mockEvent.getQueryTemplate()).thenReturn(mockTemplate);

        // Call method under test
        spy.doSaveDiskResourceQueryTemplate(mockEvent);

        /* Verify that the service was called to save the template, and only 1 template was saved */
        verify(searchService).saveQueryTemplates(drqtListCaptor.capture(), drqtListAsyncCaptor.capture());
        assertEquals("list passed to search service is expected size", 1, drqtListCaptor.getValue().size());
        assertTrue("list passed to search service contains the template to be saved", drqtListCaptor.getValue().contains(mockTemplate));
        // Mock expected behavior from service success
        drqtListAsyncCaptor.getValue().onSuccess(drqtListCaptor.getValue());


        /* ================ Save second template =================== */
        DiskResourceQueryTemplate mockTemplate_2 = mock(DiskResourceQueryTemplate.class);
        when(mockTemplate_2.getName()).thenReturn("secondMock");
        when(mockEvent.getQueryTemplate()).thenReturn(mockTemplate_2);

        // Call method under test
        spy.doSaveDiskResourceQueryTemplate(mockEvent);


        /* Verify that the service was called to save the template, and only 2 templates were saved */
        verify(searchService, times(2)).saveQueryTemplates(drqtListCaptor.capture(), drqtListAsyncCaptor.capture());
        assertEquals("list passed to search service is expected size", 2, drqtListCaptor.getValue().size());
        assertTrue("list passed to search service contains previously saved template", drqtListCaptor.getValue().contains(mockTemplate));
        assertTrue("list passed to search service contains the template to be saved", drqtListCaptor.getValue().contains(mockTemplate_2));
        // Mock expected behavior from service success
        drqtListAsyncCaptor.getValue().onSuccess(drqtListCaptor.getValue());


        /* ================ Save third template =================== */
        DiskResourceQueryTemplate mockTemplate_3 = mock(DiskResourceQueryTemplate.class);
        when(mockTemplate_3.getName()).thenReturn("firstMock");
        when(mockEvent.getQueryTemplate()).thenReturn(mockTemplate_3);
        spy.doSaveDiskResourceQueryTemplate(mockEvent);


        /* Verify that the service was called to save the template, and only 2 templates were saved but one of them was
         * replaced.
         */
        verify(searchService, times(3)).saveQueryTemplates(drqtListCaptor.capture(), drqtListAsyncCaptor.capture());
        assertEquals("list passed to search service is expected size", 2, drqtListCaptor.getValue().size());
        assertTrue("list passed to search service contains previously saved template", drqtListCaptor.getValue().contains(mockTemplate_2));
        assertTrue("list passed to search service contains previously saved template", drqtListCaptor.getValue().contains(mockTemplate_3));
        assertFalse("list passed to search service contains the template to be saved", drqtListCaptor.getValue().contains(mockTemplate));
    }

    /**
     * Verifies that a search of a given query will be requested after it is successfully persisted.
     * 
     * @see org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter#doSaveDiskResourceQueryTemplate(org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent)
     */
    @Test public void testDoSaveDiskResourceQueryTemplateOnSuccess_Case1() {
        DataSearchPresenterImpl spy = spy(dsPresenter);
        SaveDiskResourceQueryEvent mockEvent = mock(SaveDiskResourceQueryEvent.class);
        DiskResourceQueryTemplate mockTemplate = mock(DiskResourceQueryTemplate.class);
        when(mockTemplate.getName()).thenReturn("mock1");
        when(mockEvent.getQueryTemplate()).thenReturn(mockTemplate);

        spy.doSaveDiskResourceQueryTemplate(mockEvent);
        verify(searchService).saveQueryTemplates(drqtListCaptor.capture(), drqtListAsyncCaptor.capture());

        assertEquals("Verify that the query has not been added to the presenter's list", 0, spy.getQueryTemplates().size());

        // Force service success
        drqtListAsyncCaptor.getValue().onSuccess(drqtListCaptor.getValue());

        ArgumentCaptor<SubmitDiskResourceQueryEvent> submitEventCaptor = ArgumentCaptor.forClass(SubmitDiskResourceQueryEvent.class);
        verify(spy).doSubmitDiskResourceQuery(submitEventCaptor.capture());
        assertEquals("Verify that a search is requested after a successful persist. ", mockTemplate, submitEventCaptor.getValue().getQueryTemplate());

        verify(searchService).createFrozenList(drqtListCaptor.capture());
        assertEquals("Verify that list passed to createFrozenList is expected size", 1, drqtListCaptor.getValue().size());
        assertEquals("Verify that list passed to createFrozenList contains intended template", mockTemplate, drqtListCaptor.getValue().get(0));

        verify(spy).setCleanCopyQueryTemplates(anyListOf(DiskResourceQueryTemplate.class));

        assertEquals("Verify that the query has been added to the presenter's list after successful persist", 1, spy.queryTemplates.size());
        verifyNoMoreInteractions(searchService);
    }

    /**
     * Verifies that a query will be removed from the treestore when a name change is detected.
     */
    @Test public void testDoSaveDiskResourceQueryTemplateOnSuccess_Case2() {
        final String originalName = "originalMockName";
        DataSearchPresenterImpl spy = spy(dsPresenter);
        SaveDiskResourceQueryEvent mockEvent = mock(SaveDiskResourceQueryEvent.class);
        DiskResourceQueryTemplate mockTemplate = mock(DiskResourceQueryTemplate.class);
        DiskResourceQueryTemplate cleanMockTemplate = mock(DiskResourceQueryTemplate.class);

        when(mockTemplate.getName()).thenReturn("mock1");
        when(cleanMockTemplate.getName()).thenReturn(originalName);
        when(mockEvent.getQueryTemplate()).thenReturn(mockTemplate);
        when(mockEvent.getOriginalName()).thenReturn(originalName);

        spy.cleanCopyQueryTemplates = Lists.newArrayList(cleanMockTemplate);
        spy.doSaveDiskResourceQueryTemplate(mockEvent);
        verify(searchService).saveQueryTemplates(drqtListCaptor.capture(), drqtListAsyncCaptor.capture());
        
        // Force service success
        drqtListAsyncCaptor.getValue().onSuccess(Collections.<DiskResourceQueryTemplate> emptyList());

        verify(spy).updateDataNavigationWindow(eq(Lists.<DiskResourceQueryTemplate> newArrayList()),
                drqtListCaptor.capture());
        verify(mockEventBus).fireEvent(any(UpdateSavedSearchesEvent.class));
        assertEquals("Verify that the event's remove list only contains one item", 1, drqtListCaptor
                .getValue().size());
        assertEquals("Verify that the intended template was passed to event's remove list",
                cleanMockTemplate, drqtListCaptor.getValue().get(0));
    }
    
    /**
     * Verifies that a search will not be requested after a failure to persist a query.
     *
     * @see org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter#doSaveDiskResourceQueryTemplate(org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent)
     */
    @Test public void testDoSaveDiskResourceQueryTemplate_Case5() {
        DataSearchPresenterImpl spy = spy(dsPresenter);
        SaveDiskResourceQueryEvent mockEvent = mock(SaveDiskResourceQueryEvent.class);
        DiskResourceQueryTemplate mockTemplate = mock(DiskResourceQueryTemplate.class);
        when(mockTemplate.getName()).thenReturn("mock1");
        when(mockEvent.getQueryTemplate()).thenReturn(mockTemplate);

        spy.doSaveDiskResourceQueryTemplate(mockEvent);
        // Perform verify for record keeping
        verify(spy).doSaveDiskResourceQueryTemplate(eq(mockEvent));
        verify(searchService).saveQueryTemplates(drqtListCaptor.capture(), drqtListAsyncCaptor.capture());

        assertEquals("Verify that the query has not been added to the presenter's list", 0, spy.getQueryTemplates().size());

        // Force service failure
        drqtListAsyncCaptor.getValue().onFailure(null);

        /* Verify that a search is not requested after failure to persist */
        verify(spy, never()).doSubmitDiskResourceQuery(any(SubmitDiskResourceQueryEvent.class));
        verifyNoMoreInteractions(searchService);

        assertEquals("Verify that the query has not been added to the presenter's list after failed persist", 0, spy.getQueryTemplates().size());
    }

    /**
     * Verifies the list passed to the updateNavigationWindow method when the given query template is not
     * new, and has been changed.
     * 
     * The list should be equivalent to what was returned by getQueryTemplates() prior to the method
     * call, but with the changed item in place of the non-changed item.
     * 
     * @see org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter#doSaveDiskResourceQueryTemplate(org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent)
     */
    @Test
    public void testDoSaveDiskResourceQueryTemplate_Case6() {
        // Create presenter with overridden method to control test execution.
        dsPresenter = new DataSearchPresenterImpl(searchService, announcer, mockEventBus) {
            @Override
            boolean areTemplatesEqual(DiskResourceQueryTemplate lhs, DiskResourceQueryTemplate rhs) {
                if (lhs.getName().equals("qtMockId1") && rhs.getName().equals("qtMockId1")) {
                    return false;
                }
                return true;
            }
        };
        dsPresenter.searchField = viewMock;
        DataSearchPresenterImpl spy = spy(dsPresenter);
        DiskResourceQueryTemplate eventMockTemplate = mock(DiskResourceQueryTemplate.class);
        // Return empty string to indicate that the template is new
        when(eventMockTemplate.getName()).thenReturn("qtMockId1");
        SaveDiskResourceQueryEvent mockEvent = mock(SaveDiskResourceQueryEvent.class);
        when(mockEvent.getQueryTemplate()).thenReturn(eventMockTemplate);

        // Add an existing mock to the classes template list
        DiskResourceQueryTemplate existingMock1 = mock(DiskResourceQueryTemplate.class);
        DiskResourceQueryTemplate existingMock2 = mock(DiskResourceQueryTemplate.class);
        when(existingMock1.getName()).thenReturn("qtMockId1");
        when(existingMock2.getName()).thenReturn("qtMockId2");
        spy.getQueryTemplates().clear();
        ArrayList<DiskResourceQueryTemplate> existingTemplates = Lists.newArrayList(existingMock1,
                existingMock2);
        spy.getQueryTemplates().addAll(existingTemplates);
        int initialSize = spy.getQueryTemplates().size();

        spy.setCleanCopyQueryTemplates(existingTemplates);

        // Call method under test
        spy.doSaveDiskResourceQueryTemplate(mockEvent);
        verify(searchService)
                .saveQueryTemplates(drqtListCaptor.capture(), drqtListAsyncCaptor.capture());

        // Force service success
        when(searchService.createFrozenList(anyListOf(DiskResourceQueryTemplate.class))).thenReturn(
                drqtListCaptor.getValue());
        drqtListAsyncCaptor.getValue().onSuccess(drqtListCaptor.getValue());

        /* Verify that the setDirty flag of the changed template has been set */
        verify(eventMockTemplate).setDirty(eq(true));

        /* Verify that the given list only contains the existing and event mock templates */
        verify(spy).updateDataNavigationWindow(drqtListCaptor.capture(),
                anyListOf(DiskResourceQueryTemplate.class));
        assertEquals("verify that the size of the list has not changed", initialSize, drqtListCaptor
                .getValue().size());
        assertTrue("verify that the list contains the updated query template", drqtListCaptor.getValue()
                .contains(eventMockTemplate));
        assertTrue("verify that the list contains the unmodified starting original template",
                drqtListCaptor.getValue().contains(existingMock2));
        assertFalse("verify that the list does not contain the modified starting original template",
                drqtListCaptor.getValue().contains(existingMock1));
    }

    /**
     * Verifies that the item to be submitted will be set as the active query, the given template will be
     * fired in a {@link FolderSelectedEvent}, and the
     * {@link DataSearchPresenterImpl#updateDataNavigationWindow(List, TreeStore)} method will be called.
     * 
     * @see org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter#doSubmitDiskResourceQuery(SubmitDiskResourceQueryEvent)
     */
    @Test public void testDoSubmitDiskResourceQuery_Case1() {
        DataSearchPresenterImpl spy = spy(dsPresenter);
        DiskResourceQueryTemplate mockedTemplate = mock(DiskResourceQueryTemplate.class);
        when(mockedTemplate.getName()).thenReturn("mockedTemplateId");
        SubmitDiskResourceQueryEvent mockEvent = mock(SubmitDiskResourceQueryEvent.class);
        when(mockEvent.getQueryTemplate()).thenReturn(mockedTemplate);

        spy.setCleanCopyQueryTemplates(Collections.<DiskResourceQueryTemplate> emptyList());

        // Call method under test
        spy.doSubmitDiskResourceQuery(mockEvent);

        assertEquals("Verify that the active query has been set", mockedTemplate, spy.getActiveQuery());

        ArgumentCaptor<FolderSelectedEvent> fseCaptor = ArgumentCaptor.forClass(FolderSelectedEvent.class);
        verify(spy).fireEvent(fseCaptor.capture());
        assertEquals("Verify that a folder selected event has been fired with the mocked template", mockedTemplate, fseCaptor.getValue().getSelectedFolder());
    }

    /**
     * Verify #searchInit functionality when the call to retrieve saved templates is successful and returns queries.
     *
     * @see org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter#searchInit(org.iplantc.de.diskResource.client.views.DiskResourceView)
     */
    @Test public void testSearchInit_Case1() {
        final HasFolderSelectedEventHandlers hasFolderSelectHandlersMock = mock(HasFolderSelectedEventHandlers.class);
        final HasDeleteSavedSearchEventHandlers hasDeleteSavedSearchEventHandlers = mock(HasDeleteSavedSearchEventHandlers.class);
        final FolderSelectedEventHandler folderSelectedEventHandlerMock = mock(FolderSelectedEventHandler.class);
        DataSearchPresenterImpl spy = spy(dsPresenter);
        spy.searchInit(hasFolderSelectHandlersMock, hasDeleteSavedSearchEventHandlers,
                folderSelectedEventHandlerMock, viewMock);

        /* Verify that presenter adds itself as handler to hasHandlersMock */
        verify(hasFolderSelectHandlersMock).addFolderSelectedEventHandler(eq(spy));
        verify(hasDeleteSavedSearchEventHandlers).addDeleteSavedSearchEventHandler(eq(spy));

        verify(spy).addFolderSelectedEventHandler(eq(folderSelectedEventHandlerMock));

        assertEquals("Verify that view is saved", viewMock, dsPresenter.searchField);

        ArgumentCaptor<SaveDiskResourceQueryEvent.SaveDiskResourceQueryEventHandler> saveEventHandlerCaptor
                = ArgumentCaptor.forClass(SaveDiskResourceQueryEvent.SaveDiskResourceQueryEventHandler.class);
        ArgumentCaptor<SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler> submitEventHandlerCaptor
                = ArgumentCaptor.forClass(SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler.class);
        verify(viewMock).addSaveDiskResourceQueryEventHandler(saveEventHandlerCaptor.capture());
        verify(viewMock).addSubmitDiskResourceQueryEventHandler(submitEventHandlerCaptor.capture());
        assertEquals("Verify that the presenter registers itself to SaveDiskResourceQueryEvents", saveEventHandlerCaptor.getValue(), spy);
        assertEquals("Verify that the presenter registers itself to SubmitDiskResourceQueryEvents", submitEventHandlerCaptor.getValue(), spy);

        verifyNoMoreInteractions(viewMock, hasFolderSelectHandlersMock, folderSelectedEventHandlerMock, hasDeleteSavedSearchEventHandlers);
        verifyZeroInteractions(searchService, mockEventBus);
    }

    @Test public void testOnFolderSelected_Case1() {
        final HasFolderSelectedEventHandlers hasFolderSelectHandlersMock = mock(HasFolderSelectedEventHandlers.class);
        final HasDeleteSavedSearchEventHandlers hasDeleteSavedSearchEventHandlersMock = mock(HasDeleteSavedSearchEventHandlers.class);
        final FolderSelectedEventHandler folderSelectedEventHandlerMock = mock(FolderSelectedEventHandler.class);

        dsPresenter.searchInit(hasFolderSelectHandlersMock, hasDeleteSavedSearchEventHandlersMock,
                folderSelectedEventHandlerMock, viewMock);
        verify(hasFolderSelectHandlersMock).addFolderSelectedEventHandler(eq(dsPresenter));
        verify(hasDeleteSavedSearchEventHandlersMock).addDeleteSavedSearchEventHandler(dsPresenter);

        verify(viewMock).addSaveDiskResourceQueryEventHandler(any(SaveDiskResourceQueryEventHandler.class));
        verify(viewMock).addSubmitDiskResourceQueryEventHandler(any(SubmitDiskResourceQueryEventHandler.class));

        // Test update of searchResults mock
        FolderSelectedEvent fse = mock(FolderSelectedEvent.class);
        when(fse.getSelectedFolder()).thenReturn(mock(Folder.class));
        dsPresenter.onFolderSelected(fse);
        
        verify(viewMock).clearSearch();

        verifyNoMoreInteractions(viewMock, hasFolderSelectHandlersMock, folderSelectedEventHandlerMock, hasDeleteSavedSearchEventHandlersMock);
        verifyZeroInteractions(searchService, mockEventBus);
    }
    
    @Test public void testOnFolderSelected_Case2() {
        final HasFolderSelectedEventHandlers hasFolderSelectHandlersMock = mock(HasFolderSelectedEventHandlers.class);
        final HasDeleteSavedSearchEventHandlers hasDeleteSavedSearchEventHandlersMock = mock(HasDeleteSavedSearchEventHandlers.class);
        final FolderSelectedEventHandler folderSelectedEventHandlerMock = mock(FolderSelectedEventHandler.class);
        dsPresenter.searchInit(hasFolderSelectHandlersMock, hasDeleteSavedSearchEventHandlersMock,
                folderSelectedEventHandlerMock, viewMock);
        verify(hasFolderSelectHandlersMock).addFolderSelectedEventHandler(eq(dsPresenter));

        verify(viewMock).addSaveDiskResourceQueryEventHandler(any(SaveDiskResourceQueryEventHandler.class));
        verify(viewMock).addSubmitDiskResourceQueryEventHandler(any(SubmitDiskResourceQueryEventHandler.class));

        FolderSelectedEvent fse = mock(FolderSelectedEvent.class);
        final DiskResourceQueryTemplate selectedFolderMock = mock(DiskResourceQueryTemplate.class);
        when(fse.getSelectedFolder()).thenReturn(selectedFolderMock);
        dsPresenter.onFolderSelected(fse);
        
        verify(viewMock).edit(eq(selectedFolderMock));

        verifyNoMoreInteractions(viewMock, hasFolderSelectHandlersMock, folderSelectedEventHandlerMock);
        verifyZeroInteractions(searchService, mockEventBus);
    }
    
    @Test public void testLoadSavedQueries_Case1() {
        DataSearchPresenterImpl spy = spy(dsPresenter);

        assertTrue("Verify that store query templates are empty prior to calling method under test", spy.getQueryTemplates().isEmpty());

        final ArrayList<DiskResourceQueryTemplate> newArrayList = Lists.newArrayList(mock(DiskResourceQueryTemplate.class), mock(DiskResourceQueryTemplate.class));
        // Call method under test
        spy.loadSavedQueries(newArrayList);
        // Verify for record keeping
        verify(spy).loadSavedQueries(eq(newArrayList));

        verify(spy).setCleanCopyQueryTemplates(anyListOf(DiskResourceQueryTemplate.class));
        verify(searchService).createFrozenList(eq(newArrayList));
        // Verify for record keeping
        verify(spy).getQueryTemplates();
        verify(spy).updateDataNavigationWindow(eq(newArrayList),
                anyListOf(DiskResourceQueryTemplate.class));
        verify(mockEventBus).fireEvent(any(UpdateSavedSearchesEvent.class));

        verifyNoMoreInteractions(searchService, mockEventBus, spy, viewMock);
    }
    
    /**
     * 
     */
    @Test public void testOnDeleteSavedSearch_Case1() {
        DataSearchPresenterImpl spy = spy(dsPresenter);

        final DeleteSavedSearchEvent mockEvent = mock(DeleteSavedSearchEvent.class);
        final DiskResourceQueryTemplate mockSavedSearch = mock(DiskResourceQueryTemplate.class);
        when(mockEvent.getSavedSearch()).thenReturn(mockSavedSearch);
        spy.getQueryTemplates().add(mockSavedSearch);

        // Call method under test
        spy.onDeleteSavedSearch(mockEvent);

        verify(viewMock).clearSearch();
        verify(mockEvent).getSavedSearch();

        verify(searchService).saveQueryTemplates(eq(Collections.<DiskResourceQueryTemplate> emptyList()), drqtListAsyncCaptor.capture());

        verifyNoMoreInteractions(viewMock, searchService);
    }
}
