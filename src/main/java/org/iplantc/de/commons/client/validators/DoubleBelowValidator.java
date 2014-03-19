package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.List;

public class DoubleBelowValidator extends AbstractValidator<Double> {

    private final Double maxNumber;

    public DoubleBelowValidator(Double maxNumber) {
        this.maxNumber = maxNumber;
    }

    @Override
    public List<EditorError> validate(Editor<Double> editor, Double value) {
        if (value != null && (value >= maxNumber)) {
            return createError(editor, I18N.VALIDATION.notBelowValueMsg("", maxNumber), value);
        }

        return null;
    }

    public Double getMaxNumber() {
        return maxNumber;
    }
}
