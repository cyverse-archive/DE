package org.iplantc.de.client.models.errors.diskResources.categories;

import org.iplantc.de.client.models.errors.diskResources.ErrorDiskResourceMove;
import org.iplantc.de.client.util.DiskResourceUtil;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.web.bindery.autobean.shared.AutoBean;

public class ErrorDiskResourceMoveCategory {

    public static SafeHtml generateErrorMsg(AutoBean<ErrorDiskResourceMove> instance) {
        ErrorDiskResourceMove error = instance.as();

        if(error.getLimit() > 0) {
            return ErrorDiskResourceCategory.getThresholdErrorMessage(
                    ErrorDiskResourceCategory.getDiskResourceErrorCode(error.getErrorCode()),error.getLimit());
        } else {
        
        return ErrorDiskResourceCategory.getErrorMessage(
                ErrorDiskResourceCategory.getDiskResourceErrorCode(error.getErrorCode()),
                DiskResourceUtil.asCommaSeperatedNameList(error.getPaths()));
        }
    }
}