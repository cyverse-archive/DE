package org.iplantc.de.theme.base.client.admin.ontologies;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;

import com.sencha.gxt.core.client.XTemplates;

/**
 * @author aramsey
 */
public class OntologiesViewDefaultAppearance implements OntologiesView.OntologiesViewAppearance {

    interface Templates extends XTemplates {
        @XTemplates.XTemplate("<span style='color: red;'>*&nbsp</span>{label}")
        SafeHtml requiredFieldLabel(String label);
    }

    interface HelpTemplates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("{0}<img style='float: right;' src='{1}' qtip='{2}'></img>")
        SafeHtml fieldLabelImgFloatRight(SafeHtml label, SafeUri img, String toolTip);
    }

    private OntologyDisplayStrings displayStrings;
    private IplantResources iplantResources;
    private HelpTemplates helpTemplates;
    private Templates templates;

    public OntologiesViewDefaultAppearance() {
        this(GWT.<OntologyDisplayStrings>create(OntologyDisplayStrings.class),
        GWT.<IplantResources>create(IplantResources.class),
        GWT.<HelpTemplates>create(HelpTemplates.class),
        GWT.<Templates>create(Templates.class));
    }

    OntologiesViewDefaultAppearance(OntologyDisplayStrings displayStrings,
                                    IplantResources iplantResources,
                                    HelpTemplates helpTemplates,
                                    Templates templates) {
        this.displayStrings = displayStrings;
        this.iplantResources = iplantResources;
        this.helpTemplates = helpTemplates;
        this.templates = templates;
    }

    @Override
    public String addOntology() {
        return displayStrings.addOntology();
    }

    @Override
    public ImageResource addIcon() {
        return iplantResources.add();
    }

    @Override
    public String ontologyListDialogName() {
        return displayStrings.ontologyListDialogName();
    }

    @Override
    public String ontologyList() {
        return displayStrings.ontologyList();
    }

    @Override
    public int iriColumnWidth() {
        return 100;
    }

    @Override
    public String iriColumnLabel() {
        return displayStrings.iriColumnLabel();
    }

    @Override
    public int versionColumnWidth() {
        return 100;
    }

    @Override
    public String versionColumnLabel() {
        return displayStrings.versionColumnLabel();
    }

    @Override
    public int createdByColumnWidth() {
        return 100;
    }

    @Override
    public String createdByColumnLabel() {
        return displayStrings.createdByColumnLabel();
    }

    @Override
    public int createdOnColumnWidth() {
        return 100;
    }

    @Override
    public String createdOnColumnLabel() {
        return displayStrings.createdOnColumnLabel();
    }

    @Override
    public String setActiveVersion() {
        return displayStrings.setActiveVersion();
    }
}
