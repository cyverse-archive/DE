package org.iplantc.de.theme.base.client.admin.metadata;

import org.iplantc.de.admin.desktop.client.metadata.view.TemplateListingView.TemplateListingAppearance;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

public class DefaultTemplateListingAppearance implements TemplateListingAppearance {

    private final IplantDisplayStrings iplantDisplayStrings;
    private final MetadataDisplayStrings displayStrings;
    private final IplantResources iplantResources;

    public DefaultTemplateListingAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<MetadataDisplayStrings> create(MetadataDisplayStrings.class),
             GWT.<IplantResources> create(IplantResources.class));
    }

    public DefaultTemplateListingAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                            final MetadataDisplayStrings displayStrings,
                                            final IplantResources iplantResources) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.displayStrings = displayStrings;
        this.iplantResources = iplantResources;
    }

    @Override
    public String add() {
        return iplantDisplayStrings.add();
    }

    @Override
    public String delete() {
        return iplantDisplayStrings.delete();
    }

    @Override
    public String deleted() {
        return iplantDisplayStrings.deleted();
    }

    @Override
    public String edit() {
        return iplantDisplayStrings.edit();
    }

    @Override
    public ImageResource addIcon() {
        return iplantResources.add();
    }

    @Override
    public ImageResource deleteIcon() {
        return iplantResources.delete();
    }

    @Override
    public ImageResource editIcon() {
        return iplantResources.edit();
    }

    @Override
    public String createdByColumn() {
        return displayStrings.createdByColumn();
    }

    @Override
    public String createdOnColumn() {
        return displayStrings.createdOnColumn();
    }

    @Override
    public String createdBy() {
        return displayStrings.createdBy();
    }

    @Override
    public String createdOn() {
        return displayStrings.createdOn();
    }

    @Override
    public String lastModified() {
        return displayStrings.lastModified();
    }

    @Override
    public String lastModBy() {
        return displayStrings.lastModBy();
    }

    @Override
    public String nameColumn() {
        return displayStrings.nameColumn();
    }

    @Override
    public String descriptionColumn() {
        return displayStrings.descriptionColumn();
    }

}
