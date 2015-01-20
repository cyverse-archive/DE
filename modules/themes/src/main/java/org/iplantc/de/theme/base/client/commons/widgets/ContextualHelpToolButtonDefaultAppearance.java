package org.iplantc.de.theme.base.client.commons.widgets;

import org.iplantc.de.commons.client.widgets.ContextualHelpToolButton;
import org.iplantc.de.resources.client.IplantContextualHelpAccessStyle;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class ContextualHelpToolButtonDefaultAppearance implements ContextualHelpToolButton.ContextualHelpToolButtonAppearance {

    private final IplantContextualHelpAccessStyle style;

    public ContextualHelpToolButtonDefaultAppearance() {
        this(GWT.<IplantContextualHelpAccessStyle> create(IplantContextualHelpAccessStyle.class));
    }

    ContextualHelpToolButtonDefaultAppearance(final IplantContextualHelpAccessStyle style) {
        this.style = style;
        this.style.ensureInjected();
    }

    @Override
    public String contextualHelpStyle() {
        return style.contextualHelp();
    }
}
