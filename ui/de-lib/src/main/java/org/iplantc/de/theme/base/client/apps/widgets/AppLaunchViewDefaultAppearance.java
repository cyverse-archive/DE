package org.iplantc.de.theme.base.client.apps.widgets;

import org.iplantc.de.apps.widgets.client.view.AppLaunchView;

import com.google.gwt.core.client.GWT;

/**
 * @author aramsey
 */
public class AppLaunchViewDefaultAppearance implements AppLaunchView.AppLaunchViewAppearance {

    private AppLaunchViewDisplayStrings displayStrings;

    public AppLaunchViewDefaultAppearance() {
        this(GWT.<AppLaunchViewDisplayStrings> create(AppLaunchViewDisplayStrings.class));
    }

    public AppLaunchViewDefaultAppearance(AppLaunchViewDisplayStrings displayStrings) {
        this.displayStrings = displayStrings;
    }

    @Override
    public String deprecatedAppMask() {
        return displayStrings.deprecatedAppMask();
    }
}
