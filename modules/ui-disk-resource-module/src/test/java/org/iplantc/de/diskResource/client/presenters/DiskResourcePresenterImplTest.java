package org.iplantc.de.diskResource.client.presenters;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.dataLink.DataLinkFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceViewFactory;
import org.iplantc.de.diskResource.client.gin.factory.FolderContentsRpcProxyFactory;
import org.iplantc.de.diskResource.client.gin.factory.GridViewPresenterFactory;
import org.iplantc.de.diskResource.client.search.events.UpdateSavedSearchesEvent;
import org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;
import org.iplantc.de.resources.client.messages.IplantContextualHelpStrings;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.common.collect.Lists;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.TreeStore;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

/**
 * @author jstroot
 */
@RunWith(GxtMockitoTestRunner.class)
public class DiskResourcePresenterImplTest {

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
    @Mock
    ToolbarView mockToolbar;

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

    /**
     * <b>Conditions:</b>
     * 
     * Root folders have been loaded.
     * Folder has not yet been loaded.
     * Folder has no common roots with tree store.
     */
    @Ignore("Migrate to NavigationView.Presenter test")
    @Test public void testSetSelectedFolderByPath_Case1() {
        DiskResourcePresenterImpl spy = spy(uut);
        HasPath mockHasPath = mock(HasPath.class);
        final String TEST_PATH = "/no/common/root";
        when(mockHasPath.getPath()).thenReturn(TEST_PATH);

        // Roots have been loaded
        when(mockTreeStore.getRootCount()).thenReturn(4);

        spy.setSelectedFolderByPath(mockHasPath);

    }

    /**
     * <b>Conditions:<b>
     * 
     * Root folders have been loaded.
     * Folder has not yet been loaded.
     * Folder has common root with treeStore
     */
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

    }

    /**
     * <b>Conditions:<b>
     * 
     * Root folders have not been loaded.
     * Folder has not yet been loaded.
     */
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

    }

    private void setupMocks() {
        when(mockViewFactory.create(any(DiskResourceView.Presenter.class), any(NavigationView.Presenter.class), any(GridView.Presenter.class))).thenReturn(mockView);
        when(mockGridViewPresenterFactory.create(any(NavigationView.Presenter.class), anyList(), any(TYPE.class))).thenReturn(mockGridViewPresenter);
        when(mockView.getToolbar()).thenReturn(mockToolbar);
        when(mockToolbar.getSearchField()).thenReturn(mockSearchField);
        when(mockNavigationPresenter.getView()).thenReturn(mockNavigationView);
        when(mockGridViewPresenter.getView()).thenReturn(mockGridView);
    }


}
