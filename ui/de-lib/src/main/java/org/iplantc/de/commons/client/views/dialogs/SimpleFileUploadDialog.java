package org.iplantc.de.commons.client.views.dialogs;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.widgets.IPCFileUploadField;
import org.iplantc.de.diskResource.client.events.FileUploadedEvent;
import org.iplantc.de.diskResource.client.views.dialogs.DuplicateDiskResourceCallback;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.SubmitEvent;
import com.sencha.gxt.widget.core.client.event.SubmitEvent.SubmitHandler;
import com.sencha.gxt.widget.core.client.form.FormPanel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author jstroot
 */
public class SimpleFileUploadDialog extends AbstractFileUploadDialog {

    private final DiskResourceAutoBeanFactory FS_FACTORY = GWT.create(DiskResourceAutoBeanFactory.class);
    private final SimpleFileUploadPanelUiBinder BINDER = GWT.create(SimpleFileUploadPanelUiBinder.class);
    @UiField(provided = true) final AbstractFileUploadDialogAppearance appearance;

    @UiTemplate("AbstractFileUploadPanel.ui.xml")
    interface SimpleFileUploadPanelUiBinder extends UiBinder<Widget, SimpleFileUploadDialog> {
    }

    private final HasPath uploadDest;
    private final DiskResourceServiceFacade drService;
    private final String userName;
    private final EventBus eventBus;
    private final DiskResourceUtil diskResourceUtil;

    public SimpleFileUploadDialog(final HasPath uploadDest,
                                  final DiskResourceServiceFacade drService,
                                  final EventBus eventBus,
                                  final DiskResourceUtil diskResourceUtil,
                                  final SafeUri fileUploadServlet,
                                  final String userName) {
        super(fileUploadServlet);

        this.uploadDest = uploadDest;
        this.drService = drService;
        this.eventBus = eventBus;
        this.userName = userName;
        this.diskResourceUtil = diskResourceUtil;
        appearance = GWT.create(AbstractFileUploadDialogAppearance.class);

        add(BINDER.createAndBindUi(this));

        afterBinding(); //must be called after UIBinder

        initDestPathLabel();
    }

    void initDestPathLabel() {
        String destPath = uploadDest.getPath();
        final String parentPath = diskResourceUtil.parseNameFromPath(destPath);

        htmlDestText.setHTML(appearance.renderDestinationPathLabel(destPath, parentPath));
    }

    @Override
    HorizontalLayoutContainer createHLC() {
        HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
        hlc.add(new Hidden(HDN_PARENT_ID_KEY, uploadDest.getPath()));
        hlc.add(new Hidden(HDN_USER_ID_KEY, userName));
        return hlc;
    }


    @Override
    protected void onSubmitComplete(List<IPCFileUploadField> fufList,
                                    List<Status> statList,
                                    List<FormPanel> submittedForms,
                                    List<FormPanel> formList,
                                    SubmitCompleteEvent event) {
        if (submittedForms.contains(event.getSource())) {
            submittedForms.remove(event.getSource());
            statList.get(formList.indexOf(event.getSource())).clearStatus("");
        }

        IPCFileUploadField field = fufList.get(formList.indexOf(event.getSource()));
        String results2 = event.getResults();
        String fieldValue = field.getValue();
        if (Strings.isNullOrEmpty(results2)) {
            IplantAnnouncer.getInstance()
                           .schedule(new ErrorAnnouncementConfig(appearance.fileUploadsFailed(Lists.newArrayList(
                                   fieldValue))));
        } else {
            String results = Format.stripTags(results2);
            Splittable split = StringQuoter.split(results);

            if (split == null) {
                IplantAnnouncer.getInstance()
                               .schedule(new ErrorAnnouncementConfig(appearance.fileUploadsFailed(Lists.newArrayList(
                                       fieldValue))));
            } else {
                if (split.isUndefined("file") || (split.get("file") == null)) {
                    field.markInvalid(appearance.fileUploadsFailed(Lists.newArrayList(field.getValue())));
                    IplantAnnouncer.getInstance()
                                   .schedule(new ErrorAnnouncementConfig(appearance.fileUploadsFailed(
                                           Lists.newArrayList(field.getValue()))));
                } else {
                    eventBus.fireEvent(new FileUploadedEvent(uploadDest, field.getValue(), results));
                }
            }
        }

        if (submittedForms.size() == 0) {
            hide();
        }

    }

    @Override
    protected void doUpload(List<IPCFileUploadField> fufList,
                            List<Status> statList,
                            List<FormPanel> submittedForms,
                            List<FormPanel> formList) {
        FastMap<IPCFileUploadField> destResourceMap = new FastMap<>();
        for (IPCFileUploadField field : fufList) {
            String fileName = field.getValue().replaceAll(".*[\\\\/]", "");
            field.setEnabled(!Strings.isNullOrEmpty(fileName) && !fileName.equalsIgnoreCase("null"));
            if (field.isEnabled()) {
                destResourceMap.put(uploadDest.getPath() + "/" + fileName, field);
            } else {
                field.setEnabled(false);
            }
        }

        if (!destResourceMap.isEmpty()) {
            final ArrayList<String> ids = Lists.newArrayList(destResourceMap.keySet());
            final HasPaths dto = FS_FACTORY.pathsList().as();
            dto.setPaths(ids);
            final CheckDuplicatesCallback cb = new CheckDuplicatesCallback(ids, destResourceMap, statList, fufList, submittedForms, formList);
            drService.diskResourcesExist(dto, cb);
        }
    }

    private final class CheckDuplicatesCallback extends DuplicateDiskResourceCallback {
        private final FastMap<IPCFileUploadField> destResourceMap;
        private final List<Status> statList;
        private final List<IPCFileUploadField> fufList;
        private final List<FormPanel> submittedForms;
        private final List<FormPanel> formList;

        public CheckDuplicatesCallback(List<String> ids, FastMap<IPCFileUploadField> destResourceMap,
                                       List<Status> statList, List<IPCFileUploadField> fufList, List<FormPanel> submittedForms,
                                       List<FormPanel> formList) {
            super(ids, null);
            this.destResourceMap = destResourceMap;
            this.statList = statList;
            this.fufList = fufList;
            this.submittedForms = submittedForms;
            this.formList = formList;
        }

        @Override
        public void markDuplicates(Collection<String> duplicates) {
            if ((duplicates != null) && !duplicates.isEmpty()) {
                for (String id : duplicates) {
                    destResourceMap.get(id).markInvalid(appearance.fileExist());
                }
            } else {
                for (final IPCFileUploadField field : destResourceMap.values()) {
                    int index = fufList.indexOf(field);
                    statList.get(index).setBusy("");
                    FormPanel form = formList.get(index);
                    form.addSubmitHandler(new SubmitHandler() {

                        @Override
                        public void onSubmit(SubmitEvent event) {
                            if (event.isCanceled()) {
                                IplantAnnouncer.getInstance()
                                               .schedule(new ErrorAnnouncementConfig(appearance.fileUploadsFailed(
                                                       Lists.newArrayList(field.getValue()))));
                            }

                            getOkButton().disable();
                        }
                    });
                    try {
                        form.submit();
                    } catch(Exception e ) {
                        GWT.log("\nexception on submit\n" + e.getMessage());
                        IplantAnnouncer.getInstance()
                                       .schedule(new ErrorAnnouncementConfig(appearance.fileUploadsFailed(
                                               Lists.newArrayList(field.getValue()))));
                    }
                    submittedForms.add(form);
                }
            }
        }
    }
}
