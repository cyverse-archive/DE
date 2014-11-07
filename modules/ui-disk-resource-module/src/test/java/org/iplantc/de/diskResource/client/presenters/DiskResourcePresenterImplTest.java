package org.iplantc.de.diskResource.client.presenters;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.dataLink.DataLinkFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceViewFactory;
import org.iplantc.de.diskResource.client.gin.factory.FolderRpcProxyFactory;
import org.iplantc.de.diskResource.client.presenters.proxy.SelectFolderByPathLoadHandler;
import org.iplantc.de.diskResource.client.search.events.UpdateSavedSearchesEvent;
import org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.resources.client.messages.IplantContextualHelpStrings;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.data.shared.loader.TreeLoader;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

@RunWith(GxtMockitoTestRunner.class)
public class DiskResourcePresenterImplTest {

    @Mock DiskResourceView mockView;
    @Mock DiskResourceViewFactory mockViewFactory;
    @Mock FolderRpcProxyFactory mockFolderRpcFactory;
    @Mock DiskResourceView.FolderContentsRpcProxy mockFolderContentsRpcProxy;
    @Mock DiskResourceView.FolderRpcProxy mockFolderRpcProxy;
    @Mock DiskResourceServiceFacade mockDiskResourceService;
    @Mock IplantDisplayStrings mockDisplayStrings;
    @Mock IplantErrorStrings errorStringsMock;
    @Mock IplantContextualHelpStrings helpStringsMock;
    @Mock DiskResourceAutoBeanFactory mockFactory;
    @Mock DataLinkFactory mockDlFactory;
    @Mock DataSearchPresenter mockDataSearchPresenter;
    @Mock EventBus mockEventBus;
    @Mock UserInfo mockUserInfo;
    @Mock MetadataServiceFacade mockFileSystemMetadataService;
    @Mock UpdateSavedSearchesEvent eventMock;

    @Mock IplantAnnouncer mockAnnouncer;
    @Mock DiskResourceView.DiskResourceViewToolbar mockToolbar;

    @Mock DiskResourceSearchField mockSearchField;
    @Mock TreeStore<Folder> mockTreeStore;


    private DiskResourcePresenterImpl uut;

    // TODO: SS complete tests with new service
    @Before public void setUp() {
        setupMocks();
        uut = new DiskResourcePresenterImpl(mockViewFactory,
                                            mockFolderRpcFactory, mockFolderContentsRpcProxy,
                                            mockDiskResourceService, mockFileSystemMetadataService,
                                            mockDisplayStrings, errorStringsMock, helpStringsMock,
                                            mockFactory, mockDlFactory,
                                            mockUserInfo, mockDataSearchPresenter,
                                            mockEventBus, mockAnnouncer);
    }

    /**
     * <b>Conditions:</b>
     * 
     * Root folders have been loaded.
     * Folder has not yet been loaded.
     * Folder has no common roots with tree store.
     */
    @Test public void testSetSelectedFolderByPath_Case1() {
        DiskResourcePresenterImpl spy = spy(uut);
        HasPath mockHasPath = mock(HasPath.class);
        final String TEST_PATH = "/no/common/root";
        when(mockHasPath.getPath()).thenReturn(TEST_PATH);

        // Folder has not yet been loaded
        when(mockView.getFolderByPath(TEST_PATH)).thenReturn(null);

        // Roots have been loaded
        when(mockTreeStore.getRootCount()).thenReturn(4);

        spy.setSelectedFolderByPath(mockHasPath);

        verify(spy, never()).addEventHandlerRegistration(any(SelectFolderByPathLoadHandler.class), any(HandlerRegistration.class));
    }

    /**
     * <b>Conditions:<b>
     * 
     * Root folders have been loaded.
     * Folder has not yet been loaded.
     * Folder has common root with treeStore
     */
    @Test public void testSetSelectedFolderByPath_Case2() {
        DiskResourcePresenterImpl spy = spy(uut);
        HasPath mockHasPath = mock(HasPath.class);
        final String COMMON_ROOT = "/home";
        final String TEST_PATH = COMMON_ROOT + "/common/root";
        when(mockHasPath.getPath()).thenReturn(TEST_PATH);

        // Folder has not yet been loaded
        when(mockView.getFolderByPath(TEST_PATH)).thenReturn(null);

        // Folder has common root
        Folder mockFolder = mock(Folder.class);
        when(mockView.getFolderByPath(COMMON_ROOT)).thenReturn(mockFolder);
        // Set up mock to bypass id load handler init logic, we don't need to test that here.
        when(mockTreeStore.getRootItems()).thenReturn(Lists.newArrayList(mockFolder));
        when(mockFolder.getPath()).thenReturn(COMMON_ROOT);
        when(mockView.isLoaded(mockFolder)).thenReturn(false);

        // Roots have been loaded
        when(mockTreeStore.getRootCount()).thenReturn(4);

        spy.setSelectedFolderByPath(mockHasPath);

        verify(spy).addEventHandlerRegistration(any(SelectFolderByPathLoadHandler.class), any(HandlerRegistration.class));
    }

    /**
     * <b>Conditions:<b>
     * 
     * Root folders have not been loaded.
     * Folder has not yet been loaded.
     */
    @Test public void testSetSelectedFolderByPath_Case3() {
        DiskResourcePresenterImpl spy = spy(uut);
        HasPath mockHasPath = mock(HasPath.class);
        final String COMMON_ROOT = "/home";
        final String TEST_PATH = COMMON_ROOT + "/common/root";
        when(mockHasPath.getPath()).thenReturn(TEST_PATH);

        // Folder has not yet been loaded
        when(mockView.getFolderByPath(TEST_PATH)).thenReturn(null);

        // Roots have been loaded
        when(mockTreeStore.getRootCount()).thenReturn(0);

        spy.setSelectedFolderByPath(mockHasPath);

        verify(spy).addEventHandlerRegistration(any(SelectFolderByPathLoadHandler.class), any(HandlerRegistration.class));
    }

