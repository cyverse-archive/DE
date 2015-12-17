package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.client.models.tool.ToolContainer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.IntegerField;
import com.sencha.gxt.widget.core.client.form.TextField;

public class ToolContainerEditor extends Composite implements Editor<ToolContainer> {

    interface ToolContainerEditorBinder extends UiBinder<Widget, ToolContainerEditor> {
    }

    private static ToolContainerEditorBinder uiBinder = GWT.create(ToolContainerEditorBinder.class);

    @UiField TextField nameEditor;
    @UiField TextField workingDirectoryEditor;
    @UiField TextField entryPointEditor;
    @UiField IntegerField memoryLimitEditor;
    @UiField IntegerField cpuSharesEditor;
    @UiField TextField networkModeEditor;
    @Ignore
    @UiField TextButton addDeviceButton;
    @Ignore
    @UiField TextButton deleteDeviceButton;
    @UiField (provided = true) ToolDeviceListEditor deviceListEditor;
    @Ignore
    @UiField TextButton addVolumesButton;
    @Ignore
    @UiField TextButton deleteVolumesButton;
    @UiField (provided = true) ToolVolumeListEditor containerVolumesEditor;
    @Ignore
    @UiField TextButton addVolumesFromButton;
    @Ignore
    @UiField TextButton deleteVolumesFromButton;
    @UiField (provided = true) ToolVolumesFromListEditor containerVolumesFromEditor;
    @UiField (provided = true) ToolImageEditor imageEditor;
    @Ignore
    @UiField FieldSet containerFieldSet;
    @UiField (provided = true) ToolAdminView.ToolAdminViewAppearance appearance;

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
        return imageEditor.isValid() && containerVolumesFromEditor.isValid();
    }
}
