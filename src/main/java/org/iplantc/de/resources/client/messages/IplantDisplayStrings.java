package org.iplantc.de.resources.client.messages;

import com.google.gwt.safehtml.shared.SafeHtml;

import java.util.List;

/**
 * Interface to represent the messages contained in resource bundle:
 * /Users/sriram/iplant/lib-workspace/ui
 * -resources-module/src/main/resources/org/iplantc/core/resources/client
 * /messages/IplantDisplayStrings.properties'.
 */
public interface IplantDisplayStrings extends com.google.gwt.i18n.client.Messages {

    /**
     * Translated "About".
     *
     * @return translated "About"
     */
    @DefaultMessage("About")
    @Key("about")
    String about();

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
     * Translated "New Category".
     *
     * @return translated "New Category"
     */
    @DefaultMessage("New Category")
    @Key("addCategoryPrompt")
    String addCategoryPrompt();

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
     * Translated "Tool Integration Administration".
     *
     * @return translated "Tool Integration Administration"
     */
    @DefaultMessage("Tool Integration Administration")
    @Key("adminApp")
    String adminApp();

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
     * Translated "Advanced Sharing".
     * 
     * @return translated "Advanced Sharing"
     */
    @DefaultMessage("Advanced Sharing")
    @Key("advancedSharing")
    String advancedSharing();

    /**
     * Translated "Yes".
     *
     * @return translated "Yes"
     */
    @DefaultMessage("Yes")
    @Key("affirmativeResponse")
    String affirmativeResponse();

    /**
     * Translated "Alert".
     *
     * @return translated "Alert"
     */
    @DefaultMessage("Alert")
    @Key("alert")
    String alert();

    /**
     * Translated "See all notifications".
     *
     * @return translated "See all notifications"
     */
    @DefaultMessage("See all notifications")
    @Key("allNotifications")
    String allNotifications();

    /**
     * Translated "Analyses".
     *
     * @return translated "Analyses"
     */
    @DefaultMessage("Analyses")
    @Key("analyses")
    String analyses();

    /**
     * Translated
     * "This will remove selected analyses. Outputs and parameters can still be viewed in the folder in Manage Data that was created by these analyses."
     * .
     *
     * @return translated
     *         "This will remove selected analyses. Outputs and parameters can still be viewed in the folder in Manage Data that was created by these analyses."
     */
    @DefaultMessage("This will remove selected analyses. Outputs and parameters can still be viewed in the folder in Manage Data that was created by these analyses.")
    @Key("analysesExecDeleteWarning")
    String analysesExecDeleteWarning();

    /**
     * Translated "Analyses that are not in completed or failed status were not deleted.".
     *
     * @return translated "Analyses that are not in completed or failed status were not deleted."
     */
    @DefaultMessage("Analyses that are not in completed or failed status were not deleted.")
    @Key("analysesNotDeleted")
    String analysesNotDeleted();

    /**
     * Translated "Unable to retrieve the list of analyses.".
     *
     * @return translated "Unable to retrieve the list of analyses."
     */
    @DefaultMessage("Unable to retrieve the list of analyses.")
    @Key("analysesRetrievalFailure")
    String analysesRetrievalFailure();

    /**
     * Translated "Enter a description for your application".
     *
     * @return translated "Enter a description for your application"
     */
    @DefaultMessage("Enter a description for your application")
    @Key("analysisDesc")
    String analysisDesc();

    /**
     * Translated "Overview".
     *
     * @return translated "Overview"
     */
    @DefaultMessage("Overview")
    @Key("analysisOverview")
    String analysisOverview();

    /**
     * Translated "Analysis {0} stopped successfully.".
     *
     * @return translated "Analysis {0} stopped successfully."
     */
    @DefaultMessage("Analysis {0} stopped successfully.")
    @Key("analysisStopSuccess")
    String analysisStopSuccess(String arg0);

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
     * A message indicating that an app was added to one or more categories.
     * 
     * @param app The name of the app that was categorized.
     * @param categories The list of category names.
     */
    @DefaultMessage("Successfully added {0} to the following Categories: {1,list}.")
    @AlternateMessage({"=1", "Successfully added {0} to the following Category: {1,list}."})
    @Key("appCategorizeSuccess")
    String appCategorizeSuccess(String app, @PluralCount List<String> categories);

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
     * Translated "This will remove the selected app from your workspace. Do you wish to continue?".
     *
     * @return translated
     *         "This will remove the selected app from your workspace. Do you wish to continue?"
     */
    @DefaultMessage("This will remove the selected app from your workspace. Do you wish to continue?")
    @Key("appDeleteWarning")
    String appDeleteWarning();

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
     * Translated "App".
     *
     * @return translated "App"
     */
    @DefaultMessage("App")
    @Key("appName")
    String appName();

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
     * Translated "Attribute".
     * 
     * @return translated "Attribute"
     */
    @DefaultMessage("Attribute")
    @Key("attribute")
    String attribute();

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
     * Translated "Browse".
     *
     * @return translated "Browse"
     */
    @DefaultMessage("Browse")
    @Key("browse")
    String browse();

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
     * Translated "Bulk Download".
     *
     * @return translated "Bulk Download"
     */
    @DefaultMessage("Bulk Download")
    @Key("bulkDownload")
    String bulkDownload();

    /**
     * Translated "Bulk Upload from Desktop".
     *
     * @return translated "Bulk Upload from Desktop"
     */
    @DefaultMessage("Bulk Upload from Desktop")
    @Key("bulkUploadFromDesktop")
    String bulkUploadFromDesktop();

    /**
     * Translated "Cancel".
     *
     * @return translated "Cancel"
     */
    @DefaultMessage("Cancel")
    @Key("cancel")
    String cancel();

    /**
     * Translated "Cancel".
     *
     * @return translated "Cancel"
     */
    @DefaultMessage("Cancel")
    @Key("cancelAnalysis")
    String cancelAnalysis();

    /**
     * Translated "Unable to load user info".
     *
     * @return translated "Unable to load user info"
     */
    @DefaultMessage("Unable to load user info")
    @Key("cantLoadUserInfo")
    String cantLoadUserInfo();

