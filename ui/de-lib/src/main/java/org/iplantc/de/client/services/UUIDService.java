package org.iplantc.de.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.ArrayList;

/**
 * @author jstroot
 */
@RemoteServiceRelativePath("uuid.rpc")
public interface UUIDService extends RemoteService {

    ArrayList<String> getUUIDs(int num);
}
