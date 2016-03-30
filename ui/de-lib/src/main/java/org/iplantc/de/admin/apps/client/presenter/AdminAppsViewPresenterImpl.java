package org.iplantc.de.admin.apps.client.presenter;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.apps.client.AdminAppsToolbarView;
import org.iplantc.de.admin.apps.client.AdminAppsView;
import org.iplantc.de.admin.apps.client.AdminCategoriesView;
import org.iplantc.de.admin.apps.client.gin.factory.AdminAppsViewFactory;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.services.AppServiceFacade;

import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

/**
 * Presenter class for the Belphegor <code>AppsView</code>.
 * 
 * The belphegor uses a different {@link AppServiceFacade} implementation than the one used in the
 * Discovery Environment. Through the use of deferred binding, the different {@link AppServiceFacade}
 * implementations are resolved, enabling the ability to reuse code.
 * 
 * <b> There are two places in the {@link org.iplantc.de.apps.client.presenter.AppsViewPresenterImpl}
 * where this deferred binding takes place; in the
 * {@link org.iplantc.de.apps.client.presenter.categories.proxy.AppCategoryProxy}.
 * 
 * 
 * @author jstroot
 * 
 */
public class AdminAppsViewPresenterImpl implements AdminAppsView.AdminPresenter {

    private final AdminCategoriesView.Presenter categoriesPresenter;
    private final AdminAppsView view;


    @Inject
    AdminAppsViewPresenterImpl(final AdminAppsViewFactory viewFactory,
                               final AdminCategoriesView.Presenter categoriesPresenter,
                               final AdminAppsToolbarView.Presenter toolbarPresenter,
                               final AdminAppsGridView.Presenter gridPresenter) {
        this.categoriesPresenter = categoriesPresenter;
        this.view = viewFactory.create(categoriesPresenter,
                                       toolbarPresenter,
                                       gridPresenter);

        categoriesPresenter.getView().addAppCategorySelectedEventHandler(gridPresenter);
        categoriesPresenter.getView().addAppCategorySelectedEventHandler(gridPresenter.getView());
        categoriesPresenter.getView().addAppCategorySelectedEventHandler(toolbarPresenter.getView());

        gridPresenter.getView().addAppSelectionChangedEventHandler(toolbarPresenter.getView());
        gridPresenter.addStoreRemoveHandler(categoriesPresenter);

        toolbarPresenter.getView().addAddCategorySelectedHandler(categoriesPresenter);
        toolbarPresenter.getView().addRenameCategorySelectedHandler(categoriesPresenter);
        toolbarPresenter.getView().addDeleteCategorySelectedHandler(categoriesPresenter);
        toolbarPresenter.getView().addDeleteAppsSelectedHandler(gridPresenter);
        toolbarPresenter.getView().addRestoreAppSelectedHandler(gridPresenter);
        toolbarPresenter.getView().addCategorizeAppSelectedHandler(categoriesPresenter);
        toolbarPresenter.getView().addMoveCategorySelectedHandler(categoriesPresenter);
        toolbarPresenter.getView().addAppSearchResultLoadEventHandler(categoriesPresenter);
        toolbarPresenter.getView().addAppSearchResultLoadEventHandler(gridPresenter);
        toolbarPresenter.getView().addAppSearchResultLoadEventHandler(gridPresenter.getView());
        toolbarPresenter.getView().addBeforeAppSearchEventHandler(gridPresenter.getView());
    }

    @Override
    public void go(final HasOneWidget container,
                   final HasId selectedAppCategory) {
        categoriesPresenter.go(selectedAppCategory);
        container.setWidget(view);
    }

    @Override
    public void setViewDebugId(String baseId) {
        view.asWidget().ensureDebugId(baseId + Belphegor.AppIds.VIEW);
    }


//    @Override
//    public void onAppEditorSave(final App app,
//                                final AppDoc doc) {
//        final AsyncCallback<String> editCompleteCallback = new AppEditCompleteCallback(app);
//
//        final App appClone = AutoBeanCodex.decode(factory, App.class, "{}").as();
//
//        // do not send these field to service
//        appClone.setEditedDate(null);
//        appClone.setIntegrationDate(null);
//        appClone.setPipelineEligibility(null);
//        appClone.setStepCount(null);
//        appClone.setIntegratorName(null);
//        appClone.setIntegratorEmail(null);
//        appClone.setAppType(null);
//        appClone.setRating(null);
//
//        // copy rest of the fields
//        appClone.setId(app.getId());
//        appClone.setName(app.getName());
//        appClone.setDescription(app.getDescription());
//        appClone.setDeleted(app.isDeleted());
//        appClone.setDisabled(app.isDisabled());
//        appClone.setWikiUrl(app.getWikiUrl());
//
//        // Serialize App to JSON object
//        String jsonString = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(appClone)).getPayload();
//
//        final JSONObject jsonObj = jsonUtil.getObject(jsonString);
//
//        if (app.getName() != null) {
//            adminAppService.updateApplication(appClone.getId(), jsonObj, editCompleteCallback);
//        }
//        if(!Strings.isNullOrEmpty(doc.getDocumentation())) {
//            if (isDocUpdate) {
//                adminAppService.updateAppDoc(appClone.getId(),
//                                       getJsonFormat(doc.getDocumentation()),
//                                       new DocSaveCallbackImpl());
//            } else {
//                adminAppService.saveAppDoc(appClone.getId(),
//                                           getJsonFormat(doc.getDocumentation()),
//                                           new DocSaveCallbackImpl());
//            }
//        }
//
//    }
//
//    private String getJsonFormat(String doc) {
//        JSONObject json = new JSONObject();
//        json.put("documentation", new JSONString(doc));
//        return json.toString();
//    }
//
//    private final class DocSaveCallbackImpl implements AsyncCallback<String> {
//        @Override
//        public void onFailure(Throwable caught) {
//            ErrorHandler.post("Unable to update DE app documentation!", caught);
//
//        }
//
//        @Override
//        public void onSuccess(String result) {
//                                       IplantAnnouncer.getInstance()
//                                                      .schedule(new SuccessAnnouncementConfig("App documentation updated!"));
//        }
//    }
//
//    private class AppEditCompleteCallback implements AsyncCallback<String> {
//
//        private final App app;
//
//        public AppEditCompleteCallback(App app) {
//            this.app = app;
//        }
//
//        @Override
//        public void onSuccess(String result) {
//            // update app in the grid
//            view.getAppsGrid().getStore().update(app);
//        }
//
//        @Override
//        public void onFailure(Throwable caught) {
//            ErrorHandler.post(appearance.updateApplicationError());
//        }
//    }

}
