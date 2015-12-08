package org.iplantc.de.admin.desktop.client.toolAdmin;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by aramsey on 10/26/15.
 */
public interface ToolAdminView extends IsWidget, IsMaskable {

    interface ToolAdminViewAppearance {

        String add();

        String filter();

        ImageResource addIcon();

        ImageResource deleteIcon();

        String nameColumnLabel();

        int nameColumnWidth();

        String descriptionColumnLabel();

        int descriptionColumnWidth();

        String attributionColumnLabel();

        int attributionColumnWidth();

        String locationColumnInfoLabel();

        int locationColumnInfoWidth();

        String versionColumnInfoLabel();

        int versionColumnInfoWidth();

        String typeColumnInfoLabel();

        int typeColumnInfoWidth();

        String toolImportDescriptionLabel();

        SafeHtml toolImportNameLabel();

        SafeHtml toolImportTypeLabel();

        String toolImportTypeDefaultValue();

        String toolImportAttributionLabel();

        String toolImportVersionLabel();

        SafeHtml toolImportLocationLabel();

        String containerDetailsFieldSetLabel();

        String containerNameLabel();

        String containerWorkingDirLabel();

        String containerEntryPointLabel();

        String containerMemoryLimitLabel();

        int containerMemoryLimitDefaultValue();

        String containerCPUSharesLabel();

        int containerCPUSharesDefaultValue();

        String containerNetworkModeLabel();

        String containerDevicesLabel();

        String containerDevicesHostPathLabel();

        int containerDevicesHostPathWidth();

        String containerDevicesContainerPathLabel();

        int containerDevicesContainerPathWidth();

        String containerVolumesLabel();

        String containerVolumesHostPathLabel();

        int containerVolumesHostPathWidth();

        String containerVolumesContainerPathLabel();

        int containerVolumesContainerPathWidth();

        String containerImageFieldSetLabel();

        SafeHtml containerImageNameLabel();

        String containerImageTagLabel();

        String containerImageURLLabel();

        String containerVolumesFromLabel();

        String containerVolumesFromNameLabel();

        int containerVolumesFromNameWidth();

        String containerVolumesFromTagLabel();

        int containerVolumesFromTagWidth();

        String containerVolumesFromURLLabel();

        int containervolumesFromURLWidth();

        String containerVolumesFromNamePrefixLabel();

        int containerVolumesFromNamePrefixWidth();

        String containerVolumesFromReadOnlyLabel();

        int containerVolumesFromReadOnlyWidth();

        String toolImplementationFieldSetLabel();

        SafeHtml toolImplementationImplementorLabel();

        SafeHtml toolImplementationImplementorEmailLabel();

        String toolTestDataParamsLabel();

        int toolTestDataParamsWidth();

        String toolTestDataInputFilesLabel();

        int toolTestDataInputFilesWidth();

        String toolTestDataOutputFilesLabel();

        int toolTestDataOutputFilesWidth();

        String dialogWindowName();

        String dialogWindowUpdateBtnText();

        String addToolSuccessText();

        String updateToolSuccessText();

        String dialogWindowDeleteBtnText();

        String deleteToolSuccessText();

        String completeRequiredFieldsError();

        String confirmOverwriteTitle();

        String confirmOverwriteDangerZone();

        String confirmOverwriteBody();

        String deletePublicToolTitle();

        String deletePublicToolBody();

        int publicAppNameColumnWidth();

        String publicAppNameLabel();

        int publicAppIntegratorColumnWidth();

        String publicAppIntegratorLabel();

        int publicAppIntegratorEmailColumnWidth();

        String publicAppIntegratorEmailLabel();

        int publicAppDisabledColumnWidth();

        String publicAppDisabledLabel();

    }

    interface Presenter {

        void go(HasOneWidget container);

        void addTool(Tool tool);

        void updateTool(Tool tool);

        void getToolDetails(Tool tool);

        void deleteTool(Tool tool);

    }

    void setPresenter(Presenter presenter);

    void setToolList(List<Tool> tools);

    void setToolDetails(Tool tool);

    void deleteTool(String toolId);

}
