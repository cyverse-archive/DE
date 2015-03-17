package org.iplantc.de.theme.base.client.admin.apps;

import org.iplantc.de.admin.apps.client.AdminAppsToolbarView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.admin.BelphegorDisplayStrings;
import org.iplantc.de.theme.base.client.apps.toolbar.AppsToolbarViewDefaultAppearance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author jstroot
 */
public class AdminAppsToolbarDefaultAppearance extends AppsToolbarViewDefaultAppearance implements AdminAppsToolbarView.ToolbarAppearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final IplantResources iplantResources;
    private final BelphegorDisplayStrings displayStrings;

    public AdminAppsToolbarDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantResources> create(IplantResources.class),
             GWT.<BelphegorDisplayStrings> create(BelphegorDisplayStrings.class));
    }

    AdminAppsToolbarDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                      final IplantResources iplantResources,
                                      final BelphegorDisplayStrings displayStrings) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantResources = iplantResources;
        this.displayStrings = displayStrings;
    }

    @Override
    public String addCategoryPrompt() {
        return displayStrings.addCategoryPrompt();
    }

    @Override
    public String add() {
        return iplantDisplayStrings.add();
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
    public String confirmDeleteAppTitle() {
        return displayStrings.confirmDeleteAppTitle();
    }

    @Override
    public String confirmDeleteAppWarning() {
        return iplantDisplayStrings.warning();
    }

    @Override
    public String renameCategory() {
        return iplantDisplayStrings.rename();
    }

    @Override
    public ImageResource renameCategoryIcon() {
        return iplantResources.fileRename();
    }

    @Override
    public String renamePrompt() {
        return displayStrings.renamePrompt();
    }

    @Override
    public String restoreApp() {
        return displayStrings.restoreApp();
    }

    @Override
    public ImageResource restoreAppIcon() {
        return iplantResources.submitForPublic();
    }

    @Override
    public String deleteCategory() {
        return displayStrings.deleteCategory();
    }

    @Override
    public String categorizeApp() {
        return displayStrings.categorize();
    }

    @Override
    public ImageResource categoryAppIcon() {
        return iplantResources.category();
    }

    @Override
    public ImageResource deleteAppIcon() {
        return iplantResources.delete();
    }

    @Override
    public String deleteApp() {
        return displayStrings.deleteApp();
    }

    @Override
    public String moveCategory() {
        return displayStrings.moveCategory();
    }

    @Override
    public ImageResource moveCategoryIcon() {
        return iplantResources.fileRename();
    }
}
