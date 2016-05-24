package org.iplantc.de.apps.client.views;

import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.apps.client.AppsGridView;
import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.AppsView;
import org.iplantc.de.apps.client.OntologyHierarchiesView;
import org.iplantc.de.apps.shared.AppsModule.Ids;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.tree.Tree;

/**
 * @author jstroot
 */
public class AppsViewImpl extends Composite implements AppsView {
    @UiTemplate("AppsView.ui.xml")
    interface MyUiBinder extends UiBinder<Widget, AppsViewImpl> {
    }

    @UiField(provided = true) final AppsToolbarView toolBar;
    @UiField TabPanel categoryTabs;
    AppCategoriesView.Presenter categoriesPresenter;
    OntologyHierarchiesView.Presenter hierarchiesPresenter;

    @UiField(provided = true) final AppsGridView appsGridView;

    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @Inject
    protected AppsViewImpl(@Assisted final AppCategoriesView.Presenter categoriesPresenter,
                           @Assisted final OntologyHierarchiesView.Presenter hierarchiesPresenter,
                           @Assisted final AppsGridView.Presenter gridPresenter,
                           @Assisted final AppsToolbarView.Presenter toolbarPresenter) {
        this.categoriesPresenter = categoriesPresenter;
        this.hierarchiesPresenter = hierarchiesPresenter;
        this.appsGridView = gridPresenter.getView();
        this.toolBar = toolbarPresenter.getView();

        initWidget(uiBinder.createAndBindUi(this));
        categoryTabs.addSelectionHandler(new SelectionHandler<Widget>() {
            @Override
            public void onSelection(SelectionEvent<Widget> event) {
                for (Widget next : categoryTabs) {
                    if (event.getSelectedItem() instanceof AppCategoriesView && next instanceof Tree) {
                        ((Tree)next).getSelectionModel().deselectAll();
                    } else if (event.getSelectedItem() instanceof Tree && next instanceof AppCategoriesView) {
                        ((AppCategoriesView)next).getTree().getSelectionModel().deselectAll();
                    }
                }
            }
        });
    }

    @Override
    public TabPanel getCategoryTabPanel() {
        return categoryTabs;
    }

    @Override
    public void hideAppMenu() {
        toolBar.hideAppMenu();
    }

    @Override
    public void hideWorkflowMenu() {
        toolBar.hideWorkflowMenu();
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        toolBar.asWidget().ensureDebugId(baseID + Ids.MENU_BAR);
        appsGridView.asWidget().ensureDebugId(baseID);
        categoriesPresenter.setViewDebugId(baseID);
        hierarchiesPresenter.setViewDebugId(baseID);
    }

}
