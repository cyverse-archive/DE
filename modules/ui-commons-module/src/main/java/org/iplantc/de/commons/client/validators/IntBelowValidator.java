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
public class IntBelowValidator extends AbstractValidator<Integer> {

    private final Integer maxNumber;
    private final IplantValidationMessages validationMessages;

    public IntBelowValidator(Integer maxNumber) {
        this(maxNumber, GWT.<IplantValidationMessages> create(IplantValidationMessages.class));
    }

    IntBelowValidator(final Integer maxNumber,
                      final IplantValidationMessages validationMessages) {
        this.maxNumber = maxNumber;
        this.validationMessages = validationMessages;
    }

    @Override
    public List<EditorError> validate(Editor<Integer> editor, Integer value) {
        if (value != null && (value >= maxNumber)) {
            return createError(editor, validationMessages.notBelowValueMsg("", maxNumber), value);
        }

        return null;
    }

    public Integer getMaxNumber() {
        return maxNumber;
    }
}
