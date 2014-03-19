package org.iplantc.de.commons.client.validators;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.Collections;
import java.util.List;

public class DiskResourceSameNameValidator extends AbstractValidator<String> {

    private final DiskResource resource;

    public DiskResourceSameNameValidator(DiskResource resource) {
        this.resource = resource;
    }

    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        if (resource != null && resource.getName().equals(value)) {
            String errorMsg = I18N.VALIDATION.newNameSameAsOldName();

            return createError(new DefaultEditorError(editor, errorMsg, value));
        }

        return Collections.emptyList();
    }

}
