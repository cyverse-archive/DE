package org.iplantc.de.client.utils.builders;

import org.iplantc.de.client.models.ShortcutDesc;
import org.iplantc.de.client.views.windows.configs.WindowConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for managing desktop shortcuts.
 * 
 * @author amuir
 * 
 */
public abstract class DesktopBuilder {
    private final List<ShortcutDesc> shortcuts = new ArrayList<ShortcutDesc>();

    /**
     * Default constructor.
     */
    public DesktopBuilder() {
        buildShortcuts();
    }

    /**
     * Creates desktop shortcut widgets.
     */
    protected abstract void buildShortcuts();

    protected void addShortcut(String style, String id, String hoverStyle, String index, String caption,
            String action, WindowConfig config, String help) {
        shortcuts.add(new ShortcutDesc(style, id, hoverStyle, index, caption, action, config, help));
    }

    /**
     * Retrieves all added shortcuts.
     * 
     * @return all shortcuts.
     */
    public List<ShortcutDesc> getShortcuts() {
        return shortcuts;
    }
}
