package org.iplantc.de.admin.desktop.client.metadata.service;

import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MetadataTemplateAdminServiceFacade {

    void addTemplate(MetadataTemplateInfo template, AsyncCallback<String> callback);

    void deleteTemplate(String id, AsyncCallback<String> callback);
    
    void updateTemplate(MetadataTemplateInfo template, AsyncCallback<String> callback);

}
