package org.iplantc.de.admin.desktop.client.workshopAdmin.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.iplantc.de.client.models.groups.Member;
import org.iplantc.de.client.models.groups.MemberSaveResult;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import java.util.List;

/**
 * @author dennis
 */
public interface WorkshopAdminServiceFacade {

    void getMembers(AsyncCallback<List<Member>> callback);

    void saveMembers(List<Member> members, AsyncCallback<MemberSaveResult> callback);
}
