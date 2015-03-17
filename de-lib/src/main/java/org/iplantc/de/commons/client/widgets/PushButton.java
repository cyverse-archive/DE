package org.iplantc.de.commons.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;

import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.Component;

/**
 * A simple button
 *
 * @author sriram, jstroot
 */
public class PushButton extends Component implements HasClickHandlers {
    public static interface PushButtonAppearance {

        void onUpdateIcon(XElement parent, Image icon);

        void onUpdateText(XElement parent, String text);

        void render(SafeHtmlBuilder sb);
    }

    private final PushButtonAppearance appearance;

    /**
     * Create a new push button with text and width
     */
    public PushButton(final String text,
                      final int width) {
        this(text, width, GWT.<PushButtonAppearance> create(PushButtonAppearance.class));
    }

    /**
     * create a new push button with text, appearance and width
     */
    public PushButton(final String text,
                      final int width,
                      final PushButtonAppearance appearance) {
        this.appearance = appearance;
        setWidth(width);
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        this.appearance.render(sb);

        setElement(Element.as(XDOM.create(sb.toSafeHtml())));
        setText(text);
        sinkEvents(Event.ONCLICK);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public void setImage(Image icon) {
        appearance.onUpdateIcon(getElement(), icon);
    }

    public void setText(String text) {
        appearance.onUpdateText(getElement(), text);
    }
}
