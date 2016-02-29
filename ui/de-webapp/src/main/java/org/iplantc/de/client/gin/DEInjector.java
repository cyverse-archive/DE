package org.iplantc.de.client.gin;

import org.iplantc.de.analysis.client.gin.AnalysisGinModule;
import org.iplantc.de.apps.client.gin.AppsGinModule;
import org.iplantc.de.apps.integration.client.gin.AppEditorGinModule;
import org.iplantc.de.apps.widgets.client.gin.AppLaunchGinModule;
import org.iplantc.de.commons.client.comments.gin.CommentsGinModule;
import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.desktop.client.gin.DEGinModule;
import org.iplantc.de.diskResource.client.gin.DiskResourceGinModule;
import org.iplantc.de.fileViewers.client.gin.FileViewerGinModule;
import org.iplantc.de.notifications.client.gin.NotificationGinModule;
import org.iplantc.de.tags.client.gin.TagsGinModule;
import org.iplantc.de.tools.requests.client.gin.ToolRequestGinModule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * Discovery Environment Ginjector
 * @author jstroot
 */
@GinModules({DEGinModule.class,
                AnalysisGinModule.class,
                AppLaunchGinModule.class,
                AppsGinModule.class,
                ToolRequestGinModule.class,
                AppEditorGinModule.class,
                DiskResourceGinModule.class,
                CommentsGinModule.class,
                TagsGinModule.class,
                FileViewerGinModule.class,
                NotificationGinModule.class})
public interface DEInjector extends Ginjector {
    public static final DEInjector INSTANCE = GWT.create(DEInjector.class);

    DesktopView.Presenter getNewDesktopPresenter();

}
