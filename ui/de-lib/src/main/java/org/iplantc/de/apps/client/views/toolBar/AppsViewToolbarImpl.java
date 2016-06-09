package org.iplantc.de.apps.client.views.toolBar;

import static org.iplantc.de.apps.client.events.AppSearchResultLoadEvent.TYPE;

import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent.AppSearchResultLoadEventHandler;
import org.iplantc.de.apps.client.events.BeforeAppSearchEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.CopyAppSelected;
import org.iplantc.de.apps.client.events.selection.CopyWorkflowSelected;
import org.iplantc.de.apps.client.events.selection.CreateNewAppSelected;
import org.iplantc.de.apps.client.events.selection.CreateNewWorkflowSelected;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.apps.client.events.selection.EditAppSelected;
import org.iplantc.de.apps.client.events.selection.EditWorkflowSelected;
import org.iplantc.de.apps.client.events.selection.RequestToolSelected;
import org.iplantc.de.apps.client.events.selection.RunAppSelected;
import org.iplantc.de.apps.client.events.selection.ShareAppsSelected;
import org.iplantc.de.apps.client.views.submit.dialog.SubmitAppForPublicDialog;
import org.iplantc.de.apps.shared.AppsModule.Ids;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.shared.AsyncProviderWrapper;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import java.util.Arrays;
import java.util.List;

/**
 * @author jstroot
 */
public class AppsViewToolbarImpl extends Composite implements AppsToolbarView {

    @UiTemplate("AppsViewToolbar.ui.xml")
    interface AppsViewToolbarUiBinder extends UiBinder<Widget, AppsViewToolbarImpl> {
    }


    @UiField
    Menu sharingMenu;
    @UiField
    TextButton shareMenuButton;
    @UiField
    MenuItem appRun;
    @UiField
    AppSearchField appSearch;
    @UiField
    TextButton app_menu;
    @UiField
    BoxLayoutData boxData;
    @UiField
    MenuItem copyApp;
    @UiField
    MenuItem copyWf;
    @UiField
    MenuItem createNewApp;
    @UiField
    MenuItem createWorkflow;
    @UiField
    MenuItem deleteApp;
    @UiField
    MenuItem deleteWf;
    @UiField
    MenuItem editApp;
    @UiField
    MenuItem editWf;
    @UiField
    MenuItem requestTool;
    @UiField
    MenuItem shareCollab, sharePublic;
    @Inject AsyncProviderWrapper<SubmitAppForPublicDialog> submitAppDialogAsyncProvider;
    @UiField
    MenuItem wfRun;
    @UiField
    TextButton wf_menu;
    @UiField(provided = true)
    final AppsToolbarAppearance appearance;
    private static final AppsViewToolbarUiBinder uiBinder = GWT.create(AppsViewToolbarUiBinder.class);
    private final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader;
    @Inject
    UserInfo userInfo;
    protected List<App> currentSelection = Lists.newArrayList();

    @Inject
    AppsViewToolbarImpl(final AppsToolbarAppearance appearance,
                        @Assisted
                        final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader) {
        this.appearance = appearance;
        this.loader = loader;
        initWidget(uiBinder.createAndBindUi(this));
    }

    // <editor-fold desc="Handler Registrations">
    @Override
    public HandlerRegistration addAppSearchResultLoadEventHandler(AppSearchResultLoadEventHandler handler) {
        return addHandler(handler, TYPE);
    }

    @Override
    public HandlerRegistration addBeforeAppSearchEventHandler(BeforeAppSearchEvent.BeforeAppSearchEventHandler handler) {
        return addHandler(handler, BeforeAppSearchEvent.TYPE);
    }

    @Override
    public HandlerRegistration addCopyAppSelectedHandler(CopyAppSelected.CopyAppSelectedHandler handler) {
        return addHandler(handler, CopyAppSelected.TYPE);
    }

    @Override
    public HandlerRegistration addCopyWorkflowSelectedHandler(CopyWorkflowSelected.CopyWorkflowSelectedHandler handler) {
        return addHandler(handler, CopyWorkflowSelected.TYPE);
    }

    @Override
    public HandlerRegistration addCreateNewAppSelectedHandler(CreateNewAppSelected.CreateNewAppSelectedHandler handler) {
        return addHandler(handler, CreateNewAppSelected.TYPE);
    }

