package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.avu.AvuAutoBeanFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * @author aramsey
 */
public class AvuCallbackConverter extends AsyncCallbackConverter<String, Avu> {

    private final AvuAutoBeanFactory factory;

    public AvuCallbackConverter(AsyncCallback<Avu> callback, AvuAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected Avu convertFrom(String object) {
        final AutoBean<Avu> decode = AutoBeanCodex.decode(factory, Avu.class, object);
        return decode.as();
    }
}
