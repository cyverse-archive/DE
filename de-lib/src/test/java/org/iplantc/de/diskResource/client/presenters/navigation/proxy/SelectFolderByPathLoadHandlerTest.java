package org.iplantc.de.diskResource.client.presenters.navigation.proxy;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.NavigationView;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.loader.LoadEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

/**
 * Tests various lazy-loading scenarios using the SelectFolderByPathLoadHandler.
 * 
 * @author psarando
 * 
 */
@RunWith(GxtMockitoTestRunner.class)
@SuppressWarnings("nls")
public class SelectFolderByPathLoadHandlerTest {

    @Mock NavigationView.Presenter presenterMock;
    @Mock IplantAnnouncer announcerMock;
    @Mock Folder folderToSelectMock;
    @Mock LoadEvent<Folder, List<Folder>> eventMock;
    @Mock Scheduler deferredSchedulerMock;
    @Mock IsMaskable maskableMock;
    @Mock NavigationView.Presenter.Appearance appearanceMock;

    private SelectFolderByPathLoadHandler loadHandlerUnderTest;

    /**
     * Path to the target folder that should be loaded and selected by the SelectFolderByPathLoadHandler.
     */
    private final String targetFolderPath = "/test/path/target/folder";

    /**
     * Path to the parent folder of the target folder.
     */
    private final String targetFolderParentPath = "/test/path/target";

    /**
     * Path to the parent of the parent folder of the target folder.
     */
    private final String targetFolderParentParentPath = "/test/path";

    /**
     * Path to the root folder, which is also the parent of the parent of the parent folder of the target
     * folder.
     */
    private final String rootPath = "/test";

    @Before public void setUp() {
        when(folderToSelectMock.getId()).thenReturn(targetFolderPath);
        when(folderToSelectMock.getPath()).thenReturn(targetFolderPath);
        when(presenterMock.rootsLoaded()).thenReturn(true);
        when(appearanceMock.diskResourceDoesNotExist(anyString())).thenReturn("sample");
    }

    private Folder initMockFolder(String path, String mockName) {
        Folder folder = mock(Folder.class, mockName);
        when(folder.getId()).thenReturn(path);
        when(folder.getPath()).thenReturn(path);
        return folder;
    }

    /**
     * Tests the scenario where the root folders have already been loaded into the view's TreeStore, but
     * none of the child paths have been loaded yet.
     */
    @Test public void testLoad_OnlyRootsLoaded() {
        Folder rootPathFolderMock = initMockFolder(rootPath, "rootPathFolderMock");
        Folder targetFolderParentParent = initMockFolder(targetFolderParentParentPath, "targerFolderParentParent");
        Folder targetFolderParent = initMockFolder(targetFolderParentPath, "targetFolderParent");
        InOrder expandInOrder = inOrder(presenterMock);

        // Start with only the rootPath loaded in the treeStoreMock, but no children loaded under it.
        when(presenterMock.getRootItems()).thenReturn(Lists.newArrayList(rootPathFolderMock));

        // The SelectFolderByPathLoadHandler constructor will search as far down the path to the target
        // folder as possible for a folder already loaded in the viewMock.
        when(presenterMock.getFolderByPath(targetFolderPath)).thenReturn(null);
        when(presenterMock.getFolderByPath(targetFolderParentPath)).thenReturn(null);
        when(presenterMock.getFolderByPath(targetFolderParentParentPath)).thenReturn(null);
        when(presenterMock.getFolderByPath(rootPath)).thenReturn(rootPathFolderMock);

        // The handler's constructor should call viewMock#expandFolder on rootPath.
        loadHandlerUnderTest = spy(new SelectFolderByPathLoadHandler(folderToSelectMock, presenterMock,
                                                                     appearanceMock,
                                                                     maskableMock, announcerMock));
        loadHandlerUnderTest.setHandlerRegistration(mock(HandlerRegistration.class));
        verifyPresenterInit();
        verify(presenterMock, times(4)).getFolderByPath(anyString());
        verify(presenterMock).isLoaded(rootPathFolderMock);
        expandInOrder.verify(presenterMock).expandFolder(rootPathFolderMock);

        // The next onLoad method should call viewMock#expandFolder on targetFolderParentParentPath.
        when(eventMock.getLoadConfig()).thenReturn(rootPathFolderMock);
        when(presenterMock.getFolderByPath(targetFolderParentParentPath))
                .thenReturn(targetFolderParentParent);

        loadHandlerUnderTest.onLoad(eventMock);
        expandInOrder.verify(presenterMock).expandFolder(targetFolderParentParent);

        // The next onLoad method should call viewMock#expandFolder on targetFolderParentPath.
        when(eventMock.getLoadConfig()).thenReturn(targetFolderParentParent);
        when(presenterMock.getFolderByPath(targetFolderParentPath)).thenReturn(targetFolderParent);

        loadHandlerUnderTest.onLoad(eventMock);
        expandInOrder.verify(presenterMock).expandFolder(targetFolderParent);

        // The last onLoad method should find folderToSelectMock in the viewMock.
        when(presenterMock.getFolderByPath(targetFolderPath)).thenReturn(folderToSelectMock);
        loadHandlerUnderTest.onLoad(eventMock);
        verify(presenterMock).setSelectedFolder(folderToSelectMock);
        verify(loadHandlerUnderTest).unmaskView();
        verifyPresenterCleanup();

//        verifyNoMoreInteractions(presenterMock);
    }

