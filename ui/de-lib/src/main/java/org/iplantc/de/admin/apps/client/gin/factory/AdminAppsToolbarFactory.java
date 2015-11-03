package org.iplantc.de.admin.apps.client.gin.factory;

import org.iplantc.de.admin.apps.client.AdminAppsToolbarView;
import org.iplantc.de.client.models.apps.App;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

/**
 * Created by jstroot on 3/9/15.
 * @author jstroot
 */
public interface AdminAppsToolbarFactory {
    AdminAppsToolbarView create(PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader);
}
