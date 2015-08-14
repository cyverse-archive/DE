package org.iplantc.de.client.services;

import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceExistMap;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadata;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataTemplate;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.MetadataTemplate;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.client.models.diskResources.RootFolders;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.services.DiskResourceMove;
import org.iplantc.de.client.models.viewer.InfoType;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;

import java.util.List;
import java.util.Set;

/**
 * @author jstroot
 */
public interface DiskResourceServiceFacade {

    void refreshFolder(Folder folder, final AsyncCallback<List<Folder>> callback);

    DiskResource combineDiskResources(DiskResource from, DiskResource into);

    Folder convertToFolder(DiskResource diskResource);

    /**
     * Call service to retrieve the root folder info for the current user
     * 
     * @param callback executed when RPC call completes.
     */
    void getRootFolders(AsyncCallback<RootFolders> callback);

    /**
     * Called to retrieve the entire contents of a folder.
     * 
     * @param folder the folder whose contents are to be retrieved
     * @param infoTypeFilterList a list of <code>InfoType</code>s to filter the results by.
     * @param entityType used to specify if the results should contain only file, folders, or all.
     *            Defaults to all.
     * @param loadConfig the paged load config which contains all parameters necessary to construct a
     *            well-formed paged directory listing request
     * @param callback executed when RPC call completes.
     */
    void getFolderContents(Folder folder,
                           List<InfoType> infoTypeFilterList,
                           TYPE entityType,
                           FilterPagingLoadConfigBean loadConfig,
                           AsyncCallback<Folder> callback);

    /**
     * Called to retrieve the contents of a folder without its file contents.
     * 
     * @param parent requested folder.
     * @param callback executed when RPC call completes.
     */
    void getSubFolders(final Folder parent, final AsyncCallback<List<Folder>> callback);

    /**
     * Call service to create a new folder
     * 
     * @param parentFolder parent folder where the new folder will be created
     * @param callback executed when RPC call completes.
     */
    void createFolder(Folder parentFolder, final String newFolderName, AsyncCallback<Folder> callback);

    /**
     * Check if a list of files or folders exist.
     * 
     * @param diskResourcePaths paths to desired resources.
     * @param callback callback executed when RPC call completes. On success, a map that maps resource
     *            paths to whether or not they exist.
     */
    void diskResourcesExist(HasPaths diskResourcePaths, AsyncCallback<DiskResourceExistMap> callback);

    /**
     * Calls the move folder and move file services for the list of given disk resource ids.
     * 
     * @param diskResources list of file and folder ids to move.
     * @param destFolder the destination folder where the disk resources will be moved.
     */
    void moveDiskResources(final List<DiskResource> diskResources,
                           final Folder destFolder,
                           AsyncCallback<DiskResourceMove> callback);

    /**
     * Calls the move folder and move file services for moving contents of a given folder.
     * 
     * @param sourceFolderId id of the source folder
     * @param destFolder the destination folder where the disk resources will be moved.
     */
    void moveContents(final String sourceFolderId,
                      final Folder destFolder,
                      AsyncCallback<DiskResourceMove> callback);

    /**
     * Call service rename a file or folder.
     * 
     * @param src the disk resource to be renamed.
     * @param destName the new name.
     * @param callback service success/failure callback
     */
    void renameDiskResource(DiskResource src, String destName, AsyncCallback<DiskResource> callback);

    /**
     * Call service to upload a file from a given URL.
     * 
     * @param url the URL to import from.
     * @param dest id of the destination folder.
     * @param callback service success/failure callback
     */
    void importFromUrl(String url, DiskResource dest, AsyncCallback<String> callback);

    /**
     * @param path the path of the file to download.
     * @return the URL encoded simple download address for the given path.
     */
    String getEncodedSimpleDownloadURL(String path);

    /**
     * Call service to delete disk resources (i.e. {@link File}s and {@link Folder}s)
     * 
     * @param diskResources a set of <code>DiskResource</code>s to be deleted
     * @param callback callback executed when service call completes.
     */
    <T extends DiskResource> void deleteDiskResources(List<T> diskResources,
                                                      AsyncCallback<HasPaths> callback);

    /**
     * Call service to delete disk resources in case user selects all items
     * 
     * @param selectedFolderId the folder whose contents will be deleted.
     */
    void deleteContents(String selectedFolderId, AsyncCallback<HasPaths> callback);

    /**
     * Call service to delete disk resources (i.e. {@link File}s and {@link Folder}s)
     * 
     * @param diskResources a set of <code>DiskResource</code>s to be deleted
     * @param callback callback executed when service call completes.
     */
    void deleteDiskResources(HasPaths diskResources, AsyncCallback<HasPaths> callback);

    /**
     * @param resource the <code>DiskResource</code> for which metadata will be retrieved.
     * @param callback callback executed when service call completes.
     */
    void getDiskResourceMetaData(DiskResource resource,
                                 AsyncCallback<List<DiskResourceMetadata>> callback);

    /**
     * Calls service to set disk resource metadata.
     * 
     * @param resource the <code>DiskResource</code> whose metadata will be updated
     * @param mdToUpdate a list of <code>DiskResourceMetadata</code> objects which will be updated
     * @param mdToDelete a list of <code>DiskResourceMetadata</code> objects which will be deleted
     * @param callback executed when the service call completes.
     */
    void setDiskResourceMetaData(DiskResource resource,
                                 Set<DiskResourceMetadata> mdToUpdate,
                                 Set<DiskResourceMetadata> mdToDelete,
                                 AsyncCallback<String> callback);

