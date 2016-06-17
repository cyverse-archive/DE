package org.iplantc.de.diskResource.client.model;

import org.iplantc.de.client.models.avu.Avu;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface DiskResourceMetadataProperties extends PropertyAccess<Avu> {

    ValueProvider<Avu, String> attribute();

    ValueProvider<Avu, String> value();

    ValueProvider<Avu, String> unit();

}
