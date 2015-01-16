package org.iplantc.de.admin.desktop.client.gin;

import org.iplantc.de.admin.desktop.client.views.BelphegorView;
import org.iplantc.de.apps.client.gin.AppsGinModule;
import org.iplantc.de.diskResource.client.gin.DiskResourceGinModule;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.tags.client.gin.TagsGinModule;
import org.iplantc.de.tools.requests.client.gin.ToolRequestGinModule;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * @author jstroot
 */
@GinModules({BelphegorAppsGinModule.class,
                DiskResourceGinModule.class,
                TagsGinModule.class,
                AppsGinModule.class,
                ToolRequestGinModule.class})
public interface BelphegorAppInjector extends Ginjector {

    BelphegorView.Presenter getBelphegorPresenter();

    DiscEnvApiService getApiService();
}
