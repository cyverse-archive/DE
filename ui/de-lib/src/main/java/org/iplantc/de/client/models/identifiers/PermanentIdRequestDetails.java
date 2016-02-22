package org.iplantc.de.client.models.identifiers;

import org.iplantc.de.client.models.UserBootstrap;
import org.iplantc.de.client.models.requestStatus.RequestHistory;

import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * 
 * 
 * @author sriram
 * 
 */
public interface PermanentIdRequestDetails {

    String getId();

    String getType();

    String getFolder();

    @AutoBean.PropertyName("requested_by")
    UserBootstrap getRequestor();

    @AutoBean.PropertyName("history")
    List<RequestHistory> getHistory();

}
