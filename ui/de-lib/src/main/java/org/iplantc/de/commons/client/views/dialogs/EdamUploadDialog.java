package org.iplantc.de.commons.client.views.dialogs;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.widgets.IPCFileUploadField;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.SubmitEvent;
import com.sencha.gxt.widget.core.client.event.SubmitEvent.SubmitHandler;
import com.sencha.gxt.widget.core.client.form.FormPanel;

import java.util.List;

/**
 * @author jstroot
 */
public class EdamUploadDialog extends AbstractFileUploadDialog {

    @UiTemplate("AbstractFileUploadPanel.ui.xml")
    interface EdamUploadDialogUiBinder extends UiBinder<Widget, EdamUploadDialog> {
    }
    private final EdamUploadDialogUiBinder BINDER = GWT.create(EdamUploadDialogUiBinder.class);

    @UiField(provided = true) AbstractFileUploadDialogAppearance appearance;

    @Inject
    public EdamUploadDialog(final SafeUri ontologyUploadServlet) {
        super(ontologyUploadServlet);
        this.appearance = GWT.create(AbstractFileUploadDialogAppearance.class);

        add(BINDER.createAndBindUi(this));

        afterBinding(); //must be called after UIBinder
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
        if (Strings.isNullOrEmpty(results2)) {
            IplantAnnouncer.getInstance()
                           .schedule(new SuccessAnnouncementConfig(appearance.fileUploadsSuccess(Lists.newArrayList(field.getValue()))));
            hide();
        } else {
            IplantAnnouncer.getInstance()
                           .schedule(new ErrorAnnouncementConfig(appearance.fileUploadsFailed(Lists.newArrayList(field.getValue()))));
        }

    }

    @Override
    protected void doUpload(List<IPCFileUploadField> fufList,
                            List<Status> statList,
                            List<FormPanel> submittedForms,
                            List<FormPanel> formList) {

        for (final IPCFileUploadField field : fufList) {
            String fileName = field.getValue().replaceAll(".*[\\\\/]", "");
            field.setEnabled(!Strings.isNullOrEmpty(fileName) && !fileName.equalsIgnoreCase("null"));
            if (field.isEnabled()) {
                int index = fufList.indexOf(field);
                statList.get(index).setBusy("");
                FormPanel form = formList.get(index);
                form.addSubmitHandler(new SubmitHandler() {

                    @Override
                    public void onSubmit(SubmitEvent event) {
                        if (event.isCanceled()) {
                            IplantAnnouncer.getInstance()
                                           .schedule(new ErrorAnnouncementConfig(appearance.fileUploadsFailed(Lists.newArrayList(field.getValue()))));
                        }

                        getOkButton().disable();
                    }
                });
                try {
                    form.submit();
                } catch (Exception e) {
                    GWT.log("\nexception on submit\n" + e.getMessage());
                    IplantAnnouncer.getInstance()
                                   .schedule(new ErrorAnnouncementConfig(appearance.fileUploadsFailed(
                                           Lists.newArrayList(field.getValue()))));
                }
            } else {
                field.setEnabled(false);
            }
        }
    }
}
