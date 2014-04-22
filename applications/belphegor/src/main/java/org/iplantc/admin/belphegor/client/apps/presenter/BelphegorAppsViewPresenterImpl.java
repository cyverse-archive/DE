package org.iplantc.admin.belphegor.client.apps.presenter;

import org.iplantc.admin.belphegor.client.I18N;
import org.iplantc.admin.belphegor.client.apps.views.AdminAppsView;
import org.iplantc.admin.belphegor.client.apps.views.AppCategorizeViewImpl;
import org.iplantc.admin.belphegor.client.apps.views.editors.AppEditor;
import org.iplantc.admin.belphegor.client.events.CatalogCategoryRefreshEvent;
import org.iplantc.admin.belphegor.client.events.CatalogCategoryRefreshEventHandler;
import org.iplantc.admin.belphegor.client.models.ToolIntegrationAdminProperties;
import org.iplantc.admin.belphegor.client.services.AppAdminServiceFacade;
import org.iplantc.admin.belphegor.client.services.callbacks.AdminServiceCallback;
import org.iplantc.admin.belphegor.client.services.model.AppAdminServiceRequestAutoBeanFactory;
import org.iplantc.admin.belphegor.client.services.model.AppCategorizeRequest;
import org.iplantc.admin.belphegor.client.services.model.AppCategorizeRequest.CategoryPath;
import org.iplantc.admin.belphegor.client.services.model.AppCategorizeRequest.CategoryRequest;
import org.iplantc.de.apps.client.presenter.AppsViewPresenterImpl;
import org.iplantc.de.apps.client.presenter.proxy.AppGroupProxy;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.apps.client.views.cells.AppHyperlinkCell;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantPromptDialog;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.shared.services.ConfluenceServiceFacade;

import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.Collections;
import java.util.List;

/**
 * Presenter class for the Belphegor <code>AppsView</code>.
 * 
 * The belphegor uses a different {@link AppServiceFacade} implementation than the one used in the
 * Discovery Environment. Through the use of deferred binding, the different {@link AppServiceFacade}
 * implementations are resolved, enabling the ability to reuse code.
 * 
 * <b> There are two places in the {@link org.iplantc.de.apps.client.presenter.AppsViewPresenterImpl} where this deferred binding takes place; in
 * the {@link #go(com.google.gwt.user.client.ui.HasOneWidget)} method, and in the {@link AppGroupProxy}.
 * 
 * 
 * @author jstroot
 * 
 */
