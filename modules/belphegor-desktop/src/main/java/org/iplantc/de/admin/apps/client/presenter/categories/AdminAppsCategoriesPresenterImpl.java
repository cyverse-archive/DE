package org.iplantc.de.admin.apps.client.presenter.categories;

import org.iplantc.de.admin.apps.client.AdminCategoriesView;
import org.iplantc.de.admin.apps.client.AppCategorizeView;
import org.iplantc.de.admin.apps.client.events.selection.AddCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.CategorizeAppSelected;
import org.iplantc.de.admin.apps.client.events.selection.DeleteCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.MoveCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.RenameCategorySelected;
import org.iplantc.de.admin.desktop.client.apps.presenter.AppCategorizePresenter;
import org.iplantc.de.admin.desktop.client.apps.views.AppCategorizeViewImpl;
import org.iplantc.de.admin.desktop.client.models.BelphegorAdminProperties;
import org.iplantc.de.admin.desktop.client.services.AppAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.services.callbacks.AdminServiceCallback;
import org.iplantc.de.admin.desktop.client.services.model.AppAdminServiceRequestAutoBeanFactory;
import org.iplantc.de.admin.desktop.client.services.model.AppCategorizeRequest;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.commons.client.views.dialogs.IPlantPromptDialog;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.Collections;
import java.util.List;

/**
 * @author jstroot
 */
public class AdminAppsCategoriesPresenterImpl implements AdminCategoriesView.Presenter {

    @Inject AppAdminServiceFacade adminAppService;
    @Inject IplantAnnouncer announcer;
    @Inject AdminCategoriesView.Presenter.Appearance appearance;
    @Inject AppAutoBeanFactory factory;
    @Inject BelphegorAdminProperties properties;
    @Inject AppAdminServiceRequestAutoBeanFactory serviceFactory;
    private final TreeStore<AppCategory> treeStore;
    private AdminCategoriesView view;

    @Inject
    AdminAppsCategoriesPresenterImpl(final TreeStore<AppCategory> treeStore) {


        this.treeStore = treeStore;
    }

    public boolean canMoveApp(AppCategory parentCategory, App app) {
        if (parentCategory == null || app == null) {
            return false;
        }

        // Apps can only be dropped into leaf categories.
        return isLeaf(parentCategory);

    }

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
    public AdminCategoriesView getView() {
        return view;
    }

    public void moveAppCategory(final AppCategory parentCategory, final AppCategory childCategory) {
        adminAppService.moveCategory(childCategory.getId(),
                                     parentCategory.getId(),
                                     new AsyncCallback<String>() {

                                         @Override
                                         public void onFailure(Throwable caught) {
                                             ErrorHandler.post(appearance.moveCategoryError(childCategory.getName()));
                                         }

                                         @Override
                                         public void onSuccess(String result) {
                                             // Refresh the catalog, so that the proper category counts
                                             // display.
                                             // FIXME JDS These events need to be common to
                                             // ui-apps.
//                                             eventBus.fireEvent(new CatalogCategoryRefreshEvent());
                                         }
                                     });
    }

