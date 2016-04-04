/**
 * 
 */
package org.iplantc.de.client.util;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceFavorite;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.models.viewer.InfoType;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.core.shared.FastMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author sriram, jstroot
 * 
 */
public class DiskResourceUtil {

    private static DiskResourceUtil INSTANCE;
    private final JsonUtil jsonUtil;

    DiskResourceUtil() {
        this.jsonUtil = JsonUtil.getInstance();

    }

    public static DiskResourceUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DiskResourceUtil();
        }
        return INSTANCE;
    }

    /**
     * Parse the parent folder from a path.
     * 
     * @param path the path to parse.
     * @return the parent folder.
     */
    public String parseParent(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return path;
        }

        LinkedList<String> split = Lists.newLinkedList(Splitter.on("/")
                                                               .trimResults()
                                                               .omitEmptyStrings()
                                                               .split(path));
        if (split.size() > 0) {
            split.removeLast();
        }
        return "/".concat(Joiner.on("/").join(split));
    }

    /**
     * Parse the display name from a path.
     * 
     * @param path the path to parse.
     * @return the display name.
     */
    public String parseNameFromPath(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return path;
        }

        LinkedList<String> split = Lists.newLinkedList(Splitter.on("/")
                                                               .trimResults()
                                                               .omitEmptyStrings()
                                                               .split(path));

        return split.removeLast();
    }

    public List<String> parseNamesFromIdList(Iterable<String> idList) {
        if (idList == null) {
            return null;
        }

        List<String> nameList = Lists.newArrayList();
        for (String s : idList) {
            nameList.add(parseNameFromPath(s));
        }
        return nameList;
    }

    /**
     * Appends a folder or file name to an existing folder path.
     * 
     * @param basePath the folder path to extend
     * @param name the member folder or file name
     * 
     * @return the member folder or file path
     */
    public final String appendNameToPath(final String basePath, final String name) {
        if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(basePath)) {
            return null;
        }
        return basePath + "/" + name;
    }

    public String asCommaSeparatedNameList(Iterable<String> idList) {
        if (idList == null) {
            return null;
        }

        return Joiner.on(", ").join(parseNamesFromIdList(idList));
    }

    public boolean isOwner(Iterable<DiskResource> resources) {
        if (resources == null) {
            return false;
        }

        // Use predicate to determine if user is owner of all disk resources
        for (DiskResource dr : resources) {
            if (!isOwner(dr)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines if the user is an owner of one item in the given resources.
     */
    public boolean hasOwner(Iterable<DiskResource> resources) {
        if (resources == null) {
            return false;
        }

        for (DiskResource dr : resources) {
            if (isOwner(dr)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the given <code>DiskResource</code> is a direct child of the given parent
     * <code>Folder</code>.
     */
    public boolean isChildOfFolder(Folder parent, DiskResource resource) {
        return parseParent(resource.getPath()).equals(parent.getPath());
    }

    /**
     * Determines if the given folder is a descendant of the given ancestor folder. This is done by
     * verifying that the given folder's path starts with the ancestor's path.
     * 
     * @param ancestor the ancestor folder.
     * @param folder the folder whose ancestry is verified.
     * @return true if the folder is a descendant of the given ancestor, false otherwise.
     */
    public boolean isDescendantOfFolder(Folder ancestor, Folder folder) {
        return folder.getPath().startsWith(ancestor.getPath() + "/"); //$NON-NLS-1$
    }

    public boolean isMovable(Folder targetFolder, Iterable<DiskResource> dropData) {
        return isOwner(dropData) && isWritable(targetFolder);
    }

    public boolean canUploadTo(DiskResource resource) {
        return (resource instanceof Folder) && !(resource instanceof DiskResourceFavorite)
                && !(resource instanceof DiskResourceQueryTemplate)
                && (isOwner(resource) || isWritable(resource)) && !inTrash(resource);
    }

    public boolean inTrash(DiskResource resource) {
        return resource != null && resource.getPath().startsWith(UserInfo.getInstance().getTrashPath());
    }

    public boolean containsTrashedResource(List<DiskResource> selectedResources) {
        if (selectedResources != null) {
            for (DiskResource resource : selectedResources) {
                if (inTrash(resource)) {
                    return true;
                }
            }
        }

        return false;
    }

    public <R extends DiskResource> boolean contains(Iterable<R> selection, R target) {
        if (selection != null && target != null) {
            for (DiskResource resource : selection) {
                if (resource.getId().equals(target.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    public <R extends DiskResource> boolean containsFolder(Iterable<R> selection) {
        for (DiskResource resource : selection) {
            if (resource instanceof Folder) {
                return true;
            }
        }
        return false;
    }

    public <R extends DiskResource> boolean containsFile(Iterable<R> selection) {
        for (DiskResource resource : selection) {
            if (resource instanceof File) {
                return true;
            }
        }
        return false;
    }

    public <R extends DiskResource> Iterable<File> extractFiles(Iterable<R> diskresources) {
        List<File> files = Lists.newArrayList();
        for (DiskResource dr : diskresources) {
            if (dr instanceof File) {
                files.add((File)dr);
            }
        }
        return files;
    }

    public <R extends DiskResource> Iterable<Folder> extractFolders(Iterable<R> diskresources) {
        List<Folder> folders = Lists.newArrayList();
        for (DiskResource dr : diskresources) {
            if (dr instanceof Folder) {
                folders.add((Folder)dr);
            }
        }
        return folders;
    }

    public <R extends HasId> List<String> asStringIdList(Iterable<R> diskResourceList) {
        List<String> ids = Lists.newArrayList();
        for (R dr : diskResourceList) {
            ids.add(dr.getId());
        }

        return ids;
    }

    public <R extends HasPath> List<String> asStringPathList(Iterable<R> diskResourceList) {
        List<String> paths = Lists.newArrayList();
        for (R dr : diskResourceList) {
            paths.add(dr.getPath());
        }

        return paths;
    }

    public <R extends HasPath>
            FastMap<TYPE>
            asStringPathTypeMap(Iterable<R> diskResourceList, TYPE type) {
        FastMap<TYPE> pathMap = new FastMap<>();
        for (R dr : diskResourceList) {
            pathMap.put(dr.getPath(), type);
        }
        return pathMap;

    }

    public <R extends HasId> Splittable createStringIdListSplittable(Iterable<R> hasIdList) {
        JSONArray jArr = jsonUtil.buildArrayFromStrings(asStringIdList(hasIdList));

        return StringQuoter.split(jArr.toString());
    }

    public Splittable createSplittableFromStringList(List<String> strings) {
        return StringQuoter.split(jsonUtil.buildArrayFromStrings(strings).toString());
    }

    public HasPath getFolderPathFromFile(File file) {
        if (file != null) {
            return CommonModelUtils.getInstance().createHasPathFromString(parseParent(file.getPath()));
        }
        return null;
    }

    public String formatFileSize(String strSize) {
        if (strSize != null && !strSize.isEmpty()) {
            Double size = Double.parseDouble(strSize);
            if (size < 1024) {
                return NumberFormat.getFormat("0").format(size) + " bytes";
            } else if (size < 1048576) {
                return (NumberFormat.getFormat("0.0#").format(((size * 10) / 1024) / 10)) + " KB";
            } else if (size < 1073741824) {
                return (NumberFormat.getFormat("0.0#").format(((size * 10) / 1048576) / 10)) + " MB";
            } else {
                return (NumberFormat.getFormat("0.0#").format(((size * 10) / 1073741824) / 10)) + " GB";
            }
        } else {
            return null;
        }
    }

    /**
     * Returns a Set containing all Files found in the given DiskResource Set.
     * 
     * @return A Set containing all Files found in the given DiskResource Set, or an empty Set if the
     *         given Set is null or empty.
     */
    public Set<File> filterFiles(Iterable<DiskResource> diskResources) {
        Set<File> files = Sets.newHashSet();

        if (diskResources != null) {
            for (DiskResource resource : diskResources) {
                if (resource instanceof File) {
                    files.add((File)resource);
                }
            }
        }

        return files;
    }

    /**
     * Returns a Set containing all Folders found in the given DiskResource Set.
     * 
     * @return A Set containing all Folders found in the given DiskResource Set, or an empty Set if the
     *         given Set is null or empty.
     */
    public Set<Folder> filterFolders(Set<DiskResource> diskResources) {
        Set<Folder> folders = Sets.newHashSet();

        if (diskResources != null) {
            for (DiskResource resource : diskResources) {
                if (resource instanceof Folder) {
                    folders.add((Folder)resource);
                }
            }
        }

        return folders;
    }

    public boolean isOwner(DiskResource dr) {
        if (dr == null) {
            return false;
        }

        return dr.getPermission().equals(PermissionValue.own);
    }

    public boolean isReadable(DiskResource dr) {
        if (dr == null) {
            return false;
        }

        return dr.getPermission().equals(PermissionValue.own)
                || dr.getPermission().equals(PermissionValue.write)
                || dr.getPermission().equals(PermissionValue.read);
    }

    public boolean isWritable(DiskResource dr) {
        if (dr == null) {
            return false;
        }

        return dr.getPermission().equals(PermissionValue.own)
                || dr.getPermission().equals(PermissionValue.write);
    }

    public boolean checkManifest(Splittable obj) {
        if (obj == null) {
            return false;
        }

        String info_type = obj.get(DiskResource.INFO_TYPE_KEY).asString();
        if (info_type == null || info_type.isEmpty()) {
            return false;
        }

        return true;
    }

    public boolean isTreeInfoType(InfoType infoType) {
        return InfoType.NEXUS.equals(infoType) || InfoType.NEXML.equals(infoType)
                || InfoType.NEWICK.equals(infoType) || InfoType.PHYLOXML.equals(infoType);
    }

    public boolean isGenomeVizInfoType(InfoType infoType) {
        return InfoType.FASTA.equals(infoType);
    }

    public boolean isEnsemblInfoType(InfoType infoType) {
        return InfoType.BAM.equals(infoType) || InfoType.VCF.equals(infoType)
                || InfoType.GFF.equals(infoType) || InfoType.BED.equals(infoType);
    }

    private String getInfoType(Splittable obj) {
        if (obj == null)
            return null;
        if (checkManifest(obj)) {
            return obj.get(DiskResource.INFO_TYPE_KEY).asString();
        } else {
            return null;
        }
    }

    public boolean isTreeTab(Splittable obj) {
        String infoType = getInfoType(obj);
        return (infoType != null)
                && (InfoType.NEXUS.toString().equals(infoType)
                        || InfoType.NEXML.toString().equals(infoType)
                        || InfoType.NEWICK.toString().equals(infoType) || InfoType.PHYLOXML.toString()
                                                                                           .equals(infoType));
    }

    public boolean isGenomeVizTab(Splittable obj) {
        String infoType = getInfoType(obj);
        return infoType != null && InfoType.FASTA.toString().equals(infoType);
    }

    public boolean isEnsemblVizTab(Splittable obj) {
        String infoType = getInfoType(obj);
        return (infoType != null) && infoType.equals(InfoType.BAM.toString())
                || infoType.equals(InfoType.VCF.toString()) || infoType.equals(InfoType.GFF.toString())
                || infoType.equals(InfoType.BED.toString());
    }

    public Splittable createStringPathListSplittable(List<HasPath> hasPathList) {
        JSONArray jArr = jsonUtil.buildArrayFromStrings(asStringPathList(hasPathList));

        return StringQuoter.split(jArr.toString());
    }

    public Splittable createInfoTypeSplittable(String infoType) {
        Splittable s = StringQuoter.createSplittable();
        StringQuoter.create(infoType).assign(s, DiskResource.INFO_TYPE_KEY);
        return s;
    }
}
