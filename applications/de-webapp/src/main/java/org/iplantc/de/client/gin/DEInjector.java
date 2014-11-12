package org.iplantc.de.client.gin;

import org.iplantc.de.analysis.client.gin.AnalysisGinModule;
import org.iplantc.de.apps.client.gin.AppsGinModule;
import org.iplantc.de.apps.integration.client.gin.AppEditorGinModule;
import org.iplantc.de.apps.widgets.client.gin.AppLaunchGinModule;
import org.iplantc.de.client.desktop.DesktopView;
import org.iplantc.de.diskResource.client.gin.DiskResourceGinModule;
import org.iplantc.de.fileViewers.client.gin.FileViewerGinModule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * Discovery Environment GinJector
 * Created by jstroot on 4/9/14.
 */
@GinModules({DEGinModule.class,
                AnalysisGinModule.class,
                AppLaunchGinModule.class,
                AppsGinModule.class,
                AppEditorGinModule.class,
                DiskResourceGinModule.class,
                FileViewerGinModule.class})
public interface DEInjector extends Ginjector {
    public static final DEInjector INSTANCE = GWT.create(DEInjector.class);

    DesktopView.Presenter getNewDesktopPresenter();

}
