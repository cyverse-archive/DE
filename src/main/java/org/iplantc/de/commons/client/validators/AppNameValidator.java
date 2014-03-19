package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.Collections;
import java.util.List;

public class AppNameValidator extends AbstractValidator<String> {

    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        
        char[] restrictedChars = (I18N.V_CONSTANTS.restrictedAppNameChars()).toCharArray();
        StringBuilder restrictedFound = new StringBuilder();

        // check for spaces at the beginning and at the end of the file name
        if (value.startsWith(" ") || value.endsWith(" ")) { //$NON-NLS-1$ //$NON-NLS-2$
            return createError(new DefaultEditorError(editor, I18N.VALIDATION.analysisNameValidationMsg(new String(restrictedChars)), value));
        }

       

        for (char restricted : restrictedChars) {
            for (char next : value.toCharArray()) {
                if (next == restricted) {
                    restrictedFound.append(restricted);
                    break;
                }
            }
        }

        if (restrictedFound.length() > 0) {
            String errorMsg = I18N.VALIDATION.analysisNameValidationMsg(new String(restrictedChars))
                    + " " + I18N.VALIDATION.invalidChars(restrictedFound.toString()); //$NON-NLS-1$

            return createError(new DefaultEditorError(editor, errorMsg, value));
        }

        return Collections.emptyList();
    
    }

}
