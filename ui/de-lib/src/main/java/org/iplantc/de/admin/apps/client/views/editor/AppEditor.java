package org.iplantc.de.admin.apps.client.views.editor;

import org.iplantc.de.admin.apps.client.events.selection.SaveAppSelected;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.apps.widgets.client.view.editors.widgets.CheckBoxAdapter;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppDoc;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.util.RegExp;
import org.iplantc.de.commons.client.validators.BasicEmailValidator3;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
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
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;

/**
 * @author jstroot
 */
public class AppEditor extends Window implements Editor<App>, IsWidget, SaveAppSelected.HasSaveAppSelectedHandlers {

    public interface AppEditorAppearance {

        String appDocumentationLabel();

        String appEditorWidth();

        String appName();

        String appNameRestrictedChars();

        String appNameRestrictedStartingChars();

        String docHelpHtml();

        String integratorName();

        String integratorEmail();

        String invalidAppNameMsg(String s, String s1);

        String tempDisable();

        String appDisabled();

        String appDescription();

        String templateLinkPopupHeading();

        String templateLinkTitle();

        String validDocError();

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

    @UiField
    @Ignore
    Window window;
    @UiField
    TextField name;
    @UiField
    FieldLabel appNameFieldLabel;
    @UiField
    TextField integratorName;
    @UiField
    FieldLabel integratorNameFieldLabel;
    @UiField
    TextField integratorEmail;
    @UiField
    FieldLabel integratorEmailFieldLabel;
    @UiField CheckBoxAdapter disabled;
    @UiField
    FieldLabel appDisabledCheckBoxLabel;
    @UiField
    TextArea description;
    @UiField
    FieldLabel appDescFieldLabel;
    @UiField
    TextField wikiUrl;
    @UiField
    FieldLabel wikiUrlFieldLabel;
    @UiField
    @Ignore
    TextButton saveButton;
    @UiField
    @Ignore
    TextButton cancelButton;
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
    @UiField(provided = true)
    AppEditorAppearance appearance = GWT.create(AppEditorAppearance.class);

    private final Widget widget;

    private final AppDoc doc;

    public AppEditor(final App app, final AppDoc doc) {
        templateLink = new IPlantAnchor(appearance.templateLinkTitle());
        widget = uiBinder.createAndBindUi(this);
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
        appDocLbl.setHTML(appearance.appDocumentationLabel());
        window.setHeadingText(app.getName());
        docHelp.setHTML(appearance.docHelpHtml());
        disabled.setText(appearance.appDisabled());
        if (this.doc != null) {
            appDoc.setValue(this.doc.getDocumentation());
        }
        driver.initialize(this);
        driver.edit(app);
    }

    @Override
    public HandlerRegistration addSaveAppSelectedHandler(SaveAppSelected.SaveAppSelectedHandler handler) {
        return asWidget().addHandler(handler, SaveAppSelected.TYPE);
    }

    private void initTemplateLink() {
        templateLink.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open(GWT.getHostPageBaseURL() + "app-doc-template.txt",
                                                       "_blank",
                                                       null);

            }
        });
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    public void show() {
        window.show();
        ensureDebugId(Belphegor.AppIds.APP_EDITOR);
    }

    @UiHandler("saveButton")
    void onSaveClick(SelectEvent event) {
        App app = driver.flush();
        if (!driver.hasErrors() && validDoc()) {
            window.hide();
            doc.setDocumentation(appDoc.getCurrentValue());
            asWidget().fireEvent(new SaveAppSelected(app, doc));
        }
    }

    private boolean validDoc() {
        if (Strings.isNullOrEmpty(appDoc.getValue()) && Strings.isNullOrEmpty(wikiUrl.getValue())) {
            ErrorHandler.post("You must enter a either a valid URL for App documentation or supply the documentation in Markdown using the templates.");
            ErrorHandler.post(appearance.validDocError());
            return false;
        } else {
            return true;
        }
    }

    @UiHandler("cancelButton")
    void onCancelClick(SelectEvent event) {
        window.hide();
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        window.ensureDebugId(baseID + Belphegor.AppIds.EDITOR_WINDOW);
        window.getHeader().getTool(0).ensureDebugId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.CLOSE_BTN);

        name.setId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.NAME);
        appNameFieldLabel.ensureDebugId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.NAME_LABEL);
        integratorName.setId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.INTEGRATOR_NAME);
        integratorNameFieldLabel.ensureDebugId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.INTEGRATOR_NAME_LABEL);
        integratorEmail.setId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.INTEGRATOR_EMAIL);
        integratorEmailFieldLabel.ensureDebugId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.INTEGRATOR_EMAIL_LABEL);
        disabled.getCheckBox().ensureDebugId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.DISABLED);
        appDisabledCheckBoxLabel.ensureDebugId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.DISABLED_LABEL);
        description.setId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.DESCRIPTION);
        appDescFieldLabel.ensureDebugId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.DESCRIPTION_LABEL);
        wikiUrl.setId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.WIKI_URL);
        wikiUrlFieldLabel.ensureDebugId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.WIKI_URL_LABEL);
        saveButton.ensureDebugId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.SAVE);
        cancelButton.ensureDebugId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.CANCEL);
        appDoc.setId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.APP_DOC);
        appDocLbl.ensureDebugId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.APP_DOC_LABEL);
        docHelp.ensureDebugId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.DOC_HELP);
        templateLink.ensureDebugId(baseID + Belphegor.AppIds.EDITOR_WINDOW + Belphegor.AppIds.TEMPLATE_LINK);
    }

}
