package org.iplantc.de.admin.desktop.client.toolRequest.view;

import org.iplantc.de.client.models.toolRequest.ToolRequestDetails;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.CheckBox;
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
    @UiField CheckBox multiThreadedEditor;
    @UiField TextField phoneEditor;
    @UiField TextField sourceUrlEditor;
    @UiField TextField submittedByEditor;
    @UiField TextField testDataPathEditor;
    @UiField TextField versionEditor;


    public ToolRequestDetailsPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        editorDriver.initialize(this);
    }

    public void edit(ToolRequestDetails details) {
        editorDriver.edit(details);
        con.forceLayout();
    }

}
