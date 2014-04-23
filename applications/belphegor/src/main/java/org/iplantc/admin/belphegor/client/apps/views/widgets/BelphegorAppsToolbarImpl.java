package org.iplantc.admin.belphegor.client.apps.views.widgets;

import static org.iplantc.de.apps.client.events.AppGroupSelectionChangedEvent.*;
import static org.iplantc.de.apps.client.events.AppSelectionChangedEvent.*;
import org.iplantc.admin.belphegor.client.apps.views.AdminAppsView;
import org.iplantc.de.apps.client.events.AppGroupSelectionChangedEvent;
import org.iplantc.de.apps.client.events.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.views.widgets.AppSearchField;
import org.iplantc.de.apps.client.views.widgets.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.proxy.AppLoadConfig;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

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
public class BelphegorAppsToolbarImpl implements AdminAppsView.Toolbar, AppGroupSelectionChangedEventHandler, AppSelectionChangedEventHandler {

    @UiTemplate("BelphegorAppsViewToolbar.ui.xml")
    interface BelphegorAppsViewToolbarUiBinder extends UiBinder<Widget, BelphegorAppsToolbarImpl> {
    }
    @UiField
    TextButton addCategory;
    @UiField
    AppSearchField appSearch;
    @UiField
    TextButton categorizeApp;
    @UiField
    TextButton delete;
    @UiField
    PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader;
    @UiField
    TextButton renameCategory;
    @UiField
    TextButton restoreApp;
    @UiField
    ToolBar toolBar;
    private static BelphegorAppsViewToolbarUiBinder uiBinder = GWT
            .create(BelphegorAppsViewToolbarUiBinder.class);
    private final AppAutoBeanFactory appFactory;
    private final AppSearchAutoBeanFactory appSearchFactory;
    private final AppServiceFacade appService;
    private final IplantDisplayStrings displayStrings;
    private final Widget widget;
    private AdminAppsView.AdminPresenter presenter;
    private AppSearchRpcProxy proxy;

    @Inject
    public BelphegorAppsToolbarImpl(final AppServiceFacade appService,
                                    final AppSearchAutoBeanFactory appSearchFactory,
                                    final AppAutoBeanFactory appFactory,
                                    final IplantDisplayStrings displayStrings) {
        this.appService = appService;
        this.appSearchFactory = appSearchFactory;
        this.appFactory = appFactory;
        this.displayStrings = displayStrings;
        widget = uiBinder.createAndBindUi(this);
    }

    @UiHandler("addCategory")
    public void addCategoryClicked(SelectEvent event) {
        presenter.onAddAppGroupClicked();
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @UiHandler("categorizeApp")
    public void categorizeAppClicked(SelectEvent event) {
        presenter.onCategorizeAppClicked();
    }

    @UiHandler("delete")
    public void deleteClicked(SelectEvent event) {
        presenter.onDeleteClicked();
    }

    @Override
    public void init(final AdminAppsView.AdminPresenter presenter,
                     final HasAppSelectionChangedEventHandlers hasAppSelectionChangedEventHandlers,
                     final HasAppGroupSelectionChangedEventHandlers hasAppGroupSelectionChangedEventHandlers) {
        this.presenter = presenter;
        hasAppSelectionChangedEventHandlers.addAppSelectionChangedEventHandler(this);
        hasAppGroupSelectionChangedEventHandlers.addAppGroupSelectedEventHandler(this);
    }

    @Override
    public void onAppGroupSelectionChanged(AppGroupSelectionChangedEvent event) {
        addCategory.setEnabled(true);
        renameCategory.setEnabled(true);
        delete.setEnabled(true);
        restoreApp.setEnabled(false);
        categorizeApp.setEnabled(false);
    }

    @Override
    public void onAppSelectionChanged(AppSelectionChangedEvent event) {
        addCategory.setEnabled(false);
        renameCategory.setEnabled(false);
        delete.setEnabled(true);

        if (event.getAppSelection().isEmpty()) {
            restoreApp.setEnabled(false);
            categorizeApp.setEnabled(false);
        } else {
            final App app = event.getAppSelection().get(0);
            restoreApp.setEnabled(app.isDeleted());
            categorizeApp.setEnabled(!app.isDeleted());
        }
    }

    @UiHandler("renameCategory")
    public void renameCategoryClicked(SelectEvent event) {
        presenter.onRenameAppGroupClicked();
    }

    @UiHandler("restoreApp")
    public void restoreAppClicked(SelectEvent event) {
        presenter.onRestoreAppClicked();
    }

    @UiFactory
    AppSearchField createAppSearchField() {
        return new AppSearchField(loader);
    }

    @UiFactory
    PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> createPagingLoader() {
        proxy = new AppSearchRpcProxy(appService, appSearchFactory, appFactory, displayStrings);
        PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>>(
                proxy);

        AppLoadConfig appLoadConfig = appSearchFactory.loadConfig().as();
        loader.useLoadConfig(appLoadConfig);

        return loader;
    }

    @UiFactory
    ToolBar createToolbar() {
        return new ToolBar(new GrayToolBarAppearance());
    }
}
