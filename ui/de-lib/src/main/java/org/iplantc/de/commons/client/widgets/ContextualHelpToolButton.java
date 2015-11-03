package org.iplantc.de.commons.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * A ToolButton that displays a ContextualHelpPopup when clicked.
 * 
 * @author psarando, jstroot
 *
 * FIXME Need to hunt down usages of IplantContextualHelpAccessStyle
 * 
 */
public class ContextualHelpToolButton extends ToolButton {

    public interface ContextualHelpToolButtonAppearance {
        String contextualHelpStyle();
    }

    private final ContextualHelpPopup helpPopup;
    private final ContextualHelpToolButtonAppearance appearance;

    public ContextualHelpToolButton() {
        this(null);
    }

    public ContextualHelpToolButton(Widget help) {
        this(help, GWT.<ContextualHelpToolButtonAppearance> create(ContextualHelpToolButtonAppearance.class));

    }

    public ContextualHelpToolButton(final Widget help,
                                    final ContextualHelpToolButtonAppearance appearance) {
        super(appearance.contextualHelpStyle());
        this.appearance = appearance;

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
