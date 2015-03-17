package org.iplantc.de.apps.client.gin.factory;

import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.client.models.apps.App;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

/**
 * Created by jstroot on 3/5/15.
 * @author jstroot
 */
public interface AppsToolbarViewFactory {
    AppsToolbarView create(PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader);
}
