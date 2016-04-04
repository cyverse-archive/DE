package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.tool.ToolImage;
import org.iplantc.de.commons.client.validators.UrlValidator;
import org.iplantc.de.commons.client.widgets.EmptyStringValueChangeHandler;

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

        tagEditor.addValueChangeHandler(new EmptyStringValueChangeHandler(tagEditor));
        urlEditor.addValueChangeHandler(new EmptyStringValueChangeHandler(urlEditor));
    }

    public boolean isValid(){
        return nameEditor.isValid() && urlEditor.isValid();
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        nameLabel.ensureDebugId(baseID + Belphegor.ToolAdminIds.IMAGE_NAME_LABEL);
        nameEditor.setId(baseID + Belphegor.ToolAdminIds.IMAGE_NAME);
        tagEditor.setId(baseID + Belphegor.ToolAdminIds.IMAGE_TAG);
        urlEditor.setId(baseID + Belphegor.ToolAdminIds.IMAGE_URL);
    }
}
