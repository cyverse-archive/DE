package org.iplantc.de.shared;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.shared.events.UserLoggedOutEvent;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author aramsey
 *
 * AsyncProviderWrapper should be used in place of AsyncProvider in order to
 * standardize how we handle failures during the callback.
 *
 * AsyncProvider is used to perform code splitting in GWT.  When the code splitting download
 * fails, like in the case where the user is deauthenticated prior to downloading the code split,
 * DeCasAuthenticationEntryPoint correctly responds with a 401, but the client needs to handle
 * the redirect to the landing page properly.
 *
 * When the failure happens, note that it's not an AuthenticationException or StatusCodeException,
 * it's some sort of AsyncFragmentLoader$HttpDownloadFailure that I haven't found a way to explicitly
 * check for.  If a method is found, you can probably switch from AsyncCallback to AsyncCallbackWrapper
 * so all failures (RPC, websockets, or code splits) are handled in AsyncCallbackWrapper.
 *
 * Also note: it is NOT possible to test out code splitting with SDM running.
 */
public class AsyncProviderWrapper<T> {

    Logger LOG = Logger.getLogger(AsyncProviderWrapper.class.getName());
    
    @Inject AsyncProvider<T> provider;
    public AsyncProviderWrapper() {

    }

    public void get(final AsyncCallback callback) {
        provider.get(new AsyncCallback<T>() {
            @Override
            public void onFailure(Throwable caught) {
                LOG.log(Level.SEVERE, caught.getMessage());
                EventBus.getInstance().fireEvent(new UserLoggedOutEvent());
            }

            @Override
            public void onSuccess(T result) {
                callback.onSuccess(result);
            }
        });
    }
}
