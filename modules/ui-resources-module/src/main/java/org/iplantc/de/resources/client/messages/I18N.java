/**
 *
 */
package org.iplantc.de.resources.client.messages;

import org.iplantc.de.resources.client.constants.IplantValidationConstants;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsContextualHelpMessages;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsDisplayMessages;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;

import com.google.gwt.core.shared.GWT;

/**
 * @author sriram
 *
 */
public class I18N {

    public static final IplantDisplayStrings DISPLAY = GWT.create(IplantDisplayStrings.class);
	public static final IplantErrorStrings  ERROR = GWT.create(IplantErrorStrings.class);
    public static final IplantValidationMessages VALIDATION = GWT.create(IplantValidationMessages.class);
    public static final IplantContextualHelpStrings HELP = GWT.create(IplantContextualHelpStrings.class);
    public static final IplantValidationConstants V_CONSTANTS = GWT.create(IplantValidationConstants.class);
    public static final IplantNewUserTourStrings TOUR = GWT.create(IplantNewUserTourStrings.class);
    public static final AppsWidgetsPropertyPanelLabels APPS_LABELS = GWT.create(AppsWidgetsPropertyPanelLabels.class);
    public static final AppsWidgetsContextualHelpMessages APPS_HELP = GWT.create(AppsWidgetsContextualHelpMessages.class);
    public static final AppsWidgetsDisplayMessages APPS_MESSAGES = GWT.create(AppsWidgetsDisplayMessages.class);
}
