package org.iplantc.admin.belphegor.client.apps.views;

import org.iplantc.admin.belphegor.client.services.impl.AppAdminUserServiceFacade;
import org.iplantc.de.apps.client.events.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.views.AppsViewImpl;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.uibinder.client.UiFactory;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.tree.Tree;

public class AdminAppViewImpl extends AppsViewImpl implements AdminAppsView,
                                                                      AppSelectionChangedEvent.AppSelectionChangedEventHandler {

    private final AdminAppsView.Toolbar toolbar;

    @Inject
    public AdminAppViewImpl(final Tree<AppCategory, String> tree,
                            final AdminAppsView.Toolbar toolbar,
                            final DEProperties props,
                            final IplantResources resources,
                            final UserInfo userInfo,
                            final IplantDisplayStrings displayStrings,
                            final AppAdminUserServiceFacade appAdminUserService) {
        super(tree, props, null, resources, userInfo, displayStrings, appAdminUserService);
        this.toolbar = toolbar;

        // Restrict Admin view to single select, since admin services only support one item at a time.
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setNorthWidget(toolbar);
    }

    @Override
    @UiFactory
    protected ColumnModel<App> createColumnModel(){
        return new BelphegorAppColumnModel(this);
    }

    @Override
    public void onAppSelectionChanged(AppSelectionChangedEvent event) {
        deSelectAllAppCategories();
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
