package org.iplantc.de.client.desktop.views;

import org.iplantc.de.client.models.UserInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

public class DEFeedbackViewImpl implements DEFeedbackView {

    private static DEFeedbackViewUiBinder uiBinder = GWT.create(DEFeedbackViewUiBinder.class);

    @UiTemplate("DEFeedbackView.ui.xml")
    interface DEFeedbackViewUiBinder extends UiBinder<Widget, DEFeedbackViewImpl> {
    }

    final Widget widget;
    @UiField
    VerticalLayoutContainer container;

    @UiField
    Radio vastField;
    @UiField
    Radio swsatField;
    @UiField
    Radio okField;
    @UiField
    Radio swdField;
    @UiField
    Radio nsField;

    @UiField
    CheckBox expField;
    @UiField
    CheckBox mngField;
    @UiField
    CheckBox runField;
    @UiField
    CheckBox chkField;
    @UiField
    CheckBox appField;
    @UiField
    CheckBox otrField;
    @UiField
    TextField otherField;

    @UiField
    CheckBox yesField;
    @UiField
    CheckBox swField;
    @UiField
    CheckBox noField;
    @UiField
    CheckBox notField;
    @UiField
    CheckBox tskOtrField;
    @UiField
    TextField otherCompField;
    @UiField
    TextArea featureTextArea;
    @UiField
    TextArea otherTextArea;

    @UiField
    FieldLabel reasonField;

    @UiField
    FieldLabel compelteField;

    @UiField
    FieldLabel satisfyField;

    @UiField
    FieldLabel featureField;

    @UiField
    FieldLabel anythingField;
    private final ToggleGroup group;

    @UiField
    TextField otherSatisfiedField;

    @UiField
    Radio otsatField;

    public DEFeedbackViewImpl() {
        widget = uiBinder.createAndBindUi(this);
        container.setScrollMode(ScrollMode.AUTOY);
        group = new ToggleGroup();
        group.add(vastField);
        group.add(swsatField);
        group.add(okField);
        group.add(swdField);
        group.add(nsField);
        group.add(otsatField);
        reasonField.setHTML(buildRequiredFieldLabel(reasonField.getText()));
        compelteField.setHTML(buildRequiredFieldLabel(compelteField.getText()));
        satisfyField.setHTML(buildRequiredFieldLabel(satisfyField.getText()));
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
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

    private String buildRequiredFieldLabel(String label) {
        if (label == null) {
            return null;
        }

        return "<span style='color:red; top:-5px;' >*</span> " + label; //$NON-NLS-1$
    }

    private boolean validateQ1() {
        boolean ret = (expField.getValue() || mngField.getValue() || runField.getValue()
                || chkField.getValue() || appField.getValue());

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
        boolean ret = (yesField.getValue() || swField.getValue() || noField.getValue() || notField
                .getValue());

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
        boolean ret = (vastField.getValue() || swsatField.getValue() || okField.getValue()
                || swdField.getValue() || nsField.getValue());
        if (otsatField.getValue()) {
            otherSatisfiedField.setAllowBlank(false);
            return otherSatisfiedField.validate();
        } else {
            otherSatisfiedField.clearInvalid();
            return ret;
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("username", new JSONString(UserInfo.getInstance().getUsername()));
        obj.put("User-agent", new JSONString(Navigator.getUserAgent()));
        obj.put(reasonField.getText(), getAnswer1());
        obj.put(compelteField.getText(), getAnswer2());
        if (getAnswer3() != null) {
            obj.put(satisfyField.getText(), new JSONString(getAnswer3()));
        }
        if (featureTextArea.getValue() != null) {
            obj.put(featureField.getText(), new JSONString(featureTextArea.getValue()));
        }

        if (otherTextArea.getValue() != null) {
            obj.put(anythingField.getText(), new JSONString(otherTextArea.getValue()));
        }
        return obj;
    }

    private JSONArray getAnswer1() {
        JSONArray arr = new JSONArray();
        int counter = 0;
        if (expField.getValue()) {
            arr.set(counter++, new JSONString(expField.getBoxLabel()));
        }
        if (mngField.getValue()) {
            arr.set(counter++, new JSONString(mngField.getBoxLabel()));
        }
        if (runField.getValue()) {
            arr.set(counter++, new JSONString(runField.getBoxLabel()));
        }
        if (chkField.getValue()) {
            arr.set(counter++, new JSONString(chkField.getBoxLabel()));
        }
        if (appField.getValue()) {
            arr.set(counter++, new JSONString(appField.getBoxLabel()));
        }
        if (otrField.getValue()) {
            arr.set(counter++, new JSONString(otherField.getValue()));
        }

        return arr;
    }

    private JSONArray getAnswer2() {
        JSONArray arr = new JSONArray();
        int counter = 0;
        if (yesField.getValue()) {
            arr.set(counter++, new JSONString(yesField.getBoxLabel()));
        }
        if (swField.getValue()) {
            arr.set(counter++, new JSONString(swField.getBoxLabel()));
        }
        if (noField.getValue()) {
            arr.set(counter++, new JSONString(noField.getBoxLabel()));
        }
        if (notField.getValue()) {
            arr.set(counter++, new JSONString(notField.getBoxLabel()));
        }
        if (tskOtrField.getValue()) {
            arr.set(counter++, new JSONString(otherCompField.getValue()));
        }

        return arr;
    }

    private String getAnswer3() {
        if (vastField.getValue()) {
            return vastField.getBoxLabel();
        } else if (swsatField.getValue()) {
            return swsatField.getBoxLabel();
        } else if (okField.getValue()) {
            return okField.getBoxLabel();
        } else if (swdField.getValue()) {
            return swdField.getBoxLabel();
        } else if (nsField.getValue()) {
            return nsField.getBoxLabel();
        } else {
            return otherSatisfiedField.getValue();
        }

    }

}
