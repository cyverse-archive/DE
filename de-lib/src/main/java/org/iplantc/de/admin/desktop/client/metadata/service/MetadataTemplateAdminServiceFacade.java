package org.iplantc.de.admin.desktop.client.metadata.service;

import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface MetadataTemplateAdminServiceFacade {

    void getMetadataTemplateListing(AsyncCallback<List<MetadataTemplateInfo>> callback);

    void addTemplate(String template, AsyncCallback<String> callback);

    void deleteTemplate(String id, AsyncCallback<String> callback);
    
    void updateTemplate(String id, String template, AsyncCallback<String> callback);
}
