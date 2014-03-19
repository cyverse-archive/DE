package org.iplantc.admin.belphegor.client.apps.views.widgets;

import org.iplantc.de.apps.client.views.widgets.AppSearchField;
import org.iplantc.de.apps.client.views.widgets.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.proxy.AppLoadConfig;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.theme.gray.client.toolbar.GrayToolBarAppearance;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * @author jstroot
 * 
 */
public class BelphegorAppsToolbarImpl implements BelphegorAppsToolbar {

    private static BelphegorAppsViewToolbarUiBinder uiBinder = GWT
            .create(BelphegorAppsViewToolbarUiBinder.class);

    @UiTemplate("BelphegorAppsViewToolbar.ui.xml")
    interface BelphegorAppsViewToolbarUiBinder extends UiBinder<Widget, BelphegorAppsToolbarImpl> {
    }

    private final Widget widget;
    private Presenter presenter;
    private AppSearchRpcProxy proxy;

    @UiField
    ToolBar toolBar;

    @UiField
    TextButton addCategory;

    @UiField
    TextButton renameCategory;

    @UiField
    AppSearchField appSearch;

    @UiField
    TextButton delete;

    @UiField
    TextButton restoreApp;

    @UiField
    TextButton categorizeApp;

    @UiField
    PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader;

    @UiFactory
    PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> createPagingLoader() {
        proxy = new AppSearchRpcProxy();
        PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>>(
                proxy);

        AppLoadConfig appLoadConfig = AppSearchAutoBeanFactory.instance.loadConfig().as();
        loader.useLoadConfig(appLoadConfig);

        return loader;
    }

    @UiFactory
    AppSearchField createAppSearchField() {
        return new AppSearchField(loader);
    }

    @UiFactory
    ToolBar createToolbar() {
        return new ToolBar(new GrayToolBarAppearance());
    }

    public BelphegorAppsToolbarImpl() {
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("addCategory")
    public void addCategoryClicked(SelectEvent event) {
        presenter.onAddAppGroupClicked();
    }

    @UiHandler("renameCategory")
    public void renameCategoryClicked(SelectEvent event) {
        presenter.onRenameAppGroupClicked();
    }

    @UiHandler("delete")
    public void deleteClicked(SelectEvent event) {
        presenter.onDeleteClicked();
    }

    @UiHandler("restoreApp")
    public void restoreAppClicked(SelectEvent event) {
        presenter.onRestoreAppClicked();
    }

    @UiHandler("categorizeApp")
    public void categorizeAppClicked(SelectEvent event) {
        presenter.onCategorizeAppClicked();
    }

    @Override
    public void setAddAppGroupButtonEnabled(boolean enabled) {
        addCategory.setEnabled(enabled);
    }

    @Override
    public void setRenameAppGroupButtonEnabled(boolean enabled) {
        renameCategory.setEnabled(enabled);
    }

    @Override
    public void setDeleteButtonEnabled(boolean enabled) {
        delete.setEnabled(enabled);
    }

    @Override
    public void setRestoreButtonEnabled(boolean enabled) {
        restoreApp.setEnabled(enabled);
    }

    @Override
    public void setCategorizeButtonEnabled(boolean enabled) {
        categorizeApp.setEnabled(enabled);
    }

    @Override
    public AppSearchRpcProxy getAppSearchRpcProxy() {
        return proxy;
    }
}
