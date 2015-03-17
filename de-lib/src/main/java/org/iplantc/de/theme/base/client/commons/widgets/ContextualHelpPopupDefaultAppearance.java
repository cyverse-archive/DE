package org.iplantc.de.theme.base.client.commons.widgets;

import org.iplantc.de.commons.client.widgets.ContextualHelpPopup;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class ContextualHelpPopupDefaultAppearance implements ContextualHelpPopup.ContextualHelpPopupAppearance {

    private final ContextualHelp.Resources resources;

    public ContextualHelpPopupDefaultAppearance() {
        this(GWT.<ContextualHelp.Resources>create(ContextualHelp.Resources.class));
    }

    ContextualHelpPopupDefaultAppearance(final ContextualHelp.Resources resources) {
        this.resources = resources;
        this.resources.css().ensureInjected();
    }

    @Override
    public String help() {
        return resources.css().help();
    }
}
