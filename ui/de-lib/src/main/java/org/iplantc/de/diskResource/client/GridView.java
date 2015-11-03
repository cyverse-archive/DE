package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.DiskResourceNameSelectedEvent.DiskResourceNameSelectedEventHandler;
import org.iplantc.de.diskResource.client.events.DiskResourceNameSelectedEvent.HasDiskResourceNameSelectedEventHandlers;
import org.iplantc.de.diskResource.client.events.DiskResourcePathSelectedEvent.HasDiskResourcePathSelectedEventHandlers;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent.HasDiskResourceSelectionChangedEventHandlers;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent.FolderSelectionEventHandler;
import org.iplantc.de.diskResource.client.events.RequestDiskResourceFavoriteEvent.RequestDiskResourceFavoriteEventHandler;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler;
import org.iplantc.de.diskResource.client.events.selection.CopyMetadataSelected.CopyMetadataSelectedEventHandler;
import org.iplantc.de.diskResource.client.events.selection.EditInfoTypeSelected.EditInfoTypeSelectedEventHandler;
import org.iplantc.de.diskResource.client.events.selection.ManageCommentsSelected.ManageCommentsSelectedEventHandler;
import org.iplantc.de.diskResource.client.events.selection.ManageMetadataSelected.ManageMetadataSelectedEventHandler;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelected.ManageSharingSelectedEventHandler;
import org.iplantc.de.diskResource.client.events.selection.Md5ValueClicked.Md5ValueClickedHandler;
import org.iplantc.de.diskResource.client.events.selection.ResetInfoTypeSelected.ResetInfoTypeSelectedHandler;
import org.iplantc.de.diskResource.client.events.selection.SaveMetadataSelected.SaveMetadataSelectedEventHandler;
import org.iplantc.de.diskResource.client.events.selection.ShareByDataLinkSelected.ShareByDataLinkSelectedEventHandler;
import org.iplantc.de.diskResource.client.presenters.grid.proxy.FolderContentsLoadConfig;
import org.iplantc.de.diskResource.client.views.grid.DiskResourceColumnModel;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.event.StoreUpdateEvent.HasStoreUpdateHandlers;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.container.HasLayout;
import com.sencha.gxt.widget.core.client.grid.LiveGridCheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.List;

/**
 * Created by jstroot on 1/26/15.
 * @author jstroot
 */
public interface GridView extends IsWidget,
                                  HasLayout,
                                  HasDiskResourcePathSelectedEventHandlers,
                                  FolderSelectionEventHandler,
                                  HasDiskResourceNameSelectedEventHandlers,
                                  HasDiskResourceSelectionChangedEventHandlers,
                                  SubmitDiskResourceQueryEventHandler {
    interface Appearance {

        String actionsColumnLabel();

        int actionsColumnWidth();

        String createdDateColumnLabel();

        int createdDateColumnWidth();

        String dataDragDropStatusText(int totalSelectionCount);

        String lastModifiedColumnLabel();

        int lastModifiedColumnWidth();

        int liveGridViewRowHeight();

        int liveToolItemWidth();

        String nameColumnLabel();

        int nameColumnWidth();

        String pathColumnLabel();

        int pathColumnWidth();

        String pathFieldLabel();

        int pathFieldLabelWidth();

        String pathFieldEmptyText();

        String permissionErrorMessage();

        int selectionStatusItemWidth();

        void setPagingToolBarStyle(ToolBar pagingToolBar);

        String sizeColumnLabel();

        int sizeColumnWidth();

        String gridViewEmptyText();

    }

    interface FolderContentsRpcProxy extends DataProxy<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> {
        void setHasSafeHtml(HasSafeHtml centerHeader);
    }

    interface Presenter extends DiskResourceNameSelectedEventHandler,
                                ManageSharingSelectedEventHandler,
                                ManageMetadataSelectedEventHandler,
                                CopyMetadataSelectedEventHandler,
                       SaveMetadataSelectedEventHandler,
                                ShareByDataLinkSelectedEventHandler,
                                RequestDiskResourceFavoriteEventHandler,
                                ManageCommentsSelectedEventHandler,
                                FolderSelectionEventHandler,
                                SubmitDiskResourceQueryEventHandler,
                                HasStoreUpdateHandlers<DiskResource>,
                                EditInfoTypeSelectedEventHandler,
                       ResetInfoTypeSelectedHandler,
                       Md5ValueClickedHandler {

        interface Appearance {

            String comments();

            String copy();

            String copyPasteInstructions();

            String createDataLinksError();

            String favoritesError(String message);

            String markFavoriteError();

            String metadata();

            String metadataDialogHeight();

            String metadataDialogWidth();

            String metadataFormInvalid();

            String metadataHelp();

            String removeFavoriteError();

            String retrieveStatFailed();

            String searchDataResultsHeader(String searchText, int total, double executionTime_ms);

            String searchFailure();

            String shareLinkDialogHeight();

            int shareLinkDialogTextBoxWidth();

            String shareLinkDialogWidth();

            String shareFailure();

            String shareByLinkFailure();

            String metadataOverwriteWarning(String drName);

            String metadataManageFailure();

            String commentsManageFailure();

            String copyMetadata();

            String copyMetadataSuccess();

            String copyMetadataFailure();

            String md5Checksum();

            String checksum();

        }

        void deSelectDiskResources();

        void doMoveDiskResources(Folder targetFolder, List<DiskResource> resources);

        Element findGridRow(Element eventTargetElement);

        int findGridRowIndex(Element targetRow);

        List<DiskResource> getAllDiskResources();

        List<DiskResource> getSelectedDiskResources();

        Folder getSelectedUploadFolder();

        GridView getView();

        boolean isSelectAllChecked();

        void setFilePreviewEnabled(boolean filePreviewEnabled);

        void setParentPresenter(DiskResourceView.Presenter parentPresenter);

        void setSelectedDiskResourcesById(List<? extends HasId> diskResourcesToSelect);

        void unRegisterHandler(EventHandler handler);

    }

    HandlerRegistration addBeforeLoadHandler(BeforeLoadEvent.BeforeLoadHandler<FolderContentsLoadConfig> handler);

    Element findGridRow(Element eventTargetElement);

    int findGridRowIndex(Element targetRow);

    DiskResourceColumnModel getColumnModel();

    PagingLoader<FolderContentsLoadConfig,PagingLoadResult<DiskResource>> getGridLoader();

    LiveGridCheckBoxSelectionModel getSelectionModel();

    void setSingleSelect();
}
