package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.messages.IplantValidationMessages;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;

/**
 * @author jstroot
 */
public class ImportUrlValidator extends RegExValidator {
    private static final String URL_REGEX = "^(?:ftp|FTP|HTTPS?|https?)://[^/]+/.*[^/ ]$"; //$NON-NLS-1$

    public ImportUrlValidator() {
        this(URL_REGEX, GWT.<IplantValidationMessages> create(IplantValidationMessages.class));
    }

    ImportUrlValidator(final String urlRegex,
                       final IplantValidationMessages validationMessages) {
        super(urlRegex, validationMessages.invalidImportUrl());
    }
}
