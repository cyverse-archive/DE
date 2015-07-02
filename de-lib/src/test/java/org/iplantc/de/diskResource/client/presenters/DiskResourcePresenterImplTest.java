package org.iplantc.de.diskResource.client.presenters;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.dataLink.DataLinkFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.FileSystemMetadataServiceFacade;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.DetailsView;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.SearchView;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.client.events.search.UpdateSavedSearchesEvent;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceViewFactory;
import org.iplantc.de.diskResource.client.gin.factory.FolderContentsRpcProxyFactory;
import org.iplantc.de.diskResource.client.gin.factory.GridViewPresenterFactory;
import org.iplantc.de.diskResource.client.gin.factory.ToolbarViewPresenterFactory;
import org.iplantc.de.diskResource.client.views.search.DiskResourceSearchField;
import org.iplantc.de.resources.client.messages.IplantContextualHelpStrings;

import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.TreeStore;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.List;

/**
 * @author jstroot
 */
@RunWith(GxtMockitoTestRunner.class)
public class DiskResourcePresenterImplTest {

    @Mock DiskResourceView viewMock;
    @Mock DiskResourceViewFactory viewFactoryMock;
    @Mock DiskResourceView.FolderRpcProxy folderRpcMock;
    @Mock FolderContentsRpcProxyFactory folderContentsRpcFactoryMock;
    @Mock DiskResourceView.FolderRpcProxy folderRpcProxyMock;
    @Mock DiskResourceServiceFacade diskResourceServiceMock;
    @Mock IplantContextualHelpStrings helpStringsMock;
    @Mock DiskResourceAutoBeanFactory factoryMock;
    @Mock DataLinkFactory mockDlFactory;
    @Mock SearchView.Presenter dataSearchPresenterMock;
    @Mock EventBus eventBusMock;
    @Mock UserInfo userInfoMock;
    @Mock FileSystemMetadataServiceFacade ileSystemMetadataServiceMock;
    @Mock UpdateSavedSearchesEvent eventMock;

    @Mock IplantAnnouncer announcerMock;
    @Mock ToolbarView toolbarMock;

    @Mock DiskResourceSearchField searchFieldMock;
    @Mock TreeStore<Folder> treeStoreMock;
    @Mock NavigationView.Presenter navigationPresenterMock;
    @Mock NavigationView navigationViewMock;
    @Mock GridViewPresenterFactory GridViewPresenterFactoryMock;
    @Mock GridView.Presenter gridViewPresenterMock;
    @Mock GridView gridViewMock;
    @Mock ToolbarViewPresenterFactory toolbarPresenterFactoryMock;
    @Mock ToolbarView.Presenter toolbarPresenterMock;
    @Mock DetailsView.Presenter detailsPresenterMock;
    @Mock TYPE entityTypeMock;
    @Mock List<InfoType> infoTypeFiltersMock;
    @Mock DetailsView detailsViewMock;

    private DiskResourcePresenterImpl uut;

    // TODO: SS complete tests with new service
    @Before public void setUp() {
        setupMocks();
        uut = new DiskResourcePresenterImpl(viewFactoryMock,
                                            factoryMock,
                                            navigationPresenterMock,
                                            GridViewPresenterFactoryMock,
                                            dataSearchPresenterMock,
                                            toolbarPresenterFactoryMock,
                                            detailsPresenterMock,
                                            announcerMock,
                                            eventBusMock,
                                            infoTypeFiltersMock,
                                            entityTypeMock);
    }

