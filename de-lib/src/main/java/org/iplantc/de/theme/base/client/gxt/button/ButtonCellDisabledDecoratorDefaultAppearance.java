package org.iplantc.de.theme.base.client.gxt.button;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.theme.base.client.button.ButtonCellDefaultAppearance;

/**
 * Override of GXT's button cell appearance which adds/removes an HTML data
 * attribute to indicate if the button is or isn't disabled.
 *
 *
 * @author jstroot
 * @param <C>
 *
 * @see <a href="https://html.spec.whatwg.org/#embedding-custom-non-visible-data-with-the-data-*-attributes">HTML SPEC</a>
 */
public class ButtonCellDisabledDecoratorDefaultAppearance<C> extends ButtonCellDefaultAppearance<C> {
    protected final String DATA_DISABLED_ATTR = "data-disabled";

    @Override
    public void onOver(XElement parent, boolean over) {
        super.onOver(parent, over);
        if(parent.hasClassName(ThemeStyles.get().style().disabled())){
            if(!parent.hasAttribute(DATA_DISABLED_ATTR)){
                parent.setAttribute(DATA_DISABLED_ATTR, "");
            }
        } else {
            parent.removeAttribute(DATA_DISABLED_ATTR);
        }
    }
}