    @Override
    public HandlerRegistration addCreateNewWorkflowSelectedHandler(CreateNewWorkflowSelected.CreateNewWorkflowSelectedHandler handler) {
        return addHandler(handler, CreateNewWorkflowSelected.TYPE);
    }

    @Override
    public HandlerRegistration addDeleteAppsSelectedHandler(DeleteAppsSelected.DeleteAppsSelectedHandler handler) {
        return addHandler(handler, DeleteAppsSelected.TYPE);
    }

    @Override
    public HandlerRegistration addEditAppSelectedHandler(EditAppSelected.EditAppSelectedHandler handler) {
        return addHandler(handler, EditAppSelected.TYPE);
    }

    @Override
    public HandlerRegistration addEditWorkflowSelectedHandler(EditWorkflowSelected.EditWorkflowSelectedHandler handler) {
        return addHandler(handler, EditWorkflowSelected.TYPE);
    }

    @Override
    public HandlerRegistration addRequestToolSelectedHandler(RequestToolSelected.RequestToolSelectedHandler handler) {
        return addHandler(handler, RequestToolSelected.TYPE);
    }

    @Override
    public HandlerRegistration addRunAppSelectedHandler(RunAppSelected.RunAppSelectedHandler handler) {
        return addHandler(handler, RunAppSelected.TYPE);
    }

    @Override
    public HandlerRegistration addShareAppSelectedHandler(ShareAppsSelected.ShareAppsSelectedHandler handler) {
        return addHandler(handler, ShareAppsSelected.TYPE);
    }

    // </editor-fold>

    @Override
    public void hideAppMenu() {
        app_menu.setVisible(false);
        // KLUDGE:for CORE-5761 set flex to 0 so that search box shows up
        boxData.setFlex(0);
    }

    @Override
    public void hideWorkflowMenu() {
        wf_menu.setVisible(false);
        // KLUDGE:for CORE-5761 set flex to 0 so that search box shows up
        boxData.setFlex(0);
    }

    // <editor-fold desc="Selection Handlers">
    @Override
    public void onAppCategorySelectionChanged(AppCategorySelectionChangedEvent event) {
        if (!event.getAppCategorySelection().isEmpty()) {
            appSearch.clear();
        }
    }

    @Override
    public void onAppSelectionChanged(final AppSelectionChangedEvent event) {
        app_menu.setEnabled(true);
        wf_menu.setEnabled(true);

        currentSelection.clear();
        currentSelection.addAll(event.getAppSelection());

        boolean deleteAppEnabled, editAppEnabled, submitAppEnabled, copyAppEnabled, appRunEnabled;
        boolean deleteWfEnabled, editWfEnabled, copyWfEnabled, wfRunEnabled,
                shareWithCollaboratorsMiEnabled;

        switch (currentSelection.size()) {
            case 0:
                deleteAppEnabled = false;
                editAppEnabled = false;
                submitAppEnabled = false;
                copyAppEnabled = false;
                appRunEnabled = false;

                deleteWfEnabled = false;
                editWfEnabled = false;
                copyWfEnabled = false;
                wfRunEnabled = false;
                shareWithCollaboratorsMiEnabled = false;


                break;
            case 1:
                final App selectedApp = currentSelection.get(0);
                final boolean isOwner = hasOwnerPermission(selectedApp);
                final boolean isEditable =
                        isOwner || hasWritePermission(selectedApp) || isIntegrator(selectedApp);
                final boolean isCopyable = isEditable || hasReadPermission(selectedApp);
                final boolean isSingleStep = selectedApp.getStepCount() == 1;
                final boolean isMultiStep = selectedApp.getStepCount() > 1;
                final boolean isAppPublic = selectedApp.isPublic();
                final boolean isAppDisabled = selectedApp.isDisabled();
                final boolean isRunnable = selectedApp.isRunnable();

                deleteAppEnabled = isSingleStep && !isAppPublic && isOwner;
                // allow owners to edit their app
                editAppEnabled = isSingleStep && isEditable;
                submitAppEnabled = isRunnable && !isAppPublic && isOwner;
                copyAppEnabled = isSingleStep && isCopyable;
                // App run menu item is left enabled so user can get error announcement
                appRunEnabled = isSingleStep && !isAppDisabled;

                deleteWfEnabled = isMultiStep && !isAppPublic && isOwner;
                editWfEnabled = isMultiStep && !isAppPublic && isEditable;
                copyWfEnabled = isMultiStep && isCopyable;
                // Wf run menu item is left enabled so user can get error announcement
                wfRunEnabled = isMultiStep && !isAppDisabled;
                GWT.log(selectedApp.getPermission() + "&&--&&");
                shareWithCollaboratorsMiEnabled = containsSharableApps(Arrays.asList(selectedApp));

                break;
            default:
                final boolean containsSingleStepApp = containsSingleStepApp(currentSelection);
                final boolean containsMultiStepApp = containsMultiStepApp(currentSelection);
                final boolean allSelectedAppsPrivate = allAppsPrivate(currentSelection);

                deleteAppEnabled = containsSingleStepApp && allSelectedAppsPrivate;
                editAppEnabled = false;
                submitAppEnabled = false;
                copyAppEnabled = false;
                appRunEnabled = false;

                deleteWfEnabled = containsMultiStepApp && allSelectedAppsPrivate;
                editWfEnabled = false;
                copyWfEnabled = false;
                wfRunEnabled = false;
                shareWithCollaboratorsMiEnabled = containsSharableApps(currentSelection);
        }

        shareMenuButton.setEnabled(submitAppEnabled || shareWithCollaboratorsMiEnabled);
        deleteApp.setEnabled(deleteAppEnabled);
        editApp.setEnabled(editAppEnabled);
        sharePublic.setEnabled(submitAppEnabled);
        GWT.log("menu enabled->" + shareWithCollaboratorsMiEnabled);
        shareCollab.setEnabled(shareWithCollaboratorsMiEnabled);
        copyApp.setEnabled(copyAppEnabled);
        appRun.setEnabled(appRunEnabled);

        deleteWf.setEnabled(deleteWfEnabled);
        editWf.setEnabled(editWfEnabled);
        copyWf.setEnabled(copyWfEnabled);
        wfRun.setEnabled(wfRunEnabled);
    }

