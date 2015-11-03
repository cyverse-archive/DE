package org.iplantc.de.fileViewers.client.callbacks;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel.LabelAlign;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.tips.QuickTip;


public class ShareAnonymousCallback implements AsyncCallback<String> {

    public interface ShareAnonymousCallbackAppearance {

        String copyPasteInstructions();

        String ensemblUrl();

        SafeHtml notificationWithContextHelp();

        String sendToEnsemblMenuItem();

    }

    private final IsMaskable container;
    private final File file;
    private final JsonUtil jsonUtil;
    private final ShareAnonymousCallbackAppearance appearance = GWT.create(ShareAnonymousCallbackAppearance.class);


    public ShareAnonymousCallback(final File file,
                                  final IsMaskable container) {
        this.container = container;
        this.file = file;
        this.jsonUtil = JsonUtil.getInstance();
    }

    @Override
    public void onFailure(Throwable caught) {
        if (container != null) {
            container.unmask();
        }
        ErrorHandler.post("Unable to retrieve URL's for Ensembl.", caught);
    }

    @Override
    public void onSuccess(String result) {
        if (container != null) {
            container.unmask();
        }
        JSONObject obj = jsonUtil.getObject(result);
        JSONObject paths = jsonUtil.getObject(obj, "paths");
        showShareLink(jsonUtil.getString(paths, file.getPath()));
    }


    private void showShareLink(String linkId) {
        // Open dialog window with text selected.
        IPlantDialog dlg = new IPlantDialog();
        dlg.setHeadingText(appearance.sendToEnsemblMenuItem());
        dlg.setHideOnButtonClick(true);
        dlg.setResizable(false);
        dlg.setSize("535", "175");

        FieldLabel fl = new FieldLabel();
        fl.setHTML(appearance.ensemblUrl());
        TextField textBox = new TextField();
        textBox.setWidth(500);
        textBox.setReadOnly(true);
        textBox.setValue(linkId);
        fl.setWidget(textBox);
        fl.setLabelAlign(LabelAlign.TOP);

        VerticalLayoutContainer container = new VerticalLayoutContainer();
        container.add(fl);
        container.add(new Label(appearance.copyPasteInstructions()));

        // Use a fl to get html
        FieldLabel notification = new FieldLabel();
        notification.setLabelSeparator("");
        notification.setLabelAlign(LabelAlign.TOP);
        notification.setHTML(appearance.notificationWithContextHelp());
        new QuickTip(notification);

        notification.setWidth(500);
        container.add(notification);
        dlg.setWidget(container);
        dlg.setFocusWidget(textBox);
        dlg.show();
        textBox.selectAll();
    }

}