    /**
     * 
     * Share a resource with give user with permission
     * 
     * 
     * @param body - Post body in JSONObject format
     * @param callback callback object
     */
    void shareDiskResource(JSONObject body, AsyncCallback<String> callback);

    /**
     * UnShare a resource with give user with permission
     * 
     * @param body - Post body in JSONObject format
     * @param callback callback object
     */
    void unshareDiskResource(JSONObject body, AsyncCallback<String> callback);

    /**
     * get user permission info on selected disk resources
     * 
     * @param body - Post body in JSONObject format
     * @param callback callback object
     */
    void getPermissions(JSONObject body, AsyncCallback<String> callback);

    /**
     * search users irods directory structure
     * 
     * @param term search term
     * @param size limit for results to return
     * @param type file or folder
     * @param callback callback object
     */
    void search(String term, int size, String type, AsyncCallback<String> callback);

    /**
     * Get info about a selected file or folder
     * 
     * @param paths the paths to query
     * @param callback callback which returns a map of {@code DiskResource}s keyed by their paths
     */
    void getStat(final FastMap<TYPE> paths, final AsyncCallback<FastMap<DiskResource>> callback);

    /**
     * empty user's trash
     * 
     * @param user the user whose trash will be emptied.
     */
    public void emptyTrash(String user, AsyncCallback<String> callback);

    /**
     * Restore deleted disk resources.
     * 
     * @param request the disk resources to be restored.
     */
    public void restoreDiskResource(HasPaths request, AsyncCallback<String> callback);

    /**
     * Creates a set of public data links for the given disk resources.
     * 
     * @param ticketIdList the id of the disk resource for which the ticket will be created.
     */
    public void createDataLinks(List<String> ticketIdList, AsyncCallback<List<DataLink>> callback);

    /**
     * Requests a listing of all the tickets for the given disk resources.
     * 
     * @param diskResourceIds the disk resources whose tickets will be listed.
     */
    public void listDataLinks(List<String> diskResourceIds, AsyncCallback<String> callback);

    /**
     * Requests that the given Kif Share tickets will be deleted.
     * 
     * @param dataLinkIds the tickets which will be deleted.
     */
    public void deleteDataLinks(List<String> dataLinkIds, AsyncCallback<String> callback);

    /**
     * Get a list of files types recognized
     */
    void getInfoTypes(AsyncCallback<List<InfoType>> callback);

    /**
     * Set type to a file
     * 
     * @param filePath the path of the file whose type will be set
     * @param type the type the file will be set to.
     */
    void setFileType(String filePath, String type, AsyncCallback<String> callback);

    /**
     * Convenience method which returns a valid {@link DiskResourceAutoBeanFactory} instance.
     * 
     * @return a ready to use <code>DiskResourceAutoBeanFactory</code>
     */
    DiskResourceAutoBeanFactory getDiskResourceFactory();

    /**
     * Restore all items in trash to its original location.
     * 
     */
    void restoreAll(AsyncCallback<String> callback);

    /**
     * Method used to retrieve list of metadata templates
     */
    void getMetadataTemplateListing(AsyncCallback<List<MetadataTemplateInfo>> callback);

    /**
     * Method used to retrieve a metadata template
     * 
     * @param templateId id of the template
     */
    void getMetadataTemplate(String templateId, AsyncCallback<MetadataTemplate> callback);

    /**
     * share with anonymous user selected file(s)
     * 
     * @param diskResourcePaths the paths to query
     * @param callback callback object
     */
    void shareWithAnonymous(final HasPaths diskResourcePaths, final AsyncCallback<String> callback);

    void getMetadataTemplateAvus(final DiskResource resource,
                                 final AsyncCallback<DiskResourceMetadataTemplate> callback);

    void setMetadataTemplateAvus(final DiskResource resource,
                                 final DiskResourceMetadataTemplate templateAvus,
                                 final AsyncCallback<String> callback);

    void deleteMetadataTemplateAvus(final DiskResource resource,
                                    final DiskResourceMetadataTemplate templateAvus,
                                    final AsyncCallback<String> callback);

    /**
     * Copy metadata to list of files / folders
     * 
     * @param srcUUID source DR's UUID
     * @param paths destination DR's path to which metadata will be copied.
     * @param override
     * @param callback callback object
     */
            void
            copyMetadata(final String srcUUID,
                          final Splittable paths,
                       boolean override,
                       final AsyncCallback<String> callback);
            
            /**
     * save metadata to a file
     * 
     * @param srcUUID source DR's UUID
     * @param paths where the file will be created
     * @param recursive should recursively store metadata of folder contents
     * @param callback callback object
     */
    void saveMetadata(final String srcUUID,
                      final String path,
                      boolean recursive,
                      final AsyncCallback<String> callback);

    /**
     * 
     * @param parentFolder parent folder under which new folders will be created
     * @param foldersToCreate an array of folder names to be created under parent folder
     * @param callback callback object
     */
    void createNcbiSraFolderStructure(Folder parentFolder,
                                      String[] foldersToCreate,
                                      AsyncCallback<String> callback);
}
