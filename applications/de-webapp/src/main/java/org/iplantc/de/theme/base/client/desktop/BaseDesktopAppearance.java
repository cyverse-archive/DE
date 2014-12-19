package org.iplantc.de.theme.base.client.desktop;

import org.iplantc.de.desktop.client.DesktopView;

import static com.google.gwt.resources.client.ImageResource.ImageOptions;
import static com.google.gwt.resources.client.ImageResource.RepeatStyle.Both;
import static com.google.gwt.resources.client.ImageResource.RepeatStyle.Horizontal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;

public class BaseDesktopAppearance implements DesktopView.DesktopAppearance {

    public interface DesktopResources extends ClientBundle {
        @Source("analyses.png")
        ImageResource analysisBtn();

        @Source("analyses_hover.png")
        ImageResource analysisBtnHover();

        @Source("apps.png")
        ImageResource appBtn();

        @Source("apps_hover.png")
        ImageResource appBtnHover();

        @Source("Desktop.css")
        DesktopStyles css();

        @Source("data.png")
        ImageResource dataBtn();

        @Source("data_hover.png")
        ImageResource dataBtnHover();

        @Source("background.png")
        @ImageOptions(repeatStyle = Horizontal)
        ImageResource desktopBackground();

        @Source("de_desktop_background_repeat-all.png")
        @ImageOptions(repeatStyle = Both)
        ImageResource desktopBackgroundRepeat();

        @Source("de_feedback.png")
        ImageResource feedbackImg();

        @Source("forum.png")
        ImageResource forumsImg();

        @Source("header_bg.png")
        ImageResource headerBg();

        @Source("mini_logo.png")
        ImageResource iplantLogo();

        @Source("globe.png")
        ImageResource notificationsImg();

        @Source("user.png")
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
