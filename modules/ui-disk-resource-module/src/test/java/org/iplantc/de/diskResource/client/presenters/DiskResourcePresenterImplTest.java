package org.iplantc.de.diskResource.client.presenters;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsRpcProxy;
import org.iplantc.de.diskResource.client.presenters.proxy.SelectFolderByIdLoadHandler;
import org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.diskResource.client.views.widgets.DiskResourceViewToolbar;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.TreeStore;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class DiskResourcePresenterImplTest {

    @Mock DiskResourceView mockView;
    @Mock DiskResourceView.Proxy mockProck;
    @Mock FolderContentsRpcProxy mockFolderRpcProxy;
    @Mock DiskResourceServiceFacade mockDiskResourceService;
    @Mock IplantDisplayStrings mockDisplayStrings;
    @Mock DiskResourceAutoBeanFactory mockFactory;
    @Mock DataSearchPresenter mockDataSearchPresenter;
    @Mock EventBus mockEventBus;
    
    @Mock DiskResourceViewToolbar mockToolbar;
    @Mock DiskResourceSearchField mockSearchField;

    @Mock TreeStore<Folder> mockTreeStore;

    private DiskResourcePresenterImpl uut;

    @Before public void setUp() {
        setupMocks();
        uut = new DiskResourcePresenterImpl(mockView, mockProck, mockFolderRpcProxy, mockDiskResourceService, mockDisplayStrings, mockFactory, mockDataSearchPresenter, mockEventBus);
    }

    /**
     * <b>Conditions:</b>
     * 
     * Root folders have been loaded.
     * Folder has not yet been loaded.
     * Folder has no common roots with tree store.
     */
    @Test public void testSetSelectedFolderById_Case1() {
        DiskResourcePresenterImpl spy = spy(uut);
        HasId mockHasId = mock(HasId.class);
        final String TESTID = "/no/common/root";
        when(mockHasId.getId()).thenReturn(TESTID);

        // Folder has not yet been loaded
        when(mockView.getFolderById(TESTID)).thenReturn(null);

        // Roots have been loaded
        when(mockTreeStore.getAllItemsCount()).thenReturn(4);

        spy.setSelectedFolderById(mockHasId);

        verify(spy, never()).addEventHandlerRegistration(any(SelectFolderByIdLoadHandler.class), any(HandlerRegistration.class));
    }

    /**
     * <b>Conditions:<b>
     * 
     * Root folders have been loaded.
     * Folder has not yet been loaded.
     * Folder has common root with treeStore
     */
    @Test public void testSetSelectedFolderById_Case2() {
        DiskResourcePresenterImpl spy = spy(uut);
        HasId mockHasId = mock(HasId.class);
        final String COMMON_ROOT = "/home";
        final String TESTID = COMMON_ROOT + "/common/root";
        when(mockHasId.getId()).thenReturn(TESTID);

        // Folder has not yet been loaded
        when(mockView.getFolderById(TESTID)).thenReturn(null);

        // Folder has common root
        Folder mockFolder = mock(Folder.class);
        when(mockView.getFolderById(COMMON_ROOT)).thenReturn(mockFolder);
        // Set up mock to bypass id load handler init logic, we don't need to test that here.
        when(mockFolder.getPath()).thenReturn("");
        when(mockView.isLoaded(mockFolder)).thenReturn(false);

        // Roots have been loaded
        when(mockTreeStore.getAllItemsCount()).thenReturn(4);

        spy.setSelectedFolderById(mockHasId);

        verify(spy).addEventHandlerRegistration(any(SelectFolderByIdLoadHandler.class), any(HandlerRegistration.class));
    }

    /**
     * <b>Conditions:<b>
     * 
     * Root folders have not been loaded.
     * Folder has not yet been loaded.
     */
    @Test public void testSetSelectedFolderById_Case3() {
        DiskResourcePresenterImpl spy = spy(uut);
        HasId mockHasId = mock(HasId.class);
        final String COMMON_ROOT = "/home";
        final String TESTID = COMMON_ROOT + "/common/root";
        when(mockHasId.getId()).thenReturn(TESTID);

        // Folder has not yet been loaded
        when(mockView.getFolderById(TESTID)).thenReturn(null);

        // Roots have been loaded
        when(mockTreeStore.getAllItemsCount()).thenReturn(0);

        spy.setSelectedFolderById(mockHasId);

        verify(spy).addEventHandlerRegistration(any(SelectFolderByIdLoadHandler.class), any(HandlerRegistration.class));
    }

    private void setupMocks() {
        when(mockView.getTreeStore()).thenReturn(mockTreeStore);
        when(mockView.getToolbar()).thenReturn(mockToolbar);
        when(mockToolbar.getSearchField()).thenReturn(mockSearchField);

        when(mockView.getCenterPanelHeader()).thenReturn(mock(HasSafeHtml.class));

    }

}
