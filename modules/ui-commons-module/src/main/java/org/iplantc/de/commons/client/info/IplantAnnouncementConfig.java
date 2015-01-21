package org.iplantc.de.commons.client.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;

/**
 * A default config for an IplantAnnouncement that defines attributes such as style and timeout, with a
 * text message to be displayed by the Announcement. By default, the message is closable by the user and
 * will close automatically after 5 seconds.
 * 
 * @author psarando, jstroot
 * 
 */
public class IplantAnnouncementConfig {

    // SS updated to 5s from 10s since 5s is seems too long
    protected static final int DEFAULT_TIMEOUT_ms = 5000;
    protected final IconConfig CLOSER_CFG;

    public interface IplantAnnouncementConfigAppearance {

        String closeButton();

        String closeButtonOver();

        String contentStyle();

        ImageResource errorIcon();

        ImageResource okIcon();

        String panelErrorStyle();

        String panelMultipleStyle();

        String panelStyle();
    }

    protected final SafeHtml message;
    protected final IplantAnnouncementConfigAppearance appearance;
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
    protected IplantAnnouncementConfig(final SafeHtml message,
                                       final boolean closable) {
        this(message, closable, DEFAULT_TIMEOUT_ms);
    }

    public IplantAnnouncementConfig(final SafeHtml message,
                                    final boolean closable,
                                    final int timeout_ms) {
        this(message, closable, timeout_ms,
             GWT.<IplantAnnouncementConfigAppearance> create(IplantAnnouncementConfigAppearance.class));
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
    public IplantAnnouncementConfig(final SafeHtml message,
                                    final boolean closable,
                                    final int timeout_ms,
                                    final IplantAnnouncementConfigAppearance appearance) {
        this.closable = closable;
        this.timeout_ms = timeout_ms;
        this.message = message;
        this.appearance = appearance;
        CLOSER_CFG = new IconConfig(appearance.closeButton(), appearance.closeButtonOver());
    }

    public boolean isClosable() {
        return closable;
    }

    public int getTimeOut() {
        return (!closable && timeout_ms <= 0) ? DEFAULT_TIMEOUT_ms : timeout_ms;
    }

    public String getPanelStyle() {
        return appearance.panelStyle();
    }

    public String getContentStyle() {
        return appearance.contentStyle();
    }

    public String getPanelMultipleStyle() {
        return appearance.panelMultipleStyle();
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
