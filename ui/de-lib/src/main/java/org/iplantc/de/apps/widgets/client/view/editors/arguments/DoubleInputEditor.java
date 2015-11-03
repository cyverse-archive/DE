package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToDoubleConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.commons.client.widgets.IPlantSideErrorHandler;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.DoubleInputLabels;

import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.form.SpinnerField;

public class DoubleInputEditor extends AbstractArgumentEditor {
    private final ArgumentEditorConverter<Double> editorAdapter;
    private final SpinnerField<Double> spinnerField;

    public DoubleInputEditor(AppTemplateWizardAppearance appearance, DoubleInputLabels labels) {
        super(appearance);
        spinnerField = new SpinnerField<Double>(new NumberPropertyEditor.DoublePropertyEditor());
        spinnerField.setErrorSupport(new IPlantSideErrorHandler(spinnerField));
        spinnerField.setMinValue(-Double.MAX_VALUE);
        spinnerField.setEmptyText(labels.doubleInputWidgetEmptyText());
        editorAdapter = new ArgumentEditorConverter<Double>(spinnerField, new SplittableToDoubleConverter());

        argumentLabel.setWidget(editorAdapter);
    }

    @Override
    public ArgumentEditorConverter<?> valueEditor() {
        return editorAdapter;
    }

}
