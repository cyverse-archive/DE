package org.iplantc.de.admin.desktop.client.metadata.service.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;

import org.iplantc.de.admin.desktop.client.metadata.service.MetadataTemplateAdminServiceFacade;
import org.iplantc.de.shared.DEProperties;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfoList;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

public class MetadataTemplateAdminServiceFacadeImpl implements MetadataTemplateAdminServiceFacade {
    private final DEProperties deProperties;
    private final DiscEnvApiService deServiceFacade;
    private final DiskResourceAutoBeanFactory factory;

    @Inject
    public MetadataTemplateAdminServiceFacadeImpl(final DEProperties deProperties,
                                                  final DiscEnvApiService deServiceFacade,
                                                  final DiskResourceAutoBeanFactory factory) {
        this.deServiceFacade = deServiceFacade;
        this.deProperties = deProperties;
        this.factory = factory;
    }

    @Override
    public void getMetadataTemplateListing(AsyncCallback<List<MetadataTemplateInfo>> callback) {
        String address = deProperties.getDataMgmtAdminBaseUrl() + "metadata/templates";
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        callService(wrapper, new AsyncCallbackConverter<String, List<MetadataTemplateInfo>>(callback) {
            @Override
            protected List<MetadataTemplateInfo> convertFrom(String object) {
                MetadataTemplateInfoList templateInfoList = AutoBeanCodex.decode(factory, MetadataTemplateInfoList.class, object).as();
                return templateInfoList.getTemplates();
            }
        });
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
