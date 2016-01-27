package org.iplantc.de.admin.desktop.client.toolAdmin.model;

import org.iplantc.de.client.models.apps.App;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author jstroot
 * @author aramsey
 */
public interface AppProperties extends PropertyAccess<App> {

    ModelKeyProvider<App> id();

    ValueProvider<App, String> name();

    ValueProvider<App, String> integratorName();

    ValueProvider<App, String> integratorEmail();

    ValueProvider<App, Boolean> isDisabled();
}
