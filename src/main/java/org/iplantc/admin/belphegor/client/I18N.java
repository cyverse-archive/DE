package org.iplantc.admin.belphegor.client;

import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.gwt.core.client.GWT;

/**
 * Provides static access to localized strings.
 *
 * @author lenards
 *
 */
public class I18N {
    // public static final IplantDisplayStrings DISPLAY =
    // org.iplantc.de.resources.client.messages.I18N.DISPLAY;
    public static final IplantDisplayStrings DISPLAY = GWT.create(IplantDisplayStrings.class);
    public static final IplantErrorStrings ERROR = org.iplantc.de.resources.client.messages.I18N.ERROR;
}