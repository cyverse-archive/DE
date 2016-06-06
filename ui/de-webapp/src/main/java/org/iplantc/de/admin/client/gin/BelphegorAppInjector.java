package org.iplantc.de.admin.client.gin;

import org.iplantc.de.admin.apps.client.gin.AdminAppsGinModule;
import org.iplantc.de.admin.desktop.client.gin.BelphegorAppsGinModule;
import org.iplantc.de.admin.desktop.client.ontologies.gin.OntologiesGinModule;
import org.iplantc.de.admin.desktop.client.toolAdmin.gin.ToolAdminGinModule;
import org.iplantc.de.admin.desktop.client.views.BelphegorView;
import org.iplantc.de.commons.client.comments.gin.CommentsGinModule;
import org.iplantc.de.diskResource.client.gin.DiskResourceGinModule;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.tags.client.gin.TagsGinModule;
import org.iplantc.de.tools.requests.client.gin.ToolRequestGinModule;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * @author jstroot
 */
//                AppsGinModule.class,
@GinModules({ BelphegorAppsGinModule.class,
              AdminAppsGinModule.class,
              OntologiesGinModule.class,
              DiskResourceGinModule.class,
              CommentsGinModule.class,
              TagsGinModule.class,
              ToolRequestGinModule.class,
              ToolAdminGinModule.class })
public interface BelphegorAppInjector extends Ginjector {

    BelphegorView.Presenter getBelphegorPresenter();

    DiscEnvApiService getApiService();
}
