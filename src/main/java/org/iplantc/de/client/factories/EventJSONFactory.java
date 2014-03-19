package org.iplantc.de.client.factories;

import org.iplantc.de.client.utils.builders.event.json.EventJSONBuilder;
import org.iplantc.de.client.utils.builders.event.json.UploadEventJSONBuilder;

import com.google.gwt.json.client.JSONObject;

/**
 * Factory class to build a valid data event json from type and RPC success JSON.
 * 
 * @author amuir
 * 
 */
public class EventJSONFactory {
    public static enum ActionType {
        UPLOAD_COMPLETE;//, FOLDER_CREATED, SAVE_AS, DISKRESOURCE_RENAMED, DISKRESOURCE_DELETE, ANALYSIS_LAUNCHED, DISPLAY_WINDOW, DISPLAY_VIEWER_WINDOWS, DISPLAY_TREE_VIEWER_WINDOWS, LOGOUT, EMPTYTRASH
    }

    private static EventJSONBuilder getBuilder(ActionType type) {
        EventJSONBuilder ret = null; // assume failure

        switch (type) {
            case UPLOAD_COMPLETE:
                ret = new UploadEventJSONBuilder("file_uploaded"); //$NON-NLS-1$
                break;

//            case FOLDER_CREATED:
//                ret = new BasicEventJSONBuilder("data", "folder_created"); //$NON-NLS-1$ //$NON-NLS-2$
//                break;

//            case SAVE_AS:
//                ret = new BasicEventJSONBuilder("data", "save_as"); //$NON-NLS-1$ //$NON-NLS-2$
//                break;

//            case DISKRESOURCE_RENAMED:
//                ret = new BasicEventJSONBuilder("data", "diskresource_renamed"); //$NON-NLS-1$ //$NON-NLS-2$
//                break;
//
//            case DISKRESOURCE_DELETE:
//                ret = new BasicEventJSONBuilder("data", "diskresource_delete"); //$NON-NLS-1$ //$NON-NLS-2$
//                break;
//
//            case EMPTYTRASH:
//                ret = new BasicEventJSONBuilder("data", "diskresource_delete"); //$NON-NLS-1$ //$NON-NLS-2$
//                break;

//            case DISPLAY_WINDOW:
//                ret = new ViewerWindowLaunchEventJSONBuilder("display_window"); //$NON-NLS-1$
//                break;

//            case DISPLAY_VIEWER_WINDOWS:
//                ret = new ViewerWindowLaunchEventJSONBuilder("display_viewer"); //$NON-NLS-1$
//                break;

//            case DISPLAY_TREE_VIEWER_WINDOWS:
//                ret = new ViewerWindowLaunchEventJSONBuilder("display_viewer_add_treetab"); //$NON-NLS-1$
//                break;

//            case ANALYSIS_LAUNCHED:
//                ret = new AnalysisLaunchedEventJSONBuilder("analysis_launched"); //$NON-NLS-1$
//                break;

//            case LOGOUT:
//                ret = new BasicEventJSONBuilder("system", "logout"); //$NON-NLS-1$ //$NON-NLS-2$
//                break;
        }

        return ret;
    }

    /**
     * Build valid data event json from RPC success json.
     * 
     * @param type type of event to build
     * @param json json returned from the success of a data related RPC call.
     * @return new json that can be processed by the message dispatcher.
     */
    public static JSONObject build(ActionType type, final JSONObject json) {
        EventJSONBuilder builder = getBuilder(type);

        return (builder == null) ? null : builder.build(json); //$NON-NLS-1$
    }
}