    /**
     * Translated "Categorize".
     * 
     * @return translated "Categorize"
     */
    @DefaultMessage("Categorize")
    @Key("categorize")
    String categorize();

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
     * Translated "Choose from collaborators".
     *
     * @return translated "Choose from collaborators"
     */
    @DefaultMessage("Choose from collaborators")
    @Key("chooseFromCollab")
    String chooseFromCollab();

    /**
     * Translated "Clear Selection".
     * 
     * @return translated "Clear Selection"
     */
    @DefaultMessage("Clear Selection")
    @Key("clearSelection")
    String clearSelection();

    /**
     * Translated "Click to view app info.".
     *
     * @return translated "Click to view app info."
     */
    @DefaultMessage("Click to view app info.")
    @Key("clickAppInfo")
    String clickAppInfo();

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
     * Translated "Close Active window".
     *
     * @return translated "Close Active window"
     */
    @DefaultMessage("Close Active window")
    @Key("closeActivewindow")
    String closeActivewindow();

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
     * 
     * @return
     */
    String cogeResponse(String url);
    
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

    /**
     * Translated "Your search exceeded the threshold. Please refine your search.".
     *
     * @return translated "Your search exceeded the threshold. Please refine your search."
     */
    @DefaultMessage("Your search exceeded the threshold. Please refine your search.")
    @Key("collaboratorSearchTruncated")
    String collaboratorSearchTruncated();

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
     * Text displayed to collapse all nodes in a tree.
     *
     * @return
     */
    String collapseAll();

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
     * Translated "Do you want to delete the category \"{0}\"?".
     *
     * @return translated "Do you want to delete the category \"{0}\"?"
     */
    @DefaultMessage("Do you want to delete the category \"{0}\"?")
    @Key("confirmDeleteAppGroup")
    String confirmDeleteAppGroup(String arg0);

    /**
     * Translated "Delete App".
     *
     * @return translated "Delete App"
     */
    @DefaultMessage("Delete App")
    @Key("confirmDeleteAppTitle")
    String confirmDeleteAppTitle();

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
     * Translated "Support".
     *
     * @return translated "Support"
     */
    @DefaultMessage("Support")
    @Key("contactSupport")
    String contactSupport();

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
     * Translated "Copy Link".
     * 
     * @return translated "Copy Link"
     */
    @DefaultMessage("Copy Link")
    @Key("copyLink")
    String copyLink();

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
     * Translated "Create Apps".
     *
     * @return translated "Create Apps"
     */
    @DefaultMessage("Create Apps")
    @Key("createApps")
    String createApps();

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
     * Translated "Create in {0}.".
     *
     * @return translated "Create in {0}."
     */
    @DefaultMessage("Create in {0}.")
    @Key("createIn")
    String createIn(String path);

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
     * Translated "Created By".
     *
     * @return translated "Created By"
     */
    @DefaultMessage("Created By")
    @Key("createdBy")
    String createdBy();

    /**
     * Translated "Date Submitted".
     *
     * @return translated "Date Submitted"
     */
    @DefaultMessage("Date Submitted")
    @Key("createdDate")
    String createdDate();

    /**
     * Translated "Created Date".
     *
     * @return translated "Created Date"
     */
    @DefaultMessage("Created Date")
    @Key("createdDateGridHeader")
    String createdDateGridHeader();

    /**
     * Translated "Created On".
     *
     * @return translated "Created On"
     */
    @DefaultMessage("Created On")
    @Key("createdOn")
    String createdOn();

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
     * Translated "{0} items selected".
     *
     * @return translated "{0} items selected"
     */
    @DefaultMessage("{0} items selected")
    @Key("dataDragDropStatusText")
    String dataDragDropStatusText(int i);

    /**
     * Text displayed at top of Manage Data Link window.
     *
     * @return
     */
    String dataLinkWarning();
    
    /**
     * 
     * @return
     */
    @DefaultMessage("Visualization")
    String visualization();

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
     * Translated "<b> Default analysis output folder </b>".
     *
     * @return translated "<b> Default analysis output folder </b>"
     */
    @DefaultMessage("<b> Default analysis output folder </b>")
    @Key("defaultOutputFolder")
    String defaultOutputFolder();

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
     * Tool tip text displayed when hovering over the delete button in the Manage Data Links window.
     *
     * @return
     */
    String deleteDataLinkToolTip();

    /**
     * Translated "All are deleted files and folders are available under trash.".
     *
     * @return translated "All are deleted files and folders are available under trash."
     */
    @DefaultMessage("All are deleted files and folders are available under trash.")
    @Key("deleteMsg")
    String deleteMsg();

    /**
     * Translated "Selected file(s) / folder(s) deleted successfully."
     *
     * @return translated "Selected file(s) / folder(s) deleted successfully."
     */
    @DefaultMessage("Selected file(s) / folder(s) deleted successfully.")
    @Key("deleteTrash")
    String deleteTrash();
    
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
     * Translated "Poor".
     *
     * @return translated "Poor"
     */
    @DefaultMessage("Poor")
    @Key("didNotLike")
    String didNotLike();

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
     * Translate "Duplicate shortcut key"
     * 
     * @return translated "Duplicate shortcut key"
     */
    @DefaultMessage("This keyboard shortcut is already in use. Please choose another shortcut.")
    @Key("duplicateShortCutKey")
    String duplicateShortCutKey(String key);

    /**
     * Translated "Edit".
     *
     * @return translated "Edit"
     */
    @DefaultMessage("Edit")
    @Key("edit")
    String edit();
    
    /**
     * Translated "Modify".
     * 
     * @return translated "Modify"
     */
    @DefaultMessage("Modify")
    @Key("editApp")
    String editApp();
    

    /**
     * Translated "Email".
     *
     * @return translated "Email"
     */
    @DefaultMessage("Email")
    @Key("email")
    String email();

    /**
     * Translated "Empty Trash".
     *
     * @return translated "Empty Trash"
     */
    @DefaultMessage("Empty Trash")
    @Key("emptyTrash")
    String emptyTrash();

    /**
     * Translated "This action cannot be undone. Do you wish to continue ?".
     *
     * @return translated "This action cannot be undone. Do you wish to continue ?"
     */
    @DefaultMessage("This action cannot be undone. Do you wish to continue ?")
    @Key("emptyTrashWarning")
    String emptyTrashWarning();

