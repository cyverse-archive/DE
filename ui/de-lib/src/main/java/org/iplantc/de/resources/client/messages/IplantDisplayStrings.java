package org.iplantc.de.resources.client.messages;

import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * Interface to represent the messages contained in resource bundle:
 * /Users/sriram/iplant/lib-workspace/ui
 * -resources-module/src/main/resources/org/iplantc/core/resources/client
 * /messages/IplantDisplayStrings.properties'.
 */
public interface IplantDisplayStrings extends com.google.gwt.i18n.client.Messages {

    /**
     * Translated "About Discovery Environment".
     *
     * @return translated "About Discovery Environment"
     */
    @DefaultMessage("About Discovery Environment")
    @Key("aboutDiscoveryEnvironment")
    String aboutDiscoveryEnvironment();

    /**
     * Translated "Add".
     *
     * @return translated "Add"
     */
    @DefaultMessage("Add")
    @Key("add")
    String add();

    /**
     * Translated "Add to favorite apps.".
     *
     * @return translated "Add to favorite apps."
     */
    @DefaultMessage("Add to favorite apps.")
    @Key("addAppToFav")
    String addAppToFav();

    /**
     * Translated "Reference Genome added.".
     *
     * @return translated "Reference Genome added."
     */
    @DefaultMessage("Reference Genome added.")
    @Key("addRefGenome")
    String addRefGenome();

    /**
     * Translated "Select additional data".
     *
     * @return translated "Select additional data"
     */
    @DefaultMessage("Select additional data")
    @Key("addnlData")
    String addnlData();

    /**
     * Translated
     * "&nbsp;Click on the app name to edit. To re-categorize an app, drag and drop it into appropriate category in the categories tree."
     * .
     *
     * @return translated
     *         "&nbsp;Click on the app name to edit. To re-categorize an app, drag and drop it into appropriate category in the categories tree."
     */
    @DefaultMessage("&nbsp;Click on the app name to edit. To re-categorize an app, drag and drop it into appropriate category in the categories tree.")
    @Key("adminInfo")
    String adminInfo();

    /**
     * Translated "Yes".
     *
     * @return translated "Yes"
     */
    @DefaultMessage("Yes")
    @Key("affirmativeResponse")
    String affirmativeResponse();

    @DefaultMessage("Your browser will be temporarily redirected to Agave for authorization.")
    @Key("agaveAuthRequiredMsg")
    String agaveAuthRequiredMsg();

    /**
     * Translated "Alert".
     *
     * @return translated "Alert"
     */
    @DefaultMessage("Alert")
    @Key("alert")
    String alert();

    /**
     * Translated "Analyses".
     *
     * @return translated "Analyses"
     */
    @DefaultMessage("Analyses")
    @Key("analyses")
    String analyses();

    /**
     * Translated "Analyses that are not in completed or failed status were not deleted.".
     *
     * @return translated "Analyses that are not in completed or failed status were not deleted."
     */
    @DefaultMessage("Analyses that are not in completed or failed status were not deleted.")
    @Key("analysesNotDeleted")
    String analysesNotDeleted();

    /**
     * Waring that analysis may fail if input with spl chars is used
     *
     * @param splChars
     * @return
     */
    String analysisFailureWarning(String splChars);

    /**
     * Translated "Overview".
     *
     * @return translated "Overview"
     */
    @DefaultMessage("Overview")
    @Key("analysisOverview")
    String analysisOverview();

    /**
     * Translated "Analysis Submitted".
     *
     * @return translated "Analysis Submitted"
     */
    @DefaultMessage("Analysis Submitted")
    @Key("analysisSubmitted")
    String analysisSubmitted();

    /**
     * Translated
     * "Your analysis has been submitted. You will be notified about its progress through notifications."
     * .
     *
     * @return translated
     *         "Your analysis has been submitted. You will be notified about its progress through notifications."
     */
    @DefaultMessage("Your analysis has been submitted. You will be notified about its progress through notifications.")
    @Key("analysisSubmittedMsg")
    String analysisSubmittedMsg();

    /**
     * Translated "Rating Comment".
     *
     * @return translated "Rating Comment"
     */
    @DefaultMessage("Rating Comment")
    @Key("appCommentDialogTitle")
    String appCommentDialogTitle();

    /**
     * Translated "Please add a comment to accompany your rating of the {0} tool:".
     *
     * @return translated "Please add a comment to accompany your rating of the {0} tool:"
     */
    @DefaultMessage("Please add a comment to accompany your rating of the {0} tool:")
    @Key("appCommentExplanation")
    String appCommentExplanation(String arg0);

    /**
     * Translated "{0} apps in workflow".
     *
     * @return translated "{0} apps in workflow"
     */
    @DefaultMessage("{0} apps in workflow")
    @Key("appCountPlural")
    String appCountPlural(int appCount);

    /**
     * Translated "1 app in workflow".
     *
     * @return translated "1 app in workflow"
     */
    @DefaultMessage("1 app in workflow")
    @Key("appCountSingular")
    String appCountSingular();

    /**
     * Translated "Disabled".
     *
     * @return translated "Disabled"
     */
    @DefaultMessage("Disabled")
    @Key("appDisabled")
    String appDisabled();

    /**
     * Translated "App Info".
     *
     * @return translated "App Info"
     */
    @DefaultMessage("App Info")
    @Key("appInfo")
    String appInfo();

    /**
     * Translated "Apps Integration Module".
     *
     * @return translated "Apps Integration Module"
     */
    @DefaultMessage("Apps Integration Module")
    @Key("appIntegrationModule")
    String appIntegrationModule();

    /**
     * Translated "This app is temporarily unavailable".
     *
     * @return translated "This app is temporarily unavailable"
     */
    @DefaultMessage("This app is temporarily unavailable")
    @Key("appUnavailable")
    String appUnavailable();

    /**
     * Translated "Append {0} to the end of the Workflow.".
     *
     * @return translated "Append {0} to the end of the Workflow."
     */
    @DefaultMessage("Append {0} to the end of the Workflow.")
    @Key("appendAppToWorkflow")
    String appendAppToWorkflow(String arg0);

    /**
     * Translated "Apps".
     *
     * @return translated "Apps"
     */
    @DefaultMessage("Apps")
    @Key("applications")
    String applications();

    /**
     * Translated "Apply".
     *
     * @return translated "Apply"
     */
    @DefaultMessage("Apply")
    @Key("applyOperation")
    String applyOperation();

    /**
     * Translated "Argument option".
     *
     * @return translated "Argument option"
     */
    @DefaultMessage("Argument option")
    @Key("argumentOption")
    String argumentOption();

    /**
     * Translated "Argument order".
     *
     * @return translated "Argument order"
     */
    @DefaultMessage("Argument order")
    @Key("argumentOrder")
    String argumentOrder();

    /**
     * Translated "Architecture".
     *
     * @return translated "Architecture"
     */
    @DefaultMessage("Architecture")
    @Key("architecture")
    String architecture();

    /**
     * Translated "Attach sample input files (zip multiple files)".
     *
     * @return translated "Attach sample input files (zip multiple files)"
     */
    @DefaultMessage("Attach sample input files (zip multiple files)")
    @Key("attachSampleInput")
    String attachSampleInput();

    /**
     * Translated "Attach generated output files and logs (zip multiple files)".
     *
     * @return translated "Attach generated output files and logs (zip multiple files)"
     */
    @DefaultMessage("Attach generated output files and logs (zip multiple files)")
    @Key("attachSampleOutput")
    String attachSampleOutput();

    /**
     * Translated "Attribution".
     * 
     * @return translated "Attribution"
     */
    @DefaultMessage("Attribution")
    @Key("attribution")
    String attribution();

    /**
     * Translated "Available Files".
     *
     * @return translated "Available Files"
     */
    @DefaultMessage("Available Files")
    @Key("availableFiles")
    String availableFiles();

    /**
     * Translated "Average Community Rating".
     *
     * @return translated "Average Community Rating"
     */
    @DefaultMessage("Average Community Rating")
    @Key("avgCommunityRating")
    String avgCommunityRating();

    /**
     * Translated "Average User Rating".
     *
     * @return translated "Average User Rating"
     */
    @DefaultMessage("Average User Rating")
    @Key("avgUserRating")
    String avgUserRating();

    /**
     * Translated "Browse previously created barcode files:".
     *
     * @return translated "Browse previously created barcode files:"
     */
    @DefaultMessage("Browse previously created barcode files:")
    @Key("browseBarcodeFiles")
    String browseBarcodeFiles();

    /**
     * Translated "Browse previously created 3'' adapter files:".
     *
     * @return translated "Browse previously created 3'' adapter files:"
     */
    @DefaultMessage("Browse previously created 3'' adapter files:")
    @Key("browseClipperFiles")
    String browseClipperFiles();

