package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.events.RootFoldersRetrievedEvent;
import org.iplantc.de.diskResource.client.events.SavedSearchesRetrievedEvent;
import org.iplantc.de.diskResource.client.events.selection.EditInfoTypeSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelected;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsLoadConfig;
import org.iplantc.de.diskResource.client.presenters.proxy.SelectFolderByPathLoadHandler;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent.HasSubmitDiskResourceQueryEventHandlers;
import org.iplantc.de.tags.client.Taggable;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.TreeLoader;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author jstroot
 */
public interface DiskResourceView extends IsWidget,
                                          IsMaskable,
                                          Taggable,
                                          ManageSharingSelected.HasManageSharingSelectedEventHandlers,
                                          DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler,
                                          EditInfoTypeSelected.HasEditInfoTypeSelectedEventHandlers {

    interface DiskResourceViewToolbarAppearance {

        ImageResource newPathListMenuIcon();

        String newPathListMenuText();
    }

    interface FolderContentsRpcProxy extends DataProxy<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> {
        void setHasSafeHtml(HasSafeHtml centerHeader);
    }

    /**
     * A dataproxy used by the <code>Presenter</code> to fetch <code>DiskResource</code> data from the
     * {@link DiskResourceServiceFacade}.
     * When the proxy completes a load of a non-root folder, it is expected to call the
     * link DiskResourceView.PresenteronFolderLoad(Folder, ArrayList)method with the <code>Folder</code>
     * and <code>File</code> contents of the loaded folder.
     *
     * @author jstroot
     */
    public interface FolderRpcProxy extends DataProxy<Folder, List<Folder>>,
                                            HasSubmitDiskResourceQueryEventHandlers,
                                            RootFoldersRetrievedEvent.HasRootFoldersRetrievedEventHandlers,
                                            SavedSearchesRetrievedEvent.HasSavedSearchesRetrievedEventHandlers {
        void setMaskable(IsMaskable maskable);
    }


    interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter,
                                IsMaskable,
                                DiskResourceSelectionChangedEvent.HasDiskResourceSelectionChangedEventHandlers,
                                FolderSelectionEvent.HasFolderSelectionEventHandlers,
                                DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler {

        String FAVORITES_FOLDER_NAME = "Favorites";
        String FAVORITES_FOLDER_PATH = "/favorites";

        void attachTag(IplantTag tag);

        boolean canDragDataToTargetFolder(Folder targetFolder, Collection<DiskResource> dropData);

        /**
         * Method to clean up all the events when it is no longer required.
         */
        void cleanUp();

        Folder convertToFolder(DiskResource selectedItem);

        void deSelectDiskResources();

        void detachTag(IplantTag tag);

        void disableFilePreview();

        void displayAndCacheDiskResourceInfo(DiskResource info);

        void doCreateNewFolder(Folder parentFolder, String folderName);

        void doMoveDiskResources(Folder targetFolder, List<DiskResource> resources);

        void doRenameDiskResource(DiskResource diskResource, String newName);

        void doSearchTaggedWithResources(Set<IplantTag> tags);

        void getDetails(DiskResource next);

        List<DiskResource> getSelectedDiskResources();

        Folder getSelectedFolder();

        void getTagsForSelectedResource();

        DiskResourceView getView();

        void go(HasOneWidget container, HasPath folderToSelect,
                List<? extends HasId> diskResourcesToSelect);

        void resetInfoType();

        void selectTrashFolder();

        void sendSelectedResourceToEnsembl();

        void sendSelectedResourcesToCoge();

        void sendSelectedResourcesToTreeViewer();

        void setSelectedDiskResourcesById(List<? extends HasId> selectedDiskResources);

        /**
         * Selects the folder with the given path in the view. If the given path is not yet loaded in the
         * view, a {@link SelectFolderByPathLoadHandler} is added to the view's corresponding
         * {@link TreeLoader}, then a remote load is triggered.
         *
         * @param folderToSelect the folder to be selected
         */
        void setSelectedFolderByPath(HasPath folderToSelect);

        void setViewDebugId(String baseID);

        void unmaskVizMenuOptions();
    }


    void maskDetailsPanel();

    void maskSendToCoGe();

    void maskSendToEnsembl();

    void maskSendToTreeViewer();

    void resetDetailsPanel();

    void setEastWidgetHidden(boolean hideEastWidget);

    void setNorthWidgetHidden(boolean hideNorthWidget);

    void setSouthWidget(IsWidget fl);

    void setSouthWidget(IsWidget fl, double size);

    void unmaskDetailsPanel();

    void unmaskSendToCoGe();

    void unmaskSendToEnsembl();

    void unmaskSendToTreeViewer();

    void updateDetails(DiskResource info);

    void updateTags(List<IplantTag> tags);
}
