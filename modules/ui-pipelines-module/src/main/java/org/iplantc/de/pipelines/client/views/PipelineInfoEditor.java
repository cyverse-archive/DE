package org.iplantc.de.pipelines.client.views;

import org.iplantc.de.client.models.pipelines.Pipeline;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.widgets.PreventEntryAfterLimitHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.MaxLengthValidator;

/**
 * An Editor for the Pipeline name and description fields.
 * 
 * @author psarando
 * 
 */
public class PipelineInfoEditor implements IsWidget, Editor<Pipeline> {

    private static PipelineInfoEditorUiBinder uiBinder = GWT.create(PipelineInfoEditorUiBinder.class);
    private final Widget widget;

    interface PipelineInfoEditorUiBinder extends UiBinder<Widget, PipelineInfoEditor> {
    }

    @UiField
    TextField name;

    @UiField
    TextArea description;

    public PipelineInfoEditor() {
        widget = uiBinder.createAndBindUi(this);
        name.addKeyDownHandler(new PreventEntryAfterLimitHandler(name));
        name.addValidator(new MaxLengthValidator(PreventEntryAfterLimitHandler.DEFAULT_LIMIT));
        name.addValidator(new DiskResourceNameValidator());
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    public void clearInvalid() {
        name.clearInvalid();
        description.clearInvalid();
    }
}
