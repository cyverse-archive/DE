package org.iplantc.de.client.services;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FileEditorServiceFacade {

    /**
     * Call service to retrieve the manifest for a requested file
     * 
     * @param idFile desired manifest's file ID (path).
     * @param callback executes when RPC call is complete.
     */
    void getManifest(String idFile, AsyncCallback<String> callback);

    /**
     * Construct a servlet download URL for the given file ID.
     * 
     * @param path the desired file path to be used in the return URL
     * @return a URL for the given file ID.
     */
    String getServletDownloadUrl(String path);

    /**
     * Call service to retrieve data for a requested file
     * 
     * @param idFile file to retrieve raw data from.
     * @param callback executes when RPC call is complete.
     */
    void getData(String url, AsyncCallback<String> callback);
    
   /** Call service to retrieve data chunks for a requested file
    * 
    * @param callback executes when RPC call is complete.
    */
    void  getDataChunk(String url, JSONObject body, AsyncCallback<String> callback);

    /**
     * Get Tree URLs for the given tree's file ID.
     * 
     * @param idFile file ID (path) of the tree.
     * @param refresh discard existing tree and create new one
     * @param callback executes when RPC call is complete.
     */
    void getTreeUrl(String idFile, boolean refresh, AsyncCallback<String> callback);
    
    /**
     * Get genome VIZ URLs for the given  file ID.
     * 
     * @param idFile file ID (path) of the tree.
     * @param callback executes when RPC call is complete.
     */
    void getGenomeVizUrl(String idFile, AsyncCallback<String> callback);
    
    /**
     * Load genome in Coge
     * 
     * @param pathArray
     * @param callback
     */
    void viewGenomes(JSONObject pathArray,AsyncCallback<String> callback);

    void uploadTextAsFile(String destination, String fileContents, boolean newFile, AsyncCallback<String> callback);

}