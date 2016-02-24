package org.iplantc.de.commons.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiConstructor;

import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.Component;

/**
 * A widget to create html link like <a> tag
 *
 * @author sriram, jstroot
 */
public class IPlantAnchor extends Component implements HasClickHandlers {

    public static interface IPlantAnchorAppearance {

        void onUpdateText(XElement element, String text);

        void render(SafeHtmlBuilder sb);

    }

    private final IPlantAnchorAppearance appearance;

    private String text;

    @UiConstructor
    public IPlantAnchor(String text) {
        this(text, -1);
    }

    public IPlantAnchor(String text, int width) {
        this(text, width, GWT.<IPlantAnchorAppearance>create(IPlantAnchorAppearance.class));
    }

    /**
     * A widget to create <a> like hyperlinks
     *
     * @param text text to display
     */
    public IPlantAnchor(String text, int width, IPlantAnchorAppearance appearance) {
        this.text = text;
        this.appearance = appearance;
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        this.appearance.render(sb);
        setWidth(width);
        setElement(Element.as(XDOM.create(sb.toSafeHtml())));
        setText(text);
    }

    public IPlantAnchor(String text, int width, ClickHandler handler) {
        this(text, width, GWT.<IPlantAnchorAppearance>create(IPlantAnchorAppearance.class));

        if (handler != null) {
            addClickHandler(handler);
        }
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public void setText(String text) {
        this.text = text;
        appearance.onUpdateText(getElement(), text);
    }

    public String getText() {
        return text;
    }

}