    /**
     * Translated "Enabled".
     *
     * @return translated "Enabled"
     */
    @DefaultMessage("Enabled")
    @Key("enabled")
    String enabled();

    /**
     * Translated "End Date".
     *
     * @return translated "End Date"
     */
    @DefaultMessage("End Date")
    @Key("endDate")
    String endDate();

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
     * Text displayed to expand all nodes in a tree.
     *
     * @return
     */
    String expandAll();

    /**
     * A message telling what day something will expire.
     * 
     * @param expirationDate the date of expiration formatted for the current locale
     */
    @DefaultMessage("This message will expire on {0}.")
    @Key("expirationMessage")
    String expirationMessage(String expirationDate);

    /**
     * Translated "Explain".
     * 
     * @return translated "Explain"
     */
    @DefaultMessage("Explain")
    @Key("explain")
    String explain();

    @Key("feedback")
    String feedback();

    @Key("feedbackTitle")
    String feedbackTitle();

    @Key("feedbackSubmitted")
    String feedbackSubmitted();

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
     * Translated "File Name".
     *
     * @return translated "File Name"
     */
    @DefaultMessage("File Name")
    @Key("fileName")
    String fileName();

    @Key("fileOpenMsg")
    @DefaultMessage("Your file was opened in separate browser tab / window. You can close this window.")
    String fileOpenMsg();
    
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
     * Translated "Maximum total file upload size is 1.9GB.".
     *
     * @return translated Maximum size of each file is 1.9GB when using simple upload."
     */
    @DefaultMessage("Maximum size of each file is 1.9GB when using simple upload.")
    @Key("fileUploadMaxSizeWarning")
    String fileUploadMaxSizeWarning();

    /**
     * Translated "{0} uploaded successfully.".
     *
     * @return translated "{0} uploaded successfully."
     */
    @DefaultMessage("{0} uploaded successfully.")
    @Key("fileUploadSuccess")
    String fileUploadSuccess(String arg0);
    
    /**
     * 
     * Translate fileViewerHeaderRow
     * @return
     */
    String fileViewerHeaderRow();
    
    /**
     * Translate fileViewerSkipLines
     * @return
     */
    String fileViewerSkipLines();

    /**
     * Translated "File(s)".
     *
     * @return translated "File(s)"
     */
    @DefaultMessage("File(s)")
    @Key("files")
    String files();

    /**
     * Translated "Filter by Name or App".
     *
     * @return translated "Filter by Name or App"
     */
    @DefaultMessage("Filter by Name or App")
    @Key("filterAnalysesList")
    String filterAnalysesList();

    /**
     * Translated "Filter by name".
     *
     * @return translated "Filter by name"
     */
    @DefaultMessage("Filter by name")
    @Key("filterDataList")
    String filterDataList();

    /**
     * Translated "Folder Name".
     *
     * @return translated "Folder Name"
     */
    @DefaultMessage("Folder Name")
    @Key("folderName")
    String folderName();

    /**
     * Translated "Folder(s)".
     *
     * @return translated "Folder(s)"
     */
    @DefaultMessage("Folder(s)")
    @Key("folders")
    String folders();

    /**
     * Translated "Forums".
     *
     * @return translated "Forums"
     */
    @DefaultMessage("Forums")
    @Key("forums")
    String forums();

    /**
     * Translated "Group by Data".
     *
     * @return translated "Group by Data"
     */
    @DefaultMessage("Group by Data")
    @Key("groupByData")
    String groupByData();

    /**
     * Translated "Group by User".
     *
     * @return translated "Group by User"
     */
    @DefaultMessage("Group by User")
    @Key("groupByUser")
    String groupByUser();

    /**
     * Translated "GWT Version:".
     *
     * @return translated "GWT Version:"
     */
    @DefaultMessage("GWT Version:")
    @Key("gwtVersion")
    String gwtVersion();

    /**
     * Translated "GXT Version:".
     *
     * @return translated "GXT Version:"
     */
    @DefaultMessage("GXT Version:")
    @Key("gxtVersion")
    String gxtVersion();

    /**
     * Translated "Not useful".
     *
     * @return translated "Not useful"
     */
    @DefaultMessage("Not useful")
    @Key("hateIt")
    String hateIt();

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
     * Translated "Host".
     *
     * @return translated "Host"
     */
    @DefaultMessage("Host")
    @Key("host")
    String host();

    /**
     * Translated "Argument, idParent, must have a valid value provided.".
     *
     * @return translated "Argument, idParent, must have a valid value provided."
     */
    @DefaultMessage("Argument, idParent, must have a valid value provided.")
    @Key("idParentInvalid")
    String idParentInvalid();

    /**
     * Translated
     * "Are you sure you want to close this window? Closing may interrupt any transfers in progress.".
     *
     * @return translated
     *         "Are you sure you want to close this window? Closing may interrupt any transfers in progress."
     */
    @DefaultMessage("Are you sure you want to close this window? Closing may interrupt any transfers in progress.")
    @Key("idropLiteCloseConfirmMessage")
    String idropLiteCloseConfirmMessage();

    /**
     * Translated "Confirm".
     *
     * @return translated "Confirm"
     */
    @DefaultMessage("Confirm")
    @Key("idropLiteCloseConfirmTitle")
    String idropLiteCloseConfirmTitle();

    /**
     * Translated
     * "You must close this window, after the current download is complete, before beginning a new download."
     * .
     *
     * @return translated
     *         "You must close this window, after the current download is complete, before beginning a new download."
     */
    @DefaultMessage("You must close this window, after the current download is complete, before beginning a new download.")
    @Key("idropLiteDownloadNotice")
    String idropLiteDownloadNotice();
    
    String idropJavaInfo(String helpUrl);

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

    /**
     * Translated "Import".
     *
     * @return translated "Import"
     */
    @DefaultMessage("Import")
    @Key("importLabel")
    String importLabel();

    /**
     * Translated "{0} is uploading and will be available shortly.".
     *
     * @return translated "{0} is uploading and will be available shortly."
     */
    @DefaultMessage("{0} is uploading and will be available shortly.")
    @Key("importRequestSubmit")
    String importRequestSubmit(String arg0);


