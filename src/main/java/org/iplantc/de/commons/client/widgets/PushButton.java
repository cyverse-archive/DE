package org.iplantc.de.commons.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;

import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.widget.core.client.Component;

/**
 * A simple button
 * 
 * @author sriram
 * 
 */
public class PushButton extends Component implements HasClickHandlers {
    private final PushButtonAppearance appearance;

    /**
     * Create a new push button with text and width
     * 
     * @param text
     * @param width
     */
    public PushButton(String text, int width) {
        this(text, (PushButtonAppearance)GWT.create(PushButtonDefaultAppearance.class), width);
    }

    /**
     * create a new push button with text, appearance and width
     * 
     * @param text
     * @param appearance
     * @param width
     */
    public PushButton(String text, PushButtonAppearance appearance, int width) {
        this.appearance = appearance;
        setWidth(width);
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        this.appearance.render(sb);

        setElement(XDOM.create(sb.toSafeHtml()));
        setText(text);
        sinkEvents(Event.ONCLICK);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public void setText(String text) {
        appearance.onUpdateText(getElement(), text);
    }

    public void setImage(Image icon) {
        appearance.onUpdateIcon(getElement(), icon);
    }
}
