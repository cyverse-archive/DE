package org.iplantc.de.client.models.diskResources;

import com.google.web.bindery.autobean.shared.AutoBean;

public class FolderPermissionCategory {

    public static boolean owner(AutoBean<Folder> instance) {
        PermissionValue permission = instance.as().getPermission();
        return permission != null && permission.equals(PermissionValue.own);
    }

    public static boolean readable(AutoBean<Folder> instance) {
        PermissionValue permission = instance.as().getPermission();
        return permission != null && (permission.equals(PermissionValue.read) || permission.equals(PermissionValue.write) || permission.equals(PermissionValue.own));
    }

    public static boolean writable(AutoBean<Folder> instance) {
        PermissionValue permission = instance.as().getPermission();
        return permission != null && (permission.equals(PermissionValue.own) || permission.equals(PermissionValue.write));
    }
}
