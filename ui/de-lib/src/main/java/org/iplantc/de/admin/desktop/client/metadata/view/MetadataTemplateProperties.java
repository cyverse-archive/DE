package org.iplantc.de.admin.desktop.client.metadata.view;

import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import java.util.Date;

public interface MetadataTemplateProperties extends PropertyAccess<MetadataTemplateInfo> {

    ModelKeyProvider<MetadataTemplateInfo> id();

    ValueProvider<MetadataTemplateInfo, String> name();

    ValueProvider<MetadataTemplateInfo, String> description();

    ValueProvider<MetadataTemplateInfo, Date> createdDate();

    ValueProvider<MetadataTemplateInfo, String> createdBy();

    ValueProvider<MetadataTemplateInfo, Boolean> deleted();
}
