package org.iplantc.de.theme.base.client.apps.submit;

import org.iplantc.de.apps.client.SubmitAppForPublicUseView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.apps.AppsMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * @author jstroot
 */
public class SubmitAppViewDefaultAppearance implements SubmitAppForPublicUseView.SubmitAppAppearance {

    public interface Templates extends SafeHtmlTemplates {
        @Template("<span style='color:red; top:-5px;' >*</span>{0}")
        SafeHtml requiredLabel(String label);
    }

    private final AppsMessages appsMessages;
    private final IplantResources resources;
    private final IplantDisplayStrings iplantDisplayStrings;
    private final Templates templates;

    public SubmitAppViewDefaultAppearance() {
        this(GWT.<AppsMessages> create(AppsMessages.class),
             GWT.<IplantResources> create(IplantResources.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<Templates> create(Templates.class));
    }

    SubmitAppViewDefaultAppearance(final AppsMessages appsMessages,
                                   final IplantResources resources,
                                   final IplantDisplayStrings iplantDisplayStrings,
                                   final Templates templates) {
        this.appsMessages = appsMessages;
        this.resources = resources;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.templates = templates;
    }

    @Override
    public ImageResource categoryIcon() {
        return resources.category();
    }

    @Override
    public ImageResource categoryOpenIcon() {
        return resources.category_open();
    }

    @Override
    public String links() {
        return appsMessages.links();
    }

    @Override
    public String makePublicFail() {
        return appsMessages.makePublicFail();
    }

    @Override
    public String makePublicSuccessMessage(String appName) {
        return appsMessages.makePublicSuccessMessage(appName);
    }

    @Override
    public String publicNameNote() {
        return appsMessages.publicNameNote();
    }

    @Override
    public String publicName() {
        return appsMessages.publicName();
    }

    @Override
    public String publicDescriptionNote() {
        return appsMessages.publicDescriptionNote();
    }

    @Override
    public String publicSubmissionForm() {
        return appsMessages.publicSubmissionForm();
    }

    @Override
    public String publicSubmissionFormAttach() {
        return appsMessages.publicSubmissionFormAttach();
    }

    @Override
    public String publicSubmissionFormCategories() {
        return appsMessages.publicSubmissionFormCategories();
    }

    @Override
    public String completeRequiredFieldsError() {
        return iplantDisplayStrings.completeRequiredFieldsError();
    }

    @Override
    public String publishFailureDefaultMessage() {
        return appsMessages.publishFailureDefaultMessage();
    }

    @Override
    public ImageResource subCategoryIcon() {
        return resources.subCategory();
    }

    @Override
    public String submit() {
        return iplantDisplayStrings.submit();
    }

    @Override
    public String submitForPublicUse() {
        return appsMessages.submitForPublicUse();
    }

    @Override
    public String submitForPublicUseIntro() {
        return appsMessages.submitForPublicUseIntro();
    }

    @Override
    public String submitRequest() {
        return iplantDisplayStrings.submitRequest();
    }

    @Override
    public String submitting() {
        return iplantDisplayStrings.submitting();
    }

    @Override
    public String testDataLabel() {
        return templates.requiredLabel(appsMessages.testDataLabel()).asString() ;
    }

    @Override
    public String inputDescriptionEmptyText() {
        return appsMessages.inputDescriptionEmptyText();
    }

    @Override
    public String optionalParametersEmptyText() {
        return appsMessages.optionalParametersEmptyText();
    }

    @Override
    public String outputDescriptionEmptyText() {
        return appsMessages.outputDescriptionEmptyText();
    }

    @Override
    public ImageResource addIcon() {
        return resources.add();
    }

    @Override
    public String add() {
        return iplantDisplayStrings.add();
    }

    @Override
    public String delete() {
        return iplantDisplayStrings.delete();
    }

    @Override
    public ImageResource deleteIcon() {
        return resources.delete();
    }

    @Override
    public String publicDescription() {
        return templates.requiredLabel(appsMessages.publicDescription()).asString();
    }

    @Override
    public String publicAttach() {
        return appsMessages.publicAttach();
    }

    @Override
    public String describeInputLbl() {
        return templates.requiredLabel(appsMessages.describeInputLbl()).asString();
    }

    @Override
    public String describeParamLbl() {
        return templates.requiredLabel(appsMessages.describeParamLbl()).asString();
    }

    @Override
    public String describeOutputLbl() {
        return templates.requiredLabel(appsMessages.describeOutputLbl()).asString();
    }

    @Override
    public String publicCategories() {
        return templates.requiredLabel(appsMessages.publicCategories()).asString();
    }

    @Override
    public String testDataWarn() {
        return appsMessages.testDataWarn();
    }

    @Override
    public String warning() {
        return iplantDisplayStrings.warning();
    }
}
