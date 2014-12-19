package org.iplantc.de.admin.desktop.client.refGenome.view;

import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import java.util.Date;

public interface ReferenceGenomeProperties extends PropertyAccess<ReferenceGenome> {

    ModelKeyProvider<ReferenceGenome> id();

    ValueProvider<ReferenceGenome, String> name();

    ValueProvider<ReferenceGenome, String> path();

    ValueProvider<ReferenceGenome, Date> createdDate();

    ValueProvider<ReferenceGenome, String> createdBy();

}
