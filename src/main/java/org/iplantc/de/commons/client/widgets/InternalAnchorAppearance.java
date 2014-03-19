package org.iplantc.de.commons.client.widgets;

import com.google.gwt.safehtml.shared.SafeHtml;

import com.sencha.gxt.core.client.dom.XElement;

/**
 * This is the interface for any class that defines the appearance of an internal anchor.
 */
public interface InternalAnchorAppearance {

	/**
	 * Implementations of this method should retrieve the anchor element from its parent. 
	 * 
	 * @param parent The DOM element of the parent of the anchor element
	 */
	XElement getAnchorElement(XElement parent);

	/**
	 * Implementations of this method should render the anchor in HTML.
	 * 
	 * @param anchorText the text to display in the anchor
     *
     * @return the HTML
	 */
	SafeHtml render(String anchorText);

}