    /**
     * Translated "Select a valid page".
     *
     * @return translated "Select a valid page"
     */
    @DefaultMessage("Select a valid page")
    @Key("inValidPage")
    String inValidPage();


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
     * Translated "Integrated by".
     *
     * @return translated "Integrated by"
     */
    @DefaultMessage("Integrated by")
    @Key("integratedby")
    String integratedby();

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
     * Translated "Introduction".
     *
     * @return translated "Introduction"
     */
    @DefaultMessage("Introduction")
    @Key("Introduction")
    String introduction();

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
     * Translated "Keyboard Shortcuts".
     *
     * @return translated "Keyboard Shortcuts"
     */
    @DefaultMessage("Keyboard Shortcuts")
    @Key("keyboadSc")
    String keyboadSc();

    /**
     * Translated "Label".
     *
     * @return translated "Label"
     */
    @DefaultMessage("Label")
    @Key("label")
    String label();

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
     * Translated "Last Modified By".
     *
     * @return translated "Last Modified By"
     */
    @DefaultMessage("Last Modified By")
    @Key("lastModBy")
    String lastModBy();

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
     * Translated "Links".
     *
     * @return translated "Links"
     */
    @DefaultMessage("Links")
    @Key("links")
    String links();
    
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
     * Translated "Loading Session".
     *
     * @return translated "Loading Session"
     */
    @DefaultMessage("Loading Session")
    @Key("loadingSession")
    String loadingSession();

    /**
     * Translated "Loading last session, please wait...".
     *
     * @return translated "Loading last session, please wait..."
     */
    @DefaultMessage("Loading last session, please wait...")
    @Key("loadingSessionWaitNotice")
    String loadingSessionWaitNotice();

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
     * Translated "Exceptional".
     *
     * @return translated "Exceptional"
     */
    @DefaultMessage("Exceptional")
    @Key("lovedIt")
    String lovedIt();

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
     * Translated "Your app could not be made public.".
     *
     * @return translated "Your app could not be made public."
     */
    @DefaultMessage("Your app could not be made public.")
    @Key("makePublicFail")
    String makePublicFail();

    /**
     * Translated
     * "Your app is now public. Please complete the <a href=\"{0}\" target=\"_blank\">documentation page</a>."
     * .
     *
     * @return translated
     *         "Your app is now public. Please complete the <a href=\"{0}\" target=\"_blank\">documentation page</a>."
     */
    @DefaultMessage("Your app is now public. Please complete the <a href=\"{0}\" target=\"_blank\">documentation page</a>.")
    @Key("makePublicSuccessMessage")
    String makePublicSuccessMessage(String arg0);

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
     * Menu text for button to Manage Data Link tickets.
     *
     * @return
     */
    String manageDataLinks();

    /**
     * Translated "Manage Sharing".
     *
     * @return translated "Manage Sharing"
     */
    @DefaultMessage("Manage Sharing")
    @Key("manageSharing")
    String manageSharing();

    /**
     * Translated "Map Outputs to Inputs".
     *
     * @return translated "Map Outputs to Inputs"
     */
    @DefaultMessage("Map Outputs to Inputs")
    @Key("mapOutputsToInputs")
    String mapOutputsToInputs();

    /**
     * Translated "Mark All As Read".
     *
     * @return translated "Mark All As Read"
     */
    @DefaultMessage("Mark All As Read")
    @Key("markAllasSeen")
    String markAllasSeen();

    /**
     * Translated "All new notifications were marked as read.".
     *
     * @return translated "All new notifications were marked as read."
     */
    @DefaultMessage("All new notifications were marked as read.")
    @Key("markAllasSeenSuccess")
    String markAllasSeenSuccess();

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
     * Translated "Metadata".
     *
     * @return translated "Metadata"
     */
    @DefaultMessage("Metadata")
    @Key("metadata")
    String metadata();
    
    /**
     * Translated "metadataSuccess".
     *
     * @return translated "metadataSuccess"
     */
    @DefaultMessage("Metadata updated successfully.")
    @Key("metadataSuccess")
    String metadataSuccess();

    /**
     * Translated "Are you sure you want to remove this template?".
     * 
     * @return translated "Are you sure you want to remove this template?"
     */
    @DefaultMessage("Are you sure you want to remove this template?")
    @Key("metadataTemplateConfirmRemove")
    String metadataTemplateConfirmRemove();

    /**
     * Translated "Remove Template".
     * 
     * @return translated "Remove Template"
     */
    @DefaultMessage("Remove Template")
    @Key("metadataTemplateRemove")
    String metadataTemplateRemove();

    /**
     * Translated "Select a template...".
     * 
     * @return translated "Select a template..."
     */
    @DefaultMessage("Select a template...")
    @Key("metadataTemplateSelect")
    String metadataTemplateSelect();

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
     * Translated "Move".
     *
     * @return translated "Move"
     */
    @DefaultMessage("Move")
    @Key("move")
    String move();
    
    

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
     * Translated "New App".
     *
     * @return translated "New App"
     */
    @DefaultMessage("New App")
    @Key("newApp")
    String newApp();

    /**
     * Translated "New Attribute".
     * 
     * @return translated "New Attribute"
     */
    @DefaultMessage("New Attribute")
    @Key("newAttribute")
    String newAttribute();

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
     * Translated "New Folder".
     *
     * @return translated "New Folder"
     */
    @DefaultMessage("New Folder")
    @Key("newFolder")
    String newFolder();

    /**
     * Translated "New Notifications".
     *
     * @return translated "New Notifications"
     */
    @DefaultMessage("New Notifications")
    @Key("newNotifications")
    String newNotifications();

    /**
     * Translated
     * "You have new notifications. Please select 'Notifications' link from top level menu to view them."
     * .
     *
     * @return translated
     *         "You have new notifications. Please select 'Notifications' link from top level menu to view them."
     */
    @DefaultMessage("You have new notifications. Please select 'Notifications' link from top level menu to view them.")
    @Key("newNotificationsAlert")
    String newNotificationsAlert();

    /**
     * Translated "New Value".
     * 
     * @return translated "New Value"
     */
    @DefaultMessage("New Value")
    @Key("newValue")
    String newValue();

