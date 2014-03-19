package org.iplantc.de.diskResource.client.presenters.callbacks;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A common callback for disk resource service calls that implements a common onFailure method that will
 * parse JSON error messages from the given Throwable::getMessage method to display to the user.
 * 
 * @author psarando
 * 
 */
public abstract class DiskResourceServiceCallback<T> implements AsyncCallback<T> {

    private IsMaskable maskedCaller;

    public DiskResourceServiceCallback(IsMaskable maskedCaller) {
        setMaskedCaller(maskedCaller);
    }

    private void setMaskedCaller(IsMaskable maskedCaller) {
        this.maskedCaller = maskedCaller;
    }

    protected void unmaskCaller() {
        if (maskedCaller == null) {
            return;
        }

        maskedCaller.unmask();
    }

    /**
     * Child classes are expected to override this method and call it as super. {@inheritDoc}
     */
    @Override
    public void onSuccess(final T result) {
        unmaskCaller();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFailure(Throwable caught) {
        unmaskCaller();

        ErrorHandler.post(caught);
    }

    /**
     * Gets a default error message to display to the user on failure, if no error code could be parsed
     * from the service response.
     * 
     * @return Default error message to display to the user.
     */
    protected abstract String getErrorMessageDefault();

}
