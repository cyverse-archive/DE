package org.iplantc.de.client.models.errors.diskResources.categories;

import org.iplantc.de.client.models.errors.diskResources.ErrorDiskResourceRename;
import org.iplantc.de.client.util.DiskResourceUtil;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.web.bindery.autobean.shared.AutoBean;

public class ErrorDiskResourceRenameCategory {

    static DiskResourceUtil diskResourceUtil = DiskResourceUtil.getInstance();

    public static SafeHtml generateErrorMsg(AutoBean<ErrorDiskResourceRename> instance) {
        ErrorDiskResourceRename error = instance.as();

        if(error.getLimit() > 0) {
            return ErrorDiskResourceCategory.getThresholdErrorMessage(
                    ErrorDiskResourceCategory.getDiskResourceErrorCode(error.getErrorCode()),error.getLimit());
        } else {
        return ErrorDiskResourceCategory.getErrorMessage(
                ErrorDiskResourceCategory.getDiskResourceErrorCode(error.getErrorCode()),
                diskResourceUtil.parseNameFromPath(error.getPath()));
        }
    }
}