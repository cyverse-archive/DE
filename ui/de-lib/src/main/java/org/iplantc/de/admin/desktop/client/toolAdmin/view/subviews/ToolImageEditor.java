package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolImage;
import org.iplantc.de.commons.client.validators.UrlValidator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;


public class ToolImageEditor extends Composite implements Editor<ToolImage> {

    private static ToolImageEditorBinder uiBinder = GWT.create(ToolImageEditorBinder.class);

    interface EditorDriver extends SimpleBeanEditorDriver<ToolImage, ToolImageEditor> {}

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    interface ToolImageEditorBinder extends UiBinder<Widget, ToolImageEditor> {
    }

    private ToolAutoBeanFactory factory;
    @Ignore
    @UiField
    FieldLabel nameLabel;
    @UiField TextField nameEditor;
    @UiField TextField tagEditor;
    @UiField TextField urlEditor;
    @UiField (provided = true)
    ToolAdminView.ToolAdminViewAppearance appearance = GWT.create(ToolAdminView.ToolAdminViewAppearance.class);


    public ToolImageEditor() {
        factory = GWT.create(ToolAutoBeanFactory.class);
        ToolImage toolImage = factory.getImage().as();
        initWidget(uiBinder.createAndBindUi(this));

        nameLabel.setHTML(appearance.containerImageNameLabel());
        urlEditor.addValidator(new UrlValidator());

        editorDriver.initialize(this);
        editorDriver.edit(toolImage);
    }

    public ToolImage getToolImage() {
        return editorDriver.flush();
    }

    public boolean isValid(){
        return nameEditor.isValid() && urlEditor.isValid();
    }

}