    /**
     * Translated "New Tool Request".
     * 
     * @return translated "New Tool Request"
     */
    @DefaultMessage("New Tool Request")
    @Key("newToolReq")
    String newToolReq();

    /**
     * Translated "Tool Requests".
     * 
     * @return translated "Tool Requests"
     */
    @DefaultMessage("Tool Requests")
    @Key("toolRequests")
    String toolRequests();

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
     * Translated "No items to display".
     *
     * @return translated "No items to display"
     */
    @DefaultMessage("No items to display")
    @Key("noAnalyses")
    String noAnalyses();

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
     * Translated "select any one file / folder to view its details".
     *
     * @return translated "select any one file / folder to view its details"
     */
    @DefaultMessage("select any one file / folder to view its details")
    @Key("noDetails")
    String noDetails();

    /**
     * Translated "No files to display.".
     *
     * @return translated "No files to display."
     */
    @DefaultMessage("No files to display.")
    @Key("noFiles")
    String noFiles();

    /**
     * Translated "No items to display.".
     *
     * @return translated "No items to display."
     */
    @DefaultMessage("No items to display.")
    @Key("noItemsToDisplay")
    String noItemsToDisplay();

    /**
     * Translated "No new notifications!".
     *
     * @return translated "No new notifications!"
     */
    @DefaultMessage("No new notifications!")
    @Key("noNewNotifications")
    String noNewNotifications();

    /**
     * Translated "No notifications to display.".
     *
     * @return translated "No notifications to display."
     */
    @DefaultMessage("No notifications to display.")
    @Key("noNotifications")
    String noNotifications();

    /**
     * Translated "No parameters to display.".
     *
     * @return translated "No parameters to display."
     */
    @DefaultMessage("No parameters to display.")
    @Key("noParameters")
    String noParameters();

    /**
     * msg to show when there are no results to display
     *
     * @return
     */
    String noSearchResults(String searchTerm);

    /**
     * Translated "Begin sharing".
     *
     * @return translated "Begin sharing"
     */
    @DefaultMessage("Begin sharing")
    @Key("nosharing")
    String nosharing();

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
     * Translated "Notify me by email when my analysis status changes.".
     *
     * @return translated "Notify me by email when my analysis status changes."
     */
    @DefaultMessage("Notify me by email when my analysis status changes.")
    @Key("notifyemail")
    String notifyemail();

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
     * Translated "Open Analyses window".
     *
     * @return translated "Open Analyses window"
     */
    @DefaultMessage("Open Analyses window")
    @Key("openAnalyseswindow")
    String openAnalyseswindow();

    /**
     * Translated "Open Apps window".
     *
     * @return translated "Open Apps window"
     */
    @DefaultMessage("Open Apps window")
    @Key("openAppswindow")
    String openAppswindow();

    /**
     * Translated "Open Data window".
     *
     * @return translated "Open Data window"
     */
    @DefaultMessage("Open Data window")
    @Key("openDatawindow")
    String openDatawindow();

    /**
     * The open system messages window link text
     *
     * @return the text
     */
    @DefaultMessage("Read it.")
    @Key("openMessage")
    String openMessage();

    /**
     * Translated "Open Notifications window".
     *
     * @return translated "Open Notifications window"
     */
    @DefaultMessage("Open Notifications window")
    @Key("openNotificationswindow")
    String openNotificationswindow();

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
     * Translated "page size".
     *
     * @return translated "page size"
     */
    @DefaultMessage("Page Size (KB)")
    @Key("pageSize")
    String pageSize();

    /**
     * Translated "Name".
     *
     * @return translated "Name"
     */
    @DefaultMessage("Name")
    @Key("paramName")
    String paramName();

    /**
     * Translated "Type".
     *
     * @return translated "Type"
     */
    @DefaultMessage("Type")
    @Key("paramType")
    String paramType();

    /**
     * Translated "Value".
     *
     * @return translated "Value"
     */
    @DefaultMessage("Value")
    @Key("paramValue")
    String paramValue();

    /**
     *
     * @return
     */
    String partialRestore();

    /**
     * Translated "Path".
     *
     * @return translated "Path"
     */
    @DefaultMessage("Path")
    @Key("path")
    String path();

    /**
     * Translated "You do not have the permission to perform this operation on the selected item(s).".
     *
     * @return translated
     *         "You do not have the permission to perform this operation on the selected item(s)."
     */
    @DefaultMessage("You do not have the permission to perform this operation on the selected item(s).")
    @Key("permissionErrorMessage")
    String permissionErrorMessage();

    /**
     * Translated "Permission Error".
     *
     * @return translated "Permission Error"
     */
    @DefaultMessage("Permission Error")
    @Key("permissionErrorTitle")
    String permissionErrorTitle();

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
     * Translated "Permissions".
     *
     * @return translated "Permissions"
     */
    @DefaultMessage("Permissions")
    @Key("permissions")
    String permissions();

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
     * Translated "Preferences".
     *
     * @return translated "Preferences"
     */
    @DefaultMessage("Preferences")
    @Key("preferences")
    String preferences();

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
     * Translated "Public Submission Form".
     *
     * @return translated "Public Submission Form"
     */
    @DefaultMessage("Public Submission Form")
    @Key("publicSubmissionForm")
    String publicSubmissionForm();

    /**
     * Translated "Your workflow has been successfully published into your workspace.".
     *
     * @return translated "Your workflow has been successfully published into your workspace."
     */
    @DefaultMessage("Your workflow has been successfully published into your workspace.")
    @Key("publishWorkflowSuccess")
    String publishWorkflowSuccess();

    /**
     * Translated "Published on".
     *
     * @return translated "Published on"
     */
    @DefaultMessage("Published on")
    @Key("publishedOn")
    String publishedOn();

    /**
     * Translated "Please attach any helpful links or references for this app:".
     *
     * @return translated "Please attach any helpful links or references for this app:"
     */
    @DefaultMessage("Please attach any helpful links or references for this app:")
    @Key("publicAttach")
    String publicAttach();

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
     * Translated "Suggest categories for your app:".
     *
     * @return translated "Suggest categories for your app:"
     */
    @DefaultMessage("Suggest categories for your app:")
    @Key("publicCategories")
    String publicCategories();

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
     * Translated "Briefly describe your app:".
     *
     * @return translated "Briefly describe your app:"
     */
    @DefaultMessage("Briefly describe your app:")
    @Key("publicDescription")
    String publicDescription();

