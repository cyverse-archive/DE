package org.iplantc.de.apps.client.models;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppFeedback;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author jstroot
 */
public interface AppProperties extends PropertyAccess<App>{

    ValueProvider<App, String> name();

    ValueProvider<App, String> integratorName();

    ValueProvider<App, AppFeedback> rating();
}
