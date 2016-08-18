package org.iplantc.de.client.models.groups;

import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * @author dennis
 */
public interface MemberSaveResult {

    @AutoBean.PropertyName("failures")
    List<String> getFailures();

    @AutoBean.PropertyName("failures")
    void setFailures(List<String> failures);

    @AutoBean.PropertyName("members")
    List<Member> getMembers();

    @AutoBean.PropertyName("members")
    void setMembers(List<String> members);
}