    /**
     * Translated "Click app description below to edit.".
     *
     * @return translated "Click app description below to edit."
     */
    @DefaultMessage("Click app description below to edit.")
    @Key("publicDescriptionNote")
    String publicDescriptionNote();

    /**
     * Translated "What is the name of your app?".
     *
     * @return translated "What is the name of your app?"
     */
    @DefaultMessage("What is the name of your app?")
    @Key("publicName")
    String publicName();

    /**
     * Translated "Click app name below to edit.".
     *
     * @return translated "Click app name below to edit."
     */
    @DefaultMessage("Click app name below to edit.")
    @Key("publicNameNote")
    String publicNameNote();

    /**
     * Translated "Please complete required fields to submit.".
     *
     * @return translated "Please complete required fields to submit."
     */
    @DefaultMessage("Please complete all required fields.")
    @Key("publicSubmitTip")
    String publicSubmitTip();

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
     * Translated "Very useful".
     *
     * @return translated "Very useful"
     */
    @DefaultMessage("Very useful")
    @Key("reallyLikedIt")
    String reallyLikedIt();

    /**
     * Translated "Delete this Reference Genome".
     *
     * @return translated "Delete this Reference Genome"
     */
    @DefaultMessage("Delete this Reference Genome")
    @Key("refDeletePrompt")
    String refDeletePrompt();

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
     * Translated "Reference Genome(s)".
     *
     * @return translated "Reference Genome(s)"
     */
    @DefaultMessage("Reference Genome(s)")
    @Key("referenceGenomes")
    String referenceGenomes();

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
     * Translated "Relaunch".
     * 
     * @return translated "Relaunch"
     */
    @DefaultMessage("Relaunch")
    @Key("relaunchAnalysis")
    String relaunchAnalysis();

    /**
     * Translated "Remove from favorite apps.".
     *
     * @return translated "Remove from favorite apps."
     */
    @DefaultMessage("Remove from favorite apps.")
    @Key("remAppFromFav")
    String remAppFromFav();

    /**
     * Translated "Remember last file path for Apps.".
     *
     * @return translated "Remember last file path for Apps."
     */
    @DefaultMessage("Remember last file path for Apps.")
    @Key("rememberFileSectorPath")
    String rememberFileSectorPath();

    /**
     * Translated "
     * <p>
     * This option allows the Data Manager to automatically navigate the file tree to the file location
     * that was used the last time you ran that app.
     * </p>
     * <br/>
     * <p>
     * This option is helpful if you have an extensive file tree or often use the same input file
     * location.
     * </p>
     * ".
     *
     * @return translated "
     *         <p>
     *         This option allows the Data Manager to automatically navigate the file tree to the file
     *         location that was used the last time you ran that app.
     *         </p>
     * <br/>
     *         <p>
     *         This option is helpful if you have an extensive file tree or often use the same input file
     *         location.
     *         </p>
     *         "
     */
    @DefaultMessage("<p>This option allows the Data Manager to automatically navigate the file tree to the file location that was used the last time you ran that app.</p> <br/> <p> This option is helpful if you have an extensive file tree or often use the same input file location.</p>")
    @Key("rememberFileSectorPathHelp")
    String rememberFileSectorPathHelp();

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
     * Translated "New Name".
     *
     * @return translated "New Name"
     */
    @DefaultMessage("New Name")
    @Key("renamePrompt")
    String renamePrompt();

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
     * Translated "Request Tool".
     *
     * @return translated "Request Tool"
     */
    @DefaultMessage("Request Tool")
    @Key("requestTool")
    String requestTool();

    /**
     * Translated "Reset".
     *
     * @return translated "Reset"
     */
    @DefaultMessage("Reset")
    @Key("reset")
    String reset();

    /**
     * Translated "Restore".
     *
     * @return translated "Restore"
     */
    @DefaultMessage("Restore")
    @Key("restore")
    String restore();

    /**
     * Translated "Restore App".
     *
     * @return translated "Restore App"
     */
    @DefaultMessage("Restore App")
    @Key("restoreApp")
    String restoreApp();

    /**
     * Translated
     * "\"{0}\" cannot be restored automatically since it is orphaned. Please drag and drop it into appropriate category."
     * .
     *
     * @return translated
     *         "\"{0}\" cannot be restored automatically since it is orphaned. Please drag and drop it into appropriate category."
     */
    @DefaultMessage("\"{0}\" cannot be restored automatically since it is orphaned. Please drag and drop it into appropriate category.")
    @Key("restoreAppFailureMsg")
    String restoreAppFailureMsg(String arg0);

    /**
     * Translated "App Restore Failed".
     *
     * @return translated "App Restore Failed"
     */
    @DefaultMessage("App Restore Failed")
    @Key("restoreAppFailureMsgTitle")
    String restoreAppFailureMsgTitle();

    /**
     * Translated "\"{0}\" is restored into following categorie(s): \"{1}\"".
     *
     * @return translated "\"{0}\" is restored into following categorie(s): \"{1}\""
     */
    @DefaultMessage("\"{0}\" is restored into following categorie(s): \"{1}\"")
    @Key("restoreAppSucessMsg")
    String restoreAppSucessMsg(String arg0, String arg1);

    /**
     * Translated "App Restored".
     *
     * @return translated "App Restored"
     */
    @DefaultMessage("App Restored")
    @Key("restoreAppSucessMsgTitle")
    String restoreAppSucessMsgTitle();

    /**
     * Translated "Restore Defaults".
     *
     * @return translated "Restore Defaults"
     */
    @DefaultMessage("Restore Defaults")
    @Key("restoreDefaults")
    String restoreDefaults();

    /**
     * Translated "Selected files and folders are restored to their original location.".
     *
     * @return translated "Selected files and folders are restored to their original location."
     */
    @DefaultMessage("Selected files and folders are restored to their original location.")
    @Key("restoreMsg")
    String restoreMsg();

    /**
     * Translated "Discovery Environment".
     *
     * @return translated "Discovery Environment"
     */
    @DefaultMessage("Discovery Environment")
    @Key("rootApplicationTitle")
    String rootApplicationTitle();

