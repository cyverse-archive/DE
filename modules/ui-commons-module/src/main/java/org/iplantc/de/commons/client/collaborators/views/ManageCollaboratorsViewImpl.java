package org.iplantc.de.commons.client.collaborators.views;

import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.commons.client.collaborators.events.UserSearchResultSelected.USER_SEARCH_EVENT_TAG;
import org.iplantc.de.commons.client.collaborators.presenter.ManageCollaboratorsPresenter;
import org.iplantc.de.commons.client.collaborators.presenter.ManageCollaboratorsPresenter.MODE;
import org.iplantc.de.commons.client.collaborators.util.UserSearchField;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.List;

public class ManageCollaboratorsViewImpl extends Composite implements ManageCollaboratorsView {

    Presenter presenter;

    @UiField(provided = true)
    final ListStore<Collaborator> listStore;

    @UiField(provided = true)
    final ColumnModel<Collaborator> cm;

    @UiField
    Grid<Collaborator> grid;

    @UiField
    TextButton deleteBtn;

    @UiField
    TextButton manageBtn;


    @UiField
    FramedPanel collaboratorListPnl;

    @UiField
    HorizontalLayoutContainer searchPanel;

    @UiField
    BorderLayoutContainer con;

    @UiField(provided = true)
    UserSearchField searchField;

    @UiField
    ToolBar toolbar;

    private final Widget widget;

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private ManageCollaboratorsPresenter.MODE mode;

    @UiTemplate("ManageCollaboratorsView.ui.xml")
    interface MyUiBinder extends UiBinder<Widget, ManageCollaboratorsViewImpl> {
    }

    public ManageCollaboratorsViewImpl(CheckBoxSelectionModel<Collaborator> checkBoxModel,
            ColumnModel<Collaborator> cm, final ListStore<Collaborator> store, MODE mode) {
        this.cm = cm;
        this.listStore = store;
        searchField = new UserSearchField(USER_SEARCH_EVENT_TAG.MANAGE);
        widget = uiBinder.createAndBindUi(this);
        grid.setSelectionModel(checkBoxModel);
        grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
        grid.getView().setEmptyText(I18N.DISPLAY.noCollaborators());
        init();
        setMode(mode);
    }

    private void init() {
        collaboratorListPnl.setHeadingText(I18N.DISPLAY.myCollaborators());
        grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<Collaborator>() {

            @Override
            public void onSelectionChanged(SelectionChangedEvent<Collaborator> event) {
                if (event.getSelection() != null && event.getSelection().size() > 0) {
                    if (mode.equals(MODE.MANAGE)) {
                        deleteBtn.enable();
                    } else {
                        deleteBtn.disable();
                    }

                    if (mode.equals(MODE.SELECT)) {
                        manageBtn.setVisible(true);
                        deleteBtn.disable();
                    } else {
                        manageBtn.setVisible(false);
                    }

                } else {
                    deleteBtn.disable();
                }

            }
        });
    }

    @UiHandler("manageBtn")
    void manageCollaborators(SelectEvent event) {
        setMode(MODE.MANAGE);
    }

    @UiHandler("deleteBtn")
    void deleteCollaborator(SelectEvent event) {
        presenter.removeFromCollaborators(grid.getSelectionModel().getSelectedItems());
    }


    @Override
    public void setMode(ManageCollaboratorsPresenter.MODE mode) {
        this.mode = mode;
        switch (mode) {
            case MANAGE:
                grid.getView().setEmptyText(I18N.DISPLAY.noCollaborators());
                collaboratorListPnl.setHeadingText(I18N.DISPLAY.myCollaborators());
                manageBtn.setVisible(false);
                con.show(LayoutRegion.NORTH);
                break;
            case SELECT:
                grid.getView().setEmptyText(I18N.DISPLAY.noCollaborators());
                con.hide(LayoutRegion.NORTH);
                manageBtn.setVisible(true);
                collaboratorListPnl.setHeadingText(I18N.DISPLAY.selectCollabs());
                break;
        }
    }

    @Override
    public void setPresenter(Presenter p) {
        this.presenter = p;
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void loadData(List<Collaborator> models) {
        listStore.clear();
        listStore.addAll(models);
    }

    @Override
    public void mask(String maskText) {
        if (maskText == null || maskText.isEmpty()) {
            collaboratorListPnl.mask(I18N.DISPLAY.loadingMask());
        } else {
            collaboratorListPnl.mask(maskText);
        }
    }

    @Override
    public void unmask() {
        collaboratorListPnl.unmask();
    }

    @Override
    public void removeCollaborators(List<Collaborator> models) {
        if (models != null && !models.isEmpty()) {
            for (Collaborator c : models) {
                if (listStore.findModel(c) != null) {
                    listStore.remove(c);
                }
            }
        }
    }

    @Override
    public MODE getMode() {
        return mode;
    }

    @Override
    public List<Collaborator> getSelectedCollaborators() {
        return grid.getSelectionModel().getSelectedItems();
    }

    @Override
    public void addCollaborators(List<Collaborator> models) {
        listStore.addAll(models);
    }

}
