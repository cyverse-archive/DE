package org.iplantc.de.theme.base.client.desktop;

import org.iplantc.de.desktop.client.DesktopView;

import static com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;

public class BaseDesktopAppearance implements DesktopView.DesktopAppearance {

    public interface DesktopResources extends ClientBundle {
        @Source("org/iplantc/de/theme/base/client/desktop/analyses.png")
        ImageResource analysisBtn();

        @Source("org/iplantc/de/theme/base/client/desktop/analyses_hover.png")
        ImageResource analysisBtnHover();

        @Source("org/iplantc/de/theme/base/client/desktop/apps.png")
        ImageResource appBtn();

        @Source("org/iplantc/de/theme/base/client/desktop/apps_hover.png")
        ImageResource appBtnHover();

        @Source("org/iplantc/de/theme/base/client/desktop/Desktop.css")
        DesktopStyles css();

        @Source("org/iplantc/de/theme/base/client/desktop/data.png")
        ImageResource dataBtn();

        @Source("org/iplantc/de/theme/base/client/desktop/data_hover.png")
        ImageResource dataBtnHover();

        @Source("org/iplantc/de/theme/base/client/desktop/background.png")
        @ImageOptions(repeatStyle = ImageResource.RepeatStyle.Horizontal)
        ImageResource desktopBackground();

        @Source("org/iplantc/de/theme/base/client/desktop/de_desktop_background_repeat-all.png")
        @ImageOptions(repeatStyle = ImageResource.RepeatStyle.Both)
        ImageResource desktopBackgroundRepeat();

        @Source("org/iplantc/de/theme/base/client/desktop/de_feedback.png")
        ImageResource feedbackImg();

        @Source("org/iplantc/de/theme/base/client/desktop/forum.png")
        ImageResource forumsImg();

        @Source("org/iplantc/de/theme/base/client/desktop/header_bg.png")
        ImageResource headerBg();

        @Source("org/iplantc/de/theme/base/client/desktop/mini_logo.png")
        ImageResource iplantLogo();

        @Source("org/iplantc/de/theme/base/client/desktop/globe.png")
        ImageResource notificationsImg();

        @Source("org/iplantc/de/theme/base/client/desktop/user.png")
        ImageResource userPrefImg();
    }

    private final DesktopResources resources;
    private final DesktopStyles style;

    public BaseDesktopAppearance(DesktopResources resources) {
        this.resources = resources;
        this.style = this.resources.css();

        style.ensureInjected();
    }

    public BaseDesktopAppearance() {
        this(GWT.<DesktopResources>create(DesktopResources.class));
    }

    @Override
    public IconConfig analysisConfig() {
        return new IconConfig(style.analyses());
    }

    @Override
    public IconConfig appsConfig() {
        return new IconConfig(style.apps());
    }

    @Override
    public IconConfig dataConfig() {
        return new IconConfig(style.data());
    }

    @Override
    public IconConfig feedbackBtnConfig() {
        return new IconConfig(style.feedback());
    }

    @Override
    public IconConfig forumsConfig() {
        return new IconConfig(style.forums());
    }

    @Override
    public IconConfig notificationsConfig() {
        return new IconConfig(style.notification());
    }

    @Override
    public DesktopStyles styles() {
        return style;
    }

    @Override
    public IconConfig userPrefsConfig() {
        return new IconConfig(style.userPrefs());
    }
}
