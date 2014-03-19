package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.List;

public class DiskResourceUnixGlobValidator extends AbstractValidator<String> implements
        IPlantDefaultValidator {

    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            if (value.startsWith("/") || value.startsWith(" ") || value.endsWith(" ") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    || value.contains("../")) { //$NON-NLS-1$
                String errMsg = I18N.VALIDATION.drGlobValidationMsg();
                return createError(new DefaultEditorError(editor, errMsg, value));
            }
        }
        return null;
    }
}
