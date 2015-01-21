package org.iplantc.de.tools.requests.client.views;

import org.iplantc.de.client.models.toolRequests.Architecture;
import org.iplantc.de.client.models.toolRequests.YesNoMaybe;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.validators.LengthRangeValidator;
import org.iplantc.de.commons.client.validators.UrlValidator;
import org.iplantc.de.commons.client.views.dialogs.IplantInfoBox;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceSelectorFieldFactory;
import org.iplantc.de.diskResource.client.views.widgets.FileSelectorField;
import org.iplantc.de.resources.client.constants.IplantValidationConstants;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanelHelper;
import com.sencha.gxt.widget.core.client.form.IsField;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 *
 * A form to submit request to install new tools in condor
 *
 * @author sriram
 *
 */
public final class NewToolRequestFormViewImpl extends Composite implements NewToolRequestFormView {

    @UiTemplate("NewToolRequestFormView.ui.xml")
    interface NewToolRequestFormViewUiBinder extends UiBinder<Widget, NewToolRequestFormViewImpl> {
    }

    private static final NewToolRequestFormViewUiBinder uiBinder = GWT.create(NewToolRequestFormViewUiBinder.class);
    private final IplantValidationConstants validationConstants;

    private static String buildRequiredFieldLabel(final String label) {
        if (label == null) {
            return null;
        }

        return "<span style='color:red; top:-5px;' >*</span> " + label; //$NON-NLS-1$
    }

    @UiField VerticalLayoutContainer container;
    @UiField FieldLabel toolNameLbl;
    @UiField FieldLabel toolDescLbl;
    @UiField FieldLabel srcLbl;
    @UiField Radio toolLink;
    @UiField Radio toolUpld;
    @UiField Radio toolSlt;
    @UiField Radio testUpld;
    @UiField Radio testSlt;
    @UiField Radio otherUpld;
    @UiField Radio otherSlt;
    @UiField FieldLabel docUrlLbl;
    @UiField FieldLabel versionLbl;
    @UiField FieldLabel archLbl;
    @UiField FieldLabel multiLbl;
    @UiField FieldLabel cmdLineLbl;
    @UiField TextField toolName;
    @UiField TextArea toolDesc;
    @UiField TextArea toolAttrib;
    @UiField TextField binLink;
    @UiField TextField toolDoc;
    @UiField TextField toolVersion;
    @UiField TextArea runInfo;
    @UiField TextArea otherInfo;
    @UiField(provided = true) final ComboBox<Architecture> archCbo;
    @UiField(provided = true) final ComboBox<YesNoMaybe> multiThreadCbo;
    @UiField UploadForm binUpld;
    @UiField UploadForm testDataUpld;
    @UiField UploadForm otherDataUpld;
    @UiField(provided = true) FileSelectorField binSelect;
    @UiField(provided = true) FileSelectorField testDataSelect;
    @UiField(provided = true) FileSelectorField otherDataSelect;
    @UiField FieldLabel testLbl;
    @UiField FieldLabel otherLbl;
    @UiField CardLayoutContainer binOptions;
    @UiField CardLayoutContainer testDataOptions;
    @UiField CardLayoutContainer otherDataOptions;
    @UiField HtmlLayoutContainer intro;
    
    private final AutoProgressMessageBox submissionProgressBox;

    private Presenter presenter;

    @Inject
    NewToolRequestFormViewImpl(final DiskResourceSelectorFieldFactory fileSelectorFieldFactory,
                               final IplantValidationConstants validationConstants,
                               @Assisted final ComboBox<Architecture> archChooser,
                               @Assisted final ComboBox<YesNoMaybe> multithreadChooser) {
        this.validationConstants = validationConstants;
        this.binSelect = fileSelectorFieldFactory.defaultFileSelector();
        this.testDataSelect = fileSelectorFieldFactory.defaultFileSelector();
        this.otherDataSelect = fileSelectorFieldFactory.defaultFileSelector();
        archCbo = archChooser;
        multiThreadCbo = multithreadChooser;
        initWidget(uiBinder.createAndBindUi(this));
        submissionProgressBox = new AutoProgressMessageBox(I18N.DISPLAY.submitRequest());
        submissionProgressBox.auto();
        container.setScrollMode(ScrollMode.AUTOY);
        container.setAdjustForScroll(true);
        initValidators();
        initRequiredLabels();

        ToggleGroup grp1 = new ToggleGroup();
        grp1.add(toolLink);
        grp1.add(toolSlt);
        grp1.add(toolUpld);

        ToggleGroup grp2 = new ToggleGroup();
        grp2.add(testSlt);
        grp2.add(testUpld);


        ToggleGroup grp3 = new ToggleGroup();
        grp3.add(otherSlt);
        grp3.add(otherUpld);

    }
    
