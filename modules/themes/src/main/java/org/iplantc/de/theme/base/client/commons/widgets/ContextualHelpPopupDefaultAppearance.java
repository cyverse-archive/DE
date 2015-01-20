package org.iplantc.de.theme.base.client.commons.widgets;

import org.iplantc.de.commons.client.widgets.ContextualHelpPopup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author jstroot
 */
public class ContextualHelpPopupDefaultAppearance implements ContextualHelpPopup.ContextualHelpPopupAppearance {

    public static interface PopupHelpCssResources extends CssResource {
        @ClassName("help")
        String help();
    }

    interface PopupResources extends ClientBundle {
        @Source("ContextualHelpPopup.css")
        PopupHelpCssResources css();
    }

    private final PopupResources resources;

    public ContextualHelpPopupDefaultAppearance() {
        this(GWT.<PopupResources>create(PopupResources.class));
    }

    ContextualHelpPopupDefaultAppearance(final PopupResources resources) {
        this.resources = resources;
        this.resources.css().ensureInjected();
    }

    @Override
    public String help() {
        return resources.css().help();
    }
}
