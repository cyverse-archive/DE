package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * General handler for file upload. Expects to be called after form submission is complete.
 *
 * @author lenards
 */
public class DefaultUploadCompleteHandler extends UploadCompleteHandler {

    private final UserSessionServiceFacade userSessionService;
    private final DiskResourceAutoBeanFactory drFactory;
    private final JsonUtil jsonUtil;

    /**
     * Construct a new instance of the default handler.
     *
     * @param idParent the parent identifier to upload the file
     */
    public DefaultUploadCompleteHandler(final UserSessionServiceFacade userSessionService,
                                        final DiskResourceAutoBeanFactory drFactory,
                                        String idParent) {
        super(idParent);
        this.userSessionService = userSessionService;
        this.drFactory = drFactory;
        this.jsonUtil = JsonUtil.getInstance();
    }

    public JSONObject build(final JSONObject json) {
        JSONObject ret = null; // assume failure

        if (json != null) {
            ret = new JSONObject();
            ret.put("type", new JSONString("data"));
            final String messageText = buildMessageText(json);
            ret.put("subject", new JSONString(messageText));
            JSONObject payload = new JSONObject();
            payload.put("action", new JSONString("UPLOAD_COMPLETE"));
            payload.put("data", json);
            ret.put("payload", payload);
        }

        return ret;
    }

    /**
     * Invoked immediately following onCompletion().
     * <p/>
     * Provides a manner for handlers to do cleanup or post-completion operations.
     */
    @Override
    public void onAfterCompletion() {
        // Let the specific instance provide an implementation. This is not abstract
        // because then the class would to be abstract and there might be a case
        // when you want to use this without defining this action.
    }

    /**
     * Invoked on completion of file upload form submission.
     *
     * @param sourceUrl the URL the file is being imported from, or the filename if uploading a local
     *                  file
     * @param response  the server response in JSON format
     */
    @Override
    public void onCompletion(String sourceUrl, String response) {
        try {
            JSONObject payload = buildPayload(sourceUrl, response);

            final JSONObject json = build(payload);

            json.put("user", new JSONString(UserInfo.getInstance().getUsername()));
            json.put("email", JSONBoolean.getInstance(false));

            userSessionService.postClientNotification(json, new AsyncCallback<String>() {

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
        }
    }

    protected JSONObject buildPayload(String sourceUrl, String json) throws Exception {
        JSONObject jsonObj = jsonUtil.getObject(jsonUtil.formatString(json));
        if (jsonObj == null) {
            throw new Exception(I18N.ERROR.fileUploadsFailed(Lists.newArrayList(sourceUrl)) + ": " + json); //$NON-NLS-1$
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

    private String buildMessageText(final JSONObject jsonObj) {
        AutoBean<File> file = AutoBeanCodex.decode(drFactory, File.class, jsonObj.toString());
        String filename = file.as().getName();

        if (!filename.isEmpty()) {
            return I18N.DISPLAY.fileUploadSuccess(filename);
        }

        String sourceUrl = jsonUtil.getString(jsonObj, "sourceUrl"); //$NON-NLS-1$

        return I18N.ERROR.importFailed(sourceUrl);
    }

    private String processXMLErrorMsg(String sourceUrl, String message) {
        try {
            Document d = XMLParser.parse(message);
            return d.getElementsByTagName("error").item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            return I18N.ERROR.fileUploadsFailed(Lists.newArrayList(sourceUrl)) + ": " + message; //$NON-NLS-1$
        }
    }
}