    /**
     * Translated "Cancel".
     *
     * @return translated "Cancel"
     */
    @DefaultMessage("Cancel")
    @Key("cancel")
    String cancel();

    /**
     * Translated "Category".
     * 
     * @return translated "Category"
     */
    @DefaultMessage("Category")
    @Key("category")
    String category();

    /**
     * Translated "Categories".
     *
     * @return translated "Categories"
     */
    @DefaultMessage("Categories")
    @Key("categories")
    String categories();

    /**
     * Translated "Suggested display categories".
     *
     * @return translated "Suggested display categories"
     */
    @DefaultMessage("Suggested display categories")
    @Key("categorySelect")
    String categorySelect();

    /**
     * Translated "Change Permissions".
     *
     * @return translated "Change Permissions"
     */
    @DefaultMessage("Change Permissions")
    @Key("changePermissions")
    String changePermissions();

    /**
     * Translated "filename".
     *
     * @return translated "filename"
     */
    @DefaultMessage("filename")
    @Key("clipperFileName")
    String clipperFileName();

    /**
     * Translated "Close".
     *
     * @return translated "Close"
     */
    @DefaultMessage("Close")
    @Key("close")
    String close();

    /**
     * Translated "There are unsaved changes. Do you still want to close the window ?".
     *
     * @return translated "There are unsaved changes. Do you still want to close the window ?"
     */
    @DefaultMessage("There are unsaved changes. Do you still want to close the window ?")
    @Key("closeDirtyWindow")
    String closeDirtyWindow();

    /**
     * Translated "Enter instructions for how to use the tool in the Unix environment".
     *
     * @return translated "Enter instructions for how to use the tool in the Unix environment"
     */
    @DefaultMessage("Enter instructions for how to use the tool in the Unix environment")
    @Key("cmdLineRun")
    String cmdLineRun();

    /**
     * 
     * @return
     */
    @DefaultMessage("CoGe")
    String coge();
    
    /**
     * 
     * @return
     */
    @DefaultMessage("Would you like to load this genome in CoGe ?")
    String cogePrompt();
    
    /**
     * Translated "Search for users".
     *
     * @return translated "Search for users"
     */
    @DefaultMessage("Search for users")
    @Key("collabSearchPrompt")
    String collabSearchPrompt();

    /**
     * Translated "{0}  is now collaborator with you.".
     *
     * @return translated "{0}  is now collaborator with you."
     */
    @DefaultMessage("{0}  is now collaborator with you.")
    @Key("collaboratorAddConfirm")
    String collaboratorAddConfirm(String arg0);

    /**
     * Translated "Collaborator Added".
     *
     * @return translated "Collaborator Added"
     */
    @DefaultMessage("Collaborator Added")
    @Key("collaboratorAdded")
    String collaboratorAdded();

    /**
     * Translated "{0}  removed from your collaborators list".
     *
     * @return translated "{0}  removed from your collaborators list"
     */
    @DefaultMessage("{0}  removed from your collaborators list")
    @Key("collaboratorRemoveConfirm")
    String collaboratorRemoveConfirm(String arg0);

    /**
     * Translated "Collaborator Removed".
     *
     * @return translated "Collaborator Removed"
     */
    @DefaultMessage("Collaborator Removed")
    @Key("collaboratorRemoved")
    String collaboratorRemoved();

    /** Translated "Cannot add yourself as collaborator.".
    *
    * @return translated "Cannot add yourself as collaborator!"
    */
   @DefaultMessage("Cannot add yourself as collaborator.")
   @Key("collaboratorSelfAdd")
    String collaboratorSelfAdd();
    
    
    /**
     * Translated "Collaborators".
     *
     * @return translated "Collaborators"
     */
    @DefaultMessage("Collaborators")
    @Key("collaborators")
    String collaborators();

    /**
     * Translated "Comments".
     *
     * @return translated "Comments"
     */
    @DefaultMessage("Comments")
    @Key("comments")
    String comments();

    /**
     * Translated "Confirm".
     *
     * @return translated "Confirm"
     */
    @DefaultMessage("Confirm")
    @Key("confirmAction")
    String confirmAction();

    /**
     * Translated "Do you want to delete the app \"{0}\"?".
     *
     * @return translated "Do you want to delete the app \"{0}\"?"
     */
    @DefaultMessage("Do you want to delete the app \"{0}\"?")
    @Key("confirmDeleteApp")
    String confirmDeleteApp(String arg0);

    /**
     * Translated
     * "You have selected a non-empty folder. This may cause files in the folder to be overwritten. Continue?"
     * .
     *
     * @return translated
     *         "You have selected a non-empty folder. This may cause files in the folder to be overwritten. Continue?"
     */
    @DefaultMessage("You have selected a non-empty folder. This may cause files in the folder to be overwritten. Continue?")
    @Key("confirmOutputFolder")
    String confirmOutputFolder();

    /**
     * Translated "Contact Information".
     *
     * @return translated "Contact Information"
     */
    @DefaultMessage("Contact Information")
    @Key("contactTab")
    String contactTab();

    /**
     * Translated "Copy".
     *
     * @return translated "Copy"
     */
    @DefaultMessage("Copy")
    @Key("copy")
    String copy();

    /**
     * Translated "Copy / Paste".
     *
     * @return translated "Copy / Paste"
     */
    @DefaultMessage("Hit ctrl-c or cmd-c to copy.")
    @Key("copyPasteInstructions")
    String copyPasteInstructions();

    /**
     * Translated "Create".
     *
     * @return translated "Create"
     */
    @DefaultMessage("Create")
    @Key("create")
    String create();

    /**
     * Translated "Enter barcodes used in library:".
     *
     * @return translated "Enter barcodes used in library:"
     */
    @DefaultMessage("Enter barcodes used in library:")
    @Key("createBarcodeFileData")
    String createBarcodeFileData();

    /**
     * Translated "Create barcode file for future use:".
     *
     * @return translated "Create barcode file for future use:"
     */
    @DefaultMessage("Create barcode file for future use:")
    @Key("createBarcodeFilename")
    String createBarcodeFilename();

    /**
     * Translated "Enter 3'' adapters used in library:".
     *
     * @return translated "Enter 3'' adapters used in library:"
     */
    @DefaultMessage("Enter 3'' adapters used in library:")
    @Key("createClipperFileData")
    String createClipperFileData();

    /**
     * Translated "Create 3'' adapter file for future use:".
     *
     * @return translated "Create 3'' adapter file for future use:"
     */
    @DefaultMessage("Create 3'' adapter file for future use:")
    @Key("createClipperFilename")
    String createClipperFilename();

    /**
     * Translated "New App".
     *
     * @return translated "New App"
     */
    @DefaultMessage("New App")
    @Key("createNewAnalysis")
    String createNewAnalysis();

    /**
     * Translated "Automated Workflow".
     *
     * @return translated "Automated Workflow"
     */
    @DefaultMessage("Automated Workflow")
    @Key("createNewWorkflow")
    String createNewWorkflow();

    /**
     * Translated "Created Date".
     *
     * @return translated "Created Date"
     */
    @DefaultMessage("Created Date")
    @Key("createdDateGridHeader")
    String createdDateGridHeader();

    /**
     * Translated "show current collaborators".
     *
     * @return translated "show current collaborators"
     */
    @DefaultMessage("show current collaborators")
    @Key("currentCollabList")
    String currentCollabList();

    /**
     * Translated "Data".
     *
     * @return translated "Data"
     */
    @DefaultMessage("Data")
    @Key("data")
    String data();

    /**
     * Translated "Data: {0}".
     * 
     * @return translated "Data: {0}"
     */
    @DefaultMessage("Data: {0}")
    @Key("dataWindowTitle")
    String dataWindowTitle(String folderName);

    /**
     * Translated "Date".
     *
     * @return translated "Date"
     */
    @DefaultMessage("Date")
    @Key("date")
    String date();

    /**
     * Translated "Date Submitted".
     *
     * @return translated "Date Submitted"
     */
    @DefaultMessage("Date Submitted")
    @Key("dateSubmitted")
    String dateSubmitted();

    /**
     * Translated
     * "Retain Inputs? Enabling this flag will copy all the input files into the analysis result folder."
     * .
     *
     * @return translated
     *         "Retain Inputs? Enabling this flag will copy all the input files into the analysis result folder."
     */
    @DefaultMessage("Retain Inputs? Enabling this flag will copy all the input files into the analysis result folder.")
    @Key("debug")
    String debug();

