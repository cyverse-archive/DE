
/**
 * 
 * 
 */
package org.iplantc.de.client.models.sharing;

import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.util.DiskResourceUtil;

/**
 * 
 * @author sriram
 * 
 */
public class DataSharing extends Sharing {

    private String path;
    private PermissionValue displayPermission;
    private PermissionValue permission;
    private final DiskResourceUtil diskResourceUtil;

    public static enum TYPE {
        FILE, FOLDER
    }

    public DataSharing(final Collaborator c,
                       final PermissionValue p,
                       final String path) {
        super(c);
        this.diskResourceUtil = DiskResourceUtil.getInstance();
        setPath(path);
        if (p != null) {
            permission = p;
            displayPermission = permission;
        }

    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getResourceName() {
        return diskResourceUtil.parseNameFromPath(path);
    }

    public boolean isReadable() {
        return permission != null && (permission.equals(PermissionValue.read) || permission.equals(PermissionValue.write) || permission.equals(PermissionValue.own));
    }

    public boolean isWritable() {
        return permission != null && (permission.equals(PermissionValue.own) || permission.equals(PermissionValue.write));
    }

    public boolean isOwner() {
        return permission != null && permission.equals(PermissionValue.own);
    }

    public String getPath() {
        return path;
    }

    @Override
    public String getKey() {
        return super.getKey() + getPath();
    }

    public void setPermission(PermissionValue perm) {
        this.permission = perm;
    }

    public PermissionValue getPermission() {
        return permission;
    }

    public void setDisplayPermission(PermissionValue perm) {
        displayPermission = perm;
    }

    public PermissionValue getDisplayPermission() {
        return displayPermission;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof DataSharing)) {
            return false;
        }
        DataSharing s = (DataSharing)o;
        return getKey().equals(s.getKey()) && s.getDisplayPermission().equals(getDisplayPermission());
    }

    @Override
    public DataSharing copy() {
        Collaborator c = getCollaborator();
        String path = getPath();
        return new DataSharing(c, permission, path);
    }

}