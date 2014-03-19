package org.iplantc.de.apps.integration.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class AppEditorToolbarImpl implements AppEditorToolbar {

    @UiTemplate("AppIntegrationToolbar.ui.xml")
    interface AppIntegrationToolBarUiBinder extends UiBinder<Widget, AppEditorToolbarImpl> {}

    private static AppIntegrationToolBarUiBinder BINDER = GWT.create(AppIntegrationToolBarUiBinder.class);

    @UiField
    TextButton argumentOrderButton;

    @UiField
    MenuItem previewUiMenuItem, previewJsonMenuItem;

    @UiField
    TextButton saveButton;

    private AppEditorToolbar.Presenter presenter;

    private final Widget widget;

    public AppEditorToolbarImpl() {
        widget = BINDER.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setPresenter(AppEditorToolbar.Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("argumentOrderButton")
    void onArgumentOrderButtonClicked(@SuppressWarnings("unused") SelectEvent event) {
        presenter.onArgumentOrderClicked();
    }

    @UiHandler("previewJsonMenuItem")
    void onPreviewJsonClicked(@SuppressWarnings("unused") SelectionEvent<Item> event) {
        presenter.onPreviewJsonClicked();
    }

    @UiHandler("previewUiMenuItem")
    void onPreviewUiClicked(@SuppressWarnings("unused") SelectionEvent<Item> event) {
        presenter.onPreviewUiClicked();
    }

    @UiHandler("saveButton")
    void onSaveButtonClicked(@SuppressWarnings("unused") SelectEvent event) {
        presenter.onSaveClicked();
    }

}
