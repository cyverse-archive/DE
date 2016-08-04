package org.iplantc.de.theme.base.client.commons.widgets;

import org.iplantc.de.commons.client.widgets.CustomMask;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.core.client.dom.Mask;
import com.sencha.gxt.core.client.dom.Mask.MessageTemplates;

/**
 * @author aramsey
 */
public class CustomMaskDefaultAppearance extends Mask.MaskDefaultAppearance implements CustomMask.CustomMaskAppearance {

    public interface CustomMaskStyle extends MaskStyle {
        String text();
    }

    public interface CustomMaskResources extends MaskResources {
        @Source({"com/sencha/gxt/core/client/dom/Mask.css", "CustomMask.css"})
        CustomMaskStyle css();
    }

    public CustomMaskDefaultAppearance() {
        this(GWT.<MessageTemplates>create(MessageTemplates.class),
             GWT.<CustomMaskResources>create(CustomMaskResources.class));
    }

    public CustomMaskDefaultAppearance(MessageTemplates messageTemplates,
                                       CustomMaskResources customMaskResources) {

        super(messageTemplates, customMaskResources);
    }
}
