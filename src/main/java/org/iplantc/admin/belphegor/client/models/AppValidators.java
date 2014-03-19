package org.iplantc.admin.belphegor.client.models;

import org.iplantc.de.apps.client.Constants;
import org.iplantc.de.commons.client.util.RegExp;
import org.iplantc.de.commons.client.validators.BasicEmailValidator3;
import org.iplantc.de.resources.client.messages.I18N;

import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;

/**
 * @author jstroot
 */
public class AppValidators {
    private static String appNameRegex = Format.substitute("[^{0}{1}][^{1}]*", //$NON-NLS-1$
            Constants.CLIENT.appNameRestrictedStartingChars(), RegExp.escapeCharacterClassSet(Constants.CLIENT.appNameRestrictedChars()));

    public static RegExValidator APP_NAME_VALIDATOR = new RegExValidator(appNameRegex, I18N.ERROR.invalidAppNameMsg(Constants.CLIENT.appNameRestrictedStartingChars(),
            Constants.CLIENT.appNameRestrictedChars()));

    public static RegExValidator APP_WIKI_URL_VALIDATOR = new BasicEmailValidator3();

}
