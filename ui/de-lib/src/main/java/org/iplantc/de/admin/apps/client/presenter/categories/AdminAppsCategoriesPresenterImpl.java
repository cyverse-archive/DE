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
import org.iplantc.de.admin.desktop.client.services.AppAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.services.model.AppAdminServiceRequestAutoBeanFactory;
import org.iplantc.de.admin.desktop.client.services.model.AppCategorizeRequest;
import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.gin.factory.AppCategoriesViewFactory;
import org.iplantc.de.shared.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author jstroot
 */
public class AdminAppsCategoriesPresenterImpl implements AdminCategoriesView.Presenter,
                                                         AppCategoriesView.AppCategoryHierarchyProvider {

    protected static String PATH_KEY = "category_path";
    @Inject AppAdminServiceFacade adminAppService;
    @Inject IplantAnnouncer announcer;
    @Inject AppServiceFacade appService;
    @Inject AdminCategoriesView.Presenter.Appearance appearance;
    @Inject DEProperties properties;
    @Inject AppAdminServiceRequestAutoBeanFactory serviceFactory;

    private final TreeStore<AppCategory> treeStore;
    private final AppCategoriesView view;

    @Inject
    AdminAppsCategoriesPresenterImpl(final AppCategoriesViewFactory viewFactory,
                                     final TreeStore<AppCategory> treeStore) {
        this.treeStore = treeStore;
        this.view = viewFactory.create(treeStore, this);
    }

    @Override
    public List<String> getGroupHierarchy(AppCategory appCategory) {
        List<String> groupNames;

        final AutoBean<AppCategory> categoryAutoBean = AutoBeanUtils.getAutoBean(appCategory);
        String path = categoryAutoBean.getTag(PATH_KEY);
        groupNames = Arrays.asList(path.split("/"));

        return groupNames;
    }

    @Override
    public AppCategoriesView getView() {
        return view;
    }

    @Override
    public void go(final HasId selectedAppCategory) {
        if (!treeStore.getAll().isEmpty()
                && selectedAppCategory != null) {
            AppCategory desiredCategory = treeStore.findModelWithKey(selectedAppCategory.getId());
            view.getTree().getSelectionModel().select(desiredCategory, false);
        } else {
            clearAndRetrieveAppCategories(selectedAppCategory);
       }
    }


    @Override
    public void onAddCategorySelected(final AddCategorySelected event) {
        Preconditions.checkArgument(event.getAppCategories().size() == 1); // Ensure only one selected
        final AppCategory selectedParentCategory = event.getAppCategories().iterator().next();

        // Check if a new AppCategory can be created in the target AppCategory.
        /*
         * Verify Selected parent category is not "public apps"
         *
         */
        final boolean isTrashCategory = properties.getDefaultTrashAppCategoryId().equalsIgnoreCase(selectedParentCategory.getId());
        final boolean isBetaCategory = properties.getDefaultBetaCategoryId().equalsIgnoreCase(selectedParentCategory.getId());
        final boolean isPublicApps = selectedParentCategory.getName().contains("Public Apps");
        final boolean hasNoChildCategories = selectedParentCategory.getCategories() != null
                              && selectedParentCategory.getCategories().size() == 0;
        if(isTrashCategory
            || isBetaCategory) {
            announcer.schedule(new ErrorAnnouncementConfig(appearance.addCategoryPermissionError()));
            return;
        } else if (!isPublicApps
                      && hasNoChildCategories
                      && selectedParentCategory.getAppCount() > 0){
            announcer.schedule(new ErrorAnnouncementConfig(appearance.addCategoryPermissionError()));
            return;
        }

        final String newCategoryName = event.getNewCategoryName();
        view.mask(appearance.addCategoryLoadingMask());
        adminAppService.addCategory(newCategoryName,
                                    selectedParentCategory,
                                    new AsyncCallback<AppCategory>() {

                                        @Override
                                        public void onFailure(Throwable caught) {
                                            view.unmask();
                                            announcer.schedule(new ErrorAnnouncementConfig(appearance.addAppCategoryError(newCategoryName)));
                                        }

                                        @Override
                                        public void onSuccess(AppCategory result) {
                                            view.unmask();
                                            treeStore.add(selectedParentCategory, result);
                                        }
                                    });
    }

    @Override
    public void onAppSearchResultLoad(AppSearchResultLoadEvent event) {
        view.getTree().getSelectionModel().deselectAll();
    }

    @Override
    public void onCategorizeAppSelected(final CategorizeAppSelected event) {
        Preconditions.checkArgument(event.getApps().size() == 1);
        final App selectedApp = event.getApps().iterator().next();
        view.mask(appearance.getAppDetailsLoadingMask());

        adminAppService.getAppDetails(selectedApp, new AsyncCallback<App>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                view.unmask();
            }

            @Override
            public void onSuccess(App result) {
                showCategorizeAppDialog(result);
                view.unmask();
            }
        });
    }

    @Override
    public void onDeleteCategorySelected(final DeleteCategorySelected event) {
        final AppCategory selectedAppCategory = event.getAppCategories().iterator().next();

        // Determine if the selected AppCategory can be deleted.
        if (selectedAppCategory.getAppCount() > 0) {
            announcer.schedule(new ErrorAnnouncementConfig(appearance.deleteCategoryPermissionError()));
            return;
        }

        view.mask(appearance.deleteAppCategoryLoadingMask());
        adminAppService.deleteAppCategory(selectedAppCategory,
                                          new AsyncCallback<Void>() {

                                              @Override
                                              public void onFailure(Throwable caught) {
                                                  view.unmask();
                                                  announcer.schedule(new ErrorAnnouncementConfig(appearance.deleteAppCategoryError(selectedAppCategory.getName())));
                                              }

                                              @Override
                                              public void onSuccess(Void result) {
                                                  view.unmask();
                                                  // Refresh the catalog, so that the
                                                  // proper category counts
                                                  // display.
                                                  // FIXME All cat counts need to be updated. Re-fetch all categories
                                                  treeStore.remove(selectedAppCategory);
                                                  // eventBus.fireEvent(new CatalogCategoryRefreshEvent());
                                              }
                                          });
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
                if (appCategory != null
                        && null != cat_view.getSelectedCategories()
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
    public void onRenameCategorySelected(final RenameCategorySelected event) {
        final AppCategory selectedAppCategory = event.getAppCategory();

        view.mask(appearance.renameAppCategoryLoadingMask());
        adminAppService.renameAppCategory(selectedAppCategory,
                                          event.getNewCategoryName(),
                      new AsyncCallback<AppCategory>() {

                          @Override
                          public void onFailure(Throwable caught) {
                              view.unmask();
                              announcer.schedule(new ErrorAnnouncementConfig(appearance.renameCategoryError(selectedAppCategory.getName())));
                          }

                          @Override
                          public void onSuccess(AppCategory result) {
                              view.unmask();
                              selectedAppCategory.setName(result.getName());
                              treeStore.update(selectedAppCategory);
                          }
                      });
    }

    void addAppCategories(AppCategory parent, List<AppCategory> children) {
        if ((children == null)
                || children.isEmpty()) {
            return;
        }
        if (parent == null) {
            treeStore.add(children);
        } else {
            treeStore.add(parent, children);
        }

        setCategoryPathTag(parent, children);

        for (AppCategory ag : children) {
            addAppCategories(ag, ag.getCategories());
        }
    }

    void setCategoryPathTag(AppCategory parent, List<AppCategory> children) {
        String parentPath = "";
        if (parent != null) {
            final AutoBean<AppCategory> parentAutoBean = AutoBeanUtils.getAutoBean(parent);
            parentPath = parentAutoBean.getTag(PATH_KEY);
            if (children != null) {
                parentPath += "/";
            }
        }
        if (children != null) {
            for (AppCategory child : children) {
                final AutoBean<AppCategory> childAutoBean = AutoBeanUtils.getAutoBean(child);
                childAutoBean.setTag(PATH_KEY, parentPath + child.getName());
            }
        }
    }

    boolean canMoveApp(AppCategory parentCategory, App app) {
        if (parentCategory == null || app == null) {
            return false;
        }

        // Apps can only be dropped into leaf categories.
        return isLeaf(parentCategory);

    }

    boolean canMoveAppCategory(AppCategory parentCategory, AppCategory childCategory) {
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

    List<AppCategory> getGroupHierarchy(AppCategory grp, List<AppCategory> groups) {
        if (groups == null) {
            groups = Lists.newArrayList();
        }
        groups.add(grp);
        for (AppCategory ap : treeStore.getRootItems()) {
            if (ap.getId().equals(grp.getId())) {
                return groups;
            }
        }
        return getGroupHierarchy(treeStore.getParent(grp), groups);
    }

    void moveAppCategory(final AppCategory parentCategory, final AppCategory childCategory) {
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
                                             clearAndRetrieveAppCategories(childCategory);
                                         }
                                     });
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
        AppCategorizeRequest request = buildAppCategorizeRequest(selectedApp, groupAppCategories);

        view.mask(appearance.categorizeAppLoadingMask());
        adminAppService.categorizeApp(request, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                view.unmask();
                // TODO Add error message for user
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(String result) {
                view.unmask();

                List<String> groupNames = Lists.newArrayList();
                for (AppCategory group : groupAppCategories) {
                    groupNames.add(group.getName());
                }
                Collections.sort(groupNames, String.CASE_INSENSITIVE_ORDER);

                String successMsg = appearance.appCategorizeSuccess(selectedApp.getName(), groupNames);
                announcer.schedule(new SuccessAnnouncementConfig(successMsg));

                // eventBus.fireEvent(new CatalogCategoryRefreshEvent());
            }
        });
    }

    private boolean isLeaf(AppCategory parentCategory) {
        return parentCategory.getCategories() == null || parentCategory.getCategories().isEmpty();
    }

    void clearAndRetrieveAppCategories(final HasId selectedAppCategory) {
        view.mask(appearance.getAppCategoriesLoadingMask());
        treeStore.clear();
        adminAppService.getPublicAppCategories(new AsyncCallback<List<AppCategory>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                view.unmask();
            }

            @Override
            public void onSuccess(List<AppCategory> result) {
                addAppCategories(null, result);
                view.getTree().expandAll();
                if (selectedAppCategory != null) {
                    AppCategory desiredCategory = treeStore.findModelWithKey(selectedAppCategory.getId());
                    view.getTree().getSelectionModel().select(desiredCategory, false);
                    view.getTree().scrollIntoView(desiredCategory);
                } else {
                    view.getTree().getSelectionModel().selectNext();
                    final AppCategory firstCategory = treeStore.getRootItems().get(0);
                    view.getTree().getSelectionModel().select(firstCategory, false);
                    view.getTree().scrollIntoView(firstCategory);
                }
                view.unmask();
            }
        }, false);
    }

    void showCategorizeAppDialog(final App selectedApp) {
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
