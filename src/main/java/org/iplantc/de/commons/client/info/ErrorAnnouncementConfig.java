package org.iplantc.de.commons.client.info;

import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

public class ErrorAnnouncementConfig extends IplantAnnouncementConfig {

    private final ImageResource errIcon = IplantResources.RESOURCES.exclamation();

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

    @Override
    public String getPanelStyle() {
        return STYLE.panelError();
    }

    /**
     * @return The given message as an HTML widget, for display by an IplantAnnouncement, proceeded by an
     *         error icon.
     */
    @Override
    public IsWidget getWidget() {
        ImageElement imgEl = Document.get().createImageElement();
        imgEl.setSrc(errIcon.getSafeUri().asString());

        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.appendHtmlConstant(imgEl.getString());
        sb.appendHtmlConstant("&nbsp;"); //$NON-NLS-1$
        sb.appendHtmlConstant(message.asString());

        return new HTML(sb.toSafeHtml());
    }
}
