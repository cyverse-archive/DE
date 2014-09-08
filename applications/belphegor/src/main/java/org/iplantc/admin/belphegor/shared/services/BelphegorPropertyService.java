package org.iplantc.admin.belphegor.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

import java.util.Map;

/**
 * @author jstroot
 */
@RemoteServiceRelativePath("belphegorProperties")
public interface BelphegorPropertyService extends RemoteService {
    /**
     * Retrieves the entire set of Belphegor properties.
     *
     * @return the set of Belphegor properties.
     */
    Map<String, String> getProperties() throws SerializationException;

}
