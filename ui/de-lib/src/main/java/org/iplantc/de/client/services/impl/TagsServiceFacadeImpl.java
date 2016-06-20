package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;
import org.iplantc.de.shared.DEProperties;
import org.iplantc.de.client.models.tags.IplantTagAutoBeanFactory;
import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.client.services.TagsServiceFacade;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.client.services.converters.StringToVoidCallbackConverter;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;

/**
 * @author jstroot
 */
public class TagsServiceFacadeImpl implements TagsServiceFacade {
    
    private final DEProperties deProps;
    private final DiscEnvApiService deServiceFacade;
    IplantTagAutoBeanFactory factory = GWT.create(IplantTagAutoBeanFactory.class);

    @Inject
    public TagsServiceFacadeImpl(final DiscEnvApiService deServiceFacade, final DEProperties deProps) {
        this.deServiceFacade = deServiceFacade;
        this.deProps = deProps;
    }

    @Override
    public void createTag(final String tagText, AsyncCallback<Tag> callback) {
       String address = deProps.getMuleServiceBaseUrl() + "tags/user";
        final AutoBean<Tag> tag = factory.getTag();
        tag.as().setValue(tagText);
        Splittable json = AutoBeanCodex.encode(tag);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, json.getPayload());
        callService(wrapper, new AsyncCallbackConverter<String, Tag>(callback) {
            @Override
            protected Tag convertFrom(String object) {
                final Tag newTag = AutoBeanCodex.decode(factory, Tag.class, object).as();
                newTag.setValue(tagText);
                // FIXME Service Should return whole tag object
                return newTag;
            }
        });
    }

    @Override
    public void suggestTag(String text, int limit, AsyncCallback<String> callback) {
        String address = deProps.getMuleServiceBaseUrl() + "tags/suggestions?contains=" + URL.encode(text) + "&limit=" + limit;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
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

    @Override
    public void updateTagDescription(Tag tag, AsyncCallback<Void> callback) {
        String address = deProps.getMuleServiceBaseUrl() + "tags/user/" + tag.getId();
        JSONObject obj = new JSONObject();
        if (tag.getDescription() != null) {
            obj.put("description", new JSONString(tag.getDescription()));
            ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.PATCH, address, obj.toString());
            callService(wrapper, new StringToVoidCallbackConverter(callback));
        }

    }

}