    @Test public void verifyConstructorEventWiring() {
        verify(GridViewPresenterFactoryMock).create(eq(navigationPresenterMock),
                                                    eq(infoTypeFiltersMock),
                                                    eq(entityTypeMock));

        verify(navigationPresenterMock).setParentPresenter(eq(uut));
        verify(gridViewPresenterMock).setParentPresenter(eq(uut));
        verify(navigationPresenterMock).setMaskable(eq(viewMock));

        // Details
        verify(detailsViewMock).addManageSharingSelectedEventHandler(eq(gridViewPresenterMock));
        verify(detailsViewMock).addEditInfoTypeSelectedEventHandler(eq(gridViewPresenterMock));
        verify(detailsViewMock).addResetInfoTypeSelectedHandler(eq(gridViewPresenterMock));
        verify(detailsViewMock).addMd5ValueClickedHandler(eq(gridViewPresenterMock));
        verify(detailsViewMock).addSubmitDiskResourceQueryEventHandler(eq(gridViewMock));
        verify(detailsViewMock).addSubmitDiskResourceQueryEventHandler(eq(gridViewPresenterMock));
        verify(detailsViewMock).addSendToCogeSelectedHandler(eq(uut));
        verify(detailsViewMock).addSendToEnsemblSelectedHandler(eq(uut));
        verify(detailsViewMock).addSendToTreeViewerSelectedHandler(eq(uut));

        // Toolbar
        verify(toolbarMock).getSearchField();
        verify(searchFieldMock).addSaveDiskResourceQueryClickedEventHandler(eq(dataSearchPresenterMock));
        verify(searchFieldMock).addSubmitDiskResourceQueryEventHandler(eq(gridViewMock));
        verify(searchFieldMock).addSubmitDiskResourceQueryEventHandler(eq(gridViewPresenterMock));
        verify(toolbarMock).addDeleteSelectedDiskResourcesSelectedEventHandler(eq(uut));
        verify(toolbarMock).addDeleteSelectedDiskResourcesSelectedEventHandler(eq(uut));
        verify(toolbarMock).addEditInfoTypeSelectedEventHandler(eq(gridViewPresenterMock));
        verify(toolbarMock).addEmptyTrashSelectedHandler(eq(uut));
        verify(toolbarMock).addManageSharingSelectedEventHandler(eq(gridViewPresenterMock));
        verify(toolbarMock).addManageMetadataSelectedEventHandler(eq(gridViewPresenterMock));
        verify(toolbarMock).addCopyMetadataSelectedEventHandler(eq(gridViewPresenterMock));
        verify(toolbarMock).addSaveMetadataSelectedEventHandler(eq(gridViewPresenterMock));
        verify(toolbarMock).addManageCommentsSelectedEventHandler(eq(gridViewPresenterMock));
        verify(toolbarMock).addMoveDiskResourcesSelectedHandler(eq(uut));
        verify(toolbarMock).addRefreshFolderSelectedHandler(eq(uut));
        verify(toolbarMock).addRenameDiskResourceSelectedHandler(eq(uut));
        verify(toolbarMock).addRestoreDiskResourcesSelectedHandler(eq(uut));
        verify(toolbarMock).addShareByDataLinkSelectedEventHandler(eq(gridViewPresenterMock));
        verify(toolbarMock).addSendToCogeSelectedHandler(eq(uut));
        verify(toolbarMock).addSendToEnsemblSelectedHandler(eq(uut));
        verify(toolbarMock).addSendToTreeViewerSelectedHandler(eq(uut));
        verify(toolbarMock).addSimpleUploadSelectedHandler(eq(navigationPresenterMock));
        verify(toolbarMock).addImportFromUrlSelectedHandler(eq(navigationPresenterMock));


        // Grid
        verify(gridViewMock).addBeforeLoadHandler(eq(navigationPresenterMock));
        verify(gridViewMock).addDiskResourceNameSelectedEventHandler(eq(navigationPresenterMock));
        verify(gridViewMock).addDiskResourcePathSelectedEventHandler(eq(navigationPresenterMock));
        verify(gridViewMock).addDiskResourceSelectionChangedEventHandler(eq(detailsViewMock));
        verify(gridViewMock).addDiskResourceSelectionChangedEventHandler(eq(toolbarMock));
        verify(gridViewPresenterMock).addStoreUpdateHandler(eq(detailsViewMock));

        // Navigation
        verify(navigationPresenterMock).addSavedSearchedRetrievedEventHandler(eq(dataSearchPresenterMock));
        verify(navigationPresenterMock).addSubmitDiskResourceQueryEventHandler(eq(gridViewMock));
        verify(navigationPresenterMock).addSubmitDiskResourceQueryEventHandler(eq(gridViewPresenterMock));
        verify(navigationPresenterMock).addRootFoldersRetrievedEventHandler(eq(uut));
        verify(navigationViewMock).addFolderSelectedEventHandler(eq(gridViewPresenterMock));
        verify(navigationViewMock).addFolderSelectedEventHandler(eq(gridViewMock));
        verify(navigationViewMock).addFolderSelectedEventHandler(eq(toolbarMock));
        verify(navigationViewMock).addFolderSelectedEventHandler(eq(searchFieldMock));
        verify(navigationViewMock).addDeleteSavedSearchClickedEventHandler(eq(dataSearchPresenterMock));

        // Search
        verify(dataSearchPresenterMock).addUpdateSavedSearchesEventHandler(eq(navigationPresenterMock));
        verify(dataSearchPresenterMock).addSavedSearchDeletedEventHandler(eq(searchFieldMock));

        verify(detailsPresenterMock, times(11)).getView();
        verify(gridViewPresenterMock, times(9)).getView();
        verify(navigationPresenterMock, times(5)).getView();
        verify(toolbarPresenterMock, times(21)).getView();

        verifyNoMoreInteractions(navigationPresenterMock,
                                 gridViewPresenterMock,
                                 toolbarPresenterMock,
                                 detailsPresenterMock,
                                 navigationViewMock,
                                 gridViewMock,
                                 toolbarMock,
                                 detailsViewMock);
        verifyZeroInteractions(diskResourceServiceMock,
                               eventBusMock);

    }

    private void setupMocks() {
        when(viewFactoryMock.create(any(NavigationView.Presenter.class),
                                    any(GridView.Presenter.class),
                                    any(ToolbarView.Presenter.class),
                                    any(DetailsView.Presenter.class))).thenReturn(viewMock);
        when(GridViewPresenterFactoryMock.create(any(NavigationView.Presenter.class),
                                                 anyList(),
                                                 any(TYPE.class))).thenReturn(gridViewPresenterMock);
        when(toolbarMock.getSearchField()).thenReturn(searchFieldMock);
        when(navigationPresenterMock.getView()).thenReturn(navigationViewMock);
        when(gridViewPresenterMock.getView()).thenReturn(gridViewMock);
        when(detailsPresenterMock.getView()).thenReturn(detailsViewMock);
        when(toolbarPresenterFactoryMock.create(any(DiskResourceView.Presenter.class))).thenReturn(toolbarPresenterMock);
        when(toolbarPresenterMock.getView()).thenReturn(toolbarMock);
    }


}
