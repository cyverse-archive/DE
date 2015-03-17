package org.iplantc.de.apps.integration.client.view.propertyEditors.util;

import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.List;

/**
 * A Validator to ensure Environment Variable names only contain Alpha-numeric and underscore characters.
 * 
 * @author psarando
 * 
 */
public class EnvironmentVariableNameValidator extends AbstractValidator<String> {

    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        if (Strings.isNullOrEmpty(value) || !value.matches("[\\w]+")) { //$NON-NLS-1$
            String errorMsg = I18N.VALIDATION.environmentVariableNameValidationMsg();

            return createError(new DefaultEditorError(editor, errorMsg, value));
        }

        return null;
    }

}
