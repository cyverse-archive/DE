package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToIntegerConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.commons.client.widgets.IPlantSideErrorHandler;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.IntegerInputLabels;

import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.form.SpinnerField;

public class IntegerInputEditor extends AbstractArgumentEditor {
    private final ArgumentEditorConverter<Integer> editorAdapter;
    private final SpinnerField<Integer> spinnerField;

    public IntegerInputEditor(AppTemplateWizardAppearance appearance, IntegerInputLabels labels) {
        super(appearance);
        spinnerField = new SpinnerField<Integer>(new NumberPropertyEditor.IntegerPropertyEditor());
        spinnerField.setErrorSupport(new IPlantSideErrorHandler(spinnerField));
        spinnerField.setMinValue(Integer.MIN_VALUE);
        spinnerField.setEmptyText(labels.integerInputWidgetEmptyText());
        editorAdapter = new ArgumentEditorConverter<Integer>(spinnerField, new SplittableToIntegerConverter());

        argumentLabel.setWidget(editorAdapter);
    }

    @Override
    public ArgumentEditorConverter<?> valueEditor() {
        return editorAdapter;
    }

}
