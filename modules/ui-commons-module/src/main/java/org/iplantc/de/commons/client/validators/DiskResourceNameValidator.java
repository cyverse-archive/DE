package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.constants.IplantValidationConstants;
import org.iplantc.de.resources.client.messages.IplantValidationMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.Collections;
import java.util.List;

/**
 * Validates a file or folder name.
 * 
 * @author psarando, jstroot
 * 
 */
public class DiskResourceNameValidator extends AbstractValidator<String> implements
        IPlantDefaultValidator {

    private final IplantValidationConstants validationConstants;
    private final IplantValidationMessages validationMessages;

    public DiskResourceNameValidator() {
        this(GWT.<IplantValidationConstants> create(IplantValidationConstants.class),
             GWT.<IplantValidationMessages> create(IplantValidationMessages.class));
    }

    DiskResourceNameValidator(final IplantValidationConstants validationConstants,
                              final IplantValidationMessages validationMessages) {
        this.validationConstants = validationConstants;
        this.validationMessages = validationMessages;
    }

    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        if (value == null) {
            return Collections.emptyList();
        }

        char[] restrictedChars = (validationConstants.restrictedDiskResourceNameChars()).toCharArray();
        // check for spaces at the beginning and at the end of the file name
        if (value.startsWith(" ") || value.endsWith(" ")) { //$NON-NLS-1$ //$NON-NLS-2$
            return createError(new DefaultEditorError(editor,
                                                      validationMessages.drNameValidationMsg(new String(restrictedChars)),
                                                      value));
        }

        //$NON-NLS-1$
        StringBuilder restrictedFound = new StringBuilder();

        for (char restricted : restrictedChars) {
            for (char next : value.toCharArray()) {
                if (next == restricted) {
                    restrictedFound.append(restricted);
                    break;
                }
            }
        }

        if (restrictedFound.length() > 0) {
            String errorMsg = validationMessages.drNameValidationMsg(new String(restrictedChars)) + " " //$NON-NLS-1$
                    + validationMessages.invalidChars(restrictedFound.toString());

            return createError(new DefaultEditorError(editor, errorMsg, value));
        }

        return Collections.emptyList();
    }
}