    /**
     * Tests the scenario where the root folders have already been loaded into the view's TreeStore, none
     * of the child paths have been loaded yet, but the target folder does not exist under its parent
     * folder.
     */
    @Test public void testLoad_OnlyRootsLoaded_TargetDeleted() {
        Folder rootPathFolderMock = initMockFolder(rootPath, "rootPathFolderMock");
        Folder targerFolderParentParent = initMockFolder(targetFolderParentParentPath, "targerFolderParentParent");
        Folder targetFolderParent = initMockFolder(targetFolderParentPath, "targetFolderParent");
        InOrder expandInOrder = inOrder(presenterMock);

        // Start with only the rootPath loaded in the treeStoreMock, but no children loaded under it.
        when(presenterMock.getRootItems()).thenReturn(Lists.newArrayList(rootPathFolderMock));

        // The SelectFolderByPathLoadHandler constructor will search as far down the path to the target
        // folder as possible for a folder already loaded in the viewMock.
        when(presenterMock.getFolderByPath(targetFolderPath)).thenReturn(null);
        when(presenterMock.getFolderByPath(targetFolderParentPath)).thenReturn(null);
        when(presenterMock.getFolderByPath(targetFolderParentParentPath)).thenReturn(null);
        when(presenterMock.getFolderByPath(rootPath)).thenReturn(rootPathFolderMock);

        loadHandlerUnderTest = spy(new SelectFolderByPathLoadHandler(folderToSelectMock, presenterMock,
                                                                     appearanceMock,
                                                                     maskableMock, announcerMock));
        loadHandlerUnderTest.setHandlerRegistration(mock(HandlerRegistration.class));
        verifyPresenterInit();
        verify(presenterMock, times(4)).getFolderByPath(anyString());
        verify(presenterMock).isLoaded(rootPathFolderMock);
        expandInOrder.verify(presenterMock).expandFolder(rootPathFolderMock);

        // The next onLoad method should call viewMock#expandFolder on targetFolderParentParentPath.
        when(eventMock.getLoadConfig()).thenReturn(rootPathFolderMock);
        when(presenterMock.getFolderByPath(targetFolderParentParentPath))
                .thenReturn(targerFolderParentParent);

        loadHandlerUnderTest.onLoad(eventMock);
        expandInOrder.verify(presenterMock).expandFolder(targerFolderParentParent);

        // The next onLoad method should call viewMock#expandFolder on targetFolderParentPath.
        when(eventMock.getLoadConfig()).thenReturn(targerFolderParentParent);
        when(presenterMock.getFolderByPath(targetFolderParentPath)).thenReturn(targetFolderParent);

        loadHandlerUnderTest.onLoad(eventMock);
        expandInOrder.verify(presenterMock).expandFolder(targetFolderParent);

        // The next onLoad method should expect the target folder to be loaded in the view.
        when(eventMock.getLoadConfig()).thenReturn(targetFolderParent);
        when(presenterMock.getFolderByPath(targetFolderPath)).thenReturn(null);
        loadHandlerUnderTest.onLoad(eventMock);

        // Since the targetFolderParentPath was loaded but targetFolderPath was not found in the
        // viewMock, the handler should select the target folder's parent (folderMock) and display an
        // error message.
        verify(presenterMock).setSelectedFolder(targetFolderParent);
        verify(announcerMock).schedule(any(ErrorAnnouncementConfig.class));
        verify(loadHandlerUnderTest).unmaskView();
        verifyPresenterCleanup();
//        verifyNoMoreInteractions(presenterMock);
    }

