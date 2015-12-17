package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.client.models.tool.ToolImage;
import org.iplantc.de.commons.client.validators.UrlValidator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

public class ToolImageEditor extends Composite implements Editor<ToolImage> {

    private static ToolImageEditorBinder uiBinder = GWT.create(ToolImageEditorBinder.class);

    interface ToolImageEditorBinder extends UiBinder<Widget, ToolImageEditor> {
    }

    @Ignore
    @UiField
    FieldLabel nameLabel;
    @UiField TextField nameEditor;
    @UiField TextField tagEditor;
    @UiField TextField urlEditor;
    @UiField (provided = true)
    ToolAdminView.ToolAdminViewAppearance appearance;

    @Inject
    public ToolImageEditor(ToolAdminView.ToolAdminViewAppearance appearance) {
        this.appearance = appearance;

        initWidget(uiBinder.createAndBindUi(this));

        nameLabel.setHTML(appearance.containerImageNameLabel());
        urlEditor.addValidator(new UrlValidator());
    }

    public boolean isValid(){
        return nameEditor.isValid() && urlEditor.isValid();
    }

}
