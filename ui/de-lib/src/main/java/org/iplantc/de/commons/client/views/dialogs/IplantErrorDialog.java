package org.iplantc.de.commons.client.views.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;

import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

/**
 * @author jstroot
 */
public class IplantErrorDialog extends AlertMessageBox {

    public interface IplantErrorDialogAppearance {

        SafeHtml details(SafeHtml details);

        int maxHeight();
    }

    private int maxHeight;

    /**
     * Creates a message box with an error icon and the specified title and
     * message.
     *
     * @param title   the message box title
     * @param message the message displayed in the message box
     * @param details the details to be displayed
     */
    IplantErrorDialog(final String title,
                      final String message,
                      final SafeHtml details,
                      final IplantErrorDialogAppearance appearance) {
        super(title, message);

        maxHeight = appearance.maxHeight();

        if(details != null)  {
            contentAppearance.getContentElement(getElement()).setInnerSafeHtml(appearance.details(details));
        }

        getElement().getStyle().setProperty("maxHeight", String.valueOf(maxHeight));
    }

    public IplantErrorDialog(final String title,
                             final String message) {
        this(title, message, null, GWT.<IplantErrorDialogAppearance> create(IplantErrorDialogAppearance.class));
    }
}
