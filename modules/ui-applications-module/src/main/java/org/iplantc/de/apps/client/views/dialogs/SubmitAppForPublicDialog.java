/**
 * 
 */
package org.iplantc.de.apps.client.views.dialogs;

import org.iplantc.de.apps.client.events.AppCategoryCountUpdateEvent;
import org.iplantc.de.apps.client.views.SubmitAppForPublicUseView;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * @author sriram
 * 
 */
public class SubmitAppForPublicDialog extends IPlantDialog {


    private final class SubmitAppForPublicCallbackImpl implements AsyncCallback<String> {
        @Override
        public void onSuccess(String appName) {
            hide();

            IplantAnnouncer.getInstance().schedule(
new SuccessAnnouncementConfig(SafeHtmlUtils.fromTrustedString(I18N.DISPLAY.makePublicSuccessMessage(appName))));

            // Create and fire event
            AppCategoryCountUpdateEvent event = new AppCategoryCountUpdateEvent(false,
                    AppCategoryCountUpdateEvent.AppCategoryType.BETA);
            EventBus.getInstance().fireEvent(event);
        }

        @Override
        public void onFailure(Throwable caught) {
            hide();
            if (caught != null) {
                String errorMessage = getErrorMessage(caught);
                if(errorMessage.equals("")) {
                    ErrorHandler.post(I18N.DISPLAY.makePublicFail(), caught);
                } else {
                    ErrorHandler.post(I18N.DISPLAY.makePublicFail() + "Reason: " + errorMessage  , caught);
                }
            }
        }
    }

    private String getErrorMessage(Throwable caught) {
        JSONObject obj = jsonUtil.getObject(caught.getMessage());
        if(obj != null) {
            return jsonUtil.getString(obj, "reason");
        }
        return  "";
    }

    private final JsonUtil jsonUtil;
    
    public SubmitAppForPublicDialog(final App selectedApp,
                                    final SubmitAppForPublicUseView.Presenter presenter) {
        jsonUtil = JsonUtil.getInstance();
        initDialog();
        presenter.go(this, selectedApp, new SubmitAppForPublicCallbackImpl());
        setOkButtonText(I18N.DISPLAY.submit());
        addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                presenter.onSubmit();
            }
        });
        addCancelButtonSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                hide();
                
            }
        });
    }

    private void initDialog() {
        setHeadingText(I18N.DISPLAY.publicSubmissionForm()); //$NON-NLS-1$
        setPixelSize(615, 480);
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        setHideOnButtonClick(false);
    }

}
