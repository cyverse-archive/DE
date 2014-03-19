package org.iplantc.de.apps.integration.client.dialogs;

import org.iplantc.de.client.models.apps.integration.Argument;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface ArgumentProperties extends PropertyAccess<Argument> {

    ValueProvider<Argument, String> name();

    ValueProvider<Argument, Integer> order();

    ModelKeyProvider<Argument> id();
}