    /**
     * Translated "Click to use this app.".
     *
     * @return translated "Click to use this app."
     */
    @DefaultMessage("Click to use this app.")
    @Key("run")
    String run();

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
     * Translated "<b> Save session </b>".
     *
     * @return translated "<b> Save session </b>"
     */
    @DefaultMessage("<b> Save session </b>")
    @Key("saveSession")
    String saveSession();

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
     * Translated "Setting saved.".
     *
     * @return translated "Setting saved."
     */
    @DefaultMessage("Setting saved.")
    @Key("saveSettings")
    String saveSettings();

    /**
     * Translated "Saving...".
     *
     * @return translated "Saving..."
     */
    @DefaultMessage("Saving...")
    @Key("savingMask")
    String savingMask();

    /**
     * Translated "Saving Session".
     *
     * @return translated "Saving Session"
     */
    @DefaultMessage("Saving Session")
    @Key("savingSession")
    String savingSession();

    /**
     * Translated "Saving current session, please wait...".
     *
     * @return translated "Saving current session, please wait..."
     */
    @DefaultMessage("Saving current session, please wait...")
    @Key("savingSessionWaitNotice")
    String savingSessionWaitNotice();

    /**
     * Translated "Search".
     *
     * @return translated "Search"
     */
    @DefaultMessage("Search")
    @Key("search")
    String search();

    /**
     * Translated "Search results: {1} found for {0}".
     * 
     * @return translated "Search results: {1} found for {0}"
     */
    @DefaultMessage("Search results: {1} found for {0}")
    @Key("searchAppResultsHeader")
    String searchAppResultsHeader(String searchText, int total);

    /**
     * Translated "Search Apps by Name or Description".
     *
     * @return translated "Search Apps by Name or Description"
     */
    @DefaultMessage("Search Apps by Name or Description")
    @Key("searchApps")
    String searchApps();
    
    @DefaultMessage("Search by Name")
    @Key("searchData")
    String searchData();

    /**
     * Translated "Search results ({2} seconds): {1} found for {0}".
     * 
     * @return translated "Search results ({2} seconds): {1} found for {0}"
     */
    @DefaultMessage("Search results ({2} seconds): {1} found for {0}")
    @Key("searchDataResultsHeader")
    String searchDataResultsHeader(String searchText, int total, double seconds);

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
     * @return
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
     * Translated "Select a file".
     *
     * @return translated "Select a file"
     */
    @DefaultMessage("Select a file")
    @Key("selectAFile")
    String selectAFile();

    /**
     * Translated "Select a folder".
     *
     * @return translated "Select a folder"
     */
    @DefaultMessage("Select a folder")
    @Key("selectAFolder")
    String selectAFolder();

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
     * Translated "Select Categories for {0}.".
     * 
     * @return translated "Select Categories for {0}."
     */
    @DefaultMessage("Select Categories for {0}.")
    @Key("selectCategories")
    String selectCategories(String appName);

    /**
     * Translated "Select Collaborator(s)".
     *
     * @return translated "Select Collaborator(s)"
     */
    @DefaultMessage("Select Collaborator(s)")
    @Key("selectCollabs")
    String selectCollabs();

    /**
     * Translated "Selected file(s) / folder(s)".
     *
     * @return translated "Selected file(s) / folder(s)"
     */
    @DefaultMessage("Selected file(s) / folder(s)")
    @Key("selectFilesFolders")
    String selectFilesFolders();

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
     * Translated "Selected folder".
     *
     * @return translated "Selected folder"
     */
    @DefaultMessage("Selected folder")
    @Key("selectedFolder")
    String selectedFolder();

    /**
     * Translated "Selected resource".
     *
     * @return translated "Selected resource"
     */
    @DefaultMessage("Selected resource")
    @Key("selectedResource")
    String selectedResource();

    /**
     * Translated "Cannot share file(s) / folder(s) with yourself.".
     *
     * @return translated "Cannot share file(s) / folder(s) with yourself."
     */
    @DefaultMessage("Cannot share file(s) / folder(s) with yourself.")
    @Key("selfShareWarning")
    String selfShareWarning();

    /**
     * Translated "Settings".
     *
     * @return translated "Settings"
     */
    @DefaultMessage("Settings")
    @Key("settings")
    String settings();

    /**
     * Translated "Share".
     *
     * @return translated "Share"
     */
    @DefaultMessage("Share")
    @Key("share")
    String share();


    /**
     * Translated "Share promt".
     *
     * @return translated "Share promt"
     */
    @DefaultMessage("Click Choose Collaborators or search for a user to begin sharing.")
    @Key("sharePrompt")
    String sharePrompt();


    /**
     * Translated
     * "Your request has been submitted. You will receive notifications when the request is complete.".
     *
     * @return translated
     *         "Your request has been submitted. You will receive notifications when the request is complete."
     */
    @DefaultMessage("Your request has been submitted. You will receive notifications when the request is complete.")
    @Key("sharingCompleteMsg")
    String sharingCompleteMsg();

    /**
     * Translated "Show".
     *
     * @return translated "Show"
     */
    @DefaultMessage("Show")
    @Key("show")
    String show();

    /**
     * Translated "Simple Download".
     *
     * @return translated "Simple Download"
     */
    @DefaultMessage("Simple Download")
    @Key("simpleDownload")
    String simpleDownload();

    /**
     * Translated "Switch to Simple Download Links".
     *
     * @return translated "Switch to Simple Download Links"
     */
    @DefaultMessage("Switch to Simple Download Links")
    @Key("simpleDownloadForm")
    String simpleDownloadForm();

    /**
     * Translated "Click on the link(s) below to begin a download.".
     *
     * @return translated "Click on the link(s) below to begin a download."
     */
    @DefaultMessage("Click on the link(s) below to begin a download.")
    @Key("simpleDownloadNotice")
    String simpleDownloadNotice();

    /**
     * Translated "Switch to Simple Upload Form".
     *
     * @return translated "Switch to Simple Upload Form"
     */
    @DefaultMessage("Switch to Simple Upload Form")
    @Key("simpleUploadForm")
    String simpleUploadForm();

