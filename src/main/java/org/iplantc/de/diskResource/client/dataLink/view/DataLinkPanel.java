package org.iplantc.de.diskResource.client.dataLink.view;

import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.List;

public class DataLinkPanel<M extends DiskResource> implements IsWidget {

    public interface Presenter<M> extends org.iplantc.de.commons.client.presenter.Presenter {

        void deleteDataLink(DataLink dataLink);

        void deleteDataLinks(List<DataLink> dataLinks);

        void createDataLinks(List<M> selectedItems);

        String getSelectedDataLinkDownloadPage();

        String getSelectedDataLinkDownloadUrl();

        void openSelectedDataLinkDownloadPage();
    }

    @UiTemplate("DataLinkPanel.ui.xml")
    interface DataLinkPanelUiBinder extends UiBinder<Widget, DataLinkPanel<?>> {
    }

    private static DataLinkPanelUiBinder uiBinder = GWT.create(DataLinkPanelUiBinder.class);

    @UiField
    TreeStore<M> store;

    @UiField
    Tree<M, M> tree;

    @UiField
    TextButton createDataLinksBtn;

    @UiField
    TextButton expandAll;

    @UiField
    TextButton collapseAll;

    @UiField
    TextButton copyDataLinkButton;

    @UiField
    TextButton advancedDataLinkButton;

    private final Widget widget;

    private Presenter<M> presenter;

    public DataLinkPanel(List<M> sharedResources) {
        widget = uiBinder.createAndBindUi(this);
        widget.setHeight("300");

        // Set the tree's node close/open icons to an empty image. Images for our tree will be controlled
        // from the cell.
        ImageResourcePrototype emptyImgResource = new ImageResourcePrototype("",
                UriUtils.fromString(""), 0, 0, 0, 0, false, false);
        tree.getStyle().setNodeCloseIcon(emptyImgResource);
        tree.getStyle().setNodeOpenIcon(emptyImgResource);

        tree.getSelectionModel().addSelectionHandler(
                new TreeSelectionHandler(createDataLinksBtn, copyDataLinkButton, advancedDataLinkButton,
                        tree));
        new QuickTip(widget);

    }

    public void setPresenter(Presenter<M> presenter) {
        this.presenter = presenter;
        tree.setCell(new DataLinkPanelCell<M>(this.presenter));
    }

    public void addRoots(List<M> roots) {
        store.add(roots);
    }

    @UiFactory
    ValueProvider<M, M> createValueProvider() {
        return new IdentityValueProvider<M>();
    }

    @UiFactory
    TreeStore<M> createTreeStore() {
        return new TreeStore<M>(new ModelKeyProvider<M>() {

            @Override
            public String getKey(M item) {
                return item.getId();
            }
        });
    }

    @UiHandler("createDataLinksBtn")
    void onCreateDataLinksSelected(SelectEvent event) {
        presenter.createDataLinks(tree.getSelectionModel().getSelectedItems());

    }

    @UiHandler("expandAll")
    void onExpandAllSelected(SelectEvent event) {
        tree.expandAll();
    }

    @UiHandler("collapseAll")
    void onCollapseAllSelected(SelectEvent event) {
        tree.collapseAll();
    }

    @UiHandler("copyDataLinkButton")
    void onCopyDataLinkButtonSelected(SelectEvent event) {
        // Open dialog window with text selected.
        IPlantDialog dlg = new IPlantDialog();
        dlg.setHeadingText(I18N.DISPLAY.copy());
        dlg.setHideOnButtonClick(true);
        dlg.setResizable(false);
        dlg.setSize("535", "130");
        TextField textBox = new TextField();
        textBox.setWidth(500);
        textBox.setReadOnly(true);
        textBox.setValue(presenter.getSelectedDataLinkDownloadUrl());
        VerticalLayoutContainer container = new VerticalLayoutContainer();
        dlg.setWidget(container);
        container.add(textBox);
        container.add(new Label(I18N.DISPLAY.copyPasteInstructions()));
        dlg.setFocusWidget(textBox);
        dlg.show();
        textBox.selectAll();
    }

    @UiHandler("advancedDataLinkButton")
    void onAdvancedDataLinkSelected(SelectEvent event) {
        presenter.openSelectedDataLinkDownloadPage();
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    public void mask() {
        tree.mask(I18N.DISPLAY.loadingMask());
    }

    public void unmask() {
        tree.unmask();
    }


    /**
     * A handler who controls this widgets button visibility based on tree check selection.
     *
     * @author jstroot
     *
     */
    private final class TreeSelectionHandler implements SelectionHandler<M> {

        private final HasEnabled createBtn;
        private final HasEnabled copyDataLinkButton;
        private final HasEnabled advancedDataLinkButton;
        private final Tree<M, M> tree;

        public TreeSelectionHandler(HasEnabled createBtn, HasEnabled copyDataLinkButton,
                HasEnabled advancedDataLinkButton, Tree<M, M> tree) {
            this.createBtn = createBtn;
            this.copyDataLinkButton = copyDataLinkButton;
            this.advancedDataLinkButton = advancedDataLinkButton;
            this.tree = tree;
        }

        @Override
        public void onSelection(SelectionEvent<M> event) {
            List<M> selectedItems = tree.getSelectionModel().getSelectedItems();
            boolean createBtnEnabled = selectedItems.size() > 0;
            boolean dataLinkSelected = selectedItems.size() == 1
                    && (selectedItems.get(0) instanceof DataLink);

            for (M item : selectedItems) {
                if (item instanceof DataLink) {
                    createBtnEnabled = false;
                    break;
                }
            }

            createBtn.setEnabled(createBtnEnabled);
            copyDataLinkButton.setEnabled(dataLinkSelected);
            advancedDataLinkButton.setEnabled(dataLinkSelected);
        }

    }

    public Tree<M, M> getTree() {
        return tree;
    }

}
