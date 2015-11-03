package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.ViewerPagingToolBar;

import com.google.gwt.core.client.GWT;

public class ViewerPagingToolBarDefaultAppearance implements ViewerPagingToolBar.ViewerPagingToolBarAppearance {

    final FileViewerStrings fileViewerStrings;

    public ViewerPagingToolBarDefaultAppearance() {
        this(GWT.<FileViewerStrings>create(FileViewerStrings.class));

    }

    ViewerPagingToolBarDefaultAppearance(final FileViewerStrings fileViewerStrings){
        this.fileViewerStrings = fileViewerStrings;
    }

    @Override
    public String afterTextLabel(int totalPages) {
        return fileViewerStrings.ofTotalPages(totalPages);
    }

    @Override
    public String invalidPage() {
        return fileViewerStrings.invalidPage();
    }

    @Override
    public String pageNumberFieldWidth() {
        return "30px";
    }

    @Override
    public int getMaxPageSizeKb() {
        return 1024;
    }

    @Override
    public int getMinPageSizeKb() {
        return 8;
    }

    @Override
    public int getPageIncrementSizeKb() {
        return 8;
    }

    @Override
    public int sliderWidth() {
        return 100;
    }

    @Override
    public String pageSizeLabel() {
        return fileViewerStrings.pageSize();
    }
}
