package org.iplantc.de.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * Async counterpart of <code>UUIDService</code>
 * 
 * @author jstroot
 *
 */
public interface UUIDServiceAsync {

    void getUUIDs(int num, AsyncCallback<List<String>> callback);

}
