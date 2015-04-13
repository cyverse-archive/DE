package org.iplantc.de.admin.desktop.client.metadata.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MetadataTemplateAdminServiceFacade {

    void addTemplate(String template, AsyncCallback<String> callback);

    void deleteTemplate(String id, AsyncCallback<String> callback);
    
    void updateTemplate(String id, String template, AsyncCallback<String> callback);
}
