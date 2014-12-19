package org.iplantc.de.admin.desktop.client.gin;

import org.iplantc.de.admin.desktop.client.BelphegorResources;
import org.iplantc.de.admin.desktop.client.views.BelphegorView;
import org.iplantc.de.apps.client.gin.AppsGinModule;
import org.iplantc.de.diskResource.client.gin.DiskResourceGinModule;
import org.iplantc.de.shared.services.DiscEnvApiService;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules({BelphegorAppsGinModule.class,
                DiskResourceGinModule.class,
                AppsGinModule.class})
public interface BelphegorAppInjector extends Ginjector {

    BelphegorView.Presenter getBelphegorPresenter();

    DiscEnvApiService getApiService();

    BelphegorResources getResources();
}
