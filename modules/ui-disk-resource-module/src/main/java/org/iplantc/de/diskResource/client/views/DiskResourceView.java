package org.iplantc.de.diskResource.client.views;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceInfo;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsLoadConfig;
import org.iplantc.de.diskResource.client.presenters.proxy.SelectFolderByPathLoadHandler;
import org.iplantc.de.diskResource.client.search.events.DeleteSavedSearchEvent;
import org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent.HasSubmitDiskResourceQueryEventHandlers;
import org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;
import org.iplantc.de.diskResource.client.views.cells.events.DiskResourceNameSelectedEvent;
import org.iplantc.de.diskResource.client.views.cells.events.ManageMetadataEvent;
import org.iplantc.de.diskResource.client.views.cells.events.ManageSharingEvent;
import org.iplantc.de.diskResource.client.views.cells.events.ShareByDataLinkEvent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author jstroot
 *
 */
public interface DiskResourceView extends IsWidget,
                                          IsMaskable,
                                          IsDiskResourceRoot,
                                          FolderSelectionEvent.HasFolderSelectionEventHandlers,
                                          DeleteSavedSearchEvent.HasDeleteSavedSearchEventHandlers,
                                          DiskResourceSelectionChangedEvent.HasDiskResourceSelectionChangedEventHandlers {

    interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter,
                                IsMaskable,
                                HasHandlerRegistrationMgmt,
                                FolderSelectionEvent.FolderSelectionEventHandler,
                                DiskResourceNameSelectedEvent.DiskResourceNameSelectedEventHandler,
                                ManageMetadataEvent.ManageMetadataEventHandler,
                                ManageSharingEvent.ManageSharingEventHandler,
                                DiskResourceSelectionChangedEvent.HasDiskResourceSelectionChangedEventHandlers,
                                FolderSelectionEvent.HasFolderSelectionEventHandlers,
                                DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler,
                                SaveDiskResourceQueryEvent.SaveDiskResourceQueryEventHandler,
                                SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler,
                                ShareByDataLinkEvent.ShareByDataLinkEventHandler {
        interface Builder extends org.iplantc.de.commons.client.presenter.Presenter {
            Builder hideNorth();

            Builder hideWest();

            Builder hideCenter();

            Builder hideEast();

            Builder singleSelect();

            Builder disableFilePreview();
        }

        void createNewFolder();

        void createNewPlainTextFile();

        void deleteSelectedResources();

        void disableFilePreview();

        void doBulkDownload();

        void doBulkUpload();

        void doCreateNewFolder(Folder parentFolder, String folderName);

        void doImportFromUrl();

        void doRefreshFolder(Folder folder);

        void doRenameDiskResource(DiskResource diskResource, String newName);

        void doSimpleDownload();

        void doSimpleUpload();

        void editSelectedFile();

        void editSelectedResourceInfoType();

        void emptyTrash();

        Set<DiskResource> getSelectedDiskResources();

        Folder getSelectedFolder();

        void go(HasOneWidget container, HasPath folderToSelect,
                List<? extends HasId> diskResourcesToSelect);

        /**
         * Method to clean up all the events when it is no longer required.
         */
        void cleanUp();

        Builder builder();

        void manageSelectedResourceCollaboratorSharing();

        void manageSelectedResourceDataLinks();

        void manageSelectedResourceMetadata();

        void moveSelectedDiskResources();

        void moveSelectedDiskResourcesToTrash();

        void openNewWindow(boolean atCurrentLocation);

        void refreshSelectedFolder();

        void renameSelectedResource();

        void restoreSelectedResources();

        void selectTrashFolder();

        void sendSelectedResourceToEnsembl();

        void sendSelectedResourcesToCoge();

        void sendSelectedResourcesToTreeViewer();

        /**
         * Selects the folder with the given path in the view. If the given path is not yet loaded in the
         * view, a {@link SelectFolderByPathLoadHandler} is added to the view's corresponding
         * {@link TreeLoader}, then a remote load is triggered.
         *
         * @param folderToSelect
         */
        void setSelectedFolderByPath(HasPath folderToSelect);

        DiskResourceView getView();

        void doMoveDiskResources(Folder targetFolder, Set<DiskResource> resources);

        /**
         * A convenience method for looking up drop target folders for View components
         * @param widget
         * @param el
         * @return
         */
        Folder getDropTargetFolder(IsWidget widget, Element el);

        boolean canDragDataToTargetFolder(Folder targetFolder, Collection<DiskResource> dropData);

        void deSelectDiskResources();

        void setSelectedDiskResourcesById(List<? extends HasId> selectedDiskResources);

        void onInfoTypeClick(DiskResource dr, String infoType);

        Set<? extends DiskResource> getDragSources(IsWidget source, Element dragStartEl);

        void resetInfoType();

        void displayAndCacheDiskResourceInfo(String path, DiskResourceInfo info);
    }

    /**
     * A dataproxy used by the <code>Presenter</code> to fetch <code>DiskResource</code> data from the
     * {@link DiskResourceServiceFacade}.
     * When the proxy completes a load of a non-root folder, it is expected to call the
     * link DiskResourceView.PresenteronFolderLoad(Folder, ArrayList)method with the
     * <code>Folder</code> and <code>File</code> contents of the loaded folder.
     *
     * @author jstroot
     *
     */
    public interface Proxy extends DataProxy<Folder, List<Folder>>, HasSubmitDiskResourceQueryEventHandlers {
        void init(DataSearchPresenter presenter, IsMaskable isMaskable);
    }

    void setPresenter(Presenter presenter);

    void setTreeLoader(TreeLoader<Folder> treeLoader);

    Folder getSelectedFolder();

    Set<DiskResource> getSelectedDiskResources();

    TreeStore<Folder> getTreeStore();

    boolean isLoaded(Folder folder);

    void setDiskResources(Set<DiskResource> folderChildren);

    void setWestWidgetHidden(boolean hideWestWidget);

    void setCenterWidgetHidden(boolean hideCenterWidget);

    void setEastWidgetHidden(boolean hideEastWidget);

    void setNorthWidgetHidden(boolean hideNorthWidget);

    void setSouthWidget(IsWidget fl);

    void setSouthWidget(IsWidget fl, double size);

    /**
     * Selects the given Folder.
     * This method will also ensure that the Data listing widget is shown.
     *
     * @param folder
     */
    void setSelectedFolder(Folder folder);

    void setSelectedDiskResources(List<? extends HasId> diskResourcesToSelect);

    void addFolder(Folder parent, Folder newChild);

    Folder getFolderById(String folderId);

    Folder getFolderByPath(String path);

    Folder getParentFolder(Folder selectedFolder);

    void expandFolder(Folder folder);

    void deSelectDiskResources();

    void refreshFolder(Folder folder);

    void removeChildren(Folder folder);

    DiskResourceViewToolbar getToolbar();

    /**
     * Determines if the given widget is this view's <code>Tree</code> object.
     *
     * @param widget
     * @return
     */
    boolean isViewTree(IsWidget widget);

    /**
     * Determines if the given widget is this view's <code>Grid</code> object.
     *
     * @param widget
     * @return
     */
    boolean isViewGrid(IsWidget widget);

    TreeNode<Folder> findTreeNode(Element el);

    Element findGridRow(Element el);

    int findRowIndex(Element targetRow);

    ListStore<DiskResource> getListStore();

    void setSingleSelect();

    void showDataListingWidget();

    void updateDetails(String path, DiskResourceInfo info);

    void resetDetailsPanel();

    void deSelectNavigationFolder();

    void unmaskDetailsPanel();

    void maskDetailsPanel();

    void setViewLoader(PagingLoader<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> gridLoader);

    boolean isSelectAll();

    int getTotalSelectionCount();

    void setAllowSelectAll(boolean allowSelectAll);

    HasSafeHtml getCenterPanelHeader();

    void maskSendToCoGe();

    void unmaskSendToCoGe();

    void maskSendToEnsembl();

    void unmaskSendToEnsembl();

    void maskSendToTreeViewer();

    void unmaskSendToTreeViewer();

    interface DiskResourceViewToolbar extends IsWidget {

        void init(DiskResourceView.Presenter presenter, DiskResourceView view);

        DiskResourceSearchField getSearchField();

        void maskSendToCoGe();

        void unmaskSendToCoGe();

        void maskSendToEnsembl();

        void unmaskSendToEnsembl();

        void maskSendToTreeViewer();

        void unmaskSendToTreeViewer();
    }

    void displayAndCacheDiskResourceInfo(String path, DiskResourceInfo info);
}
