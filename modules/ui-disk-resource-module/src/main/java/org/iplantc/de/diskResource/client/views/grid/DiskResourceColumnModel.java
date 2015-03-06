package org.iplantc.de.diskResource.client.views.grid;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.events.DiskResourceNameSelectedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourcePathSelectedEvent;
import org.iplantc.de.diskResource.client.events.RequestDiskResourceFavoriteEvent;
import org.iplantc.de.diskResource.client.events.selection.CopyMetadataSelected;
import org.iplantc.de.diskResource.client.events.selection.CopyMetadataSelected.CopyMetadataSelectedEventHandler;
import org.iplantc.de.diskResource.client.events.selection.ManageCommentsSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageMetadataSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelected;
import org.iplantc.de.diskResource.client.events.selection.ShareByDataLinkSelected;
import org.iplantc.de.diskResource.client.model.DiskResourceNameComparator;
import org.iplantc.de.diskResource.client.model.DiskResourceProperties;
import org.iplantc.de.diskResource.client.views.grid.cells.DiskResourceActionsCell;
import org.iplantc.de.diskResource.client.views.grid.cells.DiskResourceFavoriteCell;
import org.iplantc.de.diskResource.client.views.grid.cells.DiskResourceNameCell;
import org.iplantc.de.diskResource.client.views.grid.cells.DiskResourcePathCell;
import org.iplantc.de.diskResource.share.DiskResourceModule;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jstroot
 */