    @UiFactory
    HtmlLayoutContainer buildIntroContainer() {
        return new HtmlLayoutContainer(I18N.DISPLAY.toolRequestFormIntro());
    }

    private void initRequiredLabels() {
        toolNameLbl.setHTML(buildRequiredFieldLabel(I18N.DISPLAY.toolNameLabel()));
        toolDescLbl.setHTML(buildRequiredFieldLabel(I18N.DISPLAY.toolDesc()));
        srcLbl.setHTML(buildRequiredFieldLabel(I18N.DISPLAY.srcLinkPrompt()));
        docUrlLbl.setHTML(buildRequiredFieldLabel(I18N.DISPLAY.docLink()));
        versionLbl.setHTML(buildRequiredFieldLabel(I18N.DISPLAY.version()));
        archLbl.setHTML(buildRequiredFieldLabel(I18N.DISPLAY.architecture()));
        multiLbl.setHTML(buildRequiredFieldLabel(I18N.DISPLAY.isMultiThreaded()));
        cmdLineLbl.setHTML(buildRequiredFieldLabel(I18N.DISPLAY.cmdLineRun()));
        testLbl.setHTML(buildRequiredFieldLabel((I18N.DISPLAY.upldTestData())));
    }

    private void initValidators() {
        toolName.addValidator(new LengthRangeValidator(I18N.DISPLAY.toolName(), 1, validationConstants.maxToolNameLength()));
        toolName.addValidator(new DiskResourceNameValidator());
        binLink.addValidator(new UrlValidator());
        toolDoc.addValidator(new UrlValidator());
        binUpld.addValidator(new DiskResourceNameValidator());
        testDataUpld.addValidator(new DiskResourceNameValidator());
        otherDataUpld.addValidator(new DiskResourceNameValidator());
        otherDataUpld.setAllowBlank(true);
        otherDataSelect.setRequired(false);
        binSelect.setRequired(false);
        binLink.setAllowBlank(true);
        binUpld.setAllowBlank(false);   
        testDataUpld.setAllowBlank(false);
        testDataSelect.setRequired(false);
    }

    @UiHandler("toolLink")
    void onBinLinkSelect(final ValueChangeEvent<Boolean> unused) {
        if (presenter != null) {
            presenter.onToolSelectionModeChange();
        }
    }

    @UiHandler("toolUpld")
    void onBinUploadSelect(final ValueChangeEvent<Boolean> unused) {
        if (presenter != null) {
            presenter.onToolSelectionModeChange();
        }
    }

    @UiHandler("toolSlt")
    void onBinSelect(final ValueChangeEvent<Boolean> unused) {
        if (presenter != null) {
            presenter.onToolSelectionModeChange();
        }
    }

    @UiHandler("testSlt")
    void onTestDataSelect(final ValueChangeEvent<Boolean> unused) {
        if(presenter != null) {
            presenter.onTestDataSelectionModeChange();
        }

    }

    @UiHandler("testUpld")
    void onTestDataUpload(final ValueChangeEvent<Boolean> unused) {
        if(presenter != null) {
            presenter.onTestDataSelectionModeChange();
        }

    }

    @UiHandler("otherUpld")
    void onOtherDataUpload(final ValueChangeEvent<Boolean> unused) {
        if(presenter != null) {
            presenter.onOtherDataSeelctionModeChange();
        }

    }

    @UiHandler("otherSlt")
    void onOtherDataSelect(final ValueChangeEvent<Boolean> unused) {
        if(presenter != null) {
            presenter.onOtherDataSeelctionModeChange();
        }

    }


    @Override
    public Uploader getOtherDataUploader() {
        return otherDataUpld;
    }

    @Override
    public Uploader getTestDataUploader() {
        return testDataUpld;
    }

