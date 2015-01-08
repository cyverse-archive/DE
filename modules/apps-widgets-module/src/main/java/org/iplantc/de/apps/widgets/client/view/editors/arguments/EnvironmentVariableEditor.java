package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToStringConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.commons.client.validators.CmdLineArgCharacterValidator;
import org.iplantc.de.resources.client.constants.IplantValidationConstants;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.EnvironmentVariableLabels;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author jstroot
 */
public class EnvironmentVariableEditor extends AbstractArgumentEditor {
    private final ArgumentEditorConverter<String> editorAdapter;
    private final TextField textField;
    IplantValidationConstants validationConstants = GWT.create(IplantValidationConstants.class);

    public EnvironmentVariableEditor(AppTemplateWizardAppearance appearance, EnvironmentVariableLabels labels) {
        super(appearance);
        textField = new TextField();
        textField.setEmptyText(labels.envVarWidgetEmptyText());
        textField.addValidator(new CmdLineArgCharacterValidator(validationConstants.restrictedCmdLineChars()));
        editorAdapter = new ArgumentEditorConverter<>(textField, new SplittableToStringConverter());

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
