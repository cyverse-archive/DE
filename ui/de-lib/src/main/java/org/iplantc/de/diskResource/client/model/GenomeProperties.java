/**
 * @author sriram
 * 
 */
package org.iplantc.de.diskResource.client.model;

import org.iplantc.de.client.models.genomes.Genome;

import com.google.gwt.editor.client.Editor.Path;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface GenomeProperties extends PropertyAccess<Genome> {

    ValueProvider<Genome, Integer> id();

    ValueProvider<Genome, String> version();

    ValueProvider<Genome, Integer> organismId();

    ValueProvider<Genome, Integer> chromosomeCount();

    @Path("organism.name")
    ValueProvider<Genome, String> organismName();

    @Path("sequenceType.name")
    ValueProvider<Genome, String> sequenceName();
}
