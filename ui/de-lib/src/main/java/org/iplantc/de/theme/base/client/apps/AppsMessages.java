package org.iplantc.de.theme.base.client.apps;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * Created by jstroot on 2/23/15.
 * @author jstroot
 */
public interface AppsMessages extends Messages {
    String appDeleteWarning();

    String appLaunchWithoutToolError();

    String appRemoveFailure();

    String clickAppInfo();

    String describeInputLbl();

    String describeOutputLbl();

    String describeParamLbl();

    String didNotLike();

    String editMenuItem();

    String failToRetrieveApp();

    String favServiceFailure();

    String fetchAppDetailsError(String errorMessage);

    SafeHtml getAppDocError(String errorMessage);

    String hateIt();

    String inputDescriptionEmptyText();

    String integratedBy();

    String likedIt();

    String links();

    String lovedIt();

    String makePublicFail();

    String makePublicSuccessMessage(String appName);

    String newApp();

    String optionalParametersEmptyText();

    String outputDescriptionEmptyText();

    String publicAttach();

    String publicCategories();

    String publicDescription();

    String publicDescriptionNote();

    String publicName();

    String publicNameNote();

    String publicSubmissionForm();

    String publicSubmissionFormAttach();

    String publicSubmissionFormCategories();

    String publishFailureDefaultMessage();

    String publishedOn();

    String rating();

    String reallyLikedIt();

    String refLbl();

    String requestTool();

    String run();

    SafeHtml saveAppDocError(String errorMessage);

    String searchAppResultsHeader(String searchText, int total);

    String searchApps();

    String shareMenuItem();

    String submitForPublicUse();

    String submitForPublicUseIntro();

    String testDataLabel();

    String testDataWarn();

    String useWf();

    String version();

    String workflow();

    String featureNotSupported();

    String url();

    String appUrl();

    String copyAppUrl();

    String copyAppSuccessMessage(String appName);
}
