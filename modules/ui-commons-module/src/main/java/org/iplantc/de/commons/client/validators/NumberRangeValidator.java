package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.messages.IplantValidationMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.List;

/**
 * @author jstroot
 * @param <N> The number type
 */
public class NumberRangeValidator<N extends Number> extends AbstractValidator<N> {

    protected N minNumber;
    protected N maxNumber;
    private final IplantValidationMessages validationMessages;


    public NumberRangeValidator(final N minNumber, N maxNumber) {
        this(minNumber, maxNumber, GWT.<IplantValidationMessages> create(IplantValidationMessages.class));
    }

    NumberRangeValidator(final N minNumber,
                         final N maxNumber,
                         final IplantValidationMessages validationMessages) {
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
        this.validationMessages = validationMessages;
    }


    public N getMinNumber() {
        return minNumber;
    }

    public N getMaxNumber() {
        return maxNumber;
    }

    @Override
    public List<EditorError> validate(Editor<N> field, N value) {
        if (value != null 
                && ((value.doubleValue() < minNumber.doubleValue())
                || (value.doubleValue() > maxNumber.doubleValue()))) {
            return createError(field, validationMessages.notWithinRangeMsg("", minNumber, maxNumber), value);
        }
        return null;
    }
}