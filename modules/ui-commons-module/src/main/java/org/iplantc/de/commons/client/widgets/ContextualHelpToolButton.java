package org.iplantc.de.commons.client.widgets;

import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * A ToolButton that displays a ContextualHelpPopup when clicked.
 * 
 * @author psarando
 * 
 */
public class ContextualHelpToolButton extends ToolButton {

    static {
        IplantResources.RESOURCES.getContxtualHelpStyle().ensureInjected();
    }

    private final ContextualHelpPopup helpPopup;

    public ContextualHelpToolButton() {
        this(null);
    }

    public ContextualHelpToolButton(Widget help) {
        super(IplantResources.RESOURCES.getContxtualHelpStyle().contextualHelp());

        helpPopup = new ContextualHelpPopup();

        if (help != null) {
            setHelp(help);
        }

        initHandlers();
    }

    private void initHandlers() {
        addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                helpPopup.showAt(getAbsoluteLeft(), getAbsoluteTop() + 15);
            }
        });
    }

    /**
     * Sets the ContextualHelpPopup's help contents.
     * 
     * @param help widget containing help text.
     * 
     */
    public void setHelp(Widget help) {
        helpPopup.add(help);
    }
}
