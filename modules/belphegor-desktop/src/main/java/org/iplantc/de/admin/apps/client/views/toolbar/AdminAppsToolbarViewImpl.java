package org.iplantc.de.admin.apps.client.views.toolbar;

import org.iplantc.de.admin.apps.client.AdminAppsToolbarView;
import org.iplantc.de.admin.apps.client.events.selection.AddCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.CategorizeAppSelected;
import org.iplantc.de.admin.apps.client.events.selection.DeleteCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.MoveCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.RenameCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.RestoreAppSelected;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent.AppSelectionChangedEventHandler;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.apps.client.presenter.toolBar.proxy.AppSearchRpcProxy;
import org.iplantc.de.apps.client.views.toolBar.AppSearchField;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.commons.client.views.dialogs.IPlantPromptDialog;

import com.google.common.base.Preconditions;
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
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;
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

    @Inject AdminAppsToolbarView.ToolbarAppearance appearance;

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

        boolean renameCategoryEnabled,
            deleteEnabled,
            moveCatEnabled,
            addCategoryEnabled;

        switch (appCategorySelection.size()) {
            case 1:
                renameCategoryEnabled = true;
                deleteEnabled = true;
                moveCatEnabled = true;
                addCategoryEnabled = true;
                break;
            default:
                renameCategoryEnabled = false;
                deleteEnabled = false;
                moveCatEnabled = false;
                addCategoryEnabled = true;

        }
        addCategory.setEnabled(addCategoryEnabled);
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
        fireEvent(new CategorizeAppSelected(appSelection));
    }

    @UiHandler("deleteApp")
    void deleteAppClicked(SelectEvent event) {
        ConfirmMessageBox msgBox = new ConfirmMessageBox(appearance.confirmDeleteAppWarning(),
                                                         appearance.confirmDeleteAppTitle());

        msgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if(!Dialog.PredefinedButton.YES.equals(event.getHideButton())){
                    return;
                }
                fireEvent(new DeleteAppsSelected(appSelection));
            }
        });
        msgBox.show();
    }

    @UiHandler("deleteCat")
    void deleteCatClicked(SelectEvent event) {
        Preconditions.checkState(appCategorySelection.size() == 1);
        final AppCategory selectedAppCategory = appCategorySelection.iterator().next();
        ConfirmMessageBox msgBox = new ConfirmMessageBox(appearance.confirmDeleteAppCategoryWarning(),
                                                         appearance.confirmDeleteAppCategory(selectedAppCategory.getName()));
        msgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (!Dialog.PredefinedButton.YES.equals(event.getHideButton())) {
                    return;
                }

                fireEvent(new DeleteCategorySelected(appCategorySelection));
            }
        });
        msgBox.show();
    }

    @UiHandler("moveCategory")
    void moveCategory(SelectEvent event) {
//        presenter.onMoveCategoryClicked();
        // FIXME Move dialog here
        fireEvent(new MoveCategorySelected(appCategorySelection.iterator().next()));
    }

    @UiHandler("addCategory")
    void onAddCategoryClicked(SelectEvent event) {
        final IPlantPromptDialog dlg = new IPlantPromptDialog(appearance.add(),
                                                  0, "", null);
        dlg.setHeadingText(appearance.addCategoryPrompt());
        dlg.addOkButtonSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                fireEvent(new AddCategorySelected(appCategorySelection,
                                                  dlg.getFieldText()));
            }
        });
        dlg.show();
    }

    @UiHandler("renameCategory")
    void renameCategoryClicked(SelectEvent event) {
        Preconditions.checkState(appCategorySelection.size() == 1);
        final AppCategory selectedAppCategory = appCategorySelection.iterator().next();
         final PromptMessageBox msgBox = new PromptMessageBox(appearance.renameCategory(),
                                                       appearance.renamePrompt());
        final TextField field = ((TextField) msgBox.getField());
        field.setAutoValidate(true);
        field.setAllowBlank(false);
        field.setText(selectedAppCategory.getName());
        msgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if(!Dialog.PredefinedButton.OK.equals(event.getHideButton())){
                    return;
                }
                fireEvent(new RenameCategorySelected(appCategorySelection.iterator().next(),
                                                     field.getText()));
            }
        });

        msgBox.show();
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
}
