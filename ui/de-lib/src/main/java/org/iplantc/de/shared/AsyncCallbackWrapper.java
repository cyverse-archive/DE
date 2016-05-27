package org.iplantc.de.shared;

import org.iplantc.de.shared.exceptions.AuthenticationException;
import org.iplantc.de.shared.exceptions.HttpRedirectException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;

import org.apache.http.HttpStatus;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Detects when the user is not logged in to the application and redirects the user to the login page.  Under normal
 * circumstances, we'll receive a 302 status code if the user is not authenticated. Formerly this checked for status
 * code 0, but that should no longer be necessary.
 *
 * @author Dennis Roberts
 *
 * @param <T> the type of the result we're expecting to get from the server.
 */
public class AsyncCallbackWrapper<T> implements AsyncCallback<T> {
    private static final String LANDING_PAGE = "logged-out";

    Logger LOG = Logger.getLogger(AsyncCallbackWrapper.class.getName());
    /**
     * The callback that we're wrapping.
     */
    private AsyncCallback<T> callback;

    /**
     * Creates a new callback wrapper.
     *
     * @param callback the callback that we're wrapping.
     */

    public AsyncCallbackWrapper(AsyncCallback<T> callback) {
        this.callback = callback;
    }

    /**
     * Redirects the user to the DE landing page.
     */
    private void redirectToLandingPage() {
        Window.Location.replace(GWT.getHostPageBaseURL() + LANDING_PAGE);
    }

    /**
     * Called whenever a call to the server fails. If the call failed because of an HTTP status code and
     * that status code represents a redirect request or wasn't recorded then we assume that the user isn't
     * logged in and redirect the user to the login page. The callback that we're wrapping deals with all
     * other errors.
     *
     * @param error the exception or error that indicates why the call failed.
     */
    @Override
    public void onFailure(Throwable error) {
        if (error instanceof AuthenticationException) {
            LOG.log(Level.SEVERE, "Auth error!!!!!", error);
            redirectToLandingPage();
            return;
        }
        if (error instanceof StatusCodeException) {
            int statusCode = ((StatusCodeException)error).getStatusCode();
            LOG.log(Level.SEVERE, "Status code: " + statusCode, error);
            if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                redirectToLandingPage();
                return;
            }
        }

        callback.onFailure(error);

        if (error instanceof HttpRedirectException) {
            LOG.log(Level.INFO, "Redirecting to", error);
            HttpRedirectException e = (HttpRedirectException) error;
            Window.Location.replace(e.getLocation());
        }

    }

    /**
     * Called whenever a call to the server succeeds. The callback that we're wrapping deals with all
     * successful calls.
     *
     * @param response the response from the server.
     */
    @Override
    public void onSuccess(T response) {
        callback.onSuccess(response);
    }
}
