package org.iplantc.de.commons.client.views.gxt3.dialogs;


import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

public class IplantErrorDialog extends AlertMessageBox {


    static interface DetailsTemplate extends SafeHtmlTemplates {

        @Template("<h2>Details:</h2><pre style='max-height: 268; overflow: auto; background-color: #fff'><code>{0}</code></pre>")
        SafeHtml details(SafeHtml details);
    }

    private int maxHeight;

    /**
     * Creates a message box with an error icon and the specified title and
     * message.
     *
     * @param title   the message box title
     * @param message the message displayed in the message box
     * @param details the details to be displayed
     *
     */
    public IplantErrorDialog(String title, String message, SafeHtml details) {
        super(title, message);
        DetailsTemplate template = GWT.create(DetailsTemplate.class);

        maxHeight = 400;

        contentAppearance.getContentElement(getElement()).setInnerSafeHtml(template.details(details));
        getElement().getStyle().setProperty("maxHeight", String.valueOf(maxHeight));
    }

    public IplantErrorDialog(String title, String message) {
        super(title, message);
    }
}
