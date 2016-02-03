package org.iplantc.de.desktop.client.views.windows;

import org.iplantc.de.client.models.AboutApplicationData;
import org.iplantc.de.client.models.CommonModelAutoBeanFactory;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.desktop.shared.DeModule;
import org.iplantc.de.shared.services.AboutApplicationServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

/**
 * Models a user interface for "about" application information.
 *
 * @author lenards, jstroot
 */
public class AboutApplicationWindow extends IplantWindowBase {

    /**
     * An Appearance interface for the About Window's contents and layout.
     *
     * @author psarando
     */
    public interface AboutApplicationAppearance {

        SafeHtml about(AboutApplicationData data);

        String headingText();

        ImageResource iplantAbout();

        String iplantcAboutPadText();
    }
    private final AboutApplicationServiceAsync aboutApplicationService;
    private final AboutApplicationAppearance appearance;
    private AboutApplicationData model;

    @Inject
    AboutApplicationWindow(final AboutApplicationServiceAsync aboutApplicationService,
                           final AboutApplicationAppearance appearance) {
        this.aboutApplicationService = aboutApplicationService;
        this.appearance = appearance;
        setSize("320", "260");
        setHeadingText(appearance.headingText());
        ensureDebugId(DeModule.WindowIds.ABOUT_WINDOW);
        executeServiceCall();
    }

    @Override
    public WindowState getWindowState() {
        return createWindowState(ConfigFactory.aboutWindowConfig());
    }

    /**
     * Construct and configure the details container.
     * <p/>
     * This is a panel containing details about the Discovery Environment like the release version, build
     * number, and the user's browser information
     *
     * @return a configured panel containing details information.
     */
    private ContentPanel buildDetailsContainer() {
        ContentPanel pnlDetails = new ContentPanel();
        pnlDetails.setHeaderVisible(false);
        pnlDetails.add(new HTML(appearance.about(model)));
        return pnlDetails;
    }

    private void compose() {
        VerticalLayoutContainer vlc = new VerticalLayoutContainer();
        vlc.setBorders(true);
        vlc.setStyleName(appearance.iplantcAboutPadText());
        Image logo = new Image(appearance.iplantAbout().getSafeUri());
        logo.setHeight("77px");
        logo.setWidth("300px");
        vlc.add(logo);
        vlc.add(buildDetailsContainer());
        add(vlc);
    }

    private void executeServiceCall() {
        aboutApplicationService.getAboutInfo(new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(String result) {
                CommonModelAutoBeanFactory factory = GWT.create(CommonModelAutoBeanFactory.class);
                model = AutoBeanCodex.decode(factory, AboutApplicationData.class, result).as();
                compose();
            }
        });
    }
}
