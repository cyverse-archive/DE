package org.iplantc.de.theme.base.client.admin.toolAdmin;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;

import com.sencha.gxt.core.client.XTemplates;

/**
 * Created by aramsey on 10/26/15.
 */
public class ToolAdminViewDefaultAppearance implements ToolAdminView.ToolAdminViewAppearance {


    interface Templates extends XTemplates {
        @XTemplates.XTemplate("<span style='color: red;'>*&nbsp</span>{label}")
        SafeHtml requiredFieldLabel(String label);
    }

    private final ToolAdminDisplayStrings displayStrings;
    private final IplantResources iplantResources;
    private final IplantDisplayStrings iplantDisplayStrings;
    private final Templates templates;

    public ToolAdminViewDefaultAppearance() {
        this(GWT.<ToolAdminDisplayStrings>create(ToolAdminDisplayStrings.class),
             GWT.<IplantResources>create(IplantResources.class),
             GWT.<IplantDisplayStrings>create(IplantDisplayStrings.class),
             GWT.<Templates>create(Templates.class));

    }

    ToolAdminViewDefaultAppearance(final ToolAdminDisplayStrings displayStrings,
                                   final IplantResources iplantResources,
                                   final IplantDisplayStrings iplantDisplayStrings,
                                   Templates templates) {
        this.displayStrings = displayStrings;
        this.iplantResources = iplantResources;
        this.templates = templates;
        this.iplantDisplayStrings = iplantDisplayStrings;
    }

    @Override
    public String add() {
        return displayStrings.add();
    }