    /**
     * Translated "
     * <p>
     * This will be the default location where all outputs from your analyses can be found.
     * </p>
     * <br/>
     * <p>
     * You can keep the default path or click Browse to select a new location.
     * </p>
     * ".
     *
     * @return translated "
     *         <p>
     *         This will be the default location where all outputs from your analyses can be found.
     *         </p>
     * <br/>
     *         <p>
     *         You can keep the default path or click Browse to select a new location.
     *         </p>
     *         "
     */
    @DefaultMessage("<p>This will be the default location where all outputs from your analyses can be found. </p><br/><p> You can keep the default path or click Browse to select a new location.</p>")
    @Key("defaultOutputFolderHelp")
    String defaultOutputFolderHelp();

    /**
     * Translated "Delete".
     *
     * @return translated "Delete"
     */
    @DefaultMessage("Delete")
    @Key("delete")
    String delete();

    /**
     * Translated "Delete All".
     *
     * @return translated "Delete All"
     */
    @DefaultMessage("Delete All")
    @Key("deleteAll")
    String deleteAll();

    /**
     * Translated "Delete Selected".
     *
     * @return translated "Delete Selected"
     */
    @DefaultMessage("Delete Selected")
    @Key("deleteSelected")
    String deleteSelected();

    /**
     * Translated "Deleted".
     *
     * @return translated "Deleted"
     */
    @DefaultMessage("Deleted")
    @Key("deleted")
    String deleted();

    /**
     * Translated "Description".
     *
     * @return translated "Description"
     */
    @DefaultMessage("Description")
    @Key("description")
    String description();

    /**
     * Translated "Details".
     *
     * @return translated "Details"
     */
    @DefaultMessage("Details")
    @Key("details")
    String details();

    /**
     * Translated "Disabled".
     *
     * @return translated "Disabled"
     */
    @DefaultMessage("Disabled")
    @Key("disabled")
    String disabled();

    /**
     * Translated "Display in GUI".
     *
     * @return translated "Display in GUI"
     */
    @DefaultMessage("Display in GUI")
    @Key("displayInGui")
    String displayInGui();

    /**
     * Translated "Link to tool documentation".
     *
     * @return translated "Link to tool documentation"
     */
    @DefaultMessage("Link to tool documentation")
    @Key("docLink")
    String docLink();

    /**
     * Translated "Documentation".
     *
     * @return translated "Documentation"
     */
    @DefaultMessage("Documentation")
    @Key("documentation")
    String documentation();

    /**
     * Translated "Done".
     *
     * @return translated "Done"
     */
    @DefaultMessage("Done")
    @Key("done")
    String done();

    /**
     * Translated "Move Down".
     *
     * @return translated "Move Down"
     */
    @DefaultMessage("Move Down")
    @Key("down")
    String down();

    /**
     * Translated "Download".
     *
     * @return translated "Download"
     */
    @DefaultMessage("Download")
    @Key("download")
    String download();

    /**
     * Translated "Download Result".
     *
     * @return translated "Download Result"
     */
    @DefaultMessage("Download Result")
    @Key("downloadResult")
    String downloadResult();

    /**
     * Translated "You can drag 'n' drop file(s) from Data window".
     *
     * @return translated "You can drag 'n' drop file(s) from Data window"
     */
    @DefaultMessage("You can drag 'n' drop file(s) from Data window")
    @Key("dragAndDropPrompt")
    String dragAndDropPrompt();

    /**
     * Translated "Drag and drop Apps here to add them to the Workflow.".
     *
     * @return translated "Drag and drop Apps here to add them to the Workflow."
     */
    @DefaultMessage("Drag and drop Apps here to add them to the Workflow.")
    @Key("dragDropAppsToCreator")
    String dragDropAppsToCreator();
    
    /**
     * Translated "Edit".
     *
     * @return translated "Edit"
     */
    @DefaultMessage("Edit")
    @Key("edit")
    String edit();

    /**
     * Translated "Email".
     *
     * @return translated "Email"
     */
    @DefaultMessage("Email")
    @Key("email")
    String email();

    /**
     * Translated "Enabled".
     *
     * @return translated "Enabled"
     */
    @DefaultMessage("Enabled")
    @Key("enabled")
    String enabled();

    /**
     * Translated "Enter any additional comments".
     *
     * @return translated "Enter any additional comments"
     */
    @DefaultMessage("Enter any additional comments")
    @Key("enterAnyAdditionalComments")
    String enterAnyAdditionalComments();

    /**
     * Translated "Error".
     *
     * @return translated "Error"
     */
    @DefaultMessage("Error")
    @Key("error")
    String error();

    /**
     * Translated "Exclude Argument option when field is empty".
     *
     * @return translated "Exclude Argument option when field is empty"
     */
    @DefaultMessage("Exclude Argument option when field is empty")
    @Key("excludeArgumentOption")
    String excludeArgumentOption();

    /**
     * Translated "Click to execute this app.".
     *
     * @return translated "Click to execute this app."
     */
    @DefaultMessage("Click to execute this app.")
    @Key("executeThisAnalysis")
    String executeThisAnalysis();

    /**
     * A message telling what day something will expire.
     * 
     * @param expirationDate the date of expiration formatted for the current locale
     */
    @DefaultMessage("This message will expire on {0}.")
    @Key("expirationMessage")
    String expirationMessage(String expirationDate);

    @Key("feedback")
    String feedback();

    @Key("feedbackTitle")
    String feedbackTitle();

    /**
     * Translated "This field is required".
     *
     * @return translated "This field is required"
     */
    @DefaultMessage("This field is required")
    @Key("fieldRequiredLabel")
    String fieldRequiredLabel();

    /**
     * Translated "Description".
     *
     * @return translated "Description"
     */
    @DefaultMessage("Description")
    @Key("fileDescription")
    String fileDescription();

    /**
     * Translated
     * "Only 8K of the selected file is displayed. For full view of data, please download file.".
     *
     * @return translated
     *         "Only 8K of the selected file is displayed. For full view of data, please download file."
     */
    @DefaultMessage("Only 8K of the selected file is displayed. For full view of data, please download file.")
    @Key("filePreviewNotice")
    String filePreviewNotice();

    /**
     * Translated "File Upload".
     *
     * @return translated "File Upload"
     */
    @DefaultMessage("File Upload")
    @Key("fileUpload")
    String fileUpload();

    /**
     * Translated "Uploading to {0}.".
     *
     * @return translated "Uploading to {0}."
     */
    @DefaultMessage("Uploading to {0}.")
    @Key("fileUploadFolder")
    String fileUploadFolder(String arg0);

    /**
     * Translated "{0} uploaded successfully.".
     *
     * @return translated "{0} uploaded successfully."
     */
    @DefaultMessage("{0} uploaded successfully.")
    @Key("fileUploadSuccess")
    String fileUploadSuccess(String arg0);

    /**
     * Translated "Help".
     *
     * @return translated "Help"
     */
    @DefaultMessage("Help")
    @Key("help")
    String help();

    /**
     * Translated "Hide".
     *
     * @return translated "Hide"
     */
    @DefaultMessage("Hide")
    @Key("hide")
    String hide();

    /**
     * Translated
     * "Are you sure you want to close this window? Closing may interrupt any transfers in progress.".
     *
     * @return translated
     *         "Are you sure you want to close this window? Closing may interrupt any transfers in progress."
     */
    @DefaultMessage("Are you sure you want to close this window? Closing may interrupt any transfers in progress.")
    @Key("transferCloseConfirmMessage")
    String transferCloseConfirmMessage();

    /**
     * Translated "Image".
     *
     * @return translated "Image"
     */
    @DefaultMessage("Image")
    @Key("image")
    String image();

    /**
     * Translated "Select Barcode File".
     *
     * @return translated "Select Barcode File"
     */
    @DefaultMessage("Select Barcode File")
    @Key("importBarcode")
    String importBarcode();

    /**
     * Translated "Select 3'' Adapter File".
     *
     * @return translated "Select 3'' Adapter File"
     */
    @DefaultMessage("Select 3'' Adapter File")
    @Key("importClipper")
    String importClipper();

    String indexFileMissing();

    /**
     * Translated "This field requires a valid url".
     *
     * @return translated "This field requires a valid url"
     */
    @DefaultMessage("This field requires a valid url")
    @Key("inValidUrl")
    String inValidUrl();

    /**
     * Translated
     * "Currently, only linear workflows may be created. At least one input for each app must come from an output of a previous app."
     * .
     *
     * @return translated
     *         "Currently, only linear workflows may be created. At least one input for each app must come from an output of a previous app."
     */
    @DefaultMessage("Currently, only linear workflows may be created. At least one input for each app must come from an output of a previous app.")
    @Key("infoPnlTip")
    String infoPnlTip();

    /**
     * Translated "Information".
     *
     * @return translated "Information"
     */
    @DefaultMessage("Information")
    @Key("information")
    String information();

    /**
     * Translated "Input Label".
     *
     * @return translated "Input Label"
     */
    @DefaultMessage("Input Label")
    @Key("inputLabel")
    String inputLabel();

