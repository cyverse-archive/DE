package org.iplantc.de.diskResource.client.views.navigation;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.events.search.DeleteSavedSearchClickedEvent;
import org.iplantc.de.diskResource.client.views.navigation.cells.TreeCell;
import org.iplantc.de.diskResource.share.DiskResourceModule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import static com.sencha.gxt.core.client.Style.SelectionMode.SINGLE;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.dnd.core.client.DND;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;

/**
 * Created by jstroot on 1/21/15.
 *
 * @author jstroot
 */
public class NavigationViewImpl extends ContentPanel implements NavigationView {

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
            if (!isWidget.asWidget().isAttached()) {
                return;
            }

            if (selectedItem.isFilter()) {
                tree.getSelectionModel().deselect(selectedItem);
            } else {
                isWidget.asWidget().fireEvent(new FolderSelectionEvent(selectedItem));
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

    interface NavigationViewImplUiBinder extends UiBinder<Tree<Folder, Folder>, NavigationViewImpl> {
    }
    @UiField Tree<Folder, Folder> tree;
    private static final NavigationViewImplUiBinder ourUiBinder = GWT.create(NavigationViewImplUiBinder.class);
    private final Appearance appearance;
    private final ToolButton treeCollapseButton;
    private final TreeLoader<Folder> treeLoader;
    private final TreeStore<Folder> treeStore;

    @Inject
    NavigationViewImpl(final NavigationView.Appearance appearance,
                       @Assisted final TreeStore<Folder> treeStore,
                       @Assisted final TreeLoader<Folder> treeLoader,
                       @Assisted final NavigationViewDnDHandler dndHandler) {
        this.treeStore = treeStore;
        this.treeLoader = treeLoader;
        this.appearance = appearance;
        setHeadingText(appearance.headingText());
        setCollapsible(false);
        setHeaderVisible(true);

        ourUiBinder.createAndBindUi(this);
        add(tree);

        DropTarget treeDropTarget = new DropTarget(tree);
        treeDropTarget.setAllowSelfAsSource(true);
        treeDropTarget.setOperation(DND.Operation.COPY);
        treeDropTarget.addDragEnterHandler(dndHandler);
        treeDropTarget.addDragMoveHandler(dndHandler);
        treeDropTarget.addDropHandler(dndHandler);

        DragSource treeDragSource = new DragSource(tree);
        treeDragSource.addDragStartHandler(dndHandler);

        // Create TreeCollapseButton
        treeCollapseButton = new ToolButton(new IconButton.IconConfig(appearance.treeCollapseStyle(),
                                                                      appearance.treeCollapseHoverStyle()));
        treeCollapseButton.setToolTip(appearance.treeCollapseToolTip());
        treeCollapseButton.addSelectHandler(new TreeCollapseButtonSelectHandler(tree));
        if (getHeader().getToolCount() > 0) {
            getHeader().removeTool(getHeader().getTool(0));
        }
        getHeader().addTool(treeCollapseButton);
    }

    //<editor-fold desc="Handler Registrations">
    @Override
    public HandlerRegistration addDeleteSavedSearchClickedEventHandler(DeleteSavedSearchClickedEvent.DeleteSavedSearchEventHandler handler) {
        return addHandler(handler, DeleteSavedSearchClickedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addFolderSelectedEventHandler(FolderSelectionEvent.FolderSelectionEventHandler handler) {
        return addHandler(handler, FolderSelectionEvent.TYPE);
    }
    //</editor-fold>

    @Override
    public Tree<Folder, Folder> getTree() {
        return tree;
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        treeCollapseButton.setId(baseID + DiskResourceModule.Ids.TREE_COLLAPSE);
        tree.ensureDebugId(baseID + DiskResourceModule.Ids.NAV_TREE);
    }

    @UiFactory
    Tree<Folder, Folder> createTree() {
        final Tree<Folder, Folder> tree = new Tree<>(treeStore, new IdentityValueProvider<Folder>());
        tree.setView(appearance.getTreeView());

        tree.setLoader(treeLoader);
        tree.setIconProvider(appearance.getIconProvider());
        tree.setStyle(appearance.getTreeStyle(tree.getAppearance()));
        final TreeSelectionModel<Folder> selectionModel = tree.getSelectionModel();
        selectionModel.setSelectionMode(SINGLE);
        selectionModel.addSelectionHandler(new FolderSelectionHandler(this, tree));

        final TreeCell treeCell = new TreeCell(tree);
        treeCell.setHasHandlers(this);
        treeCell.setSelectionModel(selectionModel);

        tree.setCell(treeCell);

        new QuickTip(tree);
        return tree;
    }
}