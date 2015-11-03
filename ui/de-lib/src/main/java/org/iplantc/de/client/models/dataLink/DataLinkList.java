package org.iplantc.de.client.models.dataLink;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * A convenience bean definition for deserializing lists of <code>DataLink</code>s
 * 
 * @author jstroot
 * 
 */
public interface DataLinkList {

    @PropertyName("tickets")
    List<DataLink> getTickets();

}
