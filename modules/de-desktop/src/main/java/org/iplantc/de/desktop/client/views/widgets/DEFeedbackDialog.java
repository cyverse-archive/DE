package org.iplantc.de.desktop.client.views.widgets;

import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author jstroot
 */
public class DEFeedbackDialog extends IPlantDialog {

    private static DEFeedbackViewUiBinder uiBinder = GWT.create(DEFeedbackViewUiBinder.class);

    @UiTemplate("DEFeedbackView.ui.xml")
    interface DEFeedbackViewUiBinder extends UiBinder<Widget, DEFeedbackDialog> { }

    public interface FeedbackAppearance {
        interface FeedbackStrings {
            String headingText();

            SafeHtml reason();

            SafeHtml complete();

            SafeHtml satisfy();

            String justExploring();

            String manageData();

            String runAnalysis();

            String checkStatus();

            String createApp();

            String other();

            String submit();

            String yes();

            String somewhat();

            String noSpecificTask();

            String notAtAll();

            String verySatisfied();

            String somewhatSatisfied();

            String okay();

            String somewhatDissatisfied();

            String notSatisfied();

            String featuresAndImprovements();

            String anythingElse();

        }

        String dialogHeight();

        String dialogWidth();

        FeedbackStrings displayStrings();
    }

    @UiField Radio vastField;
    @UiField Radio swsatField;
    @UiField Radio okField;
    @UiField Radio swdField;
    @UiField Radio nsField;

    @UiField CheckBox expField;
    @UiField CheckBox mngField;
    @UiField CheckBox runField;
    @UiField CheckBox chkField;
    @UiField CheckBox appField;
    @UiField CheckBox otrField;
    @UiField TextField otherField;

    @UiField CheckBox yesField;
    @UiField CheckBox swField;
    @UiField CheckBox noField;
    @UiField CheckBox notField;
    @UiField CheckBox tskOtrField;
    @UiField TextField otherCompField;
    @UiField TextArea featureTextArea;
    @UiField TextArea otherTextArea;

    @UiField FieldLabel reasonField;

    @UiField FieldLabel completeField;

    @UiField FieldLabel satisfyField;

    @UiField FieldLabel featureField;

    @UiField FieldLabel anythingField;
    private final ToggleGroup group;
    
    @UiField(provided = true) FeedbackAppearance appearance;

    @UiField TextField otherSatisfiedField;

    @UiField Radio otsatField;

    @Inject
    public DEFeedbackDialog(final FeedbackAppearance appearance) {
        this.appearance = appearance;
        setHeadingText(appearance.displayStrings().headingText());
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        getButton(PredefinedButton.OK).setText(appearance.displayStrings().submit());
        setHideOnButtonClick(false);
        setSize(appearance.dialogWidth(), appearance.dialogHeight());

        Widget widget = uiBinder.createAndBindUi(this);
        group = new ToggleGroup();
        group.add(vastField);
        group.add(swsatField);
        group.add(okField);
        group.add(swdField);
        group.add(nsField);
        group.add(otsatField);
        add(widget);
    }


    public boolean validate() {
        boolean validate1 = validateQ1();
        boolean validate2 = validateQ2();
        boolean validate3 = validateQ3();
        if (validate1 && validate2 && validate3) {
            return true;
        } else {
            return false;
        }
    }

    private boolean validateQ1() {
        boolean ret = expField.getValue()
                          || mngField.getValue()
                          || runField.getValue()
                          || chkField.getValue()
                          || appField.getValue();

        if (otrField.getValue()) {
            otherField.setAllowBlank(false);
            return otherField.validate();

        } else {
            otherField.setAllowBlank(true);
            otherField.clearInvalid();
            return ret;
        }
    }

    private boolean validateQ2() {
        boolean ret = yesField.getValue()
                          || swField.getValue()
                          || noField.getValue()
                          || notField.getValue();

        if (tskOtrField.getValue()) {
            otherCompField.setAllowBlank(false);
            return otherCompField.validate();
        } else {
            otherCompField.setAllowBlank(true);
            otherCompField.clearInvalid();
            return ret;
        }

    }