    @Override
    public void onAddCategorySelected(final AddCategorySelected event) {
        final AppCategory selectedAppCategory = event.getAppCategories().iterator().next();

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

        // FIXME Dlg should be moved to view
        final IPlantPromptDialog dlg = new IPlantPromptDialog(appearance.add(), 0, "", null);
        dlg.setHeadingText(appearance.addCategoryPrompt());
        dlg.addOkButtonSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {

                final String name = dlg.getFieldText();

                // FIXME This loading mask should happen as part of RPC proxy
//                view.maskCenterPanel(appearance.addCategoryLoadingMask());
                adminAppService.addCategory(name,
                                            selectedAppCategory.getId(),
                                            new AdminServiceCallback() {
                                                @Override
                                                protected String getErrorMessage() {
//                                                    view.unMaskCenterPanel();
                                                    return appearance.addAppCategoryError(name);
                                                }

                                                @Override
                                                protected void onSuccess(JSONObject jsonResult) {

                                                    // Get result
                                                    AutoBean<AppCategory> group = AutoBeanCodex.decode(factory,
                                                                                                       AppCategory.class,
                                                                                                       jsonResult.toString());

                                                    AppCategory child = group.as();
                                                    if (selectedAppCategory == null) {
                                                        treeStore.add(child);
                                                    } else {
                                                        treeStore.add(selectedAppCategory, child);
                                                    }
//                                                    view.unMaskCenterPanel();
                                                }
                                            });

            }
        });
        dlg.show();
    }

    @Override
    public void onCategorizeAppSelected(CategorizeAppSelected event) {
        Preconditions.checkArgument(event.getApps().size() == 1);
        final App selectedApp = event.getApps().iterator().next();
        view.mask(appearance.getAppDetailsLoadingMask());

        // FIXME Why do we have to call this?
        adminAppService.getAppDetails(selectedApp.getId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                view.unmask();
            }

            @Override
            public void onSuccess(String result) {
                App appDetails = AutoBeanCodex.decode(factory, App.class, result).as();
                showCategorizeAppDialog(appDetails);
                view.unmask();
            }
        });
    }

    @Override
    public void onDeleteCategorySelected(DeleteCategorySelected event) {
        final AppCategory selectedAppCategory = event.getAppCategories().iterator().next();

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
                if (Dialog.PredefinedButton.YES.equals(event.getHideButton())) {
//                    view.maskWestPanel(appearance.deleteAppCategoryLoadingMask());
                    adminAppService.deleteAppCategory(selectedAppCategory.getId(),
                                                      new AsyncCallback<String>() {

                                                          @Override
                                                          public void onFailure(Throwable caught) {
                                                              ErrorHandler.post(appearance.deleteAppCategoryError(selectedAppCategory.getName()));
//                                                              view.unMaskWestPanel();
                                                          }

                                                          @Override
                                                          public void onSuccess(String result) {
                                                              // Refresh the catalog, so that the
                                                              // proper category counts
                                                              // display.
                                                              // FIXME All cat counts need to be updated. Refetch all categories
                                                              treeStore.remove(selectedAppCategory);
//                                                              eventBus.fireEvent(new CatalogCategoryRefreshEvent());
//                                                              view.unMaskWestPanel();
                                                          }
                                                      });
                }
            }
        });
        msgBox.show();
    }

    @Override
    public void onMoveCategorySelected(MoveCategorySelected event) {
        final AppCategory appCategory = event.getAppCategory();
        final IPlantDialog dlg = new IPlantDialog();
        final AppCategorizeView cat_view = new AppCategorizeViewImpl(true);
        cat_view.setAppCategories(treeStore.getRootItems());
        dlg.addOkButtonSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                if (appCategory != null && null != cat_view.getSelectedCategories()
                        && cat_view.getSelectedCategories().size() > 0) {
                    AppCategory destinationCategory = cat_view.getSelectedCategories().get(0);
                    if (canMoveAppCategory(destinationCategory, appCategory)) {
                        moveAppCategory(destinationCategory, appCategory);
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

    @Override
    public void onRemove(StoreRemoveEvent<App> event) {
        // FIXME Item has been removed from list store. Need to update counts, etc.
    }

    @Override
    public void onRenameCategorySelected(RenameCategorySelected event) {
        final AppCategory selectedAppCategory = event.getAppCategory();

        // FIXME Dialog should be moved to view
        PromptMessageBox msgBox = new PromptMessageBox(appearance.renameCategory(),
                                                       appearance.renamePrompt());
        final TextField field = ((TextField) msgBox.getField());
        field.setAutoValidate(true);
        field.setAllowBlank(false);
        field.setText(selectedAppCategory.getName());
        msgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (Dialog.PredefinedButton.OK.equals(event.getHideButton())) {
//                    view.maskWestPanel(appearance.renameAppCategoryLoadingMask());
                    adminAppService.renameAppCategory(selectedAppCategory.getId(),
                                                      field.getText(),
                                                      new AsyncCallback<String>() {

                                                          @Override
                                                          public void onFailure(Throwable caught) {
                                                              ErrorHandler.post(appearance.renameCategoryError(selectedAppCategory.getName()));
//                                                              view.unMaskWestPanel();
                                                          }

                                                          @Override
                                                          public void onSuccess(String result) {
                                                              AutoBean<AppCategory> group = AutoBeanCodex.decode(factory,
                                                                                                                 AppCategory.class,
                                                                                                                 result);
                                                              selectedAppCategory.setName(group.as()
                                                                                               .getName());
                                                              treeStore.update(selectedAppCategory);

//                                                              view.unMaskWestPanel();
                                                          }
                                                      });
                }

            }
        });
        msgBox.show();
    }

    private AppCategorizeRequest buildAppCategorizeRequest(App selectedApp,
                                                           List<AppCategory> appCategories) {
        HasId appId = CommonModelUtils.getInstance().createHasIdFromString(selectedApp.getId());
        List<AppCategorizeRequest.CategoryRequest> categories = Lists.newArrayList();
        List<String> cat_ids = Lists.newArrayList();
        for (AppCategory group : appCategories) {
            cat_ids.add(group.getId());
        }

        AppCategorizeRequest.CategoryRequest categoryRequest = serviceFactory.categoryRequest().as();
        categoryRequest.setAppId(appId.getId());
        categoryRequest.setCategories(cat_ids);

        categories.add(categoryRequest);

        AppCategorizeRequest request = serviceFactory.appCategorizeRequest().as();
        request.setCategories(categories);

        return request;
    }

    private void doCategorizeSelectedApp(final App selectedApp,
                                         final List<AppCategory> groupAppCategories) {
//        view.maskCenterPanel(appearance.categorizeAppLoadingMask());
        AppCategorizeRequest request = buildAppCategorizeRequest(selectedApp, groupAppCategories);

        adminAppService.categorizeApp(request, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                // TODO Add error message for user
                ErrorHandler.post(caught);
//                view.unMaskCenterPanel();
            }

            @Override
            public void onSuccess(String result) {
//                view.unMaskCenterPanel();

                List<String> groupNames = Lists.newArrayList();
                for (AppCategory group : groupAppCategories) {
                    groupNames.add(group.getName());
                }
                Collections.sort(groupNames, String.CASE_INSENSITIVE_ORDER);

                String successMsg = appearance.appCategorizeSuccess(selectedApp.getName(), groupNames);
                announcer.schedule(new SuccessAnnouncementConfig(successMsg));

//                eventBus.fireEvent(new CatalogCategoryRefreshEvent());
            }
        });
    }

    private boolean isLeaf(AppCategory parentCategory) {
        return parentCategory.getCategories() == null || parentCategory.getCategories().isEmpty();
    }

    private void showCategorizeAppDialog(final App selectedApp) {
        // FIXME Turn into a separate dialog
        final AppCategorizePresenter presenter = new AppCategorizePresenter(new AppCategorizeViewImpl(false),
                                                                            selectedApp,
                                                                            properties);
        presenter.setAppCategories(treeStore.getRootItems());


        final IPlantDialog dlg = new IPlantDialog();
        dlg.addOkButtonSelectHandler(new SelectEvent.SelectHandler() {

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


}
