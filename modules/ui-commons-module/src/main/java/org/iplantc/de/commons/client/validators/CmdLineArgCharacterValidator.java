package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.Collections;
import java.util.List;

public class CmdLineArgCharacterValidator extends AbstractValidator<String> implements
        IPlantDefaultValidator {

    private final String restrictedChars;

    public CmdLineArgCharacterValidator(String restrictedChars) {
        this.restrictedChars = restrictedChars;
    }

    public CmdLineArgCharacterValidator() {
        this(I18N.V_CONSTANTS.restrictedCmdLineArgChars());
    }

    public CmdLineArgCharacterValidator(boolean excludeReturnChar) {
        this(I18N.V_CONSTANTS.restrictedCmdLineArgCharsExclNewline());
    }

    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        // We have an error
        char[] restrictedCharsArr = restrictedChars.toCharArray();
        StringBuilder restrictedFound = new StringBuilder();

        for (char restricted : restrictedCharsArr) {
            for (char next : value.toCharArray()) {
                if (next == restricted) {
                    restrictedFound.append(restricted);
                    break;
                }
            }
        }

        if (restrictedFound.length() > 0) {
            String errorMsg = I18N.VALIDATION.unsupportedChars(restrictedChars) + " " //$NON-NLS-1$
                    + I18N.VALIDATION.invalidChars(restrictedFound.toString());
            return createError(new DefaultEditorError(editor, errorMsg, value));
        }

        return Collections.emptyList();
    }

}
