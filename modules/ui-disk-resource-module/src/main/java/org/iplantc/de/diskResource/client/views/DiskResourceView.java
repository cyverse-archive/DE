package org.iplantc.de.diskResource.client.views;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceInfo;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.diskResource.client.events.FolderSelectedEvent;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsLoadConfig;
import org.iplantc.de.diskResource.client.search.events.DeleteSavedSearchEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent.HasSubmitDiskResourceQueryEventHandlers;
import org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter;
import org.iplantc.de.diskResource.client.views.widgets.DiskResourceViewToolbar;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author jstroot
 *
 */
public interface DiskResourceView extends IsWidget, IsMaskable, IsDiskResourceRoot, FolderSelectedEvent.HasFolderSelectedEventHandlers, DeleteSavedSearchEvent.HasDeleteSavedSearchEventHandlers {

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter,
            DiskResourceViewToolbar.Presenter, HasHandlerRegistrationMgmt, FolderSelectedEvent.FolderSelectedEventHandler, IsMaskable {
        interface Builder extends org.iplantc.de.commons.client.presenter.Presenter {
            Builder hideNorth();

            Builder hideWest();

            Builder hideCenter();

            Builder hideEast();

            Builder singleSelect();

            Builder disableFilePreview();
        }

        void go(HasOneWidget container, HasId folderToSelect, List<? extends HasId> diskResourcesToSelect);

        /**
         * Method to clean up all the events when it is no longer required.
         */
        void cleanUp();

        Builder builder();

        /**
         * Method called by the view when a folder is selected.
         * Whenever this method is called with a non-null and non-empty list, the presenter will have the
         * view de-select all disk resources
         * in the center panel.
         *
         * @param folders
         */
        void onFolderSelected(Folder folders);

        void onDiskResourceSelected(Set<DiskResource> selection);

        void addFileSelectChangedHandler(SelectionChangedHandler<DiskResource> selectionChangedHandler);

        void addFolderSelectionHandler(SelectionHandler<Folder> selectionHandler);

        /**
         * Selects the folder with the given Id by adding a {@link org.iplantc.de.diskResource.client.presenters.proxy.SelectFolderByIdLoadHandler} to the
         * view's corresponding {@link TreeLoader}, then triggering a remote load.
         *
         * @param folderId
         */
        void setSelectedFolderById(HasId folderToSelect);

        /**
         * Sets the selected disk resource with the given ids.
         *
         * @param diskResourceIdList
         */
//        void setSelectedDiskResourcesById(Set<String> diskResourceIdList);

        DiskResourceView getView();

        void doMoveDiskResources(Folder targetFolder, Set<DiskResource> resources);

        /**
         * A convenience method for looking up drop target folders for View components
         * @param widget
         * @param el
         * @return
         */
        Folder getDropTargetFolder(IsWidget widget, Element el);

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

        boolean canDragDataToTargetFolder(Folder targetFolder, Collection<DiskResource> dropData);

        void deSelectDiskResources();

        void setSelectedDiskResourcesById(List<? extends HasId> selectedDiskResources);

        void OnInfoTypeClick(String id, String infoType);

        Set<? extends DiskResource> getDragSources(IsWidget source, Element dragStartEl);

        void updateSortInfo(SortInfo sortInfo);

		void resetInfoType();
		
		
    }

    /**
     * A dataproxy used by the <code>Presenter</code> to fetch <code>DiskResource</code> data from the
     * {@link DiskResourceServiceFacade}.
     * When the proxy completes a load of a non-root folder, it is expected to call the
     * {@link DiskResourceView.Presenter#onFolderLoad(Folder, ArrayList)} method with the
     * <code>Folder</code> and <code>File</code> contents of the loaded folder.
     *
     * @author jstroot
     *
     */
    public interface Proxy extends DataProxy<Folder, List<Folder>>, HasSubmitDiskResourceQueryEventHandlers {
        void init(DataSearchPresenter presenter, IsMaskable isMaskable);
    }

    void setPresenter(Presenter presenter);
    
    Presenter getPresenter();

    void setTreeLoader(TreeLoader<Folder> treeLoader);

    Folder getSelectedFolder();

    Set<DiskResource> getSelectedDiskResources();

    TreeStore<Folder> getTreeStore();

    boolean isLoaded(Folder folder);

    void setDiskResources(Set<DiskResource> folderChildren);

    void onFolderSelected(Folder folder);

    void onDiskResourceSelected(Set<DiskResource> selection);

    void setWestWidgetHidden(boolean hideWestWidget);

    void setCenterWidgetHidden(boolean hideCenterWidget);

    void setEastWidgetHidden(boolean hideEastWidget);

    void setNorthWidgetHidden(boolean hideNorthWidget);

    void setSouthWidget(IsWidget fl);

    void setSouthWidget(IsWidget fl, double size);

    void addDiskResourceSelectChangedHandler(SelectionChangedHandler<DiskResource> selectionChangedHandler);

    void addFolderSelectionHandler(SelectionHandler<Folder> selectionHandler);

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

    Folder getParentFolder(Folder selectedFolder);

    void expandFolder(Folder folder);

    void deSelectDiskResources();

    void refreshFolder(Folder folder);

    void removeChildren(Folder folder);

    DiskResourceViewToolbar getToolbar();

    /**
     * Removes the given <code>DiskResource</code>s from all of the view's stores.
     *
     * @param resources
     */
    <D extends DiskResource> void removeDiskResources(Collection<D> resources);

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

    void disableFilePreview();

    void showDataListingWidget();

    void updateDetails(String path, DiskResourceInfo info);

    void resetDetailsPanel();

    void deSelectNavigationFolder();

    boolean isCenterHidden();

    void unmaskDetailsPanel();

    void maskDetailsPanel();

    void setViewLoader(PagingLoader<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> gridLoader);

    boolean isSelectAll();

    int getTotalSelectionCount();

    HasSafeHtml getCenterPanelHeader();

    void setAllowSelectAll(boolean allowSelectAll);

}
