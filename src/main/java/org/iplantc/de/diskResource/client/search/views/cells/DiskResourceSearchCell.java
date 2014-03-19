package org.iplantc.de.diskResource.client.search.views.cells;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent.HasSubmitDiskResourceQueryEventHandlers;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;

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
public class DiskResourceSearchCell extends TriggerFieldCell<String> implements HasExpandHandlers, HasCollapseHandlers, HasSubmitDiskResourceQueryEventHandlers, SaveDiskResourceQueryEvent.HasSaveDiskResourceQueryEventHandlers,
        SubmitDiskResourceQueryEventHandler, SaveDiskResourceQueryEvent.SaveDiskResourceQueryEventHandler, HideHandler {

    public interface DiskResourceSearchCellAppearance extends TriggerFieldAppearance {}

    private boolean expanded;
    private DiskResourceQueryForm searchForm;

    /**
     * Creates a new date cell.
     */
    public DiskResourceSearchCell() {
        this(GWT.<DiskResourceSearchCellAppearance> create(DiskResourceSearchCellAppearance.class));
    }

    /**
     * Creates a new date cell.
     * 
     * @param appearance the date cell appearance
     */
    public DiskResourceSearchCell(DiskResourceSearchCellAppearance appearance) {
        super(appearance);
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
    public HandlerRegistration addSaveDiskResourceQueryEventHandler(SaveDiskResourceQueryEvent.SaveDiskResourceQueryEventHandler handler) {
        return addHandler(handler, SaveDiskResourceQueryEvent.TYPE);
    }

    @Override
    public HandlerRegistration addSubmitDiskResourceQueryEventHandler(SubmitDiskResourceQueryEventHandler handler) {
        return addHandler(handler, SubmitDiskResourceQueryEvent.TYPE);
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

    @Override
    public void doSaveDiskResourceQueryTemplate(SaveDiskResourceQueryEvent event) {
        // Refire
        fireEvent(event);
    }

    @Override
    public void doSubmitDiskResourceQuery(SubmitDiskResourceQueryEvent event) {
        final DiskResourceQueryTemplate queryTemplate = event.getQueryTemplate();
        if (queryTemplate.isSaved()) {
            fireEvent(new SaveDiskResourceQueryEvent(queryTemplate, queryTemplate.getName()));
        } else {
            // Refire
            fireEvent(event);
        }
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
        if (searchForm == null) {
            searchForm = new DiskResourceQueryForm();
            searchForm.addHideHandler(this);
            searchForm.addSubmitDiskResourceQueryEventHandler(this);
            searchForm.addSaveDiskResourceQueryEventHandler(this);
        }
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
    protected boolean isFocusClick(XElement parent, XElement target) {
        boolean result = parent.isOrHasChild(target) || (searchForm != null && searchForm.getElement().isOrHasChild(target));
        return result;
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
}
