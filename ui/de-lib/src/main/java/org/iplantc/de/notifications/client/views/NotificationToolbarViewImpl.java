/**
 * 
 */
package org.iplantc.de.notifications.client.views;

import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.notifications.client.events.NotificationToolbarDeleteAllClickedEvent;
import org.iplantc.de.notifications.client.events.NotificationToolbarDeleteClickedEvent;
import org.iplantc.de.notifications.client.events.NotificationToolbarSelectionEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * @author sriram
 * 
 */
public class NotificationToolbarViewImpl extends Composite implements NotificationToolbarView {

    private static NotificationToolbarUiBinder uiBinder = GWT.create(NotificationToolbarUiBinder.class);

    @UiTemplate("NotificationToolbarView.ui.xml")
    interface NotificationToolbarUiBinder extends UiBinder<Widget, NotificationToolbarViewImpl> {
    }

    @UiField
    TextButton btnDelete;

    @UiField
    TextButton btnDeleteAll;

    @UiField
    ToolBar menuToolBar;

    @UiField(provided = true)
    SimpleComboBox<NotificationCategory> cboFilter = new SimpleComboBox<NotificationCategory>(
            new StringLabelProvider<NotificationCategory>());
    private NotificationView.NotificationViewAppearance appearance;

    @Inject
    public NotificationToolbarViewImpl(NotificationView.NotificationViewAppearance appearance) {
        this.appearance = appearance;
        initWidget(uiBinder.createAndBindUi(this));

        initFilters();
    }

    @Override
    public HandlerRegistration addNotificationToolbarDeleteAllClickedEventHandler(
            NotificationToolbarDeleteAllClickedEvent.NotificationToolbarDeleteAllClickedEventHandler handler) {
        return addHandler(handler, NotificationToolbarDeleteAllClickedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addNotificationToolbarDeleteClickedEventHandler(
            NotificationToolbarDeleteClickedEvent.NotificationToolbarDeleteClickedEventHandler handler) {
        return addHandler(handler, NotificationToolbarDeleteClickedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addNotificationToolbarSelectionEventHandler(
            NotificationToolbarSelectionEvent.NotificationToolbarSelectionEventHandler handler) {
        return addHandler(handler, NotificationToolbarSelectionEvent.TYPE);
    }

    private void initFilters() {
        cboFilter.add(NotificationCategory.NEW);
        cboFilter.add(NotificationCategory.ALL);
        cboFilter.add(NotificationCategory.ANALYSIS);
        cboFilter.add(NotificationCategory.DATA);
        cboFilter.add(NotificationCategory.TOOLREQUEST);
        cboFilter.add(NotificationCategory.APPS);
        cboFilter.add(NotificationCategory.PERMANENTIDREQUEST);
        cboFilter.setValue(NotificationCategory.ALL);

        cboFilter.addSelectionHandler(new SelectionHandler<NotificationCategory>() {
            @Override
            public void onSelection(SelectionEvent<NotificationCategory> event) {
                fireEvent(new NotificationToolbarSelectionEvent(event.getSelectedItem()));
            }
        });
        cboFilter.setEditable(false);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void setDeleteButtonEnabled(boolean enabled) {
        btnDelete.setEnabled(enabled);

    }

    @Override
    public void setDeleteAllButtonEnabled(boolean enabled) {
        btnDeleteAll.setEnabled(enabled);
    }

    @UiHandler("btnDelete")
    public void deleteClicked(SelectEvent event) {
        fireEvent(new NotificationToolbarDeleteClickedEvent());
    }

    @UiHandler("btnDeleteAll")
    public void deleteAllClicked(SelectEvent event) {
        fireEvent(new NotificationToolbarDeleteAllClickedEvent());
    }

    @Override
    public void setRefreshButton(TextButton refreshBtn) {
        refreshBtn.setText(appearance.refresh());
        menuToolBar.insert(refreshBtn, 1);
    }

    @Override
    public void setCurrentCategory(NotificationCategory category) {
        cboFilter.setValue(category);
    }
}
