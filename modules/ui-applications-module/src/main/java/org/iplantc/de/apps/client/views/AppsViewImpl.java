package org.iplantc.de.apps.client.views;

import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.apps.client.AppsGridView;
import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.AppsView;
import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppCommentSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppInfoSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppRatingDeselected;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.models.AppCategoryStringValueProvider;
import org.iplantc.de.apps.client.views.details.dialogs.AppDetailsDialog;
import org.iplantc.de.apps.client.views.grid.AppColumnModel;
import org.iplantc.de.apps.shared.AppsModule.Ids;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import static com.sencha.gxt.core.client.Style.SelectionMode.SINGLE;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeAppearance;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;
import com.sencha.gxt.widget.core.client.tree.TreeStyle;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

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

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @Inject
    protected AppsViewImpl(@Assisted final AppCategoriesView.Presenter categoriesPresenter,
                           @Assisted final AppsGridView.Presenter gridPresenter,
                           @Assisted final AppsToolbarView.Presenter toolbarPresenter) {
        this.appCategoriesView = categoriesPresenter.getView();
        this.appsGridView = gridPresenter.getView();
        this.toolBar = toolbarPresenter.getView();

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public AppCategory getAppCategoryFromElement(Element el) {
        TreeNode<AppCategory> node = tree.findNode(el);
        if (node != null && tree.getView().isSelectableTarget(node.getModel(), el)) {
            return node.getModel();
        }

        return null;
    }

    @Override
    public App getAppFromElement(Element el) {
        Element row = gridView.findRow(el);
        int index = gridView.findRowIndex(row);
        return listStore.get(index);
    }


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
    }

}
