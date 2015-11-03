/**
 * 
 */
package org.iplantc.de.client.services;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;
/**
 * @author sriram
 *
 */
public interface CollaboratorsServiceFacade {
    public void searchCollaborators(String term, AsyncCallback<String> callback) ;
      
    public void getCollaborators(AsyncCallback<String> callback);

    public void addCollaborators(JSONObject users, AsyncCallback<String> callback);

    public void removeCollaborators(JSONObject users, AsyncCallback<String> callback);

    public void getUserInfo(List<String> usernames, AsyncCallback<String> callback);

}
