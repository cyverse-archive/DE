package org.iplantc.de.commons.client.validators;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.resources.client.messages.IplantValidationMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.Collections;
import java.util.List;

/**
 * @author jstroot
 */
public class DiskResourceSameNameValidator extends AbstractValidator<String> {

    private final DiskResource resource;
    private final IplantValidationMessages validationMessages;

    public DiskResourceSameNameValidator(DiskResource resource) {
        this(resource, GWT.<IplantValidationMessages> create(IplantValidationMessages.class));
    }

    DiskResourceSameNameValidator(final DiskResource resource,
                                  final IplantValidationMessages validationMessages) {
        this.resource = resource;
        this.validationMessages = validationMessages;
    }

    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        if (resource != null && resource.getName().equals(value)) {
            String errorMsg = validationMessages.newNameSameAsOldName();

            return createError(new DefaultEditorError(editor, errorMsg, value));
        }

        return Collections.emptyList();
    }

}
