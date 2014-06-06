package org.iplantc.de.resources.client;

import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * The HTML templates used to render the cell.
 */
public interface FavoriteTemplates extends SafeHtmlTemplates {

    @SafeHtmlTemplates.Template("<span name='{0}' class='{1}' qtip='{2}'> </span>")
    SafeHtml cell(String imgName, String imgClassName, String imgToolTip);

    @SafeHtmlTemplates.Template("<span id='{3}' name='{0}' class='{1}' qtip='{2}'> </span>")
    SafeHtml debugCell(String imgName, String imgClassName, String imgToolTip, String debugId);
}