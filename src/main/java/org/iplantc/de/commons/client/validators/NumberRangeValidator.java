package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.List;

public class NumberRangeValidator<N extends Number> extends AbstractValidator<N> {

    protected N minNumber;
    protected N maxNumber;

    public NumberRangeValidator(N minNumber, N maxNumber) {
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
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
            return createError(field, I18N.VALIDATION.notWithinRangeMsg("", minNumber, maxNumber), value);
        }
        return null;
    }
}