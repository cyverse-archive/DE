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
public class DoubleBelowValidator extends AbstractValidator<Double> {

    private final Double maxNumber;
    private final IplantValidationMessages validationMessages;

    public DoubleBelowValidator(Double maxNumber) {
        this(maxNumber, GWT.<IplantValidationMessages> create(IplantValidationMessages.class));
    }

    DoubleBelowValidator(final Double maxNumber,
                         final IplantValidationMessages validationMessages) {
        this.maxNumber = maxNumber;
        this.validationMessages = validationMessages;
    }

    @Override
    public List<EditorError> validate(Editor<Double> editor, Double value) {
        if (value != null && (value >= maxNumber)) {
            return createError(editor, validationMessages.notBelowValueMsg("", maxNumber), value);
        }

        return null;
    }

    public Double getMaxNumber() {
        return maxNumber;
    }
}
