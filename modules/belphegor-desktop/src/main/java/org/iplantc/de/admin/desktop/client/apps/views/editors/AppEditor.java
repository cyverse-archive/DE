package org.iplantc.de.admin.desktop.client.apps.views.editors;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.commons.client.util.RegExp;
import org.iplantc.de.commons.client.validators.BasicEmailValidator3;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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
        void onAppEditorSave(App app);
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
    @UiField(provided = true) AppEditorAppearance appearance = GWT.create(AppEditorAppearance.class);

    private final Widget widget;

    public AppEditor(final App app,
                     final Presenter presenter) {
        widget = uiBinder.createAndBindUi(this);
        this.presenter = presenter;

        // Add validators
        final RegExValidator regExValidator = new RegExValidator(Format.substitute("[^{0}{1}][^{1}]*",
                                                                              appearance.appNameRestrictedStartingChars(),
                                                                              RegExp.escapeCharacterClassSet(appearance.appNameRestrictedChars())),
                                                            appearance.invalidAppNameMsg(appearance.appNameRestrictedStartingChars(),
                                                                                         appearance.appNameRestrictedChars()));
        name.addValidator(regExValidator);
        integratorEmail.addValidator(new BasicEmailValidator3());

        wikiUrlFieldLabel.setHTML(appearance.wikiUrlFieldLabel());

        window.setHeadingText(app.getName());

        driver.initialize(this);
        driver.edit(app);
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
        if (!driver.hasErrors()) {
            window.hide();
            presenter.onAppEditorSave(app);
        }
    }

    @UiHandler("cancelButton")
    void onCancelClick(SelectEvent event) {
        window.hide();
    }


}
