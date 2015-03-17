package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.messages.IplantValidationMessages;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.List;

/**
 * @author jstroot
 */
public class DiskResourceUnixGlobValidator extends AbstractValidator<String> implements
        IPlantDefaultValidator {

    private IplantValidationMessages validationMessages;

    public DiskResourceUnixGlobValidator() {
        this(GWT.<IplantValidationMessages> create(IplantValidationMessages.class));
    }

    DiskResourceUnixGlobValidator(final IplantValidationMessages validationMessages) {
        this.validationMessages = validationMessages;
    }

    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            if (value.startsWith("/") || value.startsWith(" ") || value.endsWith(" ") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    || value.contains("../")) { //$NON-NLS-1$
                String errMsg = validationMessages.drGlobValidationMsg();
                return createError(new DefaultEditorError(editor, errMsg, value));
            }
        }
        return null;
    }
}
