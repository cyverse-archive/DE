package org.iplantc.de.admin.apps.client.presenter.grid;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.apps.client.events.selection.RestoreAppSelected;
import org.iplantc.de.admin.apps.client.gin.factory.AdminAppsGridViewFactory;
import org.iplantc.de.admin.apps.client.views.editor.AppEditor;
import org.iplantc.de.admin.desktop.client.services.AppAdminServiceFacade;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.apps.client.models.AppModelKeyProvider;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppDoc;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;

/**
 * @author jstroot
 */
public class AdminAppsGridPresenterImpl implements AdminAppsGridView.Presenter,
                                                   AppNameSelectedEvent.AppNameSelectedEventHandler {


    private final AppAdminServiceFacade adminAppService;
    private final ListStore<App> listStore;
    private final AdminAppsGridView view;
    @Inject AppAutoBeanFactory factory;
    @Inject AdminAppsGridView.Presenter.Appearance appearance;
    @Inject JsonUtil jsonUtil;

    // a flag to determine if doc
    private boolean isDocUpdate;

    @Inject
    AdminAppsGridPresenterImpl(final AdminAppsGridViewFactory viewFactory,
                               final AppAdminServiceFacade adminAppService) {
        this.adminAppService = adminAppService;
        listStore = new ListStore<>(new AppModelKeyProvider());
        view = viewFactory.create(listStore);

        view.addAppNameSelectedEventHandler(this);

    }

    @Override
    public HandlerRegistration addStoreRemoveHandler(StoreRemoveEvent.StoreRemoveHandler<App> handler) {
        return listStore.addStoreRemoveHandler(handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AdminAppsGridView getView() {
        return view;
    }

    @Override
    public void onAppCategorySelectionChanged(AppCategorySelectionChangedEvent event) {

    }

    @Override
    public void onAppNameSelected(final AppNameSelectedEvent event) {
        adminAppService.getAppDoc(event.getSelectedApp().getId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                IplantAnnouncer.getInstance()
                               .schedule(new ErrorAnnouncementConfig("Documentation not found!"));
                AutoBean<AppDoc> doc = AutoBeanCodex.decode(factory, AppDoc.class, "{}");
                AppEditor.Presenter appEditorPresenter = null;
                new AppEditor(event.getSelectedApp(), doc.as(), appEditorPresenter).show();
                isDocUpdate = false;
            }

            @Override
            public void onSuccess(String result) {
                // Get result
                AutoBean<AppDoc> doc = AutoBeanCodex.decode(factory, AppDoc.class, result);
                AppEditor.Presenter appEditorPresenter = null;
                new AppEditor(event.getSelectedApp(), doc.as(), appEditorPresenter).show();
                isDocUpdate = true;
            }
        });

    }

    @Override
    public void onDeleteAppsSelected(DeleteAppsSelected event) {
        final App selectedApp = event.getAppsToBeDeleted().iterator().next();
        // FIXME Move dlg to view
        ConfirmMessageBox msgBox = new ConfirmMessageBox(appearance.confirmDeleteAppWarning(),
                                                         appearance.confirmDeleteAppTitle());
        msgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (Dialog.PredefinedButton.YES.equals(event.getHideButton())) {
                    view.mask(appearance.deleteAppLoadingMask());
                    adminAppService.deleteApplication(selectedApp.getId(),
                                                      new AsyncCallback<String>() {

                                                          @Override
                                                          public void onSuccess(String result) {
                                                              // FIXME Cat presenter needs to listen to list store remove events.
//                                                              eventBus.fireEvent(new CatalogCategoryRefreshEvent());
                                                              view.getGrid().getSelectionModel().deselectAll();
                                                              view.getGrid();
                                                              listStore.remove(selectedApp);
                                                              view.unmask();
                                                          }

                                                          @Override
                                                          public void onFailure(Throwable caught) {
                                                              ErrorHandler.post(appearance.deleteApplicationError(selectedApp.getName()));
                                                              view.unmask();
                                                          }
                                                      });
                }

            }
        });
        msgBox.show();
    }

    @Override
    public void onRestoreAppSelected(RestoreAppSelected event) {
        Preconditions.checkArgument(event.getApps().size() == 1);

        final App selectedApp = event.getApps().iterator().next();
        final App appClone = AutoBeanCodex.decode(factory, App.class, "{}").as();
        appClone.setDeleted(false);

        // Serialize App to JSON object
        String jsonString = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(appClone)).getPayload();

        final JSONObject jsonObj = jsonUtil.getObject(jsonString);

        adminAppService.updateApplication(selectedApp.getId(), jsonObj, new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                JSONObject obj = JSONParser.parseStrict(result).isObject();
                JSONArray arr = obj.get("categories").isArray();
                if (arr != null && arr.size() > 0) {
                    StringBuilder names_display = new StringBuilder("");
                    for (int i = 0; i < arr.size(); i++) {
                        names_display.append(jsonUtil.trim(arr.get(0).isObject().get("name").toString()));
                        if (i != arr.size() - 1) {
                            names_display.append(",");
                        }
                    }

                    // FIXME use announcer
                    MessageBox msgBox = new MessageBox(appearance.restoreAppSuccessMsgTitle(),
                                                       appearance.restoreAppSuccessMsg(selectedApp.getName(),
                                                                                       names_display.toString()));
                    msgBox.setIcon(MessageBox.ICONS.info());
                    msgBox.setPredefinedButtons(Dialog.PredefinedButton.OK);
                    msgBox.show();
                }
//                eventBus.fireEvent(new CatalogCategoryRefreshEvent());
            }

            @Override
            public void onFailure(Throwable caught) {
                JSONObject obj = JSONParser.parseStrict(caught.getMessage()).isObject();
                String reason = jsonUtil.trim(obj.get("reason").toString());
                if (reason.contains("orphaned")) {
                    // FIXME use announcer
                    AlertMessageBox alertBox = new AlertMessageBox(appearance.restoreAppFailureMsgTitle(),
                                                                   appearance.restoreAppFailureMsg(selectedApp.getName()));
                    alertBox.show();
                } else {
                    ErrorHandler.post(reason);
                }
            }

        });
    }
}
