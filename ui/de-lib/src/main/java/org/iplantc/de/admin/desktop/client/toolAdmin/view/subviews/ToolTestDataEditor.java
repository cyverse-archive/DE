package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.client.models.tool.ToolTestData;

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


public class ToolTestDataEditor extends Composite implements Editor<ToolTestData> {

    interface ToolTestDataEditorBinder extends UiBinder<Widget, ToolTestDataEditor> {
    }

    private static ToolTestDataEditorBinder uiBinder = GWT.create(ToolTestDataEditorBinder.class);

    @Ignore
    @UiField TextButton addInputFileButton;
    @Ignore
    @UiField TextButton deleteInputFileButton;
    @UiField (provided = true) ToolTestDataInputFilesListEditor inputFilesEditor;
    @Ignore
    @UiField TextButton addOutputFileButton;
    @Ignore
    @UiField TextButton deleteOutputFileButton;
    @UiField (provided = true) ToolTestDataOutputFilesListEditor outputFilesEditor;

    @Inject
    public ToolTestDataEditor(ToolTestDataInputFilesListEditor inputFilesEditor,
                              ToolTestDataOutputFilesListEditor outputFilesEditor) {
        this.inputFilesEditor = inputFilesEditor;
        this.outputFilesEditor = outputFilesEditor;
        initWidget(uiBinder.createAndBindUi(this));
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
