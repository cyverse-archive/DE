package org.iplantc.de.admin.desktop.client.metadata.view;

import org.iplantc.de.client.models.diskResources.MetadataTemplateAttribute;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface MetadataTemplateAttributeProperties extends PropertyAccess<MetadataTemplateInfo> {

    ModelKeyProvider<MetadataTemplateAttribute> id();

    ValueProvider<MetadataTemplateAttribute, String> name();
    
    ValueProvider<MetadataTemplateAttribute, String> description();

    ValueProvider<MetadataTemplateAttribute, Boolean> required();

    ValueProvider<MetadataTemplateAttribute, String> type();

}
