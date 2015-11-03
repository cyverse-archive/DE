package org.iplantc.de.fileViewers.client.callbacks;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.dialogs.IplantInfoBox;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author jstroot
 */
public class LoadGenomeInCoGeCallback implements AsyncCallback<String> {

    public interface LoadGenomeInCogeCallbackAppearance {

        String coge();

        String cogeError();

        String cogeResponse(String url);
    }

    private final IsMaskable container;
    private final JsonUtil jsonUtil = JsonUtil.getInstance();
    final LoadGenomeInCogeCallbackAppearance appearance = GWT.create(LoadGenomeInCogeCallbackAppearance.class);

    public LoadGenomeInCoGeCallback(final IsMaskable container) {
        this.container = container;
    }

    @Override
    public void onFailure(Throwable caught) {
        if (container != null) {
            container.unmask();
        }
        ErrorHandler.post(appearance.cogeError(), caught);
    }

    @Override
    public void onSuccess(String result) {
        JSONObject resultObj = jsonUtil.getObject(result);
        String url = jsonUtil.getString(resultObj, "coge_genome_url");
        if (!Strings.isNullOrEmpty(url)) {
            IplantInfoBox iib = new IplantInfoBox(appearance.coge(), appearance.cogeResponse(url));
            iib.show();
        } else {
            onFailure(null);
        }
        if (container != null) {
            container.unmask();
        }

    }

}
