package org.iplantc.de.apps.client.gin;

import org.iplantc.de.apps.client.models.AppModelKeyProvider;
import org.iplantc.de.client.models.apps.App;

import com.google.inject.Provider;

import com.sencha.gxt.data.shared.ListStore;

/**
 * @author jstroot
 */
public class AppListStoreProvider implements Provider<ListStore<App>> {
    @Override
    public ListStore<App> get() {
        return new ListStore<>(new AppModelKeyProvider());
    }
}
