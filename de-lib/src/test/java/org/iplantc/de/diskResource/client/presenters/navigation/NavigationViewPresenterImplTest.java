package org.iplantc.de.diskResource.client.presenters.navigation;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.diskResources.FolderRefreshedEvent;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.events.RequestImportFromUrlEvent;
import org.iplantc.de.diskResource.client.events.RequestSimpleUploadEvent;
import org.iplantc.de.diskResource.client.events.selection.ImportFromUrlSelected;
import org.iplantc.de.diskResource.client.events.selection.SimpleUploadSelected;
import org.iplantc.de.diskResource.client.gin.factory.NavigationViewFactory;
import org.iplantc.de.diskResource.client.presenters.grid.proxy.FolderContentsLoadConfig;
import org.iplantc.de.diskResource.client.views.navigation.NavigationViewDnDHandler;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;

/**
 * @author jstroot
 */
@RunWith(GxtMockitoTestRunner.class)
public class NavigationViewPresenterImplTest {
    @Mock NavigationView.Appearance appearanceMock;
    @Mock DiskResourceUtil diskResourceUtilMock;
    @Mock DiskResourceView.FolderRpcProxy folderRpcProxyMock;
    @Mock TreeStore<Folder> treeStoreMock;
    @Mock NavigationPresenterImpl uut;
    @Mock NavigationViewFactory viewFactoryMock;
    @Mock NavigationView viewMock;
    @Mock EventBus eventBusMock;

    @Mock BeforeLoadEvent<FolderContentsLoadConfig> beforeLoadEventMock;
    @Mock Tree<Folder,Folder> treeMock;
    @Mock TreeSelectionModel<Folder> selectionModelMock;

    public NavigationViewPresenterImplTest() {
    }

    @Before public void setUp() {
        when(viewFactoryMock.create(any(TreeStore.class), any(TreeLoader.class), any(NavigationViewDnDHandler.class))).thenReturn(viewMock);
        when(viewMock.getTree()).thenReturn(treeMock);
        when(treeMock.getSelectionModel()).thenReturn(selectionModelMock);
        uut = new NavigationPresenterImpl(viewFactoryMock, treeStoreMock, folderRpcProxyMock, diskResourceUtilMock, eventBusMock, appearanceMock);

        verifyConstructor(uut);
    }

    private void verifyConstructor(NavigationPresenterImpl uut) {
        verify(viewMock).addFolderSelectedEventHandler(eq(uut));
        verify(viewMock).getTree();
        verify(eventBusMock, times(5)).addHandler(Matchers.<GwtEvent.Type<NavigationPresenterImpl>>any(), eq(uut));
    }

    @Test public void onBeforeLoad_folderNotNull_contentsNotInCurrentFolder_loadCancelled() {
        FolderContentsLoadConfig loadConfigMock = mock(FolderContentsLoadConfig.class);
        Folder loadConfigFolderMock = mock(Folder.class);
        Folder currSelectedFolderMock = mock(Folder.class);
        String loadConfigIdMock = "mock config id";
        String currFolderIdMock = "mock curr id";
        when(loadConfigFolderMock.getId()).thenReturn(loadConfigIdMock);
        when(currSelectedFolderMock.getId()).thenReturn(currFolderIdMock);

        when(loadConfigMock.getFolder()).thenReturn(loadConfigFolderMock);
        when(beforeLoadEventMock.getLoadConfig()).thenReturn(loadConfigMock);
        when(selectionModelMock.getSelectedItem()).thenReturn(currSelectedFolderMock);

        /** CALL METHOD UNDER TEST **/
        uut.onBeforeLoad(beforeLoadEventMock);

        verify(viewMock, times(3)).getTree();

        verify(beforeLoadEventMock).getLoadConfig();
        verify(loadConfigMock).getFolder();

        verify(beforeLoadEventMock).setCancelled(eq(true));

        verifyNoMoreInteractions(beforeLoadEventMock,
                                 viewMock,
                                 eventBusMock);
    }