    private boolean hasReadPermission(App selectedApp) {
        return selectedApp.getPermission() != null && selectedApp.getPermission()
                                                                 .equals(PermissionValue.read);
    }

    private boolean hasOwnerPermission(App selectedApp) {
        return selectedApp.getPermission() != null && selectedApp.getPermission()
                                                                 .equals(PermissionValue.own);
    }

    private boolean isIntegrator(App selectedApp) {
        return selectedApp.isPublic() && selectedApp.getIntegratorEmail()
                                                    .equalsIgnoreCase(userInfo.getEmail());
    }

    private boolean hasWritePermission(App selectedApp) {
        return selectedApp.getPermission() != null && selectedApp.getPermission()
                                                                 .equals(PermissionValue.write);
    }

    // </editor-fold>

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        app_menu.ensureDebugId(baseID + Ids.MENU_ITEM_APPS);
        appRun.ensureDebugId(baseID + Ids.MENU_ITEM_APPS + Ids.MENU_ITEM_USE_APP);
        createNewApp.ensureDebugId(baseID + Ids.MENU_ITEM_APPS + Ids.MENU_ITEM_CREATE_APP);
        requestTool.ensureDebugId(baseID + Ids.MENU_ITEM_APPS + Ids.MENU_ITEM_REQUEST_TOOL);
        copyApp.ensureDebugId(baseID + Ids.MENU_ITEM_APPS + Ids.MENU_ITEM_COPY_APP);
        editApp.ensureDebugId(baseID + Ids.MENU_ITEM_APPS + Ids.MENU_ITEM_EDIT_APP);
        deleteApp.ensureDebugId(baseID + Ids.MENU_ITEM_APPS + Ids.MENU_ITEM_DELETE_APP);

        shareMenuButton.ensureDebugId(baseID + Ids.MENU_ITEM_SHARE_APP);
        sharePublic.ensureDebugId(baseID + Ids.MENU_ITEM_SHARE_APP + Ids.MENU_ITEM_SHARE_APP_PUBLIC);
        shareCollab.ensureDebugId(baseID + Ids.MENU_ITEM_SHARE_APP + Ids.MENU_ITEM_SHARE_APP_COLLAB);

