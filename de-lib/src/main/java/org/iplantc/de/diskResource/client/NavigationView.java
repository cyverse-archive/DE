package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.DiskResourceNameSelectedEvent.DiskResourceNameSelectedEventHandler;
import org.iplantc.de.diskResource.client.events.DiskResourcePathSelectedEvent.DiskResourcePathSelectedEventHandler;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent.HasFolderSelectionEventHandlers;
import org.iplantc.de.diskResource.client.events.RootFoldersRetrievedEvent.HasRootFoldersRetrievedEventHandlers;
import org.iplantc.de.diskResource.client.events.SavedSearchesRetrievedEvent.HasSavedSearchesRetrievedEventHandlers;
import org.iplantc.de.diskResource.client.events.search.DeleteSavedSearchClickedEvent.HasDeleteSavedSearchClickedEventHandlers;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent.HasSubmitDiskResourceQueryEventHandlers;
import org.iplantc.de.diskResource.client.events.search.UpdateSavedSearchesEvent.UpdateSavedSearchesHandler;
import org.iplantc.de.diskResource.client.events.selection.ImportFromUrlSelected.ImportFromUrlSelectedHandler;
import org.iplantc.de.diskResource.client.events.selection.SimpleUploadSelected.SimpleUploadSelectedHandler;
import org.iplantc.de.diskResource.client.presenters.grid.proxy.FolderContentsLoadConfig;

import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeStyle;
import com.sencha.gxt.widget.core.client.tree.TreeView;

import java.util.List;

/**
 * Created by jstroot on 1/21/15.
 *
 * @author jstroot
 */
public interface NavigationView extends IsWidget,
                                        HasFolderSelectionEventHandlers,
                                        HasDeleteSavedSearchClickedEventHandlers {
    interface Appearance {

        String dataDragDropStatusText(int size);

        IconProvider<Folder> getIconProvider();

        TreeStyle getTreeStyle(Tree.TreeAppearance appearance);

        TreeView<Folder> getTreeView();

        String headingText();

        String permissionErrorMessage();

        String treeCollapseHoverStyle();

        String treeCollapseStyle();

        String treeCollapseToolTip();

        SafeHtml treeNodeFilterText(String name);
    }

    interface Presenter extends HasSubmitDiskResourceQueryEventHandlers,
                                HasRootFoldersRetrievedEventHandlers,
                                HasSavedSearchesRetrievedEventHandlers,
                                BeforeLoadEvent.BeforeLoadHandler<FolderContentsLoadConfig>,
                                DiskResourceNameSelectedEventHandler,
                                DiskResourcePathSelectedEventHandler,
                                UpdateSavedSearchesHandler,
                                ImportFromUrlSelectedHandler,
                                SimpleUploadSelectedHandler {

        interface Appearance {

            String diskResourceDoesNotExist(String folderName);

            String retrieveFolderInfoFailed();

            String savedFiltersRetrievalFailure();
        }

        void addFolder(Folder folder);

        void cleanUp();

        void doMoveDiskResources(Folder targetFolder, List<DiskResource> dropData);

        /**
         * Expands the given folder in the tree.
         * XXX ********Used by SelectFolderByPathLoadHandler*********
         *
         * @param folder the folder to be expanded in the tree.
         */
        void expandFolder(Folder folder);

        /**
         * @param el the element corresponding to a tree node
         * @return the TreeNode if it exists, null otherwise.
         */
        Tree.TreeNode<Folder> findTreeNode(Element el);

        Folder getFolderByPath(String path);

        Iterable<Folder> getRootItems();

        Folder getSelectedFolder();

        Folder getSelectedUploadFolder();

        NavigationView getView();

        /**
         * ********Used by SelectFolderByPathLoadHandler*********
         *
         * @return true if the given folder is loaded.
         */
        boolean isLoaded(Folder folder);

        /**
         * Reloads the child folders under the given Folder for this view's TreeStore only.
         * May trigger a re-selection of the currently selected folder.
         *
         * @param folder The folder that has been refreshed from the service.
         */
        void reloadTreeStoreFolderChildren(Folder folder);

        boolean rootsLoaded();

        void setMaskable(IsMaskable maskable);

        // FIXME Potentially do this via assisted inject
        void setParentPresenter(DiskResourceView.Presenter parentPresenter);

        /**
         * Selects the given folder, if the folder exists in the store.
         * This method should trigger a {@link org.iplantc.de.diskResource.client.events.FolderSelectionEvent},
         * even if the given folder is already selected.
         *
         * @param folder the folder to be selected.
         */
        void setSelectedFolder(Folder folder);

        /**
         * Selects the folder which corresponds to the given path, if the folder exists in the store.
         * This method should trigger a {@link org.iplantc.de.diskResource.client.events.FolderSelectionEvent},
         * even if the given folder is already selected.
         *
         * @param hasPath the path of the folder to be selected.
         */
        void setSelectedFolder(HasPath hasPath);
    }

    Tree<Folder, Folder> getTree();

}
