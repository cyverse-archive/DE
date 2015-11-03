package org.iplantc.de.commons.client.comments.view;

import org.iplantc.de.client.models.comments.Comment;
import org.iplantc.de.client.models.comments.CommentsAutoBeanFactory;
import org.iplantc.de.commons.client.comments.CommentsView;
import org.iplantc.de.commons.client.comments.view.cells.CommentsCell;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jstroot
 */
public class CommentsViewImpl extends Composite implements CommentsView {

    @UiTemplate("CommentsView.ui.xml")
    interface CommentsViewUiBinder extends UiBinder<Widget, CommentsViewImpl> {
    }

    @UiField TextButton addBtn;
    @UiField(provided = true) ColumnModel<Comment> cm;
    @UiField TextArea commentBox;
    @UiField VerticalLayoutContainer container;
    @UiField TextButton deleteBtn;
    @UiField Grid<Comment> grid;
    @UiField(provided = true) ListStore<Comment> store;
    @UiField GridView<Comment> view;
    @UiField(provided = true) CommentsViewAppearance appearance;

    @Inject CommentsAutoBeanFactory factory;

    private static CommentsViewUiBinder uiBinder = GWT.create(CommentsViewUiBinder.class);
    private CommentsComparator commentComparator;
    private Presenter presenter;

    @Inject
    CommentsViewImpl(final CommentsViewAppearance appearance) {
        this.appearance = appearance;
        commentComparator = new CommentsComparator();
        buildColumnModel();
        buildStore();
        uiBinder.createAndBindUi(this);
        grid.getView().setAutoExpandColumn(cm.getColumn(0));
        grid.getView().setAutoFill(true);
        grid.getView().setForceFit(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<Comment>() {

            @Override
            public void onSelectionChanged(SelectionChangedEvent<Comment> event) {
                if (event.getSelection().size() > 0) {
                    deleteBtn.enable();
                    presenter.onSelect(event.getSelection().get(0));
                } else {
                    deleteBtn.disable();
                }


            }
        });
        commentBox.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (commentBox.getCurrentValue() == null || commentBox.getCurrentValue().length() < 1) {
                    addBtn.disable();
                } else {
                    addBtn.enable();
                }
                if (event.isShiftKeyDown() && event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                    Comment c = buildComment();
                    presenter.onAdd(c);
                }
            }
        });
    }

    @Override
    public void addComment(Comment c) {
        store.add(c);
        store.applySort(false);
        grid.getSelectionModel().select(c, false);
        grid.getView().ensureVisible(store.indexOf(c), 0, false);
        commentBox.clear();
    }

    @Override
    public void disableDelete() {
        deleteBtn.disable();

    }

    @Override
    public void enableDelete() {
        deleteBtn.enable();

    }

    @Override
    public Widget getWidget() {
        return container;
    }

    @Override
    public void loadComments(List<Comment> comments) {
        store.clear();
        store.addAll(comments);
        store.applySort(false);
    }

    @Override
    public void retractComment(Comment c) {
        store.update(c);
        grid.getView().refresh(false);
    }

    @Override
    public void setPresenter(Presenter p) {
        this.presenter = p;
    }

    @UiHandler("addBtn")
    void addHandler(SelectEvent event) {
        Comment c = buildComment();
        presenter.onAdd(c);
    }

    @UiHandler("deleteBtn")
    void deleteBtn(SelectEvent event) {
        Comment selectedItem = grid.getSelectionModel().getSelectedItem();
        presenter.onDelete(selectedItem);
    }

    private void buildColumnModel() {
        ColumnConfig<Comment, Comment> comcol = new ColumnConfig<>(new IdentityValueProvider<Comment>(Comment.COMMENT_TEXT_KEY),
                                                                   appearance.commentColumnWidth(),
                                                                   appearance.commentColumnHeader());
        comcol.setCell(new CommentsCell());
        comcol.setComparator(commentComparator);
        ArrayList<ColumnConfig<Comment, ?>> list = new ArrayList<>();
        list.add(comcol);
        cm = new ColumnModel<>(list);
    }

    private Comment buildComment() {
        Comment c = AutoBeanCodex.decode(factory, Comment.class, "{}").as();
        c.setCommentText(commentBox.getCurrentValue());
        return c;
    }

    private void buildStore() {
        store = new ListStore<>(new ModelKeyProvider<Comment>() {

            @Override
            public String getKey(Comment item) {
                return item.getId();
            }
        });
        store.addSortInfo(new StoreSortInfo<>(commentComparator, SortDir.DESC));

    }

}
