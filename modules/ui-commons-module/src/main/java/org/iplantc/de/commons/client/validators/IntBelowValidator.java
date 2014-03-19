package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.List;

public class IntBelowValidator extends AbstractValidator<Integer> {

    private final Integer maxNumber;

    public IntBelowValidator(Integer maxNumber) {
        this.maxNumber = maxNumber;
    }

    @Override
    public List<EditorError> validate(Editor<Integer> editor, Integer value) {
        if (value != null && (value >= maxNumber)) {
            return createError(editor, I18N.VALIDATION.notBelowValueMsg("", maxNumber), value);
        }

        return null;
    }

    public Integer getMaxNumber() {
        return maxNumber;
    }
}
