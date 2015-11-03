package org.iplantc.de.apps.client.views;

import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.apps.client.AppsGridView;
import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.AppsView;
import org.iplantc.de.apps.shared.AppsModule.Ids;

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
public class AppsViewImpl extends Composite implements AppsView {
    @UiTemplate("AppsView.ui.xml")
    interface MyUiBinder extends UiBinder<Widget, AppsViewImpl> {
    }

    @UiField(provided = true) final AppsToolbarView toolBar;
    @UiField(provided = true) final AppCategoriesView appCategoriesView;
    @UiField(provided = true) final AppsGridView appsGridView;

    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @Inject
    protected AppsViewImpl(@Assisted final AppCategoriesView.Presenter categoriesPresenter,
                           @Assisted final AppsGridView.Presenter gridPresenter,
                           @Assisted final AppsToolbarView.Presenter toolbarPresenter) {
        this.appCategoriesView = categoriesPresenter.getView();
        this.appsGridView = gridPresenter.getView();
        this.toolBar = toolbarPresenter.getView();

        initWidget(uiBinder.createAndBindUi(this));
    }

//    @Override
//    public AppCategory getAppCategoryFromElement(Element el) {
//        TreeNode<AppCategory> node = tree.findNode(el);
//        if (node != null && tree.getView().isSelectableTarget(node.getModel(), el)) {
//            return node.getModel();
//        }
//
//        return null;
//    }
//
//    @Override
//    public App getAppFromElement(Element el) {
//        Element row = gridView.findRow(el);
//        int index = gridView.findRowIndex(row);
//        return listStore.get(index);
//    }


    @Override
    public void hideAppMenu() {
        toolBar.hideAppMenu();
    }

    @Override
    public void hideWorkflowMenu() {
        toolBar.hideWorkflowMenu();
    }

    //<editor-fold desc="Selection">
//    @Override
//    public void selectApp(String appId) {
//        App app = listStore.findModelWithKey(appId);
//        if (app != null) {
//            grid.getSelectionModel().select(app, false);
//        }
//    }
//
//    @Override
//    public void selectAppCategory(HasId appCategory) {
//        if (appCategory == null) {
//            tree.getSelectionModel().deselectAll();
//            return;
//        }
//        AppCategory ag = treeStore.findModelWithKey(appCategory.getId());
//
//        if (ag != null) {
//            if (tree.getSelectionModel().isSelected(ag)) {
//                /* if category is already selected, then manually fire event since selection
//                     * model won't fire selection changed
//                     */
//                fireEvent(new AppCategorySelectionChangedEvent(Collections.singletonList(ag), hierarchyProvider.getGroupHierarchy(event.getSelection().iterator().next())));
//            } else {
//                tree.getSelectionModel().select(ag, false);
//                tree.scrollIntoView(ag);
//            }
//        } else {
//            // Try to find app group by name if ID could not locate the
//            for (AppCategory appGrp : treeStore.getAll()) {
//
//                if (appGrp.getName().equalsIgnoreCase(appCategory.getId())) {
//                    tree.getSelectionModel().select(appGrp, false);
//                    tree.scrollIntoView(appGrp);
//                    return;
//                }
//            }
//        }
//
//    }
//
//    @Override
//    public void selectFirstApp() {
//        grid.getSelectionModel().select(0, false);
//    }
//
//    @Override
//    public void selectFirstAppCategory() {
//        AppCategory ag = treeStore.getRootItems().get(0);
//        tree.getSelectionModel().select(ag, false);
//        tree.scrollIntoView(ag);
//    }
    //</editor-fold>

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        toolBar.asWidget().ensureDebugId(baseID + Ids.MENU_BAR);
        appsGridView.asWidget().ensureDebugId(baseID);
        appCategoriesView.asWidget().ensureDebugId(baseID);
    }

}
