package org.iplantc.de.commons.client.validators;

import org.iplantc.de.resources.client.messages.IplantValidationMessages;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;

/**
 * @author jstroot
 */
public class BasicEmailValidator3 extends RegExValidator {
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"; //$NON-NLS-1$

    public BasicEmailValidator3() {
        this(GWT.<IplantValidationMessages> create(IplantValidationMessages.class),
             EMAIL_PATTERN);

    }

    BasicEmailValidator3(final IplantValidationMessages validationMessages,
                         final String emailRegex){
        super(emailRegex, validationMessages.invalidEmail());
    }

}
