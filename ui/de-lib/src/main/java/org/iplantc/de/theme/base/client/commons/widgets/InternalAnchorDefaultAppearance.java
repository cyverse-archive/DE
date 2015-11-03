package org.iplantc.de.theme.base.client.commons.widgets;

import org.iplantc.de.commons.client.widgets.InternalAnchor.Appearance;
import org.iplantc.de.theme.base.client.commons.widgets.AnchorDefaultResources.Style;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;

/**
 * This class defines the default appearance for an internal anchor. The anchor may or may not be
 * in-line.
 *
 * @author jstroot
 */
public final class InternalAnchorDefaultAppearance implements Appearance {

    interface Templates extends XTemplates {
        @XTemplate("<div class='{style.anchor}'>{linkTxt}</div>")
        SafeHtml make(SafeHtml linkTxt, Style style);

        @XTemplate("<span class='{style.inlineAnchor}'>{linkTxt}</span>")
        SafeHtml makeInline(SafeHtml linkTxt, Style style);
    }

    private final Templates templates;
    private final Style style;

    private final boolean inline = true;

    /**
     * Constructs an appearance for an anchor that isn't in-line.
     */
    public InternalAnchorDefaultAppearance() {
        this(GWT.<Templates> create(Templates.class),
             GWT.<AnchorDefaultResources> create((AnchorDefaultResources.class)));
    }

    /**
     * Constructs an appearance dependent on whether anchor is in-line or not.
     *
     */
    InternalAnchorDefaultAppearance(final Templates templates,
                                    final AnchorDefaultResources resources) {
        this.templates = templates;
        this.style = resources.style();
        this.style.ensureInjected();
    }

    /**
     * @see Appearance#getAnchorElement(XElement)
     */
    @Override
    public XElement getAnchorElement(final XElement parent) {
        return parent.selectNode("." + (inline ? style.inlineAnchor() : style.anchor()));
    }

    /**
     * @see Appearance#render(String)
     */
    @Override
    public SafeHtml render(final String anchorText) {
        final SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendEscaped(anchorText);
        return inline
                   ? templates.makeInline(builder.toSafeHtml(), style)
                   : templates.make(builder.toSafeHtml(), style);
    }

}