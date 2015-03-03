package org.iplantc.de.theme.base.client.applications;

import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * An appearance for highlighting portions of a given text which match
 * a given regex pattern.
 *
 * Created by jstroot on 3/3/15.
 * @author jstroot
 */
public interface AppSearchHighlightAppearance {
    SafeHtml highlightText(String name, String pattern);
}
