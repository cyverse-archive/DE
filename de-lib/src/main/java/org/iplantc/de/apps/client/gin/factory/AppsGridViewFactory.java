package org.iplantc.de.apps.client.gin.factory;

import org.iplantc.de.apps.client.AppsGridView;
import org.iplantc.de.client.models.apps.App;

import com.sencha.gxt.data.shared.ListStore;

/**
 * Created by jstroot on 3/5/15.
 * @author jstroot
 */
public interface AppsGridViewFactory {
    AppsGridView create(ListStore<App> listStore);
}
