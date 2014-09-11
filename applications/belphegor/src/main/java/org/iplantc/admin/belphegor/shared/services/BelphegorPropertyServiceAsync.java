package org.iplantc.admin.belphegor.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Map;

/**
 * @author jstroot
 */
public interface BelphegorPropertyServiceAsync {

    void getProperties(AsyncCallback<Map<String, String>> async);

}
