package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolContainer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.IntegerField;
import com.sencha.gxt.widget.core.client.form.TextField;

public class ToolContainerEditor extends Composite implements Editor<ToolContainer> {


    interface EditorDriver extends SimpleBeanEditorDriver<ToolContainer, ToolContainerEditor> {
    }

    interface ToolContainerEditorBinder extends UiBinder<Widget, ToolContainerEditor> {
    }

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);
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
    @UiField ToolDeviceListEditor deviceListEditor;
    @Ignore
    @UiField TextButton addVolumesButton;
    @Ignore
    @UiField TextButton deleteVolumesButton;
    @UiField ToolVolumeListEditor containerVolumesEditor;
    @Ignore
    @UiField TextButton addVolumesFromButton;
    @Ignore
    @UiField TextButton deleteVolumesFromButton;
    @UiField ToolVolumesFromListEditor containerVolumesFromEditor;
    @UiField ToolImageEditor imageEditor;
    @Ignore
    @UiField FieldSet containerFieldSet;
    @UiField (provided = true)
    ToolAdminView.ToolAdminViewAppearance appearance = GWT.create(ToolAdminView.ToolAdminViewAppearance.class);


    public ToolContainerEditor() {
        ToolAutoBeanFactory factory = GWT.create(ToolAutoBeanFactory.class);
        ToolContainer container = factory.getContainer().as();

        container.setMemoryLimit(appearance.containerMemoryLimitDefaultValue());
        container.setCpuShares(appearance.containerCPUSharesDefaultValue());

        initWidget(uiBinder.createAndBindUi(this));

        editorDriver.initialize(this);
        editorDriver.edit(container);
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

    public ToolContainer getToolContainer() {
        ToolContainer container = editorDriver.flush();
        container.setDeviceList(deviceListEditor.getDeviceList());
        container.setContainerVolumes(containerVolumesEditor.getVolumeList());
        container.setContainerVolumesFrom(containerVolumesFromEditor.getVolumesFromList());
        container.setImage(imageEditor.getToolImage());
        return container;
    }

    public boolean isValid(){
        return imageEditor.isValid() && containerVolumesFromEditor.isValid();
    }
}
