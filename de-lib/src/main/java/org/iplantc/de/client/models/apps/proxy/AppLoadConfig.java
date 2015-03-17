package org.iplantc.de.client.models.apps.proxy;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;

public interface AppLoadConfig extends FilterPagingLoadConfig {
    String getQuery();

    void setQuery(String query);
}