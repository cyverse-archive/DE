package org.iplantc.de.client.models.errors.diskResources.categories;

import org.iplantc.de.client.models.errors.diskResources.ErrorDuplicateDiskResource;
import org.iplantc.de.client.util.DiskResourceUtil;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.web.bindery.autobean.shared.AutoBean;

public class ErrorDuplicateDiskResourceCategory {
    private static DiskResourceUtil diskResourceUtil = DiskResourceUtil.getInstance();

    public static SafeHtml generateErrorMsg(AutoBean<ErrorDuplicateDiskResource> instance) {
        ErrorDuplicateDiskResource error = instance.as();

        return ErrorDiskResourceCategory.getErrorMessage(
                ErrorDiskResourceCategory.getDiskResourceErrorCode(error.getErrorCode()),
                diskResourceUtil.asCommaSeparatedNameList(error.getPaths()));
    }
}