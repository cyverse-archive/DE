package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolTestData;

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


public class ToolTestDataEditor extends Composite implements Editor<ToolTestData> {

    interface ToolTestDataEditorBinder extends UiBinder<Widget, ToolTestDataEditor> {
    }

    interface EditorDriver extends SimpleBeanEditorDriver<ToolTestData, ToolTestDataEditor> {
    }

    private static ToolTestDataEditorBinder uiBinder = GWT.create(ToolTestDataEditorBinder.class);

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    @Ignore
    @UiField TextButton addInputFileButton;
    @Ignore
    @UiField TextButton deleteInputFileButton;
    @UiField ToolTestDataInputFilesListEditor inputFilesEditor;
    @Ignore
    @UiField TextButton addOutputFileButton;
    @Ignore
    @UiField TextButton deleteOutputFileButton;
    @UiField ToolTestDataOutputFilesListEditor outputFilesEditor;

    public ToolTestDataEditor() {
        ToolAutoBeanFactory factory = GWT.create(ToolAutoBeanFactory.class);
        ToolTestData toolTestData = factory.getTest().as();
        initWidget(uiBinder.createAndBindUi(this));
        editorDriver.initialize(this);
        editorDriver.edit(toolTestData);
    }

    public ToolTestData getToolTestData() {
        ToolTestData toolTestData = editorDriver.flush();
        toolTestData.setInputFiles(inputFilesEditor.getTestDataInputFilesList());
        toolTestData.setOutputFiles(outputFilesEditor.getTestDataOutputFilesList());
        return toolTestData;
    }

    @UiHandler("addInputFileButton")
    void onAddInputFileButtonClicked(SelectEvent event) {
        inputFilesEditor.addToolTestDataInputFile();
    }

    @UiHandler("deleteInputFileButton")
    void onDeleteInputFileButtonClicked(SelectEvent event) {
        inputFilesEditor.deleteToolTestDataInputFile();
    }

    @UiHandler("addOutputFileButton")
    void onAddOutputFileButtonClicked(SelectEvent event) {
        outputFilesEditor.addToolTestDataOutputFile();
    }

    @UiHandler("deleteOutputFileButton")
    void onDeleteOutputFileButton(SelectEvent event) {
        outputFilesEditor.deleteToolTestDataOutputFile();
    }


}
