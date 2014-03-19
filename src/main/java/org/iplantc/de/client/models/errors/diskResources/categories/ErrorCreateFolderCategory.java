package org.iplantc.de.client.models.errors.diskResources.categories;

import org.iplantc.de.client.models.errors.diskResources.ErrorCreateFolder;
import org.iplantc.de.client.util.DiskResourceUtil;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.web.bindery.autobean.shared.AutoBean;

public class ErrorCreateFolderCategory {

    public static SafeHtml generateErrorMsg(AutoBean<ErrorCreateFolder> instance) {
        ErrorCreateFolder error = instance.as();

        return ErrorDiskResourceCategory.getErrorMessage(
                ErrorDiskResourceCategory.getDiskResourceErrorCode(error.getErrorCode()),
                DiskResourceUtil.parseNameFromPath(error.getPath()));
    }
}