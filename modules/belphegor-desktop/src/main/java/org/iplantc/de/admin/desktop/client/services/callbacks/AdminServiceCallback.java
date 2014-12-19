package org.iplantc.de.admin.desktop.client.services.callbacks;

import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.widget.core.client.Component;

/**
 * A common callback for administrative service calls that implements common onSuccess and onFailure
 * methods, that will unmask a given caller and parse JSON messages from success or error responses.
 * 
 * @author psarando
 * 
 */
public abstract class AdminServiceCallback implements AsyncCallback<String> {
    public static final String SUCCESS = "success"; //$NON-NLS-1$

    protected Component maskedCaller;
    private final JsonUtil jsonUtil = JsonUtil.getInstance();

    /**
     * Sets a reference to a caller that is masked during the service call, and should be unmasked after
     * a response is received.
     */
    public void setMaskedCaller(Component maskedCaller) {
        this.maskedCaller = maskedCaller;
    }

    protected void unmaskCaller() {
        if (maskedCaller != null) {
            maskedCaller.unmask();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSuccess(String result) {
        try {
            JSONObject jsonResult = getJsonResponse(result);

            onSuccess(jsonResult);

            unmaskCaller();
        } catch (Throwable e) {
            onFailure(e);
        }
    }

    /**
     * Handles any additional processing of a successful service call.
     * 
     * @param jsonResult The parsed JSON results of the successful service call.
     */
    protected abstract void onSuccess(final JSONObject jsonResult);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFailure(Throwable caught) {
        unmaskCaller();

        ErrorHandler.post(getErrorMessage(), caught);
    }

    /**
     * Gets a default error message to display to the user on failure.
     * 
     * @return Error message to display to the user on failure.
     */
    protected abstract String getErrorMessage();

    /**
     * Parses result as a JSON object, checks for a success field, then returns the parsed object. Throws
     * an exception with result as its message if the given string is not a JSON object or if a false
     * success value was parsed from the object.
     * 
     * @param result The JSON object string.
     * @return The parsed JSON object.
     * @throws Throwable if result is not a JSON object or a false success value is parsed from result.
     */
    protected JSONObject getJsonResponse(String result) throws Throwable {

        JSONObject ret = jsonUtil.getObject(result);
        if (ret == null) {
            throw new Exception(result);
        }

        if (!jsonUtil.getBoolean(ret, SUCCESS, true)) {
            throw new Exception(result);
        }

        return ret;
    }
}
