/**
 * 
 */
package org.iplantc.de.commons.client.appearance.widgets;

import org.iplantc.de.commons.client.widgets.IPlantAnchorAppearance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;

/**
 * @author sriram
 *
 */
public class IPlantAnchorDefaultAppearance implements IPlantAnchorAppearance {
    
    public interface Template extends XTemplates {
        @XTemplate(source = "IPlantAnchorDefaultAppearance.html")
        SafeHtml template(AnchorDefaultResources.Style style);
    }

    protected AnchorDefaultResources.Style style;
    protected Template template;

    public IPlantAnchorDefaultAppearance() {
        this.style = AnchorDefaultResources.INSTANCE.style();
        this.style.ensureInjected();

        this.template = GWT.create(Template.class);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.commons.client.widgets.IPlantAnchorAppearance#onUpdateText(com.sencha.gxt.core.client.dom.XElement, java.lang.String)
     */
    @Override
    public void onUpdateText(XElement parent, String text) {
        parent.selectNode("." + style.anchorText()).setInnerText(text);

    }

    @Override
    public void render(SafeHtmlBuilder sb) {
        sb.append(template.template(style));
    }

}
