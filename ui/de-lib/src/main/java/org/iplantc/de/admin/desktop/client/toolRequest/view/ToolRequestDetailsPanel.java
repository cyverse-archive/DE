package org.iplantc.de.admin.desktop.client.toolRequest.view;

import org.iplantc.de.admin.desktop.client.toolRequest.ToolRequestView;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.apps.widgets.client.view.editors.widgets.CheckBoxAdapter;
import org.iplantc.de.client.models.toolRequest.ToolRequestDetails;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author jstroot
 */
public class ToolRequestDetailsPanel extends Composite implements Editor<ToolRequestDetails> {

    interface EditorDriver extends SimpleBeanEditorDriver<ToolRequestDetails, ToolRequestDetailsPanel> {}
    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);
    private static ToolRequestDetailsPanelUiBinder uiBinder = GWT.create(ToolRequestDetailsPanelUiBinder.class);

    interface ToolRequestDetailsPanelUiBinder extends UiBinder<Widget, ToolRequestDetailsPanel> {
    }

    @UiField VerticalLayoutContainer con;

    @UiField TextField additionalDataFileEditor;
    @UiField TextField additionalInfoEditor;
    @UiField TextField architectureEditor;
    @UiField TextField attributionEditor;
    @UiField TextField cmdLineDescriptionEditor;
    @UiField TextField documentationUrlEditor;
    @UiField CheckBoxAdapter multiThreadedEditor;
    @UiField TextField phoneEditor;
    @UiField TextField sourceUrlEditor;
    @UiField TextField submittedByEditor;
    @UiField TextField testDataPathEditor;
    @UiField TextField versionEditor;
    @UiField ToolRequestView.ToolRequestViewAppearance appearance = GWT.create(ToolRequestView.ToolRequestViewAppearance.class);


    public ToolRequestDetailsPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        multiThreadedEditor.setText(appearance.multiThreadedLabel());
        editorDriver.initialize(this);
    }

    public void edit(ToolRequestDetails details) {
        editorDriver.edit(details);
        con.forceLayout();
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        additionalDataFileEditor.setId(baseID + Belphegor.ToolRequestIds.ADDITIONAL_DATA_FILE);
        additionalInfoEditor.setId(baseID + Belphegor.ToolRequestIds.ADDITIONAL_INFO);
        architectureEditor.setId(baseID + Belphegor.ToolRequestIds.ARCHITECTURE);
        attributionEditor.setId(baseID + Belphegor.ToolRequestIds.ATTRIBUTION);
        cmdLineDescriptionEditor.setId(baseID + Belphegor.ToolRequestIds.CMD_LINE);
        documentationUrlEditor.setId(baseID + Belphegor.ToolRequestIds.DOC_URL);
        multiThreadedEditor.getCheckBox().ensureDebugId(baseID + Belphegor.ToolRequestIds.MULTI_THREAD);
        phoneEditor.setId(baseID + Belphegor.ToolRequestIds.PHONE);
        sourceUrlEditor.setId(baseID + Belphegor.ToolRequestIds.SOURCE_URL);
        submittedByEditor.setId(baseID + Belphegor.ToolRequestIds.SUBMITTED_BY);
        testDataPathEditor.setId(baseID + Belphegor.ToolRequestIds.TEST_DATA);
        versionEditor.setId(baseID + Belphegor.ToolRequestIds.VERSION);
    }
}
