package org.iplantc.de.admin.desktop.client.workshopAdmin.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.iplantc.de.client.models.groups.Member;

import java.util.List;

/**
 * @author dennis
 */
public interface WorkshopAdminServiceFacade {

    void getMembers(AsyncCallback<List<Member>> members);
}
