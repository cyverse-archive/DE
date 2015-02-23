package org.iplantc.de.apps.client.models;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppFeedback;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author jstroot
 */
public interface AppProperties extends PropertyAccess<App>{

    ModelKeyProvider<App> id();

    ValueProvider<App, String> integratorName();

    ValueProvider<App, AppFeedback> rating();

    ValueProvider<App, String> name();

    ValueProvider<App, String> integratorEmail();

    ValueProvider<App, Boolean> disabled();

    ValueProvider<App, String> description();

}
