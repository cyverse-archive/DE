package org.iplantc.de.client.models.diskResources;

import com.google.web.bindery.autobean.shared.AutoBean;

public class FilePermissionCategory {

    public static boolean owner(AutoBean<File> instance) {
        PermissionValue permission = instance.as().getPermission();
        return permission != null && permission.equals(PermissionValue.own);
    }

    public static boolean readable(AutoBean<File> instance) {
        PermissionValue permission = instance.as().getPermission();
        return permission != null && (permission.equals(PermissionValue.read) || permission.equals(PermissionValue.write) || permission.equals(PermissionValue.own));
    }

    public static boolean writable(AutoBean<File> instance) {
        PermissionValue permission = instance.as().getPermission();
        return permission != null && (permission.equals(PermissionValue.own) || permission.equals(PermissionValue.write));
    }
}
