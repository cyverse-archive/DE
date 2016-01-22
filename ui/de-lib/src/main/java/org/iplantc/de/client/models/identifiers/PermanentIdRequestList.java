package org.iplantc.de.client.models.identifiers;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

public interface PermanentIdRequestList {

    @PropertyName("requests")
    List<PermanentIdRequest> getRequests();

}
