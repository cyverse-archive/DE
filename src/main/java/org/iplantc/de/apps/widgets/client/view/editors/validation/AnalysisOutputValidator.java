package org.iplantc.de.apps.widgets.client.view.editors.validation;

import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.List;

/**
 * A Validator to ensure no spaces are found in the path to an analysis output folder.
 * 
 * @author psarando
 * 
 */
public class AnalysisOutputValidator extends AbstractValidator<String> {

    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        // CORE-4079 and CORE-4080: Output paths should not contain spaces.
        if (!Strings.isNullOrEmpty(value) && value.contains(" ")) { //$NON-NLS-1$
            EditorError err = new DefaultEditorError(editor,
                    I18N.ERROR.defaultOutputFolderValidationError(), value);

            return createError(err);
        }

        return null;
    }

}
