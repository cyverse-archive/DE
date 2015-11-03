package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.messages.IplantValidationMessages;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;

/**
 * @author jstroot
 */
public class UrlValidator extends RegExValidator {
    private static final String URL_REGEX = "^(?:ftp|FTP|HTTPS?|https?)://[^/]+\\.[^/]+.*"; //$NON-NLS-1$

    public UrlValidator() {
        this(URL_REGEX, GWT.<IplantValidationMessages> create(IplantValidationMessages.class));
    }

    UrlValidator(final String urlRegex,
                 final IplantValidationMessages validationMessages) {
        super(urlRegex, validationMessages.invalidUrl());
    }
}
