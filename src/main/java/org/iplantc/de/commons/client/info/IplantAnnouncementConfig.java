package org.iplantc.de.commons.client.info;

import org.iplantc.de.resources.client.AnnouncerStyle;
import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;

/**
 * A default config for an IplantAnnouncement that defines attributes such as style and timeout, with a
 * text message to be displayed by the Announcement. By default, the message is closable by the user and
 * will close automatically after 10 seconds.
 * 
 * @author psarando
 * 
 */
public class IplantAnnouncementConfig {
    protected static final int DEFAULT_TIMEOUT_ms = 10000;
    protected static final AnnouncerStyle STYLE;
    protected static final IconConfig CLOSER_CFG;

    static {
        STYLE = IplantResources.RESOURCES.getAnnouncerStyle();
        STYLE.ensureInjected();
        CLOSER_CFG = new IconConfig(STYLE.closeButton(), STYLE.closeButtonOver());
    }

    protected final SafeHtml message;
    private final boolean closable;
    private final int timeout_ms;

    /**
     * Constructs a closable announcement config that will automatically close after 10 seconds.
     * 
     * @param message
     */
    public IplantAnnouncementConfig(final String message) {
        this(SafeHtmlUtils.fromString(message));
    }

    /**
     * Constructs a closable announcement config that will automatically close after 10 seconds.
     * 
     * @param message
     */
    public IplantAnnouncementConfig(final SafeHtml message) {
        this(message, true, DEFAULT_TIMEOUT_ms);
    }

    /**
     * Constructs an announcement config that will automatically close after 10 seconds.
     * 
     * @param message
     * @param closable
     */
    public IplantAnnouncementConfig(final SafeHtml message, boolean closable) {
        this(message, closable, DEFAULT_TIMEOUT_ms);
    }

    /**
     * Constructs an announcement config. Setting a timeout of 0 or less will cause the message to not
     * close automatically.
     * 
     * If the closable flag is set to false, the message must close automatically. In this case, if the
     * provided timeout is 0 or less, the message will close automatically after 10 seconds.
     * 
     * @param message
     * @param closable
     * @param timeout_ms
     */
    public IplantAnnouncementConfig(final SafeHtml message, boolean closable, int timeout_ms) {
        this.closable = closable;
        this.timeout_ms = timeout_ms;
        this.message = message;
    }

    public boolean isClosable() {
        return closable;
    }

    public int getTimeOut() {
        return (!closable && timeout_ms <= 0) ? DEFAULT_TIMEOUT_ms : timeout_ms;
    }

    public String getPanelStyle() {
        return STYLE.panel();
    }

    public String getContentStyle() {
        return STYLE.content();
    }

    public String getPanelMultipleStyle() {
        return STYLE.panelMultiple();
    }

    public IconConfig getCloseIconConfig() {
        return CLOSER_CFG;
    }

    /**
     * @return The given message as an HTML widget, for display by an IplantAnnouncement.
     */
    public IsWidget getWidget() {
        return new HTML(message);
    }
}
