package org.iplantc.de.apps.client;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppRefLink;
import org.iplantc.de.client.models.apps.PublishAppRequest;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.TreeStore;

import java.util.List;

/**
 * @author jstroot
 */
public interface SubmitAppForPublicUseView extends IsWidget {

    interface SubmitAppAppearance {

        ImageResource categoryIcon();

        ImageResource categoryOpenIcon();

        String links();

        String makePublicFail();

        String makePublicSuccessMessage(String appName);

        String publicNameNote();

        String publicName();

        String publicDescriptionNote();

        String publicSubmissionForm();

        String publicSubmissionFormAttach();

        String publicSubmissionFormCategories();

        String completeRequiredFieldsError();

        String publishFailureDefaultMessage();

        ImageResource subCategoryIcon();

        String submit();

        String submitForPublicUse();

        String submitForPublicUseIntro();

        String submitRequest();

        String submitting();

        String testDataLabel();

        String inputDescriptionEmptyText();

        String optionalParametersEmptyText();

        String outputDescriptionEmptyText();

        ImageResource addIcon();

        String add();

        String delete();

        ImageResource deleteIcon();

        String publicDescription();

        String publicAttach();

        String describeInputLbl();

        String describeParamLbl();

        String describeOutputLbl();

        String publicCategories();

        String testDataWarn();

        String warning();
    }

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {
        void onSubmit();

        void go(HasOneWidget container, App selectedApp, AsyncCallback<String> callback);
    }

    TreeStore<OntologyHierarchy> getTreeStore();

    PublishAppRequest getPublishAppRequest();

    App getSelectedApp();

    boolean validate();

    public void loadReferences(List<AppRefLink> refs);

    void setSelectedApp(App selectedApp);
}
