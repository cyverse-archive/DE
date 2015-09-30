package org.iplantc.de.client.services;

import org.iplantc.de.client.models.diskResources.File;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FileEditorServiceFacade {

    String COMMA_DELIMITER = ",";
    String TAB_DELIMITER = "\t";
    String SPACE_DELIMITER = " ";

    /**
     * Call service to retrieve the manifest for a requested file
     *  @param file desired manifest's file ID (path).
     * @param callback executes when RPC call is complete.
     */
    void getManifest(File file, AsyncCallback<String> callback);

    /**
     * @return the file identifier string for path-list files.
     */
    String getPathListFileIdentifier();

    /**
     * Construct a servlet download URL for the given file ID.
     * 
     * @param path the desired file path to be used in the return URL
     * @return a URL for the given file ID.
     */
    String getServletDownloadUrl(String path);

    /**
     * Reads a chunk of the given file. The file must be a CSV or TSV file.
     * @param file the CSV file to be read
     * @param delimiter the file's delimiter type
     * @param pageNumber the page number where the requested chunk will begin
     * @param chunkSize the size of the chunk to be read
     * @param callback Where you will find your stuff.
     *                 FIXME improve callback to return a data type. Clients currently have to parse content themselves.
     */
    void readCsvChunk(File file, String delimiter, int pageNumber, long chunkSize, AsyncCallback<String> callback);

    /**
     * Reads a chunk of the given file.
     * @param file the file to be read
     * @param chunkPosition the position where the requested chunk will begin
     * @param chunkSize the size of the chunk to be read
     * @param callback Where you will find your stuff.
     */
    void readChunk(File file, long chunkPosition, long chunkSize, AsyncCallback<String> callback);

    /**
     * Get Tree URLs for the given tree's file ID.
     * 
     * @param pathToFile file ID (path) of the tree.
     * @param refresh discard existing tree and create new one
     * @param callback executes when RPC call is complete.
     */
    void getTreeUrl(String pathToFile, boolean refresh, AsyncCallback<String> callback);
    
    
    /**
     * Load genome in Coge
     * 
     * @param pathArray
     * @param callback
     */
    void loadGenomesInCoge(JSONObject pathArray,AsyncCallback<String> callback);

    void searchGenomesInCoge(String searchTxt, AsyncCallback<String> callback);

    void uploadTextAsFile(String destination, String fileContents, boolean newFile, AsyncCallback<File> callback);
    
    void importGenomeFromCoge(Integer id, boolean notify, AsyncCallback<String> callback);

}