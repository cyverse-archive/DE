package org.iplantc.de.theme.base.client.admin.workshopAdmin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import org.iplantc.de.admin.desktop.client.workshopAdmin.WorkshopAdminView;
import org.iplantc.de.resources.client.IplantResources;

/**
 * @author dennis
 */
public class WorkshopAdminViewDefaultAppearance implements WorkshopAdminView.WorkshopAdminViewAppearance {

    private final WorkshopAdminDisplayStrings displayStrings;
    private final IplantResources iplantResources;

    public WorkshopAdminViewDefaultAppearance() {
        this.displayStrings = GWT.create(WorkshopAdminDisplayStrings.class);
        this.iplantResources = GWT.create(IplantResources.class);
    }

    @Override
    public String add() {
        return displayStrings.add();
    }

    @Override
    public ImageResource addIcon() {
        return iplantResources.add();
    }

    @Override
    public String delete() {
        return displayStrings.delete();
    }

    @Override
    public ImageResource deleteIcon() {
        return iplantResources.delete();
    }

    @Override
    public int nameColumnWidth() {
        return 90;
    }

    @Override
    public String nameColumnLabel() {
        return displayStrings.nameColumnLabel();
    }

    @Override
    public int emailColumnWidth() {
        return 150;
    }

    @Override
    public String emailColumnLabel() {
        return displayStrings.emailColumnLabel();
    }

    @Override
    public int institutionColumnWidth() {
        return 90;
    }

    @Override
    public String institutionColumnLabel() {
        return displayStrings.institutionColumnLabel();
    }
}
