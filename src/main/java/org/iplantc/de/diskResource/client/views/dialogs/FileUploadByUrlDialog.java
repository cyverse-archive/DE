package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.validators.ImportUrlValidator;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.fx.client.FxElement;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.FormPanelHelper;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.ValueBaseField;
import com.sencha.gxt.widget.core.client.tips.ToolTip;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FileUploadByUrlDialog extends IPlantDialog implements HasPending<Entry<Field<String>, Status>> {

    private static final DiskResourceAutoBeanFactory FS_FACTORY = GWT.create(DiskResourceAutoBeanFactory.class);

    private static FileUploadByUrlPanelUiBinder UIBINDER = GWT.create(FileUploadByUrlPanelUiBinder.class);

    @UiTemplate("FileUploadByUrlPanel.ui.xml")
    interface FileUploadByUrlPanelUiBinder extends UiBinder<Widget, FileUploadByUrlDialog> {}

    private static final String FIELD_HEIGHT = "50";
    private static final String FIELD_WIDTH = "475";
    private final Folder uploadDest;
    private final DiskResourceServiceFacade drService;
    private final Set<Entry<Field<String>, Status>> pendingList = Sets.newHashSet();
    private final Map<Field<String>, Status> fieldToStatusMap = Maps.newHashMap();

    @UiField
    HTML htmlDestText;
    @UiField
    TextArea url0, url1, url2, url3, url4;
    @UiField
    Status formStatus0, formStatus1, formStatus2, formStatus3, formStatus4;

    public FileUploadByUrlDialog(Folder uploadDest, DiskResourceServiceFacade drService, String userName) {
        this.uploadDest = uploadDest;
        this.drService = drService;
        setAutoHide(false);
        setHideOnButtonClick(false);
        // Reset the "OK" button text.
        getOkButton().setText(I18N.DISPLAY.urlImport());
        getOkButton().setEnabled(false);
        setHeadingText(I18N.DISPLAY.importLabel());

        add(UIBINDER.createAndBindUi(this));

        // Load up our field to status map
        fieldToStatusMap.put(url0, formStatus0);
        fieldToStatusMap.put(url1, formStatus1);
        fieldToStatusMap.put(url2, formStatus2);
        fieldToStatusMap.put(url3, formStatus3);
        fieldToStatusMap.put(url4, formStatus4);
        initDestPathLabel();

        addCancelButtonSelectHandler(new HideSelectHandler(this));
    }

    private void initDestPathLabel() {
        String destPath = uploadDest.getPath();

        htmlDestText.setHTML(Format.ellipse(I18N.DISPLAY.uploadingToFolder(destPath), 80));
        new ToolTip(htmlDestText, new ToolTipConfig(destPath));
    }

    @UiFactory
    SimpleContainer buildHlc() {
        SimpleContainer hlc = new SimpleContainer();
        hlc.setSize(FIELD_WIDTH, FIELD_HEIGHT);
        return hlc;
    }

    @UiFactory
    TextArea buildUrlField() {
        TextArea urlField = new TextArea();
        urlField.addValidator(new ImportUrlValidator());
        urlField.setAutoValidate(true);
        return urlField;
    }

    @UiFactory
    Status createFormStatus() {
        Status status = new Status();
        status.setWidth(15);
        return status;
    }

    @UiHandler({"url0", "url1", "url2", "url3", "url4"})
    void onFieldValid(ValidEvent event) {
        getOkButton().setEnabled(FormPanelHelper.isValid(this, true) && isValidForm());
    }

    @UiHandler({"url0", "url1", "url2", "url3", "url4"})
    void onFieldInvalid(InvalidEvent event){
        getOkButton().setEnabled(false);
    }

    private boolean isValidForm(){
        for(Entry<Field<String>, Status> entry : fieldToStatusMap.entrySet()){
            ValueBaseField<String> valueBaseField = (ValueBaseField<String>)entry.getKey();
            if ((valueBaseField.getCurrentValue() != null) && !valueBaseField.getCurrentValue().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onOkButtonClicked() {
//        formStatus.setBusy(I18N.DISPLAY.uploadingToFolder(uploadDest.getId()));
//        formStatus.show();
        getOkButton().setEnabled(false);

        final FastMap<Field<String>> destResourceMap = new FastMap<Field<String>>();

        for (Entry<Field<String>, Status> entry : fieldToStatusMap.entrySet()) {
            Field<String> field = entry.getKey();
            if(field.getValue() == null)
                continue;
            String url = field.getValue().trim();
            if (!url.isEmpty()) {
                Status status = entry.getValue();
                status.setBusy("");
                status.show();
                field.setValue(url);
                String resourceId = uploadDest.getPath() + "/" + DiskResourceUtil.parseNameFromPath(url);
                destResourceMap.put(resourceId, field);
            } else {
                field.setEnabled(false);
            }
        }

        if (!destResourceMap.isEmpty()) {
            final HasPaths dto = FS_FACTORY.pathsList().as();
            dto.setPaths(Lists.newArrayList(destResourceMap.keySet()));
            drService.diskResourcesExist(dto, new CheckDuplicatesCallback<FileUploadByUrlDialog>(destResourceMap, fieldToStatusMap, uploadDest, drService, this));
        }
    }

    @Override
    public boolean addPending(Entry<Field<String>, Status> obj) {
        return pendingList.add(obj);
    }

    @Override
    public boolean hasPending() {
        return !pendingList.isEmpty();
    }

    @Override
    public boolean removePending(Entry<Field<String>, Status> obj) {
        return pendingList.remove(obj);
    }

    @Override
    public int getNumPending() {
        return pendingList.size();
    }

    private final class CheckDuplicatesCallback <D extends UIObject & IsHideable & HasPending<Entry<Field<String>, Status>>> extends DuplicateDiskResourceCallback {
        private final Map<String, Field<String>> destResourceMap;
        private final Folder uploadDest;
        private final DiskResourceServiceFacade drService;
        private final D dlg;
        private final Map<Field<String>, Status> fieldToStatusMap;

        public CheckDuplicatesCallback(Map<String, Field<String>> destResourceMap, Map<Field<String>, Status> fieldToStatusMap, Folder uploadDest, DiskResourceServiceFacade drService, D dlg) {
            super(Lists.newArrayList(destResourceMap.keySet()), null);
            this.destResourceMap = destResourceMap;
            this.fieldToStatusMap = fieldToStatusMap;
            this.uploadDest = uploadDest;
            this.drService = drService;
            this.dlg = dlg;
        }

        @Override
        public void markDuplicates(Collection<String> duplicates) {
            for(Entry<String, Field<String>> entry : destResourceMap.entrySet()){
                Field<String> urlField = entry.getValue();
                Status formStatus = fieldToStatusMap.get(urlField);

                if(duplicates.contains(entry.getKey())){
                    urlField.markInvalid(I18N.ERROR.fileExist());
                    formStatus.clearStatus("");
                }else{
                    Entry<Field<String>, Status> e = getEntry(formStatus);
                    dlg.addPending(e);
                    drService.importFromUrl(urlField.getValue(), uploadDest, new ImportFromUrlCallback<D>(dlg, e));
                }
            }
        }

        private Entry<Field<String>, Status> getEntry(Status status){
            for(Entry<Field<String>, Status> e : fieldToStatusMap.entrySet()){
                if(e.getValue() == status){
                    return e;
                }
            }
            return null;
        }
    }

    private final class ImportFromUrlCallback <D extends UIObject & IsHideable & HasPending<Entry<Field<String>, Status>>> implements AsyncCallback<String> {
        private final D dlg;
        private final Entry<Field<String>, Status> pending;

        public ImportFromUrlCallback(D dlg, Entry<Field<String>, Status> pending) {
            this.dlg = dlg;
            this.pending = pending;
        }

        @Override
        public void onSuccess(String result) {
            dlg.removePending(pending);
            pending.getValue().clearStatus("");
            if(!dlg.hasPending()){
                dlg.hide();
            }
        }

        @Override
        public void onFailure(Throwable caught) {
            // TODO JDS Determine how to update the UI
            if (dlg.getNumPending() == 1) {
                // ErrorHandler.post(caught);
                // "Blink" the window on the last pending element.
                dlg.getElement().<FxElement>cast().blink();
            }

            pending.getKey().markInvalid("Url Failed to upload");
            dlg.removePending(pending);
        }
    }

}
