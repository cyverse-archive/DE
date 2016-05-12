package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.avu.AvuAutoBeanFactory;
import org.iplantc.de.client.models.avu.AvuList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

/**
 * @author aramsey
 */
public class AvuListCallbackConverter extends AsyncCallbackConverter<String, List<Avu>> {

    private final AvuAutoBeanFactory factory;

    public AvuListCallbackConverter(AsyncCallback<List<Avu>> callback, AvuAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected List<Avu> convertFrom(String object) {
        final AutoBean<AvuList> decode = AutoBeanCodex.decode(factory, AvuList.class, object);
        return decode.as().getAvus();
    }
}
