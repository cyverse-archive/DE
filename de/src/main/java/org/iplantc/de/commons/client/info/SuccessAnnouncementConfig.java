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
public class SuccessAnnouncementConfig extends IplantAnnouncementConfig {

    public SuccessAnnouncementConfig(final String message) {
        this(SafeHtmlUtils.fromString(message));
    }

    public SuccessAnnouncementConfig(final SafeHtml message) {
        super(message);
    }

    public SuccessAnnouncementConfig(final SafeHtml message, boolean closable) {
        super(message, closable);
    }

    public SuccessAnnouncementConfig(final SafeHtml message, boolean closable, int timeout_ms) {
        super(message, closable, timeout_ms);
    }

    public SuccessAnnouncementConfig(final String message, boolean closable, int timeout_ms){
        this(SafeHtmlUtils.fromString(message), closable, timeout_ms);
    }

    /**
     * @return The given message as an HTML widget, for display by an IplantAnnouncement, proceeded by a
     *         success icon.
     */
    @Override
    public IsWidget getWidget() {
        ImageElement imgEl = Document.get().createImageElement();
        imgEl.setSrc(appearance.okIcon().getSafeUri().asString());

        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.appendHtmlConstant(imgEl.getString());
        sb.appendHtmlConstant("&nbsp;");
        sb.appendHtmlConstant(message.asString());

        return new HTML(sb.toSafeHtml());
    }

}
