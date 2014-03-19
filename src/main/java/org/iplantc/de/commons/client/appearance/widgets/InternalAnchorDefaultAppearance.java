package org.iplantc.de.commons.client.appearance.widgets;

import org.iplantc.de.commons.client.appearance.widgets.AnchorDefaultResources.Style;
import org.iplantc.de.commons.client.widgets.InternalAnchor.Appearance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;

/**
 * This class defines the default appearance for an internal anchor. The anchor may or may not be
 * in-line.
 */
public final class InternalAnchorDefaultAppearance implements Appearance {

	interface Templates extends XTemplates {
		@XTemplate("<div class='{style.anchor}'>{linkTxt}</div>")
		SafeHtml make(SafeHtml linkTxt, Style style);
		
		@XTemplate("<span class='{style.inlineAnchor}'>{linkTxt}</span>")
		SafeHtml makeInline(SafeHtml linkTxt, Style style);
	}

 	private static final Templates FACTORY;
    private static final Style STYLE;

 	static {
 		FACTORY = GWT.create(Templates.class);
 		STYLE = AnchorDefaultResources.INSTANCE.style();
 		STYLE.ensureInjected();
 	}

 	private final boolean inline;
 	
 	/**
 	 * Constructs an appearance for an anchor that isn't in-line.
 	 */
 	public InternalAnchorDefaultAppearance() {
 		this.inline = false;
 	}
 	
 	/**
 	 * Constructs an appearance dependent on whether anchor is in-line or not.
 	 * 
 	 * @param inline true if the anchor is in-line, otherwise false.
 	 */
 	public InternalAnchorDefaultAppearance(final boolean inline) {
 		this.inline = inline;
 	}
 	
 	/**
 	 * @see Appearance#getAnchorElement(XElement)
 	 */
 	@Override
 	public XElement getAnchorElement(final XElement parent) {
 		return parent.selectNode("." + (inline ? STYLE.inlineAnchor() : STYLE.anchor()));
 	}
 	
 	/**
 	 * @see Appearance#render(String)
 	 */
 	@Override
	public SafeHtml render(final String anchorText) {
 		final SafeHtmlBuilder builder = new SafeHtmlBuilder();
 		builder.appendEscaped(anchorText);
 		return inline
 				? FACTORY.makeInline(builder.toSafeHtml(), STYLE)
 				: FACTORY.make(builder.toSafeHtml(), STYLE);
 	}
 	
}