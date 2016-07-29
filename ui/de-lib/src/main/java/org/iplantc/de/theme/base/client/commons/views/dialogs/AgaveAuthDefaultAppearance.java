package org.iplantc.de.theme.base.client.commons.views.dialogs;

import org.iplantc.de.commons.client.views.dialogs.AgaveAuthPrompt;

import com.google.gwt.core.client.GWT;

/**
 * @author aramsey
 */
public class AgaveAuthDefaultAppearance implements AgaveAuthPrompt.AgaveAuthAppearance {

    private AgaveAuthDisplayStrings displayStrings;

    public AgaveAuthDefaultAppearance() {
        this(GWT.<AgaveAuthDisplayStrings>create(AgaveAuthDisplayStrings.class));
    }

    public AgaveAuthDefaultAppearance(AgaveAuthDisplayStrings agaveAuthDisplayStrings) {
        this.displayStrings = agaveAuthDisplayStrings;
    }

    @Override
    public String agaveRedirectTitle() {
        return displayStrings.agaveRedirectTitle();
    }

    @Override
    public String agaveRedirectMessage() {
        return displayStrings.agaveRedirectMessage();
    }

    @Override
    public String authenticateBtnText() {
        return displayStrings.authenticateBtnText();
    }

    @Override
    public String declineAuthBtnText() {
        return displayStrings.declineAuthBtnText();
    }
}
