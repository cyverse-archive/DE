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
public class IntAboveValidator extends AbstractValidator<Integer> {

    private final Integer minNumber;
    private final IplantValidationMessages validation;

    public IntAboveValidator(Integer minNumber) {
        this(minNumber, GWT.<IplantValidationMessages> create(IplantValidationMessages.class));
    }

    IntAboveValidator(final Integer minNumber,
                      final IplantValidationMessages validationMessages) {
        this.minNumber = minNumber;
        this.validation = validationMessages;
    }

    @Override
    public List<EditorError> validate(Editor<Integer> editor, Integer value) {
        if (value != null && (value <= minNumber)) {
            return createError(editor, validation.notAboveValueMsg("", minNumber), value);
        }

        return null;
    }

    public Integer getMinNumber() {
        return minNumber;
    }
}
