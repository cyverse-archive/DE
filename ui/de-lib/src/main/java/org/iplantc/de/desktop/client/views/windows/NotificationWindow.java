package org.iplantc.de.desktop.client.views.windows;

import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.NotifyWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.desktop.shared.DeModule;
import org.iplantc.de.notifications.client.views.NotificationView;

import com.google.inject.Inject;

/**
 * @author sriram, jstroot
 */
public class NotificationWindow extends IplantWindowBase {

    private NotificationView.Presenter presenter;
    private NotificationView.NotificationViewAppearance appearance;

    @Inject
    NotificationWindow(NotificationView.Presenter presenter,
                       NotificationView.NotificationViewAppearance appearance) {
        this.presenter = presenter;
        this.appearance = appearance;
        setHeadingText(appearance.notifications());
        ensureDebugId(DeModule.WindowIds.NOTIFICATION);
        setSize("600", "375");
    }

    @Override
    public <C extends WindowConfig> void show(C windowConfig, String tag,
                                              boolean isMaximizable) {
        NotifyWindowConfig notifyWindowConfig = (NotifyWindowConfig) windowConfig;

        presenter.go(this);
        if (notifyWindowConfig != null) {
            presenter.filterBy(notifyWindowConfig.getSortCategory());
        }
        super.show(windowConfig, tag, isMaximizable);
    }

    @Override
    public WindowState getWindowState() {
        NotifyWindowConfig config = ConfigFactory.notifyWindowConfig(presenter.getCurrentCategory());
        return createWindowState(config);
    }

}
