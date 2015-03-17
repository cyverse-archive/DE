package org.iplantc.de.theme.base.client.admin.apps;

import org.iplantc.de.admin.apps.client.AdminAppsView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.admin.BelphegorDisplayStrings;
import org.iplantc.de.theme.base.client.admin.BelphegorErrorStrings;

import com.google.gwt.core.client.GWT;

import java.util.List;

/**
 * @author jstroot
 */
public class AdminPresenterDefaultAppearance implements AdminAppsView.AdminPresenter.AdminPresenterAppearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final BelphegorDisplayStrings displayStrings;
    private final BelphegorErrorStrings errorStrings;

    public AdminPresenterDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<BelphegorDisplayStrings> create(BelphegorDisplayStrings.class),
             GWT.<BelphegorErrorStrings> create(BelphegorErrorStrings.class));
    }
    public AdminPresenterDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                           final BelphegorDisplayStrings displayStrings,
                                           final BelphegorErrorStrings errorStrings) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;
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
    public String confirmDeleteAppCategory(String name) {
        return displayStrings.confirmDeleteAppCategory(name);
    }

    @Override
    public String confirmDeleteAppTitle() {
        return displayStrings.confirmDeleteAppTitle();
    }

    @Override
    public String deleteAppCategoryError(String name) {
        return errorStrings.deleteAppCategoryError(name);
    }

    @Override
    public String deleteApplicationError(String name) {
        return errorStrings.deleteApplicationError(name);
    }

    @Override
    public String deleteCategoryPermissionError() {
        return errorStrings.deleteCategoryPermissionError();
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
    public String renameCategoryError(String name) {
        return errorStrings.renameCategoryError(name);
    }

    @Override
    public String renamePrompt() {
        return displayStrings.renamePrompt();
    }

    @Override
    public String restoreAppFailureMsg(String name) {
        return errorStrings.restoreAppFailureMsg(name);
    }

    @Override
    public String restoreAppFailureMsgTitle() {
        return errorStrings.restoreAppFailureMsgTitle();
    }

    @Override
    public String restoreAppSuccessMsg(String name, String s) {
        return displayStrings.restoreAppSuccessMsg(name, s);
    }

    @Override
    public String restoreAppSuccessMsgTitle() {
        return displayStrings.restoreAppSuccessMsgTitle();
    }

    @Override
    public String selectCategories(String name) {
        return displayStrings.selectCategories(name);
    }

    @Override
    public String submit() {
        return iplantDisplayStrings.submit();
    }

    @Override
    public String updateApplicationError() {
        return errorStrings.updateApplicationError();
    }
}
