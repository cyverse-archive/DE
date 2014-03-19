package org.iplantc.de.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;

/**
 * Detects when the user is not logged in to the application and redirects the user to the login page.  Under normal
 * circumstances, we'll receive a 302 status code if the user is not authenticated, but we also have to check for a
 * status code of 0 because GWT doesn't currently return the correct status code.
 *
 * @author Dennis Roberts
 *
 * @param <T> the type of the result we're expecting to get from the server.
 */
public class AsyncCallbackWrapper<T> implements AsyncCallback<T> {
    private static final String LANDING_PAGE = "logged-out";

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
        Window.Location.replace(GWT.getModuleBaseURL() + LANDING_PAGE);
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
            redirectToLandingPage();
        }
        if (error instanceof StatusCodeException) {
            int statusCode = ((StatusCodeException)error).getStatusCode();
            if (statusCode == 302 || statusCode == 0) {
                redirectToLandingPage();
            }
        }
        callback.onFailure(error);
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
