package org.iplantc.de.theme.base.client.applications;

import com.google.common.base.Strings;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * @author jstroot
 */
public class AppSearchHighlightDefaultAppearance implements AppSearchHighlightAppearance {

    public final String REPLACEMENT_START = "<font style='background: #FF0'>";
    public final String REPLACEMENT_END = "</font>";
    @Override
    public SafeHtml highlightText(String name, String pattern) {
        // Sanitize incoming string
        SafeHtml text = SafeHtmlUtils.fromString(Strings.nullToEmpty(name));

        final RegExp regExp = RegExp.compile(pattern, "ig");
        return SafeHtmlUtils.fromTrustedString(regExp.replace(text.asString(), REPLACEMENT_START + "$1" + REPLACEMENT_END));
    }
}
