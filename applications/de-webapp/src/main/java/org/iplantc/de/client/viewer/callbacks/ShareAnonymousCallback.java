package org.iplantc.de.client.viewer.callbacks;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel.LabelAlign;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.tips.QuickTip;


public class ShareAnonymousCallback implements AsyncCallback<String> {

    interface EnsemblPopupTemplate extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("{0}<img src='{1}' qtip='{2}'></img>")
        SafeHtml notificationWithContextHelp(SafeHtml label, SafeUri img, String toolTip);
    }

    private final IsMaskable container;
    private final File file;
    private final EnsemblPopupTemplate template = GWT.create(EnsemblPopupTemplate.class);

    public ShareAnonymousCallback(File file, IsMaskable container) {
        this.container = container;
        this.file = file;
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
        JSONObject obj = JsonUtil.getObject(result);
        JSONObject paths = JsonUtil.getObject(obj, "paths");
        showShareLink(JsonUtil.getString(paths, file.getPath()));
    }


    private void showShareLink(String linkId) {
        // Open dialog window with text selected.
        IPlantDialog dlg = new IPlantDialog();
        dlg.setHeadingText(I18N.DISPLAY.sendToEnsemblMenuItem());
        dlg.setHideOnButtonClick(true);
        dlg.setResizable(false);
        dlg.setSize("535", "150");

        FieldLabel fl = new FieldLabel();
        fl.setHTML(I18N.DISPLAY.ensemblUrl());
        TextField textBox = new TextField();
        textBox.setWidth(500);
        textBox.setReadOnly(true);
        textBox.setValue(linkId);
        fl.setWidget(textBox);
        fl.setLabelAlign(LabelAlign.TOP);

        VerticalLayoutContainer container = new VerticalLayoutContainer();
        container.add(fl);
        container.add(new Label(I18N.DISPLAY.copyPasteInstructions()));

        // Use a fl to get html
        FieldLabel notification = new FieldLabel();
        notification.setLabelSeparator("");
        notification.setLabelAlign(LabelAlign.TOP);
        notification.setHTML(template.notificationWithContextHelp(I18N.DISPLAY.sendToEnsemblePopupNote(), IplantResources.RESOURCES.help().getSafeUri(), I18N.HELP.sendToEnsemblUrlHelp()));
        new QuickTip(notification);

        notification.setWidth(500);
        container.add(notification);
        dlg.setWidget(container);
        dlg.setFocusWidget(textBox);
        dlg.show();
        textBox.selectAll();
    }

}
