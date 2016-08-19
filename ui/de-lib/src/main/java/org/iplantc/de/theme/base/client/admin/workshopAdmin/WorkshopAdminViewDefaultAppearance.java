package org.iplantc.de.theme.base.client.admin.workshopAdmin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import org.iplantc.de.admin.desktop.client.workshopAdmin.WorkshopAdminView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

/**
 * @author dennis
 */
public class WorkshopAdminViewDefaultAppearance implements WorkshopAdminView.WorkshopAdminViewAppearance {

    private final WorkshopAdminDisplayStrings displayStrings;
    private final IplantResources iplantResources;
    private final IplantDisplayStrings iplantDisplayStrings;

    public WorkshopAdminViewDefaultAppearance() {
        this.displayStrings = GWT.create(WorkshopAdminDisplayStrings.class);
        this.iplantResources = GWT.create(IplantResources.class);
        this.iplantDisplayStrings = GWT.create(IplantDisplayStrings.class);
    }

    @Override
    public String delete() {
        return iplantDisplayStrings.delete();
    }

    @Override
    public ImageResource deleteIcon() {
        return iplantResources.delete();
    }

    @Override
    public String save() {
        return iplantDisplayStrings.save();
    }

    @Override
    public ImageResource saveIcon() {
        return iplantResources.save();
    }

    @Override
    public int nameColumnWidth() {
        return 90;
    }

    @Override
    public String nameColumnLabel() {
        return iplantDisplayStrings.name();
    }

    @Override
    public int emailColumnWidth() {
        return 150;
    }

    @Override
    public String emailColumnLabel() {
        return iplantDisplayStrings.email();
    }

    @Override
    public int institutionColumnWidth() {
        return 90;
    }

    @Override
    public String institutionColumnLabel() {
        return iplantDisplayStrings.institution();
    }

    @Override
    public String partialGroupSaveMsg() {
        return displayStrings.partialGroupSaveMsg();
    }

    @Override
    public String loadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String refresh() {
        return iplantDisplayStrings.refresh();
    }

    @Override
    public ImageResource refreshIcon() {
        return iplantResources.refresh();
    }
}
