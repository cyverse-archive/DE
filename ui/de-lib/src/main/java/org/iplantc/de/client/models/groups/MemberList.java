package org.iplantc.de.client.models.groups;

import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * @author dennis
 */
public interface MemberList {

    @AutoBean.PropertyName("members")
    List<Member> getMembers();

    @AutoBean.PropertyName("members")
    void setMembers(List<Member> members);
}
