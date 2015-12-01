package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolImplementation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;


public class ToolImplementationEditor extends Composite implements Editor<ToolImplementation> {

    interface ToolImplementationEditorBinder extends UiBinder<Widget, ToolImplementationEditor> {
    }

    interface EditorDriver extends SimpleBeanEditorDriver<ToolImplementation, ToolImplementationEditor> {
    }

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);
    private static ToolImplementationEditorBinder uiBinder =
            GWT.create(ToolImplementationEditorBinder.class);

    @Ignore
    @UiField FieldLabel implementorLabel, implementorEmailLabel;
    @UiField TextField implementorEditor;
    @UiField TextField implementorEmailEditor;
    @UiField ToolTestDataEditor testEditor;
    @UiField (provided = true)
    ToolAdminView.ToolAdminViewAppearance appearance = GWT.create(ToolAdminView.ToolAdminViewAppearance.class);


    public ToolImplementationEditor() {
        ToolAutoBeanFactory factory = GWT.create(ToolAutoBeanFactory.class);
        ToolImplementation implementation = factory.getImplementation().as();
        initWidget(uiBinder.createAndBindUi(this));

        implementorLabel.setHTML(appearance.toolImplementationImplementorLabel());
        implementorEmailLabel.setHTML(appearance.toolImplementationImplementorEmailLabel());

        editorDriver.initialize(this);
        editorDriver.edit(implementation);
    }

    public ToolImplementation getToolImplementation() {
        ToolImplementation toolImplementation = editorDriver.flush();
        toolImplementation.setTest(testEditor.getToolTestData());
        return toolImplementation;
    }

    public boolean isValid(){
        return implementorEditor.isValid() && implementorEmailEditor.isValid();
    }

}
