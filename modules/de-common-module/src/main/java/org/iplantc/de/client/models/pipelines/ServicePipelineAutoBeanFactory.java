package org.iplantc.de.client.models.pipelines;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * An AutoBeanFactory for a generating service Pipeline AutoBeans.
 * 
 * @author psarando
 * 
 */
public interface ServicePipelineAutoBeanFactory extends AutoBeanFactory {
    AutoBean<ImplementorDetails> implementorDetails();

    AutoBean<ImplementorDetailTest> implementorDetailTest();

    AutoBean<ServicePipeline> servicePipeline();

    AutoBean<ServicePipelineAnalysis> servicePipelineAnalysis();

    AutoBean<ServicePipelineStep> servicePipelineStep();

    AutoBean<ServicePipelineStepConfig> servicePipelineMappingConfig();

    AutoBean<ServicePipelineMapping> servicePipelineMapping();

    AutoBean<ServiceSaveResponse> serviceSaveResponse();
}
