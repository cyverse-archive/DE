package org.iplantc.de.apps.integration.client.view.propertyEditors;

import org.iplantc.de.client.models.apps.integration.DataSource;
import org.iplantc.de.client.models.apps.integration.DataSourceEnum;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface DataSourceProperties extends PropertyAccess<DataSource> {

    ModelKeyProvider<DataSource> id();

    LabelProvider<DataSource> label();

    ValueProvider<DataSource, DataSourceEnum> type();
}
