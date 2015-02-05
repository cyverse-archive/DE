package org.iplantc.de.apps.client.presenter;

import org.iplantc.de.apps.client.events.AppPublishedEvent;
import org.iplantc.de.apps.client.presenter.proxy.PublicAppCategoryProxy;
import org.iplantc.de.apps.client.views.SubmitAppForPublicUseView;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppRefLink;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.shared.services.ConfluenceServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;

import java.util.ArrayList;
import java.util.List;

public class SubmitAppForPublicPresenter implements SubmitAppForPublicUseView.Presenter {

    private final SubmitAppForPublicUseView view;
    private AsyncCallback<String> callback;
    private final AppUserServiceFacade appService;
    private final PublicAppCategoryProxy appGroupProxy;
    private final EventBus eventBus;
    private final IplantDisplayStrings displayStrings;
    private final IplantErrorStrings errorStrings;
    private final ConfluenceServiceAsync confluenceService;
    @Inject
    JsonUtil jsonUtil;

    @Inject
    public SubmitAppForPublicPresenter(final SubmitAppForPublicUseView view,
                                       final AppUserServiceFacade appService,
                                       final PublicAppCategoryProxy appGroupProxy,
                                       final EventBus eventBus,
                                       final IplantDisplayStrings displayStrings,
                                       final IplantErrorStrings errorStrings,
                                       final ConfluenceServiceAsync confluenceService) {
        this.view = view;
        this.appService = appService;
        this.appGroupProxy = appGroupProxy;
        this.eventBus = eventBus;
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;
        this.confluenceService = confluenceService;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
        // Fetch AppCategories
        appGroupProxy.setLoadHpc(false);
        appGroupProxy.load(null, new AsyncCallback<List<AppCategory>>() {
            @Override
            public void onSuccess(List<AppCategory> result) {
                addAppCategory(null, result);
                view.expandAppCategories();
                // remove workspace node from store
                view.getTreeStore()
                    .remove(view.getTreeStore()
                                .findModelWithKey(DEProperties.getInstance().getDefaultBetaCategoryId()));
            }

            private void addAppCategory(AppCategory parent, List<AppCategory> children) {
                if ((children == null) || children.isEmpty()) {
                    return;
                }
                if (parent == null) {
                    view.getTreeStore().add(children);
                } else {
                    view.getTreeStore().add(parent, children);
                }

                for (AppCategory ag : children) {
                    addAppCategory(ag, ag.getCategories());
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(errorStrings.publishFailureDefaultMessage(), caught);
            }
        });
    }

    @Override
    public void onSubmit() {
        if (view.validate()) {
            publishApp(view.toJson());
        } else {
            AlertMessageBox amb = new AlertMessageBox(displayStrings.warning(),
                                                      displayStrings.publicSubmitTip());
            amb.show();
        }
    }

    private void getAppDetails() {
        appService.getAppDetails(view.getSelectedApp().getId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(errorStrings.publishFailureDefaultMessage(), caught);
            }

            @Override
            public void onSuccess(String result) {
                JSONObject obj = jsonUtil.getObject(result);
                JSONArray arr = jsonUtil.getArray(obj, "references");
                if (arr != null && arr.size() > 0) {
                    view.loadReferences(parseRefLinks(arr));
                }
            }
        });
    }

    private void publishApp(final JSONObject obj) {
        final AutoProgressMessageBox pmb = new AutoProgressMessageBox(displayStrings.submitForPublicUse(),
                                                                      displayStrings.submitRequest());
        pmb.setProgressText(displayStrings.submitting());
        pmb.setClosable(false);
        pmb.getProgressBar().setInterval(100);
        pmb.auto();
        pmb.show();

        appService.publishToWorld(obj, jsonUtil.getString(obj, "id"), new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                pmb.hide();
                eventBus.fireEvent(new AppPublishedEvent(view.getSelectedApp()));
            }

            @Override
            public void onFailure(Throwable caught) {
                pmb.hide();
            }
        });
    }

    private List<AppRefLink> parseRefLinks(JSONArray arr) {
        AppAutoBeanFactory factory = GWT.create(AppAutoBeanFactory.class);
        List<AppRefLink> linksList = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            AutoBean<AppRefLink> bean = AutoBeanCodex.decode(factory, AppRefLink.class, "{}");
            AppRefLink link = bean.as();
            String stringValue = arr.get(i).isString().stringValue();
            link.setId(stringValue);
            link.setRefLink(stringValue);
            linksList.add(link);
        }

        return linksList;
    }

    @Override
    public void go(HasOneWidget container, App selectedApp, AsyncCallback<String> callback) {
        view.setSelectedApp(selectedApp);
        this.callback = callback;
        getAppDetails();
        go(container);
    }

}