    /**
     * Translated "At least one input for each app must come from an output of a previous app.".
     *
     * @return translated "At least one input for each app must come from an output of a previous app."
     */
    @DefaultMessage("At least one input for each app must come from an output of a previous app.")
    @Key("inputsOutputsPnlTip")
    String inputsOutputsPnlTip();

    /**
     * Translated "Institution".
     *
     * @return translated "Institution"
     */
    @DefaultMessage("Institution")
    @Key("institution")
    String institution();

    /**
     * Translated "Integrated Tools".
     *
     * @return translated "Integrated Tools"
     */
    @DefaultMessage("Integrated Tools")
    @Key("integratedTools")
    String integratedTools();

    /**
     * Translated "Integrator".
     *
     * @return translated "Integrator"
     */
    @DefaultMessage("Integrator")
    @Key("integrator")
    String integrator();

    /**
     * Translated "Integrator email".
     *
     * @return translated "Integrator email"
     */
    @DefaultMessage("Integrator email")
    @Key("integratorEmail")
    String integratorEmail();

    /**
     * Translated "Integrator name".
     *
     * @return translated "Integrator name"
     */
    @DefaultMessage("Integrator name")
    @Key("integratorName")
    String integratorName();
    
    /**
     * Translated
     * "A valid URL must begin with either ftp or http or https and cannot end with a space or a /. It must contain a valid path to a file after the domain name."
     * .
     *
     * @return translated
     *         "A valid URL must begin with either ftp or http or https and cannot end with a space or a /. It must contain a valid path to a file after the domain name."
     */
    @DefaultMessage("A valid URL must begin with either ftp or http or https and cannot end with a space or a /. It must contain a valid path to a file after the domain name.")
    @Key("invalidImportUrl")
    String invalidImportUrl();

    /**
     * Translated "Invalid reference genonme path.".
     *
     * @return translated "Invalid reference genonme path."
     */
    @DefaultMessage("Invalid reference genonme path.")
    @Key("invalidPath")
    String invalidPath();

    /**
     * Translated "Is the tool multi-threaded".
     *
     * @return translated "Is the tool multi-threaded"
     */
    @DefaultMessage("Is the tool multi-threaded")
    @Key("isMultiThreaded")
    String isMultiThreaded();

    /**
     * Translated "You do not have Java enabled!".
     *
     * @return translated "You do not have Java enabled!"
     */
    @DefaultMessage("You do not have Java enabled!")
    @Key("javaError")
    String javaError();

    /**
     * Translated "Last: {0}".
     *
     * @return translated "Last: {0}"
     */
    @DefaultMessage("Last: {0}")
    @Key("lastApp")
    String lastApp(String arg0);

    /**
     * Translated "N/A".
     *
     * @return translated "N/A"
     */
    @DefaultMessage("N/A")
    @Key("lastAppNotDefined")
    String lastAppNotDefined();

    /**
     * Translated "Last Modified On".
     *
     * @return translated "Last Modified On"
     */
    @DefaultMessage("Last Modified On")
    @Key("lastModOn")
    String lastModOn();

    /**
     * Translated "Last Modified".
     *
     * @return translated "Last Modified"
     */
    @DefaultMessage("Last Modified")
    @Key("lastModified")
    String lastModified();

    /**
     * Translated "Launch Analysis".
     *
     * @return translated "Launch Analysis"
     */
    @DefaultMessage("Launch Analysis")
    @Key("launchAnalysis")
    String launchAnalysis();

    /**
     * Translated "Analysis '{'0'}' successfully launched.".
     *
     * @return translated "Analysis '{'0'}' successfully launched."
     */
    @DefaultMessage("Analysis '{'0'}' successfully launched.")
    @Key("launchSuccess")
    String launchSuccess();

    /**
     * Translated "Launching analysis...".
     *
     * @return translated "Launching analysis..."
     */
    @DefaultMessage("Launching analysis...")
    @Key("launchingAnalysis")
    String launchingAnalysis();

    /**
     * Translated "Works".
     *
     * @return translated "Works"
     */
    @DefaultMessage("Works")
    @Key("likedIt")
    String likedIt();

    /**
     * Translated "Link".
     *
     * @return translated "Link"
     */
    @DefaultMessage("Link")
    @Key("link")
    String link();

    /**
     * Translated "linkToFolder"
     * 
     * @return
     */
    @DefaultMessage("Link to folder")
    String linkToFolder();

    /**
     * Translated "Loading...".
     *
     * @return translated "Loading..."
     */
    @DefaultMessage("Loading...")
    @Key("loadingMask")
    String loadingMask();

    /**
     * Translated "Logout".
     *
     * @return translated "Logout"
     */
    @DefaultMessage("Logout")
    @Key("logout")
    String logout();

    /**
     * Translated
     * "Please click <a href=''{0}''>here</a> if not redirected to the logout page within a few seconds."
     * .
     *
     * @return translated
     *         "Please click <a href=''{0}''>here</a> if not redirected to the logout page within a few seconds."
     */
    @DefaultMessage("Please click <a href=''{0}''>here</a> if not redirected to the logout page within a few seconds.")
    @Key("logoutMessageText")
    String logoutMessageText(String arg0);

    /**
     * Translated
     * "You will be logged out from all the active sessions of Discovery Environment and Tool Integration."
     * .
     *
     * @return translated
     *         "You will be logged out from all the active sessions of Discovery Environment and Tool Integration."
     */
    @DefaultMessage("You will be logged out from all the active sessions of Discovery Environment and Tool Integration.")
    @Key("logoutToolTipText")
    String logoutToolTipText();

    /**
     * Translated "Major:".
     *
     * @return translated "Major:"
     */
    @DefaultMessage("Major:")
    @Key("majorVersion")
    String majorVersion();

    /**
     * Translated "Submit for public use".
     *
     * @return translated "Submit for public use"
     */
    @DefaultMessage("Submit for public use")
    @Key("makePublic")
    String makePublic();

    /**
     * Translated "App has been made public.".
     *
     * @return translated "App has been made public."
     */
    @DefaultMessage("App has been made public.")
    @Key("makePublicSuccessTitle")
    String makePublicSuccessTitle();

    /**
     * Translated "Manage Data".
     *
     * @return translated "Manage Data"
     */
    @DefaultMessage("Manage Data")
    @Key("manageData")
    String manageData();

    /**
     * Translated "Map Outputs to Inputs".
     *
     * @return translated "Map Outputs to Inputs"
     */
    @DefaultMessage("Map Outputs to Inputs")
    @Key("mapOutputsToInputs")
    String mapOutputsToInputs();

    /**
     * Translated "Mark as favorite".
     *
     * @return translated "Mark as favorite"
     */
    @DefaultMessage("Mark as favorite")
    @Key("markFav")
    String markFav();

    /**
     * Translated "Maximize".
     *
     * @return translated "Maximize"
     */
    @DefaultMessage("Maximize")
    @Key("maximize")
    String maximize();

    /**
     * The system message arrival announcement message.
     *
     * @return the message
     */
    @DefaultMessage("You have an important message.")
    @Key("messageArrivalAnnouncement")
    String messageArrivalAnnouncement();

    /**
     * The query asking the user if he really wants to dismiss the currently selected message.
     */
    @DefaultMessage("Do you really want to dismiss this message?")
    @Key("messageDismissQuery")
    String messageDismissQuery();

    /**
     * A message letting the user know a message is currently being dismissed.
     */
    @DefaultMessage("dismissing message")
    @Key("messageDismissing")
    String messageDismissing();

    /**
     * Translated "Messages".
     * 
     * @return translated "Messages"
     */
    @DefaultMessage("Messages")
    @Key("messagesGridHeader")
    String messagesGridHeader();

    /**
     * Translated "Minimize".
     * 
     * @return translated "Minimize"
     */
    @DefaultMessage("Minimize")
    @Key("minimize")
    String minimize();

    /**
     * Translated "Minor:".
     *
     * @return translated "Minor:"
     */
    @DefaultMessage("Minor:")
    @Key("minorVersion")
    String minorVersion();

    /**
     * Translated "More Actions".
     *
     * @return translated "More Actions"
     */
    @DefaultMessage("More Actions")
    @Key("moreActions")
    String moreActions();
    
    /**
     * Translated "Move down".
     *
     * @return translated "Move down"
     */
    @DefaultMessage("Move down")
    @Key("moveDown")
    String moveDown();

    /**
     * Translated "Move Up".
     *
     * @return translated "Move Up"
     */
    @DefaultMessage("Move Up")
    @Key("moveUp")
    String moveUp();

    /**
     * Translated "My Collaborators".
     *
     * @return translated "My Collaborators"
     */
    @DefaultMessage("My Collaborators")
    @Key("myCollaborators")
    String myCollaborators();

