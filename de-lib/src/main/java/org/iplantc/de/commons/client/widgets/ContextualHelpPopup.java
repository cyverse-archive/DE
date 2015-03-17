package org.iplantc.de.commons.client.widgets;

import com.google.gwt.core.shared.GWT;

import com.sencha.gxt.widget.core.client.Popup;
import com.sencha.gxt.widget.core.client.event.ShowEvent;

/**
 * @author jstroot
 */
public class ContextualHelpPopup extends Popup {

    public interface ContextualHelpPopupAppearance {

        String help();
    }

    public ContextualHelpPopup() {
        ContextualHelpPopupAppearance appearance = GWT.create(ContextualHelpPopupAppearance.class);
        addStyleName(appearance.help());
        setAutoHide(true);
    }
    
    @Override
    protected void afterShow() {
        super.afterShow();
        fireEvent(new ShowEvent());
    }

}