    @Test public void onBeforeLoad_folderNotNull_contentsInCurrentFolder_loadNotCancelled() {
        FolderContentsLoadConfig loadConfigMock = mock(FolderContentsLoadConfig.class);
        Folder loadConfigFolderMock = mock(Folder.class);
        Folder currSelectedFolderMock = mock(Folder.class);
        String currFolderIdMock = "mock curr id";
        when(loadConfigFolderMock.getId()).thenReturn(currFolderIdMock);
        when(currSelectedFolderMock.getId()).thenReturn(currFolderIdMock);

        when(loadConfigMock.getFolder()).thenReturn(loadConfigFolderMock);
        when(beforeLoadEventMock.getLoadConfig()).thenReturn(loadConfigMock);
        when(selectionModelMock.getSelectedItem()).thenReturn(currSelectedFolderMock);

        /** CALL METHOD UNDER TEST **/
        uut.onBeforeLoad(beforeLoadEventMock);

        verify(viewMock, times(3)).getTree();

        verify(beforeLoadEventMock).getLoadConfig();
        verify(loadConfigMock).getFolder();

        verify(beforeLoadEventMock, never()).setCancelled(anyBoolean());

        verifyNoMoreInteractions(beforeLoadEventMock,
                                 viewMock,
                                 eventBusMock);
    }

    @Test public void onImportFromUrlSelected_selectedFolderNull_defaultUploadFolderUsed() {
        final Folder uploadFolderMock = mock(Folder.class);
        final NavigationPresenterImpl spy = spy(new NavigationPresenterImpl(viewFactoryMock, treeStoreMock, folderRpcProxyMock, diskResourceUtilMock, eventBusMock, appearanceMock) {
            @Override
            public Folder getSelectedUploadFolder() {
                return uploadFolderMock;
            }
        });
        verify(viewMock, times(2)).addFolderSelectedEventHandler(Matchers.<FolderSelectionEvent.FolderSelectionEventHandler>any());
        verify(viewMock, times(2)).getTree();
        verify(eventBusMock, times(10)).addHandler(Matchers.<GwtEvent.Type<NavigationPresenterImpl>>any(), Matchers.<NavigationPresenterImpl>any());
        ImportFromUrlSelected eventMock = mock(ImportFromUrlSelected.class);

        /** CALL METHOD UNDER TEST **/
        spy.onImportFromUrlSelected(eventMock);

        verify(eventMock).getSelectedFolder();
        verify(spy).getSelectedUploadFolder();

        ArgumentCaptor<RequestImportFromUrlEvent> captor = ArgumentCaptor.forClass(RequestImportFromUrlEvent.class);
        verify(eventBusMock).fireEvent(captor.capture());

        assertEquals(uploadFolderMock, captor.getValue().getDestinationFolder());

        verifyNoMoreInteractions(viewMock,
                                 eventMock,
                                 eventBusMock);
    }

    @Test public void onImportFromUrlSelected_selectedFolderExists_selectedFolderUsed() {
        final Folder uploadFolderMock = mock(Folder.class);
        final NavigationPresenterImpl spy = spy(new NavigationPresenterImpl(viewFactoryMock, treeStoreMock, folderRpcProxyMock, diskResourceUtilMock, eventBusMock, appearanceMock) {
            @Override
            public Folder getSelectedUploadFolder() {
                return mock(Folder.class);
            }
        });
        verify(viewMock, times(2)).addFolderSelectedEventHandler(Matchers.<FolderSelectionEvent.FolderSelectionEventHandler>any());
        verify(viewMock, times(2)).getTree();
        verify(eventBusMock, times(10)).addHandler(Matchers.<GwtEvent.Type<NavigationPresenterImpl>>any(), Matchers.<NavigationPresenterImpl>any());
        ImportFromUrlSelected eventMock = mock(ImportFromUrlSelected.class);
        when(eventMock.getSelectedFolder()).thenReturn(uploadFolderMock);

        /** CALL METHOD UNDER TEST **/
        spy.onImportFromUrlSelected(eventMock);

        verify(eventMock).getSelectedFolder();
        verify(spy, never()).getSelectedUploadFolder();

        ArgumentCaptor<RequestImportFromUrlEvent> captor = ArgumentCaptor.forClass(RequestImportFromUrlEvent.class);
        verify(eventBusMock).fireEvent(captor.capture());

        assertEquals(uploadFolderMock, captor.getValue().getDestinationFolder());

        verifyNoMoreInteractions(viewMock,
                                 eventMock,
                                 eventBusMock);
    }

