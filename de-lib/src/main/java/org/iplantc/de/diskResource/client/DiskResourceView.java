package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent.HasDiskResourceSelectionChangedEventHandlers;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent.HasFolderSelectionEventHandlers;
import org.iplantc.de.diskResource.client.events.RootFoldersRetrievedEvent.HasRootFoldersRetrievedEventHandlers;
import org.iplantc.de.diskResource.client.events.SavedSearchesRetrievedEvent.HasSavedSearchesRetrievedEventHandlers;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent.HasSubmitDiskResourceQueryEventHandlers;
import org.iplantc.de.diskResource.client.events.selection.RefreshFolderSelected;
import org.iplantc.de.diskResource.client.presenters.navigation.proxy.SelectFolderByPathLoadHandler;

import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.TreeLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jstroot
 */
public interface DiskResourceView extends IsWidget,
                                          IsMaskable {

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
                                            HasRootFoldersRetrievedEventHandlers,
                                            HasSavedSearchesRetrievedEventHandlers {
        void setMaskable(IsMaskable maskable);
    }


    interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter,
                                IsMaskable,
                                HasDiskResourceSelectionChangedEventHandlers,
                                HasFolderSelectionEventHandlers,
                                RefreshFolderSelected.RefreshFolderSelectedHandler {

        interface Appearance {

            String createFolderLoadingMask();

            String deleteMsg();

            String deleteTrash();

            String diskResourceIncompleteMove();

            String duplicateCheckFailed();

            String emptyTrash();

            String emptyTrashWarning();

            String fileName();

            String fileUploadSuccess(String filename);

            String fileUploadsFailed(ArrayList<String> files);

            String folderName();

            String idParentInvalid();

            String importFailed(String sourceUrl);

            String loadingMask();

            String moveDiskResourcesLoadingMask();

            String permissionErrorMessage();

            String rename();

            String renameDiskResourcesLoadingMask();

            String folderRefreshFailed(String folder);

            String unsupportedCogeInfoType();

            String unsupportedEnsemblInfoType();

            String unsupportedTreeInfoType();

            String warning();

            String details();
        }

        String FAVORITES_FOLDER_NAME = "Favorites";
        String FAVORITES_FOLDER_PATH = "/favorites";

        /**
         * Method to clean up all the events when it is no longer required.
         */
        void cleanUp();

        Folder convertToFolder(DiskResource selectedItem);

        void deSelectDiskResources();

        void doCreateNewFolder(Folder parentFolder, String folderName);

        public void onCreateNcbiSraFolderStructure(Folder selectedFolder,
                                                   String projectName,
                                                   Integer numSample,
                                                   Integer numlibs);

        void doMoveDiskResources(Folder targetFolder, List<DiskResource> resources);

        void doRenameDiskResource(DiskResource diskResource, String newName);

        List<DiskResource> getSelectedDiskResources();

        Folder getSelectedFolder();

        void go(HasOneWidget container, HasPath folderToSelect,
                List<? extends HasId> diskResourcesToSelect);

        void selectTrashFolder();

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

    }

    void setEastWidgetHidden(boolean hideEastWidget);

    void setNorthWidgetHidden(boolean hideNorthWidget);

    void setSouthWidget(IsWidget fl);

    void setSouthWidget(IsWidget fl, double size);

}
