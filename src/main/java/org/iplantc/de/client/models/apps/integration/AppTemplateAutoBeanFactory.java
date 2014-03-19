package org.iplantc.de.client.models.apps.integration;

import org.iplantc.de.client.models.errorHandling.SimpleServiceError;

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

    AutoBean<DataObject> dataObject();

    AutoBean<JobExecution> jobExecution();

    AutoBean<FileInfoType> fileInfoType();

    AutoBean<FileInfoTypeList> fileInfoTypeList();

    AutoBean<DataSource> dataSource();

    AutoBean<DataSourceList> dataSourceList();

    AutoBean<ReferenceGenome> referenceGenome();

    AutoBean<ReferenceGenomeList> referenceGenomeList();

    AutoBean<SimpleServiceError> simpleServiceError();

}
