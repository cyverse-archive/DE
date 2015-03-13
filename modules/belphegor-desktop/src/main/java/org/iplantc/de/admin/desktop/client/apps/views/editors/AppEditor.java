package org.iplantc.de.admin.desktop.client.apps.views.editors;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppDoc;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.util.RegExp;
import org.iplantc.de.commons.client.validators.BasicEmailValidator3;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;

/**
 * @author jstroot
 */
public class AppEditor implements Editor<App>, IsWidget {

    public interface AppEditorAppearance {

        String appEditorWidth();

        String appName();

        String appNameRestrictedChars();

        String appNameRestrictedStartingChars();

        String integratorName();

        String integratorEmail();

        String invalidAppNameMsg(String s, String s1);

        String tempDisable();

        String appDisabled();

        String appDescription();

        SafeHtml wikiUrlFieldLabel();
    }

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {
        void onAppEditorSave(App app, AppDoc doc);
    }

    private static BINDER uiBinder = GWT.create(BINDER.class);

    interface BINDER extends UiBinder<Widget, AppEditor> {
    }

    interface Driver extends SimpleBeanEditorDriver<App, AppEditor> {
    }

    Driver driver = GWT.create(Driver.class);

    private final Presenter presenter;

    @UiField @Ignore Window window;
    @UiField TextField name;
    @UiField FieldLabel appNameFieldLabel;
    @UiField TextField integratorName;
    @UiField FieldLabel integratorNameFieldLabel;
    @UiField TextField integratorEmail;
    @UiField FieldLabel integratorEmailFieldLabel;
    @UiField CheckBox disabled;
    @UiField FieldLabel appDisabledCheckBoxLabel;
    @UiField TextArea description;
    @UiField FieldLabel appDescFieldLabel;
    @UiField TextField wikiUrl;
    @UiField FieldLabel wikiUrlFieldLabel;
    @UiField @Ignore TextButton saveButton;
    @UiField @Ignore TextButton cancelButton;
    @UiField
    FieldLabel appDocLbl;
    @UiField
    @Ignore
    TextArea appDoc;
    @UiField
    @Ignore
    HTML docHelp;
    @UiField(provided = true)
    @Ignore
    IPlantAnchor templateLink;
    @UiField(provided = true) AppEditorAppearance appearance = GWT.create(AppEditorAppearance.class);

    private final Widget widget;

    private final AppDoc doc;

    public AppEditor(final App app, final AppDoc doc,
                     final Presenter presenter) {
        templateLink = new IPlantAnchor("View Documentaion Template");
        widget = uiBinder.createAndBindUi(this);
        this.presenter = presenter;
        this.doc = doc;
        initTemplateLink();

        // Add validators
        final RegExValidator regExValidator = new RegExValidator(Format.substitute("[^{0}{1}][^{1}]*",
                                                                              appearance.appNameRestrictedStartingChars(),
                                                                              RegExp.escapeCharacterClassSet(appearance.appNameRestrictedChars())),
                                                            appearance.invalidAppNameMsg(appearance.appNameRestrictedStartingChars(),
                                                                                         appearance.appNameRestrictedChars()));
        name.addValidator(regExValidator);
        integratorEmail.addValidator(new BasicEmailValidator3());
        wikiUrlFieldLabel.setHTML(appearance.wikiUrlFieldLabel());
        appDocLbl.setHTML("DE App Documentation");
        window.setHeadingText(app.getName());
        docHelp.setHTML("<p><i>Note:</i> Please complete the following section for documentation to be displayed with in DE itself. The documentation must be in Markdown format. Please clear wiki URL field once you fill this field. Replace everything inside '{{}}'.<br/>");
        if (this.doc != null) {
            appDoc.setValue(this.doc.getDocumentaion());
        }
        driver.initialize(this);
        driver.edit(app);
    }

    private void initTemplateLink() {
        templateLink.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                IPlantDialog popup = new IPlantDialog();
                popup.setHeadingText("Copy Markdown template");
                TextArea area = new TextArea();
                area.setValue("### {{appName}} \n> #### Description and Quick Start \n>> {{quickStart}} \n> #### Test Data \n>> {{testData}} \n> #### Input File(s) \n>> {{Input Files Description & types}} \n> #### Parameters Used in App \n>> {{params used in app}} \n> #### Output File(s) \n>> {{Output Files description & types}}");
                area.setSize("350px", "250px");
                popup.add(area);
                popup.show();
            }
        });
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    public void show() {
        window.show();
    }

    @UiHandler("saveButton")
    void onSaveClick(SelectEvent event) {
        App app = driver.flush();
        if (!driver.hasErrors() && validDoc()) {
            window.hide();
            doc.setDocumentation(appDoc.getCurrentValue());
            presenter.onAppEditorSave(app, doc);
        }
    }

    private boolean validDoc() {
        if (Strings.isNullOrEmpty(appDoc.getValue()) && Strings.isNullOrEmpty(wikiUrl.getValue())) {
            ErrorHandler.post("You must enter a either a valid URL for App documentation or supply the documentation in Markdown using the templates.");
            return false;
        } else {
            return true;
        }
    }

    @UiHandler("cancelButton")
    void onCancelClick(SelectEvent event) {
        window.hide();
    }


}
