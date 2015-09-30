package org.iplantc.de.client.models.genomes;

import org.iplantc.de.client.models.HasDescription;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface Genome extends HasName, HasDescription {

    Integer getId();

    String getLink();

    String getVersion();

    @PropertyName("organism_id")
    Integer getOrganismId();

    @PropertyName("sequence_type")
    SequenceType getSequenceType();

    @PropertyName("chromosome_count")
    Integer getChromosomeCount();

    @PropertyName("info")
    String getInfo();

    Organism getOrganism();

}
