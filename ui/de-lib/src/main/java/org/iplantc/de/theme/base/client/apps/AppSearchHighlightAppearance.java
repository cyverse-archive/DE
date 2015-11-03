package org.iplantc.de.theme.base.client.apps;

/**
 * An appearance for highlighting portions of a given text which match
 * a given regex pattern.
 *
 * Created by jstroot on 3/3/15.
 * @author jstroot
 */
public interface AppSearchHighlightAppearance {
    String highlightText(String name, String pattern);
}
