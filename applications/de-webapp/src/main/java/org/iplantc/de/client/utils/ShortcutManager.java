package org.iplantc.de.client.utils;

import org.iplantc.de.client.desktop.widget.Shortcut;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.models.ShortcutDesc;
import org.iplantc.de.client.utils.builders.DesktopBuilder;

import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all of the application's desktop shortcuts.
 * 
 * @author amuir
 * 
 */
public class ShortcutManager {
    private final List<Shortcut> shortcuts = new ArrayList<Shortcut>();

    private final SelectHandler handler = new SelectHandler() {

        @Override
        public void onSelect(SelectEvent event) {
            Shortcut shortcut = (Shortcut)event.getSource();
            eventBus.fireEvent(new WindowShowRequestEvent(shortcut.getWindowConfig()));

        }
    };

    private final EventBus eventBus;

    /**
     * Instantiate from desktop builder.
     * 
     * @param builder builder which contains shortcut templates.
     * @param eventBus
     */
    public ShortcutManager(DesktopBuilder builder, EventBus eventBus) {
        this.eventBus = eventBus;
        addShortcuts(builder);
    }

    /**
     * Helper method to extract shortcut templates from a desktop builder and allocate shortcuts.
     * 
     * @param builder builder which contains shortcut templates.
     */
    protected void addShortcuts(DesktopBuilder builder) {
        if (builder != null) {
            List<ShortcutDesc> descs = builder.getShortcuts();

            for (ShortcutDesc desc : descs) {
                shortcuts.add(new Shortcut(desc, handler));
            }
        }
    }

    /**
     * Retrieve our list of shortcuts.
     * 
     * @return shortcut list.
     */
    public List<Shortcut> getShortcuts() {
        return shortcuts;
    }
}
