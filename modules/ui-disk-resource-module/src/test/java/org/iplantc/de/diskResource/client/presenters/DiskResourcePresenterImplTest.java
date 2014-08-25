package org.iplantc.de.diskResource.client.presenters;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.dataLink.DataLinkFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.presenters.proxy.SelectFolderByPathLoadHandler;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class DiskResourcePresenterImplTest {

    @Mock DiskResourceView mockView;
    @Mock DiskResourceView.Proxy mockProck;
    @Mock DiskResourceServiceFacade mockDiskResourceService;
    @Mock IplantDisplayStrings mockDisplayStrings;
    @Mock IplantErrorStrings errorStringsMock;
    @Mock IplantContextualHelpStrings helpStringsMock;
    @Mock DiskResourceAutoBeanFactory mockFactory;
    @Mock DataLinkFactory mockDlFactory;
    @Mock DataSearchPresenter mockDataSearchPresenter;
    @Mock EventBus mockEventBus;
    @Mock UserInfo mockUserInfo;
    @Mock MetadataServiceFacade mockFileSystemMetataService;

    @Mock IplantAnnouncer mockAnnouncer;
    @Mock DiskResourceView.DiskResourceViewToolbar mockToolbar;

    @Mock DiskResourceSearchField mockSearchField;
    @Mock TreeStore<Folder> mockTreeStore;

    private DiskResourcePresenterImpl uut;

    // TODO: SS complete tests with new service
    @Before public void setUp() {
        setupMocks();
        uut = new DiskResourcePresenterImpl(mockView, mockProck,
                                            mockDiskResourceService, mockFileSystemMetataService, mockDisplayStrings, errorStringsMock, helpStringsMock,
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
        when(mockView.getTreeStore()).thenReturn(mockTreeStore);
        when(mockView.getToolbar()).thenReturn(mockToolbar);
        when(mockToolbar.getSearchField()).thenReturn(mockSearchField);
    }

}
