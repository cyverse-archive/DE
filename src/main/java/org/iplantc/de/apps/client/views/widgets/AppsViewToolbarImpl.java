package org.iplantc.de.apps.client.views.widgets;


import org.iplantc.de.apps.client.views.widgets.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.proxy.AppLoadConfig;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class AppsViewToolbarImpl implements AppsViewToolbar {

    private static AppsViewToolbarUiBinder uiBinder = GWT.create(AppsViewToolbarUiBinder.class);

    @UiTemplate("AppsViewToolbar.ui.xml")
    interface AppsViewToolbarUiBinder extends UiBinder<Widget, AppsViewToolbarImpl> {
    }

    private final Widget widget;
    private Presenter presenter;
    private final AppSearchRpcProxy proxy;
    private final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader;

    @UiField
    TextButton create;
    
    @UiField
    TextButton edit;

    @UiField
    MenuItem createNewApp;

    @UiField
    MenuItem createWorkflow;

    @UiField
    TextButton appRun;

    @UiField
    TextButton requestTool;

    @UiField
    MenuItem copy;

    @UiField
    MenuItem editApp;

    @UiField
    MenuItem delete;

    @UiField
    TextButton submit;

    @UiField
    AppSearchField appSearch;

    @UiFactory
    AppSearchField createAppSearchField() {
        return new AppSearchField(loader);
    }

    public AppsViewToolbarImpl() {
        proxy = new AppSearchRpcProxy();
        loader = createPagingLoader();
        widget = uiBinder.createAndBindUi(this);
    }

    private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> createPagingLoader() {
        PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>>(
                proxy);

        AppLoadConfig appLoadConfig = AppSearchAutoBeanFactory.instance.loadConfig().as();
        loader.useLoadConfig(appLoadConfig);

        return loader;
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("appRun")
    public void appInfoClicked(SelectEvent event) {
        presenter.onAppRunClick();
    }

    @UiHandler("requestTool")
    public void requestToolClicked(SelectEvent event) {
        presenter.onRequestToolClicked();
    }

    @UiHandler("copy")
    public void copyClicked(SelectionEvent<Item> event) {
        presenter.onCopyClicked();
    }

    @UiHandler("editApp")
    public void editClicked(SelectionEvent<Item> event) {
        presenter.onEditClicked();
    }

    @UiHandler("delete")
    public void deleteClicked(SelectionEvent<Item> event) {
        presenter.onDeleteClicked();
    }

    @UiHandler("submit")
    public void submitClicked(SelectEvent event) {
        presenter.submitClicked();
    }

    @UiHandler("createNewApp")
    public void createNewAppClicked(SelectionEvent<Item> event) {
        presenter.createNewAppClicked();
    }

    @UiHandler("createWorkflow")
    public void createWorkflowClicked(SelectionEvent<Item> event) {
        presenter.createWorkflowClicked();
    }
    
    @Override
    public void setEditMenuEnabled(boolean enabled) {
        edit.setEnabled(enabled);
    }

    @Override
    public void setEditButtonEnabled(boolean enabled) {
        editApp.setEnabled(enabled);
    }

    @Override
    public void setSubmitButtonEnabled(boolean enabled) {
        submit.setEnabled(enabled);
    }

    @Override
    public void setDeleteButtonEnabled(boolean enabled) {
        delete.setEnabled(enabled);
    }

    @Override
    public void setCopyButtonEnabled(boolean enabled) {
        copy.setEnabled(enabled);
    }

    @Override
    public void setAppRunButtonEnabled(boolean enabled) {
        appRun.setEnabled(enabled);
    }

    @Override
    public void setCreateButtonVisible(boolean visible) {
        create.setVisible(visible);
    }

    @Override
    public void setCopyButtonVisible(boolean visible) {
        copy.setVisible(visible);
    }
    
    @Override
    public void setEditMenuVisible(boolean visible) {
        edit.setVisible(visible);
    }

    @Override
    public void setEditButtonVisible(boolean visible) {
        editApp.setVisible(visible);
    }

    @Override
    public void setDeleteButtonVisible(boolean visible) {
        delete.setVisible(visible);
    }

    @Override
    public void setSubmitButtonVisible(boolean visible) {
        submit.setVisible(visible);
    }

    @Override
    public void setRequestToolButtonVisible(boolean visible) {
        requestTool.setVisible(visible);
    }

    @Override
    public AppSearchRpcProxy getAppSearchRpcProxy() {
        return proxy;
    }
}
