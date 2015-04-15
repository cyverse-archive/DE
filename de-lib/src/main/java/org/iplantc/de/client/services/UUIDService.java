package org.iplantc.de.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

/**
 * @author jstroot
 */
@RemoteServiceRelativePath("uuid.rpc")
public interface UUIDService extends RemoteService {

    List<String> getUUIDs(int num);
}
