/**
 *
 */
package org.iplantc.de.resources.client;

import org.iplantc.de.resources.client.uiapps.integration.AppIntegrationPaletteImages;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author sriram
 * 
 */
public interface IplantResources extends ClientBundle,
                                         AppResources,
                                         DiskResourceResources,
                                         AppIntegrationPaletteImages,
                                         DEHeaderResources,
                                         DEFeedbackResources {

    public static IplantResources RESOURCES = GWT.create(IplantResources.class);

    
    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("iplant_tiny.png")
    ImageResource iplantTiny();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("list-items.gif")
    ImageResource listItems();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("delete.gif")
    ImageResource cancel();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("save.gif")
    ImageResource save();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("refresh.gif")
    ImageResource refresh();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("whitelogo.png")
    ImageResource whitelogo();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("Stop.png")
    ImageResource stop();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("tick.png")
    ImageResource tick();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("up.gif")
    ImageResource goUp();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("de-menu.png")
    ImageResource userMenu();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("run.png")
    ImageResource run();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("edit.gif")
    ImageResource edit();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("file_copy.gif")
    ImageResource copy();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("delete_icon.png")
    ImageResource deleteIcon();

    /**
     * 
     * @return image.
     */
    @Source("arrow.gif")
    ImageResource menuAnchor();

    @Source("wand.png")
    ImageResource layoutWand();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("book.png")
    ImageResource subCategory();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("book_edit.png")
    ImageResource cat_edit();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("magnifier.png")
    ImageResource search();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("new.gif")
    ImageResource add();

    /**
     * Image resource
     * 
     * @return image.
     */
    @Source("delete.gif")
    ImageResource delete();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("group_key.png")
    ImageResource share();

	/**
	 * Image resource.
	 * 
	 * @return image.
	 */
    @Source("group.png")
    ImageResource viewCurrentCollabs();

    @Source("down.png")
    DataResource down();

    @Source("trash_can_open.png")
    DataResource trashOpen();

    @Source("trash_can_close.png")
    DataResource trashClose();

    @Source("information.png")
    ImageResource info();

    @Source("publish.png")
    ImageResource publish();

    @Source("arrow_up.png")
    ImageResource arrowUp();

    @Source("arrow_down.png")
    ImageResource arrowDown();

    /** Begin App resources **/

    @Source("FavoriteCell.css")
    FavoriteCellStyle favoriteCss();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("fav.png")
    ImageResource favIcon();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("fav_add.png")
    ImageResource favIconAdd();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("fav_remove.png")
    ImageResource favIconDelete();

    /**
     * Image resource for a View Deployed Components icon.
     * 
     * @return image.
     */
    @Override
    @Source("script_link.png")
    ImageResource viewDeployedComponents();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Override
    @Source("book_add.png")
    ImageResource category();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Override
    @Source("book_open.png")
    ImageResource category_open();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Override
    @Source("go_public.png")
    ImageResource submitForPublic();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Source("not_fav.png")
    ImageResource disabledFavIcon();

    @Override
    @Source("exclamation.png")
    ImageResource exclamation();

    /** end app resources **/

    /** begin Disk resource **/

    @Source("DiskResourceNameCell.css")
    DiskResourceNameCellStyle diskResourceNameCss();

    /**
     * Image resource.
     *
     * @return image.
     */
    @Source("folder.gif")
    ImageResource folder();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Override
    @Source("file_rename.gif")
    ImageResource fileRename();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Override
    @Source("folder_add.gif")
    ImageResource folderAdd();

    /**
     * Image resource.
     * 
     * @return image.
     */
    @Override
    @Source("drive_web.png")
    ImageResource urlImport();

    @Override
    @Source("integrator_checkbox.png")
    ImageResource inputCheckBox();

    @Override
    @Source("integrator_env_var.png")
    ImageResource inputEnvVar();

    @Override
    @Source("integrator_multi_input_files.png")
    ImageResource inputFileMulti();

    @Override
    @Source("integrator_input_file.png")
    ImageResource inputFile();

    @Override
    @Source("integrator_input_folder.png")
    ImageResource inputFolder();

    @Override
    @Source("integrator_info_text.png")
    ImageResource generalInfoText();

    @Override
    @Source("integrator_section.png")
    ImageResource inputSection();

    @Override
    @Source("integrator_Integer.png")
    ImageResource inputNumberInteger();

    @Override
    @Source("integrator_Double.png")
    ImageResource inputNumberDouble();

    @Override
    @Source("integrator_tree_list.png")
    ImageResource inputSelectGrouped();

    @Override
    @Source("integrator_list.png")
    ImageResource inputSelectSingle();

    @Override
    @Source("integrator_select_integer.png")
    ImageResource inputSelectInteger();

    @Override
    @Source("integrator_select_double.png")
    ImageResource inputSelectDouble();

    @Override
    @Source("integrator_multi_line_text.png")
    ImageResource inputTextMulti();

    @Override
    @Source("integrator_single_line_text.png")
    ImageResource inputTextSingle();

    @Override
    @Source("integrator_output_file_name.png")
    ImageResource outputFileName();

    @Override
    @Source("integrator_output_folder_name.png")
    ImageResource outputFolderName();

    @Override
    @Source("integrator_multi_output.png")
    ImageResource outputMultiFile();

    @Override
    @Source("integrator_ref_annotation.png")
    ImageResource referenceAnnotation();

    @Override
    @Source("integrator_ref_genome.png")
    ImageResource referenceGenome();

    @Override
    @Source("integrator_ref_seq.png")
    ImageResource referenceSequence();

    @Override
    @Source("header_bg.png")
    ImageResource headerBg();

    @Override
    @Source("mini_logo.png")
    ImageResource headerLogo();

    @Source("help.png")
    ImageResource help();

    @Source("DEHeader.css")
    DEHeaderStyle getHeaderStyle();

    @Source("tree_collapse_hover.png")
    ImageResource treeCollapseHover();

    @Source("warning_mini.png")
    ImageResource warningMini();

    @Override
    @Source("de_feedback.png")
    ImageResource feedback();
    
    @Source("forums.png")
    ImageResource forums();
    
    @Source("ToolRequestStatusHelp.css")
    ToolRequestStatusHelpStyle getToolRequestStatusHelpCss();

    @Source("Feedback.css")
    DEFeedbackStyle getFeedbackStyle();
    
    @Source("IplantContextualHelpAccess.css")
    IplantContextualHelpAccessStyle getContxtualHelpStyle();

    @Source("user_comment.png")
    ImageResource userComment();
}
