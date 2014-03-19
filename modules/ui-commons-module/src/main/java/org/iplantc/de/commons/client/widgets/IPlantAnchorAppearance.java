package org.iplantc.de.commons.client.widgets;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.dom.XElement;

/**
 * @author sriram
 */
public interface IPlantAnchorAppearance {

    void onUpdateText(XElement element, String text);

    void render(SafeHtmlBuilder sb);
}
