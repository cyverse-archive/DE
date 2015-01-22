package org.iplantc.de.diskResource.client.views.navigation;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;

import com.google.gwt.dom.client.Element;

import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.tree.Tree;

/**
 * Created by jstroot on 1/21/15.
 * @author jstroot
 */
public interface NavigationView extends FolderSelectionEvent.HasFolderSelectionEventHandlers {
    interface Appearance {

        String treeCollapseHoverStyle();

        String treeCollapseStyle();

        String treeCollapseToolTip();
    }
    interface Presenter {

        Folder getSelectedFolder();

        void setSelectedFolder(Folder folder);

        /**
         * ********Used by SelectFolderByPathLoadHandler*********
         * @return true if the given folder is loaded.
         */
        boolean isLoaded(Folder folder);

        /**
         * Expands the given folder in the tree.
         * XXX ********Used by SelectFolderByPathLoadHandler*********
         * @param folder the folder to be expanded in the tree.
         */
        void expandFolder(Folder folder);

        void deSelectAll();

        /**
         *
         * @param el the element corresponding to a tree node
         * @return the TreeNode if it exists, null otherwise.
         */
        Tree.TreeNode<Folder> findTreeNode(Element el);

    }

    ToolButton getTreeCollapseButton();
}