    /**
     * Translated "Simple Upload from Desktop".
     *
     * @return translated "Simple Upload from Desktop"
     */
    @DefaultMessage("Simple Upload from Desktop")
    @Key("simpleUploadFromDesktop")
    String simpleUploadFromDesktop();

    /**
     * Translated "Size".
     *
     * @return translated "Size"
     */
    @DefaultMessage("Size")
    @Key("size")
    String size();

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
     * Translated "Start Date".
     *
     * @return translated "Start Date"
     */
    @DefaultMessage("Start Date")
    @Key("startDate")
    String startDate();

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
     * Translated "Submit your app for public use.".
     *
     * @return translated "Submit your app for public use."
     */
    @DefaultMessage("Submit your app for public use.")
    @Key("submitForPublicUse")
    String submitForPublicUse();
    
    /**
     * Translated "Intro text for Submit your app for public use form.".
     *
     * @return translated "Intro text for Submit your app for public use form."
     */
    @Key("submitForPublicUseIntro")
    String submitForPublicUseIntro();

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
     * Translated "Disable this app temporarily".
     *
     * @return translated "Disable this app temporarily"
     */
    @DefaultMessage("Disable this app temporarily")
    @Key("tempDisable")
    String tempDisable();

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
    @DefaultMessage("Tool Request Status")
    @Key("toolRequestStatus")
    String toolRequestStatus();
    
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
     * Translated "Tree URL".
     *
     * @return translated "Tree URL"
     */
    @DefaultMessage("Tree URL")
    @Key("treeUrl")
    String treeUrl();

    /**
     * Translated "Delete a previous rating".
     *
     * @return translated "Delete a previous rating"
     */
    @DefaultMessage("Delete a previous rating")
    @Key("unrate")
    String unrate();

    /**
     * Translated "Unshare".
     *
     * @return translated "Unshare"
     */
    @DefaultMessage("Unshare")
    @Key("unshare")
    SafeHtml unshare();

    /**
     * Translated "Move Up".
     *
     * @return translated "Move Up"
     */
    @DefaultMessage("Move Up")
    @Key("up")
    String up();

    /**
     * Translated "Reference Genome updated.".
     *
     * @return translated "Reference Genome updated."
     */
    @DefaultMessage("Reference Genome updated.")
    @Key("updateRefGenome")
    String updateRefGenome();

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
     * Translated "Uploading to {0}.".
     *
     * @return translated "Uploading to {0}."
     */
    @DefaultMessage("Uploading to {0}.")
    @Key("uploadingToFolder")
    String uploadingToFolder(String arg0);

    /**
     * Translated "Import from URL".
     *
     * @return translated "Import from URL"
     */
    @DefaultMessage("Import from URL")
    @Key("urlImport")
    String urlImport();

    /**
     * Translated "Enter URLs below (HTTP(S) or FTP only):".
     *
     * @return translated "Enter URLs below (HTTP(S) or FTP only):"
     */
    @DefaultMessage("Enter URLs below (HTTP(S) or FTP only):")
    @Key("urlPrompt")
    String urlPrompt();

    /**
     * Translated "User-Agent:".
     *
     * @return translated "User-Agent:"
     */
    @DefaultMessage("User-Agent:")
    @Key("userAgent")
    String userAgent();

    /**
     * Translated "User Name".
     *
     * @return translated "User Name"
     */
    @DefaultMessage("User Name")
    @Key("userId")
    String userId();

    /**
     * Translated "User Metadata".
     * 
     * @return translated "User Metadata"
     */
    @DefaultMessage("User Metadata")
    @Key("userMetadata")
    String userMetadata();

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
     * Translated "Visibility Varies".
     *
     * @return translated "Visibility Varies"
     */
    @DefaultMessage("Visibility Varies")
    @Key("variablePermissionsNotice")
    String variablePermissionsNotice();

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
     * Translated "via Discovery Environment".
     *
     * @return translated "via Discovery Environment"
     */
    @DefaultMessage("via Discovery Environment")
    @Key("viaDiscoveryEnvironment")
    String viaDiscoveryEnvironment();

    /**
     * Translated "via Public Link".
     *
     * @return translated "via Public Link"
     */
    @DefaultMessage("via Public Link")
    @Key("viaPublicLink")
    String viaPublicLink();

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
     * Translated "View Parameters".
     *
     * @return translated "View Parameters"
     */
    @DefaultMessage("View Parameters")
    @Key("viewParamLbl")
    String viewParamLbl();

    /**
     * Translated "Viewing parameters for {0}".
     *
     * @return translated "Viewing parameters for {0}"
     */
    @DefaultMessage("Viewing parameters for {0}")
    @Key("viewParameters")
    String viewParameters(String arg0);

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
     * Translated "Who has access".
     *
     * @return translated "Who has access"
     */
    @DefaultMessage("Who has access")
    @Key("whoHasAccess")
    String whoHasAccess();

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
     * Translated "Workflow".
     *
     * @return translated "Workflow"
     */
    @DefaultMessage("Workflow")
    @Key("workflow")
    String workflow();

    /**
     * Translated "Workflow Information".
     *
     * @return translated "Workflow Information"
     */
    @DefaultMessage("Workflow Information")
    @Key("workflowInfo")
    String workflowInfo();

    /**
     * Translated "Wrap Text".
     *
     * @return translated "Wrap Text"
     */
    @DefaultMessage("Wrap Text")
    @Key("wrap")
    String wrap();

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
     * @param path
     * @return translated "Pending verification that {0} exists."
     */
    @DefaultMessage("Pending verification that {0} exists.")
    @Key("diskResourceExistCheckPending")
    String diskResourceExistCheckPending(String path);
    
    /**
     * Translated "Disk resource not available."
     * 
     * @return
     */
    String diskResourceNotAvailable();
    

    /**
     * Translated "System Messages"
     * 
     * @return translated "System Messages"
     */
    @DefaultMessage("System Messages")
    @Key("systemMessages")
    String systemMessages();

    /**
     * Translated "Add New Reference Genome"
     * 
     * @return translated "Add New Reference Genome"
     */
    @DefaultMessage("Add New Reference Genome")
    @Key("addReferenceGenome")
    String addReferenceGenome();

   

}
