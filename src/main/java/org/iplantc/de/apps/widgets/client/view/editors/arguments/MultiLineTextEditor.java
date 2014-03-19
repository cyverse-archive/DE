package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToStringConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.commons.client.validators.CmdLineArgCharacterValidator;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.TextInputLabels;

import com.sencha.gxt.widget.core.client.form.TextArea;

public class MultiLineTextEditor extends AbstractArgumentEditor {
    private final ArgumentEditorConverter<String> editorAdapter;
    private final TextArea textArea;

    public MultiLineTextEditor(AppTemplateWizardAppearance appearance, TextInputLabels labels) {
        super(appearance);
        textArea = new TextArea();
        textArea.setEmptyText(labels.textInputWidgetEmptyText());
        textArea.addValidator(new CmdLineArgCharacterValidator(true));
        editorAdapter = new ArgumentEditorConverter<String>(textArea, new SplittableToStringConverter());

        argumentLabel.setWidget(editorAdapter);
    }

    @Override
    public ArgumentEditorConverter<?> valueEditor() {
        return editorAdapter;
    }

    @Override
    public void disableValidations() {
        super.disableValidations();

        textArea.getValidators().clear();
    }
}
