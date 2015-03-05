package org.iplantc.de.apps.client.views.toolBar;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.commons.client.widgets.SearchField;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

/**
 * A SearchField for the App Catalog main toolbar that performs remote app searches.
 * 
 * @author psarando
 * 
 */
public class AppSearchField extends SearchField<App> {
    public AppSearchField(PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader) {
        super(loader);
    }

    @Override
    protected void clearFilter() {
        // Do not reload.
    }
}
