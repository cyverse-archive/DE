package org.iplantc.de.admin.apps.client.views.toolbar;

import org.iplantc.de.admin.apps.client.events.selection.AddCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.CategorizeAppSelected;
import org.iplantc.de.admin.apps.client.events.selection.DeleteCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.MoveCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.RenameCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.RestoreAppSelected;
import org.iplantc.de.admin.apps.client.AdminAppsToolbarView;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent.AppSelectionChangedEventHandler;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.apps.client.presenter.toolBar.proxy.AppSearchRpcProxy;
import org.iplantc.de.apps.client.views.toolBar.AppSearchField;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.List;

/**
 * @author jstroot
 */
public class AdminAppsToolbarViewImpl extends Composite implements AdminAppsToolbarView,
                                                                   AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler,
                                                                   AppSelectionChangedEventHandler,
                                                                   AppSearchResultLoadEvent.HasAppSearchResultLoadEventHandlers {

    @UiTemplate("AdminAppsViewToolbar.ui.xml")
    interface BelphegorAppsViewToolbarUiBinder extends UiBinder<Widget, AdminAppsToolbarViewImpl> {
    }

    @UiField TextButton addCategory;
    @UiField AppSearchField appSearch;
    @UiField TextButton categorizeApp;
    @UiField TextButton deleteApp;
    @UiField TextButton deleteCat;
    @UiField TextButton moveCategory;
    @UiField TextButton renameCategory;
    @UiField TextButton restoreApp;
    @UiField ToolBar toolBar;

    private static BelphegorAppsViewToolbarUiBinder uiBinder = GWT.create(BelphegorAppsViewToolbarUiBinder.class);
    private final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader;
    private List<AppCategory> appCategorySelection;
    private List<App> appSelection;
    private AppSearchRpcProxy proxy;

    @Inject
    AdminAppsToolbarViewImpl(@Assisted final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader) {
        this.loader = loader;
        initWidget(uiBinder.createAndBindUi(this));
    }

    //<editor-fold desc="Handler Registrations">
    @Override
    public HandlerRegistration addAddCategorySelectedHandler(AddCategorySelected.AddCategorySelectedHandler handler) {
        return addHandler(handler, AddCategorySelected.TYPE);
    }

    @Override
    public HandlerRegistration addAppSearchResultLoadEventHandler(AppSearchResultLoadEvent.AppSearchResultLoadEventHandler handler) {
        return asWidget().addHandler(handler, AppSearchResultLoadEvent.TYPE);
    }

    @Override
    public HandlerRegistration addCategorizeAppSelectedHandler(CategorizeAppSelected.CategorizeAppSelectedHandler handler) {
        return addHandler(handler, CategorizeAppSelected.TYPE);
    }

    @Override
    public HandlerRegistration addDeleteAppsSelectedHandler(DeleteAppsSelected.DeleteAppsSelectedHandler handler) {
        return addHandler(handler, DeleteAppsSelected.TYPE);
    }

    @Override
    public HandlerRegistration addDeleteCategorySelectedHandler(DeleteCategorySelected.DeleteCategorySelectedHandler handler) {
        return addHandler(handler, DeleteCategorySelected.TYPE);
    }

    @Override
    public HandlerRegistration addMoveCategorySelectedHandler(MoveCategorySelected.MoveCategorySelectedHandler handler) {
        return addHandler(handler, MoveCategorySelected.TYPE);
    }

    @Override
    public HandlerRegistration addRenameCategorySelectedHandler(RenameCategorySelected.RenameCategorySelectedHandler handler) {
        return addHandler(handler, RenameCategorySelected.TYPE);
    }

    @Override
    public HandlerRegistration addRestoreAppSelectedHandler(RestoreAppSelected.RestoreAppSelectedHandler handler) {
        return addHandler(handler, RestoreAppSelected.TYPE);
    }
    //</editor-fold>

    //<editor-fold desc="Selection Handlers">
    @Override
    public void onAppCategorySelectionChanged(AppCategorySelectionChangedEvent event) {
        appCategorySelection = event.getAppCategorySelection();

        boolean renameCategoryEnabled, deleteEnabled, moveCatEnabled;
        switch (appCategorySelection.size()) {
            case 1:
                renameCategoryEnabled = true;
                deleteEnabled = true;
                moveCatEnabled = true;
                break;
            default:
                renameCategoryEnabled = false;
                deleteEnabled = false;
                moveCatEnabled = false;

        }
        addCategory.setEnabled(true);
        renameCategory.setEnabled(renameCategoryEnabled);
        deleteCat.setEnabled(deleteEnabled);
        moveCategory.setEnabled(moveCatEnabled);
    }

    @Override
    public void onAppSelectionChanged(AppSelectionChangedEvent event) {
        appSelection = event.getAppSelection();

        boolean deleteEnabled, restoreAppEnabled, categorizeAppEnabled;
        switch (appSelection.size()) {
            case 1:
                final boolean isDeleted = appSelection.get(0).isDeleted();
                deleteEnabled = !isDeleted;
                restoreAppEnabled = isDeleted;
                categorizeAppEnabled = !isDeleted;
                break;
            default:
                deleteEnabled = false;
                restoreAppEnabled = false;
                categorizeAppEnabled = false;

        }
        addCategory.setEnabled(false);
        renameCategory.setEnabled(false);
        deleteApp.setEnabled(deleteEnabled);
        restoreApp.setEnabled(restoreAppEnabled);
        categorizeApp.setEnabled(categorizeAppEnabled);
    }
    //</editor-fold>

    @UiHandler("categorizeApp")
    void categorizeAppClicked(SelectEvent event) {
//        presenter.onCategorizeAppClicked();
        fireEvent(new CategorizeAppSelected(appSelection));
    }

    @UiHandler("deleteApp")
    void deleteAppClicked(SelectEvent event) {
//        presenter.onDeleteAppClicked();
        fireEvent(new DeleteAppsSelected(appSelection));
    }

    @UiHandler("deleteCat")
    void deleteCatClicked(SelectEvent event) {
//        presenter.onDeleteCatClicked();
        // FIXME Move confirm dialog here
        fireEvent(new DeleteCategorySelected(appCategorySelection));
    }

    @UiHandler("moveCategory")
    void moveCategory(SelectEvent event) {
//        presenter.onMoveCategoryClicked();
        // FIXME Move dialog here
        fireEvent(new MoveCategorySelected(appCategorySelection.iterator().next()));
    }

    @UiHandler("addCategory")
    void onAddCategoryClicked(SelectEvent event) {
//        presenter.onAddAppCategoryClicked();
        fireEvent(new AddCategorySelected(appCategorySelection));
    }

//    @Override
//    public void init(final AdminAppsView.AdminPresenter presenter,
//                     final AdminAppsView appView,
//                     final HasAppSelectionChangedEventHandlers hasAppSelectionChangedEventHandlers,
//                     final AppCategorySelectionChangedEvent.HasAppCategorySelectionChangedEventHandlers hasAppCategorySelectionChangedEventHandlers) {
//        this.presenter = presenter;
    // FIXME wire up search result load handler
//        addAppSearchResultLoadEventHandler(appView);
//        hasAppSelectionChangedEventHandlers.addAppSelectionChangedEventHandler(this);
//        hasAppCategorySelectionChangedEventHandlers.addAppCategorySelectedEventHandler(this);
//        proxy.setHasHandlers(asWidget());
//        proxy.setMaskable(new IsMaskable() {
//            @Override
//            public void mask(String loadingMask) {
//                appView.maskCenterPanel(loadingMask);
//            }
//
//            @Override
//            public void unmask() {
//                appView.unMaskCenterPanel();
//            }
//        });
//    }

    @UiHandler("renameCategory")
    void renameCategoryClicked(SelectEvent event) {
//        presenter.onRenameAppCategoryClicked();
        // FIXME Move dialog here
        fireEvent(new RenameCategorySelected(appCategorySelection.iterator().next()));
    }

    @UiHandler("restoreApp")
    void restoreAppClicked(SelectEvent event) {
//        presenter.onRestoreAppClicked();
        fireEvent(new RestoreAppSelected(appSelection));
    }

    @UiFactory
    AppSearchField createAppSearchField() {
        return new AppSearchField(loader);
    }
//
//    @UiFactory
//    PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> createPagingLoader() {
//        IplantDisplayStrings displayStrings = GWT.create(IplantDisplayStrings.class);
//        // FIXME Fix this with injection
//        AppsToolbarView.AppsToolbarAppearance appearance = GWT.create(AppsToolbarView.AppsToolbarAppearance.class);
//        proxy = new AppSearchRpcProxy(appService, appSearchFactory, appFactory, appearance);
//        PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader = new PagingLoader<>(
//                                                                                                   proxy);
//
//        AppLoadConfig appLoadConfig = appSearchFactory.loadConfig().as();
//        loader.useLoadConfig(appLoadConfig);
//
//        return loader;
//    }

}
