package org.iplantc.de.pipelines.client.views.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * The main PipelineViewToolbar implementation.
 * 
 * @author psarando
 * 
 */
public class PipelineViewToolbarImpl implements PipelineViewToolbar {

    private static PipelineViewToolbarUiBinder uiBinder = GWT.create(PipelineViewToolbarUiBinder.class);

    @UiTemplate("PipelineViewToolbar.ui.xml")
    interface PipelineViewToolbarUiBinder extends UiBinder<Widget, PipelineViewToolbarImpl> {
    }

    private final Widget widget;
    private Presenter presenter;

    public PipelineViewToolbarImpl() {
        widget = uiBinder.createAndBindUi(this);
    }

    @UiField
    TextButton publishBtn;

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("publishBtn")
    public void onPublishClick(SelectEvent e) {
        presenter.onPublishClicked();
    }

    @UiHandler("swapViewBtn")
    public void onSwapViewClick(SelectEvent e) {
        presenter.onSwapViewClicked();
    }

    @Override
    public void setPublishButtonEnabled(boolean enabled) {
        publishBtn.setEnabled(enabled);
    }
}
