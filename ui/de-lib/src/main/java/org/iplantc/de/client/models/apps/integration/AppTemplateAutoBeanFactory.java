package org.iplantc.de.client.models.apps.integration;

import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenomeList;
import org.iplantc.de.client.models.errorHandling.SimpleServiceError;
import org.iplantc.de.client.services.impl.models.AnalysisSubmissionResponse;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface AppTemplateAutoBeanFactory extends AutoBeanFactory {
    
    AutoBean<AppTemplate> appTemplate();
    
    AutoBean<ArgumentGroup> argumentGroup();
    
    AutoBean<Argument> argument();

    AutoBean<ArgumentValidator> argumentValidator();

    AutoBean<SelectionItem> selectionItem();

    AutoBean<SelectionItemList> selectionItemList();

    AutoBean<SelectionItemGroup> selectionItemGroup();

    AutoBean<FileParameters> fileParameters();

    AutoBean<JobExecution> jobExecution();

    AutoBean<FileInfoType> fileInfoType();

    AutoBean<FileInfoTypeList> fileInfoTypeList();

    AutoBean<DataSource> dataSource();

    AutoBean<DataSourceList> dataSourceList();

    AutoBean<ReferenceGenome> referenceGenome();

    AutoBean<ReferenceGenomeList> referenceGenomeList();

    AutoBean<SimpleServiceError> simpleServiceError();

    AutoBean<AnalysisSubmissionResponse> analysisSubmissionResponse();
}