  @Test public void onSimpleUploadSelected_selectedFolderNull_defaultUploadFolderUsed() {
      final Folder uploadFolderMock = mock(Folder.class);
      when(uploadFolderMock.getPath()).thenReturn("mock/path");
      final NavigationPresenterImpl spy = spy(new NavigationPresenterImpl(viewFactoryMock, treeStoreMock, folderRpcProxyMock, diskResourceUtilMock, eventBusMock, appearanceMock) {
          @Override
          public Folder getSelectedUploadFolder() {
              return uploadFolderMock;
          }
      });
      verify(viewMock, times(2)).addFolderSelectedEventHandler(Matchers.<FolderSelectionEvent.FolderSelectionEventHandler>any());
      verify(viewMock, times(2)).getTree();
      verify(eventBusMock, times(10)).addHandler(Matchers.<GwtEvent.Type<NavigationPresenterImpl>>any(), Matchers.<NavigationPresenterImpl>any());
      SimpleUploadSelected eventMock = mock(SimpleUploadSelected.class);

      /** CALL METHOD UNDER TEST **/
      spy.onSimpleUploadSelected(eventMock);

      verify(eventMock).getSelectedFolder();
      verify(spy).getSelectedUploadFolder();

      ArgumentCaptor<RequestSimpleUploadEvent> captor = ArgumentCaptor.forClass(RequestSimpleUploadEvent.class);
      verify(eventBusMock).fireEvent(captor.capture());

      assertEquals(uploadFolderMock, captor.getValue().getDestinationFolder());

      verifyNoMoreInteractions(viewMock,
                               eventMock,
                               eventBusMock);
    }

    @Test public void onSimpleUploadSelected_selectedFolderExists_selectedFolderUsed() {
        final Folder uploadFolderMock = mock(Folder.class);
        when(uploadFolderMock.getPath()).thenReturn("mock/path");
        final NavigationPresenterImpl spy = spy(new NavigationPresenterImpl(viewFactoryMock, treeStoreMock, folderRpcProxyMock, diskResourceUtilMock, eventBusMock, appearanceMock) {
            @Override
            public Folder getSelectedUploadFolder() {
                return mock(Folder.class);
            }
        });
        verify(viewMock, times(2)).addFolderSelectedEventHandler(Matchers.<FolderSelectionEvent.FolderSelectionEventHandler>any());
        verify(viewMock, times(2)).getTree();
        verify(eventBusMock, times(10)).addHandler(Matchers.<GwtEvent.Type<NavigationPresenterImpl>>any(), Matchers.<NavigationPresenterImpl>any());
        SimpleUploadSelected eventMock = mock(SimpleUploadSelected.class);
        when(eventMock.getSelectedFolder()).thenReturn(uploadFolderMock);

        /** CALL METHOD UNDER TEST **/
        spy.onSimpleUploadSelected(eventMock);

        verify(eventMock).getSelectedFolder();
        verify(spy, never()).getSelectedUploadFolder();

        ArgumentCaptor<RequestSimpleUploadEvent> captor = ArgumentCaptor.forClass(RequestSimpleUploadEvent.class);
        verify(eventBusMock).fireEvent(captor.capture());

        assertEquals(uploadFolderMock, captor.getValue().getDestinationFolder());

        verifyNoMoreInteractions(viewMock,
                                 eventMock,
                                 eventBusMock);
    }

    @Test public void onRequestFolderRefresh_methodCalled() {
        final NavigationPresenterImpl spy = spy(new NavigationPresenterImpl(viewFactoryMock, treeStoreMock, folderRpcProxyMock, diskResourceUtilMock, eventBusMock, appearanceMock) {
            @Override
            public void reloadTreeStoreFolderChildren(Folder folder) {
            }
        });
        verify(viewMock, times(2)).addFolderSelectedEventHandler(Matchers.<FolderSelectionEvent.FolderSelectionEventHandler>any());
        verify(viewMock, times(2)).getTree();
        verify(eventBusMock, times(10)).addHandler(Matchers.<GwtEvent.Type<NavigationPresenterImpl>>any(), Matchers.<NavigationPresenterImpl>any());
        FolderRefreshedEvent eventMock = mock(FolderRefreshedEvent.class);
        Folder folderMock = mock(Folder.class);
        when(eventMock.getFolder()).thenReturn(folderMock);

        /** CALL METHOD UNDER TEST **/
        spy.onFolderRefreshed(eventMock);

        verify(eventMock).getFolder();
        verify(spy).reloadTreeStoreFolderChildren(eq(folderMock));

        verifyNoMoreInteractions(viewMock,
                                 eventMock,
                                 eventBusMock);
    }


}
