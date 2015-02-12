package org.iplantc.de.admin.desktop.client.apps.presenter;

import org.iplantc.de.admin.desktop.client.apps.views.AdminAppsView;
import org.iplantc.de.admin.desktop.client.apps.views.AppCategorizeView;
import org.iplantc.de.admin.desktop.client.apps.views.AppCategorizeViewImpl;
import org.iplantc.de.admin.desktop.client.apps.views.editors.AppEditor;
import org.iplantc.de.admin.desktop.client.events.CatalogCategoryRefreshEvent;
import org.iplantc.de.admin.desktop.client.events.CatalogCategoryRefreshEventHandler;
import org.iplantc.de.admin.desktop.client.models.BelphegorAdminProperties;
import org.iplantc.de.admin.desktop.client.services.AppAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.services.callbacks.AdminServiceCallback;
import org.iplantc.de.admin.desktop.client.services.model.AppAdminServiceRequestAutoBeanFactory;
import org.iplantc.de.admin.desktop.client.services.model.AppCategorizeRequest;
import org.iplantc.de.admin.desktop.client.services.model.AppCategorizeRequest.CategoryRequest;
import org.iplantc.de.apps.client.events.AppNameSelectedEvent;
import org.iplantc.de.apps.client.presenter.AppsViewPresenterImpl;
import org.iplantc.de.apps.client.presenter.proxy.AppCategoryProxy;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppMetadataServiceFacade;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.commons.client.views.dialogs.IPlantPromptDialog;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Presenter class for the Belphegor <code>AppsView</code>.
 * 
 * The belphegor uses a different {@link AppServiceFacade} implementation than the one used in the
 * Discovery Environment. Through the use of deferred binding, the different {@link AppServiceFacade}
 * implementations are resolved, enabling the ability to reuse code.
 * 
 * <b> There are two places in the {@link org.iplantc.de.apps.client.presenter.AppsViewPresenterImpl}
 * where this deferred binding takes place; in the
 * {@link #go(com.google.gwt.user.client.ui.HasOneWidget)} method, and in the
 * {@link org.iplantc.de.apps.client.presenter.proxy.AppCategoryProxy}.
 * 
 * 
 * @author jstroot
 * 
 */
public class BelphegorAppsViewPresenterImpl extends AppsViewPresenterImpl implements
                                                                         AdminAppsView.AdminPresenter,
                                                                         AppEditor.Presenter {

    private final AppsView view;
    private final AppAdminServiceFacade adminAppService;
    private final EventBus eventBus;

    private final IplantAnnouncer announcer;

    @Inject AppAutoBeanFactory factory;
    @Inject AppAdminServiceRequestAutoBeanFactory serviceFactory;
    @Inject AdminPresenterAppearance appearance;
    @Inject BelphegorAdminProperties properties;
    @Inject JsonUtil jsonUtil;

    @Inject
    public BelphegorAppsViewPresenterImpl(final AppsView view,
                                          final AppCategoryProxy proxy,
                                          final AppAdminServiceFacade appService,
                                          final AppUserServiceFacade appUserService,
                                          final EventBus eventBus,
                                          final UserInfo userInfo,
                                          final DEProperties props,
                                          final IplantAnnouncer announcer,
                                          final IplantDisplayStrings displayStrings,
                                          final IplantErrorStrings errorStrings,
                                          final AppMetadataServiceFacade metadataFacade) {
        super(view,
              proxy,
              appService,
              appUserService,
              eventBus,
              userInfo,
              props,
              announcer,
              displayStrings,
              errorStrings,
              metadataFacade);
        this.view = view;
        this.adminAppService = appService;
        this.eventBus = eventBus;
        this.announcer = announcer;

        eventBus.addHandler(CatalogCategoryRefreshEvent.TYPE, new CatalogCategoryRefreshEventHandler() {

            @Override
            public void onRefresh(CatalogCategoryRefreshEvent event) {
                reloadAppCategories(getSelectedAppCategory(), getSelectedApp());
            }
        });
    }

    @Override
    public void onAppNameSelected(AppNameSelectedEvent event) {
        new AppEditor(event.getSelectedApp(), this).show();
    }

    @Override
    public void onAddAppCategoryClicked() {
        if (getSelectedAppCategory() == null) {
            return;
        }
        final AppCategory selectedAppCategory = getSelectedAppCategory();

        // Check if a new AppCategory can be created in the target AppCategory.
        if ((!selectedAppCategory.getName().contains("Public Apps"))
                && selectedAppCategory.getAppCount() > 0
                && (selectedAppCategory.getCategories() != null && selectedAppCategory.getCategories()
                                                                                      .size() == 0)
                || ((properties.getDefaultTrashAppCategoryId().equalsIgnoreCase(selectedAppCategory.getId())) || properties.getDefaultBetaAppCategoryId()
                                                                                                                           .equalsIgnoreCase(selectedAppCategory.getId()))) {
            ErrorHandler.post(appearance.addCategoryPermissionError());
            return;
        }

        final IPlantPromptDialog dlg = new IPlantPromptDialog(appearance.add(), 0, "", null);
        dlg.setHeadingText(appearance.addCategoryPrompt());
        dlg.addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {

                final String name = dlg.getFieldText();

                view.maskCenterPanel(appearance.addCategoryLoadingMask());
                adminAppService.addCategory(name,
                                            selectedAppCategory.getId(),
                                            new AdminServiceCallback() {
                                                @Override
                                                protected void onSuccess(JSONObject jsonResult) {

                                                    // Get result
                                                    AutoBean<AppCategory> group = AutoBeanCodex.decode(factory,
                                                                                                       AppCategory.class,
                                                                                                       jsonResult.toString());

                                                    view.addAppCategory(selectedAppCategory, group.as());
                                                    view.unMaskCenterPanel();
                                                }

                                                @Override
                                                protected String getErrorMessage() {
                                                    view.unMaskCenterPanel();
                                                    return appearance.addAppCategoryError(name);
                                                }
                                            });

            }
        });
        dlg.show();
    }

    @Override
    public void onRenameAppCategoryClicked() {
        if (getSelectedAppCategory() == null) {
            return;
        }
        final AppCategory selectedAppCategory = getSelectedAppCategory();

        PromptMessageBox msgBox = new PromptMessageBox(appearance.renameCategory(),
                                                       appearance.renamePrompt());
        final TextField field = ((TextField)msgBox.getField());
        field.setAutoValidate(true);
        field.setAllowBlank(false);
        field.setText(selectedAppCategory.getName());
        msgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (PredefinedButton.OK.equals(event.getHideButton())) {
                    view.maskWestPanel(appearance.renameAppCategoryLoadingMask());
                    adminAppService.renameAppCategory(selectedAppCategory.getId(),
                                                      field.getText(),
                                                      new AsyncCallback<String>() {

                                                          @Override
                                                          public void onSuccess(String result) {
                                                              AutoBean<AppCategory> group = AutoBeanCodex.decode(factory,
                                                                                                                 AppCategory.class,
                                                                                                                 result);
                                                              selectedAppCategory.setName(group.as()
                                                                                               .getName());
                                                              view.updateAppCategory(selectedAppCategory);
                                                              view.unMaskWestPanel();
                                                          }

                                                          @Override
                                                          public void onFailure(Throwable caught) {
                                                              ErrorHandler.post(appearance.renameCategoryError(selectedAppCategory.getName()));
                                                              view.unMaskWestPanel();
                                                          }
                                                      });
                }

            }
        });
        msgBox.show();
    }

    @Override
    protected void selectFirstApp() {
        // Do Nothing
    }

    @Override
    public void onDeleteCatClicked() {
        if (getSelectedAppCategory() != null) {
            final AppCategory selectedAppCategory = getSelectedAppCategory();

            // Determine if the selected AppCategory can be deleted.
            if (selectedAppCategory.getAppCount() > 0) {
                ErrorHandler.post(appearance.deleteCategoryPermissionError());
                return;
            }

            ConfirmMessageBox msgBox = new ConfirmMessageBox(appearance.confirmDeleteAppCategoryWarning(),
                                                             appearance.confirmDeleteAppCategory(selectedAppCategory.getName()));
            msgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                @Override
                public void onDialogHide(DialogHideEvent event) {
                    if (PredefinedButton.YES.equals(event.getHideButton())) {
                        view.maskWestPanel(appearance.deleteAppCategoryLoadingMask());
                        adminAppService.deleteAppCategory(selectedAppCategory.getId(),
                                                          new AsyncCallback<String>() {

                                                              @Override
                                                              public void onSuccess(String result) {
                                                                  // Refresh the catalog, so that the
                                                                  // proper category counts
                                                                  // display.
                                                                  // FIXME JDS These events need to be
                                                                  // common to ui-applications.
                                                                  eventBus.fireEvent(new CatalogCategoryRefreshEvent());
                                                                  view.unMaskWestPanel();
                                                              }

                                                              @Override
                                                              public void onFailure(Throwable caught) {
                                                                  ErrorHandler.post(appearance.deleteAppCategoryError(selectedAppCategory.getName()));
                                                                  view.unMaskWestPanel();
                                                              }
                                                          });
                    }
                }
            });
            msgBox.show();

        }
    }

    @Override
    public void onDeleteAppClicked() {
        if (getSelectedApp() != null) {
            final App selectedApp = getSelectedApp();
            ConfirmMessageBox msgBox = new ConfirmMessageBox(appearance.confirmDeleteAppWarning(),
                                                             appearance.confirmDeleteAppTitle());
            msgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                @Override
                public void onDialogHide(DialogHideEvent event) {
                    if (PredefinedButton.YES.equals(event.getHideButton())) {
                        view.maskCenterPanel(appearance.deleteAppLoadingMask());
                        adminAppService.deleteApplication(selectedApp.getId(),
                                                          new AsyncCallback<String>() {

                                                              @Override
                                                              public void onSuccess(String result) {
                                                                  eventBus.fireEvent(new CatalogCategoryRefreshEvent());
                                                                  view.removeApp(selectedApp);
                                                                  view.unMaskCenterPanel();
                                                              }

                                                              @Override
                                                              public void onFailure(Throwable caught) {
                                                                  ErrorHandler.post(appearance.deleteApplicationError(selectedApp.getName()));
                                                                  view.unMaskCenterPanel();
                                                              }
                                                          });
                    }

                }
            });
            msgBox.show();
        }
    }

    @Override
    public void deleteSelectedApps() {
        // Do nothing, this is for the non-admin toolbar
    }

    @Override
    public void onRestoreAppClicked() {
        if (getSelectedApp() == null) {
            return;
        }
        final App selectedApp = getSelectedApp();
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

                    MessageBox msgBox = new MessageBox(appearance.restoreAppSuccessMsgTitle(),
                                                       appearance.restoreAppSuccessMsg(selectedApp.getName(),
                                                                                       names_display.toString()));
                    msgBox.setIcon(MessageBox.ICONS.info());
                    msgBox.setPredefinedButtons(PredefinedButton.OK);
                    msgBox.show();
                }
                eventBus.fireEvent(new CatalogCategoryRefreshEvent());
            }

            @Override
            public void onFailure(Throwable caught) {
                JSONObject obj = JSONParser.parseStrict(caught.getMessage()).isObject();
                String reason = jsonUtil.trim(obj.get("reason").toString());
                if (reason.contains("orphaned")) {
                    AlertMessageBox alertBox = new AlertMessageBox(appearance.restoreAppFailureMsgTitle(),
                                                                   appearance.restoreAppFailureMsg(selectedApp.getName()));
                    alertBox.show();
                } else {
                    ErrorHandler.post(reason);
                }
            }
        });
    }

    @Override
    public void onCategorizeAppClicked() {
        App selectedApp = view.getSelectedApp();
        view.maskCenterPanel(appearance.getAppDetailsLoadingMask());

        adminAppService.getAppDetails(selectedApp.getId(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                App appDetails = AutoBeanCodex.decode(factory, App.class, result).as();
                showCategorizeAppDialog(appDetails);
                view.unMaskCenterPanel();
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                view.unMaskCenterPanel();
            }
        });
    }

    private void showCategorizeAppDialog(final App selectedApp) {
        final AppCategorizePresenter presenter = new AppCategorizePresenter(new AppCategorizeViewImpl(false),
                                                                            selectedApp,
                                                                            properties);
        presenter.setAppCategories(view.getAppCategoryRoots());

        final IPlantDialog dlg = new IPlantDialog();
        dlg.addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                List<AppCategory> categories = presenter.getSelectedCategories();
                if (categories == null || categories.isEmpty()) {
                    announcer.schedule(new ErrorAnnouncementConfig(appearance.noCategoriesSelected()));
                } else {
                    doCategorizeSelectedApp(selectedApp, categories);
                }
            }
        });

        dlg.setHeadingText(appearance.selectCategories(selectedApp.getName()));
        dlg.setResizable(true);
        dlg.setOkButtonText(appearance.submit());

        presenter.go(dlg);
        dlg.show();
    }

    private void doCategorizeSelectedApp(final App selectedApp,
                                         final List<AppCategory> groupAppCategories) {
        view.maskCenterPanel(appearance.categorizeAppLoadingMask());
        AppCategorizeRequest request = buildAppCategorizeRequest(selectedApp, groupAppCategories);

        adminAppService.categorizeApp(request, new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                view.unMaskCenterPanel();

                List<String> groupNames = Lists.newArrayList();
                for (AppCategory group : groupAppCategories) {
                    groupNames.add(group.getName());
                }
                Collections.sort(groupNames, String.CASE_INSENSITIVE_ORDER);

                String successMsg = appearance.appCategorizeSuccess(selectedApp.getName(), groupNames);
                announcer.schedule(new SuccessAnnouncementConfig(successMsg));

                eventBus.fireEvent(new CatalogCategoryRefreshEvent());
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO Add error message for user
                ErrorHandler.post(caught);
                view.unMaskCenterPanel();
            }
        });
    }

    private AppCategorizeRequest buildAppCategorizeRequest(App selectedApp,
                                                           List<AppCategory> appCategories) {
        HasId appId = CommonModelUtils.getInstance().createHasIdFromString(selectedApp.getId());
        List<CategoryRequest> categories = Lists.newArrayList();
        List<String> cat_ids = Lists.newArrayList();
        for (AppCategory group : appCategories) {
            cat_ids.add(group.getId());
        }

        CategoryRequest categoryRequest = serviceFactory.categoryRequest().as();
        categoryRequest.setAppId(appId.getId());
        categoryRequest.setCategories(cat_ids);

        categories.add(categoryRequest);

        AppCategorizeRequest request = serviceFactory.appCategorizeRequest().as();
        request.setCategories(categories);

        return request;
    }

    @Override
    public void onAppEditorSave(final App app) {
        final AsyncCallback<String> editCompleteCallback = new AppEditCompleteCallback(app);

        final App appClone = AutoBeanCodex.decode(factory, App.class, "{}").as();

        // do not send these field to service
        appClone.setEditedDate(null);
        appClone.setIntegrationDate(null);
        appClone.setPipelineEligibility(null);
        appClone.setStepCount(null);
        appClone.setIntegratorName(null);
        appClone.setIntegratorEmail(null);
        appClone.setAppType(null);
        appClone.setRating(null);

        // copy rest of the fields
        appClone.setId(app.getId());
        appClone.setName(app.getName());
        appClone.setDescription(app.getDescription());
        appClone.setDeleted(app.isDeleted());
        appClone.setDisabled(app.isDisabled());
        appClone.setWikiUrl(app.getWikiUrl());

        // Serialize App to JSON object
        String jsonString = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(appClone)).getPayload();

        final JSONObject jsonObj = jsonUtil.getObject(jsonString);

        if (app.getName() != null) {
            adminAppService.updateApplication(appClone.getId(), jsonObj, editCompleteCallback);
        }

    }

    private class AppEditCompleteCallback implements AsyncCallback<String> {

        private final App app;

        public AppEditCompleteCallback(App app) {
            this.app = app;
        }

        @Override
        public void onSuccess(String result) {
            // update app in the grid
            view.getAppsGrid().getStore().update(app);
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(appearance.updateApplicationError());
        }
    }

    @Override
    public void moveAppCategory(final AppCategory parentCategory, final AppCategory childCategory) {
        adminAppService.moveCategory(childCategory.getId(),
                                     parentCategory.getId(),
                                     new AsyncCallback<String>() {

                                         @Override
                                         public void onSuccess(String result) {
                                             // Refresh the catalog, so that the proper category counts
                                             // display.
                                             // FIXME JDS These events need to be common to
                                             // ui-applications.
                                             eventBus.fireEvent(new CatalogCategoryRefreshEvent());
                                         }

                                         @Override
                                         public void onFailure(Throwable caught) {
                                             ErrorHandler.post(appearance.moveCategoryError(childCategory.getName()));
                                         }
                                     });
    }

    @Override
    public void moveApp(final AppCategory parentCategory, final App app) {
        // adminAppService.categorizeApp(app.getId(),
        // parentCategory.getId(),
        // new AsyncCallback<String>() {
        //
        // @Override
        // public void onSuccess(String result) {
        // // Refresh the catalog, so that the proper category
        // // counts
        // // display.
        // // FIXME JDS These events need to be common to
        // // ui-applications.
        // eventBus.fireEvent(new CatalogCategoryRefreshEvent());
        // }
        //
        // @Override
        // public void onFailure(Throwable caught) {
        // ErrorHandler.post(errorStrings.moveApplicationError(app.getName()));
        // }
        // });
        doCategorizeSelectedApp(app, Arrays.asList(parentCategory));
    }

    @Override
    public boolean canMoveAppCategory(AppCategory parentCategory, AppCategory childCategory) {
        if (parentCategory == null || childCategory == null) {
            return false;
        }

        // Don't allow a category drop onto itself.
        if (childCategory == parentCategory) {
            return false;
        }

        // Don't allow a category drop into a category leaf with apps in it.
        if (isLeaf(parentCategory) && parentCategory.getAppCount() > 0) {
            return false;
        }

        // Don't allow a category drop into one of its children.
        if (childCategory.getCategories() != null
                && childCategory.getCategories().contains(parentCategory)) {
            return false;
        }

        // Don't allow a category drop into its own parent.
        if (childCategory.getCategories() != null) {
            return !parentCategory.getCategories().contains(childCategory);
        }

        return true;

    }

    @Override
    public boolean canMoveApp(AppCategory parentCategory, App app) {
        if (parentCategory == null || app == null) {
            return false;
        }

        // Apps can only be dropped into leaf categories.
        return isLeaf(parentCategory);

    }

    private boolean isLeaf(AppCategory parentCategory) {
        return parentCategory.getCategories() == null || parentCategory.getCategories().isEmpty();
    }

    @Override
    public void onMoveCategoryClicked() {
        final IPlantDialog dlg = new IPlantDialog();
        final AppCategorizeView cat_view = new AppCategorizeViewImpl(true);
        cat_view.setAppCategories(view.getAppCategoryRoots());
        dlg.addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                AppCategory sourceCategory = getSelectedAppCategory();
                if (sourceCategory != null && null != cat_view.getSelectedCategories()
                        && cat_view.getSelectedCategories().size() > 0) {
                    AppCategory destinationCategory = cat_view.getSelectedCategories().get(0);
                    if (canMoveAppCategory(destinationCategory, sourceCategory)) {
                        moveAppCategory(destinationCategory, sourceCategory);
                    } else {
                        ErrorHandler.post(appearance.invalidMoveMsg());
                    }
                }
            }
        });

        dlg.setHeadingText(appearance.moveCategory());
        dlg.setResizable(true);
        dlg.setOkButtonText(appearance.submit());
        dlg.add(cat_view.asWidget());
        dlg.show();

    }
}
