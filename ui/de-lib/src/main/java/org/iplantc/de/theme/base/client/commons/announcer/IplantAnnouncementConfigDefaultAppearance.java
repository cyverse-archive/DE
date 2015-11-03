package org.iplantc.de.theme.base.client.commons.announcer;

import org.iplantc.de.commons.client.info.IplantAnnouncementConfig;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author jstroot
 */
public class IplantAnnouncementConfigDefaultAppearance implements IplantAnnouncementConfig.IplantAnnouncementConfigAppearance {

    /**
     * This is the styling applied to the Announcer widget.
     */
    public static interface AnnouncerStyle extends CssResource {

        /**
         * The close button styling when the mouse is not over the button.
         *
         * @return the style name
         */
        String closeButton();

        /**
         * The close button styling when the mouse is over the button.
         *
         * @return the style name
         */
        String closeButtonOver();

        /**
         * The styling applied to the content widget area of the panel.
         *
         * @return the style name
         */
        String content();

        /**
         * The styling applied to the main announcer panel.
         *
         * @return the style name
         */
        String panel();

        /**
         * The styling applied to error announcements.
         *
         * @return the style name
         */
        String panelError();

        /**
         * The additive styling applied to the main announcer panel when there are multiple announcements.
         *
         * @return the additive style name
         */
        String panelMultiple();

    }

    public interface Resources extends ClientBundle {
        @Source("close.png")
        ImageResource close();

        @Source("close_hover.png")
        ImageResource closeHover();

        @Source("Announcer.css")
        AnnouncerStyle css();

        @Source("exclamation.png")
        ImageResource errorIcon();

        @Source("tick.png")
        ImageResource okIcon();
    }

    private final Resources resources;

    public IplantAnnouncementConfigDefaultAppearance() {
        this(GWT.<Resources>create(Resources.class));
    }

    IplantAnnouncementConfigDefaultAppearance(final Resources resources) {
        this.resources = resources;
        this.resources.css().ensureInjected();
    }

    @Override
    public String closeButton() {
        return resources.css().closeButton();
    }

    @Override
    public String closeButtonOver() {
        return resources.css().closeButtonOver();
    }

    @Override
    public String contentStyle() {
        return resources.css().content();
    }

    @Override
    public ImageResource errorIcon() {
        return resources.errorIcon();
    }

    @Override
    public ImageResource okIcon() {
        return resources.okIcon();
    }

    @Override
    public String panelErrorStyle() {
        return resources.css().panelError();
    }

    @Override
    public String panelMultipleStyle() {
        return resources.css().panelMultiple();
    }

    @Override
    public String panelStyle() {
        return resources.css().panel();
    }
}
