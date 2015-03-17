package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;

import com.google.gwt.editor.client.LeafValueEditor;

public class VisibilityEditor implements LeafValueEditor<Boolean> {

    private final AppTemplateForm.ArgumentEditor argEditor;
    private Boolean value;

    public VisibilityEditor(AppTemplateForm.ArgumentEditor argEditor) {
        this.argEditor = argEditor;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        if (value == null) {
            return;
        }
        this.value = value.booleanValue();
        if (argEditor.isDisabledOnNotVisible()) {
            argEditor.setVisible(true);
            if (!argEditor.isLabelOnlyEditMode()) {
                argEditor.setEnabled(value);
            }
        } else {
            argEditor.setVisible(value);
        }
    }

}