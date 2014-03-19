package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.models.AboutApplicationData;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;

/**
 * The Default production Appearance for the About Window's contents and layout.
 * 
 * @author psarando
 * 
 */
public class AboutApplicationDefaultAppearance implements AboutApplicationAppearance {
    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<p style='font-style:italic;'> {4} </p>"
                + "<p>Release: {0}</p>"
                + "<p>Build: {1}</p>"
                + "<p>User Agent: {2}</p>"
                + "<p style='font-weight:700'> {3} </p>")
        SafeHtml about(String release, String build, String userAgent, SafeHtml copyright,
                SafeHtml nsfProject);
    }

    private final Templates template = GWT.create(Templates.class);

    @Override
    public SafeHtml about(AboutApplicationData data, String userAgent, SafeHtml copyright,
            SafeHtml nsfProject) {
        return template.about(Strings.nullToEmpty(data.getReleaseVersion()),
                Strings.nullToEmpty(data.getBuild()), Window.Navigator.getUserAgent(),
                I18N.DISPLAY.projectCopyrightStatement(), I18N.DISPLAY.nsfProjectText());
    }

}
