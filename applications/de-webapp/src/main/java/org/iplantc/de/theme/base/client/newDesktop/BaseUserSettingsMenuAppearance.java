package org.iplantc.de.theme.base.client.newDesktop;

import org.iplantc.de.client.newDesktop.views.widgets.UserSettingsMenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public class BaseUserSettingsMenuAppearance implements UserSettingsMenu.UserSettingsMenuAppearance {
    public interface UserSettingsMenuResources extends ClientBundle {

        @Source("UserSettingsMenu.css")
        UserSettingsMenuStyles css();
    }

    private final UserSettingsMenuResources resources;
    private final UserSettingsMenuStyles style;

    public BaseUserSettingsMenuAppearance() {
        this(GWT.<UserSettingsMenuResources> create(UserSettingsMenuResources.class));
    }

    public BaseUserSettingsMenuAppearance(UserSettingsMenuResources resources) {
        this.resources = resources;
        this.style = resources.css();
        style.ensureInjected();

    }

    @Override
    public UserSettingsMenuStyles styles() {
        return style;
    }
}
