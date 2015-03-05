package org.iplantc.de.admin.desktop.client.apps.views;

import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.views.AppsViewImpl;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.uibinder.client.UiFactory;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

/**
 * @author jstroot
 */
public class AdminAppViewImpl extends AppsViewImpl implements AdminAppsView,
                                                              AppSelectionChangedEvent.AppSelectionChangedEventHandler {

    private final AdminAppsView.Toolbar toolbar;

    /**
     * FIXME This will need an assisted inject
     */
    @Inject
    public AdminAppViewImpl(final AdminAppsView.Toolbar toolbar,
                            final IplantResources resources,
                            final IplantDisplayStrings displayStrings,
                            @Assisted final ListStore<App> listStore,
                            @Assisted final TreeStore<AppCategory> treeStore) {
        super((AppsToolbarView) toolbar, // FIXME Need to reconcile belphegor toolbar
              resources,
              displayStrings,
              listStore,
              treeStore);
        this.toolbar = toolbar;

        // Restrict Admin view to single select, since admin services only support one item at a time.
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // FIXME Toolbar will be provided via gin-jection. Rebinds through specialized gin module
//        setNorthWidget(toolbar);
    }

    @Override
    @UiFactory
    protected ColumnModel<App> createColumnModel(){
        return new BelphegorAppColumnModel();
    }

    @Override
    public void onAppSelectionChanged(AppSelectionChangedEvent event) {
        tree.getSelectionModel().deselectAll();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        if(presenter instanceof AdminAppsView.AdminPresenter){
            toolbar.init((AdminAppsView.AdminPresenter) presenter, this, this, this);
            ((BelphegorAppColumnModel)cm).addAppNameSelectedEventHandler(presenter);
            initDragAndDrop((AdminAppsView.AdminPresenter)presenter);
            addAppSelectionChangedEventHandler(this);
            addAppCategorySelectedEventHandler(presenter);
        }
    }

    private void initDragAndDrop(AdminAppsView.AdminPresenter presenter) {
        AppCategoryDnDHandler dndHandler = new AppCategoryDnDHandler(this, presenter);

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
