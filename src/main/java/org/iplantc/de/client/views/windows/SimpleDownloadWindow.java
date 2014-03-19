package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.SimpleDownloadWindowConfig;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

/**
 * An iPlant window for displaying simple download links.
 * 
 * @author psarando
 * 
 */
public class SimpleDownloadWindow extends IplantWindowBase {

    private final DeResources res = GWT.create(DeResources.class);

    public SimpleDownloadWindow(SimpleDownloadWindowConfig config) {
        super(null, false, true, true, true);
        res.css().ensureInjected();
        setHeadingText(org.iplantc.de.resources.client.messages.I18N.DISPLAY.download());
        setSize("320", "320");

        init(config);
    }

    private void init(SimpleDownloadWindowConfig config) {
        // Add window contents container for the simple download links
        VerticalLayoutContainer contents = new VerticalLayoutContainer();

        contents.add(new Label("" + org.iplantc.de.resources.client.messages.I18N.DISPLAY.simpleDownloadNotice()));
        buildLinks(config, contents);
        add(contents);

    }

    private void buildLinks(SimpleDownloadWindowConfig config, VerticalLayoutContainer vlc) {
        for (final DiskResource dr : config.getResourcesToDownload()) {
            IPlantAnchor link2 = new IPlantAnchor(DiskResourceUtil.parseNameFromPath(dr.getId()), 120, new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    final String encodedSimpleDownloadURL = ServicesInjector.INSTANCE.getDiskResourceServiceFacade().getEncodedSimpleDownloadURL(dr.getId());
                    WindowUtil.open(encodedSimpleDownloadURL, "width=100,height=100");
                }
            });

            vlc.add(link2);
        }
    }

    @Override
    public WindowState getWindowState() {
        SimpleDownloadWindowConfig config = ConfigFactory.simpleDownloadWindowConfig();
        return createWindowState(config);
    }

}
