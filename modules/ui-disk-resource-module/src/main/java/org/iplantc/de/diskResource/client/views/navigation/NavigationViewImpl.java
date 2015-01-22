package org.iplantc.de.diskResource.client.views.navigation;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.search.events.DeleteSavedSearchClickedEvent;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.views.navigation.cells.TreeCell;
import org.iplantc.de.diskResource.share.DiskResourceModule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import static com.sencha.gxt.core.client.Style.SelectionMode.SINGLE;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.dnd.core.client.DND;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;
import com.sencha.gxt.widget.core.client.tree.TreeView;


/**
 * Created by jstroot on 1/21/15.
 * @author jstroot
 */
public class NavigationViewImpl extends Composite implements NavigationView {

    /**
     * Handles folder selections from the tree
     */
    private static class FolderSelectionHandler implements SelectionHandler<Folder> {
        private final IsWidget isWidget;
        private final Tree<Folder, Folder> tree;

        public FolderSelectionHandler(IsWidget isWidget, Tree<Folder, Folder> tree) {
            this.isWidget = isWidget;
            this.tree = tree;
        }

        @Override
        public void onSelection(SelectionEvent<Folder> event) {
            final Folder selectedItem = event.getSelectedItem();
            if(!isWidget.asWidget().isAttached()){
                return;
            }

            if(selectedItem.isFilter()){
                isWidget.asWidget().fireEvent(new FolderSelectionEvent(selectedItem));
            } else {
                tree.getSelectionModel().deselect(selectedItem);
            }
        }
    }

    /**
     * Performs action when the tree collapse button is clicked.
     */
    private static class TreeCollapseButtonSelectHandler implements SelectEvent.SelectHandler {
        private final Tree<Folder, Folder> tree;

        public TreeCollapseButtonSelectHandler(Tree<Folder, Folder> tree) {
            this.tree = tree;
        }

        @Override
        public void onSelect(SelectEvent event) {
            tree.collapseAll();
        }
    }

    interface NavigationViewImplUiBinder extends UiBinder<ContentPanel, NavigationViewImpl> { }

    private final class CustomTreeView extends TreeView<Folder> {

        @Override
        public void onTextChange(Tree.TreeNode<Folder> node, SafeHtml text) {
            Element textEl = getTextElement(node);
            if (textEl != null) {
                Folder folder = node.getModel();
                if (!folder.isFilter()) {
                    textEl.setInnerHTML(Util.isEmptyString(text.asString()) ? "&#160;" : text.asString());
                } else {
                    textEl.setInnerHTML(Util.isEmptyString(text.asString()) ? "&#160;" : "<span style='color:red;font-style:italic;'>" + text.asString() + "</span>");
                }
            }
        }
    }

    private static NavigationViewImplUiBinder ourUiBinder = GWT.create(NavigationViewImplUiBinder.class);

    @UiField Tree<Folder, Folder> tree;
    @UiField ContentPanel container;
    private final ToolButton treeCollapseButton;
    private final TreeStore<Folder> treeStore;
    private final TreeLoader<Folder> treeLoader;
    @UiField(provided = true) Appearance appearance;

    @Inject
    NavigationViewImpl(final NavigationView.Appearance appearance,
                       @Assisted final TreeStore<Folder> treeStore,
                       @Assisted final TreeLoader<Folder> treeLoader) {
        this.treeStore = treeStore;
        this.treeLoader = treeLoader;
        this.appearance = appearance;
        initWidget(ourUiBinder.createAndBindUi(this));

        DropTarget treeDropTarget = new DropTarget(tree);
        treeDropTarget.setAllowSelfAsSource(true);
        treeDropTarget.setOperation(DND.Operation.COPY);
        // FIXME Complete wiring up of dnd handler
//        treeDropTarget.addDragEnterHandler(dndHandler);
//        treeDropTarget.addDragMoveHandler(dndHandler);
//        treeDropTarget.addDropHandler(dndHandler);
//
//        DragSource treeDragSource = new DragSource(tree);
//        treeDragSource.addDragStartHandler(dndHandler);



        // Create TreeCollapseButton
        treeCollapseButton = new ToolButton(new IconButton.IconConfig(appearance.treeCollapseStyle(),
                                                                      appearance.treeCollapseHoverStyle()));
        treeCollapseButton.setToolTip(appearance.treeCollapseToolTip());
        treeCollapseButton.addSelectHandler(new TreeCollapseButtonSelectHandler(tree));
        container.getHeader().removeTool(container.getHeader().getTool(0));
        container.getHeader().addTool(treeCollapseButton);
    }

    @Override
    public HandlerRegistration addDeleteSavedSearchClickedEventHandler(DeleteSavedSearchClickedEvent.DeleteSavedSearchEventHandler handler) {
        return addHandler(handler, DeleteSavedSearchClickedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addFolderSelectedEventHandler(FolderSelectionEvent.FolderSelectionEventHandler handler) {
        return addHandler(handler, FolderSelectionEvent.TYPE);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        treeCollapseButton.setId("idTreeCollapse");
        tree.ensureDebugId(baseID + DiskResourceModule.Ids.NAVIGATION);
    }

    @Override
    public Tree<Folder, Folder> getTree() {
        return tree;
    }

    @Override
    public ToolButton getTreeCollapseButton() {
        return treeCollapseButton;
    }

    @UiFactory
    Tree<Folder, Folder> createTree() {
        final Tree<Folder, Folder> folderFolderTree = new Tree<>(treeStore, new IdentityValueProvider<Folder>());
        folderFolderTree.setView(new CustomTreeView());
        folderFolderTree.setLoader(treeLoader);
        folderFolderTree.setIconProvider(appearance.getIconProvider());
        tree.setStyle(appearance.getTreeStyle());
        final TreeSelectionModel<Folder> selectionModel = folderFolderTree.getSelectionModel();
        selectionModel.setSelectionMode(SINGLE);
        selectionModel.addSelectionHandler(new FolderSelectionHandler(this, tree));

        final TreeCell treeCell = new TreeCell(tree);
        treeCell.setHasHandlers(this);
        treeCell.setSelectionModel(selectionModel);

        folderFolderTree.setCell(treeCell);

        new QuickTip(tree);
        return folderFolderTree;
    }
}