    /**
     * Tests the scenario where the target folder, and all folders along the path to the target folder,
     * have already been loaded from the service (i.e. they are already cached by the service facade),
     * but they have not yet been loaded into the view's TreeStore.
     */
    @Test public void testLoad_TargetCached() {
        Folder rootPathFolderMock = initMockFolder(rootPath, "rootPathFolderMock");
        when(presenterMock.rootsLoaded()).thenReturn(false);

        // Start with no roots loaded in the treeStoreMock. This causes the handler to wait until the
        // first onLoad callback, which means that the roots have just been loaded into the viewMock.
        when(presenterMock.getRootItems()).thenReturn(Collections.<Folder>emptyList());
        loadHandlerUnderTest = new SelectFolderByPathLoadHandler(folderToSelectMock, presenterMock,
                                                                 appearanceMock,
                                                                 maskableMock, announcerMock, mock(HandlerRegistration.class));
        verify(maskableMock).mask(any(String.class));
        verify(presenterMock).rootsLoaded();

        when(presenterMock.getFolderByPath(targetFolderPath)).thenReturn(folderToSelectMock);
        when(presenterMock.getRootItems()).thenReturn(Lists.newArrayList(rootPathFolderMock));
        loadHandlerUnderTest.onLoad(eventMock);

        verify(presenterMock).getSelectedFolder();
        // The onLoad method should have found folderToSelectMock in the viewMock.
        verify(presenterMock).setSelectedFolder(folderToSelectMock);
        verifyPresenterCleanup();
//        verifyNoMoreInteractions(presenterMock);
        verifyZeroInteractions(eventMock);
    }

    /**
     * Tests the scenario where the target folder has not been loaded yet (it was created by another
     * service), and its parent has already been loaded from the service but not yet loaded into the
     * view's TreeStore (i.e. it's been cached by the service facade).
     */
    @Test public void testLoad_ParentCached_TargetNew() {
        Folder targetFolderParent = initMockFolder(targetFolderParentPath, "targetFolderParent");
        Folder rootPathFolderMock = initMockFolder(rootPath, "rootPathFolderMock");

        // Start with the target's parent and its children already loaded in the treeStoreMock, but not
        // the target.
        when(presenterMock.getRootItems()).thenReturn(Lists.newArrayList(rootPathFolderMock));
        when(presenterMock.getFolderByPath(targetFolderPath)).thenReturn(null);
        when(presenterMock.getFolderByPath(targetFolderParentPath)).thenReturn(targetFolderParent);
        when(presenterMock.isLoaded(targetFolderParent)).thenReturn(true);

        // Since deferred commands can't be tested, the refreshFolder method will be overridden to ensure
        // the DiskResourceView.Presenter#onFolderRefreshed method is called.
        loadHandlerUnderTest = new SelectFolderByPathLoadHandler(folderToSelectMock, presenterMock,
                                                                 appearanceMock,
                                                                 maskableMock, announcerMock) {
            @Override
            void refreshFolder(final Folder folder) {
                presenterMock.reloadTreeStoreFolderChildren(folder);
            }
        };
        loadHandlerUnderTest.setHandlerRegistration(mock(HandlerRegistration.class));
        verifyPresenterInit();
        verify(presenterMock, times(2)).getFolderByPath(anyString());
        verify(presenterMock).isLoaded(eq(targetFolderParent));

        // The handler's constructor should call presenterMock#onFolderRefreshed on targetFolderParentPath.
        verify(presenterMock).reloadTreeStoreFolderChildren(targetFolderParent);

        when(eventMock.getLoadConfig()).thenReturn(targetFolderParent);
        when(presenterMock.getFolderByPath(targetFolderPath)).thenReturn(folderToSelectMock);
        loadHandlerUnderTest.onLoad(eventMock);
        verify(presenterMock, times(3)).getFolderByPath(anyString());

        // The onLoad method should have found folderToSelectMock in the viewMock.
        verify(presenterMock).setSelectedFolder(folderToSelectMock);
        verifyPresenterCleanup();
        verifyNoMoreInteractions(presenterMock);
    }

