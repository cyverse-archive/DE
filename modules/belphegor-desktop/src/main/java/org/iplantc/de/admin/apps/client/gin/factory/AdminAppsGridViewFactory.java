package org.iplantc.de.admin.apps.client.gin.factory;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.client.models.apps.App;

import com.sencha.gxt.data.shared.ListStore;

/**
 * Created by jstroot on 3/9/15.
 * @author jstroot
 */
public interface AdminAppsGridViewFactory {
    AdminAppsGridView create(ListStore<App> listStore);
}
