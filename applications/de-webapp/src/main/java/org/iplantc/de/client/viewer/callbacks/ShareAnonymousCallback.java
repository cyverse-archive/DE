package org.iplantc.de.client.viewer.callbacks;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel.LabelAlign;
import com.sencha.gxt.widget.core.client.form.TextField;

public class ShareAnonymousCallback implements AsyncCallback<String> {
    
    private final IsMaskable container;
    private final File file;
    
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
        dlg.setSize("535", "130");
        FieldLabel fl = new FieldLabel();
        fl.setHTML("Please vist <a target='_blank' href='http://www.ensembl.org/'>Ensembl</a> and use the following URL to import your bam / vcf / gff file");
        TextField textBox = new TextField();
        textBox.setWidth(500);
        textBox.setReadOnly(true);
        textBox.setValue(linkId);
        fl.setWidget(textBox);
        fl.setLabelAlign(LabelAlign.TOP);
        VerticalLayoutContainer container = new VerticalLayoutContainer();
        dlg.setWidget(container);
        container.add(fl);
        container.add(new Label(I18N.DISPLAY.copyPasteInstructions()));
        dlg.setFocusWidget(textBox);
        dlg.show();
        textBox.selectAll();
    }

}
