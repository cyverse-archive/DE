package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.widgets.IPCFileUploadField;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.client.ToolbarView.Presenter;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceSelectorFieldFactory;
import org.iplantc.de.diskResource.client.views.dialogs.GenomeSearchDialog.Appearance;
import org.iplantc.de.diskResource.client.views.widgets.FileSelectorField;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.FormPanel.Encoding;
import com.sencha.gxt.widget.core.client.form.FormPanel.Method;

import java.util.List;

public class BulkMetadataDialog extends Dialog {

    private final class CancelButtonHandler implements SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            hide();

        }
    }

    private final class OkButtonHandler implements SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            if (isValid()) {
                if (templateCombo.getValue() != null) {
                    presenter.submitBulkMetadataFromExistingFile(fileSelector.getValue().getPath(),

                    templateCombo.getValue().getId(), BulkMetadataDialog.this.destFolder, false);
                } else {
                    presenter.submitBulkMetadataFromExistingFile(fileSelector.getValue().getPath(),
                                                                 null,
                                                                 BulkMetadataDialog.this.destFolder,
                                                                 false);
                }
                hide();
            }
        }
    }

    public interface Appearance {
        String heading();

        String selectMetadataFile();

        String selectTemplate();

        String applyBulkMetadata();

        String uploadMetadata();
    }

    private final class TemplateInfoLabelProvider implements LabelProvider<MetadataTemplateInfo> {
        @Override
        public String getLabel(MetadataTemplateInfo item) {
            return item.getName();
        }
    }

    private final class TemplateInfoModelKeyProvider implements ModelKeyProvider<MetadataTemplateInfo> {
        @Override
        public String getKey(MetadataTemplateInfo item) {
            return item.getId();
        }
    }

    @UiTemplate("BulkMetadataPanel.ui.xml")
    interface BulkMetadataUiBinder extends UiBinder<Widget, BulkMetadataDialog> {
    }

    public enum BULK_MODE {
        UPLOAD, SELECT;
    }

    private static final BulkMetadataUiBinder BINDER = GWT.create(BulkMetadataUiBinder.class);

    private static final String FORM_WIDTH = "475";
    private static final String FORM_HEIGHT = "28";

    @UiField
    FormPanel form0;
    @UiField
    IPCFileUploadField fuf0;
    @UiField
    TextButton btn0;
    @UiField
    Status status0;

    @UiField(provided = true)
    FileSelectorField fileSelector;

    @UiField
    ComboBox<MetadataTemplateInfo> templateCombo;

    @UiField
    HTML upFileLbl, selLbl;

    ToolbarView.Presenter presenter;

    private final ListStore<MetadataTemplateInfo> templateStore;

    private final BULK_MODE mode;

    private final String destFolder;

    private final Appearance apperance;

    @Inject
    public BulkMetadataDialog(final DiskResourceSelectorFieldFactory drSelectorFieldFactory,
                              @Assisted("destFolder") String destPath,
                              @Assisted("templates") List<MetadataTemplateInfo> templates,
                              @Assisted("mode") BULK_MODE mode) {

        apperance = GWT.<Appearance> create(Appearance.class);
        setHeadingText(apperance.heading());
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        setModal(true);
        this.mode = mode;
        this.fileSelector = drSelectorFieldFactory.defaultFileSelector();
        fileSelector.setValidatePermissions(true);
        fileSelector.addValidator(new DiskResourceNameValidator());
        this.destFolder = destPath;
        templateStore = new ListStore<>(new TemplateInfoModelKeyProvider());
        add(BINDER.createAndBindUi(this));
        selLbl.setHTML(buildRequiredFieldLabel(selLbl.getText()));
        if (mode.equals(BULK_MODE.SELECT)) {
            selectFileOption();
        } else {
            uploadFileOption();
        }
        templateStore.clear();
        templateStore.addAll(templates);
        getButton(PredefinedButton.OK).addSelectHandler(new OkButtonHandler());

        getButton(PredefinedButton.CANCEL).addSelectHandler(new CancelButtonHandler());

    }

    private static String buildRequiredFieldLabel(final String label) {
        if (label == null) {
            return null;
        }

        return "<span style='color:red; top:-5px;' >*</span> " + label; //$NON-NLS-1$
    }

    private boolean isValid() {
        if (mode.equals(BULK_MODE.SELECT)) {
            if (fileSelector.getValue() == null) {
                return false;
            } else {
                return true;
            }
        } else {
            return fuf0.validate(false);
        }
    }

    private void uploadFileOption() {
        fileSelector.hide();
        selLbl.setVisible(false);
        form0.show();
        upFileLbl.setVisible(true);
    }

    private void selectFileOption() {
        fileSelector.show();
        selLbl.setVisible(true);
        form0.hide();
        upFileLbl.setVisible(false);
    }

    @UiFactory
    ComboBox<MetadataTemplateInfo> buildTemplateCombo() {

        final AbstractSafeHtmlRenderer<MetadataTemplateInfo> htmlRenderer = new AbstractSafeHtmlRenderer<MetadataTemplateInfo>() {
            @Override
            public SafeHtml render(MetadataTemplateInfo object) {
                SafeHtmlBuilder shb = new SafeHtmlBuilder();
                shb.appendEscaped(object.getName());
                return shb.toSafeHtml();
            }
        };
        ComboBox<MetadataTemplateInfo> comboBox = new ComboBox<>(templateStore,
                                                                 new TemplateInfoLabelProvider(),
                                                                 htmlRenderer);
        comboBox.setEditable(false);
        comboBox.setWidth(250);
        comboBox.setEmptyText(apperance.selectTemplate());
        comboBox.setTypeAhead(true);
        return comboBox;
    }

    @UiFactory
    FormPanel createFormPanel() {
        FormPanel form = new FormPanel();
        // form.setAction(fileUploadServlet);
        form.setMethod(Method.POST);
        form.setEncoding(Encoding.MULTIPART);
        form.setSize(FORM_WIDTH, FORM_HEIGHT);
        return form;
    }

    @UiFactory
    HorizontalLayoutContainer createHLC() {
        HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
        // hlc.add(new Hidden(HDN_PARENT_ID_KEY, uploadDest.getPath()));
        // hlc.add(new Hidden(HDN_USER_ID_KEY, userName));
        return hlc;
    }

    @UiFactory
    Status createFormStatus() {
        Status status = new Status();
        status.setWidth(15);
        return status;
    }

    @UiHandler("btn0")
    void onResetClicked(SelectEvent event) {
        fuf0.reset();
        fuf0.validate(true);
    }

    @UiHandler("fuf0")
    void onFieldChanged(ChangeEvent event) {

    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

}
