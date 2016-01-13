package org.iplantc.de.theme.base.client.admin.toolAdmin;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;


/**
 * @author aramsey
 */
public interface ToolAdminDisplayStrings extends Messages {

    @Key("add")
    String add();

    @Key("filter")
    String filter();

    @Key("nameColumnLabel")
    String nameColumnLabel();

    @Key("descriptionColumnLabel")
    String descriptionColumnLabel();

    @Key("attributionColumnLabel")
    String attributionColumnLabel();

    @Key("locationColumnInfoLabel")
    String locationColumnInfoLabel();

    @Key("versionColumnInfoLabel")
    String versionColumnInfoLabel();

    @Key("typeColumnInfoLabel")
    String typeColumnInfoLabel();

    @Key("toolImportDescriptionLabel")
    String toolImportDescriptionLabel();

    @Key("toolImportNameLabel")
    String toolImportNameLabel();

    @Key("toolImportTypeLabel")
    String toolImportTypeLabel();

    @Key("toolImportTypeDefaultValue")
    String toolImportTypeDefaultValue();

    @Key("toolImportAttributionLabel")
    String toolImportAttributionLabel();

    @Key("toolImportVersionLabel")
    String toolImportVersionLabel();

    @Key("toolImportLocationLabel")
    String toolImportLocationLabel();

    @Key("containerNameLabel")
    String containerNameLabel();

    @Key("containerWorkingDirLabel")
    String containerWorkingDirLabel();

    @Key("containerEntryPointLabel")
    String containerEntryPointLabel();

    @Key("containerMemoryLimitLabel")
    String containerMemoryLimitLabel();

    @Key("containerCPUSharesLabel")
    String containerCPUSharesLabel();

    @Key("containerNetworkModeLabel")
    String containerNetworkModeLabel();

    @Key("containerDevicesLabel")
    String containerDevicesLabel();

    @Key("containerDevicesHostPathLabel")
    String containerDevicesHostPathLabel();

    @Key("containerDevicesContainerPathLabel")
    String containerDevicesContainerPathLabel();

    @Key("containerVolumesLabel")
    String containerVolumesLabel();

    @Key("containerVolumesHostPathLabel")
    String containerVolumesHostPathLabel();

    @Key("containerVolumesContainerPathLabel")
    String containerVolumesContainerPathLabel();

    @Key("containerImageNameLabel")
    String containerImageNameLabel();

    @Key("containerImageTagLabel")
    String containerImageTagLabel();

    @Key("containerImageURLLabel")
    String containerImageURLLabel();

    @Key("containerVolumesFromLabel")
    String containerVolumesFromLabel();

    @Key("containerVolumesFromNameLabel")
    String containerVolumesFromNameLabel();

    @Key("containerVolumesFromTagLabel")
    String containerVolumesFromTagLabel();

    @Key("containerVolumesFromURLLabel")
    String containerVolumesFromURLLabel();

    @Key("containerVolumesFromNamePrefixLabel")
    String containerVolumesFromNamePrefixLabel();

    @Key("containerVolumesFromReadyOnlyLabel")
    String containerVolumesFromReadyOnlyLabel();

    @Key("toolImplementationImplementorLabel")
    String toolImplementationImplementorLabel();

    @Key("toolImplementationImplementorEmailLabel")
    String toolImplementationImplementorEmailLabel();

    @Key("testToolDataInputFilesLabel")
    String testToolDataInputFilesLabel();

    @Key("testToolDataInputFilesColumnLabel")
    String testToolDataInputFilesColumnLabel();

    @Key("testToolOutputFilesLabel")
    String testToolOutputFilesLabel();

    @Key("testToolOutputFilesColumnLabel")
    String testToolOutputFilesColumnLabel();

    @Key("dialogWindowName")
    String dialogWindowName();

    @Key("dialogWindowUpdateBtnText")
    String dialogWindowUpdateBtnText();

    @Key("dialogWindowDeleteBtnText")
    String dialogWindowDeleteBtnText();

    @Key("deleteToolSuccessText")
    String deleteToolSuccessText();

    @Key("addToolSuccessText")
    String addToolSuccessText();

    @Key("updateToolSuccessText")
    String updateToolSuccessText();

    @Key("containerDetailsFieldSetLabel")
    String containerDetailsFieldSetLabel();

    @Key("containerImageFieldSetLabel")
    String containerImageFieldSetLabel();

    @Key("toolImplementationFieldSetLabel")
    String toolImplementationFieldSetLabel();

    @Key("confirmOverwriteTitle")
    String confirmOverwriteTitle();

    @Key("confirmOverwriteBody")
    String confirmOverwriteBody();

    @Key("deletePublicToolTitle")
    String deletePublicToolTitle();

    @Key("deletePublicToolBody")
    String deletePublicToolBody();

    @Key("confirmOverwriteDangerZone")
    String confirmOverwriteDangerZone();

    @Key("publicAppNameLabel")
    String publicAppNameLabel();

    @Key("publicAppIntegratorLabel")
    String publicAppIntegratorLabel();

    @Key("publicAppIntegratorEmailLabel")
    String publicAppIntegratorEmailLabel();

    @Key("publicAppDisabledLabel")
    String publicAppDisabledLabel();

    @Key("inputFilesHelp")
    String inputFilesHelp();

    @Key("outputFilesHelp")
    String outputFilesHelp();

    @Key("containerDeviceHelp")
    String containerDeviceHelp();

    @Key("containerVolumeHelp")
    String containerVolumeHelp();

    @Key("containerVolumesFromHelp")
    String containerVolumesFromHelp();

    @Key("toolAdminHelp")
    SafeHtml toolAdminHelp();

    @Key("toolEntryPointWarning")
    SafeHtml toolEntryPointWarning();

    @Key("toolVolumeWarning")
    SafeHtml toolVolumeWarning();
}
