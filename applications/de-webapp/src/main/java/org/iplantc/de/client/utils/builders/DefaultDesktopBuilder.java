package org.iplantc.de.client.utils.builders;

import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.commons.client.CommonUiConstants;

import com.google.gwt.core.client.GWT;

/**
 * Initializes all desktop shortcuts.
 * 
 * @author amuir
 * 
 */
public class DefaultDesktopBuilder extends DesktopBuilder {
    @Override
    protected void buildShortcuts() {
        DeResources res = GWT.create(DeResources.class);
        final CommonUiConstants uiConstants = GWT.create(CommonUiConstants.class);
        res.css().ensureInjected();
        addShortcut(res.css().iplantcMydataShortcut(), "idMydataShortCut", res.css()
                .iplantcMydataShortcutHover(), "1",
                org.iplantc.de.resources.client.messages.I18N.HELP.iconHomepageDataTip(),
                uiConstants.windowTag(), ConfigFactory.diskResourceWindowConfig(false),
                org.iplantc.de.resources.client.messages.I18N.TOUR.introDataWindow());

        addShortcut(res.css().iplantcCatalogShortcut(), "idAppsShortCut", res.css()
                .iplantcCatalogShortcutHover(), "2",
                org.iplantc.de.resources.client.messages.I18N.HELP.iconHomepageAppsTip(),
 uiConstants.windowTag(), //$NON-NLS-1$
                ConfigFactory.appsWindowConfig(),
                org.iplantc.de.resources.client.messages.I18N.TOUR.introAppsWindow());

        addShortcut(res.css().iplantcMyanalysisShortcut(), "idAnalysisShortCut",
                res.css().iplantcMyanalysisShortcutHover(),
 "3", org.iplantc.de.resources.client.messages.I18N.HELP.iconHomepageAnalysesTip(), uiConstants.windowTag(), //$NON-NLS-1$
                ConfigFactory.analysisWindowConfig(),
                org.iplantc.de.resources.client.messages.I18N.TOUR.introAnalysesWindow());
    }
}
