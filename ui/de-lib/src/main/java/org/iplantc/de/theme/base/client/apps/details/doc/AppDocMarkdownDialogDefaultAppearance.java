package org.iplantc.de.theme.base.client.apps.details.doc;

import org.iplantc.de.apps.client.views.details.doc.AppDocMarkdownDialog;
import org.iplantc.de.theme.base.client.apps.AppsMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import java.util.List;

/**
 * @author jstroot
 */
public class AppDocMarkdownDialogDefaultAppearance implements AppDocMarkdownDialog.AppDocMarkdownDialogAppearance {
    public interface AppDocTemplates extends SafeHtmlTemplates {
        @Template("<h4>{0}</h4>")
        SafeHtml createRefLabel(String header);
        @Template("<li>{0}</li>")
        SafeHtml createReference(String ref);
    }

    private final AppDocTemplates templates;
    private final AppsMessages appsMessages;

    public AppDocMarkdownDialogDefaultAppearance() {
        this(GWT.<AppDocTemplates> create(AppDocTemplates.class),
             GWT.<AppsMessages> create(AppsMessages.class));
    }

    public AppDocMarkdownDialogDefaultAppearance(final AppDocTemplates templates,
                                                 final AppsMessages appsMessages) {
        this.templates = templates;
        this.appsMessages = appsMessages;
    }

    @Override
    public SafeHtml createDocumentMarkdown(final String mdRenderedDoc,
                                           final List<String> appDocReferences) {
        // Assuming that given doc string has been MD rendered already
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.append(SafeHtmlUtils.fromTrustedString(mdRenderedDoc));
        sb.append(templates.createRefLabel(appsMessages.refLbl()));
        sb.appendHtmlConstant("<ul>");
        for(String ref : appDocReferences){
            sb.append(templates.createReference(ref));
        }
        sb.appendHtmlConstant("</ul>");
        return sb.toSafeHtml();
    }
}
