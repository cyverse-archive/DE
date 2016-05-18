package org.iplantc.de.apps.client.presenter.submit;

import org.iplantc.de.apps.client.SubmitAppForPublicUseView;
import org.iplantc.de.apps.client.events.AppPublishedEvent;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppRefLink;
import org.iplantc.de.client.models.apps.PublishAppRequest;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.services.OntologyServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;

import java.util.List;
import java.util.Map;

/**
 * @author jstroot
 */
public class SubmitAppForPublicPresenter implements SubmitAppForPublicUseView.Presenter {

    interface SubmitAppPresenterBeanFactory extends AutoBeanFactory {
        AutoBean<AppRefLink> appRefLink();
    }

    private class HierarchiesCallback implements AsyncCallback<List<OntologyHierarchy>> {
        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(appearance.publishFailureDefaultMessage(), caught);
        }

        @Override
        public void onSuccess(List<OntologyHierarchy> result) {
            addHierarchies(view.getTreeStore(), null, result);
        }

        void addHierarchies(TreeStore<OntologyHierarchy> treeStore, OntologyHierarchy parent, List<OntologyHierarchy> children) {
            if ((children == null)
                || children.isEmpty()) {
                return;
            }
            if (parent == null) {
                treeStore.add(children);

            } else {
                treeStore.add(parent, children);
            }

            helperMap(children);

            for (OntologyHierarchy hierarchy : children) {
                addHierarchies(treeStore, hierarchy, hierarchy.getSubclasses());
            }
        }

        void helperMap(List<OntologyHierarchy> children) {
            for (OntologyHierarchy hierarchy : children) {
                String iri = hierarchy.getIri();
                List<OntologyHierarchy> hierarchies = iriToHierarchyMap.get(iri);
                if (hierarchies == null) {
                    hierarchies = Lists.newArrayList();
                }
                hierarchies.add(hierarchy);
                iriToHierarchyMap.put(hierarchy.getIri(), hierarchies);
            }
        }
    }

    @Inject AppUserServiceFacade appService;
    @Inject SubmitAppForPublicUseView.SubmitAppAppearance appearance;
    @Inject EventBus eventBus;
    @Inject SubmitAppPresenterBeanFactory factory;
    @Inject JsonUtil jsonUtil;
    @Inject SubmitAppForPublicUseView view;
    private OntologyServiceFacade ontologyService;
    private AsyncCallback<String> callback;
    private Map<String, List<OntologyHierarchy>> iriToHierarchyMap = new FastMap<>();

    @Inject
    SubmitAppForPublicPresenter(OntologyServiceFacade ontologyService) {
        this.ontologyService = ontologyService;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
        // Fetch Hierarchies
        ontologyService.getRootHierarchies(new HierarchiesCallback());
    }

    @Override
    public void go(HasOneWidget container, App selectedApp, AsyncCallback<String> callback) {
        view.setSelectedApp(selectedApp);
        this.callback = callback;
        getAppDetails();
        go(container);
    }

    @Override
    public void onSubmit() {
        if (view.validate()) {
            publishApp(view.getPublishAppRequest());
        } else {
            AlertMessageBox amb = new AlertMessageBox(appearance.warning(),
                                                      appearance.completeRequiredFieldsError());
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

    private List<AppRefLink> parseRefLinks(List<String> arr) {
        List<AppRefLink> linksList = Lists.newArrayList();
        for (String ref : arr) {
            AppRefLink refLink = factory.appRefLink().as();
            refLink.setId(ref);
            refLink.setRefLink(ref);
            linksList.add(refLink);
        }

        return linksList;
    }

    private void publishApp(final PublishAppRequest publishAppRequest) {
        final AutoProgressMessageBox pmb = new AutoProgressMessageBox(appearance.submitForPublicUse(),
                                                                      appearance.submitRequest());
        pmb.setProgressText(appearance.submitting());
        pmb.setClosable(false);
        pmb.getProgressBar().setInterval(100);
        pmb.auto();
        pmb.show();

        appService.publishToWorld(publishAppRequest, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                pmb.hide();
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Void result) {
                pmb.hide();
                eventBus.fireEvent(new AppPublishedEvent(view.getSelectedApp()));
                if (callback != null) {
                    callback.onSuccess(publishAppRequest.getName());
                }
            }
        });
    }

}
