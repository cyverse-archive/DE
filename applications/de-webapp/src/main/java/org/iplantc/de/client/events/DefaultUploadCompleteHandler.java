package org.iplantc.de.client.events;

import org.iplantc.de.client.factories.EventJSONFactory;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

/**
 * General handler for file upload. Expects to be called after form submission is complete.
 * 
 * @author lenards
 * 
 */
public class DefaultUploadCompleteHandler extends UploadCompleteHandler {
    /**
     * Construct a new instance of the default handler.
     * 
     * @param idParent the parent identifier to upload the file
     */
    public DefaultUploadCompleteHandler(String idParent) {
        super(idParent);
    }

    protected JSONObject buildPayload(String sourceUrl, String json) throws Exception {
        JSONObject jsonObj = JsonUtil.getObject(JsonUtil.formatString(json));
        if (jsonObj == null) {
            throw new Exception(I18N.ERROR.fileUploadFailed(sourceUrl) + ": " + json); //$NON-NLS-1$
        }

        // since our current file info objects don't have a parent folder id, we have to add it in.
        // This is further complicated by the fact the messages we receive will have this field.
        // The following code is to transform our file info retrieved from upload completion into the
        // form
        // we expect in the message.
        jsonObj.put("parentFolderId", new JSONString(getParentId())); //$NON-NLS-1$
        jsonObj.put("sourceUrl", new JSONString(sourceUrl)); //$NON-NLS-1$

        return jsonObj;
    }

    /**
     * Invoked on completion of file upload form submission.
     * 
     * @param sourceUrl the URL the file is being imported from, or the filename if uploading a local
     *            file
     * @param response the server response in JSON format
     */
    @Override
    public void onCompletion(String sourceUrl, String response) {
        try {
            JSONObject payload = buildPayload(sourceUrl, response);
            final JSONObject json = EventJSONFactory.build(EventJSONFactory.ActionType.UPLOAD_COMPLETE,
                    payload);

            json.put("user", new JSONString(UserInfo.getInstance().getUsername()));
            json.put("email", JSONBoolean.getInstance(false));

            ServicesInjector.INSTANCE.getUserSessionServiceFacade().postClientNotification(json, new AsyncCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    // do nothing intentionally

                }

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(caught);
                }
            });

        } catch (Exception e) {
            ErrorHandler.post(processXMLErrorMsg(sourceUrl, response), e);
        } /*
           * finally { // TODO: consider having onCompletion and onAfterCompletion called by superclass
           * // method to more appropriately confirm w/ Template Method and Command patterns
           * onAfterCompletion(); }
           */
    }

    private String processXMLErrorMsg(String sourceUrl, String message) {
        try {
            Document d = XMLParser.parse(message);
            String error = d.getElementsByTagName("error").item(0).getFirstChild().getNodeValue(); //$NON-NLS-1$
            return error;
        } catch (Exception e) {
            return I18N.ERROR.fileUploadFailed(sourceUrl) + ": " + message; //$NON-NLS-1$
        }
    }

    /**
     * Invoked immediately following onCompletion().
     * 
     * Provides a manner for handlers to do cleanup or post-completion operations.
     */
    @Override
    public void onAfterCompletion() {
        // Let the specific instance provide an implementation. This is not abstract
        // because then the class would to be abstract and there might be a case
        // when you want to use this without defining this action.
    }
}
