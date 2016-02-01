package org.iplantc.de.resources.client.messages;

import org.iplantc.de.resources.client.uiapps.integration.AppIntegrationErrorMessages;

import java.util.List;

/**
 * Interface to represent the messages contained in resource bundle:
 * /Users/sriram/iplant/lib-workspace/ui
 * -resources-module/src/main/resources/org/iplantc/core/resources/client
 * /messages/IplantErrorStrings.properties'.
 */
public interface IplantErrorStrings extends com.google.gwt.i18n.client.Messages,
        AppIntegrationErrorMessages {

    /**
     * Translated "Unable to add collaborator(s). Please try again later.".
     * 
     * @return translated "Unable to add collaborator(s). Please try again later."
     */
    @DefaultMessage("Unable to add collaborator(s). Please try again later.")
    @Key("addCollabErrorMsg")
    String addCollabErrorMsg();

    /**
     * Translated "Could not add a Reference Genome.".
     * 
     * @return translated "Could not add a Reference Genome."
     */
    @DefaultMessage("Could not add a Reference Genome.")
    @Key("addRefGenomeError")
    String addRefGenomeError();

    /**
     * Translated "Analysis {0} failed to launch.".
     * 
     * @return translated "Analysis {0} failed to launch."
     */
    @DefaultMessage("Analysis {0} failed to launch.")
    @Key("analysisFailedToLaunch")
    String analysisFailedToLaunch(String arg0);

    /**
     * Translated "No application exists for the specified ID.".
     * 
     * @return translated "No application exists for the specified ID."
     */
    @DefaultMessage("No application exists for the specified ID.")
    @Key("appNotFound")
    String appNotFound();

    @DefaultMessage("Authorization Error: {0}")
    @Key("authError")
    String authError(String service);

    /**
     * Translated "Cannot create documentation page for app {0}".
     * 
     * @return translated "Cannot create documentation page for app {0}"
     */
    @DefaultMessage("Cannot create documentation page for app {0}")
    @Key("cantCreateConfluencePage")
    String cantCreateConfluencePage(String arg0);

    /**
     * Translated "Unable to complete this request. Please try again later.".
     * 
     * @return translated "Unable to complete this request. Please try again later."
     */
    @DefaultMessage("Unable to complete this request. Please try again later.")
    @Key("confluenceError")
    String confluenceError();

    /**
     * Translated "The server reported a missing or corrupt request parameter. Please try again.".
     * 
     * @return translated "The server reported a missing or corrupt request parameter. Please try again."
     */
    @DefaultMessage("The server reported a missing or corrupt request parameter. Please try again.")
    @Key("dataErrorBadOrMissingField")
    String dataErrorBadOrMissingField();

    /**
     * Translated "The server did not understand the request. Please try again.".
     * 
     * @return translated "The server did not understand the request. Please try again."
     */
    @DefaultMessage("The server did not understand the request. Please try again.")
    @Key("dataErrorInvalidJson")
    String dataErrorInvalidJson();

    /**
     * Translated "The server reported a missing request parameter. Please try again.".
     * 
     * @return translated "The server reported a missing request parameter. Please try again."
     */
    @DefaultMessage("The server reported a missing request parameter. Please try again.")
    @Key("dataErrorMissingQueryParameter")
    String dataErrorMissingQueryParameter();

    /**
     * Translated "The request was not made for a valid user.".
     * 
     * @return translated "The request was not made for a valid user."
     */
    @DefaultMessage("The request was not made for a valid user.")
    @Key("dataErrorNotAUser")
    String dataErrorNotAUser();

    /**
     * Translated "You are not authorized to make that request.".
     * 
     * @return translated "You are not authorized to make that request."
     */
    @DefaultMessage("You are not authorized to make that request.")
    @Key("dataErrorNotAuthorized")
    String dataErrorNotAuthorized();

    /**
     * Translated "Unable to retrieve inputs and outputs for selected apps.".
     * 
     * @return translated "Unable to retrieve inputs and outputs for selected apps."
     */
    @DefaultMessage("Unable to retrieve inputs and outputs for selected apps.")
    @Key("dataObjectsRetrieveError")
    String dataObjectsRetrieveError();

    /**
     * Translated "Unable to get the list of installed tools. Please try again later.".
     * 
     * @return translated "Unable to get the list of installed tools. Please try again later."
     */
    @DefaultMessage("Unable to get the list of installed tools. Please try again later.")
    @Key("dcLoadError")
    String dcLoadError();

    /**
     * Validation error message displayed when selected default analysis output folder path is invalid.
     * 
     * @return localized error string.
     */
    String defaultOutputFolderValidationError();

    /**
     * Translated "Unable to restore default preferences. Please try again.".
     * 
     * @return translated "Unable to restore default preferences. Please try again."
     */
    @DefaultMessage("Unable to restore default preferences. Please try again.")
    @Key("defaultPrefError")
    String defaultPrefError();

    /**
     * Translated "Unable to retrieve list of integrated tools.".
     * 
     * @return translated "Unable to retrieve list of integrated tools."
     */
    @DefaultMessage("Unable to retrieve list of integrated tools.")
    @Key("deployedComponentRetrievalFailure")
    String deployedComponentRetrievalFailure();

    /**
     * Translated "The following file / folder does not exist: {0}".
     * 
     * @return translated "The following file / folder does not exist: {0}"
     */
    @DefaultMessage("The following file / folder does not exist: {0}")
    @Key("diskResourceDoesNotExist")
    String diskResourceDoesNotExist(String arg0);

    /**
     * Translated "The following file(s) / folder(s) do not exist:<br />
     * {0}".
     * 
     * @return translated "The following file(s) / folder(s) do not exist:<br />
     *         {0}"
     */
    @DefaultMessage("The following file(s) / folder(s) do not exist:<br />{0}")
    @Key("diskResourcesDoNotExist")
    String diskResourcesDoNotExist(String diskResourceList);

    /**
     * Translated "The following disk resources already exist:<br />
     * {0}".
     * 
     * @return translated "The following disk resources already exist:<br />
     *         {0}"
     */
    @DefaultMessage("The following disk resources already exist:<br />{0}")
    @Key("diskResourceExists")
    String diskResourceExists(String arg0);

    /**
     * Translated "The delete request was incomplete. Please try again.".
     * 
     * @return translated "The delete request was incomplete. Please try again."
     */
    @DefaultMessage("The delete request was incomplete. Please try again.")
    @Key("diskResourceIncompleteDeletion")
    String diskResourceIncompleteDeletion();

    /**
     * Translated "The rename request was incomplete. Please try again.".
     * 
     * @return translated "The rename request was incomplete. Please try again."
     */
    @DefaultMessage("The rename request was incomplete. Please try again.")
    @Key("diskResourceIncompleteRename")
    String diskResourceIncompleteRename();

    /**
     * Translated "The following are files:<br />
     * {0}".
     * 
     * @return translated "The following are files:<br />
     *         {0}"
     */
    @DefaultMessage("The following are files:<br />{0}")
    @Key("diskResourceIsAFile")
    String diskResourceIsAFile(String arg0);

    /**
     * Translated "The following are folders:<br />
     * {0}".
     * 
     * @return translated "The following are folders:<br />
     *         {0}"
     */
    @DefaultMessage("The following are folders:<br />{0}")
    @Key("diskResourceIsAFolder")
    String diskResourceIsAFolder(String arg0);

    /**
     * Translated "The following are not files:<br />
     * {0}".
     * 
     * @return translated "The following are not files:<br />
     *         {0}"
     */
    @DefaultMessage("The following are not files:<br />{0}")
    @Key("diskResourceNotAFile")
    String diskResourceNotAFile(String arg0);

    /**
     * Translated "The following are not folders:<br />
     * {0}".
     * 
     * @return translated "The following are not folders:<br />
     *         {0}"
     */
    @DefaultMessage("The following are not folders:<br />{0}")
    @Key("diskResourceNotAFolder")
    String diskResourceNotAFolder(String arg0);

    /**
     * Translated "The following disk resources are not readable:<br />
     * {0}".
     * 
     * @return translated "The following disk resources are not readable:<br />
     *         {0}"
     */
    @DefaultMessage("The following disk resources are not readable:<br />{0}")
    @Key("diskResourceNotReadable")
    String diskResourceNotReadable(String arg0);

    /**
     * Translated "The following disk resources are not writable:<br />
     * {0}".
     * 
     * @return translated "The following disk resources are not writable:<br />
     *         {0}"
     */
    @DefaultMessage("The following disk resources are not writable:<br />{0}")
    @Key("diskResourceNotWriteable")
    String diskResourceNotWriteable(String arg0);

    /**
     * Translated "The following disk resources are readable:<br />
     * {0}".
     * 
     * @return translated "The following disk resources are readable:<br />
     *         {0}"
     */
    @DefaultMessage("The following disk resources are readable:<br />{0}")
    @Key("diskResourceReadable")
    String diskResourceReadable(String arg0);

    /**
     * Translated "The following disk resources are writable:<br />
     * {0}".
     * 
     * @return translated "The following disk resources are writable:<br />
     *         {0}"
     */
    @DefaultMessage("The following disk resources are writable:<br />{0}")
    @Key("diskResourceWriteable")
    String diskResourceWriteable(String arg0);

    /**
     * The error indicating that a system message could not be dismissed.
     */
    @DefaultMessage("The system message could not be dismissed.")
    @Key("dismissMessageFailed")
    String dismissMessageFailed();

    /**
     * A message indicating the user has requested to upload the same file more than once.
     */
    @DefaultMessage("Duplicate file to upload")
    @Key("duplicateUpload")
    String duplicateUpload();

    String ensemblError();

    /**
     * Error msg to show when empty trash fails
     * 
     * @return
     */
    String emptyTrashError();

    /**
     * Translated "The following file(s) do not exist:<br />
     * {0}".
     * 
     * @return translated "The following file(s) do not exist:<br />
     *         {0}"
     */
    @DefaultMessage("The following file(s) do not exist:<br />{0}")
    @Key("fileDoesNotExist")
    String fileDoesNotExist(String arg0);

    /**
     * Translated "This file already exists.".
     * 
     * @return translated "This file already exists."
     */
    @DefaultMessage("This file already exists.")
    @Key("fileExist")
    String fileExist();

    /**
     * Translated "The following file(s) already exist:<br />
     * {0}".
     * 
     * @return translated "The following file(s) already exist:<br />
     *         {0}"
     */
    @DefaultMessage("The following file(s) already exist:<br />{0}")
    @Key("fileExists")
    String fileExists(String arg0);

    /**
     * Translated "The following file(s) are not readable:<br />
     * {0}".
     * 
     * @return translated "The following file(s) are not readable:<br />
     *         {0}"
     */
    @DefaultMessage("The following file(s) are not readable:<br />{0}")
    @Key("fileNotReadable")
    String fileNotReadable(String arg0);

    /**
     * Translated "The following file(s) are not writable:<br />
     * {0}".
     * 
     * @return translated "The following file(s) are not writable:<br />
     *         {0}"
     */
    @DefaultMessage("The following file(s) are not writable:<br />{0}")
    @Key("fileNotWriteable")
    String fileNotWriteable(String arg0);

    /**
     * Translated "The following file(s) are readable:<br />
     * {0}".
     * 
     * @return translated "The following file(s) are readable:<br />
     *         {0}"
     */
    @DefaultMessage("The following file(s) are readable:<br />{0}")
    @Key("fileReadable")
    String fileReadable(String arg0);

    /**
     * Translated "Upload of {0} failed.".
     * 
     * @return translated "Upload of {0} failed."
     * 
     * @deprecated use fileUploadsFailed(List) instead
     */
    @Deprecated
    @DefaultMessage("Upload of {0} failed.")
    @Key("fileUploadFailed")
    String fileUploadFailed(String arg0);

    /**
     * A message indicating that a file failed to upload. This message should be used when the file
     * that failed to upload is obvious from the context.
     */
    @DefaultMessage("upload failed")
    @Key("fileUploadFailedAnon")
    String fileUploadFailedAnon();

    /**
     * A message indicating that one or more files failed to upload.
     * 
     * @param files The list of files that couldn't be uploaded.
     */
    @DefaultMessage("The files {0,list} failed to upload.")
    @AlternateMessage({"=1", "The file {0,list} failed to upload."})
    @Key("fileUploadsFailed")
    String fileUploadsFailed(@PluralCount List<String> files);
    
    /**
     * Translated "The following file(s) are writable:<br />
     * {0}".
     * 
     * @return translated "The following file(s) are writable:<br />
     *         {0}"
     */
    @DefaultMessage("The following file(s) are writable:<br />{0}")
    @Key("fileWriteable")
    String fileWriteable(String arg0);

    /**
     * Translated "The following folder(s) do not exist:<br />
     * {0}".
     * 
     * @return translated "The following folder(s) do not exist:<br />
     *         {0}"
     */
    @DefaultMessage("The following folder(s) do not exist:<br />{0}")
    @Key("folderDoesNotExist")
    String folderDoesNotExist(String arg0);

    /**
     * Translated "The following folder(s) already exist:<br />
     * {0}".
     * 
     * @return translated "The following folder(s) already exist:<br />
     *         {0}"
     */
    @DefaultMessage("The following folder(s) already exist:<br />{0}")
    @Key("folderExists")
    String folderExists(String arg0);

    /**
     * Translated "Folder \"{0}\" not found.".
     * 
     * @return translated "Folder \"{0}\" not found."
     */
    @DefaultMessage("Folder \"{0}\" not found.")
    @Key("folderNotFound")
    String folderNotFound(String arg0);

    /**
     * Translated "The following folder(s) are not readable:<br />
     * {0}".
     * 
     * @return translated "The following folder(s) are not readable:<br />
     *         {0}"
     */
    @DefaultMessage("The following folder(s) are not readable:<br />{0}")
    @Key("folderNotReadable")
    String folderNotReadable(String arg0);

    /**
     * Translated "The following folder(s) are not writable:<br />
     * {0}".
     * 
     * @return translated "The following folder(s) are not writable:<br />
     *         {0}"
     */
    @DefaultMessage("The following folder(s) are not writable:<br />{0}")
    @Key("folderNotWriteable")
    String folderNotWriteable(String arg0);

    /**
     * Translated "The following folder(s) are readable:<br />
     * {0}".
     * 
     * @return translated "The following folder(s) are readable:<br />
     *         {0}"
     */
    @DefaultMessage("The following folder(s) are readable:<br />{0}")
    @Key("folderReadable")
    String folderReadable(String arg0);

    /**
     * Translated "Could not refresh the contents of the folder {0}".
     *
     * @return translated "Could not refresh the contents of the folder {0}"
     */
    @DefaultMessage("Could not refresh the contents of the folder {0}")
    @Key("folderRefreshFailed")
    String folderRefreshFailed(String folder);

    /**
     * Translated "The following folder(s) are writable:<br />
     * {0}".
     * 
     * @return translated "The following folder(s) are writable:<br />
     *         {0}"
     */
    @DefaultMessage("The following folder(s) are writable:<br />{0}")
    @Key("folderWriteable")
    String folderWriteable(String arg0);

    /**
     * Translated "Import of {0} failed.".
     * 
     * @return translated "Import of {0} failed."
     */
    @DefaultMessage("Import of {0} failed.")
    @Key("importFailed")
    String importFailed(String arg0);

    /**
     * A message presented to the user when an invalid tool request is made.
     */
    @DefaultMessage("Your tool request is invalid. Please fix the issue(s).")
    @Key("invalidToolRequest")
    String invalidToolRequest();

    /**
     * Translated "Invalid file name entered.".
     * 
     * @return translated "Invalid file name entered."
     */
    @DefaultMessage("Invalid file name entered.")
    @Key("invalidFilenameEntered")
    String invalidFilenameEntered();

    /**
     * Translated
     * "Unable to request login session extension; please log out and log back in at your earliest convenience."
     * .
     * 
     * @return translated
     *         "Unable to request login session extension; please log out and log back in at your earliest convenience."
     */
    @DefaultMessage("Unable to request login session extension; please log out and log back in at your earliest convenience.")
    @Key("keepaliveRequestFailed")
    String keepaliveRequestFailed();

    /**
     * The error displayed when system messages cannot be retrieved from storage.
     */
    @DefaultMessage("The system messages could not be loaded.")
    @Key("loadMessagesFailed")
    String loadMessagesFailed();

    /**
     * Error msg shown when an attempt to load App references goes awry!!
     * 
     * @return
     */
    String loadReferencesError();

    /**
     * Translated "Could not load the previous session, however, you may continue working safely.".
     * 
     * @return translated
     *         "Could not load the previous session, however, you may continue working safely."
     */
    @DefaultMessage("Could not load the previous session, however, you may continue working safely.")
    @Key("loadSessionFailureNotice")
    String loadSessionFailureNotice();

    /**
     * Translated "Each step must have at least one output to input mapping (except Step 1).".
     * 
     * @return translated "Each step must have at least one output to input mapping (except Step 1)."
     */
    @DefaultMessage("Each step must have at least one output to input mapping (except Step 1).")
    @Key("mappingStepError")
    String mappingStepError();

    /**
     * the error displayed when a stored system message cannot be marked as received
     */
    @DefaultMessage("The system messages could not be marked as received.")
    @Key("markMessageReceivedFailed")
    String markMessageReceivedFailed();

    /**
     * the error displayed when a stored system message cannot be marked as seen
     */
    @DefaultMessage("The system messages could not be marked as seen.")
    @Key("markMessageSeenFailed")
    String markMessageSeenFailed();

    /**
     * Translated "Could not move Application \"{0}\".".
     * 
     * @return translated "Could not move Application \"{0}\"."
     */
    @DefaultMessage("Could not move Application \"{0}\".")
    @Key("moveApplicationError")
    String moveApplicationError(String arg0);

    /**
     * Translated
     * "An error occurred while processing your request. Please contact CyVerse support at support@cyverse.org"
     * .
     * 
     * @return translated
     *         "An error occurred while processing your request. Please contact CyVerse support at support@cyverse.org"
     */
    @DefaultMessage("An error occurred while processing your request. Please contact CyVerse support at support@cyverse.org.")
    @Key("newToolRequestError")
    String newToolRequestError();

    /**
     * Translated "No folder selected.".
     * 
     * @return translated "No folder selected."
     */
    @DefaultMessage("No folder selected.")
    @Key("noFolderSelected")
    String noFolderSelected();

    /**
     * Translated "Unable to delete notifications.".
     * 
     * @return translated "Unable to delete notifications."
     */
    @DefaultMessage("Unable to delete notifications.")
    @Key("notificationDeletFail")
    String notificationDeletFail();

    /**
     * Translated "Permission Error".
     * 
     * @return translated "Permission Error"
     */
    @DefaultMessage("Permission Error")
    @Key("permissionErrorTitle")
    String permissionErrorTitle();

    /**
     * Translated "Unable to remove collaborator(s). Please try again later.".
     * 
     * @return translated "Unable to remove collaborator(s). Please try again later."
     */
    @DefaultMessage("Unable to remove collaborator(s). Please try again later.")
    @Key("removeCollabErrorMsg")
    String removeCollabErrorMsg();

    /**
     * Translated "Cannot move an ancestor folder into one of its descendants.".
     * 
     * @return translated "Cannot move an ancestor folder into one of its descendants."
     */
    @DefaultMessage("Cannot move an ancestor folder into one of its descendants.")
    @Key("resourcesContainAncestors")
    String resourcesContainAncestors();

    /**
     * Translated "Failed to retrieve app group contents.".
     * 
     * @return translated "Failed to retrieve app group contents."
     */
    @DefaultMessage("Failed to retrieve app group contents.")
    @Key("retrieveAppListingFailed")
    String retrieveAppListingFailed();

    /**
     * Translated "Failed to retrieve saved filters.".
     * 
     * @return translated "Failed to retrieve saved filters."
     */
    @DefaultMessage("Failed to retrieve saved filters.")
    @Key("retrieveSavedQueryTemplatesFailed")
    String retrieveSavedQueryTemplatesFailed();

    /**
     * Translated "Unable to save this parameters as a file. Please try again.".
     * 
     * @return translated "Unable to save this parameters as a file. Please try again."
     */
    @DefaultMessage("Unable to save this parameters as a file. Please try again.")
    @Key("saveParamFailed")
    String saveParamFailed();

    /**
     * Error msg to show when search fails
     * 
     * @return
     */
    String searchError();

    /**
     * Translated "Unable to save search history.".
     * 
     * @return translated "Unable to save search history."
     */
    @DefaultMessage("Unable to save search history.")
    @Key("searchHistoryError")
    String searchHistoryError();

    /**
     * Translated "Unable to complete this request. Please try again later.".
     * 
     * @return translated "Unable to complete this request. Please try again later."
     */
    @DefaultMessage("Unable to complete this request. Please try again later.")
    @Key("shareFailed")
    String shareFailed();

    /**
     * Translated "Tree retrieval failed.".
     * 
     * @return translated "Tree retrieval failed."
     */
    @DefaultMessage("Tree retrieval failed.")
    @Key("treeServiceRetrievalFailed")
    String treeServiceRetrievalFailed();

    /**
     * Translated "Unable to build workspace.".
     * 
     * @return translated "Unable to build workspace."
     */
    @DefaultMessage("Unable to build workspace.")
    @Key("unableToBuildWorkspace")
    String unableToBuildWorkspace();

    /**
     * Translated "Unable to retrieve manifest for file {0}.".
     * 
     * @return translated "Unable to retrieve manifest for file {0}."
     */
    @DefaultMessage("Unable to retrieve manifest for file {0}.")
    @Key("unableToRetrieveFileManifest")
    String unableToRetrieveFileManifest(String arg0);

    /**
     * Translated "Unable to open the selected App".
     * 
     * @return translated "Unable to open the selected App"
     */
    @DefaultMessage("Unable to open the selected App")
    @Key("unableToRetrieveWorkflowGuide")
    String unableToRetrieveWorkflowGuide();

    /**
     * Translated "Could not update Reference Genome.".
     * 
     * @return translated "Could not update Reference Genome."
     */
    @DefaultMessage("Could not update Reference Genome.")
    @Key("updateRefGenomeError")
    String updateRefGenomeError();

    /**
     * Translated "Unable to publish your workflow.".
     * 
     * @return translated "Unable to publish your workflow."
     */
    @DefaultMessage("Unable to publish your workflow.")
    @Key("workflowPublishError")
    String workflowPublishError();

    /**
     * Translated
     * "A workflow must have a name and description, 2 or more apps, and at least one input of each app must come from an output of a previous app."
     * .
     * 
     * @return translated
     *         "A workflow must have a name and description, 2 or more apps, and at least one input of each app must come from an output of a previous app."
     */
    @DefaultMessage("A workflow must have a name and description, 2 or more apps, and at least one input of each app must come from an output of a previous app.")
    @Key("workflowValidationError")
    String workflowValidationError();

    // tags
    String tagCreateError();

    String tagRetrieveError();

}
