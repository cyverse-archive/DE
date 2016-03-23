package org.iplantc.de.theme.base.client.desktop;

import static com.google.gwt.resources.client.ImageResource.ImageOptions;

import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;

/**
 * @author jstroot
 */
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
        ImageResource cyverseLogo();

        @Source("org/iplantc/de/theme/base/client/desktop/notification.png")
        ImageResource notificationsImg();

        @Source("org/iplantc/de/theme/base/client/desktop/user.png")
        ImageResource userPrefImg();

        @DataResource.MimeType("font/opentype")
        @Source("org/iplantc/de/theme/base/client/desktop/Texta_Font/Texta-Bold.otf")
        DataResource textaBold();
    }

    private final DesktopResources resources;
    private final DesktopMessages desktopMessages;
    private final DesktopStyles style;
    private final IplantDisplayStrings displayStrings;
    private final DesktopContextualHelpMessages help;

    BaseDesktopAppearance(final DesktopResources resources,
                          final IplantDisplayStrings iplantDisplayStrings,
                          final DesktopMessages desktopMessages,
                          final DesktopContextualHelpMessages desktopContextualHelpMessages) {
        this.resources = resources;
        displayStrings = iplantDisplayStrings;
        this.desktopMessages = desktopMessages;
        this.help = desktopContextualHelpMessages;
        this.style = this.resources.css();

        style.ensureInjected();
    }

    public BaseDesktopAppearance() {
        this(GWT.<DesktopResources>create(DesktopResources.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<DesktopMessages> create(DesktopMessages.class),
             GWT.<DesktopContextualHelpMessages> create(DesktopContextualHelpMessages.class));
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
    public String feedbackAlertValidationWarning() {
        return displayStrings.warning();
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
    public String completeRequiredFieldsError() {
        return displayStrings.completeRequiredFieldsError();
    }

    @Override
    public String rootApplicationTitle(int count) {
        return "(" + count + ") " + displayStrings.rootApplicationTitle();
    }

    @Override
    public String rootApplicationTitle() {
        return displayStrings.rootApplicationTitle();
    }

    @Override
    public DesktopStyles styles() {
        return style;
    }

    @Override
    public IconConfig userPrefsConfig() {
        return new IconConfig(style.userPrefs());
    }

    @Override
    public String notifications() {
        return displayStrings.notifications();
    }

    @Override
    public String preferences() {
        return desktopMessages.preferences();
    }

    @Override
    public String collaborators() {
        return displayStrings.collaborators();
    }

    @Override
    public String systemMessagesLabel() {
        return displayStrings.systemMessagesLabel();
    }

    @Override
    public String introduction() {
        return desktopMessages.introduction();
    }

    @Override
    public String documentation() {
        return displayStrings.documentation();
    }

    @Override
    public String contactSupport() {
        return desktopMessages.contactSupport();
    }

    @Override
    public String about() {
        return desktopMessages.about();
    }

    @Override
    public String logout() {
        return displayStrings.logout();
    }

    @Override
    public String iconHomepageDataTip() {
        return help.iconHomepageDataTip();
    }

    @Override
    public String forums() {
        return desktopMessages.forums();
    }

    @Override
    public String iconHomepageAnalysesTip() {
        return help.iconHomepageAnalysesTip();
    }

    @Override
    public String iconHomepageAppsTip() {
        return help.iconHomepageAppsTip();
    }
}
