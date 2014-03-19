package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.models.AboutApplicationData;

import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * An Appearance interface for the About Window's contents and layout.
 * 
 * @author psarando
 * 
 */
public interface AboutApplicationAppearance {

    SafeHtml about(AboutApplicationData data, String userAgent, SafeHtml copyright, SafeHtml nsfProject);
}
