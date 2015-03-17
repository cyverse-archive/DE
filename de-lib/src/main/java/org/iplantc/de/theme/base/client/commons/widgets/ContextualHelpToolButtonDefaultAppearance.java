package org.iplantc.de.theme.base.client.commons.widgets;

import org.iplantc.de.commons.client.widgets.ContextualHelpToolButton;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class ContextualHelpToolButtonDefaultAppearance implements ContextualHelpToolButton.ContextualHelpToolButtonAppearance {

    private ContextualHelp.Resources resources;

    public ContextualHelpToolButtonDefaultAppearance() {
        this(GWT.<ContextualHelp.Resources> create(ContextualHelp.Resources.class));
    }

    ContextualHelpToolButtonDefaultAppearance(final ContextualHelp.Resources resources) {
        this.resources = resources;
        this.resources.css().ensureInjected();
    }

    @Override
    public String contextualHelpStyle() {
        return resources.css().contextualHelp();
    }
}