    @Override
    public String filter() {
        return displayStrings.filter();
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
    public String nameColumnLabel() {
        return displayStrings.nameColumnLabel();
    }

    @Override
    public int nameColumnWidth() {
        return 90;
    }

    @Override
    public String descriptionColumnLabel() {
        return displayStrings.descriptionColumnLabel();
    }

    @Override
    public int descriptionColumnWidth() {
        return 150;
    }

    @Override
    public String attributionColumnLabel() {
        return displayStrings.attributionColumnLabel();
    }

    @Override
    public int attributionColumnWidth() {
        return 90;
    }

    @Override
    public String locationColumnInfoLabel() {
        return displayStrings.locationColumnInfoLabel();
    }

    @Override
    public int locationColumnInfoWidth() {
        return 90;
    }

    @Override
    public String versionColumnInfoLabel() {
        return displayStrings.versionColumnInfoLabel();
    }

    @Override
    public int versionColumnInfoWidth() {
        return 50;
    }

    @Override
    public String typeColumnInfoLabel() {
        return displayStrings.typeColumnInfoLabel();
    }

    @Override
    public int typeColumnInfoWidth() {
        return 50;
    }

    @Override
    public String toolImportDescriptionLabel() {
        return displayStrings.toolImportDescriptionLabel();
    }

    @Override
    public SafeHtml toolImportNameLabel() {
        return templates.requiredFieldLabel(displayStrings.toolImportNameLabel());
    }

    @Override
    public SafeHtml toolImportTypeLabel() {
        return templates.requiredFieldLabel(displayStrings.toolImportTypeLabel());
    }

    @Override
    public String toolImportTypeDefaultValue() {
        return displayStrings.toolImportTypeDefaultValue();
    }

    @Override
    public String toolImportAttributionLabel() {
        return displayStrings.toolImportAttributionLabel();
    }

    @Override
    public String toolImportVersionLabel() {
        return displayStrings.toolImportVersionLabel();
    }

    @Override
    public SafeHtml toolImportLocationLabel() {
        return templates.requiredFieldLabel(displayStrings.toolImportLocationLabel());
    }

    @Override
    public String containerDetailsFieldSetLabel() {
        return displayStrings.containerDetailsFieldSetLabel();
    }

    @Override
    public String containerNameLabel() {
        return displayStrings.containerNameLabel();
    }

    @Override
    public String containerWorkingDirLabel() {
        return displayStrings.containerWorkingDirLabel();
    }

    @Override
    public String containerEntryPointLabel() {
        return displayStrings.containerEntryPointLabel();
    }

    @Override
    public String containerMemoryLimitLabel() {
        return displayStrings.containerMemoryLimitLabel();
    }

    @Override
    public int containerMemoryLimitDefaultValue() {
        return 0;
    }

    @Override
    public String containerCPUSharesLabel() {
        return displayStrings.containerCPUSharesLabel();
    }

    @Override
    public int containerCPUSharesDefaultValue() {
        return 0;
    }

    @Override
    public String containerNetworkModeLabel() {
        return displayStrings.containerNetworkModeLabel();
    }

    @Override
    public String containerDevicesLabel() {
        return displayStrings.containerDevicesLabel();
    }

    @Override
    public String containerDevicesHostPathLabel() {
        return displayStrings.containerDevicesHostPathLabel();
    }

    @Override
    public int containerDevicesHostPathWidth() {
        return 400;
    }

    @Override
    public String containerDevicesContainerPathLabel() {
        return displayStrings.containerDevicesContainerPathLabel();
    }

    @Override
    public int containerDevicesContainerPathWidth() {
        return 400;
    }

    @Override
    public String containerVolumesLabel() {
        return displayStrings.containerVolumesLabel();
    }

    @Override
    public String containerVolumesHostPathLabel() {
        return displayStrings.containerVolumesHostPathLabel();
    }

    @Override
    public int containerVolumesHostPathWidth() {
        return 400;
    }

    @Override
    public String containerVolumesContainerPathLabel() {
        return displayStrings.containerVolumesContainerPathLabel();
    }

    @Override
    public int containerVolumesContainerPathWidth() {
        return 400;
    }

    @Override
    public String containerImageFieldSetLabel() {
        return displayStrings.containerImageFieldSetLabel();
    }

    @Override
    public SafeHtml containerImageNameLabel() {
        return templates.requiredFieldLabel(displayStrings.containerImageNameLabel());
    }

    @Override
    public String containerImageTagLabel() {
        return displayStrings.containerImageTagLabel();
    }

    @Override
    public String containerImageURLLabel() {
        return displayStrings.containerImageURLLabel();
    }

    @Override
    public String containerVolumesFromLabel() {
        return displayStrings.containerVolumesFromLabel();
    }

    @Override
    public String containerVolumesFromNameLabel() {
        return displayStrings.containerVolumesFromNameLabel();
    }

    @Override
    public int containerVolumesFromNameWidth() {
        return 300;
    }

    @Override
    public String containerVolumesFromTagLabel() {
        return displayStrings.containerVolumesFromTagLabel();
    }

    @Override
    public int containerVolumesFromTagWidth() {
        return 50;
    }

    @Override
    public String containerVolumesFromURLLabel() {
        return displayStrings.containerVolumesFromURLLabel();
    }

    @Override
    public int containervolumesFromURLWidth() {
        return 350;
    }

    @Override
    public String containerVolumesFromNamePrefixLabel() {
        return displayStrings.containerVolumesFromNamePrefixLabel();
    }

    @Override
    public int containerVolumesFromNamePrefixWidth() {
        return 150;
    }

    @Override
    public String containerVolumesFromReadOnlyLabel() {
        return displayStrings.containerVolumesFromReadyOnlyLabel();
    }

    @Override
    public int containerVolumesFromReadOnlyWidth() {
        return 75;
    }

    @Override
    public String toolImplementationFieldSetLabel() {
        return displayStrings.toolImplementationFieldSetLabel();
    }

    @Override
    public SafeHtml toolImplementationImplementorLabel() {
        return templates.requiredFieldLabel(displayStrings.toolImplementationImplementorLabel());
    }

    @Override
    public SafeHtml toolImplementationImplementorEmailLabel() {
        return templates.requiredFieldLabel(displayStrings.toolImplementationImplementorEmailLabel());
    }

    @Override
    public int toolTestDataParamsWidth() {
        return 500;
    }

    @Override
    public String toolTestDataInputFilesLabel() {
        return displayStrings.testToolDataInputFilesLabel();
    }

    @Override
    public int toolTestDataInputFilesWidth() {
        return 500;
    }

    @Override
    public String toolTestDataOutputFilesLabel() {
        return displayStrings.testToolOutputFilesLabel();
    }

    @Override
    public int toolTestDataOutputFilesWidth() {
        return 500;
    }

    @Override
    public String dialogWindowName() {
        return displayStrings.dialogWindowName();
    }

    @Override
    public String dialogWindowUpdateBtnText() {
        return displayStrings.dialogWindowUpdateBtnText();
    }

    @Override
    public String addToolSuccessText() {
        return displayStrings.addToolSuccessText();
    }

    @Override
    public String updateToolSuccessText() {
        return displayStrings.updateToolSuccessText();
    }

    @Override
    public String dialogWindowDeleteBtnText() {
        return displayStrings.dialogWindowDeleteBtnText();
    }

    @Override
    public String deleteToolSuccessText() {
        return displayStrings.deleteToolSuccessText();
    }

    @Override
    public String completeRequiredFieldsError() {
        return iplantDisplayStrings.completeRequiredFieldsError();
    }

    @Override
    public String confirmOverwriteTitle() {
        return displayStrings.confirmOverwriteTitle();
    }

    @Override
    public String confirmOverwriteDangerZone() {
        return displayStrings.confirmOverwriteDangerZone();
    }

    @Override
    public String confirmOverwriteBody() {
        return displayStrings.confirmOverwriteBody();
    }

    @Override
    public String deletePublicToolTitle() {
        return displayStrings.deletePublicToolTitle();
    }

    @Override
    public String deletePublicToolBody() {
        return displayStrings.deletePublicToolBody();
    }

    @Override
    public int publicAppNameColumnWidth() {
        return 200;
    }

    @Override
    public String publicAppNameLabel() {
        return displayStrings.publicAppNameLabel();
    }

    @Override
    public int publicAppIntegratorColumnWidth() {
        return 100;
    }

    @Override
    public String publicAppIntegratorLabel() {
        return displayStrings.publicAppIntegratorLabel();
    }

    @Override
    public int publicAppIntegratorEmailColumnWidth() {
        return 200;
    }

    @Override
    public String publicAppIntegratorEmailLabel() {
        return displayStrings.publicAppIntegratorEmailLabel();
    }

    @Override
    public int publicAppDisabledColumnWidth() {
        return 100;
    }

    @Override
    public String publicAppDisabledLabel() {
        return displayStrings.publicAppDisabledLabel();
    }

}
