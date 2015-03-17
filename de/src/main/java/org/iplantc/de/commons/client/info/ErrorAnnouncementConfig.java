package org.iplantc.de.commons.client.info;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author jstroot
 */
public class ErrorAnnouncementConfig extends IplantAnnouncementConfig {

    public ErrorAnnouncementConfig(final String message) {
        this(SafeHtmlUtils.fromString(message));
    }

    public ErrorAnnouncementConfig(final SafeHtml message) {
        super(message);
    }

    public ErrorAnnouncementConfig(final SafeHtml message, boolean closable) {
        super(message, closable);
    }

    public ErrorAnnouncementConfig(final SafeHtml message, boolean closable, int timeout_ms) {
        super(message, closable, timeout_ms);
    }

    public ErrorAnnouncementConfig(final String message, boolean closable, int timeout_ms) {
        this(SafeHtmlUtils.fromString(message), closable, timeout_ms);
    }

    @Override
    public String getPanelStyle() {
        return appearance.panelErrorStyle();
    }

    /**
     * @return The given message as an HTML widget, for display by an IplantAnnouncement, proceeded by an
     *         error icon.
     */
    @Override
    public IsWidget getWidget() {
        ImageElement imgEl = Document.get().createImageElement();
        imgEl.setSrc(appearance.errorIcon().getSafeUri().asString());

        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.appendHtmlConstant(imgEl.getString());
        sb.appendHtmlConstant("&nbsp;"); //$NON-NLS-1$
        sb.appendHtmlConstant(message.asString());

        return new HTML(sb.toSafeHtml());
    }
}
