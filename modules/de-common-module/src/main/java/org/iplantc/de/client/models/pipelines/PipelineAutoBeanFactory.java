package org.iplantc.de.client.models.pipelines;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * A Pipeline AutoBeanFactory.
 * 
 * @author psarando
 * 
 */
public interface PipelineAutoBeanFactory extends AutoBeanFactory {

    AutoBean<Pipeline> pipeline();

    AutoBean<PipelineApp> app();

    AutoBean<PipelineAppMapping> appMapping();

    AutoBean<PipelineAppData> appData();
}
