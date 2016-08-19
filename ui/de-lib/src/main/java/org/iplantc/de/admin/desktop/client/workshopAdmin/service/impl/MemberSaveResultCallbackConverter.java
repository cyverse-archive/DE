package org.iplantc.de.admin.desktop.client.workshopAdmin.service.impl;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import org.iplantc.de.client.models.groups.GroupAutoBeanFactory;
import org.iplantc.de.client.models.groups.MemberSaveResult;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

/**
 * @author dennis
 */
public class MemberSaveResultCallbackConverter extends AsyncCallbackConverter<String, MemberSaveResult> {

    private final GroupAutoBeanFactory factory;

    public MemberSaveResultCallbackConverter(AsyncCallback<MemberSaveResult> callback, GroupAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected MemberSaveResult convertFrom(String object) {
        final AutoBean<MemberSaveResult> decode = AutoBeanCodex.decode(factory, MemberSaveResult.class, object);
        return decode.as();
    }
}
