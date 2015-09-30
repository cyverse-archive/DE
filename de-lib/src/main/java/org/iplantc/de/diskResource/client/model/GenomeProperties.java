package org.iplantc.de.diskResource.client.model;

import org.iplantc.de.client.models.genomes.Genome;

import com.google.gwt.editor.client.Editor.Path;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface GenomeProperties extends PropertyAccess<Genome> {

    ValueProvider<Genome, Integer> id();
    
    ValueProvider<Genome, String> version();

    @Path("sequence_type")
    ValueProvider<Genome, String> sequenceType();

    @Path("organism_id")
    ValueProvider<Genome, Integer> organismId();

    @Path("chromosome_count")
    ValueProvider<Genome, Integer> chromosomeCount();

}
