package org.iplantc.de.resources.client.uiapps.integration;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface AppIntegrationMessages extends Messages {

    String paletteHeader();

    String cmdLinePreviewHeader();

    String previewUI();

    String previewJSON();

    String selectTool();

    String commandLineOrder();

    String noArguments();

    String unorderedArgument();

    SafeHtml argumentLabel();

    SafeHtml orderLabel();

    String saveSuccessful();

    String fileFolderCategoryTitle();

    String listsCategoryTitle();

    String textNumericalInputCategoryTitle();

    String outputCategoryTitle();

    String referenceGenomeCategoryTitle();
}
