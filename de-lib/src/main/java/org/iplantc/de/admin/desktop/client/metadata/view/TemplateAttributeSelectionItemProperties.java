package org.iplantc.de.admin.desktop.client.metadata.view;

import org.iplantc.de.client.models.diskResources.TemplateAttributeSelectionItem;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface TemplateAttributeSelectionItemProperties extends
                                                         PropertyAccess<TemplateAttributeSelectionItem> {

    ValueProvider<TemplateAttributeSelectionItem, String> value();

    ValueProvider<TemplateAttributeSelectionItem, Boolean> defaultValue();

}