    /**
     * Translated "View Notifications".
     *
     * @return translated "View Notifications"
     */
    @DefaultMessage("View Notifications")
    @Key("myNotifications")
    String myNotifications();

    /**
     * Translated "{0} bytes".
     *
     * @return translated "{0} bytes"
     */
    @DefaultMessage("{0} bytes")
    @Key("nBytes")
    String nBytes(String arg0);

    /**
     * Translated "{0} GB".
     *
     * @return translated "{0} GB"
     */
    @DefaultMessage("{0} GB")
    @Key("nGigabytes")
    String nGigabytes(String arg0);

    /**
     * Translated "{0} KB".
     *
     * @return translated "{0} KB"
     */
    @DefaultMessage("{0} KB")
    @Key("nKilobytes")
    String nKilobytes(String arg0);

    /**
     * Translated "{0} MB".
     *
     * @return translated "{0} MB"
     */
    @DefaultMessage("{0} MB")
    @Key("nMegabytes")
    String nMegabytes(String arg0);

    /**
     * Translated "Name".
     *
     * @return translated "Name"
     */
    @DefaultMessage("Name")
    @Key("name")
    String name();

    /**
     * Translated
     * "Letters, numbers, hyphens (-), underscores (_), and periods (.) are allowed. Spaces may be used if not at the beginning or end of the file name."
     * .
     *
     * @return translated
     *         "Letters, numbers, hyphens (-), underscores (_), and periods (.) are allowed. Spaces may be used if not at the beginning or end of the file name."
     */
    @DefaultMessage("Letters, numbers, hyphens (-), underscores (_), and periods (.) are allowed. Spaces may be used if not at the beginning or end of the file name.")
    @Key("nameValidationMsg")
    String nameValidationMsg();

    /**
     * Translated
     * "You are about to navigate away from this page. Unsaved changes will be lost. Do you wish to continue?"
     * .
     *
     * @return translated
     *         "You are about to navigate away from this page. Unsaved changes will be lost. Do you wish to continue?"
     */
    @DefaultMessage("You are about to navigate away from this page. Unsaved changes will be lost. Do you wish to continue?")
    @Key("navigateWarning")
    String navigateWarning();

    /**
     * Translated "No".
     *
     * @return translated "No"
     */
    @DefaultMessage("No")
    @Key("negativeResponse")
    String negativeResponse();

    /**
     * Translated "Create Barcode File".
     * 
     * @return translated "Create Barcode File"
     */
    @DefaultMessage("Create Barcode File")
    @Key("newBarcode")
    String newBarcode();

    /**
     * Translated "Create 3'' Adapter File".
     *
     * @return translated "Create 3'' Adapter File"
     */
    @DefaultMessage("Create 3'' Adapter File")
    @Key("newClipper")
    String newClipper();

    /**
     * Translated "New Tool Request".
     * 
     * @return translated "New Tool Request"
     */
    @DefaultMessage("New Tool Request")
    @Key("newToolReq")
    String newToolReq();

    /**
     * Translated
     * "An error occurred while processing your request. Please contact iPlant support at support@iplantcollaborative.org."
     * .
     *
     * @return translated
     *         "An error occurred while processing your request. Please contact iPlant support at support@iplantcollaborative.org."
     */
    @DefaultMessage("An error occurred while processing your request. Please contact iPlant support at support@iplantcollaborative.org.")
    @Key("newToolRequestError")
    String newToolRequestError();

    /**
     * Translated "Select Apps for workflow construction".
     *
     * @return translated "Select Apps for workflow construction"
     */
    @DefaultMessage("Select Apps for workflow construction")
    @Key("noApps")
    String noApps();

    /**
     * Translated "search and add new collaborators".
     *
     * @return translated "search and add new collaborators"
     */
    @DefaultMessage("search and add new collaborators")
    @Key("noCollaborators")
    String noCollaborators();

    /**
     * Translated "No results found.".
     *
     * @return translated "No results found."
     */
    @DefaultMessage("No results found.")
    @Key("noCollaboratorsSearchResult")
    String noCollaboratorsSearchResult();

    /**
     * Translated "No files to display.".
     *
     * @return translated "No files to display."
     */
    @DefaultMessage("No files to display.")
    @Key("noFiles")
    String noFiles();

    /**
     * Translated "No notifications to display.".
     *
     * @return translated "No notifications to display."
     */
    @DefaultMessage("No notifications to display.")
    @Key("noNotifications")
    String noNotifications();

    /**
     * msg to show when there are no results to display
     *
     * @return
     */
    String noSearchResults(String searchTerm);

	/**
	 * The system messages window message indicating that the user has no system messages to view.
	 *
	 * @return the message
	 */
	@DefaultMessage("You have no new messages.")
	@Key("noSystemMessages")
	String noSystemMessages();

    /**
     * Translated "must be a valid iPlant Wiki Documentation URL".
     *
     * @return translated "must be a valid iPlant Wiki Documentation URL"
     */
    @DefaultMessage("must be a valid iPlant Wiki Documentation URL")
    @Key("notValidAppWikiUrl")
    String notValidAppWikiUrl();

    /**
     * Translated "Notifications".
     *
     * @return translated "Notifications"
     */
    @DefaultMessage("Notifications")
    @Key("notifications")
    String notifications();

    /**
     * Translated "
     * <p>
     * This option will send you an email when the status of your analysis changes (running, completed,
     * failed, etc.).
     * </p>
     * <p>
     * It will be sent to the email address you used to register for your iPlant account.
     * </p>
     * <br/>
     * <p>
     * This option is helpful if you would like to track your analysis status while outside of the
     * Discovery Environment.
     * </p>
     * ".
     *
     * @return translated "
     *         <p>
     *         This option will send you an email when the status of your analysis changes (running,
     *         completed, failed, etc.).
     *         </p>
     *         <p>
     *         It will be sent to the email address you used to register for your iPlant account.
     *         </p>
     * <br/>
     *         <p>
     *         This option is helpful if you would like to track your analysis status while outside of
     *         the Discovery Environment.
     *         </p>
     *         "
     */
    @DefaultMessage("<p>This option will send you an email when the status of your analysis changes (running, completed, failed, etc.).</p> <p> It will be sent to the email address you used to register for your iPlant account.</p> <br/><p> This option is helpful if you would like to track your analysis status while outside of the Discovery Environment.</p>")
    @Key("notifyemailHelp")
    String notifyemailHelp();

    /**
     * Translated
     * "The iPlant Collaborative is funded by a grant from the National Science Foundation Plant Science Cyberinfrastructure Collaborative (#DBI-0735191)."
     * .
     *
     * @return translated
     *         "The iPlant Collaborative is funded by a grant from the National Science Foundation Plant Science Cyberinfrastructure Collaborative (#DBI-0735191)."
     */
    @DefaultMessage("The iPlant Collaborative is funded by a grant from the National Science Foundation Plant Science Cyberinfrastructure Collaborative (#DBI-0735191).")
    @Key("nsfProjectText")
    SafeHtml nsfProjectText();

    /**
     * The open system messages window link text
     *
     * @return the text
     */
    @DefaultMessage("Read it.")
    @Key("openMessage")
    String openMessage();

    /**
     * Translated "Other Information".
     *
     * @return translated "Other Information"
     */
    @DefaultMessage("Other Information")
    @Key("otherTab")
    String otherTab();

    /**
     * Translated "Other Information".
     *
     * @return translated "Other Information"
     */
    @DefaultMessage("Enter any other Information")
    @Key("otherInfo")
    String otherInfo();

    /**
     * Translated "Other data".
     *
     * @return translated "Other data"
     */
    @DefaultMessage("Upload any supplemental data")
    @Key("otherData")
    String otherData();

    /**
     * Translated "Output(s)".
     *
     * @return translated "Output(s)"
     */
    @DefaultMessage("Output(s)")
    @Key("outputs")
    String outputs();

    /**
     * Translated "own".
     *
     * @return translated "own"
     */
    @DefaultMessage("own")
    @Key("own")
    String own();

    /**
     * Translated "Owner".
     *
     * @return translated "Owner"
     */
    @DefaultMessage("Owner")
    @Key("owner")
    String owner();

    /**
     * Translated "Value".
     *
     * @return translated "Value"
     */
    @DefaultMessage("Value")
    @Key("paramValue")
    String paramValue();

    /**
     * Translated "Path".
     *
     * @return translated "Path"
     */
    @DefaultMessage("Path")
    @Key("path")
    String path();

    /**
     * 
     * Tranlated "You must make a selection you own or have write permissions to."
     * 
     * @return translated "You must make a selection you own or have write permissions to."
     */
    @DefaultMessage("You must make a selection you own or have write permissions to.")
    @Key("permissionSelectErrorMessage")
    String permissionSelectErrorMessage();
    
