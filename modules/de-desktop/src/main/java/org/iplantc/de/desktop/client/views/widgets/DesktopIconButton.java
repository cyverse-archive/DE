package org.iplantc.de.desktop.client.views.widgets;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.Event;

import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.event.ShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.menu.Menu;

/**
 * An icon button capable of displaying a menu on click by utilizing
 * the {@link Component#setContextMenu(Menu)}. This is accomplished by displaying the
 * context menu on left click instead of right click.
 *
 * @author jstroot
 */
public class DesktopIconButton extends IconButton {

    private Menu menu;

    @UiConstructor
    public DesktopIconButton(IconConfig config) {
        super(config);
    }

    public void hideMenu() {
        menu.hide();
    }

    @Override
    protected void onRightClick(Event event) {
        // Disable right click functionality of super
    }

    @Override
    protected void onShowContextMenu(int clientX, int clientY) {
        // Disable context menu functionality
    }

    @Override
    public void setContextMenu(Menu menu) {
        this.menu = menu;
        super.setContextMenu(menu);
    }

    @Override
    protected void onClick(Event e) {
        super.onClick(e);
        fireEvent(new ShowContextMenuEvent(menu));
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                final Style.AnchorAlignment anchorAlignment = new Style.AnchorAlignment(Style.Anchor.BOTTOM_LEFT, Style.Anchor.TOP_LEFT, true);
                menu.show(getElement(), anchorAlignment);
            }
        });
    }
}
