package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.models.diskResources.File;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;

/**
 * Created by jstroot on 10/30/14.
 */
public abstract class AbstractStructuredTextViewer extends AbstractFileViewer {
    private static class StructuredTextModelKeyProvider implements ModelKeyProvider<Splittable> {
        private int index;
        @Override
        public String getKey(Splittable item) {
            return String.valueOf(index++);
        }
    }

    public interface StructuredTextViewerAppearance {

        String saveBtnText();

        ImageResource saveBtnIcon();
    }
    interface AbstractStructuredTextViewerUiBinder extends UiBinder<Widget, AbstractStructuredTextViewer> {
    }

    @UiField
    AbstractToolBar toolBar;
    @UiField
    GridView gridView;
    @UiField
    ColumnModel columnModel;
    @UiField
    ListStore<Splittable> listStore;
    @UiField(provided = true)
    StructuredTextViewerAppearance appearance;
    @UiField
    ViewerPagingToolBar pagingToolBar;
    @UiField
    Grid<Splittable> grid;

    public AbstractStructuredTextViewer(final File file,
                                        final String infoType) {
        super(file, infoType);
    }

    @UiFactory ListStore<Splittable> createListStore(){
        return new ListStore<>(new StructuredTextModelKeyProvider());
    }

    @UiFactory ViewerPagingToolBar createPagingToolBar(){
        return new ViewerPagingToolBar(getFileSize());
    }
}