    /**
     * Translated "Phone".
     *
     * @return translated "Phone"
     */
    @DefaultMessage("Phone")
    @Key("phone")
    String phone();

    /**
     * Translated "Automate Workflow".
     *
     * @return translated "Automate Workflow"
     */
    @DefaultMessage("Automate Workflow")
    @Key("pipeline")
    String pipeline();

    /**
     * Translated "Description".
     *
     * @return translated "Description"
     */
    @DefaultMessage("Description")
    @Key("pipelineDescription")
    String pipelineDescription();

    /**
     * Translated "Name".
     *
     * @return translated "Name"
     */
    @DefaultMessage("Name")
    @Key("pipelineName")
    String pipelineName();

    /**
     * Translated "Pop-up Warning".
     *
     * @return translated "Pop-up Warning"
     */
    @DefaultMessage("Pop-up Warning")
    @Key("popUpWarning")
    String popUpWarning();

    /**
     * Translated
     * "You have your pop-up blocker enabled. Please click the Ok button to complete your action. Please disable your pop-up blocker in the future."
     * .
     *
     * @return translated
     *         "You have your pop-up blocker enabled. Please click the Ok button to complete your action. Please disable your pop-up blocker in the future."
     */
    @DefaultMessage("You have your pop-up blocker enabled. Please click the Ok button to complete your action. Please disable your pop-up blocker in the future.")
    @Key("popWarningMsg")
    String popWarningMsg();

    /**
     * Translated "Preview".
     *
     * @return translated "Preview"
     */
    @DefaultMessage("Preview")
    @Key("preview")
    String preview();

    /**
     * Translated "Progress".
     *
     * @return translated "Progress"
     */
    @DefaultMessage("Progress")
    @Key("progress")
    String progress();

    /**
     * Translated "&copy;2012 iPlant Collaborative".
     *
     * @return translated "&copy;2012 iPlant Collaborative"
     */
    @DefaultMessage("&copy;2012 iPlant Collaborative")
    @Key("projectCopyrightStatement")
    SafeHtml projectCopyrightStatement();

    /**
     * Translated "Boolean".
     *
     * @return translated "Boolean"
     */
    @DefaultMessage("Boolean")
    @Key("propertyCategoryBoolean")
    String propertyCategoryBoolean();

    /**
     * Translated "Environment Variable".
     *
     * @return translated "Environment Variable"
     */
    @DefaultMessage("Environment Variable")
    @Key("propertyCategoryEnvironmentVariable")
    String propertyCategoryEnvironmentVariable();

    /**
     * Translated "Input".
     *
     * @return translated "Input"
     */
    @DefaultMessage("Input")
    @Key("propertyCategoryInput")
    String propertyCategoryInput();

    /**
     * Translated "Number".
     *
     * @return translated "Number"
     */
    @DefaultMessage("Number")
    @Key("propertyCategoryNumber")
    String propertyCategoryNumber();

    /**
     * Translated "Output".
     *
     * @return translated "Output"
     */
    @DefaultMessage("Output")
    @Key("propertyCategoryOutput")
    String propertyCategoryOutput();

    /**
     * Translated "String".
     *
     * @return translated "String"
     */
    @DefaultMessage("String")
    @Key("propertyCategoryString")
    String propertyCategoryString();

    /**
     * Translated "Your workflow has been successfully published into your workspace.".
     *
     * @return translated "Your workflow has been successfully published into your workspace."
     */
    @DefaultMessage("Your workflow has been successfully published into your workspace.")
    @Key("publishWorkflowSuccess")
    String publishWorkflowSuccess();

    /**
     * Translated "Click Add to attach an item.".
     *
     * @return translated "Click Add to attach an item."
     */
    @DefaultMessage("Click Add to attach an item.")
    @Key("publicAttachBox")
    String publicAttachBox();

    /**
     * Translated "For example, you may want to include a link to a paper which referenced your app.".
     *
     * @return translated
     *         "For example, you may want to include a link to a paper which referenced your app."
     */
    @DefaultMessage("For example, you may want to include a link to a paper which referenced your app.")
    @Key("publicAttachTip")
    String publicAttachTip();

    /**
     * Translated "Click Browse to explore categories.".
     *
     * @return translated "Click Browse to explore categories."
     */
    @DefaultMessage("Click Browse to explore categories.")
    @Key("publicCategoriesBox")
    String publicCategoriesBox();

    /**
     * Translated
     * "Select the possible categories under which your app may be displayed in the apps list within the Apps window."
     * .
     *
     * @return translated
     *         "Select the possible categories under which your app may be displayed in the apps list within the Apps window."
     */
    @DefaultMessage("Select the possible categories under which your app may be displayed in the apps list within the Apps window.")
    @Key("publicCategoriesTip")
    String publicCategoriesTip();

    /**
     * Translated "Please complete required fields to submit.".
     *
     * @return translated "Please complete required fields to submit."
     */
    @DefaultMessage("Please complete all required fields.")
    @Key("completeRequiredFieldsError")
    String completeRequiredFieldsError();

    /**
     * Translated "Quick Tips".
     *
     * @return translated "Quick Tips"
     */
    @DefaultMessage("Quick Tips")
    @Key("quickTipsHeading")
    String quickTipsHeading();

    /**
     * Translated "Rating".
     *
     * @return translated "Rating"
     */
    @DefaultMessage("Rating")
    @Key("rating")
    String rating();

    /**
     * Translated "Your app {0}".
     *
     * @return translated "Your app {0}"
     */
    @DefaultMessage("Your app {0}")
    @Key("ratingEmailSubject")
    String ratingEmailSubject(String arg0);

    /**
     * Translated "Your Discovery Environment app {0} has received a rating by a user.".
     *
     * @return translated "Your Discovery Environment app {0} has received a rating by a user."
     */
    @DefaultMessage("Your Discovery Environment app {0} has received a rating by a user.")
    @Key("ratingEmailText")
    String ratingEmailText(String arg0);

    /**
     * Translated "out of 5".
     *
     * @return translated "out of 5"
     */
    @DefaultMessage("out of 5")
    @Key("ratingOutOfTotal")
    String ratingOutOfTotal();

    /**
     * Translated "Raw".
     *
     * @return translated "Raw"
     */
    @DefaultMessage("Raw")
    @Key("raw")
    String raw();

    /**
     * Translated "read".
     *
     * @return translated "read"
     */
    @DefaultMessage("read")
    @Key("read")
    String read();

    /**
     * Translated "read only".
     *
     * @return translated "read only"
     */
    @DefaultMessage("read only")
    @Key("readOnly")
    String readOnly();

    /**
     * Translated "read-write".
     *
     * @return translated "read-write"
     */
    @DefaultMessage("read-write")
    @Key("readWrite")
    String readWrite();

    /**
     * Translated "Name".
     *
     * @return translated "Name"
     */
    @DefaultMessage("Name")
    @Key("refGenName")
    String refGenName();

    /**
     * Translated "Path".
     *
     * @return translated "Path"
     */
    @DefaultMessage("Path")
    @Key("refGenPath")
    String refGenPath();

    /**
     * Translated "Attach any relevant links or references for this app".
     *
     * @return translated "Attach any relevant links or references for this app"
     */
    @DefaultMessage("Attach any relevant links or references for this app")
    @Key("referencesLabel")
    String referencesLabel();

    /**
     * Translated "Refresh".
     *
     * @return translated "Refresh"
     */
    @DefaultMessage("Refresh")
    @Key("refresh")
    String refresh();

    /**
     * Translated "Remove from favorite apps.".
     *
     * @return translated "Remove from favorite apps."
     */
    @DefaultMessage("Remove from favorite apps.")
    @Key("remAppFromFav")
    String remAppFromFav();

    /**
     * Translated "Remove".
     *
     * @return translated "Remove"
     */
    @DefaultMessage("Remove")
    @Key("remove")
    String remove();

    /**
     *
     * @return
     */
    String removeAccess();

    /**
     * Translated "Remove from favorites".
     *
     * @return translated "Remove from favorites"
     */
    @DefaultMessage("Remove from favorites")
    @Key("removeFav")
    String removeFav();

    /**
     * Translated "Rename".
     *
     * @return translated "Rename"
     */
    @DefaultMessage("Rename")
    @Key("rename")
    String rename();

    /**
     * Translated
     * "Your request for new tool deployment has been submitted. You will receive an email confirmation."
     * .
     *
     * @return translated
     *         "Your request for new tool deployment has been submitted. You will receive an email confirmation."
     */
    @DefaultMessage("Your request for new tool deployment has been submitted. You will receive an email confirmation.")
    @Key("requestConfirmMsg")
    String requestConfirmMsg();

    /**
     * Translated "Request New Tool Installation".
     *
     * @return translated "Request New Tool Installation"
     */
    @DefaultMessage("Request New Tool Installation")
    @Key("requestNewTool")
    String requestNewTool();

