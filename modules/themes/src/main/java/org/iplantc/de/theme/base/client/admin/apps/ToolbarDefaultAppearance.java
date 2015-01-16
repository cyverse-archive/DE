package org.iplantc.de.theme.base.client.admin.apps;

import org.iplantc.de.admin.desktop.client.apps.views.AdminAppsView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.admin.BelphegorDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author jstroot
 */
public class ToolbarDefaultAppearance implements AdminAppsView.Toolbar.ToolbarAppearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final IplantResources iplantResources;
    private final BelphegorDisplayStrings displayStrings;

    public ToolbarDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantResources> create(IplantResources.class),
             GWT.<BelphegorDisplayStrings> create(BelphegorDisplayStrings.class));
    }

    ToolbarDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                             final IplantResources iplantResources,
                             final BelphegorDisplayStrings displayStrings) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantResources = iplantResources;
        this.displayStrings = displayStrings;
    }

    @Override
    public ImageResource addIcon() {
        return iplantResources.add();
    }

    @Override
    public String add() {
        return iplantDisplayStrings.add();
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
    public ImageResource deleteIcon() {
        return iplantResources.delete();
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