    /**
     * Tests the scenario where the target folder no longer exists, but all other folders along the path
     * to the target folder have already been loaded from the service (i.e. they are already cached by
     * the service facade), but they have not yet been loaded into the view's TreeStore.
     */
    @Test public void testLoad_ParentCached_TargetDeleted() {
        Folder targetFolderParent = initMockFolder(targetFolderParentPath, "targetFolderParent");
        Folder rootPathFolderMock = initMockFolder(rootPath, "rootPathFolderMock");

        // Start with the target's parent and its children already loaded in the treeStoreMock, but not
        // the target.
        when(presenterMock.getRootItems()).thenReturn(Lists.newArrayList(rootPathFolderMock));
        when(presenterMock.getFolderByPath(targetFolderPath)).thenReturn(null);
        when(presenterMock.getFolderByPath(targetFolderParentPath)).thenReturn(targetFolderParent);
        when(presenterMock.isLoaded(targetFolderParent)).thenReturn(true);

        // Since deferred commands can't be tested, the refreshFolder method will be overridden to ensure
        // the DiskResourceView.Presenter#onFolderRefreshed method is called.
        loadHandlerUnderTest = new SelectFolderByPathLoadHandler(folderToSelectMock, presenterMock,
                                                                 appearanceMock,
                                                                 maskableMock, announcerMock) {
            @Override
            void refreshFolder(final Folder folder) {
                presenterMock.reloadTreeStoreFolderChildren(folder);
            }
        };
        loadHandlerUnderTest.setHandlerRegistration(mock(HandlerRegistration.class));
        verifyPresenterInit();
        verify(presenterMock, times(2)).getFolderByPath(anyString());
        verify(presenterMock).isLoaded(targetFolderParent);
        // The handler's constructor should call presenterMock#onFolderRefreshed on targetFolderParentPath.
        verify(presenterMock).reloadTreeStoreFolderChildren(targetFolderParent);

        when(eventMock.getLoadConfig()).thenReturn(targetFolderParent);
        when(presenterMock.getFolderByPath(targetFolderPath)).thenReturn(null);
        loadHandlerUnderTest.onLoad(eventMock);

        verify(presenterMock, times(3)).getFolderByPath(anyString());

        // Since the targetFolderParentPath was reloaded but targetFolderPath was not found in the
        // viewMock, the handler should select the target folder's parent (folderMock) and display an
        // error message.
        verify(presenterMock).setSelectedFolder(targetFolderParent);
        verify(announcerMock).schedule(any(ErrorAnnouncementConfig.class));
        verifyPresenterCleanup();
//        verifyNoMoreInteractions(presenterMock);
    }

    /**
     * The SelectFolderByPathLoadHandler constructor will mask the presenter and get a reference to its
     * view.
     */
    private void verifyPresenterInit() {
        verify(maskableMock).mask(any(String.class));
        verify(presenterMock).rootsLoaded();
        verify(presenterMock).getRootItems();
    }

    /**
     * When the SelectFolderByPathLoadHandler finishes loading and searching for the target folder, it
     * will unregister itself as a handler and unmask the presenter.
     */
    private void verifyPresenterCleanup() {
        verify(maskableMock).unmask();
    }
}
