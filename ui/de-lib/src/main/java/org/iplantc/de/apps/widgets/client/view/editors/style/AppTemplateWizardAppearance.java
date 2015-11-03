package org.iplantc.de.apps.widgets.client.view.editors.style;

import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsContextualHelpMessages;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;

import com.sencha.gxt.widget.core.client.button.IconButton;

import java.util.List;

/**
 * @author jstroot
 * 
 */
public interface AppTemplateWizardAppearance {
    interface AppTemplateWizardTemplates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<p style='text-overflow: ellipsis;overflow: hidden;white-space: nowrap;'>{0}</p>")
        SafeHtml contentPanelHeader(SafeHtml label);

        @SafeHtmlTemplates.Template("<p style='text-overflow: ellipsis;overflow: hidden;white-space: nowrap;'><span style='color: red;'>*&nbsp</span>{0}</p>")
        SafeHtml contentPanelHeaderRequired(SafeHtml label);

        @SafeHtmlTemplates.Template("{0}&nbsp;<img src='{1}' qtip='{2}' ></img>")
        SafeHtml fieldLabelImg(SafeHtml label, SafeUri img, String toolTip);

        @SafeHtmlTemplates.Template("{0}<img style='float: right;' src='{1}' qtip='{2}'></img>")
        SafeHtml fieldLabelImgFloatRight(SafeHtml label, SafeUri img, String toolTip);

        @SafeHtmlTemplates.Template("{0}&nbsp;<img src='{1}' title='{2}'></img>")
        SafeHtml fieldLabelImgChkBox(SafeHtml label, SafeUri img, String toolTip);

        @SafeHtmlTemplates.Template("{0}<img style='float: right;' src='{1}' title='{2}'></img>")
        SafeHtml fieldLabelImgFloatRightChkBox(SafeHtml label, SafeUri img, String toolTip);

        @SafeHtmlTemplates.Template("<span style='color: red;'>*&nbsp</span>")
        SafeHtml fieldLabelRequired();

    }

    interface Resources extends IplantResources {
        @Source("AppTemplateWizard.css")
        Style css();

        // KLUDGE Duplicated resource in apps theme as well.
        @Source("delete_rating.png")
        ImageResource deleteRating();

        // KLUDGE Duplicated resource in apps theme as well.
        @Source("delete_rating_hover.png")
        ImageResource deleteRatingHover();
    }

    interface Style extends CssResource {

        String appHeaderSelect();

        String argument();

        String argumentSelect();

        String delete();

        String deleteBtn();

        String deleteHover();

        String emptyGroupBgText();

        String grab();

        String grabbing();

    }

    public static final AppTemplateWizardAppearance INSTANCE = GWT.create(AppTemplateWizardAppearance.class);

    /**
     * @param label
     * @return a formatted label for the ArgumentGroup and AppTemplate ContentPanel headers.
     */
    SafeHtml createContentPanelHeaderLabel(SafeHtml label, boolean required);

    SafeHtml createContextualHelpLabel(String label, String toolTip);

    SafeHtml createContextualHelpLabelNoFloat(String label, String toolTip);

    /**
     * spl. casing for chkbox
     * 
     * @param label
     * @param toolTip
     * @return
     */
    SafeHtml createChkBoxContextualHelpLabel(String label, String toolTip);

    SafeHtml createChkBoxContextualHelpLabelNoFloat(String label, String toolTip);

    /**
     * @return the character limit which is applied to the <code>AppTemplate</code> <i>name</i> field in
     *         the  AppTemplatePropertyEditor.
     */
    int getAppNameCharLimit();

    IconButton getArgListDeleteButton();

    int getAutoExpandOnHoverDelay();

    int getAutoScrollDelay();

    int getAutoScrollRegionHeight();

    int getAutoScrollRepeatDelay();

    AppsWidgetsContextualHelpMessages getContextHelpMessages();

    int getDefaultArgListHeight();

    /**
     * @return default height for the tree selection widget.
     */
    int getDefaultTreeSelectionHeight();

    /**
     * @return returns a freshly constructed Error ImageElement.
     */
    ImageElement getErrorIconImg();

    /**
     * 
     * @param errors list of <code>EditorError</code>s whose messages will be used in the returned
     *            <code>ImageElement</code>'s "qtip" attribute.
     * @return an <code>ImageElement</code> with a "qtip" attribute populated with the messages of the
     *         given errors.
     */
    ImageElement getErrorIconImgWithErrQTip(List<EditorError> errors);

    AppsWidgetsPropertyPanelLabels getPropertyPanelLabels();

    Style getStyle();

    AppTemplateWizardTemplates getTemplates();

    /**
     * @return the safehtml which represents the "required field" text of an argument label.
     */
    SafeHtml getRequiredFieldLabel();

    /**
     * 
     * @param label
     * @param contextualHelp
     * @return an html representation of a contextual help label.
     */
    SafeHtml getContextualHelpLabel(SafeHtml label, String contextualHelp);

    /**
     * 
     * @param label
     * @param contextualHelp
     * @return an html representation of a contextual help label spl. cased for check box.
     */
    SafeHtml getChkBoxContextualHelpLabel(SafeHtml label, String contextualHelp);

    SafeHtml sanitizeHtml(String html);
}
