package org.iplantc.de.admin.desktop.client.metadata.service.impl;

import org.iplantc.de.admin.desktop.client.metadata.service.MetadataTemplateAdminServiceFacade;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class MetadataTemplateAdminServiceFacadeImpl implements MetadataTemplateAdminServiceFacade {
    private final DEProperties deProperties;
    private final DiscEnvApiService deServiceFacade;

    @Inject
    public MetadataTemplateAdminServiceFacadeImpl(final DEProperties deProperties,
                                                  final DiscEnvApiService deServiceFacade) {
        this.deServiceFacade = deServiceFacade;
        this.deProperties = deProperties;
    }

    @Override
    public void addTemplate(String template, AsyncCallback<String> callback) {
        String address = deProperties.getDataMgmtAdminBaseUrl() + "metadata/templates";
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, template);
        callService(wrapper, callback);

    }

    @Override
    public void deleteTemplate(String id, AsyncCallback<String> callback) {
        String address = deProperties.getDataMgmtAdminBaseUrl() + "metadata/templates/" + id;
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.DELETE, address);
        callService(wrapper, callback);
    }

    @Override
    public void updateTemplate(String id, String template, AsyncCallback<String> callback) {
        String address = deProperties.getDataMgmtAdminBaseUrl() + "metadata/templates/" + id;
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, template);
        callService(wrapper, callback);
    }

    /**
     * Performs the actual service call.
     * 
     * @param wrapper the wrapper used to get to the actual service via the service proxy.
     * @param callback executed when RPC call completes.
     */
    private void callService(ServiceCallWrapper wrapper, AsyncCallback<String> callback) {
        deServiceFacade.getServiceData(wrapper, callback);
    }

}
