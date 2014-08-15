package org.iplantc.de.client.windows;

import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.sysmsgs.presenter.MessagesPresenter;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.SystemMessagesWindowConfig;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.shared.DeModule;

import com.google.gwt.user.client.Window;

/**
 * The window for displaying all active system messages.
 */
public final class SystemMessagesWindow extends IplantWindowBase {

    private final IplantDisplayStrings displayStrings;
    private final MessagesPresenter presenter;

    /**
     * the constructor
     *
     * @param config the persisted window configuration
     */
    public SystemMessagesWindow(final SystemMessagesWindowConfig config) {
        super("", config);
        displayStrings = I18N.DISPLAY;
        this.presenter = new MessagesPresenter(config.getSelectedMessage());

        ensureDebugId(DeModule.WindowIds.SYSTEM_MESSAGES);
        setHeadingText(displayStrings.systemMessagesLabel());
        this.setWidth(computeDefaultWidth());
        this.setHeight(computeDefaultHeight());

        presenter.go(this);
    }

    private static int computeDefaultHeight() {
        return Math.max(400, Window.getClientHeight() / 3);
    }

    private static int computeDefaultWidth() {
        return Math.max(600, Window.getClientWidth() / 3);
    }

    /**
     * @see IplantWindowBase#getWindowState()
     */
    @Override
    public WindowState getWindowState() {
        final String selMsg = presenter.getSelectedMessageId();
        return createWindowState(ConfigFactory.systemMessagesWindowConfig(selMsg));
    }

    /**
     * @see IplantWindowBase#doHide()
     */
    @Override
    protected void doHide() {
        presenter.stop();
        super.doHide();
    }

}
