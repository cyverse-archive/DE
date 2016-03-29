package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.ServiceFacadeLoggerConstants.METRIC_TYPE_KEY;
import static org.iplantc.de.shared.ServiceFacadeLoggerConstants.SHARE_EVENT;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.DELETE;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.diskResources.DiskResourcesMovedEvent;
import org.iplantc.de.client.events.diskResources.FolderRefreshedEvent;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.dataLink.DataLinkList;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceExistMap;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadata;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataList;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataTemplate;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.MetadataTemplate;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfoList;
import org.iplantc.de.client.models.diskResources.RootFolders;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.services.DiskResourceMove;
import org.iplantc.de.client.models.services.DiskResourceRename;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.client.services.impl.models.DiskResourceMetadataBatchRequest;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Provides access to remote services for folder operations.
 * 
 * @author amuir, jstroot
 * 
 */
public class DiskResourceServiceFacadeImpl extends TreeStore<Folder> implements
                                                                    DiskResourceServiceFacade {

    private final DiskResourceAutoBeanFactory factory;
    private final DEProperties deProperties;
    private final DiscEnvApiService deServiceFacade;
    private final DEClientConstants constants;
    private final UserInfo userInfo;
    @Inject DiskResourceUtil diskResourceUtil;
    @Inject EventBus eventBus;

    @Inject
    public DiskResourceServiceFacadeImpl(final DiscEnvApiService deServiceFacade,
                                         final DEProperties deProperties,
                                         final DEClientConstants constants,
                                         final DiskResourceAutoBeanFactory factory,
                                         final UserInfo userInfo,
                                         final EventBus eventBus) {
        super(new ModelKeyProvider<Folder>() {

            @Override
            public String getKey(Folder item) {
                return item == null ? null : item.getId();
            }
        });

        this.deServiceFacade = deServiceFacade;
        this.deProperties = deProperties;
        this.constants = constants;
        this.factory = factory;
        this.userInfo = userInfo;
        GWT.log("DISK RESOURCE SERVICE FACADE CONSTRUCTOR");
    }

    private <T> String encode(final T entity) {
        return AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(entity)).getPayload();
    }

    private <T> T decode(Class<T> clazz, String payload) {
        return AutoBeanCodex.decode(factory, clazz, payload).as();
    }

    @Override
    public DiskResource combineDiskResources(DiskResource from, DiskResource into) {
        Splittable splitFrom = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(from));
        AutoBean<DiskResource> autoBeanInto = AutoBeanUtils.getAutoBean(into);
        AutoBeanCodex.decodeInto(splitFrom, autoBeanInto);
        return autoBeanInto.as();
    }

    @Override
    public Folder convertToFolder(DiskResource diskResource) {
        AutoBean<DiskResource> autoBean = AutoBeanUtils.getAutoBean(diskResource);
        Splittable encode = AutoBeanCodex.encode(autoBean);
        AutoBean<Folder> decode = AutoBeanCodex.decode(factory, Folder.class, encode);
        return decode.as();
    }

    @Override
    public final void getRootFolders(final AsyncCallback<RootFolders> callback) {
        if (getRootCount() > 0) {
            RootFolders result = factory.rootFolders().as();
            result.setRoots(getRootItems());
            callback.onSuccess(result);
        } else {
            String address = deProperties.getDataMgmtBaseUrl() + "root"; //$NON-NLS-1$
            ServiceCallWrapper wrapper = new ServiceCallWrapper(address);

            callService(wrapper, new AsyncCallbackConverter<String, RootFolders>(callback) {
                @Override
                protected RootFolders convertFrom(final String json) {
                    RootFolders result = decode(RootFolders.class, json);
                    setRootFolders(result.getRoots());

                    return result;
                }
            });
        }
    }

    private void setRootFolders(List<Folder> rootNodes) {
        clear();
        add(rootNodes);
    }

    @Override
    public void getFolderContents(final Folder folder,
                                  final List<InfoType> infoTypeFilterList,
                                  final TYPE entityType,
                                  final FilterPagingLoadConfigBean loadConfig,
                                  final AsyncCallback<Folder> callback) {
        String address = deProperties.getDataMgmtBaseUrl() + "paged-directory?";

        SortInfoBean sortInfo = Iterables.getFirst(loadConfig.getSortInfo(),
                                                   new SortInfoBean("NAME", SortDir.ASC));
        if (!Strings.isNullOrEmpty(folder.getPath())) {
            address += "path=" + URL.encodeQueryString(folder.getPath())
                            + "&sort-col=" + sortInfo.getSortField()
                            + "&limit=" + loadConfig.getLimit()
                            + "&offset=" + loadConfig.getOffset()
                            + "&sort-dir=" + sortInfo.getSortDir().toString();
        }

        // Apply entity type query parameter if applicable
        if(entityType != null){
            address += "&entity-type=" + entityType.toString();
        }

        // Apply InfoType filters if applicable
        if((infoTypeFilterList != null)){
            String infoTypeUrlParameters = "";
            for(InfoType infoType : infoTypeFilterList){
                infoTypeUrlParameters += "&info-type=" + infoType.toString();
            }
            if(!Strings.isNullOrEmpty(infoTypeUrlParameters)){
                address += infoTypeUrlParameters;
            }
        }
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        callService(wrapper, new AsyncCallbackConverter<String, Folder>(callback) {

            @Override
            protected Folder convertFrom(String object) {
                return decode(Folder.class, object);
            }
        });

    }

    @Override
    public void getSubFolders(final Folder parent, final AsyncCallback<List<Folder>> callback) {
        final Folder folder = findModel(parent);

        if (hasFoldersLoaded(folder)) {
            callback.onSuccess(getSubFolders(folder));
        } else {
            String address = getDirectoryListingEndpoint(parent.getPath(), false);
            ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
            callService(wrapper, new AsyncCallbackConverter<String, List<Folder>>(callback) {

                @Override
                protected List<Folder> convertFrom(String result) {
                    // Decode JSON result into a folder
                    Folder folderListing = decode(Folder.class, result);

                    // Store or update the folder's subfolders.
                    return saveSubFolders(folderListing);
                }
            });
        }
    }

    private List<Folder> saveSubFolders(final Folder folder) {
        if (folder == null) {
            return null;
        }

        // Filter root folders from results to avoid duplicate folder issues in the navigation tree.
        List<Folder> subfolders = filterRoots(folder.getFolders());

        Folder parent = findModel(folder);
        if (parent != null && subfolders != null) {
            parent.setFolders(subfolders);

            for (Folder child : subfolders) {
                Folder current = findModel(child);
                if (current == null) {
                    add(parent, child);
                } else {
                    child.setFolders(current.getFolders());
                    update(child);
                }
            }
        }

        return subfolders;
    }

    private List<Folder> getSubFolders(final Folder folder) {
        if (folder != null && folder.getFolders() != null) {
            return folder.getFolders();
        }

        return Lists.newArrayList();
    }

    private boolean hasFoldersLoaded(final Folder folder) {
        return folder != null && folder.getFolders() != null;
    }

    /**
     * A root folder may be included as a child of another root folder in API responses
     * (e.g. the user's home folder is included under the "Shared with Me" folder).
     * In order to avoid selection model problems in the navigation view, root folders should be
     * filtered out of subfolder lists before being cached and returned to view stores.
     *
     * @param subfolders
     * @return List of subfolders with root folders removed.
     */
    private List<Folder> filterRoots(List<Folder> subfolders) {
        List<Folder> filteredFolders = Lists.newArrayList();

        if (subfolders != null) {
            List<Folder> roots = getRootItems();
            for (Folder f : subfolders) {
                Folder current = findModel(f);
                if (current == null || !roots.contains(current)) {
                    filteredFolders.add(f);
                }
            }
        }

        return filteredFolders;
    }

    private String getDirectoryListingEndpoint(final String path, boolean includeFiles) {
        String address = deProperties.getDataMgmtBaseUrl()
                + "directory?includefiles=" + (includeFiles ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        if (!Strings.isNullOrEmpty(path)) {
            address += "&path=" + URL.encodeQueryString(path); //$NON-NLS-1$
        }

        return address;
    }

    @Override
    public void createFolder(final Folder parentFolder,
                             final String newFolderName,
                             AsyncCallback<Folder> callback) {
        final String parentId = parentFolder.getPath();

        String fullAddress = deProperties.getDataMgmtBaseUrl() + "directory/create"; //$NON-NLS-1$
        JSONObject obj = new JSONObject();
        obj.put("path", new JSONString(parentId + "/" + newFolderName));
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, fullAddress, obj.toString());
        callService(wrapper, new AsyncCallbackConverter<String, Folder>(callback) {

            @Override
            protected Folder convertFrom(String result) {
                Folder folder = decode(Folder.class, result);

                // Set the new folder name since the create folder service call result does not contain
                // the name of the new folder
                folder.setName(newFolderName);

                // Use the service call result to set the ID of the new folder. Otherwise, calls to
                // getId() on this new folder instance will return null.
                // folder.setId(folder.getPath());

                addFolder(parentFolder.getId(), folder);

                return folder;
            }
        });
    }

    @Override
    public void createNcbiSraFolderStructure(final Folder parentFolder,
                                             final String[] foldersToCreate,
                                             AsyncCallback<String> callback) {
        final String parentId = parentFolder.getPath();

        String fullAddress = deProperties.getDataMgmtBaseUrl() + "directories"; //$NON-NLS-1$
        JSONObject obj = new JSONObject();

        JSONArray arr = new JSONArray();
        for (int i = 0; i < foldersToCreate.length; i++) {
            arr.set(i, new JSONString(parentId + "/" + foldersToCreate[i]));
        }
        obj.put("paths", arr);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, fullAddress, obj.toString());
        callService(wrapper, callback);
    }

    private void addFolder(String parentId, Folder child) {
        Folder parent = findModelWithKey(parentId);
        if (parent != null) {
            if (parent.getFolders() != null) {
                parent.getFolders().add(child);
            }

            add(parent, child);
        }
    }

    @Override
    public final void diskResourcesExist(final HasPaths diskResourcePaths,
                                         final AsyncCallback<DiskResourceExistMap> callback) {
        String address = deProperties.getDataMgmtBaseUrl() + "exists"; //$NON-NLS-1$
        final String body = encode(diskResourcePaths);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body);
        callService(wrapper, new AsyncCallbackConverter<String, DiskResourceExistMap>(callback) {
            @Override
            protected DiskResourceExistMap convertFrom(final String json) {
                // TODO Verify this facade's store against these results?
                return decode(DiskResourceExistMap.class, json);
            }
        });
    }

    @Override
    public void moveDiskResources(final Folder sourceFolder,
                                  final Folder destFolder,
                                  final List<DiskResource> diskResources,
                                  final AsyncCallback<DiskResourceMove> callback) {

        String address = deProperties.getDataMgmtBaseUrl() + "move"; //$NON-NLS-1$

        DiskResourceMove request = factory.diskResourceMove().as();
        request.setDest(destFolder.getPath());
        request.setSources(diskResourceUtil.asStringPathList(diskResources));

        // Fire this movedEvent after folder refreshes, so views can load correct folders from the cache.
        final DiskResourcesMovedEvent movedEvent = new DiskResourcesMovedEvent(sourceFolder,
                                                                               destFolder,
                                                                               diskResources,
                                                                               false);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, encode(request));

        callService(wrapper, new AsyncCallbackConverter<String, DiskResourceMove>(callback) {

            @Override
            protected DiskResourceMove convertFrom(String result) {
                DiskResourceMove resourcesMoved = decode(DiskResourceMove.class, result);

                /*
                 * JDS Result should have a "sources" key
                 * and a "dest" key.
                 *
                 * TODO JDS Verify returned keys to the objects we have already.
                 */

                // KLUDGE manually set destFolder until services are updated to return full dest info.
                resourcesMoved.setDestination(destFolder);

                return resourcesMoved;
            }

            @Override
            public void onSuccess(String result) {
                final DiskResourceMove resourcesMoved = convertFrom(result);

                if (diskResourceUtil.containsFolder(diskResources)) {
                    onFoldersMoved(sourceFolder, destFolder, resourcesMoved, callback, movedEvent);
                } else {
                    eventBus.fireEvent(movedEvent);
                    callback.onSuccess(resourcesMoved);
                }
            }
        });
    }

    private void onFoldersMoved(Folder sourceFolder,
                                Folder destFolder,
                                final DiskResourceMove resourcesMoved,
                                final AsyncCallback<DiskResourceMove> callback,
                                final DiskResourcesMovedEvent movedEvent) {
        if (!diskResourceUtil.isDescendantOfFolder(destFolder, sourceFolder)) {
            // The source folder is not under the destination, so it needs to be refreshed.
            refreshFolderOnChildrenMoved(sourceFolder, resourcesMoved, callback, movedEvent);
        }
        if (!diskResourceUtil.isDescendantOfFolder(sourceFolder, destFolder)) {
            // The destination is not under the source folder, so it needs to be refreshed.
            refreshFolderOnChildrenMoved(destFolder, resourcesMoved, callback, movedEvent);
        }
    }

    private void refreshFolderOnChildrenMoved(Folder folder,
                                              final DiskResourceMove resourcesMoved,
                                              final AsyncCallback<DiskResourceMove> callback,
                                              final DiskResourcesMovedEvent movedEvent) {
        refreshFolder(folder, new AsyncCallback<List<Folder>>() {

            @Override
            public void onSuccess(List<Folder> folders) {
                eventBus.fireEvent(movedEvent);
                callback.onSuccess(resourcesMoved);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }

    private void moveFolderTree(Folder folder, Folder dest) {
        if (folder == null || dest == null) {
            return;
        }

        // Update the folder's path to its new location, then cache it in the TreeStore.
        String folderName = diskResourceUtil.parseNameFromPath(folder.getPath());
        String newPath = diskResourceUtil.appendNameToPath(dest.getPath(), folderName);
        folder.setPath(newPath);
        add(dest, folder);

        // Move the folder's children to the new location in the cache.
        List<Folder> children = folder.getFolders();
        if (children != null) {
            for (Folder child : children) {
                moveFolderTree(child, folder);
            }
        }
    }

    @Override
    public void renameDiskResource(final DiskResource src,
                                   String destName,
                                   AsyncCallback<DiskResource> callback) {
        String fullAddress = deProperties.getDataMgmtBaseUrl() + "rename"; //$NON-NLS-1$

        DiskResourceRename request = factory.diskResourceRename().as();
        String srcId = src.getPath();
        request.setSource(srcId);
        request.setDest(diskResourceUtil.appendNameToPath(diskResourceUtil.parseParent(srcId), destName));

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, fullAddress, encode(request));
        callService(wrapper, new AsyncCallbackConverter<String, DiskResource>(callback) {

            @Override
            protected DiskResource convertFrom(String result) {
                DiskResourceRename response = decode(DiskResourceRename.class, result);

                DiskResource newDr;
                if (src instanceof Folder) {
                    newDr = decode(Folder.class, encode(src));
                } else {
                    newDr = decode(File.class, encode(src));
                }

                String newPath = response.getDest();
                newDr.setPath(newPath);
                newDr.setName(diskResourceUtil.parseNameFromPath(newPath));

                if (newDr instanceof Folder) {
                    renameFolder((Folder)src, (Folder)newDr);
                }

                return newDr;
            }
        });
    }

    private void renameFolder(Folder src, Folder renamed) {
        if (src == null || renamed == null) {
            return;
        }

        Folder folder = findModel(src);
        if (folder != null) {
            Folder parent = getParent(folder);

            // Remove the folder and its children from the cache.
            remove(folder);

            if (hasFoldersLoaded(parent)) {
                // Replace the folder and its children with the renamed folder, by adding the children to
                // the renamed folder and resetting their paths.
                parent.getFolders().remove(folder);
                renamed.setFolders(folder.getFolders());
                parent.getFolders().add(renamed);

                moveFolderTree(renamed, parent);
            }
        }
    }

    @Override
    public void refreshFolder(Folder parent, final AsyncCallback<List<Folder>> callback) {
        final Folder folder = findModel(parent);
        if (folder == null) {
            // If this folder is not in the cache, it may be a pseudo-folder like 'Favorites'.
            // Notify all listeners now so they can handle refreshing this kind of folder.
            List<Folder> result = Lists.newArrayList();
            callback.onSuccess(result);
            eventBus.fireEvent(new FolderRefreshedEvent(parent));
            return;
        }

        removeChildren(folder);
        folder.setFolders(null);

        getSubFolders(folder, new AsyncCallback<List<Folder>>() {
            @Override
            public void onSuccess(List<Folder> result) {
                eventBus.fireEvent(new FolderRefreshedEvent(folder));
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    @Override
    public void search(String term, int size, String type, AsyncCallback<String> callback) {
        String fullAddress = deProperties.getMuleServiceBaseUrl() + "search?search-term="
                + URL.encodeQueryString(term) + "&size=" + size;

        if (type != null && !type.isEmpty()) {
            fullAddress = fullAddress + "&type=" + type;
        }

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, fullAddress);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void importFromUrl(final String url, final DiskResource dest, AsyncCallback<String> callback) {
        String fullAddress = deProperties.getFileIoBaseUrl() + "urlupload"; //$NON-NLS-1$
        JSONObject body = new JSONObject();
        body.put("dest", new JSONString(dest.getPath())); //$NON-NLS-1$
        body.put("address", new JSONString(url)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, fullAddress, body.toString());
        callService(wrapper, callback);
    }

    @Override
    public String getEncodedSimpleDownloadURL(String path) {
        // We must proxy the download requests through a servlet, since the actual download service may
        // be on a port behind a firewall that the servlet can access, but the client can not.
        String address = Format.substitute("{0}?user={1}&path={2}",
                                           constants.fileDownloadServlet(),
                                           userInfo.getUsername(),
                                           URL.encodeQueryString(path));
        return address;
    }

    @Override
    public <T extends DiskResource> void deleteDiskResources(final List<T> diskResources,
                                                             final AsyncCallback<HasPaths> callback) {
        final HasPaths dto = factory.pathsList().as();
        dto.setPaths(diskResourceUtil.asStringPathList(diskResources));
        deleteDiskResources(dto, callback);
    }

    @Override
    public final void deleteDiskResources(final HasPaths diskResources,
                                          final AsyncCallback<HasPaths> callback) {
        String fullAddress = deProperties.getDataMgmtBaseUrl() + "delete"; //$NON-NLS-1$
        final String body = encode(diskResources);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, fullAddress, body);
        callService(wrapper, new AsyncCallbackConverter<String, HasPaths>(callback) {
            @Override
            protected HasPaths convertFrom(final String json) {
                HasPaths deletedPaths = decode(HasPaths.class, json);

                // Remove any folders found in the response from the TreeStore.
                if (deletedPaths != null) {
                    removeFoldersByPath(deletedPaths.getPaths());
                }

                return deletedPaths;
            }
        });
    }

    private void removeFoldersByPath(List<String> deletedPaths) {
        if (deletedPaths != null) {
            for (Folder folder : getAll()) {
                for (String path : deletedPaths) {
                    if (folder.getPath().equals(path)) {
                        Folder parent = getParent(folder);
                        parent.getFolders().remove(folder);
                        remove(folder);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void getDiskResourceMetaData(DiskResource resource, AsyncCallback<DiskResourceMetadataList> callback) {
        String fullAddress = deProperties.getDataMgmtBaseUrl() + resource.getId()
                + "/metadata";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, fullAddress);

        callService(wrapper, new AsyncCallbackConverter<String, DiskResourceMetadataList>(callback) {
            @Override
            protected DiskResourceMetadataList convertFrom(String object) {
                DiskResourceMetadataList metadata = AutoBeanCodex.decode(factory, DiskResourceMetadataList.class, object).as();
                return metadata;
            }
        });
    }

    @Override
    public void setDiskResourceMetaData(DiskResource resource,
                                        DiskResourceMetadataTemplate metadata,
                                        List<DiskResourceMetadata> irodsAvus,
                                        AsyncCallback<String> callback) {
        String address = deProperties.getDataMgmtBaseUrl() + resource.getId() + "/metadata"; //$NON-NLS-1$

        // Create request consisting of metadata to update and delete.
        DiskResourceMetadataBatchRequest request = factory.metadataBatchRequest().as();
        request.setMetadata(metadata);
        request.setAvus(irodsAvus);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, encode(request));
        callService(wrapper, callback);
    }

    @Override
    public void shareDiskResource(JSONObject body, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "share"; //$NON-NLS-1$
        HashMap<String, String> mdcMap = Maps.newHashMap();
        mdcMap.put(METRIC_TYPE_KEY, SHARE_EVENT);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body.toString());

        deServiceFacade.getServiceData(wrapper,
                                       mdcMap,
                                       callback);
    }

    @Override
    public void unshareDiskResource(JSONObject body, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "unshare"; //$NON-NLS-1$
        HashMap<String, String> mdcMap = Maps.newHashMap();
        mdcMap.put(METRIC_TYPE_KEY, SHARE_EVENT);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body.toString());

        deServiceFacade.getServiceData(wrapper,
                                       mdcMap,
                                       callback);
    }

    @Override
    public void getPermissions(JSONObject body, AsyncCallback<String> callback) {
        String fullAddress = deProperties.getDataMgmtBaseUrl() + "user-permissions"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, fullAddress, body.toString());
        callService(wrapper, callback);
    }

    @Override
    public final void getStat(final FastMap<TYPE> paths,
                              final AsyncCallback<FastMap<DiskResource>> callback) {
        String address = deProperties.getDataMgmtBaseUrl() + "stat"; //$NON-NLS-1$
        HasPaths pathsAb = decode(HasPaths.class, "{}");
        pathsAb.setPaths(new ArrayList<>(paths.keySet()));
        final String body = encode(pathsAb);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body);
        callService(wrapper, new AsyncCallbackConverter<String, FastMap<DiskResource>>(callback) {
            @Override
            protected FastMap<DiskResource> convertFrom(final String json) {
                FastMap<DiskResource> map = new FastMap<>();
                Splittable obj = StringQuoter.split(json);
                Splittable pathSplittable = obj.get("paths");
                for (String key : paths.keySet()) {
                    Splittable dr = pathSplittable.get(key);
                    if (dr != null) {
                        String payload = dr.getPayload();
                        if (paths.get(key).equals(TYPE.FILE)) {
                            File decodeFile = decode(File.class, payload);
                            decodeFile.setStatLoaded(true);
                            map.put(key, decodeFile);
                        } else {
                            Folder decodeFolder = decode(Folder.class, payload);
                            decodeFolder.setStatLoaded(true);
                            map.put(key, decodeFolder);
                        }
                    }
                }

                return map;
            }

        });
    }

    /**
     * Performs the actual service call.
     * 
     * @param wrapper the wrapper used to get to the actual service via the service proxy.
     * @param callback executed when RPC call completes.
     */
    void callService(ServiceCallWrapper wrapper, AsyncCallback<String> callback) {
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void restoreDiskResource(HasPaths request, AsyncCallback<String> callback) {
        final String fullAddress = deProperties.getDataMgmtBaseUrl() + "restore"; //$NON-NLS-1$
        final String body = encode(request);
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, fullAddress, body);
        callService(wrapper, callback);
    }

    @Override
    public void emptyTrash(String user, AsyncCallback<String> callback) {
        String address = deProperties.getDataMgmtBaseUrl() + "trash"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address);
        callService(wrapper, callback);
    }

    @Override
    public void createDataLinks(List<String> ticketIdList, AsyncCallback<List<DataLink>> callback) {
        String fullAddress = deProperties.getDataMgmtBaseUrl() + "tickets"; //$NON-NLS-1$
        String args = "public=1";

        JSONObject body = new JSONObject();
        JSONArray tickets = new JSONArray();
        int index = 0;
        for (String id : ticketIdList) {
            tickets.set(index, new JSONString(id));
            index++;
        }
        body.put("paths", tickets);

        HashMap<String, String> mdcMap = Maps.newHashMap();
        mdcMap.put(METRIC_TYPE_KEY, SHARE_EVENT);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, fullAddress, body.toString());
        wrapper.setArguments(args);
        deServiceFacade.getServiceData(wrapper,
                                       mdcMap,
                                       new AsyncCallbackConverter<String, List<DataLink>>(callback) {
            @Override
            protected List<DataLink> convertFrom(String object) {
                AutoBean<DataLinkList> tickets = AutoBeanCodex.decode(factory, DataLinkList.class, object);
                return tickets.as().getTickets();
            }
        });

    }

    @Override
    public void listDataLinks(List<String> diskResourceIds, AsyncCallback<String> callback) {
        String fullAddress = deProperties.getDataMgmtBaseUrl() + "list-tickets"; //$NON-NLS-1$

        JSONObject body = new JSONObject();
        body.put("paths", buildArrayFromStrings(diskResourceIds));

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, fullAddress, body.toString());
        callService(wrapper, callback);

    }

    @Override
    public void deleteDataLinks(List<String> dataLinkIds, AsyncCallback<String> callback) {
        String fullAddress = deProperties.getDataMgmtBaseUrl() + "delete-tickets"; //$NON-NLS-1$

        JSONObject body = new JSONObject();
        body.put("tickets", buildArrayFromStrings(dataLinkIds));
        HashMap<String, String> mdcMap = Maps.newHashMap();
        mdcMap.put(METRIC_TYPE_KEY, SHARE_EVENT);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, fullAddress, body.toString());
        deServiceFacade.getServiceData(wrapper,
                                       mdcMap,
                                       callback);
    }

    JSONArray buildArrayFromStrings(List<?> items) {
        if (items == null) {
            return null;
        }

        JSONArray ret = new JSONArray();

        int index = 0;
        for (Object item : items) {
            JSONString str = new JSONString(item.toString());
            ret.set(index++, str);
        }

        return ret;
    }

    @Override
    public void getInfoTypes(AsyncCallback<List<InfoType>> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "filetypes/type-list";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper, new AsyncCallbackConverter<String, List<InfoType>>(callback) {
            @Override
            protected List<InfoType> convertFrom(String object) {
                Splittable split = StringQuoter.split(object);
                Splittable types = split.get("types");
                List<InfoType> infoTypes = Lists.newArrayList();
                for(int i = 0; i < types.size(); i++){
                    InfoType infoType = InfoType.fromTypeString(types.get(i).asString());
                    infoTypes.add(infoType);
                }

                return infoTypes;
            }
        });
    }

    @Override
    public void setFileType(String filePath, String type, AsyncCallback<String> callback) {
        JSONObject obj = new JSONObject();
        obj.put("path", new JSONString(filePath));
        obj.put("type", new JSONString(type));

        String address = deProperties.getMuleServiceBaseUrl() + "filetypes/type";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, obj.toString());
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public DiskResourceAutoBeanFactory getDiskResourceFactory() {
        return factory;
    }

    @Override
    public void moveContents(final Folder sourceFolder,
                             final Folder destFolder,
                             final AsyncCallback<DiskResourceMove> callback) {
        String address = deProperties.getDataMgmtBaseUrl() + "move-contents"; //$NON-NLS-1$

        DiskResourceMove request = factory.diskResourceMove().as();
        request.setDest(destFolder.getPath());
        request.setSourcePath(sourceFolder.getPath());

        // Fire this movedEvent after folder refreshes, so views can load correct folders from the cache.
        final DiskResourcesMovedEvent movedEvent = new DiskResourcesMovedEvent(sourceFolder,
                                                                               destFolder,
                                                                               null,
                                                                               true);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, encode(request));

        callService(wrapper, new AsyncCallbackConverter<String, DiskResourceMove>(callback) {

            @Override
            protected DiskResourceMove convertFrom(String result) {
                DiskResourceMove resourcesMoved = decode(DiskResourceMove.class, result);
                // KLUDGE manually set destFolder until services are updated to return full dest info.
                resourcesMoved.setDestination(destFolder);

                return resourcesMoved;
            }

            @Override
            public void onSuccess(String result) {
                // All sourceFolder contents were moved, which may contain a folder.
                onFoldersMoved(sourceFolder, destFolder, convertFrom(result), callback, movedEvent);
            }
        });

    }

    @Override
    public void deleteContents(String selectedFolderId, AsyncCallback<HasPaths> callback) {
        String fullAddress = deProperties.getDataMgmtBaseUrl() + "delete-contents"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, fullAddress, "{\"path\":\""
                + selectedFolderId + "\"}");
        callService(wrapper, new AsyncCallbackConverter<String, HasPaths>(callback) {
            @Override
            protected HasPaths convertFrom(final String json) {
                HasPaths deletedPaths = decode(HasPaths.class, json);

                // Remove any folders found in the response from the TreeStore.
                if (deletedPaths != null) {
                    removeFoldersByPath(deletedPaths.getPaths());
                }

                return deletedPaths;
            }
        });
    }

    @Override
    public void restoreAll(AsyncCallback<String> callback) {
        final String fullAddress = deProperties.getDataMgmtBaseUrl() + "restore-all"; //$NON-NLS-1$
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, fullAddress, "{}");
        callService(wrapper, callback);
    }

    @Override
    public void getMetadataTemplateListing(AsyncCallback<List<MetadataTemplateInfo>> callback) {
        String address = deProperties.getDataMgmtBaseUrl() + "metadata/templates";
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        callService(wrapper, new AsyncCallbackConverter<String, List<MetadataTemplateInfo>>(callback) {
            @Override
            protected List<MetadataTemplateInfo> convertFrom(String object) {
                MetadataTemplateInfoList templateInfoList = AutoBeanCodex.decode(factory, MetadataTemplateInfoList.class, object).as();
                return templateInfoList.getTemplates();
            }
        });
    }

    @Override
    public void getMetadataTemplate(String templateId, AsyncCallback<MetadataTemplate> callback) {
        String address = deProperties.getDataMgmtBaseUrl() + "metadata/template/" + templateId;
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        callService(wrapper, new AsyncCallbackConverter<String, MetadataTemplate>(callback) {
            @Override
            protected MetadataTemplate convertFrom(String object) {
                AutoBean<MetadataTemplate> bean = AutoBeanCodex.decode(factory, MetadataTemplate.class, object);
                return bean.as();
            }
        });

    }

    @Override
    public void shareWithAnonymous(HasPaths diskResourcePaths, AsyncCallback<String> callback) {
        String address = deProperties.getDataMgmtBaseUrl() + "anon-files"; //$NON-NLS-1$
        final String body = encode(diskResourcePaths);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body);
        callService(wrapper, callback);
    }

    @Override
    public void copyMetadata(String srcUUID,
                              Splittable paths,
                              boolean override,
                              AsyncCallback<String> callback) {
        String address = null;
        if(override) {
            address = deProperties.getDataMgmtBaseUrl() + srcUUID + "/metadata/copy?force="
                + override;
        } else {
            address = deProperties.getDataMgmtBaseUrl() + srcUUID + "/metadata/copy";
        }
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, paths.getPayload());
        callService(wrapper, callback);
    }

    @Override
    public void saveMetadata(String srcUUID,
                             String path,
                             boolean recursive,
                             AsyncCallback<String> callback) {
        Splittable body = StringQuoter.createSplittable();
        String address = deProperties.getDataMgmtBaseUrl() + srcUUID + "/metadata/save";
        Splittable sppath = StringQuoter.create(path);
        sppath.assign(body, "dest");
        Splittable sprec = StringQuoter.create(recursive);
        sprec.assign(body, "recursive");

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body.getPayload());
        callService(wrapper, callback);

    }

    @Override
    public void setBulkMetadataFromFile(String metadataFilePath,
                                        String template_id,
                                        String destFolder,
                                        boolean force,
                                        AsyncCallback<String> callback) {
        StringBuilder address = new StringBuilder(deProperties.getDataMgmtBaseUrl()
                + "metadata/csv-parser?");

        address.append("dest=" + destFolder);
        address.append("&src=" + metadataFilePath);
        address.append("&force=" + force);

        if (!Strings.isNullOrEmpty(template_id)) {
            address.append("&template-id=" + template_id);
        }

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address.toString(), "{}");
        callService(wrapper, callback);
    }

    @Override
    public void requestIdentifier(String uuid, String path, AsyncCallback<String> callback) {
        StringBuilder address = new StringBuilder(deProperties.getPermIdBaseUrl());

        Splittable body = StringQuoter.createSplittable();
        Splittable sppath = StringQuoter.create(uuid);
        sppath.assign(body, "folder");
        Splittable sprec = StringQuoter.create("DOI");
        sprec.assign(body, "type");

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address.toString(), body.getPayload());
        callService(wrapper, callback);

    }
}
