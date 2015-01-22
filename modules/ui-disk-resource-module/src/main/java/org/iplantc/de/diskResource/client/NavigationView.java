package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.search.events.DeleteSavedSearchClickedEvent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeStyle;

/**
 * Created by jstroot on 1/21/15.
 *
 * @author jstroot
 */
public interface NavigationView extends IsWidget,
                                        FolderSelectionEvent.HasFolderSelectionEventHandlers,
                                        DeleteSavedSearchClickedEvent.HasDeleteSavedSearchClickedEventHandlers {
    interface Appearance {

        IconProvider<Folder> getIconProvider();

        TreeStyle getTreeStyle();

        String treeCollapseHoverStyle();

        String treeCollapseStyle();

        String treeCollapseToolTip();

        String headingText();
    }

    interface Presenter {

        void addFolder(Folder folder);

        void addFolder(Folder parent, Folder newChild);

        NavigationView getView();

        Folder getSelectedFolder();

        Folder getFolderById(HasId hasId);

        Folder getFolderByPath(String path);

        Folder getParentFolder(Folder child);

        void refreshFolder(Folder folder);

        void removeFolder(Folder folder);

        void setSelectedFolder(Folder folder);

        void setSelectedFolder(HasPath hasPath);

        void deSelectFolder(Folder folder);

        /**
         * ********Used by SelectFolderByPathLoadHandler*********
         *
         * @return true if the given folder is loaded.
         */
        boolean isLoaded(Folder folder);

        /**
         * Expands the given folder in the tree.
         * XXX ********Used by SelectFolderByPathLoadHandler*********
         *
         * @param folder the folder to be expanded in the tree.
         */
        void expandFolder(Folder folder);

        void deSelectAll();

        /**
         * @param el the element corresponding to a tree node
         * @return the TreeNode if it exists, null otherwise.
         */
        Tree.TreeNode<Folder> findTreeNode(Element el);


        void removeChildren(Folder folder);

        void updateQueryTemplate(DiskResourceQueryTemplate queryTemplate);
    }

    Tree<Folder, Folder> getTree();

    ToolButton getTreeCollapseButton();
}
