package org.iplantc.de.diskResource.client.presenters.grid;

import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.gin.factory.FolderContentsRpcProxyFactory;
import org.iplantc.de.diskResource.client.gin.factory.GridViewFactory;
import org.iplantc.de.diskResource.client.views.grid.DiskResourceColumnModel;

import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.ListStore;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.List;

/**
 * @author jstroot
 * FIXME Implement/update/correct this test.
 * This is a clone-and-own of the DR presenter test, since much of the implementation is simply
 * being migrated.
 */
@RunWith(GxtMockitoTestRunner.class)
public class GridViewPresenterImplTest {

    @Mock FolderContentsRpcProxyFactory folderContentsProxyFactoryMock;
    @Mock GridViewFactory gridViewFactoryMock;
    @Mock List<InfoType> infoTypeFiltersMock;
    @Mock NavigationView.Presenter navigationPresenterMock;
    @Mock TYPE entityTypeMock;
    @Mock GridView viewMock;
    @Mock GridView.FolderContentsRpcProxy folderContentsProxyMock;
    @Mock DiskResourceColumnModel columnModelMock;
    @Mock GridView.Presenter.Appearance appearanceMock;

    private GridViewPresenterImpl uut;

    @Before public void setUp() {
        when(folderContentsProxyFactoryMock.createWithEntityType(eq(infoTypeFiltersMock), eq(entityTypeMock))).thenReturn(folderContentsProxyMock);
        when(gridViewFactoryMock.create(any(GridView.Presenter.class), any(ListStore.class), eq(folderContentsProxyMock))).thenReturn(viewMock);
        when(viewMock.getColumnModel()).thenReturn(columnModelMock);
        uut = new GridViewPresenterImpl(gridViewFactoryMock, folderContentsProxyFactoryMock, appearanceMock, navigationPresenterMock, infoTypeFiltersMock, entityTypeMock);
    }

