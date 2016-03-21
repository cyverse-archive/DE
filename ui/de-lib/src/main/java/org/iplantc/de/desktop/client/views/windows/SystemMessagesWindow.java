package org.iplantc.de.desktop.client.views.windows;

import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.SystemMessagesWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.desktop.shared.DeModule;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.systemMessages.client.presenter.MessagesPresenter;
import org.iplantc.de.systemMessages.shared.SystemMessages;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;

/**
 * The window for displaying all active system messages.
 * @author jstroot
 */
public final class SystemMessagesWindow extends IplantWindowBase {

    private MessagesPresenter presenter;

    @Inject
    SystemMessagesWindow(final IplantDisplayStrings displayStrings) {
        setHeadingText(displayStrings.systemMessagesLabel());
        setWidth(computeDefaultWidth());
        setHeight(computeDefaultHeight());
    }

    @Override
    public <C extends WindowConfig> void show(C windowConfig, String tag,
                                              boolean isMaximizable) {
        this.presenter = new MessagesPresenter(((SystemMessagesWindowConfig)windowConfig).getSelectedMessage());
        presenter.go(this);
        super.show(windowConfig, tag, isMaximizable);
        ensureDebugId(DeModule.WindowIds.SYSTEM_MESSAGES);
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

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        presenter.setViewDebugId(baseID + SystemMessages.Ids.VIEW);
    }
}
