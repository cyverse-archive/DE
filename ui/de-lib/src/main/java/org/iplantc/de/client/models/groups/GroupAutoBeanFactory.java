package org.iplantc.de.client.models.groups;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * @author dennis
 */
public interface GroupAutoBeanFactory extends AutoBeanFactory {

    AutoBean<Member> getMember();

    AutoBean<MemberList> getMemberList();

    AutoBean<MemberSaveRequest> getMemberSaveRequest();

    AutoBean<MemberSaveResult> getMemberSaveResult();
}