    @Test public void placeHolderTest() {

    }
/*

    @Mock DiskResourceView mockView;
    @Mock DiskResourceViewFactory mockViewFactory;
    @Mock DiskResourceView.FolderRpcProxy mockFolderRpc;
    @Mock FolderContentsRpcProxyFactory mockFolderContentsRpcFactory;
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
    @Mock NavigationView.Presenter mockNavigationPresenter;
    @Mock NavigationView mockNavigationView;
    @Mock GridViewPresenterFactory mockGridViewPresenterFactory;
    @Mock GridView.Presenter mockGridViewPresenter;
    @Mock GridView mockGridView;


    private DiskResourcePresenterImpl uut;

    // TODO: SS complete tests with new service
    @Before public void setUp() {
        setupMocks();
        uut = new DiskResourcePresenterImpl(mockViewFactory,
                                            mockFactory,
                                            mockNavigationPresenter,
                                            mockGridViewPresenterFactory,
                                            mockDataSearchPresenter,
                                            mockDisplayStrings,
                                            mockAnnouncer,
                                            mockEventBus,
                                            null,
                                            null);
    }

    *//**
     * <b>Conditions:</b>
     * 
     * Root folders have been loaded.
     * Folder has not yet been loaded.
     * Folder has no common roots with tree store.
     *//*
    @Ignore("Migrate to NavigationView.Presenter test")
    @Test public void testSetSelectedFolderByPath_Case1() {
        DiskResourcePresenterImpl spy = spy(uut);
        HasPath mockHasPath = mock(HasPath.class);
        final String TEST_PATH = "/no/common/root";
        when(mockHasPath.getPath()).thenReturn(TEST_PATH);

        // Roots have been loaded
        when(mockTreeStore.getRootCount()).thenReturn(4);

        spy.setSelectedFolderByPath(mockHasPath);

//        verify(spy, never()).addEventHandlerRegistration(any(SelectFolderByPathLoadHandler.class), any(HandlerRegistration.class));
    }

    *//**
     * <b>Conditions:<b>
     * 
     * Root folders have been loaded.
     * Folder has not yet been loaded.
     * Folder has common root with treeStore
     *//*
    @Ignore("Migrate to NavigationView.Presenter test")
    @Test public void testSetSelectedFolderByPath_Case2() {
        DiskResourcePresenterImpl spy = spy(uut);
        HasPath mockHasPath = mock(HasPath.class);
        final String COMMON_ROOT = "/home";
        final String TEST_PATH = COMMON_ROOT + "/common/root";
        when(mockHasPath.getPath()).thenReturn(TEST_PATH);

        // Folder has common root
        Folder mockFolder = mock(Folder.class);
        // Set up mock to bypass id load handler init logic, we don't need to test that here.
        when(mockTreeStore.getRootItems()).thenReturn(Lists.newArrayList(mockFolder));
        when(mockFolder.getPath()).thenReturn(COMMON_ROOT);

        // Roots have been loaded
        when(mockTreeStore.getRootCount()).thenReturn(4);

        spy.setSelectedFolderByPath(mockHasPath);

//        verify(spy).addEventHandlerRegistration(any(SelectFolderByPathLoadHandler.class), any(HandlerRegistration.class));
    }

    *//**
     * <b>Conditions:<b>
     * 
     * Root folders have not been loaded.
     * Folder has not yet been loaded.
     *//*
    @Ignore("Migrate to NavigationView.Presenter test")
    @Test public void testSetSelectedFolderByPath_Case3() {
        DiskResourcePresenterImpl spy = spy(uut);
        HasPath mockHasPath = mock(HasPath.class);
        final String COMMON_ROOT = "/home";
        final String TEST_PATH = COMMON_ROOT + "/common/root";
        when(mockHasPath.getPath()).thenReturn(TEST_PATH);

        // Roots have been loaded
        when(mockTreeStore.getRootCount()).thenReturn(0);

        spy.setSelectedFolderByPath(mockHasPath);

//        verify(spy).addEventHandlerRegistration(any(SelectFolderByPathLoadHandler.class), any(HandlerRegistration.class));
    }

    private void setupMocks() {
        when(mockViewFactory.create(any(DiskResourceView.Presenter.class), any(NavigationView.Presenter.class), any(GridView.Presenter.class))).thenReturn(mockView);
        when(mockGridViewPresenterFactory.create(any(NavigationView.Presenter.class), anyList(), any(TYPE.class))).thenReturn(mockGridViewPresenter);
        when(mockView.getToolbar()).thenReturn(mockToolbar);
        when(mockToolbar.getSearchField()).thenReturn(mockSearchField);
        when(mockNavigationPresenter.getView()).thenReturn(mockNavigationView);
        when(mockGridViewPresenter.getView()).thenReturn(mockGridView);
    }

    *//**
     * Verifies that a template will be added to the tree store if it is not already there.
     *
     *//*
    @Test public void testUpdateSavedSearches_Case1() {
        DiskResourceQueryTemplate mock1 = mock(DiskResourceQueryTemplate.class);
        DiskResourceQueryTemplate mock2 = mock(DiskResourceQueryTemplate.class);
        DiskResourceQueryTemplate mock3 = mock(DiskResourceQueryTemplate.class);
        when(mock1.getId()).thenReturn("qtMock1Id");
        when(mock2.getId()).thenReturn("qtMock2Id");
        when(mock3.getId()).thenReturn("qtMock3Id");

        // Create list to pass which contains all 3 query template mocks
        List<DiskResourceQueryTemplate> queryTemplates = Lists.newArrayList(mock1, mock2, mock3);
        when(eventMock.getSavedSearches()).thenReturn(queryTemplates);

        // Call method under test
        uut.onUpdateSavedSearches(eventMock);

        verify(mockNavigationPresenter).updateQueryTemplate(eq(mock1));
        verify(mockNavigationPresenter).updateQueryTemplate(eq(mock2));
        verify(mockNavigationPresenter).updateQueryTemplate(eq(mock3));

        *//* Verify that nothing is removed from the store *//*
        verify(mockNavigationPresenter, never()).removeFolder(any(Folder.class));

    }


    *//**
     * Verifies that an item which is dirty and already in the tree store will be updated.
     *
     *//*
    @Test public void testUpdateSavedSearches_Case2() {
        DiskResourceQueryTemplate mock1 = mock(DiskResourceQueryTemplate.class);
        DiskResourceQueryTemplate mock2 = mock(DiskResourceQueryTemplate.class);
        when(mock1.getId()).thenReturn("qtMock1Id");
        when(mock1.isDirty()).thenReturn(true);
        when(mock2.getId()).thenReturn("qtMock2Id");

        // Create list to pass which contains all 3 query template mocks
        List<DiskResourceQueryTemplate> queryTemplates = Lists.newArrayList(mock1, mock2);
        when(eventMock.getSavedSearches()).thenReturn(queryTemplates);

        // Call method under test
        uut.onUpdateSavedSearches(eventMock);

        *//* Verify that nothing is removed from the store *//*
        verify(mockNavigationPresenter, never()).removeFolder(any(Folder.class));

        *//* Verify that the tree store is updated with the dirty query template *//*
        verify(mockNavigationPresenter).updateQueryTemplate(eq(mock1));

    }

    *//**
     * Verifies that templates in the RemovedSearches list of an UpdateSavedSearchesEvent will be removed
     * from the tree store.
     *//*
    @Test public void testUpdateSavedSearches_Case3() {
        final ArrayList<DiskResourceQueryTemplate> newArrayList = Lists.newArrayList(
                mock(DiskResourceQueryTemplate.class), mock(DiskResourceQueryTemplate.class));
        when(eventMock.getRemovedSearches()).thenReturn(newArrayList);

        // Call method under test
        uut.onUpdateSavedSearches(eventMock);

        // Verify for record keeping
        verify(mockNavigationPresenter).removeFolder(eq(newArrayList.get(0)));
        verify(mockNavigationPresenter).removeFolder(eq(newArrayList.get(1)));

    }*/

}
