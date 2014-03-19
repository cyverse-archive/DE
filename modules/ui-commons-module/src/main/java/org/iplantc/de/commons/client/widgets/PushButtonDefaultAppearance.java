package org.iplantc.de.commons.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;

public class PushButtonDefaultAppearance implements PushButtonAppearance {

    public interface Template extends XTemplates {
        @XTemplate(source = "PushButtonDefaultAppearance.html")
        SafeHtml template(Style style);
    }

    public interface Style extends CssResource {

        String pushButton();

        String pushButtonText();

        String pushButtonImage();
    }

    private final Style style;
    private final Template template;

    public interface Resources extends ClientBundle {

        @Source("PushButtonDefaultAppearance.css")
        Style style();
    }

    public PushButtonDefaultAppearance() {
        this((Resources)GWT.create(Resources.class));
    }

    public PushButtonDefaultAppearance(Resources resources) {
        this.style = resources.style();
        this.style.ensureInjected();

        this.template = GWT.create(Template.class);
    }

    @Override
    public void onUpdateIcon(XElement parent, Image icon) {
        XElement element = parent.selectNode("." + style.pushButtonImage());
        element.removeChildren();
        element.appendChild(icon.getElement());
    }

    @Override
    public void onUpdateText(XElement parent, String text) {
        parent.selectNode("." + style.pushButtonText()).setInnerText(text);
    }

    @Override
    public void render(SafeHtmlBuilder sb) {
        sb.append(template.template(style));
    }

}
