package org.iplantc.de.theme.base.client.commons.error;

import com.google.gwt.i18n.client.Messages;

/**
 * Created by jstroot on 1/20/15.
 * @author jstroot
 */
public interface ErrorStrings extends Messages{
    @Key("error")
    String error();

    @Key("errorReport")
    String errorReport(String name, String message);

    @Key("gwtVersion")
    String gwtVersion();

    @Key("gxtVersion")
    String gxtVersion();

    @Key("host")
    String host();

    @Key("serviceErrorCode")
    String serviceErrorCode(String errorCode);

    @Key("serviceErrorReason")
    String serviceErrorReason(String reason);

    @Key("serviceErrorStatus")
    String serviceErrorStatus(String status);

    @Key("userAgent")
    String userAgent();
}
