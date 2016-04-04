package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.tool.ToolContainer;
import org.iplantc.de.commons.client.widgets.EmptyStringValueChangeHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.IntegerField;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

public class ToolContainerEditor extends Composite implements Editor<ToolContainer> {

    interface ToolContainerEditorBinder extends UiBinder<Widget, ToolContainerEditor> {
    }

    private static ToolContainerEditorBinder uiBinder = GWT.create(ToolContainerEditorBinder.class);

    @UiField TextField nameEditor;
    @UiField TextField workingDirectoryEditor;
    @Ignore
    @UiField HTML entryPointWarningHTML;
    @UiField TextField entryPointEditor;
    @UiField IntegerField memoryLimitEditor;
    @UiField IntegerField cpuSharesEditor;
    @UiField TextField networkModeEditor;
    @Ignore
    @UiField FieldLabel containerDevicesLabel;
    @Ignore
    @UiField TextButton addDeviceButton;
    @Ignore
    @UiField TextButton deleteDeviceButton;
    @UiField (provided = true) ToolDeviceListEditor deviceListEditor;
    @Ignore
    @UiField HTML toolVolumesWarningHTML;
    @Ignore
    @UiField FieldLabel containerVolumesLabel;
    @Ignore
    @UiField TextButton addVolumesButton;
    @Ignore
    @UiField TextButton deleteVolumesButton;
    @UiField (provided = true) ToolVolumeListEditor containerVolumesEditor;
    @Ignore
    @UiField FieldLabel containerVolumesFromLabel;
    @Ignore
    @UiField TextButton addVolumesFromButton;
    @Ignore
    @UiField TextButton deleteVolumesFromButton;
    @UiField (provided = true) ToolVolumesFromListEditor containerVolumesFromEditor;
    @UiField (provided = true) ToolImageEditor imageEditor;
    @Ignore
    @UiField FieldSet containerFieldSet;
    @UiField (provided = true) ToolAdminView.ToolAdminViewAppearance appearance;

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        nameEditor.setId(baseID + Belphegor.ToolAdminIds.CONTAINER_NAME);
        workingDirectoryEditor.setId(baseID + Belphegor.ToolAdminIds.CONTAINER_WORKING_DIR);
        entryPointEditor.setId(baseID + Belphegor.ToolAdminIds.CONTAINER_ENTRY_POINT);
        memoryLimitEditor.setId(baseID + Belphegor.ToolAdminIds.CONTAINER_MEMORY);
        cpuSharesEditor.setId(baseID + Belphegor.ToolAdminIds.CONTAINER_CPU);
        networkModeEditor.setId(baseID + Belphegor.ToolAdminIds.CONTAINER_NETWORK_MODE);
        containerDevicesLabel.ensureDebugId(baseID + Belphegor.ToolAdminIds.CONTAINER_DEVICES_LABEL);
        addDeviceButton.ensureDebugId(baseID + Belphegor.ToolAdminIds.CONTAINER_DEVICES_ADD);
        deleteDeviceButton.ensureDebugId(baseID + Belphegor.ToolAdminIds.CONTAINER_DEVICES_DELETE);
        containerVolumesLabel.ensureDebugId(baseID + Belphegor.ToolAdminIds.CONTAINER_VOLUMES_LABEL);
        addVolumesButton.ensureDebugId(baseID + Belphegor.ToolAdminIds.CONTAINER_VOLUMES_ADD);
        deleteVolumesButton.ensureDebugId(baseID + Belphegor.ToolAdminIds.CONTAINER_VOLUMES_DELETE);
        containerVolumesFromLabel.ensureDebugId(baseID + Belphegor.ToolAdminIds.CONTAINER_VOLUMES_FROM_LABEL);
        addVolumesFromButton.ensureDebugId(baseID + Belphegor.ToolAdminIds.CONTAINER_VOLUMES_FROM_ADD);
        deleteVolumesFromButton.ensureDebugId(baseID + Belphegor.ToolAdminIds.CONTAINER_VOLUMES_FROM_DELETE);

        deviceListEditor.ensureDebugId(baseID + Belphegor.ToolAdminIds.TOOL_DEVICES);
        containerVolumesEditor.ensureDebugId(baseID + Belphegor.ToolAdminIds.CONTAINER_VOLUMES);
        containerVolumesFromEditor.ensureDebugId(baseID + Belphegor.ToolAdminIds.CONTAINER_VOLUMES_FROM);
        imageEditor.ensureDebugId(baseID + Belphegor.ToolAdminIds.TOOL_IMAGE);
    }

    @Inject
    public ToolContainerEditor(ToolAdminView.ToolAdminViewAppearance appearance,
                               ToolDeviceListEditor deviceListEditor,
                               ToolVolumeListEditor containerVolumesEditor,
                               ToolVolumesFromListEditor containerVolumesFromEditor,
                               ToolImageEditor toolImageEditor) {

        this.appearance = appearance;
        this.deviceListEditor = deviceListEditor;
        this.containerVolumesEditor = containerVolumesEditor;
        this.containerVolumesFromEditor = containerVolumesFromEditor;
        this.imageEditor = toolImageEditor;
        initWidget(uiBinder.createAndBindUi(this));

        entryPointWarningHTML.setHTML(appearance.toolEntryPointWarning());
        toolVolumesWarningHTML.setHTML(appearance.toolVolumeWarning());
        containerDevicesLabel.setHTML(appearance.containerDevicesLabel());
        containerVolumesLabel.setHTML(appearance.containerVolumesLabel());
        containerVolumesFromLabel.setHTML(appearance.containerVolumesFromLabel());

        nameEditor.addValueChangeHandler(new EmptyStringValueChangeHandler(nameEditor));
        entryPointEditor.addValueChangeHandler(new EmptyStringValueChangeHandler(entryPointEditor));
        networkModeEditor.addValueChangeHandler(new EmptyStringValueChangeHandler(networkModeEditor));
        workingDirectoryEditor.addValueChangeHandler(new EmptyStringValueChangeHandler(workingDirectoryEditor));

        setUpLabelToolTips();
    }

    void setUpLabelToolTips() {
        new QuickTip(containerDevicesLabel).getToolTipConfig().setDismissDelay(0);
        new QuickTip(containerVolumesLabel).getToolTipConfig().setDismissDelay(0);
        new QuickTip(containerVolumesFromLabel).getToolTipConfig().setDismissDelay(0);
    }

    @UiHandler("addDeviceButton")
    void onAddDeviceButtonClicked(SelectEvent event) {
        deviceListEditor.addDevice();
    }

    @UiHandler("deleteDeviceButton")
    void onDeleteDeviceButtonClicked(SelectEvent event) {
        deviceListEditor.deleteDevice();
    }

    @UiHandler("addVolumesButton")
    void onAddVolumesButtonClicked(SelectEvent event) {
        containerVolumesEditor.addVolume();
    }

    @UiHandler("deleteVolumesButton")
    void onDeleteVolumesButtonClicked(SelectEvent event) {
        containerVolumesEditor.deleteVolume();
    }

    @UiHandler("addVolumesFromButton")
    void onAddVolumesFromButtonClicked(SelectEvent event) {
        containerVolumesFromEditor.addVolumesFrom();
    }

    @UiHandler("deleteVolumesFromButton")
    void onDeleteVolumesFromButtonClicked(SelectEvent event) {
        containerVolumesFromEditor.deleteVolumesFrom();
    }

    public boolean isValid(){
        return imageEditor.isValid() && deviceListEditor.isValid()
               && containerVolumesEditor.isValid() && containerVolumesFromEditor.isValid();
    }
}