public class DiskResourceColumnModel extends ColumnModel<DiskResource> implements DiskResourceNameSelectedEvent.HasDiskResourceNameSelectedEventHandlers,
                                                                                  ShareByDataLinkSelected.HasShareByDataLinkSelectedEventHandlers,
                                                                                  ManageSharingSelected.HasManageSharingSelectedEventHandlers,
                                                                                  ManageMetadataSelected.HasManageMetadataSelectedEventHandlers,
                                                                      CopyMetadataSelected.HasCopyMetadataSelectedEventHandlers,
                                                                                  RequestDiskResourceFavoriteEvent.HasManageFavoritesEventHandlers,
                                                                                  ManageCommentsSelected.HasManageCommentsSelectedEventHandlers,
                                                                                  DiskResourcePathSelectedEvent.HasDiskResourcePathSelectedEventHandlers {


    public DiskResourceColumnModel(@SuppressWarnings("rawtypes") final CheckBoxSelectionModel sm,
                                   final GridView.Appearance appearance,
                                   final DiskResourceUtil diskResourceUtil) {
        super(createColumnConfigList(sm, appearance, diskResourceUtil));

        for(ColumnConfig<DiskResource, ?> cc : configs){
            if(cc.getCell() instanceof DiskResourceNameCell){
                ((DiskResourceNameCell)cc.getCell()).setHasHandlers(this);
            } else if(cc.getCell() instanceof DiskResourceActionsCell){
                ((DiskResourceActionsCell)cc.getCell()).setHasHandlers(this);
            } else if(cc.getCell() instanceof DiskResourcePathCell) {
                ((DiskResourcePathCell)cc.getCell()).setHasHandlers(this);
            } else if(cc.getCell() instanceof DiskResourceFavoriteCell) {
                ((DiskResourceFavoriteCell)cc.getCell()).setHasHandlers(this);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static List<ColumnConfig<DiskResource, ?>> createColumnConfigList(@SuppressWarnings("rawtypes") final CheckBoxSelectionModel sm,
                                                                             final GridView.Appearance appearance,
                                                                             final DiskResourceUtil diskResourceUtil) {
        List<ColumnConfig<DiskResource, ?>> list = new ArrayList<>();

        DiskResourceProperties props = GWT.create(DiskResourceProperties.class);

        ColumnConfig<DiskResource, DiskResource> name = new ColumnConfig<>(new IdentityValueProvider<DiskResource>("name"),
                                                                           appearance.nameColumnWidth(),
                                                                           appearance.nameColumnLabel());
        ColumnConfig<DiskResource, Date> lastModified = new ColumnConfig<>(props.lastModified(),
                                                                           appearance.lastModifiedColumnWidth(),
                                                                           appearance.lastModifiedColumnLabel());
        ColumnConfig<DiskResource, Long> size = new ColumnConfig<>(new DiskResourceSizeValueProvider(),
                                                                   appearance.sizeColumnWidth(),
                                                                   appearance.sizeColumnLabel());
        ColumnConfig<DiskResource, DiskResource> path = new ColumnConfig<>(new IdentityValueProvider<DiskResource>("path"),
                                                                           appearance.pathColumnWidth(),
                                                                           appearance.pathColumnLabel());
        ColumnConfig<DiskResource, Date> created = new ColumnConfig<>(props.dateSubmitted(),
                                                                      appearance.createdDateColumnWidth(),
                                                                      appearance.createdDateColumnLabel());
        ColumnConfig<DiskResource, DiskResource> actions = new ColumnConfig<>(new IdentityValueProvider<DiskResource>("actions"),
                                                                              appearance.actionsColumnWidth(),
                                                                              appearance.actionsColumnLabel());
        lastModified.setFixed(true);
        size.setFixed(true);
        created.setFixed(true);
        actions.setFixed(true);

        name.setCell(new DiskResourceNameCell(diskResourceUtil));
        lastModified.setCell(new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM)));
        size.setCell(new DiskResourceSizeCell());
        created.setCell(new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM)));
        actions.setCell(new DiskResourceActionsCell(diskResourceUtil));
        path.setCell(new DiskResourcePathCell());

        name.setComparator(new DiskResourceNameComparator());
        name.setHideable(false);

        path.setHidden(true);
        created.setHidden(true);
        actions.setHidden(false);

        actions.setMenuDisabled(true);
        actions.setSortable(false);
        actions.setHideable(false);
        
        list.add(sm.getColumn());
        list.add(name);
        list.add(path);
        list.add(lastModified);
        list.add(created);

        list.add(size);
        list.add(actions);

        return list;
    }

    @Override
    public HandlerRegistration addDiskResourceNameSelectedEventHandler(DiskResourceNameSelectedEvent.DiskResourceNameSelectedEventHandler handler) {
        return ensureHandlers().addHandler(DiskResourceNameSelectedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addDiskResourcePathSelectedEventHandler(DiskResourcePathSelectedEvent.DiskResourcePathSelectedEventHandler handler) {
        return ensureHandlers().addHandler(DiskResourcePathSelectedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addManageMetadataSelectedEventHandler(ManageMetadataSelected.ManageMetadataSelectedEventHandler handler) {
        return ensureHandlers().addHandler(ManageMetadataSelected.TYPE, handler);
    }
    
    @Override
    public HandlerRegistration
            addCopyMetadataSelectedEventHandler(CopyMetadataSelected.CopyMetadataSelectedEventHandler handler) {

        return ensureHandlers().addHandler(CopyMetadataSelected.TYPE, handler);
    }

    @Override
    public HandlerRegistration addManageSharingSelectedEventHandler(ManageSharingSelected.ManageSharingSelectedEventHandler handler) {
        return ensureHandlers().addHandler(ManageSharingSelected.TYPE, handler);
    }

    @Override
    public HandlerRegistration addShareByDataLinkSelectedEventHandler(ShareByDataLinkSelected.ShareByDataLinkSelectedEventHandler handler) {
        return ensureHandlers().addHandler(ShareByDataLinkSelected.TYPE, handler);
    }
    
    @Override
    public HandlerRegistration addManageFavoritesEventHandler(RequestDiskResourceFavoriteEvent.RequestDiskResourceFavoriteEventHandler handler) {
        return ensureHandlers().addHandler(RequestDiskResourceFavoriteEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addManageCommentsSelectedEventHandler(ManageCommentsSelected.ManageCommentsSelectedEventHandler handler) {
        return ensureHandlers().addHandler(ManageCommentsSelected.TYPE, handler);
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
            } else if(cc.getCell() instanceof DiskResourceFavoriteCell){
                ((DiskResourceFavoriteCell)cc.getCell()).setBaseDebugId(baseID + DiskResourceModule.Ids.GRID);
            } else if(cc.getCell() instanceof DiskResourcePathCell) {
                ((DiskResourcePathCell)cc.getCell()).setBaseDebugId(baseID + DiskResourceModule.Ids.GRID);
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
    private static final class DiskResourceSizeValueProvider implements ValueProvider<DiskResource, Long> {
        @Override
        public Long getValue(DiskResource object) {
            if (object instanceof File) {
                return ((File) object).getSize();
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
     */
    private static final class DiskResourceSizeCell extends AbstractCell<Long> {
        final DiskResourceUtil diskResourceUtil = DiskResourceUtil.getInstance();

        @Override
        public void render(Context context, Long value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.appendEscaped(diskResourceUtil.formatFileSize(value.toString()));
            }
        }
    }
}
