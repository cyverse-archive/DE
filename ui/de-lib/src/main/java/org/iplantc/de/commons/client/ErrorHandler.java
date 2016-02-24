package org.iplantc.de.commons.client;

import org.iplantc.de.client.models.errorHandling.ServiceError;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.views.dialogs.ErrorDialog;

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
 * @author amuir, jstroot
 *
 */
public class ErrorHandler {
    public interface ErrorHandlerAppearance {

        String date();

        String error();

        String errorReport(String name, String message);

        String gwtVersion();

        String gxtVersion();

        String host();

        String serviceErrorCode(String errorCode);

        String serviceErrorReason(String reason);

        String serviceErrorStatus(String status);

        String userAgent();
    }


    private static final ErrorHandlerAppearance appearance;
    static {
        appearance = GWT.create(ErrorHandlerAppearance.class);
    }

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
        post(appearance.error(), caught);
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
            errorSummary = appearance.error();
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
            errorSummary = SafeHtmlUtils.fromString(appearance.error());
        }

        if (caught != null) {
            GWT.log(errorSummary.asString(), caught);

            errorDetails = parseExceptionJson(caught) + NEWLINE + NEWLINE + errorDetails;
        }

        ErrorDialog ed3 = new ErrorDialog(errorSummary, errorDetails);
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
            errDetails += appearance.serviceErrorStatus(error.getStatus());
        }
        if (!Strings.isNullOrEmpty(error.getErrorCode())) {
            errDetails += "\n" + appearance.serviceErrorCode(error.getErrorCode()); //$NON-NLS-1$
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
            errDetails += "\n" + appearance.serviceErrorReason(error.getReason()); //$NON-NLS-1$
        } else if (errorMsg != null && !Strings.isNullOrEmpty(errorMsg.asString())) {
            errDetails += "\n" + appearance.serviceErrorReason(errorMsg.asString()); //$NON-NLS-1$
        }


        Throwable newCaught = new Exception(errDetails, caught);
        post(errorMsg, newCaught);
    }

    private static String parseExceptionJson(Throwable caught) {
        String exceptionMessage = caught.getMessage();

        JSONObject jsonError = null;
        try {
            jsonError = JsonUtil.getInstance().getObject(exceptionMessage);
        } catch (Exception ignoreParseErrors) {
            // intentionally ignore JSON parse errors
        }

        if (jsonError != null) {
            String error_code = JsonUtil.getInstance().getString(jsonError, "error_code"); //$NON-NLS-1$
            String message = JsonUtil.getInstance().getString(jsonError, "reason"); //$NON-NLS-1$

            if (!message.isEmpty() || !error_code.isEmpty()) {
                exceptionMessage = appearance.errorReport(error_code, message);
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
        String gwtVersion = appearance.gwtVersion() + " " + GWT.getVersion(); //$NON-NLS-1$

        String gxtVersion = appearance.gxtVersion() +
                 ": " + GXT.getVersion().getRelease();

        String userAgent = appearance.userAgent() + " " + Window.Navigator.getUserAgent(); //$NON-NLS-1$

        String date = appearance.date() + ": " + new Date().toString(); //$NON-NLS-1$

        String host = appearance.host() + ": " + GWT.getHostPageBaseURL();

        return gwtVersion + NEWLINE + gxtVersion + NEWLINE + userAgent + NEWLINE + date + NEWLINE + host;
    }

}
