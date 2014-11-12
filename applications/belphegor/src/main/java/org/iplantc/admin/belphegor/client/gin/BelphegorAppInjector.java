package org.iplantc.admin.belphegor.client.gin;

import org.iplantc.admin.belphegor.client.BelphegorResources;
import org.iplantc.admin.belphegor.client.views.BelphegorView;
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
