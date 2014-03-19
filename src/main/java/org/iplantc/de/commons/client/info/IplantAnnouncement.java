package org.iplantc.de.commons.client.info;

import com.google.gwt.dom.client.Style.Float;

import com.sencha.gxt.widget.core.client.Popup;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * A Popup for displaying a message by the IplantAnnouncer. Uses an IplantAnnouncementConfig to configure
 * if it's closable, timeout delay, and styles.
 * 
 * @author psarando
 * 
 */
public class IplantAnnouncement extends Popup {

    private final IplantAnnouncementConfig config;
    private final AnnouncementId id;

    private ToolButton closeButton;

    /**
     * Constructs an announcement with message content and styling from the given config.
     * 
     * @see org.iplantc.de.commons.client.info.IplantAnnouncementConfig#getWidget()
     * @param config an IplantAnnouncementConfig that configures the style and message content of this
     *            announcement.
     */
    public IplantAnnouncement(final IplantAnnouncementConfig config) {
        this.config = config;
        id = new AnnouncementId();
        initPanel();
    }

    private void initPanel() {
        final SimpleContainer contentContainer = new SimpleContainer();
        contentContainer.setWidget(config.getWidget());
        contentContainer.addStyleName(config.getContentStyle());

        final CssFloatLayoutContainer layout = new CssFloatLayoutContainer();
        layout.add(contentContainer, new CssFloatData(-1));

        if (config.isClosable()) {
            closeButton = new ToolButton(config.getCloseIconConfig());
            layout.add(closeButton, new CssFloatData(-1));
            closeButton.getElement().getStyle().setFloat(Float.RIGHT);
        }

        setWidget(layout);
        setAutoHide(false);
        addStyleName(config.getPanelStyle());
        setShadow(true);
    }

    public void addCloseButtonHandler(SelectHandler handler) {
        if (closeButton != null && handler != null) {
            closeButton.addSelectHandler(handler);
        }
    }

    public void indicateMore() {
        addStyleName(config.getPanelMultipleStyle());
    }

    public void indicateNoMore() {
        removeStyleName(config.getPanelMultipleStyle());
    }

    public int getTimeOut() {
        return config.getTimeOut();
    }

    /**
     * Returns the identifier for this announcement
     * 
     * @return the identifier
     */
    final AnnouncementId getAnnouncementId() {
        return id;
    }

    /**
     * Indicates whether or not this announcement has a certain id
     * 
     * @param id the id in question
     * 
     * @return true if the announcement has the id, otherwise false.
     */
    final boolean hasId(final AnnouncementId id) {
        return this.id.equals(id);
    }

}