public class BelphegorAppsViewPresenterImpl extends AppsViewPresenterImpl implements AdminAppsView.AdminPresenter,
                                                                                     AppEditor.Presenter {

    private final AppAutoBeanFactory factory;
    private final AppAdminServiceRequestAutoBeanFactory serviceFactory;
    private final AppsView view;
    private final AppGroupProxy proxy;
    private final AppAdminServiceFacade adminAppService;
    private final AppUserServiceFacade appUserService;
    private final EventBus eventBus;
    private final UserInfo userInfo;
    private final DEProperties props;
    private final IplantAnnouncer announcer;
    private final IplantDisplayStrings displayStrings;
    private final IplantErrorStrings errorStrings;

    @Override
    public void onAppNameSelected(AppHyperlinkCell.AppNameSelectedEvent event) {
        new AppEditor(event.getSelectedApp(), this).show();
    }

    @Inject
    public BelphegorAppsViewPresenterImpl(final AppsView view,
                                          final AppGroupProxy proxy,
                                          AppAdminServiceFacade appService,
                                          AppUserServiceFacade appUserService,
                                          final AppAutoBeanFactory factory,
                                          final AppAdminServiceRequestAutoBeanFactory serviceFactory,
                                          final EventBus eventBus,
                                          final UserInfo userInfo,
                                          final DEProperties props,
                                          final IplantAnnouncer announcer,
                                          final IplantDisplayStrings displayStrings,
                                          final IplantErrorStrings errorStrings) {
        super(view, proxy, appService, appUserService, eventBus, userInfo, props, announcer, displayStrings, errorStrings);
        this.view = view;
        this.proxy = proxy;
        this.adminAppService = appService;
        this.appUserService = appUserService;
        this.factory = factory;
        this.serviceFactory = serviceFactory;
        this.eventBus = eventBus;
        this.userInfo = userInfo;
        this.props = props;
        this.announcer = announcer;
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;

        this.view.setPresenter(this);
    }

    @Override
    protected void initHandlers() {
        super.initHandlers();

        EventBus.getInstance().addHandler(CatalogCategoryRefreshEvent.TYPE,
                new CatalogCategoryRefreshEventHandler() {

                    @Override
                    public void onRefresh(CatalogCategoryRefreshEvent event) {
                        reloadAppGroups(getSelectedAppGroup(), getSelectedApp());
                    }
                });
    }

    @Override
    public void onAddAppGroupClicked() {
        if (getSelectedAppGroup() == null) {
            return;
        }
        final AppGroup selectedAppGroup = getSelectedAppGroup();

        ToolIntegrationAdminProperties props = ToolIntegrationAdminProperties.getInstance();

        // Check if a new AppGroup can be created in the target AppGroup.
        if ((!selectedAppGroup.getName().contains("Public Apps"))
                && selectedAppGroup.getAppCount() > 0
                && selectedAppGroup.getGroups().size() == 0
                || ((props.getDefaultTrashAnalysisGroupId().equalsIgnoreCase(selectedAppGroup.getId()))
                        || props.getDefaultBetaAnalysisGroupId().equalsIgnoreCase(selectedAppGroup.getId()))) {
            ErrorHandler.post(I18N.ERROR.addCategoryPermissionError());
            return;
        }

        final IPlantPromptDialog dlg = new IPlantPromptDialog(I18N.DISPLAY.add(), 0, "", null);
        dlg.setHeadingText(I18N.DISPLAY.addCategoryPrompt());
        dlg.addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {

                final String name = dlg.getFieldText();

                view.maskCenterPanel(I18N.DISPLAY.loadingMask());
                adminAppService.addCategory(name, selectedAppGroup.getId(), new AdminServiceCallback() {
                    @Override
                    protected void onSuccess(JSONObject jsonResult) {

                        // Get result
                        AutoBean<AppGroup> group = AutoBeanCodex.decode(factory, AppGroup.class,
                                jsonResult.get("category").toString());

                        view.addAppGroup(selectedAppGroup, group.as());
                        view.unMaskCenterPanel();
                    }

                    @Override
                    protected String getErrorMessage() {
                        view.unMaskCenterPanel();
                        return I18N.ERROR.addAppGroupError(name);
                    }
                });

            }
        });
        dlg.show();

    }

    @Override
    public void onRenameAppGroupClicked() {
        if (getSelectedAppGroup() == null) {
            return;
        }
        final AppGroup selectedAppGroup = getSelectedAppGroup();

        PromptMessageBox msgBox = new PromptMessageBox(I18N.DISPLAY.rename(),
                I18N.DISPLAY.renamePrompt());
        final TextField field = ((TextField)msgBox.getField());
        field.setAutoValidate(true);
        field.setAllowBlank(false);
        field.setText(selectedAppGroup.getName());
        msgBox.addHideHandler(new HideHandler() {

            @Override
            public void onHide(HideEvent event) {
                Dialog btn = (Dialog)event.getSource();
                String text = btn.getHideButton().getItemId();
                if (text.equals(PredefinedButton.OK.name())) {
                    view.maskWestPanel(I18N.DISPLAY.loadingMask());
                    adminAppService.renameAppGroup(selectedAppGroup.getId(), field.getText(),
                            new AsyncCallback<String>() {

                                @Override
                                public void onSuccess(String result) {
                                    AutoBean<AppGroup> group = AutoBeanCodex.decode(factory,
                                            AppGroup.class, result);
                                    selectedAppGroup.setName(group.as().getName());
                                    view.updateAppGroup(selectedAppGroup);
                                    view.unMaskWestPanel();
                                }

                                @Override
                                public void onFailure(Throwable caught) {
                                    ErrorHandler.post(I18N.ERROR.renameCategoryError(selectedAppGroup
                                            .getName()));
                                    view.unMaskWestPanel();
                                }
                            });
                }

            }
        });
        msgBox.show();

    }

    @Override
    public void onDeleteClicked() {

    }

    @Override
    public void deleteSelectedApps() {
        // Determine if the current selection is an AnalysisGroup
        if (getSelectedAppGroup() != null) {
            final AppGroup selectedAppGroup = getSelectedAppGroup();

            // Determine if the selected AnalysisGroup can be deleted.
            if (selectedAppGroup.getAppCount() > 0) {
                ErrorHandler.post(I18N.ERROR.deleteCategoryPermissionError());
                return;
            }

            ConfirmMessageBox msgBox = new ConfirmMessageBox(I18N.DISPLAY.warning(),
                    I18N.DISPLAY.confirmDeleteAppGroup(selectedAppGroup.getName()));
            msgBox.addHideHandler(new HideHandler() {

                @Override
                public void onHide(HideEvent event) {
                    Dialog btn = (Dialog)event.getSource();
                    String text = btn.getHideButton().getItemId();
                    if (text.equals(PredefinedButton.YES.name())) {
                        view.maskWestPanel(I18N.DISPLAY.loadingMask());
                        adminAppService.deleteAppGroup(selectedAppGroup.getId(),
                                new AsyncCallback<String>() {

                                    @Override
                                    public void onSuccess(String result) {
                                        // Refresh the catalog, so that the proper category counts
                                        // display.
                                        // FIXME JDS These events need to be common to ui-applications.
                                        EventBus.getInstance().fireEvent(
                                                new CatalogCategoryRefreshEvent());
                                        view.unMaskWestPanel();
                                    }

                                    @Override
                                    public void onFailure(Throwable caught) {
                                        ErrorHandler.post(I18N.ERROR
                                                .deleteAppGroupError(selectedAppGroup.getName()));
                                        view.unMaskWestPanel();
                                    }
                                });
                    }

                }
            });
            msgBox.show();

        } else if (getSelectedApp() != null) {
            final App selectedApp = getSelectedApp();
            ConfirmMessageBox msgBox = new ConfirmMessageBox(I18N.DISPLAY.warning(),
                    I18N.DISPLAY.confirmDeleteAppTitle());
            msgBox.addHideHandler(new HideHandler() {

                @Override
                public void onHide(HideEvent event) {
                    Dialog btn = (Dialog)event.getSource();
                    String text = btn.getHideButton().getItemId();
                    if (text.equals(PredefinedButton.YES.name())) {
                        view.maskCenterPanel(I18N.DISPLAY.loadingMask());
                        adminAppService.deleteApplication(selectedApp.getId(),
                                new AsyncCallback<String>() {

                                    @Override
                                    public void onSuccess(String result) {
                                        EventBus.getInstance().fireEvent(
                                                new CatalogCategoryRefreshEvent());
                                        view.removeApp(selectedApp);
                                        view.unMaskCenterPanel();
                                    }

                                    @Override
                                    public void onFailure(Throwable caught) {
                                        ErrorHandler.post(I18N.ERROR.deleteApplicationError(selectedApp
                                                .getName()));
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
    public void onRestoreAppClicked() {
        if (getSelectedApp() == null) {
            return;
        }
        final App selectedApp = getSelectedApp();

        adminAppService.restoreApplication(selectedApp.getId(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                JSONObject obj = JSONParser.parseStrict(result).isObject();
                JSONArray arr = obj.get("categories").isArray();
                if (arr != null && arr.size() > 0) {
                    StringBuilder names_display = new StringBuilder("");
                    for (int i = 0; i < arr.size(); i++) {
                        names_display.append(JsonUtil.trim(arr.get(0).isObject().get("name").toString()));
                        if (i != arr.size() - 1) {
                            names_display.append(",");
                        }
                    }

                    MessageBox msgBox = new MessageBox(I18N.DISPLAY.restoreAppSucessMsgTitle(),
                            I18N.DISPLAY.restoreAppSucessMsg(selectedApp.getName(),
                                    names_display.toString()));
                    msgBox.setIcon(MessageBox.ICONS.info());
                    msgBox.setPredefinedButtons(PredefinedButton.OK);
                    msgBox.show();
                }
                EventBus.getInstance().fireEvent(new CatalogCategoryRefreshEvent());
            }

            @Override
            public void onFailure(Throwable caught) {
                JSONObject obj = JSONParser.parseStrict(caught.getMessage()).isObject();
                String reason = JsonUtil.trim(obj.get("reason").toString());
                if (reason.contains("orphaned")) {
                    AlertMessageBox alertBox = new AlertMessageBox(I18N.DISPLAY
                            .restoreAppFailureMsgTitle(), I18N.DISPLAY.restoreAppFailureMsg(selectedApp
                            .getName()));
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
        view.maskCenterPanel(I18N.DISPLAY.loadingMask());

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
        final AppCategorizePresenter presenter = new AppCategorizePresenter(new AppCategorizeViewImpl(),
                selectedApp);
        presenter.setAppGroups(view.getAppGroupRoots());

        final IPlantDialog dlg = new IPlantDialog();
        dlg.addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                List<AppGroup> groups = presenter.getSelectedGroups();
                if (groups == null || groups.isEmpty()) {
                    IplantAnnouncer.getInstance().schedule(
                            new ErrorAnnouncementConfig(I18N.ERROR.noCategoriesSelected()));
                } else {
                    doCategorizeSelectedApp(selectedApp, groups);
                }
            }
        });

        dlg.setHeadingText(I18N.DISPLAY.selectCategories(selectedApp.getName()));
        dlg.setResizable(true);
        dlg.setOkButtonText(I18N.DISPLAY.submit());

        presenter.go(dlg);
        dlg.show();
    }

    private void doCategorizeSelectedApp(final App selectedApp, final List<AppGroup> groups) {
        view.maskCenterPanel(I18N.DISPLAY.loadingMask());
        AppCategorizeRequest request = buildAppCategorizeRequest(selectedApp, groups);

        adminAppService.categorizeApp(request, new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                view.unMaskCenterPanel();

                List<String> groupNames = Lists.newArrayList();
                for (AppGroup group : groups) {
                    groupNames.add(group.getName());
                }
                Collections.sort(groupNames, String.CASE_INSENSITIVE_ORDER);

                String successMsg = I18N.DISPLAY.appCategorizeSuccess(selectedApp.getName(), groupNames);
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(successMsg));

                EventBus.getInstance().fireEvent(new CatalogCategoryRefreshEvent());
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO Add error message for user
                ErrorHandler.post(caught);
                view.unMaskCenterPanel();
            }
        });
    }

    private AppCategorizeRequest buildAppCategorizeRequest(App selectedApp, List<AppGroup> groups) {
        HasId analysis = CommonModelUtils.createHasIdFromString(selectedApp.getId());
        List<CategoryRequest> categories = Lists.newArrayList();
        for (AppGroup group : groups) {

            List<String> path = Lists.newArrayList();
            while (group != null) {
                path.add(group.getName());
                group = view.getParent(group);
            }

            Collections.reverse(path);

            CategoryPath groupPath = serviceFactory.categoryPath().as();
            groupPath.setUsername("<public>"); //$NON-NLS-1$
            groupPath.setPath(path);

            CategoryRequest categoryRequest = serviceFactory.categoryRequest().as();
            categoryRequest.setAnalysis(analysis);
            categoryRequest.setCategoryPath(groupPath);

            categories.add(categoryRequest);
        }

        AppCategorizeRequest request = serviceFactory.appCategorizeRequest().as();
        request.setCategories(categories);

        return request;
    }

    @Override
    public void onAppEditorSave(App app) {
        final AsyncCallback<String> editCompleteCallback = new AppEditCompleteCallback(app);

        // Serialize App to JSON object
        String jsonString = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(app)).getPayload();
        final JSONObject jsonObj = JsonUtil.getObject(jsonString);

        if (app.getName() != null) {
            ConfluenceServiceFacade.getInstance().movePage(app.getName(), app.getName(),
                    new AsyncCallback<String>() {

                        @Override
                        public void onSuccess(String result) {
                            adminAppService.updateApplication(jsonObj, editCompleteCallback);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            ErrorHandler.post(caught.getMessage());
                            adminAppService.updateApplication(jsonObj, editCompleteCallback);
                        }
                    });
            // new ConfluenceServiceMovePageCallback(tmpCallback, jsonObj));
        } else {
            adminAppService.updateApplication(jsonObj, editCompleteCallback);
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
            ErrorHandler.post(I18N.ERROR.updateApplicationError());
        }
    }

    @Override
    public void moveAppGroup(final AppGroup parentGroup, final AppGroup childGroup) {
        adminAppService.moveCategory(childGroup.getId(), parentGroup.getId(),
                new AsyncCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        // Refresh the catalog, so that the proper category counts
                        // display.
                        // FIXME JDS These events need to be common to ui-applications.
                        EventBus.getInstance().fireEvent(new CatalogCategoryRefreshEvent());
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        ErrorHandler.post(I18N.ERROR.moveCategoryError(childGroup.getName()));
                    }
                });
    }

    @Override
    public void moveApp(final AppGroup parentGroup, final App app) {
        adminAppService.moveApplication(app.getId(), parentGroup.getId(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                // Refresh the catalog, so that the proper category counts
                // display.
                // FIXME JDS These events need to be common to ui-applications.
                EventBus.getInstance().fireEvent(new CatalogCategoryRefreshEvent());
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.moveApplicationError(app.getName()));
            }
        });
    }

    @Override
    public boolean canMoveAppGroup(AppGroup parentGroup, AppGroup childGroup) {
        if (parentGroup == null || childGroup == null) {
            return false;
        }

        // Don't allow a category drop onto itself.
        if (childGroup == parentGroup) {
            return false;
        }

        // Don't allow a category drop into a category leaf with apps in it.
        if (isLeaf(parentGroup) && parentGroup.getAppCount() > 0) {
            return false;
        }

        // Don't allow a category drop into one of its children.
        if (childGroup.getGroups().contains(parentGroup)) {
            return false;
        }

        // Don't allow a category drop into its own parent.
        if (parentGroup.getGroups().contains(childGroup)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canMoveApp(AppGroup parentGroup, App app) {
        if (parentGroup == null || app == null) {
            return false;
        }

        // Apps can only be dropped into leaf categories.
        if (!isLeaf(parentGroup)) {
            return false;
        }

        // FIXME this check will always pass, since app.getGroupId() is always null, currently.
        // if (parentGroup.getId().equals(app.getGroupId())) {
        // return false;
        // }

        return true;
    }

    private boolean isLeaf(AppGroup parentGroup) {
        return parentGroup.getGroups() == null || parentGroup.getGroups().isEmpty();
    }
}
