package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.List;

public class DoubleAboveValidator extends AbstractValidator<Double> {

    private final Double minNumber;

    public DoubleAboveValidator(Double minNumber) {
        this.minNumber = minNumber;
    }

    @Override
    public List<EditorError> validate(Editor<Double> editor, Double value) {
        if (value != null && (value <= minNumber)) {
            return createError(editor, I18N.VALIDATION.notAboveValueMsg("", minNumber), value);
        }

        return null;
    }

    public Double getMinNumber() {
        return minNumber;
    }
}
