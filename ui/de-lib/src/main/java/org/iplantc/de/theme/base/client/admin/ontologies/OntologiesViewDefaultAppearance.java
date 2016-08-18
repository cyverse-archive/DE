package org.iplantc.de.theme.base.client.admin.ontologies;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;

import com.sencha.gxt.core.client.XTemplates;

import java.util.List;

/**
 * @author aramsey
 */
public class OntologiesViewDefaultAppearance implements OntologiesView.OntologiesViewAppearance {

    interface Templates extends XTemplates {

        @XTemplates.XTemplate("<b>{label}</b>")
        SafeHtml boldLabel(String label);

        @XTemplates.XTemplate("<span style='color: red;'><b>{label}</b></span>")
        SafeHtml boldRedLabel(String label);
    }

    interface HelpTemplates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("{0}<img style='float: right;' src='{1}' qtip='{2}'></img>")
        SafeHtml fieldLabelImgFloatRight(SafeHtml label, SafeUri img, String toolTip);
    }

    private OntologyDisplayStrings displayStrings;
    private IplantResources iplantResources;
    private IplantDisplayStrings iplantDisplayStrings;
    private HelpTemplates helpTemplates;
    private Templates templates;

    public OntologiesViewDefaultAppearance() {
        this(GWT.<OntologyDisplayStrings>create(OntologyDisplayStrings.class),
             GWT.<IplantResources>create(IplantResources.class),
             GWT.<IplantDisplayStrings>create(IplantDisplayStrings.class),
             GWT.<HelpTemplates>create(HelpTemplates.class),
             GWT.<Templates>create(Templates.class));
    }

    OntologiesViewDefaultAppearance(OntologyDisplayStrings displayStrings,
                                    IplantResources iplantResources,
                                    IplantDisplayStrings iplantDisplayStrings,
                                    HelpTemplates helpTemplates,
                                    Templates templates) {
        this.displayStrings = displayStrings;
        this.iplantResources = iplantResources;
        this.iplantDisplayStrings = iplantDisplayStrings;
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
    public String ontologyList() {
        return displayStrings.ontologyList();
    }

    @Override
    public String setActiveVersion() {
        return displayStrings.setActiveVersion();
    }

    @Override
    public String upload() {
        return iplantDisplayStrings.upload();
    }

    @Override
    public String fileUploadMaxSizeWarning() {
        return displayStrings.fileUploadMaxSizeWarning();
    }

    @Override
    public String reset() {
        return displayStrings.reset();
    }

    @Override
    public ImageResource arrowUndoIcon() {
        return iplantResources.arrowUndoIcon();
    }

    @Override
    public String confirmAction() {
        return iplantDisplayStrings.confirmAction();
    }

    @Override
    public String closeConfirmMessage() {
        return iplantDisplayStrings.transferCloseConfirmMessage();
    }

    @Override
    public String fileUploadsFailed(String file) {
        return displayStrings.fileUploadFailed(file);
    }

    @Override
    public String fileUploadSuccess(String file) {
        return displayStrings.fileUploadSuccess(file);
    }

    @Override
    public ImageResource refreshIcon() {
        return iplantResources.refresh();
    }

    @Override
    public String publishOntology() {
        return displayStrings.publishOntology();
    }

    @Override
    public ImageResource publishIcon() {
        return iplantResources.publish();
    }

    @Override
    public String activeOntologyLabel() {
        return displayStrings.activeOntologyLabel();
    }

    @Override
    public String editedOntologyLabel() {
        return displayStrings.editedOntologyLabel();
    }

    @Override
    public String setActiveOntologySuccess() {
        return displayStrings.setActiveOntologySuccess();
    }

    @Override
    public String saveHierarchy() {
        return displayStrings.saveHierarchy();
    }

    @Override
    public ImageResource saveIcon() {
        return iplantResources.save();
    }

    @Override
    public String selectOntologyVersion() {
        return displayStrings.selectOntologyVersion();
    }

    @Override
    public SafeHtml activeOntologyField(String version) {
        return templates.boldLabel(version);
    }

    @Override
    public SafeHtml editedOntologyField(String version) {
        return templates.boldRedLabel(version);
    }

    @Override
    public String successTopicSaved() {
        return displayStrings.successTopicSaved();
    }

    @Override
    public String successOperationSaved() {
        return displayStrings.successOperationSaved();
    }

    @Override
    public SafeHtml publishOntologyWarning() {
        return templates.boldLabel(displayStrings.publishOntologyWarning());
    }

    @Override
    public String rootIriLabel() {
        return displayStrings.rootIriLabel();
    }

    @Override
    public String enterIriEmptyText() {
        return displayStrings.enterIriEmptyText();
    }

    @Override
    public String add() {
        return iplantDisplayStrings.add();
    }

    @Override
    public ImageResource deleteIcon() {
        return iplantResources.delete();
    }

    @Override
    public String delete() {
        return iplantDisplayStrings.delete();
    }

    @Override
    public String invalidHierarchySubmitted(String iri) {
        return displayStrings.invalidHierarchySubmitted(iri);
    }

    @Override
    public String clearHierarchySelection() {
        return displayStrings.clearHierarchySelection();
    }

    @Override
    public ImageResource blueFolder() {
        return iplantResources.category();
    }

    @Override
    public ImageResource blueFolderOpen() {
        return iplantResources.category_open();
    }

    @Override
    public ImageResource blueFolderLeaf() {
        return iplantResources.subCategory();
    }

    @Override
    public String categorize() {
        return displayStrings.categorize();
    }

    @Override
    public String categorizeDialogWidth() {
        return "400px";
    }

    @Override
    public String categorizeDialogHeight() {
        return "400px";
    }

    @Override
    public String categorizeApp(App targetApp) {
        return displayStrings.categorizeApp(targetApp.getName());
    }

    @Override
    public String appAvusCleared(App targetApp) {
        return displayStrings.appAvusCleared(targetApp.getName());
    }

    @Override
    public String appClassified(String name, List<Avu> result) {
        List<String> tags = Lists.newArrayList();
        for (Avu avu : result) {
            tags.add(avu.getUnit());
        }
        return displayStrings.appClassifiedList(name, tags);
    }

    @Override
    public String appClassified(String name, String label) {
        return displayStrings.appClassified(name,label);
    }

    @Override
    public String loadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public int publishDialogWidth() {
        return 500;
    }

    @Override
    public int publishDialogMinHeight() {
        return 200;
    }

    @Override
    public int publishDialogMinWidth() {
        return 500;
    }

    @Override
    public int publishDialogHeight() {
        return 200;
    }

    @Override
    public String activeOntologyFieldWidth() {
        return "400";
    }

    @Override
    public String editedOntologyFieldWidth() {
        return "400";
    }

    @Override
    public int rootIriLabelWidth() {
        return 400;
    }

    @Override
    public String emptyDEOntologyLabel() {
        return displayStrings.emptyDEOntologyLabel();
    }

    @Override
    public String deleteOntology() {
        return displayStrings.deleteOntology();
    }

    @Override
    public String deleteHierarchy() {
        return displayStrings.deleteHierarchy();
    }

    @Override
    public String hierarchyDeleted(String hierarchy) {
        return displayStrings.hierarchyDeleted(hierarchy);
    }

    @Override
    public String confirmDeleteOntology(String version) {
        return displayStrings.confirmDeleteOntology(version);
    }

    @Override
    public String ontologyDeleted(String ontologyVersion) {
        return displayStrings.ontologyDeleted(ontologyVersion);
    }

    @Override
    public int rootColumnWidth() {
        return 200;
    }

    @Override
    public String rootColumnLabel() {
        return displayStrings.rootColumnLabel();
    }

    @Override
    public int hierarchyColumnWidth() {
        return 100;
    }

    @Override
    public String hierarchyColumnLabel() {
        return displayStrings.hierarchyColumnLabel();
    }

    @Override
    public String confirmDeleteHierarchy(String selectedItem) {
        return displayStrings.confirmDeleteHierarchy(selectedItem);
    }

    @Override
    public String confirmDeleteAppWarning(String name) {
        return displayStrings.confirmDeleteAppWarning(name);
    }

    @Override
    public String confirmDeleteAppTitle() {
        return displayStrings.confirmDeleteAppTitle();
    }

    @Override
    public String externalAppDND(String appLabels) {
        return displayStrings.externalAppDND(appLabels);
    }

    public String ontologyAttrMatchingError() {
        return displayStrings.ontologyAttrMatchingError();
    }

    public String emptySearchFieldText() {
        return displayStrings.emptySearchFieldText();
    }
}
