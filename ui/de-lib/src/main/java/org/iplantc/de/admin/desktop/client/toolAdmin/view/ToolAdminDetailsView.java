package org.iplantc.de.admin.desktop.client.toolAdmin.view;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolContainerEditor;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolImplementationEditor;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author aramsey
 */
public class ToolAdminDetailsView extends Composite implements Editor<Tool> {

    interface EditorDriver
            extends SimpleBeanEditorDriver<Tool, ToolAdminDetailsView> {
    }

    interface ToolAdminDetailsWindowUiBinder extends UiBinder<Widget, ToolAdminDetailsView> {

    }

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);
    private static ToolAdminDetailsWindowUiBinder uiBinder =
            GWT.create(ToolAdminDetailsWindowUiBinder.class);

    @UiField VerticalLayoutContainer layoutContainer;
    @Ignore
    @UiField FieldLabel nameLabel, typeLabel, locationLabel;
    @UiField TextArea descriptionEditor;
    @UiField TextField nameEditor;
    @UiField TextField typeEditor;
    @UiField TextField attributionEditor;
    @UiField TextField versionEditor;
    @UiField TextField locationEditor;
    @UiField (provided = true) ToolImplementationEditor implementationEditor;
    @UiField (provided = true) ToolContainerEditor containerEditor;
    @UiField (provided = true) ToolAdminView.ToolAdminViewAppearance appearance;

    @Inject
    private ToolAdminDetailsView(ToolAdminView.ToolAdminViewAppearance appearance,
                                 ToolImplementationEditor implementationEditor,
                                 ToolContainerEditor containerEditor) {
        this.appearance = appearance;
        this.implementationEditor = implementationEditor;
        this.containerEditor = containerEditor;

        initWidget(uiBinder.createAndBindUi(this));

        nameLabel.setHTML(appearance.toolImportNameLabel());
        typeLabel.setHTML(appearance.toolImportTypeLabel());
        locationLabel.setHTML(appearance.toolImportLocationLabel());

        descriptionEditor.setHeight(250);

        editorDriver.initialize(this);
    }

    public void edit(Tool tool) {
        editorDriver.edit(tool);
    }

    public Tool getTool() {
        return editorDriver.flush();
    }

    public boolean isValid() {
        return containerEditor.isValid() && implementationEditor.isValid() && nameEditor.isValid()
               && typeEditor.isValid() && locationEditor.isValid();
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        nameLabel.ensureDebugId(baseID + Belphegor.ToolAdminIds.TOOL_NAME_LABEL);
        typeLabel.ensureDebugId(baseID + Belphegor.ToolAdminIds.TOOL_TYPE_LABEL);
        locationLabel.ensureDebugId(baseID + Belphegor.ToolAdminIds.TOOL_LOCATION_LABEL);
        descriptionEditor.setId(baseID + Belphegor.ToolAdminIds.TOOL_DESCRIPTION);
        nameEditor.setId(baseID + Belphegor.ToolAdminIds.TOOL_NAME);
        typeEditor.setId(baseID + Belphegor.ToolAdminIds.TOOL_TYPE);
        attributionEditor.setId(baseID + Belphegor.ToolAdminIds.TOOL_ATTRIBUTION);
        versionEditor.setId(baseID + Belphegor.ToolAdminIds.TOOL_VERSION);
        locationEditor.setId(baseID + Belphegor.ToolAdminIds.TOOL_LOCATION);
        implementationEditor.ensureDebugId(baseID + Belphegor.ToolAdminIds.TOOL_IMPLEMENTATION);
        containerEditor.ensureDebugId(baseID + Belphegor.ToolAdminIds.TOOL_CONTAINER);
    }
}
