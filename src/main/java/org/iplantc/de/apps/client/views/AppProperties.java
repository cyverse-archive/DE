package org.iplantc.de.apps.client.views;


import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppFeedback;

import com.google.gwt.editor.client.Editor.Path;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface AppProperties extends PropertyAccess<App>{

    ModelKeyProvider<App> id();

    @Path("name")
    LabelProvider<App> nameLabel();

    ValueProvider<App, String> integratorName();

    ValueProvider<App, AppFeedback> rating();

    ValueProvider<App, String> name();

    ValueProvider<App, String> integratorEmail();

    ValueProvider<App, Boolean> disabled();

    ValueProvider<App, String> description();

    ValueProvider<App, String> wikiUrl();

}
