package org.iplantc.de.theme.base.client.admin.refGenome;

import org.iplantc.de.admin.desktop.client.refGenome.RefGenomeView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;

import com.sencha.gxt.core.client.XTemplates;

/**
 * @author jstroot
 */
public class RefGenomeDefaultAppearance implements RefGenomeView.RefGenomeAppearance {

    interface Templates extends XTemplates {
        @XTemplates.XTemplate("<span style='color: red;'>*&nbsp</span>{label}")
        SafeHtml requiredFieldLabel(String label);
    }

    private final IplantDisplayStrings iplantDisplayStrings;
    private final IplantResources iplantResources;
    private final ReferenceGenomeDisplayStrings displayStrings;
    private final Templates templates;

    public RefGenomeDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantResources> create(IplantResources.class),
             GWT.<ReferenceGenomeDisplayStrings> create(ReferenceGenomeDisplayStrings.class),
             GWT.<Templates> create(Templates.class));
    }

    RefGenomeDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                               final IplantResources iplantResources,
                               final ReferenceGenomeDisplayStrings displayStrings,
                               final Templates templates) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantResources = iplantResources;
        this.displayStrings = displayStrings;
        this.templates = templates;
    }

    @Override
    public String add() {
        return iplantDisplayStrings.add();
    }

    @Override
    public String addReferenceGenomeDialogHeading() {
        return displayStrings.addReferenceGenome();
    }

    @Override
    public ImageResource categoryIcon() {
        return iplantResources.category();
    }

    @Override
    public String createdByColumn() {
        return displayStrings.createdBy();
    }

    @Override
    public String createdOnColumn() {
        return displayStrings.createdOn();
    }

    @Override
    public String editReferenceGenomeDialogHeading(String refGenomeName) {
        return iplantDisplayStrings.edit() + ": " + refGenomeName;
    }

    @Override
    public String filterDataListEmptyText() {
        return displayStrings.filterDataList();
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
        return iplantDisplayStrings.lastModified();
    }

    @Override
    public String lastModBy() {
        return displayStrings.lastModBy();
    }

    @Override
    public String nameColumn() {
        return iplantDisplayStrings.name();
    }

    @Override
    public String pathColumn() {
        return iplantDisplayStrings.path();
    }

    @Override
    public String refDeletePrompt() {
        return displayStrings.refDeletePrompt();
    }

    @Override
    public SafeHtml requiredNameLabel() {
        return templates.requiredFieldLabel(iplantDisplayStrings.name());
    }

    @Override
    public SafeHtml requiredPathLabel() {
        return templates.requiredFieldLabel(iplantDisplayStrings.path());
    }

    @Override
    public String saveBtnText() {
        return iplantDisplayStrings.save();
    }
}
