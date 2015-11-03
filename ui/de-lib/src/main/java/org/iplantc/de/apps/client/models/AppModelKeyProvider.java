package org.iplantc.de.apps.client.models;

import org.iplantc.de.client.models.apps.App;

import com.sencha.gxt.data.shared.ModelKeyProvider;

/**
 * @author jstroot
 */
public class AppModelKeyProvider implements ModelKeyProvider<App> {
    @Override
    public String getKey(App item) {
        return item.getId();
    }
}