    /**
     * Translated "Discovery Environment".
     *
     * @return translated "Discovery Environment"
     */
    @DefaultMessage("Discovery Environment")
    @Key("rootApplicationTitle")
    String rootApplicationTitle();

    /**
     * Translated "Save".
     *
     * @return translated "Save"
     */
    @DefaultMessage("Save")
    @Key("save")
    String save();

    /**
     * Translated "You have unsaved changes. Save now?"
     *
     * @return translated "You have unsaved changes. Save now?"
     */
    @DefaultMessage("You have unsaved changes. Save now?")
    @Key("unsavedChanges")
    String unsavedChanges();

    /**
     * Translated "Save As".
     *
     * @return translated "Save As"
     */
    @DefaultMessage("Save As")
    @Key("saveAs")
    String saveAs();

    /**
     * Translated "
     * <p>
     * Enabling the Save session option will restore your Discovery Environment desktop to exactly how
     * you left it when you last logged out.
     * </p>
     * ".
     *
     * @return translated "
     *         <p>
     *         Enabling the Save session option will restore your Discovery Environment desktop to
     *         exactly how you left it when you last logged out.
     *         </p>
     *         "
     */
    @DefaultMessage("<p>Enabling the Save session option will restore your Discovery Environment desktop to exactly how you left it when you last logged out.</p>")
    @Key("saveSessionHelp")
    String saveSessionHelp();

    /**
     * Translated "Search".
     *
     * @return translated "Search"
     */
    @DefaultMessage("Search")
    @Key("search")
    String search();

    @DefaultMessage("Search by Name")
    @Key("searchData")
    String searchData();

    /**
     * Translated "Search for users".
     * 
     * @return translated "Search for users"
     */
    @DefaultMessage("Search for users")
    @Key("searchCollab")
    String searchCollab();

    /**
     * Translated "enter 3 or more characters".
     *
     * @return translated "enter 3 or more characters"
     */
    @DefaultMessage("enter 3 or more characters")
    @Key("searchEmptyText")
    String searchEmptyText();

    /**
     * Translated "Search History".
     *
     * @return translated "Search History"
     */
    @DefaultMessage("Search History")
    @Key("searchHistory")
    String searchHistory();

    /**
     * Translated "This field requires 3 or more characters!".
     *
     * @return translated "This field requires 3 or more characters!"
     */
    @DefaultMessage("This field requires 3 or more characters!")
    @Key("searchMinChars")
    String searchMinChars();

    /**
     * search threshold msg
     *
     */
    String searchThresholdMsg(int limit);

    /**
     * Translated "Searching...".
     *
     * @return translated "Searching..."
     */
    @DefaultMessage("Searching...")
    @Key("searching")
    String searching();

    /**
     * Translated "Select a file".
     *
     * @return translated "Select a file"
     */
    @DefaultMessage("Select from existing")
    @Key("selectExsisting")
    String selectExsisting();

    /**
     * Translated "Select output folder (a sub-folder will created by default when {0} is selected)".
     *
     * @return translated
     *         "Select output folder (a sub-folder will created by default when {0} is selected)"
     */
    @DefaultMessage("Select output folder (a sub-folder will created by default when {0} is selected)")
    @Key("selectAnalysisOutputDir")
    String selectAnalysisOutputDir(String arg0);

    /**
     * Translated "Click to view output(s).".
     *
     * @return translated "Click to view output(s)."
     */
    @DefaultMessage("Click to view output(s).")
    @Key("selectAnalysisOutputs")
    String selectAnalysisOutputs();

    /**
     * Translated "Select & Order Apps".
     *
     * @return translated "Select & Order Apps"
     */
    @DefaultMessage("Select & Order Apps")
    @Key("selectAndOrderApps")
    String selectAndOrderApps();

    /**
     * Translated "Select Collaborator(s)".
     *
     * @return translated "Select Collaborator(s)"
     */
    @DefaultMessage("Select Collaborator(s)")
    @Key("selectCollabs")
    String selectCollabs();

    /**
     * Translated "Select a folder to view its contents.".
     *
     * @return translated "Select a folder to view its contents."
     */
    @DefaultMessage("Select a folder to view its contents.")
    @Key("selectFolderToViewContents")
    String selectFolderToViewContents();

    /**
     * Translated "Select Input(s)".
     *
     * @return translated "Select Input(s)"
     */
    @DefaultMessage("Select Input(s)")
    @Key("selectInputs")
    String selectInputs();

    /**
     * Translated "You must add 2 or more apps for this workflow.".
     *
     * @return translated "You must add 2 or more apps for this workflow."
     */
    @DefaultMessage("You must add 2 or more apps for this workflow.")
    @Key("selectOrderPnlTip")
    String selectOrderPnlTip();

    /**
     * Translated "Add apps to your workflow".
     *
     * @return translated "Add apps to your workflow"
     */
    @DefaultMessage("Add apps to your workflow")
    @Key("selectWindowTitle")
    String selectWindowTitle();

    /**
     * Translated "Selected file".
     *
     * @return translated "Selected file"
     */
    @DefaultMessage("Selected file")
    @Key("selectedFile")
    String selectedFile();

    /**
     * Translated "Selected resource".
     *
     * @return translated "Selected resource"
     */
    @DefaultMessage("Selected resource")
    @Key("selectedResource")
    String selectedResource();

    /**
     * Translated "Settings".
     *
     * @return translated "Settings"
     */
    @DefaultMessage("Settings")
    @Key("settings")
    String settings();

    /**
     * Translated "Share promt".
     *
     * @return translated "Share promt"
     */
    @DefaultMessage("Click Choose Collaborators or search for a user to begin sharing.")
    @Key("sharePrompt")
    String sharePrompt();

    /**
     * Translated "Show".
     *
     * @return translated "Show"
     */
    @DefaultMessage("Show")
    @Key("show")
    String show();

    /**
     * Translated "Click on the link(s) below to begin a download.".
     *
     * @return translated "Click on the link(s) below to begin a download."
     */
    @DefaultMessage("Click on the link(s) below to begin a download.")
    @Key("simpleDownloadNotice")
    String simpleDownloadNotice();

    /**
     * Translated "Source / binary".
     *
     * @return translated "Source / binary"
     */
    @DefaultMessage("Source / binary")
    @Key("srcBin")
    String srcBin();

    /**
     * Translated "Link to Source / binary".
     *
     * @return translated "Link to Source / binary"
     */
    @DefaultMessage("Link to Source / binary")
    @Key("srcLinkPrompt")
    String srcLinkPrompt();

    /**
     * Translated "Start".
     *
     * @return translated "Start"
     */
    @DefaultMessage("Start")
    @Key("start")
    String start();

    /**
     * Translated "Status".
     *
     * @return translated "Status"
     */
    @DefaultMessage("Status")
    @Key("status")
    String status();

    /**
     * Translated "Step".
     *
     * @return translated "Step"
     */
    @DefaultMessage("Step")
    @Key("step")
    String step();

    /**
     * Translated "Step {0}".
     *
     * @return translated "Step {0}"
     */
    @DefaultMessage("Step {0}")
    @Key("stepWithValue")
    String stepWithValue(int i);

    /**
     * Translated "Stop".
     *
     * @return translated "Stop"
     */
    @DefaultMessage("Stop")
    @Key("stop")
    String stop();

    /**
     * Translated "Submit".
     *
     * @return translated "Submit"
     */
    @DefaultMessage("Submit")
    @Key("submit")
    String submit();

    /**
     * Translated "Submitting your request, please wait...".
     *
     * @return translated "Submitting your request, please wait..."
     */
    @DefaultMessage("Submitting your request, please wait...")
    @Key("submitRequest")
    String submitRequest();

    /**
     * Translated "Submitting...".
     *
     * @return translated "Submitting..."
     */
    @DefaultMessage("Submitting...")
    @Key("submitting")
    String submitting();

    /**
     * Translated "Success".
     *
     * @return translated "Success"
     */
    @DefaultMessage("Success")
    @Key("success")
    String success();

    /**
     * Translated "Switch View".
     *
     * @return translated "Switch View"
     */
    @DefaultMessage("Switch View")
    @Key("swapView")
    String swapView();

    /**
     * The system messages menu item text
     *
     * @return the text
     */
    @DefaultMessage("System Messages")
    @Key("systemMessagesLabel")
    String systemMessagesLabel();

    /**
     * the name of the current day
     */
    @DefaultMessage("Today")
    @Key("today")
    String today();

    /**
     * Translated "Please include link to publication, manual, etc.".
     * 
     * @return translated "Please include link to publication, manual, etc."
     */
    @DefaultMessage("Please include link to publication, manual, etc.")
    @Key("toolAttributionEmptyText")
    String toolAttributionEmptyText();