    @Override
    public Uploader getToolBinaryUploader() {
        return binUpld;
    }

    @Override
    public final void indicateSubmissionStart() {
        submissionProgressBox.setProgressText(I18N.DISPLAY.submitting());
        submissionProgressBox.getProgressBar().reset();
        submissionProgressBox.show();
    }

    @Override
    public final void indicateSubmissionFailure(final String reason) {
        submissionProgressBox.hide();
        final AlertMessageBox amb = new AlertMessageBox(I18N.DISPLAY.alert(), reason);
        amb.show();
    }

    @Override
    public final void indicateSubmissionSuccess() {
        submissionProgressBox.hide();
        final IplantInfoBox successMsg = new IplantInfoBox(I18N.DISPLAY.success(), I18N.DISPLAY.requestConfirmMsg());
        successMsg.show();
    }

    @Override
    public boolean isValid() {
        return FormPanelHelper.isValid(container, false);
    }

    @Override
    public void setPresenter(final Presenter p) {
        this.presenter = p;
    }

    @Override
    public void setToolSelectionMode() {
        if(toolLink.getValue()) {
            binOptions.setActiveWidget(binOptions.getWidget(1));
            presenter.setToolMode(SELECTION_MODE.LINK);
            binUpld.setAllowBlank(true);
            binSelect.setRequired(false);
            binLink.setAllowBlank(false);
        } else if(toolUpld.getValue()) {
            binOptions.setActiveWidget(binOptions.getWidget(0));
            presenter.setToolMode(SELECTION_MODE.UPLOAD);
            binUpld.setAllowBlank(false);
            binSelect.setRequired(false);
            binLink.setAllowBlank(true);
        } else if (toolSlt.getValue()) {
            binOptions.setActiveWidget(binOptions.getWidget(2));
            presenter.setToolMode(SELECTION_MODE.SELECT);
            binUpld.setAllowBlank(true);
            binSelect.setRequired(true);
            binLink.setAllowBlank(true);
        }
    }

    @Override
    public IsField<String> getNameField() {
        return toolName;
    }

    @Override
    public IsField<String> getDescriptionField() {
        return toolDesc;
    }

    @Override
    public IsField<String> getAttributionField() {
        return toolAttrib;
    }

    @Override
    public IsField<String> getSourceURLField() {
        return binLink;
    }

    @Override
    public IsField<String> getDocURLField() {
        return toolDoc;
    }

    @Override
    public IsField<String> getVersionField() {
        return toolVersion;
    }

    @Override
    public IsField<YesNoMaybe> getMultithreadedField() {
        return multiThreadCbo;
    }

    @Override
    public IsField<String> getInstructionsField() {
        return runInfo;
    }

    @Override
    public IsField<String> getAdditionalInfoField() {
        return otherInfo;
    }

    @Override
    public IsField<Architecture> getArchitectureField() {
        return archCbo;
    }

    @Override
    public FileSelectorField getBinSelectField() {
        return binSelect;
    }

    @Override
    public FileSelectorField getTestDataSelectField() {
        return testDataSelect;
    }

    @Override
    public FileSelectorField getOtherDataSelectField() {
        return otherDataSelect;
    }

    @Override
    public void setTestDataSelectMode() {
        if(testUpld.getValue()) {
            testDataOptions.setActiveWidget(testDataOptions.getWidget(0));
            presenter.setTestDataMode(SELECTION_MODE.UPLOAD);
            testDataUpld.setAllowBlank(false);
            testDataSelect.setRequired(false);
        } else if (testSlt.getValue()) {
            testDataOptions.setActiveWidget(testDataOptions.getWidget(1));
            presenter.setTestDataMode(SELECTION_MODE.SELECT);
            testDataUpld.setAllowBlank(true);
            testDataSelect.setRequired(true);
        }

    }

    @Override
    public void setOtherDataSelectMode() {
       if(otherUpld.getValue()) {
           otherDataOptions.setActiveWidget(otherDataOptions.getWidget(0));
           presenter.setOtherDataMode(SELECTION_MODE.UPLOAD);
       } else if (otherSlt.getValue()) {
           otherDataOptions.setActiveWidget(otherDataOptions.getWidget(1));
           presenter.setOtherDataMode(SELECTION_MODE.SELECT);
       }
    }

}
