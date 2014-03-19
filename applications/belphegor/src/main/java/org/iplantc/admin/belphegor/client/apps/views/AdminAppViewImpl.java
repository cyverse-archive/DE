package org.iplantc.admin.belphegor.client.apps.views;

import org.iplantc.admin.belphegor.client.apps.presenter.AdminAppsViewPresenter;
import org.iplantc.de.apps.client.views.AppsViewImpl;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppGroup;

import com.google.gwt.uibinder.client.UiFactory;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.tree.Tree;

public class AdminAppViewImpl extends AppsViewImpl {

    @Inject
    public AdminAppViewImpl(Tree<AppGroup, String> tree) {
        super(tree);

        // Restrict Admin view to single select, since admin services only support one item at a time.
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @Override
    @UiFactory
    public ColumnModel<App> createColumnModel() {
        return new BelphegorAnalysisColumnModel(this);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        super.setPresenter(presenter);

        if (presenter instanceof AdminAppsViewPresenter) {
            initDragAndDrop((AdminAppsViewPresenter)presenter);
        }
    }

    private void initDragAndDrop(AdminAppsViewPresenter presenter) {
        AppGroupDnDHandler dndHandler = new AppGroupDnDHandler(presenter);

        DragSource gridDragSource = new DragSource(grid);
        gridDragSource.addDragStartHandler(dndHandler);

        DropTarget treeDropTarget = new DropTarget(tree);
        treeDropTarget.setAllowSelfAsSource(true);
        treeDropTarget.setOperation(Operation.COPY);
        treeDropTarget.addDragEnterHandler(dndHandler);
        treeDropTarget.addDragMoveHandler(dndHandler);
        treeDropTarget.addDropHandler(dndHandler);

        DragSource treeDragSource = new DragSource(tree);
        treeDragSource.addDragStartHandler(dndHandler);
    }
}