        wf_menu.ensureDebugId(baseID + Ids.MENU_ITEM_WF);
        wfRun.ensureDebugId(baseID + Ids.MENU_ITEM_WF + Ids.MENU_ITEM_USE_WF);
        createWorkflow.ensureDebugId(baseID + Ids.MENU_ITEM_WF + Ids.MENU_ITEM_CREATE_WF);
        copyWf.ensureDebugId(baseID + Ids.MENU_ITEM_WF + Ids.MENU_ITEM_COPY_WF);
        editWf.ensureDebugId(baseID + Ids.MENU_ITEM_WF + Ids.MENU_ITEM_EDIT_WF);
        deleteWf.ensureDebugId(baseID + Ids.MENU_ITEM_WF + Ids.MENU_ITEM_DELETE_WF);

        appSearch.ensureDebugId(baseID + Ids.MENU_ITEM_SEARCH);
    }

    boolean containsSharableApps(List<App> apps) {
        if(apps!=null && apps.size() > 0) {
            for (App a : apps) {
                System.out.println(a.getAppType());
                System.out.println(a.getPermission());
                if (!(a.getPermission() != null && a.getPermission().equals(PermissionValue.own)
                      && a.getAppType() != null && !a.getAppType().equals(App.EXTERNAL_APP))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    boolean containsSingleStepApp(List<App> apps) {
        for (App app : apps) {
            if (app.getStepCount() == 1) {
                return true;
            }
        }
        return false;
    }

    boolean containsMultiStepApp(List<App> apps) {
        for (App app : apps) {
            if (app.getStepCount() > 1) {
                return true;
            }
        }
        return false;
    }

    boolean allAppsPrivate(List<App> apps) {
        for (App app : apps) {
            if (app.isPublic()) {
                return false;
            }
        }
        return true;
    }

    @UiFactory
    AppSearchField createAppSearchField() {
        return new AppSearchField(loader);
    }

    // <editor-fold desc="UI Handlers">
    @UiHandler({ "appRun", "wfRun" })
    void appRunClicked(SelectionEvent<Item> event) {
        fireEvent(new RunAppSelected(currentSelection.iterator().next()));
    }

    @UiHandler("copyApp")
    void copyAppClicked(SelectionEvent<Item> event) {
        fireEvent(new CopyAppSelected(currentSelection));
    }

    @UiHandler("copyWf")
    void copyWorkFlowClicked(SelectionEvent<Item> event) {
        fireEvent(new CopyWorkflowSelected(currentSelection));
    }

    @UiHandler("createNewApp")
    void createNewAppClicked(SelectionEvent<Item> event) {
        fireEvent(new CreateNewAppSelected());
    }

    @UiHandler("createWorkflow")
    void createWorkflowClicked(SelectionEvent<Item> event) {
        fireEvent(new CreateNewWorkflowSelected());
    }

    @UiHandler({ "deleteApp", "deleteWf" })
    void deleteClicked(SelectionEvent<Item> event) {
        ConfirmMessageBox msgBox =
                new ConfirmMessageBox(appearance.warning(), appearance.appDeleteWarning());
        msgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (!Dialog.PredefinedButton.YES.equals(event.getHideButton())) {
                    return;
                }
                fireEvent(new DeleteAppsSelected(currentSelection));

            }
        });
        msgBox.show();
    }

    @UiHandler("editApp")
    void editAppClicked(SelectionEvent<Item> event) {
        Preconditions.checkState(currentSelection.size() == 1);
        fireEvent(new EditAppSelected(currentSelection.iterator().next()));
    }

    @UiHandler("editWf")
    void editWfClicked(SelectionEvent<Item> event) {
        Preconditions.checkState(currentSelection.size() == 1);
        fireEvent(new EditWorkflowSelected(currentSelection.iterator().next()));
    }

    @UiHandler("requestTool")
    void requestToolClicked(SelectionEvent<Item> event) {
        fireEvent(new RequestToolSelected());
    }

    @UiHandler({ "sharePublic" })
    void submitClicked(SelectionEvent<Item> event) {
        Preconditions.checkState(currentSelection.size() == 1);

        submitAppDialogAsyncProvider.get(new AsyncCallback<SubmitAppForPublicDialog>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(SubmitAppForPublicDialog result) {
                result.show(currentSelection.iterator().next());
            }
        });
    }

    // </editor-fold>

    @UiHandler("shareCollab")
    void shareWithCollaborator(SelectionEvent<Item> event) {
        fireEvent(new ShareAppsSelected(currentSelection));
    }

}
