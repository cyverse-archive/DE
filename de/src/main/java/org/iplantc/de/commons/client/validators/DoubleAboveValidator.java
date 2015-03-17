package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.messages.IplantValidationMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.List;

/**
 * @author jstroot
 */
public class DoubleAboveValidator extends AbstractValidator<Double> {

    private final Double minNumber;
    private final IplantValidationMessages validationMessages;

    public DoubleAboveValidator(final Double minNumber) {
        this(minNumber, GWT.<IplantValidationMessages> create(IplantValidationMessages.class));
    }

    DoubleAboveValidator(final Double minNumber,
                         final IplantValidationMessages validationMessages) {
        this.minNumber = minNumber;
        this.validationMessages = validationMessages;
    }

    @Override
    public List<EditorError> validate(Editor<Double> editor, Double value) {
        if (value != null && (value <= minNumber)) {
            return createError(editor, validationMessages.notAboveValueMsg("", minNumber), value);
        }

        return null;
    }

    public Double getMinNumber() {
        return minNumber;
    }
}
