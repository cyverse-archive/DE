package org.iplantc.de.admin.desktop.client.workshopAdmin.model;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import org.iplantc.de.client.models.groups.Member;

/**
 * @author dennis
 */
public interface MemberProperties {

    ModelKeyProvider<Member> id();

    ValueProvider<Member, String> name();

    ValueProvider<Member, String> email();

    ValueProvider<Member, String> institution();
}
