package org.iplantc.de.client.models;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * Interface definition for POJOs which have an ID property. 
 * 
 * This class is often used with autobeans.
 * 
 * @author jstroot
 *
 */
public interface HasId {
    
    @PropertyName("id")
    String getId();

}
