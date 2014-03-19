package org.iplantc.de.client.services;

import com.google.gwt.i18n.client.Constants;

/**
 * This interface is used to access the endpoint property keys. These keys are resolved to their actual
 * endpoint URL on the server side.
 * 
 * @author jstroot
 * 
 */
public interface Endpoints extends Constants {
    String buckets();

    String filesystemIndex();

    String filesystemIndexStatus();
}
