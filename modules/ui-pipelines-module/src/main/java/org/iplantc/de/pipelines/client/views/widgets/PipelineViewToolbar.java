package org.iplantc.de.pipelines.client.views.widgets;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * A Toolbar View for the PipelineView.
 * 
 * @author psarando
 * 
 */
public interface PipelineViewToolbar extends IsWidget {
    public interface Presenter {
        public void onPublishClicked();

        public void onSwapViewClicked();
    }

    void setPresenter(Presenter presenter);

    void setPublishButtonEnabled(boolean enabled);
}
