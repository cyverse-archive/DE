package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.shared.Belphegor;
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
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.tips.QuickTip;


public class ToolTestDataEditor extends Composite implements Editor<ToolTestData> {

    interface ToolTestDataEditorBinder extends UiBinder<Widget, ToolTestDataEditor> {
    }

    private static ToolTestDataEditorBinder uiBinder = GWT.create(ToolTestDataEditorBinder.class);

    @Ignore
    @UiField FieldLabel inputFilesLabel;
    @Ignore
    @UiField TextButton addInputFileButton;
    @Ignore
    @UiField TextButton deleteInputFileButton;
    @UiField (provided = true) ToolTestDataInputFilesListEditor inputFilesEditor;
    @Ignore
    @UiField FieldLabel outputFilesLabel;
    @Ignore
    @UiField TextButton addOutputFileButton;
    @Ignore
    @UiField TextButton deleteOutputFileButton;
    @UiField (provided = true) ToolTestDataOutputFilesListEditor outputFilesEditor;
    @UiField (provided = true) ToolAdminView.ToolAdminViewAppearance appearance;

    @Inject
    public ToolTestDataEditor(ToolTestDataInputFilesListEditor inputFilesEditor,
                              ToolTestDataOutputFilesListEditor outputFilesEditor,
                              ToolAdminView.ToolAdminViewAppearance appearance) {
        this.inputFilesEditor = inputFilesEditor;
        this.outputFilesEditor = outputFilesEditor;
        this.appearance = appearance;
        initWidget(uiBinder.createAndBindUi(this));

        inputFilesLabel.setHTML(appearance.toolTestDataInputFilesLabel());
        outputFilesLabel.setHTML(appearance.toolTestDataOutputFilesLabel());

        setUpLabelToolTips();
    }

    void setUpLabelToolTips() {
        new QuickTip(inputFilesLabel).getToolTipConfig().setDismissDelay(0);
        new QuickTip(outputFilesLabel).getToolTipConfig().setDismissDelay(0);
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

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        inputFilesLabel.ensureDebugId(baseID + Belphegor.ToolAdminIds.INPUT_FILES_LABEL);
        addInputFileButton.ensureDebugId(baseID + Belphegor.ToolAdminIds.ADD_INPUT);
        deleteInputFileButton.ensureDebugId(baseID + Belphegor.ToolAdminIds.DELETE_INPUT);
        outputFilesLabel.ensureDebugId(baseID + Belphegor.ToolAdminIds.OUTPUT_FILES_LABEL);
        addOutputFileButton.ensureDebugId(baseID + Belphegor.ToolAdminIds.ADD_OUTPUT);
        deleteOutputFileButton.ensureDebugId(baseID + Belphegor.ToolAdminIds.DELETE_OUTPUT);

        inputFilesEditor.ensureDebugId(baseID + Belphegor.ToolAdminIds.INPUT_FILES);
        outputFilesEditor.ensureDebugId(baseID + Belphegor.ToolAdminIds.OUTPUT_FILES);
    }
}
