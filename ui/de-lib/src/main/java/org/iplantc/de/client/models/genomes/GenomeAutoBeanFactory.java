package org.iplantc.de.client.models.genomes;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface GenomeAutoBeanFactory extends AutoBeanFactory {

    AutoBean<GenomeList> getGenomeList();

}
