package org.iplantc.de.commons.client.comments.view;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.comments.Comment;
import org.iplantc.de.client.models.comments.CommentsAutoBeanFactory;
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
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.client.IdentityValueProvider;
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
import java.util.Date;
import java.util.List;

public class CommentsViewImpl extends Composite implements CommentsView {

    private static CommentsViewUiBinder uiBinder = GWT.create(CommentsViewUiBinder.class);

    @UiTemplate("CommentsView.ui.xml")
    interface CommentsViewUiBinder extends UiBinder<Widget, CommentsViewImpl> {
    }

    public CommentsViewImpl() {
        commentComparator = new CommentsComparator();
        buildColumnModel();
        buildStore();
        uiBinder.createAndBindUi(this);
        grid.getView().setAutoExpandColumn(cm.getColumn(0));
        grid.getView().setAutoFill(true);
        grid.getView().setForceFit(true);
        grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<Comment>() {

            @Override
            public void onSelectionChanged(SelectionChangedEvent<Comment> event) {
                if (event.getSelection().size() > 0) {
                    deleteBtn.enable();
                } else {
                    deleteBtn.disable();
                }
                
            }
        });
        commentBox.addKeyPressHandler(new KeyPressHandler() {
            
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (commentBox.getCurrentValue() == null) {
                    addBtn.disable();
                } else {
                    addBtn.enable();
                }
                if (event.isShiftKeyDown() && event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                    Comment c = buildComment();
                    addComment(c);
                    return;
                }
                

            }
        });
    }

    private void buildStore() {
        store = new ListStore<Comment>(new ModelKeyProvider<Comment>() {

            @Override
            public String getKey(Comment item) {
                return item.getId();
            }
        });
        store.addSortInfo(new StoreSortInfo<Comment>(commentComparator, SortDir.DESC));

    }

    private void buildColumnModel() {
        ColumnConfig<Comment, Comment> comcol = new ColumnConfig<Comment, Comment>(new IdentityValueProvider<Comment>("commenttext"), 350, "Comments");
        comcol.setCell(new CommentsCell());
        comcol.setComparator(commentComparator);
        ArrayList<ColumnConfig<Comment,?>> list = new ArrayList<ColumnConfig<Comment,?>>();
        list.add(comcol);
        cm = new ColumnModel<Comment>(list);
    }

    @UiField(provided = true)
    ColumnModel<Comment> cm;

    @UiField(provided = true)
    ListStore<Comment> store;

    @UiField
    GridView<Comment> view;

    @UiField
    Grid<Comment> grid;

    @UiField
    TextButton deleteBtn;

    @UiField
    TextArea commentBox;

    @UiField
    TextButton addBtn;

    @UiField
    VerticalLayoutContainer container;

    private Presenter presenter;

    private final CommentsAutoBeanFactory factory = GWT.create(CommentsAutoBeanFactory.class);;

    private final UserInfo userInfo = UserInfo.getInstance();

    private CommentsComparator commentComparator;;

    @UiHandler("addBtn")
    void addHandler(SelectEvent event) {
        Comment c = buildComment();
        addComment(c);
    }

    private void addComment(Comment c) {
        store.add(0, c);
        presenter.onAdd(c);
    }

    private Comment buildComment() {
        Comment c = AutoBeanCodex.decode(factory, Comment.class, "{}").as();
        long time = new Date().getTime();
        c.setId(time + "");
        c.setCommentText(commentBox.getCurrentValue());
        c.setCommentedBy(userInfo.getUsername());
        c.setTimestamp(time);
        return c;
    }

    @UiHandler("deleteBtn")
    void deleteBtn(SelectEvent event) {
        Comment selectedItem = grid.getSelectionModel().getSelectedItem();
        store.remove(selectedItem);
        presenter.onDelete(selectedItem);
    }

    @Override
    public void loadComments(List<Comment> comments) {
        store.clear();
        store.addAll(comments);
    }

    @Override
    public void setPresenter(Presenter p) {
        this.presenter = p;

    }

    @Override
    public Widget getWidget() {
        return container;
    }

}
