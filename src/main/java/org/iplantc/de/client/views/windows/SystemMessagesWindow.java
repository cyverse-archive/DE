package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.sysmsgs.presenter.MessagesPresenter;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.SystemMessagesWindowConfig;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.Window;

/**
 * The window for displaying all active system messages.
 */
public final class SystemMessagesWindow extends IplantWindowBase {

    private static int computeDefaultWidth() {
        return Math.max(600, Window.getClientWidth() / 3);
    }

    private static int computeDefaultHeight() {
        return Math.max(400, Window.getClientHeight() / 3);
    }

    private final MessagesPresenter presenter;

    /**
     * the constructor
     * 
     * @param config the persisted window configuration
     */
    public SystemMessagesWindow(final SystemMessagesWindowConfig config) {
        super("", config);
        this.presenter = new MessagesPresenter(config.getSelectedMessage());
        setTitle(I18N.DISPLAY.systemMessagesLabel());
        this.setWidth(computeDefaultWidth());
        this.setHeight(computeDefaultHeight());
        presenter.go(this);
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
