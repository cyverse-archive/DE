package org.iplantc.de.client.models;

import org.iplantc.de.commons.client.views.window.configs.WindowConfig;

/**
 * Models the data associated to a desktop shortcut.
 * 
 * @author amuir
 * 
 */
public class ShortcutDesc {
    private final String style;

    /**
     * @return the style
     */
    public String getStyle() {
        return style;
    }

    private final String id;
    private final String hoverStyle;
    private final String caption;
    private String action;
    private String tag;
    private String index;
    private final WindowConfig config;
    private String help;

    public ShortcutDesc(String style, String id, String hoverStyle, String index, String caption,
            String action, WindowConfig config, String help) {
        this.style = style;
        this.id = id;
        this.hoverStyle = hoverStyle;
        this.caption = caption;
        if (action != null) {
            this.action = action;
        }
        this.index = index;
        this.config = config;
        this.help = help;
    }

    public WindowConfig getWindowConfig() {
        return config;
    }

    /**
     * Retrieves the unique identifier.
     * 
     * @return unique identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the caption.
     * 
     * @return shortcut caption.
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Retrieve action.
     * 
     * @return action field.
     */
    public String getAction() {
        return action;
    }

    /**
     * Retrieve tag.
     * 
     * @return tag field.
     */
    public String getTag() {
        return tag;
    }

    /**
     * @return the index
     */
    public String getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * @return the hoverStyle
     */
    public String getHoverStyle() {
        return hoverStyle;
    }

    /**
     * @return the help
     */
    public String getHelp() {
        return help;
    }

    /**
     * @param help the help to set
     */
    public void setHelp(String help) {
        this.help = help;
    }
}
