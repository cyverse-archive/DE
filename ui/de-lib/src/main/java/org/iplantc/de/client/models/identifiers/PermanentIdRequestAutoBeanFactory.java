package org.iplantc.de.client.models.identifiers;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * 
 * 
 * @author sriram
 * 
 */
public interface PermanentIdRequestAutoBeanFactory extends AutoBeanFactory {

    AutoBean<PermanentIdRequest> getRequest();

    AutoBean<PermanentIdRequestList> getAllRequests();

    AutoBean<PermanentIdRequestUpdate> getStatus();

    AutoBean<PermanentIdRequestDetails> getDetails();
}
