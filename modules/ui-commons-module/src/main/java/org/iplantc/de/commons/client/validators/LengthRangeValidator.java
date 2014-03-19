package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.List;

/**
 * validates that the length of some string value is within a given range.
 */
public final class LengthRangeValidator extends AbstractValidator<String> {

    private final String field;
    private final int minLength;
    private final int maxLength;

    /**
     * the constructor
     * 
     * The actual values of the minimum and maximum lengths will be set so that 0 <= min <= max.
     * 
     * @param field the field whose value will be checked
     * @param minLength the minimum allowed length
     * @param maxLength the maximum allowed length
     */
    public LengthRangeValidator(final String field, final int minLength, final int maxLength) {
        this.field = field;
        this.minLength = Math.max(0, Math.min(minLength, maxLength));
        this.maxLength = Math.max(0, Math.max(minLength, maxLength));
    }

    /**
     * @see AbstractValidtor<T>#validate(Editor, T)
     */
    @Override
    public List<EditorError> validate(final Editor<String> editor, final String value) {
        final String testVal = (value == null) ? "" : value;

        if (testVal.length() < minLength || testVal.length() > maxLength) {
            final String msg = I18N.VALIDATION.lengthViolationMsg(field, minLength, maxLength);
            return createError(new DefaultEditorError(editor, msg, testVal));
        }

        return null;
    }

}
