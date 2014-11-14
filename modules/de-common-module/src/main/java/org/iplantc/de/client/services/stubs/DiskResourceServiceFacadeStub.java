package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceExistMap;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadata;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataTemplate;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.RootFolders;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.services.DiskResourceMove;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.DiskResourceServiceFacade;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;

import java.util.List;
import java.util.Set;

/**
 * @author jstroot
 */
public class DiskResourceServiceFacadeStub implements DiskResourceServiceFacade {

    @Override
    public DiskResource combineDiskResources(DiskResource from, DiskResource into) {
        return null;
    }

    @Override
    public void getRootFolders(AsyncCallback<RootFolders> callback) {

    }

    @Override
    public void getFolderContents(Folder folder, List<InfoType> infoTypeFilterList,
                                  TYPE entityType, FilterPagingLoadConfigBean loadConfig,
                                  AsyncCallback<Folder> callback) {

    }

    @Override
    public void getSubFolders(Folder parent, AsyncCallback<List<Folder>> callback) {

    }

    @Override
    public void createFolder(Folder parentFolder, String newFolderName, AsyncCallback<Folder> callback) {

    }

    @Override
    public void diskResourcesExist(HasPaths diskResourcePaths, AsyncCallback<DiskResourceExistMap> callback) {

    }

    @Override
    public void moveDiskResources(Set<DiskResource> diskResources, Folder destFolder, AsyncCallback<DiskResourceMove> callback) {

    }

    @Override
    public void moveContents(String sourceFolderId, Folder destFolder, AsyncCallback<DiskResourceMove> callback) {

    }

    @Override
    public void renameDiskResource(DiskResource src, String destName, AsyncCallback<DiskResource> callback) {

    }

    @Override
    public void importFromUrl(String url, DiskResource dest, AsyncCallback<String> callback) {

    }

    @Override
    public void upload(AsyncCallback<String> callback) {

    }

    @Override
    public void download(HasPaths paths, AsyncCallback<String> callback) {

    }

    @Override
    public String getEncodedSimpleDownloadURL(String path) {
        return null;
    }

    @Override
    public <T extends DiskResource> void deleteDiskResources(Set<T> diskResources, AsyncCallback<HasPaths> callback) {

    }

    @Override
    public void deleteContents(String selectedFolderId, AsyncCallback<HasPaths> callback) {

    }

    @Override
    public void deleteDiskResources(HasPaths diskResources, AsyncCallback<HasPaths> callback) {

    }

    @Override
    public void getDiskResourceMetaData(DiskResource resource, AsyncCallback<String> callback) {

    }

    @Override
    public void setDiskResourceMetaData(DiskResource resource, Set<DiskResourceMetadata> mdToUpdate, Set<DiskResourceMetadata> mdToDelete, AsyncCallback<String> callback) {

    }

    @Override
    public void shareDiskResource(JSONObject body, AsyncCallback<String> callback) {

    }

    @Override
    public void unshareDiskResource(JSONObject body, AsyncCallback<String> callback) {

    }

    @Override
    public void getPermissions(JSONObject body, AsyncCallback<String> callback) {

    }

    @Override
    public void search(String term, int size, String type, AsyncCallback<String> callback) {

    }

    @Override
    public void getStat(FastMap<TYPE> paths, AsyncCallback<FastMap<DiskResource>> callback) {

    }

    @Override
    public void emptyTrash(String user, AsyncCallback<String> callback) {

    }

    @Override
    public void restoreDiskResource(HasPaths request, AsyncCallback<String> callback) {

    }

    @Override
    public void createDataLinks(List<String> ticketIdList, AsyncCallback<String> callback) {

    }

    @Override
    public void listDataLinks(List<String> diskResourceIds, AsyncCallback<String> callback) {

    }

    @Override
    public void deleteDataLinks(List<String> dataLinkIds, AsyncCallback<String> callback) {

    }

    @Override
    public void getInfoTypes(AsyncCallback<List<InfoType>> callback) {

    }

    @Override
    public void setFileType(String filePath, String type, AsyncCallback<String> callback) {

    }

    @Override
    public DiskResourceAutoBeanFactory getDiskResourceFactory() {
        return null;
    }

    @Override
    public void restoreAll(AsyncCallback<String> callback) {

    }

    @Override
    public void downloadContents(String parentFolderId, AsyncCallback<String> callback) {

    }

    @Override
    public void getMetadataTemplateListing(AsyncCallback<String> callback) {

    }

    @Override
    public void getMetadataTemplate(String templateId, AsyncCallback<String> callback) {

    }

    @Override
    public void shareWithAnonymous(HasPaths diskResourcePaths, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getMetadataTemplateAvus(DiskResource resource,
                                        AsyncCallback<DiskResourceMetadataTemplate> callback) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setMetadataTemplateAvus(DiskResource resource,
                                        DiskResourceMetadataTemplate templateAvus,
                                        AsyncCallback<String> callback) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteMetadataTemplateAvus(DiskResource resource,
                                           DiskResourceMetadataTemplate templateAvus,
                                           AsyncCallback<String> callback) {
        // TODO Auto-generated method stub
    }
}
