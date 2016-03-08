package org.iplantc.de.admin.apps.client.views;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.apps.client.AdminAppsToolbarView;
import org.iplantc.de.admin.apps.client.AdminAppsView;
import org.iplantc.de.admin.apps.client.AdminCategoriesView;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.apps.client.AppCategoriesView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.widget.core.client.Composite;

/**
 * @author jstroot
 */
public class AdminAppViewImpl extends Composite implements AdminAppsView {

    @UiTemplate("AdminAppsViewImpl.ui.xml")
    interface AdminAppsViewImplUiBinder extends UiBinder<Widget, AdminAppViewImpl>{}

    private static final AdminAppsViewImplUiBinder BINDER = GWT.create(AdminAppsViewImplUiBinder.class);
    @UiField(provided = true) final AdminAppsToolbarView toolBar;
    @UiField(provided = true) final AppCategoriesView appCategoriesView;
    @UiField(provided = true) final AdminAppsGridView appsGridView;

    /**
     * FIXME This will need an assisted inject
     */
    @Inject
    public AdminAppViewImpl(@Assisted final AdminCategoriesView.Presenter categoriesPresenter,
                            @Assisted final AdminAppsToolbarView.Presenter toolbarPresenter,
                            @Assisted final AdminAppsGridView.Presenter gridPresenter) {
        appCategoriesView = categoriesPresenter.getView();
        toolBar = toolbarPresenter.getView();
        appsGridView = gridPresenter.getView();

        initWidget(BINDER.createAndBindUi(this));
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        appCategoriesView.asWidget().ensureDebugId(baseID + Belphegor.AppIds.CATEGORIES);
        toolBar.asWidget().ensureDebugId(baseID + Belphegor.AppIds.TOOLBAR);
        appsGridView.asWidget().ensureDebugId(baseID + Belphegor.AppIds.GRID_VIEW);
    }

    //    private void initDragAndDrop(AdminAppsView.AdminPresenter presenter) {
//        AppCategoryDnDHandler dndHandler = new AppCategoryDnDHandler(this, presenter);
//
//        DragSource gridDragSource = new DragSource(grid);
//        gridDragSource.addDragStartHandler(dndHandler);
//
//        DropTarget treeDropTarget = new DropTarget(tree);
//        treeDropTarget.setAllowSelfAsSource(true);
//        treeDropTarget.setOperation(Operation.COPY);
//        treeDropTarget.addDragEnterHandler(dndHandler);
//        treeDropTarget.addDragMoveHandler(dndHandler);
//        treeDropTarget.addDropHandler(dndHandler);
//
//        DragSource treeDragSource = new DragSource(tree);
//        treeDragSource.addDragStartHandler(dndHandler);
//    }
}
