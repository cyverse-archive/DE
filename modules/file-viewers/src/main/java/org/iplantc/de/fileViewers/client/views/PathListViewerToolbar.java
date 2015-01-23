package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.fileViewers.client.events.DeleteSelectedPathsSelectedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * @author jstroot
 */
public class PathListViewerToolbar extends AbstractToolBar {
    public interface PathListViewerToolbarAppearance extends AbstractToolBarAppearance {

        String deleteSelectedPathsButtonTooltip();

        ImageResource deleteSelectedPathsButtonIcon();

        String addPathsButtonTooltip();

        ImageResource addPathsButtonIcon();
    }

    interface PathListViewerToolbarUiBinder extends UiBinder<ToolBar, PathListViewerToolbar>{}

    private static final PathListViewerToolbarUiBinder BINDER = GWT.create(PathListViewerToolbarUiBinder.class);

    @UiField(provided = true) PathListViewerToolbarAppearance appearance;
    @UiField TextButton deleteSelectedPathsBtn;

    public PathListViewerToolbar(boolean editing){
        this(editing, GWT.<PathListViewerToolbarAppearance> create(PathListViewerToolbarAppearance.class));
        initWidget(BINDER.createAndBindUi(this));
        setEditing(editing);
    }

    PathListViewerToolbar(boolean editing,
                                 PathListViewerToolbarAppearance appearance) {
        super(editing, appearance);
        this.appearance = appearance;
    }

    public HandlerRegistration addDeleteSelectedPathsSelectedEventHandler(DeleteSelectedPathsSelectedEvent.DeleteSelectedPathsSelectedEventHandler handler){
        return addHandler(handler, DeleteSelectedPathsSelectedEvent.TYPE);
    }

    @UiHandler("deleteSelectedPathsBtn") void onDeleteSelectedPathsBtnSelected(SelectEvent event){
        fireEvent(new DeleteSelectedPathsSelectedEvent());
    }


}
