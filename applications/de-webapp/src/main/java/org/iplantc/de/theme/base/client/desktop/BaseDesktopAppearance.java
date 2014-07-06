package org.iplantc.de.theme.base.client.desktop;

import org.iplantc.de.client.newDesktop.NewDesktopView.DesktopAppearance;

import static com.google.gwt.resources.client.ImageResource.ImageOptions;
import static com.google.gwt.resources.client.ImageResource.RepeatStyle.Both;
import static com.google.gwt.resources.client.ImageResource.RepeatStyle.Horizontal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;

public class BaseDesktopAppearance implements DesktopAppearance {

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
        ImageResource desktopBackground();

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
        return new IconConfig(style.analyses(), style.analysesOver());
    }

    @Override
    public IconConfig appsConfig() {
        return new IconConfig(style.apps(), style.appsOver());
    }

    @Override
    public IconConfig dataConfig() {
        return new IconConfig(style.data(), style.dataOver());
    }

    @Override
    public IconConfig feedbackBtnConfig() {
        return new IconConfig(style.feedback(), style.feedbackOver());
    }

    @Override
    public IconConfig forumsConfig() {
        return new IconConfig(style.forums(), style.forumsOver());
    }

    @Override
    public IconConfig notificationsConfig() {
        return new IconConfig(style.notification(), style.notificationOver());
    }

    @Override
    public DesktopStyles styles() {
        return style;
    }

    @Override
    public IconConfig userPrefsConfig() {
        return new IconConfig(style.userPrefs(), style.userPrefsOver());
    }
}
