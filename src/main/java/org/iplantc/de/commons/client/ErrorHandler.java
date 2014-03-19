package org.iplantc.de.commons.client;

import org.iplantc.de.client.models.errorHandling.ServiceError;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.views.gxt3.dialogs.ErrorDialog3;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;

import com.sencha.gxt.core.client.GXT;

import java.util.Date;

/**
 * Provides a uniform manner for posting errors to the user.
 *
 * @author amuir
 *
 */
public class ErrorHandler {
    private static final String NEWLINE = "\n"; //$NON-NLS-1$

    /**
     * Post a message box with error styles with the argument error message.
     *
     * @param error the string message to include in the displayed dialog
     */
    public static void post(String error) {
        post(error, null);
    }

    /**
     * Post a message box with error styles with a general error message summary and the given caught
     * with additional error details.
     *
     * @param caught
     */
    public static void post(Throwable caught) {
        post(I18N.ERROR.error(), caught);
    }

    /**
     * Post a message box with error styles with the given error message summary and optional caught with
     * additional error details.
     *
     * @param errorSummary
     * @param caught
     */
    public static void post(String errorSummary, Throwable caught) {
        if (Strings.isNullOrEmpty(errorSummary)) {
            errorSummary = I18N.ERROR.error();
        }

        post(SafeHtmlUtils.fromString(errorSummary), caught);
    }

    /**
     * Post a message box with error styles with the given error message summary and optional caught with
     * additional error details.
     * 
     * @param errorSummary
     * @param caught
     */
    public static void post(SafeHtml errorSummary, Throwable caught) {
        String errorDetails = getSystemDescription();

        if (errorSummary == null) {
            errorSummary = SafeHtmlUtils.fromString(I18N.ERROR.error());
        }

        if (caught != null) {
            GWT.log(errorSummary.asString(), caught);

            errorDetails = parseExceptionJson(caught) + NEWLINE + NEWLINE + errorDetails;
        }

        ErrorDialog3 ed3 = new ErrorDialog3(errorSummary, errorDetails);
        ed3.show();
    }

    /**
     * Posts a message box with the error message summary provided by the <code>ServiceError</code> object.
     *
     * TODO JDS Using info from given ServiceError, create new Throwable to pass to sibling overridden method.
     * @param error the error object representing the error.
     * @param caught
     */
    public static void post(ServiceError error, Throwable caught) {

        // Build a new Exception message for the ErrorHandler details panel.
        String errDetails = ""; //$NON-NLS-1$
        SafeHtml errorMsg = SafeHtmlUtils.fromString("");
        if (!Strings.isNullOrEmpty(error.getStatus())) {
            errDetails += I18N.ERROR.serviceErrorStatus(error.getStatus());
        }
        if (!Strings.isNullOrEmpty(error.getErrorCode())) {
            errDetails += "\n" + I18N.ERROR.serviceErrorCode(error.getErrorCode()); //$NON-NLS-1$
        }

        if(error != null) {
            errorMsg= error.generateErrorMsg();
        }

        /*
         * JDS - The if block below used to be in DiskResourceServiceCallback in DE-Webapp. The issue is
         * that it used to be for a field named "reason" in the error json response. Using the new "ErrorMsg" roll up
         * is going to be a duplicate.
         * TODO JDS Need to determine what the default error fields are, and if they include a "Reason" field.
         */
        if (!Strings.isNullOrEmpty(error.getReason())) {
            errDetails += "\n" + I18N.ERROR.serviceErrorReason(error.getReason()); //$NON-NLS-1$
        } else if (errorMsg != null && !Strings.isNullOrEmpty(errorMsg.asString())) {
            errDetails += "\n" + I18N.ERROR.serviceErrorReason(errorMsg.asString()); //$NON-NLS-1$
        }


        Throwable newCaught = new Exception(errDetails, caught);
        post(errorMsg, newCaught);
    }

    private static String parseExceptionJson(Throwable caught) {
        String exceptionMessage = caught.getMessage();

        JSONObject jsonError = null;
        try {
            jsonError = JsonUtil.getObject(exceptionMessage);
        } catch (Exception ignoreParseErrors) {
            // intentionally ignore JSON parse errors
        }

        if (jsonError != null) {
            String name = JsonUtil.getString(jsonError, "name"); //$NON-NLS-1$
            String message = JsonUtil.getString(jsonError, "message"); //$NON-NLS-1$

            if (!message.isEmpty() || !name.isEmpty()) {
                exceptionMessage = I18N.ERROR.errorReport(name, message);
            }
        }

        return exceptionMessage;
    }

    /**
     * Builds a string with details about the GXT user agent and version, and GWT version.
     *
     * @return A system description string.
     */
    private static String getSystemDescription() {
        String gwtVersion = I18N.DISPLAY.gwtVersion() + " " + GWT.getVersion(); //$NON-NLS-1$

        String gxtVersion = I18N.DISPLAY.gxtVersion() + 
                 ": " + GXT.getVersion().getRelease();

        String userAgent = I18N.DISPLAY.userAgent() + " " + Window.Navigator.getUserAgent(); //$NON-NLS-1$

        String date = I18N.DISPLAY.date() + ": " + new Date().toString(); //$NON-NLS-1$

        String host = I18N.DISPLAY.host() + ": " + GWT.getHostPageBaseURL();

        return gwtVersion + NEWLINE + gxtVersion + NEWLINE + userAgent + NEWLINE + date + NEWLINE + host;
    }

}
