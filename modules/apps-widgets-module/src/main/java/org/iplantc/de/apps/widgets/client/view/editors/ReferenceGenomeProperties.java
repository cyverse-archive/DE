package org.iplantc.de.apps.widgets.client.view.editors;

import org.iplantc.de.client.models.apps.integration.ReferenceGenome;

import com.google.gwt.editor.client.Editor.Path;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface ReferenceGenomeProperties extends PropertyAccess<ReferenceGenome> {

    ModelKeyProvider<ReferenceGenome> id();

    LabelProvider<ReferenceGenome> name();

    @Path("name")
    ValueProvider<ReferenceGenome, String> nameValue();

}
