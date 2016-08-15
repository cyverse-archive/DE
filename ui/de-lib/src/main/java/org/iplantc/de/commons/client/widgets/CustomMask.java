package org.iplantc.de.commons.client.widgets;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.dom.Mask;
import com.sencha.gxt.core.client.dom.XElement;

/**
 * @author aramsey
 */
public class CustomMask {

    public interface CustomMaskAppearance extends Mask.MaskAppearance {

    }

    private CustomMaskAppearance appearance;

    @Inject
    public CustomMask(CustomMaskAppearance appearance) {
        this.appearance = appearance;
    }

    public void mask(Widget widget, String message) {
        XElement parent = (XElement)widget.getElement();
        mask(parent, message);
    }

    public void mask(XElement parent, String message) {
        parent.addClassName(appearance.masked());
        if ("static".equals(parent.getComputedStyle("position"))) {
            parent.addClassName(appearance.positioned());
        }
        appearance.unmask(parent);
        appearance.mask(parent, message);
    }
}