    private void setupMocks() {
        when(mockViewFactory.create(any(DiskResourceView.Presenter.class), any(TreeLoader.class), any(PagingLoader.class))).thenReturn(mockView);
        when(mockFolderRpcFactory.create(any(IsMaskable.class))).thenReturn(mockFolderRpcProxy);
        when(mockView.getTreeStore()).thenReturn(mockTreeStore);
        when(mockView.getToolbar()).thenReturn(mockToolbar);
        when(mockToolbar.getSearchField()).thenReturn(mockSearchField);
    }

    /**
     * Verifies that a template will be added to the tree store if it is not already there.
     *
     */
    @Test public void testUpdateSavedSearches_Case1() {
        DiskResourceQueryTemplate mock1 = mock(DiskResourceQueryTemplate.class);
        DiskResourceQueryTemplate mock2 = mock(DiskResourceQueryTemplate.class);
        DiskResourceQueryTemplate mock3 = mock(DiskResourceQueryTemplate.class);
        when(mock1.getId()).thenReturn("qtMock1Id");
        when(mock2.getId()).thenReturn("qtMock2Id");
        when(mock3.getId()).thenReturn("qtMock3Id");

        // Set up Folder root tree store items
        List<Folder> toReturn = createTreeStoreRootFolderList();
        // Add 2 mocks to tree store root items
        toReturn.add(mock1);
        toReturn.add(mock2);
        when(mockTreeStore.getRootItems()).thenReturn(toReturn);
        when(mockTreeStore.findModelWithKey("qtMock1Id")).thenReturn(mock1);
        when(mockTreeStore.findModelWithKey("qtMock2Id")).thenReturn(mock2);

        // Create list to pass which contains all 3 query template mocks
        List<DiskResourceQueryTemplate> queryTemplates = Lists.newArrayList(mock1, mock2, mock3);
        when(eventMock.getSavedSearches()).thenReturn(queryTemplates);

        // Call method under test
        uut.onUpdateSavedSearches(eventMock);

        verify(mockTreeStore, times(queryTemplates.size())).findModelWithKey(anyString());

        /* Verify that nothing is removed from the store */
        verify(mockTreeStore, never()).remove(any(Folder.class));

        /* Verify that no item is updated in the store */
        verify(mockTreeStore, never()).update(any(Folder.class));

        /* Verify that the mock not previously contained in the tree store is added */
        verify(mockTreeStore).add(eq(mock3));
    }


    /**
     * Verifies that an item which is dirty and already in the tree store will be updated.
     *
     */
    @Test
    public void testUpdateSavedSearches_Case2() {
        DiskResourceQueryTemplate mock1 = mock(DiskResourceQueryTemplate.class);
        DiskResourceQueryTemplate mock2 = mock(DiskResourceQueryTemplate.class);
        when(mock1.getId()).thenReturn("qtMock1Id");
        when(mock1.isDirty()).thenReturn(true);
        when(mock2.getId()).thenReturn("qtMock2Id");

        // Set up Folder root tree store items
        List<Folder> toReturn = createTreeStoreRootFolderList();
        // Add 2 mocks to tree store root items
        toReturn.add(mock1);
        toReturn.add(mock2);
        when(mockTreeStore.getRootItems()).thenReturn(toReturn);
        when(mockTreeStore.findModelWithKey("qtMock1Id")).thenReturn(mock1);
        when(mockTreeStore.findModelWithKey("qtMock2Id")).thenReturn(mock2);

        // Create list to pass which contains all 3 query template mocks
        List<DiskResourceQueryTemplate> queryTemplates = Lists.newArrayList(mock1, mock2);
        when(eventMock.getSavedSearches()).thenReturn(queryTemplates);

        // Call method under test
        uut.onUpdateSavedSearches(eventMock);

        /* Verify that nothing is removed from the store */
        verify(mockTreeStore, never()).remove(any(Folder.class));

        /* Verify that the tree store is updated with the dirty query template */
        verify(mockTreeStore).update(eq(mock1));

        /* Verify that nothing is added to the store */
        verify(mockTreeStore, never()).add(any(Folder.class));
    }

    /**
     * Verifies that templates in the RemovedSearches list of an UpdateSavedSearchesEvent will be removed
     * from the tree store.
     */
    @Test
    public void testUpdateSavedSearches_Case3() {
        final ArrayList<DiskResourceQueryTemplate> newArrayList = Lists.newArrayList(
                mock(DiskResourceQueryTemplate.class), mock(DiskResourceQueryTemplate.class));
        when(eventMock.getRemovedSearches()).thenReturn(newArrayList);

        // Call method under test
        uut.onUpdateSavedSearches(eventMock);

        // Verify for record keeping
        verify(mockTreeStore).remove(eq(newArrayList.get(0)));
        verify(mockTreeStore).remove(eq(newArrayList.get(1)));

        verifyNoMoreInteractions(mockTreeStore);
    }

    List<Folder> createTreeStoreRootFolderList() {
        // Set up Folder root tree store items
        Folder root1 = mock(Folder.class);
        Folder root2 = mock(Folder.class);
        Folder root3 = mock(Folder.class);
        Folder root4 = mock(Folder.class);
        when(root1.getId()).thenReturn("root1Id");
        when(root2.getId()).thenReturn("root2Id");
        when(root3.getId()).thenReturn("root3Id");
        when(root4.getId()).thenReturn("root4Id");
        return Lists.newArrayList(root1, root2, root3, root4);
    }
}
