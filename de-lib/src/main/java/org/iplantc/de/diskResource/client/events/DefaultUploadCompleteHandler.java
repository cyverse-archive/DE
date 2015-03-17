package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.DiskResourceView;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * General handler for file upload. Expects to be called after form submission is complete.
 *
 * @author lenards, jstroot
 */
public class DefaultUploadCompleteHandler extends UploadCompleteHandler {

    interface DefaultUploadAutoBeanFactory extends AutoBeanFactory {
        AutoBean<File> file();
    }

    private final UserInfo userInfo;
    private final UserSessionServiceFacade userSessionService;
    private final DefaultUploadAutoBeanFactory factory = GWT.create(DefaultUploadAutoBeanFactory.class);
    private final JsonUtil jsonUtil;


    /**
     * Construct a new instance of the default handler.
     *
     * @param idParent the parent identifier to upload the file
     */
    public DefaultUploadCompleteHandler(final UserSessionServiceFacade userSessionService,
                                        String idParent) {
        super(idParent,
              GWT.<DiskResourceView.Presenter.Appearance> create(DiskResourceView.Presenter.Appearance.class));
        this.userSessionService = userSessionService;
        this.jsonUtil = JsonUtil.getInstance();
        userInfo = UserInfo.getInstance();
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

            json.put("user", new JSONString(userInfo.getUsername()));
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
            throw new Exception(((DiskResourceView.Presenter.Appearance) GWT.create(DiskResourceView.Presenter.Appearance.class)).fileUploadsFailed(Lists.newArrayList(sourceUrl)) + ": " + json); //$NON-NLS-1$
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
        AutoBean<File> file = AutoBeanCodex.decode(factory, File.class, jsonObj.toString());
        String filename = file.as().getName();

        if (!filename.isEmpty()) {
            return ((DiskResourceView.Presenter.Appearance) GWT.create(DiskResourceView.Presenter.Appearance.class)).fileUploadSuccess(filename);
        }

        String sourceUrl = jsonUtil.getString(jsonObj, "sourceUrl"); //$NON-NLS-1$

        return ((DiskResourceView.Presenter.Appearance) GWT.create(DiskResourceView.Presenter.Appearance.class)).importFailed(sourceUrl);
    }

    private String processXMLErrorMsg(String sourceUrl, String message) {
        try {
            Document d = XMLParser.parse(message);
            return d.getElementsByTagName("error").item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            return ((DiskResourceView.Presenter.Appearance) GWT.create(DiskResourceView.Presenter.Appearance.class)).fileUploadsFailed(Lists.newArrayList(sourceUrl)) + ": " + message; //$NON-NLS-1$
        }
    }
}
