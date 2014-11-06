package org.iplantc.de.fileViewers.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by jstroot on 10/31/14.
 */
public class ViewerPagingToolbarUpdatedEvent extends GwtEvent<ViewerPagingToolbarUpdatedEvent.ViewerPagingToolbarUpdatedEventHandler> {
    public static interface ViewerPagingToolbarUpdatedEventHandler extends EventHandler {
        void onViewerPagingToolbarUpdated(ViewerPagingToolbarUpdatedEvent event);
    }

    public static Type<ViewerPagingToolbarUpdatedEventHandler> TYPE = new Type<>();
    private final Integer pageNumber;
    private final Integer pageSize;

    public ViewerPagingToolbarUpdatedEvent(Integer pageNumber,
                                           Integer pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public Type<ViewerPagingToolbarUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    protected void dispatch(ViewerPagingToolbarUpdatedEventHandler handler) {
        handler.onViewerPagingToolbarUpdated(this);
    }
}
