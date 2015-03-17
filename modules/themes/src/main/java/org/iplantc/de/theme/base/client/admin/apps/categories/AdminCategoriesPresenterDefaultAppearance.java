package org.iplantc.de.theme.base.client.admin.apps.categories;

import org.iplantc.de.admin.apps.client.AdminCategoriesView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.admin.BelphegorDisplayStrings;
import org.iplantc.de.theme.base.client.admin.BelphegorErrorStrings;

import com.google.gwt.core.client.GWT;

import java.util.List;

/**
 * @author jstroot
 */
public class AdminCategoriesPresenterDefaultAppearance implements AdminCategoriesView.Presenter.Appearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final BelphegorErrorStrings errorStrings;
    private final BelphegorDisplayStrings displayStrings;

    public AdminCategoriesPresenterDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<BelphegorErrorStrings> create(BelphegorErrorStrings.class),
             GWT.<BelphegorDisplayStrings> create(BelphegorDisplayStrings.class));
    }

    public AdminCategoriesPresenterDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                                     final BelphegorErrorStrings errorStrings,
                                                     final BelphegorDisplayStrings displayStrings) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.errorStrings = errorStrings;
        this.displayStrings = displayStrings;
    }

    @Override
    public String add() {
        return iplantDisplayStrings.add();
    }

    @Override
    public String addAppCategoryError(String name) {
        return errorStrings.addAppCategoryError(name);
    }

    @Override
    public String addCategoryLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String addCategoryPermissionError() {
        return errorStrings.addCategoryPermissionError();
    }

    @Override
    public String addCategoryPrompt() {
        return displayStrings.addCategoryPrompt();
    }

    @Override
    public String appCategorizeSuccess(String name, List<String> groupNames) {
        return displayStrings.appCategorizeSuccess(name, groupNames);
    }

    @Override
    public String categorizeAppLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String confirmDeleteAppCategory(String name) {
        return displayStrings.confirmDeleteAppCategory(name);
    }

    @Override
    public String confirmDeleteAppCategoryWarning() {
        return iplantDisplayStrings.warning();
    }

    @Override
    public String deleteAppCategoryError(String name) {
        return errorStrings.deleteAppCategoryError(name);
    }

    @Override
    public String deleteAppCategoryLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String deleteCategoryPermissionError() {
        return errorStrings.deleteCategoryPermissionError();
    }

    @Override
    public String getAppCategoriesLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String getAppDetailsLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String invalidMoveMsg() {
        return errorStrings.invalidMoveCategoryMsg();
    }

    @Override
    public String moveCategory() {
        return displayStrings.moveCategory();
    }

    @Override
    public String moveCategoryError(String name) {
        return errorStrings.moveCategoryError(name);
    }

    @Override
    public String noCategoriesSelected() {
        return errorStrings.noCategoriesSelected();
    }

    @Override
    public String renameAppCategoryLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String renameCategory() {
        return iplantDisplayStrings.rename();
    }

    @Override
    public String renameCategoryError(String name) {
        return errorStrings.renameCategoryError(name);
    }

    @Override
    public String renamePrompt() {
        return displayStrings.renamePrompt();
    }

    @Override
    public String selectCategories(String name) {
        return displayStrings.selectCategories(name);
    }

    @Override
    public String submit() {
        return iplantDisplayStrings.submit();
    }
}
