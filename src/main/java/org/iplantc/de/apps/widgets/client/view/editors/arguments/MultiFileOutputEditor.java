package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToStringConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.commons.client.validators.DiskResourceUnixGlobValidator;

import com.sencha.gxt.widget.core.client.form.TextField;

public class MultiFileOutputEditor extends AbstractArgumentEditor {
    private final ArgumentEditorConverter<String> editorAdapter;
    private final TextField textField;

    public MultiFileOutputEditor(AppTemplateWizardAppearance appearance) {
        super(appearance);
        textField = new TextField();
        textField.addValidator(new DiskResourceUnixGlobValidator());
        editorAdapter = new ArgumentEditorConverter<String>(textField, new SplittableToStringConverter());

        argumentLabel.setWidget(editorAdapter);
    }

    @Override
    public void disableValidations() {
        super.disableValidations();

        textField.getValidators().clear();
    }

    @Override
    public ArgumentEditorConverter<?> valueEditor() {
        return editorAdapter;
    }

}
