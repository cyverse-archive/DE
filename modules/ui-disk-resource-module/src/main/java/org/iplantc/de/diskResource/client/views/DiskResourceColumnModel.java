package org.iplantc.de.diskResource.client.views;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.views.cells.DiskResourceActionsCell;
import org.iplantc.de.diskResource.client.views.cells.DiskResourceNameCell;
import org.iplantc.de.diskResource.client.views.cells.events.DiskResourceNameSelectedEvent;
import org.iplantc.de.diskResource.client.views.cells.events.ManageMetadataEvent;
import org.iplantc.de.diskResource.client.views.cells.events.ManageSharingEvent;
import org.iplantc.de.diskResource.client.views.cells.events.ShareByDataLinkEvent;
import org.iplantc.de.diskResource.share.DiskResourceModule;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiskResourceColumnModel extends ColumnModel<DiskResource> implements DiskResourceNameSelectedEvent.HasDiskResourceNameSelectedEventHandlers,
                                                                                  ShareByDataLinkEvent.HasShareByDataLinkEventHandlers,
                                                                                  ManageSharingEvent.HasManageSharingEventHandlers,
                                                                                  ManageMetadataEvent.HasManageMetadataEventHandlers {

    public DiskResourceColumnModel(DiskResourceSelectionModel sm, IplantDisplayStrings displayStrings) {
        super(createColumnConfigList(sm, displayStrings));

        for(ColumnConfig<DiskResource, ?> cc : configs){
            if(cc.getCell() instanceof DiskResourceNameCell){
                ((DiskResourceNameCell)cc.getCell()).setHasHandlers(this);
            } else if(cc.getCell() instanceof DiskResourceActionsCell){
                ((DiskResourceActionsCell)cc.getCell()).setHasHandlers(this);
            }
        }
    }

    public static List<ColumnConfig<DiskResource, ?>> createColumnConfigList(DiskResourceSelectionModel sm, IplantDisplayStrings displayStrings) {
        List<ColumnConfig<DiskResource, ?>> list = new ArrayList<ColumnConfig<DiskResource, ?>>();

        DiskResourceProperties props = GWT.create(DiskResourceProperties.class);

        ColumnConfig<DiskResource, DiskResource> name = new ColumnConfig<DiskResource, DiskResource>(new IdentityValueProvider<DiskResource>("name"), 100, displayStrings.name());
        ColumnConfig<DiskResource, Date> lastModified = new ColumnConfig<DiskResource, Date>(props.lastModified(), 120, displayStrings.lastModified());
        ColumnConfig<DiskResource, Long> size = new ColumnConfig<DiskResource, Long>(new DiskResourceSizeValueProvider(), 50, displayStrings.size());
        ColumnConfig<DiskResource, String> path = new ColumnConfig<DiskResource, String>(props.id(), 100, displayStrings.path());
        ColumnConfig<DiskResource, Date> created = new ColumnConfig<DiskResource, Date>(props.dateSubmitted(), 120, displayStrings.dateSubmitted());
        ColumnConfig<DiskResource, DiskResource> actions = new ColumnConfig<DiskResource, DiskResource>(new IdentityValueProvider<DiskResource>("actions"), 100, "");

        name.setCell(new DiskResourceNameCell());
        lastModified.setCell(new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM)));
        size.setCell(new DiskResourceSizeCell());
        created.setCell(new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM)));
        actions.setCell(new DiskResourceActionsCell());

        name.setComparator(new DiskResourceNameComparator());

        path.setHidden(true);
        created.setHidden(true);
        actions.setHidden(false);

        actions.setMenuDisabled(true);
        actions.setSortable(false);
        actions.setFixed(true);
        
        list.add(sm.getColumn());
        list.add(name);
        list.add(lastModified);
        list.add(size);
        list.add(path);
        list.add(created);
        list.add(actions);

        return list;
    }

    @Override
    public HandlerRegistration addDiskResourceNameSelectedEventHandler(DiskResourceNameSelectedEvent.DiskResourceNameSelectedEventHandler handler) {
        return ensureHandlers().addHandler(DiskResourceNameSelectedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addManageMetadataEventHandler(ManageMetadataEvent.ManageMetadataEventHandler handler) {
        return ensureHandlers().addHandler(ManageMetadataEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addManageSharingEventHandler(ManageSharingEvent.ManageSharingEventHandler handler) {
        return ensureHandlers().addHandler(ManageSharingEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addShareByDataLinkEventHandler(ShareByDataLinkEvent.ShareByDataLinkEventHandler handler) {
        return ensureHandlers().addHandler(ShareByDataLinkEvent.TYPE, handler);
    }

    public void setCheckboxColumnHidden(boolean hidden) {
        setHidden(0, hidden);
    }

    public void ensureDebugId(String baseID) {
        for(ColumnConfig<DiskResource, ?> cc : configs){
            if(cc.getCell() instanceof DiskResourceNameCell){
                ((DiskResourceNameCell)cc.getCell()).setBaseDebugId(baseID + DiskResourceModule.Ids.GRID);
            } else if(cc.getCell() instanceof DiskResourceActionsCell){
                ((DiskResourceActionsCell)cc.getCell()).setBaseDebugId(baseID + DiskResourceModule.Ids.GRID);
            }
        }
    }

    /**
     * This is a value provider class which returns the size of any <code>DiskResource</code>. If the
     * <code>DiskResource</code> is a <code>Folder</code>, this provider will return null. If it is a
     * <code>File</code>, then it returns the value of the {@link File#getSize()} method as an Integer.
     * 
     * @author jstroot
     * 
     */
    private static final class DiskResourceSizeValueProvider implements
            ValueProvider<DiskResource, Long> {
        @Override
        public Long getValue(DiskResource object) {
            if (object instanceof File) {
                return new Long(((File)object).getSize());
            } else {
                return null;
            }
        }

        @Override
        public void setValue(DiskResource object, Long value) {
        }

        @Override
        public String getPath() {
            return "size"; //$NON-NLS-1$
        }
    }

    /**
     * A <code>Cell</code> for converting bytes as integers into human readable <code>File</code> sizes.
     * 
     * @author psarando
     * 
     */
    private static final class DiskResourceSizeCell extends AbstractCell<Long> {

        @Override
        public void render(Context context, Long value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.appendEscaped(DiskResourceUtil.formatFileSize(value.toString()));
            }
        }
    }
}
