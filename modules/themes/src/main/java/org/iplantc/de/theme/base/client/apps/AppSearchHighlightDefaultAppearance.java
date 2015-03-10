package org.iplantc.de.theme.base.client.apps;

import com.google.common.base.Strings;
import com.google.gwt.regexp.shared.RegExp;

/**
 * @author jstroot
 */
public class AppSearchHighlightDefaultAppearance implements AppSearchHighlightAppearance {

    public final String REPLACEMENT_START = "<font style='background: #FF0'>";
    public final String REPLACEMENT_END = "</font>";
    @Override
    public String highlightText(String name, String pattern) {
        if(Strings.isNullOrEmpty(pattern)){
            return name;
        }

        // XXX JDS Keep an eye on performance.
        final RegExp regExp = RegExp.compile(pattern, "ig");
        return regExp.replace(name, REPLACEMENT_START + "$1" + REPLACEMENT_END);
    }
}