    /**
     * Translated "Describe appropriate attribution for this tool:".
     *
     * @return translated "Describe appropriate attribution for this tool:"
     */
    @DefaultMessage("Describe appropriate attribution for this tool:")
    @Key("toolAttributionLabel")
    String toolAttributionLabel();

    /**
     * Translated "Brief description of the tool".
     *
     * @return translated "Brief description of the tool"
     */
    @DefaultMessage("Brief description of the tool")
    @Key("toolDesc")
    String toolDesc();

    /**
     * Translated "Tool name".
     *
     * @return translated "Tool name"
     */
    @DefaultMessage("Tool name")
    @Key("toolName")
    String toolName();

    /**
     * Translated "Name of tool/script".
     *
     * @return translated "Name of tool/script"
     */
    @DefaultMessage("Name of tool/script")
    @Key("toolNameLabel")
    String toolNameLabel();

    /**
     * Translated "Tool Request Status".
     *
     * @return translated "Tool Request Status"
     */
    @DefaultMessage("Request Status")
    @Key("requestStatus")
    String requestStatus();
    
    /**
     * Translated "tool Request Form Intro".
     *
     * @return translated "Tool Request form intro"
     */
    @DefaultMessage("tool Request Form Intro")
    @Key("toolRequestFormIntro")
    String toolRequestFormIntro();
    

    /**
     * Translated "Tool Information".
     *
     * @return translated "Tool Information"
     */
    @DefaultMessage("Tool Information")
    @Key("toolTab")
    String toolTab();

    /**
     * Translated "Tool Tip Text".
     *
     * @return translated "Tool Tip Text"
     */
    @DefaultMessage("Tool Tip Text")
    @Key("toolTipTextLabel")
    String toolTipTextLabel();


    /**
     * Translated "Tool version".
     *
     * @return translated "Tool version"
     */
    @DefaultMessage("Version")
    @Key("toolVersion")
    String toolVersion();

    /**
     *
     * Translated "Children".
     *
     * @return translated "Children"
     */
    @DefaultMessage("Children")
    @Key("treeSelectorCascadeChildren")
    String treeSelectorCascadeChildren();

    /**
     * Translated "None".
     *
     * @return translated "None"
     */
    @DefaultMessage("None")
    @Key("treeSelectorCascadeNone")
    String treeSelectorCascadeNone();

    /**
     * Translated "Parent".
     *
     * @return translated "Parent"
     */
    @DefaultMessage("Parent")
    @Key("treeSelectorCascadeParent")
    String treeSelectorCascadeParent();

    /**
     * Translated "Both".
     *
     * @return translated "Both"
     */
    @DefaultMessage("Both")
    @Key("treeSelectorCascadeTri")
    String treeSelectorCascadeTri();

    /**
     * Translated "Filter by name. Clear to review selection.".
     *
     * @return translated "Filter by name. Clear to review selection."
     */
    @DefaultMessage("Filter by name. Clear to review selection.")
    @Key("treeSelectorFilterEmptyText")
    String treeSelectorFilterEmptyText();

    /**
     * Translated "Delete a previous rating".
     *
     * @return translated "Delete a previous rating"
     */
    @DefaultMessage("Delete a previous rating")
    @Key("unrate")
    String unrate();

    /**
     * Translated "Move Up".
     *
     * @return translated "Move Up"
     */
    @DefaultMessage("Move Up")
    @Key("up")
    String up();

    /**
     * Translated "Select test data file to upload".
     *
     * @return translated "Select test data file to upload"
     */
    @DefaultMessage("Select test data file to upload")
    @Key("upldTestData")
    String upldTestData();

    /**
     * Translated "Upload".
     *
     * @return translated "Upload"
     */
    @DefaultMessage("Upload")
    @Key("upload")
    String upload();

    /**
     * Translated "New upload".
     *
     * @return translated "New upload"
     */
    @DefaultMessage("New upload")
    @Key("uploadNew")
    String uploadNew();
    
    
    /**
     * Translated "Uploaded".
     *
     * @return translated "Uploaded"
     */
    @DefaultMessage("Uploaded")
    @Key("uploaded")
    String uploaded();

    /**
     * Translated "User Name".
     *
     * @return translated "User Name"
     */
    @DefaultMessage("User Name")
    @Key("userId")
    String userId();

    /**
     * Translated "User preference saved successfully.".
     * 
     * @return translated "User preference saved successfully."
     */
    @DefaultMessage("User preference saved successfully.")
    @Key("userPrefSaveSuccess")
    String userPrefSaveSuccess();

    /**
     * Translated "User Provided".
     *
     * @return translated "User Provided"
     */
    @DefaultMessage("User Provided")
    @Key("userProvided")
    String userProvided();

    /**
     * Translated "Value(s)".
     *
     * @return translated "Value(s)"
     */
    @DefaultMessage("Value(s)")
    @Key("valueParenS")
    String valueParenS();

    /**
     * Translated "varies".
     *
     * @return translated "varies"
     */
    @DefaultMessage("varies")
    @Key("varies")
    String varies();

    /**
     * Translated "Version".
     *
     * @return translated "Version"
     */
    @DefaultMessage("Version")
    @Key("version")
    String version();

    /**
     * Translated "View".
     *
     * @return translated "View"
     */
    @DefaultMessage("View")
    @Key("view")
    String view();

    /**
     * Translated "View Integrated Tools".
     *
     * @return translated "View Integrated Tools"
     */
    @DefaultMessage("View Integrated Tools")
    @Key("viewDeployedComponents")
    String viewDeployedComponents();

    /**
     * Translated "View Raw".
     *
     * @return translated "View Raw"
     */
    @DefaultMessage("View Raw")
    @Key("viewRaw")
    String viewRaw();

    /**
     * Translated "View Results".
     *
     * @return translated "View Results"
     */
    @DefaultMessage("View Results")
    @Key("viewResults")
    String viewResults();

    /**
     * Translated "View Tree".
     *
     * @return translated "View Tree"
     */
    @DefaultMessage("View Tree")
    @Key("viewTreeViewer")
    String viewTreeViewer();

    /**
     * Translated "Warning".
     *
     * @return translated "Warning"
     */
    @DefaultMessage("Warning")
    @Key("warning")
    String warning();

    /**
     * Translated "Welcome".
     *
     * @return translated "Welcome"
     */
    @DefaultMessage("Welcome")
    @Key("welcome")
    String welcome();

    /**
     * Translated
     * "Enter the URL for the iPlant wiki documentation, created using <a href=\"{0}\" target=\"_blank\">these instructions</a>"
     * .
     *
     * @return translated
     *         "Enter the URL for the iPlant wiki documentation, created using <a href=\"{0}\" target=\"_blank\">these instructions</a>"
     */
    @DefaultMessage("Enter the URL for the iPlant wiki documentation, created using <a href=\"{0}\" target=\"_blank\">these instructions</a>")
    @Key("wikiUrlLabel")
    String wikiUrlLabel(String arg0);

    /**
     * Translated "Workflow Information".
     *
     * @return translated "Workflow Information"
     */
    @DefaultMessage("Workflow Information")
    @Key("workflowInfo")
    String workflowInfo();

    /**
     * Translated "write".
     *
     * @return translated "write"
     */
    @DefaultMessage("write")
    @Key("write")
    String write();

    /**
     * Translated "Pending verification that {0} exists."
     * 
     * @return translated "Pending verification that {0} exists."
     */
    @DefaultMessage("Pending verification that {0} exists.")
    @Key("diskResourceExistCheckPending")
    String diskResourceExistCheckPending(String path);

    /**
     * Translated "Add to Sidebar"
     *
     * @return translated "Add to Sidebar"
     */
    @DefaultMessage("Add to Sidebar")
    @Key("addToSideBarMenuItem")
    String addToSideBarMenuItem();

    /**
     * Translated "Send to CoGE"
     *
     * @return translated "Send to CoGE"
     */
    @DefaultMessage("Send to CoGE")
    @Key("sendToCogeMenuItem")
    String sendToCogeMenuItem();

    /**
     * Translated "Send to Ensembl"
     *
     * @return translated "Send to Ensembl"
     */
    @DefaultMessage("Send to Ensembl")
    @Key("sendToEnsemblMenuItem")
    String sendToEnsemblMenuItem();

    /**
     * Translated "Send to Tree Viewer"
     *
     * @return translated "Send to Tree Viewer"
     */
    @DefaultMessage("Send to Tree Viewer")
    @Key("sendToTreeViewerMenuItem")
    String sendToTreeViewerMenuItem();

    /**
     * Translated "Learn more."
     *
     * @return translated "Learn more."
     */
    @DefaultMessage("Learn more.")
    @Key("learnMore")
    String learnMore();

    String tags();

    String addToFavorites(String resource_name);

    String removeFromFavorites(String resource_name);

}
