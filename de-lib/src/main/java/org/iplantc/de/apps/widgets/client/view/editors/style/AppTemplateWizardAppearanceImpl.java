package org.iplantc.de.apps.widgets.client.view.editors.style;

import org.iplantc.de.apps.widgets.client.view.util.IPlantSimpleHtmlSanitizer;
import org.iplantc.de.client.models.HasLabel;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsContextualHelpMessages;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;

import java.util.List;

/**
 * @author jstroot
 * 
 */
public class AppTemplateWizardAppearanceImpl implements AppTemplateWizardAppearance {

    private final AppsWidgetsContextualHelpMessages help;
    private final AppsWidgetsPropertyPanelLabels labels;
    private final Resources res;
    private final AppTemplateWizardTemplates templates;

    public AppTemplateWizardAppearanceImpl() {
        res = GWT.create(Resources.class);
        templates = GWT.create(AppTemplateWizardTemplates.class);
        labels = I18N.APPS_LABELS;
        help = I18N.APPS_HELP;
    }

    @Override
    public SafeHtml createContentPanelHeaderLabel(SafeHtml label, boolean required) {
        if (required) {
            return templates.contentPanelHeaderRequired(label);
        }
        return templates.contentPanelHeader(label);
    }

    @Override
    public SafeHtml createContextualHelpLabel(String labelToolTipText, String propertyToolTip) {
        return templates.fieldLabelImgFloatRight(SafeHtmlUtils.fromString(labelToolTipText), res.help().getSafeUri(), propertyToolTip);
    }

    @Override
    public SafeHtml createContextualHelpLabelNoFloat(String label, String toolTip) {
        return templates.fieldLabelImg(SafeHtmlUtils.fromString(label), res.help().getSafeUri(), toolTip);
    }

    @Override
    public int getAppNameCharLimit() {
        return 255;
    }

    @Override
    public IconButton getArgListDeleteButton() {
        IconButton argDeleteBtn = new IconButton(new IconConfig(res.css().delete(), res.css().deleteHover()));
        argDeleteBtn.addStyleName(res.css().deleteBtn());
        return argDeleteBtn;
    }

    @Override
    public int getAutoExpandOnHoverDelay() {
        return 500;
    }

    @Override
    public int getAutoScrollDelay() {
        return 200;
    }

    @Override
    public int getAutoScrollRegionHeight() {
        return 5;
    }

    @Override
    public int getAutoScrollRepeatDelay() {
        return 50;
    }

    @Override
    public AppsWidgetsContextualHelpMessages getContextHelpMessages() {
        return help;
    }

    @Override
    public int getDefaultArgListHeight() {
        return 200;
    }

    @Override
    public int getDefaultTreeSelectionHeight() {
        return 200;
    }

    @Override
    public ImageElement getErrorIconImg() {
        ImageElement errIconImg = Document.get().createImageElement();
        errIconImg.setSrc(res.exclamation().getSafeUri().asString());
        errIconImg.getStyle().setFloat(Float.LEFT);
        return errIconImg;
    }

    @Override
    public ImageElement getErrorIconImgWithErrQTip(List<EditorError> errors) {
        ImageElement errIconImg = getErrorIconImg();
        String errorString = "";
        for (EditorError err : errors) {
            if (err instanceof HasLabel) {
                errorString += ((HasLabel)err).getLabel() + ": ";
            }
            errorString += err.getMessage();
            if (errors.indexOf(err) != errors.size() - 1) {
                errorString += "<br>";
            }
        }
        errIconImg.setAttribute("qtip", errorString);
        return errIconImg;
    }

    @Override
    public AppsWidgetsPropertyPanelLabels getPropertyPanelLabels() {
        return labels;
    }

    @Override
    public Style getStyle() {
        res.css().ensureInjected();
        return res.css();
    }

    @Override
    public AppTemplateWizardTemplates getTemplates() {
        return templates;
    }

    @Override
    public SafeHtml getRequiredFieldLabel() {
        return templates.fieldLabelRequired();
    }

    @Override
    public SafeHtml getContextualHelpLabel(SafeHtml label, String contextualHelp) {
        return templates.fieldLabelImgFloatRight(label, res.info().getSafeUri(), contextualHelp);
    }

    @Override
    public SafeHtml sanitizeHtml(String html) {
        return IPlantSimpleHtmlSanitizer.sanitizeHtml(html);
    }

}
