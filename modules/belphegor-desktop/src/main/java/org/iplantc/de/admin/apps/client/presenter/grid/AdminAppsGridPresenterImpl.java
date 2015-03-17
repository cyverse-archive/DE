package org.iplantc.de.admin.apps.client.presenter.grid;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.apps.client.events.selection.RestoreAppSelected;
import org.iplantc.de.admin.apps.client.gin.factory.AdminAppsGridViewFactory;
import org.iplantc.de.admin.apps.client.views.editor.AppEditor;
import org.iplantc.de.admin.desktop.client.services.AppAdminServiceFacade;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppDoc;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;

import java.util.List;

/**
 * @author jstroot
 */
public class AdminAppsGridPresenterImpl implements AdminAppsGridView.Presenter,
                                                   AppNameSelectedEvent.AppNameSelectedEventHandler {


    @Inject AppAdminServiceFacade adminAppService;
    @Inject AppServiceFacade appService;
    @Inject AdminAppsGridView.Presenter.Appearance appearance;
    @Inject AppAutoBeanFactory factory;
    @Inject JsonUtil jsonUtil;
    @Inject IplantAnnouncer announcer;

    private final ListStore<App> listStore;
    private final AdminAppsGridView view;
    protected App desiredSelectedApp;

    // a flag to determine if doc
    private boolean isDocUpdate;

    @Inject
    AdminAppsGridPresenterImpl(final AdminAppsGridViewFactory viewFactory,
                               final ListStore<App> listStore) {
        this.listStore = listStore;
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
        if (event.getAppCategorySelection().isEmpty()) {
            return;
        }
        Preconditions.checkArgument(event.getAppCategorySelection().size() == 1);
        view.mask(appearance.getAppsLoadingMask());

        final AppCategory appCategory = event.getAppCategorySelection().iterator().next();
        appService.getApps(appCategory, new AsyncCallback<List<App>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                view.unmask();
            }

            @Override
            public void onSuccess(final List<App> apps) {
                listStore.clear();
                listStore.addAll(apps);

                if (getDesiredSelectedApp() != null) {
                    view.getGrid().getSelectionModel().select(getDesiredSelectedApp(), false);
                } else if (listStore.size() > 0) {
                    // Select first app
                    view.getGrid().getSelectionModel().select(listStore.get(0), false);
                }
                setDesiredSelectedApp(null);
                view.unmask();
            }
        });
    }

    @Override
    public void onAppNameSelected(final AppNameSelectedEvent event) {
        adminAppService.getAppDoc(event.getSelectedApp(), new AsyncCallback<AppDoc>() {

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
            public void onSuccess(final AppDoc result) {
                // Get result
                AppEditor.Presenter appEditorPresenter = null;
                new AppEditor(event.getSelectedApp(), result, appEditorPresenter).show();
                isDocUpdate = true;
            }
        });

    }

    @Override
    public void onDeleteAppsSelected(final DeleteAppsSelected event) {
        Preconditions.checkArgument(event.getAppsToBeDeleted().size() == 1);
        final App selectedApp = event.getAppsToBeDeleted().iterator().next();

        view.mask(appearance.deleteAppLoadingMask());
        adminAppService.deleteApp(selectedApp,
                                  new AsyncCallback<Void>() {

                                      @Override
                                      public void onFailure(Throwable caught) {
                                          view.unmask();
                                          announcer.schedule(new ErrorAnnouncementConfig(appearance.deleteApplicationError(selectedApp.getName())));
                                      }

                                      @Override
                                      public void onSuccess(Void result) {
                                          view.unmask();
                                          //  eventBus.fireEvent(new CatalogCategoryRefreshEvent());
                                          view.getGrid().getSelectionModel().deselectAll();
                                          listStore.remove(selectedApp);
                                      }
                                  });
    }

    @Override
    public void onRestoreAppSelected(final RestoreAppSelected event) {
        Preconditions.checkArgument(event.getApps().size() == 1);
        final App selectedApp = event.getApps().iterator().next();
        Preconditions.checkArgument(selectedApp.isDeleted());

        view.mask(appearance.restoreAppLoadingMask());
        adminAppService.restoreApp(selectedApp, new AsyncCallback<App>() {

            @Override
            public void onFailure(Throwable caught) {
                view.unmask();
                JSONObject obj = JSONParser.parseStrict(caught.getMessage()).isObject();
                String reason = jsonUtil.trim(obj.get("reason").toString());
                if (reason.contains("orphaned")) {
                    announcer.schedule(new ErrorAnnouncementConfig(appearance.restoreAppFailureMsg(selectedApp.getName())));
                } else {
                    announcer.schedule(new ErrorAnnouncementConfig(reason));
                }
            }

            @Override
            public void onSuccess(App result) {
                view.unmask();
                List<String> categoryNames = Lists.newArrayList();
                for(AppCategory category : result.getGroups()){
                    categoryNames.add(category.getName());
                }

                String joinedCatNames = Joiner.on(",").join(categoryNames);
                announcer.schedule(new SuccessAnnouncementConfig(appearance.restoreAppSuccessMsgTitle() + "\n"
                                                                     + appearance.restoreAppSuccessMsg(result.getName(),
                                                                                                       joinedCatNames)));
                }
                // eventBus.fireEvent(new CatalogCategoryRefreshEvent());
            });
    }

    App getDesiredSelectedApp() {
        return desiredSelectedApp;
    }

    void setDesiredSelectedApp(App desiredSelectedApp) {
        this.desiredSelectedApp = desiredSelectedApp;
    }
}
