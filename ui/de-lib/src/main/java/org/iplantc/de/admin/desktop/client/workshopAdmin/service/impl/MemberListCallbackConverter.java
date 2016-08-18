package org.iplantc.de.admin.desktop.client.workshopAdmin.service.impl;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import org.iplantc.de.client.models.groups.GroupAutoBeanFactory;
import org.iplantc.de.client.models.groups.Member;
import org.iplantc.de.client.models.groups.MemberList;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import java.util.List;

/**
 * @author dennis
 */
public class MemberListCallbackConverter extends AsyncCallbackConverter<String, List<Member>> {

    private final GroupAutoBeanFactory factory;

    public MemberListCallbackConverter(AsyncCallback<List<Member>> callback, GroupAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected List<Member> convertFrom(String object) {
        final AutoBean<MemberList> decode = AutoBeanCodex.decode(factory, MemberList.class, object);
        return decode.as().getMembers();
    }
}
