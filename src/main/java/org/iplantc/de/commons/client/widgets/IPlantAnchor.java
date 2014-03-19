/**
 * 
 */
package org.iplantc.de.commons.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.widget.core.client.Component;

/**
 * A widget to create html link like <a> tag
 * 
 * @author sriram
 * 
 */
public class IPlantAnchor extends Component implements HasClickHandlers {

    private final IPlantAnchorAppearance appearance;

    public IPlantAnchor(String text, int width) {
        this(text, width, GWT.<IPlantAnchorAppearance> create(IPlantAnchorAppearance.class));
    }

    /**
     * 
     * A widget to create <a> like hyperlinks
     * 
     * @param text text to display
     */
    public IPlantAnchor(String text, int width, IPlantAnchorAppearance appearance) {
        this.appearance = appearance;
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        this.appearance.render(sb);
        setWidth(width);
        setElement(XDOM.create(sb.toSafeHtml()));
        setText(text);
    }

    public IPlantAnchor(String text, int width, ClickHandler handler) {
        this(text, width, GWT.<IPlantAnchorAppearance> create(IPlantAnchorAppearance.class));

        if (handler != null) {
            addClickHandler(handler);
        }
    }

    public void setText(String text) {
        appearance.onUpdateText(getElement(), text);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

}