    private boolean validateQ3() {
        boolean ret = vastField.getValue()
                          || swsatField.getValue()
                          || okField.getValue()
                          || swdField.getValue()
                          || nsField.getValue();
        if (otsatField.getValue()) {
            otherSatisfiedField.setAllowBlank(false);
            return otherSatisfiedField.validate();
        } else {
            otherSatisfiedField.clearInvalid();
            return ret;
        }
    }

    public Splittable toJson() {
        Splittable split = StringQuoter.createSplittable();
        getAnswer1().assign(split, appearance.displayStrings().reason().asString());
        getAnswer2().assign(split, appearance.displayStrings().complete().asString());

        if (getAnswer3() != null) {
            getAnswer3().assign(split, appearance.displayStrings().satisfy().asString());
        }
        if (featureTextArea.getValue() != null) {
            StringQuoter.create(featureTextArea.getValue()).assign(split, appearance.displayStrings().featuresAndImprovements());
        }

        if (otherTextArea.getValue() != null) {
            StringQuoter.create(otherTextArea.getValue()).assign(split, appearance.displayStrings().anythingElse());
        }
        return split;
    }

    private Splittable getAnswer1() {
        Splittable indexedSplit = StringQuoter.createIndexed();
        int counter = 0;
        if (expField.getValue()) {
            StringQuoter.create(appearance.displayStrings().justExploring()).assign(indexedSplit, counter++);
        }
        if (mngField.getValue()) {
            StringQuoter.create(appearance.displayStrings().manageData()).assign(indexedSplit, counter++);
        }
        if (runField.getValue()) {
            StringQuoter.create(appearance.displayStrings().runAnalysis()).assign(indexedSplit, counter++);
        }
        if (chkField.getValue()) {
            StringQuoter.create(appearance.displayStrings().checkStatus()).assign(indexedSplit, counter++);
        }
        if (appField.getValue()) {
            StringQuoter.create(appearance.displayStrings().createApp()).assign(indexedSplit, counter++);
        }
        if (otrField.getValue()) {
            StringQuoter.create(otherField.getValue()).assign(indexedSplit, counter++);
        }

        return indexedSplit;
    }

    private Splittable getAnswer2() {
        Splittable indexedSplit = StringQuoter.createIndexed();
        int counter = 0;
        if (yesField.getValue()) {
            StringQuoter.create(appearance.displayStrings().yes()).assign(indexedSplit, counter++);
        }
        if (swField.getValue()) {
            StringQuoter.create(appearance.displayStrings().somewhat()).assign(indexedSplit, counter++);
        }
        if (noField.getValue()) {
            StringQuoter.create(appearance.displayStrings().noSpecificTask()).assign(indexedSplit, counter++);
        }
        if (notField.getValue()) {
            StringQuoter.create(appearance.displayStrings().notAtAll()).assign(indexedSplit, counter++);
        }
        if (tskOtrField.getValue()) {
            StringQuoter.create(otherCompField.getValue()).assign(indexedSplit, counter++);
        }

        return indexedSplit;
    }

    private Splittable getAnswer3() {
        if (vastField.getValue()) {
            return StringQuoter.create(appearance.displayStrings().verySatisfied());
        } else if (swsatField.getValue()) {
            return StringQuoter.create(appearance.displayStrings().somewhatSatisfied());
        } else if (okField.getValue()) {
            return StringQuoter.create(appearance.displayStrings().okay());
        } else if (swdField.getValue()) {
            return StringQuoter.create(appearance.displayStrings().somewhatDissatisfied());
        } else if (nsField.getValue()) {
            return StringQuoter.create(appearance.displayStrings().notSatisfied());
        } else {
            return StringQuoter.create(otherSatisfiedField.getValue());
        }
    }

}
