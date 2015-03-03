package org.iplantc.de.apps.client.presenter.submit;

import org.iplantc.de.apps.client.SubmitAppForPublicUseView;
import org.iplantc.de.apps.client.events.AppPublishedEvent;
import org.iplantc.de.apps.client.presenter.proxy.PublicAppCategoryProxy;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppRefLink;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;

import java.util.List;

/**
 * @author jstroot
 */
public class SubmitAppForPublicPresenter implements SubmitAppForPublicUseView.Presenter {

    interface SubmitAppPresenterBeanFactory extends AutoBeanFactory {
        AutoBean<AppRefLink> appRefLink();
    }

    @Inject SubmitAppForPublicUseView view;
    @Inject AppUserServiceFacade appService;
    @Inject PublicAppCategoryProxy appGroupProxy;
    @Inject EventBus eventBus;
    @Inject SubmitAppForPublicUseView.SubmitAppAppearance appearance;
    @Inject JsonUtil jsonUtil;
    @Inject SubmitAppPresenterBeanFactory factory;

    private AsyncCallback<String> callback;

    @Inject
    SubmitAppForPublicPresenter() {
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
                ErrorHandler.post(appearance.publishFailureDefaultMessage(), caught);
            }
        });
    }

    @Override
    public void onSubmit() {
        if (view.validate()) {
            publishApp(view.toJson());
        } else {
            AlertMessageBox amb = new AlertMessageBox(appearance.warning(),
                                                      appearance.publicSubmitTip());
            amb.show();
        }
    }

    private void getAppDetails() {
        appService.getAppDetails(view.getSelectedApp(), new AsyncCallback<App>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(appearance.publishFailureDefaultMessage(), caught);
            }

            @Override
            public void onSuccess(App result) {
                view.loadReferences(parseRefLinks(result.getReferences()));
            }
        });
    }

    private void publishApp(final JSONObject obj) {
        final AutoProgressMessageBox pmb = new AutoProgressMessageBox(appearance.submitForPublicUse(),
                                                                      appearance.submitRequest());
        pmb.setProgressText(appearance.submitting());
        pmb.setClosable(false);
        pmb.getProgressBar().setInterval(100);
        pmb.auto();
        pmb.show();

        appService.publishToWorld(obj, jsonUtil.getString(obj, "id"), new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                pmb.hide();
                eventBus.fireEvent(new AppPublishedEvent(view.getSelectedApp()));
                if (callback != null) {
                    callback.onSuccess(jsonUtil.getString(obj, "name"));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                pmb.hide();
                callback.onFailure(caught);
            }
        });
    }

    private List<AppRefLink> parseRefLinks(List<String> arr) {
        List<AppRefLink> linksList = Lists.newArrayList();
        for(String ref : arr){
            AppRefLink refLink = factory.appRefLink().as();
            refLink.setId(ref);
            refLink.setRefLink(ref);
            linksList.add(refLink);
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
