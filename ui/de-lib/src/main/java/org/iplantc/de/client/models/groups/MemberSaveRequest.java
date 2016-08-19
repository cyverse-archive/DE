package org.iplantc.de.client.models.groups;

import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * @author dennis
 */
public interface MemberSaveRequest {

    @AutoBean.PropertyName("members")
    List<String> getMembers();

    @AutoBean.PropertyName("members")
    void setMembers(List<String> members);
}
