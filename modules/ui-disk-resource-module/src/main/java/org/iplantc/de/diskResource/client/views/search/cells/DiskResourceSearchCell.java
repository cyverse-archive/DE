package org.iplantc.de.diskResource.client.views.search.cells;

import org.iplantc.de.diskResource.client.events.search.SaveDiskResourceQueryClickedEvent;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent.HasSubmitDiskResourceQueryEventHandlers;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;

import com.sencha.gxt.cell.core.client.form.DateCell;
import com.sencha.gxt.cell.core.client.form.TriggerFieldCell;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.HasCollapseHandlers;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.HasExpandHandlers;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

/**
 * This class is a clone-and-own of {@link DateCell}.
 * 
 * @author jstroot
 * 
 */
public class DiskResourceSearchCell extends TriggerFieldCell<String> implements HasExpandHandlers,
                                                                                HasCollapseHandlers,
                                                                                HasSubmitDiskResourceQueryEventHandlers,
                                                                                SaveDiskResourceQueryClickedEvent.HasSaveDiskResourceQueryClickedEventHandlers,
                                                                                HideHandler {

    public interface DiskResourceSearchCellAppearance extends TriggerFieldAppearance {
        String advancedSearchToolTip();
    }

    private boolean expanded;
    private final DiskResourceQueryForm searchForm;
    private final DiskResourceSearchCellAppearance appearance;

    /**
     * Creates a new date cell.
     *
     * @param appearance the date cell appearance
     */
    @Inject
    DiskResourceSearchCell(final DiskResourceQueryForm searchForm,
                           final DiskResourceSearchCellAppearance appearance) {
        super(appearance);
        this.searchForm = searchForm;
        this.appearance = appearance;
        searchForm.addHideHandler(this);
    }

    @Override
    public HandlerRegistration addCollapseHandler(CollapseHandler handler) {
        return addHandler(handler, CollapseEvent.getType());
    }

    @Override
    public HandlerRegistration addExpandHandler(ExpandHandler handler) {
        return addHandler(handler, ExpandEvent.getType());
    }

    @Override
    public HandlerRegistration addSaveDiskResourceQueryClickedEventHandler(SaveDiskResourceQueryClickedEvent.SaveDiskResourceQueryClickedEventHandler handler) {
        return searchForm.addSaveDiskResourceQueryClickedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addSubmitDiskResourceQueryEventHandler(SubmitDiskResourceQueryEventHandler handler) {
        return searchForm.addSubmitDiskResourceQueryEventHandler(handler);
    }

    public void collapse(final Context context, final XElement parent) {
        if (!expanded) {
            return;
        }

        expanded = false;

        getSearchForm().hide();
        getInputElement(parent).focus();
        fireEvent(context, new CollapseEvent(context));
    }

    public void expand(final Context context, final XElement parent, String value, ValueUpdater<String> valueUpdater) {
        if (expanded) {
            return;
        }

        this.expanded = true;

        // expand may be called without the cell being focused
        // saveContext sets focusedCell so we clear if cell
        // not currently focused
        boolean focused = focusedCell != null;
        saveContext(context, parent, null, valueUpdater, value);
        if (!focused) {
            focusedCell = null;
        }

        /*String s = null;
        try {
            s = getPropertyEditor().parse(getText(parent));
        } catch (ParseException e) {
            s = value == null ? "" : value;
        }*/

        // TODO JDS we want to take the text they have typed in and put it in the searchForm

        // handle case when down arrow is opening menu
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                getSearchForm().show(parent, new AnchorAlignment(Anchor.TOP_LEFT, Anchor.BOTTOM_LEFT, true));

                fireEvent(context, new ExpandEvent(context));
            }
        });
    }

    public DiskResourceQueryForm getSearchForm() {
        return searchForm;
    }

    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void onHide(HideEvent event) {
        collapse(lastContext, lastParent);
    }

    @Override
    protected boolean isFocusedWithTarget(Element parent, Element target) {
        return super.isFocusedWithTarget(parent, target) || (searchForm != null && searchForm.getElement().isOrHasChild(target));
    }

    @Override
    protected void onNavigationKey(Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
        if (event.getKeyCode() == KeyCodes.KEY_DOWN && !isExpanded()) {
            event.stopPropagation();
            event.preventDefault();
            onTriggerClick(context, parent.<XElement> cast(), event, value, valueUpdater);
        }
    }

    @Override
    protected void onTriggerClick(Context context, XElement parent, NativeEvent event, String value, ValueUpdater<String> updater) {
        super.onTriggerClick(context, parent, event, value, updater);
        if (!isReadOnly() && !isDisabled()) {
            // blur is firing after the expand so context info on expand is being cleared
            // when value change fires lastContext and lastParent are null without this code
            if ((GXT.isWebKit()) && lastParent != null && lastParent != parent) {
                getInputElement(lastParent).blur();
            }
            expand(context, parent, value, updater);
        }
    }

    @Override
    protected void onMouseOver(XElement parent, NativeEvent event) {
        super.onMouseOver(parent, event);
        parent.getStyle().setCursor(Cursor.POINTER);
        parent.setTitle(appearance.advancedSearchToolTip());
    }

    @Override
    protected void onMouseOut(XElement parent, NativeEvent event) {
        super.onMouseOut(parent, event);
        parent.getStyle().setCursor(Cursor.AUTO);
    }
}
