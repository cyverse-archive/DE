package org.iplantc.de.theme.base.client.commons.error;

import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class ErrorHandlerDefaultAppearance implements ErrorHandler.ErrorHandlerAppearance {
    private final ErrorStrings errorStrings;
    private final IplantDisplayStrings iplantDisplayStrings;

    public ErrorHandlerDefaultAppearance() {
        this(GWT.<ErrorStrings> create(ErrorStrings.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class));
    }

    ErrorHandlerDefaultAppearance(final ErrorStrings errorStrings,
                                  final IplantDisplayStrings iplantDisplayStrings) {
        this.errorStrings = errorStrings;
        this.iplantDisplayStrings = iplantDisplayStrings;
    }

    @Override
    public String date() {
        return iplantDisplayStrings.date();
    }

    @Override
    public String error() {
        return errorStrings.error();
    }

    @Override
    public String errorReport(String err_code, String message) {
        return errorStrings.errorReport(err_code, message);
    }

    @Override
    public String gwtVersion() {
        return errorStrings.gwtVersion();
    }

    @Override
    public String gxtVersion() {
        return errorStrings.gxtVersion();
    }

    @Override
    public String host() {
        return errorStrings.host();
    }

    @Override
    public String serviceErrorCode(String errorCode) {
        return errorStrings.serviceErrorCode(errorCode);
    }

    @Override
    public String serviceErrorReason(String reason) {
        return errorStrings.serviceErrorReason(reason);
    }

    @Override
    public String serviceErrorStatus(String status) {
        return errorStrings.serviceErrorStatus(status);
    }

    @Override
    public String userAgent() {
        return errorStrings.userAgent();
    }
}
