package org.iplantc.de.theme.base.client.commons.error;

import org.iplantc.de.commons.client.views.dialogs.IplantErrorDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * @author jstroot
 */
public class IplantErrorDialogDefaultAppearance implements IplantErrorDialog.IplantErrorDialogAppearance {

    static interface DetailsTemplate extends SafeHtmlTemplates {
        @Template("<h2>Details:</h2><pre style='max-height: 268; overflow: auto; background-color: #fff'><code>{0}</code></pre>")
        SafeHtml details(SafeHtml details);
    }

    private final DetailsTemplate template;

    public IplantErrorDialogDefaultAppearance() {
        this(GWT.<DetailsTemplate> create(DetailsTemplate.class));
    }

    IplantErrorDialogDefaultAppearance(final DetailsTemplate template) {
        this.template = template;
    }

    @Override
    public SafeHtml details(SafeHtml details) {
        return null;
    }

    @Override
    public int maxHeight() {
        return 400;
    }
}
